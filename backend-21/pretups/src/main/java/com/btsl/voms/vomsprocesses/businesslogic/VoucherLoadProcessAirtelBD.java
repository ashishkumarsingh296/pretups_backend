package com.btsl.voms.vomsprocesses.businesslogic;

import java.io.File;
import java.io.FilenameFilter;
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
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchesDAO;
import com.btsl.voms.voucher.businesslogic.VomsSerialUploadCheckVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class VoucherLoadProcessAirtelBD {

    private static final Log _log = LogFactory.getLog(VoucherLoadProcessAirtelBD.class.getName());
    private String _filePath; // File path name
    private ArrayList<File> _fileList; // Contain all the file object thats name
                                       // start with file prefix.
    // private String _allowedExt="txt,csv";//Input file should be either txt or
    // csv.
    private String _loginID = null;
    private String _password = null;
    private File _inputFile; // File object for input file
    private File _destFile; // Destination File object
    private String _moveLocation; // Input file is move to this location After
                                  // successfull proccessing
    private String _fileExt = "LOADED"; // Files are picked up only this
                                        // extention from the specified
                                        // directory
    private String _fileName = null;
    private ChannelUserVO _channelUserVO = null;
    private static VoucherFileChecksI _parserObj = null;
    private ProcessStatusDAO _processStatusDAO = null;
    private ProcessStatusVO _processStatusVO = null;
    private static Date _currentDate = null;
    private VoucherUploadVO _voucherUploadVO = null;
    private static int _maxNoRecordsAllowed = 0;
    private long _numberOfRecords = 0;
    private String _productID = null;
    private static boolean _directVoucherEnable = false;
    private VomsSerialUploadCheckVO _vomsSerialUploadCheckVO = null;

    public VoucherLoadProcessAirtelBD() {
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

            // This logic uses the number of arguments specified to decide how
            // to retrieve the information
            // for userid, password, profile, name of the voucherFile, number of
            // records.
            if (argSize > 2 && argSize != 4) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherLoaderProcess[main]", "", "", "", "Improper usage. Usage : VoucherLoaderProcess [Constants file] [ProcessLogConfig file][loginID][password][profile][fileName][total records in file]");
                throw new BTSLBaseException("VoucherLoaderProcess ", " main ", PretupsErrorCodesI.VOUCHER_MISSING_INITIAL_FILES);
            } else if (argSize < 2) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherLoaderProcess[main]", "", "", "", "Improper usage. Usage : VoucherLoaderProcess [Constants file] [ProcessLogConfig file]");
                throw new BTSLBaseException("VoucherLoaderProcess ", " main ", PretupsErrorCodesI.VOUCHER_MISSING_INITIAL_FILES);
            }
            new VoucherLoadProcessAirtelBD().process(args);
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.debug(methodName, " Exiting BTSLBaseException " + be.getMessage());
            // be.printStackTrace();
        } catch (Exception e) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcess: main", "", "", "", "Exiting the exception of main");
            _log.errorTrace(methodName, e);
        }// end of outer Exception
        finally {
            long endTime = (new Date()).getTime();
            ConfigServlet.destroyProcessCache();
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

            vomsBatchVO.setProductID(_productID.toUpperCase());
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
            _log.error("VoucherLoaderProcess[prepareVomsBatchesVO]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcess[prepareVomsBatchesVO]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherLoaderProcess ", " prepareVomsBatchesVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" prepareVomsBatchesVO", " Exiting with vomsBatchVO= " + vomsBatchVO);
            }
        }
        return vomsBatchVO;
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
        ArrayList<VomsBatchVO> batchList = null;
        try {
            int argSize = p_args.length;
            VoucherFileUploaderUtil.loadCachesAndLogFiles(p_args[0], p_args[1]);

            // opening the connection
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                _log.info(methodName, " Could not connect to database. Please make sure that database server is up..............");
                _log.error("main ", ": Could not connect to database. Please make sure that database server is up..............");
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileUploader[process]", "", "", "", "Could not connect to Database");
                throw new BTSLBaseException("VoucherLoaderProcess", "main", PretupsErrorCodesI.VOUCHER_ERROR_CONN_NULL);
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
            } else if (argSize == 4) {
                getDetailsFromConsole(con, p_args);
            }
            loadFilesFromDir();

            VomsBatchVO vomsBatchVO = null;
            VomsProductDAO vomsProductDAO = null;
            VomsVoucherDAO vomsVoucherDAO = null;
            VomsBatchVO enableBatchVO = null;

            for (int l = 0, size = _fileList.size(); l < size; l++) {
                _voucherUploadVO = null;
                _productID = null;
                _numberOfRecords = 0;
                vomsBatchVO = null;
                _inputFile = null;
                _fileName = null;
                _parserObj = null;

                // Getting the file object
                _inputFile = _fileList.get(l);
                _fileName = _inputFile.getName();

                try {
                    // creates an instance of the parser class based on the
                    // entry in the Constants file
                    getParserObj(Constants.getProperty("VOUCHER_PARSER_CLASS"));

                    // this method will be used for validation of the Voucher
                    // file
                    validateInputFile();
                    _vomsSerialUploadCheckVO = _parserObj.populateVomsSerialUploadCheckVO(con);
                    if (_voucherUploadVO.getVoucherArrayList() == null || _voucherUploadVO.getVoucherArrayList().isEmpty()) {
                        _log.error("VoucherLoaderProcess[process]", " No voucher for adding in file=" + _fileName);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherLoaderProcess[process]", "", "", "", " No vouchers for adding in file=" + _fileName);
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_NO_RECORDS_ERROR);
                    }

                    _productID = BTSLUtil.NullToString(_voucherUploadVO.getProductID());
                    _numberOfRecords = _voucherUploadVO.getNoOfRecordsInFile();

                    vomsProductDAO = new VomsProductDAO();

                    // checks if the particular profile exists or not
                    if (!vomsProductDAO.isProductExitsVoucherGen(con, _productID, VOMSI.VOMS_STATUS_ACTIVE)) {
                        _log.debug(methodName, " No such product exists in the system for file=" + _fileName + ".........");
                        _log.error("process", " : Product does not exists for file=" + _fileName);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherLoaderProcess[process]", "", "", "", "Product ID provided for voucher file does not exists in the system for file=" + _fileName);
                        throw new BTSLBaseException("VoucherLoaderProcess", "process", PretupsErrorCodesI.VOUCHER_ERROR_PRODUCT_NOT_EXISTS);
                    }

                    vomsBatchVO = prepareVomsBatchesVO(VOMSI.BATCH_GENERATED, false, null);

                    batchList = new ArrayList<VomsBatchVO>();
                    batchList.add(vomsBatchVO);

                    int recordCount = 0;
                    recordCount = new VomsBatchesDAO().addBatch(con, batchList);
                    if (recordCount <= 0) {
                        _log.error("VoucherLoaderProcess[process]", " Not able to insert batches for file=" + _fileName);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherLoaderProcess[process]", "", "", "", " The batch entry could not be made into the table for file=" + _fileName);
                        throw new BTSLBaseException("VoucherLoaderProcess ", " process ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
                    }

                    // For Direct enabling of Vouchers
                    VomsVoucherVO voucherVO = null;
                    ArrayList<VomsVoucherVO> voucherLogList = new ArrayList<VomsVoucherVO>();

                    if (_directVoucherEnable) {
                        enableBatchVO = prepareVomsBatchesVO(VOMSI.BATCH_ENABLED, true, vomsBatchVO);
                        batchList = new ArrayList<VomsBatchVO>();
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
                        voucherVO.setMRP(0);
                        voucherVO.setExpiryDateStr("");
                        voucherVO.setLastErrorMessage(BTSLUtil.NullToString(enableBatchVO.getMessage()));
                        voucherVO.setProcess("1");
                        voucherVO.setProductionLocationCode(enableBatchVO.getLocationCode());
                        voucherLogList.add(voucherVO); // adding the last entry
                    }

                    vomsVoucherDAO = new VomsVoucherDAO();
                    recordCount = vomsVoucherDAO.insertVouchers(con, vomsBatchVO, _voucherUploadVO.getVoucherArrayList(), _directVoucherEnable);

                    if (recordCount <= 0) {
                        _log.error("VoucherLoaderProcess[process]", " Not able to insert Vouchers for file=" + _fileName);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherLoaderProcess[process]", "", "", "", " The vouchers could not be inserted into the table for file=" + _fileName);
                        throw new BTSLBaseException("VoucherLoaderProcess ", " process ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
                    }

                    recordCount = 0;
                    recordCount = vomsVoucherDAO.updateSummaryTable(con, vomsBatchVO, _directVoucherEnable);
                    if (recordCount <= 0) {
                        _log.error("VoucherLoaderProcess[process]", " Not able to update Summary tables for file=" + _fileName);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherLoaderProcess[process]", "", "", "", " The batch summary table could not be updated for file=" + _fileName);
                        throw new BTSLBaseException("VoucherLoaderProcess ", " process ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
                    }

                    con.commit();

                    // name of the file after moving
                    VoucherFileUploaderUtil.moveFileToAnotherDirectory(_fileName, _filePath + File.separator + _fileName, _moveLocation);

                    // Move the key file
                    VoucherFileUploaderUtil.moveFileToAnotherDirectory(_fileName.substring(0, _fileName.lastIndexOf("_") + 1) + "key" + _fileName.substring(_fileName.lastIndexOf(".")), _filePath + File.separator + _fileName.substring(0, _fileName.lastIndexOf("_") + 1) + "key" + _fileName.substring(_fileName.lastIndexOf(".")), _moveLocation);

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
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcess[process]", "", "", "", " Voucher Upload Process Failed for file=" + _fileName + " Getting Exception=" + be.getMessage());
                    continue;
                } catch (Exception e) {
                    _log.debug(methodName, "Voucher Upload Process Failed for file=" + _fileName + " .Check the log file or OAM Screen or Event log for exact errors ");
                    try {
                        con.rollback();
                    } catch (SQLException e1) {
                        _log.errorTrace(methodName, e1);
                    }
                    _log.errorTrace(methodName, e);
                    _log.error("VoucherLoaderProcess[process]", " Exception =" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcess[process]", "", "", "", "Voucher Upload Process Failed for file=" + _fileName + " Getting Exception=" + e.getMessage());
                    throw new BTSLBaseException("VoucherLoaderProcess ", " process ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
                }
                if (_directVoucherEnable) {
                    // validating for number of actual records inserted in
                    // voms_voucher.On the basis of records inserted, successful
                    // uploading of file is determined
                    _log.debug(methodName, "Voucher Upload Process Successfully Executed.Batch Successfully Enabled. Your Batch number for the process is = " + enableBatchVO.getBatchNo());
                    if (_log.isDebugEnabled()) {
                        _log.debug(" process", "Voucher Upload Process Successfully Executed.Batch Successfully Enabled. Your Batch number for the process is = " + enableBatchVO.getBatchNo());
                    }
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileProcessor[process]", "", "", "", "Voucher Upload Process Successfully Executed.Batch Successfully Enabled. Your Batch number for the process is = " + enableBatchVO.getBatchNo());

                } else {
                    // validating for number of actual records inserted in
                    // voms_voucher.On the basis of records inserted, successful
                    // uploading of file is determined
                    _log.debug(methodName, "Voucher Upload Process for file=" + _fileName + " Successfully Executed.Batch Successfully Generated. Your Batch number for the process is = " + vomsBatchVO.getBatchNo());
                    if (_log.isDebugEnabled()) {
                        _log.debug(" process", "Voucher Upload Process for file=" + _fileName + " Successfully Executed.Batch Successfully Generated. Your Batch number for the process is = " + vomsBatchVO.getBatchNo());
                    }
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherLoaderProcess[process]", "", "", "", "Voucher Upload Process for file=" + _fileName + " Successfully Executed.Batch Successfully Generated for file=" + _fileName + " . Your Batch number for the process is = " + vomsBatchVO.getBatchNo());
                }

            }
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
            _log.error("VoucherLoaderProcess[process]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcess[process]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherLoaderProcess ", " process ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
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
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcess[process]", "processStatusVO.getProcessID()" + ProcessI.VOUCHER_FILE_UPLOAD_PROCCESSID, "", "", "Error while updating the process status after completing the process");
                        // throw new
                        // BTSLBaseException(this,"process",PretupsErrorCodesI.PROCESS_ERROR_UPDATE_STATUS);
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
                    // throw be;
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcess[process]", "processStatusVO.getProcessID()" + ProcessI.VOUCHER_FILE_UPLOAD_PROCCESSID, "", "", "BaseException:" + e.getMessage());
                // throw new
                // BTSLBaseException(this,"process","error.general.processing");
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
                _channelUserVO = null;
                _parserObj = null;
                _processStatusDAO = null;
                _processStatusVO = null;
                _voucherUploadVO = null;
                _maxNoRecordsAllowed = 0;
                _numberOfRecords = 0;
                _productID = null;
            }
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Exiting ");
            }
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
            _voucherUploadVO.setChannelUserVO(_channelUserVO);
            _voucherUploadVO.setCurrentDate(_currentDate);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("VoucherLoaderProcess[populateVoucherUploadVO]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcess[populateVoucherUploadVO]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherLoaderProcess ", " populateVoucherUploadVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" populateVoucherUploadVO", " Exiting................ ");
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

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("validateInputFile", "BTSLBaseException be= " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("VoucherLoaderProcess[validateInputFile]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcess[validateInputFile]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherLoaderProcess ", " validateInputFile ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(" validateInputFile", " Exiting................ ");
        }
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcess[getParserObj]", "", "", "", "Exception: The object of the parser class could not be created dynamically");
            throw new BTSLBaseException(this, "getParserObj", PretupsErrorCodesI.VOUCHER_ERROR_PARSER_CLASS_NOT_INSTANTIATED);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug("getParserObj", " Exiting with _parserObj = " + _parserObj);
            }
        }// end of finally
    }// end of getParserObj

    /**
     * This method is used to loadAll the files with specified prefix.
     * All these file objects are stored in ArrayList.
     * 
     * @throws BTSLBaseException
     */
    public void loadFilesFromDir() throws BTSLBaseException {
        final String methodName = "loadFilesFromDir";
        if (_log.isDebugEnabled()) {
            _log.debug("loadFilesFromDir", "Entered");
        }
        File directory = null;
        try {
            directory = new File(_filePath);
            // Check if the directory contains any files
            if (directory.list() == null) {
                throw new BTSLBaseException("VoucherLoaderProcess", "loadFileFromDir", PretupsErrorCodesI.VOUCHER_ERROR_FILE_DOES_NOT_EXIST);// "The voucher file does not exists at the location specified"
            }

            // This filter is used to filter all the files that start with
            // _filePrefix;
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    if (name.endsWith(_fileExt)) {
                        if (!name.substring(name.lastIndexOf("_") + 1, name.lastIndexOf(".")).equals("key")) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                    return false;

                }
            };

            // List of files that start with the file prefix with specified
            // extantion.
            File[] tempFileArray = directory.listFiles(filter);
            _fileList = new ArrayList<File>();

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
                throw new BTSLBaseException("VoucherLoaderProcess", "loadFileFromDir", PretupsErrorCodesI.VOUCHER_ERROR_FILE_DOES_NOT_EXIST);// "The voucher file does not exists at the location specified"
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error("loadFilesFromDir", "BTSLBaseException be = " + be.getMessage());
            _log.debug(methodName, " No files exists at the following (" + _filePath + ") specified, please check the path...............");
            _log.error("loadFilesFromDir", " No files exists at the following (" + _filePath + ") specified, please check the path");
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherLoaderProcess[loadFilesFromDir]", "", "", "", "No files exists at the following (" + _filePath + ") specified, please check the path");
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error("loadFilesFromDir", "Exception e = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherLoaderProcess[loadFilesFromDir]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadFilesFromDir", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug("loadFilesFromDir", "Exited");
            }
        }// end of finally
    }// end of loadFilesFromDir

    /**
     * This method takes the required input from the arguments directly and
     * authenticates the user
     * 
     * @param p_con
     * @param p_args
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

            _channelUserVO = VoucherFileUploaderUtil.validateUserWithRole(p_con, _loginID, _password, PretupsI.LOCALE_LANGAUGE_EN, VoucherFileUploaderUtil.AUTH_TYPE_ROLE);

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("getDetailsFromConsole ", "BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("VoucherLoaderProcess[getDetailsFromConsole]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcess[getDetailsFromConsole]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherLoaderProcess ", " getDetailsFromConsole ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of Exception
    }

    /**
     * Method to get the details of the process interactively
     * 
     * @param p_con
     * @throws BTSLBaseException
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
            try {
                _password = passfield.getPassword(VoucherFileUploaderI.PASSWORD);
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error("getDetailsInteractively ", ": The password could not be retreived ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcess[getDetailsInteractively]", "", "", "", "Not able to get input data (Password) from console from user");
                throw new BTSLBaseException("VoucherLoaderProcess", "getDetailsInteractively", PretupsErrorCodesI.VOUCHER_ERROR_PASSWORD_RETREIVAL);
            }

            // validates the user for being network admin. If not throws an
            // exception
            _channelUserVO = VoucherFileUploaderUtil.validateUserWithRole(p_con, _loginID, _password, PretupsI.LOCALE_LANGAUGE_EN, VoucherFileUploaderUtil.AUTH_TYPE_ROLE);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("getDetailsInteractively ", "BTSLBaseException be = " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("VoucherLoaderProcess[getDetailsInteractively]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcess[getDetailsInteractively]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherLoaderProcess ", " getDetailsInteractively ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherLoaderProcess[loadConstantValues]", "", "", "", "Configuration Problem, Parameter VOMS_MAX_FILE_LENGTH not defined properly");
                throw new BTSLBaseException("VoucherLoaderProcess", "loadConstantValues", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherLoaderProcess[loadConstantValues]", "", "", "", "Configuration Problem, Parameter VOMS_VOUCHER_FILE_PATH not defined properly");
                throw new BTSLBaseException("VoucherLoaderProcess", "loadConstantValues", PretupsErrorCodesI.VOUCHER_ERROR_DIR_NOT_EXIST);
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
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherLoaderProcess[loadConstantValues]", "", "", "", "Configuration Problem, Could not create the backup directory at the specified location " + _moveLocation);
                        throw new BTSLBaseException("VoucherLoaderProcess", "loadConstantValues", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherLoaderProcess[loadConstantValues]", "", "", "", "Exception while loading the constants from the Constants.prop file");
            throw new BTSLBaseException("VoucherLoaderProcess", "loadConstantValues", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug("loadConstantValues ", " Exiting: _filePath = " + _filePath + " _moveLocation= " + _moveLocation);
            }
        }// end of finally
    }// end of loadConstantValues

}
