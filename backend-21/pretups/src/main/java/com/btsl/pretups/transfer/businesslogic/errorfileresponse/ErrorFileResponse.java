package com.btsl.pretups.transfer.businesslogic.errorfileresponse;

import com.btsl.common.BaseResponseMultiple;

public class ErrorFileResponse extends BaseResponseMultiple{
	
	String fileAttachment;
	String fileName;

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ErrorFileResponse [fileAttachment=").append(fileAttachment).append(", fileName=")
				.append(fileName).append("]");
		return builder.toString();
	}

	
	
	

}
