package net.oscartech.tesseract.node.pojo;

import net.oscartech.tesseract.node.NodeProposalBroker;
import net.oscartech.tesseract.node.exception.NodeProcessException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by tylaar on 15/5/16.
 */
public class MasterProposalTask extends ProposalTask {
    private NodeProposal proposal;
    private NodeProposalBroker proposalBroker;

    public MasterProposalTask(final NodeProposal proposal, final NodeProposalBroker broker) {
        this.proposal = proposal;
        this.proposalBroker = broker;
    }

    @Override
    public void run() {
        if (proposalBroker.getPreCommitCountingLatch().contains(proposal.getProposalId())) {
            throw new NodeProcessException("node pre commit failure. there is leaking latch resource in proposal broker.");
        }

        /**
         * Putting a latch inside the mapping for tracking.
         */
        final CountDownLatch latch = new CountDownLatch(proposalBroker.getQuorumSize() / 2 + 1);
        proposalBroker.getPreCommitCountingLatch().put(proposal.getProposalId(), latch);

        try {
            System.out.println("waiting for count down latch.");
            boolean result = latch.await(5, TimeUnit.SECONDS);
            if (result) {
                System.out.println("latch down reached.");
                proposalBroker.sendPrecommitProposal(proposal);
            } else {
                System.out.println("time out reached. This transaction will be aborted.");
                proposalBroker.getPreCommitCountingLatch().remove(proposal.getProposalId());
                proposalBroker.getOngoingProposalMapping().remove(proposal.getProposalId());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
