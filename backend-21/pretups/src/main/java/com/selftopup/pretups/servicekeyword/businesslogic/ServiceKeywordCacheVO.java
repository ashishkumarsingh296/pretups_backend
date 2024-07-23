/**
 * @(#)ServiceKeywordCacheVO.java
 *                                Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                All Rights Reserved
 * 
 *                                <description>
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                avinash.kamthan Mar 16, 2005 Initital Creation
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 * 
 */

package com.selftopup.pretups.servicekeyword.businesslogic;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;

/**
 * @author avinash.kamthan
 * 
 */
public class ServiceKeywordCacheVO implements Serializable {

    private String _keyword;
    private String _module;
    private String _requestInterfaceType;
    private String _serverPort;

    private String _serviceType;
    private String _requestHandlerClass;
    private String _errorKey;
    private String _allowedVersion;
    private String _status;
    private String _type;
    private String _externalInterface;
    private String _flexible;
    private String _name;

    private Date _modifiedServiceType;
    private Date _modifiedOnServiceKeyword;
    private Timestamp _modifiedServiceTypeTimestamp;
    private Timestamp _modifiedOnServiceKeywordTimestamp;

    private String _unregisteredAccessAllowed = "N";
    private String _messageFormat;

    // variable to store either to take language from IN or not
    private String _useInterfaceLanguage;
    private String _groupType;
    private boolean _subKeywordApplicable = false;

    // CR 000009 Sub Keyword Fields
    private String _subKeyword;
    private String _fileParser = null;
    private ArrayList _subKeywordList = null;
    private String _requestParam = null;

    public ServiceKeywordCacheVO() {
        super();
    }

    public ServiceKeywordCacheVO(ServiceKeywordCacheVO cache) {
        _keyword = cache.getKeyword();
        _module = cache.getModule();
        _requestInterfaceType = cache.getRequestInterfaceType();
        _serverPort = cache.getServerPort();

        _serviceType = cache.getServiceType();
        _requestHandlerClass = cache.getRequestHandlerClass();
        _errorKey = cache.getErrorKey();
        _allowedVersion = cache.getAllowedVersion();
        _status = cache.getStatus();
        _type = cache.getType();
        _externalInterface = cache.getExternalInterface();
        _flexible = cache.getFlexible();
        _name = cache.getName();

        _modifiedServiceType = cache.getModifiedServiceType();
        _modifiedOnServiceKeyword = cache.getModifiedOnServiceKeyword();
        _modifiedServiceTypeTimestamp = cache.getModifiedServiceTypeTimestamp();
        _modifiedOnServiceKeywordTimestamp = cache.getModifiedOnServiceKeywordTimestamp();

        _unregisteredAccessAllowed = cache.getUnregisteredAccessAllowed();
        _messageFormat = cache.getMessageFormat();
        _useInterfaceLanguage = cache.getUseInterfaceLanguage();
        _groupType = cache.getGroupType();
        _subKeywordApplicable = cache.isSubKeywordApplicable();
        _subKeyword = cache.getSubKeyword();
        _subKeywordList = cache.getSubKeywordList();
        _requestParam = cache.getRequestParam();
    }

    public String getAllowedVersion() {
        return _allowedVersion;
    }

    public void setAllowedVersion(String allowedVersion) {
        _allowedVersion = allowedVersion;
    }

    public String getErrorKey() {
        return _errorKey;
    }

    public void setErrorKey(String errorKey) {
        _errorKey = errorKey;
    }

    public String getKeyword() {
        return _keyword;
    }

    public void setKeyword(String keyword) {
        _keyword = keyword;
    }

    public Date getModifiedOnServiceKeyword() {
        return _modifiedOnServiceKeyword;
    }

    public void setModifiedOnServiceKeyword(Date modifiedOnServiceKeyword) {
        _modifiedOnServiceKeyword = modifiedOnServiceKeyword;
    }

    public Date getModifiedServiceType() {
        return _modifiedServiceType;
    }

