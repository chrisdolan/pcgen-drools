package net.chrisdolan.pcgen.drools.input;

public class AbilityInput {
    private String name;

    public AbilityInput() {
    }
    public AbilityInput(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String toString() {
        return "Ability[" + name + "]";
    }
}
