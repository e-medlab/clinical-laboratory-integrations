package org.taltech.emedlab.models.astm.v25.groups;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractGroup;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.taltech.emedlab.models.astm.v25.segments.P;

public class ASTM_MSG_PATIENT_INFORMATION extends AbstractGroup {

    public ASTM_MSG_PATIENT_INFORMATION(Group parent, ModelClassFactory factory) {
        super(parent, factory);
        this.init(factory);
    }

    private void init(ModelClassFactory factory) {
        try {
            this.add(P.class, true, false, true);
            this.add(ASTM_MSG_TEST_ORDER.class, false, true, true);
        } catch (HL7Exception e) {
            log.error("Unexpected error creating ASTM_MSG_PATIENT_INFORMATION.", e);
        }

    }
}
