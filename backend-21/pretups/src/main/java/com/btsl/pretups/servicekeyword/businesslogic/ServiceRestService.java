package com.btsl.pretups.servicekeyword.businesslogic;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;

@Path("/service")
public interface ServiceRestService {

	@POST
	@Path("/load-services")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<ListValueVO>> loadUserServices(String requestData) throws BTSLBaseException;
}
