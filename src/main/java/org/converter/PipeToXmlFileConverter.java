package org.converter;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.parser.XMLParser;

import java.nio.file.Files;
import java.nio.file.Path;

public class PipeToXmlFileConverter {

    private static final String FILE_PATH = "src/main/resources/";

    public static void main(String args[]) throws Exception {
        convert("ExampleOruR30Message.hl7", "ExampleOruR30MessageXml.xml");
    }

    public static void convert(String pipeFileName, String xmlFileName) throws Exception {
        PipeParser pipeParser = new PipeParser();
        XMLParser xmlParser = new DefaultXMLParser();

        String messageString = Files.readString(Path.of(FILE_PATH + pipeFileName));

        Message message = pipeParser.parse(messageString);
        String encodedMessage = xmlParser.encode(message);
        encodedMessage = encodedMessage.replaceAll("(</?)([A-Za-z_:][\\w:.-]*?)\\.([A-Za-z0-9_-]+)", "$1$2--$3");

        Files.writeString(Path.of(FILE_PATH + xmlFileName), encodedMessage);
    }
}
