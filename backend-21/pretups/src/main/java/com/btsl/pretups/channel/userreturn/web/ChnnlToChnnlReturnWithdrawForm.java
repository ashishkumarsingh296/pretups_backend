/**
 * @(#)ChnnlToChnnlReturnWithdrawForm.java
 *                                         Copyright(c) 2005, Bharti Telesoft
 *                                         Ltd.
 *                                         All Rights Reserved
 * 
 *                                         <description>
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         -----------------------
 *                                         Author Date History
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         -----------------------
 *                                         avinash.kamthan Sep 5, 2005 Initital
 *                                         Creation
 *                                         Sandeep Goel Nov 10,2005
 *                                         Customization,Modification
 *                                         Sandeep Goel Aug, 03,2006
 *                                         Modification ID QTY001
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         -----------------------
 * 
 */

package com.btsl.pretups.channel.userreturn.web;

import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;

/*import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorActionForm;*/

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.user.businesslogic.UserLoanVO;

/**
 * @author avinash.kamthan
 * 
 */
public class ChnnlToChnnlReturnWithdrawForm /*extends ValidatorActionForm*/ {
    private Log _log = LogFactory.getLog(ChnnlToChnnlReturnWithdrawForm.class.getName());
    private String _fromUserName;
    private String _fromUserID;
    private String _domainCode;
    private String _domainDesc;
    private String _fromCategoryCode;
    private String _fromCategoryDesc;
    private String _userID;
    private int _listSize;
    private String _userName;
    private String _toUserName;
    private String _toUserID;
    private String _toCategoryCode;
    private String _fromMSISDN;
    private String _fromGradeCode;
    private String _fromGradeCodeDesc;
    private String _fromCommissionProfileID;
    private String _fromCommissionProfileIDDesc;
    private String _fromTxnProfile;
    private String _fromTxnProfileDesc;
    private String _toMSISDN;
    private String _toGradeCode;
    private String _toGradeCodeDesc;
    private String _toCommissionProfileID;
    private String _toCommissionProfileIDDesc;
    private String _toTxnProfile;
    private String _toTxnProfileDesc;
    private String _currentDate;
    private String _toCategoryDesc;

    private String _requestedQuantity;
    private String _transferMRP;
    private String _payableAmount;
    private String _netPayableAmount;
    private String _totalTax1;
    private String _totalTax2;
    private String _totalTax3;
    private boolean _outsideHierarchyFlag;
    private String _remarks;
    private String _toCommissionProfileVersion;
    private String _toGeoDomain;
    private String _fromCommissionProfileVersion;
    private String _fromGeoDomain;

    private boolean _isReturnFlag = false;

    private ArrayList _userList;
    private ArrayList _categoryList;
    private ArrayList _productList;
    private ArrayList _productListWithTaxes;
    private String _userCode;

    private String _totalMRP;
    private String _totalComm;
    private String _totalReqQty;
    private String _totalStock;
    private String _transferCategory;

    private ChannelTransferRuleVO _channelTransferRuleVO = null;

    private boolean _isOperationPerformed = false;
    private boolean _isReturnToParentFlag = false; // this flag to indicate that
    // return only to the parent
    // user.
    private String _toDomainCode = null;
    private long _time = 0;
    private String _fromPrimaryMSISDN;
    // For Mali --- +ve Commision Apply
    private String _senderDebitQty = null;
    private String _receiverCreditQty = null;
    private String _netCommQty = null;
    // reverse trx
    private String _type;
    private String _typeDesc;
    private ArrayList _typeList;
    private String _transferNum;
    private String _userLoginID;
    private String _categoryCode;
    private ArrayList _channelDomainList;
    private String _selectedIndex;
    private ArrayList _transferList;
    private ArrayList _transferItemsList;
    private ChannelTransferVO _channelTransferVO = null;
    private String _msisdn = null;
    private ArrayList _oldTransferVoList = null;
    private ArrayList _reverseTransferList = null;
    private C2STransferVO _c2STransferVO = null;
    private String _revTransferNum;
    // Added by Amit Raheja
    private String _smsPin = null;
    private String _displayMsisdn = null;
    private String _displayPin = null;
    // Addition ends
    // For CAPTCHA
    private String j_captcha_response = null;
    // user life cycle
    private String _toChannelUserStatus = null;
    private String _fromChannelUserStatus = null;
    
