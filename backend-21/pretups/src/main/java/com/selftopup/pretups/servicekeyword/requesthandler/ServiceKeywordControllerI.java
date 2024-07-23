package com.selftopup.pretups.servicekeyword.requesthandler;

/*
 * @(#)ServiceKeywordControllerI.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 10/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */
import java.sql.Connection;

import com.selftopup.pretups.receiver.RequestVO;

public interface ServiceKeywordControllerI {
    /**
     * Process
     * 
     * @param p_con
     * @param p_requestVO
     * @return void
     */
    public void process(RequestVO p_requestVO);
}
