package net.oscartech.tesseract.node.rpc;

/**
 * Created by tylaar on 15/5/2.
 */
public class RpcResponce {
    private long responceId;
    /**
     * If the calling is a SUCCESS, here it will be marked as success.
     */
    private String errorCode;
    /**
     * Simple, or JSON, or other. Defined in RpcConstants interface.
     */
    private String responceType;
    private String responceBody;

    public long getResponceId() {
        return responceId;
    }

    public void setResponceId(final long responceId) {
        this.responceId = responceId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }

    public String getResponceType() {
        return responceType;
    }

    public void setResponceType(final String responceType) {
        this.responceType = responceType;
    }

    public String getResponceBody() {
        return responceBody;
    }

    public void setResponceBody(final String responceBody) {
        this.responceBody = responceBody;
    }
}
