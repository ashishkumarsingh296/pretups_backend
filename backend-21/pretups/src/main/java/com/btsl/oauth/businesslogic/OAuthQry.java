package com.btsl.oauth.businesslogic;

public interface OAuthQry {

	public String loadRefreshTokenQry(); 
	public String updateOAuthAccessTokenQry();
	public String updateOAuthRefreshTokenQry();
	public String generateNewTokenId();
	public String persistAccessToken();
	public String persistRefreshToken();
	public String persistOAuthUserInfo();
	public String validateTokenQry();
	public String validateTokenQuery();
	public String validateClient();
	public String updatetOAuthUserInfo();
	public String getAccessTokenId();
	public String deleteFromOAuthRefreshToken();
	public String deleteFromOAuthAccessToken();
	public String deleteFromUserLoginInfo();
	public String queryOAuthUserInfo();
}
