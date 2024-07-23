package com.inter.vodafoneghana.voucherconsumptiongh;

import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

public class VoucherConsumptionGHError {

    public String mapError(int errorCode) {
        String errorString = null;
        switch (errorCode) {
        case 91:
            errorString = "login.ldapauth.error.serverdown";
            break;
        case 87:
            errorString = "login.ldapauth.error.filtererror";
            break;
        case 86:
            errorString = "login.ldapauth.error.autherror";
            break;
        case 49:
            errorString = "login.ldapauth.error.invalid.credentials";
            break;

        case 51:
            errorString = "login.ldapauth.error.serverbusy";
            break;
        case 52:
            errorString = "login.ldapauth.error.server.unavailable";
            break;

        default:
            errorString = "login.ldapauth.error.server.error";
            break;
        }// End of switch
        return errorString;
    }

    public String mapErrorCode(int errorCode) {
        String errorString = null;
        switch (errorCode) {
        case 01:
            errorString = InterfaceErrorCodesI.ERROR_BAD_REQUEST;
            break;
        case 02:
            errorString = InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND;
            break;
        case 03:
            errorString = InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED;
            break;
        case 05:
            errorString = InterfaceErrorCodesI.INTERFACE_MSISDN_BARRED;
            break;
        case 06:
            errorString = InterfaceErrorCodesI.INTERFACE_MSISDN_BARRED;
            break;
        case 9:
            errorString = InterfaceErrorCodesI.RC_INSIFFICENT_ACC_BALANCE;
            break;
        case 18:
            errorString = InterfaceErrorCodesI.ERROR_BAD_REQUEST;
            break;
        case 19:
            errorString = InterfaceErrorCodesI.INTERFACE_MSISDN_BARRED;
            break;
        case 35:
            errorString = InterfaceErrorCodesI.RC_INSIFFICENT_ACC_BALANCE;
            break;
        default:
            errorString = InterfaceErrorCodesI.ERROR_RESPONSE;
            break;
        }// End of switch
        return errorString;
    }
}
