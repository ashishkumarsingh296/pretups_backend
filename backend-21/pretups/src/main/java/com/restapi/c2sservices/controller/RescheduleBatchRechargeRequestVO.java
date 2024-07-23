package com.restapi.c2sservices.controller;

import com.fasterxml.jackson.annotation.JsonProperty;



public class RescheduleBatchRechargeRequestVO {

	@io.swagger.v3.oas.annotations.media.Schema(example = "SB2012220.0004", required = true)
	@JsonProperty("batchId")
	private String batchId;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true)
	@JsonProperty("categoryCode")
	private String categoryCode;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true)
	@JsonProperty("domainCode")
	private String domainCode;
	
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "RC", required = true)
	@JsonProperty("serviceCode")
	private String serviceCode;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "schedule007", required = true)
	@JsonProperty("fileName")
	private String fileName;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "Base64", required = true)
	@JsonProperty("fileAttachment")
	private String fileAttachment;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "xls/csv/xlsx", required = true)
	@JsonProperty("fileType")
	private String fileType;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "3", required = true)
	@JsonProperty("iteration")
	private String iteration;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "Monthly", required = true)
	@JsonProperty("frequency")
	private String frequency;

	@io.swagger.v3.oas.annotations.media.Schema(example = "DD/MM/YY", required = true)
	@JsonProperty("scheduleDate")
	private String scheduleDate;

	@io.swagger.v3.oas.annotations.media.Schema(example = "Corporate/Normal", required = true)
	@JsonProperty("batchType")
	private String batchType;

	
	
	/**
	 * @return the batchType
	 */
	public String getBatchType() {
		return batchType;
	}

	/**
	 * @param batchType the batchType to set
	 */
	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}

	/**
	 * @return the scheduleDate
	 */
	public String getScheduleDate() {
		return scheduleDate;
	}

	/**
	 * @param scheduleDate the scheduleDate to set
	 */
	public void setScheduleDate(String scheduleDate) {
		this.scheduleDate = scheduleDate;
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
	 * @return the categoryCode
	 */
	public String getCategoryCode() {
		return categoryCode;
	}

	/**
	 * @param categoryCode the categoryCode to set
	 */
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	/**
	 * @return the domainCode
	 */
	public String getDomainCode() {
		return domainCode;
	}

	/**
	 * @param domainCode the domainCode to set
	 */
	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}


	/**
	 * @return the serviceCode
	 */
	public String getServiceCode() {
		return serviceCode;
	}

	/**
	 * @param serviceCode the serviceCode to set
	 */
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
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

	/**
	 * @return the iteration
	 */
	public String getIteration() {
		return iteration;
	}

	/**
	 * @param iteration the iteration to set
	 */
	public void setIteration(String iteration) {
		this.iteration = iteration;
	}

	/**
	 * @return the frequency
	 */
	public String getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RescheduleBatchRechargeRequestVO [batchId=").append(batchId).append(", categoryCode=")
				.append(categoryCode).append(", domainCode=").append(domainCode).append(", serviceCode=")
				.append(serviceCode).append(", fileName=").append(fileName).append(", fileAttachment=")
				.append(fileAttachment).append(", fileType=").append(fileType).append(", iteration=").append(iteration)
				.append(", frequency=").append(frequency).append(", scheduleDate=").append(scheduleDate)
				.append(", batchType=").append(batchType).append("]");
		return builder.toString();
	}

	
	
	
	
	
}
