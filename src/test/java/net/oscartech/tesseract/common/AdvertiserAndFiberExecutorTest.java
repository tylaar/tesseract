package net.oscartech.tesseract.common;

import net.oscartech.tesseract.common.fiber.FiberExecutor;
import org.junit.After;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by tylaar on 15/5/12.
 */
public class AdvertiserAndFiberExecutorTest {
    private ExecutorService threadPool = Executors.newFixedThreadPool(10);

    @After
    public void shutdown() throws InterruptedException {
        threadPool.shutdownNow();
        fullyAwaitTermination(threadPool);
    }

    private void fullyAwaitTermination(final ExecutorService threadPool) throws InterruptedException {
        while(!threadPool.isTerminated()) {
            threadPool.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    @Test(timeout = 10000)
    public void shouldWorkSimply() throws InterruptedException {
        final ExecutorService fiber = new FiberExecutor(threadPool);
        final CountDownLatch latch = new CountDownLatch(2);

        final Runnable decrementor = new Runnable() {
            @Override
            public void run() {
                latch.countDown();
            }
        };

        fiber.execute(decrementor);
        fiber.execute(decrementor);
        latch.await();
    }
}
