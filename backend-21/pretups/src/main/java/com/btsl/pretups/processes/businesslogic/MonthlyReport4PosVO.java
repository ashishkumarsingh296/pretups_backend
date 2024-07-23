package com.btsl.pretups.processes.businesslogic;

/**
 * @(#)MonthlyReport4PosVO.java
 *                              Copyright(c) 2014, Comviva technologies Ltd.
 *                              All Rights Reserved
 * 
 *                              ------------------------------------------------
 *                              -------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              -------------------------------
 *                              Diwakar Jan 08 2014 Initial Creation
 *                              This VO class will be used store the details
 *                              related MonthlyReport4PosDAO.
 * 
 */
import java.io.Serializable;

import com.btsl.util.BTSLUtil;

public class MonthlyReport4PosVO implements Serializable, Comparable {

    private static final long serialVersionUID = -8824134008423321350L;
    private String _eventMonth;
    private String _resion;
    private String _area;
    private String _retailerName;
    private long _posMsisdn;
    private long _activeDays;
    private long _amount;
    private long _count;
    private double _dailyAvgTxnAmount;
    private double _dailyAvgTxnCount;
    private String _classType;

    /**
     * @return the _eventMonth
     */
    public String get_eventMonth() {
        return _eventMonth;
    }

    /**
     * @param month
     *            the _eventMonth to set
     */
    public void set_eventMonth(String month) {
        _eventMonth = month;
    }

    /**
     * @return the _resion
     */
    public String get_resion() {
        return _resion;
    }

    /**
     * @param _resion
     *            the _resion to set
     */
    public void set_resion(String _resion) {
        this._resion = _resion;
    }

    /**
     * @return the _area
     */
    public String get_area() {
        return _area;
    }

    /**
     * @param _area
     *            the _area to set
     */
    public void set_area(String _area) {
        this._area = _area;
    }

    /**
     * @return the _posMsisdn
     */
    public long get_posMsisdn() {
        return _posMsisdn;
    }

    /**
     * @param msisdn
     *            the _posMsisdn to set
     */
    public void set_posMsisdn(long msisdn) {
        _posMsisdn = msisdn;
    }

    /**
     * @param type
     *            the _classType to set
     */
    public void set_classType(String type) {
        _classType = type;
    }

    public int compareTo(Object arg0) {
        MonthlyReport4PosVO obj = (MonthlyReport4PosVO) arg0;
        if (BTSLUtil.isNullString(String.valueOf(this._posMsisdn)))
            return 1;
        else if (BTSLUtil.isNullString(String.valueOf(obj._posMsisdn)))
            return -1;
        else if (this._posMsisdn == obj._posMsisdn)
            return 1;
        else
            return -1;
    }

    public long get_activeDays() {
        return _activeDays;
    }

    public void set_activeDays(long days) {
        _activeDays = days;
    }

    public long get_amount() {
        return _amount;
    }

    public void set_amount(long _amount) {
        this._amount = _amount;
    }

    public long get_count() {
        return _count;
    }

    public void set_count(long _count) {
        this._count = _count;
    }

    public double get_dailyAvgTxnAmount() {
        return _dailyAvgTxnAmount;
    }

    public void set_dailyAvgTxnAmount(double avgTxnAmount) {
        _dailyAvgTxnAmount = avgTxnAmount;
    }

    public double get_dailyAvgTxnCount() {
        return _dailyAvgTxnCount;
    }

    public void set_dailyAvgTxnCount(double avgTxnCount) {
        _dailyAvgTxnCount = avgTxnCount;
    }

    public String get_classType() {
        return _classType;
    }

    public String get_retailerName() {
        return _retailerName;
    }

    public void set_retailerName(String name) {
        _retailerName = name;
    }

}
