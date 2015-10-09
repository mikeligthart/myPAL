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
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.databind.JsonNode;
import jdk.nashorn.internal.parser.JSONParser;
import models.UserMyPAL;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.math.NumberUtils;
import play.Logger;
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
                new TreeMap<String, String>(params);
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
        URL url = null;
        try {
            url = new URL(urlToConnect);
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
        }

        return Json.parse(result.toString());
    }

    public void updateMeasurements(JsonNode node){
        //TODO: extract measurements from JsonNode and add them to the database
    }

    public static boolean validateGluconlineID(String id){
        return !(id.isEmpty() || id.length() != 9 || NumberUtils.isDigits(id));
    }

    private String hmac(String stringToSign) {
        String signature = null;
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
        String timestamp = null;
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

        StringBuffer buffer = new StringBuffer();
        Iterator<Map.Entry<String, String>> iter =
                sortedParamMap.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<String, String> kvpair = iter.next();
            buffer.append(percentEncodeRfc3986(kvpair.getKey()));
            buffer.append("=");
            buffer.append(percentEncodeRfc3986(kvpair.getValue()));
            if (iter.hasNext()) {
                buffer.append("&");
            }
        }
        return buffer.toString();
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