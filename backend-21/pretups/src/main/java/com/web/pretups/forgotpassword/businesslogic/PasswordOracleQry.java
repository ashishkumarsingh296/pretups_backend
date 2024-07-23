package com.web.pretups.forgotpassword.businesslogic;

public class PasswordOracleQry implements PasswordQry {
	
	/**
	 * Method loadUserDetailsQry
	 * 
	 * @return query in the form of String.
	 */
	@Override
	public String loadUserDetailsQry() {
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("select u.email,u.msisdn,u.network_code,u.user_name,u.user_id,up.country,up.phone_language from users u, user_phones up ");
		//strBuff.append("where u.login_id = ?  and   u.user_id = up.user_id(+)");
		strBuff.append("where upper(u.login_id) = upper(?)  and   u.user_id = up.user_id(+) AND up.PRIMARY_NUMBER(+)='Y' ");
		return strBuff.toString();
	}
	
	/**
	 * Method checkPasswordHistoryQry
	 * 
	 * @return query in the form of String.
	 */
	@Override
	public String checkPasswordHistoryQry(){
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT pin_or_password,modified_on FROM (SELECT pin_or_password,modified_on,  row_number()  over (ORDER BY modified_on DESC) rn  ");
        strBuff.append(" FROM pin_password_history WHERE user_id=? )  WHERE rn <= ? ");
        strBuff.append(" ORDER BY modified_on DESC ");
        return strBuff.toString();
	}

}
