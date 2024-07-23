package com.btsl.pretups.restrictedsubs.businesslogic;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.ScheduleFileProcessLog;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.txn.pretups.restrictedsubs.businesslogic.RestrictedSubscriberTxnDAO;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.web.pretups.restrictedsubs.web.RestrictedSubscriberModel;

/**
 * Implement ScheduleRechargeRestService interface and provides basic funtionality 
 * for processign batch recharge
 * @author lalit.chattar *
 */
public class BatchScheduleRechargeRestServiceImpl implements BatchScheduleRechargeRestService {

	private static final Log log = LogFactory.getLog(BatchScheduleRechargeRestServiceImpl.class.getName());
	private static final String NO_OF_RECORDS_EXCEEDS = "restrictedsubs.scheduletopupdetails.msg.noofrecordexceeds";
	private static final String UPLOADED_FILE = "UPLOADED FILE =";
	private static final String TYPE = ", TYPE=";
	private static final String VALIDATING_DATA = "Validating Data";
	private String className = "BatchScheduleRechargeRestServiceImpl"; 
	/**
	 * provide implementation for downloading batch recharges template
	 * @param String requestParam
	 * @return PretupsResponse
	 */
	@Override
	public PretupsResponse<String> downloadScheduleRechargeTemplate(String requestData) throws BTSLBaseException {
		String methodName = "downloadScheduleRechargeTemplate";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		PretupsResponse<String> response = new PretupsResponse<>();
		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
			RestrictedSubscriberModel model = (RestrictedSubscriberModel) PretupsRestUtil.convertJSONToObject(requestNode.get(PretupsI.DATA).toString(), new TypeReference<RestrictedSubscriberModel>() {});
			ScheduleTopupValidator scheduleTopupValidator = new ScheduleTopupValidator();
			scheduleTopupValidator.validateRequestData(requestNode.get("type").textValue(), response, model, "BatchScheduleFileTemplate");
			if(response.hasFieldError()){
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			File file = PretupsRestUtil.getFileForTemplate(
					"DownloadRestrictedMSISDNFileName",
					"DownloadRestrictedMSISDNFilePath");
			PretupsRestUtil.checkForLocation(file.getParent());
			String[] columnsKeys = getColumnKeys(model.getFileType());
			String[] columns = new String[columnsKeys.length];
			for (int i = 0; i < columnsKeys.length; i++) {
				columns[i] = PretupsRestUtil.getMessageString(columnsKeys[i]);
			}
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			UserVO userVO = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(
					requestNode.get(PretupsI.DATA), con);

			Map<String, Object> dataMap = new HashMap<>();
			dataMap.put(PretupsI.COLUMN_HEADER_KEY, columns);
			dataMap.put(PretupsI.OWNER_ID, userVO.getOwnerID());
			dataMap.put(PretupsI.USER_ID, userVO.getUserID());
			dataMap.put(PretupsI.SERVICE_TYPE, model.getServiceCode());
			downloadFile(con, model.getFileType(), dataMap, file);
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,	file.getAbsolutePath());
		} catch (IOException | SQLException e) {
			throw new BTSLBaseException(e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("BatchScheduleRechargeRestServiceImpl#downloadScheduleRechargeTemplate");
				mcomCon = null;
			}
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
		return response;
	}

