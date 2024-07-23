package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.List;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.channel.query.businesslogic.C2sBalanceQueryVO;

public class O2CTransferUserDetails extends BaseResponseMultiple{
	private String senderUserName;
	private String receiverUserName;
	private String senderMsisdn;
	private String receiverMsisdn;
	private String senderCategoryID;
	private String receiverCategoryID;
	private String senderCategoryName;
	private String receiverCategoryName;
	private String senderCommissionProfileID;
	private String receiverCommissionProfileID;
	private String senderCommissionProfileName;
	private String receiverCommissionProfileName;
	private String senderCommissionProfileSetVersion;
	private String receiverCommissionProfileSetVersion;
	private String senderUserGradeCode;
	private String receiverUserGradeCode;
	private String senderUserGradeName;
	private String receiverUserGradeName;
	private String senderTransferProfileID;
	private String receiverTransferProfileID;
	private String senderTransferProfileName;
	private String receiverTransferProfileName;
	private String geographyCode;
	private String geographyName;
	private String domainCode;
	private String domainName;
	private String senderDualCommission;
	private String receiverDualCommission;
	private List<UserProductDetailsForO2C> userProductDetails;
	private List<C2sBalanceQueryVO> userBalanceDetails;
	public String getSenderUserName() {
		return senderUserName;
	}
	public void setSenderUserName(String senderUserName) {
		this.senderUserName = senderUserName;
	}
	public String getReceiverUserName() {
		return receiverUserName;
	}
	public void setReceiverUserName(String receiverUserName) {
		this.receiverUserName = receiverUserName;
	}
	public String getSenderMsisdn() {
		return senderMsisdn;
	}
	public void setSenderMsisdn(String senderMsisdn) {
		this.senderMsisdn = senderMsisdn;
	}
	public String getReceiverMsisdn() {
		return receiverMsisdn;
	}
	public void setReceiverMsisdn(String receiverMsisdn) {
		this.receiverMsisdn = receiverMsisdn;
	}
	public String getSenderCategoryID() {
		return senderCategoryID;
	}
	public void setSenderCategoryID(String senderCategoryID) {
		this.senderCategoryID = senderCategoryID;
	}
	public String getReceiverCategoryID() {
		return receiverCategoryID;
	}
	public void setReceiverCategoryID(String receiverCategoryID) {
		this.receiverCategoryID = receiverCategoryID;
	}
	public String getSenderCategoryName() {
		return senderCategoryName;
	}
	public void setSenderCategoryName(String senderCategoryName) {
		this.senderCategoryName = senderCategoryName;
	}
	public String getReceiverCategoryName() {
		return receiverCategoryName;
	}
	public void setReceiverCategoryName(String receiverCategoryName) {
		this.receiverCategoryName = receiverCategoryName;
	}
	public String getSenderCommissionProfileID() {
		return senderCommissionProfileID;
	}
	public void setSenderCommissionProfileID(String senderCommissionProfileID) {
		this.senderCommissionProfileID = senderCommissionProfileID;
	}
	public String getReceiverCommissionProfileID() {
		return receiverCommissionProfileID;
	}
	public void setReceiverCommissionProfileID(String receiverCommissionProfileID) {
		this.receiverCommissionProfileID = receiverCommissionProfileID;
	}
	public String getSenderCommissionProfileName() {
		return senderCommissionProfileName;
	}
	public void setSenderCommissionProfileName(String senderCommissionProfileName) {
		this.senderCommissionProfileName = senderCommissionProfileName;
	}
	public String getReceiverCommissionProfileName() {
		return receiverCommissionProfileName;
	}
	public void setReceiverCommissionProfileName(String receiverCommissionProfileName) {
		this.receiverCommissionProfileName = receiverCommissionProfileName;
	}
	public String getSenderCommissionProfileSetVersion() {
		return senderCommissionProfileSetVersion;
	}
	public void setSenderCommissionProfileSetVersion(String senderCommissionProfileSetVersion) {
		this.senderCommissionProfileSetVersion = senderCommissionProfileSetVersion;
	}
	public String getReceiverCommissionProfileSetVersion() {
		return receiverCommissionProfileSetVersion;
	}
	public void setReceiverCommissionProfileSetVersion(String receiverCommissionProfileSetVersion) {
		this.receiverCommissionProfileSetVersion = receiverCommissionProfileSetVersion;
	}
	public String getSenderUserGradeCode() {
		return senderUserGradeCode;
	}
	public void setSenderUserGradeCode(String senderUserGradeCode) {
		this.senderUserGradeCode = senderUserGradeCode;
	}
	public String getReceiverUserGradeCode() {
		return receiverUserGradeCode;
	}
	public void setReceiverUserGradeCode(String receiverUserGradeCode) {
		this.receiverUserGradeCode = receiverUserGradeCode;
	}
	public String getSenderUserGradeName() {
		return senderUserGradeName;
	}
	public void setSenderUserGradeName(String senderUserGradeName) {
		this.senderUserGradeName = senderUserGradeName;
	}
	public String getReceiverUserGradeName() {
		return receiverUserGradeName;
	}
	public void setReceiverUserGradeName(String receiverUserGradeName) {
		this.receiverUserGradeName = receiverUserGradeName;
	}
	public String getSenderTransferProfileID() {
		return senderTransferProfileID;
	}
	public void setSenderTransferProfileID(String senderTransferProfileID) {
		this.senderTransferProfileID = senderTransferProfileID;
	}
	public String getReceiverTransferProfileID() {
		return receiverTransferProfileID;
	}
	public void setReceiverTransferProfileID(String receiverTransferProfileID) {
		this.receiverTransferProfileID = receiverTransferProfileID;
	}
	public String getSenderTransferProfileName() {
		return senderTransferProfileName;
	}
	public void setSenderTransferProfileName(String senderTransferProfileName) {
		this.senderTransferProfileName = senderTransferProfileName;
	}
	public String getReceiverTransferProfileName() {
		return receiverTransferProfileName;
	}
	public void setReceiverTransferProfileName(String receiverTransferProfileName) {
		this.receiverTransferProfileName = receiverTransferProfileName;
	}
	public String getGeographyCode() {
		return geographyCode;
	}
	public void setGeographyCode(String geographyCode) {
		this.geographyCode = geographyCode;
	}
	public String getGeographyName() {
		return geographyName;
	}
	public void setGeographyName(String geographyName) {
		this.geographyName = geographyName;
	}
	public String getDomainCode() {
		return domainCode;
	}
	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getSenderDualCommission() {
		return senderDualCommission;
	}
	public void setSenderDualCommission(String senderDualCommission) {
		this.senderDualCommission = senderDualCommission;
	}
	public String getReceiverDualCommission() {
		return receiverDualCommission;
	}
	public void setReceiverDualCommission(String receiverDualCommission) {
		this.receiverDualCommission = receiverDualCommission;
	}
	
