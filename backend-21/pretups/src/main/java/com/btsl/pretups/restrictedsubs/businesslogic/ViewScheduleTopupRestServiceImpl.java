package com.btsl.pretups.restrictedsubs.businesslogic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.ValidatorException;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.web.pretups.restrictedsubs.web.RestrictedSubscriberModel;

public class ViewScheduleTopupRestServiceImpl implements ViewScheduleTopupRestService {

	public static final Log log = LogFactory.getLog(ViewScheduleTopupRestServiceImpl.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<Object>> viewScheduledTopup(String requestData)
			throws BTSLBaseException, IOException, SQLException,
			ValidatorException, SAXException {
		final String methodName = "viewScheduledTopup";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List<Object>> response = new PretupsResponse<List<Object>>();
		ArrayList list = new ArrayList();
		try{
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,new TypeReference<JsonNode>() {});

			LogFactory.printLog(methodName, "DATA ="+dataObject, log);
			if(dataObject.has(PretupsI.DATA)){
				PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
				
				RestrictedSubscriberModel restSubsModel = (RestrictedSubscriberModel) PretupsRestUtil
						.convertJSONToObject(dataObject.get("data").toString(), new TypeReference<RestrictedSubscriberModel>() {
						});
				
				ScheduleTopupValidator scheduleValidator = new ScheduleTopupValidator();
				//server side validation
				scheduleValidator.validateRequestData(dataObject.get("type").textValue(), response, restSubsModel, "ViewScheduleTopUpList");
				if (response.hasFieldError()) {
					response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					return response;
				}
				UserVO userVO = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(dataObject.get("data"), con);
				scheduleValidator.validateUserDetails( response, userVO, restSubsModel); //	validating if user's details match with given information
				if (response.hasFieldError()) {
					response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					return response;
				}
				//Validating msisdn provided in request
				String msisdn = restSubsModel.getMsisdn();
				ScheduleTopupValidator msisdnValidator = new ScheduleTopupValidator();
				msisdnValidator.validateMsisdn(msisdn, userVO, response );
				if (response.hasFieldError()) {
					response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					return response;
				}
				ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
				String status = "'" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "','" + PretupsI.SCHEDULE_STATUS_EXECUTED + "','" + PretupsI.SCHEDULE_STATUS_CANCELED + "'";
	            list = scheduledBatchDetailDAO.loadScheduleDetailVOList(con, userVO.getOwnerID(), userVO.getUserID(), msisdn, PretupsI.STATUS_IN, status, userVO.isStaffUser(), userVO.getActiveUserID());
				if(list.isEmpty()){
					response.setFormError("restrictedsubs.viewsingletrfschedule.msg.noschedule");
					response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					response.setMessageCode(PretupsErrorCodesI.SCHEDULED_LIST_NOT_FOUND);
					return response;
					
			        }
				response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, list);
				
			}
			else{
				response.setResponse(PretupsI.RESPONSE_FAIL, false, "incorrect.request");
	
			}
		}
		finally
		{
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
			if (mcomCon != null) {
				mcomCon.close("ViewScheduleTopupRestServiceImpl#viewScheduledTopup");
				mcomCon = null;
			}
		}
			
		return response;  
	}
}
