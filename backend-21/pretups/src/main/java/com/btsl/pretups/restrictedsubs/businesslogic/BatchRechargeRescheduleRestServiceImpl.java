package com.btsl.pretups.restrictedsubs.businesslogic;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.web.pretups.restrictedsubs.web.RestrictedSubscriberModel;

/**
 * @author jashobanta.mahapatra
 * All rest requests comes from Batch Re-charge Reschedule lands on this class
 */
public class BatchRechargeRescheduleRestServiceImpl extends BatchSchedulerBL implements BatchRechageRescheduleRestService{

	private Log log = LogFactory.getLog(this.getClass().getName());
	private String className = "BatchRecahgeRescheduleRestServiceImpl";

	/* (non-Javadoc)
	 * @see com.btsl.pretups.restrictedsubs.businesslogic.BatchRechageRescheduleRestService#loadScheduleBatchList(java.lang.String)
	 * 1st page submit(request data : serviceTypeCode, userID)
	   response data : scheduled batch List
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public PretupsResponse<RestrictedSubscriberModel> loadScheduleBatchList(String requestData) throws BTSLBaseException {
		String methodName = className+":loadScheduleBatchList";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		String loginId = null;
		RestrictedSubscriberModel restrictedTopUpForm = null;
		String reqType = null;
		try{
			JsonNode dataObject =  (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,	new TypeReference<JsonNode>() {});
			reqType = dataObject.get(PretupsI.TYPE).textValue();
			loginId =  (String) PretupsRestUtil.convertJSONToObject(dataObject.get(PretupsI.LOGIN_ID).toString(), new TypeReference<String>() {});
			restrictedTopUpForm =  (RestrictedSubscriberModel) PretupsRestUtil.convertJSONToObject(dataObject.get(PretupsI.DATA).toString(), new TypeReference<RestrictedSubscriberModel>() {});
		}
		catch ( IOException e) {
			log.error(methodName, e);
			throw new BTSLBaseException(e);
		}
		int numberOfDays = 0;
		try{
			numberOfDays = Integer.parseInt(Constants.getProperty("RESCHEDULE_BATCH_BACK_DAYS"));
		} catch (Exception e) {
			log.errorTrace(methodName +  "RESCHEDULE_BATCH_BACK_DAYS is not defined in constants.props file e=", e);
		}


		Connection con = null;
		MComConnectionI mcomCon = null;
		try{
			PretupsResponse<RestrictedSubscriberModel> pretupsResponse = new PretupsResponse<>();
			BatchRechargeRescheduleValidator validator = new BatchRechargeRescheduleValidator();
			if(!validator.validateLoadBatchListRequest(restrictedTopUpForm, reqType , pretupsResponse )){
				pretupsResponse.setStatus(false);
				pretupsResponse.setStatusCode(PretupsI.RESPONSE_FAIL);
				return pretupsResponse;
			}
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
			final UserDAO userDAO = new UserDAO();
			final ChannelUserVO channelUserVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			Date newDate = BTSLUtil.addDaysInUtilDate(new Date(), numberOfDays * -1);            
			//Validate userID

			List<ScheduleBatchMasterVO> scheduleList = (ArrayList<ScheduleBatchMasterVO>)scheduledBatchDetailDAO.loadScheduleBatchMasterList(con, restrictedTopUpForm.getUserID(), PretupsI.STATUS_EQUAL, PretupsI.SCHEDULE_STATUS_SCHEDULED, newDate, null, null, restrictedTopUpForm.getServiceCode(), channelUserVO.isStaffUser(), channelUserVO.getActiveUserID());
			LogFactory.printLog(methodName, "No of scheduled batch list "+scheduleList.size(), log);
			restrictedTopUpForm.setScheduleList(scheduleList);

			pretupsResponse.setDataObject(restrictedTopUpForm);
			pretupsResponse.setMessageCode(PretupsErrorCodesI.BATCH_LIST_LOADED);
			pretupsResponse.setResponse(PretupsI.RESPONSE_SUCCESS, true, "batch.reschedule.recharge.batchlist.loaded");
			LogFactory.printLog(methodName,  PretupsI.EXITED, log);
			return pretupsResponse;
		}catch (Exception e) {
			log.error(methodName, e);
			throw new BTSLBaseException(e);
		}
		finally{
			if (mcomCon != null) {
				mcomCon.close("BatchRechargeRescheduleRestServiceImpl#loadScheduleBatchList");
				mcomCon = null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.btsl.pretups.restrictedsubs.businesslogic.BatchRechageRescheduleRestService#createBatchFileForReschedule(java.lang.String)
	 * 	2nd page  : download batch file - (request :  UserID, BatchID , ServiceTypeCode, FileType) - create the batch file in the specified location send back the location
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<RestrictedSubscriberModel> createBatchFileForReschedule( String requestData) throws BTSLBaseException {
		String methodName = className+":createBatchFileForReschedule";
		LogFactory.printLog(methodName,  PretupsI.ENTERED, log);
		RestrictedSubscriberModel restrictedTopUpForm = null;
		String loginId = null;
		String reqType = null;
		try{
			JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,	new TypeReference<JsonNode>() {});
			reqType = dataObject.get(PretupsI.TYPE).textValue();
			loginId =  (String) PretupsRestUtil.convertJSONToObject(dataObject.get(PretupsI.LOGIN_ID).toString(), new TypeReference<String>() {});
			restrictedTopUpForm = (RestrictedSubscriberModel) PretupsRestUtil.convertJSONToObject(dataObject.get(PretupsI.DATA).toString(), new TypeReference<RestrictedSubscriberModel>() {});
		}
		catch(IOException ex){
			log.error(methodName, ex);
			throw new BTSLBaseException(ex);
		}
		String csvPath = null;
		PretupsResponse<RestrictedSubscriberModel> pretupsResponse = new PretupsResponse<>();
		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			BatchRechargeRescheduleValidator validator = new BatchRechargeRescheduleValidator();
			if(!validator.validateBatchFileCreateRequest(restrictedTopUpForm, reqType , pretupsResponse )){
				pretupsResponse.setStatus(false);
				pretupsResponse.setStatusCode(PretupsI.RESPONSE_FAIL);
				return pretupsResponse;
			}
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			BatchRechargeRescheduleValidator batchRechargeRescheduleValidator = new BatchRechargeRescheduleValidator();
			//validate batch id 
			if(!batchRechargeRescheduleValidator.isValidBatchID(loginId, restrictedTopUpForm.getUserID(),restrictedTopUpForm.getBatchID(), restrictedTopUpForm.getServiceCode() )){
				pretupsResponse.setStatus(false);
				pretupsResponse.setStatusCode(PretupsI.RESPONSE_FAIL);
				pretupsResponse.setMessageCode(PretupsErrorCodesI.BATCH_ID_NOT_FOUND);
				pretupsResponse.setFormError("batch.id.not.found");
				return pretupsResponse;
			}
			final RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
			// Get batch detail by batch id and create batch file
			if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(restrictedTopUpForm.getFileType())) {
				LinkedHashMap<String ,ScheduleBatchDetailVO> scheduleBatchDetailVOs = restrictedSubscriberDAO.loadScheduleBatchDetailsList(con, restrictedTopUpForm.getBatchID(), PretupsI.STATUS_EQUAL, PretupsI.SCHEDULE_STATUS_SCHEDULED);
				LogFactory.printLog(methodName, "No of MSISDNs in batch detail:"+scheduleBatchDetailVOs.size() , log);
				csvPath = createCorporateBatchCSV(scheduleBatchDetailVOs, restrictedTopUpForm.getServiceCode(), restrictedTopUpForm.getFileType());
			} else {
				//To be tested for normal user (not corporate)
				List<ScheduleBatchDetailVO> list = ( List<ScheduleBatchDetailVO>) restrictedSubscriberDAO.loadBatchDetailVOList(con, (String) restrictedTopUpForm.getBatchID(), PretupsI.STATUS_EQUAL, PretupsI.SCHEDULE_STATUS_SCHEDULED);
				LogFactory.printLog(methodName, "No of MSISDNs in batch detail:"+list.size() , log);
				csvPath = createNormalBatchCSV(list, restrictedTopUpForm.getServiceCode(), restrictedTopUpForm.getFileType());
			}
			LogFactory.printLog(methodName, "batch file created in path:"+csvPath , log);
			restrictedTopUpForm.setDownloadFilePath(csvPath);
		} catch (Exception e) {
			log.error(methodName, e);
			throw new BTSLBaseException(e);
		}
		finally{
			if (mcomCon != null) {
				mcomCon.close("BatchRechargeRescheduleRestServiceImpl#createBatchFileForReschedule");
				mcomCon = null;
			}
		}
		if(!BTSLUtil.isNullString(csvPath)){
			pretupsResponse.setDataObject(restrictedTopUpForm);
			pretupsResponse.setMessageCode(PretupsErrorCodesI.BATCH_FILE_CREATED);
			pretupsResponse.setResponse(PretupsI.RESPONSE_SUCCESS, true, "batch.reschedule.batchfile.create");
		}
		else{
			pretupsResponse.setMessageCode(PretupsErrorCodesI.BATCH_FILE_CREATE_FAILED);
			pretupsResponse.setResponse(PretupsI.RESPONSE_FAIL, false, "batch.detail.create.csv.error");
		}
		LogFactory.printLog(methodName, PretupsI.EXITED , log);
		return pretupsResponse;
	}


	//Create batch file
	/**
	 * @param batchDetailVOs
	 * @param serviceTypeCode
	 * @param fileType
	 * @return
	 * @throws BTSLBaseException
	 */
	private String createNormalBatchCSV( List<ScheduleBatchDetailVO> batchDetailVOs, String serviceTypeCode , String fileType)throws BTSLBaseException {

		File file = PretupsRestUtil.getFileForTemplate("DownloadRestrictedMSISDNFileName",  "DownloadRestrictedMSISDNFilePath");
		PretupsRestUtil.checkForLocation(file.getParent());
		Map<String, Object> dataMap = new HashMap<>();
		String[] columnsKeys = getColumnKeys(fileType);
		String[] columns = new String[columnsKeys.length];
		for (int i = 0; i < columnsKeys.length; i++) {
			columns[i] = PretupsRestUtil.getMessageString(columnsKeys[i]);
		}
		dataMap.put(PretupsI.COLUMN_HEADER_KEY, columns);
		List<String> dataList = new ArrayList<>();                        
		for (ScheduleBatchDetailVO restrictedSubscriberVO : batchDetailVOs) {
			List<String> list = new ArrayList<>();
			list.add(restrictedSubscriberVO.getMsisdn());
		/*	list.add(restrictedSubscriberVO.getSubscriberID());
			list.add(BTSLUtil.NullToString(restrictedSubscriberVO
					.getEmployeeName()));
			list.add(PretupsBL.getDisplayAmount(restrictedSubscriberVO
					.getMinTxnAmount()));
			list.add(PretupsBL.getDisplayAmount(restrictedSubscriberVO
					.getMaxTxnAmount()));
			list.add(PretupsBL.getDisplayAmount(restrictedSubscriberVO
					.getMonthlyLimit()));
			list.add(PretupsBL.getDisplayAmount(restrictedSubscriberVO
					.getTotalTransferAmount()));*/
			list.add(restrictedSubscriberVO.getSubService());
			list.add(PretupsBL.getDisplayAmount(restrictedSubscriberVO.getAmount()));
			dataList.add(StringUtils.join(list, ','));
		}
		dataMap.put(PretupsI.DATA, dataList);
		dataMap.put(PretupsI.HEADER_MESSAGE, "requested.amount.editable");
		PretupsRestUtil.writeCSVFile(dataMap, file);
		return file.getAbsolutePath();

	}	

