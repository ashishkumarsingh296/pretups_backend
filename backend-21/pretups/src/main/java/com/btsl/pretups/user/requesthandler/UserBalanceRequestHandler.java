package com.btsl.pretups.user.requesthandler;

/**
 * @(#)UserBalanceRequestHandler.java
 *                                    Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                    All Rights Reserved
 * 
 *                                    <description>
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Author Date History
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    manoj kumar june 22, 2005 Initital
 *                                    Creation
 *                                    Gurjeet Singh Bedi Dec 03,2005 Modified
 *                                    for PIN position changes
 *                                    Zafar Abbas May 05, 2008 Add method for
 *                                    User Balance for EXTGW
 *                                    Rahul Dutt jul 09,2012 SMS PIN Optional
 *                                    changes based on preference
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 * 
 */
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OracleUtil;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;

public class UserBalanceRequestHandler implements ServiceKeywordControllerI {
    private final Log _log = LogFactory.getLog(UserBalanceRequestHandler.class.getName());
    private ArrayList agentBalanceList = null;

    @Override
	public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered p_requestVO=" + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        ChannelUserDAO channelUserDAO = null;
        ChannelUserVO _channelUserVO;
        ArrayList userBalanceList = null;
        List userChildeBalanceList = null;
        boolean isotherbal = false;
        ChannelUserTxnDAO channelUserTxnDAO = null;
        try {
            channelUserTxnDAO = new ChannelUserTxnDAO();
            _channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            UserPhoneVO userPhoneVO = null;
            if (!_channelUserVO.isStaffUser()) {
                userPhoneVO = _channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = _channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }
            final String messageArr[] = p_requestVO.getRequestMessageArray();
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Message Array " + messageArr);
            }
            if (!BTSLUtil.isNullString(messageArr[0])) {
                final String msgArr[] = new String[] { p_requestVO.getActualMessageFormat() };
                if (messageArr.length < 1 || messageArr.length > 3) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_ERROR_USERBAL_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO
                                    .getActualMessageFormat() }, null);
                }
            }

            mcomCon = new MComConnection();con=mcomCon.getConnection();
            channelUserDAO = new ChannelUserDAO();
            userBalanceList = new ArrayList();
            userChildeBalanceList = new ArrayList();
            agentBalanceList = new ArrayList();
            if (messageArr.length == 1 ) {
            	// case when  (messageArr.length==1 irrespective of pin bypass check) 
                // self Balance
            	
	                if ((PretupsI.AGENT_ALLOWED.equalsIgnoreCase(_channelUserVO.getCategoryVO().getAgentAllowed()))) {
	                    // user have agent under itself
	                    agentBalanceList = channelUserDAO.loadUserAgentsBalance(con, _channelUserVO.getUserID());
	                }
                userChildeBalanceList = channelUserDAO.loadAllChildUserBalance(con,_channelUserVO.getUserID());
	                userBalanceList = channelUserDAO.loadUserBalances(con, _channelUserVO.getNetworkID(), _channelUserVO.getNetworkID(), _channelUserVO.getUserID());
	                if (userBalanceList.size() > 0) {
	                    // call the local method for the message formating
                    this.formatBalanceListForSMSForSelf(userBalanceList,userChildeBalanceList,   p_requestVO);
	                } else {
	                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.BAL_QUERY_SELF_BALANCE_LIST_NOTFOUND);
	                }
            	
            } else if (messageArr.length == 2) {
                // if((((ChannelUserVO)p_requestVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)
                // &&
                // !PretupsI.DEFAULT_C2S_PIN.equals(BTSLUtil.decryptText((((ChannelUserVO)p_requestVO.getSenderVO()).getUserPhoneVO()).getSmsPin())))
                ChannelUserVO chnlUserVO = null;
                // below condition added by rahul on 10 jun 2012 for bypassing
                // PIN validation based on gatewya type for preferece
                if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_PIN_BYPASS_GATEWAY_TYPE)).contains(p_requestVO.getRequestGatewayType()) || (PretupsI.MOBILE_APP_GATEWAY.equalsIgnoreCase(p_requestVO.getRequestGatewayType()) && PretupsI.YES.equalsIgnoreCase(Constants.getProperty("MAPPGW_PIN_BYPASS")))) {
                    if (messageArr[1].length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue()) {
                        chnlUserVO = channelUserTxnDAO.loadOtherUserBalanceVO(con, PretupsBL.getFilteredMSISDN(messageArr[1]), _channelUserVO);
                        if (chnlUserVO.getUserName() != null) {
                            isotherbal = true;
                            chnlUserVO.setUserCode(messageArr[1]);
                            if (PretupsI.CATEGORY_TYPE_AGENT.equals(_channelUserVO.getCategoryVO().getCategoryType()) && chnlUserVO.getCategoryCode().equals(
                                            _channelUserVO.getCategoryCode())) {
                                final String[] arr = new String[1];
                                arr[0] = chnlUserVO.getUserCode();
                                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.BAL_AGENT_NOT_SEEN_SAME_LEVEL, 0, arr, null);
                            }
                        } else {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.BAL_QUERY_OTHER_BALANCE_CHECK_NOT_ALLOWED);
                        }
                    }
                } else if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                    try {
                        ChannelUserBL.validatePIN(con, ((ChannelUserVO) p_requestVO.getSenderVO()), messageArr[1]);
                    } catch (BTSLBaseException be) {
                        _log.errorTrace(METHOD_NAME, be);
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                            OracleUtil.commit(con);
                        }
                        _log.error("process", "BTSLBaseException " + be);
                        throw be;
                    }
                }
                // self Balance
                // if((_channelUserVO.getCategoryVO()).getAgentAllowed().equalsIgnoreCase(PretupsI.AGENT_ALLOWED))
                if (PretupsI.AGENT_ALLOWED.equalsIgnoreCase((_channelUserVO.getCategoryVO()).getAgentAllowed())) {
                    if (isotherbal) {
                        agentBalanceList = channelUserDAO.loadUserAgentsBalance(con, chnlUserVO.getUserID());
                    } else {
                        // user have agent under itself
                        agentBalanceList = channelUserDAO.loadUserAgentsBalance(con, _channelUserVO.getUserID());
                    }
                }
                if (isotherbal) {
                    userBalanceList = channelUserDAO.loadUserBalances(con, _channelUserVO.getNetworkID(), _channelUserVO.getNetworkID(), chnlUserVO.getUserID());
                    userChildeBalanceList = channelUserDAO.loadAllChildUserBalance(con,chnlUserVO.getUserID());
                } else {
                    userBalanceList = channelUserDAO.loadUserBalances(con, _channelUserVO.getNetworkID(), _channelUserVO.getNetworkID(), _channelUserVO.getUserID());
                    userChildeBalanceList = channelUserDAO.loadAllChildUserBalance(con,_channelUserVO.getUserID());
                }

                final String[] arr1 = new String[2];
                arr1[0] = _channelUserVO.getUserCode();
                long balance = 0;

                // if message gateway is EXTGW : added by zafar.abbas
                int   userBalanceListSize = userBalanceList.size();
                for (int i = 0; i < userBalanceListSize; i++) {
                    final UserBalancesVO userBalanceVO = (UserBalancesVO) userBalanceList.get(i);
                    balance = balance + userBalanceVO.getBalance();
                }
                arr1[1] = String.valueOf(PretupsBL.getDisplayAmount(balance));

                if (userBalanceList.size() > 0 && !isotherbal) {
                    // if message gateway is EXTGW : added by zafar.abbas
                    if (p_requestVO.getMessageGatewayVO().getGatewayType().equals(PretupsI.REQUEST_SOURCE_TYPE_EXTGW)||p_requestVO.getMessageGatewayVO().getGatewayType().equals(PretupsI.REQUEST_SOURCE_TYPE_REST)) {
                        p_requestVO.setValueObject(userBalanceList);
                        p_requestVO.setMessageArguments(arr1);
                        p_requestVO.setMessageCode(PretupsErrorCodesI.BAL_QUERY_OTHER_BALANCE_LIST_SUCCESS);
                    } else if (p_requestVO.getMessageGatewayVO().getGatewayType().equals(PretupsI.GATEWAY_TYPE_PHYSICAL_POS)) {
                        p_requestVO.setValueObject(userBalanceList);
                        this.formatBalanceListForSMSForSelf(userBalanceList,userChildeBalanceList,  p_requestVO);
                    } else {
                        // call the local method for the message formating
                        this.formatBalanceListForSMSForSelf(userBalanceList,userChildeBalanceList, p_requestVO);
                    }
                } else if (userBalanceList.size() > 0 && isotherbal) {
                    if (p_requestVO.getMessageGatewayVO().getGatewayType().equals(PretupsI.REQUEST_SOURCE_TYPE_EXTGW)) {
                        p_requestVO.setValueObject(userBalanceList);
                        p_requestVO.setMessageCode(PretupsErrorCodesI.BAL_QUERY_OTHER_BALANCE_LIST_SUCCESS);
                    } else if (p_requestVO.getMessageGatewayVO().getGatewayType().equals(PretupsI.GATEWAY_TYPE_PHYSICAL_POS)) {
                        p_requestVO.setValueObject(userBalanceList);
                        this.formatBalanceListForSMSForOtherBlance(userBalanceList, p_requestVO, chnlUserVO);
                    } else {
                        this.formatBalanceListForSMSForOtherBlance(userBalanceList, p_requestVO, chnlUserVO);
                    }
                } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.BAL_QUERY_SELF_BALANCE_LIST_NOTFOUND);
                }
            } else if (messageArr.length > 2) {
                // if((((ChannelUserVO)p_requestVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)
                // &&
                // !PretupsI.DEFAULT_C2S_PIN.equals(BTSLUtil.decryptText((((ChannelUserVO)p_requestVO.getSenderVO()).getUserPhoneVO()).getSmsPin())))
                if (userPhoneVO.getPinRequired().equals(PretupsI.YES) && !((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_PIN_BYPASS_GATEWAY_TYPE)).contains(p_requestVO.getRequestGatewayType())) {
                    try {
                        ChannelUserBL.validatePIN(con, ((ChannelUserVO) p_requestVO.getSenderVO()), messageArr[2]);
                    } catch (BTSLBaseException be) {
                        _log.errorTrace(METHOD_NAME, be);
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                            OracleUtil.commit(con);
                        }
                        throw be;
                    }
                }
                // other channel user balance detail
                ChannelUserVO chnlUserVO = new ChannelUserVO();
                chnlUserVO = channelUserTxnDAO.loadOtherUserBalanceVO(con, PretupsBL.getFilteredMSISDN(messageArr[1]), _channelUserVO);
                if (chnlUserVO.getUserName() != null) {
                    chnlUserVO.setUserCode(messageArr[1]);
                    // add by ved 16-mar-2007, this condition checks agent can
                    // not seen the same level agent balance.
                    if (PretupsI.CATEGORY_TYPE_AGENT.equals(_channelUserVO.getCategoryVO().getCategoryType()) && chnlUserVO.getCategoryCode().equals(
                                    _channelUserVO.getCategoryCode())) {
                        final String[] arr = new String[1];
                        arr[0] = chnlUserVO.getUserCode();
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.BAL_AGENT_NOT_SEEN_SAME_LEVEL, 0, arr, null);
                    }
                    if (PretupsI.AGENT_ALLOWED.equalsIgnoreCase((_channelUserVO.getCategoryVO()).getAgentAllowed())) {
                        // user have agent under itself
                        agentBalanceList = channelUserDAO.loadUserAgentsBalance(con, chnlUserVO.getUserID());
                    }
                    userBalanceList = channelUserDAO.loadUserBalances(con, _channelUserVO.getNetworkID(), _channelUserVO.getNetworkID(), chnlUserVO.getUserID());
                    final String[] arr1 = new String[2];
                    arr1[0] = chnlUserVO.getUserCode();
                    long balance = 0;

                    // if message gateway is EXTGW : added by zafar.abbas
                    for (int i = 0; i < userBalanceList.size(); i++) {
                        final UserBalancesVO userBalanceVO = (UserBalancesVO) userBalanceList.get(i);
                        balance = balance + userBalanceVO.getBalance();
                    }
                    arr1[1] = String.valueOf(PretupsBL.getDisplayAmount(balance));
                    if (userBalanceList.size() > 0) {
                        // if message gateway is EXTGW : added by zafar.abbas
                        if (p_requestVO.getMessageGatewayVO().getGatewayType().equals(PretupsI.REQUEST_SOURCE_TYPE_EXTGW)) {
                            p_requestVO.setValueObject(userBalanceList);
                            p_requestVO.setMessageArguments(arr1);
                            p_requestVO.setMessageCode(PretupsErrorCodesI.BAL_QUERY_OTHER_BALANCE_LIST_SUCCESS);
                        } else if (p_requestVO.getMessageGatewayVO().getGatewayType().equals(PretupsI.GATEWAY_TYPE_PHYSICAL_POS)) {
                            p_requestVO.setValueObject(userBalanceList);
                            this.formatBalanceListForSMSForOtherBlance(userBalanceList, p_requestVO, chnlUserVO);
                        } else {
                            // call teh BL to fomr the array according to
                            // message format
                            this.formatBalanceListForSMSForOtherBlance(userBalanceList, p_requestVO, chnlUserVO);
                        }
                    } else {
                        final String[] arr = new String[1];
                        arr[0] = chnlUserVO.getUserCode();
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.BAL_QUERY_NO_TRANSFER_HAS_BEEN_DONE, 0, arr, null);
                    }
                } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.BAL_QUERY_OTHER_BALANCE_CHECK_NOT_ALLOWED);
                }
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            OracleUtil.rollbackConnection(con, UserBalanceRequestHandler.class.getName(), METHOD_NAME);
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UserBalanceRequestHandler[process]","","","","BTSL Exception:"+be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return;
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            OracleUtil.rollbackConnection(con, UserBalanceRequestHandler.class.getName(), METHOD_NAME);
            _log.error("process", "BTSLBaseException " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalanceRequestHandler[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
			if (mcomCon != null) {
				mcomCon.close("UserBalanceRequestHandler#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }

    /**
     * to get the Balance list of product for self
     * 
     * @param p_balanceList
     *            java.util.ArrayList
     * @param p_requestVO
     *            RequestVO
     * @throws BTSLBaseException
     * @author manoj kumar
     */
    private void formatBalanceListForSMSForSelf(ArrayList p_balanceList,List p_ChildBalanceList, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "formatBalanceListForSMSForSelf";
        if (_log.isDebugEnabled()) {
            _log.debug("formatBalanceListForSMSForSelf", "Entered: p_balanceList:" + p_balanceList.size() + " ,p_requestVO=" + p_requestVO.toString());
        }
        String[] balanceListArr = null;
        try {
            if (p_balanceList != null) {
                balanceListArr = new String[p_balanceList.size()];
            }
            final StringBuffer sbf = new StringBuffer();
            final String[] arr = new String[3];
            String networkCode = null;
            if (p_balanceList != null && p_balanceList.size() > 0) {
                final ArrayList argumentVOList = new ArrayList();
                KeyArgumentVO argumentVO = null;
                int balanceListSize = p_balanceList.size();
                for (int i = 0, k = balanceListSize; i < k; i++) {
                    final UserBalancesVO userBalanceVO = (UserBalancesVO) p_balanceList.get(i);
                    argumentVO = new KeyArgumentVO();
                    if (i == 0) {
                        final String[] productArr = new String[5];
                        networkCode = userBalanceVO.getNetworkCode();
                        productArr[0] = userBalanceVO.getNetworkCode();
                        productArr[1] = userBalanceVO.getProductShortName();
                        productArr[2] = PretupsBL.getDisplayAmount(userBalanceVO.getBalance());
                        productArr[3] = userBalanceVO.getProductShortCode();
                        productArr[4] = userBalanceVO.getProductName();
                        argumentVO.setKey(PretupsErrorCodesI.BAL_QUERY_SELF_USR_PRODUCT_CODE_WITH_NETWORK_MSG);
                        argumentVO.setArguments(productArr);
                        argumentVOList.add(argumentVO);
                    } else {
                        final String[] productArr = new String[5];
                        networkCode = userBalanceVO.getNetworkCode();
                        productArr[0] = userBalanceVO.getNetworkCode();
                        productArr[1] = userBalanceVO.getProductShortName();
                        productArr[2] = PretupsBL.getDisplayAmount(userBalanceVO.getBalance());
                        productArr[3] = userBalanceVO.getProductShortCode();
                        productArr[4] = userBalanceVO.getProductName();
                        argumentVO.setKey(PretupsErrorCodesI.BAL_QUERY_SELF__USR_PRODUCT_MSG);
                        argumentVO.setArguments(productArr);
                        argumentVOList.add(argumentVO);
                    }
                }
                arr[0] = BTSLUtil.getMessage(p_requestVO.getSenderLocale(), argumentVOList);
                arr[2]  = "0";
                if (p_ChildBalanceList != null && p_ChildBalanceList.size() > 0) {
               	 final String[] childArr = new String[5];
                    final ArrayList childArgumentVOList = new ArrayList();
                    KeyArgumentVO delrArgumentVO;
                    int childBalanceListSize = p_ChildBalanceList.size();
                    for (int i = 0; i < childBalanceListSize; i++) {
                        delrArgumentVO = new KeyArgumentVO();
                        final UserBalancesVO userBalanceVO = (UserBalancesVO) p_ChildBalanceList.get(i);
                        childArr[0] = networkCode;
                        childArr[1] = userBalanceVO.getProductShortName();
                        childArr[2] = String.valueOf(PretupsBL.getDisplayAmount(userBalanceVO.getBalance()));
                        childArr[3] = userBalanceVO.getProductShortCode();
                        childArr[4] = userBalanceVO.getProductName();
                        delrArgumentVO.setKey(PretupsErrorCodesI.BAL_QUERY_SELF_USR_PRODUCT_CODE_WITH_NETWORK_MSG);
                        delrArgumentVO.setArguments(childArr);
                        childArgumentVOList.add(delrArgumentVO);
                    }
                    arr[2] = BTSLUtil.getMessage(p_requestVO.getSenderLocale(), childArgumentVOList);
               }
                // if user have agnet balance
                if (agentBalanceList != null && agentBalanceList.size() > 0) {
                    final String[] agentArr = new String[3];
                    final ArrayList agentargumentVOList = new ArrayList();
                    KeyArgumentVO agentArgumentVO;
                    int agentBalanceListSize = agentBalanceList.size();
                    for (int i = 0; i < agentBalanceListSize; i++) {
                        agentArgumentVO = new KeyArgumentVO();
                        final UserBalancesVO userBalanceVO = (UserBalancesVO) agentBalanceList.get(i);
                        // System.out.println("agentnetworkCode>>>>>>>>>>>"+networkCode);
                        agentArr[0] = networkCode;
                        agentArr[1] = userBalanceVO.getProductShortName();
                        agentArr[2] = String.valueOf(PretupsBL.getDisplayAmount(userBalanceVO.getBalance()));
                        agentArgumentVO.setKey(PretupsErrorCodesI.BAL_QUERY_AGENT_BALANCE_SUCCESSS);
                        agentArgumentVO.setArguments(agentArr);
                        agentargumentVOList.add(agentArgumentVO);
                    }
                    arr[1] = BTSLUtil.getMessage(p_requestVO.getSenderLocale(), agentargumentVOList);
                    p_requestVO.setMessageArguments(arr);
                    p_requestVO.setMessageCode(PretupsErrorCodesI.BAL_QUERY_SELF_BALANCE_LIST_SUCCESS_WITH_USERAGENT);
                } else {
                    p_requestVO.setMessageArguments(arr);
                    p_requestVO.setMessageCode(PretupsErrorCodesI.BAL_QUERY_SELF_BALANCE_LIST_SUCCESS);
                }

            }
        } catch (Exception e) {
            _log.error("formatBalanceListForSMSForSelf", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "UserBalanceRequestHandler[formatBalanceListForSMSForSelf]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("UserBalanceRequestHandler", "formatBalanceListForSMSForSelf", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatBalanceListForSMSForSelf", "Exited: ");
            }
        }
    }

    /**
     * to get the Balance list of other user
     * 
     * @param p_balanceList
     *            java.util.ArrayList
     * @param p_requestVO
     * @param p_channelUserVO
     * @throws BTSLBaseException
     * @author manoj kumar
     */
    private void formatBalanceListForSMSForOtherBlance(ArrayList p_balanceList, RequestVO p_requestVO, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        final String METHOD_NAME = "formatBalanceListForSMSForOtherBlance";
        if (_log.isDebugEnabled()) {
            _log.debug("formatBalanceListForSMSForOtherBlance", "Entered: p_balanceList:" + p_balanceList + ", p_requestVO=" + p_requestVO.toString() + " " + p_channelUserVO
                            .toString());
        }
        try {
            final String[] arr = new String[4];
            String networkCode = null;
            if (p_balanceList != null && p_balanceList.size() > 0) {
                final ArrayList argumentVOList = new ArrayList();
                KeyArgumentVO argumentVO = null;
                arr[0] = p_channelUserVO.getUserCode();
                int   balanceListSize = p_balanceList.size();
                for (int i = 0, k = balanceListSize; i < k; i++) {
                    final UserBalancesVO userBalanceVO = (UserBalancesVO) p_balanceList.get(i);
                    argumentVO = new KeyArgumentVO();
                    if (i == 0) {
                        final String[] productArr = new String[3];//
                        networkCode = userBalanceVO.getNetworkCode();
                        productArr[0] = userBalanceVO.getNetworkCode();
                        productArr[1] = userBalanceVO.getProductShortName();
                        productArr[2] = PretupsBL.getDisplayAmount(userBalanceVO.getBalance());
                        argumentVO.setKey(PretupsErrorCodesI.BAL_QUERY_OTHER__USR_PRODUCT_WITH_NETWORK_MSG);
                        argumentVO.setArguments(productArr);
                        argumentVOList.add(argumentVO);
                    } else {
                        final String[] productArr = new String[2];//
                        productArr[0] = userBalanceVO.getProductShortName();
                        productArr[1] = PretupsBL.getDisplayAmount(userBalanceVO.getBalance());
                        argumentVO.setKey(PretupsErrorCodesI.BAL_QUERY_OTHER__USR_PRODUCT_MSG);
                        argumentVO.setArguments(productArr);
                        argumentVOList.add(argumentVO);
                    }
                }
                arr[1] = BTSLUtil.getMessage(p_requestVO.getSenderLocale(), argumentVOList);
                if (agentBalanceList != null && agentBalanceList.size() > 0) {
                    final String[] agentArr = new String[3];
                    final ArrayList agentargumentVOList = new ArrayList();
                    KeyArgumentVO agentArgumentVO;
                    int agentBalanceListSize = agentBalanceList.size();
                    for (int i = 0; i < agentBalanceListSize; i++) {
                        agentArgumentVO = new KeyArgumentVO();
                        final UserBalancesVO userBalanceVO = (UserBalancesVO) agentBalanceList.get(i);
                        agentArr[0] = networkCode;
                        agentArr[1] = userBalanceVO.getProductShortName();
                        agentArr[2] = String.valueOf(PretupsBL.getDisplayAmount(userBalanceVO.getBalance()));
                        agentArgumentVO.setKey(PretupsErrorCodesI.BAL_QUERY_OTHER_AGENT_BALANCE_SUCCESSS);
                        agentArgumentVO.setArguments(agentArr);
                        agentargumentVOList.add(agentArgumentVO);
                    }
                    arr[2] = BTSLUtil.getMessage(p_requestVO.getSenderLocale(), agentargumentVOList);
                    p_requestVO.setMessageArguments(arr);
                    p_requestVO.setMessageCode(PretupsErrorCodesI.BAL_QUERY_OTHER_BALANCE_LIST_SUCCESS_WITH_USERAGENT);
                } else {
                    p_requestVO.setMessageArguments(arr);
                    p_requestVO.setMessageCode(PretupsErrorCodesI.BAL_QUERY_OTHER_BALANCE_LIST_SUCCESS);
                }
            }
        } catch (Exception e) {
            _log.error("formatBalanceListForSMSForOtherBlance", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "UserBalanceRequestHandler[formatBalanceListForSMSForOtherBlance]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("UserBalanceRequestHandler", "formatBalanceListForSMSForOtherBlance", PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatBalanceListForSMSForOtherBlance", "Exited: size =");
            }
        }
    }
}
