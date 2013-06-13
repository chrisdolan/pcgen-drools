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

    public static final String ACTYPE_NORMAL = "Normal";
    public static final String ACTYPE_TOUCH = "Touch";

    private String acType;
    private int value;

    public ArmorClass() {
    }
    public ArmorClass(String acType, int value) {
        this.acType = acType;
        this.value = value;
    }
    public String getAcType() {
        return acType;
    }
    public void setAcType(String actype) {
        this.acType = actype;
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
        result = prime * result + ((acType == null) ? 0 : acType.hashCode());
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
        if (acType == null) {
            if (other.acType != null)
                return false;
        } else if (!acType.equals(other.acType))
            return false;
        if (value != other.value)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AC[" + acType + "=" + value + "]";
    }
}
