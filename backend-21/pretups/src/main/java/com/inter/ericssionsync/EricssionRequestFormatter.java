package com.inter.ericssionsync;

/**
 * @(#)EricssionRequestFormatter.java
 *                                    Copyright(c) 2005, Bharti Telesoft Int.
 *                                    Public Ltd.
 *                                    All Rights Reserved
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Author Date History
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Abhijit Chauhan June 22,2005 Initial
 *                                    Creation
 *                                    Ashish Kumar July 12,2006 Modification
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    ------------
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;

public class EricssionRequestFormatter {
    public Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * This method is used to generate the Unique IN_RECON_ID for the
     * transaction
     * This Unique ID is generated from the system.
     * 1.Get a database connection from connection pool
     * 2.Select a Transaction id based on sequence,Table is IN_SEQUENCE_ID
     * 3.Make the transaction id's length equal to 6.(This may be configurable?)
     * 
     * @param p_requestMap
     * @return
     */

    private String getINReconTxnID(HashMap p_requestMap) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getINReconTxnID", "Entered  p_requestMap = " + p_requestMap);
        // This method will be used when we have transID based on database
        // sequence.
        Date date = null;
        int minutes = 0;
        String mintStr = "";
        String counter = "";
        int inTxnLength = 4;
        String interfaceID = null;
        int length = 0;
        int tmpLength = 0;
        // Commented code is used when we get the transaction ID from database
        // sequence.
        // String interfaceID=null;
        // String counterStr="";
        /*
         * Connection conn=null;
         * Statement stmt = null;
         * String inReconID=null;
         * ResultSet rs = null;
         * String interfaceID=null;
         * int length=0;
         * int tmpLength=0;
         * int inTxnLength=6;
         * try
         * {
         * interfaceID=(String)p_requestMap.get("INTERFACE_ID");
         * conn = OracleUtil.getConnection();
         * String selectQuery = "SELECT SEQ_TRANS_ID.nextval FROM dual";
         * stmt = conn.createStatement();
         * rs = stmt.executeQuery(selectQuery);
         * 
         * if(rs.next())
         * inReconID = rs.getString(1);
         * //Check the length of the sequence if it not equal to 6 then
         * String inTxnLengthStr =
         * FileCache.getValue(interfaceID,"IN_TXN_LENGTH");
         * if(inTxnLengthStr!=null)
         * inTxnLength = Integer.parseInt(inTxnLengthStr);
         * if(inReconID!=null)
         * {
         * length = inReconID.length();
         * tmpLength=inTxnLength-length;
         * if(length<inTxnLength)
         * {
         * for(int i=0;i<tmpLength;i++)
         * inReconID = "0"+inReconID;
         * }
         * 
         * }
         * //Adding the inTxnID to the requestMap
         * p_requestMap.put("IN_RECON_ID",inReconID);
         */
        try {
            date = new Date();
            minutes = date.getMinutes();
            if (minutes < 10)
                mintStr = "0" + minutes;
            else
                mintStr = String.valueOf(minutes);
            interfaceID = (String) p_requestMap.get("INTERFACE_ID");
            String inTxnLengthStr = FileCache.getValue(interfaceID, "IN_TXN_LENGTH");
            if (inTxnLengthStr != null)
                inTxnLength = Integer.parseInt(inTxnLengthStr);
            counter = EricssionValidation.getIncrCounter();
            if (_log.isDebugEnabled())
                _log.debug("getINReconTxnID", "counter value from EricsionValidation is " + counter);
            length = counter.length();
            tmpLength = inTxnLength - length;
            if (length < inTxnLength) {
                for (int i = 0; i < tmpLength; i++)
                    counter = "0" + counter;
            }
            counter = mintStr + counter;
            p_requestMap.put("IN_RECON_ID", counter);
            p_requestMap.put("IN_TXN_ID", counter);// Put the IN_RECON_ID and
                                                   // IN_TXN_ID same discussed
                                                   // with Abhijit.

        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getINReconTxnID", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getINReconTxnID]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getINReconTxnID", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }// end of catch-Exception
        finally {
            // Uncomment the bellow if we are generating transaction from db
            // sequence.
            // try{if(conn!=null)conn.close();}catch(Exception ee){}
            if (_log.isDebugEnabled())
                _log.debug("getINReconTxnID", "Exiting counter = " + counter);
        }// end of finally
        return counter;
    }

    /**
     * This method is responsible to generate a request format for the
     * PPSubsData
     * PPSubsData request is resposible to get the subsciber data like Service
     * class, Language and FirstCall flag value.
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    public String getINRequestToPPSubsData(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("getINRequestToPPSubsData", "Entered p_map = " + p_map);
        String requestString = null;
        try {
            // requestString="GET PPSubsData?TransId="+(String)p_map.get("IN_TXN_ID")
            // + getRequestString(4,p_map);
            requestString = "GET PPSubsData?TransId=" + getINReconTxnID(p_map) + getRequestString(4, p_map);
            p_map.put("IN_REQUEST_STR", requestString);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("getINRequestToPPSubsData", "Exception e = " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getINRequestToPPSubsData]", "", " INTERFACE ID = " + (String) p_map.get("INTERFACE_ID"), "NETWORK_CODE = " + (String) p_map.get("NETWORK_CODE"), "Exception e = " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PROCESS_REQUEST_ERROR);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getINRequestToPPSubsData", "Exiting requestString = " + requestString);
        }// end of finally
        return requestString;
    }// end of getINRequestToPPSubsData

    /**
     * This method is useful to generate request string for PPSubsData messages
     * to IVRFirstCall.
     * This request is send to IN when operator sets FIRST_FLAG equal to "Y" and
     * subcriber has FirstCall value equals "N".
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    public String getINPPSubsDataToIVRFirstCall(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("getINPPSubsDataToIVRFirstCall", "Entered p_map = " + p_map);
        String requestString = null;
        try {
            // requestString=
            // "GET PPIVRFirstCall?TransId="+(String)p_map.get("IN_TXN_ID")+
            // getRequestString(4,p_map);
            requestString = "GET PPIVRFirstCall?TransId=" + getINReconTxnID(p_map) + getRequestString(4, p_map);
            p_map.put("IN_REQUEST_STR", requestString);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("getINPPSubsDataToIVRFirstCall", "Exception e = " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getINPPSubsDataToIVRFirstCall]", "", " INTERFACE ID = " + (String) p_map.get("INTERFACE_ID"), "NETWORK_CODE = " + (String) p_map.get("NETWORK_CODE"), "Exception e = " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PROCESS_REQUEST_ERROR);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getINPPSubsDataToIVRFirstCall", "Exited requestString = " + requestString);
        }// end of finally
        return requestString;
    }// getINPPSubsDataToIVRFirstCall

    /**
     * This method is responsible to generate request string for PPSubsData
     * messages to check the PPExpiryDate.
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    public String getINPPSubsDataToPPExpiryDate(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("getINPPSubsDataToPPExpiryDate", "Entered p_map = " + p_map);
        String requestString = null;
        try {
            // requestString=
            // "GET PPExpiryDates?TransId="+(String)p_map.get("IN_TXN_ID")+
            // getRequestString(4,p_map);
            requestString = "GET PPExpiryDates?TransId=" + getINReconTxnID(p_map) + getRequestString(4, p_map);
            p_map.put("IN_REQUEST_STR", requestString);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("getINPPSubsDataToPPExpiryDate", "Exception e = " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getINPPSubsDataToPPExpiryDate]", "", " INTERFACE ID = " + (String) p_map.get("INTERFACE_ID"), "NETWORK_CODE = " + (String) p_map.get("NETWORK_CODE"), "Exception e = " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PROCESS_REQUEST_ERROR);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getINPPSubsDataToPPExpiryDate", "Exited requestString = " + requestString);
        }
        return requestString;
    }// end of getINPPSubsDataToPPExpiryDate

    /**
     * This method is responsible to gernerate the request string for PPBalance.
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    public String getINPPBalance(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("getINPPBalance", "Entered p_map = " + p_map);
        String requestString = null;
        try {
            // requestString=
            // "GET PPBalance?TransId="+(String)p_map.get("IN_TXN_ID")+
            // getRequestString(1,p_map);
            requestString = "GET PPBalance?TransId=" + getINReconTxnID(p_map) + getRequestString(1, p_map);
            p_map.put("IN_REQUEST_STR", requestString);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("getINPPBalance", "Exception e = " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getINPPBalance]", "", " INTERFACE ID = " + (String) p_map.get("INTERFACE_ID"), "NETWORK_CODE = " + (String) p_map.get("NETWORK_CODE"), "Exception e = " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PROCESS_REQUEST_ERROR);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getINPPBalance", "Exited requestString = " + requestString);
        }
        return requestString;
    }// end of getINPPBalance

    /**
     * This method is responsible to generate the request for PPBalance messages
     * to PPRecharge
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    public String getINPPBalanceToPPRecharge(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("getINPPBalanceToPPRecharge", "Entered p_map = " + p_map);
        String requestString = null;
        try {
            // requestString=
            // "GET PPRecharge?TransId="+(String)p_map.get("IN_TXN_ID")+
            // getRequestString(2,p_map);
            requestString = "GET PPRecharge?TransId=" + getINReconTxnID(p_map) + getRequestString(2, p_map);
            p_map.put("IN_REQUEST_STR", requestString);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("getINPPSubsDataToIVRFirstCall", "Exception e = " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getINPPSubsDataToIVRFirstCall]", "", " INTERFACE ID = " + (String) p_map.get("INTERFACE_ID"), "NETWORK_CODE = " + (String) p_map.get("NETWORK_CODE"), "Exception e = " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PROCESS_REQUEST_ERROR);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getINPPBalanceToPPRecharge", "Exited requestString = " + requestString);
        }
        return requestString;
    }// end of getINPPBalanceToPPRecharge

    /**
     * This method is responsible to generate the request string for PPBalance
     * messages to PPAccount
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    public String getINPPBalanceToPPAccount(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("getINPPBalanceToPPAccount", "Entered p_map = " + p_map);
        String requestString = null;
        try {
            // requestString=
            // "GET PPPayment?TransId="+(String)p_map.get("IN_TXN_ID")+
            // getRequestString(3,p_map);
            requestString = "GET PPPayment?TransId=" + getINReconTxnID(p_map) + getRequestString(3, p_map);
            p_map.put("IN_REQUEST_STR", requestString);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("getINPPBalanceToPPAccount", "Exception e = " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getINPPBalanceToPPAccount]", "", " INTERFACE ID = " + (String) p_map.get("INTERFACE_ID"), "NETWORK_CODE = " + (String) p_map.get("NETWORK_CODE"), "Exception e = " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PROCESS_REQUEST_ERROR);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getINPPBalanceToPPAccount", "Exited requestString = " + requestString);
        }
        return requestString;
    }// end of getINPPBalanceToPPAccount

    /**
     * This method is responsible to generate the request string for PPAdjust
     * for creditAdjust.
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    public String getINPPAdjustCredit(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("getINPPAdjustCredit", "Entered p_map = " + p_map);
        String requestString = null;
        try {
            // requestString=
            // "GET PPAdjust?TransId="+(String)p_map.get("IN_TXN_ID")+
            // getRequestString(5,p_map);
            requestString = "GET PPAdjust?TransId=" + getINReconTxnID(p_map) + getRequestString(5, p_map);
            p_map.put("IN_REQUEST_STR", requestString);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("getINPPAdjustCredit", "Exception e = " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getINPPAdjustCredit]", "", " INTERFACE ID = " + (String) p_map.get("INTERFACE_ID"), "NETWORK_CODE = " + (String) p_map.get("NETWORK_CODE"), "Exception e = " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PROCESS_REQUEST_ERROR);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("", "Exited requestString = " + requestString);
        }
        return requestString;
    }

    /**
     * This method is responsible to generate a request string for PPAdjust for
     * debit.
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    public String getINPPAdjustDebit(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("getINPPAdjustDebit", "Entered p_map = " + p_map);
        String requestString = null;
        try {
            // requestString=
            // "GET PPAdjust?TransId="+(String)p_map.get("IN_TXN_ID")+
            // getRequestString(6,p_map);
            requestString = "GET PPAdjust?TransId=" + getINReconTxnID(p_map) + getRequestString(6, p_map);
            p_map.put("IN_REQUEST_STR", requestString);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("getINPPAdjustDebit", "Exception e = " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getINPPAdjustDebit]", "", " INTERFACE ID = " + (String) p_map.get("INTERFACE_ID"), "NETWORK_CODE = " + (String) p_map.get("NETWORK_CODE"), "Exception e = " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PROCESS_REQUEST_ERROR);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getINPPAdjustDebit", "Exited requestString = " + requestString);
        }
        return requestString;
    }

    /**
     * This method is responsible to generate common request string based on the
     * type.
     * 
     * @param int type
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    private String getRequestString(int type, HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("getRequestString", "Entered type = " + type + " p_map = " + p_map);
        StringBuffer requestStringBuff = null;
        // int allowedLengthInt = 0;
        String interfaceID = null;
        String networkCode = null;
        String msisdn = null;
        // String allowedLength = null;
        try {
            networkCode = (String) p_map.get("NETWORK_CODE");
            interfaceID = (String) p_map.get("INTERFACE_ID");
            /*
             * allowedLength = FileCache.getValue(interfaceID,"MSISDNLength");
             * if(allowedLength==null)
             * {
             * EventHandler.handle(EventIDI.SYSTEM_ERROR,
             * EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
             * "EricssionRequestFormatter[getRequestString]"
             * ,""," INTERFACE ID = "+interfaceID,
             * "NETWORK_CODE = "+networkCode,
             * "MSISDNLength is not defined in INFIle");
             * throw new
             * BTSLBaseException(this,"getRequestString",InterfaceErrorCodesI
             * .INTERFACE_HANDLER_EXCEPTION);
             * }
             */
            try {
                // Adding or Removing the MSISDN Prefix based on the INFile
                // configuration
                msisdn = InterfaceUtil.getFilterMSISDN((String) p_map.get("INTERFACE_ID"), (String) p_map.get("MSISDN"));
            } catch (Exception e) {
                e.printStackTrace();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getRequestString]", "", " INTERFACE ID = " + interfaceID, "NETWORK_CODE = " + networkCode, "Exception e = " + e.getMessage());
                throw new BTSLBaseException(this, "getRequestString", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            requestStringBuff = new StringBuffer("&TransDateTime=" + InterfaceUtil.getEricssionCurrentDateTime() + "&MSISDN=" + msisdn);
            if (_log.isDebugEnabled())
                _log.debug("getRequestString", "networkCode " + networkCode + " interfaceID = " + interfaceID + " msisdn = " + msisdn);
            /* adding transcurrency for ppbalance */
            if (type == 1 || type == 5 || type == 6) {
                String transCurrency = FileCache.getValue(interfaceID, "TransCurrency");
                if (transCurrency == null) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getRequestString]", "", " INTERFACE ID = " + interfaceID, "NETWORK_CODE = " + networkCode, "TransCurrency is not defined in INFIle");
                    throw new BTSLBaseException(this, "getRequestString", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                requestStringBuff.append("&TransCurrency=" + transCurrency);
            }

            /* adding TranProcCode,TransCurrency,CardGroup */
            if (type == 3) {

                String transProcCodeCredit = FileCache.getValue(interfaceID, "TransProcCodeCredit");
                if (transProcCodeCredit == null) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getRequestString]", "", " INTERFACE ID = " + interfaceID, "NETWORK_CODE = " + networkCode, "TransProcCodeCredit is not defined in INFIle");
                    throw new BTSLBaseException(this, "getRequestString", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                requestStringBuff.append("&TransProcCode=" + transProcCodeCredit);
                // Get the value from String Parser Object.
                // CONFIRM whether it is CARD_GROUP or
                // CARD_GROUP_SELCTOR?????????
                String cardGroupStr = (String) p_map.get("CARD_GROUP");
                String amountStr = (String) p_map.get("transfer_amount");

                String message = (cardGroupStr == null) ? "CARD_GROUP" : (amountStr == null) ? "INTERFACE_AMOUNT" : "N";
                if (!"N".equals(message)) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getRequestString]", "", " INTERFACE ID = " + interfaceID, "NETWORK_CODE = " + networkCode, message + " contains NULL value in request map");
                    throw new BTSLBaseException(this, "getRequestString", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                // The length of amount(12) is hard coded ,should we make this
                // as variable or put into INFile
                int pad = 12 - amountStr.length();
                for (int i = 0; i < pad; i++)
                    amountStr = "0" + amountStr;
                requestStringBuff.append("&TransAmt=" + amountStr + "&TransCurrency=" + FileCache.getValue(interfaceID, "TransCurrency") + "&CardGroup=" + cardGroupStr);
                String cardType = FileCache.getValue(interfaceID, "CARD_TYPE");
                if (!InterfaceUtil.isNullString(cardType)) {
                    requestStringBuff.append("&CardType=");
                    requestStringBuff.append(cardType);
                }
            }
            // For PPAdjust Credit
            if (type == 5) {
                // Add TransProcCode
                requestStringBuff.append("&TransProcCode=" + FileCache.getValue(interfaceID, "TransProcCodeCredit"));
                // Add Amount to Debit / Credit
                String amountStr = (String) p_map.get("transfer_amount");

                // As per discussion,We should also pad the amount string in
                // case of creditAdjust
                int pad = 12 - amountStr.length();
                for (int i = 0; i < pad; i++)
                    amountStr = "0" + amountStr;
                requestStringBuff.append("&TransAmt=" + amountStr);
                // Add AdjType
                // First check whether AdjType is present then AdjCode must be
                // supplied
                String adjType = FileCache.getValue(interfaceID, "AdjType");
                String adjCode = FileCache.getValue(interfaceID, "AdjCode_C");
                String adjDes = FileCache.getValue(interfaceID, "ADJ_DESC");
                if (adjDes == null && (adjType == null && adjCode == null))
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PROCESS_REQUEST_ERROR);
                // As per Specification ::adjType is optional but if it is
                // present adjCode should also be present
                // And adjCode is optional but if it is present adjType should
                // also be present.
                if (adjType != null && adjCode != null) {
                    requestStringBuff.append("&AdjType=" + adjType);
                    // Add AdjCode //Why we are adding network code
                    // requestStringBuff.append("&AdjCode=" + adjCode+
                    // FileCache.getValue(interfaceID,networkCode));
                    requestStringBuff.append("&AdjCode=" + adjCode);
                    if (adjDes != null && BTSLUtil.isNullString(adjDes))
                        requestStringBuff.append("&AdjDescription=" + adjDes);
                }
                // adjDes is optional but if not provided this should conatain
                // the combination of adjType and adjCode.
                if ((adjDes == null || BTSLUtil.isNullString(adjDes)) && ((adjType == null || BTSLUtil.isNullString(adjType)) && (adjCode == null) || BTSLUtil.isNullString(adjCode))) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getRequestString]", "", " INTERFACE ID = " + interfaceID, "NETWORK_CODE = " + networkCode, "In case of ADJ_DES is not provided in INFile AdjType and AdjCode must be present");
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PROCESS_REQUEST_ERROR);
                }
            }
            if (type == 6) {
                // Add TransProcCode Debit
                requestStringBuff.append("&TransProcCode=" + FileCache.getValue(interfaceID, "TransProcCodeDebit"));
                // Add Amount to Debit / Credit
                String amountStr = (String) p_map.get("transfer_amount");
                String adjType = FileCache.getValue(interfaceID, "AdjType");
                String adjCode = FileCache.getValue(interfaceID, "AdjCode_D");
                String adjDes = FileCache.getValue(interfaceID, "ADJ_DESC");
                // As per discussion,we also pad the trans amount like in
                // credit.
                int pad = 12 - amountStr.length();
                for (int i = 0; i < pad; i++)
                    amountStr = "0" + amountStr;
                // Add AdjType
                requestStringBuff.append("&TransAmt=" + amountStr);
                // As per Specification ::1.adjType is optional but if it is
                // present adjCode should also be present
                // 2.And adjCode is optional but if it is present adjType should
                // also be present.
                if ((adjType != null || BTSLUtil.isNullString(adjType)) && (adjCode != null) || BTSLUtil.isNullString(adjCode)) {
                    requestStringBuff.append("&AdjType=" + adjType);
                    // Add AdjCode //Why we are adding network code
                    // requestStringBuff.append("&AdjCode=" + adjCode+
                    // FileCache.getValue(interfaceID,networkCode));
                    requestStringBuff.append("&AdjCode=" + adjCode);
                    if (adjDes != null && BTSLUtil.isNullString(adjDes))
                        requestStringBuff.append("&AdjDescription=" + adjDes);
                }
                // adjDes is optional but if not provided this should conatain
                // the combination of adjType and adjCode.
                if ((adjDes == null || BTSLUtil.isNullString(adjDes)) && ((adjType == null || BTSLUtil.isNullString(adjType)) && (adjCode == null) || BTSLUtil.isNullString(adjCode))) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getRequestString]", "", " INTERFACE ID = " + interfaceID, "NETWORK_CODE = " + networkCode, "In case of ADJ_DES is not provided in INFile AdjType and AdjCode must be present");
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PROCESS_REQUEST_ERROR);
                }
            }
            /* adding PIN for PPRecharge */
            /*
             * //This is not used
             * if(type == 2)
             * {
             * requestStringBuff.append("&PIN=" + (String)p_map.get("PIN"));
             * }
             *//* adding dest and origin */
            if (type <= 6) {
                requestStringBuff.append("&Dest=" + FileCache.getValue(interfaceID, "DEST") + "&Origin=" + FileCache.getValue(interfaceID, "ORIGIN"));// (String)p_map.get("TASOrigInstCode"));//Origin
                                                                                                                                                      // to
                                                                                                                                                      // be
                                                                                                                                                      // discussed
                                                                                                                                                      // with
                                                                                                                                                      // supratim
            }
            /* adding Accepatance Location for pppayment CONFIRM What is SPEID */
            if (type == 3) {
                /*
                 * if(BTSLUtil.isNullString((String)p_map.get("SPEID"))||BTSLUtil
                 * .isNullString((String)p_map.get("TRANSACTION_ID")))
                 * requestStringBuff.append("&AcceptLoc=" +
                 * "SMS\\BAL\\Pun\\0000040000PunInd");
                 * else
                 * requestStringBuff.append("&AcceptLoc=" +
                 * FileCache.getValue(interfaceID,"AcceptLoc")+
                 * (String)p_map.get("SPEID") +
                 * (String)p_map.get("TRANSACTION_ID"));//SPEID to be discussed
                 * with supratim
                 */// Changed on 09/03/2007
                   // ------------------------------------------------------------------------------
                String acceptLocYN = FileCache.getValue(interfaceID, "ACCEPT_LOC_IS_HARDCODED");
                if (InterfaceUtil.isNullString(acceptLocYN)) {
                    _log.error("getRequestString", "Value of ACCEPT_LOC_IS_HARDCODED is not defined in the INFile");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getRequestString]", "", "INTERFACE ID" + interfaceID + " MSISDN " + (String) p_map.get("SENDER_MSISDN"), (String) p_map.get("NETWORK_CODE"), "ACCEPT_LOC_IS_HARDCODED is not defined in the INFile.");
                    throw new BTSLBaseException(this, "getRequestString", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }

                if ("N".equals(acceptLocYN)) {
                    String acceptLocVarKeys = FileCache.getValue(interfaceID, "ACCEPT_LOC_VAR_KEYS");
                    if (InterfaceUtil.isNullString(acceptLocVarKeys)) {
                        _log.error("getRequestString", "Value of ACCEPT_LOC_VAR_KEYS is not defined in the INFile");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getRequestString]", "", "INTERFACE ID" + interfaceID + " MSISDN " + (String) p_map.get("SENDER_MSISDN"), (String) p_map.get("NETWORK_CODE"), "ACCEPT_LOC_VAR_KEYS is not defined in the INFile.");
                        throw new BTSLBaseException(this, "getRequestString", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                    }
                    String middleSeparator = FileCache.getValue(interfaceID, "ACCEPT_LOC_MIDDLE_SEPRATOR");
                    if (InterfaceUtil.isNullString(middleSeparator)) {
                        _log.error("getRequestString", "Value of ACCEPT_LOC_MIDDLE_SEPRATOR is not defined in the INFile");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getRequestString]", "", "INTERFACE ID" + interfaceID + " MSISDN " + (String) p_map.get("SENDER_MSISDN"), (String) p_map.get("NETWORK_CODE"), "ACCEPT_LOC_MIDDLE_SEPRATOR is not defined in the INFile.");
                        throw new BTSLBaseException(this, "getRequestString", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                    }

                    String lastSeparator = FileCache.getValue(interfaceID, "ACCEPT_LOC_LAST_SEPRATOR");
                    if (InterfaceUtil.isNullString(lastSeparator)) {
                        _log.error("getRequestString", "Value of ACCEPT_LOC_LAST_SEPRATOR is not defined in the INFile");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getRequestString]", "", "INTERFACE ID" + interfaceID + " MSISDN " + (String) p_map.get("SENDER_MSISDN"), (String) p_map.get("NETWORK_CODE"), "ACCEPT_LOC_LAST_SEPRATOR is not defined in the INFile.");
                        throw new BTSLBaseException(this, "getRequestString", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                    }

                    String[] mapKeys = acceptLocVarKeys.split(",");
                    requestStringBuff.append("&AcceptLoc=");
                    StringBuffer tempBuffer = new StringBuffer();
                    for (int i = 0, j = mapKeys.length; i < j; i++) {
                        tempBuffer.append((String) p_map.get(mapKeys[i]));
                        if (i == (j - 1)) {
                            tempBuffer.append(lastSeparator);
                            break;
                        }
                        tempBuffer.append(middleSeparator);
                    }
                    requestStringBuff.append(tempBuffer);
                } else {
                    String acceptLocHardCodeValue = FileCache.getValue(interfaceID, "ACCEPT_LOC_HARD_CODE_VALUE");
                    if (InterfaceUtil.isNullString(acceptLocHardCodeValue)) {
                        _log.error("getRequestString", "Value of ACCEPT_LOC_HARD_CODE_VALUE is not defined in the INFile");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getRequestString]", "", "INTERFACE ID" + interfaceID + " MSISDN " + (String) p_map.get("SENDER_MSISDN"), (String) p_map.get("NETWORK_CODE"), "ACCEPT_LOC_HARD_CODE_VALUE is not defined in the INFile.");
                        throw new BTSLBaseException(this, "getRequestString", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                    }

                    requestStringBuff.append("&AcceptLoc=" + acceptLocHardCodeValue);
                }

                // requestStringBuff.append("&AcceptLoc="+(String)p_map.get("TRANSACTION_ID")+"."+(String)p_map.get("SENDER_MSISDN")+"\\\\\\");

                // --------------------------------------------------------------------------------

                // requestStringBuff.append("&AcceptLoc="+(String)p_map.get("TRANSACTION_ID")+"."+(String)p_map.get("SENDER_MSISDN")+"\\\\\\");
                // requestStringBuff.append("&AcceptLoc=" +
                // "SMS\\BAL\\Pun\\0000040000PunInd");
            }
            /* adding Opid for PPRecharge this is an optional parameter */
            if (type >= 2 && type <= 6) {
                requestStringBuff.append("&Opid=" + FileCache.getValue(interfaceID, "OP_ID"));
            }
        }// end of try-block
        catch (BTSLBaseException be) {
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            e.printStackTrace();
            _log.error("getRequestString", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getRequestString]", "", " INTERFACE ID = " + interfaceID, "NETWORK_CODE = " + networkCode, "Exception e = " + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_PROCESS_REQUEST_ERROR);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getRequestString", "Exiting requestStringBuff.toString() = " + requestStringBuff.toString());
        }// end of finally
        return requestStringBuff.toString();
    }// end of getRequestString

    /**
     * This method is responsible to format a date string into date.
     * 
     * @param StringdateStr
     * @param Strinp_format
     * @return String
     * @throws BTSLBaseException
     */
    public String getInterfaceDateFromDateString(String dateStr, String p_format) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getInterfaceDateFromDateString ", "Entered date = " + dateStr + " p_format = " + p_format);
        String format = p_format;
        SimpleDateFormat sdf = null;
        Date d1 = null;
        try {
            sdf = new SimpleDateFormat(format);
            sdf.setLenient(false); // this is required else it will convert
            d1 = sdf.parse(dateStr);
            sdf = new SimpleDateFormat("ddMMyyyy");
            sdf.setLenient(false); // this is required else it will convert
            return sdf.format(d1);
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getEricssionCurrentDateTime]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getEricssionCurrentDateTime", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getInterfaceDateFromDateString", "Exiting sdf.format(d1) = " + sdf.format(d1));
        }
    }

    /**
     * This method is used to get the date and time specified by the ericsion IN
     * 
     * @return String
     * @throws BTSLBaseException
     */
    public String getEricssionCurrentDateTime() throws BTSLBaseException {
        // String s;
        if (_log.isDebugEnabled())
            _log.debug("getEricssionCurrentDateTime", "Entered");
        Calendar calender = null;
        String s = "";
        try {
            calender = Calendar.getInstance();
            int t = 0;
            t = calender.get(Calendar.YEAR);
            s = s + t;
            t = calender.get(Calendar.MONTH);
            t++;
            if (t / 10 == 0)
                s += "0" + t;
            else
                s += t;

            t = calender.get(Calendar.DATE);
            if (t / 10 == 0)
                s += "0" + t;
            else
                s += t;
            s += "T";
            t = calender.get(Calendar.HOUR_OF_DAY);
            if (t / 10 == 0)
                s += "0" + t;
            else
                s += t;
            t = calender.get(Calendar.MINUTE);
            if (t / 10 == 0)
                s += "0" + t;
            else
                s += t;
            t = calender.get(Calendar.SECOND);
            if (t / 10 == 0) {
                s += "0" + t;
            } else {
                s += t;
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "EricssionRequestFormatter[getEricssionCurrentDateTime]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getEricssionCurrentDateTime", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getEricssionCurrentDateTime", "Exited String s = " + s);
        }
        return s;
    }

    public static void main(String args[]) throws Exception {
        /*
         * String outHash = "";
         * String spStr =
         * "MSISDN=919845156374&TASDateTime=20020615010100&Type=SMS&Index=4&TransId=000001&PAMI_Id=000002&TASOrigInstCode=000003&PIN=34dsfsdf3dfsdf%@#$sad&TransAmt=12000000"
         * ;
         */
        /*
         * spObj.put("MSISDN","919845156374");
         * spObj.put("TASDateTime","20020615010100");
         * spObj.put("Type","SMS");
         * spObj.put("Index","4");
         * spObj.put("TransId","000001");
         * spObj.put("PAMI_Id","000002");
         * spObj.put("TASOrigInstCode","000003");
         * //----- Extra PArameter for PPRecharge -----
         * spObj.put("PIN","34dsfsdf3dfsdf%@#$sad");
         * //-----Extra Parameter for PPAcount -----
         * spObj.put("TransProcCode","12312");
         * spObj.put("TransAmt","12000000");
         * spObj.put("CardGroup","A1");
         * spObj.put("AcceptLoc","Sema\\High+Holborn\\London\\000WC1V7DJLONGBR");
         */
        // outHash = ms.InRequestToPPSubsData(spStr);
        // outHash = ms.PPSubsDataToIVRFirstCall(spStr);
        // outHash = ms.PPSubsDataToPPExpiryDate(spStr);
        // outHash = ms.PPBalance(spStr);
        // outHash = ms.PPBalanceToPPRecharge(spStr);
        // outHash = ms.PPBalanceToPPAccount(spStr);
        // System.out.println("String------>: "+outHash);
        /*
         * Constants.load("C:\\Constants.props");
         * org.apache.log4j.PropertyConfigurator.configure("C:\\LogConfig.props")
         * ;
         * HashMap h = new HashMap();
         * getINReconTxnID(h);
         */
    }
}
