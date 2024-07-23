package com.btsl.pretups.restrictedsubs.businesslogic;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.web.pretups.restrictedsubs.web.RestrictedSubscriberModel;

/**
 * @author jashobanta.mahapatra
 *
 */
@Path("/batch-reschedule")
public interface BatchRechageRescheduleRestService {
	
	/**
	 * @param requestData
	 * @return
	 * @throws BTSLBaseException
	 */
	@POST
	@Path("/load-batch-list")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonInclude(Include.NON_NULL)
	public  PretupsResponse<RestrictedSubscriberModel>  loadScheduleBatchList(String requestData) throws BTSLBaseException;

	/**
	 * @param requestData
	 * @return
	 * @throws BTSLBaseException
	 */
	@POST
	@Path("/download-batch-file")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonInclude(Include.NON_NULL)
	public PretupsResponse<RestrictedSubscriberModel> createBatchFileForReschedule( String requestData) throws BTSLBaseException;
	
	/**
	 * @param requestData
	 * @return
	 * @throws BTSLBaseException
	 */
	@POST
	@Path("/process-reschedule")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonInclude(Include.NON_NULL)
	public PretupsResponse<Object>  updateAndProcessBatchRechargeReschedule(String requestData)throws BTSLBaseException ;
}
