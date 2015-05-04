package net.oscartech.tesseract.node.rpc;

import java.util.Map;

/**
 * Created by tylaar on 15/5/2.
 */
public class RpcRequest {
    private String requestService;
    /**
     * Only two type will be available: sync, and async;
     */
    private String requestType;
    private Map<String, String> requestParam;

    public String getRequestService() {
        return requestService;
    }

    public void setRequestService(final String requestService) {
        this.requestService = requestService;
    }

    public Map<String, String> getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(final Map<String, String> requestParam) {
        this.requestParam = requestParam;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(final String requestType) {
        this.requestType = requestType;
    }
}
