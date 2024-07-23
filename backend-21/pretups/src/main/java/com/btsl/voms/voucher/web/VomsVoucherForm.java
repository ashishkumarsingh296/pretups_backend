/*
 * Created on Jun 21, 2006
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.voms.voucher.web;

import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomsprocesses.util.VoucherFileUploaderUtil;
import com.ibm.icu.util.Calendar;

/**
 * @(#)VomsVoucherForm.java
 *                          Copyright(c) 2006, Bharti Telesoft Ltd.
 *                          All Rights Reserved
 * 
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          vikas.yadav 01/07/2006 Initial Creation
 * 
 * 
 *                          This class is used for Change Voucher
 *                          Status(Genrated as well as Others) of EVD
 */
public class VomsVoucherForm  {

    private Log _log = LogFactory.getLog(this.getClass().getName());

    private String _fromSerial = null;
    private String _toSerial = null;
    private String _totalNoOfVouchStr = null;
    private String _mrpStr = null;
    private ArrayList _productList = null;
    private String _productID = null;
    private ArrayList _statusList = null;
    private String _voucherStatus = null;
    private String _createdBy = null;
    private String _locationCode = null;
    private String _underProcessExpiry = null;
    private String _offPeakHoursRange = null;
    private long _maxErrorAllowed = 0;
    private String _ProductIDName = null;
    private String _VoucherStatusName = null;
    private double _mrp = 0;
    private long _totalNoOfVouch = 0;
    private long _serialToNos = 0;
    private long _serialFromNos = 0;
    private int _processScreen = 0;

    // Add by Anjali
    private String _type = null;
    private ArrayList _typeList = null;
    private String _typeDesc = null;
    private int _typeListSize = 0;
    private String _voucherType = null;
    private ArrayList<VomsCategoryVO> _voucherTypeList;
    private String _voucherTypeDesc = null;

    // End

    /**
     * Clears all the instance variables
     */
    public void flush() {
        _fromSerial = null;
        _toSerial = null;
        _totalNoOfVouchStr = null;
        _mrpStr = null;
        _productList = null;
        _productID = null;
        _statusList = null;
        _voucherStatus = null;
        _createdBy = null;
        _locationCode = null;
        _underProcessExpiry = null;
        _offPeakHoursRange = null;
        _maxErrorAllowed = 0;
        _processScreen = 0;

        // Add by Anjali

        _type = null;
        _typeDesc = null;
        _typeList = null;

        // End
        _voucherType=null;
	    _voucherTypeDesc=null;

    }

    public void semiFlush() {
        _fromSerial = null;
        _toSerial = null;
        _totalNoOfVouchStr = null;
        _mrpStr = null;
        _productList = null;

    }


    /**
     * @return Returns the createdBy.
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * @param createdBy
     *            The createdBy to set.
     */
    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    /**
     * @return Returns the fromSerial.
     */
    public String getFromSerial() {
        return _fromSerial;
    }

    /**
     * @param fromSerial
     *            The fromSerial to set.
     */
    public void setFromSerial(String fromSerial) {
        _fromSerial = fromSerial;
    }

    /**
     * @return Returns the locationCode.
     */
    public String getLocationCode() {
        return _locationCode;
    }

    /**
     * @param locationCode
     *            The locationCode to set.
     */
    public void setLocationCode(String locationCode) {
        _locationCode = locationCode;
    }

    /**
     * @return Returns the log.
     */
    public Log getLog() {
        return _log;
    }

    /**
     * @param log
     *            The log to set.
     */
    public void setLog(Log log) {
        _log = log;
    }

    /**
     * @return Returns the maxErrorAllowed.
     */
    public long getMaxErrorAllowed() {
        return _maxErrorAllowed;
    }

    /**
     * @param maxErrorAllowed
     *            The maxErrorAllowed to set.
     */
    public void setMaxErrorAllowed(long maxErrorAllowed) {
        _maxErrorAllowed = maxErrorAllowed;
    }

    /**
     * @return Returns the mrpStr.
     */
    public String getMrpStr() {
        return _mrpStr;
    }

    /**
     * @param mrpStr
     *            The mrpStr to set.
     */
    public void setMrpStr(String mrpStr) {
        _mrpStr = mrpStr;
    }

    /**
     * @return Returns the offPeakHoursRange.
     */
    public String getOffPeakHoursRange() {
        return _offPeakHoursRange;
    }

    /**
     * @param offPeakHoursRange
     *            The offPeakHoursRange to set.
     */
    public void setOffPeakHoursRange(String offPeakHoursRange) {
        _offPeakHoursRange = offPeakHoursRange;
    }

    /**
     * @return Returns the productList.
     */
    public ArrayList getProductList() {
        return _productList;
    }

    /**
     * @param productList
     *            The productList to set.
     */
    public void setProductList(ArrayList productList) {
        _productList = productList;
    }

