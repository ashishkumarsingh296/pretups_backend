package com.btsl.pretups.master.businesslogic;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;

@Path("/lookups")
public interface LookupsRestService {

	@POST
	@Path("/lookups-dropdown")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<ListValueVO>> loadLookupDropDown(String requestData) throws IOException;
	
	
	@POST
	@Path("/sub-lookups-dropdown")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<SubLookUpVO>> loadSublookupVOList(String requestData) throws IOException, SQLException, BTSLBaseException;
	
}
