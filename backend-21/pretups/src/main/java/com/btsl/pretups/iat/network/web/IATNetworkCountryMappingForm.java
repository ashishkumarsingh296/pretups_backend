/*
 * @# IATNetworkCountryMappingForm.java
 * This Bean class is for IAT Country network mapping
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Jul 07, 2009 Chetan Kothari Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2009 comviva technologies
 */
package com.btsl.pretups.iat.network.web;

import java.util.ArrayList;

//import org.apache.struts.action.ActionMapping;
//import org.apache.struts.validator.ValidatorActionForm;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.iat.businesslogic.IATNetworkServiceMappingVO;

/**
 * 
 */
public class IATNetworkCountryMappingForm /*extends ValidatorActionForm*/ {

    private static Log _logger = LogFactory.getLog(IATNetworkCountryMappingForm.class.getName());

    public IATNetworkCountryMappingForm() {

    }

    /**
     * Method flush. This method is used to reset all the values of the form
     * bean.
     */

    private ArrayList _receiverCountryList = null;
    private String _receiverCountry = null;
    private String _receiverNetworkName = null;
    private String _receiverNetworkCode = null;
    private String _receiverNetworkPrefix = null;
    private String _iatName = null;
    private String _serviceType = null;
    private ArrayList _iatNetworkServiceList = null;
    private ArrayList _iatServiceList = null;
    private String _msgLanguage1 = null;
    private String _msgLanguage2 = null;
    private String _iatIP = null;
    private String _iatPort = null;
    private ArrayList _iatList = null;
    private String _receiverCountryName = null;
    private long _time = 0;
    private String _action = null;
    private ArrayList _iatNetworkNameList = null;
    private ArrayList _iatNetworkCodeList = null;
    private String _mainAction = null;
    private String _status = null;
    private ArrayList _statusList = null;
    private String _networkMapID;
    private String _networkCountrySrId;
    private ArrayList _serviceStatusList;
    private boolean _allServiceMapped = false;

    private ArrayList _iatStatusList = null;

    public void flush() {
        _receiverCountry = null;
        _receiverNetworkName = null;
        _receiverNetworkCode = null;
        _receiverNetworkPrefix = null;
        _iatName = null;
        _serviceType = null;
        _msgLanguage1 = null;
        _msgLanguage2 = null;
        _iatNetworkServiceList = null;
        _iatServiceList = null;
        _iatIP = null;
        _iatPort = null;
        _status = null;
        _allServiceMapped = false;
    }

    public String toString() {
        StringBuffer sbf = new StringBuffer("");
        sbf.append("[Receiver country Name :" + _receiverCountry + "]");
        sbf.append("[Receiver network Name :" + _receiverNetworkName + "]");
        sbf.append("[Receiver network code :" + _receiverNetworkCode + "]");
        sbf.append("[Receiver network Prefix :" + _receiverNetworkPrefix + "]");
        sbf.append("[IAT Name :" + _iatName + "]");
        sbf.append("[Service Type :" + _serviceType + "]");
        sbf.append("[message language1 :" + _msgLanguage1 + "]");
        sbf.append("[message language2 :" + _msgLanguage2 + "]");
        return sbf.toString();
    }

    /**
     * Method semiFlush. This method is used to reset some of the values of the
     * form
     * bean.
     */
    public void semiFlush() {
        _iatName = null;
        _serviceType = null;
        _msgLanguage1 = null;
        _msgLanguage2 = null;
        _iatIP = null;
        _iatPort = null;
        _allServiceMapped = false;
    }

    /**
     * @return Returns the iatName.
     */
    public String getIatName() {
        return _iatName;
    }

    /**
     * @param iatName
     *            The iatName to set.
     */
    public void setIatName(String iatName) {
        _iatName = iatName;
    }

    /**
     * @return Returns the iatStatusList.
     */
    public ArrayList getIatStatusList() {
        return _iatStatusList;
    }

    /**
     * @param iatStatusList
     *            The iatStatusList to set.
     */
    public void setIatStatusList(ArrayList iatStatusList) {
        _iatStatusList = iatStatusList;
    }

    /**
     * @return Returns the msgLanguage1.
     */
    public String getMsgLanguage1() {
        return _msgLanguage1;
    }

