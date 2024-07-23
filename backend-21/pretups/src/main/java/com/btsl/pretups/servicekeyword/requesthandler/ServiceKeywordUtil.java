package com.btsl.pretups.servicekeyword.requesthandler;

/*
 * @(#)ServiceKeywordUtil.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Sanjeev Sharma 21/05/2010 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2010 Comviva Pvt Ltd.
 */

import java.sql.Connection;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.receiver.RequestVO;

public abstract class ServiceKeywordUtil {

    abstract public void processRequest(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException, Exception;

    public void processTopUPValidationRequest(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException, Exception {
    }

    public void processTopUPCreditRequest(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException, Exception {
    }

}
