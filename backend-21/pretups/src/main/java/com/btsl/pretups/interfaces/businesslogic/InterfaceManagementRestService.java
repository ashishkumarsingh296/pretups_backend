package com.btsl.pretups.interfaces.businesslogic;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

@Path("/interface")
public interface InterfaceManagementRestService {

	@POST
	@Path("/load-interface-details")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<InterfaceVO>> loadInterfaceDetails(String requestData) throws BTSLBaseException, JsonParseException, JsonMappingException, IOException, SQLException;
	
	
	@POST
	@Path("/delete-interface")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<JsonNode> deleteInterface(String requestData) throws BTSLBaseException, JsonParseException, JsonMappingException, IOException, SQLException;
}
