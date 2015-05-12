package net.oscartech.tesseract.common.concurrent;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tylaar on 15/5/12.
 */
public class TessyThreadFactory  implements ThreadFactory {

    private final String name;
    private final AtomicInteger threadCount = new AtomicInteger(0);

    private Set<Thread> liveThreads = new HashSet<>();
    private Set<Thread> dyingThreads = new HashSet<>();

    public TessyThreadFactory(final String name) {
        this.name = name;
    }

    public TessyThreadFactory() {
        this.name = "Tessy_default_thread_factory";
    }

    private class ThreadTask implements Runnable {
        final Runnable runnable;

        public ThreadTask(final Runnable runnable) {
            this.runnable = runnable;
        }


        @Override
        public void run() {
            try {
                runnable.run();
            } finally {
                moveToDyingThread(Thread.currentThread());
            }
        }
    }

    private synchronized void addThread(Thread t) {
        liveThreads.add(t);
    }

    private void moveToDyingThread(final Thread thread) {
        if (liveThreads.remove(thread)) {
            dyingThreads.add(thread);
        }
        throw new RuntimeException("moving to dying thread failed");
    }

    @Override
    public Thread newThread(final Runnable r) {
        Thread t = new Thread(new ThreadTask(r), name + "-" + threadCount.incrementAndGet());
        t.setDaemon(true);
        addThread(t);
        cleanUp();
        return t;
    }

    /**
     * Used to monitor and join all threads.
     * @throws InterruptedException
     */
    public void joinAll() throws InterruptedException {
        for (Thread t : allThreads()) {
            t.join();
        }
    }

    private synchronized List<Thread> allThreads() {
        List<Thread> threads = new ArrayList<>(liveThreads);
        threads.addAll(dyingThreads);
        return threads;
    }

    private synchronized void cleanUp() {
        Iterator<Thread> iterator = dyingThreads.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getState() == Thread.State.TERMINATED) {
                iterator.remove();
            }
        }
    }
}
