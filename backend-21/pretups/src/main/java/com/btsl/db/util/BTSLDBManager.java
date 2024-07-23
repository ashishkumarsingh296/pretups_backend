package com.btsl.db.util;

import java.sql.Connection;
import java.sql.DriverManager;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;


public abstract class BTSLDBManager {
    protected String DB_CONN_FAILED = "00000";
    
    protected static String DRIVER_CLASS_NAME;
    
    static{
    	String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
    	if (QueryConstants.DB_POSTGRESQL.equals(dbConnected))
    		DRIVER_CLASS_NAME = "org.postgresql.Driver";
    	else if(QueryConstants.DB_ORACLE.equals(dbConnected))
    		DRIVER_CLASS_NAME = "oracle.jdbc.driver.OracleDriver";
    }

    abstract public Connection getConnection() throws BTSLBaseException;
    abstract public int getActiveConnection();
    abstract public int getAvailableConnection();
    private Log _log = LogFactory.getFactory().getInstance(BTSLDBManager.class.getName());

    public Connection getSingleConnection() throws BTSLBaseException {
        final String METHOD_NAME = "getSingleConnection";
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLDBManager[getSingleConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,DB_CONN_FAILED,e);
        }
        Connection conn = null;
        try {
            String db_url = Constants.getProperty("datasourceurl");
            String db_user = Constants.getProperty("userid");
            if (db_user != null)
                db_user = BTSLUtil.decrypt3DesAesText(db_user);
            String db_password = Constants.getProperty("passwd");
            if (db_password != null)
                db_password = BTSLUtil.decrypt3DesAesText(db_password);
            conn = DriverManager.getConnection(db_url, db_user, db_password);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLDBManager[getSingleConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,DB_CONN_FAILED,e);
        }
        return conn;
    }

    abstract public Connection getReportDBConnection() throws BTSLBaseException;

    public Connection getReportDBSingleConnection() throws BTSLBaseException {
        final String METHOD_NAME = "getReportDBSingleConnection";
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLDBManager[getReportDBSingleConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,DB_CONN_FAILED,e);
        }
        Connection conn = null;
        try {
            String db_url = Constants.getProperty("reportdbdatasourceurl");
            String db_user = Constants.getProperty("reportdbuserid");
            if (db_user != null)
                db_user = BTSLUtil.decrypt3DesAesText(db_user);
            String db_password = Constants.getProperty("reportdbpasswd");
            if (db_password != null)
                db_password = BTSLUtil.decrypt3DesAesText(db_password);
            conn = DriverManager.getConnection(db_url, db_user, db_password);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLDBManager[getReportDBSingleConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,DB_CONN_FAILED,e);
        }
        return conn;
    }

    abstract public Connection getCurrentReportDBConnection() throws BTSLBaseException;

    public Connection getCurrentReportDBSingleConnection() throws BTSLBaseException {
        final String METHOD_NAME = "getCurrentReportDBSingleConnection";
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLDBManager[getCurrentReportDBSingleConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,DB_CONN_FAILED,e);
        }
        Connection conn = null;
        try {
            String db_url = Constants.getProperty("currentDateRptDBDataSourceURL");
            String db_user = Constants.getProperty("currentReportDBUserId");
            if (db_user != null)
                db_user = BTSLUtil.decrypt3DesAesText(db_user);
            String db_password = Constants.getProperty("currentReportDBPasswd");
            if (db_password != null)
                db_password = BTSLUtil.decrypt3DesAesText(db_password);
            conn = DriverManager.getConnection(db_url, db_user, db_password);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLDBManager[getCurrentReportDBSingleConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,DB_CONN_FAILED,e);
        }
        return conn;
    }

    abstract public Connection getExternalDBConnection() throws BTSLBaseException;
}
