package com.btsl.user.businesslogic;

import java.io.Serializable;

/*
 * SessionVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 10/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
public class SessionVO implements Serializable {
    private String _remoteAddr;
    private String _remoteHost;
    private String _currentPageCode;
    private String _currentPageName;
    private String _currentModuleCode;
    private String _sessionID;

    /**
     * @return Returns the currentModuleCode.
     */
    public String getCurrentModuleCode() {
        return _currentModuleCode;
    }

    /**
     * @param currentModuleCode
     *            The currentModuleCode to set.
     */
    public void setCurrentModuleCode(String currentModuleCode) {
        _currentModuleCode = currentModuleCode;
    }

    /**
     * @return Returns the currentPageCode.
     */
    public String getCurrentPageCode() {
        return _currentPageCode;
    }

    /**
     * @param currentPageCode
     *            The currentPageCode to set.
     */
    public void setCurrentPageCode(String currentPageCode) {
        _currentPageCode = currentPageCode;
    }

    /**
     * @return Returns the currentPageName.
     */
    public String getCurrentPageName() {
        return _currentPageName;
    }

    /**
     * @param currentPageName
     *            The currentPageName to set.
     */
    public void setCurrentPageName(String currentPageName) {
        _currentPageName = currentPageName;
    }

    /**
     * @return Returns the remoteAddr.
     */
    public String getRemoteAddr() {
        return _remoteAddr;
    }

    /**
     * @param remoteAddr
     *            The remoteAddr to set.
     */
    public void setRemoteAddr(String remoteAddr) {
        _remoteAddr = remoteAddr;
    }

    /**
     * @return Returns the remoteHost.
     */
    public String getRemoteHost() {
        return _remoteHost;
    }

    /**
     * @param remoteHost
     *            The remoteHost to set.
     */
    public void setRemoteHost(String remoteHost) {
        _remoteHost = remoteHost;
    }

    /**
     * @return Returns the sessionID.
     */
    public String getSessionID() {
        return _sessionID;
    }

    /**
     * @param sessionID
     *            The sessionID to set.
     */
    public void setSessionID(String sessionID) {
        _sessionID = sessionID;
    }

}
