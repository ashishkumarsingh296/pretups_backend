/**SFTPDemo.java
 * Name                              Date            History
 *------------------------------------------------------------------------
 * Mahindra Comviva					17/04/2015	Initial Creation
 *------------------------------------------------------------------------
 * Copyright (c) 2015 Mahindra Comviva Technologies Limited.
 * Main class for SFTPDemo
 */

package com.client.pretups.processes;
import java.util.HashMap;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

public class SFTPDemo {
	private static final String className="SFTPDemo";
	private static Log LOG = LogFactory.getLog(SFTPDemo.class.getName());
	
	
	public  void initaliseProcess(HashMap<String,String> p_map,String p_fileDate) {
		final String methodName="initaliseProcess";
		
		if(LOG.isDebugEnabled())LOG.debug(methodName,"Entered :p_map"+p_map +"p_fileDate"+p_fileDate );	
		String userName=null;
		String password=null;
		String destinationDir=null;
		String hostPort=null;
		String hostName=null;
		String sourceFile =null;
		String filename = null;
		String movePath=null;
		String moduleType=null;
		String result=null;

		try {
			hostName=p_map.get("FTP_AMB_SERVER_IP");
			userName=p_map.get("FTP_AMB_USER_NAME");
			password=p_map.get("FTP_AMB_PASSWD");
			moduleType=p_map.get("MODULE_TYPE");
			hostPort=p_map.get("FTP_AMB_PORT");
		
			//added by rajvi to distinguish whether request is for c2s or for p2p
			if((PretupsI.C2S_MODULE).equalsIgnoreCase(moduleType))
			{
				destinationDir=p_map.get("FTP_AMB_SRC_DIR");
				sourceFile=p_map.get("FTP_AMB_RECV_DIR");
				filename=p_map.get("AMB_SERVER_FILE_NAME");
				movePath=p_map.get("SERVER_MOVE_PATH");
			}
			else{
				
				destinationDir=p_map.get("FTP_AMB_SRC_DIR_P2P");
				sourceFile=p_map.get("FTP_AMB_P2P_RECV_DIR");
				filename=p_map.get("AMB_P2P_SERVER_FILE_NAME");
				movePath=p_map.get("SERVER_MOVE_PATH_P2P");
			}
			if(LOG.isDebugEnabled())LOG.debug(methodName,"Entered hostName := "+hostName +" userName := "+userName+" password := "+password+" moduleType := "+moduleType+" hostPort := "+hostPort+" destinationDir := "+destinationDir+" sourceFile := "+sourceFile+" filename :="+filename+" movePath :="+movePath);	
			SFTPUtils sftpUtils = new SFTPUtils();
			sftpUtils.setHostName(hostName);
			sftpUtils.setHostPort(hostPort);
			sftpUtils.setUserName(userName);
			sftpUtils.setPassWord(password);
			sftpUtils.setDestinationDir(destinationDir);
		
			result = sftpUtils.downloadFileToFTP1(sourceFile,filename,p_fileDate,movePath);
		} catch (Exception e) {
		
			LOG.error(className,"File not found"+e);
		}
		finally
		{
			LOG.debug(className,"\n Exited from the process of downloding file from server"+result);
			if(LOG.isDebugEnabled())LOG.debug(methodName,"Exit");	
		}

	}
	


}

