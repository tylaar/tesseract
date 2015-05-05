package net.oscartech.tesseract.node;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import net.oscartech.tesseract.node.exception.NodeProcessException;
import net.oscartech.tesseract.node.rpc.aop.RpcMethod;
import net.oscartech.tesseract.node.rpc.aop.RpcService;
import net.oscartech.tesseract.node.util.JsonObjectDecoder;
import net.oscartech.tesseract.node.util.SequenceGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by tylaar on 15/4/26.
 */
@RpcService(name = "nodeService")
public class NodeService {
    private static final String ADDR_DELIMER = ":";
    private Node node;
    private boolean isMaster = false;
    private NodeProposalBroker proposalBroker;
    private ThreadPoolExecutor serverExecutor;
    private ThreadPoolExecutor clientExecutor;
    private ServerBootstrap serverBootstrap;
    private List<Bootstrap> clientBootstrap;
    private NodePeerTopology peerTopology;

    public NodeService(final String address, final int port, final List<String> networkTopology, final SequenceGenerator sequenceGenerator) {
        this.node = new Node();
        this.peerTopology = new NodePeerTopology(parseFromConfig(networkTopology), new NodeAddress(address, port));
        this.node.setCurrentAcceptId(0, sequenceGenerator.generateSequence());

        this.serverBootstrap = getServerBootStrap();
        this.clientBootstrap = getClientBootStrap();
        this.proposalBroker = new NodeProposalBroker(node, peerTopology, new NodeProposalVerbConstructor(sequenceGenerator));
    }

    /**
     * This is default version, for launching a local server binding to default local 127.0.0.1 address.
     * @param port
     * @param networkTopology
     * @param sequenceGenerator
     */
    public NodeService(final int port, final List<String> networkTopology, final SequenceGenerator sequenceGenerator) {
        this.node = new Node();
        this.peerTopology = new NodePeerTopology(parseFromConfig(networkTopology), new NodeAddress(port));
        this.node.setCurrentAcceptId(0l, sequenceGenerator.generateSequence());

        this.serverBootstrap = getServerBootStrap();
        this.clientBootstrap = getClientBootStrap();
        this.proposalBroker = new NodeProposalBroker(node, peerTopology, new NodeProposalVerbConstructor(sequenceGenerator));
    }


    private List<NodeAddress> parseFromConfig(final List<String> networkTopology) {
        List<NodeAddress> resultSet = new ArrayList<>();
        for (String addr : networkTopology) {
            String[] pair = addr.split(ADDR_DELIMER);
            resultSet.add(new NodeAddress(pair[0], Integer.valueOf(pair[1])));
        }
        return resultSet;
    }

    @RpcMethod(name = "test")
    private ServerBootstrap getServerBootStrap() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class) // (3)
                .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {

                        ch.pipeline().addLast(new NodeServerHandler(proposalBroker));
                        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));

                        ch.pipeline().addLast(new JsonObjectDecoder());
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

        return b;
    }

    private List<Bootstrap> getClientBootStrap() {
        List<Bootstrap> clientSet = new ArrayList<>();
        for (NodeAddress otherHost : peerTopology.getNetworkTopology()) {

            EventLoopGroup group = new NioEventLoopGroup();

            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .remoteAddress(otherHost.toInetSocketAddress())
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NodeClientInboundHandler());

                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                        }
                    });
            clientSet.add(b);
            peerTopology.increasePeerNumber();
        }
        return clientSet;

    }

    public void init() {
        serverExecutor = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        clientExecutor = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        serverExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ChannelFuture f = serverBootstrap.bind(peerTopology.getLocalAddress().toInetSocketAddress()); // (7)

                f.channel().closeFuture();
            }
        });
        for (final Bootstrap clientBootStrap : clientBootstrap) {
            clientExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    ChannelFuture f;
                    try {
                        f = clientBootStrap.connect().sync();
                        /**
                         * Once the connection successed, add the hosts to the map.
                         */
                        peerTopology.addPeerHostChannel(f.channel());
                        /**
                         * Before the node clients are all connected, the proposal shall not happen.
                         */

                        f.addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(final ChannelFuture future) throws Exception {
                                peerTopology.increaseReadyPeerNumber();
                            }
                        });

                        f.channel().closeFuture();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void sendMasterProposal() {
        try {
            proposalBroker.sendMasterProposal();
        } catch (IOException e) {
            throw new NodeProcessException("master selection proposal issue: ", e);
        }
    }

    private boolean isMaster() {
        System.out.println("setting me to be the master");
        return isMaster;
    }

    public void setMaster(final boolean isMaster) {
        this.isMaster = isMaster;
    }

    public Node getNode() {
        return node;
    }

}
