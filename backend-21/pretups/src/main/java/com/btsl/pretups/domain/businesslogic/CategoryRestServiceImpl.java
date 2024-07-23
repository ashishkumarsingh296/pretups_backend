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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferRuleWebDAO;

/**
 * 
 * @author lalit.chattar
 *
 */
public class CategoryRestServiceImpl implements CategoryRestService {
	
	private static final Log log = LogFactory
			.getLog(CategoryRestServiceImpl.class.getName());

	private static String className = "CategoryRestServiceImpl";

	@Override
	public PretupsResponse<List<Object>> loadCategoryDetails(String requestData)	throws BTSLBaseException {
		final String methodName = className + ": loadCategoryDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		try{
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			JsonNode jsonNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
			PretupsResponse<List<Object>> pretupsResponse = new PretupsResponse<>();
			
			//Validate 
			
			if(pretupsResponse.hasFormError()){
				return pretupsResponse;
			}
			
			return getCategory(jsonNode, pretupsResponse, con);
				

		} catch (SQLException | IOException e) {
			throw new BTSLBaseException(e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("CategoryRestServiceImpl#loadCategoryDetails");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
		
	}

	private PretupsResponse<List<Object>> getCategory(JsonNode jsonNode, PretupsResponse<List<Object>> pretupsResponse, Connection connection) throws BTSLBaseException {
		if(jsonNode.has(PretupsI.DATA) && jsonNode.get(PretupsI.DATA).has(PretupsI.IS_RESTRICTED)){
			return getCategoryForRestrictedMSISDN(jsonNode, pretupsResponse, connection);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private PretupsResponse<List<Object>> getCategoryForRestrictedMSISDN(JsonNode jsonNode, PretupsResponse<List<Object>> pretupsResponse, Connection connection) throws BTSLBaseException {
		final String methodName = className + ": getDomainsByUserID";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		try {
			ChannelTransferRuleWebDAO dao = new ChannelTransferRuleWebDAO();
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			UserVO userVO = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(jsonNode.get(PretupsI.DATA), connection); 
			List<Object> list = dao.loadTrfRuleCatListForRestrictedMsisdn(connection, userVO.getNetworkID(), userVO.getCategoryCode(), jsonNode.get(PretupsI.DATA).get(PretupsI.OWNER_ONLY).asBoolean(), jsonNode.get(PretupsI.DATA).get(PretupsI.IS_RESTRICTED).asBoolean());
			if(list.isEmpty()){
				pretupsResponse.setResponse(PretupsI.RESPONSE_FAIL, false, null);
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
