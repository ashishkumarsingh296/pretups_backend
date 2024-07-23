package com.btsl.pretups.routing.subscribermgmt.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.interfaces.businesslogic.InterfaceDAO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.pretups.logging.RoutingFileProcessLog;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.RoutingUploadDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

/**
 * @(#)RoutingUpload.java
 *                        Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *                        All Rights Reserved
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Siddhartha Srivastava March 8th,2007 Initial Creation
 *                        ------------------------------------------------------
 *                        ------------------------------------------
 *                        This class is responsible for reading files from the
 *                        specified directory and uploading the
 *                        numbers which are inside the files into the table.
 *                        Besides uploading of the MSISDN, this class also
 *                        performs neccessary checks on the header of the files
 *                        etc.
 * 
 */

public class RoutingUpload {
    // these are the fixed label present in the files always and need to be
    // validated for each file uploaded

    private static final String INTFACE_CAT = "INTERFACE_CATEGORY";
    private static final String INTFACE_ID = "INTERFACE_ID";
    private static final String EXT_ID = "EXTERNAL_ID";
    private static final String NO_OF_REC = "NUM_OF_RECORDS";
    private static final String NTW_CODE = "NETWORK_CODE";
    // header labels end

    private String _filePath; // File path name
    private ArrayList _fileList; // Contain all the file object thats name start
                                 // with file prefix.
    private BufferedReader _in; // BufferedReader object that is created for
                                // each file.
    private String _headerStart; // Define whether the file has optional header
                                 // or not.
    private String _headerEnd; // Define whether the file has optional header or
                               // not.
    private File _inputFile; // File object for input file
    private File _destFile; // Destination File object
    private String _moveLocation; // Input file is move to this location After
                                  // successfull proccessing
    private String _fileExt; // Files are picked up only this extention from the
                             // specified directory
    private final String _allowedExt = "txt";// Input file should be either txt
                                             // or
    // csv.
    private int _errorCount;// If invalid records equal or greater than this
                            // percentage that file is not proccessed
    private String _interfaceID;
    private String _networkCode;
    private String _interfaceCategory;
    private String _externalID;
    private String _numberOfRecords;
    private HashMap _headerMap;
    private RoutingUploadDAO _routingUploadDAO = null;

    private static Log _log = LogFactory.getLog(RoutingUpload.class.getName());

    public RoutingUpload() {
    }

    /**
     * This method is the method from which actual operation related to the data
     * validation, inertion, file movement
     * are performed in the specific order.
     * 
     * @throws BTSLBaseException
     */
    private void process() throws BTSLBaseException {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", "Entered");
        }

        Connection con = null;
        ProcessStatusVO processStatusVO = null;
        ProcessBL processBL = null;
        boolean processStatusOK = false;
        int successCount = 0;

