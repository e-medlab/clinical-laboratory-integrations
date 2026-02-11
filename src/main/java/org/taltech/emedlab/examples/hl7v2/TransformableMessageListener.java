package org.taltech.emedlab.examples.hl7v2;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.*;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import org.taltech.emedlab.infra.fhir.AbstractTransformer;
import org.taltech.emedlab.infra.parsers.TransformableXmlParser;
import org.taltech.emedlab.infra.fhir.TermXTransformer;

import java.io.IOException;
import java.util.Map;

/**
 * This is a standard HL7 v2 listener that upon receiving a message, outputs it
 * in a custom XML format that is structured for the ability of
 * FHIR Mapping Language transformations.
 *
 * The converter being used is
 */
public class TransformableMessageListener {
    // configuration parameters
    // the real end-product should have these specifiable on launch
    private static final int PORT_NUMBER = 56420;
    private static final boolean USE_TLS = false;

    // In HAPI, almost all things revolve around a context object
    private static HapiContext context = new DefaultHapiContext();

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        HL7Service ourHl7Server = context.newServer(PORT_NUMBER, USE_TLS);

        // route all messages to this application
        ourHl7Server.registerApplication("*", "*", new TransformableMessageParserApplication());

        //register a connection listener for traffic monitoring
        ourHl7Server.registerConnectionListener(new OurConnectionListener());

        System.out.println("Starting a HL7 server listening on port " + PORT_NUMBER);
        ourHl7Server.start();
    }
}

class TransformableMessageParserApplication implements ReceivingApplication {

    private static HapiContext context = new DefaultHapiContext();
    private static AbstractTransformer transformer = new TermXTransformer();

    @Override
    public Message processMessage(Message message, Map map) throws HL7Exception {
        try {
            TransformableXmlParser xmlParser = new TransformableXmlParser();
            String xml = xmlParser.parse(message);

            // The end goal:
            // Instead of printing, log the message and forward to the next component queue
            System.out.println("---- Incoming HL7 Message ----");
            System.out.println(xml);
            System.out.println("------------------------------");

            // For now, as an example, let's take the parsed component, call a transformation on it
            String transformedXml = transformer.fromOruR30ToFhirBundle(xml);
            System.out.println("---- Transformed HL7 Message as FHIR ----");
            System.out.println(transformedXml);
            System.out.println("------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            return message.generateACK();
        } catch (IOException e) {
            throw new HL7Exception(e);
        }
    }

    @Override
    public boolean canProcess(Message message) {
        return true; // accept all messages
    }
}

class OurConnectionListener implements ConnectionListener {

    @Override
    public void connectionDiscarded(Connection connectionBeingDiscarded) {
        System.out.println("Connection discarded event fired " + connectionBeingDiscarded.getRemoteAddress());
        System.out.println("For Remote Address: " + connectionBeingDiscarded.getRemoteAddress());
        System.out.println("For Remote Port: " + connectionBeingDiscarded.getRemotePort());
    }

    @Override
    public void connectionReceived(Connection connectionBeingOpened) {
        System.out.println("Connection opened event fired " + connectionBeingOpened.getRemoteAddress());
        System.out.println("From Remote Address: " + connectionBeingOpened.getRemoteAddress());
        System.out.println("From Remote Port: " + connectionBeingOpened.getRemotePort());
    }

}
