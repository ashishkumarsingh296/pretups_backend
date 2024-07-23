package com.inter.safaricomreversal;

import java.io.Serializable;

public class ValidityPeriod  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	FromDate FromDate;
	ToDate ToDate;

	public ValidityPeriod(FromDate fromDate, ToDate toDate) {
		super();
		this.FromDate = fromDate;
		this.ToDate = toDate;
	}
	
	
	@Override
	public String toString() {
		return "ValidityPeriod [FromDate=" + FromDate + ", ToDate=" + ToDate + "]";
	}


	public FromDate getFromDate() {
		return FromDate;
	}
	public void setFromDate(FromDate fromDate) {
		this.FromDate = fromDate;
	}
	public ToDate getToDate() {
		return ToDate;
	}
	public void setToDate(ToDate toDate) {
		this.ToDate = toDate;
	}

	

}
