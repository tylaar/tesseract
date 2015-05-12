package net.oscartech.tesseract.common.concurrent;

/**
 * Created by tylaar on 15/4/27.
 */
public interface Callback<T> {
    void apply(T message);
}

