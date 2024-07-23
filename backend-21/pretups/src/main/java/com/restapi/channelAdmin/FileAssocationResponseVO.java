package com.restapi.channelAdmin;

import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileAssocationResponseVO extends BaseResponse {
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
	public String getFileAttachment() {
		return fileAttachment;
	}
	public void setFileAttachment(String fileAttachment) {
		this.fileAttachment = fileAttachment;
	}
	public List<ListValueVO> getErrorList() {
		return errorList;
	}
	public void setErrorList(List<ListValueVO> errorList) {
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
	public int getFailCount() {
		return failCount;
	}
	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}
	private String fileName;
    private String fileType;
    private String fileAttachment;
    private List<ListValueVO> errorList;
    private String errorFlag;
    private int totalRecords = 0;
    private int failCount = 0;
}
