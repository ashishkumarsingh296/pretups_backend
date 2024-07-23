package com.web.pretups.restrictedsubs.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
/**
 * @# RestrictedTopUpForm.java
 * 
 *    Created by Created on History
 *    --------------------------------------------------------------------------
 *    ------
 *    Sandeep Goel Mar 29, 2006 Initial creation
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2006 Bharti Telesoft Ltd.
 */
public class RestrictedTopUpForm extends RestrictedSubscriberForm {
    private Log _log = LogFactory.getLog(RestrictedTopUpForm.class.getName());
    //private FormFile _fileName = null; // this variable take the file from the
                                       // jsp page.
    private String _fileNameStr = null; // to store the file name.

    private String _noOfRecords = null;
    private String _scheduleDate = null;
    private ArrayList _scheduleList = null;
    private String _batchID = null;
    private String _requestFor = null;
    private HashMap _downLoadDataMap = null;

    private Date _createdOn = null;
    private String _createdBy = null;
    private Date _modifiedOn = null;
    private String _modifiedBy = null;
    private ArrayList _serviceTypeList = new ArrayList<>();
    private String _serviceTypeCode = null;
    private String _serviceTypeDesc = null;

    private String _invalidDataStr = null;
    private ArrayList _errorLogList = null;
    private String _processedRecs = null;

    private String _downLoadBatchID = null;

    /* varible for cancel schedule starts */
    private String _mobileNumbers;
    private ArrayList _scheduleMasterVOList;
    private String _scheduleStatus;
    private ArrayList _deleteList;
    private ArrayList _scheduleDetailList;
    private ArrayList _tempScheduleList;
    private ScheduleBatchMasterVO _scheduleBatchMasterVO = null;
    /* varible for cancel schedule ends */
    private String _fileType;// for the batch type that is uploaded

	// Schedule Now Recharge
	private String _scheduleNow=null;
	private String _scheduleDateNow=null;
	private String _scheduleNowChkBox=null;
	private List<String> frequencyList;
	private String frequency;
	private String iteration;
	
	


