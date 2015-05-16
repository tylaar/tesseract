package net.oscartech.tesseract.node.util;

import java.util.UUID;

/**
 * Created by tylaar on 15/5/16.
 */
public class TxIdGenerator {
    public static String generateTxId() {
        return UUID.randomUUID().toString();
    }
}