	//Create batch file for Corporate
	/**
	 * @param scheduleBatchDetailVOs
	 * @param serviceTypeCode
	 * @param fileType
	 * @return
	 * @throws BTSLBaseException
	 */
	private String createCorporateBatchCSV(Map<String ,ScheduleBatchDetailVO> scheduleBatchDetailVOs, String serviceTypeCode , String fileType)throws BTSLBaseException {

		File file = PretupsRestUtil.getFileForTemplate("DownloadRestrictedMSISDNFileName",  "DownloadRestrictedMSISDNFilePath");
		PretupsRestUtil.checkForLocation(file.getParent());
		Map<String, Object> dataMap = new HashMap<>();
		String[] columnsKeys = getColumnKeys(fileType);
		String[] columns = new String[columnsKeys.length];
		for (int i = 0; i < columnsKeys.length; i++) {
			columns[i] = PretupsRestUtil.getMessageString(columnsKeys[i]);
		}
		dataMap.put(PretupsI.COLUMN_HEADER_KEY, columns);
		List<String> dataList = new ArrayList<>();                        
		for (Entry<String, ScheduleBatchDetailVO> entrySet : scheduleBatchDetailVOs.entrySet()) {
			ScheduleBatchDetailVO restrictedSubscriberVO = entrySet.getValue();
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
			list.add(restrictedSubscriberVO.getSubService());
			list.add(PretupsBL.getDisplayAmount(restrictedSubscriberVO.getAmount()));
			dataList.add(StringUtils.join(list, ','));
		}
		dataMap.put(PretupsI.DATA, dataList);
		dataMap.put(PretupsI.HEADER_MESSAGE, "requested.amount.editable");
		PretupsRestUtil.writeCSVFile(dataMap, file);
		return file.getAbsolutePath();

	}	

