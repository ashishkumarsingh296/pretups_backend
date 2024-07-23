package com.selftopup.db.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import oracle.jdbc.pool.OracleConnectionCacheManager;
import oracle.jdbc.pool.OracleDataSource;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.BaseException;
import com.selftopup.db.util.BTSLDBManager;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;

public class OJDBCPoolManager extends BTSLDBManager {
    private static int _cntConnection = 0;
    private static Log _log = LogFactory.getLog(OJDBCPoolManager.class.getName());

    private static String DB_CONN_FAILED = "00000";

    private static OracleDataSource _ods = null;
    private static OracleDataSource _ods_single = null;
    private static OracleConnectionCacheManager _occm;
    private static OracleConnectionCacheManager _occm_single;

    static final String cacheName = "PreTUPSCache_" + Constants.getProperty("CACHE_ID");
    static final String cacheName_single = "PreTUPSCache_" + Constants.getProperty("CACHE_ID") + "_SINGLE";

    private static String ONS_CONFIG = Constants.getProperty("ONS_CONFIG");
    private static String ONS_EN = Constants.getProperty("ONS_EN");

    private final static ReentrantLock lock = new ReentrantLock();

    static {
        try {
            _ods = new OracleDataSource();
        } catch (Exception e) {
            _log.errorTrace("static block: Exception print stack trace:=", e);
        }
    }

