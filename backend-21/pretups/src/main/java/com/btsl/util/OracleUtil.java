package com.btsl.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.pool.BTSLDataSourceManager;
import com.btsl.db.util.BTSLDBManager;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

/**
 * OracleUtil.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 14/11/2006 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 */
public class OracleUtil {
    public static final Log log = LogFactory.getLog(OracleUtil.class.getName());
    private static BTSLDBManager btslDBManager = null;
    private static DatabaseHelperInterface databaseHelper = null;
    private static BTSLDBManager btslDBManagerReportDB = null;
    private static BTSLDBManager btslCurrentDBManagerReportDB = null;
    // For Separate External Database- 18/12/13
    private static BTSLDBManager btslDBManagerExternalDB = null;
    private static DatabaseHelperInterface databaseHelperReportDB = null;
    private static DatabaseHelperInterface databaseHelperCurrentReportDB = null;
    private static Class thisClass = null;
    static {
        try {

            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                final String poolmanagerclass = Constants.getProperty("poolmanagerclassDB2");
                btslDBManager = (BTSLDBManager) Class.forName(poolmanagerclass).newInstance();
                thisClass = Class.forName(poolmanagerclass);

            } else {
                final String applicationPool = Constants.getProperty("applicationpool");
                final String databasehelperclass = Constants.getProperty("databasehelperclass");
                final String datasourcename = Constants.getProperty("datasourcename");
                final String poolmanagerclass = Constants.getProperty("poolmanagerclass");
                if ("N".equals(applicationPool)) {
                    btslDBManager = new BTSLDataSourceManager(datasourcename);
                } else {
                    btslDBManager = (BTSLDBManager) Class.forName(poolmanagerclass).newInstance();
                    thisClass = Class.forName(poolmanagerclass);
                }
                databaseHelper = (DatabaseHelperInterface) Class.forName(databasehelperclass).newInstance();

                // for seperate report db
                final String applicationPoolReportDB = Constants.getProperty("reportdbapplicationpool");
                final String databasehelperclassReportDB = Constants.getProperty("reportdbdatabasehelperclass");
                final String datasourcenameReportDB = Constants.getProperty("reportdbdatasourcename");
                final String poolmanagerclassReportDB = Constants.getProperty("reportdbpoolmanagerclass");
                if ("N".equals(applicationPoolReportDB)) {
                    btslDBManagerReportDB = new BTSLDataSourceManager(datasourcenameReportDB);
                } else {
                    btslDBManagerReportDB = (BTSLDBManager) Class.forName(poolmanagerclassReportDB).newInstance();
                }
                databaseHelperReportDB = (DatabaseHelperInterface) Class.forName(databasehelperclassReportDB).newInstance();

                // current date reports, Use this for current date reports
                final String currentapplicationPoolReportDB = Constants.getProperty("currentReportDBApplicationPool");
                final String currentdatabasehelperclassReportDB = Constants.getProperty("currentReportDBDatabaseHelperClass");
                final String currentdatasourcenameReportDB = Constants.getProperty("currentRDBDDataSourceName");
                final String currentpoolmanagerclassReportDB = Constants.getProperty("currentReportDBPoolManagerClass");
                if ("N".equals(currentapplicationPoolReportDB)) {
                    btslCurrentDBManagerReportDB = new BTSLDataSourceManager(currentdatasourcenameReportDB);
                } else {
                    btslCurrentDBManagerReportDB = (BTSLDBManager) Class.forName(currentpoolmanagerclassReportDB).newInstance();
                }
                databaseHelperCurrentReportDB = (DatabaseHelperInterface) Class.forName(currentdatabasehelperclassReportDB).newInstance();

                // For Separate External Database- 18/12/13
                final String applicationPoolExternalDB = Constants.getProperty("externaldbapplicationpool");
                final String databasehelperclassExternalDB = Constants.getProperty("externaldbdatabasehelperclass");
                final String datasourcenameExternalDB = Constants.getProperty("externaldbdatasourcename");
                final String poolmanagerclassExternalDB = Constants.getProperty("externaldbpoolmanagerclass");
                if ("N".equals(applicationPoolExternalDB)) {
                    btslDBManagerExternalDB = new BTSLDataSourceManager(datasourcenameExternalDB);
                } else {
                    btslDBManagerExternalDB = (BTSLDBManager) Class.forName(poolmanagerclassExternalDB).newInstance();
                }
                databaseHelperReportDB = (DatabaseHelperInterface) Class.forName(databasehelperclassExternalDB).newInstance();

            }
        } catch (ClassNotFoundException e) {
            log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OracleUtil[static]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        } catch (InstantiationException e) {
            log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OracleUtil[static]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        } catch (IllegalAccessException e) {
            log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OracleUtil[static]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        } catch (Exception e) {
            log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OracleUtil[static]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }
    
