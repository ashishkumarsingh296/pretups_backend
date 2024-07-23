package com.btsl.voms.vomsprocesses.businesslogic;

/*
 * @(#)MobinilVoucherFileChecks.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Siddhartha 4/07/06 Initial Creation
 * Gurjeet Singh Bedi 21/07/2006 Modified (Restructured the code)
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Parser class for voucher file processing
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
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
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsprocesses.util.VoucherFileChecksI;
import com.btsl.voms.vomsprocesses.util.VoucherFileUploaderUtil;
import com.btsl.voms.vomsprocesses.util.VoucherUploadVO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.voucher.businesslogic.VomsSerialUploadCheckVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class MobinilVoucherFileChecks implements VoucherFileChecksI {

    private String _valueSeparator = null;
    private int _numberOfRecordsEntered = 0;
    private int _pinLength = 0;
    private int _serialNumberLength = 0;
    private int _allowedNumberofErrors = 0;
    private String _startingRecord = null;
    private String _endingRecord = null;
    private int _firstPartPinLength = 0;
    private String _productID = null;
    private String _filePath = null;
    private String _fileName = null;
    private ChannelUserVO _channelUserVO = null;
    private Date _currentDate = null;

    private ArrayList _voucherArr = null;
    private VoucherUploadVO _voucherUploadVO = null;
    private ArrayList _fileDataArr = null;
    private String _defaultPIN = null;
    private static Log _log = LogFactory.getLog(MobinilVoucherFileChecks.class.getName());
    private int _maxUpladRecord = 0;

    /**
     * The method loads the values for the various properties to be used later
     * during the parsing and validation of the file
     * 
     * @return void
     * @throws BTSLBaseException
     */
    public void loadConstantValues() throws BTSLBaseException {
        final String METHOD_NAME = "loadConstantValues";
        if (_log.isDebugEnabled()) {
            _log.debug("loadConstantValues", " Entered the loadContantsValues.....");
        }
        try {
            try {
                _firstPartPinLength = Integer.parseInt(Constants.getProperty("VOMS_FIRST_PART_PIN_LENGTH"));
                if (_log.isDebugEnabled()) {
                    _log.debug("MobinilVoucherFileChecks", " loadConstantValues   VOMS_FIRST_PART_PIN_LENGTH " + _firstPartPinLength);
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" loadConstantValues ", "Invalid value for VOMS_FIRST_PART_PIN_LENGTH  in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MobinilVoucherFileChecks[loadConstantValues]", "", "", "", "Invalid value for VOMS_FIRST_PART_PIN_LENGTH in Constant File for voucher upload process");
                throw new BTSLBaseException("MobinilVoucherFileChecks ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                _allowedNumberofErrors = Integer.parseInt(Constants.getProperty("VOMS_TOTAL_ALLOWED_ERRORS"));
                if (_log.isDebugEnabled()) {
                    _log.debug("MobinilVoucherFileChecks ", " loadConstantValues   VOMS_TOTAL_ALLOWED_ERRORS " + _allowedNumberofErrors);
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
                _valueSeparator = "\"";
            }
            _defaultPIN = Constants.getProperty("VOMS_DEFAULT_PIN");
            if (BTSLUtil.isNullString(_defaultPIN)) {
                _log.info("loadConstantValues", " Entry VOMS_DEFAULT_PIN is defined blank in Constants . Thus taking default values as 123456789");
                _valueSeparator = "123456789";
            }
            _maxUpladRecord = Integer.parseInt(Constants.getProperty("VOMS_MAX_FILE_LENGTH"));
        }// end of try block
        catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("loadConstantValues", " BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("loadConstantValues", " Exception e=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherFileChecks[loadConstantValues]", "", "", "", "Exception while loading the constant values from the Constants.prop file " + e.getMessage());
            throw new BTSLBaseException("MobinilVoucherFileChecks ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" loadConstantValues", "   Exiting with _pinLength=" + _pinLength + " _firstPartPinLength=" + _firstPartPinLength + " _serialNumberLength=" + _serialNumberLength + " _allowedNumberofErrors=" + _allowedNumberofErrors + " _valueSeparator=" + _valueSeparator);
            }
        }// end of finally
    }// end of loadConstantValues

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
    public void getFileLength(VoucherUploadVO p_voucherUploadVO) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "getFileLength";
        if (_log.isDebugEnabled()) {
            _log.debug(" getFileLength", " Entered with p_voucherUploadVO=" + p_voucherUploadVO.toString() + ",_filePath=" + _filePath + ", _fileName=" + _fileName);
        }
        int lineCount = 0;
        String fileData = null;
        BufferedReader inFile = null;
        int fileLineCount = 0;
        try {
            getValuesFromVO(p_voucherUploadVO);
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

            // Set the count equal to data length for automatic upload process
            if (VoucherUploadVO._AUTOPROCESSTYPE.equalsIgnoreCase(_voucherUploadVO.getProcessType())) {
                p_voucherUploadVO.setNoOfRecordsInFile(fileLineCount);
            }

            // this is used to check if the actual number of records in the file
            // and the records
            // entered by the user are same or not
            if (_numberOfRecordsEntered != fileLineCount) {
                _log.error("getFileLength", " Total Number of Records (" + (fileLineCount) + ") in the File (" + _fileName + ") doesn't match the entered value = " + _numberOfRecordsEntered + ". Control Returning ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MobinilVoucherFileChecks[getFileLength]", "", "", "", "The number of records (" + (fileLineCount) + ") in the file doesn't match the entered value (" + _numberOfRecordsEntered + ")");
                throw new BTSLBaseException("MobinilVoucherFileChecks", "getFileLength", PretupsErrorCodesI.VOUCHER_ERROR_INVALID_RECORD_COUNT);// "Total number of records in the file are different from the one specified"
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
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MobinilVoucherFileChecks[getFileLength]", "", "", "", "The file (" + _fileName + ")could not be read properly to get the number of records");
            throw new BTSLBaseException("MobinilVoucherFileChecks", "getFileLength", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);
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
    public VoucherUploadVO validateVoucherFile() throws BTSLBaseException, Exception {
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
        int totalRecord = 0;

        try {
            minSerialLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MIN_LENGTH))).intValue();
            maxSerialLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH))).intValue();
            _voucherArr = null;
            ArrayList voucherUploadArr = new ArrayList();
            VoucherUploadVO voucherUploadVO = null;
            Iterator fileIter = _fileDataArr.iterator();
            String startSerialNumber = null;
            String endSerialNumber = null;
            String denomination = null;
            String dataFile[] = null;
            String product = null;
            // isValidFile = true;
            while (fileIter.hasNext()) {
                _voucherArr = new ArrayList();
                voucherUploadVO = new VoucherUploadVO();
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
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MobinilVoucherFileChecks[validateVoucherFile]", "", "", "", "The number of error (" + runningErrorCount + ") in the file exceed the user specified value (" + _allowedNumberofErrors + ")");
                    throw new BTSLBaseException("MobinilVoucherFileChecks", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);// "Total number of error in the file exceed the specified number of error"
                }
                dataFile = fileData.split(_valueSeparator);
                if (dataFile.length != 3) {
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " Data should contain= Denomination, Start range, End Range but given data is=" + dataFile.toString());
                    runningErrorCount++;
                    continue;
                }
                denomination = dataFile[0];
                // opening the connection
                con = OracleUtil.getSingleConnection();
                VomsProductDAO vomsProductDAO = new VomsProductDAO();
                _productID = null;
                _productID = vomsProductDAO.getProductID(con, denomination);
                // checks if the particular profile exists or not
                if (BTSLUtil.isNullString(_productID)) {
                    if (con != null) {
                        try {
                            con.close();
                        } catch (Exception e1) {
                            _log.errorTrace(METHOD_NAME, e1);
                        }
                    }

                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " DProduct does not exists for denomination:=" + denomination);
                    runningErrorCount++;
                    continue;
                }
                if (con != null) {
                    try {
                        con.close();
                    } catch (Exception e1) {
                        _log.errorTrace(METHOD_NAME, e1);
                    }
                }
                startSerialNumber = dataFile[1];
                if (!VoucherFileUploaderUtil.isValidDataLength(startSerialNumber.length(), minSerialLength, maxSerialLength)) {
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " Start Serial Number Not Valid" + startSerialNumber);
                    runningErrorCount++;
                    continue;
                }
                endSerialNumber = dataFile[2];
                if (!VoucherFileUploaderUtil.isValidDataLength(endSerialNumber.length(), minSerialLength, maxSerialLength)) {
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " End Serial Number Not Valid" + endSerialNumber);
                    runningErrorCount++;
                    continue;
                }
                String encryptedPin = null;
                try {
                    encryptedPin = VomsUtil.encryptText(_defaultPIN);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " Not able to encrypt PIN");
                    runningErrorCount++;
                    continue;
                }
                if (runningFileRecordCount == 1) {
                    product = _productID;
                    _startingRecord = startSerialNumber;// set only for the
                                                        // first time
                    _endingRecord = endSerialNumber;
                } else {
                    product = product + _valueSeparator + _productID;
                    _endingRecord = endSerialNumber;
                }
                populateValuesInVoucherList(denomination, startSerialNumber, endSerialNumber, encryptedPin, _productID);
                populateVoucherVo(voucherUploadVO);
                voucherUploadVO.setFromSerialNo(startSerialNumber);
                voucherUploadVO.setToSerialNo(endSerialNumber);
                totalRecord = totalRecord + _voucherArr.size();
                voucherUploadVO.setActualNoOfRecords(_voucherArr.size());
                voucherUploadVO.setVoucherArrayList(_voucherArr);
                voucherUploadVO.setProductID(_productID);
                voucherUploadArr.add(voucherUploadVO);
            }// end of while loop
             // checks if the number of error encontered while parsing of file
             // does not
             // exceed the user given error count limit in case the exception
             // occurs at the last line
            if (runningErrorCount > _allowedNumberofErrors) {
                _log.error("validateVoucherFile", " Total Number of error (" + runningErrorCount + ") in the File (" + _fileName + ") exceeds the user specified error number= " + _allowedNumberofErrors);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "MobinilVoucherFileChecks[validateVoucherFile]", "", "", "", "The number of error (" + runningErrorCount + ") in the file exceed the user specified value (" + _allowedNumberofErrors + ")");
                throw new BTSLBaseException("MobinilVoucherFileChecks", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);// "Total number of error in the file exceed the specified number of error"
            }
            _productID = product;
            // if the number of records in the file is greater than the number
            // in the config file, it is an error
            if (_maxUpladRecord < totalRecord) {

                _log.error("getDetailsInteractively", "Total number of records entered (" + totalRecord + ") should be less than the allowed records in file (" + _maxUpladRecord + ")");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MobinilVoucherFileChecks[validateVoucherFile]", "", "", "", "Total number of records entered (" + totalRecord + ") should be less than the allowed records in voucher file (" + _maxUpladRecord + ")");
                throw new BTSLBaseException("MobinilVoucherFileChecks", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_ERROR_NUMBER_REC_MORE_THAN_ALLWD);//
            }
            _voucherUploadVO.setFromSerialNo(_startingRecord);
            _voucherUploadVO.setToSerialNo(_endingRecord);
            _voucherUploadVO.setActualNoOfRecords(voucherUploadArr.size());
            _voucherUploadVO.setVoucherArrayList(voucherUploadArr);
            _voucherUploadVO.setProductID(_productID);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("validateVoucherFile", " : BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(" validateVoucherFile", " Exception = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherFileChecks[validateVoucherFile]", "", "", "", e.getMessage());
            throw new BTSLBaseException("MobinilVoucherFileChecks", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);

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

    /**
     * This method populates the Voms Voucher VO that will be inserted in
     * database
     * 
     * @param p_serialNo
     * @param p_pin
     * @param p_productID
     * @throws BTSLBaseException
     */
    private void populateValuesInVoucherList(String p_denomination, String p_startSerialNumber, String p_endSerialNumber, String p_pin, String p_productID) throws BTSLBaseException {
        final String METHOD_NAME = "populateValuesInVoucherList";
        if (_log.isDebugEnabled()) {
            _log.debug(" populateValuesInVoucherList", " Entered with p_denomination=" + p_denomination + " p_pin=" + p_pin + " p_productID=" + p_productID);
        }
        try {
            long startSerialNumber = Long.parseLong(p_startSerialNumber);
            long endSerialNumber = Long.parseLong(p_endSerialNumber);
            VomsVoucherVO vomsVoucherVO = null;
            if (_log.isDebugEnabled()) {
                _log.debug(" populateValuesInVoucherList", "_currentDate " + _currentDate);
            }
            long time = _currentDate.getTime() + (long) 24 * 365 * 1000 * 50 * 60 * 60;
            long diff = (endSerialNumber - startSerialNumber);
            for (long k = 0; k <= diff; k++) {
                vomsVoucherVO = new VomsVoucherVO();
                vomsVoucherVO.setSerialNo(Long.toString(startSerialNumber++));
                vomsVoucherVO.setPinNo(p_pin);
                vomsVoucherVO.setProductID(p_productID);
                vomsVoucherVO.setProductionLocationCode(_channelUserVO.getNetworkID());
                vomsVoucherVO.setModifiedBy(_channelUserVO.getUserID());
                vomsVoucherVO.setOneTimeUsage(PretupsI.YES);
                vomsVoucherVO.setStatus(VOMSI.VOUCHER_NEW);
                vomsVoucherVO.setCurrentStatus(VOMSI.VOUCHER_NEW);
                vomsVoucherVO.setModifiedOn(new Date());
                vomsVoucherVO.setCreatedOn(_currentDate);
                vomsVoucherVO.setExpiryDate(new Date(time));
                _voucherArr.add(vomsVoucherVO);
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("MobinilVoucherFileChecks[populateValuesInVoucherList]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherFileChecks[populateValuesInVoucherList]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("MobinilVoucherFileChecks ", " populateValuesInVoucherList ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" populateValuesInVoucherList", " Exiting ");
            }
        }
    }

    /**
     * This method will get the values from voucher upload VO passed from
     * controlling class to the global variables
     * 
     * @param p_voucherUploadVO
     * @throws BTSLBaseException
     */
    private void getValuesFromVO(VoucherUploadVO p_voucherUploadVO) throws BTSLBaseException {
        final String METHOD_NAME = "getValuesFromVO";
        if (_log.isDebugEnabled()) {
            _log.debug(" getValuesFromVO", " Entered with  p_voucherUploadVO=" + p_voucherUploadVO.toString());
        }
        try {
            _fileName = p_voucherUploadVO.getFileName();
            _filePath = p_voucherUploadVO.getFilePath();
            _productID = p_voucherUploadVO.getProductID();
            _numberOfRecordsEntered = p_voucherUploadVO.getNoOfRecordsInFile();

            _channelUserVO = p_voucherUploadVO.getChannelUserVO();
            _currentDate = p_voucherUploadVO.getCurrentDate();
            _voucherUploadVO = p_voucherUploadVO;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("MobinilVoucherFileChecks[getValuesFromVO]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherFileChecks[getValuesFromVO]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("MobinilVoucherFileChecks ", " getValuesFromVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" getValuesFromVO", " Exiting");
            }
        }
    }

    /**
     * This method will get the values from voucher upload VO passed from
     * controlling class to the global variables
     * 
     * @param p_voucherUploadVO
     * @throws BTSLBaseException
     */
    private void populateVoucherVo(VoucherUploadVO p_voucherUploadVO) throws BTSLBaseException {
        final String METHOD_NAME = "populateVoucherVo";
        if (_log.isDebugEnabled()) {
            _log.debug(" populateVoucherVo", " Entered with  p_voucherUploadVO=" + p_voucherUploadVO.toString());
        }
        try {
            p_voucherUploadVO.setFileName(_voucherUploadVO.getFileName());
            p_voucherUploadVO.setFilePath(_voucherUploadVO.getFilePath());
            p_voucherUploadVO.setProductID(_voucherUploadVO.getProductID());
            p_voucherUploadVO.setNoOfRecordsInFile(_voucherUploadVO.getNoOfRecordsInFile());
            p_voucherUploadVO.setMaxNoOfRecordsAllowed(_voucherUploadVO.getMaxNoOfRecordsAllowed());
            p_voucherUploadVO.setChannelUserVO(_voucherUploadVO.getChannelUserVO());
            p_voucherUploadVO.setCurrentDate(_voucherUploadVO.getCurrentDate());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("MobinilVoucherFileChecks[populateVoucherVo]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MobinilVoucherFileChecks[populateVoucherVo]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("MobinilVoucherFileChecks ", " populateVoucherVo ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" populateVoucherVo", " Exiting");
            }
        }
    }

    public VomsSerialUploadCheckVO populateVomsSerialUploadCheckVO(Connection p_con) throws BTSLBaseException, Exception {
        // TODO Auto-generated method stub
        return null;
    }

}