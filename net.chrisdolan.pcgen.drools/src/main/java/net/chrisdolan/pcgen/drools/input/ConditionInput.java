package net.chrisdolan.pcgen.drools.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("condition")
public class ConditionInput {
    public static final String TYPE_BLEED = "Bleed";
    public static final String TYPE_BLINDED = "Blinded";
    public static final String TYPE_BROKEN = "Broken";
    public static final String TYPE_CONFUSED = "Confused";
    public static final String TYPE_COWERING = "Cowering";
    public static final String TYPE_DAZED = "Dazed";
    public static final String TYPE_DAZZLED = "Dazzled";
    public static final String TYPE_DEAD = "Dead";
    public static final String TYPE_DEAFENED = "Deafened";
    public static final String TYPE_DISABLED = "Disabled";
    public static final String TYPE_DYING = "Dying";
    public static final String TYPE_ENERGYDRAINED = "EnergyDrained";
    public static final String TYPE_ENTANGLED = "Entangled";
    public static final String TYPE_EXHAUSTED = "Exhausted";
    public static final String TYPE_FASCINATED = "Fascinated";
    public static final String TYPE_FATIGUED = "Fatigued";
    public static final String TYPE_FLATFOOTED = "FlatFooted";
    public static final String TYPE_FRIGHTENED = "Frightened";
    public static final String TYPE_GRAPPLED = "Grappled";
    public static final String TYPE_HELPLESS = "Helpless";
    public static final String TYPE_INCORPOREAL = "Incorporeal";
    public static final String TYPE_INVISIBLE = "Invisible";
    public static final String TYPE_NAUSEATED = "Nauseated";
    public static final String TYPE_PANICKED = "Panicked";
    public static final String TYPE_PARALYZED = "Paralyzed";
    public static final String TYPE_PETRIFIED = "Petrified";
    public static final String TYPE_PINNED = "Pinned";
    public static final String TYPE_PRONE = "Prone";
    public static final String TYPE_SHAKEN = "Shaken";
    public static final String TYPE_SICKENED = "Sickened";
    public static final String TYPE_STABLE = "Stable";
    public static final String TYPE_STAGGERED = "Staggered";
    public static final String TYPE_STUNNED = "Stunned";
    public static final String TYPE_UNCONSCIOUS = "Unconscious";

    public static final String TYPE_RAGE = "Rage";

    @XStreamAlias("type")
    @XStreamAsAttribute
    private String type;

    public ConditionInput() {
    }
    public ConditionInput(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return "ConditionInput[" + type + "]";
    }
}
