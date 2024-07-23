package com.restapi.c2s.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ErrorMap;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.mcom.common.CommonUtil;
import com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberBL;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberDAO;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;
import com.btsl.pretups.restrictedsubs.businesslogic.ScheduleTopUpNowBL;
import com.btsl.pretups.restrictedsubs.businesslogic.ScheduledBatchDetailDAO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.pretups.scheduletopup.process.BatchFileParserI;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.NumberConstants;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.OracleUtil;
import com.restapi.c2sservices.service.ReadGenericFileUtil;

@Component
public class C2SBulkEVDProcessor {

	private CommonUtil commonUtil = new CommonUtil();

	private static final Log log = LogFactory.getLog(C2SBulkEVDProcessor.class.getName());
	private ScheduleBatchMasterVO scheduleMasterVO = null;

	public int addBatch(Connection con, C2SBulkEvdRechargeRequestVO requestVO, ChannelUserVO userVO,
			String servicekeyword, int record) throws ParseException, BTSLBaseException, SQLException {

		String batchID;
		long totalCount;
		long failCount;
		// /
		// Constructing the new BatchVO for the creation of the new
		// batch
		// at this time the batchID of the batch will be the
		// referenceBatchID of the batch
		// /
		scheduleMasterVO = new ScheduleBatchMasterVO();
		Date curDate = new Date();
		scheduleMasterVO.setStatus(PretupsI.SCHEDULE_STATUS_SCHEDULED);
		scheduleMasterVO.setScheduledDate(BTSLUtil.getDateFromDateString(requestVO.getData().getScheduleDate()));
		scheduleMasterVO.setCreatedBy(userVO.getActiveUserID());// changed from active
		scheduleMasterVO.setCreatedOn(curDate);
		scheduleMasterVO.setModifiedBy(userVO.getActiveUserID());// changed from active
		scheduleMasterVO.setModifiedOn(curDate);
		scheduleMasterVO.setInitiatedBy(userVO.getUserID());
		scheduleMasterVO.setNetworkCode(userVO.getNetworkID());
		scheduleMasterVO.setParentCategory(userVO.getCategoryCode());
		scheduleMasterVO.setParentDomain(userVO.getCategoryVO().getDomainCodeforCategory());

		scheduleMasterVO.setParentID(userVO.getUserID());
		scheduleMasterVO.setOwnerID(userVO.getOwnerID());
		scheduleMasterVO.setServiceType(servicekeyword);
		scheduleMasterVO.setTotalCount(record);
		scheduleMasterVO.setActiveUserId(userVO.getActiveUserID());
		scheduleMasterVO.setFrequency(requestVO.getData().getOccurence());
		scheduleMasterVO.setIterations(Integer.parseInt(requestVO.getData().getNoOfDays()));
		scheduleMasterVO.setSuccessfulCount(0);
		if (PretupsI.BATCH_TYPE_NORMAL.equalsIgnoreCase(requestVO.getData().getBatchType())) {
			scheduleMasterVO.setBatchType(PretupsI.BATCH_TYPE_NORMAL);
		} else {
			scheduleMasterVO.setBatchType(PretupsI.BATCH_TYPE_CORPORATE);
		}

		// /
		// Generation of the new BatchID
		// /
		RestrictedSubscriberBL.generateScheduleBatchID(scheduleMasterVO);
		scheduleMasterVO.setRefBatchID(scheduleMasterVO.getBatchID());
		batchID = scheduleMasterVO.getBatchID();

		// creating the single line logger for the indication of the
		// starting of the processing
		// /
		// ScheduleFileProcessLog.log("Schedule File Processing START",
		// theForm.getCreatedBy(), null, theForm.getBatchID(), "FILE = " +
		// theForm.getFileNameStr() + "PROCESSING START", "START", "TYPE=" +
		// theForm.getRequestFor());

		RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
		ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
		// /
		// Adding the new batch in the batch master table.
		// /
		int count = scheduledBatchDetailDAO.addScheduleBatchMaster(con, scheduleMasterVO);

		totalCount = scheduleMasterVO.getTotalCount();
		failCount = scheduleMasterVO.getUploadFailedCount();
		return count;
	}

