package com.btsl.voms.vomsprocesses.businesslogic;

/*
 * @(#)VoucherFileChecks.java
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
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsprocesses.util.VoucherFileChecksI;
import com.btsl.voms.vomsprocesses.util.VoucherFileUploaderUtil;
import com.btsl.voms.vomsprocesses.util.VoucherUploadVO;
import com.btsl.voms.voucher.businesslogic.VomsSerialUploadCheckVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class VoucherFileChecks implements VoucherFileChecksI {
    private final String PIN_LABEL = "pin=";
    private static final String PIN_FIRST_PART_LABEL = "scnum=";
    private static final String SERIAL_NO_LABEL = "senum=";
    private static final String PRODUCT_ID_LABEL = "SCRPREF=";

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
    private static Log _log = LogFactory.getLog(VoucherFileChecks.class.getName());

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
                    _log.debug("VoucherFileChecks", " loadConstantValues   VOMS_FIRST_PART_PIN_LENGTH " + _firstPartPinLength);
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" loadConstantValues ", "Invalid value for VOMS_FIRST_PART_PIN_LENGTH  in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecks[loadConstantValues]", "", "", "", "Invalid value for VOMS_FIRST_PART_PIN_LENGTH in Constant File for voucher upload process");
                throw new BTSLBaseException("VoucherFileChecks ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                _allowedNumberofErrors = Integer.parseInt(Constants.getProperty("VOMS_TOTAL_ALLOWED_ERRORS"));
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecks ", " loadConstantValues   VOMS_TOTAL_ALLOWED_ERRORS " + _allowedNumberofErrors);
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
        }// end of try block
        catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("loadConstantValues", " BTSLBaseException be = " + be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("loadConstantValues", " Exception e=" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecks[loadConstantValues]", "", "", "", "Exception while loading the constant values from the Constants.prop file " + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecks ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecks[getFileLength]", "", "", "", "The number of records (" + (fileLineCount) + ") in the file doesn't match the entered value (" + _numberOfRecordsEntered + ")");
                throw new BTSLBaseException("VoucherFileChecks", "getFileLength", PretupsErrorCodesI.VOUCHER_ERROR_INVALID_RECORD_COUNT);// "Total number of records in the file are different from the one specified"
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
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecks[getFileLength]", "", "", "", "The file (" + _fileName + ")could not be read properly to get the number of records");
            throw new BTSLBaseException("VoucherFileChecks", "getFileLength", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);
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

        String dataValuePINFirst = null;
        String dataValuePIN = null;
        String dataValueSerial = null;
        String dataValueProfile = null;
        long previousSerialNo = 0L;
        long presentSerialNo = 0L;
        int runningErrorCount = 0;
        int runningFileRecordCount = 0;
        String fileData = null;
        ArrayList pinList = new ArrayList();
        String encryptedPin = null;
        boolean firstOnly = true;
        int minSerialLength = 0;
        int maxSerialLength = 0;

        try {
            minSerialLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MIN_LENGTH))).intValue();
            maxSerialLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH))).intValue();
            _voucherArr = new ArrayList();
            Iterator fileIter = _fileDataArr.iterator();
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
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecks[validateVoucherFile]", "", "", "", "The number of error (" + runningErrorCount + ") in the file exceed the user specified value (" + _allowedNumberofErrors + ")");
                    throw new BTSLBaseException("VoucherFileChecks", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);// "Total number of error in the file exceed the specified number of error"
                }

                if (fileData.indexOf(PIN_FIRST_PART_LABEL) == -1)// means not
                                                                 // found
                {
                    // isValidFile = false;
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " PinFirstPart Not Found");
                    runningErrorCount++;
                    continue;
                }
                dataValuePINFirst = VoucherFileUploaderUtil.extractData(fileData, PIN_FIRST_PART_LABEL, _valueSeparator);
                if (BTSLUtil.isNullString(dataValuePINFirst)) {
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " PinFirstPart Not Valid");
                    runningErrorCount++;
                    continue;
                } else if (!VoucherFileUploaderUtil.isValidDataLength(dataValuePINFirst.length(), _firstPartPinLength, _firstPartPinLength))// checks
                                                                                                                                            // if
                                                                                                                                            // the
                                                                                                                                            // value
                                                                                                                                            // returned
                                                                                                                                            // has
                                                                                                                                            // valid
                                                                                                                                            // length
                {
                    _log.error("validateVoucherFile", " : Record Number = " + runningFileRecordCount + " PinFirstPart Not Valid");
                    runningErrorCount++;
                    continue;
                }

                if (fileData.indexOf(PIN_LABEL) == -1)// means not found
                {
                    // isValidFile = false;
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " Pin Not Found");
                    runningErrorCount++;
                    continue;
                }

                dataValuePIN = VoucherFileUploaderUtil.extractData(fileData, PIN_LABEL, _valueSeparator);
                if (BTSLUtil.isNullString(dataValuePIN)) {
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " PIN Not Valid");
                    runningErrorCount++;
                    continue;
                }
                /*
                 * else
                 * if(!VoucherFileUploaderUtil.isValidDataLength(dataValuePIN
                 * ,SystemPreferences
                 * .VOMS_PIN_MIN_LENGTH,((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MAX_LENGTH))).intValue()))
                 * {
                 * _log.error("validateVoucherFile"," Record Number = "+
                 * runningFileRecordCount+ " PIN Not Valid");
                 * runningErrorCount++;
                 * continue;
                 * }
                 */String finalPin = dataValuePINFirst + dataValuePIN;

                if (BTSLUtil.isNullString(finalPin)) {
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " PIN Not Valid");
                    runningErrorCount++;
                    continue;
                } else if (!VoucherFileUploaderUtil.isValidDataLength(finalPin.length(), ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MIN_LENGTH))).intValue(), ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MAX_LENGTH))).intValue())) {
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " PIN Not Valid");
                    runningErrorCount++;
                    continue;
                }

                // this condition checks for the uniqueness of the pin in the
                // file. Adds the pin in the
                // arraylist pinList and before adding next time searches in
                // this list first
                if (pinList.isEmpty()) {
                    pinList.add(finalPin);
                } else {
                    if (pinList.contains(finalPin)) {
                        _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " PIN Not Unique");
                        runningErrorCount++;
                        continue;
                    }
                    pinList.add(finalPin);
                }
                try {
                    encryptedPin = VomsUtil.encryptText(finalPin);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " Not able to encrypt PIN");
                    runningErrorCount++;
                    continue;
                }

                if (fileData.indexOf(SERIAL_NO_LABEL) == -1)// means not found
                {
                    // isValidFile = false;
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " Serial Number Not Found");
                    runningErrorCount++;
                    continue;
                }

                dataValueSerial = VoucherFileUploaderUtil.extractData(fileData, SERIAL_NO_LABEL, _valueSeparator);
                if (BTSLUtil.isNullString(dataValueSerial)) {
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " Serial Number Not Valid");
                    runningErrorCount++;
                    continue;
                } else if (!VoucherFileUploaderUtil.isValidDataLength(dataValueSerial.length(), minSerialLength, maxSerialLength)) {
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " Serial Number Not Valid");
                    runningErrorCount++;
                    continue;
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
                        runningErrorCount++;
                        _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " Serial Numbers are not Continous");
                        /*
                         * EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI
                         * .SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,
                         * "VoucherFileChecks[validateVoucherFile]"
                         * ,"","","","The serial number ("
                         * +presentSerialNo+") is not consecutive");
                         * throw new BTSLBaseException("VoucherFileChecks",
                         * "validateVoucherFile"
                         * ,PretupsErrorCodesI.VOUCHER_DUPLICATE_SERIAL_NO_ERROR
                         * );
                         */
                        continue;
                    }
                    previousSerialNo++;
                    // continue;
                }

                if (fileData.indexOf(PRODUCT_ID_LABEL) == -1)// means not found
                {
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " Product Not Found");
                    runningErrorCount++;
                    continue;
                }
                dataValueProfile = VoucherFileUploaderUtil.extractData(fileData, PRODUCT_ID_LABEL, _valueSeparator);

                if (VoucherUploadVO._AUTOPROCESSTYPE.equalsIgnoreCase(_voucherUploadVO.getProcessType()) && runningFileRecordCount == 1) {
                    _productID = dataValueProfile;
                    _voucherUploadVO.setProductID(_productID);
                }

                if (BTSLUtil.isNullString(dataValueProfile)) {
                    _log.error("validateVoucherFile", " Record Number = " + runningFileRecordCount + " Product Not Valid");
                    runningErrorCount++;
                    continue;
                } else if (!dataValueProfile.equalsIgnoreCase(_productID)) {
                    _log.error("validateVoucherFile", "Record Number = " + runningFileRecordCount + " Product Not consistent in the voucher File.");
                    runningErrorCount++;
                    continue;
                }

                // if the record is valid, prepare the 2D array with the parsed
                // values
                if (runningFileRecordCount == 1) {
                    _startingRecord = dataValueSerial;// set only for the first
                }
                // time
                _endingRecord = dataValueSerial;

                populateValuesInVoucherList(dataValueSerial, encryptedPin, dataValueProfile);

            }// end of while loop
             // checks if the number of error encontered while parsing of file
             // does not
             // exceed the user given error count limit in case the exception
             // occurs at the last line
            if (runningErrorCount > _allowedNumberofErrors) {
                _log.error("validateVoucherFile", " Total Number of error (" + runningErrorCount + ") in the File (" + _fileName + ") exceeds the user specified error number= " + _allowedNumberofErrors);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecks[validateVoucherFile]", "", "", "", "The number of error (" + runningErrorCount + ") in the file exceed the user specified value (" + _allowedNumberofErrors + ")");
                throw new BTSLBaseException("VoucherFileChecks", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);// "Total number of error in the file exceed the specified number of error"
            }

            _voucherUploadVO.setFromSerialNo(_startingRecord);
            _voucherUploadVO.setToSerialNo(_endingRecord);
            _voucherUploadVO.setActualNoOfRecords(_voucherArr.size());
            _voucherUploadVO.setVoucherArrayList(_voucherArr);

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("validateVoucherFile", " : BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(" validateVoucherFile", " Exception = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecks[validateVoucherFile]", "", "", "", e.getMessage());
            throw new BTSLBaseException("VoucherFileChecks", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);

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
    private void populateValuesInVoucherList(String p_serialNo, String p_pin, String p_productID) throws BTSLBaseException {
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

            long time = _currentDate.getTime() + (long) 24 * 365 * 1000 * 50 * 60 * 60;
            vomsVoucherVO.setExpiryDate(new Date(time));
            _voucherArr.add(vomsVoucherVO);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("VoucherFileChecks[populateValuesInVoucherList]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecks[populateValuesInVoucherList]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecks ", " populateValuesInVoucherList ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
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

            _channelUserVO = p_voucherUploadVO.getChannelUserVO();
            _currentDate = p_voucherUploadVO.getCurrentDate();
            _voucherUploadVO = p_voucherUploadVO;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("VoucherFileChecks[getValuesFromVO]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecks[getValuesFromVO]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecks ", " getValuesFromVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
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
