# Clinical Laboratory Integrations

The aim of this project is two-fold:
- To give practical examples on how to use the HAPI v2 library to manage connections with clinical laboratory analyzer devices that speak either HL7 v2 or ASTM
- To enable using the FHIR Mapping Language for converting HL7 v2 messages to another format

## Dependencies

The project utilizes the open source HAPI v2 and FHIR libraries to generate StructureDefinitions from HL7 v2 message classes, to exemplify connecting to HL7 v2 speaking analyzers and to process analyzer messages.

The project also utilizes a forked version of the Apache-2.0 licensed 'comm' library for ASTM connection drivers: https://github.com/e-medlab/comm.

## Features

### FHIR StructureDefinition generator

This project contains a feature capable of generating FHIR StructureDefinition resources from HAPI v2 message classes.
The StructureDefinitions conform to the output of HL7 v2 defined native XML representation, output by the XML parsers, with a slight modification (see section on using FML below).

This feature works for both HL7 v2, ASTM and any custom message classes that make use of the HAPI v2 library's message model creation API.

The feature is encapsulated in a single file, available at the folder structure `infra/fhir/HapiMessageClassToFhirStructureDefinitionConverter.java`.

To run the generator, execute the main method of the `HapiMessageClassToFhirStructureDefinitionConverter` class.
In the main method, there are two options:

```java
    generateStructureDefinitions(message);
    // or
    generateAstmStructureDefinitions();
```

`generateAstmStructureDefinitions()` is a convenience method that generates StructureDefinitions for all ASTM message classes, as all ASTM messages are encapsulated inside the ASTM_MSG class.
Because other initial setup is needed for ASTM message usage, such as setting up the context and the parsers, this method is provided to simplify the generation process.
Under the hood, it calls `generateStructureDefinitions(message)`.
The message parameter is an instance of the HAPI v2 `Message` class.

The generator crawls the message tree recursively, generating FHIR StructureDefinitions for each segment, field, component and sub-component it encounters.

### Using the HAPI v2 library to define and parse ASTM-1394 / LIS02-A messages

HL7 v2 has great community support and open-source libraries for working with the standard, its various versions and devices/systems that speak the protocol.
ASTM-1381 / LIS01-A and ASTM-1394 / LIS02-A is another protocol commonly used by clinical laboratory analyzers, but openly available resources for development with this standard are extremely limited.
This project contains examples and infrastructure code for working with ASTM-1381 / LIS01-A and ASTM-1394 / LIS02-A messages using the HAPI v2 library, standardizing the development experience across both HL7 v2 and ASTM protocols.

In the folder `models.astm.v25` there are HAPI v2 message class definitions for ASTM-1394 / LIS02-A messages.
These message classes can be used to parse and generate ASTM messages in the same way as HL7 v2 messages using the HAPI v2 library.
What makes ASTM messages simpler than Hl7 v2 messages is that ASTM-1394 / LIS02-A messages are made specifically for clinical laboratory analyzers.
The highest level message structure is the same across all ASTM messages, and only the segments and fields inside the message vary based on the use case.
In HL7 v2, there are various different top-level messages that have different structures and contexts and events where they are supposed to be used.

This project makes use of the similarity of the two standards to provide a unified way of working with them.
The pipe-delimited format of both standards is very similar.
The message structures are also similar, but with a key difference in the fact that while HL7 v2 has the option for sub-components in components, ASTM-1394 / LIS02-A stops at the component level.
In addition, the message header segments are named differently and key elements in the header, such as delimiters and versions are located in different fields.
This is why the following overrides are made in the ASTM message class.

```java
    // A dummy version is necessary to bypass version validation
    @Override
    public String getVersion() {
        return "2.5";
    }

    @Override
    public String getEncodingCharactersValue() throws HL7Exception {
        Segment h = (Segment) get("H");
        Primitive value = (Primitive) h.getField(2, 0);

        // ASTM message contains the repeat delimiter first, then component delimiter
        // HL7 expects component delimiter first, then repeat delimiter
        // This is why we swap them here
        String original = value.getValue();
        String swapped = "" + original.charAt(1) + original.charAt(0) + original.substring(2);

        // tilde added as a hack, v2 requires subcomponent separator not present in ASTM at all
        return swapped + "~";
    }

    @Override
    public Character getFieldSeparatorValue() throws HL7Exception {
        Segment h = (Segment) get("H");
        Primitive value = (Primitive) h.getField(1, 0);

        return value.getValue().charAt(0);
    }
```

Many HL7 v2 structural assumptions are hard-coded in the HAPI v2 library.
Because of this, custom implementations of the pipe parser and XML parser are required and can be found in the folder `infra.parsers` as `Astm1394PipeParser.java` and `Astm1394XmlParser.java`.
Both of these parsers extend the default HAPI v2 parsers and override methods to accommodate for the hard-coded assumptions of the HL7 v2 structures.
The `AstmModelClassFactory.java` is also provided to enable the HAPI v2 context to create ASTM message instances.

Notably, validation is disabled in the HAPI context in the examples when working with ASTM messages.
This is a workaround.
In fact, a custom validation routine should be implemented to validate ASTM messages.

### Transforming v2 and ASTM messages using the FHIR Mapping Language

