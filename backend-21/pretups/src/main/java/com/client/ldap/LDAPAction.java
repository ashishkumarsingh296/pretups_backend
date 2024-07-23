package com.client.ldap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.util.LDAPUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldap.sdk.LDAPConnection;

public class LDAPAction extends LDAPUtil{
    // updated by akanksha

	private static LDAPPoolUtilError err = null;
    private static Log log = LogFactory.getLog(LDAPAction.class.getName());
    
    public LDAPAction() {
        err = new LDAPPoolUtilError();
    }


    /**
     * @author vipan.kumar
     *         LDAP Authenticate User
     * @param loginID
     * @return
     * @throws BTSLBaseException
     */
    public boolean authenticateUser(String pLoginID, String password) throws BTSLBaseException {

        if (log.isDebugEnabled())
        	log.debug("Entered in LDAP authenticateUser with Login Id: ", pLoginID + ", Password =" + password);

        String loginID=pLoginID;
        LDAPConnection con = null;
        boolean flag = false;
        try {
            String baseDn = Constants.getProperty("LDAP_DN");
            if (BTSLUtil.isNullString(baseDn)) {
                baseDn = "@example.com";
            }

            if(LDAPTypeI.LDAP_SERVER.equals(Constants.getProperty("LDAP_SERVER")))
            		loginID = loginID + baseDn;		// For Live
            else
            		loginID = "cn="+loginID+","+baseDn;// For Local
            
            con = LDAPPoolUtil.getConnection();
            if (con != null) {
                SimpleBindRequest bindRequest1 = new SimpleBindRequest(loginID, password);
                con.bind(bindRequest1);
                flag = true;
            }
        } catch (BTSLBaseException e) {
        	log.errorTrace("Exception in method authenticateUser()", e);
            flag = false;
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LDAPAction[authenticateUser] ", "", "", "", "LDAP Search  Error" + e.getMessage());
            throw new BTSLBaseException(e.getMessage(), "index ");
        } catch (com.unboundid.ldap.sdk.LDAPException e) {
            flag = false;
            log.errorTrace("Exception in method authenticateUser()  ", e);
            String be = err.mapError(e.getResultCode().intValue());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LDAPAction[authenticateUser]", "", "", "", "LDAP Search  Error =" + be);
            throw new BTSLBaseException(be, "index");
        } catch (Exception e) {
            flag = false;
            log.errorTrace("Exception in method authenticateUser() ", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LDAPAction[authenticateUser]", "", "", "", "LDAP Search  Error" + e.getMessage());
            throw new BTSLBaseException(e.getMessage(), "index");
        } finally {
            if ((con != null) && con.isConnected()) {
                try {
                    con.close();
                } catch (Exception e) {
                	log.errorTrace("Exception in method authenticateUser() ", e);
                	log.debug("Exception", e.getMessage());
                }
            }
        }
        log.debug("Exiting from authenticateUser with  Login Id: " + loginID + " , User found Flag : ", flag);
        return flag;

    }

}
