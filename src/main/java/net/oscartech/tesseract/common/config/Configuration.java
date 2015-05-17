package net.oscartech.tesseract.common.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tylaar on 15/5/17.
 */
public class Configuration {

    public Config config = ConfigFactory.load();
    public String configPrefix = null;

    public Configuration(final String configPrefix) {
        this.configPrefix = configPrefix;
    }

    public void putComponentConfig(String componentName, String configName, String configValue) {

    }

    private String getComponentConfigValue(String configName) {
        if (config != null) {
            return config.getString(configPrefix + "." + configName);
        }
        return null;
    }

    public String getString(String configName) {
        return getComponentConfigValue(configName);
    }

    public long getLong(String configName) {
        return Long.valueOf(getComponentConfigValue(configName));
    }

    public boolean getBoolean(String configName) {
        return Boolean.valueOf(getComponentConfigValue(configName));
    }

    public int getInt(final String configName) {
        return Integer.valueOf(getComponentConfigValue(configName));
    }
}
