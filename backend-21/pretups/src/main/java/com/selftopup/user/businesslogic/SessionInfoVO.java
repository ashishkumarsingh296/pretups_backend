package com.selftopup.user.businesslogic;

import java.io.Serializable;
import java.util.HashMap;

import com.selftopup.pretups.gateway.businesslogic.MessageGatewayVO;

/*
 * SessionInfoVO
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 10/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
public class SessionInfoVO implements Serializable {
    private String _remoteAddr;
    private String _remoteHost;
    private String _currentPageCode;
    private String _currentPageName;
    private String _currentModuleCode;
    private String _sessionID;
    private String _currentRoleCode;
    private long _totalHit;
    private long _underProcessHit;
    private boolean _underProcess = false;
    private HashMap _roleHitTimeMap = new HashMap();//
    private MessageGatewayVO _messageGatewayVO = null;
    private String _cookieID = null;

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

    public String getCurrentRoleCode() {
        return _currentRoleCode;
    }

    public void setCurrentRoleCode(String currentRoleCode) {
        _currentRoleCode = currentRoleCode;
    }

    public HashMap getRoleHitTimeMap() {
        return _roleHitTimeMap;
    }

    public void setRoleHitTimeMap(HashMap roleHitTimeMap) {
        _roleHitTimeMap = roleHitTimeMap;
    }

    public long getTotalHit() {
        return _totalHit;
    }

    public void setTotalHit(long totalHit) {
        _totalHit = totalHit;
    }

    public boolean isUnderProcess() {
        return _underProcess;
    }

    public void setUnderProcess(boolean underProcess) {
        _underProcess = underProcess;
    }

    public long getUnderProcessHit() {
        return _underProcessHit;
    }

    public void setUnderProcessHit(long underProcessHit) {
        _underProcessHit = underProcessHit;
    }

    public MessageGatewayVO getMessageGatewayVO() {
        return _messageGatewayVO;
    }

    public void setMessageGatewayVO(MessageGatewayVO messageGatewayVO) {
        _messageGatewayVO = messageGatewayVO;
    }

    /**
     * @return Returns the cookieID.
     */
    public String getCookieID() {
        return _cookieID;
    }

    /**
     * @param cookieID
     *            The cookieID to set.
     */
    public void setCookieID(String cookieID) {
        _cookieID = cookieID;
    }
}
