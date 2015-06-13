package net.oscartech.tesseract.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.oscartech.tesseract.node.rpc.protocol.TessyCommand;
import net.oscartech.tesseract.node.rpc.protocol.TessyCommandBuilder;
import net.oscartech.tesseract.node.rpc.protocol.TessyFormat;
import org.junit.Test;

/**
 * Created by tylaar on 15/6/13.
 */
public class TessyCommandMarshallTest {
    @Test
    public void marshallingTessyCommand() throws JsonProcessingException {
        TessyCommandBuilder builder = new TessyCommandBuilder();
        builder.setCommandName("hello")
                .setCommandFormat(TessyFormat.JSON)
                .addCommandParams("one","one")
                .addCommandParams("two","two");

        TessyCommand command = builder.build();
        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writeValueAsString(command);
        System.out.println(result);
    }
}
