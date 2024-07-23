/*
 * @(#)ViewChannelUserHandler.java
 * 
 * Name Date History
 * ------------------------------------------------------------------------
 * Manish K. Singh 11/12/2006 Initial Creation
 * 
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 * 
 * This program processes the request for Channel User details for MSISDN or/and
 * USERLOGINID.
 * If both MSISDN and USERLOGINID are there the processing will be based on
 * MSISDN.
 * It loads the Channel user's details (ChannelUserVO object)and sets it
 * (RequestVO object)
 */
package com.btsl.pretups.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

/**
 * com.btsl.pretups.requesthandler
 * ViewChannelUserHandler.java
 * 
 * This program processes the request for Channel User details for MSISDN or/and
 * USERLOGINID.
 * If both MSISDN and USERLOGINID are there the processing will be based on
 * MSISDN.
 * It loads the Channel user's details (ChannelUserVO object)and sets it
 * (RequestVO object)
 */
public class ViewChannelUserHandler implements ServiceKeywordControllerI {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private HashMap _requestMap = null;
    private static final String MSISDN_STR = "MSISDN";
    private static final String LOGINID_STR = "USERLOGINID";

    /**
     * This method is the entry point in the class and is declared in the
     * Interface ServiceKeywordControllerI
     * This method processes the request for the MSISDN OR USERLOGINID
     * calls the validate() for validating MSISDN, USERLOGINID
     * calls the loadChannelUserDetails() that sets the Channel User details in
     * the channelUserVO
     * and sets the Channel User details in the p_requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", "Entered.....p_requestVO=" + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        _requestMap = p_requestVO.getRequestMap();
        try {
            // validating the request. if msisdn is given then MSISDN,
            // filteredMsisdn return or if
            // loginid is given USERLOGINID, loginId
            String[] sArrRequest = validate(p_requestVO);

            mcomCon = new MComConnection();con=mcomCon.getConnection();

            // Loading the Channel User details
            ChannelUserVO channelUserVO = loadChannelUserDetails(con, sArrRequest, p_requestVO);

            // Setting the response object
            p_requestVO.setValueObject(channelUserVO);
            p_requestVO.setSuccessTxn(true);

        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.error("process", p_requestVO.getRequestIDStr(), "BTSLBaseException " + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                String[] args = be.getArgs();
                p_requestVO.setMessageArguments(args);
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            _log.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", p_requestVO.getRequestIDStr(), "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ViewChannelUserHandler[process]", "", "", "", "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("ViewChannelUserHandler#process");
				mcomCon = null;
			}
            _requestMap.put("RES_TYPE", "NA");
            p_requestVO.setRequestMap(_requestMap);
            if (_log.isDebugEnabled()) {
                _log.debug("process", p_requestVO.getRequestIDStr(), "Exited.....p_requestVO=" + p_requestVO);
            }
        }
    }

    /**
     * This method checks for the mandatory value MSISDN OR USERLOGINID is not
     * null,
     * if msisdn is there, then Network Prefix and supporting network are there
     * or not
     * 
     * @param p_requestVO
     *            RequestVO
     * @return retStrArr String[]
     * @throws BTSLBaseException
     */
    private String[] validate(RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "validate";
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Entered.....");
        }
        String msisdn = null;
        String loginId = null;
        String filteredMsisdn = null;
        String msisdnPrefix = null;
        NetworkPrefixVO networkPrefixVO = null;
        String networkCode = null;
        String retStrArr[] = null;
        try {
            msisdn = (String) _requestMap.get(MSISDN_STR);
            loginId = (String) _requestMap.get(LOGINID_STR);

            if (BTSLUtil.isNullString(msisdn) && BTSLUtil.isNullString(loginId)) { // if
                                                                                   // both
                                                                                   // absent
                _requestMap.put("RES_ERR_KEY", "MSISDN or USERLOGINID"); // tbd
                if (_log.isDebugEnabled()) {
                    _log.debug("validate", "Missing mandatory value: MSISDN OR USERLOGINID");
                }
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_ONE_VALUE_REQUIRED);
            }
            /*
             * else if (!BTSLUtil.isNullString(msisdn) &&
             * !BTSLUtil.isNullString(loginId)) { //if both present
             * _requestMap.put("RES_ERR_KEY","MSISDN and USERLOGINID"); //tbd
             * if(_log.isDebugEnabled()) _log.debug("validate",
             * "Excess values given, only MSISDN OR USERLOGINID should be given"
             * );
             * throw new BTSLBaseException(this,"validate",PretupsErrorCodesI.
             * CCE_XML_VCH_ERROR_ONLY_ONE_VALUE_REQUIRED, new
             * String[]{msisdn,loginId});
             * 
             * }
             */
            else if (!BTSLUtil.isNullString(msisdn)) { // if msisdn present
                                                       // and/or loginid present
                filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn); // before
                                                                      // process
                                                                      // MSISDN
                                                                      // filter
                                                                      // each-one
                if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                    _requestMap.put("RES_ERR_KEY", MSISDN_STR);
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
                }
                // get prefix of the MSISDN
                msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); // get
                                                                          // the
                                                                          // prefix
                                                                          // of
                                                                          // the
                                                                          // MSISDN
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                if (networkPrefixVO == null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("validate", "No Network prefix found for msisdn=" + msisdn);
                    }
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK);
                }
                // check network support of the MSISDN
                networkCode = networkPrefixVO.getNetworkCode();
                if (!networkCode.equals(((UserVO) p_requestVO.getSenderVO()).getNetworkID())) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("validate", "No supporting Network for msisdn=" + msisdn);
                    }
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
                }
                retStrArr = new String[] { MSISDN_STR, filteredMsisdn };
            } else if (!BTSLUtil.isNullString(loginId)) { // if only loginid
                                                          // present
                retStrArr = new String[] { LOGINID_STR, loginId };
            }
        } catch (BTSLBaseException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("validate", "BTSLBaseException " + e.getMessage());
            throw e;
        } catch (Exception e) {
            _log.error("validate", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ViewChannelUserHandler[validate]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Exiting.....");
        }
        return retStrArr;
    }

    /**
     * This method loads the channel user details for p_request is for msisdn or
     * loginid
     * 
     * @param p_con
     *            Connection
     * @param p_sArrRequest
     *            String []
     * @param p_requestVO
     *            RequestVO
     * @return channelUserVO ChannelUserVO
     * @throws BTSLBaseException
     */
    private ChannelUserVO loadChannelUserDetails(Connection p_con, String[] p_sArrRequest, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "loadChannelUserDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("loadChannelUserDetails", "Entered.....");
        }
        ChannelUserVO channelUserVO = null;
        try {
            ChannelUserDAO channelUserDAO = new ChannelUserDAO();

            String status = "'" + PretupsI.USER_STATUS_DELETED + "','" + PretupsI.USER_STATUS_CANCELED + "'";
            String statusUsed = PretupsI.STATUS_NOTIN;

            if (p_sArrRequest[0].equals(MSISDN_STR)) {
                channelUserVO = channelUserDAO.loadUsersDetails(p_con, p_sArrRequest[1], null, statusUsed, status);
                if (channelUserVO == null) {
                    throw new BTSLBaseException(this, "loadChannelUserDetails", PretupsErrorCodesI.CCE_XML_ERROR_CU_DETAILS_NOT_FOUND4MSISDN, new String[] { p_sArrRequest[1] });
                }
            } else if (p_sArrRequest[0].equals(LOGINID_STR)) {
                channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(p_con, p_sArrRequest[1], null, statusUsed, status);
                if (channelUserVO == null) {
                    throw new BTSLBaseException(this, "loadChannelUserDetails", PretupsErrorCodesI.CCE_XML_ERROR_CU_DETAILS_NOT_FOUND4LOGINID, new String[] { p_sArrRequest[1] });
                }
                if (!channelUserVO.getNetworkID().equals(((UserVO) p_requestVO.getSenderVO()).getNetworkID())) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("loadChannelUserDetails", "No supporting Network for Login ID = " + p_sArrRequest[1]);
                    }
                    throw new BTSLBaseException(this, "loadChannelUserDetails", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
                }
            }

            // load the channel user other details
            ChannelUserVO chUserVO = channelUserDAO.loadChannelUser(p_con, channelUserVO.getUserID());
            channelUserVO.setUserGrade(chUserVO.getUserGrade());
            channelUserVO.setContactPerson(chUserVO.getContactPerson());
            channelUserVO.setTransferProfileID(chUserVO.getTransferProfileID());
            channelUserVO.setCommissionProfileSetID(chUserVO.getCommissionProfileSetID());
            channelUserVO.setInSuspend(chUserVO.getInSuspend());
            channelUserVO.setOutSuspened(chUserVO.getOutSuspened());
            channelUserVO.setOutletCode(chUserVO.getOutletCode());
            channelUserVO.setSubOutletCode(chUserVO.getSubOutletCode());

            // load the channel user other details
            // load the Commission profile Name
            ArrayList valueObjectList = new UserDAO().loadCommisionProfileListByCategoryID(p_con, channelUserVO.getCategoryCode(), channelUserVO.getNetworkID());
            if (valueObjectList != null && valueObjectList.size() != 0) {
                ListValueVO vo = BTSLUtil.getOptionDesc(channelUserVO.getCommissionProfileSetID(), valueObjectList);
                channelUserVO.setCommissionProfileSetName(vo.getLabel());
            }
            // load the User Grade Name
            valueObjectList = new CategoryGradeDAO().loadGradeList(p_con, channelUserVO.getCategoryCode());
            GradeVO gradeVO = null;
            if (valueObjectList != null && valueObjectList.size() != 0) {
                for (int i = 0, j = valueObjectList.size(); i < j; i++) {
                    gradeVO = (GradeVO) valueObjectList.get(i);
                    if (gradeVO.getGradeCode().equals(channelUserVO.getUserGrade())) {
                        channelUserVO.setUserGradeName(gradeVO.getGradeName());
                        break;
                    }
                }
            }
            // load the Transfer Profile Name
            valueObjectList = new TransferProfileDAO().loadTransferProfileByCategoryID(p_con, channelUserVO.getNetworkID(), channelUserVO.getCategoryCode(), PretupsI.PARENT_PROFILE_ID_USER);
            if (valueObjectList != null && valueObjectList.size() != 0) {
                ListValueVO vo = BTSLUtil.getOptionDesc(channelUserVO.getTransferProfileID(), valueObjectList);
                channelUserVO.setTransferProfileName(vo.getLabel());
            }

            /*
             * Roles are not showing
             * 
             * //getting the Roles set in channelUserVO
             * String roleAssignment=null;
             * if("Y".equalsIgnoreCase(channelUserVO.getCategoryVO().getFixedRoles
             * ()))
             * roleAssignment=MenuBL.FIXED;
             * else
             * roleAssignment=MenuBL.ASSIGNED;
             * ArrayList
             * alMenuItem=MenuBL.getMenuItemList(p_con,channelUserVO.getUserID
             * (),
             * channelUserVO.getCategoryCode(),roleAssignment,Constants.getProperty
             * ("ROLE_TYPE"),channelUserVO.getCategoryVO().getDomainTypeCode());
             * 
             * ArrayList alModuleRoles=null;
             * if (alMenuItem != null && alMenuItem.size()!=0) {
             * Hashtable htModuleRoles = new Hashtable();
             * MenuItem menuItem=null;
             * String moduleCode=null;
             * String roleCode=null;
             * ArrayList alRoles = null;
             * //String moduleName=null;
             * //String roleName=null;
             * //Locale locale=new
             * Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),SystemPreferences
             * .DEFAULT_COUNTRY);
             * 
             * for(int i=0;i<alMenuItem.size();i++)
             * {
             * menuItem =(MenuItem) alMenuItem.get(i);
             * moduleCode = menuItem.getModuleCode();
             * roleCode = menuItem.getRoleCode();
             * //moduleName=BTSLUtil.getMessage(locale, moduleCode,null);
             * //roleName=BTSLUtil.getMessage(locale, roleCode,null);
             * alRoles = (ArrayList) htModuleRoles.get(moduleCode);
             * 
             * if (alRoles == null) {
             * alRoles = new ArrayList();
             * alRoles.add(roleCode);
             * htModuleRoles.put(moduleCode, alRoles);
             * }
             * else if (!alRoles.contains(roleCode)) {
             * alRoles.add(roleCode);
             * htModuleRoles.put(moduleCode, alRoles);
             * }
             * }
             * 
             * alModuleRoles= new ArrayList();
             * Set setModules = htModuleRoles.keySet();
             * Iterator iter = setModules.iterator();
             * StringBuffer strBuf = null;
             * String modCode = null;
             * for (int j=0;j<htModuleRoles.size();j++) {
             * strBuf = new StringBuffer(100);
             * modCode= (String) iter.next();
             * strBuf.append(modCode);
             * strBuf.append(':');
             * alRoles = (ArrayList) htModuleRoles.get(modCode);
             * for (int k=0; k<alRoles.size(); k++) {
             * strBuf.append((String) alRoles.get(k));
             * strBuf.append(',');
             * }
             * alModuleRoles.add(strBuf.substring(0, strBuf.length()-1));
             * }
             * }
             * channelUserVO.setMenuItemList(alModuleRoles);
             */

            // Setting the ServiceType
            ArrayList alServiceType = channelUserDAO.loadUserServicesList(p_con, channelUserVO.getUserID());
            if (alServiceType != null && alServiceType.size() != 0) {
                ListValueVO listValueVO = null;
                StringBuffer strBuf = new StringBuffer(100);
                for (int i = 0, j = alServiceType.size(); i < j; i++) {
                    listValueVO = (ListValueVO) alServiceType.get(i);
                    strBuf.append(listValueVO.getValue());
                    strBuf.append(",");
                }
                channelUserVO.setServiceTypes(strBuf.substring(0, strBuf.length() - 1));
            }
        } catch (BTSLBaseException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("loadChannelUserDetails", "BTSLBaseException " + e.getMessage());
            throw e;
        } catch (Exception e) {
            _log.error("loadChannelUserDetails", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ViewChannelUserHandler[loadChannelUserDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadChannelUserDetails", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("loadChannelUserDetails", "Exiting.....");
        }
        return channelUserVO;
    }

}
