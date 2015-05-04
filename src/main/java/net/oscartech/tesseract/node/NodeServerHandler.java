package net.oscartech.tesseract.node;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import net.oscartech.tesseract.node.util.MarshallUtils;

import java.io.IOException;

/**
* Created by tylaar on 15/4/29.
*/
@ChannelHandler.Sharable
class NodeServerHandler extends ChannelInboundHandlerAdapter {

    private NodeProposalBroker proposalBroker;

    NodeServerHandler(final NodeProposalBroker proposalBroker) {
        this.proposalBroker = proposalBroker;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        try {
            System.out.println("server read hit" + MarshallUtils.fromStringToProposal(in.toString(CharsetUtil.UTF_8)).getProposalId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
