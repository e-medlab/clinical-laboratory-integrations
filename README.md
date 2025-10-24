# HL7 v2 StructureDefinitions and parser

The aim of this project is to enable using the FHIR Mapping Language for converting HL7 v2 messages to another format.

The project utilizes the open source HAPI v2 and FHIR libraries to generate StructureDefinitions from HL7 v2 message classes.

A v2 message class is traversed top-down and all required message segments, groups, and datatypes are output as FHIR StructureDefinitions.


The project also contains an enhancement to the HL7 v2 HAPI library DefaultXmlParser that outputs he v2 message XMLs in a format that StructureDefinitions and FML can consume, in respect to the XML tree element names and paths.

An example is shown where a pipe-delimited v2 message is read in and a custom XML representation is output.
