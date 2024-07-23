package com.restapi.channelAdmin.requestVO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BulkCUStatusChangeRequestVO {
	
	@JsonProperty("file")
	private String file;
	
	@JsonProperty("fileName")
	private String fileName;
	
	@JsonProperty("fileType")
	private String fileType;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BulkCUStatusChangeRequestVO [file=" + file + ", fileName=" + fileName + ", fileType=" + fileType + "]");
		return builder.toString();
	}
	
	
	

}
