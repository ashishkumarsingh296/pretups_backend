package com.btsl.voms.voucher.businesslogic;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.EMailSender;
import com.btsl.common.PretupsRestUtil;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.OracleUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomslogging.VomsBatchInfoLog;
import com.btsl.voms.vomslogging.VomsVoucherChangeStatusLog;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

public class VomsPinExpiryExtGenerator {
	
	  private static Log _logger = LogFactory.getLog(VomsPinExpiryExtGenerator.class.getName());
	    private long seed;
	    private static long starttime = System.currentTimeMillis();
	    
	    public static void main(String args[]) {
	        final String METHOD_NAME = "main";
	        try {
	            if (args.length < 2 || args.length > 3) {
	                System.out.println("Usage : VomsPinExpiryExtGenerator [Constants file] [LogConfig file] [Y/N]");
	                return;
	            }
	            // load constants.props
	            File constantsFile = new File(args[0]);
	            if (!constantsFile.exists()) {
	                System.out.println("VomsPinExpiryExtGenerator" + " Constants File Not Found .............");
	                _logger.error("VomsPinExpiryExtGenerator[main]", "Constants file not found on location: " + constantsFile.toString());
	                return;
	            }
	            // load log config file
	            File logFile = new File(args[1]);
	            if (!logFile.exists()) {
	                System.out.println("VomsPinExpiryExtGenerator" + " Logconfig File Not Found .............");
	                _logger.error("VomsPinExpiryExtGenerator[main]", "Logconfig File not found on location: " + logFile.toString());
	                return;
	            }
	            ConfigServlet.loadProcessCache(constantsFile.toString(), logFile.toString());
	        }// end of try block
	        catch (Exception e) {
	            if (_logger.isDebugEnabled()) {
	                _logger.debug("main", " Error in Loading Files ...........................: " + e.getMessage());
	            }
	            _logger.errorTrace(METHOD_NAME, e);
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
	            _logger.info("process ", "Entered ");
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
	        try {
	            processId = ProcessI.VOMSPINEXP;
	            con = OracleUtil.getSingleConnection();
	            processBL = new ProcessBL();
	            processStatusVO = processBL.checkProcessUnderProcess(con, processId);
	            beforeInterval = BTSLUtil.parseLongToInt( processStatusVO.getBeforeInterval() / (60 * 24) );
	            if (processStatusVO.isStatusOkBool()) {
	                // method call to find maximum date till which process has been
	                // executed
	                processedUpto = processStatusVO.getExecutedUpto();
	                if (processedUpto != null) {
	                    currentDate = BTSLUtil.getTimestampFromUtilDate(new Date());
	                    
	                    con.commit();
	                    processedUpto = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(currentDate));
	                    processedUpto = currentDate;
	                    // call process for uploading transfer details
	                    boolean isDataProcessed = generateVouchersPinExp(con);
	                    if (isDataProcessed) {
	                        processStatusVO.setExecutedUpto(BTSLUtil.addDaysInUtilDate(processedUpto, -beforeInterval));
	                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VomsPinExpiryExtGenerator[process]", "", "", "", " VomsPinExpiryExtGenerator process has been executed successfully.");
	                        if (_logger.isDebugEnabled()) {
	                            _logger.debug("process", " successfully");
	                        }
	                    }
	                } else {
	                    throw new BTSLBaseException("VomsPinExpiryExtGenerator", "process", PretupsErrorCodesI.ERROR_VOMS_PIN_EXP_EXT);
	                }
	            } else {
	                throw new BTSLBaseException("VomsPinExpiryExtGenerator", "process", PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
	            }
	        } catch (BTSLBaseException be) {
	            _logger.error("process", "BTSLBaseException : " + be.getMessage());
	            throw be;
	        } catch (Exception e) {
	            _logger.error("process", "Exception : " + e.getMessage());
	            _logger.errorTrace(METHOD_NAME, e);
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherGenerator[process]", "", "", "", " VoucherGenerator process could not be executed successfully.");
	            throw new BTSLBaseException("VomsPinExpiryExtGenerator", "process", PretupsErrorCodesI.ERROR_VOMS_GEN,e);
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

	    public static boolean generateVouchersPinExp(Connection p_con) throws SQLException, BTSLBaseException {
	        if (_logger.isDebugEnabled()) {
	            _logger.info("generateVouchers", "Entered p_startDate:");
	        }
	        final String METHOD_NAME = "generateVouchersPinExp";
	        boolean isDataProcessed = false;
	        ArrayList <VomsVoucherVO>batchList = null;
	        int update = 0;
	        VomsPinExpiryExtGenerator vomsPinExpiryExtGenerator = null;
	        VomsPinExpiryDateExtensionDAO vomsPinExpiryDateExtensionDAO = null;
	        try {
		        batchList = new ArrayList<VomsVoucherVO>();
	            vomsPinExpiryExtGenerator = new VomsPinExpiryExtGenerator();
	            vomsPinExpiryDateExtensionDAO = new VomsPinExpiryDateExtensionDAO();
	            batchList = vomsPinExpiryDateExtensionDAO.loadBatchPinExpiryList(p_con, VOMSI.BATCH_INTIATED);
	            final Locale locale = BTSLUtil.getSystemLocaleForEmail();

	             for(VomsVoucherVO batchVO : batchList){
	                _logger.info("generateVouchersPinExp", "BATCH proceesing started:" + batchVO.getBatchNo());
	    			int count=vomsPinExpiryDateExtensionDAO.updateExpiryDate(p_con, batchVO);
	    			if (count < 0) {
	    					 p_con.rollback();
	    					 _logger.info("generateVouchersPinExp", "BATCH is not proceesed successfully:No voucher found for Batch No." + batchVO.getBatchNo());
	    			}else{
	    				//updateCount=BTSLUtil.getInsertCount(updateCount);
					 if (_logger.isDebugEnabled()) {
	                    _logger.debug("generateVouchersPinExp", "Update  batch table");
					 }
					 batchVO.setStatus(VOMSI.EXECUTED);
					 batchVO.setTotal_success(Integer.toString(count));
					 batchVO.setTotal_failure(Long.toString(batchVO.get_totalVouchers()-count));
					 update = vomsPinExpiryDateExtensionDAO.updateVomsPinExpExt(p_con, batchVO);
					 if (update < 0) {
						 p_con.rollback();
					 	}
					 p_con.commit();
					 _logger.info("generateVouchersPinExp", "BATCH proceesed successfully:" + batchVO.getBatchNo());
					 VomsVoucherChangeStatusLog.expiryLog(batchVO);
			            final String[] messArgArray = {batchVO.getTotal_success(),Long.toString(batchVO.get_totalVouchers()),batchVO.getTotal_failure() };
						String message = BTSLUtil.getMessage(locale,"voucher.total.no.of.vouchers.limit.batch.process",messArgArray);
						sendEmailNotification(p_con, batchVO,message, "voucher.expiry.date.notification");
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
	        }
	        return isDataProcessed;
	    }
	    
	    
	    private static void sendEmailNotification(Connection p_con, VomsVoucherVO _requestVO, String messages,String p_subject) {
			final String methodName = "sendEmailNotification";
	        final Locale locale = BTSLUtil.getSystemLocaleForEmail();
			if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, "Entered ");
			}
			try {
				String cc = PretupsI.EMPTY;
				final String bcc = "";
				String subject = "";
				ChannelUserWebDAO userWebDAO = new ChannelUserWebDAO();
				String emailID = userWebDAO.loadUserEmail(p_con, _requestVO.getCreatedBy());
				final boolean isAttachment = false;
				final String pathofFile = "";
				final String fileNameTobeDisplayed = "";
				subject =PretupsRestUtil.getMessageString(p_subject);
				if (!BTSLUtil.isNullString(emailID)) {
					EMailSender.sendMail(emailID, "", bcc, cc, subject, messages, isAttachment, pathofFile, fileNameTobeDisplayed);
				}
				if (_logger.isDebugEnabled()) {
					_logger.debug("MAIL CONTENT ", messages);
				}
			} catch (Exception e) {
				if (_logger.isDebugEnabled()) {
					_logger.error("sendEmailNotification ", " Email sending failed" + e.getMessage());
				}
				_logger.errorTrace(methodName, e);
			}
			if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, "Exiting ....");
			}
		}

	    
}
