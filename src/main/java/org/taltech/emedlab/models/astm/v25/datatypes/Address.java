package org.taltech.emedlab.models.astm.v25.datatypes;

import ca.uhn.hl7v2.model.AbstractComposite;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;

/*
 * Range ID Number
 * 5.6.5 in the LIS2-A2 specification
 */
public class Address extends AbstractComposite {
    private Type[] data;

    public Address(Message message) {
        super(message);
        this.init();
    }

    private void init() {
        this.data = new Type[5];
        this.data[0] = new ST(this.getMessage()); // Street address
        this.data[1] = new ST(this.getMessage()); // City
        this.data[2] = new ST(this.getMessage()); // State
        this.data[3] = new ST(this.getMessage()); // Zip or postal code
        this.data[4] = new ST(this.getMessage()); // Country code
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
