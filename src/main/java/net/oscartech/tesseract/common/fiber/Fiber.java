package net.oscartech.tesseract.common.fiber;

import com.google.common.base.Throwables;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Created by tylaar on 15/5/9.
 */
public abstract class Fiber {
    public enum State {
        RUNNING,
        PAUSE,
        HALT,
    }

    private static final int IDLE = 0;
    private static final int ACTIVE = 1;
    /**
     * You can not use the normal operation to operate on this field.
     * CAS is the only way to update this field, to prevent racing condition.
     */
    private volatile int isActive = 0;

    private volatile State state = State.RUNNING;

    /**
     * Poison pill for terminating threads
     */
    private CountDownLatch terminator = new CountDownLatch(1);

    //private AtomicInteger ACTIVE_CAS = new AtomicInteger(IDLE);
    private static final AtomicIntegerFieldUpdater<Fiber> ACTIVE_CAS = AtomicIntegerFieldUpdater.newUpdater(Fiber.class, "currentState");
    private static final AtomicReferenceFieldUpdater<Fiber, State> STATE_CAS = AtomicReferenceFieldUpdater.newUpdater(Fiber.class, State.class, "state");

    private final Executor executor;

    private Runnable processor = new Runnable() {
        @Override
        public void run() {
            processSignalOnExecutor();
        }
    };

    public Fiber(final Executor executor) {
        this.executor = executor;
    }

    public void signal() {
        if (state == State.RUNNING) {
            ensureRunning();
        }
    }

    private void ensureRunning() {
        if (ACTIVE_CAS.getAndSet(this, ACTIVE) == IDLE) {
            executor.execute(processor);
        }
    }

    public void pause() {
        STATE_CAS.compareAndSet(this, State.RUNNING, State.PAUSE);
    }

    public void unPause() {
        if (STATE_CAS.compareAndSet(this, State.PAUSE, State.RUNNING) && isSignaled()) {
            ensureRunning();
        }
    }

    public void halt() {
        STATE_CAS.set(this, State.HALT);
        /**
         * TODO: might be buggy.
         */
        if (ACTIVE_CAS.getAndSet(this, IDLE) == ACTIVE) {
            terminator.countDown();
        }
    }

    private void processSignalOnExecutor() {
        try {
            if (state == State.HALT) {
                terminator.countDown();
                return;
            }

            processSignal();

            isActive = IDLE;
            final State currentState = state;
            if (currentState == State.HALT) {
                halt();
                return;
            } else if (currentState == State.RUNNING && isSignaled()) {
                ensureRunning();
            }
        } catch (final Throwable e) {
            handleError(e);
        }
    }

    public final boolean awaitTerminated(final long timeout, final TimeUnit timeUnit) throws InterruptedException {
        return terminator.await(timeout, timeUnit);
    }

    private void handleError(final Throwable e) {
        Throwables.propagate(e);
    }

    public boolean isTerminated() {
        return terminator.getCount() == 0;
    }

    public boolean isHalt() {
        return state == State.HALT;
    }

    public boolean isPaused() {
        return state == State.PAUSE;
    }

    public abstract boolean isSignaled();

    protected abstract void processSignal();
}
