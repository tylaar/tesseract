package net.oscartech.tesseract.node.rpc.protocol;

/**
 * Created by tylaar on 15/6/13.
 */
public class TessyProtocolException extends RuntimeException {
    public static final int PARAM_ALREADY_EXIST = 0;
    public static final int PROTOCOL_NAME_NOT_EXIST = 1;

    private int code;
    private String description;

    public TessyProtocolException(final int code, final String description) {
        this.code = code;
        this.description = description;
    }

    public TessyProtocolException(final String message, final int code, final String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public TessyProtocolException(final String message, final Throwable cause, final int code, final String description) {
        super(message, cause);
        this.code = code;
        this.description = description;
    }
}