    /**
     * @param msgLanguage1
     *            The msgLanguage1 to set.
     */
    public void setMsgLanguage1(String msgLanguage1) {
        _msgLanguage1 = msgLanguage1;
    }

    /**
     * @return Returns the msgLanguage2.
     */
    public String getMsgLanguage2() {
        return _msgLanguage2;
    }

    /**
     * @param msgLanguage2
     *            The msgLanguage2 to set.
     */
    public void setMsgLanguage2(String msgLanguage2) {
        _msgLanguage2 = msgLanguage2;
    }

    /**
     * @return Returns the receiverCountry.
     */
    public String getReceiverCountry() {
        return _receiverCountry;
    }

    /**
     * @param receiverCountry
     *            The receiverCountry to set.
     */
    public void setReceiverCountry(String receiverCountry) {
        _receiverCountry = receiverCountry;
    }

    /**
     * @return Returns the receiverCountryList.
     */
    public ArrayList getReceiverCountryList() {
        return _receiverCountryList;
    }

    /**
     * @param receiverCountryList
     *            The receiverCountryList to set.
     */
    public void setReceiverCountryList(ArrayList receiverCountryList) {
        _receiverCountryList = receiverCountryList;
    }

    /**
     * @return Returns the receiverNetworkCode.
     */
    public String getReceiverNetworkCode() {
        return _receiverNetworkCode;
    }

    /**
     * @param receiverNetworkCode
     *            The receiverNetworkCode to set.
     */
    public void setReceiverNetworkCode(String receiverNetworkCode) {
        _receiverNetworkCode = receiverNetworkCode;
    }

    /**
     * @return Returns the receiverNetworkName.
     */
    public String getReceiverNetworkName() {
        return _receiverNetworkName;
    }

    /**
     * @param receiverNetworkName
     *            The receiverNetworkName to set.
     */
    public void setReceiverNetworkName(String receiverNetworkName) {
        _receiverNetworkName = receiverNetworkName;
    }

    /**
     * @return Returns the receiverNetworkprefix.
     */
    public String getReceiverNetworkPrefix() {
        return _receiverNetworkPrefix;
    }

