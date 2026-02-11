package org.taltech.emedlab.astm;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.taltech.emedlab.infra.fhir.AbstractTransformer;
import org.taltech.emedlab.infra.fhir.LocalStructureMapTransformer;
import org.taltech.emedlab.infra.parsers.Astm1394PipeParser;
import org.taltech.emedlab.infra.parsers.AstmModelClassFactory;
import org.taltech.emedlab.infra.parsers.TransformableXmlParser;
import org.taltech.emedlab.infra.fhir.TermXTransformer;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import java.nio.file.Files;
import java.nio.file.Path;

/*
 * The following unit tests file implements the message exchange simulation experimental protocol for testing ASTM.
 * The example messages and exchange flow used are Konelab T60 analyzer messages from official documentation
 * with minor implementation changes.
 * The actual connections with the analyzer and LIS are not established, as it is not an integration test.
 * For the flow chart and more information, please refer to the docs folder.
 *
 * NOTE: The FHIR-ASTM transformations are implemented in a TermX dev server, meaning that transformations are
 * unstable and may change over time.
 * This will be changed in the future, the transformations will be implemented using static
 * implementation guides / conformance resources.
 */
public class KonelabT60SimulationTests {

    private static final HapiContext context = new DefaultHapiContext();
    private static final String RESOURCES_PATH = "src/test/resources/astm";
    private static final AbstractTransformer transformer = new LocalStructureMapTransformer();

    @BeforeAll
    static void setup() {
        ModelClassFactory customModelClassFactory = new AstmModelClassFactory();
        context.setModelClassFactory(customModelClassFactory);
        context.getParserConfiguration().setValidating(false);
    }

    @Test
    void successfulOrderResultsTest() throws Exception {
        // a new specimen/test is introduced to the analyzer
        analyzerQueriesOrdersFromLis();
        lisRespondsWithOrdersToAnalyzer();
        analyzerSendsResultsToLis(true);
    }

    @Test
    void unsuccessfulOrderResultsTest() throws Exception {
        // a new specimen/test is introduced to the analyzer
        analyzerQueriesOrdersFromLis();
        lisRespondsWithOrdersToAnalyzer();
        analyzerSendsResultsToLis(false);
    }

    private void analyzerQueriesOrdersFromLis() throws Exception {
        String stepPath = RESOURCES_PATH + "/analyzerOrdersQueryToLis";
        Path astmPipePath = Path.of(stepPath + "/astmPipe.txt");
        Path astmXmlPath = Path.of(stepPath + "/astmXml.xml");
        Path fhirXmlPath = Path.of(stepPath + "/fhirXml.xml");

        // Analyzer pipe-delimited message
        String pipeMessageString = Files.readString(astmPipePath);
        Message parsedPipeMessage = getParsedAstmPipeMessage(pipeMessageString);
        Assertions.assertNotNull(parsedPipeMessage);

        // Analyzer XML-serialized message
        String parsedMessageAsXml = getParsedMessageAsXml(parsedPipeMessage);
        String expectedMessageAsXml = Files.readString(astmXmlPath);
        Diff astmDiff = buildXmlDiff(expectedMessageAsXml, parsedMessageAsXml);
        Assertions.assertFalse(astmDiff.hasDifferences(), astmDiff.toString());

        // Analyzer ASTM message transformed to FHIR
        String fhirXmlString = transformer.fromAstmMsgQueryAllOrdersToFhirBundle(parsedMessageAsXml);
        String expectedFhirXmlString = Files.readString(fhirXmlPath);
        Diff fhirDiff = buildXmlDiff(expectedFhirXmlString, fhirXmlString);
        Assertions.assertFalse(fhirDiff.hasDifferences(), fhirDiff.toString());
    }

