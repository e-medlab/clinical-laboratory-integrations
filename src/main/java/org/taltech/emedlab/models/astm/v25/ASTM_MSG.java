package org.taltech.emedlab.models.astm.v25;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.Primitive;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.taltech.emedlab.models.astm.v25.groups.ASTM_MSG_PATIENT_INFORMATION;
import org.taltech.emedlab.models.astm.v25.segments.H;
import org.taltech.emedlab.models.astm.v25.segments.L;
import org.taltech.emedlab.models.astm.v25.segments.Q;

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

    // A dummy version is necessary to bypass version validation
    @Override
    public String getVersion() {
        return "2.5";
    }

    @Override
    public String getEncodingCharactersValue() throws HL7Exception {
        Segment h = (Segment) get("H");
        Primitive value = (Primitive) h.getField(2, 0);

        // ASTM message contains the repeat delimiter first, then component delimiter
        // HL7 expects component delimiter first, then repeat delimiter
        // This is why we swap them here
        String original = value.getValue();
        String swapped = "" + original.charAt(1) + original.charAt(0) + original.substring(2);

        // tilde added as a hack, v2 requires subcomponent separator not present in ASTM at all
        return swapped + "~";
    }

    @Override
    public Character getFieldSeparatorValue() throws HL7Exception {
        Segment h = (Segment) get("H");
        Primitive value = (Primitive) h.getField(1, 0);

        return value.getValue().charAt(0);
    }
}
