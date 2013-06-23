package net.chrisdolan.pcgen.drools.input;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("alignment")
public class AlignmentInput {
    public static final String LG = "LawfulGood";
    public static final String LN = "LawfulNeutral";
    public static final String LE = "LawfulEvil";
    public static final String NG = "NeutralGood";
    public static final String TN = "TrueNeutral";
    public static final String NE = "NeutralEvil";
    public static final String CG = "ChaoticGood";
    public static final String CN = "ChaoticNeutral";
    public static final String CE = "ChaoticEvil";

    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;

    public AlignmentInput() {
    }
    public AlignmentInput(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "Alignment[" + name + "]";
    }
}
