package org.taltech.emedlab.hl7v2;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.taltech.emedlab.infra.parsers.TransformableXmlParser;
import org.taltech.emedlab.infra.termx.TermXTransformer;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.xmlunit.diff.ComparisonResult.EQUAL;

/*
 * The following unit tests file implements the message exchange simulation experimental protocol for testing HL7 v2.
 * The example messages and exchange flow used are actual Cobas Liat analyzer messages from official documentation.
 * The actual connections with the analyzer and LIS are not established, as it is not an integration test.
 * For the flow chart and more information, please refer to the docs folder.
 *
 * NOTE: The FHIR-V2 transformations are implemented in a TermX dev server, meaning that transformations are
 * unstable and may change over time.
 * This will be changed in the future, the transformations will be implemented using static
 * implementation guides / conformance resources.
 */
public class CobasLiatSimulationTests {

    private static HapiContext context = new DefaultHapiContext();
    private static final String RESOURCES_PATH = "src/test/resources/hl7v2";
    private static TermXTransformer transformer = new TermXTransformer();

    @Test
    void successfulOrderResultsTest() throws Exception {
        // a new specimen/test is introduced to the analyzer
        // analyzer performs test on sample
        analyzerSendsTestResultToLis();
        lisRespondsWithAcknowledgment(true);
    }

    @Test
    void unsuccessfulOrderResultsTest() throws Exception {
        // a new specimen/test is introduced to the analyzer
        // analyzer performs test on sample
        analyzerSendsTestResultToLis();
        lisRespondsWithAcknowledgment(false);
    }

    private void analyzerSendsTestResultToLis() throws Exception {
        String stepPath = RESOURCES_PATH + "/analyzerSendsResultsToLis";
        Path v2PipePath = Path.of(stepPath + "/v2Pipe.txt");
        Path v2XmlPath = Path.of(stepPath + "/v2Xml.xml");
        Path fhirXmlPath = Path.of(stepPath + "/fhirXml.xml");

        // Analyzer pipe-delimited message
        String pipeMessageString = Files.readString(v2PipePath);
        Message parsedPipeMessage = getParsedV2PipeMessage(pipeMessageString);
        Assertions.assertNotNull(parsedPipeMessage);

        // Analyzer XML-serialized message
        String parsedMessageAsXml = getParsedMessageAsXml(parsedPipeMessage);
        String expectedMessageAsXml = Files.readString(v2XmlPath);
        Diff v2Diff = buildXmlDiff(expectedMessageAsXml, parsedMessageAsXml);
        Assertions.assertFalse(v2Diff.hasDifferences(), v2Diff.toString());

        // Analyzer V2 message transformed to FHIR
        String fhirMessage = transformer.fromOruR30ToFhirBundle(parsedMessageAsXml);
        String expectedFhirMessage = Files.readString(fhirXmlPath);
        Diff fhirDiff = buildXmlDiff(expectedFhirMessage, fhirMessage);
        Assertions.assertFalse(fhirDiff.hasDifferences(), fhirDiff.toString());
    }

    private void lisRespondsWithAcknowledgment(boolean success) throws Exception {
        String stepPath = RESOURCES_PATH + "/lisAcknowledgementResponse";
        stepPath += success ? "/success" : "/failure";

        Path v2PipePath = Path.of(stepPath + "/v2Pipe.txt");
        Path v2XmlPath = Path.of(stepPath + "/v2Xml.xml");
        Path fhirXmlPath = Path.of(stepPath + "/fhirXml.xml");

        // Incoming LIS FHIR message
        String fhirXmlString = Files.readString(fhirXmlPath);

        // LIS FHIR message transformed to v2 XML
        String v2XmlString = transformer.fromFhirBundleToAckR33(fhirXmlString);
        Message parsedV2Message = getParsedV2XmlMessage(v2XmlString);
        String expectedV2XmlString = Files.readString(v2XmlPath);
        Assertions.assertNotNull(parsedV2Message);
        Diff v2Diff = buildXmlDiff(expectedV2XmlString, v2XmlString);
        Assertions.assertFalse(v2Diff.hasDifferences(), v2Diff.toString());

        // LIS V2 XML message transformed to pipe-delimited format
        String pipeDelimitedString = getParsedMessageAsPipeDelimited(parsedV2Message);
        String expectedPipeDelimitedString = Files.readString(v2PipePath);
        Assertions.assertEquals(
                normalizeAndIgnoreMsh7Timestamp(expectedPipeDelimitedString).trim(),
                normalizeAndIgnoreMsh7Timestamp(pipeDelimitedString).trim()
        );
    }

    private Message getParsedV2PipeMessage(String pipeMessage) throws Exception {
        Parser parser = context.getPipeParser();
        return parser.parse(pipeMessage);
    }

    private String getParsedMessageAsPipeDelimited(Message message) throws Exception {
        Parser parser = context.getPipeParser();
        return parser.encode(message);
    }

    private String getParsedMessageAsXml(Message message) throws Exception {
        TransformableXmlParser xmlParser = new TransformableXmlParser(context);
        String xmlString = xmlParser.parse(message);
        return xmlString;
    }

    private Message getParsedV2XmlMessage(String xmlString) throws Exception {
        TransformableXmlParser xmlParser = new TransformableXmlParser(context);
        Message message = xmlParser.parse(xmlString);
        return message;
    }

    private Diff buildXmlDiff(String expectedXml, String actualXml) {
        // Ignore timestamp differences in MSH-7 as it is dynamically generated on creation
        expectedXml = expectedXml.replaceAll(
                "<TS--1>\\d{14}</TS--1>",
                "<TS--1>IGNORED</TS--1>"
        );

        actualXml = actualXml.replaceAll(
                "<TS--1>\\d{14}</TS--1>",
                "<TS--1>IGNORED</TS--1>"
        );

        return DiffBuilder
                .compare(expectedXml)
                .withTest(actualXml)
                .ignoreWhitespace()
                .ignoreComments()
                .checkForSimilar()
                .build();
    }

    private static String normalizeAndIgnoreMsh7Timestamp(String hl7) {
        String[] lines = hl7.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1);

        // Ignore timestamp differences in MSH-7 as it is dynamically generated on creation
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith("MSH|")) {
                String[] fields = lines[i].split("\\|", -1); // keep empty fields
                if (fields.length > 6) { // fields[6] == MSH-7
                    fields[6] = "<IGNORED>";
                    lines[i] = String.join("|", fields);
                }
            }
        }
        return String.join("\n", lines);
    }
}
