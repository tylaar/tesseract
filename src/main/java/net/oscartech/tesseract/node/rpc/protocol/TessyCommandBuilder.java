package net.oscartech.tesseract.node.rpc.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tylaar on 15/6/13.
 */
public class TessyCommandBuilder {

    private TessyCommand commandToBeRendered = new TessyCommand();
    private AtomicInteger i = new AtomicInteger(0);

    public static TessyCommandBuilder newTessyCommand() {
        return new TessyCommandBuilder();
    }

    public TessyCommandBuilder setServiceName(String serviceName) {
        this.commandToBeRendered.setServiceName(serviceName);
        return this;
    }

    public TessyCommandBuilder setCommandName(String name) {
        this.commandToBeRendered.setCommandName(name);
        return this;
    }

    public TessyCommandBuilder setCommandFormat(TessyFormat format) {
        this.commandToBeRendered.setCommandFormat(format);
        return this;
    }

    public TessyCommandBuilder addCommandParams(String paramName, String paramValue) {
        List<TessyCommandParam> paramList = commandToBeRendered.getCommandParams();
        if (paramList == null) {
            paramList = new ArrayList<>();
            this.commandToBeRendered.setCommandParams(paramList);
        }
        if (paramsContainKey(paramList, paramName)) {
            throw new TessyProtocolException(TessyProtocolException.PARAM_ALREADY_EXIST, "param: " + paramName + "already exist");
        }
        paramList.add(new TessyCommandParam(i.getAndIncrement(), paramName, paramValue));
        return this;
    }

    private boolean paramsContainKey(final List<TessyCommandParam> paramList, final String paramName) {
        for (TessyCommandParam param : paramList) {
            if (param.getParameterName().equals(paramName))
                return true;
        }
        return false;
    }

    public TessyCommand build() {
        TessyCommand result = commandToBeRendered;
        this.commandToBeRendered = null;
        return result;
    }
}
