/*
 * Created on Jun 21, 2006
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.voms.voucher.businesslogic;

/**
 * @author vikas.yadav
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.util.VOMSVoucherQry;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.BurnRateIndicatorProcessQry;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.SqlParameterEncoder;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsprocesses.util.VoucherFileUploaderUtil;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;

public class VomsVoucherDAO {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    public VomsVoucherDAO() {
        super();
    }

    /**
     * This method is called after the vouchers have been inserted into the
     * voms_voucher to update the
     * vom_batch_summary
     * 
     * @param p_con
     * @param p_batchVO
     * @return int
     * @throws BTSLBaseException
     * @throws Exception
     */
    public int updateSummaryTable(Connection p_con, VomsBatchVO p_vomsBatchVO, boolean p_directVoucherEnable) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateSummaryTable() ", " Entered for Batch No=" + p_vomsBatchVO.getBatchNo() + " p_directVoucherEnable=" + p_directVoucherEnable);
        }
        String query;
        PreparedStatement psmt = null;
        PreparedStatement psmt1 = null;
        PreparedStatement psmt2 = null;
        PreparedStatement psmt3 = null;
        int addCount = 0;
        ResultSet rsVoSummary = null;
        StringBuffer strbuff = new StringBuffer();
        final String methodName = "updateSummaryTable";  
        try {
            strbuff.append("INSERT INTO voms_voucher_batch_summary (batch_no,total_generated ");
            if (p_directVoucherEnable) {
                strbuff.append(" ,total_enabled)  VALUES(?,?,?)");
            } else {
                strbuff.append(" )  VALUES(?,?)");
            }
            // query =
            // "INSERT INTO voms_voucher_batch_summary (batch_no,total_generated)VALUES(?,?)";
            psmt = p_con.prepareStatement(strbuff.toString());
            psmt.setString(1, p_vomsBatchVO.getBatchNo());
            //psmt.setInt(2, (int) p_vomsBatchVO.getNoOfVoucher());
            psmt.setInt(2, BTSLUtil.parseLongToInt(p_vomsBatchVO.getNoOfVoucher()));
            if (p_directVoucherEnable) {
                psmt.setInt(3, BTSLUtil.parseLongToInt( p_vomsBatchVO.getNoOfVoucher()));
            }
            addCount = psmt.executeUpdate();
            if (addCount <= 0) {
                _log.error(" updateSummaryTable", " Not able to insert in Summary table for Batch No=" + p_vomsBatchVO.getBatchNo());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VomsVoucherDAO[updateSummaryTable]", "", "", "", "Not able to insert in Summary table for Batch No=" + p_vomsBatchVO.getBatchNo());
                throw new BTSLBaseException(this, "updateSummaryTable", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
            }
            addCount = 0;
            query = "";

            if (_log.isDebugEnabled()) {
                _log.debug("updateSummaryTable() ", "  Values inserted in Voucher Batch Summary");
            }
            if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
                query = "SELECT total_generated,total_enabled FROM voms_voucher_summary WHERE summary_date = ? AND product_id=? AND production_network_code=? AND user_network_code=? FOR UPDATE WITH RS";
            } else {
                query = "SELECT total_generated,total_enabled FROM voms_voucher_summary WHERE summary_date = ? AND product_id=? AND production_network_code=? AND user_network_code=? FOR UPDATE";
            }
            psmt1 = p_con.prepareStatement(query);
            psmt1.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_vomsBatchVO.getCreatedOn()));
            psmt1.setString(2, p_vomsBatchVO.getProductID()); // sidd
            psmt1.setString(3, p_vomsBatchVO.getLocationCode());
            psmt1.setString(4, p_vomsBatchVO.getLocationCode());
            rsVoSummary = psmt1.executeQuery();
            query = "";

            // If the record exist add the total generated in the previous value
            // present in data base
            int i = 1;
            if (rsVoSummary.next()) {
                if (p_directVoucherEnable) {
                    query = "UPDATE voms_voucher_summary SET total_generated=?,total_enabled=? WHERE summary_date=? AND product_id=? AND production_network_code=? AND user_network_code=?";
                } else {
                    query = "UPDATE voms_voucher_summary SET total_generated=? WHERE summary_date=? AND product_id=? AND production_network_code=? AND user_network_code=?";
                }
                psmt2 = p_con.prepareStatement(query);
                psmt2.setInt(i++, (rsVoSummary.getInt("total_generated") + BTSLUtil.parseLongToInt(p_vomsBatchVO.getNoOfVoucher())));
                if (p_directVoucherEnable) {
                    psmt2.setInt(i++, (rsVoSummary.getInt("total_enabled") + BTSLUtil.parseLongToInt(p_vomsBatchVO.getNoOfVoucher()) ));
                }
                psmt2.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_vomsBatchVO.getCreatedOn()));
                psmt2.setString(i++, p_vomsBatchVO.getProductID());
                psmt2.setString(i++, p_vomsBatchVO.getLocationCode());
                psmt2.setString(i++, p_vomsBatchVO.getLocationCode());
                addCount = psmt2.executeUpdate();
                if (addCount <= 0) {
                    _log.error(" updateSummaryTable", " Not able to update in Summary table for Batch No=" + p_vomsBatchVO.getBatchNo());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VomsVoucherDAO[updateSummaryTable]", "", "", "", "Not able to update in Summary table for Batch No=" + p_vomsBatchVO.getBatchNo());
                    throw new BTSLBaseException(this, "updateSummaryTable", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
                }

                if (_log.isDebugEnabled()) {
                    _log.debug("updateSummaryTable() ", " Values updated in Voucher Summary");
                }
            } else {
                if (p_directVoucherEnable) {
                    query = "INSERT INTO voms_voucher_summary(summary_date,product_id,production_network_code,user_network_code,total_generated,total_enabled)VALUES(?,?,?,?,?,?)";
                } else {
                    query = "INSERT INTO voms_voucher_summary(summary_date,product_id,production_network_code,user_network_code,total_generated)VALUES(?,?,?,?,?)";
                }
                psmt3 = p_con.prepareStatement(query);
                psmt3.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_vomsBatchVO.getCreatedOn()));
                psmt3.setString(i++, p_vomsBatchVO.getProductID());
                psmt3.setString(i++, p_vomsBatchVO.getLocationCode());
                psmt3.setString(i++, p_vomsBatchVO.getLocationCode());
                psmt3.setInt(i++, BTSLUtil.parseLongToInt(p_vomsBatchVO.getNoOfVoucher()) );

                if (p_directVoucherEnable) {
                    psmt3.setInt(i++, BTSLUtil.parseLongToInt( p_vomsBatchVO.getNoOfVoucher()) );
                }       
                addCount = psmt3.executeUpdate();
                if (addCount <= 0) {
                    _log.error(" updateSummaryTable", " Not able to insert in Summary table for Batch No=" + p_vomsBatchVO.getBatchNo());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VomsVoucherDAO[updateSummaryTable]", "", "", "", "Not able to insert in Summary table for Batch No=" + p_vomsBatchVO.getBatchNo());
                    throw new BTSLBaseException(this, "updateSummaryTable", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("updateSummaryTable() ", " Values added in Voucher Summary");
                }
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sql) {
            _log.error(" updateSummaryTable() ", " SQLException sql=" + sql);
            _log.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[updateSummaryTable]", "", "", "", "Not able to insert in Summary table for Batch No=" + p_vomsBatchVO.getBatchNo() + " Getting Exception=" + sql.getMessage());
            throw new BTSLBaseException(this, "updateSummaryTable", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } catch (Exception e) {
            _log.error(" updateSummaryTable() ", "  Exception e=" + e);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[updateSummaryTable]", "", "", "", "Not able to insert in Summary table for Batch No=" + p_vomsBatchVO.getBatchNo() + " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException(this, "updateSummaryTable", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            try {
                if (rsVoSummary != null) {
                    rsVoSummary.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmt1 != null) {
                    psmt1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmt2 != null) {
                    psmt2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmt3 != null) {
                    psmt3.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            
            if (_log.isDebugEnabled()) {
                _log.debug("updateSummaryTable() ", " Exiting .............addCount " + addCount);
            }
        }
        return addCount;

    }

    /**
     * This method inserts vouchers in VOMS_VOUCHERS Table
     * 
     * @param p_con
     * @param p_batchVO
     * @param p_voucherArray
     * @return int
     * @throws BTSLBaseException
     */
    public int insertVouchers(Connection p_con, VomsBatchVO p_vomsBatchVO, ArrayList p_voucherArray, boolean p_isDirectEnable) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("insertVouchers ", "  Entered p_voucherArray =" + p_voucherArray.size() + " " + p_vomsBatchVO.getProductID() + " p_isDirectEnable=" + p_isDirectEnable);
        }
        int addCount = 0;
        String query;
        int counter = 0;
        ResultSet rsBatches = null;
        PreparedStatement psmt = null;
        String queryPin = null;
        PreparedStatement psmtPin = null;
        ResultSet rsPin = null;
        PreparedStatement psmtSerial = null;
        ResultSet rsSerial = null;
        String querySerial = null;
        long sequenceNumber = 0;
        int insertCounter = 0;
        int updateCount = 0;
        VomsVoucherVO vomsVoucherVO = null;
        String queryInsertVoucherENSummary;
        String querySelectVoucherENSummary;
        String queryUpdateVoucherENSummary;
        PreparedStatement psmtSummary = null;
        PreparedStatement psmtSummary1 = null;
        PreparedStatement psmtSummary2 = null;
        ResultSet rsSummary = null;
        boolean summaryAddedFlag = false;
        boolean summaryFoundFlag = false;
        final String methodName = "insertVouchers";

        try {
            sequenceNumber = VoucherFileUploaderUtil.loadNextSeqNumber(p_con, Integer.parseInt(BTSLUtil.getFinancialYear()), VOMSI.SEQNUM, VOMSI.ALL);

            _log.info("insertVouchers() ", " For Batch" + p_vomsBatchVO.getBatchNo() + " No of pins required " + p_vomsBatchVO.getNoOfVoucher());

            querySerial = " SELECT serial_no FROM voms_vouchers WHERE SERIAL_NO =?";
            if (_log.isDebugEnabled()) {
                _log.debug("insertVouchers() querySerial=", querySerial);
            }
            psmtSerial = p_con.prepareStatement(querySerial);

            queryPin = "SELECT pin_no FROM voms_vouchers WHERE pin_no=?";
            if (_log.isDebugEnabled()) {
                _log.debug("insertVouchers() querySerial=", queryPin);
            }
            psmtPin = p_con.prepareStatement(queryPin);

            queryInsertVoucherENSummary = "INSERT INTO VOMS_ENABLE_SUMMARY ( PRODUCT_ID, CREATED_DATE, VOUCHER_COUNT,EXPIRY_DATE ) VALUES (?,?,?,?)";
            querySelectVoucherENSummary = "SELECT VOUCHER_COUNT FROM VOMS_ENABLE_SUMMARY WHERE PRODUCT_ID = ? AND CREATED_DATE = ? AND EXPIRY_DATE = ?";
            queryUpdateVoucherENSummary = "UPDATE VOMS_ENABLE_SUMMARY SET VOUCHER_COUNT = (VOUCHER_COUNT + ?) WHERE PRODUCT_ID = ? AND CREATED_DATE = ? AND EXPIRY_DATE = ?";
            if (_log.isDebugEnabled()) {
                _log.debug("insertVouchers() queryInsertVoucherENSummary=", queryInsertVoucherENSummary);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("insertVouchers() querySelectVoucherENSummary=", querySelectVoucherENSummary);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("insertVouchers() queryUpdateVoucherENSummary=", queryUpdateVoucherENSummary);
            }

            if (p_isDirectEnable) {
                query = "INSERT INTO voms_vouchers(serial_no,pin_no,product_id,production_network_code,generation_batch_no,current_status,expiry_date,created_on,created_date, status,seq_no,modified_on,modified_by,ONE_TIME_USAGE,last_batch_no,enable_batch_no,previous_status)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            } else {
                query = "INSERT INTO voms_vouchers(serial_no,pin_no,product_id,production_network_code,generation_batch_no,current_status,expiry_date,CONSUME_BEFORE,created_on,created_date, status,seq_no,modified_on,modified_by,ONE_TIME_USAGE)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            }
            if (_log.isDebugEnabled()) {
                _log.debug("insertVouchers() query=", query);
            }

            psmt = p_con.prepareStatement(query);

            insertCounter = 0;
            int s = p_voucherArray.size();
            int insertQueryCounter = 1;
            for (counter = 0; counter < s; counter++) {
                addCount = 0;
                insertQueryCounter = 1;
                vomsVoucherVO = (VomsVoucherVO) p_voucherArray.get(counter);
                psmtPin.setString(1, vomsVoucherVO.getPinNo());
                rsPin = psmtPin.executeQuery();
                psmtPin.clearParameters();
                if (rsPin.next()) {
                    _log.error("insertVouchers() ", " Duplicate pin encountered::Same pin no returned in resultset.counter==" + counter + " For serial No=" + vomsVoucherVO.getSerialNo());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[insertVouchers]", "", "", "", "Duplicate pin number encountered For serial No=" + vomsVoucherVO.getSerialNo());
                    throw new BTSLBaseException(this, "insertVouchers", PretupsErrorCodesI.VOUCHER_PIN_ALREADY_EXIST_IN_DB);
                }

                // verifying for Serail Number start
                psmtSerial.setString(1, vomsVoucherVO.getSerialNo());
                rsSerial = psmtSerial.executeQuery();
                psmtSerial.clearParameters();
                if (rsSerial.next()) {
                    _log.error("insertVouchers() ", " Duplicate Serial Number encountered::Same Serial no returned in resultset.counter==" + counter + "  " + vomsVoucherVO.getSerialNo());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[insertVouchers]", "", "", "", "Duplicate Serial Number encountered For " + vomsVoucherVO.getSerialNo());
                    throw new BTSLBaseException(this, "insertVouchers", PretupsErrorCodesI.VOUCHER_SERIAL_NO_ALREADY_EXIST_IN_DB);
                }

                // verifying for PIN ends
                psmt.clearParameters();
                psmt.setString(insertQueryCounter++, vomsVoucherVO.getSerialNo());
                psmt.setString(insertQueryCounter++, vomsVoucherVO.getPinNo());
                psmt.setString(insertQueryCounter++, vomsVoucherVO.getProductID());
                psmt.setString(insertQueryCounter++, vomsVoucherVO.getProductionLocationCode());
                psmt.setString(insertQueryCounter++, p_vomsBatchVO.getBatchNo());
                psmt.setString(insertQueryCounter++, vomsVoucherVO.getCurrentStatus());
                psmt.setDate(insertQueryCounter++, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getExpiryDate()));
             //   psmt.setDate(insertQueryCounter++, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getExpiryDate()));
                psmt.setTimestamp(insertQueryCounter++, BTSLUtil.getTimestampFromUtilDate(vomsVoucherVO.getCreatedOn()));
                psmt.setDate(insertQueryCounter++, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getCreatedOn()));
                psmt.setString(insertQueryCounter++, vomsVoucherVO.getStatus());
                psmt.setLong(insertQueryCounter++, sequenceNumber);
                psmt.setTimestamp(insertQueryCounter++, BTSLUtil.getTimestampFromUtilDate(vomsVoucherVO.getModifiedOn()));
                psmt.setString(insertQueryCounter++, vomsVoucherVO.getModifiedBy());
                psmt.setString(insertQueryCounter++, vomsVoucherVO.getOneTimeUsage());
                if (p_isDirectEnable) {
                    psmt.setString(insertQueryCounter++, vomsVoucherVO.getEnableBatchNo());
                    psmt.setString(insertQueryCounter++, vomsVoucherVO.getEnableBatchNo());
                    psmt.setString(insertQueryCounter++, vomsVoucherVO.getPreviousStatus());
                }
                addCount = psmt.executeUpdate();
                addCount = BTSLUtil.getInsertCount(addCount); // added to make code compatible with insertion in partitioned table in postgres
                psmt.clearParameters();
                if (addCount <= 0) {
                    _log.error("insertVouchers() ", " Not able to insert record for Serial No" + vomsVoucherVO.getSerialNo());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[insertVouchers]", "", "", "", "Not able to insert record for Serial No" + vomsVoucherVO.getSerialNo());
                    throw new BTSLBaseException(this, "insertVouchers", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
                }
                // Insert details in VOMS_ENABLE_SUMMARY if direct ENABLE of
                // vouchers applicable and if not exists.
                if (!summaryAddedFlag) {
                    if (p_isDirectEnable) {
                        psmtSummary = p_con.prepareStatement(querySelectVoucherENSummary);
                        psmtSummary.clearParameters();
                        psmtSummary.setString(1, vomsVoucherVO.getProductID());
                        psmtSummary.setDate(2, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getCreatedOn()));
                        psmtSummary.setDate(3, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getExpiryDate()));
                        rsSummary = psmtSummary.executeQuery();
                        summaryFoundFlag = rsSummary.next();

                        if (!summaryFoundFlag) {
                            // Add voucher summary
                            psmtSummary1 = p_con.prepareStatement(queryInsertVoucherENSummary);
                            psmtSummary1.setString(1, vomsVoucherVO.getProductID());
                            psmtSummary1.setDate(2, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getCreatedOn()));
                            psmtSummary1.setInt(3, s);
                            psmtSummary1.setDate(4, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getExpiryDate()));

                            addCount = psmtSummary1.executeUpdate();
                            if (addCount <= 0) {
                                _log.error("insertVouchers() ", "Not able to insert record into VOMS_ENABLE_SUMMARY");
                                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[insertVouchers]", "", "", "", "Not able to insert record into VOMS_ENABLE_SUMMARY");
                                throw new BTSLBaseException(this, "insertVouchers", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
                            }
                        } else {
                            // Update voucher summary
                            psmtSummary2 = p_con.prepareStatement(queryUpdateVoucherENSummary);
                            psmtSummary2.setInt(1, s);
                            psmtSummary2.setString(2, vomsVoucherVO.getProductID());
                            psmtSummary2.setDate(3, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getCreatedOn()));
                            psmtSummary2.setDate(4, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getExpiryDate()));

                            updateCount = psmtSummary2.executeUpdate();
                            if (updateCount <= 0) {
                                _log.error("insertVouchers() ", "Not able to update record into VOMS_ENABLE_SUMMARY");
                                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[insertVouchers]", "", "", "", "Not able to update record into VOMS_ENABLE_SUMMARY");
                                throw new BTSLBaseException(this, "insertVouchers", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
                            }
                        }
                    }
                    summaryAddedFlag = true;
                }
                insertCounter = insertCounter + 1;
                sequenceNumber = sequenceNumber + 1;
            }

            if (insertCounter != p_vomsBatchVO.getNoOfVoucher()) {
                _log.error("VomsVoucherDAO[insertVoucher]", " The number of records inserted is not equal to the total number specified by the user");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VomsVoucherDAO[insertVouchers]", "", "", "", "The number of vouchers inserted (" + insertCounter + ") is not equal to the number of total records entered (" + p_vomsBatchVO.getNoOfVoucher() + ")");
                throw new BTSLBaseException(this, "insertVouchers", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_RECORDS_MISMATCH);
            }
            _log.info("insertVouchers() ", " Vouchers generated for " + p_vomsBatchVO.getBatchNo());

            updateCount = VoucherFileUploaderUtil.updateFinalSeqNumber(p_con, Integer.parseInt(BTSLUtil.getFinancialYear()), VOMSI.SEQNUM, VOMSI.ALL, sequenceNumber);

        } catch (BTSLBaseException be) {
            try {
                p_con.rollback();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error("insertVouchers() ", " Getting BaseException :" + be);
            _log.errorTrace(methodName, be);
            throw be;
        } catch (Exception ex) {
            try {
                p_con.rollback();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.debug("insertVouchers()", " Getting Exception :" + ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VomsVoucherDAO[insertVouchers]", "", "", "", "Exception while executing the insertVoucher");
            _log.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, "insertVouchers", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            try {
                try {
                    if (rsBatches != null) {
                        rsBatches.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (rsPin != null) {
                        rsPin.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (rsSerial != null) {
                        rsSerial.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (rsSummary != null) {
                    	rsSummary.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (psmtPin != null) {
                        psmtPin.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (psmtSerial != null) {
                        psmtSerial.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (psmt != null) {
                        psmt.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (psmtSummary != null) {
                    	psmtSummary.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (psmtSummary1 != null) {
                    	psmtSummary1.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (psmtSummary2 != null) {
                    	psmtSummary2.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }

            } catch (Exception _ex) {
                _log.debug("insertVouchers() ", " exception while closing statement");
                _log.errorTrace(methodName, _ex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VomsVoucherDAO[insertVouchers]", "", "", "", "Exception while closing prepared statements in finally of insertVoucher");
                // throw new
                // BTSLBaseException(this,"insertVouchers",PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
            }
        }
        return updateCount;
    }

    /**
     * This method is called at the time of reconciliation to change status of
     * voucher
     * vom_batch_summary
     * 
     * @param p_con
     * @param p_Operation
     * @return int
     * @throws BTSLBaseException
     * @throws Exception
     */
    public int updateVoucherStatus(Connection p_con, String p_Operation, TransferVO p_transferVO, VomsVoucherVO p_vomsVoucherVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateVoucherStatus() ", " Entered for voucher serial no.=" + p_vomsVoucherVO.getSerialNo() + " p_Operation=" + p_Operation);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        final String methodName = "updateVoucherStatus";
        try {
            int i = 1;
            StringBuffer updateQueryBuff = new StringBuffer(" UPDATE voms_vouchers SET   ");
            updateQueryBuff.append(" current_status=?, previous_status=?, status=?, ");
			if ("Fail".equals(p_Operation)) {
            	 updateQueryBuff.append(" subscriber_id=?,CONSUMED_GATEWAY_TYPE=?,CONSUMED_GATEWAY_CODE=?, ");
            }
            updateQueryBuff.append(" modified_by=?, modified_on=?, last_consumed_on=? ");
            updateQueryBuff.append(" WHERE serial_no=? ");
            String updateQuery = updateQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("updateVoucherStatus", "Insert query:" + updateQuery);
            }
            pstmtUpdate = p_con.prepareStatement(updateQuery);

            if ("Success".equals(p_Operation)) {
            	p_Operation = PretupsI.SUCCESS;
                if (VOMSI.VOUCHER_UNPROCESS.equals(p_vomsVoucherVO.getCurrentStatus()) || VOMSI.VOUCHER_REP_ENABLE.equals(p_vomsVoucherVO.getCurrentStatus())) {
                    pstmtUpdate.setString(i++, VOMSI.VOUCHER_USED);
                    pstmtUpdate.setString(i++, p_vomsVoucherVO.getCurrentStatus());
                    pstmtUpdate.setString(i++, VOMSI.VOUCHER_USED);
                } else {
                    throw new BTSLBaseException(this, "updateVoucherStatus", PretupsErrorCodesI.VOMS_RECON_INVALID_VOUCHER_STATUS);
                }
            } else if ("Fail".equals(p_Operation)) {
            	p_Operation = PretupsI.FAIL;
                if (VOMSI.VOUCHER_UNPROCESS.equals(p_vomsVoucherVO.getCurrentStatus()) || VOMSI.VOUCHER_REP_ENABLE.equals(p_vomsVoucherVO.getCurrentStatus())) {
                    pstmtUpdate.setString(i++, VOMSI.VOUCHER_REP_ENABLE);
                    pstmtUpdate.setString(i++, p_vomsVoucherVO.getCurrentStatus());
                    pstmtUpdate.setString(i++, VOMSI.VOUCHER_REP_ENABLE);
					pstmtUpdate.setString(i++, PretupsI.EMPTY);
					pstmtUpdate.setString(i++, PretupsI.EMPTY);
					pstmtUpdate.setString(i++, PretupsI.EMPTY);
                } else {
                    throw new BTSLBaseException(this, "updateVoucherStatus", PretupsErrorCodesI.VOMS_RECON_INVALID_VOUCHER_STATUS);
                }
            }
            pstmtUpdate.setString(i++, p_transferVO.getModifiedBy());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getModifiedOn()));
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_transferVO.getSerialNumber());
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount > 0) {
                updateCount = 0;
                updateCount = updateVoucherAuditStatus(p_con, p_Operation, p_transferVO, p_vomsVoucherVO);
            }
            return updateCount;
        } catch (SQLException sqle) {
            _log.error("updateVoucherStatus", "SQLException " + sqle.getMessage());
            updateCount = 0;
            _log.errorTrace(methodName, sqle);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VomsVoucherDAO[updateTransferDetails]",p_transferVO.getTransferID(),senderVO.getMsisdn(),senderVO.getNetworkCode(),"Exception:"+sqle.getMessage());
            throw new BTSLBaseException(this, "updateVoucherStatus", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("updateVoucherStatus", "Exception " + e.getMessage());
            updateCount = 0;
            _log.errorTrace(methodName, e);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"TransferDAO[updateTransferDetails]",p_transferVO.getTransferID(),senderVO.getMsisdn(),senderVO.getNetworkCode(),"Exception:"+e.getMessage());
            throw new BTSLBaseException(this, "updateVoucherStatus", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("updateVoucherStatus", "Exiting updateCount=" + updateCount);
            }
        }// end of finally
    }

    /**
     * 
     * @param p_con
     * @param p_Operation
     * @param p_transferVO
     * @param p_vomsVoucherVO
     * @return
     * @throws BTSLBaseException
     */
    public int updateVoucherAuditStatus(Connection p_con, String p_Operation, TransferVO p_transferVO, VomsVoucherVO p_vomsVoucherVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateVoucherAuditStatus() ", " Entered for voucher serial no.=" + p_vomsVoucherVO.getSerialNo() + " p_Operation=" + p_Operation);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        final String methodName = "updateVoucherAuditStatus";
        try {
            int i = 1;
            StringBuilder updateQueryBuff = new StringBuilder(" UPDATE voms_voucher_audit SET   ");
            updateQueryBuff.append(" current_status=?, previous_status=?, ");
            updateQueryBuff.append(" modified_by=?, modified_on=? ");
            updateQueryBuff.append(" WHERE serial_no=? ");
            String updateQuery = updateQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("updateVoucherStatus", "Insert query:" + updateQuery);
            }
            pstmtUpdate = p_con.prepareStatement(updateQuery);

            if (PretupsI.SUCCESS.equals(p_Operation)) {
                if (VOMSI.VOUCHER_UNPROCESS.equals(p_vomsVoucherVO.getCurrentStatus()) || VOMSI.VOUCHER_REP_ENABLE.equals(p_vomsVoucherVO.getCurrentStatus())) {
                    pstmtUpdate.setString(i++, VOMSI.VOUCHER_USED);
                    pstmtUpdate.setString(i++, p_vomsVoucherVO.getCurrentStatus());
                } else {
                    throw new BTSLBaseException(this, "updateVoucherStatus", PretupsErrorCodesI.VOMS_RECON_INVALID_VOUCHER_STATUS);
                }
            } else if (PretupsI.FAIL.equals(p_Operation)) {
                if (VOMSI.VOUCHER_UNPROCESS.equals(p_vomsVoucherVO.getCurrentStatus()) || VOMSI.VOUCHER_REP_ENABLE.equals(p_vomsVoucherVO.getCurrentStatus())) {
                    pstmtUpdate.setString(i++, VOMSI.VOUCHER_REP_ENABLE);
                    pstmtUpdate.setString(i++, p_vomsVoucherVO.getCurrentStatus());
                } else {
                    throw new BTSLBaseException(this, "updateVoucherStatus", PretupsErrorCodesI.VOMS_RECON_INVALID_VOUCHER_STATUS);
                }
            }
            else if (PretupsI.TXN_STATUS_AMBIGIOUS.equals(p_Operation)) {
                if (VOMSI.VOUCHER_UNPROCESS.equals(p_vomsVoucherVO.getCurrentStatus()) || VOMSI.VOUCHER_REP_ENABLE.equals(p_vomsVoucherVO.getCurrentStatus())) {
                    pstmtUpdate.setString(i++, VOMSI.VOUCHER_UNPROCESS);
                    pstmtUpdate.setString(i++, p_vomsVoucherVO.getCurrentStatus());
                } else {
                    throw new BTSLBaseException(this, "updateVoucherStatus", PretupsErrorCodesI.VOMS_RECON_INVALID_VOUCHER_STATUS);
                }
            }
            pstmtUpdate.setString(i++, p_transferVO.getModifiedBy());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_transferVO.getSerialNumber());
            updateCount = pstmtUpdate.executeUpdate();
            return updateCount;
        } catch (SQLException sqle) {
            _log.error("updateVoucherAuditStatus", "SQLException " + sqle.getMessage());
            updateCount = 0;
            _log.errorTrace(methodName, sqle);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"TransferDAO[updateTransferDetails]",p_transferVO.getTransferID(),senderVO.getMsisdn(),senderVO.getNetworkCode(),"Exception:"+sqle.getMessage());
            throw new BTSLBaseException(this, "updateVoucherAuditStatus", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("updateVoucherAuditStatus", "Exception " + e.getMessage());
            updateCount = 0;
            _log.errorTrace(methodName, e);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"TransferDAO[updateTransferDetails]",p_transferVO.getTransferID(),senderVO.getMsisdn(),senderVO.getNetworkCode(),"Exception:"+e.getMessage());
            throw new BTSLBaseException(this, "updateVoucherAuditStatus", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("updateVoucherAuditStatus", "Exiting updateCount=" + updateCount);
            }
        }// end of finally
    }

    /**
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @return
     * @throws BTSLBaseException
     */
    public VomsVoucherVO loadVomsVoucherVO(Connection p_con, TransferVO p_c2sTransferVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadVomsVoucherVO", " Entered voucher serial no.=" + p_c2sTransferVO.getSerialNumber());
        }
        PreparedStatement dbPs = null;
        ResultSet rs = null;
        VomsVoucherVO voucherVO = null;
        final String methodName = "loadVomsVoucherVO";
        try {
            StringBuffer sqlSelectBuf = new StringBuffer(" select v.serial_no SERIALNO, v.current_status CURRENTSTAT,  v.previous_status PREVSTAT, v.status  STAT ,PIN_NO ");
            sqlSelectBuf.append(" FROM voms_vouchers v WHERE v.SERIAL_NO=? ");

            if (_log.isDebugEnabled()) {
                _log.debug("loadVomsVoucherVO", "Select Query=" + sqlSelectBuf.toString());
            }
            dbPs = p_con.prepareStatement(sqlSelectBuf.toString());
            dbPs.setString(1, p_c2sTransferVO.getSerialNumber());
            rs = dbPs.executeQuery();

            while (rs.next()) {
                voucherVO = new VomsVoucherVO();
                voucherVO.setSerialNo(rs.getString("SERIALNO"));
                voucherVO.setPreviousStatus(rs.getString("PREVSTAT"));
                voucherVO.setCurrentStatus(rs.getString("CURRENTSTAT"));
                voucherVO.setVoucherStatus(rs.getString("STAT"));
                voucherVO.setPinNo(rs.getString("PIN_NO"));
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadVomsVoucherVO", "After executing the query loadVomsVoucherVO method VomsVoucherVO=" + voucherVO);
            }
            return voucherVO;
        } catch (SQLException sqle) {
            _log.error("loadVomsVoucherVO", "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadVomsVoucherVO]", "", "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadVomsVoucherVO", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadVomsVoucherVO", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadVomsVoucherVO]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadVomsVoucherVO", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.error("loadVomsVoucherVO", " Exception while closing rs ex=" + ex);
            }
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.error("loadVomsVoucherVO", " Exception while closing prepared statement ex=" + ex);
            }
            try {
                _log.debug("loadVomsVoucherVO", " Exiting.. VomsVoucherVO=" + voucherVO);
            } catch (Exception e) {
                _log.error("loadVomsVoucherVO", " Exception while closing rs ex=" + e);
            }
            ;
        }
    }

    public VomsVoucherVO loadVomsVoucherVOCons(Connection p_con, P2PTransferVO p_c2sTransferVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadVomsVoucherVOCons", " Entered voucher serial no.=" + p_c2sTransferVO.getSerialNumber());
        }
        PreparedStatement dbPs = null;
        ResultSet rs = null;
        VomsVoucherVO voucherVO = null;
        final String methodName = "loadVomsVoucherVOCons";
        try {
            StringBuffer sqlSelectBuf = new StringBuffer(" select v.serial_no SERIALNO, v.current_status CURRENTSTAT,  v.previous_status PREVSTAT, v.status  STAT ,PIN_NO ");
            sqlSelectBuf.append(" FROM voms_vouchers v WHERE v.SERIAL_NO=? ");
            if(VOMSI.VOUCHER_SEGMENT_LOCAL.equals(p_c2sTransferVO.getVoucherSegment() ))
            	sqlSelectBuf.append("and v.production_network_code = ? ");
            if (_log.isDebugEnabled()) {
                _log.debug("loadVomsVoucherVO", "Select Query=" + sqlSelectBuf.toString());
            }
            dbPs = p_con.prepareStatement(sqlSelectBuf.toString());
            dbPs.setString(1, p_c2sTransferVO.getSerialNumber());
            if(VOMSI.VOUCHER_SEGMENT_LOCAL.equals(p_c2sTransferVO.getVoucherSegment() ))
            dbPs.setString(2, p_c2sTransferVO.getNetworkCode());
            rs = dbPs.executeQuery();

            while (rs.next()) {
                voucherVO = new VomsVoucherVO();
                voucherVO.setSerialNo(rs.getString("SERIALNO"));
                voucherVO.setPreviousStatus(rs.getString("PREVSTAT"));
                voucherVO.setCurrentStatus(rs.getString("CURRENTSTAT"));
                voucherVO.setVoucherStatus(rs.getString("STAT"));
                voucherVO.setPinNo(rs.getString("PIN_NO"));
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadVomsVoucherVOCons", "After executing the query loadVomsVoucherVO method VomsVoucherVO=" + voucherVO);
            }
            return voucherVO;
        } catch (SQLException sqle) {
            _log.error("loadVomsVoucherVOCons", "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadVomsVoucherVOCons]", "", "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadVomsVoucherVOCons", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadVomsVoucherVOCons", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadVomsVoucherVOCons]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadVomsVoucherVOCons", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.error("loadVomsVoucherVO", " Exception while closing rs ex=" + ex);
            }
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.error("loadVomsVoucherVO", " Exception while closing prepared statement ex=" + ex);
            }
            try {
                _log.debug("loadVomsVoucherVO", " Exiting.. VomsVoucherVO=" + voucherVO);
            } catch (Exception e) {
                _log.error("loadVomsVoucherVO", " Exception while closing rs ex=" + e);
            }
            ;
        }
    }
    /**
     * This method will select the productID based on the MRP, because in system
     * there is only
     * one productID for one MRP.
     * 
     * @param p_con
     *            of Connection type
     * @param faceValue
     *            of int type
     * @return returns String productID
     * @exception SQLException
     * @exception BTSLBaseException
     */
    // As this method is used in operator specific files so not moved to
    // refactored files
    public String loadProductIDFromMRP(Connection p_con, int faceValue) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadProductIDFromMRP", " Entered.. p_mrp=" + faceValue);
        }
        PreparedStatement dbPs = null;
        ResultSet rs = null;
        String productID = null;
        final String methodName = "loadProductIDFromMRP";
        String sqlSelect = null;
        try {
            sqlSelect = ("select PRODUCT_ID from VOMS_PRODUCTS where MRP = ? and status=? ");

            if (_log.isDebugEnabled()) {
                _log.debug("loadProductIDFromMRP", "Select Query=" + sqlSelect);
            }
            dbPs = p_con.prepareStatement(sqlSelect);
            dbPs.setInt(1, faceValue);
            dbPs.setString(2, VOMSI.VOMS_STATUS_ACTIVE);
            rs = dbPs.executeQuery();
            while (rs.next()) {
                productID = rs.getString("product_id");
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadProductIDFromMRP", "After executing the query loadProductIDFromMRP method productID=" + productID);
            }

            return productID;
        } catch (SQLException sqle) {
            _log.error("loadProductIDFromMRP", "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadActiveProfiles]", "", "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadProductIDFromMRP", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadProductIDFromMRP", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadActiveProfiles]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadProductIDFromMRP", "error.general.sql.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.error("loadProductIDFromMRP", " Exception while closing rs ex=" + ex);
            }

            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.error("loadProductIDFromMRP", " Exception while closing prepared statement ex=" + ex);
            }
        }
    }

    // As this method is used in operator specific files so not moved to
    // refactored files
    public boolean checkDuplicateSerialNO(Connection p_con, VomsSerialUploadCheckVO p_serialUploadCheckVO) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("checkDuplicateSerialNO", "Entered in checkDuplicateSerialNO");
        }

        
        boolean flag = false;
        final String methodName = "checkDuplicateSerialNO";
        String sqlSelect = null;
        try {
            sqlSelect = ("SELECT count(1) as Count FROM VOUCHER_SRL_UPLOAD_CHK WHERE (? between start_srl_no and end_srl_no) or (? between start_srl_no and end_srl_no)");

            if (_log.isDebugEnabled()) {
                _log.debug("checkDuplicateSerialNO", "Select Query=" + sqlSelect);
            }
            try(PreparedStatement pst = p_con.prepareStatement(sqlSelect);)
            {
            pst.setString(1, p_serialUploadCheckVO.getStartSerialNo());
            pst.setString(2, p_serialUploadCheckVO.getEndSerialNO());

           try(ResultSet resultset = pst.executeQuery();)
           {
            while (resultset.next()) {
                int count = resultset.getInt("Count");
                if (count != 0) {
                    flag = true;
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug("checkDuplicateSerialNO", "After executing the query checkDuplicateSerialNO. Status is" + flag);
            }
        }
            }
        }catch (SQLException sqle) {
            _log.error("checkDuplicateSerialNO", "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[checkDuplicateSerialNO]", "", "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "checkDuplicateSerialNO", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("checkDuplicateSerialNO", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[checkDuplicateSerialNO]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "checkDuplicateSerialNO", "error.general.sql.processing");
        }// end of catch
        return flag;
    }

    // As this method is used in operator specific files so not moved to
    // refactored files
    public int insertFileValues(Connection p_con, VomsSerialUploadCheckVO p_serialUploadCheckVO) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("insertFileValues ", "  Entered p_serialUploadCheckVO =" + p_serialUploadCheckVO);
        }
        int addCount = 0;
        PreparedStatement pst = null;
        final String methodName = "insertFileValues";
        String sqlSelect = null;

        try {
            sqlSelect = ("INSERT INTO VOUCHER_SRL_UPLOAD_CHK values(?,?,?,?,?,?,?,?) ");
            if (_log.isDebugEnabled()) {
                _log.debug("insertFileValues", "Select Query=" + sqlSelect);
            }
            pst = p_con.prepareStatement(sqlSelect);
            pst.setString(1, p_serialUploadCheckVO.getStartSerialNo());
            pst.setString(2, p_serialUploadCheckVO.getEndSerialNO());
            pst.setInt(3, p_serialUploadCheckVO.getDenomination());
            pst.setDate(4, BTSLUtil.getSQLDateFromUtilDate(p_serialUploadCheckVO.getExpiryDate()));
            pst.setString(5, p_serialUploadCheckVO.getFileName());
            pst.setDate(6, BTSLUtil.getSQLDateFromUtilDate(p_serialUploadCheckVO.getUploadDate()));
            pst.setString(7, p_serialUploadCheckVO.getCreatedBy());
            pst.setDate(8, BTSLUtil.getSQLDateFromUtilDate(p_serialUploadCheckVO.getCreatedOn()));

            addCount = pst.executeUpdate();

            if (addCount <= 0) {
                _log.error("insertFileValues() ", " Not able to insert record for Serial No" + p_serialUploadCheckVO.getStartSerialNo());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[insertFileValues]", "", "", "", "Not able to insert record for Serial No" + p_serialUploadCheckVO.getStartSerialNo());
                throw new BTSLBaseException(this, "insertFileValues", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
            }

        }
        //
        catch (BTSLBaseException be) {
            try {
                p_con.rollback();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error("insertFileValues() ", " Getting BaseException :" + be);
            throw be;
        } catch (Exception ex) {
            try {
                p_con.rollback();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.debug("insertFileValues()", " Getting Exception :" + ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VomsVoucherDAO[insertFileValues]", "", "", "", "Exception while executing the insertFileValues");
            _log.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, "insertFileValues", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        }
        return addCount;
    }

    /**
     * This method inserts vouchers in VOMS_VOUCHERS Table
     * 
     * @param p_con
     * @param p_batchVO
     * @param p_voucherArray
     * @return int
     * @throws BTSLBaseException
     */
    // As this method is used in operator specific files so not moved to
    // refactored files
    public int insertVouchers(Connection p_con, VomsBatchVO p_vomsBatchVO, ArrayList p_voucherArray, boolean p_isDirectEnable, boolean p_dublicatePINAllowed) throws BTSLBaseException {
    	final String methodName = "insertVouchers";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "  Entered p_voucherArray =" + p_voucherArray.size() + " " + p_vomsBatchVO.getProductID() + " p_isDirectEnable=" + p_isDirectEnable + ",p_dublicatePINAllowed=" + p_dublicatePINAllowed);
        }
        int addCount = 0;
        String query;
        int counter = 0;
        ResultSet rsBatches = null;
        PreparedStatement psmt = null;
        String queryPin = null;
        PreparedStatement psmtPin = null;
        ResultSet rsPin = null;
        PreparedStatement psmtSerial = null;
        ResultSet rsSerial = null;
        String querySerial = null;
        long sequenceNumber = 0;
        int insertCounter = 0;
        int updateCount = 0;
        VomsVoucherVO vomsVoucherVO = null;
        
        String queryInsertVoucherENSummary;
		String querySelectVoucherENSummary;
		String queryUpdateVoucherENSummary;
		
		PreparedStatement psmtSummarySelect = null;
		PreparedStatement psmtSummaryUpdate = null;
		PreparedStatement psmtSummaryInsert = null;
		ResultSet rsSummary = null;
		boolean summaryAddedFlag = false;
		boolean summaryFoundFlag = false;
        try {
            sequenceNumber = VoucherFileUploaderUtil.loadNextSeqNumber(p_con, Integer.parseInt(BTSLUtil.getFinancialYear()), VOMSI.SEQNUM, VOMSI.ALL);

            _log.info("insertVouchers() ", " For Batch" + p_vomsBatchVO.getBatchNo() + " No of pins required " + p_vomsBatchVO.getNoOfVoucher());

            querySerial = " SELECT serial_no FROM voms_vouchers WHERE SERIAL_NO =?";
            psmtSerial = p_con.prepareStatement(querySerial);

            queryPin = "SELECT pin_no FROM voms_vouchers WHERE pin_no=?";
            psmtPin = p_con.prepareStatement(queryPin);

            queryInsertVoucherENSummary = "INSERT INTO VOMS_ENABLE_SUMMARY ( PRODUCT_ID, CREATED_DATE, VOUCHER_COUNT,EXPIRY_DATE ) VALUES (?,?,?,?)";
			querySelectVoucherENSummary = "SELECT VOUCHER_COUNT FROM VOMS_ENABLE_SUMMARY WHERE PRODUCT_ID = ? AND CREATED_DATE = ? AND EXPIRY_DATE = ?";
			queryUpdateVoucherENSummary = "UPDATE VOMS_ENABLE_SUMMARY SET VOUCHER_COUNT = (VOUCHER_COUNT + ?) WHERE PRODUCT_ID = ? AND CREATED_DATE = ? AND EXPIRY_DATE = ?";
			if(_log.isDebugEnabled())_log.debug("insertVouchers() queryInsertVoucherENSummary=",queryInsertVoucherENSummary);
			if(_log.isDebugEnabled())_log.debug("insertVouchers() querySelectVoucherENSummary=",querySelectVoucherENSummary);
			if(_log.isDebugEnabled())_log.debug("insertVouchers() queryUpdateVoucherENSummary=",queryUpdateVoucherENSummary);
			
			psmtSummarySelect = p_con.prepareStatement(querySelectVoucherENSummary);			
			psmtSummaryInsert = p_con.prepareStatement(queryInsertVoucherENSummary);			
			psmtSummaryUpdate = p_con.prepareStatement(queryUpdateVoucherENSummary);			
            if (p_isDirectEnable) {
                query = "INSERT INTO voms_vouchers(serial_no,pin_no,product_id,production_network_code,generation_batch_no,current_status,expiry_date,created_on,status,seq_no,modified_on,modified_by,ONE_TIME_USAGE,last_batch_no,enable_batch_no,previous_status)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            } else {
                query = "INSERT INTO voms_vouchers(serial_no,pin_no,product_id,production_network_code,generation_batch_no,current_status,expiry_date,created_on,status,seq_no,modified_on,modified_by,ONE_TIME_USAGE)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            }
            psmt = p_con.prepareStatement(query);

            insertCounter = 0;
            int s = p_voucherArray.size();
            int insertQueryCounter = 1;
            for (counter = 0; counter < s; counter++) {
                addCount = 0;
                insertQueryCounter = 1;
                vomsVoucherVO = (VomsVoucherVO) p_voucherArray.get(counter);
                if (!p_dublicatePINAllowed) {
                    psmtPin.setString(1, vomsVoucherVO.getPinNo());
                    rsPin = psmtPin.executeQuery();
                    psmtPin.clearParameters();
                    if (rsPin.next()) {
                        _log.error("insertVouchers() ", " Duplicate pin encountered::Same pin no returned in resultset.counter==" + counter + " For serial No=" + vomsVoucherVO.getSerialNo());
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[insertVouchers]", "", "", "", "Duplicate pin number encountered For serial No=" + vomsVoucherVO.getSerialNo());
                        throw new BTSLBaseException(this, "insertVouchers", PretupsErrorCodesI.VOUCHER_PIN_ALREADY_EXIST_IN_DB);
                    }
                }
                // verifying for Serail Number start
                psmtSerial.setString(1, vomsVoucherVO.getSerialNo());
                rsSerial = psmtSerial.executeQuery();
                psmtSerial.clearParameters();
                if (rsSerial.next()) {
                    _log.error("insertVouchers() ", " Duplicate Serial Number encountered::Same Serial no returned in resultset.counter==" + counter + "  " + vomsVoucherVO.getSerialNo());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[insertVouchers]", "", "", "", "Duplicate Serial Number encountered For " + vomsVoucherVO.getSerialNo());
                    throw new BTSLBaseException(this, "insertVouchers", PretupsErrorCodesI.VOUCHER_SERIAL_NO_ALREADY_EXIST_IN_DB);
                }

                // verifying for PIN ends
                psmt.clearParameters();
                psmt.setString(insertQueryCounter++, vomsVoucherVO.getSerialNo());
                psmt.setString(insertQueryCounter++, vomsVoucherVO.getPinNo());
                psmt.setString(insertQueryCounter++, vomsVoucherVO.getProductID());
                psmt.setString(insertQueryCounter++, vomsVoucherVO.getProductionLocationCode());
                psmt.setString(insertQueryCounter++, p_vomsBatchVO.getBatchNo());
                psmt.setString(insertQueryCounter++, vomsVoucherVO.getCurrentStatus());
                psmt.setDate(insertQueryCounter++, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getExpiryDate()));
                psmt.setTimestamp(insertQueryCounter++, BTSLUtil.getTimestampFromUtilDate(vomsVoucherVO.getCreatedOn()));
                psmt.setString(insertQueryCounter++, vomsVoucherVO.getStatus());
                psmt.setLong(insertQueryCounter++, sequenceNumber);
                psmt.setTimestamp(insertQueryCounter++, BTSLUtil.getTimestampFromUtilDate(vomsVoucherVO.getModifiedOn()));
                psmt.setString(insertQueryCounter++, vomsVoucherVO.getModifiedBy());
                psmt.setString(insertQueryCounter++, vomsVoucherVO.getOneTimeUsage());
                if (p_isDirectEnable) {
                    psmt.setString(insertQueryCounter++, vomsVoucherVO.getEnableBatchNo());
                    psmt.setString(insertQueryCounter++, vomsVoucherVO.getEnableBatchNo());
                    psmt.setString(insertQueryCounter++, vomsVoucherVO.getPreviousStatus());
                }
                addCount = psmt.executeUpdate();
                addCount = BTSLUtil.getInsertCount(addCount); // added to make code compatible with insertion in partitioned table in postgres
                psmt.clearParameters();
                if (addCount <= 0) {
                    _log.error("insertVouchers() ", " Not able to insert record for Serial No" + vomsVoucherVO.getSerialNo());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[insertVouchers]", "", "", "", "Not able to insert record for Serial No" + vomsVoucherVO.getSerialNo());
                    throw new BTSLBaseException(this, "insertVouchers", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
                }
                
//            	Insert details in VOMS_ENABLE_SUMMARY if direct ENABLE of vouchers applicable and if not exists.
				if (!summaryAddedFlag) 
				{
				    if (p_isDirectEnable)
				    {
				    	psmtSummarySelect.clearParameters();
				    	psmtSummarySelect.setString(1, vomsVoucherVO.getProductID());
				    	psmtSummarySelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getCreatedOn()));
				    	psmtSummarySelect.setDate(3, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getExpiryDate()));
				        rsSummary = psmtSummarySelect.executeQuery();
				        summaryFoundFlag = rsSummary.next(); 
				  				        
				        if (!summaryFoundFlag) 
				        {
				        	//	Add voucher summary
				        	psmtSummaryInsert.clearParameters();
				        	psmtSummaryInsert.setString(1, vomsVoucherVO.getProductID());
				        	psmtSummaryInsert.setDate(2, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getCreatedOn()));
				        	psmtSummaryInsert.setInt(3, s);
				        	psmtSummaryInsert.setDate(4, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getExpiryDate()));
				            addCount = psmtSummaryInsert.executeUpdate();
				            if(addCount<=0)
							{
								_log.error("insertVouchers() ","Not able to insert record into VOMS_ENABLE_SUMMARY");
								EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VomsVoucherDAO[insertVouchers]","","","","Not able to insert record into VOMS_ENABLE_SUMMARY");
								throw new BTSLBaseException(this,"insertVouchers",PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
							}
				        }
				        else 
				        {
				        	//  Update voucher summary				            
				            psmtSummaryUpdate.clearParameters();
				            psmtSummaryUpdate.setInt(1, s);
				            psmtSummaryUpdate.setString(2, vomsVoucherVO.getProductID());
				            psmtSummaryUpdate.setDate(3, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getCreatedOn()));
				            psmtSummaryUpdate.setDate(4, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getExpiryDate()));
				            updateCount = psmtSummaryUpdate.executeUpdate();
				            if(updateCount<=0)
							{
								_log.error("insertVouchers() ","Not able to update record into VOMS_ENABLE_SUMMARY");
								EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VomsVoucherDAO[insertVouchers]","","","","Not able to update record into VOMS_ENABLE_SUMMARY");
								throw new BTSLBaseException(this,"insertVouchers",PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
							}
				        }
				    }
				    summaryAddedFlag = true;
				}
                insertCounter = insertCounter + 1;
                sequenceNumber = sequenceNumber + 1;
            }

            if (insertCounter != p_vomsBatchVO.getNoOfVoucher()) {
                _log.error("VomsVoucherDAO[insertVoucher]", " The number of records inserted is not equal to the total number specified by the user");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VomsVoucherDAO[insertVouchers]", "", "", "", "The number of vouchers inserted (" + insertCounter + ") is not equal to the number of total records entered (" + p_vomsBatchVO.getNoOfVoucher() + ")");
                throw new BTSLBaseException(this, "insertVouchers", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_RECORDS_MISMATCH);
            }
            _log.info("insertVouchers() ", " Vouchers generated for " + p_vomsBatchVO.getBatchNo());

            updateCount = VoucherFileUploaderUtil.updateFinalSeqNumber(p_con, Integer.parseInt(BTSLUtil.getFinancialYear()), VOMSI.SEQNUM, VOMSI.ALL, sequenceNumber);

        } catch (BTSLBaseException be) {
            try {
                p_con.rollback();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error("insertVouchers() ", " Getting BaseException :" + be);
            throw be;
        } catch (Exception ex) {
            try {
                p_con.rollback();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.debug("insertVouchers()", " Getting Exception :" + ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VomsVoucherDAO[insertVouchers]", "", "", "", "Exception while executing the insertVoucher");
            _log.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, "insertVouchers", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            try {
                try {
                    if (rsBatches != null) {
                        rsBatches.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                // try {if (rsPin != null)rsPin.close();} catch (Exception e) {}
                try {
                    if (rsSerial != null) {
                        rsSerial.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (rsPin != null) {
                    	rsPin.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                // try {if (psmtPin != null)psmtPin.close();} catch (Exception
                // e) {}
                try {
                    if (psmtSerial != null) {
                        psmtSerial.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (psmt != null) {
                        psmt.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (psmtPin != null) {
                    	psmtPin.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {if (psmtSummaryInsert != null)psmtSummaryInsert.close();} catch (Exception e) {_log.errorTrace(methodName, e);}
                try {if (psmtSummarySelect != null)psmtSummarySelect.close();} catch (Exception e) {_log.errorTrace(methodName, e);}
                try {if (psmtSummaryUpdate != null)psmtSummaryUpdate.close();} catch (Exception e) {_log.errorTrace(methodName, e);}

            } catch (Exception _ex) {
                _log.debug("insertVouchers() ", " exception while closing statement");
                _log.errorTrace(methodName, _ex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VomsVoucherDAO[insertVouchers]", "", "", "", "Exception while closing prepared statements in finally of insertVoucher");
                // throw new
                // BTSLBaseException(this,"insertVouchers",PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
            }
        }
        return updateCount;
    }

    /**
     * This method inserts vouchers in VOMS_VOUCHERS Table
     * 
     * @param p_con
     * @param p_batchVO
     * @param p_voucherArray
     * @return int
     * @throws BTSLBaseException
     */
    public int insertVouchers_new(Connection p_con, VomsBatchVO p_vomsBatchVO, ArrayList p_voucherArray, boolean p_isDirectEnable) throws BTSLBaseException {
        final String methodName = "VomsVoucherDAO[insertVouchers_new]";

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "  Entered p_voucherArray =" + p_voucherArray.size() + " " + p_vomsBatchVO.getProductID() + " p_isDirectEnable=" + p_isDirectEnable);
        }
        int addCount = 0;
        String query;
        StringBuilder queryBuilder;
        int counter = 0;
        ResultSet rsBatches = null;
        PreparedStatement psmt = null;
        String queryPin = null;
        PreparedStatement psmtPin = null;
        ResultSet rsPin = null;
        PreparedStatement psmtSerial = null;
        ResultSet rsSerial = null;
        String querySerial = null;
        long sequenceNumber = 0;
        int[] insertCounter = null;
        int updateCount = 0;
        VomsVoucherVO vomsVoucherVO = null;
        String queryInsertVoucherENSummary;
        String querySelectVoucherENSummary;
        String queryUpdateVoucherENSummary;
        PreparedStatement psmtSummary = null;
        ResultSet rsSummary = null;
        boolean summaryAddedFlag = false;
        boolean summaryFoundFlag = false;

        try {
            sequenceNumber = VoucherFileUploaderUtil.loadNextSeqNumber(p_con, Integer.parseInt(BTSLUtil.getFinancialYear()), VOMSI.SEQNUM, VOMSI.ALL);
            queryBuilder=new StringBuilder();
            _log.info(methodName, " For Batch" + p_vomsBatchVO.getBatchNo() + " No of pins required " + p_vomsBatchVO.getNoOfVoucher());
            final  String startHashValue = Constants.getProperty("STARTING_VALUE");
            int intStartHashValue = Integer.parseInt(startHashValue);
            querySerial = " SELECT serial_no FROM voms_vouchers WHERE SERIAL_NO =?";
            if (_log.isDebugEnabled()) {
                _log.debug(methodName + " querySerial=", querySerial);
            }
            psmtSerial = p_con.prepareStatement(querySerial);

            queryPin = "SELECT pin_no FROM voms_vouchers WHERE pin_no=?";
            if (_log.isDebugEnabled()) {
                _log.debug(methodName + " queryPIN=", queryPin);
            }
            psmtPin = p_con.prepareStatement(queryPin);

            if (p_isDirectEnable) {
            	queryBuilder.append("INSERT INTO voms_vouchers(serial_no,pin_no,product_id,production_network_code,generation_batch_no,current_status,expiry_date,CONSUME_BEFORE,ATTEMPT_USED,USER_NETWORK_CODE,LAST_BATCH_NO,created_on,created_date, status,seq_no,modified_on,modified_by,ONE_TIME_USAGE,MRP,VALIDITY,TALKTIME,enable_batch_no,previous_status") ;
            	if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE))).booleanValue()){
            		queryBuilder.append(",SEQUENCE_ID");
            	}
            	queryBuilder.append(")");
            	queryBuilder.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
            	if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE))).booleanValue()){
            		queryBuilder.append(",?");
            	}
            	queryBuilder.append(")");
            } else {
            	queryBuilder.append("INSERT INTO voms_vouchers(serial_no,pin_no,product_id,production_network_code,generation_batch_no,current_status,expiry_date,CONSUME_BEFORE,ATTEMPT_USED,USER_NETWORK_CODE,LAST_BATCH_NO,created_on,created_date, status,seq_no,modified_on,modified_by,ONE_TIME_USAGE,MRP,VALIDITY,TALKTIME");
            	if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE))).booleanValue()){
            		queryBuilder.append(",SEQUENCE_ID");
            	}
            	queryBuilder.append(")");
            	queryBuilder.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
            	if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE))).booleanValue()){
            		queryBuilder.append(",?");
            	}
            	queryBuilder.append(")");
            }

            query=queryBuilder.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName + " query=", query);
            }

            psmt = p_con.prepareStatement(query);

            int s = p_voucherArray.size();
            int insertQueryCounter = 1;
            for (counter = 0; counter < s; counter++) {

                insertQueryCounter = 1;
                vomsVoucherVO = (VomsVoucherVO) p_voucherArray.get(counter);
                psmtPin.setString(1, vomsVoucherVO.getPinNo());
                rsPin = psmtPin.executeQuery();
                psmtPin.clearParameters();
                if (rsPin.next()) {
                    _log.error(methodName, " Duplicate pin encountered::Same pin no returned in resultset.counter==" + counter + " For serial No=" + vomsVoucherVO.getSerialNo());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "", "Duplicate pin number encountered For serial No=" + vomsVoucherVO.getSerialNo());
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_PIN_ALREADY_EXIST_IN_DB);
                }

                // verifying for Serail Number start
                psmtSerial.setString(1, vomsVoucherVO.getSerialNo());
                rsSerial = psmtSerial.executeQuery();
                psmtSerial.clearParameters();
                if (rsSerial.next()) {
                    _log.error(methodName, " Duplicate Serial Number encountered::Same Serial no returned in resultset.counter==" + counter + "  " + vomsVoucherVO.getSerialNo());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "", "Duplicate Serial Number encountered For " + vomsVoucherVO.getSerialNo());
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_SERIAL_NO_ALREADY_EXIST_IN_DB);
                }

                // verifying for PIN ends
                psmt.clearParameters();
                psmt.setString(insertQueryCounter++, vomsVoucherVO.getSerialNo());
                psmt.setString(insertQueryCounter++, vomsVoucherVO.getPinNo());
                psmt.setString(insertQueryCounter++, p_vomsBatchVO.getProductID());
                psmt.setString(insertQueryCounter++, p_vomsBatchVO.get_NetworkCode());
                psmt.setString(insertQueryCounter++, p_vomsBatchVO.getBatchNo());
                if (p_isDirectEnable) {
                	psmt.setString(insertQueryCounter++, VOMSI.VOUCHER_ENABLE);
                }
                else{
                psmt.setString(insertQueryCounter++, p_vomsBatchVO.getStatus());
                }
                psmt.setDate(insertQueryCounter++, BTSLUtil.getSQLDateFromUtilDate(p_vomsBatchVO.getExpiryDate()));
                psmt.setDate(insertQueryCounter++, BTSLUtil.getSQLDateFromUtilDate(p_vomsBatchVO.getExpiryDate()));
               //psmt.setString(insertQueryCounter++, "0");
                psmt.setFloat(insertQueryCounter++, 0);
                psmt.setString(insertQueryCounter++, p_vomsBatchVO.get_NetworkCode());
                psmt.setString(insertQueryCounter++, p_vomsBatchVO.getBatchNo());
                psmt.setTimestamp(insertQueryCounter++, BTSLUtil.getTimestampFromUtilDate(p_vomsBatchVO.getCreatedOn()));
                psmt.setDate(insertQueryCounter++, BTSLUtil.getSQLDateFromUtilDate(p_vomsBatchVO.getCreatedOn()));
                if (p_isDirectEnable) {
                	psmt.setString(insertQueryCounter++, VOMSI.VOUCHER_ENABLE);
                }
                else{
                psmt.setString(insertQueryCounter++, p_vomsBatchVO.getStatus());
                }
                psmt.setLong(insertQueryCounter++, sequenceNumber);
                psmt.setTimestamp(insertQueryCounter++, BTSLUtil.getTimestampFromUtilDate(p_vomsBatchVO.getModifiedOn()));
                psmt.setString(insertQueryCounter++, p_vomsBatchVO.getModifiedBy());
                psmt.setString(insertQueryCounter++, p_vomsBatchVO.getOneTimeUsage());
                //psmt.setString(insertQueryCounter++, p_vomsBatchVO.getMrp());
                psmt.setInt(insertQueryCounter++, Integer.parseInt(p_vomsBatchVO.getMrp()));
                psmt.setInt(insertQueryCounter++, p_vomsBatchVO.getValidity());
                psmt.setInt(insertQueryCounter++, p_vomsBatchVO.getTalktime());
                if (p_isDirectEnable) {
                    psmt.setString(insertQueryCounter++, vomsVoucherVO.getEnableBatchNo());
                    psmt.setString(insertQueryCounter++, vomsVoucherVO.getPreviousStatus());
                }
               
                if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE))).booleanValue()){
                	psmt.setInt(insertQueryCounter++,BTSLUtil.getUniqueInteger( vomsVoucherVO.getPinNo(), intStartHashValue, intStartHashValue+((Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ID_RANGE)).intValue()));
            	}
                psmt.addBatch();
                sequenceNumber = sequenceNumber + 1;
            }
            insertCounter = psmt.executeBatch();

            if (insertCounter == null || insertCounter.length < p_vomsBatchVO.getNoOfVoucher()) {
                _log.error(methodName, " The number of records inserted is not equal to the total number specified by the user");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, methodName, "", "", "", "The number of vouchers inserted (" + insertCounter + ") is not equal to the number of total records entered (" + p_vomsBatchVO.getNoOfVoucher() + ")");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_RECORDS_MISMATCH);
            }

            _log.info(methodName, " Vouchers generated for " + p_vomsBatchVO.getBatchNo());

            updateCount = VoucherFileUploaderUtil.updateFinalSeqNumber(p_con, Integer.parseInt(BTSLUtil.getFinancialYear()), VOMSI.SEQNUM, VOMSI.ALL, sequenceNumber);

        } catch (BTSLBaseException be) {
            try {
                p_con.rollback();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, " Getting BaseException :" + be);
            throw be;
        } catch (Exception ex) {
            try {
                p_con.rollback();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.debug(methodName, " Getting Exception :" + ex);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "", "Exception while executing the insertVoucher");
            _log.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            try {
                try {
                    if (rsBatches != null) {
                        rsBatches.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (rsPin != null) {
                        rsPin.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (rsSerial != null) {
                        rsSerial.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (psmtPin != null) {
                        psmtPin.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (psmtSerial != null) {
                        psmtSerial.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (psmt != null) {
                        psmt.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }

            } catch (Exception _ex) {
                _log.debug(methodName, " exception while closing statement");
                _log.errorTrace(methodName, _ex);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "", "Exception while closing prepared statements in finally of insertVoucher");
                // throw new
                // BTSLBaseException(this,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
            }
        }
        return updateCount;
    }
    
    /**
     * Method: isProductNameExists
	 * This method is used for checking the existance of a product
	 * 
	 * @author amit.singh
	 * @param p_con java.sql.Connection
	 * @param p_productName productName
	 * @return existFlag boolean 
	 * @throws  BTSLBaseException
     */
	public boolean isVoucherExistsforMrp(Connection p_con,VomsCategoryVO  p_vomsCategoryVO,String v_product_Id) throws BTSLBaseException
	{
		final String methodName = "isVoucherExistsforMrp";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered: p_mrp=" + p_vomsCategoryVO.getMrp()+"v_product_Id="+v_product_Id);
		}
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean existFlag = false;
		
		String sqlSelect = "select 1 from VOMS_VOUCHERS where MRP=? and CURRENT_STATUS in (?,?) and product_id=?";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}
		
		try
		{
			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setLong(1, PretupsBL.getSystemAmount(p_vomsCategoryVO.getMrp()));
			pstmt.setString(2, VOMSI.BATCH_GENERATED);
			pstmt.setString(3, VOMSI.BATCH_ENABLED);
			pstmt.setString(4, v_product_Id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				existFlag = true;
			}
		}
		catch (SQLException sqe)
		{
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VomsVoucherDAO[isVoucherExistsforMrp]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
		catch (Exception ex)
		{

			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VomsVoucherDAO[isVoucherExistsforMrp]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}
		finally
		{
			try{if (rs != null){rs.close();}} catch (Exception e){_log.errorTrace(methodName, e);}
			try{if (pstmt != null){pstmt.close();}} catch (Exception e){_log.errorTrace(methodName, e);}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting: existFlag=" + existFlag);
			}
		}
		return existFlag;
	}

	/**
     * 
     * @param p_con
     * @param requestedVoucherVO
     * @param fromVoucherVO
     */
    public synchronized long changeVoucherStatus(Connection p_con,VomsBatchVO batchVO,VomsVoucherVO requestedVoucherVO)throws BTSLBaseException
    {
    	final String methodName = "changeVoucherStatus";
    	if(_log.isDebugEnabled())_log.debug(methodName," Entered for requestedVoucherVO No:" +requestedVoucherVO +" batchVO No:" +batchVO);
    	PreparedStatement pstmtUpdate=null;
    	ResultSet rs = null;
    	long count=0;
    	try 
    	{
    		int i=1;
    		StringBuffer updateQueryBuff =new StringBuffer(" update VOMS_VOUCHERS  set CURRENT_STATUS=? ,PREVIOUS_STATUS=CURRENT_STATUS,STATUS=?,INFO1=?,ENABLE_BATCH_NO=?,LAST_BATCH_NO=?,MODIFIED_ON=?,MODIFIED_BY=? "); 
    		updateQueryBuff.append(" where SERIAL_NO >=? and SERIAL_NO <=? and PRODUCT_ID=? and CURRENT_STATUS=?"); 

    		String updateQuery=updateQueryBuff.toString();
    		if(_log.isDebugEnabled())_log.debug(methodName,"select query:"+updateQuery );
    		pstmtUpdate = p_con.prepareStatement(updateQuery);
    		pstmtUpdate.setString(i++,requestedVoucherVO.getStatus());
    		pstmtUpdate.setString(i++,requestedVoucherVO.getStatus());
    		pstmtUpdate.setString(i++,requestedVoucherVO.getInfo1());
    		pstmtUpdate.setString(i++,requestedVoucherVO.getEnableBatchNo());
    	
    		pstmtUpdate.setString(i++,batchVO.getBatchNo());
    		pstmtUpdate.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(batchVO.getModifiedOn()));
    		pstmtUpdate.setString(i++,batchVO.getModifiedBy());
    		pstmtUpdate.setString(i++,requestedVoucherVO.get_fromSerialNo());
    		pstmtUpdate.setString(i++,requestedVoucherVO.getToSerialNo());
    		pstmtUpdate.setString(i++,batchVO.getProductID());
    		pstmtUpdate.setString(i++,batchVO.getReferenceType());
    		count= pstmtUpdate.executeUpdate();
    		
    	}
    	catch (SQLException sqle)
    	{
    		_log.error(methodName,"SQLException "+sqle.getMessage());
    		count=0;
    	}//end of catch
    	catch (Exception e)
    	{
    		_log.error(methodName,"Exception "+e.getMessage());
    		count=0;
    	}//end of catch
    	finally
    	{
    		try{if(pstmtUpdate!=null) pstmtUpdate.close();}catch(Exception e){
    			_log.error(methodName,"Exception "+e.getMessage());
                _log.errorTrace(methodName, e);
    		}
    		if(_log.isDebugEnabled())_log.debug(methodName,"Exiting count="+count);
    	}//end of finally
    	return 	count;
    }
    
    /**
     * 
     * @param p_con
     * @param requestedVoucherVO
     * @param batchVO
     */
    public synchronized long changeVoucherStatusByMasterSerialNo(Connection p_con,VomsBatchVO batchVO,VomsVoucherVO requestedVoucherVO)throws BTSLBaseException
    {
    	final String methodName = "changeVoucherStatusByMasterSerialNo";
    	if(_log.isDebugEnabled())_log.debug(methodName," Entered for requestedVoucherVO No:" +requestedVoucherVO +" batchVO No:" +batchVO);
    	PreparedStatement pstmtUpdate=null;
    	ResultSet rs = null;
    	long count=0;
    	try 
    	{
    		int i=1;
    		StringBuffer updateQueryBuff =new StringBuffer(" update VOMS_VOUCHERS  set CURRENT_STATUS=? ,PREVIOUS_STATUS=CURRENT_STATUS,STATUS=?,INFO1=?,ENABLE_BATCH_NO=?,LAST_BATCH_NO=?,MODIFIED_ON=?,MODIFIED_BY=? "); 
    		updateQueryBuff.append(" where MASTER_SERIAL_NO =? and CURRENT_STATUS=?"); 

    		String updateQuery=updateQueryBuff.toString();
    		if(_log.isDebugEnabled())_log.debug(methodName,"select query:"+updateQuery );
    		pstmtUpdate = p_con.prepareStatement(updateQuery);
    		pstmtUpdate.setString(i++,requestedVoucherVO.getStatus());
    		pstmtUpdate.setString(i++,requestedVoucherVO.getStatus());
    		pstmtUpdate.setString(i++,requestedVoucherVO.getInfo1());
    		pstmtUpdate.setString(i++,requestedVoucherVO.getEnableBatchNo());
    	
    		pstmtUpdate.setString(i++,batchVO.getBatchNo());
    		pstmtUpdate.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(batchVO.getModifiedOn()));
    		pstmtUpdate.setString(i++,batchVO.getModifiedBy());
    		pstmtUpdate.setLong(i++,requestedVoucherVO.getMasterSerialNo());
    		pstmtUpdate.setString(i++,batchVO.getReferenceType());
    		count= pstmtUpdate.executeUpdate();		
    	}
    	catch (SQLException sqle)
    	{
    		_log.error(methodName,"SQLException "+sqle.getMessage());
    		count=0;
    	}//end of catch
    	catch (Exception e)
    	{
    		_log.error(methodName,"Exception "+e.getMessage());
    		count=0;
    	}//end of catch
    	finally
    	{
    		try{if(pstmtUpdate!=null) pstmtUpdate.close();}catch(Exception e){
    			_log.error(methodName,"Exception "+e.getMessage());
                _log.errorTrace(methodName, e);
    		}
    		if(_log.isDebugEnabled())_log.debug(methodName,"Exiting count="+count);
    	}//end of finally
    	return 	count;
    }

	
    /**
     * 
     * @param p_con
     * @param requestedVoucherVO
     * @param batchVO
     */
    public synchronized long changeVoucherStatusByMasterAndRange(Connection p_con,VomsBatchVO batchVO,VomsVoucherVO requestedVoucherVO)throws BTSLBaseException
    {
    	final String methodName = "changeVoucherStatusByMasterAndRange";
    	if(_log.isDebugEnabled())_log.debug(methodName," Entered for requestedVoucherVO No:" +requestedVoucherVO +" batchVO No:" +batchVO);
    	PreparedStatement pstmtUpdate=null;
    	ResultSet rs = null;
    	long count=0;
    	try 
    	{
    		int i=1;
    		StringBuffer updateQueryBuff =new StringBuffer(" update VOMS_VOUCHERS  set CURRENT_STATUS=? ,PREVIOUS_STATUS=CURRENT_STATUS,STATUS=?,INFO1=?,ENABLE_BATCH_NO=?,LAST_BATCH_NO=?,MODIFIED_ON=?,MODIFIED_BY=? "); 
    		updateQueryBuff.append(" where SERIAL_NO >=? and SERIAL_NO <=? and MASTER_SERIAL_NO =? and CURRENT_STATUS=?"); 

    		String updateQuery=updateQueryBuff.toString();
    		if(_log.isDebugEnabled())_log.debug(methodName,"select query:"+updateQuery );
    		pstmtUpdate = p_con.prepareStatement(updateQuery);
    		pstmtUpdate.setString(i++,requestedVoucherVO.getStatus());
    		pstmtUpdate.setString(i++,requestedVoucherVO.getStatus());
    		pstmtUpdate.setString(i++,requestedVoucherVO.getInfo1());
    		pstmtUpdate.setString(i++,requestedVoucherVO.getEnableBatchNo());
    	
    		pstmtUpdate.setString(i++,batchVO.getBatchNo());
    		pstmtUpdate.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(batchVO.getModifiedOn()));
    		pstmtUpdate.setString(i++,batchVO.getModifiedBy());
    		pstmtUpdate.setString(i++,requestedVoucherVO.get_fromSerialNo());
    		pstmtUpdate.setString(i++,requestedVoucherVO.getToSerialNo());
    		pstmtUpdate.setLong(i++,requestedVoucherVO.getMasterSerialNo());
    		pstmtUpdate.setString(i++,batchVO.getReferenceType());
    		count= pstmtUpdate.executeUpdate();		
    	}
    	catch (SQLException sqle)
    	{
    		_log.error(methodName,"SQLException "+sqle.getMessage());
    		count=0;
    	}//end of catch
    	catch (Exception e)
    	{
    		_log.error(methodName,"Exception "+e.getMessage());
    		count=0;
    	}//end of catch
    	finally
    	{
    		try{if(pstmtUpdate!=null) pstmtUpdate.close();}catch(Exception e){
    			_log.error(methodName,"Exception "+e.getMessage());
                _log.errorTrace(methodName, e);
    		}
    		if(_log.isDebugEnabled())_log.debug(methodName,"Exiting count="+count);
    	}//end of finally
    	return 	count;
    }
    
	public long validateStatusMapping(Connection p_con, String requestedstatus, String currentStatus) {
    	final String methodName = "validateStatusMapping";
    	if(_log.isDebugEnabled())_log.debug(methodName," Entered for requestedstatus No:" +requestedstatus +" currentStatus No:" +currentStatus);
    	
    	long count=0;
    	try 
    	{
    		int i=1;
    		StringBuffer updateQueryBuff =new StringBuffer(" select count(*) as count from VOMS_VOUCHERS_STATUS_MAPPING   "); 
    		updateQueryBuff.append(" where STATUS =? and MAPPED_STATUS =? "); 

    		String updateQuery=updateQueryBuff.toString();
    		if(_log.isDebugEnabled())_log.debug(methodName,"select query:"+updateQuery );
    		try(PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQuery);)
    		{
    		pstmtUpdate.setString(i++,currentStatus);
    		pstmtUpdate.setString(i++,requestedstatus);
    		
    		try(ResultSet rs= pstmtUpdate.executeQuery();)
    		{
    		while(rs.next())
    		{
    			count=rs.getLong("count");
    		}
    	}
    		}
    	}
    	catch (SQLException sqle)
    	{
    		_log.error(methodName,"SQLException "+sqle.getMessage());
    		count=0;
    	}//end of catch
    	catch (Exception e)
    	{
    		_log.error(methodName,"Exception "+e.getMessage());
    		count=0;
    	}//end of catch
    	finally
    	{
    	
    		if(_log.isDebugEnabled())
    			_log.debug(methodName,"Exiting count="+count);
    	}//end of finally
    	return 	count;
    }
	
	/**
	 * This method is used to validate masterSerialNumber.
	 * @param p_con
	 * @param masterSerialNo
	 * @return long
	 */
	public long validateMasterSerialNumber(Connection p_con, Long masterSerialNo) {
		final String methodName = "validateMasterSerialNumber";
		if(_log.isDebugEnabled())_log.debug(methodName," Entered for masterSerial No:" +masterSerialNo);

		long count=0;
		try 
		{
			int i=1;
			StringBuffer selectQueryBuff =new StringBuffer(" select count(*) as count from MO_SO_NUMBER   "); 
			selectQueryBuff.append(" where FROMSERIAL_NUMBER <=? and TOSERIAL_NUMBER >=?");

			String selectQuery=selectQueryBuff.toString();
			if(_log.isDebugEnabled())_log.debug(methodName,"select query:"+selectQuery );
			try(PreparedStatement pstmtUpdate = p_con.prepareStatement(selectQuery);)
			{
				pstmtUpdate.setLong(i++,masterSerialNo);
				pstmtUpdate.setLong(i++,masterSerialNo);

				try(ResultSet rs= pstmtUpdate.executeQuery();)
				{
					while(rs.next())
					{
						count=rs.getLong("count");
					}
				}
			}
		}
		catch (SQLException sqle)
		{
			_log.error(methodName,"SQLException "+sqle.getMessage());
			count=0;
		}//end of catch
		catch (Exception e)
		{
			_log.error(methodName,"Exception "+e.getMessage());
			count=0;
		}//end of catch
		finally
		{

			if(_log.isDebugEnabled())
				_log.debug(methodName,"Exiting count="+count);
		}//end of finally
		return 	count;
	}

