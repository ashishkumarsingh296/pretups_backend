package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EmailSendToUser;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.OperatorUserLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.user.requesthandler.ChannelSOSSettlementHandler;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.txn.user.businesslogic.UserTxnDAO;

/**
 * @description : This controller class will be used to process the SRD request
 *              for operator user through external system via operator receiver.
 * @author : Vipan
 */

public class OPTUserSRDController implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(OPTUserSRDController.class.getName());

    private UserDAO _userDAO = null;
    private CategoryVO _categoryVO = null;
    private ChannelUserVO _senderVO = null;

    /**
     * Method Process
     * Process Method , Processes Operator user SRD request
     * 
     * @param p_requestVO
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	 loggerValue.setLength(0);
        	 loggerValue.append("Entered p_requestVO=");
        	 loggerValue.append(p_requestVO);
            _log.debug("OPTUserSRDController process", loggerValue);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        final HashMap requestMap = p_requestVO.getRequestMap();
        _userDAO = new UserDAO();
        UserVO requestUserVO = null;
        Locale locale = null;
        try {
        	mcomCon = new MComConnection();con=mcomCon.getConnection();
            _senderVO = (ChannelUserVO) p_requestVO.getSenderVO();
            final String userExistingLoginId = (String) requestMap.get("USRLOGINID");

            if (BTSLUtil.isNullString(userExistingLoginId)) {
                final String[] argsArray = { userExistingLoginId };
                throw new BTSLBaseException("OPTUserSRDController", METHOD_NAME, PretupsErrorCodesI.ERROR_ERP_EXT_OPT_USER_LOGINID_BLANK, argsArray);
            }

            locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));

            // Load user details for modification.
            requestUserVO = new UserTxnDAO().loadUsersDetailsByLoginId(con, userExistingLoginId);

            // If no user details found for the user, throw an exception.
            if (requestUserVO == null) {
                final String[] argsArray = { userExistingLoginId };
                throw new BTSLBaseException("OPTUserSRDController", METHOD_NAME, PretupsErrorCodesI.ERROR_USER_NOT_EXIST, argsArray);
            }

            // Get category code
            String catCode = requestUserVO.getCategoryCode();

            // get User Id
            String userId = requestUserVO.getUserID();
            userId = userId.trim();
            catCode = catCode.trim();

            final CategoryDAO categoryDAO = new CategoryDAO();
            final ArrayList catagorynList = categoryDAO.loadCategoryList(con, PretupsI.OPERATOR_TYPE_OPT, (String) requestMap.get("CATCODE"));

            if (catagorynList != null && !catagorynList.isEmpty()) {
                final Iterator catagoryIte = catagorynList.iterator();
                boolean flag = false;

                while (catagoryIte.hasNext()) {
                    final CategoryVO categoryVO = (CategoryVO) catagoryIte.next();
                    if (categoryVO.getCategoryCode().equalsIgnoreCase(catCode)) {
                        flag = true;
                    }
                }
                if (!flag) {
                    final String[] argsArray = { requestUserVO.getCategoryVO().getCategoryName() };
                    throw new BTSLBaseException("OPTUserSRDController", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_CATAGORY_NOT_ALOOWED, argsArray);
                }
            } else {
                final String[] argsArray = { requestUserVO.getCategoryVO().getCategoryName() };
                throw new BTSLBaseException("OPTUserSRDController", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_CATAGORY_NOT_ALOOWED, argsArray);
            }

            // User External Code set
            boolean blank = true;
            final String action = (String) requestMap.get("ACTION");
            blank = BTSLUtil.isNullString(action);
            if (blank) {
                throw new BTSLBaseException("OPTUserSRDController", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_ACTION_BLANK);
            }

            String remarks = (String) requestMap.get("REMARKS");
            if (!BTSLUtil.isNullString(remarks)) {
                remarks = remarks.trim();
                requestUserVO.setRemarks(remarks);
            }

            final UserDAO userDAO = new UserDAO();
            // insert in to user table
            int updateCount = 0;

            if (action.equalsIgnoreCase(PretupsI.USER_STATUS_DELETED)) {
                boolean isBalanceFlag = false;
                boolean isO2CPendingFlag = false;
                final boolean isChildFlag = userDAO.isChildUserActive(con, userId);
                if (isChildFlag) {
                    throw new BTSLBaseException("OPTUserSRDController", METHOD_NAME, PretupsErrorCodesI.ERROR_ERP_CHILD_USER_EXISTS);
                } else {
                    isBalanceFlag = userDAO.isUserBalanceExist(con, userId);
                }

                if (isBalanceFlag) {
                    throw new BTSLBaseException("OPTUserSRDController", METHOD_NAME, PretupsErrorCodesI.ERROR_ERP_BALANCE_EXISTS);
                }
                else {
                	// Checking SOS Pending transactions
                	if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()){
                        ChannelSOSSettlementHandler channelSOSSettlementHandler = new ChannelSOSSettlementHandler();
                        boolean isSOSPendingFlag = channelSOSSettlementHandler.validateSOSPending(con, userId);
                        if (isSOSPendingFlag) 
                            throw new BTSLBaseException("OPTUserSRDController", METHOD_NAME, PretupsErrorCodesI.SOS_NOT_SETTLED_FOR_DELETION);
                    }
                	// Checking last recharge credit request Pending transactions
                	if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED))).booleanValue()){
                        UserTransferCountsVO userTrfCntVO = new UserTransferCountsVO();
                        UserTransferCountsDAO userTrfCntDAO = new UserTransferCountsDAO();
                        userTrfCntVO = userTrfCntDAO.selectLastLRTxnID(userId, con, false, null);
                        if (userTrfCntVO!=null) 
                            throw new BTSLBaseException("OPTUserSRDController", METHOD_NAME, PretupsErrorCodesI.LR_NOT_SETTLED_FOR_DELETION);
                    }
                    // Checking O2C Pending transactions
                    final ChannelTransferDAO transferDAO = new ChannelTransferDAO();
                    isO2CPendingFlag = transferDAO.isPendingTransactionExist(con, userId);
                }
                boolean isbatchFocPendingTxn = false;
                if (isO2CPendingFlag) {
                    throw new BTSLBaseException("OPTUserSRDController", METHOD_NAME, PretupsErrorCodesI.ERROR_ERP_O2C_TXN_PENDING);
                } else {
                    // Checking Batch FOC pending transactions
                    final FOCBatchTransferDAO batchTransferDAO = new FOCBatchTransferDAO();
                    isbatchFocPendingTxn = batchTransferDAO.isPendingTransactionExist(con, userId);
                }
                if (isbatchFocPendingTxn) {
                    throw new BTSLBaseException("OPTUserSRDController", METHOD_NAME, PretupsErrorCodesI.ERROR_ERP_FOC_TXN_PENDING);
                }
            }

            final Date currentDate = new Date();
            requestUserVO.setModifiedBy(_senderVO.getUserID());
            requestUserVO.setModifiedOn(currentDate);
            if (action.equalsIgnoreCase(PretupsI.USER_STATUS_DELETED)) {
                requestUserVO.setStatus(PretupsI.USER_STATUS_DELETED);
            } else if (action.equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND)) {
                requestUserVO.setStatus(PretupsI.USER_STATUS_SUSPEND);
            } else {
                requestUserVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
            }
            loggerValue.setLength(0);
        	loggerValue.append("process Going to update status of : _channelUserVO.getUserID = ");
        	loggerValue.append( requestUserVO.getUserID() );
        	loggerValue.append(" ,Status=");
        	loggerValue.append(requestUserVO.getStatus());
            _log.debug("OPTUserSRDController",  loggerValue );

            requestUserVO.setPreviousStatus(requestUserVO.getStatus());
            final ArrayList userList = new ArrayList();
            userList.add(requestUserVO);
            updateCount = userDAO.deleteSuspendUser(con, userList);

            if (updateCount <= 0) {
                con.rollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "OPTUserSRDController[process]", "", "", "",
                    "Exception:Update count <=0 ");
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USR_SUS_RES_FAILED);
            }

            con.commit();
            OperatorUserLog.apiLog("SRDOPTUSR", requestUserVO, _senderVO, p_requestVO);
            loggerValue.setLength(0);
        	loggerValue.append("process After to update status of : _channelUserVO.getUserID = ");
        	loggerValue.append( requestUserVO.getUserID());
        	loggerValue.append( " ,Status=");
        	loggerValue.append( requestUserVO.getStatus());
            _log.debug("OPTUserSRDController", loggerValue);
            BTSLMessages btslPushMessage = null;
            if (action.equalsIgnoreCase(PretupsI.USER_STATUS_DELETED)) {
                final String arr[] = { requestUserVO.getUserName() };
                p_requestVO.setMessageArguments(arr);
                btslPushMessage = new BTSLMessages("user.addoperatoruser.deletesuccessmessage", arr);
                p_requestVO.setMessageCode(PretupsErrorCodesI.USER_DELETED_SUCCESS);
                if (!BTSLUtil.isNullString(requestUserVO.getMsisdn())) {
                    final PushMessage pushMessage = new PushMessage(requestUserVO.getMsisdn(), PretupsErrorCodesI.USER_DELETED_SUCCESS, null, null, locale);
                    pushMessage.push();
                }
            } else if (action.equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND)) {
                final String arr[] = { requestUserVO.getUserName() };
                p_requestVO.setMessageArguments(arr);
                btslPushMessage = new BTSLMessages("user.addoperatoruser.suspendedsuccessmessage", arr);
                p_requestVO.setMessageCode(PretupsErrorCodesI.USER_SUSPEND_SUCCESS);
                if (!BTSLUtil.isNullString(requestUserVO.getMsisdn())) {
                    final PushMessage pushMessage = new PushMessage(requestUserVO.getMsisdn(), PretupsErrorCodesI.USER_SUSPEND_SUCCESS, null, null, locale);
                    pushMessage.push();
                }
            } else {
                final String arr[] = { requestUserVO.getUserName() };
                p_requestVO.setMessageArguments(arr);
                btslPushMessage = new BTSLMessages("user.addoperatoruser.resumedsuccessmessage", arr);
                p_requestVO.setMessageCode(PretupsErrorCodesI.USER_RESUMED_SUCCESS);
                if (!BTSLUtil.isNullString(requestUserVO.getMsisdn())) {
                    final PushMessage pushMessage = new PushMessage(requestUserVO.getMsisdn(), PretupsErrorCodesI.USER_RESUMED_SUCCESS, null, null, locale);
                    pushMessage.push();
                }
            }
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(requestUserVO.getEmail())) {
                final String arr[] = { requestUserVO.getUserName() };
                final String subject = BTSLUtil.getMessage(locale, "user.addoperatoruser.updatesuccessmessage", arr);
                final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, requestUserVO.getNetworkID(), "Email will be delivered shortly",
                    requestUserVO, requestUserVO);
                emailSendToUser.sendMail();
            }

        }

        catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException " );
        	loggerValue.append(be.getMessage());
            _log.error(METHOD_NAME, loggerValue);
            _log.errorTrace(METHOD_NAME, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return;
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(METHOD_NAME,  loggerValue);
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OPTUserSRDController[process]", "", "", "",
            		loggerValue.toString() );
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
            _userDAO = null;
            _categoryVO = null;
            requestMap.put("CHNUSERVO", requestUserVO);
            p_requestVO.setRequestMap(requestMap);

			if (mcomCon != null) {
				mcomCon.close("OPTUserSRDController#process");
				mcomCon = null;
			}
			   if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " Exited ");
            }
        }
    }

}
