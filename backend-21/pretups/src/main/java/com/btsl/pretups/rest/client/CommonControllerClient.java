package com.btsl.pretups.rest.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.RestfulConstants;

/**
 * Common Controller Client
 *
 */
public class CommonControllerClient {
	private static final Log LOG = LogFactory.getLog(CommonControllerClient.class.getName());
	public static final String URL = RestfulConstants.getProperty("URL");
	private static final String LISTMETHOD="LIST";
	private static final String READMETHOD="READ";
	private static final String ADDMETHOD="ADD";
	private static final String UPDATEMETHOD="UPDATE";
	private static final String DELETEMETHOD="DELETE";
	private final Client restClient;
	private final WebTarget webResource;
	
	/**
	 * Constructor
	 */
	public CommonControllerClient() {
		 restClient = ClientBuilder.newClient();
	     webResource = restClient.target(URL);
	}
	
	/**
	 * @param pMethodType
	 * @param pWebServiceType
	 * @param pWebServiceMethod
	 * @param obj
	 * @return
	 */
	public String processRequest(String pMethodType,String pWebServiceType, String pWebServiceMethod, Object obj  ){//String p_jsonString,
        
		
        String pathString=RestfulConstants.getProperty(pWebServiceType);

        String output ="";
        Response resp=null;
        try {
	        if(pMethodType.equalsIgnoreCase(LISTMETHOD))
	        {	        	
	        	
	        	 
	        	 //url
	        	 Invocation.Builder invocationBuilder =  webResource.path(pathString).path(pWebServiceMethod).request(MediaType.APPLICATION_JSON);
	        	 //parameter obj
	        	 resp=  invocationBuilder.post(Entity.entity(obj, MediaType.APPLICATION_JSON));
	        	 output=resp.readEntity(String.class);
	        
	        	
	        }
	      
	        
	    }
	        catch (Exception e) {
	        	LOG.error("processRequest",
						"Exception " + e.getMessage());
	        	LOG.errorTrace("processRequest", e);
	        }
        finally
        {
        	if(resp!=null){
        	resp.close();
        	}
        	restClient.close();
        	
        }
	        return output;
    }
}
