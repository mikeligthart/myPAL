package util;

/**
 * myPAL
 * Purpose: exception thrown when there is no valid gluconline ID
 * <p>
 * Developed for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 9-10-2015
 */
public class NoValidGluconlineIDException extends Exception {

    private static final String BASEMESSAGE = "No valid GluconlineID was delivered.";

    public NoValidGluconlineIDException() {
        super(BASEMESSAGE);
    }

    public NoValidGluconlineIDException(String message) {
        super(BASEMESSAGE + " " + message);
    }

    public NoValidGluconlineIDException(String message, Throwable cause) {
        super(BASEMESSAGE + " " + message, cause);
    }

    public NoValidGluconlineIDException(Throwable cause) {
        super(BASEMESSAGE, cause);
    }
}
