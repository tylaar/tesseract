package net.oscartech.tesseract.node;

import net.oscartech.tesseract.node.pojo.NodeProposal;
import net.oscartech.tesseract.node.pojo.NodeProposalType;
import net.oscartech.tesseract.node.util.SequenceGenerator;

/**
 * Created by tylaar on 15/4/29.
 */
public class NodeProposalVerbConstructor {

    private SequenceGenerator sequenceGenerator;

    public NodeProposalVerbConstructor(final SequenceGenerator sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }

    public NodeProposal constructMasterSelectionProposal() {
        NodeProposal proposal = new NodeProposal();
        proposal.setNanoDuration(System.nanoTime());
        proposal.setType(NodeProposalType.MASTER_SELECTION.getCode());
        long proposalId = sequenceGenerator.generateSequence();
        proposal.setProposalId(proposalId);
        proposal.setProposalContent(String.valueOf(proposalId));
        return proposal;
    }

    public NodeProposal constructAckForProposal(long proposalId, String proposalContent) {
        NodeProposal proposal = new NodeProposal();
        proposal.setNanoDuration(System.nanoTime());
        proposal.setType(NodeProposalType.ACK.getCode());
        proposal.setProposalId(proposalId);
        proposal.setProposalContent(proposalContent);
        return proposal;
    }
}