/**
     * 
     * @param p_con
     * @param requestedVoucherVO
     * @param fromVoucherVO
     */
    public long validateAllVoucherDetails(Connection p_con,VomsVoucherVO requestedVoucherVO,VomsVoucherVO fromVoucherVO)throws BTSLBaseException
    {
    	final String methodName = "validateAllVoucherDetails";
    	if(_log.isDebugEnabled())_log.debug(methodName," Entered for requestedVoucherVO No:" +requestedVoucherVO +" fromVoucherVO No:" +fromVoucherVO);
    	
    	long count=0;
    	try 
    	{
    		int i=1;
    		StringBuffer updateQueryBuff =new StringBuffer(" select count(*) as count from VOMS_VOUCHERS   "); 
    		updateQueryBuff.append(" where SERIAL_NO >=? and SERIAL_NO <=? and PRODUCT_ID=? and CURRENT_STATUS=?"); 

    		String updateQuery=updateQueryBuff.toString();
    		if(_log.isDebugEnabled())_log.debug(methodName,"select query:"+updateQuery );
    		try(PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQuery);)
    		{
    		pstmtUpdate.setString(i++,requestedVoucherVO.get_fromSerialNo());
    		pstmtUpdate.setString(i++,requestedVoucherVO.getToSerialNo());
    		pstmtUpdate.setString(i++,fromVoucherVO.getProductID());
    		pstmtUpdate.setString(i++,fromVoucherVO.getCurrentStatus());
    		try(ResultSet rs= pstmtUpdate.executeQuery();)
    		{
    		while(rs.next())
    		{
    			count=rs.getLong("count");
    		}
    	}
    		}
    	}
    	catch (SQLException sqle)
    	{
    		_log.error(methodName,"SQLException "+sqle.getMessage());
    		count=0;
    	}//end of catch
    	catch (Exception e)
    	{
    		_log.error(methodName,"Exception "+e.getMessage());
    		count=0;
    	}//end of catch
    	finally
    	{
    		
    		if(_log.isDebugEnabled())_log.debug(methodName,"Exiting count="+count);
    	}//end of finally
    	return 	count;
    }
