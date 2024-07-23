package com.btsl.login;

public interface LoginQry {
	String loadUserDetailsQry();
	String loadUserDetailsByMsisdnOrLoginIdQry(String msisdn, String loginId);
	String loadUserLoanDetailsQry();
}
