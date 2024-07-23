package com.restapi.user.service;

import java.sql.Connection;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.user.businesslogic.UserVO;

public interface BarUnbarUserService {

	 public BarUnbarResponseVO addBarredUser(BarUnbarRequestVO barUnbarRequestVO,UserVO userVO) throws BTSLBaseException; 
	 public BarUnbarResponseVO unBarredUser(BarUnbarRequestVO barUnbarRequestVO,UserVO userVO) throws BTSLBaseException;
	 public void vaidateBarUserRequest(Connection con,BarUnbarRequestVO barUnbarRequestVO, ErrorMap errorMap,UserVO userVO) throws BTSLBaseException;
	 public void processGetUserInfoForBarring(Connection con , String msisdn , UserVO userVO , BarUserInfoResponseVO barUserInfoResponseVO) throws BTSLBaseException;
}
