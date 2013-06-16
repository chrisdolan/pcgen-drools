package net.chrisdolan.pcgen.drools.input;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConditionInput other = (ConditionInput) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "ConditionInput[" + type + "]";
    }
}
