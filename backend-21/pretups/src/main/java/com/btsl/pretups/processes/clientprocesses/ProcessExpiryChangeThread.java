package com.btsl.pretups.processes.clientprocesses;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileCache;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductCache;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucher.businesslogic.VomsBatchExpiryVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

/***
 * this thread is responsible for changing expiry date of vouchers
 * 
 * @author
 *
 */
public class ProcessExpiryChangeThread implements Runnable {

	private static OperatorUtilI calculatorI = null;
	private static Log log = LogFactory.getLog(ProcessExpiryChangeThread.class.getName());

	public ProcessExpiryChangeThread() {
	}
	
	/*
	 * If number of vouchers in the expiry date change request is less than online limit, than the request will call this method.
	 */
	public HashMap<String, ArrayList<String>> normalProcess(VomsBatchExpiryVO vomsBatchExpiryVO) {
		final String methodName = "normalProcess";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		VomsVoucherDAO vomsVoucherDAO = null;
		Date currentDate = new Date();
		HashMap<String, ArrayList<String>> map = new HashMap<>();
		ArrayList<String> successList = new ArrayList<>();
		ArrayList<String> failureList = new ArrayList<>();
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			vomsVoucherDAO = new VomsVoucherDAO();
			if (BTSLUtil.isNullString(vomsBatchExpiryVO.getFilename()))
				map = vomsVoucherDAO.changeVoucherExpiryDate(con, vomsBatchExpiryVO);
			else {
				ArrayList<VomsVoucherVO> voList = readFromFile(vomsBatchExpiryVO.getFilename());
				map = vomsVoucherDAO.changeVoucherExpiryDate(con, voList, vomsBatchExpiryVO.getBatchNo());
			}
			vomsBatchExpiryVO.setSuccessCount(map.get("successList").size());
			vomsBatchExpiryVO.setFailCount(map.get("failureList").size());
			vomsBatchExpiryVO.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(currentDate));
			vomsBatchExpiryVO.setModifiedBy(PretupsI.SYSTEM);
			vomsBatchExpiryVO.setExecutionStatus(VOMSI.EXECUTED);
			vomsBatchExpiryVO.setStatus(VOMSI.VOMS_CLOSED_STATUS);
			int updateCount = vomsVoucherDAO.updateVomsBatchesExpiry(con, vomsBatchExpiryVO);
			if (updateCount > 0) {
				con.commit();
				successList = map.get("successList");
				failureList = map.get("failureList");
				String finalPath = createDirectory();
				writeToFile(vomsBatchExpiryVO, successList, failureList, finalPath);
			}
		} catch (BTSLBaseException be) {
			log.errorTrace(methodName, be);
		} catch (Exception e) {
			log.errorTrace(methodName, e);
		} finally {
			try {
				mcomCon.finalCommit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (mcomCon != null) {
				mcomCon.close("VoucherExpiryChangeHandler#process");
				mcomCon = null;
			}
		}
		return map;
	}

	public static void main(String arg[]) {
		final String METHOD_NAME = "main";
		Connection con = null;
		MComConnectionI mcomCon = null;

		boolean statusOk = false;
		statusOk = true;
		Date currentDate = null;
		try {

			final String constnt = arg[0];
			final File constantsFile = new File(constnt);

			if (!constantsFile.exists()) {
				if (log.isDebugEnabled()) {
					log.debug(METHOD_NAME, "Constants File Not Found .............");
				}
				return;
			}
			final String logconfig = arg[1];
			final File logconfigFile = new File(logconfig);

			if (!logconfigFile.exists()) {
				if (log.isDebugEnabled()) {
					log.debug(METHOD_NAME, " Logconfig File Not Found .............");
				}
				return;
			}
			ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
			TransferProfileProductCache.loadTransferProfileProductsAtStartup();
			TransferProfileCache.loadTransferProfileAtStartup();
			final String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
			try {
				calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();

			} catch (Exception e) {
				log.errorTrace(METHOD_NAME, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL, "ChannelTransferBL[initialize]", "", "", "",
						"Exception while loading the class at the call:" + e.getMessage());
			}
		} // // end try
		catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.debug("Error in Loading Configuration files ...........................: ", e);
			}
			log.errorTrace(METHOD_NAME, e);
			ConfigServlet.destroyProcessCache();
			return;
		} // end catch
		try {

			currentDate = new Date();
			currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
			// Getting database connection
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			if (con == null) {
				if (log.isDebugEnabled()) {
					log.debug("process", " DATABASE Connection is NULL. ");
				}
				throw new BTSLBaseException("ProcessExpiryChangeThread", "process", "Not able to get the connection.");
			}

			ProcessExpiryChangeThread obj = new ProcessExpiryChangeThread();
			Thread thread = new Thread(obj);
			thread.start();

		} catch (BTSLBaseException bse) {

			if (log.isDebugEnabled()) {
				log.debug("main", " " + bse.getMessage());
			}
			log.errorTrace(METHOD_NAME, bse);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.debug("main", " " + e.getMessage());
			}
			log.errorTrace(METHOD_NAME, e);
		} finally {
			if (statusOk) {

				if (mcomCon != null) {
					mcomCon.close("ProcessExpiryChangeThread#DistribuidorDataRequestType.java");
					mcomCon = null;
				}

				if (log.isDebugEnabled()) {
					log.info("main", "Exiting");
				}

				try {
					Thread.sleep(10000);
					ConfigServlet.destroyProcessCache();
				} catch (InterruptedException e1) {
					log.errorTrace(METHOD_NAME, e1);
				}

			}
		}
		System.out.println("exiting main");
	}

	@Override
	public void run() {
		final String methodName = "run";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		VomsVoucherDAO vomsVoucherDAO = null;
		ProcessStatusVO processStatusVO = null;
		Date currentDate = new Date();
		Date process_upto = null;

		ArrayList<VomsBatchExpiryVO> list = null;
		ArrayList<VomsBatchExpiryVO> sequentialList = null;
		ArrayList<VomsBatchExpiryVO> nonSequentialList = null;
		ArrayList<String> successList = null;
		ArrayList<String> failureList = null;
		int updateCount = 0;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			final ProcessBL processBL = new ProcessBL();
			processStatusVO = processBL.checkProcessUnderProcess(con, ProcessI.VOUCHER_EXPIRY_CHANGE_PROCESS);
			log.debug(methodName, "process :Voucher Expiry Change Started");

			if (processStatusVO.isStatusOkBool()) {
				process_upto = processStatusVO.getExecutedUpto();
				if (process_upto != null) {
					vomsVoucherDAO = new VomsVoucherDAO();
					list = vomsVoucherDAO.fetchVomsBatchExpiryDetail(con, process_upto, currentDate);

					if (list.size() > 0) {
						sequentialList = new ArrayList<>();
						nonSequentialList = new ArrayList<>();
						for (int i = 0; i < list.size(); i++) {
							if (BTSLUtil.isNullString(list.get(i).getFilename())) {
								sequentialList.add(list.get(i));
							} else {
								nonSequentialList.add(list.get(i));
							}
						}
						if (sequentialList.size() > 0) {
							for (int i = 0; i < sequentialList.size(); i++) {
								VomsBatchExpiryVO vomsBatchExpiryVO = sequentialList.get(i);
								HashMap<String, ArrayList<String>> map = vomsVoucherDAO.changeVoucherExpiryDate(con,
										vomsBatchExpiryVO);
								vomsBatchExpiryVO.setSuccessCount(map.get("successList").size());
								vomsBatchExpiryVO.setFailCount(map.get("failureList").size());
								vomsBatchExpiryVO.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(currentDate));
								vomsBatchExpiryVO.setModifiedBy(PretupsI.SYSTEM);
								vomsBatchExpiryVO.setExecutionStatus(VOMSI.EXECUTED);
								vomsBatchExpiryVO.setStatus(VOMSI.VOMS_CLOSED_STATUS);
								int count = vomsVoucherDAO.updateVomsBatchesExpiry(con, vomsBatchExpiryVO);
								if (count > 0) {
									con.commit();
									successList = map.get("successList");
									failureList = map.get("failureList");
									String finalPath = createDirectory();
									writeToFile(vomsBatchExpiryVO, successList, failureList, finalPath);
								}
							}
						}
						if (nonSequentialList.size() > 0) {
							for (int i = 0; i < nonSequentialList.size(); i++) {
								VomsBatchExpiryVO vomsBatchExpiryVO = nonSequentialList.get(i);
								ArrayList<VomsVoucherVO> voList = readFromFile(vomsBatchExpiryVO.getFilename());
								HashMap<String, ArrayList<String>> map = vomsVoucherDAO.changeVoucherExpiryDate(con,
										voList, vomsBatchExpiryVO.getBatchNo());
								vomsBatchExpiryVO.setSuccessCount(map.get("successList").size());
								vomsBatchExpiryVO.setFailCount(map.get("failureList").size());
								vomsBatchExpiryVO.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(currentDate));
								vomsBatchExpiryVO.setModifiedBy(PretupsI.SYSTEM);
								vomsBatchExpiryVO.setExecutionStatus(VOMSI.EXECUTED);
								vomsBatchExpiryVO.setStatus(VOMSI.VOMS_CLOSED_STATUS);
								int count = vomsVoucherDAO.updateVomsBatchesExpiry(con, vomsBatchExpiryVO);
								if (count > 0) {
									con.commit();
									successList = map.get("successList");
									failureList = map.get("failureList");
									String finalPath = createDirectory();
									writeToFile(vomsBatchExpiryVO, successList, failureList, finalPath);
								}
							}
						}
					}
				} else {
					throw new BTSLBaseException("VoucherExpiryChangeProcess", "process",
							PretupsErrorCodesI.VOUCHER_EXPIRY_CHANGE_PROCESS);
				}
			} else {
				throw new BTSLBaseException("VoucherExpiryChangeProcess", "process",
						PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
			}
		} catch (BTSLBaseException be) {
			log.errorTrace(methodName, be);
			try {
				mcomCon.finalRollback();
			} catch (Exception e) {
				log.error(methodName, "Exception:e=" + e);
				log.errorTrace(methodName, e);

			}
		} catch (Exception e) {
			log.errorTrace(methodName, e);
		} finally {
			try {
				if (processStatusVO.isStatusOkBool()) {
					processStatusVO.setStartDate(currentDate);
					processStatusVO.setExecutedOn(currentDate);
					processStatusVO.setExecutedUpto(currentDate);
					processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
					updateCount = (new ProcessStatusDAO()).updateProcessDetail(con, processStatusVO);
					if (updateCount > 0) {
						con.commit();
					}
				}
				log.debug(methodName, "Voucher Expiry change process ENDS");
			} catch (Exception ex) {
				if (log.isDebugEnabled()) {
					log.debug("process", "Exception in closing connection ");
				}
				log.errorTrace(methodName, ex);
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e1) {
					log.errorTrace(methodName, e1);
				}
			}
			if (log.isDebugEnabled()) {
				log.debug("process", "Exiting..... ");
			}
		}

	}

	public static String createDirectory() {
		String methodName = "createDirectory";
		if (log.isDebugEnabled())
			log.debug(methodName, "Entered");
		String path = Constants.getProperty("VCHEXPCHGFILES");
		String dirName = Constants.getProperty("VCHEXPCHGDIR");
		String completeFinalDirPath = path + dirName;
		try {
			File file = new File(completeFinalDirPath);
			if (!file.exists()) {
				if (file.mkdir()) {
					log.debug(methodName, "Directory is created!");
				} else {
					log.debug(methodName, "Failed to create directory!");
				}
			} else {
				log.debug(methodName, "Directory already exist !!");
			}
		} catch (Exception e) {
			log.error(methodName, "Exception: " + e.getMessage());
		} finally {
			if (log.isDebugEnabled())
				log.debug(methodName, "Exiting.. finalDirectoryName :: " + completeFinalDirPath);
		}
		return completeFinalDirPath;
	}

	/*
	 * The serial numbers whose expiry date is changed successfully will be displayed in success.txt file
	 * and failure serial numbers will be displayed in fail.txt file. This is for voucher expiry date change request.
	 */
	public static void writeToFile(VomsBatchExpiryVO p_vomsBatchExpiryVO, ArrayList<String> p_successList,
			ArrayList<String> p_failureList, String p_finalPath) {
		String methodName = "writeToFile";
		if (log.isDebugEnabled())
			log.debug(methodName,
					"Entered with p_vomsBatchExpiryVO=" + p_vomsBatchExpiryVO + ",p_successList="
							+ p_successList.toString() + ",p_failureList=" + p_failureList.toString() + ",p_finalPath="
							+ p_finalPath);
		File file = null;
		FileWriter fr = null;
		BufferedWriter writer = null;
		try {
			String successListPath = p_finalPath + "/" + p_vomsBatchExpiryVO.getBatchNo() + "_"
					+ BTSLUtil.getDateStringFromDate(new Date()).replace("/", "") + "_" + "Success.txt";
			String failureListPath = p_finalPath + "/" + p_vomsBatchExpiryVO.getBatchNo() + "_"
					+ BTSLUtil.getDateStringFromDate(new Date()).replace("/", "") + "_" + "Fail.txt";
			log.debug(methodName, "successListPath = " + successListPath);
			log.debug(methodName, "failureListPath = " + failureListPath);
			file = new File(successListPath);
			fr = new FileWriter(file, true);
			writer = new BufferedWriter(fr);
			for (String string : p_successList) {
				writer.write(string + "\n");
			}
			writer.close();

			file = new File(failureListPath);
			fr = new FileWriter(file, true);
			writer = new BufferedWriter(fr);
			for (String string : p_failureList) {
				writer.write(string + "\n");
			}
			writer.close();
		} catch (ParseException e) {
			log.debug(methodName, e.getMessage());
		} catch (IOException e) {
			log.debug(methodName, e.getMessage());
		} catch (Exception e) {
			log.debug(methodName, e.getMessage());
		}
	}

	public static ArrayList<VomsVoucherVO> readFromFile(String p_fileName) {
		String methodName = "readFromFile";
		if (log.isDebugEnabled()) {
			log.debug(methodName, " Entered with p_fileName=" + p_fileName);
		}
		String dir = Constants.getProperty("VOMS_EXPIRY_BULK_FILEPATH");
		String filePath = dir + p_fileName;
		if (log.isDebugEnabled()) {
			log.debug(methodName, " dir = " + dir);
			log.debug(methodName, " filePath = " + filePath);
		}

		String strCurrentLine = null;
		ArrayList<String> dataList = new ArrayList<>();
		ArrayList<VomsVoucherVO> voList = new ArrayList<>();
		VomsVoucherVO vomsVoucherVO = null;
		BufferedReader objReader;
		try {
			objReader = new BufferedReader(new FileReader(filePath));
			int headerCount = Integer.parseInt(Constants.getProperty("VOMS_EXPIRY_FILE_HEADER_ROW_COUNT"));
			for (int i = 0; i < headerCount; i++) {
				objReader.readLine();
			}
			while ((strCurrentLine = objReader.readLine()) != null) {
				dataList.add(strCurrentLine);
			}
			for (int i = 0; i < dataList.size(); i++) {
				String[] arr = dataList.get(i).split(",");
				vomsVoucherVO = new VomsVoucherVO();
				vomsVoucherVO.setSerialNo(arr[0]);
				vomsVoucherVO.setExpiryDate(BTSLUtil.getDateFromDateString(arr[1], "dd-MM-yyyy"));
				voList.add(vomsVoucherVO);
			}
		} catch (FileNotFoundException e) {
			log.debug(methodName, e.getMessage());
		} catch (IOException e) {
			log.debug(methodName, e.getMessage());
		} catch (ParseException e) {
			log.debug(methodName, e.getMessage());
		}
		return voList;
	}
}