    /**
   	 * to ensure no class instantiation 
   	 */
    private OracleUtil(){
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
    public static Connection getConnection() throws BTSLBaseException {
        return btslDBManager.getConnection();
    } // getConnection

    public static Connection getSingleConnection() throws BTSLBaseException {
        return btslDBManager.getSingleConnection();

    }

    public static BTSLDBManager getBtslDBManager() {
        return btslDBManager;
    }

    public static void setBtslDBManager(BTSLDBManager btslDBManager) {
        btslDBManager = btslDBManager;
    }

    public static void setFormOfUse(PreparedStatement stmt, int index, String setString) {
        databaseHelper.setFormOfUse(stmt, index, setString);
    }

    public static Connection getReportDBConnection() throws BTSLBaseException {

        return btslDBManagerReportDB.getReportDBConnection();
    }

    public static Connection getReportDBSingleConnection() throws BTSLBaseException {
        return btslDBManagerReportDB.getReportDBSingleConnection();
    }

    public static BTSLDBManager getReportDBBtslDBManager() {
        return btslDBManagerReportDB;
    }

    public static void setReportDBBtslDBManager(BTSLDBManager btslDBManagerReportDB) {
        btslDBManagerReportDB = btslDBManagerReportDB;
    }

    public static Connection getCurrentReportDBConnection() throws BTSLBaseException {

        return btslDBManager.getCurrentReportDBConnection();
    }

    public static Connection getCurrentReportDBSingleConnection() throws BTSLBaseException {
        return btslCurrentDBManagerReportDB.getCurrentReportDBSingleConnection();
    }

    public static BTSLDBManager getCurrentReportDBBtslDBManager() {
        return btslCurrentDBManagerReportDB;
    }

    public static void setCurrentReportDBBtslDBManager(BTSLDBManager btslDBManagerReportDB) {
        btslCurrentDBManagerReportDB = btslDBManagerReportDB;
    }

    public static Connection getExternalDBConnection() throws BTSLBaseException {

        return btslDBManagerExternalDB.getExternalDBConnection();
    }

    public static DataSource getDataSource() {
        DataSource ds = null;

        Method datasourceMethod = null;
        try {
            datasourceMethod = thisClass.getMethod("getDatasource", null);
        } catch (NoSuchMethodException e) {
            log.errorTrace("Exception in method getDataSource() ", e);
        } catch (SecurityException e) {
            log.errorTrace("Exception in method getDataSource() ", e);
        }

        try {
            ds = (DataSource) datasourceMethod.invoke(btslDBManager, null);
        } catch (IllegalAccessException e) {
            log.errorTrace("Exception in method getDataSource() ", e);
        } catch (IllegalArgumentException e) {
            log.errorTrace("Exception in method getDataSource() ", e);
        } catch (InvocationTargetException e) {
            log.errorTrace("Exception in method getDataSource() ", e);
        }

        return ds;
    }
    
    /**
     * @param connection
     */
    public static void closeQuietly(Connection connection){
      try{
    	  log.error("closeQuietly", "Before closing connection");
    	  if (connection != null){
    		  connection.close();
    	  }
    	  log.error("closeQuietly", "After closing connection");
      }
      catch (SQLException e){
    	  log.error("An error occurred closing connection.", e);
      }
    }

    /**
     * @param statement
     */
    public static void closeQuietly(PreparedStatement statement){
      try{
        if (statement!= null){
          statement.close();
        }
      }
      catch (SQLException e){
    	  log.error("An error occurred closing statement.", e);
      }
    }

    /**
     * @param resultSet
     */
    public static void closeQuietly(ResultSet resultSet)
    {
      try{
        if (resultSet!= null){
          resultSet.close();
        }
      }
      catch (SQLException e){
    	  log.error("An error occurred closing result set.", e);
      }
    }
    
    
    /**
     * @param connection
     * @param className
     * @param methodName
     */
    public static void rollbackConnection(Connection connection, String className, String methodName)
    {
      try{
    	  log.error("rollbackConnection :"+className+ " "+ methodName, " Before rollback");
        if (connection!= null){
         connection.rollback();
        }
        log.error("rollbackConnection", "After rollback");
      }
      catch (SQLException e){
    	  log.error("An error occurred in rollback.", e);
      }
    }
    
    /**
     * @param connection
     * @throws SQLException 
     */
    public static void commit(Connection connection) throws SQLException{
    	log.error("commit", "Before commit");
    	 try{
    	        if (connection!= null){
    	        	connection.commit();
    	        }
    	      }
    	      catch (SQLException e){
    	    	  log.error("An error occurred closing result set.", e);
    	      }
    	log.error("commit", "After commit");
      }
    
    /**
     * @param connection
     * @throws SQLException 
     */
    public static void close(Connection connection) throws SQLException{
    	log.error("close", "Before closing connection");
    	 try{
 	        if (connection!= null){
 	        	connection.close();
 	        }
 	      }
 	      catch (SQLException e){
 	    	  log.error("An error occurred closing result set.", e);
 	      }
    	log.error("close", "After closing connection");
      }
    
    public static int getActiveConnection() {
    	return btslDBManager.getActiveConnection();
    }
    public static int getAvailableConnection() {
        return btslDBManager.getAvailableConnection();
    }
}// end of class oracleUtil