	/**
	 * provide implementation for processing batch recharge file
	 * @param String requestParam
	 * @return PretupsResponse
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws  
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<Object> processUplodedScheduleRechargeFile(String requestData) throws BTSLBaseException{
		String methodName = "processUplodedScheduleRechargeFile";
		Connection con = null;
		MComConnectionI mcomCon = null;
		try{
			JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
			RestrictedSubscriberModel model = (RestrictedSubscriberModel) PretupsRestUtil.convertJSONToObject(requestNode.get(PretupsI.DATA).toString(), new TypeReference<RestrictedSubscriberModel>() {});
			PretupsResponse<Object> response = new PretupsResponse<>();
			ScheduleTopupValidator scheduleTopupValidator = new ScheduleTopupValidator();
			scheduleTopupValidator.validateRequestData(requestNode.get("type").textValue(), response, model, "BatchScheduleRechargeFileProcess");
			if(response.hasFieldError()){
				LogFactory.printLog(methodName, "Response has some field Error", log);
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			UserVO userVO = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(requestNode.get(PretupsI.DATA), con);
			UserVO userVOSession = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(requestNode, con);
			scheduleTopupValidator.validateUserDetails(response, userVO, model);
			if(response.hasFieldError()){
				LogFactory.printLog(methodName, "Invalid User Detail found", log);
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			ScheduleBatchMasterVO scheduleMasterVO = new ScheduleBatchMasterVO();
			if (this.saveInScheduleMaster(con, scheduleMasterVO,
					userVOSession, userVOSession, model) <= 0) {
				LogFactory.printLog(methodName, "Could not save data in Master Details", log);
				OracleUtil.rollbackConnection(con, className, methodName);
				response.setFormError("restrictedsubs.scheduletopupdetails.msg.unsuccess");
				return response;
			}
			RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
			RestrictedSubscriberTxnDAO restrictedSubscriberTxnDAO = new RestrictedSubscriberTxnDAO();
			RowListProcessor processor = PretupsRestUtil.readCsvFile(model.getUploadedFileLocation(), 2);
			List<String[]> rows = processor.getRows();
			if (!fileContentValidation(rows, response, model)) {
				LogFactory.printLog(methodName, "File content validation fail", log);
				OracleUtil.rollbackConnection(con, className, methodName);
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			String[] errorList = getErrorKey(model.getFileType());
			Map<String, Object> downLoadDataMap = null;

			if (PretupsI.SCHEDULE.equalsIgnoreCase(model.getRequestFor())) {
				downLoadDataMap = restrictedSubscriberTxnDAO
						.loadRestrictedSubscriberList(con,
								userVO.getUserID(), userVO.getOwnerID());
			}

			if (PretupsI.RESCHEDULE.equalsIgnoreCase(model.getRequestFor())) {
				downLoadDataMap = restrictedSubscriberDAO.loadScheduleBatchDetailsList(con, scheduleMasterVO.getBatchID(), PretupsI.STATUS_EQUAL, PretupsI.SCHEDULE_STATUS_CANCELED);
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
				if (PretupsRestUtil.checkIfEmpty(row)) {
					ScheduleFileProcessLog.log(VALIDATING_DATA, scheduleMasterVO.getCreatedBy(), "", scheduleMasterVO.getBatchID(), "Record is blank", "FAIL", UPLOADED_FILE + model.getUploadedFileLocation() + TYPE + model.getRequestFor());
					String[] errorReason = { Integer.toString(++rowNumber),	"", PretupsRestUtil.getMessageString(DATA_ERROR_KEY[0]) };
					dataErrorList.add(errorReason);
					continue;
				}
				if (BTSLUtil.isNullString(row[0])) {
					ScheduleFileProcessLog.log(VALIDATING_DATA, scheduleMasterVO.getCreatedBy(), row[0], scheduleMasterVO.getBatchID(), "Msisdn not provided", "FAIL", UPLOADED_FILE + model.getUploadedFileLocation() + TYPE + model.getRequestFor());
					String[] errorReason = { Integer.toString(++rowNumber),
							row[0], PretupsRestUtil.getMessageString(DATA_ERROR_KEY[1]) };
					dataErrorList.add(errorReason);
					continue;
				}
				int duplicateMsisdnCount = 0;
				boolean duplicateFlag = false;
				for (String[] array1 : rows) {					
					if(duplicateMsisdn.contains(row[0])){
						ScheduleFileProcessLog.log(VALIDATING_DATA, scheduleMasterVO.getCreatedBy(), row[0], scheduleMasterVO.getBatchID(), "Duplicate MSISDN found", "FAIL", UPLOADED_FILE + model.getUploadedFileLocation() + TYPE + model.getRequestFor());
						String[] errorReason = { Integer.toString(++rowNumber),
								row[0], PretupsRestUtil.getMessageString(DATA_ERROR_KEY[2]) };
						dataErrorList.add(errorReason);
						duplicateMsisdnCount = 0;
						duplicateMsisdn.add(row[0]);
						duplicateFlag = true;
						break;
					}					
					
					if (array1.length != 0 && row[0].equalsIgnoreCase(array1[0])) {
						duplicateMsisdnCount++;
					}

					if (duplicateMsisdnCount > 1) {
						ScheduleFileProcessLog.log(VALIDATING_DATA, scheduleMasterVO.getCreatedBy(), row[0], scheduleMasterVO.getBatchID(), "Duplicate MSISDN found", "FAIL", UPLOADED_FILE + model.getUploadedFileLocation() + TYPE + model.getRequestFor());
						String[] errorReason = { Integer.toString(++rowNumber),
								row[0], PretupsRestUtil.getMessageString(DATA_ERROR_KEY[2]) };
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
					 log.errorTrace(methodName, e);
					ScheduleFileProcessLog.log(VALIDATING_DATA, scheduleMasterVO.getCreatedBy(), row[0], scheduleMasterVO.getBatchID(), "Invalid MSISDN", "FAIL", UPLOADED_FILE + model.getUploadedFileLocation() + TYPE + model.getRequestFor());
					String[] errorReason = { Integer.toString(++rowNumber),
							row[0], PretupsRestUtil.getMessageString(errorList[0]) };
					dataErrorList.add(errorReason);
					continue;
				}
				if (!BTSLUtil.isValidIdentificationNumber(filteredMsisdn)) {
					ScheduleFileProcessLog.log(VALIDATING_DATA, scheduleMasterVO.getCreatedBy(), row[0], scheduleMasterVO.getBatchID(), "Invalid Identification Number", "FAIL", UPLOADED_FILE + model.getUploadedFileLocation() + TYPE + model.getRequestFor());
					String[] errorReason = { Integer.toString(++rowNumber),
							row[0], PretupsRestUtil.getMessageString(errorList[0]) };
					dataErrorList.add(errorReason);
					continue;
				}

				String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);

				NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache
						.getObject(msisdnPrefix);

				if (networkPrefixVO == null) {
					ScheduleFileProcessLog.log(VALIDATING_DATA, scheduleMasterVO.getCreatedBy(), row[0], scheduleMasterVO.getBatchID(), "Network prefix not found", "FAIL", UPLOADED_FILE + model.getUploadedFileLocation() + TYPE + model.getRequestFor());
					String[] errorReason = { Integer.toString(++rowNumber),
							row[0], PretupsRestUtil.getMessageString(errorList[1]) };
					dataErrorList.add(errorReason);
					continue;
				}
				String networkCode = networkPrefixVO.getNetworkCode();

				if (!networkCode.equals(userVO.getNetworkID())) {
					ScheduleFileProcessLog.log(VALIDATING_DATA, scheduleMasterVO.getCreatedBy(), row[0], scheduleMasterVO.getBatchID(), "Network id not found", "FAIL", UPLOADED_FILE + model.getUploadedFileLocation() + TYPE + model.getRequestFor());
					String[] errorReason = { Integer.toString(++rowNumber),
							row[0], PretupsRestUtil.getMessageString(errorList[2]) };
					dataErrorList.add(errorReason);
					continue;
				}
				String subService =null;
				if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(model.getFileType()))
				 subService = row[7];
				else
				 subService = row[1];
				if (BTSLUtil.isNullString(subService)) {
					ScheduleFileProcessLog.log(VALIDATING_DATA, scheduleMasterVO.getCreatedBy(), row[0], scheduleMasterVO.getBatchID(), "Invalid Subservice", "FAIL", UPLOADED_FILE + model.getUploadedFileLocation() + TYPE + model.getRequestFor());
					String[] errorReason = { Integer.toString(++rowNumber),
							row[0], PretupsRestUtil.getMessageString(errorList[18], new String[]{subService}) };
					dataErrorList.add(errorReason);
					continue;
				}
				try {
					Integer.parseInt(subService);

				} catch (NumberFormatException e) {
					ScheduleFileProcessLog.log(VALIDATING_DATA, scheduleMasterVO.getCreatedBy(), row[0], scheduleMasterVO.getBatchID(), "Invalid Subservice", "FAIL", UPLOADED_FILE + model.getUploadedFileLocation() + TYPE + model.getRequestFor());
					String[] errorReason = { Integer.toString(++rowNumber),
							row[0], PretupsRestUtil.getMessageString(errorList[6], new String[]{subService}) };
					dataErrorList.add(errorReason);
					continue;
				}

				if (definedSubServiceValue.indexOf(subService) == -1) {
					ScheduleFileProcessLog.log(VALIDATING_DATA, scheduleMasterVO.getCreatedBy(), row[0], scheduleMasterVO.getBatchID(), "Invalid Subservice", "FAIL", UPLOADED_FILE + model.getUploadedFileLocation() + TYPE + model.getRequestFor());
					String[] errorReason = { Integer.toString(++rowNumber),
							row[0], PretupsRestUtil.getMessageString(errorList[6], new String[]{subService}) };
					dataErrorList.add(errorReason);
					continue;
				}

				String amount = null;
				try{
						if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(model.getFileType()))
					amount = row[8];
						else
						amount = row[2];
				}catch(ArrayIndexOutOfBoundsException e){
					 log.errorTrace(methodName, e);
					ScheduleFileProcessLog.log(VALIDATING_DATA, scheduleMasterVO.getCreatedBy(), row[0], scheduleMasterVO.getBatchID(), "Invalid amount", "FAIL", UPLOADED_FILE + model.getUploadedFileLocation() + TYPE + model.getRequestFor());
					String[] errorReason = { Integer.toString(++rowNumber),
							row[0], PretupsRestUtil.getMessageString(errorList[17]) };
					dataErrorList.add(errorReason);
					continue;
				}				
				if (BTSLUtil.isNullString(amount)) {
					ScheduleFileProcessLog.log(VALIDATING_DATA, scheduleMasterVO.getCreatedBy(), row[0], scheduleMasterVO.getBatchID(), "Amount not provided", "FAIL", UPLOADED_FILE + model.getUploadedFileLocation() + TYPE + model.getRequestFor());
					String[] errorReason = { Integer.toString(++rowNumber),
							row[0], PretupsRestUtil.getMessageString(errorList[17]) };
					dataErrorList.add(errorReason);
					continue;
				}
				long systemAmount;
				try {
					systemAmount = PretupsBL.getSystemAmount(amount);
				} catch (BTSLBaseException e) {
					 log.errorTrace(methodName, e);
					ScheduleFileProcessLog.log(VALIDATING_DATA, scheduleMasterVO.getCreatedBy(), row[0], scheduleMasterVO.getBatchID(), "Invalid Amount", "FAIL", UPLOADED_FILE + model.getUploadedFileLocation() + TYPE + model.getRequestFor());
					LogFactory.printLog(methodName, e.getMessage(), log);
					String[] errorReason = { Integer.toString(++rowNumber),
							row[0], PretupsRestUtil.getMessageString(errorList[7], new String[]{amount}) };
					dataErrorList.add(errorReason);
					continue;
				}				
				if(systemAmount <= 0){
					ScheduleFileProcessLog.log(VALIDATING_DATA, scheduleMasterVO.getCreatedBy(), row[0], scheduleMasterVO.getBatchID(), "Negative amount", "FAIL", UPLOADED_FILE + model.getUploadedFileLocation() + TYPE + model.getRequestFor());
					String[] errorReason = { Integer.toString(++rowNumber),
							row[0], PretupsRestUtil.getMessageString(errorList[7], new String[]{amount}) };
					dataErrorList.add(errorReason);
					continue;
				}
				String language =null;
				String country =null;
				RestrictedSubscriberVO restrictedSubscriberVO =null;
				if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(model.getFileType())){
				restrictedSubscriberVO = (RestrictedSubscriberVO) downLoadDataMap
						.get(filteredMsisdn);
				if (restrictedSubscriberVO == null) {
					ScheduleFileProcessLog.log(VALIDATING_DATA, scheduleMasterVO.getCreatedBy(), row[0], scheduleMasterVO.getBatchID(), "No information found", "FAIL", UPLOADED_FILE + model.getUploadedFileLocation() + TYPE + model.getRequestFor());
					String[] errorReason = { Integer.toString(++rowNumber),
							row[0], PretupsRestUtil.getMessageString(errorList[3]) };
					dataErrorList.add(errorReason);
					continue;
				}
				 language = restrictedSubscriberVO.getLanguage();
				 country = restrictedSubscriberVO.getCountry();

				if (systemAmount < restrictedSubscriberVO.getMinTxnAmount()
						|| systemAmount > restrictedSubscriberVO
								.getMaxTxnAmount()) {
					ScheduleFileProcessLog.log(VALIDATING_DATA, scheduleMasterVO.getCreatedBy(), row[0], scheduleMasterVO.getBatchID(), "Invalid amount. It should be in Min Max Range", "FAIL", UPLOADED_FILE + model.getUploadedFileLocation() + TYPE + model.getRequestFor());
					String[] errorReason = { Integer.toString(++rowNumber),
							row[0], PretupsRestUtil.getMessageString(errorList[8],new String[]{amount}) };
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
				scheduleDetailVO.setModifiedBy(userVOSession.getUserID());
				scheduleDetailVO.setModifiedOn(new Date());
				scheduleDetailVO.setCreatedBy(userVOSession.getUserID());
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
				scheduleDetailVO.setRowNumber(++rowNumber);
				validDataList.add(scheduleDetailVO);
			}		
			if (validDataList.isEmpty()) {
				LogFactory.printLog(methodName, "No valid information found", log);
				OracleUtil.rollbackConnection(con, className, methodName);
				response.setResponse(DATA_ERROR_KEY[4], null);
				response.setDataObject(this.writeErrorLogsOnCSV(dataErrorList));
				response.setMessageCode(PretupsErrorCodesI.SCHEDULE_RECHARGE_NO_VALID_DATA_FOUND);
				return response;
			}
			String statusData = this.checkIfSubscriberExistByStatus(con, userVOSession, restrictedSubscriberDAO, validDataList);
			this.processStatusData(statusData, validDataList, errorList, dataErrorList);
			
			if (validDataList.isEmpty()) {
				LogFactory.printLog(methodName, "All subscriber status exists", log);
				OracleUtil.rollbackConnection(con, className, methodName);
				response.setFormError(DATA_ERROR_KEY[4]);
				response.setDataObject(this.writeErrorLogsOnCSV(dataErrorList));
				response.setMessageCode(PretupsErrorCodesI.SCHEDULE_RECHARGE_NO_VALID_DATA_FOUND);
				return response;
			}
			
			String scheduleStatus = this.checkScheduleExistByStatus(con, validDataList, userVOSession);
			this.processScheduleStatus(scheduleStatus, errorList, validDataList, errorMessages);
			
			if (validDataList.isEmpty()) {
				LogFactory.printLog(methodName, "All subscriber alredy scheduled", log);
				OracleUtil.rollbackConnection(con, className, methodName);
				response.setFormError(DATA_ERROR_KEY[4]);
				response.setDataObject(this.writeErrorLogsOnCSV(dataErrorList));
				response.setMessageCode(PretupsErrorCodesI.SCHEDULE_RECHARGE_NO_VALID_DATA_FOUND);
				return response;
			}
			
			if(this.saveInScheduleRechargeDetails(con, validDataList, scheduleMasterVO, rows.size())){
				if(this.updateInScheduleMasterDetail(con, scheduleMasterVO)){
					mcomCon.finalCommit();
				}else{
					OracleUtil.rollbackConnection(con, className, methodName);
				}
                
			}else{
				OracleUtil.rollbackConnection(con, className, methodName);
			}
			
			if ("on".equalsIgnoreCase(model.getScheduleNow())){
				LogFactory.printLog(methodName, "Schedule Now is on.... Processing request", log);
				ScheduleTopUpNowBL upNowBL= new ScheduleTopUpNowBL(); 
				upNowBL.invokeProcessByStatus(con,PretupsI.SCHEDULE_NOW_TYPE, scheduleMasterVO, null);
			}
			
			if(dataErrorList.size() > 1){
				int processedRecords = validDataList.size();
				int totalRecords = rows.size();
				response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, this.writeErrorLogsOnCSV(dataErrorList));
				String message = "on".equalsIgnoreCase(model.getScheduleNow()) ? "restrictedsubs.scheduletopupdetails.msg.schedulenowsuccess" : "restrictedsubs.scheduletopuprecharge.file.x.out.of.y.records.processed.successfully";
				LogFactory.printLog(methodName, message, log);
				response.setFormError(message ,new String[]{Integer.toString(processedRecords), Integer.toString(totalRecords), scheduleMasterVO.getBatchID()});
				response.setMessageCode(PretupsErrorCodesI.SCHEDULE_RECHARGE_SUCCESSFULL);
				return response;
			}else{
				String message = "on".equalsIgnoreCase(model.getScheduleNow()) ? "restrictedsubs.scheduletopupdetails.msg.success" : "restrictedsubs.scheduletopuprecharge.file.has.been.processed.successfully";
				LogFactory.printLog(methodName, message, log);
				response.setResponse(PretupsI.RESPONSE_SUCCESS, true, message);
				response.setParameters(new String[]{scheduleMasterVO.getBatchID()});
				response.setMessageCode(PretupsErrorCodesI.SCHEDULE_RECHARGE_SUCCESSFULL);
				return response;
			}			
		}catch(BTSLBaseException | IOException | SQLException | ArrayIndexOutOfBoundsException e) {
			OracleUtil.rollbackConnection(con, className, methodName);
			throw new BTSLBaseException(e);
		}finally{
			if (mcomCon != null) {
				mcomCon.close("BatchScheduleRechargeRestServiceImpl#processUplodedScheduleRechargeFile");
				mcomCon = null;
			}
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	/**
	 * provide template for downloading on the basis of type
	 * @param connection
	 * @param fileType
	 * @param dataMap
	 * @param file
	 * @throws BTSLBaseException
	 */
	public void downloadFile(Connection connection, String fileType, Map<String, Object> dataMap, File file) throws BTSLBaseException {
		String methodName = "downloadFile";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		if (PretupsI.BATCH_TYPE_CORPORATE.equals(fileType)) {
			downloadCorpFile(connection, dataMap, file);
		} else {
			 downloadNormalFile(dataMap, file);
		}
		LogFactory.printLog(methodName, PretupsI.EXITED, log);

	}
	
	
	/**
	 * Download Corporate file
	 * 
	 * @param connection
	 * @param dataMap
	 * @param file
	 * @throws BTSLBaseException
	 */
	@SuppressWarnings("unchecked")
	private void downloadCorpFile(Connection connection,
			Map<String, Object> dataMap, File file) throws BTSLBaseException {
		String methodName = "downloadCorpFile";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try {
			RestrictedSubscriberTxnDAO restrictedSubscriberTxnDAO = new RestrictedSubscriberTxnDAO();
			Map<String, Object> data = restrictedSubscriberTxnDAO
					.loadRestrictedSubscriberList(connection,
							dataMap.get(PretupsI.OWNER_ID).toString(),
							dataMap.get(PretupsI.USER_ID).toString());
			List<String> dataList = new ArrayList<>();
			for (Entry<String, Object> entrySet : data.entrySet()) {
				RestrictedSubscriberVO restrictedSubscriberVO = (RestrictedSubscriberVO) entrySet
						.getValue();
				if (restrictedSubscriberTxnDAO
						.isAllowedMsisdn(restrictedSubscriberVO.getMsisdn())) {
					List<String> list = new ArrayList<>();
					list.add(restrictedSubscriberVO.getMsisdn());
					list.add(restrictedSubscriberVO.getSubscriberID());
					list.add(BTSLUtil.NullToString(restrictedSubscriberVO
							.getEmployeeName()));
					list.add(PretupsBL.getDisplayAmount(restrictedSubscriberVO
							.getMinTxnAmount()));
					list.add(PretupsBL.getDisplayAmount(restrictedSubscriberVO
							.getMaxTxnAmount()));
					list.add(PretupsBL.getDisplayAmount(restrictedSubscriberVO
							.getMonthlyLimit()));
					list.add(PretupsBL.getDisplayAmount(restrictedSubscriberVO
							.getTotalTransferAmount()));
					list.add(ServiceSelectorMappingCache
							.getDefaultSelectorForServiceType(
									(String) dataMap
											.get(PretupsI.SERVICE_TYPE))
							.getSelectorCode());
					dataList.add(StringUtils.join(list, ','));
				}
			}
			dataMap.put(PretupsI.HEADER_MESSAGE, "requested.amount.editable");
			dataMap.put(PretupsI.DATA, dataList);
			PretupsRestUtil.writeCSVFile(dataMap, file);
		} catch (BTSLBaseException e) {
			throw e;
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}
	
	
	/**
	 * Download Normal file
	 * 
	 * @param connection
	 * @param dataMap
	 * @param file
	 * @throws BTSLBaseException
	 */
	private void downloadNormalFile(Map<String, Object> dataMap, File file) throws BTSLBaseException {
		String methodName = "downloadNormalFile";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try {
			List<Object> dataList = new ArrayList<>(Integer.parseInt(Constants.getProperty("BATCH_MSISDN_LIST_SIZE")));
			dataMap.put(PretupsI.DATA, dataList);
			dataMap.put(PretupsI.HEADER_MESSAGE, "requested.amount.editable");
			PretupsRestUtil.writeCSVFile(dataMap, file);
		} catch (BTSLBaseException e) {
			throw e;
		} finally {
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
	 * Save recordsin ScheduleBatch Master
	 * 
	 * @param connection
	 * @param scheduleMasterVO
	 * @param userVOSession
	 * @param userVO
	 * @param model
	 * @return
	 * @throws BTSLBaseException
	 */
	public int saveInScheduleMaster(Connection connection,
			ScheduleBatchMasterVO scheduleMasterVO, UserVO userVOSession,
			UserVO userVO, RestrictedSubscriberModel model)
			throws BTSLBaseException {
		String methodName = "saveInScheduleMaster";
		LogFactory.printLog(methodName, PretupsI.EXITED, log);
		try {
			scheduleMasterVO.setStatus(PretupsI.SCHEDULE_STATUS_SCHEDULED);
			scheduleMasterVO.setScheduledDate(BTSLUtil.getDateFromString(model.getScheduleDate(), ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT))));
			scheduleMasterVO.setCreatedBy(userVOSession.getUserID());
			scheduleMasterVO.setCreatedOn(new Date());
			scheduleMasterVO.setModifiedBy(userVOSession.getUserID());
			scheduleMasterVO.setModifiedOn(new Date());
			scheduleMasterVO.setInitiatedBy(userVOSession.getUserID());
			scheduleMasterVO.setNetworkCode(userVOSession.getNetworkID());
			scheduleMasterVO.setParentCategory(model.getCategoryCode());
			scheduleMasterVO.setParentDomain(model.getDomainCode());
			scheduleMasterVO.setParentID(userVO.getUserID());
			scheduleMasterVO.setOwnerID(userVO.getOwnerID());
			scheduleMasterVO.setServiceType(model.getServiceCode());
			scheduleMasterVO.setTotalCount(Long.parseLong(model
					.getNoOfRecords().toString()));
			scheduleMasterVO.setActiveUserId(userVO.getUserID());
			if (PretupsI.BATCH_TYPE_NORMAL
					.equalsIgnoreCase(model.getFileType())) {
				scheduleMasterVO.setBatchType(PretupsI.BATCH_TYPE_NORMAL);
			} else {
				scheduleMasterVO.setBatchType(PretupsI.BATCH_TYPE_CORPORATE);
			}
			RestrictedSubscriberBL.generateScheduleBatchID(scheduleMasterVO);
			scheduleMasterVO.setRefBatchID(scheduleMasterVO.getBatchID());
			scheduleMasterVO.setIterations(model.getIterations());
			scheduleMasterVO.setFrequency(model.getFrequencyCode());
			ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
			return scheduledBatchDetailDAO.addScheduleBatchMaster(connection,	scheduleMasterVO);
		} catch (BTSLBaseException | ParseException e) {
			throw new BTSLBaseException(e);
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}
	
	/**
	 * Perform basic validation on file content
	 * 
	 * @param rows
	 * @param response
	 * @param model
	 * @return boolean
	 * @throws BTSLBaseException
	 */
	public Boolean fileContentValidation(List<String[]> rows,
			PretupsResponse<Object> response, RestrictedSubscriberModel model)
			throws BTSLBaseException {
		String methodName = "fileContentValidation";
		LogFactory.printLog(methodName, PretupsI.EXITED, log);
		try {
			int emptyRows = getEmptryRow(rows);
			if (!this.checkForInvalidOrNoRecords(rows, emptyRows, response,
					model)) {
				return false;
			}
			if (!this.checkForRecordsExceed(rows, emptyRows, response, model)) {
				return false;
			}
			return true;
		} catch (NumberFormatException e) {
			throw new BTSLBaseException(e);
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}
	
	/**
	 * Get Empty rows from file
	 * 
	 * @param rows
	 * @return
	 */
	public int getEmptryRow(List<String[]> rows) {
		int emptyRows = 0;
		for (String[] strings : rows) {
			Boolean isEmpty = true;
			if (strings.length == 0) {
				emptyRows++;
				continue;
			}
			for (Object value : strings) {
				if(value != null){
					isEmpty = false;
					break;
				}
			}			
			if(isEmpty){
				emptyRows++;
			}
		}
		return emptyRows;
	}
	
	/**
	 * Check for invalid records
	 * 
	 * @param rows
	 * @param emptyRows
	 * @param response
	 * @param model
	 * @return boolean
	 */
	public Boolean checkForInvalidOrNoRecords(List<String[]> rows,
			int emptyRows, PretupsResponse<Object> response,
			RestrictedSubscriberModel model) {
		String methodName = "checkForInvalidOrNoRecords";
		LogFactory.printLog(methodName, PretupsI.EXITED, log);
		try{
			if (rows.size() == emptyRows) {
				response.setFormError("restrictedsubs.scheduletopupdetails.msg.zerorecords");
				response.setMessageCode(PretupsErrorCodesI.SCHEDULE_RECHARGE_ZERO_RECORD);
				return false;
			}
			if (model.getNoOfRecords() != (rows.size() - emptyRows)) {
				response.setFormError("restrictedsubs.scheduletopupdetails.msg.invalidnoofrecord");
				response.setMessageCode(PretupsErrorCodesI.SCHEDULE_RECHARGE_INVALID_NO_OF_RECORDS);
				return false;
			}			
			return true;
		}finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}


	/**
	 * Get Error key for File prrocessing
	 * 
	 * @param fileType
	 * @return String[]
	 */
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
					UPLOAD_ERROR_KEYS[36], UPLOAD_ERROR_KEYS[38], UPLOAD_ERROR_KEYS[39]};
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
	 * Check for no of records in file if it exceed to the no of records
	 * provided by users
	 * 
	 * @param rows
	 * @param emptyRows
	 * @param response
	 * @param model
	 * @return boolean
	 * @throws BTSLBaseException
	 */
	public boolean checkForRecordsExceed(List<String[]> rows, int emptyRows,
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
				response.setFormError(NO_OF_RECORDS_EXCEEDS, new String[]{Integer.toString(scheduleNowContentsSize)});
				response.setMessageCode(PretupsErrorCodesI.SCHEDULE_RECHARGE_SCHEDULE_NOW_NO_OF_RECORDS_EXCEED);
				return false;
			}
			return checkForNoOfRecords(rows, emptyRows, response);			
		} catch (NumberFormatException e) {
			throw new BTSLBaseException(e);
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}
	
	/**
	 * Check for no of records
	 * @param model
	 * @param rows
	 * @param emptyRows
	 * @param response
	 * @return
	 */
	public boolean checkForNoOfRecords(List<String[]> rows,  int emptyRows, PretupsResponse<Object> response){
		
		int batchContentsSize = Integer.parseInt(Constants
				.getProperty("BATCH_MSISDN_LIST_SIZE"));		
		if(batchContentsSize < (rows.size() - emptyRows)){
			response.setFormError(NO_OF_RECORDS_EXCEEDS, new String[]{Integer.toString(batchContentsSize)});
			response.setMessageCode(PretupsErrorCodesI.SCHEDULE_RECHARGE_SCHEDULE_NOW_NO_OF_RECORDS_EXCEED);
			return false;
		}
		
		return true;
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
			return restrictedSubscriberDAO.isSubscriberExistByStatus(connection, userVO.getOwnerID(), validDataList, PretupsI.STATUS_EQUAL,	PretupsI.RES_MSISDN_STATUS_ASSOCIATED, null);
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
	 * @param dataErrorList
	 * @return List<ScheduleBatchDetailVO>
	 */
	public void processStatusData(String statusData, List<ScheduleBatchDetailVO> validDataList, String[] errorList, List<String[]> dataErrorList){
		String methodName = "processStatusData";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		List<ScheduleBatchDetailVO> tempList = new ArrayList<>();
		try{
			String[] requestDataArray;
			if (BTSLUtil.isNullString(statusData)) {
				LogFactory.printLog(methodName, "statusData is null", log);
				return;
			}			
			requestDataArray = statusData.split(",");			
			List<String> mobileNumbers = Arrays.asList(requestDataArray);			
			for(ScheduleBatchDetailVO scheduleDetailVO : validDataList){
				if(!mobileNumbers.contains(scheduleDetailVO.getMsisdn())){
					tempList.add(scheduleDetailVO);
					String[] errorReason = { Integer.toString(scheduleDetailVO.getRowNumber()), scheduleDetailVO.getMsisdn(), PretupsRestUtil.getMessageString( errorList[9]) };
					dataErrorList.add(errorReason);
				}
			}
			validDataList.removeAll(tempList);
		}finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
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
			String statusStr = "'" + PretupsI.SCHEDULE_STATUS_CANCELED + "','" + PretupsI.SCHEDULE_STATUS_EXECUTED + "'";
			String statusMode = PretupsI.STATUS_NOTIN;
			ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
			return scheduledBatchDetailDAO.isScheduleExistByStatus(connection, validDataList, statusMode, statusStr, userVO.getOwnerID(), new Date());
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
	public  void processScheduleStatus(String scheduleStatus, String[] errorList, List<ScheduleBatchDetailVO> validDataList,  Map<String, String> errorMessages){
		String methodName = "processScheduleStatus";
		LogFactory.printLog(methodName, PretupsI.EXITED, log);
		List<ScheduleBatchDetailVO> tempList = new ArrayList<>();
		try{
			if (BTSLUtil.isNullString(scheduleStatus)) {
				LogFactory.printLog(methodName, "Schedule Status is NULL", log);
				return;
			}
			String[] dataArray = scheduleStatus.split(",");
			List<String> mobileNumbers = Arrays.asList(dataArray);
			for (ScheduleBatchDetailVO scheduleBatchDetailVO : validDataList) {
				if (mobileNumbers.contains(scheduleBatchDetailVO.getMsisdn())) {
					tempList.add(scheduleBatchDetailVO);
					errorMessages.put(scheduleBatchDetailVO.getMsisdn(), PretupsRestUtil.getMessageString(errorList[10]));
				}
			}
			validDataList.removeAll(tempList);
		}finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
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
                scheduleBatchMasterVO.setUploadFailedCount((long)(totalRows - addCount));
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
}
