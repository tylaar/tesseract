package net.oscartech.tesseract.node.rpc.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tylaar on 15/6/13.
 */
public class TessyCommandBuilder {

    private TessyCommand commandToBeRendered = new TessyCommand();

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
        Map<String, String> paramMap = commandToBeRendered.getCommandParams();
        if (paramMap == null) {
            paramMap = new HashMap<>();
            this.commandToBeRendered.setCommandParams(paramMap);
        }
        if (paramMap.containsKey(paramName)) {
            throw new TessyProtocolException(TessyProtocolException.PARAM_ALREADY_EXIST, "param: " + paramName + "already exist");
        }
        paramMap.put(paramName, paramValue);
        return this;
    }

    public TessyCommand build() {
        TessyCommand result = commandToBeRendered;
        this.commandToBeRendered = null;
        return result;
    }
}
