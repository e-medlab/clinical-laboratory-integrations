package org.astm.v25.segments;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.astm.v25.datatypes.*;

/*
 * From the spec:
 *
 * NOTE: Only one request record may be outstanding at a time; the receiver of a request record must
 * terminate the request, when finished, by means of the message terminator record, or the sender must
 * cancel the request before sending a second logical request.
 *
 * To me, this implies that ASTM_MSG should only carry a single Q element at most.
 */
public class Q extends AbstractSegment {

    public Q(Group parent, ModelClassFactory factory) {
        super(parent, factory);
        init(factory);
    }

    private void init(ModelClassFactory factory) {
        try {
            this.add(ST.class, true, 1, 5, new Object[]{this.getMessage()}, "Sequence Number");
            this.add(RangeIdNumber.class, false, 1, 255, new Object[] {this.getMessage()}, "Starting Range ID Number");
            this.add(RangeIdNumber.class, false, 1, 255, new Object[] {this.getMessage()}, "Ending Range ID Number");
            this.add(UniversalTestId.class, false, 1, 255, new Object[] {this.getMessage()}, "Universal Test ID");
            this.add(ST.class, false, 1, 1, new Object[] {this.getMessage()}, "Nature of Request Time Limits");
            this.add(DT.class, false, 0, 255, new Object[] {this.getMessage()}, "Beginning Request Results Date and Time");
            this.add(DT.class, false, 1, 255, new Object[] {this.getMessage()}, "Ending Request Results Date and Time");
            this.add(UserId.class, false, 1, 255, new Object[] {this.getMessage()}, "Requesting Physician Name");
            this.add(ST.class, false, 0, 255, new Object[] {this.getMessage()}, "Requesting Physician Telephone Number");
            this.add(ST.class, false, 0, 255, new Object[] {this.getMessage()}, "User Field Number 1");
            this.add(ST.class, false, 0, 255, new Object[] {this.getMessage()}, "User Field Number 2");
            this.add(ST.class, true, 0, 1, new Object[] {this.getMessage()}, "Request Information Status Codes");
        } catch (HL7Exception e) {
            log.error("Unexpected error creating H.", e);
        }
    }
}
