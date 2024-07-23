package com.restapi.o2c.service;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;

@Service
public interface FOCApprovalServiceI {
	
	public BaseResponseMultiple processFOCApproval(FOCApprovalRequestVO focApprovalRequest, MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag) throws BTSLBaseException;

}
