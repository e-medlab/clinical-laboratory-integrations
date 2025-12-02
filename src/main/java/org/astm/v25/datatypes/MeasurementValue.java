package org.astm.v25.datatypes;

import ca.uhn.hl7v2.model.AbstractComposite;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;

public class MeasurementValue extends AbstractComposite {
    private Type[] data;

    public MeasurementValue(Message message) {
        super(message);
        this.init();
    }

    private void init() {
        this.data = new Type[2];
        this.data[0] = new ST(this.getMessage()); // Numeric, text or coded value of equal stature to rest of the components
        this.data[1] = new ST(this.getMessage()); // Numeric, text or coded value of equal stature to rest of the components
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
