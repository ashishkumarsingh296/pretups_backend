package com.btsl.user.businesslogic;

import com.btsl.common.BaseResponse;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;


/**
 * Base class for token validation RequestVO(s)
 * 
 * @author akhilesh.mittal1
 *
 */
@Schema(description="OAuth Token Response")
public class OAuthTokenRes extends BaseResponse {
    
	@io.swagger.v3.oas.annotations.media.Schema(example ="", required= true, description= "This token will be used for validation" )
	private String token;
	@io.swagger.v3.oas.annotations.media.Schema(example ="", required= true, description= "Refresh Token" )
	private String refreshToken;
	//private String errorMsg;
	private Date serverTime;
	
	
	
	/*public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;

	}*/

	public Date getServerTime() {
		return serverTime;
	}

	public void setServerTime(Date serverTime) {
		this.serverTime = serverTime;

	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@Override
	public String toString() {
		return "OAuthTokenRes [token=" + token + ", refreshToken=" + refreshToken + "]";
	}

}
