package net.chrisdolan.pcgen.drools;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

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

    private Object readResolve() {
        // Validate
        boolean hasName = name != null;
        boolean hasChildren = (rules != null && !rules.isEmpty()) || (rulesets != null && !rulesets.isEmpty());
        if (hasName && hasChildren)
            throw new IllegalStateException("rulesets cannot have both a name and rule/ruleset children");
        if (!hasName && !hasChildren)
            throw new IllegalStateException("rulesets must have either a name or rule/ruleset children");
        return this;
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
    @XStreamConverter(value=ToAttributedValueConverter.class, strings={"body"})
    public static class Rule {
        @XStreamOmitField
        private URI uri;

        @XStreamAlias("name")
        @XStreamAsAttribute
        private String name;

        @XStreamAlias("type")
        @XStreamAsAttribute
        private String type;

        private String body;

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

        public String getBody() {
            return body;
        }
        public void setBody(String body) {
            this.body = body;
        }

        public URI getUri() {
            return uri;
        }
        public void setUri(URI uri) {
            this.uri = uri;
        }

        private Object readResolve() {
            // Validate
            boolean hasName = name != null;
            boolean hasBody = body != null;
            if (hasName && hasBody)
                throw new IllegalStateException("rules cannot have both a name and a body");
            if (!hasName && !hasBody)
                throw new IllegalStateException("rules must have either a name or a body");
            return this;
        }

        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((type == null) ? 0 : type.hashCode());
            result = prime * result + ((body == null) ? 0 : body.hashCode());
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
            if (body == null) {
                if (other.body != null)
                    return false;
            } else if (!body.equals(other.body))
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
            sb.append("Rule[");
            if (name != null)
                sb.append(" name=").append(name);
            sb.append(" type=").append(type);
            if (body != null)
                sb.append(" body=").append(hash(body));
            sb.append(" ]");
            return sb.toString();
        }
        private String hash(String s) {
            try {
                byte[] digest = MessageDigest.getInstance("MD5").digest(s.getBytes(Charset.forName("UTF-8")));
                StringBuilder sb = new StringBuilder();
                for (byte b : digest) {
                    String hexString = Integer.toHexString(b);
                    if (hexString.length() == 1)
                        sb.append('0');
                    sb.append(hexString);
                }
                return sb.toString();
            } catch (NoSuchAlgorithmException e) {
                return s;
            } 
        }
    }
}
