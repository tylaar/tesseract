package net.oscartech.tesseract.node.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import net.oscartech.tesseract.node.Node;
import net.oscartech.tesseract.node.NodeAddress;
import net.oscartech.tesseract.node.rpc.aop.RpcServiceProcessor;
import net.oscartech.tesseract.node.util.JsonObjectDecoder;
import net.oscartech.tesseract.node.util.SequenceGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by tylaar on 15/6/28.
 */
public class NodeBroker {
    private static final String ADDR_DELIMER = ":";
    private Node node;
    private boolean isMaster = false;
    private ThreadPoolExecutor serverExecutor;
    private ThreadPoolExecutor clientExecutor;
    private ServerBootstrap serverBootstrap;
    private List<Bootstrap> clientBootstrap;
    private NodeAddress nodeAddress;
    private RpcServiceProcessor serviceProcessor;

    public NodeBroker(final String address,
                      final int port,
                      final NodeAddress nodeAddress,
                      final SequenceGenerator sequenceGenerator,
                      final RpcServiceProcessor serviceProcessor) {
        this.node = new Node();
        this.nodeAddress = nodeAddress;
        this.node.setCurrentAcceptId("0", sequenceGenerator.generateSequence());

        this.serverBootstrap = getServerBootStrap();
        this.serviceProcessor = serviceProcessor;
    }

    public NodeBroker(final int port,
                      final NodeAddress nodeAddress,
                      final SequenceGenerator sequenceGenerator) {
        this.node = new Node();
        this.node.setCurrentAcceptId("0", sequenceGenerator.generateSequence());
        this.nodeAddress = nodeAddress;
        this.serverBootstrap = getServerBootStrap();
    }


    private List<NodeAddress> parseFromConfig(final List<String> networkTopology) {
        List<NodeAddress> resultSet = new ArrayList<>();
        for (String addr : networkTopology) {
            String[] pair = addr.split(ADDR_DELIMER);
            resultSet.add(new NodeAddress(pair[0], Integer.valueOf(pair[1])));
        }
        return resultSet;
    }


    private ServerBootstrap getServerBootStrap() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class) // (3)
                .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NodeServiceCallHandler(serviceProcessor));
                        ch.pipeline().addLast(new JsonObjectDecoder());
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

        return b;
    }

}
