/*
 * Created on Jun 27, 2006
 * 
 */
package com.btsl.voms.voucher.businesslogic;

/**
 * @(#)VomsChangeBatchStatusThread.java
 *                                      Copyright(c) 2006, Bharti Telesoft Ltd.
 *                                      All Rights Reserved
 * 
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Author Date History
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      vikas.yadav 01/07/2006 Initial Creation
 * 
 * 
 *                                      This thread class is used for Change
 *                                      Voucher Status(Genrated as well as
 *                                      Others) of EVD
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;

import org.spring.custom.action.Globals;
import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomslogging.VomsBatchInfoLog;
import com.btsl.voms.vomslogging.VomsVoucherChangeStatusLog;

/**
 * This method is used to call change status package to change the status of vouchers
 *
 */
public class VomsChangeBatchStatusThread extends Thread {
    private Log log = LogFactory.getLog(this.getClass().getName());
    private Thread t;
    private VomsBatchVO batchVO = null;
    private long maxErrorAllowed = 0;
    private int processScreen = 0;
    private Connection con = null;MComConnectionI mcomCon = null;
    private PreparedStatement pstmt = null;
    private HttpServletRequest request = null;
    private ResultSet rs = null;
    private Locale locale = null;
    
    
    VomsChangeBatchStatusThread() {
    }

