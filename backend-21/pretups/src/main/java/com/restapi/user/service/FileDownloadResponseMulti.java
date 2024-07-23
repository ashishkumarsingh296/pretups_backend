package com.restapi.user.service;

import com.btsl.common.BaseResponseMultiple;

public class FileDownloadResponseMulti extends BaseResponseMultiple{

	private String fileType;
	private String fileName;
	private String fileattachment;
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

}
