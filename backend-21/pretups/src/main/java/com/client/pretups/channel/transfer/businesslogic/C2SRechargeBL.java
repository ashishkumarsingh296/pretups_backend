
package com.client.pretups.channel.transfer.businesslogic;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2SRechargeBLI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.client.pretups.user.businesslogic.ChannelPgpUserVO;
import com.client.pretups.util.pgp.encrypTor;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class C2SRechargeBL implements C2SRechargeBLI{
    /**
     * Commons Logging instance.
     */
    private static Log _log = LogFactory.getLog(C2SRechargeBL.class.getName());
    
	public void loadPGPUser(Locale locale, ArrayList pgpUserList, ChannelUserVO channelUserVO, String PGP_FILE_PATH, String filePath, String fileName) throws BTSLBaseException
    {
		final String methodName = "loadPGPUser";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered  p_userID " + channelUserVO.getUserID());

		ChannelPgpUserVO pgpUserVO=new ChannelPgpUserVO();
        Channel channel = null;
		ChannelSftp channelSftp = null;
		Session session = null;
		FileInputStream fis = null;
		File file = null;
		try {
			pgpUserVO=(ChannelPgpUserVO)pgpUserList.get(0);
			encrypTor.pgpEncrypt(PGP_FILE_PATH+"/"+channelUserVO.getUserID()+"/"+"ENCRYPTION/"+pgpUserVO.getpgpEncryptKeyFileName(),PGP_FILE_PATH+"/"+channelUserVO.getUserID()+"/PGPENCRYPTED/"+fileName.split("[.]")[0]+".pgp",filePath+fileName,pgpUserVO.getPassphrase());
			filePath=PGP_FILE_PATH+"/"+channelUserVO.getUserID()+"/PGPENCRYPTED/";
			fileName=fileName.split("[.]")[0]+".pgp";
			String SFTPHOST = pgpUserVO.getpgpIp();
			int SFTPPORT = Integer.parseInt(pgpUserVO.getpgpPort());
			String SFTPUSER = pgpUserVO.getUserName();
			String SFTPPASS = pgpUserVO.getPassword();
			String SFTPWORKINGDIR = pgpUserVO.getSftpPGPFilePath();
			String FILETOTRANSFER = filePath+fileName;
			JSch jsch = new JSch();
			session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
			session.setPassword(SFTPPASS);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;						
			channelSftp.cd(SFTPWORKINGDIR);
			file = new File(FILETOTRANSFER);
			fis = new FileInputStream(file);
			channelSftp.put(fis, file.getName());
		} catch (Exception ex) {
			_log.errorTrace(methodName,ex);
			PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(),BTSLUtil.getMessage(locale, PretupsErrorCodesI.FTP_FAIL, null),null, null, locale);
			pushMessage.push();
			throw new BTSLBaseException("sftp.failed");
		}finally{
			try {
				if (channelSftp != null) {
					channelSftp.disconnect();
				}
			}
			catch (Exception e) {
				_log.errorTrace(methodName,e);
			}
			try {
				if (channel != null) {
					channel.disconnect();
				}
			}
			catch (Exception e) {
				_log.errorTrace(methodName,e);
			}
			try {
				if (session != null) {
					session.disconnect();
				}
			}
			catch (Exception e) {
				_log.errorTrace(methodName,e);
			}
			try {
				if(fis !=null) {
					fis.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}			
			if (_log.isDebugEnabled())
	            _log.debug(methodName, "Exit " );
		}
	
    } 
}
