package com.btsl.pretups.channel.userreturn.web;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;

/**
 * @author akanksha
 * This model will be used as form for c2c withdraw return
 */
public class ChnnlToChnnlReturnWithdrawModel implements Serializable {
private String fromUserName;
private String fromUserID;
private String domainCode;
private String domainName;
public String getDomainName() {
	return domainName;
}

public void setDomainName(String domainName) {
	this.domainName = domainName;
}

private String domainDesc;
private String fromCategoryCode;
private String fromCategoryDesc;
private String userID;
private int listSize;
private String userName;
private String toUserName;
private String toUserID;
private String toCategoryCode;
private String fromMSISDN;
private String fromGradeCode;
private String fromGradeCodeDesc;
private String fromCommissionProfileID;
private String fromCommissionProfileIDDesc;
private String fromTxnProfile;
private String fromTxnProfileDesc;
private String toMSISDN;
private String toGradeCode;
private String toGradeCodeDesc;
private String toCommissionProfileID;
private String toCommissionProfileIDDesc;
private String toTxnProfile;
private String toTxnProfileDesc;
private String currentDate;
private String toCategoryDesc;
private String requestedQuantity;
private String transferMRP;
private String payableAmount;
private String netPayableAmount;
private String totalTax1;
private String totalTax2;
private String totalTax3;
private boolean outsideHierarchyFlag;
private String remarks;
private String toCommissionProfileVersion;
private String toGeoDomain;
private String fromCommissionProfileVersion;
private String fromGeoDomain;
private boolean isReturnFlag = false;
private List userList;
private List categoryList;
private ArrayList<ChannelTransferItemsVO> productList =null;
private ArrayList<ChannelTransferItemsVO> productListWithTaxes;
private String userCode;
private String totalMRP;
private String totalComm;
private String totalReqQty;
private String totalStock;
private String transferCategory;
private ChannelTransferRuleVO channelTransferRuleVO = null;
private boolean isOperationPerformed = false;
private boolean isReturnToParentFlag = false; // this flag to indicate that
// return only to the parent
// user.
private String toDomainCode = null;
private long time = 0;
private String fromPrimaryMSISDN;
// For Mali --- +ve Commision Apply
private String senderDebitQty = null;
private String receiverCreditQty = null;
private String netCommQty = null;
// reverse trx
private String type;
private String typeDesc;
private List typeList;
private String transferNum;
private String userLoginID;
private String categoryCode;
private List channelDomainList;
private String selectedIndex;
private List transferList;
private List transferItemsList;
private ChannelTransferVO channelTransferVO = null;
private String msisdn = null;
private List oldTransferVoList = null;
private List reverseTransferList = null;
private C2STransferVO c2STransferVO = null;
private String revTransferNum;
// Added by Amit Raheja
private String smsPin = null;
private String displayMsisdn = null;
private String displayPin = null;
// Addition ends
// For CAPTCHA
private String jcaptcharesponse = null;
// user life cycle
private String toChannelUserStatus = null;
private String fromChannelUserStatus = null;

private String fromUserSosAllowed = null;
private long fromUserSosAllowedAmount = 0;
private long fromUserSosThresholdLimit = 0;
private String nameAndId;
private String displayCategoryCode;
private String toUsrDualCommType;
private String fromUsrDualCommType;


public String getToUsrDualCommType() {
	return toUsrDualCommType;
}

public void setToUsrDualCommType(String toUsrDualCommType) {
	this.toUsrDualCommType = toUsrDualCommType;
}

public String getFromUsrDualCommType() {
	return fromUsrDualCommType;
}

public void setFromUsrDualCommType(String fromUsrDualCommType) {
	this.fromUsrDualCommType = fromUsrDualCommType;
}

public String getNameAndId() {
	return nameAndId;
}

public void setNameAndId(String nameAndId) {
	this.nameAndId = nameAndId;
}

// For CAPTCHA
private String j_captcha_response = null;

public String getJ_captcha_response() {
	return j_captcha_response;
}

public void setJ_captcha_response(String j_captcha_response) {
	this.j_captcha_response = j_captcha_response;
}

public String getFromUserName() {
	return fromUserName;
}

public void setFromUserName(String fromUserName) {
	this.fromUserName = fromUserName;
}

public String getFromUserID() {
	return fromUserID;
}

public void setFromUserID(String fromUserID) {
	this.fromUserID = fromUserID;
}

public String getDomainCode() {
	return domainCode;
}

public void setDomainCode(String domainCode) {
	this.domainCode = domainCode;
}

public String getDomainDesc() {
	return domainDesc;
}

public void setDomainDesc(String domainDesc) {
	this.domainDesc = domainDesc;
}

public String getFromCategoryCode() {
	return fromCategoryCode;
}

public void setFromCategoryCode(String fromCategoryCode) {
	this.fromCategoryCode = fromCategoryCode;
}

public String getFromCategoryDesc() {
	return fromCategoryDesc;
}

public void setFromCategoryDesc(String fromCategoryDesc) {
	this.fromCategoryDesc = fromCategoryDesc;
}

public String getUserID() {
	return userID;
}

public void setUserID(String userID) {
	this.userID = userID;
}

public int getListSize() {
	return listSize;
}

public void setListSize(int listSize) {
	this.listSize = listSize;
}

public String getUserName() {
	return userName;
}

public void setUserName(String userName) {
	this.userName = userName;
}

public String getToUserName() {
	return toUserName;
}

public void setToUserName(String toUserName) {
	this.toUserName = toUserName;
}

public String getToUserID() {
	return toUserID;
}

public void setToUserID(String toUserID) {
	this.toUserID = toUserID;
}

public String getToCategoryCode() {
	return toCategoryCode;
}

public void setToCategoryCode(String toCategoryCode) {
	this.toCategoryCode = toCategoryCode;
}

public String getFromMSISDN() {
	return fromMSISDN;
}

public void setFromMSISDN(String fromMSISDN) {
	this.fromMSISDN = fromMSISDN;
}

public String getFromGradeCode() {
	return fromGradeCode;
}

public void setFromGradeCode(String fromGradeCode) {
	this.fromGradeCode = fromGradeCode;
}

public String getFromGradeCodeDesc() {
	return fromGradeCodeDesc;
}

public void setFromGradeCodeDesc(String fromGradeCodeDesc) {
	this.fromGradeCodeDesc = fromGradeCodeDesc;
}

public String getFromCommissionProfileID() {
	return fromCommissionProfileID;
}

public void setFromCommissionProfileID(String fromCommissionProfileID) {
	this.fromCommissionProfileID = fromCommissionProfileID;
}

public String getFromCommissionProfileIDDesc() {
	return fromCommissionProfileIDDesc;
}

public void setFromCommissionProfileIDDesc(String fromCommissionProfileIDDesc) {
	this.fromCommissionProfileIDDesc = fromCommissionProfileIDDesc;
}

public String getFromTxnProfile() {
	return fromTxnProfile;
}

public void setFromTxnProfile(String fromTxnProfile) {
	this.fromTxnProfile = fromTxnProfile;
}

public String getFromTxnProfileDesc() {
	return fromTxnProfileDesc;
}

public void setFromTxnProfileDesc(String fromTxnProfileDesc) {
	this.fromTxnProfileDesc = fromTxnProfileDesc;
}

public String getToMSISDN() {
	return toMSISDN;
}

public void setToMSISDN(String toMSISDN) {
	this.toMSISDN = toMSISDN;
}

public String getToGradeCode() {
	return toGradeCode;
}

public void setToGradeCode(String toGradeCode) {
	this.toGradeCode = toGradeCode;
}

public String getToGradeCodeDesc() {
	return toGradeCodeDesc;
}

public void setToGradeCodeDesc(String toGradeCodeDesc) {
	this.toGradeCodeDesc = toGradeCodeDesc;
}

public String getToCommissionProfileID() {
	return toCommissionProfileID;
}

public void setToCommissionProfileID(String toCommissionProfileID) {
	this.toCommissionProfileID = toCommissionProfileID;
}

public String getToCommissionProfileIDDesc() {
	return toCommissionProfileIDDesc;
}

public void setToCommissionProfileIDDesc(String toCommissionProfileIDDesc) {
	this.toCommissionProfileIDDesc = toCommissionProfileIDDesc;
}

public String getToTxnProfile() {
	return toTxnProfile;
}

public void setToTxnProfile(String toTxnProfile) {
	this.toTxnProfile = toTxnProfile;
}

public String getToTxnProfileDesc() {
	return toTxnProfileDesc;
}

public void setToTxnProfileDesc(String toTxnProfileDesc) {
	this.toTxnProfileDesc = toTxnProfileDesc;
}

public String getCurrentDate() {
	return currentDate;
}

public void setCurrentDate(String currentDate) {
	this.currentDate = currentDate;
}

public String getToCategoryDesc() {
	return toCategoryDesc;
}

public void setToCategoryDesc(String toCategoryDesc) {
	this.toCategoryDesc = toCategoryDesc;
}

public String getRequestedQuantity() {
	return requestedQuantity;
}

public void setRequestedQuantity(String requestedQuantity) {
	this.requestedQuantity = requestedQuantity;
}

public String getTransferMRP() {
	return transferMRP;
}

public void setTransferMRP(String transferMRP) {
	this.transferMRP = transferMRP;
}

public String getPayableAmount() {
	return payableAmount;
}

public void setPayableAmount(String payableAmount) {
	this.payableAmount = payableAmount;
}

public String getNetPayableAmount() {
	return netPayableAmount;
}

public void setNetPayableAmount(String netPayableAmount) {
	this.netPayableAmount = netPayableAmount;
}

public String getTotalTax1() {
	return totalTax1;
}

public void setTotalTax1(String totalTax1) {
	this.totalTax1 = totalTax1;
}

public String getTotalTax2() {
	return totalTax2;
}

public void setTotalTax2(String totalTax2) {
	this.totalTax2 = totalTax2;
}

public String getTotalTax3() {
	return totalTax3;
}

public void setTotalTax3(String totalTax3) {
	this.totalTax3 = totalTax3;
}

public boolean isOutsideHierarchyFlag() {
	return outsideHierarchyFlag;
}

public void setOutsideHierarchyFlag(boolean outsideHierarchyFlag) {
	this.outsideHierarchyFlag = outsideHierarchyFlag;
}

public String getRemarks() {
	return remarks;
}

public void setRemarks(String remarks) {
	this.remarks = remarks;
}

public String getToCommissionProfileVersion() {
	return toCommissionProfileVersion;
}

public void setToCommissionProfileVersion(String toCommissionProfileVersion) {
	this.toCommissionProfileVersion = toCommissionProfileVersion;
}

public String getToGeoDomain() {
	return toGeoDomain;
}

public void setToGeoDomain(String toGeoDomain) {
	this.toGeoDomain = toGeoDomain;
}

public String getFromCommissionProfileVersion() {
	return fromCommissionProfileVersion;
}

public void setFromCommissionProfileVersion(String fromCommissionProfileVersion) {
	this.fromCommissionProfileVersion = fromCommissionProfileVersion;
}

public String getFromGeoDomain() {
	return fromGeoDomain;
}

public void setFromGeoDomain(String fromGeoDomain) {
	this.fromGeoDomain = fromGeoDomain;
}

public boolean getIsReturnFlag() {
	return isReturnFlag;
}

public boolean getReturnFlag() {
    return isReturnFlag;
}

public void setReturnFlag(boolean isReturnFlag) {
	this.isReturnFlag = isReturnFlag;
}

public List getUserList() {
	return userList;
}

public void setUserList(List userList) {
	this.userList = userList;
}

public List getCategoryList() {
	return categoryList;
}

public void setCategoryList(List categoryList) {
	this.categoryList = categoryList;
}

public ArrayList<ChannelTransferItemsVO> getProductList() {
	return productList;
}

public void setProductList(ArrayList<ChannelTransferItemsVO> productList) {
	this.productList = productList;
}

public ArrayList<ChannelTransferItemsVO> getProductListWithTaxes() {
	return productListWithTaxes;
}

public void setProductListWithTaxes(ArrayList<ChannelTransferItemsVO> productListWithTaxes) {
	this.productListWithTaxes = productListWithTaxes;
}

public String getUserCode() {
	return userCode;
}

public void setUserCode(String userCode) {
	this.userCode = userCode;
}

public String getTotalMRP() {
	return totalMRP;
}

public void setTotalMRP(String totalMRP) {
	this.totalMRP = totalMRP;
}

public String getTotalComm() {
	return totalComm;
}

public void setTotalComm(String totalComm) {
	this.totalComm = totalComm;
}

public String getTotalReqQty() {
	return totalReqQty;
}

public void setTotalReqQty(String totalReqQty) {
	this.totalReqQty = totalReqQty;
}

public String getTotalStock() {
	return totalStock;
}

public void setTotalStock(String totalStock) {
	this.totalStock = totalStock;
}

public String getTransferCategory() {
	return transferCategory;
}

public void setTransferCategory(String transferCategory) {
	this.transferCategory = transferCategory;
}

public ChannelTransferRuleVO getChannelTransferRuleVO() {
	return channelTransferRuleVO;
}

public void setChannelTransferRuleVO(ChannelTransferRuleVO channelTransferRuleVO) {
	this.channelTransferRuleVO = channelTransferRuleVO;
}

public boolean isOperationPerformed() {
	return isOperationPerformed;
}

public void setOperationPerformed(boolean isOperationPerformed) {
	this.isOperationPerformed = isOperationPerformed;
}

public boolean getIsReturnToParentFlag() {
	return isReturnToParentFlag;
}

public void setReturnToParentFlag(boolean isReturnToParentFlag) {
	this.isReturnToParentFlag = isReturnToParentFlag;
}

public String getToDomainCode() {
	return toDomainCode;
}

public void setToDomainCode(String toDomainCode) {
	this.toDomainCode = toDomainCode;
}

public long getTime() {
	return time;
}

public void setTime(long time) {
	this.time = time;
}

public String getFromPrimaryMSISDN() {
	return fromPrimaryMSISDN;
}

public void setFromPrimaryMSISDN(String fromPrimaryMSISDN) {
	this.fromPrimaryMSISDN = fromPrimaryMSISDN;
}

public String getSenderDebitQty() {
	return senderDebitQty;
}

public void setSenderDebitQty(String senderDebitQty) {
	this.senderDebitQty = senderDebitQty;
}

public String getReceiverCreditQty() {
	return receiverCreditQty;
}

public void setReceiverCreditQty(String receiverCreditQty) {
	this.receiverCreditQty = receiverCreditQty;
}

public String getNetCommQty() {
	return netCommQty;
}

public void setNetCommQty(String netCommQty) {
	this.netCommQty = netCommQty;
}

public String getType() {
	return type;
}

public void setType(String type) {
	this.type = type;
}

public String getTypeDesc() {
	return typeDesc;
}

public void setTypeDesc(String typeDesc) {
	this.typeDesc = typeDesc;
}

public List getTypeList() {
	return typeList;
}

public void setTypeList(List typeList) {
	this.typeList = typeList;
}

public String getTransferNum() {
	return transferNum;
}

public void setTransferNum(String transferNum) {
	this.transferNum = transferNum;
}

public String getUserLoginID() {
	return userLoginID;
}

public void setUserLoginID(String userLoginID) {
	this.userLoginID = userLoginID;
}

public String getCategoryCode() {
	return categoryCode;
}

public void setCategoryCode(String categoryCode) {
	this.categoryCode = categoryCode;
}

public List getChannelDomainList() {
	return channelDomainList;
}

public void setChannelDomainList(List channelDomainList) {
	this.channelDomainList = channelDomainList;
}

public String getSelectedIndex() {
	return selectedIndex;
}

public void setSelectedIndex(String selectedIndex) {
	this.selectedIndex = selectedIndex;
}

public List getTransferList() {
	return transferList;
}

public void setTransferList(List transferList) {
	this.transferList = transferList;
}

public List getTransferItemsList() {
	return transferItemsList;
}

public void setTransferItemsList(List transferItemsList) {
	this.transferItemsList = transferItemsList;
}

public ChannelTransferVO getChannelTransferVO() {
	return channelTransferVO;
}

public void setChannelTransferVO(ChannelTransferVO channelTransferVO) {
	this.channelTransferVO = channelTransferVO;
}

public String getMsisdn() {
	return msisdn;
}

public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
}

