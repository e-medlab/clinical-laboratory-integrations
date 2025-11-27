package org.astm.v25;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.Primitive;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.astm.v25.groups.ASTM_MSG_PATIENT_INFORMATION;
import org.astm.v25.segments.H;
import org.astm.v25.segments.L;
import org.astm.v25.segments.Q;

public class ASTM_MSG extends AbstractMessage {

    public ASTM_MSG(ModelClassFactory factory) {
        super(factory);
        this.init(factory);
    }

    private void init(ModelClassFactory factory) {
        try {
            this.add(H.class, true, false);
            this.add(ASTM_MSG_PATIENT_INFORMATION.class, false, true);
            this.add(Q.class, false, false);
            this.add(L.class, true, false);
        } catch (HL7Exception e) {
            log.error("Unexpected error creating ASTM_MSG.", e);
        }
    }

    @Override
    public String getVersion() {
        return "2.5";
    }

    @Override
    public String getEncodingCharactersValue() throws HL7Exception {
        Segment h = (Segment) get("H");
        Primitive value = (Primitive) h.getField(2, 0);

        return value.getValue() + "~"; // TODO: tilde added as a hack, v2 requires subcomponent separator not present in ASTM
    }

    @Override
    public Character getFieldSeparatorValue() throws HL7Exception {
        Segment h = (Segment) get("H");
        Primitive value = (Primitive) h.getField(1, 0);

        return value.getValue().charAt(0);
    }
}
