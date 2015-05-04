package net.oscartech.tesseract.common.concurrent;

/**
 * Created by tylaar on 15/4/27.
 */
public interface Callback<T> {
    void apply(T message);
}

class CallbackUtil {
    private CallbackUtil() {}

    static <T> Runnable asyncFunctor(final T message, final Callback<T> callback) {
        return new Runnable() {
            @Override
            public void run() {
                callback.apply(message);
            }
        };
    }
}
