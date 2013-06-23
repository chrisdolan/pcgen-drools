package net.chrisdolan.pcgen.drools.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("feat")
public class FeatInput {
    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;

    @XStreamAlias("source")
    @XStreamAsAttribute
    private String source;

    @XStreamAlias("type")
    @XStreamAsAttribute
    private String type;

    public FeatInput() {
    }
    public FeatInput(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String toString() {
        return "Feat[" + name +
                (source == null ? "" : "/source=" + source) +
                (type == null ? "" : "/type=" + type) +
                "]";
    }

}
