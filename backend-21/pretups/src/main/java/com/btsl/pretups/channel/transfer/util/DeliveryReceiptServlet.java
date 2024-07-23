/**
 * @(#)DeliveryReceiptServlet.java
 *                                 Copyright(c) 2006, Bharti Telesoft Ltd.
 *                                 All Rights Reserved
 *                                 Servlet that will be called by Kannel for
 *                                 receiving the delivery receipt
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Amit Ruwali 14/09/2006 Initial Creation
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 This servlet recives the request from Kannel
 *                                 System after sending the SMS to the user.
 *                                 On the basis of the passed status code this
 *                                 servlet call the differential commission
 *                                 calculation or credit programs and updates
 *                                 the voucher status
 */

package com.btsl.pretups.channel.transfer.util;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.loadcontroller.LoadController;
import com.btsl.loadcontroller.LoadControllerI;
import com.btsl.loadcontroller.ReqNetworkServiceLoadController;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.logging.BalanceLogger;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeCountersVO;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeDAO;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeProfileVO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.pretups.logging.SMSChargingLog;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleCache;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.NetworkProductServiceTypeCache;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberBL;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.subscriber.businesslogic.SubscriberControlDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class DeliveryReceiptServlet extends HttpServlet {
   
    private static final Log _log = LogFactory
			.getLog(DeliveryReceiptServlet.class.getName());
    private String receiptStatus = null;
    private String transMsisdn = null;
    private String transactionId = null;
    private String _instanceID = null;

    /**
     * This method is used to called at servlet initilization
     * 
     * @param p_conf
     *            ServletConfig
     * @exception jakarta.servlet.ServletException
     */

    public void init(ServletConfig p_conf) throws ServletException {
        super.init(p_conf);
        final String METHOD_NAME = "init";
        try {
            _instanceID = getInitParameter("instanceCode");
            _log.debug(this, "DeliveryReceiptServlet[init] : Entered");
        } catch (Exception ex) {
        	_log.error(METHOD_NAME, "Exception:e=" + ex);
			_log.errorTrace(METHOD_NAME, ex);
            
        }
    } // end of init

    /**
     * This method is used to process get request
     * it does nothing but calls dopost method
     * 
     * @param p_request
     *            HttpServletRequest
     * @param p_response
     *            HttpServletResponse
     * @exception jakarta.servlet.ServletException
     * @exception java.io.IOException
     */

    public void doGet(HttpServletRequest p_request, HttpServletResponse p_response) throws java.io.IOException {
        doPost(p_request, p_response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
        if (_log.isDebugEnabled()) {
            _log.debug("DeliveryReceiptServlet[doPost]", " Entered");
        }
        // Getting the printwriter object and sending response back to Kannal If
        // Required
        final String METHOD_NAME = "doPost";
        final PrintWriter out = response.getWriter();
        final String respStr = "Received";
        out.println(respStr);
        out.flush();
        Connection con = null;
        MComConnectionI mcomCon = null;
        C2STransferVO c2sTransferVO = null;
        ReceiverVO receiverVO = null;
        BTSLMessages btslMessages = null;
        boolean isUpdateRequired = true;
        boolean messageSentRequired = false;
        boolean unmarkReceiverRequired = true;
        // Get the instance id from the web.xml
        GroupTypeCountersVO userGroupTypeCounterVO = null;
        ServiceKeywordCacheVO cacheVO = null;
        try {
            // Get the value of various parametrs from the request
            receiptStatus = request.getParameter("type");
            transMsisdn = request.getParameter("msisdn");
            transactionId = request.getParameter("dlrid"); // seq num

            if (_log.isDebugEnabled()) {
                _log.debug(this,
                    "DeliveryReceiptServlet[doPost]: request parameters receiptStatus=" + receiptStatus + " transMsisdn=" + transMsisdn + " transactionId=" + transactionId);
            }

            // If receipt is null then throw error
            if ("null".equalsIgnoreCase(receiptStatus) || BTSLUtil.isNullString(receiptStatus)) {
                if (_log.isDebugEnabled()) {
                    _log.debug(this, "[doPost]Transaction No[" + receiptStatus + "] Receipt call status is null or blank");
                }
                isUpdateRequired = false;
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DeliveryReceiptServlet[doPost]", "", "", "",
                    "Transaction No[" + transactionId + "] Receipt call status is null or blank");
                throw new BTSLBaseException("EVDController", "process", PretupsErrorCodesI.EVD_DELIVERY_STATUS_NOT_FOUND);
            }// end if Status null or blank

            mcomCon = new MComConnection();
            con=mcomCon.getConnection();

            // Load the Record for updation when Receipt is recieved and Load
            // the transfer items list
            final C2STransferDAO c2STransferDAO = new C2STransferDAO();
            c2sTransferVO = c2STransferDAO.loadC2STransferDetails(con, transactionId);

            // If transfer detaild are not found the throw error
            if (c2sTransferVO == null) {
                _log.debug(this, "[doPost]Transaction No[" + transactionId + "] Records not found in recharges table");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DeliveryReceiptServlet[doPost]", "", "", "",
                    "Records not found in transfers table For Transfer ID =" + transactionId);
				if (mcomCon != null) {
					mcomCon.close("DeliveryReceiptServlet#doPost");
					mcomCon = null;
				}
                isUpdateRequired = false;
                throw new BTSLBaseException("EVDController", "process", PretupsErrorCodesI.EVD_TRANSACTION_DETAILS_NOT_FOUND);
            }// end if smsUserVO null

            messageSentRequired = true;
            // Get the transfer status from the transaction data
            final String databaseStatus = c2sTransferVO.getTransferStatus();
            if (_log.isDebugEnabled()) {
                _log.debug(
                    this,
                    "[doPost]Transaction No[" + transactionId + "] after processing request parameters Status incoming :" + receiptStatus + " Status stored in DB:" + databaseStatus);
            }

            // If transsaction status is not under process throw error
            if (!PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS.equalsIgnoreCase(databaseStatus)) {
                _log.debug(this, "[doPost]Transaction No[" + transactionId + "] Status in database for recharge No=" + transMsisdn + " is Not 200, Status=" + databaseStatus);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DeliveryReceiptServlet[doPost]", "", "", "",
                    "Status in database for recharge No is Not 200  for" + transactionId);
				if (mcomCon != null) {
					mcomCon.close("DeliveryReceiptServlet#doPost");
					mcomCon = null;
				}
                isUpdateRequired = false;
                throw new BTSLBaseException("EVDController", "process", PretupsErrorCodesI.EVD_TRANSACTION_NOT_UNDERPROCESS);
            }// end

            final ArrayList itemlist = c2STransferDAO.loadC2STransferItemsVOList(con, transactionId);

            final String interfaceID = c2sTransferVO.getSenderTransferItemVO().getInterfaceID();
            c2sTransferVO.setTransferItemList(itemlist);
            c2sTransferVO.setReceiverTransferItemVO((C2STransferItemVO) itemlist.get(1));
            final InterfaceVO interfaceVO = (InterfaceVO) NetworkInterfaceModuleCache.getObject(interfaceID);
            final NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(PretupsI.C2S_MODULE, c2sTransferVO
                .getReceiverNetworkCode(), PretupsI.INTERFACE_CATEGORY_VOMS);
            receiverVO = new ReceiverVO();
            receiverVO.setModule(PretupsI.C2S_MODULE);
            receiverVO.setMsisdn(c2sTransferVO.getReceiverMsisdn());
            receiverVO.setCreatedDate(c2sTransferVO.getTransferDate());
            new SubscriberControlDAO().loadSubscriberControlDetails(con, receiverVO);
            receiverVO.setLastTransferID(c2sTransferVO.getTransferID());
            receiverVO.setSubscriberType(c2sTransferVO.getReceiverTransferItemVO().getSubscriberType());
            receiverVO.setPrefixID(c2sTransferVO.getReceiverTransferItemVO().getPrefixID());
            c2sTransferVO.setReceiverVO(receiverVO);
            final EvdUtil evdUtil = new EvdUtil();
            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            final ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, c2sTransferVO.getSenderMsisdn());
            c2sTransferVO.setSenderVO(channelUserVO);

            if (channelUserVO != null && PretupsI.STATUS_ACTIVE.equals(channelUserVO.getCategoryVO().getRestrictedMsisdns())) {
                try {
                    RestrictedSubscriberBL.isRestrictedMsisdnExist(con, c2sTransferVO, channelUserVO, c2sTransferVO.getReceiverMsisdn(), c2sTransferVO.getRequestedAmount());
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
            }
            cacheVO = ServiceKeywordCache.getServiceTypeObject(c2sTransferVO.getServiceType(), PretupsI.C2S_MODULE);
            if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CTRL_ALLOWED)) != null && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CTRL_ALLOWED)).indexOf(c2sTransferVO.getRequestGatewayType()) != -1 && !PretupsI.NOT_APPLICABLE
                .equals(cacheVO.getGroupType())) {
                final GroupTypeDAO groupTypeDAO = new GroupTypeDAO();
                if (PretupsI.GRPT_CONTROL_LEVEL_MSISDN.equals(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CONTROL_LEVEL)))) {
                    userGroupTypeCounterVO = groupTypeDAO.loadUserGroupTypeCounters(con, channelUserVO.getUserID(), channelUserVO.getMsisdn(), cacheVO.getGroupType(),
                        PretupsI.GRPT_TYPE_CONTROLLING);
                } else if (PretupsI.GRPT_CONTROL_LEVEL_USERID.equals(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CONTROL_LEVEL)))) {
                    userGroupTypeCounterVO = groupTypeDAO.loadUserGroupTypeCounters(con, channelUserVO.getUserID(), PretupsI.ALL, cacheVO.getGroupType(),
                        PretupsI.GRPT_TYPE_CONTROLLING);
                }
            }
            if ("1".equalsIgnoreCase(receiptStatus)) {

				if (mcomCon != null) {
					mcomCon.close("DeliveryReceiptServlet#doPost");
					mcomCon = null;
				}
                con = null;
                final ListValueVO listValueVO = NetworkProductServiceTypeCache.getProductServiceValueVO(PretupsI.SERVICE_TYPE_EVD, PretupsI.DEFAULT_SUBSERVICE);
                if (listValueVO != null) {
                    c2sTransferVO.setDifferentialAllowedForService(listValueVO.getValue());
                }

                // 1.Set voucher status to consumed.
                // 2.Give diff comm.
                try {
                    evdUtil.updateVoucherAndGiveDifferentials(receiverVO, c2sTransferVO, networkInterfaceModuleVOS, interfaceVO, _instanceID, false);
                } catch (BTSLBaseException be) {
                    c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                    if (BTSLUtil.isNullString(c2sTransferVO.getErrorCode())) {
                        if (be.isKey()) {
                            c2sTransferVO.setErrorCode(be.getMessageKey());
                        } else {
                            c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                        }
                    }// end if

                    // Update the sender back for fail transaction
                    // Check Status if Ambigous then credit back preference wise
                    if (((c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CREDIT_BACK_ALWD_EVD_AMB))).booleanValue())) || c2sTransferVO
                        .getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                        isUpdateRequired = voucherUpdateSenderCreditBack(transactionId, interfaceID, c2sTransferVO);
                    }
                    throw be;
                } catch (Exception e) {
                    c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                    if (BTSLUtil.isNullString(c2sTransferVO.getErrorCode())) {
                        c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                    }

                    // Check Status if Ambigous then credit back preference wise
                    if (((c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CREDIT_BACK_ALWD_EVD_AMB))).booleanValue())) || c2sTransferVO
                        .getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                        isUpdateRequired = voucherUpdateSenderCreditBack(transactionId, interfaceID, c2sTransferVO);
                    }
                    throw new BTSLBaseException(this, "doPost", "Exception in processing get request");
                }
            } else if ("2".equalsIgnoreCase(receiptStatus) || "16".equalsIgnoreCase(receiptStatus)) {
				if (mcomCon != null) {
					mcomCon.close("DeliveryReceiptServlet#doPost");
					mcomCon = null;
				}
                con = null;
                c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                c2sTransferVO.setErrorCode(PretupsErrorCodesI.VMS_PIN_SENT_FAIL);
                isUpdateRequired = voucherUpdateSenderCreditBack(transactionId, interfaceID, c2sTransferVO);
                // For increaseing the counters in network and service type
                ReqNetworkServiceLoadController.increaseRechargeCounters(_instanceID, c2sTransferVO.getRequestGatewayType(), c2sTransferVO.getNetworkCode(), c2sTransferVO
                    .getServiceType(), c2sTransferVO.getTransferID(), LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, c2sTransferVO.getReceiverNetworkCode());

            } else {
                isUpdateRequired = false;
                messageSentRequired = false;
                unmarkReceiverRequired = false;
            }
        } catch (BTSLBaseException e) {
            _log.error(this, "Exception occured" + e.getMessage());
            if (c2sTransferVO != null) {
                c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                if (BTSLUtil.isNullString(c2sTransferVO.getErrorCode())) {
                    if (e.isKey()) {
                        c2sTransferVO.setErrorCode(e.getMessageKey());
                    } else {
                        c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                    }
                }// end if
                if (e.isKey() && c2sTransferVO.getSenderReturnMessage() == null) {
                    btslMessages = e.getBtslMessages();
                } else if (c2sTransferVO.getSenderReturnMessage() == null) {
                    c2sTransferVO.setSenderReturnMessage(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                }

                // For increaseing the counters in network and service type
                ReqNetworkServiceLoadController.increaseRechargeCounters(_instanceID, c2sTransferVO.getRequestGatewayType(), c2sTransferVO.getNetworkCode(), c2sTransferVO
                    .getServiceType(), c2sTransferVO.getTransferID(), LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, c2sTransferVO.getReceiverNetworkCode());
                _log.errorTrace(METHOD_NAME, e);
            }
        }// end of catch
        catch (Exception e) {
            _log.error(this, "Exception occured" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            if (c2sTransferVO != null) {
                c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                if (BTSLUtil.isNullString(c2sTransferVO.getErrorCode())) {
                    c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);
                }
                btslMessages = new BTSLMessages(PretupsErrorCodesI.C2S_ERROR_EXCEPTION_EVD);

                // For increaseing the counters in network and service type
                ReqNetworkServiceLoadController.increaseRechargeCounters(_instanceID, c2sTransferVO.getRequestGatewayType(), c2sTransferVO.getNetworkCode(), c2sTransferVO
                    .getServiceType(), c2sTransferVO.getTransferID(), LoadControllerI.COUNTER_FAIL_REQUEST, 0, false, c2sTransferVO.getReceiverNetworkCode());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DeliveryReceiptServlet[doPost]", c2sTransferVO
                    .getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(),
                    "voucher serial number=" + c2sTransferVO.getSerialNumber() + " Exception:" + e.getMessage());
            }
        }// end of catch
        finally {
            try {

                if (mcomCon == null) {
                	mcomCon = new MComConnection();
                	try{con=mcomCon.getConnection();}catch(SQLException e){
                		_log.error(METHOD_NAME, "SQLException " + e.getMessage());
            			_log.errorTrace(METHOD_NAME, e);
                	}
                }
                try {
                    if (receiverVO != null && unmarkReceiverRequired) {
                        PretupsBL.unmarkReceiverLastRequest(con, c2sTransferVO.getTransferID(), receiverVO);
                    }
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    _log.error("run", c2sTransferVO.getTransferID(), "BTSLBaseException while updating Receiver last request status in database:" + be.getMessage());
                } catch (Exception e) {
                    try {
                        if (con != null) {
                            con.rollback();
                        }
                    } catch (Exception ex) {
                        _log.errorTrace(METHOD_NAME, ex);
                    }
                    _log.errorTrace(METHOD_NAME, e);
                    _log.error("run", c2sTransferVO.getTransferID(), "Exception while updating Receiver last request status in database:" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "EVDController[run]", c2sTransferVO
                        .getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getSenderNetworkCode(),
                        "Exception while updating Receiver last request status in database , Exception:" + e.getMessage());
                }// end catch
                if (c2sTransferVO != null) {
                    if (c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_FAIL) && (c2sTransferVO.getReceiverReturnMsg() == null || !((BTSLMessages) c2sTransferVO
                        .getReceiverReturnMsg()).isKey())) {
                        c2sTransferVO.setReceiverReturnMsg(new BTSLMessages((PretupsErrorCodesI.C2S_RECEIVER_FAIL_EVD), new String[] { String.valueOf(c2sTransferVO
                            .getTransferID()), PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()) }));
                    }
                    LoadController.decreaseTransactionLoad(c2sTransferVO.getTransferID(), c2sTransferVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
                    if (isUpdateRequired) {
                        c2sTransferVO.setModifiedOn(new Date());
                        c2sTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
                        // Updating C2S Transfer details in database
                        ChannelTransferBL.updateC2STransferDetails(con, c2sTransferVO);
                    }
                }
                // If transaction is fail and grouptype counters need to be
                // decrease then decrease the counters
                // This change has been done by ankit on date 14/07/06 for SMS
                // charging
                if (!c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && userGroupTypeCounterVO != null) {
                    PretupsBL.decreaseGroupTypeCounters(userGroupTypeCounterVO);
                }

            } catch (BTSLBaseException be) {
                if (_log.isDebugEnabled()) {
                    _log.debug(this, "BTSLBaseException occured" + be.getMessage());
                }
                _log.errorTrace(METHOD_NAME, be);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "DeliveryReceiptServlet[doPost]", c2sTransferVO
                    .getTransferID(), c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getNetworkCode(), "BTSLBaseException::" + be.getMessage());
            }
            if (con != null) {
                try {
                	mcomCon.finalCommit();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
				if (mcomCon != null) {
					mcomCon.close("DeliveryReceiptServlet#doPost");
					mcomCon = null;
				}
                con = null;
            }
            if (messageSentRequired) {
                // Send the messages to sender and receiver as needed
                final C2STransferItemVO receiverTransferItemVO = (C2STransferItemVO) c2sTransferVO.getTransferItemList().get(1);
                final Locale receiverLocale = new Locale(receiverTransferItemVO.getLanguage(), receiverTransferItemVO.getCountry());
                // Success message to receiver will be send only when PIN is
                // send to retailer
                if (c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                    if (c2sTransferVO.getSenderMsisdn().equals(c2sTransferVO.getPinSentToMsisdn())) {
                        if (c2sTransferVO.getReceiverReturnMsg() == null) {
                            (new PushMessage(c2sTransferVO.getReceiverMsisdn(), getReceiverSuccessMessage(receiverLocale, c2sTransferVO, receiverTransferItemVO
                                .getServiceClass()), c2sTransferVO.getTransferID(), c2sTransferVO.getRequestGatewayCode(), receiverLocale)).push();
                        } else if (c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                            final BTSLMessages btslRecMessages = (BTSLMessages) c2sTransferVO.getReceiverReturnMsg();
                            (new PushMessage(c2sTransferVO.getReceiverMsisdn(), BTSLUtil
                                .getMessage(receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()), c2sTransferVO.getTransferID(), c2sTransferVO
                                .getRequestGatewayCode(), receiverLocale)).push();
                        } else {
                            (new PushMessage(c2sTransferVO.getReceiverMsisdn(), (String) c2sTransferVO.getReceiverReturnMsg(), c2sTransferVO.getTransferID(), c2sTransferVO
                                .getRequestGatewayCode(), receiverLocale)).push();
                        }
                    }
                }// Send ambiguous message to receiver
                else if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("EVD_REC_GEN_FAIL_MSG_REQD_T"))) && c2sTransferVO.getTransferStatus().equals(
                    PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                    if (c2sTransferVO.getReceiverReturnMsg() == null) {
                        (new PushMessage(c2sTransferVO.getReceiverMsisdn(), BTSLUtil.getMessage(receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_AMBIGOUS_KEY_EVD,
                            new String[] { c2sTransferVO.getTransferID(), PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), c2sTransferVO.getSenderMsisdn() },
                            c2sTransferVO.getRequestGatewayType()), c2sTransferVO.getTransferID(), c2sTransferVO.getRequestGatewayCode(), receiverLocale)).push();
                    } else if (c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                        final BTSLMessages btslRecMessages = (BTSLMessages) c2sTransferVO.getReceiverReturnMsg();
                        (new PushMessage(c2sTransferVO.getReceiverMsisdn(), BTSLUtil.getMessage(receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()),
                            c2sTransferVO.getTransferID(), c2sTransferVO.getRequestGatewayCode(), receiverLocale)).push();
                    } else {
                        (new PushMessage(c2sTransferVO.getReceiverMsisdn(), (String) c2sTransferVO.getReceiverReturnMsg(), c2sTransferVO.getTransferID(), c2sTransferVO
                            .getRequestGatewayCode(), receiverLocale)).push();
                    }
                }// Send failure message to receiver
                else if ("Y".equals(BTSLUtil.NullToString(Constants.getProperty("EVD_REC_GEN_FAIL_MSG_REQD_T"))) && c2sTransferVO.getTransferStatus().equals(
                    PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                    if (c2sTransferVO.getReceiverReturnMsg() == null) {
                        (new PushMessage(c2sTransferVO.getReceiverMsisdn(), BTSLUtil.getMessage(receiverLocale, PretupsErrorCodesI.C2S_RECEIVER_FAIL_KEY_EVD,
                            new String[] { c2sTransferVO.getTransferID(), PretupsBL.getDisplayAmount(c2sTransferVO.getRequestedAmount()), c2sTransferVO.getSenderMsisdn() },
                            c2sTransferVO.getRequestGatewayType()), c2sTransferVO.getTransferID(), c2sTransferVO.getRequestGatewayCode(), receiverLocale)).push();
                    } else if (c2sTransferVO.getReceiverReturnMsg() != null && ((BTSLMessages) c2sTransferVO.getReceiverReturnMsg()).isKey()) {
                        final BTSLMessages btslRecMessages = (BTSLMessages) c2sTransferVO.getReceiverReturnMsg();
                        (new PushMessage(c2sTransferVO.getReceiverMsisdn(), BTSLUtil.getMessage(receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs()),
                            c2sTransferVO.getTransferID(), c2sTransferVO.getRequestGatewayCode(), receiverLocale)).push();
                    } else {
                        (new PushMessage(c2sTransferVO.getReceiverMsisdn(), (String) c2sTransferVO.getReceiverReturnMsg(), c2sTransferVO.getTransferID(), c2sTransferVO
                            .getRequestGatewayCode(), receiverLocale)).push();
                    }
                }
                // Message to sender will be send to retailer only when request
                // gateway allows to send message
                // This check may be removed after discussion.
                if (!BTSLUtil.isStringIn(c2sTransferVO.getRequestGatewayCode(), BTSLUtil.NullToString(Constants.getProperty("EVD_SEN_MSG_NOT_REQD_GW")))) {
                    final C2STransferItemVO senderTransferItemVO = (C2STransferItemVO) c2sTransferVO.getTransferItemList().get(0);
                    final Locale senderLocale = new Locale(senderTransferItemVO.getLanguage(), senderTransferItemVO.getCountry());
                    // Message to sender will only be send when
                    // 1. Transaction is fail
                    // 2. Transaction is ambigous
                    // 3. If transaction is success and pin is send to customer
                    if (!(c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && c2sTransferVO.getSenderMsisdn().equals(
                        c2sTransferVO.getPinSentToMsisdn()))) {
                        PushMessage pushMessages = null;
                        if (btslMessages != null) {
                            // push final error message to sender
                            pushMessages = (new PushMessage(c2sTransferVO.getSenderMsisdn(), BTSLUtil.getMessage(senderLocale, btslMessages.getMessageKey(), btslMessages
                                .getArgs()), c2sTransferVO.getTransferID(), c2sTransferVO.getRequestGatewayCode(), senderLocale));
                        } else {
                            // push Additional Commission success message to
                            // sender and final status to sender
                            if (!BTSLUtil.isNullString(c2sTransferVO.getSenderReturnMessage())) {
                                pushMessages = (new PushMessage(c2sTransferVO.getSenderMsisdn(), c2sTransferVO.getSenderReturnMessage(), c2sTransferVO.getTransferID(),
                                    c2sTransferVO.getRequestGatewayCode(), senderLocale));
                            } else if (c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                                pushMessages = (new PushMessage(c2sTransferVO.getSenderMsisdn(), BTSLUtil.getMessage(senderLocale, PretupsErrorCodesI.EVD_SENDER_SUCCESS,
                                    new String[] { c2sTransferVO.getReceiverMsisdn(), c2sTransferVO.getTransferID(), PretupsBL.getDisplayAmount(c2sTransferVO
                                        .getTransferValue()), PretupsBL.getDisplayAmount(senderTransferItemVO.getPostBalance()), String.valueOf(receiverTransferItemVO
                                        .getValidity()), PretupsBL.getDisplayAmount(receiverTransferItemVO.getPostBalance()), String.valueOf(receiverTransferItemVO
                                        .getNewGraceDate()), PretupsBL.getDisplayAmount(c2sTransferVO.getReceiverAccessFee()), c2sTransferVO.getSubService() }), c2sTransferVO
                                    .getTransferID(), c2sTransferVO.getRequestGatewayCode(), senderLocale));
                            }
                        }// end if
                        if (pushMessages != null) {
                            // If transaction is successfull then if group type
                            // counters reach limit then send message using
                            // gateway that is associated with group type
                            // profile
                            if (c2sTransferVO.getTransferStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS) && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED)) != null && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CHRG_ALLOWED))
                                .indexOf(c2sTransferVO.getRequestGatewayType()) != -1 && !PretupsI.NOT_APPLICABLE.equals(cacheVO.getGroupType())) {
                                try {
                                    GroupTypeProfileVO groupTypeProfileVO = null;
                                    // load the user running and profile
                                    // counters
                                    // Check the counters
                                    // update the counters
                                    final RequestVO requestVO = new RequestVO();
                                    requestVO.setSenderVO(c2sTransferVO.getSenderVO());
                                    requestVO.setGroupType(cacheVO.getGroupType());
                                    requestVO.setCreatedOn(c2sTransferVO.getCreatedOn());
                                    requestVO.setModule(PretupsI.C2S_MODULE);
                                    requestVO.setRequestGatewayCode(c2sTransferVO.getRequestGatewayCode());
                                    requestVO.setRequestGatewayType(c2sTransferVO.getRequestGatewayType());
                                    groupTypeProfileVO = PretupsBL.loadAndCheckC2SGroupTypeCounters(requestVO, PretupsI.GRPT_TYPE_CHARGING);
                                    // if group type counters reach limit then
                                    // send message using gateway that is
                                    // associated with group type profile
                                    if (groupTypeProfileVO != null && groupTypeProfileVO.isGroupTypeCounterReach()) {
                                        pushMessages.push(groupTypeProfileVO.getGatewayCode(), groupTypeProfileVO.getAltGatewayCode());// new
                                        // method
                                        // will
                                        // be
                                        // called
                                        // here
                                        SMSChargingLog.log(((ChannelUserVO) requestVO.getSenderVO()).getUserID(), (((ChannelUserVO) requestVO.getSenderVO())
                                            .getUserChargeGrouptypeCounters()).getCounters(), groupTypeProfileVO.getThresholdValue(), groupTypeProfileVO.getReqGatewayType(),
                                            groupTypeProfileVO.getResGatewayType(), groupTypeProfileVO.getNetworkCode(), requestVO.getGroupType(), c2sTransferVO
                                                .getServiceType(), requestVO.getModule());
                                    } else {
                                        pushMessages.push();
                                    }
                                } catch (Exception e) {
                                    _log.errorTrace(METHOD_NAME, e);
                                }
                            } else {
                                pushMessages.push();
                            }
                        }
                    }
                }
            }

        }// end finally
    }

    /**
     * Method:getReceiverSuccessMessage
     * This method is used to contruct the success message of receiver.
     * In this method message can be service class wise
     * 
     * @param p_locale
     * @param p_c2sTransferVO
     * @param p_serviceClass
     * @return
     */
    private String getReceiverSuccessMessage(Locale p_locale, C2STransferVO p_c2sTransferVO, String p_serviceClass) {
        final String METHOD_NAME = "getReceiverSuccessMessage";
        String[] messageArgArray = null;
        String key = null;
        try {
            messageArgArray = new String[] { p_c2sTransferVO.getTransferID(), PretupsBL.getDisplayAmount(p_c2sTransferVO.getReceiverTransferValue()), p_c2sTransferVO
                .getSenderMsisdn(), PretupsBL.getDisplayAmount(p_c2sTransferVO.getRequestedAmount()), PretupsBL.getDisplayAmount(p_c2sTransferVO.getReceiverAccessFee()), p_c2sTransferVO
                .getSubService() };
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        key = PretupsErrorCodesI.EVD_RECEIVER_SUCCESS_WITHOUT_POSTBAL;// return
        // BTSLUtil.getMessage(_receiverLocale,PretupsErrorCodesI.C2S_RECEIVER_SUCCESS_WITHOUT_POSTBAL,messageArgArray);
        // Check if service class wise message is required or not
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.NOTIFICATION_SERVICECLASSWISE_REC_EVD))).booleanValue()) {
            String message = null;
            try {
                message = BTSLUtil.getMessage(p_locale, key + "_" + p_serviceClass, messageArgArray, p_c2sTransferVO.getRequestGatewayType());
                if (!BTSLUtil.isNullString(message)) {
                    return message;
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
        }
        return BTSLUtil.getMessage(p_locale, key, messageArgArray, p_c2sTransferVO.getRequestGatewayType());
    }

    /**
     * Method:voucherUpdateSenderCreditBack
     * The method below will update the voucher status and also credit back the
     * sender
     * 
     * @param p_transferID
     * @param p_interfaceID
     * @param p_c2sTransferVO
     * @return
     */
    private boolean voucherUpdateSenderCreditBack(String p_transferID, String p_interfaceID, C2STransferVO p_c2sTransferVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("voucherUpdateSenderCreditBack", "Entered for p_transferID=" + p_transferID);
        }
        final String METHOD_NAME = "voucherUpdateSenderCreditBack";
        Connection con = null;MComConnectionI mcomCon = null;
        boolean finalTransferStatusUpdate = true;
        try {
            final EvdUtil evdUtil = new EvdUtil();
            try {
                final InterfaceVO interfaceVO = (InterfaceVO) NetworkInterfaceModuleCache.getObject(p_interfaceID);
                final NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(PretupsI.C2S_MODULE,
                    p_c2sTransferVO.getReceiverNetworkCode(), PretupsI.INTERFACE_CATEGORY_VOMS);
                evdUtil.updateVoucherForFailedTransaction(p_c2sTransferVO, networkInterfaceModuleVOS, interfaceVO);
            } catch (Exception e) {
                // Event Handle to show that voucher could not be updated and is
                // still Under process
                _log.error("voucherUpdateSenderCreditBack", " For transfer ID=" + p_transferID + " Error while updating voucher status for =" + p_c2sTransferVO
                    .getSerialNumber() + " So leaving the voucher marked as under process. Exception: " + e.getMessage());
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "EvdController[voucherUpdateSenderCreditBack]",
                    p_transferID, "", "",
                    "Error while updating voucher status for =" + p_c2sTransferVO.getSerialNumber() + " So leaving the voucher marked as under process. Exception: " + e
                        .getMessage());
            }
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            if (_log.isDebugEnabled()) {
                _log.debug("voucherUpdateSenderCreditBack", "transferID=" + p_transferID + " Doing Sender Credit back ");
            }
            final UserBalancesVO userBalancesVO = evdUtil.updateSenderForFailedTransaction(con, p_c2sTransferVO);
            final C2STransferItemVO senderCreditBackItemVO = (C2STransferItemVO) p_c2sTransferVO.getTransferItemList().get(2);
            senderCreditBackItemVO.setUpdateStatus(((C2STransferItemVO) p_c2sTransferVO.getTransferItemList().get(0)).getUpdateStatus1());

            p_c2sTransferVO.setModifiedOn(new Date());
            p_c2sTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
            // Updating C2S Transfer details in database
            ChannelTransferBL.updateC2STransferDetails(con, p_c2sTransferVO);

            mcomCon.finalCommit();

            // Log the details if the transfer Details were added i.e. if User
            // was creditted
            if (userBalancesVO != null) {
                BalanceLogger.log(userBalancesVO);
            }

            finalTransferStatusUpdate = false;
        } catch (BTSLBaseException be) {
            _log.error("voucherUpdateSenderCreditBack", " For transfer ID=" + p_transferID + " Getting BTSL Base Exception: " + be.getMessage());
            finalTransferStatusUpdate = false;
            if (con != null) {
                try {
                	mcomCon.finalRollback();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
            _log.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            if (con != null) {
                try {
                	mcomCon.finalRollback();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
            _log.errorTrace(METHOD_NAME, e);
            finalTransferStatusUpdate = false;
            _log.error("voucherUpdateSenderCreditBack", " For transfer ID=" + p_transferID + " Getting Exception: " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EvdController[voucherUpdateSenderCreditBack]",
                p_transferID, "", "", "Error while credit back sender, getting exception: " + e.getMessage());
        } finally {
			if (mcomCon != null) {
				mcomCon.close("DeliveryReceiptServlet#voucherUpdateSenderCreditBack");
				mcomCon = null;
			}
        	con=null;
            if (_log.isDebugEnabled()) {
                _log.debug("voucherUpdateSenderCreditBack", "Exiting for _transferID=" + p_transferID);
            }
        }
        return finalTransferStatusUpdate;
    }

}
