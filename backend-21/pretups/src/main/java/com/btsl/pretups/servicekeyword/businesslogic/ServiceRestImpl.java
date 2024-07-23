package com.btsl.pretups.servicekeyword.businesslogic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
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

public class ServiceRestImpl implements ServiceRestService {

	private static final Log log = LogFactory.getLog(ServiceRestImpl.class.getName());
	private static final String CLASS_NAME = "ServiceRestImpl";

	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<ListValueVO>> loadUserServices(	String requestData) throws BTSLBaseException {
		final String methodName = "#loadUserServices";
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List<ListValueVO>> response;
		try {
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName, "Entered");
			}
			
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			response = new PretupsResponse<>();
			JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
			if(dataObject.has(PretupsI.DATA) && dataObject.get(PretupsI.DATA).has(PretupsI.LOGIN_ID)){
				PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
				UserVO userVO = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(dataObject.get(PretupsI.DATA), con);
				List<ListValueVO> serviceTypeList = new ServicesTypeDAO().loadUserServicesList(con, userVO.getUserID());
				response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, serviceTypeList);
			}else{
				response.setResponse(PretupsI.RESPONSE_FAIL, false, "");
			}
		} catch (SQLException | IOException e) {
			throw new BTSLBaseException(e);
		}finally{
			if (mcomCon != null) {
				mcomCon.close("ServiceRestImpl#PretupsResponse");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName, "Exit");
			}
		}
		return response;
	}

}
