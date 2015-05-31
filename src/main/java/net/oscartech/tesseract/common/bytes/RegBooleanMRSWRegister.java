package net.oscartech.tesseract.common.bytes;

/**
 * Created by tylaar on 15/5/31.
 */
public class RegBooleanMRSWRegister implements Register<Boolean> {

    private ThreadLocal<Boolean> last;
    private boolean value;

    public RegBooleanMRSWRegister(int capacity) {
        last = new ThreadLocal<Boolean>() {
            protected Boolean initialValue() {return false;}
        };
    }

    @Override
    public Boolean read() {
        return value;
    }

    @Override
    public void write(final Boolean v) {
        if (v != last.get()) {
            last.set(v);
            value = v;
        }
    }
}
