package net.chrisdolan.pcgen.drools.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("action")
public class ActionInput {
    public static final String TYPE_CHARGE = "Charge";
    public static final String TYPE_LUNGE = "Lunge";
    public static final String TYPE_FIGHTING_DEFENSIVELY = "FightingDefensively";
    public static final String TYPE_TOTAL_DEFENSE = "TotalDefense";

    @XStreamAlias("type")
    @XStreamAsAttribute
    private String type;

    public ActionInput() {
    }
    public ActionInput(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return "ActionInput[" + type + "]";
    }
}
