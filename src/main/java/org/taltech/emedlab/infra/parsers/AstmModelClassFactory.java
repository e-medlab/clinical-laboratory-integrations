package org.taltech.emedlab.infra.parsers;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import org.taltech.emedlab.models.astm.v25.ASTM_MSG;

public class AstmModelClassFactory extends DefaultModelClassFactory {

    @Override
    public Class<? extends Message> getMessageClass(String name, String version, boolean isExplicit)
            throws HL7Exception {

        if ("ASTM_MSG".equals(name)) {
            return ASTM_MSG.class;
        }

        return super.getMessageClass(name, version, isExplicit);
    }
}
