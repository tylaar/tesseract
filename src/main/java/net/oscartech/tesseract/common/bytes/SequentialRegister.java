package net.oscartech.tesseract.common.bytes;

/**
 * Created by tylaar on 15/5/31.
 */
public class SequentialRegister<T> implements Register<T> {

    T value;

    @Override
    public T read() {
        return value;
    }

    @Override
    public void write(final T v) {
        value = v;
    }
}