/**
     * 
     * @param p_con
     * @param fromSerialNo
     */
    public VomsVoucherVO getVoucherDetails(Connection p_con,String fromSerialNo)throws BTSLBaseException
    {
    	final String methodName = "getVoucherDetails";
    	if(_log.isDebugEnabled())_log.debug(methodName," Entered for Serial No:" +fromSerialNo);
    	 
    	VomsVoucherVO fromVoucherVO=null;
    	try 
    	{
    		int i=1;
    		StringBuffer updateQueryBuff =new StringBuffer(" select SERIAL_NO,PRODUCT_ID,CURRENT_STATUS,PRODUCTION_NETWORK_CODE,USER_NETWORK_CODE,GENERATION_BATCH_NO,MRP,LAST_BATCH_NO,ENABLE_BATCH_NO,EXPIRY_DATE,MODIFIED_ON,VOUCHER_SEGMENT,VOUCHER_TYPE from VOMS_VOUCHERS   "); 
    		updateQueryBuff.append(" where SERIAL_NO=? "); 

    		String updateQuery=updateQueryBuff.toString();
    		if(_log.isDebugEnabled())_log.debug(methodName,"select query:"+updateQuery );
    		try(PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQuery);)
    		{
    		pstmtUpdate.setString(i++,fromSerialNo);
    		try(ResultSet rs= pstmtUpdate.executeQuery();)
    		{
    		while(rs.next())
    		{
    			fromVoucherVO=new VomsVoucherVO();
    			fromVoucherVO.set_fromSerialNo(rs.getString("SERIAL_NO"));
    			fromVoucherVO.setProductID(rs.getString("PRODUCT_ID"));
    			fromVoucherVO.setCurrentStatus(rs.getString("CURRENT_STATUS"));
    			fromVoucherVO.setProductionLocationCode(rs.getString("PRODUCTION_NETWORK_CODE"));
    			fromVoucherVO.setUserLocationCode(rs.getString("USER_NETWORK_CODE"));
    			fromVoucherVO.setMRP(rs.getDouble("mrp"));
    			fromVoucherVO.setGenerationBatchNo(rs.getString("GENERATION_BATCH_NO"));
    			fromVoucherVO.set_batch_no(rs.getString("GENERATION_BATCH_NO"));
    			fromVoucherVO.setEnableBatchNo(rs.getString("ENABLE_BATCH_NO"));
    			fromVoucherVO.setExpiryDateStr(BTSLUtil.getDateStringFromDate(rs.getDate("expiry_date")));
    			fromVoucherVO.setPrevStatusModifiedOn(BTSLUtil.getDateStringFromDate(rs.getDate("modified_on")));
    			fromVoucherVO.setVoucherSegment(rs.getString("VOUCHER_SEGMENT"));
    			fromVoucherVO.setVoucherType(rs.getString("VOUCHER_TYPE"));
    			
    		}
    		}
    	}
    	}
    	catch (SQLException sqle)
    	{
    		_log.error(methodName,"SQLException "+sqle.getMessage());
    		fromVoucherVO=null;
    	}//end of catch
    	catch (Exception e)
    	{
    		_log.error(methodName,"Exception "+e.getMessage());
    		fromVoucherVO=null;
    	}//end of catch
    	finally
    	{
    	
    		if(_log.isDebugEnabled())_log.debug(methodName,"Exiting fromVoucherVO="+fromVoucherVO);
    	}//end of finally
    	return fromVoucherVO;
    }	

	 public boolean validateVoucherStatus(Connection p_con,String status)throws BTSLBaseException
    {
    	final String methodName = "validateVoucherStatus";
    	if(_log.isDebugEnabled())_log.debug(methodName," Entered for Status Requested:" +status);
    	 
    	boolean isexists=false;
    	try 
    	{
    		int i=1;
    		StringBuffer updateQueryBuff =new StringBuffer(" select LOOKUP_CODE from LOOKUPS   "); 
    		updateQueryBuff.append(" where LOOKUP_TYPE='VSTAT' and status='Y' and LOOKUP_CODE=?"); 

    		String updateQuery=updateQueryBuff.toString();
    		if(_log.isDebugEnabled())_log.debug(methodName,"select query:"+updateQuery );
    		try(PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQuery);)
    		{
    		pstmtUpdate.setString(i++,status);
    		try(ResultSet rs= pstmtUpdate.executeQuery();)
    		{
    		while(rs.next())
    		{
    			isexists= true;
    		}

    	}
    		}
    	}
    	catch (SQLException sqle)
    	{
    		_log.error(methodName,"SQLException "+sqle.getMessage());
    		isexists= false;
    	}//end of catch
    	catch (Exception e)
    	{
    		_log.error(methodName,"Exception "+e.getMessage());
    		isexists= false;
    	}//end of catch
    	finally
    	{
    		
    		if(_log.isDebugEnabled())_log.debug(methodName,"Exiting isexists="+isexists);
    	}//end of finally
    	return isexists;
    }
	 
	 /**
	     * This method finds the list of users to which alert is to be sent for voucher burn rate
	     * 
	     * @param con
	     *            Connection
	     * @return boolean
	     */
	    public  ArrayList<VomsVoucherVO> burnRateAlertUsers(Connection con) throws BTSLBaseException {
	        final String methodName = "burnRateAlertUsers";
	        final String processName = "burnRateAlertProcess";
	        if (_log.isDebugEnabled()) {
	            _log.info(methodName, "Entered");
	        }
	        PreparedStatement pstmt = null;
	        ResultSet rst = null;
	        ArrayList<VomsVoucherVO> list = null;
	        VomsVoucherVO alertVO = null;
	        Date dateobj = new Date();
            DateFormat df = new SimpleDateFormat(PretupsI.DATE_FORMAT);


	        try {
	        	BurnRateIndicatorProcessQry burnRateIndicatorProcessQry = (BurnRateIndicatorProcessQry)ObjectProducer.getObject(QueryConstants.BURN_RATE_ALERT_PROCESS_QRY, QueryConstants.QUERY_PRODUCER);
	        	
	            final String query = burnRateIndicatorProcessQry.burnRateAlertUsersQry();
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query:" + query);
	            }
	        	Date currentDate = df.parse(df.format(dateobj));
	            pstmt = con.prepareStatement(query.toString());
	            pstmt.setLong(1,((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue() );
	            pstmt.setLong(2,Long.parseLong(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.BURN_RATE_THRESHOLD))));
	            pstmt.setDate(3, BTSLUtil.getSQLDateFromUtilDate(currentDate));
	            pstmt.setDate(4, BTSLUtil.getSQLDateFromUtilDate(currentDate));
	            rst = pstmt.executeQuery();
	            list = new ArrayList<>();
	            while (rst.next()) {
	                alertVO = new VomsVoucherVO();
	                alertVO.setUserID(rst.getString("user_id"));
	                alertVO.setMsisdn(rst.getString("msisdn"));
	                alertVO.setProductID(rst.getString("product_id"));
	                alertVO.setUserNetworkCode(rst.getString("user_network_code"));
	                alertVO.setBurnRate(rst.getLong("burn_rate"));
	                alertVO.setTotalDistributed(rst.getLong("total_distributed"));
	                alertVO.setTotalConsumed(rst.getLong("total_recharged"));
	                alertVO.setMRP(rst.getLong("mrp"));
	                list.add(alertVO);
	            }
	        }// end try
	        catch (SQLException sqle) {
	            _log.error(methodName, "SQLException " + sqle.getMessage());
	            _log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, processName, "", "",
	                "", "SQL Exception:" + sqle.getMessage());
	            throw new BTSLBaseException(processName, methodName, "error.general.sql.processing");
	        }// end of catch
	        catch (Exception e) {
	            _log.error(methodName, "Exception " + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, processName, "", "",
	                "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(processName, methodName, "error.general.processing");
	        }// end of catch
	        finally {
	            if (rst != null) {
	                try {
	                    rst.close();
	                } catch (SQLException e2) {
	                    _log.errorTrace(methodName, e2);
	                }
	            }
	            if (pstmt != null) {
	                try {
	                    pstmt.close();
	                } catch (SQLException e3) {
	                    _log.errorTrace(methodName, e3);
	                }
	            }
	            if (_log.isDebugEnabled()) {
	                _log.info(methodName, " Exiting list size " + list.size());
	            }
	        }// end finally
	        return list;
	    }

	public int updateVoucherConsumptionStatus(Connection con,P2PTransferVO _p2pTransferVO, VomsVoucherVO vomsVoucherVO) throws BTSLBaseException {
		final String methodName = "updateVoucherConsumptionStatus";
		if (_log.isDebugEnabled()) {

        _log.debug(methodName, " Entered for voucher serial no.=" + vomsVoucherVO.getSerialNo() + " Status=" + _p2pTransferVO.getStatus());
    }
    PreparedStatement pstmtUpdate = null;
    int updateCount = 0;
    String operation = null;
   
    try {
        int i = 1;
        StringBuffer updateQueryBuff = new StringBuffer(" UPDATE voms_vouchers SET   ");
        updateQueryBuff.append(" current_status=?, previous_status=?, status=?, CONSUMED_GATEWAY_TYPE=?,CONSUMED_GATEWAY_CODE=?,  ");
        updateQueryBuff.append("  first_consumed_on=?,  modified_by=?, modified_on=?, last_consumed_on=?, last_transaction_id=? ");
        updateQueryBuff.append(" WHERE serial_no=? ");
    	if(VOMSI.VOUCHER_SEGMENT_LOCAL.equals(vomsVoucherVO.getVoucherSegment()))
    		updateQueryBuff.append(" production_network_code = ? ");
        String updateQuery = updateQueryBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "UPDATE query:" + updateQuery);
        }
        pstmtUpdate = con.prepareStatement(updateQuery);

			if (PretupsI.TXN_STATUS_SUCCESS.equals(_p2pTransferVO.getTransferStatus())) {
				operation = PretupsI.SUCCESS;
				pstmtUpdate.setString(i++, VOMSI.VOUCHER_USED);
				pstmtUpdate.setString(i++, vomsVoucherVO.getCurrentStatus());
				pstmtUpdate.setString(i++, VOMSI.VOUCHER_USED);
				pstmtUpdate.setString(i++, _p2pTransferVO.getRequestGatewayType());
				pstmtUpdate.setString(i++, _p2pTransferVO.getRequestGatewayCode());
				pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(_p2pTransferVO.getModifiedOn()));

			} else if (PretupsI.TXN_STATUS_FAIL.equals(_p2pTransferVO.getTransferStatus())) {
        	    operation = PretupsI.FAIL;
                pstmtUpdate.setString(i++, VOMSI.VOUCHER_REP_ENABLE);
                pstmtUpdate.setString(i++, vomsVoucherVO.getCurrentStatus());
                pstmtUpdate.setString(i++, VOMSI.VOUCHER_REP_ENABLE);
                pstmtUpdate.setString(i++, "");
				pstmtUpdate.setString(i++, "");
                pstmtUpdate.setTimestamp(i++, null);
            
			} else if (PretupsI.TXN_STATUS_AMBIGIOUS.equals(_p2pTransferVO.getTransferStatus())) {
				operation = PretupsI.TXN_STATUS_AMBIGIOUS;
				pstmtUpdate.setString(i++, VOMSI.UNDERPROCESS);
				pstmtUpdate.setString(i++, VOMSI.VOUCHER_REP_ENABLE);
				pstmtUpdate.setString(i++, VOMSI.UNDERPROCESS);
				pstmtUpdate.setString(i++, _p2pTransferVO.getRequestGatewayType());
				pstmtUpdate.setString(i++, _p2pTransferVO.getRequestGatewayCode());
				pstmtUpdate.setTimestamp(i++, null);
			}
        pstmtUpdate.setString(i++, _p2pTransferVO.getModifiedBy());
        pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(_p2pTransferVO.getModifiedOn()));
        pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(_p2pTransferVO.getModifiedOn()));
        pstmtUpdate.setString(i++, _p2pTransferVO.getTransferID());
        pstmtUpdate.setString(i++, _p2pTransferVO.getSerialNumber());
        if(VOMSI.VOUCHER_SEGMENT_LOCAL.equals(vomsVoucherVO.getVoucherSegment()))
        	pstmtUpdate.setString(i++, _p2pTransferVO.getNetworkCode());
        updateCount = pstmtUpdate.executeUpdate();
        if (updateCount > 0) {
            updateCount = 0;
            updateCount = updateVoucherAuditStatus(con, operation, _p2pTransferVO, vomsVoucherVO);
        }
        return updateCount;
    } catch (BTSLBaseException | SQLException sqle) {
        _log.error(methodName, "SQLException " + sqle.getMessage());
        updateCount = 0;
        _log.errorTrace(methodName, sqle);
        throw new BTSLBaseException(this, methodName, "error.general.processing");

    }

     finally {
        try {
            if (pstmtUpdate != null) {
                pstmtUpdate.close();
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting updateCount=" + updateCount);
        }
    }

  }
	
	/**
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @return
     * @throws BTSLBaseException
     */
    public VomsVoucherVO loadVomsVoucherVO(Connection p_con, VomsDetailVO rightelVoucherDetailVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadVomsVoucherVO", " Entered voucher serial no.=" + rightelVoucherDetailVO.getSerialNumber());
        }
        PreparedStatement dbPs = null;
        ResultSet rs = null;
        VomsVoucherVO voucherVO = null;
        final String methodName = "loadVomsVoucherVO";
        try {
            StringBuffer sqlSelectBuf = new StringBuffer(" select v.serial_no SERIALNO, v.current_status CURRENTSTAT,  v.previous_status PREVSTAT, v.status  STAT ,PIN_NO, v.user_id USERID, v.product_id  PID ");
            sqlSelectBuf.append(" FROM voms_vouchers v WHERE v.SERIAL_NO=? ");

            if (_log.isDebugEnabled()) {
                _log.debug("loadVomsVoucherVO", "Select Query=" + sqlSelectBuf.toString());
            }
            dbPs = p_con.prepareStatement(sqlSelectBuf.toString());
            dbPs.setString(1, rightelVoucherDetailVO.getSerialNumber());
            rs = dbPs.executeQuery();

            while (rs.next()) {
                voucherVO = new VomsVoucherVO();
                voucherVO.setSerialNo(rs.getString("SERIALNO"));
                voucherVO.setPreviousStatus(rs.getString("PREVSTAT"));
                voucherVO.setCurrentStatus(rs.getString("CURRENTSTAT"));
                voucherVO.setVoucherStatus(rs.getString("STAT"));
                voucherVO.setPinNo(rs.getString("PIN_NO"));
                voucherVO.setUserID(rs.getString("USERID"));
                voucherVO.setProductID(rs.getString("PID"));
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadVomsVoucherVO", "After executing the query loadVomsVoucherVO method VomsVoucherVO=" + voucherVO);
            }
            return voucherVO;
        } catch (SQLException sqle) {
            _log.error("loadVomsVoucherVO", "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadVomsVoucherVO]", "", "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadVomsVoucherVO", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadVomsVoucherVO", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadVomsVoucherVO]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadVomsVoucherVO", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.error("loadVomsVoucherVO", " Exception while closing rs ex=" + ex);
            }
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.error("loadVomsVoucherVO", " Exception while closing prepared statement ex=" + ex);
            }
            try {
                _log.debug("loadVomsVoucherVO", " Exiting.. VomsVoucherVO=" + voucherVO);
            } catch (Exception e) {
                _log.error("loadVomsVoucherVO", " Exception while closing rs ex=" + e);
            }
            ;
        }
    }
    public ArrayList<ArrayList<String> > loadAvailableVouchers(Connection con, String voucherType, String voucherSement, String denomination, String voucherProduct, String channelUserId, String networkId) throws BTSLBaseException {
		final String methodName = "loadAvailableVouchers";
		StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: voucherType =");
        	loggerValue.append(voucherType);
        	loggerValue.append(" voucherSement=");
        	loggerValue.append(voucherSement);
        	loggerValue.append(" denomination=");
        	loggerValue.append(denomination);
        	loggerValue.append(" voucherProduct=");
        	loggerValue.append(voucherProduct);
        	loggerValue.append(" channelUserId=");
        	loggerValue.append(channelUserId);
        	loggerValue.append(" networkId=");
        	loggerValue.append(networkId);
        	_log.debug(methodName, loggerValue);
        }
		// commented for DB2 OraclePreparedStatement pstmt =null commented
        PreparedStatement pstmt = null;
        ResultSet rs = null;
		final ArrayList<ArrayList<String> > response = new ArrayList<>();
		try {
			StringBuffer sqlBuffer = null;
			if(!BTSLUtil.isNullString(voucherProduct)){
				sqlBuffer = new StringBuffer(" SELECT vv.voucher_type, vv.voucher_segment, vv.mrp, vv.product_id, count(*) AS available "
					+ "FROM voms_vouchers vv WHERE vv.status = 'EN' AND vv.SUBSCRIBER_ID IS NULL "
					+ "AND vv.USER_ID = ? AND vv.EXPIRY_DATE >= ? and vv.voucher_type = ? and vv.voucher_segment = ? and vv.mrp = ? and vv.product_id = ? and vv.user_network_code = ? "
					+ "GROUP BY vv.voucher_type, vv.VOUCHER_SEGMENT, vv.mrp, vv.product_id");
			}
			
			else
			{
				sqlBuffer = new StringBuffer(" SELECT vv.voucher_type, vv.voucher_segment, vv.mrp, count(*) AS available "
						+ "FROM voms_vouchers vv WHERE vv.status = 'EN' AND vv.SUBSCRIBER_ID IS NULL "
						+ "AND vv.USER_ID = ? AND vv.EXPIRY_DATE >= ? AND vv.voucher_type = ? and vv.voucher_segment = ? and vv.mrp = ? and vv.user_network_code = ? "
						+ "GROUP BY vv.voucher_type, vv.VOUCHER_SEGMENT, vv.mrp");
			}
			final String sqlSelect = sqlBuffer.toString();
			
			if(_log.isDebugEnabled()){
				loggerValue.setLength(0);
				loggerValue.append("Query =");
				loggerValue.append(sqlSelect);
				_log.debug(methodName, loggerValue);
			}
			pstmt = con.prepareStatement(sqlSelect);
			int i = 0;
			pstmt.setString(++i, channelUserId);
			pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(new java.util.Date()));
			pstmt.setString(++i, voucherType);
			pstmt.setString(++i, voucherSement);
			pstmt.setLong(++i, PretupsBL.getSystemAmount(denomination));
			if(!BTSLUtil.isNullString(voucherProduct))
				pstmt.setString(++i, voucherProduct);
			
			pstmt.setString(++i, networkId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				ArrayList<String> array;
				if(!BTSLUtil.isNullString(voucherProduct))
					array = new ArrayList<String>(Arrays.asList(rs.getString("VOUCHER_TYPE"), BTSLUtil.getSegmentDesc(rs.getString("VOUCHER_SEGMENT")), rs.getString("MRP"), rs.getString("PRODUCT_ID"), rs.getString("AVAILABLE")));
				else
					array = new ArrayList<String>(Arrays.asList(rs.getString("VOUCHER_TYPE"), BTSLUtil.getSegmentDesc(rs.getString("VOUCHER_SEGMENT")), rs.getString("MRP"), rs.getString("AVAILABLE")));
				response.add(array);
			}
			}
			catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append("SQL_EXCEPTION");
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByParentIDRecursive]", "", "",
					"", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append("EXCEPTION");
			loggerValue.append(ex.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByParentIDRecursive]", "", "",
					"", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
		} finally {
			try {
                if (rs != null) {
                	rs.close();
                }
            } catch (SQLException e) {
            	_log.errorTrace(methodName, e);
            } catch (Exception e) {
            	_log.errorTrace(methodName, e);
            }
			try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (SQLException e) {
            	_log.errorTrace(methodName, e);
            } catch (Exception e) {
            	_log.errorTrace(methodName, e);
            }
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting Array List Size:");
				loggerValue.append(response.size());
				_log.debug(methodName, loggerValue);
			}
		}
		return response;
	}
    
    /**
	 * This method is used to  p2p loadAvailableDigitalVoucherSubscriberList.
	 * @param p_con
	 * @param p_subscriberId
	 * @return VomsVoucherVO
	 * @throws BTSLBaseException
	 */
	public ArrayList loadAvailableDigitalVoucherSubscriberList(Connection p_con, String p_subscriberId ) throws BTSLBaseException
	{
        final String methodName = "loadAvailableDigitalVoucherSubscriberList";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_subscriberId=");
        	loggerValue.append(p_subscriberId);
        	_log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        VomsVoucherVO voucherAssigned=null;
        ResultSet rs = null;
		ArrayList<VomsVoucherVO> assignedVoucherList = new ArrayList<VomsVoucherVO>();
        try {
            final StringBuffer selectQueryBuff = new StringBuffer("SELECT VS.SERIAL_NO ,VS.PRODUCT_ID,VS.PIN_NO,VS.USER_ID,VS.VOUCHER_SEGMENT,VS.VOUCHER_TYPE,VS.MRP,VP.PRODUCT_NAME,VS.EXPIRY_DATE ");
            selectQueryBuff.append("FROM VOMS_VOUCHERS VS,VOMS_PRODUCTS VP ");
            selectQueryBuff.append("WHERE VS.SUBSCRIBER_ID=? AND VS.STATUS='EN' AND VS.EXPIRY_DATE >=? AND VS.PRODUCT_ID=VP.PRODUCT_ID ");
            
            final String selectQuery = selectQueryBuff.toString();
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("QUERY_KEY");
    			loggerValue.append(selectQuery);
    			_log.debug(methodName, loggerValue);
    		}
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_subscriberId);
            pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(new java.util.Date()));
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
            	voucherAssigned = new VomsVoucherVO();
            	voucherAssigned.setProductName(rs.getString("PRODUCT_NAME"));
            	voucherAssigned.setSerialNo(rs.getString("SERIAL_NO"));
            	voucherAssigned.setVoucherType(rs.getString("VOUCHER_TYPE"));
            	voucherAssigned.setVoucherSegment(rs.getString("VOUCHER_SEGMENT"));
            	voucherAssigned.setProductID(rs.getString("PRODUCT_ID"));
            	voucherAssigned.setUserID(rs.getString("USER_ID"));
            	voucherAssigned.setMRP(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("MRP"))));
            	voucherAssigned.setPinNo(rs.getString("PIN_NO"));
            	voucherAssigned.setExpiryDate(rs.getDate("EXPIRY_DATE"));
                assignedVoucherList.add(voucherAssigned);
                
            }
            return assignedVoucherList;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append("SQL_EXCEPTION");
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadAvailableDigitalVoucherSubscriberList]", "",
            		p_subscriberId, "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append("EXCEPTION");
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadAvailableDigitalVoucherSubscriberList]", "",
            		p_subscriberId, "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: VoucherList:");
             	loggerValue.append(assignedVoucherList.size());
             	_log.debug(methodName, loggerValue);
             }
        }// end of finally
    }
    
	
	  /**
	 * @param p_con
	 * @param _c2sTransferVO
	 * @return
	 * @throws BTSLBaseException
	 */
	public ArrayList<VomsVoucherVO> loadVomsVoucherVObyUserId(Connection p_con, C2STransferVO _c2sTransferVO,String systemLimit ) throws BTSLBaseException {
	        if (_log.isDebugEnabled()) {
	            _log.debug("loadVomsVoucherVObyUserId", " Entered _c2sTransferVO=" + _c2sTransferVO);
	        }
	        PreparedStatement dbPs = null;
	        ResultSet rs = null;
	        ArrayList<VomsVoucherVO> voucherVoList = new ArrayList<>();
	        VomsVoucherVO voucherVO = null;
	        final String methodName = "loadVomsVoucherVObyUserId";
	        try {
	        	int i =1;
	        	String orderBy = getDVDOrderByParams(systemLimit);
	        	VOMSVoucherQry vvomsVoucherQry = (VOMSVoucherQry) ObjectProducer.getObject(QueryConstants.VOMS_VOUCHER_QRY,QueryConstants.QUERY_PRODUCER);
	        	StringBuilder sqlSelectBuf = vvomsVoucherQry.loadVomsVoucherVObyUserId(_c2sTransferVO.getProductId(),orderBy);
	            if (_log.isDebugEnabled()) {
	                _log.debug("loadVomsVoucherVObyUserId", "Select Query=" + sqlSelectBuf.toString());
	            }
	            dbPs = p_con.prepareStatement(sqlSelectBuf.toString());
	            dbPs.setString(i++, VOMSI.VOUCHER_ENABLE);
	            dbPs.setString(i++, _c2sTransferVO.getSenderID());
	            dbPs.setString(i++, _c2sTransferVO.getVoucherType());
	            dbPs.setString(i++, _c2sTransferVO.getVoucherSegment());
	            if(!BTSLUtil.isNullString(_c2sTransferVO.getProductId()))
	            	dbPs.setString(i++, _c2sTransferVO.getProductId());
	            dbPs.setLong(i++,_c2sTransferVO.getRequestedAmount());
	            dbPs.setString(i++, _c2sTransferVO.getNetworkCode());
	            dbPs.setInt(i++,Integer.parseInt(_c2sTransferVO.getVoucherQuantity()));
	            rs = dbPs.executeQuery();

	            while (rs.next()) {
	                voucherVO = new VomsVoucherVO();
	                voucherVO.setSerialNo(rs.getString("SERIAL_NO"));
	                voucherVO.setCurrentStatus(rs.getString("current_status"));
	                voucherVO.setMRP(rs.getDouble("MRP"));
	                voucherVO.setPinNo(rs.getString("PIN_NO"));
	                voucherVO.setProductID(rs.getString("product_id"));
	                voucherVO.setVoucherType(rs.getString("VOUCHER_TYPE"));
	                voucherVO.setVoucherSegment(rs.getString("VOUCHER_SEGMENT"));
	                voucherVO.setExpiryDate(rs.getDate("EXPIRY_DATE"));
	                voucherVoList.add(voucherVO);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug("loadVomsVoucherVObyUserId", "After executing the query loadVomsVoucherVO method VomsVoucherVO=" + voucherVO);
	            }
	            return voucherVoList;
	        } catch (SQLException sqle) {
	            _log.error("loadVomsVoucherVObyUserId", "SQLException " + sqle.getMessage());
	            _log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadVomsVoucherVO]", "", "", "", "Exception:" + sqle.getMessage());
	            throw new BTSLBaseException(this, "loadVomsVoucherVObyUserId", "error.general.sql.processing");
	        }// end of catch
	        catch (Exception e) {
	            _log.error("loadVomsVoucherVObyUserId", "Exception " + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadVomsVoucherVO]", "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, "loadVomsVoucherVObyUserId", "error.general.processing");
	        }// end of catch
	        finally {
	            try {
	                if (rs != null) {
	                    rs.close();
	                }
	            } catch (Exception ex) {
	                _log.error("loadVomsVoucherVObyUserId", " Exception while closing rs ex=" + ex);
	            }
	            try {
	                if (dbPs != null) {
	                    dbPs.close();
	                }
	            } catch (Exception ex) {
	                _log.error("loadVomsVoucherVObyUserId", " Exception while closing prepared statement ex=" + ex);
	            }
	            try {
	                _log.debug("loadVomsVoucherVObyUserId", " Exiting.. VomsVoucherVO=" + voucherVO);
	            } catch (Exception e) {
	                _log.error("loadVomsVoucherVObyUserId", " Exception while closing rs ex=" + e);
	            }
	        }
	    }
	  

	  /**
	 * @param p_con
	 * @param vomsVoucherVOList
	 * @param _c2sTransferVO
	 * @return
	 * @throws BTSLBaseException
	 */
	/**
	 * @param p_con
	 * @param vomsVoucherVOList
	 * @param c2sTransferVO
	 * @return
	 * @throws BTSLBaseException
	 */
	public int updateVoucherSubsriberId(Connection p_con,  ArrayList<VomsVoucherVO> vomsVoucherVOList,C2STransferVO c2sTransferVO ) throws BTSLBaseException {
	        if (_log.isDebugEnabled()) {
	            _log.debug("updateVoucherSubsriberId() ", " Entered vomsVoucherVOList.=" + vomsVoucherVOList);
	        }
	        PreparedStatement pstmtUpdate = null;
	        int updateCount = 0;
	        final String methodName = "updateVoucherSubsriberId";
	        StringBuilder vomsString = new StringBuilder();
	        
	        try {
	            int i = 1;
	            int count = 0;
	            Date currDate = new Date();
	            StringBuffer updateQueryBuff = new StringBuffer(" UPDATE voms_vouchers SET");
	            updateQueryBuff.append(" subscriber_id= ? ,sold_status = ?, sold_date = ?,");
	            updateQueryBuff.append(" modified_by = ?, modified_on = ?, sale_batch_no=?");
	            if(!BTSLUtil.isNullString(c2sTransferVO.getServiceType()) && PretupsI.SERVICE_TYPE_DVD.equals(c2sTransferVO.getServiceType())){
	            	 updateQueryBuff.append(" ,c2s_transaction_id=? ");
	            }
	            updateQueryBuff.append(" WHERE serial_no=? AND USER_NETWORK_CODE = ?");
	            String updateQuery = updateQueryBuff.toString();
	            if (_log.isDebugEnabled()) {
	                _log.debug("updateVoucherSubsriberId", "Update query:" + updateQuery);
	            }
	          for(VomsVoucherVO vomsVoucherVO: vomsVoucherVOList){
		        	count =0;
		        	i=1;
		        	pstmtUpdate = p_con.prepareStatement(updateQuery);
		            pstmtUpdate.setString(i++, c2sTransferVO.getReceiverMsisdn());
		            pstmtUpdate.setString(i++, VOMSI.VOMS_SOLD);
		            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currDate));
		            pstmtUpdate.setString(i++, c2sTransferVO.getSenderID());
		            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currDate));
		            if (!BTSLUtil.isNullString(vomsVoucherVO.getSaleBatchNo())) {
		            	pstmtUpdate.setString(i++, vomsVoucherVO.getSaleBatchNo());
	                } else
	                	pstmtUpdate.setString(i++, null);
		            if(!BTSLUtil.isNullString(c2sTransferVO.getServiceType()) && PretupsI.SERVICE_TYPE_DVD.equals(c2sTransferVO.getServiceType())){
			            pstmtUpdate.setString(i++, vomsVoucherVO.getTransactionID());
		            }
		            pstmtUpdate.setString(i++, vomsVoucherVO.getSerialNo());
		            pstmtUpdate.setString(i++, c2sTransferVO.getNetworkCode());
		            count = pstmtUpdate.executeUpdate();
		            if(count>0){
		            	updateCount++;
		            	if(BTSLUtil.isNullString(vomsString.toString()))
		            		vomsString.append(vomsVoucherVO.getSerialNo()).append(":").append(VomsUtil.decryptText(vomsVoucherVO.getPinNo()));
		            	else
		            		vomsString.append(",").append(vomsVoucherVO.getSerialNo()).append(":").append(VomsUtil.decryptText(vomsVoucherVO.getPinNo()));
		            }
		           try{
		        	   if (pstmtUpdate != null) {
	                    pstmtUpdate.close();
	                }
		           }catch (Exception e) {
		        	   _log.error("updateVoucherStatus", "Exception:Unable to close pstmtUpdate statement " + e);
				}
	          }
	          
	         c2sTransferVO.setVomsSerialPinAsString(vomsString.toString());
	          return updateCount;
	        } catch (SQLException sqle) {
	            _log.error("updateVoucherStatus", "SQLException " + sqle.getMessage());
	            updateCount = 0;
	            _log.errorTrace(methodName, sqle);
	            throw new BTSLBaseException(this, "updateVoucherStatus", "error.general.sql.processing");
	        }// end of catch
	        catch (Exception e) {
	            _log.error("updateVoucherStatus", "Exception " + e.getMessage());
	            updateCount = 0;
	            _log.errorTrace(methodName, e);
	            throw new BTSLBaseException(this, "updateVoucherStatus", "error.general.processing");
	        }// end of catch
	        finally {
	            try {
	                if (pstmtUpdate != null) {
	                    pstmtUpdate.close();
	                }
	            } catch (Exception e) {
	                _log.errorTrace(methodName, e);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug("updateVoucherStatus", "Exiting updateCount=" + updateCount);
	            }
	        }// end of finally
	    }

	  public long loadVomsVoucherDetails(Connection p_con, C2STransferVO _c2sTransferVO ) throws BTSLBaseException {

	        final String methodName = "loadVomsVoucherDetails";  
		    if (_log.isDebugEnabled()) {
	            _log.debug(methodName, " Entered _c2sTransferVO=" + _c2sTransferVO);
	        }
	        PreparedStatement dbPs = null;
	        ResultSet rs = null;
	        VomsVoucherVO voucherVO = null;
	        long vomsVoucherCount = 0;
	        try {
	        	int i =1;
	            StringBuffer sqlSelectBuf = new StringBuffer("select count(1)  FROM voms_vouchers v ");
	            sqlSelectBuf.append(" WHERE v.voucher_type=?  and v.voucher_segment =? and v.mrp = ? and v.USER_NETWORK_CODE = ? ");

	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Select Query=" + sqlSelectBuf.toString());
	            }
	            dbPs = p_con.prepareStatement(sqlSelectBuf.toString());
	            dbPs.setString(i++, _c2sTransferVO.getVoucherType());
	            dbPs.setString(i++, _c2sTransferVO.getVoucherSegment());
	            dbPs.setLong(i++, _c2sTransferVO.getRequestedAmount());
	            dbPs.setString(i++, ((ChannelUserVO)_c2sTransferVO.getSenderVO()).getNetworkID());
	            rs = dbPs.executeQuery();

	            if (rs.next()) {
	            	vomsVoucherCount = rs.getLong(1);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "After executing the query loadVomsVoucherVO method vomsVoucherCount=" + vomsVoucherCount);
	            }
	            return vomsVoucherCount;
	        } catch (SQLException sqle) {
	            _log.error(methodName, "SQLException " + sqle.getMessage());
	            _log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadVomsVoucherDetails]", "", "", "", "Exception:" + sqle.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        }// end of catch
	        catch (Exception e) {
	            _log.error("loadVomsVoucherDetails", "Exception " + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadVomsVoucherDetails]", "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        }// end of catch
	        finally {
	            try {
	                if (rs != null) {
	                    rs.close();
	                }
	            } catch (Exception ex) {
	                _log.error(methodName, " Exception while closing rs ex=" + ex);
	            }
	            try {
	                if (dbPs != null) {
	                    dbPs.close();
	                }
	            } catch (Exception ex) {
	                _log.error("loadVomsVoucherVO", " Exception while closing prepared statement ex=" + ex);
	            } 
	            _log.debug("loadVomsVoucherVO", " Exiting.. vomsVoucherCount=" + vomsVoucherCount);
	        }
	    }
		
		public VomsVoucherVO getVoucherDetails(Connection p_con,String fromSerialNo,String networkCode)throws BTSLBaseException{
			final String methodName = "getVoucherDetails";
			if(_log.isDebugEnabled())_log.debug(methodName," Entered for Serial No:" +fromSerialNo+" NetworkCode "+networkCode);

			VomsVoucherVO fromVoucherVO=null;
			try 
			{
				int i=1;
				StringBuffer updateQueryBuff =new StringBuffer(" select SERIAL_NO,PRODUCT_ID,CURRENT_STATUS,PRODUCTION_NETWORK_CODE,USER_NETWORK_CODE,GENERATION_BATCH_NO,MRP,LAST_BATCH_NO,ENABLE_BATCH_NO,EXPIRY_DATE,MODIFIED_ON from VOMS_VOUCHERS   "); 
				updateQueryBuff.append(" where SERIAL_NO=? and PRODUCTION_NETWORK_CODE=?"); 

				String updateQuery=updateQueryBuff.toString();
				if(_log.isDebugEnabled())_log.debug(methodName,"select query:"+updateQuery );
				try(PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQuery);)
				{
					pstmtUpdate.setString(i++,fromSerialNo);
					pstmtUpdate.setString(i++,networkCode);
					try(ResultSet rs= pstmtUpdate.executeQuery();)
					{
						while(rs.next())
						{
							fromVoucherVO=new VomsVoucherVO();
							fromVoucherVO.set_fromSerialNo(rs.getString("SERIAL_NO"));
							fromVoucherVO.setProductID(rs.getString("PRODUCT_ID"));
							fromVoucherVO.setCurrentStatus(rs.getString("CURRENT_STATUS"));
							fromVoucherVO.setProductionLocationCode(rs.getString("PRODUCTION_NETWORK_CODE"));
							fromVoucherVO.setUserLocationCode(rs.getString("USER_NETWORK_CODE"));
							fromVoucherVO.setMRP(rs.getDouble("mrp"));
							fromVoucherVO.setGenerationBatchNo(rs.getString("GENERATION_BATCH_NO"));
							fromVoucherVO.set_batch_no(rs.getString("GENERATION_BATCH_NO"));
							fromVoucherVO.setEnableBatchNo(rs.getString("ENABLE_BATCH_NO"));
							fromVoucherVO.setExpiryDateStr(BTSLUtil.getDateStringFromDate(rs.getDate("expiry_date")));
							fromVoucherVO.setPrevStatusModifiedOn(BTSLUtil.getDateStringFromDate(rs.getDate("modified_on")));
						}
					}
				}
			}
			catch (SQLException sqle)
			{
				_log.error(methodName,"SQLException "+sqle.getMessage());
				fromVoucherVO=null;
			}//end of catch
			catch (Exception e)
			{
				_log.error(methodName,"Exception "+e.getMessage());
				fromVoucherVO=null;
			}//end of catch
			finally
			{

				if(_log.isDebugEnabled())_log.debug(methodName,"Exiting fromVoucherVO="+fromVoucherVO);
			}//end of finally
			return fromVoucherVO;
		}
		/**
		 * This method is used to load voucher details by masterSerialNumber.
		 * @param p_con
		 * @param masterSerialNo
		 * @param networkCode
		 * @return VomsVoucherVO List
		 */
		public ArrayList<VomsVoucherVO> getVoucherDetailsByMasterSerialNumber(Connection p_con,String masterSerialNo,String networkCode)throws BTSLBaseException
		{
			final String methodName = "getVoucherDetailsByMasterSerialNumber";
			if(_log.isDebugEnabled())_log.debug(methodName," Entered for Master Serial No:" +masterSerialNo+" NetworkCode "+networkCode);

			VomsVoucherVO vomsVoucher=null;
			ArrayList<VomsVoucherVO> vomsVoucherVOList = new ArrayList<>();
			try 
			{
				int i=1;
				VOMSVoucherQry vvomsVoucherQry = (VOMSVoucherQry) ObjectProducer.getObject(QueryConstants.VOMS_VOUCHER_QRY,QueryConstants.QUERY_PRODUCER);

				String updateQuery=vvomsVoucherQry.loadVomsVoucherByMasterSerialNumber(masterSerialNo);
				if(_log.isDebugEnabled())_log.debug(methodName,"select query:"+updateQuery );
				try(PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQuery);)
				{
					pstmtUpdate.setString(i++,masterSerialNo);
					pstmtUpdate.setString(i++,networkCode);
					try(ResultSet rs= pstmtUpdate.executeQuery();)
					{
						while(rs.next())
						{
							vomsVoucher=new VomsVoucherVO();
							vomsVoucher.set_fromSerialNo(rs.getString("SERIAL_NO"));
							vomsVoucher.setProductID(rs.getString("PRODUCT_ID"));
							vomsVoucher.setCurrentStatus(rs.getString("CURRENT_STATUS"));
							vomsVoucher.setProductionLocationCode(rs.getString("PRODUCTION_NETWORK_CODE"));
							vomsVoucher.setUserLocationCode(rs.getString("USER_NETWORK_CODE"));
							vomsVoucher.setMRP(rs.getDouble("mrp"));
							vomsVoucher.setGenerationBatchNo(rs.getString("GENERATION_BATCH_NO"));
							vomsVoucher.set_batch_no(rs.getString("GENERATION_BATCH_NO"));
							vomsVoucher.setEnableBatchNo(rs.getString("ENABLE_BATCH_NO"));
							vomsVoucher.setExpiryDateStr(BTSLUtil.getDateStringFromDate(rs.getDate("expiry_date")));
							vomsVoucher.setPrevStatusModifiedOn(BTSLUtil.getDateStringFromDate(rs.getDate("modified_on")));
							vomsVoucherVOList.add(vomsVoucher);
						}
					}
				}
			}
			catch (SQLException sqle)
			{
				_log.error(methodName,"SQLException "+sqle.getMessage());
				vomsVoucherVOList=null;
			}//end of catch
			catch (Exception e)
			{
				_log.error(methodName,"Exception "+e.getMessage());
				vomsVoucherVOList=null;
			}//end of catch
			finally
			{

				if(_log.isDebugEnabled())_log.debug(methodName,"Exiting");
				if(vomsVoucherVOList.size()==0)
				{
					vomsVoucherVOList = null;
				}
			}//end of finally
			return vomsVoucherVOList;
		}
		
		public ArrayList<CardGroupDetailsVO> returnVoucherDetailsWithCount (Connection con, String channelUserID, String[]voucherType, String networkId) throws BTSLBaseException
		{
			final String methodName = "returnVoucherDetailsWithCount";
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
			StringBuilder loggerValue= new StringBuilder();
			ArrayList<CardGroupDetailsVO> response = new ArrayList<CardGroupDetailsVO>();
			StringBuffer sqlBuffer = null;
	
			if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	for(int i =0; i<voucherType.length; i++)
	        	{
	        		loggerValue.append("Entered: voucherType"+i+ " =");
		        	loggerValue.append(voucherType);
	        	}
	        	loggerValue.append(" channelUserId=");
	        	loggerValue.append(channelUserID);
	        	loggerValue.append(" networkId=");
	        	loggerValue.append(networkId);
	        	_log.debug(methodName, loggerValue);
	        }
			
			try {
			if(voucherType.length ==1)
			{
				sqlBuffer = new StringBuffer("SELECT product_id, mrp, VOUCHER_SEGMENT,vv.voucher_type,vt.name, count(*) AS available "
						+ "FROM voms_vouchers vv, voms_types vt WHERE vv.status = 'EN' AND vv.voucher_type = vt.voucher_type AND vv.user_id = ? AND vv.EXPIRY_DATE >= ? AND vv.SUBSCRIBER_ID IS NULL and vv.user_network_code = ?"
						+ "and vv.voucher_type = ?"		
						+ "GROUP BY vv.voucher_type, vv.VOUCHER_SEGMENT, vv.mrp, vv.product_id, vt.name");
			}
			else if(voucherType.length ==0)
			{
				sqlBuffer = new StringBuffer("SELECT product_id, mrp, VOUCHER_SEGMENT,vv.voucher_type,vt.name, count(*) AS available "
						+ "FROM voms_vouchers vv, voms_types vt WHERE vv.status = 'EN' AND vv.user_id = ? AND vv.EXPIRY_DATE >= ? AND vv.SUBSCRIBER_ID IS NULL and vv.user_network_code = ?"	
						+ "AND vv.voucher_type = vt.voucher_type"
						+ " GROUP BY vv.voucher_type, vv.VOUCHER_SEGMENT, vv.mrp, vv.product_id, vt.name");
			}
			else
			{
				sqlBuffer = new StringBuffer("SELECT product_id, mrp, VOUCHER_SEGMENT,vv.voucher_type,vt.name ,count(*) AS available "
						+ "FROM voms_vouchers vv, voms_types vt WHERE vv.status = 'EN' AND vv.voucher_type = vt.voucher_type AND vv.user_id = ? AND vv.EXPIRY_DATE >= ? AND vv.SUBSCRIBER_ID IS NULL and vv.user_network_code = ?"
						+ "and ( vv.voucher_type = ? ");
						for(int i =1; i<voucherType.length;i++)
						{
							sqlBuffer.append("or vv.voucher_type = ?");
						}
						sqlBuffer.append(")");
						sqlBuffer.append("GROUP BY vv.voucher_type, vv.VOUCHER_SEGMENT, vv.mrp, vv.product_id,vt.name");
			}
				final String sqlSelect = sqlBuffer.toString();
				
				if(_log.isDebugEnabled()){
					loggerValue.setLength(0);
					loggerValue.append("Query =");
					loggerValue.append(sqlSelect);
					_log.debug(methodName, loggerValue);
				}
				
				pstmt = con.prepareStatement(sqlSelect);
				int i = 0;
				pstmt.setString(++i, channelUserID);
				pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(new java.util.Date()));
				pstmt.setString(++i, networkId);
				if(voucherType.length >0)
				{
					pstmt.setString(++i, voucherType[0]);
				}
				if(voucherType.length >1)
				{
					for(int j = 1;j<voucherType.length;j++)
					{
						pstmt.setString(++i, voucherType[j]);
					}
				}
				
				rs = pstmt.executeQuery();
				VomsProductDAO vomsProductDAO = new VomsProductDAO();
				while (rs.next()) 
				{
					CardGroupDetailsVO cardGroupDetailsVO = new CardGroupDetailsVO();
					/*if(voucherType.length >0)
					{
						cardGroupDetailsVO.setVoucherType("Digital");
					}
					else
					{
						cardGroupDetailsVO.setVoucherType(rs.getString("name"));
					}*/
					cardGroupDetailsVO.setVoucherType(rs.getString("name"));
					cardGroupDetailsVO.setVoucherTypeDesc(SqlParameterEncoder.encodeParams(rs.getString("voucher_type")));
					cardGroupDetailsVO.setVoucherSegment(SqlParameterEncoder.encodeParams(rs.getString("voucher_segment")));
					cardGroupDetailsVO.setVoucherSegmentDesc(SqlParameterEncoder.encodeParams(BTSLUtil.getSegmentDesc(rs.getString("voucher_segment"))));
					cardGroupDetailsVO.setVoucherDenomination(SqlParameterEncoder.encodeParams(String.valueOf(Integer.parseInt(rs.getString("mrp"))/100)));
					cardGroupDetailsVO.setVoucherProductId(SqlParameterEncoder.encodeParams(rs.getString("product_id")));
					cardGroupDetailsVO.setAvailableVouchers(SqlParameterEncoder.encodeParams(rs.getString("available")));
					cardGroupDetailsVO.setProductName(vomsProductDAO.getProductName(con,SqlParameterEncoder.encodeParams(rs.getString("product_id"))));
					
					response.add(cardGroupDetailsVO);
				}
				
			}
			catch (SQLException sqe) {
				loggerValue.setLength(0);
				loggerValue.append("SQL_EXCEPTION");
				loggerValue.append(sqe.getMessage());
				_log.error(methodName, loggerValue);
				_log.errorTrace(methodName, sqe);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByParentIDRecursive]", "", "",
						"", "SQL Exception:" + sqe.getMessage());
				throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
			}
			catch (Exception ex) {
				loggerValue.setLength(0);
				loggerValue.append("EXCEPTION");
				loggerValue.append(ex.getMessage());
				_log.error(methodName, loggerValue);
				_log.errorTrace(methodName, ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByParentIDRecursive]", "", "",
						"", "Exception:" + ex.getMessage());
				throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
			}
			finally {
				try {
	                if (rs != null) {
	                	rs.close();
	                }
	            } catch (SQLException e) {
	            	_log.errorTrace(methodName, e);
	            } catch (Exception e) {
	            	_log.errorTrace(methodName, e);
	            }
				try {
	                if (pstmt != null) {
	                	pstmt.close();
	                }
	            } catch (SQLException e) {
	            	_log.errorTrace(methodName, e);
	            } catch (Exception e) {
	            	_log.errorTrace(methodName, e);
	            }
				if (_log.isDebugEnabled()) {
					loggerValue.setLength(0);
					loggerValue.append("Exiting Array List Size:");
					loggerValue.append(response.size());
					_log.debug(methodName, loggerValue);
				}
			}
			return response;
			
		}
		
		/**
		 * @param prefKey
		 * @return
		 */
		public String getDVDOrderByParams(String prefKey) {
			final String methodName = "getDVDOrderByParams";
	    	if(_log.isDebugEnabled())
	    		_log.debug(methodName," Entered preference :" + prefKey);
	    	
			 StringBuilder orderBy = new StringBuilder();
			 HashSet<String> prefCodes = new HashSet<String>();
			  List<String> prefList = Arrays.asList(prefKey.split(","));
			  prefCodes.add("EXPIRY_DATE");
			  prefCodes.add("CREATED_ON");
			  prefCodes.add("SERIAL_NO");
			  for(String pref : prefList){
				  if(prefCodes.contains(pref.trim())) {
					  	if(!BTSLUtil.isEmpty(orderBy.toString())) {
					  		orderBy.append(",");
					  	}
					  	orderBy.append(pref.trim());
				  }
				  else{
					  orderBy.setLength(0);
					  break;
				  }
			  
			  }
			  if(BTSLUtil.isEmpty(orderBy.toString())){
				  orderBy.append("EXPIRY_DATE , CREATED_ON, SERIAL_NO");
			  }
			  if(_log.isDebugEnabled())
				  _log.debug(methodName,"Exiting preference :" + prefKey);
			 return orderBy.toString();
	  }

	  public int updateVoucherProductId(Connection p_con, RequestVO p_requestVO, VomsVoucherVO p_vomsVoucherVO, VomsProductVO p_vomsProductVO,
				boolean flag) throws BTSLBaseException {
			final String methodName = "updateVoucherProductId";

			if (_log.isDebugEnabled()) {
				StringBuffer enteredString = new StringBuffer("");
				if (!flag)
					enteredString.append("p_serialNo =" + p_vomsVoucherVO.getSerialNo() + ", ");
				else
					enteredString.append("p_masterSerialNo =" + p_vomsVoucherVO.getSerialNo() + ", ");
				enteredString.append("p_productId =" + p_vomsProductVO.getProductID() + ", ");
				enteredString.append("p_requestVO =" + p_requestVO.toString());
				_log.debug(methodName, " Entered with " + enteredString.toString());
			}

			int updateCount = 0;

			try {
				int i = 1;
				String extTXNID = null;
				if(p_requestVO.getRequestMap().get("EXTXNID")!=null)
					extTXNID = (String)p_requestVO.getRequestMap().get("EXTXNID");
				Date currDate = new Date();
				StringBuffer updateQueryBuff = new StringBuffer("update voms_vouchers set prev_product_id=product_id,product_id=?,expiry_date=?,consume_before=?,");
				updateQueryBuff.append("talktime=?,validity=?,mrp=?,voucher_type=?,previous_status=current_status,current_status=?,status=?,info5=?,modified_on=?,modified_by=?");
				if(p_requestVO.getRequestMap().containsKey("INFO1") && !(BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("INFO1"))))
					updateQueryBuff.append(",info1=? ");
				if(p_requestVO.getRequestMap().containsKey("INFO3") && !(BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("INFO3"))))
					updateQueryBuff.append(",info3=? ");
				if(p_requestVO.getRequestMap().containsKey("INFO4") && !(BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("INFO4"))))
					updateQueryBuff.append(",info4=? ");
				if(!BTSLUtil.isNullString(extTXNID))
					updateQueryBuff.append(" , EXT_TRANSACTION_ID=? ");
				if (!flag)
					updateQueryBuff.append("where SERIAL_NO=? ");
				else
					updateQueryBuff.append("where master_serial_no=? ");
				updateQueryBuff.append("and current_status <> ? ");
				String updateQuery = updateQueryBuff.toString();
				if (_log.isDebugEnabled())
					_log.debug(methodName, "update query:" + updateQuery);
				String[] info5 = p_vomsVoucherVO.getInfo5().split("#");
				try (PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQuery);) {
					pstmtUpdate.setString(i++, p_vomsProductVO.getProductID());
					pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_vomsProductVO.getExpiryDate()));
					pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_vomsProductVO.getExpiryDate()));
					pstmtUpdate.setDouble(i++, p_vomsProductVO.getTalkTime());
					pstmtUpdate.setLong(i++, p_vomsProductVO.getValidity());
					pstmtUpdate.setDouble(i++,p_vomsProductVO.getMrp());
					pstmtUpdate.setString(i++,p_vomsProductVO.getVoucherType());
					pstmtUpdate.setString(i++, VOMSI.VOUCHER_ENABLE);
					pstmtUpdate.setString(i++, VOMSI.VOUCHER_ENABLE);
					pstmtUpdate.setString(i++, p_vomsProductVO.getShortName()+"#"+p_vomsProductVO.getProductName()+"#"+info5[2]+"#"+info5[3]+"#"+p_vomsProductVO.getValidity()+"#"+p_vomsProductVO.getItemCode());
					pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currDate));
					pstmtUpdate.setString(i++, ((ChannelUserVO)(p_requestVO.getSenderVO())).getUserID());
					if(p_requestVO.getRequestMap().containsKey("INFO1") && !(BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("INFO1"))))
						pstmtUpdate.setString(i++, (String) p_requestVO.getRequestMap().get("INFO1"));
					if(p_requestVO.getRequestMap().containsKey("INFO3") && !(BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("INFO3"))))
						pstmtUpdate.setString(i++, (String) p_requestVO.getRequestMap().get("INFO3"));
					if(p_requestVO.getRequestMap().containsKey("INFO4") && !(BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("INFO4"))))
						pstmtUpdate.setString(i++, (String) p_requestVO.getRequestMap().get("INFO4"));
					if(!BTSLUtil.isNullString(extTXNID))
						pstmtUpdate.setString(i++, extTXNID);
					pstmtUpdate.setString(i++, p_vomsVoucherVO.getSerialNo());
					pstmtUpdate.setString(i++, PretupsI.VOUCHER_CONSUMED_STATUS);
					
					updateCount = pstmtUpdate.executeUpdate();
				}
			} catch (SQLException sqle) {
				_log.error(methodName, "SQLException " + sqle.getMessage());
			} // end of catch
			catch (Exception e) {
				_log.error(methodName, "Exception " + e.getMessage());
			} // end of catch
			finally {

				if (_log.isDebugEnabled())
					_log.debug(methodName, "Exiting updateCount ="+updateCount);
			} // end of finally
			return updateCount;
		}

