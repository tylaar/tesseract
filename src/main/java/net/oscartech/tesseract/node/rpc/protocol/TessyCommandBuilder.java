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

    public TessyCommandBuilder addCommandParams(Object paramValue) {
        List<TessyCommandParam> paramList = commandToBeRendered.getCommandParams();
        if (paramList == null) {
            paramList = new ArrayList<>();
            this.commandToBeRendered.setCommandParams(paramList);
        }
        int type = TessyCommandParamType.fromClazz(paramValue.getClass()).getCode();
        paramList.add(new TessyCommandParam(i.getAndIncrement(), type, paramValue));
        return this;
    }


    public TessyCommand build() {
        TessyCommand result = commandToBeRendered;
        this.commandToBeRendered = null;
        return result;
    }
}
