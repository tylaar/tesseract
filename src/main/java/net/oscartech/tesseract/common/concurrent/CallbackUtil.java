package net.oscartech.tesseract.common.concurrent;

/**
 * Created by tylaar on 15/5/13.
 */
public class CallbackUtil {
    private CallbackUtil() {}

    public static <T> Runnable asyncFunctor(final T message, final Callback<T> callback) {
        return new Runnable() {
            @Override
            public void run() {
                callback.apply(message);
            }
        };
    }
}
