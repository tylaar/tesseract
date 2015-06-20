package net.oscartech.tesseract.node.rpc;

/**
 * Created by tylaar on 15/5/2.
 */
public abstract class AbstractRpcService implements RpcService {

    @Override
    public String getDescriptor() {
        return null;
    }

    @Override
    public long getResponseId() {
        return 0;
    }
}
