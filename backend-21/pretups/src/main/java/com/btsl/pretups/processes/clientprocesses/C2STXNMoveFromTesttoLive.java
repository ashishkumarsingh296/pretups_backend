/*
 * @(#)TXNMoveFronTesttoLive.java
 * Copyright(c) 2010, Comviva Technologies Ltd.
 * All Rights Reserved
 * Description :-
 * --------------------------------------------------------------------
 * Author Date History
 * --------------------------------------------------------------------
 * ved.sharma Oct 14, 2010 Initial creation
 * --------------------------------------------------------------------
 */
package com.btsl.pretups.processes.clientprocesses;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class C2STXNMoveFromTesttoLive {
    private static Properties properties = new Properties(); // to keep the
    // value of
    // propertie
    private static final Log _log = LogFactory.getLog(C2STXNMoveFromTesttoLive.class.getName());

    public static void main(String[] args) {
        final String METHOD_NAME = "main";
        try {
            if (args.length != 1)// check the argument length
            {
                _log.info(METHOD_NAME, "C2STXNMoveFromTesttoLive :: Not sufficient arguments, please pass C2STXNMoveFromTesttoLive.props");
                return;
            }
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists())// check file
            // (C2STXNMoveFromTesttoLive.props)
            // exist or not
            {
                _log.debug(METHOD_NAME, "C2STXNMoveFromTesttoLive" + " C2STXNMoveFromTesttoLive.props File Not Found at the path : " + args[0]);
                return;
            }
            load(args[0]);
            new C2STXNMoveFromTesttoLive().process();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            _log.info(METHOD_NAME, "C2STXNMoveFromTesttoLive: main finished");
        }
    }

    private void process() throws IOException, BTSLBaseException {
        final String METHOD_NAME = "process";
        Connection fromCon = null;
        Connection toCon = null;
        PrintWriter out = null;
        String errorFile = null;
        final String errorFilePath = getProperty("ERROR_FILE_PATH");
        try {

            if (BTSLUtil.isNullString(errorFilePath)) {
                throw new BTSLBaseException("process : ERROR_FILE_PATH key not avilable in configuration file C2STXNMoveFromTesttoLive.props ");
            } else {
                final File errFile = new File(errorFilePath);
                if (!errFile.isDirectory()) {
                    if (!errFile.mkdirs()) {
                        throw new BTSLBaseException("process : " + errorFilePath + "  Derectory not create, Please check permission or create manually");
                    }
                } else {
                    _log.debug(METHOD_NAME, "C2STXNMoveFromTesttoLive" + " Directory not exist : " + errorFilePath);
                }
            }
            fromCon = getFromConnection();
            if (fromCon == null) {
                throw new BTSLBaseException("process : Not able to get the From connection");
            }
            toCon = getToConnection();
            if (toCon == null) {
                throw new BTSLBaseException("process : Not able to get the To connection");
            }

            final String txnIDs = getProperty("C2S_TRANSACTION_IDS");
            if (!BTSLUtil.isNullString(txnIDs)) {
                final String txnIDsArr[] = txnIDs.split(",");
                String txnID = null;
                ArrayList<C2STransferItemVO> itemlist = null;
                C2STransferVO c2sTransferVO = null;
                UserBalancesVO userBalancesVO = null;
                final SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_hhmmss");
                final Date currentDate = new Date();
                errorFile = errorFilePath + File.separator + "ERROR_FILE_" + sdf.format(currentDate) + ".txt";
                out = new PrintWriter(new BufferedWriter(new FileWriter(errorFile)));
                for (int i = 0, j = txnIDsArr.length; i < j; i++) {
                    try {
                        if (!BTSLUtil.isNullString(txnIDsArr[i])) {
                            txnID = txnIDsArr[i].trim();
                            c2sTransferVO = loadTXNDetails(fromCon, txnID);
                            if (c2sTransferVO != null) {
                                // check the success transaction, if transaction
                                // not success then it will not move
                                if (!"200".equals(c2sTransferVO.getTransferStatus())) {
                                    out.write("This Transaction ID=" + txnID + " is not success(200) status in from database: table c2s_transfer: its status is " + c2sTransferVO
                                        .getTransferStatus() + " \n");
                                    continue;
                                } else {
                                    itemlist = loadTXNItemList(fromCon, txnID);
                                    if (itemlist != null && itemlist.size() > 0) {
                                        final int addCount = insertTXNDetails(toCon, c2sTransferVO);
                                        if (addCount > 0) {
                                            final int addCountItem = insertTXNItemList(toCon, itemlist);
                                            if (addCountItem > 0) {
                                                userBalancesVO = cunstructUserBalancesVO(c2sTransferVO);
                                                final int updateCount = updateUserBalance(toCon, userBalancesVO);
                                                if (updateCount > 0) {
                                                    toCon.commit();
                                                    out.write("Transaction ID=" + txnID + " Successfull UserID=" + userBalancesVO.getUserID() + " PREV BAL=" + userBalancesVO
                                                        .getPreviousBalance() + " POST BAL(Current bal)=" + userBalancesVO.getBalance() + " \n");
                                                } else {
                                                    toCon.rollback();
                                                    out.write("Balance not update for Transaction ID=" + txnID + " \n");
                                                    continue;
                                                }
                                            } else {
                                                toCon.rollback();
                                                out.write("Transaction ID=" + txnID + " is not insert into To Database: table c2s_transfer_items \n");
                                                continue;
                                            }
                                        } else {
                                            toCon.rollback();
                                            out.write("Transaction ID=" + txnID + " is not insert into To Database: table c2s_transfer \n");
                                            continue;
                                        }
                                    }// End of if(itemlist!=null &
                                     // itemlist.size()>0)
                                    else {
                                        toCon.rollback();
                                        out.write("Transaction ID=" + txnID + " is not exist in From Database: table c2s_transfer_items \n");
                                        continue;
                                    }
                                }// End of else block of
                                 // if(!"200".equals(c2sTransferVO.getTransferStatus()))
                            }// End of if(c2sTransferVO!=null)
                            else {
                                toCon.rollback();
                                out.write("Transaction ID=" + txnID + " is not exist in From Database: table c2s_transfer \n");
                                continue;
                            }
                        }// End of if(!BTSLUtil.isNullString(txnIDsArr[i]))
                    }// End of try block
                    catch (Exception e) {
                        try {
                            if (toCon != null) {
                                toCon.rollback();
                            }
                        } catch (Exception ee) {
                            _log.errorTrace(METHOD_NAME, ee);
                        }
                        // try{if(fromCon!=null)fromCon.rollback();}catch(Exception
                        // ee){}
                        out.write("Transaction ID=" + txnID + " Exception=" + e.getMessage() + " \n");
                        _log.errorTrace(METHOD_NAME, e);
                    } finally {
                        c2sTransferVO = null;
                        itemlist = null;
                        txnID = null;
                        userBalancesVO = null;
                    }
                } // End of for loop
                if (out != null) {
                    out.close();
                }
            } else {
                throw new BTSLBaseException("process : transaction ids not avilable at C2S_TRANSACTION_IDS key");
            }
        } catch (Exception e) {
            try {
                if (toCon != null) {
                    toCon.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            // try{if(fromCon!=null)fromCon.rollback();}catch(Exception ee){}
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME, "");
        } finally {
            try {
                if (fromCon != null) {
                    fromCon.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (toCon != null) {
                    toCon.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (out != null) {
                out.close();
            }
            _log.debug(METHOD_NAME, "C2STXNMoveFromTesttoLive: process finished Check the Error File =" + errorFile);
        }

    }

    private static void load(String fileName) throws IOException {
        final File file = new File(fileName);
        try( final FileInputStream fileInputStream = new FileInputStream(file);)
        {
        properties.load(fileInputStream);
        fileInputStream.close();
        }
    }// end of load

    private String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }// end of getProperty

    public Connection getFromConnection() throws SQLException, ClassNotFoundException {
        final String METHOD_NAME = "getFromConnection";
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new ClassNotFoundException("getFromConnection DB_CONN_FAILED");
        }
        Connection conn = null;
        /*try {*/
            final String db_url = getProperty("from_url");
            final String db_user = getProperty("from_userid");
            final String db_password = getProperty("from_passwd");
            conn = DriverManager.getConnection(db_url, db_user, db_password);
            conn.setAutoCommit(false);
        /*} catch (SQLException e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new Exception("getFromConnection DB_CONN_FAILED Exception");
        }*/
        return conn;
    }

    public Connection getToConnection() throws SQLException, ClassNotFoundException {
        final String METHOD_NAME = "getToConnection";
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new ClassNotFoundException("getToConnection DB_CONN_FAILED");
        }
        Connection conn = null;
        try {
            final String db_url = getProperty("to_url");
            final String db_user = getProperty("to_userid");
            final String db_password = getProperty("to_passwd");
            conn = DriverManager.getConnection(db_url, db_user, db_password);
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new SQLException("getToConnection DB_CONN_FAILED Exception");
        }
        return conn;
    }

    private C2STransferVO loadTXNDetails(Connection p_con, String p_txnID) throws SQLException, ParseException {
    	//local_index_implemented
        final String METHOD_NAME = "loadTXNDetails";
        _log.debug(METHOD_NAME, "loadTXNDetails Entered TRANSFER_ID=" + p_txnID);
        C2STransferVO c2sTransferVO = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            final StringBuffer str = new StringBuffer("SELECT TRANSFER_ID, TRANSFER_DATE, TRANSFER_DATE_TIME, NETWORK_CODE, SENDER_ID, SENDER_CATEGORY,");
            str.append("PRODUCT_CODE, SENDER_MSISDN, RECEIVER_MSISDN, RECEIVER_NETWORK_CODE, TRANSFER_VALUE, ERROR_CODE, REQUEST_GATEWAY_TYPE,");
            str.append("REQUEST_GATEWAY_CODE, REFERENCE_ID, SERVICE_TYPE, DIFFERENTIAL_APPLICABLE, PIN_SENT_TO_MSISDN, LANGUAGE, COUNTRY, SKEY, ");
            str.append("SKEY_GENERATION_TIME, SKEY_SENT_TO_MSISDN, REQUEST_THROUGH_QUEUE, CREDIT_BACK_STATUS, QUANTITY, RECONCILIATION_FLAG, ");
            str.append("RECONCILIATION_DATE, RECONCILIATION_BY, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TRANSFER_STATUS, CARD_GROUP_SET_ID,");
            str.append("VERSION, CARD_GROUP_ID, SENDER_TRANSFER_VALUE, RECEIVER_ACCESS_FEE, RECEIVER_TAX1_TYPE, RECEIVER_TAX1_RATE, RECEIVER_TAX1_VALUE,");
            str.append("RECEIVER_TAX2_TYPE, RECEIVER_TAX2_RATE, RECEIVER_TAX2_VALUE, RECEIVER_VALIDITY, RECEIVER_TRANSFER_VALUE, RECEIVER_BONUS_VALUE, ");
            str.append("RECEIVER_GRACE_PERIOD, RECEIVER_BONUS_VALIDITY, CARD_GROUP_CODE, RECEIVER_VALPERIOD_TYPE, TEMP_TRANSFER_ID, TRANSFER_PROFILE_ID, ");
            str.append("COMMISSION_PROFILE_ID, DIFFERENTIAL_GIVEN, GRPH_DOMAIN_CODE, SOURCE_TYPE, SUB_SERVICE, START_TIME, END_TIME, SERIAL_NUMBER, ");
            str.append("EXT_CREDIT_INTFCE_TYPE, BONUS_DETAILS, ACTIVE_USER_ID FROM C2S_TRANSFERS WHERE TRANSFER_DATE=? AND TRANSFER_ID=?");

            _log.debug(METHOD_NAME, "loadTXNDetails Query =" + str.toString()); 

            stmt = p_con.prepareStatement(str.toString());
            stmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_txnID)));
            stmt.setString(2, p_txnID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                c2sTransferVO = new C2STransferVO();
                c2sTransferVO.setTransferID(rs.getString("TRANSFER_ID"));
                c2sTransferVO.setTransferDate(rs.getDate("TRANSFER_DATE"));
                c2sTransferVO.setTransferDateTime(rs.getTimestamp("TRANSFER_DATE_TIME"));
                c2sTransferVO.setNetworkCode(rs.getString("NETWORK_CODE"));
                c2sTransferVO.setSenderID(rs.getString("SENDER_ID"));
                c2sTransferVO.setSenderCategoryCode(rs.getString("SENDER_CATEGORY"));
                c2sTransferVO.setProductCode(rs.getString("PRODUCT_CODE"));
                c2sTransferVO.setSenderMsisdn(rs.getString("SENDER_MSISDN"));
                c2sTransferVO.setReceiverMsisdn(rs.getString("RECEIVER_MSISDN"));
                c2sTransferVO.setReceiverNetworkCode(rs.getString("RECEIVER_NETWORK_CODE"));
                c2sTransferVO.setTransferValue(rs.getLong("TRANSFER_VALUE"));
                c2sTransferVO.setErrorCode(rs.getString("ERROR_CODE"));
                c2sTransferVO.setRequestGatewayType(rs.getString("REQUEST_GATEWAY_TYPE"));
                c2sTransferVO.setRequestGatewayCode(rs.getString("REQUEST_GATEWAY_CODE"));
                c2sTransferVO.setReferenceID(rs.getString("REFERENCE_ID"));
                c2sTransferVO.setServiceType(rs.getString("SERVICE_TYPE"));
                c2sTransferVO.setDifferentialApplicable(rs.getString("DIFFERENTIAL_APPLICABLE"));
                c2sTransferVO.setPinSentToMsisdn(rs.getString("PIN_SENT_TO_MSISDN"));
                c2sTransferVO.setLanguage(rs.getString("LANGUAGE"));
                c2sTransferVO.setCountry(rs.getString("COUNTRY"));
                c2sTransferVO.setSkey(rs.getLong("SKEY"));
                c2sTransferVO.setSkeyGenerationTime(rs.getTimestamp("SKEY_GENERATION_TIME"));
                c2sTransferVO.setSkeySentToMsisdn(rs.getString("SKEY_SENT_TO_MSISDN"));
                c2sTransferVO.setRequestThroughQueue(rs.getString("REQUEST_THROUGH_QUEUE"));
                c2sTransferVO.setCreditBackStatus(rs.getString("CREDIT_BACK_STATUS"));
                c2sTransferVO.setQuantity(rs.getLong("QUANTITY"));
                c2sTransferVO.setReconciliationFlag(rs.getString("RECONCILIATION_FLAG"));
                c2sTransferVO.setReconciliationDate(rs.getTimestamp("RECONCILIATION_DATE"));
                c2sTransferVO.setReconciliationBy(rs.getString("RECONCILIATION_BY"));
                c2sTransferVO.setCreatedOn(rs.getTimestamp("CREATED_ON"));
                c2sTransferVO.setCreatedBy(rs.getString("CREATED_BY"));
                c2sTransferVO.setModifiedOn(rs.getTimestamp("MODIFIED_ON"));
                c2sTransferVO.setModifiedBy(rs.getString("MODIFIED_BY"));
                c2sTransferVO.setTransferStatus(rs.getString("TRANSFER_STATUS"));
                c2sTransferVO.setCardGroupSetID(rs.getString("CARD_GROUP_SET_ID"));
                c2sTransferVO.setVersion(rs.getString("VERSION"));
                c2sTransferVO.setCardGroupID(rs.getString("CARD_GROUP_ID"));
                c2sTransferVO.setSenderTransferValue(rs.getLong("SENDER_TRANSFER_VALUE"));
                c2sTransferVO.setReceiverAccessFee(rs.getLong("RECEIVER_ACCESS_FEE"));
                c2sTransferVO.setReceiverTax1Type(rs.getString("RECEIVER_TAX1_TYPE"));
                c2sTransferVO.setReceiverTax1Rate(rs.getDouble("RECEIVER_TAX1_RATE"));
                c2sTransferVO.setReceiverTax1Value(rs.getLong("RECEIVER_TAX1_VALUE"));
                c2sTransferVO.setReceiverTax2Type(rs.getString("RECEIVER_TAX2_TYPE"));
                c2sTransferVO.setReceiverTax2Rate(rs.getDouble("RECEIVER_TAX2_RATE"));
                c2sTransferVO.setReceiverTax2Value(rs.getLong("RECEIVER_TAX2_VALUE"));
                c2sTransferVO.setReceiverValidity(rs.getInt("RECEIVER_VALIDITY"));
                c2sTransferVO.setReceiverTransferValue(rs.getLong("RECEIVER_TRANSFER_VALUE"));
                c2sTransferVO.setReceiverBonusValue(rs.getLong("RECEIVER_BONUS_VALUE"));
                c2sTransferVO.setReceiverGracePeriod(rs.getLong("RECEIVER_GRACE_PERIOD"));
                c2sTransferVO.setReceiverBonusValidity(rs.getInt("RECEIVER_BONUS_VALIDITY"));
                c2sTransferVO.setCardGroupCode(rs.getString("CARD_GROUP_CODE"));
                c2sTransferVO.setReceiverValPeriodType(rs.getString("RECEIVER_VALPERIOD_TYPE"));
                c2sTransferVO.setDifferentialGiven(rs.getString("DIFFERENTIAL_GIVEN"));
                c2sTransferVO.setGrphDomainCode(rs.getString("GRPH_DOMAIN_CODE"));
                c2sTransferVO.setSourceType(rs.getString("SOURCE_TYPE"));
                c2sTransferVO.setSubService(rs.getString("SUB_SERVICE"));
                c2sTransferVO.setSerialNumber(rs.getString("SERIAL_NUMBER"));
                c2sTransferVO.setExtCreditIntfceType(rs.getString("EXT_CREDIT_INTFCE_TYPE"));
                c2sTransferVO.setBonusBundleCode(rs.getString("BONUS_DETAILS"));
                c2sTransferVO.setActiveUserId(rs.getString("ACTIVE_USER_ID"));
                c2sTransferVO.setRequestStartTime(rs.getLong("START_TIME"));

                // no getter setter avilable
                c2sTransferVO.setLastTransferId(rs.getString("TEMP_TRANSFER_ID"));
                c2sTransferVO.setActiveUserName(rs.getString("TRANSFER_PROFILE_ID"));
                c2sTransferVO.setErrorMessage(rs.getString("COMMISSION_PROFILE_ID"));
                c2sTransferVO.setCreditAmount(rs.getLong("END_TIME"));
            }

        } catch (SQLException e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new SQLException("loadTXNDetails  Exception=" + e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
            _log.debug(METHOD_NAME, "loadTXNDetails Exiting TRANSFER_ID=" + p_txnID);
        }
        return c2sTransferVO;
    }

    private ArrayList<C2STransferItemVO> loadTXNItemList(Connection p_con, String p_txnID) throws SQLException {
        final String METHOD_NAME = "loadTXNItemList";
        _log.debug(METHOD_NAME, "loadTXNItemList Entered TRANSFER_ID=" + p_txnID);
        C2STransferItemVO c2sTransferItemVO = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        final ArrayList<C2STransferItemVO> list = new ArrayList<C2STransferItemVO>();

        try {
            final StringBuffer str = new StringBuffer("SELECT TRANSFER_ID, MSISDN, ENTRY_DATE, REQUEST_VALUE, PREVIOUS_BALANCE, POST_BALANCE, USER_TYPE,");
            str.append("TRANSFER_TYPE, ENTRY_TYPE, VALIDATION_STATUS, UPDATE_STATUS, TRANSFER_VALUE, INTERFACE_TYPE, INTERFACE_ID,");
            str.append("INTERFACE_RESPONSE_CODE, INTERFACE_REFERENCE_ID, SUBSCRIBER_TYPE, SERVICE_CLASS_CODE, MSISDN_PREVIOUS_EXPIRY, ");
            str.append("MSISDN_NEW_EXPIRY, TRANSFER_STATUS, TRANSFER_DATE, TRANSFER_DATE_TIME, ENTRY_DATE_TIME, FIRST_CALL, SNO, PREFIX_ID, ");
            str.append("SERVICE_CLASS_ID, PROTOCOL_STATUS, ACCOUNT_STATUS, ADJUST_DR_TXN_TYPE, ADJUST_DR_TXN_ID, ADJUST_DR_UPDATE_STATUS, ");
            str.append("ADJUST_CR_TXN_TYPE, ADJUST_CR_TXN_ID, ADJUST_CR_UPDATE_STATUS, ADJUST_VALUE, REFERENCE_ID, COUNTRY, LANGUAGE");
            str.append(" FROM C2S_TRANSFER_ITEMS WHERE TRANSFER_ID=?");

            _log.debug(METHOD_NAME, "loadTXNItemList Query =" + str);

            stmt = p_con.prepareStatement(str.toString());
            stmt.setString(1, p_txnID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                c2sTransferItemVO = new C2STransferItemVO();
                c2sTransferItemVO.setTransferID(rs.getString("TRANSFER_ID"));
                c2sTransferItemVO.setMsisdn(rs.getString("MSISDN"));
                c2sTransferItemVO.setEntryDate(rs.getDate("ENTRY_DATE"));
                c2sTransferItemVO.setRequestValue(rs.getLong("REQUEST_VALUE"));
                c2sTransferItemVO.setPreviousBalance(rs.getLong("PREVIOUS_BALANCE"));
                c2sTransferItemVO.setPostBalance(rs.getLong("POST_BALANCE"));
                c2sTransferItemVO.setUserType(rs.getString("USER_TYPE"));
                c2sTransferItemVO.setTransferType(rs.getString("TRANSFER_TYPE"));
                c2sTransferItemVO.setEntryType(rs.getString("ENTRY_TYPE"));
                c2sTransferItemVO.setValidationStatus(rs.getString("VALIDATION_STATUS"));
                c2sTransferItemVO.setUpdateStatus(rs.getString("UPDATE_STATUS"));
                c2sTransferItemVO.setTransferValue(rs.getLong("TRANSFER_VALUE"));
                c2sTransferItemVO.setInterfaceType(rs.getString("INTERFACE_TYPE"));
                c2sTransferItemVO.setInterfaceID(rs.getString("INTERFACE_ID"));
                c2sTransferItemVO.setInterfaceResponseCode(rs.getString("INTERFACE_RESPONSE_CODE"));
                c2sTransferItemVO.setInterfaceReferenceID(rs.getString("INTERFACE_REFERENCE_ID"));
                c2sTransferItemVO.setSubscriberType(rs.getString("SUBSCRIBER_TYPE"));
                c2sTransferItemVO.setServiceClassCode(rs.getString("SERVICE_CLASS_CODE"));
                c2sTransferItemVO.setPreviousExpiry(rs.getTimestamp("MSISDN_PREVIOUS_EXPIRY"));
                c2sTransferItemVO.setNewExpiry(rs.getTimestamp("MSISDN_NEW_EXPIRY"));
                c2sTransferItemVO.setTransferStatus(rs.getString("TRANSFER_STATUS"));
                c2sTransferItemVO.setTransferDate(rs.getDate("TRANSFER_DATE"));
                c2sTransferItemVO.setTransferDateTime(rs.getTimestamp("TRANSFER_DATE_TIME"));
                c2sTransferItemVO.setEntryDateTime(rs.getTimestamp("ENTRY_DATE_TIME"));
                c2sTransferItemVO.setFirstCall(rs.getString("FIRST_CALL"));
                c2sTransferItemVO.setSNo(rs.getInt("SNO"));
                c2sTransferItemVO.setPrefixID(rs.getLong("PREFIX_ID"));
                c2sTransferItemVO.setServiceClass(rs.getString("SERVICE_CLASS_ID"));
                c2sTransferItemVO.setProtocolStatus(rs.getString("PROTOCOL_STATUS"));
                c2sTransferItemVO.setAccountStatus(rs.getString("ACCOUNT_STATUS"));
                c2sTransferItemVO.setAdjustValue(rs.getLong("ADJUST_VALUE"));
                c2sTransferItemVO.setReferenceID(rs.getString("REFERENCE_ID"));
                c2sTransferItemVO.setCountry(rs.getString("COUNTRY"));
                c2sTransferItemVO.setLanguage(rs.getString("LANGUAGE"));

                // No setter getter avilable
                c2sTransferItemVO.setBonus1Name(rs.getString("ADJUST_DR_TXN_TYPE"));
                c2sTransferItemVO.setBonus2Name(rs.getString("ADJUST_DR_TXN_ID"));
                c2sTransferItemVO.setBonusBundleValidities(rs.getString("ADJUST_DR_UPDATE_STATUS"));
                c2sTransferItemVO.setInterfaceReferenceID1(rs.getString("ADJUST_CR_TXN_TYPE"));
                c2sTransferItemVO.setInterfaceReferenceID2(rs.getString("ADJUST_CR_TXN_ID"));
                c2sTransferItemVO.setInterfaceHandlerClass(rs.getString("ADJUST_CR_UPDATE_STATUS"));

                list.add(c2sTransferItemVO);
            }

        } catch (SQLException e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new SQLException("loadTXNItemList  Exception=" + e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
            _log.debug(METHOD_NAME, "loadTXNItemList Exiting TRANSFER_ID=" + p_txnID);
        }
        return list;
    }

    private int insertTXNDetails(Connection p_con, C2STransferVO p_c2sTransferVO) throws SQLException  {
    	//local_index_insert_statement
        final String METHOD_NAME = "insertTXNDetails";
        _log.info(METHOD_NAME, "insertTXNDetails Entered ");
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
            int i = 1;
            final StringBuffer insertQueryBuff = new StringBuffer(
                " INSERT INTO c2s_transfers(transfer_id,transfer_date,transfer_date_time,network_code,sender_id,sender_category,product_code,sender_msisdn, ");
            insertQueryBuff.append(" receiver_msisdn,receiver_network_code,transfer_value,error_code,request_gateway_type,request_gateway_code, ");
            insertQueryBuff.append(" grph_domain_code,reference_id,service_type,pin_sent_to_msisdn,language,country,skey,skey_generation_time, ");
            insertQueryBuff.append(" skey_sent_to_msisdn,request_through_queue,quantity,created_by,created_on,modified_by,modified_on,transfer_status, ");
            insertQueryBuff.append(" card_group_set_id,version,card_group_id,sender_transfer_value,receiver_access_fee,receiver_tax1_type, ");
            insertQueryBuff.append(" receiver_tax1_rate,receiver_tax1_value,receiver_tax2_type,receiver_tax2_rate,receiver_tax2_value, ");
            insertQueryBuff.append(" receiver_validity,receiver_transfer_value,receiver_bonus_value,receiver_grace_period,receiver_bonus_validity, ");
            insertQueryBuff
                .append(" card_group_code,receiver_valperiod_type,temp_transfer_id,transfer_profile_id,commission_profile_id,source_type,sub_service,start_time,end_time,serial_number, ");
            insertQueryBuff
                .append(" EXT_CREDIT_INTFCE_TYPE,BONUS_DETAILS,ACTIVE_USER_ID,DIFFERENTIAL_APPLICABLE,RECONCILIATION_FLAG,RECONCILIATION_DATE,RECONCILIATION_BY,CREDIT_BACK_STATUS,DIFFERENTIAL_GIVEN) ");
            insertQueryBuff
                .append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

            pstmtInsert = p_con.prepareStatement(insertQueryBuff.toString());
            pstmtInsert.setString(i++, p_c2sTransferVO.getTransferID());
            pstmtInsert.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_c2sTransferVO.getTransferDate()));
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getTransferDateTime()));
            pstmtInsert.setString(i++, p_c2sTransferVO.getNetworkCode());
            pstmtInsert.setString(i++, p_c2sTransferVO.getSenderID());
            pstmtInsert.setString(i++, p_c2sTransferVO.getSenderCategoryCode());
            pstmtInsert.setString(i++, p_c2sTransferVO.getProductCode());
            pstmtInsert.setString(i++, p_c2sTransferVO.getSenderMsisdn());
            pstmtInsert.setString(i++, p_c2sTransferVO.getReceiverMsisdn());
            pstmtInsert.setString(i++, p_c2sTransferVO.getReceiverNetworkCode());
            pstmtInsert.setLong(i++, p_c2sTransferVO.getTransferValue());
            pstmtInsert.setString(i++, p_c2sTransferVO.getErrorCode());
            pstmtInsert.setString(i++, p_c2sTransferVO.getRequestGatewayType());
            pstmtInsert.setString(i++, p_c2sTransferVO.getRequestGatewayCode());
            pstmtInsert.setString(i++, p_c2sTransferVO.getGrphDomainCode());
            pstmtInsert.setString(i++, p_c2sTransferVO.getReferenceID());
            pstmtInsert.setString(i++, p_c2sTransferVO.getServiceType());
            pstmtInsert.setString(i++, p_c2sTransferVO.getPinSentToMsisdn());
            pstmtInsert.setString(i++, p_c2sTransferVO.getLanguage());
            pstmtInsert.setString(i++, p_c2sTransferVO.getCountry());
            pstmtInsert.setLong(i++, p_c2sTransferVO.getSkey());
            if (p_c2sTransferVO.getSkeyGenerationTime() == null) {
                pstmtInsert.setNull(i++, Types.TIMESTAMP);
            } else {
                pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getSkeyGenerationTime()));
            }
            pstmtInsert.setString(i++, p_c2sTransferVO.getSkeySentToMsisdn());
            pstmtInsert.setString(i++, p_c2sTransferVO.getRequestThroughQueue());
            pstmtInsert.setLong(i++, p_c2sTransferVO.getQuantity());
            pstmtInsert.setString(i++, p_c2sTransferVO.getCreatedBy());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getCreatedOn()));
            pstmtInsert.setString(i++, p_c2sTransferVO.getModifiedBy());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getModifiedOn()));
            pstmtInsert.setString(i++, p_c2sTransferVO.getTransferStatus());
            pstmtInsert.setString(i++, p_c2sTransferVO.getCardGroupSetID());
            pstmtInsert.setString(i++, p_c2sTransferVO.getVersion());
            pstmtInsert.setString(i++, p_c2sTransferVO.getCardGroupID());
            pstmtInsert.setLong(i++, p_c2sTransferVO.getSenderTransferValue());
            pstmtInsert.setLong(i++, p_c2sTransferVO.getReceiverAccessFee());
            pstmtInsert.setString(i++, p_c2sTransferVO.getReceiverTax1Type());
            pstmtInsert.setDouble(i++, p_c2sTransferVO.getReceiverTax1Rate());
            pstmtInsert.setLong(i++, p_c2sTransferVO.getReceiverTax1Value());
            pstmtInsert.setString(i++, p_c2sTransferVO.getReceiverTax2Type());
            pstmtInsert.setDouble(i++, p_c2sTransferVO.getReceiverTax2Rate());
            pstmtInsert.setLong(i++, p_c2sTransferVO.getReceiverTax2Value());
            pstmtInsert.setInt(i++, p_c2sTransferVO.getReceiverValidity());
            pstmtInsert.setLong(i++, p_c2sTransferVO.getReceiverTransferValue());
            pstmtInsert.setLong(i++, p_c2sTransferVO.getReceiverBonusValue());
            pstmtInsert.setLong(i++, p_c2sTransferVO.getReceiverGracePeriod());
            pstmtInsert.setInt(i++, p_c2sTransferVO.getReceiverBonusValidity());
            pstmtInsert.setString(i++, p_c2sTransferVO.getCardGroupCode());
            pstmtInsert.setString(i++, p_c2sTransferVO.getReceiverValPeriodType());
            pstmtInsert.setString(i++, p_c2sTransferVO.getLastTransferId());
            pstmtInsert.setString(i++, p_c2sTransferVO.getActiveUserName());
            pstmtInsert.setString(i++, p_c2sTransferVO.getErrorMessage());
            pstmtInsert.setString(i++, p_c2sTransferVO.getSourceType());
            pstmtInsert.setString(i++, p_c2sTransferVO.getSubService());
            pstmtInsert.setLong(i++, p_c2sTransferVO.getRequestStartTime());
            pstmtInsert.setLong(i++, p_c2sTransferVO.getCreditAmount());
            pstmtInsert.setString(i++, p_c2sTransferVO.getSerialNumber());
            pstmtInsert.setString(i++, p_c2sTransferVO.getExtCreditIntfceType());
            pstmtInsert.setString(i++, p_c2sTransferVO.getBonusBundleCode());
            pstmtInsert.setString(i++, p_c2sTransferVO.getActiveUserId());
            pstmtInsert.setString(i++, p_c2sTransferVO.getDifferentialApplicable());
            pstmtInsert.setString(i++, p_c2sTransferVO.getReconciliationFlag());
            if (p_c2sTransferVO.getReconciliationDate() == null) {
                pstmtInsert.setNull(i++, Types.TIMESTAMP);
            } else {
                pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_c2sTransferVO.getReconciliationDate()));
            }
            pstmtInsert.setString(i++, p_c2sTransferVO.getReconciliationBy());
            pstmtInsert.setString(i++, p_c2sTransferVO.getCreditBackStatus());
            pstmtInsert.setString(i++, p_c2sTransferVO.getDifferentialGiven());

            addCount = pstmtInsert.executeUpdate();
            addCount = BTSLUtil.getInsertCount(addCount);// added to make code compatible with insertion in partitioned table in postgres DB
 
            return addCount;
        }// end of try

        catch (SQLException e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new SQLException("insertTXNDetails  Exception=" + e.getMessage());
        }// end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.debug(METHOD_NAME, "insertTXNDetails Exiting addCount=" + addCount);
        }// end of finally
    }

    private int insertTXNItemList(Connection p_con, ArrayList<C2STransferItemVO> p_list) throws SQLException {
        final String METHOD_NAME = "insertTXNItemList";
        _log.debug(METHOD_NAME, "insertTXNItemList Entered p_list=" + p_list.size());
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
            final StringBuffer insertQueryBuff = new StringBuffer(" INSERT INTO C2S_TRANSFER_ITEMS(TRANSFER_ID, MSISDN, ENTRY_DATE, REQUEST_VALUE,  ");
            insertQueryBuff.append(" PREVIOUS_BALANCE, POST_BALANCE, USER_TYPE, TRANSFER_TYPE,ENTRY_TYPE,VALIDATION_STATUS, UPDATE_STATUS,");
            insertQueryBuff.append(" TRANSFER_VALUE, INTERFACE_TYPE, INTERFACE_ID, INTERFACE_RESPONSE_CODE, INTERFACE_REFERENCE_ID, SUBSCRIBER_TYPE,");
            insertQueryBuff.append(" SERVICE_CLASS_CODE, MSISDN_PREVIOUS_EXPIRY, MSISDN_NEW_EXPIRY, TRANSFER_STATUS, TRANSFER_DATE, TRANSFER_DATE_TIME,");
            insertQueryBuff.append(" ENTRY_DATE_TIME, FIRST_CALL, SNO, PREFIX_ID, SERVICE_CLASS_ID, PROTOCOL_STATUS, ACCOUNT_STATUS, ADJUST_DR_TXN_TYPE, ");
            insertQueryBuff.append(" ADJUST_DR_TXN_ID,ADJUST_DR_UPDATE_STATUS, ADJUST_CR_TXN_TYPE, ADJUST_CR_TXN_ID, ADJUST_CR_UPDATE_STATUS, ADJUST_VALUE,  ");
            insertQueryBuff.append(" REFERENCE_ID,COUNTRY, LANGUAGE ) ");
            insertQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ) ");

            pstmtInsert = p_con.prepareStatement(insertQueryBuff.toString());
            C2STransferItemVO c2sTransferItemVO = null;
            int listSizes=p_list.size();
            for (int count = 0; count <listSizes ; count++) {
                c2sTransferItemVO = p_list.get(count);
                int i = 1;
                pstmtInsert.setString(i++, c2sTransferItemVO.getTransferID());
                pstmtInsert.setString(i++, c2sTransferItemVO.getMsisdn());
                pstmtInsert.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(c2sTransferItemVO.getEntryDate()));
                pstmtInsert.setLong(i++, c2sTransferItemVO.getRequestValue());
                pstmtInsert.setLong(i++, c2sTransferItemVO.getPreviousBalance());
                pstmtInsert.setLong(i++, c2sTransferItemVO.getPostBalance());
                pstmtInsert.setString(i++, c2sTransferItemVO.getUserType());
                pstmtInsert.setString(i++, c2sTransferItemVO.getTransferType());
                pstmtInsert.setString(i++, c2sTransferItemVO.getEntryType());
                pstmtInsert.setString(i++, c2sTransferItemVO.getValidationStatus());
                pstmtInsert.setString(i++, c2sTransferItemVO.getUpdateStatus());
                pstmtInsert.setLong(i++, c2sTransferItemVO.getTransferValue());
                pstmtInsert.setString(i++, c2sTransferItemVO.getInterfaceType());
                pstmtInsert.setString(i++, c2sTransferItemVO.getInterfaceID());
                pstmtInsert.setString(i++, c2sTransferItemVO.getInterfaceResponseCode());
                pstmtInsert.setString(i++, c2sTransferItemVO.getInterfaceReferenceID());
                pstmtInsert.setString(i++, c2sTransferItemVO.getSubscriberType());
                pstmtInsert.setString(i++, c2sTransferItemVO.getServiceClassCode());
                if (c2sTransferItemVO.getPreviousExpiry() == null) {
                    pstmtInsert.setNull(i++, Types.DATE);
                } else {
                    pstmtInsert.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(c2sTransferItemVO.getPreviousExpiry()));
                }
                if (c2sTransferItemVO.getNewExpiry() == null) {
                    pstmtInsert.setNull(i++, Types.DATE);
                } else {
                    pstmtInsert.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(c2sTransferItemVO.getNewExpiry()));
                }
                pstmtInsert.setString(i++, c2sTransferItemVO.getTransferStatus());
                pstmtInsert.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(c2sTransferItemVO.getTransferDate()));
                pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(c2sTransferItemVO.getTransferDateTime()));
                if (c2sTransferItemVO.getEntryDateTime() == null) {
                    pstmtInsert.setNull(i++, Types.TIMESTAMP);
                } else {
                    pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(c2sTransferItemVO.getEntryDateTime()));
                }
                pstmtInsert.setString(i++, c2sTransferItemVO.getFirstCall());
                pstmtInsert.setInt(i++, c2sTransferItemVO.getSNo());
                pstmtInsert.setLong(i++, c2sTransferItemVO.getPrefixID());
                pstmtInsert.setString(i++, c2sTransferItemVO.getServiceClass());
                pstmtInsert.setString(i++, c2sTransferItemVO.getProtocolStatus());
                pstmtInsert.setString(i++, c2sTransferItemVO.getAccountStatus());

                // c2sTransferItemVO.setBonus1Name(rs.getString("ADJUST_DR_TXN_TYPE"));
                pstmtInsert.setString(i++, c2sTransferItemVO.getBonus1Name());

                // c2sTransferItemVO.setBonus2Name(rs.getString("ADJUST_DR_TXN_ID"));
                pstmtInsert.setString(i++, c2sTransferItemVO.getBonus2Name());

                // c2sTransferItemVO.setBonusBundleValidities(rs.getString("ADJUST_DR_UPDATE_STATUS"));
                pstmtInsert.setString(i++, c2sTransferItemVO.getBonusBundleValidities());

                // c2sTransferItemVO.setInterfaceReferenceID1(rs.getString("ADJUST_CR_TXN_TYPE"));
                pstmtInsert.setString(i++, c2sTransferItemVO.getInterfaceReferenceID1());

                // c2sTransferItemVO.setInterfaceReferenceID2(rs.getString("ADJUST_CR_TXN_ID"));
                pstmtInsert.setString(i++, c2sTransferItemVO.getInterfaceReferenceID2());

                // c2sTransferItemVO.setInterfaceHandlerClass(rs.getString("ADJUST_CR_UPDATE_STATUS"));
                pstmtInsert.setString(i++, c2sTransferItemVO.getInterfaceHandlerClass());

                pstmtInsert.setLong(i++, c2sTransferItemVO.getAdjustValue());
                pstmtInsert.setString(i++, c2sTransferItemVO.getReferenceID());
                pstmtInsert.setString(i++, c2sTransferItemVO.getCountry());
                pstmtInsert.setString(i++, c2sTransferItemVO.getLanguage());

                addCount = addCount + pstmtInsert.executeUpdate();
                addCount = BTSLUtil.getInsertCount(addCount);// added to make code compatible with insertion in partitioned table in postgres DB
                 
                
            }
            return addCount;
        }// end of try

        catch (SQLException e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new SQLException("insertTXNItemList  Exception=" + e.getMessage());
        }// end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.debug(METHOD_NAME, "insertTXNItemList Exiting addCount=" + addCount);
        }// end of finally
    }

    private int updateUserBalance(Connection p_con, UserBalancesVO p_userBalancesVO) throws SQLException {
        final String METHOD_NAME = "updateUserBalance";
        _log.debug(METHOD_NAME, "updateUserBalance Entered p_UserBalancesVO=" + p_userBalancesVO);

        int updateCount = 0;

        long balance = 0;
        long newBalance = 0;
        ResultSet rs = null;
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmt = null;

        final StringBuffer strBuffSelect = new StringBuffer();
        strBuffSelect.append(" SELECT balance ");
        strBuffSelect.append(" FROM user_balances ");
        // DB220120123for update WITH RS
        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
            strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ?   FOR UPDATE OF balance WITH RS ");
        } else {
            strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ?   FOR UPDATE OF balance ");
        }

        final StringBuffer strBuffUpdate = new StringBuffer();
        strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
        strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ? ");
        strBuffUpdate.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? ");
        final String[] strArr = null;
        try {
            final String sqlSelect = strBuffSelect.toString();
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_userBalancesVO.getUserID());
            pstmt.setString(2, p_userBalancesVO.getProductCode());
            pstmt.setString(3, p_userBalancesVO.getNetworkCode());
            pstmt.setString(4, p_userBalancesVO.getNetworkFor());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                balance = rs.getLong("balance");
            }
            _log.debug(METHOD_NAME, "updateUserBalance  balance=" + balance);

            newBalance = balance - p_userBalancesVO.getQuantityToBeUpdated();

            _log.debug(METHOD_NAME, "updateUserBalance  newBalance=" + newBalance);
            final String updateQuery = strBuffUpdate.toString();

            pstmtUpdate = p_con.prepareStatement(updateQuery);
            p_userBalancesVO.setPreviousBalance(balance);
            p_userBalancesVO.setBalance(newBalance);

            pstmtUpdate.setLong(1, newBalance);
            pstmtUpdate.setString(2, p_userBalancesVO.getLastTransferType());
            pstmtUpdate.setString(3, p_userBalancesVO.getLastTransferID());
            pstmtUpdate.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
            pstmtUpdate.setString(5, p_userBalancesVO.getUserID());
            pstmtUpdate.setString(6, p_userBalancesVO.getProductCode());
            pstmtUpdate.setString(7, p_userBalancesVO.getNetworkCode());
            pstmtUpdate.setString(8, p_userBalancesVO.getNetworkFor());
            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new SQLException("updateUserBalance  Exception=" + e.getMessage());
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.debug(METHOD_NAME, "updateUserBalance Exiting updateCount=" + updateCount);
        }// end of finally
        return updateCount;
    }

    private UserBalancesVO cunstructUserBalancesVO(C2STransferVO p_c2sTransferVO) {
        final UserBalancesVO userBalancesVO = new UserBalancesVO();
        userBalancesVO.setUserID(p_c2sTransferVO.getSenderID());
        userBalancesVO.setProductCode(p_c2sTransferVO.getProductCode());
        userBalancesVO.setNetworkCode(p_c2sTransferVO.getNetworkCode());
        userBalancesVO.setNetworkFor(p_c2sTransferVO.getReceiverNetworkCode());
        userBalancesVO.setLastTransferType("TXN");
        userBalancesVO.setLastTransferID(p_c2sTransferVO.getTransferID());
        userBalancesVO.setLastTransferOn(p_c2sTransferVO.getTransferDateTime());
        userBalancesVO.setQuantityToBeUpdated(p_c2sTransferVO.getTransferValue());

        return userBalancesVO;
    }
}
