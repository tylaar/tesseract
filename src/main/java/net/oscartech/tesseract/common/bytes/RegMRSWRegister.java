package net.oscartech.tesseract.common.bytes;

/**
 * Created by tylaar on 15/5/31.
 */
public class RegMRSWRegister implements Register<Byte> {

    private static final int RANGE = Byte.MAX_VALUE - Byte.MIN_VALUE + 1;
    private boolean[] r_bits = new boolean[RANGE];

    public RegMRSWRegister(int capacity) {
        for (int i = 0 ; i < capacity ; i++) {
            r_bits[i] = false;
        }
        r_bits[0] = true;
    }

    @Override
    public Byte read() {
        for (Byte i = 0 ; i < RANGE ; i++) {
            if (r_bits[i]) return i;
        }
        return -1;
    }

    @Override
    public void write(final Byte v) {
        r_bits[v] = true;
        for (int i = v - 1 ; v > 0 ; i--) {
            r_bits[i] = false;
        }
    }
}
