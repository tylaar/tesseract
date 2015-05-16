package net.oscartech.tesseract.common.fiber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This is a queue where task's priority shall be considered.
 * You need to make sure the task is comparable.
 * Created by tylaar on 15/5/13.
 */
public abstract class PriorityFiberAdvertiser<T> extends Fiber implements Advertiser<T> {

    private static final int DEFAULT_QUEUE_SIZE = 10;
    private static final int DEFAULT_BATCH_SIZE = 10;

    private PriorityBlockingQueue<T> queue;
    private ReadWriteLock queueLock = new ReentrantReadWriteLock();
    private final int batchSize;

    public PriorityFiberAdvertiser(final Executor executor) {
        super(executor);
        queue = new PriorityBlockingQueue<>(DEFAULT_QUEUE_SIZE);
        batchSize = DEFAULT_BATCH_SIZE;
    }

    public PriorityFiberAdvertiser(final Executor executor, final int queueSize) {
        super(executor);
        queue = new PriorityBlockingQueue<>(queueSize);
        batchSize = DEFAULT_BATCH_SIZE;
    }

    public PriorityFiberAdvertiser(final Executor executor, final int queueSize, final int batchSize) {
        super(executor);
        this.batchSize = batchSize;
        this.queue = new PriorityBlockingQueue<>(queueSize);
    }

    @Override
    public synchronized boolean isSignaled() {
        return !queue.isEmpty();
    }

    protected void processSignal() {
        for (int i = 0 ; i < batchSize ; i++) {

            final T task = queue.poll();
            /**
             * If no task been picked, then it will go back to idle.
             * It is still potential to result in some low priority
             * task never been executed since the priority high one
             * will always been executed first. No easy guarantee.
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

    /**
     * As the priority queue is not concurrent, we need to use the default internal lock mechanism.
     * @param element
     */
    @Override
    public void publish(T element) {
        queueLock.writeLock().lock();
        try {
            if (isHalt()) {
                return;
            }
            queue.add(element);
            signal();
            if (isHalt())
                queue.remove(element);
        } finally {
            queueLock.writeLock().unlock();
        }

    }

}
