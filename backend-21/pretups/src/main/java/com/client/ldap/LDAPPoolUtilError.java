package com.client.ldap;

public class LDAPPoolUtilError {

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
}
