package com.btsl.user.businesslogic;

import java.io.IOException;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.validator.ValidatorException;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;

@Path("/user")
public interface ViewSelfDetailsRestService {
	
	/**
	 * @param requestData
	 * @return
	 * @throws SAXException 
	 * @throws ValidatorException 
	 * @throws SQLException 
	 * @throws BTSLBaseException 
	 * @throws IOException
	 */
	@POST
	@Path("/view-selfdetails")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<ChannelUserVO> viewSelfDetails(String requestData) throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException;

}
