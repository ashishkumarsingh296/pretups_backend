package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.Date;

public class ReportMasterRespVO {

	private String reportID;
	private String reportName;
	private String createdON;
	private String reportProcessorBeanName;
	private String fileNamePrefix;
	public String getReportID() {
		return reportID;
	}
	public void setReportID(String reportID) {
		this.reportID = reportID;
	}
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public String getCreatedON() {
		return createdON;
	}
	public void setCreatedON(String createdON) {
		this.createdON = createdON;
	}
	public String getReportProcessorBeanName() {
		return reportProcessorBeanName;
	}
	public void setReportProcessorBeanName(String reportProcessorBeanName) {
		this.reportProcessorBeanName = reportProcessorBeanName;
	}
	public String getFileNamePrefix() {
		return fileNamePrefix;
	}
	public void setFileNamePrefix(String fileNamePrefix) {
		this.fileNamePrefix = fileNamePrefix;
	}
	
	
}
