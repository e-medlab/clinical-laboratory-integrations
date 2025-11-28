package org.astm.v25.groups;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractGroup;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.astm.v25.segments.O;
import org.astm.v25.segments.R;

public class ASTM_MSG_TEST_ORDER extends AbstractGroup {

    public ASTM_MSG_TEST_ORDER(Group parent, ModelClassFactory factory) {
        super(parent, factory);
        this.init(factory);
    }

    private void init(ModelClassFactory factory) {
        try {
            this.add(O.class, true, false, true);
            this.add(R.class, false, true, true);
        } catch (HL7Exception e) {
            log.error("Unexpected error creating ASTM_MSG_PATIENT_INFORMATION.", e);
        }
    }
}
