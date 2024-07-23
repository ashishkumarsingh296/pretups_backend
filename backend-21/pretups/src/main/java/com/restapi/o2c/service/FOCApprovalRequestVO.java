package com.restapi.o2c.service;

import java.util.List;


/**
 * 
 * @author md.sohail
 *
 */
public class FOCApprovalRequestVO {
	
	private List<FOCApprovalData> focApprovalRequests = null;

	@io.swagger.v3.oas.annotations.media.Schema(required = true, description = "FOC requestVO")
	public List<FOCApprovalData> getFocApprovalRequests() {
		return focApprovalRequests;
	}

	public void setFocApprovalRequests(List<FOCApprovalData> focApprovalRequests) {
		this.focApprovalRequests = focApprovalRequests;
	}

	@Override
	public String toString() {
		return "FOCApprovalRequestVO [focApprovalRequests=" + focApprovalRequests + "]";
	}

}


class FOCApprovalData {
    private String currentStatus;
    private String extNwCode;
    private String status;
    private String txnId;
    
    private String remarks;
    private String extTxnNumber;
    private String extTxnDate;
    private String refNumber;
    private String toMsisdn;
    private String language1;
    private String language2;
    
    @io.swagger.v3.oas.annotations.media.Schema(example = "NEW", required = true, description = "Current status")
	public String getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = false, description = "External Network Code", hidden = true)
	public String getExtNwCode() {
		return extNwCode;
	}
	public void setExtNwCode(String extNwCode) {
		this.extNwCode = extNwCode;
	}
	@io.swagger.v3.oas.annotations.media.Schema(example = "approve", required = true, description = "Status: approve/reject")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "OT990811.1923.100001", required = true, description = "Transaction Id")
	public String getTxnId() {
		return txnId;
	}
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "Test remarks", required = true, description = "Remarks")
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "123456", required = true, description = "External transaction number")
	public String getExtTxnNumber() {
		return extTxnNumber;
	}
	public void setExtTxnNumber(String extTxnNumber) {
		this.extTxnNumber = extTxnNumber;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "01/02/2021", required = true, description = "External transaction date")
	public String getExtTxnDate() {
		return extTxnDate;
	}
	public void setExtTxnDate(String extTxnDate) {
		this.extTxnDate = extTxnDate;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "123456789", required = false, description = "Reference Number")
	public String getRefNumber() {
		return refNumber;
	}
	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "72525252", required = true, description = "MSISDN of receiver")
	public String getToMsisdn() {
		return toMsisdn;
	}
	public void setToMsisdn(String toMsisdn) {
		this.toMsisdn = toMsisdn;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "0", required = false, description = "Language1")
	public String getLanguage1() {
		return language1;
	}
	
	public void setLanguage1(String language1) {
		this.language1 = language1;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "1", required = false, description = "Language2")
	public String getLanguage2() {
		return language2;
	}
	public void setLanguage2(String language2) {
		this.language2 = language2;
	}
	
	@Override
	public String toString() {
		return "FOCApprovalData [currentStatus=" + currentStatus + ", extNwCode=" + extNwCode + ", status=" + status
				+ ", txnId=" + txnId + ", remarks=" + remarks + ", extTxnNumber=" + extTxnNumber + ", extTxnDate="
				+ extTxnDate + ", refNumber=" + refNumber + ", toMsisdn=" + toMsisdn + ", language1=" + language1
				+ ", language2=" + language2 + "]";
	}
	
	
	
}


