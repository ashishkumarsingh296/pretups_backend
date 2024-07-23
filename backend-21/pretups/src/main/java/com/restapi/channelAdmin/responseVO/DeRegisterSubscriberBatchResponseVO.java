package com.restapi.channelAdmin.responseVO;

import com.btsl.common.BaseResponse;

public class DeRegisterSubscriberBatchResponseVO extends BaseResponse{
	private String fileName;
	private String fileAttachment;
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileAttachment() {
		return fileAttachment;
	}
	public void setFileAttachment(String fileAttachment) {
		this.fileAttachment = fileAttachment;
	}

}
