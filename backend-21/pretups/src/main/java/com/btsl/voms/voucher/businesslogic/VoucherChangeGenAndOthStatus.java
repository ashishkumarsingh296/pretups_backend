package com.btsl.voms.voucher.businesslogic;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
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
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.voms.vomslogging.VomsBatchInfoLog;
import com.web.voms.voucher.businesslogic.VomsBatchesWebDAO;

/**
 * @author akanksha This class is used for changing voucher status, it calls
 *         thread that changes generated status and other status of voucher.
 */
public class VoucherChangeGenAndOthStatus {

	private static Log log = LogFactory
			.getLog(VoucherChangeGenAndOthStatus.class.getName());
	
	/**
	 * @param arg
	 * @throws java.sql.SQLException
	 */
	public static void main(String[] arg){
		final String methodName = "main";
		Connection con = null;
		MComConnectionI mcomCon = null;
		Date currentDate = null;
		loadPropertiesFiles(arg, methodName);// end catch
		try {

			currentDate = new Date();
			currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
			// Getting database connection
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();

			
			if (con == null) {
				LogFactory.printLog(methodName,
						" DATABASE Connection is NULL. ", log);
				throw new BTSLBaseException(
						"Voucher Change Generate/other status process",
						methodName, "Not able to get the connection.");
			}

			
			new VoucherChangeGenAndOthStatus().process(con);
		} catch (BTSLBaseException bse) {
			LogFactory.printLog(methodName, " " + bse.getMessage(), log);
			log.errorTrace(methodName, bse);
		} catch (Exception e) {
			LogFactory.printLog(methodName, " " + e.getMessage(), log);
			log.errorTrace(methodName, e);
		} finally {
			if(mcomCon != null){
				mcomCon.close("VoucherChangeGenAndOthStatus#main");
				mcomCon=null;
				}
			LogFactory.printLog(methodName, "Exiting", log);
			ConfigServlet.destroyProcessCache();
		}
		log.debug(methodName, "exiting main");
	}

	private static void loadPropertiesFiles(String[] arg,
			final String methodName) {
		try {

			final String constnt = arg[0];
			final File constantsFile = new File(constnt);

			if (!constantsFile.exists()) {
				log.debug(methodName, " Constants File Not Found .............");
				return;
			}
			final String logconfig = arg[1];
			final File logconfigFile = new File(logconfig);

			if (!logconfigFile.exists()) {
				log.debug(methodName, " Logconfig File Not Found .............");
				return;
			}
			ConfigServlet.loadProcessCache(constantsFile.toString(),
					logconfigFile.toString());
		}// // end try
		catch (Exception e) {
			log.debug(methodName,
					"Error in Loading Configuration files ...........................: "
							+ e);
			log.errorTrace(methodName, e);
			ConfigServlet.destroyProcessCache();
			return;
		}
	}

