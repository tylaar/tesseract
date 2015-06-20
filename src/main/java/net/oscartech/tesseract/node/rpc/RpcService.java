package net.oscartech.tesseract.node.rpc;

/**
 * Created by tylaar on 15/5/2.
 */
public interface RpcService <I, O>{

    O syncCall(I input);

    void asyncCall(I input, RpcCallBack<I, O> callBack);

    String getDescriptor();

    long getResponseId();

}
