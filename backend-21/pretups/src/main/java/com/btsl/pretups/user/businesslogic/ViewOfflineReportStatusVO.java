package com.btsl.pretups.user.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeCountersVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

/**
 * Author Subesh KCV
 *                        ------------------------------------------------------
 *                        ------------------------------------------
 */

public class ViewOfflineReportStatusVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String reportProcessTaskID;
	private String initiatedDateTime;
	private String reportName;
	private String fileName;
	private String reportStatus;
	private String rptDowldCompletionTime;
	private String totalRecords;
	
	
	public String getInitiatedDateTime() {
		return initiatedDateTime;
	}
	public void setInitiatedDateTime(String initiatedDateTime) {
		this.initiatedDateTime = initiatedDateTime;
	}
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getReportStatus() {
		return reportStatus;
	}
	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}
	public String getRptDowldCompletionTime() {
		return rptDowldCompletionTime;
	}
	public void setRptDowldCompletionTime(String rptDowldCompletionTime) {
		this.rptDowldCompletionTime = rptDowldCompletionTime;
	}
	public String getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(String totalRecords) {
		this.totalRecords = totalRecords;
	}
	public String getReportProcessTaskID() {
		return reportProcessTaskID;
	}
	public void setReportProcessTaskID(String reportProcessTaskID) {
		this.reportProcessTaskID = reportProcessTaskID;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

	
}
