package net.chrisdolan.pcgen.drools.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("input")
public class PCInput {
    // Permanent qualities
    @XStreamImplicit(itemFieldName="property")
    private List<PropertyInput> properties = new ArrayList<PropertyInput>();
    @XStreamAlias("alignment")
    private AlignmentInput alignment;
    @XStreamImplicit(itemFieldName="stat")
    private List<StatInput> stats = new ArrayList<StatInput>();
    @XStreamAlias("race")
    private RaceInput race;
    @XStreamImplicit(itemFieldName="level")
    private List<LevelInput> levels = new ArrayList<LevelInput>();
    @XStreamAlias("inventory")
    private InventoryInput inventory;

    // Transient qualities
    // todo: move these to a separate top-level element?
    @XStreamImplicit(itemFieldName="condition")
    private List<ConditionInput> conditions = new ArrayList<ConditionInput>();
    @XStreamImplicit(itemFieldName="damage")
    private List<DamageInput> damage = new ArrayList<DamageInput>();
    @XStreamImplicit(itemFieldName="action")
    private List<ActionInput> actions = new ArrayList<ActionInput>();

    public List<PropertyInput> getProperties() {
        return properties == null ? Collections.<PropertyInput>emptyList() : new ArrayList<PropertyInput>(properties);
    }
    public void setProperties(List<PropertyInput> properties) {
        this.properties = new ArrayList<PropertyInput>(properties);
    }
    public AlignmentInput getAlignment() {
        return alignment;
    }
    public void setAlignment(AlignmentInput alignment) {
        this.alignment = alignment;
    }
    public List<StatInput> getStats() {
        return stats == null ? Collections.<StatInput>emptyList() : new ArrayList<StatInput>(stats);
    }
    public void setStats(List<StatInput> stats) {
        this.stats = new ArrayList<StatInput>(stats);
    }
    public RaceInput getRace() {
        return race;
    }
    public void setRace(RaceInput race) {
        this.race = race;
    }
    public List<LevelInput> getLevels() {
        return levels == null ? Collections.<LevelInput>emptyList() : new ArrayList<LevelInput>(levels);
    }
    public void setLevels(List<LevelInput> levels) {
        this.levels = new ArrayList<LevelInput>(levels);
    }
    public InventoryInput getInventory() {
        return inventory;
    }
    public void setInventory(InventoryInput inventory) {
        this.inventory = inventory;
    }
    public List<ConditionInput> getConditions() {
        return conditions == null ? Collections.<ConditionInput>emptyList() : new ArrayList<ConditionInput>(conditions);
    }
    public void setConditions(List<ConditionInput> conditions) {
        this.conditions = new ArrayList<ConditionInput>(conditions);
    }
    public List<DamageInput> getDamage() {
        return damage == null ? Collections.<DamageInput>emptyList() : new ArrayList<DamageInput>(damage);
    }
    public void setDamage(List<DamageInput> damage) {
        this.damage = new ArrayList<DamageInput>(damage);
    }
    public List<ActionInput> getActions() {
        return actions == null ? Collections.<ActionInput>emptyList() : new ArrayList<ActionInput>(actions);
    }
    public void setActions(List<ActionInput> actions) {
        this.actions = new ArrayList<ActionInput>(actions);
    }

    public String toString() {
        return "PCInput[...]";
    }

    private Object readResolve() {
        if (levels != null) {
            int numLevels = 0;
            Map<String,Integer> classCount = new HashMap<String, Integer>();
            for (LevelInput levelInput : levels) {
                levelInput.setOrdinal(++numLevels);
                Integer count = classCount.get(levelInput.getClassname());
                if (count == null)
                    count = 0;
                levelInput.setClassOrdinal(count+1);
                classCount.put(levelInput.getClassname(), count+1);
            }
        }
        return this;
    }
}
