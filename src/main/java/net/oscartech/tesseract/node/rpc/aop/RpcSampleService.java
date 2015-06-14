package net.oscartech.tesseract.node.rpc.aop;

import org.springframework.context.annotation.Bean;

/**
 * Created by tylaar on 15/6/14.
 */
@RpcService(name = "sampleService")
public class RpcSampleService {

    public RpcSampleService() {
    }

    @RpcMethod(name = "command")
    public void calling(String one, String two) {
        System.out.println(one + " and " + two);
    }
}
