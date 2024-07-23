/**
 * @(#)UserBalancesVO.java
 *                         Copyright(c) 2005, Bharti Telesoft Ltd.
 *                         All Rights Reserved
 * 
 *                         <description>
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         avinash.kamthan Aug 3, 2005 Initital Creation
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 * 
 */

package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.btsl.pretups.channel.user.businesslogic.wallet.UserProductWalletMappingVO;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserLoanVO;
/**
 * @author avinash.kamthan
 * 
 */
public class UserBalancesVO implements Serializable, Cloneable {
   

	private String _userID;
    private String _networkCode;
    private String _networkFor;
    private String _productCode;
    private long _balance;
    private long _previousBalance;
    private String _lastTransferType;
    private String _lastTransferID;
    private Date _lastTransferOn;
    private long _quantityToBeUpdated;

    private String _productShortName;
    private String productType;
    private long _unitValue;
    private String _transferCategory;
    private String _requestedQuantity;
    private String _transferProfileID;// transfr Profile ID for check the
    // balances of product against the profile
    // ID
    private String _entryType;
    private String _type;
    private String _source;
    private String _createdBy;
    private String _otherInfo;
    private String _productShortCode;

    private String _balanceStr; // added by sandeep goel to view the balance in
    // the display amount mode.

    private String _agentBalanceStr;
    private long _netAmount;
    private String _productName;
    
    private transient  ArrayList<UserBalancesVO> dailyBalanceUpdateCountList;

    Date dailyBalanceUpdatedOn;
    
    public Date getDailyBalanceUpdatedOn() {
		return dailyBalanceUpdatedOn;
	}

	public void setDailyBalanceUpdatedOn(Date dailyBalanceUpdatedOn) {
		this.dailyBalanceUpdatedOn = dailyBalanceUpdatedOn;
	}

	/** Birendra : START */
    private List<UserProductWalletMappingVO> pdaWalletList;
    private String walletCode;
    /** Birendra : END */

    private String balanceType;

    // To add subscriber number & user type on 11/02/2008
    private String _userMSISDN;

    // roam penalty
    private long _roamPenalty;
    
    private long _openingBalance;
    
    private long _totalHierarchyBalance;
    
    private long _totalChildrenBalance;
    private List<ChannelSoSVO> channelSoSVOList;
    private boolean lrFlag;
    
    private List<UserLoanVO> userLoanVOList;
    public List<ChannelSoSVO> getChannelSoSVOList() {
		return channelSoSVOList;
	}

	public void setChannelSoSVOList(List<ChannelSoSVO> channelSoSVOList) {
		this.channelSoSVOList = channelSoSVOList;
	}

	/**
	 * @return the _openingBalance
	 */
	public long getOpeningBalance() {
		return _openingBalance;
	}

	/**
	 * @param _openingBalance the _openingBalance to set
	 */
	public void setOpeningBalance(long openingBalance) {
		this._openingBalance = openingBalance;
	}

	/**
	 * @return the _totalHierarchyBalance
	 */
	public long getTotalHierarchyBalance() {
		return _totalHierarchyBalance;
	}

	/**
	 * @param _totalHierarchyBalance the _totalHierarchyBalance to set
	 */
	public void setTotalHierarchyBalance(long totalHierarchyBalance) {
		this._totalHierarchyBalance = totalHierarchyBalance;
	}

	/**
	 * @return the _totalChildrenBalance
	 */
	public long getTotalChildrenBalance() {
		return _totalChildrenBalance;
	}

	/**
	 * @param _totalChildrenBalance the _totalChildrenBalance to set
	 */
	public void setTotalChildrenBalance(long totalChildrenBalance) {
		this._totalChildrenBalance = totalChildrenBalance;
	}

	public long getRoamPenalty() {
        return _roamPenalty;
    }

    public void setRoamPenalty(long _roamPenalty) {
        this._roamPenalty = _roamPenalty;
    }

