package com.web.pretups.channel.transfer.web;

import java.io.Serializable;
import java.util.ArrayList;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;

/**
 * @author yogesh.keshari
 * This model will be used as form for c2c transfer
 */

public class C2CTransferModel implements Serializable {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
    public static final Log log = LogFactory.getLog(C2CTransferModel.class.getName());
    
    private String domainCode;

    private String domainDesc;

    private ArrayList categoryList;

    private String fromCategoryCode;

    private String fromCategoryDesc;

    private String toCategoryCode;

    private String toCategoryDesc;

    private String toUserName;

    private String toUserID;

    private String fromUserName;

    private String fromUserID;

 
    private ArrayList userList;

    private String userName;

    private int listSize;

    private String userID;

    // Transfer Details
    private String toMSISDN;

    private String toGradeCode;

    private String toGradeCodeDesc;

    private String toCommissionProfileID;

    private String toCommissionProfileIDDesc;

    private String toCommissionProfileVersion;

    private String toTxnProfile;

    private String toTxnProfileDesc;

    private String toGeoDomain;

    private String toGeoDomainDesc;

    private String fromMSISDN;

    private String fromGradeCode;

    private String fromGradeCodeDesc;

    private String fromCommissionProfileID;

    private String fromCommissionProfileIDDesc;

    private String fromCommissionProfileVersion;

    private String fromTxnProfile;

    private String fromTxnProfileDesc;

    private String fromGeoDomain;

    private String fromGeoDomainDesc;

    private String refrenceNum;

    private String currDate;

    private String remarks;

    private String requestedQuantity;

    private String transferMRP;

    private String serviceTaxAmount;

    private String payableAmount;

    private String netPayableAmount;

    private String totalTax1;

    private String totalTax2;

    private String totalTax3;

    private String currentDate;

    private ArrayList productList;

    private ArrayList productListWithTaxes;

    private boolean outsideHierarchyFlag;

    private String userCode;

    private String totalMRP;
    private String totalComm;
    private String totalReqQty;
    private String totalStock;
    private String totalBalance;
    private String transferCategory;

    private String toDomainCode = null;
    private long time = 0;
    private String toPrimaryMSISDN;
    // For mali -- +ve commision apply
    private String netCommision = null;
    private String senderDrQty = null;
    private String receiverCrQty = null;
    private String productCode = null;

    // added by nilesh: SMS PIN for C2C
    private String smsPin = null;
    private String displayMsisdn = null;
    private String displayPin = null;
    // For CAPTCHA
    private String jcaptcharesponse = null;

    // user life cycle
    private String toChannelUserStatus = null;
    private String fromChannelUserStatus = null;
    private boolean otfCountsUpdated = false;
    private String networkCode = null;
    private String commSetId = null;
    private String commLatestVersion = null;
    private ArrayList channelTransferitemsVOList;
    private boolean targetAchieved;
    
    private String domainName;
    
    private String searchLoginId;
    
    private String toUsrDualCommType;
    private String fromUsrDualCommType;
    
    

	public String getFromUsrDualCommType() {
		return fromUsrDualCommType;
	}

	public void setFromUsrDualCommType(String fromUsrDualCommType) {
		this.fromUsrDualCommType = fromUsrDualCommType;
	}

	public String getToUsrDualCommType() {
		return toUsrDualCommType;
	}

	public void setToUsrDualCommType(String toUsrDualCommType) {
		this.toUsrDualCommType = toUsrDualCommType;
	}

	public boolean isTargetAchieved() {
		return targetAchieved;
	}

