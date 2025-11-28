package org.astm.v25.segments;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.astm.v25.datatypes.ST;

public class R extends AbstractSegment {

    public R(Group parent, ModelClassFactory factory) {
        super(parent, factory);
        init(factory);
    }

    private void init(ModelClassFactory factory) {
        try {
            this.add(ST.class, true, 1, 5, new Object[]{this.getMessage()}, "Sequence Number");
        } catch (HL7Exception e) {
            log.error("Unexpected error creating H.", e);
        }
    }
}
