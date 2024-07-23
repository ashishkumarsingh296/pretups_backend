package com.sshmanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.CONSTANT;
import com.commons.MasterI;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.pretupsControllers.BTSLUtil;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;

public class SSHService {

	/**
	 * @see executeCommand method executes a command on Application Server and
	 *      returns the console output as String.
	 * @param SessionName
	 * @param Command
	 * @return
	 * @throws IOException
	 * @throws JSchException
	 */
	private static String executeCommand(Session SessionName, String Command) {

		StringBuilder outputBuffer = new StringBuilder();
		InputStream in = null;
		try {
			Channel channel = SessionName.openChannel("exec");
			((ChannelExec) channel).setCommand(Command);
			Log.debug("Executing: " + Command);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);

			in = channel.getInputStream();
			channel.connect();
			byte[] tmp = new byte[1024];

			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					String content;
					content = (new String(tmp, 0, i));
					outputBuffer.append(content);
				}
				if (channel.isClosed()) {
					Log.debug("Channel Exit Status: " + channel.getExitStatus());
					break;
				}
			}
			channel.disconnect();
		} catch (JSchException JSchEx) {
			Log.error("Error while executing SSH Command : " + JSchEx);
		} catch (IOException IOEx) {
			Log.error("Input / Output Exception during executeCommand on SSH Server : " + IOEx);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Log.error("Input / Output Exception during executeCommand on SSH Server : " + e);
				}
			}
		}
		
		return outputBuffer.toString();
	}

	/**
	 * @see getFileFromSFTP method gets a Simple File Transfer Protocol Connection
	 *      used in order to download Application specific files from Application
	 *      Server
	 * @throws SftpException
	 * @throws JSchException
	 */
	private static void getFileFromSFTP(Session session, String sourcepath, String destinationpath) {
		try {
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.get(sourcepath, destinationpath);
			sftpChannel.exit();
			channel.disconnect();
		} catch (JSchException JSchEx) {
			Log.error("Error while opening SFTP Channel : " + JSchEx);
		} catch (SftpException SftpEx) {
			Log.error("Error while fetching " + sourcepath + " file from SSH Server : " + SftpEx);
		}
	}

	public static void loadApplicationLogsPath() {

		String oneLineTransactionLogCommand = "grep '^[^#]' " + _masterVO.getMasterValue(MasterI.LOG_CONFIG_PROPS)
				+ "|grep '^log4j.appender.OneLineTXNLogC2S.File[ =]'|cut -d'=' -f2|sed 's/^[ \t]*//;s/[ \t]*$//'";
		String channelRequestDailyLogCommand = "grep '^[^#]' " + _masterVO.getMasterValue(MasterI.LOG_CONFIG_PROPS)
				+ "|grep '^log4j.appender.ChannelRequestDailyLog.File[ =]'|cut -d'=' -f2|sed 's/^[ \t]*//;s/[ \t]*$//'";

		Session session;

		try {
			session = ConnectionManager.getInstance();
		} catch (JSchException ex) {
			Log.error("Error while getting SSH Server Instance : " + ex);
			return;
		}

		CONSTANT.ONELINEC2STRANSACTIONLOGS_PATH = executeCommand(session, oneLineTransactionLogCommand);
		CONSTANT.CHANNELREQUESTDAILYLOG_PATH = executeCommand(session, channelRequestDailyLogCommand);
	}

	public static String getCatalina() {
		final String CatalinaGrepCommandString = "tail -n " + _masterVO.getProperty("CatalinaLogLimit") + " "
				+ _masterVO.getMasterValue(MasterI.CATALINA_LOG_PATH);

		try {
			Session session = ConnectionManager.getInstance();
			String CatalinaFileName = "CatalinaLog_" + System.currentTimeMillis() + ".log";

			try (PrintWriter out = new PrintWriter(_masterVO.getProperty("CatalinaLogsOUTPath") + CatalinaFileName)) {
				out.println(executeCommand(session, CatalinaGrepCommandString));
			}

			return _masterVO.getProperty("CatalinaLogsPath")+CatalinaFileName;
		} catch (JSchException JSchEx) {
			Log.error("Error while getting SSH Server Instance : " + JSchEx);
		} catch (IOException IOEx) {
			Log.error("Error while writing catalina file : " + IOEx);
		}

		return null;
	}

	/*public static String executeScript(String scriptname) {
		String pretupsScriptsPath = _masterVO.getMasterValue(MasterI.SCRIPTPATH);
		final String ScriptExecutionCommand = "sh " + pretupsScriptsPath + scriptname;

		try {
			Session session = ConnectionManager.getInstance();
			String scriptOutputFileName = _masterVO.getProperty("CatalinaLogsOUTPath") + "ScriptLog_"
					+ System.currentTimeMillis() + ".log";

			try (PrintWriter out = new PrintWriter(scriptOutputFileName)) {
				out.println(executeCommand(session, ScriptExecutionCommand));
			}

			return scriptOutputFileName;
		} catch (JSchException JSchEx) {
			Log.error("Error while getting SSH Server Instance : " + JSchEx);
		} catch (IOException IOEx) {
			Log.error("Error while writing catalina file : " + IOEx);
		}

		return null;
	}*/

	public static String executeScript(String scriptname) {

		String changeTomcatPathStr = _masterVO.getMasterValue(MasterI.TOMCAT_PATH);

		String pretupsScriptsPath = _masterVO.getMasterValue(MasterI.SCRIPTPATH);

		String commandToBeExecuted = "sed -i -- 's/<Tomcat-Path>/" + changeTomcatPathStr + "/g' " + pretupsScriptsPath
				+ scriptname;

		String commandToBeExecuted2 = "sed -i '1s/^.*#//;s/\\r$//'  "  + pretupsScriptsPath
				+ scriptname;
		
		final String ScriptExecutionCommand = "sh " + pretupsScriptsPath + scriptname;

		try {
			Session session = ConnectionManager.getInstance();

			executeCommand(session, commandToBeExecuted);
			
			executeCommand(session, commandToBeExecuted2);
			

			String scriptOutputFileName = _masterVO.getProperty("CatalinaLogsOUTPath") + "ScriptLog_"
					+ System.currentTimeMillis() + ".log";

			try (PrintWriter out = new PrintWriter(scriptOutputFileName)) {
				out.println(executeCommand(session, ScriptExecutionCommand));
			}

			return scriptOutputFileName;
		} catch (JSchException JSchEx) {
			Log.error("Error while getting SSH Server Instance : " + JSchEx);
		} catch (IOException IOEx) {
			Log.error("Error while writing catalina file : " + IOEx);
		}

		return null;
	}
	
	public static void getApplicationFiles() {
		
		String messageResourceSource = _masterVO.getMasterValue(MasterI.MESSAGE_PROPERTIES_FILE);
		String messageResourceDestination = _masterVO.getProperty("MessagePropertiesFilePath")
				+ _masterVO.getMasterValue(MasterI.LANGUAGE) + ".properties";

		Session session;

		try {
			session = ConnectionManager.getInstance();
		} catch (JSchException ex) {
			Log.error("Error while getting SSH Server Instance : " + ex);
			return;
		}

		getFileFromSFTP(session, messageResourceSource, messageResourceDestination);
		Log.info("Exiting getFileFromSFTP()");
		String messagePropertiesSource = _masterVO.getMasterValue(MasterI.C2S_MESSAGE_PROPERTIES_FILE);
		String messagePropertiesDestination = _masterVO.getProperty("MessagePropertiesFilePath") + "C2S_"
				+ _masterVO.getMasterValue(MasterI.LANGUAGE) + ".properties";
	
		getFileFromSFTP(session, messagePropertiesSource, messagePropertiesDestination);
		Log.info("Exiting getFileFromSFTP()");
	}

	public static String getOneLineC2STransactionLogs(String transactionid) {

		Session session;
		if(BTSLUtil.isNullString(CONSTANT.ONELINEC2STRANSACTIONLOGS_PATH))
		{
			Log.info("Trying to get logs path.");
			loadApplicationLogsPath(); 
			Log.info("Path for ChannelRequestDailyLog: "+CONSTANT.CHANNELREQUESTDAILYLOG_PATH);
			Log.info("Path for OneLineTXNLogC2S: "+CONSTANT.ONELINEC2STRANSACTIONLOGS_PATH);
		}
		
		try {
			session = ConnectionManager.getInstance();
		} catch (JSchException ex) {
			Log.error("Error while getting SSH Server Instance : " + ex);
			return null;
		}
		
		Log.info("OneLineTXNLogsC2S: "+CONSTANT.ONELINEC2STRANSACTIONLOGS_PATH);
		String grepCommand = "grep '" + transactionid + "' " + CONSTANT.ONELINEC2STRANSACTIONLOGS_PATH;
		return executeCommand(session, grepCommand);
	}

	public static String getChannelRequestDailyLog(String transactionid) {

		Session session;

		if(BTSLUtil.isNullString(CONSTANT.CHANNELREQUESTDAILYLOG_PATH))
		{
			Log.info("Trying to get logs path.");
			loadApplicationLogsPath(); 
			Log.info("Path for ChannelRequestDailyLog: "+CONSTANT.CHANNELREQUESTDAILYLOG_PATH);
			Log.info("Path for OneLineTXNLogC2S: "+CONSTANT.ONELINEC2STRANSACTIONLOGS_PATH);
		}
		
		try {
			session = ConnectionManager.getInstance();
		} catch (JSchException ex) {
			Log.error("Error while getting SSH Server Instance : " + ex);
			return null;
		}
		
		Log.info("ChannelRequestDailyLog : "+CONSTANT.CHANNELREQUESTDAILYLOG_PATH);
		String grepCommand = "grep '" + transactionid + "' " + CONSTANT.CHANNELREQUESTDAILYLOG_PATH;
		return executeCommand(session, grepCommand);
	}

	public static boolean startMessageSentLogMonitor() {

		Session session;

		try {
			session = ConnectionManager.getInstance();
		} catch (JSchException ex) {
			Log.error("Error while getting SSH Server Instance: " + ex);
			return false;
		}

		String Command = "cp " + _masterVO.getMasterValue(MasterI.MESSAGESENT_LOG_PATH) + " /tmp/AUTMSGLOG.log";
		executeCommand(session, Command);
		ExtentI.Markup(ExtentColor.GREY, "MessageSentLog Monitor Started.");
		return true;
	}

	public static String stopMessageSentLogMonitor() {

		Session session;

		try {
			session = ConnectionManager.getInstance();
		} catch (JSchException ex) {
			Log.error("Error while getting SSH Server Instance: " + ex);
			return null;
		}

		String Command = "diff --changed-group-format='%<' --unchanged-group-format='' "
				+ _masterVO.getMasterValue(MasterI.MESSAGESENT_LOG_PATH)
				+ " /tmp/AUTMSGLOG.log; rm /tmp/AUTMSGLOG.log;";
		ExtentI.Markup(ExtentColor.GREY, "Fetching Messages from MessageSentLog.");
		String message = executeCommand(session, Command);
		ExtentI.Markup(ExtentColor.GREY, message);
		return message;
	}

}