public ArrayList loadVouchersListForTxnID(Connection p_con, String p_transferID,ArrayList p_voucherList) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadVouchersListForTxnID", " Entered voucher p_transferID=" + p_transferID);
        }
        PreparedStatement dbPs = null;
        ResultSet rs = null;
        VomsVoucherVO voucherVO = null;
        final String methodName = "loadVouchersListForTxnID";        
        try {

		StringBuffer sqlSelectBuf = new StringBuffer(" SELECT v.serial_no SERIALNO,v.current_status CURRENTSTAT,v.previous_status PREVSTAT,v.status STAT,PIN_NO,v.user_id USERID,v.product_id PID,v.MRP,v.EXPIRY_DATE,ct.PMT_INST_NO,ct.EXT_TXN_NO,p.PRODUCT_SHORT_CODE,v.bundle_id,v.master_serial_no,v.created_date,v.last_batch_no,v.consume_before ");
        	sqlSelectBuf.append(" FROM voms_vouchers v, channel_transfers ct, products p ");
        	sqlSelectBuf.append(" WHERE ct.TRANSFER_ID = ? ");
        	sqlSelectBuf.append(" AND ct.transfer_id = v.last_transaction_id ");
        	sqlSelectBuf.append(" AND ct.PRODUCT_TYPE = p.PRODUCT_TYPE ");

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Select Query=" + sqlSelectBuf.toString());
            }
            dbPs = p_con.prepareStatement(sqlSelectBuf.toString());
            dbPs.setString(1, p_transferID);
            
            rs = dbPs.executeQuery();

            while (rs.next()) {
                voucherVO = new VomsVoucherVO();
                voucherVO.setSerialNo(rs.getString("SERIALNO"));
                voucherVO.setPreviousStatus(rs.getString("PREVSTAT"));
                voucherVO.setCurrentStatus(rs.getString("CURRENTSTAT"));
                voucherVO.setVoucherStatus(rs.getString("STAT"));
                voucherVO.setPinNo(rs.getString("PIN_NO"));
                voucherVO.setUserID(rs.getString("USERID"));
                voucherVO.setProductID(rs.getString("PID"));
		voucherVO.setMRP(rs.getDouble("MRP"));
		voucherVO.setExpiryDate(rs.getDate("EXPIRY_DATE"));
		voucherVO.setInfo1(rs.getString("PMT_INST_NO"));
                voucherVO.setInfo2(rs.getString("EXT_TXN_NO"));
                voucherVO.setInfo3(rs.getString("PRODUCT_SHORT_CODE"));
		voucherVO.setBundleId(rs.getLong("Bundle_ID"));
		voucherVO.setMasterSerialNo(rs.getLong("MASTER_SERIAL_NO"));
		voucherVO.setCreatedOn(rs.getDate("created_date"));
                voucherVO.setConsumeBeforeStr(String.valueOf(rs.getDate("CONSUME_BEFORE")));
                voucherVO.set_batch_no(rs.getString("last_batch_no"));
                p_voucherList.add(voucherVO);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "After executing the query loadVomsVoucherVO method p_voucherList.size=" + p_voucherList.size());
            }
            return p_voucherList;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadVouchersListForTxnID]", "", "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadVomsVoucherVO", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadVouchersListForTxnID]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.error(methodName, " Exception while closing rs ex=" + ex);
            }
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.error(methodName, " Exception while closing prepared statement ex=" + ex);
            }
            try {
                _log.debug(methodName, " Exiting.. p_voucherList.size=" + p_voucherList.size());
            } catch (Exception e) {
                _log.error(methodName, " Exception while closing rs ex=" + e);
            }
         }
    }
    
	 /**
	 * @param pcon
	 * @param p_fromSerialNo
	 * @param p_toSerialNo
	 * @return
	 * @throws BTSLBaseException
	 */
	public ArrayList<VomsVoucherVO> loadVoucherData(Connection pcon, String p_fromSerialNo, String p_toSerialNo) throws BTSLBaseException {
    	final String METHOD_NAME = "loadVoucherData";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");        	  	
        	msg.append("p_fromSerialNo=");
        	msg.append(p_fromSerialNo);
        	msg.append("p_toSerialNo=");
        	msg.append(p_toSerialNo);
        	String message=msg.toString();
            _log.debug(METHOD_NAME, message);
        }
        PreparedStatement pselect = null;
        ResultSet rs = null;
        ArrayList voucherList = null;
        VomsVoucherVO voucherVO = new VomsVoucherVO();        
       
        try {

        	StringBuffer sqlSelectBuf = new StringBuffer(" SELECT SERIAL_NO,CURRENT_STATUS,EXPIRY_DATE,LAST_TRANSACTION_ID,STATUS,CREATED_DATE ");
        	sqlSelectBuf.append(" ,MRP,USER_ID, VOUCHER_TYPE, BUNDLE_ID, MASTER_SERIAL_NO, INFO1, VOUCHER_SEGMENT ");
        	sqlSelectBuf.append(" FROM VOMS_VOUCHERS ");
			sqlSelectBuf.append(" WHERE SERIAL_NO BETWEEN ? AND ? ");
			
			
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Select Query=" + sqlSelectBuf);
            }
            pselect = pcon.prepareStatement(sqlSelectBuf.toString());
            int i = 0;
            
            pselect.setLong(++i, Long.parseLong(p_fromSerialNo));            
            pselect.setLong(++i, Long.parseLong(p_toSerialNo));
		    
            rs = pselect.executeQuery();
            voucherList = new ArrayList();
            while (rs.next()) {
            	voucherVO = new VomsVoucherVO();
            	voucherVO.setSerialNo(rs.getString("SERIAL_NO"));
            	voucherVO.setCurrentStatus(rs.getString("CURRENT_STATUS"));
            	voucherVO.setExpiryDate(rs.getDate("EXPIRY_DATE"));
            	voucherVO.setTransactionID(rs.getString("LAST_TRANSACTION_ID"));
            	voucherVO.setStatus(rs.getString("STATUS"));
            	voucherVO.setCreatedOn(rs.getDate("CREATED_DATE"));
            	voucherVO.setMRP(rs.getDouble("MRP"));
            	voucherVO.setUserID(rs.getString("USER_ID"));
            	voucherVO.setVoucherType(rs.getString("VOUCHER_TYPE"));
            	voucherVO.setBundleId(rs.getLong("BUNDLE_ID"));
            	voucherVO.setMasterSerialNo(rs.getLong("MASTER_SERIAL_NO"));
            	voucherVO.setInfo1(rs.getString("INFO1"));
            	voucherVO.setVoucherSegment(rs.getString("VOUCHER_SEGMENT"));
            	
                voucherList.add(voucherVO);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "voucherlist" + voucherList.size());
            }
            return voucherList;
        } catch (SQLException sqle) {
            _log.errorTrace(METHOD_NAME, sqle);
            _log.error(METHOD_NAME, PretupsI.SQLEXCEPTION + sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadVoucherData]", "", "", "",
                "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, PretupsI.EXCEPTION + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadVoucherData]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.error(METHOD_NAME, " Exception while closing rs ex=" + ex);
            }
            try {
                if (pselect != null) {
                    pselect.close();
                }
            } catch (Exception ex) {
                _log.error(METHOD_NAME, " Exception while closing prepared statement ex=" + ex);
            }
        }
    }
	
	/**
	 * @param p_con
	 * @return
	 * @throws BTSLBaseException
	 */
	public ArrayList<VomsBatchExpiryVO> loadChangeVoucherExpiryList(Connection p_con)
			throws BTSLBaseException {
		final String methodName = "loadChangeVoucherExpiryList";
		if (_log.isDebugEnabled())
			_log.debug(methodName, PretupsI.ENTERED);

		VomsBatchExpiryVO vomsBatchExpiryVO = null;
		ArrayList<VomsBatchExpiryVO> list = null;
		try {
			int i = 1;
			list = new ArrayList<>();
			StringBuffer selectQueryBuff = new StringBuffer(
					"select BATCH_ID,FROM_SERIAL_NUMBER,TO_SERIAL_NUMBER,TOTAL_VOUCHER_COUNT,SUCCESS_COUNT,FAILURE_COUNT,EXECUTION_STATUS, ");
			selectQueryBuff.append("EXPIRY_DATE,CREATED_ON,CREATED_BY,MODIFIED_ON,MODIFIED_BY,FILENAME,STATUS,VOUCHER_TYPE ");
			selectQueryBuff.append("from voms_batches_expiry ");
			selectQueryBuff.append("where EXECUTION_STATUS = ? ");
			selectQueryBuff.append("and STATUS = ? ");
			selectQueryBuff.append("and FILENAME IS NOT NULL ");

			String selectQuery = selectQueryBuff.toString();
			if (_log.isDebugEnabled())
				_log.debug(methodName, "select query:" + selectQuery);
			try (PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {
				pstmtSelect.setString(i++, VOMSI.SCHEDULED);
				pstmtSelect.setString(i++, VOMSI.BATCH_ACCEPTED);
				try (ResultSet rs = pstmtSelect.executeQuery();) {
					while (rs.next()) {
						vomsBatchExpiryVO = new VomsBatchExpiryVO();
						vomsBatchExpiryVO.setBatchNo(rs.getString("BATCH_ID"));
						vomsBatchExpiryVO.setFromSerialNo(rs.getString("FROM_SERIAL_NUMBER"));
						vomsBatchExpiryVO.setToSerialNo(rs.getString("TO_SERIAL_NUMBER"));
						vomsBatchExpiryVO.setNoOfVoucher(rs.getLong("TOTAL_VOUCHER_COUNT"));
						vomsBatchExpiryVO.setSuccessCount(rs.getLong("SUCCESS_COUNT"));
						vomsBatchExpiryVO.setFailCount(rs.getLong("FAILURE_COUNT"));
						vomsBatchExpiryVO.setExecutionStatus(rs.getString("EXECUTION_STATUS"));
						vomsBatchExpiryVO.setExpiryDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("EXPIRY_DATE")));
						vomsBatchExpiryVO.setCreatedOn(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("CREATED_ON")));
						vomsBatchExpiryVO.setCreatedBy(rs.getString("CREATED_BY"));
						vomsBatchExpiryVO.setModifiedOn(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("MODIFIED_ON")));
						vomsBatchExpiryVO.setModifiedBy(rs.getString("MODIFIED_BY"));
						vomsBatchExpiryVO.setFilename(rs.getString("FILENAME"));
						vomsBatchExpiryVO.setStatus(rs.getString("STATUS"));
						vomsBatchExpiryVO.setVoucherType(rs.getString("VOUCHER_TYPE"));
						list.add(vomsBatchExpiryVO);
					}
				}
			}
		} catch (SQLException sqle) {
			_log.error(methodName, PretupsI.SQLEXCEPTION + sqle.getMessage());
			vomsBatchExpiryVO = null;
		} // end of catch
		catch (Exception e) {
			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			vomsBatchExpiryVO = null;
		} // end of catch
		finally {

			if (_log.isDebugEnabled())
				_log.debug(methodName, PretupsI.EXITED_VALUE+" list size = " + list.size());
		} // end of finally
		return list;
	}


    /**
     * @param p_con
     * @param p_vomsBatchExpiryVO
     * @return
     * @throws BTSLBaseException
     */
    public int insertVomsBatchesExpiry(Connection p_con, VomsBatchExpiryVO p_vomsBatchExpiryVO)
			throws BTSLBaseException {
		final String methodName = "insertVomsBatchesExpiry";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,   PretupsI.ENTERED_VALUE+"p_vomsVoucherVO =" + p_vomsBatchExpiryVO.toString());
		}
		int addCount = 0;
		PreparedStatement pst = null;
		StringBuffer insertQryBuff = new StringBuffer(
				"insert into voms_batches_expiry (batch_id, from_serial_number, to_serial_number, ");
		insertQryBuff.append(
				"total_voucher_count, success_count, failure_count, execution_status, expiry_date, created_on, created_by, modified_on, modified_by, status, filename,voucher_type ) ");
		insertQryBuff.append("values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

		try {
			String insertQry = insertQryBuff.toString();
			if (_log.isDebugEnabled())
				_log.debug(methodName, "Insert Query = " + insertQry);
			int i = 1;
			pst = p_con.prepareStatement(insertQry);
			pst.setString(i++, p_vomsBatchExpiryVO.getBatchNo());
			pst.setString(i++, p_vomsBatchExpiryVO.getFromSerialNo());
			pst.setString(i++, p_vomsBatchExpiryVO.getToSerialNo());
			pst.setLong(i++, p_vomsBatchExpiryVO.getNoOfVoucher());
			pst.setLong(i++, p_vomsBatchExpiryVO.getSuccessCount());
			pst.setLong(i++, p_vomsBatchExpiryVO.getFailCount());
			pst.setString(i++, p_vomsBatchExpiryVO.getExecutionStatus());
			pst.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_vomsBatchExpiryVO.getExpiryDate()));
			pst.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_vomsBatchExpiryVO.getCreatedOn()));
			pst.setString(i++, p_vomsBatchExpiryVO.getCreatedBy());
			pst.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_vomsBatchExpiryVO.getModifiedOn()));
			pst.setString(i++, p_vomsBatchExpiryVO.getModifiedBy());
			pst.setString(i++, p_vomsBatchExpiryVO.getStatus());
			pst.setString(i++, p_vomsBatchExpiryVO.getFilename());
			pst.setString(i++, p_vomsBatchExpiryVO.getVoucherType());

			addCount = pst.executeUpdate();

			if (addCount <= 0) {
				_log.error(methodName,
						" Not able to insert record for Serial No" + p_vomsBatchExpiryVO.getFromSerialNo());
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL, "VomsVoucherDAO[insertVomsBatchesExpiry]", "", "", "",
						"Not able to insert record for Serial No" + p_vomsBatchExpiryVO.getFromSerialNo());
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
			}

		}
		//
		catch (BTSLBaseException be) {
			try {
				p_con.rollback();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			_log.error(methodName, PretupsI.BTSLEXCEPTION + be);
			throw be;
		} catch (Exception ex) {
			try {
				p_con.rollback();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			_log.debug(methodName,PretupsI.EXCEPTION + ex);
			EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
					this.getClass()+methodName, "", "", "",
					"Exception while executing the insertFileValues");
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName,PretupsI.EXITED_VALUE+"addCount = " + addCount);
			}
		}
		return addCount;
	}
	  
	/**
	 * @param p_con
	 * @param p_vomsBatchExpiryVO
	 * @return
	 * @throws BTSLBaseException
	 */
	public HashMap<String,ArrayList<String>> changeVoucherExpiryDate(Connection p_con,VomsBatchExpiryVO p_vomsBatchExpiryVO)throws BTSLBaseException
	{
		final String methodName = "changeVoucherExpiryDate";
		if (_log.isDebugEnabled())
			_log.debug(methodName,
					PretupsI.ENTERED_VALUE+"p_con = " + p_con + " p_vomsVoucherVO = " + p_vomsBatchExpiryVO.toString());
		PreparedStatement pstmtUpdate = null;
		ResultSet rs = null;
		StringBuffer updateQueryBuff = new StringBuffer(
				" UPDATE VOMS_VOUCHERS SET PRE_EXPIRY_DATE=EXPIRY_DATE, EXPIRY_DATE=?, INFO1=?, MODIFIED_ON=?, MODIFIED_BY=? ");
		updateQueryBuff.append("WHERE SERIAL_NO = ? and CURRENT_STATUS NOT IN (?,?,?)");
		String updateQuery = updateQueryBuff.toString();
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, " UPDATE QUERY = " + updateQuery);
		}
		Long currentSerialNo = 0L;
		String fromSerialNo = p_vomsBatchExpiryVO.getFromSerialNo();
		String toSerialNo = p_vomsBatchExpiryVO.getToSerialNo();
		if (!(BTSLUtil.isNullString(fromSerialNo)))
			currentSerialNo = Long.parseLong(fromSerialNo);
		Date expiryDate = p_vomsBatchExpiryVO.getExpiryDate();
		ArrayList<String> successList = new ArrayList<>();
		ArrayList<String> failureList = new ArrayList<>();
		HashMap<String, ArrayList<String>> map = new HashMap<>();
		try {
			pstmtUpdate = p_con.prepareStatement(updateQuery);
			while (currentSerialNo <= Long.parseLong(toSerialNo)) {
				int i = 1;
				int count = 0;
				if (_log.isDebugEnabled()) {
					_log.debug(methodName, " currentSerialNo = " + currentSerialNo);
				}
				pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(expiryDate));
				pstmtUpdate.setString(i++, p_vomsBatchExpiryVO.getBatchNo());
				pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_vomsBatchExpiryVO.getModifiedOn()));
				pstmtUpdate.setString(i++, p_vomsBatchExpiryVO.getModifiedBy());
				pstmtUpdate.setString(i++, currentSerialNo + "");
				pstmtUpdate.setString(i++, VOMSI.VOUCHER_USED);
				pstmtUpdate.setString(i++, VOMSI.VOUCHER_STOLEN);
				pstmtUpdate.setString(i++, VOMSI.VOUCHER_DAMAGED);
				if (expiryDate.compareTo(new Date()) > 0) {
					count = pstmtUpdate.executeUpdate();
				}
				if (count > 0) {
					successList.add(currentSerialNo + "");
				} else {
					failureList.add(currentSerialNo + "");
				}
				currentSerialNo++;
			}

			map.put("successList", successList);
			map.put("failureList", failureList);
		} catch (SQLException sqle) {
			_log.error(methodName, PretupsI.SQLEXCEPTION + sqle.getMessage());
		} // end of catch
		catch (Exception e) {
			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
		} // end of catch
		finally {
			try {
				if (pstmtUpdate != null)
					pstmtUpdate.close();
			} catch (Exception e) {
				_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
				_log.errorTrace(methodName, e);
			}
			if (_log.isDebugEnabled())
				_log.debug(methodName, PretupsI.EXITED);
		} // end of finally
		return map;
	}
	  
	/**
	 * @param p_con
	 * @param p_voList
	 * @param p_batchNo
	 * @return
	 * @throws BTSLBaseException
	 */
	public HashMap<String, ArrayList<String>> changeVoucherExpiryDate(Connection p_con,
			ArrayList<VomsVoucherVO> p_voList, String p_batchNo) throws BTSLBaseException {
		final String methodName = "changeVoucherExpiryDate";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}
		PreparedStatement pstmtUpdate = null;
		ArrayList<String> successList = new ArrayList<>();
		ArrayList<String> failureList = new ArrayList<>();
		HashMap<String, ArrayList<String>> map = new HashMap<>();
		try {
			int i = 1;
			StringBuffer updateQueryBuff = new StringBuffer(
					"UPDATE VOMS_VOUCHERS SET PRE_EXPIRY_DATE=EXPIRY_DATE,EXPIRY_DATE=?,INFO1=?,MODIFIED_ON=?,MODIFIED_BY=? ");
			updateQueryBuff.append("WHERE SERIAL_NO = ? and CURRENT_STATUS NOT IN (?,?,?)");
			String updateQuery = updateQueryBuff.toString();
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Update query:" + updateQuery);
			}
			pstmtUpdate = p_con.prepareStatement(updateQuery);
			for (VomsVoucherVO vomsVoucherVO : p_voList) {
				i = 1;
				int updateCount = 0;
				pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(vomsVoucherVO.getExpiryDate()));
				pstmtUpdate.setString(i++, p_batchNo);
				pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new Date()));
				pstmtUpdate.setString(i++, PretupsI.SYSTEM);
				pstmtUpdate.setString(i++, vomsVoucherVO.getSerialNo());
				pstmtUpdate.setString(i++, VOMSI.VOUCHER_USED);
				pstmtUpdate.setString(i++, VOMSI.VOUCHER_STOLEN);
				pstmtUpdate.setString(i++, VOMSI.VOUCHER_DAMAGED);
				if (vomsVoucherVO.getExpiryDate().compareTo(new Date()) > 0) {
					updateCount = pstmtUpdate.executeUpdate();
				}
				if (updateCount > 0) {
					successList.add(vomsVoucherVO.getSerialNo());
				} else {
					failureList.add(vomsVoucherVO.getSerialNo());
				}
			}
			map.put("successList", successList);
			map.put("failureList", failureList);
			return map;
		} catch (SQLException sqle) {
			_log.error(methodName,PretupsI.SQLEXCEPTION + sqle.getMessage());
			_log.errorTrace(methodName, sqle);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			_log.error(methodName,PretupsI.EXCEPTION + e.getMessage());
			_log.errorTrace(methodName, e);
			throw new BTSLBaseException(this,methodName, "error.general.processing");
		} finally {
			try {
				if (pstmtUpdate != null) {
					pstmtUpdate.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName,
						PretupsI.EXITED_VALUE+"successListSize=" + successList.size() + " ,failureListSize=" + failureList.size());
			}
		}
	}
	/**
	 * @param p_con
	 * @param p_vomsBatchExpiryVO
	 * @return
	 * @throws BTSLBaseException
	 */
	public int updateVomsBatchesExpiry(Connection p_con, VomsBatchExpiryVO p_vomsBatchExpiryVO)
			throws BTSLBaseException {
		final String methodName = "updateVomsBatchesExpiry";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED_VALUE+"p_vomsVoucherVO = " + p_vomsBatchExpiryVO.toString() + " and batchID = "
					+ p_vomsBatchExpiryVO.getBatchNo());
		}
		PreparedStatement pstmtUpdate = null;
		int updateCount = 0;

		try {
			int i = 1;
			StringBuffer updateQueryBuff = new StringBuffer("UPDATE voms_batches_expiry SET ");
			updateQueryBuff.append(
					"success_count=?, failure_count=?, modified_on=?, modified_by=?, execution_status=?, status=? ");
			updateQueryBuff.append("WHERE batch_id=? ");
			String updateQuery = updateQueryBuff.toString();
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Update query:" + updateQuery);
			}
			pstmtUpdate = p_con.prepareStatement(updateQuery);
			pstmtUpdate.setLong(i++, p_vomsBatchExpiryVO.getSuccessCount());
			pstmtUpdate.setLong(i++, p_vomsBatchExpiryVO.getFailCount());
			pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_vomsBatchExpiryVO.getModifiedOn()));
			pstmtUpdate.setString(i++, p_vomsBatchExpiryVO.getModifiedBy());
			pstmtUpdate.setString(i++, p_vomsBatchExpiryVO.getExecutionStatus());
			pstmtUpdate.setString(i++, p_vomsBatchExpiryVO.getStatus());
			pstmtUpdate.setString(i++, p_vomsBatchExpiryVO.getBatchNo());
			updateCount = pstmtUpdate.executeUpdate();
			return updateCount;
		} catch (SQLException sqle) {
			_log.error(methodName, PretupsI.SQLEXCEPTION + sqle.getMessage());
			updateCount = 0;
			_log.errorTrace(methodName, sqle);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} // end of catch
		catch (Exception e) {
			_log.error(methodName,PretupsI.EXCEPTION + e.getMessage());
			updateCount = 0;
			_log.errorTrace(methodName, e);
			throw new BTSLBaseException(this,methodName, "error.general.processing");
		} // end of catch
		finally {
			try {
				if (pstmtUpdate != null) {
					pstmtUpdate.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting updateCount=" + updateCount);
			}
		} // end of finally
	}  
	
	/**
	 * @param p_con
	 * @param p_process_upto
	 * @param p_currentDate
	 * @return
	 * @throws BTSLBaseException
	 */
	public ArrayList<VomsBatchExpiryVO> fetchVomsBatchExpiryDetail(Connection p_con, Date p_process_upto, Date p_currentDate)
			throws BTSLBaseException {
		final String methodName = "fetchVomsBatchExpiryDetail";
		if (_log.isDebugEnabled())
			_log.debug(methodName, PretupsI.ENTERED_VALUE+ "p_process_upto = " + p_process_upto + ", p_currentDate = " + p_currentDate);

		VomsBatchExpiryVO vomsBatchExpiryVO = null;
		ArrayList<VomsBatchExpiryVO> list = null;
		try {
			int i = 1;
			list = new ArrayList<>();
			StringBuffer selectQueryBuff = new StringBuffer(
					"select BATCH_ID,FROM_SERIAL_NUMBER,TO_SERIAL_NUMBER,TOTAL_VOUCHER_COUNT,SUCCESS_COUNT,FAILURE_COUNT,EXECUTION_STATUS, ");
			selectQueryBuff.append("EXPIRY_DATE,CREATED_ON,CREATED_BY,MODIFIED_ON,MODIFIED_BY,FILENAME,STATUS ");
			selectQueryBuff.append("from voms_batches_expiry ");
			selectQueryBuff.append("where EXECUTION_STATUS = ? ");
			selectQueryBuff.append("and STATUS = ? ");
			selectQueryBuff.append("and CREATED_ON > ? ");
			selectQueryBuff.append("and CREATED_ON <= ? ");
			selectQueryBuff.append("and TOTAL_VOUCHER_COUNT > ? ");

			String selectQuery = selectQueryBuff.toString();
			if (_log.isDebugEnabled())
				_log.debug(methodName, "select query:" + selectQuery);
			try (PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {
				pstmtSelect.setString(i++, VOMSI.SCHEDULED);
				pstmtSelect.setString(i++, VOMSI.VOMS_CLOSED_STATUS);
				pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_process_upto));
				pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
				pstmtSelect.setLong(i++, Long.parseLong(Constants.getProperty("VOMS_EXPIRY_VOUCHER_COUNT")));
				try (ResultSet rs = pstmtSelect.executeQuery();) {
					while (rs.next()) {
						vomsBatchExpiryVO = new VomsBatchExpiryVO();
						vomsBatchExpiryVO.setBatchNo(rs.getString("BATCH_ID"));
						vomsBatchExpiryVO.setFromSerialNo(rs.getString("FROM_SERIAL_NUMBER"));
						vomsBatchExpiryVO.setToSerialNo(rs.getString("TO_SERIAL_NUMBER"));
						vomsBatchExpiryVO.setNoOfVoucher(rs.getLong("TOTAL_VOUCHER_COUNT"));
						vomsBatchExpiryVO.setSuccessCount(rs.getLong("SUCCESS_COUNT"));
						vomsBatchExpiryVO.setFailCount(rs.getLong("FAILURE_COUNT"));
						vomsBatchExpiryVO.setExecutionStatus(rs.getString("EXECUTION_STATUS"));
						vomsBatchExpiryVO.setExpiryDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("EXPIRY_DATE")));
						vomsBatchExpiryVO.setCreatedOn(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("CREATED_ON")));
						vomsBatchExpiryVO.setCreatedBy(rs.getString("CREATED_BY"));
						vomsBatchExpiryVO.setModifiedOn(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("MODIFIED_ON")));
						vomsBatchExpiryVO.setModifiedBy(rs.getString("MODIFIED_BY"));
						vomsBatchExpiryVO.setFilename(rs.getString("FILENAME"));
						vomsBatchExpiryVO.setStatus(rs.getString("STATUS"));
						list.add(vomsBatchExpiryVO);
					}
				}
			}
		} catch (SQLException sqle) {
			_log.error(methodName, PretupsI.SQLEXCEPTION + sqle.getMessage());
			vomsBatchExpiryVO = null;
		} // end of catch
		catch (Exception e) {
			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			vomsBatchExpiryVO = null;
		} // end of catch
		finally {

			if (_log.isDebugEnabled())
				_log.debug(methodName,PretupsI.EXITED_VALUE+"list size = " + list.size());
		} // end of finally
		return list;
	}
	

 
 /**
 * @param p_con
 * @return
 * @throws BTSLBaseException
 */
