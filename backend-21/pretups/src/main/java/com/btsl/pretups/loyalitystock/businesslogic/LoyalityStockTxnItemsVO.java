/*
 * @# NetworkStockTxnItemsVO.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Sandeep Goel Aug 11, 2005 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.loyalitystock.businesslogic;

import java.io.Serializable;
import java.util.Date;

import com.btsl.pretups.product.businesslogic.ProductVO;
import com.btsl.pretups.util.PretupsBL;

public class LoyalityStockTxnItemsVO extends ProductVO implements Serializable {

    private int _sNo;
    private String _txnNo;
    private long _requiredQuantity;
    private long _approvedQuantity;
    private long _stock;
    private long _mrp;
    private long _amount;
    private String _amountStr;
    private String _approvedQuantityStr;
    // Added on 07/02/08 for addition of new date_time column in the table
    // NETWORK_STOCK_TRANS_ITEMS.
    private Date _dateTime = null;

    // Added for multiple wallet
    private long _focStock;
    private long _incentiveStock;
    private String _txnWallet;

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append("_sNo =" + _sNo);
        sbf.append(",_txnNo =" + _txnNo);
        sbf.append(",_requiredQuantity =" + _requiredQuantity);
        sbf.append(",_approvedQuantity =" + _approvedQuantity);
        sbf.append(",_stock =" + _stock);
        sbf.append(",_mrp =" + _mrp);
        sbf.append(",_amount =" + _amount);
        sbf.append(",_amountStr =" + _amountStr);
        sbf.append(",_approvedQuantityStr =" + _approvedQuantityStr);
        sbf.append(",_focStock =" + _focStock);
        sbf.append(",_incentiveStock =" + _incentiveStock);
        sbf.append(",_dateTime =" + _dateTime);
        sbf.append(",_txnWallet =" + _txnWallet);
        return sbf.toString();
    }

    public String getStockStr() {
        return PretupsBL.getDisplayAmount(_stock);
    }

    public String getFocStockStr() {
        return PretupsBL.getDisplayAmount(_focStock);
    }

    public String getIncentiveStockStr() {
        return PretupsBL.getDisplayAmount(_incentiveStock);
    }

    public long getAmount() {
        return _amount;
    }

    public void setAmount(long amount) {
        _amount = amount;
    }

    public long getApprovedQuantity() {
        return _approvedQuantity;
    }

    public void setApprovedQuantity(long approvedQuantity) {
        _approvedQuantity = approvedQuantity;
    }

    public long getMrp() {
        return _mrp;
    }

    public void setMrp(long mrp) {
        _mrp = mrp;
    }

    public long getRequiredQuantity() {
        return _requiredQuantity;
    }

    public void setRequiredQuantity(long requiredQuantity) {
        _requiredQuantity = requiredQuantity;
    }

    public int getSNo() {
        return _sNo;
    }

    public void setSNo(int no) {
        _sNo = no;
    }

    public long getStock() {
        return _stock;
    }

    public void setStock(long stock) {
        _stock = stock;
    }

    public String getTxnNo() {
        return _txnNo;
    }

    public void setTxnNo(String txnNo) {
        _txnNo = txnNo;
    }

    public String getAmountStr() {
        return _amountStr;
    }

    public void setAmountStr(String amountStr) {
        _amountStr = amountStr;
    }

    public String getApprovedQuantityStr() {
        return _approvedQuantityStr;
    }

    public void setApprovedQuantityStr(String approvedQuantityStr) {
        _approvedQuantityStr = approvedQuantityStr;
    }

    /**
     * @return Returns the dateTime.
     */
    public Date getDateTime() {
        return _dateTime;
    }

    /**
     * @param dateTime
     *            The dateTime to set.
     */
    public void setDateTime(Date dateTime) {
        this._dateTime = dateTime;
    }

    public long getFocStock() {
        return _focStock;
    }

    public void setFocStock(long focStock) {
        _focStock = focStock;
    }

    public long getIncentiveStock() {
        return _incentiveStock;
    }

    public void setIncentiveStock(long incentiveStock) {
        _incentiveStock = incentiveStock;
    }

    public String getTxnWallet() {
        return _txnWallet;
    }

    public void setTxnWallet(String txnWallet) {
        _txnWallet = txnWallet;
    }
}
