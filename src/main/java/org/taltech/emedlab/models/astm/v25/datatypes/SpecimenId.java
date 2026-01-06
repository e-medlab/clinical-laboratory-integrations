package org.taltech.emedlab.models.astm.v25.datatypes;

import ca.uhn.hl7v2.model.AbstractComposite;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;

public class SpecimenId extends AbstractComposite {
    private Type[] data;

    public SpecimenId(Message message) {
        super(message);
        this.init();
    }

    private void init() {
        this.data = new Type[3];
        this.data[0] = new ST(this.getMessage()); // Specimen ID
        this.data[1] = new ST(this.getMessage()); // Specimen ID component 1
        this.data[2] = new ST(this.getMessage()); // Specimen ID component 2
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
