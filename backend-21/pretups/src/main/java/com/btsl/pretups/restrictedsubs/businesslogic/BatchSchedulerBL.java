package com.btsl.pretups.restrictedsubs.businesslogic;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.txn.pretups.restrictedsubs.businesslogic.RestrictedSubscriberTxnDAO;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.web.pretups.restrictedsubs.web.RestrictedSubscriberModel;

public class BatchSchedulerBL {

	private Log log = LogFactory.getLog(this.getClass().getName());
	private String className = "BatchSchedulerBL";

	private static final String NO_OF_RECORDS_EXECED = "restrictedsubs.scheduletopupdetails.msg.noofrecordexeced";

        public static final String[] ERROR_LOG_CONSTANTS = {
			"restrictedsubs.scheduletopupdetails.errorlog.msg.linenumber",
			"restrictedsubs.scheduletopupdetails.errorlog.msg.msisdn",
	"restrictedsubs.scheduletopupdetails.errorlog.msg.failuerreason" };


        public static final String[] DATA_ERROR_KEY = {
			"restrictedsubs.scheduletopupdetails.errorlog.msg.blankrow",
			"restrictedsubs.scheduletopupdetails.errorlog.msg.msisdnnull",
			"restrictedsubs.scheduletopupdetails.errorlog.msg.msisdnduplicate",
	"restrictedsubs.scheduletopupdetails.errorlog.msg.novaliddatafound"};


        public static final String[] UPLOAD_ERROR_KEYS = new String[] {
			"restrictedsubs.scheduletopupdetails.errorfile.msg.invalidmsisdn",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.networkprefixnotfound",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.networknotsupport",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.noinfo",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.subservicenotfound",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.reqamtnotfound",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.subserviceinvalid",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.reqamtinvalid",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.amtnotinrange",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.notassociated",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.alreadyscheduled",
			"restrictedsubs.scheduletopupdetails.msg.novaliddatainfile",
			"restrictedsubs.rescheduletopupdetails.msg.novaliddatainfile",
			"restrictedsubs.scheduletopupdetails.msg.invalidcorpfiletype",
			"restrictedsubs.scheduletopupdetails.msg.unsuccess",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.languagecodeinvalid",
			"restrictedsubs.scheduletopupdetails.msg.invalidfiletype",
			"restrictedsubs.associatesubscriberdetails.msg.novaliddatainfile",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.invalidreceivermsisdn",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.invalidgiftermsisdn",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.networkprefixnotfoundreceiver",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.networkprefixnotfoundgifter",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.networknotsupportreceiver",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.networknotsupportgifter",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.languagecodeinvalidreceiver",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.languagecodeinvalidgifter",
			"restrictedsubs.scheduletopupdetails.msg.invaliGRCfiletype",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.invalidgiftername",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.gifternamenotfound",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.gifterreceivernotsame",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.receivermsisdnnull",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.giftermsisdnnull",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.languagecodenull",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.notificationMsisdnnull",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.notPstnSeries",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.invalidnotificationmsisdn",
			"restrictedsubs.scheduletopupdetails.errorfile.msg.notGsmSeries",
	"restrictedsubs.scheduletopupdetails.errorfile.msg.notInternateSeries" };

        public static final String[] FILE_COLUMN_HEADER_KEYS = new String[] {
			"restrictedsubs.scheduletopupdetails.file.label.msisdn",
			"restrictedsubs.scheduletopupdetails.file.label.subscriberid",
			"restrictedsubs.scheduletopupdetails.file.label.subscribername",
			"restrictedsubs.scheduletopupdetails.file.label.mintxnamt",
			"restrictedsubs.scheduletopupdetails.file.label.maxtxnamt",
			"restrictedsubs.scheduletopupdetails.file.label.monthlimit",
			"restrictedsubs.scheduletopupdetails.file.label.usedlimit",
			"restrictedsubs.scheduletopupdetails.file.label.subservice",
			"restrictedsubs.scheduletopupdetails.file.label.reqamt",
			"restrictedsubs.scheduletopupdetails.file.label.languagecode",
			"restrictedsubs.scheduletopupdetails.file.label.receiverlanguage",
			"restrictedsubs.scheduletopupdetails.file.label.giftermsisdn",
			"restrictedsubs.scheduletopupdetails.file.label.giftername",
			"restrictedsubs.scheduletopupdetails.file.label.gifterlanguage",
	"restrictedsubs.scheduletopupdetails.file.label.notificationMsisdn" };

