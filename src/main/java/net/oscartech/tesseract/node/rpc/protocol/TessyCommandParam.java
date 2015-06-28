package net.oscartech.tesseract.node.rpc.protocol;

import java.util.Objects;

/**
 * Created by tylaar on 15/6/14.
 */
public class TessyCommandParam {
    private int index;
    private int type;
    private Object value;

    public TessyCommandParam() {
    }

    public TessyCommandParam(final int index, final int type, final Object value) {
        this.index = index;
        this.type = type;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public int getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setIndex(final int index) {
        this.index = index;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof TessyCommandParam)) return false;
        final TessyCommandParam param = (TessyCommandParam) o;
        return Objects.equals(getIndex(), param.getIndex()) &&
                Objects.equals(getType(), param.getType()) &&
                Objects.equals(getValue(), param.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIndex(), getType(), getValue());
    }
}
