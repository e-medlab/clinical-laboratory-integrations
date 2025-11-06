package org.sender;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import org.converter.TransformableXmlParser;
import org.transformer.TermXTransformer;

import java.nio.file.Files;
import java.nio.file.Path;

public class TransformableMessageSender {

    private static final int PORT_NUMBER = 52463;

    private static HapiContext context = new DefaultHapiContext();
    private static TermXTransformer transformer = new TermXTransformer();

    public static void main(String[] args) throws Exception {
        String fhirMessage = Files.readString(Path.of("src/main/resources/ExampleOmlO21MessageAsFhir.xml"));

        String v2MessageString = transformer.fromFhirBundleToOmlO21(fhirMessage);

        TransformableXmlParser parser = new TransformableXmlParser();
        Message v2Message = parser.parse(v2MessageString);

        send(v2Message);
    }

    private static void send(Message message) throws Exception {
        Connection connection = context.newClient("localhost", PORT_NUMBER, false);
        Initiator initiator = connection.getInitiator();

        Parser parser = context.getPipeParser();
        System.out.println("Sending message:" + "\n" + parser.encode(message));
        Message response = initiator.sendAndReceive(message);

        String responseString = parser.encode(response);
        System.out.println("Received response:\n" + responseString);
    }
}