	public List<UserProductDetailsForO2C> getUserProductDetails() {
		return userProductDetails;
	}
	public void setUserProductDetails(List<UserProductDetailsForO2C> userProductDetails) {
		this.userProductDetails = userProductDetails;
	}
	
	public List<C2sBalanceQueryVO> getUserBalanceDetails() {
		return userBalanceDetails;
	}
	public void setUserBalanceDetails(List<C2sBalanceQueryVO> userBalanceDetails) {
		this.userBalanceDetails = userBalanceDetails;
	}
	@Override
	public String toString() {
		return "O2CTransferUserDetails [senderUserName=" + senderUserName + ", receiverUserName=" + receiverUserName
				+ ", senderMsisdn=" + senderMsisdn + ", receiverMsisdn=" + receiverMsisdn + ", senderCategoryID="
				+ senderCategoryID + ", receiverCategoryID=" + receiverCategoryID + ", senderCategoryName="
				+ senderCategoryName + ", receiverCategoryName=" + receiverCategoryName + ", senderCommissionProfileID="
				+ senderCommissionProfileID + ", receiverCommissionProfileID=" + receiverCommissionProfileID
				+ ", senderCommissionProfileName=" + senderCommissionProfileName + ", receiverCommissionProfileName="
				+ receiverCommissionProfileName + ", senderCommissionProfileSetVersion="
				+ senderCommissionProfileSetVersion + ", receiverCommissionProfileSetVersion="
				+ receiverCommissionProfileSetVersion + ", senderUserGradeCode=" + senderUserGradeCode
				+ ", receiverUserGradeCode=" + receiverUserGradeCode + ", senderUserGradeName=" + senderUserGradeName
				+ ", receiverUserGradeName=" + receiverUserGradeName + ", senderTransferProfileID="
				+ senderTransferProfileID + ", receiverTransferProfileID=" + receiverTransferProfileID
				+ ", senderTransferProfileName=" + senderTransferProfileName + ", receiverTransferProfileName="
				+ receiverTransferProfileName + ", geographyCode=" + geographyCode + ", geographyName=" + geographyName
				+ ", domainCode=" + domainCode + ", domainName=" + domainName + ", senderDualCommission="
				+ senderDualCommission + ", receiverDualCommission=" + receiverDualCommission
				+ ", userProductDetails=" + userProductDetails + ", userBalanceDetails=" + userBalanceDetails +  "]";
	}
	
	
	
	
	
	
	


}
