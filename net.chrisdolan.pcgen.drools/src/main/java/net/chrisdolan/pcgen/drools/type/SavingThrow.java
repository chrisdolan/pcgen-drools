package net.chrisdolan.pcgen.drools.type;

public class SavingThrow {

    public static final String TYPE = "SavingThrow";
    public static final String SUBTYPE_ABILITY = "Ability";

    public static final String FORT = "Fortitude";
    public static final String REFL = "Reflex";
    public static final String WILL = "Will";

    public static final String ALL = "All";

    private String type;
    private int value;

    public SavingThrow() {
    }
    public SavingThrow(String type, int value) {
        this.type = type;
        this.value = value;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
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
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        SavingThrow other = (SavingThrow) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (value != other.value)
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "SavingThrow[" + type + "=" + value + "]";
    }
}
