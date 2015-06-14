package net.oscartech.tesseract.node.rpc.protocol;

import freemarker.ext.beans.HashAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tylaar on 15/6/14.
 */
public enum TessyCommandParamType {
    BOOLEAN(1, Boolean.class),
    INT(2, Integer.class),
    LONG(3, Long.class),
    FLOAT(4, Float.class),
    DOUBLE(5, Double.class),
    CHAR(6, Character.class),
    STRING(7, String.class);

    private int code;
    private Class<?> clazz;

    public static final Map<Integer, TessyCommandParamType> codeToEnumMap = new HashMap<>();
    public static final Map<Class<?>, TessyCommandParamType> clazzToEnumMap = new HashMap<>();

    static {
        for (TessyCommandParamType type : TessyCommandParamType.values()) {
            codeToEnumMap.put(type.getCode(), type);
            clazzToEnumMap.put(type.getClazz(), type);
        }
    }

    TessyCommandParamType(final int code, final Class<?> clazz) {
        this.code = code;
        this.clazz = clazz;
    }

    public static TessyCommandParamType fromCode(final int code) {
        return codeToEnumMap.get(code);
    }

    public static TessyCommandParamType fromClazz(final Class<?> clazz) {
        return clazzToEnumMap.get(clazz);
    }

    public int getCode() {
        return code;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
