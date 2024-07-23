package com.inter.safaricomreversal;

import java.io.Serializable;

public class AdjustmentSpecification implements Serializable{

	private static final long serialVersionUID = 1L;
	String Name;
	String Desc;
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
	public AdjustmentSpecification(String name, String desc) {
		super();
		Name = name;
		Desc = desc;
	}
	


}
