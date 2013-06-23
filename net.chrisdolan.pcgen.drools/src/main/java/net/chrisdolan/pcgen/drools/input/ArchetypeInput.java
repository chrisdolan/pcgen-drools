package net.chrisdolan.pcgen.drools.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("archetype")
public class ArchetypeInput {
    @XStreamAlias("class")
    @XStreamAsAttribute
    private String classname;

    @XStreamAlias("name")
    @XStreamAsAttribute
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
