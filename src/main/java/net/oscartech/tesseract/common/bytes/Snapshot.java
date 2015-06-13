package net.oscartech.tesseract.common.bytes;

/**
 * Created by tylaar on 15/5/31.
 */
public interface Snapshot<T> {
    public void update(T v);

    public T[] scan();
}
