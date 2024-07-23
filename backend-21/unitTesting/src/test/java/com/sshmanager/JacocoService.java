package com.sshmanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.commons.MasterI;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.utils.Log;
import com.utils._masterVO;

public class JacocoService extends BaseTest {

	@Test
    public static void executeJacoco() {

        // PATH ex. /data1/pretupsapp/tomcat_trunk_test_ora18c/webapps
        String path = "/data1/pretupsapp/tomcat_trunk_test_ora18c"+"/webapps";
        
        // TEMP Path /data1/pretupsapp/qa/war_test
        String removeFolders ="rm -rf /data1/pretupsapp/qa/reports_test/*";
        String removeFolders2 ="rm -rf /data1/pretupsapp/qa/war_test/*";
        String tempPath = "/data1/pretupsapp/qa/war_test";
        String commandToBeExecutedCopyTemp = "cp "+path+"/pretups.war "+tempPath+"/pretups.war";
        String commandToBeExecutedUnzipWar = "unzip "+tempPath+"/pretups.war -d "+tempPath+"/warextract";
        String commandToBeExecutedUnzipJar = "unzip "+tempPath+"/warextract//WEB-INF/lib/pretupsCore.jar -d "+tempPath+"/pretupscore";

        // COMMAND ex. java -jar /data1/pretupsapp/qa/jacoco/lib/jacococli.jar report /data1/pretupsapp/qa/coverage/jacoco.exec --classfiles /data1/pretupsapp/qa/war_test/WEB-INF/lib/com  --html /data1/pretupsapp/qa/reports_test/ --name sampleApplication 
        String commandToBeExecutedJacoco = "/data1/pretupsapp/jdk1.8.0_74/bin/java -jar /data1/pretupsapp/qa/jacoco/lib/jacococli.jar report /data1/pretupsapp/qa/coverage/jacoco.exec --classfiles "+tempPath+"/pretupscore/com  --html /data1/pretupsapp/qa/reports_test/ --name PreTUPS";
  
        try {
               Session session = ConnectionManager.getInstance();
               executeCommand(session, removeFolders);
               executeCommand(session, removeFolders2);
               executeCommand(session, commandToBeExecutedCopyTemp);
               executeCommand(session, commandToBeExecutedUnzipWar);
               executeCommand(session, commandToBeExecutedUnzipJar);
               executeCommand(session, commandToBeExecutedJacoco);
        } catch (JSchException JSchEx) {
               Log.error("Error while getting SSH Server Instance : " + JSchEx);
        } catch (Exception ex) {
               Log.error("Error while writing catalina file : " + ex);
               ex.printStackTrace();
        }

        
 }
    
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

	
}
