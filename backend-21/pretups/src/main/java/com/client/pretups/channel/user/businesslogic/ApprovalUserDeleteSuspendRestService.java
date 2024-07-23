package com.client.pretups.channel.user.businesslogic;


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


/**
 * Interface which provides base for ApprovalUserDeleteSuspendRestServiceImpl class
 * also declares different service url with method type and  for Approval Batch User SUspend/Delete functionalities
 * @author MOHD SUHEL
 * @since 17/10/2016
 *
 */
@Path("/approve-user-delete-suspend")
public interface ApprovalUserDeleteSuspendRestService {

	 /**this method to approve request in files
     * ApprovalUserDeleteSuspendRestService.java
     * @param requestData
     * @throws BTSLBaseException
     * @throws IOException
     * @return 
     * @author MOHD SUHEL
     * @since 17/10/2016
     */
	@POST
	@Path("/approval-batch")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<ListValueVO>> approveDeleteSuspendUser(String requestData) throws BTSLBaseException;


	 /**this method use to download all user list in approval request
    * ApprovalUserDeleteSuspendRestService.java
    * @param requestData
    * @throws BTSLBaseException
    * @throws IOException
    * @return
    * @author MOHD SUHEL
	 * @throws SQLException 
    * @since 17/10/2016
    */
	@POST
    @Path("/download-user-list")
    @Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse< byte[]> downloadUserList(String requestData) throws BTSLBaseException;
	
	
	
}
