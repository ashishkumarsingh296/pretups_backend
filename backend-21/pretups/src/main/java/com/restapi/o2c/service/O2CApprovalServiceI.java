package com.restapi.o2c.service;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;

@Service
public interface O2CApprovalServiceI {

	public BaseResponseMultiple processO2CStockApproval(O2CStockAppRequestVO o2cStockAppRequest, MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag) throws BTSLBaseException;

	O2CApprovalTxnDetailsResponseVO transferApprovalDetails(MultiValueMap<String, String> headers,
			String transactionID, HttpServletResponse responseSwag);

}
