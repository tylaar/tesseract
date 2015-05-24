package net.oscartech.tesseract.common.bytes;

/**
 * Created by tylaar on 15/5/24.
 */
public final class ByteArrayUtils {
    private ByteArrayUtils() {
    }
    public static final byte[] EMPTY = new byte[]{};

    /**
     * Offset is compared here. so Arrays.equal is not a good choice.
     * @param b1
     * @param offset1
     * @param length1
     * @param b2
     * @param offset2
     * @param length2
     * @return
     */
    public static boolean areBytesEqual(byte[] b1, int offset1, int length1,
                                        byte[] b2, int offset2, int length2) {
        if (b1 == b2 && offset1 == offset2 && length1 == length2) {
            return true;
        }
        if((b1 == null) != (b2 == null)) {
            return false;
        }

        if (length1 != length2) {
            return false;
        }

        for (int i = 0 ; i < length1 ; i++) {
            if (b1[i] != b2[i]) {
                return false;
            }
        }
        return true;
    }

    public static int compare(byte[] b1, byte[] b2) {
        if (b2 == null) {
            if (b1 == null)
                return 0;
            else
                return 1;
        }
        for (int i = 0 ; i < Math.min(b1.length, b2.length) ; i++) {
            int unsignedLeft = b1[i] & 0xff;
            int unsignedRight = b2[i] & 0xff;
            if (unsignedLeft < unsignedRight)
                return -1;
            else if(unsignedLeft > unsignedRight)
                return 1;
        }
        if (b1.length < b2.length) {
            return -1;
        } else if(b1.length > b2.length) {
            return 1;
        }
        return 0;
    }

    public static boolean matchesPrefix(byte[] target, byte[] prefix) {
        if (target.length > prefix.length) return false;
        for (int i = 0 ; i < prefix.length ; i++) {
            if (prefix[i] != target[i]) return false;
        }
        return true;
    }

    public static int findCommonPrefixLength(byte[] b1, byte[] b2) {
        int n = 0;
        int l = Math.min(b1.length, b2.length);
        while(n < l) {
            if (b1[n] != b2[n])
                break;
            n++;
        }
        return n;
    }
}
