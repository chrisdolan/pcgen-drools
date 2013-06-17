package net.chrisdolan.pcgen.drools.input;

public class DamageInput {
    private int value;

    public DamageInput() {
    }
    public DamageInput(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }

    public String toString() {
        return "Damage[" + value + "]";
    }
}
