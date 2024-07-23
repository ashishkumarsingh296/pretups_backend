/**
 * @(#)ChannelTransferEnquiryForm.java
 *                                     Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                     All Rights Reserved
 * 
 *                                     <description>
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 *                                     Author Date History
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 *                                     avinash.kamthan Aug 19, 2005 Initital
 *                                     Creation
 *                                     Sandeep Goel Nov 10,2005 Modification and
 *                                     customization
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 * 
 */

package com.web.pretups.channel.transfer.web;

import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;

import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;

/**
 * @author avinash.kamthan
 * 
 */
public class ChannelTransferEnquiryForm {

    private ArrayList _geoDomainList;
    private ArrayList _channelDomainList;
    private ArrayList _productsTypeList;
    private ArrayList _categoryList;

    private String _geoDomainCode;
    private String _channelDomain;
    private String _productType;
    private String _categoryCode;
    private String _geoDomainCodeDesc;
    private String _channelDomainDesc;
    private String _productTypeDesc;
    private String _categoryCodeDesc;
    private String _transferNum; // user inpu field
    private String _networkCode;
    private String _networkName;
    private String _reportHeaderName;

    // popup
    private ArrayList _userList;
    private String _userName;
    private int _listSize;
    private String _userID;

    // screen 2
    private boolean _ownerSame;
    private String _channelOwnerCategory;
    private String _channelOwnerCategoryDesc;
    private String _channelOwnerCategoryUserName;
    private String _channelCategoryUserName;
    private String _channelOwnerCategoryUserID;
    private String _channelCategoryUserID;

    private String _fromDate;
    private String _toDate;
    private String _statusCode;
    private String _statusDesc;
    private ArrayList _statusList;

    // enquiry screen
    private String selectedIndex;
    // detail screen
    private ArrayList _transferList;

    // view detail screen for enquiry
    private ArrayList _transferItemsList;

    private String _domainCode;
    private String _transferNumberDispaly;
    private String _distributorName;
    private String _domainName;
    private String _geographicDomainName;
    private String _primaryTxnNum;
    private String _categoryName;
    private String _gardeDesc;
    private String _erpCode;
    private String _commissionProfileName;
    private String _transferDate;
    private String _externalTxnExist;
    private String _externalTxnNum;
    private String _externalTxnDate;
    private String _refrenceNum;
    private String _address;
    private String _remarks;
    private String _paymentInstrumentName;
    private String _paymentInstrumentCode;
    private String _paymentInstNum;
    private String _paymentInstrumentDate;
    private String _paymentInstrumentAmt;
    private String _approve1Remark;
    private String _approve2Remark;
    private String _approve3Remark;
    private String _currentApprovalLevel;

    private String _selectedUserId;
    private String _payableAmount;
    private String _netPayableAmount;
    private boolean _channelUserLoginedFlag;
    private int _searchListSize;

    private String _totalMRP;
    private String _totalTax1;
    private String _totalTax2;
    private String _totalTax3;
    private String _totalComm;
    private String _totalReqQty;
    private String _totalStock;
    private ArrayList _transferTypeList;
    private String _transferTypeCode;
    private String _transferTypeValue;

    // to store the information of transfer Category (SALE/TRANSFER).
    private ArrayList _transferCategoryList = null;
    private String _transferCategoryCode = null;
    private String _transferCategoryDesc = null;
    private String _transferProfileName;

    private String _geoDomainNameForUser = null;

    // parameters for the enquiry by the user mobile number (user code)
    private String _userCode = null;
    private String _fromDateForUserCode = null;
    private String _toDateForUserCode = null;
    private String _statusCodeForUserCode = null;
    private String _trfCatForUserCode = null;

    private String _trfTypeForUserCode = null;
    private String _statusDetail = null;
    private String _sessionDomainCode = null;
    // ends here

    private String _trfTypeDetail = null;
    private long _time = 0;
    private String _currentDateFlag = null;
    private String _currentDateFlagForUserCode = null;
    // For Mali CR--- +ve Commision Apply
    private String _commissionQuantity = null;
    private String _receiverCreditQuantity = null;
    private String _senderDebitQuantity = null;

