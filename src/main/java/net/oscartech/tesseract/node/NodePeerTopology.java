package net.oscartech.tesseract.node;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tylaar on 15/4/29.
 */
public class NodePeerTopology {
    private List<NodeAddress> networkTopology = new ArrayList<>();
    private NodeAddress localAddress;
    private List<Channel> peerHostChannels = new ArrayList<>();
    private AtomicInteger peerNumber = new AtomicInteger(0);
    private AtomicInteger readyNodeNumber = new AtomicInteger(0);

    public NodePeerTopology(final List<NodeAddress> networkTopology, final NodeAddress localAddress) {
        this.networkTopology = excludeSelf(networkTopology, localAddress);
        this.localAddress = localAddress;
    }

    private List<NodeAddress> excludeSelf(final List<NodeAddress> networkTopology, final NodeAddress localAddress) {
        List<NodeAddress> selfExcluded = new ArrayList<>();
        for (NodeAddress host : networkTopology) {
            if (localAddress.getPort() == host.getPort()) {
                continue;
            }
            selfExcluded.add(host);
        }
        return selfExcluded;
    }

    public List<NodeAddress> getNetworkTopology() {
        return networkTopology;
    }

    public boolean isNetworkInitialized() {
        return peerNumber.get() == readyNodeNumber.get();
    }

    public List<Channel> getPeerHostChannels() {
        return peerHostChannels;
    }

    public NodeAddress getLocalAddress() {
        return localAddress;
    }

    public void addPeerHostChannel(Channel channel) {
        peerHostChannels.add(channel);
    }

    public void increasePeerNumber() {
        this.peerNumber.getAndIncrement();
    }

    public void increaseReadyPeerNumber() {
        this.readyNodeNumber.getAndIncrement();
    }
}
