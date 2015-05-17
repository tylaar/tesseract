package net.oscartech.tesseract.node.log;

import net.oscartech.tesseract.common.util.Check;

/**
 * Created by tylaar on 15/5/17.
 */
public abstract class AbstractLoggable implements Loggable {
    protected void checkIfIsOpen() {
        Check.state(isOpen(), "The log is not currently open");
    }

    protected void checkIfIsNotOpen() {
        Check.state(!isOpen(), "The log is currently opened.");
    }

    protected void checkContainsIndex(long index) {
        Check.index(index, containsIndex(index), "Log doesn't contains index %d", index);
    }
}

