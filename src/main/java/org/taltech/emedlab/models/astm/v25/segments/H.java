package org.taltech.emedlab.models.astm.v25.segments;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.taltech.emedlab.models.astm.v25.datatypes.ST;
import org.taltech.emedlab.models.astm.v25.datatypes.DT;

public class H extends AbstractSegment {

    public H(Group parent, ModelClassFactory factory) {
        super(parent, factory);
        init(factory);
    }

    private void init(ModelClassFactory factory) {
        try {
            this.add(ST.class, true, 1, 1, new Object[]{this.getMessage()}, "Field Separator");
            this.add(ST.class, true, 1, 3, new Object[] {this.getMessage()}, "Delimiter Definition");
            this.add(ST.class, true, 1, 255, new Object[] {this.getMessage()}, "Message Control ID");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "Access Password");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "Sender Name or ID");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "Sender Street Address");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "Reserved Field");
            this.add(ST.class, false, 40, 255, new Object[] {this.getMessage()}, "Sender Telephone Number");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "Characteristics of Sender");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "Receiver ID");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "Comment or Special Instructions");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "Processing ID");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "Version No");
            this.add(DT.class, false, 1, 255, new Object[] {this.getMessage()}, "Date and Time of Message");
        } catch (HL7Exception e) {
            log.error("Unexpected error creating H.", e);
        }
    }
}
