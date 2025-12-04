package org.astm.v25.parser;

import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.parser.DefaultXMLParser;

public class Astm1394XmlParser extends DefaultXMLParser {

    public Astm1394XmlParser(HapiContext context) {
        super(context);
    }

    @Override
    public String getEncoding(String message) {
        try {
            if (!message.contains("H.1>")) throw new RuntimeException("Expected to find MSH.1");
            if (!message.contains("H.2>")) throw new RuntimeException("Expected to find MSH.2");
            return "XML";
        } catch (Exception e) {
            return getDefaultEncoding();
        }
    }

    @Override
    public String getVersion(String message) {
        return "2.5";
    }
}
