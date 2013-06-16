package net.chrisdolan.pcgen.drools.input;

public class LevelInput {
    private String classname;
    /**
     * The ordinal is to track which levels were added first, which matters for prereqs and some bonuses. It doesn't need to be serialized.
     */
    private int ordinal;

    public LevelInput() {
    }
    public LevelInput(String classname) {
        this.classname = classname;
    }
    public String getClassname() {
        return classname;
    }
    public void setClassname(String classname) {
        this.classname = classname;
    }
    public int getOrdinal() {
        return ordinal;
    }
    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }
    public String toString() {
        return "Level[" + classname + "]";
    }
}
