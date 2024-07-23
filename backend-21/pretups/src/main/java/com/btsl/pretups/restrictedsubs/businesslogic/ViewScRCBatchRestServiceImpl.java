package com.btsl.pretups.restrictedsubs.businesslogic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.validator.ValidatorException;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.web.pretups.restrictedsubs.web.RestrictedSubscriberModel;

public class ViewScRCBatchRestServiceImpl implements ViewScRCBatchRestService {

	public static final Log _log = LogFactory.getLog(ViewScRCBatchRestServiceImpl.class.getName());
	public static final String STATUSIN="statusin";
	public static final String STATUS="status";
	
	/**
	 * This method loads loadScheduleBatchMasterList
	 * @param requestdata
	 * @return String response
	 * @throws BTSLBaseException,IOException, SQLException, ValidatorException, SAXException, ParseException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<ScheduleBatchMasterVO>> viewSCRCBatch(String requestData)
			throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException, ParseException {
		final String methodName = "viewSCRCBatch";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List<ScheduleBatchMasterVO>> response = new PretupsResponse<>();
		try{
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
		JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
		JsonNode dataNode =  requestNode.get("data");
		Date fromScheduleDate = new Date();
		Date toScheduleDate= new Date();
		PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
		UserVO userVO = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(dataNode, con);
		
		//server side validation
		RestrictedSubscriberModel restSubsModel = (RestrictedSubscriberModel) PretupsRestUtil
				.convertJSONToObject(requestNode.get("data").toString(), new TypeReference<RestrictedSubscriberModel>() {
				});
		ScheduleTopupValidator scheduleValidator = new ScheduleTopupValidator();
		if(restSubsModel.getScheduleFromDate()==null&&restSubsModel.getScheduleToDate()==null){
			scheduleValidator.validateRequestData("VIEWSCHRCBATCH", response, restSubsModel, "CancelSignleRecharge");
		}
		
		else{
		
		scheduleValidator.validateRequestData("VIEWSCHRCBATCH", response, restSubsModel, "ViewSchRechargeBatch");
		}
		
		if("E".equalsIgnoreCase(dataNode.get(STATUS).textValue())){
			Date toDate = BTSLUtil.getDateFromDateString(restSubsModel.getScheduleToDate());
			Date curruntDate  = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(new Date(), ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT))));
			if(toDate.after(curruntDate)){
				response.setFieldError("scheduleToDate", "for.status.execute.to.date.can.not.be.future.date");
			}
		}
		if (response.hasFieldError()) {
			response.setStatus(false);
			response.setStatusCode(PretupsI.RESPONSE_FAIL);
			return response;
		}
		//validating user details
		scheduleValidator.validateUserDetails( response,userVO,restSubsModel);
		if (response.hasFormError()) {
			response.setStatus(false);
			response.setStatusCode(PretupsI.RESPONSE_FAIL);
			return response;
		}
		
		ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
		if(dataNode.get("scheduleFromDate").textValue()!=null){
		 fromScheduleDate=BTSLUtil.getDateFromDateString(dataNode.get("scheduleFromDate").textValue());
		}
		else{
			fromScheduleDate=null;
		}
		if(dataNode.get("scheduleToDate").textValue()!=null){
		 toScheduleDate=BTSLUtil.getDateFromDateString(dataNode.get("scheduleToDate").textValue());
		}
		else{
			toScheduleDate=null;
		}
		 //validating date
		if(fromScheduleDate!=null && toScheduleDate!=null)
		{
			scheduleValidator.validateDateDifference(fromScheduleDate,toScheduleDate,response);
			if(response.hasFormError())
			{
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
				
			}
		}
		
		//status check
		if(!(("'" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "'").equalsIgnoreCase(dataNode.get(STATUS).textValue()) ||("'"+PretupsI.SCHEDULE_STATUS_CANCELED+"'").equalsIgnoreCase(dataNode.get(STATUS).textValue()) || ("'"+ PretupsI.SCHEDULE_STATUS_EXECUTED+"'").equalsIgnoreCase(dataNode.get(STATUS).textValue())))
		{
			response.setFormError("restrictedsubs.scheduletopupdetails.error.msg.schedule.status");
			response.setMessageCode(PretupsErrorCodesI.INVALID_SCHEDULE_STATUS);
			response.setStatus(false);
			response.setStatusCode(PretupsI.RESPONSE_FAIL);
			return response;
		}
		
		 List<ScheduleBatchMasterVO> list = scheduledBatchDetailDAO.loadScheduleBatchMasterList(con,userVO.getUserID() ,dataNode.get(STATUSIN).textValue(), dataNode.get(STATUS).textValue(), null, fromScheduleDate, toScheduleDate, null, userVO.isStaffUser(), userVO.getActiveUserID());
		
		if(list.isEmpty())
		{
			response.setFormError("error.no.schedule.data.found");
			response.setMessageCode(PretupsErrorCodesI.NO_SCHEDULE_FOUND);
			response.setStatus(false);
			response.setStatusCode(PretupsI.RESPONSE_FAIL);
			return response;
			
		}
		response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,list);
		}
		finally{
			if (mcomCon != null) {
				mcomCon.close("ViewScRCBatchRestServiceImpl#viewSCRCBatch");
				mcomCon = null;
			}
		}
		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		return response;
	
	}
	
	/**
	 * This method loads loadScheduleBatchDetailsList
	 * @param requestdata
	 * @return String response
	 * @throws BTSLBaseException,IOException, SQLException, ValidatorException, SAXException, ParseException
	 */
	@Override
	public PretupsResponse<LinkedHashMap> loadScheduleBatchDetailsList(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException, ParseException
	{
		final String methodName = "loadScheduleBatchDetailsList";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<LinkedHashMap> response = new PretupsResponse<>();
		try{
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
		JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
		JsonNode dataNode =  requestNode.get("data");
		
		RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
		
		LinkedHashMap map= restrictedSubscriberDAO.loadScheduleBatchDetailsList(con,dataNode.get("batchID").textValue() ,dataNode.get(STATUSIN).textValue(), dataNode.get(STATUS).textValue());
		
		if(map.isEmpty())
		{
			response.setStatus(false);
			response.setStatusCode(PretupsI.RESPONSE_FAIL);
			response.setFormError("There is no Schedule Found");
			response.setMessageCode(PretupsErrorCodesI.NO_SCHEDULE_FOUND);
			return response;
			
		}
		response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,map);
		}
		finally{
			if (mcomCon != null) {
				mcomCon.close("ViewScRCBatchRestServiceImpl#loadScheduleBatchDetailsList");
				mcomCon = null;
			}
		}
		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		return response;
		
	}
	
	
	/**
	 * This method loads loadScheduleBatchDetailsMap
	 * @param requestdata
	 * @return String response
	 * @throws BTSLBaseException,IOException, SQLException, ValidatorException, SAXException, ParseException
	 */
	@Override
	public PretupsResponse<LinkedHashMap> loadScheduleBatchDetailsMap(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException, ParseException
	{
		final String methodName = "loadScheduleBatchDetailsMap";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<LinkedHashMap> response = new PretupsResponse<>();
		try{
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
		JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
		JsonNode dataNode =  requestNode.get("data");
		
		RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
		
		LinkedHashMap map= restrictedSubscriberDAO.loadScheduleBatchDetailsMap(con,dataNode.get("batchID").textValue() ,dataNode.get(STATUSIN).textValue(), dataNode.get(STATUS).textValue());
		
		if(map.isEmpty())
		{
			response.setStatus(false);
			response.setStatusCode(PretupsI.RESPONSE_FAIL);
			response.setFormError("There is no Schedule Found");
			response.setMessageCode(PretupsErrorCodesI.NO_SCHEDULE_FOUND);
			return response;
			
		}
		response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,map);
		}
		finally{
			if (mcomCon != null) {
				mcomCon.close("ViewScRCBatchRestServiceImpl#loadScheduleBatchDetailsMap");
				mcomCon = null;
			}
			}
		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		return response;
	}
	
	
	/**
	 * This method loads lookup for schedule status
	 * @param requestdata
	 * @return String response
	 * @throws BTSLBaseException,IOException, SQLException, ValidatorException, SAXException, ParseException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<ListValueVO>> loaddropdownforViewSCRBatch(String requestData) throws BTSLBaseException
	{
		final String methodName = "loaddropdownforViewSCRBatch";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		try{
		PretupsResponse<List<ListValueVO>> response = new PretupsResponse<>();
		JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
		JsonNode dataNode =  requestNode.get("data");
		if(!dataNode.has("lookupType") || !dataNode.has("active") || dataNode.size() == 0){
			 response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, new ArrayList<ListValueVO>());
			 return response;
		}
		
		List<ListValueVO> list = LookupsCache.loadLookupDropDown(dataNode.get("lookupType").textValue(), dataNode.get("active").asBoolean());
		List<ListValueVO> newlist = new ArrayList<>();
		ListValueVO listValueVO;
		if(!list.isEmpty())
		{
			int listSize = list.size();
			  for (int i = 0; i < listSize; i++) {
                  listValueVO = list.get(i);
                  if (listValueVO.getValue().equals(PretupsI.SCHEDULE_STATUS_CANCELED) || listValueVO.getValue().equals(PretupsI.SCHEDULE_STATUS_EXECUTED) || listValueVO.getValue().equals(PretupsI.SCHEDULE_STATUS_SCHEDULED)) 
                	  newlist.add(listValueVO);          
			  }
		}
		response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,newlist);
		return response;
		}catch(Exception e)
		{
			throw new BTSLBaseException(e);
		}
		
		finally
		{
			LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		}
		
		
	}
	
	
	

}
