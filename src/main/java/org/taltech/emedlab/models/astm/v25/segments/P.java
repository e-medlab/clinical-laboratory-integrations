package org.taltech.emedlab.models.astm.v25.segments;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.taltech.emedlab.models.astm.v25.datatypes.*;
import org.astm.v25.datatypes.*;

public class P extends AbstractSegment {

    public P(Group parent, ModelClassFactory factory) {
        super(parent, factory);
        init(factory);
    }

    private void init(ModelClassFactory factory) {
        try {
            this.add(ST.class, true, 1, 5, new Object[]{this.getMessage()}, "Sequence Number");
            this.add(ST.class, true, 1, 255, new Object[]{this.getMessage()}, "Practice-Assigned Patient ID");
            this.add(ST.class, false, 1, 255, new Object[]{this.getMessage()}, "Laboratory-Assigned Patient ID");
            this.add(ST.class, false, 1, 255, new Object[]{this.getMessage()}, "Patient ID Number 3");
            this.add(UserId.class, true, 1, 255, new Object[]{this.getMessage()}, "Patient Name");
            this.add(ST.class, false, 1, 255, new Object[]{this.getMessage()}, "Mother’s Maiden Name");
            this.add(DT.class, false, 1, 255, new Object[]{this.getMessage()}, "Birthdate");
            this.add(ST.class, false, 1, 1, new Object[]{this.getMessage()}, "Patient Sex");
            this.add(ST.class, false, 1, 2, new Object[]{this.getMessage()}, "Patient Race-Ethnic Origin");
            this.add(Address.class, false, 1, 2, new Object[]{this.getMessage()}, "Patient Address");
            this.add(ST.class, false, 1, 255, new Object[]{this.getMessage()}, "Reserved Field");
            this.add(ST.class, false, 0, 255, new Object[]{this.getMessage()}, "Patient Telephone Number");
            this.add(UserId.class, false, 1, 255, new Object[]{this.getMessage()}, "Attending Physician ID");
            this.add(ST.class, false, 0, 255, new Object[]{this.getMessage()}, "Special Field 1");
            this.add(ST.class, false, 0, 255, new Object[]{this.getMessage()}, "Special Field 2");
            this.add(MeasurementObservation.class, false, 1, 255, new Object[]{this.getMessage()}, "Patient Height");
            this.add(MeasurementObservation.class, false, 1, 255, new Object[]{this.getMessage()}, "Patient Weight");
            this.add(ST.class, false, 0, 255, new Object[]{this.getMessage()}, "Patient’s Known or Suspected Diagnosis");
            this.add(ST.class, false, 0, 255, new Object[]{this.getMessage()}, "Patient Active Medications");
            this.add(ST.class, false, 1, 255, new Object[]{this.getMessage()}, "Patient's Diet");
            this.add(ST.class, false, 1, 255, new Object[]{this.getMessage()}, "Practice Field Number 1");
            this.add(ST.class, false, 1, 255, new Object[]{this.getMessage()}, "Practice Field Number 2");
            this.add(DT.class, false, 2, 255, new Object[]{this.getMessage()}, "Admission and Discharge Dates");
            this.add(ST.class, false, 1, 2, new Object[]{this.getMessage()}, "Admission Status");
            this.add(ST.class, false, 1, 255, new Object[]{this.getMessage()}, "Location");
            this.add(ST.class, false, 1, 255, new Object[]{this.getMessage()}, "Nature of Alternative Diagnostic Code and Classifiers");
            this.add(DiagnosticCode.class, false, 0, 255, new Object[]{this.getMessage()}, "Alternative Diagnostic Code and Classification");
            this.add(ST.class, false, 1, 255, new Object[]{this.getMessage()}, "Patient Religion");
            this.add(ST.class, false, 1, 1, new Object[]{this.getMessage()}, "Marital Status");
            this.add(ST.class, false, 0, 3, new Object[]{this.getMessage()}, "Isolation Status");
            this.add(ST.class, false, 1, 255, new Object[]{this.getMessage()}, "Language");
            this.add(UserId.class, false, 1, 255, new Object[]{this.getMessage()}, "Hospital Service");
            this.add(UserId.class, false, 1, 255, new Object[]{this.getMessage()}, "Hospital Institution");
            this.add(ST.class, false, 1, 255, new Object[]{this.getMessage()}, "Dosage Category");
        } catch (HL7Exception e) {
            log.error("Unexpected error creating H.", e);
        }
    }
}
