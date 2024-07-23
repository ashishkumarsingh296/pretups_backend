package com.btsl.pretups.whitelist.businesslogic;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;

/**
 * @(#)DefaultParser.java
 *                        Copyright(c) 2006, Bharti Telesoft Int. Public Ltd.
 *                        All Rights Reserved
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Ashish Kumar May 16,2006 Initial Creation
 *                        ------------------------------------------------------
 *                        ------------------------------------------
 *                        This interface is implemented by the Parser class
 *                        that implement the functionality to generate
 *                        whiteListVO method
 */
public interface ParserI {
    public void generateWhiteListVO(WhiteListVO p_whiteListVO) throws BTSLBaseException;

    public HashMap parseHeaderInformation(String p_headerStr) throws BTSLBaseException;

    public void init(HashMap p_ntwkInterfaceServiceDetail) throws BTSLBaseException;
}
