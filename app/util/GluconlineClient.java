package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.databind.JsonNode;
import models.UserMyPAL;
import models.diary.measurement.DayPart;
import models.diary.measurement.Glucose;
import models.diary.measurement.Insulin;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.math.NumberUtils;
import play.Logger;
import play.i18n.Messages;
import play.libs.Json;

public class GluconlineClient {
    private static final String UTF8_CHARSET = "UTF-8";
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    private static final String REQUEST_METHOD = "GET";

    private static final String ENDPOINT = "gluconline.menarini.acceptatie.netbasics.nl";
    private static final String REQUEST_URI = "/api/";
    private static final String ACCESSKEYID = "JO1Z4LXT9EMGDBGVCEO8";
    private static final String SECRETKEY = "qSFBgYCJ91P81f3P0UhhGojtX0wdtvtU1HziWCjG";

    private Mac mac = null;
    private UserMyPAL user;

    public static boolean validateGluconlineID(String id){
        return !(id.isEmpty() || id.length() != 9 || !NumberUtils.isDigits(id));
    }

    public GluconlineClient(UserMyPAL user) throws NoValidGluconlineIDException {
        this.user = user;
        if(!validateGluconlineID(user.getGluconlineID())){
            throw new NoValidGluconlineIDException();
        }

        //Initialize Mac instance
        SecretKeySpec secretKeySpec;
        byte[] secretyKeyBytes;
        try {
            secretyKeyBytes = SECRETKEY.getBytes(UTF8_CHARSET);
            secretKeySpec = new SecretKeySpec(secretyKeyBytes, HMAC_SHA256_ALGORITHM);
            mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(secretKeySpec);
        } catch (UnsupportedEncodingException e) {
            Logger.error("[GluconlineClient > constructor] UnsupportedEncodingException: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Logger.error("[GluconlineClient > constructor] NoSuchAlgorithmException: " + e.getMessage());
        } catch (InvalidKeyException e) {
            Logger.error("[GluconlineClient > constructor] InvalidKeyException: " + e.getMessage());
        }
    }

    public JsonNode retrieve() {
        //Build URL to connect with Gluconline Server
        Map<String, String> params = new HashMap<>();
        params.put("AccessKeyId", ACCESSKEYID);
        params.put("Action", "GetPatientDiary");
        params.put("SignatureMethod", HMAC_SHA256_ALGORITHM);
        params.put("SignatureVersion", "2");
        params.put("Timestamp", timestamp());
        params.put("Version", "2015-09-09");
        params.put("bsn", user.getGluconlineID());
        params.put("searchPeriodStep", "-78");

        SortedMap<String, String> sortedParamMap =
                new TreeMap<>(params);
        String canonicalQS = canonicalize(sortedParamMap);
        String toSign =
                REQUEST_METHOD + "\n"
                        + ENDPOINT + "\n"
                        + REQUEST_URI + "\n"
                        + canonicalQS;

        String hmac = hmac(toSign);
        String sig = percentEncodeRfc3986(hmac);
        String urlToConnect = "http://" + ENDPOINT + REQUEST_URI + "?" +
                canonicalQS + "&Signature=" + sig;

        //Connect to server and fetch result
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlToConnect);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(REQUEST_METHOD);
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        } catch (MalformedURLException e) {
            Logger.error("[GluconlineClient > retrieve] MalformedURLException: " + e.getMessage());
        } catch (IOException e) {
            Logger.error("[GluconlineClient > retrieve] IOException: " + e.getMessage());
            return null;
        }

