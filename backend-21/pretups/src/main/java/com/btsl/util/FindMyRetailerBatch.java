package com.btsl.util;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.voms.vomslogging.VomsBatchInfoLog;

public class FindMyRetailerBatch {

	private static Log _logger = LogFactory.getLog(FindMyRetailerBatch.class.getName());
	private static long starttime = System.currentTimeMillis();

	
	public static void main(String[] args) {
		final String methodName = "main";
		
		try {
			
			
			// load constants.props
			File constantsFile = new File(args[0]);
			if (!constantsFile.exists()) {
				_logger.debug(methodName , " Constants File Not Found .............");
				_logger.error(methodName+"[main]",
						"Constants file not found on location: " + constantsFile.toString());
				return;
			}
			// load log config file
			File logFile = new File(args[1]);
			if (!logFile.exists()) {
				_logger.debug(methodName , " Logconfig File Not Found .............");
				_logger.error(methodName+"[main]", "Logconfig File not found on location: " + logFile.toString());
				return;
			}
			ConfigServlet.loadProcessCache(constantsFile.toString(), logFile.toString());
		} // end of try block
		catch (Exception e) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("main", " Error in Loading Files ...........................: " + e.getMessage());
			}
			_logger.errorTrace(methodName, e);
			ConfigServlet.destroyProcessCache();
			return;
		   }
		 
		try {			
			_logger.debug(methodName, "Start");
			_logger.debug(methodName, " Starting");
			
			process(args[2], args[3]);//BWDORFWD, noOfDays incase of BWD
			_logger.debug(methodName, "End");
			_logger.debug(methodName , " End");
		} catch (BTSLBaseException be) {
			be.printStackTrace();
			_logger.errorTrace(methodName, be);
			_logger.error(methodName, "BTSLBaseException : " + be.getMessage());
			return;
		} // end of catch block
		catch (Exception e) {
			e.printStackTrace();
			if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, " " + e.getMessage());
			}
			_logger.errorTrace(methodName, e);
			return;
		} // end of catch block
		finally {
			VomsBatchInfoLog.log("Total time taken:" + (System.currentTimeMillis() - starttime));
			if (_logger.isDebugEnabled()) {
				_logger.info(methodName, "Exiting");
			}
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				_logger.errorTrace(methodName, e);
			}
			ConfigServlet.destroyProcessCache();
		} // end of finally
	}
	

	public static void process(String batchFwdBwdMode, String noOfDays) throws BTSLBaseException {

		final String METHOD_NAME = "process";
		Connection con = null;
		MComConnectionI mcomCon = null;
			
		if (_logger.isDebugEnabled()) {
			_logger.info("process ", "Entered ");
		}
		
		_logger.info("process ", "Entered ");
		try {
			System.out.println("Inside "+METHOD_NAME);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			int counter = 1;
			
			if(noOfDays != null) {
				try {
					counter = Integer.parseInt(noOfDays);
				}catch(Exception e) {
					counter  = 1;
				}
			}
			
			for(int count = 1; count <= counter ; count++) {
				executeBatch(con, batchFwdBwdMode);
			}
			
			
		} catch (BTSLBaseException be) {
			 be.printStackTrace();  
			 System.out.println(METHOD_NAME+", Exception "+be.getMessage());
			_logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(METHOD_NAME+", Exception "+e.getMessage());
			_logger.error(METHOD_NAME, "Exception : " + e.getMessage());
			_logger.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
					"FindMyRetailerBatch[process]", "", "", "",
					" FindMyRetailerBatch process could not be executed successfully.");
			throw new BTSLBaseException("FindMyRetailerBatch", METHOD_NAME,
					PretupsErrorCodesI.ERROR_VOMS_GEN, e);
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e1) {
					_logger.errorTrace(METHOD_NAME, e1);
				}
			}
			if (_logger.isDebugEnabled()) {
				_logger.debug("process", "Exiting..... ");
			}
			

			
			
		}

	}

	
	
	public static void executeBatch(Connection con, String batchFwdBwdMode) throws BTSLBaseException, SQLException {

		final String methodName = "executeBatch";
		CallableStatement cstmt = null;
		String plSqlRetuns[] = new String[3];
		try {

			if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
				cstmt = con.prepareCall("{call " + Constants.getProperty("currentschema")
						+ ".FIN_MY_RETAILER_SUMMARY_CREATOR(?)}");
			} else {
				cstmt = con.prepareCall(
						"{call FIN_MY_RETAILER_SUMMARY_CREATOR(?)}"); 
			}

			int i = 1;
			cstmt.setString(i, batchFwdBwdMode);// which is created at add
														// batch function
			long startTime = System.currentTimeMillis();
			cstmt.execute();
			long endTime = System.currentTimeMillis();
			_logger.debug(methodName,
					"Procedure : FIN_MY_RETAILER_SUMMARY_CREATOR  executed in time " + (endTime - startTime) / 1000.0 + " seconds");


			
		} catch(Exception e){
			_logger.error(methodName, "   Exception while executing FIN_MY_RETAILER_SUMMARY_CREATOR Proc ex=" + e);
		}finally {
			try {
				if (cstmt != null) {
					cstmt.close();
				}
				
			} catch (Exception ex) {
				_logger.error(methodName, "   Exception while closing prepared statement ex=" + ex);
			}

		}

	}

	
}
