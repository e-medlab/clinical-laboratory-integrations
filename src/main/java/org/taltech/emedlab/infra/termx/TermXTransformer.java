package org.taltech.emedlab.infra.termx;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TermXTransformer {
    private static final String TERMX_URL = "https://dev.termx.org/api/fhir/";

    public String fromOruR30ToFhirBundle(String hl7Xml) throws IOException, InterruptedException {
        return transform("V2OruR30ToFhirBundle", hl7Xml);
    }

    public String fromFhirBundleToOmlO21(String fhirXml) throws IOException, InterruptedException {
        return transform("FhirBundleToV2OmlO21", fhirXml);
    }

    public String fromAstmMsgOrderResultsToFhirBundle(String astmXml) throws IOException, InterruptedException {
        return transform("AstmMsgOrderResultsToFhirBundle", astmXml);
    }

    public String fromFhirBundleToAstmMsgAllOrders(String fhirXml) throws IOException, InterruptedException {
        return transform("FhirBundleToAstmMsgAllOrders", fhirXml);
    }

    public String transform(String structureMapName, String input) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TERMX_URL + "StructureMap/" + structureMapName + "/$transform"))
                .header("Content-Type", "application/fhir+xml;fhirVersion=4.3.0")
                .header("Accept", "application/fhir+xml;fhirVersion=4.3.0")
                .POST(HttpRequest.BodyPublishers.ofString(input))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
