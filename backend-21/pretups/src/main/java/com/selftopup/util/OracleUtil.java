package com.selftopup.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.db.pool.BTSLDataSourceManager;
import com.selftopup.db.util.BTSLDBManager;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.pretups.common.PretupsI;

/*
 * OracleUtil.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 14/11/2006 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 */
public class OracleUtil {

    private static BTSLDBManager _btslDBManager = null;
    public static DatabaseHelperInterface _databaseHelper = null;
    public static DatabaseHelperInterface _databaseHelperReportDB = null;
    public static DatabaseHelperInterface _databaseHelperCurrentReportDB = null;
    static {
        try {

            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                String poolmanagerclass = Constants.getProperty("poolmanagerclassDB2");
                _btslDBManager = (BTSLDBManager) Class.forName(poolmanagerclass).newInstance();
            } else {
                String applicationPool = Constants.getProperty("applicationpool");
                String databasehelperclass = Constants.getProperty("databasehelperclass");
                String datasourcename = Constants.getProperty("datasourcename");
                String poolmanagerclass = Constants.getProperty("poolmanagerclass");
                if (applicationPool.equals("N")) {
                    _btslDBManager = new BTSLDataSourceManager(datasourcename);
                } else {
                    _btslDBManager = (BTSLDBManager) Class.forName(poolmanagerclass).newInstance();
                }
                _databaseHelper = (DatabaseHelperInterface) Class.forName(databasehelperclass).newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OracleUtil[static]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
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
    public static Connection getConnection() throws BTSLBaseException {
        return _btslDBManager.getConnection();
    } // getConnection

    public static Connection getSingleConnection() throws BTSLBaseException {
        return _btslDBManager.getSingleConnection();

    }

    public static BTSLDBManager getBtslDBManager() {
        return _btslDBManager;
    }

    public static void setBtslDBManager(BTSLDBManager btslDBManager) {
        _btslDBManager = btslDBManager;
    }

    public static void setFormOfUse(PreparedStatement p_stmt, int p_index, String p_setString) {
        _databaseHelper.setFormOfUse(p_stmt, p_index, p_setString);
    }
    
    public static void closeQuietly(Connection connection){
        try{
          if (connection != null){
            connection.close();
          }
        }
        catch (SQLException e){
        	e.printStackTrace();
        }
      }

      public static void closeQuietly(PreparedStatement statement){
        try{
          if (statement!= null){
            statement.close();
          }
        }
        catch (SQLException e){
        	e.printStackTrace();
        }
      }

      public static void closeQuietly(ResultSet resultSet)
      {
        try{
          if (resultSet!= null){
            resultSet.close();
          }
        }
        catch (SQLException e){
        	e.printStackTrace();
        }
      }

}// end of class oracleUtil