    // for transfer quantity change while approval
    private String _firstLevelApprovedQuantity = null;
    private String _secondLevelApprovedQuantity = null;
    private String _thirdLevelApprovedQuantity = null;
    private String _validationCheck = null;
    // Added By Babu Kunwar For showing post/pre balance in C2C Transfers
    private String _senderPostStock = null;
    private String _receiverPostStock = null;
    private String _senderPreviousStock = null;
    private String _receiverPreviousStock = null;

    // Added by Amit Raheja for txn reversal
    private String _revTransferNum;

    private boolean _showPaymentDetails = false;
    
    private String transactionMode = "N";
    private String sosSettlementDate;
    private String sosStatus;
    private String totalOtf;
	private String _totalOthComm=null;
    
	private String dualCommissionType;
	 // added For Voucher Details
	 private String batch_no;
	 private String product_name;
	 private String batch_type;
	 private String from_serial_no;
	 private String to_serial_no;
	 private String total_no_of_vouchers;
	 private String transferSubType;
	 private String distributorType;
	 private String distributorTypeDesc;
	 private  ArrayList _distributorTypeList;
	 private  ArrayList _packageTransferList;
	 private String encKey;
	
	 public String getDistributorType() {
			return distributorType;
		}

		public void setDistributorType(String distributorType) {
			this.distributorType = distributorType;
		}
		
		public String getDistributorTypeDesc() {
			return distributorTypeDesc;
		}

		public void setDistributorTypeDesc(String distributorTypeDesc) {
			this.distributorTypeDesc = distributorTypeDesc;
		}
		 public ArrayList getDistributorTypeList() {
				return _distributorTypeList;
			}
		 public void setDistributorTypeList(ArrayList<String> _distributorTypeList) {
				this._distributorTypeList = _distributorTypeList;
			} 
		 public int getDistributorTypeListSize() {
			 if (_distributorTypeList != null) {
		            return _distributorTypeList.size();
		        }
		        return 0;
		}
	 public String getBatch_no() {
			return batch_no;
		}

		public void setBatch_no(String batch_no) {
			this.batch_no = batch_no;
		}

		public String getProduct_name() {
			return product_name;
		}

		public void setProduct_name(String product_name) {
			this.product_name = product_name;
		}	
	 public String getBatch_type() {
		return batch_type;
	}

	public void setBatch_type(String batch_type) {
		this.batch_type = batch_type;
	}

	public String getTransferSubType() {
		return transferSubType;
	}

	public void setTransferSubType(String transferSubType) {
		this.transferSubType = transferSubType;
	}

	public String getFrom_serial_no() {
		return from_serial_no;
	}

	public void setFrom_serial_no(String from_serial_no) {
		this.from_serial_no = from_serial_no;
	}

	public String getTo_serial_no() {
		return to_serial_no;
	}

	public void setTo_serial_no(String to_serial_no) {
		this.to_serial_no = to_serial_no;
	}

	public String getTotal_no_of_vouchers() {
		return total_no_of_vouchers;
	}

	public void setTotal_no_of_vouchers(String total_no_of_vouchers) {
		this.total_no_of_vouchers = total_no_of_vouchers;
	}

	public String getDualCommissionType() {
		return dualCommissionType;
	}

	public void setDualCommissionType(String dualCommissionType) {
		this.dualCommissionType = dualCommissionType;
	}

	public String getTotalOtf() {
		return totalOtf;
	}

	public void setTotalOtf(String totalOtf) {
		this.totalOtf = totalOtf;
	}

	public String getSosSettlementDate() {
		return sosSettlementDate;
	}

	public void setSosSettlementDate(String sosSettlementDate) {
		this.sosSettlementDate = sosSettlementDate;
	}

	public String getSosStatus() {
		return sosStatus;
	}

	public void setSosStatus(String sosStatus) {
		this.sosStatus = sosStatus;
	}

	public String getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(String transactionMode) {
		this.transactionMode = transactionMode;
	}

	public boolean getShowPaymentDetails() {
        return _showPaymentDetails;
    }

    public void setShowPaymentDetails(boolean paymentDetails) {
        _showPaymentDetails = paymentDetails;
    }

    /**
     * @return the validationCheck
     */
    public String getValidationCheck() {
        return _validationCheck;
    }

    /**
     * @param validationCheck
     *            the validationCheck to set
     */
    public void setValidationCheck(String validationCheck) {
        _validationCheck = validationCheck;
    }

    public int getProductTypesListSize() {
        if (_productsTypeList != null) {
            return _productsTypeList.size();
        }
        return 0;
    }