    /**
     * This method returns connection from the connection pool, if there is no
     * connection
     * in the pool then adds new connection in the connection pool.
     * 
     * @return Connection, database connection
     * @param none
     * 
     */
    public Connection getConnection() throws BTSLBaseException {
        Connection dbConnection = null;
        try {
            if (_cntConnection == 0) {
                _cntConnection++;
                if (!createPool())
                    throw new BaseException(DB_CONN_FAILED);
            }
            try {
                dbConnection = _ods.getConnection();
                dbConnection.setAutoCommit(false);
                try {
                    dbConnection.rollback();
                } catch (SQLException sqe1) {
                    _log.error("getConnection Rollback", "Exception Error Code=" + sqe1.getErrorCode());
                }
            } catch (SQLException sqe) {
                _log.errorTrace("getConnection: Exception print stack trace:=", sqe);
                int errorCode = sqe.getErrorCode();
                _log.error("getConnection", "Exception Error Code=" + errorCode);
                /*
                 * Error Codes to be handled.
                 * 17002=Io exception
                 * 17008=Closed Connection
                 * 17410=No more data to read from socket
                 * 17416=FATAL
                 */
                if (errorCode == 17008) {
                    dbConnection = _ods.getConnection();
                    dbConnection.setAutoCommit(false);
                    try {
                        dbConnection.rollback();
                    } catch (SQLException sqe1) {
                        _log.error("getConnection Rollback", "Exception Error Code=" + sqe1.getErrorCode());
                    }
                } else if (errorCode == 17002 || errorCode == 17410 || errorCode == 17416) {
                    _log.error("getConnection", "Recreating pool");
                    if (lock.tryLock()) {
                        try {
                            if (!createPool())
                                throw new BTSLBaseException(DB_CONN_FAILED);
                            dbConnection = _ods.getConnection();
                            dbConnection.setAutoCommit(false);
                        } finally {
                            lock.unlock();
                        }

                    } else {
                        try {
                            dbConnection = _ods.getConnection();
                            dbConnection.setAutoCommit(false);
                            try {
                                dbConnection.rollback();
                            } catch (SQLException sqe1) {
                                _log.error("getConnection Rollback", "Exception Error Code=" + sqe1.getErrorCode());
                            }
                        } catch (SQLException sqe3) {
                            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBC14PoolManager[getConnection]", "", "", "", "Database Connection Problem " + sqe3.getErrorCode());
                            throw sqe3;
                        }
                    }
                } else
                    throw new BTSLBaseException(DB_CONN_FAILED);
            } catch (Exception e) {
                _log.errorTrace("getConnection: Exception print stack trace:=", e);
                throw new BTSLBaseException(DB_CONN_FAILED);
            }
            if (dbConnection == null)
                throw new BTSLBaseException(DB_CONN_FAILED);
            // added error log to always print this line
            if (_occm != null)
                _log.error("getConnection 10G", "DB Connections  getting connection from connection pool :" + dbConnection + " active size:" + _occm.getNumberOfActiveConnections(cacheName) + " cache size:" + _occm.getNumberOfAvailableConnections(cacheName));
        } catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBC14PoolManager[getConnection]", "", "", "", "Database Connection Problem " + be.getErrorCode());
            throw be;
        } catch (Exception ex) {
            // Trap errors
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBC14PoolManager[getConnection]", "", "", "", "Database Connection Problem ");
            _log.error("getConnection", "Error in Connecting to the Database \n" + ex.getMessage() + "\n");
            _log.errorTrace("getConnection:Exception print stack trace:=", ex);
            throw new BTSLBaseException(DB_CONN_FAILED);
        }
        return dbConnection;
    } // getConnection

    /**
     * This method returns a single connection
     * Creation date: (18/07/04)
     * 
     * @return Connection, database connection
     * @param none
     */
    public Connection getSingleConnection() {
        Connection dbConnection = null;
        try {
            if (_ods_single == null) {
                String db_url = Constants.getProperty("datasourceurl");
                Properties prop = new Properties();
                String db_user = Constants.getProperty("userid");
                if (db_user != null)
                    db_user = BTSLUtil.decrypt3DesAesText(db_user);
                String db_password = Constants.getProperty("passwd");
                if (db_password != null)
                    db_password = BTSLUtil.decrypt3DesAesText(db_password);
                // System.out.println("getConnection() >> userid=" + db_user +
                // ",  password="+ db_password+ ", db_url="+ db_url);
                if (db_url != null) {
                    _ods_single = new OracleDataSource();
                    // provide various attribute of database driver to make
                    // connection
                    _ods_single.setURL(db_url);
                    _ods_single.setUser(db_user);
                    _ods_single.setPassword(db_password);
                    prop.setProperty("MinLimit", "1"); // the cache size is 5 at
                                                       // least
                    prop.setProperty("MaxLimit", "1");
                    prop.setProperty("InitialLimit", "1"); // create 3
                                                           // connections at
                                                           // startup
                    prop.setProperty("ValidateConnection", "TRUE");
                    _ods_single.setConnectionCacheProperties(prop);
                    _ods_single.setConnectionCachingEnabled(true);
                    if ("Y".equalsIgnoreCase(ONS_EN)) {
                        _ods_single.setFastConnectionFailoverEnabled(true);
                        _ods_single.setONSConfiguration(ONS_CONFIG);
                    }

                    _occm_single = OracleConnectionCacheManager.getConnectionCacheManagerInstance();
                    _occm_single.createCache(cacheName_single, _ods_single, prop);
                } else
                    return null;
            }// end of the m_connectionSinglePool

            dbConnection = _ods_single.getConnection();
            dbConnection.setAutoCommit(false);
            if (_log.isDebugEnabled())
                _log.error("getSingleConnection 10G", "DB Connections  getting connection from connection pool :" + dbConnection + " active size:" + _occm_single.getNumberOfActiveConnections(cacheName_single) + " cache size:" + _occm_single.getNumberOfAvailableConnections(cacheName_single));
        } catch (Exception ex) {
            // Trap errors
            _log.error("getSingleConnection", "Error in Connecting to the Database \n" + ex.getMessage() + "\n");
            _log.errorTrace("getSingleConnection: Exception print stack trace:=", ex);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBC14PoolManager[getSingleConnection]", "", "", "", "Database Connection Problem");
        }
        return dbConnection;
    } // getConnection

    public boolean createPool() {
        try {
            /*
             * if(!creatingPool)
             * {
             * creatingPool=true;
             */
            if (_log.isDebugEnabled())
                _log.debug("createPool", "Creating oracle connection pool");

            String db_url = Constants.getProperty("datasourceurl");
            Properties prop = new Properties();

            String db_user = Constants.getProperty("userid");
            if (db_user != null)
                db_user = BTSLUtil.decrypt3DesAesText(db_user);
            String db_password = Constants.getProperty("passwd");
            if (db_password != null)
                db_password = BTSLUtil.decrypt3DesAesText(db_password);

            String strMinPoolSize = Constants.getProperty("minpoolsize");

            String strPoolSize = Constants.getProperty("poolsize");
            String oraclePoolLogFile = Constants.getProperty("oraclePoolLogFile");
            int minPoolSize = 0;
            try {
                minPoolSize = Integer.parseInt(strMinPoolSize);
            } catch (Exception e) {
                minPoolSize = 50;
            }
            int poolSize = 0;
            try {
                poolSize = Integer.parseInt(strPoolSize);
            } catch (Exception e) {
                _log.errorTrace("createPool: Exception print stack trace:=", e);
                poolSize = 60;
            }

            if (_log.isDebugEnabled())
                _log.debug("createPool 10G", "userid=" + db_user + ", db password=" + db_password + ", db_url=" + db_url + ", minPoolSize=" + minPoolSize + " max poolSize=" + poolSize + " oraclePoolLogFile=" + oraclePoolLogFile);
            try {
                if (db_url != null) {
                    // creating the instatnce of oracle datasource
                    // _ods = new OracleDataSource();
                    // provide various attribute of database driver to make
                    // connection
                    _ods.setURL(db_url);
                    _ods.setUser(db_user);
                    _ods.setPassword(db_password);
                    prop.setProperty("MinLimit", strPoolSize);
                    prop.setProperty("MaxLimit", strPoolSize);
                    prop.setProperty("InitialLimit", strMinPoolSize);
                    prop.setProperty("ValidateConnection", "TRUE");
                    _ods.setConnectionCacheProperties(prop);
                    _ods.setConnectionCachingEnabled(true);
                    if ("Y".equalsIgnoreCase(ONS_EN)) {
                        _ods.setFastConnectionFailoverEnabled(true);
                        _ods.setONSConfiguration(ONS_CONFIG);
                    }
                    _occm = OracleConnectionCacheManager.getConnectionCacheManagerInstance();

                    _occm.createCache(cacheName, _ods, prop);

                }
            } catch (Exception e) {
                _log.errorTrace("createPool: Exception print stack trace:=", e);
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBC14PoolManager[createPool]", "", "", "", "Database Connection Problem");
                return false;
            }
            // }
        } catch (Exception e) {
            _log.errorTrace("createPool: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBC14PoolManager[createPool]", "", "", "", "Database Connection Problem");
            return false;
        } finally {
            // creatingPool=false;
        }
        return true;
    }

}// end of class OJDBC14PoolManager

