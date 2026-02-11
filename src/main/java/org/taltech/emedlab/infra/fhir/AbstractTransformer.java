package org.taltech.emedlab.infra.fhir;

import java.io.IOException;

public abstract class AbstractTransformer {
    public String fromOruR30ToFhirBundle(String hl7Xml) throws Exception {
        return transform("V2OruR30ToFhirBundle", hl7Xml);
    }

    public String fromFhirBundleToOmlO21(String fhirXml) throws Exception {
        return transform("FhirBundleToV2OmlO21", fhirXml);
    }

    public String fromFhirBundleToAckR33(String fhirXml) throws Exception {
        return transform("FhirBundleToV2AckR33", fhirXml);
    }

    public String fromAstmMsgOrderResultsToFhirBundle(String astmXml) throws Exception {
        return transform("AstmMsgOrderResultsToFhirBundle", astmXml);
    }

    public String fromAstmMsgQueryAllOrdersToFhirBundle(String astmXml) throws Exception {
        return transform("AstmMsgQueryAllOrdersToFhirBundle", astmXml);
    }

    public String fromFhirBundleToAstmMsgAllOrders(String fhirXml) throws Exception {
        return transform("FhirBundleToAstmMsgAllOrders", fhirXml);
    }

    public abstract String transform(String structureMapName, String input) throws Exception;
}
