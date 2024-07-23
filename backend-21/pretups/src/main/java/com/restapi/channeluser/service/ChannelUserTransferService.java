package com.restapi.channeluser.service;

import java.sql.Connection;
import java.sql.SQLException;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserVO;

public interface ChannelUserTransferService {
   
	
	 /**
     * 
     * @param operatorUtili
     * @param response
     * @param responseSwag
     * @param requestVO
     * @return
     * @throws BTSLBaseException
     */
	 public void sendOtp(OperatorUtilI operatorUtili,BaseResponse response,HttpServletResponse responseSwag,ChannelUserTransferOtpRequestVO requestVO);
	 
	  /**
	   * 
	   * @param con
	   * @param response
	   * @param OTP
	   * @param msisdn
	   * @param responseSwag
	   */
     public void validateOTP(Connection con,BaseResponse response, String OTP,String msisdn,HttpServletResponse responseSwag);

     /**
      * 
      * @param con
      * @param mcomCon
      * @param channelUserVO
      * @param userVO
      * @param sessionUserVO
      * @param requestVO
      * @return
      * @throws BTSLBaseException
      * @throws SQLException
      */
	 public BaseResponse confirmTransferUser(Connection con,MComConnectionI mcomCon, ChannelUserVO channelUserVO,UserVO userVO,UserVO sessionUserVO,ConfimChannelUserTransferRequestVO requestVO) throws BTSLBaseException,SQLException;
}
