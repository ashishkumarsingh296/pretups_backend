package com.selftopup.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.util.HashMap;

import java.util.Base64;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.UDPClient;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.logging.GiveMeBalanceRequestResponseLog;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixCache;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixVO;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;
import com.selftopup.util.OracleUtil;

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
        if (_log.isDebugEnabled())
            _log.debug("process[P2PGiveMeBalanceHandler]", "Entered....: p_requestVO= " + p_requestVO);

        _requestVO = p_requestVO;
        Connection con = null;
        try {
            con = OracleUtil.getConnection();
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
            GiveMeBalanceRequestResponseLog.log(_requestVO, "validation successful", (String) _requestMap.get("MSISDN1"), (String) _requestMap.get("MSISDN2"), (String) _requestMap.get("AMOUNT"));
            String pushMessage = getPUSHMessage();

            UDPClient udpClient = new UDPClient(Constants.getProperty("FLARES_UDP_IP"), Constants.getProperty("FLARES_UDP_PORT"));
            String responseStr = udpClient.sendMessage(pushMessage);
            String[] responseArr = null;

            if (!BTSLUtil.isNullString(responseStr))
                responseArr = responseStr.split("\\#");

            if (!PretupsI.GATEWAY_MESSAGE_SUCCESS.equals(responseArr[0])) {
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2PGMB_ERROR_TIME_OUT);
            }
            _requestVO.setMessageCode(PretupsI.GATEWAY_MESSAGE_SUCCESS);

        } catch (BTSLBaseException be) {

            _requestVO.setSuccessTxn(false);
            // GiveMeBalanceRequestResponseLog logger to check response after
            // validation
            GiveMeBalanceRequestResponseLog.log(_requestVO, "validation failed", (String) _requestMap.get("MSISDN1"), (String) _requestMap.get("MSISDN2"), (String) _requestMap.get("AMOUNT"));
            _log.error("process", "BTSLBaseException " + be.getMessage());
            if (be.isKey()) {
                _requestVO.setMessageCode(be.getMessageKey());
                _requestVO.setMessageArguments(be.getArgs());
            } else
                _requestVO.setMessageCode(SelfTopUpErrorCodesI.REQ_NOT_PROCESS);
            // Rollbacking the transaction
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
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
            }
            _log.error("process", "Exception " + ex.getMessage());
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PGiveMeBalanceHandler[process]", "", "", "", "Exception:" + ex.getMessage());
            _requestVO.setMessageCode(SelfTopUpErrorCodesI.REQ_NOT_PROCESS);
            return;
        }// end of Exception
        finally {
            p_requestVO = _requestVO;
            p_requestVO.setRequestMap(_requestMap);
            _requestMap = null;
            _requestVO = null;
            _senderVO = null;
            // clossing database connection
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("process", " Exited ");
        }// end of finally
        if (_log.isDebugEnabled())
            _log.debug("process", "Exiting ....: ");
    }

    private String getPUSHMessage() {
        // TODO Auto-generated method stub
        StringBuffer msgBuff = new StringBuffer();
        BASE64Encoder encoder = new BASE64Encoder();
        msgBuff.append("125:");
        msgBuff.append(encoder.encode("MENU_PUSH".getBytes()));
        String msisdn = (String) _requestMap.get("MSISDN2");
        msgBuff.append("#100:");
        msgBuff.append(encoder.encode(msisdn.getBytes()));
        msgBuff.append("#128:");
        String skippingString = (String) _requestMap.get("MSISDN1") + "*" + _requestMap.get("AMOUNT");
        msgBuff.append(encoder.encode(skippingString.getBytes()));// skipping
                                                                  // string
        msgBuff.append("#126:");
        msgBuff.append(encoder.encode("100".getBytes()));
        String userID = Constants.getProperty("USERID");
        msgBuff.append("#129:");
        msgBuff.append(encoder.encode(userID.getBytes()));
        String passwd = Constants.getProperty("PASSWD");
        msgBuff.append("#130:");
        msgBuff.append(encoder.encode(passwd.getBytes()));
        String txnID = "";
        msgBuff.append("#124:");
        msgBuff.append(encoder.encode(txnID.getBytes()));
        String menuHeader = "GMB Request: " + (String) _requestMap.get("MSISDN1") + "is requesting amount " + _requestMap.get("AMOUNT");
        msgBuff.append("#127:");
        msgBuff.append(encoder.encode(menuHeader.getBytes()));

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
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered: ");
        Object serviceObjVal = null;
        try {
            // validating the initiator msisdn
            String msisdn1 = (String) _requestMap.get("MSISDN1");
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
                throw new BTSLBaseException(this, "validate", SelfTopUpErrorCodesI.P2PGMB_ERROR_INVALID_MSISDN1);
            }
            msisdn1Prefix = PretupsBL.getMSISDNPrefix(filteredMsisdn1);
            _requestVO.setFilteredMSISDN(filteredMsisdn1);
            NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdn1Prefix);
            if (networkPrefixVO == null) {
                throw new BTSLBaseException("P2PGiveMeBalanceHandler", "validate", SelfTopUpErrorCodesI.P2PGMB_ERROR_UNSUPPORTED_NETWORK_MSISDN1);
            }
            // validating the donor msisdn
            String msisdn2 = (String) _requestMap.get("MSISDN2");
            String msisdn2Prefix = null;
            String filteredMsisdn2 = null;
            filteredMsisdn2 = PretupsBL.getFilteredMSISDN(msisdn2);
            if (!BTSLUtil.isValidMSISDN(filteredMsisdn2)) {
                throw new BTSLBaseException(this, "validate", SelfTopUpErrorCodesI.P2PGMB_ERROR_INVALID_MSISDN2);
            }
            msisdn2Prefix = PretupsBL.getMSISDNPrefix(filteredMsisdn2);
            _requestVO.setFilteredMSISDN(filteredMsisdn2);
            networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdn2Prefix);
            if (networkPrefixVO == null) {
                throw new BTSLBaseException("P2PGiveMeBalanceHandler", "validate", SelfTopUpErrorCodesI.P2PGMB_ERROR_UNSUPPORTED_NETWORK_MSISDN2);
            }
            // amount validation
            String amount = (String) _requestMap.get("AMOUNT");
            if (!BTSLUtil.isNumeric(amount)) {
                throw new BTSLBaseException(this, "validate", SelfTopUpErrorCodesI.P2PGMB_ERROR_AMOUNT_NOT_NUMERIC);
            }
        } catch (BTSLBaseException be) {
            _log.error("validate", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("validate", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PGiveMeBalanceHandler[validate]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("P2PGiveMeBalanceHandler", "validate", SelfTopUpErrorCodesI.REQ_NOT_PROCESS);
        } finally {
            if (p_con != null) {
                try {
                    p_con.close();
                } catch (Exception e) {
                }
            }
            p_con = null;
            if (_log.isDebugEnabled())
                _log.debug("validate", "Exiting: ");
        }
    }
}
