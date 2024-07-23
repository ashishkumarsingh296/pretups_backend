package com.btsl.db.pool;

/*
 * OraclePoolManager.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Pradyumn Mishra 06/09/2013
 * ------------------------------------------------------------------------
 * Copyright (c) 2012 Comviva technologies Ltd..
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.BTSLDBManager;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

import oracle.jdbc.pool.OracleConnectionCacheManager;
import oracle.jdbc.pool.OracleDataSource;

public class OraclePoolManager extends BTSLDBManager {

    private static Context _context = null;
    private static DataSource _dataSource = null;
    private static Context _rptcontext = null;
    private static DataSource _rptdataSource = null;
    // External DB-18/12/13
    private static Context _extcontext = null;
    private static DataSource _extdataSource = null;

    private static OracleDataSource _ods_single = null;
    private static OracleConnectionCacheManager _occm_single;

    private static String ONS_CONFIG = Constants.getProperty("ONS_CONFIG");
    private static String ONS_EN = Constants.getProperty("ONS_EN");
    private static final String cacheName_single = "PreTUPSCache_" + Constants.getProperty("CACHE_ID") + "_SINGLE";

    private Log _log = LogFactory.getLog(this.getClass().getName());
    public int getActiveConnection() {
    	return 1;
    }
    public int getAvailableConnection() {
        return 100;
    }
    public boolean createWASPool() throws BTSLBaseException {// Connection
                                                             // connection =
                                                             // null;
        _log.debug("OraclePoolManager", " createWASPool Entered");
        final String METHOD_NAME = "createWASPool";
        try {
            // _context = new InitialContext();
            // _dataSource =
            // (DataSource)_context.lookup(Constants.getProperty("DataSourceJNDIName"));
            // _dataSource = (DataSource)new
            // InitialContext().lookup("java:comp/env/jdbc/DB");
            _context = new InitialContext();
            Context _envContext = (Context) _context.lookup("java:/comp/env");
            _dataSource = (DataSource) _envContext.lookup("jdbc/DB");
            // connection = _dataSource.getConnection();

        } catch (Exception e) {
            // _//log.error("OraclePoolManager Rollback","Exception Error Code="+e.getMessage());
            // EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OraclePoolManager[createWASPool]","","","","Database Connection Problem");
            // throw new BTSLBaseException(e.getMessage());
            _log.error("getConnection Rollback", "Exception Error Code=" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            return false;

        }
        return true;
    }

    public boolean createRPTWASPool() throws BTSLBaseException {
        _log.debug("OraclePoolManager", " createRPTWASPool Entered");
        final String METHOD_NAME = "createRPTWASPool";
        try {
            _rptcontext = new InitialContext();
            _rptdataSource = (DataSource) _rptcontext.lookup(Constants.getProperty("ReportDataSourceJNDIName"));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("getConnection Rollback", "Exception Error Code=" + e.getMessage());
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OraclePoolManager[createRPTWASPool]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,"Database Connection Problem",e);
        }
        return true;
    }

    // External DB-18/12/13
    public boolean createEXTWASPool() throws BTSLBaseException {
        _log.debug("OraclePoolManager", " createEXTWASPool Entered");
        final String METHOD_NAME = "createEXTWASPool";
        try {
            _extcontext = new InitialContext();
            _extdataSource = (DataSource) _extcontext.lookup(Constants.getProperty("ExternalDataSourceJNDIName"));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("getConnection Rollback", "Exception Error Code=" + e.getMessage());
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OraclePoolManager[createEXTWASPool]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,"Database Connection Problem",e);
        }
        return true;
    }

    public Connection getWASConnection() throws BTSLBaseException {
        final String METHOD_NAME = "getWASConnection";
        _log.debug("OraclePoolManager ", "  getWASConnection ds = " + _dataSource);
        Connection connection = null;
        try {
            if (_dataSource == null)
                if (!createWASPool())
                    return null;

            connection = _dataSource.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("getConnection Rollback", "Exception Error Code=" + e.getMessage());
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OraclePoolManager[getWASConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,"Database Connection Problem",e);
        }
        _log.debug("OraclePoolManager ", "  getWASConnection Exit ", "  connection = " + connection);
        return connection;

    }

    /**
     * @Date sep 06, 2013
     * @Return RPT Connection
     * @return
     **/
    public Connection getRPTWASConnection() throws BTSLBaseException {
        _log.debug("OraclePoolManager ", "  getRPTWASConnection ds = " + _rptdataSource);
        final String METHOD_NAME = "getRPTWASConnection";
        Connection rConnection = null;
        try {
            if (_rptdataSource == null)
                if (!createRPTWASPool())
                    return null;

            rConnection = _rptdataSource.getConnection();
            rConnection.setAutoCommit(false);
        } catch (SQLException e) {
            _log.error("getConnection Rollback", "Exception Error Code=" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OraclePoolManager[getRPTWASConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,"Database Connection Problem",e);
        }
        _log.debug("Reporting JNDI", " " + Constants.getProperty("ReportDataSourceJNDIName"));
        return rConnection;

    }

    /**
     * @Date 6 september,2013
     * @Return Connection
     * @return
     **/
    public Connection getConnection() throws BTSLBaseException {
        final String METHOD_NAME = "getConnection";
        _log.debug("OraclePoolManager ", "  getConnection Entered");
        Connection con = null;
        try {
            con = getWASConnection();
        } catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            _log.error("getConnection Rollback", "Exception Error Code=" + ex.getMessage());
        }
        return con;
    }

    /**
     * @Date Feb 14, 2012
     * @Return Connection for Reports
     * @return
     **/
    public Connection getReportConnection() {
        final String METHOD_NAME = "getReportConnection";
        Connection con = null;
        try {
            con = getRPTWASConnection();
        } catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            _log.error("getConnection Rollback", "Exception Error Code=" + ex.getMessage());
        }
        return con;
    }

    /**
     * @Date Feb 14, 2012
     * @Return Connection
     * @return
     **/
    public Connection getPretupConnection() {
        final String METHOD_NAME = "getPretupConnection";
        Connection con = null;
        try {
            con = getWASConnection();
        } catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            _log.error("getConnection Rollback", "Exception Error Code=" + ex.getMessage());
        }
        return con;
    }

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
        }// end of try
        catch (Exception ex) {
            // Trap errors
            _log.error("getSingleConnection", "Error in Connecting to the Database \n" + ex.getMessage() + "\n");
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBC14PoolManager[getSingleConnection]", "", "", "", "Database Connection Problem");
        }// end of catch
        return dbConnection;
    } // getConnection

    /**
     * This method is for Reporting returns connection from the connection pool,
     * if there is no connection
     * in the pool then adds new connection in the connection pool.
     * 
     * @return Connection, database connection
     * @param none
     * @Modified Aug 27,2012
     */
    public Connection getReportDBConnection() throws BTSLBaseException {
        _log.debug("getReportDBConnection  ", "  Entered ");
        final String METHOD_NAME = "getReportDBConnection";
        Connection dbConnection = null;
        try {
            _log.debug("OraclePoolManager ", "  getReportDBConnection ds " + _rptdataSource, "datasoruse" + _dataSource);
            if (_rptdataSource == null)
                if (!createRPTWASPool())
                    return null;

            dbConnection = _rptdataSource.getConnection();
            dbConnection.setAutoCommit(false);
            _log.debug("OraclePoolManager ", "  getReportDBConnection ds " + _rptdataSource);
        } catch (SQLException e) {
            _log.error("getConnection Rollback", "Exception Error Code=" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OraclePoolManager[getReportDBConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,"Database Connection Problem",e);
        }
        _log.debug("Reporting JNDI", " " + Constants.getProperty("ReportDataSourceJNDIName"));
        return dbConnection;
    } // getConnection

    /**
     * This method is for Reporting returns connection from the connection pool,
     * if there is no connection
     * in the pool then adds new connection in the connection pool.
     * 
     * @return Connection, database connection
     * @param none
     * @Modified Aug 27,2012
     * 
     */
    public Connection getCurrentReportDBConnection() throws BTSLBaseException {
        final String METHOD_NAME = "getCurrentReportDBConnection";
        _log.debug("getCurrentReportDBConnection ", "  getCurrentReportDBConnection Entereed");
        Connection dbConnection = null;
        try {
            _log.debug("OraclePoolManager ", "  getCurrentReportDBConnection ds = " + _rptdataSource);
            if (_rptdataSource == null)
                if (!createRPTWASPool())
                    return null;

            dbConnection = _rptdataSource.getConnection();
            dbConnection.setAutoCommit(false);
            _log.debug("OraclePoolManager ", "  getCurrentReportDBConnection ds " + _rptdataSource);
        } catch (SQLException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("getConnection Rollback", "Exception Error Code=" + e.getMessage());
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OraclePoolManager[getCurrentReportDBConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,"Database Connection Problem",e);
        }
        _log.debug("Reporting JNDI", " " + Constants.getProperty("ReportDataSourceJNDIName"));
        return dbConnection;
    } // getConnection

    /**
     * This method is for External Pooling. returns connection from the
     * connection pool, if there is no connection
     * in the pool then adds new connection in the connection pool.
     * 
     * @return Connection, database connection
     * @param none
     * @Modified Dec 18,2013
     */
    public Connection getExternalDBConnection() throws BTSLBaseException {
        _log.debug("getExternalDBConnection  ", "  Entered ");
        final String METHOD_NAME = "getExternalDBConnection";
        Connection dbConnection = null;
        try {
            _log.debug("OraclePoolManager ", "  getExternalDBConnection ds " + _extdataSource, "datasoruse" + _dataSource);
            if (_extdataSource == null)
                if (!createEXTWASPool())
                    return null;

            dbConnection = _extdataSource.getConnection();
            dbConnection.setAutoCommit(false);
            _log.debug("OraclePoolManager ", "  getExternalDBConnection ds " + _extdataSource);
        } catch (SQLException e) {
            _log.error("getConnection Rollback", "Exception Error Code=" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OraclePoolManager[getExternalDBConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,"Database Connection Problem",e);
        }
        _log.debug("External JNDI", " " + Constants.getProperty("ExternalDataSourceJNDIName"));
        return dbConnection;
    } // getConnection

}
