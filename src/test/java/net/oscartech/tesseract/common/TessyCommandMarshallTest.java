package net.oscartech.tesseract.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.oscartech.tesseract.node.rpc.aop.RpcSampleService;
import net.oscartech.tesseract.node.rpc.aop.RpcServiceProcessor;
import net.oscartech.tesseract.node.rpc.protocol.TessyCommand;
import net.oscartech.tesseract.node.rpc.protocol.TessyCommandBuilder;
import net.oscartech.tesseract.node.rpc.protocol.TessyFormat;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.io.IOException;

/**
 * Created by tylaar on 15/6/13.
 */
@ContextConfiguration(locations = "file:src/main/META-INF/spring/unittest/application.xml")
public class TessyCommandMarshallTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    private RpcSampleService sampleService;
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
        RpcServiceProcessor processor = new RpcServiceProcessor();
        processor.scanAnnotation("net.oscartech.tesseract");
    }

    @Test
    public void testCalling() {
        RpcServiceProcessor processor = new RpcServiceProcessor();
        processor.scanAnnotation("net.oscartech.tesseract");
        TessyCommandBuilder builder = new TessyCommandBuilder();
        builder.setServiceName("sampleService").
                setCommandName("command")
                .setCommandFormat(TessyFormat.JSON)
                .addCommandParams("one")
                .addCommandParams("two");

        TessyCommand command = builder.build();
        processor.callMethod(command);
    }
}
