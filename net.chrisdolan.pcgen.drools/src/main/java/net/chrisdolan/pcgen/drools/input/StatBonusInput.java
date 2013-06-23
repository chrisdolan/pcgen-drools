package net.chrisdolan.pcgen.drools.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("statbonus")
public class StatBonusInput {
    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;

    public StatBonusInput() {
    }
    public StatBonusInput(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "StatBonus[" + name + "]";
    }

}
