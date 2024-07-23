package com.btsl.pretups.processes.clientprocesses;

import java.util.Date;

public class MODataVO {

	private Date creationDate;	
	private String doNumber;
	private String fileRef;
	private long fromSerialNumber;
	private String itemCode;	
	private String moLineNumber;
	private long moNumber;
	private String orgCode;
	private long quantity;
	private String subInventory;
	private long toSerialNumber;
	private String uom;
	private String wmsRef;	
	private String remarks;
	@Override
	public String toString() {
		return "MODataVO [creationDate=" + creationDate + ", doNumber=" + doNumber + ", fileRef=" + fileRef
				+ ", fromSerialNumber=" + fromSerialNumber + ", itemCode=" + itemCode + ", moLineNumber=" + moLineNumber
				+ ", moNumber=" + moNumber + ", orgCode=" + orgCode + ", quantity=" + quantity + ", subInventory="
				+ subInventory + ", toSerialNumber=" + toSerialNumber + ", uom=" + uom + ", wmsRef=" + wmsRef
				+ ", remarks=" + remarks + "]";
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getFileRef() {
		return fileRef;
	}
	public void setFileRef(String fileRef) {
		this.fileRef = fileRef;
	}
	public long getMoNumber() {
		return moNumber;
	}
	public void setMoNumber(long moNumber) {
		this.moNumber = moNumber;
	}
	public String getMoLineNumber() {
		return moLineNumber;
	}
	public void setMoLineNumber(String moLineNumber) {
		this.moLineNumber = moLineNumber;
	}
	public String getDoNumber() {
		return doNumber;
	}
	public void setDoNumber(String doNumber) {
		this.doNumber = doNumber;
	}
	public String getWmsRef() {
		return wmsRef;
	}
	public void setWmsRef(String wmsRef) {
		this.wmsRef = wmsRef;
	}
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getSubInventory() {
		return subInventory;
	}
	public void setSubInventory(String subInventory) {
		this.subInventory = subInventory;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
	public long getFromSerialNumber() {
		return fromSerialNumber;
	}
	public void setFromSerialNumber(long fromSerialNumber) {
		this.fromSerialNumber = fromSerialNumber;
	}
	public long getToSerialNumber() {
		return toSerialNumber;
	}
	public void setToSerialNumber(long toSerialNumber) {
		this.toSerialNumber = toSerialNumber;
	}
}
