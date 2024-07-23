package com.btsl.pretups.network.businesslogic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.validator.ValidatorException;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

public class ChangeNetworkRestServiceImpl implements ChangeNetworkRestService {

	
	
	private static final Log log = LogFactory.getLog(ChangeNetworkRestServiceImpl.class.getName());
	private static final String CLASS_NAME = "ChangeNetworkRestServiceImpl";
	
	
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<NetworkVO>> loadNetworkListForChange(String requestData) throws BTSLBaseException, IOException, SQLException,ValidatorException, SAXException {
		final String methodName = "#loadNetworkListForChange";
		Connection  con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List<NetworkVO>> response ;
		try {
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName, "Entered");
			}
			
			
	    response = new PretupsResponse<>();
		JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
		JsonNode dataNode =  requestNode.get("data");	
		mcomCon = new MComConnection();
		con=mcomCon.getConnection();
		 PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
		 Map<String, String> fieldError = new HashMap<>();
		 if(dataNode.get("loginId").textValue().isEmpty())
		 {
			   
			   fieldError.put("loginId","network.changeNetwork.errors.login.required");
			    response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setFieldError(fieldError);
				return response; 
		 }
		 else {
			 UserVO userVO = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(dataNode, con);
			 if(userVO == null)
			 {
				 fieldError.put("loginId","network.changeNetwork.errors.login.invalid");
				    response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					response.setFieldError(fieldError);
					return response; 
			 }
		NetworkVO networkVO = new NetworkVO();
	
		NetworkDAO networkDAO = new NetworkDAO();
		 String status = "'" + PretupsI.STATUS_DELETE + "'";
         if (TypesI.NO.equals(userVO.getCategoryVO().getViewOnNetworkBlock())) {
             status = "'" + PretupsI.STATUS_DELETE + "','" + PretupsI.STATUS_SUSPEND + "'";
         }
         List<NetworkVO> networkList, finalNetworkList = new ArrayList<NetworkVO>();
         
             
         if(TypesI.SUPER_NETWORK_ADMIN.equals(userVO.getCategoryCode()) || TypesI.SUPER_CUSTOMER_CARE.equals(userVO.getCategoryCode()))
         {
         	networkList = networkDAO.loadNetworkListForSuperOperatorUsers(con, status, userVO.getUserID());
         }
         else if(TypesI.SUPER_CHANNEL_ADMIN.equals(userVO.getCategoryCode()))
         {
         	networkList = networkDAO.loadNetworkListForSuperChannelAdm(con, status, userVO.getUserID());
         }
         else
         {
         	 networkList = networkDAO.loadNetworkList(con, status);
         }
         if (!networkList.isEmpty()) {
             
             networkVO.setstatusList(LookupsCache.loadLookupDropDown(PretupsI.STATUS_TYPE, true));
             for (int i = 0, j = networkList.size(); i < j; i++) {
                 NetworkVO netVO = (NetworkVO) networkList.get(i);
                 ListValueVO listVO = BTSLUtil.getOptionDesc(netVO.getStatus(), networkVO.getstatusList());
                 netVO.setStatusDesc(listVO.getLabel());
                 finalNetworkList.add(netVO);
             } 
          
             response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, finalNetworkList);
             
             
         }
			
		} 
		}catch (BTSLBaseException | IOException | SQLException e ) {
			throw new BTSLBaseException(e);
		}finally{
			if (mcomCon != null) {
				mcomCon.close("ChangeNetworkRestServiceImpl#loadNetworkListForChange");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName, "Exit");
			}
		}
		
		
		
		return response;
	}


	@Override
	@SuppressWarnings("unchecked")
	public PretupsResponse<UserVO> processNetworkListForChange(String requestData) throws BTSLBaseException, IOException,SQLException, ValidatorException, SAXException {
		final String methodName = "#processNetworkListForChange";
		Connection  con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<UserVO> response ;
		try {
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName, "Entered");
			}
			
			
	    response = new PretupsResponse<>();
		JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
		JsonNode dataNode =  requestNode.get("data");	
		mcomCon = new MComConnection();
		con=mcomCon.getConnection();
		 PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
		 NetworkDAO networkDAO = new NetworkDAO();
		 Map<String, String> fieldError = new HashMap<>();
		 if(dataNode.get("loginId").textValue().isEmpty())
		 {
			   
			   fieldError.put("loginId","network.changeNetwork.errors.login.required");
			    response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setFieldError(fieldError);
				return response; 
		 }
		 else {
			 UserVO userVO = pretupsRestUtil.getUserVOByLoginIdOrExternalCode(dataNode, con);
			 if(userVO == null)
			 {
				 fieldError.put("loginId","network.changeNetwork.errors.login.invalid");
				    response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					response.setFieldError(fieldError);
					return response; 
			 }
		 
		NetworkVO networkVO = networkDAO.loadNetwork(con, dataNode.get("networkCode").textValue());
		NetworkStatusValidator networkStatusValidator=new NetworkStatusValidator();
		networkStatusValidator.validateNetworkData(networkVO, dataNode, response);
		if (response.hasFieldError()) {
			response.setStatus(false);
			response.setStatusCode(PretupsI.RESPONSE_FAIL);
			

			return response;
		}
		// change the network related information into the userVO
        userVO.setNetworkID(networkVO.getNetworkCode());
        userVO.setNetworkName(networkVO.getNetworkName());
        userVO.setReportHeaderName(networkVO.getReportHeaderName());
        userVO.setNetworkStatus(networkVO.getStatus());
        LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
        if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
            userVO.setMessage(networkVO.getLanguage1Message());
        } else {
            userVO.setMessage(networkVO.getLanguage2Message());
        }
        /*
         * while change the network location also change the user geographical list
         * because while adding user we check the domain type of the loginUser  and AddedUser
         * if domain type is same of both the user then the geographical list of the added user is same as
         * the geographical list of the login user*/
        if(!TypesI.SUPER_CHANNEL_ADMIN.equals(userVO.getCategoryCode()))
        {
        UserGeographiesVO geographyVO = null;
        ArrayList<UserGeographiesVO> geographyList = new ArrayList<>();
        geographyVO = new UserGeographiesVO();
        geographyVO.setGraphDomainCode(userVO.getNetworkID());
        geographyVO.setGraphDomainName(userVO.getNetworkName());
        geographyVO.setGraphDomainTypeName(userVO.getCategoryVO().getGrphDomainTypeName());
        geographyList.add(geographyVO);

        userVO.setGeographicalAreaList(geographyList);
        }
        else
        {
        	
			ArrayList<UserGeographiesVO>	userGeoList = new GeographicalDomainDAO().loadUserGeographyList(con, userVO.getUserID(), userVO.getNetworkID());
        	userVO.setGeographicalAreaList(userGeoList);
        }
        
        
        
        response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, userVO);
	 
		 
		 }	 
	}catch (BTSLBaseException | IOException | SQLException e ) {
		throw new BTSLBaseException(e);
	}finally{
			if (mcomCon != null) {
				mcomCon.close("ChangeNetworkRestServiceImpl#processNetworkListForChange");
				mcomCon = null;
			}
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, "Exit");
		}
	}
		return response;
		
		
	}
	

}
