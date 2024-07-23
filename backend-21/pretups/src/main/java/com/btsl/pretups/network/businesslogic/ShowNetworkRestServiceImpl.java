package com.btsl.pretups.network.businesslogic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.validator.ValidatorException;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author 
 *
 */
public class ShowNetworkRestServiceImpl implements ShowNetworkRestService {

	public static final Log _log = LogFactory.getLog(ViewNetworkRestServiceImpl.class.getName());
	private static final String CLASS_NAME = "ShowNetworkRestServiceImpl";
	
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<NetworkVO> showNetwork(String requestData)
			throws BTSLBaseException, IOException, SQLException,
			ValidatorException, SAXException {
		
		final String methodName = "#showNetwork";
		if (_log.isDebugEnabled()) {
			_log.debug(CLASS_NAME+methodName, "Entered");
		}
		Connection con= null;
		MComConnectionI mcomCon = null;
		PretupsResponse<NetworkVO> response =null;
		
		try
		{
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			    response = new PretupsResponse<NetworkVO>();
				JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
				JsonNode data=dataObject.get("data");
				ViewNetworkValidator viewNetworkValidator=new ViewNetworkValidator();
				viewNetworkValidator.validateShowNetworkData(data, response);
				if (response.hasFormError()) {
					response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					return response;
				}
				new PretupsRestUtil();
				 String networkCode =  data.get("networkCode").textValue() ;
				 NetworkDAO networkDAO = new NetworkDAO();
				 NetworkVO networkVO = networkDAO.loadNetwork(con, networkCode);
				
				 response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,networkVO );
		}
		catch(IOException|BTSLBaseException e)
		{
			throw new BTSLBaseException (e);
		}
			finally {
			if (mcomCon != null) {
				mcomCon.close("ShowNetworkRestServiceImpl#showNetwork");
				mcomCon = null;
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug(CLASS_NAME+methodName, "Exiting");
		}
		
		return response;
		
	}

	
}
