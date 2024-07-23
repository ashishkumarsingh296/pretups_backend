package com.restapi.channelAdmin;

import com.btsl.common.BaseResponse;

public class BulkSusResCUResponseVO extends BaseResponse{
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
	@Override
	public String toString() {
		return "bulkSusResCUResponseVO [fileName=" + fileName + ", fileAttachment=" + fileAttachment + "]";
	}
	
	
}
