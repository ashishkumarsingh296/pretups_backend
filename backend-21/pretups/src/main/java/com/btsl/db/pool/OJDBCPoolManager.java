package com.btsl.db.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseException;
import com.btsl.db.util.BTSLDBManager;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.DBConenctionPoolLog;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

import oracle.jdbc.pool.OracleConnectionCacheManager;
import oracle.jdbc.pool.OracleDataSource;

/*
 * OJDBCPoolManager.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Sachin Kumar Sharma 14/06/2012 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 */

public class OJDBCPoolManager extends BTSLDBManager {
    private static int _cntConnection = 0;
    private static int _cntReportConnection = 0;
    private static int _cntCurrentReportDBConnection = 0;
    private static int _cntExternalDBConnection = 0;
    private static Log _log = LogFactory.getLog(OJDBCPoolManager.class.getName());

    private static String DB_CONN_FAILED = "00000";

    private static OracleDataSource _ods = null;

    public static javax.sql.DataSource getDatasource() {
        return _ods;
    }

    //private static OracleDataSource _ods_single = null;
    private static OracleConnectionCacheManager _occm;
    private static OracleConnectionCacheManager _occm_single;

    static final String cacheName = "PreTUPSCache_" + Constants.getProperty("CACHE_ID");
    static final String cacheName_single = "PreTUPSCache_" + Constants.getProperty("CACHE_ID") + "_SINGLE";

    private static String ONS_CONFIG = Constants.getProperty("ONS_CONFIG");
    private static String ONS_CONFIG_REPORT_DB = Constants.getProperty("ONS_CONFIG_REPORT_DB");
    private static String ONS_CONFIG_CURRENT_REPORT_DB = Constants.getProperty("ONS_CONFIG_CURRENT_REPORT_DB");
    private static String ONS_EN = Constants.getProperty("ONS_EN");
    private static String ONS_REPORT_DB_EN = Constants.getProperty("ONS_REPORT_DB_EN");
    private static String ONS_CURRENT_REPORT_DB_EN = Constants.getProperty("ONS_CURRENT_REPORT_DB_EN");

    private static String ONS_CONFIG_EXTERNAL_DB = Constants.getProperty("ONS_CONFIG_EXTERNAL_DB");
    private static String ONS_EXTERNAL_DB_EN = Constants.getProperty("ONS_EXTERNAL_DB_EN");
    private static final ReentrantLock lock = new ReentrantLock();
    private static final ReentrantLock lockReportConnection = new ReentrantLock();
    private static final ReentrantLock lockCurrentReportConnection = new ReentrantLock();
    private static final ReentrantLock lockExternalDBConnection = new ReentrantLock();
    private static final ConcurrentMap<String, Integer> activeSizeKey = new ConcurrentHashMap();

    static {
        try {
            _ods = new OracleDataSource();
        } catch (Exception e) {
            _log.errorTrace("static", e);
        }
    }

