package org.astm.v25.groups;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractGroup;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.astm.v25.segments.P;

public class ASTM_MSG_PATIENT_INFORMATION extends AbstractGroup {

    protected ASTM_MSG_PATIENT_INFORMATION(Group parent, ModelClassFactory factory) {
        super(parent, factory);
        this.init(factory);
    }

    private void init(ModelClassFactory factory) {
        try {
            this.add(P.class, true, false, true);
        } catch (HL7Exception e) {
            log.error("Unexpected error creating ADR_A19_INSURANCE - this is probably a bug in the source code generator.", e);
        }

    }
}
