package net.oscartech.tesseract.node.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import net.oscartech.tesseract.node.exception.RpcException;
import net.oscartech.tesseract.node.util.MarshallUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tylaar on 15/5/2.
 */
@ChannelHandler.Sharable
public class NodeRpcServiceHandler extends ChannelInboundHandlerAdapter implements RpcConstants {

    private Map<String, RpcService> serviceMap = new ConcurrentHashMap<String, RpcService>();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        try {
            //System.out.println("server read hit" + MarshallUtils.fromStringToProposal(in.toString(CharsetUtil.UTF_8)).getProposalId());
            RpcRequest request = MarshallUtils.fromStringToRpcRequest(in.toString(CharsetUtil.UTF_8));
            if (request.getRequestType().equals(ASYNC_TYPE)) {
                /**
                 * Do async operation here.
                 */
            } else if (request.getRequestType().equals(SYNC_TYPE)) {
                /**
                 * Do sync operation here.
                 */
            } else {
                throw new RpcException("invalid operation type.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private RpcService getServiceByDescriptor(String descriptor) {
        return serviceMap.get(descriptor);
    }

    public void registerService(RpcService service) {
        serviceMap.put(service.getDescriptor(), service);
    }

    public void unregisterService(String descriptor) {
        serviceMap.remove(descriptor);
    }
}