	/**
	 * @param pCon
	 * @throws BTSLBaseException
	 * @throws java.sql.SQLException
	 */
	public void process(Connection pCon) throws BTSLBaseException,
			java.sql.SQLException {
		final String methodName = "process";
		ProcessStatusVO processStatusVO = null;
		Date currentDate = new Date();
		int updateCount = 0;
		Date processUpto = null;
		List<VomsBatchVO> batches = new ArrayList<VomsBatchVO>();
		try {
			final ProcessBL processBL = new ProcessBL();
			processStatusVO = processBL.checkProcessUnderProcess(pCon,
					ProcessI.VOMS_CHANGE_STATUS_PROCESS);
			VomsBatchInfoLog
					.log("Process :Voucher Change status process started");
			// check process status.
			if (processStatusVO.isStatusOkBool()) {
				processUpto = processStatusVO.getExecutedUpto();
				currentDate = BTSLUtil.getTimestampFromUtilDate(new Date());
				if (processUpto != null) {
					processUpto = BTSLUtil.getDateFromDateString(BTSLUtil
							.getDateStringFromDate(processUpto));
					final int diffDate = BTSLUtil.getDifferenceInUtilDates(
							processUpto, currentDate);
					if(null==Constants.getProperty("ALLOW_CHANGE_STATUS_PROCESS_FOR_CURRENT") || Constants.getProperty("ALLOW_CHANGE_STATUS_PROCESS_FOR_CURRENT").equalsIgnoreCase(PretupsI.NO))
					{
					if (diffDate <= 0) {
						log.error(methodName, " Process already executed.....");
						 // If process is already ran for the last day then do not run again
			                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherChangeGenAndOthStatu[process]", "",
			                    "", "", "Voms change status process already run for the date "+currentDate);
			                return;
						}
					}
					
					HttpServletRequest pRequest = null;
					double startTime = System.currentTimeMillis();
					log.debug(methodName, "Start Time @@@@@@ " + startTime);
					VomsBatchesWebDAO vomsbatchesDAO = new VomsBatchesWebDAO();
					batches = vomsbatchesDAO.loadBatchesForStatusChange(pCon);
					if(batches==null || batches.size()<=0)
					{
						log.error(methodName, " Process :No voucher batch found for change status process.");
						 // If process is already ran for the last day then do not run again
			                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherChangeGenAndOthStatu[process]", "",
			                    "", "", " Process :No voucher batch found for change status process.");
			                return;
						
					}					
					String ntwrkCode = batches.get(0).getLocationCode();
					
					callChangeThread(batches, pRequest);
					
					log.debug(methodName, "total time taken  @@@@@@@ = "
							+ (System.currentTimeMillis() - startTime) + " ms");
					processStatusVO.setExecutedUpto(currentDate);

					EventHandler
							.handle(EventIDI.SYSTEM_INFO,
									EventComponentI.SYSTEM,
									EventStatusI.RAISED,
									EventLevelI.MAJOR,
									"Change voucher status[" + methodName + "]",
									"", "", "",
									" Change status process has been completed. For batch details kindly refer to view batch list screen.");
					log.debug("process",
							"Change voucher status has been Executed  For batch details kindly refer to view batch list screen.");
					VomsBatchInfoLog
							.log("process :Change voucher statuss has been Executed ");

					

				}
			} else {
				throw new BTSLBaseException("Change status Process ",
						methodName, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
			}

		}// / end try
		catch (Exception e) {
			pCon.rollback();
			log.error("Voucher change status ::",
					"Exception : " + e.getMessage());
			log.errorTrace(methodName, e);
			EventHandler
					.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM,
							EventStatusI.RAISED, EventLevelI.MAJOR,
							"VOMS_CHANGE_STATUS[" + methodName + "]", "", "",
							"",
							" Voms change status process could not be executed successfully.");
			throw new BTSLBaseException("Change status ::", methodName,
					PretupsErrorCodesI.VOMS_CHANGE_STATUS_ERROR);
		} finally {
			try {
				if (processStatusVO != null && processStatusVO.isStatusOkBool()) {
					processStatusVO.setStartDate(currentDate);
					processStatusVO.setExecutedOn(currentDate);
					processStatusVO.setExecutedUpto(currentDate);
					processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
					updateCount = (new ProcessStatusDAO()).updateProcessDetail(
							pCon, processStatusVO);
					if (updateCount > 0) {
						pCon.commit();
					}
				}
				VomsBatchInfoLog
						.log("process :Voucher change status process END");
			} catch (Exception ex) {
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exception in closing connection ");
				}
				log.errorTrace(methodName, ex);
			}
			if (pCon != null) {
				try {
					pCon.close();
				} catch (SQLException e1) {
					log.errorTrace(methodName, e1);
				}
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting..... ");
			}
		}
	}

	private void callChangeThread(List<VomsBatchVO> batches,
			HttpServletRequest pRequest) throws BTSLBaseException {
		VomsBatchVO innerBatchVO;
		long maxErrorCount;
	 
		if (!batches.isEmpty()) {
			 int batchesSize = 	batches.size();
			for (int i = 0; i < batchesSize; i++) {
				innerBatchVO = batches.get(i);
				if(innerBatchVO.getProcessScreen()==1){
					maxErrorCount=((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_MAX_ERROR_COUNTEN))).longValue();
				}
				else {
					maxErrorCount=((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_MAX_ERROR_COUNTOTH))).longValue();
				}
				VomsChangeBatchStatusThread cThread = new VomsChangeBatchStatusThread(
						innerBatchVO, maxErrorCount, pRequest,
						innerBatchVO.getProcessScreen());
				cThread.run();
			}
		}
	}


}
