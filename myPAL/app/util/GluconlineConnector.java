package util;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import play.Logger;

public class GluconlineConnector {
    private static final String UTF8_CHARSET = "UTF-8";
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    private static final String REQUEST_URI = "/api/";
    private static final String REQUEST_METHOD = "GET";

    private String endpoint = "gluconline.menarini.acceptatie.netbasics.nl"; // must be lowercase
    private String accessKeyId = "JO1Z4LXT9EMGDBGVCEO8";
    private String secretKey = "qSFBgYCJ91P81f3P0UhhGojtX0wdtvtU1HziWCjG";

    private SecretKeySpec secretKeySpec = null;
    private Mac mac = null;

    public GluconlineConnector() {
        byte[] secretyKeyBytes = new byte[0];
        try {
            secretyKeyBytes = secretKey.getBytes(UTF8_CHARSET);
            secretKeySpec = new SecretKeySpec(secretyKeyBytes, HMAC_SHA256_ALGORITHM);
            mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(secretKeySpec);
        } catch (UnsupportedEncodingException e) {
            Logger.debug("[GluconlineConnector > constructor] UnsupportedEncodingException: " + e.getStackTrace());
        } catch (NoSuchAlgorithmException e) {
            Logger.debug("[GluconlineConnector > constructor] NoSuchAlgorithmException: " + e.getStackTrace());
        } catch (InvalidKeyException e) {
            Logger.debug("[GluconlineConnector > constructor] InvalidKeyException: " + e.getStackTrace());
        }

    }

    public String sign(Map<String, String> params) {
        params.put("AccessKeyId", accessKeyId);
        params.put("Action", "GetPatientDiary");
        params.put("SignatureMethod", HMAC_SHA256_ALGORITHM);
        params.put("SignatureVersion", "2");
        params.put("Timestamp", timestamp());
        params.put("Version", "2015-09-09");

        SortedMap<String, String> sortedParamMap =
                new TreeMap<String, String>(params);
        String canonicalQS = canonicalize(sortedParamMap);
        String toSign =
                REQUEST_METHOD + "\n"
                        + endpoint + "\n"
                        + REQUEST_URI + "\n"
                        + canonicalQS;

        String hmac = hmac(toSign);
        String sig = percentEncodeRfc3986(hmac);
        String url = "http://" + endpoint + REQUEST_URI + "?" +
                canonicalQS + "&Signature=" + sig;

        return url;
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
        String canonical = buffer.toString();
        return canonical;
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