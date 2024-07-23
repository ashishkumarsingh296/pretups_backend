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

package restassuredapi.pojo.addTCPRequestPojo;

import java.io.Serializable;

/**
 * @author avinash.kamthan
 * 
 */
public class TransferProfileProductReqVO implements Serializable {

    private String minBalance;
    private String maxBalance;
    private long minResidualBalanceAsLong;
    private long maxBalanceAsLong;
    private String productCode;
    private String productName;
    private String altBalance;
    private String allowedMaxPercentage;
    private long altBalanceLong;
    private int allowedMaxPercentageInt;
    private String currentBalance;
    private String productShortCode;
    private String c2sMinTxnAmt;
    private String c2sMaxTxnAmt;
    private long c2sMinTxnAmtAsLong;
    private long c2sMaxTxnAmtAsLong;
    
    
	public String getMinBalance() {
		return minBalance;
	}
	public void setMinBalance(String minBalance) {
		this.minBalance = minBalance;
	}
	public String getMaxBalance() {
		return maxBalance;
	}
	public void setMaxBalance(String maxBalance) {
		this.maxBalance = maxBalance;
	}
	public long getMinResidualBalanceAsLong() {
		return minResidualBalanceAsLong;
	}
	public void setMinResidualBalanceAsLong(long minResidualBalanceAsLong) {
		this.minResidualBalanceAsLong = minResidualBalanceAsLong;
	}
	public long getMaxBalanceAsLong() {
		return maxBalanceAsLong;
	}
	public void setMaxBalanceAsLong(long maxBalanceAsLong) {
		this.maxBalanceAsLong = maxBalanceAsLong;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getAltBalance() {
		return altBalance;
	}
	public void setAltBalance(String altBalance) {
		this.altBalance = altBalance;
	}
	public String getAllowedMaxPercentage() {
		return allowedMaxPercentage;
	}
	public void setAllowedMaxPercentage(String allowedMaxPercentage) {
		this.allowedMaxPercentage = allowedMaxPercentage;
	}
	public long getAltBalanceLong() {
		return altBalanceLong;
	}
	public void setAltBalanceLong(long altBalanceLong) {
		this.altBalanceLong = altBalanceLong;
	}
	public int getAllowedMaxPercentageInt() {
		return allowedMaxPercentageInt;
	}
	public void setAllowedMaxPercentageInt(int allowedMaxPercentageInt) {
		this.allowedMaxPercentageInt = allowedMaxPercentageInt;
	}
	public String getCurrentBalance() {
		return currentBalance;
	}
	public void setCurrentBalance(String currentBalance) {
		this.currentBalance = currentBalance;
	}
	public String getProductShortCode() {
		return productShortCode;
	}
	public void setProductShortCode(String productShortCode) {
		this.productShortCode = productShortCode;
	}
	public String getC2sMinTxnAmt() {
		return c2sMinTxnAmt;
	}
	public void setC2sMinTxnAmt(String c2sMinTxnAmt) {
		this.c2sMinTxnAmt = c2sMinTxnAmt;
	}
	public String getC2sMaxTxnAmt() {
		return c2sMaxTxnAmt;
	}
	public void setC2sMaxTxnAmt(String c2sMaxTxnAmt) {
		this.c2sMaxTxnAmt = c2sMaxTxnAmt;
	}
	public long getC2sMinTxnAmtAsLong() {
		return c2sMinTxnAmtAsLong;
	}
	public void setC2sMinTxnAmtAsLong(long c2sMinTxnAmtAsLong) {
		this.c2sMinTxnAmtAsLong = c2sMinTxnAmtAsLong;
	}
	public long getC2sMaxTxnAmtAsLong() {
		return c2sMaxTxnAmtAsLong;
	}
	public void setC2sMaxTxnAmtAsLong(long c2sMaxTxnAmtAsLong) {
		this.c2sMaxTxnAmtAsLong = c2sMaxTxnAmtAsLong;
	}

   
    

}
