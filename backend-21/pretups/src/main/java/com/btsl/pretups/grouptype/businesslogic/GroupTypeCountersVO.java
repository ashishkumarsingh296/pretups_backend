package com.btsl.pretups.grouptype.businesslogic;

import java.io.Serializable;

/**
 * @(#)GroupTypeCountersVO.java
 *                              Copyright(c) 2006, Bharti Telesoft Ltd.
 *                              All Rights Reserved
 *                              Travelling object for channel user
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Ankit Zindal 11/07/2006 Initial Creation
 *                              ------------------------------------------------
 *                              ------------------------------------------------
 *                              This class will hold the running group type
 *                              counters of user
 * 
 */

public class GroupTypeCountersVO implements Serializable {

    private int _year;
    private int _month;
    private int _day;
    private String _userID;
    private String _msisdn;
    private String _groupType;
    private String _type;
    private long _counters;
    private String _module;
    private static final long serialVersionUID = 1L;
    @Override
    public String toString() {
        StringBuilder sbf = new StringBuilder();
        sbf.append(super.toString());
        sbf.append("_year =" + _year);
        sbf.append(",_month =" + _month);
        sbf.append(",_day =" + _day);
        sbf.append(",_userID=" + _userID);
        sbf.append(",_msisdn =" + _msisdn);
        sbf.append(",_groupType=" + _groupType);
        sbf.append(",_type =" + _type);
        sbf.append(",_counters =" + _counters);
        sbf.append(",_module =" + _module);
        return sbf.toString();
    }

    /**
     * @return Returns the counters.
     */
    public long getCounters() {
        return _counters;
    }

    /**
     * @param counters
     *            The counters to set.
     */
    public void setCounters(long counters) {
        _counters = counters;
    }

    /**
     * @return Returns the day.
     */
    public int getDay() {
        return _day;
    }

    /**
     * @param day
     *            The day to set.
     */
    public void setDay(int day) {
        _day = day;
    }

    /**
     * @return Returns the groupType.
     */
    public String getGroupType() {
        return _groupType;
    }

    /**
     * @param groupType
     *            The groupType to set.
     */
    public void setGroupType(String groupType) {
        _groupType = groupType;
    }

    /**
     * @return Returns the month.
     */
    public int getMonth() {
        return _month;
    }

    /**
     * @param month
     *            The month to set.
     */
    public void setMonth(int month) {
        _month = month;
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
        _msisdn = msisdn;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return _type;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setType(String type) {
        _type = type;
    }

    /**
     * @return Returns the userID.
     */
    public String getUserID() {
        return _userID;
    }

    /**
     * @param userID
     *            The userID to set.
     */
    public void setUserID(String userID) {
        _userID = userID;
    }

    /**
     * @return Returns the year.
     */
    public int getYear() {
        return _year;
    }

    /**
     * @param year
     *            The year to set.
     */
    public void setYear(int year) {
        _year = year;
    }

    /**
     * @return Returns the module.
     */
    public String getModule() {
        return _module;
    }

    /**
     * @param module
     *            The module to set.
     */
    public void setModule(String module) {
        _module = module;
    }
}
