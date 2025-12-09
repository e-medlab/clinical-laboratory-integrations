package org.listener;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.parser.Parser;
import org.astm.v25.parser.Astm1394PipeParser;
import org.astm.v25.parser.AstmModelClassFactory;
import org.astm.v25.tcp.AstmTcpServer;
import org.converter.TransformableXmlParser;
import org.transformer.TermXTransformer;

public class AstmMessageListener {
    private static final int PORT_NUMBER = 56420;

    private static HapiContext context = new DefaultHapiContext();
    private static TermXTransformer transformer = new TermXTransformer();

    public static void main(String[] args) throws Exception {
        ModelClassFactory customModelClassFactory = new AstmModelClassFactory();
        context.setModelClassFactory(customModelClassFactory);
        context.getParserConfiguration().setValidating(false);

        run();
    }

    public static void run() throws Exception {
        AstmTcpServer server = new AstmTcpServer(PORT_NUMBER);

        System.out.println("Starting an ASTM server listening on port " + PORT_NUMBER);
        String messageString = server.waitForMessage(null);

        System.out.println("Received message: " + messageString);

        Parser parser = new Astm1394PipeParser(context);
        Message message = parser.parse(messageString);

        TransformableXmlParser xmlParser = new TransformableXmlParser(context);
        String xml = xmlParser.parse(message);

        System.out.println("---- ASTM XML Message to transform ----");
        System.out.println(xml);
        System.out.println("------------------------------");

        String transformedXml = transformer.fromAstmMsgOrderResultsToFhirBundle(xml);

        System.out.println("---- Transformed ASTM Message as FHIR ----");
        System.out.println(transformedXml);
        System.out.println("------------------------------");
    }
}
