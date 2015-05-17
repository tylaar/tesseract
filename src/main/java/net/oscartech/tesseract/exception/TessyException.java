package net.oscartech.tesseract.exception;

/**
 * Created by tylaar on 15/5/17.
 */
public class TessyException extends RuntimeException {

    public TessyException() {
    }

    public TessyException(final String message) {
        super(message);
    }

    public TessyException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TessyException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public TessyException(final Throwable cause) {
        super(cause);
    }
}
