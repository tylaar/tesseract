package net.oscartech.tesseract.node;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import net.oscartech.tesseract.node.pojo.NodeProposal;
import net.oscartech.tesseract.node.util.MarshallUtils;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
* Created by tylaar on 15/4/29.
*/
@ChannelHandler.Sharable
public class NodeServerHandler extends ChannelInboundHandlerAdapter {

    private NodeProposalBroker proposalBroker;
    private AtomicInteger counter = new AtomicInteger(0);

    public NodeServerHandler(final NodeProposalBroker proposalBroker) {
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
            counter.getAndIncrement();
            NodeProposal proposal = MarshallUtils.fromStringToProposal(in.toString(CharsetUtil.UTF_8));
            //System.out.println("server read hit" + proposal.getProposalId() + " and type: " + proposal.getType());
            /**
             * One thing that we shall always bear in mind is that, proposal broker
             * theoretically shall be state less. The code is shared by different
             * thread.
             */
            NodeProposal reply = proposalBroker.handleProposal(proposal);

            if (reply != null && ctx.channel().isWritable()) {
                System.out.println("replying: ");
                proposalBroker.replyProposal(ctx.channel(), reply);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCounter() {
        return counter.get();
    }

}
