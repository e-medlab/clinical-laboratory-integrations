package org.taltech.emedlab.models.astm.v25.segments;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.taltech.emedlab.models.astm.v25.datatypes.*;

public class R extends AbstractSegment {

    public R(Group parent, ModelClassFactory factory) {
        super(parent, factory);
        init(factory);
    }

    private void init(ModelClassFactory factory) {
        try {
            this.add(ST.class, true, 1, 5, new Object[]{this.getMessage()}, "Sequence Number");
            this.add(UniversalTestId.class, true, 1, 255, new Object[]{this.getMessage()}, "Universal Test ID");
            this.add(MeasurementValue.class, true, 1, 255, new Object[]{this.getMessage()}, "Measurement Value");
            this.add(ST.class, false, 0, 255, new Object[]{this.getMessage()}, "Units");
            this.add(ReferenceRange.class, false, 0, 255, new Object[]{this.getMessage()}, "Reference Range");
            this.add(ST.class, false, 1, 2, new Object[]{this.getMessage()}, "Abnormal Flags");
            this.add(ST.class, false, 1, 255, new Object[]{this.getMessage()}, "Nature of Abnormality Testing");
            this.add(ST.class, false, 1, 1, new Object[]{this.getMessage()}, "Result Status");
            this.add(DT.class, false, 1, 255, new Object[]{this.getMessage()}, "Date of Change in Instrument Normative Values or Units");
            this.add(ST.class, false, 1, 255, new Object[]{this.getMessage()}, "Operator Identification");
            this.add(DT.class, false, 1, 255, new Object[]{this.getMessage()}, "Date/Time Test Started");
            this.add(DT.class, false, 1, 255, new Object[]{this.getMessage()}, "Date/Time Test Completed");
            this.add(InstrumentIdentifier.class, false, 1, 255, new Object[]{this.getMessage()}, "Instrument Identification");
        } catch (HL7Exception e) {
            log.error("Unexpected error creating H.", e);
        }
    }
}
