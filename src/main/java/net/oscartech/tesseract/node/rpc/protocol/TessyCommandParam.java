package net.oscartech.tesseract.node.rpc.protocol;

import java.util.Objects;

/**
 * Created by tylaar on 15/6/14.
 */
public class TessyCommandParam {
    private int index;
    private String parameterName;
    private String parameterValue;

    public TessyCommandParam(final int index, final String parameterName, final String parameterValue) {
        this.index = index;
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
    }

    public int getIndex() {
        return index;
    }

    public String getParameterName() {
        return parameterName;
    }


    public String getParameterValue() {
        return parameterValue;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TessyCommandParam that = (TessyCommandParam) o;
        return Objects.equals(index, that.index) &&
                Objects.equals(parameterName, that.parameterName) &&
                Objects.equals(parameterValue, that.parameterValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, parameterName, parameterValue);
    }
}
