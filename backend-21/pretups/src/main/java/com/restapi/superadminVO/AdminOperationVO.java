package com.restapi.superadminVO;

import java.util.Date;

public class AdminOperationVO {
    private Date _date;// [Date & Time ]
    private String _source = null;// Source [ServiceKeyword] - Menu option name
    private String _operation = null;// Operation [Add] - Add/ Modify, Delete,
                                     // suspend, resume depending on the
                                     // operation performed.
    private String _loginID = null;// login id [sanjay] - logged in users login
                                   // id
    private String _userID = null;// Created By [U010001] - logged in users user
                                  // id
    private String _categoryCode = null;// User Category [CCE] - Logged in users
                                        // category
    private String _networkCode = null;// Network [Network] - looged in users
                                       // network code
    private String _msisdn = null;// Mobile Number [msisdn] - logged in users
                                  // mobile number
    private String _info = null;// other information related with the operation

    
    /**
     * @return Returns the categoryCode.
     */
    public String getCategoryCode() {
        return _categoryCode;
    }

    /**
     * @param categoryCode
     *            The categoryCode to set.
     */
    public void setCategoryCode(String categoryCode) {
        if (categoryCode != null) {
            this._categoryCode = categoryCode.trim();
        }
    }

    /**
     * @return Returns the createdBy.
     */
    public String getUserID() {
        return _userID;
    }

    /**
     * @param createdBy
     *            The createdBy to set.
     */
    public void setUserID(String createdBy) {
        if (createdBy != null) {
            this._userID = createdBy.trim();
        }
    }

    /**
     * @return Returns the date.
     */
    public Date getDate() {
        return _date;
    }

    /**
     * @param date
     *            The date to set.
     */
    public void setDate(Date date) {
        this._date = date;
    }

    /**
     * @return Returns the loginID.
     */
    public String getLoginID() {
        return _loginID;
    }

    /**
     * @param loginID
     *            The loginID to set.
     */
    public void setLoginID(String loginID) {
        if (loginID != null) {
            this._loginID = loginID.trim();
        }
    }

    /**
     * @return Returns the msisdn.
     */
    public String getMsisdn() {
        return _msisdn;
    }

    /**
     * @param msisdn
     *            The msisdn to set.
     */
    public void setMsisdn(String msisdn) {
        if (msisdn != null) {
            this._msisdn = msisdn.trim();
        }
    }

    /**
     * @return Returns the networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param networkCode
     *            The networkCode to set.
     */
    public void setNetworkCode(String networkCode) {
        if (networkCode != null) {
            this._networkCode = networkCode.trim();
        }
    }

    /**
     * @return Returns the operation.
     */
    public String getOperation() {
        return _operation;
    }

    /**
     * @param operation
     *            The operation to set.
     */
    public void setOperation(String operation) {
        if (operation != null) {
            this._operation = operation.trim();
        }
    }

    /**
     * @return Returns the source.
     */
    public String getSource() {
        return _source;
    }

    /**
     * @param source
     *            The source to set.
     */
    public void setSource(String source) {
        if (source != null) {
            _source = source.trim();
        }
    }

    /**
     * @return Returns the info.
     */
    public String getInfo() {
        return _info;
    }

    /**
     * @param info
     *            The info to set.
     */
    public void setInfo(String info) {
        if (info != null) {
            _info = info.trim();
        }
    }
}
