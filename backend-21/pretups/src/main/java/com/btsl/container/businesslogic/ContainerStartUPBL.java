package com.btsl.container.businesslogic;

import java.net.InetAddress;
import java.sql.Connection;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

/**
 * 
 * @author akhilesh.mittal1
 *
 */
public class ContainerStartUPBL {

	private static Log _logger = LogFactory.getLog(ContainerStartUPBL.class.getName());

	/**
	 * Method to initialize container
	 * 
	 * @param environment
	 */
	public static String initializeContainer(Connection conStartUp, String serverPort, String instanceType, String module) {

		String methodName = "initializeContainer";
		if (_logger.isDebugEnabled()) {
			StringBuilder loggerValue = new StringBuilder();
			loggerValue.setLength(0);
			loggerValue.append("Entered: serverPort=");
			loggerValue.append(serverPort);
			_logger.debug(methodName, loggerValue);
		}
		String instanceId = null;
		try {

			ContainerStartUpDao cStartUpDao = new ContainerStartUpDao();
			instanceId = cStartUpDao.updateInstance(conStartUp, InetAddress.getLocalHost().getHostAddress(), serverPort , instanceType, module);

		} catch (Exception e) {
			_logger.errorTrace(methodName, e);
		} finally {
			if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, " Exit " + instanceId);
	        }
		}
		return instanceId;
	}
	
	public static int updateContainer(Connection con, String serverPort, String instanceType, String module, String instanceId) {

		String methodName = "updateContainer";
		if (_logger.isDebugEnabled()) {
			StringBuilder loggerValue = new StringBuilder();
			loggerValue.setLength(0);
			loggerValue.append("Entered: serverPort=");
			_logger.debug(methodName, loggerValue);
		}
		int result = 0;
		try {

			ContainerStartUpDao cStartUpDao = new ContainerStartUpDao();
			String ip = null;
			try{
				ip = InetAddress.getLocalHost().getHostAddress();
			}catch (Exception e) {
				_logger.errorTrace(methodName, e);
				ip = Constants.getProperty("container.host.ip"); //"172.30.24.104"
			}
			result = cStartUpDao.updateInstance(con, ip, serverPort, instanceType, module , instanceId);//

		} catch (Exception e) {
			_logger.errorTrace(methodName, e);
		} finally {
			if (_logger.isDebugEnabled()) {
				_logger.debug(methodName, " Exit " + instanceId);
	        }
		}
		return result;
	}

}
