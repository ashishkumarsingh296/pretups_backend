package com.btsl.pretups.channel.transfer.businesslogic;

public class BulkUserAddStsRespDTO {
	private String totalDownloadedRecords;
	private Long lastRecordNo; // for CSV for lastRecord.
	private String onlineDownloadFileData;
	private boolean noDataFound;  // Just to check Offline report input criteria has data or not.
	
	public String getTotalDownloadedRecords() {
		return totalDownloadedRecords;
	}
	public void setTotalDownloadedRecords(String totalDownloadedRecords) {
		this.totalDownloadedRecords = totalDownloadedRecords;
	}
	
	public Long getLastRecordNo() {
		return lastRecordNo;
	}
	public void setLastRecordNo(Long lastRecordNo) {
		this.lastRecordNo = lastRecordNo;
	}
	public String getOnlineDownloadFileData() {
		return onlineDownloadFileData;
	}
	public void setOnlineDownloadFileData(String onlineDownloadFileData) {
		this.onlineDownloadFileData = onlineDownloadFileData;
	}
	public boolean isNoDataFound() {
		return noDataFound;
	}
	public void setNoDataFound(boolean noDataFound) {
		this.noDataFound = noDataFound;
	}
	
	

}