    private String fromUserSosAllowed = null;
    private long fromUserSosAllowedAmount = 0;
    private long fromUserSosThresholdLimit = 0;
    private String toUsrDualCommType;
    private String fromUsrDualCommType;
	private String _totalOtherComm;
    private String totalOtfValue;
    private String networkCode;
    private ArrayList<UserLoanVO> fromUserLoanVOList;
    public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getTotalOtfValue() {
		return totalOtfValue;
	}

	public void setTotalOtfValue(String totalOtfValue) {
		this.totalOtfValue = totalOtfValue;
	}

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

	public int getChannelDomainListSize() {
        if (_channelDomainList != null) {
            return _channelDomainList.size();
        }
        return 0;
    }

    public int getTransferListSize() {
        if (_transferList != null) {
            return _transferList.size();
        }
        return 0;
    }

    public int getReverseTransferListSize() {
        if (_reverseTransferList != null) {
            return _reverseTransferList.size();
        }
        return 0;
    }

    public String getToDomainCode() {
        return _toDomainCode;
    }

    public void setToDomainCode(String toDomainCode) {
        _toDomainCode = toDomainCode;
    }

    public ChannelTransferRuleVO getChannelTransferRuleVO() {
        return _channelTransferRuleVO;
    }

    public void setChannelTransferRuleVO(ChannelTransferRuleVO channelTransferRuleVO) {
        _channelTransferRuleVO = channelTransferRuleVO;
    }

    public String getTotalComm() {
        return _totalComm;
    }

    public void setTotalComm(String totalComm) {
        _totalComm = totalComm;
    }

    public String getTotalMRP() {
        return _totalMRP;
    }

    public void setTotalMRP(String totalMRP) {
        _totalMRP = totalMRP;
    }

    public String getTotalReqQty() {
        return _totalReqQty;
    }

    public void setTotalReqQty(String totalReqQty) {
        _totalReqQty = totalReqQty;
    }

    public String getTotalStock() {
        return _totalStock;
    }

    public void setTotalStock(String totalStock) {
        _totalStock = totalStock;
    }

    public boolean getIsReturnFlag() {
        return _isReturnFlag;
    }

    public String getUserCode() {
        return _userCode;
    }

    public void setUserCode(String userCode) {
        _userCode = userCode;
    }

    public void setProductListIndexed(int i, ChannelTransferItemsVO vo) {
        _productList.set(i, vo);
    }

    public ChannelTransferItemsVO getProductListIndexed(int i) {
        return (ChannelTransferItemsVO) _productList.get(i);
    }

    public ArrayList getProductListWithTaxes() {
        return _productListWithTaxes;
    }

    public void setProductListWithTaxes(ArrayList productListWithTaxes) {
        _productListWithTaxes = productListWithTaxes;
    }

    public ArrayList getProductList() {
        return _productList;
    }

    public void setProductList(ArrayList productList) {
        this._productList = productList;
    }

    public ArrayList getCategoryList() {
        return _categoryList;
    }

    public int getCategoryListSize() {
        if (_categoryList != null) {
            return _categoryList.size();
        }
        return 0;
    }

    public void setCategoryList(ArrayList categoryList) {
        _categoryList = categoryList;
    }

    public String getDomainCode() {
        return _domainCode;
    }

    public void setDomainCode(String domainCode) {
        _domainCode = domainCode;
    }

    public String getDomainDesc() {
        return _domainDesc;
    }

    public void setDomainDesc(String domainDesc) {
        _domainDesc = domainDesc;
    }

    public String getFromCategoryCode() {
        return _fromCategoryCode;
    }

    public void setFromCategoryCode(String fromCategoryCode) {
        _fromCategoryCode = fromCategoryCode;
    }

    public String getFromCategoryDesc() {
        return _fromCategoryDesc;
    }

    public void setFromCategoryDesc(String fromCategoryDesc) {
        _fromCategoryDesc = fromCategoryDesc;
    }

    public String getFromUserID() {
        return _fromUserID;
    }

    public void setFromUserID(String fromUserID) {
        _fromUserID = fromUserID;
    }

    public String getFromUserName() {
        return _fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        _fromUserName = fromUserName;
    }

    public int getListSize() {
        return _listSize;
    }

    public void setListSize(int listSize) {
        _listSize = listSize;
    }

    public String getUserID() {
        return _userID;
    }

    public void setUserID(String userID) {
        _userID = userID;
    }

    public ArrayList getUserList() {
        return _userList;
    }

