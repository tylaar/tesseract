package net.oscartech.tesseract.node.task;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import net.oscartech.tesseract.node.exception.NodeProcessException;
import net.oscartech.tesseract.node.pojo.NodeProposal;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by tylaar on 15/5/16.
 */
public abstract class ProposalTask implements Runnable {
    @Override
    public void run() {
        NodeProposal wrapped = getWrappedProposal();

        if (wrapped == null) {
            Throwables.propagate(new NodeProcessException("task exception: null pointer for the wrapped task"));
            return;
        }

        latching(wrapped);
        cleanup();
    }

    protected abstract void cleanup();

    /**
     * Sometimes it will be easy for the impl task to determine how many ack it requires to finish an
     * operation. This is vital for some scenario to adjust the robust and availability to the system.
     * @return
     */
    protected abstract int getQuorumSize();

    protected abstract Map<Long, CountDownLatch> latchMap();

    public abstract NodeProposal getWrappedProposal();

    public abstract Function<Boolean, Void> getLatchingResultHook();

    public void latching(final NodeProposal wrapped) {
        /**
         * Putting a latch inside the mapping for tracking.
         */
        final CountDownLatch latch = new CountDownLatch(getQuorumSize() / 2 + 1);
        latchMap().put(wrapped.getProposalId(), latch);

        try {
            System.out.println("PreCommit : waiting for count down latch.");
            /**
             * Use default latching handle hookup to handle it.
             */
            getLatchingResultHook().apply(latch.await(5, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * ERROR: I try to add this in the thread, but apparently this shall be a synchronized
     * verification in the receiving main thread instead of a task, so I comment this line out.
     * In order to let every other thread know that there is a exception happening. At least
     * in the very early days, this is easy for debugging.
     */
    //public abstract boolean validatingProposalExistence();
}
