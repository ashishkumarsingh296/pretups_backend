/**
 * @(#)ChannelTransferApprovalForm.java
 *                                      Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                      All Rights Reserved
 * 
 *                                      <description>
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Author Date History
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      avinash.kamthan Aug 10, 2005 Initital
 *                                      Creation
 *                                      Sandeep goel Nov 10, 2005
 *                                      Modification,customization
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 * 
 */

package com.web.pretups.channel.transfer.web;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
/*

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorActionForm;
*/

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserOTFCountsVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscategory.businesslogic.VomsPackageVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;

/**
 * @author avinash.kamthan
 * 
 */
public class ChannelTransferApprovalForm /*extends ValidatorActionForm*/ {
    private final Log _log = LogFactory.getLog(ChannelTransferApprovalForm.class.getName());
    private String _networkCode = null;
    private String _networkName = null;
    private ArrayList _domainList = null;
    private int _listSize;
    private String _domainCode = null;
    private String _transferNum = null;
    private String _distributorName = null;
    private String _domainName = null;
    private String _domainNameForUserCode = null;
    private String _geographicDomainName = null;
    private String _geographicDomainCode = null;
    private ArrayList _geographicDomainList = null;
    private String _userName = null;
    private String _userNameTmp = null;
    private String _primaryTxnNum = null;
    private String _categoryName = null;
    private String _categoryCode = null;
    private String _categoryCodeForUserCode = null;
    private String _gardeDesc = null;
    private String _erpCode = null;
    private String _productType = null;
    private String _commissionProfileName = null;
    private String _transferDate = null;
    private String _refrenceNum = null;
    private String _address = null;
    private String _remarks = null;
    private String _paymentInstrumentName = null;
    private String _paymentInstrumentCode = null;
    private String _paymentInstNum = null;
    private String _paymentInstrumentDate = null;
    private String _paymentInstrumentAmt = null;
    private String _approve1Remark = null;
    private String _approve2Remark = null;
    private String _approve3Remark = null;
    private String _currentApprovalLevel = null;

    private String _selectedUserId = null;
    private String _payableAmount = null;
    private String _netPayableAmount = null;
    private boolean _fromUserCodeFlag;

    private ArrayList _channelTransferList = null;
    private ArrayList _transferItemList = null;
    private String _selectedTransfer = null;
    private String _firstApprovalLimit = null;
    private String _secondApprovalLimit = null;

    private String _totalMRP = null;
    private String _totalTax1 = null;
    private String _totalTax2 = null;
    private String _totalTax3 = null;
    private String _totalComm = null;
    private String _totalReqQty = null;
    private String _totalStock = null;

    private String _externalTxnNum = null;
    private String _externalTxnDate = null;
    private String _externalTxnExist = null;
    private String _externalTxnMandatory = null;
    private String _transferProfileName = null;
    private ArrayList _categoryList = null;
    private String _userCode = null;
    private int _approvalLevel = 0;
    private String _channelOwnerCategory = null;
    private String _channelOwnerCategoryDesc = null;
    private String _channelOwnerCategoryUserID = null;
    private String _channelOwnerCategoryUserName = null;
    private boolean _ownerSame = false;
    private ArrayList _userList = null;
    private String _userID = null;
    private String _popUpUserID = null;
    private String _allOrder = null;
    // to validate payment instruments
    private boolean _validatePaymentInstruments = false;
    private boolean _approvalDone = false;

    // to load the all user's orders
    private String _allUser = null;

    // to reject order confirmation
    private String _rejectOrder = null;

    private String _domainTypeCode = null; // for the domain type of the user
    // for EXTTXN number mandatory

    private String _geoDomainNameForUser = null;
    private String _geoDomainCodeForUser = null;
    private String _sessionDomainCode = null;
    private long _time = 0;
    private boolean _isPrimaryNumber = true;
    private String _toPrimaryMSISDN;
    // For mali changes - +ve commission appy
    private String _receiverCreditQuantity = null;
    private String _senderDebitQuantity = null;
    private String _commissionQuantity = null;

    // for transfer quantity change while approval
    private String _firstLevelApprovedQuantity = null;
    private String _secondLevelApprovedQuantity = null;
    private String _thirdLevelApprovedQuantity = null;
    private String totalInitialRequestedQuantity=null;
    
    private long _transferMultipleOff;

    private ArrayList _paymentInstrumentList = null;

