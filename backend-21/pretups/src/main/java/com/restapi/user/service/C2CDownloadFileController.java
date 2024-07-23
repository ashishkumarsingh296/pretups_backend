package com.restapi.user.service;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.springframework.web.bind.annotation.RequestBody;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.util.SqlParameterEncoder;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author anshul.goyal2
 *  Download the file using API
 */
@Path("/c2c")
@Tag(name = "${dummy.name}", description = "${dummy.desc}")//@Api(value = "C2C Download File")
public class C2CDownloadFileController {

	public static final Log log = LogFactory
			.getLog(C2CDownloadFileController.class.getName());

	@POST
	@Path("/downloadFile")
	@Consumes(value = MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile(@RequestBody String requestData)throws BTSLBaseException, SQLException, JsonParseException,JsonMappingException, IOException {
		JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
		String transferid = SqlParameterEncoder.encodeParams((requestNode.get("transferid").textValue()));
		
		File fileNew = this.loadDownloadFile(transferid);
		String fileName = fileNew.getName();
		ResponseBuilder response = Response.ok((Object) fileNew);
		response.header("Content-Disposition", "attachment;filename="+ fileName);
		return response.build();
	}

	private File loadDownloadFile(String transferId) throws BTSLBaseException,SQLException {
		Connection con = null;
		MComConnectionI mcomCon = null;
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();
		File file = new ChannelTransferDAO().getChannelTransferFile(con,transferId);
		return file;
	}

}
