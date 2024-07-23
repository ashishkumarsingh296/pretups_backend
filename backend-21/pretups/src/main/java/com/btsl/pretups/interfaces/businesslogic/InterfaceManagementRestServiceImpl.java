package com.btsl.pretups.interfaces.businesslogic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.user.businesslogic.UserVO;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

public class InterfaceManagementRestServiceImpl implements InterfaceManagementRestService {

	public static final Log _log = LogFactory.getLog(InterfaceManagementRestServiceImpl.class.getName());

	/**
	 * Load interface details
	 *
	 * @param requestData
	 *            The request data in the form of JSON String
	 * @return response The PretupsResponse object having status of request and
	 *         different types of messages
	 * @throws BTSLBaseException,
	 * @throws IOException
	 * @throws SQLException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */

	@Override
	public PretupsResponse<List<InterfaceVO>> loadInterfaceDetails(String requestData)
			throws BTSLBaseException, JsonParseException, JsonMappingException, IOException, SQLException {

		if (_log.isDebugEnabled()) {
			_log.debug("InterfaceManagementRestServiceImpl#loadInterfaceDetails", "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		List<InterfaceVO> interfaceDetailList = null;
		PretupsResponse<List<InterfaceVO>> response = null;
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,
					new TypeReference<JsonNode>() {
					});
			if (_log.isDebugEnabled()) {
				_log.debug("Data Object is", dataObject);
			}
			InterfaceVO interfaceVO = (InterfaceVO) PretupsRestUtil
					.convertJSONToObject(dataObject.get("data").toString(), new TypeReference<InterfaceVO>() {
					});
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			UserVO userVO = (UserVO) pretupsRestUtil.getUserVOByLoginIdOrExternalCode(dataObject, con);
			if (_log.isDebugEnabled()) {
				_log.debug("UserVO is ", userVO);
			}

			InterfaceDAO interfaceDAO = new InterfaceDAO();
			interfaceDetailList = interfaceDAO.loadInterfaceDetails(con, interfaceVO.getInterfaceCategoryCode(),
					userVO.getCategoryCode(), userVO.getNetworkID());
			response = new PretupsResponse<List<InterfaceVO>>();
			if (interfaceDetailList == null || interfaceDetailList.isEmpty()) {
				response.setFormError("interface.details.not.found");
				return response;
			}
			response.setDataObject(interfaceDetailList);
			if (_log.isDebugEnabled()) {
				_log.debug("Interface Detail List is ", interfaceDetailList);
			}
		} finally {
			if (mcomCon != null) {
				mcomCon.close("InterfaceManagementRestServiceImpl#loadInterfaceDetails");
				mcomCon = null;
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug("InterfaceManagementRestServiceImpl#loadInterfaceDetails", "Exiting");
		}

		return response;
	}

	/**
	 * Load interface details
	 *
	 * @param requestData
	 *            The request data in the form of JSON String
	 * @return response The PretupsResponse object having status of request and
	 *         different types of messages
	 * @throws BTSLBaseException,
	 * @throws IOException
	 * @throws SQLException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */
	@Override
	public PretupsResponse<JsonNode> deleteInterface(String requestData)
			throws BTSLBaseException, JsonParseException, JsonMappingException, IOException, SQLException {
		if (_log.isDebugEnabled()) {
			_log.debug("InterfaceManagementRestServiceImpl#deleteInterface", "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<JsonNode> response = null;
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,
					new TypeReference<JsonNode>() {
					});

			if (_log.isDebugEnabled()) {
				_log.debug("Data Object is", dataObject);
			}
			InterfaceVO interfaceVO = (InterfaceVO) PretupsRestUtil
					.convertJSONToObject(dataObject.get("data").toString(), new TypeReference<InterfaceVO>() {
					});

			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			UserVO userVO = (UserVO) pretupsRestUtil.getUserVOByLoginIdOrExternalCode(dataObject, con);
			Date currentDate = new Date();
			interfaceVO.setModifiedOn(currentDate);
			interfaceVO.setModifiedBy(userVO.getUserID());
			
			InterfaceDAO interfaceDAO = new InterfaceDAO();
			response = new PretupsResponse<JsonNode>();
			
			if (interfaceDAO.isInterfaceExistsInInterfaceNwkPrefix(con, interfaceVO.getInterfaceId())) {
				response.setFormError("interface.can.not.be.deleted.as.msisdn.prefixes.is.associated.with.the.interface");
				return response;
            } else if (interfaceDAO.isInterfaceExistsInInterfaceNwkMapping(con, interfaceVO.getInterfaceId())) {
            	response.setFormError("interface.can.not.be.deleted.as.network.is.associated.with.it");
            	return response;
            }
			
			if (PretupsI.INTERFACE_CATEGORY_IAT.equals(interfaceVO.getInterfaceTypeId())) {
                if (interfaceDAO.isInterfaceExistsInIATMapping(con, interfaceVO.getInterfaceId())) {
                	response.setFormError("interface.can.not.be.deleted.as.iat.receiver.country.is.associated.with.it");
                	return response;
                }
            }
			
			if(interfaceDAO.deleteInterface(con, interfaceVO)>0){
				 interfaceDAO.deleteAllNodes(con, interfaceVO.getInterfaceId(), userVO.getUserID());
				 mcomCon.finalCommit();
                 AdminOperationVO adminOperationVO = new AdminOperationVO();
                 adminOperationVO.setDate(currentDate);
                 adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
                 adminOperationVO.setLoginID(userVO.getLoginID());
                 adminOperationVO.setUserID(userVO.getUserID());
                 adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                 adminOperationVO.setNetworkCode(userVO.getNetworkID());
                 adminOperationVO.setMsisdn(userVO.getMsisdn());
                 adminOperationVO.setSource(TypesI.LOGGER_INTERFACE_SOURCE);
                 adminOperationVO.setInfo("Interface " + interfaceVO.getInterfaceDescription() + " deleted successfully");
                 AdminOperationLog.log(adminOperationVO);
                 response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "interfaces.addmodify.delete.success");
			}else{
				mcomCon.finalRollback();
				response.setFormError("interfaces.addmodify.delete.notsuccess");
			}
		} finally {
			if (mcomCon != null) {
				mcomCon.close("InterfaceManagementRestServiceImpl#deleteInterface");
				mcomCon = null;
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug("InterfaceManagementRestServiceImpl#deleteInterface", "Exiting");
		}
		return response;
	}

}
