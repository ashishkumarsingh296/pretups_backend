package com.inter.uganda_webservices;

/**
 * OUGVoluBillI.java
 * Copyright(c) 2011, Comviva Technologies Pvt Ltd.
 * All Rights Reserved
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Shashank Shukla September 04, 2011 Initial Creation
 * ----------------------------------------------------------------------------
 * -------------------
 * This interface defined some constant corresponding to the UgandaWebservice
 * interface.
 */
public interface OUGVoluBillI {
    public String RESULT_OK = "0";
    public int ACTION_LOGON = 1;
    public int ACTION_LOGOFF = 2;
    public int ACTION_ACCOUNT_INFO = 3;
    public int ACTION_INTERNET_RECHARGE = 4;
    public String ACTION_NOT_OK = "6";
    public String OPERATION_LOGIN_SUCCESSFUL = "Logon success";
    public String OPERATION_LOGOFF_SUCCESSFUL = "Logoff success";
    public String OPERATION_UPDATE_SUCCESSFUL = "Update account completed";
    public String OPERATION_GETACCOUNT_SUCCESSFUL = "Account is read successful";
    public String OPERATION_ADD_SUBSCRIBEDPACKAGE_SUCCESSFUL = "Successfully completed";
    public String SUBSCRIBER_NOT_FOUND = "Account not found";
}