    public void setUserList(ArrayList userList) {
        _userList = userList;
    }

    public String getUserName() {
        return _userName;
    }

    public void setUserName(String userName) {
        _userName = userName;
    }

    public String getToUserID() {
        return _toUserID;
    }

    public void setToUserID(String toUserID) {
        _toUserID = toUserID;
    }

    public String getToUserName() {
        return _toUserName;
    }

    public void setToUserName(String toUserName) {
        _toUserName = toUserName;
    }

    public String getToCategoryCode() {
        return _toCategoryCode;
    }

    public void setToCategoryCode(String toCategoryCode) {
        _toCategoryCode = toCategoryCode;
    }

    public String getCurrentDate() {
        return _currentDate;
    }

    public void setCurrentDate(String currentDate) {
        _currentDate = currentDate;
    }

    public String getFromCommissionProfileID() {
        return _fromCommissionProfileID;
    }

    public void setFromCommissionProfileID(String fromCommissionProfileID) {
        _fromCommissionProfileID = fromCommissionProfileID;
    }

    public String getFromCommissionProfileIDDesc() {
        return _fromCommissionProfileIDDesc;
    }

    public void setFromCommissionProfileIDDesc(String fromCommissionProfileIDDesc) {
        _fromCommissionProfileIDDesc = fromCommissionProfileIDDesc;
    }

    public String getFromGradeCode() {
        return _fromGradeCode;
    }

    public void setFromGradeCode(String fromGradeCode) {
        _fromGradeCode = fromGradeCode;
    }

    public String getFromGradeCodeDesc() {
        return _fromGradeCodeDesc;
    }

    public void setFromGradeCodeDesc(String fromGradeCodeDesc) {
        _fromGradeCodeDesc = fromGradeCodeDesc;
    }

    public String getFromTxnProfile() {
        return _fromTxnProfile;
    }

    public void setFromTxnProfile(String fromTxnProfile) {
        _fromTxnProfile = fromTxnProfile;
    }

    public String getFromTxnProfileDesc() {
        return _fromTxnProfileDesc;
    }

    public void setFromTxnProfileDesc(String fromTxnProfileDesc) {
        _fromTxnProfileDesc = fromTxnProfileDesc;
    }

    public String getToCategoryDesc() {
        return _toCategoryDesc;
    }

    public void setToCategoryDesc(String toCategoryDesc) {
        _toCategoryDesc = toCategoryDesc;
    }

    public String getToCommissionProfileID() {
        return _toCommissionProfileID;
    }

    public void setToCommissionProfileID(String toCommissionProfileID) {
        _toCommissionProfileID = toCommissionProfileID;
    }

    public String getToCommissionProfileIDDesc() {
        return _toCommissionProfileIDDesc;
    }

    public void setToCommissionProfileIDDesc(String toCommissionProfileIDDesc) {
        _toCommissionProfileIDDesc = toCommissionProfileIDDesc;
    }

    public String getToGradeCode() {
        return _toGradeCode;
    }

    public void setToGradeCode(String toGradeCode) {
        _toGradeCode = toGradeCode;
    }

    public String getToGradeCodeDesc() {
        return _toGradeCodeDesc;
    }

    public void setToGradeCodeDesc(String toGradeCodeDesc) {
        _toGradeCodeDesc = toGradeCodeDesc;
    }

    public String getToMSISDN() {
        return _toMSISDN;
    }

    public void setToMSISDN(String toMSISDN) {
        _toMSISDN = toMSISDN;
    }

    public String getToTxnProfile() {
        return _toTxnProfile;
    }

    public void setToTxnProfile(String toTxnProfile) {
        _toTxnProfile = toTxnProfile;
    }

    public String getToTxnProfileDesc() {
        return _toTxnProfileDesc;
    }

    public void setToTxnProfileDesc(String toTxnProfileDesc) {
        _toTxnProfileDesc = toTxnProfileDesc;
    }

    public String getFromMSISDN() {
        return _fromMSISDN;
    }

    public void setFromMSISDN(String fromMSISDN) {
        _fromMSISDN = fromMSISDN;
    }

    public String getNetPayableAmount() {
        return _netPayableAmount;
    }

    public void setNetPayableAmount(String netPayableAmount) {
        _netPayableAmount = netPayableAmount;
    }

    public String getPayableAmount() {
        return _payableAmount;
    }