	/* (non-Javadoc)
	 * @see com.btsl.pretups.restrictedsubs.businesslogic.BatchRechageRescheduleRestService#processRescheduleBatchRecharge(java.lang.String)
	 * 2nd page submit - process batch re-schedule
	 * request data :  uploadFilePath, batchID, categoryCode,domainCode, serviceTypeCode,onOfRecords, scheduleDate, scheduleNow, requestFor, userID, frequency, iterations
	 */
	@Override
	public PretupsResponse<Object> updateAndProcessBatchRechargeReschedule(String requestData)throws BTSLBaseException {
		String methodName = className+":updateAndProcessBatchRechargeReschedule";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		RestrictedSubscriberModel restrictedTopUpForm = null;
		String loginId = null;
		String reqType;
		try{
			JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,	new TypeReference<JsonNode>() {});
			reqType = dataObject.get(PretupsI.TYPE).textValue();
			loginId =  (String) PretupsRestUtil.convertJSONToObject(dataObject.get(PretupsI.LOGIN_ID).toString(), new TypeReference<String>() {});
			restrictedTopUpForm = (RestrictedSubscriberModel) PretupsRestUtil.convertJSONToObject(dataObject.get(PretupsI.DATA).toString(), new TypeReference<RestrictedSubscriberModel>() {});
		}
		catch(IOException ex){
			LogFactory.printLog(methodName, ex.getMessage(), log);
			throw new BTSLBaseException(ex);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<Object> response = new PretupsResponse<>();
		try{
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			BatchRechargeRescheduleValidator validator = new BatchRechargeRescheduleValidator();
			if(!validator.validateProcessRescheduleBatchFileRequest(restrictedTopUpForm, reqType , response )){
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			
			//validate batch id
			if(!validator.isValidBatchID(loginId, restrictedTopUpForm.getUserID(),restrictedTopUpForm.getBatchID(), restrictedTopUpForm.getServiceCode() )){
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setMessageCode(PretupsErrorCodesI.BATCH_ID_NOT_FOUND);
				response.setFormError("batch.id.not.found");
				return response;
			}
			
			UserDAO userDAO = new UserDAO();
			UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			UserVO searchedUserVO = userDAO.loadUserDetailsFormUserID(con, restrictedTopUpForm.getUserID());
			//update old batch status as cancel in Schedule_detail and Schedule_Master table, refer new batch id in master 
			response = cancelOldBatch(response, con , restrictedTopUpForm,userVO,searchedUserVO );
			//process new batch 
			response = processBatchRecharge(con , response, restrictedTopUpForm , userVO,searchedUserVO );
		
			if(response.hasFormError()){
				response.setStatus( false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
			}
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
			return response;
		} catch (Exception e) {
			OracleUtil.rollbackConnection(con, BatchRechargeRescheduleRestServiceImpl.class.getName(), methodName);
			LogFactory.printLog(methodName, e.getMessage(), log);
			throw new BTSLBaseException(e);
		}finally{
			if (mcomCon != null) {
				mcomCon.close("BatchRechargeRescheduleRestServiceImpl#updateAndProcessBatchRechargeReschedule");
				mcomCon = null;
			}
		}
	}


	/**
	 * @param response
	 * @param connection
	 * @param restrictedTopUpForm
	 * @param channelUserVO
	 * @param searchedUserVO
	 * @throws BTSLBaseException 
	 */
	@SuppressWarnings("unchecked")
	private PretupsResponse<Object> cancelOldBatch(PretupsResponse<Object> response, Connection connection, RestrictedSubscriberModel restrictedTopUpForm,UserVO channelUserVO,UserVO searchedUserVO ) throws BTSLBaseException {
		String methodName = className+":cancelOldBatch";
		LogFactory.printLog(methodName,  PretupsI.ENTERED, log);
		int numberOfDays = 0;
		try{
			numberOfDays = Integer.parseInt(Constants.getProperty("RESCHEDULE_BATCH_BACK_DAYS"));
		} catch (Exception e) {
			log.errorTrace(methodName +  "RESCHEDULE_BATCH_BACK_DAYS is not defined in constants.props file e=", e);
		}
		ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
		try {
			Date newDate = BTSLUtil.addDaysInUtilDate(new Date(), numberOfDays * -1);         
			List<ScheduleBatchMasterVO> scheduleList = (ArrayList<ScheduleBatchMasterVO>)scheduledBatchDetailDAO.loadScheduleBatchMasterList(connection, restrictedTopUpForm.getUserID(), PretupsI.STATUS_EQUAL, PretupsI.SCHEDULE_STATUS_SCHEDULED, newDate, null, null, restrictedTopUpForm.getServiceCode(), channelUserVO.isStaffUser(), channelUserVO.getActiveUserID());
			// Extracting the selected batchVO form the list
			ScheduleBatchMasterVO scheduleMasterVO = null;
			for (int i = 0, j = scheduleList.size(); i < j; i++) {
				scheduleMasterVO = scheduleList.get(i);
				if (scheduleMasterVO.getBatchID().equals(restrictedTopUpForm.getBatchID())) {
					restrictedTopUpForm.setFileType(scheduleMasterVO.getBatchType());
					break;
				}
			}
			Date curDate = new Date();
			restrictedTopUpForm.setCreatedBy(channelUserVO.getUserID());
			restrictedTopUpForm.setCreatedOn(curDate);
			restrictedTopUpForm.setModifiedBy(channelUserVO.getUserID());
			restrictedTopUpForm.setModifiedOn(curDate);
			// set the information in the selected batchVO for the canceling
			// the batch
			scheduleMasterVO.setModifiedBy(channelUserVO.getUserID());
			scheduleMasterVO.setModifiedOn(curDate);
			scheduleMasterVO.setStatus(PretupsI.SCHEDULE_STATUS_CANCELED);

			// Constructing the new BatchVO for the creation of the new
			// batch on the canceling of the old batch
			// here the batchID of the new batch will be the
			// referenceBatchID of the old batch and the batchID of the
			// old batch will be the referenceBatchID of the new batch.
			ScheduleBatchMasterVO scheduleMasterVONew = new ScheduleBatchMasterVO();
			scheduleMasterVONew.setStatus(PretupsI.SCHEDULE_STATUS_SCHEDULED);
			scheduleMasterVONew.setScheduledDate(BTSLUtil.getDateFromDateString(restrictedTopUpForm.getScheduleDate()));
			scheduleMasterVONew.setCreatedBy(channelUserVO.getUserID());
			scheduleMasterVONew.setCreatedOn(curDate);
			scheduleMasterVONew.setModifiedBy(channelUserVO.getUserID());
			scheduleMasterVONew.setModifiedOn(curDate);
			scheduleMasterVONew.setInitiatedBy(channelUserVO.getUserID());
			scheduleMasterVONew.setNetworkCode(channelUserVO.getNetworkID());
			scheduleMasterVONew.setParentCategory(restrictedTopUpForm.getCategoryCode());
			scheduleMasterVONew.setParentDomain(restrictedTopUpForm.getDomainCode());
			scheduleMasterVONew.setParentID(restrictedTopUpForm.getUserID());
			scheduleMasterVONew.setOwnerID(searchedUserVO.getOwnerID());
			scheduleMasterVONew.setServiceType(restrictedTopUpForm.getServiceCode());
			scheduleMasterVONew.setTotalCount(restrictedTopUpForm.getNoOfRecords());
			scheduleMasterVONew.setActiveUserId(scheduleMasterVO.getActiveUserId());
			scheduleMasterVONew.setFrequency(restrictedTopUpForm.getFrequencyCode());
			scheduleMasterVONew.setIterations(restrictedTopUpForm.getIterations());

			// Generation of the new BatchID
			RestrictedSubscriberBL.generateScheduleBatchID(scheduleMasterVONew);

			// seting the batchID and the ReferenceBatchID
			scheduleMasterVO.setRefBatchID(scheduleMasterVONew.getBatchID());
			scheduleMasterVONew.setRefBatchID(scheduleMasterVO.getBatchID());
			scheduleMasterVONew.setBatchType(scheduleMasterVO.getBatchType());

			// Construction of the new detail vo for the caneling of the
			// deatils of the old batch for this
			// we are setting the batchID of the old batch
			ScheduleBatchDetailVO scheduleDetailVO = new ScheduleBatchDetailVO();
			scheduleDetailVO.setModifiedBy(channelUserVO.getUserID());
			scheduleDetailVO.setModifiedOn(curDate);
			scheduleDetailVO.setStatus(PretupsI.SCHEDULE_STATUS_CANCELED);
			scheduleDetailVO.setBatchID(scheduleMasterVO.getBatchID());

			// creating the single line logger for the indication of the
			// starting of the processing
			ScheduleFileProcessLog.log("Re-schedule File Processing START", restrictedTopUpForm.getCreatedBy(), null, restrictedTopUpForm.getBatchID(), "FILE = " + restrictedTopUpForm.getUploadFilePath() + "PROCESSING START", "START", /*"TYPE=" + restrictedTopUpForm.getRequestFor()*/null);

			// Canceling the details of the selected batch form the details
			int count = scheduledBatchDetailDAO.updateScheduleStatus(connection, scheduleDetailVO);
			if (count <= 0) {
				LogFactory.printLog(methodName,  "Schedule Details can not be updated", log);
				connection.rollback();
				response.setFormError("restrictedsubs.rescheduletopupdetails.msg.unsuccess");
				return response;
			}             

			// Canceling the Batch master information form the master table
			// here we have to update the cancel count of the batch
			scheduleMasterVO.setCancelledCount(scheduleMasterVO.getCancelledCount() + count);
			count = scheduledBatchDetailDAO.updateScheduleBatchMasterStatus(connection, scheduleMasterVO);
			if (count <= 0) {
				LogFactory.printLog(methodName,  "Schedule Master can not be updated", log);
				connection.rollback();
				response.setMessageCode(PretupsErrorCodesI.ERROR_WHILE_CANCELLING_OLD_BATCH);
				response.setFormError("restrictedsubs.rescheduletopupdetails.msg.unsuccess");
				return response;
			}

			// Adding the informaion of the new Batch
			count = scheduledBatchDetailDAO.addScheduleBatchMaster(connection, scheduleMasterVONew);
			if (count <= 0) {
				LogFactory.printLog(methodName,  "Schedule Master can not be added", log);
				connection.rollback();
				response.setMessageCode(PretupsErrorCodesI.ERROR_WHILE_CANCELLING_OLD_BATCH);
				response.setFormError("restrictedsubs.scheduletopupdetails.msg.unsuccess");
				return response;
			}
			// Connection will be commited in the processFile method
			restrictedTopUpForm.setScheduleBatchMasterVO(scheduleMasterVONew);
		}
		catch (Exception e) {
			log.error(methodName, e);
			//if error occurred rollback , close connection
			OracleUtil.rollbackConnection(connection, BatchRechargeRescheduleRestServiceImpl.class.getName(), methodName);
			throw new BTSLBaseException(e);
		}
		//Note : Don't close Connection in finally in this method as same connection object need to be used further
		
		LogFactory.printLog(methodName,  PretupsI.EXITED, log);
		return response;
	}
}
