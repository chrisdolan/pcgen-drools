package net.chrisdolan.pcgen.drools;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.chrisdolan.pcgen.drools.Ruleset.Rule;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

public class XStreamRulesetReader implements Ruleset.Reader {

    public Ruleset read(URI uri) throws IOException {
        try {
            XStream xstream = new XStream();
            xstream.autodetectAnnotations(true);
            xstream.alias("ruleset", Ruleset.class);
            URL url = uri.isAbsolute() ? uri.toURL() : getClass().getResource(uri.toString());
            Object o = xstream.fromXML(url);
            if (!(o instanceof Ruleset))
                throw new IOException("Unmarshalled XML is not a Ruleset, but is: " + o.getClass());
            Ruleset rs = (Ruleset) o;
            rs.setUri(uri);
            //rs = flatten(rs);
            return rs;
        } catch (XStreamException e) {
            throw new IOException(e);
        }
    }

    public Ruleset flatten(Ruleset rs) throws IOException {
        try {
            Set<Rule> rules = new HashSet<Rule>();

            URI baseUri = rs.getUri();
            if (rs.getName() != null) {
                String relUri = rs.getName() + ".xml";
                URI nameURI = baseUri == null ? new URI(relUri) : baseUri.resolve(relUri);
                rules.addAll(flatten(read(nameURI)).getRules());
            }
            for (Ruleset r : rs.getRulesets()) {
                if (baseUri != null)
                    r.setUri(baseUri);
                rules.addAll(flatten(r).getRules());
            }
            for (Rule rule : rs.getRules()) {
                Rule r = new Rule();
                r.setName(rule.getName());
                r.setType(rule.getType());
                r.setBody(rule.getBody());
                if (rule.getName() != null) {
                    String relUri = rule.getName();
                    r.setUri(baseUri == null ? new URI(relUri) : baseUri.resolve(relUri));
                }
                rules.add(r);
            }

            List<Rule> rulelist = new ArrayList<Rule>(rules);
            Collections.sort(rulelist, new Comparator<Rule>() {
                public int compare(Rule o1, Rule o2) {
                    URI uri1 = o1.getUri();
                    URI uri2 = o2.getUri();

                    if (uri1 == null) {
                        if (uri2 == null)
                            return o1.toString().compareTo(o2.toString());
                        else
                            return 1;
                    } else {
                        if (uri2 == null)
                            return -1;
                        else
                            return uri1.compareTo(uri2);
                    }
                }
            });

            Ruleset out = new Ruleset();
            out.setUri(baseUri);
            out.setRules(rulelist);
            return out;
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }
}
