package com.btsl.pretups.processes;

/*
 * UserDailyClosingBalanceNew.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Chhaya Sikheria 15/08/2011 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Main class for entering closing balances for all the users
 */

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class UserDailyClosingBalanceNew {

    private static final Log LOGGER = LogFactory.getLog(UserDailyClosingBalanceNew.class.getName());

    /**
     * to ensure no class instantiation 
     */
    private UserDailyClosingBalanceNew(){
    	
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {

        final String METHOD_NAME = "main";
        Connection con = null;
        final CallableStatement cstmt = null;

        try {
            final File constantsFile = Constants.validateFilePath(args[0]);

            final File logconfigFile = Constants.validateFilePath(args[1]);

            Constants.load(constantsFile.toString());

         //   org.apache.log4j.PropertyConfigurator.configure(logconfigFile.toString());

        } catch (Exception exception) {
        	LOGGER.error(METHOD_NAME, "Error in Loading Configuration files ...........................: " + exception);
            LOGGER.errorTrace(METHOD_NAME, exception);
        }// end of catch
        try {
            // Get the connection
            con = OracleUtil.getSingleConnection();
            // call to user balances
            updateDailyClosingBalance(con, cstmt);
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("main", " " + e.getMessage());
            }
            LOGGER.errorTrace(METHOD_NAME, e);
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("main", " Exiting");
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e1) {
                    LOGGER.errorTrace(METHOD_NAME, e1);
                }
            }
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException e1) {
                    LOGGER.errorTrace(METHOD_NAME, e1);
                }
            }

        }
    }

    /**
     * This method inserts closing balance for all users.
     * Method updateDailyClosingBalance
     * 
     * @param p_con
     *            Connection
     * @param p_rst
     *            ResultSet
     * @return void
     */
    private static void updateDailyClosingBalance(Connection con, CallableStatement cstmt) {
        final String METHOD_NAME = "updateDailyClosingBalance";

        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("UserDailyClosingBalanceNew", " Entered");
        }
        String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
        String message=null;
        String messageforlog=null;
        String sqlerrmsgforlog=null;

        try {
        	 if (LOGGER.isDebugEnabled()) {
                 LOGGER.debug("UserDailyClosingBalanceNew[main]", "Before Exceuting Procedure");
             }
            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                cstmt = con.prepareCall("{call " + Constants.getProperty("currentschema") + ".user_daily_closing_balance(?,?,?)}");
            }else if(QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
            	cstmt =con.prepareCall("{call user_daily_closing_balance()}");
            	  ResultSet rs = cstmt.executeQuery();
                  if(rs.next()){
                	message =rs.getString("rtn_message");
                    messageforlog = rs.getString("rtn_messageforlog");
                  	sqlerrmsgforlog=rs.getString("rtn_sqlerrmsgforlog");
                   }
            }  
            else {
                cstmt = con.prepareCall("{call user_daily_closing_balance(?,?,?)}");
            }
           
            if(!QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
            cstmt.registerOutParameter(1, Types.VARCHAR); // Message
            cstmt.registerOutParameter(2, Types.VARCHAR); // Message for log
            cstmt.registerOutParameter(3, Types.VARCHAR); // Sql Exception
            cstmt.executeUpdate();
            }
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("UserDailyClosingBalanceNew[main]", "After Exceuting Procedure");
            }
            
            if(!QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("UserDailyClosingBalanceNew[main]",
                    "Parameters Returned : Status=" + cstmt.getString(1) + " , Message=" + cstmt.getString(2) + " ,Exception if any=" + cstmt.getString(3));
            }

            if (cstmt.getString(1) == null || !PretupsI.SUCCESS.equalsIgnoreCase(message)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDailyClosingBalance[main]", "", "", "", cstmt
                    .getString(2) + " Exception if any: " + cstmt.getString(3));
            } else {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "UserDailyClosingBalance[main]", "", "", "", cstmt
                    .getString(2) + " Exception if any: " + cstmt.getString(3));
            }
            }
            else{
            	 if (LOGGER.isDebugEnabled()) 
                     LOGGER.debug("UserDailyClosingBalanceNew[main]", "Parameters Returned : Status=" + message + " , Message=" + messageforlog + " ,Exception if any=" + sqlerrmsgforlog);
            	 if (message == null || !PretupsI.SUCCESS.equalsIgnoreCase(message)) {
                	 if (con != null) 
                             con.rollback();
                     EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDailyClosingBalance[main]", "", "", "", messageforlog + " Exception if any:" +sqlerrmsgforlog);
                 } else {
                	  if (con != null)
                          con.commit();
                     EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "UserDailyClosingBalance[main]", "", "", "", messageforlog + " Exception if any:" + sqlerrmsgforlog);
                 }
            	
            }

        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("UserDailyClosingBalanceNew", " Exiting" + e.getMessage());
            }
            LOGGER.errorTrace(METHOD_NAME, e);
        }
    }
}
