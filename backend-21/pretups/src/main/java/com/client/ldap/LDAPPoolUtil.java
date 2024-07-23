package com.client.ldap;

import java.util.StringTokenizer;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.Constants;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.RoundRobinServerSet;

public class LDAPPoolUtil {

    private static Log _log = LogFactory.getLog(LDAPPoolUtil.class.getName());

    private static LDAPPoolUtilError err = new LDAPPoolUtilError();;

    private static boolean creatingPool = false;

    private static String[] host = new String[LDAPTypeI.NO_HOST_TYPES];
    private static int[] port = new int[LDAPTypeI.NO_HOST_TYPES];

    private static int minLDAPPoolSize = 0;
    private static int maxLDAPPoolSize = 0;

    private static LDAPConnectionPool connectionPool = null;

    /**
	 * ensures no instantiation
	 */
    private LDAPPoolUtil(){
    	
    }
    
    
    static {
        try {

            int i = 0;
            String ldapHosts = Constants.getProperty("LDAP_HOSTS");
            String ldapPorts = Constants.getProperty("LDAP_PORTS");

            // Host
            StringTokenizer st = new StringTokenizer(ldapHosts, ",");
            while (st.hasMoreTokens()) {
                host[i++] = st.nextToken();
            }

            i = 0;
            // Port
            StringTokenizer st1 = new StringTokenizer(ldapPorts, ",");

            while (st1.hasMoreTokens()) {
                port[i++] = Integer.parseInt(st1.nextToken());
            }

            minLDAPPoolSize = Integer.valueOf(Constants.getProperty("LDAP_minpoolsize"));

            maxLDAPPoolSize = Integer.valueOf(Constants.getProperty("LDAP_maxpoolsize"));

        } catch (Exception e) {
            _log.errorTrace("Exception in method ", e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LDAPPoolUtil[static]", "", "", "", "LDAP Database Connection Problem");
            try {
                throw new BTSLBaseException("login.ldapauth.error.server.error");
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                _log.errorTrace("Exception in method ", e1);
            }
        }

    }

    /**
     * @author vipan.kumar
     *         LDAP Connection Returining
     * @return
     * @throws BTSLBaseException
     */
    public static LDAPConnection getConnection() throws BTSLBaseException {
        final String METHOD_NAME = "getConnection";
        if (_log.isDebugEnabled())
            _log.debug("LDAP getConnection", "Entered");
        LDAPConnection connection = null;
        try {
            if (connectionPool == null) {
                if (!createPool())
                    throw new BaseException("LDAP_CONNE_FAILED");
            }
            connection = connectionPool.getConnection();
        } catch (BTSLBaseException be) {
            _log.errorTrace("Exception in method getConnection() ", be);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LDAPPoolUtil[static]", "", "", "", "LDAP Database Connection Problem");
            throw new BTSLBaseException(be.getMessage(), "index");
        } catch (com.unboundid.ldap.sdk.LDAPException e) {
            int errorCode = e.getResultCode().intValue();
            System.out.println("ERROR CODE =====" + e.getResultCode().intValue());
            _log.debug("LDAPPoolUtil::getConnection Exception ,[ErrorCode] " + errorCode, e.getMessage());
            if (errorCode == 91) {
                synchronized (LDAPPoolUtil.class) {
                    try {
                        connection = connectionPool.getConnection();
                        _log.debug("LDAPPoolUtil::getConnection [Comments]", "LDAP Pool Connection Found");
                    } catch (com.unboundid.ldap.sdk.LDAPException le) {
                        try {
                            _log.errorTrace("Exception in method getConnection() ", le);
                            _log.debug("LDAPPoolUtil::getConnection :", "try to Close the old connection pool===");
                            connectionPool.close();
                            connectionPool = null;
                            _log.debug("LDAPPoolUtil::getConnection :", "Old connection pool Successfully Closed===");
                        } catch (Exception ex) {
                            _log.errorTrace("Exception in method getConnection() ", ex);
                            connectionPool = null;
                            _log.debug("LDAPPoolUtil::getConnection : [Comments] Error during close old connection pool ,[LDAPException] ", ex.getMessage());
                        }
                        _log.debug("LDAPPoolUtil::getConnection :", "Recreating LDAP pool");
                        try {
                            if (!createPool()) {
                                return null;
                            }
                            _log.debug("LDAPPoolUtil::getConnection :=", "DB pool recreated Successfully");
                            connection = connectionPool.getConnection();
                        } catch (com.unboundid.ldap.sdk.LDAPException ex) {
                            connectionPool = null;
                            _log.errorTrace(METHOD_NAME, ex);
                            String be = err.mapError(e.getResultCode().intValue());
                            _log.debug("LDAPPoolUtil::getConnection : [Comments] Error during close old connection pool ,[ErrorCode] " + ex.getResultCode().intValue(), "[LDAPException] " + ex.getMessage());
                            throw new BTSLBaseException(be, "index");
                        } catch (Exception exx) {
                            creatingPool = false;
                            _log.errorTrace("Exception in method getConnection() ", exx);
                            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LDAPPoolUtil[createPool]", "", "", "", "LDAP Database Connection Problem");
                            // String be=
                            // err.mapError(e.getResultCode().intValue());
                            throw new BTSLBaseException("login.ldapauth.error.serverdown", "index");
                        }
                    } finally {
                        // _log.debug("LDAPPoolUtil::getConnection := :[connection pool]===> [Current available] "+connectionPool.getCurrentAvailableConnections(),"[MAX Limit] "+connectionPool.getMaximumAvailableConnections());
                    }
                }
            } else {
                connectionPool = null;
                e.printStackTrace();
                throw new BTSLBaseException("login.ldapauth.error.serverdown", "index");
            }
            _log.error("getConnection", "Exceptin:e=" + e);
            // e.printStackTrace();
        } catch (Exception e) {
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LDAPPoolUtil[createPool]", "", "", "", "LDAP Database Connection Problem");
            _log.error("getConnection", "Exceptin:e=" + e);
            throw new BTSLBaseException("login.ldapauth.error.serverdown", "index");
        }
        return connection;
    }

    /**
     * @author vipan.kumar
     *         LDAP Pool Creation
     * @return
     * @throws BTSLBaseException
     */
    static boolean createPool() throws BTSLBaseException {
        
        final String METHOD_NAME = "createPool";

        try {
            if (!creatingPool) {
                creatingPool = true;
                int _connectTimeout = 0;
                int _responseTimeout = 0;
                if (_log.isDebugEnabled())
                    _log.debug("createPool", "Creating LDAP connection pool");

                try {
                    _connectTimeout = Integer.parseInt(Constants.getProperty("LDAP_CONNE_TIMEOUT"));
                } catch (Exception io) {
                    _log.errorTrace("Exception in method createPool() ", io);
                    _connectTimeout = 1000;
                }

                try {
                    _responseTimeout = Integer.parseInt(Constants.getProperty("LDAP_RESP_TIMEOUT"));
                } catch (Exception io) {
                    _log.errorTrace("Exception in method createPool() ", io);
                    _responseTimeout = 1000;
                }

                LDAPConnectionOptions _connectionOptions = new LDAPConnectionOptions();
                _connectionOptions.setConnectTimeoutMillis(_connectTimeout);
                _connectionOptions.setResponseTimeoutMillis(_responseTimeout);
                _connectionOptions.setAutoReconnect(true);

                RoundRobinServerSet serverSet = new RoundRobinServerSet(host, port, _connectionOptions);

                connectionPool = new LDAPConnectionPool(serverSet.getConnection(), minLDAPPoolSize, maxLDAPPoolSize);

                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "LDAPPoolUtil[createPool]", "", "", "", "LDAP Pool Created Successfully");
            }
        } catch (com.unboundid.ldap.sdk.LDAPException e) {
            creatingPool = false;
            _log.errorTrace(METHOD_NAME, e);
            String be = err.mapError(e.getResultCode().intValue());
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LDAPPoolUtil[createPool]", "", "", "", "LDAP Database Connection Problem Error Code=" + be);
            throw new BTSLBaseException(be, "index");
        } catch (Exception e) {
            creatingPool = false;
            _log.errorTrace("Exception in method createPool() ", e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LDAPPoolUtil[createPool]", "", "", "", "LDAP Database Connection Problem");
            // String be= err.mapError(e.getResultCode().intValue());
            throw new BTSLBaseException("login.ldapauth.error.serverdown", "index");
        } finally {
            creatingPool = false;
        }
        return true;

    }
}
