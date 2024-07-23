/*
 * #SlabGeneratorForm.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Dec 26, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.master.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import jakarta.servlet.http.HttpServletRequest;

/*import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorActionForm;*/

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.SlabGeneratorVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.ibm.icu.util.Calendar;

public class SlabGeneratorForm /*extends ValidatorActionForm */{
    private String _slabid;
    private String _toRange;
    private String _fromRange;
    private String _serviceType;
    private String _serviceTypeTemp;// this is used to store the temp. value of
                                    // the service type selected by the 1st jsp
    private String _serviceTypeName;
    private String _slabDate;
    private String _slabDateTemp;// this is used to store the temp. value of the
                                 // slab date selected by the 1st jsp
    private String _slabDateStr;
    private ArrayList _serviceTypeList;
    private ArrayList _slabList;
    private String _radioIndex;
    private ArrayList _slabListVO;
    private ArrayList _newSlabListVO;
    private ArrayList _slabDateListVO;
    private ArrayList _tempSlabListVO;
    private LinkedHashMap _slabMap;
    private ArrayList _tempSlabList;
    private String _modifyRequired;
    private Log _log = LogFactory.getLog(SlabGeneratorForm.class.getName());

    /**
     * Clears all the instance variables
     */
    public void flush() {
        _serviceType = null;
        _slabDateStr = null;
    }

    /**
     * @return Returns the serviceTypeTemp.
     */
    public String getServiceTypeTemp() {
        return _serviceTypeTemp;
    }

    /**
     * @param serviceTypeTemp
     *            The serviceTypeTemp to set.
     */
    public void setServiceTypeTemp(String serviceTypeTemp) {
        _serviceTypeTemp = serviceTypeTemp;
    }

    /**
     * @return Returns the slabDateTemp.
     */
    public String getSlabDateTemp() {
        return _slabDateTemp;
    }

    /**
     * @param slabDateTemp
     *            The slabDateTemp to set.
     */
    public void setSlabDateTemp(String slabDateTemp) {
        _slabDateTemp = slabDateTemp;
    }

    /**
     * @return Returns the modifyRequired.
     */
    public String getModifyRequired() {
        return _modifyRequired;
    }

    /**
     * @param modifyRequired
     *            The modifyRequired to set.
     */
    public void setModifyRequired(String modifyRequired) {
        _modifyRequired = modifyRequired;
    }

    /**
     * @return Returns the tempSlabList.
     */
    public ArrayList getTempSlabList() {
        return _tempSlabList;
    }

    /**
     * @param tempSlabList
     *            The tempSlabList to set.
     */
    public void setTempSlabList(ArrayList tempSlabList) {
        _tempSlabList = tempSlabList;
    }

    /**
     * @return Returns the slabDateStr.
     */
    public String getSlabDateStr() {
        return _slabDateStr;
    }

    /**
     * @param slabDateStr
     *            The slabDateStr to set.
     */
    public void setSlabDateStr(String slabDateStr) {
        _slabDateStr = slabDateStr;
    }

    /**
     * @return Returns the newSlabListVO.
     */
    public ArrayList getNewSlabListVO() {
        return _newSlabListVO;
    }

    /**
     * @param newSlabListVO
     *            The newSlabListVO to set.
     */
    public void setNewSlabListVO(ArrayList newSlabListVO) {
        _newSlabListVO = newSlabListVO;
    }

    /**
     * @return Returns the slabDateListVO.
     */
    public ArrayList getSlabDateListVO() {
        return _slabDateListVO;
    }

    /**
     * @param slabDateListVO
     *            The slabDateListVO to set.
     */
    public void setSlabDateListVO(ArrayList slabDateListVO) {
        _slabDateListVO = slabDateListVO;
    }

    public int getSlabMapCount() {
        if (_slabMap != null && _slabMap.size() > 0) {
            return _slabMap.size();
        } else {
            return 0;
        }

    }

    /**
     * @return Returns the slabMap.
     */
    public LinkedHashMap getSlabMap() {
        return _slabMap;
    }

    /**
     * @param slabMap
     *            The slabMap to set.
     */
    public void setSlabMap(LinkedHashMap slabMap) {
        _slabMap = slabMap;
    }

    public void setSlabListIndexed(int i, SlabGeneratorVO vo) {
        _slabListVO.set(i, vo);
    }

    public SlabGeneratorVO getSlabListIndexed(int i) {
        return (SlabGeneratorVO) _slabListVO.get(i);
    }

