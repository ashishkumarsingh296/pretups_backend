package com.btsl.pretups.channel.profile.businesslogic;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.web.pretups.channel.profile.web.CommissionProfileModel;

@Path("/commission-profile")
public interface CommissionProfileRestService {
	
	@POST
	@Path("/load-commission-set-list")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonInclude(Include.NON_NULL)
	public PretupsResponse<CommissionProfileModel> loadCommissionSetList(String requestData) throws BTSLBaseException, SQLException, Exception;

	@POST
	@Path("/load-commission-status")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonInclude(Include.NON_NULL)
	public PretupsResponse<CommissionProfileModel> loadCommissionStatus(String requestData) throws BTSLBaseException, SQLException, Exception;
	
	
	@POST
	@Path("/save-suspend")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonInclude(Include.NON_NULL)
	public PretupsResponse<CommissionProfileModel> commissionProfileSetSuspend(String requestData) throws BTSLBaseException, SQLException, Exception ;
	

}
