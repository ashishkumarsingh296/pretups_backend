package com.inter.safaricomreversal;

import java.io.Serializable;

public class RelatedServiceAccountAdjustment implements Serializable{

	private static final long serialVersionUID = 1L;
	String Name;
	String Desc;
	
	
	@Override
	public String toString() {
		return "RelatedServiceAccountAdjustment [Name=" + Name + ", Desc=" + Desc + "]";
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getDesc() {
		return Desc;
	}
	public void setDesc(String desc) {
		Desc = desc;
	}
	public RelatedServiceAccountAdjustment(String name, String desc) {
		super();
		Name = name;
		Desc = desc;
	}
}
