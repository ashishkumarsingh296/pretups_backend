package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.List;

public class C2CtransferCommRespDTO {
	
	private List<C2CtransferCommisionRecordVO> listC2CTransferCommRecordVO;
	private C2CtransferCommSummryData c2CtransferCommSummryData;
	private String totalDownloadedRecords;
	private Long lastRecordNo; // for CSV for lastRecord.
//  This is specific for Offline purpose,as we don't add data in listC2sTransferCommRecordVO 
//	due to millions records adding in Arraylist will cause memory issue.,
	private boolean noDataFound;  // Just to check Offline report input criteria has data or not.
	private String onlineFileData;
	private String onlineFilePath;
	
	public List<C2CtransferCommisionRecordVO> getListC2CTransferCommRecordVO() {
		return listC2CTransferCommRecordVO;
	}
	public void setListC2CTransferCommRecordVO(List<C2CtransferCommisionRecordVO> listC2CTransferCommRecordVO) {
		this.listC2CTransferCommRecordVO = listC2CTransferCommRecordVO;
	}
	public C2CtransferCommSummryData getC2CtransferCommSummryData() {
		return c2CtransferCommSummryData;
	}
	public void setC2CtransferCommSummryData(C2CtransferCommSummryData c2CtransferCommSummryData) {
		this.c2CtransferCommSummryData = c2CtransferCommSummryData;
	}
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
	public boolean isNoDataFound() {
		return noDataFound;
	}
	public void setNoDataFound(boolean noDataFound) {
		this.noDataFound = noDataFound;
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
