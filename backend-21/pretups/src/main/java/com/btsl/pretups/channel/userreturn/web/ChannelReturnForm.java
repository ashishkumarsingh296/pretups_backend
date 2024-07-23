/**
 * @(#)ChannelReturnForm.java
 *                            Copyright(c) 2005, Bharti Telesoft Ltd.
 *                            All Rights Reserved
 * 
 *                            <description>
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            avinash.kamthan Aug 16, 2005 Initital Creation
 *                            Sandeep Goel Nov 10,2005
 *                            Customization,Modification
 *                            Sandeep Goel Aug, 03,2006 Modification ID QTY001
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 * 
 */

package com.btsl.pretups.channel.userreturn.web;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

/*import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorActionForm;*/

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.user.businesslogic.UserLoanVO;
/**
 * @author avinash.kamthan
 * 
 */
public class ChannelReturnForm /*extends ValidatorActionForm*/ {
    private Log _log = LogFactory.getLog(ChannelReturnForm.class.getName());
    private String _networkName;
    private String _networkCode;

    private String _geoDomainCode;
    private String _geoDomainCodeForUserCode;
    private String _channelDomain;
    private String _channelDomainForUser;

    private String _productType;
    private String _categoryCode;
    private String _categoryCodeForUserCode;

    private String _geoDomainCodeDesc;
    private String _geoDomainCodeDescOfUser;
    private String _channelDomainDesc;
    private String _channelDomainDescOfUser;

    private String _productTypeDesc;
    private String _categoryCodeDesc;

    private ArrayList _geoDomainList;
    private ArrayList _channelDomainList;
    private ArrayList _productsTypeList;
    private ArrayList _categoryList;

    // screen2
    private boolean _ownerSame;
    private String _channelOwnerCategory;
    private String _channelOwnerCategoryDesc;
    private String _channelOwnerCategoryUserName;
    private String _channelCategoryUserName;
    private String _channelOwnerCategoryUserID;
    private String _channelCategoryUserID;
    // screen2a
    private ArrayList _userList;
    private String _userName;
    private int _listSize;
    private String _userID;

    // screen 3 product list
    private ArrayList _returnedProductList;
    private String _primaryPhoneNum;
    private String _commissionProfile;
    private String _gradeDesc;
    private String _txnProfileName;
    private String _remarks;
    private String _address;

    private String _commissionProfileID;
    private String _commissionProfileVersionID;
    private String _gradeCode;
    private String _txnProfileID;

    // screen 4
    private ArrayList _returnedProductListWithTaxes;

    private boolean _returnFlag = false;

    private String _productTypeWithUserCode;
    private boolean _fromUserCodeFlag;
    private String _userCode;

    private String _totalMRP;
    private String _totalPayableAmount;
    private String _totalNetPayableAmount;
    private String _totalTax1;
    private String _totalTax2;
    private String _totalTax3;
    private String _totalReqQty;
    private String _totalTransferedAmount;
    private String _totalStock;
    private String _totalComm;
    private String _transferCategory;
    private long _time = 0;

    private String _sessionDomainCode = null;
    private String _toPrimaryMSISDN;
    // For Mali --- +ve Commision Apply
    private String _senderDebitQty = null;
    private String _receiverCreditQty = null;
    private String _netCommQty = null;

    // withdrawal to multiple wallet
    private ArrayList _walletTypeList;
    private String _walletType;
    private String _walletTypeName;

    // Added by Amit Raheja
    private String _smsPin = null;
    private String _displayMsisdn = null;
    private String _displayPin = null;
    private List<ChannelSoSVO> channelSoSVOList;
    // Addition ends
    // For PIN Authentication in O2C Transactions- 04/03/13.
    private boolean _showPin = false;
    // For CAPTCHA
    private String j_captcha_response = null;
    
    private String dualCommissionType;
	 
    private List<UserLoanVO> userLoanVOList;
    
	public String getDualCommissionType() {
		return dualCommissionType;
	}

	public void setDualCommissionType(String dualCommissionType) {
		this.dualCommissionType = dualCommissionType;
	}


    public String getWalletTypeName() {
        return _walletTypeName;
    }

    public void setWalletTypeName(String walletTypeName) {
        _walletTypeName = walletTypeName;
    }

    public String getWalletType() {
        return _walletType;
    }

    public void setWalletType(String walletType) {
        _walletType = walletType;
    }

    public ArrayList getWalletTypeList() {
        return _walletTypeList;
    }

    public void setWalletTypeList(ArrayList walletTypeList) {
        _walletTypeList = walletTypeList;
    }

    public String getSessionDomainCode() {
        return _sessionDomainCode;
    }

    public void setSessionDomainCode(String sessionDomainCode) {
        _sessionDomainCode = sessionDomainCode;
    }

