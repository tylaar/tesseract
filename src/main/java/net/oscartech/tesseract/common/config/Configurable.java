package net.oscartech.tesseract.common.config;

import net.oscartech.tesseract.exception.TessyException;

import java.util.Map;

/**
 * If you need to configure something, you need to implement this interface.
 * Created by tylaar on 15/5/17.
 */
public abstract class Configurable {

    private String componentName;

    public Configurable(final String componentName) {
        this.componentName = componentName;
    }

    public abstract void putConfiguration();

    public Map<String, String> getConfiguration() {
        if (componentName == null) {
            throw new TessyException("component name must be present.");
        }
        return Configuration.getComponentConfig(this.componentName);
    }

}
