package net.oscartech.tesseract.node.exception;

import net.oscartech.tesseract.exception.TessyException;

/**
 * Created by tylaar on 15/5/17.
 */
public class ConfigurationException extends TessyException {

    public ConfigurationException(String message, Object... args) {
        super(String.format(message, args));
    }

    public ConfigurationException(Throwable cause, String message, Object... args) {
        super(String.format(message, args), cause);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