public List getOldTransferVoList() {
	return oldTransferVoList;
}

public void setOldTransferVoList(List oldTransferVoList) {
	this.oldTransferVoList = oldTransferVoList;
}

public List getReverseTransferList() {
	return reverseTransferList;
}

public void setReverseTransferList(List reverseTransferList) {
	this.reverseTransferList = reverseTransferList;
}

public C2STransferVO getC2STransferVO() {
	return c2STransferVO;
}

public void setC2STransferVO(C2STransferVO c2sTransferVO) {
	c2STransferVO = c2sTransferVO;
}

public String getRevTransferNum() {
	return revTransferNum;
}

public void setRevTransferNum(String revTransferNum) {
	this.revTransferNum = revTransferNum;
}

public String getSmsPin() {
	return smsPin;
}

public void setSmsPin(String smsPin) {
	this.smsPin = smsPin;
}

public String getDisplayMsisdn() {
	return displayMsisdn;
}

public void setDisplayMsisdn(String displayMsisdn) {
	this.displayMsisdn = displayMsisdn;
}

public String getDisplayPin() {
	return displayPin;
}

public void setDisplayPin(String displayPin) {
	this.displayPin = displayPin;
}

public String getJcaptcharesponse() {
	return jcaptcharesponse;
}

public void setJcaptcharesponse(String jcaptcharesponse) {
	this.jcaptcharesponse = jcaptcharesponse;
}

