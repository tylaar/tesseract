package net.oscartech.tesseract.node.pojo;

/**
 * Created by tylaar on 15/4/26.
 */
public class NodeProposal {
    private long nanoDuration;
    private int type;
    private long proposalId;

    public long getNanoDuration() {
        return nanoDuration;
    }

    public void setNanoDuration(final long nanoDuration) {
        this.nanoDuration = nanoDuration;
    }

    public long getProposalId() {
        return proposalId;
    }

    public void setProposalId(final long proposalId) {
        this.proposalId = proposalId;
    }

    public int getType() {
        return type;
    }

    public void setType(final int type) {
        this.type = type;
    }
}
