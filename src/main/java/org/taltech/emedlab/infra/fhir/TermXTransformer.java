package org.taltech.emedlab.infra.fhir;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TermXTransformer extends AbstractTransformer{
    private static final String TERMX_URL = "https://dev.termx.org/api/fhir/";

    @Override
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
