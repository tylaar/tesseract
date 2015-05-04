package net.oscartech.tesseract.node.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * Here is protocol. The server side thread will confirm the proposal with ACK, or with the same
 * proposal id but instead with the same type message, giving the disagreement information enclosed.
 * Created by tylaar on 15/4/29.
 */

public enum NodeProposalType {
    MASTER_SELECTION(0),
    LOCK_ACQUIRE(1),
    ACK(2);

    private int code;

    private static Map<Integer, NodeProposalType> codeToType = new HashMap<>();

    static {
        for (NodeProposalType type : NodeProposalType.values()) {
            codeToType.put(type.code, type);
        }
    }

    public static NodeProposalType fromCode(int code) {
        return codeToType.get(code);
    }

    NodeProposalType(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
