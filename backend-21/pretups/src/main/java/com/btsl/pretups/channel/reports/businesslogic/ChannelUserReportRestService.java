package com.btsl.pretups.channel.reports.businesslogic;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/*
 * Interface which provides base for BarredUserRestServiceImpl class
 * also declares different service url with method type and  for Bar User functionalities

 */
import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.user.businesslogic.UserVO;

@Path("/reports")
public interface ChannelUserReportRestService {

	@POST
	@Path("/load-user-list")
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<UserVO>> loadUserListData(String requestData) throws BTSLBaseException, Exception;

}