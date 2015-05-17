package net.oscartech.tesseract.common;

import net.oscartech.tesseract.common.config.Configuration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by tylaar on 15/5/17.
 */
public class ConfigurationTest {
    @Test
    public void testConfig() {
        Configuration configuration = new Configuration("log");
        String value = configuration.getString("size");
        assertEquals("1", value);
    }
}
