package net.chrisdolan.pcgen.drools.input;

public class Input {

    private String type;
    private String subtype;
    private int value;
    
    public Input() {
    }
    public Input(String type, String subtype, int value) {
        this.type = type;
        this.subtype = subtype;
        this.value = value;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getSubtype() {
        return subtype;
    }
    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }

//    @Override
//    public int hashCode() {
//        final int prime = 31;
//        int result = 1;
//        result = prime * result + ((subtype == null) ? 0 : subtype.hashCode());
//        result = prime * result + ((type == null) ? 0 : type.hashCode());
//        result = prime * result + value;
//        return result;
//    }
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj)
//            return true;
//        if (obj == null)
//            return false;
//        if (getClass() != obj.getClass())
//            return false;
//        Input other = (Input) obj;
//        if (subtype == null) {
//            if (other.subtype != null)
//                return false;
//        } else if (!subtype.equals(other.subtype))
//            return false;
//        if (type == null) {
//            if (other.type != null)
//                return false;
//        } else if (!type.equals(other.type))
//            return false;
//        if (value != other.value)
//            return false;
//        return true;
//    }
    @Override
    public String toString() {
        return "Input[" + type + "/" + subtype + "=" + value + "]";
    }

}
