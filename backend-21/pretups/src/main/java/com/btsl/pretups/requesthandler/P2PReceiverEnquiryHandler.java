/*
 * @(#)P2PReceiverEnquiryHandler.java
 * 
 * Name Date History
 * ------------------------------------------------------------------------
 * Manish K. Singh 13/12/2006 Initial Creation
 * 
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 * 
 * This program processes the request for Customer P2P Receiver enquiry for
 * MSISDN, FromDate, ToDate, TransactionID.
 * It loads the Customer P2P Receiver transaction Summary(ies) (arTransferVOs
 * object)
 * or transaction Details (arTransferDetailsVOs object) and sets it in Hashmap
 * response in (RequestVO setValueObject)
 * If MSISDN is there processing will be on the basis of it. and Summary Records
 * will be returned
 * If MSISDN, TransactionID are there, Processing will be based on MSISDN and
 * TransactionID, and a detailed record
 * corresponding to the transactionId will be returned.
 * Dates FromDate and ToDate are optional
 */
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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.p2p.query.businesslogic.ReceiverTransferDAO;
import com.btsl.pretups.p2p.query.businesslogic.SubscriberTransferDAO;
import com.btsl.pretups.p2p.query.businesslogic.TransferDetailsVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

/**
 * com.btsl.pretups.requesthandler
 * P2PReceiverEnquiryHandler.java
 * 
 * This program processes the request for Customer P2P Receiver enquiry for
 * MSISDN, FromDate, ToDate, TransactionID.
 * It loads the Customer P2P Receiver transaction Summary(ies) (arTransferVOs
 * object)
 * or transaction Details (arTransferDetailsVOs object) and sets it in Hashmap
 * response in (RequestVO setValueObject)
 * If MSISDN is there processing will be on the basis of it. and Summary Records
 * will be returned
 * If MSISDN, TransactionID are there, Processing will be based on MSISDN and
 * TransactionID, and a detailed record
 * corresponding to the transactionId will be returned.
 * Dates FromDate and ToDate are optional
 */
public class P2PReceiverEnquiryHandler implements ServiceKeywordControllerI {

    private Log log = LogFactory.getLog(this.getClass().getName());
    private HashMap _requestMap = null;
    private static final String MSISDN_STR = "MSISDN2";
    private static final String TRANSACTION_ID = "TRANSACTIONID";

