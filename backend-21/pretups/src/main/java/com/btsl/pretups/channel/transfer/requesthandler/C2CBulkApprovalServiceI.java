package com.btsl.pretups.channel.transfer.requesthandler;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.user.businesslogic.OAuthUser;
import com.restapi.o2c.service.C2CBulkApprovalRequestVO;

@Service
public interface C2CBulkApprovalServiceI {
	
	public BaseResponseMultiple processc2cBulkApproval(MultiValueMap<String, String> headers, C2CBulkApprovalRequestVO c2cBulkApprovalRequestVO,
			HttpServletResponse responseSwag);
	
	public void loadAllC2cBulkApprovalDetails(OAuthUser oAuthUserData, C2cBatchesApprovalDetailsVO response,
			HttpServletResponse responseSwag, String category) throws BTSLBaseException, Exception;
	
	

}
