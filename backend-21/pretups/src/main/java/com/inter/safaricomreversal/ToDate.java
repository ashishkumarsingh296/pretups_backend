package com.inter.safaricomreversal;

import java.io.Serializable;

public class ToDate  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String DateString;

	@Override
	public String toString() {
		return "ToDate [ToDate=" + DateString + "]";
	}

	public ToDate(String toDate) {
		super();
		DateString = toDate;
	}

	public String getToDate() {
		return DateString;
	}

	public void setToDate(String toDate) {
		DateString = toDate;
	}
	
	

}
