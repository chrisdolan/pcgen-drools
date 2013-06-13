package net.chrisdolan.pcgen.drools.input;

public class AttributeInput {
	public static final String TYPE = "Attribute";
	public static final String SUBTYPE_BASE = "Base";

	public static final String STR = "Strength";
	public static final String CON = "Constitution";
	public static final String DEX = "Dexterity";
	public static final String INT = "Intelligence";
	public static final String WIS = "Wisdom";
	public static final String CHA = "Charisma";

	private String name;
	private int value;

	public AttributeInput() {
	}
	public AttributeInput(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + value;
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
		AttributeInput other = (AttributeInput) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value != other.value)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "AttributeInput[" + name + "=" + value + "]";
	}
}
