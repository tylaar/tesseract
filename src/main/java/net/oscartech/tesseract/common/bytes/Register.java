package net.oscartech.tesseract.common.bytes;

/**
 * Created by tylaar on 15/5/31.
 */
public interface Register<T> {
    T read();

    void write(T v);
}
