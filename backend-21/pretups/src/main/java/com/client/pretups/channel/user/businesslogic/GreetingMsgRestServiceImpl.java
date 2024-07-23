package com.client.pretups.channel.user.businesslogic;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
/*
 * This class implements LookupsRestService and provides basic method to load Look and sublookups
 */
public class GreetingMsgRestServiceImpl implements GreetingMsgRestService {
	
	private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    

	public static final Log _log = LogFactory.getLog(GreetingMsgRestServiceImpl.class.getName());
	
	/**
	 * This method load Domain data from DB 
	 * @param requestData Json string of Of User Type
	 * @return  PretupsResponse<List<ListValueVO>>
	 * @throws BTSLBaseException, Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<ListValueVO>> loadDomainData(String requestData) throws BTSLBaseException ,Exception{
		final String METHOD_NAME = "loadDomainData";
		
		if (_log.isDebugEnabled()) {
			_log.debug("GreetingMsgRestServiceImpl#loadDomainData", "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List<ListValueVO>> response = new PretupsResponse<List<ListValueVO>>();
		Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<Map<String, Object>>() {});
		Map<String, Object> map = (Map<String, Object>) dataMap.get("data");
		if(!map.containsKey("excludeUserType") || map.isEmpty()){
			 response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, new ArrayList<ListValueVO>());
			 response.setResponse(PretupsI.RESPONSE_SUCCESS , true , "tag.is.missing");
			 return response;
		}
		try{
			 DomainDAO domainDAO = new DomainDAO();
			 mcomCon = new MComConnection();
			 con=mcomCon.getConnection();
			 List<ListValueVO> list = domainDAO.loadDomainList(con, map.get("excludeUserType").toString());
			 response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,list);
			
		}
		finally {
           
			if (mcomCon != null) {
				mcomCon.close("GreetingMsgRestServiceImpl#loadDomainData");
				mcomCon = null;
			}
           
        }
		
		if (_log.isDebugEnabled()) {
			_log.debug("GreetingMsgRestServiceImpl#loadDomainData", "Exiting");
		}
		
		return response;
	}

	
	
	/**
	 * This method load CategoryList data from DB 
	 * @param requestData Json string of Of User Type
	 * @return  PretupsResponse<List<ListValueVO>>
	 * @throws BTSLBaseException, Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<ListValueVO>> loadCategoryData(String requestData) throws BTSLBaseException ,Exception {
		final String METHOD_NAME = "loadCategoryData";
		
		if (_log.isDebugEnabled()) {
			_log.debug("GreetingMsgRestServiceImpl#loadCategoryData", "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List<ListValueVO>> response = new PretupsResponse<List<ListValueVO>>();
		
		try{
			 _log.debug("loadCategoryData", "ENTERED IN DATA");
			 CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
			 mcomCon = new MComConnection();
			 con=mcomCon.getConnection();
			 List<ListValueVO> list = categoryWebDAO.loadCategoryVOList(con);
			 response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,list);
			
		}
		finally {
           
			if (mcomCon != null) {
				mcomCon.close("GreetingMsgRestServiceImpl#loadCategoryData");
				mcomCon = null;
			}
        }
		
		if (_log.isDebugEnabled()) {
			_log.debug("GreetingMsgRestServiceImpl#loadCategoryData", "Exiting");
		}
		
		return response;
	}

	
	/**
	 * This method load CategoryList data from DB 
	 * @param requestData Json string of Of User Type
	 * @return  PretupsResponse<List<ListValueVO>>
	 * @throws BTSLBaseException, Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<ListValueVO>> loadUserData(String requestData) throws BTSLBaseException, Exception {
		final String METHOD_NAME = "loadUserData";
		
		if (_log.isDebugEnabled()) {
			_log.debug("GreetingMsgRestServiceImpl#loadUserData", "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List<ListValueVO>> response = new PretupsResponse<List<ListValueVO>>();
		Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<Map<String, Object>>() {});
		Map<String, Object> map = (Map<String, Object>) dataMap.get("data");
		if(!map.containsKey("zoneCode") ||!map.containsKey("userCat") || !map.containsKey("domainCode") || !map.containsKey("loginUserID") || map.isEmpty()){
			 _log.debug(METHOD_NAME, "Missing Tag in Json Request : zoneCode or userCat or domainCode or loginUserID");
			 response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, new ArrayList<ListValueVO>());
			 response.setResponse(PretupsI.RESPONSE_SUCCESS , true , "tag.is.missing");
			 return response;
		}
		try{
			 
			 ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
			 mcomCon = new MComConnection();
			 con=mcomCon.getConnection();
			 String fromUserID = null , userName = "%%%";
			 
			 if(map.containsKey("fromUserID")){
				 fromUserID = map.get("fromUserID").toString();
			 }
			 if(map.containsKey("userName"))
			 {
				 
				 userName = map.get("userName").toString();
			 }
			 List<ListValueVO> list = channelUserWebDAO.loadUserListOnZoneDomainCategoryWithMSISDN(con ,  map.get("userCat").toString() ,  map.get("zoneCode").toString() , fromUserID , userName , map.get("loginUserID").toString() ,  map.get("domainCode").toString());
			 response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,list);
			
		}finally {
         
			if (mcomCon != null) {
				mcomCon.close("GreetingMsgRestServiceImpl#loadUserData");
				mcomCon = null;
			}
         
        }
		
		if (_log.isDebugEnabled()) {
			_log.debug("GreetingMsgRestServiceImpl#"+METHOD_NAME+"", "Exiting");
		}
		
		return response;
	}

	
	@Override
	public PretupsResponse< byte[]> downloadUserList(String requestData) throws BTSLBaseException, Exception{
		
		

    	InputStream inputStream = null;
		final String METHOD_NAME = "DownlaodUserList";
		
		if (_log.isDebugEnabled()) {
			_log.debug("GreetingMsgRestServiceImpl#DownloadUIserData", "Entered ");
		}
		String fileHeader = PretupsRestUtil.getMessageString("greetmsg.file.header");
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse< byte[]> response = new PretupsResponse< byte[]>();
		Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<Map<String, Object>>() {});
		Map<String, Object> map = (Map<String, Object>) dataMap.get("data");
		
		
		if(!map.containsKey("zoneCode") ||!map.containsKey("userCat") || !map.containsKey("domainCode") || !map.containsKey("loginUserID") || map.isEmpty()){
			 _log.debug(METHOD_NAME, "Missing Tag in Json Request : zoneCode or userCat or domainCode or loginUserID");
			 response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, null);
			 response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "tag.is.missing");
			 return response;
		}
		
		
		String fileDataStream = "";
		fileDataStream+=fileHeader.toString();
		fileDataStream+=NEW_LINE_SEPARATOR;
		try{
			
			 ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
			 mcomCon = new MComConnection();
			 con=mcomCon.getConnection();
			 String fromUserID = null ;
			 String userName = "%%%";
			 
			 if(map.containsKey("fromUserID")){
				 fromUserID = map.get("fromUserID").toString();
			 }
			 if(map.containsKey("userName"))
			 {
				 userName = map.get("userName").toString();
			 }
			
			 List<ListValueVO> list = channelUserWebDAO.loadUserListOnZoneDomainCategoryWithMSISDN(con ,  map.get("userCat").toString() ,  map.get("zoneCode").toString() , fromUserID , userName , map.get("loginUserID").toString() ,  map.get("domainCode").toString());
        
		
			if(list.isEmpty())
			{
				response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "no.data.found");
				response.setDataObject(fileDataStream.getBytes());
				return response;
			}
			
			try{
				
				
				
				for(ListValueVO item : list)
				{
				
					fileDataStream+=item.getValue().toString();
					fileDataStream+=COMMA_DELIMITER;
					fileDataStream+=NEW_LINE_SEPARATOR;
				
				}

			}
			finally{}
		}
		finally{
			if (mcomCon != null) {
				mcomCon.close("GreetingMsgRestServiceImpl#downloadUserList");
				mcomCon = null;
			}
		}
		
	
		response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, fileDataStream.getBytes());
		response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "file.download.successfully");
		
		if (_log.isDebugEnabled()) {
			_log.debug("GreetingMsgRestServiceImpl#DownloadUIserData", "Exited ");
		}
		
        return response;
		
    }
	
	

	
}
