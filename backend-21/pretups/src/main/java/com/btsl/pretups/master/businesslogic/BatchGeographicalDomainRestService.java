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

/**
 * Interface which provides base for BatchGeographicalDomainServiceImpl class
 * also declares different service url with method type and  for Batch Geographical Domain Creation functionality
 * @author VIKAS CHAUDHARY
 * @since 02/11/2016
 *
 */
@Path("/batch-geographical-domain")
public interface BatchGeographicalDomainRestService {
	
	 /**this method use to download template sheet 
    * BatchGeographicalDomainRestService.java
    * @param requestData
    * @throws BTSLBaseException
    * @throws IOException
    * @return
    * @author VIKAS CHAUDHARY
    * @throws SQLException 
    * @since 02/11/2016
    */
	
	@POST
    @Path("/download-template")
    @Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<byte[]> downloadList(String requestData) throws BTSLBaseException;
	
	
	/**this method is used to initiate batch geographical domain creation
	 * atchGeographicalDomainRestService.java
	 * @param requestData
	 * @throws BTSLBaseException
	 * @throws IOException
	 * @return
	 * @author VIKAS CHAUDHARY
	 * @throws SQLException
	 * @since 02/11/2016
	 */
		@POST
	    @Path("/initiate-batch")
	    @Consumes(value=MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public PretupsResponse<List<ListValueVO>> initiateBatchGeographicalDomainCreation(String requestData) throws BTSLBaseException;
	
	
	
}
