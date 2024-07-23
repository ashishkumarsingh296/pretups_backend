package com.btsl.pretups.gateway.util;

/*
 * @(#)GatewayParsersI.java
 * Copyright(c) 2005, Bharti Telesoft Ltd.
 * All Rights Reserved
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * Gurjeet Singh Nov 04, 2005 Initital Creation
 * Ashish K.Todia May 17, 2012 Modification.
 * ------------------------------------------------------------------------------
 * -------------------
 * Parser class to handle request from various message gateways
 */

import java.sql.Connection;

import jakarta.servlet.http.HttpServletRequest;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;

public interface GatewayParsersI {

    // This method must set decrypted message in request VO
    // And also _requestMSISDN if not set already
    public void parseRequestMessage(RequestVO p_requestVO) throws BTSLBaseException;

	public default void parseRequestMessage(RequestVO p_requestVO,HttpServletRequest p_request) throws BTSLBaseException{
		
	}
    public void generateResponseMessage(RequestVO p_requestVO);

    public void parseChannelRequestMessage(RequestVO p_requestVO, Connection pCon) throws BTSLBaseException;

    public void generateChannelResponseMessage(RequestVO p_requestVO);

    public void validateUserIdentification(RequestVO p_requestVO) throws BTSLBaseException;

    public void loadValidateNetworkDetails(RequestVO p_requestVO) throws BTSLBaseException;

    public ChannelUserVO loadValidateUserDetails(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException;

    public void checkRequestUnderProcess(Connection p_con, RequestVO p_requestVO, String p_module, boolean p_mark, ChannelUserVO p_channelUserVO) throws BTSLBaseException;

    public void parseChannelRequestMessage(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException;

    public void parseOperatorRequestMessage(RequestVO p_requestVO) throws BTSLBaseException; // For
    // Operator
    // user
    // request.
}
