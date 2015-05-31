package net.oscartech.tesseract.common.bytes;

/**
 * Created by tylaar on 15/5/31.
 */
public class AtomicSRSWRegister<T> implements Register<T> {
    ThreadLocal<Long> lastStamp;
    ThreadLocal<StampedValue<T>> lastRead;

    StampedValue<T> r_value;

    public AtomicSRSWRegister(final StampedValue<T> r_value) {
        this.r_value = r_value;
        this.lastStamp = new ThreadLocal<Long>() {
            protected Long initialValue() {return 0l;}
        };
        this.lastRead = new ThreadLocal<StampedValue<T>>() {
            protected StampedValue<T> initialValue() {return r_value;}
        };
    }

    @Override
    public T read() {
        StampedValue<T> value = r_value;
        StampedValue<T> last = lastRead.get();
        StampedValue<T> result = StampedValue.max(value, last);
        lastRead.set(result);
        return result.value;
    }

    @Override
    public void write(final T v) {
        long stamp = lastStamp.get() + 1;
        r_value = new StampedValue<T>(stamp, v);
        lastStamp.set(stamp);
    }
}