    public void setPayableAmount(String payableAmount) {
        _payableAmount = payableAmount;
    }

    public String getRequestedQuantity() {
        return _requestedQuantity;
    }

    public void setRequestedQuantity(String requestedQuantity) {
        _requestedQuantity = requestedQuantity;
    }

    public String getTotalTax1() {
        return _totalTax1;
    }

    public void setTotalTax1(String totalTax1) {
        _totalTax1 = totalTax1;
    }

    public String getTotalTax2() {
        return _totalTax2;
    }

    public void setTotalTax2(String totalTax2) {
        _totalTax2 = totalTax2;
    }

    public String getTotalTax3() {
        return _totalTax3;
    }

    public void setTotalTax3(String totalTax3) {
        _totalTax3 = totalTax3;
    }

    public String getTransferMRP() {
        return _transferMRP;
    }

    public void setTransferMRP(String transferMRP) {
        _transferMRP = transferMRP;
    }

    public boolean getOutsideHierarchyFlag() {
        return _outsideHierarchyFlag;
    }

    public void setOutsideHierarchyFlag(boolean isOutsideHierarchy) {
        _outsideHierarchyFlag = isOutsideHierarchy;
    }

    public String getRemarks() {
        return _remarks;
    }

    public void setRemarks(String remarks) {
        _remarks = remarks;
    }

    public String getToCommissionProfileVersion() {
        return _toCommissionProfileVersion;
    }

    public void setToCommissionProfileVersion(String toCommissionProfileVersion) {
        _toCommissionProfileVersion = toCommissionProfileVersion;
    }

    public String getToGeoDomain() {
        return _toGeoDomain;
    }

    public void setToGeoDomain(String toGeoDomain) {
        _toGeoDomain = toGeoDomain;
    }

    public boolean getReturnFlag() {
        return _isReturnFlag;
    }

    public void setReturnFlag(boolean isReturn) {
        _isReturnFlag = isReturn;
    }

    public String getFromCommissionProfileVersion() {
        return _fromCommissionProfileVersion;
    }

    public void setFromCommissionProfileVersion(String fromCommissionProfileVersion) {
        _fromCommissionProfileVersion = fromCommissionProfileVersion;
    }

    public String getFromGeoDomain() {
        return _fromGeoDomain;
    }

    public void setFromGeoDomain(String fromGeoDomain) {
        _fromGeoDomain = fromGeoDomain;
    }

    public String getTransferCategory() {
        return _transferCategory;
    }

    public void setTransferCategory(String transferCategory) {
        _transferCategory = transferCategory;
    }

    public boolean getOperationPerformed() {
        return _isOperationPerformed;
    }

    public void setOperationPerformed(boolean isOperationPerformed) {
        _isOperationPerformed = isOperationPerformed;
    }

    public boolean getIsReturnToParentFlag() {
        return _isReturnToParentFlag;
    }

    public void setIsReturnToParentFlag(boolean isReturnToParentFlag) {
        _isReturnToParentFlag = isReturnToParentFlag;
    }

    /**
     * Method flust()
     * This method is to flush all the contents from the FormBean
     * void
     */
    public void flush() {
        _fromUserName = null;
        _fromUserID = null;
        _domainCode = null;
        _domainDesc = null;
        _fromCategoryCode = null;
        _fromCategoryDesc = null;
        _userID = null;
        _listSize = 0;
        _userName = null;
        _toUserName = null;
        _toUserID = null;
        _toCategoryCode = null;
        _fromMSISDN = null;
        _fromGradeCode = null;
        _fromGradeCodeDesc = null;
        _fromCommissionProfileID = null;
        _fromCommissionProfileIDDesc = null;
        _fromTxnProfile = null;
        _fromTxnProfileDesc = null;
        _toMSISDN = null;
        _toGradeCode = null;
        _toGradeCodeDesc = null;
        _toCommissionProfileID = null;
        _toCommissionProfileIDDesc = null;
        _toTxnProfile = null;
        _toTxnProfileDesc = null;
        _currentDate = null;
        _toCategoryDesc = null;

        _requestedQuantity = null;
        _transferMRP = null;
        _payableAmount = null;
        _netPayableAmount = null;
        _totalTax1 = null;
        _totalTax2 = null;
        _totalTax3 = null;
        _outsideHierarchyFlag = false;
        _remarks = null;
        _toCommissionProfileVersion = null;
        _toGeoDomain = null;
        _fromCommissionProfileVersion = null;
        _fromGeoDomain = null;

        _isReturnFlag = false;

        _userList = null;
        _categoryList = null;
        _productList = null;
        _productListWithTaxes = null;
        _channelTransferRuleVO = null;
        _userCode = null;
        _transferCategory = null;
        _isReturnToParentFlag = false;
        _type = null;
        _typeList = null;
        _transferNum = null;
        _userLoginID = null;
        _channelDomainList = null;
        _categoryCode = null;
        _typeDesc = null;
        _selectedIndex = null;
        _transferList = null;
        _transferItemsList = null;
        _channelTransferVO = null;
        _msisdn = null;
        _c2STransferVO = null;
        // For Mali --- +ve Commision Apply
        _senderDebitQty = null;
        _receiverCreditQty = null;
        _netCommQty = null;
        _revTransferNum = null;
        _smsPin = null;
        _displayMsisdn = null;
        _displayPin = null;

    }

