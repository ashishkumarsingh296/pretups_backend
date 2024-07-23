package com.btsl.pretups.iccidkeymgmt.businesslogic;

/*
 * @(#)SimVenderCache.java
 * Copyright(c) 2007, Bharti Telesoft Ltd.
 * All Rights Reserved
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * vikas yadav Jan 09, 2007 Initital Creation
 * ------------------------------------------------------------------------------
 * -------------------
 * Parser class to handle SMSC requests
 */

import java.util.HashMap;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.services.businesslogic.SimProfileVO;

public class SimVenderCache implements Runnable  {

    private static Log _log = LogFactory.getLog(SimVenderCache.class.getName());
    private static HashMap _cipherParameterMap = new HashMap();
    private static HashMap _masterKeyMap = new HashMap();
    
    public void run() {
        try {
            Thread.sleep(50);
            loadMessageGatewayAtStartup();
        } catch (Exception e) {
        	 _log.error("SimVenderCache init() Exception ", e);
        }
    }


    public static void loadMessageGatewayAtStartup() {
        if (_log.isDebugEnabled()) {
            _log.debug("loadSimVenderCacheAtStartup()", "entered");
        }
        _cipherParameterMap = loadCipherParameter();
        _masterKeyMap = loadMasterKey();
        if (_log.isDebugEnabled()) {
            _log.debug("loadSimVenderCacheAtStartup()", "exited");
        }
    }

    /**
     * To load Cipher Parameter
     * 
     * @return HashMap
     */
    private static HashMap loadCipherParameter() {
        final String METHOD_NAME = "loadCipherParameter";
        if (_log.isDebugEnabled()) {
            _log.debug("loadCipherParameter()", "entered");
        }
        PosKeyDAO _posKeyDAO = new PosKeyDAO();
        HashMap map = null;
        try {
            map = _posKeyDAO.loadEncryptionParameters();
        } catch (Exception e) {
            _log.error("loadCipherParameter()", "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadCipherParameter()", "exited");
        }

        return map;
    }

    /**
     * To load the gateway details
     * 
     * @return HashMap
     */
    private static HashMap loadMasterKey() {
        final String METHOD_NAME = "loadMasterKey";
        if (_log.isDebugEnabled()) {
            _log.debug("loadMasterKey()", "entered");
        }
        PosKeyDAO _posKeyDAO = new PosKeyDAO();
        HashMap map = null;
        try {
            map = _posKeyDAO.masterKeyByVenderCode();
        } catch (Exception e) {
            _log.error("loadMasterKey()", "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadMasterKey()", "exited");
        }

        return map;
    }

    /**
     * get the SimProfileVO vo from cache
     * 
     * @param p_venderCode
     * @param p_networkID
     * @param p_profileID
     * @return SimProfileVO
     */
    public static SimProfileVO getCipherparamObject(String p_venderCode, String p_networkID) {
        final String METHOD_NAME = "getCipherparamObject";
        if (_log.isDebugEnabled()) {
            _log.debug("getCipherparamObject()", "entered p_venderCode" + p_venderCode + "p_networkID" + p_networkID);
        }
        SimProfileVO simProfileVO = null;
        try {
            simProfileVO = (SimProfileVO) _cipherParameterMap.get(p_venderCode + "_" + p_networkID);
        } catch (Exception e) {
            _log.error("getCipherparamObject()", "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getCipherparamObject()", "exited " + simProfileVO);
        }
        return simProfileVO;
    }

    /**
     * get the PosKeyVO vo from cache
     * 
     * @param p_venderCode
     *            * @param p_networkID
     *            * @param p_profileID
     * @return PosKeyVO
     */
    public static PosKeyVO getkeyObject(String p_venderCode, String p_networkID, String p_profileID) {
        final String METHOD_NAME = "getkeyObject";
        if (_log.isDebugEnabled()) {
            _log.debug("getkeyObject()", "entered p_venderCode" + p_venderCode + "p_profileID" + p_profileID + "p_networkID" + p_networkID);
        }
        PosKeyVO posKeyVO = null;
        try {
            posKeyVO = (PosKeyVO) _masterKeyMap.get(p_venderCode + "_" + p_networkID + "_" + p_profileID);
        } catch (Exception e) {
            _log.error("getkeyObject()", "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getkeyObject()", "exited " + posKeyVO);
        }
        return posKeyVO;
    }

}
