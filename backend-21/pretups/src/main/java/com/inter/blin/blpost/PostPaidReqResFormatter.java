package com.inter.blin.blpost;

/**
 * @PostPaidReqResFormatter.java
 *                 Copyright(c) 2007, Mahindra Comviva.
 *                 All Rights Reserved
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Author Date History
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Sanjeew K 30 Aug, 2016 Initial Creation
 *                 ------------------------------------------------------------
 *                 ------------------------------------
 *                 This Class Have validate and credit method to validate the subscriber 
 *                 in DB and make the billing entry in DB respectively
 *                 
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;
import com.inter.blin.blpost.PostPaidI;

public class PostPaidReqResFormatter{

    public PostPaidReqResFormatter(){
        super();
    }
    private static Log _log = LogFactory.getLog(PostPaidReqResFormatter.class.getName());

    public void validate(Connection p_con, HashMap<String, String> p_requestMap) throws BTSLBaseException {
        String METHODE_NAME="PostPaidReqResFormatter[validate()]";
        String custCode=null;
        if (_log.isDebugEnabled())
            _log.debug(METHODE_NAME, "Entered p_requestMap:" + p_requestMap);
        PreparedStatement pstmtSelect = null;
        String status=PostPaidI.SUBSCRIBER_NOT_FOUND;
        ResultSet resultSet = null;
        StringBuffer responseBuffer = null;
        String responseStr = null;
        String network_id=null;
        String entryDateStr=null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer("SELECT cust_code,entry_date,network_id FROM postpaid_cust_master WHERE msisdn=? ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug(METHODE_NAME, "selectQuery= " + selectQuery);
            pstmtSelect = p_con.prepareCall(selectQuery);
            pstmtSelect.setString(1, p_requestMap.get("MSISDN"));
            resultSet = pstmtSelect.executeQuery();
            if (resultSet.next())
            {
                custCode=resultSet.getString("cust_code");
                network_id=resultSet.getString("network_id");
                entryDateStr=resultSet.getString("entry_date");
                status=PostPaidI.SP_SUCCESS_OK;
                p_requestMap.put("CUST_CODE", custCode);
                p_requestMap.put("IN_RECON_ID", custCode);
            } else
                status=PostPaidI.NOT_POSTPAID_NO;
            responseBuffer = new StringBuffer(1028);
            responseBuffer.append("Status=");
            responseBuffer.append(status);
            responseBuffer.append("&Custcode=");
            responseBuffer.append(custCode);
            responseBuffer.append("&TransactionId=");
            responseBuffer.append(p_requestMap.get("TRANSACTION_ID"));
            responseBuffer.append("&Msisdn=");
            responseBuffer.append(p_requestMap.get("MSISDN"));
            responseBuffer.append("&EntryDate=");
            responseBuffer.append(entryDateStr);
            responseBuffer.append("&Networkid=");
            responseBuffer.append(network_id);

            responseStr = responseBuffer.toString();
            p_requestMap.put("RESPONSE_STR", responseStr);
        } catch (SQLException sqlEx) {
            _log.errorTrace(METHODE_NAME, sqlEx);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, METHODE_NAME, "REFERENCE ID = " + p_requestMap.get("IN_TXN_ID") + "MSISDN = " +p_requestMap.get("MSISDN"), "INTERFACE ID = " + p_requestMap.get("INTERFACE_ID"), "Network code = " +p_requestMap.get("NETWORK_CODE") + " Action = " + PostPaidI.ACTION_ACCOUNT_INFO, "While validating the subscriber get SQLException sqlEx:" + sqlEx.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        } catch (Exception e) {
            _log.errorTrace(METHODE_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, METHODE_NAME, "REFERENCE ID = " +p_requestMap.get("IN_TXN_ID") + "MSISDN = " +p_requestMap.get("MSISDN"), "INTERFACE ID = " +p_requestMap.get("INTERFACE_ID"), "Network code = " +p_requestMap.get("NETWORK_CODE") + " Action = " + PostPaidI.ACTION_ACCOUNT_INFO, "While validating the subscriber get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        } finally {
            try {if (pstmtSelect != null)pstmtSelect.clearParameters();} catch (Exception e){}
            try {if (resultSet != null)resultSet.close();} catch (Exception e) {}
            if (_log.isDebugEnabled())
                _log.debug(METHODE_NAME, "Exited custCode:" +custCode+", interfaceStatus:"+status);
        }
    }
    /**
     * This method credit the balance of user.
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     *             ,Exception
     */
    public void credit(Connection p_con,HashMap<String,String> p_requestMap) throws BTSLBaseException, Exception {
        String METHODE_NAME="PostPaidReqResFormatter[credit()]";
        if (_log.isDebugEnabled())
            _log.debug(METHODE_NAME, "Entered " + InterfaceUtil.getPrintMap(p_requestMap));
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer responseBuffer = null;
        String responseStr = null;
        try {
            /*
                CIRCLE_ID          1
                MSISDN              with country code
                CUST_CODE
                AMOUNT
                TRANS_NUMB
                STATUS              9,0
                ENTRY_DATE
                PROCESS_ID
                PROCESS_DATE
                CHANNEL             00100701007,Channel User number with country code
                INFO1               SmscBg492
                INFO2               127.0.0.1
                INFO3               13015
                INFO4               9969
                PROCESS_STATUS          0
                POSTED_DATE         
                DESCRIPTION         NORMAL
             */
            String inTXNID = InterfaceUtil.getINTransactionID();
            String referenceID = p_requestMap.get("TRANSACTION_ID");
            String interfaceID = p_requestMap.get("INTERFACE_ID");
            int circleID= Integer.parseInt(FileCache.getValue(interfaceID, "CIRCLE_ID"));
            String custCode = p_requestMap.get("ACCOUNT_ID");
            double taxAmount = Double.parseDouble(p_requestMap.get("transfer_amount"));
            int status=Integer.parseInt(FileCache.getValue(interfaceID, "STATUS"));
            final Date entryDate = new Date();
            String chnlMSISDN=p_requestMap.get("FILT_SEND_MSISDN");
            String channel=FileCache.getValue(interfaceID, "CHANNEL_ID")+","+chnlMSISDN;
            String info1=FileCache.getValue(interfaceID, "INFO1");
            String info2=FileCache.getValue(interfaceID, "INFO2");
            int info3=Integer.parseInt(FileCache.getValue(interfaceID, "INFO3"));
            String info4=FileCache.getValue(interfaceID, "INFO4");
            int processStatus=Integer.parseInt(FileCache.getValue(interfaceID, "PROCESS_STATUS"));
            String description=FileCache.getValue(interfaceID, "DESCRIPTION");                
            String msisdn = p_requestMap.get("FILT_REC_MSISDN");
            StringBuffer insertQueryBuff = new StringBuffer("INSERT INTO postpaid_cust_pay_master");
            insertQueryBuff.append("(circle_id,msisdn,cust_code,amount,trans_numb,status,entry_date,channel,info1,info2,info3,info4,process_status,description) ");
            insertQueryBuff.append(" VALUES ");
            insertQueryBuff.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            final String insertQuery = insertQueryBuff.toString();
            if (_log.isDebugEnabled())
            {
                _log.debug(METHODE_NAME, "selectQuery= " + insertQuery);
                _log.debug(METHODE_NAME, "Parameter Passed are circleID= " + circleID+", msisdn="+msisdn+", custCode="+custCode+", taxAmount="+taxAmount+
                        ", inTXNID="+inTXNID+", status="+status+", entryDate="+entryDate+", channel="+channel+", info1="+info1+", info2="+info2+
                        ", info3="+info3+", info4="+info4+", processStatus="+processStatus+", description="+description);
            }
            pstmt = p_con.prepareStatement(insertQuery);
            int i = 0;
            pstmt.clearParameters();
            pstmt.setInt(++i, circleID);
            pstmt.setString(++i, msisdn);
            pstmt.setString(++i, custCode);
            pstmt.setDouble(++i, taxAmount);

            pstmt.setString(++i, inTXNID);
            pstmt.setInt(++i, status);
            pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(entryDate));
            pstmt.setString(++i, channel);
            pstmt.setString(++i, info1);
            pstmt.setString(++i, info2);
            pstmt.setInt(++i, info3);
            pstmt.setString(++i, info4);
            pstmt.setInt(++i, processStatus);
            pstmt.setString(++i, description);
            int updateCount = pstmt.executeUpdate();
            if (_log.isDebugEnabled()) {
                _log.debug(METHODE_NAME, "Before result Sender MSISDN:" + chnlMSISDN+", Receiver MSISDN:"+msisdn+", TRANSACTION_ID:"+referenceID);
            }
            p_requestMap.put("IN_TXN_ID", inTXNID);
            p_requestMap.put("Stage", "Credit");
            p_requestMap.put("ENTRY_TYPE", PretupsI.CREDIT);
            //sendRequestToDB(_requestMap, "Credit");
            if (updateCount <= 0) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                        METHODE_NAME, "", "", "", "BTSLBaseException: update count <=0");
                _log.error(METHODE_NAME, "Parameter Passed are circleID= " + circleID+", msisdn="+msisdn+", custCode="+custCode+", taxAmount="+taxAmount+
                        ", inTXNID="+inTXNID+", status="+status+", entryDate="+entryDate+", channel="+channel+", info1="+info1+", info2="+info2+
                        ", info3="+info3+", info4="+info4+", processStatus="+processStatus+", description="+description);
                throw new BTSLBaseException(this, METHODE_NAME, "error.general.sql.processing");
            }
            else{
                p_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
                responseBuffer = new StringBuffer(1028);
                responseBuffer.append("Status=");
                responseBuffer.append(PostPaidI.SP_SUCCESS_OK);
                responseBuffer.append("&Custcode=");
                responseBuffer.append(custCode);
                responseBuffer.append("&TransactionId=");
                responseBuffer.append(p_requestMap.get("TRANSACTION_ID"));
                responseBuffer.append("&Msisdn=");
                responseBuffer.append(p_requestMap.get("MSISDN"));
                responseBuffer.append("&Networkid=");
                responseBuffer.append(circleID);
                responseStr = responseBuffer.toString();
                p_requestMap.put("RESPONSE_STR", responseStr);
            }
        } 
        catch (SQLException sqlEx) {
            _log.errorTrace(METHODE_NAME, sqlEx);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, METHODE_NAME, "REFERENCE ID = " + p_requestMap.get("IN_TXN_ID") + "MSISDN = " +p_requestMap.get("MSISDN"), "INTERFACE ID = " + p_requestMap.get("INTERFACE_ID"), "Network code = " +p_requestMap.get("NETWORK_CODE") + " Action = " + PostPaidI.ACTION_RECHARGE_CREDIT, "While Billing the subscriber get SQLException sqlEx:" + sqlEx.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        }
        catch (Exception e) {
            _log.errorTrace(METHODE_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "QueueTableHandler[credit]",p_requestMap.get("TRANSACTION_ID"), p_requestMap.get("MSISDN"), p_requestMap.get("NETWORK_CODE"), "Exception while credit");
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
        } finally {
            if (pstmt != null){pstmt.close();}
            if (rs != null){rs.close();}
            if (_log.isDebugEnabled())
                _log.debug(METHODE_NAME, "Exited _requestMap=" + p_requestMap);
        }
    }
}