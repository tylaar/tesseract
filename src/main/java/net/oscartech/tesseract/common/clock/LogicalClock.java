package net.oscartech.tesseract.common.clock;

/**
 * Created by tylaar on 15/4/26.
 */
public class LogicalClock implements Clock {
    private long now;
    private final TaskQueue taskQueue;

    public LogicalClock(final TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    public LogicalClock() {
        this.taskQueue = null;
    }

    public synchronized void set(long t) {
        this.now = t;
    }

    public void tick(long delta) {
        long duration = tickImpl(delta);
        taskQueue.runTasks(duration);
    }

    private long tickImpl(final long delta) {
        long duration = delta + now;
        if ((delta < 0 && now < 0 && duration > 0) || (delta > 0 && now > 0 && duration < 0)) {
            throw new TimeOverflowException("time overflowing issue detected in logical clock");
        }
        now = duration;
        return now;
    }

    @Override
    public long now() {
        return now;
    }

    @Override
    public void schedule(final Runnable runnable, final long t) {
        taskQueue.schedule(now(), t, runnable);
    }
}
