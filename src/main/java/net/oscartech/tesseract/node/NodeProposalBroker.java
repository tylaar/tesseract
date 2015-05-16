package net.oscartech.tesseract.node;

import io.netty.channel.Channel;
import net.oscartech.tesseract.common.fiber.FiberExecutor;
import net.oscartech.tesseract.node.exception.NodeProcessException;
import net.oscartech.tesseract.node.pojo.NodeProposal;
import net.oscartech.tesseract.node.pojo.NodeProposalType;
import net.oscartech.tesseract.node.util.MarshallUtils;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A broker shall be responsible for tmux proposal.
 * Created by tylaar on 15/4/29.
 */
class NodeProposalBroker {

    private Node node;
    private NodePeerTopology peerTopology;
    private NodeProposalVerbConstructor verbConstructor;

    private ExecutorService threadPool = Executors.newFixedThreadPool(10);
    final ExecutorService fiber = new FiberExecutor(threadPool);

    /**
     * These fields below are protocol aware and vital.
     */
    private ConcurrentHashMap<Long, String> ongoingProposalMapping = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, CountDownLatch> latchCoucurrentHashMap = new ConcurrentHashMap<>();

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
            long replyProposalContent = node.tryToAcceptNewProposal("0", proposal.getProposalId());
            return verbConstructor.constructAckForProposal(proposal.getProposalId(), String.valueOf(replyProposalContent));
        }
        return null;
    }

    private void sendProposal(NodeProposal nodeProposal) {
        System.out.println("this is broker: " + this.hashCode() + " trying to send proposal.");

        try {
            peerTopology.awaitForNetworkToBeInitialized();
            for (Channel channel : peerTopology.getPeerHostChannels()) {

                if (channel.isWritable()) {
                    System.out.println("writable");
                }
                cacheProposalValue(nodeProposal);
                String proposalWords = MarshallUtils.serializeToString(nodeProposal);
                channel.writeAndFlush(proposalWords);
                System.out.println("delivering" + proposalWords);
            }
            tryToPreCommitProposal(nodeProposal);
        } catch (IOException e) {
            throw new NodeProcessException("during proposal marshalling, exception happened:", e);
        } catch (InterruptedException e) {
            throw new NodeProcessException("node initialization procedure interrupted.");
        }
    }

    /**
     * ONLY local thread can call this.
     * @param nodeProposal
     */
    private void cacheProposalValue(final NodeProposal nodeProposal) {
        ongoingProposalMapping.put(nodeProposal.getProposalId(), nodeProposal.getProposalContent());
    }

    private void tryToPreCommitProposal(final NodeProposal nodeProposal) {
        if (latchCoucurrentHashMap.contains(nodeProposal.getProposalId())) {
            throw new NodeProcessException("node pre commit failure. there is leaking latch resource in proposal broker.");
        }

        /**
         * Putting a latch inside the mapping for tracking.
         */
        final CountDownLatch latch = new CountDownLatch(this.peerTopology.getNetworkTopology().size() / 2 + 1);
        latchCoucurrentHashMap.put(nodeProposal.getProposalId(), latch);

        final Runnable preCommitHookUp = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("waiting for count down latch.");
                    boolean result = latch.await(5, TimeUnit.SECONDS);
                    if (result) {
                        System.out.println("latch down reached.");
                        sendPrecommitProposal(nodeProposal);
                    } else {
                        System.out.println("time out reached. This transaction will be aborted.");
                        latchCoucurrentHashMap.remove(nodeProposal.getProposalId());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        fiber.execute(preCommitHookUp);
    }

    private void sendPrecommitProposal(final NodeProposal nodeProposal) {

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
        if (ongoingProposalMapping.containsKey(reply.getProposalId())
                && ongoingProposalMapping.get(reply.getProposalId()).equals(reply.getProposalContent())) {
            /**
             * I've got a positive ack from peer side!.
             */
            CountDownLatch latch = latchCoucurrentHashMap.get(reply.getProposalId());
            if (latch == null) {
                System.out.println("latch timeout will automatically remove itself from the mapping.");
            } else {
                latch.countDown();

                System.out.println("counting down to: " + latch.getCount());
            }
        } else if (!ongoingProposalMapping.containsKey(reply.getProposalId())){
            System.out.println("no ongoing my proposal discovered in local cache. Probably timeout and failed.");
        }
    }
}
