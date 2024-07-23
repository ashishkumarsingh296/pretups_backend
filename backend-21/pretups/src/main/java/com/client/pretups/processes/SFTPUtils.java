
/**SFTPUtils.java
 * Name                              Date            History
 *------------------------------------------------------------------------
 * Mahindra Comviva					17/04/2015	Initial Creation
 *------------------------------------------------------------------------
 * Copyright (c) 2015 Mahindra Comviva Technologies Limited.
 * Main class for uploading and downloading file from server
 */
package com.client.pretups.processes;
import java.io.InputStream;
import java.util.Vector;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.client.pretups.processes.clientprocesses.HandleAmbiguousC2SUnsettledCases;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPUtils {
	private Logger logger = LogManager.getLogger(SFTPUtils.class);
	public static Log LOG = LogFactory.getLog(HandleAmbiguousC2SUnsettledCases.class.getName());

	private String _hostName;
	private String _hostPort;
	private String _userName;
	private String _passWord;
	private String _destinationDir;
	private ChannelSftp _channelSftp = null;
	private Session _session = null;
	private Channel _channel = null;
	private int _userGroupId = 0;
	private String _filePermission;
	 
	/**
	 * default constructor
	 */
	public SFTPUtils()
	{
	}

	public String getHostName() {
		return _hostName;
	}

	public void setHostName(String hostName) {
		this._hostName = hostName;
	}

	public String getHostPort() {
		return _hostPort;
	}

	public void setHostPort(String hostPort) {
		this._hostPort = hostPort;
	}

	public String getUserName() {
		return _userName;
	}

	public void setUserName(String userName) {
		this._userName = userName;
	}

	public String getPassWord() {
		return _passWord;
	}

	public void setPassWord(String passWord) {
		this._passWord = passWord;
	}

	public String getDestinationDir() {
		return _destinationDir;
	}

	public void setDestinationDir(String destinationDir) {
		this._destinationDir = destinationDir;
	}

	public int getUserGroupId() {
		return _userGroupId;
	}

	public void setUserGroupId(int userGroupId) {
		this._userGroupId = userGroupId;
	}
	public String getFilePermission() {
		return _filePermission;
	}

	public void setFilePermission(String filePermission) {
		_filePermission = filePermission;
	}
	private void initChannelSftp() {
		if(LOG.isDebugEnabled())LOG.debug("initChannelSftp","Entered userName :"+_userName+" hostName :"+_hostName+" hostPort :"+_hostPort );
		_channelSftp = null;
		_session = null;
		try
		{
			JSch jsch = new JSch();
			_session = jsch.getSession(_userName, _hostName,Integer.valueOf(_hostPort));
			_session.setPassword(_passWord);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			_session.setConfig(config);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			LOG.error("initChannelSftp","Exception occured"+ex.getMessage());
		}
		if(LOG.isDebugEnabled())LOG.debug("initChannelSftp","Exit" );
	}

	 /**
     * Method uploadFileToFTP1()
     * This method is used to download file from server 
     * @param filename String
   	 * @param fis InputStream
     * @author rajvi.desai
     */
	public String uploadFileToFTP1(String filename, InputStream fis,boolean enableLog) {
		if(LOG.isDebugEnabled())LOG.debug("uploadFileToFTP1","Entered");
		String result = "";
		
		try
		{
			initChannelSftp();
			if (!_session.isConnected())
				_session.connect();
				
			_channel = _session.openChannel("sftp");
			_channel.connect();
			_channelSftp = (ChannelSftp) _channel;
			try
			{
				_channelSftp.cd(_destinationDir);

			} catch (SftpException e) {
				_channelSftp.mkdir(_destinationDir);
				_channelSftp.cd(_destinationDir);
			}
			_channelSftp.put(fis, filename);
			try{
				if(logger.isDebugEnabled())
				 logger.debug("uploaded successful file name:" + filename+"_destinationDir="+_destinationDir+"file permission="+_filePermission+" permission after conversion"+Integer.parseInt(_filePermission,8));
				_channelSftp.chmod(Integer.parseInt(_filePermission,8),_destinationDir+"/"+filename);
			}
			catch(Exception e){
				LOG.error("initChannelSftp","Exception occured in chmod"+e.getMessage());
				
			}
			if(LOG.isDebugEnabled())LOG.debug("uploadFileToFTP1","Upload successful portfolio file name:" + filename);
			logger.info("Upload successful portfolio file name:" + filename);
			result = String.format("sftp://%s/%s/%s", _hostName, _destinationDir, filename);
		}
		catch (Exception ex) {
			LOG.error("initChannelSftp","Exception occured"+ex.getMessage());
		}
		finally
		{
			_channelSftp.exit();
			_channel.disconnect();
			_session.disconnect();	
			if(LOG.isDebugEnabled())LOG.debug("uploadFileToFTP1","Exit result"+result );
		
		}
		return result;
	}
	 /**
     * Method downloadFileToFTP1()
     * This method is used to download file from server 
     * @param sourceFile String
     * @param filename String
     * @param fixedDate String
     * @param movePath String
     * @author rajvi.desai
     */

	public String downloadFileToFTP1(String sourceFile,String filename,String p_fileDate,String movePath) {
		if(LOG.isDebugEnabled())LOG.debug("downloadFileToFTP1","Entered : sourceFile="+sourceFile+",filename="+filename+",p_fileDate="+p_fileDate+",movePath="+movePath);
		String result = null;
		
		try
		{
			initChannelSftp();
			if (!_session.isConnected())
				_session.connect();
			_channel = _session.openChannel("sftp");
			_channel.connect();
			
			_channelSftp = (ChannelSftp) _channel;
			try
			{
				if(LOG.isDebugEnabled())LOG.debug("downloadFileToFTP1","_destinationDir="+_destinationDir);
				_channelSftp.cd(_destinationDir);
			}
			catch (SftpException e) {
				_channelSftp.mkdir(_destinationDir);
				_channelSftp.cd(_destinationDir);
			}

			Vector<ChannelSftp.LsEntry> list = _channelSftp.ls(_destinationDir);            
			for(ChannelSftp.LsEntry entry : list) {
				if(entry.getFilename().equalsIgnoreCase(filename+"_"+p_fileDate+".csv"))
				{
					if(LOG.isDebugEnabled())LOG.debug("downloadFileToFTP1","_destinationDir="+_destinationDir);
					_channelSftp.get(entry.getFilename(), sourceFile+entry.getFilename());
					_channelSftp.cd("..");
				try
				{
					_channelSftp.cd(movePath);
				}
				catch(SftpException e)
				{   
					_channelSftp.mkdir(movePath);}
					_channelSftp.rename(_destinationDir+"/"+entry.getFilename(), movePath+entry.getFilename());
				}
			}

			logger.info("Upload successful portfolio file name:" );
			result = String.format("sftp://%s/%s", _hostName, _destinationDir);
			
		}
		catch (Exception ex) {
			logger.error(ex);
			ex.printStackTrace();
			LOG.error("downloadFileToFTP1","Exception occured"+ex.getMessage());
		}
		finally
		{
			_channelSftp.exit();
			_channel.disconnect();
			_session.disconnect();	
			if(LOG.isDebugEnabled())LOG.debug("downloadFileToFTP1","Exit result:"+result);
		
		}
		return result;
	}  

	/**
     * Method checkExist()
     * This method is used to download file from server 
     * @param sourceFile String
     * @param filename String
     * @param fixedDate String
     * @param movePath String
     * @author rajvi.desai
     */
	public boolean checkExist(String fileName) {
		if(LOG.isDebugEnabled())LOG.debug("checkExist","Entered");
		boolean existed = false;
		try
		{
			initChannelSftp();
			if (!_session.isConnected())
				_session.connect();
			_channel = _session.openChannel("sftp");
			_channel.connect();
			_channelSftp = (ChannelSftp) _channel;
			try {
				_channelSftp.cd(_destinationDir);
			} catch (SftpException e) {
				_channelSftp.mkdir(_destinationDir);
				_channelSftp.cd(_destinationDir);
			}
			Vector ls = _channelSftp.ls(_destinationDir);
			if (ls != null)
			{
				logger.info(fileName);
				for (int i = 0; i < ls.size(); i++)
				{
					LsEntry entry = (LsEntry) ls.elementAt(i);
					String file_name = entry.getFilename();
					
					if (!entry.getAttrs().isDir())
					{
						if (fileName.toLowerCase().startsWith(file_name))
							existed = true;
						else
							existed = false;
					}
				}
			}
			_channelSftp.exit();
			_channel.disconnect();
			_session.disconnect();
		}
		catch (Exception ex)
		{
			LOG.error("checkExist","Exception occured"+ex.getMessage());
			existed = false;
			if (_session.isConnected()) 
				_session.disconnect();
		}
		return existed;
	}

	public void deleteFile(String fileName)
	{
		if(LOG.isDebugEnabled())LOG.debug("deleteFile","Entered");
		try
		{
			if (!_session.isConnected())
				_session.connect();
			_channel = _session.openChannel("sftp");
			_channel.connect();
			_channelSftp = (ChannelSftp) _channel;
			try
			{
				_channelSftp.cd(_destinationDir);
			}
			catch (SftpException e)
			{
				_channelSftp.mkdir(_destinationDir);
				_channelSftp.cd(_destinationDir);
			}
			_channelSftp.rm(fileName);
			_channelSftp.exit();
			_channel.disconnect();
			_session.disconnect();
		}
		catch (Exception ex)
		{
			LOG.error("deleteFile","Exception occured"+ex.getMessage());
			logger.info(ex.getMessage());
			if (_session.isConnected()) 
				_session.disconnect();
		}

	}

}
