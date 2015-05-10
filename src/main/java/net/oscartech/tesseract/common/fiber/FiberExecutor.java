package net.oscartech.tesseract.common.fiber;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Created by tylaar on 15/5/10.
 */
public class FiberExecutor extends AbstractExecutorService {

    private Runner runner;

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

    }

    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void execute(final Runnable command) {

    }
}
