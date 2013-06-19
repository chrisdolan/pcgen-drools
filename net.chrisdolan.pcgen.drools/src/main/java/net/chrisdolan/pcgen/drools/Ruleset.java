package net.chrisdolan.pcgen.drools;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Ruleset {

    public interface Reader {
        Ruleset read(URL url) throws IOException;
    }

    private List<Rule> rules = new ArrayList<Rule>();

    public List<Rule> getRules() {
        return rules;
    }
    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public static class Rule {
        private String name;
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
    }
}
