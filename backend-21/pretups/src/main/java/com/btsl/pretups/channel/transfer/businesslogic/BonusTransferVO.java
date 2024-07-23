package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.Serializable;
import java.util.Date;

public class BonusTransferVO implements Serializable {
    private String _transferId = null;
    private String _accountId = null;
    private String _accountCode = null;
    private String _accountName = null;
    private String _accountType = null;
    private double _accountRate;
    private double _previousBalance;
    private Date _previousValidity;
    private Date _previousGrace;
    private double _balance;
    private long _validity;
    private long _grace;
    private double _postBalance;
    private Date _postValidity;
    private Date _postGrace;
    private Date _createdOn;
    private static final long serialVersionUID = 1L;
@Override
    public String toString() {
        final StringBuilder sbf = new StringBuilder();
        sbf.append("Transfer id =" + _transferId);
        sbf.append(",Accound id =" + _accountId);
        sbf.append(",Account code =" + _accountCode);
        sbf.append(",Account name =" + _accountName);
        sbf.append(",Account type =" + _accountType);
        sbf.append(",Account rate =" + _accountRate);
        sbf.append(",Previous balance =" + _previousBalance);
        sbf.append(",Previous validity =" + _previousValidity);
        sbf.append(",Previous grace =" + _previousGrace);
        sbf.append(",Balance =" + _balance);
        sbf.append(",Validity =" + _validity);
        sbf.append(",Grace =" + _grace);
        sbf.append(",Post balance =" + _postBalance);
        sbf.append(",Post validity =" + _postValidity);
        sbf.append(",Post grace" + _postGrace);
        return sbf.toString();
    }

    public String getTransferId() {
        return _transferId;
    }

    public void setTransferId(String id) {
        _transferId = id;
    }

    public String getAccountCode() {
        return _accountCode;
    }

    public void setAccountCode(String code) {
        _accountCode = code;
    }

    public String getAccountId() {
        return _accountId;
    }

    public void setAccountId(String id) {
        _accountId = id;
    }

    public String getAccountName() {
        return _accountName;
    }

    public void setAccountName(String name) {
        _accountName = name;
    }

    public double getAccountRate() {
        return _accountRate;
    }

    public void setAccountRate(double rate) {
        _accountRate = rate;
    }

    public String getAccountType() {
        return _accountType;
    }

    public void setAccountType(String type) {
        _accountType = type;
    }

    public double getBalance() {
        return _balance;
    }

    public void setBalance(double _balance) {
        this._balance = _balance;
    }

    public long getGrace() {
        return _grace;
    }

    public void setGrace(long _grace) {
        this._grace = _grace;
    }

    public double getPostBalance() {
        return _postBalance;
    }

    public void setPostBalance(double balance) {
        _postBalance = balance;
    }

    public Date getPostGrace() {
        return _postGrace;
    }

    public void setPostGrace(Date grace) {
        _postGrace = grace;
    }

    public Date getPostValidity() {
        return _postValidity;
    }

    public void setPostValidity(Date validity) {
        _postValidity = validity;
    }

    public double getPreviousBalance() {
        return _previousBalance;
    }

    public void setPreviousBalance(double balance) {
        _previousBalance = balance;
    }

    public Date getPreviousGrace() {
        return _previousGrace;
    }

    public void setPreviousGrace(Date grace) {
        _previousGrace = grace;
    }

    public Date getPreviousValidity() {
        return _previousValidity;
    }

    public void setPreviousValidity(Date validity) {
        _previousValidity = validity;
    }

    public long getValidity() {
        return _validity;
    }

    public void setValidity(long _validity) {
        this._validity = _validity;
    }

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date on) {
        _createdOn = on;
    }
}
