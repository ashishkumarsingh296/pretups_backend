package com.btsl.voms.vomsprocesses.businesslogic;

/**
 * @(#)VoucherLoaderProcess.java
 *                               Copyright(c) 2006, Bharti Telesoft Int. Public
 *                               Ltd.
 *                               All Rights Reserved
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Gurjeet Singh Bedi 12/03/07 Created
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               --
 *                               This class is responsible to provide following
 *                               functionalities.
 *                               1.Reading particular directory for voucher
 *                               files.
 *                               2.Call the parser class for the parsing and
 *                               validating of the input file
 *                               3.Use the VomsVoucherDAO class for uploading
 *                               the records in the file to the database
 */

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.TypesI;
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
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomslogging.VomsBatchInfoLog;
import com.btsl.voms.vomslogging.VomsVoucherChangeStatusLog;
import com.btsl.voms.vomsprocesses.util.VoucherFileChecksI;
import com.btsl.voms.vomsprocesses.util.VoucherFileUploaderUtil;
import com.btsl.voms.vomsprocesses.util.VoucherUploadVO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchesDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class VoucherLoaderProcessGH {
    private static final Log _log = LogFactory.getLog(VoucherLoaderProcessGH.class.getName());

    private String _filePath; // File path name
    private ArrayList _fileList; // Contain all the file object thats name start
                                 // with file prefix.

    private File _inputFile; // File object for input file
    private File _destFile; // Destination File object
    private String _moveLocation; // Input file is move to this location After
                                  // successfull proccessing
    private static final String _fileExt = "txt"; // Files are picked up only this
                                           // extention
    // from the specified directory
    private String _fileName = null;

    private static VoucherFileChecksI _parserObj = null;
    private ProcessStatusDAO _processStatusDAO = null;
    private ProcessStatusVO _processStatusVO = null;
    private static Date _currentDate = null;
    private VoucherUploadVO _voucherUploadVO = null;
    private static int _maxNoRecordsAllowed = 0;
    private long _numberOfRecords = 0;
    private String _profileID = null;
    private String _productId = null;

    private static boolean _directVoucherEnable = false;

    public VoucherLoaderProcessGH() {
        _processStatusDAO = new ProcessStatusDAO();
        _currentDate = new Date();
    }

    /**
     * Main starting point for the process
     * 
     * @param args
     */
    public static void main(String[] args) {
        final String methodName = "main";
        long startTime = (new Date()).getTime();
        try {
            int argSize = args.length;

            if (argSize != 2) {
                _log.info(methodName, "Usage : VoucherLoaderProcessGH [Constants file] [ProcessLogConfig file][product id] [expiryduration]");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherLoaderProcessGH[main]", "", "", "", "Improper usage. Usage : VoucherLoaderProcessGH [Constants file] [ProcessLogConfig file][loginID][password][profile][fileName][total records in file]");
                throw new BTSLBaseException("VoucherLoaderProcessGH ", " main ", PretupsErrorCodesI.VOUCHER_MISSING_INITIAL_FILES);
            }
            new VoucherLoaderProcessGH().process(args);
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
        } catch (Exception e) {

            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcessGH: main", "", "", "", "Exiting the exception of main");
            _log.errorTrace(methodName, e);
        }// end of outer Exception
        finally {
            long endTime = (new Date()).getTime();
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * Method that handle the complete flow of the process
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
            VoucherFileUploaderUtil.loadCachesAndLogFiles(p_args[0], p_args[1]);

            con = OracleUtil.getSingleConnection();

            if (con == null) {
                _log.error("main ", ": Could not connect to database. Please make sure that database server is up..............");
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileUploader[process]", "", "", "", "Could not connect to Database");
                throw new BTSLBaseException("VoucherLoaderProcessGH", "main", PretupsErrorCodesI.VOUCHER_ERROR_CONN_NULL);
            }

            _processStatusVO = processBL.checkProcessUnderProcess(con, ProcessI.VOUCHER_FILE_UPLOAD_PROCCESSID);

            if (!(_processStatusVO != null && _processStatusVO.isStatusOkBool())) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
            }

            processStatusOK = _processStatusVO.isStatusOkBool();

            con.commit();

            // loading all the constant variables;
            loadConstantValues();

            loadFilesFromDir();

            VomsBatchVO vomsBatchVO = null;

            VomsVoucherDAO vomsVoucherDAO = null;

            VomsBatchVO enableBatchVO = null;

            getParserObj(Constants.getProperty("VOUCHER_PARSER_CLASS"));
            VomsProductVO vomsProductVO = null;
            ArrayList productList = null;

            productList = new VomsProductDAO().loadProductDetailsList(con, "'" + VOMSI.VOMS_STATUS_ACTIVE + "'", false, null, null);

            for (int l = 0, size = _fileList.size(); l < size; l++) {
                _voucherUploadVO = null;
                _numberOfRecords = 0;
                vomsBatchVO = null;
                _inputFile = null;
                _fileName = null;
                // Getting the file object
                _inputFile = (File) _fileList.get(l);
                _fileName = _inputFile.getName();

                try {
                    validateInputFile();

                    if (_voucherUploadVO.getVoucherArrayList() == null || _voucherUploadVO.getVoucherArrayList().isEmpty()) {
                        _log.error("VoucherLoaderProcessGH[process]", " No voucher for adding in file=" + _fileName);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherLoaderProcessGH[process]", "", "", "", " No vouchers for adding in file=" + _fileName);
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_NO_RECORDS_ERROR);
                    }

                    _numberOfRecords = _voucherUploadVO.getNoOfRecordsInFile();

                    _profileID = _voucherUploadVO.getPrfileId();

                    if (productList != null) {
                        Iterator profileList = productList.iterator();
                        while (profileList.hasNext()) {
                            vomsProductVO = (VomsProductVO) profileList.next();
                            String key = vomsProductVO.getCategoryID() + ":" + vomsProductVO.getProductID();
                            if (key.equalsIgnoreCase(_profileID)) {
                                break;
                            }
                        }
                    }
                    _productId = vomsProductVO.getProductID();
                    vomsBatchVO = prepareVomsBatchesVO(VOMSI.BATCH_GENERATED, false, null);
                    vomsBatchVO.setMrp(Long.toString(PretupsBL.getSystemAmount(vomsProductVO.getMrpStr())));
                    vomsBatchVO.setValidity(Integer.parseInt(vomsProductVO.getValidityStr()));
                    vomsBatchVO.setTalktime(Integer.parseInt(Long.toString(PretupsBL.getSystemAmount(vomsProductVO.getTalkTimeStr()))));
                    vomsBatchVO.setProductID(vomsProductVO.getProductID());
                    vomsBatchVO.setExpiryPeriod(Integer.parseInt(Long.toString(vomsProductVO.getExpiryPeriod())));
                    vomsBatchVO.set_NetworkCode("GH");
                    vomsBatchVO.setModifiedBy(TypesI.SYSTEM_USER);
                    vomsBatchVO.setOneTimeUsage(PretupsI.NO);
                    vomsBatchVO.setModifiedOn(_currentDate);
                    vomsBatchVO.setCreatedOn(_currentDate);
                    vomsBatchVO.setExpiryDate(BTSLUtil.addDaysInUtilDate(_currentDate, vomsBatchVO.getExpiryPeriod()));

                    batchList = new ArrayList();
                    batchList.add(vomsBatchVO);

                    int recordCount = 0;
                    recordCount = new VomsBatchesDAO().addBatch(con, batchList);
                    if (recordCount <= 0) {
                        _log.error("VoucherLoaderProcessGH[process]", " Not able to insert batches for file=" + _fileName);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherLoaderProcessGH[process]", "", "", "", " The batch entry could not be made into the table for file=" + _fileName);
                        throw new BTSLBaseException("VoucherLoaderProcessGH ", " process ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
                    }

                    // For Direct enabling of Vouchers
                    VomsVoucherVO voucherVO = null;
                    ArrayList voucherLogList = new ArrayList();
                    vomsBatchVO.setStatus(VOMSI.VOUCHER_NEW);
                    if (_directVoucherEnable) {
                        enableBatchVO = prepareVomsBatchesVO(VOMSI.BATCH_ENABLED, true, vomsBatchVO);
                        batchList = new ArrayList();
                        batchList.add(enableBatchVO);
                        recordCount = new VomsBatchesDAO().addBatch(con, batchList);
                        if (recordCount <= 0) {
                            _log.error("VoucherFileProcessor[process]", " Not able to insert batches ");
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileProcessor[process]", "", "", "", " The batch entry could not be made into the table");
                            throw new BTSLBaseException("VoucherFileProcessor ", " process ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
                        }
                        ArrayList voucherList = _voucherUploadVO.getVoucherArrayList();
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
                        voucherVO.setMRP(Double.parseDouble(enableBatchVO.getMrp()));
                        voucherVO.setValidity(enableBatchVO.getValidity());
                        voucherVO.setTalkTime(Long.parseLong(Integer.toString(enableBatchVO.getTalktime())));

                        voucherVO.setExpiryDateStr("");
                        voucherVO.setLastErrorMessage(BTSLUtil.NullToString(enableBatchVO.getMessage()));
                        voucherVO.setProcess("1");
                        voucherVO.setProductionLocationCode(enableBatchVO.getLocationCode());
                        voucherLogList.add(voucherVO); // adding the last entry
                    }

                    vomsVoucherDAO = new VomsVoucherDAO();
                    recordCount = vomsVoucherDAO.insertVouchers_new(con, vomsBatchVO, _voucherUploadVO.getVoucherArrayList(), _directVoucherEnable);
                    if (recordCount <= 0) {
                        _log.error("VoucherLoaderProcessGH[process]", " Not able to insert Vouchers for file=" + _fileName);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherLoaderProcessGH[process]", "", "", "", " The vouchers could not be inserted into the table for file=" + _fileName);
                        throw new BTSLBaseException("VoucherLoaderProcessGH ", " process ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
                    }

                    recordCount = 0;
                    recordCount = vomsVoucherDAO.updateSummaryTable(con, vomsBatchVO, _directVoucherEnable);
                    if (recordCount <= 0) {
                        _log.error("VoucherLoaderProcessGH[process]", " Not able to update Summary tables for file=" + _fileName);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherLoaderProcessGH[process]", "", "", "", " The batch summary table could not be updated for file=" + _fileName);
                        throw new BTSLBaseException("VoucherLoaderProcessGH ", " process ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
                    }

                    con.commit();

                    // name of the file after moving
                    VoucherFileUploaderUtil.moveFileToAnotherDirectory(_fileName, _filePath + File.separator + _fileName, _moveLocation);

                    VomsBatchInfoLog.modifyBatchLog(vomsBatchVO);

                    if (_directVoucherEnable) {
                        VomsVoucherChangeStatusLog.log(voucherLogList);
                        VomsBatchInfoLog.modifyBatchLog(enableBatchVO);
                    }

                } catch (BTSLBaseException be) {
                    _log.errorTrace(methodName, be);
                    _log.debug(methodName, "Voucher Upload Process Failed for file=" + _fileName + " .Check the log file or OAM Screen or Event log for exact errors ");

                    if (con != null) {
                        try {
                            con.rollback();
                        } catch (SQLException e1) {
                            _log.errorTrace(methodName, e1);
                        }
                    }

                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcessGH[process]", "", "", "", " Voucher Upload Process Failed for file=" + _fileName + " Getting Exception=" + be.getMessage());
                    continue;
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    _log.debug(methodName, "Voucher Upload Process Failed for file=" + _fileName + " .Check the log file or OAM Screen or Event log for exact errors ");
                    if (con != null) {
                        try {
                            con.rollback();
                        } catch (SQLException e1) {
                            _log.errorTrace(methodName, e1);
                        }
                    }

                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcessGH[process]", "", "", "", "Voucher Upload Process Failed for file=" + _fileName + " Getting Exception=" + e.getMessage());
                    throw new BTSLBaseException("VoucherLoaderProcessGH ", " process ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
                }
                if (_directVoucherEnable) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(" process", "Voucher Upload Process Successfully Executed.Batch Successfully Enabled. Your Batch number for the process is = " + enableBatchVO.getBatchNo());
                    }
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileProcessor[process]", "", "", "", "Voucher Upload Process Successfully Executed.Batch Successfully Enabled. Your Batch number for the process is = " + enableBatchVO.getBatchNo());

                } else {
                    _log.debug(methodName, "Voucher Upload Process for file=" + _fileName + " Successfully Executed.Batch Successfully Generated. Your Batch number for the process is = " + vomsBatchVO.getBatchNo());
                    if (_log.isDebugEnabled()) {
                        _log.debug(" process", "Voucher Upload Process for file=" + _fileName + " Successfully Executed.Batch Successfully Generated. Your Batch number for the process is = " + vomsBatchVO.getBatchNo());
                    }
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherLoaderProcessGH[process]", "", "", "", "Voucher Upload Process for file=" + _fileName + " Successfully Executed.Batch Successfully Generated for file=" + _fileName + " . Your Batch number for the process is = " + vomsBatchVO.getBatchNo());
                }
            }
        } catch (BTSLBaseException be) {
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
            _log.errorTrace(methodName, e);
            _log.info(methodName, "Voucher Upload Process Failed .Check the log file or OAM Screen or Event log for exact errors ");
            try {
                con.rollback();
            } catch (SQLException e1) {
                _log.errorTrace(methodName, e1);
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcessGH[process]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherLoaderProcessGH ", " process ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            try {
                if (processStatusOK) {
                    Date date = new Date();
                    _processStatusVO.setStartDate(_processStatusVO.getStartDate());
                    _processStatusVO.setExecutedOn(date);
                    _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    int successU = _processStatusDAO.updateProcessDetail(con, _processStatusVO);

                    if (successU > 0) {
                        con.commit();
                    } else {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcessGH[process]", "processStatusVO.getProcessID()" + ProcessI.VOUCHER_FILE_UPLOAD_PROCCESSID, "", "", "Error while updating the process status after completing the process");
                        }
                }
            } catch (BTSLBaseException be) {
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcessGH[process]", "processStatusVO.getProcessID()" + ProcessI.VOUCHER_FILE_UPLOAD_PROCCESSID, "", "", "BaseException:" + e.getMessage());
        
            }// end of catch-Exception
            finally {
                if (con != null) {
                    try {
                        con.close();
                    } catch (Exception e1) {
                        _log.errorTrace(methodName, e1);
                    }
                }
                _fileName = null;
                _parserObj = null;
                _processStatusDAO = null;
                _processStatusVO = null;
                _voucherUploadVO = null;
                _maxNoRecordsAllowed = 0;
                _numberOfRecords = 0;
                _profileID = null;
            }
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Exiting ");
            }
        }
    }

    /**
     * Method to load constant values for the process
     * 
     * @throws BTSLBaseException
     */
    public void loadConstantValues() throws BTSLBaseException {
        final String methodName = "loadConstantValues";
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
                _log.errorTrace(methodName, e);
                _log.info(methodName, " Configuration Problem, Parameter VOMS_MAX_FILE_LENGTH not defined properly");
                _log.error("loadConstantValues ", "Configuration Problem, Parameter VOMS_MAX_FILE_LENGTH not defined properly");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherLoaderProcessGH[loadConstantValues]", "", "", "", "Configuration Problem, Parameter VOMS_MAX_FILE_LENGTH not defined properly");
                throw new BTSLBaseException("VoucherLoaderProcessGH", "loadConstantValues", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }

            // this is the path of the input voucher file.
            if (_log.isDebugEnabled()) {
                _log.debug(" loadConstantValues ", ": reading  _filePath ");
            }
            _filePath = BTSLUtil.NullToString(Constants.getProperty("VOMS_VOUCHER_FILE_PATH"));

            // Checking whether the file path provided exist or not.If not,
            // throw an exception
            if (!(new File(_filePath).exists())) {
                _log.info(methodName, " Configuration Problem, Parameter VOMS_VOUCHER_FILE_PATH not defined properly");
                _log.error("loadConstantValues ", "Configuration Problem, Parameter VOMS_VOUCHER_FILE_PATH not defined properly");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherLoaderProcessGH[loadConstantValues]", "", "", "", "Configuration Problem, Parameter VOMS_VOUCHER_FILE_PATH not defined properly");
                throw new BTSLBaseException("VoucherLoaderProcessGH", "loadConstantValues", PretupsErrorCodesI.VOUCHER_ERROR_DIR_NOT_EXIST);
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
                        _log.debug(methodName, " Configuration Problem, Could not create the backup directory at the specified location " + _moveLocation);
                        _log.error("loadConstantsValues ", " Configuration Problem, Could not create the backup directory at the specified location " + _moveLocation);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherLoaderProcessGH[loadConstantValues]", "", "", "", "Configuration Problem, Could not create the backup directory at the specified location " + _moveLocation);
                        throw new BTSLBaseException("VoucherLoaderProcessGH", "loadConstantValues", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
                    }
                }
            }
            // Added for Direct Voucher Enabling
            _directVoucherEnable = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DIRECT_VOUCHER_ENABLE))).booleanValue();

        }// end of try block
        catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error("loadConstantValues ", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error("loadConstantValues ", "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcessGH[loadConstantValues]", "", "", "", "Exception while loading the constants from the Constants.prop file");
            throw new BTSLBaseException("VoucherLoaderProcessGH", "loadConstantValues", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR,e);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug("loadConstantValues ", " Exiting: _filePath = " + _filePath + " _moveLocation= " + _moveLocation);
            }
        }// end of finally
    }// end of loadConstantValues

    /**
     * This method is used to loadAll the files with specified prefix.
     * All these file objects are stored in ArrayList.
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
                throw new BTSLBaseException(methodName, "loadFileFromDir", PretupsErrorCodesI.VOUCHER_ERROR_FILE_DOES_NOT_EXIST);// "The voucher file does not exists at the location specified"
            }

            // This filter is used to filter all the files that start with
            // _filePrefix;
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return (name.endsWith(_fileExt));
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
                        _log.debug((methodName), "File = " + tempFileArray[l] + " is added to fileList");
                    }
                }
            }// end of for loop

            // Check whether the directory contains the file start with
            // filePrefix.
            if (_fileList.isEmpty()) {
                throw new BTSLBaseException(methodName, "loadFileFromDir", PretupsErrorCodesI.VOUCHER_ERROR_FILE_DOES_NOT_EXIST);// "The voucher file does not exists at the location specified"
            }
        } catch (BTSLBaseException be) {
            _log.error(methodName, " No files exists at the following (" + _filePath + ") specified, please check the path");
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherLoaderProcessGH[loadFilesFromDir]", "", "", "", "No files exists at the following (" + _filePath + ") specified, please check the path");
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherLoaderProcessGH[loadFilesFromDir]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadFilesFromDir", e.getMessage(),e);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exited");
            }
        }// end of finally
    }// end of loadFilesFromDir

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

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("validateInputFile", "BTSLBaseException be= " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("VoucherLoaderProcessGH[validateInputFile]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcessGH[validateInputFile]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherLoaderProcessGH ", " validateInputFile ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR,e);
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
            _log.debug(" populateVoucherUploadVO", " Entered................ ");
        }
        try {
            _voucherUploadVO = new VoucherUploadVO();
            _voucherUploadVO.setProcessType(VoucherUploadVO._AUTOPROCESSTYPE);
            _voucherUploadVO.setFileName(_fileName);
            _voucherUploadVO.setFilePath(_filePath);
            _voucherUploadVO.setMaxNoOfRecordsAllowed(_maxNoRecordsAllowed);
            _voucherUploadVO.setCurrentDate(_currentDate);
            _voucherUploadVO.setRunningFromCron("Y");
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("VoucherLoaderProcessGH[populateVoucherUploadVO]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcessGH[populateVoucherUploadVO]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherLoaderProcessGH ", " populateVoucherUploadVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR,e);
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
    private VomsBatchVO prepareVomsBatchesVO(String p_batchType, boolean p_isDirectEnable, VomsBatchVO p_vomsBatchVO) throws BTSLBaseException {
        final String METHOD_NAME = "prepareVomsBatchesVO";
        if (_log.isDebugEnabled()) {
            _log.debug(" prepareVomsBatchesVO", " Entered with p_batchType=" + p_batchType + " p_isDirectEnable=" + p_isDirectEnable);
        }

        VomsBatchVO vomsBatchVO = null;
        String batchNo = null;
        try {
            vomsBatchVO = new VomsBatchVO();
            batchNo = String.valueOf(IDGenerator.getNextID(VOMSI.VOMS_BATCHES_DOC_TYPE, String.valueOf(BTSLUtil.getFinancialYear()), VOMSI.ALL));

            vomsBatchVO.setProductID(_productId);
            vomsBatchVO.setBatchType(p_batchType);
            vomsBatchVO.setNoOfVoucher(_voucherUploadVO.getActualNoOfRecords());
            vomsBatchVO.setFromSerialNo(_voucherUploadVO.getFromSerialNo());
            vomsBatchVO.setToSerialNo(String.valueOf(_voucherUploadVO.getToSerialNo()));
            vomsBatchVO.setFailCount(_numberOfRecords - (_voucherUploadVO.getVoucherArrayList()).size());// for
                                                                                                         // setting
                                                                                                         // the
                                                                                                         // no.
                                                                                                         // of
                                                                                                         // failed
                                                                                                         // records
            vomsBatchVO.setOneTimeUsage(PretupsI.YES);
            vomsBatchVO.setSuccessCount(_voucherUploadVO.getActualNoOfRecords());
            vomsBatchVO.setLocationCode("GH");
            vomsBatchVO.setCreatedBy(TypesI.SYSTEM_USER);
            vomsBatchVO.setCreatedOn(_currentDate);
            vomsBatchVO.setBatchNo(new VomsUtil().formatVomsBatchID(vomsBatchVO, batchNo));
            vomsBatchVO.setModifiedBy(TypesI.SYSTEM_USER);
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
            _log.error("VoucherLoaderProcessGH[prepareVomsBatchesVO]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcessGH[prepareVomsBatchesVO]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherLoaderProcessGH ", " prepareVomsBatchesVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR,e);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" prepareVomsBatchesVO", " Exiting with vomsBatchVO= " + vomsBatchVO);
            }
        }
        return vomsBatchVO;
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcessGH[getParserObj]", "", "", "", "Exception: The object of the parser class could not be created dynamically");
            throw new BTSLBaseException(this, "getParserObj", PretupsErrorCodesI.VOUCHER_ERROR_PARSER_CLASS_NOT_INSTANTIATED,e);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug("getParserObj", " Exiting with _parserObj = " + _parserObj);
            }
        }// end of finally
    }// end of getParserObj
}
