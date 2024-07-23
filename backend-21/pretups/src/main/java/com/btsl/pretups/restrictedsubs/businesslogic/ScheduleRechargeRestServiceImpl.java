package com.btsl.pretups.restrictedsubs.businesslogic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.validator.ValidatorException;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.web.pretups.restrictedsubs.businesslogic.RestrictedSubscriberWebDAO;
import com.web.pretups.restrictedsubs.web.RestrictedSubscriberModel;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

public class ScheduleRechargeRestServiceImpl implements
		ScheduleRechargeRestService {

	@Override
	public PretupsResponse<RestrictedSubscriberModel> viewCancelScehduleSubs(
			String requestData) throws BTSLBaseException, IOException,
			SQLException, ValidatorException, SAXException {
		ArrayList listMaster;
		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			JsonNode dataObject = (JsonNode) PretupsRestUtil
					.convertJSONToObject(requestData,
							new TypeReference<JsonNode>() {
							});
			PretupsResponse<RestrictedSubscriberModel> response = new PretupsResponse<>();
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			UserVO sessionUserVO = pretupsRestUtil
					.getUserVOByLoginIdOrExternalCode(dataObject.get("data"),
							con);

			RestrictedSubscriberModel restrictedSubscriberModel = (RestrictedSubscriberModel) PretupsRestUtil
					.convertJSONToObject(dataObject.get("data").toString(),
							new TypeReference<RestrictedSubscriberModel>() {
							});
			ScheduleTopupValidator scheduleTopupValidator = new ScheduleTopupValidator();
			scheduleTopupValidator.validateRequestData(dataObject.get("type")
					.textValue(), response, restrictedSubscriberModel,
					"CancelSignleRechargeViewBatchDetail");
			if (response.hasFieldError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			
			

			String status = "'" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "'";

			listMaster = new ScheduledBatchDetailDAO()
					.loadScheduleBatchMasterList(con,
							sessionUserVO.getUserID(), PretupsI.STATUS_IN,
							status, null, null, null, null,
							sessionUserVO.isStaffUser(),
							sessionUserVO.getActiveUserID());

			if (!listMaster.isEmpty()) {
				restrictedSubscriberModel.setScheduleList(listMaster);
			} else {
				response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,
						restrictedSubscriberModel);
				return response;
			}
			ScheduleBatchMasterVO scheduleBatchMasterVO;
			for (int i = 0; i < restrictedSubscriberModel.getScheduleListSize(); i++) {
				scheduleBatchMasterVO = restrictedSubscriberModel
						.getScheduleList().get(i);
				if (scheduleBatchMasterVO.getBatchID().equals(
						restrictedSubscriberModel.getBatchID())) {
					restrictedSubscriberModel
							.setScheduleBatchMasterVO(scheduleBatchMasterVO);
					break;
				}
			}
			String status1 = "'" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "','"
					+ PretupsI.SCHEDULE_STATUS_CANCELED + "','"
					+ PretupsI.SCHEDULE_STATUS_EXECUTED + "','"
					+ PretupsI.SCHEDULE_STATUS_UNDERPROCESSED + "'";
			try {
				restrictedSubscriberModel
						.setScheduleDetailList(new RestrictedSubscriberDAO()
								.loadBatchDetailVOList(con,
										restrictedSubscriberModel.getBatchID(),
										PretupsI.STATUS_IN, status1));
			} catch (Exception e) {
				throw new BTSLBaseException(e);
			}

			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,
					restrictedSubscriberModel);
			return response;
		} catch (BTSLBaseException | IOException | SQLException e) {
			throw new BTSLBaseException(e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ScheduleRechargeRestServiceImpl#viewCancelScehduleSubs");
				mcomCon = null;
			}
		}
	}

	@Override
	public PretupsResponse<RestrictedSubscriberModel> loadDetailsForSingle(
			String requestData) throws BTSLBaseException, IOException,
			SQLException, ValidatorException, SAXException {
		Connection con = null;
		MComConnectionI mcomCon = null;
		JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(
				requestData, new TypeReference<JsonNode>() {
				});
		PretupsResponse<RestrictedSubscriberModel> response = new PretupsResponse<>();
		PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			UserVO sessionUserVO = pretupsRestUtil
					.getUserVOByLoginIdOrExternalCode(dataObject.get("data"),
							con);

			RestrictedSubscriberModel restrictedSubscriberModel = (RestrictedSubscriberModel) PretupsRestUtil
					.convertJSONToObject(dataObject.get("data").toString(),
							new TypeReference<RestrictedSubscriberModel>() {
							});

			ScheduleTopupValidator scheduleTopupValidator = new ScheduleTopupValidator();
			scheduleTopupValidator.validateRequestData(dataObject.get("type")
					.textValue(), response, restrictedSubscriberModel,
					"CancelSignleRechargeBatchDetail");
			if (response.hasFieldError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			restrictedSubscriberModel.setScheduleMasterVOList(null);
			restrictedSubscriberModel.setMobileNumbers(BTSLUtil.NullToString(
					restrictedSubscriberModel.getMobileNumbers()).trim());
			// Code for textArea Validation start, added by Ashish S dated as
			// 12-07-2007
			if (!BTSLUtil.isNullString(restrictedSubscriberModel
					.getMobileNumbers())
					&& restrictedSubscriberModel.getMobileNumbers().length() > 500) {
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setFormError("restrictedsubs.cancel.mobilenumbers.maxlength");
				response.setMessageCode(PretupsErrorCodesI.MOBILE_NUMBER_MAX_LENGTH);
				return response;
			}
			// Code for textArea Validation end
			String[] msisdnSeriesArr = restrictedSubscriberModel
					.getMobileNumbers().split(",");
			StringBuilder msisdnSeriesStrBuff = new StringBuilder("'");
			String filteredMsisdn;
			NetworkPrefixVO networkPrefixVO;
			String msisdnPrefix;
			String arr[] = new String[1];
			int endIndex = msisdnSeriesArr.length;
			// validate the mobile number with network
			for (int index = 0; index < endIndex; index++) {
				if (!BTSLUtil.isNullString(msisdnSeriesArr[index])) {
					msisdnSeriesArr[index] = msisdnSeriesArr[index].trim();
					// Change ID=ACCOUNTID
					// FilteredMSISDN is replaced by
					// getFilteredIdentificationNumber
					// This is done because this field can contains msisdn or
					// account id
					filteredMsisdn = PretupsBL
							.getFilteredIdentificationNumber(msisdnSeriesArr[index]);
					arr[0] = msisdnSeriesArr[index];
					if(filteredMsisdn.length()<((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LENGTH_CODE))).intValue()){
						response.setStatusCode(PretupsI.RESPONSE_FAIL);
						response.setFormError(
								"restrictedsubs.msisdn.prefix.length",
								arr);
						response.setMessageCode(PretupsErrorCodesI.MSISDN_PREFIX_LENGTH);
						return response;
					}
					msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
					
					networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache
							.getObject(msisdnPrefix);
					
					// if no network found for the msisdn.
					if (networkPrefixVO == null) {
						response.setStatusCode(PretupsI.RESPONSE_FAIL);
						response.setFormError(
								"restrictedsubs.displaydetailsforcancelsinglesub.msg.nonetworkfound",
								arr);
						response.setMessageCode(PretupsErrorCodesI.NO_NETWORK_FOUND_FOR_MSISDN);
						return response;
					}
					// If network is different from the login network.
					if (!networkPrefixVO.getNetworkCode().equals(
							sessionUserVO.getNetworkID())) {
						response.setStatusCode(PretupsI.RESPONSE_FAIL);
						response.setFormError(
								"restrictedsubs.displaydetailsforcancelsinglesub.msg.networknotsupported",
								arr);
						response.setMessageCode(PretupsErrorCodesI.MOBILE_NUMBER_NOT_FROM_SUPPORTED_NETWORK);
						return response;
					}
					msisdnSeriesStrBuff.append(filteredMsisdn);
					msisdnSeriesStrBuff.append("','");
				}
			}
			// remove the comma from the end
			String msisdnSeriesStr = msisdnSeriesStrBuff.substring(0,
					msisdnSeriesStrBuff.length() - 2);
			ArrayList scheduleMasterVOList;
			RestrictedSubscriberWebDAO restrictedSubscriberWebDAO = new RestrictedSubscriberWebDAO();
			boolean isRestricted = false;
			ScheduleBatchMasterVO scheduleBatchMasterVO = null;
			String status = "'" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "'";
			ArrayList listMaster;
			listMaster = new ScheduledBatchDetailDAO()
					.loadScheduleBatchMasterList(con,
							sessionUserVO.getUserID(), PretupsI.STATUS_IN,
							status, null, null, null, null,
							sessionUserVO.isStaffUser(),
							sessionUserVO.getActiveUserID());
			restrictedSubscriberModel.setScheduleList(listMaster);
			for (int i = 0; i < restrictedSubscriberModel.getScheduleListSize(); i++) {
				scheduleBatchMasterVO = restrictedSubscriberModel
						.getScheduleList().get(i);
				if (scheduleBatchMasterVO.getBatchID().equals(
						restrictedSubscriberModel.getBatchID())) {
					restrictedSubscriberModel.setFileType(scheduleBatchMasterVO
							.getBatchType());
					if (PretupsI.BATCH_TYPE_CORPORATE
							.equalsIgnoreCase(scheduleBatchMasterVO
									.getBatchType())) {
						isRestricted = true;
					}
					break;
				}
			}
			scheduleMasterVOList = restrictedSubscriberWebDAO
					.loadDetailsForCancelSingle(con,
							PretupsI.SCHEDULE_STATUS_SCHEDULED,
							msisdnSeriesStr,
							restrictedSubscriberModel.getBatchID(),
							isRestricted, sessionUserVO.getUserID());
			if (scheduleMasterVOList != null && !scheduleMasterVOList.isEmpty()) {

				ScheduleBatchDetailVO scheduleBatchDetailVO;
				ScheduleBatchMasterVO scheduleMasterVO;
				ArrayList scheduleDetailVOList;
				String invalidNumbers = "";
				int outerEnd = msisdnSeriesArr.length;
				int innerEnd = scheduleMasterVOList.size();
				boolean found = false;
				for (int index = 0; index < outerEnd; index++) {
					for (int i = 0; i < innerEnd; i++) {
						scheduleMasterVO = (ScheduleBatchMasterVO) scheduleMasterVOList
								.get(i);
						scheduleDetailVOList = scheduleMasterVO.getList();
						scheduleBatchDetailVO = (ScheduleBatchDetailVO) scheduleDetailVOList
								.get(i);
						if (msisdnSeriesArr[index].trim().equals(
								scheduleBatchDetailVO.getMsisdn())) {
							found = true;
							break;
						}
					}
					if (!found) {
						invalidNumbers += msisdnSeriesArr[index] + ",";
					}
					found = false;
				}
				if (!BTSLUtil.isNullString(invalidNumbers)) {
					invalidNumbers = invalidNumbers.substring(0,
							invalidNumbers.length() - 1);
					String[] arr1 = new String[1];
					arr1[0] = invalidNumbers;
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					response.setFormError(
							"restrictedsubs.displaydetailsforcancelsinglesub.msg.numbernotscheduled",
							arr1);
					response.setMessageCode(PretupsErrorCodesI.MOBILE_NUMBER_NOT_SCHEDULED);
					return response;
				}
				restrictedSubscriberModel
						.setScheduleMasterVOList(scheduleMasterVOList);
				scheduleMasterVO = (ScheduleBatchMasterVO) scheduleMasterVOList
						.get(0);

				restrictedSubscriberModel.setScheduleDate(BTSLUtil
						.getDateStringFromDate(scheduleMasterVO
								.getScheduledDate()));

				scheduleDetailVOList = scheduleMasterVO.getList();
				if (scheduleDetailVOList != null
						&& !scheduleDetailVOList.isEmpty()) {
					restrictedSubscriberModel
							.setScheduleMasterVOList(scheduleDetailVOList);
					if (scheduleDetailVOList.get(0) != null) {
						restrictedSubscriberModel
								.setScheduleStatus(((ScheduleBatchDetailVO) scheduleDetailVOList
										.get(0)).getStatus());
					}
				}
			} else {
				String[] arr1 = new String[1];
				arr1[0] = restrictedSubscriberModel.getMobileNumbers();
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setFormError(
						"restrictedsubs.displaydetailsforcancelsinglesub.msg.numbernotscheduled",
						arr1);
				response.setMessageCode(PretupsErrorCodesI.MOBILE_NUMBER_NOT_SCHEDULED);
				return response;
			}
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,
					restrictedSubscriberModel);
		} catch (BTSLBaseException | IOException | SQLException
				| ParseException |StringIndexOutOfBoundsException e) {
			throw new BTSLBaseException(e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ScheduleRechargeRestServiceImpl#loadDetailsForSingle");
				mcomCon = null;
			}
		}

		return response;
	}

	@Override
	public PretupsResponse<RestrictedSubscriberModel> deleteDetailsForSelectedMsisdn(
			String requestData) throws BTSLBaseException {
		ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList listMaster;
		boolean isRestricted = false;
		String msisdnSeriesStr;
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			RestrictedSubscriberWebDAO restrictedSubscriberWebDAO = new RestrictedSubscriberWebDAO();
			JsonNode dataObject = (JsonNode) PretupsRestUtil
					.convertJSONToObject(requestData,
							new TypeReference<JsonNode>() {
							});
			PretupsResponse<RestrictedSubscriberModel> response = new PretupsResponse<>();
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();

			UserVO userVO = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(
					dataObject.get("data"), con);

			RestrictedSubscriberModel restrictedSubscriberModel = (RestrictedSubscriberModel) PretupsRestUtil
					.convertJSONToObject(dataObject.get("data").toString(),
							new TypeReference<RestrictedSubscriberModel>() {
							});
			StringBuilder mobileStr = new StringBuilder();
			for(int i=0;i<restrictedSubscriberModel.getChecklist().size();i++){
				if(mobileStr.length()==0){
				mobileStr.append(restrictedSubscriberModel.getChecklist().get(i));
				}
				else{
					mobileStr.append(",");
					mobileStr.append(restrictedSubscriberModel.getChecklist().get(i));
				}
			}
			restrictedSubscriberModel.setMobileNumbers(mobileStr.toString());
			ScheduleTopupValidator scheduleTopupValidator = new ScheduleTopupValidator();
			scheduleTopupValidator.validateRequestData(dataObject.get("type")
					.textValue(), response, restrictedSubscriberModel,
					"CancelSignleRechargeDeleteMsisdnDetail");
			if (response.hasFieldError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			

			ScheduleBatchMasterVO scheduleBatchMasterVO = null;
			String status = "'" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "'";
			listMaster = new ScheduledBatchDetailDAO()
					.loadScheduleBatchMasterList(con,
							userVO.getUserID(), PretupsI.STATUS_IN, status,
							null, null, null, null, userVO.isStaffUser(),
							userVO.getActiveUserID());
			if (!listMaster.isEmpty()) {
				restrictedSubscriberModel.setScheduleList(listMaster);
			}
			for (int i = 0; i < restrictedSubscriberModel.getScheduleListSize(); i++) {
				scheduleBatchMasterVO = restrictedSubscriberModel
						.getScheduleList().get(i);
				if (scheduleBatchMasterVO.getBatchID().equals(
						restrictedSubscriberModel.getBatchID())) {
					restrictedSubscriberModel
							.setScheduleBatchMasterVO(scheduleBatchMasterVO);
					break;
				}
			}
			if (PretupsI.BATCH_TYPE_CORPORATE
					.equalsIgnoreCase(scheduleBatchMasterVO.getBatchType())) {
				isRestricted = true;
			}
			List<ScheduleBatchMasterVO> scheduleMasterVOList;
			Date curDate = new Date();
			StringBuilder msisdnSeriesStrBuff = new StringBuilder("'");
			int totaldeleteSize = dataObject.get("data").get("checklist")
					.size();
			for (int i = 0; i < totaldeleteSize; i++) {
				msisdnSeriesStrBuff.append(dataObject.get("data")
						.get("checklist").get(i).textValue()
						);
				msisdnSeriesStrBuff.append("','");
			}
			msisdnSeriesStr = msisdnSeriesStrBuff.substring(0,
					msisdnSeriesStrBuff.length() - 2);

			scheduleMasterVOList = restrictedSubscriberWebDAO
					.loadDetailsForCancelSingle(con,
							PretupsI.SCHEDULE_STATUS_SCHEDULED,
							msisdnSeriesStr,
							restrictedSubscriberModel.getBatchID(),
							isRestricted, userVO.getUserID());

			if(scheduleMasterVOList.isEmpty()){
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setFormError(
						"restrictedsubs.viewtransferschedulebatch.label.noscheuled"
						);
				response.setMessageCode(PretupsErrorCodesI.NO_SCHEDULE_FOUND);
				return response;
				
			}
				
			restrictedSubscriberModel.setDeleteList(scheduleMasterVOList.get(0)
					.getList());
			ArrayList<String> unprocessed = new ArrayList<String>();
			int updateCount = scheduledBatchDetailDAO
					.updateScheduleRest(
							con,
							(ArrayList<ScheduleBatchDetailVO>) restrictedSubscriberModel
									.getDeleteList(), userVO.getUserID(),
							curDate, restrictedSubscriberModel
									.getScheduleBatchMasterVO()
									.getCancelledCount(),
							restrictedSubscriberModel
									.getScheduleBatchMasterVO()
									.getNoOfRecords(), unprocessed);
			if (!unprocessed.isEmpty()) {
				// data not processed
			}
			if (updateCount <= 0) {
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setFormError(
						"restrictedsubs.displaydetailsforcancelsinglesub.msg.unsuccess"
						);
				response.setMessageCode(PretupsErrorCodesI.SCHEDULE_CAN_NOT_CANCEL);
				return response;
				
			}
			mcomCon.finalCommit();
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,
					restrictedSubscriberModel);
			return response;
		} catch (BTSLBaseException | IOException | SQLException e) {
			throw new BTSLBaseException(e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ScheduleRechargeRestServiceImpl#deleteDetailsForSelectedMsisdn");
				mcomCon = null;
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<UserVO>> loadUsersBatchRecharge(
			String requestData) throws BTSLBaseException, IOException,
			SQLException, ValidatorException, SAXException {
		Connection con = null;
		MComConnectionI mcomCon = null;
		JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(
				requestData, new TypeReference<JsonNode>() {
				});
		PretupsResponse<List<UserVO>> response = new PretupsResponse<>();
		PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
		ChannelUserWebDAO channelUserWebDAO = null;
		List<UserVO> userList;
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			UserVO sessionUserVO = pretupsRestUtil
					.getUserVOByLoginIdOrExternalCode(dataObject.get("data"),
							con);
			RestrictedSubscriberModel restrictedSubscriberModel = (RestrictedSubscriberModel) PretupsRestUtil
					.convertJSONToObject(dataObject.get("data").toString(),
							new TypeReference<RestrictedSubscriberModel>() {
							});
			channelUserWebDAO = new ChannelUserWebDAO();
			restrictedSubscriberModel.setUserID(null);
			restrictedSubscriberModel.setUserList(null);

			restrictedSubscriberModel.setUserListSize(0);
			String userName = restrictedSubscriberModel.getUserName();

			if (BTSLUtil.isNullString(userName)) {

			}
			userName = "%" + userName + "%";
			String geoDomain = null;

			if (sessionUserVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
				userList = channelUserWebDAO.loadChannelUserListHierarchy(
						con,
						restrictedSubscriberModel.getCategoryCode(),
						restrictedSubscriberModel.getDomainCode(),
						restrictedSubscriberModel.getGeoDomainCode(), userName,
						sessionUserVO.getUserID());
			} else {
				userList = channelUserWebDAO.loadChannelUserList(con,
						restrictedSubscriberModel.getCategoryCode(),
						restrictedSubscriberModel.getDomainCode(),
						restrictedSubscriberModel.getGeoDomainCode(), userName,
						sessionUserVO.getUserID());
			}

			if (userList.isEmpty()) {
				response.setResponse(PretupsI.RESPONSE_FAIL, false,
						"no.user.found");
			} else {
				response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, userList);
			}
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ScheduleRechargeRestServiceImpl#loadUsersBatchRecharge");
				mcomCon = null;
			}
		}

		return response;
	}

	@Override
	public PretupsResponse<RestrictedSubscriberModel> cancelSelectedBatch(String requestData)
			throws BTSLBaseException, IOException, SQLException,
			ValidatorException, SAXException {
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList listMaster;
		ScheduleBatchMasterVO scheduleMasterVO = new ScheduleBatchMasterVO();
		ScheduleBatchDetailVO scheduleDetailVO = new ScheduleBatchDetailVO();
		ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
		int cancelCount = 0;
		int j,m=0,k=0;
		int updateCountDetail = 0;
		int updateCountMaster = 0;
		boolean found = false;
		String arr[] = new String[1];
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			RestrictedSubscriberWebDAO restrictedSubscriberWebDAO = new RestrictedSubscriberWebDAO();
			JsonNode dataObject = (JsonNode) PretupsRestUtil
					.convertJSONToObject(requestData,
							new TypeReference<JsonNode>() {
							});
			PretupsResponse<RestrictedSubscriberModel> response = new PretupsResponse<>();
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();

			UserVO userVO = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(
					dataObject.get("data"), con);

			RestrictedSubscriberModel restrictedSubscriberModel = (RestrictedSubscriberModel) PretupsRestUtil
					.convertJSONToObject(dataObject.get("data").toString(),
							new TypeReference<RestrictedSubscriberModel>() {
							});
			 boolean modified = false;
			 ScheduleTopupValidator scheduleTopupValidator = new ScheduleTopupValidator();
				scheduleTopupValidator.validateRequestData(dataObject.get("type")
						.textValue(), response, restrictedSubscriberModel,
						"CancelSignleRechargeDeleteBatchDetail");
				if (response.hasFieldError()) {
					response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					return response;
				}
			 final Date currentDate = new java.util.Date();
			 j = restrictedSubscriberModel.getChecklist().size();
			 String status = "'" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "'";

				listMaster = new ScheduledBatchDetailDAO()
						.loadScheduleBatchMasterList(con,
								userVO.getUserID(), PretupsI.STATUS_IN,
								status, null, null, null, null,
								userVO.isStaffUser(),
								userVO.getActiveUserID());
				StringBuilder batchIDs = new StringBuilder();

				if (!listMaster.isEmpty()) {
					restrictedSubscriberModel.setScheduleList(listMaster);
				} else {
					response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,
							restrictedSubscriberModel);
					return response;
				}
			 for (int i = 0; i < j; i++) {
	                cancelCount = 0;
	                for(k=0;k<restrictedSubscriberModel.getScheduleList().size();k++){
	                	for(m=0;m<restrictedSubscriberModel.getChecklist().size();m++){
	                   if(restrictedSubscriberModel.getScheduleList().get(k).getBatchID().equals(restrictedSubscriberModel.getChecklist().get(m))){
	                	   if(batchIDs.length()==0){
	                		   batchIDs.append(restrictedSubscriberModel.getChecklist().get(m));
	           				}
	           				else{
	           					batchIDs.append(",");
	           					batchIDs.append(restrictedSubscriberModel.getChecklist().get(m));
	           				}
	                	restrictedSubscriberModel.getChecklist().remove(m);
	                	found = true;
	                	break;
	                   }
	                  }
	                	if(found){
	                		break;
	                	}
	                }
	                found = false;
	                scheduleMasterVO = restrictedSubscriberModel.getScheduleList().get(k);
	                scheduleDetailVO.setBatchID(scheduleMasterVO.getBatchID());
	                scheduleDetailVO.setModifiedBy(userVO.getActiveUserID());
	                scheduleDetailVO.setModifiedOn(currentDate);
	                scheduleDetailVO.setStatus(PretupsI.SCHEDULE_STATUS_CANCELED);
	                modified = scheduledBatchDetailDAO.isScheduleBatchMasterModified(con, scheduleMasterVO.getLastModifiedTime(), scheduleMasterVO.getBatchID());
	                if (modified) {
	                   
	                }
	                cancelCount = scheduledBatchDetailDAO.updateScheduleStatus(con, scheduleDetailVO);
	                updateCountDetail = updateCountDetail + cancelCount;
	                scheduleMasterVO.setCancelledCount(cancelCount + scheduleMasterVO.getCancelledCount());
	                scheduleMasterVO.setModifiedOn(currentDate);
	                scheduleMasterVO.setModifiedBy(userVO.getActiveUserID());
	                scheduleMasterVO.setStatus(PretupsI.SCHEDULE_STATUS_CANCELED);
	                updateCountMaster = updateCountMaster + scheduledBatchDetailDAO.updateScheduleBatchMasterStatus(con, scheduleMasterVO);
	                
		
	    }
			 if (updateCountMaster > 0)// give the successs message
	            {
				 mcomCon.finalCommit();
				 restrictedSubscriberModel.setBatchID(batchIDs.toString());
				  response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, restrictedSubscriberModel); 
				  return response;
	            }
			 else{
				 mcomCon.finalRollback();
				 arr[0] =batchIDs.toString();
				 response.setFormError("restrictedsubs.web.viewscheduleaction.cancelconfirmschedule.batchid.fail.msg",arr );
				 response.setMessageCode(PretupsErrorCodesI.UNABLE_TO_CANCEL);
				 return response;
	            }
	}
		finally {
			if (mcomCon != null) {
				mcomCon.close("ScheduleRechargeRestServiceImpl#cancelSelectedBatch");
				mcomCon = null;
			}
		}

}
}
