package com.inter.postonline;

/**
 * @FtpClient.java
 *                 Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *                 All Rights Reserved
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Author Date History
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Ashish K Apr 13, 2007 Initial Creation
 *                 ------------------------------------------------------------
 *                 ------------------------------------
 *                 This class will provide implementation details of for various
 *                 API that are to be handled by Stored Procedures
 * 
 */
import java.io.IOException;

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
import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPConnectMode;
import com.inter.pool.ClientMarkerI;

/**
 * This class has methods used to communicate with an FTP Server Process
 */
public class FtpClient implements FTP, ClientMarkerI {
    private Log _log = LogFactory.getLog(FtpClient.class.getName());
    String hostIP;
    String userNameQ;
    String passWD;
    String currentWorkingDirectory;
    boolean busy = false;
    FTPClient _ftpClient = null;
    long acquiredTime = 0;
    private String _rootDirectory = null;

    /**
     * Default Constructor
     */
    public FtpClient() {
        this.hostIP = IP_ADDRESS;
        try {
            /*
             * _ftpClient = new FTPClient(hostIP);
             * _ftpClient.setTimeout(300000);
             * _ftpClient.setConnectMode(FTPConnectMode.ACTIVE);
             */} catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to connect the FTP machine
     * 
     * @throws BTSLBaseException
     */
    private void connectFtpClient(String p_ipAddress, int p_ftpTimeOut) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("connectFtpClient", "Entered");
        try {
            _ftpClient = new FTPClient(p_ipAddress);
            _ftpClient.setTimeout(p_ftpTimeOut);
            _ftpClient.setConnectMode(FTPConnectMode.ACTIVE);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("connectFtpClient", "Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FTP_CONNECT_FAIL);
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
     */
    public FtpClient(String p_interfaceID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("FtpClient[constructor]", "Entered p_interfaceID:" + p_interfaceID);
        try {
            String ipAddress = FileCache.getValue(p_interfaceID, "FTP_IP");
            if (InterfaceUtil.isNullString(ipAddress)) {
                _log.error("FtpClient[constructor]", "FTP_IP is not defined in the INFile with INTERFACE_ID:" + p_interfaceID);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "FtpClient[constructor]", "", "INTERFACE_ID:" + p_interfaceID, "", "FTP_IP is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            ipAddress = ipAddress.trim();
            String portStr = FileCache.getValue(p_interfaceID, "FTP_PORT");
            if (InterfaceUtil.isNullString(portStr)) {
                _log.error("FtpClient[constructor]", "FTP_PORT is not defined in the INFile with INTERFACE_ID:" + p_interfaceID);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "FtpClient[constructor]", "", "INTERFACE_ID:" + p_interfaceID, "", "FTP_PORT is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            int port = Integer.parseInt(portStr.trim());
            String ftpTimeOutStr = FileCache.getValue(p_interfaceID, "FTP_TIME_OUT");
            if (InterfaceUtil.isNullString(ftpTimeOutStr)) {
                _log.error("FtpClient[constructor]", "FTP_TIME_OUT is not defined in the INFile with INTERFACE_ID:" + p_interfaceID);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "FtpClient[constructor]", "", "INTERFACE_ID:" + p_interfaceID, "", "FTP_TIME_OUT is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            int ftpTimeOut = Integer.parseInt(ftpTimeOutStr.trim());
            String userName = FileCache.getValue(p_interfaceID, "FTP_USER_NAME");
            if (InterfaceUtil.isNullString(userName)) {
                _log.error("FtpClient[constructor]", "FTP_USER_NAME is not defined in the INFile with INTERFACE_ID:" + p_interfaceID);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "FtpClient[constructor]", "", "INTERFACE_ID:" + p_interfaceID, "", "FTP_USER_NAME is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            userName = userName.trim();
            String password = FileCache.getValue(p_interfaceID, "FTP_PASSWD");
            if (InterfaceUtil.isNullString(password)) {
                _log.error("FtpClient[constructor]", "FTP_PASSWD is not defined in the INFile with INTERFACE_ID:" + p_interfaceID);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "FtpClient[constructor]", "", "INTERFACE_ID:" + p_interfaceID, "", "FTP_PASSWD is not defined in the INFile");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
            if (_log.isDebugEnabled())
                _log.debug("FtpClient[constructor]", "ipAddress :" + ipAddress + " port :" + port + " userName :" + userName + " password :" + password);
            password = password.trim();
            // Connect to the remote machine via FTPClient
            connectFtpClient(ipAddress, ftpTimeOut);
            // Login to the FTP Server
            login(userName, password);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("FtpClient[constructor]", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "FtpClient[constructor]", "", "INTERFACE_ID:" + p_interfaceID, "", "While initializing the Clinet object get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("FtpClient[constructor]", "Exited");
        }
    }

    /**
     * This method is used to open an FTP Session with remote FTP Server
     * 
     * @param None
     * @returen None
     */
    public void login(String p_userName, String p_password) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("login", "Entered p_userName:" + p_userName + " p_password:" + p_password);
        try {
            _ftpClient.login(p_userName, p_password);
            _rootDirectory = _ftpClient.pwd();
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("login", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "FtpClient[login]", "", "INTERFACE_ID:", "", "While Login to the FTP server get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_FTP_LOGIN_FAILED);// Confirm
                                                                                     // for
                                                                                     // new
                                                                                     // InterfaceErrorCode.
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("login", "Exited");
        }
    }

    /**
     * This method changes the current working directory on the remote FTP
     * Server
     * 
     * @param dirName
     *            The directory to be changed to from the current working
     *            directory
     * @return none
     */
    public void changeDirectoryTo(String dirName) throws Exception {
        currentWorkingDirectory = dirName;
        _ftpClient.chdir(currentWorkingDirectory);
    }

    /**
     * This method is used to change the data transfer mode to Ascii
     * 
     * @param none
     * @return none
     */
    public void toAscii() throws Exception {
        // ftpClient.ascii();
    }

    /**
     * This method is used to change the data transfer mode to Binary
     * 
     * @param none
     * @return none
     */
    public void toBinary() throws Exception {
        // ftpClient.binary();
    }

    /**
     * This method is used to close the current session to remote FTP Server and
     * logout
     * 
     * @param none
     * @return none
     */
    public void logout() {
        try {
            _ftpClient.quit();
            _ftpClient.clearSOCKS();
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("logout", "Exception e:" + e.getMessage());
        }
    }

    /**
     * This method is used to get all the files in the current Directory
     * 
     * @param none
     * @return The listing of the current working directory
     */
    public String[] getDir(String fileNameStr) throws IOException, com.enterprisedt.net.ftp.FTPException {
        String arr[] = _ftpClient.dir(fileNameStr);
        /*
         * for (int j=0;j < arr.length;j++)
         * {
         * System.out.println("FILE NAME---->" + arr[j] + "Array size is " +
         * arr.length);
         * }
         */return arr;
    }

    /**
     * This method is used to read and return the contents of the remote FTP
     * Server
     * 
     * @param fileName
     *            The remote file name, contents of which are to be read
     * @param localFile
     *            The file name of the localFile, storing contents of remote
     *            file
     * @return The contents of the read file
     */
    public void readFromFile(String localFile, String fileName) throws Exception {
        _ftpClient.get(localFile, fileName);
    }

    /**
     * This method is used to upload a given local file to the remote FTP Server
     * 
     * @param p_localFileName
     *            The local file to be uploaded
     * @param p_remoteFileName
     *            The Name of the local file on the remote machine after the
     *            update
     */
    public void uploadFileToServer(String p_localFileName, String p_remoteFileName) throws Exception {
        _ftpClient.put(p_localFileName, p_remoteFileName);
    }

    /**
     * @return
     */
    public boolean isBusy() {
        return busy;
    }

    /**
     * @param b
     */
    public void setBusy(boolean b) {
        busy = b;
    }

    /**
     * @return
     */
    public long getAcquiredTime() {
        return acquiredTime;
    }

    /**
     * @param l
     */
    public void setAcquiredTime(long l) {
        acquiredTime = l;
    }

    public void chmod(String p_mode, String p_file) throws Exception {
        _ftpClient.chmod(p_mode, p_file);
    }

    /*
     * public static void main(String[] arg)
     * {
     * FtpClient ftpClient=null;
     * FileOutputStream fo=null;
     * boolean isUploaded=false;
     * try
     * {
     * Date date= new Date();
     * String time=String.valueOf(date.getTime());
     * String localFile="C:/ProcessLogConfig.txt";
     * String testString="Latest Testing file 5";
     * System.out.println("Connecting...");
     * ftpClient=new FtpClient();
     * ftpClient.connectFtpClient("172.16.1.109",0);
     * //Thread.currentThread().sleep(10000);
     * System.out.println("Connected");
     * ftpClient.login("oracle","oracle123");
     * System.out.println("SUCCESSFUL logged in");
     * //ftpClient.getDir("oraInventory");
     * ftpClient.getDir("testdir");
     * String fileName="C:/ABC"+time+".txt";
     * //if()
     * fo = new FileOutputStream(new File(fileName));
     * fo.write(testString.getBytes());
     * try{if(fo!=null)fo.close();}catch(Exception e){}
     * 
     * ftpClient.changeDirectoryTo("testdir");
     * System.out.println("Before uploading the File to dir the files are "+
     * ftpClient.getDir("."));
     * System.out.println("Directory has been changed uploading the File...");
     * //Thread.sleep(5000);
     * ftpClient.uploadFileToServer(fileName,"ABC"+time+".txt");
     * System.out.println("File has been uploaded to server");
     * String[] dir=ftpClient.getDir(".");
     * 
     * System.out.println("length of dir: "+dir.length);
     * for(int i=0,size=dir.length;i<size;i++)
     * {
     * System.out.println("files present in the dir "+dir[i]);
     * if(dir[i].equals("ABC"+time+".txt"))
     * {
     * isUploaded=true;
     * break;
     * }
     * }
     * if(isUploaded)
     * System.out.println("File "+fileName+" is uploaded successfully");
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * System.out.println(e.getMessage());
     * }
     * finally
     * {
     * try{if(fo!=null)fo.close();}catch(Exception e){}
     * }
     * }
     */
    /**
     * Main Program
     */
    /*
     * public static void main(String args[])
     * {
     * 
     * FtpClient ftpcli = null;
     * Utility u = new Utility();
     * 
     * try
     * {
     * //ftpcli.login( "btsoft", "btsoft123");
     * for(int i = 1;i <= 1;i++)
     * {
     * ftpcli = new FtpClient("190.0.8.5");
     * ftpcli.login( "btsoft", "btsoft123");
     * 
     * String infile = "test" + i;
     * ftpcli.uploadFileToServer("/data2/IN_INPUT_FILES/test1", infile);
     * u.runCommand("rsh -l btsoft 190.0.8.5 ncopy /home1/smsftac/btsoft/" +
     * infile + " x3smp3\\!/INPUT/" + infile + " btsoftadmission");
     * String outfile = "test" + i + ".out";
     * String errfile = "test" + i + ".error";
     * 
     * ftpcli.changeDirectoryTo("/home1/smsftac/btsoft/OUTPUT");
     * 
     * //System.out.println("Processing request no " + (i+1));
     * 
     * int catchme = 0;
     * 
     * while(catchme == 0)
     * {
     * try
     * {
     * //System.out.println("READ FILE :::"+"/data2/IN_OUTPUT_FILES/" +
     * errfile);
     * String a[] = ftpcli.getDir(errfile);
     * Thread.sleep(700);
     * 
     * if (errfile.startsWith(a[0]))
     * {
     * ftpcli.readFromFile("/data2/IN_OUTPUT_FILES/" + errfile, errfile);
     * }
     * 
     * catchme = 1;
     * }
     * catch(SocketException se)
     * {
     * catchme = 0;
     * try
     * {
     * //System.out.println("Closing the connection");
     * ftpcli.logout();
     * }
     * catch (Exception ex)
     * {
     * //System.out.println("------ Exception -------");
     * }
     * finally
     * {
     * //System.out.println("Re logging in");
     * ftpcli = new FtpClient("190.0.8.5");
     * ftpcli.login( "btsoft", "btsoft123");
     * ftpcli.changeDirectoryTo("/home1/smsftac/btsoft/OUTPUT");
     * }
     * }
     * catch(Exception e)
     * {
     * catchme = 0;
     * //e.printStackTrace();
     * }
     * }
     * 
     * ftpcli.readFromFile("/data2/IN_OUTPUT_FILES/" + outfile, outfile);
     * ftpcli.logout();
     * 
     * //System.out.println("Processed Request no " + (i + 1));
     * 
     * }
     * 
     * }
     * catch(Exception e)
     * {
     * //e.printStackTrace();
     * }
     * finally
     * {
     * //System.out.println("Finally :: Error logging in FTP Server");
     * try
     * {
     * ftpcli.logout();
     * }
     * catch(Exception e)
     * {
     * //e.printStackTrace();
     * }
     * }
     * }
     */

    /**
     * Destroy the FTP connection with remote machiene through FTPClient.
     */
    public void destroy() {
        try {
            logout();
            _ftpClient = null;
        } catch (Exception e) {
        }
    }

    public String getPWD() {
        String presentWorkingDirectory = null;
        try {
            presentWorkingDirectory = _ftpClient.pwd();
        } catch (Exception e) {
        }
        return presentWorkingDirectory;
    }

    public String getFirstDir() {
        return _rootDirectory;
    }
}
