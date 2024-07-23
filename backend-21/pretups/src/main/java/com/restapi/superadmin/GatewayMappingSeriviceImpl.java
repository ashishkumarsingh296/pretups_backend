package com.restapi.superadmin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayMappingVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.RequestGatewayVO;
import com.btsl.pretups.gateway.businesslogic.ResponseGatewayVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.gateway.businesslogic.MessageGatewayWebDAO;

@Service("GatewayMappingService")
public class GatewayMappingSeriviceImpl implements GatewayMappingService {

	public static final Log LOG = LogFactory.getLog(GatewayMappingSeriviceImpl.class.getName());
	public static final String classname = "GatewayMappingSeriviceImpl";

	@Override
	public GatewayMappingResponseVO viewGatewayList(Connection con, HttpServletResponse responseSwag)
			throws BTSLBaseException, SQLException {

		final String METHOD_NAME = "viewGatewayList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		GatewayMappingResponseVO response = new GatewayMappingResponseVO();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		ArrayList arrRequestGatewayList = null;
		ArrayList arrResponseGatewayList = null;
		MessageGatewayWebDAO messageGatewaywebDAO = new MessageGatewayWebDAO();

		try {

			arrRequestGatewayList = messageGatewaywebDAO.loadRequestGateway(con);
			arrResponseGatewayList = messageGatewaywebDAO.loadResponseGateway(con);
			if (BTSLUtil.isNullOrEmptyList(arrRequestGatewayList)
					|| BTSLUtil.isNullOrEmptyList(arrResponseGatewayList)) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.GATE_FAIL, 0, null);
			}
			response.setRequestGatewayList(arrRequestGatewayList);
			response.setResponseGatewayList(arrResponseGatewayList);
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GATE_SUCCESS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.GATE_SUCCESS);

		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.GATE_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.GATE_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	@Override
	public BaseResponse ModifyGatewayAdmin(Connection con,UserVO userVO, GatewayMappingRequestVO requestVO,
			HttpServletResponse responseSwag) throws BTSLBaseException, SQLException {

		final String METHOD_NAME = "ModifyGatewayAdmin";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		BaseResponse response = new BaseResponse();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		Date currentDate = new Date();

		int updateCount = -1;
		MessageGatewayWebDAO messageGatewaywebDAO = new MessageGatewayWebDAO();

		try {

			Integer size = requestVO.getGatewayList().size();
			ArrayList newGatewayList = new ArrayList();

			for (int i = 0; i < size; i++) {
				MessageGatewayMappingVO messageGatewayMappingVO = new MessageGatewayMappingVO();
				messageGatewayMappingVO
						.setRequestGatewayCode(requestVO.getGatewayList().get(i).get_requestGatewayCode());
				messageGatewayMappingVO
						.setResponseGatewayCode(requestVO.getGatewayList().get(i).get_responseGatewayCode());
				if (BTSLUtil.isNullorEmpty(requestVO.getGatewayList().get(i).get_altresponseGatewayCode())) {
					messageGatewayMappingVO.setAltresponseGatewayCode(null);

				} else {
					messageGatewayMappingVO
							.setAltresponseGatewayCode(requestVO.getGatewayList().get(i).get_altresponseGatewayCode());
				}
				messageGatewayMappingVO.setModifiedOn(currentDate);
				messageGatewayMappingVO.setModifyFlag(requestVO.getGatewayList().get(i).getModifyFlag());
				newGatewayList.add(messageGatewayMappingVO);

			}

			updateCount = messageGatewaywebDAO.saveGatewayMapping(con, newGatewayList);
			if (updateCount > 0) {
				con.commit();
				String msgInfo = RestAPIStringParser.getMessage(locale,
						PretupsErrorCodesI.MESSAGE_GATEWAY_HAS_MODIFIED_SUCCESSFULLY, null);
				AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setSource(TypesI.LOGGER_MESSAGE_GATEWAY_SOURCE);
                adminOperationVO.setDate(currentDate);
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
                adminOperationVO.setInfo(msgInfo);
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);

				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GATE_UP_SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.GATE_UP_SUCCESS);
			} else {
				con.rollback();
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.GATE_UP_FAIL, 0, null);
			}
		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.GATE_UP_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.GATE_UP_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	public BaseResponse deleteGatewayAdmin(Connection con,UserVO userVO, GatewayMappingRequestVO requestVO,
			HttpServletResponse responseSwag) {
		final String METHOD_NAME = "deleteGatewayAdmin";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		BaseResponse response = new BaseResponse();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		Date currentDate = new Date();

		int deleteCount = -1;
		MessageGatewayWebDAO messageGatewaywebDAO = new MessageGatewayWebDAO();

		try {

			Integer size = requestVO.getGatewayList().size();
			ArrayList newGatewayList = new ArrayList();

			for (int i = 0; i < size; i++) {
				MessageGatewayMappingVO messageGatewayMappingVO = new MessageGatewayMappingVO();
				messageGatewayMappingVO
						.setRequestGatewayCode(requestVO.getGatewayList().get(i).get_requestGatewayCode());
				messageGatewayMappingVO
						.setResponseGatewayCode(requestVO.getGatewayList().get(i).get_responseGatewayCode());
				if (BTSLUtil.isNullorEmpty(requestVO.getGatewayList().get(i).get_altresponseGatewayCode())) {
					messageGatewayMappingVO.setAltresponseGatewayCode(null);

				} else {
					messageGatewayMappingVO
							.setAltresponseGatewayCode(requestVO.getGatewayList().get(i).get_altresponseGatewayCode());
				}
				messageGatewayMappingVO.setModifiedOn(currentDate);
				messageGatewayMappingVO.setModifyFlag("true");
				newGatewayList.add(messageGatewayMappingVO);

			}

			deleteCount = messageGatewaywebDAO.deleteMapping(con, newGatewayList);
			if (deleteCount > 0) {
				con.commit();
				String msgInfo = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MESSAGE_GATEWAY_HAS_DELETED_SUCCESSFULLY, null);
				AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setSource(TypesI.LOGGER_MESSAGE_GATEWAY_SOURCE);
                adminOperationVO.setDate(currentDate);
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
                adminOperationVO.setInfo(msgInfo);
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);

				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GATE_DEL_SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.GATE_DEL_SUCCESS);
			} else {
				con.rollback();
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.GATE_DEL_FAIL, 0, null);
			}
		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.GATE_DEL_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.GATE_DEL_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	public MessageGatewayResponseVO viewMessageGatewayList(Connection con, String loginID,
			HttpServletResponse responseSwag) {

		final String METHOD_NAME = "viewMessageGatewayList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		MessageGatewayResponseVO response = new MessageGatewayResponseVO();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		MessageGatewayWebDAO messageGatewaywebDAO = new MessageGatewayWebDAO();
		ArrayList messageGatewayList = null;

		try {

			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			messageGatewayList = messageGatewaywebDAO.loadMessageGatewayList(con, userVO.getNetworkID());
			if (BTSLUtil.isNullOrEmptyList(messageGatewayList)) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.GATE_MSG_FAIL, 0, null);
			}

			else {
				response.setStatus((HttpStatus.SC_OK));
				response.setMessageGatewayList(messageGatewayList);
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GATE_MSG_SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.GATE_MSG_SUCCESS);
			}
		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.GATE_MSG_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.GATE_MSG_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;

	}

	public BaseResponse deleteMessageGatweway(Connection con,UserVO userVO, String gatewayCode, String loginID,
			HttpServletResponse responseSwag) {

		final String METHOD_NAME = "deleteMessageGatweway";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		BaseResponse response = new BaseResponse();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		
		MessageGatewayWebDAO messageGatewaywebDAO = null;
		MessageGatewayVO messageGatewayVO = new MessageGatewayVO();
		Date currentDate = new Date();
		int deleteCount = 0;

		try {

		
			messageGatewaywebDAO = new MessageGatewayWebDAO();

			if (messageGatewaywebDAO.isMessageGatewayMappingExist(con, gatewayCode, gatewayCode)) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.GATE__MSG_DEL_FAIL_MAP, 0, null);
			} else if (messageGatewaywebDAO.isRequestMessageGatewayExist(con, gatewayCode, PretupsI.STATUS_ACTIVE)) {

				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.GATE__MSG_DEL_FAIL_RMAP, 0,
						null);
			} else if (messageGatewaywebDAO.isResponseMessageGatewayExist(con, gatewayCode, PretupsI.STATUS_ACTIVE)) {

				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.GATE__MSG_DEL_FAIL_ROMAP, 0,
						null);

			} else {
				messageGatewayVO.setStatus(PretupsI.STATUS_DELETE);
				messageGatewayVO.setGatewayCode(gatewayCode);
				messageGatewayVO.setModifiedOn(currentDate);
				messageGatewayVO.setModifiedBy(userVO.getUserID());

				deleteCount = messageGatewaywebDAO.deleteMessageGateway(con, messageGatewayVO);
			}
			if (con != null) {
				if (deleteCount > 0) {
					con.commit();
					String msgInfo = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MESSAGE_GATEWAY_HAS_DELETED_SUCCESSFULLY, null);
					AdminOperationVO adminOperationVO = new AdminOperationVO();
	                adminOperationVO.setSource(TypesI.LOGGER_MESSAGE_GATEWAY_SOURCE);
	                adminOperationVO.setDate(currentDate);
	                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
	                adminOperationVO.setInfo(msgInfo);
	                adminOperationVO.setLoginID(userVO.getLoginID());
	                adminOperationVO.setUserID(userVO.getUserID());
	                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
	                adminOperationVO.setNetworkCode(userVO.getNetworkID());
	                adminOperationVO.setMsisdn(userVO.getMsisdn());
	                AdminOperationLog.log(adminOperationVO);

					response.setStatus((HttpStatus.SC_OK));
					String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GATE_MSG_DEL_SUCCESS, null);
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.GATE_MSG_DEL_SUCCESS);

				} else {
					con.rollback();
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.GATE__MSG_DEL_FAIL, 0, null);
				}
			}

		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.GATE__MSG_DEL_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.GATE__MSG_DEL_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	public MessageModifyResponseVO ModifyMessageGatweway(Connection con, String gatewayCode,
			HttpServletResponse responseSwag) {

		final String METHOD_NAME = "ModifyMessageGatweway";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		MessageModifyResponseVO response = new MessageModifyResponseVO();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		MessageGatewayWebDAO messageGatewaywebDAO = new MessageGatewayWebDAO();
		RequestGatewayVO requestGatewayVO = new RequestGatewayVO();
		ResponseGatewayVO responseGatewayVO = new ResponseGatewayVO();

		try {
			requestGatewayVO = messageGatewaywebDAO.loadRequestMessageGateway(con, gatewayCode);
			responseGatewayVO = messageGatewaywebDAO.loadResponseMessageGateway(con, gatewayCode);
			String password = null;
			ArrayList requestList = new ArrayList();
			ArrayList responseList = new ArrayList();
			if (!BTSLUtil.isNullObject(requestGatewayVO)) {
				if (!BTSLUtil.isNullString(requestGatewayVO.getPassword())) {
					password = BTSLUtil.getDefaultPasswordText(requestGatewayVO.getPassword());
				}

				if (!BTSLUtil.isNullString(requestGatewayVO.getPassword())) {
					requestGatewayVO.setPassword(password);
					requestGatewayVO.setConfirmPassword(password);
				}

			}
			if (!BTSLUtil.isNullObject(responseGatewayVO)) {
				if (!BTSLUtil.isNullString(responseGatewayVO.getPassword())) {
					password = BTSLUtil.getDefaultPasswordText(responseGatewayVO.getPassword());
				}
				if (!BTSLUtil.isNullString(responseGatewayVO.getPassword())) {

					responseGatewayVO.setPassword(password);
					responseGatewayVO.setConfirmPassword(password);
				}
			}
			requestList.add(requestGatewayVO);
			responseList.add(responseGatewayVO);

			if (BTSLUtil.isNullorEmpty(requestGatewayVO)) {
				response.setRequestGatewayList(null);
			}

			else {
				response.setRequestGatewayList(requestList);
			}
			if (BTSLUtil.isNullorEmpty(responseGatewayVO)) {
				response.setResponseGatewayList(null);
			} else {
				response.setResponseGatewayList(responseList);
			}

			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GATE_MSG_SUCCESS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.MSG_SUCCESS);
		}

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.MSG_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.MSG_FAIL);
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

}
