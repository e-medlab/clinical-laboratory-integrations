package org.sender;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.apache.commons.lang3.NotImplementedException;
import org.astm.v25.parser.Astm1394PipeParser;
import org.astm.v25.parser.AstmModelClassFactory;
import org.converter.TransformableXmlParser;
import org.transformer.TermXTransformer;

import java.nio.file.Files;
import java.nio.file.Path;

public class AstmMessageSender {

    private static final int PORT_NUMBER = 52463;

    private static HapiContext context = new DefaultHapiContext();
    private static TermXTransformer transformer = new TermXTransformer();

    public static void main(String[] args) throws Exception {
        ModelClassFactory customModelClassFactory = new AstmModelClassFactory();
        context.setModelClassFactory(customModelClassFactory);
        context.getParserConfiguration().setValidating(false);

        String fhirMessage = Files.readString(Path.of("src/test/resources/ExampleAstmAllOrdersResponseFromLisAsFhir.xml"));

        String astmMessageString = transformer.fromFhirBundleToAstmMsgAllOrders(fhirMessage);

        TransformableXmlParser parser = new TransformableXmlParser(context);
        Message astmMessage = parser.parseAstm(astmMessageString);

        send(astmMessage);
    }

    private static void send(Message message) throws Exception {
        // TODO
        Astm1394PipeParser parser = new Astm1394PipeParser(context);
        String encodedMessage = parser.encode(message);

        throw new NotImplementedException();
    }
}
