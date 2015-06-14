package net.oscartech.tesseract.node.rpc.protocol;

import java.util.List;
import java.util.Map;

/**
 * Created by tylaar on 15/6/13.
 */
public class TessyCommand {
    private String serviceName;
    private String commandName;
    private TessyFormat commandFormat;
    private Map<String, String> commandParams;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(final String commandName) {
        this.commandName = commandName;
    }

    public TessyFormat getCommandFormat() {
        return commandFormat;
    }

    public void setCommandFormat(final TessyFormat commandFormat) {
        this.commandFormat = commandFormat;
    }

    public Map<String, String> getCommandParams() {
        return commandParams;
    }

    public void setCommandParams(final Map<String, String> commandParams) {
        this.commandParams = commandParams;
    }
}
