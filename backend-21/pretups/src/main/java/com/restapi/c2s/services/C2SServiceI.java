package com.restapi.c2s.services;

import java.sql.Connection;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.user.businesslogic.UserVO;

@Service
public interface C2SServiceI {
	
	public void loadDenomination(MvdDenominationResponseVO response)  throws BTSLBaseException;
	public  DvdApiResponse processRequestDVD(DvdSwaggRequestVO requestVO, String requestIDStr ,MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag, HttpServletRequest httpServletRequest)  throws BTSLBaseException;
	public List<ChannelTransferVO> getReversalList(Connection p_con, UserVO uservo,String senderMsisdn, String receiversisdn, String txnId) throws Exception;
	public  GetUserServiceBalanceResponseVO processRequest(String serviceName, MultiValueMap<String, String> headers, HttpServletResponse responseSwag)  throws BTSLBaseException;
	public UserWidgetResponse processUserWiget(UserWidgetRequestVO requestVO, MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag, HttpServletRequest httpServletRequest);
}
