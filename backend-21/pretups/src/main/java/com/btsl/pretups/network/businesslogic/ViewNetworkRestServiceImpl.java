package com.btsl.pretups.network.businesslogic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.ValidatorException;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

/*
 * This class implements ViewNetworkRestService and provides basic method for network view
 */

/**
 * @author 
 *
 */
public class ViewNetworkRestServiceImpl implements ViewNetworkRestService {
	
	public static final Log _log = LogFactory.getLog(ViewNetworkRestServiceImpl.class.getName());
	private static final String CLASS_NAME = "ViewNetworkRestServiceImpl";
	
	@Override
	public PretupsResponse<List<NetworkVO>> viewNetwork(String requestData)throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException
		{
		final String methodName = "#viewNetwork";
		if (_log.isDebugEnabled()) {
			_log.debug(CLASS_NAME+methodName, "Entered ");
		}
		Connection con= null;
		MComConnectionI mcomCon = null;
		 PretupsResponse<List<NetworkVO>> response =null;
		NetworkDAO networkDAO = new NetworkDAO();
		
		try{
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
		   
			JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
			JsonNode data=dataObject.get("data");
			response = new PretupsResponse<>();
			Map<String, String> fieldError = new HashMap<>();
            ViewNetworkValidator viewNetworkValidator=new ViewNetworkValidator();
			viewNetworkValidator.validateViewNetworkData(data, response);
			if (response.hasFormError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			UserVO userVO = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(data, con);
			
            if(userVO == null)
			{
					fieldError.put("loginId","network.viewNetwork.errors.login.invalid");
				    response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					response.setFieldError(fieldError);
					return response;
			}
			
			
			if(userVO.getStatus().isEmpty())
			{
				fieldError.put("Status","network.viewNetwork.errors.status");
			    response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setFieldError(fieldError);
				return response;
			}
			
			if (TypesI.SUPER_ADMIN.equals(userVO.getCategoryCode())) {
				
				
           String status = "'" + data.get("status").textValue() + "'";
           List<NetworkVO> networkList = networkDAO.loadNetworkList(con, status);
  

         if(networkList.isEmpty())
           {
        	   response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "no data found");
           }
           
         response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, networkList);
           
           
		}
			
			
			else {
			
				ShowNetworkRestServiceImpl showNetworkRestService = new ShowNetworkRestServiceImpl();
				PretupsResponse<NetworkVO> rs = showNetworkRestService.showNetwork(requestData);
				NetworkVO networkVO = rs.getDataObject();
				 List<NetworkVO> networkList = new ArrayList<>();
				 networkList.add(networkVO);
				 response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, networkList);
		           
			}
			
		}
		catch(IOException|BTSLBaseException e)
		{
			throw new BTSLBaseException (e);
		}
			finally {
			if (mcomCon != null) {
				mcomCon.close("ViewNetworkRestServiceImpl#viewNetwork");
				mcomCon = null;
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug(CLASS_NAME+methodName, "Exiting");
		}
		
		return response;
		
		
	}

	

}
