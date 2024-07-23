package com.inter.safaricomreversal;

import java.io.Serializable;

public class IDs  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String ID;
	
	@Override
	public String toString() {
		return "IDs [ID=" + ID + "]";
	}

	public IDs(String ID) {
		super();
		this.ID = ID;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

}
