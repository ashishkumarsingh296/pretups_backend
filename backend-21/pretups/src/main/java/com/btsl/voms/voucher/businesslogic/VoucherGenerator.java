package com.btsl.voms.voucher.businesslogic;

/**
 * @(#)VoucherGenerator.java
 *                           All Rights Reserved
 *                           voucher generator
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Rahul.Dutt 07/05/2012 Initial Creation
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 */
import java.io.File;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EMailSender;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.util.SqlParameterEncoder;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomslogging.VomsBatchInfoLog;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
/**
 * This class will be used for voucher generation of scheduled batches; Creation
 * 
 * @author rahul.dutt
 */
public class VoucherGenerator {

    private static Log _logger = LogFactory.getLog(VoucherGenerator.class.getName());
    private static long starttime = System.currentTimeMillis();
    private static OperatorUtilI _operatorUtil = null;

    public static void main(String args[]) {
        final String METHOD_NAME = "main";
        try {
            if (args.length < 2 || args.length > 3) {
                System.out.println("Usage : VoucherGenerator [Constants file] [LogConfig file] [Y/N]");
                return;
            }
            // load constants.props
            File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                System.out.println("VoucherGenerator: Constants File Not Found .............");
                return;
            }
            // load log config file
            File logFile = new File(args[1]);
            if (!logFile.exists()) {
                System.out.println("VoucherGenerator: Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logFile.toString());
        }// end of try block
        catch (Exception e) {
            System.err.println("Error in Loading Configuration files ...........................: " + e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch block
        try {
            process();
        }// end of try block
        catch (BTSLBaseException be) {
            _logger.errorTrace(METHOD_NAME, be);
            _logger.error("main", "BTSLBaseException : " + be.getMessage());
            return;
        }// end of catch block
        catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("main", " " + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
            return;
        }// end of catch block
        finally {
            VomsBatchInfoLog.log("Total time taken:" + (System.currentTimeMillis() - starttime));
            if (_logger.isDebugEnabled()) {
                _logger.info("main", "Exiting");
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            ConfigServlet.destroyProcessCache();
        }// end of finally
    }

    public static void process() throws BTSLBaseException {
        if (_logger.isDebugEnabled()) {
            _logger.info("process : ", "Entered ");
        }
        final String METHOD_NAME = "process";
        String processId = null;
        ProcessBL processBL = null;
        Connection con = null;
        int beforeInterval = 0;
        ProcessStatusVO processStatusVO = null;
        Date currentDate = null;
        Date processedUpto = null;
        int updateCount = 0; // check process details are updated or not
        Date startdate, endDate = null;
        try {
            processId = ProcessI.VOMS_GEN;
            con = OracleUtil.getSingleConnection();
            processBL = new ProcessBL();
            processStatusVO = processBL.checkProcessUnderProcess(con, processId);
            beforeInterval = BTSLUtil.parseLongToInt( processStatusVO.getBeforeInterval() / (60 * 24));
            if (processStatusVO.isStatusOkBool()) {
                // method call to find maximum date till which process has been
                // executed
                processedUpto = processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    currentDate = BTSLUtil.getTimestampFromUtilDate(new Date());
                    
                    startdate = processedUpto;
               
                    con.commit();
                    processedUpto = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(currentDate));
                    processedUpto = currentDate;
                    // call process for uploading transfer details
                    boolean isDataProcessed = generateVouchers(con, startdate, processedUpto,VOMSI.MANUAL);
                    if (isDataProcessed) {
                        processStatusVO.setExecutedUpto(BTSLUtil.addDaysInUtilDate(processedUpto, -beforeInterval));
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherGenerator[process]", "", "", "", " VoucherGenerator process has been executed successfully.");
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("process", " successfully");
                        }
                    }
                } else {
                    throw new BTSLBaseException("VoucherGenerator", "process", PretupsErrorCodesI.ERROR_VOMS_GEN);
                }
            } else {
                throw new BTSLBaseException("VoucherGenerator", "process", PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
            }
        } catch (BTSLBaseException be) {
            _logger.error("process", "BTSLBaseException : " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _logger.error("process", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherGenerator[process]", "", "", "", " VoucherGenerator process could not be executed successfully.");
            throw new BTSLBaseException("VoucherGenerator", "process", PretupsErrorCodesI.ERROR_VOMS_GEN,e);
        } finally {
            try {
                if (processStatusVO.isStatusOkBool()) {
                    processStatusVO.setStartDate(currentDate);
                    processStatusVO.setExecutedOn(currentDate);
                    //
                    processStatusVO.setExecutedUpto(currentDate);
                    processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    updateCount = (new ProcessStatusDAO()).updateProcessDetail(con, processStatusVO);
                    if (updateCount > 0) {
                        con.commit();
                    }
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", "Exception in closing connection ");
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e1) {
                    _logger.errorTrace(METHOD_NAME, e1);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", "Exiting..... ");
            }
        }
    }

    /**
     * @param p_con
     * @param p_startDate
     * @param p_endDate
     * @return
     * @author rahul.dutt
     *         This method is used to generate voucher pins and serial numbers
     *         based on Accepted batched in DB
     *         and creates vouchers with status GE
     */
    public static boolean generateVouchers(Connection p_con, Date p_startDate, Date p_endDate,String generationType) throws SQLException, BTSLBaseException {
        if (_logger.isDebugEnabled()) {
            _logger.info("generateVouchers", "Entered p_startDate:" + p_startDate + "p_endDate:" + p_endDate +"generationType: "+generationType);
        }
        final String METHOD_NAME = "generateVouchers";
        boolean isDataProcessed = false;
        PreparedStatement pstmtSel = null, psmtInsert = null, psmtupdate = null;
        ;
        PreparedStatement psmtupdate1 = null;
        ResultSet rst = null, rst1 = null;
        ArrayList batchList = null, pinsList = null;
        VomsBatchesDAO vomsVatchesDAO = null;
        VomsBatchVO batchVO = null;
        String product_id = null, activeProductId = "", serialnumbrStr, location = null, category = null, voucher_type = null, table_name = null, segment = null;;
        String sqlSelectUpdate = null, sqlInsert = null;
        long serialNOcounter = 0, startserialnumber, fromserialnum = 0;
        VomsUtil vomsutil = null;
        Date currentdate = new Date();
        Date expiryDate = null;
        int[] updateCount = null;
        VomsVoucherDAO vomsVoucherDao = null;	
        int update = 0;
        VoucherGenerator voucherGenerator = null;
        long successCount = 0;
        long failCount = 0;
        int retryCount = 0;
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {        	
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
        	_logger.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherGenerator[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
        try {
            vomsVatchesDAO = new VomsBatchesDAO();
            if(generationType.equalsIgnoreCase(VOMSI.AUTO))        	
            batchList = vomsVatchesDAO.loadAutoBatchList(p_con, VOMSI.ALL, VOMSI.BATCH_ACCEPTED, VOMSI.BATCH_INTIATED, p_startDate, p_endDate,generationType);
            else
            	batchList = vomsVatchesDAO.loadBatchList(p_con, VOMSI.ALL, VOMSI.BATCH_ACCEPTED, VOMSI.BATCH_INTIATED, p_startDate, p_endDate);
            StringBuffer sqlselect = new StringBuffer();
            sqlselect.append("SELECT DISTINCT  VC.serial_number_counter,VC.category_id, VC.voucher_type ");
            sqlselect.append(" FROM voms_categories VC,voms_products VP WHERE VC.CATEGORY_ID=VP.CATEGORY_ID and VP.PRODUCT_ID=?");
            sqlSelectUpdate = sqlselect.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("generateVouchers", "sqlSelect" + sqlSelectUpdate);
            }
            pstmtSel = p_con.prepareStatement(sqlSelectUpdate);
            vomsutil = new VomsUtil();
            voucherGenerator = new VoucherGenerator();
            long mrp = 0;
            for (int i = 0; i < batchList.size(); i++) {
            	retryCount = 0;
                batchVO = (VomsBatchVO) batchList.get(i);
                product_id = batchVO.getProductID();
                location = batchVO.getLocationCode();
                segment = batchVO.getSegment();
                mrp = PretupsBL.getSystemAmount(batchVO.getMrp());
                
                if(batchVO.getExpiryDate()!=null){
                	expiryDate=batchVO.getExpiryDate();
                	if(expiryDate.before(currentdate)||expiryDate.equals(currentdate))
                	{
                		throw new BTSLBaseException(PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED);
                	}
                }
                else
                expiryDate = BTSLUtil.addDaysInUtilDate(currentdate, batchVO.getExpiryPeriod());
                _logger.info("generateVouchers", "BATCH proceesing started:" + batchVO.getBatchNo() + "product_id" + product_id);
                pstmtSel.setString(1, product_id);
                rst = pstmtSel.executeQuery();
                if (rst.next()) {
                    serialNOcounter = rst.getLong("serial_number_counter");
                    activeProductId = SqlParameterEncoder.encodeParams(rst.getString("category_id"));
                    category = SqlParameterEncoder.encodeParams(rst.getString("category_id"));
                    voucher_type = SqlParameterEncoder.encodeParams(rst.getString("voucher_type"));
                } else {
                    throw new BTSLBaseException(PretupsErrorCodesI.ERROR_VOMS_NOTFOUND_COUNTER);
                }
                if (_logger.isDebugEnabled()) {
                	_logger.debug(METHOD_NAME, "networkPrefix=" + BTSLUtil.getPrefixCodeUsingNwCode("NG"));
                    _logger.debug(METHOD_NAME, "nwCodePrefixMappingStr=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.NW_CODE_NW_PREFIX_MAPPING)));
                }
                serialnumbrStr = _operatorUtil.formatVomsSerialnum(serialNOcounter, activeProductId, segment, location);
                startserialnumber = Long.parseLong(serialnumbrStr);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("generateVouchers", "startserialnumber" + startserialnumber);
                }
                fromserialnum = startserialnumber + 1;
                pinsList = _operatorUtil.generatePin(location, activeProductId, batchVO.getNoOfVoucher(),batchVO.getSeq_id());
                boolean flag = true;
                l1: while (flag) {
                    try {
                        boolean matchFound = BTSLUtil.validateTableName(voucher_type);
                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
                            table_name = "VOMS_" + voucher_type + "_VOUCHERS";
                        } else {
                            table_name = "voms_vouchers";
                        }

                        sqlselect = new StringBuffer("INSERT INTO " + table_name);
                        sqlselect.append(" (serial_no,product_id,pin_no,generation_batch_no,attempt_used,current_status,");
                        sqlselect.append("expiry_date,consume_before,mrp,talktime,validity,production_network_code,");
                        sqlselect.append("user_network_code,modified_by,last_batch_no,modified_on,");
                        sqlselect.append("created_on,previous_status,");
                        sqlselect.append("status,seq_no,created_date,VOUCHER_TYPE, VOUCHER_SEGMENT  ");
                         if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue() || ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE))).booleanValue() ){
                        	 sqlselect.append(",sequence_id  ");
                         }
                         sqlselect.append(" ) ");
                        sqlselect.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ");
                        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue() || ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE))).booleanValue() ){
                       	 sqlselect.append(" ,?  ");
                        }
                        sqlselect.append("  ) ");
                        sqlInsert = sqlselect.toString();
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("generateVouchers", "sqlInsert" + sqlInsert);
                        }
                        psmtInsert = p_con.prepareStatement(sqlInsert);
                        if (failCount == 0) {
                            successCount = 0;
                        } else {
                            failCount = 0;
                        }
                        for (int j = 0; j < pinsList.size(); j++) {

                            ++serialNOcounter;
                            int k = 0;
                            psmtInsert.setLong(++k, ++startserialnumber);
                            psmtInsert.setString(++k, product_id);
                            psmtInsert.setString(++k, (String) pinsList.get(j));
                            psmtInsert.setString(++k, (String) batchVO.getBatchNo());
                            psmtInsert.setInt(++k, 0);
                            psmtInsert.setString(++k, VOMSI.VOUCHER_NEW);
                            psmtInsert.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(expiryDate));
                            psmtInsert.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(expiryDate));
                            psmtInsert.setLong(++k, mrp);
                            psmtInsert.setLong(++k, batchVO.getTalktime());
                            psmtInsert.setLong(++k, batchVO.getValidity());
                            psmtInsert.setString(++k, location);
                            psmtInsert.setString(++k, location);
                            psmtInsert.setString(++k, PretupsI.SYSTEM);
                            psmtInsert.setString(++k, (String) batchVO.getBatchNo());
                            psmtInsert.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(currentdate));
                            psmtInsert.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(currentdate));
                            psmtInsert.setString(++k, VOMSI.VOUCHER_NEW);
                            psmtInsert.setString(++k, VOMSI.VOUCHER_NEW);
                            psmtInsert.setLong(++k, j);
                            psmtInsert.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(currentdate));
                            psmtInsert.setString(++k, voucher_type);
                            psmtInsert.setString(++k, segment);
                            
                            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
                                psmtInsert.setInt(++k,batchVO.getSeq_id());
                            }else if(!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()&&((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE))).booleanValue()){
                            	psmtInsert.setInt(++k,BTSLUtil.getUniqueInteger((String) pinsList.get(j), 100, 100+((Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ID_RANGE)).intValue()));
                            }

                            psmtInsert.addBatch();
                            try {
                                if ((j + 1) % 200 == 0) {
                                    updateCount = psmtInsert.executeBatch();
                                    successCount = successCount + updateCount.length;
                                    VomsBatchInfoLog.log(currentdate + "Batch No" + batchVO.getBatchNo() + "Batch insert length:" + updateCount.length);
                                }
                            } catch (BatchUpdateException be) {
                                startserialnumber = startserialnumber - (200 - (be.getUpdateCounts().length));
                                serialNOcounter = serialNOcounter - (200 - (be.getUpdateCounts().length));
                                failCount = failCount + (200 - (be.getUpdateCounts().length));
                                successCount = successCount + (be.getUpdateCounts().length);
                                VomsBatchInfoLog.log(currentdate + "Duplicate voucher PIN generated failCount:" + failCount + ",serialNOcounter:" + serialNOcounter + ",startserialnumber:" + startserialnumber);
                                _logger.errorTrace(METHOD_NAME, be);
                            }
                        }
                        try {
                            updateCount = psmtInsert.executeBatch();
                            successCount = successCount + updateCount.length;
                        } catch (BatchUpdateException be) {
                            startserialnumber = startserialnumber - (batchList.size() % 200 - (be.getUpdateCounts().length));
                            serialNOcounter = serialNOcounter - (batchList.size() % 200 - (be.getUpdateCounts().length));
                            failCount = failCount + (batchList.size() % 200 - (be.getUpdateCounts().length ));
                            successCount = successCount + (be.getUpdateCounts().length);
                            VomsBatchInfoLog.log(currentdate + "Duplicate voucher PIN generated failCount:" + failCount + ",serialNOcounter:" + serialNOcounter + ",startserialnumber:" + startserialnumber);
                            _logger.errorTrace(METHOD_NAME, be);
                        }
                        VomsBatchInfoLog.log(currentdate + "Batch Processing commited for batch:" + batchVO.getBatchNo() + "and successCount: " + successCount);

                    } catch (SQLException e) {
                        p_con.rollback();
                        VomsBatchInfoLog.log(currentdate + "Batch Processing terminated for batch:" + batchVO.getBatchNo());
                        _logger.errorTrace(METHOD_NAME, e);
                        throw e;

                    } catch (IllegalStateException e1) {
                        VomsBatchInfoLog.log(currentdate + "Batch Processing terminated for batch:" + batchVO.getBatchNo());
                        _logger.errorTrace(METHOD_NAME, e1);
                        throw e1;
                    }
                    if (failCount > 0) {
                        VomsBatchInfoLog.log(currentdate + "Batch Processing started for fail count:" + failCount);
                        retryCount++;
                        pinsList = _operatorUtil.generatePin(location, activeProductId, failCount,batchVO.getSeq_id());
                        if (pinsList.size() > 0 && retryCount < 5) {
                            continue l1;
                        } else {
                            flag = false;
                        }
                    } else {
                        flag = false;
                    }
                }
         
                // update serial number counter
                sqlselect = new StringBuffer("UPDATE voms_categories SET serial_number_counter=? where category_id=?");
                if (_logger.isDebugEnabled()) {
                    _logger.debug("generateVouchers", "Update Query" + sqlselect.toString());
                }
                psmtupdate1 = p_con.prepareStatement(sqlselect.toString());
                psmtupdate1.setLong(1, serialNOcounter);
                psmtupdate1.setString(2, category);
                update = psmtupdate1.executeUpdate();
                if (update <= 0) {
                    p_con.rollback();
                } else {
                    vomsVoucherDao = new VomsVoucherDAO();
                    sqlselect = new StringBuffer("UPDATE voms_batches set from_serial_no=?,to_serial_no=?,total_no_of_success=?,");
                    sqlselect.append("modified_date=?,status=?,modified_on=?,message=?,batch_type=?, TOTAL_NO_OF_FAILURE=? ");
                    sqlselect.append(" where batch_no=?");
                    psmtupdate = p_con.prepareStatement(sqlselect.toString());
                    psmtupdate.setLong(1, fromserialnum);
                    psmtupdate.setLong(2, startserialnumber);
                    psmtupdate.setLong(3, successCount);
                    psmtupdate.setDate(4, BTSLUtil.getSQLDateFromUtilDate(currentdate));
                    psmtupdate.setString(5, VOMSI.EXECUTED);
                    psmtupdate.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(currentdate));
                    psmtupdate.setString(7, "Generated Successfully");
                    psmtupdate.setString(8, VOMSI.BATCH_GENERATED);
                    psmtupdate.setLong(9, failCount);
                    psmtupdate.setString(10, batchVO.getBatchNo());
                    update = psmtupdate.executeUpdate();
                    vomsVoucherDao.updateSummaryTable(p_con, batchVO, false);
                }
                // update the batch status to GE and from serial number and to
                // serial number fields
                p_con.commit();
                // }
                _logger.info("generateVouchers", "BATCH proceesed successfully:" + batchVO.getBatchNo());
                serialnumbrStr = null;
                VomsBatchInfoLog.genVoucherBatchLog(batchVO);
				batchVO.setFromSerialNo(Long.toString(fromserialnum));
				batchVO.setToSerialNo(Long.toString(fromserialnum + successCount - 1));
				batchVO.setVoucherType(voucher_type);
				batchVO.setQuantity(Long.toString(successCount));
				batchVO.setDenomination(PretupsBL.getDisplayAmount(Long.parseLong(vomsVatchesDAO.getDenomination(p_con, batchVO.getProductID()))));
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_GEN_EMAIL_NOTIFICATION))).booleanValue())
					sendEmailNotification(p_con, batchVO, "voucher.generation.notification.subject");
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_GEN_SMS_NOTIFICATION))).booleanValue()){
					sendSMSNotification(p_con, batchVO, "voucher.generation.notification.subject");
				}
            }// end of for loop for batchlist
            isDataProcessed = true;
        } catch (SQLException sqe) {
            p_con.rollback();
            _logger.errorTrace(METHOD_NAME, sqe);
            _logger.error("generateVouchers", "SQLException" + sqe);
            throw sqe;
        } catch (BTSLBaseException be) {
            _logger.error("generateVouchers", "BTSLBaseException" + be);
            throw be;
        } catch (Exception e) {
            _logger.error("generateVouchers", "Exception e" + e);
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.info("generateVouchers", "Exciting ");
            }
            try {
                if (pstmtSel != null) {
                    pstmtSel.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtupdate != null) {
                	psmtupdate.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtupdate1 != null) {
                	psmtupdate1.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
        }
        return isDataProcessed;
    }

    public static boolean generateTestVouchers(Connection p_con, String batchId,String voucherType, ArrayList batchList,int index) throws SQLException, BTSLBaseException {
        if (_logger.isDebugEnabled()) {
            _logger.info("generateTestVouchers", "Entered batchId:" + batchId + "voucherType:" + voucherType);
        }
        final String METHOD_NAME = "generateTestVouchers";
        boolean isDataProcessed = false;
        PreparedStatement pstmtSel = null, psmtInsert = null, psmtupdate = null;
        ;
        PreparedStatement psmtupdate1 = null;
        ResultSet rst = null, rst1 = null;
        ArrayList pinsList = null;
        VomsBatchVO batchVO = null;
        String product_id = null, activeProductId = "", serialnumbrStr, location = null, category = null, voucher_type = null, table_name = null, segment = null;
        String sqlSelectUpdate = null, sqlInsert = null;
        long serialNOcounter = 0, startserialnumber, fromserialnum = 0;
        Date currentdate = new Date();
        Date expiryDate = null;
        int[] updateCount = null;
        VomsVoucherDAO vomsVoucherDao = null;
        VomsBatchesDAO vomsVatchesDAO = new VomsBatchesDAO();
        int update = 0;
        long successCount = 0;
        long failCount = 0;
        int retryCount = 0;
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
        	
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
        	_logger.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherGenerator[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
        try {
        
            StringBuffer sqlselect = new StringBuffer();
            sqlselect.append("SELECT DISTINCT  VC.serial_number_counter,VC.category_id, VC.voucher_type ");
            sqlselect.append(" FROM voms_categories VC,voms_products VP WHERE VC.CATEGORY_ID=VP.CATEGORY_ID and VP.PRODUCT_ID=?");
            sqlSelectUpdate = sqlselect.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("generateVouchers", "sqlSelect" + sqlSelectUpdate);
            }
            pstmtSel = p_con.prepareStatement(sqlSelectUpdate);
            long mrp = 0;
            //for (int i = 0; i < batchList.size(); i++) {
            if(batchList.size() > 0){
            	retryCount = 0;
                batchVO = (VomsBatchVO) batchList.get(index);
                product_id = batchVO.getProductID();
                location = batchVO.getLocationCode();
                mrp = PretupsBL.getSystemAmount(batchVO.getMrp());
                segment = batchVO.getSegment();
                if(batchVO.getExpiryDate()!=null){
                	expiryDate=batchVO.getExpiryDate();
                	if(expiryDate.before(currentdate)||expiryDate.equals(currentdate))
                	{
                		throw new BTSLBaseException(PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED);
                	}
                }
                else
                expiryDate = BTSLUtil.addDaysInUtilDate(currentdate, batchVO.getExpiryPeriod());
                _logger.info("generateVouchers", "BATCH proceesing started:" + batchVO.getBatchNo() + "product_id" + product_id);
                pstmtSel.setString(1, product_id);
                rst = pstmtSel.executeQuery();
                if (rst.next()) {
                    serialNOcounter = rst.getLong("serial_number_counter");
                    activeProductId = SqlParameterEncoder.encodeParams(rst.getString("category_id"));
                    category = SqlParameterEncoder.encodeParams(rst.getString("category_id"));
                    voucher_type = SqlParameterEncoder.encodeParams(rst.getString("voucher_type"));
                } else {
                    throw new BTSLBaseException(PretupsErrorCodesI.ERROR_VOMS_NOTFOUND_COUNTER);
                }
                serialnumbrStr = _operatorUtil.formatVomsSerialnum(serialNOcounter, activeProductId, segment, location);
                startserialnumber = Long.parseLong(serialnumbrStr);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("generateVouchers", "startserialnumber" + startserialnumber);
                }
                fromserialnum = startserialnumber + 1;
                pinsList = _operatorUtil.generatePin(location, activeProductId, batchVO.getNoOfVoucher(),batchVO.getSeq_id());
                boolean flag = true;
                l1: while (flag) {
                    try {
                        boolean matchFound = BTSLUtil.validateTableName(voucher_type);
                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
                            table_name = "VOMS_" + voucher_type + "_VOUCHERS";
                        } else {
                            table_name = "voms_vouchers";
                        }

                        
                        sqlselect = new StringBuffer("INSERT INTO " + table_name);
                        sqlselect.append(" (serial_no,product_id,pin_no,generation_batch_no,attempt_used,current_status,");
                        sqlselect.append("expiry_date,consume_before,mrp,talktime,validity,production_network_code,");
                        sqlselect.append("user_network_code,modified_by,last_batch_no,modified_on,");
                        sqlselect.append("created_on,previous_status,");
                        sqlselect.append("status,seq_no,created_date,VOUCHER_TYPE, VOUCHER_SEGMENT  ");
                         if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue() || ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE))).booleanValue() ){
                        	 sqlselect.append(",sequence_id  ");
                         }
                         sqlselect.append(" ) ");
                        sqlselect.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ");
                        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue() || ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE))).booleanValue() ){
                       	 sqlselect.append(" ,?  ");
                        }
                        sqlselect.append("  ) ");
                        sqlInsert = sqlselect.toString();
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("generateVouchers", "sqlInsert" + sqlInsert);
                        }
                        psmtInsert = p_con.prepareStatement(sqlInsert);
                        if (failCount == 0) {
                            successCount = 0;
                        } else {
                            failCount = 0;
                        }
                        for (int j = 0; j < pinsList.size(); j++) {

                            ++serialNOcounter;
                            int k = 0;
                            psmtInsert.setLong(++k, ++startserialnumber);
                            psmtInsert.setString(++k, product_id);
                            psmtInsert.setString(++k, (String) pinsList.get(j));
                            psmtInsert.setString(++k, (String) batchVO.getBatchNo());
                            psmtInsert.setInt(++k, 0);
                            psmtInsert.setString(++k, VOMSI.VOUCHER_NEW);
                            psmtInsert.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(expiryDate));
                            psmtInsert.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(expiryDate));
                            psmtInsert.setLong(++k, mrp);
                            psmtInsert.setLong(++k, batchVO.getTalktime());
                            psmtInsert.setLong(++k, batchVO.getValidity());
                            
                            psmtInsert.setString(++k, location);
                            psmtInsert.setString(++k, location);
                            psmtInsert.setString(++k, PretupsI.SYSTEM);
                            psmtInsert.setString(++k, (String) batchVO.getBatchNo());
                            psmtInsert.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(currentdate));
                            psmtInsert.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(currentdate));
                            psmtInsert.setString(++k, VOMSI.VOUCHER_NEW);
                            psmtInsert.setString(++k, VOMSI.VOUCHER_NEW);
                            psmtInsert.setLong(++k, j);
                            psmtInsert.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(currentdate));
                            psmtInsert.setString(++k, voucher_type);
                            psmtInsert.setString(++k, segment);
                            
                            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
                                psmtInsert.setInt(++k,batchVO.getSeq_id());
                            }else if(!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()&&((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE))).booleanValue()){
                            	psmtInsert.setInt(++k,BTSLUtil.getUniqueInteger((String) pinsList.get(j), 100, 100+((Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ID_RANGE)).intValue()));
                            }

                            psmtInsert.addBatch();
                            try {
                                if ((j + 1) % 200 == 0) {
                                    updateCount = psmtInsert.executeBatch();
                                    successCount = successCount + updateCount.length;
                                    VomsBatchInfoLog.log(currentdate + "Batch No" + batchVO.getBatchNo() + "Batch insert length:" + updateCount.length);
                                }
                            } catch (BatchUpdateException be) {
                                startserialnumber = startserialnumber - (200 - (be.getUpdateCounts().length));
                                serialNOcounter = serialNOcounter - (200 - (be.getUpdateCounts().length));
                                failCount = failCount + (200 - (be.getUpdateCounts().length));
                                successCount = successCount + (be.getUpdateCounts().length);
                                VomsBatchInfoLog.log(currentdate + "Duplicate voucher PIN generated failCount:" + failCount + ",serialNOcounter:" + serialNOcounter + ",startserialnumber:" + startserialnumber);
                                _logger.errorTrace(METHOD_NAME, be);
                            }
                        }
                        try {
                            updateCount = psmtInsert.executeBatch();
                            successCount = successCount + updateCount.length;
                        } catch (BatchUpdateException be) {
                            startserialnumber = startserialnumber - (batchList.size() % 200 - (be.getUpdateCounts().length));
                            serialNOcounter = serialNOcounter - (batchList.size() % 200 - (be.getUpdateCounts().length));
                            failCount = failCount + (batchList.size() % 200 - (be.getUpdateCounts().length ));
                            successCount = successCount + (be.getUpdateCounts().length);
                            VomsBatchInfoLog.log(currentdate + "Duplicate voucher PIN generated failCount:" + failCount + ",serialNOcounter:" + serialNOcounter + ",startserialnumber:" + startserialnumber);
                            _logger.errorTrace(METHOD_NAME, be);
                        }
                        VomsBatchInfoLog.log(currentdate + "Batch Processing commited for batch:" + batchVO.getBatchNo() + "and successCount: " + successCount);

                    } catch (SQLException e) {
                        p_con.rollback();
                        VomsBatchInfoLog.log(currentdate + "Batch Processing terminated for batch:" + batchVO.getBatchNo());
                        _logger.errorTrace(METHOD_NAME, e);
                        throw e;

                    } catch (IllegalStateException e1) {
                        VomsBatchInfoLog.log(currentdate + "Batch Processing terminated for batch:" + batchVO.getBatchNo());
                        _logger.errorTrace(METHOD_NAME, e1);
                        throw e1;
                    }
                    if (failCount > 0) {
                        VomsBatchInfoLog.log(currentdate + "Batch Processing started for fail count:" + failCount);
                        retryCount++;
                        pinsList = _operatorUtil.generatePin(location, activeProductId, failCount,batchVO.getSeq_id());
                        if (pinsList.size() > 0 && retryCount < 5) {
                            continue l1;
                        } else {
                            flag = false;
                        }
                    } else {
                        flag = false;
                    }
                }
         
                // update serial number counter
                sqlselect = new StringBuffer("UPDATE voms_categories SET serial_number_counter=? where category_id=?");
                if (_logger.isDebugEnabled()) {
                    _logger.debug("generateVouchers", "Update Query" + sqlselect.toString());
                }
                psmtupdate1 = p_con.prepareStatement(sqlselect.toString());
                psmtupdate1.setLong(1, serialNOcounter);
                psmtupdate1.setString(2, category);
                update = psmtupdate1.executeUpdate();
                if (update <= 0) {
                    p_con.rollback();
                } else {
                    vomsVoucherDao = new VomsVoucherDAO();
                    sqlselect = new StringBuffer("UPDATE voms_batches set from_serial_no=?,to_serial_no=?,total_no_of_success=?,");
                    sqlselect.append("modified_date=?,status=?,modified_on=?,message=?,batch_type=?, TOTAL_NO_OF_FAILURE=? ");
                    sqlselect.append(" where batch_no=?");
                    psmtupdate = p_con.prepareStatement(sqlselect.toString());
                    psmtupdate.setLong(1, fromserialnum);
                    psmtupdate.setLong(2, startserialnumber);
                    psmtupdate.setLong(3, successCount);
                    psmtupdate.setDate(4, BTSLUtil.getSQLDateFromUtilDate(currentdate));
                    psmtupdate.setString(5, VOMSI.EXECUTED);
                    psmtupdate.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(currentdate));
                    psmtupdate.setString(7, "Generated Successfully");
                    psmtupdate.setString(8, VOMSI.BATCH_GENERATED);
                    psmtupdate.setLong(9, failCount);
                    psmtupdate.setString(10, batchVO.getBatchNo());
                    update = psmtupdate.executeUpdate();
                    vomsVoucherDao.updateSummaryTable(p_con, batchVO, false);
                }
                // update the batch status to GE and from serial number and to
                // serial number fields
               // p_con.commit();
                // }
                _logger.info("generateVouchers", "BATCH proceesed successfully:" + batchVO.getBatchNo());
                serialnumbrStr = null;
                VomsBatchInfoLog.genVoucherBatchLog(batchVO);
                batchVO.setFromSerialNo(Long.toString(fromserialnum));
				batchVO.setToSerialNo(Long.toString(fromserialnum + successCount - 1));
				batchVO.setVoucherType(voucher_type);
				batchVO.setQuantity(Long.toString(successCount));
				batchVO.setDenomination(PretupsBL.getDisplayAmount(Long.parseLong(vomsVatchesDAO.getDenomination(p_con, batchVO.getProductID()))));
                if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_GEN_EMAIL_NOTIFICATION))).booleanValue())
                	sendEmailNotification(p_con, batchVO, "voucher.generation.notification.subject");
                if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_GEN_SMS_NOTIFICATION))).booleanValue()){
                	sendSMSNotification(p_con, batchVO, "voucher.generation.notification.subject");
                }
            }// end of if condition for batchlist
            isDataProcessed = true;
        } catch (SQLException sqe) {
            p_con.rollback();
            _logger.errorTrace(METHOD_NAME, sqe);
            _logger.error("generateVouchers", "SQLException" + sqe);
            throw sqe;
        } catch (BTSLBaseException be) {
            _logger.error("generateVouchers", "BTSLBaseException" + be);
            throw be;
        } catch (Exception e) {
            _logger.error("generateVouchers", "Exception e" + e);
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.info("generateVouchers", "Exciting ");
            }
            try {
                if (pstmtSel != null) {
                    pstmtSel.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtupdate != null) {
                	psmtupdate.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtupdate1 != null) {
                	psmtupdate1.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
        }
        return isDataProcessed;
    }
    
    public static void sendEmailNotification(Connection p_con, VomsBatchVO batchVO, String p_subject) {
		final String METHOD_NAME = "sendEmailNotification";
        
		final Locale locale = BTSLUtil.getSystemLocaleForEmail();
        
		if (_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME, "Entered ");
		}

		try {
			final String from = BTSLUtil.getMessage(locale,"o2c.email.notification.from");
			//final String from = "System";
			String cc = PretupsI.EMPTY;
			String message1 = null;
			final String bcc = "";
			String subject = "";
			String to = "";
            ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
            StringBuilder sb=new StringBuilder("");  
            sb.append("<br>");
            sb.append("<table><tr>"); 
            sb.append(" <td style='width: 12%;'>");
            sb.append(BTSLUtil.getMessage(locale, "o2c.email.notification.voucherType"));
            sb.append("</td>");
            sb.append(" <td style='width: 12%;'>");
            sb.append(BTSLUtil.getMessage(locale, "voucher.generation.email.notification.voucherSegment"));
            sb.append("</td>");
            sb.append(" <td style='width: 5%;'>");
            sb.append(BTSLUtil.getMessage(locale, "o2c.email.notification.denomination"));
            sb.append("</td>");
            sb.append(" <td style='width: 12%;'>");
            sb.append(BTSLUtil.getMessage(locale, "voucher.generation.email.notification.voucherProfile"));
            sb.append("</td>");
            sb.append(" <td style='width: 12%;'>");
            sb.append(BTSLUtil.getMessage(locale, "o2c.email.notification.batchId"));
            sb.append("</td>");
            sb.append(" <td style='width: 25%;'>");
            sb.append(BTSLUtil.getMessage(locale, "o2c.email.notification.fromSerialNo"));
            sb.append("</td>");
            sb.append(" <td style='width: 25%;'>");
            sb.append(BTSLUtil.getMessage(locale, "o2c.email.notification.toSerialNo"));
            sb.append("</td>");
            sb.append(" <td style='width: 10%;'>");
            sb.append(BTSLUtil.getMessage(locale, "o2c.email.notification.quantity"));
            sb.append("</td>");
            sb.append(" <td style='width: 12%;'>");
            sb.append(BTSLUtil.getMessage(locale, "voucher.generation.email.notification.batch.status"));
            sb.append("</td>");
            sb.append("</tr>");
            message1=sb.toString();
            StringBuilder sb1=new StringBuilder(""); 
            sb1.append("<tr>"); 
            sb1.append("<td style='width: 10%;'>");
            sb1.append(batchVO.getVoucherType());
            sb1.append("</td>");
            sb1.append("<td style='width: 10%;'>");
            sb1.append(BTSLUtil.getSegmentDesc(batchVO.getSegment()));
            sb1.append("</td>");
            sb1.append("<td style='width: 10%;'>");
            sb1.append(batchVO.getDenomination()); 
            sb1.append("</td>");  
            sb1.append("<td style='width: 10%;'>");
            sb1.append(batchVO.getProductName());
            sb1.append("</td>");
            sb1.append("<td style='width: 25%;'>");
            sb1.append(batchVO.getBatchNo());
            sb1.append("</td>");  
            sb1.append("<td style='width: 25%;'>");
            sb1.append(batchVO.getFromSerialNo());
            sb1.append("</td>");
            sb1.append("<td style='width: 25%;'>");
            sb1.append(batchVO.getToSerialNo());
            sb1.append("</td>");
            sb1.append("<td style='width: 25%;'>");
            sb1.append(batchVO.getQuantity());
            sb1.append("</td>");
            sb1.append("<td style='width: 10%;'>");
            sb1.append(((batchVO.getStatus()).equals("AC"))?"Success":"Failure");
            sb1.append("</td>");
            sb1.append("</tr>");
            message1 = message1 + sb1.toString();
            message1 = message1 + "</table>";
            subject = BTSLUtil.getMessage(locale,p_subject);
            
            //For getting name, msisdn, email of initiator
            ArrayList arrayList = new ArrayList();
            arrayList = channelUserWebDAO.loadUserNameAndEmail(p_con, batchVO.getCreatedBy());
            to = (String)(arrayList.get(2));
            if(batchVO.getFirstApprovedBy() != null)
            	to += "," + (channelUserWebDAO.loadUserNameAndEmail(p_con, batchVO.getFirstApprovedBy())).get(2);
            if(batchVO.getSecondApprovedBy() != null)
            	to += "," + (channelUserWebDAO.loadUserNameAndEmail(p_con, batchVO.getSecondApprovedBy())).get(2);
            if(batchVO.getThirdApprovedBy() != null)
            	to += "," + (channelUserWebDAO.loadUserNameAndEmail(p_con, batchVO.getThirdApprovedBy())).get(2);
            
			if (_logger.isDebugEnabled()) {
				_logger.debug("MAIL CONTENT",message1);
			}
			boolean isAttachment = false;
			String pathofFile = "";
			String fileNameTobeDisplayed = "";
			// Send email
			EMailSender.sendMail(to, from, bcc, cc, subject, message1, isAttachment, pathofFile, fileNameTobeDisplayed);
		} catch (Exception e) {
			if (_logger.isDebugEnabled()) {
				_logger.error(METHOD_NAME, " Email sending failed" + e.getMessage());
			}
			_logger.errorTrace(METHOD_NAME, e);
		}
		if (_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME, "Exiting ....Anshul");
		}
	}
    
    /**
     * @param p_con
     * @param batchList
     * @return
     * @author pankaj.rawat
     *         This method is used to generate voucher pins and serial numbers
     *         based on Accepted batched in DB
     *         and creates vouchers with status GE
     */
    public static boolean generateVouchersOnline(Connection p_con, ArrayList batchList) throws SQLException, BTSLBaseException {
        if (_logger.isDebugEnabled()) {
            _logger.info("generateVouchersOnline", "Entered");
        }
        final String METHOD_NAME = "generateVouchersOnline";
        boolean isDataProcessed = false;
        PreparedStatement pstmtSel = null, psmtInsert = null, psmtupdate = null;
        ;
        PreparedStatement psmtupdate1 = null;
        ResultSet rst = null, rst1 = null;
        ArrayList pinsList = null;
        VomsBatchesDAO vomsVatchesDAO = null;
        VomsBatchVO batchVO = null;
        String product_id = null, activeProductId = "", serialnumbrStr, location = null, category = null, voucher_type = null, table_name = null, segment = null;;
        String sqlSelectUpdate = null, sqlInsert = null;
        long serialNOcounter = 0, startserialnumber, fromserialnum = 0;
        VomsUtil vomsutil = null;
        Date currentdate = new Date();
        Date expiryDate = null;
        int[] updateCount = null;
        VomsVoucherDAO vomsVoucherDao = null;	
        int update = 0;
        VoucherGenerator voucherGenerator = null;
        long successCount = 0;
        long failCount = 0;
        int retryCount = 0;
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {        	
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
        	_logger.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherGenerator[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
        try {
            vomsVatchesDAO = new VomsBatchesDAO();
            StringBuffer sqlselect = new StringBuffer();
            sqlselect.append("SELECT DISTINCT  VC.serial_number_counter,VC.category_id, VC.voucher_type ");
            sqlselect.append(" FROM voms_categories VC,voms_products VP WHERE VC.CATEGORY_ID=VP.CATEGORY_ID and VP.PRODUCT_ID=?");
            sqlSelectUpdate = sqlselect.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "sqlSelect" + sqlSelectUpdate);
            }
            pstmtSel = p_con.prepareStatement(sqlSelectUpdate);
            vomsutil = new VomsUtil();
            voucherGenerator = new VoucherGenerator();
            long mrp = 0;
            for (int i = 0; i < batchList.size(); i++) {
            	retryCount = 0;
                batchVO = (VomsBatchVO) batchList.get(i);
                product_id = batchVO.getProductID();
                location = batchVO.getLocationCode();
                segment = batchVO.getSegment();
                mrp = PretupsBL.getSystemAmount(batchVO.getMrp());
                if(batchVO.getExpiryPeriod() != 0)
                	expiryDate = BTSLUtil.addDaysInUtilDate(currentdate, batchVO.getExpiryPeriod());
                else
                {
                	expiryDate = batchVO.getExpiryDate();
                	if(expiryDate.before(currentdate)||expiryDate.equals(currentdate))
                	{
                		throw new BTSLBaseException(PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED);
                	}
                }
                _logger.info(METHOD_NAME, "BATCH proceesing started:" + batchVO.getBatchNo() + "product_id" + product_id);
                pstmtSel.setString(1, product_id);
                rst = pstmtSel.executeQuery();
                if (rst.next()) {
                    serialNOcounter = rst.getLong("serial_number_counter");
                    activeProductId = SqlParameterEncoder.encodeParams(rst.getString("category_id"));
                    category = SqlParameterEncoder.encodeParams(rst.getString("category_id"));
                    voucher_type = SqlParameterEncoder.encodeParams(rst.getString("voucher_type"));
                } else {
                    throw new BTSLBaseException(PretupsErrorCodesI.ERROR_VOMS_NOTFOUND_COUNTER);
                }
                if (_logger.isDebugEnabled()) {
                	_logger.debug(METHOD_NAME, "networkPrefix=" + BTSLUtil.getPrefixCodeUsingNwCode("NG"));
                    _logger.debug(METHOD_NAME, "nwCodePrefixMappingStr=" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.NW_CODE_NW_PREFIX_MAPPING)));
                }
                serialnumbrStr = _operatorUtil.formatVomsSerialnum(serialNOcounter, activeProductId, segment, location);
                startserialnumber = Long.parseLong(serialnumbrStr);
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "startserialnumber" + startserialnumber);
                }
                fromserialnum = startserialnumber + 1;
                pinsList = _operatorUtil.generatePin(location, activeProductId, batchVO.getNoOfVoucher(),batchVO.getSeq_id());
                boolean flag = true;
                l1: while (flag) {
                    try {
                        boolean matchFound = BTSLUtil.validateTableName(voucher_type);
                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
                            table_name = "VOMS_" + voucher_type + "_VOUCHERS";
                        } else {
                            table_name = "voms_vouchers";
                        }

                        sqlselect = new StringBuffer("INSERT INTO " + table_name);
                        sqlselect.append(" (serial_no,product_id,pin_no,generation_batch_no,attempt_used,current_status,");
                        sqlselect.append("expiry_date,consume_before,mrp,talktime,validity,production_network_code,");
                        sqlselect.append("user_network_code,modified_by,last_batch_no,modified_on,");
                        sqlselect.append("created_on,previous_status,");
                        sqlselect.append("status,seq_no,created_date,VOUCHER_TYPE, VOUCHER_SEGMENT  ");
                         if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue() || ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE))).booleanValue() ){
                        	 sqlselect.append(",sequence_id  ");
                         }
                         sqlselect.append(" ) ");
                        sqlselect.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ");
                        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue() || ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE))).booleanValue() ){
                       	 sqlselect.append(" ,?  ");
                        }
                        sqlselect.append("  ) ");
                        sqlInsert = sqlselect.toString();
                        if (_logger.isDebugEnabled()) {
                            _logger.debug(METHOD_NAME, "sqlInsert" + sqlInsert);
                        }
                        psmtInsert = p_con.prepareStatement(sqlInsert);
                        if (failCount == 0) {
                            successCount = 0;
                        } else {
                            failCount = 0;
                        }
                        for (int j = 0; j < pinsList.size(); j++) {

                            ++serialNOcounter;
                            int k = 0;
                            psmtInsert.setLong(++k, ++startserialnumber);
                            psmtInsert.setString(++k, product_id);
                            psmtInsert.setString(++k, (String) pinsList.get(j));
                            psmtInsert.setString(++k, (String) batchVO.getBatchNo());
                            psmtInsert.setInt(++k, 0);
                            psmtInsert.setString(++k, VOMSI.VOUCHER_NEW);
                            psmtInsert.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(expiryDate));
                            psmtInsert.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(expiryDate));
                            psmtInsert.setLong(++k, mrp);
                            psmtInsert.setLong(++k, batchVO.getTalktime());
                            psmtInsert.setLong(++k, batchVO.getValidity());
                            psmtInsert.setString(++k, location);
                            psmtInsert.setString(++k, location);
                            psmtInsert.setString(++k, PretupsI.SYSTEM);
                            psmtInsert.setString(++k, (String) batchVO.getBatchNo());
                            psmtInsert.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(currentdate));
                            psmtInsert.setTimestamp(++k, BTSLUtil.getTimestampFromUtilDate(currentdate));
                            psmtInsert.setString(++k, VOMSI.VOUCHER_NEW);
                            psmtInsert.setString(++k, VOMSI.VOUCHER_NEW);
                            psmtInsert.setLong(++k, j);
                            psmtInsert.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(currentdate));
                            psmtInsert.setString(++k, voucher_type);
                            psmtInsert.setString(++k, segment);
                            
                            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
                                psmtInsert.setInt(++k,batchVO.getSeq_id());
                            }else if(!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()&&((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE))).booleanValue()){
                            	psmtInsert.setInt(++k,BTSLUtil.getUniqueInteger((String) pinsList.get(j), 100, 100+((Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ID_RANGE)).intValue()));
                            }

                            psmtInsert.addBatch();
                            try {
                                if ((j + 1) % 200 == 0) {
                                    updateCount = psmtInsert.executeBatch();
                                    successCount = successCount + updateCount.length;
                                    VomsBatchInfoLog.log(currentdate + "Batch No" + batchVO.getBatchNo() + "Batch insert length:" + updateCount.length);
                                }
                            } catch (BatchUpdateException be) {
                                startserialnumber = startserialnumber - (200 - (be.getUpdateCounts().length));
                                serialNOcounter = serialNOcounter - (200 - (be.getUpdateCounts().length));
                                failCount = failCount + (200 - (be.getUpdateCounts().length));
                                successCount = successCount + (be.getUpdateCounts().length);
                                VomsBatchInfoLog.log(currentdate + "Duplicate voucher PIN generated failCount:" + failCount + ",serialNOcounter:" + serialNOcounter + ",startserialnumber:" + startserialnumber);
                                _logger.errorTrace(METHOD_NAME, be);
                            }
                        }
                        try {
                            updateCount = psmtInsert.executeBatch();
                            successCount = successCount + updateCount.length;
                        } catch (BatchUpdateException be) {
                            startserialnumber = startserialnumber - (batchList.size() % 200 - (be.getUpdateCounts().length));
                            serialNOcounter = serialNOcounter - (batchList.size() % 200 - (be.getUpdateCounts().length));
                            failCount = failCount + (batchList.size() % 200 - (be.getUpdateCounts().length ));
                            successCount = successCount + (be.getUpdateCounts().length);
                            VomsBatchInfoLog.log(currentdate + "Duplicate voucher PIN generated failCount:" + failCount + ",serialNOcounter:" + serialNOcounter + ",startserialnumber:" + startserialnumber);
                            _logger.errorTrace(METHOD_NAME, be);
                        }
                        VomsBatchInfoLog.log(currentdate + "Batch Processing commited for batch:" + batchVO.getBatchNo() + "and successCount: " + successCount);

                    } catch (SQLException e) {
                        p_con.rollback();
                        VomsBatchInfoLog.log(currentdate + "Batch Processing terminated for batch:" + batchVO.getBatchNo());
                        _logger.errorTrace(METHOD_NAME, e);
                        throw e;

                    } catch (IllegalStateException e1) {
                        VomsBatchInfoLog.log(currentdate + "Batch Processing terminated for batch:" + batchVO.getBatchNo());
                        _logger.errorTrace(METHOD_NAME, e1);
                        throw e1;
                    }
                    if (failCount > 0) {
                        VomsBatchInfoLog.log(currentdate + "Batch Processing started for fail count:" + failCount);
                        retryCount++;
                        pinsList = _operatorUtil.generatePin(location, activeProductId, failCount,batchVO.getSeq_id());
                        if (pinsList.size() > 0 && retryCount < 5) {
                            continue l1;
                        } else {
                            flag = false;
                        }
                    } else {
                        flag = false;
                    }
                }
         
                // update serial number counter
                sqlselect = new StringBuffer("UPDATE voms_categories SET serial_number_counter=? where category_id=?");
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "Update Query" + sqlselect.toString());
                }
                psmtupdate1 = p_con.prepareStatement(sqlselect.toString());
                psmtupdate1.setLong(1, serialNOcounter);
                psmtupdate1.setString(2, category);
                update = psmtupdate1.executeUpdate();
                if (update <= 0) {
                    p_con.rollback();
                } else {
                    vomsVoucherDao = new VomsVoucherDAO();
                    sqlselect = new StringBuffer("UPDATE voms_batches set from_serial_no=?,to_serial_no=?,total_no_of_success=?,");
                    sqlselect.append("modified_date=?,status=?,modified_on=?,message=?,batch_type=?, TOTAL_NO_OF_FAILURE=? ");
                    sqlselect.append(" where batch_no=?");
                    psmtupdate = p_con.prepareStatement(sqlselect.toString());
                    psmtupdate.setLong(1, fromserialnum);
                    psmtupdate.setLong(2, startserialnumber);
                    psmtupdate.setLong(3, successCount);
                    psmtupdate.setDate(4, BTSLUtil.getSQLDateFromUtilDate(currentdate));
                    psmtupdate.setString(5, VOMSI.EXECUTED);
                    psmtupdate.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(currentdate));
                    psmtupdate.setString(7, "Generated Successfully");
                    psmtupdate.setString(8, VOMSI.BATCH_GENERATED);
                    psmtupdate.setLong(9, failCount);
                    psmtupdate.setString(10, batchVO.getBatchNo());
                    update = psmtupdate.executeUpdate();
                    vomsVoucherDao.updateSummaryTable(p_con, batchVO, false);
                }
                // update the batch status to GE and from serial number and to
                // serial number fields
                p_con.commit();
                // }
                _logger.info(METHOD_NAME, "BATCH proceesed successfully:" + batchVO.getBatchNo());
                serialnumbrStr = null;
                VomsBatchInfoLog.genVoucherBatchLog(batchVO);
				batchVO.setFromSerialNo(Long.toString(fromserialnum));
				batchVO.setToSerialNo(Long.toString(fromserialnum + successCount - 1));
				batchVO.setVoucherType(voucher_type);
				batchVO.setQuantity(Long.toString(successCount));
				batchVO.setDenomination(PretupsBL.getDisplayAmount(Long.parseLong(vomsVatchesDAO.getDenomination(p_con, batchVO.getProductID()))));
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_GEN_EMAIL_NOTIFICATION))).booleanValue())
					sendEmailNotification(p_con, batchVO, "voucher.generation.notification.subject");
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_GEN_SMS_NOTIFICATION))).booleanValue())
				{
					sendSMSNotification(p_con, batchVO, "voucher.generation.notification.subject");
				}
            }// end of for loop for batchlist
            isDataProcessed = true;
        } catch (SQLException sqe) {
            p_con.rollback();
            _logger.errorTrace(METHOD_NAME, sqe);
            _logger.error(METHOD_NAME, "SQLException" + sqe);
            throw sqe;
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException" + be);
            throw be;
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception e" + e);
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.info(METHOD_NAME, "Exciting ");
            }
            try {
                if (pstmtSel != null) {
                    pstmtSel.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtupdate != null) {
                	psmtupdate.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtupdate1 != null) {
                	psmtupdate1.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rst != null) {
                    rst.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
        }
        return isDataProcessed;
    }
   
    /**
     * @param p_con
     * @param batchVO
     * @param p_subject
     * @return
     * @author pankaj.rawat
     *         This method is used to send SMS Notification to admin mobiles on generation of vouchers and rejection of vouchers. 
     */
    public static void sendSMSNotification(Connection p_con, VomsBatchVO batchVO, String p_subject) throws Exception
    {
    	final String METHOD_NAME = "sendSMSNotification";
		if (_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME, "Entered ");
		}
		
		try {
            final String msisdnString = new String(Constants.getProperty("adminmobile"));
            final String[] smsReceivers = msisdnString.split(",");
			if (_logger.isDebugEnabled()) {
				_logger.debug("SMS RECEIVERS:",smsReceivers);
			}

			final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
			String messageKey = null;
			String[] array = new String[9];
			if(p_subject.equals("voucher.generation.notification.subject"))
			{
				messageKey = PretupsErrorCodesI.VOUCHER_GEN_NOTIFICATION;
				array[0]=batchVO.getVoucherType();
				array[1]=BTSLUtil.getSegmentDesc(batchVO.getSegment());
				array[2]=batchVO.getDenomination();
				array[3]=batchVO.getProductName();
				array[4]=batchVO.getBatchNo();
				array[5]=batchVO.getFromSerialNo();
				array[6]=batchVO.getToSerialNo();
				array[7]=batchVO.getQuantity();
				array[8]=(("AC").equals(batchVO.getStatus()))?"Success":"Failure";
			}
			else
			{
				messageKey = PretupsErrorCodesI.VOUCHER_REJECT_NOTIFICATION;
				String finalApprLvl = null;
				if(batchVO.getFirstApprovedBy() == null)
					finalApprLvl = "1";
    			else if(batchVO.getSecondApprovedBy() == null)
    				finalApprLvl = "2";
    			else
    				finalApprLvl = "3";
				array[0]=batchVO.getBatchNo();
				array[1]=finalApprLvl;
			}
			final BTSLMessages messages = new BTSLMessages(messageKey, array);
			for(String msisdn : smsReceivers){
				final PushMessage pushMessage = new PushMessage(msisdn, messages, batchVO.getBatchNo(), null, locale, batchVO.get_NetworkCode());
				pushMessage.push();
			}
		}
		catch(Exception e)
		{
			if (_logger.isDebugEnabled()) {
				_logger.error(METHOD_NAME, " Sms sending failed" + e.getMessage());
			}
			_logger.errorTrace(METHOD_NAME, e);
		}
		if (_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME, "Exiting ....");
		}
    }
} // end class
