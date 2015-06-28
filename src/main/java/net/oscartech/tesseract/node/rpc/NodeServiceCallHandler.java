package net.oscartech.tesseract.node.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import net.oscartech.tesseract.node.NodeProposalBroker;
import net.oscartech.tesseract.node.pojo.NodeProposal;
import net.oscartech.tesseract.node.rpc.aop.RpcServiceProcessor;
import net.oscartech.tesseract.node.rpc.protocol.TessyCommand;
import net.oscartech.tesseract.node.util.MarshallUtils;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
* Created by tylaar on 15/4/29.
*/
@ChannelHandler.Sharable
public class NodeServiceCallHandler extends ChannelInboundHandlerAdapter {

    private RpcServiceProcessor serviceProcessor;
    private AtomicInteger counter = new AtomicInteger(0);

    public NodeServiceCallHandler(final RpcServiceProcessor processor) {
        this.serviceProcessor = processor;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


    /**
     * TODO: this version, the service returns a shit.
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        try {
            counter.getAndIncrement();
            TessyCommand command = MarshallUtils.fromStringToTessyCommand(in.toString(CharsetUtil.UTF_8));
            /**
             * One thing that we shall always bear in mind is that, proposal broker
             * theoretically shall be state less. The code is shared by different
             * thread.
             */
            serviceProcessor.callMethod(command);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
