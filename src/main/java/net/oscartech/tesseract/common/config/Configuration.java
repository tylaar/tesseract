package net.oscartech.tesseract.common.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tylaar on 15/5/17.
 */
public class Configuration {
    public static Map<String, Map<String, String>> configurationMap = new ConcurrentHashMap<>();

    public static Map<String, String> getComponentConfig(String componentName) {
        return configurationMap.get(componentName);
    }

    public static void putComponentConfig(String componentName, String configName, String configValue) {
        Map<String, String> componentConfig;
        if (configurationMap.containsKey(componentName)) {
            componentConfig = configurationMap.get(componentName);
        } else {
            componentConfig = new ConcurrentHashMap<>();
            configurationMap.put(componentName, componentConfig);
        }
        componentConfig.put(configName, configValue);
    }

    public static String getComponentConfigValue(String componentName, String configName) {
        if (configurationMap.containsKey(componentName)) {
            return configurationMap.get(componentName).get(configName);
        }
        return null;
    }
}
