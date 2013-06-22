package net.chrisdolan.pcgen.drools.input;

public class RaceInput {
    public static final String DWARF = "Dwarf";
    public static final String ELF = "Elf";
    public static final String GNOME = "Gnome";
    public static final String HALFELF = "Half-elf";
    public static final String HALFLING = "Halfling";
    public static final String HALFORC = "Half-orc";
    public static final String HUMAN = "Human";

    private String name;

    public RaceInput() {
    }
    public RaceInput(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String toString() {
        return "Race[" + name + "]";
    }
}