    /**
     * @param p_batchVO
     * @param p_maxErrorAllowed
     * @param p_request
     * @param p_processScreen
     * @throws BTSLBaseException
     */
    public VomsChangeBatchStatusThread(VomsBatchVO p_batchVO, long p_maxErrorAllowed, HttpServletRequest p_request, int p_processScreen) throws BTSLBaseException {
        t = new Thread(this, "VomsChangeBatchStatus");
        batchVO = p_batchVO;
        maxErrorAllowed = p_maxErrorAllowed;
        request = p_request;
        processScreen = p_processScreen;
        
    }

     
    public void run() {
        int updateCount = 0;
        final String methodName = "run";
        String threadName = "VomsChangeBatchStatusThread";
		locale = BTSLUtil.getSystemLocaleForEmail();
		
		try {
			mcomCon = new MComConnection();
			
			con = mcomCon.getConnection();
			
			
            if (log.isDebugEnabled()) {
                log.debug(threadName, "Before Calling the package. Input values: Generated Batch No:=" + batchVO.getBatchNo() + "  From Serial No:=" + batchVO.getFromSerialNo() + "To Serial No:=" + batchVO.getToSerialNo() + "  Max Eeror Entries allowed :=" + maxErrorAllowed);
                log.debug(threadName, "No. of Vouchers :=" + batchVO.getNoOfVoucher() + "  Input Screen  :=" + processScreen + "New Status  :=" + batchVO.getBatchType() + "  Date   :=" + batchVO.getCreatedDate());
                log.debug(threadName, "batchVO.getRcAdminDaysAllowed()::" + batchVO.getRcAdminDaysAllowed());
            }
			
			
            VomsChangeBatchStatusQry batchStatusQry = (VomsChangeBatchStatusQry) ObjectProducer.getObject(QueryConstants.VOMS_CHANGE_BATCH_STATUS, QueryConstants.QUERY_PRODUCER);
            
            String plSqlRetuns[] = null;
            
            if(request != null) {
            
            	 plSqlRetuns =  batchStatusQry.changeVoucherStatusPlSqlQry(con, batchVO, maxErrorAllowed, processScreen, (MessageResources)request.getAttribute(Globals.MESSAGES_KEY));
            }else {
            	plSqlRetuns =  batchStatusQry.changeVoucherStatusPlSqlQry(con, batchVO, maxErrorAllowed, processScreen);
            }
            
            String errMessage = BTSLUtil.NullToString(plSqlRetuns[1]);
            if (errMessage.length() > 200) {
                errMessage = errMessage.substring(0, 200);
            }
            if (plSqlRetuns[0].equalsIgnoreCase("FAILED"))// when procedure
                                                               // return fail
                                                               // update voms
                                                               // batches
                                                               // table.set
                                                               // status failed
                                                               // to FA
            {
                // rollback the transaction done in package
                con.rollback();
                // update batch status to fail
                updateCount = changeBatchStatus(con, VOMSI.BATCHFAILEDSTATUS, batchVO.getNoOfVoucher(), 0, batchVO.getModifiedBy(), batchVO.getBatchNo(), errMessage, batchVO.get_NetworkCode());
                if (log.isDebugEnabled()) {
                    log.debug(threadName, "After exceuting update updateCount" + updateCount);
                }
                if (updateCount > 0) {
                    con.commit();
                    batchVO.setMessage(BTSLUtil.NullToString(plSqlRetuns[0]));
                    batchVO.setSuccessCount(0);
                    batchVO.setFailCount(batchVO.getNoOfVoucher());
                    batchVO.setStatus(VOMSI.BATCHFAILEDSTATUS);
                    VomsBatchInfoLog.modifyBatchLog(batchVO);// set fail
                                                             // information into
                                                             // logs.
                } else {
                    con.rollback();
                }
            } else {// when procedure returns other than fail set infomation
                    // into logs
                ArrayList voucherList = null;
                VomsVoucherVO voucherVO = null;
                String prevProcessStatus = null;
                String currProcessStatus = null;
                String initialSerialNo = null;
                String rsSerialNo = null;
                String rsPreviousStat = null;
                String rsModifiedBy = null;
                String rsModifiedOn = null;
                String rsCurrentStat = null;
                String rsChangeSrc = null;
                String rsMessage = null;
                long rsMrp = 0;
                String rsExpiryDate = null;
                int successCount = 0;
                int errorCount = 0;
                boolean flag = true;
                int count = 0;
                StringBuffer sqlSelectBuf = new StringBuffer(" SELECT va.serial_no SERIAL_NO, va.current_status CURRENTSTAT,  va.previous_status PREVSTAT,  va.modified_by MODIFIEDBY, ");
                sqlSelectBuf.append(" va.modified_on MODIFIEDON, p.mrp MRP, ");
                sqlSelectBuf.append(" va.status_change_source STATCHSRC, batch_no, coalesce( va.message,'') MESSAGE,  va.process_status PRSTAT, ");
                sqlSelectBuf.append(" v.expiry_date EXPDATE ");
                sqlSelectBuf.append(" FROM voms_voucher_audit va,voms_vouchers v,voms_products p WHERE va.batch_no=? ");
                sqlSelectBuf.append(" AND va.serial_no=v.serial_no and v.product_id=p.product_id order by va.serial_no ");
                if (log.isDebugEnabled()) {
                    log.debug(threadName, "Select Query=" + sqlSelectBuf.toString());
                }
                pstmt = con.prepareStatement(sqlSelectBuf.toString());
                pstmt.setString(1, batchVO.getBatchNo());
                rs = pstmt.executeQuery();
                if (log.isDebugEnabled()) {
                    log.debug(threadName, "After executing query ");
                }
                voucherList = new ArrayList();
                while (rs.next()) {
                    if (BTSLUtil.NullToString(rs.getString("PRSTAT")).equalsIgnoreCase(VOMSI.VA_PROCESS_SUCCESS_STAT)) {
                        successCount = successCount + 1;
                    } else if (BTSLUtil.NullToString(rs.getString("PRSTAT")).equalsIgnoreCase(VOMSI.VA_PROCESS_ERROR_STAT)) {
                        errorCount = errorCount + 1;
                    }
                    if (count == 0) {
                        prevProcessStatus = rs.getString("PRSTAT");
                    }
                    if (flag) {
                        initialSerialNo = rs.getString("SERIAL_NO");
                    }
                    currProcessStatus = rs.getString("PRSTAT");
                    count++;
                    if (prevProcessStatus!= null && prevProcessStatus.equalsIgnoreCase(currProcessStatus))// when
                                                                              // previous
                                                                              // proess
                                                                              // status
                                                                              // of
                                                                              // voucher
                                                                              // is
                                                                              // same
                                                                              // as
                                                                              // current
                                                                              // Status
                                                                              // of
                                                                              // voucher
                                                                              // which
                                                                              // is
                                                                              // under
                                                                              // process
                    {
                        flag = false;
                        rsSerialNo = rs.getString("SERIAL_NO");
                        rsPreviousStat = rs.getString("PREVSTAT");
                        rsModifiedBy = rs.getString("MODIFIEDBY");
                        rsModifiedOn = BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("MODIFIEDON"));
                        rsCurrentStat = rs.getString("CURRENTSTAT");
                        rsChangeSrc = rs.getString("STATCHSRC");
                        rsMrp = rs.getLong("MRP");
                        rsExpiryDate = BTSLUtil.getVomsDateStringFromDate(rs.getTimestamp("EXPDATE"));
                        rsMessage = rs.getString("MESSAGE");
                        continue;
                    } else {
                        // when previous process status of vocher is different
                        // from current voucher process status.and previous
                        // process is success .
                        if (prevProcessStatus.equalsIgnoreCase(VOMSI.VA_PROCESS_SUCCESS_STAT)) {
                            voucherVO = new VomsVoucherVO();
                            voucherVO.setEnableBatchNo(batchVO.getBatchNo());
                            voucherVO.setSerialNo(initialSerialNo);
                            voucherVO.setToSerialNo(rsSerialNo);
                            voucherVO.setPreviousStatus(rsPreviousStat);
                            voucherVO.setPrevStatusModifiedBy(rsModifiedBy);
                            voucherVO.setPrevStatusModifiedOn(rsModifiedOn);
                            voucherVO.setVoucherStatus(rsCurrentStat);
                            voucherVO.setStatusChangeSource(rsChangeSrc);
                            voucherVO.setMRP(Double.parseDouble(PretupsBL.getDisplayAmount(rsMrp)));
                            voucherVO.setExpiryDateStr(rsExpiryDate);
                            voucherVO.setLastErrorMessage(BTSLUtil.NullToString(rsMessage));
                            voucherVO.setProcess(batchVO.getProcess());
                            voucherVO.setProductionLocationCode(batchVO.getLocationCode());
                            voucherList.add(voucherVO);
                            
                        }
                    }// previous process state is failed.
                    prevProcessStatus = rs.getString("PRSTAT");
                    initialSerialNo = rs.getString("SERIAL_NO");
                    rsSerialNo = rs.getString("SERIAL_NO");
                    rsPreviousStat = rs.getString("PREVSTAT");
                    rsModifiedBy = rs.getString("MODIFIEDBY");
                    rsModifiedOn = rs.getString("MODIFIEDON");
                    rsCurrentStat = rs.getString("CURRENTSTAT");
                    rsChangeSrc = rs.getString("STATCHSRC");
                    rsMrp = rs.getLong("MRP");
                    rsExpiryDate = rs.getString("EXPDATE");
                    rsMessage = rs.getString("MESSAGE");
                }
                if (count > 0) {
                    if (prevProcessStatus.equalsIgnoreCase(VOMSI.VA_PROCESS_SUCCESS_STAT)) {
                        voucherVO = new VomsVoucherVO();
                        voucherVO.setEnableBatchNo(batchVO.getBatchNo());
                        voucherVO.setSerialNo(initialSerialNo);
                        voucherVO.setToSerialNo(rsSerialNo);
                        voucherVO.setPreviousStatus(rsPreviousStat);
                        voucherVO.setPrevStatusModifiedBy(rsModifiedBy);
                        voucherVO.setPrevStatusModifiedOn(rsModifiedOn);
                        voucherVO.setVoucherStatus(rsCurrentStat);
                        voucherVO.setStatusChangeSource(rsChangeSrc);
                        voucherVO.setMRP(Double.parseDouble(PretupsBL.getDisplayAmount(rsMrp)));
                        voucherVO.setExpiryDateStr(rsExpiryDate);
                        voucherVO.setLastErrorMessage(BTSLUtil.NullToString(rsMessage));
                        voucherVO.setProcess(batchVO.getProcess());
                        voucherVO.setProductionLocationCode(batchVO.getLocationCode());
                        voucherList.add(voucherVO); // adding the last entry
                    }
                }
                pstmt.close();
                // update voms batches table mark status EX
                updateCount = changeBatchStatus(con, VOMSI.EXECUTED, errorCount, successCount, batchVO.getModifiedBy(), batchVO.getBatchNo(), errMessage, batchVO.get_NetworkCode());
                if (log.isDebugEnabled()) {
                    log.debug(threadName, "After exceuting update updateCount" + updateCount);
                }
                if (updateCount > 0) {
                    con.commit();
                    if (voucherList != null && voucherList.size() > 0) {
                        VomsVoucherChangeStatusLog.log(voucherList);
                    } else {

                        log.error(threadName, "Not able to get arrayList for writing to status change log file");
                        throw new BTSLBaseException(this, threadName, "btsl.error.updatelogfile");
                    }
                    batchVO.setModifiedOn(new java.util.Date());
                    batchVO.setSuccessCount(successCount);
                    batchVO.setFailCount(errorCount);
                    batchVO.setStatus(VOMSI.EXECUTED);
                    VomsBatchInfoLog.modifyBatchLog(batchVO);

                } else {
                    con.rollback();
                }

            }
        } catch (SQLException sqlex) {
            log.error(threadName, "SQLException in change status thread sqe=" + sqlex);
            log.errorTrace(methodName, sqlex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VomsChangeBatchStatusThread[VomsChangeBatchStatusThread]", "", "", "", "Exception:" + sqlex.getMessage());
            int count = 0;
            try {
                con.rollback();
            } catch (Exception e) {
                log.error(threadName, "Exception in change status thread while rollback ");
                log.errorTrace(methodName, e);
            }
            try {
            	if(request != null) {
            		count = changeBatchStatus(con, VOMSI.BATCHFAILEDSTATUS, batchVO.getNoOfVoucher(), 0, batchVO.getModifiedBy(), batchVO.getBatchNo(), ((MessageResources) request.getAttribute(Globals.MESSAGES_KEY)).getMessage(BTSLUtil.getBTSLLocale(request), "voms.changestatusthread.error.message"), batchVO.get_NetworkCode());
            	}else {

            		count = changeBatchStatus(con, VOMSI.BATCHFAILEDSTATUS, batchVO.getNoOfVoucher(), 0, batchVO.getModifiedBy(), batchVO.getBatchNo(), BTSLUtil.getMessage(locale,"voms.changestatusthread.error.message"), batchVO.get_NetworkCode());
            	}
                if (log.isDebugEnabled()) {
                    log.debug(threadName, "After exceuting update updateCount" + updateCount);
                }
                if (count > 0) {
                    con.commit();
                    batchVO.setModifiedOn(new java.util.Date());
                    batchVO.setSuccessCount(0);
                    batchVO.setFailCount(batchVO.getNoOfVoucher());
                    batchVO.setStatus(VOMSI.BATCHFAILEDSTATUS);
                    VomsBatchInfoLog.modifyBatchLog(batchVO);
                } else {
                    con.rollback();
                }
            } catch (Exception e) {
                log.error(threadName, "Exception in change status thread while rollback ");
                log.errorTrace(methodName, e);
                try {
                    con.rollback();
                } catch (Exception exc) {
                    log.error(threadName, "Exception in change status thread while rollback ");
                    log.errorTrace(methodName, exc);
                }
            }
        } catch (Exception bex) {
            log.error(threadName, "SQLException in  change status thread sqe=" + bex);
            log.errorTrace(methodName, bex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VomsChangeBatchStatusThread[VomsChangeBatchStatusThread]", "", "", "", "Exception:" + bex.getMessage());
            int count = 0;

            try {
                con.rollback();
            } catch (Exception ex) {
                log.error(threadName, "Exception in  change status thread while rollback ");
                log.errorTrace(methodName, ex);
            }
            try {
            	if(request != null) {
            		count = changeBatchStatus(con, VOMSI.BATCHFAILEDSTATUS, batchVO.getNoOfVoucher(), 0, batchVO.getModifiedBy(), batchVO.getBatchNo(), ((MessageResources) request.getAttribute(Globals.MESSAGES_KEY)).getMessage(BTSLUtil.getBTSLLocale(request), "voms.changestatusthread.error.message"), batchVO.get_NetworkCode());
            	}
            	else {
            		count = changeBatchStatus(con, VOMSI.BATCHFAILEDSTATUS, batchVO.getNoOfVoucher(), 0, batchVO.getModifiedBy(), batchVO.getBatchNo(), BTSLUtil.getMessage(locale,"voms.changestatusthread.error.message"), batchVO.get_NetworkCode());
            	}
                if (log.isDebugEnabled()) {
                    log.debug(threadName, "After exceuting update updateCount" + updateCount);
                }
                if (count > 0) {
                    con.commit();
                    batchVO.setModifiedOn(new java.util.Date());
                    batchVO.setSuccessCount(0);
                    batchVO.setFailCount(batchVO.getNoOfVoucher());
                    batchVO.setStatus(VOMSI.BATCHFAILEDSTATUS);
                    VomsBatchInfoLog.modifyBatchLog(batchVO);// modify batch log
                                                             // information
                } else {
                    con.rollback();
                }
            } catch (Exception ex) {
                log.error(threadName, "Exception in change status thread while rollback ");
                log.errorTrace(methodName, ex);
                try {
                    con.rollback();
                } catch (Exception exc) {
                    log.error(threadName, "Exception in change status thread while rollback ");
                    log.errorTrace(methodName, exc);
                }

            }
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.error(threadName, " changeBatchStatusThread  Exception while closing rs ex=" + ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                log.error(threadName, " changeBatchStatusThread  Exception while closing prepared statement ex=" + ex);
            }
            
			if (mcomCon != null) {
				mcomCon.close("VomsChangeBatchStatusThread#run");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug(threadName, " Exiting.");
            }

        }

    }


    /**
     * This method will change the batch Status after the voucher processing is
     * over.
     * 
     * @param p_con
     *            Connection
     * @param p_newStatus
     *            String
     * @param p_errorCount
     *            long
     * @param p_successCount
     *            long
     * @param p_modifiedBy
     *            String
     * @param p_batchNo
     *            String
     * @param p_errorMessage
     *            String
     * @param network_code TODO
     * @return int
     */

    private int changeBatchStatus(Connection p_con, String p_newStatus, long p_errorCount, long p_successCount, String p_modifiedBy, String p_batchNo, String p_errorMessage, String network_code) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("changeBatchStatus", " Method entered:" + p_newStatus + "  Batch No=" + p_batchNo + "  p_errorMessage=" + p_errorMessage + "  p_modifiedBy=" + p_modifiedBy+"network_code="+network_code);
        }
        final String METHOD_NAME = "changeBatchStatus";
        int i = 0;
        StringBuffer sqlLoadBuf = new StringBuffer(" UPDATE voms_batches set status=?, ");
        sqlLoadBuf.append(" total_no_of_failure=?, total_no_of_success=? , modified_date=?, modified_by=?,modified_on=?,message=?  ");
        sqlLoadBuf.append(" WHERE batch_no=? and network_code = ? ");
        if (log.isDebugEnabled()) {
            log.debug("changeBatchStatus", "Update Query=" + sqlLoadBuf.toString());
        }
        try {
        	int k=1;
            pstmt = p_con.prepareStatement(sqlLoadBuf.toString());
            pstmt.setString(k++, p_newStatus);
            pstmt.setLong(k++, p_errorCount);
            pstmt.setLong(k++, p_successCount);
            pstmt.setDate(k++, BTSLUtil.getSQLDateFromUtilDate(new Date()));
            pstmt.setString(k++, p_modifiedBy);
            pstmt.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(new Date()));
            pstmt.setString(k++, p_errorMessage);
            pstmt.setString(k++, p_batchNo);
            pstmt.setString(k++, network_code);
            i = pstmt.executeUpdate();
        } catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VomsChangeBatchStatusThread[changeBatchStatus]", "", "", "", "Exception:" + e.getMessage());
            log.error("changeBatchStatus", "SQLException in  change status sqe=" + e);
            i = 0;
            try {
                con.rollback();
            } catch (Exception ex) {
                log.errorTrace(METHOD_NAME, ex);
                log.error("changeBatchStatus", "Exception in  change status while rollback ");
            }
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                log.error("changeBatchStatus", "Exception while closing prepared statement ex=" + ex);
            }
            if (log.isDebugEnabled()) {
                log.debug("changeBatchStatus", "Exiting. with i=" + i);
            }
        }
        return i;
    }
   
 
}
