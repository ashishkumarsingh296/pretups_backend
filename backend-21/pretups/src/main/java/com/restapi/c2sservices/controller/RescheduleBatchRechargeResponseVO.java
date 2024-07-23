package com.restapi.c2sservices.controller;
import com.btsl.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RescheduleBatchRechargeResponseVO extends BaseResponse {

	@JsonProperty("noOfRecords")
	private Integer noOfRecords;
	
	@JsonProperty("batchId")
	private String batchId;
	
	@JsonProperty("cancelledRecords")
	private Integer cancelledRecords;
	
	@JsonProperty("fileName")
	private String fileName;
	
	@JsonProperty("failedRecords")
	private int failedRecords;
	
	@JsonProperty("rescheduleDate")
	private String rescheduleDate;
	
	@JsonProperty("previousDate")
	private String previousDate;
	
	@JsonProperty("errorFileAttachment")
	private String errorFileAttachment;

	@JsonProperty("errorFileName")
	private String errorFileName;
	
	
	/**
	 * @return the errorFileName
	 */
	public String getErrorFileName() {
		return errorFileName;
	}

	/**
	 * @param errorFileName the errorFileName to set
	 */
	public void setErrorFileName(String errorFileName) {
		this.errorFileName = errorFileName;
	}

	/**
	 * @return the errorFileAttachment
	 */
	public String getErrorFileAttachment() {
		return errorFileAttachment;
	}

	/**
	 * @param errorFileAttachment the errorFileAttachment to set
	 */
	public void setErrorFileAttachment(String errorFileAttachment) {
		this.errorFileAttachment = errorFileAttachment;
	}

	/**
	 * @return the noOfRecords
	 */
	public Integer getNoOfRecords() {
		return noOfRecords;
	}

	/**
	 * @param noOfRecords the noOfRecords to set
	 */
	public void setNoOfRecords(Integer noOfRecords) {
		this.noOfRecords = noOfRecords;
	}

	/**
	 * @return the batchId
	 */
	public String getBatchId() {
		return batchId;
	}

	/**
	 * @param batchId the batchId to set
	 */
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	/**
	 * @return the cancelledRecords
	 */
	public Integer getCancelledRecords() {
		return cancelledRecords;
	}

	/**
	 * @param cancelledRecords the cancelledRecords to set
	 */
	public void setCancelledRecords(Integer cancelledRecords) {
		this.cancelledRecords = cancelledRecords;
	}

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
	 * @return the failedRecords
	 */
	public int getFailedRecords() {
		return failedRecords;
	}

	/**
	 * @param errorRecords the failedRecords to set
	 */
	public void setFailedRecords(int errorRecords) {
		this.failedRecords = errorRecords;
	}

	/**
	 * @return the rescheduleDate
	 */
	public String getRescheduleDate() {
		return rescheduleDate;
	}

	/**
	 * @param rescheduleDate the rescheduleDate to set
	 */
	public void setRescheduleDate(String rescheduleDate) {
		this.rescheduleDate = rescheduleDate;
	}

	/**
	 * @return the previousDate
	 */
	public String getPreviousDate() {
		return previousDate;
	}

	/**
	 * @param previousDate the previousDate to set
	 */
	public void setPreviousDate(String previousDate) {
		this.previousDate = previousDate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RescheduleBatchRechargeResponseVO [noOfRecords=").append(noOfRecords).append(", batchId=")
				.append(batchId).append(", cancelledRecords=").append(cancelledRecords).append(", fileName=")
				.append(fileName).append(", failedRecords=").append(failedRecords).append(", rescheduleDate=")
				.append(rescheduleDate).append(", previousDate=").append(previousDate).append(", errorFileAttachment=")
				.append(errorFileAttachment).append(", errorFileName=").append(errorFileName).append("]");
		return builder.toString();
	}

	

	
	
	
	
}

