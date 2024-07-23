package com.btsl.pretups.channel.user.businesslogic;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.btsl.common.PretupsResponse;

@Path("/mobileapp")
public interface  MobileAppRestService {

	@POST
	@Path("/load-app-version")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<Double> loadAppVersion(String requestData) throws   Exception;

}
