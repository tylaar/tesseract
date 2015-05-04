package net.oscartech.tesseract.node.rpc;

/**
 * Created by tylaar on 15/5/2.
 */
public interface RpcCallBack <I, O>{
    O apply(I input);
}
