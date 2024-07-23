package com.restapi.o2c.service;

import java.util.HashMap;
import java.util.Map;

import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.channel.transfer.businesslogic.BatchO2CMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CBatchMasterVO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class O2CBatchApprovalDetailsResponse extends BaseResponse{

@JsonProperty("Batch Approval Details")
private BatchO2CMasterVO approvalDetails = new BatchO2CMasterVO();
@JsonIgnore
public BatchO2CMasterVO getApprovalDetails() {
	return approvalDetails;
}

public void setApprovalDetails(BatchO2CMasterVO approvalDetails) {
	this.approvalDetails = approvalDetails;
} 
@JsonProperty("fileName")
private String fileName;
@JsonProperty("fileAttachment")
private String fileAttachment;
@JsonProperty("fileType")
private String fileType;
/**
 * @return the fileName
 */
public String getFileName() {
	return fileName;
}

/**
 * @param fileName the fileName to set
 */
public void setFileName(String fileName) {
	this.fileName = fileName;
}

/**
 * @return the fileAttachment
 */
public String getFileAttachment() {
	return fileAttachment;
}

/**
 * @param fileAttachment the fileAttachment to set
 */
public void setFileAttachment(String fileAttachment) {
	this.fileAttachment = fileAttachment;
}

/**
 * @return the fileType
 */
public String getFileType() {
	return fileType;
}

/**
 * @param fileType the fileType to set
 */
public void setFileType(String fileType) {
	this.fileType = fileType;
}


}