    private boolean _showPaymentDetails = false;
    private boolean _showPaymentInstrumentType = false;

    // user life cycle
    private String _channelUserStatus = null;

    private String _totalCommValue = null;
    private String otfType;
    private Double otfRate;
    private Long otfValue;
    private String totalOtfValue;
    private boolean otfCountsUpdated = false;
	private String _totalOthComm;
	private String dualCommissionType;
    private UserOTFCountsVO userOTFCountsVO;
    private String netPayableAmountApproval;
    private String payableAmountApproval;
    private ArrayList<ListValueVO> _errorList = null;
    private ArrayList _slabsList = null;
    private ArrayList<VomsCategoryVO> _voucherTypeList;
    private String _voucherType = null;
    private String _voucherTypeDesc = null;
    private ArrayList _vomsProductList = null;
    private ArrayList _vomsCategoryList = null;
    private ArrayList<String> _mrpList;
    private String _vomsActiveMrp = null;
    private String _paymentInstDesc;
    private String _totalPayableAmount;
    private String _totalNetPayableAmount;
    private String _totalTransferedAmount;
    private ChannelTransferVO _channelTransferVO;
    private String reportHeaderName;
    private boolean closeTransaction = false;
    private String segment = null;
    private String segmentDesc = null;
    //package distributor mode
	private List _distributorModeList;
	private String distributorModeDesc;
	private String distributorMode;
    private String packageDetails;
	private String packageDetailsDesc;
	private List _packageDetailsList;
	private String _quantity;
	private String _retPrice;
	private double packageTotal;
	private int slabsListSize;
    private String distributorModeValue; //to enable/disable distributor Mode option
    
    
    public boolean isCloseTransaction() {
		return closeTransaction;
	}

	public void setCloseTransaction(boolean closeTransaction) {
		this.closeTransaction = closeTransaction;
	}
    public String getReportHeaderName() {
		return reportHeaderName;
	}

	public void setReportHeaderName(String reportHeaderName) {
		this.reportHeaderName = reportHeaderName;
	}

	public boolean getReconcilationFlag() {
		return reconcilationFlag;
	}

	public void setReconcilationFlag(boolean reconcilationFlag) {
		this.reconcilationFlag = reconcilationFlag;
	}
	private boolean reconcilationFlag = false;
	
	
    public ArrayList getBatchList() {
		return batchList;
	}

	public void setBatchList(ArrayList batchList) {
		this.batchList = batchList;
	}
	private ArrayList batchList = null;
    
    public String getNetPayableAmountApproval() {
		return netPayableAmountApproval;
	}

	public void setNetPayableAmountApproval(String netPayableAmountApproval) {
		this.netPayableAmountApproval = netPayableAmountApproval;
	}

	public String getPayableAmountApproval() {
		return payableAmountApproval;
	}

	public void setPayableAmountApproval(String payableAmountApproval) {
		this.payableAmountApproval = payableAmountApproval;
	}

	
    public UserOTFCountsVO getUserOTFCountsVO() {
		return userOTFCountsVO;
	}

	public void setUserOTFCountsVO(UserOTFCountsVO userOTFCountsVO) {
		this.userOTFCountsVO = userOTFCountsVO;
	}

	public boolean isOtfCountsUpdated() {
		return otfCountsUpdated;
	}

	public void setOtfCountsUpdated(boolean otfCountsUpdated) {
		this.otfCountsUpdated = otfCountsUpdated;
	}

	public String getTotalOtfValue() {
		return totalOtfValue;
	}

	public void setTotalOtfValue(String totalOtfValue) {
		this.totalOtfValue = totalOtfValue;
	}

	public String getOtfType() {
		return otfType;
	}

	public void setOtfType(String otfType) {
		this.otfType = otfType;
	}

	public Double getOtfRate() {
		return otfRate;
	}

	public void setOtfRate(Double otfRate) {
		this.otfRate = otfRate;
	}

	public Long getOtfValue() {
		return otfValue;
	}

	public void setOtfValue(Long otfValue) {
		this.otfValue = otfValue;
	}

	
    
    public boolean getShowPaymentDetails() {
        return _showPaymentDetails;
    }

    public void setShowPaymentDetails(boolean paymentDetails) {
        _showPaymentDetails = paymentDetails;
    }

    public String getUserNameTmp() {
        return _userNameTmp;
    }

    public void setUserNameTmp(String userNameTmp) {
        _userNameTmp = userNameTmp;
    }

