package net.oscartech.tesseract.common.fiber;

/**
 * Created by tylaar on 15/5/10.
 */
public interface Advertiser<T> {
    void publish(T element);
}
