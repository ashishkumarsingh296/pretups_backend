package com.client.voms.vomsprocesses.businesslogic;

/*
 * @(#)VoucherFileChecksGH.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Ved Prakash 16/11/07 Initial Creation
 * Manisha Jain 10/12/07 Modified for validations
 * ------------------------------------------------------------------------
 * Copyright (c) 2007 Bharti Telesoft Ltd.
 * Parser class for voucher file processing
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomsprocesses.util.VoucherFileChecksI;
import com.btsl.voms.vomsprocesses.util.VoucherFileUploaderUtil;
import com.btsl.voms.vomsprocesses.util.VoucherUploadVO;
import com.btsl.voms.voucher.businesslogic.VomsSerialUploadCheckVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class VoucherFileChecksGH implements VoucherFileChecksI {
    private String _valueSeparator = null; // separater for the file
    private String _headerSeparator = null; // separater for header
    private String _startingRecord = null;
    private String _endingRecord = null;
    private String _productID = null; // product ID for the vouchers
    private String _filePath = null; // path of the file
    private String _fileName = null; // name of file
    private String _startSN = null; // start serial number of file
    private String _quantity = null; // number of records to be uploaded
    private String _prfileId = null;
    private Date _expiryDate = null; // expiry date of vouchers

    private ArrayList _fileDataArr = null;
    private ArrayList _voucherArr = null;
    private ChannelUserVO _channelUserVO = null;
    private VoucherUploadVO _voucherUploadVO = null;

    private int _maxNoRecordsAllowed = 0;
    private int _numberOfRecordsEntered = 0;
    private int _allowedNumberofErrors = 0;
    private int _numberofvaluesinheader = 0;
    private int _runningErrorCount = 0;
    private long _numberOfRecordScheduled = 0;
    private ArrayList _errorList = null; // used to save error information for
                                         // fututre use

    // file format
    private static final String LINE_HEADER_1 = "QUANTITY";
    private static final String LINE_HEADER_2 = "START SN";
    private static final String LINE_HEADER_3 = "PROFILE";
    private static Log _log = LogFactory.getLog(VoucherFileChecksGH.class.getName());

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
                _allowedNumberofErrors = Integer.parseInt(Constants.getProperty("VOMS_TOTAL_ALLOWED_ERRORS"));
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksGH ", " loadConstantValues   VOMS_TOTAL_ALLOWED_ERRORS " + _allowedNumberofErrors);
                }

            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _allowedNumberofErrors = 0;
                _log.info("loadConstantValues", " Total number of error (Entry VOMS_TOTAL_ALLOWED_ERRORS) not found in Constants . Thus taking default values as 0");
            }
            try {
                _numberofvaluesinheader = Integer.parseInt(Constants.getProperty("NUMBER_OF_VALUES_IN_HEADER"));
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksGH ", " loadConstantValues  NUMBER_OF_VALUES_IN_HEADER " + _numberofvaluesinheader);
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" loadConstantValues", " Invalid value for NUMBER_OF_VALUES_IN_HEADER  in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksGH[loadConstantValues]", "", "", "", "Invalid value for NUMBER_OF_VALUES_IN_HEADER in Constant File while voucher upload process");
                throw new BTSLBaseException("VoucherFileChecksGH ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                _valueSeparator = Constants.getProperty("VOMS_FILE_SEPARATOR");
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksGH ", " loadConstantValues   VOMS_FILE_SEPARATOR " + _valueSeparator);
                }
                if (BTSLUtil.isNullString(_valueSeparator)) {
                    _log.error(" loadConstantValues", " Invalid value for VOMS_FILE_SEPARATOR  in Constant File ");
                    _valueSeparator = " ";
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" loadConstantValues", " Invalid value for VOMS_FILE_SEPARATOR  in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksGH[loadConstantValues]", "", "", "", "Invalid value for VOMS_FILE_SEPARATOR in Constant File while voucher upload process");
                throw new BTSLBaseException("VoucherFileChecksGH ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                _headerSeparator = Constants.getProperty("VOMS_FILE_HEADER_SEPARATOR");
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksGH ", " loadConstantValues   VOMS_FILE_HEADER_SEPARATOR " + _headerSeparator);
                }
                if (BTSLUtil.isNullString(_headerSeparator)) {
                    _log.error(" loadConstantValues", " Invalid value for VOMS_FILE_HEADER_SEPARATOR  in Constant File ");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksGH[loadConstantValues]", "", "", "", "Invalid value for VOMS_FILE_HEADER_SEPARATOR in Constant File while voucher upload process");
                    throw new BTSLBaseException("VoucherFileChecksGH ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" loadConstantValues", " Invalid value for VOMS_FILE_HEADER_SEPARATOR  in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksGH[loadConstantValues]", "", "", "", "Invalid value for VOMS_FILE_HEADER_SEPARATOR in Constant File while voucher upload process");
                throw new BTSLBaseException("VoucherFileChecksGH ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                _numberOfRecordScheduled = Long.parseLong(Constants.getProperty("VOMS_MAX_FILE_LENGTH"));
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksGH", " loadConstantValues   NUMBER_OF_RECORDS_SCHEDULE " + _numberOfRecordScheduled);
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" loadConstantValues ", "Invalid value for REC_ALLOWED_FOR_SCRIPT in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksGH[loadConstantValues]", "", "", "", "Invalid value for REC_ALLOWED_FOR_SCRIPT in Constant File ");
                throw new BTSLBaseException("VoucherFileChecksGH ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
        }// end of try block
        catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("loadConstantValues", " BTSLBaseException be = " + be);
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("loadConstantValues", " Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksGH[loadConstantValues]", "", "", "", "Exception while loading the constant values from the Constants.prop file " + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksGH ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" loadConstantValues", "   Exiting with _allowedNumberofErrors=" + _allowedNumberofErrors + " _valueSeparator=" + _valueSeparator);
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
            _log.debug(" getFileLength", " Entered with p_voucherUploadVO=" + p_voucherUploadVO);
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
                _log.debug("getFileLength", " Starting processing to get the number of records for source File Path = " + srcFile + " File Name" + _fileName);
            }

            // creates a new bufferedreader to read the Voucher Upload file
            inFile = new BufferedReader(new FileReader(srcFile));

            p_voucherUploadVO.setNoOfRecordsInFile(readFileHeader(inFile));

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
                if (fileLineCount == 1) {
                    String[] firstRecord = fileData.split(_valueSeparator);
                    String firstSerialNo = firstRecord[0].trim();
                    // check starting serial number is equal to first serial
                    // number
                    if (!(firstSerialNo).equals(_startSN)) {
                        _log.error("getFileLength", " Start Serial Number  (" + _startSN + ") in the File (" + _fileName + ") is not equal to first serial Number = " + firstSerialNo);
                        throw new BTSLBaseException("VoucherFileChecksGH", "getFileLength", PretupsErrorCodesI.START_SERIALNO_NOT_EQUAL_TO_FIRST_SERIALNO, "uploadvoucher");
                    }
                }
                _fileDataArr.add(fileData);
            }
            // this is used to check if the actual number of records in the file
            // and the records
            // entered by the user are same or not
            if (_numberOfRecordsEntered != fileLineCount) {
                _log.error("getFileLength", " Total Number of Records (" + (fileLineCount) + ") in the File (" + _fileName + ") doesn't match the entered value = " + _numberOfRecordsEntered + ". Control Returning ");
                throw new BTSLBaseException("VoucherFileChecksGH", "getFileLength", PretupsErrorCodesI.NO_OF_RECORDS_NOT_EQUALTO_QUANTITY, "uploadvoucher");
            }
            // this is used to check the number of records are greater than
            // 20000
            // if number of records in the file are greater than 20000 than
            // schedule the file for later process
            if (fileLineCount > _numberOfRecordScheduled) {
                _log.error("getFileLength", " Number of records in the file  (" + fileLineCount + ") in the File (" + _fileName + ") are more than scheduled = (" + _numberOfRecordScheduled + ")");
                throw new BTSLBaseException("VoucherFileChecksGH", "getFileLength", PretupsErrorCodesI.VOUCHER_FILE_MORE_RECORDS);
            }

        }// end of try block
        catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("getFileLength", " : BTSLBaseException " + be);
            throw be;
        } catch (IOException io) {
            _log.errorTrace(METHOD_NAME, io);
            _log.error("getFileLength", "The file (" + _fileName + ")could not be read properly to get the number of records");
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksGH[getFileLength]", "", "", "", "The file (" + _fileName + ")could not be read properly to get the number of records");
            throw new BTSLBaseException("VoucherFileChecksGH", "getFileLength", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);
        }// end of IOException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(" getFileLength", " Exception = " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VocuherFileChecks[getFileLength]", "", "", "", e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksGH", "getFileLength", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);
        }// end of Exception
        finally {
            try {
                inFile.close();
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" getFileLength", " Exception while closing input stream = " + e);
            }
        }// end of finally
    }// end of getFileLength

    /**
     * This method is used to parse the header
     * and it also validate the header values
     * 
     * @param p_inFile
     * @throws Exception
     */
    private int readFileHeader(BufferedReader p_inFile) throws Exception {
        final String METHOD_NAME = "readFileHeader";
        if (_log.isDebugEnabled()) {
            _log.debug(" readFileHeader", " Entered to validate the header of _fileName=" + _fileName);
        }
        Connection con = null;
        _errorList = new ArrayList();
        try {
            String fileData = null;
            String batchValue = null;
            Date currentDate = new Date();
            int validHeaderCount = 0;
            int counter = 0;

            if ("Y".equalsIgnoreCase(_voucherUploadVO.getRunningFromCron())) {
                _numberofvaluesinheader = _numberofvaluesinheader + 1;
            }
            for (int i = 0; i < _numberofvaluesinheader; i++) {
                fileData = p_inFile.readLine();
                fileData = fileData.toUpperCase();
                batchValue = null;
                counter = i + 1;
                // extract the Quantity
                if (counter == 1) {
                    if (fileData.indexOf(LINE_HEADER_1) == -1)// means not found
                    {
                        _errorList.add("Information for " + LINE_HEADER_1 + " Not found");
                        _runningErrorCount++;
                    } else {
                        batchValue = extractData(fileData, LINE_HEADER_1, _headerSeparator);
                        if (BTSLUtil.isNullString(batchValue)) {
                            _errorList.add("Information for " + LINE_HEADER_1 + " Not found");
                            _runningErrorCount++;
                        } else {
                            _quantity = batchValue.trim();
                            if (!BTSLUtil.isNumeric(_quantity)) {
                                _log.error(" readFileHeader", " Invalid Quantity");
                                throw new BTSLBaseException("VoucherFileChecksGH", "readFileHeader", PretupsErrorCodesI.ERROR_INVALID_PRODUCT_QUANTITY, "uploadvoucher");
                            }
                            validHeaderCount++;
                            continue;
                        }
                    }
                }
                // extract the Start Serial number
                if (counter == 2) {
                    if (fileData.indexOf(LINE_HEADER_2) == -1)// means not found
                    {
                        _errorList.add("Information for " + LINE_HEADER_2 + " Not found");
                        _runningErrorCount++;
                    } else {
                        batchValue = extractData(fileData, LINE_HEADER_2, _headerSeparator);
                        if (BTSLUtil.isNullString(batchValue)) {
                            _errorList.add("Information for " + LINE_HEADER_2 + " Not found");
                            _runningErrorCount++;
                        } else {
                            _startSN = batchValue.trim();
                            validHeaderCount++;
                            // continue;
                        }
                    }
                }
                if ("Y".equalsIgnoreCase(_voucherUploadVO.getRunningFromCron())) {

                    if (counter == 3) {

                        if (fileData.indexOf(LINE_HEADER_3) == -1)// means not
                                                                  // found
                        {
                            _errorList.add("Information for " + LINE_HEADER_3 + " Not found");
                            _runningErrorCount++;
                        } else {
                            batchValue = extractData(fileData, LINE_HEADER_3, _headerSeparator);
                            if (BTSLUtil.isNullString(batchValue)) {
                                _errorList.add("Information for " + LINE_HEADER_3 + " Not found");
                                _runningErrorCount++;
                            } else {
                                _prfileId = batchValue.trim();
                                validHeaderCount++;
                                // continue;
                            }
                        }
                    }
                }
            }
            if (!(validHeaderCount == _numberofvaluesinheader)) {
                _log.error("readFileHeader", " Total Number of values in the header is not equal to =" + _numberofvaluesinheader);
                throw new BTSLBaseException("VoucherFileChecksGH", "readFileHeader", PretupsErrorCodesI.HEADER_FIELDS_NOT_EQUALTO_DEFINED_VALUE, "uploadvoucher");
            }
            try {
                _numberOfRecordsEntered = Integer.parseInt(_quantity);
                if (VoucherUploadVO._MANUALPROCESSTYPE.equalsIgnoreCase(_voucherUploadVO.getProcessType()) && _numberOfRecordsEntered != _voucherUploadVO.getNoOfRecordsInFile()) {
                    _log.error("readFileHeader", " Total number of records in file header (" + _numberOfRecordsEntered + ") is not equal to the file length entered by user (" + _voucherUploadVO.getNoOfRecordsInFile() + ")");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherFileProcessor[readFileHeader]", "", "", "", "Total number of records in file header (" + _numberOfRecordsEntered + ") is not equal to the file length entered by user (" + _voucherUploadVO.getNoOfRecordsInFile() + ")");
                    throw new BTSLBaseException("VoucherFileChecksGH", "readFileHeader", PretupsErrorCodesI.VOUCHER_ERROR_INVALID_RECORD_COUNT);// "The voucher file does not exists at the location specified"
                }
                // check number of records entered are less than maximum number
                // of records allowed
                if (_maxNoRecordsAllowed < _numberOfRecordsEntered) {
                    _log.error("readFileHeader", "Total number of records entered (" + _numberOfRecordsEntered + ") should be less than the allowed records in file (" + _maxNoRecordsAllowed + ")");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherFileProcessor[readFileHeader]", "", "", "", "Total number of records entered (" + _numberOfRecordsEntered + ") should be less than the allowed records in voucher file (" + _maxNoRecordsAllowed + ")");
                    throw new BTSLBaseException("VoucherFileChecksGH", "readFileHeader", PretupsErrorCodesI.VOUCHER_ERROR_NUMBER_REC_MORE_THAN_ALLWD, "uploadvoucher");// "The voucher file does not exists at the location specified"
                }
            }// end of try block
            catch (NumberFormatException nfe) {
                _log.errorTrace(METHOD_NAME, nfe);
                _log.error("readFileHeader", " Quantity field in the header should be Numeric");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksGH[readFileHeader]", "", "", "", " Quantity field in the header should be integer");
                throw new BTSLBaseException("VoucherFileChecksGH", "readFileHeader", PretupsErrorCodesI.VOUCHER_FILE_HEADER_QUANTITY_ERROR);
            }
            _voucherUploadVO.setNoOfRecordsInFile(_numberOfRecordsEntered);
            _voucherUploadVO.setPrfileId(_prfileId);

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error(" readFileHeader", " Exception = " + be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChechsUmniah[readFileHeader]", "", "", "", be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(" readFileHeader", " Exception = " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChechsUmniah[readFileHeader]", "", "", "", e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksGH", "readFileHeader", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" readFileHeader", " Exiting _numberOfRecordsEntered=" + _numberOfRecordsEntered);
            }
        }// end of Exception
        return _numberOfRecordsEntered;
    }// end of readFileHeader

    /**
     * This method is used to validate the given voucher File
     * _fileName - name of the file to be parsed
     * 
     * @return VoucherUploadVO
     * @throws Exception
     * @throws BTSLBaseException
     */
    public VoucherUploadVO validateVoucherFile() throws BTSLBaseException, Exception {
        final String METHOD_NAME = "validateVoucherFile";
        if (_log.isDebugEnabled()) {
            _log.debug(" validateVoucherFile", " Entered to validate the file _fileName=" + _fileName);
        }

        String dataValuePIN = null;
        String dataValueSerial = null;
        String fileData = null;
        String encryptedPin = null;
        String[] fileRecord = null;
        long previousSerialNo = 0L;
        long presentSerialNo = 0L;
        int _runningFileRecordCount = 0;
        int minSerialLength = 0;
        int maxSerialLength = 0;
        int minPinLength = 0;
        int maxPinLength = 0;
        Hashtable pinList = new Hashtable();
        boolean firstOnly = true;
        long beforeSize = 0;
        long afterSize = 0;
        try {
            _voucherArr = new ArrayList();
            minSerialLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MIN_LENGTH))).intValue();
            maxSerialLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH))).intValue();
            minPinLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MIN_LENGTH))).intValue();
            maxPinLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MAX_LENGTH))).intValue();
            int fileDataArrSize = _fileDataArr.size();
            for (int loop = 0; loop < fileDataArrSize; loop++) {
                fileData = null;
                fileData = (String) _fileDataArr.get(loop);
                fileRecord = fileData.split(_valueSeparator);

                _runningFileRecordCount++;// keeps record of number of records
                                          // in file

                if (fileRecord.length == 1) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " No Data Found");
                    _runningErrorCount++;
                    _errorList.add(" Record Number = " + _runningFileRecordCount + " No Data Found");
                    continue;
                }
                if (fileRecord.length < 2) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + "Number of values in the record are not correct");
                    _runningErrorCount++;
                    _errorList.add("Record Number = " + _runningFileRecordCount + "Number of values in the record are not correct");
                    continue;
                }

                // checks if the number of error encontered while parsing of
                // file does not
                // exceed the user given error count limit
                if (_runningErrorCount > _allowedNumberofErrors) {
                    _log.error("validateVoucherFile", " Total Number of error (" + _runningErrorCount + ") in the File (" + _fileName + ") exceeds the user specified error number= " + _allowedNumberofErrors);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksGH[validateVoucherFile]", "", "", "", "The number of error (" + _runningErrorCount + ") in the file exceed the user specified value (" + _allowedNumberofErrors + ")");
                    throw new BTSLBaseException("VoucherFileChecksGH", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT, "uploadvoucher");// "Total number of error in the file exceed the specified number of error"
                }

                dataValueSerial = fileRecord[0].trim();
                if ("Y".equalsIgnoreCase(_voucherUploadVO.getRunningFromCron())) {
                    if (BTSLUtil.isNullString(dataValueSerial)) {
                        _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Serial Number Not Valid");
                        _runningErrorCount++;
                        _errorList.add(" Record Number = " + _runningFileRecordCount + " Serial Number Not Valid");
                        continue;
                    } else if (!VoucherFileUploaderUtil.isValidDataLength(dataValueSerial.length(), minSerialLength, maxSerialLength)) {
                        _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Serial Number Length Not Valid");
                        _runningErrorCount++;
                        _errorList.add(" Record Number = " + _runningFileRecordCount + " Serial Number Length Not Valid");
                        continue;
                    }
                }
                // on the first serial number, length will be validated against
                // min and max values
                // then the min and max values will be set to length of first
                // serial number
                // this will be done for first record only
                else if (firstOnly) {
                    minSerialLength = dataValueSerial.length();
                    maxSerialLength = dataValueSerial.length();
                    firstOnly = false;
                }

                // this if-else construct checks for the consequitivity of the
                // serial numbers
                // initially previousSerialNo and presentSerialNo are both 0
                if (previousSerialNo == 0 && presentSerialNo == 0) {
                    previousSerialNo = Long.parseLong(dataValueSerial);// checks
                                                                       // for
                                                                       // the
                                                                       // first
                                                                       // time
                                                                       // and
                                                                       // set
                                                                       // the
                                                                       // value
                } else {
                    presentSerialNo = Long.parseLong(dataValueSerial);
                }
                if (presentSerialNo != 0) {
                    // checks for the consecutivness of the Serial number
                    if (previousSerialNo + 1 != presentSerialNo) {
                        _runningErrorCount++;
                        _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Serial Numbers are not Continous");
                        _errorList.add(" Record Number = " + _runningFileRecordCount + " Serial Numbers are not Continous");
                        continue;
                    }
                    previousSerialNo++;
                }

                dataValuePIN = fileRecord[1].trim();
                if ("Y".equalsIgnoreCase(_voucherUploadVO.getRunningFromCron())) {
                    if (BTSLUtil.isNullString(dataValuePIN)) {
                        _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " PIN Not Valid");
                        _runningErrorCount++;
                        _errorList.add(" Record Number = " + _runningFileRecordCount + " PIN Not Valid");
                        continue;
                    }
                    // Above we have validated the pin length in the header with
                    // system preference
                    // so here just validating, pin length against header pin
                    // length
                    else if (!VoucherFileUploaderUtil.isValidDataLength(dataValuePIN.length(), minPinLength, maxPinLength)) {
                        _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " PIN Length Not Valid");
                        _errorList.add(" Record Number = " + _runningFileRecordCount + " PIN Length Not Valid");
                        _runningErrorCount++;
                        continue;
                    }
                }
                String finalPin = dataValuePIN;

                // this condition checks for the uniqueness of the pin in the
                // file. Adds the pin in the
                // arraylist pinList and before adding next time searches in
                // this list first
                if ("Y".equalsIgnoreCase(_voucherUploadVO.getRunningFromCron())) {
                    if (pinList.isEmpty()) {
                        pinList.put(finalPin, finalPin);
                        beforeSize = pinList.size();
                    } else {
                        pinList.put(finalPin, finalPin);
                        if (pinList.size() == beforeSize) {
                            _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " PIN Not Unique");
                            _runningErrorCount++;
                            _errorList.add(" Record Number = " + _runningFileRecordCount + " PIN Not Unique");
                            continue;
                        }
                        beforeSize = pinList.size();
                    }
                }
                try {
                    encryptedPin = VomsUtil.encryptText(finalPin);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Not able to encrypt PIN");
                    _runningErrorCount++;
                    _errorList.add(" Record Number = " + _runningFileRecordCount + " Not able to encrypt PIN");
                    continue;
                }

                if (_runningFileRecordCount == 1) {
                    _startingRecord = dataValueSerial;// set only for the first
                }
                // time
                _endingRecord = dataValueSerial;
                populateValuesInVoucherList(dataValueSerial, encryptedPin, _productID, _expiryDate);

            }// end of for loop
             // check running error count is less than allowed error count
            if (_runningErrorCount > _allowedNumberofErrors) {
                _log.error("validateVoucherFile", " Total Number of error (" + _runningErrorCount + ") in the File (" + _fileName + ") exceeds the user specified error number= " + _allowedNumberofErrors);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksGH[validateVoucherFile]", "", "", "", "The number of error (" + _runningErrorCount + ") in the file exceed the user specified value (" + _allowedNumberofErrors + ")");
                throw new BTSLBaseException("VoucherFileChecksGH", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT, "uploadvoucher");// "Total number of error in the file exceed the specified number of error"
            }// end of if block
             // counts the number of actual valid records in the file
            _voucherUploadVO.setFromSerialNo(_startingRecord);
            _voucherUploadVO.setToSerialNo(_endingRecord);
            _voucherUploadVO.setActualNoOfRecords(_voucherArr.size());
            _voucherUploadVO.setVoucherArrayList(_voucherArr);
            _voucherUploadVO.setErrorArrayList(_errorList);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("validateVoucherFile", " : BTSLBaseException " + be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksGH[validateVoucherFile]", "", "", "", be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(" validateVoucherFile", " Exception = " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksGH[validateVoucherFile]", "", "", "", e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksGH", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" validateVoucherFile", " Exiting ");
            }
        }
        return _voucherUploadVO;
    }// end of validateVoucherFile

    /**
     * This method populates the Voms Voucher VO that will be inserted in
     * database
     * 
     * @param p_serialNo
     * @param p_pin
     * @param p_productID
     * @param expiryDate
     * @throws BTSLBaseException
     */
    private void populateValuesInVoucherList(String p_serialNo, String p_pin, String p_productID, Date expiryDate) throws BTSLBaseException {
        final String METHOD_NAME = "populateValuesInVoucherList";
        if (_log.isDebugEnabled()) {
            _log.debug(" populateValuesInVoucherList", " Entered with p_serialNo=" + p_serialNo + " p_pin=" + p_pin + " p_productID=" + p_productID);
        }
        try {
            VomsVoucherVO vomsVoucherVO = new VomsVoucherVO();
            vomsVoucherVO.setSerialNo(p_serialNo);
            vomsVoucherVO.setPinNo(p_pin);
            _voucherArr.add(vomsVoucherVO);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("VoucherFileChecksGH[populateValuesInVoucherList]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksGH[populateValuesInVoucherList]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksGH ", " populateValuesInVoucherList ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" populateValuesInVoucherList", " Exiting ");
            }
        }
    } // end of populateValuesInVoucherList

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
            _log.debug(" getValuesFromVO", " Entered with  p_voucherUploadVO=" + p_voucherUploadVO);
        }
        try {
            _fileName = p_voucherUploadVO.getFileName();
            _filePath = p_voucherUploadVO.getFilePath();
            _numberOfRecordsEntered = p_voucherUploadVO.getNoOfRecordsInFile();
            _maxNoRecordsAllowed = p_voucherUploadVO.getMaxNoOfRecordsAllowed();
            _channelUserVO = p_voucherUploadVO.getChannelUserVO();
            _voucherUploadVO = p_voucherUploadVO;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("VoucherFileChecksGH[getValuesFromVO]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksGH[getValuesFromVO]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksGH ", " getValuesFromVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" getValuesFromVO", " Exiting");
            }
        }
    }// end of getValuesFromVO

    /**
     * This method extracts the data from the p_data string starting from the
     * pattern p_pattern
     * 
     * @param p_data
     * @param p_pattern
     * @param p_valueSeparator
     * @return String
     */
    private String extractData(String p_data, String p_pattern, String p_valueSeparator) {
        final String METHOD_NAME = "extractData";
        if (_log.isDebugEnabled()) {
            _log.debug("extractData", " Entered with data  =  " + p_data + " pattern=" + p_pattern + " p_valueSeparator=" + p_valueSeparator);
        }
        String extractedString = null;
        int index = 0;
        int index1 = 0;

        try {
            index = p_data.indexOf(p_pattern);
            index1 = p_data.indexOf(p_valueSeparator, index + p_pattern.length());

            extractedString = p_data.substring(index1 + 1);
        } catch (Exception e) {
            _log.error(" extractData", " Exception  = " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksGH[extractData]", "", "", "", " Getting Exception=" + e.getMessage());
            return extractedString;
        }// end of Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" extractData", "  Exiting with extractedString = " + extractedString);
            }
        }// end of finally
        return extractedString;
    }// end of extractData

    public VomsSerialUploadCheckVO populateVomsSerialUploadCheckVO(Connection p_con) throws BTSLBaseException, Exception {
        // TODO Auto-generated method stub
        return null;
    }
}