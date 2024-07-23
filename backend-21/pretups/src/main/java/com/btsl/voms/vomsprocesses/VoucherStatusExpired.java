package com.btsl.voms.vomsprocesses;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.ConfigServlet;
import com.btsl.util.OracleUtil;

/**
 * @(#)VoucherStatusExpired
 *                     Copyright(c) 2018, Mahindra Comviva  Ltd.
 *                     All Rights Reserved
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Mahindra Comviva 05/12/2018 Initial Creation
 *                     
 *                     This class used to make voucher status as EX if expiry date less than define date.
 * */

public class VoucherStatusExpired {
	private static Log _logger = LogFactory.getLog(VoucherStatusExpired.class.getName());

	private final static String  expiredVoucher="EX";
	/**
	 * ensures no instantiation
	 */
	private VoucherStatusExpired() {

	}
	public static void main(String arg[]) {
		final String METHOD_NAME = "main";
		try {
			if (arg.length != 2) {
				System.out.println("Usage : VoucherStatusExpired [Constants file] [LogConfig file]");
				return;
			}
			File constantsFile = new File(arg[0]);
			if (!constantsFile.exists()) {
				System.out.println("VoucherStatusExpired" + " Constants File Not Found .............");
				return;
			}
			File logconfigFile = new File(arg[1]);
			if (!logconfigFile.exists()) {
				System.out.println("VoucherStatusExpired" + " Logconfig File Not Found .............");
				return;
			}
			ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
		}// end of try
		catch (Exception e) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("main", " Error in Loading Files ...........................: " + e.getMessage());
			}
			_logger.errorTrace(METHOD_NAME, e);
			ConfigServlet.destroyProcessCache();
			return;
		}// end of catch
		try {
			process();
		} catch (BTSLBaseException be) {
			_logger.error("main", "BTSLBaseException : " + be.getMessage());
			_logger.errorTrace(METHOD_NAME, be);
		} finally {
			if (_logger.isDebugEnabled()) {
				_logger.debug("main", "Exiting..... ");
			}
			ConfigServlet.destroyProcessCache();
		}
	}

	private static void process() throws BTSLBaseException {
		final String methodName = "process";
		_logger.debug(methodName, " Entered: ");
		Connection con = null;
		
		PreparedStatement psmt = null;
		int updateCount=0;
		try {
			con = OracleUtil.getSingleConnection();
			if (con == null) {
				if (_logger.isDebugEnabled()) {
					_logger.debug(methodName, " DATABASE Connection is NULL ");
				}
				EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherStatusExpired[process]", "", "", "", "DATABASE Connection is NULL");
				return;
			}
			StringBuffer strBuff = new StringBuffer("update voms_vouchers set current_status=? , status =? , PREVIOUS_STATUS=current_status , modified_on = sysdate where expiry_date<= sysdate- ? and current_status <> ? and  rownum < 2");
			
			boolean flag=false;
			
			while (flag==false) {
				updateCount=0;
				psmt= con.prepareStatement(strBuff.toString());
				psmt.clearParameters();
				psmt.setString(1,expiredVoucher);
				psmt.setString(2,expiredVoucher);
				psmt.setInt(3,((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_MIN_EXPIRY_DAYS))).intValue());
				psmt.setString(4,expiredVoucher);
				updateCount= psmt.executeUpdate();
				if (updateCount > 0) {
					_logger.debug(methodName, "Voucher Marked as expired Count :" +updateCount);
					con.commit();
				} else {
					flag=true;
					_logger.debug(methodName, "No Voucher  Marked as expired ");
					con.commit();
					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherStatusExpired[process]", "", "", "", " No Voucher Marked as expired .");
				}
			}
		
		} catch (BTSLBaseException be) {
			_logger.error(methodName, "BTSLBaseException : " + be.getMessage());
			_logger.errorTrace(methodName, be);
			throw be;
		} catch (Exception e) {
			_logger.error(methodName, "Exception : " + e.getMessage());
			_logger.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherStatusExpired[process]", "", "", "", " VoucherStatusExpired process could not be executed successfully.");
			throw new BTSLBaseException("VoucherStatusExpired", methodName, PretupsErrorCodesI.VOMS_CHANGE_STATUS_ERROR,e);
		} finally {
			// if the status was marked as under process by this method call,
			// only then it is marked as complete on termination
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception ex) {
				_logger.errorTrace(methodName, ex);
			}
			try {
				if (psmt != null) {
					psmt.close();
				}
			} catch (Exception e) {
				_logger.errorTrace(methodName, e);
			}
		
			if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, "Exiting..... ");
			}
		}

	}

	
}