    /**
     * method flushProductDetail()
     * This method is to flush the products information
     * void
     */
    public void flushProductDetail() {
        _productList = null;
        _productListWithTaxes = null;
        _remarks = null;
    }

    /**
     * @return Returns the time.
     */
    public long getTime() {
        return _time;
    }

    /**
     * @param time
     *            The time to set.
     */
    public void setTime(long time) {
        _time = time;
    }

    /**
     * @return Returns the fromPrimaryMSISDN.
     */
    public String getFromPrimaryMSISDN() {
        return _fromPrimaryMSISDN;
    }

    /**
     * @param fromPrimaryMSISDN
     *            The fromPrimaryMSISDN to set.
     */
    public void setFromPrimaryMSISDN(String fromPrimaryMSISDN) {
        _fromPrimaryMSISDN = fromPrimaryMSISDN;
    }

    /**
     * @return Returns the netCommQty.
     */
    public String getNetCommQty() {
        return _netCommQty;
    }

    /**
     * @return Returns the receiverCreditQty.
     */
    public String getReceiverCreditQty() {
        return _receiverCreditQty;
    }

    /**
     * @return Returns the senderDebitQty.
     */
    public String getSenderDebitQty() {
        return _senderDebitQty;
    }

    /**
     * @param netCommQty
     *            The netCommQty to set.
     */
    public void setNetCommQty(String netCommQty) {
        _netCommQty = netCommQty;
    }

    /**
     * @param receiverCreditQty
     *            The receiverCreditQty to set.
     */
    public void setReceiverCreditQty(String receiverCreditQty) {
        _receiverCreditQty = receiverCreditQty;
    }

    /**
     * @param senderDebitQty
     *            The senderDebitQty to set.
     */
    public void setSenderDebitQty(String senderDebitQty) {
        _senderDebitQty = senderDebitQty;
    }

    /**
     * @return Returns the transferNum.
     */
    public String getTransferNum() {
        return _transferNum;
    }

    /**
     * @param transferNum
     *            The transferNum to set.
     */
    public void setTransferNum(String transferNum) {
        _transferNum = transferNum;
    }

    /**
     * @return Returns the toUserLoginID.
     */
    public String getUserLoginID() {
        return _userLoginID;
    }

    /**
     * @param toUserLoginID
     *            The toUserLoginID to set.
     */
    public void setUserLoginID(String toUserLoginID) {
        _userLoginID = toUserLoginID;
    }

    /**
     * @return Returns the channelDomainList.
     */
    public ArrayList getChannelDomainList() {
        return _channelDomainList;
    }

    /**
     * @param channelDomainList
     *            The channelDomainList to set.
     */
    public void setChannelDomainList(ArrayList channelDomainList) {
        _channelDomainList = channelDomainList;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return _type;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setType(String type) {
        _type = type;
    }

    /**
     * @return Returns the categoryCode.
     */
    public String getCategoryCode() {
        return _categoryCode;
    }

    /**
     * @param categoryCode
     *            The categoryCode to set.
     */
    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }

    /**
     * @return Returns the typeDesc.
     */
    public String getTypeDesc() {
        return _typeDesc;
    }

    /**
     * @param typeDesc
     *            The typeDesc to set.
     */
    public void setTypeDesc(String typeDesc) {
        _typeDesc = typeDesc;
    }

    /**
     * @return Returns the typeList.
     */
    public ArrayList getTypeList() {
        return _typeList;
    }

