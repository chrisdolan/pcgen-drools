package net.chrisdolan.pcgen.drools.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("favoredclassbonus")
public class FavoredClassBonusInput {
    public static final String HITPOINT = "Hitpoint";
    public static final String SKILLPOINT = "SkillPoint";

    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;

    public FavoredClassBonusInput() {
    }
    public FavoredClassBonusInput(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "FavoredClassBonus[" + name + "]";
    }
}
