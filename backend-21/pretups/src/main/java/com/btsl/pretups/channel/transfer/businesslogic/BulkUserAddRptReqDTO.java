
package com.btsl.pretups.channel.transfer.businesslogic;

public class BulkUserAddRptReqDTO extends CommonDownloadReqDTO {
   
	private String batchNo;
	private String domain;
	private String geography;
	private String reqTab;
	


	public String getDomain() {
		return domain;
	}


	public void setDomain(String domain) {
		this.domain = domain;
	}


	public String getBatchNo() {
		return batchNo;
	}


	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}


	public String getGeography() {
		return geography;
	}


	public void setGeography(String geography) {
		this.geography = geography;
	}
	
	public String getReqTab() {
		return reqTab;
	}


	public void setReqTab(String reqTab) {
		this.reqTab = reqTab;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("LowThresholdDownloadReqDTO [ ");
		sb.append("From Date : ");
		sb.append( fromDate ).append(" ,");
		sb.append(",To Date : ");
		sb.append( toDate ).append(" ,");
		sb.append(",NetworkCode: ");
		sb.append( extnwcode ).append(" ,");
		sb.append(",domain: ");
		sb.append( domain).append(" ,");
		sb.append(",geography: ");
		sb.append( geography).append(" ,");
		sb.append(",user: ");
		
		sb.append(" ]");
		return sb.toString();
	}



}
