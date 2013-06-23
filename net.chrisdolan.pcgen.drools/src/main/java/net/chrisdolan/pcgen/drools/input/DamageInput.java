package net.chrisdolan.pcgen.drools.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("damage")
public class DamageInput {
    @XStreamAlias("value")
    @XStreamAsAttribute
    private int value;

    public DamageInput() {
    }
    public DamageInput(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }

    public String toString() {
        return "Damage[" + value + "]";
    }
}
