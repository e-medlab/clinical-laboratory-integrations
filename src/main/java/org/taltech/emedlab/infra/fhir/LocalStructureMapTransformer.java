package org.taltech.emedlab.infra.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r5.context.ContextUtilities;
import org.hl7.fhir.r5.elementmodel.Element;
import org.hl7.fhir.r5.elementmodel.Manager;
import org.hl7.fhir.r5.elementmodel.ParserBase;
import org.hl7.fhir.r5.model.*;
import org.hl7.fhir.utilities.ByteProvider;
import org.hl7.fhir.validation.ValidationEngine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LocalStructureMapTransformer extends AbstractTransformer {
    private static final String STRUCTUREDEFINITION_PATH = "src/main/resources/structuredefinitions/";
    private static final String CONCEPTMAP_PATH = "src/main/resources/conceptmaps/";
    private static final String STRUCTUREMAP_PATH = "src/main/resources/transformations/structuremaps/";

    private final FhirContext context = FhirContext.forR5();
    private final IParser jsonParser = context.newJsonParser();
    private ValidationEngine engine;

    @Override
    public String transform(String structureMapName, String input) throws Exception {
        ValidationEngine engine = getEngine();

        Manager.FhirFormat format = Manager.FhirFormat.XML;

        String mapUri = "http://termx.health/fhir/StructureMap/" + structureMapName;

        if (input.contains("ASTM_MSG") || input.contains("ORU_R30") ) {
            input = input.replace("xmlns=\"http://hl7.org/fhir\"", "");
        }

        Element transformed = engine.transform(ByteProvider.forBytes(input.getBytes(StandardCharsets.UTF_8)), format, mapUri);

        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        ParserBase parser = new org.hl7.fhir.r5.elementmodel.XmlParser(engine.getContext());
        parser.compose(transformed, boas, org.hl7.fhir.r5.formats.IParser.OutputStyle.PRETTY, null);
        String result = boas.toString(StandardCharsets.UTF_8);
        boas.close();

        return result;
    }

    private synchronized ValidationEngine getEngine() {
        if (engine == null) {
            try {
                engine = new ValidationEngine.ValidationEngineBuilder().fromSource("hl7.fhir.r5.core#5.0.0");

                List<StructureDefinition> sds = getStructureDefinitions();
                sds.forEach(sd -> engine.getContext().cacheResource(sd));

                List<ConceptMap> cms = getConceptMaps();
                cms.forEach(cm -> engine.getContext().cacheResource(cm));

                List<StructureMap> sms = getStructureMaps();
                sms.forEach(sm -> {
                    prepareStructureMap(engine, sm);
                    engine.getContext().cacheResource(sm);
                });

                return engine;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return engine;
    }
    private List<StructureDefinition> getStructureDefinitions() throws IOException {
        List<Path> structureDefinitionPaths = new ArrayList<>();

        Files.list(Path.of(STRUCTUREDEFINITION_PATH + "st/"))
                .filter(p -> p.toString().endsWith(".json"))
                .forEach(structureDefinitionPaths::add);

        Files.list(Path.of(STRUCTUREDEFINITION_PATH + "astmst/"))
                .filter(p -> p.toString().endsWith(".json"))
                .forEach(structureDefinitionPaths::add);

        List<StructureDefinition> structureDefinitions = new ArrayList<>();
        for (Path p : structureDefinitionPaths) {
            String sdString = Files.readString(p);
            StructureDefinition sd = (StructureDefinition) jsonParser.parseResource(sdString);
            structureDefinitions.add(sd);
        }

        return structureDefinitions;
    }

    private List<ConceptMap> getConceptMaps() throws IOException {
        List<Path> conceptMapFilePaths = Files.list(Path.of(CONCEPTMAP_PATH))
                .filter(p -> p.toString().endsWith(".json"))
                .toList();

        List<ConceptMap> conceptMaps = new ArrayList<>();
        for (Path p : conceptMapFilePaths) {
            String cmString = Files.readString(p);
            ConceptMap cm = (ConceptMap) jsonParser.parseResource(cmString);
            conceptMaps.add(cm);
        }

        return conceptMaps;
    }

    private List<StructureMap> getStructureMaps() throws IOException {
        List<Path> structureMapFilePaths = Files.list(Path.of(STRUCTUREMAP_PATH))
                .filter(p -> p.toString().endsWith(".json"))
                .toList();

        List<StructureMap> structureMaps = new ArrayList<>();
        for (Path p : structureMapFilePaths) {
            String smString = Files.readString(p);
            StructureMap sm = (StructureMap) jsonParser.parseResource(smString);
            structureMaps.add(sm);
        }

        return structureMaps;
    }

    private void prepareStructureMap(ValidationEngine eng, StructureMap sm) {
        // this should not be needed. ValidationEngine#getSourceResourceFromStructureMap searches for definition by alias, however alias is nullable. workaround.
        sm.getStructure().stream().filter(s -> s.getAlias() == null)
                .forEach(s -> s.setAlias(eng.getContext().listStructures().stream()
                        .filter(sd -> sd.getUrl().equals(s.getUrl())).findFirst().orElseThrow().getName()));

        //fix snapshots?
        ContextUtilities cu = new ContextUtilities(eng.getContext());
        cu.allStructures().stream().filter(sd -> !sd.hasSnapshot())
                .forEach(cu::generateSnapshot);
    }
}
