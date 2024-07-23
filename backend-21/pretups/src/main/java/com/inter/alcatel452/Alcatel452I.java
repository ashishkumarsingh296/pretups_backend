package com.inter.alcatel452;

/*
 * Created on Jul 14, 2008
 * 
 * @author vinay.singh
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public interface Alcatel452I {
    public int ACTION_GET_ACCOUNT_INFO = 90; // Get Account Information
    public int ACTION_IMMEDIATE_DEBIT = 0; // IMMEDIATE_DEBIT request
    public int ACTION_IMMEDIATE_CREDIT = 1; // IMMEDIATE_CREDIT request
    public String RESULT_OK = "0";
    public String HTTP_STATUS_OK = "200";
    public String RESULT_ERROR_MALFORMED_REQUEST = "3"; // Request structure is
                                                        // not valid
    public String RESULT_ERROR_XML_PARSE = "9"; // Error during parser treatment
    public String RESULT_ERROR_ACC_NOT_FOUND = "201"; // The client/account was
                                                      // not found in the
                                                      // database.
}
