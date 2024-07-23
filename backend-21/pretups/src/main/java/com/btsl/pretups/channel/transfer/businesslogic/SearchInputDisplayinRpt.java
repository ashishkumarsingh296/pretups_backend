package com.btsl.pretups.channel.transfer.businesslogic;

public class SearchInputDisplayinRpt {

	String fillValue;
	int cellNo;
	public String getFillValue() {
		return fillValue;
	}
	public void setFillValue(String fillValue) {
		this.fillValue = fillValue;
	}
	public int getCellNo() {
		return cellNo;
	}
	public void setCellNo(int cellNo) {
		this.cellNo = cellNo;
	}
	public SearchInputDisplayinRpt(String fillValue, int cellNo) {
		super();
		this.fillValue = fillValue;
		this.cellNo = cellNo;
	}
	
	
}
