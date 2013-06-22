package net.chrisdolan.pcgen.drools.test;

public class SpecialTestInput {

    private String type;
    private String subtype;
    private int value;
    
    public SpecialTestInput() {
    }
    public SpecialTestInput(String type, String subtype, int value) {
        this.type = type;
        this.subtype = subtype;
        this.value = value;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getSubtype() {
        return subtype;
    }
    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SpecialTestInput[" + type + "/" + subtype + "=" + value + "]";
    }

}
