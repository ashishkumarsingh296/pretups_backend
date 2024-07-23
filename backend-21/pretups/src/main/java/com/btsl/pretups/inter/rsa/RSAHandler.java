package com.btsl.pretups.inter.rsa;

/**
 * @(#)RSAHandler.java
 *                     Copyright(c) 2009, Comviva Technologies Ltd.
 *                     All Rights Reserved
 *                     Handler class for interaction to RSA system
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Vinay kumar 22/10/2009 Initial Creation
 *                     Praveen Singh 24/12/2012 Moved from Tigo to Pretups 6.1.0
 *                     version
 *                     --------------------------------------------------------
 *                     ----------------------------------------
 */

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.rsa.authagent.authapi.AuthSession;

public class RSAHandler {

    private static Log _log = LogFactory.getFactory().getInstance(RSAHandler.class.getName());

    /**
     * ensures no instantiation
     */
    private RSAHandler(){
    	
    }
    
    /**
     * Method to validate RSA User ID and Passcode at RSA system
     * 
     * @param String
     *            p_RSAUserID
     * @param String
     *            p_RSAPasscode
     * @return boolean rsaAuthenticationOK
     */
    public static boolean checkRSAAuthentication(String p_RSAUserID, String p_RSAPasscode) throws Exception {

        boolean rsaAuthenticationOK = false;
        if (_log.isDebugEnabled())
            _log.debug("checkRSAAuthentication", "Enterd: UserID=" + p_RSAUserID + " and Passcode=" + p_RSAPasscode);
        try {
            AuthSession authSession = RSAClient.createUserSession();
            if (authSession != null) {
                if (RSAClient.validateRSAAuthentication(authSession, p_RSAUserID, p_RSAPasscode) == 0)
                    rsaAuthenticationOK = true;
                RSAClient.closeUserSession(authSession);
            }
        } catch (Exception e) {
            _log.error("checkRSAAuthentication", "Exception:e=" + e);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("checkRSAAuthentication", "Exit, RSA authentication status " + rsaAuthenticationOK);
        }
        return rsaAuthenticationOK;
    }
}
