package com.btsl.pretups.inter.rsa;

/**
 * @(#)RSAClient.java
 *                    Copyright(c) 2009, Comviva Technologies Ltd.
 *                    All Rights Reserved
 *                    Client class for interaction to RSA system
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Vinay kumar 24/10/2009 Initial Creation
 *                    Praveen Singh 24/12/2012 Moved from Tigo to Pretups6.1.0
 *                    version
 *                    ----------------------------------------------------------
 *                    --------------------------------------
 */

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.rsa.authagent.authapi.AuthAgentException;
import com.rsa.authagent.authapi.AuthSession;
import com.rsa.authagent.authapi.AuthSessionFactory;

public class RSAClient {

    private static Log _log = LogFactory.getFactory().getInstance(RSAClient.class.getName());
    static public AuthSessionFactory api = null;

    /**
     * Static block which will load the rsa property file and return the intance
     * of AuthSession
     */
    static {

        try {
            String directoryPath = Constants.getProperty("RSA_INTERFACE_DIRECTORY");
            if (BTSLUtil.isNullString(directoryPath))
                throw new Exception("RSA_INTERFACE_DIRECTORY can't defined in constants.props");
            api = AuthSessionFactory.getInstance(directoryPath + "/rsa_api.properties");
            if (_log.isDebugEnabled())
                _log.debug("RSAClient", "RSA AuthSessionFactory Status=" + api.getAceServerStatus().toString());
        } catch (Exception e) {
            if (_log.isDebugEnabled())
                _log.debug("RSAClient", "Can't create RSA api: " + e.getMessage());
        }
    }
    /**
     * ensures no instantiation
     */
    private RSAClient(){
    	
    }

    /**
     * Method to create the user session for RSA validation
     * 
     * @return AuthSession authSession
     * @throws Exception
     */
    public static AuthSession createUserSession() throws Exception {

        if (_log.isDebugEnabled())
            _log.debug("createUserSession", "Entered...");
        AuthSession authSession = null;

        try {
            authSession = api.createUserSession();
            if (_log.isDebugEnabled())
                _log.debug("createUserSession", "User session successfully created. Session is " + authSession);
        } catch (AuthAgentException aae) {
            aae.printStackTrace();
            if (_log.isDebugEnabled())
                _log.debug("createUserSession", "Exception during session creation, AuthAgentException=" + aae.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            if (_log.isDebugEnabled())
                _log.debug("createUserSession", "Exception during session creation, Exception=" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("createUserSession", "Exit.");
        }
        return authSession;
    }

    /**
     * Method to close the user session for RSA validation
     * 
     * @param Authsession
     *            authSession
     * @return int closeStatus
     * @throws Exception
     */
    public static int closeUserSession(AuthSession authSession) throws Exception {

        if (_log.isDebugEnabled())
            _log.debug("closeUserSession", "Entered: Session=" + authSession);
        int closeStatus = 1;

        try {
            authSession.close();
            closeStatus = 0;
            if (_log.isDebugEnabled())
                _log.debug("closeUserSession", "User session successfully closed.");
        } catch (AuthAgentException aae) {
            aae.printStackTrace();
            if (_log.isDebugEnabled())
                _log.debug("closeUserSession", "Exception during closing session, AuthAgentException=" + aae.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            if (_log.isDebugEnabled())
                _log.debug("closeUserSession", "Exception during closing session, Exception=" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("closeUserSession", "Exit.");
        }
        return closeStatus;
    }

    /**
     * Method to validate RSA User ID and Passcode at RSA system
     * 
     * @param AuthSession
     *            authSession
     * @param String
     *            p_RSAUserID
     * @param String
     *            p_RSAPasscode
     * @return int checkStatus
     * @throws Exception
     */
    public static int validateRSAAuthentication(AuthSession authSession, String p_RSAUserID, String p_RSAPasscode) throws Exception {

        if (_log.isDebugEnabled())
            _log.debug("validateRSAAuthentication", "Enterd: UserID=" + p_RSAUserID + " and Passcode=" + p_RSAPasscode);
        int lockStatus = AuthSession.ACCESS_DENIED;
        int checkStatus = AuthSession.ACCESS_DENIED;

        /*
         * AuthSession.ACCESS_OK=0;
         * AuthSession.ACCESS_DENIED=1;
         * AuthSession.PIN_ACCEPTED=6;
         * AuthSession.PIN_REJECTED=7;
         */

        try {
            lockStatus = authSession.lock(p_RSAUserID);
            if (_log.isDebugEnabled())
                _log.debug("validateRSAAuthentication", "User ID " + p_RSAUserID + " locked at RSA system. Status: " + finalAuthStatus(lockStatus));
            if (lockStatus == AuthSession.ACCESS_OK)
                checkStatus = authSession.check(p_RSAUserID, p_RSAPasscode);
            if (_log.isDebugEnabled())
                _log.debug("validateRSAAuthentication", "User ID " + p_RSAUserID + " with Passcode " + p_RSAPasscode + " checked at RSA system. Status: " + finalAuthStatus(checkStatus));
        } catch (AuthAgentException aae) {
            aae.printStackTrace();
            if (_log.isDebugEnabled())
                _log.debug("validateRSAAuthentication", "Exception during validation at RSA system, AuthAgentException=" + aae.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            if (_log.isDebugEnabled())
                _log.debug("validateRSAAuthentication", "Exception during validation at RSA system, Exception=" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("validateRSAAuthentication", "Exit(Status definition: ACCESS_OK=0, ACCESS_DENIED=1, PIN_ACCEPTED=6, PIN_REJECTED=7). Status=" + checkStatus);
        }
        return checkStatus;
    }

    /**
     * Method to decide different RSA status
     * 
     * @param int status
     * @return String message
     */
    private static String finalAuthStatus(int status) {

        String message = null;

        switch (status) {
        case AuthSession.ACCESS_OK:
            message = "Access Ok";
            break;
        case AuthSession.PIN_ACCEPTED:
            message = "Passcode Accepted";
            break;
        case AuthSession.PIN_REJECTED:
            message = "Passcode Rejected";
            break;
        case AuthSession.ACCESS_DENIED:
        default:
            message = "Access Denied";
            break;
        }
        return message;
    }
}
