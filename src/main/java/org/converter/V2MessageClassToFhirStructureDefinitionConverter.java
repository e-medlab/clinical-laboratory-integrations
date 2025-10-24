package org.converter;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.*;
import ca.uhn.hl7v2.model.primitive.*;
import ca.uhn.hl7v2.model.v25.message.ORU_R30;
import org.hl7.fhir.r5.model.ElementDefinition;
import org.hl7.fhir.r5.model.Enumerations;
import org.hl7.fhir.r5.model.StructureDefinition;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class V2MessageClassToFhirStructureDefinitionConverter {

    private static String PROFILE_BASE_URL = "http://hl7.org/hapi-v2-parser/StructureDefinition/";
    private static String FILE_PATH = "src/main/resources/st/";

    private static HapiContext v2Context = new DefaultHapiContext();
    private static FhirContext fhirContext = FhirContext.forR5();

    private static ArrayList<StructureDefinition> structureDefinitions = new ArrayList();

    public static void main(String args[]) throws Exception {
        ORU_R30 oruR30 = new ORU_R30(v2Context.getModelClassFactory());

        convert(oruR30);

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
            convertPrimitive(type, "datetime");
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
        String structureName = structure.getName();
        String structurePath = message.getName() + '.' + structureName;

        elementDefinition.setId(structurePath);
        elementDefinition.setMin(message.isRequired(structureName) ? 1 : 0);
        elementDefinition.setMax(message.isRepeating(structureName) ? "*" : "1");
        elementDefinition.setPath(structurePath);

        ElementDefinition.TypeRefComponent typeRef = new ElementDefinition.TypeRefComponent();
        typeRef.setCode(PROFILE_BASE_URL + structureName);
        elementDefinition.addType(typeRef);

        return elementDefinition;
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
                groupName
        );
        structureDefinition.setId(groupName);
        structureDefinition.setBaseDefinition("http://hl7.org/fhir/StructureDefinition/Element");
        structureDefinition.setDerivation(StructureDefinition.TypeDerivationRule.SPECIALIZATION);
        StructureDefinition.StructureDefinitionDifferentialComponent diff = structureDefinition.getDifferential();

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
        String structurePath = group.getName() + '.' + group.getName() + "--" + structureName;

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
                segmentName
        );
        structureDefinition.setId(segmentName);
        structureDefinition.setBaseDefinition("http://hl7.org/fhir/StructureDefinition/Element");
        structureDefinition.setDerivation(StructureDefinition.TypeDerivationRule.SPECIALIZATION);
        StructureDefinition.StructureDefinitionDifferentialComponent diff = structureDefinition.getDifferential();

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
        typeRef.setCode(PROFILE_BASE_URL + field.getName());
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
                compositeName
        );
        structureDefinition.setId(compositeName);
        structureDefinition.setBaseDefinition("http://hl7.org/fhir/StructureDefinition/Element");
        structureDefinition.setDerivation(StructureDefinition.TypeDerivationRule.SPECIALIZATION);
        StructureDefinition.StructureDefinitionDifferentialComponent diff = structureDefinition.getDifferential();

        Type[] fields = composite.getComponents();
        for (int i = 0; i < fields.length; i++) {
            int fieldNum = i + 1;
            ElementDefinition fld = getElementDefinition(fieldNum, fields[i], composite);
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
        typeRef.setCode(PROFILE_BASE_URL + field.getName());
        elementDefinition.addType(typeRef);

        return elementDefinition;
    }

    private static void convertPrimitive(Type primitive, String fhirPrimitive) {
        String primitiveName = primitive.getName();
        String idPrimitiveUrl = PROFILE_BASE_URL + primitiveName;

        StructureDefinition structureDefinition = new StructureDefinition(
                idPrimitiveUrl,
                primitiveName,
                Enumerations.PublicationStatus.DRAFT,
                StructureDefinition.StructureDefinitionKind.LOGICAL,
                false,
                fhirPrimitive
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
