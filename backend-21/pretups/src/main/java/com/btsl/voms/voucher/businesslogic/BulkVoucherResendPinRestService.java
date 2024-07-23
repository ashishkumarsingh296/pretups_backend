package com.btsl.voms.voucher.businesslogic;

import java.io.IOException;
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
 * Interface which provides base for bulk voucher resend pin class
 * also declares different service url with method type and  for bulk voucher resend pin functionalities
 * @author Hargovind Karki
 * @since 12/01/2017
 *
 */
@Path("/bulk-voucher-resend-pin")
public interface BulkVoucherResendPinRestService {

	 /**this method to bulk voucher resend pin request in files
     * BulkVoucherResendPinRestService.java
     * @param requestData
     * @throws BTSLBaseException
     * @throws IOException
     * @return 
     * @author Hargovind Karki
     * @since 12/01/2017
     */
	@POST
	@Path("/upload-bulkVoucherResendPin")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<List<ListValueVO>> uploadBulkVoucherResendPin(String requestData) throws BTSLBaseException;


	
	
	
	
}
