package net.oscartech.tesseract.common.bytes;

/**
 * Created by tylaar on 15/5/31.
 */
public class StampedValue<T> {
    public long stamp;;
    public T value;

    public StampedValue(final long stamp, final T value) {
        this.stamp = stamp;
        this.value = value;
    }

    public StampedValue(final T value) {
        stamp = 0;
        this.value = value;
    }

    public static StampedValue max(StampedValue x, StampedValue y) {
        if (x.stamp > y.stamp) return x;
        else return y;
    }

    public static StampedValue MIN = new StampedValue(null);
}
