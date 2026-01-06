package org.taltech.emedlab.models.astm.v25.datatypes;

import ca.uhn.hl7v2.model.AbstractComposite;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;

/*
 * Provider and User IDs
 * 5.6.6 in the LIS2-A2 specification
 */
public class UserId extends AbstractComposite {
    private Type[] data;

    public UserId(Message message) {
        super(message);
        this.init();
    }

    private void init() {
        this.data = new Type[5];
        this.data[0] = new ST(this.getMessage()); // Last name
        this.data[1] = new ST(this.getMessage()); // First name
        this.data[2] = new ST(this.getMessage()); // Middle initial or name
        this.data[3] = new ST(this.getMessage()); // Suffix
        this.data[4] = new ST(this.getMessage()); // Title
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
