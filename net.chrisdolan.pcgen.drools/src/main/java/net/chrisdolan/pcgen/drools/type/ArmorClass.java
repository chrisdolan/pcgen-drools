package net.chrisdolan.pcgen.drools.type;

public class ArmorClass {

    public static final String TYPE = "ArmorClass";

    public static final String SUBTYPE_BASE = "Base";
    public static final String SUBTYPE_ARMOR = "Armor";
    public static final String SUBTYPE_SHIELD = "Shield";
    public static final String SUBTYPE_DEFLECTION = "Deflection";
    public static final String SUBTYPE_DEXTERITY = "Dexterity";
    public static final String SUBTYPE_NATURAL = "Natural";
    public static final String SUBTYPE_DODGE = "Dodge";
    public static final String SUBTYPE_SIZE = "Size";
    public static final String SUBTYPE_OTHER = "Other";

    public static final String SUBTYPE_CIRCUMSTANCE = "Circumstance";
    public static final String SUBTYPE_INSIGHT = "Insight";
    public static final String SUBTYPE_MORALE = "Morale";
    public static final String SUBTYPE_PROFANE = "Profane";
    public static final String SUBTYPE_SACRED = "Sacred";

    public static final String ACTYPE_NORMAL = "Normal";
    public static final String ACTYPE_TOUCH = "Touch";
    public static final String ACTYPE_RANGE = "Range";
    public static final String ACTYPE_MELEE = "Melee";
    public static final String ACTYPE_RANGE_TOUCH = "RangeTouch";
    public static final String ACTYPE_MELEE_TOUCH = "MeleeTouch";

    private String actype;
    private int value;

    public ArmorClass() {
    }
    public ArmorClass(String actype, int value) {
        this.actype = actype;
        this.value = value;
    }
    public String getActype() {
        return actype;
    }
    public void setActype(String actype) {
        this.actype = actype;
    }
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((actype == null) ? 0 : actype.hashCode());
        result = prime * result + value;
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
        ArmorClass other = (ArmorClass) obj;
        if (actype == null) {
            if (other.actype != null)
                return false;
        } else if (!actype.equals(other.actype))
            return false;
        if (value != other.value)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AC[" + actype + "=" + value + "]";
    }
}
