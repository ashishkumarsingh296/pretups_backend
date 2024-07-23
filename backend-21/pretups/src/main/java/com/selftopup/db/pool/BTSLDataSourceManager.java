package com.selftopup.db.pool;

import java.sql.Connection;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.db.util.BTSLDBManager;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.p2p.subscriber.requesthandler.ModifyCardDetailsController;

/*
 * BTSLDataSourceManager.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 14/11/2006 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 */
public class BTSLDataSourceManager extends BTSLDBManager {
    private static Log _log = LogFactory.getLog(BTSLDataSourceManager.class.getName());
    private InitialContext _context = null;
    private DataSource _dataSource = null;

    public BTSLDataSourceManager(String dataSource) {
        super();

        try {
            _context = new InitialContext();
            _dataSource = (DataSource) _context.lookup(dataSource);
        } catch (Exception e) {
            _log.errorTrace("BTSLDataSourceManager: Exception print stack trace:e=", e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLDataSourceManager[constructor]", "", "", "", "Database Connection Problem");
        }
    }

    /**
	 * 
	 */
    public Connection getConnection() throws BTSLBaseException {
        Connection dbConnection = null;
        try {
            dbConnection = (Connection) _dataSource.getConnection();
            dbConnection.setAutoCommit(false);
            dbConnection.rollback();
            if (dbConnection == null)
                throw new BTSLBaseException("selftopup.project.databseconnectionfailed");
            return dbConnection;
        } catch (Exception ex) {
            _log.errorTrace("getConnection: Exception print stack trace:e=", ex);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLDataSourceManager[getConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(DB_CONN_FAILED);
            // Trap errors
        }
    }

    public Connection getReportDBConnection() throws BTSLBaseException {
        Connection dbConnection = null;
        try {
            dbConnection = (Connection) _dataSource.getConnection();
            dbConnection.setAutoCommit(false);
            dbConnection.rollback();
            if (dbConnection == null)
                throw new BTSLBaseException("selftopup.project.databseconnectionfailed");
            return dbConnection;
        } catch (Exception ex) {
            _log.errorTrace("getReportDBConnection: Exception print stack trace:e=", ex);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLDataSourceManager[getReportDBConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(DB_CONN_FAILED);
            // Trap errors
        }
    }

    public Connection getCurrentReportDBConnection() throws BTSLBaseException {
        Connection dbConnection = null;
        try {
            dbConnection = (Connection) _dataSource.getConnection();
            dbConnection.setAutoCommit(false);
            dbConnection.rollback();
            if (dbConnection == null)
                throw new BTSLBaseException("selftopup.project.databseconnectionfailed");
            return dbConnection;
        } catch (Exception ex) {
            _log.errorTrace("getCurrentReportDBConnection: Exception print stack trace:e=", ex);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLDataSourceManager[getCurrentReportDBConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(DB_CONN_FAILED);
            // Trap errors
        }
    }

    public Connection getExternalDBConnection() throws BTSLBaseException {
        Connection dbConnection = null;
        try {
            dbConnection = (Connection) _dataSource.getConnection();
            dbConnection.setAutoCommit(false);
            dbConnection.rollback();
            if (dbConnection == null)
                throw new BTSLBaseException("selftopup.project.databseconnectionfailed");
            return dbConnection;
        } catch (Exception ex) {
            _log.errorTrace("getExternalDBConnection: Exception print stack trace:e=", ex);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLDataSourceManager[getExternalDBConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(DB_CONN_FAILED);
            // Trap errors
        }
    }

}
