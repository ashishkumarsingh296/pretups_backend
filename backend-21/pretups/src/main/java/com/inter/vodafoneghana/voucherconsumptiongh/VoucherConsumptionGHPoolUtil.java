package com.inter.vodafoneghana.voucherconsumptiongh;

import java.util.HashMap;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;
import netscape.ldap.util.ConnectionPool;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.util.BTSLUtil;

public class VoucherConsumptionGHPoolUtil {

    private static Log _log = LogFactory.getLog(VoucherConsumptionGHPoolUtil.class.getName());

    static boolean creatingPool = false;

    static VoucherConsumptionGHError err = new VoucherConsumptionGHError();

    public static HashMap<String, VoucherConsumptionGHSocket> _poolBucket = new HashMap<String, VoucherConsumptionGHSocket>();// Contains
                                                                                                                              // the
                                                                                                                              // list
                                                                                                                              // of
                                                                                                                              // free
                                                                                                                              // client
                                                                                                                              // objects,associated
                                                                                                                              // with
                                                                                                                              // interface
                                                                                                                              // id.

    public static LDAPConnection getConnection(String fileCacheId) throws BTSLBaseException {
        final String methodName = "VoucherConsumptionGHPoolUtil[getConnection]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered");
        ConnectionPool connectionPool = null;

