package net.chrisdolan.pcgen.drools.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("level")
public class LevelInput {
    @XStreamAlias("class")
    @XStreamAsAttribute
    private String classname;

    @XStreamImplicit(itemFieldName="archetype")
    private List<ArchetypeInput> archetypes = new ArrayList<ArchetypeInput>();

    @XStreamAlias("hitpoints")
    private HitpointsInput hitpoints;

    @XStreamImplicit(itemFieldName="skill")
    private List<SkillInput> skills = new ArrayList<SkillInput>();

    @XStreamAlias("favoredclassbonus")
    private FavoredClassBonusInput favoredClassBonus;

    @XStreamImplicit(itemFieldName="feat")
    private List<FeatInput> feats = new ArrayList<FeatInput>();

    @XStreamAlias("statbonus")
    private StatBonusInput statBonus;

    @XStreamImplicit(itemFieldName="language")
    private List<LanguageInput> languages = new ArrayList<LanguageInput>();

    /**
     * The ordinal is to track which levels were added first, which matters for prereqs and some bonuses.
     * It doesn't need to be serialized because it's implicit in the order of the XML layout.
     */
    private transient int ordinal;
    private transient int classOrdinal;

    public LevelInput() {
    }
    public LevelInput(String classname) {
        this.classname = classname;
    }
    public String getClassname() {
        return classname;
    }
    public void setClassname(String classname) {
        this.classname = classname;
    }
    public List<ArchetypeInput> getArchetypes() {
        return archetypes == null ? Collections.<ArchetypeInput>emptyList() : new ArrayList<ArchetypeInput>(archetypes);
    }
    public void setArchetypes(List<ArchetypeInput> archetypes) {
        this.archetypes = new ArrayList<ArchetypeInput>(archetypes);
    }
    public HitpointsInput getHitpoints() {
        return hitpoints;
    }
    public void setHitpoints(HitpointsInput hitpoints) {
        this.hitpoints = hitpoints;
    }
    public List<SkillInput> getSkills() {
        return skills == null ? Collections.<SkillInput>emptyList() : new ArrayList<SkillInput>(skills);
    }
    public void setSkills(List<SkillInput> skills) {
        this.skills = new ArrayList<SkillInput>(skills);
    }
    public FavoredClassBonusInput getFavoredClassBonus() {
        return favoredClassBonus;
    }
    public void setFavoredClassBonus(FavoredClassBonusInput favoredClassBonus) {
        this.favoredClassBonus = favoredClassBonus;
    }
    public List<FeatInput> getFeats() {
        return feats == null ? Collections.<FeatInput>emptyList() : new ArrayList<FeatInput>(feats);
    }
    public void setFeats(List<FeatInput> feats) {
        this.feats = new ArrayList<FeatInput>(feats);
    }
    public StatBonusInput getStatBonus() {
        return statBonus;
    }
    public void setStatBonus(StatBonusInput statBonus) {
        this.statBonus = statBonus;
    }
    public List<LanguageInput> getLanguages() {
        return languages == null ? Collections.<LanguageInput>emptyList() : new ArrayList<LanguageInput>(languages);
    }
    public void setLanguages(List<LanguageInput> languages) {
        this.languages = new ArrayList<LanguageInput>(languages);
    }
    public int getOrdinal() {
        return ordinal;
    }
    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }
    public int getClassOrdinal() {
        return classOrdinal;
    }
    public void setClassOrdinal(int classOrdinal) {
        this.classOrdinal = classOrdinal;
    }
    public String toString() {
        return "Level[PC-" + ordinal + "/" + classname + "-" + classOrdinal + "]";
    }

    private Object readResolve() {
        if (skills != null) {
            for (SkillInput s : skills)
                s.setClassname(classname);
        }
        return this;
    }
}