    public boolean getChannelUserLoginedFlag() {
        return _channelUserLoginedFlag;
    }

    public void setChannelUserLoginedFlag(boolean channelUserLoginedFlag) {
        _channelUserLoginedFlag = channelUserLoginedFlag;
    }

    public String getFromDate() {
        return _fromDate;
    }

    public void setFromDate(String fromDate) {
        _fromDate = fromDate;
    }

    public String getStatusCode() {
        return _statusCode;
    }

    public void setStatusCode(String statusCode) {
        _statusCode = statusCode;
    }

    public ArrayList getStatusList() {
        return _statusList;
    }

    public void setStatusList(ArrayList statusList) {
        _statusList = statusList;
    }

    public String getToDate() {
        return _toDate;
    }

    public void setToDate(String toDate) {
        _toDate = toDate;
    }

    public String getCategoryCode() {
        return _categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }

    public String getCategoryCodeDesc() {
        return _categoryCodeDesc;
    }

    public void setCategoryCodeDesc(String categoryCodeDesc) {
        _categoryCodeDesc = categoryCodeDesc;
    }

    public ArrayList getCategoryList() {
        return _categoryList;
    }

    public void setCategoryList(ArrayList categoryList) {
        _categoryList = categoryList;
    }

    public String getChannelCategoryUserID() {
        return _channelCategoryUserID;
    }

    public void setChannelCategoryUserID(String channelCategoryUserID) {
        _channelCategoryUserID = channelCategoryUserID;
    }

    public String getChannelCategoryUserName() {
        return _channelCategoryUserName;
    }

    public void setChannelCategoryUserName(String channelCategoryUserName) {
        _channelCategoryUserName = channelCategoryUserName;
    }

    public String getChannelDomain() {
        return _channelDomain;
    }

    public void setChannelDomain(String channelDomain) {
        _channelDomain = channelDomain;
    }

    public String getChannelDomainDesc() {
        return _channelDomainDesc;
    }

    public void setChannelDomainDesc(String channelDomainDesc) {
        _channelDomainDesc = channelDomainDesc;
    }

    public ArrayList getChannelDomainList() {
        return _channelDomainList;
    }

