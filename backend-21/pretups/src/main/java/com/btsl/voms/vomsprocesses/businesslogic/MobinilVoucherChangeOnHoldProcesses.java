package com.btsl.voms.vomsprocesses.businesslogic;

/**
 * @(#)MobinilVoucherChangeOnHoldProcesses.java
 *                                              Copyright(c) 2011, Comviva.
 *                                              All Rights Reserved
 *                                              --------------------------------
 *                                              --------------------------------
 *                                              --
 *                                              -------------------------------
 *                                              Author Date History
 *                                              --------------------------------
 *                                              --------------------------------
 *                                              --
 *                                              -------------------------------
 *                                              SSanjeew Kumar Dec 25,2011
 *                                              Initial Creation
 *                                              --------------------------------
 *                                              --------------------------------
 *                                              --------------------------------
 *                                              This class is responsible to
 *                                              provide following
 *                                              functionalities.
 *                                              1.Reading Voucher file.
 *                                              2.Parsing the file
 *                                              3.Validating the records of file
 *                                              and then changing status of
 *                                              voucher from on-Hold status to
 *                                              enable or stolen or damaged
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.util.PasswordField;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsprocesses.util.VoucherFileUploaderI;
import com.btsl.voms.vomsprocesses.util.VoucherFileUploaderUtil;
import com.btsl.voms.vomsprocesses.util.VoucherUploadVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class MobinilVoucherChangeOnHoldProcesses {

    private File _destFile; // Destination File object
    private static String _moveLocation; // Input file is moved to this location
                                         // after successfull proccessing
    private static int _maxNoRecordsAllowed = 0;
    private static int _numberOfRecords = 0;
    private static String _filePath = null;

    private String _loginID = null;
    private String _password = null;
    private String _fileName = null;
    private ChannelUserVO _channelUserVO = null;
    private static Date _currentDate = null;
    private VoucherUploadVO _voucherUploadVO = null;
    private ArrayList _fileDataArr = null;
    private ArrayList _voucherArr = null;
    private int _allowedNumberofErrors = 0;
    private String _valueSeparator = null;
    private static Log _log = LogFactory.getLog(MobinilVoucherChangeOnHoldProcesses.class.getName());

    public MobinilVoucherChangeOnHoldProcesses() {
        super();
        _currentDate = new Date();
    }

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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MobinilVoucherChangeOnHoldProcesses[loadConstantValues]", "", "", "", "Configuration Problem, Parameter VOMS_MAX_FILE_LENGTH not defined properly");
                throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", "loadConstantValues", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MobinilVoucherChangeOnHoldProcesses[loadConstantValues]", "", "", "", "Configuration Problem, Parameter VOMS_VOUCHER_FILE_PATH not defined properly");
                throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", "loadConstantValues", PretupsErrorCodesI.VOUCHER_ERROR_DIR_NOT_EXIST);
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
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MobinilVoucherChangeOnHoldProcesses[loadConstantValues]", "", "", "", "Configuration Problem, Could not create the backup directory at the specified location " + _moveLocation);
                        throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", "loadConstantValues", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
                    }
                }
            }
            try {
                _allowedNumberofErrors = Integer.parseInt(Constants.getProperty("VOMS_TOTAL_ALLOWED_ERRORS"));
                if (_log.isDebugEnabled()) {
                    _log.debug("MobinilVoucherChangeOnHoldProcesses ", " loadConstantValues   VOMS_TOTAL_ALLOWED_ERRORS " + _allowedNumberofErrors);
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _allowedNumberofErrors = 0;
                _log.info("loadConstantValues", " Total number of error (Entry VOMS_TOTAL_ALLOWED_ERRORS) not found in Constants . Thus taking default values as 0");
            }

            // this is used to seperate the different fields in a single record
            _valueSeparator = Constants.getProperty("VOMS_FILE_SEPARATOR");

            if (BTSLUtil.isNullString(_valueSeparator)) {
                _log.info("loadConstantValues", " Entry VOMS_FILE_SEPARATOR is defined blank in Constants . Thus taking default values as \"");
                _valueSeparator = ",";
            }

        }// end of try block
        catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("loadConstantValues ", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("loadConstantValues ", "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherChangeOnHoldProcesses[loadConstantValues]", "", "", "", "Exception while loading the constants from the Constants.prop file");
            throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", "loadConstantValues", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
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

            _fileName = p_args[4];

            // checks the existence of the input Voucher file.If it doesn't
            // exists then false is returned
            if (BTSLUtil.isNullString(_fileName) || !VoucherFileUploaderUtil.isFileExists(_fileName, _filePath)) {

                _log.error("getDetailsFromConsole", " The specified file " + _fileName + "couldn't be found at the specfied location" + _filePath);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MobinilVoucherChangeOnHoldProcesses[getDetailsFromConsole]", "", "", "", "Voucher file (" + _fileName + ") does not exist at the path specified in voucher upload process");
                throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", "getDetailsFromConsole", PretupsErrorCodesI.VOUCHER_ERROR_FILE_DOES_NOT_EXIST);// "The voucher file does not exists at the location specified"
            }
            // takes as input the total number of records in the file above
            try {
                _numberOfRecords = Integer.parseInt(p_args[5]);
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error("getDetailsFromConsole ", "Invalid Total Number of records in file ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherChangeOnHoldProcesses[getDetailsFromConsole]", "", "", "", "Invalid Total Number of records in file");
                throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", "getDetailsFromConsole", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            // if the number of records in the file is greater than the number
            // in the config file, it is an error
            if (_maxNoRecordsAllowed < _numberOfRecords) {
                _log.error("getDetailsFromConsole", "Total number of records entered (" + _numberOfRecords + ") should be less than the allowed records in file (" + _maxNoRecordsAllowed + ")");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MobinilVoucherChangeOnHoldProcesses[getDetailsFromConsole]", "", "", "", "Total number of records entered (" + _numberOfRecords + ") should be less than the allowed records in voucher file (" + _maxNoRecordsAllowed + ")");
                throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", "getDetailsFromConsole", PretupsErrorCodesI.VOUCHER_ERROR_NUMBER_REC_MORE_THAN_ALLWD);//
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("getDetailsFromConsole ", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("MobinilVoucherChangeOnHoldProcesses[getDetailsFromConsole]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherChangeOnHoldProcesses[getDetailsFromConsole]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses ", " getDetailsFromConsole ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
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
        final String METHOD_NAME = "getDetailsInteractively";
        if (_log.isDebugEnabled()) {
            _log.debug(" getDetailsInteractively", " : Constant Variable Uploaded ................ ");
        }

        try {
            // asks the user to input the loginid
            _loginID = VoucherFileUploaderUtil.dataFromUser(VoucherFileUploaderI.LOGINID);

            // asks the user to input the password field usng the password class
            PasswordField passfield = new PasswordField();
            final String methodName = "getDetailsInteractively";
            try {
                _password = passfield.getPassword(VoucherFileUploaderI.PASSWORD);
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error("getDetailsInteractively ", ": The password could not be retreived ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherChangeOnHoldProcesses[getDetailsInteractively]", "", "", "", "Not able to get input data (Password) from console from user");
                throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", methodName, PretupsErrorCodesI.VOUCHER_ERROR_PASSWORD_RETREIVAL);
            }

            // validates the user for being network admin. If not throws an
            // exception
            _channelUserVO = VoucherFileUploaderUtil.validateUser(p_con, _loginID, _password, PretupsI.LOCALE_LANGAUGE_EN);

            // takes as input the name of the voucher file to be uploaded
            _fileName = VoucherFileUploaderUtil.dataFromUser(VoucherFileUploaderI.FILENAME);

            // checks the existence of the input Voucher file.If it doesn't
            // exists then false is returned
            if (!VoucherFileUploaderUtil.isFileExists(_fileName, _filePath)) {
                _log.debug(methodName, " Entered file (" + _fileName + ") does not exist at the following (" + _filePath + ") specified, please check the file name and the path...............");
                _log.error(methodName, " The specified file " + _fileName + "couldn't be found at the specfied location" + _filePath);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MobinilVoucherChangeOnHoldProcesses[getDetailsInteractively]", "", "", "", "Voucher file (" + _fileName + ") does not exist at the following does not exists at the path specified in voucher upload process");
                throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", methodName, PretupsErrorCodesI.VOUCHER_ERROR_FILE_DOES_NOT_EXIST);// "The voucher file does not exists at the location specified"
            }

            // takes as input the total number of records in the file above
            _numberOfRecords = VoucherFileUploaderUtil.dataFromUserLong(VoucherFileUploaderI.FILELENGTH);

            // if the number of records in the file is greater than the number
            // in the config file, it is an error
            if (_maxNoRecordsAllowed < _numberOfRecords) {
                _log.error(methodName, "Total number of records entered (" + _numberOfRecords + ") should be less than the allowed records in file (" + _maxNoRecordsAllowed + ")");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MobinilVoucherChangeOnHoldProcesses[getDetailsInteractively]", "", "", "", "Total number of records entered (" + _numberOfRecords + ") should be less than the allowed records in voucher file (" + _maxNoRecordsAllowed + ")");
                throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", methodName, PretupsErrorCodesI.VOUCHER_ERROR_NUMBER_REC_MORE_THAN_ALLWD);//
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("getDetailsInteractively ", "BTSLBaseException be = " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("MobinilVoucherChangeOnHoldProcesses[getDetailsInteractively]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherChangeOnHoldProcesses[getDetailsInteractively]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses ", " getDetailsInteractively ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }
    }

    /**
     * Main Method. This method creates an instance of the class
     * MobinilVoucherChangeOnHoldProcesses. After creating the instance, the
     * methods
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
        long startTime = (new Date()).getTime();
        final String methodName = "main";
        try {
            int argSize = args.length;

            // This logic uses the number of arguments specified to decide how
            // to retrieve the information
            // for userid, password, profile, name of the voucherFile, number of
            // records.
            if (argSize > 2 && argSize != 6) {
                _log.info(methodName, "Usage : MobinilVoucherChangeOnHoldProcesses [Constants file] [ProcessLogConfig file][loginID][password][fileName][total records in file]");
                _log.error("MobinilVoucherChangeOnHoldProcesses main()", " Usage : MobinilVoucherChangeOnHoldProcesses [Constants file] [ProcessLogConfig file][loginID][password][fileName][total records in file]");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MobinilVoucherChangeOnHoldProcesses[main]", "", "", "", "Improper usage. Usage : MobinilVoucherChangeOnHoldProcesses [Constants file] [ProcessLogConfig file][loginID][password][fileName][total records in file]");
                throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses ", " main ", PretupsErrorCodesI.VOUCHER_MISSING_INITIAL_FILES);
            } else if (argSize < 2) {
                _log.info(methodName, "Usage : MobinilVoucherChangeOnHoldProcesses [Constants file] [ProcessLogConfig file]");
                _log.error("MobinilVoucherChangeOnHoldProcesses main()", " Usage : MobinilVoucherChangeOnHoldProcesses [Constants file] [ProcessLogConfig file]");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MobinilVoucherChangeOnHoldProcesses[main]", "", "", "", "Improper usage. Usage : MobinilVoucherChangeOnHoldProcesses [Constants file] [ProcessLogConfig file]");
                throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses ", " main ", PretupsErrorCodesI.VOUCHER_MISSING_INITIAL_FILES);
            }
            new MobinilVoucherChangeOnHoldProcesses().process(args);
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error(methodName, " : Exiting BTSLBaseException " + be.getMessage());
            // be.printStackTrace();
        } catch (Exception e) {
            _log.error("main ", ": Exiting Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherChangeOnHoldProcesses: main", "", "", "", "Exiting the exception of main");
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
        Connection con = null;
        PreparedStatement pstmtUpdateVoucher = null;
        PreparedStatement pstmtUpdateAudit = null;
        int updateCount = 0;
        final String methodName = "process";
        try {
            int argSize = p_args.length;
            VoucherFileUploaderUtil.loadCachesAndLogFiles(p_args[0], p_args[1]);

            // opening the connection
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                _log.info(methodName, " Could not connect to database. Please make sure that database server is up..............");
                _log.error("main ", ": Could not connect to database. Please make sure that database server is up..............");
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileUploader[process]", "", "", "", "Could not connect to Database");
                throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", "main", PretupsErrorCodesI.VOUCHER_ERROR_CONN_NULL);
            }
            // loading all the constant variables;
            loadConstantValues();

            if (argSize == 2) {
                getDetailsInteractively(con);
            } else if (argSize == 6) {
                getDetailsFromConsole(con, p_args);
            }
            // this method will be used for validation of the Voucher file
            validateInputFile();

            if (_voucherUploadVO.getVoucherArrayList() == null || _voucherUploadVO.getVoucherArrayList().isEmpty()) {
                _log.error("MobinilVoucherChangeOnHoldProcesses[process]", " No voucher for adding in file");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MobinilVoucherChangeOnHoldProcesses[process]", "", "", "", " No vouchers for adding in file");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_NO_RECORDS_ERROR);
            }

            StringBuffer updateVoucherQueryBuff = new StringBuffer(" UPDATE voms_vouchers SET   ");
            updateVoucherQueryBuff.append(" current_status=?, previous_status=?, status=?, ");
            updateVoucherQueryBuff.append(" modified_by=?, modified_on=? ");
            updateVoucherQueryBuff.append(" WHERE serial_no=? and current_status=? ");
            String updateVoucherQuery = updateVoucherQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("MobinilVoucherChangeOnHoldProcesses[process]", "Update query:" + updateVoucherQuery);
            }
            pstmtUpdateVoucher = con.prepareStatement(updateVoucherQuery);

            StringBuffer updateAuditQueryBuff = new StringBuffer(" UPDATE voms_voucher_audit SET   ");
            updateAuditQueryBuff.append(" current_status=?, previous_status=?, ");
            updateAuditQueryBuff.append(" modified_by=?, modified_on=? ");
            updateAuditQueryBuff.append(" WHERE serial_no=? and current_status=? ");
            String updateAuditQuery = updateAuditQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("MobinilVoucherChangeOnHoldProcesses[process]", "Update query:" + updateAuditQuery);
            }
            pstmtUpdateAudit = con.prepareStatement(updateAuditQuery);

            ArrayList voucherUploadArr = _voucherUploadVO.getVoucherArrayList();
            VomsVoucherVO vomsVoucherVO = null;
            int   actualNoOfRecords = _voucherUploadVO.getActualNoOfRecords();
            for (int i = 0, j = 0; i < actualNoOfRecords; i++) {
                j = 1;
                pstmtUpdateVoucher.clearParameters();
                vomsVoucherVO = (VomsVoucherVO) voucherUploadArr.get(i);
                pstmtUpdateVoucher.setString(j++, vomsVoucherVO.getCurrentStatus());
                pstmtUpdateVoucher.setString(j++, VOMSI.VOUCHER_REP_ON_HOLD);
                pstmtUpdateVoucher.setString(j++, vomsVoucherVO.getStatus());
                pstmtUpdateVoucher.setString(j++, vomsVoucherVO.getModifiedBy());
                pstmtUpdateVoucher.setTimestamp(j++, BTSLUtil.getTimestampFromUtilDate(vomsVoucherVO.getModifiedOn()));
                pstmtUpdateVoucher.setString(j++, vomsVoucherVO.getSerialNo());
                pstmtUpdateVoucher.setString(j++, VOMSI.VOUCHER_REP_ON_HOLD);
                updateCount = pstmtUpdateVoucher.executeUpdate();

                if (updateCount > 0) {
                    updateCount = 0;
                    j = 1;
                    pstmtUpdateAudit.clearParameters();
                    pstmtUpdateAudit.setString(j++, vomsVoucherVO.getCurrentStatus());
                    pstmtUpdateAudit.setString(j++, VOMSI.VOUCHER_REP_ON_HOLD);
                    pstmtUpdateAudit.setString(j++, vomsVoucherVO.getModifiedBy());
                    pstmtUpdateAudit.setTimestamp(j++, BTSLUtil.getTimestampFromUtilDate(vomsVoucherVO.getModifiedOn()));
                    pstmtUpdateAudit.setString(j++, vomsVoucherVO.getSerialNo());
                    pstmtUpdateAudit.setString(j++, VOMSI.VOUCHER_REP_ON_HOLD);
                    updateCount = pstmtUpdateAudit.executeUpdate();
                } else {
                    _log.error("MobinilVoucherChangeOnHoldProcesses[process]", " unable to update voucher in voms_voucher tables SerialNo=" + vomsVoucherVO.getSerialNo() + " Changed to status:" + vomsVoucherVO.getCurrentStatus());
                    continue;
                }
            }
            con.commit();
            // name of the file after moving
            VoucherFileUploaderUtil.moveFileToAnotherDirectory(_fileName, _filePath + File.separator + _fileName, _moveLocation);
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
            _log.error("MobinilVoucherChangeOnHoldProcesses[process]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherChangeOnHoldProcesses[process]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses ", " process ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            try {
                if (pstmtUpdateVoucher != null) {
                    pstmtUpdateVoucher.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateAudit != null) {
                    pstmtUpdateAudit.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e1) {
                    _log.errorTrace(methodName, e1);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting ");
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
            populateVoucherUploadVO();

            // to validate the file length against the uesr input file length
            getFileLength();

            // called to validate the file, whole file at the time.
            validateVoucherFile();
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("validateInputFile", "BTSLBaseException be= " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("MobinilVoucherChangeOnHoldProcesses[validateInputFile]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherChangeOnHoldProcesses[validateInputFile]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses ", " validateInputFile ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
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
            _voucherUploadVO.setNoOfRecordsInFile(_numberOfRecords);
            _voucherUploadVO.setMaxNoOfRecordsAllowed(_maxNoRecordsAllowed);
            _voucherUploadVO.setChannelUserVO(_channelUserVO);
            _voucherUploadVO.setCurrentDate(_currentDate);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("MobinilVoucherChangeOnHoldProcesses[populateVoucherUploadVO]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherChangeOnHoldProcesses[populateVoucherUploadVO]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses ", " populateVoucherUploadVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" populateVoucherUploadVO", " Exiting................ ");
            }
        }
    }

    /**
     * This method counts the number of records in the file and simultaneously
     * stores the record in the
     * arraylist for future processing of the records.
     * 
     * @param VoucherUploadVO
     *            -- this VO stores all the details contained in the
     *            Constants.prop and also all the
     *            user entered details related to the process.
     * @return void
     * @throws BTSLBaseException
     *             , Exception
     */
    public void getFileLength() throws BTSLBaseException, Exception {
        final String METHOD_NAME = "getFileLength";
        if (_log.isDebugEnabled()) {
            _log.debug(" getFileLength", " Entered with _voucherUploadVO=" + _voucherUploadVO.toString() + ",_filePath=" + _filePath + ", _fileName=" + _fileName);
        }
        int lineCount = 0;
        String fileData = null;
        BufferedReader inFile = null;
        int fileLineCount = 0;
        try {
            _fileDataArr = new ArrayList();
            File srcFile = null;

            // creates the actual path of the file
            if (BTSLUtil.isNullString(_filePath)) {
                srcFile = new File(_fileName);
            } else {
                srcFile = new File(_filePath + File.separator + _fileName);
            }

            if (_log.isDebugEnabled()) {
                _log.debug("getFileLength", " Starting processing to get the number of records for source File Path = " + srcFile + " File Name=" + _fileName);
            }

            // creates a new bufferedreader to read the Voucher Upload file
            inFile = new BufferedReader(new FileReader(srcFile));

            while (inFile.ready()) {
                fileData = null;
                fileData = inFile.readLine();// reads line by line

                // this is used to check if the line is blank ie the record is
                // blank
                if (BTSLUtil.isNullString(fileData) || fileData == null) {
                    _log.error("getFileLength", " Record Number = " + lineCount + " No Data Found");
                    continue;
                }
                ++fileLineCount;
                _fileDataArr.add(fileData);
            }

            _voucherUploadVO.setNoOfRecordsInFile(fileLineCount);

            // this is used to check if the actual number of records in the file
            // and the records
            // entered by the user are same or not
            if (_numberOfRecords != fileLineCount) {
                _log.error("getFileLength", " Total Number of Records (" + (fileLineCount) + ") in the File (" + _fileName + ") doesn't match the entered value = " + _numberOfRecords + ". Control Returning ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MobinilVoucherFileChecks[getFileLength]", "", "", "", "The number of records (" + (fileLineCount) + ") in the file doesn't match the entered value (" + _numberOfRecords + ")");
                throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", "getFileLength", PretupsErrorCodesI.VOUCHER_ERROR_INVALID_RECORD_COUNT);// "Total number of records in the file are different from the one specified"
            }
        }// end of try block
        catch (BTSLBaseException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(" getFileLength", " BTSLBaseException = " + e);
            throw e;
        }// end of Exception
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("getFileLength", "The file (" + _fileName + ")could not be read properly to get the number of records");
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MobinilVoucherChangeOnHoldProcesses[getFileLength]", "", "", "", "The file (" + _fileName + ")could not be read properly to get the number of records");
            throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", "getFileLength", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);
        }// end of IOException
        finally {
            try {
                inFile.close();
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" getFileLength", " Exception while closing input stream = " + e);
            }
        }// end of finally
    }

    /**
     * This method is used to parse and then validate the given voucher File
     * 
     * @param p_fileName
     *            - name of the file to be parsed
     * @param p_path
     *            - path of the voucher file specified
     * @param_pProfile - the profile to be checked in the file
     * @return VoucherUploadVO
     */
    public VoucherUploadVO validateVoucherFile() throws BTSLBaseException {
        final String METHOD_NAME = "validateVoucherFile";
        if (_log.isDebugEnabled()) {
            _log.debug(" validateVoucherFile", " Entered to validate the file _fileName=" + _fileName);
        }

        int runningErrorCount = 0;
        int runningFileRecordCount = 0;
        String fileData = null;
        int minSerialLength = 0;
        int maxSerialLength = 0;
        Connection con = null;
        try {
            minSerialLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MIN_LENGTH))).intValue();
            maxSerialLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH))).intValue();
            _voucherArr = new ArrayList();
            Iterator fileIter = _fileDataArr.iterator();
            String serialNumber = null;
            String chngedStatus = null;
            String dataFile[] = null;
            VomsVoucherVO vomsVoucherVO = null;
            // isValidFile = true;
            while (fileIter.hasNext()) {
                fileData = null;
                fileData = (String) fileIter.next();
                runningFileRecordCount++;// keeps record of number of records in
                                         // file

                if (BTSLUtil.isNullString(fileData)) {
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " No Data Found");
                    runningErrorCount++;
                    continue;
                }
                // checks if the number of error encontered while parsing of
                // file does not
                // exceed the user given error count limit
                if (runningErrorCount > _allowedNumberofErrors) {
                    // isValidFile = false;
                    _log.error("validateVoucherFile", " Total Number of error (" + runningErrorCount + ") in the File (" + _fileName + ") exceeds the user specified error number= " + _allowedNumberofErrors);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MobinilVoucherChangeOnHoldProcesses[validateVoucherFile]", "", "", "", "The number of error (" + runningErrorCount + ") in the file exceed the user specified value (" + _allowedNumberofErrors + ")");
                    throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);// "Total number of error in the file exceed the specified number of error"
                }
                dataFile = fileData.split(_valueSeparator);
                if (dataFile.length != 2) {
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " Data should contain= Serial Number, Changed Status but given data is=" + dataFile.toString());
                    runningErrorCount++;
                    continue;
                }
                serialNumber = dataFile[0];
                if (!VoucherFileUploaderUtil.isValidDataLength(serialNumber.length(), minSerialLength, maxSerialLength)) {
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " Start Serial Number Not Valid" + serialNumber);
                    runningErrorCount++;
                    continue;
                }
                chngedStatus = (dataFile[1]);
                if (!BTSLUtil.isNullString(chngedStatus)) {
                    if ("E".equalsIgnoreCase(chngedStatus)) {
                        chngedStatus = VOMSI.VOUCHER_ENABLE;
                    } else if ("S".equalsIgnoreCase(chngedStatus)) {
                        chngedStatus = VOMSI.VOUCHER_STOLEN;
                    } else if ("D".equalsIgnoreCase(chngedStatus)) {
                        chngedStatus = VOMSI.VOUCHER_DAMAGED;
                    } else {
                        _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " Status (It should be E=Enable, D=Damaged, S=Stolen)=" + chngedStatus);
                        runningErrorCount++;
                        continue;
                    }
                } else {
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " Status is not defined (value should be E=Enable, D=Damaged, S=Stolen)=" + chngedStatus);
                    runningErrorCount++;
                    continue;
                }
                if (runningFileRecordCount == 1) {
                    _voucherUploadVO.setFromSerialNo(serialNumber);// set only
                                                                   // for the
                                                                   // first time
                }

                vomsVoucherVO = new VomsVoucherVO();
                vomsVoucherVO.setSerialNo(serialNumber);
                vomsVoucherVO.setProductionLocationCode(_channelUserVO.getNetworkID());
                vomsVoucherVO.setModifiedBy(_channelUserVO.getUserID());
                vomsVoucherVO.setOneTimeUsage(PretupsI.YES);
                vomsVoucherVO.setStatus(chngedStatus);
                vomsVoucherVO.setCurrentStatus(chngedStatus);
                vomsVoucherVO.setModifiedOn(_currentDate);
                _voucherArr.add(vomsVoucherVO);
            }// end of while loop
             // checks if the number of error encontered while parsing of file
             // does not
             // exceed the user given error count limit in case the exception
             // occurs at the last line
            if (runningErrorCount > _allowedNumberofErrors) {
                _log.error("validateVoucherFile", " Total Number of error (" + runningErrorCount + ") in the File (" + _fileName + ") exceeds the user specified error number= " + _allowedNumberofErrors);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MobinilVoucherChangeOnHoldProcesses[validateVoucherFile]", "", "", "", "The number of error (" + runningErrorCount + ") in the file exceed the user specified value (" + _allowedNumberofErrors + ")");
                throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);// "Total number of error in the file exceed the specified number of error"
            }
            _voucherUploadVO.setToSerialNo(serialNumber);
            _voucherUploadVO.setActualNoOfRecords(_voucherArr.size());
            _voucherUploadVO.setVoucherArrayList(_voucherArr);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("validateVoucherFile", " : BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(" validateVoucherFile", " Exception = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherChangeOnHoldProcesses[validateVoucherFile]", "", "", "", e.getMessage());
            throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);

        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e1) {
                    _log.errorTrace(METHOD_NAME, e1);
                }
            }
        }
        return _voucherUploadVO;
    }
}