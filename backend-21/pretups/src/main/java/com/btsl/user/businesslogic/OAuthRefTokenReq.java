package com.btsl.user.businesslogic;



public class OAuthRefTokenReq {
    
	@io.swagger.v3.oas.annotations.media.Schema(required= true, example= "", description= "Refresh Tokken" )
	private String refreshToken;

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@Override
	public String toString() {
		return "OAuthRefTokenReq [refreshToken=" + refreshToken + "]";
	}

}
