package net.chrisdolan.pcgen.drools.type;

public class Initiative {

    public static final String TYPE = "Initiative";

    public static final String SUBTYPE_DEXTERITY = "Dexterity";
    public static final String SUBTYPE_OTHER = "Other";

    private int value;

    public Initiative() {
    }
    public Initiative(int value) {
        this.value = value;
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
        Initiative other = (Initiative) obj;
        if (value != other.value)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Init[" + value + "]";
    }
}