    public void setChannelDomainList(ArrayList channelDomainList) {
        _channelDomainList = channelDomainList;
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

    public String getGeoDomainCode() {
        return _geoDomainCode;
    }

    public void setGeoDomainCode(String geoDomainCode) {
        _geoDomainCode = geoDomainCode;
    }

    public String getGeoDomainCodeDesc() {
        return _geoDomainCodeDesc;
    }

    public void setGeoDomainCodeDesc(String geoDomainCodeDesc) {
        _geoDomainCodeDesc = geoDomainCodeDesc;
    }

    public ArrayList getGeoDomainList() {
        return _geoDomainList;
    }

    public void setGeoDomainList(ArrayList geoDomainList) {
        _geoDomainList = geoDomainList;
    }

    public boolean isOwnerSame() {
        return _ownerSame;
    }

    public void setOwnerSame(boolean ownerSame) {
        _ownerSame = ownerSame;
    }

    public ArrayList getProductsTypeList() {
        return _productsTypeList;
    }

    public void setProductsTypeList(ArrayList productsTypeList) {
        _productsTypeList = productsTypeList;
    }

    public String getProductType() {
        return _productType;
    }

    public void setProductType(String productType) {
        _productType = productType;
    }

    public String getProductTypeDesc() {
        return _productTypeDesc;
    }

    public void setProductTypeDesc(String productTypeDesc) {
        _productTypeDesc = productTypeDesc;
    }

    public String getTransferNum() {
        return _transferNum;
    }

    public void setTransferNum(String transferNum) {
        _transferNum = transferNum;
    }

    public String getUserID() {
        return _userID;
    }

    public void setUserID(String userId) {
        _userID = userId;
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

    public ArrayList getTransferList() {
        return _transferList;
    }

    public void setTransferList(ArrayList transferList) {
        _transferList = transferList;
    }

    public String getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(String selectedIndex) {
        this.selectedIndex = selectedIndex;
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

    public String getApprove2Remark() {
        return _approve2Remark;
    }

    public void setApprove2Remark(String approve2Remark) {
        _approve2Remark = approve2Remark;
    }

    public String getApprove3Remark() {
        return _approve3Remark;
    }

    public void setApprove3Remark(String approve3Remark) {
        _approve3Remark = approve3Remark;
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

    public String getCurrentApprovalLevel() {
        return _currentApprovalLevel;
    }

    public void setCurrentApprovalLevel(String currentApprovalLevel) {
        _currentApprovalLevel = currentApprovalLevel;
    }

    public String getDistributorName() {
        return _distributorName;
    }

    public void setDistributorName(String distributorName) {
        _distributorName = distributorName;
    }

    public String getDomainCode() {
        return _domainCode;
    }

    public void setDomainCode(String domainCode) {
        _domainCode = domainCode;
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

    public String getExternalTxnExist() {
        return _externalTxnExist;
    }

    public void setExternalTxnExist(String externalTxnExist) {
        _externalTxnExist = externalTxnExist;
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

    public String getSelectedUserId() {
        return _selectedUserId;
    }

    public void setSelectedUserId(String selectedUserId) {
        _selectedUserId = selectedUserId;
    }

    public String getTransferDate() {
        return _transferDate;
    }

    public void setTransferDate(String transferDate) {
        _transferDate = transferDate;
    }

    public ArrayList getTransferItemsList() {
        return _transferItemsList;
    }

    public void setTransferItemsList(ArrayList transferItemsList) {
        _transferItemsList = transferItemsList;
    }

    public String getTransferNumberDispaly() {
        return _transferNumberDispaly;
    }

    public void setTransferNumberDispaly(String transferNumberDispaly) {
        _transferNumberDispaly = transferNumberDispaly;
    }

    public int getListSize() {
        return _listSize;
    }

    public void setListSize(int listSize) {
        _listSize = listSize;
    }

    public int getSearchListSize() {
        return _searchListSize;
    }

    public void setSearchListSize(int searchListSize) {
        _searchListSize = searchListSize;
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

	private void setlocaleDate() {
		_fromDate = BTSLDateUtil.getSystemLocaleDate(_fromDate);
		_toDate = BTSLDateUtil.getSystemLocaleDate(_toDate);
		_toDateForUserCode = BTSLDateUtil.getSystemLocaleDate(_toDateForUserCode);
		_fromDateForUserCode = BTSLDateUtil.getSystemLocaleDate(_fromDateForUserCode);
	}

    /**
     * @return Returns the transferTypeCode.
     */
    public String getTransferTypeCode() {
        return _transferTypeCode;
    }

    /**
     * @param transferTypeCode
     *            The transferTypeCode to set.
     */
    public void setTransferTypeCode(String transferTypeCode) {
        _transferTypeCode = transferTypeCode;
    }

    /**
     * @return Returns the transferTypeValue.
     */
    public String getTransferTypeValue() {
        return _transferTypeValue;
    }

    /**
     * @param transferTypeValue
     *            The transferTypeValue to set.
     */
    public void setTransferTypeValue(String transferTypeValue) {
        _transferTypeValue = transferTypeValue;
    }

    /**
     * @return Returns the transferTypeList.
     */
    public ArrayList getTransferTypeList() {
        return _transferTypeList;
    }

    /**
     * @param transferTypeList
     *            The transferTypeList to set.
     */
    public void setTransferTypeList(ArrayList transferTypeList) {
        _transferTypeList = transferTypeList;
    }

    public String getTransferCategoryCode() {
        return _transferCategoryCode;
    }

    public void setTransferCategoryCode(String transferCategoryCode) {
        _transferCategoryCode = transferCategoryCode;
    }

    public String getTransferCategoryDesc() {
        return _transferCategoryDesc;
    }

    public void setTransferCategoryDesc(String transferCategoryDesc) {
        _transferCategoryDesc = transferCategoryDesc;
    }

    public ArrayList getTransferCategoryList() {
        return _transferCategoryList;
    }

    public void setTransferCategoryList(ArrayList transferCategoryList) {
        _transferCategoryList = transferCategoryList;
    }

    public String getTransferProfileName() {
        return _transferProfileName;
    }

    public void setTransferProfileName(String transferProfileName) {
        _transferProfileName = transferProfileName;
    }

    public String getStatusDesc() {
        return _statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        _statusDesc = statusDesc;
    }

    /**
     * @return Returns the networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param networkCode
     *            The networkCode to set.
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * @return Returns the networkName.
     */
    public String getNetworkName() {
        return _networkName;
    }

    /**
     * @param networkName
     *            The networkName to set.
     */
    public void setNetworkName(String networkName) {
        _networkName = networkName;
    }

    /**
     * @return Returns the reportHeaderName.
     */
    public String getReportHeaderName() {
        return _reportHeaderName;
    }

    /**
     * @param reportHeaderName
     *            The reportHeaderName to set.
     */
    public void setReportHeaderName(String reportHeaderName) {
        _reportHeaderName = reportHeaderName;
    }

    public String getGeoDomainNameForUser() {
        return _geoDomainNameForUser;
    }

    public void setGeoDomainNameForUser(String geoDomainNameForUser) {
        _geoDomainNameForUser = geoDomainNameForUser;
    }

    public String getFromDateForUserCode() {
        return _fromDateForUserCode;
    }

    public void setFromDateForUserCode(String fromDateForUserCode) {
        _fromDateForUserCode = fromDateForUserCode;
    }

    public String getStatusCodeForUserCode() {
        return _statusCodeForUserCode;
    }

    public void setStatusCodeForUserCode(String statusCodeForUserCode) {
        _statusCodeForUserCode = statusCodeForUserCode;
    }

    public String getToDateForUserCode() {
        return _toDateForUserCode;
    }

    public void setToDateForUserCode(String toDateForUserCode) {
        _toDateForUserCode = toDateForUserCode;
    }

    public String getTrfCatForUserCode() {
        return _trfCatForUserCode;
    }

    public void setTrfCatForUserCode(String trfCatForUserCode) {
        _trfCatForUserCode = trfCatForUserCode;
    }

    public String getUserCode() {
        return _userCode;
    }

    public void setUserCode(String userCode) {
        _userCode = userCode;
    }

    public String getStatusDetail() {
        return _statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        _statusDetail = statusDetail;
    }

    public String getTrfTypeForUserCode() {
        return _trfTypeForUserCode;
    }

    public void setTrfTypeForUserCode(String trfTypeForUserCode) {
        _trfTypeForUserCode = trfTypeForUserCode;
    }

    public String getTrfTypeDetail() {
        return _trfTypeDetail;
    }

    public void setTrfTypeDetail(String trfTypeDetail) {
        _trfTypeDetail = trfTypeDetail;
    }

    public String getSessionDomainCode() {
        return _sessionDomainCode;
    }

    public void setSessionDomainCode(String sessionDomainCode) {
        _sessionDomainCode = sessionDomainCode;
    }

    /**
     * method flush()
     * to flush all data from the from bean .
     * void
     */
    public void flush() {
        _geoDomainList = null;
        _channelDomainList = null;
        _productsTypeList = null;
        _categoryList = null;
        _geoDomainCode = null;
        _channelDomain = null;
        _productType = null;
        _categoryCode = null;
        _geoDomainCodeDesc = null;
        _channelDomainDesc = null;
        _productTypeDesc = null;
        _categoryCodeDesc = null;
        _transferNum = null;
        _userList = null;
        _userName = null;
        _listSize = 0;
        _userID = null;
        _ownerSame = false;
        _channelOwnerCategory = null;
        _channelOwnerCategoryDesc = null;
        _channelOwnerCategoryUserName = null;
        _channelCategoryUserName = null;
        _channelOwnerCategoryUserID = null;
        _channelCategoryUserID = null;
        _fromDate = null;
        _toDate = null;
        _statusCode = null;
        _statusDesc = null;
        _statusList = null;
        selectedIndex = null;
        _transferList = null;
        _transferItemsList = null;
        _domainCode = null;
        _transferNumberDispaly = null;
        _distributorName = null;
        _domainName = null;
        _geographicDomainName = null;
        _primaryTxnNum = null;
        _categoryName = null;
        _gardeDesc = null;
        _erpCode = null;
        _commissionProfileName = null;
        _transferDate = null;
        _externalTxnExist = null;
        _externalTxnNum = null;
        _externalTxnDate = null;
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
        _channelUserLoginedFlag = false;
        _searchListSize = 0;
        _totalMRP = null;
        _totalTax1 = null;
        _totalTax2 = null;
        _totalTax3 = null;
        _totalComm = null;
        _totalReqQty = null;
        _totalStock = null;
        _transferTypeList = null;
        _transferTypeCode = null;
        _transferTypeValue = null;
        _transferCategoryList = null;
        _transferCategoryCode = null;
        _transferCategoryDesc = null;
        _transferProfileName = null;
        _geoDomainNameForUser = null;
        _userCode = null;
        _fromDateForUserCode = null;
        _toDateForUserCode = null;
        _statusCodeForUserCode = null;
        _trfCatForUserCode = null;
        _statusDetail = null;
        _trfTypeForUserCode = null;
        _trfTypeDetail = null;
        _sessionDomainCode = null;
        // For Mali CR--- +ve Commision Apply
        _commissionQuantity = null;
        _receiverCreditQuantity = null;
        _senderDebitQuantity = null;

        _firstLevelApprovedQuantity = null;
        _secondLevelApprovedQuantity = null;
        _thirdLevelApprovedQuantity = null;
			_totalOthComm=null;
			distributorType=null;
    }

    /**
     * method flushSearchUser()
     * To flush the user selection data.
     * void
     */
    public void flushSearchUser() {
        _ownerSame = false;
        _channelOwnerCategory = null;
        _channelOwnerCategoryDesc = null;
        _channelOwnerCategoryUserName = null;
        _channelCategoryUserName = null;
        _channelOwnerCategoryUserID = null;
        _channelCategoryUserID = null;
        _userList = null;
        _userName = null;
        _listSize = 0;
        _userID = null;
		_totalOthComm=null;

    }

    /**
     * To flush the contents of the detailed screen.
     * void
     */
    public void flushRecordContent() {
        _transferNumberDispaly = null;
        _userName = null;
        _domainName = null;
        _geographicDomainName = null;
        _primaryTxnNum = null;
        _gardeDesc = null;
        _erpCode = null;
        _productType = null;
        _productTypeDesc = null;
        _commissionProfileName = null;
        _externalTxnDate = null;
        _externalTxnNum = null;
        _refrenceNum = null;
        _remarks = null;
        _paymentInstrumentName = null;
        _paymentInstNum = null;
        _paymentInstrumentDate = null;
        _paymentInstrumentAmt = null;
        _transferDate = null;
        _payableAmount = null;
        _netPayableAmount = null;
        _approve1Remark = null;
        _approve2Remark = null;
        _approve3Remark = null;
        _address = null;
        _transferProfileName = null;
        _transferCategoryDesc = null;
        _geoDomainCodeDesc = null;
        _channelDomainDesc = null;
        _categoryCodeDesc = null;
        _currentDateFlag = null;
        _currentDateFlagForUserCode = null;
        // For Mali CR--- +ve Commision Apply
        _commissionQuantity = null;
        _receiverCreditQuantity = null;
        _senderDebitQuantity = null;
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

    public String getCurrentDateFlag() {
        return _currentDateFlag;
    }

    public void setCurrentDateFlag(String currentDateFlag) {
        this._currentDateFlag = currentDateFlag;
    }

    public String getCurrentDateFlagForUserCode() {
        return _currentDateFlagForUserCode;
    }

    public void setCurrentDateFlagForUserCode(String currentDateFlagForUserCode) {
        this._currentDateFlagForUserCode = currentDateFlagForUserCode;
    }

    /**
     * @return Returns the commisionQuantity.
     */
    public String getCommissionQuantity() {
        return _commissionQuantity;
    }

    /**
     * @return Returns the receiverCreditQuantity.
     */
    public String getReceiverCreditQuantity() {
        return _receiverCreditQuantity;
    }

    /**
     * @param commisionQuantity
     *            The commisionQuantity to set.
     */
    public void setCommisionQuantity(String commissionQuantity) {
        _commissionQuantity = commissionQuantity;
    }

    /**
     * @param receiverCreditQuantity
     *            The receiverCreditQuantity to set.
     */
    public void setReceiverCreditQuantity(String receiverCreditQuantity) {
        _receiverCreditQuantity = receiverCreditQuantity;
    }

    /**
     * @return Returns the senderDebitQuantity.
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
     * @param senderDebitQuantity
     *            The senderDebitQuantity to set.
     */
    public void setSenderDebitQuantity(String senderDebitQuantity) {
        _senderDebitQuantity = senderDebitQuantity;
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

    public String getReceiverPostStock() {
        return _receiverPostStock;
    }

    public void setReceiverPostStock(String receiverPostStock) {
        _receiverPostStock = receiverPostStock;
    }

    public String getReceiverPreviousStock() {
        return _receiverPreviousStock;
    }

    public void setReceiverPreviousStock(String receiverPreviousStock) {
        _receiverPreviousStock = receiverPreviousStock;
    }

    public String getSenderPostStock() {
        return _senderPostStock;
    }

    public void setSenderPostStock(String senderPostStock) {
        _senderPostStock = senderPostStock;
    }

    public String getSenderPreviousStock() {
        return _senderPreviousStock;
    }

    public void setSenderPreviousStock(String senderPreviousStock) {
        _senderPreviousStock = senderPreviousStock;
    }

    public String getRevTransferNum() {
        return _revTransferNum;
    }

    public void setRevTransferNum(String revTransferNum) {
        this._revTransferNum = revTransferNum;
    }
	public String getTotalOthComm()
	{
		return _totalOthComm;
	}

	public void setTotalOthComm(String totalOthComm)
	{
		this._totalOthComm = totalOthComm;
	}

	public ArrayList<VomsBatchVO> getVoucherList() {
		return voucherList;
	}

	public void setVoucherList(ArrayList<VomsBatchVO> voucherList) {
		this.voucherList = voucherList;
	}

	private ArrayList<VomsBatchVO> voucherList;

	public void setPackageTransferList(ArrayList packageTransferList){
		this._packageTransferList = packageTransferList;
	}
	
	public ArrayList getPackageTransferList(){
		return this._packageTransferList;
	}

	public String getEncKey() {
		return encKey;
	}

	public void setEncKey(String encKey) {
		this.encKey = encKey;
	}

	@Override
	public String toString() {
		return "ChannelTransferEnquiryForm [_geoDomainList=" + _geoDomainList + ", _channelDomainList="
				+ _channelDomainList + ", _productsTypeList=" + _productsTypeList + ", _categoryList=" + _categoryList
				+ ", _geoDomainCode=" + _geoDomainCode + ", _channelDomain=" + _channelDomain + ", _productType="
				+ _productType + ", _categoryCode=" + _categoryCode + ", _geoDomainCodeDesc=" + _geoDomainCodeDesc
				+ ", _channelDomainDesc=" + _channelDomainDesc + ", _productTypeDesc=" + _productTypeDesc
				+ ", _categoryCodeDesc=" + _categoryCodeDesc + ", _transferNum=" + _transferNum + ", _networkCode="
				+ _networkCode + ", _networkName=" + _networkName + ", _reportHeaderName=" + _reportHeaderName
				+ ", _userList=" + _userList + ", _userName=" + _userName + ", _listSize=" + _listSize + ", _userID="
				+ _userID + ", _ownerSame=" + _ownerSame + ", _channelOwnerCategory=" + _channelOwnerCategory
				+ ", _channelOwnerCategoryDesc=" + _channelOwnerCategoryDesc + ", _channelOwnerCategoryUserName="
				+ _channelOwnerCategoryUserName + ", _channelCategoryUserName=" + _channelCategoryUserName
				+ ", _channelOwnerCategoryUserID=" + _channelOwnerCategoryUserID + ", _channelCategoryUserID="
				+ _channelCategoryUserID + ", _fromDate=" + _fromDate + ", _toDate=" + _toDate + ", _statusCode="
				+ _statusCode + ", _statusDesc=" + _statusDesc + ", _statusList=" + _statusList + ", selectedIndex="
				+ selectedIndex + ", _transferList=" + _transferList + ", _transferItemsList=" + _transferItemsList
				+ ", _domainCode=" + _domainCode + ", _transferNumberDispaly=" + _transferNumberDispaly
				+ ", _distributorName=" + _distributorName + ", _domainName=" + _domainName + ", _geographicDomainName="
				+ _geographicDomainName + ", _primaryTxnNum=" + _primaryTxnNum + ", _categoryName=" + _categoryName
				+ ", _gardeDesc=" + _gardeDesc + ", _erpCode=" + _erpCode + ", _commissionProfileName="
				+ _commissionProfileName + ", _transferDate=" + _transferDate + ", _externalTxnExist="
				+ _externalTxnExist + ", _externalTxnNum=" + _externalTxnNum + ", _externalTxnDate=" + _externalTxnDate
				+ ", _refrenceNum=" + _refrenceNum + ", _address=" + _address + ", _remarks=" + _remarks
				+ ", _paymentInstrumentName=" + _paymentInstrumentName + ", _paymentInstrumentCode="
				+ _paymentInstrumentCode + ", _paymentInstNum=" + _paymentInstNum + ", _paymentInstrumentDate="
				+ _paymentInstrumentDate + ", _paymentInstrumentAmt=" + _paymentInstrumentAmt + ", _approve1Remark="
				+ _approve1Remark + ", _approve2Remark=" + _approve2Remark + ", _approve3Remark=" + _approve3Remark
				+ ", _currentApprovalLevel=" + _currentApprovalLevel + ", _selectedUserId=" + _selectedUserId
				+ ", _payableAmount=" + _payableAmount + ", _netPayableAmount=" + _netPayableAmount
				+ ", _channelUserLoginedFlag=" + _channelUserLoginedFlag + ", _searchListSize=" + _searchListSize
				+ ", _totalMRP=" + _totalMRP + ", _totalTax1=" + _totalTax1 + ", _totalTax2=" + _totalTax2
				+ ", _totalTax3=" + _totalTax3 + ", _totalComm=" + _totalComm + ", _totalReqQty=" + _totalReqQty
				+ ", _totalStock=" + _totalStock + ", _transferTypeList=" + _transferTypeList + ", _transferTypeCode="
				+ _transferTypeCode + ", _transferTypeValue=" + _transferTypeValue + ", _transferCategoryList="
				+ _transferCategoryList + ", _transferCategoryCode=" + _transferCategoryCode
				+ ", _transferCategoryDesc=" + _transferCategoryDesc + ", _transferProfileName=" + _transferProfileName
				+ ", _geoDomainNameForUser=" + _geoDomainNameForUser + ", _userCode=" + _userCode
				+ ", _fromDateForUserCode=" + _fromDateForUserCode + ", _toDateForUserCode=" + _toDateForUserCode
				+ ", _statusCodeForUserCode=" + _statusCodeForUserCode + ", _trfCatForUserCode=" + _trfCatForUserCode
				+ ", _trfTypeForUserCode=" + _trfTypeForUserCode + ", _statusDetail=" + _statusDetail
				+ ", _sessionDomainCode=" + _sessionDomainCode + ", _trfTypeDetail=" + _trfTypeDetail + ", _time="
				+ _time + ", _currentDateFlag=" + _currentDateFlag + ", _currentDateFlagForUserCode="
				+ _currentDateFlagForUserCode + ", _commissionQuantity=" + _commissionQuantity
				+ ", _receiverCreditQuantity=" + _receiverCreditQuantity + ", _senderDebitQuantity="
				+ _senderDebitQuantity + ", _firstLevelApprovedQuantity=" + _firstLevelApprovedQuantity
				+ ", _secondLevelApprovedQuantity=" + _secondLevelApprovedQuantity + ", _thirdLevelApprovedQuantity="
				+ _thirdLevelApprovedQuantity + ", _validationCheck=" + _validationCheck + ", _senderPostStock="
				+ _senderPostStock + ", _receiverPostStock=" + _receiverPostStock + ", _senderPreviousStock="
				+ _senderPreviousStock + ", _receiverPreviousStock=" + _receiverPreviousStock + ", _revTransferNum="
				+ _revTransferNum + ", _showPaymentDetails=" + _showPaymentDetails + ", transactionMode="
				+ transactionMode + ", sosSettlementDate=" + sosSettlementDate + ", sosStatus=" + sosStatus
				+ ", totalOtf=" + totalOtf + ", _totalOthComm=" + _totalOthComm + ", dualCommissionType="
				+ dualCommissionType + ", batch_no=" + batch_no + ", product_name=" + product_name + ", batch_type="
				+ batch_type + ", from_serial_no=" + from_serial_no + ", to_serial_no=" + to_serial_no
				+ ", total_no_of_vouchers=" + total_no_of_vouchers + ", transferSubType=" + transferSubType
				+ ", distributorType=" + distributorType + ", distributorTypeDesc=" + distributorTypeDesc
				+ ", _distributorTypeList=" + _distributorTypeList + ", _packageTransferList=" + _packageTransferList
				+ ", encKey=" + encKey + ", voucherList=" + voucherList + "]";
	}
	
	
}
