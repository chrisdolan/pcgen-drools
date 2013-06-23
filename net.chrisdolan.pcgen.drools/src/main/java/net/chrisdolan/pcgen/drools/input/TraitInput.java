package net.chrisdolan.pcgen.drools.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("trait")
public class TraitInput {
    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;

    public TraitInput() {
    }
    public TraitInput(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "Trait[" + name + "]";
    }
}
