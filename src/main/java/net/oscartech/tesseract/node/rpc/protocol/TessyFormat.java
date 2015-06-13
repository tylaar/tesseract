package net.oscartech.tesseract.node.rpc.protocol;

import java.util.HashMap;

/**
 * Created by tylaar on 15/6/13.
 */
public enum TessyFormat {

    PLAIN(0),
    JSON(1);

    private int code;
    private static final HashMap<Integer, TessyFormat> codeMapping = new HashMap<>();

    static {
        for (TessyFormat format : TessyFormat.values()) {
            codeMapping.put(format.code(), format);
        }
    }

    TessyFormat(final int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public TessyFormat fromCode(TessyFormat format) {
        return codeMapping.get(format);
    }
}
