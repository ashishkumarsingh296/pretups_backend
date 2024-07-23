package com.btsl.filetransfer;

/**
 * @author divyakant.verma
 *         Created on Nov 27, 2006
 */
public interface FileTransfer {

    /**
     * Perform files upload
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
     * @throws Exception
     */
    public int doUpload(String hostname, String username, String password, String fileName, String localServerPath, String ftpServerPath) throws Exception;

    /**
     * Perform file download
     * 
     * @param hostname
     *            String
     * @param username
     *            String
     * @param password
     *            String
     * @param filter
     *            String (like *.txt)
     * @throws Exception
     */
    public int doDownload(String hostname, String username, String password, String filter, String localServerPath, String ftpServerPath) throws Exception;

} // end interface
