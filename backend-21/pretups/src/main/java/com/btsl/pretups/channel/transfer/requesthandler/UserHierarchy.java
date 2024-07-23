package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.UserHierarchyRequestMessage;
import com.btsl.pretups.channel.transfer.businesslogic.UserHierarchyVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

public class UserHierarchy implements ServiceKeywordControllerI {

	protected final Log _log = LogFactory.getLog(getClass().getName());
	Connection con = null;
	MComConnectionI mcomCon = null;

	private ArrayList<UserHierarchyVO> fetchUpwardHierarchy(UserHierarchyVO userHierarchyVO) throws BTSLBaseException, Exception {

		final String methodName = "fetchUpwardHierarchy";
		UserDAO userDao = new UserDAO();
		ArrayList<UserHierarchyVO> userHierarchyVOList = null;
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			userHierarchyVOList = userDao.fetchUserHierarchy(con,
					userHierarchyVO.getMsisdn(), userHierarchyVO.getLoginId());
		} catch (Exception e) {
			throw new BTSLBaseException(this, methodName, "Could not create Database connection", "Exception " + e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2CVoucherApprovalController#saveVoucherProductDetalis");
				mcomCon = null;
			}
		}
		return userHierarchyVOList;

	}

	
	
	
	
	@Override
	public void process(RequestVO p_requestVO) {
		
		
		final String methodName = "process";
		
		Gson gson = new Gson();
		PretupsResponse<JsonNode> jsonReponse = new PretupsResponse<JsonNode>();;
		JsonNode dataObject = null;
		HashMap responseMap = new HashMap();
		HashMap reqMap = p_requestVO.getRequestMap();
		UserHierarchyVO userHierarchyVO = new UserHierarchyVO();
		UserHierarchyRequestMessage reqMsgObj = null;
		ArrayList<UserHierarchyVO> userHirerachyRes = null;
		StringBuffer responseStr = new StringBuffer("");
		
		
		try {
			
			if (reqMap != null && (reqMap.get("MSISDN") != null || reqMap.get("LOGINID") != null) && p_requestVO.getRequestMessage() != null  &&  !p_requestVO.getRequestMessage().trim().startsWith("{")) {
				userHierarchyVO.setMsisdn((String) reqMap.get("MSISDN"));
				userHierarchyVO.setLoginId((String) reqMap.get("LOGINID"));

				
				userHirerachyRes = fetchUpwardHierarchy(userHierarchyVO);
				
				if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {
					responseStr.append("[");
				}
				for (UserHierarchyVO resObj : userHirerachyRes) {
					
					if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {

						responseStr.append("{ \"msisdn\": \"" + resObj.getMsisdn() + "\" ,");
						responseStr.append("\"firstname\":   \"" + resObj.getFirstName() + "\" , ");
						responseStr.append(" \"lastname\":    \"" + resObj.getLastName() + "\" , ");
						responseStr.append(" \"category_code\":   \"" + resObj.getCategoryCode() + "\" , ");
						responseStr.append(" \"category_name\":   \"" + resObj.getCategoryName() + "\" , ");
						responseStr.append("   \"loginId\":  \"" + resObj.getLoginId() + "\" },");

						
					} else {
						responseStr.append("<USERDETAIL>");
						responseStr.append("<MSISDN>" + resObj.getMsisdn() + "</MSISDN>");
						responseStr.append("<FIRSTNAME>" + resObj.getFirstName() + "</FIRSTNAME>");
						responseStr.append("<LASTNAME>" + resObj.getLastName() + "</LASTNAME>");
						responseStr.append("<CATEGORY_CODE>" + resObj.getCategoryCode() + "</CATEGORY_CODE>");
						responseStr.append("<CATEGORY_NAME>" + resObj.getCategoryName() + "</CATEGORY_NAME>");
						responseStr.append("<LOGINID>" + resObj.getLoginId() + "</LOGINID>");
						responseStr.append("</USERDETAIL>");
					}
					
				}
				if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {
					if(responseStr != null && responseStr.toString().trim().equalsIgnoreCase("[")) {
						
					}else {
					responseStr.deleteCharAt(responseStr.length()-1);
					}
					responseStr.append("]");
				}
				_log.debug("response ", "User Hierarchy responseStr  " + responseStr);

				responseMap.put("RESPONSE", responseStr);

				p_requestVO.setResponseMap(responseMap);

			}else {
				 reqMsgObj = gson.fromJson(p_requestVO.getRequestMessage(), UserHierarchyRequestMessage.class);
				userHierarchyVO.setMsisdn(reqMsgObj.getMsisdn());
				userHierarchyVO.setLoginId(reqMsgObj.getLoginid());
				userHirerachyRes = fetchUpwardHierarchy(userHierarchyVO);
				dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(
						PretupsRestUtil.convertObjectToJSONString(userHirerachyRes), new TypeReference<JsonNode>() {
						});

				jsonReponse.setDataObject(dataObject);

				p_requestVO.setJsonReponse(jsonReponse);

			}
			
			p_requestVO.setSuccessTxn(true);
			p_requestVO.setMessageCode(PretupsErrorCodesI.USER_HIERRACHY_SUCCESS);
			p_requestVO.setSenderReturnMessage("Transaction has been completed!");

			
		} catch (BTSLBaseException be) {
			p_requestVO.setSuccessTxn(false);
			p_requestVO.setSenderReturnMessage("Transaction has been failed!");

			if ("REST".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {

				try {

					userHirerachyRes = new ArrayList<UserHierarchyVO>();

					UserHierarchyVO userHierarchyVOErr = new UserHierarchyVO();

					userHierarchyVOErr.setFirstName("");
					userHirerachyRes.add(userHierarchyVOErr);

					dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(
							PretupsRestUtil.convertObjectToJSONString(userHirerachyRes), new TypeReference<JsonNode>() {
							});
				} catch (Exception e) {
					_log.errorTrace(methodName, e);
				}

				jsonReponse.setDataObject(dataObject);

				p_requestVO.setJsonReponse(jsonReponse);

			} else if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {
				responseStr.append("Exception occured - " + be);
				responseMap.put("RESPONSE", responseStr);

				p_requestVO.setResponseMap(responseMap);

			} else {

				responseStr.append("<USERDETAIL>Exception occured - " + be + "</USERDETAIL>");
				responseMap.put("RESPONSE", responseStr);

				p_requestVO.setResponseMap(responseMap);

			}
			
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			}

			catch (SQLException esql) {
				_log.error(methodName, "SQLException : ", esql.getMessage());
			}
			_log.error("process", "BTSLBaseException " + be.getMessage());
			if (be.getMessageList() != null && be.getMessageList().size() > 0) {
				final String[] array = {
						BTSLUtil.getMessage(p_requestVO.getLocale(), (ArrayList) be.getMessageList()) };
				p_requestVO.setMessageArguments(array);
			}
			if (be.getArgs() != null) {
				p_requestVO.setMessageArguments(be.getArgs());
			}

			if (be.getMessageKey() != null) {
				p_requestVO.setMessageCode(be.getMessageKey());
			} else {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
			}
			_log.errorTrace(methodName, be);
			return;

		} catch (Exception e) {

			
			
			if ("REST".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {

				try {

					userHirerachyRes = new ArrayList<UserHierarchyVO>();

					UserHierarchyVO userHierarchyVOErr = new UserHierarchyVO();

					userHierarchyVOErr.setFirstName("");
					userHirerachyRes.add(userHierarchyVOErr);

					dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(
							PretupsRestUtil.convertObjectToJSONString(userHirerachyRes), new TypeReference<JsonNode>() {
							});
				} catch (Exception e2) {
					_log.errorTrace(methodName, e2);
				}

				jsonReponse.setDataObject(dataObject);

				p_requestVO.setJsonReponse(jsonReponse);

			} else if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {
				responseStr.append("Exception occured - " + e);
				responseMap.put("RESPONSE", responseStr);

				p_requestVO.setResponseMap(responseMap);

			} else {

				responseStr.append("<USERDETAIL>Exception occured - " + e + "</USERDETAIL>");
				responseMap.put("RESPONSE", responseStr);

				p_requestVO.setResponseMap(responseMap);

			}

			
			p_requestVO.setSenderReturnMessage("Transaction has been failed!");
			p_requestVO.setSuccessTxn(false);

			_log.error(methodName, "Exception " + e);
			_log.errorTrace(methodName, e);
			p_requestVO.setSuccessTxn(false);
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			}

			catch (SQLException esql) {
				_log.error(methodName, "SQLException : ", esql.getMessage());
			}
			_log.error("process", "BTSLBaseException " + e.getMessage());
			_log.errorTrace(methodName, e);
			
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);

			return;
		}

	}

}
