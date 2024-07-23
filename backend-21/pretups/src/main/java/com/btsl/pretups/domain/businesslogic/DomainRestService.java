package com.btsl.pretups.domain.businesslogic;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;

/**
 * This interface declares method for managing domain
 * @author lalit.chattar
 *
 */

@Path("/domain")
public interface DomainRestService {

	/**
	 * This method return domain details
	 * @param <T>
	 * @param requestData Request Data
	 * @return List of Domains
	 */
	@POST
	@Path("/load-domain-details")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<Object>> loadDomainDetails(String requestData) throws BTSLBaseException;
}
