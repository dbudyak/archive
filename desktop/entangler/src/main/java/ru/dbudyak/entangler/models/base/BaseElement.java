package ru.dbudyak.entangler.models.base;

import ru.dbudyak.entangler.Data;


/**
 * Created by dbudyak on 06.05.14.
 */
public class BaseElement {

    public final static int OUT = 1;
    public final static int IN = 0;
    public final static int NONE = -1;
    private ElementType elType;
    private Data in;
    private Data out;

    public BaseElement(ElementType type) {
        this.setElementType(type);
    }

    public Data getIn() {
        return in;
    }

    public void setIn(Data in) {
        this.in = in;
    }

    public Data getOut() {
        return out;
    }

    public void setOut(Data out) {
        this.out = out;
    }

    public ElementType getElementType() {
        return this.elType;
    }

    public void setElementType(ElementType type) {
        this.elType = type;

    }

    public enum ElementType {MIRROR, BS, SOURCE, DETECTOR, PHASE_SHIFTER, WAVEGUIDE}


}
