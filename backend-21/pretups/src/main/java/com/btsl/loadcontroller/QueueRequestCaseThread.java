package com.btsl.loadcontroller;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Locale;

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
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.requesthandler.C2SPrepaidController;
import com.btsl.pretups.channel.transfer.requesthandler.EVDController;
import com.btsl.pretups.channel.transfer.requesthandler.PostPaidBillPaymentController;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.p2p.transfer.requesthandler.PrepaidController;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

/*
 * QueueRequestCaseThread.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 22/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class to process the request from the queue
 */

public class QueueRequestCaseThread extends Thread {

    private static Log _log = LogFactory.getLog(QueueRequestCaseThread.class.getName());
    private InterfaceLoadVO interfaceLoadVO = null;
    private String loadType = null;

    public QueueRequestCaseThread(InterfaceLoadVO p_interfaceLoadVO) {
        interfaceLoadVO = p_interfaceLoadVO;
        loadType = LoadControllerCache.getLoadType();
    }

    public void run() {
        if (_log.isDebugEnabled()) {
            _log.debug("QueueRequestCaseThread[run]", " Entered ");
        }
        ArrayList queueList = null;
        queueList = interfaceLoadVO.getQueueList();

        // long requestStartTime=0;
        long currentTime = System.currentTimeMillis();
        String transactionID = null;
        TransferVO transferVO = null;
        String instanceID = interfaceLoadVO.getInstanceID();
        String networkID = interfaceLoadVO.getNetworkCode();
        String interfaceID = interfaceLoadVO.getInterfaceID();
        long queueTimeOutSec = interfaceLoadVO.getQueueTimeOut();

        InterfaceLoadController interfaceLoadController = null;
        NetworkLoadController networkLoadController = null;
        InstanceLoadController instanceLoadController = null;

        if (_log.isDebugEnabled()) {
            _log.debug("QueueRequestCaseThread[run]", " Processing for instanceID=" + instanceID + " networkID=" + networkID + " interfaceID=" + interfaceID + " queueTimeOutSec=" + queueTimeOutSec + " with queueList=" + queueList);
        }
        if (queueList != null && !queueList.isEmpty()) {
            for (int i = 0, j = queueList.size(); i < j; i++) {
                // Check for Transfer VO time out case
                // If Timed out Remove from Queue, Decrase the Interface,
                // network and Instance Counters
                // If not then check load of service and pass that request to it
                // after decreaing the queue size counters.
                transferVO = (TransferVO) queueList.get(i);
                transactionID = transferVO.getTransferID();
                if (_log.isDebugEnabled()) {
                    _log.debug("QueueRequestCaseThread[run]", " Starting processing for Transaction ID=" + transactionID + " Check for timeout currentTime=" + currentTime + " transferVO.getQueueAdditionTime()=" + transferVO.getQueueAdditionTime() + " queueTimeOutSec=" + queueTimeOutSec);
                }

                interfaceLoadController = LoadControllerUtil.getInterfaceLoadObject(instanceID, networkID, interfaceID);

                if (currentTime > transferVO.getQueueAdditionTime() + queueTimeOutSec) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("QueueRequestCaseThread[run]", " Timeout for Transaction ID=" + transactionID + " Removing from Queue List and decreasing counters ");
                    }

                    queueList.remove(i);
                    networkLoadController = LoadControllerUtil.getNetworkLoadObject(instanceID, networkID);
                    instanceLoadController = LoadControllerUtil.getInstanceLoadObject(instanceID);

                    interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.TIMEOUT_FROM_QUEUE, networkID, interfaceID, transactionID);
                    networkLoadController.decreaseCurrentNetworkLoad(networkID, LoadControllerI.DEC_LAST_TRANS_COUNT);
                    instanceLoadController.decreaseCurrentInstanceLoad(instanceID, LoadControllerI.DEC_LAST_TRANS_COUNT);

                    // Add in database for the timeout entry
                    addEntryInTransfers(transferVO);
                    if (_log.isDebugEnabled()) {
                        _log.debug("processRequestFromQueue", "After adding entries in DB for Time out error code for p_transactionID=" + transferVO.getTransferID() + " request timeout from queue");
                    }

                } else {
                    if (_log.isDebugEnabled()) {
                        _log.debug("QueueRequestCaseThread[run]", " Transaction ID=" + transactionID + " transferVO.getModule()=" + transferVO.getModule() + " Started process");
                    }

                    if (PretupsI.C2S_MODULE.equals(transferVO.getModule())) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("QueueRequestCaseThread[run]", " Transaction ID=" + transactionID + " Check Interface Load networkID=" + networkID + "interfaceID=" + interfaceID + " Set Queue Addition to false");
                        }

