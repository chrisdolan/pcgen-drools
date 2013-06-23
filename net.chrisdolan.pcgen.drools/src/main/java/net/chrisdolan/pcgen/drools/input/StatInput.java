package net.chrisdolan.pcgen.drools.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("stat")
public class StatInput {
    public static final String TYPE = "Stat";
    public static final String SUBTYPE_BASE = "Base";

    public static final String STR = "Strength";
    public static final String CON = "Constitution";
    public static final String DEX = "Dexterity";
    public static final String INT = "Intelligence";
    public static final String WIS = "Wisdom";
    public static final String CHA = "Charisma";

    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;

    @XStreamAlias("value")
    @XStreamAsAttribute
    private int value;

    public StatInput() {
    }
    public StatInput(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }

    public String toString() {
        return "Stat[" + name + "=" + value + "]";
    }
}
