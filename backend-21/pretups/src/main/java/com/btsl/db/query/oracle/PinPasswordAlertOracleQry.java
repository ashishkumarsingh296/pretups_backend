package com.btsl.db.query.oracle;

import com.btsl.pretups.processes.PinPasswordAlertQry;

/**
 * PinPasswordAlertOracleQry
 * @author sadhan.k
 *
 */
public class PinPasswordAlertOracleQry implements PinPasswordAlertQry{

	@Override
	public String p2pPinAlert(int p_alertDays,int p2pChangePinDays) {
		 
		StringBuilder queryBuf = new StringBuilder("SELECT msisdn,trunc(nvl(pin_modified_on,activated_on)) pin_modified_on,language,country");
		 queryBuf.append(" FROM p2p_subscribers WHERE status='Y' AND ? + ").append(p_alertDays).append(" > trunc(nvl(pin_modified_on,activated_on)) +").append(p2pChangePinDays).append(" AND trunc(nvl(pin_modified_on,activated_on)) + ");
		 queryBuf.append(p2pChangePinDays).append("  > ?");
 
		return queryBuf.toString();
	}

	@Override
	public String c2sPinAlert() {
		
		StringBuilder queryBuf = new StringBuilder(
		            "SELECT U.category_code,U.network_code,UP.msisdn ,trunc(nvl(UP.pin_modified_on,UP.created_on)) pin_modified_on,UP.country ,UP.PHONE_language language");
		        queryBuf
		            .append(" FROM user_phones UP,users U,categories C WHERE U.USER_ID=UP.USER_ID AND U.status='Y' AND C.category_code=U.category_code AND C.sms_interface_allowed='Y'");
		       
		 return queryBuf.toString();
	}

	@Override
	public String channelPasswordAlert() {
		
		StringBuilder queryBuf = new StringBuilder(
	            "SELECT U.category_code,U.network_code, U.msisdn,trunc(nvl(U.pswd_modified_on,U.created_on)) pswd_modified_on,UP.country,UP.phone_language language");
	        queryBuf.append(" FROM user_phones UP,users U,categories C WHERE U.USER_ID=UP.USER_ID AND C.category_code=U.category_code ");
	        queryBuf.append(" AND U.msisdn=UP.msisdn AND U.user_type='CHANNEL' AND U.status='Y' AND C.web_interface_allowed='Y'");
	 
	      return queryBuf.toString();
	}

	@Override
	public String operatorPasswordAlert() {
		
		StringBuilder queryBuf = new StringBuilder("SELECT U.category_code,U.network_code, U.msisdn,trunc(nvl(U.pswd_modified_on,U.created_on)) pswd_modified_on");
        queryBuf.append(" FROM users U,categories C WHERE U.user_type='OPERATOR' AND U.status='Y' AND C.category_code=U.category_code AND C.web_interface_allowed='Y'");
       
        return queryBuf.toString();
	}

}