    public void setSlabListIndexedAsString(int i, SlabGeneratorVO vo) {
        _slabListVO.set(i, vo);
    }

    public SlabGeneratorVO getSlabListIndexedAsString(int i) {
        return (SlabGeneratorVO) _slabListVO.get(i);
    }

    public void setNewSlabListIndexedAsString(int i, SlabGeneratorVO vo) {
        _newSlabListVO.set(i, vo);
    }

    public SlabGeneratorVO getNewSlabListIndexedAsString(int i) {
        return (SlabGeneratorVO) _newSlabListVO.get(i);
    }

    /**
     * @return Returns the tempSlabListVO.
     */
    public ArrayList getTempSlabListVO() {
        return _tempSlabListVO;
    }

    /**
     * @param tempSlabListVO
     *            The tempSlabListVO to set.
     */
    public void setTempSlabListVO(ArrayList tempSlabListVO) {
        _tempSlabListVO = tempSlabListVO;
    }

    /**
     * @return Returns the slabListVO.
     */
    public ArrayList getSlabListVO() {
        return _slabListVO;
    }

    /**
     * @param slabListVO
     *            The slabListVO to set.
     */
    public void setSlabListVO(ArrayList slabListVO) {
        _slabListVO = slabListVO;
    }

    /**
     * @return Returns the radioIndex.
     */
    public String getRadioIndex() {
        return _radioIndex;
    }

    /**
     * @param radioIndex
     *            The radioIndex to set.
     */

    public void setRadioIndex(String radioIndex) {
        _radioIndex = radioIndex;
    }

    public int SlabListSize() {
        if (_slabList != null && !_slabList.isEmpty()) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the fromRange.
     */
    public String getFromRange() {
        return _fromRange;
    }

    /**
     * @param fromRange
     *            The fromRange to set.
     */
    public void setFromRange(String fromRange) {
        _fromRange = fromRange;
    }

    /**
     * @return Returns the serviceType.
     */
    public String getServiceType() {
        return _serviceType;
    }

    /**
     * @param serviceType
     *            The serviceType to set.
     */
    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    /**
     * @return Returns the serviceTypeName.
     */
    public String getServiceTypeName() {
        return _serviceTypeName;
    }

    /**
     * @param serviceTypeName
     *            The serviceTypeName to set.
     */
    public void setServiceTypeName(String serviceTypeName) {
        _serviceTypeName = serviceTypeName;
    }

    /**
     * @return Returns the serviceTypeList.
     */
    public ArrayList getServiceTypeList() {
        return _serviceTypeList;
    }

    /**
     * @param serviceTypeList
     *            The serviceTypeList to set.
     */
    public void setServiceTypeList(ArrayList serviceTypeList) {
        _serviceTypeList = serviceTypeList;
    }

    /**
     * @return Returns the slabDate.
     */
    public String getSlabDate() {
        return _slabDate;
    }

    /**
     * @param slabDate
     *            The slabDate to set.
     */
    public void setSlabDate(String slabDate) {
        _slabDate = slabDate;
    }

    /**
     * @return Returns the slabid.
     */
    public String getSlabid() {
        return _slabid;
    }

    /**
     * @param slabid
     *            The slabid to set.
     */
    public void setSlabid(String slabid) {
        _slabid = slabid;
    }

    /**
     * @return Returns the slabList.
     */
    public ArrayList getSlabList() {
        return _slabList;
    }

    /**
     * @param slabList
     *            The slabList to set.
     */
    public void setSlabList(ArrayList slabList) {
        _slabList = slabList;
    }

    /**
     * @return Returns the toRange.
     */
    public String getToRange() {
        return _toRange;
    }

    /**
     * @param toRange
     *            The toRange to set.
     */
    public void setToRange(String toRange) {
        _toRange = toRange;
    }

    /*
     * public void reset(ActionMapping mapping, HttpServletRequest request)
     * {
     * 
     * System.out.println("\n\nInside Reset");
     * 
     * if(request.getParameter("confirm")!=null)
     * {
     * _fromRange=null;
     * _toRange=null;
     * _slabDate=null;
     * }
     * }
     */

    /**
     * Method validate.
     * This method is used to ignore the validation if the user clicks back
     * button &
     * 
     * @param mapping
     *            ActionMapping
     * @param request
     *            HttpServletRequest
     * @return ActionErrors
     */

}