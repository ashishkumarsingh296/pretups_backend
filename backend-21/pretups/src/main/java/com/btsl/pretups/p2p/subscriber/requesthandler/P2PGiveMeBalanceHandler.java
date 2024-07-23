package com.btsl.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.UDPClient;
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
import com.btsl.pretups.logging.GiveMeBalanceRequestResponseLog;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;


/**
 * * @(#)P2PGiveMeBalanceHandler.java
 * Copyright(c) 1999-2009, Comviva Technologies Ltd.
 * All Rights Reserved
 * 
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Mukesh Singh February,25 2010 Initial Creation
 * 
 * This class handles the request for balance initiated by subscriber to the
 * other subscriber
 * registered as a P2P Subscriber in pretups system.
 */
public class P2PGiveMeBalanceHandler implements ServiceKeywordControllerI {

    private Log _log = LogFactory.getLog(P2PGiveMeBalanceHandler.class.getName());
    private HashMap _requestMap = null;
    private RequestVO _requestVO = null;
    private SenderVO _senderVO = null;

    /**
     * This is the entry point and only public method of the class.The process
     * involved in the Give me balance request
     * for a subscriber are called from this method.
     * 
     * @param p_requestVO
     */
    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("process[P2PGiveMeBalanceHandler]", "Entered....: p_requestVO= " + p_requestVO);
        }
        final String methodName = "process";
        _requestVO = p_requestVO;
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            // retreiving the Map which has all the values in the request.These
            // values can be retreived by
            // using the tag name
            _requestMap = _requestVO.getRequestMap();
            // GiveMeBalanceRequestResponseLog to log request
            GiveMeBalanceRequestResponseLog.log(_requestVO);
            // retreiving senderVO from requestMap for the validation
            _senderVO = (SenderVO) _requestVO.getSenderVO();
            // validates the parameter passed in the request
            validate(con);
            // setting the transaction status to true
            _requestVO.setSuccessTxn(true);
            GiveMeBalanceRequestResponseLog.log(_requestVO, "validation successful", (String) _requestMap.get("MSISDN1"), (String) _requestMap.get("MSISDN2"),
                (String) _requestMap.get("AMOUNT"));
            final String pushMessage = getPUSHMessage();

            final UDPClient udpClient = new UDPClient(Constants.getProperty("FLARES_UDP_IP"), Constants.getProperty("FLARES_UDP_PORT"));
            final String responseStr = udpClient.sendMessage(pushMessage);
            String[] responseArr = null;

            if (!BTSLUtil.isNullString(responseStr)) {
                responseArr = responseStr.split("\\#");
            }

            if (!PretupsI.GATEWAY_MESSAGE_SUCCESS.equals(responseArr[0])) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2PGMB_ERROR_TIME_OUT);
            }
            _requestVO.setMessageCode(PretupsI.GATEWAY_MESSAGE_SUCCESS);

        } catch (BTSLBaseException be) {

            _requestVO.setSuccessTxn(false);
            // GiveMeBalanceRequestResponseLog logger to check response after
            // validation
            GiveMeBalanceRequestResponseLog.log(_requestVO, "validation failed", (String) _requestMap.get("MSISDN1"), (String) _requestMap.get("MSISDN2"),
                (String) _requestMap.get("AMOUNT"));
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(methodName, be);
            if (be.isKey()) {
                _requestVO.setMessageCode(be.getMessageKey());
                _requestVO.setMessageArguments(be.getArgs());
            } else {
                _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            // Rollbacking the transaction
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            // be.printStackTrace();
        }// end of BTSLBaseException
        catch (Exception ex) {
            _requestVO.setSuccessTxn(false);
            // Rollbacking the transaction
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(methodName, ee);
            }
            _log.error("process", "Exception " + ex.getMessage());
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PGiveMeBalanceHandler[process]", "", "", "",
                "Exception:" + ex.getMessage());
            _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        }// end of Exception
        finally {
            p_requestVO = _requestVO;
            p_requestVO.setRequestMap(_requestMap);
            _requestMap = null;
            _requestVO = null;
            _senderVO = null;
            // clossing database connection
			if (mcomCon != null) {
				mcomCon.close("P2PGiveMeBalanceHandler#process");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }// end of finally
        if (_log.isDebugEnabled()) {
            _log.debug("process", "Exiting ....: ");
        }
    }

    private String getPUSHMessage() {
        // TODO Auto-generated method stub
        final StringBuffer msgBuff = new StringBuffer();
        msgBuff.append("125:");
        msgBuff.append(BTSLUtil.encode("MENU_PUSH".getBytes()));
        final String msisdn = (String) _requestMap.get("MSISDN2");
        msgBuff.append("#100:");
        msgBuff.append(BTSLUtil.encode(msisdn.getBytes()));
        msgBuff.append("#128:");
        final String skippingString = (String) _requestMap.get("MSISDN1") + "*" + _requestMap.get("AMOUNT");
        msgBuff.append(BTSLUtil.encode(skippingString.getBytes()));// skipping
        // string
        msgBuff.append("#126:");
        msgBuff.append(BTSLUtil.encode("100".getBytes()));
        final String userID = Constants.getProperty("USERID");
        msgBuff.append("#129:");
        msgBuff.append(BTSLUtil.encode(userID.getBytes()));
        final String passwd = Constants.getProperty("PASSWD");
        msgBuff.append("#130:");
        msgBuff.append(BTSLUtil.encode(passwd.getBytes()));
        final String txnID = "";
        msgBuff.append("#124:");
        msgBuff.append(BTSLUtil.encode(txnID.getBytes()));
        final String menuHeader = "GMB Request: " + (String) _requestMap.get("MSISDN1") + "is requesting amount " + _requestMap.get("AMOUNT");
        msgBuff.append("#127:");
        msgBuff.append(BTSLUtil.encode(menuHeader.getBytes()));

        return msgBuff.toString();
    }

    /**
     * This methods gets the msisdn from the requestMap and validates it. It
     * checks whether the msisdn is not null.
     * Also it checks whether the filteredMsisdn is from the network passed in
     * the request and the amount is validated for
     * numeric
     * 
     * @throws BTSLBaseException
     */
    private void validate(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Entered: ");
        }
        final String methodName = "validate";
        final Object serviceObjVal = null;
        try {
            // validating the initiator msisdn
            final String msisdn1 = (String) _requestMap.get("MSISDN1");
            String msisdn1Prefix = null;
            String filteredMsisdn1 = null;
            filteredMsisdn1 = PretupsBL.getFilteredMSISDN(msisdn1);// filtering
            // the msisdn
            // for
            // country
            // independent
            // dial
            // format
            if (!BTSLUtil.isValidMSISDN(filteredMsisdn1)) {
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.P2PGMB_ERROR_INVALID_MSISDN1);
            }
            msisdn1Prefix = PretupsBL.getMSISDNPrefix(filteredMsisdn1);
            _requestVO.setFilteredMSISDN(filteredMsisdn1);
            NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdn1Prefix);
            if (networkPrefixVO == null) {
                throw new BTSLBaseException("P2PGiveMeBalanceHandler", "validate", PretupsErrorCodesI.P2PGMB_ERROR_UNSUPPORTED_NETWORK_MSISDN1);
            }
            // validating the donor msisdn
            final String msisdn2 = (String) _requestMap.get("MSISDN2");
            String msisdn2Prefix = null;
            String filteredMsisdn2 = null;
            filteredMsisdn2 = PretupsBL.getFilteredMSISDN(msisdn2);
            if (!BTSLUtil.isValidMSISDN(filteredMsisdn2)) {
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.P2PGMB_ERROR_INVALID_MSISDN2);
            }
            msisdn2Prefix = PretupsBL.getMSISDNPrefix(filteredMsisdn2);
            _requestVO.setFilteredMSISDN(filteredMsisdn2);
            networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdn2Prefix);
            if (networkPrefixVO == null) {
                throw new BTSLBaseException("P2PGiveMeBalanceHandler", "validate", PretupsErrorCodesI.P2PGMB_ERROR_UNSUPPORTED_NETWORK_MSISDN2);
            }
            // amount validation
            final String amount = (String) _requestMap.get("AMOUNT");
            if (!BTSLUtil.isNumeric(amount)) {
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.P2PGMB_ERROR_AMOUNT_NOT_NUMERIC);
            }
        } catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("validate", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PGiveMeBalanceHandler[validate]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("P2PGiveMeBalanceHandler", "validate", PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
            if (p_con != null) {
                try {
                    p_con.close();
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
            }
            p_con = null;
            if (_log.isDebugEnabled()) {
                _log.debug("validate", "Exiting: ");
            }
        }
    }
}