    /**
     * @param receiverNetworkprefix
     *            The receiverNetworkprefix to set.
     */
    public void setReceiverNetworkPrefix(String receiverNetworkprefix) {
        _receiverNetworkPrefix = receiverNetworkprefix;
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
     * @return Returns the iapPort.
     */
    public String getIatPort() {
        return _iatPort;
    }

    /**
     * @param iapPort
     *            The iapPort to set.
     */
    public void setIatPort(String iatPort) {
        _iatPort = iatPort;
    }

    /**
     * @return Returns the iatIP.
     */
    public String getIatIP() {
        return _iatIP;
    }

    /**
     * @param iatIP
     *            The iatIP to set.
     */
    public void setIatIP(String iatIP) {
        _iatIP = iatIP;
    }

    /**
     * @return Returns the iatList.
     */
    public ArrayList getIatList() {
        return _iatList;
    }

    /**
     * @param iatList
     *            The iatList to set.
     */
    public void setIatList(ArrayList iatList) {
        _iatList = iatList;
    }

    /**
     * @return Returns the iatNetworkServiceList.
     */
    public ArrayList getIatNetworkServiceList() {
        return _iatNetworkServiceList;
    }

    /**
     * @param iatNetworkServiceList
     *            The iatNetworkServiceList to set.
     */
    public void setIatNetworkServiceList(ArrayList iatNetworkServiceList) {
        _iatNetworkServiceList = iatNetworkServiceList;
    }

    /**
     * @return Returns the iatServiceList.
     */
    public ArrayList getIatServiceList() {
        return _iatServiceList;
    }

    /**
     * @param iatServiceList
     *            The iatServiceList to set.
     */
    public void setIatServiceList(ArrayList iatServiceList) {
        _iatServiceList = iatServiceList;
    }

    /**
     * @return Returns the iatServiceList.
     */
    public IATNetworkServiceMappingVO getIatServiceListIndexed(int i) {
        return (IATNetworkServiceMappingVO) _iatServiceList.get(i);
    }

    /**
     * @param iatServiceList
     *            The iatServiceList to set.
     */
    public void setIatServiceListIndexed(int i, IATNetworkServiceMappingVO iatNetworkVO) {
        _iatServiceList.set(i, iatNetworkVO);
    }

    /**
     * @return Returns the receiverCountryName.
     */
    public String getReceiverCountryName() {
        return _receiverCountryName;
    }

    /**
     * @param receiverCountryName
     *            The receiverCountryName to set.
     */
    public void setReceiverCountryName(String receiverCountryName) {
        _receiverCountryName = receiverCountryName;
    }

    /**
     * @return Returns the time.
     */
    public long getTime() {
        return _time;
    }

    /**
     * @param time
     *            The time to set.
     */
    public void setTime(long time) {
        _time = time;
    }

    /**
     * @return Returns the iatNetworkCodeList.
     */
    public ArrayList getIatNetworkCodeList() {
        return _iatNetworkCodeList;
    }

    /**
     * @param iatNetworkCodeList
     *            The iatNetworkCodeList to set.
     */
    public void setIatNetworkCodeList(ArrayList iatNetworkCodeList) {
        _iatNetworkCodeList = iatNetworkCodeList;
    }

    /**
     * @return Returns the iatNetworkNameList.
     */
    public ArrayList getIatNetworkNameList() {
        return _iatNetworkNameList;
    }

    /**
     * @param iatNetworkNameList
     *            The iatNetworkNameList to set.
     */
    public void setIatNetworkNameList(ArrayList iatNetworkNameList) {
        _iatNetworkNameList = iatNetworkNameList;
    }

    /**
     * @return Returns the action.
     */
    public String getAction() {
        return _action;
    }

    /**
     * @param action
     *            The action to set.
     */
    public void setAction(String action) {
        _action = action;
    }

    public int getReceiverCountryListSize() {
        if (_receiverCountryList != null && !_receiverCountryList.isEmpty()) {
            return _receiverCountryList.size();
        }
        return 0;
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
     * @return Returns the mainAction.
     */
    public String getMainAction() {
        return _mainAction;
    }

    /**
     * @param mainAction
     *            The mainAction to set.
     */
    public void setMainAction(String mainAction) {
        _mainAction = mainAction;
    }

    /**
     * @return Returns the networkMapID.
     */
    public String getNetworkMapID() {
        return _networkMapID;
    }

    /**
     * @param networkMapID
     *            The networkMapID to set.
     */
    public void setNetworkMapID(String networkMapID) {
        _networkMapID = networkMapID;
    }

    /**
     * @return Returns the networkCountrySrId.
     */
    public String getNetworkCountrySrId() {
        return _networkCountrySrId;
    }

    /**
     * @param networkCountrySrId
     *            The networkCountrySrId to set.
     */
    public void setNetworkCountrySrId(String networkCountrySrId) {
        _networkCountrySrId = networkCountrySrId;
    }

    /*public void reset(ActionMapping mapping, HttpServletRequest request) {
        if (request.getParameter("add") != null) {
            if (_iatServiceList != null && !_iatServiceList.isEmpty()) {
                Iterator itr = _iatServiceList.iterator();
                IATNetworkServiceMappingVO iatVO = null;
                while (itr.hasNext()) {
                    iatVO = new IATNetworkServiceMappingVO();
                    iatVO = (IATNetworkServiceMappingVO) itr.next();
                    iatVO.setServiceStatus(null);
                }
            }

        }
    }*/

    /**
     * @return the serviceStatusList
     */
    public ArrayList getServiceStatusList() {
        return _serviceStatusList;
    }

    /**
     * @param serviceStatusList
     *            the serviceStatusList to set
     */
    public void setServiceStatusList(ArrayList serviceStatusList) {
        _serviceStatusList = serviceStatusList;
    }

    /**
     * @return the allServiceMapped
     */
    public boolean isAllServiceMapped() {
        return _allServiceMapped;
    }

    /**
     * @param allServiceMapped
     *            the allServiceMapped to set
     */
    public void setAllServiceMapped(boolean allServiceMapped) {
        _allServiceMapped = allServiceMapped;
    }

}
