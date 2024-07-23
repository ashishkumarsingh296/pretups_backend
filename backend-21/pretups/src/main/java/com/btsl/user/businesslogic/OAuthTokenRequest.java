package com.btsl.user.businesslogic;

import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Base class for token validation RequestVO(s)
 * 
 * @author akhilesh.mittal1
 *
 */
@Schema(name= "Authentication Token API VO" )
public class OAuthTokenRequest {
	
	@io.swagger.v3.oas.annotations.media.Schema(example="loginid/msisdn/externalcode", description="loginid/msisdn/externalcode", required=true)
	private String identifierType;

	@io.swagger.v3.oas.annotations.media.Schema(example="superadmin", description="superadmin", required=true)
	private String identifierValue;
	
	
	@io.swagger.v3.oas.annotations.media.Schema(example="Admin", description="Admin", required=true)
	private String passwordOrSmspin;


	public String getIdentifierType() {
		return identifierType;
	}


	public void setIdentifierType(String identifierType) {
		this.identifierType = identifierType;
	}


	public String getIdentifierValue() {
		return identifierValue;
	}


	public void setIdentifierValue(String identifierValue) {
		this.identifierValue = identifierValue;
	}


	public String getPasswordOrSmspin() {
		return passwordOrSmspin;
	}


	public void setPasswordOrSmspin(String passwordOrSmspin) {
		this.passwordOrSmspin = passwordOrSmspin;
	}
	
	
	
	
	
	/*
    @io.swagger.v3.oas.annotations.media.Schema(example="ydist", description="Login Id", required=true)
	private String loginId;
    
    @io.swagger.v3.oas.annotations.media.Schema(example="72525252", description="Msisdn", required=true)
	private String msisdn;
    
    @io.swagger.v3.oas.annotations.media.Schema(example="2468", description="Password", required=true,  dataType="String" )
	private String password;
    @io.swagger.v3.oas.annotations.media.Schema(example="1357", description="Pin", required=true)
	private String pin;
    */
    //@io.swagger.v3.oas.annotations.media.Schema(example="REST", description="Request Gateway Type", required=true, hidden = true)
	@io.swagger.v3.oas.annotations.media.Schema(example = "REST", description = "Request Gateway Type", required = true, hidden = true)
	private String reqGatewayType;
    
    @io.swagger.v3.oas.annotations.media.Schema(example="REST", description="Request Gateway Code", required=true, hidden = true)
	private String reqGatewayCode;
    
    @io.swagger.v3.oas.annotations.media.Schema(example="pretups", description="Request Gateway Login Id", required=true, hidden = true)
	private String reqGatewayLoginId;
    
    @io.swagger.v3.oas.annotations.media.Schema(example="1357", description="Request Gateway Password", required=true, hidden = true)
	private String reqGatewayPassword;
    
    @io.swagger.v3.oas.annotations.media.Schema(example="190", description="Service port", required=true, hidden = true)
	private String servicePort;
    
    @io.swagger.v3.oas.annotations.media.Schema(example="JSON", description="Client Id", required=true, hidden = true)
	private String clientId;
    
    @io.swagger.v3.oas.annotations.media.Schema(example="JSON", description="Client Secret", required=true, hidden = true)
	private String clientSecret;
    
    @io.swagger.v3.oas.annotations.media.Schema(example="JSON", description="Scope", required=true, hidden = true)
	private String scope;
    
    
    
    public String getScope() {
		return scope;
	}


	public void setScope(String scope) {
		this.scope = scope;
	}





	@io.swagger.v3.oas.annotations.media.Schema(example="JSON", description="Source Type", required=true, hidden = true)
	private String sourceType;
    
    
    
    /*
    @io.swagger.v3.oas.annotations.media.Schema(example="ABC", description="External Code", required=true)
	private String extCode;*/

    public String getClientId() {
		return clientId;
	}


	public void setClientId(String clientId) {
		this.clientId = clientId;
	}


	public String getClientSecret() {
		return clientSecret;
	}


	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
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



}
