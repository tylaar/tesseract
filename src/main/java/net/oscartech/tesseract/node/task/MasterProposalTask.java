package net.oscartech.tesseract.node.task;

import com.google.common.base.Throwables;
import net.oscartech.tesseract.node.NodeProposalBroker;
import net.oscartech.tesseract.node.pojo.NodeProposal;

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
    public void latching() {
        /**
         * Putting a latch inside the mapping for tracking.
         */
        final CountDownLatch latch = new CountDownLatch(proposalBroker.getQuorumSize() / 2 + 1);
        proposalBroker.getPreCommitCountingLatch().put(proposal.getProposalId(), latch);

        try {
            System.out.println("MasterINIT: waiting for count down latch.");
            boolean result = latch.await(5, TimeUnit.SECONDS);
            if (result) {
                System.out.println("latch down reached.");
                proposalBroker.sendPrecommitProposal(proposal);
            } else {
                System.out.println("timeout reaching for latch. aborting");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void cleanup() {
        System.out.println("cleaning up job trigger");
        proposalBroker.getPreCommitCountingLatch().remove(proposal.getProposalId());
        proposalBroker.getOngoingProposalMapping().remove(proposal.getProposalId());
    }

    @Override
    public NodeProposal getWrappedProposal() {
        return this.proposal;
    }
}
