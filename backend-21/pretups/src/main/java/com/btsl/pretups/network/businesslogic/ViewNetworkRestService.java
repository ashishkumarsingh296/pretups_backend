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
public interface ViewNetworkRestService {

	/**
	 * @param requestData
	 * @return
	 * @throws SAXException 
	 * @throws ValidatorException 
	 * @throws SQLException 
	 * @throws BTSLBaseException 
	 * @throws IOException
	 */
	@POST
	@Path("/view-network")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<NetworkVO>> viewNetwork(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException;
	

}
