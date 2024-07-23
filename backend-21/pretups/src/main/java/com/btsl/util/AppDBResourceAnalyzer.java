package com.btsl.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.SigarProxyCache;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * 
 * @author akhilesh.mittal1
 *
 */
public class AppDBResourceAnalyzer {

	private static Log _logger = LogFactory.getLog(AppDBResourceAnalyzer.class.getName());
	private static volatile double DB_CPU_UTILIZATION = 0.0;
	private static volatile double APP_SERVER_CPU_UTILIZATION = 0.0;
	private static Sigar sigarImpl = null;
	private static SigarProxy sigar = null;
	private static boolean keepContinueAnalyzer = false;
	private static Thread dbUtilizationCheckTh = null;
	private static Thread appServerCpuUtilizationTh = null;
	private static boolean threadsLive = false;

	/**
	 * 
	 * @return Returns DB Server CPU Utilization
	 */
	private static Double getDBSystemCPUUtilization() {
		ChannelShell channel = null;
		PipedInputStream pipeIn = null;
		PipedOutputStream pipeOut = null;
		String response = "";
		final String METHOD_NAME = "getDBSystemCPUUtilization";
		ChannelShell chSell = null;
		Session session = null;
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(Constants.getProperty("DB_USER_MONITORING"),
					Constants.getProperty("DB_HOSTNAME_MONITORING"), 22);
			session.setPassword(Constants.getProperty("DB_PASSWORD_MONITORING"));
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			_logger.debug("startUtilizationThread", "Host connected....");

			String command = Constants.getProperty("DB_COMMAND_MONITORING");
			channel = (ChannelShell) session.openChannel("shell");
			pipeIn = new PipedInputStream();
			pipeOut = new PipedOutputStream(pipeIn);
			channel.setInputStream(pipeIn);
			channel.connect(3 * 1000);
			pipeOut.write(command.getBytes());
			Thread.sleep(1 * 1000);
			Reader in = new InputStreamReader(channel.getInputStream());
			int count = 5;
			StringBuilder sb = new StringBuilder();
			while (count > 0) {
				int code = in.read();
				if (code == -1) {
					count = 0;
				} else {
					count--;
					sb.append(String.valueOf(Character.toChars(code)));
				}
			}
			return Double.parseDouble(sb.toString());

		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				channel.disconnect();
			} catch (Exception ee) {
				_logger.debug(METHOD_NAME, "Exception during closing channel Object..");
			}

