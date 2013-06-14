package net.chrisdolan.pcgen.drools;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={})
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class Ruleset {
    private List<Rule> rules = new ArrayList<Rule>();

    @XmlElement(name = "rule")
    public List<Rule> getRules() {
        return rules;
    }
    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    @XmlType(propOrder={})
    @XmlAccessorType(XmlAccessType.NONE)
    public static class Rule {
        private String name;
        private String type;

        @XmlAttribute(required=true)
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        @XmlAttribute(required=true)
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        
    }
}
