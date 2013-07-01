package net.chrisdolan.pcgen.drools.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("language")
public class LanguageInput {
    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;

    public LanguageInput() {
    }
    public LanguageInput(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "Language[" + name + "]";
    }

}