    public boolean getApprovalDone() {
        return _approvalDone;
    }

    public void setApprovalDone(boolean approvalDone) {
        _approvalDone = approvalDone;
    }

    public String getSelectedTransfer() {
        return _selectedTransfer;
    }

    public void setSelectedTransfer(String selectedTransfer) {
        _selectedTransfer = selectedTransfer;
    }

    public ArrayList getTransferItemList() {
        return _transferItemList;
    }

    public void setTransferItemList(ArrayList transferItemList) {
        _transferItemList = transferItemList;
    }

    public String getSelectedUserId() {
        return _selectedUserId;
    }

    public void setSelectedUserId(String selectedUserId) {
        _selectedUserId = selectedUserId;
    }

    public String getAddress() {
        return _address;
    }

    public void setAddress(String address) {
        _address = address;
    }

    public String getApprove1Remark() {
        return _approve1Remark;
    }

    public void setApprove1Remark(String approve1Remark) {
        _approve1Remark = approve1Remark;
    }

    public String getCategoryCode() {
        return _categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return _categoryName;
    }

    public void setCategoryName(String categoryName) {
        _categoryName = categoryName;
    }

    public String getCommissionProfileName() {
        return _commissionProfileName;
    }

    public void setCommissionProfileName(String commissionProfileName) {
        _commissionProfileName = commissionProfileName;
    }

    public String getDistributorName() {
        return _distributorName;
    }

    public void setDistributorName(String distributorName) {
        _distributorName = distributorName;
    }

    public String getDomainName() {
        return _domainName;
    }

    public void setDomainName(String domainName) {
        _domainName = domainName;
    }

    public String getErpCode() {
        return _erpCode;
    }

    public void setErpCode(String erpCode) {
        _erpCode = erpCode;
    }

    public String getExternalTxnDate() {
        return _externalTxnDate;
    }

    public void setExternalTxnDate(String externalTxnDate) {
        _externalTxnDate = externalTxnDate;
    }

    public String getExternalTxnNum() {
        return _externalTxnNum;
    }

    public void setExternalTxnNum(String externalTxnNum) {
        _externalTxnNum = externalTxnNum;
    }

    public String getGardeDesc() {
        return _gardeDesc;
    }

    public void setGardeDesc(String gardeDesc) {
        _gardeDesc = gardeDesc;
    }

    public String getGeographicDomainName() {
        return _geographicDomainName;
    }

    public void setGeographicDomainName(String geographicDomainName) {
        _geographicDomainName = geographicDomainName;
    }

    public String getPaymentInstNum() {
        return _paymentInstNum;
    }

    public void setPaymentInstNum(String paymentInstNum) {
        _paymentInstNum = paymentInstNum;
    }

    public String getPaymentInstrumentAmt() {
        return _paymentInstrumentAmt;
    }

    public void setPaymentInstrumentAmt(String paymentInstrumentAmt) {
        _paymentInstrumentAmt = paymentInstrumentAmt;
    }

    public String getPaymentInstrumentCode() {
        return _paymentInstrumentCode;
    }

    public void setPaymentInstrumentCode(String paymentInstrumentCode) {
        _paymentInstrumentCode = paymentInstrumentCode;
    }

    public String getPaymentInstrumentDate() {
        return _paymentInstrumentDate;
    }

    public void setPaymentInstrumentDate(String paymentInstrumentDate) {
        _paymentInstrumentDate = paymentInstrumentDate;
    }

    public String getPaymentInstrumentName() {
        return _paymentInstrumentName;
    }

    public void setPaymentInstrumentName(String paymentInstrumentName) {
        _paymentInstrumentName = paymentInstrumentName;
    }

    public String getPrimaryTxnNum() {
        return _primaryTxnNum;
    }

    public void setPrimaryTxnNum(String primaryTxnNum) {
        _primaryTxnNum = primaryTxnNum;
    }

    public String getProductType() {
        return _productType;
    }

    public void setProductType(String productType) {
        _productType = productType;
    }

    public String getRefrenceNum() {
        return _refrenceNum;
    }

    public void setRefrenceNum(String refrenceNum) {
        _refrenceNum = refrenceNum;
    }

    public String getRemarks() {
        return _remarks;
    }

    public void setRemarks(String remarks) {
        _remarks = remarks;
    }

    public String getTransferDate() {
        return _transferDate;
    }

