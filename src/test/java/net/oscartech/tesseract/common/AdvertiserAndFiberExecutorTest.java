package net.oscartech.tesseract.common;

import com.google.common.collect.Lists;
import net.oscartech.tesseract.common.concurrent.Callback;
import net.oscartech.tesseract.common.fiber.DefaultListeningAdvertiser;
import net.oscartech.tesseract.common.fiber.FiberExecutor;
import net.oscartech.tesseract.common.fiber.ListeningAdvertiser;
import org.junit.After;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

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
                System.out.println("yes");
                latch.countDown();
            }
        };

        fiber.execute(decrementor);
        fiber.execute(decrementor);
        latch.await();
    }

    @Test(timeout = 10000)
    public void testDefaultListeningAdvertiser() throws InterruptedException {
        final ExecutorService fiber = new FiberExecutor(threadPool);
        final ListeningAdvertiser<String> advertiser = new DefaultListeningAdvertiser<>();

        final CountDownLatch latch = new CountDownLatch(3);
        advertiser.addCallback(fiber, new Callback<String>() {
            @Override
            public void apply(final String message) {
                if (message.equals("Hello")) {
                    latch.countDown();
                    latch.countDown();
                } else if (message.equals("Goodbye")) {
                    latch.countDown();
                }
            }
        });

        final CountDownLatch adderLatch = new CountDownLatch(2);
        final List<String> messages = new CopyOnWriteArrayList<>();
        advertiser.addCallback(fiber, new Callback<String>() {
            @Override
            public void apply(final String message) {
                messages.add(message);
                adderLatch.countDown();
            }
        });
        assertEquals(3, latch.getCount());
        advertiser.publish("Hello");
        advertiser.publish("Goodbye");
        latch.await();
        adderLatch.await();
        assertEquals(0, latch.getCount());
        assertEquals(Lists.newArrayList("Hello", "Goodbye"), messages);
    }
}