        if(result.length() == 0){
            return null;
        }
        return Json.parse(result.toString());
    }

    public void updateMeasurements(JsonNode rootNode){
        if (rootNode != null){
            if(!rootNode.isNull()) {
                JsonNode resultNode = rootNode.get("GetPatientDiaryResponse").get("Result");
                JsonNode weekNode = resultNode.get("WEEK");
                for(int index = 0; index < weekNode.size(); index++){
                    String dayKey = weekNode.get(index).asText();
                    JsonNode dayNode = resultNode.get(dayKey);
                    extractInsulin(dayNode.get("insulin"), dayKey);
                    extractGlucose(dayNode.get("measurements"), dayKey);
                }
            }
        }
    }

    private void extractInsulin(JsonNode insulinRootNode, String dayKey){
        Date date = null;
        try {
            date =  new SimpleDateFormat("dd-MM-yyyy").parse(dayKey);
        } catch (ParseException e) {
            Logger.error("[GluconlineClient > updateMeasurements] ParseException: " + e.getMessage());
        }

        for(Iterator<String> dayParts = insulinRootNode.fieldNames(); dayParts.hasNext();){
            String dayPart = dayParts.next();
            DayPart dayPartEnum = null;
            try {
                dayPartEnum = stringToDayPart(dayPart);
            } catch (AppException e) {
                Logger.error("[GluconlineClient > updateMeasurements] AppException: " + e.getMessage());
            }
            JsonNode dayPartNode = insulinRootNode.get(dayPart).get("tooltip");
            for(Iterator<String> insulinValues = dayPartNode.fieldNames(); insulinValues.hasNext();){
                JsonNode insulinValueNode = dayPartNode.get(insulinValues.next());
                Insulin insulin = new Insulin();
                try {
                    insulin.setDate(date);
                    Long timestamp = new SimpleDateFormat("HH:mm").parse(insulinValueNode.get("time").asText()).getTime();
                    insulin.setStarttime(new Time(timestamp));
                    insulin.setEndtime(new Time(timestamp + 300000));
                } catch (AppException e) {
                    Logger.error("[GluconlineClient > updateMeasurements] AppException: " + e.getMessage());
                } catch (ParseException e) {
                    Logger.error("[GluconlineClient > updateMeasurements] ParseException: " + e.getMessage());
                }
                insulin.setUser(user);
                insulin.setValue(Double.parseDouble(insulinValueNode.get("unit").asText()));
                insulin.setDaypart(dayPartEnum);
                insulin.setComment(insulinValueNode.get("comment").asText());
                if(!Insulin.exists(insulin)){
                    insulin.save();
                }

            }
        }
    }

    private void extractGlucose(JsonNode glucoseRootNode, String dayKey){
        Date date = null;
        try {
            date =  new SimpleDateFormat("dd-MM-yyyy").parse(dayKey);
        } catch (ParseException e) {
            Logger.error("[GluconlineClient > updateMeasurements] ParseException: " + e.getMessage());
        }

        for(Iterator<String> measurements = glucoseRootNode.fieldNames(); measurements.hasNext();){
            JsonNode measurement = glucoseRootNode.get(measurements.next());
            String dayPartString = measurement.fieldNames().next();
            JsonNode glucoseNode = measurement.get(dayPartString);
            Glucose glucose = new Glucose();
            try {
                glucose.setDate(date);
                Long timestamp = new SimpleDateFormat("HH:mm").parse(glucoseNode.get("time").asText()).getTime();
                glucose.setStarttime(new Time(timestamp));
                glucose.setEndtime(new Time(timestamp + 300000));
                glucose.setUser(user);
                glucose.setValue(Double.parseDouble(glucoseNode.get("value").asText()));
                glucose.setDaypart(stringToDayPart(dayPartString));
                glucose.setComment(glucoseNode.get("comment").asText());
                if(!Glucose.exists(glucose)) {
                    glucose.save();
                }
            } catch (AppException e) {
                Logger.error("[GluconlineClient > updateMeasurements] AppException: " + e.getMessage());
            } catch (ParseException e) {
                Logger.error("[GluconlineClient > updateMeasurements] ParseException: " + e.getMessage());
            }
        }

    }

    private DayPart stringToDayPart(String dayPartString) throws AppException{
        if(dayPartString.equalsIgnoreCase(Messages.get("gluconline.daypart.sober"))){
            return DayPart.SOBER;
        } else if (dayPartString.equalsIgnoreCase(Messages.get("gluconline.daypart.afterbreakfast"))){
            return DayPart.AFTERBREAKFAST;
        } else if (dayPartString.equalsIgnoreCase(Messages.get("gluconline.daypart.beforelunch"))){
            return DayPart.BEFORELUNCH;
        }else if (dayPartString.equalsIgnoreCase(Messages.get("gluconline.daypart.afterlunch"))){
            return DayPart.AFTERLUNCH;
        } else if (dayPartString.equalsIgnoreCase(Messages.get("gluconline.daypart.beforedinner"))){
            return DayPart.BEFOREDINNER;
        } else if (dayPartString.equalsIgnoreCase(Messages.get("gluconline.daypart.afterdinner"))){
            return DayPart.AFTERDINNER;
        } else {
            throw new AppException("dayPartString does not match any of the known dayparts");
        }
    }

    private String hmac(String stringToSign) {
        String signature;
        byte[] data;
        byte[] rawHmac;
        try {
            data = stringToSign.getBytes(UTF8_CHARSET);
            rawHmac = mac.doFinal(data);
            Base64 encoder = new Base64();
            signature = new String(encoder.encode(rawHmac));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(UTF8_CHARSET + " is unsupported!", e);
        }
        return signature;
    }

    private String timestamp() {
        String timestamp;
        Calendar cal = Calendar.getInstance();
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
        timestamp = dfm.format(cal.getTime());
        return timestamp;
    }

    private String canonicalize(SortedMap<String, String> sortedParamMap)
    {
        if (sortedParamMap.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iter =
                sortedParamMap.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<String, String> kvpair = iter.next();
            builder.append(percentEncodeRfc3986(kvpair.getKey()));
            builder.append("=");
            builder.append(percentEncodeRfc3986(kvpair.getValue()));
            if (iter.hasNext()) {
                builder.append("&");
            }
        }
        return builder.toString();
    }

    private String percentEncodeRfc3986(String s) {
        String out;
        try {
            out = URLEncoder.encode(s, UTF8_CHARSET)
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            out = s;
        }
        return out;
    }
}