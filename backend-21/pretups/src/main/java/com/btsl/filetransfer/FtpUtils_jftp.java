package com.btsl.filetransfer;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

import net.sf.jftp.net.BasicConnection;
import net.sf.jftp.net.ConnectionHandler;
import net.sf.jftp.net.ConnectionListener;
import net.sf.jftp.net.FtpConnection;

/**
 * See FtpDownload.java for comments.
 */
public class FtpUtils_jftp implements ConnectionListener, FileTransfer {

    private boolean isThere = false;
    private String hostname;
    private String username;
    private String password;
    private int FILEUPLOADED = -1;
    private int FILEDOWNLOADED = -1;
    private static Log _logger = LogFactory.getLog(FtpUtils_jftp.class.getName());

    private ConnectionHandler handler = new ConnectionHandler();

    /**
     * Default constructor
     */
    public FtpUtils_jftp() {
        super();
    }

    /**
     * Parameterized constructor
     * 
     * @param hostname
     *            String
     * @param username
     *            String
     * @param password
     *            String
     */
    public FtpUtils_jftp(String hostname, String username, String password) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
    }

    /**
     * Upload files
     * 
     * @param hostname
     *            String
     * @param username
     *            String
     * @param password
     *            String
     * @param fileName
     *            String
     * @param ftpServerPath
     *            String
     * @throws BTSLBaseException 
     */
    public int doUpload(String hostname, String username, String password, String fileName, String localServerPath, String ftpServerPath) throws BTSLBaseException {
        if (_logger.isDebugEnabled())
            _logger.debug("doUpload()", " Entered.");
        FtpConnection con = new FtpConnection(hostname);
        try {
            con.addConnectionListener(this);
            con.setLocalPath(localServerPath);
            con.setConnectionHandler(handler);
            int status = con.login(username, password); // Returns:
                                                        // WRONG_LOGIN_DATA
                                                        // (=-6), OFFLINE (=-7),
                                                        // GENERIC_FAILED (=-8)
                                                        // or LOGIN_OK (=2)
                                                        // status code

            if (status != 2) {
                if (status == -6) {
                    throw new BTSLBaseException("Invalid username or password.");
                } else if (status == -7) {
                    throw new BTSLBaseException("Server closed Connection, maybe too many users.");
                } else if (status == -8) {
                    throw new BTSLBaseException("FTP not allow.");
                } else {
                    throw new BTSLBaseException("Unable to login.");
                }
            }
            con.chdir(ftpServerPath);
            FILEUPLOADED = con.upload(fileName); // Status : 1 if success, -1 in
                                                 // failed
        } finally {
            con.disconnect();
            if (_logger.isDebugEnabled())
                _logger.debug("doUpload() Exited. FILEUPLOADED = ", FILEUPLOADED);
        }
        return FILEUPLOADED;
    }

    /**
     * Perform multiple file download
     * 
     * @param hostname
     *            String
     * @param username
     *            String
     * @param password
     *            String
     * @param filter
     *            String (like file name)
     * @throws BTSLBaseException 
     */
    public int doDownload(String hostname, String username, String password, String filter, String localServerPath, String ftpServerPath) throws BTSLBaseException {
        if (_logger.isDebugEnabled())
            _logger.debug("doDownload()", "Entered.");
        FtpConnection con = new FtpConnection(hostname);
        try {
            con.addConnectionListener(this);
            con.setLocalPath(localServerPath);
            con.setConnectionHandler(handler);
            int status = con.login(username, password); // Returns:
                                                        // WRONG_LOGIN_DATA
                                                        // (=-6), OFFLINE (=-7),
                                                        // GENERIC_FAILED (=-8)
                                                        // or LOGIN_OK (=2)
                                                        // status code

            if (status != 2) {
                if (status == -6) {
                    throw new BTSLBaseException("Invalid username or password.");
                } else if (status == -7) {
                    throw new BTSLBaseException("Server closed Connection, maybe too many users...");
                } else if (status == -8) {
                    throw new BTSLBaseException("FTP not available!");
                } else {
                    throw new BTSLBaseException("Unable to FTP");
                }
            }
            con.chdir(ftpServerPath);
            FILEDOWNLOADED = con.download(filter); // Status : 1 if success, -1
                                                   // in failed
        } finally {
            con.disconnect();
            if (_logger.isDebugEnabled())
                _logger.debug("doDownload() Exited. FILEDOWNLOADED = ", FILEDOWNLOADED);
        }
        return FILEDOWNLOADED;
    }

    public void updateRemoteDirectory(BasicConnection con) {
        // _logger.info("new path is: " + con.getPWD());
    }

    public void connectionInitialized(BasicConnection con) {
        isThere = true;
    }

    public void updateProgress(String file, String type, long bytes) {
    }

    public void connectionFailed(BasicConnection con, String why) {
        // _logger.info("connection failed!");
    }

    public void actionFinished(BasicConnection con) {
        // _logger.info("Action Finished!");
    }

    public void debug(String msg) {
        _logger.info("debug", msg);
    }

    public void debugRaw(String msg) {
        _logger.info("debugRaw", msg);
    }

    public void debug(String msg, Throwable throwable) {
    }

    public void warn(String msg) {
    }

    public void warn(String msg, Throwable throwable) {
    }

    public void error(String msg) {
    }

    public void error(String msg, Throwable throwable) {
    }

    public void info(String msg) {
    }

    public void info(String msg, Throwable throwable) {
    }

    public void fatal(String msg) {
    }

    public void fatal(String msg, Throwable throwable) {
    }

    /**
     * main method()
     * 
     * @param arg
     */
    /*
     * public static void main(String arg[]) {
     * try {
     * //new
     * FtpUtils_jftp().doUpload("","","","PRE_PAID_CARD_20061204_63521.DAT",
     * "C:/", "/home/bcl/Divyakant/");
     * new FtpUtils_jftp().doDownload("172.16.7.172","oracle","oracle123",
     * "master_111229_12.csv", "C:\\", "/datadisk1/oracle/c2sdwh");
     * 
     * } catch (Exception exception) {
     * if (_logger.isDebugEnabled())
     * _logger.debug("@Error is ",exception.getMessage());
     * }
     * }
     */

}