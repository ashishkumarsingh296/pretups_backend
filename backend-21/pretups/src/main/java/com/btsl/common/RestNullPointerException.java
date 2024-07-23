package com.btsl.common;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * This class handles null pointer exception occurs at rest level
 */
public class RestNullPointerException implements ExceptionMapper<NullPointerException> {

	public static final Log log = LogFactory.getLog(RestNullPointerException.class.getName());
	
	/**
	 * This method hold exception object occurs at rest level and prepare response
	 * @param exception Exception Object
	 * @return pretupsResponse return response in the form of pretups response
	 */
	@Override
	public Response toResponse(NullPointerException exception) {
		final String METHOD_NAME = "toResponse";
		PretupsResponse<JsonNode> pretupsResponse = new PretupsResponse<JsonNode>();
		if (log.isDebugEnabled()) {
			log.debug("load", "Entered in RestNullPointerException Handler");
		}

		pretupsResponse.setStatusCode(200);
		pretupsResponse.setStatus(false);
		

		pretupsResponse.setGlobalError("NullPointerException");
		
		if (log.isDebugEnabled()) {
			log.error("toResponse in RestNullPointerException", "Exception exception=" + exception);
			log.errorTrace("toResponse", exception);
		}
		
		
		try {
			return Response.serverError().entity(new ObjectMapper().writeValueAsString(pretupsResponse)).build();
		} catch (JsonProcessingException e) {
			log.errorTrace(METHOD_NAME, e);
		}
		return null;
	}

}