    public int getSizeOfProductList() {
        if (_productList != null) {
            return _productList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the statusList.
     */
    public ArrayList getStatusList() {
        return _statusList;
    }

    /**
     * @param statusList
     *            The statusList to set.
     */
    public void setStatusList(ArrayList statusList) {
        _statusList = statusList;
    }

    /**
     * @return Returns the toSerial.
     */
    public String getToSerial() {
        return _toSerial;
    }

    /**
     * @param toSerial
     *            The toSerial to set.
     */
    public void setToSerial(String toSerial) {
        _toSerial = toSerial;
    }

    /**
     * @return Returns the totalNoOfVouchStr.
     */
    public String getTotalNoOfVouchStr() {
        return _totalNoOfVouchStr;
    }

    /**
     * @param totalNoOfVouchStr
     *            The totalNoOfVouchStr to set.
     */
    public void setTotalNoOfVouchStr(String totalNoOfVouchStr) {
        _totalNoOfVouchStr = totalNoOfVouchStr;
    }

    /**
     * @return Returns the underProcessExpiry.
     */
    public String getUnderProcessExpiry() {
        return _underProcessExpiry;
    }

    /**
     * @param underProcessExpiry
     *            The underProcessExpiry to set.
     */
    public void setUnderProcessExpiry(String underProcessExpiry) {
        _underProcessExpiry = underProcessExpiry;
    }

    /**
     * @return Returns the voucherStatus.
     */
    public String getVoucherStatus() {
        return _voucherStatus;
    }

    /**
     * @param voucherStatus
     *            The voucherStatus to set.
     */
    public void setVoucherStatus(String voucherStatus) {
        _voucherStatus = voucherStatus;
    }

    /**
     * @return Returns the productIDName.
     */
    public String getProductIDName() {
        return _ProductIDName;
    }

    /**
     * @param productIDName
     *            The productIDName to set.
     */
    public void setProductIDName(String productIDName) {
        _ProductIDName = productIDName;
    }

    /**
     * @return Returns the voucherStatusName.
     */
    public String getVoucherStatusName() {
        return _VoucherStatusName;
    }

    /**
     * @param voucherStatusName
     *            The voucherStatusName to set.
     */
    public void setVoucherStatusName(String voucherStatusName) {
        _VoucherStatusName = voucherStatusName;
    }

    /**
     * @return Returns the mrp.
     */
    public double getMrp() {
        return _mrp;
    }

    /**
     * @param mrp
     *            The mrp to set.
     */
    public void setMrp(double mrp) {
        _mrp = mrp;
    }

    /**
     * @return Returns the serialFromNos.
     */
    public long getSerialFromNos() {
        return _serialFromNos;
    }

    /**
     * @param serialFromNos
     *            The serialFromNos to set.
     */
    public void setSerialFromNos(long serialFromNos) {
        _serialFromNos = serialFromNos;
    }

    /**
     * @return Returns the serialToNos.
     */
    public long getSerialToNos() {
        return _serialToNos;
    }

    /**
     * @param serialToNos
     *            The serialToNos to set.
     */
    public void setSerialToNos(long serialToNos) {
        _serialToNos = serialToNos;
    }

    /**
     * @return Returns the totalNoOfVouch.
     */
    public long getTotalNoOfVouch() {
        return _totalNoOfVouch;
    }

    /**
     * @param totalNoOfVouch
     *            The totalNoOfVouch to set.
     */
    public void setTotalNoOfVouch(long totalNoOfVouch) {
        _totalNoOfVouch = totalNoOfVouch;
    }

    /**
     * @return Returns the processScreen.
     */
    public int getProcessScreen() {
        return _processScreen;
    }

    /**
     * @param processScreen
     *            The processScreen to set.
     */
    public void setProcessScreen(int processScreen) {
        _processScreen = processScreen;
    }

    /**
     * @return Returns the productID.
     */
    public String getProductID() {
        return _productID;
    }

    /**
     * @param productID
     *            The productID to set.
     */
    public void setProductID(String productID) {
        _productID = productID;
    }

    // Add by Anjali

    /**
     * @return the typeListSize
     */
    public int getTypeListSize() {
        return _typeList.size();
    }

    /**
     * @param typeListSize
     *            the typeListSize to set
     */
    public void setTypeListSize(int typeListSize) {
        _typeListSize = typeListSize;
    }

    /**
     * @return the typeList
     */
    public ArrayList getTypeList() {
        return _typeList;
    }

    /**
     * @param typeList
     *            the typeList to set
     */
    public void setTypeList(ArrayList typeList) {
        _typeList = typeList;
    }

    /**
     * @return the type
     */
    public String getType() {
        return _type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        _type = type;
    }

    /**
     * @return the typeDesc
     */
    public String getTypeDesc() {
        return _typeDesc;
    }

    /**
     * @param typeDesc
     *            the typeDesc to set
     */
    public void setTypeDesc(String typeDesc) {
        _typeDesc = typeDesc;
    }

    public String getVoucherType() {
        return _voucherType;
    }

    public void setVoucherType(String type) {
        _voucherType = type;
    }

    public ArrayList<VomsCategoryVO> getVoucherTypeList() {
        return _voucherTypeList;
    }

    public void setVoucherTypeList(ArrayList<VomsCategoryVO> typeList) {
        _voucherTypeList = typeList;
    }

    public String getVoucherTypeDesc() {
        return _voucherTypeDesc;
    }

    public void setVoucherTypeDesc(String typeDesc) {
        _voucherTypeDesc = typeDesc;
    }

    public int getVoucherTypeListSize() {
        if (_voucherTypeList != null) {
            return _voucherTypeList.size();
        } else {
            return 0;
        }
    }
    // End
}