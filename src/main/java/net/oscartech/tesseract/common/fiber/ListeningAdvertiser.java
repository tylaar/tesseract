package net.oscartech.tesseract.common.fiber;

import net.oscartech.tesseract.common.concurrent.Callback;

import java.util.concurrent.Executor;

/**
 * Created by tylaar on 15/5/12.
 */
public interface ListeningAdvertiser<T> extends Advertiser<T> {
    void addCallback(final Executor executor, final Callback<? super T> callback);

    void removeCallback(final Callback<? super T> callback);
}
