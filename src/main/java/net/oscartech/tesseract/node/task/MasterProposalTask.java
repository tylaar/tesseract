package net.oscartech.tesseract.node.task;

import com.google.common.base.Function;
import net.oscartech.tesseract.node.NodeProposalBroker;
import net.oscartech.tesseract.node.pojo.NodeProposal;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by tylaar on 15/5/16.
 */
public class MasterProposalTask extends ProposalTask {

    private NodeProposal proposal;
    private NodeProposalBroker proposalBroker;
    private Function<Boolean, Void> handleLatchingResult;

    public MasterProposalTask(final NodeProposal proposal, final NodeProposalBroker broker) {
        this.proposal = proposal;
        this.proposalBroker = broker;
        this.handleLatchingResult = new Function<Boolean, Void>() {
            @Nullable
            @Override
            public Void apply(final Boolean latchResult) {
                if (latchResult) {
                    System.out.println("Master proposal latching down reach.");
                    proposalBroker.sendPrecommitProposal(MasterProposalTask.this.proposal);
                } else {
                    System.out.println("time out reached. This transaction will be aborted.");
                }
                return null;
            }
        };
    }


    @Override
    protected void cleanup() {
        System.out.println("cleaning up job trigger");
        proposalBroker.getPreCommitCountingLatch().remove(proposal.getProposalId());
        proposalBroker.getOngoingProposalMapping().remove(proposal.getProposalId());
    }

    @Override
    protected int getQuorumSize() {
        return proposalBroker.getQuorumSize();
    }

    @Override
    protected Map<Long, CountDownLatch> latchMap() {
        return proposalBroker.getPreCommitCountingLatch();
    }

    @Override
    public NodeProposal getWrappedProposal() {
        return this.proposal;
    }

    @Override
    public Function<Boolean, Void> getLatchingResultHook() {
        return this.handleLatchingResult;
    }
}
