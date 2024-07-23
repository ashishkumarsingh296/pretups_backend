package com.btsl.pretups.processes;

/*
 * NetworkDailyClosingStockNew.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Anu Garg 09/12/2011 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Main class for entering closing stocks for all the networks
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
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class NetworkDailyClosingStockNew {
    private static final Log LOGGER = LogFactory.getLog(NetworkDailyClosingStockNew.class.getName());

    /**
     * to ensure no class instantiation 
     */
    private NetworkDailyClosingStockNew(){
    	
    }
    public static void main(String args[]) {
        final String METHOD_NAME = "main";
        Connection con = null;
        final CallableStatement cstmt = null;
        try {
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                LOGGER.debug(METHOD_NAME, " Constants file not found on provided location.");
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                LOGGER.debug(METHOD_NAME, " Logconfig file not found on provided location.");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        }// end try
        catch (Exception ex) {
            LOGGER.error(METHOD_NAME, "Error in Loading Configuration files ...........................: " + ex);
            LOGGER.errorTrace(METHOD_NAME, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }

        try {
            // Get the connection
            con = OracleUtil.getSingleConnection();
            // Call to update all the records
            updateDailyClosingStock(con, cstmt);
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("main", " " + e.getMessage());
            }
            LOGGER.errorTrace(METHOD_NAME, e);
        } finally {
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
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("main", " Exiting");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * This method inserts closing stock data.
     * Method updateDailyClosingStock
     * 
     * @param p_con
     *            Connection
     * @return int
     */
    private static void updateDailyClosingStock(Connection p_con, CallableStatement p_cstmt) {
        final String METHOD_NAME = "updateDailyClosingStock";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("updateDailyClosingStock", " Entered");
        }
        
        String dbConnected=Constants.getProperty(QueryConstants.PRETUPS_DB);
        String message=null;
        String messageforlog=null;
        String sqlerrmsgforlog=null;
        try {
            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                p_cstmt = p_con.prepareCall("{call" + Constants.getProperty("currentschema") + ".network_daily_closing_stock(?,?,?)}");
            } else if(QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
            	p_cstmt=p_con.prepareCall("{call network_daily_closing_stock()}");
            }
            else {
                p_cstmt = p_con.prepareCall("{call network_daily_closing_stock(?,?,?)}");
            }
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("NetworkDailyClosingStockNew[main]", "Before Exceuting Procedure");
            }
            
            if(!QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
            p_cstmt.registerOutParameter(1, Types.VARCHAR); // Message
            p_cstmt.registerOutParameter(2, Types.VARCHAR); // Message for log
            p_cstmt.registerOutParameter(3, Types.VARCHAR); // Sql Exception
            p_cstmt.executeUpdate();
            }
            else
            {
            	 ResultSet rs = p_cstmt.executeQuery();
                 if(rs.next()){
                	 message =rs.getString("rtn_message");
                     messageforlog = rs.getString("rtn_messageforlog");
                   	sqlerrmsgforlog=rs.getString("rtn_sqlerrmsgforlog");
                 }
            }
            
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("NetworkDailyClosingStockNew[main]", "After Exceuting Procedure");
            }
            
            if(!QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("NetworkDailyClosingStockNew[main]",
                    "Parameters Returned : Status=" + p_cstmt.getString(1) + " , Message=" + p_cstmt.getString(2) + " ,Exception if any=" + p_cstmt.getString(3));
            }
            if (p_cstmt.getString(1) == null || !p_cstmt.getString(1).equalsIgnoreCase("SUCCESS")) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDailyClosingStockNew[main]", "", "", "",
                    p_cstmt.getString(2) + " Exception if any:" + p_cstmt.getString(3));
            } else {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "NetworkDailyClosingStockNew[main]", "", "", "",
                    p_cstmt.getString(2) + " Exception if any:" + p_cstmt.getString(3));
            }
            }
            else{
              if (LOGGER.isDebugEnabled()) 
                   LOGGER.debug("NetworkDailyClosingStockNew[main]", "Parameters Returned : Status=" + message + " , Message=" + messageforlog + " ,Exception if any=" +sqlerrmsgforlog);
              if (message == null || !PretupsI.SUCCESS.equalsIgnoreCase(message)) {
            	  if (p_con != null) 
                      p_con.rollback();
                  EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkDailyClosingStockNew[main]", "", "", "",
                		  messageforlog + " Exception if any:" + sqlerrmsgforlog);
              } else {
            	  if (p_con != null) 
            		  p_con.commit();
                  EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "NetworkDailyClosingStockNew[main]", "", "", "",
                		  messageforlog + " Exception if any:" + sqlerrmsgforlog);
              }   
            }

        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("updateDailyClosingStock", " Exiting" + e.getMessage());
            }
            LOGGER.errorTrace(METHOD_NAME, e);
        }
    }
}
