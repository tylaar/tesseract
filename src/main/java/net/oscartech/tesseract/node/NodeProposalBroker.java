package net.oscartech.tesseract.node;

import io.netty.channel.Channel;
import net.oscartech.tesseract.node.exception.NodeProcessException;
import net.oscartech.tesseract.node.pojo.NodeProposal;
import net.oscartech.tesseract.node.pojo.NodeProposalType;
import net.oscartech.tesseract.node.util.MarshallUtils;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A broker shall be responsible for tmux proposal.
 * Created by tylaar on 15/4/29.
 */
class NodeProposalBroker {

    private Node node;
    private NodePeerTopology peerTopology;
    private NodeProposalVerbConstructor verbConstructor;

    /**
     * These fields below are protocol aware and vital.
     */
    private ConcurrentHashMap<Long, Integer> ongoingProposalMapping = new ConcurrentHashMap<>();

    public NodeProposalBroker(final Node node, final NodePeerTopology peerTopology, final NodeProposalVerbConstructor verbConstructor) {
        this.node = node;
        this.peerTopology = peerTopology;
        this.verbConstructor = verbConstructor;
    }

    /**
     * Tmuxing all the proposal to specific handler.
     * @param proposal
     * @return
     */
    public NodeProposal handleProposal(NodeProposal proposal) {
        if (proposal.getType() == NodeProposalType.MASTER_SELECTION.getCode()) {
            return handleMasterSelection(proposal);
        } else if (proposal.getType() == NodeProposalType.LOCK_ACQUIRE.getCode()) {
            return handleNormalProposal(proposal);
        } else {
            return null;
        }
    }

    private NodeProposal handleNormalProposal(final NodeProposal proposal) {
        System.out.println("hitting normal proposal");
        return null;
    }

    private NodeProposal handleMasterSelection(final NodeProposal proposal) {
        if (node.canAcceptMasterSelection()) {
            NodeProposal reply = new NodeProposal();
            reply.setNanoDuration(System.nanoTime());
            long replyProposalId = node.tryToAcceptNewProposal("0", proposal.getProposalId());
            reply.setProposalId(replyProposalId);
            reply.setType(NodeProposalType.ACK.getCode());
            return reply;
        }
        return null;
    }

    private void sendProposal(NodeProposal nodeProposal) {
        System.out.println("this is broker: " + this.hashCode() + " trying to send proposal.");
        if (!peerTopology.isNetworkInitialized()) {
            System.out.println("not ready for proposal");
            return;
        }
        try {
            for (Channel channel : peerTopology.getPeerHostChannels()) {
                if (channel.isWritable()) {
                    System.out.println("writable");
                }
                String proposalWords = MarshallUtils.serializeToString(nodeProposal);
                channel.writeAndFlush(proposalWords);
                System.out.println("delivering" + proposalWords);
            }
        } catch (IOException e) {
            throw new NodeProcessException("during proposal marshalling, exception happened:", e);
        }
    }

    public void replyProposal(Channel targetChannel, NodeProposal proposalReply) {
        System.out.println("this is broker: " + this.hashCode() + " trying to reply proposal.");
        try {
            if (targetChannel.isWritable()) {
                System.out.println("broker is going to reply to the writable channel");
                targetChannel.writeAndFlush(MarshallUtils.serializeToString(proposalReply));
            }
        } catch (IOException e) {
            throw new NodeProcessException("during proposal marshalling, exception happened:", e);
        }
    }

    public void sendMasterProposal() throws IOException {
        NodeProposal proposal = verbConstructor.constructMasterSelectionProposal();
        sendProposal(proposal);
    }

    public void handleAck(final NodeProposal reply) {
        if (reply.getType() != NodeProposalType.ACK.getCode()) {
            throw new RuntimeException("if this is not an ack, I shall not be received.");
        }

    }
}