	public Boolean fileContentValidation(List<String[]> rows,
			PretupsResponse<Object> response, RestrictedSubscriberModel model)
					throws BTSLBaseException {
		String methodName = "fileContentValidation";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try {
			int emptyRows = getEmptryRow(rows);
			if (!this.checkForInvalidOrNoRecords(rows, emptyRows, response,
					model)) {
				return false;
			}

			if (!this.checkForRecordsExeced(rows, emptyRows, response, model)) {
				return false;
			}

			return true;
		} catch (NumberFormatException e) {
			throw new BTSLBaseException(e);
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	public int getEmptryRow(List<String[]> rows) {
		int emptyRows = 0;
		for (String[] strings : rows) {
			if (strings.length == 0) {
				emptyRows++;
			}
		}
		return emptyRows;
	}

	public Boolean checkForInvalidOrNoRecords(List<String[]> rows,
			int emptyRows, PretupsResponse<Object> response,
			RestrictedSubscriberModel model) {
		String methodName = "checkForInvalidOrNoRecords";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try{
			if (rows.size() == emptyRows) {
				response.setMessageCode(PretupsErrorCodesI.NO_RECORDS_FOUND_IN_RESCHEDULE);
				response.setFormError("restrictedsubs.scheduletopupdetails.msg.zerorecords");
				return false;
			}
			if (model.getNoOfRecords() != (rows.size() - emptyRows)) {
				response.setMessageCode(PretupsErrorCodesI.INVALID_NOS_OF_RECORDS_IN_RESCHEDULE);
				response.setFormError("restrictedsubs.scheduletopupdetails.msg.invalidnoofrecord");
				return false;
			}

			return true;
		}finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	public boolean checkForRecordsExeced(List<String[]> rows, int emptyRows,
			PretupsResponse<Object> response, RestrictedSubscriberModel model)
					throws BTSLBaseException {
		String methodName = "checkForRecordsExeced";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try {
			int scheduleNowContentsSize = Integer.parseInt(Constants
					.getProperty("SCHEDULE_NOW_BATCH_RECHARGE_FILE_SIZE")
					.trim());

			if ("on".equalsIgnoreCase(model.getScheduleNow())
					&& ((rows.size() - emptyRows) > scheduleNowContentsSize)) {
				response.setMessageCode(PretupsErrorCodesI.INVALID_NOS_OF_RECORDS_IN_RESCHEDULE);
				response.setFormError(NO_OF_RECORDS_EXECED, new String[]{Integer.toString(scheduleNowContentsSize)});
				return false;
			}

			int batchContentsSize = Integer.parseInt(Constants
					.getProperty("BATCH_MSISDN_LIST_SIZE"));

			if ( batchContentsSize < (rows.size() - emptyRows) ) {
				response.setMessageCode(PretupsErrorCodesI.INVALID_NOS_OF_RECORDS_IN_RESCHEDULE);
				response.setFormError(NO_OF_RECORDS_EXECED,  new String[]{Integer.toString(batchContentsSize)});
				return false;
			} 

			return true;
		} catch (NumberFormatException e) {
			throw new BTSLBaseException(e);
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	public String[] getErrorKey(String fileType) {
		if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(fileType)) {
			return new String[] { UPLOAD_ERROR_KEYS[0], UPLOAD_ERROR_KEYS[1],
					UPLOAD_ERROR_KEYS[2], UPLOAD_ERROR_KEYS[3],
					UPLOAD_ERROR_KEYS[4], UPLOAD_ERROR_KEYS[5],
					UPLOAD_ERROR_KEYS[6], UPLOAD_ERROR_KEYS[7],
					UPLOAD_ERROR_KEYS[8], UPLOAD_ERROR_KEYS[9],
					UPLOAD_ERROR_KEYS[10], UPLOAD_ERROR_KEYS[11],
					UPLOAD_ERROR_KEYS[12], UPLOAD_ERROR_KEYS[13],
					UPLOAD_ERROR_KEYS[14], UPLOAD_ERROR_KEYS[30],
					UPLOAD_ERROR_KEYS[36] };
		} else {
			return new String[] { UPLOAD_ERROR_KEYS[0], UPLOAD_ERROR_KEYS[1],
					UPLOAD_ERROR_KEYS[2], UPLOAD_ERROR_KEYS[4],
					UPLOAD_ERROR_KEYS[5], UPLOAD_ERROR_KEYS[6],
					UPLOAD_ERROR_KEYS[7], UPLOAD_ERROR_KEYS[14],
					UPLOAD_ERROR_KEYS[15], UPLOAD_ERROR_KEYS[16],
					UPLOAD_ERROR_KEYS[17], UPLOAD_ERROR_KEYS[32],
					UPLOAD_ERROR_KEYS[30], UPLOAD_ERROR_KEYS[36] };
		}
	}


	/**
	 * This file write error on the csv file.
	 * @param dataErrorList
	 */
	private String writeErrorLogsOnCSV(List<String[]> dataErrorList) throws BTSLBaseException{
		String methodName = "writeErrorLogsOnCSV";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try{
			Map<String, Object> dataMap = new HashMap<>();

			String[] columnsKey = dataErrorList.get(0);
			String[] columns = new String[3];
			for (int i = 0; i < columnsKey.length; i++) {
				columns[i] = PretupsRestUtil.getMessageString(columnsKey[i]);
			}

			dataMap.put(PretupsI.COLUMN_HEADER_KEY, columns);

			dataErrorList.remove(0);

			List<String> errorDetails = new ArrayList<>();
			for(String[] errors : dataErrorList){
				errorDetails.add(StringUtils.join(errors, ","));
			}
			dataMap.put(PretupsI.DATA, errorDetails);
			File fileObj = PretupsRestUtil.getFileForTemplate("SCHEDULE_ERROR_LOG_FILE_NAME", "SCHEDULE_TOPUP_ERROR_LOG_FILE_LOCATION");
			PretupsRestUtil.checkForLocation(fileObj.getParent());
			PretupsRestUtil.writeCSVFile(dataMap, fileObj);
			return fileObj.getAbsolutePath();
		}catch(BTSLBaseException e){
			throw e;
		}finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	/**
	 * @param dataErrorList
	 * @param fileRefNo
	 * @return
	 * @throws BTSLBaseException
	 */
	private String writeErrorLogsOnCSV(List<String[]> dataErrorList, String fileRefNo, String fileName, String fileLocation) throws BTSLBaseException{
		String methodName = "writeErrorLogsOnCSV";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try{
			Map<String, Object> dataMap = new HashMap<>();

			String[] columnsKey = dataErrorList.get(0);
			String[] columns = new String[3];
			for (int i = 0; i < columnsKey.length; i++) {
				columns[i] = PretupsRestUtil.getMessageString(columnsKey[i]);
			}

			dataMap.put(PretupsI.COLUMN_HEADER_KEY, columns);

			dataErrorList.remove(0);

			List<String> errorDetails = new ArrayList<>();
			for(String[] errors : dataErrorList){
				errorDetails.add(StringUtils.join(errors, ","));
			}
			dataMap.put(PretupsI.DATA, errorDetails);
			File fileObj = PretupsRestUtil.getFileForTemplate(fileName, fileLocation, fileRefNo);
			PretupsRestUtil.checkForLocation(fileObj.getParent());
			PretupsRestUtil.writeCSVFile(dataMap, fileObj);
			return fileObj.getAbsolutePath();
		}catch(BTSLBaseException e){
			throw e;
		}finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}


	/**
	 * Check if subscriber status exists
	 * @param connection
	 * @param userVO
	 * @param restrictedSubscriberDAO
	 * @param validDataList
	 * @return
	 * @throws BTSLBaseException
	 */
	public String checkIfSubscriberExistByStatus(Connection connection, UserVO userVO, RestrictedSubscriberDAO restrictedSubscriberDAO, List<ScheduleBatchDetailVO> validDataList) throws BTSLBaseException{
		String methodName = "checkIfSubscriberExistByStatus";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try{
			return restrictedSubscriberDAO
					.isSubscriberExistByStatus(connection, userVO.getOwnerID(),
							validDataList, PretupsI.STATUS_EQUAL,
							PretupsI.RES_MSISDN_STATUS_ASSOCIATED, null);
		}catch(BTSLBaseException e){
			throw e;
		}finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}

	}

	/**
	 * process status data of subscriber
	 * @param statusData
	 * @param validDataList
	 * @param errorList
	 * @param errorMessages
	 * @return List<ScheduleBatchDetailVO>
	 */
	public List<ScheduleBatchDetailVO> processStatusData(String statusData, List<ScheduleBatchDetailVO> validDataList, String[] errorList, Map<String, String> errorMessages){
		String methodName = "processStatusData";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try{
			String[] requestDataArray;
			List<ScheduleBatchDetailVO> newValidDataList = new ArrayList<>();
			if (BTSLUtil.isNullString(statusData)) {
				//LogFactory.printLog(methodName, "statusData is null", log);
				return newValidDataList;
			}

			newValidDataList = new ArrayList<>();
			requestDataArray = statusData.split(",");

			for (int i = 0; i < requestDataArray.length; i++) {
				String filteredMsisdn = requestDataArray[i];
				for(ScheduleBatchDetailVO scheduleDetailVO : validDataList){
					if(scheduleDetailVO.getMsisdn().equalsIgnoreCase(filteredMsisdn)){
						newValidDataList.add(scheduleDetailVO);
					}else{
						errorMessages.put(scheduleDetailVO.getMsisdn(),	errorList[9]);
					}
				}
			}
		}finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
		return validDataList;
	}

	/**
	 * Get schedule by status
	 * @param connection
	 * @param validDataList
	 * @param userVO
	 * @return
	 * @throws BTSLBaseException
	 */
	public String checkScheduleExistByStatus(Connection connection, List<ScheduleBatchDetailVO> validDataList, UserVO userVO) throws BTSLBaseException{
		String methodName = "checkScheduleExistByStatus";
		LogFactory.printLog(methodName, PretupsI.EXITED, log);
		try{
			String statusStr = "'" + PretupsI.SCHEDULE_STATUS_CANCELED + "','"
					+ PretupsI.SCHEDULE_STATUS_EXECUTED + "'";
			String statusMode = PretupsI.STATUS_NOTIN;

			ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
			return scheduledBatchDetailDAO.isScheduleExistByStatus(
					connection, validDataList, statusMode, statusStr,
					userVO.getOwnerID(), new Date());
		}catch(BTSLBaseException e){
			throw e;
		}finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	/**
	 * Process schedule status
	 * @param scheduleStatus
	 * @param errorList
	 * @param validDataList
	 * @param errorMessages
	 * @return
	 */
	public  List<ScheduleBatchDetailVO> processScheduleStatus(String scheduleStatus, String[] errorList, List<ScheduleBatchDetailVO> validDataList,  Map<String, String> errorMessages){
		String methodName = "processScheduleStatus";
		LogFactory.printLog(methodName, PretupsI.EXITED, log);
		List<ScheduleBatchDetailVO> tempList = new ArrayList<>();
		try{
			if (!BTSLUtil.isNullString(scheduleStatus)) {
				String[] dataArray = scheduleStatus.split(",");

				for (int i = 0; i < dataArray.length; i++) {
					String filteredMsisdn = dataArray[i];
					for (ScheduleBatchDetailVO scheduleBatchDetailVO : validDataList) {
						if (scheduleBatchDetailVO.getMsisdn().equals(filteredMsisdn)) {
							tempList.remove(scheduleBatchDetailVO);
							errorMessages.put(filteredMsisdn, errorList[10]);
						}
					}
				}
				validDataList.removeAll(tempList);
			}
		}finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
		return validDataList;
	}

	/**
	 * Save data in schedule recharge details
	 * @param connection
	 * @param validDataList
	 * @param scheduleBatchMasterVO
	 * @param totalRows
	 * @return
	 * @throws BTSLBaseException
	 */
	public Boolean saveInScheduleRechargeDetails(Connection connection, List<ScheduleBatchDetailVO> validDataList, ScheduleBatchMasterVO scheduleBatchMasterVO, int totalRows) throws BTSLBaseException{
		String methodName = "saveInScheduleRechargeDetails";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try{
			ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
			int addCount =  scheduledBatchDetailDAO.addScheduleDetails(connection, validDataList, true); 
			if(addCount == validDataList.size()){
				scheduleBatchMasterVO.setTotalCount(totalRows);
				scheduleBatchMasterVO.setUploadFailedCount(totalRows - addCount);
				scheduleBatchMasterVO.setCancelledCount(0);
				scheduleBatchMasterVO.setProcessFailedCount(0);
				scheduleBatchMasterVO.setSuccessfulCount(0);
				return true;
			}else{
				return false;
			}
		}catch(BTSLBaseException e){
			throw e;
		}finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	/**
	 * Save in Schedule Master Details
	 * @param connection
	 * @param scheduleBatchMasterVO
	 * @return
	 * @throws BTSLBaseException
	 */
	public boolean updateInScheduleMasterDetail(Connection connection, ScheduleBatchMasterVO scheduleBatchMasterVO) throws BTSLBaseException{
		String methodName = "saveInScheduleMasterDetail";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try{
			ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
			int addCount = scheduledBatchDetailDAO.updateScheduleBatchMaster(connection, scheduleBatchMasterVO);
			if(addCount > 0){
				return true;
			}

			return false;
		}catch(BTSLBaseException e){
			throw e;
		}finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	/**
	 * get columns name on the basis of file type
	 * @param fileType
	 * @return String[] 
	 */
	public String[] getColumnKeys(String fileType) {

		String methodName = "getColumnKeys";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try{
			if (PretupsI.BATCH_TYPE_CORPORATE.equals(fileType)) {
				return new String[] { FILE_COLUMN_HEADER_KEYS[0],
						FILE_COLUMN_HEADER_KEYS[1], FILE_COLUMN_HEADER_KEYS[2],
						FILE_COLUMN_HEADER_KEYS[3], FILE_COLUMN_HEADER_KEYS[4],
						FILE_COLUMN_HEADER_KEYS[5], FILE_COLUMN_HEADER_KEYS[6],
						FILE_COLUMN_HEADER_KEYS[7], FILE_COLUMN_HEADER_KEYS[8] };

			} else if (PretupsI.BATCH_TYPE_NORMAL.equalsIgnoreCase(fileType)
					|| PretupsI.BATCH_TYPE_BOTH.equalsIgnoreCase(fileType)) {
				return new String[] { FILE_COLUMN_HEADER_KEYS[0],
						FILE_COLUMN_HEADER_KEYS[7], FILE_COLUMN_HEADER_KEYS[8],
						FILE_COLUMN_HEADER_KEYS[9] };
			}


			return new String[] { FILE_COLUMN_HEADER_KEYS[0],
					FILE_COLUMN_HEADER_KEYS[7], FILE_COLUMN_HEADER_KEYS[8],
					FILE_COLUMN_HEADER_KEYS[9] };
		}finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	/**
	 * @param connection
	 * @param response
	 * @param model
	 * @param userVO
	 * @param seachedUserVO
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public PretupsResponse<Object> processBatchRecharge(Connection connection, PretupsResponse<Object> response, RestrictedSubscriberModel model, UserVO userVO,UserVO seachedUserVO) throws BTSLBaseException, SQLException
	{
		String methodName ="processBatchRecharge";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		
		RowListProcessor processor = null;
		try{
		 processor = PretupsRestUtil.readCsvFile(model.getUploadFilePath(), 2);
		}
		catch(BTSLBaseException ex){
			OracleUtil.rollbackConnection(connection, className, methodName);
			LogFactory.printLog(methodName, ex.getMessage(), log);
			response.setMessageCode(PretupsErrorCodesI.BATCH_FILE_NOT_AVAILABLE_IN_RESCHEDULE);
			response.setFormError("reschedule.recharge.batchfile.not.available");
			response.setParameters(new String[]{model.getUploadFilePath()});
			return response;
		}

		try{
			RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
			RestrictedSubscriberTxnDAO restrictedSubscriberTxnDAO = new RestrictedSubscriberTxnDAO();
			
			List<String[]> rows = processor.getRows();

			if (!fileContentValidation(rows, response, model)) {
				OracleUtil.rollbackConnection(connection, className, methodName);
				return response;
			}

			String[] errorList = getErrorKey(model.getFileType());
			ScheduleBatchMasterVO scheduleMasterVO = model.getScheduleBatchMasterVO();
			Map<String, Object> downLoadDataMap = null;

			if (PretupsI.SCHEDULE.equalsIgnoreCase(model.getRequestFor())) {
				downLoadDataMap = restrictedSubscriberTxnDAO
						.loadRestrictedSubscriberList(connection,
								userVO.getUserID(), userVO.getOwnerID());
			}

			if (PretupsI.RESCHEDULE.equalsIgnoreCase(model.getRequestFor())) {
				downLoadDataMap = restrictedSubscriberDAO
						.loadScheduleBatchDetailsList(connection, model.getBatchID(), PretupsI.STATUS_EQUAL, PretupsI.SCHEDULE_STATUS_CANCELED);
			}

			Map<String, String> errorMessages = new HashMap<>();
			String definedSubServiceValue = Constants
					.getProperty("DEFINED_SUB_SERVICE_VALUE");
			List<ScheduleBatchDetailVO> validDataList = new ArrayList<>();

			int rowNumber = 2;
			List<String[]> dataErrorList = new ArrayList<>();
			String[] errorColumnHeader = ERROR_LOG_CONSTANTS;
			dataErrorList.add(errorColumnHeader);
			List<String> duplicateMsisdn = new ArrayList<>();
			for (String[] row : rows) {
				++rowNumber;
				if (PretupsRestUtil.checkIfEmpty(row)) {
					String[] errorReason = { Integer.toString(rowNumber),	row[0], PretupsRestUtil.getMessageString(DATA_ERROR_KEY[0]) };
					dataErrorList.add(errorReason);
					continue;
				}

				if (BTSLUtil.isNullString(row[0])) {
					String[] errorReason = { Integer.toString(rowNumber),
							row[0], PretupsRestUtil.getMessageString(DATA_ERROR_KEY[1]) };
					dataErrorList.add(errorReason);
					continue;
				}

				int duplicateMsisdnCount = 0;
				boolean duplicateFlag = false;
				for (String[] array1 : rows) {

					if(duplicateMsisdn.contains(row[0])){
						String[] errorReason = { Integer.toString(rowNumber),
								row[0], PretupsRestUtil.getMessageString(DATA_ERROR_KEY[2]) };
						dataErrorList.add(errorReason);
						duplicateMsisdnCount = 0;
						duplicateMsisdn.add(row[0]);
						duplicateFlag = true;
						break;
					}
					if (row[0].equalsIgnoreCase(array1[0])) {
						duplicateMsisdnCount++;
					}

					if (duplicateMsisdnCount > 1) {
						String[] errorReason = { Integer.toString(rowNumber),
								row[0], PretupsRestUtil.getMessageString(DATA_ERROR_KEY[2] )};
						dataErrorList.add(errorReason);
						duplicateMsisdnCount = 0;
						duplicateMsisdn.add(row[0]);
						duplicateFlag = true;
						break;
					}
				}

				if(duplicateFlag){
					continue;
				}

				String filteredMsisdn = null;

				try {
					filteredMsisdn = PretupsBL
							.getFilteredIdentificationNumber(row[0]);
				} catch (BTSLBaseException e) {
					String[] errorReason = { Integer.toString(rowNumber),
							row[0], PretupsRestUtil.getMessageString(errorList[0])};
					dataErrorList.add(errorReason);
					continue;
				}

				if (!BTSLUtil.isValidIdentificationNumber(filteredMsisdn)) {
					String[] errorReason = { Integer.toString(rowNumber),
							row[0], PretupsRestUtil.getMessageString(errorList[0]) };
					dataErrorList.add(errorReason);
					continue;
				}

				String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);

				NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache
						.getObject(msisdnPrefix);

				if (networkPrefixVO == null) {
					String[] errorReason = { Integer.toString(rowNumber),
							row[0],PretupsRestUtil.getMessageString( errorList[1] )};
					dataErrorList.add(errorReason);
					continue;
				}

				String networkCode = networkPrefixVO.getNetworkCode();

				if (!networkCode.equals(userVO.getNetworkID())) {
					String[] errorReason = { Integer.toString(rowNumber),
							row[0], PretupsRestUtil.getMessageString(errorList[2])};
					dataErrorList.add(errorReason);
					continue;
				}

				long prefixId = networkPrefixVO.getPrefixID();
				String type = networkPrefixVO.getSeriesType();
                                String subService = row[7];
				if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(model.getFileType()))
                    subService = row[7];
                   else
                    subService = row[1];
				if (BTSLUtil.isNullString(subService)) {
					String[] errorReason = { Integer.toString(rowNumber),
							row[0],PretupsRestUtil.getMessageString( errorList[6], new String[]{subService}) };
					dataErrorList.add(errorReason);
					continue;
				}
				try {
					Integer.parseInt(subService);

				} catch (NumberFormatException e) {
					String[] errorReason = { Integer.toString(rowNumber),
							row[0], PretupsRestUtil.getMessageString(errorList[6], new String[]{subService}) };
					dataErrorList.add(errorReason);
					continue;
				}

				if (definedSubServiceValue.indexOf(subService) == -1) {
					String[] errorReason = { Integer.toString(rowNumber),
							row[0],PretupsRestUtil.getMessageString( errorList[6], new String[]{subService}) };
					dataErrorList.add(errorReason);
					continue;
				}
                                String amount = row[8];
				 if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(model.getFileType()))
                     amount = row[8];
                     else
                     amount = row[2];
				if (BTSLUtil.isNullString(amount)) {
					String[] errorReason = { Integer.toString(rowNumber),
							row[0], PretupsRestUtil.getMessageString(errorList[7], new String[]{amount}) };
					dataErrorList.add(errorReason);
					continue;
				}
				long systemAmount = 0;
				try {
					systemAmount = PretupsBL.getSystemAmount(amount);
				} catch (BTSLBaseException e) {
					String[] errorReason = { Integer.toString(rowNumber),
							row[0], PretupsRestUtil.getMessageString(errorList[7], new String[]{amount}) };
					dataErrorList.add(errorReason);
					log.debug(methodName, e.getMessage());
					continue;
				}
				 String language =null;
                 String country =null;
                 RestrictedSubscriberVO restrictedSubscriberVO =null;
                 if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(model.getFileType())){

				 restrictedSubscriberVO = (RestrictedSubscriberVO) downLoadDataMap
						.get(filteredMsisdn);

				if (restrictedSubscriberVO == null) {
					String[] errorReason = { Integer.toString(rowNumber),
							row[0], PretupsRestUtil.getMessageString( errorList[3] )};
					dataErrorList.add(errorReason);
					continue;
				}

				 language = restrictedSubscriberVO.getLanguage();
				 country = restrictedSubscriberVO.getCountry();

				if (systemAmount < restrictedSubscriberVO.getMinTxnAmount()
						|| systemAmount > restrictedSubscriberVO
						.getMaxTxnAmount()) {
					String[] errorReason = { Integer.toString(rowNumber),
							row[0], PretupsRestUtil.getMessageString(errorList[8],  new String[]{amount}) };
					dataErrorList.add(errorReason);
					continue;
				}
                 }

				ScheduleBatchDetailVO scheduleDetailVO = new ScheduleBatchDetailVO();
				scheduleDetailVO.setBatchID(scheduleMasterVO.getBatchID());
				scheduleDetailVO.setAmount(systemAmount);
				scheduleDetailVO.setSubService(subService);
				scheduleDetailVO.setMsisdn(filteredMsisdn);
				if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(model.getFileType()))
				scheduleDetailVO.setSubscriberID(restrictedSubscriberVO
						.getSubscriberID());
                    else
                    scheduleDetailVO.setSubscriberID(row[0]);
				scheduleDetailVO.setStatus(PretupsI.SCHEDULE_STATUS_SCHEDULED);
				scheduleDetailVO.setModifiedBy(seachedUserVO.getUserID());
				scheduleDetailVO.setModifiedOn(new Date());
				scheduleDetailVO.setCreatedBy(seachedUserVO.getUserID());
				scheduleDetailVO.setCreatedOn(new Date());
				 if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(model.getFileType())){
				scheduleDetailVO.setLanguage(language);
				scheduleDetailVO.setCountry(country);
             }
             else
             {
                     scheduleDetailVO.setLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)));
                     scheduleDetailVO.setCountry((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
             }

				validDataList.add(scheduleDetailVO);
	
			}

			String fileName = "RESCHEDULE_ERROR_LOG_FILE_NAME";
			String fileLocation = "SCHEDULE_TOPUP_ERROR_LOG_FILE_LOCATION";
			String fileRefNo = model.getBatchID();

			if (validDataList.isEmpty()) {
				OracleUtil.rollbackConnection(connection, className, methodName);
				response.setMessageCode(PretupsErrorCodesI.NO_VALID_DATA_FOUND_IN_RESCHEDULE);
				response.setFormError(DATA_ERROR_KEY[3]);
				response.setDataObject(this.writeErrorLogsOnCSV(dataErrorList, fileRefNo, fileName, fileLocation));
				return response;
			}

			String statusData = checkIfSubscriberExistByStatus(connection, seachedUserVO, restrictedSubscriberDAO, validDataList);
			List<ScheduleBatchDetailVO> validdataList = processStatusData(statusData, validDataList, errorList, errorMessages);

			if (validDataList.isEmpty()) {
				OracleUtil.rollbackConnection(connection, className, methodName);
				response.setMessageCode(PretupsErrorCodesI.NO_VALID_DATA_FOUND_IN_RESCHEDULE);
				response.setFormError(DATA_ERROR_KEY[3]);
				response.setDataObject(this.writeErrorLogsOnCSV(dataErrorList, fileRefNo, fileName, fileLocation));
				return response;
			}

			String scheduleStatus = this.checkScheduleExistByStatus(connection, validdataList, seachedUserVO);
			validdataList = processScheduleStatus(scheduleStatus, errorList, validdataList, errorMessages);

			if (validDataList.isEmpty()) {
				OracleUtil.rollbackConnection(connection, className, methodName);
				response.setMessageCode(PretupsErrorCodesI.NO_VALID_DATA_FOUND_IN_RESCHEDULE);
				response.setFormError(DATA_ERROR_KEY[3]);
				response.setDataObject(this.writeErrorLogsOnCSV(dataErrorList, fileRefNo, fileName, fileLocation));
				return response;
			}


			if(saveInScheduleRechargeDetails(connection, validdataList, scheduleMasterVO, rows.size())){
				if(updateInScheduleMasterDetail(connection, scheduleMasterVO)){
					connection.commit();
				}else{
					OracleUtil.rollbackConnection(connection, className, methodName);
				}

			}else{
				OracleUtil.rollbackConnection(connection, className, methodName);
			}

			//if schedule now
			if ("on".equalsIgnoreCase(model.getScheduleNow())){
				ScheduleTopUpNowBL upNowBL= new ScheduleTopUpNowBL(); 
				upNowBL.invokeProcessByStatus(connection,PretupsI.SCHEDULE_NOW_TYPE, scheduleMasterVO, null);
			}

			
			if(dataErrorList.size() > 1){
				int processedRecords = validDataList.size();
				int totalRecords = rows.size();
				String message = "on".equalsIgnoreCase(model.getScheduleNow()) ? "restrictedsubs.scheduletopupdetails.msg.schedulenowsuccess" : "restrictedsubs.scheduletopuprecharge.file.x.out.of.y.records.processed.successfully";
				response.setDataObject(PretupsI.RESPONSE_FAIL, false, this.writeErrorLogsOnCSV(dataErrorList, fileRefNo, fileName, fileLocation));
				response.setMessageCode(PretupsErrorCodesI.RESCHEDULE_PROCCESSED_WITH_ERRORS);
				response.setFormError(message,new String[]{Integer.toString(processedRecords), Integer.toString(totalRecords), scheduleMasterVO.getBatchID()});
				return response;
			}
			else{
				String message = "on".equalsIgnoreCase(model.getScheduleNow()) ? "restrictedsubs.scheduletopupdetails.msg.success" : "restrictedsubs.scheduletopuprecharge.file.has.been.processed.successfully";
				response.setMessageCode(PretupsErrorCodesI.RESCHEDULE_PROCCESSED_SUCCESSFULLY);
				response.setResponse(PretupsI.RESPONSE_SUCCESS, true, message);
				response.setParameters(new String[]{ scheduleMasterVO.getBatchID()});
				return response;
			}
		}
		catch(BTSLBaseException | SQLException e){
			OracleUtil.rollbackConnection(connection, className, methodName);
			throw new BTSLBaseException(e);
		}finally{
			OracleUtil.closeQuietly(connection);
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}

	}


}
