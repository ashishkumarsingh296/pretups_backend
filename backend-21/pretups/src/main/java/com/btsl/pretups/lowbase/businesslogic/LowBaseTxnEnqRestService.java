package com.btsl.pretups.lowbase.businesslogic;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;


/**
 * This Interface provides basic method for Low Base Transaction Enquiry
 * @author lalit.chattar
 *
 */
@Path("/low-base")
public interface LowBaseTxnEnqRestService {

	/**
	 * This method declaration is for loading Low Base Transaction Enquiry Details
	 * @param requestData JSON string of request data
	 * @return 
	 * @throws BTSLBaseException 
	 */
	
	@POST
	@Path("/load-transaction-details")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<LowBasedRechargeVO>> loadLowBaseTransactionDetails(String requestData) throws BTSLBaseException;
	
	
	
	
	/**
	 * This method declaration is for loading Low Base Transaction Enquiry Details
	 * @param requestData JSON string of request data
	 * @return 
	 * @throws BTSLBaseException 
	 */
	
	@POST
	@Path("/load-eligibility-details")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<LowBasedRechargeVO> loadLowBaseEligibilityDetails(String requestData) throws BTSLBaseException;
	
	
}
