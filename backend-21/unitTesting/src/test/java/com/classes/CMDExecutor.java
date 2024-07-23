package com.classes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aventstack.extentreports.markuputils.ExtentColor;
import com.commons.MasterI;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.pretupsControllers.BTSLUtil;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;

/**
 * @author krishan.chawla This class is created to make connection with the
 *         Application Server
 */
public class CMDExecutor {

	static String HOSTNAME;
	static String USERNAME;
	static String PASSWORD;
	static String CATALINAPATH;
	static String CATALINALOGLIMIT;
	static String CATALINALOGSOUTPATH;
	static String CatalinaGrepCommandString;
	static String CatalinaFileName;
	static String SCRIPTPATH;
	static String CatalinaScript;
	static String CatalinaScriptCommand;
	static String REMOTEMESSAGESFILEPATH;
	static String MESSAGEPROPERTIES_FILENAME;
	static String C2S_REMOTEMESSAGESFILEPATH;
	static String C2S_MESSAGEPROPERTIES_FILENAME;
	static String ACTION_MESSAGEMONITORSTART;
	static String ACTION_MESSAGEMONITORSTOP;
	//static String ChannelRequestDailyLogPATH;

	public CMDExecutor() {
		HOSTNAME = _masterVO.getMasterValue(MasterI.PUTTY_IP);
		USERNAME = _masterVO.getMasterValue(MasterI.PUTTY_USERNAME);
		PASSWORD = _masterVO.getMasterValue(MasterI.PUTTY_PASSWORD);
		CATALINAPATH = _masterVO.getMasterValue(MasterI.CATALINA_LOG_PATH);
		SCRIPTPATH = _masterVO.getMasterValue(MasterI.SCRIPTPATH);
		//ChannelRequestDailyLogPATH = _masterVO.getMasterValue(MasterI.CHANNEL_REQ_DAILY_LOG_PATH);
		CATALINALOGLIMIT = _masterVO.getProperty("CatalinaLogLimit");
		CATALINALOGSOUTPATH = _masterVO.getProperty("CatalinaLogsOUTPath");
		CatalinaGrepCommandString = "tail -n " + CATALINALOGLIMIT + " " + CATALINAPATH;
		REMOTEMESSAGESFILEPATH = _masterVO.getMasterValue(MasterI.MESSAGE_PROPERTIES_FILE);
		MESSAGEPROPERTIES_FILENAME = _masterVO.getProperty("MessagePropertiesFilePath")
				+ _masterVO.getMasterValue(MasterI.LANGUAGE) + ".properties";
		C2S_REMOTEMESSAGESFILEPATH = _masterVO.getMasterValue(MasterI.C2S_MESSAGE_PROPERTIES_FILE);
		C2S_MESSAGEPROPERTIES_FILENAME = _masterVO.getProperty("MessagePropertiesFilePath") + "C2S_"
				+ _masterVO.getMasterValue(MasterI.LANGUAGE) + ".properties";
		
		ACTION_MESSAGEMONITORSTART = "cp " + _masterVO.getMasterValue(MasterI.MESSAGESENT_LOG_PATH) + " /tmp/AUTMSGLOG.log";
		ACTION_MESSAGEMONITORSTOP = "diff --changed-group-format='%<' --unchanged-group-format='' " + _masterVO.getMasterValue(MasterI.MESSAGESENT_LOG_PATH) + " /tmp/AUTMSGLOG.log; rm /tmp/AUTMSGLOG.log;";
	}

