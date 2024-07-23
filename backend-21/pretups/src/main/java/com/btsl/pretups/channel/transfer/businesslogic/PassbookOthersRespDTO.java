package com.btsl.pretups.channel.transfer.businesslogic;

public class PassbookOthersRespDTO {
	
	
	private String totalDownloadedRecords;
	private Long lastRecordNo; // for CSV for lastRecord.
//  This is specific for Offline purpose,as we don't add data in listC2sTransferCommRecordVO 
//	due to millions records adding in Arraylist will cause memory issue.,
	private boolean noDataFound;  // Just to check Offline report input criteria has data or not.
	private String onlineFileData;
	private String onlineFilePath;
	
	
	
	public String getTotalDownloadedRecords() {
		return totalDownloadedRecords;
	}
	public void setTotalDownloadedRecords(String totalDownloadedRecords) {
		this.totalDownloadedRecords = totalDownloadedRecords;
	}
	public boolean isNoDataFound() {
		return noDataFound;
	}
	public void setNoDataFound(boolean noDataFound) {
		this.noDataFound = noDataFound;
	}
	public Long getLastRecordNo() {
		return lastRecordNo;
	}
	public void setLastRecordNo(Long lastRecordNo) {
		this.lastRecordNo = lastRecordNo;
	}
	public String getOnlineFileData() {
		return onlineFileData;
	}
	public void setOnlineFileData(String onlineFileData) {
		this.onlineFileData = onlineFileData;
	}
	public String getOnlineFilePath() {
		return onlineFilePath;
	}
	public void setOnlineFilePath(String onlineFilePath) {
		this.onlineFilePath = onlineFilePath;
	}
	
	

}
