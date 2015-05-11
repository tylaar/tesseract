package net.oscartech.tesseract.common.fiber;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by tylaar on 15/5/10.
 */
public class FiberExecutor extends AbstractExecutorService {

    private final Runner runner;

    public FiberExecutor(final Executor executor) {
        this.runner = new Runner(executor);
    }

    public FiberExecutor(final Executor executor, final int batchSize) {
        this.runner = new Runner(executor, batchSize);
    }

    public

    static class Runner extends FiberAdvertiser<Runnable> {

        /**
         * Default batchSize will be set to 10.
         * @param executor
         */
        public Runner(final Executor executor) {
            super(executor);
        }

        public Runner(final Executor executor, final int batchSize) {
            super(executor, batchSize);
        }

        @Override
        protected void process(final Runnable task) {
            task.run();
        }
    }

    private class Stopper implements Runnable {

        @Override
        public void run() {
            runner.halt();
        }


    }

    @Override
    public void shutdown() {
        this.runner.publish(new Stopper());
    }

    /**
     * We have to filter out the stopper poison stuff from the awaiting queue.
     * @return
     */
    @Override
    public List<Runnable> shutdownNow() {
        List<Runnable> awaitingExceptions = this.runner.haltNow();

        final Stopper stopper = new Stopper();
        while(awaitingExceptions.remove(stopper)) {
            Thread.currentThread().yield();
        }
        return awaitingExceptions;
    }

    @Override
    public boolean isShutdown() {
        return this.runner.isHalt();
    }

    @Override
    public boolean isTerminated() {
        return this.runner.isTerminated();
    }

    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        return awaitTermination(timeout, unit);
    }

    @Override
    public void execute(final Runnable command) {
        try {
            this.runner.publish(command);
        } catch (IllegalStateException e) {
            throw new RejectedExecutionException("rejected command as the runner has already been halted.");
        }
    }
}
