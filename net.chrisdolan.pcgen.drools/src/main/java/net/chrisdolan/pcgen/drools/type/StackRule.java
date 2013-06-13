package net.chrisdolan.pcgen.drools.type;

public class StackRule {
	private String type;
	private String subtype;
	private String rulename;

	public StackRule() {
	}
	public StackRule(String type, String subtype, String rulename) {
		this.type = type;
		this.subtype = subtype;
		this.rulename = rulename;
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
	public String getRulename() {
		return rulename;
	}
	public void setRulename(String rulename) {
		this.rulename = rulename;
	}
}
