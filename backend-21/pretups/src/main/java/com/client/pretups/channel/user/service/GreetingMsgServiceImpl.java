package com.client.pretups.channel.user.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestClient;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.fasterxml.jackson.core.type.TypeReference;


/*
 *  * This class implements BarredUserService and define method for performing 
 *   * BAR User related operations 
 *    */
@Service("greetingMsgService")
public class GreetingMsgServiceImpl implements GreetingMsgService {

	public static final Log logger = LogFactory.getLog(GreetingMsgServiceImpl.class.getName());
	
	
	@Autowired
	private PretupsRestClient pretupsRestClient;
	/**
 * 	 * Load Domain for Channel User 
 * 	 	 * @return List The list of lookup filtered from DB
 * 	 	 	 * @throws IOException, Exception
 * 	 	 	 	 */


	@SuppressWarnings("unchecked")
	@Override
	public List<ListValueVO> loadDomain() throws BTSLBaseException, Exception  {
		final String methodName = "loadDomains";
		if (logger.isDebugEnabled()) {
			logger.debug(methodName, "Entered ");
		}
		Map<String, Object> data = new HashMap<>();
		data.put("excludeUserType", PretupsI.DOMAIN_TYPE_CODE);
		
		Map<String, Object> object = new HashMap<>();
		object.put("data", data);
		String responseString = pretupsRestClient.postJSONRequest(object, PretupsI.GREETMSGDOMAIN);
		
		PretupsResponse<List<ListValueVO>> response = (PretupsResponse<List<ListValueVO>>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<ListValueVO>>>() {});
		List<ListValueVO> list = (List<ListValueVO>) response.getDataObject();
		if (logger.isDebugEnabled()) {
			logger.debug(methodName, "Exiting");
		}
		return list;
	}
	

	@SuppressWarnings("unchecked")
	@Override
        public List<ListValueVO> loadCategory() throws BTSLBaseException, Exception   {
   		final String methodName = "loadCategory";
                
		if (logger.isDebugEnabled()) {
                        logger.debug(methodName, "Entered ");
                }
                
				Map<String, Object> data = new HashMap<>();
                
                
                Map<String, Object> object = new HashMap<>();
                object.put("data", data);
                String responseString = pretupsRestClient.postJSONRequest(object, PretupsI.GREETMSGCAT);
                
				PretupsResponse<List<ListValueVO>> response = (PretupsResponse<List<ListValueVO>>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<ListValueVO>>>() {});
                
				List<ListValueVO> list = (List<ListValueVO>) response.getDataObject();
				processListValueVOValue(list , "OPT");
				
				
                if (logger.isDebugEnabled()) {
                        logger.debug(methodName, "Exiting");
                }
                return list;
        }


	

		@SuppressWarnings("unchecked")
        @Override
        public InputStream downloadUserList(String p_domain , String p_category , String p_geography,String p_loginID) throws BTSLBaseException, Exception   {
                final String methodName = "DownloadUserList";
                InputStream inputStream = null;
				String responseString = null;
				if (logger.isDebugEnabled()) {
                        logger.debug(methodName, "Entered : domain : "+p_domain+" category : "+p_category+" geography : "+p_geography+" loginID : "+p_loginID);
                }
                Map<String, Object> data = new HashMap<>();
                data.put("domainCode", p_domain);
				data.put("userCat" , p_category);
				data.put("loginUserID" ,p_loginID);
                data.put("zoneCode", p_geography);
                Map<String, Object> object = new HashMap<>();
                object.put("data", data);
                
				try{
				responseString = pretupsRestClient.postJSONRequest(object, PretupsI.GREET_USER_DOWNLOAD);
				 PretupsResponse<byte[]> response = (PretupsResponse<byte[]>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<byte[]>>() {});
                if(response.getDataObject()!=null)
                {
                	byte[] bytes = (byte[]) response.getDataObject();
					inputStream = new ByteArrayInputStream(bytes);
                }
			
			    if (logger.isDebugEnabled()) {
                        logger.debug(methodName, "Exiting");
                }
					
					return inputStream;
				}
				finally{
					if(inputStream!=null)
						inputStream.close();
				}
        }

		
	
	@Override
	public <T> void processListValueVOValue(List<T> listObject, String type) throws Exception{
		ListValueVO listValueVO = null;
		if (listObject != null && !listObject.isEmpty()) {
			for (int i = 0, j = listObject.size(); i < j; i++) {
				listValueVO = (ListValueVO) listObject.get(i);
				if ((listValueVO.getValue().split(":")[0].toString()).equals(type)) {
					listObject.remove(i);
					i--;
					j--;
				}
			}
		}
	}


}


