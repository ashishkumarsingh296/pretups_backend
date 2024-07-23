package com.btsl.pretups.user.requesthandler;

/**
 * @(#)DailyTransferRequestHandler.java
 *                                      Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                      All Rights Reserved
 * 
 *                                      <description>
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Author Date History
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      manoj kumar june 22, 2005 Initital
 *                                      Creation
 *                                      Gurjeet Singh Bedi Dec 03,2005 Modified
 *                                      for PIN position changes
 *                                      manisha jain Dec 31, 2009 Modification
 *                                      for PIN change for Staff user
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      ----------------
 * 
 *                                      if requested MSISDN is of a channel user
 *                                      then send details of all the
 *                                      transactions
 *                                      that is done by channel user and its
 *                                      staff user to the channel user. If
 *                                      request MSISDN is of a staff user
 *                                      then send details of all the
 *                                      transactions that is done by staff user
 */
import java.sql.Connection;
// import java.sql.Date;
// import java.sql.Date;
import java.util.ArrayList;
import java.util.Date;
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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.ChannelTransfrsReturnsVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;

public class DailyTransferRequestHandler implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(DailyTransferRequestHandler.class.getName());

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered " + p_requestVO);
        }
		Connection con = null;
		MComConnectionI mcomCon = null;
        ChannelUserTxnDAO channelUserTxnDAO = null;
        ChannelUserVO _channelUserVO;
        ArrayList channelIntransferList = null;
        ArrayList channelOutTransferList = null;
        ArrayList subscriberOutTransferList = null;
        ArrayList channelOutChildTransferList = null;
        final boolean balanceCheck = false;
        final String chechBalanceType = null;
        UserPhoneVO userPhoneVO = null;
        boolean otherEnq = false;
        try {
            channelUserTxnDAO = new ChannelUserTxnDAO();
            _channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            if (!_channelUserVO.isStaffUser()) {
                userPhoneVO = _channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = _channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }

            final String messageArr[] = p_requestVO.getRequestMessageArray();
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Message Array " + messageArr.length);
            }
            if (messageArr.length < 1 || messageArr.length > 3) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_ERROR_DA_TRANSFER_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO
                                .getActualMessageFormat() }, null);
            }
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            if (messageArr.length == 1 && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_PIN_BYPASS_GATEWAY_TYPE)).contains(p_requestVO.getRequestGatewayType())) {
                final Date todaysDate = new Date();
                channelIntransferList = new ArrayList();
 
                channelIntransferList = channelUserTxnDAO.loadUserChannelInTransferList(con, _channelUserVO, todaysDate);
              
                channelOutChildTransferList = channelUserTxnDAO.loadChannelUserOutChildTransferList(con, _channelUserVO , todaysDate);
                channelOutTransferList = channelUserTxnDAO.loadUserChannelOutTransferList(con, _channelUserVO, todaysDate);
                subscriberOutTransferList = channelUserTxnDAO.loadUserSubscriberOutTransferList(con, _channelUserVO, todaysDate);
                if (channelIntransferList.size() > 0 || channelOutTransferList.size() > 0 || subscriberOutTransferList.size() > 0) {

                    this.formatTransferListForSMSForSelf(channelIntransferList, channelOutTransferList,channelOutChildTransferList, subscriberOutTransferList, p_requestVO);
                } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.DAILY_TRANSFER_LIST_NOTFOUND);
                }

            } else if (messageArr.length == 2) {
                ChannelUserVO chnlUserVO = null;
                // if((((ChannelUserVO)p_requestVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)
                // &&
                // !PretupsI.DEFAULT_C2S_PIN.equals(BTSLUtil.decryptText((((ChannelUserVO)p_requestVO.getSenderVO()).getUserPhoneVO()).getSmsPin())))
                // below condition added by rahul on 10 jun 2012 for bypassing
                // PIN validation based on gatewya type for preferece
                if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_PIN_BYPASS_GATEWAY_TYPE)).contains(p_requestVO.getRequestGatewayType())) {
                    try {
                        if (messageArr[1].length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue()) {
                            if (BTSLUtil.isNumeric(messageArr[1])) {
                                messageArr[1] = PretupsBL.getFilteredMSISDN(messageArr[1]);
                            } else {
                                messageArr[1] = messageArr[1];
                            }
                            chnlUserVO = channelUserTxnDAO.loadOtherUserBalanceVO(con, messageArr[1], _channelUserVO);
                            if (chnlUserVO.getUserName() != null) {
                                _channelUserVO.setUserID(chnlUserVO.getUserID());
                                _channelUserVO.setUserCode(messageArr[1]);
                                otherEnq = true;
                            } else {
                                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.DAILY_OTHER_TRANSFER_LIST_NOTFOUND);
                            }
                        }
                    } catch (Exception e) {
                        _log.error("process", "Exception" + e);
                    }
                } else if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                    try {
                        ChannelUserBL.validatePIN(con, ((ChannelUserVO) p_requestVO.getSenderVO()), messageArr[1]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                        	mcomCon.finalCommit();
                        }
                        _log.error("process", "BTSLBaseException " + be);
                        throw be;
                    }
                }
                // self Balance
                final Date todaysDate = new Date();
                channelIntransferList = new ArrayList();

                channelIntransferList = channelUserTxnDAO.loadUserChannelInTransferList(con, _channelUserVO, todaysDate);
                // }

                channelOutChildTransferList = channelUserTxnDAO.loadChannelUserOutChildTransferList(con, _channelUserVO , todaysDate);
                channelOutTransferList = channelUserTxnDAO.loadUserChannelOutTransferList(con, _channelUserVO, todaysDate);
                subscriberOutTransferList = channelUserTxnDAO.loadUserSubscriberOutTransferList(con, _channelUserVO, todaysDate);
                if (otherEnq && (channelIntransferList.size() > 0 || channelOutTransferList.size() > 0 || subscriberOutTransferList.size() > 0)) {
                    this.formatTransferListForSMSForOtherUser(channelIntransferList, channelOutTransferList,channelOutChildTransferList, subscriberOutTransferList, p_requestVO, chnlUserVO);
                } else if (channelIntransferList.size() > 0 || channelOutTransferList.size() > 0 || subscriberOutTransferList.size() > 0) {

                    this.formatTransferListForSMSForSelf(channelIntransferList, channelOutTransferList, channelOutChildTransferList , subscriberOutTransferList, p_requestVO);
                } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.DAILY_TRANSFER_LIST_NOTFOUND);
                }

            } else if (messageArr.length > 2) {
                if ((((ChannelUserVO) p_requestVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES) && !((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_PIN_BYPASS_GATEWAY_TYPE))
                                .contains(p_requestVO.getRequestGatewayType())) {
                    try {
                        ChannelUserBL.validatePIN(con, ((ChannelUserVO) p_requestVO.getSenderVO()), messageArr[2]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                        	mcomCon.finalCommit();
                        }
                        throw be;
                    }
                }
                // other channel user balance detail
                ChannelUserVO chnlUserVO = new ChannelUserVO();
                if (BTSLUtil.isNumeric(messageArr[1])) {
                    messageArr[1] = PretupsBL.getFilteredMSISDN(messageArr[1]);
                } else {
                    messageArr[1] = messageArr[1];
                }
                chnlUserVO = channelUserTxnDAO.loadOtherUserBalanceVO(con, messageArr[1], _channelUserVO);

                if (chnlUserVO.getUserName() != null) {
                    _channelUserVO.setUserID(chnlUserVO.getUserID());
                    _channelUserVO.setUserCode(messageArr[1]);

                    final Date todaysDate = new Date();
                    channelIntransferList = channelUserTxnDAO.loadUserChannelInTransferList(con, _channelUserVO, todaysDate);
                    channelOutTransferList = channelUserTxnDAO.loadUserChannelOutTransferList(con, _channelUserVO, todaysDate);
                    channelOutChildTransferList = channelUserTxnDAO.loadChannelUserOutChildTransferList(con, _channelUserVO , todaysDate);
                    subscriberOutTransferList = channelUserTxnDAO.loadUserSubscriberOutTransferList(con, chnlUserVO, todaysDate);
                    if (channelIntransferList.size() > 0 || channelOutTransferList.size() > 0 || subscriberOutTransferList.size() > 0) {
                        this.formatTransferListForSMSForOtherUser(channelIntransferList, channelOutTransferList,channelOutChildTransferList, subscriberOutTransferList, p_requestVO, chnlUserVO);
                    } else {
                        final String[] arr = new String[1];
                        arr[0] = messageArr[1];
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.DAILY_OTHER_TRANSFER_LIST_NOTFOUND, 0, arr, null);
                    }
                } else {
                	final String[] arr = new String[1];
                    arr[0] = messageArr[1];
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_INVALID_MSISDN,0, arr, null);
                }
            }
            HashMap map = p_requestVO.getRequestMap();
            if (map == null) {
                map = new HashMap();
            }
            map.put("CHANNEL_IN_TRANSFER", channelIntransferList);
            map.put("CHANNEL_OUT_TRANSFER", channelOutTransferList);
            map.put("SUBSCRIBER_OUT_TRANSFER", subscriberOutTransferList);

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.error("process", "BTSLBaseException " + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return;
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "BTSLBaseException " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyTransferRequestHandler[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("DailyTransferRequestHandler#process");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }

    /**
     * @param p_balanceList
     *            java.util.ArrayList
     * @param p_requestVO
     *            RequestVO
     * @throws BTSLBaseException
     * @author manoj kumar
     */
    

    private void formatTransferListForSMSForSelf(ArrayList p_channelIntransferList, ArrayList p_channelOutTransferList, ArrayList p_channelOutChildTransferList , ArrayList p_subscriberOutTransferList, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "formatTransferListForSMSForSelf";
        if (_log.isDebugEnabled()) {
            _log.debug("formatTransferListForSMSForSelf",
                            "Entered: p_channelIntransferList:" + p_channelIntransferList.size() + ", p_channelOutTransferList=" + p_channelOutTransferList.size() + " p_channelOutChildTransferList : "+p_channelOutChildTransferList +",p_subscriberOutTransferList=" + p_subscriberOutTransferList
                                            .size() + " ,p_requestVO=" + p_requestVO.toString());
        }
        final String[] balanceListArr = null;
        try {
            final String[] arr = new String[4];
            final ArrayList argumentVOListForTransferIn = new ArrayList();
            final ArrayList argumentVOListForTransferOut = new ArrayList();
            final ArrayList argumentVOListForChildTransferOut = new ArrayList();
            final ArrayList argumentVOListForSubsciberOut = new ArrayList();
            if (p_channelIntransferList.size() > 0) {
                KeyArgumentVO argumentVO = null;
                for (int i = 0, k = p_channelIntransferList.size(); i < k; i++) {
                    final ChannelTransfrsReturnsVO channelTransfrsInVO = (ChannelTransfrsReturnsVO) p_channelIntransferList.get(i);
                    argumentVO = new KeyArgumentVO();
                    final String[] channelTransferArr = new String[3];//
                    channelTransferArr[0] = channelTransfrsInVO.getShortName();
                    channelTransferArr[1] = PretupsBL.getDisplayAmount(channelTransfrsInVO.getTransfes());
                    channelTransferArr[2] = PretupsBL.getDisplayAmount(channelTransfrsInVO.getReturns());
                    argumentVO.setKey(PretupsErrorCodesI.DAILY_CHANNEL_TRANSFER_IN__PRODUCT_MSG);
                    argumentVO.setArguments(channelTransferArr);
                    argumentVOListForTransferIn.add(argumentVO);
                }
            }
            if (p_channelOutTransferList.size() > 0) {
                KeyArgumentVO argumentVO = null;
                for (int i = 0, k = p_channelOutTransferList.size(); i < k; i++) {
                    final ChannelTransfrsReturnsVO channelTransfrsInVO = (ChannelTransfrsReturnsVO) p_channelOutTransferList.get(i);
                    argumentVO = new KeyArgumentVO();
                    final String[] channelTransferArr = new String[3];//
                    channelTransferArr[0] = channelTransfrsInVO.getShortName();
                    channelTransferArr[1] = PretupsBL.getDisplayAmount(channelTransfrsInVO.getTransfes());
                    channelTransferArr[2] = PretupsBL.getDisplayAmount(channelTransfrsInVO.getReturns());
                    argumentVO.setKey(PretupsErrorCodesI.DAILY_CHANNEL_TRANSFER_OUT__PRODUCT_MSG);
                    argumentVO.setArguments(channelTransferArr);
                    argumentVOListForTransferOut.add(argumentVO);
                }
            }
            if (p_channelOutChildTransferList.size() > 0) {
                KeyArgumentVO argumentVO = null;
                for (int i = 0, k = p_channelOutChildTransferList.size(); i < k; i++) {
                    final ChannelTransfrsReturnsVO channelTransfrsOutChildVO = (ChannelTransfrsReturnsVO) p_channelOutChildTransferList.get(i);
                    argumentVO = new KeyArgumentVO();
                    final String[] channelTransferChildArr = new String[3];//
                    channelTransferChildArr[0] = channelTransfrsOutChildVO.getShortName();
                    channelTransferChildArr[1] = PretupsBL.getDisplayAmount(channelTransfrsOutChildVO.getTransfes());
                    channelTransferChildArr[2] = PretupsBL.getDisplayAmount(channelTransfrsOutChildVO.getReturns());
                    argumentVO.setKey(PretupsErrorCodesI.DAILY_CHANNEL_TRANSFER_OUT__PRODUCT_MSG);
                    argumentVO.setArguments(channelTransferChildArr);
                    argumentVOListForChildTransferOut.add(argumentVO);
                }
            }
            if (p_subscriberOutTransferList.size() > 0) {
                KeyArgumentVO argumentVO = null;
                for (int i = 0, k = p_subscriberOutTransferList.size(); i < k; i++) {
                    final ChannelTransfrsReturnsVO channelTransfrsInVO = (ChannelTransfrsReturnsVO) p_subscriberOutTransferList.get(i);
                    argumentVO = new KeyArgumentVO();
                    final String[] channelTransferArr = new String[3];//
                    channelTransferArr[0] = channelTransfrsInVO.getServiceName();
                    channelTransferArr[1] = channelTransfrsInVO.getShortName();
                    channelTransferArr[2] = PretupsBL.getDisplayAmount(channelTransfrsInVO.getTransfes());
                    argumentVO.setKey(PretupsErrorCodesI.DAILY_SUBSCRIBER_TRANSFER_OUT_PRODUCT_MSG);
                    argumentVO.setArguments(channelTransferArr);
                    argumentVOListForSubsciberOut.add(argumentVO);
                }
            }
            arr[0] = BTSLUtil.getMessage(p_requestVO.getSenderLocale(), argumentVOListForTransferIn);
            arr[1] = BTSLUtil.getMessage(p_requestVO.getSenderLocale(), argumentVOListForTransferOut);
            arr[2] = BTSLUtil.getMessage(p_requestVO.getSenderLocale(), argumentVOListForChildTransferOut);
            arr[3] = BTSLUtil.getMessage(p_requestVO.getSenderLocale(), argumentVOListForSubsciberOut);
            p_requestVO.setMessageArguments(arr);
            p_requestVO.setMessageCode(PretupsErrorCodesI.SELF_DAILY_TRANSFER_LIST_SUCCESS);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("formatBalanceListForSMSForSelf", "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "DailyTransferRequestHandler[formatBalanceListForSMSForSelf]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("DailyTransferRequestHandler", "formatBalanceListForSMSForSelf", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatBalanceListForSMSForSelf", "Exited: ");
            }
        }
    }

    /**
     * 
     * @param p_balanceList
     *            java.util.ArrayList
     * @param p_requestVO
     *            RequestVO
     * @throws BTSLBaseException
     * @author manoj kumar
     */
    private void formatTransferListForSMSForOtherUser(ArrayList p_channelIntransferList, ArrayList p_channelOutTransferList, ArrayList p_channelOutChildTransferList , ArrayList p_subscriberOutTransferList, RequestVO p_requestVO, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        final String METHOD_NAME = "formatTransferListForSMSForOtherUser";
        if (_log.isDebugEnabled()) {
            _log.debug("formatTransferListForSMSForOtherUser",
                            "Entered: p_channelIntransferList:" + p_channelIntransferList.size() + ",p_channelOutTransferList=" + p_channelOutTransferList.size() + ",p_subscriberOutTransferList=" + p_subscriberOutTransferList
                                            .size() + " ,p_requestVO=" + p_requestVO.toString() + ",p_channelUserVO=" + p_channelUserVO.toString());
        }
        final String[] balanceListArr = null;
        try {
            final String[] arr = new String[5];
            final ArrayList argumentVOListForTransferIn = new ArrayList();
            final ArrayList argumentVOListForTransferOut = new ArrayList();
            final ArrayList argumentVOListForChildTransferOut = new ArrayList();
            final ArrayList argumentVOListForSubsciberOut = new ArrayList();
            if (p_channelIntransferList != null && p_channelIntransferList.size() > 0) {
                KeyArgumentVO argumentVO = null;
                for (int i = 0, k = p_channelIntransferList.size(); i < k; i++) {
                    final ChannelTransfrsReturnsVO channelTransfrsInVO = (ChannelTransfrsReturnsVO) p_channelIntransferList.get(i);
                    argumentVO = new KeyArgumentVO();
                    final String[] channelTransferArr = new String[3];
                    channelTransferArr[0] = channelTransfrsInVO.getShortName();
                    channelTransferArr[1] = PretupsBL.getDisplayAmount(channelTransfrsInVO.getTransfes());
                    channelTransferArr[2] = PretupsBL.getDisplayAmount(channelTransfrsInVO.getReturns());
                    argumentVO.setKey(PretupsErrorCodesI.DAILY_CHANNEL_TRANSFER_IN__PRODUCT_MSG);
                    argumentVO.setArguments(channelTransferArr);
                    argumentVOListForTransferIn.add(argumentVO);
                }
            }
            if (p_channelOutTransferList != null && p_channelOutTransferList.size() > 0) {
                KeyArgumentVO argumentVO = null;
                for (int i = 0, k = p_channelOutTransferList.size(); i < k; i++) {
                    final ChannelTransfrsReturnsVO channelTransfrsInVO = (ChannelTransfrsReturnsVO) p_channelOutTransferList.get(i);
                    argumentVO = new KeyArgumentVO();
                    final String[] channelTransferArr = new String[3];//
                    channelTransferArr[0] = channelTransfrsInVO.getShortName();
                    channelTransferArr[1] = PretupsBL.getDisplayAmount(channelTransfrsInVO.getTransfes());
                    channelTransferArr[2] = PretupsBL.getDisplayAmount(channelTransfrsInVO.getReturns());
                    argumentVO.setKey(PretupsErrorCodesI.DAILY_CHANNEL_TRANSFER_OUT__PRODUCT_MSG);
                    argumentVO.setArguments(channelTransferArr);
                    argumentVOListForTransferOut.add(argumentVO);
                }
            }
            if (p_channelOutChildTransferList.size() > 0) {
                KeyArgumentVO argumentVO = null;
                for (int i = 0, k = p_channelOutChildTransferList.size(); i < k; i++) {
                    final ChannelTransfrsReturnsVO channelTransfrsOutChildVO = (ChannelTransfrsReturnsVO) p_channelOutChildTransferList.get(i);
                    argumentVO = new KeyArgumentVO();
                    final String[] channelTransferChildArr = new String[3];//
                    channelTransferChildArr[0] = channelTransfrsOutChildVO.getShortName();
                    channelTransferChildArr[1] = PretupsBL.getDisplayAmount(channelTransfrsOutChildVO.getTransfes());
                    channelTransferChildArr[2] = PretupsBL.getDisplayAmount(channelTransfrsOutChildVO.getReturns());
                    argumentVO.setKey(PretupsErrorCodesI.DAILY_CHANNEL_TRANSFER_OUT__PRODUCT_MSG);
                    argumentVO.setArguments(channelTransferChildArr);
                    argumentVOListForChildTransferOut.add(argumentVO);
                }
            }
            if (p_subscriberOutTransferList != null && p_subscriberOutTransferList.size() > 0) {
                KeyArgumentVO argumentVO = null;
                for (int i = 0, k = p_subscriberOutTransferList.size(); i < k; i++) {
                    final ChannelTransfrsReturnsVO channelTransfrsInVO = (ChannelTransfrsReturnsVO) p_subscriberOutTransferList.get(i);
                    argumentVO = new KeyArgumentVO();
                    final String[] channelTransferArr = new String[3];//
                    channelTransferArr[0] = channelTransfrsInVO.getServiceName();
                    channelTransferArr[1] = channelTransfrsInVO.getShortName();
                    channelTransferArr[2] = PretupsBL.getDisplayAmount(channelTransfrsInVO.getTransfes());
                    argumentVO.setKey(PretupsErrorCodesI.DAILY_SUBSCRIBER_TRANSFER_OUT_PRODUCT_MSG);
                    argumentVO.setArguments(channelTransferArr);
                    argumentVOListForSubsciberOut.add(argumentVO);
                }
            }
            arr[0] = p_channelUserVO.getUserCode();
            arr[1] = BTSLUtil.getMessage(p_requestVO.getSenderLocale(), argumentVOListForTransferIn);
            arr[2] = BTSLUtil.getMessage(p_requestVO.getSenderLocale(), argumentVOListForTransferOut);
            arr[3] = BTSLUtil.getMessage(p_requestVO.getSenderLocale(), argumentVOListForChildTransferOut);
            arr[4] = BTSLUtil.getMessage(p_requestVO.getSenderLocale(), argumentVOListForSubsciberOut);
            p_requestVO.setMessageArguments(arr);
            p_requestVO.setMessageCode(PretupsErrorCodesI.OTHERUSER_DAILY_TRANSFER_LIST_SUCCESS);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("formatTransferListForSMSForOtherUser", "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "DailyTransferRequestHandler[formatTransferListForSMSForOtherUser]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("DailyTransferRequestHandler", "formatTransferListForSMSForOtherUser", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatTransferListForSMSForOtherUser", "Exited: ");
            }
        }

    }

}
