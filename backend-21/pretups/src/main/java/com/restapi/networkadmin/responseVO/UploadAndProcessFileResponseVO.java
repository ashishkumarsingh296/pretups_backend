package com.restapi.networkadmin.responseVO;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.btsl.common.BaseResponse;
import com.btsl.common.ErrorMap;

public class UploadAndProcessFileResponseVO extends BaseResponse {
	
	private ArrayList subServiceTypeIdList;
	private String subscriberStatus;
	private ArrayList subscriberStatusList;
	private ArrayList errorList;
	private String fileAttachment;
	private String fileName;
	private String fileType;
	private int totalRecords = 0;
	private int validRecords = 0;
	private ErrorMap errorMap;
	private String messageCode;
	private String message;
	private String noOfRecords;
	private String errorFlag;
	
	
	
	
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
	public ErrorMap getErrorMap() {
		return errorMap;
	}
	public void setErrorMap(ErrorMap errorMap) {
		this.errorMap = errorMap;
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
	public String getNoOfRecords() {
		return noOfRecords;
	}
	public void setNoOfRecords(String noOfRecords) {
		this.noOfRecords = noOfRecords;
	}
	public String getErrorFlag() {
		return errorFlag;
	}
	public void setErrorFlag(String errorFlag) {
		this.errorFlag = errorFlag;
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
	public ArrayList getSubServiceTypeIdList() {
		return subServiceTypeIdList;
	}
	public void setSubServiceTypeIdList(ArrayList subServiceTypeIdList) {
		this.subServiceTypeIdList = subServiceTypeIdList;
	}
	public String getSubscriberStatus() {
		return subscriberStatus;
	}
	public void setSubscriberStatus(String subscriberStatus) {
		this.subscriberStatus = subscriberStatus;
	}
	public ArrayList getSubscriberStatusList() {
		return subscriberStatusList;
	}
	public void setSubscriberStatusList(ArrayList subscriberStatusList) {
		this.subscriberStatusList = subscriberStatusList;
	}
	public ArrayList getErrorList() {
		return errorList;
	}
	public void setErrorList(ArrayList errorList) {
		this.errorList = errorList;
	}
	@Override
	public String toString() {
		return "UploadAndProcessFileResponseVO [subServiceTypeIdList=" + subServiceTypeIdList + ", subscriberStatus="
				+ subscriberStatus + ", subscriberStatusList=" + subscriberStatusList + ", errorList=" + errorList
				+ "]";
	}

}
