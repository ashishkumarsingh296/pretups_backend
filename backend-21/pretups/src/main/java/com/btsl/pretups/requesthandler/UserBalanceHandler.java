package com.btsl.pretups.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

/*
 * *
 * * @(#)UserBalanceHandler.java
 * Copyright(c) 2005-2006, Bharti Telesoft Ltd.
 * All Rights Reserved
 * 
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * sanjeev April 16, 2007 Initial Creation
 * 
 * This class handles the request for the UserBalanceHandler.
 */

public class UserBalanceHandler implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(UserBalanceHandler.class.getName());
    private HashMap _requestMap = null;
    private RequestVO _requestVO = null;

    private String _msisdn = null;
    private String _userLoginId = null;
    private String _userExtCode = null;
    private static final String XML_TAG_MSISDN = "MSISDN";
    private static final String XML_TAG_USERLOGINID = "USERLOGINID";
    private static final String XML_TAG_USEREXTCODE = "USEREXTCODE";

    /**
     * This method is the entry point into the class.
     * method process
     * 
     * @param RequestVO
     */

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", "Entered....: p_requestVO= " + p_requestVO.toString());
        }
        Connection con = null;MComConnectionI mcomCon = null;
        _requestVO = p_requestVO;

        try {
            _requestMap = p_requestVO.getRequestMap();
            mcomCon = new MComConnection();con=mcomCon.getConnection();

            _msisdn = (String) _requestMap.get(XML_TAG_MSISDN);
            _userLoginId = (String) _requestMap.get(XML_TAG_USERLOGINID);
            _userExtCode = (String) _requestMap.get(XML_TAG_USEREXTCODE);
            // this method validates whether the msisdn and userLoginId have
            // valid values in the request
            ChannelUserVO channelUserVO = validate(con);
            loadUserBalance(con, channelUserVO);

            // setting the transaction status to true
            p_requestVO.setSuccessTxn(true);

        } catch (BTSLBaseException be) {
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _requestVO.setSuccessTxn(false);

            if (be.isKey()) {
                _requestVO.setMessageCode(be.getMessageKey());
                _requestVO.setMessageArguments(be.getArgs());
            } else {
                _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.errorTrace(METHOD_NAME, be);
        } catch (Exception ex) {
            _requestVO.setSuccessTxn(false);

            // Rollbacking the transaction
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "Exception " + ex.getMessage());
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalanceHandler[process]", "", "", "", "Exception:" + ex.getMessage());
            _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        }// end of Exception
        finally {
            _requestVO.setRequestMap(_requestMap);
            // clossing database connection
			if (mcomCon != null) {
				mcomCon.close("UserBalanceHandler#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }// end of finally
    }

    /**
     * This method is called to validate the values present in the requestMap of
     * the requestVO
     * The purpose of this method is to validate the values of the msisdn or
     * login-id
     * * @param p_con Connection
     * 
     * @return channelUserVO ChannelUserVO
     * @throws BTSLBaseException
     */

    private ChannelUserVO validate(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "validate";
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Entered.....");
        }

        String msisdnPrefix = null;
        String arr[] = null;
        String filteredMsisdn = null;
        ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        ChannelUserVO channelUserVO = null;

        String status = "'" + PretupsI.USER_STATUS_ACTIVE + "','" + PretupsI.USER_STATUS_SUSPEND + "'";
        String statusUsed = PretupsI.STATUS_IN;
        try {
            if (!BTSLUtil.isNullString(_msisdn)) {
                // filtering the msisdn for country independent dial format
                filteredMsisdn = PretupsBL.getFilteredMSISDN(_msisdn);

                if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                    _requestMap.put("RES_ERR_KEY", XML_TAG_MSISDN);
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
                }

                // load user details by msisdn
                channelUserVO = channelUserDAO.loadUsersDetails(p_con, filteredMsisdn, null, statusUsed, status);
                if (channelUserVO == null) {
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_CU_DETAILS_NOT_FOUND4MSISDN);
                }
                // if login-id and msisdn both are present than check whether
                // they belong to same user or not.
                if ((!BTSLUtil.isNullString(_userLoginId) && !channelUserVO.getLoginID().equalsIgnoreCase(_userLoginId)) || (!BTSLUtil.isNullString(_userExtCode) && !_userExtCode.equalsIgnoreCase(channelUserVO.getExternalCode()))) {
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_LOGIN_ID_FOR_MSISDN);
                }
            }
            // this check ensures that when the _userLoginId is not null, a
            // check on the network is made so that any
            // request for login-Id outside the network is not entertained
            else if (!BTSLUtil.isNullString(_userLoginId)) {
                // load user details by login id
                channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(p_con, _userLoginId, null, statusUsed, status);
                if (channelUserVO == null) {
                    _requestMap.put("RES_ERR_KEY", XML_TAG_USERLOGINID);
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_CU_DETAILS_NOT_FOUND4LOGINID, _userLoginId);
                }
                if (!BTSLUtil.isNullString(_userExtCode)) {
                    if (!_userExtCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                        throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_CU_DETAILS_NOT_FOUND4LOGINID, _userExtCode);
                    }
                }
                filteredMsisdn = PretupsBL.getFilteredMSISDN(channelUserVO.getMsisdn());
            }
            // this is used for getting the user details on the basis of
            // External Code done in 5.1 and TZ version
            else if (!BTSLUtil.isNullString(_userExtCode)) {
                // load user details by External Code
                channelUserVO = channelUserDAO.loadChnlUserDetailsByExtCode(p_con, _userExtCode);
                if (channelUserVO == null) {
                    _requestMap.put("RES_ERR_KEY", XML_TAG_USEREXTCODE);
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_CU_DETAILS_NOT_FOUND4LOGINID, _userExtCode);
                }
                filteredMsisdn = PretupsBL.getFilteredMSISDN(channelUserVO.getMsisdn());
            }
            // if both _userLoginId and _msisdn are not present than throw
            // exception with status fail transaction Missing mandatory value
            else {
                _requestMap.put("RES_ERR_KEY", XML_TAG_MSISDN);
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            }

            msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
            _requestVO.setFilteredMSISDN(filteredMsisdn);
            // checking whether the msisdn prefix is valid in the network
            NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            if (networkPrefixVO == null) {
                arr = new String[] { filteredMsisdn };
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK, arr);
            }
            String networkCode = networkPrefixVO.getNetworkCode();
            if (networkCode != null && !networkCode.equalsIgnoreCase(((UserVO) _requestVO.getSenderVO()).getNetworkID())) {
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
            }

            return channelUserVO;
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("validate", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("validate", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalanceHandler[validate]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("UserBalanceHandler", "validate", PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("validate", "Exiting ");
            }
        }
    }

    /**
     * This method is called for loadUserBalance a request is received for a
     * user.
     * method loadUserBalance
     * 
     * @param p_con
     * @param channelUserVO
     */

    private void loadUserBalance(Connection p_con, ChannelUserVO channelUserVO) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserBalance";
        if (_log.isDebugEnabled()) {
            _log.debug("loadUserBalance", "Entered .....");
        }

        try {
            ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            ArrayList userBalanceList = null;
            ArrayList agentBalanceList = null;

            // checking whether agents are allowed for the user of this category
            // or not
            if (channelUserVO.getCategoryVO().getAgentAllowed().equalsIgnoreCase(PretupsI.AGENT_ALLOWED_YES)) {
                agentBalanceList = channelUserDAO.loadUserAgentsBalance(p_con, channelUserVO.getUserID());
            }

            // Load the user�s balances.
            userBalanceList = channelUserDAO.loadUserBalances(p_con, channelUserVO.getNetworkID(), channelUserVO.getNetworkID(), channelUserVO.getUserID());
            int userBalanceLists = userBalanceList.size();
            if (userBalanceList.size() > 0) {
                if (agentBalanceList != null && agentBalanceList.size() > 0) {
                	int agentBalanceLists = agentBalanceList.size();
                    for (int index = 0; index < agentBalanceLists; index++) {
                    	
                        for (int index1 = 0; index1 < userBalanceLists; index1++) {
                            // checking whether user product code is also
                            // available in agents product code list
                            // if yes than store agents balance in the users
                            // balance list
                            if (((UserBalancesVO) userBalanceList.get(index1)).getProductShortCode().equalsIgnoreCase(((UserBalancesVO) agentBalanceList.get(index)).getProductShortCode())) {
                                ((UserBalancesVO) userBalanceList.get(index1)).setAgentBalanceStr(PretupsBL.getDisplayAmount(((UserBalancesVO) agentBalanceList.get(index)).getBalance()));
                            }
                        }
                    }
                }
            } else {
                throw new BTSLBaseException(this, "loadUserBalance", PretupsErrorCodesI.CCE_XML_ERROR_USER_BALANCE_NOT_FOUND, "'");
            }
            _requestVO.setValueObject(userBalanceList);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("UserBalanceHandler ", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("loadUserBalance ", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalanceHandler[loadUserBalance ]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "UserBalanceHandler ", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("UserBalanceHandler ", "Exiting.....=");
        }
    }
}
