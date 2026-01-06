package org.taltech.emedlab.models.astm.v25.datatypes;

import ca.uhn.hl7v2.model.AbstractComposite;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;

/*
 * Universal Test ID
 * 5.6.1 in the LIS2-A2 specification
 */
public class UniversalTestId extends AbstractComposite {
    private Type[] data;

    public UniversalTestId(Message message) {
        super(message);
        this.init();
    }

    private void init() {
        this.data = new Type[7];
        this.data[0] = new ST(this.getMessage()); // Universal test identifier (LOINC code), reserved for future use by spec
        this.data[1] = new ST(this.getMessage()); // Test or battery name
        this.data[2] = new ST(this.getMessage()); // Coding scheme
        this.data[3] = new ST(this.getMessage()); // Code(s) defined by the manufacturer
        this.data[4] = new ST(this.getMessage()); // Code(s) defined by the manufacturer
        this.data[5] = new ST(this.getMessage()); // Code(s) defined by the manufacturer
        this.data[6] = new ST(this.getMessage()); // Code(s) defined by the manufacturer
        // possibly more components, depending on the implementation of the equipment vendor
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
