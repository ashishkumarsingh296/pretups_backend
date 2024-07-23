package com.restapi.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
@Service
public interface SuspendResumeServiceI {

	SuspendResumeResponse processRequest(SuspendResumeUserVo requestVO,HttpServletRequest httprequest, MultiValueMap<String, String> headers, HttpServletResponse responseSwag) 
			throws BTSLBaseException 
	;
	SuspendResumeResponse processRequestStaff(SuspendResumeUserVo requestVO,HttpServletRequest httprequest, MultiValueMap<String, String> headers, HttpServletResponse responseSwag) 
			throws BTSLBaseException 
	;
	
}
