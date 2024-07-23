package com.btsl.pretups.receiver;

import java.io.Serializable;
import java.util.Date;

/*
 * AdditionalInfoVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Pankaj K Namdev 20/02/2009 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2009 Bharti Telesoft Ltd.
 * This class is used to collect the Request parameter details that are received
 * for processing
 */

/**
 * @author pankaj.namdev
 * 
 */
public class AdditionalInfoVO implements Serializable {

    private long _topupAmount;
    private String _currency = null;
    private String _balance = null;
    private Date _validityDate = null;
    private Date _graceDate = null;

    public String getBalance() {
        return _balance;
    }

    public void setBalance(String balance) {
        _balance = balance;
    }

    public String getCurrency() {
        return _currency;
    }

    public void setCurrency(String currency) {
        _currency = currency;
    }

    public Date getGraceDate() {
        return _graceDate;
    }

    public void setGraceDate(Date graceDate) {
        _graceDate = graceDate;
    }

    public long getTopupAmount() {
        return _topupAmount;
    }

    public void setTopupAmount(long topupAmount) {
        _topupAmount = topupAmount;
    }

    public Date getValidityDate() {
        return _validityDate;
    }

    public void setValidityDate(Date validityDate) {
        _validityDate = validityDate;
    }

}