public ArrayList<VomsProductVO> loadBlankProfileList(Connection p_con)
			throws BTSLBaseException {
		final String methodName = "loadBlankProfileList";
		if (_log.isDebugEnabled())
			_log.debug(methodName, PretupsI.ENTERED);

		ArrayList<VomsProductVO> list = new ArrayList<>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sb = null;
		String stringSb = null;
		String blankCategoryId = null;
		VomsProductVO vomsProductVO = null;
		try {
			sb =new StringBuffer("Select category_id from voms_categories where voucher_type=? and mrp=?");
			stringSb = sb.toString();
			if (_log.isDebugEnabled())
				_log.debug(methodName+" select query 1 : ", stringSb);
			ps = p_con.prepareStatement(stringSb);
			ps.setString(1,"BLANK_VOUCHER");
			ps.setLong(2, 100L);
			rs = ps.executeQuery();
			if(rs.next()) {
				blankCategoryId = rs.getString("category_id");
			}
			
			if(! BTSLUtil.isNullString(blankCategoryId)) {
				sb = new StringBuffer("select product_id,product_name,short_name,description,mrp,talktime,validity,item_code,secondary_prefix_code from voms_products where category_id=?");
				stringSb = sb.toString();
				if (_log.isDebugEnabled())
					_log.debug(methodName+" select query 2 : ", stringSb);
				ps = p_con.prepareStatement(stringSb);
				ps.setString(1,blankCategoryId);
				rs = ps.executeQuery();
				while(rs.next()) {
					vomsProductVO = new VomsProductVO();
					vomsProductVO.setProductID(rs.getString("product_id"));
					vomsProductVO.setProductName(rs.getString("product_name"));
					vomsProductVO.setShortName(rs.getString("short_name"));
					vomsProductVO.setDescription(rs.getString("description"));
					vomsProductVO.setMrp(rs.getDouble("mrp"));
					vomsProductVO.setTalkTime(rs.getDouble("talktime"));
					vomsProductVO.setValidity(rs.getLong("validity"));
					vomsProductVO.setItemCode(rs.getString("item_code"));
					vomsProductVO.setSecondaryPrefixCode(rs.getString("secondary_prefix_code"));
					list.add(vomsProductVO);
				}
			}
		} catch (SQLException sqle) {
			_log.error(methodName, PretupsI.SQLEXCEPTION+ sqle.getMessage());
		} // end of catch
		catch (Exception e) {
			_log.error(methodName,PretupsI.EXCEPTION+ e.getMessage());
		} // end of catch
		finally {
			p_con = null;
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (_log.isDebugEnabled())
				_log.debug(methodName,PretupsI.EXITED_VALUE+"list size = " + list.size());
		} // end of finally
		return list;
	}

}
