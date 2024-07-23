package com.btsl.pretups.processes.clientprocesses;

/**
 * @(#)VomsSalesVoucherReport.java
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
 *                                              Sanjay Kumar Bind1 June 25, 2018
 *                                              Initial Creation
 *                                              --------------------------------
 *                                              --------------------------------
 *                                              --------------------------------
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsprocesses.util.VoucherFileUploaderUtil;
import com.btsl.voms.vomsprocesses.util.VoucherUploadVO;
//import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsDetailVO;
import com.btsl.voms.voucher.businesslogic.VomsMasterVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class VomsSalesVoucherReport {
	
	private File _destFile; // Destination File object
    private static String _moveLocation; // Input file is moved to this location
                                         // after successfull proccessing
    private static int _maxNoRecordsAllowed = 0;
    private static int _numberOfRecords = 0;
    private static String _filePath = null;

    private String _fileName = null;
    private ChannelUserVO _channelUserVO = null;
    private static Date _currentDate = null;
    private VoucherUploadVO _voucherUploadVO = null;
    private VomsMasterVO vomsMasterVO = null;
    private VomsDetailVO vomsDetailVO = null;
    private ProcessStatusVO _processStatusVO = null;
    private ArrayList _fileDataArr = null;
    private ArrayList _voucherArr = null;
    private int _allowedNumberofErrors = 0;
    private String _valueSeparator = null;
    private String _fileHeader = null;
    VomsVoucherDAO vomsVoucherDAO = null;
    VomsVoucherVO vomsVoucherVO = null;
    String batchNo = null;
    String batchId = null;
    boolean isFileMoved = false;
    boolean isFileNameErrorFound = false;
    boolean isFileHeaderErrorFound = false;
    boolean isFileDataErrorFound = false;
    private static ArrayList<ListValueVO> lookupList = null;
    Iterator<ListValueVO> itr= null;
    
    private static Log _log = LogFactory.getLog(VomsSalesVoucherReport.class.getName());

    public VomsSalesVoucherReport() {
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
                _maxNoRecordsAllowed = Integer.parseInt(Constants.getProperty("DAILY_SALES_MAX_FILE_LENGTH"));
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error("loadConstantValues ", "Configuration Problem, Parameter VOMS_MAX_FILE_LENGTH not defined properly");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MobinilVoucherChangeOnHoldProcesses[loadConstantValues]", "", "", "", "Configuration Problem, Parameter VOMS_MAX_FILE_LENGTH not defined properly");
                throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", "loadConstantValues", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(" loadConstantValues ", ": reading  _filePath ");
            }
            
            // this is the path of the input voucher file.
            _filePath = BTSLUtil.NullToString(Constants.getProperty("DAILY_SALES_FILE_PATH"));

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
            _moveLocation = BTSLUtil.NullToString(Constants.getProperty("DAILY_SALES_FILE_MOVE_PATH"));

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
                _allowedNumberofErrors = Integer.parseInt(Constants.getProperty("DAILY_SALES_ALLOWED_ERRORS"));
                if (_log.isDebugEnabled()) {
                    _log.debug("MobinilVoucherChangeOnHoldProcesses ", " loadConstantValues   VOMS_TOTAL_ALLOWED_ERRORS " + _allowedNumberofErrors);
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _allowedNumberofErrors = 0;
                _log.info("loadConstantValues", " Total number of error (Entry VOMS_TOTAL_ALLOWED_ERRORS) not found in Constants . Thus taking default values as 0");
            }

            // this is used to seperate the different fields in a single record
            _valueSeparator = Constants.getProperty("DAILY_SALES_SEPARATOR");

            if (BTSLUtil.isNullString(_valueSeparator)) {
                _log.info("loadConstantValues", " Entry VOMS_FILE_SEPARATOR is defined blank in Constants . Thus taking default values as \"");
                _valueSeparator = ",";
            }
            
            
            lookupList = new ArrayList();
            lookupList = LookupsCache.loadLookupDropDown(PretupsI.TERMINAL_TYPE_VOUCHER,true);
            
            
            

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
        // long startTime = (new Date()).getTime();
        final String methodName = "main";
        try {
        	// int argSize = args.length;
            // This logic uses the number of arguments specified to decide how
            // to retrieve the information
            // for userid, password, profile, name of the voucherFile, number of
            // records.
        	
        	if (args.length < 2) {
                if (_log.isDebugEnabled()) {
            		_log.debug("main", "Usage : VomsSalesVoucherReport [Constants file] [LogConfig file]");
                }
                return;
            }
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                if (_log.isDebugEnabled()) {
            		_log.debug("main", "Constants File Not Found .............");
                }
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                if (_log.isDebugEnabled()) {
            		_log.debug("main", "Logconfig File Not Found .............");
                }
                return;
            }
            
            VoucherFileUploaderUtil.loadCachesAndLogFiles(args[0], args[1]);
            
        } catch (Exception e) {
        	if (_log.isDebugEnabled()) {
        		_log.debug("main", " Error in Loading Files ...........................: " + e.getMessage());
            }
        	_log.errorTrace(methodName, e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        
        try {
        		// starting the process
        		new VomsSalesVoucherReport().process(args);
        		
        } catch (BTSLBaseException be) {
        	_log.error("main", PretupsI.BTSLEXCEPTION + be.getMessage());
        	_log.errorTrace(methodName, be);
        } finally {
            if (_log.isDebugEnabled()) {
            	_log.debug("main", "Exiting..... ");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * This is the main method that is controlling the flow of the process
     * 
     * @param p_args
     * @throws BTSLBaseException
     */
    @SuppressWarnings("null")
	private void process(String[] p_args) throws BTSLBaseException {

    	Connection con = null;
    	MComConnectionI mcomCon = null;
        ProcessBL processBL = new ProcessBL();
        boolean processStatusOK = false;
        PreparedStatement pstmtInsertVoucher = null;
        PreparedStatement pstmtInsertVoucherStatus = null;
        int updateCount = 0;
        String status = null;
        String remarks = null;
        long invalidVoucherCount = 0;
        LinkedHashMap<String, String> invalidSerialHashMap = null;
        int j = 0;
        int k = 0;
        
        final String methodName = "process";
        try {
            vomsDetailVO = new VomsDetailVO();
            vomsMasterVO = new VomsMasterVO();

            // opening the connection
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            //con = OracleUtil.getSingleConnection();
            if (con == null) {
                _log.info(methodName, " Could not connect to database. Please make sure that database server is up..............");
                _log.error("main ", ": Could not connect to database. Please make sure that database server is up..............");
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsSalesVoucherReport[process]", "", "", "", "Could not connect to Database");
                throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", "main", PretupsErrorCodesI.VOUCHER_ERROR_CONN_NULL);
            }
            
            _processStatusVO = processBL.checkProcessUnderProcess(con, ProcessI.VOUCHER_SALES_DAILY_REPORT_PROCCESSID);

            if (!(_processStatusVO != null && _processStatusVO.isStatusOkBool())) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
            }
            processStatusOK = _processStatusVO.isStatusOkBool();
            // Commiting the status of process status as 'U-Under Process'.
            con.commit();
            
            // loading all the constant variables;
            loadConstantValues();
            
            File dir= new File(_filePath);
			String[] fileArr={};
			if(dir.exists()) {
				fileArr=dir.list();
			} else {
				_log.debug(methodName,"Directory does not exist "+_filePath);
				throw new BTSLBaseException("DirectPayOut",methodName,PretupsErrorCodesI.DIR_NOT_EXIST);
			}
			
            int filesLength=fileArr.length;
            
            // start of single file procession
			for(int f=0; f < filesLength; f++)
			{
				isFileMoved = false;
				_fileName=fileArr[f];
				invalidVoucherCount = 0;

            // this method will be used for validation of the Voucher file
            _voucherUploadVO = new VoucherUploadVO();
            
            validateInputFile();
            
            if(isFileMoved) {
            	continue;
            }
            
            if (_voucherUploadVO.getVoucherArrayList() == null || _voucherUploadVO.getVoucherArrayList().isEmpty()) {
                _log.error("MobinilVoucherChangeOnHoldProcesses[process]", " No voucher for adding in file");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MobinilVoucherChangeOnHoldProcesses[process]", "", "", "", " No vouchers for adding in file");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_NO_RECORDS_ERROR);
            }
            
            // inserting the header info into voms_daily_report_master
            StringBuffer insertVoucherMasterQueryBuff = new StringBuffer("INSERT INTO voms_daily_report_master (batch_id,user_id,network_code,network_code_for,company_name,");
            insertVoucherMasterQueryBuff.append("operator_code,bank_code,batch_file_name,total_record,total_amount,batch_date,");
            insertVoucherMasterQueryBuff.append("created_by,created_on,modified_by,modified_on)");
            insertVoucherMasterQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            
            final String insertVoucherMasterQuery = insertVoucherMasterQueryBuff.toString();
            
            pstmtInsertVoucher = con.prepareStatement(insertVoucherMasterQuery);
            
            try {
            j = 1;

            pstmtInsertVoucher.setString(j++, vomsMasterVO.getBatchId());
            pstmtInsertVoucher.setString(j++, vomsMasterVO.getUserId());
            pstmtInsertVoucher.setString(j++, vomsMasterVO.getNetworkCode());
            pstmtInsertVoucher.setString(j++, vomsMasterVO.getNetworkCode());
            pstmtInsertVoucher.setString(j++, vomsMasterVO.getCompanyName());
            pstmtInsertVoucher.setString(j++, vomsMasterVO.getOperatorCode());
            pstmtInsertVoucher.setString(j++, vomsMasterVO.getBankCode());
            pstmtInsertVoucher.setString(j++, vomsMasterVO.getBatchFileName());
            pstmtInsertVoucher.setLong(j++, vomsMasterVO.getTotalRecord());
            pstmtInsertVoucher.setLong(j++, vomsMasterVO.getTotalAmount());
            pstmtInsertVoucher.setDate(j++, BTSLUtil.getSQLDateFromUtilDate(vomsMasterVO.getBatchDate()));
            pstmtInsertVoucher.setString(j++, vomsMasterVO.getCreatedBy());
            pstmtInsertVoucher.setDate(j++, BTSLUtil.getSQLDateFromUtilDate(vomsMasterVO.getCreatedOn()));
            pstmtInsertVoucher.setString(j++, vomsMasterVO.getModifiedBy());
            pstmtInsertVoucher.setDate(j++, BTSLUtil.getSQLDateFromUtilDate(vomsMasterVO.getModifiedOn()));
            
            updateCount = pstmtInsertVoucher.executeUpdate();

            if (updateCount > 0) {
                updateCount = 0;
                j = 1;
                
            } else {
                _log.error("MobinilVoucherChangeOnHoldProcesses[process]", " unable to update voucher in voms_voucher tables SerialNumber=" + vomsDetailVO.getSerialNumber());
            }
            }
            finally
            {
            	if (pstmtInsertVoucher != null) {
            		pstmtInsertVoucher.close();
                }
            	
            }

            // inserting the header info into voms_daily_report_details
            StringBuffer insertVoucherDetailsQueryBuff = new StringBuffer("INSERT INTO voms_daily_report_details (batch_id,branch_code,city_code,terminal_type,");
            insertVoucherDetailsQueryBuff.append("terminal_code,credit_sale_date,serial_no,serial_no_cdigit,payment_id_number,");
            insertVoucherDetailsQueryBuff.append("payment_type,txn_id,card_number,created_by,created_on,modified_by,modified_on,status,");
            insertVoucherDetailsQueryBuff.append("remarks,external_code,user_id,voucher_status,product_id,deliver_date)");
            insertVoucherDetailsQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            
            final String insertVoucherDetailsQuery = insertVoucherDetailsQueryBuff.toString();
            
            //pstmtInsertVoucher = con.prepareStatement(insertVoucherDetailsQuery);
            
            ArrayList voucherUploadArr = _voucherUploadVO.getVoucherArrayList();
            
            int   actualNoOfRecords = _voucherUploadVO.getActualNoOfRecords();
            invalidSerialHashMap = new LinkedHashMap<String, String>();
            
            for (int i = 0; i < actualNoOfRecords; i++) {
            	
            	try {
            	pstmtInsertVoucher = con.prepareStatement(insertVoucherDetailsQuery);
                j = 1;
                vomsDetailVO = (VomsDetailVO) voucherUploadArr.get(i);
                
                
                vomsVoucherDAO = new VomsVoucherDAO();
                vomsVoucherVO = vomsVoucherDAO.loadVomsVoucherVO(con, vomsDetailVO);
                
                if(vomsVoucherVO != null ) {
                	status = PretupsI.SUCCESS;
                	remarks = "Serial number is valid";
                	
                	if(!_channelUserVO.getUserID().equals(vomsVoucherVO.getUserID())) {
                		_log.error(" validateVoucherFile", " Serial number is not mapped to user id = ");
                		invalidSerialHashMap.put(vomsDetailVO.getSerialNumber(), "Serial number is not mapped to user id");
                		status = PretupsI.FAIL;
                		remarks = "Serial number is not mapped to user id";
                		continue;
                	}
                	
                } else {
                	_log.error(" validateVoucherFile", " Serial number is not valid = " + PretupsErrorCodesI.VOUCHER_NOT_FOUND);
                	invalidSerialHashMap.put(vomsDetailVO.getSerialNumber(), "Serial number is not valid");
                	status = PretupsI.FAIL;
                	remarks = "Serial number is not valid";
                	continue;
                }
                
            	boolean isDuplicateSerialNumber = false;
                StringBuilder strBuff = new StringBuilder();
                strBuff.append("SELECT SERIAL_NO from VOMS_DAILY_REPORT_DETAILS where SERIAL_NO=?");
                
            	String sqlSelect = strBuff.toString();
               
                try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
                    pstmt.setString(1, vomsDetailVO.getSerialNumber());
                    try(ResultSet rs = pstmt.executeQuery();) {
	                    if (rs.next()) {
	                    	isDuplicateSerialNumber = true;
	                    }
                    }
                }
                
                if(isDuplicateSerialNumber) {
                	invalidSerialHashMap.put(vomsDetailVO.getSerialNumber(), "Found duplicate serial number and it is already processed");
                	invalidVoucherCount++;
                	continue;
                }
                
                /*if(vomsVoucherVO != null ) {
                	status = PretupsI.SUCCESS;
                	remarks = "Serial number is valid";
                	
                	if(!_channelUserVO.getUserID().equals(vomsVoucherVO.getUserID())) {
                		_log.error(" validateVoucherFile", " Serial number is not mapped to user id = " + PretupsErrorCodesI.SERIAL_NO_NOT_MAPPED_WITH_USER);
                		inValidHashMap.put(vomsDetailVO.getSerialNumber(), "Serial number is not mapped to user id");
                		status = PretupsI.FAIL;
                		remarks = "Serial number is not mapped to user id";
                	}
                	
                } else {
                	_log.error(" validateVoucherFile", " Serial number is not valid = " + PretupsErrorCodesI.VOUCHER_NOT_FOUND);
                	inValidHashMap.put(vomsDetailVO.getSerialNumber(), "Serial number is not valid");
                	status = PretupsI.FAIL;
                	remarks = "Serial number is not valid";
                }*/
                
                pstmtInsertVoucher.setString(j++, batchId);
                pstmtInsertVoucher.setString(j++, vomsDetailVO.getBranchCode());
                pstmtInsertVoucher.setString(j++, vomsDetailVO.getCityCode());
                pstmtInsertVoucher.setString(j++, vomsDetailVO.getTerminalType());
                pstmtInsertVoucher.setString(j++, vomsDetailVO.getTerminalCode());
                //Check
                pstmtInsertVoucher.setDate(j++, BTSLUtil.getSQLDateFromUtilDate(vomsDetailVO.getCreditSaleDate()));
                pstmtInsertVoucher.setString(j++, vomsDetailVO.getSerialNumber());
                pstmtInsertVoucher.setString(j++, vomsDetailVO.getSerialNoCdigit());
                pstmtInsertVoucher.setString(j++, vomsDetailVO.getPaymentIdNumber());
                pstmtInsertVoucher.setString(j++, vomsDetailVO.getPaymentType());
                pstmtInsertVoucher.setLong(j++, vomsDetailVO.getTxnId());
                pstmtInsertVoucher.setLong(j++, vomsDetailVO.getCardNumber());
                pstmtInsertVoucher.setString(j++, vomsDetailVO.getCreatedBy());
                pstmtInsertVoucher.setDate(j++, BTSLUtil.getSQLDateFromUtilDate(vomsDetailVO.getCreatedOn()));
                pstmtInsertVoucher.setString(j++, vomsDetailVO.getModifiedBy());
                pstmtInsertVoucher.setDate(j++, BTSLUtil.getSQLDateFromUtilDate(vomsDetailVO.getModifiedOn()));
                pstmtInsertVoucher.setString(j++, status);
                pstmtInsertVoucher.setString(j++, remarks);
                pstmtInsertVoucher.setString(j++, _channelUserVO.getExternalCode());
                pstmtInsertVoucher.setString(j++, _channelUserVO.getUserID());
                pstmtInsertVoucher.setString(j++, PretupsI.VOUCHER_SOLD_STATUS);
                
                if(vomsVoucherVO != null ) {
                	pstmtInsertVoucher.setString(j++, vomsVoucherVO.getProductID());
                } else {
                	pstmtInsertVoucher.setString(j++, "");
                }
                
                pstmtInsertVoucher.setDate(j++, BTSLUtil.getSQLDateFromUtilDate(_currentDate));
                
                updateCount = pstmtInsertVoucher.executeUpdate();
                
            }
            finally {
            	if (pstmtInsertVoucher != null) {
            		pstmtInsertVoucher.close();
                }
            	
            }

                if (updateCount > 0) {
                    updateCount = 0;
                    j = 1;
                    k = 1;
                    
                    StringBuffer updateQueryBuff = new StringBuffer(" UPDATE voms_vouchers SET   ");
                    updateQueryBuff.append(" SOLD_STATUS=?, SOLD_DATE=? ");
                    //updateQueryBuff.append(" modified_by=?, modified_on=? ");
                    updateQueryBuff.append(" WHERE serial_no=? ");
                    String updateQuery = updateQueryBuff.toString();
                    
                    if (_log.isDebugEnabled()) {
                        _log.debug("process", "Update query:" + updateQuery);
                    }
                    
                    pstmtInsertVoucherStatus = con.prepareStatement(updateQuery);

                    try {
                    pstmtInsertVoucherStatus.setString(k++, PretupsI.YES);
                    pstmtInsertVoucherStatus.setDate(k++, BTSLUtil.getSQLDateFromUtilDate(vomsDetailVO.getCreditSaleDate()));
                    //pstmtInsertVoucherStatus.setString(k++, vomsDetailVO.getUserId());
                    //pstmtInsertVoucherStatus.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(vomsDetailVO.getModifiedOn()));
                    pstmtInsertVoucherStatus.setString(k++, vomsDetailVO.getSerialNumber());
                    
                    updateCount = pstmtInsertVoucherStatus.executeUpdate();
                    
                    if (updateCount > 0) {
                        updateCount = 0;
                        k = 1;
                        //updateCount = updateVoucherAuditStatus(p_con, p_Operation, p_transferVO, p_vomsVoucherVO);
                    } else {
                    	invalidVoucherCount++;
                    }
                    }
                    finally 
                    {
                    	 if (pstmtInsertVoucherStatus != null) {
                    		 pstmtInsertVoucherStatus.close();
                         }
                    }
                    
                    
                } else {
                    _log.error("MobinilVoucherChangeOnHoldProcesses[process]", " unable to update voucher in voms_voucher tables SerialNumber=" + vomsDetailVO.getSerialNumber());
                    continue;
                }
            }
            	if(invalidVoucherCount == actualNoOfRecords) {
            		invalidSerialHashMap.put("INVALID_FILE", "All records are invalid in file");
                	createAndMoveFailureFileToFinalDirectory(_fileName, _filePath, _moveLocation, invalidSerialHashMap);
                	isFileMoved = true;
                	mcomCon.finalRollback();
            	} else {
            		con.commit();
            		
            		if(invalidSerialHashMap.isEmpty()) {
            			moveFileToAnotherDirectory(_fileName, _filePath, _moveLocation);
                    	isFileMoved = true;
            		} else {
            			//inValidHashMap.put("INVALID_FILE", "All records are invalid in file");
            			createAndMoveFailureFileToFinalDirectory(_fileName, _filePath, _moveLocation, invalidSerialHashMap);
            			moveFileToAnotherDirectory(_fileName, _filePath, _moveLocation);
                    	//createAndMoveFailureFileToFinalDirectory(_fileName, _filePath, _moveLocation, inValidHashMap);
                    	isFileMoved = true;
            		}
            	}
	            
			} // end of a single file processing
			
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.info(methodName, "Voucher Upload Process Failed .Check the log file or OAM Screen or Event log for exact errors ");
            if (con != null) {
                try {
                	mcomCon.finalRollback();
                } catch (SQLException e1) {
                    _log.errorTrace(methodName, e1);
                }
            }
            throw be;
        } catch (Exception e) {
            _log.info(methodName, "Voucher Upload Process Failed .Check the log file or OAM Screen or Event log for exact errors ");
            try {
            	mcomCon.finalRollback();
            } catch (SQLException e1) {
                _log.errorTrace(methodName, e1);
            }
            _log.errorTrace(methodName, e);
            _log.error("MobinilVoucherChangeOnHoldProcesses[process]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherChangeOnHoldProcesses[process]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses ", " process ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
        	
        	// if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
            if (processStatusOK) {
                try {
                    if (markProcessStatusAsComplete(con, ProcessI.VOUCHER_SALES_DAILY_REPORT_PROCCESSID) == 1) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            _log.errorTrace(methodName, e);
                        }
                    } else {
                        try {
                        	mcomCon.finalRollback();
                        } catch (Exception e) {
                        	_log.errorTrace(methodName, e);
                        }
                    }
                } catch (Exception e) {
                	_log.errorTrace(methodName, e);
                }
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Exception ex) {
                    if (_log.isDebugEnabled()) {
                    	_log.debug(methodName, "Exception closing connection ");
                    }
                    _log.errorTrace(methodName, ex);
                }
            }
            
            try {
                if (pstmtInsertVoucher != null) {
                    pstmtInsertVoucher.close();
                }
                
                if (pstmtInsertVoucherStatus != null) {
                	pstmtInsertVoucherStatus.close();
                }
                
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            } if (con != null) {
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
            //populateVoucherUploadVO();

            // to validate the file length
            getFileLength();

            // called to validate the file, whole file at the time.
            if(!isFileMoved) {
            	validateVoucherFile();
            }
            
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
            _log.debug(" getFileLength", " Entered ");
        }
        int lineCount = 0;
        String fileData = null;
        BufferedReader inFile = null;
        int fileLineCount = -1;
       // PreparedStatement pstmtInsertVoucherUpdate = null;
       //  PreparedStatement pstmtDailyReport = null;
       //  int updateCount = 0;
        LinkedHashMap<String, String> invalidHashMap = null;
        UserDAO userDAO = null;
        Connection con = null;
    	MComConnectionI mcomCon = null;
        
        //VomsBatchVO vomsBatchVO = null;
        
        try {
        	mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            if (con == null) {
                _log.info(METHOD_NAME, " Could not connect to database. Please make sure that database server is up..............");
                _log.error("main ", ": Could not connect to database. Please make sure that database server is up..............");
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsSalesVoucherReport[process]", "", "", "", "Could not connect to Database");
                throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", "main", PretupsErrorCodesI.VOUCHER_ERROR_CONN_NULL);
            }
        	
        	/*File Naming Format validation - START*/
        	
        	String bankCode = null; //Ex. For Melli bank is "MLI" (3)
            String reportDate = null; //YYMMDD format (Persian calender) (6)
            String UD02 = null; //"UD02" The fixed term (4)
            String fileCreationSerialNo = null; //File Generation serial number for this report (3)
            String creationTime = null; //Report creation time 6 HHMMSS format (6)
           // String operatorCode = null; //Operator code/File format 4 For RighTel it fills with ".920" (4)
            invalidHashMap = new LinkedHashMap<String, String>();
            
            if(_fileName.length() > 22) {
            	String fileExtn = "";
            	
            	int i = _fileName.lastIndexOf('.');
            	
            	if (i > 0) {
            		String checkFileName = _fileName.substring(0, i);
            		if(checkFileName.length()!=22) {
            			invalidHashMap.put("INVALID_FILE_NAME_LENGTH", "File name length is not valid");
            			createAndMoveFailureFileToFinalDirectory(_fileName, _filePath, _moveLocation, invalidHashMap);
            			isFileMoved = true;
                	} else {
                		fileExtn = _fileName.substring(i+1);
                		if (_log.isDebugEnabled()) {
                            _log.debug(".......fileExtn.......", " fileExtn=" + fileExtn);
                        }
                		
                		if(fileExtn.equals("920")) {
                			bankCode = _fileName.substring(0, 3);
                			if(bankCode.length()!=3) {
                				invalidHashMap.put("INVALID_BANK_CODE", "Bank code is not valid");
                    			isFileNameErrorFound = true;
                			}
                			
                			reportDate = _fileName.substring(3, 9);
                			//Need to validate the date if it is in persian date
                			
                			if(reportDate.length()!=6) {
                				invalidHashMap.put("INVALID_DATE", "Date is not valid");
                    			isFileNameErrorFound = true;
                			}
                			
                			UD02 = _fileName.substring(9, 13);
                			if(!"UD02".equalsIgnoreCase(UD02) || UD02.length()!=4) {
                				invalidHashMap.put("UD02", "UD02 is not valid");
                    			isFileNameErrorFound = true;
                			}
                			
                			fileCreationSerialNo = _fileName.substring(13, 16);
                			if(fileCreationSerialNo.length()!=3) {
                				invalidHashMap.put("INVALID_SERIAL_NO", "Serial number is not valid");
                    			isFileNameErrorFound = true;
                			}
                			
                			creationTime = _fileName.substring(16, 22);
                			if(creationTime.length()!=6) {
                				invalidHashMap.put("INVALID_CREATION_TIME", "Creation time is not valid");
                    			isFileNameErrorFound = true;
                			}
                			
                			if(isFileNameErrorFound) {
                    			createAndMoveFailureFileToFinalDirectory(_fileName, _filePath, _moveLocation, invalidHashMap);
                    			isFileMoved = true;
                			}
                			
                		} else {
                			invalidHashMap.put("INVALID_EXTN", "Not a valid file extension");
                			createAndMoveFailureFileToFinalDirectory(_fileName, _filePath, _moveLocation, invalidHashMap);
                			isFileMoved = true;
                		}
                	}
            	}
            	
            } else {
            	invalidHashMap.put("INVALID_FILE_NAME_LENGTH", "File name length is not valid");
            	createAndMoveFailureFileToFinalDirectory(_fileName, _filePath, _moveLocation, invalidHashMap);
            	isFileMoved = true;
    			// throw new BTSLBaseException("VomsSalesVoucherReport", "getFileLength", PretupsErrorCodesI.VOUCHER_ERROR_INVALID_FILENAME_FORMAT);
            }
            
            /*File Naming Format validation - END*/
        	
            
            if(!isFileMoved) {
            	
            _fileDataArr = new ArrayList();
            File srcFile = null;

            // creates the actual path of the file
            if (BTSLUtil.isNullString(_filePath)) {
                srcFile = new File(_fileName);
            } else {
                srcFile = new File(_filePath + File.separator + _fileName);
            }
            
            if (_log.isDebugEnabled()) {
                _log.debug("getFileLength", " Starting processing to get the header & number of records for source File Path = " + srcFile + " File Name=" + _fileName);
            }
            
            // creates a new BufferedReader to read the Voucher Upload file
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
                
                // count 0 for fileHeader and rest for fileData
                if(fileLineCount==0) {
                	_fileHeader = fileData;
                	
                	if(_fileHeader.length()!=30) {
                		invalidHashMap.put("INVALID_HEADER_LENGTH", "Header length is not valid");
                		isFileHeaderErrorFound = true;
            			}
                	
                	String serviceCompany = _fileHeader.subSequence(0, 1).toString(); //For RighTel it fills with "9" (1)
                	if(serviceCompany.length()!=1) {
                		invalidHashMap.put("INVALID_SER_COMPANY", "Service company name length is not valid");
                		isFileHeaderErrorFound = true;
        			}
                	
                    String operatorExtCode = _fileHeader.subSequence(1, 4).toString(); //For RighTel it fills with "920" (3)
                    if(operatorExtCode.length()!=3) {
                    	invalidHashMap.put("INVALID_OPT_CODE_LEN", "Operator code length is not valid");
                		isFileHeaderErrorFound = true;
        			} 
                    
                    String operatingBankCode = _fileHeader.subSequence(4, 6).toString(); //Ex. For Melli bank fills with 17 (2)
                    if(operatingBankCode.length()!=2) {
                    	invalidHashMap.put("INVALID_BANK_CODE_LEN", "Operating bank code length is not valid");
                		isFileHeaderErrorFound = true;
        			}
                    
                    String sendFileDateStr = _fileHeader.subSequence(6, 12).toString(); //YYMMDD format (Persian calender) (6)
                    if(sendFileDateStr.length()!=6) {
                    	invalidHashMap.put("SEND_FILE_DATE_LEN", "Send file date length is not valid");
                		isFileHeaderErrorFound = true;
        			}
                    
                    String totalAmtSoldVouchers = _fileHeader.subSequence(12, 22).toString(); //Total amount to pay divided by 1000, If the amount is less than 10 digits, the left is filled with zeros (10)
                    if(totalAmtSoldVouchers.length()!=10) {
                    	invalidHashMap.put("INVALID_TOTAL_AMT_LEN", "Total amount length is not valid");
                		isFileHeaderErrorFound = true;
        			}
                    
                    String soldCardQty = _fileHeader.subSequence(22, 30).toString(); //If the number is less than 8 digits, the left is filled with zeros (8)
                    if(soldCardQty.length()!=8) {
                    	invalidHashMap.put("INVALID_SOLD_CARD_QTY", "Sold card quantity is not valid");
                		isFileHeaderErrorFound = true;
        			}
                    
                    userDAO = new UserDAO();
        			_channelUserVO = userDAO.loadAllUserDetailsByExternalCode(con, operatorExtCode);
        			
        			if(_channelUserVO==null) {
        				invalidHashMap.put("INVALID_OPT_CODE", "Invalid Operator code found in header");
                		isFileHeaderErrorFound = true;
        			}
        			
                    if(isFileHeaderErrorFound)
                    {
                    	createAndMoveFailureFileToFinalDirectory(_fileName, _filePath, _moveLocation, invalidHashMap);
                    	isFileMoved = true;
                    	break;
                    }
                    
                    vomsMasterVO.setUserId(_channelUserVO.getUserID());
                    vomsMasterVO.setNetworkCode(_channelUserVO.getNetworkID());
                    vomsMasterVO.setNetworkCodeFor(_channelUserVO.getNetworkID());
                    vomsMasterVO.setCompanyName(serviceCompany);
                    vomsMasterVO.setOperatorCode(operatorExtCode);
                    vomsMasterVO.setBankCode(operatingBankCode);
                    vomsMasterVO.setBatchFileName(_fileName);
                    vomsMasterVO.setTotalRecord(Long.parseLong(soldCardQty));
                    vomsMasterVO.setTotalAmount(Long.parseLong(totalAmtSoldVouchers));
                    vomsMasterVO.setBatchDate(BTSLDateUtil.getDateFromId(sendFileDateStr));
                    vomsMasterVO.setCreatedBy(_channelUserVO.getUserID());
                    vomsMasterVO.setModifiedBy(_channelUserVO.getUserID());
                    vomsMasterVO.setCreatedOn(_currentDate);
                    vomsMasterVO.setModifiedOn(_currentDate);
                    
	                    batchNo = String.valueOf(IDGenerator.getNextID(VOMSI.VOMS_BATCHES_DOC_TYPE, String.valueOf(BTSLUtil.getFinancialYear()), VOMSI.ALL));
	                    batchId = new VomsUtil().formatVomsBatchID(vomsMasterVO, batchNo);
	                    vomsMasterVO.setBatchId(batchId);
                	
                    	continue;
                	}
                
                	_fileDataArr.add(fileData);
                }
                
            }

            //_voucherUploadVO.setNoOfRecordsInFile(fileLineCount);

            // this is used to check if the actual number of records in the file
            // and the records
            // entered by the user are same or not
            /*if (_numberOfRecords != fileLineCount) {
                _log.error("getFileLength", " Total Number of Records (" + (fileLineCount) + ") in the File (" + _fileName + ") doesn't match the entered value = " + _numberOfRecords + ". Control Returning ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MobinilVoucherFileChecks[getFileLength]", "", "", "", "The number of records (" + (fileLineCount) + ") in the file doesn't match the entered value (" + _numberOfRecords + ")");
                throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", "getFileLength", PretupsErrorCodesI.VOUCHER_ERROR_INVALID_RECORD_COUNT);// "Total number of records in the file are different from the one specified"
            }*/
            
        }// end of try block
        catch (BTSLBaseException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(" getFileLength", " BTSLBaseException = " + e);
            
            /*if (con != null) {
                try {
                	mcomCon.finalRollback();
                } catch (SQLException e1) {
                    _log.errorTrace(METHOD_NAME, e1);
                }
            }*/
            
            throw e;
        }// end of Exception
        catch (Exception e) {
        	
        	/*if (con != null) {
                try {
                    con.close();
                } catch (Exception e1) {
                    _log.errorTrace(METHOD_NAME, e1);
                }
            }*/
        	
        	/*try {
        		mcomCon.finalRollback();
            } catch (SQLException e1) {
                _log.errorTrace(METHOD_NAME, e1);
            }*/
        	
            _log.errorTrace(METHOD_NAME, e);
            _log.error("getFileLength", "The file (" + _fileName + ")could not be read properly to get the number of records");
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MobinilVoucherChangeOnHoldProcesses[getFileLength]", "", "", "", "The file (" + _fileName + ")could not be read properly to get the number of records");
            throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", "getFileLength", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);
        }// end of IOException
        finally {
            try {
            	if (con != null) {
                    try {
                        con.close();
                    } catch (Exception e1) {
                        _log.errorTrace(METHOD_NAME, e1);
                    }
                }
            	
            	if(!isFileMoved)
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
    public VomsDetailVO validateVoucherFile() throws BTSLBaseException {
        final String METHOD_NAME = "validateVoucherFile";
        if (_log.isDebugEnabled()) {
            _log.debug(" validateVoucherFile", " Entered to validate the file _fileName=" + _fileName);
        }

        int runningErrorCount = 0;
        int runningFileRecordCount = 0;
        String fileData = null;
        //String headerData = null;
        int minSerialLength = 0;
        int maxSerialLength = 0;
        LinkedHashMap<String, String> invalidDataHashMap = null;
        HashSet<String> duplicateSerialNumberSet = null;
        
        try {
            minSerialLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MIN_LENGTH))).intValue();
            maxSerialLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH))).intValue();
            
            invalidDataHashMap = new LinkedHashMap<String, String>();
            duplicateSerialNumberSet = new HashSet<String>();
            
            _voucherArr = new ArrayList();
            Iterator fileItr = _fileDataArr.iterator();
            
            String branchCode = null;
            String cityCode = null;
            String terminalType = null;
            String terminalCode = null;
            Date creditSaleDate = null;
            String soldSerialNumber = null;
            String serialNoCdigit = null;
            String paymentIdNumber = null;
            String paymentIdDigit = null;
            long txnId = 0;
            long cardNumber = 0;
            String status = null;
            String remarks = null;
            String externalCode = null;
            String voucherStatus = null;
            String productId = null;
            //Iterator<ListValueVO> itr= null;
            String dataFile[] = null;
            
            
           
            
            
            // isValidFile = true;
            while (fileItr.hasNext()) {
                fileData = null;
                fileData = (String) fileItr.next();
                runningFileRecordCount++;// keeps record of number of records in
                                         // file
                /*if(runningFileRecordCount==0) {
                	headerData = fileData; 
                }*/

                if (BTSLUtil.isNullString(fileData)) {
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " No Data Found");
                    runningErrorCount++;
                    continue;
                }
                // checks if the number of error encontered while parsing of
                // file does not
                // exceed the user given error count limit
                /*if (runningErrorCount > _allowedNumberofErrors) {
                    // isValidFile = false;
                    _log.error("validateVoucherFile", " Total Number of error (" + runningErrorCount + ") in the File (" + _fileName + ") exceeds the user specified error number= " + _allowedNumberofErrors);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MobinilVoucherChangeOnHoldProcesses[validateVoucherFile]", "", "", "", "The number of error (" + runningErrorCount + ") in the file exceed the user specified value (" + _allowedNumberofErrors + ")");
                    throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);// "Total number of error in the file exceed the specified number of error"
                }*/
                dataFile = fileData.split(_valueSeparator);
                if (dataFile.length != 11) {
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " Data should contain= Serial Number, Changed Status but given data is=" + dataFile.toString());
                    runningErrorCount++;
                    invalidDataHashMap.put("Error in record number-"+runningFileRecordCount, "Record lenght is not valid");
                    isFileDataErrorFound = true;
                    continue;
                }
                
                branchCode = dataFile[0];
                if(branchCode.length()!=8) {
                	runningErrorCount++;
                	invalidDataHashMap.put("Error in record number-"+runningFileRecordCount, "Branch code length is not valid");
                    isFileDataErrorFound = true;
                    continue;
    			}
                
                cityCode = dataFile[1];
                if(cityCode.length()!=6) {
                	runningErrorCount++;
                	invalidDataHashMap.put("Error in record number-"+runningFileRecordCount, "City code length is not valid");
                    isFileDataErrorFound = true;
                    continue;
    			}
                
                terminalType = dataFile[2];
                if(terminalType.length()!=2) {
                	runningErrorCount++;
                	invalidDataHashMap.put("Error in record number-"+runningFileRecordCount, "Terminal type length is not valid");
                    isFileDataErrorFound = true;
                    continue;
    			}
                
                itr = lookupList.iterator(); 
                
                String ttype = null;
                boolean found = false;
                while(itr.hasNext()) {
                	ttype = itr.next().getValue();
                	if(ttype.equals(terminalType)) {
        				//Need to correct
                		found = true;
                		break;
        			}
                }
                
                if(found) {
                	if (_log.isDebugEnabled()) {
                        _log.debug("validateVoucherFile ", " Terminal type is... " + terminalType);
                    }
                } else {
                	runningErrorCount++;
                	invalidDataHashMap.put("Error in record number-"+runningFileRecordCount, "Terminal type is not valid");
                    isFileDataErrorFound = true;
                    continue;
                }
                
                terminalCode = dataFile[3];
                if(terminalCode.length()!=8) {
                	runningErrorCount++;
                	invalidDataHashMap.put("Error in record number-"+runningFileRecordCount, "Terminal code length is not valid");
                    isFileDataErrorFound = true;
                    continue;
    			}
                
                String creditSaleDateStr = dataFile[4];
                
                soldSerialNumber = dataFile[5];
                //ERROR_FROM_TO_SERIALNO_INVALID
                if (!VoucherFileUploaderUtil.isValidDataLength(soldSerialNumber.length(), minSerialLength, maxSerialLength)) {
                	runningErrorCount++;
                	invalidDataHashMap.put("Error in record number-"+runningFileRecordCount, "Sold serial number length is not valid");
                    isFileDataErrorFound = true;
                    continue;
                }
                
               /* if(soldSerialNumber.length()!=Integer.parseInt(Constants.getProperty("DAILY_SALES_VOMS_SERIALNO_LENGTH"))) {
                	runningErrorCount++;
                	invalidDataHashMap.put("Error in record number-"+runningFileRecordCount, "Sold serial number length is not valid");
                    isFileDataErrorFound = true;
                    continue;
    			}*/
                
                if(duplicateSerialNumberSet.isEmpty()) {
                	duplicateSerialNumberSet.add(soldSerialNumber);
                } else {
                	if(duplicateSerialNumberSet.contains(soldSerialNumber)) {
                		runningErrorCount++;
                		invalidDataHashMap.put("Error in record number-"+runningFileRecordCount, "Duplicate serial number found in file");
                        isFileDataErrorFound = true;
                        continue;
                    } else {
                    	duplicateSerialNumberSet.add(soldSerialNumber);
                    }
                }
                
                serialNoCdigit = dataFile[6];
                if(serialNoCdigit.length()!=1) {
                	runningErrorCount++;
                	invalidDataHashMap.put("Error in record number-"+runningFileRecordCount, "Serial number control digit length is not valid");
                    isFileDataErrorFound = true;
                    continue;
    			}
                
                paymentIdNumber = dataFile[7];
                if(paymentIdNumber.length()!=11) {
                	runningErrorCount++;
                	invalidDataHashMap.put("Error in record number-"+runningFileRecordCount, "Payment id number length is not valid");
                    isFileDataErrorFound = true;
                    continue;
    			}
                
                paymentIdDigit = dataFile[8];
                if(paymentIdDigit.length()!=1) {
                	runningErrorCount++;
                	invalidDataHashMap.put("Error in record number-"+runningFileRecordCount, "Payment id digit is not valid");
                    isFileDataErrorFound = true;
                    continue;
    			}
                
                txnId = Long.parseLong(dataFile[9]);
                cardNumber = Long.parseLong(dataFile[10]);                     

                /*if (runningFileRecordCount == 1) {
                	vomsDetailVO.setFromSerialNo(soldSerialNumber);// set only for the first time
                }*/
                
                vomsDetailVO = new VomsDetailVO();
                
                vomsDetailVO.setBatchId(batchId);
                vomsDetailVO.setBranchCode(branchCode);
                vomsDetailVO.setCityCode(cityCode);
                vomsDetailVO.setTerminalType(terminalType);
                vomsDetailVO.setTerminalCode(terminalCode);
                creditSaleDate = BTSLDateUtil.getDateFromId(creditSaleDateStr);
                vomsDetailVO.setCreditSaleDate(creditSaleDate);
                vomsDetailVO.setSerialNumber(soldSerialNumber);
                vomsDetailVO.setSerialNoCdigit(serialNoCdigit);
                vomsDetailVO.setPaymentIdNumber(paymentIdNumber);
                vomsDetailVO.setPaymentType(paymentIdDigit);
                vomsDetailVO.setTxnId(txnId);
                vomsDetailVO.setCardNumber(cardNumber);
                vomsDetailVO.setCreatedBy(_channelUserVO.getUserID());
                vomsDetailVO.setCreatedOn(_currentDate);
                vomsDetailVO.setModifiedBy(_channelUserVO.getUserID());
                vomsDetailVO.setModifiedOn(_currentDate);
                vomsDetailVO.setStatus(status);
                vomsDetailVO.setRemarks(remarks);
                vomsDetailVO.setExternalCode(externalCode);
                vomsDetailVO.setUserId(_channelUserVO.getUserCode());
                vomsDetailVO.setVoucherStatus(voucherStatus);
                vomsDetailVO.setProductID(productId);
                
                _voucherArr.add(vomsDetailVO);
                
            }// end of while loop
            
            if(isFileDataErrorFound) {
            	createAndMoveFailureFileToFinalDirectory(_fileName, _filePath, _moveLocation, invalidDataHashMap);
            	isFileMoved = true;
            }
            
             // checks if the number of error encontered while parsing of file
             // does not
             // exceed the user given error count limit in case the exception
             // occurs at the last line
            /*if (runningErrorCount > _allowedNumberofErrors) {
                _log.error("validateVoucherFile", " Total Number of error (" + runningErrorCount + ") in the File (" + _fileName + ") exceeds the user specified error number= " + _allowedNumberofErrors);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MobinilVoucherChangeOnHoldProcesses[validateVoucherFile]", "", "", "", "The number of error (" + runningErrorCount + ") in the file exceed the user specified value (" + _allowedNumberofErrors + ")");
                throw new BTSLBaseException("MobinilVoucherChangeOnHoldProcesses", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);// "Total number of error in the file exceed the specified number of error"
            }*/
           // _voucherUploadVO.setToSerialNo(soldSerialNumber);
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
        	if (_log.isDebugEnabled()) {
            	_log.debug("validateVoucherFile", "Exiting..... ");
            }
        }
        return vomsDetailVO;
    }
    
    /**
	 * @param fileName
	 * @param oldFilePath
	 * @param p_finalDirectoryPath
	 * @param invalidHashMap
	 * @throws BTSLBaseException
	 */
	private static void createAndMoveFailureFileToFinalDirectory(String fileName, String filePath, String moveLocationPath, Map<String, String> invalidHashMap) throws BTSLBaseException
	{
		final String methodName = "createAndMoveFailureFileToFinalDirectory";
		LogFactory.printLog(methodName, " Entered: fileName="+ fileName+" moveLocationPath="+ moveLocationPath + "filePath "+filePath, _log);
		
		String newFileName = "";
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
		int lastDot = fileName.lastIndexOf('.');
		newFileName = fileName.substring(0,lastDot) + "_FAILED_" + timeStamp + fileName.substring(lastDot);
		
		File parentDir = new File(moveLocationPath);
		if(!parentDir.exists())
			parentDir.mkdirs();
		
		String newDirName=moveLocationPath;
		File newDir = new File(newDirName);
		if(!newDir.exists())
			newDir.mkdirs();
		
		File oldFile = new File(filePath);
		
		File actualFile = new File (newDir, newFileName);
		
		LogFactory.printLog(methodName, " newDirName="+newDirName, _log);

		LogFactory.printLog(methodName, " actualFile="+actualFile, _log);
		
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(actualFile);
			
			Set<String> keys = invalidHashMap.keySet();
		        for(String key: keys){
		        	LogFactory.printLog(methodName, " key: "+key, _log);
		        }
		
		        Set<String> keySet = invalidHashMap.keySet();
		        Iterator<String> keySetIterator = keySet.iterator();
		        while (keySetIterator.hasNext()) {
		        	
		           String key = keySetIterator.next();
		          LogFactory.printLog(methodName, " Iterating Map in Java using KeySet Iterator key value is ="+key + "value" +invalidHashMap.get(key), _log);
		          
		          	writer.append(key);
				    writer.append(',');
				    writer.append(invalidHashMap.get(key));
			        writer.append('\n');
		           
		        }

		    writer.flush();
			
		    //oldFile.delete();
		}
		catch(Exception e)
		{
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName,e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DWHFileCreationGP[moveFilesToFinalDirectory]","","","","Exception:"+e.getMessage());
		    //throw new BTSLBaseException(CLASS_NAME,methodName,PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
		}
        finally
        {
			if(parentDir!=null) parentDir = null;
			if(newDir!=null) newDir = null;
			//if(oldDir!=null) oldDir = null;
            LogFactory.printLog(methodName, "Exiting.. ", _log);
            
            try {
				if(writer!=null) {
					writer.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				_log.error(methodName,"IOException e="+e.getMessage());
			}
        } // end of finally
	}
	
	/** 
	 * This method will move the processed file in separate folder
	 * @param p_fileName1 String
	 * @param pathWithFileName1 String
	 * @param path2 String
	 * @throws BTSLBaseException
	 */
	public void moveFileToAnotherDirectory(String fileName, String filePath, String moveLocationPath) throws BTSLBaseException
	{
		final String METHOD_NAME = "moveFileToAnotherDirectory";
		if(_log.isDebugEnabled()) {
			_log.debug(" moveFileToAnotherDirectory ","Entered with :: p_fileName1="+fileName+"  pathWithFileName1="+filePath+"path2="+moveLocationPath);
		}
		boolean flag =false;
		FileWriter writer = null;
		File newDir = null;
		try
		{
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
			int lastDot = fileName.lastIndexOf('.');
			
			newDir = new File(moveLocationPath);
			if(!newDir.exists())
				newDir.mkdirs();
			File oldFile = new File(filePath,fileName);
			
			File actualFile = new File (filePath, fileName);
			//File newFile =  new File(moveLocationPath,fileName);
			
			actualFile.renameTo(new File(newDir,fileName.substring(0,lastDot) +"_"+ timeStamp + fileName.substring(lastDot)));
			
			//	writer = new FileWriter(actualFile);
			
			//	writer.flush();
			
			oldFile.delete();
			/*File fileRead = new File(filePath);
			File fileArchive = new File(moveLocationPath);
			if(!fileArchive.isDirectory()) {
				fileArchive.mkdirs();
			}
			//newFileName=fileName.concat("_").concat(BTSLUtil.getFileNameStringFromDate(new Date())).concat(_fileExt);
			fileArchive = new File(moveLocationPath+File.separator+fileName);
			flag = fileRead.renameTo(fileArchive);*/

			EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"DirectPayOut[moveFileToAnotherDirectory]","","","","Successfully moved the File "+fileName+" to backup location ("+moveLocationPath+")");
		}
		catch(Exception be)
		{
			_log.error("DirectPayOut[moveFileToAnotherDirectory]","BTSLBaseException ="+be.getMessage());
			_log.errorTrace(METHOD_NAME,be);
			throw new BTSLBaseException("moveFileToAnotherDirectory",PretupsErrorCodesI.ERROR_MOVING_FILE_TO_FINAL_DIR);
		}
		finally
		{
			if(writer!=null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(newDir!=null) newDir = null;
			if(_log.isDebugEnabled()) {
				_log.debug(" moveFileToAnotherDirectory "," Exiting with flag="+flag);
			}
		}
	}
	
	/**
     * @param pCon
     *            Connection
     * @param pProcessId
     *            String
     * @throws BTSLBaseException
     * @return int
     */
    private int markProcessStatusAsComplete(Connection pCon, String pProcessId) throws BTSLBaseException {
        final String methodName = "markProcessStatusAsComplete";
        if (_log.isDebugEnabled()) {
        	_log.debug(methodName, " Entered:  pProcessId:" + pProcessId);
        }
        int updateCount = 0;
        final Date currentDate = new Date();
        final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        _processStatusVO.setProcessID(pProcessId);
        _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        _processStatusVO.setStartDate(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(pCon, _processStatusVO);
        } catch (Exception e) {
        	_log.errorTrace(methodName, e);
        	_log.error(methodName, "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[markProcessStatusAsComplete]", "", "",
                "", PretupsI.EXCEPTION + e.getMessage());
            throw new BTSLBaseException("VomsSalesVoucherReport", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
            	_log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }            
        } // end of finally
        return updateCount;

    }
}