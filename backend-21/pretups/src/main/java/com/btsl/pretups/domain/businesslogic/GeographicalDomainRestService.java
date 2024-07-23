package com.btsl.pretups.domain.businesslogic;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author jashobanta.mahapatra
 * GeographicalDomainRestService provides all services for geographical domain 
 */
@Path("/geo-domain")
public interface GeographicalDomainRestService {
	
	/**
	 * @param requestData
	 * @return
	 * @throws BTSLBaseException
	 * getAllGeoDomainList gives all geographical domain list
	 */
	@POST
	@Path("/load-geo-domain-list")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonInclude(Include.NON_NULL)
	public PretupsResponse<List<Object>> getGeoDomainList(String requestData) throws BTSLBaseException;


}
