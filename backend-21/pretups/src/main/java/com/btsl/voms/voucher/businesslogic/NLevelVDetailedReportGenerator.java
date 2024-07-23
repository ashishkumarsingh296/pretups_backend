package com.btsl.voms.voucher.businesslogic;

import java.io.File;
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
import com.btsl.pretups.channel.reports.businesslogic.ChannelTransferReportDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.util.ConfigServlet;
import com.btsl.voms.vomslogging.VomsBatchInfoLog;

public class NLevelVDetailedReportGenerator {

	private static Log _logger = LogFactory.getLog(NLevelVDetailedReportGenerator.class.getName());
	private static long starttime = System.currentTimeMillis();

	
	public static void main(String[] args) {
		final String methodName = "main";
		
		try {
			
			// load constants.props
			File constantsFile = new File(args[0]);
			if (!constantsFile.exists()) {
				System.out.println(methodName + " Constants File Not Found .............");
				_logger.error(methodName+"[main]",
						"Constants file not found on location: " + constantsFile.toString());
				return;
			}
			// load log config file
			File logFile = new File(args[1]);
			if (!logFile.exists()) {
				System.out.println(methodName + " Logconfig File Not Found .............");
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
			System.out.println(methodName+" Starting");
			
			process();
			_logger.debug(methodName, "End");
			System.out.println(methodName+" End");
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
	

	public static void process() throws BTSLBaseException {

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
	
			ChannelTransferReportDAO channelTransferDao =  new ChannelTransferReportDAO();
			System.out.println(METHOD_NAME+" Calling generateNlevelDetailVReport");
			channelTransferDao.generateNlevelDetailVReport(con);
			System.out.println(METHOD_NAME+" generateNlevelDetailVReport invoked");
			
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
					"NLevelVDetailedReportGenerator[process]", "", "", "",
					" NLevelVDetailedReportGenerator process could not be executed successfully.");
			throw new BTSLBaseException("NLevelVDetailedReportGenerator", METHOD_NAME,
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

}
