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
 * This interface declares method for category 
 * @author lalit.chattar
 *
 */

@Path("/category")
public interface CategoryRestService {

	/**
	 * This method loads category form DB
	 * @param requestData
	 * @return pretupsResponse
	 * @throws BTSLBaseException
	 */
	@POST
	@Path("/load-category-details")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<Object>> loadCategoryDetails(String requestData) throws BTSLBaseException;
}
