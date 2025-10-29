package org.transformer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Hl7ToFhirTransformer {
    private static final String TRANSFORM_URL = "https://dev.termx.org/api/fhir/StructureMap/V2OruR30ToFhirBundle/$transform";

    /**
     * Sends an HL7 v2 message (in XML form) to the FHIR transformation endpoint
     * and returns the response body as a string.
     *
     * @param hl7Xml The HL7 v2 message serialized as XML
     * @return The response from the server (FHIR Bundle in JSON or XML)
     * @throws IOException If a network or I/O error occurs
     * @throws InterruptedException If the HTTP request is interrupted
     */
    public String transform(String hl7Xml) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TRANSFORM_URL))
                .header("Content-Type", "application/fhir+xml;fhirVersion=4.3.0")
                .header("Accept", "application/fhir+xml;fhirVersion=4.3.0")
                .POST(HttpRequest.BodyPublishers.ofString(hl7Xml))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