    public void setModifiedServiceType(Date modifiedServiceType) {
        _modifiedServiceType = modifiedServiceType;
    }

    public String getModule() {
        return _module;
    }

    public void setModule(String module) {
        _module = module;
    }

    public String getRequestHandlerClass() {
        return _requestHandlerClass;
    }

    public void setRequestHandlerClass(String requestHandlerClass) {
        _requestHandlerClass = requestHandlerClass;
    }

    public String getRequestInterfaceType() {
        return _requestInterfaceType;
    }

    public void setRequestInterfaceType(String requestInterfaceType) {
        _requestInterfaceType = requestInterfaceType;
    }

    public String getServerPort() {
        return _serverPort;
    }

    public void setServerPort(String serverPort) {
        _serverPort = serverPort;
    }

    public String getServiceType() {
        return _serviceType;
    }

    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public String getServiceTypeKeyword() {
        StringBuffer sbf = new StringBuffer();

        sbf.append(this.getServiceType());
        sbf.append("_");
        sbf.append(this.getKeyword());

        return sbf.toString();
    }

    public String toString() {

        StringBuffer sbf = new StringBuffer(200);

        sbf.append(" keyword  " + _keyword);
        sbf.append(" module  " + _module);
        sbf.append(" requestInterfaceType  " + _requestInterfaceType);
        sbf.append(" serverPort  " + _serverPort);
        sbf.append(" serviceType  " + _serviceType);
        sbf.append(" requestHandlerClass  " + _requestHandlerClass);
        sbf.append(" errorKey  " + _errorKey);
        sbf.append(" allowedVersion  " + _allowedVersion);
        sbf.append(" status  " + _status);
        sbf.append(" type  " + _type);
        sbf.append(" _externalInterface  " + _externalInterface);
        sbf.append(" _flexible  " + _flexible);
        sbf.append(" _name  " + _name);
        sbf.append(" _messageFormat  " + _messageFormat);
        sbf.append(" modifiedServiceType  " + _modifiedServiceType);
        sbf.append(" modifiedOnServiceKeyword  " + _modifiedOnServiceKeyword);
        sbf.append(" _useInterfaceLanguage  " + _useInterfaceLanguage);
        sbf.append(" _groupType  " + _groupType);
        sbf.append(" _subKeywordApplicable  " + _subKeywordApplicable);
        sbf.append(" _subKeyword  " + _subKeyword);
        return sbf.toString();
    }

    public boolean equals(ServiceKeywordCacheVO p_serviceCacheVO) {
        boolean flag = false;

        if (this.getModifiedOnServiceKeywordTimestamp().equals(p_serviceCacheVO.getModifiedOnServiceKeywordTimestamp())) {
            if (this.getModifiedServiceTypeTimestamp().equals(p_serviceCacheVO.getModifiedServiceTypeTimestamp())) {
                flag = true;
            }
        }

        return flag;
    }

    public Timestamp getModifiedOnServiceKeywordTimestamp() {
        return _modifiedOnServiceKeywordTimestamp;
    }

    public void setModifiedOnServiceKeywordTimestamp(Timestamp modifiedOnServiceKeywordTimestamp) {
        _modifiedOnServiceKeywordTimestamp = modifiedOnServiceKeywordTimestamp;
    }

    public Timestamp getModifiedServiceTypeTimestamp() {
        return _modifiedServiceTypeTimestamp;
    }

    public void setModifiedServiceTypeTimestamp(Timestamp modifiedServiceTypeTimestamp) {
        _modifiedServiceTypeTimestamp = modifiedServiceTypeTimestamp;
    }

