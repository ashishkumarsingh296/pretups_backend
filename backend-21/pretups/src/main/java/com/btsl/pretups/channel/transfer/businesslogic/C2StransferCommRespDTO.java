package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.List;

public class C2StransferCommRespDTO {
	
	private List<C2StransferCommisionRecordVO> listC2sTransferCommRecordVO;
	private C2StransferCommSummryData c2StransferCommSummryData;
	private String totalDownloadedRecords;
	private Long lastRecordNo; // for CSV for lastRecord.
//  This is specific for Offline purpose,as we don't add data in listC2sTransferCommRecordVO 
//	due to millions records adding in Arraylist will cause memory issue.,
	private boolean noDataFound;  // Just to check Offline report input criteria has data or not.
	private String onlineFilePath;
	
	                             
	
	public List<C2StransferCommisionRecordVO> getListC2sTransferCommRecordVO() {
		return listC2sTransferCommRecordVO;
	}
	public void setListC2sTransferCommRecordVO(List<C2StransferCommisionRecordVO> listC2sTransferCommRecordVO) {
		this.listC2sTransferCommRecordVO = listC2sTransferCommRecordVO;
	}
	public C2StransferCommSummryData getC2StransferCommSummryData() {
		return c2StransferCommSummryData;
	}
	public void setC2StransferCommSummryData(C2StransferCommSummryData c2StransferCommSummryData) {
		this.c2StransferCommSummryData = c2StransferCommSummryData;
	}
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
	public String getOnlineFilePath() {
		return onlineFilePath;
	}
	public void setOnlineFilePath(String onlineFilePath) {
		this.onlineFilePath = onlineFilePath;
	}
	

}
