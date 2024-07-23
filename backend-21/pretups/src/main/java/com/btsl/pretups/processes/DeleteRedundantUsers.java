package com.btsl.pretups.processes;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.ibm.icu.util.Calendar;

public class DeleteRedundantUsers {
    private static final Log _logger = LogFactory.getLog(DeleteRedundantUsers.class.getName());
    // private static final int MIS_MONTH_COUNT=12;
    private static final int COMMIT_SIZE = 10000;
    
    /**
     * ensures no instantiation
     */
    private DeleteRedundantUsers(){
    	
    }

    /*
     * private static String[] PURGE_TABLES_ARRAY={
     * "CHANNEL_USERS"
     * ,"USER_BALANCES"
     * ,"USER_DAILY_BALANCES"
     * ,"USER_DOMAINS"
     * ,"USER_GEOGRAPHIES"
     * ,"USER_OTH_PROFILES"
     * ,"USER_PRODUCT_TYPES"
     * ,"USER_ROLES"
     * ,"USER_SERVICES"
     * ,"USER_THRESHOLD_COUNTER"
     * ,"USER_TRANSFER_COUNTS"
     * ,"USER_TXNS"
     * ,"RESTRICTED_MSISDNS"
     * ,"PIN_PASSWORD_HISTORY"
     * ,"USER_PHONES"
     * ,"USERS"
     * };
     */
    // private static String[] WRITE_TABLES_ARRAY={ "USERS"};

    public static void main(String[] args) {

        final String methodName = "main";
        try {
            final File constantsFile = Constants.validateFilePath(args[0]);
            if (!constantsFile.exists()) {
                _logger.debug(methodName, " Constants file not found on provided location.");
                return;
            }
            final File logconfigFile = Constants.validateFilePath(args[1]);
            if (!logconfigFile.exists()) {
                _logger.debug(methodName, " Logconfig file not found on provided location.");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        }// end try
        catch (Exception ex) {
            System.out.println("Error in Loading Configuration files ...........................: " + ex);
            _logger.errorTrace(methodName, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }

        Date ref_date = null;
        if (args.length == 3) {
            try {
                ref_date = getValidDateFromString(args[2]);
            } catch (Exception e) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, " " + e.getMessage());
                }
                _logger.errorTrace(methodName, e);
                return;
            }
        } else {
            ref_date = new Date();
        }
        try {
            deleteUsers(ref_date);
        }// end try
        catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, " " + e.getMessage());
            }
            _logger.errorTrace(methodName, e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, " Exiting");
            }
            ConfigServlet.destroyProcessCache();
        }

    }

    private static void deleteUsers(Date p_date) throws BTSLBaseException {
        final String methodName = "deleteUsers";
        if (_logger.isDebugEnabled()) {
            _logger.info(methodName, " Entered");
        }

        Connection con = null;
        CallableStatement deleteCstmt = null;
        try {
            con = OracleUtil.getSingleConnection();

            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                deleteCstmt = con.prepareCall("{call " + Constants.getProperty("currentschema") + ".PKG_PURGE_REDUNDANT_USER.DELETE_USERS_FROM_TABLES(?,?,?,?,?)}");
            } else {
                deleteCstmt = con.prepareCall("{call PKG_PURGE_REDUNDANT_USER.DELETE_USERS_FROM_TABLES(?,?,?,?,?)}");
            }
            deleteCstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_date));
            deleteCstmt.setInt(2, COMMIT_SIZE);
            deleteCstmt.registerOutParameter(3, Types.VARCHAR);// Message
            deleteCstmt.registerOutParameter(4, Types.VARCHAR);// Message for
            // log
            deleteCstmt.registerOutParameter(5, Types.VARCHAR);// SQL Exception
            deleteCstmt.execute();

            if (_logger.isDebugEnabled()) {
                _logger.debug("DeleteRedundantUsers[deleteUsers]",
                    "Parameters Returned : Status=" + deleteCstmt.getString(3) + " , Message=" + deleteCstmt.getString(4) + " ,Exception if any=" + deleteCstmt.getString(5));
            }

        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, " Exiting" + e.getMessage());
            }
            _logger.errorTrace(methodName, e);
            throw new BTSLBaseException("DeleteRedundantUsers", methodName, "Exception in deleting Users.");
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, " Exiting");
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e1) {
                    _logger.errorTrace(methodName, e1);
                }
            }
            if (deleteCstmt != null) {
                try {
                    deleteCstmt.close();
                } catch (SQLException e1) {
                    _logger.errorTrace(methodName, e1);
                }
            }

            // pushMessage(success_users,error_cases);
        }

    }

    private static Date getValidDateFromString(String dateString) throws BTSLBaseException {
        Date validDate = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT);
        final Calendar cal = BTSLDateUtil.getInstance();

        try {
            cal.setTime(sdf.parse(dateString));
            if (dateString.equals(sdf.format(cal.getTime()))) {
                validDate = sdf.parse(dateString);
            }
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("main", "Exception while parsing user specified date in format dd/mm/yy : " + e.getMessage());
            }
            throw new BTSLBaseException("DeleteRedundantUsers", "getValidDateFromString", "Exception while parsing user specified date in format dd/mm/yy.");
        }

        return validDate;
    }
}