    public String getRequestedQuantity() {
        return _requestedQuantity;
    }

    public void setRequestedQuantity(String requestedQuantity) {
        _requestedQuantity = requestedQuantity;
    }

    public long getBalance() {
        return _balance;
    }

    public void setBalance(long balances) {
        _balance = balances;
    }

    public String getLastTransferID() {
        return _lastTransferID;
    }

    public void setLastTransferID(String lastTransferNum) {
        _lastTransferID = lastTransferNum;
    }

    public Date getLastTransferOn() {
        return _lastTransferOn;
    }

    public void setLastTransferOn(Date lastTransferOn) {
        _lastTransferOn = lastTransferOn;
    }

    public String getLastTransferType() {
        return _lastTransferType;
    }

    public void setLastTransferType(String lastTransferType) {
        _lastTransferType = lastTransferType;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networCode) {
        _networkCode = networCode;
    }

    public long getPreviousBalance() {
        return _previousBalance;
    }

    public void setPreviousBalance(long previousBalances) {
        _previousBalance = previousBalances;
    }

    public String getProductCode() {
        return _productCode;
    }

    public void setProductCode(String productCode) {
        _productCode = productCode;
    }

    public String getNetworkFor() {
        return _networkFor;
    }

    public void setNetworkFor(String roamNetworkCode) {
        _networkFor = roamNetworkCode;
    }

    public String getUserID() {
        return _userID;
    }

    public void setUserID(String userID) {
        _userID = userID;
    }

    public String getBalanceAsString() {
        return PretupsBL.getDisplayAmount(_balance);
    }

    public String getProductShortName() {
        return _productShortName;
    }

    public void setProductShortName(String productShortName) {
        _productShortName = productShortName;
    }

    public String getUnitValueAsString() {
        return PretupsBL.getDisplayAmount(_unitValue);
    }

    public long getUnitValue() {
        return _unitValue;
    }

    public void setUnitValue(long unitValue) {
        _unitValue = unitValue;
    }

    public String getTransferProfileID() {
        return _transferProfileID;
    }

    public void setTransferProfileID(String transferProfileID) {
        _transferProfileID = transferProfileID;
    }

    public long getQuantityToBeUpdated() {
        return _quantityToBeUpdated;
    }

    public void setQuantityToBeUpdated(long quantityToBeUpdated) {
        _quantityToBeUpdated = quantityToBeUpdated;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    public String getEntryType() {
        return _entryType;
    }

    public void setEntryType(String entryType) {
        _entryType = entryType;
    }

    public String getSource() {
        return _source;
    }

    public void setSource(String source) {
        _source = source;
    }

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        _type = type;
    }

    public String getTransferCategory() {
        return _transferCategory;
    }

    public void setTransferCategory(String transferCategory) {
        _transferCategory = transferCategory;
    }

    public String getOtherInfo() {
        return _otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        _otherInfo = otherInfo;
    }

    public String getBalanceStr() {
        return _balanceStr;
    }

    public void setBalanceStr(String balanceStr) {
        _balanceStr = balanceStr;
    }

    public String getProductShortCode() {
        return _productShortCode;
    }

    public void setProductShortCode(String productShortCode) {
        _productShortCode = productShortCode;
    }

    public String getAgentBalanceStr() {
        return _agentBalanceStr;
    }

    public void setAgentBalanceStr(String agentBalanceStr) {
        _agentBalanceStr = agentBalanceStr;
    }

    public long getNetAmount() {
        return _netAmount;
    }

    public void setNetAmount(long netAmount) {
        _netAmount = netAmount;
    }

    /**
     * @return Returns the productName.
     */
    public String getProductName() {
        return _productName;
    }

    /**
     * @param productName
     *            The productName to set.
     */
    public void setProductName(String productName) {
        _productName = productName;
    }

    /**
     * Returns userMSISDN
     * 
     * @return Returns the userMSISDN.
     */
    public String getUserMSISDN() {
        return this._userMSISDN;
    }

