package org.astm.v25;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.parser.Parser;
import org.astm.v25.parser.Astm1394PipeParser;
import org.astm.v25.parser.AstmModelClassFactory;
import org.converter.TransformableXmlParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ASTM_MSGTests {

    private static final HapiContext context = new DefaultHapiContext();
    private static final String RESOURCES_PATH = "src/test/resources/";

    @BeforeAll
    static void setup() {
        ModelClassFactory customModelClassFactory = new AstmModelClassFactory();
        context.setModelClassFactory(customModelClassFactory);

        // TODO: I need a custom validator instead of disabling one
        context.getParserConfiguration().setValidating(false);
    }

    @Test
    void ingestAstmAllOrdersQueryToLis() throws Exception {
        Path path = Path.of(RESOURCES_PATH + "ExampleAstmAllOrdersQueryToLis.txt");
        Message message = testAstmMsgParsing(path);
        testAstmMsgXmlParsing(message);
    }

    @Test
    void ingestAstmAllOrdersResponseFromLis() throws Exception {
        Path path = Path.of(RESOURCES_PATH + "ExampleAstmAllOrdersResponseFromLis.txt");
        Message message = testAstmMsgParsing(path);
        testAstmMsgXmlParsing(message);
    }

    private Message testAstmMsgParsing(Path messageFilePath) throws IOException, HL7Exception {
        Parser parser = new Astm1394PipeParser(context);

        String messageString = Files.readString(messageFilePath);
        Message message = parser.parse(messageString);

        Assertions.assertNotNull(message);

        return message;
    }

    private void testAstmMsgXmlParsing(Message message) throws Exception {
        TransformableXmlParser xmlParser = new TransformableXmlParser(context);
        String xmlString = xmlParser.parse(message);
        Assertions.assertNotNull(xmlString);
    }
}
