package com.btsl.user.businesslogic;

import io.swagger.v3.oas.annotations.media.Schema;


/**
 * Base class for token validation RequestVO(s)
 * 
 * @author akhilesh.mittal1
 *
 */

public class OAuthTokenInternalRequest {
	
   	private String reqGatewayType;
    private String reqGatewayCode;
    private String reqGatewayLoginId;
    private String reqGatewayPassword;
    private String servicePort;
    
    
    public String getReqGatewayType() {
		return reqGatewayType;
	}

	public void setReqGatewayType(String reqGatewayType) {
		this.reqGatewayType = reqGatewayType;
	}

	public String getReqGatewayCode() {
		return reqGatewayCode;
	}

	public void setReqGatewayCode(String reqGatewayCode) {
		this.reqGatewayCode = reqGatewayCode;
	}

	public String getReqGatewayLoginId() {
		return reqGatewayLoginId;
	}

	public void setReqGatewayLoginId(String reqGatewayLoginId) {
		this.reqGatewayLoginId = reqGatewayLoginId;
	}

	public String getReqGatewayPassword() {
		return reqGatewayPassword;
	}

	public void setReqGatewayPassword(String reqGatewayPassword) {
		this.reqGatewayPassword = reqGatewayPassword;
	}


	public String getServicePort() {
		return servicePort;
	}

	public void setServicePort(String servicePort) {
		this.servicePort = servicePort;
	}


}