	public C2SBulkEvdRechargeResponseVO processRequestBulkEVD(C2SBulkEvdRechargeRequestVO requestVO,
			String serviceKeyword, String requestIDStr, String requestFor, MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag) throws BTSLBaseException {
		final String METHOD_NAME = "processRequestBulkEVD";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered");
		}
		String batchID = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		String contentsSize = null;
		String batchContentsSize = null;
		// Schedule Now Recharge
		String scheduleNowContentsSize = null;
		HashMap<String, List<String>> fileContent = new HashMap<String, List<String>>();
		LinkedHashMap<String, List<String>> bulkDataMap;
		int records = 0;
		int errorRecords = 0;
		int totalRecords = 0;
		ArrayList fileContents = null; // contains all the data form the file
		RestrictedSubscriberVO errorVO = null;
		HashMap scheduleInfoMap = new HashMap();
		boolean isErrorFound = false;
		String userpin;
		String processedRecords = "";
		C2SBulkEvdRechargeResponseVO response = new C2SBulkEvdRechargeResponseVO();
		ArrayList finalList = new ArrayList();
		String arr[] = null;
		Locale locale = new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE),
				PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
		ErrorMap errorMap = new ErrorMap();
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			userpin = requestVO.getData().getPin();
			/*
			 * Authentication
			 * 
			 * @throws BTSLBaseException
			 */
			OAuthenticationUtil.validateTokenApi(requestVO, headers, responseSwag);
			try {
				contentsSize = Constants.getProperty("RESTRICTED_MSISDN_LIST_SIZE");
				batchContentsSize = Constants.getProperty("BATCH_MSISDN_LIST_SIZE");
				// Schedule Now Recharge
				scheduleNowContentsSize = Constants.getProperty("SCHEDULE_NOW_BATCH_RECHARGE_FILE_SIZE").trim();

			} catch (Exception e) {
				if (log.isDebugEnabled()) {
					log.debug(METHOD_NAME, "RESTRICTED_MSISDN_LIST_SIZE not defined in Constant Property file");
				}
				log.errorTrace(METHOD_NAME, e);
				throw new BTSLBaseException(this, METHOD_NAME,
						"restrictedsubs.scheduletopupdetails.msg.contentsizemissing");
			}
			ChannelUserVO channelUserVO = new ChannelUserVO();
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			channelUserVO = channelUserDAO.loadChannelUserDetails(con, requestVO.getData().getMsisdn());
			channelUserVO.setActiveUserID(channelUserVO.getUserID());
			if (PretupsI.STAFF_USER_TYPE.equals(channelUserVO.getUserType())) {
				// validateStaffLoginDetails(con, theForm, request, mapping, loginLoggerVO,
				// channelUserVO);
				UserDAO userDao = new UserDAO();
				UserPhoneVO phoneVO = userDao.loadUserPhoneVO(con, channelUserVO.getUserID());
				if (phoneVO != null) {
					channelUserVO.setActiveUserMsisdn(phoneVO.getMsisdn());
					channelUserVO.setActiveUserPin(phoneVO.getSmsPin());
				}
				// Set Staff User Details
				ChannelUserVO staffUserVO = new ChannelUserVO();
				UserPhoneVO staffphoneVO = new UserPhoneVO();
				BeanUtils.copyProperties(staffUserVO, channelUserVO);
				if (phoneVO != null) {
					BeanUtils.copyProperties(staffphoneVO, phoneVO);
					staffUserVO.setUserPhoneVO(staffphoneVO);
				}
				staffUserVO.setPinReset(channelUserVO.getPinReset());
				channelUserVO.setStaffUserDetails(staffUserVO);
				ChannelUserVO parentChannelUserVO = new UserDAO().loadUserDetailsFormUserID(con,
						channelUserVO.getParentID());

				staffUserDetails(channelUserVO, parentChannelUserVO);

				channelUserVO.setPrefixId(parentChannelUserVO.getPrefixId());
			}
			if (PretupsI.YES.equals(channelUserVO.getUserPhoneVO().getPinRequired())) {
				if (BTSLUtil.isNullString(userpin)) {
					throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN, "");
				} else {
					try {
						ChannelUserBL.validatePIN(con, channelUserVO, userpin);
					} catch (BTSLBaseException be) {
						if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN))
								|| (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
							OracleUtil.commit(con);
						}
						throw new BTSLBaseException(this, METHOD_NAME, be.getMessageKey(), "");
					}
				}
			}

			if (PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(channelUserVO.getOutSuspened())) {
				if (log.isDebugEnabled()) {
					log.debug(METHOD_NAME, "USER IS OUT SUSPENDED IN THE SYSTEM");
				}
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.OUT_SUSPENDED, "");
			}
			String systemDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);
			if (PretupsI.ON.equalsIgnoreCase(requestVO.getData().getScheduleNow()) && !commonUtil.isSameDay(
					new SimpleDateFormat(systemDateFormat).parse(requestVO.getData().getScheduleDate()), new Date())) {
				if (log.isDebugEnabled()) {
					log.debug(METHOD_NAME, "PLESE SPECIFY TODAYS DATE");
				}
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2C_BULK_NOT_CURRNT_DATE);
			}
			if (BTSLUtil.isDateBeforeToday(
					new SimpleDateFormat(systemDateFormat).parse(requestVO.getData().getScheduleDate()))) {
				if (log.isDebugEnabled()) {
					log.debug(METHOD_NAME, "PLESE SPECIFY TODAYS DATE");
				}
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2C_BULK_LESSTHAN_CURRNT_DATE);
			}

			// code for read file content
			ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
			HashMap<String, String> fileDetailsMap = new HashMap<String, String>();
			fileDetailsMap.put(PretupsI.FILE_TYPE1, requestVO.getData().getFileType());
			fileDetailsMap.put(PretupsI.FILE_NAME, requestVO.getData().getFileName());
			fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, requestVO.getData().getFile());
			fileDetailsMap.put(PretupsI.SERVICE_KEYWORD, serviceKeyword);

			bulkDataMap = fileUtil.uploadAndReadGenericFile(fileDetailsMap, 1, errorMap);

			if (Integer.parseInt(fileDetailsMap.get(PretupsI.COLUMN_LENGTH)) == NumberConstants.NINE.getIntValue()) {
				requestVO.getData().setBatchType(PretupsI.BATCH_TYPE_CORPORATE);
			} else {
				requestVO.getData().setBatchType(PretupsI.BATCH_TYPE_NORMAL);
			}

			if (errorMap.getRowErrorMsgLists() == null) {
				errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
			}

			String dataStr = null;
			fileContents = commonUtil.getFileContentsList(bulkDataMap);
			records = fileContents.size();
			errorRecords = errorMap.getRowErrorMsgLists().size();
			totalRecords = records + errorRecords;
			response.setNumberOfRecords(totalRecords);
			// /
			// If file does not contain record as entered by the user then
			// show the error message
			// (records excludes blank lines)
			// /

			if (records == 0) {
				if (log.isDebugEnabled()) {
					log.debug(METHOD_NAME, "Number of records are zero : ");
				}
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2C_BULK_No_RECORDS, "");
			}
			/*
			 * if (records != Integer.parseInt(requestVO.getData().get)) { if
			 * (log.isDebugEnabled()) { log.debug("processFile",
			 * "File contents size is not matching with specified size : " +
			 * fileContents.size()); } throw new BTSLBaseException(this, "processFile",
			 * "restrictedsubs.scheduletopupdetails.msg.invalidnoofrecord",
			 * "scheduleDetail"); }
			 */
			// Schedule Now Recharge
			// start code added to check if the number of records in file is more that the
			// maximum size define in the constant.properties as value

			if (PretupsI.ON.equals(requestVO.getData().getScheduleNow())) {
				try {
					if ((totalRecords) > Integer.parseInt(scheduleNowContentsSize)) {
						if (log.isDebugEnabled())
							log.debug(METHOD_NAME,
									"File contents size of the file is not valid in constant properties file : "
											+ fileContents.size());
						throw new BTSLBaseException(this, METHOD_NAME,
								"restrictedsubs.scheduletopupdetails.msg.noofrecordexeced", 0,
								new String[] { scheduleNowContentsSize }, "scheduleDetail");
					}
				} catch (BTSLBaseException be) {
					if (log.isDebugEnabled())
						log.debug(METHOD_NAME,
								"File contents size of the file is more than that in constant properties file : "
										+ fileContents.size());
					log.errorTrace(METHOD_NAME, be);
					throw new BTSLBaseException(this, METHOD_NAME,
							"restrictedsubs.scheduletopupdetails.msg.noofrecordexeced", 0,
							new String[] { scheduleNowContentsSize }, "scheduleDetail");
				} catch (Exception e) {
					if (log.isDebugEnabled())
						log.debug(METHOD_NAME,
								"SCHEDULE_NOW_BATCH_RECHARGE_FILE_SIZE not defined in Constant Property file");
					log.errorTrace(METHOD_NAME, e);
					throw new BTSLBaseException(this, METHOD_NAME,
							"restrictedsubs.scheduletopupdetails.msg.contentsizemissing");
				}

			}
			// code ended
			// /
			// it can not be allowed to process the file if MSISDN's are
			// more than the defined Limit
			// /

			if (PretupsI.OFF.equals(requestVO.getData().getScheduleNow())) {
				if (PretupsI.BATCH_TYPE_CORPORATE.equals(requestVO.getData().getBatchType())) {
					if ((totalRecords) > Integer.parseInt(contentsSize)) {
						if (log.isDebugEnabled())
							log.debug(METHOD_NAME,
									"File contents size of the file is not valid in constant properties file : "
											+ fileContents.size());
						throw new BTSLBaseException(this, METHOD_NAME,
								"restrictedsubs.scheduletopupdetails.msg.noofrecordexeced", 0,
								new String[] { contentsSize }, "scheduleDetail");
					}
				} else {
					if ((totalRecords) > Integer.parseInt(batchContentsSize)) {
						if (log.isDebugEnabled())
							log.debug(METHOD_NAME,
									"File contents size of the file is not valid in constant properties file : "
											+ fileContents.size());
						throw new BTSLBaseException(this, METHOD_NAME,
								"restrictedsubs.scheduletopupdetails.msg.noofrecordexeced", 0,
								new String[] { batchContentsSize }, "scheduleDetail");
					}

				}
			}

			// /
			// Check for the duplicate mobile numbers in the list
			// here we add the information in the new list called finalList it
			// contains informaion about all
			// the data.
			// /
			String duplicateMSISDN = "restrictedsubs.scheduletopupdetails.errorfile.msg.duplicatemsisdn";
			if (fileContents != null && records > 0) {

				for (int i = 0; i < records; i++) {
					dataStr = (String) fileContents.get(i);
					arr = dataStr.split(",");
					errorVO = new RestrictedSubscriberVO();
					if (arr[0] != null && arr[0].trim().length() != 0) {
						if (!commonUtil.isContain(finalList, arr[0])) {
							errorVO.setLineNumber((i + 1) + "");
							errorVO.setMsisdn(dataStr);
							finalList.add(errorVO);
						} else {
							// ScheduleFileProcessLog.log("Processing File", requestVO.getCreatedBy(),
							// arr[0], requestVO.getBatchID(), "Mobile number is Duplicate", "FAIL",
							// "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" +
							// requestVO.getRequestFor());
							errorVO.setLineNumber((i + 1) + "");
							errorVO.setMsisdn(arr[0] + "(D)");
							errorVO.setErrorCode(duplicateMSISDN);
							errorVO.setisErrorFound(true);
							errorVO.setAmount(Long.parseLong(arr[3]));
							finalList.add(errorVO);
						}
					}
				}
			}
			int count = 0;
			count = addBatch(con, requestVO, channelUserVO, serviceKeyword, totalRecords);
			if (count <= 0) {
				mcomCon.finalRollback();
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.C2C_BULK_FILE_UNSUCCESSFUL, "");
			}

			ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceTypeObject(serviceKeyword,
					PretupsI.C2S_MODULE);
			BatchFileParserI batchFileParserI = (BatchFileParserI) Class.forName(serviceKeywordCacheVO.getFileParser())
					.newInstance();
			String[] uploadErrorList = batchFileParserI.getErrorKeys(requestVO.getData().getBatchType());
			if (PretupsI.SERVICE_TYPE_CHNL_RECHARGE.equalsIgnoreCase(serviceKeyword)
					|| PretupsI.SERVICE_TYPE_VAS_RECHARGE.equalsIgnoreCase(serviceKeyword)
					|| PretupsI.SERVICE_TYPE_GIFT_RECHARGE.equalsIgnoreCase(serviceKeyword)
					|| PretupsI.SERVICE_TYPE_CHNL_RECHARGE_PSTN.equalsIgnoreCase(serviceKeyword)
					|| PretupsI.SERVICE_TYPE_CHNL_RECHARGE_INTR.equalsIgnoreCase(serviceKeyword)
					|| PretupsI.SERVICE_TYPE_POSTPAID_BILL_PAYMENT.equalsIgnoreCase(serviceKeyword)
					|| PretupsI.SERVICE_TYPE_CHNL_DATA_RECHARGE.equalsIgnoreCase(serviceKeyword)
					|| PretupsI.SERVICE_TYPE_DVD.equalsIgnoreCase(serviceKeyword)
					|| PretupsI.SERVICE_TYPE_EVD.equalsIgnoreCase(serviceKeyword)) {
				scheduleInfoMap.put(BatchFileParserI.ERROR_KEY, uploadErrorList);
				// scheduleInfoMap.put(BatchFileParserI.DATA_MAP, theForm.getDownLoadDataMap());
				scheduleInfoMap.put(BatchFileParserI.USER_ID, channelUserVO.getUserID());
				scheduleInfoMap.put(BatchFileParserI.OWNER_ID, channelUserVO.getOwnerID());
				scheduleInfoMap.put(BatchFileParserI.BATCH_ID, batchID);
				scheduleInfoMap.put("CREATED_BY", scheduleMasterVO.getCreatedBy());
				scheduleInfoMap.put("FINAL_LIST", finalList);
				scheduleInfoMap.put("FINAL_LIST_SIZE", records);
				scheduleInfoMap.put("FILE_NAME", requestVO.getData().getFileName());
				scheduleInfoMap.put("REQUEST_FOR", requestFor);
				// scheduleInfoMap.put("DOWNLOAD_BATCH_ID",
				// scheduleMasterVO.getDownLoadBatchID());
				scheduleInfoMap.put("USER_VO", channelUserVO);
				scheduleInfoMap.put("SCHEDULED_VO", scheduleMasterVO);
				scheduleInfoMap.put("MODIFIED_ON", scheduleMasterVO.getModifiedOn());
				scheduleInfoMap.put("CREATED_ON", scheduleMasterVO.getCreatedOn());
				scheduleInfoMap.put("RECORDS", totalRecords);
				if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(requestVO.getData().getBatchType())) {
					scheduleInfoMap.put("BATCH_TYPE", PretupsI.BATCH_TYPE_CORPORATE);
				} else {
					scheduleInfoMap.put("BATCH_TYPE", PretupsI.BATCH_TYPE_NORMAL);
				}

			}
			BTSLMessages btslMessage = null;
			try {
				batchFileParserI.uploadFile(con, requestVO.getData().getFileType(), scheduleInfoMap, isErrorFound);
			} catch (BTSLBaseException e) {
				log.error(METHOD_NAME, "Exception:e=" + e);
				log.errorTrace(METHOD_NAME, e);
			}
			isErrorFound = ((Boolean) scheduleInfoMap.get("IS_ERROR_FOUND")).booleanValue();
			ArrayList errorList = (ArrayList) scheduleInfoMap.get("FINAL_LIST");

			if (!isErrorFound && (errorList != null && errorList.size() == 0)) {

				if (!PretupsI.ON.equalsIgnoreCase(requestVO.getData().getScheduleNow())) {

					if (PretupsI.SCHEDULE.equalsIgnoreCase(requestFor)) {
						String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2C_BULK_SCH_SUCCESS,
								new String[] { String.valueOf(scheduleMasterVO.getBatchID()) });
						response.setStatus(PretupsI.RESPONSE_SUCCESS.toString());
						responseSwag.setStatus(HttpStatus.SC_ACCEPTED);
						response.setMessage(msg);
						response.setScheduleBatchId(String.valueOf(scheduleMasterVO.getBatchID()));
						response.setMessageCode(PretupsErrorCodesI.C2C_BULK_SCH_SUCCESS);
						// btslMessage = new
						// BTSLMessages("restrictedsubs.scheduletopupdetails.msg.success",new
						// String[]{scheduleMasterVO.getBatchID()},"firstPage");
					} else if (PretupsI.RESCHEDULE.equals(requestFor)) {
						// btslMessage = new
						// BTSLMessages("restrictedsubs.rescheduletopupdetails.msg.success",new
						// String[]{scheduleMasterVO.getRefBatchID(),scheduleMasterVO.getBatchID()},"firstPage");

						String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2C_BULK_RESCH_SUCCESS,
								new String[] { scheduleMasterVO.getRefBatchID(), scheduleMasterVO.getBatchID() });
						response.setMessage(msg);
						response.setScheduleBatchId(String.valueOf(scheduleMasterVO.getBatchID()));
						response.setMessageCode(PretupsErrorCodesI.C2C_BULK_RESCH_SUCCESS);
						responseSwag.setStatus(HttpStatus.SC_ACCEPTED);
					}

				}
				processedRecords = (String) scheduleInfoMap.get("PROCESSED_RECS");
			} else {
				errorList = (ArrayList) scheduleInfoMap.get("FINAL_LIST");
				ArrayList list = new ArrayList();

				if (errorList != null) {
					int errorListSize = errorList.size();
					for (int i = 0, j = errorListSize; i < j; i++) {
						errorVO = (RestrictedSubscriberVO) errorList.get(i);
						if (!BTSLUtil.isNullString(errorVO.getErrorCode())) {
							RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
							ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
							MasterErrorList masterErrorList = new MasterErrorList();
							masterErrorList.setErrorCode(errorVO.getErrorCode());
							String msg = RestAPIStringParser.getMessage(locale, errorVO.getErrorCode(),
									errorVO.getErrorCodeArgs());
							masterErrorList.setErrorMsg(msg);
							masterErrorLists.add(masterErrorList);
							rowErrorMsgLists.setMasterErrorList(masterErrorLists);
							rowErrorMsgLists.setRowValue(errorVO.getMsisdn());
							rowErrorMsgLists
									.setRowName("Line" + String.valueOf(Long.parseLong(errorVO.getLineNumber()) + 2));
							if (errorMap.getRowErrorMsgLists() == null)
								errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
							(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
						}
					}
				}

				processedRecords = (String) scheduleInfoMap.get("PROCESSED_RECS");
				if (("0").equals(processedRecords)) {
					MasterErrorList masterErrorList = new MasterErrorList();
					masterErrorList.setErrorCode("restrictedsubs.associatesubscriberdetails.msg.novaliddatainfile");
					String msg = RestAPIStringParser.getMessage(locale,
							"restrictedsubs.associatesubscriberdetails.msg.novaliddatainfile", null);
					masterErrorList.setErrorMsg(msg);
					errorMap.setMasterErrorList(new ArrayList<MasterErrorList>());
					errorMap.getMasterErrorList().add(masterErrorList);
				}
			}

			if (BTSLUtil.isNullString(requestVO.getData().getOccurence())) {
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.OCCURENCE_EMTPY, "");
			}

			// Schedule Now Recharge
			if (requestVO.getData().getScheduleNow() != null && !requestVO.getData().getScheduleNow().isEmpty()) {

				if (PretupsI.ON.equals(requestVO.getData().getScheduleNow())) {

					ScheduleTopUpNowBL upNowBL = new ScheduleTopUpNowBL();

					if (mcomCon != null) {
						mcomCon.close("C2SBulkEVDProcessor#processRequestBulkEVD");
						mcomCon = null;
					}

					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
					String respCode = null;
					try {
						if (errorMap == null)
							errorMap = new ErrorMap();
						upNowBL.invokeProcessByStatus(con, PretupsI.SCHEDULE_NOW_TYPE, scheduleMasterVO, errorMap);
						if (PretupsI.SCHEDULE.equalsIgnoreCase(requestFor)) {

							String msg = null;

							if (scheduleMasterVO.getSuccessfulCount() == totalRecords) {
								response.setStatus(PretupsI.TXN_STATUS_SUCCESS);
								responseSwag.setStatus(HttpStatus.SC_OK);
								respCode = PretupsErrorCodesI.C2C_BULK_SCH_SUCCESS;
								msg = RestAPIStringParser.getMessage(locale, respCode,
										new String[] { String.valueOf(scheduleMasterVO.getSuccessfulCount()),
												String.valueOf(totalRecords), scheduleMasterVO.getBatchID() });
							} else if (scheduleMasterVO.getSuccessfulCount() > 0
									&& scheduleMasterVO.getSuccessfulCount() < totalRecords) {
								response.setStatus(String.valueOf(HttpStatus.SC_ACCEPTED));
								responseSwag.setStatus(HttpStatus.SC_ACCEPTED);
								respCode = PretupsErrorCodesI.PARTIAL_SUCCESS;
								msg = RestAPIStringParser.getMessage(locale, respCode,
										new String[] { String.valueOf(scheduleMasterVO.getSuccessfulCount()),
												String.valueOf(totalRecords), scheduleMasterVO.getBatchID() });
							} else {
								response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
								responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
								respCode = PretupsErrorCodesI.BULK_PROCESS_FAILED_ALLRECORDS;
								msg = RestAPIStringParser.getMessage(locale, respCode,
										new String[] { scheduleMasterVO.getBatchID() });
							}
							log.debug("Counts Checks : ", scheduleMasterVO.getSuccessfulCount() + " " + totalRecords);

							response.setScheduleBatchId(String.valueOf(scheduleMasterVO.getBatchID()));
							response.setMessage(msg);
							response.setMessageCode(respCode);
							response.setErrorMap(errorMap);
							writeFileForResponse(response, errorMap);
						}

					} catch (BTSLBaseException e) {
						log.error(METHOD_NAME, "Exception:e=" + e);
						log.errorTrace(METHOD_NAME, e);
						btslMessage = new BTSLMessages(
								"restrictedsubs.associatesubscriberdetails.msg.novaliddatainfile",
								new String[] { scheduleMasterVO.getBatchID() }, "showMsg");
						if (!BTSLUtil.isNullorEmpty(e.getMessageKey())) {
							String message = getErrorMessage(e.getMessageKey());
							response.setMessage(message);
							response.setMessageCode(e.getMessageKey());
						}
						response.setErrorMap(errorMap);
						response.setStatus(PretupsI.RESPONSE_FAIL.toString());
						responseSwag.setStatus(HttpResponseCodes.SC_BAD_REQUEST);
						response.setScheduleBatchId(String.valueOf(scheduleMasterVO.getBatchID()));
						writeFileForResponse(response, errorMap);

					}
				} else {
					try {
						if (PretupsI.SCHEDULE.equalsIgnoreCase(requestFor) && !isErrorFound) {
							response.setMessageCode("");
							String msg = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.C2S_BULK_SCHEDULE_SUCCESS,
									new String[] { (String) scheduleInfoMap.get("PROCESSED_RECS"),
											String.valueOf(records), scheduleMasterVO.getBatchID() });
							response.setStatus(PretupsI.RESPONSE_SUCCESS.toString());
							responseSwag.setStatus(HttpResponseCodes.SC_OK);
							response.setScheduleBatchId(String.valueOf(scheduleMasterVO.getBatchID()));
							response.setMessage(msg);
							response.setMessageCode(PretupsErrorCodesI.C2S_BULK_SCHEDULE_SUCCESS);
							response.setErrorMap(errorMap);
							writeFileForResponse(response, errorMap);
							// btslMessage = new
							// BTSLMessages("restrictedsubs.scheduletopupdetails.msg.schedulenowsuccess"+"",new
							// String[]{String.valueOf(scheduleMasterVO.getSuccessfulCount()),
							// String.valueOf(scheduleMasterVO.getNoOfRecords()),
							// scheduleMasterVO.getBatchID()},"showMsg");
						} else  if(records > Integer.parseInt(processedRecords)  && records != Integer.parseInt(processedRecords)){
							response.setMessageCode( PretupsErrorCodesI.PARTIAL_SUCCESS);
							String msg = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.PARTIAL_SUCCESS,
									new String[] { String.valueOf(processedRecords),
											String.valueOf(records), scheduleMasterVO.getBatchID() });
							response.setStatus(String.valueOf(HttpStatus.SC_ACCEPTED));
							responseSwag.setStatus(HttpStatus.SC_ACCEPTED);
							response.setScheduleBatchId(String.valueOf(scheduleMasterVO.getBatchID()));
							response.setMessage(msg);
							response.setMessageCode(PretupsErrorCodesI.C2S_BULK_SCHEDULE_FAILED);
							response.setErrorMap(errorMap);
							writeFileForResponse(response, errorMap);
							
						}else
						
						{
							response.setMessageCode("");
							String msg = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.C2S_BULK_SCHEDULE_FAILED,
									new String[] { (String) scheduleInfoMap.get("PROCESSED_RECS"),
											String.valueOf(records), scheduleMasterVO.getBatchID() });
							response.setStatus(PretupsI.RESPONSE_FAIL.toString());
							responseSwag.setStatus(HttpResponseCodes.SC_BAD_REQUEST);
							response.setScheduleBatchId(String.valueOf(scheduleMasterVO.getBatchID()));
							response.setMessage(msg);
							response.setMessageCode(PretupsErrorCodesI.C2S_BULK_SCHEDULE_FAILED);
							response.setErrorMap(errorMap);
							writeFileForResponse(response, errorMap);
						}
					} catch (BTSLBaseException e) {
						log.error(METHOD_NAME, "Exception:e=" + e);
						log.errorTrace(METHOD_NAME, e);
						btslMessage = new BTSLMessages(
								"restrictedsubs.associatesubscriberdetails.msg.novaliddatainfile",
								new String[] { scheduleMasterVO.getBatchID() }, "showMsg");

						if (!BTSLUtil.isNullorEmpty(e.getMessageKey())) {
							String message = getErrorMessage(e.getMessageKey());
							response.setMessage(message);
							response.setMessageCode(e.getMessageKey());
						}
						response.setErrorMap(errorMap);
						response.setStatus(PretupsI.RESPONSE_FAIL.toString());
						responseSwag.setStatus(HttpResponseCodes.SC_BAD_REQUEST);
						response.setScheduleBatchId(String.valueOf(scheduleMasterVO.getBatchID()));
						writeFileForResponse(response, errorMap);
					}
				}

			}

		} catch (BTSLBaseException be) {
			log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(PretupsI.UNAUTHORIZED_ACCESS.toString());
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(PretupsI.RESPONSE_FAIL.toString());
			}

			response.setErrorMap(errorMap);
			try {
				writeFileForResponse(response, errorMap);
			} catch (IOException io) {
				log.debug(METHOD_NAME, io);
			}
		} catch (Exception e) {
			log.debug(METHOD_NAME, e);
			response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),
							(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.REQ_NOT_PROCESS, null);
			response.setMessage(resmsg);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(PretupsI.RESPONSE_FAIL.toString());

		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2sBulkEvdProcessor#processRequestBulkEVD");
				mcomCon = null;
			}
			log.debug(METHOD_NAME, "Exit");
		}
		return response;

	}

	/**
	 * Common Method used for set the login details for StaffUser
	 * 
	 * @param channelUserVO
	 * @param parentChannelUserVO
	 */

	protected void staffUserDetails(ChannelUserVO channelUserVO, ChannelUserVO parentChannelUserVO) {
		channelUserVO.setUserID(channelUserVO.getParentID());
		channelUserVO.setParentID(parentChannelUserVO.getParentID());
		channelUserVO.setOwnerID(parentChannelUserVO.getOwnerID());
		channelUserVO.setStatus(parentChannelUserVO.getStatus());
		channelUserVO.setUserType(parentChannelUserVO.getUserType());
		channelUserVO.setStaffUser(true);
		channelUserVO.setMsisdn(parentChannelUserVO.getMsisdn());
		channelUserVO.setPinRequired(parentChannelUserVO.getPinRequired());
		channelUserVO.setSmsPin(parentChannelUserVO.getSmsPin());
		channelUserVO.setParentLoginID(parentChannelUserVO.getLoginID());
	}

	private void writeFileForResponse(C2SBulkRechargeResponseVO response, ErrorMap errorMap)
			throws BTSLBaseException, IOException {
		if (errorMap == null || errorMap.getRowErrorMsgLists() == null || errorMap.getRowErrorMsgLists().size() <= 0)
			return;
		List<List<String>> rows = new ArrayList<>();
		for (int i = 0; i < errorMap.getRowErrorMsgLists().size(); i++) {
			RowErrorMsgLists rowErrorMsgList = errorMap.getRowErrorMsgLists().get(i);
			for (int col = 0; col < rowErrorMsgList.getMasterErrorList().size(); col++) {
				MasterErrorList masterErrorList = rowErrorMsgList.getMasterErrorList().get(col);
				rows.add((Arrays.asList(rowErrorMsgList.getRowName(), rowErrorMsgList.getRowValue(),
						masterErrorList.getErrorMsg())));
			}

		}
		String filePathCons = Constants.getProperty("ErrorBatchC2CUserListFilePath");
		C2CFileUploadApiController c2CFileUploadApiControllerObject = new C2CFileUploadApiController();
		c2CFileUploadApiControllerObject.validateFilePathCons(filePathCons);

		String filePathConstemp = filePathCons + "temp/";
		c2CFileUploadApiControllerObject.createDirectory(filePathConstemp);

		String filepathtemp = filePathConstemp;

		String logErrorFilename = "Errorlog_" + (System.currentTimeMillis());
		writeFileCSV(rows, filepathtemp + logErrorFilename + ".csv");
		File error = new File(filepathtemp + logErrorFilename + ".csv");
		byte[] fileContent = FileUtils.readFileToByteArray(error);
		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		response.setFileAttachment(encodedString);
		response.setFileName(logErrorFilename + ".csv");
	}

	public void writeFileCSV(List<List<String>> listBook, String excelFilePath) throws IOException {
		try (FileWriter csvWriter = new FileWriter(excelFilePath)) {
			csvWriter.append("Line number");
			csvWriter.append(Constants.getProperty("FILE_SEPARATOR_C2C"));
			csvWriter.append("Mobile number");
			csvWriter.append(Constants.getProperty("FILE_SEPARATOR_C2C"));
			csvWriter.append("Reason");
			csvWriter.append("\n");

			for (List<String> rowData : listBook) {
				csvWriter.append(String.join(Constants.getProperty("FILE_SEPARATOR_C2C"), rowData));
				csvWriter.append("\n");
			}
		}
	}

	private void writeFileForResponse(C2SBulkEvdRechargeResponseVO response, ErrorMap errorMap)
			throws BTSLBaseException, IOException {
		if (errorMap == null || errorMap.getRowErrorMsgLists() == null || errorMap.getRowErrorMsgLists().size() <= 0)
			return;
		List<List<String>> rows = new ArrayList<>();
		for (int i = 0; i < errorMap.getRowErrorMsgLists().size(); i++) {
			RowErrorMsgLists rowErrorMsgList = errorMap.getRowErrorMsgLists().get(i);
			for (int col = 0; col < rowErrorMsgList.getMasterErrorList().size(); col++) {
				MasterErrorList masterErrorList = rowErrorMsgList.getMasterErrorList().get(col);
				rows.add((Arrays.asList(rowErrorMsgList.getRowName(), rowErrorMsgList.getRowValue(),
						masterErrorList.getErrorMsg())));
			}

		}
		String filePathCons = Constants.getProperty("ErrorBatchC2CUserListFilePath");
		C2CFileUploadApiController c2CFileUploadApiControllerObject = new C2CFileUploadApiController();
		c2CFileUploadApiControllerObject.validateFilePathCons(filePathCons);

		String filePathConstemp = filePathCons + "temp/";
		c2CFileUploadApiControllerObject.createDirectory(filePathConstemp);

		String filepathtemp = filePathConstemp;

		String logErrorFilename = "Errorlog_" + (System.currentTimeMillis());
		writeFileCSV(rows, filepathtemp + logErrorFilename + ".csv");
		File error = new File(filepathtemp + logErrorFilename + ".csv");
		byte[] fileContent = FileUtils.readFileToByteArray(error);
		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		response.setFileAttachment(encodedString);
		response.setFileName(logErrorFilename + ".csv");
	}

	/**
	 * 
	 * @param errorCode
	 * @return
	 */
	public String getErrorMessage(String errorCode) {
		String message = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),
						(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
				errorCode, null);
		return message;
	}

}
