package com.restapi.c2cbulk;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.restapi.c2s.services.C2SBulkRechargeRequestVO;

@Service
public interface C2CBulkServiceI {

	
	public C2CProcessBulkApprovalResponseVO processC2cBulkTrfAppProcess(MultiValueMap<String, String> headers, HttpServletResponse responseSwag,C2CProcessBulkRequestVO req,HttpServletRequest httprequest) ;

}
