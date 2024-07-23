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
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.user.businesslogic.UserVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author jashobanta.mahapatra
 * GeographicalDomainRestServiceImpl is the implementation class for all services of geographical domain 
 */
public class GeographicalDomainRestServiceImpl implements GeographicalDomainRestService{

	private  static final Log log = LogFactory.getLog(GeographicalDomainRestServiceImpl.class.getName());

	private static String className = "GeographicalDomainRestServiceImpl";

	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<Object>> getGeoDomainList(String requestData) throws BTSLBaseException {
		final String methodName =  className+": getGeoDomainList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		try{
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			JsonNode jsonNode =  (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,	new TypeReference<JsonNode>() {});
			
			PretupsResponse<List<Object>> pretupsResponse = new PretupsResponse<>();
			DomainValidator domainValidator = new DomainValidator();
			domainValidator.validateRequestDataForGeographyDomain(jsonNode.get("data"), pretupsResponse);
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			UserVO userVO = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(jsonNode.get("data"), con); 
			if(pretupsResponse.hasFormError()){
				return pretupsResponse;
			}
			
			GeographicalDomainDAO dao = new GeographicalDomainDAO();
			List<Object> list = dao.loadUserGeographyList(con, userVO.getUserID(), userVO.getNetworkID());
			
			if(list.isEmpty()){
				pretupsResponse.setResponse("", null);
			}
			
			pretupsResponse.setDataObject(PretupsI.RESPONSE_SUCCESS, true, list);
			return pretupsResponse;
		}catch (IOException | SQLException e) {
			throw new BTSLBaseException(e);
		}finally{
			if (mcomCon != null) {
				mcomCon.close("GeographicalDomainRestServiceImpl#getGeoDomainList");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
	}
	
}
