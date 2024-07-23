package com.btsl.user.businesslogic;

import java.util.Date;

public class Batchc2sRevEntryVO {

	private String batchId;
	private String batchType;
	private String batchName;
	private int batchSize;
	private String networkCode;
	private String status;
	private String createdBy;
	private Date createdOn ;
	private String modifiedBy;
	private Date modifiedOn ;
	private String fileName;
	private int approvedRecords;
	private int rejectedRecords;
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getBatchType() {
		return batchType;
	}
	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	
	public String getNetworkCode() {
		return networkCode;
	}
	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public Date getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public int getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
	public int getApprovedRecords() {
		return approvedRecords;
	}
	public void setApprovedRecords(int approvedRecords) {
		this.approvedRecords = approvedRecords;
	}
	public int getRejectedRecords() {
		return rejectedRecords;
	}
	public void setRejectedRecords(int rejectedRecords) {
		this.rejectedRecords = rejectedRecords;
	}
	
	
}
