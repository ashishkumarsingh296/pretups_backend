package com.btsl.user.businesslogic;



public class OAuthRefTokenRequest {
    
	@io.swagger.v3.oas.annotations.media.Schema(required= true, example= "", description= "Refresh Tokken" )
	private String refreshToken;

    @io.swagger.v3.oas.annotations.media.Schema(example="JSON", description="Client Id", required=true, hidden = true)
	private String clientId;
    
    @io.swagger.v3.oas.annotations.media.Schema(example="JSON", description="Client Secret", required=true, hidden = true)
	private String clientSecret;
    
    @io.swagger.v3.oas.annotations.media.Schema(example="JSON", description="Scope", required=true, hidden = true)
	private String scope;

    
    
    
	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	
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

	
	
	
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	public String toString() {
		return "OAuthRefTokenReq [refreshToken=" + refreshToken + "]";
	}

}
