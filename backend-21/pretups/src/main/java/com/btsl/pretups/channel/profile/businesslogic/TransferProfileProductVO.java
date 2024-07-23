/**
 * @(#)TransferProfileProductVO.java
 *                                   Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                   All Rights Reserved
 * 
 *                                   <description>
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Author Date History
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   avinash.kamthan Aug 27, 2005 Initital
 *                                   Creation
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 * 
 */

package com.btsl.pretups.channel.profile.businesslogic;

import java.io.Serializable;

/**
 * @author avinash.kamthan
 * 
 */
public class TransferProfileProductVO implements Serializable {

    private String _minBalance;
    private String _maxBalance;
    private long _minResidualBalanceAsLong;
    private long _maxBalanceAsLong;
    private String _productCode;
    private String _productName;
    private String _altBalance;
    private String _allowedMaxPercentage;
    private long _altBalanceLong;
    private int _allowedMaxPercentageInt;
    private String _currentBalance;
    private String _productShortCode;
    private String _c2sMinTxnAmt;
    private String _c2sMaxTxnAmt;
    private long _c2sMinTxnAmtAsLong;
    private long _c2sMaxTxnAmtAsLong;

    /**
     * @return Returns the allowedMaxPercentage.
     */
    public String getAllowedMaxPercentage() {
        return _allowedMaxPercentage;
    }

    /**
     * @param allowedMaxPercentage
     *            The allowedMaxPercentage to set.
     */
    public void setAllowedMaxPercentage(String allowedMaxPercentage) {
        _allowedMaxPercentage = allowedMaxPercentage;
    }

    /**
     * @return Returns the altBalance.
     */
    public String getAltBalance() {
        return _altBalance;
    }

    /**
     * @param altBalance
     *            The altBalance to set.
     */
    public void setAltBalance(String altBalance) {
        _altBalance = altBalance;
    }

    public String getMaxBalance() {
        return _maxBalance;
    }

    public void setMaxBalance(String maxBalance) {
        _maxBalance = maxBalance;
    }

    public long getMaxBalanceAsLong() {
        return _maxBalanceAsLong;
    }

    public void setMaxBalanceAsLong(long maxBalanceAsLong) {
        _maxBalanceAsLong = maxBalanceAsLong;
    }

    public String getMinBalance() {
        return _minBalance;
    }

    public void setMinBalance(String minBalance) {
        _minBalance = minBalance;
    }

    public long getMinResidualBalanceAsLong() {
        return _minResidualBalanceAsLong;
    }

    public void setMinResidualBalanceAsLong(long minResidualBalanceAsLong) {
        _minResidualBalanceAsLong = minResidualBalanceAsLong;
    }

    public String getProductCode() {
        return _productCode;
    }

    public void setProductCode(String productCode) {
        _productCode = productCode;
    }

    public String getProductName() {
        return _productName;
    }

    public void setProductName(String productName) {
        _productName = productName;
    }

    public int getAllowedMaxPercentageInt() {
        return _allowedMaxPercentageInt;
    }

    public void setAllowedMaxPercentageInt(int allowedMaxPercentageInt) {
        _allowedMaxPercentageInt = allowedMaxPercentageInt;
    }

    public long getAltBalanceLong() {
        return _altBalanceLong;
    }

    public void setAltBalanceLong(long altBalanceLong) {
        _altBalanceLong = altBalanceLong;
    }

    /**
     * @return Returns the currentBalance.
     */
    public String getCurrentBalance() {
        return _currentBalance;
    }

    /**
     * @param currentBalance
     *            The currentBalance to set.
     */
    public void setCurrentBalance(String currentBalance) {
        _currentBalance = currentBalance;
    }

    /**
     * @return Returns the productShortCode.
     */
    public String getProductShortCode() {
        return _productShortCode;
    }

    /**
     * @param productShortCode
     *            The productShortCode to set.
     */
    public void setProductShortCode(String productShortCode) {
        _productShortCode = productShortCode;
    }

    public String getC2sMaxTxnAmt() {
        return _c2sMaxTxnAmt;
    }

    public void setC2sMaxTxnAmt(String maxTxnAmt) {
        _c2sMaxTxnAmt = maxTxnAmt;
    }

    public String getC2sMinTxnAmt() {
        return _c2sMinTxnAmt;
    }

    public void setC2sMinTxnAmt(String minTxnAmt) {
        _c2sMinTxnAmt = minTxnAmt;
    }

    public long getC2sMaxTxnAmtAsLong() {
        return _c2sMaxTxnAmtAsLong;
    }

    public void setC2sMaxTxnAmtAsLong(long maxTxnAmtAsLong) {
        _c2sMaxTxnAmtAsLong = maxTxnAmtAsLong;
    }

    public long getC2sMinTxnAmtAsLong() {
        return _c2sMinTxnAmtAsLong;
    }

    public void setC2sMinTxnAmtAsLong(long minTxnAmtAsLong) {
        _c2sMinTxnAmtAsLong = minTxnAmtAsLong;
    }
    /**
     * Create new object of this class
     * @return TransferProfileProductVO new object of this class
     */
	public static TransferProfileProductVO getInstance() {
		return new TransferProfileProductVO();
	}

}
