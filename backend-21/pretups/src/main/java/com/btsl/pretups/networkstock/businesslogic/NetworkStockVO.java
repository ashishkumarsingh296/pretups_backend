/**
 * @(#)NetworkStockVO.java
 *                         Copyright(c) 2005, Bharti Telesoft Ltd.
 *                         All Rights Reserved
 * 
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         avinash.kamthan Aug 11, 2005 Initital Creation
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 * 
 */

package com.btsl.pretups.networkstock.businesslogic;

import java.io.Serializable;
import java.util.Date;

import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

/**
 * @author avinash.kamthan
 * 
 */
public class NetworkStockVO implements Serializable {

    private String _networkCode;
    private String _networkCodeFor;
    private String _productCode;
    private String _walletType;

    private long _walletCreated;
    private long _walletReturned;
    private long _walletBalance;
    private long _walletSold;
    private String _lastTxnNum;
    private String _lastTxnType;
    private long _lastTxnBalance;
    private long _previousBalance;
    private String _modifiedBy;
    private Date _modifiedOn;
    private String _createdBy;
    private Date _createdOn;

    // added by sandeep
    private String _networkName;
    private String _networkForName;
    private String _productName;
    private String _productMrp;
    private String _walletBalanceValue;
    private String _otherValue;
    private static final long serialVersionUID = 1L;
    // ends here

    public String getWalletbalanceValueStr() {
        if (!BTSLUtil.isNullString(_productMrp)) {
            return PretupsBL.getDisplayAmount( BTSLUtil.parseDoubleToLong( ((_walletBalance) * Double.parseDouble(_productMrp)) ));
        } else {
            return null;
        }
    }

    public String getWalletBalanceStr() {
        return PretupsBL.getDisplayAmount(_walletBalance);
    }

    public String getWalletCreatedStr() {
        return PretupsBL.getDisplayAmount(_walletCreated);
    }

    public String getWalletReturnedStr() {
        return PretupsBL.getDisplayAmount(_walletReturned);
    }

    public String getWalletSoldStr() {
        return PretupsBL.getDisplayAmount(_walletSold);
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    public String getLastTxnNum() {
        return _lastTxnNum;
    }

    public void setLastTxnNum(String lastTxnNum) {
        _lastTxnNum = lastTxnNum;
    }

    public long getLastTxnBalance() {
        return _lastTxnBalance;
    }

    public void setLastTxnBalance(long lastTxnbal) {
        _lastTxnBalance = lastTxnbal;
    }

    public String getLastTxnType() {
        return _lastTxnType;
    }

    public void setLastTxnType(String lastTxnType) {
        _lastTxnType = lastTxnType;
    }

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public long getPreviousBalance() {
        return _previousBalance;
    }

    public void setPreviousBalance(long previousbal) {
        _previousBalance = previousbal;
    }

    public String getProductCode() {
        return _productCode;
    }

    public void setProductCode(String productCode) {
        _productCode = productCode;
    }

    public String getNetworkCodeFor() {
        return _networkCodeFor;
    }

    public void setNetworkCodeFor(String roamNetworkCode) {
        _networkCodeFor = roamNetworkCode;
    }

    public long getWalletbalance() {
        return _walletBalance;
    }

    public void setWalletBalance(long walletBal) {
        _walletBalance = walletBal;
    }

    public long getWalletCreated() {
        return _walletCreated;
    }

    public void setWalletCreated(long walletCreated) {
        _walletCreated = walletCreated;
    }

    public long getWalletReturned() {
        return _walletReturned;
    }

    public void setWalletReturned(long walletReturned) {
        _walletReturned = walletReturned;
    }

    public long getWalletSold() {
        return _walletSold;
    }

    public void setWalletSold(long walletSold) {
        _walletSold = walletSold;
    }

    public String getNetworkForName() {
        return _networkForName;
    }

    public void setNetworkForName(String networkForName) {
        _networkForName = networkForName;
    }

    public String getNetworkName() {
        return _networkName;
    }

    public void setNetworkName(String networkName) {
        _networkName = networkName;
    }

    public String getProductMrp() {
        return _productMrp;
    }

    public void setProductMrp(String productMrp) {
        _productMrp = productMrp;
    }

    public String getProductName() {
        return _productName;
    }

    public void setProductName(String productName) {
        _productName = productName;
    }

    public String getWalletBalanceValue() {
        return _walletBalanceValue;
    }

    public void setWalletBalanceValue(String walletBalValue) {
        _walletBalanceValue = walletBalValue;
    }

    public String getWalletType() {
        return _walletType;
    }

    public void setWalletType(String walletType) {
        _walletType = walletType;
    }
    public String getOtherValue() {
        return _otherValue;
    }

    public void setOtherValue(String otherValue) {
        _otherValue = otherValue;
    }
    @Override
    public String toString() {
        StringBuilder sbf = new StringBuilder();
        sbf.append("_lastTxnNum =" + _lastTxnNum);
        sbf.append(", _lastTxnStock=" + _lastTxnBalance);
        sbf.append(", _lastTxnType=" + _lastTxnType);
        sbf.append(",_networkCode =" + _networkCode);
        sbf.append(", _networkCodeFor=" + _networkCodeFor);
        sbf.append(", _productCode=" + _productCode);
        sbf.append(",_previousStock =" + _previousBalance);
        sbf.append(",_stock =" + _walletBalance);
        sbf.append(",_createdOn =" + _createdOn);
        sbf.append(",_createdBy =" + _createdBy);
        sbf.append(",_modifiedOn =" + _modifiedOn);
        sbf.append(",_modifiedBy =" + _modifiedBy);
        sbf.append(",_walletType =" + _walletType);
        sbf.append(",_otherValue =" + _otherValue);

        return sbf.toString();
    }
	
	/**
     * Create new object of this class
     * @return NetworkStockVO new object of this class
     */
    public static NetworkStockVO getInstance(){
		return new NetworkStockVO();
	}
}
