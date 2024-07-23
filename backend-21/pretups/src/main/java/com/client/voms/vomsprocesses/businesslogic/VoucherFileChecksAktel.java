package com.client.voms.vomsprocesses.businesslogic;

/*
 * @(#)VoucherFileChecksAktel.java
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
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

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
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsprocesses.util.VoucherFileChecksI;
import com.btsl.voms.vomsprocesses.util.VoucherFileUploaderUtil;
import com.btsl.voms.vomsprocesses.util.VoucherUploadVO;
import com.btsl.voms.voucher.businesslogic.VomsSerialUploadCheckVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class VoucherFileChecksAktel implements VoucherFileChecksI {
    private String _valueSeparator = null;
    private String _vomsfiledateformat = null;
    private int _numberOfRecordsEntered = 0;
    private int _pinLength = 0;
    private int _allowedNumberofErrors = 0;
    private int _numberofvaluesinheader = 0;
    private String _startingRecord = null;
    private String _endingRecord = null;
    private String _productID = null;
    private String _filePath = null;
    private String _fileName = null;
    private ChannelUserVO _channelUserVO = null;
    private Date _currentDate = null;
    private int _maxNoRecordsAllowed = 0;
    private ArrayList _voucherArr = null;
    private VoucherUploadVO _voucherUploadVO = null;
    private ArrayList _fileDataArr = null;
    private Date _expiryDate = null; // added by varun
    private String _denomination = null; // added by varun
    private String _venderName = null; // added by varun

    private static Log _log = LogFactory.getLog(VoucherFileChecksAktel.class.getName());

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
                _vomsfiledateformat = Constants.getProperty("VOUCHER_EXPIRY_DATE_FORMAT");
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksAktel", " loadConstantValues   VOUCHER_EXPIRY_DATE_FORMAT " + _vomsfiledateformat);
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" loadConstantValues ", "Invalid value for VOUCHER_EXPIRY_DATE_FORMAT in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksAktel[loadConstantValues]", "", "", "", "Invalid value for VOUCHER_EXPIRY_DATE_FORMAT in Constant File ");
                throw new BTSLBaseException("VoucherFileChecksAktel ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                _allowedNumberofErrors = Integer.parseInt(Constants.getProperty("VOMS_TOTAL_ALLOWED_ERRORS"));
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksAktel ", " loadConstantValues   VOMS_TOTAL_ALLOWED_ERRORS " + _allowedNumberofErrors);
                }

            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _allowedNumberofErrors = 0;
                _log.info("loadConstantValues", " Total number of error (Entry VOMS_TOTAL_ALLOWED_ERRORS) not found in Constants . Thus taking default values as 0");
            }
            try {
                _numberofvaluesinheader = Integer.parseInt(Constants.getProperty("NUMBER_OF_VALUES_IN_HEADER"));
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksAktel ", " loadConstantValues  NUMBER_OF_VALUES_IN_HEADER " + _numberofvaluesinheader);
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" loadConstantValues", " Invalid value for NUMBER_OF_VALUES_IN_HEADER  in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksAktel[loadConstantValues]", "", "", "", "Invalid value for NUMBER_OF_VALUES_IN_HEADER in Constant File while voucher upload process");
                throw new BTSLBaseException("VoucherFileChecksAktel ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                _valueSeparator = Constants.getProperty("VOMS_FILE_SEPARATOR");
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksAktel ", " loadConstantValues   VOMS_FILE_SEPARATOR " + _valueSeparator);
                }
                if (BTSLUtil.isNullString(_valueSeparator)) {
                    _log.error(" loadConstantValues", " Invalid value for VOMS_FILE_SEPARATOR  in Constant File ");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksAktel[loadConstantValues]", "", "", "", "Invalid value for VOMS_FILE_SEPARATOR in Constant File while voucher upload process");
                    throw new BTSLBaseException("VoucherFileChecksAktel ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);

                }

            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" loadConstantValues", " Invalid value for VOMS_FILE_SEPARATOR  in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksAktel[loadConstantValues]", "", "", "", "Invalid value for VOMS_FILE_SEPARATOR in Constant File while voucher upload process");
                throw new BTSLBaseException("VoucherFileChecksAktel ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksAktel[loadConstantValues]", "", "", "", "Exception while loading the constant values from the Constants.prop file " + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksAktel ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
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

            // To parse Header in file this header is as the first line of the
            // data file
            fileData = inFile.readLine();
            p_voucherUploadVO.setNoOfRecordsInFile(readFileHeader(fileData));

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
            // this is used to check if the actual number of records in the file
            // and the records
            // entered by the user are same or not
            if (_numberOfRecordsEntered != fileLineCount) {
                _log.error("getFileLength", " Total Number of Records (" + (fileLineCount) + ") in the File (" + _fileName + ") doesn't match the entered value = " + _numberOfRecordsEntered + ". Control Returning ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktel[getFileLength]", "", "", "", "The number of records (" + (fileLineCount) + ") in the file doesn't match the entered value (" + _numberOfRecordsEntered + ")");
                throw new BTSLBaseException("VoucherFileChecksAktel", "getFileLength", PretupsErrorCodesI.VOUCHER_ERROR_INVALID_RECORD_COUNT);// "Total number of records in the file are different from the one specified"
            }
        }// end of try block
        catch (IOException io) {
            _log.errorTrace(METHOD_NAME, io);
            _log.error("getFileLength", "The file (" + _fileName + ")could not be read properly to get the number of records");
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktel[getFileLength]", "", "", "", "The file (" + _fileName + ")could not be read properly to get the number of records");
            throw new BTSLBaseException("VoucherFileChecksAktel", "getFileLength", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);
        }// end of IOException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(" getFileLength", " Exception = " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VocuherFileChecks[getFileLength]", "", "", "", e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksAktel", "getFileLength", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);
        }// end of Exception
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
     * This method is used to parse the header
     * and it also validate the header values
     * 
     * @param fileData
     */
    private int readFileHeader(String fileData) throws BTSLBaseException {
        final String METHOD_NAME = "readFileHeader";
        if (_log.isDebugEnabled()) {
            _log.debug(" readFileHeader", " Entered to validate the header of _fileName=" + _fileName);
        }
        try {

            String[] headerData = fileData.split(_valueSeparator);

            if (!(headerData.length == _numberofvaluesinheader)) {
                _log.error("readFileHeader", " Total Number of values in the header is not equal to =" + _numberofvaluesinheader);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktel[readFileHeader]", "", "", "", "Total Number of values in the header is not equal to =" + _numberofvaluesinheader);
                throw new BTSLBaseException("VoucherFileChecksAktel", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_FILE_HEADER_ERROR);
            }
            try {
                _numberOfRecordsEntered = Integer.parseInt(headerData[0]);
                if (VoucherUploadVO._MANUALPROCESSTYPE.equalsIgnoreCase(_voucherUploadVO.getProcessType()) && _numberOfRecordsEntered != _voucherUploadVO.getNoOfRecordsInFile()) {
                    _log.error("readFileHeader", " Total number of records in file header (" + _numberOfRecordsEntered + ") is not equal to the file length entered by user (" + _voucherUploadVO.getNoOfRecordsInFile() + ")");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherFileProcessor[getDetailsFromConsole]", "", "", "", "Total number of records in file header (" + _numberOfRecordsEntered + ") is not equal to the file length entered by user (" + _voucherUploadVO.getNoOfRecordsInFile() + ")");
                    throw new BTSLBaseException("VoucherFileProcessor", "getDetailsFromConsole", PretupsErrorCodesI.VOUCHER_ERROR_INVALID_RECORD_COUNT);// "The voucher file does not exists at the location specified"
                }

                if (_maxNoRecordsAllowed < _numberOfRecordsEntered) {
                    _log.error("readFileHeader", "Total number of records entered (" + _numberOfRecordsEntered + ") should be less than the allowed records in file (" + _maxNoRecordsAllowed + ")");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherFileProcessor[getDetailsFromConsole]", "", "", "", "Total number of records entered (" + _numberOfRecordsEntered + ") should be less than the allowed records in voucher file (" + _maxNoRecordsAllowed + ")");
                    throw new BTSLBaseException("VoucherFileProcessor", "getDetailsFromConsole", PretupsErrorCodesI.VOUCHER_ERROR_NUMBER_REC_MORE_THAN_ALLWD);// "The voucher file does not exists at the location specified"
                }
            } catch (NumberFormatException nfe) {
                _log.errorTrace(METHOD_NAME, nfe);
                _log.error("readFileHeader", " Quantity field in the header should be Numeric");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktel[readFileHeader]", "", "", "", " Quantity field in the header should be integer");
                throw new BTSLBaseException("VoucherFileChecksAktel", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_FILE_HEADER_QUANTITY_ERROR);
            }
            try {
                _pinLength = Integer.parseInt(headerData[1]);
                if (!VoucherFileUploaderUtil.isValidDataLength(Integer.parseInt(headerData[1]), ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MIN_LENGTH))).intValue(), ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MAX_LENGTH))).intValue())) {
                    _log.error("readFileHeader", "PIN number length (" + _pinLength + ") in the header, should be in between MIN(" + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MIN_LENGTH))).intValue() + ") and MAX(" + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MAX_LENGTH))).intValue() + ") values.");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherFileProcessor[getDetailsFromConsole]", "", "", "", "PIN number length (" + _pinLength + ") in the header, should be in between MIN(" + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MIN_LENGTH))).intValue() + ") and MAX(" + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MAX_LENGTH))).intValue() + ") values.");
                    throw new BTSLBaseException("VoucherFileChecksAktel", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_FILE_HEADER_PINLENGTH_ERROR);
                }
            } catch (NumberFormatException nfe) {
                _log.errorTrace(METHOD_NAME, nfe);
                _log.error("readFileHeader", " Pin Length field in the header should be Numeric");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktel[readFileHeader]", "", "", "", " Pin Length field in the header should be integer");
                throw new BTSLBaseException("VoucherFileChecksAktel", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_FILE_HEADER_PINLENGTH_ERROR);
            }
            _voucherUploadVO.setNoOfRecordsInFile(_numberOfRecordsEntered);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error(" readFileHeader", " Exception = " + be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VocuherFileChecks[readFileHeader]", "", "", "", be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(" readFileHeader", " Exception = " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VocuherFileChecks[readFileHeader]", "", "", "", e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksAktel", "readFileHeader", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" readFileHeader", " Exiting _numberOfRecordsEntered=" + _numberOfRecordsEntered);
            }
        }// end of Exception

        return _numberOfRecordsEntered;

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

        // String dataValuePINFirst =null;
        String dataValuePIN = null;
        String dataValueSerial = null;
        String dataValueProfile = null;
        long previousSerialNo = 0L;
        long presentSerialNo = 0L;
        int runningErrorCount = 0;
        int _runningFileRecordCount = 0;
        String fileData = null;
        ArrayList pinList = new ArrayList();
        String encryptedPin = null;
        Date currentDate = new Date();
        String[] fileRecord = null;

        boolean firstOnly = true;
        int minSerialLength = 0;
        int maxSerialLength = 0;

        try {
            _voucherArr = new ArrayList();
            minSerialLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MIN_LENGTH))).intValue();
            maxSerialLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH))).intValue();
             int  fileDataArrSize = _fileDataArr.size();
            for (int loop = 0; loop < fileDataArrSize; loop++) {
                fileData = null;
                fileData = (String) _fileDataArr.get(loop);
                fileRecord = fileData.split(_valueSeparator);

                _runningFileRecordCount++;// keeps record of number of records
                                          // in file

                if (fileRecord.length == 1) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " No Data Found");
                    runningErrorCount++;
                    continue;
                }
                if (fileRecord.length < 6) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + "Number of values in the record are not currect");
                    runningErrorCount++;
                    continue;
                }

                // checks if the number of error encontered while parsing of
                // file does not
                // exceed the user given error count limit
                if (runningErrorCount > _allowedNumberofErrors) {
                    // isValidFile = false;
                    _log.error("validateVoucherFile", " Total Number of error (" + runningErrorCount + ") in the File (" + _fileName + ") exceeds the user specified error number= " + _allowedNumberofErrors);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktel[validateVoucherFile]", "", "", "", "The number of error (" + runningErrorCount + ") in the file exceed the user specified value (" + _allowedNumberofErrors + ")");
                    throw new BTSLBaseException("VoucherFileChecksAktel", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);// "Total number of error in the file exceed the specified number of error"
                }
                // dataValueSerial=VoucherFileUploaderUtil.extractData(fileData,SERIAL_NO_LABEL,_valueSeparator);

                dataValueSerial = fileRecord[1];
                if (BTSLUtil.isNullString(dataValueSerial)) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Serial Number Not Valid");
                    runningErrorCount++;
                    continue;
                } else if (!VoucherFileUploaderUtil.isValidDataLength(dataValueSerial.length(), minSerialLength, maxSerialLength)) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Serial Number Not Valid");
                    runningErrorCount++;
                    // continue;
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
                        // isValidFile=false;
                        runningErrorCount++;
                        _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Serial Numbers are not Continous");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktel[validateVoucherFile]", "", "", "", "The serial number (" + presentSerialNo + ") is not consecutive");
                        // break;
                        throw new BTSLBaseException("VoucherFileChecksAktel", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_DUPLICATE_SERIAL_NO_ERROR);
                    }
                    previousSerialNo++;
                }

                // dataValueProfile=VoucherFileUploaderUtil.extractData(fileData,PRODUCT_ID_LABEL,_valueSeparator);
                dataValuePIN = fileRecord[0];
                if (BTSLUtil.isNullString(dataValuePIN)) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " PIN Not Valid");
                    runningErrorCount++;
                    continue;
                }
                // Above we have validated the pin length in the header with
                // system preference
                // so here just validating, pin length against header pin length
                else if (!VoucherFileUploaderUtil.isValidDataLength(dataValuePIN.length(), _pinLength, _pinLength)) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " PIN Not Valid");
                    runningErrorCount++;
                    continue;
                }
                String finalPin = dataValuePIN;

                // this condition checks for the uniqueness of the pin in the
                // file. Adds the pin in the
                // arraylist pinList and before adding next time searches in
                // this list first
                if (pinList.isEmpty()) {
                    pinList.add(finalPin);
                } else {
                    if (pinList.contains(finalPin)) {
                        _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " PIN Not Unique");
                        runningErrorCount++;
                        continue;
                    }
                    if (finalPin.length() != _pinLength) {
                        _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " PIN Length is not correct");
                        runningErrorCount++;
                        continue;
                    }
                    pinList.add(finalPin);
                }
                try {
                    encryptedPin = VomsUtil.encryptText(finalPin);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Not able to encrypt PIN");
                    runningErrorCount++;
                    continue;
                }

                // dataValueProfile=VoucherFileUploaderUtil.extractData(fileData,PRODUCT_ID_LABEL,_valueSeparator);
                _denomination = fileRecord[2];
                if (BTSLUtil.isNullString(_denomination)) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Denomination Not Valid");
                    runningErrorCount++;
                    continue;
                } else {
                    try {
                        // mrp=Long.parseLong(_denomination);
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Denomination Not Valid");
                        runningErrorCount++;
                        continue;
                    }
                }
                dataValueProfile = fileRecord[3];

                // NEW: GURJEET
                if (VoucherUploadVO._AUTOPROCESSTYPE.equalsIgnoreCase(_voucherUploadVO.getProcessType()) && loop == 0) {
                    _productID = dataValueProfile;
                    _voucherUploadVO.setProductID(_productID);
                }

                if (BTSLUtil.isNullString(dataValueProfile)) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Product Not Valid");
                    runningErrorCount++;
                    continue;
                } else if (!dataValueProfile.equalsIgnoreCase(_productID)) {
                    _log.error("validateVoucherFile", "Record Number = " + _runningFileRecordCount + " Product Not consistent in the voucher File.");
                    runningErrorCount++;
                    continue;
                }

                String _tempExpiryDate = fileRecord[4];
                fileData = fileData.substring(fileData.indexOf(_valueSeparator), fileData.length());
                if (!BTSLUtil.isNullString(_tempExpiryDate)) {
                    try {
                        _expiryDate = BTSLUtil.getDateFromDateString(_tempExpiryDate, _vomsfiledateformat);
                        if (_expiryDate.before(currentDate)) {
                            _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " voucher already expired");
                            runningErrorCount++;
                            continue;
                        }
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " expiry-date Not Valid");
                        runningErrorCount++;
                        continue;
                    }

                } else {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " expiry-date Not Valid");
                    runningErrorCount++;
                    continue;
                }

                // dataValueProfile=VoucherFileUploaderUtil.extractData(fileData,PRODUCT_ID_LABEL,_valueSeparator);
                _venderName = fileRecord[5];
                if (BTSLUtil.isNullString(_venderName)) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " expiry-date Not Valid");
                    runningErrorCount++;
                    continue;
                }

                // if the record is valid, prepare the 2D array with the parsed
                // values
                if (_runningFileRecordCount == 1) {
                    _startingRecord = dataValueSerial;// set only for the first
                }
                // time
                _endingRecord = dataValueSerial;

                populateValuesInVoucherList(dataValueSerial, encryptedPin, dataValueProfile, _expiryDate);

            }// end of for loop
             // counts the number of actual valid records in the file

            _voucherUploadVO.setFromSerialNo(_startingRecord);
            _voucherUploadVO.setToSerialNo(_endingRecord);
            _voucherUploadVO.setActualNoOfRecords(_voucherArr.size());
            _voucherUploadVO.setVoucherArrayList(_voucherArr);

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("validateVoucherFile", " : BTSLBaseException " + be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksAktel[validateVoucherFile]", "", "", "", be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(" validateVoucherFile", " Exception = " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksAktel[validateVoucherFile]", "", "", "", e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksAktel", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);

        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" validateVoucherFile", " Exiting ");
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
    private void populateValuesInVoucherList(String p_serialNo, String p_pin, String p_productID, Date expiryDate) throws BTSLBaseException {
        final String METHOD_NAME = "populateValuesInVoucherList";
        if (_log.isDebugEnabled()) {
            _log.debug(" populateValuesInVoucherList", " Entered with p_serialNo=" + p_serialNo + " p_pin=" + p_pin + " p_productID=" + p_productID);
        }
        try {
            VomsVoucherVO vomsVoucherVO = new VomsVoucherVO();
            vomsVoucherVO.setSerialNo(p_serialNo);
            vomsVoucherVO.setPinNo(p_pin);
            vomsVoucherVO.setProductID(p_productID);
            vomsVoucherVO.setProductionLocationCode(_channelUserVO.getNetworkID());
            vomsVoucherVO.setModifiedBy(_channelUserVO.getUserID());
            vomsVoucherVO.setOneTimeUsage(PretupsI.YES);
            vomsVoucherVO.setStatus(VOMSI.VOUCHER_NEW);
            vomsVoucherVO.setCurrentStatus(VOMSI.VOUCHER_NEW);
            vomsVoucherVO.setModifiedOn(new Date());
            vomsVoucherVO.setCreatedOn(_currentDate);
            vomsVoucherVO.setVenderName(_venderName);
            long time = expiryDate.getTime();
            vomsVoucherVO.setExpiryDate(new Date(time));
            _voucherArr.add(vomsVoucherVO);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("VoucherFileChecksAktel[populateValuesInVoucherList]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksAktel[populateValuesInVoucherList]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksAktel ", " populateValuesInVoucherList ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
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
            _log.debug(" getValuesFromVO", " Entered with  p_voucherUploadVO=" + p_voucherUploadVO);
        }
        try {
            _fileName = p_voucherUploadVO.getFileName();
            _filePath = p_voucherUploadVO.getFilePath();
            _productID = p_voucherUploadVO.getProductID();
            _numberOfRecordsEntered = p_voucherUploadVO.getNoOfRecordsInFile();
            _maxNoRecordsAllowed = p_voucherUploadVO.getMaxNoOfRecordsAllowed();
            _channelUserVO = p_voucherUploadVO.getChannelUserVO();
            _currentDate = p_voucherUploadVO.getCurrentDate();
            _voucherUploadVO = p_voucherUploadVO;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("VoucherFileChecksAktel[getValuesFromVO]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksAktel[getValuesFromVO]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksAktel ", " getValuesFromVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" getValuesFromVO", " Exiting");
            }
        }
    }

    public VomsSerialUploadCheckVO populateVomsSerialUploadCheckVO(Connection p_con) throws BTSLBaseException, Exception {
        // TODO Auto-generated method stub
        return null;
    }
}