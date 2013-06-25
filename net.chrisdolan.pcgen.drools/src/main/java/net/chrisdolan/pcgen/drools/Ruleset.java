package net.chrisdolan.pcgen.drools;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("ruleset")
public class Ruleset {

    public interface Reader {
        Ruleset read(URI url) throws IOException;
        Ruleset flatten(Ruleset rs) throws IOException;
    }

    @XStreamOmitField
    private URI uri;
    
    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;

    @XStreamImplicit(itemFieldName="ruleset")
    private List<Ruleset> rulesets = new ArrayList<Ruleset>();

    @XStreamImplicit(itemFieldName="rule")
    private List<Rule> rules = new ArrayList<Rule>();

    public Ruleset() {
    }
    public Ruleset(String name) {
        this.name = name;
    }
    public Ruleset(List<Ruleset> rulesets) {
        this.rulesets = new ArrayList<Ruleset>(rulesets);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Ruleset> getRulesets() {
        return rulesets == null ? Collections.<Ruleset>emptyList() : rulesets;
    }
    public void setRulesets(List<Ruleset> rulesets) {
        this.rulesets = rulesets;
    }
    public List<Rule> getRules() {
        return rules == null ? Collections.<Rule>emptyList() : rules;
    }
    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public URI getUri() {
        return uri;
    }
    public void setUri(URI uri) {
        this.uri = uri;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((rules == null) ? 0 : rules.hashCode());
        result = prime * result
                + ((rulesets == null) ? 0 : rulesets.hashCode());
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Ruleset other = (Ruleset) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (rules == null) {
            if (other.rules != null)
                return false;
        } else if (!rules.equals(other.rules))
            return false;
        if (rulesets == null) {
            if (other.rulesets != null)
                return false;
        } else if (!rulesets.equals(other.rulesets))
            return false;
        if (uri == null) {
            if (other.uri != null)
                return false;
        } else if (!uri.equals(other.uri))
            return false;
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ruleset[");
        if (name != null)
            sb.append(" name=").append(name);
        if (rulesets != null && !rulesets.isEmpty())
            sb.append(" rulesets=").append(rulesets);
        if (rules != null && !rules.isEmpty())
            sb.append(" rules=").append(rules);
        sb.append(" ]");
        return sb.toString();
    }

    @XStreamAlias("rule")
    public static class Rule {
        @XStreamOmitField
        private URI uri;

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

        public URI getUri() {
            return uri;
        }
        public void setUri(URI uri) {
            this.uri = uri;
        }
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((type == null) ? 0 : type.hashCode());
            result = prime * result + ((uri == null) ? 0 : uri.hashCode());
            return result;
        }
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Rule other = (Rule) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            if (type == null) {
                if (other.type != null)
                    return false;
            } else if (!type.equals(other.type))
                return false;
            if (uri == null) {
                if (other.uri != null)
                    return false;
            } else if (!uri.equals(other.uri))
                return false;
            return true;
        }

        public String toString() {
            return "Rule[name=" + name + ", type=" + type + "]";
        }
    }
}
