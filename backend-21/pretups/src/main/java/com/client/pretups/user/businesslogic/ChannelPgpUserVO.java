package com.client.pretups.user.businesslogic;

import java.io.Serializable;
import java.util.Date;

//import org.apache.struts.upload.FormFile;

import com.btsl.pretups.grouptype.businesslogic.GroupTypeCountersVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;

/**
 * @(#)ChannelPgpUserVO.java Copyright(c) 2015, Bharti Telesoft Int. Public Ltd.
 *                           All Rights Reserved Travelling object for channel
 *                           user
 *                           -------------------------------------------------------------------------------------------------
 *                           Author Date History
 *                           -------------------------------------------------------------------------------------------------
 *                           Yogesh Pandey 04/02/2015 Initial Creation
 *                           ------------------------------------------------------------------------------------------------
 */

public class ChannelPgpUserVO extends UserVO implements Serializable,
        Comparable {

    // Added by Yogesh Pandey
    private String _userId;
    private String _pgpIp;
    private String _pgpPort;
    //private FormFile _pgpEncryptionKey;
    //private FormFile _pgpDecryptionKey;
    private String _pgpEncryptKeyFileName;
    private String _pgpDecryptKeyFileName;
    private String _pgpUserName;
    private String _pgpPassword;
    private String _sftpPGPFilePath;
    private String _passphrase;

    public int compareTo(Object arg0) {
        ChannelUserVO obj = (ChannelUserVO) arg0;
        try {
            if (this.getCategoryVO().getSequenceNumber() > obj.getCategoryVO()
                    .getSequenceNumber())
                return 1;
            return -1;
        } catch (Exception e) {
            return 1;
        }
    }

    public String getpgpPort() {
        return _pgpPort;
    }

    public void setpgpPort(String port) {
        _pgpPort = port;
    }

/*
    public FormFile getpgpEncryptionKey() {
        return _pgpEncryptionKey;
    }

    public void setpgpEncryptionKey(FormFile encryptionKey) {
        _pgpEncryptionKey = encryptionKey;
    }
*/

    public String getpgpUserName() {
        return _pgpUserName;
    }

    public void setpgpUserName(String userName) {
        _pgpUserName = userName;
    }

    public String getpgpPassword() {
        return _pgpPassword;
    }

    public void setpgpPassword(String password) {
        _pgpPassword = password;
    }

    public String getpgpIp() {
        return _pgpIp;
    }

    public void setpgpIp(String ip) {
        _pgpIp = ip;
    }

    public String getuserId() {
        return _userId;
    }

    public void setuserId(String id) {
        _userId = id;
    }

/*
    public FormFile getpgpDecryptionKey() {
        return _pgpDecryptionKey;
    }

    public void setpgpDecryptionKey(FormFile decryptionKey) {
        _pgpDecryptionKey = decryptionKey;
    }
*/

    public String getpgpDecryptKeyFileName() {
        return _pgpDecryptKeyFileName;
    }

    public void setpgpDecryptKeyFileName(String decryptKey) {
        _pgpDecryptKeyFileName = decryptKey;
    }

    public String getpgpEncryptKeyFileName() {
        return _pgpEncryptKeyFileName;
    }

    public void setpgpEncryptKeyFileName(String encryptKey) {
    	_pgpEncryptKeyFileName = encryptKey;
    }

	public String getSftpPGPFilePath() {
		return _sftpPGPFilePath;
	}

	public void setSftpPGPFilePath(String sftpPGPFilePath) {
		_sftpPGPFilePath = sftpPGPFilePath;
	}

	public String getPassphrase() {
		return _passphrase;
	}

	public void setPassphrase(String passphrase) {
		_passphrase = passphrase;
	}

}
