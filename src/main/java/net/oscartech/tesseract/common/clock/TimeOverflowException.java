package net.oscartech.tesseract.common.clock;

/**
 * Created by tylaar on 15/4/26.
 */
public class TimeOverflowException extends RuntimeException {
    public TimeOverflowException(final String message) {
        super(message);
    }
}
