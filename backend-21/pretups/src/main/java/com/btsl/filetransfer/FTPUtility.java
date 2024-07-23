package com.btsl.filetransfer;

/**
 * @author dhiraj.tiwari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window -
 *         Preferences - Java - Code Style - Code Templates
 */
import java.io.File;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;
import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPConnectMode;

public class FTPUtility {

    private Log _log = LogFactory.getLog(FTPUtility.class.getName());
    private FTPClient _ftpClient = null;
    private String _rootDirectory = null;
    private String _filePermission = null;

    /**
     * Default Constructor
     */
    public FTPUtility() {

    }

    /**
     * This method is used to connect the Remote FTP Server
     * 
     * @param String
     *            p_ipAddress
     * @param int p_ftpTimeOut
     * @throws BTSLBaseException
     */
    private void connectFtpClient(String p_ipAddress, int p_ftpTimeOut) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("connectFtpClient", "Entered p_ipAddress=" + p_ipAddress + " p_ftpTimeOut=" + p_ftpTimeOut);
        try {
            _ftpClient = new FTPClient(p_ipAddress);
            _ftpClient.setTimeout(p_ftpTimeOut);
            _ftpClient.setConnectMode(FTPConnectMode.ACTIVE);

        } catch (Exception e) {
            _log.errorTrace("Exception in connectFtpClient() ", e);
            _log.error("connectFtpClient", "Exception e:" + e.getMessage());
            throw new BTSLBaseException(this,"connectFtpClient",InterfaceErrorCodesI.ERROR_FTP_CONNECT_FAIL,e);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("connectFtpClient", "Exited");
        }
    }

    /**
     * This method implements the functionality to open FTPConnection and send
     * the login request.
     * 
     * @param String
     *            p_interfaceID
     * @param String
     *            p_ipAddress
     * @param int p_port
     * @param String
     *            p_userName
     * @param String
     *            p_password
     * @param int p_ftpTimeOut
     */
    public void connectNLoginToRemoteServer(String p_interfaceID, String p_ipAddress, int p_port, String p_userName, String p_password, int p_ftpTimeOut) throws BTSLBaseException {
        if (_log.isDebugEnabled())
        {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_interfaceID:");
        	msg.append(p_interfaceID);
        	msg.append("p_ipAddress: ");
        	msg.append(p_ipAddress);
        	msg.append(",p_port: ");
        	msg.append(p_port);
        	msg.append(", p_userName: ");
        	msg.append(p_userName);
        	msg.append(",p_password :");
        	msg.append(p_password);
        	msg.append(",p_ftpTimeOut: ");
        	msg.append(p_ftpTimeOut);
        	
        	String message=msg.toString();
            _log.debug("connectNLoginToRemoteServer", message);
        }
        
        try {
            // Connect to the remote machine via FTPClient
            connectFtpClient(p_ipAddress, p_ftpTimeOut);
            // Login to the FTP Server
            login(p_userName, p_password);
        } catch (BTSLBaseException be) {
            throw new BTSLBaseException(be);
        } catch (Exception e) {
            _log.errorTrace("Exception in connectNLoginToRemoteServer() ", e);
            _log.error("connectNLoginToRemoteServer", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "FTPUtility[connectNLoginToRemoteServer]", "", "INTERFACE_ID:" + p_interfaceID, "", "While initializing the Clinet object get Exception e:" + e.getMessage());
            throw new BTSLBaseException(this,"connectNLoginToRemoteServer",InterfaceErrorCodesI.ERROR_FTP_CONNECT_FAIL,e);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("connectNLoginToRemoteServer", "Exited");
        }
    }

    /**
     * This method is used to open an FTP Session with remote FTP Server
     * 
     * @param None
     * @returen None
     */
    public void login(String p_userName, String p_password) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("login", "Entered p_userName:" + p_userName + " p_password:" + p_password);
        try {
            _ftpClient.login(p_userName, p_password);
            _rootDirectory = _ftpClient.pwd();
            if (_log.isDebugEnabled())
                _log.debug("login successful", "login successful");
        } catch (Exception e) {
            _log.errorTrace("Exception in login() ", e);
            _log.error("login", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "FTPUtility[login]", "", "INTERFACE_ID:", "", "While Login to the FTP server get Exception e:" + e.getMessage());
            throw new BTSLBaseException(this,"login",InterfaceErrorCodesI.ERROR_FTP_LOGIN_FAILED,e);// Confirm
                                                                                     // for
                                                                                     // new
                                                                                     // InterfaceErrorCode.
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("login", "Exited");
        }
    }

    /**
     * Method ftpFilesToRemoteSystem
     * This is the method by which files would be ftp'd
     * 
     * @param p_interfaceID
     *            String
     * @return void
     * @throws BTSLBaseException
     */

    public void ftpFilesToRemoteSystem(String p_interfaceID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("ftpFilesToRemoteSystem", "Entered with p_interfaceID=" + p_interfaceID);
        int connectRetryCounts = 1;
        int transferRetryCounts = 1;
        long connectRetrySleepTime = 1000;
        long transferRetrySleepTime = 1000;
        String sourceDir = null;
        String destDir = null;
        String[] files = null;
        File dir = null;
        int currentConnectCount = 0;
        String localSuccessDestDir;
        String localFailedDestDir;
        try {
            connectRetryCounts = Integer.parseInt(FileCache.getValue(p_interfaceID, "FTP_CONNECT_POST2PRE_RETRY_ATTEMPT"));
            connectRetrySleepTime = Integer.parseInt(FileCache.getValue(p_interfaceID, "FTP_CONNECT_POST2PRE_RETRY_SLEEP_TIME"));
            transferRetryCounts = Integer.parseInt(FileCache.getValue(p_interfaceID, "FTP_TRANSFER_POST2PRE_RETRY_ATTEMPT"));
            transferRetrySleepTime = Integer.parseInt(FileCache.getValue(p_interfaceID, "FTP_TRANSFER_POST2PRE_RETRY_SLEEP_TIME"));

            sourceDir = FileCache.getValue(p_interfaceID, "FTP_POST2PRE_SRC_DIR");
            if (BTSLUtil.isNullString(sourceDir)) {
                _log.error("ftpFilesToRemoteSystem", "FTP_POST2PRE_SRC_DIR is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FTPUtility[ftpFilesToRemoteSystem]", "", "", "", "FTP_POST2PRE_SRC_DIR is not defined in IN File");
                throw new BTSLBaseException(this, "ftpFilesToRemoteSystem", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            sourceDir = sourceDir.trim();
            destDir = FileCache.getValue(p_interfaceID, "FTP_POST2PRE_DEST_DIR");
            if (BTSLUtil.isNullString(destDir)) {
                _log.error("ftpFilesToRemoteSystem", "FTP_POST2PRE_DEST_DIR is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FTPUtility[ftpFilesToRemoteSystem]", "", "", "", "FTP_POST2PRE_DEST_DIR is not defined in IN File");
                throw new BTSLBaseException(this, "ftpFilesToRemoteSystem", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            destDir = destDir.trim();
            localSuccessDestDir = FileCache.getValue(p_interfaceID, "LOCAL_POST2PRE_SUCCESS_DIR");
            if (BTSLUtil.isNullString(localSuccessDestDir)) {
                _log.error("ftpFilesToRemoteSystem", "LOCAL_POST2PRE_SUCCESS_DIR is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FTPUtility[ftpFilesToRemoteSystem]", "", "", "", "LOCAL_POST2PRE_SUCCESS_DIR is not defined in IN File");
                throw new BTSLBaseException(this, "ftpFilesToRemoteSystem", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            localSuccessDestDir = localSuccessDestDir.trim();
            localFailedDestDir = FileCache.getValue(p_interfaceID, "LOCAL_POST2PRE_FAILED_DIR");
            if (BTSLUtil.isNullString(localFailedDestDir)) {
                _log.error("ftpFilesToRemoteSystem", "LOCAL_POST2PRE_FAILED_DIR is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FTPUtility[ftpFilesToRemoteSystem]", "", "", "", "LOCAL_POST2PRE_FAILED_DIR is not defined in IN File");
                throw new BTSLBaseException(this, "ftpFilesToRemoteSystem", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            String ipAddress = FileCache.getValue(p_interfaceID, "FTP_POST2PRE_IP");
            if (InterfaceUtil.isNullString(ipAddress)) {
                _log.error("ftpFilesToRemoteSystem", "FTP_POST2PRE_IP is not defined in the INFile with INTERFACE_ID:" + p_interfaceID);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "FTPUtility[ftpFilesToRemoteSystem]", "", "INTERFACE_ID:" + p_interfaceID, "", "FTP_POST2PRE_IP is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FTP_CONNECT_FAIL);
            }
            ipAddress = ipAddress.trim();
            String portStr = FileCache.getValue(p_interfaceID, "FTP_POST2PRE_PORT");
            if (InterfaceUtil.isNullString(portStr)) {
                _log.error("ftpFilesToRemoteSystem", "FTP_POST2PRE_PORT is not defined in the INFile with INTERFACE_ID:" + p_interfaceID);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "FTPUtility[ftpFilesToRemoteSystem]", "", "INTERFACE_ID:" + p_interfaceID, "", "FTP_POST2PRE_PORT is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FTP_CONNECT_FAIL);
            }
            int port = Integer.parseInt(portStr.trim());
            String ftpTimeOutStr = FileCache.getValue(p_interfaceID, "FTP_POST2PRE_TIME_OUT");
            if (InterfaceUtil.isNullString(ftpTimeOutStr)) {
                _log.error("ftpFilesToRemoteSystem", "FTP_POST2PRE_TIME_OUT is not defined in the INFile with INTERFACE_ID:" + p_interfaceID);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "FTPUtility[ftpFilesToRemoteSystem]", "", "INTERFACE_ID:" + p_interfaceID, "", "FTP_POST2PRE_TIME_OUT is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FTP_CONNECT_FAIL);
            }
            int ftpTimeOut = Integer.parseInt(ftpTimeOutStr.trim());
            String userName = FileCache.getValue(p_interfaceID, "FTP_POST2PRE_USER_NAME");
            if (InterfaceUtil.isNullString(userName)) {
                _log.error("ftpFilesToRemoteSystem", "FTP_POST2PRE_USER_NAME is not defined in the INFile with INTERFACE_ID:" + p_interfaceID);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "FTPUtility[ftpFilesToRemoteSystem]", "", "INTERFACE_ID:" + p_interfaceID, "", "FTP_POST2PRE_USER_NAME is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FTP_CONNECT_FAIL);
            }
            userName = userName.trim();
            String password = FileCache.getValue(p_interfaceID, "FTP_POST2PRE_PASSWD");
            if (InterfaceUtil.isNullString(password)) {
                _log.error("ftpFilesToRemoteSystem", "FTP_POST2PRE_PASSWD is not defined in the INFile with INTERFACE_ID:" + p_interfaceID);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "FTPUtility[ftpFilesToRemoteSystem]", "", "INTERFACE_ID:" + p_interfaceID, "", "FTP_POST2PRE_PASSWD is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FTP_CONNECT_FAIL);
            }
            password = password.trim();
            _filePermission = FileCache.getValue(p_interfaceID, "FTP_POST2PRE_FILE_PERMISSION");
            if (InterfaceUtil.isNullString(_filePermission) || _filePermission.length() != 3) {
                _log.info("ftpFilesToRemoteSystem", "FTP_POST2PRE_PASSWD is not defined in the INFile with INTERFACE_ID:" + p_interfaceID);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "FTPUtility[ftpFilesToRemoteSystem]", "", "INTERFACE_ID:" + p_interfaceID, "", "FTP_POST2PRE_FILE_PERMISSION is not defined in the INFile so making it 555");
                _filePermission = "555";
            }
            _filePermission = _filePermission.trim();
            /*
             * while(currentConnectCount++ <= connectRetryCounts)
             * {
             * try
             * {
             * if(!isServerConnected)
             * connectNLoginToRemoteServer(p_interfaceID);
             * if(_log.isDebugEnabled())_log.debug("ftpFilesToRemoteSystem"
             * ,"connectNLoginToRemoteServer completed successfully");
             * }
             * catch(BTSLBaseException be)
             * {
             * if(_log.isDebugEnabled())_log.debug("ftpFilesToRemoteSystem"
             * ,"connectNLoginToRemoteServer completed successfully catch");
             * //Should RETRY be done when login fails
             * if(currentConnectCount >connectRetryCounts)
             * throw be;
             * try{Thread.currentThread().sleep(connectRetrySleepTime);}catch(
             * Exception e){};
             * continue;
             * }
             * 
             * currentConnectCount=1;
             * try
             * {
             * isServerConnected=true;
             * dir = new File(sourceDir);
             * files = dir.list();
             * for(int i=0,j=files.length; i<j; i++)
             * {
             * if(_log.isDebugEnabled())_log.debug("ftpFilesToRemoteSystem"
             * ,"file number["+i+"] is "+files[i]);
             * transferFiles(sourceDir,destDir,localSuccessDestDir,
             * localFailedDestDir,files[i]);
             * }
             * break;
             * }
             * catch(BTSLBaseException be)
             * {
             * if(currentConnectCount > transferRetryCounts)
             * throw be;
             * try{Thread.currentThread().sleep(transferRetrySleepTime);}catch(
             * Exception e){};
             * continue;
             * }
             * }
             * }
             */
            while (currentConnectCount++ <= connectRetryCounts) {
                try {
                    connectNLoginToRemoteServer(p_interfaceID, ipAddress, port, userName, password, ftpTimeOut);
                    if (_log.isDebugEnabled())
                    {
                        _log.debug("ftpFilesToRemoteSystem", "connectNLoginToRemoteServer completed successfully currentConnectCount" + currentConnectCount + connectRetryCounts);
                    }
                    break;
                } catch (BTSLBaseException be) {
                    if (_log.isDebugEnabled())
                        _log.debug("ftpFilesToRemoteSystem", "connectNLoginToRemoteServer completed successfully catch currentConnectCount" + currentConnectCount + connectRetryCounts);
                    // Should RETRY be done when login fails
                    if (currentConnectCount > connectRetryCounts) {
                        dir = new File(sourceDir);
                        files = dir.list();
                        int j = files.length;
                        for (int i = 0; i < j; i++) {
                            moveFailedTxnFile(sourceDir, localFailedDestDir, files[i]);
                        }
                        throw new BTSLBaseException(be);
                    }
                    try {
                        Thread.currentThread().sleep(connectRetrySleepTime);
                    } catch (Exception e) {
                        _log.errorTrace("Exception in ftpFilesToRemoteSystem() ", e);
                    }
                    ;
                    continue;
                }
            }

            dir = new File(sourceDir);
            files = dir.list();
            int j = files.length;
            for (int i = 0; i < j; i++) {
                currentConnectCount = 0;
                while (currentConnectCount++ <= transferRetryCounts) {
                    if (_log.isDebugEnabled())
                    {
                    	StringBuffer msg=new StringBuffer("");
                    	msg.append("file number[");
                    	msg.append(i);
                    	msg.append("] is ");
                    	msg.append(files[i]);
                    	msg.append(" currentConnectCount");
                    	msg.append(currentConnectCount);
                    	
                    	String message=msg.toString();
                        _log.debug("ftpFilesToRemoteSystem", message);
                    }
                    
                    try {
                        transferFiles(sourceDir, destDir, files[i]);
                        moveSuccessTxnFile(sourceDir, localSuccessDestDir, files[i]);
                        break;
                    } catch (BTSLBaseException be) {
                        _log.errorTrace("Exception in ftpFilesToRemoteSystem() ", be);
                        if (currentConnectCount > transferRetryCounts)
                        {
                        	StringBuffer msg=new StringBuffer("");
                        	msg.append("Even after ");
                        	msg.append(transferRetryCounts);
                        	msg.append(" attempts ");
                        	msg.append(files[i]);
                        	msg.append("could not be ftp'd so storing this file into failed transaction directory: ");
                        	msg.append(localFailedDestDir);
                       
                        	String message=msg.toString();
                            _log.info("ftpFilesToRemoteSystem", message);
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "FTPUtility[ftpFilesToRemoteSystem]", "", "", "", "Even after " + transferRetryCounts + " attempts " + files[i] + "could not be ftp'd so storing this file into failed transaction directory: " + localFailedDestDir);
                            moveFailedTxnFile(sourceDir, localFailedDestDir, files[i]);
                            break;
                            // throw be;
                        }
                        try {
                            Thread.currentThread().sleep(transferRetrySleepTime);
                        } catch (Exception e) {
                            _log.errorTrace("Exception in ftpFilesToRemoteSystem() ", e);
                        }
                        ;
                        if (_log.isDebugEnabled())
                        {
                        	StringBuffer msg=new StringBuffer("");
                        	msg.append("going for retry  ");
                        	msg.append(currentConnectCount);
                        	msg.append(" after transferRetrySleepTime ");
                        	msg.append(transferRetrySleepTime);                        	
                        	
                        	String message=msg.toString();
                            _log.debug("ftpFilesToRemoteSystem", message);
                        }
                        
                        continue;
                    }
                }
            }
            if (j == 0) {
                _log.info("ftpFilesToRemoteSystem", "No POST2PRE CDR Files found in the source directory for FTP ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "FTPUtility[ftpFilesToRemoteSystem]", "", "", "", "No POST2PRE CDR Files found in the source directory for FTP ");
            }
            /*
             * while(currentConnectCount++ <= transferRetryCounts)
             * {
             * try
             * {
             * dir = new File(sourceDir);
             * files = dir.list();
             * int i=0,j=0;
             * for(i=0,j=files.length; i<j; i++)
             * {
             * if(_log.isDebugEnabled())_log.debug("ftpFilesToRemoteSystem"
             * ,"file number["+i+"] is "+files[i]);
             * transferFiles(sourceDir,destDir,localSuccessDestDir,
             * localFailedDestDir,files[i]);
             * }
             * if(j==0)
             * {
             * _log.info("ftpFilesToRemoteSystem",
             * "No POST2PRE CDR Files found in the source directory for FTP ");
             * EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,
             * EventStatusI
             * .RAISED,EventLevelI.INFO,"FTPUtility[ftpFilesToRemoteSystem]"
             * ,"",""
             * ,"","No POST2PRE CDR Files found in the source directory for FTP "
             * );
             * }
             * break;
             * }
             * catch(BTSLBaseException be)
             * {
             * if(currentConnectCount > transferRetryCounts)
             * {
             * 
             * throw be;
             * }
             * try{Thread.currentThread().sleep(transferRetrySleepTime);}catch(
             * Exception e){};
             * if(_log.isDebugEnabled())_log.debug("ftpFilesToRemoteSystem"
             * ,"going for retry  " + currentConnectCount
             * +" after transferRetrySleepTime "+ transferRetrySleepTime);
             * continue;
             * }
             * }
             */
        } catch (BTSLBaseException be) {
            throw new BTSLBaseException(be);
        } catch (Exception e) {
            _log.errorTrace("Exception in ftpFilesToRemoteSystem() ", e);
            _log.error("ftpFilesToRemoteSystem", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FTPUtility[ftpFilesToRemoteSystem]", "", "", "", "While trying to FTP got Exception=" + e.getMessage());
            throw new BTSLBaseException(this, "ftpFilesToRemoteSystem", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION,e);
        } finally {
            if (_ftpClient != null)
                try {
                    _ftpClient.quit();
                    _ftpClient.clearSOCKS();
                } catch (Exception e) {
                    _log.errorTrace("Exception in ftpFilesToRemoteSystem() ", e);
                }
            if (_log.isDebugEnabled())
                _log.debug("ftpFilesToRemoteSystem", "Exited currentConnectCount" + currentConnectCount);
        }
    }

    /**
     * This method is used to change directory, transfer files on remote server.
     * This also check whether file has been uploaded or not
     * 
     * @param String
     *            localFileLoaction
     * @param String
     *            destFileLocation
     * @param String
     *            fileName
     * @throws BTSLBaseException
     */

    private void transferFiles(String localFileLoaction, String destFileLocation, String fileName) throws BTSLBaseException {
        if (_log.isDebugEnabled())
        {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered localFileLoaction: ");
        	msg.append(localFileLoaction);
        	msg.append(" ,destFileLocation: ");
        	msg.append(destFileLocation);
        	msg.append(" ,fileName:");
        	msg.append(fileName);
        	
        	String message=msg.toString();
            _log.debug("transferFiles", message);
        }
        
        try {
            _ftpClient.chdir(destFileLocation);
            _ftpClient.put(localFileLoaction + "/" + fileName, fileName);
            isFileUploaded(fileName);
            try {
                _ftpClient.chmod(_filePermission, destFileLocation + "/" + fileName);
            } catch (Exception e) {
                _log.errorTrace("Exception in transferFiles() ", e);
                _log.info("transferFiles", "After file transfer, unable to change file permission (" + _filePermission + ") on remote ftp server. Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "FTPUtility[transferFiles]", "", "", "", "After file transfer, unable to change file permission (" + _filePermission + ") on remote ftp server. Exception=" + e.getMessage());

            }
            // After the uploading file to FTP Server, change the directory to
            // root
            // _ftpClient.chdir(_rootDirectory);
        } catch (Exception e) {
            _log.errorTrace("Exception in transferFiles() ", e);
            _log.error("transferFiles", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "FTPUtility[transferFiles]", "", "", "", "While transferring file " + fileName + " got Exception=" + e.getMessage());
            throw new BTSLBaseException(this,"transferFiles",InterfaceErrorCodesI.ERROR_FTP_FILE_UPLOAD,e);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("transferFiles", "Exited");
        }
    }

    /*
     * private void transferFiles(String localFileLoaction,String
     * destFileLocation ,String p_localSuccessDestDir, String
     * p_localFailedDestDir,String fileName) throws BTSLBaseException
     * {
     * if(_log.isDebugEnabled())
     * _log.debug("transferFiles","Entered localFileLoaction: "
     * +localFileLoaction+" ,destFileLocation: "+destFileLocation
     * +",p_localSuccessDestDir: "+ p_localSuccessDestDir+" ,fileName:"+
     * fileName +",p_localFailedDestDir: "+ p_localFailedDestDir);
     * try
     * {
     * _ftpClient.chdir(destFileLocation);
     * _ftpClient.put(localFileLoaction+"/"+fileName,fileName);
     * isFileUploaded(fileName);
     * //After the uploading file to FTP Server, change the directory to root
     * _ftpClient.chdir(_rootDirectory);
     * // //moveSuccessTxnFile(localFileLoaction,p_localFailedDestDir,fileName);
     * }
     * catch(Exception e)
     * {
     * //moveFailedTxnFile(localFileLoaction,p_localFailedDestDir,fileName);
     * throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FTP_FILE_UPLOAD);
     * }
     * finally
     * {
     * if(_log.isDebugEnabled()) _log.debug("transferFiles","Exited");
     * }
     * }
     */

    /**
     * This method is used to check whether the file has been uploaded
     * successfully to the server or not.
     * 
     * @param String
     *            fileName
     * @return boolean
     * @throws BTSLBaseException
     */
    private boolean isFileUploaded(String fileName) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isFileUploaded", "Entered  fileName:" + fileName);
        boolean isUploaded = false;
        String[] fileList = null;
        try {
            fileList = _ftpClient.dir(".");
            for (int i = 0, size = fileList.length; i < size; i++) {
                if (fileList[i].equals(fileName)) {
                    isUploaded = true;
                    break;
                }
            }
            if (!isUploaded)
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FTP_FILE_UPLOAD);
        } catch (BTSLBaseException be) {
            throw new BTSLBaseException(be);
        } catch (Exception e) {
            _log.errorTrace("Exception in isFileUploaded() ", e);
            _log.error("isFileUploaded", " While cheking the file whether uploaded successfully or not,Exception e:" + e.getMessage());

            throw new BTSLBaseException(this,"isFileUploaded",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION,e);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("isFileUploaded", "Exited [if true then success] isUploaded:" + isUploaded);
        }
        return isUploaded;
    }

    /**
     * This method is used to store the successfully ftp'd file into the
     * specific location provided by the system.
     * 
     * @param String
     *            p_orgFileLoaction
     * @param String
     *            p_successFileLocation
     * @param String
     *            p_fileName
     */
    private void moveSuccessTxnFile(String p_orgFileLoaction, String p_successFileLocation, String p_fileName) {
        if (_log.isDebugEnabled())
        {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_orgFileLoaction=");
        	msg.append(p_orgFileLoaction);
        	msg.append("p_successFileLocation=");
        	msg.append(p_successFileLocation);
        	msg.append("p_fileName=");
        	msg.append(p_fileName);
        	
        	String message=msg.toString();
            _log.debug("moveSuccessTxnFile", message);
        }
        
        boolean isFileRenamed = false;
        try {
            File faileTxnDir = new File(p_successFileLocation);
            if (!faileTxnDir.exists()) {
                if (faileTxnDir.mkdirs()) {
                    _log.info("moveSuccessTxnFile", "Location p_successFileLocation:" + p_successFileLocation + " does not exist. So creating it and is created successfully");
                    isFileRenamed = new File(p_orgFileLoaction + "/" + p_fileName).renameTo(new File(p_successFileLocation + "/" + p_fileName));
                    if (isFileRenamed) {
                        _log.info("moveSuccessTxnFile", p_successFileLocation + "does not exist so created " + p_successFileLocation + " directory and then successful txn POST2PRE File " + p_fileName + " is successfully stored at location :" + p_successFileLocation);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "FTPUtility[moveSuccessTxnFile]", "", "", "", p_successFileLocation + "does not exist so created " + p_successFileLocation + " directory and then successful txn POST2PRE File " + p_fileName + " is successfully stored at location :" + p_successFileLocation);
                    } else {
                        _log.info("moveSuccessTxnFile", "Location p_successFileLocation:" + p_successFileLocation + "does not exist so created " + p_successFileLocation + " directory and then successful txn POST2PRE File " + p_fileName + " could not be stored at location :" + p_successFileLocation);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "FTPUtility[moveSuccessTxnFile]", "", "", "", p_successFileLocation + "does not exist so created " + p_successFileLocation + " directory and then successful txn POST2PRE File " + p_fileName + " could not be stored at location :" + p_successFileLocation);
                    }
                    // EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"FTPUtility[moveSuccessTxnFile]","","","",p_successFileLocation
                    // + "does not exist so creating "
                    // +p_successFileLocation+" directory and then successful POST2PRE File "+
                    // p_fileName
                    // +" is successfully stored at location :"+p_successFileLocation);
                } else {
                    _log.info("moveSuccessTxnFile", "Location p_successFileLocation:" + p_successFileLocation + " does not exist and creation also failed ");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "FTPUtility[moveSuccessTxnFile]", "", "", "", "Location p_successFileLocation:" + p_successFileLocation + " does not exist and creation also failed." + "successful txn POST2PRE File " + p_fileName + " could not be stored at location :" + p_successFileLocation);
                }
            } else {
                isFileRenamed = new File(p_orgFileLoaction + "/" + p_fileName).renameTo(new File(p_successFileLocation + "/" + p_fileName));
                if (isFileRenamed) {
                    _log.info("moveSuccessTxnFile", "successful txn POST2PRE File " + p_fileName + " is successfully stored at location :" + p_successFileLocation);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "FTPUtility[moveSuccessTxnFile]", "", "", "", "successful txn POST2PRE File " + p_fileName + " is successfully stored at location :" + p_successFileLocation);
                } else {
                    _log.info("moveSuccessTxnFile", "successful txn POST2PRE File " + p_fileName + " is could not be stored at location :" + p_successFileLocation);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "FTPUtility[moveSuccessTxnFile]", "", "", "", "successful txn POST2PRE File " + p_fileName + " is could not be stored at location :" + p_successFileLocation);
                }
             
            }
        } catch (Exception ex) {
            _log.errorTrace("Exception in moveSuccessTxnFile() ", ex);
            _log.error("moveSuccessTxnFile", "In case of successful trnasaction While moving file " + p_fileName + " to location " + p_successFileLocation + " got Exception ex:" + ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "FTPUtility[moveSuccessTxnFile]", "", "", "", "In case of successful trnasaction While moving file " + p_fileName + " to location " + p_successFileLocation + " got Exception ex:" + ex.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("moveSuccessTxnFile", "Exited");
        }
    }

    /**
     * This method is used to stored the failed ftp file into the specific
     * location provided by the system.
     * 
     * @param String
     *            p_orgFileLoaction
     * @param String
     *            p_failedFileLocation
     * @param String
     *            p_fileName
     */
    private void moveFailedTxnFile(String p_orgFileLoaction, String p_failedFileLocation, String p_fileName) {
        boolean isFileRenamed = false;
        if (_log.isDebugEnabled())
        {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_orgFileLoaction=");
        	msg.append(p_orgFileLoaction);
        	msg.append("p_failedFileLocation=");
        	msg.append(p_failedFileLocation);
        	msg.append("p_fileName=");
        	msg.append(p_fileName);
        	
        	String message=msg.toString();
            _log.debug("moveFailedTxnFile", message);
        }
        
        try {
            File faileTxnDir = new File(p_failedFileLocation);
            if (!faileTxnDir.exists()) {
                if (faileTxnDir.mkdirs()) {
                    _log.info("moveFailedTxnFile", "Location p_failedFileLocation:" + p_failedFileLocation + " does not exist. So creating it and is created successfully");
                    isFileRenamed = new File(p_orgFileLoaction + "/" + p_fileName).renameTo(new File(p_failedFileLocation + "/" + p_fileName));
                    if (isFileRenamed) {
                        _log.info("moveFailedTxnFile", p_failedFileLocation + "does not exist so created " + p_failedFileLocation + " directory and then failed txn POST2PRE File " + p_fileName + " is successfully stored at location :" + p_failedFileLocation);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "FTPUtility[moveFailedTxnFile]", "", "", "", p_failedFileLocation + "does not exist so created " + p_failedFileLocation + " directory and then failed txn POST2PRE File " + p_fileName + " is successfully stored at location :" + p_failedFileLocation);
                    } else {
                        _log.info("moveFailedTxnFile", "Location p_failedFileLocation:" + p_failedFileLocation + "does not exist so created " + p_failedFileLocation + " directory and then failed txn POST2PRE File " + p_fileName + " could not be stored at location :" + p_failedFileLocation);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "FTPUtility[moveFailedTxnFile]", "", "", "", p_failedFileLocation + "does not exist so created " + p_failedFileLocation + " directory and then failed txn POST2PRE File " + p_fileName + " could not be stored at location :" + p_failedFileLocation);
                    }
                } else {
                    _log.info("moveFailedTxnFile", "Location p_failedFileLocation:" + p_failedFileLocation + " does not exist and creation also failed ");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "FTPUtility[moveFailedTxnFile]", "", "", "", "Location p_failedFileLocation:" + p_failedFileLocation + " does not exist and creation also failed." + "failed txn POST2PRE File " + p_fileName + " could not be stored at location :" + p_failedFileLocation);
                }
            } else {
                isFileRenamed = new File(p_orgFileLoaction + "/" + p_fileName).renameTo(new File(p_failedFileLocation + "/" + p_fileName));
                if (isFileRenamed) {
                    _log.info("moveFailedTxnFile", "successful txn POST2PRE File " + p_fileName + " is successfully stored at location :" + p_failedFileLocation);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "FTPUtility[moveFailedTxnFile]", "", "", "", "failed txn POST2PRE File " + p_fileName + " is successfully stored at location :" + p_failedFileLocation);
                } else {
                    _log.info("moveFailedTxnFile", "failed txn POST2PRE File " + p_fileName + " is could not be stored at location :" + p_failedFileLocation);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "FTPUtility[moveFailedTxnFile]", "", "", "", "failed txn POST2PRE File " + p_fileName + " is could not be stored at location :" + p_failedFileLocation);
                }
            }
        } catch (Exception ex) {
            _log.errorTrace("Exception in moveFailedTxnFile() ", ex);
            _log.error("moveSuccessTxnFile", "In case of failed trnasaction While moving file " + p_fileName + " to location " + p_failedFileLocation + " got Exception ex:" + ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "FTPUtility[moveSuccessTxnFile]", "", "", "", "In case of failed trnasaction While moving file " + p_fileName + " to location " + p_failedFileLocation + " got Exception ex:" + ex.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("moveFailedTxnFile", "Exited");
        }
    }
}
