/*
 * Created on Apr 13, 2007
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.postonline;

import java.sql.Connection;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;

/**
 * @(#)DBPoolManager.java
 *                        Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *                        All Rights Reserved
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Ashish Kumar Apr 13, 2007 Initial Creation
 *                        ------------------------------------------------------
 *                        ------------------------------------------
 * 
 *                        This interface defines the various methods that is is
 *                        to be implemented by the utility class
 *                        for validate, credit, creditAdjust and debitAdjust.
 * 
 */
public interface InnerClassI {
    public void validate(Connection p_con, HashMap p_requestMap) throws BTSLBaseException, Exception;

    public void credit(Connection p_con, HashMap p_requestMap) throws BTSLBaseException, Exception;

    public void creditAdjust(Connection p_con, HashMap p_requestMap) throws BTSLBaseException, Exception;

    public void debitAdjust(Connection p_con, HashMap p_requestMap) throws BTSLBaseException, Exception;
}
