package com.inter.safaricomreversal;

import java.io.Serializable;

public class FromDate  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String DateString;

	@Override
	public String toString() {
		return "FromDate [FromDate=" + DateString + "]";
	}

	public FromDate(String fromDate) {
		super();
		DateString = fromDate;
	}

	public String getFromDate() {
		return DateString;
	}

	public void setFromDate(String fromDate) {
		DateString = fromDate;
	}
	
	
}
