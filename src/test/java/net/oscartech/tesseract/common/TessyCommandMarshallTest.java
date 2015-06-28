package net.oscartech.tesseract.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import net.oscartech.tesseract.node.NodeClientInboundHandler;
import net.oscartech.tesseract.node.NodeServiceFactory;
import net.oscartech.tesseract.node.rpc.NodeBroker;
import net.oscartech.tesseract.node.rpc.aop.RpcSampleService;
import net.oscartech.tesseract.node.rpc.aop.RpcServiceProcessor;
import net.oscartech.tesseract.node.rpc.protocol.TessyCommand;
import net.oscartech.tesseract.node.rpc.protocol.TessyCommandBuilder;
import net.oscartech.tesseract.node.rpc.protocol.TessyFormat;
import net.oscartech.tesseract.node.util.JsonObjectDecoder;
import net.oscartech.tesseract.node.util.MarshallUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.channels.Channel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by tylaar on 15/6/13.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/META-INF/spring/unittest/application.xml")
public class TessyCommandMarshallTest {

    @Autowired
    private RpcServiceProcessor processor;

    @Test
    public void marshallingTessyCommand() throws JsonProcessingException {
        TessyCommandBuilder builder = new TessyCommandBuilder();
        builder.setCommandName("hello")
                .setCommandFormat(TessyFormat.JSON)
                .addCommandParams(1l)
                .addCommandParams("two");

        TessyCommand command = builder.build();
        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writeValueAsString(command);
        System.out.println(result);
    }


    @Test
    public void testAnnotationRefine() throws IOException {
        processor.scanAnnotation("net.oscartech.tesseract");
    }

    @Test
    public void testCalling() {
        processor.scanAnnotation("net.oscartech.tesseract");
        TessyCommand command = getCommand();
        processor.callMethod(command);
    }

    private TessyCommand getCommand() {
        TessyCommandBuilder builder = new TessyCommandBuilder();
        builder.setServiceName("sampleService").
                setCommandName("command")
                .setCommandFormat(TessyFormat.JSON)
                .addCommandParams("one")
                .addCommandParams("two");

        return builder.build();
    }

    @Test
    public void testRealCalling() throws InterruptedException {

        NodeBroker broker = new NodeBroker(NodeServiceFactory.generateLocalNodeAddress(), processor);
        broker.init();

        Bootstrap b = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();

        b.group(group).channel(NioSocketChannel.class)
                .remoteAddress(NodeServiceFactory.generateLocalNodeAddress().toInetSocketAddress())
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new JsonObjectDecoder());
                        ch.pipeline().addLast(new StringEncoder());
                    }
                });

        ChannelFuture f;
        try {
            f = b.connect().sync();

            /**
             * Before the node clients are all connected, the proposal shall not happen.
             */

            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(final ChannelFuture future) throws Exception {
                    System.out.println("connected");
                    io.netty.channel.Channel channel = future.channel();
                    if (!channel.isWritable()) {
                        System.out.println("unwritable!!");
                    }

                    channel.writeAndFlush(MarshallUtils.fromTessyCommandToString(getCommand()));
                }
            });

            f.channel().closeFuture();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread.sleep(2000);
        while(true) {
            Thread.sleep(2000);
            Thread.yield();
        }

    }
}
