package net.oscartech.tesseract.common.clock;

/**
 * Created by tylaar on 15/4/26.
 */
public interface Clock {
    long now();

    void schedule(Runnable runnable, long t);
}
