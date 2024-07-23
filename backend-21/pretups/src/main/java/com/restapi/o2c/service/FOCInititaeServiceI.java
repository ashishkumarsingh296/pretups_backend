package com.restapi.o2c.service;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BaseResponseMultiple;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public interface FOCInititaeServiceI {
	
	public BaseResponseMultiple<JsonNode> processFocInitiateRequest(FocInitiateRequestVO focInitiateRequestVO, HttpServletResponse response1) ;

}