    public String getTotalComm() {
        return _totalComm;
    }

    public void setTotalComm(String totalComm) {
        _totalComm = totalComm;
    }

    public String getTotalStock() {
        return _totalStock;
    }

    public void setTotalStock(String totalStock) {
        _totalStock = totalStock;
    }

    public String getTotalReqQty() {
        return _totalReqQty;
    }

    public void setTotalReqQty(String totalReqQty) {
        _totalReqQty = totalReqQty;
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

    public String getTotalTransferedAmount() {
        return _totalTransferedAmount;
    }

    public void setTotalTransferedAmount(String totalTransferedAmount) {
        _totalTransferedAmount = totalTransferedAmount;
    }

    public int getProductTypesListSize() {
        if (_productsTypeList != null) {
            return _productsTypeList.size();
        }
        return 0;
    }

    public String getUserCode() {
        return _userCode;
    }

    public void setUserCode(String userCode) {
        _userCode = userCode;
    }

    public boolean getFromUserCodeFlag() {
        return _fromUserCodeFlag;
    }

    public void setFromUserCodeFlag(boolean fromUserCodeFlag) {
        _fromUserCodeFlag = fromUserCodeFlag;
    }

    public String getProductTypeWithUserCode() {
        return _productTypeWithUserCode;
    }

    public void setProductTypeWithUserCode(String productTypeWithUserCode) {
        _productTypeWithUserCode = productTypeWithUserCode;
    }

    public boolean getReturnFlag() {
        return _returnFlag;
    }

    public void setReturnFlag(boolean returnFlag) {
        _returnFlag = returnFlag;
    }

    public ArrayList getReturnedProductListWithTaxes() {
        return _returnedProductListWithTaxes;
    }

    public void setReturnedProductListWithTaxes(ArrayList returnedProductListWithTaxes) {
        _returnedProductListWithTaxes = returnedProductListWithTaxes;
    }

    public String getAddress() {
        return _address;
    }

    public void setAddress(String address) {
        _address = address;
    }

    public String getCommissionProfile() {
        return _commissionProfile;
    }

    public void setCommissionProfile(String commissionProfile) {
        _commissionProfile = commissionProfile;
    }

    public String getGradeDesc() {
        return _gradeDesc;
    }

    public void setGradeDesc(String gradeDesc) {
        _gradeDesc = gradeDesc;
    }

    public String getPrimaryPhoneNum() {
        return _primaryPhoneNum;
    }

    public void setPrimaryPhoneNum(String primaryPhoneNum) {
        _primaryPhoneNum = primaryPhoneNum;
    }

    public String getRemarks() {
        return _remarks;
    }

    public void setRemarks(String remarks) {
        _remarks = remarks;
    }

    public String getTxnProfileName() {
        return _txnProfileName;
    }

    public void setTxnProfileName(String txnProfileName) {
        _txnProfileName = txnProfileName;
    }

    public void setReturnedProductListIndexed(int i, ChannelTransferItemsVO vo) {
        _returnedProductList.set(i, vo);
    }

    public ChannelTransferItemsVO getReturnedProductListIndexed(int i) {
        return (ChannelTransferItemsVO) _returnedProductList.get(i);
    }

    public ArrayList getReturnedProductList() {
        return _returnedProductList;
    }

    public void setReturnedProductList(ArrayList returnedProductList) {
        _returnedProductList = returnedProductList;
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

    public boolean isOwnerSame() {
        return _ownerSame;
    }

    public void setOwnerSame(boolean ownerSame) {
        _ownerSame = ownerSame;
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

    /**
     * Method flush()
     * This method flush all the contenst of the form bean
     * void
     */
    public void flush() {
        _networkName = null;
        _networkCode = null;
        _geoDomainCode = null;
        _channelDomain = null;
        _productType = null;
        _categoryCode = null;
        _geoDomainCodeDesc = null;
        _channelDomainDesc = null;
        _productTypeDesc = null;
        _categoryCodeDesc = null;
        _geoDomainList = null;
        _channelDomainList = null;
        _productsTypeList = null;
        _categoryList = null;
        // screen2
        _ownerSame = false;
        _channelOwnerCategory = null;
        _channelOwnerCategoryDesc = null;
        _channelOwnerCategoryUserName = null;
        _channelCategoryUserName = null;
        _channelOwnerCategoryUserID = null;
        _channelCategoryUserID = null;
        // screen2a
        _userList = null;
        _userName = null;
        _listSize = 0;
        _userID = null;
        _channelDomainForUser = null;
        _categoryCodeForUserCode = null;
        _productTypeWithUserCode = null;
        _fromUserCodeFlag = false;
        _userCode = null;
        _remarks = null;
        _transferCategory = null;
        _sessionDomainCode = null;
        // For Positive commission
        _senderDebitQty = null;
        _receiverCreditQty = null;
        _netCommQty = null;
        _smsPin = null;
        _displayMsisdn = null;
        _displayPin = null;

    }

    public String getCommissionProfileID() {
        return _commissionProfileID;
    }

    public void setCommissionProfileID(String commissionProfileID) {
        _commissionProfileID = commissionProfileID;
    }

    public String getCommissionProfileVersionID() {
        return _commissionProfileVersionID;
    }

    public void setCommissionProfileVersionID(String commissionProfileVersionID) {
        _commissionProfileVersionID = commissionProfileVersionID;
    }

    public String getGradeCode() {
        return _gradeCode;
    }

    public void setGradeCode(String gradeCode) {
        _gradeCode = gradeCode;
    }

    public String getTxnProfileID() {
        return _txnProfileID;
    }

    public void setTxnProfileID(String txnProfileID) {
        _txnProfileID = txnProfileID;
    }


    /**
     * Mehtod flushProductsList()
     * this method is to flush the list of products.
     * void
     */
    public void flushProductsList() {
        _returnedProductList = null;
        _primaryPhoneNum = null;
        _commissionProfile = null;
        _gradeDesc = null;
        _txnProfileName = null;
        _remarks = null;
        _address = null;
        _commissionProfileID = null;
        _commissionProfileVersionID = null;
        _gradeCode = null;
        _txnProfileID = null;

    }

    /**
     * method flushSearchUser()
     * This method is to flush the user search information
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
        // screen2a
        _userList = null;
        _userName = null;
        _listSize = 0;
        _userID = null;
        _smsPin = null;
        _displayMsisdn = null;
        _displayPin = null;

    }

    public int getListSize() {
        return _listSize;
    }

    public void setListSize(int listSize) {
        _listSize = listSize;
    }

    public String getTotalMRP() {
        return _totalMRP;
    }

    public void setTotalMRP(String totalMRP) {
        _totalMRP = totalMRP;
    }

    public String getTotalNetPayableAmount() {
        return _totalNetPayableAmount;
    }

    public void setTotalNetPayableAmount(String totalNetPayableAmount) {
        _totalNetPayableAmount = totalNetPayableAmount;
    }

    public String getTotalPayableAmount() {
        return _totalPayableAmount;
    }

    public void setTotalPayableAmount(String totalPayableAmount) {
        _totalPayableAmount = totalPayableAmount;
    }

    public String getCategoryCodeForUserCode() {
        return _categoryCodeForUserCode;
    }

    public void setCategoryCodeForUserCode(String categoryCodeForUserCode) {
        _categoryCodeForUserCode = categoryCodeForUserCode;
    }

    public String getChannelDomainForUser() {
        return _channelDomainForUser;
    }

    public void setChannelDomainForUser(String channelDomainForUser) {
        _channelDomainForUser = channelDomainForUser;
    }

    public String getGeoDomainCodeForUserCode() {
        return _geoDomainCodeForUserCode;
    }

    public void setGeoDomainCodeForUserCode(String geoDomainCodeForUserCode) {
        _geoDomainCodeForUserCode = geoDomainCodeForUserCode;
    }

    public String getChannelDomainDescOfUser() {
        return _channelDomainDescOfUser;
    }

    public void setChannelDomainDescOfUser(String channelDomainDescForUserCode) {
        _channelDomainDescOfUser = channelDomainDescForUserCode;
    }

    public String getGeoDomainCodeDescOfUser() {
        return _geoDomainCodeDescOfUser;
    }

    public void setGeoDomainCodeDescOfUser(String geoDomainCodeDescForUserCode) {
        _geoDomainCodeDescOfUser = geoDomainCodeDescForUserCode;
    }

    public String getTransferCategory() {
        return _transferCategory;
    }

    public void setTransferCategory(String transferCategory) {
        _transferCategory = transferCategory;
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

    public boolean getShowPin() {
        return _showPin;
    }

    /**
     * @param _showPin
     *            to set.
     */
    public void setShowPin(boolean ShowPin) {
        _showPin = ShowPin;
    }

    public String getJ_captcha_response() {
        return j_captcha_response;
    }

    public void setJ_captcha_response(String j_captcha_response) {
        this.j_captcha_response = j_captcha_response;
    }

	public List<ChannelSoSVO> getChannelSoSVOList() {
		return channelSoSVOList;
	}

	public void setChannelSoSVOList(List<ChannelSoSVO> channelSoSVOList) {
		this.channelSoSVOList = channelSoSVOList;
	}
	
    public List<UserLoanVO> getUserLoanVOList() {
		return userLoanVOList;
	}

	public void setUserLoanVOList(List<UserLoanVO> userLoanVOList) {
		this.userLoanVOList = userLoanVOList;
	}
    
}
