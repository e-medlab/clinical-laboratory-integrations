package org.converter;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.parser.XMLParser;

import java.nio.file.Files;
import java.nio.file.Path;

public class TransformableXmlParser {
    private final String DELIMITER = "--";
    private final String TARGET_NAMESPACE = "http://hl7.org/fhir";

    public String parse(Message message) throws Exception {
        XMLParser xmlParser = new DefaultXMLParser();
        String encodedMessage = xmlParser.encode(message);
        encodedMessage = encodedMessage.replaceAll("(</?)([A-Za-z_:][\\w:.-]*?)\\.([A-Za-z0-9_-]+)", "$1$2" + DELIMITER + "$3");
        encodedMessage = encodedMessage.replace("urn:hl7-org:v2xml", TARGET_NAMESPACE);
        return encodedMessage;
    }

    public Message parse(String message) throws Exception {
        XMLParser xmlParser = new DefaultXMLParser();
        String decodedMessage = message.replace(TARGET_NAMESPACE, "urn:hl7-org:v2xml");
        decodedMessage = decodedMessage.replaceAll("(</?)([A-Za-z_:][\\w:.-]*?)" + DELIMITER + "([A-Za-z0-9_-]+)", "$1$2.$3");
        return xmlParser.parse(decodedMessage);
    }
}

class Example {

    private static final String FILE_PATH = "src/main/resources/";

    public static void main(String args[]) throws Exception {
        //convertMessage("ExampleOruR30Message");
        convertMessage("ExampleOmlO21Message");
    }

    private static void convertMessage(String fileName) throws Exception {
        PipeParser pipeParser = new PipeParser();
        TransformableXmlParser xmlParser = new TransformableXmlParser();

        String messageString = Files.readString(Path.of(FILE_PATH + fileName + ".hl7"));

        Message message = pipeParser.parse(messageString);
        String encodedMessage = xmlParser.parse(message);

        Files.writeString(Path.of(FILE_PATH + fileName + "Xml.xml"), encodedMessage);
    }

}