	public String getFrequency() {
		return frequency;
	}


	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}


	public List<String> getFrequencyList() {
		return frequencyList;
	}


	public void setFrequencyList(List<String> frequency) {
		this.frequencyList = frequency;
	}


	public String getIteration() {
		return iteration;
	}


	public void setIteration(String iteration) {
		this.iteration = iteration;
	}


	/**
	 * Constructor for RestrictedTopUpForm.
	 */
	public RestrictedTopUpForm()
	{
		super();
	}
	
	
	/**
	 * Flush some contents of the form bean
	 */
	public void semiFlush()
	{
		//_fileName=null;
		_fileNameStr=null;
		_noOfRecords=null;
		_scheduleNow=null;
		_scheduleDate=null;
		_scheduleList=null;
		_mobileNumbers=null;
		_scheduleDateNow=null;
		
	}
	
	/**
	 * Flush some contents of the form bean
	 */
	public void flush()
	{
		super.flush();
		//_fileName=null;
		_fileNameStr=null;
		_noOfRecords=null;
		_scheduleNow=null;
		_scheduleDate=null;
		_scheduleList=null;
		_batchID=null;
		_requestFor=null;
		_downLoadDataMap=null;
		_createdOn=null; 
		_createdBy=null; 
		_modifiedOn=null; 
		_modifiedBy=null;
		_serviceTypeList=null;
		_serviceTypeCode=null;
		_serviceTypeDesc=null;
		_invalidDataStr=null;
		_errorLogList=null;
		_processedRecs=null;
		_downLoadBatchID=null;
		_mobileNumbers=null;
		_scheduleMasterVOList=null;
		_scheduleStatus=null;
		_deleteList=null;
		_scheduleDetailList=null;
		_scheduleDateNow=null;
	}

    public String getFileNameStr() {
        return _fileNameStr;
    }

    public void setFileNameStr(String fileNameStr) {
        _fileNameStr = fileNameStr;
    }

    public String getNoOfRecords() {
        return _noOfRecords;
    }

    public void setNoOfRecords(String noOfRecords) {
        _noOfRecords = noOfRecords;
    }

    public String getScheduleDate() {
        return _scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        _scheduleDate = scheduleDate;
    }

    public String getBatchID() {
        return _batchID;
    }

    public void setBatchID(String batchID) {
        _batchID = batchID;
    }

    public ArrayList getScheduleList() {
        return _scheduleList;
    }

    public int getScheduleListSize() {
        if (_scheduleList != null) {
            return _scheduleList.size();
        }
        return 0;
    }

    public void setScheduleList(ArrayList scheduleList) {
        _scheduleList = scheduleList;
    }

    public String getRequestFor() {
        return _requestFor;
    }

    public void setRequestFor(String requestFor) {
        _requestFor = requestFor;
    }

    public HashMap getDownLoadDataMap() {
        return _downLoadDataMap;
    }

    public void setDownLoadDataMap(HashMap downLoadDataMap) {
        _downLoadDataMap = downLoadDataMap;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    public String getServiceTypeCode() {
        return _serviceTypeCode;
    }

    public void setServiceTypeCode(String serviceTypeCode) {
        _serviceTypeCode = serviceTypeCode;
    }

    public String getServiceTypeDesc() {
        return _serviceTypeDesc;
    }

    public void setServiceTypeDesc(String serviceTypeDesc) {
        _serviceTypeDesc = serviceTypeDesc;
    }

    public ArrayList getServiceTypeList() {
        return _serviceTypeList;
    }

    public int getServiceTypeListSize() {
        if (_serviceTypeList != null) {
            return _serviceTypeList.size();
        }
        return 0;
    }

    public void setServiceTypeList(ArrayList serviceTypeList) {
        _serviceTypeList = serviceTypeList;
    }

    public String getInvalidDataStr() {
        return _invalidDataStr;
    }

    public void setInvalidDataStr(String invalidDataStr) {
        _invalidDataStr = invalidDataStr;
    }

    public ArrayList getErrorLogList() {
        return _errorLogList;
    }

    public void setErrorLogList(ArrayList errorLogList) {
        _errorLogList = errorLogList;
    }

    public String getProcessedRecs() {
        return _processedRecs;
    }

    public void setProcessedRecs(String processedRecs) {
        _processedRecs = processedRecs;
    }

    public String getDownLoadBatchID() {
        return _downLoadBatchID;
    }

    public void setDownLoadBatchID(String downLoadBatchID) {
        _downLoadBatchID = downLoadBatchID;
    }

    public String getMobileNumbers() {
        return _mobileNumbers;
    }

    public void setMobileNumbers(String mobileNumbers) {
        _mobileNumbers = mobileNumbers;
    }

    public ArrayList getScheduleMasterVOList() {
        return _scheduleMasterVOList;
    }

    public void setScheduleMasterVOList(ArrayList rsltFromDAOList) {
        _scheduleMasterVOList = rsltFromDAOList;
    }

    public String getScheduleStatus() {
        return _scheduleStatus;
    }

    public void setScheduleStatus(String scheduleStatus) {
        _scheduleStatus = scheduleStatus;
    }

    public ScheduleBatchDetailVO getSubVOIndexed(int i) {
        if (_scheduleMasterVOList != null) {
            return (ScheduleBatchDetailVO) _scheduleMasterVOList.get(i);
        }
        return null;
    }

    public int getListSize() {
        if (_scheduleMasterVOList == null) {
            return 0;
        }
        return _scheduleMasterVOList.size();
    }

    /**
     * @return Returns the deleteList.
     */
    public ArrayList getDeleteList() {
        return _deleteList;
    }

    /**
     * @param deleteList
     *            The deleteList to set.
     */
    public void setDeleteList(ArrayList deleteList) {
        _deleteList = deleteList;
    }

    /**
     * @return Returns the scheduleDetailList.
     */
    public ArrayList getScheduleDetailList() {
        return _scheduleDetailList;
    }

    /**
     * @param scheduleDetailList
     *            The scheduleDetailList to set.
     */
    public void setScheduleDetailList(ArrayList scheduleDetailList) {
        _scheduleDetailList = scheduleDetailList;
    }

    public int getSizeOfScheduleDetailList() {
        if (_scheduleDetailList != null) {
            return _scheduleDetailList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the scheduleBatchMasterVO.
     */
    public ScheduleBatchMasterVO getScheduleBatchMasterVO() {
        return _scheduleBatchMasterVO;
    }

    /**
     * @param scheduleBatchMasterVO
     *            The scheduleBatchMasterVO to set.
     */
    public void setScheduleBatchMasterVO(ScheduleBatchMasterVO scheduleBatchMasterVO) {
        _scheduleBatchMasterVO = scheduleBatchMasterVO;
    }

    /**
     * @return Returns the tempScheduleList.
     */
    public ArrayList getTempScheduleList() {
        return _tempScheduleList;
    }

    /**
     * @param tempScheduleList
     *            The tempScheduleList to set.
     */
    public void setTempScheduleList(ArrayList tempScheduleList) {
        _tempScheduleList = tempScheduleList;
    }

    public int getSizeOfTempScheduleList() {
        if (_tempScheduleList != null) {
            return _tempScheduleList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the fileType.
     */
    public String getFileType() {
        return _fileType;
    }

    /**
     * @param fileType
     *            The fileType to set.
     */
    public void setFileType(String fileType) {
        _fileType = fileType;
    }

//Schedule Now Recharge
	public String getScheduleNow() {
		return _scheduleNow;
	}


	public void setScheduleNow(String now) {
		_scheduleNow = now;
	}


	public String getScheduleDateNow() {
		return _scheduleDateNow;
	}


	public void setScheduleDateNow(String dateNow) {
		_scheduleDateNow = dateNow;
	}
	
	public String getScheduleNowChkBox() {
		return _scheduleNowChkBox;
	}


	public void setScheduleNowChkBox(String dateNow) {
		_scheduleNowChkBox = dateNow;
	}
//Schedule Now Recharge	
}
