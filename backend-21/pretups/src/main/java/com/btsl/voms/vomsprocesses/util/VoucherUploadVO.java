package com.btsl.voms.vomsprocesses.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.pretups.user.businesslogic.ChannelUserVO;

/**
 * @(#)VoucherUploadVO.java
 *                          Copyright(c) 2006, Bharti Telesoft Ltd.
 *                          All Rights Reserved
 * 
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Gurjeet Bedi Jul 21, 2006 Initial Creation
 * 
 */

public class VoucherUploadVO implements Serializable {
    
	private static final long serialVersionUID = 1L;
	private String _fileName = null;
    private String _filePath = null;
    private int _noOfRecordsInFile = 0;
    private int _maxNoOfRecordsAllowed = 0;
    private String _productID = null;
    private ChannelUserVO _channelUserVO = null;
    private Date _currentDate = null;
    private String _fromSerialNo = null;
    private String _toSerialNo = null;
    private int _actualNoOfRecords = 0;
    private ArrayList _voucherArrayList = null;
    public static final String _MANUALPROCESSTYPE = "MANUAL";
    public static final String _AUTOPROCESSTYPE = "AUTO";
    private String _processType = _MANUALPROCESSTYPE; // Dont set if process
                                                      // type is manual else set
                                                      // AUTO
    private ArrayList _errorArrayList = null; // Added to save information about
                                              // errors in voucher file
    private String _mrp = null;
    private String _runningFromCron = null;
    private String _prfileId = null;
    private String netwrkID=null;
    public String getNetwrkID() {
		return netwrkID;
	}

	public void setNetwrkID(String netwrkID) {
		this.netwrkID = netwrkID;
	}

	public String getMrp() {
        return _mrp;
    }

    public void setMrp(String mrp) {
        _mrp = mrp;
    }

    public ChannelUserVO getChannelUserVO() {
        return _channelUserVO;
    }

    public void setChannelUserVO(ChannelUserVO channelUserVO) {
        _channelUserVO = channelUserVO;
    }

    public String getFileName() {
        return _fileName;
    }

    public void setFileName(String fileName) {
        _fileName = fileName;
    }

    public String getFilePath() {
        return _filePath;
    }

    public void setFilePath(String filePath) {
        _filePath = filePath;
    }

    public int getNoOfRecordsInFile() {
        return _noOfRecordsInFile;
    }

    public void setNoOfRecordsInFile(int noOfRecords) {
        _noOfRecordsInFile = noOfRecords;
    }

    public String getProductID() {
        return _productID;
    }

    public void setProductID(String productID) {
        _productID = productID;
    }

    public int getMaxNoOfRecordsAllowed() {
        return _maxNoOfRecordsAllowed;
    }

    public void setMaxNoOfRecordsAllowed(int maxNoOfRecordsAllowed) {
        _maxNoOfRecordsAllowed = maxNoOfRecordsAllowed;
    }

    public Date getCurrentDate() {
        return _currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        _currentDate = currentDate;
    }

    public String getFromSerialNo() {
        return _fromSerialNo;
    }

    public void setFromSerialNo(String fromSerialNo) {
        _fromSerialNo = fromSerialNo;
    }

    public String getToSerialNo() {
        return _toSerialNo;
    }

    public void setToSerialNo(String toSerialNo) {
        _toSerialNo = toSerialNo;
    }

    public ArrayList getVoucherArrayList() {
        return _voucherArrayList;
    }

    public void setVoucherArrayList(ArrayList voucherArrayList) {
        _voucherArrayList = voucherArrayList;
    }

    public int getActualNoOfRecords() {
        return _actualNoOfRecords;
    }

    public void setActualNoOfRecords(int actualNoOfRecords) {
        _actualNoOfRecords = actualNoOfRecords;
    }

    /**
     * @return Returns the processType.
     */
    public String getProcessType() {
        return _processType;
    }

    /**
     * @param processType
     *            The processType to set.
     */
    public void setProcessType(String processType) {
        _processType = processType;
    }

    // added by manisha for saving errors in voucher file (date 10/12/2007)
    /**
     * @return Returns the errorArrayList.
     */
    public ArrayList getErrorArrayList() {
        return _errorArrayList;
    }

    /**
     * @param errorArrayList
     *            The errorArrayList to set.
     */
    public void setErrorArrayList(ArrayList errorArrayList) {
        _errorArrayList = errorArrayList;
    }

    public String getRunningFromCron() {
        return _runningFromCron;
    }

    public void setRunningFromCron(String fromCron) {
        _runningFromCron = fromCron;
    }

    public String getPrfileId() {
        return _prfileId;
    }

    public void setPrfileId(String id) {
        _prfileId = id;
    }
}
