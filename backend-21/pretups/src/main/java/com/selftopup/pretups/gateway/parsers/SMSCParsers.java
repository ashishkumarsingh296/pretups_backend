package com.selftopup.pretups.gateway.parsers;

import java.sql.Connection;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.pretups.gateway.util.ParserUtility;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;

public class SMSCParsers extends ParserUtility {

    public void parseRequestMessage(RequestVO p_requestVO) throws BTSLBaseException {
        p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
    }

    public void generateResponseMessage(RequestVO p_requestVO) {
        String message = null;
        if (!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage()))
            message = p_requestVO.getSenderReturnMessage();
        else
            message = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());

        p_requestVO.setSenderReturnMessage(message);
    }

    public void parseChannelRequestMessage(RequestVO p_requestVO) throws BTSLBaseException {
    }

    public void generateChannelResponseMessage(RequestVO p_requestVO) {
    }

    public void parseChannelRequestMessage(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
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

    public void loadValidateNetworkDetails(RequestVO p_requestvo) throws BTSLBaseException {
        // TODO Auto-generated method stub

    }

    public void validateUserIdentification(RequestVO p_requestvo) throws BTSLBaseException {
        // TODO Auto-generated method stub

    }

}
