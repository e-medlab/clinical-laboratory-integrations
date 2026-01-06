package org.taltech.emedlab.models.astm.v25.datatypes;

import ca.uhn.hl7v2.model.AbstractComposite;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;

/*
 * Instrument Identification
 * 9.14 in the LIS2-A2 specification
 */
public class InstrumentIdentifier extends AbstractComposite {
    private Type[] data;

    public InstrumentIdentifier(Message message) {
        super(message);
        this.init();
    }

    private void init() {
        this.data = new Type[6];
        this.data[0] = new ST(this.getMessage()); // Instrument identifier
        this.data[1] = new ST(this.getMessage()); // Instrument section identifier
        this.data[2] = new ST(this.getMessage()); // Instrument section identifier
        this.data[3] = new ST(this.getMessage()); // Instrument section identifier
        this.data[4] = new ST(this.getMessage()); // Instrument section identifier
        this.data[5] = new ST(this.getMessage()); // Instrument section identifier
    }

    @Override
    public Type[] getComponents() { return this.data; }

    @Override
    public Type getComponent(int number) throws DataTypeException {
        try {
            return this.data[number];
        } catch (ArrayIndexOutOfBoundsException var3) {
            throw new DataTypeException("Element " + number + " doesn't exist (Type " + this.getClass().getName() + " has only " + this.data.length + " components)");
        }
    }
}