        LDAPConnection connection = null;
        try {
            VoucherConsumptionGHSocket consumptionGHSocket = null;

            if (_poolBucket.get(fileCacheId) == null) {
                if (!createPool(fileCacheId))
                    throw new BaseException("LDAP_CONNE_FAILED");
            }

            consumptionGHSocket = _poolBucket.get(fileCacheId);
            connectionPool = consumptionGHSocket.getConnectionPool();

            if (connectionPool == null) {
                if (!createPool(fileCacheId))
                    throw new BaseException("LDAP_CONNE_FAILED");
            }
            connection = connectionPool.getConnection();
            if (connection == null || !connection.isConnected()) {
                throw new netscape.ldap.LDAPException(methodName + "Connection Null", LDAPException.CONNECT_ERROR);
            }
        } catch (netscape.ldap.LDAPException e) {
            int errorCode = e.getLDAPResultCode();

            _log.debug(methodName + " Exception ,[ErrorCode] " + errorCode, e.getMessage());
            if (errorCode == 91) {
                synchronized (VoucherConsumptionGHPoolUtil.class) {
                    try {
                        connection = connectionPool.getConnection();
                        if (connection == null || !connection.isConnected()) {
                            throw new netscape.ldap.LDAPException(methodName + "Connection Null", LDAPException.CONNECT_ERROR);
                        }
                        _log.debug(methodName + " [Comments]", "LDAP Pool Connection Found");
                    } catch (netscape.ldap.LDAPException le) {
                        try {
                            if (!createPool(fileCacheId)) {
                                return null;
                            }
                            _log.debug(methodName + ":=", "DB pool recreated Successfully");
                            connection = connectionPool.getConnection();
                            if (connection == null || !connection.isConnected()) {
                                throw new netscape.ldap.LDAPException(methodName + "Connection Null", LDAPException.CONNECT_ERROR);
                            }
                        } catch (netscape.ldap.LDAPException ex) {
                            connectionPool = null;
                            String be = err.mapError(e.getLDAPResultCode());
                            _log.debug(methodName + ": [Comments] Error during close old connection pool ,[ErrorCode] " + ex.getLDAPResultCode(), methodName + ex.getMessage());
                            return null;

                        } catch (Exception exx) {
                            creatingPool = false;
                            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "", "LDAP Database Connection Problem");
                            // String be=
                            // err.mapError(e.getResultCode().intValue());
                            return null;
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
        } catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "", "LDAP Database Connection Problem");
            return null;
        } catch (Exception e) {
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "", "LDAP Database Connection Problem");
            _log.error(methodName, "Exceptin:e=" + e);
            return null;
        }
        return connection;
    }

    /**
     * @author vipan.kumar
     *         LDAP Connection Returining
     * @return
     * @throws BTSLBaseException
     */
    public static void retunConnection(String fileCacheId, LDAPConnection ldapConnection) throws BTSLBaseException {
        final String methodName = "VoucherConsumptionGHPoolUtil[retunConnection]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered");
        ConnectionPool connectionPool = null;

        try {
            VoucherConsumptionGHSocket consumptionGHSocket = null;
            if (_poolBucket.get(fileCacheId) != null) {
                consumptionGHSocket = _poolBucket.get(fileCacheId);
                connectionPool = consumptionGHSocket.getConnectionPool();
                if (connectionPool != null) {
                    connectionPool.close(ldapConnection);
                }
            }
        } catch (Exception e) {
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "", "LDAP Database Connection Problem");
            _log.error(methodName, "Exceptin:e=" + e);
        }
    }

    /**
     * @author vipan.kumar
     *         LDAP Connection Returining
     * @return
     * @throws BTSLBaseException
     */
    public static void removeConnection(String fileCacheId) throws BTSLBaseException {
        final String methodName = "VoucherConsumptionGHPoolUtil[removeConnection]";

        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered");
        ConnectionPool connectionPool = null;

        try {
            VoucherConsumptionGHSocket consumptionGHSocket = null;

            if (_poolBucket.get(fileCacheId) == null) {

                consumptionGHSocket = _poolBucket.get(fileCacheId);
                connectionPool = consumptionGHSocket.getConnectionPool();

                if (connectionPool != null) {
                    connectionPool.destroy();
                }
            }
        } catch (Exception e) {
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "", "Not able to destroy LDAP Connection Problem");
            _log.error(methodName, "Exceptin:e=" + e);
        }
    }

    /**
     * @author vipan.kumar
     *         LDAP Pool Creation
     * @return
     * @throws BTSLBaseException
     */
    static boolean createPool(String fileCacheId) throws BTSLBaseException {

        final String methodName = "VoucherConsumptionGHPoolUtil[createPool]";
        try {
            if (!creatingPool) {
                creatingPool = true;

                VoucherConsumptionGHSocket _voucherConsumptionGHSocket = new VoucherConsumptionGHSocket();

                if (!BTSLUtil.isNullString(FileCache.getValue(fileCacheId, "LDAP_IN_IP"))) {
                    _voucherConsumptionGHSocket.setDestinationIP(FileCache.getValue(fileCacheId, "LDAP_IN_IP"));
                } else {
                    _voucherConsumptionGHSocket.setDestinationIP("");
                }
                if (!BTSLUtil.isNullString(FileCache.getValue(fileCacheId, "LDAP_IN_AUTHID"))) {
                    _voucherConsumptionGHSocket.setAuthId(FileCache.getValue(fileCacheId, "LDAP_IN_AUTHID"));
                } else {
                    _voucherConsumptionGHSocket.setAuthId("");
                }
                if (!BTSLUtil.isNullString(FileCache.getValue(fileCacheId, "LDAP_IN_PASSWORD"))) {
                    _voucherConsumptionGHSocket.setAuthPassword(FileCache.getValue(fileCacheId, "LDAP_IN_PASSWORD"));
                } else {
                    _voucherConsumptionGHSocket.setAuthPassword("");
                }
                try {
                    _voucherConsumptionGHSocket.setDestinationPort(Integer.parseInt(FileCache.getValue(fileCacheId, "LDAP_IN_PORT")));
                } catch (Exception e) {
                    _voucherConsumptionGHSocket.setDestinationPort(7200);
                }

                try {
                    _voucherConsumptionGHSocket.setMinPoolSize(Integer.parseInt(FileCache.getValue(fileCacheId, "LDAP_IN_MIN_POOL_SIZE")));
                } catch (Exception e) {
                    _voucherConsumptionGHSocket.setMinPoolSize(1);
                }
                try {
                    _voucherConsumptionGHSocket.setMaxPoolSize(Integer.parseInt(FileCache.getValue(fileCacheId, "LDAP_IN_MAX_POOL_SIZE")));
                } catch (Exception e) {
                    _voucherConsumptionGHSocket.setMaxPoolSize(1);
                }
                try {
                    _voucherConsumptionGHSocket.setVersion(Integer.parseInt(FileCache.getValue(fileCacheId, "LDAP_VERSION")));
                } catch (Exception e) {
                    _voucherConsumptionGHSocket.setVersion(2);
                }
                try {
                    _voucherConsumptionGHSocket.setConnectTimeOut(Integer.parseInt(FileCache.getValue(fileCacheId, "LDAP_TIMEOUT")));
                } catch (Exception e) {
                    _voucherConsumptionGHSocket.setConnectTimeOut(1000);
                }

                LDAPConnection connection = new LDAPConnection();
                ConnectionPool connectionPool = null;

                connection.connect(_voucherConsumptionGHSocket.getDestinationIP(), _voucherConsumptionGHSocket.getDestinationPort());
                connection.bind(_voucherConsumptionGHSocket.getVersion(), _voucherConsumptionGHSocket.getAuthId(), _voucherConsumptionGHSocket.getAuthPassword());
                connection.setConnectTimeout(_voucherConsumptionGHSocket.getConnectTimeOut());

                if (_poolBucket.containsKey(fileCacheId)) {
                    VoucherConsumptionGHSocket voucherConsumptionGHSocket = _poolBucket.get(fileCacheId);
                    if (voucherConsumptionGHSocket.getConnectionPool() != null) {
                        voucherConsumptionGHSocket.getConnectionPool().destroy();
                    }
                    _poolBucket.remove(fileCacheId);
                }

                connectionPool = new ConnectionPool(_voucherConsumptionGHSocket.getMinPoolSize(), _voucherConsumptionGHSocket.getMaxPoolSize(), connection);
                _voucherConsumptionGHSocket.setConnectionPool(connectionPool);
                _poolBucket.put(fileCacheId, _voucherConsumptionGHSocket);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, methodName, "", "", "", "LDAP Pool Created Successfully");
            }
        } catch (LDAPException e) {
            creatingPool = false;
            String be = err.mapError(e.getLDAPResultCode());
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "", "LDAP Database Connection Problem Error Code=" + be);
            throw new BTSLBaseException(be, "index");
        } catch (Exception e) {
            creatingPool = false;
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "", "LDAP Database Connection Problem");
            // String be= err.mapError(e.getResultCode().intValue());
            throw new BTSLBaseException("login.ldapauth.error.serverdown", "index");
        } finally {
            creatingPool = false;
        }
        return true;

    }
}
