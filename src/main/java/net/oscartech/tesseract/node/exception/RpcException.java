package net.oscartech.tesseract.node.exception;

/**
 * Created by tylaar on 15/5/2.
 */
public class RpcException extends RuntimeException {
    public RpcException() {
    }

    public RpcException(final String message) {
        super(message);
    }

    public RpcException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
