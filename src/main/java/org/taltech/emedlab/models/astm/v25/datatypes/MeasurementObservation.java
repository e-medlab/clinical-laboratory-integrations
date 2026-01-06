package org.taltech.emedlab.models.astm.v25.datatypes;

import ca.uhn.hl7v2.model.AbstractComposite;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;

/*
 * Fixed Measurements and Units
 * 5.6.4 in the LIS2-A2 specification
 */
public class MeasurementObservation extends AbstractComposite {
    private Type[] data;

    public MeasurementObservation(Message message) {
        super(message);
        this.init();
    }

    private void init() {
        this.data = new Type[2];
        this.data[0] = new NM(this.getMessage()); // Numeric measurement value
        this.data[1] = new ST(this.getMessage()); // Measurement unit, empty if default
    }

    @Override
    public Type[] getComponents() {
        return new Type[0];
    }

    @Override
    public Type getComponent(int number) throws DataTypeException {
        return null;
    }
}
