package com.sshmanager;

import com.commons.MasterI;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.utils._masterVO;

import jline.internal.Log;

public class ConnectionManager {
	
    private static Session session_appserver;

	public static Session getInstance() throws JSchException {
		String HOSTNAME = _masterVO.getMasterValue(MasterI.PUTTY_IP);
		String USERNAME = _masterVO.getMasterValue(MasterI.PUTTY_USERNAME);
		String PASSWORD = _masterVO.getMasterValue(MasterI.PUTTY_PASSWORD);
		    
		try {
			ChannelExec testChannel = (ChannelExec) session_appserver.openChannel("exec");
		    testChannel.setCommand("true");
		    testChannel.connect();
		    Log.info("SSH Connection to Application Server already exists. Returning Same Session.");
		    testChannel.disconnect();
		} catch (Throwable t) {
		   	JSch jsch = new JSch();
		    Log.info("SSH Connection to Application Server not found! Creating New Session.");
				
		    java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
		        
			session_appserver = jsch.getSession(USERNAME, HOSTNAME, 22);
			session_appserver.setPassword(PASSWORD);
			session_appserver.setConfig(config);
			session_appserver.setConfig("PreferredAuthentications",
	                  "publickey,keyboard-interactive,password");
			session_appserver.connect();
		}
			
			return session_appserver;
	}
	
	public static void releaseInstance() {
		session_appserver.disconnect();
	}
}
