package com.restapi.channelAdmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class BulkUserUploadResponseVO extends BaseResponse {

	private String fileType;
	private String fileName;
	private String fileattachment;
	private ArrayList errorList;
	private String errorFlag;
	private int totalRecords;
	private String noOfRecords;

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileattachment() {
		return fileattachment;
	}

	public void setFileattachment(String fileattachment) {
		this.fileattachment = fileattachment;
	}

	public ArrayList getErrorList() {
		return errorList;
	}

	public void setErrorList(ArrayList errorList) {
		this.errorList = errorList;
	}

	public String getErrorFlag() {
		return errorFlag;
	}

	public void setErrorFlag(String errorFlag) {
		this.errorFlag = errorFlag;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public String getNoOfRecords() {
		return noOfRecords;
	}

	public void setNoOfRecords(String noOfRecords) {
		this.noOfRecords = noOfRecords;
	}

}