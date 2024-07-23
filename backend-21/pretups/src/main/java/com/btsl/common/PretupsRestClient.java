package com.btsl.common;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.loadcontroller.LoadControllerCache;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CalculateVoucherTransferRuleVO;
import com.btsl.pretups.channel.profile.businesslogic.CardGroupStatusVO;
import com.btsl.pretups.channel.profile.businesslogic.DefaultCardGroupVO;
import com.btsl.pretups.channel.profile.businesslogic.LoadVersionListRequestVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.WebServiceKeywordCacheVO;
import com.btsl.util.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.btsl.util.BTSLUtil;

/*
 * This class provides method for sending HTTP request on rest Services
 */
@Component
public class PretupsRestClient {

	public static final Log log = LogFactory.getLog(PretupsRestClient.class.getName());
	
	
	/**
	 * This method works as Client for sending http request and receiving response
	 * @param object A generic object which user wants to process
	 * @operationCode Operation Code which helps us to recognize rest service
	 * @return String response received from Rest Service
	 * @throws IOException, RuntimeException, Exception
	 */
	public <T> String postJSONRequest(T object, String operationCode) throws IOException, RuntimeException{
		if (log.isDebugEnabled()) {
			log.debug("PretupsRestClient#postJSONRequest", "Entered ");
		}
		Response response = null;
		String responseString = null;
		WebServiceKeywordCacheVO webServiceKeywordCacheVO=ServiceKeywordCache.getWebServiceTypeObject(operationCode.trim());
		try {
			Client client = ClientBuilder.newClient();
			InstanceLoadVO instanceLoadVO = this.getInstanceLoadVOObject();
			String targetUrl = null;
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.HTTPS_ENABLE)).booleanValue()) {
				targetUrl = PretupsI.HTTPS_URL + instanceLoadVO.getHostAddress() + PretupsI.COLON + instanceLoadVO.getHostPort() + PretupsI.FORWARD_SLASH + instanceLoadVO.getContext() + webServiceKeywordCacheVO.getServiceUrl();
             }else{
            	targetUrl = PretupsI.HTTP_URL + instanceLoadVO.getHostAddress() + PretupsI.COLON + instanceLoadVO.getHostPort() + PretupsI.FORWARD_SLASH + instanceLoadVO.getContext() + webServiceKeywordCacheVO.getServiceUrl();
             }
			
			WebTarget target = client.target(targetUrl);
			if (log.isDebugEnabled()) {
			    try{
			        log.debug("Request Data", BTSLUtil.maskParam(PretupsRestUtil.convertObjectToJSONString(object)));
			    }catch(Exception e){
                e.printStackTrace();
                }
				
			}
			
			if (log.isDebugEnabled()) {
				log.debug("Target URL", targetUrl);
			}

			//removing this object condition as now catered by Spring services instead of resteasy
			/**
			if(object instanceof LoadVersionListRequestVO || object instanceof CardGroupStatusVO
					|| object instanceof CalculateVoucherTransferRuleVO || object instanceof DefaultCardGroupVO)
			{
				response = target.request(MediaType.APPLICATION_JSON).header("AUTHENTICATED", true)
						.post(Entity.entity(object, MediaType.APPLICATION_JSON));
			}
			else
			{
				response = target.request(MediaType.APPLICATION_JSON).header("AUTHENTICATED", true)
						.post(Entity.entity(PretupsRestUtil.convertObjectToJSONString(object), MediaType.APPLICATION_JSON));
			}
			 */
			response = target.request(MediaType.APPLICATION_JSON).header("AUTHENTICATED", true)
					.post(Entity.entity(PretupsRestUtil.convertObjectToJSONString(object), MediaType.APPLICATION_JSON));
			responseString = response.readEntity(String.class);
			if (log.isDebugEnabled()) {
				log.debug("Response Data", responseString);
			}
			JsonNode json = (JsonNode) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<JsonNode>() {});
			if(json.has("globalError") && !json.get("globalError").isNull()){
				throw new RuntimeException(PretupsRestUtil.getMessageString("common.logout.message.session.expired"));
			}
			
			if (log.isDebugEnabled()) {
				log.debug("PretupsRestClient#postJSONRequest", "Exiting");
			}
		} finally {
			if(response != null){
				response.close();
			}
		}
		
		return responseString;
	}
	
	
	/**
	 * This method return InstanceLoadVO object for creating dynamic url
	 * @return InstanceLoadVO object
	 * @throws RuntimeException
	 */
	private InstanceLoadVO getInstanceLoadVOObject() throws RuntimeException{
		InstanceLoadVO instanceLoadVO, instanceLoadVORest;
		instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(Constants.getProperty("INSTANCE_ID") + "_" + PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if(instanceLoadVO != null){
			instanceLoadVORest = LoadControllerCache.getInstanceLoadForNetworkHash(instanceLoadVO.getRstInstanceID() + "_" + PretupsI.REQUEST_SOURCE_TYPE_REST);
		}else{
			throw new RuntimeException(PretupsRestUtil.getMessageString("no.mapping.found.for.web.sms.or.rest.configuration"));
		}
		
		if(instanceLoadVORest == null){
			instanceLoadVORest = LoadControllerCache.getInstanceLoadForNetworkHash(instanceLoadVO.getRstInstanceID() + "_" + PretupsI.REQUEST_SOURCE_TYPE_SMS);
		}
		
		if(instanceLoadVORest == null){
			instanceLoadVORest = instanceLoadVO;
		}
		
		return instanceLoadVORest;
	}
	
}
