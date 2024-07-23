package com.selftopup.pretups.p2p.query.businesslogic;

/*
 * #P2pQueryHistoryVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * july 28, 2005 ved prakash sharma Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import com.selftopup.util.BTSLUtil;

public class P2pQueryHistoryVO implements Serializable {

    private String _subscriberMsisdn;
    private String _fromDate;
    private String _toDate;
    private Object _queryhistoryVO;
    private Date _modified_on;

    private String _status;

    private ArrayList subscriberList;

    /**
     * @return Returns the subscriberList.
     */
    public ArrayList getSubscriberList() {
        return subscriberList;
    }

    /**
     * @param subscriberList
     *            The subscriberList to set.
     */
    public void setSubscriberList(ArrayList subscriberList) {
        this.subscriberList = subscriberList;
    }

    /**
     * @return Returns the modified_on.
     */
    public Date getModified_on() {
        return _modified_on;
    }

    public String getModified_onAsString() throws ParseException {
        return BTSLUtil.getDateStringFromDate(_modified_on);
    }

    public String getModified_onDateTimeAsString() throws ParseException {
        return BTSLUtil.getDateTimeStringFromDate(_modified_on);
    }

    /**
     * @param modified_on
     *            The modified_on to set.
     */
    public void setModified_on(Date modified_on) {
        _modified_on = modified_on;
    }

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(String status) {
        _status = status;
    }

    /**
     * @return Returns the queryhistoryVO.
     */
    public Object getQueryhistoryVO() {
        return _queryhistoryVO;
    }

    /**
     * @param queryhistoryVO
     *            The queryhistoryVO to set.
     */
    public void setQueryhistoryVO(Object queryhistoryVO) {
        _queryhistoryVO = queryhistoryVO;
    }

    /**
	 * 
	 */
    public P2pQueryHistoryVO() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @return Returns the fromDate.
     */
    public String getFromDate() {
        return _fromDate;
    }

    /**
     * @param fromDate
     *            The fromDate to set.
     */
    public void setFromDate(String fromDate) {
        _fromDate = fromDate;
    }

    /**
     * @return Returns the subscriberMsisdn.
     */
    public String getSubscriberMsisdn() {
        return _subscriberMsisdn;
    }

    /**
     * @param subscriberMsisdn
     *            The subscriberMsisdn to set.
     */
    public void setSubscriberMsisdn(String subscriberMsisdn) {
        _subscriberMsisdn = subscriberMsisdn;
    }

    /**
     * @return Returns the toDate.
     */
    public String getToDate() {
        return _toDate;
    }

    /**
     * @param toDate
     *            The toDate to set.
     */
    public void setToDate(String toDate) {
        _toDate = toDate;
    }
}
