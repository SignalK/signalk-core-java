package nz.co.fortytwo.signalk.model;

public class Attr {
	private int mode = 755; //rwxr-xr-x
	private String owner = "self";
	private String group = "self";
	
	public Attr(){
		
	}
	public Attr(String[] values){
		this.mode=Integer.valueOf(values[0].trim());
		this.owner=values[1].trim();
		this.group=values[2].trim();
	}
	public Attr(int mode, String owner, String group){
		this.mode=mode;
		this.owner=owner;
		this.group=group;
	}
	
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + mode;
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
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
		Attr other = (Attr) obj;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (mode != other.mode)
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Attr [mode=" + mode + ", owner=" + owner + ", group=" + group + "]";
	}

	public String asString() {
		return  mode + ", " + owner + ", " + group;
	}
}
