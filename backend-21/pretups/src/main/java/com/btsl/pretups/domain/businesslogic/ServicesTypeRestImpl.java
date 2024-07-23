package com.btsl.pretups.domain.businesslogic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author jashobanta.mahapatra
 * ServicesTypeRestImpl is the implementation class for all services of service type
 */
public class ServicesTypeRestImpl implements ServicesTypeRest{

	private Log log = LogFactory.getLog(this.getClass().getName());

	private static String className = "ServicesTypeRestImpl";
	
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<ListValueVO>> getAllServiceTypeList(String requestData) throws BTSLBaseException {
		String methodName =  className+": getAllServiceTypeList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		try{
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			JsonNode dataObject =  (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,	new TypeReference<JsonNode>() {});
			String loginId =  (String) PretupsRestUtil.convertJSONToObject(dataObject.get("loginId").textValue(), new TypeReference<String>() {});
			List<ListValueVO> serviceTypeList = new ArrayList<>();

			final UserDAO userDAO = new UserDAO();
			final ServicesTypeDAO servicesTypeDAO = new ServicesTypeDAO();
			final UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			serviceTypeList = servicesTypeDAO.loadUserServicesList(con, userVO.getUserID()); 
			
			PretupsResponse<List<ListValueVO>> response = new PretupsResponse<>();
			if(serviceTypeList.isEmpty()){
				response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "no.data.found");
				return response;
			}
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, serviceTypeList);

			if (log.isDebugEnabled()) {
				log.debug(methodName, "Existing");
			}

			return response;
		}catch (SQLException | IOException e) {
			throw new BTSLBaseException(e);
		}finally{
			if (mcomCon != null) {
				mcomCon.close("ServicesTypeRestImpl#getAllServiceTypeList");
				mcomCon = null;
			}
		}
	}


}
