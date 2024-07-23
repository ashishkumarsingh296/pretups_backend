/**
 * @(#) ViewScheduleForm.java
 *      Copyright(c) 2006, Bharti Telesoft Ltd.
 *      All Rights Reserved
 * 
 *      ------------------------------------------------------------------------
 *      -------------------------
 *      Author Date History
 *      ------------------------------------------------------------------------
 *      -------------------------
 *      Ved Prakash 04/04/2006 Initial Creation
 * 
 */

package com.web.pretups.restrictedsubs.web;

import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class ViewScheduleForm extends RestrictedSubscriberForm {
   
	private static final long serialVersionUID = 1L;
	private ArrayList _viewScheduleList = null;
    private ArrayList _scheduleStatusList = null;
    private ArrayList _listForCancel = null;
    private HashMap _viewScheduleDetailList = null;
    private String _batchID = null;
    private String _scheduledDateAsString = null;
    private String _requestType = null;
    private String _scheduleStatus = null;
    private String _scheduleStatusDesc = null;
    private String _scheduleFromDate = null;
    private String _scheduleToDate = null;
    public static final Log log = LogFactory.getLog(ViewScheduleForm.class.getName());
    /*
     * variables for the single schedule view
     */
    private String _msisdn = null;
    private RestrictedSubscriberVO _restrictedSubscriberVO = null;

    // ends here
    public void flush() {
        super.flush();
        _viewScheduleList = null;
        _scheduleStatusList = null;
        _listForCancel = null;
        _viewScheduleDetailList = null;
        _batchID = null;
        _scheduledDateAsString = null;
        _requestType = null;
        _scheduleStatus = null;
        _scheduleStatusDesc = null;
        _scheduleFromDate = null;
        _scheduleToDate = null;
        _msisdn = null;
        _restrictedSubscriberVO = null;
    }

    /**
     * @return Returns the scheduleFromDate.
     */
    public String getScheduleFromDate() {
        return _scheduleFromDate;
    }

    /**
     * @param scheduleFromDate
     *            The scheduleFromDate to set.
     */
    public void setScheduleFromDate(String scheduleFromDate) {
        _scheduleFromDate = scheduleFromDate;
    }

    /**
     * @return Returns the scheduleToDate.
     */
    public String getScheduleToDate() {
        return _scheduleToDate;
    }

    /**
     * @param scheduleToDate
     *            The scheduleToDate to set.
     */
    public void setScheduleToDate(String scheduleToDate) {
        _scheduleToDate = scheduleToDate;
    }

    /**
     * @return Returns the scheduleStatus.
     */
    public String getScheduleStatus() {
        return _scheduleStatus;
    }

    /**
     * @param scheduleStatus
     *            The scheduleStatus to set.
     */
    public void setScheduleStatus(String scheduleStatus) {
        _scheduleStatus = scheduleStatus;
    }

    /**
     * @return Returns the scheduleStatusDesc.
     */
    public String getScheduleStatusDesc() {
        return _scheduleStatusDesc;
    }

    /**
     * @param scheduleStatusDesc
     *            The scheduleStatusDesc to set.
     */
    public void setScheduleStatusDesc(String scheduleStatusDesc) {
        _scheduleStatusDesc = scheduleStatusDesc;
    }

    /**
     * @return Returns the scheduleStatusList.
     */
    public ArrayList getScheduleStatusList() {
        return _scheduleStatusList;
    }

    /**
     * @param scheduleStatusList
     *            The scheduleStatusList to set.
     */
    public void setScheduleStatusList(ArrayList scheduleStatusList) {
        _scheduleStatusList = scheduleStatusList;
    }

    public String getMsisdn() {
        return _msisdn;
    }

    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    public RestrictedSubscriberVO getRestrictedSubscriberVO() {
        return _restrictedSubscriberVO;
    }

    public void setRestrictedSubscriberVO(RestrictedSubscriberVO restrictedSubscriberVO) {
        _restrictedSubscriberVO = restrictedSubscriberVO;
    }

    public ScheduleBatchDetailVO getIndexedScheduledBatchDetailVO(int i) {
        if (_viewScheduleList != null) {
            return (ScheduleBatchDetailVO) _viewScheduleList.get(i);
        }
        return null;
    }

    /**
     * @return Returns the listForCancel.
     */
    public ArrayList getListForCancel() {
        return _listForCancel;
    }

    /**
     * @param listForCancel
     *            The listForCancel to set.
     */
    public void setListForCancel(ArrayList listForCancel) {
        _listForCancel = listForCancel;
    }

    public int getSizeOfListForCancel() {
        if (_listForCancel != null) {
            return _listForCancel.size();
        }
        return 0;

    }

    /**
     * @return Returns the viewScheduleList.
     */
    public ArrayList getViewScheduleList() {
        return _viewScheduleList;
    }

    /**
     * @param viewScheduleList
     *            The viewScheduleList to set.
     */
    public void setViewScheduleList(ArrayList viewScheduleList) {
        _viewScheduleList = viewScheduleList;
    }

    public int getSizeOfViewScheduleList() {
        if (_viewScheduleList != null) {
            return _viewScheduleList.size();
        }
        return 0;
    }

    /**
     * @return Returns the viewScheduleDetailList.
     */
    public HashMap getViewScheduleDetailList() {
        return _viewScheduleDetailList;
    }

    /**
     * @param viewScheduleDetailList
     *            The viewScheduleDetailList to set.
     */
    public void setViewScheduleDetailList(HashMap viewScheduleDetailList) {
        _viewScheduleDetailList = viewScheduleDetailList;
    }

    public int getSizeOfViewScheduleDetailList() {
        if (_viewScheduleDetailList != null) {
            return _viewScheduleDetailList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the batchID.
     */
    public String getBatchID() {
        return _batchID;
    }

    /**
     * @param batchID
     *            The batchID to set.
     */
    public void setBatchID(String batchID) {
        _batchID = batchID;
    }

    /**
     * @return Returns the scheduledDateAsString.
     */
    public String getScheduledDateAsString() {
        return _scheduledDateAsString;
    }

    /**
     * @param scheduledDateAsString
     *            The scheduledDateAsString to set.
     */
    public void setScheduledDateAsString(String scheduledDateAsString) {
        _scheduledDateAsString = scheduledDateAsString;
    }

    /**
     * @return Returns the requestType.
     */
    public String getRequestType() {
        return _requestType;
    }

    /**
     * @param requestType
     *            The requestType to set.
     */
    public void setRequestType(String requestType) {
        _requestType = requestType;
    }

    /**
     * @param int i
     *        param ScheduleBatchMasterVO vo
     */

    public void setNewSubVOIndexedForCancel(int i, ScheduleBatchMasterVO vo) {
        _viewScheduleList.set(i, vo);
    }

    /**
     * @param int i
     * @return Returns the ScheduleBatchMasterVO.
     */
    public ScheduleBatchMasterVO getNewSubVOIndexedForCancel(int i) {
        if (_viewScheduleList != null) {
            return (ScheduleBatchMasterVO) _viewScheduleList.get(i);
        } else {
            return null;
        }
    }


    
    
    
}