			try {
				session.disconnect();
			} catch (Exception ee) {
				_logger.debug(METHOD_NAME, "Exception during closing session Object");
			}
		}

		finally {
			try {
				pipeIn.close();
			} catch (IOException e1) {
				_logger.debug(METHOD_NAME, "Exception during closing pipein Object");
			}
			try {
				pipeOut.close();
			} catch (Exception e) {
				_logger.debug(METHOD_NAME, "Exception during closing PipeOutputStream Object");
			}

			try {
				channel.disconnect();
			} catch (Exception e) {
				_logger.debug(METHOD_NAME, "Exception during closing ChannelShell Object");
			}

			try {
				session.disconnect();
			} catch (Exception ee) {
				_logger.debug(METHOD_NAME, "Exception during closing session Object");
			}
		}
		return 0.0;
	}

	/**
	 * 
	 * @return Returns App Server CPU Utilization
	 */
	private static double getSystemCPUUtilization() {
		try {
			if (sigar == null) {
				sigarImpl = new Sigar();
				sigar = SigarProxyCache.newInstance(sigarImpl, Integer.parseInt(Constants.getProperty("SLEEP_TIME")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		CpuPerc cpuperc = null;
		try {
			cpuperc = sigar.getCpuPerc();
			_logger.debug("getSystemCPUUtilization", "cpuperc:: " + cpuperc);
		} catch (SigarException se) {
			se.printStackTrace();
		}

		return cpuperc.getCombined() * 100;
	}

	public static void stopUtilizationThread() {

		threadsLive = false;
		try {
			sigarImpl.close();
			sigarImpl = null;
			sigar = null;
		} catch (Exception e) {
			_logger.debug("stopUtilizationThread", "Exception during closing sigarImpl Object");
		}

	}

	public static void startUtilizationThread() {

		threadsLive = true;
		Runnable dbUtilizationCheck = () -> {

			try {

				int loopCounter = 0;
				while (threadsLive) {
					if (keepContinueAnalyzer && loopCounter < 120) { // loopCounter < 120 i.e. 1 minutes

						DB_CPU_UTILIZATION = getDBSystemCPUUtilization();
						_logger.debug("run", "DB_CPU_UTILIZATION " + DB_CPU_UTILIZATION);
						try {
							Thread.sleep(500);
						} catch (Exception e) {
							_logger.debug("run", "Exception during sleep operation");
						}
						loopCounter++;
					} else {
						loopCounter = 0;
						keepContinueAnalyzer = false;

						try {
							Thread.sleep(3000);
						} catch (Exception e) {
							_logger.debug("run", "Exception during sleep operation");
						}
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
				_logger.debug("run", "Exception while creating getDBSystemCPUUtilization thread... " + e);
			}

		};
		dbUtilizationCheckTh = new Thread(dbUtilizationCheck);
		dbUtilizationCheckTh.start();
		Runnable appServerUtilizationCheck = () -> {

			try {

				int loopCounter = 0;
				while (threadsLive) {
					if (keepContinueAnalyzer && loopCounter < 120) { // loopCounter < 120 i.e. 1 minutes

						APP_SERVER_CPU_UTILIZATION = getSystemCPUUtilization();
						_logger.debug("run", "APP_SERVER_CPU_UTILIZATION " + APP_SERVER_CPU_UTILIZATION);
						try {
							Thread.sleep(500);
						} catch (Exception e) {
							_logger.debug("run", "Exception during sleep operation");
						}
						loopCounter++;
					} else {
						loopCounter = 0;
						keepContinueAnalyzer = false;
						try {
							Thread.sleep(3000);
						} catch (Exception e) {
							_logger.debug("run", "Exception during sleep operation");
						}
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
				_logger.debug("run", "Exception while creating getDBSystemCPUUtilization thread... " + e);
			}

		};
		appServerCpuUtilizationTh = new Thread(appServerUtilizationCheck);
		appServerCpuUtilizationTh.start();
	}

	public static void monitorResourcesAndWait() {
		keepContinueAnalyzer = true;

		try {
		if (APP_SERVER_CPU_UTILIZATION < Integer
				.parseInt(Constants.getProperty("RESOURCE_UTIL_ONLINE_VOMS_GEN_THRESHOLD_LIMIT"))
				&& DB_CPU_UTILIZATION < Integer
						.parseInt(Constants.getProperty("RESOURCE_UTIL_ONLINE_VOMS_GEN_THRESHOLD_LIMIT"))) {
			// Nothing
		} else {
			try {
				Thread.sleep(Integer.parseInt(Constants.getProperty("PAUSE_APPLICATION_TIME")));
			} catch (Exception e) {
				_logger.debug("run", "Exception during sleep operation");
			}
		}
		} catch (Exception e) {
			_logger.debug("run", "Exception during monitorResourcesAndWait operation "+e);
		}
	}

	public static void monitorResourcesAndWait(int appCpuWaitMillis, int dbCpuWaitMillis) {
		keepContinueAnalyzer = true;

		if (APP_SERVER_CPU_UTILIZATION < appCpuWaitMillis && DB_CPU_UTILIZATION < dbCpuWaitMillis) {
			// Nothing
		} else {
			try {
				Thread.sleep(Integer.parseInt(Constants.getProperty("PAUSE_APPLICATION_TIME")));
			} catch (Exception e) {
				_logger.debug("run", "Exception during sleep operation");
			}
		}
	}

	public static void monitorAppResourcesAndWait(int appCpuWaitMillis) {
		keepContinueAnalyzer = true;

		if (APP_SERVER_CPU_UTILIZATION < appCpuWaitMillis) {
			// Nothing
		} else {
			try {
				Thread.sleep(Integer.parseInt(Constants.getProperty("PAUSE_APPLICATION_TIME")));
			} catch (Exception e) {
				_logger.debug("run", "Exception during sleep operation");
			}
		}
	}

	public static void monitorDBResourcesAndWait(int dbCpuWaitMillis) {
		keepContinueAnalyzer = true;

		if (DB_CPU_UTILIZATION < dbCpuWaitMillis) {
			// Nothing
		} else {
			try {
				Thread.sleep(Integer.parseInt(Constants.getProperty("PAUSE_APPLICATION_TIME")));
			} catch (Exception e) {
				_logger.debug("run", "Exception during sleep operation");
			}
		}
	}

	public static double getDBCPUUtilization() {

		keepContinueAnalyzer = true;
		return DB_CPU_UTILIZATION;
	}

	public static double getAppCPUUtilization() {

		keepContinueAnalyzer = true;
		return APP_SERVER_CPU_UTILIZATION;
	}

}
