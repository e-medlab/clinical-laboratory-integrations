package org.taltech.emedlab.models.astm.v25.segments;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.taltech.emedlab.models.astm.v25.datatypes.*;
import org.astm.v25.datatypes.*;

public class O extends AbstractSegment {

    public O(Group parent, ModelClassFactory factory) {
        super(parent, factory);
        init(factory);
    }

    private void init(ModelClassFactory factory) {
        try {
            this.add(ST.class, true, 1, 5, new Object[]{this.getMessage()}, "Sequence Number");
            this.add(SpecimenId.class, true, 1, 255, new Object[] {this.getMessage()}, "Specimen ID");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "Instrument Specimen ID");
            this.add(UniversalTestId.class, true, 1, 255, new Object[] {this.getMessage()}, "Universal Test ID");
            this.add(ST.class, true, 1, 1, new Object[] {this.getMessage()}, "Priority");
            this.add(DT.class, true, 1, 255, new Object[] {this.getMessage()}, "Requested/Ordered Date and Time");
            this.add(DT.class, false, 1, 255, new Object[] {this.getMessage()}, "Specimen Collection Date and Time");
            this.add(DT.class, false, 1, 255, new Object[] {this.getMessage()}, "Collection End Time");
            this.add(MeasurementObservation.class, false, 1, 255, new Object[] {this.getMessage()}, "Collection Volume");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "Collector Identifier");
            this.add(ST.class, true, 1, 1, new Object[] {this.getMessage()}, "Specimen Action Code");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "Danger Code");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "Relecant Clinical Information");
            this.add(DT.class, false, 1, 255, new Object[] {this.getMessage()}, "Date/Time Specimen Received");
            this.add(SpecimenDescriptor.class, true, 1, 255, new Object[] {this.getMessage()}, "Specimen Descriptor");
            this.add(UserId.class, false, 1, 255, new Object[] {this.getMessage()}, "Ordering Physician");
            this.add(ST.class, false, 0, 255, new Object[] {this.getMessage()}, "Physician's Telephone Number");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "User Field Number 1");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "User Field Number 2");
            this.add(DT.class, false, 1, 255, new Object[] {this.getMessage()}, "Date/Time Results Reported or Last Modified");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "Instrument Charge to Information System");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "Instrument Section ID");
            this.add(ST.class, false, 0, 255, new Object[] {this.getMessage()}, "Report Types");
            this.add(ST.class, false, 1, 0, new Object[] {this.getMessage()}, "Reserved Field");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "Location of Specimen Collection");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "Nosocomial Infection Flag");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "Specimen Service");
            this.add(ST.class, false, 1, 255, new Object[] {this.getMessage()}, "Specimen Institution");
        } catch (HL7Exception e) {
            log.error("Unexpected error creating H.", e);
        }
    }
}