    private void lisRespondsWithOrdersToAnalyzer() throws Exception {
        String stepPath = RESOURCES_PATH + "/lisOrdersResponseToAnalyzer";
        Path astmPipePath = Path.of(stepPath + "/astmPipe.txt");
        Path astmXmlPath = Path.of(stepPath + "/astmXml.xml");
        Path fhirXmlPath = Path.of(stepPath + "/fhirXml.xml");

        // Incoming LIS FHIR message
        String fhirXmlString = Files.readString(fhirXmlPath);

        // LIS FHIR message transformed to ASTM XML
        String astmXmlString = transformer.fromFhirBundleToAstmMsgAllOrders(fhirXmlString);
        Message parsedAstmMessage = getParsedAstmXmlMessage(astmXmlString);
        String expectedAstmXmlString = Files.readString(astmXmlPath);
        Assertions.assertNotNull(parsedAstmMessage);
        Diff astmDiff = buildXmlDiff(expectedAstmXmlString, astmXmlString);
        Assertions.assertFalse(astmDiff.hasDifferences(), astmDiff.toString());

        // LIS ASTM XML message transformed to pipe-delimited format
        String pipeDelimitedString = getParsedMessageAsPipeDelimited(parsedAstmMessage);
        String expectedPipeDelimitedString = Files.readString(astmPipePath);
        Assertions.assertEquals(
                normalizeLineEndings(expectedPipeDelimitedString).trim(),
                normalizeLineEndings(pipeDelimitedString).trim()
        );
    }

    private void analyzerSendsResultsToLis(boolean success) throws Exception {
        String stepPath = RESOURCES_PATH + "/analyzerSendsResultsToLis";
        stepPath += success ? "/success" : "/failure";
        Path astmPipePath = Path.of(stepPath + "/astmPipe.txt");
        Path astmXmlPath = Path.of(stepPath + "/astmXml.xml");
        Path fhirXmlPath = Path.of(stepPath + "/fhirXml.xml");

        // Analyzer pipe-delimited message
        String pipeMessageString = Files.readString(astmPipePath);
        Message parsedPipeMessage = getParsedAstmPipeMessage(pipeMessageString);
        Assertions.assertNotNull(parsedPipeMessage);

        // Analyzer XML-serialized message
        String parsedMessageAsXml = getParsedMessageAsXml(parsedPipeMessage);
        String expectedMessageAsXml = Files.readString(astmXmlPath);
        Diff astmDiff = buildXmlDiff(expectedMessageAsXml, parsedMessageAsXml);
        Assertions.assertFalse(astmDiff.hasDifferences(), astmDiff.toString());

        // Analyzer ASTM message transformed to FHIR
        String fhirXmlString = transformer.fromAstmMsgOrderResultsToFhirBundle(parsedMessageAsXml);
        String expectedFhirXmlString = Files.readString(fhirXmlPath);
        Diff fhirDiff = buildXmlDiff(expectedFhirXmlString, fhirXmlString);
        Assertions.assertFalse(fhirDiff.hasDifferences(), fhirDiff.toString());
    }

    private Message getParsedAstmPipeMessage(String pipeDelimitedString) throws HL7Exception {
        Parser parser = new Astm1394PipeParser(context);
        Message message = parser.parse(pipeDelimitedString);
        return message;
    }

    private Message getParsedAstmXmlMessage(String xmlString) throws Exception {
        TransformableXmlParser xmlParser = new TransformableXmlParser(context);
        Message message = xmlParser.parseAstm(xmlString);
        return message;
    }

    private String getParsedMessageAsXml(Message message) throws Exception {
        TransformableXmlParser xmlParser = new TransformableXmlParser(context);
        String xmlString = xmlParser.parse(message);
        return xmlString;
    }

    private String getParsedMessageAsPipeDelimited(Message message) throws Exception {
        Astm1394PipeParser parser = new Astm1394PipeParser(context);
        String pipeDelimitedString = parser.encode(message);
        return pipeDelimitedString;
    }

    private Diff buildXmlDiff(String expectedXml, String actualXml) {
        return DiffBuilder
                .compare(expectedXml)
                .withTest(actualXml)
                .ignoreWhitespace()
                .ignoreComments()
                .checkForSimilar()
                .build();
    }

    private String normalizeLineEndings(String text) {
        return text.replace("\r\n", "\n").replace("\r", "\n");
    }
}