                        int k = interfaceLoadController.checkInterfaceLoad(networkID, interfaceID, transactionID, transferVO, false);
                        if (_log.isDebugEnabled()) {
                            _log.debug("QueueRequestCaseThread[run]", " Transaction ID=" + transactionID + " Check Interface Load networkID=" + networkID + "interfaceID=" + interfaceID + " Set Queue Addition to false Got Status as (0 means Process)=" + k);
                        }

                        if (k == 0) // Can be processed
                        {
                            interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.PROCESSED_FROM_QUEUE, networkID, interfaceID, transactionID);
                            queueList.remove(i);

                            // Pass the request for further processing and break
                            // the loop so that next is not processed
                            if (_log.isDebugEnabled()) {
                                _log.debug("QueueRequestCaseThread[run]", " Transaction ID=" + transactionID + " Calling controller methods to process the request");
                            }
                            processRequestFromQueue(transactionID, transferVO);
                            break;
                        }
                    } else {
                        if (_log.isDebugEnabled()) {
                            _log.debug("QueueRequestCaseThread[run]", " Transaction ID=" + transactionID + " Checking for sender interface load on Network =" + ((SenderVO) transferVO.getSenderVO()).getNetworkCode() + " Interface=" + transferVO.getSenderTransferItemVO().getInterfaceID() + " Set Queue Addition to false");
                        }

                        int senderLoadStatus = LoadController.checkInterfaceLoad(((SenderVO) transferVO.getSenderVO()).getNetworkCode(), transferVO.getSenderTransferItemVO().getInterfaceID(), transactionID, transferVO, false);
                        int recieverLoadStatus = 0;
                        // Further process the request
                        if (_log.isDebugEnabled()) {
                            _log.debug("QueueRequestCaseThread[run]", " Transaction ID=" + transactionID + " Sender Load Status (0 means check for receiver Load)=" + senderLoadStatus);
                        }

                        if (senderLoadStatus == 0) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("QueueRequestCaseThread[run]", " Transaction ID=" + transactionID + " Checking for Receiver interface load on Network =" + ((ReceiverVO) transferVO.getReceiverVO()).getNetworkCode() + " Interface=" + transferVO.getReceiverTransferItemVO().getInterfaceID() + " Set Queue Addition to false");
                            }

                            recieverLoadStatus = LoadController.checkInterfaceLoad(((ReceiverVO) transferVO.getReceiverVO()).getNetworkCode(), transferVO.getReceiverTransferItemVO().getInterfaceID(), transactionID, transferVO, false);
                            if (_log.isDebugEnabled()) {
                                _log.debug("QueueRequestCaseThread[run]", " Transaction ID=" + transactionID + " Receiver Load Status (0 means process request)=" + recieverLoadStatus);
                            }

                            if (recieverLoadStatus == 0) // Can be processed
                            {
                                interfaceLoadController.decreaseCurrentInterfaceLoad(LoadControllerI.PROCESSED_FROM_QUEUE, networkID, interfaceID, transactionID);
                                queueList.remove(i);

                                // Pass the request for further processing and
                                // break the loop so that next is not processed
                                if (_log.isDebugEnabled()) {
                                    _log.debug("QueueRequestCaseThread[run]", " Transaction ID=" + transactionID + " Calling controller methods to process the request");
                                }

                                processRequestFromQueue(transactionID, transferVO);
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("QueueRequestCaseThread[run]", " Exiting");
        }
    }

    /**
     * Method to add entry in database for time out case from queue
     * 
     * @param p_transferVO
     */
    private void addEntryInTransfers(TransferVO p_transferVO) {
        _log.info("addEntryInTransfers", p_transferVO.getTransferID(), "Adding transfer details in database for queue time out case");
        final String METHOD_NAME = "addEntryInTransfers";
        Connection con = null;MComConnectionI mcomCon = null;
        try {
        	mcomCon = new MComConnection();con=mcomCon.getConnection();
            p_transferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);

            if (PretupsI.C2S_MODULE.equals(p_transferVO.getModule())) {
                p_transferVO.setErrorCode(PretupsErrorCodesI.REQ_TIMEOUT_FROM_QUEUE_C2S);
                ChannelTransferBL.addC2STransferDetails(con, (C2STransferVO) p_transferVO);
                mcomCon.finalCommit();
            } else {
                p_transferVO.setErrorCode(PretupsErrorCodesI.REQ_TIMEOUT_FROM_QUEUE_P2P);
                PretupsBL.addTransferDetails(con, (P2PTransferVO) p_transferVO);
                mcomCon.finalCommit();
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            if (con != null) {
                try {
                	mcomCon.finalRollback();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
            _log.error("addEntryInTransfers", p_transferVO.getTransferID(), "BTSLBaseException while adding transfer details in database:" + be.getMessage());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            if (con != null) {
                try {
                	mcomCon.finalRollback();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueRequestCaseThread[addEntryInTransfers]", p_transferVO.getTransferID(), "", "", "Exception:" + e.getMessage());
            _log.error("addEntryInTransfers", p_transferVO.getTransferID(), "Exception while adding transfer details in database:" + e.getMessage());
        } finally {
        	if(mcomCon != null){mcomCon.close("QueueRequestCaseThread#addEntryInTransfers");mcomCon=null;}
            pushSenderFailMessage(p_transferVO);
        }
    }

    /**
     * Method to push message to the sender of the request timeout
     * 
     * @param p_transferVO
     */
    private void pushSenderFailMessage(TransferVO p_transferVO) {
        if (PretupsI.C2S_MODULE.equals(p_transferVO.getModule())) {
            String messageToSend = BTSLUtil.getMessage(p_transferVO.getLocale(), PretupsErrorCodesI.REQ_TIMEOUT_FROM_QUEUE_C2S, p_transferVO.getMessageArguments());
            (new PushMessage(((SenderVO) p_transferVO.getSenderVO()).getMsisdn(), messageToSend, p_transferVO.getTransferID(), p_transferVO.getRequestGatewayCode(), p_transferVO.getLocale())).push();
        } else {
            String messageToSend = BTSLUtil.getMessage(p_transferVO.getLocale(), PretupsErrorCodesI.REQ_TIMEOUT_FROM_QUEUE_P2P, p_transferVO.getMessageArguments());
            (new PushMessage(((ChannelUserVO) p_transferVO.getSenderVO()).getUserPhoneVO().getMsisdn(), messageToSend, p_transferVO.getTransferID(), p_transferVO.getRequestGatewayCode(), p_transferVO.getLocale())).push();
        }
    }

    /**
     * Method to process the request from queue
     * 
     * @param p_transactionID
     */
    private void processRequestFromQueue(String p_transactionID, TransferVO p_transferVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("processRequestFromQueue", "Entered p_transactionID=" + p_transactionID + " p_transferVO=" + p_transferVO);
        }
        final String METHOD_NAME = "processRequestFromQueue";
        try {
            String serviceType = p_transferVO.getServiceType();
            if (PretupsI.SERVICE_TYPE_P2PRECHARGE.equals(serviceType)) {
                new PrepaidController().processFromQueue(p_transferVO);
                // return message to sender
                // pass default locale also
                Locale defaultLocale = p_transferVO.getLocale();
                String messageToSend = null;
                if (!BTSLUtil.isNullString(p_transferVO.getSenderReturnMessage())) {
                    messageToSend = p_transferVO.getSenderReturnMessage();
                } else {
                    messageToSend = BTSLUtil.getMessage(defaultLocale, p_transferVO.getMessageCode(), p_transferVO.getMessageArguments());
                }
                // push success message to sender and receiver
                if (p_transferVO.isUnderProcessMsgReq()) {
                    (new PushMessage(((SenderVO) p_transferVO.getSenderVO()).getMsisdn(), messageToSend, p_transferVO.getTransferID(), p_transferVO.getRequestGatewayCode(), defaultLocale)).push();
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("processRequestFromQueue", "p_transactionID=" + p_transactionID + " successfully process");
                }
            } else if (PretupsI.SERVICE_TYPE_CHNL_RECHARGE.equals(serviceType)) {
                new C2SPrepaidController().processFromQueue(p_transferVO);
                // return message to sender
                // pass default locale also
                Locale defaultLocale = p_transferVO.getLocale();
                String messageToSend = null;

                if (!BTSLUtil.isNullString(p_transferVO.getSenderReturnMessage())) {
                    messageToSend = p_transferVO.getSenderReturnMessage();
                } else {
                    messageToSend = BTSLUtil.getMessage(defaultLocale, p_transferVO.getMessageCode(), p_transferVO.getMessageArguments());
                }
                // push success message to sender and receiver
                if (p_transferVO.isUnderProcessMsgReq()) {
                    (new PushMessage(((ChannelUserVO) p_transferVO.getSenderVO()).getUserPhoneVO().getMsisdn(), messageToSend, p_transferVO.getTransferID(), p_transferVO.getRequestGatewayCode(), defaultLocale)).push();
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("processRequestFromQueue", "p_transactionID=" + p_transactionID + " successfully process");
                }
            } else if (PretupsI.SERVICE_TYPE_BILLPAYMENT.equals(serviceType)) {
                new PostPaidBillPaymentController().processFromQueue(p_transferVO);
                // return message to sender
                // pass default locale also
                Locale defaultLocale = p_transferVO.getLocale();
                String messageToSend = null;

                if (!BTSLUtil.isNullString(p_transferVO.getSenderReturnMessage())) {
                    messageToSend = p_transferVO.getSenderReturnMessage();
                } else {
                    messageToSend = BTSLUtil.getMessage(defaultLocale, p_transferVO.getMessageCode(), p_transferVO.getMessageArguments());
                }
                // push success message to sender and receiver
                if (p_transferVO.isUnderProcessMsgReq()) {
                    (new PushMessage(((ChannelUserVO) p_transferVO.getSenderVO()).getUserPhoneVO().getMsisdn(), messageToSend, p_transferVO.getTransferID(), p_transferVO.getRequestGatewayCode(), defaultLocale)).push();
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("processRequestFromQueue", "p_transactionID=" + p_transactionID + " successfully process");
                }
            } else if (PretupsI.SERVICE_TYPE_EVD.equals(serviceType) || PretupsI.SERVICE_TYPE_EVR.equals(serviceType)) {
                new EVDController().processFromQueue(p_transferVO);
                // return message to sender
                // pass default locale also
                Locale defaultLocale = p_transferVO.getLocale();
                String messageToSend = null;

                if (!BTSLUtil.isNullString(p_transferVO.getSenderReturnMessage())) {
                    messageToSend = p_transferVO.getSenderReturnMessage();
                } else {
                    messageToSend = BTSLUtil.getMessage(defaultLocale, p_transferVO.getMessageCode(), p_transferVO.getMessageArguments());
                }
                // push success message to sender and receiver
                if (p_transferVO.isUnderProcessMsgReq()) {
                    (new PushMessage(((ChannelUserVO) p_transferVO.getSenderVO()).getUserPhoneVO().getMsisdn(), messageToSend, p_transferVO.getTransferID(), p_transferVO.getRequestGatewayCode(), defaultLocale)).push();
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("processRequestFromQueue", "p_transactionID=" + p_transactionID + " successfully process");
                }
            }
        } catch (Exception e) {
            _log.error("processRequestFromQueue", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            if (PretupsI.P2P_MODULE.equals(p_transferVO.getModule())) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueRequestCaseThread[processRequestFromQueue]", p_transactionID, ((SenderVO) p_transferVO.getSenderVO()).getMsisdn(), ((SenderVO) p_transferVO.getSenderVO()).getNetworkCode(), "Exception:" + e.getMessage());
                if (!BTSLUtil.isNullString(p_transferVO.getSenderReturnMessage())) {
                    (new PushMessage(((SenderVO) p_transferVO.getSenderVO()).getMsisdn(), p_transferVO.getSenderReturnMessage(), p_transferVO.getTransferID(), p_transferVO.getRequestGatewayCode(), p_transferVO.getLocale())).push();
                }
            } else {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueRequestCaseThread[processRequestFromQueue]", p_transactionID, ((ChannelUserVO) p_transferVO.getSenderVO()).getUserPhoneVO().getMsisdn(), ((ChannelUserVO) p_transferVO.getSenderVO()).getNetworkID(), "Exception:" + e.getMessage());
                // Push Sender Fail message if any
                if (!BTSLUtil.isNullString(p_transferVO.getSenderReturnMessage())) {
                    (new PushMessage(((ChannelUserVO) p_transferVO.getSenderVO()).getUserPhoneVO().getMsisdn(), p_transferVO.getSenderReturnMessage(), p_transferVO.getTransferID(), p_transferVO.getRequestGatewayCode(), p_transferVO.getLocale())).push();
                }
            }

        }
    }
}
