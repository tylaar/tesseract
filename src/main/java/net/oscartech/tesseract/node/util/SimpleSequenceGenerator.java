package net.oscartech.tesseract.node.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by tylaar on 15/4/26.
 */
public class SimpleSequenceGenerator implements SequenceGenerator {
    private static final AtomicLong id = new AtomicLong(123456);
    @Override
    public long generateSequence() {
        return id.getAndIncrement();
    }
}
