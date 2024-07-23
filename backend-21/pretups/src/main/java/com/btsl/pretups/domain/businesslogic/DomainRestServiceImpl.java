package com.btsl.pretups.domain.businesslogic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.SqlParameterEncoder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.web.pretups.domain.businesslogic.DomainWebDAO;

/**
 * This class implements DomainRestService and provide implementation of domain
 * management methods R
 * 
 * @author lalit.chattar
 * @param <T>
 *
 */
public class DomainRestServiceImpl implements DomainRestService {

	private static final Log log = LogFactory
			.getLog(GeographicalDomainRestServiceImpl.class.getName());

	private static String className = "DomainRestServiceImpl";

	/**
	 * This method return domain details
	 * 
	 * @param requestData
	 *            Request data
	 * @return List of Domain
	 */
	@Override
	public PretupsResponse<List<Object>> loadDomainDetails(String requestData)
			throws BTSLBaseException {
		final String methodName = className + ": loadDomainDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		requestData = SqlParameterEncoder.encodeParams(requestData);
		Connection con = null;
		MComConnectionI mcomCon = null;
		try{
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			JsonNode jsonNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
			PretupsResponse<List<Object>> pretupsResponse = new PretupsResponse<>();
			
			DomainValidator domainValidator = new DomainValidator();
			domainValidator.validateRequestDataForDomain(jsonNode, pretupsResponse);
			
			if(pretupsResponse.hasFormError()){
				return pretupsResponse;
			}
			
			return getDomainList(jsonNode, pretupsResponse, con);
				

		} catch (SQLException | IOException e) {
			throw new BTSLBaseException(e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("DomainRestServiceImpl#loadDomainDetails");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
	}

	private PretupsResponse<List<Object>> getDomainList(JsonNode jsonNode, PretupsResponse<List<Object>> pretupsResponse, Connection connection) throws BTSLBaseException{
		if(jsonNode.has(PretupsI.DATA) && jsonNode.get(PretupsI.DATA).has(PretupsI.LOGIN_ID)){
			return getDomainsByUserID(jsonNode, pretupsResponse, connection);
		}else if(jsonNode.has(PretupsI.DATA) && jsonNode.get(PretupsI.DATA).has(PretupsI.BY_USER_TYPE)){
			return getDomainsByOperatorUserType(jsonNode, pretupsResponse, connection);
		}else if(jsonNode.has(PretupsI.DATA) && jsonNode.get(PretupsI.DATA).has(PretupsI.IS_SCHEDULED)){
			return getDomainIfScheduled(jsonNode, pretupsResponse, connection);
		}
		else{
			return getDomains(pretupsResponse, connection);
		}
		
	}

	
	@SuppressWarnings("unchecked")
	private PretupsResponse<List<Object>> getDomainIfScheduled(	JsonNode jsonNode, PretupsResponse<List<Object>> pretupsResponse, Connection connection) throws BTSLBaseException {
		final String methodName = className + ": getDomainIfScheduled";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		try {
			DomainWebDAO domainWebDAO = new DomainWebDAO();
	        List<Object> domainList= domainWebDAO.loadRestrictedMsisdnsDomainList(connection, jsonNode.get("domainList").textValue(), jsonNode.get(PretupsI.IS_SCHEDULED).asBoolean(), jsonNode.get(PretupsI.USER_ID).textValue());
			if(domainList.isEmpty()){
				pretupsResponse.setResponse(PretupsI.RESPONSE_FAIL, false, "");
			}else{
				pretupsResponse.setDataObject(PretupsI.RESPONSE_SUCCESS, true, domainList);
			}
		} catch (BTSLBaseException e) {
			throw e;
		}finally{
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
		return pretupsResponse;
	}

	@SuppressWarnings("unchecked")
	private PretupsResponse<List<Object>> getDomains(PretupsResponse<List<Object>> pretupsResponse, Connection connection) throws BTSLBaseException {
		final String methodName = className + ": getDomains";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		try {
			DomainDAO dao = new DomainDAO();
			List<Object> list = dao.loadDomainDetails(connection);
			if(list.isEmpty()){
				pretupsResponse.setResponse(PretupsI.RESPONSE_FAIL, false, "");
			}else{
				pretupsResponse.setDataObject(PretupsI.RESPONSE_SUCCESS, true, list);
			}
		} catch (BTSLBaseException e) {
			throw e;
		}finally{
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
		return pretupsResponse;
	}

	@SuppressWarnings("unchecked")
	private <T> PretupsResponse<List<T>> getDomainsByOperatorUserType(JsonNode jsonNode, PretupsResponse<List<T>> pretupsResponse, Connection connection) throws BTSLBaseException {
		final String methodName = className + ": getDomainsByOperatorUserType";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		try {
			DomainDAO dao = new DomainDAO();
			List<T> list = dao.loadDomainList(connection, jsonNode.get(PretupsI.DATA).get(PretupsI.BY_USER_TYPE).textValue());
			if(list.isEmpty()){
				pretupsResponse.setResponse(PretupsI.RESPONSE_FAIL, false, "");
			}else{
				pretupsResponse.setDataObject(PretupsI.RESPONSE_SUCCESS, true, list);
			}
			return pretupsResponse;
		} catch (BTSLBaseException e) {
			throw e;
		}finally{
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private PretupsResponse<List<Object>> getDomainsByUserID(JsonNode jsonNode, PretupsResponse<List<Object>> pretupsResponse, Connection connection) throws BTSLBaseException {
		final String methodName = className + ": getDomainsByUserID";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		try {
			DomainDAO dao = new DomainDAO();
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			UserVO userVO = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(jsonNode.get(PretupsI.DATA), connection); 
			List<Object> list = dao.loadDomainListByUserId(connection, userVO.getUserID());
			if(list.isEmpty()){
				pretupsResponse.setResponse(PretupsI.RESPONSE_FAIL, false, "");
			}else{
				pretupsResponse.setDataObject(PretupsI.RESPONSE_SUCCESS, true, list);
			}
			return pretupsResponse;
		} catch (BTSLBaseException | SQLException e) {
			throw new BTSLBaseException(e);
		}finally{
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
	}

	

}
