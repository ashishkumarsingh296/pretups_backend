package com.btsl.pretups.channel.transfer.businesslogic;

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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.web.pretups.channel.transfer.web.C2SReversalModel;

@Path("/cs")
public interface C2SReversalRestService {
	@POST
	@Path("/reversal")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonInclude(Include.NON_NULL)
	public PretupsResponse<C2SReversalModel> c2sReversal(String requestData) throws JsonParseException,SQLException, IOException, BTSLBaseException;

	
	@POST
	@Path("/reversal-load-txn")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonInclude(Include.NON_NULL)
	public PretupsResponse<C2SReversalModel> confirmC2SReversal(String requestData) throws ValidatorException, JsonParseException,SQLException, IOException, BTSLBaseException, SAXException;
	
	@POST
	@Path("/do-reverse")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonInclude(Include.NON_NULL)
	public PretupsResponse<C2SReversalModel> doReverse(String requestData) throws JsonParseException,SQLException, IOException, BTSLBaseException, Exception;

	@POST
	@Path("/reverse-status")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonInclude(Include.NON_NULL)
	public PretupsResponse<C2SReversalModel> reverseStatus(String requestData) throws JsonParseException,SQLException, IOException, BTSLBaseException, Exception;


}