Using transformer classes (`TermXTransformer.java` and `LocalStructureMapTransformer.java`), this project provides examples of how to use the FHIR Mapping Language to transform HL7 v2 and ASTM messages to other formats, in our example cases FHIR.
The TermXTransformer transformations call a remote TermX development environment instance, which makes them unstable and likely already unusable.
The code simply exemplifies how to use the TermX API to perform transformations.
The LocalStructureMapTransformer is more stable.

It uses:
- local generated StructureDefinitions (see section above on FHIR StructureDefinition generator).
- local transformation StructureMaps and ConceptMaps exported from TermX where the transformations were developed using the visual FML redactor.

All of those resources can be found under the `resources` folder in the `org.taltech.emedlab` package.
The local transformer also contains comments that give instructions where and how a terminology or FHIR server (external or local) can be used to perform the transformations.

The examples show how to pre-process HL7 v2 and ASTM messages for transformations using the FHIR Mapping Language.
The pre-processing utilizes the native XML serialized format of the HL7 v2 standard.
Thanks to being able to parse ASTM messages with the HAPI v2 library, the same XML serialization can be used for ASTM messages as well.

A notable change had to be made in the native XML serialization format, however.
In the native format, groups, components and sub-components have tag names containing dots (.), for example:

```xml
<OML_O21>
    <OML_O21.PATIENT>
        <PID>
            <PID.1>1</PID.1>
            <PID.3>
                <CX.1>PatientID-1234</CX.1>
            </PID.3>
        </PID>
    </OML_O21.PATIENT>
</OML_O21>
```

This raises problems when defining FHIR StructureDefinition resources of the XML representation and when using FHIR Mapping Language.
The dots are treated as path separators, which breaks the structure.
This is why the dot delimiter has been replaced with `--` in this project, but any other delimiter could be used you are confident is not appearing in the message content.
Check the modification at `TransformableXmlParser.java` at path `infra/parsers`.

An example with the modified format:

```xml
<OML_O21>
    <OML_O21--PATIENT>
        <PID>
            <PID--1>1</PID--1>
            <PID--3>
                <CX--1>PatientID-1234</CX--1>
            </PID--3>
        </PID>
    </OML_O21--PATIENT>
</OML_O21>
```

### Examples and infrastructure code for connecting to clinical laboratory analyzers with both HL7 v2 and ASTM-1381 / LIS01-A protocols

In the world of laboratory analyzers, lower level connections vary from vendor to vendor. Sometimes the laboratory information system (LIS) acts as the server and the analyzer as the client.
Sometimes the LIS acts as the client and the analyzer as the server.
Sometimes connections are kept open continuously, sometimes connections are opened and closed for each message exchange.

This project contains example code and infrastructure code for connecting to clinical laboratory analyzers using both HL7 v2 and ASTM-1381 / LIS01-A protocols.
Although the higher level model sections of the two standard families are similar, the lower level connection management and message exchange flows differ significantly.

For connection management of HL7 v2 speaking analyzers, the HAPI v2 library's connection management features are utilized.
For ASTM-1381 / LIS01-A speaking analyzers, an open source library is utilized. For separation of concerns and licensing reasons, the library has been forked and is included as a module in this project.
The forked library is available at: https://github.com/e-medlab/comm.
Compared to the fork, TCP/IP connection support has been supplemented with a continuously open socket between the analyzer and the host.
Before forking, the library only supported opening and closing a socket connection for each message exchange in the case the LIS acted as the server and the analyzer as the client.

In the `infra/tcp` folder, there are `AstmTcpClient.java` and `AstmTcpServer.java` classes that encapsulate the driver for ASTM TCP/IP connection management.
This is an effort to provide a similar abstraction layer and API as the HAPI v2 library provides for HL7 v2 connections.

Under `examples` in their respective folders for ASTM and HL7 v2, there are code examples for connecting to analyzers using both protocols, utilizing the abstractions and infrastructure code explained in this section.

Notably, the examples in this project only cover TCP/IP connections.
Many analyzers still use serial connections (RS-232) or USB virtual COM port connections, for which examples can be added in the future if needed.

### Test cases for ASTM / LIS01/02 and HL7 v2 analyzer message exchange flows based on real analyzer documentation

To validate that this project provides useful and relevant working examples, test cases have been created based on real analyzer documentation.
The test cases have been described as validation experiment protocols, available in the `docs` folder.
These test cases are designed to be carried out with real clinical laboratory analyzers or simulator software (such as the HAPI test panel and ASTM sender, for example).
The project is meant to act as the mediating agent and the researcher is meant to act as the LIS.
For testing ASTM, the test cases are based on the Konelab T60 analyzers.
For testing HL7 v2, the test cases are based on the Cobas Liat analyzers.
Validation experiment protocol flow-charts described the message flow between the parties.

To complement these test cases and validate the project code itself, unit tests have been created that implement the same experiment flow charts used for manual validation experiments.
The unit tests follow the protocol, but simulate both the LIS and the analyzer sides of the communication using simple hard-coded message files.
These unit tests can be found in the `test` folder under their respective ASTM and HL7 v2 folders, in files `KonelabT60SimulationTests.java` and `CobasLiatSimulationTests.java`.