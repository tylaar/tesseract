package net.oscartech.tesseract.node;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Created by tylaar on 15/4/26.
 */
public class Node {
    private volatile NodeState state;

    private Map<Long, Long> txIdToAcceptId = new ConcurrentHashMap<>();

    class NodeState implements Comparable<NodeState> {

        private final Integer code;

        NodeState(final Integer code) {
            this.code = code;
        }

        @Override
        public int compareTo(final NodeState o) {
            return this.code.compareTo(o.code);
        }
    }

    private final NodeState INITAL = new NodeState(0);
    private final NodeState MASTER_CHOOSING = new NodeState(1);
    private final NodeState IDLE = new NodeState(2);
    private final NodeState ACCEPT_PROPOSE = new NodeState(3);
    private final NodeState PRE_COMMIT = new NodeState(4);

    private AtomicReferenceFieldUpdater<Node, NodeState> changer = AtomicReferenceFieldUpdater.newUpdater(
            Node.class,
            NodeState.class,
            "state"
    );

    public Node() {
    }

    private boolean changeToStart() {
        return changer.compareAndSet(this, INITAL, MASTER_CHOOSING);
    }

    private boolean changeToAcceptProposal() {
        return changer.compareAndSet(this, MASTER_CHOOSING, ACCEPT_PROPOSE);
    }

    private boolean changeToPreCommit(long txId) {
        if (changer.get(this).equals(ACCEPT_PROPOSE)) {
            changer.compareAndSet(this, ACCEPT_PROPOSE, PRE_COMMIT);
        }
        throw new RuntimeException("not a good time to pre commit");
    }

    private synchronized long submitNewProposal(long txId, long version) {
        long currentVersionSnapshot = txIdToAcceptId.get(txId);
        if (txIdToAcceptId.get(txId) > version) {
            return currentVersionSnapshot;
        }
        return version;
    }

    private boolean acceptNewProposal(long txId, long version) {
        /**
         * Try to see if this version is currently supported version.
         */
        changeToPreCommit(txId);
        return true;
    }


    private boolean changeBackToAccepProposal() {
        return changer.compareAndSet(this, PRE_COMMIT, ACCEPT_PROPOSE);
    }
    private boolean changeBackToMasterSelection() {
        if (changer.compareAndSet(this, ACCEPT_PROPOSE, MASTER_CHOOSING)) {
            return true;
        }
        throw new RuntimeException("this is not a good time to accept");
    }

    public void setCurrentAcceptId(final long txId, final long currentAcceptId) {
        this.txIdToAcceptId.put(txId, currentAcceptId);
    }

    public boolean canAcceptProposal() {
        return changer.get(this).equals(ACCEPT_PROPOSE);
    }

    public boolean canAcceptMasterSelection() {
        return changer.get(this).equals(INITAL);
    }

    public long tryToAcceptNewProposal(long txId, long proposalId) {
        return submitNewProposal(txId,proposalId);
    }

}
