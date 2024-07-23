package com.btsl.common;

import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.annotation.PretupsRestControllerAdvice;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;

import jakarta.servlet.http.HttpServletResponse;

/*
 * This class handles all type of exception at spring level
 */
@PretupsRestControllerAdvice
public class SpringExceptionHanlder extends ResponseEntityExceptionHandler {

	public static final Log log = LogFactory.getLog(SpringExceptionHanlder.class.getName());

	/**
	 * This method handles any exception
	 * @param ex Exception object
	 * @return String path of view where it wants to redirect
	 */
	@ExceptionHandler(BTSLBaseException.class)
	public BaseResponse handleBTSLBaseException(BTSLBaseException ex, HttpServletResponse httpServletResponse){
		BaseResponse response = new BaseResponse();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		log.error("Exception", ex);
		log.errorTrace("Exception", ex);
		String msg = RestAPIStringParser.getMessage(locale, ex.getMessageKey(), ex.getArgs());
		response.setMessageCode(ex.getMessageKey());
		response.setMessage(msg);
		if (Arrays.asList(PretupsI.OAUTHCODES).contains(ex.getMessage())) {
			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			httpServletResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
		} else {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		return response;
	}
	/**
	 * This method handles Exception exception
	 * @param ex Exception object
	 * @return Baseresponse with appropriate response code
	 */
	@ExceptionHandler(Exception.class)
	public BaseResponse handleAllException(Exception ex, HttpServletResponse httpServletResponse) {
		String randomUUID = UUID.randomUUID().toString().replace("-", "");
		log.error("Exception randomUUID: {} ", randomUUID, ex);
		log.errorTrace("Exception randomUUID: " + randomUUID, ex);

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String responseMessage = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GENERIC_ERROR, null);

		BaseResponse response = new BaseResponse();
		response.setMessage(randomUUID + " " + responseMessage);
		response.setMessageCode(PretupsErrorCodesI.GENERIC_ERROR);
		response.setStatus(HttpStatus.SC_BAD_REQUEST);

		httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);

		return response;
	}

}
