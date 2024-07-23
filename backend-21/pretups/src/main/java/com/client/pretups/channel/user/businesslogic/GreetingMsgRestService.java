package com.client.pretups.channel.user.businesslogic;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;

@Path("/greetings")
public interface GreetingMsgRestService {

	@POST
	@Path("/load-domain-data")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<ListValueVO>> loadDomainData(String requestData) throws BTSLBaseException , Exception;
	
	
	@POST
	@Path("/load-category-data")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<ListValueVO>> loadCategoryData(String requestData) throws BTSLBaseException , Exception;


	@POST
	@Path("/load-user-data")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<ListValueVO>> loadUserData(String requestData) throws BTSLBaseException, Exception;

	@POST
    @Path("/download/user-list")
    @Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse< byte[]> downloadUserList(String requestData) throws BTSLBaseException, Exception;
	
	
	
	
}
