package com.btsl.db.pool;

import java.sql.Connection;

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

/*
 * BTSLDataSourceManager.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 14/11/2006 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 */
public class BTSLDataSourceManager extends BTSLDBManager {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private InitialContext _context = null;
    private DataSource _dataSource = null;

    public int getActiveConnection() {
    	return 0;
    }
    public int getAvailableConnection() {
    	return 100;
    }
    public BTSLDataSourceManager(String dataSource) {
        super();
        final String METHOD_NAME = "BTSLDataSourceManager";
        try {
            _context = new InitialContext();
            _dataSource = (DataSource) _context.lookup(dataSource);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLDataSourceManager[constructor]", "", "", "", "Database Connection Problem");
        }
    }

    /**
	 * 
	 */
    public Connection getConnection() throws BTSLBaseException {
        Connection dbConnection = null;
        final String METHOD_NAME = "getConnection";
        try {
            dbConnection =  _dataSource.getConnection();
            if (dbConnection == null)
                throw new BTSLBaseException("btsl.project.databseconnectionfailed");
            dbConnection.setAutoCommit(false);
            dbConnection.rollback();
            return dbConnection;
        }// end of try
        catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLDataSourceManager[getConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,DB_CONN_FAILED,ex);
            // Trap errors
        }// end of catch
    }

    public Connection getReportDBConnection() throws BTSLBaseException {
        Connection dbConnection = null;
        final String METHOD_NAME = "getReportDBConnection";
        try {
            dbConnection = _dataSource.getConnection();
            if (dbConnection == null)
                throw new BTSLBaseException("btsl.project.databseconnectionfailed");
            dbConnection.setAutoCommit(false);
            dbConnection.rollback();
            return dbConnection;
        }// end of try
        catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLDataSourceManager[getReportDBConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,DB_CONN_FAILED,ex);
            // Trap errors
        }// end of catch
    }

    public Connection getCurrentReportDBConnection() throws BTSLBaseException {
        Connection dbConnection = null;
        final String METHOD_NAME = "getCurrentReportDBConnection";
        try {
            dbConnection =  _dataSource.getConnection();
            if (dbConnection == null)
                throw new BTSLBaseException("btsl.project.databseconnectionfailed");
            dbConnection.setAutoCommit(false);
            dbConnection.rollback();
            return dbConnection;
        }// end of try
        catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLDataSourceManager[getCurrentReportDBConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,DB_CONN_FAILED,ex);
            // Trap errors
        }// end of catch
    }

    public Connection getExternalDBConnection() throws BTSLBaseException {
        Connection dbConnection = null;
        final String METHOD_NAME = "getExternalDBConnection";
        try {
            dbConnection =  _dataSource.getConnection();
            if (dbConnection == null)
                throw new BTSLBaseException("btsl.project.databseconnectionfailed");
            dbConnection.setAutoCommit(false);
            dbConnection.rollback();
            return dbConnection;
        }// end of try
        catch (Exception ex) {
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLDataSourceManager[getExternalDBConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(this,METHOD_NAME,DB_CONN_FAILED,ex);
            // Trap errors
        }// end of catch
    }

}