public String getToChannelUserStatus() {
	return toChannelUserStatus;
}

public void setToChannelUserStatus(String toChannelUserStatus) {
	this.toChannelUserStatus = toChannelUserStatus;
}

public String getFromChannelUserStatus() {
	return fromChannelUserStatus;
}

public void setFromChannelUserStatus(String fromChannelUserStatus) {
	this.fromChannelUserStatus = fromChannelUserStatus;
}

public String getFromUserSosAllowed() {
	return fromUserSosAllowed;
}

public void setFromUserSosAllowed(String fromUserSosAllowed) {
	this.fromUserSosAllowed = fromUserSosAllowed;
}

public long getFromUserSosAllowedAmount() {
	return fromUserSosAllowedAmount;
}

public void setFromUserSosAllowedAmount(long fromUserSosAllowedAmount) {
	this.fromUserSosAllowedAmount = fromUserSosAllowedAmount;
}

public long getFromUserSosThresholdLimit() {
	return fromUserSosThresholdLimit;
}

public void setFromUserSosThresholdLimit(long fromUserSosThresholdLimit) {
	this.fromUserSosThresholdLimit = fromUserSosThresholdLimit;
}

	
    
    
    /**
     * Method flust()
     * This method is to flush all the contents from the FormBean
     * void
     */
    public void flush() {
        fromUserName = null;
        fromUserID = null;
        domainCode = null;
        domainDesc = null;
        fromCategoryCode = null;
        fromCategoryDesc = null;
        userID = null;
        listSize = 0;
        userName = null;
        toUserName = null;
        toUserID = null;
        toCategoryCode = null;
        fromMSISDN = null;
        fromGradeCode = null;
        fromGradeCodeDesc = null;
        fromCommissionProfileID = null;
        fromCommissionProfileIDDesc = null;
        fromTxnProfile = null;
        fromTxnProfileDesc = null;
        toMSISDN = null;
        toGradeCode = null;
        toGradeCodeDesc = null;
        toCommissionProfileID = null;
        toCommissionProfileIDDesc = null;
        toTxnProfile = null;
        toTxnProfileDesc = null;
        currentDate = null;
        toCategoryDesc = null;

        requestedQuantity = null;
        transferMRP = null;
        payableAmount = null;
        netPayableAmount = null;
        totalTax1 = null;
        totalTax2 = null;
        totalTax3 = null;
        outsideHierarchyFlag = false;
        remarks = null;
        toCommissionProfileVersion = null;
        toGeoDomain = null;
        fromCommissionProfileVersion = null;
        fromGeoDomain = null;

        isReturnFlag = false;

        userList = null;
        categoryList = null;
        productList = null;
        productListWithTaxes = null;
        channelTransferRuleVO = null;
        userCode = null;
        transferCategory = null;
        isReturnToParentFlag = false;
        type = null;
        typeList = null;
        transferNum = null;
        userLoginID = null;
        channelDomainList = null;
        categoryCode = null;
        typeDesc = null;
        selectedIndex = null;
        transferList = null;
        transferItemsList = null;
        channelTransferVO = null;
        msisdn = null;
        c2STransferVO = null;
        // For Mali --- +ve Commision Apply
        senderDebitQty = null;
        receiverCreditQty = null;
        netCommQty = null;
        revTransferNum = null;
        smsPin = null;
        displayMsisdn = null;
        displayPin = null;

    }

    /**
     * method flushProductDetail()
     * This method is to flush the products information
     * void
     */
    public void flushProductDetail() {
        productList = null;
        productListWithTaxes = null;
        remarks = null;
    }



   

}
