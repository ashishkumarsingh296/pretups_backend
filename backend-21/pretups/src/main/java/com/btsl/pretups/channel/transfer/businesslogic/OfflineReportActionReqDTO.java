
package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.Locale;

public class OfflineReportActionReqDTO  {
	
	private String reportTaskID;
	private String fileName;
	private Locale locale;
	private String reportAction;
	private String offlineDownloadPath;
	public String getReportTaskID() {
		return reportTaskID;
	}
	public void setReportTaskID(String reportTaskID) {
		this.reportTaskID = reportTaskID;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	public String getReportAction() {
		return reportAction;
	}
	public void setReportAction(String reportAction) {
		this.reportAction = reportAction;
	}
	public String getOfflineDownloadPath() {
		return offlineDownloadPath;
	}
	public void setOfflineDownloadPath(String offlineDownloadPath) {
		this.offlineDownloadPath = offlineDownloadPath;
	}
	
	
   
  
 
}
