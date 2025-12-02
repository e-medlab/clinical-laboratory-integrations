package org.converter;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.*;
import ca.uhn.hl7v2.model.primitive.*;
import ca.uhn.hl7v2.model.v25.message.OML_O21;
import ca.uhn.hl7v2.model.v25.message.ORU_R30;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import org.astm.v25.ASTM_MSG;
import org.astm.v25.parser.Astm1394PipeParser;
import org.astm.v25.parser.AstmModelClassFactory;
import org.hl7.fhir.r5.model.ElementDefinition;
import org.hl7.fhir.r5.model.Enumerations;
import org.hl7.fhir.r5.model.StructureDefinition;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class V2MessageClassToFhirStructureDefinitionConverter {

    private static String PROFILE_BASE_URL = "http://hl7.org/hapi-v2-parser/StructureDefinition/";
    private static String MESSAGE_PATH = "src/main/resources/ExampleOmlO21Message.hl7";
    private static String FILE_PATH = "src/main/resources/astmst/";

    private static HapiContext v2Context = new DefaultHapiContext();
    private static FhirContext fhirContext = FhirContext.forR5();

    private static ArrayList<StructureDefinition> structureDefinitions = new ArrayList();

    public static void main(String args[]) throws Exception {
        //PipeParser pipeParser = new PipeParser();
        //String messageString = Files.readString(Path.of(MESSAGE_PATH));

        //ORU_R30 oruR30 = new ORU_R30(v2Context.getModelClassFactory());
        //generateStructureDefinitions(oruR30);

        //OML_O21 message = (OML_O21) pipeParser.parse(messageString);
        //generateStructureDefinitions(message);

        generateAstmStructureDefinitions();
    }

    private static void generateAstmStructureDefinitions() throws Exception {
        ModelClassFactory customModelClassFactory = new AstmModelClassFactory();
        v2Context.setModelClassFactory(customModelClassFactory);
        v2Context.getParserConfiguration().setValidating(false);
        Parser parser = new Astm1394PipeParser(v2Context);

        String messageString = Files.readString(Path.of("src/test/resources/ExampleAstmPatientOrderResultsToLis.txt"));
        Message message = parser.parse(messageString);
        generateStructureDefinitions(message);
    }

    private static void generateStructureDefinitions(Message message) throws Exception {
        convert(message);

        IParser fhirParser = fhirContext.newJsonParser().setPrettyPrint(true);
        for (StructureDefinition st : structureDefinitions){
            String stString = fhirParser.encodeToString(st);
            Files.writeString(Path.of(FILE_PATH + st.getName() + ".json"), stString);
        }

        structureDefinitions = new ArrayList();
    }

    private static void convert(Structure structure) throws Exception {
        if (structure instanceof Message) {
            System.out.println("Converting message structure: " + structure.getName());
            convertMessage((Message)structure);
            return;
        }
        if (structure instanceof Segment) {
            System.out.println("Converting segment structure: " + structure.getName());
            convertSegment((Segment)structure);
            return;
        }
        if (structure instanceof Group) {
            System.out.println("Converting group structure: " + structure.getName());
            convertGroup((Group)structure);
            return;
        }

        System.out.println("WARNING: Skipping unconfigured structure: " + structure.getName());
    }

    // NB: Order is important here
    private static void convert(Type type) {
        if (type instanceof ca.uhn.hl7v2.model.Composite) {
            System.out.println("Converting composite type: " + type.getName());
            convertComposite((Composite)type);
            return;
        }
        if (type instanceof ca.uhn.hl7v2.model.primitive.TSComponentOne) {
            System.out.println("Converting TSComponentOne primitive type: " + type.getName());
            convertPrimitive(type, "dateTime");
            return;
        }
        if (type instanceof ca.uhn.hl7v2.model.primitive.DT) {
            System.out.println("Converting DT primitive type: " + type.getName());
            convertPrimitive(type, "date");
            return;
        }
        if (type instanceof ca.uhn.hl7v2.model.primitive.TM) {
            System.out.println("Converting TM primitive type: " + type.getName());
            convertPrimitive(type, "time");
            return;
        }
        // In the future, it would be nice if legal code table references could also be added to the codes
        // But this can be done once terminology of v2 is handled
        if (type instanceof ca.uhn.hl7v2.model.primitive.ID || type instanceof ca.uhn.hl7v2.model.primitive.IS) {
            System.out.println("Converting text primitive type: " + type.getName());
            convertPrimitive(type, "code");
            return;
        }
        if (type instanceof AbstractTextPrimitive) {
            System.out.println("Converting text primitive type: " + type.getName());
            convertPrimitive(type, "string");
            return;
        }
        // Most version-agnostic implementation I could find, SI - Sequence Integer
        if (type.getClass().getSimpleName().equals("SI")) {
            System.out.println("Converting text primitive type: " + type.getName());
            convertPrimitive(type, "integer");
            return;
        }
        // All remaining types are safest if treated as decimals
        if (type instanceof AbstractPrimitive) {
            System.out.println("Converting text primitive type: " + type.getName());
            convertPrimitive(type, "decimal");
            return;
        }
        if (type instanceof Varies) {
            // We choose to model varying types, like UNKNOWN, as strings
            // varying types are always complemented with another field that specifies that type of that field
            // at the same level of the structure
            // for example: OBX--5 is UNKNOWN, but OBX--2 specifies whether it is ST, NM or any other primitive
            // this is why this variation must be handled at mapping level
            System.out.println("Not converting varying type: " + type.getName());
            return;
        }
        System.out.println("WARNING: Skipping unconfigured type: " + type.getName());
    }

    private static void convertMessage(Message v2Message) throws Exception {
        String messageUrl = PROFILE_BASE_URL + v2Message.getName();

        StructureDefinition structureDefinition = new StructureDefinition(
                messageUrl,
                v2Message.getName(),
                Enumerations.PublicationStatus.DRAFT,
                StructureDefinition.StructureDefinitionKind.LOGICAL,
                false,
                messageUrl
        );
        structureDefinition.setId(v2Message.getName());
        structureDefinition.setBaseDefinition("http://hl7.org/fhir/StructureDefinition/Element");
        structureDefinition.setDerivation(StructureDefinition.TypeDerivationRule.SPECIALIZATION);
        StructureDefinition.StructureDefinitionDifferentialComponent differential = structureDefinition.getDifferential();

        ElementDefinition parentElementDefinition = new ElementDefinition();
        parentElementDefinition.setId(v2Message.getName());
        parentElementDefinition.setPath(v2Message.getName());
        differential.addElement(parentElementDefinition);

        String[] names = v2Message.getNames();
        for (String name : names) {
            Structure structure = v2Message.get(name);
            convert(structure);
            differential.addElement(getElementDefinition(structure, v2Message));
        }

        structureDefinition.setDifferential(differential);
        structureDefinitions.add(structureDefinition);
    }

    private static ElementDefinition getElementDefinition(Structure structure, Message message) throws HL7Exception {
        ElementDefinition elementDefinition = new ElementDefinition();
        String structureName = getMessageElementName(structure, message);
        String structurePath = message.getName() + '.' + structureName;

        elementDefinition.setId(structurePath);
        elementDefinition.setMin(message.isRequired(structure.getName()) ? 1 : 0);
        elementDefinition.setMax(message.isRepeating(structure.getName()) ? "*" : "1");
        elementDefinition.setPath(structurePath);

        ElementDefinition.TypeRefComponent typeRef = new ElementDefinition.TypeRefComponent();
        typeRef.setCode(PROFILE_BASE_URL + structure.getName());
        elementDefinition.addType(typeRef);

        return elementDefinition;
    }

    private static String getMessageElementName(Structure structure, Message message) throws HL7Exception {
        if (structure instanceof Group) {
            return message.getName() + "--" + structure.getName();
        }
        return structure.getName();
    }

    private static void convertGroup(Group group) throws Exception {
        String groupName = group.getName();
        String groupUrl = PROFILE_BASE_URL + groupName;

        StructureDefinition structureDefinition = new StructureDefinition(
                groupUrl,
                groupName,
                Enumerations.PublicationStatus.DRAFT,
                StructureDefinition.StructureDefinitionKind.LOGICAL,
                false,
                groupUrl
        );
        structureDefinition.setId(groupName);
        structureDefinition.setBaseDefinition("http://hl7.org/fhir/StructureDefinition/Element");
        structureDefinition.setDerivation(StructureDefinition.TypeDerivationRule.SPECIALIZATION);
        StructureDefinition.StructureDefinitionDifferentialComponent diff = structureDefinition.getDifferential();

        ElementDefinition parentElementDefinition = new ElementDefinition();
        parentElementDefinition.setId(groupName);
        parentElementDefinition.setPath(groupName);
        diff.addElement(parentElementDefinition);

        String[] names = group.getNames();
        for (String name : names) {
            Structure child = group.get(name);
            convert(child);
            diff.addElement(getElementDefinition(child, group));
        }

        structureDefinition.setDifferential(diff);
        structureDefinitions.add(structureDefinition);
    }

    private static ElementDefinition getElementDefinition(Structure structure, Group group) throws HL7Exception {
        ElementDefinition elementDefinition = new ElementDefinition();
        String structureName = structure.getName();
        String structurePath = group.getName() + '.' + structureName;

        elementDefinition.setId(structurePath);
        elementDefinition.setMin(group.isRequired(structureName) ? 1 : 0);
        elementDefinition.setMax(group.isRepeating(structureName) ? "*" : "1");
        elementDefinition.setPath(structurePath);

        ElementDefinition.TypeRefComponent typeRef = new ElementDefinition.TypeRefComponent();
        typeRef.setCode(PROFILE_BASE_URL + structureName);
        elementDefinition.addType(typeRef);

        return elementDefinition;
    }

    private static void convertSegment(Segment segment) throws Exception {
        String segmentName = segment.getName();
        String segmentUrl = PROFILE_BASE_URL + segmentName;

        StructureDefinition structureDefinition = new StructureDefinition(
                segmentUrl,
                segmentName,
                Enumerations.PublicationStatus.DRAFT,
                StructureDefinition.StructureDefinitionKind.LOGICAL,
                false,
                segmentUrl
        );
        structureDefinition.setId(segmentName);
        structureDefinition.setBaseDefinition("http://hl7.org/fhir/StructureDefinition/Element");
        structureDefinition.setDerivation(StructureDefinition.TypeDerivationRule.SPECIALIZATION);
        StructureDefinition.StructureDefinitionDifferentialComponent diff = structureDefinition.getDifferential();

        ElementDefinition parentElementDefinition = new ElementDefinition();
        parentElementDefinition.setId(segmentName);
        parentElementDefinition.setPath(segmentName);
        diff.addElement(parentElementDefinition);

        String[] fieldNames = segment.getNames();
        for (int i = 0; i < fieldNames.length; i++) {
            int fieldNum = i + 1;
            Type field = segment.getField(fieldNum, 0);
            ElementDefinition fld = getElementDefinition(fieldNum, field, segment);
            convert(field);
            diff.addElement(fld);
        }

        structureDefinition.setDifferential(diff);
        structureDefinitions.add(structureDefinition);
    }

    private static ElementDefinition getElementDefinition(int fieldNum, Type field, Segment segment) throws HL7Exception {
        ElementDefinition elementDefinition = new ElementDefinition();
        String fieldPath = segment.getName() + "." + segment.getName() + "--" + fieldNum;

        elementDefinition.setId(fieldPath);
        elementDefinition.setMin(segment.isRequired(fieldNum) ? 1 : 0);
        elementDefinition.setMax(Integer.toString(segment.getMaxCardinality(fieldNum)));
        elementDefinition.setPath(fieldPath);
        elementDefinition.setShort(segment.getNames()[fieldNum - 1]);

        ElementDefinition.TypeRefComponent typeRef = new ElementDefinition.TypeRefComponent();

        // We choose to model varying types, like UNKNOWN, as strings
        // varying types are always complemented with another field that specifies that type of that field
        // at the same level of the structure
        // for example: OBX--5 is UNKNOWN, but OBX--2 specifies whether it is ST, NM or any other primitive
        // this is why this variation must be handled at mapping level
        if (field instanceof Varies) {
            typeRef.setCode(PROFILE_BASE_URL + "ST");
            elementDefinition.setDefinition("""
                    This is a varying type that is serialized as an XML string.
                    The actual primitive data type of this field is determined the value by another field on this structure.
                    """);
        }
        else {
            typeRef.setCode(PROFILE_BASE_URL + field.getName());
        }

        elementDefinition.addType(typeRef);

        return elementDefinition;
    }

    private static void convertComposite(Composite composite) {
        String compositeName = composite.getName();
        String compositeUrl = PROFILE_BASE_URL + compositeName;

        StructureDefinition structureDefinition = new StructureDefinition(
                compositeUrl,
                compositeName,
                Enumerations.PublicationStatus.DRAFT,
                StructureDefinition.StructureDefinitionKind.LOGICAL,
                false,
                compositeUrl
        );
        structureDefinition.setId(compositeName);
        structureDefinition.setBaseDefinition("http://hl7.org/fhir/StructureDefinition/Element");
        structureDefinition.setDerivation(StructureDefinition.TypeDerivationRule.SPECIALIZATION);
        StructureDefinition.StructureDefinitionDifferentialComponent diff = structureDefinition.getDifferential();

        ElementDefinition parentElementDefinition = new ElementDefinition();
        parentElementDefinition.setId(compositeName);
        parentElementDefinition.setPath(compositeName);
        diff.addElement(parentElementDefinition);

        Type[] fields = composite.getComponents();
        for (int i = 0; i < fields.length; i++) {
            int fieldNum = i + 1;
            ElementDefinition fld = getElementDefinition(fieldNum, fields[i], composite);
            convert(fields[i]);
            diff.addElement(fld);
        }

        structureDefinition.setDifferential(diff);
        structureDefinitions.add(structureDefinition);
    }

    private static ElementDefinition getElementDefinition(int fieldNum, Type field, Composite composite) {
        ElementDefinition elementDefinition = new ElementDefinition();
        String fieldPath = composite.getName() + "." + composite.getName() + "--" + fieldNum;

        elementDefinition.setId(fieldPath);
        elementDefinition.setMin(0); // Whether a field is required or not is not encoded into HAPI models, therefore we default to may or may not be present
        elementDefinition.setMax("1"); // Repetition is not allowed inside composite types per HL7 v2 spec
        elementDefinition.setPath(fieldPath);

        ElementDefinition.TypeRefComponent typeRef = new ElementDefinition.TypeRefComponent();

        // We choose to model varying types, like UNKNOWN, as strings
        // varying types are always complemented with another field that specifies that type of that field
        // at the same level of the structure
        // for example: OBX--5 is UNKNOWN, but OBX--2 specifies whether it is ST, NM or any other primitive
        // this is why this variation must be handled at mapping level
        if (field instanceof Varies) {
            typeRef.setCode(PROFILE_BASE_URL + "ST");
            elementDefinition.setDefinition("""
                    This is a varying type that is serialized as an XML string.
                    The actual primitive data type of this field is determined the value by another field on this structure.
                    """);
        }
        else {
            typeRef.setCode(PROFILE_BASE_URL + field.getName());
        }

        elementDefinition.addType(typeRef);

        return elementDefinition;
    }

    private static void convertPrimitive(Type primitive, String fhirPrimitive) {
        String primitiveName = primitive.getName();
        String primitiveUrl = PROFILE_BASE_URL + primitiveName;

        StructureDefinition structureDefinition = new StructureDefinition(
                primitiveUrl,
                primitiveName,
                Enumerations.PublicationStatus.DRAFT,
                StructureDefinition.StructureDefinitionKind.LOGICAL,
                false,
                primitiveUrl
        );
        structureDefinition.setId(primitiveName);
        structureDefinition.setBaseDefinition("http://hl7.org/fhir/StructureDefinition/" + fhirPrimitive);
        structureDefinition.setDerivation(StructureDefinition.TypeDerivationRule.SPECIALIZATION);

        StructureDefinition.StructureDefinitionDifferentialComponent diff = structureDefinition.getDifferential();

        ElementDefinition parentElementDefinition = new ElementDefinition();
        parentElementDefinition.setId(primitiveName);
        parentElementDefinition.setPath(primitiveName);
        diff.addElement(parentElementDefinition);

        ElementDefinition xmlTextElementDefinition = new ElementDefinition();
        xmlTextElementDefinition.setId(primitiveName + "." + "xmlText");
        xmlTextElementDefinition.setPath(primitiveName + "." + "xmlText");
        xmlTextElementDefinition.setMin(0);
        xmlTextElementDefinition.setMax("1");
        ElementDefinition.TypeRefComponent typeRef = new ElementDefinition.TypeRefComponent();
        typeRef.setCode(fhirPrimitive);
        xmlTextElementDefinition.addType(typeRef);
        xmlTextElementDefinition.addRepresentation(ElementDefinition.PropertyRepresentation.XMLTEXT);
        diff.addElement(xmlTextElementDefinition);

        structureDefinitions.add(structureDefinition);
    }
}
