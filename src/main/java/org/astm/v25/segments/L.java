package org.astm.v25.segments;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.astm.v25.datatypes.ST;

public class L extends AbstractSegment {
    /**
     * Calls the abstract init() method to create the fields in this segment.
     *
     * @param parent  parent group
     * @param factory all implementors need a model class factory to find datatype
     *                classes, so we include it as an arg here to emphasize that
     *                fact ... AbstractSegment doesn't actually use it though
     */
    public L(Group parent, ModelClassFactory factory) {
        super(parent, factory);
        init(factory);
    }

    private void init(ModelClassFactory factory) {
        try {
            this.add(ST.class, true, 1, 5, new Object[]{this.getMessage()}, "Sequence Number");
            this.add(ST.class, true, 1, 10, new Object[] {this.getMessage()}, "Termination Code");

        } catch (HL7Exception e) {
            log.error("Unexpected error creating H.", e);
        }
    }
}
