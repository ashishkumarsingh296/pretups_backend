package com.inter.mobinilvoms;

/*
 * @(#)MobinilVOMSI.java
 * ----------------------------------------------------------------------
 * Name Date History
 * ------------------------------------------------------------------------
 * Vinay Kumar Singh 22/11/2011 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2011 Comviva Technologies Ltd.
 * Interface class for constants definition.
 */
public interface MobinilVOMSI {
    public int ACTION_GET_VOUCHER_DETAILS = 1; // Get Account Information
    public int ACTION_UPDATE_VOUCHER_STATE = 2; // IMMEDIATE_DEBIT request
    public String RESULT_OK = "0";
    public String HTTP_STATUS_OK = "200";
    public String RESULT_ERROR_MALFORMED_REQUEST = "3"; // Request structure is
                                                        // not valid
    public String RESULT_ERROR_XML_PARSE = "9"; // Error during parser treatment
    public String VOUCHER_ALLOWED_STATE = "0,5";
    public String VERIFY_ON_HOLD = "OH";
}