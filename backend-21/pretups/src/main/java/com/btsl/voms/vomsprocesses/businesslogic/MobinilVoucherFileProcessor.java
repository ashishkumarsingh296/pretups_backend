package com.btsl.voms.vomsprocesses.businesslogic;

/**
 * @(#)MobinilVoucherFileProcessor.java
 *                                      Copyright(c) 2006, Bharti Telesoft Int.
 *                                      Public Ltd.
 *                                      All Rights Reserved
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Author Date History
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Siddhartha Srivastava July 4,2006
 *                                      Initial Creation
 *                                      Gurjeet Singh Bedi 21/07/06 Modified
 *                                      (Restructured the code)
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      ----------------
 *                                      This class is responsible to provide
 *                                      following functionalities.
 *                                      1.Reading VoucherUpload file.
 *                                      2.Call the parser class for the parsing
 *                                      and validating of the input file
 *                                      3.Use the VomsVoucherDAO class for
 *                                      uploading the records in the file to the
 *                                      database
 */

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.util.PasswordField;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomslogging.VomsBatchInfoLog;
import com.btsl.voms.vomslogging.VomsVoucherChangeStatusLog;
import com.btsl.voms.vomsprocesses.util.VoucherFileChecksI;
import com.btsl.voms.vomsprocesses.util.VoucherFileUploaderI;
import com.btsl.voms.vomsprocesses.util.VoucherFileUploaderUtil;
import com.btsl.voms.vomsprocesses.util.VoucherUploadVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchesDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class MobinilVoucherFileProcessor {

    private File _destFile; // Destination File object
    private static String _moveLocation; // Input file is moved to this location
                                         // after successfull proccessing
    private static int _maxNoRecordsAllowed = 0;
    private static int _numberOfRecords = 0;
    private static String _productID = null;
    private static String _filePath = null;

    private String _loginID = null;
    private String _password = null;
    private String _fileName = null;
    private ChannelUserVO _channelUserVO = null;
    private static VoucherFileChecksI _parserObj = null;
    private ProcessStatusDAO _processStatusDAO = null;
    private ProcessStatusVO _processStatusVO = null;
    private static Date _currentDate = null;
    private VoucherUploadVO _voucherUploadVO = null;
    private static boolean _directVoucherEnable = false;
    private static Log _log = LogFactory.getLog(MobinilVoucherFileProcessor.class.getName());

    public MobinilVoucherFileProcessor() {
        super();
        _processStatusDAO = new ProcessStatusDAO();
        _currentDate = new Date();
    }

    /**
     * This method is used to create an instance of parser class.This parser
     * class is operator specific
     * 
     * @param String
     *            parserClassName
     * @throws BTSLBaseException
     */
    private void getParserObj(String p_parserClassName) throws BTSLBaseException {
        final String METHOD_NAME = "getParserObj";
        if (_log.isDebugEnabled()) {
            _log.debug("getParserObj ", " Entered with p_parserClassName = " + p_parserClassName);
        }
        try {
            // Creating the instance of parser class.
            _parserObj = (VoucherFileChecksI) Class.forName(p_parserClassName).newInstance();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("getParserObj ", " Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherFileProcessor[getParserObj]", "", "", "", "Exception: The object of the parser class could not be created dynamically");
            throw new BTSLBaseException(this, "getParserObj", PretupsErrorCodesI.VOUCHER_ERROR_PARSER_CLASS_NOT_INSTANTIATED);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug("getParserObj", " Exiting with _parserObj = " + _parserObj);
            }
        }// end of finally
    }// end of getParserObj

    /**
     * The method loads the values for the various properties to be used later
     * in the process from the Constants.props file
     * 
     * @throws BTSLBaseException
     */

    public void loadConstantValues() throws BTSLBaseException {
        final String METHOD_NAME = "loadConstantValues";
        if (_log.isDebugEnabled()) {
            _log.debug("loadConstantValues ", " Entered ");
        }
        try {
            // this reads the number of records allowed in the file.
            if (_log.isDebugEnabled()) {
                _log.debug(" loadConstantValues ", ": reading  _maxNoRecordsAllowed  ");
            }
            try {
                _maxNoRecordsAllowed = Integer.parseInt(Constants.getProperty("VOMS_MAX_FILE_LENGTH"));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error("loadConstantValues ", "Configuration Problem, Parameter VOMS_MAX_FILE_LENGTH not defined properly");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MobinilVoucherFileProcessor[loadConstantValues]", "", "", "", "Configuration Problem, Parameter VOMS_MAX_FILE_LENGTH not defined properly");
                throw new BTSLBaseException("MobinilVoucherFileProcessor", "loadConstantValues", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }

            // this is the path of the input voucher file.
            if (_log.isDebugEnabled()) {
                _log.debug(" loadConstantValues ", ": reading  _filePath ");
            }
            _filePath = BTSLUtil.NullToString(Constants.getProperty("VOMS_VOUCHER_FILE_PATH"));

            // Checking whether the file path provided exist or not.If not,
            // throw an exception
            if (!(new File(_filePath).exists())) {
                _log.error("loadConstantValues ", "Configuration Problem, Parameter VOMS_VOUCHER_FILE_PATH not defined properly _filePath" + _filePath);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MobinilVoucherFileProcessor[loadConstantValues]", "", "", "", "Configuration Problem, Parameter VOMS_VOUCHER_FILE_PATH not defined properly");
                throw new BTSLBaseException("MobinilVoucherFileProcessor", "loadConstantValues", PretupsErrorCodesI.VOUCHER_ERROR_DIR_NOT_EXIST);
            }

            // this is the location where the voucher file will be moved after
            // the vouchers are uploaded
            if (_log.isDebugEnabled()) {
                _log.debug(" loadConstantValues :", " reading _moveLocation ");
            }
            _moveLocation = BTSLUtil.NullToString(Constants.getProperty("VOMS_VOUCHER_FILE_MOVE_PATH"));

            // Destination location where the input file will be moved after
            // successful reading.
            _destFile = new File(_moveLocation);

            // Checking the destination location for the existence. If it does
            // not exist stop the proccess.
            if (!_destFile.exists()) {
                if (_log.isDebugEnabled()) {
                    _log.debug("loadConstantValues ", " Destination Location checking= " + _moveLocation + " does not exist");
                }
                boolean fileCreation = _destFile.mkdirs();
                if (fileCreation) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("loadConstantValues ", " New Location = " + _destFile + "has been created successfully");
                    } else {
                        _log.error("loadConstantsValues ", " Configuration Problem, Could not create the backup directory at the specified location " + _moveLocation);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MobinilVoucherFileProcessor[loadConstantValues]", "", "", "", "Configuration Problem, Could not create the backup directory at the specified location " + _moveLocation);
                        throw new BTSLBaseException("MobinilVoucherFileProcessor", "loadConstantValues", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
                    }
                }
            }

            // Added for Direct Voucher Enabling
            _directVoucherEnable = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DIRECT_VOUCHER_ENABLE))).booleanValue();

        }// end of try block
        catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("loadConstantValues ", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("loadConstantValues ", "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherFileProcessor[loadConstantValues]", "", "", "", "Exception while loading the constants from the Constants.prop file");
            throw new BTSLBaseException("MobinilVoucherFileProcessor", "loadConstantValues", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug("loadConstantValues ", " Exiting: _filePath = " + _filePath + " _moveLocation= " + _moveLocation);
            }
        }// end of finally
    }// end of loadConstantValues

    /**
     * This method retrieves the information interactively from user for userid,
     * password, profile, name of the voucherFile, number of records.
     * 
     * @param args
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void getDetailsFromConsole(Connection p_con, String p_args[]) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "getDetailsFromConsole";
        if (_log.isDebugEnabled()) {
            _log.debug(" getDetailsFromConsole ", " Enetered ");
        }

        try {
            // Get the login ID from the arguments
            _loginID = p_args[2];

            // Get the Password from the arguments
            _password = p_args[3];

            _channelUserVO = VoucherFileUploaderUtil.validateUser(p_con, _loginID, _password, PretupsI.LOCALE_LANGAUGE_EN);
            /*
             * //profile is taken from the user
             * _productID = p_args[4].trim();
             * //_productID =_productID.toUpperCase();
             * 
             * VomsProductDAO vomsProductDAO = new VomsProductDAO();
             * //checks if the particular profile exists or not
             * if(BTSLUtil.isNullString(_productID) ||
             * !vomsProductDAO.isProductExitsVoucherGen
             * (p_con,_productID,VOMSI.VOMS_STATUS_ACTIVE))
             * {
             * System.out.println(" No such product exists in the system........."
             * );
             * _log.error("getDetailsFromConsole"," : Product does not exists"
             * );
             * EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,
             * EventStatusI.RAISED,EventLevelI.MINOR,
             * "MobinilVoucherFileProcessor[getDetailsFromConsole]","","","",
             * "Product ID provided for voucher upload does not exists in the system"
             * );
             * throw new BTSLBaseException("MobinilVoucherFileProcessor",
             * "getDetailsFromConsole"
             * ,PretupsErrorCodesI.VOUCHER_ERROR_PRODUCT_NOT_EXISTS);
             * }
             */
            _fileName = p_args[4];

            // checks the existence of the input Voucher file.If it doesn't
            // exists then false is returned
            if (BTSLUtil.isNullString(_fileName) || !VoucherFileUploaderUtil.isFileExists(_fileName, _filePath)) {
                _log.error("getDetailsFromConsole", " The specified file " + _fileName + "couldn't be found at the specfied location" + _filePath);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MobinilVoucherFileProcessor[getDetailsFromConsole]", "", "", "", "Voucher file (" + _fileName + ") does not exist at the path specified in voucher upload process");
                throw new BTSLBaseException("MobinilVoucherFileProcessor", "getDetailsFromConsole", PretupsErrorCodesI.VOUCHER_ERROR_FILE_DOES_NOT_EXIST);// "The voucher file does not exists at the location specified"
            }

            // takes as input the total number of records in the file above
            try {
                _numberOfRecords = Integer.parseInt(p_args[5]);
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error("getDetailsFromConsole ", "Invalid Total Number of records in file ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherFileProcessor[getDetailsFromConsole]", "", "", "", "Invalid Total Number of records in file");
                throw new BTSLBaseException("MobinilVoucherFileProcessor", "getDetailsFromConsole", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }

            // if the number of records in the file is greater than the number
            // in the config file, it is an error
            if (_maxNoRecordsAllowed < _numberOfRecords) {
                _log.error("getDetailsFromConsole", "Total number of records entered (" + _numberOfRecords + ") should be less than the allowed records in file (" + _maxNoRecordsAllowed + ")");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MobinilVoucherFileProcessor[getDetailsFromConsole]", "", "", "", "Total number of records entered (" + _numberOfRecords + ") should be less than the allowed records in voucher file (" + _maxNoRecordsAllowed + ")");
                throw new BTSLBaseException("MobinilVoucherFileProcessor", "getDetailsFromConsole", PretupsErrorCodesI.VOUCHER_ERROR_NUMBER_REC_MORE_THAN_ALLWD);//
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("getDetailsFromConsole ", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("MobinilVoucherFileProcessor[getDetailsFromConsole]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherFileProcessor[getDetailsFromConsole]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("MobinilVoucherFileProcessor ", " getDetailsFromConsole ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of Exception
    }

    /**
     * This method retrieves the information from the console for userid,
     * password, profile, name of the voucherFile, number of records.
     * 
     * @param args
     * @throws BTSLBaseException
     * @throws Exception
     */
    public void getDetailsInteractively(Connection p_con) throws BTSLBaseException {
        final String methodName = "getDetailsInteractively";
        if (_log.isDebugEnabled()) {
            _log.debug(" getDetailsInteractively", " : Constant Variable Uploaded ................ ");
        }

        try {
            // asks the user to input the loginid
            _loginID = VoucherFileUploaderUtil.dataFromUser(VoucherFileUploaderI.LOGINID);

            // asks the user to input the password field usng the password class
            PasswordField passfield = new PasswordField();
            try {
                _password = passfield.getPassword(VoucherFileUploaderI.PASSWORD);
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                _log.error("getDetailsInteractively ", ": The password could not be retreived ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherFileProcessor[getDetailsInteractively]", "", "", "", "Not able to get input data (Password) from console from user");
                throw new BTSLBaseException("MobinilVoucherFileProcessor", "getDetailsInteractively", PretupsErrorCodesI.VOUCHER_ERROR_PASSWORD_RETREIVAL);
            }

            // validates the user for being network admin. If not throws an
            // exception
            _channelUserVO = VoucherFileUploaderUtil.validateUser(p_con, _loginID, _password, PretupsI.LOCALE_LANGAUGE_EN);
            /*
             * //profile is taken from the user
             * _productID =
             * VoucherFileUploaderUtil.dataFromUser(VoucherFileUploaderI
             * .PRODUCTID);
             * //_productID = _productID.toUpperCase();
             * 
             * VomsProductDAO vomsProductDAO = new VomsProductDAO();
             * 
             * //checks if the particular profile exists or not
             * if(!vomsProductDAO.isProductExitsVoucherGen(p_con,_productID,VOMSI
             * .VOMS_STATUS_ACTIVE))
             * {
             * System.out.println(" No such product exists in the system........."
             * );
             * _log.error("getDetailsInteractively"," : Product does not exists"
             * );
             * EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,
             * EventStatusI.RAISED,EventLevelI.MINOR,
             * "MobinilVoucherFileProcessor[getDetailsInteractively]","","","",
             * "Product ID provided for voucher upload does not exists in the system"
             * );
             * throw new BTSLBaseException("MobinilVoucherFileProcessor",
             * "getDetailsInteractively"
             * ,PretupsErrorCodesI.VOUCHER_ERROR_PRODUCT_NOT_EXISTS);
             * }
             */
            // takes as input the name of the voucher file to be uploaded
            _fileName = VoucherFileUploaderUtil.dataFromUser(VoucherFileUploaderI.FILENAME);

            // checks the existence of the input Voucher file.If it doesn't
            // exists then false is returned
            if (!VoucherFileUploaderUtil.isFileExists(_fileName, _filePath)) {
                _log.debug(methodName, " Entered file (" + _fileName + ") does not exist at the following (" + _filePath + ") specified, please check the file name and the path...............");
                _log.error("getDetailsInteractively", " The specified file " + _fileName + "couldn't be found at the specfied location" + _filePath);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MobinilVoucherFileProcessor[getDetailsInteractively]", "", "", "", "Voucher file (" + _fileName + ") does not exist at the following does not exists at the path specified in voucher upload process");
                throw new BTSLBaseException("MobinilVoucherFileProcessor", "getDetailsInteractively", PretupsErrorCodesI.VOUCHER_ERROR_FILE_DOES_NOT_EXIST);// "The voucher file does not exists at the location specified"
            }

            // takes as input the total number of records in the file above
            _numberOfRecords = VoucherFileUploaderUtil.dataFromUserLong(VoucherFileUploaderI.FILELENGTH);

            // if the number of records in the file is greater than the number
            // in the config file, it is an error
            if (_maxNoRecordsAllowed < _numberOfRecords) {
                _log.debug(methodName, " Total number of records entered (" + _numberOfRecords + ") should be less than the allowed records in file (" + _maxNoRecordsAllowed + ") .............");
                _log.error("getDetailsInteractively", "Total number of records entered (" + _numberOfRecords + ") should be less than the allowed records in file (" + _maxNoRecordsAllowed + ")");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MobinilVoucherFileProcessor[getDetailsInteractively]", "", "", "", "Total number of records entered (" + _numberOfRecords + ") should be less than the allowed records in voucher file (" + _maxNoRecordsAllowed + ")");
                throw new BTSLBaseException("MobinilVoucherFileProcessor", "getDetailsInteractively", PretupsErrorCodesI.VOUCHER_ERROR_NUMBER_REC_MORE_THAN_ALLWD);//
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error("getDetailsInteractively ", "BTSLBaseException be = " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error("MobinilVoucherFileProcessor[getDetailsInteractively]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherFileProcessor[getDetailsInteractively]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("MobinilVoucherFileProcessor ", " getDetailsInteractively ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }
    }

    /**
     * Main Method. This method creates an instance of the class
     * MobinilVoucherFileProcessor. After creating the instance, the methods
     * reads the Constants.prop and ProcessLogConfig file specified as
     * parameter. After successful completion
     * of the above two steps, the method call checkProcessState method on the
     * created instance.After this method
     * call,the method call insertVoucherDetails() which ultimately add voucher
     * details to the voms_voucher table and
     * makes simultaneous entries to voms_batch, voms_batch_summary.
     * 
     * @param args
     */

    public static void main(String[] args) {
        final String methodName = "main";
        long startTime = (new Date()).getTime();
        try {
            int argSize = args.length;

            // This logic uses the number of arguments specified to decide how
            // to retrieve the information
            // for userid, password, profile, name of the voucherFile, number of
            // records.
            if (argSize > 2 && argSize != 6) {
                _log.info(methodName, "Usage : MobinilVoucherFileProcessor [Constants file] [ProcessLogConfig file][loginID][password][fileName][total records in file]");
                _log.error("MobinilVoucherFileProcessor main()", " Usage : MobinilVoucherFileProcessor [Constants file] [ProcessLogConfig file][loginID][password][fileName][total records in file]");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MobinilVoucherFileProcessor[main]", "", "", "", "Improper usage. Usage : MobinilVoucherFileProcessor [Constants file] [ProcessLogConfig file][loginID][password][fileName][total records in file]");
                throw new BTSLBaseException("MobinilVoucherFileProcessor ", " main ", PretupsErrorCodesI.VOUCHER_MISSING_INITIAL_FILES);
            } else if (argSize < 2) {
                _log.info(methodName, "Usage : MobinilVoucherFileProcessor [Constants file] [ProcessLogConfig file]");
                _log.error("MobinilVoucherFileProcessor main()", " Usage : MobinilVoucherFileProcessor [Constants file] [ProcessLogConfig file]");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MobinilVoucherFileProcessor[main]", "", "", "", "Improper usage. Usage : MobinilVoucherFileProcessor [Constants file] [ProcessLogConfig file]");
                throw new BTSLBaseException("MobinilVoucherFileProcessor ", " main ", PretupsErrorCodesI.VOUCHER_MISSING_INITIAL_FILES);
            }
            new MobinilVoucherFileProcessor().process(args);
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error("main", " : Exiting BTSLBaseException " + be.getMessage());
            // be.printStackTrace();
        } catch (Exception e) {
            _log.error("main ", ": Exiting Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherFileProcessor: main", "", "", "", "Exiting the exception of main");
            _log.errorTrace(methodName, e);
        }// end of outer Exception
        finally {
            long endTime = (new Date()).getTime();
            _log.debug(methodName, "Main method Exiting .......Time taken in Milli seconds=" + (endTime - startTime));
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * This is the main method that is controlling the flow of the process
     * 
     * @param p_args
     * @throws BTSLBaseException
     */
    private void process(String[] p_args) throws BTSLBaseException {
        final String methodName = "process";
        Connection con = null;
        ProcessBL processBL = new ProcessBL();
        boolean processStatusOK = false;
        ArrayList batchList = null;
        try {
            int argSize = p_args.length;
            VoucherFileUploaderUtil.loadCachesAndLogFiles(p_args[0], p_args[1]);

            // opening the connection
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                _log.info(methodName, " Could not connect to database. Please make sure that database server is up..............");
                _log.error("main ", ": Could not connect to database. Please make sure that database server is up..............");
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileUploader[process]", "", "", "", "Could not connect to Database");
                throw new BTSLBaseException("MobinilVoucherFileProcessor", "main", PretupsErrorCodesI.VOUCHER_ERROR_CONN_NULL);
            }

            _processStatusVO = processBL.checkProcessUnderProcess(con, ProcessI.VOUCHER_FILE_UPLOAD_PROCCESSID);

            if (!(_processStatusVO != null && _processStatusVO.isStatusOkBool())) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
            }
            processStatusOK = _processStatusVO.isStatusOkBool();
            // Commiting the status of process status as 'U-Under Process'.
            con.commit();

            // loading all the constant variables;
            loadConstantValues();

            if (argSize == 2) {
                getDetailsInteractively(con);
            } else if (argSize == 6) {
                getDetailsFromConsole(con, p_args);
            }

            // creates an instance of the parser class based on the entry in the
            // Constants file
            getParserObj(Constants.getProperty("VOUCHER_PARSER_CLASS"));

            // this method will be used for validation of the Voucher file
            validateInputFile();

            if (_voucherUploadVO.getVoucherArrayList() == null || _voucherUploadVO.getVoucherArrayList().isEmpty()) {
                _log.error("MobinilVoucherFileProcessor[process]", " No voucher for adding in file");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MobinilVoucherFileProcessor[process]", "", "", "", " No vouchers for adding in file");
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_NO_RECORDS_ERROR);
            }
            VomsBatchVO vomsBatchVO = null;
            VoucherUploadVO voucherUploadVO = null;
            ArrayList voucherUploadArr = _voucherUploadVO.getVoucherArrayList();
            int actualNoOfRecords = _voucherUploadVO.getActualNoOfRecords();
            for (int i = 0; i < actualNoOfRecords; i++) {
                voucherUploadVO = (VoucherUploadVO) voucherUploadArr.get(i);
                vomsBatchVO = prepareVomsBatchesVO(VOMSI.BATCH_GENERATED, false, null, voucherUploadVO);
                batchList = new ArrayList();
                batchList.add(vomsBatchVO);

                int recordCount = 0;
                recordCount = new VomsBatchesDAO().addBatch(con, batchList);
                if (recordCount <= 0) {
                    _log.error("MobinilVoucherFileProcessor[process]", " Not able to insert batches ");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MobinilVoucherFileProcessor[process]", "", "", "", " The batch entry could not be made into the table");
                    throw new BTSLBaseException("MobinilVoucherFileProcessor ", " process ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
                }

                // For Direct enabling of Vouchers
                VomsVoucherVO voucherVO = null;
                ArrayList voucherLogList = new ArrayList();
                VomsBatchVO enableBatchVO = null;
                if (_directVoucherEnable) {
                    enableBatchVO = prepareVomsBatchesVO(VOMSI.BATCH_ENABLED, true, vomsBatchVO, voucherUploadVO);
                    batchList = new ArrayList();
                    batchList.add(enableBatchVO);
                    recordCount = new VomsBatchesDAO().addBatch(con, batchList);
                    if (recordCount <= 0) {
                        _log.error("MobinilVoucherFileProcessor[process]", " Not able to insert batches ");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MobinilVoucherFileProcessor[process]", "", "", "", " The batch entry could not be made into the table");
                        throw new BTSLBaseException("MobinilVoucherFileProcessor ", " process ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
                    }
                    ArrayList voucherList = voucherUploadVO.getVoucherArrayList();
                    int s = voucherList.size();
                    VomsVoucherVO vomsVoucherVO = null;
                    for (int counter = 0; counter < s; counter++) {
                        vomsVoucherVO = (VomsVoucherVO) voucherList.get(counter);
                        vomsVoucherVO.setEnableBatchNo(enableBatchVO.getBatchNo());
                        vomsVoucherVO.setStatus(VOMSI.VOUCHER_ENABLE);
                        vomsVoucherVO.setCurrentStatus(VOMSI.VOUCHER_ENABLE);
                        vomsVoucherVO.setPreviousStatus(VOMSI.VOUCHER_NEW);
                    }
                    voucherVO = new VomsVoucherVO();
                    voucherVO.setEnableBatchNo(enableBatchVO.getBatchNo());
                    voucherVO.setSerialNo(enableBatchVO.getFromSerialNo());
                    voucherVO.setToSerialNo(enableBatchVO.getToSerialNo());
                    voucherVO.setPreviousStatus(VOMSI.VOUCHER_NEW);
                    voucherVO.setPrevStatusModifiedBy(enableBatchVO.getModifiedBy());
                    voucherVO.setPrevStatusModifiedOn(BTSLUtil.getDateStringFromDate(enableBatchVO.getModifiedOn()));
                    voucherVO.setVoucherStatus(VOMSI.VOUCHER_ENABLE);
                    voucherVO.setStatusChangeSource(VOMSI.VOUCHER_ENABLE);
                    voucherVO.setMRP(0);
                    voucherVO.setExpiryDateStr("");
                    voucherVO.setLastErrorMessage(BTSLUtil.NullToString(enableBatchVO.getMessage()));
                    voucherVO.setProcess("1");
                    voucherVO.setProductionLocationCode(enableBatchVO.getLocationCode());
                    voucherLogList.add(voucherVO); // adding the last entry
                }
                VomsVoucherDAO vomsVoucherDAO = new VomsVoucherDAO();
                recordCount = vomsVoucherDAO.insertVouchers(con, vomsBatchVO, voucherUploadVO.getVoucherArrayList(), _directVoucherEnable, true);
                if (recordCount <= 0) {
                    _log.error("MobinilVoucherFileProcessor[process]", " Not able to insert Vouchers ");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MobinilVoucherFileProcessor[process]", "", "", "", " The vouchers could not be inserted into the table");
                    throw new BTSLBaseException("MobinilVoucherFileProcessor ", " process ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
                }

                recordCount = 0;
                recordCount = vomsVoucherDAO.updateSummaryTable(con, vomsBatchVO, _directVoucherEnable);
                if (recordCount <= 0) {
                    _log.error("MobinilVoucherFileProcessor[process]", " Not able to update Summary tables ");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MobinilVoucherFileProcessor[process]", "", "", "", " The batch summary table could not be updated");
                    throw new BTSLBaseException("MobinilVoucherFileProcessor ", " process ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
                }
                if (_directVoucherEnable) {
                    VomsVoucherChangeStatusLog.log(voucherLogList);
                    VomsBatchInfoLog.modifyBatchLog(enableBatchVO);
                    // validating for number of actual records inserted in
                    // voms_voucher.On the basis of records inserted, successful
                    // uploading of file is determined
                    _log.debug(methodName, "Voucher Upload Process Successfully Executed.Batch Successfully Enabled. Your Batch number for the process is = " + enableBatchVO.getBatchNo());
                    if (_log.isDebugEnabled()) {
                        _log.debug(" process", "Voucher Upload Process Successfully Executed.Batch Successfully Enabled. Your Batch number for the process is = " + enableBatchVO.getBatchNo());
                    }
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MobinilVoucherFileProcessor[process]", "", "", "", "Voucher Upload Process Successfully Executed.Batch Successfully Enabled. Your Batch number for the process is = " + enableBatchVO.getBatchNo());
                } else {
                    // validating for number of actual records inserted in
                    // voms_voucher.On the basis of records inserted, successful
                    // uploading of file is determined
                    _log.debug(methodName, "Voucher Upload Process Successfully Executed.Batch Successfully Generated. Your Batch number for the process is = " + vomsBatchVO.getBatchNo());
                    if (_log.isDebugEnabled()) {
                        _log.debug(" process", "Voucher Upload Process Successfully Executed.Batch Successfully Generated. Your Batch number for the process is = " + vomsBatchVO.getBatchNo());
                    }
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MobinilVoucherFileProcessor[process]", "", "", "", "Voucher Upload Process Successfully Executed.Batch Successfully Generated. Your Batch number for the process is = " + vomsBatchVO.getBatchNo());
                }
            }
            con.commit();

            // name of the file after moving
            VoucherFileUploaderUtil.moveFileToAnotherDirectory(_fileName, _filePath + File.separator + _fileName, _moveLocation);

            VomsBatchInfoLog.modifyBatchLog(vomsBatchVO);
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.info(methodName, "Voucher Upload Process Failed .Check the log file or OAM Screen or Event log for exact errors ");
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    _log.errorTrace(methodName, e1);
                }
            }
            throw be;
        } catch (Exception e) {
            _log.info(methodName, "Voucher Upload Process Failed .Check the log file or OAM Screen or Event log for exact errors ");
            try {
                con.rollback();
            } catch (SQLException e1) {
                _log.errorTrace(methodName, e1);
            }
            _log.errorTrace(methodName, e);
            _log.error("MobinilVoucherFileProcessor[process]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherFileProcessor[process]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("MobinilVoucherFileProcessor ", " process ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            try {
                // Setting the process status as 'C-Complete' if the
                // processStatusOK is true
                if (processStatusOK) {
                    Date date = new Date();
                    _processStatusVO.setStartDate(_processStatusVO.getStartDate());
                    _processStatusVO.setExecutedOn(date);
                    _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    int successU = _processStatusDAO.updateProcessDetail(con, _processStatusVO);

                    // Commiting the process status as 'C-Complete'
                    if (successU > 0) {
                        con.commit();
                    } else {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherFileProcessor[process]", "processStatusVO.getProcessID()" + ProcessI.VOUCHER_FILE_UPLOAD_PROCCESSID, "", "", "Error while updating the process status after completing the process");
            
                    }
                }// end of IF-Checks the proccess status
            }// end of try-block
            catch (BTSLBaseException be) {
                _log.errorTrace(methodName, be);
                _log.error("process", "BTSLBaseException be= " + be.getMessage());
                if (con != null) {
                    try {
                        con.rollback();
                    } catch (Exception e1) {
                        _log.errorTrace(methodName, e1);
                    }

                }
            }// end of catch-BTSLBaseException
            catch (Exception e) {
                _log.errorTrace(methodName, e);
                _log.error("process", "Exception e= " + e.getMessage());
                if (con != null) {
                    try {
                        con.rollback();
                    } catch (Exception e1) {
                        _log.errorTrace(methodName, e1);
                    }
                }
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherFileProcessor[process]", "processStatusVO.getProcessID()" + ProcessI.VOUCHER_FILE_UPLOAD_PROCCESSID, "", "", "BaseException:" + e.getMessage());

            }// end of catch-Exception
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e1) {
                    _log.errorTrace(methodName, e1);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Exiting ");
            }
        }
    }

    /**
     * This method will validate the input file specified by the user.
     * Validation logic is in Parser class
     * 
     * @throws BTSLBaseException
     */
    private void validateInputFile() throws BTSLBaseException {
        final String METHOD_NAME = "validateInputFile";
        if (_log.isDebugEnabled()) {
            _log.debug(" validateInputFile", " Entered................ ");
        }
        try {
            // call the loadConstantValues() to load the values.
            _parserObj.loadConstantValues();

            populateVoucherUploadVO();

            // to validate the file length against the uesr input file length
            _parserObj.getFileLength(_voucherUploadVO);

            // called to validate the file, whole file at the time.
            _voucherUploadVO = _parserObj.validateVoucherFile();
            _productID = _voucherUploadVO.getProductID();

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("validateInputFile", "BTSLBaseException be= " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("MobinilVoucherFileProcessor[validateInputFile]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherFileProcessor[validateInputFile]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("MobinilVoucherFileProcessor ", " validateInputFile ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(" validateInputFile", " Exiting................ ");
        }
    }

    /**
     * This method populates the Voucher Upload VO that will be given to Parser
     * class
     * 
     * @throws BTSLBaseException
     */
    private void populateVoucherUploadVO() throws BTSLBaseException {
        final String METHOD_NAME = "populateVoucherUploadVO";
        if (_log.isDebugEnabled()) {
            _log.debug(" populateVoucherUploadVO", " Entered................_fileName=" + _fileName + ", _filePath=" + _filePath);
        }
        try {
            _voucherUploadVO = new VoucherUploadVO();
            _voucherUploadVO.setFileName(_fileName);
            _voucherUploadVO.setFilePath(_filePath);
            _voucherUploadVO.setProductID(_productID);
            _voucherUploadVO.setNoOfRecordsInFile(_numberOfRecords);
            _voucherUploadVO.setMaxNoOfRecordsAllowed(_maxNoRecordsAllowed);
            _voucherUploadVO.setChannelUserVO(_channelUserVO);
            _voucherUploadVO.setCurrentDate(_currentDate);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("MobinilVoucherFileProcessor[populateVoucherUploadVO]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherFileProcessor[populateVoucherUploadVO]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("MobinilVoucherFileProcessor ", " populateVoucherUploadVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" populateVoucherUploadVO", " Exiting................ ");
            }
        }
    }

    /**
     * It prepares Voms Batches VO for insertion in Database
     * 
     * @return
     * @throws BTSLBaseException
     */
    private VomsBatchVO prepareVomsBatchesVO(String p_batchType, boolean p_isDirectEnable, VomsBatchVO p_vomsBatchVO, VoucherUploadVO p_voucherUploadVO) throws BTSLBaseException {
        final String METHOD_NAME = "prepareVomsBatchesVO";
        if (_log.isDebugEnabled()) {
            _log.debug(" prepareVomsBatchesVO", " Entered with p_batchType=" + p_batchType + " p_isDirectEnable=" + p_isDirectEnable + ", _productID=" + _productID);
        }

        VomsBatchVO vomsBatchVO = null;
        String batchNo = null;
        try {
            vomsBatchVO = new VomsBatchVO();
            batchNo = String.valueOf(IDGenerator.getNextID(VOMSI.VOMS_BATCHES_DOC_TYPE, String.valueOf(BTSLUtil.getFinancialYear()), VOMSI.ALL));

            vomsBatchVO.setProductID(p_voucherUploadVO.getProductID());
            vomsBatchVO.setBatchType(p_batchType);
            vomsBatchVO.setNoOfVoucher(p_voucherUploadVO.getActualNoOfRecords());
            vomsBatchVO.setFromSerialNo(p_voucherUploadVO.getFromSerialNo());
            vomsBatchVO.setToSerialNo(String.valueOf(p_voucherUploadVO.getToSerialNo()));
            vomsBatchVO.setFailCount(p_voucherUploadVO.getActualNoOfRecords() - (p_voucherUploadVO.getVoucherArrayList()).size());// for
                                                                                                                                  // setting
                                                                                                                                  // the
                                                                                                                                  // no.
                                                                                                                                  // of
                                                                                                                                  // failed
                                                                                                                                  // records
            vomsBatchVO.setOneTimeUsage(PretupsI.YES);
            vomsBatchVO.setSuccessCount(p_voucherUploadVO.getActualNoOfRecords());
            vomsBatchVO.setLocationCode(_channelUserVO.getNetworkID());
            vomsBatchVO.setCreatedBy(_channelUserVO.getUserID());
            vomsBatchVO.setCreatedOn(_currentDate);
            vomsBatchVO.setBatchNo(new VomsUtil().formatVomsBatchID(vomsBatchVO, batchNo));
            vomsBatchVO.setModifiedBy(_channelUserVO.getUserID());
            vomsBatchVO.setModifiedOn(_currentDate);
            vomsBatchVO.setDownloadCount(1);
            vomsBatchVO.setStatus(VOMSI.EXECUTED);
            vomsBatchVO.setCreatedDate(_currentDate);
            vomsBatchVO.setModifiedDate(_currentDate);
            vomsBatchVO.setProcess(VOMSI.BATCH_PROCESS_GEN);
            vomsBatchVO.setMessage(" Batch SuccessFully Executed ...........");

            if (p_isDirectEnable) {
                vomsBatchVO.setReferenceNo(p_vomsBatchVO.getBatchNo());
                vomsBatchVO.setReferenceType(p_vomsBatchVO.getBatchType());
                vomsBatchVO.setTotalVoucherPerOrder(0);
                vomsBatchVO.setDownloadCount(0);
                vomsBatchVO.setProcess(VOMSI.BATCH_PROCESS_ENABLE);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("prepareVomsBatchesVO", "BTSLBaseException be= " + be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("MobinilVoucherFileProcessor[prepareVomsBatchesVO]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherFileProcessor[prepareVomsBatchesVO]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("MobinilVoucherFileProcessor ", " prepareVomsBatchesVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" prepareVomsBatchesVO", " Exiting with vomsBatchVO= " + vomsBatchVO);
            }
        }
        return vomsBatchVO;
    }
}