	public void setTargetAchieved(boolean targetAchieved) {
		this.targetAchieved = targetAchieved;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getCommSetId() {
		return commSetId;
	}

	public void setCommSetId(String commSetId) {
		this.commSetId = commSetId;
	}

	public String getCommLatestVersion() {
		return commLatestVersion;
	}

	public void setCommLatestVersion(String commLatestVersion) {
		this.commLatestVersion = commLatestVersion;
	}

	public boolean isOtfCountsUpdated() {
		return otfCountsUpdated;
	}

	public void setOtfCountsUpdated(boolean otfCountsUpdated) {
		this.otfCountsUpdated = otfCountsUpdated;
	}

	public String getToDomainCode() {
        return toDomainCode;
    }

    public void setToDomainCode(String toDomainCode) {
        this.toDomainCode = toDomainCode;
    }

    /**
     * @return Returns the totalBalance.
     */
    public String getTotalBalance() {
        return totalBalance;
    }

    /**
     * @param totalBalance
     *            The totalBalance to set.
     */
    public void setTotalBalance(String totalBalance) {
        this.totalBalance = totalBalance;
    }

    public String getTotalComm() {
        return totalComm;
    }

    public void setTotalComm(String totalComm) {
    	this.totalComm = totalComm;
    }

    public String getTotalMRP() {
        return totalMRP;
    }

    public void setTotalMRP(String totalMRP) {
    	this.totalMRP = totalMRP;
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

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public boolean getOutsideHierarchyFlag() {
        return outsideHierarchyFlag;
    }

    public void setOutsideHierarchyFlag(boolean outsideHierarchy) {
        this.outsideHierarchyFlag = outsideHierarchy;
    }

    public void setDataListIndexed(int i, ChannelTransferItemsVO vo) {
        productList.set(i, vo);
    }

    public ChannelTransferItemsVO getDataListIndexed(int i) {
        return (ChannelTransferItemsVO) productList.get(i);
    }

    public ArrayList<ChannelTransferItemsVO> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<ChannelTransferItemsVO> productList) {
        this.productList = productList;
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

    public void setUserID(String userId) {
    	this.userID = userId;
    }

    public ArrayList getUserList() {
        return userList;
    }

    public void setUserList(ArrayList userList) {
    	this.userList = userList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
    	this.userName = userName;
    }

    public String getCurrDate() {
        return currDate;
    }

    public void setCurrDate(String currDate) {
    	this.currDate = currDate;
    }

    public String getFromCommissionProfileID() {
        return fromCommissionProfileID;
    }

    public void setFromCommissionProfileID(String fromCommissionProfileID) {
    	this.fromCommissionProfileID = fromCommissionProfileID;
    }

    public String getFromGeoDomain() {
        return fromGeoDomain;
    }

    public void setFromGeoDomain(String fromGeoDomain) {
    	this.fromGeoDomain = fromGeoDomain;
    }

    public String getFromGradeCode() {
        return fromGradeCode;
    }

    public void setFromGradeCode(String fromGradeCode) {
    	this.fromGradeCode = fromGradeCode;
    }

    public String getFromMSISDN() {
        return fromMSISDN;
    }

    public void setFromMSISDN(String fromMSISDN) {
    	this.fromMSISDN = fromMSISDN;
    }

    public String getFromTxnProfile() {
        return fromTxnProfile;
    }

    public void setFromTxnProfile(String fromTxnProfile) {
    	this.fromTxnProfile = fromTxnProfile;
    }

    public String getRefrenceNum() {
        return refrenceNum;
    }

    public void setRefrenceNum(String refrenceNum) {
    	this.refrenceNum = refrenceNum;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
    	this.remarks = remarks;
    }

    public String getToCategoryCode() {
        return toCategoryCode;
    }

    public void setToCategoryCode(String toCategoryCode) {
    	this.toCategoryCode = toCategoryCode;
    }

    public String getToCategoryDesc() {
        return toCategoryDesc;
    }

    public void setToCategoryDesc(String toCategoryDesc) {
    	this.toCategoryDesc = toCategoryDesc;
    }

    public String getToCommissionProfileID() {
        return toCommissionProfileID;
    }

    public void setToCommissionProfileID(String toCommissionProfileID) {
    	this.toCommissionProfileID = toCommissionProfileID;
    }

    public String getToGeoDomain() {
        return toGeoDomain;
    }

    public void setToGeoDomain(String toGeoDomain) {
    	this.toGeoDomain = toGeoDomain;
    }

    public String getToGradeCode() {
        return toGradeCode;
    }

    public void setToGradeCode(String toGradeCode) {
    	this.toGradeCode = toGradeCode;
    }

    public String getToMSISDN() {
        return toMSISDN;
    }

    public void setToMSISDN(String toMSISDN) {
    	this.toMSISDN = toMSISDN;
    }

    public String getToTxnProfile() {
        return toTxnProfile;
    }

    public void setToTxnProfile(String toTxnProfile) {
    	this.toTxnProfile = toTxnProfile;
    }

    public String getToUserID() {
        return toUserID;
    }

    public void setToUserID(String toUserID) {
    	this.toUserID = toUserID;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
    	this.toUserName = toUserName;
    }

    public String getFromCommissionProfileIDDesc() {
        return fromCommissionProfileIDDesc;
    }

    public void setFromCommissionProfileIDDesc(String fromCommissionProfileIDDesc) {
    	this.fromCommissionProfileIDDesc = fromCommissionProfileIDDesc;
    }

    public String getFromGeoDomainDesc() {
        return fromGeoDomainDesc;
    }

    public void setFromGeoDomainDesc(String fromGeoDomainDesc) {
    	this.fromGeoDomainDesc = fromGeoDomainDesc;
    }

    public String getFromGradeCodeDesc() {
        return fromGradeCodeDesc;
    }

    public void setFromGradeCodeDesc(String fromGradeCodeDesc) {
    	this.fromGradeCodeDesc = fromGradeCodeDesc;
    }

    public String getFromTxnProfileDesc() {
        return fromTxnProfileDesc;
    }

    public void setFromTxnProfileDesc(String fromTxnProfileDesc) {
    	this.fromTxnProfileDesc = fromTxnProfileDesc;
    }

    public String getToCommissionProfileIDDesc() {
        return toCommissionProfileIDDesc;
    }

    public void setToCommissionProfileIDDesc(String toCommissionProfileIDDesc) {
    	this.toCommissionProfileIDDesc = toCommissionProfileIDDesc;
    }

    public String getToGeoDomainDesc() {
        return toGeoDomainDesc;
    }

    public void setToGeoDomainDesc(String toGeoDomainDesc) {
    	this.toGeoDomainDesc = toGeoDomainDesc;
    }

    public String getToGradeCodeDesc() {
        return toGradeCodeDesc;
    }

    public void setToGradeCodeDesc(String toGradeCodeDesc) {
    	this.toGradeCodeDesc = toGradeCodeDesc;
    }

    public String getToTxnProfileDesc() {
        return toTxnProfileDesc;
    }

    public void setToTxnProfileDesc(String toTxnProfileDesc) {
    	this.toTxnProfileDesc = toTxnProfileDesc;
    }

    public ArrayList getProductListWithTaxes() {
        return productListWithTaxes;
    }

    public void setProductListWithTaxes(ArrayList productListWithTaxes) {
    	this.productListWithTaxes = productListWithTaxes;
    }

    public String getFromCommissionProfileVersion() {
        return fromCommissionProfileVersion;
    }

    public void setFromCommissionProfileVersion(String fromCommissionProfileVersion) {
    	this.fromCommissionProfileVersion = fromCommissionProfileVersion;
    }

    public String getToCommissionProfileVersion() {
        return toCommissionProfileVersion;
    }

    public void setToCommissionProfileVersion(String toCommissionProfileVersion) {
    	this.toCommissionProfileVersion = toCommissionProfileVersion;
    }

    public String getNetPayableAmount() {
        return netPayableAmount;
    }

    public void setNetPayableAmount(String netPayableAmount) {
    	this.netPayableAmount = netPayableAmount;
    }

    public String getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(String payableAmount) {
    	this.payableAmount = payableAmount;
    }

    public String getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(String requestedQuantity) {
    	this.requestedQuantity = requestedQuantity;
    }

    public String getServiceTaxAmount() {
        return serviceTaxAmount;
    }

    public void setServiceTaxAmount(String serviceTaxAmount) {
    	this.serviceTaxAmount = serviceTaxAmount;
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

    public String getTransferMRP() {
        return transferMRP;
    }

    public void setTransferMRP(String transferMRP) {
    	this.transferMRP = transferMRP;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
    	this.currentDate = currentDate;
    }

    public ArrayList getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(ArrayList categoryList) {
    	this.categoryList = categoryList;
    }

    public String getFromUserID() {
        return fromUserID;
    }

    public void setFromUserID(String fromUserID) {
    	this.fromUserID = fromUserID;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
    	this.fromUserName = fromUserName;
    }




   
    public int getListSize() {
        return listSize;
    }

    public void setListSize(int listSize) {
    	this.listSize = listSize;
    }

    public String getTransferCategory() {
        return transferCategory;
    }

    public void setTransferCategory(String transferCategory) {
    	this.transferCategory = transferCategory;
    }

    /**
     * @return Returns the time.
     */
    public long getTime() {
        return time;
    }

    /**
     * @param time
     *            The time to set.
     */
    public void setTime(long time) {
    	this.time = time;
    }

    /**
     * @return Returns the toPrimaryMSISDN.
     */
    public String getToPrimaryMSISDN() {
        return toPrimaryMSISDN;
    }

    /**
     * @param toPrimaryMSISDN
     *            The toPrimaryMSISDN to set.
     */
    public void setToPrimaryMSISDN(String toPrimaryMSISDN) {
    	this.toPrimaryMSISDN = toPrimaryMSISDN;
    }

    /**
     * @return Returns the netCommision.
     */
    public String getNetCommision() {
        return netCommision;
    }

    /**
     * @return Returns the senderDrQty.
     */
    public String getSenderDrQty() {
        return senderDrQty;
    }

    /**
     * @return Returns the receiverCrQty.
     */
    public String getReceiverCrQty() {
        return receiverCrQty;
    }

    /**
     * @param netCommision
     *            The netCommision to set.
     */
    public void setNetCommision(String netCommision) {
    	this.netCommision = netCommision;
    }

    /**
     * @param senderDrQty
     *            The senderDrQty to set.
     */
    public void setSenderDrQty(String senderDrQty) {
        this.senderDrQty = senderDrQty;
    }

    /**
     * @param receiverCrQty
     *            The receiverCrQty to set.
     */
    public void setReceiverCrQty(String receiverCrQty) {
        this.receiverCrQty = receiverCrQty;
    }

    // added by nilesh
    public String getProductCode() {
        return productCode;
    }
    public void setProductCode(String productCode) {
    	this.productCode = productCode;
    }

   

    // added by nilesh : SMS PIN for C2C
    /**
     * @return Returns the smsPin.
     */
    public String getSmsPin() {
        return smsPin;
    }

    /**
     * @param smsPin
     *            The smsPin to set.
     */
    public void setSmsPin(String smsPin) {
    	this.smsPin = smsPin;
    }

    /**
     * @return Returns the displayMsisdn.
     */
    public String getDisplayMsisdn() {
        return displayMsisdn;
    }

    /**
     * @param displayMsisdn
     *            The displayMsisdn to set.
     */
    public void setDisplayMsisdn(String displayMsisdn) {
    	this.displayMsisdn = displayMsisdn;
    }

    /**
     * @return Returns the displayPin.
     */
    public String getDisplayPin() {
        return displayPin;
    }

    /**
     * @param displayPin
     *            The displayPin to set.
     */
    public void setDisplayPin(String displayPin) {
    	this.displayPin = displayPin;
    }

    public String getJcaptcharesponse() {
        return jcaptcharesponse;
    }

    public void setJcaptcharesponse(String jcaptcharesponse) {
        this.jcaptcharesponse = jcaptcharesponse;
    }

    public String getFromChannelUserStatus() {
        return fromChannelUserStatus;
    }

    public void setFromChannelUserStatus(String channelUserStatus) {
        fromChannelUserStatus = channelUserStatus;
    }

    public String getToChannelUserStatus() {
        return toChannelUserStatus;
    }

    public void setToChannelUserStatus(String channelUserStatus) {
        toChannelUserStatus = channelUserStatus;
    }
    public ArrayList getChannelTransferitemsVOList() {
        return channelTransferitemsVOList;
    }

    public void setChannelTransferitemsVOList(ArrayList channelTransferitemsVO) {
        channelTransferitemsVOList = channelTransferitemsVO;
    }

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getSearchLoginId() {
		return searchLoginId;
	}

	public void setSearchLoginId(String searchLoginId) {
		this.searchLoginId = searchLoginId;
	}



}