    public String differences(ServiceKeywordCacheVO p_serviceCacheVO) {

        StringBuffer sbf = new StringBuffer(400);
        String startSeperator = Constants.getProperty("cachestartseparator");
        String middleSeperator = Constants.getProperty("cachemiddleseparator");

        if (!BTSLUtil.isNullString(this.getServiceType()) && !this.getServiceType().equals(p_serviceCacheVO.getServiceType())) {
            sbf.append(startSeperator);
            sbf.append("Service Type");
            sbf.append(middleSeperator);
            sbf.append(p_serviceCacheVO.getServiceType());
            sbf.append(middleSeperator);
            sbf.append(this.getServiceType());
        }

        if (!BTSLUtil.isNullString(this.getRequestHandlerClass()) && !this.getRequestHandlerClass().equals(p_serviceCacheVO.getRequestHandlerClass())) {
            sbf.append(startSeperator);
            sbf.append("Request Handler Class");
            sbf.append(middleSeperator);
            sbf.append(p_serviceCacheVO.getRequestHandlerClass());
            sbf.append(middleSeperator);
            sbf.append(this.getRequestHandlerClass());
        }

        if (!BTSLUtil.isNullString(this.getErrorKey()) && !this.getErrorKey().equals(p_serviceCacheVO.getErrorKey())) {
            sbf.append(startSeperator);
            sbf.append("Error Key");
            sbf.append(middleSeperator);
            sbf.append(p_serviceCacheVO.getErrorKey());
            sbf.append(middleSeperator);
            sbf.append(this.getErrorKey());
        }

        if (!BTSLUtil.isNullString(this.getAllowedVersion()) && !this.getAllowedVersion().equals(p_serviceCacheVO.getAllowedVersion())) {
            sbf.append(startSeperator);
            sbf.append("Allowed Version");
            sbf.append(middleSeperator);
            sbf.append(p_serviceCacheVO.getAllowedVersion());
            sbf.append(middleSeperator);
            sbf.append(this.getAllowedVersion());
        }

        if (!BTSLUtil.isNullString(this.getName()) && !this.getName().equals(p_serviceCacheVO.getName())) {
            sbf.append(startSeperator);
            sbf.append("Name");
            sbf.append(middleSeperator);
            sbf.append(p_serviceCacheVO.getName());
            sbf.append(middleSeperator);
            sbf.append(this.getName());
        }

        if (!BTSLUtil.isNullString(this.getMessageFormat()) && !this.getMessageFormat().equals(p_serviceCacheVO.getMessageFormat())) {
            sbf.append(startSeperator);
            sbf.append("MessageFormat");
            sbf.append(middleSeperator);
            sbf.append(p_serviceCacheVO.getMessageFormat());
            sbf.append(middleSeperator);
            sbf.append(this.getMessageFormat());
        }

        if (!BTSLUtil.isNullString(this.getType()) && !this.getType().equals(p_serviceCacheVO.getType())) {
            sbf.append(startSeperator);
            sbf.append("Type");
            sbf.append(middleSeperator);
            sbf.append(p_serviceCacheVO.getType());
            sbf.append(middleSeperator);
            sbf.append(this.getType());
        }
        if (!BTSLUtil.isNullString(this.getExternalInterface()) && !this.getExternalInterface().equals(p_serviceCacheVO.getExternalInterface())) {
            sbf.append(startSeperator);
            sbf.append("External Interface");
            sbf.append(middleSeperator);
            sbf.append(p_serviceCacheVO.getExternalInterface());
            sbf.append(middleSeperator);
            sbf.append(this.getExternalInterface());
        }

        if (!BTSLUtil.isNullString(this.getFlexible()) && !this.getFlexible().equals(p_serviceCacheVO.getFlexible())) {
            sbf.append(startSeperator);
            sbf.append("Flexible");
            sbf.append(middleSeperator);
            sbf.append(p_serviceCacheVO.getFlexible());
            sbf.append(middleSeperator);
            sbf.append(this.getFlexible());
        }
        if (!BTSLUtil.isNullString(this.getStatus()) && !this.getStatus().equals(p_serviceCacheVO.getStatus())) {
            sbf.append(startSeperator);
            sbf.append("Status");
            sbf.append(middleSeperator);
            sbf.append(p_serviceCacheVO.getStatus());
            sbf.append(middleSeperator);
            sbf.append(this.getStatus());
        }

        return sbf.toString();
    }

