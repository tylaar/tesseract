package net.oscartech.tesseract.common.clock;

import org.apache.log4j.Logger;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by tylaar on 15/4/26.
 */
public class NanoClock implements Clock {

    private final static Logger log = Logger.getLogger(NanoClock.class);
    private final long offset = System.nanoTime();
    private final TaskQueue taskQueue = new TaskQueue();
    private NanoScheduler scheduler;

    public interface NanoScheduler {
        void schedule(Runnable runnable, long nanoSecDelay);
    }

    public NanoClock(final ScheduledExecutorService service) {
        if (service == null) {
            this.scheduler = null;
        }
        this.scheduler = new NanoScheduler() {
            @Override
            public void schedule(final Runnable runnable, final long nanoSecDelay) {
                service.schedule(runnable, nanoSecDelay, TimeUnit.NANOSECONDS);
            }
        };
    }

    public static NanoClock newNanoClock(final ScheduledExecutorService executorService) {
        final NanoScheduler scheduler = new NanoScheduler() {
            @Override
            public void schedule(final Runnable runnable, final long nanoSecDelay) {
                executorService.schedule(runnable, nanoSecDelay, TimeUnit.NANOSECONDS);
            }
        };
        return new NanoClock(scheduler);
    }

    public NanoClock(final NanoScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public NanoClock() {
        this.scheduler = null;
    }

    @Override
    public long now() {
        long now = System.nanoTime() - offset;
        return now;
    }

    /**
     * A passive hook up to the taskQueue, which require the thread to check if the system can run the task now.
     * TaskQueue guarantee that the consistency issue of the priority issue. It's simply like a polling.
     * @param runnable
     * @param t
     */
    @Override
    public void schedule(final Runnable runnable, final long t) {
        if (this.scheduler == null) {
            throw new UnsupportedOperationException("not scheduler set for this clock");
        }
        long now = now();
        if (now - t > TimeUnit.SECONDS.toNanos(5)) {
            log.warn("you are scheduling a task which already passed in 5 seconds");
        }
        boolean executed = taskQueue.schedule(now, t, runnable);
        if (!executed) {
            final long delay = t - now;
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    long now = now();
                    if (t > now) {
                        while (t > (now = now()))
                            Thread.yield();
                    }
                    taskQueue.runTasks(now);
                }
            }, delay);
        }
    }
}
