package net.oscartech.tesseract.common.util;

import net.oscartech.tesseract.node.exception.ConfigurationException;

/**
 * Created by tylaar on 15/5/17.
 */
public class Check {

    private Check() {}

    /**
     * Validates that a entry is not null.
     *
     * @param value The entry to validate.
     * @throws NullPointerException if {@code entry} is null
     */
    public static <T> T isNotNull(T value, String parameterName) {
        if (value == null) {
            throw new NullPointerException(String.format("%s cannot be null", parameterName));
        }
        return value;
    }

    /**
     * Validates that a entry is null.
     *
     * @param value The entry to validate.
     * @param message The exception message.
     * @param args A list of message string formatting arguments.
     * @throws NullPointerException if {@code entry} is null
     */
    public static <T> T isNull(T value, String message, Object... args) {
        if (value != null) {
            throw new NullPointerException(String.format(message, args));
        }
        return value;
    }

    /**
     * Validates that a state applies.
     *
     * @param state The state to assert.
     * @param message The failure exception message.
     * @param args A list of message string formatting arguments.
     * @throws IllegalStateException if {@code state} is not true
     */
    public static void state(boolean state, String message, Object... args) {
        if (!state) {
            throw new IllegalStateException(String.format(message, args));
        }
    }

    /**
     * Validates that a configuration condition applies.
     *
     * @param value The resulting value to passthrough
     * @param condition The condition to assert.
     * @param message The failure exception message.
     * @param args A list of message string formatting arguments.
     * @throws IllegalArgumentException if {@code condition} is not true
     */
    public static <T> T config(T value, boolean condition, String message, Object... args) {
        if (!condition) {
            throw new ConfigurationException(String.format(message, args));
        }
        return value;
    }

    /**
     * Validates that a condition applies.
     *
     * @param value The resulting entry to passthrough
     * @param condition The condition to assert.
     * @param message The failure exception message.
     * @param args A list of message string formatting arguments.
     * @throws IllegalArgumentException if {@code condition} is not true
     */
    public static <T> T arg(T value, boolean condition, String message, Object... args) {
        if (!condition) {
            throw new IllegalArgumentException(String.format(message, args));
        }
        return value;
    }

    /**
     * Validates that an index meets the given condition.
     *
     * @param value The value to validate.
     * @param condition The The condition to assert.
     * @param message The failure exception message.
     * @param args A list of message string formatting arguments.
     * @param <T> The index type.
     * @return The index.
     * @throws IllegalArgumentException if {@code condition} is not true
     */
    public static <T extends Number> T index(T value, boolean condition, String message, Object... args) {
        if (!condition) {
            throw new IndexOutOfBoundsException(String.format(message, args));
        }
        return value;
    }

}
