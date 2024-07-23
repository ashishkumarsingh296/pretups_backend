package com.btsl.pretups.network.businesslogic;


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

@Path("/network")
public interface NetworkStatusRestService {

	
	@POST
	@Path("/fetch-network-status")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<NetworkVO>> loadnetworkStatus(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException;

@POST
	@Path("/save-network-status")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<NetworkVO>> saveNetworkStatus(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException;

	
}
