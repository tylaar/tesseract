package net.oscartech.tesseract.node.task;

import com.google.common.base.Function;
import net.oscartech.tesseract.node.NodeProposalBroker;
import net.oscartech.tesseract.node.pojo.NodeProposal;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * We will see if this abstract class layer is a good call ...
 * Created by tylaar on 15/5/16.
 */
public class MasterPreCommitProposalTask extends ProposalTask {

    private NodeProposal nodeProposal;
    private NodeProposalBroker proposalBroker;
    private Function<Boolean, Void> handleLatchingResult;

    public MasterPreCommitProposalTask(final NodeProposal proposal, final NodeProposalBroker broker) {
        this.nodeProposal = proposal;
        this.proposalBroker = broker;
        this.handleLatchingResult = new Function<Boolean, Void>() {
            @Nullable
            @Override
            public Void apply(final Boolean latchResult) {
                if (latchResult) {
                    System.out.println("I AAAAAMMMMM the MASTER !!!!");
                } else {
                    System.out.println("time out reached. This transaction will be aborted.");
                }
                return null;
            }
        };
    }

    @Override
    protected void cleanup() {
        proposalBroker.getCommitCountingLatch().remove(nodeProposal.getProposalId());
        proposalBroker.getOngoingProposalMapping().remove(nodeProposal.getProposalId());
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
        return nodeProposal;
    }

    @Override
    public Function<Boolean, Void> getLatchingResultHook() {
        return handleLatchingResult;
    }

}
