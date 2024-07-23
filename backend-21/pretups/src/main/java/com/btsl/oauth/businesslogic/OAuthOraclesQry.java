package com.btsl.oauth.businesslogic;

public class OAuthOraclesQry implements OAuthQry {

	/**
	 * 
	 * @return
	 */
	public String loadRefreshTokenQry() {

		return "SELECT * from oauth_refresh_token  WHERE TOKEN_ID = ? ";
	}

	public String updateOAuthAccessTokenQry() {

		return "UPDATE oauth_access_token SET TOKEN  = utl_raw.cast_to_raw(?), MODIFIED_ON = ?, TOKEN_ID = ? WHERE TOKEN_ID = ? ";
	}

	public String updateOAuthRefreshTokenQry() {
		return "UPDATE oauth_refresh_token SET TOKEN  = utl_raw.cast_to_raw(?), MODIFIED_ON = ? , TOKEN_ID = ? WHERE TOKEN_ID = ? ";
	}

	public String generateNewTokenId() {
		return "select max(token_id) from oauth_access_token ";
	}

	public String persistAccessToken() {
		return "INSERT INTO oauth_access_token ( token , scope, created_on, modified_on, expires, client_id, token_id )  VALUES (utl_raw.cast_to_raw(?),?,?,?,?,?,?)";
	}

	public String persistRefreshToken() {
		return "INSERT INTO oauth_refresh_token ( token , created_on, modified_on,expires, token_id ) VALUES (utl_raw.cast_to_raw(?),?,?,?,?)";
	}

	public String persistOAuthUserInfo() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("INSERT INTO oauth_users_login_info (LOGIN_ID," + "MSISDN," + "REQUEST_GATEWAY_TYPE,"
				+ "REQUEST_GATEWAY_CODE," + "REQUEST_GATEWAY_LOGIN_ID," + "SERVICE_PORT," +

				"REQUEST_GATEWAY_PASSWORD," + "SOURCE_TYPE," + "CREATED_ON," + "MODIFIED_ON," +

				"EXT_CODE, USER_ID, TOKEN_ID");

		strBuff.append(" ) ");
		strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");

		return strBuff.toString();
	}
	
	public String queryOAuthUserInfo() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT  LOGIN_ID," + "MSISDN," + "REQUEST_GATEWAY_TYPE,"
				+ "REQUEST_GATEWAY_CODE," + "REQUEST_GATEWAY_LOGIN_ID," + "SERVICE_PORT," +

				"REQUEST_GATEWAY_PASSWORD," + "SOURCE_TYPE," + "CREATED_ON," + "MODIFIED_ON," +

				"EXT_CODE, USER_ID, TOKEN_ID");

		strBuff.append(" from oauth_users_login_info ");
		strBuff.append(" WHERE TOKEN_ID = ? ");

		return strBuff.toString();
	}
	
	public String validateTokenQry() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(
				"SELECT expires, modified_on from oauth_access_token where token_id = ? ");

		return strBuff.toString();
	}
	
	public String validateTokenQuery() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT " + "ou.request_gateway_type," + "ou.request_gateway_code,"
				+ "ou.request_gateway_login_id," + "ou.service_port," + "ou.request_gateway_password,"
				+ "ou.source_type," + "ou.login_id," + "ou.msisdn," + "ou.ext_code," + "ou.user_id,"
				+ "oat.modified_on, oat.expires "
				+ "from oauth_access_token  oat, oauth_users_login_info ou where oat.token_id = ou.token_id and oat.token_id = ? ");
	
		
		return strBuff.toString();
	}
	
	public String validateClient() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(
				"select access_token_validity, refresh_token_validity from oauth_client_details where client_id = ? and client_secret = ? AND scope = ? ");

		return strBuff.toString();
	}
	
	
	public String updatetOAuthUserInfo() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("UPDATE oauth_users_login_info SET MODIFIED_ON = ?, TOKEN_ID = ? ");
		strBuff.append(" WHERE TOKEN_ID = ?");

		return strBuff.toString();
	}
	
	public String getAccessTokenId() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT OAT.TOKEN_ID  FROM \r\n" + 
				"  OAUTH_USERS_LOGIN_INFO OULI,\r\n" + 
				"  OAUTH_ACCESS_TOKEN OAT\r\n" + 
				"  \r\n" + 
				"	 WHERE  OULI.TOKEN_ID = OAT.TOKEN_ID\r\n" + 
				"   AND OULI.LOGIN_ID=?  "); // + 
				//"   AND ((OAT.modified_on + numtodsinterval(OAT.expires,'SECOND'))  > systimestamp )  ORDER BY OAT.modified_on ASC  ");
		
		return strBuff.toString();
	}
	public String deleteFromOAuthRefreshToken() {
		StringBuilder strBuff = new StringBuilder();
		//strBuff.append("DELETE FROM oauth_refresh_token WHERE TOKEN_ID = ? ");
		
		strBuff.append("DELETE FROM oauth_refresh_token WHERE TOKEN_ID IN (SELECT OAT.TOKEN_ID  FROM \r\n" + 
				"		 OAUTH_USERS_LOGIN_INFO OULI,\r\n" + 
				"		  OAUTH_ACCESS_TOKEN OAT \r\n" + 
				"\r\n" + 
				"	 WHERE  OULI.TOKEN_ID = OAT.TOKEN_ID \r\n" + 
				"		   AND OULI.LOGIN_ID=?  ) ");
		
		
		
		   
		   
		return strBuff.toString();
	}
	public String deleteFromOAuthAccessToken() {
		StringBuilder strBuff = new StringBuilder();
		//strBuff.append("DELETE FROM oauth_access_token WHERE TOKEN_ID = ? ");
		strBuff.append("DELETE FROM oauth_access_token WHERE TOKEN_ID IN(SELECT OAT.TOKEN_ID  FROM \r\n" + 
				"				 OAUTH_USERS_LOGIN_INFO OULI,\r\n" + 
				"				  OAUTH_ACCESS_TOKEN OAT \r\n" + 
				"      \r\n" + 
				"      	 WHERE  OULI.TOKEN_ID = OAT.TOKEN_ID \r\n" + 
				"				   AND OULI.LOGIN_ID=?) ");
		
		
		return strBuff.toString();
	}
	public String deleteFromUserLoginInfo() {
		StringBuilder strBuff = new StringBuilder();
		//strBuff.append("DELETE FROM oauth_users_login_info WHERE TOKEN_ID = ? ");
		strBuff.append("DELETE FROM oauth_users_login_info WHERE LOGIN_ID=? ");
		
		return strBuff.toString();
		
	}
	
	
}
