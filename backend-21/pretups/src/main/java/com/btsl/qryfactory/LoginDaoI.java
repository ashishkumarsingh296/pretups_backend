package com.btsl.qryfactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;

public interface LoginDaoI {
  
    public  ChannelUserVO loadUserDetails(java.sql.Connection p_con, String p_loginID, String p_password, Locale locale) throws SQLException, Exception ;

    public int updateUserLoginDetails(java.sql.Connection p_con, UserVO p_userVO) throws SQLException, Exception ;

    public int updatePasswordCounter(Connection p_con, UserVO p_userVO) throws BTSLBaseException ;

    public ChannelUserVO loadUserDetailsByMsisdnOrLoginId(java.sql.Connection p_con, String p_msisdn, String p_loginId, String p_password, Locale locale) throws SQLException, Exception ;
 
	public GradeVO loadUserDetailsOnTwoFAallowed(java.sql.Connection p_con, String userid) throws SQLException, Exception;
    
}
