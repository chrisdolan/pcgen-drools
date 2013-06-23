package net.chrisdolan.pcgen.drools.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("hitpoints")
public class HitpointsInput {
    @XStreamAlias("value")
    @XStreamAsAttribute
    private int value;

    public HitpointsInput() {
    }
    public HitpointsInput(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }

    public String toString() {
        return "Hitpoints[" + value + "]";
    }
}
