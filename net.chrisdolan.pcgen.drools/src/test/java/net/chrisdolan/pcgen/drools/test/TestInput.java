package net.chrisdolan.pcgen.drools.test;

public class TestInput {

    private String type;
    private String subtype;
    private int value;
    
    public TestInput() {
    }
    public TestInput(String type, String subtype, int value) {
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
        return "TestInput[" + type + "/" + subtype + "=" + value + "]";
    }

}