    /**
     * @param typeList
     *            The typeList to set.
     */
    public void setTypeList(ArrayList typeList) {
        _typeList = typeList;
    }

    /**
     * @return Returns the selectedIndex.
     */
    public String getSelectedIndex() {
        return _selectedIndex;
    }

    /**
     * @param selectedIndex
     *            The selectedIndex to set.
     */
    public void setSelectedIndex(String selectedIndex) {
        _selectedIndex = selectedIndex;
    }

    /**
     * @return Returns the transferList.
     */
    public ArrayList getTransferList() {
        return _transferList;
    }

    /**
     * @param transferList
     *            The transferList to set.
     */
    public void setTransferList(ArrayList transferList) {
        _transferList = transferList;
    }

    /**
     * @return Returns the transferItemsList.
     */
    public ArrayList getTransferItemsList() {
        return _transferItemsList;
    }

    /**
     * @param transferItemsList
     *            The transferItemsList to set.
     */
    public void setTransferItemsList(ArrayList transferItemsList) {
        this._transferItemsList = transferItemsList;
    }

    /**
     * @return Returns the channelTransferVOList.
     */
    public ChannelTransferVO getChannelTransferVO() {
        return _channelTransferVO;
    }

    /**
     * @param channelTransferVOList
     *            The channelTransferVOList to set.
     */
    public void setChannelTransferVO(ChannelTransferVO channelTransferVOList) {
        _channelTransferVO = channelTransferVOList;
    }

    /**
     * @return Returns the msisdn.
     */
    public String getMsisdn() {
        return _msisdn;
    }

    /**
     * @param msisdn
     *            The msisdn to set.
     */
    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    /**
     * @return Returns the oldTransferVoList.
     */
    public ArrayList getOldTransferVoList() {
        return _oldTransferVoList;
    }

    /**
     * @param oldTransferVoList
     *            The oldTransferVoList to set.
     */
    public void setOldTransferVoList(ArrayList oldTransferVoList) {
        _oldTransferVoList = oldTransferVoList;
    }

    /**
     * @return Returns the reverseTransferList.
     */
    public ArrayList getReverseTransferList() {
        return _reverseTransferList;
    }

    /**
     * @param reverseTransferList
     *            The reverseTransferList to set.
     */
    public void setReverseTransferList(ArrayList reverseTransferList) {
        _reverseTransferList = reverseTransferList;
    }

    /**
     * @return Returns the c2STransferVO.
     */
    public C2STransferVO getC2STransferVO() {
        return _c2STransferVO;
    }

    /**
     * @param transferVO
     *            The c2STransferVO to set.
     */
    public void setC2STransferVO(C2STransferVO transferVO) {
        _c2STransferVO = transferVO;
    }

    public String getRevTransferNum() {
        return _revTransferNum;
    }

    public void setRevTransferNum(String revTransferNum) {
        this._revTransferNum = revTransferNum;
    }

    public String getSmsPin() {
        return _smsPin;
    }

    public void setSmsPin(String pin) {
        _smsPin = pin;
    }

    public String getDisplayMsisdn() {
        return _displayMsisdn;
    }

    public void setDisplayMsisdn(String msisdn) {
        _displayMsisdn = msisdn;
    }

    public String getDisplayPin() {
        return _displayPin;
    }

    public void setDisplayPin(String pin) {
        _displayPin = pin;
    }

    public String getJ_captcha_response() {
        return j_captcha_response;
    }

    public void setJ_captcha_response(String j_captcha_response) {
        this.j_captcha_response = j_captcha_response;
    }

    public String getToChannelUserStatus() {
        return _toChannelUserStatus;
    }

    public void setToChannelUserStatus(String channelUserStatus) {
        _toChannelUserStatus = channelUserStatus;
    }

    public String getFromChannelUserStatus() {
        return _fromChannelUserStatus;
    }

    public void setFromChannelUserrStatus(String channelUserrStatus) {
        _fromChannelUserStatus = channelUserrStatus;
    }
	public String getTotalOtherComm() {
		return _totalOtherComm;
	}
	
	public void setTotalOtherComm(String totalOtherComm) {
		_totalOtherComm = totalOtherComm;
	}
	
    public ArrayList getFromUserLoanVOList() {
 		return fromUserLoanVOList;
 	}
 	public void setFromUserLoanVOList(ArrayList fromUserLoanVOList) {
 		this.fromUserLoanVOList = fromUserLoanVOList;
 	}
}
