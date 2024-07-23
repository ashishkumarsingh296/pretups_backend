package com.btsl.pretups.iat.businesslogic;

import java.util.ArrayList;

public class IATNetworkServiceMappingVO {

    private String _recCountryShortName;
    private String _recNetworkCode;
    private String _iatName;
    private String _iatCode;
    private String _serviceType;
    private String _serviceStatus;
    private String _language1Message;
    private String _language2Message;
    private String _iatip;
    private String _iatPort;
    private String _handlerClass;
    private ArrayList _iatServiceList = null;
    private String _serialNumber = null;
    private String _underProcessMsgReq;
    private String _networkMapSrID;
    private String _interfaceTypeID;
    private String _serviceName;

    public String toString() {
        StringBuffer sbf = new StringBuffer("");
        sbf.append("[_recCountryShortName :" + _recCountryShortName + "]");
        sbf.append("[_recNetworkCode :" + _recNetworkCode + "]");
        sbf.append("[_iatName :" + _iatName + "]");
        sbf.append("[_iatCode :" + _iatCode + "]");
        sbf.append("[_serviceType :" + _serviceType + "]");
        sbf.append("[_serviceStatus :" + _serviceStatus + "]");
        sbf.append("[_language1Message :" + _language1Message + "]");
        sbf.append("[_language2Message :" + _language2Message + "]");
        sbf.append("[_iatip :" + _iatip + "]");
        sbf.append("[_iatPort:" + _iatPort + "]");
        sbf.append("[_handlerClass :" + _handlerClass + "]");
        sbf.append("[_serialNumber :" + _serialNumber + "]");
        sbf.append("[_underProcessMsgReq :" + _underProcessMsgReq + "]");
        sbf.append("[_networkMapSrID :" + _networkMapSrID + "]");
        sbf.append("[_interfaceTypeID :" + _interfaceTypeID + "]");
        return sbf.toString();
    }

    /**
     * @return Returns the networkMapSrID.
     */
    public String getNetworkMapSrID() {
        return _networkMapSrID;
    }

    /**
     * @param networkMapSrID
     *            The networkMapSrID to set.
     */
    public void setNetworkMapSrID(String networkMapSrID) {
        _networkMapSrID = networkMapSrID;
    }

    public String getIatCode() {
        return _iatCode;
    }

    public void setIatCode(String iatCode) {
        _iatCode = iatCode;
    }

    public String getIatName() {
        return _iatName;
    }

    public void setIatName(String iatName) {
        _iatName = iatName;
    }

    public String getLanguage1Message() {
        return _language1Message;
    }

    public void setLanguage1Message(String language1Message) {
        _language1Message = language1Message;
    }

    public String getLanguage2Message() {
        return _language2Message;
    }

    public void setLanguage2Message(String language2Message) {
        _language2Message = language2Message;
    }

    public String getRecCountryShortName() {
        return _recCountryShortName;
    }

    public void setRecCountryShortName(String recCountryShortName) {
        _recCountryShortName = recCountryShortName;
    }

    public String getRecNetworkCode() {
        return _recNetworkCode;
    }

    public void setRecNetworkCode(String recNetworkCode) {
        _recNetworkCode = recNetworkCode;
    }

    public String getServiceStatus() {
        return _serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        _serviceStatus = serviceStatus;
    }

    public String getServiceType() {
        return _serviceType;
    }

    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    public String getIatip() {
        return _iatip;
    }

    public void setIatip(String iatip) {
        _iatip = iatip;
    }

    public String getIatPort() {
        return _iatPort;
    }

    public void setIatPort(String iatPort) {
        _iatPort = iatPort;
    }

    public String getHandlerClass() {
        return _handlerClass;
    }

    public void setHandlerClass(String handlerClass) {
        _handlerClass = handlerClass;
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
     * @return Returns the serialNumber.
     */
    public String getSerialNumber() {
        return _serialNumber;
    }

    /**
     * @param serialNumber
     *            The serialNumber to set.
     */
    public void setSerialNumber(String serialNumber) {
        _serialNumber = serialNumber;
    }

    /**
     * @return Returns the underProcessMsgReq.
     */
    public String getUnderProcessMsgReq() {
        return _underProcessMsgReq;
    }

    /**
     * @param underProcessMsgReq
     *            The underProcessMsgReq to set.
     */
    public void setUnderProcessMsgReq(String underProcessMsgReq) {
        _underProcessMsgReq = underProcessMsgReq;
    }

    public String getInterfaceTypeID() {
        return _interfaceTypeID;
    }

    public void setInterfaceTypeID(String interfaceTypeID) {
        _interfaceTypeID = interfaceTypeID;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return _serviceName;
    }

    /**
     * @param serviceName
     *            the serviceName to set
     */
    public void setServiceName(String serviceName) {
        _serviceName = serviceName;
    }
    
    public static IATNetworkServiceMappingVO getInstance(){
    	return new IATNetworkServiceMappingVO();
    }

}
