package com.restapi.user.service;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.common.SchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * File download response
 * @author anshul.goyal2
 *
 */
public class FileDownloadResponse extends BaseResponse{
	@Schema(pattern = SchemaConstants.STRING_INPUT_PATTERN,maxLength = SchemaConstants.STRING_MAX_SIZE)
	private String fileType;
	@Schema(pattern = SchemaConstants.STRING_INPUT_PATTERN,maxLength = SchemaConstants.STRING_MAX_SIZE)
	private String fileName;
	@Schema(pattern = SchemaConstants.STRING_INPUT_PATTERN,maxLength = SchemaConstants.STRING_MAX_SIZE)
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
