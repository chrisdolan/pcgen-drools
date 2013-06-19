package net.chrisdolan.pcgen.drools;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.chrisdolan.pcgen.drools.Ruleset.Rule;

public class JAXBRulesetReader implements Ruleset.Reader {

    public Ruleset read(URL url) throws IOException {
        try {
            Object o = JAXBContext.newInstance(JAXBRuleset.class, JAXBRuleset.JAXBRule.class).createUnmarshaller().unmarshal(url.openStream());
            if (!(o instanceof JAXBRuleset))
                throw new IOException("Unmarshalled XML is not a JAXBRuleset, but is: " + o.getClass());
            JAXBRuleset rs = (JAXBRuleset) o;
            if (rs.getRules().isEmpty())
                throw new IOException("No rules found in the ruleset");
            return rs.toRuleset();
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    @XmlType(propOrder={})
    @XmlAccessorType(XmlAccessType.NONE)
    @XmlRootElement(name = "ruleset")
    public static class JAXBRuleset {
        private List<JAXBRule> rules = new ArrayList<JAXBRule>();


        @XmlElement(name = "rule")
        List<JAXBRule> getRules() {
            return rules;
        }
        void setRules(List<JAXBRule> rules) {
            this.rules = rules;
        }

        private Ruleset toRuleset() {
            Ruleset ruleset = new Ruleset();
            ArrayList<Rule> r = new ArrayList<Rule>();
            for (JAXBRule rule : rules) {
                r.add(rule.toRule());
            }
            ruleset.setRules(r);
            return ruleset;
        }

        @XmlType(propOrder={})
        @XmlAccessorType(XmlAccessType.NONE)
        public static class JAXBRule {
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

            private Rule toRule() {
                Rule rule = new Rule();
                rule.setName(name);
                rule.setType(type);
                return rule;
            }
        }
    }
}