    /**
     * This method is the entry point in the class. The method is declared in
     * the Interface ServiceKeywordControllerI
     * This method processes the request for the ReceiverMsisdn, TRANSACTIONID,
     * FROMDATE and TODATE
     * calls the validate() for validating MSISDN, LOGINID, FROMDATE and TODATE
     * and calls loadTransferData() that sets the Customer P2P Receiver
     * transaction details in the p_requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (log.isDebugEnabled()) {
            log.debug("process", "Entered.....p_requestVO=" + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        _requestMap = p_requestVO.getRequestMap();
        try {
            // validating the request. if MSISDN, FROMDATE, TODATE are given
            // these will get set in the requestTransferVO
            // if TRANSACTIONID is not given then "" string or else the value is
            // set in the requestTransferVO
            TransferVO requestTransferVO = validate();

            mcomCon = new MComConnection();con=mcomCon.getConnection();

            // Getting the Transactions Summary(ies) or a transaction Details
            loadTransferData(con, requestTransferVO, p_requestVO);

            p_requestVO.setSuccessTxn(true);

        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            log.error("process", p_requestVO.getRequestIDStr(), "BTSLBaseException " + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                String[] args = be.getArgs();
                p_requestVO.setMessageArguments(args);
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                log.errorTrace(METHOD_NAME, ee);
            }
            log.error("process", p_requestVO.getRequestIDStr(), "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ViewChannelUserHandler[process]", "", "", "", "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("P2PReceiverEnquiryHandler#process");
				mcomCon = null;
			}
            p_requestVO.setRequestMap(_requestMap);
            if (log.isDebugEnabled()) {
                log.debug("process", p_requestVO.getRequestIDStr(), "Exited.....p_requestVO=" + p_requestVO);
            }
        }
    }

    /**
     * This method checks for the mandatory value ReceiverMsisdn OR
     * TRANSACTIONID is not null,
     * if msisdn is there, then Network Prefix and supporting network are there
     * or not and fromDate and toDate Checks
     * 
     * @return requestTransferVO TransferVO
     * @throws BTSLBaseException
     */
    private TransferVO validate() throws BTSLBaseException {
        final String METHOD_NAME = "validate";
        if (log.isDebugEnabled()) {
            log.debug("validate", "Entered.....");
        }
        String transactionID = null;
        String msisdn = null;
        String fromDateStr = null;
        String toDateStr = null;
        String filteredMsisdn = null;
        String msisdnPrefix = null;
        NetworkPrefixVO networkPrefixVO = null;
        TransferVO requestTransferVO = new TransferVO();
        try {
            msisdn = (String) _requestMap.get(MSISDN_STR);
            fromDateStr = (String) _requestMap.get("FROMDATE");
            toDateStr = (String) _requestMap.get("TODATE");
            transactionID = (String) _requestMap.get(TRANSACTION_ID);

            if (BTSLUtil.isNullString(msisdn)) {
                _requestMap.put("RES_ERR_KEY", "MSISDN"); // tbd
                _requestMap.put("RES_TYPE", "NA");
                if (log.isDebugEnabled()) {
                    log.debug("validate", "Missing mandatory value: MSISDN");
                }
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            }
            filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn); // before
                                                                  // process
                                                                  // MSISDN
                                                                  // filter
                                                                  // each-one
            // check the MSISDN is valid or not
            if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                _requestMap.put("RES_ERR_KEY", MSISDN_STR);
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
            }
            // get prefix of the MSISDN
            msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); // get the
                                                                      // prefix
                                                                      // of the
                                                                      // MSISDN
            networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            if (networkPrefixVO == null) {
                if (log.isDebugEnabled()) {
                    log.debug("validate", "No Network prefix found for msisdn=" + msisdn);
                }
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK);
            }

            // NOT Checking the network as receiver could be from outside
            // network
            // Setting the MSISDN
            SenderVO senderVO = new SenderVO();
            senderVO.setMsisdn(msisdn);
            requestTransferVO.setSenderVO(senderVO);

            // date validation and date setting to dateStr variables
            HashMap hmdates = HandlerUtil.dateValidation(fromDateStr, toDateStr);
            requestTransferVO.setFromDate((String) hmdates.get(HandlerUtil.FROM_DATE_STR));
            requestTransferVO.setToDate((String) hmdates.get(HandlerUtil.TO_DATE_STR));

            // Setting the transactionID
            if (!BTSLUtil.isNullString(transactionID)) {
                requestTransferVO.setTransferID(transactionID);
            } else {
                requestTransferVO.setTransferID("");
            }
        } catch (BTSLBaseException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("validate", "BTSLBaseException " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("validate", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IccidMsisdnHandler[validate]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (log.isDebugEnabled()) {
            log.debug("validate", "Exiting.....");
        }
        return requestTransferVO;
    }

    /**
     * This method loads the transaction summary(ies) if transaction id is not
     * present in the _requestMap
     * and loads a transaction details (a summary + a detail) if transaction id
     * is present in the _requestMap
     * It sets the response hashMap as {"SUMMARY", 'arraylist of TransferVOs'}
     * and {"DETAILS", 'arraylist of TransferItemVOs'}
     * 
     * @param p_con
     * @param p_requestTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    private void loadTransferData(Connection p_con, TransferVO p_requestTransferVO, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "loadTransferData";
        if (log.isDebugEnabled()) {
            log.debug("loadTransferData", "Entered.....");
        }
        try {
            HashMap hmResponse = new HashMap();
            ArrayList alTransferVOs = null;
            ReceiverTransferDAO receiverTransferDAO = new ReceiverTransferDAO();
            SubscriberTransferDAO subscriberTransferDAO = new SubscriberTransferDAO();
            alTransferVOs = receiverTransferDAO.loadReceiverDetails(p_con, p_requestTransferVO);

            if (alTransferVOs == null || alTransferVOs.isEmpty()) {
                throw new BTSLBaseException(this, "loadTransferData", PretupsErrorCodesI.CCE_ERROR_TRANSFER_SUMMARY_NOT_FOUND, new String[] { (String) _requestMap.get(MSISDN_STR) });
            } else if (BTSLUtil.isNullString((String) _requestMap.get(TRANSACTION_ID))) { // if
                                                                                          // transactionId
                                                                                          // null
                                                                                          // fetch
                                                                                          // the
                                                                                          // summaries
                                                                                          // for
                                                                                          // transactions
                hmResponse.put("SUMMARY", alTransferVOs);
                _requestMap.put("RES_TYPE", "SUMMARY");
                p_requestVO.setValueObject(hmResponse); // Setting the response
                                                        // object for multiple
                                                        // transactions
            } else { // if a transactionID is there, fetch the summary and
                     // details
                TransferVO transactionTransferVO = (TransferVO) alTransferVOs.get(0);
                ArrayList alTransferDetails = subscriberTransferDAO.loadSubscriberItemList(p_con, transactionTransferVO, new TransferItemVO());
                if (alTransferDetails == null || alTransferDetails.isEmpty()) {
                    throw new BTSLBaseException(this, "loadTransferData", PretupsErrorCodesI.CCE_ERROR_TRANSFER_DETAILS_NOT_FOUND, new String[] { (String) _requestMap.get(TRANSACTION_ID) });
                }

                ArrayList alTransferItemVO = new ArrayList();
                int transferDetails = alTransferDetails.size();
                for (int i = 0; i < transferDetails; i++) {
                    alTransferItemVO.add(((TransferDetailsVO) alTransferDetails.get(i)).getTransferItemVO());
                }

                hmResponse.put("SUMMARY", alTransferVOs);
                hmResponse.put("DETAILS", alTransferItemVO);
                _requestMap.put("RES_TYPE", "DETAILS");
                p_requestVO.setValueObject(hmResponse); // Setting the response
                                                        // object for a
                                                        // transactionID
            }
        } catch (BTSLBaseException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("loadTransferData", "BTSLBaseException " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("loadTransferData", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IccidMsisdnHandler[loadTransferData]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadTransferData", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (log.isDebugEnabled()) {
            log.debug("loadTransferData", "Exiting.....");
        }
    }

}
