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
import com.btsl.user.businesslogic.UserVO;


@Path("/network")
public interface ChangeNetworkRestService {
	
	
	@POST
	@Path("/Change-Network")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<NetworkVO>> loadNetworkListForChange(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException;

	@POST
	@Path("/Submit-Change-Network")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<UserVO> processNetworkListForChange(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException;


}
