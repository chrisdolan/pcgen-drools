package net.chrisdolan.pcgen.drools.input;

import java.util.List;

import net.chrisdolan.pcgen.drools.Ruleset;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("pc")
public class PC {
    @XStreamImplicit(itemFieldName="ruleset")
    private List<Ruleset> rulesets;
    @XStreamAlias("input")
    private PCInput input;
    //@XStreamAlias("derived")
    @XStreamOmitField // todo: remove this when I finish implementing PCDerived!
    private PCDerived derived;

    public List<Ruleset> getRulesets() {
        return rulesets;
    }
    public void setRulesets(List<Ruleset> rulesets) {
        this.rulesets = rulesets;
    }
    public PCInput getInput() {
        return input;
    }
    public void setInput(PCInput input) {
        this.input = input;
    }
    public PCDerived getDerived() {
        return derived;
    }
    public void setDerived(PCDerived derived) {
        this.derived = derived;
    }
}
