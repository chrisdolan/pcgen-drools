package net.chrisdolan.pcgen.drools.input;

public class HitpointsInput {
    private int value;

    public HitpointsInput() {
    }
    public HitpointsInput(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }

    public String toString() {
        return "Hitpoints[" + value + "]";
    }
}
