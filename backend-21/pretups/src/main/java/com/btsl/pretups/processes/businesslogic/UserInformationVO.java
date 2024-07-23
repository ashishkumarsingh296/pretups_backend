package com.btsl.pretups.processes.businesslogic;

import java.io.Serializable;

public class UserInformationVO implements Serializable {
    private String _msisdn;
    private String _userid;
    private String _productCode;
    private long _openingBalance;
    private long _c2CTxn;
    private long _topupAmt;
    private long _closingBal;
    private long _billing;
    private long _billingMTD;
    private long _billingLMTD;

    /*
     * public String getAmountDate() {
     * SimpleDateFormat dateFormat = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
     * Calendar cal = Calendar.getInstance();
     * cal.add(Calendar.DATE,-1);
     * amountDate = dateFormat.format(cal.getTime());
     * //amountDate = cal.getTime();
     * return amountDate;
     * }
     */

    public String getMsisdn() {
        return _msisdn;
    }

    public void setMsisdn(String _msisdn) {
        this._msisdn = _msisdn;
    }

    public String getUserid() {
        return _userid;
    }

    public void setUserid(String _userid) {
        this._userid = _userid;
    }

    public long getOpeningBalance() {
        return _openingBalance;
    }

    public void setOpeningBalance(long balance) {
        _openingBalance = balance;
    }

    public long getC2CTxn() {
        return _c2CTxn;
    }

    public void setC2CTxn(long txn) {
        _c2CTxn = txn;
    }

    public long getTopupAmt() {
        return _topupAmt;
    }

    public void setTopupAmt(long amt) {
        _topupAmt = amt;
    }

    public long getClosingBal() {
        return _closingBal;
    }

    public void setClosingBal(long bal) {
        _closingBal = bal;
    }

    public long getBilling() {
        return _billing;
    }

    public void setBilling(long _billing) {
        this._billing = _billing;
    }

    public long getBillingMTD() {
        return _billingMTD;
    }

    public void setBillingMTD(long _billingmtd) {
        _billingMTD = _billingmtd;
    }

    public long getBillingLMTD() {
        return _billingLMTD;
    }

    public void setBillingLMTD(long _billinglmtd) {
        _billingLMTD = _billinglmtd;
    }

    public String getProductCode() {
        return _productCode;
    }

    public void setProductCode(String code) {
        _productCode = code;
    }

    /*
     * public static void main(String args[])
     * {
     * PushSmsVO p = new PushSmsVO();
     * p.setOpeningBalance(100.00);
     * p.setC2CTxn(90);
     * p.setTopupAmt(50.00);
     * p.setBilling(23.00);
     * p.setBillingMTD(22.00);
     * p.setBillingLMTD(21.00);
     * System.out.println(p.getAmountDate());
     * System.out.println(p.toString());
     * }
     */

    public String toString() {
        StringBuffer sbf = new StringBuffer();
        // sbf.append("amountDate ="+ this.getAmountDate());
        sbf.append("[ MSISDN =" + _msisdn + "]");
        sbf.append("[ openingBalance =" + _openingBalance + "]");
        sbf.append("[ c2CTxn =" + _c2CTxn + "]");
        sbf.append("[ topupAmt =" + _topupAmt + "]");
        sbf.append("[ closingBal =" + _closingBal + "]");
        sbf.append("[ billing =" + _billing + "]");
        sbf.append("[ billingMTD =" + _billingLMTD + "]");
        sbf.append("[ billingLMTD =" + _billingLMTD + "]");
        return sbf.toString();
    }

}