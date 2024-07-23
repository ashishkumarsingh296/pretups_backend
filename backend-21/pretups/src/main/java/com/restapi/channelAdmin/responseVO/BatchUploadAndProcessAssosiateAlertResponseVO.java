package com.restapi.channelAdmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.ErrorMap;

public class BatchUploadAndProcessAssosiateAlertResponseVO {

	private String fileAttachment;
	private int totalRecords = 0;
	private int validRecords = 0;
	
	private ErrorMap errorMap;
	private String status;
    private String messageCode;
    private String message;
	private String fileName;
	
	//new starts 
	//private int _totalRecords;
	private String _noOfRecords;
	private ArrayList _errorList = null;
	private String _errorFlag;
	
	//new ends
	private String fileType;
	
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
	
	
	/**
     * @return Returns the errorList.
     */
    public ArrayList getErrorList() {
        return _errorList;
    }

    /**
     * @param errorList
     *            The errorList to set.
     */
    public void setErrorList(ArrayList errorList) {
        _errorList = errorList;
    }
    
    /**
     * @return Returns the noOfRecords.
     */
    public String getNoOfRecords() {
        return _noOfRecords;
    }

    /**
     * @param noOfRecords
     *            The noOfRecords to set.
     */
    public void setNoOfRecords(String noOfRecords) {
        _noOfRecords = noOfRecords;
    }
    
    /**
     * @return Returns the errorFlag.
     */
    public String getErrorFlag() {
        return _errorFlag;
    }

    /**
     * @param errorFlag
     *            The errorFlag to set.
     */
    public void setErrorFlag(String errorFlag) {
        _errorFlag = errorFlag;
    }

    public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	

	
	@Override
	public String toString() {
		return null;
	}
}
