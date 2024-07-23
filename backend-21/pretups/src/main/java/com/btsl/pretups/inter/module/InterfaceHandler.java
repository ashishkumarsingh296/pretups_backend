package com.btsl.pretups.inter.module;

/**
 * @(#)InterfaceHandler.java
 *                           Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                           All Rights Reserved
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Abhijit Chauhan June 22,2005 Initial Creation
 *                           --------------------------------------------------
 *                           ----------------------------------------------
 */
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;

public interface InterfaceHandler {
    public void validate(HashMap<String, String> p_map) throws BTSLBaseException, Exception;

    public void credit(HashMap<String, String> p_map) throws BTSLBaseException, Exception;

    public void creditAdjust(HashMap<String, String> p_map) throws BTSLBaseException, Exception;

    public void debitAdjust(HashMap<String, String> p_map) throws BTSLBaseException, Exception;

    public void validityAdjust(HashMap<String, String> p_map) throws BTSLBaseException, Exception;
}