    public int getActiveConnection() {
    	return activeSizeKey.get("ACTIVESIZE")!=null?activeSizeKey.get("ACTIVESIZE"):1;
    }
    public int getAvailableConnection() {
    	return activeSizeKey.get("CACHESIZE")!=null?activeSizeKey.get("CACHESIZE"):100;
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
        final String METHOD_NAME = "getConnection";
        try {
            if (_cntConnection == 0) {
                _cntConnection++;
                if (!createPool())
                    throw new BaseException(DB_CONN_FAILED);
            }
            try {
                dbConnection = _ods.getConnection();
                dbConnection.setAutoCommit(false);
               /* try {
                    dbConnection.rollback();
                } catch (SQLException sqe1) {
                    _log.errorTrace(METHOD_NAME, sqe1);
                    _log.error("getConnection Rollback", "Exception Error Code=" + sqe1.getErrorCode());
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getConnection]", "", "", "", "Database Connection Problem " + sqe1.getErrorCode());
                }*/
            } catch (SQLException sqe) {
                _log.errorTrace(METHOD_NAME, sqe);
                
                int errorCode = sqe.getErrorCode();
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getConnection]", "", "", "", "Database Connection Problem " + errorCode);
                _log.error("getConnection", "Exception Error Code=" + errorCode);


                if (errorCode == 17143) {
                    _occm.getConnectionCacheManagerInstance().refreshCache(cacheName, OracleConnectionCacheManager.REFRESH_ALL_CONNECTIONS);
					dbConnection = _ods.getConnection();
                    dbConnection.setAutoCommit(false);
                    _log.error("getConnection ", "Refresh Invalid or Stale Connection found in the Connection Cache");

                } else if (errorCode == 17008) {
                    dbConnection = _ods.getConnection();
                    dbConnection.setAutoCommit(false);
                    /*try {
                        dbConnection.rollback();
                    } catch (SQLException sqe1) {
                        _log.errorTrace(METHOD_NAME, sqe1);
                        _log.error("getConnection Rollback", "Exception Error Code=" + sqe1.getErrorCode());
                        
                    }*/
                } else if (errorCode == 17002 || errorCode == 17410 || errorCode == 17416 || errorCode == 17143) {
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
                            /*try {
                                dbConnection.rollback();
                            } catch (SQLException sqe1) {
                                _log.errorTrace(METHOD_NAME, sqe1);
                                _log.error("getConnection Rollback", "Exception Error Code=" + sqe1.getErrorCode());
                                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getConnection]", "", "", "", "Database Connection Problem " + sqe1.getErrorCode());
                            }*/
                        } catch (SQLException sqe3) {
                            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getConnection]", "", "", "", "Database Connection Problem " + sqe3.getErrorCode());
                            throw sqe3;
                        }
                    }
                } else
                    throw new BTSLBaseException(DB_CONN_FAILED);
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                
                throw new BTSLBaseException(DB_CONN_FAILED);
            }
            if (dbConnection == null){
                throw new BTSLBaseException(DB_CONN_FAILED);
            }
            // added error log to always print this line
            if (_occm != null){
                _log.error("getConnection 10G", "DB Connections  getting connection from connection pool :" + dbConnection + " active size:" + _occm.getNumberOfActiveConnections(cacheName) + " cache size:" + _occm.getNumberOfAvailableConnections(cacheName));
                activeSizeKey.put("ACTIVESIZE", _occm.getNumberOfActiveConnections(cacheName));
                activeSizeKey.put("CACHESIZE", _occm.getNumberOfAvailableConnections(cacheName));
                DBConenctionPoolLog.log(_occm.getNumberOfActiveConnections(cacheName),_occm.getNumberOfAvailableConnections(cacheName));
            }
        }// end of try
        catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getConnection]", "", "", "", "Database Connection Problem " + be.getErrorCode());
            throw be;
        } catch (Exception ex) {
            // Trap errors
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getConnection]", "", "", "", "Database Connection Problem ");
            _log.error("getConnection", "Error in Connecting to the Database \n" + ex.getMessage() + "\n");
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(DB_CONN_FAILED);
        }// end of catch
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
        final String METHOD_NAME = "getSingleConnection";
        OracleDataSource _ods_single = null;
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
                if (db_url != null && _ods_single == null) {
                    _ods_single = new OracleDataSource();
                    _ods_single.setURL(db_url);
                    _ods_single.setUser(db_user);
                    _ods_single.setPassword(db_password);
                    
                    /*String editionFlag = Constants.getProperty("EDITION_SUPPORT");
                    String edition = Constants.getProperty("EDITION_NAME");
                    if ("Y".equalsIgnoreCase(editionFlag))
                    {
                	prop.put("oracle.jdbc.editionName", edition);
                	_ods_single.setConnectionProperties(prop);
                    }
             
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
                    }*/

                   /* _occm_single = OracleConnectionCacheManager.getConnectionCacheManagerInstance();
                    if (_occm_single.existsCache(cacheName_single)) {
                        _occm_single.removeCache(cacheName_single, 0);
                    }
                    _occm_single.createCache(cacheName_single, _ods_single, prop);*/
                } else
                    return null;
            }// end of the m_connectionSinglePool
        
            dbConnection = _ods_single.getConnection();
            dbConnection.setAutoCommit(false);
            if (_log.isDebugEnabled())
                _log.error("getSingleConnection 10G", "DB Connections  getting connection from connection pool :" + dbConnection + " active size: cache size:");

        
        }// end of try
        catch (Exception ex) {
            // Trap errors
            _log.error("getSingleConnection", "Error in Connecting to the Database \n" + ex.getMessage() + "\n");
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getSingleConnection]", "", "", "", "Database Connection Problem");
        }// end of catch
        return dbConnection;
    } // getConnection

    
    
        
        
    public boolean createPool() {
        final String METHOD_NAME = "createPool";
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
                _log.errorTrace(METHOD_NAME, e);
                minPoolSize = 50;
            }
            int poolSize = 0;
            try {
                poolSize = Integer.parseInt(strPoolSize);
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                ;
                poolSize = 60;
            }

            if (_log.isDebugEnabled())
                _log.debug("createPool 10G", "userid=" + db_user + ", db password=" + db_password + ", db_url=" + db_url + ", minPoolSize=" + minPoolSize + " max poolSize=" + poolSize + " oraclePoolLogFile=" + oraclePoolLogFile);
            try {
                if (db_url != null) {
                    // creating the instatnce of oracle datasource
                    // provide various attribute of database driver to make
                    // connection
                    _ods.setURL(db_url);
                    _ods.setUser(db_user);
                    _ods.setPassword(db_password);
                    
                    String editionFlag = Constants.getProperty("EDITION_SUPPORT");
                    String edition = Constants.getProperty("EDITION_NAME");
                    if ("Y".equalsIgnoreCase(editionFlag))
                    {
                	prop.put("oracle.jdbc.editionName", edition);
                    _ods.setConnectionProperties(prop);
                    }
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
                    if (_occm.existsCache(cacheName)) {
                        _occm.removeCache(cacheName, 0);
                    }
                    _occm.createCache(cacheName, _ods, prop);
                    
                    

                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[createPool]", "", "", "", "Database Connection Problem");
                return false;
            }
            // }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[createPool]", "", "", "", "Database Connection Problem");
            return false;
        } finally {
            // creatingPool=false;
        }
        return true;
    }

    private boolean creatingPoolReportDB = false;
    private static String DB_CONN_FAILED_REPORTDB = "00000";
    private static OracleDataSource _odsReportDB = null;
    private static OracleDataSource _ods_single_report_DB = null;
    private static OracleConnectionCacheManager _occmreportdb = null;
    private static OracleConnectionCacheManager _occmSinglereportdb = null;
    static final String CACHENAME_REPORTDB = "PreTUPSCache_" + Constants.getProperty("REPORT_CACHE_ID");
    static final String CACHENAME_SINGLE_REPORTDB = "PreTUPSCache_" + Constants.getProperty("REPORT_CACHE_ID") + "_SINGLE";

    static {
        try {
            _odsReportDB = new OracleDataSource();
        } catch (Exception e) {
            _log.errorTrace("static", e);
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
    public Connection getReportDBConnection() throws BTSLBaseException {
        Connection dbConnection = null;
        final String METHOD_NAME = "getReportDBConnection";
        try {
            if (_cntReportConnection == 0) {
                _cntReportConnection++;
                if (!createPoolReportDB())
                    throw new BaseException(DB_CONN_FAILED_REPORTDB);
            }
            try {
                dbConnection = _odsReportDB.getConnection();
                dbConnection.setAutoCommit(false);
                /*try {
                    dbConnection.rollback();
                } catch (SQLException sqe1) {
                    _log.errorTrace(METHOD_NAME, sqe1);
                    _log.error("getReportDBConnection Rollback", "Exception Error Code=" + sqe1.getErrorCode());
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getReportDBConnection]", "", "", "", "Database Connection Problem " + sqe1.getErrorCode());
                }*/
            } catch (SQLException sqe) {
                _log.errorTrace(METHOD_NAME, sqe);
                int errorCode = sqe.getErrorCode();
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getReportDBConnection]", "", "", "", "Database Connection Problem " + errorCode);
                _log.error("getReportDBConnection", "Exception Error Code=" + errorCode);
                /*
                 * Error Codes to be handled.
                 * 17002=Io exception
                 * 17008=Closed Connection
                 * 17410=No more data to read from socket
                 * 17416=FATAL
                 */
                if (errorCode == 17143) {
                    // OracleConnectionCacheManager.getConnectionCacheManagerInstance().refreshCache(cacheName,
                    // OracleConnectionCacheManager.REFRESH_ALL_CONNECTIONS);
                    _occmreportdb.getConnectionCacheManagerInstance().refreshCache(CACHENAME_REPORTDB, OracleConnectionCacheManager.REFRESH_ALL_CONNECTIONS);
					dbConnection = _odsReportDB.getConnection();
                    dbConnection.setAutoCommit(false);
                    _log.error("getReportDBConnection ", "Refresh Invalid or Stale Connection found in the Connection Cache");
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getReportDBConnection]", "", "", "", "Database Connection Problem " + errorCode);

                } else if (errorCode == 17008) {
                    dbConnection = _odsReportDB.getConnection();
                    dbConnection.setAutoCommit(false);
                    /*try {
                        dbConnection.rollback();
                    } catch (SQLException sqe1) {
                        _log.errorTrace(METHOD_NAME, sqe1);
                        _log.error("getReportDBConnection Rollback", "Exception Error Code=" + sqe1.getErrorCode());
                    }*/
                } else if (errorCode == 17002 || errorCode == 17410 || errorCode == 17416 || errorCode == 17143) {
                    _log.error("getReportDBConnection", "Recreating pool");
                    if (lockReportConnection.tryLock()) {
                        try {
                            if (!createPoolReportDB())
                                throw new BTSLBaseException(DB_CONN_FAILED_REPORTDB);
                            dbConnection = _odsReportDB.getConnection();
                            dbConnection.setAutoCommit(false);
                        } finally {
                            lockReportConnection.unlock();
                        }

                    } else {
                        try {
                            dbConnection = _odsReportDB.getConnection();
                            dbConnection.setAutoCommit(false);
                            /*try {
                                dbConnection.rollback();
                            } catch (SQLException sqe1) {
                                _log.errorTrace(METHOD_NAME, sqe1);
                                _log.error("getReportDBConnection Rollback", "Exception Error Code=" + sqe1.getErrorCode());
                                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getReportDBConnection]", "", "", "", "Database Connection Problem " + sqe1.getErrorCode());
                            }*/
                        } catch (SQLException sqe3) {
                            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getReportDBConnection]", "", "", "", "Database Connection Problem " + sqe3.getErrorCode());
                            throw sqe3;
                        }
                    }
                } else
                    throw new BTSLBaseException(DB_CONN_FAILED_REPORTDB);
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException(DB_CONN_FAILED_REPORTDB);
            }
            if (dbConnection == null)
                throw new BTSLBaseException(DB_CONN_FAILED_REPORTDB);
            // added error log to always print this line
            if (_occmreportdb != null)
                _log.error("getReportDBConnection 10G", "DB Connections  getting connection from connection pool :" + dbConnection + " active size:" + _occmreportdb.getNumberOfActiveConnections(CACHENAME_REPORTDB) + " cache size:" + _occmreportdb.getNumberOfAvailableConnections(CACHENAME_REPORTDB));
        }// end of try
        catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getReportDBConnection]", "", "", "", "Database Connection Problem");
            throw be;
        } catch (Exception ex) {
            // Trap errors
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getReportDBConnection]", "", "", "", "Database Connection Problem");
            _log.error("getReportDBConnection", "Error in Connecting to the Database \n" + ex.getMessage() + "\n");
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(DB_CONN_FAILED_REPORTDB);
        }// end of catch
        return dbConnection;
    } // getConnection

    /**
     * This method returns a single connection
     * Creation date: (18/07/04)
     * 
     * @return Connection, database connection
     * @param none
     */
    public Connection getReportDBSingleConnection() {
        Connection dbConnection = null;
        final String METHOD_NAME = "getReportDBSingleConnection";
        try {
            if (_ods_single_report_DB == null) {
                String db_url = Constants.getProperty("reportdbdatasourceurl");

                Properties prop = new Properties();
                String db_user = Constants.getProperty("reportdbuserid");
                if (db_user != null)
                    db_user = BTSLUtil.decrypt3DesAesText(db_user);
                String db_password = Constants.getProperty("reportdbpasswd");
                if (db_password != null)
                    db_password = BTSLUtil.decrypt3DesAesText(db_password);
                // System.out.println("getConnection() >> userid=" + db_user +
                // ",  password="+ db_password+ ", db_url="+ db_url);
                if (db_url != null) {
                    _ods_single_report_DB = new OracleDataSource();
                    // provide various attribute of database driver to make
                    // connection
                    _ods_single_report_DB.setURL(db_url);
                    _ods_single_report_DB.setUser(db_user);
                    _ods_single_report_DB.setPassword(db_password);
                    prop.setProperty("MinLimit", "1"); // the cache size is 5 at
                                                       // least
                    prop.setProperty("MaxLimit", "1");
                    prop.setProperty("InitialLimit", "1"); // create 3
                                                           // connections at
                                                           // startup

                    prop.setProperty("ValidateConnection", "TRUE");
                    _ods_single_report_DB.setConnectionCacheProperties(prop);
                    _ods_single_report_DB.setConnectionCachingEnabled(true);
                    if ("Y".equalsIgnoreCase(ONS_REPORT_DB_EN)) {
                        _ods_single_report_DB.setFastConnectionFailoverEnabled(true);
                        _ods_single_report_DB.setONSConfiguration(ONS_CONFIG_REPORT_DB);
                    }
                    _occmSinglereportdb = OracleConnectionCacheManager.getConnectionCacheManagerInstance();
                    if (_occmSinglereportdb.existsCache(CACHENAME_SINGLE_REPORTDB)) {
                        _occmSinglereportdb.removeCache(CACHENAME_SINGLE_REPORTDB, 0);
                    }
                    _occmSinglereportdb.createCache(CACHENAME_SINGLE_REPORTDB, _ods_single_report_DB, prop);

                } else
                    return null;
            }// end of the m_connectionSinglePool

            dbConnection = _ods_single_report_DB.getConnection();
            dbConnection.setAutoCommit(false);
            if (_log.isDebugEnabled())
                _log.error("getReportDBSingleConnection 10G", "DB Connections  getting connection from connection pool :" + dbConnection + " active size:" + _occmSinglereportdb.getNumberOfActiveConnections(CACHENAME_REPORTDB) + " cache size:" + _occmSinglereportdb.getNumberOfAvailableConnections(CACHENAME_REPORTDB));
        }// end of try
        catch (Exception ex) {
            // Trap errors
            _log.error("getReportDBSingleConnection", "Error in Connecting to the Database \n" + ex.getMessage() + "\n");
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getReportDBSingleConnection]", "", "", "", "Database Connection Problem");
        }// end of catch
        return dbConnection;
    } // getConnection

    public boolean createPoolReportDB() {
        final String METHOD_NAME = "createPoolReportDB";
        try {
            /*
             * if(!creatingPoolReportDB)
             * {
             * creatingPoolReportDB=true;
             */
            if (_log.isDebugEnabled())
                _log.debug("createPoolReportDB", "Creating oracle connection pool");

            Properties prop = new Properties();
            String db_url = Constants.getProperty("reportdbdatasourceurl");

            String db_user = Constants.getProperty("reportdbuserid");
            if (db_user != null)
                db_user = BTSLUtil.decrypt3DesAesText(db_user);
            String db_password = Constants.getProperty("reportdbpasswd");
            if (db_password != null)
                db_password = BTSLUtil.decrypt3DesAesText(db_password);

            String strMinPoolSize = Constants.getProperty("reportdbminpoolsize");
            String strPoolSize = Constants.getProperty("reportdbpoolsize");
            String oraclePoolLogFile = Constants.getProperty("oraclePoolLogFile");

            if (_log.isDebugEnabled())
                _log.debug("createPoolReportDB 10G", "userid=" + db_user + ", db password=" + db_password + ", db_url=" + db_url + ", minPoolSize=" + strMinPoolSize + " max poolSize=" + strPoolSize + " oraclePoolLogFile=" + oraclePoolLogFile);
            try {
                if (db_url != null) {
                    // creating the instatnce of oracle datasource
                    // _odsReportDB = new OracleDataSource();
                    // provide various attribute of database driver to make
                    // connection
                    _odsReportDB.setURL(db_url);
                    _odsReportDB.setUser(db_user);
                    _odsReportDB.setPassword(db_password);
                    prop.setProperty("MinLimit", strPoolSize); // the cache size
                                                               // is 5 at least
                    prop.setProperty("MaxLimit", strPoolSize);
                    prop.setProperty("InitialLimit", strMinPoolSize); // create
                                                                      // 3
                                                                      // connections
                                                                      // at
                                                                      // startup

                    prop.setProperty("ValidateConnection", "TRUE");
                    _odsReportDB.setConnectionCacheProperties(prop);
                    _odsReportDB.setConnectionCachingEnabled(true);
                    if ("Y".equalsIgnoreCase(ONS_REPORT_DB_EN)) {
                        _odsReportDB.setFastConnectionFailoverEnabled(true);
                        _odsReportDB.setONSConfiguration(ONS_CONFIG_REPORT_DB);
                    }
                    _occmreportdb = OracleConnectionCacheManager.getConnectionCacheManagerInstance();
                    if (_occmreportdb.existsCache(CACHENAME_REPORTDB)) {
                        _occmreportdb.removeCache(CACHENAME_REPORTDB, 0);
                    }
                    _occmreportdb.createCache(CACHENAME_REPORTDB, _odsReportDB, prop);

                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[createPoolReportDB]", "", "", "", "Database Connection Problem");
                return false;
            }
            // }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[createPoolReportDB]", "", "", "", "Database Connection Problem");
            return false;
        } finally {
            // creatingPoolReportDB=false;
        }
        return true;
    }

    private boolean creatingPoolCurrentReportDB = false;
    private static String DB_CONN_FAILED_CURRENT_REPORTDB = "00000";
    private static OracleDataSource _odsCurrentReportDB = null;
    private static OracleDataSource _ods_single_current_report_DB = null;
    private static OracleConnectionCacheManager _occmcurrentreportdb = null;
    private static OracleConnectionCacheManager _occmSinglecurrentreportdb = null;
    static final String CACHENAME_CURRENT_REPORTDB = "PreTUPSCache_" + Constants.getProperty("CURRENT_REPORT_CACHE_ID");
    static final String CACHENAME_SINGLE_CURRENT_REPORTDB = "PreTUPSCache_" + Constants.getProperty("CURRENT_REPORT_CACHE_ID") + "_SINGLE";

    static {
        try {
            _odsCurrentReportDB = new OracleDataSource();
        } catch (Exception e) {
            _log.errorTrace("static", e);
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
    public Connection getCurrentReportDBConnection() throws BTSLBaseException {
        Connection dbConnection = null;
        final String METHOD_NAME = "getCurrentReportDBConnection";
        try {

            if (_cntCurrentReportDBConnection == 0) {
                _cntCurrentReportDBConnection++;
                if (!createPoolCurrentReportDB())
                    throw new BaseException(DB_CONN_FAILED_CURRENT_REPORTDB);
            }
            try {
                dbConnection = _odsCurrentReportDB.getConnection();
                dbConnection.setAutoCommit(false);
                try {
                    dbConnection.rollback();
                } catch (SQLException sqe1) {
                    _log.errorTrace(METHOD_NAME, sqe1);
                    _log.error("getCurrentReportDBConnection Rollback", "Exception Error Code=" + sqe1.getErrorCode());
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getCurrentReportDBConnection]", "", "", "", "Database Connection Problem " + sqe1.getErrorCode());
                }
            } catch (SQLException sqe) {
                _log.errorTrace(METHOD_NAME, sqe);
                int errorCode = sqe.getErrorCode();
                _log.error("getCurrentReportDBConnection", "Exception Error Code=" + errorCode);
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getCurrentReportDBConnection]", "", "", "", "Database Connection Problem " + errorCode);
                /*
                 * Error Codes to be handled.
                 * 17002=Io exception
                 * 17008=Closed Connection
                 * 17410=No more data to read from socket
                 * 17416=FATAL
                 */
                if (errorCode == 17143) {
                    // OracleConnectionCacheManager.getConnectionCacheManagerInstance().refreshCache(cacheName,
                    // OracleConnectionCacheManager.REFRESH_ALL_CONNECTIONS);
                    _occmcurrentreportdb.getConnectionCacheManagerInstance().refreshCache(CACHENAME_CURRENT_REPORTDB, OracleConnectionCacheManager.REFRESH_ALL_CONNECTIONS);
					dbConnection = _odsCurrentReportDB.getConnection();
                    dbConnection.setAutoCommit(false);
                    _log.error("getCurrentReportDBConnection ", "Refresh Invalid or Stale Connection found in the Connection Cache");

                } else if (errorCode == 17008) {
                    dbConnection = _odsCurrentReportDB.getConnection();
                    dbConnection.setAutoCommit(false);
                    try {
                        dbConnection.rollback();
                    } catch (SQLException sqe1) {
                        _log.errorTrace(METHOD_NAME, sqe1);
                        _log.error("getCurrentReportDBConnection Rollback", "Exception Error Code=" + sqe1.getErrorCode());
                    }
                } else if (errorCode == 17002 || errorCode == 17410 || errorCode == 17416 || errorCode == 17143) {
                    _log.error("getCurrentReportDBConnection", "Recreating pool");
                    if (lockCurrentReportConnection.tryLock()) {
                        try {
                            if (!createPoolCurrentReportDB())
                                throw new BTSLBaseException(DB_CONN_FAILED_CURRENT_REPORTDB);
                            dbConnection = _odsCurrentReportDB.getConnection();
                            dbConnection.setAutoCommit(false);
                        } finally {
                            lockCurrentReportConnection.unlock();
                        }

                    } else {
                        try {
                            dbConnection = _odsCurrentReportDB.getConnection();
                            dbConnection.setAutoCommit(false);
                            try {
                                dbConnection.rollback();
                            } catch (SQLException sqe1) {
                                _log.errorTrace(METHOD_NAME, sqe1);
                                _log.error("getCurrentReportDBConnection Rollback", "Exception Error Code=" + sqe1.getErrorCode());
                                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getCurrentReportDBConnection]", "", "", "", "Database Connection Problem " + sqe1.getErrorCode());
                            }
                        } catch (SQLException sqe3) {
                            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getCurrentReportDBConnection]", "", "", "", "Database Connection Problem " + sqe3.getErrorCode());
                            throw sqe3;
                        }
                    }
                } else
                    throw new BTSLBaseException(DB_CONN_FAILED_CURRENT_REPORTDB);
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException(DB_CONN_FAILED_CURRENT_REPORTDB);
            }
            if (dbConnection == null)
                throw new BTSLBaseException(DB_CONN_FAILED_CURRENT_REPORTDB);
            // added error log to always print this line
            if (_occmcurrentreportdb != null)
                _log.error("getCurrentReportDBConnection 10G", "DB Connections  getting connection from connection pool :" + dbConnection + " active size:" + _occmcurrentreportdb.getNumberOfActiveConnections(CACHENAME_CURRENT_REPORTDB) + " cache size:" + _occmcurrentreportdb.getNumberOfAvailableConnections(CACHENAME_CURRENT_REPORTDB));
        }// end of try
        catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getCurrentReportDBConnection]", "", "", "", "Database Connection Problem");
            throw be;
        } catch (Exception ex) {
            // Trap errors
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getCurrentReportDBConnection]", "", "", "", "Database Connection Problem");
            _log.error("getReportDBConnection", "Error in Connecting to the Database \n" + ex.getMessage() + "\n");
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(DB_CONN_FAILED_CURRENT_REPORTDB);
        }// end of catch
        return dbConnection;
    } // getConnection

    /**
     * This method returns a single connection
     * Creation date: (18/07/04)
     * 
     * @return Connection, database connection
     * @param none
     */
    public Connection getCurrentReportDBSingleConnection() {
        Connection dbConnection = null;
        final String METHOD_NAME = "getCurrentReportDBSingleConnection";
        try {
            if (_ods_single_current_report_DB == null) {
                String db_url = Constants.getProperty("currentDateRptDBDataSourceURL");
                Properties prop = new Properties();
                String db_user = Constants.getProperty("currentReportDBUserId");
                if (db_user != null)
                    db_user = BTSLUtil.decrypt3DesAesText(db_user);
                String db_password = Constants.getProperty("currentReportDBPasswd");
                if (db_password != null)
                    db_password = BTSLUtil.decrypt3DesAesText(db_password);
                // System.out.println("getConnection() >> userid=" + db_user +
                // ",  password="+ db_password+ ", db_url="+ db_url);
                if (db_url != null) {
                    _ods_single_current_report_DB = new OracleDataSource();
                    // provide various attribute of database driver to make
                    // connection
                    _ods_single_current_report_DB.setURL(db_url);
                    _ods_single_current_report_DB.setUser(db_user);
                    _ods_single_current_report_DB.setPassword(db_password);
                    prop.setProperty("MinLimit", "1"); // the cache size is 5 at
                                                       // least
                    prop.setProperty("MaxLimit", "1");
                    prop.setProperty("InitialLimit", "1"); // create 3
                                                           // connections at
                                                           // startup
                    prop.setProperty("ValidateConnection", "TRUE");
                    _ods_single_current_report_DB.setConnectionCacheProperties(prop);
                    _ods_single_current_report_DB.setConnectionCachingEnabled(true);
                    if ("Y".equalsIgnoreCase(ONS_CURRENT_REPORT_DB_EN)) {
                        _ods_single_current_report_DB.setFastConnectionFailoverEnabled(true);
                        _ods_single_current_report_DB.setONSConfiguration(ONS_CONFIG_CURRENT_REPORT_DB);
                    }
                    _occmSinglecurrentreportdb = OracleConnectionCacheManager.getConnectionCacheManagerInstance();
                    if (_occmSinglecurrentreportdb.existsCache(CACHENAME_CURRENT_REPORTDB)) {
                        _occmSinglecurrentreportdb.removeCache(CACHENAME_CURRENT_REPORTDB, 0);
                    }
                    _occmSinglecurrentreportdb.createCache(CACHENAME_CURRENT_REPORTDB, _ods_single_current_report_DB, prop);

                } else
                    return null;
            }// end of the m_connectionSinglePool

            dbConnection = _ods_single_current_report_DB.getConnection();
            dbConnection.setAutoCommit(false);
            if (_log.isDebugEnabled())
                _log.error("getCurrentReportDBSingleConnection 10G", "DB Connections  getting connection from connection pool :" + dbConnection + " active size:" + _occmSinglecurrentreportdb.getNumberOfActiveConnections(CACHENAME_SINGLE_CURRENT_REPORTDB) + " cache size:" + _occmSinglecurrentreportdb.getNumberOfAvailableConnections(CACHENAME_SINGLE_CURRENT_REPORTDB));
        }// end of try
        catch (Exception ex) {
            // Trap errors
            _log.error("getCurrentReportDBSingleConnection", "Error in Connecting to the Database \n" + ex.getMessage() + "\n");
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getCurrentReportDBSingleConnection]", "", "", "", "Database Connection Problem");
        }// end of catch
        return dbConnection;
    } // getConnection

    public boolean createPoolCurrentReportDB() {
        final String METHOD_NAME = "createPoolCurrentReportDB";
        try {
            /*
             * if(!creatingPoolCurrentReportDB)
             * {
             * creatingPoolCurrentReportDB=true;
             */
            if (_log.isDebugEnabled())
                _log.debug("createPoolCurrentReportDB", "Creating oracle connection pool");
            Properties prop = new Properties();
            String db_url = Constants.getProperty("currentDateRptDBDataSourceURL");

            String db_user = Constants.getProperty("currentReportDBUserId");
            if (db_user != null)
                db_user = BTSLUtil.decrypt3DesAesText(db_user);
            String db_password = Constants.getProperty("currentReportDBPasswd");
            if (db_password != null)
                db_password = BTSLUtil.decrypt3DesAesText(db_password);

            String strMinPoolSize = Constants.getProperty("currentReportDBMinPoolSize");
            String strPoolSize = Constants.getProperty("currentReportDBPoolSize");
            String oraclePoolLogFile = Constants.getProperty("oraclePoolLogFile");

            if (_log.isDebugEnabled())
                _log.debug("createPoolCurrentReportDB 10G", "userid=" + db_user + ", db password=" + db_password + ", db_url=" + db_url + ", minPoolSize=" + strMinPoolSize + " max poolSize=" + strPoolSize + " oraclePoolLogFile=" + oraclePoolLogFile);
            try {
                if (db_url != null) {
                    // creating the instatnce of oracle datasource
                    // _odsCurrentReportDB = new OracleDataSource();
                    // provide various attribute of database driver to make
                    // connection
                    _odsCurrentReportDB.setURL(db_url);
                    _odsCurrentReportDB.setUser(db_user);
                    _odsCurrentReportDB.setPassword(db_password);
                    prop.setProperty("MinLimit", strPoolSize); // the cache size
                                                               // is 5 at least
                    prop.setProperty("MaxLimit", strPoolSize);
                    prop.setProperty("InitialLimit", strMinPoolSize); // create
                                                                      // 3
                                                                      // connections
                                                                      // at
                                                                      // startup
                    prop.setProperty("ValidateConnection", "TRUE");
                    _odsCurrentReportDB.setConnectionCacheProperties(prop);
                    _odsCurrentReportDB.setConnectionCachingEnabled(true);
                    if ("Y".equalsIgnoreCase(ONS_CURRENT_REPORT_DB_EN)) {
                        _odsCurrentReportDB.setFastConnectionFailoverEnabled(true);
                        _odsCurrentReportDB.setONSConfiguration(ONS_CONFIG_CURRENT_REPORT_DB);
                    }
                    _occmcurrentreportdb = OracleConnectionCacheManager.getConnectionCacheManagerInstance();
                    if (_occmcurrentreportdb.existsCache(CACHENAME_CURRENT_REPORTDB)) {
                        _occmcurrentreportdb.removeCache(CACHENAME_CURRENT_REPORTDB, 0);
                    }
                    _occmcurrentreportdb.createCache(CACHENAME_CURRENT_REPORTDB, _odsCurrentReportDB, prop);

                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[createPoolCurrentReportDB]", "", "", "", "Database Connection Problem");
                return false;
            }
            // }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[createPoolCurrentReportDB]", "", "", "", "Database Connection Problem");
            return false;
        } finally {
            // creatingPoolCurrentReportDB=false;
        }
        return true;
    }

    // For Separate External Database- 18/12/13
    private boolean creatingPoolExternalDB = false;
    private static String DB_CONN_FAILED_EXTERNALDB = "00000";
    private static OracleDataSource _odsExternalDB = null;
    // private static OracleDataSource _ods_single_external_DB=null;
    private static OracleConnectionCacheManager _occmexternaldb = null;
    // private static OracleConnectionCacheManager _occmSingleexternaldb = null;
    static final String cacheNameExternalDB = "PreTUPSCache_" + Constants.getProperty("EXTERNAL_CACHE_ID");
    static final String cacheNameSingleExternalDB = "PreTUPSCache_" + Constants.getProperty("EXTERNAL_CACHE_ID") + "_SINGLE";
    static {
        try {
            _odsExternalDB = new OracleDataSource();
        } catch (Exception e) {
            _log.errorTrace("static", e);
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
    public Connection getExternalDBConnection() throws BTSLBaseException {
        Connection dbConnection = null;
        final String METHOD_NAME = "getExternalDBConnection";
        try {

            if (_cntExternalDBConnection == 0) {
                _cntExternalDBConnection++;
                if (!createPoolExternalDB())
                    throw new BaseException(DB_CONN_FAILED_EXTERNALDB);
            }
            try {
                dbConnection = _odsExternalDB.getConnection();
                dbConnection.setAutoCommit(false);
                try {
                    dbConnection.rollback();
                } catch (SQLException sqe1) {
                    _log.errorTrace(METHOD_NAME, sqe1);
                    _log.error("getExternalDBConnection Rollback", "Exception Error Code=" + sqe1.getErrorCode());
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getExternalDBConnection]", "", "", "", "Database Connection Problem " + sqe1.getErrorCode());
                }
            }

            catch (SQLException sqe) {

                _log.errorTrace(METHOD_NAME, sqe);
                int errorCode = sqe.getErrorCode();
                _log.error("getExternalDBConnection", "Exception Error Code=" + errorCode);
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getExternalDBConnection]", "", "", "", "Database Connection Problem " + errorCode);
                /*
                 * Error Codes to be handled.
                 * 17002=Io exception
                 * 17008=Closed Connection
                 * 17410=No more data to read from socket
                 * 17416=FATAL
                 */
                if (errorCode == 17143) {
                    // OracleConnectionCacheManager.getConnectionCacheManagerInstance().refreshCache(cacheName,
                    // OracleConnectionCacheManager.REFRESH_ALL_CONNECTIONS);
                    _occmexternaldb.getConnectionCacheManagerInstance().refreshCache(cacheNameExternalDB, OracleConnectionCacheManager.REFRESH_ALL_CONNECTIONS);
					dbConnection = _odsExternalDB.getConnection();
                    dbConnection.setAutoCommit(false);
                    _log.error("getExternalDBConnection ", "Refresh Invalid or Stale Connection found in the Connection Cache");

                } else if (errorCode == 17008) {
                    dbConnection = _odsExternalDB.getConnection();
                    dbConnection.setAutoCommit(false);
                    try {
                        dbConnection.rollback();
                    } catch (SQLException sqe1) {
                        _log.errorTrace(METHOD_NAME, sqe1);
                        _log.error("getExternalDBConnection Rollback", "Exception Error Code=" + sqe1.getErrorCode());
                    }
                } else if (errorCode == 17002 || errorCode == 17410 || errorCode == 17416 || errorCode == 17143) {
                    _log.error("getExternalDBConnection", "Recreating pool");
                    if (lockExternalDBConnection.tryLock()) {
                        try {
                            if (!createPoolExternalDB())
                                throw new BTSLBaseException(DB_CONN_FAILED_EXTERNALDB);
                            dbConnection = _odsExternalDB.getConnection();
                            dbConnection.setAutoCommit(false);
                        } finally {
                            lockExternalDBConnection.unlock();
                        }

                    } else {
                        try {
                            dbConnection = _odsExternalDB.getConnection();
                            dbConnection.setAutoCommit(false);
                            try {
                                dbConnection.rollback();
                            } catch (SQLException sqe1) {
                                _log.errorTrace(METHOD_NAME, sqe1);
                                _log.error("getExternalDBConnection Rollback", "Exception Error Code=" + sqe1.getErrorCode());
                                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getConnection]", "", "", "", "Database Connection Problem " + sqe1.getErrorCode());
                            }
                        } catch (SQLException sqe3) {
                            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getConnection]", "", "", "", "Database Connection Problem " + sqe3.getErrorCode());
                            throw sqe3;
                        }
                    }
                } else
                    throw new BTSLBaseException(DB_CONN_FAILED_EXTERNALDB);
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException(DB_CONN_FAILED_EXTERNALDB);
            }
            if (dbConnection == null)
                throw new BTSLBaseException(DB_CONN_FAILED_EXTERNALDB);
            // added error log to always print this line
            if (_occmexternaldb != null)
                _log.error("getExternalDBConnection 10G", "DB Connections  getting connection from connection pool :" + dbConnection + " active size:" + _occmexternaldb.getNumberOfActiveConnections(cacheNameExternalDB) + " cache size:" + _occmexternaldb.getNumberOfAvailableConnections(cacheNameExternalDB));
        }// end of try
        catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getExternalDBConnection]", "", "", "", "Database Connection Problem");
            throw be;
        } catch (Exception ex) {
            // Trap errors
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getExternalDBConnection]", "", "", "", "Database Connection Problem");
            _log.error("getExternalDBConnection", "Error in Connecting to the Database \n" + ex.getMessage() + "\n");
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(DB_CONN_FAILED_EXTERNALDB);
        }// end of catch
        return dbConnection;
    } // getConnection

    public boolean createPoolExternalDB() {
        final String METHOD_NAME = "createPoolExternalDB";
        try {
            /*
             * if(!creatingPoolExternalDB)
             * {
             * creatingPoolExternalDB=true;
             */
            if (_log.isDebugEnabled())
                _log.debug("createPoolExternalDB", "Creating oracle connection pool");

            Properties prop = new Properties();
            String db_url = Constants.getProperty("externaldbdatasourceurl");

            String db_user = Constants.getProperty("externaldbuserid");
            if (db_user != null)
                db_user = BTSLUtil.decrypt3DesAesText(db_user);
            String db_password = Constants.getProperty("externaldbpasswd");
            if (db_password != null)
                db_password = BTSLUtil.decrypt3DesAesText(db_password);

            String strMinPoolSize = Constants.getProperty("externaldbminpoolsize");
            String strPoolSize = Constants.getProperty("externaldbpoolsize");
            String oraclePoolLogFile = Constants.getProperty("oraclePoolLogFile");

            if (_log.isDebugEnabled())
                _log.debug("createPoolExternalDB 10G", "userid=" + db_user + ", db password=" + db_password + ", db_url=" + db_url + ", minPoolSize=" + strMinPoolSize + " max poolSize=" + strPoolSize + " oraclePoolLogFile=" + oraclePoolLogFile);
            try {
                if (db_url != null) {
                    // creating the instatnce of oracle datasource
                    // _odsExternalDB = new OracleDataSource();
                    // provide various attribute of database driver to make
                    // connection
                    _odsExternalDB.setURL(db_url);
                    _odsExternalDB.setUser(db_user);
                    _odsExternalDB.setPassword(db_password);
                    prop.setProperty("MinLimit", strPoolSize); // the cache size
                                                               // is 5 at least
                    prop.setProperty("MaxLimit", strPoolSize);
                    prop.setProperty("InitialLimit", strMinPoolSize); // create
                                                                      // 3
                                                                      // connections
                                                                      // at
                                                                      // startup

                    prop.setProperty("ValidateConnection", "TRUE");
                    _odsExternalDB.setConnectionCacheProperties(prop);
                    _odsExternalDB.setConnectionCachingEnabled(true);
                    if ("Y".equalsIgnoreCase(ONS_EXTERNAL_DB_EN)) {
                        _odsExternalDB.setFastConnectionFailoverEnabled(true);
                        _odsExternalDB.setONSConfiguration(ONS_CONFIG_EXTERNAL_DB);
                    }
                    _occmexternaldb = OracleConnectionCacheManager.getConnectionCacheManagerInstance();
                    if (_occmexternaldb.existsCache(cacheNameExternalDB)) {
                        _occmexternaldb.removeCache(cacheNameExternalDB, 0);
                    }
                    _occmexternaldb.createCache(cacheNameExternalDB, _odsExternalDB, prop);

                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[createPoolExternalDB]", "", "", "", "Database Connection Problem");
                return false;
            }
            // }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[createPoolExternalDB]", "", "", "", "Database Connection Problem");
            return false;
        } finally {
            // creatingPoolExternalDB=false;
        }
        return true;
    }

}// end of class OJDBCPoolManager

