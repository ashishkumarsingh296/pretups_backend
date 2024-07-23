package com.restapi.networkadmin.requestVO;

public class DisplayStockTxnDetailsViewRequestVO {

	private String tmpTxnNo;
	private String networkCodeFor;
	private String networkCode;
	
	private String fromDateStr;
    private String toDateStr;
    
    private String txnStatus;
    private String entryType;
    
    
	public String getTmpTxnNo() {
		return tmpTxnNo;
	}
	public void setTmpTxnNo(String tmpTxnNo) {
		this.tmpTxnNo = tmpTxnNo;
	}
	public String getNetworkCodeFor() {
		return networkCodeFor;
	}
	public void setNetworkCodeFor(String networkCodeFor) {
		this.networkCodeFor = networkCodeFor;
	}
	public String getNetworkCode() {
		return networkCode;
	}
	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
	public String getFromDateStr() {
		return fromDateStr;
	}
	public void setFromDateStr(String fromDateStr) {
		this.fromDateStr = fromDateStr;
	}
	public String getToDateStr() {
		return toDateStr;
	}
	public void setToDateStr(String toDateStr) {
		this.toDateStr = toDateStr;
	}
	public String getTxnStatus() {
		return txnStatus;
	}
	public void setTxnStatus(String txnStatus) {
		this.txnStatus = txnStatus;
	}
	public String getEntryType() {
		return entryType;
	}
	public void setEntryType(String entryType) {
		this.entryType = entryType;
	}
    
}
