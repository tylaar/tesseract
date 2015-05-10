package net.oscartech.tesseract.common.fiber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;

/**
 * Created by tylaar on 15/5/10.
 */
public abstract class FiberAdvertiser<T> extends Fiber implements Advertiser<T> {

    private final int batchSize;

    private ConcurrentLinkedDeque<T> queue = new ConcurrentLinkedDeque<>();

    private static final int DEFAULT_BATCH_SIZE = 10;

    public FiberAdvertiser(final Executor executor) {
        super(executor);
        this.batchSize = DEFAULT_BATCH_SIZE;
    }

    public FiberAdvertiser(final Executor executor, final int batchSize) {
        super(executor);
        this.batchSize = batchSize;
    }

    @Override
    public boolean isSignaled() {
        return !queue.isEmpty();
    }

    protected void processSignal() {
        for (int i = 0 ; i < batchSize ; i++) {
            final T task = queue.poll();
            /**
             * If no task been picked, then it will go back to idle.
             */
            if (task == null) {
                return;
            }
            process(task);

            if(isPaused() || isHalt()) {
                return;
            }
        }
    }

    protected abstract void process(final T task);

    public synchronized List<T> haltNow() {
        halt();
        List<T> remains = new ArrayList<T>(queue);
        queue.clear();
        return remains;
    }

    @Override
    public void publish(T element) {
        if (isHalt()) {
            return;
        }
        queue.add(element);
        signal();
        if (isHalt() || queue.remove(element));
    }
}
