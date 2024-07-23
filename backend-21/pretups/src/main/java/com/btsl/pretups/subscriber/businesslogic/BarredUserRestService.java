package com.btsl.pretups.subscriber.businesslogic;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.validator.ValidatorException;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.fasterxml.jackson.databind.JsonNode;

/*
 * Interface which provides base for BarredUserRestServiceImpl class
 * also declares different service url with method type and  for Bar User functionalities

 */


@Path("/barred-user")
public interface BarredUserRestService {

	@POST
	@Path("/add-barred-user")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<JsonNode> addBarredUser(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException;

	@POST
	@Path("/fetch-barred-user-list")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<BarredUserVO>> fetchBarredUserList(String requestData)	throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException;

	@POST
	@Path("/unbarred-user-list")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<BarredUserVO>> fetchBarredUserToUnbarredList(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException;

	@POST
	@Path("/unbarred-barred-user-list")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<JsonNode> processSelectedBarredUserToUnbar(	String requestData) throws BTSLBaseException, IOException,	SQLException, ValidatorException, SAXException;

}
