package com.btsl.common;

import java.io.Serializable;

public class VomsListValueVO extends ListValueVO implements Serializable, Comparable {

    private String msisdn;
    private String extTXNNumber;
    private String extTXNDate;
    private String extCode;
    private long quantity;
    private String remarks;
    private String loginId;
    private String userCategory;
    private String userGrade;
    // LMS Point AdjustMent
    private String userName;
    private String pointAction;
    private static final long serialVersionUID = 1L;
    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getExtTXNNumber() {
        return extTXNNumber;
    }

    public void setExtTXNNumber(String extTXNNumber) {
        this.extTXNNumber = extTXNNumber;
    }

    public String getExtTXNDate() {
        return extTXNDate;
    }

    public void setExtTXNDate(String extTXNDate) {
        this.extTXNDate = extTXNDate;
    }

    public String getExtCode() {
        return extCode;
    }

    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getLoginID() {
        return loginId;
    }

    public void setLoginID(String loginId) {
        this.loginId = loginId;
    }

    public String getUserCategogry() {
        return userCategory;
    }

    public void setUserCategory(String userCategory) {
        this.userCategory = userCategory;
    }

    public String getUserGrade() {
        return userGrade;
    }

    public void setUserGrade(String userGrade) {
        this.userGrade = userGrade;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPointAction() {
        return pointAction;
    }

    public void setPointAction(String pointAction) {
        this.pointAction = pointAction;
    }
} // end of class ListValueBean
