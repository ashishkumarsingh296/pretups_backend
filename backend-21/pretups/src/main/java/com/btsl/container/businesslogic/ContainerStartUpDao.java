package com.btsl.container.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

public class ContainerStartUpDao {

	private final Log _log = LogFactory.getLog(this.getClass().getName());
	private static final String SQL_EXCEPTION = "SQL EXCEPTION: ";
	private static final String EXCEPTION = "EXCEPTION: ";
	
	
	public String updateInstance(Connection p_con, String ip, String port, String instanceType, String module) throws BTSLBaseException {

		final String methodName = "updateInstance";
		StringBuilder loggerValue = new StringBuilder();
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: ip=");
			loggerValue.append(ip);
			_log.debug(methodName, loggerValue);
		}
		String instanceId = null;
		try {

			StringBuilder strBuff = new StringBuilder();
			StringBuilder strBuff2 = new StringBuilder();

			strBuff.append("SELECT INSTANCE_ID FROM INSTANCE_LOAD WHERE PORT = ? and CURRENT_STATUS = ? and INSTANCE_TYPE = ? and MODULE = ?");

			String selectQuery = strBuff.toString();
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append(selectQuery);
				_log.debug(methodName, loggerValue);
			}

			try (PreparedStatement psmt = p_con.prepareStatement(selectQuery);) {

				psmt.setString(1, port);
				psmt.setString(2, PretupsI.NO);
				psmt.setString(3, instanceType);
				psmt.setString(4, module);

				try (ResultSet rs = psmt.executeQuery();) {
					if (rs.next()) {

						strBuff2.append(
								"UPDATE INSTANCE_LOAD SET IP = ?, CURRENT_STATUS = ? WHERE INSTANCE_ID = ? ");

						selectQuery = strBuff2.toString();
						try (PreparedStatement psmt2 = p_con.prepareStatement(selectQuery);) {
							instanceId = rs.getString(1);
							psmt2.setString(1, ip);
							psmt2.setString(2, PretupsI.YES);
							psmt2.setString(3, instanceId);
							psmt2.executeUpdate();
						}

					} else {

						throw new BTSLBaseException(this, methodName, "Unable to find available instance_id to assign in the instance_load pool !!!");
					}

				}
			}
		} // end of try
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserDAO[updateInstance]", "", "", "", loggerValue.toString());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqle);
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserDAO[updateInstance]", "", "", "", loggerValue.toString());
			throw new BTSLBaseException(this, methodName, "error.general.processing", e);
		} // end of catch
		finally {
			if (_log.isDebugEnabled()) {
	            _log.debug(methodName, " Exit " + instanceId);
	        }
		} // end of finally

		return instanceId;
	}
	
	
	public int updateInstance(Connection p_con, String ip, String port, String instanceType, String module, String instanceId) throws BTSLBaseException {

		final String methodName = "updateInstance";
		StringBuilder loggerValue = new StringBuilder();
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: updateInstance=");
			_log.debug(methodName, loggerValue);
		}
		int result = 0;
		try {
			StringBuilder strBuff2 = new StringBuilder();
			strBuff2.append("UPDATE INSTANCE_LOAD SET IP = ?, CURRENT_STATUS = ? WHERE INSTANCE_ID = ? and CURRENT_STATUS = ? and PORT = ? and INSTANCE_TYPE = ? and MODULE = ? ");
			String selectQuery = strBuff2.toString();
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append(selectQuery);
				_log.debug(methodName, loggerValue);
			}
			selectQuery = strBuff2.toString();
			try (PreparedStatement psmt2 = p_con.prepareStatement(selectQuery);) {
				psmt2.setString(1, ip);
				psmt2.setString(2, PretupsI.YES);
				psmt2.setString(3, instanceId);
				psmt2.setString(4, PretupsI.NO);
				psmt2.setString(5, port);
				psmt2.setString(6, instanceType);
				psmt2.setString(7, module);
				result = psmt2.executeUpdate();
				if (result <= 0){
					throw new BTSLBaseException(this, methodName, "Unable to find available instance_id to assign in the instance_load pool !!!");
				}
			}

		} // end of try
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserDAO[updateInstance]", "", "", "", loggerValue.toString());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqle);
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserDAO[updateInstance]", "", "", "", loggerValue.toString());
			throw new BTSLBaseException(this, methodName, "error.general.processing", e);
		} // end of catch
		finally {
			if (_log.isDebugEnabled()) {
	            _log.debug(methodName, " Exit " + instanceId);
	        }
		} // end of finally

		return result;
	}

}
