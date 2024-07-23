package com.btsl.user.businesslogic;

import io.swagger.v3.oas.annotations.media.Schema;


/**
 * Base class for token validation RequestVO(s)
 * 
 * @author akhilesh.mittal1
 *
 */
@Schema(name= "Authentication Token VO" )
public class OAuthTokenReq {
    @io.swagger.v3.oas.annotations.media.Schema(example="ydist", description="Login Id", required=true)
	private String loginId;
    
    @io.swagger.v3.oas.annotations.media.Schema(example="72525252", description="Msisdn", required=true)
	private String msisdn;
    
    @io.swagger.v3.oas.annotations.media.Schema(example="2468", description="Password", required=true/* ,  dataType="String"  */)
	private String password;
    @io.swagger.v3.oas.annotations.media.Schema(example="1357", description="Pin", required=true)
	private String pin;
    
    @io.swagger.v3.oas.annotations.media.Schema(example="REST", description="Request Gateway Type", required=true)
	private String reqGatewayType;
    
    @io.swagger.v3.oas.annotations.media.Schema(example="REST", description="Request Gateway Code", required=true)
	private String reqGatewayCode;
    
    @io.swagger.v3.oas.annotations.media.Schema(example="pretups", description="Request Gateway Login Id", required=true)
	private String reqGatewayLoginId;
    
    @io.swagger.v3.oas.annotations.media.Schema(example="1357", description="Request Gateway Password", required=true)
	private String reqGatewayPassword;
    
    @io.swagger.v3.oas.annotations.media.Schema(example="190", description="Service port", required=true)
	private String servicePort;
    
    @io.swagger.v3.oas.annotations.media.Schema(example="JSON", description="Source Type", required=true)
	private String sourceType;
    
    @io.swagger.v3.oas.annotations.media.Schema(example="ABC", description="External Code", required=true)
	private String extCode;

	public String getExtCode() {
		return extCode;
	}

	public void setExtCode(String extCode) {
		this.extCode = extCode;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

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

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getServicePort() {
		return servicePort;
	}

	public void setServicePort(String servicePort) {
		this.servicePort = servicePort;
	}

	@Override
	public String toString() {
		return "OAuthTokenReq [loginId=" + loginId + ", msisdn=" + msisdn + ", password=" + password + ", pin=" + pin
				+ ", reqGatewayType=" + reqGatewayType + ", reqGatewayCode=" + reqGatewayCode + ", reqGatewayLoginId="
				+ reqGatewayLoginId + ", reqGatewayPassword=" + reqGatewayPassword + ", servicePort=" + servicePort
				+ ", sourceType=" + sourceType + ", extCode=" + extCode + "]";
	}

}