        try {
            if (_log.isDebugEnabled()) {
                _log.debug("process", "START::TOTAL MEMORY --->" + Runtime.getRuntime().totalMemory() + " START::FREE MEMORY --->" + Runtime.getRuntime().freeMemory());
            }

            // Create connection
            con = OracleUtil.getSingleConnection();

            // check the connection for null if it is null stop with showing
            // error message.
            if (con == null) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ROUTING_ERROR_CONN_NULL);
            }

            processBL = new ProcessBL();
            // check the process status by calling checkProcessUnderprocess
            // method of processBL,if its ok continue else stop the process with
            // error message.
            processStatusVO = processBL.checkProcessUnderProcess(con, ProcessI.ROUTING_UPLOAD_PROCESSID);

            if (!(processStatusVO != null && processStatusVO.isStatusOkBool())) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
            }
            processStatusOK = processStatusVO.isStatusOkBool();
            // Commiting the status of process status as 'U-Under Process'.
            con.commit();

            // Load constant values from the Constant.props file which are
            // defined for the process
            loadConstantValues();

            // Load All the files from the specified directory path.
            loadFilesFromDir();

            // processing each file present at the location specified for file
            // upload and stored in the _fileList
            for (int l = 0, size = _fileList.size(); l < size; l++) {
                // Getting the file object
                _inputFile = (File) _fileList.get(l);
                try {
                    // Files are stored in memory,before processing check
                    // whether it is exist or not.
                    if (!_inputFile.exists()) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("process", "File = " + _inputFile + "does not exist");
                        }
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ROUTING_ERROR_FILE_DOES_NOT_EXIST);
                    }
                    // setting the buffer reader for the file.
                    setBufferReader();

                    // proccessing the file headers.
                    _headerMap = new HashMap();
                    proccessHeader();

                    _routingUploadDAO = new RoutingUploadDAO();
                    validateHeadersValues(con);

                    if (_log.isDebugEnabled()) {
                        _log.debug("process", "File = " + _inputFile + " is picked up for proccessing");
                    }

                    // proccess the data part of the file.
                    // Initialize the successCount and fileProcessStatus for
                    // each file.
                    StringBuffer invalidMSISDN = new StringBuffer();

                    ArrayList validRecordData = readValidateData(invalidMSISDN);
                    successCount = _routingUploadDAO.insertData(con, _headerMap, validRecordData, _inputFile.getName());
                    if (successCount >= 0) {
                        if (_in != null) {
                            _in.close();
                        }
                        // Moving File after Processing
                        Date date = new Date();
                        String fileName = _inputFile.getName();
                        String moveFileName = fileName.substring(0, fileName.indexOf(".")) + "_" + date.getDate() + "_" + date.getHours() + "_" + date.getMinutes() + "." + fileName.substring(fileName.indexOf(".") + 1);
                        boolean fileMoved = moveFile(_inputFile.getPath(), moveFileName);
                        if (!fileMoved) {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ROUTING_FILE_MOVE_ERROR);
                        }
                        // Commiting the transaction after successful
                        // proccessing of file data.
                        con.commit();
                    } else {
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ROUTING_ERROR_DATA_UPDATE);
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug("process", "File = " + _inputFile + " is proccessed");
                    }
                }// end of inner try-block
                catch (BTSLBaseException be) {
                    if (con != null) {
                        try {
                            con.rollback();
                        } catch (Exception e1) {
                            _log.errorTrace(METHOD_NAME, e1);
                        }
                    }
                    _log.error("process", "BTSLBaseException be = " + be.getMessage());
                    _log.errorTrace(METHOD_NAME, be);
                }// end of catch-BTSLBaseException
                catch (Exception e) {
                    _log.error("process", "Exception e = " + e.getMessage());
                    if (con != null) {
                        try {
                            con.rollback();
                        } catch (Exception e1) {
                            _log.errorTrace(METHOD_NAME, e1);
                        }
                    }
                    _log.error("process", "Exception e = " + e.getMessage());
                    _log.errorTrace(METHOD_NAME, e);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "RoutingUpload[process]", "[File Name = " + _inputFile + " ] [File Process Status is Unsuccessfull]", "", "", "Exception:" + e.getMessage());
                } finally {
                    try {
                        if (_in != null) {
                            _in.close();
                        }
                    } catch (Exception e1) {
                        _log.errorTrace(METHOD_NAME, e1);
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug("process", "File = " + _inputFile + " is proccessed");
                    }
                }// end of inner finally
            }// end of for loop
        }// end of try-block
        catch (BTSLBaseException be) {
            _log.error("process", "BTSLBaseException be= " + be.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception e1) {
                    _log.errorTrace(METHOD_NAME, e1);
                }
            }
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.error("process", "Exception be= " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingUpload[process]", "processStatusVO.getProcessID()" + ProcessI.ROUTING_UPLOAD_PROCESSID, "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "process", e.getMessage());
        }// end of catch-Exception
        finally {
            try {
                // Setting the process status as 'C-Complete' if the
                // processStatusOK is true
                if (processStatusOK) {
                    Date date = new Date();
                    processStatusVO.setStartDate(processStatusVO.getStartDate());
                    processStatusVO.setExecutedOn(date);
                    processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
                    int successU = processStatusDAO.updateProcessDetail(con, processStatusVO);

                    // Commiting the process status as 'C-Complete'
                    if (successU > 0) {
                        con.commit();
                    } else {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingUpload[process]", "processStatusVO.getProcessID()" + ProcessI.ROUTING_UPLOAD_PROCESSID, "", "", "Error while updating the process status after completing the process");
                        }
                }// end of IF-Checks the proccess status
            }// end of try-block
            catch (BTSLBaseException be) {
                _log.errorTrace(METHOD_NAME, be);
                _log.error("process", "BTSLBaseException be= " + be.getMessage());
                if (con != null) {
                    try {
                        con.rollback();
                    } catch (Exception e1) {
                        _log.errorTrace(METHOD_NAME, e1);
                    }
               
                }
            }// end of catch-BTSLBaseException
            catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error("process", "Exception e= " + e.getMessage());
                if (con != null) {
                    try {
                        con.rollback();
                    } catch (Exception e1) {
                        _log.errorTrace(METHOD_NAME, e1);
                    }
                }
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingUpload[process]", "processStatusVO.getProcessID()" + ProcessI.ROUTING_UPLOAD_PROCESSID, "", "", "BaseException:" + e.getMessage());

            }// end of catch-Exception
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e1) {
                    _log.errorTrace(METHOD_NAME, e1);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Exiting ");
            }
        }// end of finally
    }// end of proccess

    /**
     * This method is used to load the Constant values from the Constants file.
     * 
     * @throws BTSLBaseException
     */
    private void loadConstantValues() throws BTSLBaseException {
        final String METHOD_NAME = "loadConstantValues";
        if (_log.isDebugEnabled()) {
            _log.debug("loadConstantValues", "Entered");
        }
        try {
            // getting the value of the START tag for the data
            if (BTSLUtil.isNullString(Constants.getProperty("ROUTING_HEADER_START"))) {
                _log.error("loadConstantValues ", "Configuration Problem, Parameter ROUTING_HEADER_START not defined.");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "RoutingUpload[loadConstantValues]", "", "", "", "Configuration Problem, Parameter ROUTING_HEADER_START not defined.");
                throw new BTSLBaseException("RoutingUpload", "loadConstantValues", PretupsErrorCodesI.ROUTING_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            _headerStart = Constants.getProperty("ROUTING_HEADER_START").trim();

            // getting the value of END tag for the file
            if (BTSLUtil.isNullString(Constants.getProperty("ROUTING_HEADER_END"))) {
                _log.error("loadConstantValues ", "Configuration Problem, Parameter ROUTING_HEADER_END not defined.");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "RoutingUpload[loadConstantValues]", "", "", "", "Configuration Problem, Parameter ROUTING_HEADER_END not defined.");
                throw new BTSLBaseException("RoutingUpload", "loadConstantValues", PretupsErrorCodesI.ROUTING_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            _headerEnd = Constants.getProperty("ROUTING_HEADER_END").trim();

            // path where the input file will be placed for reading.
            _filePath = BTSLUtil.NullToString(Constants.getProperty("ROUTING_FILE_PATH")).trim();

            // Checking whether the file path is provided and if yes, whether it
            // exists or not.
            if (!(new File(_filePath).exists())) {
                _log.error("loadConstantValues", "Directory Path = " + _filePath + " does not exist");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "RoutingUpload[loadConstantValues]", "", "", "", "Configuration Problem, the location for where the file will be read is not correct");
                throw new BTSLBaseException(this, "loadConstantValues", PretupsErrorCodesI.ROUTING_ERROR_DIR_NOT_EXIST);
            }

            // Checking whether the file extention is provided or not and if
            // provided check that if it allowed or not.
            // only text files are allowed for now.
            _fileExt = Constants.getProperty("ROUTING_FILE_EXT");
            if (BTSLUtil.isNullString(_fileExt) || !(_allowedExt.contains(_fileExt))) {
                _log.error("loadConstantValues", "File Extension = " + _fileExt + " not defined correctly");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "RoutingUpload[loadConstantValues]", "", "", "", "Configuration Problem, Parameter ROUTING_FILE_EXT not defined correctly");
                throw new BTSLBaseException(this, "loadConstantValues", PretupsErrorCodesI.ROUTING_ERROR_FILE_EXT_NOT_DEFINED);
            }
            _fileExt = _fileExt.trim();
            // Destination location for the file where it will be moved after
            // reading is complete and the data in it is uploaded
            _moveLocation = BTSLUtil.NullToString(Constants.getProperty("ROUTING_MOVE_LOCATION")).trim();
            _destFile = new File(_moveLocation);

            // Checking the destination location whether it exist or not,if not
            // create the directory.
            if (!_destFile.exists()) {
                if (_log.isDebugEnabled()) {
                    _log.debug("loadConstantValues", "Destination Location = " + _moveLocation + " does not exist");
                }
                boolean fileCreation = _destFile.mkdirs();
                if (fileCreation) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("loadConstantValues", "New Location = " + _destFile + "has been created successfully");
                    }
                }
            }
            try {
                // Check if the invalid record count exist and should be
                // numeric.
                String errorCount = Constants.getProperty("ROUTING_ERROR_COUNT");
                if (BTSLUtil.isNullString(errorCount)) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("loadConstantValues", "Error count is not specified");
                    }
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "RoutingUpload[loadConstantValues]", "", "", "", "Configuration Problem, Parameter ROUTING_ERROR_COUNT not defined.");
                    throw new BTSLBaseException(this, "loadConstantValues", PretupsErrorCodesI.ROUTING_ERROR_COUNT_NOT_FOUND);
                }
                _errorCount = Integer.parseInt(errorCount.trim());
            } catch (Exception e) {
                if (_log.isDebugEnabled()) {
                    _log.debug("loadConstantValues", "Error count is not numeric");
                }
                _log.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException(this, "loadConstantValues", PretupsErrorCodesI.ROUTING_ERROR_COUNT_NOT_NUMERIC);
            }
        }// end of try block
        catch (BTSLBaseException be) {
            _log.error("loadConstantValues", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("loadConstantValues", "Exception e= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingUpload[loadConstantValues]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadConstantValues", e.getMessage());
        }// end of catch-Exception
        if (_log.isDebugEnabled()) {
            _log.debug("loadConstantValues", "Exiting: _FILE_PATH = " + _filePath + " _fileExt= " + _fileExt + "_moveLocation= " + _moveLocation + " _headerStart = " + _headerStart + " _headerEnd = " + _headerEnd);
        }
    }// end of loadConstantValues

    /**
     * This method is used to load all the files with specified file extension
     * from the file location
     * specified in the Constant.props.
     * All these file objects are stored in ArrayList.
     * 
     * @throws BTSLBaseException
     */
    private void loadFilesFromDir() throws BTSLBaseException {
        final String METHOD_NAME = "loadFilesFromDir";
        if (_log.isDebugEnabled()) {
            _log.debug("loadFilesFromDir", "Entered");
        }
        File directory = null;
        try {
            directory = new File(_filePath);
            // Check if the directory contains any files
            if (directory.list() == null) {
                _log.error("loadFilesFromDir ", "No file exists at the location specified from where the file will be uploaded");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "RoutingUpload[loadFilesFromDir]", "", "", "", "No file exists at the location of file upload");
                throw new BTSLBaseException(this, "loadFileFromDir", PretupsErrorCodesI.ROUTING_ERROR_DIR_CONTAINS_NO_FILES);
            }

            // This filter is used to filter all the files that ends with
            // _fileExt;
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return (name.endsWith(_fileExt));
                }
            };

            // List of files that ends with the specified extension.
            File[] tempFileArray = directory.listFiles(filter);
            _fileList = new ArrayList();

            // Storing all the files(not dir)to array list for uploading
            for (int l = 0, size = tempFileArray.length; l < size; l++) {
                if (tempFileArray[l].isFile()) {
                    _fileList.add(tempFileArray[l]);
                    if (_log.isDebugEnabled()) {
                        _log.debug("loadFileFromDir", "File = " + tempFileArray[l] + " is added to fileList");
                    }
                }
            }// end of for loop

            // Check whether the directory contains any file ending with the
            // specified file extension.
            if (_fileList.isEmpty()) {
                throw new BTSLBaseException(this, "loadFilesFromDir", PretupsErrorCodesI.ROUTING_ERROR_DIR_CONTAINS_NO_FILES);
            }
        } catch (BTSLBaseException be) {
            _log.error("loadFilesFromDir", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("loadFilesFromDir", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "RoutingUpload[loadFilesFromDir]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadFilesFromDir", e.getMessage());
        }// end of catch-Exception
        if (_log.isDebugEnabled()) {
            _log.debug("loadFilesFromDir", "Exited");
        }
    }// end of loadFilesFromDir

    /**
     * This method is used to set the Buffered Reader object for the file.
     * 
     * @throws BTSLBaseException
     */
    private void setBufferReader() throws BTSLBaseException {
        final String METHOD_NAME = "setBufferReader";
        if (_log.isDebugEnabled()) {
            _log.debug("setBufferReader", "Entered");
        }
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("setBufferReader", "_inputFile = " + _inputFile);
            }
            _in = new BufferedReader(new FileReader(_inputFile));
        }// end of try block
        catch (IOException ioe) {
            _log.error("setBufferReader", "IOException ioe = " + ioe.getMessage());
            _log.errorTrace(METHOD_NAME, ioe);
            throw new BTSLBaseException(this, "setBufferReader", ioe.getMessage(),ioe);
        }// end of catch-IOException
        if (_log.isDebugEnabled()) {
            _log.debug("setBufferReader", "Exited");
        }
    }// end of setBufferReader

    /**
     * This method is used to proccess the Header information
     * 1.First check whether the header is present or not.
     * 2.Read the file header information.
     * 3.If start is not present then Raise an error in case Required header.
     * 
     * @throws BTSLBaseException
     */
    private void proccessHeader() throws BTSLBaseException {
        final String METHOD_NAME = "proccessHeader";
        if (_log.isDebugEnabled()) {
            _log.debug("proccessHeader", "Entered");
        }

        String str = "";
        int headerCount = 1;
        try {
            while (str != null) {
                str = _in.readLine();
                if (str.trim().equals("")) {
                    continue;// Skip all the blank lines
                }
                if (headerCount == 6) {
                    if (_headerStart != null && !_headerStart.equals(str)) {
                        _log.error("proccessHeader", "Required header start tag = " + _headerStart + " is not found");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingUpload[proccessHeader]", "", "", "", "No START tag in the file.");
                        throw new BTSLBaseException(this, "proccessHeader", PretupsErrorCodesI.ROUTING_ERROR_HEADER_START_TAG_NOT_FOUND);
                    }
                    break;
                }
                try {
                    if (_log.isDebugEnabled()) {
                        _log.debug("proccessHeader", "str = " + str);
                    }
                    extractData(str, headerCount);
                    headerCount++;
                } catch (Exception e) {
                    _log.error("proccessHeader", "Exception e = " + e.getMessage());
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException(this, "proccessHeader", e.getMessage());
                }
            }// end of while
        }// end of try-block
        catch (BTSLBaseException be) {
            _log.error("proccessHeader", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("proccessHeader", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingUpload[proccessHeader]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "proccessHeader", e.getMessage());
        }// end of catch-Exception
        if (_log.isDebugEnabled()) {
            _log.debug("proccessHeader", "Exited");
        }
    }// end of proccessHeader

    /**
     * This method is used to extract data from the line based on the delimiter
     * from the passed line.
     * Then the extracted label is checked against the label value for present
     * at the passed label position.
     * 
     * @param p_fileLine
     * @param p_labelPos
     * @throws BTSLBaseException
     */
    private void extractData(String p_fileLine, int p_labelPos) throws BTSLBaseException {
        final String METHOD_NAME = "extractData";
        if (_log.isDebugEnabled()) {
            _log.debug("extractData", "Entered with p_fileLine:= " + p_fileLine + " p_labelPos " + p_labelPos);
        }
        StringTokenizer lineTkn = null;
        String label = null;
        String lblValue = null;
        try {
            lineTkn = new StringTokenizer(p_fileLine, "=");
            if (lineTkn.countTokens() != 2) {
                throw new BTSLBaseException(this, "extractData", "The format of the file is wrong at line :" + p_fileLine);
            }
            while (lineTkn.hasMoreTokens()) {
                label = lineTkn.nextToken().trim();
                lblValue = lineTkn.nextToken().trim().toUpperCase();
            }
            switch (p_labelPos) {
            case 1:
                if (!INTFACE_CAT.equals(label)) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("extractData", "Interface Category label not found");
                    }
                    throw new BTSLBaseException(this, "extractData", PretupsErrorCodesI.ROUTING_ERROR_INTFACE_CAT_NOT_FOUND);
                }
                break;
            case 2:
                if (!INTFACE_ID.equals(label)) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("extractData", "Interface ID label not found");
                    }
                    throw new BTSLBaseException(this, "extractData", PretupsErrorCodesI.ROUTING_ERROR_INTFACE_ID_NOT_FOUND);
                }
                break;
            case 3:
                if (!EXT_ID.equals(label)) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("extractData", "External ID label not found");
                    }
                    throw new BTSLBaseException(this, "extractData", PretupsErrorCodesI.ROUTING_ERROR_EXT_ID_NOT_FOUND);
                }
                break;
            case 4:
                if (!NO_OF_REC.equals(label)) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("extractData", "Number of Record label not found");
                    }
                    throw new BTSLBaseException(this, "extractData", PretupsErrorCodesI.ROUTING_ERROR_NO_OF_REC_NOT_FOUND);
                }
                break;
            case 5:
                if (!NTW_CODE.equals(label)) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("extractData", "Network Code label not found");
                    }
                    throw new BTSLBaseException(this, "extractData", PretupsErrorCodesI.ROUTING_ERROR_NTW_CODE_NOT_FOUND);
                }
                break;
            }
            _headerMap.put(label, lblValue);

        } catch (BTSLBaseException be) {
            _log.error("extractData", "Exception be = " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("extractData", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingUpload[extractData]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "extractData", e.getMessage());
        }// end of catch-Exception
    }

    /**
     * This method is responsible for validating the values of the headers read
     * from the file
     * 
     * @param p_con
     * @throws BTSLBaseException
     */
    public void validateHeadersValues(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "validateHeadersValues";
        if (_log.isDebugEnabled()) {
            _log.debug("proccessData", "Entered");
        }

        // setting the values in the variable after reading from the file
        _externalID = _headerMap.get(EXT_ID).toString();
        _networkCode = _headerMap.get(NTW_CODE).toString();
        _interfaceID = _headerMap.get(INTFACE_ID).toString();
        _interfaceCategory = _headerMap.get(INTFACE_CAT).toString();
        _numberOfRecords = _headerMap.get(NO_OF_REC).toString();

        try {
            ArrayList interfaceCatList = LookupsCache.loadLookupDropDown(PretupsI.INTERFACE_CATEGORY, true);
            // this call removes the PostPaid entry from the Interface Category
            // List
            ListValueVO listValueVO = null;
            for (int i = 0, j = interfaceCatList.size(); i < j; i++) {
                listValueVO = (ListValueVO) interfaceCatList.get(i);
                if (PretupsI.INTERFACE_CATEGORY_POST.equalsIgnoreCase(listValueVO.getValue())) {
                    interfaceCatList.remove(i);
                    break;
                }
            }
            // checks whether the value of interface category is valid or not
            if (BTSLUtil.isNullString(BTSLUtil.getOptionDesc(_interfaceCategory, interfaceCatList).getLabel())) {
                throw new BTSLBaseException(this, "validateHeadersValues", PretupsErrorCodesI.ROUTING_ERROR_INVALID_INTFACE_CAT);
            }

            // checks whether the network code specified in the file is valid or
            // not
            if (NetworkCache.getObject(_networkCode) == null) {
                throw new BTSLBaseException(this, "validateHeadersValues", PretupsErrorCodesI.ROUTING_ERROR_INVALID_NTW_CODE);
            }

            InterfaceDAO interfaceDAO = new InterfaceDAO();
            HashMap interMap = interfaceDAO.loadInterfaceByID();
     
            boolean mappingFound = false;
            InterfaceVO interfaceVO = null;
            Iterator itr = interMap.keySet().iterator();
            while (itr.hasNext()) {
                interfaceVO = (InterfaceVO) interMap.get(itr.next());
       
                if (_interfaceID.equals(interfaceVO.getInterfaceId())) {
                    mappingFound = true;
                    break;
                }
            }
            if (!mappingFound) {
                throw new BTSLBaseException(this, "validateHeadersValues", PretupsErrorCodesI.ROUTING_ERROR_INTERFACE_NTWK_MAPPING_NOT_FOUND);
            }

            String extId = _routingUploadDAO.loadExternalID(p_con, _interfaceID);

            if (BTSLUtil.isNullString(extId)) {
                throw new BTSLBaseException(this, "validateHeadersValues", PretupsErrorCodesI.ROUTING_ERROR_EXT_ID_NOT_FOUND);
            }

            if (!_externalID.equals(extId)) {
                throw new BTSLBaseException(this, "validateHeadersValues", PretupsErrorCodesI.ROUTING_ERROR_EXT_ID_INVALID);
            }

            if (BTSLUtil.isNullString(_numberOfRecords)) {
                throw new BTSLBaseException(this, "validateHeadersValues", PretupsErrorCodesI.ROUTING_NO_OF_REC_NOT_FOUND);
            }
            try {
                Long.parseLong(_numberOfRecords);
            } catch (NumberFormatException nfe) {
                _log.error("validateHeadersValues", "Invalid number of record value " + _numberOfRecords);
                _log.errorTrace(METHOD_NAME, nfe);
                throw new BTSLBaseException(this, "validateHeadersValues", PretupsErrorCodesI.ROUTING_NO_OF_REC_INVALID);
            }
        } catch (BTSLBaseException be) {
            _log.error("validateHeadersValues", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("validateHeadersValues", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingUpload[validateHeadersValues]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "validateHeadersValues", e.getMessage());
        }
    }

    /**
     * Main Method
     * 
     * @param args
     */
    public static void main(String[] args) {
        final String METHOD_NAME = "main";
        if (_log.isDebugEnabled()) {
            _log.debug(" RoutingUpload ", "Entered main ");
        }

        RoutingUpload routingFileUpload = new RoutingUpload();
        try {
            if (args.length != 2) {
                _log.error("RoutingUpload main()", " Usage : RoutingUpload [Constants file] [ProcessLogConfig file]");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "RoutingUpload[main]", "", "", "", "Improper usage. Usage : RoutingUpload [Constants file] [ProcessLogConfig file]");
                throw new BTSLBaseException("RoutingUpload ", " main ", PretupsErrorCodesI.ROUTING_MISSING_INITIAL_FILES);

            }
            loadCachesAndLogFiles(args[0], args[1]);
            routingFileUpload.process();
        } catch (BTSLBaseException be) {

            _log.error("RoutingUpload main()", "BTSLBaseException be=" + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {

            _log.error("RoutingUpload main()", " Exception e= " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            ConfigServlet.destroyProcessCache();
            if (_log.isDebugEnabled()) {
                _log.debug(" RoutingUpload ", "Exiting main ");
            }
        }
    }

    /**
     * This method moves the files to the backup location once it is processed
     * and the data within it has been updated
     * in the database.It also checks whether the file being moved already
     * exists at the destination.If yes it throws
     * an exception
     * 
     * @param p_fileName
     *            String
     * @return boolean
     */

    private boolean moveFile(String p_fileName, String p_newFileName) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("moveFile", " Entered p_fileName:= " + p_fileName + " p_newFileName" + p_newFileName);
        }

        File fileRead = new File(p_fileName);
        File fileArchive = new File(_moveLocation);
        if (!fileArchive.isDirectory()) {
            fileArchive.mkdirs();
        }

        fileArchive = new File(_moveLocation + File.separator);

        boolean flag = fileRead.renameTo(new File(fileArchive, p_newFileName));
        if (_log.isDebugEnabled()) {
            _log.debug("moveFile", " Exiting File Moved= " + flag);
        }
        return flag;
    }// end of moveFileToArchive

    /**
     * This method loads the Constants.props and LogConfigFile.Besides it also
     * loads the Caches.
     * The name of the files to be uploaded are provided as parameter to the
     * process.
     * 
     * @param p_arg1
     * @param p_arg2
     * @throws BTSLBaseException
     */
    public static void loadCachesAndLogFiles(String p_arg1, String p_arg2) throws BTSLBaseException {
        final String METHOD_NAME = "loadCachesAndLogFiles";
        if (_log.isDebugEnabled()) {
            _log.debug(" loadCachesAndLogFiles ", " Entered with p_arg1=" + p_arg1 + " p_arg2=" + p_arg2);
        }
        File logconfigFile = null;
        File constantsFile = null;
        try {
            constantsFile = new File(p_arg1);
            if (!constantsFile.exists()) {

                _log.error("RoutingUpload[loadCachesAndLogFiles]", " Constants file not found on location:: " + constantsFile.toString());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "RoutingUpload[loadCachesAndLogFiles]", "", "", "", " The Constants file doesn't exists at the path specified. ");
                throw new BTSLBaseException("RoutingUpload ", " loadCachesAndLogFiles ", PretupsErrorCodesI.ROUTING_MISSING_CONST_FILE);
            }

            logconfigFile = new File(p_arg2);
            if (!logconfigFile.exists()) {

                _log.error("RoutingUpload[loadCachesAndLogFiles]", " ProcessLogConfig file not found on location:: " + logconfigFile.toString());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingUpload[loadCachesAndLogFiles]", "", "", "", " The ProcessLogConfig file doesn't exists  at the path specified. ");
                throw new BTSLBaseException("RoutingUpload ", "loadCachesAndLogFiles ", PretupsErrorCodesI.ROUTING_MISSING_LOG_FILE);
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            LookupsCache.loadLookAtStartup();
            NetworkPrefixCache.loadNetworkPrefixesAtStartup();
        } catch (BTSLBaseException be) {
            _log.error("RoutingUpload[loadCachesAndLogFiles]", "BTSLBaseException =" + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        }// end of BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("RoutingUpload[loadCachesAndLogFiles]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingUpload[loadCachesAndLogFiles]", "", "", "", "Exception=" + e.getMessage());
            throw new BTSLBaseException("RoutingUpload ", " loadCachesAndLogFiles ", PretupsErrorCodesI.ROUTING_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of Exception
        finally {
            if (logconfigFile != null) {
                logconfigFile = null;
            }
            if (constantsFile != null) {
                constantsFile = null;
            }
            if (_log.isDebugEnabled()) {
                _log.debug("RoutingUpload[loadCachesAndLogFiles]", " Exiting..........");
            }
        }// end of finally
    }

    /**
     * This method does the actual work of reading the data line by line and
     * performing the various checks required.
     * After passing all the checks, the data is stored in the list and this
     * list is returned
     * 
     * @param p_invalidMsisdn
     * @return arraylist
     * @throws BTSLBaseException
     */
    public ArrayList readValidateData(StringBuffer p_invalidMsisdn) throws BTSLBaseException {
        final String METHOD_NAME = "readValidateData";
        if (_log.isDebugEnabled()) {
            _log.debug("readValidateData", " Entered ");
        }
        String tempStr = "";
        StringTokenizer startparser = null;
        long recordsTotal = 0;
        String delim = Constants.getProperty("DelimiterforuploadRouting");
        if (BTSLUtil.isNullString(delim)) {
            delim = " ";
        }
        String msisdn = null;
        String filteredMsisdn = null;
        String msisdnPrefix = null;
        NetworkPrefixVO networkPrefixVO = null;
        String networkCode = null;
        ArrayList validMSISDNList = new ArrayList();
        boolean headerEnd = true;
        int errorCount = 0;
        try {
            while ((tempStr != null) && !(_headerEnd != null ? _headerEnd.equals(tempStr = _in.readLine()) : false)) // Read
                                                                                                                     // the
                                                                                                                     // file
                                                                                                                     // till
                                                                                                                     // the
                                                                                                                     // end
                                                                                                                     // of
                                                                                                                     // file.
            {
                if (tempStr == null) {
                    headerEnd = false;
                    break;
                }
                if (tempStr.trim().length() == 0) {
                    continue;
                }
                try {
                    startparser = new StringTokenizer(tempStr, delim);
                    if (_log.isDebugEnabled()) {
                        _log.debug("readData", "Input = " + tempStr);
                        _log.debug("readData", "There are " + startparser.countTokens() + " entries");
                    }
                    while (startparser.hasMoreTokens()) {
                        recordsTotal++; // Keeps track of line number
                        msisdn = startparser.nextToken().trim();
                        // FilteredMSISDN is caluculated from
                        // getFilteredIdentificationNumber
                        // This is done because this field can contains msisdn
                        // or account id
                        filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(msisdn);
                        // isValidMsisdn is replaced by
                        // isValidIdentificationNumber
                        // This is done because this field can contains msisdn
                        // or account id
                        if (!BTSLUtil.isValidIdentificationNumber(filteredMsisdn)) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("readValidateData", "Not a valid MSISDN " + msisdn);
                            }
                            RoutingFileProcessLog.log("RoutingUpload", PretupsI.SYSTEM_USER, msisdn, recordsTotal, "Not a valid MSISDN", "Fail", _inputFile + "," + _networkCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(delim);
                            errorCount++;
                            continue;
                        }
                        msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                        networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);

                        if (networkPrefixVO == null) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("readValidateData", "Not supporting Network " + msisdn);
                            }
                            RoutingFileProcessLog.log("RoutingUpload", PretupsI.SYSTEM_USER, msisdn, recordsTotal, "Not supporting Network", "Fail", _inputFile + "," + _networkCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(delim);
                            errorCount++;
                            continue;
                        }
                        networkCode = networkPrefixVO.getNetworkCode();
                        if (!networkCode.equals(_networkCode)) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("readValidateData", "Not supporting Network" + msisdn);
                            }
                            RoutingFileProcessLog.log("RoutingUpload", PretupsI.SYSTEM_USER, msisdn, recordsTotal, "Not supporting Network", "Fail", _inputFile + "," + _networkCode);
                            p_invalidMsisdn.append(msisdn);
                            p_invalidMsisdn.append(delim);
                            errorCount++;
                            continue;
                        }

                        if (errorCount > _errorCount) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("readValidateData", "The number of errors in the file exceed the number specified in the file");
                            }
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingUpload[readValidateData]", "", "", "", "Number of errors in the file are more than the value specified in the file");
                            throw new BTSLBaseException("RoutingUpload ", " readValidateData ", PretupsErrorCodesI.ROUTING_NO_OF_ERROR_MORE);
                        }
                        validMSISDNList.add(filteredMsisdn);
                    }
                } catch (BTSLBaseException be) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("readValidateData", "BTSLBaseException :=" + be.getMessage());
                    }
                    throw be;
                }
            }
            if (recordsTotal == 0) {
                if (_log.isDebugEnabled()) {
                    _log.debug("readValidateData", "No records in the file ");
                }
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingUpload[readValidateData]", "", "", "", "No records in the file.");
                throw new BTSLBaseException(this, "readValidateData", PretupsErrorCodesI.ROUTING_ERROR_FILE_NO_RECORDS);
            }
            if (recordsTotal != Long.parseLong(_numberOfRecords)) {
                if (_log.isDebugEnabled()) {
                    _log.debug("readValidateData", "The number of records in the file exceed the number specified in the file");
                }
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingUpload[readValidateData]", "", "", "", "Number of records in the file are more than the value specified in the header");
                throw new BTSLBaseException("RoutingUpload ", " readValidateData ", PretupsErrorCodesI.ROUTING_NO_OF_REC_NOT_MATCHING);
            }
            if (!headerEnd) {
                if (_log.isDebugEnabled()) {
                    _log.debug("readValidateData", "No END tag present in the file.");
                }
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RoutingUpload[readValidateData]", "", "", "", "No END tag in the file.");
                throw new BTSLBaseException(this, "readValidateData", PretupsErrorCodesI.ROUTING_ERROR_NO_END_TAG);
            }
            return validMSISDNList;
        } catch (BTSLBaseException e) {
            if (_log.isDebugEnabled()) {
                _log.debug("readValidateData", "Exception e:=" + e.getMessage());
            }
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "readValidateData", e.getMessage());
        } catch (IOException e) {
            if (_log.isDebugEnabled()) {
                _log.debug("readValidateData", "Exception e:=" + e.getMessage());
            }
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "readValidateData", e.getMessage());
        } catch (Exception e) {
            if (_log.isDebugEnabled()) {
                _log.debug("readValidateData", "Exception e:=" + e.getMessage());
            }
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "readValidateData", e.getMessage());
        }
    }
}
