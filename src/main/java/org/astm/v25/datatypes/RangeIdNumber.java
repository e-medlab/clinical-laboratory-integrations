package org.astm.v25.datatypes;

import ca.uhn.hl7v2.model.AbstractComposite;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;

/*
 * Range ID Number
 * 11.3 in the LIS2-A2 specification
 */
public class RangeIdNumber extends AbstractComposite {
    private Type[] data;

    public RangeIdNumber(Message message) {
        super(message);
        this.init();
    }

    private void init() {
        this.data = new Type[3];
        this.data[0] = new ST(this.getMessage()); // Patient ID number or ALL
        this.data[1] = new ST(this.getMessage()); // Specimen ID number
        this.data[2] = new ST(this.getMessage()); // Manufacturer selection criteria
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
