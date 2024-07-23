package com.btsl.pretups.whitelist.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.logging.FileProcessLogger;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.whitelist.businesslogic.ParserI;
import com.btsl.pretups.whitelist.businesslogic.WhiteListDAO;
import com.btsl.pretups.whitelist.businesslogic.WhiteListVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

/**
 * @(#)FileProcess.java Copyright(c) 2006, Bharti Telesoft Int. Public Ltd. All
 *                      Rights Reserved
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Author Date History
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Ashish Kumar May 16,2006 Initial Creation
 *                      --------------------------------------------------------
 *                      ----------------------------------------
 *                      This class is responsible to provide following
 *                      functionalities. 1.Reading WhiteList file. 2.Interact
 *                      with the business logic of white list 3.Decide the mod
 *                      to interact with businesslogic of white list
 */

public class FileProcess {

    private String _filePath; // File path name
    private String _filePrefix = ""; // All the files with this prefix will
    // be loaded from given file path
    // this time file prefix is initializes as empty string as per requirement
    // if required it will be picked up from the constant file(code is
    // commented)
    private ArrayList _fileList; // Contain all the file object thats name
    // start with file prefix.
    private BufferedReader _in; // BufferedReader object that is created for
    // each file.
    private String _headerStart; // Define whether the file has optional
    // header or not.
    private String _headerEnd; // Define whether the file has optional header
    // or not.
    private File _inputFile; // File object for input file
    private File _destFile; // Destination File object
    private String _moveLocation; // Input file is move to this location After
    // successfull proccessing
    private String _fileExt; // Files are picked up only this extention from
    // the specified directory
    private final String _allowedExt = "txt,csv";// Input file should be
    // either txt or csv.
    private ArrayList _deleteList; // This list stores all the records to be
    // deleted
    private ArrayList _updateList; // This list stores all the records to be
    // updated
    private ArrayList _insertList; // This list stores all the records to be
    // inserted
    private Date _date = null; // Used for the current date;
    private String _action; // Defines the mod by which data to be
    // updated(DROP,Delete,Update,Append
    private ParserI _parserObj = null; // parser class object this class is
    // specific to operator
    private int _percentageValidCount;// If invalid records equal or greater
    // than this percentage that file is not
    // proccessed
    private ProcessBL _processBL = null;
    private String _interfaceID;
    private String _networkCode;
    private String _fileNameSeparator; // This separator will be used while
    // parsing the file name and should be
    // defined in Constants file.

    private ProcessStatusDAO _processStatusDAO = null;
    private static Log _log = LogFactory.getLog(FileProcess.class.getName());

    public FileProcess() {
        super();
        _processBL = new ProcessBL();
        _processStatusDAO = new ProcessStatusDAO();
    }