	/*
	 * Function to get Catalina Logs
	 */
	public String getCatalinaLog() {

		try {
			Log.debug(
					"Trying to Connect to Application Server with UserName: " + USERNAME + " & Password: " + PASSWORD);
			CatalinaFileName = CATALINALOGSOUTPATH + "CatalinaLog_" + System.currentTimeMillis() + ".log";
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			Session session = jsch.getSession(USERNAME, HOSTNAME, 22);
			session.setPassword(PASSWORD);
			session.setConfig(config);
			session.connect();
			Log.debug("Connected to server Successfully");

			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(CatalinaGrepCommandString);
			Log.debug("Executing: " + CatalinaGrepCommandString);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);

			InputStream in = channel.getInputStream();
			channel.connect();
			byte[] tmp = new byte[1024];
			BufferedWriter out = new BufferedWriter(new FileWriter(CatalinaFileName));
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					String content;
					try {
						out = new BufferedWriter(new FileWriter(CatalinaFileName, true));
						content = (new String(tmp, 0, i));
						out.write(content);
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (channel.isClosed()) {
					Log.debug("Channel Exit Status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			channel.disconnect();
			session.disconnect();
			Log.debug("Session Disconnected successfully");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "." + CatalinaFileName;
	}

	public String messagesLogMonitor(String action) {
		final String methodname = "execute";
		Log.debug("Entered " + methodname + "(" + action + ")");
		
		if (action.equalsIgnoreCase("start"))
			ExtentI.Markup(ExtentColor.GREY, "MessageSentLog Monitor Enabled.");
		else if (action.equalsIgnoreCase("stop"))
			ExtentI.Markup(ExtentColor.GREY, "Fetching Messages from MessageSentLog.");
		
		StringBuilder messages = new StringBuilder("");
		try {
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			Session session = jsch.getSession(USERNAME, HOSTNAME, 22);
			session.setPassword(PASSWORD);
			session.setConfig(config);
			session.connect();
			Log.debug("Connection to App Server created successfully.");

			Channel channel = session.openChannel("exec");
			
			if (action.equalsIgnoreCase("start"))
				((ChannelExec) channel).setCommand(ACTION_MESSAGEMONITORSTART);
			else if (action.equalsIgnoreCase("stop"))
				((ChannelExec) channel).setCommand(ACTION_MESSAGEMONITORSTOP);
			
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);

			InputStream in = channel.getInputStream();
			channel.connect();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					messages.append(new String(tmp, 0, i));
					Log.info("<pre>" + messages.toString() + "</pre>");

					final String regex = "&text=(.*)&smsc";
					final Pattern pattern = Pattern.compile(regex);
					final Matcher matcher = pattern.matcher(messages.toString());

					while (matcher.find()) {
						for (i = 1; i <= matcher.groupCount(); i++) {
							Log.info("<b>Decoded Message: </b>" + URLDecoder.decode(matcher.group(i), "UTF-8"));
						}
					}

				}
				if (channel.isClosed()) {
					Log.debug("exit-status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			channel.disconnect();
			session.disconnect();
			Log.debug("Disconnected successfully from App Server");

		} catch (Exception e) {
			Log.debug("Error while performing execute ");
			Log.writeStackTrace(e);
		}
		
		if (action.equalsIgnoreCase("stop") && messages.toString().equals(""))
			ExtentI.Markup(ExtentColor.GREY, "No New Messages found in MessageSentLog");
			
		Log.debug("Exiting " + methodname + "(" + messages.toString() + ")");
		return messages.toString();
	}

	public void getMessagePropertiesFile() {
		JSch jsch = new JSch();
		Session session = null;
		try {
			Log.info(
					"Trying to Connect to Application Server with User Name: " + USERNAME + " & Password: " + PASSWORD);
			session = jsch.getSession(USERNAME, HOSTNAME, 22);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(PASSWORD);
			session.connect();
			Log.info("Connected to Application Server Successfully");
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			Log.info("Trying to fetch Message Properties File from: " + REMOTEMESSAGESFILEPATH);
			sftpChannel.get(REMOTEMESSAGESFILEPATH, MESSAGEPROPERTIES_FILENAME);
			Log.info("MessageProperties File Downloaded Successfully to: " + MESSAGEPROPERTIES_FILENAME);
			Log.info("Trying to fetch C2S Messages File from: " + C2S_REMOTEMESSAGESFILEPATH);
			sftpChannel.get(C2S_REMOTEMESSAGESFILEPATH, C2S_MESSAGEPROPERTIES_FILENAME);
			Log.info("C2S Messages File Downloaded Successfully to: " + C2S_MESSAGEPROPERTIES_FILENAME);
			sftpChannel.exit();
			session.disconnect();
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		}
	}

	public String executeCMDScript(String scriptName) {
		String scriptOutputFileName = null;
		try {
			Log.debug(
					"Trying to Connect to Application Server with UserName: " + USERNAME + " & Password: " + PASSWORD);
			scriptOutputFileName = CATALINALOGSOUTPATH + "ScriptLog_" + System.currentTimeMillis() + ".log";
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			Session session = jsch.getSession(USERNAME, HOSTNAME, 22);
			session.setPassword(PASSWORD);
			session.setConfig(config);
			session.connect();
			Log.debug("Connected to server Successfully");

			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand("sh " + SCRIPTPATH + scriptName);
			Log.debug("Executing: sh " + SCRIPTPATH + scriptName);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);

			InputStream in = channel.getInputStream();
			channel.connect();
			byte[] tmp = new byte[1024];
			BufferedWriter out = new BufferedWriter(new FileWriter(scriptOutputFileName));
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					String content;
					try {
						out = new BufferedWriter(new FileWriter(scriptOutputFileName, true));
						content = (new String(tmp, 0, i));
						out.write(content);
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (channel.isClosed()) {
					Log.debug("Channel Exit Status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			channel.disconnect();
			session.disconnect();
			Log.debug("Session Disconnected successfully");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "." + scriptOutputFileName;
	}

	public String getChannelRequestDailyLog(String transferID) {
		String path = getPathofAnyLogfile("log4j.appender.ChannelRequestDailyLog.File");
		if(!BTSLUtil.isNullString(path)){
		String executeCMD="grep '"+transferID+"' "+path;
		StringBuilder messages = new StringBuilder("");
		try {
			Log.debug(
					"Trying to Connect to Application Server with UserName: " + USERNAME + " & Password: " + PASSWORD);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			Session session = jsch.getSession(USERNAME, HOSTNAME, 22);
			session.setPassword(PASSWORD);
			session.setConfig(config);
			session.connect();
			Log.debug("Connected to server Successfully");
			
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(executeCMD);
			Log.debug("Executing: " + executeCMD);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			InputStream in = channel.getInputStream();
			channel.connect();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					messages.append(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					Log.debug("exit-status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			channel.disconnect();
			session.disconnect();
			Log.debug("Session Disconnected successfully");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return messages.toString();}else{
			Log.info("Unable to fetch Logs as Path provided for LogConfig.props in MasterSheet is not valid OR key specified for ChannelRequestDailyLog is not valid");return null;
		}
	}
	
	
	public String getOneLineTXNLogC2S(String transferID) {
		String path = getPathofAnyLogfile("log4j.appender.OneLineTXNLogC2S.File");
		if(!BTSLUtil.isNullString(path)){
		String executeCMD="grep '"+transferID+"' "+path;
		StringBuilder messages = new StringBuilder("");
		try {
			Log.debug(
					"Trying to Connect to Application Server with UserName: " + USERNAME + " & Password: " + PASSWORD);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			Session session = jsch.getSession(USERNAME, HOSTNAME, 22);
			session.setPassword(PASSWORD);
			session.setConfig(config);
			session.connect();
			Log.debug("Connected to server Successfully");
			
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(executeCMD);
			Log.debug("Executing: " + executeCMD);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			InputStream in = channel.getInputStream();
			channel.connect();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					messages.append(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					Log.debug("exit-status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			channel.disconnect();
			session.disconnect();
			Log.debug("Session Disconnected successfully");
		} catch (Exception e) {
			e.printStackTrace();
		}return messages.toString();}
		else
		{Log.info("Unable to fetch Logs as Path provied in MasterSheet for LogConfig.props not valid OR key specified for OneLineTXNLogC2S is not valid.");return null;}
	}
	
	public String getPathofAnyLogfile(String key) {
		String executeCMD="grep '^[^#]' "+_masterVO.getMasterValue(MasterI.LOG_CONFIG_PROPS)+"|grep '^"+key+"[ =]'|cut -d'=' -f2|sed 's/^[ \t]*//;s/[ \t]*$//'";
		StringBuilder messages = new StringBuilder("");
		try {
			Log.debug(
					"Trying to Connect to Application Server with UserName: " + USERNAME + " & Password: " + PASSWORD);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			Session session = jsch.getSession(USERNAME, HOSTNAME, 22);
			session.setPassword(PASSWORD);
			session.setConfig(config);
			session.connect();
			Log.debug("Connected to server Successfully");
			
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(executeCMD);
			Log.debug("Executing: " + executeCMD);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			InputStream in = channel.getInputStream();
			channel.connect();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					messages.append(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					Log.debug("exit-status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			channel.disconnect();
			session.disconnect();
			Log.debug("Session Disconnected successfully");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return messages.toString();
	}
}