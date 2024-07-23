package com.btsl.voms.voucher.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomslogging.VomsBatchInfoLog;
import com.btsl.voms.vomslogging.VomsVoucherChangeStatusLog;

public class OnlineChangeVoucherBatchStatus implements Runnable
{
	  private  ArrayList<String> networksNotAllowed = null;
	  private  ArrayList<String>  masterBatchesNotAllowed = null;
	  private static Log _logger = LogFactory.getLog(OnlineVoucherGenerator.class.getName());
	  private static HttpServletRequest request = null;
	  private static String language = null;
	  private static String country = null;
	    
		  /**
		     * @param p_request
		     * @throws BTSLBaseException
		     */
		 public OnlineChangeVoucherBatchStatus(HttpServletRequest p_request) throws BTSLBaseException {
		        request = p_request;
		    }
		@Override
		public void run() {
			try{
				this.process();
	    	}catch(BTSLBaseException e)
	    	{
	    		_logger.debug("run", "Exception in process method");
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

    private static  int changeBatchStatus(Connection p_con, String p_newStatus, long p_errorCount, long p_successCount, String p_modifiedBy, String p_batchNo, String p_errorMessage, String network_code) throws BTSLBaseException {
        if (_logger.isDebugEnabled()) {
        	_logger.debug("changeBatchStatus", " Method entered:" + p_newStatus + "  Batch No=" + p_batchNo + "  p_errorMessage=" + p_errorMessage + "  p_modifiedBy=" + p_modifiedBy+"network_code="+network_code);
        }
        final String METHOD_NAME = "changeBatchStatus";
        MComConnectionI mcomCon = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int i = 0;
        StringBuffer sqlLoadBuf = new StringBuffer(" UPDATE voms_batches set status=?, ");
        sqlLoadBuf.append(" total_no_of_failure=?, total_no_of_success=? , modified_date=?, modified_by=?,modified_on=?,message=?  ");
        sqlLoadBuf.append(" WHERE batch_no=? and network_code = ? ");
        if (_logger.isDebugEnabled()) {
        	_logger.debug("changeBatchStatus", "Update Query=" + sqlLoadBuf.toString());
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
        	_logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VomsChangeBatchStatusThread[changeBatchStatus]", "", "", "", "Exception:" + e.getMessage());
            _logger.error("changeBatchStatus", "SQLException in  change status sqe=" + e);
            i = 0;
            try {
            	p_con.rollback();
            } catch (Exception ex) {
            	_logger.errorTrace(METHOD_NAME, ex);
            	_logger.error("changeBatchStatus", "Exception in  change status while rollback ");
            }
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
            	_logger.error("changeBatchStatus", "Exception while closing prepared statement ex=" + ex);
            }
            if (_logger.isDebugEnabled()) {
            	_logger.debug("changeBatchStatus", "Exiting. with i=" + i);
            }
        }
        return i;
    }
	  private static String getMessageResource(String key) {
	    	try {   	
	    	Locale curLoc = new Locale(language, country);
	     	String resourcesPath= Constants.getProperty("MESSAGE_RESOURCE_CONFG_PATH");//";
	        ResourceBundle words
	                = ResourceBundle.getBundle(resourcesPath, curLoc);
	        return words.getString(key);
	    	}catch(Exception e) {
	    		_logger.debug("getMessageResource", "Exception while retrieving value from MessageResoures properties , lan: " + language+" country: "+country);
	    	}
	    	return null;
	    }
	  public void process() throws BTSLBaseException {
	    	 final String METHOD_NAME = "process";
	    	 ProcessBL processBL = null;	
	    	 Date currentDate = null;
	    	 int updateCount = 0;
	    	 String processId = PretupsI.CHANGE_STATUS_ONLINE;
	    	 String processCounterId=PretupsI.CHANGE_STATUS_ONLINE_NETWORK_COUNTER;
	    	 ProcessStatusVO processStatusVO = null, _processStatusVOForSelectedNetwork = null;
	    	 String networkCode = null;
	    	 VomsBatchesDAO vomsBatchesDAO = null;
	    	 Connection con = null;
	    	 int onlineVoucherAllowedCount = 0;
	    	 ProcessStatusDAO processStatusDao=null;
	    	 MComConnectionI mcomCon=null;
			 try  {
				 mcomCon = new MComConnection();
				 con = mcomCon.getConnection();
				 currentDate = BTSLUtil.getTimestampFromUtilDate(new Date());
				 processStatusDao= new ProcessStatusDAO();
				 HttpServletRequest pRequest = null;
				 int systemLimit =  ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ONLINE_CHANGE_STATUS_SYSTEM_LMT))).intValue();
				 processBL = new ProcessBL();
			     processStatusVO = processBL.checkProcessUnderProcess(con,processId);
			     int networkRecordCount = 0;
			     long maxerrorcount;
			     boolean isBatchAvaibale = true;
				     if (processStatusVO.isStatusOkBool()) {
				    	 mcomCon.partialCommit();
					    	//Resetting the record counts to zero for every network code if the date changes
					        if(BTSLUtil.getDifferenceInUtilDates(currentDate, processStatusVO.getExecutedUpto()) != 0) {
					        	int updateCount2 = 0;
					        	updateCount2 = (new ProcessStatusDAO()).resetProcessRecordCounts(con,processCounterId);
					        	if(updateCount2 > 0) {
					        		con.commit();
					        	}
					        	networksNotAllowed = null;
					        }
					       int systemRecordCount = processStatusDao.getRecordCountSumForProcess(con,processCounterId);
					       if(systemRecordCount < systemLimit) {
					    	while(isBatchAvaibale)
					    	{
					    		currentDate = BTSLUtil.getTimestampFromUtilDate(new Date());
					    		int daysDiff = 0;
					    		if(BTSLUtil.getDifferenceInUtilDates(processStatusVO.getStartDate(),currentDate) != daysDiff) {
					            	int updateCount2 = 0;
					            	updateCount2 = (new ProcessStatusDAO()).resetProcessRecordCounts(con, processCounterId);
					            	if(updateCount2 > 0) {
					            		con.commit();
					            	}
					            	daysDiff = BTSLUtil.getDifferenceInUtilDates(processStatusVO.getStartDate(),currentDate);
					            	networksNotAllowed = null;
					            }
								if(Constants.getProperty("VOMS_CHANGE_GEN_STATUS_ONLINE_COUNT") != null && Constants.getProperty("VOMS_CHANGE_GEN_STATUS_ONLINE_COUNT").isEmpty()){
									onlineVoucherAllowedCount = 1000;
								} else {
							    	onlineVoucherAllowedCount = Integer.parseInt(Constants.getProperty("VOMS_CHANGE_GEN_STATUS_ONLINE_COUNT"));
							    }
					    		ArrayList<VomsBatchVO> onlinechnstatusbtchlist = new VomsBatchesDAO().getOnlineVoucherBatchListForChangeStatus(con,networksNotAllowed,masterBatchesNotAllowed);
					    		if(onlinechnstatusbtchlist != null && onlinechnstatusbtchlist.size() > 0) {
					    			VomsBatchVO vb = onlinechnstatusbtchlist.get(0);
					    			vomsBatchesDAO = new VomsBatchesDAO();
					    			int voucherCounter = vomsBatchesDAO.getNumberOfVouchersForMasterBatch(con,vb.getMasterBatchNo());
					    			if( vb != null && !("NA".equals(vb.getMasterBatchNo())) && voucherCounter <= onlineVoucherAllowedCount ) {
					    			networkCode = vb.get_NetworkCode();
						        	_processStatusVOForSelectedNetwork = new ProcessStatusDAO().loadProcessDetailNetworkWise(con,processCounterId,networkCode);
						        	networkRecordCount = _processStatusVOForSelectedNetwork.getRecordCount();
									int networkLimit = Integer.parseInt(PreferenceCache.getNetworkPrefrencesValue(PreferenceI.ONLINE_CHANGE_STATUS_NETWORK_LMT,networkCode).toString());
									if(BTSLUtil.isNullString(PreferenceCache.getNetworkPrefrencesValue(PreferenceI.ONLINE_CHANGE_STATUS_NETWORK_LMT,networkCode).toString())) {
										networkLimit =  ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ONLINE_CHANGE_STATUS_NETWORK_LMT))).intValue();
									}
									if(networkLimit - networkRecordCount >= 0)  {
										if(voucherCounter <= networkLimit - networkRecordCount)
										{
										if(vb.getProcessScreen() == 1) {
											maxerrorcount = ((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_MAX_ERROR_COUNTEN))).longValue();
										}
										else {
											maxerrorcount=((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_MAX_ERROR_COUNTOTH))).longValue();
										}
										 boolean isDataProcessed = changeVouchersStatusOnline(con,vb,maxerrorcount,vb.getProcessScreen());
										 if (isDataProcessed) {
											 EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VomsChangeBatchStatusThread[process]", "", "", "", " VomsChangeBatchStatusThread process has been executed successfully.");
											 if (_logger.isDebugEnabled()) {
												 _logger.debug("process", " successfully");
											 }
										 }
										 //int updateCount1 = new ProcessStatusDAO().updateProcessDetailNetworkWiseOnlineChangeOthStatus(con, _processStatusVOForSelectedNetwork,(int)vb.getNoOfVoucher());
										 int updateCount1 = new ProcessStatusDAO().updateProcessDetailNetworkWiseOnlineChangeOthStatus(con, _processStatusVOForSelectedNetwork,BTSLUtil.parseLongToInt(vb.getNoOfVoucher()));
										 if(updateCount1 > 0) {
										    con.commit();
										 }
										}
										else
										{
											if(masterBatchesNotAllowed != null) {
												masterBatchesNotAllowed.add(vb.getMasterBatchNo());
											} else {
												masterBatchesNotAllowed = new ArrayList<>();
												masterBatchesNotAllowed.add(vb.getMasterBatchNo());
											}
										}
									} else {
										if(networksNotAllowed != null) {
											networksNotAllowed.add(networkCode);
										} else {
											networksNotAllowed = new ArrayList<>();
											networksNotAllowed.add(networkCode);
										}
									}
					    		  }	else {
					    			  if(masterBatchesNotAllowed != null) {
											masterBatchesNotAllowed.add(vb.getMasterBatchNo());
										} else {
											masterBatchesNotAllowed = new ArrayList<>();
											masterBatchesNotAllowed.add(vb.getMasterBatchNo());
										}
					    		  }
					    		}
		 			    		else {
					    			isBatchAvaibale = false;// To Stop the While loop
					    		}
					    	}
					       }
				     } else {
				         throw new BTSLBaseException("VomsChangeBatchStatusThread", "process", PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
				     }
			 } catch(SQLException sqle){
					try {
						if(mcomCon != null){
							mcomCon.partialRollback();
						}
					} catch (Exception ee) {
						_logger.errorTrace(METHOD_NAME, ee);
					}
				 _logger.error("process", "BTSLBaseException : " + sqle.getMessage());
		     } catch (BTSLBaseException be) {
		    		try {
						if(mcomCon != null){
							mcomCon.partialRollback();
						}
					} catch (Exception ee) {
						_logger.errorTrace(METHOD_NAME, ee);
					}
		    	 _logger.error("process", "BTSLBaseException : " + be.getMessage());
		    	 throw be;
		     } catch (Exception e) {
		    		try {
						if(mcomCon != null){
							mcomCon.partialRollback();
						}
					} catch (Exception ee) {
						_logger.errorTrace(METHOD_NAME, ee);
					}
		    	 _logger.error("process", "Exception : " + e.getMessage());
		    	 _logger.errorTrace("process", e);
		    	 EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VomsChangeBatchStatusThread[process]", "", "", "", " VomsChangeBatchStatusThread process could not be executed successfully.");
		    	 throw new BTSLBaseException("VomsChangeBatchStatusThread", "process", PretupsErrorCodesI.ERROR_VOMS_GEN,e);
		     } finally {
		    	 try {
		    		 if (processStatusVO.isStatusOkBool()) {
		    			 processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
		    			 processStatusVO.setExecutedOn(currentDate);
		    			 processStatusVO.setExecutedUpto(currentDate);
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
    
    public static boolean changeVouchersStatusOnline(Connection con, VomsBatchVO batchVO,long maxErrorAllowed,int processScreen)
    {
    	MComConnectionI mcomCon = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    int updateCount=0 ;
	    boolean isStatuschangesuccess=false;
	    final String methodName = "changeVouchersStatusOnline";
    	try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            if (_logger.isDebugEnabled()) {
            	_logger.debug(methodName, "Before Calling the package. Input values: Generated Batch No:=" + batchVO.getBatchNo() + "  From Serial No:=" + batchVO.getFromSerialNo() + "To Serial No:=" + batchVO.getToSerialNo() + "  Max Eeror Entries allowed :=" + maxErrorAllowed);
            	_logger.debug(methodName, "No. of Vouchers :=" + batchVO.getNoOfVoucher() + "  Input Screen  :=" + processScreen + "New Status  :=" + batchVO.getBatchType() + "  Date   :=" + batchVO.getCreatedDate());
            	_logger.debug(methodName, "batchVO.getRcAdminDaysAllowed()::" + batchVO.getRcAdminDaysAllowed());
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
                if (_logger.isDebugEnabled()) {
                	_logger.debug(methodName, "After exceuting update updateCount" + updateCount);
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
                isStatuschangesuccess=false;
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
                if (_logger.isDebugEnabled()) {
                	_logger.debug(methodName, "Select Query=" + sqlSelectBuf.toString());
                }
                pstmt = con.prepareStatement(sqlSelectBuf.toString());
                pstmt.setString(1, batchVO.getBatchNo());
                rs = pstmt.executeQuery();
                if (_logger.isDebugEnabled()) {
                	_logger.debug(methodName, "After executing query ");
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
                    if (prevProcessStatus.equalsIgnoreCase(currProcessStatus))// when
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
                if (_logger.isDebugEnabled()) {
                	_logger.debug(methodName, "After exceuting update updateCount" + updateCount);
                }
                if (updateCount > 0) {
                    con.commit();
                    if (voucherList != null && voucherList.size() > 0) {
                        VomsVoucherChangeStatusLog.log(voucherList);
                    } else {

                    	_logger.error(methodName, "Not able to get arrayList for writing to status change log file");
             //           throw new BTSLBaseException(this, methodName, "btsl.error.updatelogfile");
                    }
                    batchVO.setModifiedOn(new java.util.Date());
                    batchVO.setSuccessCount(successCount);
                    batchVO.setFailCount(errorCount);
                    batchVO.setStatus(VOMSI.EXECUTED);
                    isStatuschangesuccess=true;
                    VomsBatchInfoLog.modifyBatchLog(batchVO);

                } else {
                    con.rollback();
                    isStatuschangesuccess=false;
                }
            }
        } catch (SQLException sqlex) {
        	_logger.error(methodName, "SQLException in change status thread sqe=" + sqlex);
        	_logger.errorTrace(methodName, sqlex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VomsChangeBatchStatusThread[VomsChangeBatchStatusThread]", "", "", "", "Exception:" + sqlex.getMessage());
            int count = 0;
            try {
                con.rollback();
            } catch (Exception e) {
            	_logger.error(methodName, "Exception in change status thread while rollback ");
            	_logger.errorTrace(methodName, e);
            }
            try {
            	if(request != null) {
            		count = changeBatchStatus(con, VOMSI.BATCHFAILEDSTATUS, batchVO.getNoOfVoucher(), 0, batchVO.getModifiedBy(), batchVO.getBatchNo(), ((MessageResources) request.getAttribute(Globals.MESSAGES_KEY)).getMessage(BTSLUtil.getBTSLLocale(request), "voms.changestatusthread.error.message"), batchVO.get_NetworkCode());
            	}else {
            		count = changeBatchStatus(con, VOMSI.BATCHFAILEDSTATUS, batchVO.getNoOfVoucher(), 0, batchVO.getModifiedBy(), batchVO.getBatchNo(), getMessageResource("voms.changestatusthread.error.message"), batchVO.get_NetworkCode());
            	}
            	
                if (_logger.isDebugEnabled()) {
                	_logger.debug(methodName, "After exceuting update updateCount" + updateCount);
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
                isStatuschangesuccess=false;
            } catch (Exception e) {
            	_logger.error(methodName, "Exception in change status thread while rollback ");
            	_logger.errorTrace(methodName, e);
                try {
                    con.rollback();
                } catch (Exception exc) {
                	_logger.error(methodName, "Exception in change status thread while rollback ");
                	_logger.errorTrace(methodName, exc);
                }
            }
            isStatuschangesuccess=false;
        } catch (Exception bex) {
        	_logger.error(methodName, "SQLException in  change status thread sqe=" + bex);
        	_logger.errorTrace(methodName, bex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VomsChangeBatchStatusThread[VomsChangeBatchStatusThread]", "", "", "", "Exception:" + bex.getMessage());
            int count = 0;

            try {
                con.rollback();
            } catch (Exception ex) {
            	_logger.error(methodName, "Exception in  change status thread while rollback ");
            	_logger.errorTrace(methodName, ex);
            }
            try {
            	if(request != null) {
            		count = changeBatchStatus(con, VOMSI.BATCHFAILEDSTATUS, batchVO.getNoOfVoucher(), 0, batchVO.getModifiedBy(), batchVO.getBatchNo(), ((MessageResources) request.getAttribute(Globals.MESSAGES_KEY)).getMessage(BTSLUtil.getBTSLLocale(request), "voms.changestatusthread.error.message"), batchVO.get_NetworkCode());
            	}
            	else {
            		count = changeBatchStatus(con, VOMSI.BATCHFAILEDSTATUS, batchVO.getNoOfVoucher(), 0, batchVO.getModifiedBy(), batchVO.getBatchNo(), getMessageResource("voms.changestatusthread.error.message"), batchVO.get_NetworkCode());
            	}
                if (_logger.isDebugEnabled()) {
                	_logger.debug(methodName, "After exceuting update updateCount" + updateCount);
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
            	_logger.error(methodName, "Exception in change status thread while rollback ");
            	_logger.errorTrace(methodName, ex);
                try {
                    con.rollback();
                } catch (Exception exc) {
                	_logger.error(methodName, "Exception in change status thread while rollback ");
                	_logger.errorTrace(methodName, exc);
                }

            }
            isStatuschangesuccess=false;
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
            	_logger.error(methodName, " changeBatchStatusThread  Exception while closing rs ex=" + ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
            	_logger.error(methodName, " changeBatchStatusThread  Exception while closing prepared statement ex=" + ex);
            }
            
			if (mcomCon != null) {
				mcomCon.close("VomsChangeBatchStatusThread#run");
				mcomCon = null;
			}
            if (_logger.isDebugEnabled()) {
            	_logger.debug(methodName, " Exiting.");
            }

        }
	
    	return isStatuschangesuccess;
    }

}
