package net.chrisdolan.pcgen.drools.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("race")
public class RaceInput {
    public static final String DWARF = "Dwarf";
    public static final String ELF = "Elf";
    public static final String GNOME = "Gnome";
    public static final String HALFELF = "Half-elf";
    public static final String HALFLING = "Halfling";
    public static final String HALFORC = "Half-orc";
    public static final String HUMAN = "Human";

    
    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;

    @XStreamAlias("age")
    @XStreamAsAttribute
    private int age;

    @XStreamImplicit(itemFieldName="favoredclass")
    private List<FavoredClassInput> favoredClasses = new ArrayList<FavoredClassInput>();
    @XStreamImplicit(itemFieldName="trait")
    private List<TraitInput> traits = new ArrayList<TraitInput>();
    @XStreamImplicit(itemFieldName="statbonus")
    private List<StatBonusInput> statBonuses = new ArrayList<StatBonusInput>();
    @XStreamImplicit(itemFieldName="language")
    private List<LanguageInput> languages = new ArrayList<LanguageInput>();

    public RaceInput() {
    }
    public RaceInput(String name) {
        this.name = name;
    }
    public RaceInput(String name, int age) {
        this.name = name;
        this.age = age;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    public List<FavoredClassInput> getFavoredClasses() {
        return favoredClasses == null ? Collections.<FavoredClassInput>emptyList() : new ArrayList<FavoredClassInput>(favoredClasses);
    }
    public void setFavoredClasses(List<FavoredClassInput> favoredClasses) {
        this.favoredClasses = new ArrayList<FavoredClassInput>(favoredClasses);
    }
    public List<TraitInput> getTraits() {
        return traits == null ? Collections.<TraitInput>emptyList() : new ArrayList<TraitInput>(traits);
    }
    public void setTraits(List<TraitInput> traits) {
        this.traits = new ArrayList<TraitInput>(traits);
    }
    public List<StatBonusInput> getStatBonuses() {
        return statBonuses == null ? Collections.<StatBonusInput>emptyList() : new ArrayList<StatBonusInput>(statBonuses);
    }
    public void setStatBonuses(List<StatBonusInput> statBonuses) {
        this.statBonuses = new ArrayList<StatBonusInput>(statBonuses);
    }
    public List<LanguageInput> getLanguages() {
        return languages == null ? Collections.<LanguageInput>emptyList() : new ArrayList<LanguageInput>(languages);
    }
    public void setLanguages(List<LanguageInput> languages) {
        this.languages = new ArrayList<LanguageInput>(languages);
    }
    public String toString() {
        return "Race[" + name + "]";
    }
}