    /**
     * This method is envoked by the main method. -By looking the status of the
     * process this method decide whether to continue the process or stop. This
     * method is responsible for reading the file and process it. Processing of
     * file includes. 1.File contains two parts Header and Data,First header is
     * proccessed and then Data is processed. a.If Header is invalid stop the
     * proccess b.If Header is valid, Data is processed.In presence of Header,
     * Data lies between [START] and [END] tag-Case sensitive. 2.Calls internal
     * method that create instance of ParserClass. 3.While proccesing of
     * records,each Record is passed to Parser Class and that returns
     * whitelistVO. 4.WhiteListVO is added to the list based on the movement
     * code (Insert,Delete and Update) 5.Based on the
     * Action(Drop,Delete,Update)data is populated in the white list data store.
     * 
     * @throws BTSLBaseException
     */
    private void process(String p_processID) throws BTSLBaseException {
        final String methodName = "process";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_processID" + p_processID);
        }
        final String METHOD_NAME = "process";
        Connection con = null;
        HashMap netInterfaceServiceMap = null; // Used to store the
        // network,interface service map
        // list.
        HashMap tempInterfaceNtwkMappingMap = null; // Used to store the
        // interface list,that is
        // mapped with network code.
        ArrayList tempPostpaidInterfaceList = null; // Used to store the post
        // paid interface list
        // corresponding to network
        // code.
        ArrayList tempNetworkList = null; // Contains the detail of supported
        // network.
        String parserClassPathName = null; // This is the parser class name
        // with qualified path,defined in
        // INFile.
        String[] networkExtID = null; // After parsing the name of file
        // ,externalInterfaceID and network code
        // is stored as Element of networkExtID
        // array
        String externalInterfaceID = null;
        int successU = 0;
        ProcessStatusVO processStatusVO = null;
        int successCount = 0;
        boolean processStatusOK = false; // This is used to varify the status
        // of process,fetched from the
        // process_status table.
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "START::TOTAL MEMORY --->" + Runtime.getRuntime().totalMemory());
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "START::FREE MEMORY --->" + Runtime.getRuntime().freeMemory());
            }
            // Load constant values
            loadConstantValues();

            // Create connection
            con = OracleUtil.getSingleConnection();

            // check the connection for null if it is null stop with showing
            // error message.
            if (con == null) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_CONN_NULL);
            }

            // check the process status by calling checkProcessUnderprocess
            // method of processBL,if its ok continue else stop the process with
            // error message.
            processStatusVO = _processBL.checkProcessUnderProcess(con, p_processID);

            if (!(processStatusVO != null && processStatusVO.isStatusOkBool())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
            }
            processStatusOK = processStatusVO.isStatusOkBool();
            // Commiting the status of process status as 'U-Under Process'.
            con.commit();

            // Call the loadNetwkInterfaceServiceDetails method of processBL
            // that returns HashMap,
            // netInterfaceServiceMap::containing network,interface and service
            // details in the memory and pass this HashMap to parser class.
            netInterfaceServiceMap = _processBL.loadNetwkInterfaceServiceDetails(con);

            if (netInterfaceServiceMap == null) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_NETWK_DETAIL_NULL);
            }

            // Load All the files from the specified directory path.
            loadFilesFromDir();

            // Implement the file proccessing logic, before process the file do
            // the following
            // 1.Parse the file name to get the network code and external
            // interface id.
            // 2.get the interface id based on the external interfaceid.
            // 3.validate the network code(Since interface id is picked up from
            // the postpaidInterfaceList,hence no need to validate the
            // interfaceID).
            // 4.After getting the interfaceID get the parser class from
            // FileCache and instantiate it for each file.
            // 5.Set the network code,interfaceID,externalInterfaceId to parser.
            // 6.Set netInterfaceServiceMap for parser class to validate the
            // service class for each record(validation occurs in processBL)
            // File Proccessing include
            // 1.Files are process one by one and after successful processing of
            // 2.File transaction is committed incase of successful and rollback
            // in case of failure.
            // after successful process file is moved to history location.
            for (int l = 0, size = _fileList.size(); l < size; l++) {
                // Getting the file object
                _inputFile = (File) _fileList.get(l);
                try {
                    // Files are stored in memory,before processing check
                    // whether it is exist or not.
                    // Initialize the network code and interfaceID for each
                    // file.
                    _interfaceID = null;
                    _networkCode = null;
                    if (!_inputFile.exists()) {
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "File = " + _inputFile + "does not exist");
                        }
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_FILE_DOES_NOT_EXIST);
                    }
                    // Initialize the successCount and fileProcessStatus for
                    // each file.
                    successCount = 0;

                    // File name parser: This is responsible to parse the file
                    // name to get the ExternalInterfaceID and NetworkCode.
                    networkExtID = fileNameParser(_inputFile);

                    // At the 0th index of networkCdExtID array external
                    // interface is present
                    externalInterfaceID = networkExtID[0];
                    if (externalInterfaceID == null) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_FILE_NAME_EXTID_NOT_FOUND);
                    }

                    // At the 1st index of networkCdExtID array network code is
                    // present
                    _networkCode = networkExtID[1];
                    if (_networkCode == null) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_FILE_NAME_NTWKCODE_NOT_FOUND);
                    }

                    // Get the networkList and check whether any network exist
                    // or not.
                    tempNetworkList = (ArrayList) netInterfaceServiceMap.get("NETWORK_LIST");
                    if (tempNetworkList == null || tempNetworkList.isEmpty()) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_NO_NETWORK_AVALAIBLE);
                    }

                    // Validate the network code.
                    _processBL.validateNetworkCode(_networkCode, tempNetworkList);

                    // Check whether postpaid interface list does not contains
                    // any post paid interface.
                    tempPostpaidInterfaceList = (ArrayList) netInterfaceServiceMap.get("POST_PAID_INTF_LIST");
                    if (tempPostpaidInterfaceList == null || tempPostpaidInterfaceList.isEmpty()) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_NO_POST_PAID_INTERFACE);
                    }

                    // Get the interfaceID based on the external interfaceID
                    _interfaceID = _processBL.getInterfaceID(externalInterfaceID, tempPostpaidInterfaceList);

                    // Get the Interface Network Mapping HashMap from
                    // netInterfaceServiceMap
                    tempInterfaceNtwkMappingMap = (HashMap) netInterfaceServiceMap.get("INTF_NETWK_MAPPING");

                    // Validate the interfaceID whether it is mapped with any
                    // network or not.
                    _processBL.validateInterfaceID(_interfaceID, _networkCode, tempInterfaceNtwkMappingMap);

                    // Get the parser class path name from INFile based on the
                    // interfaceID
                    parserClassPathName = (FileCache.getValue(_interfaceID, "PARSER_CLASS_PATH_NAME"));
                    // Check whether the parser class is defined properly in
                    // INFile if not stop the processin of that input file with
                    // handling the event.
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "parserClassPathName -----> " + parserClassPathName);
                    }

                    if (parserClassPathName == null || "".equals(parserClassPathName.trim())) {
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "", "FileProcess[process]", "", "IN file  interfaceID = " + _interfaceID, "[For input file = " + _inputFile + " ] [Parser class path name = ]" + parserClassPathName + " is invalid");
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_PARSER_CLASS_PATH);
                    }
                    // Creat the instance of the parser class
                    getParserObj(parserClassPathName);

                    // Call init method to load the network list, interface
                    // mapping and service class list in memory
                    // Also add the network code,externalInterfaceId and
                    // interfaceID into netInterfaceServiceMap.
                    netInterfaceServiceMap.put("NETWORK_CODE", _networkCode);
                    netInterfaceServiceMap.put("EXT_INTERFACE_ID", externalInterfaceID);
                    netInterfaceServiceMap.put("INTERFACE_ID", _interfaceID);
                    _parserObj.init(netInterfaceServiceMap);

                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "File = " + _inputFile + " is picked up for proccessing");
                    }
                    // setting the buffer reader for the file.
                    setBufferReader();

                    // proccessing the file header.
                    proccessHeader();

                    // proccess the data part of the file.
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Before Reading and validating data file data :: TOTAL MEMORY --->" + Runtime.getRuntime().totalMemory());
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Before Reading and validating data file data :: FREE MEMORY --->" + Runtime.getRuntime().freeMemory());
                    }
                    proccessData();

                    // Log the Total memory and Free memory before sending the
                    // data to be updated in data store
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Before updating file data :: TOTAL MEMORY --->" + Runtime.getRuntime().totalMemory());
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Before updating file data :: FREE MEMORY --->" + Runtime.getRuntime().freeMemory());
                    }
                    // Select the data updation mod and update the data store as
                    // per action.
                    successCount = dataUpdationMod(con);
                    // Log the Free memory after data has been updated in data
                    // store
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "After Updating data to data store :: FREE MEMORY --->" + Runtime.getRuntime().freeMemory());
                    }
                    if (successCount >= 0) {
                        // Commiting the transaction after successful
                        // proccessing of file data.
                        con.commit();
                    } else {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_DATA_UPDATE);
                    }
                    try {
                        // Before moving file we have close the Buffered Reader
                        // object.
                        try {
                            if (_in != null) {
                                _in.close();
                            }
                        } catch (Exception e1) {
                            _log.errorTrace(METHOD_NAME, e1);
                        }
                        // After closing the bufferedreader object on successful
                        // updation of file data move the file into history
                        // location.
                        String fileName = _inputFile.getName();
                        String moveFileName = fileName.substring(0, fileName.indexOf(".")) + "_" + _date.getDate() + "_" + _date.getHours() + "_" + _date.getMinutes() + "." + fileName.substring(fileName.indexOf(".") + 1);
                        _processBL.moveFile(_inputFile, _moveLocation, moveFileName);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "FileProcess[process]", "[File Name = " + _inputFile + " ]", "[File Process Status is Successfull]", "", "Moved to location = " + _destFile);
                    } catch (Exception e) {
                        _log.error(methodName, "Exception e = " + e.getMessage());
                        _log.errorTrace(METHOD_NAME, e);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "FileProcess[process]", "[File Name = " + _inputFile + " ]", "[File Process Status is Successfull]", "File not moved successfully to Location = " + _destFile, " ");
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "File = " + _inputFile + " is proccessed");
                    }
                }// end of inner try-block
                catch (BTSLBaseException be) {
                    // In case of unsuccessful processing of file set this flag
                    // so that file is not moved.
                    if (con != null) {
                        try {
                            con.rollback();
                        } catch (Exception e1) {
                            _log.errorTrace(METHOD_NAME, e1);
                        }
                    }
                    _log.errorTrace(METHOD_NAME, be);
                    _log.error(methodName, "BTSLBaseException be = " + be.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "FileProcess[process]", "[File Name = " + _inputFile + " ] [File Process Status is Unsuccessfull]", "", "", "BTSLBaseException:" + be.getMessage());
                }// end of catch-BTSLBaseException
                catch (Exception e) {
                    _log.error(methodName, "Exception e = " + e.getMessage());
                    if (con != null) {
                        try {
                            con.rollback();
                        } catch (Exception e1) {
                            _log.errorTrace(METHOD_NAME, e1);
                        }
                    }
                    _log.errorTrace(METHOD_NAME, e);
                    _log.error(methodName, "Exception e = " + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "FileProcess[process]", "[File Name = " + _inputFile + " ] [File Process Status is Unsuccessfull]", "", "", "Exception:" + e.getMessage());
                } finally {
                    try {
                        if (_in != null) {
                            _in.close();
                        }
                    } catch (Exception e1) {
                        _log.errorTrace(METHOD_NAME, e1);
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "File = " + _inputFile + " is proccessed");
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "END::TOTAL MEMORY --->" + Runtime.getRuntime().totalMemory());
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "END::FREE MEMORY --->" + Runtime.getRuntime().freeMemory());
                    }
                }// end of inner finally
            }// end of for loop
        }// end of try-block
        catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException be= " + be);
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception e1) {
                    _log.errorTrace(METHOD_NAME, e1);
                }
            }
            _log.errorTrace(METHOD_NAME, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FileProcess[process]", "processStatusVO.getProcessID()" + ProcessI.WHITE_LIST_PROCCESSID, "", "", "BTSLBaseException:" + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.error(methodName, "Exception be= " + e);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FileProcess[process]", "processStatusVO.getProcessID()" + ProcessI.WHITE_LIST_PROCCESSID, "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, e.getMessage());
        }// end of catch-Exception
        finally {
            try {
                // Setting the process status as 'C-Complete' if the
                // processStatusOK is true
                if (processStatusOK) {
                    _date = new Date();
                    processStatusVO.setStartDate(processStatusVO.getStartDate());
                    processStatusVO.setExecutedOn(_date);
                    processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    successU = _processStatusDAO.updateProcessDetail(con, processStatusVO);

                    // Commiting the process status as 'C-Complete'
                    if (successU > 0) {
                        con.commit();
                    } else {
                        // throw new BTSLBaseException(this, methodName,
                        // PretupsErrorCodesI.PROCESS_ERROR_UPDATE_STATUS);
                    }
                }// end of IF-Checks the proccess status
            }// end of try-block
            catch (BTSLBaseException be) {
                _log.error(methodName, "BTSLBaseException be= " + be);
                _log.errorTrace(METHOD_NAME, be);
                if (con != null) {
                    try {
                        con.rollback();
                    } catch (Exception e1) {
                        _log.errorTrace(METHOD_NAME, e1);
                    }
                }
                // Commented by Ashish 16-01-07
                // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"FileProcess[process]","processStatusVO.getProcessID()"+ProcessI.WHITE_LIST_PROCCESSID,"","","BTSLBaseException:"+be.getMessage());
                // throw be;
            }// end of catch-BTSLBaseException
            catch (Exception e) {
                _log.errorTrace(methodName, e);
                _log.error(methodName, "Exception e= " + e);
                if (con != null) {
                    try {
                        con.rollback();
                    } catch (Exception e1) {
                        _log.errorTrace(METHOD_NAME, e1);
                    }
                }
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FileProcess[process]", "processStatusVO.getProcessID()" + ProcessI.WHITE_LIST_PROCCESSID, "", "", "BaseException:" + e.getMessage());
            }// end of catch-Exception
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e1) {
                    _log.errorTrace(METHOD_NAME, e1);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting ");
            }
        }// end of finally
    }// end of proccess

    /**
     * This method is used to load the Constant values from the Constants file.
     * 
     * @throws BTSLBaseException
     */
    private void loadConstantValues() throws BTSLBaseException {
        final String methodName = "loadConstantValues";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        try {
            _headerStart = Constants.getProperty("WHITELIST_HEADER_START");
            _headerEnd = Constants.getProperty("WHITELIST_HEADER_END");

            _fileNameSeparator = Constants.getProperty("WHITELIST_FILE_NAME_SEPARATOR");
            if (_fileNameSeparator == null || "".equals(_fileNameSeparator)) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_FILENAME_SEPARATOR);
            }
            // File extention of input file.
            _filePath = BTSLUtil.NullToString(Constants.getProperty("WHITELIST_FILE_PATH"));

            // Checking whether the file path is provided and it exist or not.
            if (!(new File(_filePath).exists())) {
                _log.error(methodName, "Directory Path = " + _filePath + " does not exist");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_DIR_NOT_EXIST);
            }

            // Check the file prefix if it is not provided then set it as empty
            // string.
            _filePrefix = (Constants.getProperty("WHITELIST_FILE_PREFIX") == null ? "" : Constants.getProperty("WHITELIST_FILE_PREFIX"));

            // Checking the fileExtention is provided or not and if provided
            // check that is it allowed or not.
            _fileExt = Constants.getProperty("WHITELIST_FILE_EXT");
            if (_fileExt == null || !(_allowedExt.contains(_fileExt))) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_FILE_EXT_NOT_DEFINED);
            }

            // Destination location of the file where to move after reading the
            // input file.
            _moveLocation = BTSLUtil.NullToString(Constants.getProperty("WHITELIST_MOVE_LOCATION"));
            _destFile = new File(_moveLocation);

            // Checking the destination location whether it is exist or not,if
            // not exist stop the proccess.
            if (!_destFile.exists()) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Destination Location = " + _moveLocation + " does not exist");
                }
                boolean fileCreation = _destFile.mkdirs();
                if (fileCreation) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "New Location = " + _destFile + "has been created successfully");
                    }
                }
            }
            // Check whether the action is provided or not.
            _action = Constants.getProperty("WHITELIST_ACTION");
            if (_action == null) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_ACTION_IS_NOT_FOUND);
            }
            try {
                // Check if the percentage invalid record is exist and should be
                // numeric.
                String percentageValidCount = Constants.getProperty("WHITELIST_PERCENTAGE_VALID_COUNT");
                if (percentageValidCount == null) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_PERC_INVALID_COUNT_NOT_FOUND);
                }
                _percentageValidCount = Integer.parseInt(percentageValidCount);
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_PERCTG_INVALIDCT_NOT_NUMERIC);
            }
        }// end of try block
        catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException be = " + be);
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FileProcess[loadConstantValues]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: _FILE_PATH = " + _filePath + " _fileExt= " + _fileExt + "_moveLocation= " + _moveLocation + " _headerStart = " + _headerStart + " _headerEnd = " + _headerEnd + " _action = " + _action);
            }
        }// end of finally
    }// end of loadConstantValues

    /**
     * This method is used to create an instance of parser class
     * 
     * @param String
     *            parserClassName
     * @throws BTSLBaseException
     */
    private void getParserObj(String p_parserClassName) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getParserObj", "Entered p_parserClassName = " + p_parserClassName);
        }
        final String METHOD_NAME = "getParserObj";
        try {
            // Creating the instance of parser class.
            _parserObj = (ParserI) Class.forName(p_parserClassName).newInstance();
        } catch (Exception e) {
            _log.error("getParserObj", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FileProcess[getParserObj]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getParserObj", PretupsErrorCodesI.WLIST_ERROR_PARSER_CLASS_NOT_INSTANTIATED);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug("getParserObj", "Exiting");
            }
        }// end of finally
    }// end of getParserObj

    /**
     * This method is used to loadAll the files with specified prefix. All these
     * file objects are stored in ArrayList.
     * 
     * @throws BTSLBaseException
     */
    public void loadFilesFromDir() throws BTSLBaseException {
        final String methodName = "loadFilesFromDir";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        File directory = null;
        try {
            directory = new File(_filePath);
            // Check if the directory contains any files
            if (directory.list() == null) {
                throw new BTSLBaseException(this, "loadFileFromDir", PretupsErrorCodesI.WLIST_ERROR_DIR_CONTAINS_NO_FILES);
            }

            // This filter is used to filter all the files that start with
            // _filePrefix;
            FilenameFilter filter = new FilenameFilter() {
                @Override
				public boolean accept(File dir, String name) {
                    return (name.startsWith(_filePrefix) && name.endsWith(_fileExt));
                }
            };

            // List of files that start with the file prefix with specified
            // extantion.
            File[] tempFileArray = directory.listFiles(filter);
            _fileList = new ArrayList();

            // Storing all the files(not dir)to array list
            for (int l = 0, size = tempFileArray.length; l < size; l++) {
                if (tempFileArray[l].isFile()) {
                    _fileList.add(tempFileArray[l]);
                    if (_log.isDebugEnabled()) {
                        _log.debug("loadFileFromDir", "File = " + tempFileArray[l] + " is added to fileList");
                    }
                }
            }// end of for loop

            // Check whether the directory contains the file start with
            // filePrefix.
            if (_fileList.isEmpty()) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_DIR_CONTAINS_NO_FILES);
            }
        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "FileProcess[loadFilesFromDir]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exited");
            }
        }// end of finally
    }// end of loadFilesFromDir

    /**
     * This method is used to set the Buffered Reader object for the file.
     * 
     * @throws BTSLBaseException
     */
    private void setBufferReader() throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("setBufferReader", "Entered");
        }
        final String METHOD_NAME = "setBufferReader";
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("setBufferReader", "_inputFile = " + _inputFile);
            }
            _in = new BufferedReader(new FileReader(_inputFile));
        }// end of try block
        catch (IOException ioe) {
            _log.errorTrace(METHOD_NAME, ioe);
            _log.error("setBufferReader", "IOException ioe = " + ioe.getMessage());
            throw new BTSLBaseException(this, "setBufferReader", ioe.getMessage());
        }// end of catch-IOException
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug("setBufferReader", "Exited");
            }
        }
    }// end of setBufferReader

    /**
     * This method is used to proccess the Header information 1.First check
     * whether the header is present or not. 2.Read the file header information.
     * 3.If header information is presend then Header information is parsed in
     * parser class. 4.If start is not present then Raise an error in case
     * Required header.
     * 
     * @throws BTSLBaseException
     */
    private void proccessHeader() throws BTSLBaseException {
        final String methodName = "proccessHeader";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        final String METHOD_NAME = "proccessHeader";
        StringBuffer strBuff = new StringBuffer();
        String str = "";
        try {
            while (str != null && (_headerStart != null ? !_headerStart.equals(str = _in.readLine()) : false)) {
                if (str == null) {
                    break;
                }
                if ("".equals(str.trim())) {
                    continue;// Skip all the blank lines
                }
                try {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "str = " + str);
                    }
                    strBuff.append(str);
                    strBuff.append("\n");
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    _log.error(methodName, "Exception be = " + e.getMessage());
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_HEADER_INFO);
                }
            }// end of while
            if (_headerStart != null && !_headerStart.equals(str)) {
                _log.error(methodName, "Required header Start tag = " + _headerStart + " is not found");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_HEADER_START_TAG_NOT_FOUND);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Header string strBuff.toString() = " + strBuff.toString());
                // _headerInfo=_parserObj.parseHeaderInformation(strBuff.toString());
            }
        }// end of try-block
        catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException e = " + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FileProcess[proccessHeader]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exited");
            }
        }// end of finally
    }// end of proccessHeader

    /**
     * This method is used to proccess the data
     * 
     * @throws BTSLBaseException
     */
    private void proccessData() throws BTSLBaseException {
        final String methodName = "proccessData";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        WhiteListVO whiteListVO = null;
        _deleteList = new ArrayList(); // Initialize the _delete list each time
        // when file is start for processing.
        _updateList = new ArrayList(); // Initialize the _update list each time
        // when file is start for processing.
        _insertList = new ArrayList(); // Initialize the _insert list each time
        // when file is start for processing.
        String str = "";
        int recordCounter = 0; // Used to count the total number of records of
        // a file.
        int invalidCount = 0; // Used to count the number of invalid records.
        int proccessedRecords = 0;// Used to count the number of processed
        // record.
        _date = new Date();
        boolean startFlag = true; // Flag is used for Logging the detail in
        // FileProcessLogger.
        try {
            while (str != null && (_headerEnd != null ? !_headerEnd.equals(str = _in.readLine()) : (str = _in.readLine()) != null)) {
                if (str == null) {
                    break;// Check for null str before trim the str
                }
                if ("".equals(str.trim())) {
                    continue;// Skip all the blank lines
                }
                try {
                    recordCounter++;
                    whiteListVO = new WhiteListVO();
                    whiteListVO.setStartDate(_date);
                    if (startFlag) {
                        FileProcessLogger.log(startFlag, whiteListVO);
                    }
                    startFlag = false;
                    whiteListVO.setRequestString(str);
                    // Pass the record to parser class that generate a VO.
                    _parserObj.generateWhiteListVO(whiteListVO);
                    if ((whiteListVO.getDelete()).equals(whiteListVO.getMovementCode())) {
                        _deleteList.add(whiteListVO);
                    } else if ((whiteListVO.getUpdate()).equals(whiteListVO.getMovementCode())) {
                        _updateList.add(whiteListVO);
                    } else {
                        _insertList.add(whiteListVO);
                    }
                }// end of try block
                catch (BTSLBaseException be) {
                    invalidCount++; // Incase of exception increment the invalid
                    // count.
                    FileProcessLogger.log(startFlag, whiteListVO); // Log the
                    // detail
                    // corresponding
                    // to the
                    // invalid
                    // record.
                    _log.errorTrace(methodName, be);
                }// end of catch-BTSLBaseException
            }// end of while
            if (_headerEnd != null && !_headerEnd.equals(str)) {
                _log.error(methodName, methodName, "Required header End tag = " + _headerEnd + " is not found");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_HEADER_END_TAG_NOT_FOUND);
            }
            // Check the maximum percentage of invalid records if it is greater
            // or equal to allowed percentage stop the proccessing.
            // Check the recordCounter value it should not be zero incase if
            // there are no records.
            if (recordCounter == 0) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_FILE_CONTAINS_NO_RECORDS);
            }
            if ((invalidCount * 100) / recordCounter >= (_percentageValidCount)) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_MAX_INVALID_REC_REACH);
            }
        }// end of try-block
        catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FileProcess[proccessData]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, e.getMessage());
        }// end of catch-Exception
        finally {
            try {
                proccessedRecords = _deleteList.size() + _insertList.size() + _updateList.size();
                whiteListVO.setEndDate(new Date());
                whiteListVO.setTotalRecords(recordCounter);
                whiteListVO.setProcessedRecords(proccessedRecords);
                whiteListVO.setUnProccessedRecords(recordCounter - proccessedRecords);
                FileProcessLogger.log(startFlag, whiteListVO);
            } catch (Exception e) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Exception e = " + e.getMessage());
                }
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exited");
            }
        }// end of finally
    }// end of proccessData

    /**
     * This method is used to decide the mod by which white list data is
     * updated.
     * 
     * @param int
     * @return int
     * @throws BTSLBaseException
     */
    private int dataUpdationMod(Connection p_con) throws BTSLBaseException {
        final String methodName = "dataUpdationMod";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        int action = 0;
        int count = 0;
        try {
            if ("BIN".equalsIgnoreCase(_action)) {
                action = 1;
            } else if ("DEL".equalsIgnoreCase(_action)) {
                action = 2;
            } else if ("UPD".equalsIgnoreCase(_action)) {
                action = 3;
            } else if ("DROP".equalsIgnoreCase(_action)) {
                action = 4;
            } else {
                action = 5;
            }
            if ((action == 1 || action == 2) && (_deleteList.size() > 0 || _updateList.size() > 0)) {
                _log.error(methodName, "error invalid action" + action + " is selected");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_INVALID_ACTION);
            }

            if (action == 3 && (_deleteList.isEmpty() && _updateList.isEmpty() && _insertList.isEmpty())) {
                _log.error(methodName, "error invalid action" + action + " is selected");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_INVALID_ACTION);
            }
            switch (action) {
            case 1: {
                count = batchInsert(p_con);
                break;
            }
            case 2: {
                count = deleteInsert(p_con);
                break;
            }
            case 3: {
                count = updateInsertDelete(p_con);
                break;
            }
            case 4: {
                count = dropInsert(p_con); // implement when required
                break;
            }
            case 5: {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_NO_ACTION_IS_SELECTED);
            }
            }// end of cases
        }// end of try blcok
        catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException be=" + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FileProcess[dataUpdationMod]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exited");
            }
        }// end of finally
        return count;
    }// end of dataUpdationMod

    /**
     * 
     * @param Connection
     *            p_con
     * @throws BTSLBaseException
     */
    private int dropInsert(Connection p_con) throws BTSLBaseException {
        final String methodName = "dropInsert";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        int dropSuccess = 0;
        CallableStatement cstmt = null;
        String dbConnected=Constants.getProperty(QueryConstants.PRETUPS_DB);
        String errorcode=null;
        String returnmessage=null;
        try {

            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                cstmt = p_con.prepareCall("{call " + Constants.getProperty("currentschema") + ".p_whiteListDataMgt(?,?)}");
            } else if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
            	cstmt = p_con.prepareCall("{call p_whiteListDataMgt()}");
            }
            else {
                cstmt = p_con.prepareCall("{call p_whiteListDataMgt(?,?)}");
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Before Exceuting Procedure");
            }
            
            if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                ResultSet rs = cstmt.executeQuery();
                if(rs.next()){
                	errorcode =rs.getString("p_errorcode");
                	returnmessage = rs.getString("p_returnmessage");
                 }
             }
            else{
            cstmt.registerOutParameter(1, Types.VARCHAR); // Error code
            cstmt.registerOutParameter(2, Types.VARCHAR); // Error Message
            cstmt.executeUpdate();
            }
            
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "After Exceuting Procedure");
                /*
                 * if(_log.isDebugEnabled()) _log.debug("dropInsert","Parameters
                 * Returned : ErrorCode="+cstmt.getString(1)+" , Error
                 * Message="+cstmt.getString(2)); if (cstmt.getString(1)==null
                 * ||
                 * !cstmt.getString(3).equalsIgnoreCase("SUCCESS"))
                 * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM
                 * ,EventStatusI.RAISED,EventLevelI.FATAL,
                 * "P2pMisDataProcessing[main]","","","",cstmt.getString(4)+"
                 * Exception if any:"+cstmt.getString(5)); else
                 * EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM
                 * ,EventStatusI.RAISED,EventLevelI.MAJOR,
                 * "P2pMisDataProcessing[main]","","","",cstmt.getString(4)+"
                 * Exception if any:"+cstmt.getString(5));
                 */
            }
        }  catch (Exception e) {
        	 if (QueryConstants.DB_POSTGRESQL.equals(dbConnected) && p_con != null)
        	 {
    				try {
    					p_con.rollback();
    				} catch (SQLException e1) {
    					 _log.errorTrace(methodName, e1);
    					 _log.error(methodName, "Exception e = " + e1.getMessage());
    				}
        	 }
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FileProcess[dropInsert]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, e.getMessage());
        }// end of catch-Exception
        return dropSuccess;
    }// end of dropInsert

    /**
     * This method is used to insert the white list info in batch.
     * 
     * @param Connection
     *            p_con
     * @return int
     * @throws BTSLBaseException
     */
    private int batchInsert(Connection p_con) throws BTSLBaseException {
        final String methodName = "batchInsert";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        WhiteListDAO whiteListDAO = null;
        int insertCt = 0;
        try {
            whiteListDAO = new WhiteListDAO();
            insertCt = whiteListDAO.insertIndWhiteListDetail(p_con, _insertList);
            if (insertCt < 0) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_IN_INSERT_RECORDS);
            }
        }// end of try block
        catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException be=" + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FileProcess[batchInsert]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, e.getMessage());
        }// end catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exited inserCt = " + insertCt);
            }
        }// end of finally
        return insertCt;
    }// end of batchInsert

    /**
     * This method is used to delete all the records that are present in the
     * deleteList and Insert all the records that are present in the insertList.
     * 
     * @param Connection
     *            p_con
     * @throws BTSLBaseException
     */
    private int deleteInsert(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("deleteInsert", "Entered");
        }
        WhiteListDAO whiteListDAO = null;
        int deleteCt = 0;
        int insertCt = 0;
        try {
            whiteListDAO = new WhiteListDAO();
            // First delete all the records in existing table before inserting
            // the new records
            deleteCt = whiteListDAO.batchDeleteWhiteListSubsDetail(p_con, _networkCode, _interfaceID);
            if (deleteCt < 0) {
                throw new BTSLBaseException(this, "deleteInsert", PretupsErrorCodesI.WLIST_ERROR_IN_DELETION_RECORDS);
            }
            // After the successful deletion of the record, insert the records.
            insertCt = whiteListDAO.insertIndWhiteListDetail(p_con, _insertList);
            if (insertCt < 0) {
                throw new BTSLBaseException(this, "deleteInsert", PretupsErrorCodesI.WLIST_ERROR_IN_INSERT_RECORDS);
            }
        }// end of try block
        catch (BTSLBaseException be) {
            _log.error("deleteInsert", "BTSLBaseException be=" + be);
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.error("deleteInsert", "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FileProcess[deleteInsert]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteInsert", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug("deleteInsert", "Exited deleteCt = " + deleteCt);
            }
        }// end of finally
        return insertCt;
    }// end of deleteInsert

    /**
     * This method is used to update,delete and insert the record that are
     * specified in the white list. 1.First Delete the records. 2.Insert Record
     * and check the inserted records is equal to insert list,if not throw
     * exception.Also if the record already exist update the existing record.
     * 3.Update the record present in the update list and if not exist insert
     * it.
     * 
     * @param Connection
     *            p_con
     * @throws BTSLBaseException
     */
    private int updateInsertDelete(Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateInsertDelete", "Entered");
        }
        final String METHOD_NAME = "updateInsertDelete";
        WhiteListDAO whiteListDAO = null;
        int deleteCt = 0;
        int insertCt = 0;
        int updateCt = 0;
        try {
            whiteListDAO = new WhiteListDAO();
            // Check the existence of delete records
            if (!_deleteList.isEmpty()) {
                deleteCt = whiteListDAO.deleteWhiteListSubsDetail(p_con, _deleteList);
                // If deleteCt==0 then it means there are not any rows deleted,
                // or record found to be deleted(Not ERROR).
                if (deleteCt < 0) {
                    throw new BTSLBaseException("FileProcess", "updateInsertDelete", PretupsErrorCodesI.WLIST_ERROR_IN_DELETION_RECORDS);
                }
            }// end of delete list proccessing.

            // Check the existance of insert records
            if (!_insertList.isEmpty()) {
                insertCt = whiteListDAO.insertIndWhiteListDetail(p_con, _insertList);
                if (insertCt < 0) {
                    throw new BTSLBaseException("FileProcess", "updateInsertDelete", PretupsErrorCodesI.WLIST_ERROR_IN_INSERT_RECORDS);
                }
            }// end of insert list proccessing

            // Check the existance of update records.
            if (!_updateList.isEmpty()) {
                updateCt = whiteListDAO.updateWhiteListSubsDetail(p_con, _updateList);
                if (updateCt < 0) {
                    throw new BTSLBaseException("FileProcess", "updateInsertDelete", PretupsErrorCodesI.WLIST_ERROR_IN_UPDATE_RECORDS);
                }
            }// end of update list proccessing
        }// end try block
        catch (BTSLBaseException be) {
            _log.error("updateInsertDelete", "BTSLBaseException be=" + be);
            throw be;
        }// end catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("updateInsertDelete", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FileProcess[updateInsertDelete]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateInsertDelete", e.getMessage());
        }// end catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug("updateInsertDelete", "Exited ");
            }
        }// end of finally
        return deleteCt + insertCt + updateCt;
    }// end of updateInsertDelete

    /**
     * This method is used to parse the file name to get the network code and
     * external interface id. 1.File prefix contains the network code and
     * external interface id. 2.Network code and External interface id is
     * separated by under score.
     * 
     * @throws BTSLBaseException
     */
    private String[] fileNameParser(File p_inputFile) throws BTSLBaseException {
        final String methodName = "fileNameParser";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exited ");
        }
        String[] networkCdExtID = null;// used to contain the external
        // interfaceID at 0th index and network
        // code and 1st index.
        // String fileName=p_inputFile.getName();
        String fileName = null;
        String parseStr = null;
        try {
            fileName = p_inputFile.getName();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "fileName = " + fileName);
            }
            if (fileName == null || "".equals(fileName)) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_INVALID_FILENAME_FORMAT);
            }
            // Check whether file contains the specified separator or not?
            if (!fileName.contains(_fileNameSeparator)) {
                _log.error(methodName, "File name does not contain the separator :: " + _fileNameSeparator);
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_INVALID_FILENAME_FORMAT);
            }
            parseStr = fileName.substring(_filePrefix.length());
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "parseStr = " + parseStr);
            }
            networkCdExtID = parseStr.split("\\" + _fileNameSeparator);
            if (networkCdExtID == null || networkCdExtID.length < 2 || networkCdExtID.length > 3) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.WLIST_ERROR_INVALID_FILENAME_FORMAT);
                // Here if required we can set the default network code and
                // external interfaceID
            }
        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "FileProcess[fileNameParser]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "Error occurs while parsing the prefix of file name");
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exited ");
            }
        }// end of finally
        return networkCdExtID;
    }// end of fileNameParser

    /**
     * Main Method
     * 
     * @param args
     */
    public static void main(String[] args) {
        FileProcess fileProcess = new FileProcess();
        final String methodName = "main";
        try {
            if (args.length != 3) {
                _log.info(methodName, "Usage : FileProcess [Constants file] [LogConfig file] [Process ID]");
                return;
            }
            File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                _log.debug(methodName, "FileProcess main() Constants file not found on location:: " + constantsFile.toString());
                return;
            }
            File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                _log.debug(methodName, "FileProcess main() Logconfig file not found on location:: " + logconfigFile.toString());
                return;
            }
            String processID = args[2];
            if (BTSLUtil.isNullString(processID)) {
                _log.info(methodName, "FileProcess main() " + "Process ID should be given as a input parameter");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            FileCache.loadAtStartUp();
            NetworkPrefixCache.loadNetworkPrefixesAtStartup();
            fileProcess.process(processID);
        } catch (BTSLBaseException be) {
            _log.debug(methodName, "FileProcess main() BTSLBaseException be=" + be.getMessage());
            _log.errorTrace(methodName, be);
        } catch (Exception e) {
            _log.debug(methodName, "FileProcess main() Exception e=" + e.getMessage());
            _log.errorTrace(methodName, e);
        } finally {
            ConfigServlet.destroyProcessCache();
        }
    }
}