    public void setTransferDate(String transferDate) {
        _transferDate = transferDate;
    }

    public String getTransferNum() {
        return _transferNum;
    }

    public void setTransferNum(String transferNum) {
        _transferNum = transferNum;
    }

    public String getUserName() {
        return _userName;
    }

    public void setUserName(String userName) {
        _userName = userName;
    }

    public ArrayList getChannelTransferList() {
        return _channelTransferList;
    }

    public void setChannelTransferList(ArrayList channelTransferList) {
        _channelTransferList = channelTransferList;
    }

    public String getExternalTxnExist() {
        return _externalTxnExist;
    }

    public void setExternalTxnExist(String externalTxnExist) {
        _externalTxnExist = externalTxnExist;
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

    public String getApprove2Remark() {
        return _approve2Remark;
    }

    public void setApprove2Remark(String approve2Remark) {
        _approve2Remark = approve2Remark;
    }

    public String getCurrentApprovalLevel() {
        return _currentApprovalLevel;
    }

    public void setCurrentApprovalLevel(String currentApprovalLevel) {
        _currentApprovalLevel = currentApprovalLevel;
    }

    public String getApprove3Remark() {
        return _approve3Remark;
    }

    public void setApprove3Remark(String approve3Remark) {
        _approve3Remark = approve3Remark;
    }

    public String getDomainCode() {
        return _domainCode;
    }

    public void setDomainCode(String domainCode) {
        _domainCode = domainCode;
    }

    public ArrayList getDomainList() {
        return _domainList;
    }

    public void setDomainList(ArrayList domainList) {
        _domainList = domainList;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public String getNetworkName() {
        return _networkName;
    }

    public void setNetworkName(String networkName) {
        _networkName = networkName;
    }

    public int getListSize() {
        return _listSize;
    }

    public void setListSize(int listSize) {
        _listSize = listSize;
    }

    public int getUserListSize() {
        if (_userList == null) {
            return 0;
        }
        return _userList.size();
    }
    
    public String getTotalCommValue() {
        return _totalCommValue;
    }
    
    public void setTotalCommValue(String commValue)
    {
    	_totalCommValue=commValue;
    }
    

    public String getCategoryCodeForUserCode() {
        return _categoryCodeForUserCode;
    }

    public void setCategoryCodeForUserCode(String categoryCodeForUserCode) {
        _categoryCodeForUserCode = categoryCodeForUserCode;
    }

    public String getDomainNameForUserCode() {
        return _domainNameForUserCode;
    }

    public void setDomainNameForUserCode(String domainNameForUserCode) {
        _domainNameForUserCode = domainNameForUserCode;
    }

    public boolean getFromUserCodeFlag() {
        return _fromUserCodeFlag;
    }

    public void setFromUserCodeFlag(boolean fromUserCodeFlag) {
        _fromUserCodeFlag = fromUserCodeFlag;
    }

    public String getFirstApprovalLimit() {
        return _firstApprovalLimit;
    }

    public void setFirstApprovalLimit(String firstApprovalLimit) {
        _firstApprovalLimit = firstApprovalLimit;
    }

    public String getSecondApprovalLimit() {
        return _secondApprovalLimit;
    }

    public void setSecondApprovalLimit(String secondApprovalLimit) {
        _secondApprovalLimit = secondApprovalLimit;
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

    public String getExternalTxnMandatory() {
        return _externalTxnMandatory;
    }

    public void setExternalTxnMandatory(String externalTxnMandatory) {
        _externalTxnMandatory = externalTxnMandatory;
    }

    public String getTransferProfileName() {
        return _transferProfileName;
    }

    public void setTransferProfileName(String transferProfileName) {
        _transferProfileName = transferProfileName;
    }

    public String getGeographicDomainCode() {
        return _geographicDomainCode;
    }

    public void setGeographicDomainCode(String geographicDomainCode) {
        _geographicDomainCode = geographicDomainCode;
    }

    public ArrayList getGeographicDomainList() {
        return _geographicDomainList;
    }

    public int getGeographicDomainListSize() {
        if (_geographicDomainList != null) {
            return _geographicDomainList.size();
        }
        return 0;
    }

    public void setGeographicDomainList(ArrayList geographicDomainList) {
        _geographicDomainList = geographicDomainList;
    }

    public ArrayList getCategoryList() {
        return _categoryList;
    }

    public void setCategoryList(ArrayList categoryList) {
        _categoryList = categoryList;
    }

    public String getUserCode() {
        return _userCode;
    }

    public void setUserCode(String userCode) {
        _userCode = userCode;
    }

    public int getApprovalLevel() {
        return _approvalLevel;
    }

    public void setApprovalLevel(int approvalLevel) {
        _approvalLevel = approvalLevel;
    }

    public String getChannelOwnerCategory() {
        return _channelOwnerCategory;
    }

    public void setChannelOwnerCategory(String channelOwnerCategory) {
        _channelOwnerCategory = channelOwnerCategory;
    }

    public String getChannelOwnerCategoryDesc() {
        return _channelOwnerCategoryDesc;
    }

    public void setChannelOwnerCategoryDesc(String channelOwnerCategoryDesc) {
        _channelOwnerCategoryDesc = channelOwnerCategoryDesc;
    }

    public boolean getOwnerSame() {
        return _ownerSame;
    }

    public void setOwnerSame(boolean ownerSame) {
        _ownerSame = ownerSame;
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

    public String getChannelOwnerCategoryUserID() {
        return _channelOwnerCategoryUserID;
    }

    public void setChannelOwnerCategoryUserID(String channelOwnerCategoryUserID) {
        _channelOwnerCategoryUserID = channelOwnerCategoryUserID;
    }

    public String getChannelOwnerCategoryUserName() {
        return _channelOwnerCategoryUserName;
    }

    public void setChannelOwnerCategoryUserName(String channelOwnerCategoryUserName) {
        _channelOwnerCategoryUserName = channelOwnerCategoryUserName;
    }

    public String getPopUpUserID() {
        return _popUpUserID;
    }

    public void setPopUpUserID(String popUpUserID) {
        _popUpUserID = popUpUserID;
    }

    public String getAllOrder() {
        return _allOrder;
    }

    public void setAllOrder(String allOrder) {
        _allOrder = allOrder;
    }

    public boolean getValidatePaymentInstruments() {
        return _validatePaymentInstruments;
    }

    public void setValidatePaymentInstruments(boolean validatePaymentInstruments) {
        _validatePaymentInstruments = validatePaymentInstruments;
    }

    public String getAllUser() {
        return _allUser;
    }

    public void setAllUser(String allUser) {
        _allUser = allUser;
    }

    public String getRejectOrder() {
        return _rejectOrder;
    }

    public void setRejectOrder(String rejectOrder) {
        _rejectOrder = rejectOrder;
    }

    public String getDomainTypeCode() {
        return _domainTypeCode;
    }

    public void setDomainTypeCode(String domainTypeCode) {
        _domainTypeCode = domainTypeCode;
    }

    public String getGeoDomainCodeForUser() {
        return _geoDomainCodeForUser;
    }

    public void setGeoDomainCodeForUser(String geoDomainCodeForUser) {
        _geoDomainCodeForUser = geoDomainCodeForUser;
    }

    public String getGeoDomainNameForUser() {
        return _geoDomainNameForUser;
    }

    public void setGeoDomainNameForUser(String geoDomainNameForUser) {
        _geoDomainNameForUser = geoDomainNameForUser;
    }

    public String getSessionDomainCode() {
        return _sessionDomainCode;
    }

    public void setSessionDomainCode(String sessionDomainCode) {
        _sessionDomainCode = sessionDomainCode;
    }

    /**
     * Method flush()
     * to flush all the information of the form bean.
     * void
     */
    public void flush() {
        _networkCode = null;
        _networkName = null;
        _domainList = null;
        _listSize = 0;
        _domainCode = null;
        _transferNum = null;
        _distributorName = null;
        _domainName = null;
        _domainNameForUserCode = null;
        _geographicDomainName = null;
        _userName = null;
        _userNameTmp = null;
        _primaryTxnNum = null;
        _categoryName = null;
        _categoryCode = null;
        _categoryCodeForUserCode = null;
        _gardeDesc = null;
        _erpCode = null;
        _productType = null;
        _commissionProfileName = null;
        _transferDate = null;
        _externalTxnExist = null;
        _externalTxnNum = null;
        _externalTxnDate = null;
        _externalTxnMandatory = null;
        _refrenceNum = null;
        _address = null;
        _remarks = null;
        _paymentInstrumentName = null;
        _paymentInstrumentCode = null;
        _paymentInstNum = null;
        _paymentInstrumentDate = null;
        _paymentInstrumentAmt = null;
        _approve1Remark = null;
        _approve2Remark = null;
        _approve3Remark = null;
        _currentApprovalLevel = null;
        _selectedUserId = null;
        _payableAmount = null;
        _netPayableAmount = null;
        _fromUserCodeFlag = false;
        _channelTransferList = null;
        _transferItemList = null;
        _selectedTransfer = null;
        _firstApprovalLimit = null;
        _secondApprovalLimit = null;
        _transferProfileName = null;
        _geographicDomainCode = null;
        _geographicDomainList = null;
        _categoryList = null;
        _userCode = null;
        _approvalLevel = 0;
        _channelOwnerCategory = null;
        _channelOwnerCategoryDesc = null;
        _ownerSame = false;
        _userList = null;
        _userID = null;
        _channelOwnerCategoryUserID = null;
        _channelOwnerCategoryUserName = null;
        _popUpUserID = null;
        _allOrder = null;
        _validatePaymentInstruments = false;
        _approvalDone = false;
        _allUser = null;
        _rejectOrder = null;
        _geoDomainNameForUser = null;
        _geoDomainCodeForUser = null;
        _sessionDomainCode = null;
        _firstLevelApprovedQuantity = null;
        _secondLevelApprovedQuantity = null;
        _thirdLevelApprovedQuantity = null;
        _paymentInstrumentList = null;
        _slabsList = null;
        _fromDate = null;
        _toDate = null;
    }

    /**
     * Method flushRecordContent()
     * to flush the information of the record.
     * void
     */
    void flushRecordContent() {
        _externalTxnExist = null;
        _externalTxnNum = null;
        _externalTxnDate = null;
        _externalTxnMandatory = null;
        _validatePaymentInstruments = false;
        _paymentInstrumentList = null;
        _approvalDone = false;
        _transferItemList = null;
    }

    void flushApprovalQuantities() {
        _firstLevelApprovedQuantity = null;
        _secondLevelApprovedQuantity = null;
        _thirdLevelApprovedQuantity = null;
    }
    
    public void flushMrp() {
        _mrpList = null;

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
     * @return Returns the isPrimaryNumber.
     */
    public boolean isPrimaryNumber() {
        return _isPrimaryNumber;
    }

    /**
     * @param isPrimaryNumber
     *            The isPrimaryNumber to set.
     */
    public void setPrimaryNumber(boolean isPrimaryNumber) {
        _isPrimaryNumber = isPrimaryNumber;
    }

    /**
     * @return Returns the toPrimaryMSISDN.
     */
    public String getToPrimaryMSISDN() {
        return _toPrimaryMSISDN;
    }

    /**
     * @param toPrimaryMSISDN
     *            The toPrimaryMSISDN to set.
     */
    public void setToPrimaryMSISDN(String toPrimaryMSISDN) {
        _toPrimaryMSISDN = toPrimaryMSISDN;
    }

    // o2c transfer quantity change
    // Indexing is to be used for modifying the requested quantity during
    // approval
    public ChannelTransferItemsVO getChannelTransferIndexed(int i) {
        return (ChannelTransferItemsVO) _transferItemList.get(i);

    }

    public void setChannelTransferIndexed(int i, ChannelTransferItemsVO channelTransferItemsVO) {
        _transferItemList.set(i, channelTransferItemsVO);
    }

    public String getFirstLevelApprovedQuantity() {
        return _firstLevelApprovedQuantity;
    }

    public void setFirstLevelApprovedQuantity(String firstLevelApprovedQuantity) {
        _firstLevelApprovedQuantity = firstLevelApprovedQuantity;
    }

    public String getSecondLevelApprovedQuantity() {
        return _secondLevelApprovedQuantity;
    }

    public void setSecondLevelApprovedQuantity(String secondLevelApprovedQuantity) {
        _secondLevelApprovedQuantity = secondLevelApprovedQuantity;
    }

    public String getThirdLevelApprovedQuantity() {
        return _thirdLevelApprovedQuantity;

    }

    public void setThirdLevelApprovedQuantity(String thirdLevelApprovedQuantity) {
        _thirdLevelApprovedQuantity = thirdLevelApprovedQuantity;

    }

    public long getTransferMultipleOff() {
        return _transferMultipleOff;

    }

    public void setTransferMultipleOff(long transferMultipleOff) {
        _transferMultipleOff = transferMultipleOff;

    }

    /**
     * @return Returns the commissionQuantity.
     */
    public String getCommissionQuantity() {
        return _commissionQuantity;
    }

    /**
     * @return Returns the receiverCreditAmount.
     */
    public String getReceiverCreditQuantity() {
        return _receiverCreditQuantity;
    }

    /**
     * @return Returns the senderCreditAmount.
     */
    public String getSenderDebitQuantity() {
        return _senderDebitQuantity;
    }

    /**
     * @param commissionQuantity
     *            The commissionQuantity to set.
     */
    public void setCommissionQuantity(String commissionQuantity) {
        _commissionQuantity = commissionQuantity;
    }

    /**
     * @param receiverCreditAmount
     *            The receiverCreditAmount to set.
     */
    public void setReceiverCreditQuantity(String receiverCreditQuantity) {
        _receiverCreditQuantity = receiverCreditQuantity;
    }

    /**
     * @param senderCreditAmount
     *            The senderCreditAmount to set.
     */
    public void setSenderDebitQuantity(String senderDebitQuantity) {
        _senderDebitQuantity = senderDebitQuantity;
    }

    public ArrayList getPaymentInstrumentList() {
        return _paymentInstrumentList;
    }

    public void setPaymentInstrumentList(ArrayList paymentInstrumentList) {
        _paymentInstrumentList = paymentInstrumentList;
    }

    public boolean getShowPaymentInstrumentType() {
        return _showPaymentInstrumentType;
    }

    public void setShowPaymentInstrumentType(boolean paymentInstrumentType) {
        _showPaymentInstrumentType = paymentInstrumentType;
    }

    public String getChannelUserStatus() {
        return _channelUserStatus;
    }

    public void setChannelUserStatus(String userStatus) {
        _channelUserStatus = userStatus;
    }
	public String getTotalOthComm()
	{
			return _totalOthComm;
	}
	public void setTotalOthComm(String totalOthComm)
	{
			_totalOthComm = totalOthComm;
	}

  public String getDualCommissionType() {
    return dualCommissionType;
  }

  public void setDualCommissionType(String dualCommissionType) {
    this.dualCommissionType = dualCommissionType;
  }
  public ArrayList<ListValueVO> getErrorList() {
      return _errorList;
  }
  public void setErrorList(ArrayList<ListValueVO> list) {
      _errorList = list;
  }	
  public ArrayList getSlabsList() {
      return _slabsList;
  }
  public void setSlabsList(ArrayList slabsList) {
      _slabsList = slabsList;
  }

  public VomsBatchVO getSlabsListIndexed(int i) {
      return (VomsBatchVO) _slabsList.get(i);
  }
  public void setSlabsListIndexed(int i, VomsBatchVO detailsVO) {
      _slabsList.set(i, detailsVO);
  }
  public ArrayList<VomsCategoryVO> getVoucherTypeList() {
      return _voucherTypeList;
  }

  public void setVoucherTypeList(ArrayList<VomsCategoryVO> typeList) {
      _voucherTypeList = typeList;
  }

  public int getVoucherTypeListSize() {
      if (_voucherTypeList != null) {
          return _voucherTypeList.size();
      } else {
          return 0;
      }
  }
  public String getVoucherType() {
      return _voucherType;
  }
  public void setVoucherType(String voucherType) {
      _voucherType = voucherType;
  }
  public String getVoucherTypeDesc() {
      return _voucherTypeDesc;
  }
  public void setVoucherTypeDesc(String voucherTypeDesc) {
      _voucherTypeDesc = voucherTypeDesc;
  }
  public ArrayList getVomsProductList() {
      return _vomsProductList;
  }
  public void setVomsProductList(ArrayList productList) {
      _vomsProductList = productList;
  }
  public ArrayList getVomsCategoryList() {
      return _vomsCategoryList;
  }
  public void setVomsCategoryList(ArrayList vomsCategoryList) {
      _vomsCategoryList = vomsCategoryList;
  }
  public ArrayList getMrpList() {
      return _mrpList;
  }
  public void setMrpList(ArrayList mrpList) {
      _mrpList = mrpList;
  }
  public String getVomsActiveMrp() {
      return _vomsActiveMrp;
  }
  public void setVomsActiveMrp(String mrp) {
      _vomsActiveMrp = mrp;
  }
  public String getPaymentInstDesc() {
      return _paymentInstDesc;
  }
  public void setPaymentInstDesc(String paymentInstDesc) {
      _paymentInstDesc = paymentInstDesc;
  }
  public String getTotalPayableAmount() {
      return _totalPayableAmount;
  }
  public void setTotalPayableAmount(String totalPayableAmount) {
      _totalPayableAmount = totalPayableAmount;
  }
  public String getTotalNetPayableAmount() {
      return _totalNetPayableAmount;
  }
  public void setTotalNetPayableAmount(String totalNetPayableAmount) {
      _totalNetPayableAmount = totalNetPayableAmount;
  }
  public String getTotalTransferedAmount() {
      return _totalTransferedAmount;
  }
  public void setTotalTransferedAmount(String totalTransferedAmount) {
      _totalTransferedAmount = totalTransferedAmount;
  }
  public ChannelTransferVO getChannelTransferVO() {
      return _channelTransferVO;
  }
  public void setChannelTransferVO(ChannelTransferVO channelTransferVO) {
	  _channelTransferVO = channelTransferVO;
  }
  private String _fromDate = null;
  private String _toDate = null;
  public String getFromDate() {
      return _fromDate;
  }
  public void setFromDate(String fromDate) {
      _fromDate = fromDate;
  }
  public String getToDate() {
      return _toDate;
  }
  public void setToDate(String toDate) {
      _toDate = toDate;
  }
	private String actionType = "o2c";
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	private String actionName = null;
	
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getTotalInitialRequestedQuantity() {
		return totalInitialRequestedQuantity;
	}

	public void setTotalInitialRequestedQuantity(String totalInitialRequestedQuantity) {
		this.totalInitialRequestedQuantity = totalInitialRequestedQuantity;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public String getSegmentDesc() {
		return segmentDesc;
	}

	public void setSegmentDesc(String segmentDesc) {
		this.segmentDesc = segmentDesc;
	}
	 public List getDistributorModeList() {
			return _distributorModeList;
		}

		public String getDistributorModeDesc() {
			return distributorModeDesc;
		}

		public void setDistributorModeDesc(String distributorModeDesc) {
			this.distributorModeDesc = distributorModeDesc;
		}

		public void setDistributorModeList(ArrayList<String> _distributorModeList) {
			this._distributorModeList = _distributorModeList;
		}
		
		
		public int getDistributorModeListSize() {
			 if (_distributorModeList != null) {
		            return _distributorModeList.size();
		        }
		        return 0;
		}
		
		public String getDistributorMode() {
			return distributorMode;
		}
		
		public void setDistributorMode(String distributorMode) {
			this.distributorMode = distributorMode;
		}

		//packages for bundles
	public String getPackageDetails() {
		return packageDetails;
	}

	public void setPackageDetails(String packageDetails) {
		this.packageDetails = packageDetails;
	}

	public String getPackageDetailsDesc() {
		return packageDetailsDesc;
	}

	public void setPackageDetailsDesc(String packageDetailsDesc) {
		this.packageDetailsDesc = packageDetailsDesc;
	}

	public List getPackageDetailsList() {
		return _packageDetailsList;
	}

	public void setPackageDetailsList(List _packageDetailsList) {
		this._packageDetailsList = _packageDetailsList;
	}
	
	public int getPackageDetailsListSize() {
		 if (_packageDetailsList != null) {
	            return _packageDetailsList.size();
	        }
	        return 0;
	}
	
	public String getQuantity() {
		return _quantity;
	}

	public void setQuantity(String _quantity) {
		this._quantity = _quantity;
	}

	public String getRetPrice() {
		return _retPrice;
	}

	public void setRetPrice(String _retPrice) {
		this._retPrice = _retPrice;
	}
	
	public VomsPackageVO getPckgSlabsListIndexed(int i) {
        return (VomsPackageVO) _slabsList.get(i);
    }

    public void setPckgSlabsListIndexed(int i, VomsPackageVO detailsVO) {
        _slabsList.set(i, detailsVO);
    }
    
	public double getPackageTotal() {
		return packageTotal;
	}

	public void setPackageTotal(double packageTotal) {
		this.packageTotal = packageTotal;
	}
	  
    public int getSlabsListSize() {
		return slabsListSize;
	}

	public void setSlabsListSize(int slabsListSize) {
		this.slabsListSize = slabsListSize;
	}
	
	public String getDistributorModeValue() {
		return distributorModeValue;
	}

	public void setDistributorModeValue(String distributorModeValue) {
		this.distributorModeValue = distributorModeValue;
	}
}
