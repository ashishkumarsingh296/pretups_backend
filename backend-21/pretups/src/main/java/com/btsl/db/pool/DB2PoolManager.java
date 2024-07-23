package com.btsl.db.pool;

/*
 * DB2PoolManager.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Lalit 14/11/2011 Initial Creation
 * Jasmine 27/09/2012 Modified
 * ------------------------------------------------------------------------
 * Copyright (c) 2012 Comviva technologies Ltd..
 */

import java.sql.Connection;
import java.sql.DriverManager;
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

public class DB2PoolManager extends BTSLDBManager {

    private static Context _context = null;
    private static DataSource _dataSource = null;
    private static Context _rptcontext = null;
    private static DataSource _rptdataSource = null;
    private Log _log = LogFactory.getLog(this.getClass().getName());
    // External DB- 18/12/13
    private static Context _extcontext = null;
    private static DataSource _extdataSource = null;

    /**
     * @Date Feb 14, 2012
     *       This method is to create data source for the configured JNDI in
     *       constants props
     * @Return boolean
     * @return
     **/
    public int getActiveConnection() {
    	return 1;
    }
    public int getAvailableConnection() {
        return 100;
    }
    public boolean createWASPool() throws BTSLBaseException {
        _log.debug("DB2PoolManager", " createWASPool Entered");
        final String METHOD_NAME = "createWASPool";
        try {
            _context = new InitialContext();
            _dataSource = (DataSource) _context.lookup(Constants.getProperty("DataSourceJNDIName"));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("DB2PoolManager Rollback", "Exception Error Code=" + e.getMessage());
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DB2PoolManager[createWASPool]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,"Database Connection Problem",e);
        }
        return true;
    }

    /**
     * @Date Feb 14, 2012
     * @Modified Aug 27,2012
     *           This method is to create RPT data source for the configured
     *           JNDI in constants props
     * @Return boolean
     * @return
     **/
    public boolean createRPTWASPool() throws BTSLBaseException {
        _log.debug("DB2PoolManager", " createRPTWASPool Entered");
        final String METHOD_NAME = "createRPTWASPool";
        try {
            _rptcontext = new InitialContext();
            _rptdataSource = (DataSource) _rptcontext.lookup(Constants.getProperty("ReportDataSourceJNDIName"));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("getConnection Rollback", "Exception Error Code=" + e.getMessage());
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DB2PoolManager[createRPTWASPool]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,"Database Connection Problem",e);
        }
        return true;
    }

    /**
     * @Date Dec 12, 2013
     * @ModifiedDec 12, 2013
     *              This method is to create EXT data source for the configured
     *              JNDI in constants props
     * @Return boolean
     * @return
     **/
    public boolean createEXTWASPool() throws BTSLBaseException {
        _log.debug("DB2PoolManager", " createEXTWASPool Entered");
        final String METHOD_NAME = "createEXTWASPool";
        try {
            _extcontext = new InitialContext();
            _extdataSource = (DataSource) _extcontext.lookup(Constants.getProperty("ExternalDataSourceJNDIName"));
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("getConnection Rollback", "Exception Error Code=" + e.getMessage());
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DB2PoolManager[createEXTWASPool]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,"Database Connection Problem",e);
        }
        return true;
    }

    /**
     * @Date Feb 14, 2012
     * @Return WAS Connection
     * @return
     **/
    public Connection getWASConnection() throws BTSLBaseException {
        _log.debug("DB2PoolManager ", "  getWASConnection ds = " + _dataSource);
        final String METHOD_NAME = "getWASConnection";
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
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DB2PoolManager[getWASConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,"Database Connection Problem",e);
        }
        _log.debug("DB2PoolManager ", "  getWASConnection Exit ", "  connection = " + connection);
        return connection;

    }

    /**
     * @Date Feb 14, 2012
     * @Return RPT Connection
     * @return
     **/
    public Connection getRPTWASConnection() throws BTSLBaseException {
        _log.debug("DB2PoolManager ", "  getRPTWASConnection ds = " + _rptdataSource);
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
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DB2PoolManager[getRPTWASConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,"Database Connection Problem",e);
        }
        _log.debug("Reporting JNDI", " " + Constants.getProperty("ReportDataSourceJNDIName"));
        return rConnection;

    }

    /**
     * @Date Dec 18, 2013
     * @Return EXT Connection
     * @return
     **/
    public Connection getEXTWASConnection() throws BTSLBaseException {
        _log.debug("DB2PoolManager ", "  getRPTWASConnection ds = " + _extdataSource);
        final String METHOD_NAME = "getEXTWASConnection";
        Connection rConnection = null;
        try {
            if (_extdataSource == null)
                if (!createRPTWASPool())
                    return null;

            rConnection = _extdataSource.getConnection();
            rConnection.setAutoCommit(false);
        } catch (SQLException e) {
            _log.error("getConnection Rollback", "Exception Error Code=" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DB2PoolManager[getEXTWASConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,"Database Connection Problem",e);
        }
        _log.debug("External JNDI", " " + Constants.getProperty("ExternalDataSourceJNDIName"));
        return rConnection;

    }

    /**
     * @Date Feb 14, 2012
     * @Return Connection
     * @return
     **/
    public Connection getConnection() throws BTSLBaseException {
        _log.debug("DB2PoolManager ", "  getConnection Entered");
        final String METHOD_NAME = "getConnection";
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
     * This method is for single connection returns connection
     * 
     * @return Connection, database connection
     * @param none
     * 
     */
    public Connection getSingleConnection() {
        final String METHOD_NAME = "getSingleConnection";
        Connection con = null;
        try {
            Class.forName(Constants.getProperty("datasourcedriver"));
            // establish a connection to DB2
            Properties properties = new Properties(); // Create Properties
            // object
            properties.put("user", BTSLUtil.decrypt3DesAesText(Constants.getProperty("userid"))); // Set
                                                                                                  // user
                                                                                                  // ID
                                                                                                  // for
                                                                                                  // connection
            properties.put("password", BTSLUtil.decrypt3DesAesText(Constants.getProperty("passwd")));
            properties.put("currentSchema", Constants.getProperty("currentschema"));// Set
                                                                                    // password
                                                                                    // for
            con = DriverManager.getConnection(Constants.getProperty("datasourceurl"), properties);
        } catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            _log.error("getConnection Rollback", "Exception Error Code=" + ex.getMessage());
        }
        return con;
    }

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
            _log.debug("DB2PoolManager ", "  getReportDBConnection ds " + _rptdataSource, "datasoruse" + _dataSource);
            if (_rptdataSource == null)
                if (!createRPTWASPool())
                    return null;

            dbConnection = _rptdataSource.getConnection();
            dbConnection.setAutoCommit(false);
            _log.debug("DB2PoolManager ", "  getReportDBConnection ds " + _rptdataSource);
        } catch (SQLException e) {
            _log.error("getConnection Rollback", "Exception Error Code=" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DB2PoolManager[getReportDBConnection]", "", "", "", "Database Connection Problem");
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
        _log.debug("getCurrentReportDBConnection ", "  getCurrentReportDBConnection Entereed");
        final String METHOD_NAME = "getCurrentReportDBConnection";
        Connection dbConnection = null;
        try {
            _log.debug("DB2PoolManager ", "  getCurrentReportDBConnection ds = " + _rptdataSource);
            if (_rptdataSource == null)
                if (!createRPTWASPool())
                    return null;

            dbConnection = _rptdataSource.getConnection();
            dbConnection.setAutoCommit(false);
            _log.debug("DB2PoolManager ", "  getCurrentReportDBConnection ds " + _rptdataSource);
        } catch (SQLException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("getConnection Rollback", "Exception Error Code=" + e.getMessage());
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DB2PoolManager[getCurrentReportDBConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,"Database Connection Problem",e);
        }
        _log.debug("Reporting JNDI", " " + Constants.getProperty("ReportDataSourceJNDIName"));
        return dbConnection;
    } // getConnection

    /**
     * This method is for External returns connection from the connection pool,
     * if there is no connection
     * in the pool then adds new connection in the connection pool.
     * 
     * @return Connection, database connection
     * @param none
     * @Modified 18/12/13
     */
    public Connection getExternalDBConnection() throws BTSLBaseException {
        _log.debug("getExternalDBConnection  ", "  Entered ");
        final String METHOD_NAME = "getExternalDBConnection";
        Connection dbConnection = null;
        try {
            _log.debug("DB2PoolManager ", "  getExternalDBConnection ds " + _extdataSource, "datasoruse" + _dataSource);
            if (_extdataSource == null)
                if (!createEXTWASPool())
                    return null;

            dbConnection = _extdataSource.getConnection();
            dbConnection.setAutoCommit(false);
            _log.debug("DB2PoolManager ", "  getExternalDBConnection ds " + _extdataSource);
        } catch (SQLException e) {
            _log.error("getConnection Rollback", "Exception Error Code=" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DB2PoolManager[getExternalDBConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,"Database Connection Problem",e);
        }
        _log.debug("External JNDI", " " + Constants.getProperty("ExternalDataSourceJNDIName"));
        return dbConnection;
    } // getConnection

}
