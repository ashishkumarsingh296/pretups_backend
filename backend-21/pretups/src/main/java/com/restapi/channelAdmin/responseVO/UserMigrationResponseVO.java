package com.restapi.channelAdmin.responseVO;

import com.btsl.common.BaseResponse;

public class UserMigrationResponseVO extends BaseResponse{

	private String fileAttachment;
	private String fileName;
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
	
	
}
