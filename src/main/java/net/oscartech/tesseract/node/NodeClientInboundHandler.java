package net.oscartech.tesseract.node;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import net.oscartech.tesseract.node.pojo.NodeProposal;
import net.oscartech.tesseract.node.util.MarshallUtils;

import java.io.IOException;

/**
 * Created by tylaar on 15/4/27.
 */
@ChannelHandler.Sharable
public class NodeClientInboundHandler extends ChannelInboundHandlerAdapter {

    private NodeProposalBroker broker;

    public NodeClientInboundHandler(final NodeProposalBroker broker) {
        this.broker = broker;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("active read");
        //ctx.writeAndFlush(Unpooled.copiedBuffer("netty rocks!", CharsetUtil.UTF_8));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf m = (ByteBuf) msg;
        try {
            NodeProposal reply = MarshallUtils.fromStringToProposal(m.toString(CharsetUtil.UTF_8));
            System.out.println("CLIENT read reply" + reply.getProposalId() + " and type: " + reply.getType());
            /**
             * One thing that we shall always bear in mind is that, proposal broker
             * theoretically shall be state less. The code is shared by different
             * thread.
             */

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