    public String logInfo() {

        StringBuffer sbf = new StringBuffer(100);
        String startSeperator = Constants.getProperty("cachestartseparator");
        String middleSeperator = Constants.getProperty("cachemiddleseparator");

        sbf.append(startSeperator);
        sbf.append("Service Type");
        sbf.append(middleSeperator);
        sbf.append(this.getServiceType());

        sbf.append(startSeperator);
        sbf.append("Request Handler Class");
        sbf.append(middleSeperator);
        sbf.append(this.getRequestHandlerClass());

        sbf.append(startSeperator);
        sbf.append("Error Key");
        sbf.append(middleSeperator);
        sbf.append(this.getErrorKey());

        sbf.append(startSeperator);
        sbf.append("Allowed Version");
        sbf.append(middleSeperator);
        sbf.append(this.getAllowedVersion());

        sbf.append(startSeperator);
        sbf.append("Type");
        sbf.append(middleSeperator);
        sbf.append(this.getType());

        sbf.append(startSeperator);
        sbf.append("Name");
        sbf.append(middleSeperator);
        sbf.append(this.getName());

        sbf.append(startSeperator);
        sbf.append("MessageFormat");
        sbf.append(middleSeperator);
        sbf.append(this.getMessageFormat());

        sbf.append(startSeperator);
        sbf.append("External Interface");
        sbf.append(middleSeperator);
        sbf.append(this.getExternalInterface());

        sbf.append(startSeperator);
        sbf.append("Flexible");
        sbf.append(middleSeperator);
        sbf.append(this.getFlexible());

        sbf.append(startSeperator);
        sbf.append("Status");
        sbf.append(middleSeperator);
        sbf.append(this.getStatus());

        return sbf.toString();
    }

    public String getExternalInterface() {
        return _externalInterface;
    }

    public void setExternalInterface(String externalInterface) {
        _externalInterface = externalInterface;
    }

    public String getFlexible() {
        return _flexible;
    }

    public void setFlexible(String flexible) {
        _flexible = flexible;
    }

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        _type = type;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getUnregisteredAccessAllowed() {
        return _unregisteredAccessAllowed;
    }

    public void setUnregisteredAccessAllowed(String unregisteredAccessAllowed) {
        _unregisteredAccessAllowed = unregisteredAccessAllowed;
    }

    public String getMessageFormat() {
        return _messageFormat;
    }

    public void setMessageFormat(String messageFormat) {
        _messageFormat = messageFormat;
    }

    public String getUseInterfaceLanguage() {
        return _useInterfaceLanguage;
    }

    public void setUseInterfaceLanguage(String useInterfaceLanguage) {
        _useInterfaceLanguage = useInterfaceLanguage;
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
     * Sub Keyword is allowed or not
     * 
     * @return
     */
    public boolean isSubKeywordApplicable() {
        return _subKeywordApplicable;
    }

    /**
     * Set the Sub Keyword Allowed Parameter
     * 
     * @param subKeywordApplicable
     */
    public void setSubKeywordApplicable(boolean subKeywordApplicable) {
        _subKeywordApplicable = subKeywordApplicable;
    }

    public String getSubKeyword() {
        return _subKeyword;
    }

    public void setSubKeyword(String subKeyword) {
        _subKeyword = subKeyword;
    }

    public ArrayList getSubKeywordList() {
        return _subKeywordList;
    }

    public void setSubKeywordList(ArrayList subKeywordList) {
        _subKeywordList = subKeywordList;
    }

    /**
     * @return Returns the fileParser.
     */
    public String getFileParser() {
        return _fileParser;
    }

    /**
     * @param fileParser
     *            The fileParser to set.
     */
    public void setFileParser(String fileParser) {
        _fileParser = fileParser;
    }

    public String getRequestParam() {
        return _requestParam;
    }

    public void setRequestParam(String param) {
        _requestParam = param;
    }
}
