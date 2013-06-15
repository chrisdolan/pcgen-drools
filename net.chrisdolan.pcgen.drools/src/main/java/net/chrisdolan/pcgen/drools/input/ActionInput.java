package net.chrisdolan.pcgen.drools.input;

public class ActionInput {
    public static final String TYPE_CHARGE = "Charge";
    public static final String TYPE_LUNGE = "Lunge";
    public static final String TYPE_FIGHTING_DEFENSIVELY = "FightingDefensively";
    public static final String TYPE_TOTAL_DEFENSE = "TotalDefense";

    private String type;

    public ActionInput() {
    }
    public ActionInput(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ActionInput other = (ActionInput) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "ActionInput[" + type + "]";
    }
}
