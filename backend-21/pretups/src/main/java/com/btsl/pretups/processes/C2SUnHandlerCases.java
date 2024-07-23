package com.btsl.pretups.processes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.OracleUtil;

/**
 * @(#)C2SUnHandlerCases .java
 *                       Copyright(c) 2011 Comviva Ltd. Ltd.
 *                       All Rights Reserved
 * 
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Jasmine JUN 15, 2011 Initial Creation
 *                       This process is inserting C2S_TRANSFER_ITEMS data for
 *                       ambiguous transactions in C2S transfer table.
 */

public class C2SUnHandlerCases {

    private static Log _log = LogFactory.getLog(C2SUnHandlerCases.class.getName());
    private static Date _fromDate = null;
    private static Date _toDate = null;
    private static String _interfacetype = "CS3";
    private static String _interfaceID = "INTID00002";
    private static String _serviceClassCode = "ALL";
    private static String _serviceClassID = "SERID00001";

    public static void main(String args[]) {
        final String METHOD_NAME = "main";
        ArrayList c2stranferdetails = null;
        Connection con = null;

        // File file=new File(Constants.getProperty("C2SFilePath"));
        final File file = new File("C://transcation.txt");

        if (args.length != 4) {
            System.out.println("Usage : C2SUnHandlerCases [from date] [to date ] ");
            return;
        }
        final File constantsFile = new File(args[0]);
        if (!constantsFile.exists()) {
            System.out.println("C2SUnHandlerCases main() Constants file not found on location:: " + constantsFile.toString());
            return;
        }
        final File logconfigFile = new File(args[1]);
        if (!logconfigFile.exists()) {
            System.out.println("C2SUnHandlerCases main() Logconfig file not found on location:: " + logconfigFile.toString());
            return;
        }
        try {

            if (args.length == 4 && !BTSLUtil.isNullString(args[2]) && !BTSLUtil.isNullString(args[3])) {
                _fromDate = (BTSLUtil.getDateFromDateString(args[2]));
                _toDate = (BTSLUtil.getDateFromDateString(args[3]));
            } else {
                _fromDate = BTSLUtil.addDaysInUtilDate(new Date(), -1);
                _fromDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(_fromDate));
                _toDate = BTSLUtil.addDaysInUtilDate(new Date(), -1);
                _toDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(_toDate));

            }
        } catch (Exception pe) {
            _log.error("main", "Error occurred during processing records .Database not updated. BTSLBaseException : " + pe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SUnHandlerCases[main]", "", "", "",
                "SQL Exception:" + pe.getMessage());
            _log.errorTrace(METHOD_NAME, pe);
            // throw new
            // BTSLBaseException("C2SUnHandlerCases "," main ","Date is not in proper format");//change
            // error code
        }

        try {

            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            NetworkPrefixCache.loadNetworkPrefixesAtStartup();
            final C2SUnHandlerCases c2sunhandlercases = new C2SUnHandlerCases();
            con = OracleUtil.getSingleConnection();
            c2stranferdetails = c2sunhandlercases.loadC2STransferVO(con, _fromDate, _toDate);
           
            
            try(final FileWriter fstream = new FileWriter(file);PrintWriter writer1 = new PrintWriter(new BufferedWriter(fstream));) {
                if (c2stranferdetails != null) {
                    C2STransferVO c2sTransferVO = null;
                    int count = 0;

                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    

                    for (int i = 0, j = c2stranferdetails.size(); i < j; i++) {
                        c2sTransferVO = (C2STransferVO) c2stranferdetails.get(i);
                        c2sTransferVO.setTransferItemList(prepareItemVO(c2sTransferVO));
                        // try{

                        count = (new C2STransferDAO()).addC2STransferItemDetails(con, c2sTransferVO.getTransferItemList(), c2sTransferVO.getTransferID());

                        if (count <= 0) {
                            writer1.println(c2sTransferVO.getTransferID() + " : Failed \n");
                        } else {
                            writer1.println(c2sTransferVO.getTransferID() + " : SUCCESS \n");
                        }
                        // }
                        // catch(SQLException qe)
                        // {
                        // writer1.println(c2sTransferVO.getTransferID()+" : Failed "+qe+"\n");
                        // }
                    }
                    con.commit();
                    writer1.close();
                }

            } catch (Exception e) {
                con.rollback();
                
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "[main]", "", "", "", "Exception:" + e.getMessage());
            }

        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "[main]", "", "", "", "Exception:" + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
        } finally {

            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    private static ArrayList prepareItemVO(C2STransferVO p_C2STransferVO) {
        final String METHOD_NAME = "prepareItemVO";
        final ArrayList list = new ArrayList();
        final C2STransferItemVO senderItemVO = new C2STransferItemVO();
        final C2STransferItemVO receiverItemVO = new C2STransferItemVO();
        try {
            // -----Sender

            // senderItemVO
            senderItemVO.setTransferID(p_C2STransferVO.getTransferID());
            senderItemVO.setMsisdn(p_C2STransferVO.getSenderMsisdn());
            senderItemVO.setEntryDate(p_C2STransferVO.getTransferDate());
            senderItemVO.setRequestValue(p_C2STransferVO.getTransferValue());
            senderItemVO.setUserType("SENDER");
            senderItemVO.setTransferType("TXN");
            senderItemVO.setEntryType("DR");
            senderItemVO.setValidationStatus("200");
            senderItemVO.setUpdateStatus("200");
            senderItemVO.setTransferValue(p_C2STransferVO.getSenderTransferValue());
            senderItemVO.setInterfaceType(_interfacetype);
            senderItemVO.setInterfaceID(_interfaceID);
            senderItemVO.setInterfaceResponseCode(" ");
            senderItemVO.setInterfaceReferenceID(" ");
            senderItemVO.setSubscriberType("");
            senderItemVO.setServiceClassCode(_serviceClassCode);
            senderItemVO.setTransferStatus("200");
            senderItemVO.setTransferDate(p_C2STransferVO.getTransferDate());
            senderItemVO.setTransferDateTime(p_C2STransferVO.getTransferDateTime());
            senderItemVO.setEntryDateTime(p_C2STransferVO.getTransferDateTime());
            senderItemVO.setFirstCall("");
            senderItemVO.setSNo(1);
            NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(p_C2STransferVO.getSenderMsisdn()));
            senderItemVO.setPrefixID(prefixVO.getPrefixID());
            senderItemVO.setAccountStatus("ACTIVE");
            senderItemVO.setReferenceID("");
            list.add(senderItemVO);
            // -----Receiver
            // receiverItemVO
            receiverItemVO.setTransferID(p_C2STransferVO.getTransferID());
            receiverItemVO.setMsisdn(p_C2STransferVO.getReceiverMsisdn());
            receiverItemVO.setEntryDate(p_C2STransferVO.getTransferDate());
            receiverItemVO.setRequestValue(p_C2STransferVO.getTransferValue());
            receiverItemVO.setUserType("RECEIVER");
            receiverItemVO.setTransferType("TXN");
            receiverItemVO.setEntryType("CR");
            receiverItemVO.setValidationStatus("200");
            receiverItemVO.setUpdateStatus("200");
            receiverItemVO.setTransferValue(p_C2STransferVO.getSenderTransferValue());
            receiverItemVO.setInterfaceType(_interfacetype);
            receiverItemVO.setInterfaceID(_interfaceID);
            receiverItemVO.setInterfaceResponseCode(" ");
            receiverItemVO.setInterfaceReferenceID(" ");
            receiverItemVO.setSubscriberType("PRE");
            receiverItemVO.setServiceClassCode(_serviceClassCode);
            receiverItemVO.setTransferStatus("250");
            receiverItemVO.setTransferDate(p_C2STransferVO.getTransferDate());
            receiverItemVO.setTransferDateTime(p_C2STransferVO.getTransferDateTime());
            receiverItemVO.setEntryDateTime(p_C2STransferVO.getTransferDateTime());
            receiverItemVO.setFirstCall("");
            receiverItemVO.setSNo(2);
            prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(p_C2STransferVO.getReceiverMsisdn()));
            receiverItemVO.setPrefixID(prefixVO.getPrefixID());// karna hai
            receiverItemVO.setServiceClass(_serviceClassID);
            receiverItemVO.setAccountStatus("ACTIVE");
            receiverItemVO.setReferenceID(p_C2STransferVO.getTransferID() + "R");
            list.add(receiverItemVO);

        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            // TODO: handle exception
        }
        return list;
    }

    /**
     * Method to load the TransferVO based on Date
     * 
     * @param p_con
     * @param p_Date
     * @return
     */
    public ArrayList loadC2STransferVO(Connection p_con, Date p_fromdate, Date p_todate) {
    	//local_index_implemented
        final String METHOD_NAME = "loadC2STransferVO";
        if (_log.isDebugEnabled()) {
            _log.debug("loadC2STransferVO", "Entered p_fromTransferDate=" + p_fromdate, "p_toTransferDate=" + p_todate);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        C2STransferVO c2sTransferVO = null;
        final ArrayList c2sTransferVOList = new ArrayList();
        try {
            final StringBuffer selectQueryBuff = new StringBuffer();
            selectQueryBuff.append("SELECT  CTRF.TRANSFER_ID ,CTRF.TRANSFER_DATE,CTRF.TRANSFER_DATE_TIME");
            selectQueryBuff.append(",CTRF.SENDER_MSISDN ,CTRF.RECEIVER_MSISDN,CTRF.TRANSFER_VALUE,CTRF.SENDER_TRANSFER_VALUE");
            selectQueryBuff.append(",CTRF.RECEIVER_TRANSFER_VALUE ");
            selectQueryBuff.append("FROM c2s_transfers CTRF ");
            selectQueryBuff.append("WHERE Transfer_date>=? AND Transfer_date<=? AND TRANSFER_STATUS in('205','250')");
            // selectQueryBuff.append("AND  not exists(SELECT transfer_id FROM c2s_transfer_items C2ST ");
            // selectQueryBuff.append("WHERE CTRF.transfer_id=C2ST.transfer_id) AND Transfer_date>=?");
            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("loadC2STransferVO", "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_fromdate));
            pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_todate));
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                c2sTransferVO = new C2STransferVO();
                c2sTransferVO.setTransferID(rs.getString("transfer_id"));
                c2sTransferVO.setTransferDate(rs.getDate("TRANSFER_DATE"));
                c2sTransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                c2sTransferVO.setTransferDateStr(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time")));
                c2sTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                c2sTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                c2sTransferVO.setTransferValue(rs.getLong("transfer_value"));
                c2sTransferVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                c2sTransferVO.setSenderTransferValue(rs.getLong("sender_transfer_value"));
                c2sTransferVO.setReceiverTransferValue(rs.getLong("receiver_transfer_value"));
                c2sTransferVOList.add(c2sTransferVO);

            }

        }// end of try
        catch (SQLException sqle) {
            _log.error("loadC2STransferVO", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SUnHandlerCases[loadC2STransferVO]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
        }// end of catch
        catch (Exception e) {
            _log.error("loadC2STransferVO", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SUnHandlerCases[loadC2STransferVO]", "", "", "",
                "Exception:" + e.getMessage());
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadC2STransferVO", "Exiting ");
            }
        }// end of finally

        return c2sTransferVOList;
    }

    // public int insertC2STransferItems(Connection p_con,ArrayList
    // c2stransferVO) throws SQLException
    //
    // {
    //
    // if (_log.isDebugEnabled())
    // _log.debug("insertC2STransferItems", "Entered c2stransferVOList: " +
    // c2stransferVO );
    //
    // PreparedStatement psmt = null;
    // int updateCount = 0;
    // try
    // {
    //
    // StringBuffer strBuff = new
    // StringBuffer(" INSERT INTO c2s_transfer_items ( ");
    // strBuff.append(" TRANSFER_ID,MSISDN,ENTRY_DATE,REQUEST_VALUE,USER_TYPE,TRANSFER_TYPE,ENTRY_TYPE,VALIDATION_STATUS,UPDATE_STATUS,");
    // strBuff.append(" TRANSFER_VALUE,INTERFACE_TYPE,INTERFACE_ID,INTERFACE_RESPONSE_CODE,INTERFACE_REFERENCE_ID,SUBSCRIBER_TYPE, ");
    // strBuff.append(" SERVICE_CLASS_CODE,TRANSFER_STATUS,TRANSFER_DATE,TRANSFER_DATE_TIME,ENTRY_DATE_TIME,FIRST_CALL,SNO, ");
    // strBuff.append(" PREFIX_ID,SERVICE_CLASS_ID,ACCOUNT_STATUS,REFERENCE_ID)");
    // strBuff.append(" VALUES  ");
    // strBuff.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
    // String query = strBuff.toString();
    //
    //
    // if (_log.isDebugEnabled())
    // _log.debug("insertC2STransferItems", "insert query:" + query);
    // C2STransferItemVO itemsVO = null;
    // psmt = p_con.prepareStatement(query);
    //
    // for (int i = 0, k = c2stransferVO.size(); i < k; i++)
    // {
    // itemsVO=(C2STransferItemVO)c2stransferVO.get(i);
    //
    // psmt.clearParameters();
    // int m = 0;
    //
    // psmt.setString(++m,itemsVO.getTransferID());
    // psmt.setString(++m,itemsVO.getMsisdn());
    // psmt.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(itemsVO.getEntryDate()));
    // psmt.setLong(++m,itemsVO.getRequestValue());
    // psmt.setString(++m,itemsVO.getUserType());
    // psmt.setString(++m,itemsVO.getTransferType());
    // psmt.setString(++m,itemsVO.getEntryType());
    // psmt.setString(++m,itemsVO.getValidationStatus());
    // psmt.setString(++m,itemsVO.getUpdateStatus());
    // psmt.setLong(++m,itemsVO.getTransferValue());
    // psmt.setString(++m,itemsVO.getInterfaceType());
    // psmt.setString(++m,itemsVO.getInterfaceID());
    // psmt.setString(++m,itemsVO.getInterfaceResponseCode());
    // psmt.setString(++m,itemsVO.getInterfaceReferenceID());
    // psmt.setString(++m,itemsVO.getSubscriberType());
    // psmt.setString(++m,itemsVO.getServiceClassCode());
    // psmt.setString(++m,itemsVO.getTransferStatus());
    // psmt.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(itemsVO.getTransferDate()));
    // psmt.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(itemsVO.getTransferDateTime()));
    // psmt.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(itemsVO.getEntryDateTime()));
    // psmt.setString(++m,itemsVO.getFirstCall());
    // psmt.setLong(++m,itemsVO.getSNo());
    // psmt.setLong(++m,itemsVO.getPrefixID());
    // psmt.setString(++m,itemsVO.getServiceClass());
    // psmt.setString(++m,itemsVO.getAccountStatus());
    // psmt.setString(++m,itemsVO.getReferenceID());
    // updateCount = psmt.executeUpdate();
    //
    //
    //
    // if (updateCount <= 0)
    // {
    // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"C2SUnHandlerCases[insertC2STransferItems]","","","","BTSLBaseException: update count <=0");
    //
    // }
    // }
    //
    // }
    //
    // catch (SQLException sqle)
    // {
    // _log.error("insertC2STransferItems", "SQLException " +
    // sqle.getMessage());
    // sqle.printStackTrace();
    // EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
    // EventStatusI.RAISED, EventLevelI.FATAL,
    // "C2SUnHandlerCases[insertC2STransferItems]", "", "", "", "SQL Exception:"
    // + sqle.getMessage());
    // throw sqle;
    // }
    //
    // finally
    // {
    // try{if (psmt != null)psmt.close();} catch (Exception e){}
    // if (_log.isDebugEnabled())
    // _log.debug("addTransferItems", "Exiting Success :" + updateCount);
    // }// end of finally
    //
    // return updateCount;
    // }

}
