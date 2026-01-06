package org.taltech.emedlab.examples.astm;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.taltech.emedlab.infra.parsers.Astm1394PipeParser;
import org.taltech.emedlab.infra.parsers.AstmModelClassFactory;
import org.taltech.emedlab.infra.tcp.AstmTcpClient;
import org.taltech.emedlab.infra.parsers.TransformableXmlParser;
import org.taltech.emedlab.infra.termx.TermXTransformer;

import java.nio.file.Files;
import java.nio.file.Path;

public class AstmMessageSender {

    private static final String HOST = "192.168.53.22";
    private static final int PORT_NUMBER = 49999;

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
        Astm1394PipeParser parser = new Astm1394PipeParser(context);
        String encodedMessage = parser.encode(message);

        AstmTcpClient client = new AstmTcpClient(HOST, PORT_NUMBER);

        String response = client.send(encodedMessage);

        System.out.println(response);
        Thread.sleep(50000);
        client.close();
    }
}