    /**
     * Sets userMSISDN
     * 
     * @param userMSISDN
     *            String
     */
    public void setUserMSISDN(String userMSISDN) {
        this._userMSISDN = userMSISDN;
    }

    /** Birendra : START */
    public List<UserProductWalletMappingVO> getPdaWalletList() {
        return pdaWalletList;
    }

    public void setPdaWalletList(List<UserProductWalletMappingVO> pdaWalletList) {
        this.pdaWalletList = pdaWalletList;
    }

    /** Birendra : END */
    public String getWalletCode() {
        return walletCode;
    }

    public void setWalletCode(String walletCode) {
        this.walletCode = walletCode;
    }

    public String getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(String balanceType) {
        this.balanceType = balanceType;
    }
    public boolean getLRFlag() {
        return lrFlag;
    }

    public void setLRFlag(boolean flag) {
    	lrFlag = flag;
    }
    @Override
  	public String toString() {
      	StringBuilder sbf = new StringBuilder();
      	 sbf.append("UserBalancesVO [_userID=").append(_userID);
      	 sbf.append(", _networkCode=").append(_networkCode);
      	 sbf.append(", _networkFor=").append(_networkFor);
      	 sbf.append(", _productCode=").append(_productCode);
      	 sbf.append(", _balance=").append(_balance);
      	 sbf.append(", _previousBalance=").append(_previousBalance);
      	 sbf.append(", _lastTransferType=").append(_lastTransferType);
      	 sbf.append(", _lastTransferID=").append(_lastTransferID);
      	 sbf.append(", _lastTransferOn=").append(_lastTransferOn);
      	 sbf.append(", _quantityToBeUpdated=").append(_quantityToBeUpdated);
      	 sbf.append(", _productShortName=").append(_productShortName);
      	 sbf.append(", _transferCategory=").append(_transferCategory);
      	 sbf.append(", _requestedQuantity=").append(_requestedQuantity);
      	 sbf.append(", _transferProfileID=").append(_transferProfileID);
      	 sbf.append(", _entryType=").append(_entryType);
      	 sbf.append(", _type=").append(_type);
      	 sbf.append(", _source=").append(_source);
      	 sbf.append(", _createdBy=").append(_createdBy);
      	 sbf.append(", _otherInfo=").append(_otherInfo);
      	 sbf.append(", _productShortCode=").append(_productShortCode);
      	 sbf.append(", _balanceStr=").append(_balanceStr);
      	 sbf.append(", _agentBalanceStr=").append(_agentBalanceStr);
      	 sbf.append(", _netAmount=").append(_netAmount);
      	 sbf.append(", _productName=").append(_productName);
      	 sbf.append(", _pdaWalletList=").append(pdaWalletList);
      	 sbf.append(", _walletCode=").append(walletCode);
      	 sbf.append(", _balanceType=").append(balanceType);
      	 sbf.append(", _userMSISDN=").append(_userMSISDN);
      	 sbf.append(", _roamPenalty=").append(_roamPenalty).append("]");
      	 return sbf.toString();
  	}
    
    
    @Override
	public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

	public ArrayList<UserBalancesVO> getDailyBalanceUpdateCountList() {
		return dailyBalanceUpdateCountList;
	}

	public void setDailyBalanceUpdateCountList(ArrayList<UserBalancesVO> dailyBalanceUpdateCountList) {
		this.dailyBalanceUpdateCountList = dailyBalanceUpdateCountList;
	}

	/**
     * Create new object of this class
     * @return UserBalancesVO new object of this class
     */
    public static UserBalancesVO getInstance(){
		return new UserBalancesVO();
	}
    
	public List<UserLoanVO> getUserLoanVOList() {
		return userLoanVOList;
	}

	public void setUserLoanVOList(List<UserLoanVO> userLoanVOList) {
		this.userLoanVOList = userLoanVOList;
	}

    
}

