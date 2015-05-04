package net.oscartech.tesseract.common.clock;

/**
 * Refer to the underlying clock, but will simply add an offset of the clock, which exactly refer back
 * to the pass / or the future of the underlying clock. This clock knows nothing but refer to the underlying
 * clock.
 * Created by tylaar on 15/4/26.
 */
public class OffsetClock implements Clock {
    private final long offset;
    private final Clock backClock;

    public OffsetClock(final Clock backClock, final long offset) {
        this.backClock = backClock;
        this.offset = offset;
    }

    private boolean checkIfTimeOverflowed(long duration, long offset, long backFromNow) {

        return (offset < 0 && backFromNow < 0 && duration > 0) || (offset > 0 && backFromNow > 0 && duration < 0);

    }

    @Override
    public long now() {
        long backFromNow = backClock.now();
        long duration = backFromNow + offset;
        if (checkIfTimeOverflowed(duration, offset, backFromNow)) {
            throw new TimeOverflowException("time over flow detected");
        }
        return duration;
    }

    @Override
    public void schedule(final Runnable runnable, final long t) {
        long backFromNow = backClock.now();
        long duration = backFromNow + offset;
        if (checkIfTimeOverflowed(duration, offset, backFromNow)) {
            throw new TimeOverflowException("time over flow detected during scheduling");
        }
        backClock.schedule(runnable, duration);
    }
}
