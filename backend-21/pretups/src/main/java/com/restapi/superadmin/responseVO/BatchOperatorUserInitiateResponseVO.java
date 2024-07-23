package com.restapi.superadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.ErrorMap;

public class BatchOperatorUserInitiateResponseVO {
	
	public ErrorMap getErrorMap() {
		return errorMap;
	}
	public void setErrorMap(ErrorMap errorMap) {
		this.errorMap = errorMap;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessageCode() {
		return messageCode;
	}
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}


	private String fileAttachment;
	private int totalRecords = 0;
	private int validRecords = 0;
	private String noOfRecords;
	private String batchID;
	private ErrorMap errorMap;
	private String status;
    private String messageCode;
    private String message;
	private String fileName;
	private String fileType;
	private ArrayList errorList;
	
	public int getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
	public int getValidRecords() {
		return validRecords;
	}
	public void setValidRecords(int validRecords) {
		this.validRecords = validRecords;
	}
	public String getFileAttachment() {
		return fileAttachment;
	}
	public void setFileAttachment(String fileAttachment) {
		this.fileAttachment = fileAttachment;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	public ArrayList getErrorList() {
		return errorList;
	}

	public void setErrorList(ArrayList errorList) {
		this.errorList = errorList;
	}
	
	public String getBatchID() {
		return batchID;
	}
	public void setBatchID(String batchID) {
		this.batchID = batchID;
	}
	public String getNoOfRecords() {
		return noOfRecords;
	}

	public void setNoOfRecords(String noOfRecords) {
		this.noOfRecords = noOfRecords;
	}

	@Override
	public String toString() {
		return null;
	}

}
