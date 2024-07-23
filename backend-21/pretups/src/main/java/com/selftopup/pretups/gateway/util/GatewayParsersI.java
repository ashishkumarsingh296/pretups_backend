package com.selftopup.pretups.gateway.util;

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

import com.selftopup.common.BTSLBaseException;
import com.selftopup.pretups.receiver.RequestVO;

public interface GatewayParsersI {

    // This method must set decrypted message in request VO
    // And also _requestMSISDN if not set already
    public void parseRequestMessage(RequestVO p_requestVO) throws BTSLBaseException;

    public void generateResponseMessage(RequestVO p_requestVO);

    public void validateUserIdentification(RequestVO p_requestVO) throws BTSLBaseException;

    public void loadValidateNetworkDetails(RequestVO p_requestVO) throws BTSLBaseException;

    public void parseOperatorRequestMessage(RequestVO p_requestVO) throws BTSLBaseException; // For
                                                                                             // Operator
                                                                                             // user
                                                                                             // request.
}
