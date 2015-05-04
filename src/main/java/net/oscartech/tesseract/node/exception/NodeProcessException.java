package net.oscartech.tesseract.node.exception;

/**
 * Created by tylaar on 15/4/29.
 */
public class NodeProcessException extends RuntimeException {
    public NodeProcessException() {
    }

    public NodeProcessException(final String message) {
        super(message);
    }

    public NodeProcessException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
