package com.btsl.pretups.domain.businesslogic;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * @author jashobanta.mahapatra
 * ServicesTypeRest provides all services for service type
 */
@Path("/service-type")
public interface ServicesTypeRest {

	/**
	 * @param requestData
	 * @return
	 * @throws BTSLBaseException
	 * getAllServiceTypeList gives service list
	 */
	@POST
	@Path("/load-service-type-list")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonInclude(Include.NON_NULL)
	public PretupsResponse<List<ListValueVO>> getAllServiceTypeList(String requestData) throws BTSLBaseException;
}
