package org.taltech.emedlab.infra.parsers;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.*;

public class TransformableXmlParser {
    private final HapiContext context;
    private final String DELIMITER = "--";
    private final String TARGET_NAMESPACE = "http://hl7.org/fhir";

    public TransformableXmlParser() {
        this.context = new DefaultHapiContext();
    }

    public TransformableXmlParser(HapiContext context) {
        this.context = context;
    }

    public String parse(Message message) throws Exception {
        XMLParser xmlParser = context.getXMLParser();
        String encodedMessage = xmlParser.encode(message);
        encodedMessage = encodedMessage.replaceAll("(</?)([A-Za-z_:][\\w:.-]*?)\\.([A-Za-z0-9_-]+)", "$1$2" + DELIMITER + "$3");
        encodedMessage = encodedMessage.replace("urn:hl7-org:v2xml", TARGET_NAMESPACE);
        return encodedMessage;
    }

    public Message parse(String message) throws Exception {
        XMLParser xmlParser = context.getXMLParser();
        String decodedMessage = message.replace(TARGET_NAMESPACE, "urn:hl7-org:v2xml");
        decodedMessage = decodedMessage.replaceAll("(</?)([A-Za-z_:][\\w:.-]*?)" + DELIMITER + "([A-Za-z0-9_-]+)", "$1$2.$3");
        return xmlParser.parse(decodedMessage);
    }

    public String parseAstm(Message message) throws Exception {
        XMLParser xmlParser = new Astm1394XmlParser(context);
        String encodedMessage = xmlParser.encode(message);
        encodedMessage = encodedMessage.replaceAll("(</?)([A-Za-z_:][\\w:.-]*?)\\.([A-Za-z0-9_-]+)", "$1$2" + DELIMITER + "$3");
        encodedMessage = encodedMessage.replace("urn:hl7-org:v2xml", "http://www.astm.org/standards/V2.5A");
        return encodedMessage;
    }

    public Message parseAstm(String message) throws Exception {
        XMLParser xmlParser = new Astm1394XmlParser(context);
        String decodedMessage = message.replace(TARGET_NAMESPACE, "urn:hl7-org:v2xml");
        decodedMessage = decodedMessage.replaceAll("(</?)([A-Za-z_:][\\w:.-]*?)" + DELIMITER + "([A-Za-z0-9_-]+)", "$1$2.$3");
        return xmlParser.parse(decodedMessage);
    }
}

