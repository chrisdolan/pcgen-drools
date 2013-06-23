package net.chrisdolan.pcgen.drools.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("favoredclass")
public class FavoredClassInput {
    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;

    public FavoredClassInput() {
    }
    public FavoredClassInput(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "FavoredClass[" + name + "]";
    }
}
