package com.utils;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.commons.MasterI;

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

public final class GenerateToken {

	private GenerateToken() {}

	private static String accessToken;
	private static Instant expiryTime;
	private static String accessTokenChnlUsr;
	private static Instant chnlUsrAccessTokenexpiryTime;

	static OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
	static OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	static Map<String, Object> headerMap = new HashMap<String, Object>();

	public static void setHeaders() {
		headerMap.put("CLIENT_ID", _masterVO.getProperty("CLIENT_ID"));
		headerMap.put("CLIENT_SECRET", _masterVO.getProperty("CLIENT_SECRET"));
		headerMap.put("requestGatewayCode", _masterVO.getProperty("requestGatewayCode"));
		headerMap.put("requestGatewayLoginId", _masterVO.getProperty("requestGatewayLoginID"));
		headerMap.put("requestGatewayPsecure", _masterVO.getProperty("requestGatewayPasswordVMS"));
		headerMap.put("requestGatewayType", _masterVO.getProperty("requestGatewayType"));
		headerMap.put("scope", _masterVO.getProperty("scope"));
		headerMap.put("servicePort", _masterVO.getProperty("servicePort"));
	}

	public static void setupAuth(String username, String password) {
		oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
		oAuthenticationRequestPojo.setIdentifierValue(username);
		oAuthenticationRequestPojo.setPasswordOrSmspin(password);
	}

	public static String getAcccessToken(String username, String password) {
		Log.info("Credentials used for generating token: " + "LoginId: " + username + " Password: " + password);
		try {
			if (accessToken == null || Instant.now().isAfter(expiryTime)) {
				setHeaders();
				setupAuth(username, password);
				OAuthenticationAPI oAuthenticationAPI = new OAuthenticationAPI(
						_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), headerMap);
				oAuthenticationAPI.setContentType(_masterVO.getProperty("contentType"));
				oAuthenticationAPI.addBodyParam(oAuthenticationRequestPojo);
				oAuthenticationAPI.setExpectedStatusCode(200);
				oAuthenticationAPI.perform();
				try {
					oAuthenticationResponsePojo = oAuthenticationAPI
							.getAPIResponseAsPOJO(OAuthenticationResponsePojo.class);
				} catch (IOException e) {
					e.printStackTrace();
				}
				expiryTime = Instant.now().plusSeconds(Long.parseLong(_masterVO.getProperty("tokenExpirySeconds")));
				accessToken = oAuthenticationResponsePojo.getToken();
			} else {
				Log.info("Token is good to use and has not expired yet!!");
			}
		} catch (Exception e) {
			throw new RuntimeException("ABORT!! Error generating token.");
		}
		return accessToken;
	}

	public static String getAcccessTokenForChannelUser(String username, String password) {
		Log.info("Credentials used for generating token: " + "LoginId: " + username + " Password: " + password);
		try {
			if (accessTokenChnlUsr == null || Instant.now().isAfter(chnlUsrAccessTokenexpiryTime)) {
				setHeaders();
				setupAuth(username, password);
				OAuthenticationAPI oAuthenticationAPI = new OAuthenticationAPI(
						_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), headerMap);
				oAuthenticationAPI.setContentType(_masterVO.getProperty("contentType"));
				oAuthenticationAPI.addBodyParam(oAuthenticationRequestPojo);
				oAuthenticationAPI.setExpectedStatusCode(200);
				oAuthenticationAPI.perform();
				try {
					oAuthenticationResponsePojo = oAuthenticationAPI
							.getAPIResponseAsPOJO(OAuthenticationResponsePojo.class);
				} catch (IOException e) {
					e.printStackTrace();
				}
				chnlUsrAccessTokenexpiryTime = Instant.now().plusSeconds(Long.parseLong(_masterVO.getProperty("tokenExpirySeconds")));
				accessTokenChnlUsr = oAuthenticationResponsePojo.getToken();
			} else {
				Log.info("Token is good to use and has not expired yet!!");
			}
		} catch (Exception e) {
			throw new RuntimeException("ABORT!! Error generating token.");
		}
		return accessTokenChnlUsr;
	}
}
