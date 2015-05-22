package net.oscartech.tesseract.common.fiber;

import com.google.common.collect.Maps;
import net.oscartech.tesseract.common.concurrent.Callback;
import net.oscartech.tesseract.common.concurrent.CallbackUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by tylaar on 15/5/13.
 */
public class DefaultListeningAdvertiser<T> implements ListeningAdvertiser<T> {

    private final Map<Callback<? super T>, Executor> callbacks = Maps.newLinkedHashMap();

    private final ReadWriteLock callbackLock = new ReentrantReadWriteLock();

    @Override
    public void addCallback(final Executor executor, final Callback<? super T> callback) {
        if (callback == null || executor == null)
            return;
        callbackLock.writeLock().lock();
        try {
            callbacks.put(callback, executor);
        } finally {
            callbackLock.writeLock().unlock();
        }
    }

    @Override
    public void removeCallback(final Callback<? super T> callback) {
        if (callback == null)
            return;
        callbackLock.writeLock().lock();
        try {
            callbacks.remove(callback);
        } finally {
            callbackLock.writeLock().unlock();
        }
    }

    @Override
    public void publish(final T element) {
        callbackLock.readLock().lock();
        try {
            for (final Map.Entry<Callback<? super T>, Executor> context : callbacks.entrySet()) {
                final Callback<? super T> callback = context.getKey();
                final Executor executor = context.getValue();
                executor.execute(CallbackUtil.asyncFunctor(element, callback));
            }
        } finally {
            callbackLock.readLock().unlock();
        }
    }
}
