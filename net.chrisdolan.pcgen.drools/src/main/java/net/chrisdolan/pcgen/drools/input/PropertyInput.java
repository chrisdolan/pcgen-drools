package net.chrisdolan.pcgen.drools.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("property")
public class PropertyInput {
    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;

    @XStreamAlias("value")
    @XStreamAsAttribute
    private String value;

    public PropertyInput() {
    }
    public PropertyInput(String name, String value) {
        this.name = name;
        this.value = value;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return "Property[" + name + "=" + value + "]";
    }
}
