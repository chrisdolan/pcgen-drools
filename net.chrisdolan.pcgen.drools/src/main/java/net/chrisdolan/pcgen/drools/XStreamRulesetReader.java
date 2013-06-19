package net.chrisdolan.pcgen.drools;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.chrisdolan.pcgen.drools.Ruleset.Rule;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class XStreamRulesetReader implements Ruleset.Reader {

    public Ruleset read(URL url) throws IOException {
        try {
            XStream xstream = new XStream();
            xstream.autodetectAnnotations(true);
            xstream.alias("ruleset", XStreamRuleset.class);
            Object o = xstream.fromXML(url);
            if (!(o instanceof XStreamRuleset))
                throw new IOException("Unmarshalled XML is not a JAXBRuleset, but is: " + o.getClass());
            XStreamRuleset rs = (XStreamRuleset) o;
            if (rs.getRules().isEmpty())
                throw new IOException("No rules found in the ruleset");
            return rs.toRuleset();
        } catch (XStreamException e) {
            throw new IOException(e);
        }
    }

    @XStreamAlias("ruleset")
    public static class XStreamRuleset {
        @XStreamImplicit(itemFieldName="rule")
        private List<XStreamRule> rules = new ArrayList<XStreamRule>();

        List<XStreamRule> getRules() {
            return rules;
        }
        void setRules(List<XStreamRule> rules) {
            this.rules = rules;
        }

        private Ruleset toRuleset() {
            Ruleset ruleset = new Ruleset();
            ArrayList<Rule> r = new ArrayList<Rule>();
            for (XStreamRule rule : rules) {
                r.add(rule.toRule());
            }
            ruleset.setRules(r);
            return ruleset;
        }

        @XStreamAlias("rule")
        public static class XStreamRule {
            @XStreamAlias("name")
            @XStreamAsAttribute
            private String name;

            @XStreamAlias("type")
            @XStreamAsAttribute
            private String type;

            public String getName() {
                return name;
            }
            public void setName(String name) {
                this.name = name;
            }

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
