package net.oscartech.tesseract.node.util;

import net.oscartech.tesseract.node.pojo.NodeProposal;
import net.oscartech.tesseract.node.rpc.RpcRequest;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * Created by tylaar on 15/4/29.
 */
public final class MarshallUtils {
    private MarshallUtils(){}
    public static NodeProposal fromStringToProposal(String target) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        NodeProposal proposal = mapper.readValue(target, NodeProposal.class);
        return proposal;
    }

    public static RpcRequest fromStringToRpcRequest(String target) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        RpcRequest request = mapper.readValue(target, RpcRequest.class);
        return request;
    }

    public static String serializeToString(NodeProposal proposal) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(proposal);
    }
}
