package com.btsl.pretups.gateway.parsers;

/*
 * @(#)IVRParsers.java
 * Copyright(c) 2005, Bharti Telesoft Ltd.
 * All Rights Reserved
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * Gurjeet Singh Nov 04, 2005 Initital Creation
 * ------------------------------------------------------------------------------
 * -------------------
 * Parser class to handle IVR requests
 */

import java.sql.Connection;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class IVRParsers extends ParserUtility {

    public void parseRequestMessage(RequestVO p_requestVO) throws BTSLBaseException {
        p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());

    }

    public void generateResponseMessage(RequestVO p_requestVO) {
        // TODO Auto-generated method stub

    }

    public void parseChannelRequestMessage(RequestVO p_requestVO, Connection pCon) throws BTSLBaseException {
        p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
        ChannelUserBL.updateUserInfo(pCon, p_requestVO);

    }

    public void generateChannelResponseMessage(RequestVO p_requestVO) {
        // TODO Auto-generated method stub

    }

    public void parseChannelRequestMessage(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
        updateUserInfo(p_con, p_requestVO);

    }

    /**
     * Method to parse the Operator request
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void parseOperatorRequestMessage(RequestVO p_requestVO) throws BTSLBaseException {
        p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
        if (!BTSLUtil.isNullString(p_requestVO.getRequestMSISDN())) {
            String filteredMsisdn = PretupsBL.getFilteredMSISDN(p_requestVO.getRequestMSISDN());
            p_requestVO.setFilteredMSISDN(filteredMsisdn);
            p_requestVO.setMessageSentMsisdn(filteredMsisdn);
        }
    }
}
