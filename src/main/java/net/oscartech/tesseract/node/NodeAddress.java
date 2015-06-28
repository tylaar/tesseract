package net.oscartech.tesseract.node;

import java.net.InetSocketAddress;

/**
* Created by tylaar on 15/4/29.
*/
public class NodeAddress {
    String address;
    int port;

    public NodeAddress(final String address, final int port) {
        this.address = address;
        this.port = port;
    }

    public NodeAddress(final int port) {
        this.address = "127.0.0.1";
        this.port = port;
    }

    public InetSocketAddress toInetSocketAddress() {
        return new InetSocketAddress(address, port);
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
