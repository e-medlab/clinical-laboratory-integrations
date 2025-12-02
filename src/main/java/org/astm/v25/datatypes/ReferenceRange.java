package org.astm.v25.datatypes;

import ca.uhn.hl7v2.model.AbstractComposite;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;

/*
 * Reference Range
 * 9.6 in the LIS2-A2 specification
 */
public class ReferenceRange extends AbstractComposite {
    private Type[] data;

    public ReferenceRange(Message message) {
        super(message);
        this.init();
    }

    private void init() {
        this.data = new Type[2];
        this.data[0] = new ST(this.getMessage()); // Reference range in the following sample format: (lower limit to upper limit; i.e., 3.5 to 4.5)
        this.data[1] = new ST(this.getMessage()); // Reference range description
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
