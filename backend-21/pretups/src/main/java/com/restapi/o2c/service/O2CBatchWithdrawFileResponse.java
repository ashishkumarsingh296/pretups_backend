package com.restapi.o2c.service;

import java.util.ArrayList;

import com.btsl.common.BaseResponseMultiple;

public class O2CBatchWithdrawFileResponse extends BaseResponseMultiple{
	
	
	private ArrayList<String> fileValidationErrorList;
	private String fileAttachment;
	private String batchID;
	public String getBatchID() {
		return batchID;
	}
	public void setBatchID(String batchID) {
		this.batchID = batchID;
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


	private String fileName;
	public ArrayList<String> getFileValidationErrorList() {
		return fileValidationErrorList;
	}
	public void setFileValidationErrorList(ArrayList<String> fileValidationErrorList) {
		this.fileValidationErrorList = fileValidationErrorList;
	}
	
	
	@Override
	public String toString() {
		return "C2CFileUploadApiResponse [ fileValidationErrorList=" + fileValidationErrorList + "]";
	}


	
	

}
