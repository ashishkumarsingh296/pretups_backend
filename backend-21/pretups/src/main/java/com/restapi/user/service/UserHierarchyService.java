package com.restapi.user.service;

import javax.ws.rs.Path;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

import io.swagger.v3.oas.annotations.tags.Tag;

@Path("")
@Tag(name = "${dummy.name}", description = "${dummy.desc}")//@Api(value="User Hierarchy Service")
public class UserHierarchyService {

	
	
	private final Log log = LogFactory.getLog(this.getClass().getName());
	
	
	
	/*@POST
    @Path("/userservice/userhierarchy")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "User Hierarchy Request", response = PretupsResponse.class)
    public PretupsResponse<JsonNode> userHierarchy( @Parameter(description = SwaggerAPIDescriptionI.USER_HIERARCHY_REQUEST ) UserHierarchyRequestParentVO request ) {
    	
    	String serviceKeyword  = "UPUSRHRCHY";
    	PretupsResponse<JsonNode> response;
        JsonNode jsonReq = null;
		
        
        try {
			jsonReq = (JsonNode) PretupsRestUtil.convertJSONToObject(PretupsRestUtil.convertObjectToJSONString(request), new TypeReference<JsonNode>() {});
		}catch (Exception e) {
			log.error("userHierarchy", "Exception while parsig request"+e);
		}
        
        
        RestReceiver restService = new RestReceiver();
        
        response = restService.processRequestChannel(jsonReq, serviceKeyword.toUpperCase(),"");
        return response;
    }*/
    
}
