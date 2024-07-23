/**
 * @(#)ReverseHirerachyCommisionVO.java 
 * 
 * -------------------------------------------------------------------------------------------------
 * Author 				Date 			History
 * -------------------------------------------------------------------------------------------------
 * Vishal Kumar 	Oct 25, 2017 		Initial Creation
 * 
 *  VO Class for the DUAL_WALLET_AUTO_C2C Table
 * 
 */

package com.btsl.pretups.processes.clientprocesses.businesslogic;

public class ReverseHirerachyCommisionVO {

	private String throughUser;        
	private String beneficaryParentId; 
	private String transactionDate;     
	private String commProfileSetId;  
	private double onAmount;            
	private double focAmount;           
	private String fileGenerated;
	private String msisdn;
	public String getThroughUser() {
		return throughUser;
	}
	public void setThroughUser(String throughUser) {
		this.throughUser = throughUser;
	}
	public String getBeneficaryParentId() {
		return beneficaryParentId;
	}
	public void setBeneficaryParentId(String beneficaryParentId) {
		this.beneficaryParentId = beneficaryParentId;
	}

	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getCommProfileSetId() {
		return commProfileSetId;
	}
	public void setCommProfileSetId(String commProfileSetId) {
		this.commProfileSetId = commProfileSetId;
	}
	public double getOnAmount() {
		return onAmount;
	}
	public void setOnAmount(double onAmount) {
		this.onAmount = onAmount;
	}
	public double getFocAmount() {
		return focAmount;
	}
	public void setFocAmount(double focAmount) {
		this.focAmount = focAmount;
	}
	public String getFileGenerated() {
		return fileGenerated;
	}
	public void setFileGenerated(String fileGenerated) {
		this.fileGenerated = fileGenerated;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("throughUser=" + throughUser);
		sb.append("beneficaryParentId=" + beneficaryParentId);
		sb.append(",transactionDate="+transactionDate);
		sb.append("commProfileSetId=" + commProfileSetId);
		sb.append("onAmount=" + onAmount);
		sb.append(",focAmount="+focAmount);
		sb.append("fileGenerated=" + fileGenerated);
		sb.append("msisdn=" + msisdn);
		return sb.toString();

	}


}
