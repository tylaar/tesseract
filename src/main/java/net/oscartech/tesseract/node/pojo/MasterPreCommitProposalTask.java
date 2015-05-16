package net.oscartech.tesseract.node.pojo;

import net.oscartech.tesseract.node.NodeProposalBroker;
import net.oscartech.tesseract.node.exception.NodeProcessException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by tylaar on 15/5/16.
 */
public class MasterPreCommitProposalTask extends ProposalTask {

    private NodeProposal nodeProposal;
    private NodeProposalBroker proposalBroker;

    public MasterPreCommitProposalTask(final NodeProposal proposal, final NodeProposalBroker broker) {
        this.nodeProposal = proposal;
        this.proposalBroker = broker;
    }

    @Override
    public void run() {
        if (proposalBroker.getCommitCountingLatch().contains(nodeProposal.getProposalId())) {
            throw new NodeProcessException("node commit failure. there is leaking latch resource in proposal commit broker.");
        }

        /**
         * Putting a latch inside the mapping for tracking.
         */
        final CountDownLatch latch = new CountDownLatch(proposalBroker.getQuorumSize() / 2 + 1);
        proposalBroker.getCommitCountingLatch().put(nodeProposal.getProposalId(), latch);

        try {
            System.out.println("waiting for count down latch.");
            boolean result = latch.await(5, TimeUnit.SECONDS);
            if (result) {
                System.out.println("I AAAAAMMMMM the MASTER !!!!");
            } else {
                System.out.println("time out reached. This transaction will be aborted.");
                proposalBroker.getCommitCountingLatch().remove(nodeProposal.getProposalId());
                proposalBroker.getOngoingProposalMapping().remove(nodeProposal.getProposalId());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
