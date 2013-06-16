package net.chrisdolan.pcgen.drools.input;

public class ArchetypeInput {
    private String classname;
    private String name;

    public ArchetypeInput() {
    }
    public ArchetypeInput(String classname, String name) {
        this.classname = classname;
        this.name = name;
    }

    public String getClassname() {
        return classname;
    }
    public void setClassname(String classname) {
        this.classname = classname;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "Archetype[" + classname + "." + name+ "]";
    }
}
