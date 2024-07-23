package com.btsl.common;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * This class handles all the exception occurs at rest level
 */
@Provider
public class RestAllExceptionHandler implements ExceptionMapper<Exception> {

	public static final Log log = LogFactory.getLog(RestAllExceptionHandler.class.getName());

	/**
	 * This method hold exception object occurs at rest level and prepare response
	 * @param exception Exception Object
	 * @return pretupsResponse return response in the form of pretups response
	 */
	@Override
	public Response toResponse(Exception exception) {
		final String methodName = "toResponse";
		PretupsResponse<JsonNode> pretupsResponse = new PretupsResponse<>();
		if (log.isDebugEnabled()) {
			log.debug("load", "Entered in RestAllException Handler");
		}

		pretupsResponse.setStatusCode(200);
		pretupsResponse.setStatus(false);
		

		Throwable cause = exception.getCause();
		StringWriter stringWriter = new StringWriter();
		if (cause != null) {
			try(PrintWriter printWriter = new PrintWriter(stringWriter);)
			{
			
			cause.printStackTrace(printWriter);
			pretupsResponse.setGlobalError(stringWriter.toString());
			}
			}else{
			pretupsResponse.setGlobalError(exception.getMessage());
		}
		
		if (log.isDebugEnabled()) {
			log.error("toResponse in RestAllExeptionHandler", "Exception exception=" + exception);
			log.errorTrace("toResponse", exception);
		}
		
		
		try {
			return Response.serverError().entity(new ObjectMapper().writeValueAsString(pretupsResponse)).build();
		} catch (JsonProcessingException e) {
			log.errorTrace(methodName, e);
		}
		return null;
		
	}

}
