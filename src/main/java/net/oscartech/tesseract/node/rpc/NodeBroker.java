package net.oscartech.tesseract.node.rpc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import net.oscartech.tesseract.node.NodeAddress;
import net.oscartech.tesseract.node.rpc.aop.RpcServiceProcessor;
import net.oscartech.tesseract.node.util.JsonObjectDecoder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by tylaar on 15/6/28.
 */
public class NodeBroker {

    private ThreadPoolExecutor serverExecutor;
    private ServerBootstrap serverBootstrap;
    private NodeAddress nodeAddress;
    private RpcServiceProcessor serviceProcessor;

    public NodeBroker(final NodeAddress nodeAddress,
                      final RpcServiceProcessor serviceProcessor) {
        this.nodeAddress = nodeAddress;

        this.serverBootstrap = getServerBootStrap();
        this.serviceProcessor = serviceProcessor;
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

    public void init() {
        serverExecutor = new ThreadPoolExecutor(4, 8, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        serverExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ChannelFuture f = serverBootstrap.bind(nodeAddress.toInetSocketAddress()); // (7)
                f.channel().closeFuture();
            }
        });
        System.out.println("initialized.");
    }

}
