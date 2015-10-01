package util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import play.Logger;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

/**
 * Created by ligthartmeu on 1-10-2015.
 */
public class GluconlineConnector {

    public static final String KEY = "qSFBgYCJ91P81f3P0UhhGojtX0wdtvtU1HziWCjG";
    public static final String MESSAGE = "GET \n gluconline.menarini.acceptatie.netbasics.nl/api/AccessKeyId=JO1Z4LXT9EMGDBGVCEO8&Action=GetPatientDiary&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2015-10-01T12%3A06%3A39.457Z&Version=2015-09-09&bsn=067835880&searchPeriodStep=-78";
    public static final String MESSAGE2 = "GET & \n & gluconline.menarini.acceptatie.netbasics.nl/api/ & \n & AccessKeyId=JO1Z4LXT9EMGDBGVCEO8[&Action=GetPatientDiary[&SignatureMethod=HmacSHA256[&SignatureVersion=2[&Timestamp=2015-10-01T12%3A06%3A39.457Z[&Version=2015-09-09[&bsn=067835880[&searchPeriodStep=-78]]]]]]]";

    public static String testKey = "key";
    public static String testMessages = "The quick brown fox jumps over the lazy dog";


    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    /**
     * Computes RFC 2104-compliant HMAC signature.
     * * @param data
     * The signed data.
     * @param key
     * The signing key.
     * @return
     * The Base64-encoded RFC 2104-compliant HMAC signature.
     * @throws
     * java.security.SignatureException when signature generation fails
     */
    public static String calculateSignature(String data, String key)
    {
        String result = null;
        try {

            // Get an hmac_sha256 key from the raw key bytes.
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("UTF8"), HMAC_SHA256_ALGORITHM);

            // Get an hmac_sha256 Mac instance and initialize with the signing key.
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);

            // Compute the hmac on input data bytes.
            byte[] rawHmac = mac.doFinal(data.getBytes("UTF8"));

            // Base64-encode the hmac by using the utility in the SDK
            result = Base64.encodeBase64String(rawHmac);

        } catch (NoSuchAlgorithmException e) {
            Logger.debug("NoSuchAlgorithmException: " + e.getLocalizedMessage());
        } catch (InvalidKeyException e) {
            Logger.debug("InvalidKeyException: " + e.getLocalizedMessage());
        } catch (UnsupportedEncodingException e) {
            Logger.debug("UnsupportedEncodingException: " + e.getLocalizedMessage());
        }
        return result;
    }

}
