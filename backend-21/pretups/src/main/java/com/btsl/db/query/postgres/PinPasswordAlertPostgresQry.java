package com.btsl.db.query.postgres;

import com.btsl.pretups.processes.PinPasswordAlertQry;

/**
 * PinPasswordAlertPostgresQry
 * @author sadhan.k
 *
 */
public class PinPasswordAlertPostgresQry implements PinPasswordAlertQry {

	@Override
	public String p2pPinAlert(int p_alertDays,int p2pChangePinDays) {
		StringBuilder queryBuf = new StringBuilder("SELECT msisdn,date_trunc('day',coalesce(pin_modified_on,activated_on)::timestamp) pin_modified_on,language,country");
		 queryBuf.append(" FROM p2p_subscribers WHERE status='Y' AND ?::timestamp +  interval '");
		 queryBuf.append(p_alertDays);
		 queryBuf.append("' day > date_trunc('day',coalesce(pin_modified_on,activated_on)::timestamp) + interval '");
		 queryBuf.append(p2pChangePinDays);
		 queryBuf.append("' day AND date_trunc('day',coalesce(pin_modified_on,activated_on)::timestamp) ");
		 queryBuf.append(" + interval '");
		 queryBuf.append(p2pChangePinDays);
		 queryBuf.append("' day > ?::timestamp");

		return queryBuf.toString();
	}

	@Override
	public String c2sPinAlert() {
		
		StringBuilder queryBuf = new StringBuilder(
		            "SELECT U.category_code,U.network_code,UP.msisdn ,date_trunc('day',coalesce(UP.pin_modified_on,UP.created_on)::timestamp) pin_modified_on,UP.country ,UP.PHONE_LANGUAGE as language");
		        queryBuf
		            .append(" FROM user_phones UP,users U,categories C WHERE U.USER_ID=UP.USER_ID AND U.status='Y' AND C.category_code=U.category_code AND C.sms_interface_allowed='Y'");
		       
		 return queryBuf.toString();
	}

	@Override
	public String channelPasswordAlert() {
		StringBuilder queryBuf = new StringBuilder(
	            "SELECT U.category_code,U.network_code, U.msisdn,date_trunc('day',coalesce(U.pswd_modified_on,U.created_on)::timestamp) pswd_modified_on,UP.country,UP.PHONE_LANGUAGE as language ");
	        queryBuf.append(" FROM user_phones UP,users U,categories C WHERE U.USER_ID=UP.USER_ID AND C.category_code=U.category_code ");
	        queryBuf.append(" AND U.msisdn=UP.msisdn AND U.user_type='CHANNEL' AND U.status='Y' AND C.web_interface_allowed='Y'");
	 
	      return queryBuf.toString();
	}

	@Override
	public String operatorPasswordAlert() {
		
		StringBuilder queryBuf = new StringBuilder("SELECT U.category_code,U.network_code, U.msisdn,date_trunc('day',coalesce(U.pswd_modified_on,U.created_on)::timestamp) pswd_modified_on");
        queryBuf.append(" FROM users U,categories C WHERE U.user_type='OPERATOR' AND U.status='Y' AND C.category_code=U.category_code AND C.web_interface_allowed='Y'");
       
        return queryBuf.toString();
	}

}
