package com.btsl.pretups.channel.reports.businesslogic;

/**
 * @author mohit.miglani
 *
 */
public class StaffSelfC2CReportVO {
private String activeUserId;
private String toUserId;
private String fromUser;
private String toUser;
private String transferID;
private String transferSubType;
private String type;
private String closeDate;


private String productName;
private String transferMrp;
private String payableAmt;
private String netPayableAmt;
private String status;
private String mrp;
private String commission;
private String commissionQuantity;
private String receiverCreditQuantity;
private String senderDebitQuantity;
private String tax3Value;
private String tax1Value;
private String tax2Value;
private String senderCategoryCode;
private String receiverCategoryCode;
private String receiverCategoryName;
private String source;
public String getActiveUserId() {
	return activeUserId;
}
public void setActiveUserId(String activeUserId) {
	this.activeUserId = activeUserId;
}
public String getToUserId() {
	return toUserId;
}
public void setToUserId(String toUserId) {
	this.toUserId = toUserId;
}
public String getFromUser() {
	return fromUser;
}
public void setFromUser(String fromUser) {
	this.fromUser = fromUser;
}
public String getToUser() {
	return toUser;
}
public void setToUser(String toUser) {
	this.toUser = toUser;
}
public String getTransferID() {
	return transferID;
}
public void setTransferID(String transferID) {
	this.transferID = transferID;
}

public String getTransferSubType() {
	return transferSubType;
}
public void setTransferSubType(String transferSubType) {
	this.transferSubType = transferSubType;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public String getCloseDate() {
	return closeDate;
}
public void setCloseDate(String closeDate) {
	this.closeDate = closeDate;
}
public String getProductName() {
	return productName;
}
public void setProductName(String productName) {
	this.productName = productName;
}
public String getTransferMrp() {
	return transferMrp;
}
public void setTransferMrp(String transferMrp) {
	this.transferMrp = transferMrp;
}
public String getPayableAmt() {
	return payableAmt;
}
public void setPayableAmt(String payableAmt) {
	this.payableAmt = payableAmt;
}
public String getNetPayableAmt() {
	return netPayableAmt;
}
public void setNetPayableAmt(String netPayableAmt) {
	this.netPayableAmt = netPayableAmt;
}
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public String getMrp() {
	return mrp;
}
public void setMrp(String mrp) {
	this.mrp = mrp;
}
public String getCommission() {
	return commission;
}
public void setCommission(String commission) {
	this.commission = commission;
}
public String getCommissionQuantity() {
	return commissionQuantity;
}
public void setCommissionQuantity(String commissionQuantity) {
	this.commissionQuantity = commissionQuantity;
}
public String getReceiverCreditQuantity() {
	return receiverCreditQuantity;
}
public void setReceiverCreditQuantity(String receiverCreditQuantity) {
	this.receiverCreditQuantity = receiverCreditQuantity;
}
public String getSenderDebitQuantity() {
	return senderDebitQuantity;
}
public void setSenderDebitQuantity(String senderDebitQuantity) {
	this.senderDebitQuantity = senderDebitQuantity;
}
public String getTax3Value() {
	return tax3Value;
}
public void setTax3Value(String tax3Value) {
	this.tax3Value = tax3Value;
}
public String getTax1Value() {
	return tax1Value;
}
public void setTax1Value(String tax1Value) {
	this.tax1Value = tax1Value;
}
public String getTax2Value() {
	return tax2Value;
}
public void setTax2Value(String tax2Value) {
	this.tax2Value = tax2Value;
}
public String getSenderCategoryCode() {
	return senderCategoryCode;
}
public void setSenderCategoryCode(String senderCategoryCode) {
	this.senderCategoryCode = senderCategoryCode;
}
public String getReceiverCategoryCode() {
	return receiverCategoryCode;
}
public void setReceiverCategoryCode(String receiverCategoryCode) {
	this.receiverCategoryCode = receiverCategoryCode;
}
public String getReceiverCategoryName() {
	return receiverCategoryName;
}
public void setReceiverCategoryName(String receiverCategoryName) {
	this.receiverCategoryName = receiverCategoryName;
}
public String getSource() {
	return source;
}
public void setSource(String source) {
	this.source = source;
}
@Override
public String toString() {
	return "StaffSelfC2CReportVO [activeUserId=" + activeUserId + ", toUserId="
			+ toUserId + ", fromUser=" + fromUser + ", toUser=" + toUser
			+ ", transferID=" + transferID + ", TransferSubType="
			+ transferSubType + ", type=" + type + ", closeDate=" + closeDate
			+ ", productName=" + productName + ", transferMrp=" + transferMrp
			+ ", payableAmt=" + payableAmt + ", netPayableAmt=" + netPayableAmt
			+ ", status=" + status + ", mrp=" + mrp + ", commission="
			+ commission + ", commissionQuantity=" + commissionQuantity
			+ ", receiverCreditQuantity=" + receiverCreditQuantity
			+ ", senderDebitQuantity=" + senderDebitQuantity + ", tax3Value="
			+ tax3Value + ", tax1Value=" + tax1Value + ", tax2Value="
			+ tax2Value + ", senderCategoryCode=" + senderCategoryCode
			+ ", receiverCategoryCode=" + receiverCategoryCode
			+ ", receiverCategoryName=" + receiverCategoryName + ", source="
			+ source + "]";
}
}
