package util;

/**
 * myPAL
 * Purpose: exception thrown when the decision model cannot be correctly parsed
 * <p>
 * Developed for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 3-11-2015
 */
public class ParseAvatarDecisionModelException extends Exception {

    public ParseAvatarDecisionModelException() {
    }

    public ParseAvatarDecisionModelException(String message) {
        super(message);
    }

    public ParseAvatarDecisionModelException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseAvatarDecisionModelException(Throwable cause) {
        super(cause);
    }
}
