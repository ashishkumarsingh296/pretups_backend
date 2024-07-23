
package com.inter.safaricomreversal;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author dhiraj.tiwari
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SafcomTestServer {

        /**
         *
         */
        public SafcomTestServer() {
                super();
                // TODO Auto-generated constructor stub
        }

        public static void main(String[] args) 
        {

		  String url = "http://172.29.221.203:28890/ServiceAccountAdjustment";
              
		  String p_requestStr="{\"CreateServiceAccountAdjustmentListVBMRequest\":{\"IDs\":{\"ID\":\"254700100548\"},\"ValidityPeriod\":{\"FromDate\":{\"DateString\":\"20190101010101\"},\"ToDate\":{\"DateString\":\"20190101020101\"}},\"Details\":{\"AdjustmentAmount\":10000},\"Parts\":{\"AdjustmentSpecification\":{\"Name\":\"Account\",\"Desc\":\"C_MAIN_ACCOUNT\"}},\"RelatedServiceAccountAdjustments\":{\"RelatedServiceAccountAdjustment\":[{\"Name\":\"Operation\",\"Desc\":\"Airtime\"},{\"Name\":\"AdjustmentType\",\"Desc\":\"2\"},{\"Name\":\"AdditionalInfo\",\"Desc\":\"blahblah\"}]}}}}";
          Client client = ClientBuilder.newClient();
	          
              WebTarget target = client.target(url);
	          Response responseObj = target.request(MediaType.APPLICATION_JSON).header("AUTHENTICATED", true).post(Entity.entity(p_requestStr, MediaType.APPLICATION_JSON));
	          System.out.println("Response: " + responseObj);//.toString());
	          String result = responseObj.readEntity(String.class);
	          System.out.println("result: " + result.toString());
        
        }


        }
	

