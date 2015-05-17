package net.oscartech.tesseract.node.task;

import com.google.common.base.Throwables;
import net.oscartech.tesseract.node.exception.NodeProcessException;
import net.oscartech.tesseract.node.pojo.NodeProposal;

/**
 * Created by tylaar on 15/5/16.
 */
public abstract class ProposalTask implements Runnable {
    @Override
    public void run() {
        NodeProposal wrapped = getWrappedProposal();
        if (!validatingProposalExistence()) {
            System.out.println("task exception: no existing proposal id latch for this proposal, ignoring.");
            return;
        }
        if (wrapped == null) {
            Throwables.propagate(new NodeProcessException("task exception: null pointer for the wrapped task"));
            return;
        }

        latching();
        cleanup();
    }

    protected abstract void cleanup();

    public abstract NodeProposal getWrappedProposal();

    public abstract void latching();
    /**
     * In order to let every other thread know that there is a exception happening. At least
     * in the very early days, this is easy for debugging.
     */
    public abstract boolean validatingProposalExistence();
}
