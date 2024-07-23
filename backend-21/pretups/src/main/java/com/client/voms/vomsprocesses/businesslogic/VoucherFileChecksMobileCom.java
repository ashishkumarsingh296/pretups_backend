package com.client.voms.vomsprocesses.businesslogic;

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

public class VoucherFileChecksMobileCom implements VoucherFileChecksI {

    private String _startingRecord = null;
    private String _endingRecord = null;
    private String _productID = null; // product ID for the vouchers
    private String _filePath = null; // path of the file
    private String _fileName = null; // name of file
    private String _venderName = null;
    private String _currency = null;
    private String _quantity = null; // number of records to be uploaded

    private Date _currentDate = new Date(); // today's date

    private ArrayList<String> _fileDataArr = null;
    private ArrayList _voucherArr = null;
    private ChannelUserVO _channelUserVO = null;
    private VoucherUploadVO _voucherUploadVO = null;

    private int _maxNoRecordsAllowed = 0;
    private int _numberOfRecordsEntered = 0;
    private int _runningErrorCount = 0;
    private int _pinLength = 0;
    private ArrayList<String> _errorList = null; // used to save error
                                                 // information for fututre use

    // file format
    private int _numberofvaluesinheader = 0;
    private long _numberOfRecordScheduled = 0;
    private int _allowedNumberofErrors = 0;
    private String _valueSeparator = null; // separater for the file
    private String _headerSeparator = null;
    private String _vomsfiledateformat = null; // date format for the file
    private static Log _log = LogFactory.getLog(VoucherFileChecksMobileCom.class.getName());

    public void loadConstantValues() throws BTSLBaseException {
        final String METHOD_NAME = "loadConstantValues";
        if (_log.isDebugEnabled()) {
            _log.debug("loadConstantValues", " Entered the loadContantsValues.....");
        }
        try {
            try {
                _vomsfiledateformat = Constants.getProperty("VOUCHER_EXPIRY_DATE_FORMAT");
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksMobileCom", " loadConstantValues   VOUCHER_EXPIRY_DATE_FORMAT " + _vomsfiledateformat);
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" loadConstantValues ", "Invalid value for VOUCHER_EXPIRY_DATE_FORMAT in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksMobileCom[loadConstantValues]", "", "", "", "Invalid value for VOUCHER_EXPIRY_DATE_FORMAT in Constant File ");
                throw new BTSLBaseException("VoucherFileChecksMobileCom ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                _allowedNumberofErrors = Integer.parseInt(Constants.getProperty("VOMS_TOTAL_ALLOWED_ERRORS"));
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksMobileCom ", " loadConstantValues   VOMS_TOTAL_ALLOWED_ERRORS " + _allowedNumberofErrors);
                }

            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _allowedNumberofErrors = 0;
                _log.info("loadConstantValues", " Total number of error (Entry VOMS_TOTAL_ALLOWED_ERRORS) not found in Constants . Thus taking default values as 0");
            }
            try {
                _numberofvaluesinheader = Integer.parseInt(Constants.getProperty("NUMBER_OF_VALUES_IN_HEADER"));
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksMobileCom ", " loadConstantValues  NUMBER_OF_VALUES_IN_HEADER " + _numberofvaluesinheader);
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" loadConstantValues", " Invalid value for NUMBER_OF_VALUES_IN_HEADER  in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksMobileCom[loadConstantValues]", "", "", "", "Invalid value for NUMBER_OF_VALUES_IN_HEADER in Constant File while voucher upload process");
                throw new BTSLBaseException("VoucherFileChecksMobileCom ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                _valueSeparator = Constants.getProperty("VOMS_FILE_SEPARATOR");
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksMobileCom ", " loadConstantValues   VOMS_FILE_SEPARATOR " + _valueSeparator);
                }
                if (BTSLUtil.isNullString(_valueSeparator)) {
                    _log.error(" loadConstantValues", " Invalid value for VOMS_FILE_SEPARATOR  in Constant File ");
                    _valueSeparator = " ";
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" loadConstantValues", " Invalid value for VOMS_FILE_SEPARATOR  in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksMobileCom[loadConstantValues]", "", "", "", "Invalid value for VOMS_FILE_SEPARATOR in Constant File while voucher upload process");
                throw new BTSLBaseException("VoucherFileChecksMobileCom ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                _headerSeparator = Constants.getProperty("VOMS_FILE_HEADER_SEPARATOR");
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksMobileCom ", " loadConstantValues   VOMS_FILE_HEADER_SEPARATOR " + _headerSeparator);
                }
                if (BTSLUtil.isNullString(_headerSeparator)) {
                    _log.error(" loadConstantValues", " Invalid value for VOMS_FILE_HEADER_SEPARATOR  in Constant File ");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksMobileCom[loadConstantValues]", "", "", "", "Invalid value for VOMS_FILE_HEADER_SEPARATOR in Constant File while voucher upload process");
                    throw new BTSLBaseException("VoucherFileChecksMobileCom ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" loadConstantValues", " Invalid value for VOMS_FILE_HEADER_SEPARATOR  in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksMobileCom[loadConstantValues]", "", "", "", "Invalid value for VOMS_FILE_HEADER_SEPARATOR in Constant File while voucher upload process");
                throw new BTSLBaseException("VoucherFileChecksMobileCom ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                _numberOfRecordScheduled = Long.parseLong(Constants.getProperty("VOMS_MAX_FILE_LENGTH"));
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksMobileCom", " loadConstantValues   NUMBER_OF_RECORDS_SCHEDULE " + _numberOfRecordScheduled);
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" loadConstantValues ", "Invalid value for REC_ALLOWED_FOR_SCRIPT in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksMobileCom[loadConstantValues]", "", "", "", "Invalid value for REC_ALLOWED_FOR_SCRIPT in Constant File ");
                throw new BTSLBaseException("VoucherFileChecksMobileCom ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksMobileCom[loadConstantValues]", "", "", "", "Exception while loading the constant values from the Constants.prop file " + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksMobileCom ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" loadConstantValues", "   Exiting with _allowedNumberofErrors=" + _allowedNumberofErrors + " _valueSeparator=" + _valueSeparator);
            }
        }// end of finally
    }

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
            _fileDataArr = new ArrayList<String>();
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
                _fileDataArr.add(fileData);
            }
            // this is used to check if the actual number of records in the file
            // and the records
            // entered by the user are same or not
            if (_numberOfRecordsEntered != fileLineCount) {
                _log.error("getFileLength", " Total Number of Records (" + (fileLineCount) + ") in the File (" + _fileName + ") doesn't match the entered value = " + _numberOfRecordsEntered + ". Control Returning ");
                throw new BTSLBaseException("VoucherFileChecksMobileCom", "getFileLength", PretupsErrorCodesI.NO_OF_RECORDS_NOT_EQUALTO_QUANTITY, "uploadvoucher");
            }
            // this is used to check the number of records are greater than
            // 20000
            // if number of records in the file are greater than 20000 than
            // schedule the file for later process
            if (fileLineCount > _numberOfRecordScheduled) {
                _log.error("getFileLength", " Number of records in the file  (" + fileLineCount + ") in the File (" + _fileName + ") are more than scheduled = (" + _numberOfRecordScheduled + ")");
                throw new BTSLBaseException("VoucherFileChecksMobileCom", "getFileLength", PretupsErrorCodesI.VOUCHER_FILE_MORE_RECORDS);
            }

        }// end of try block
        catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("getFileLength", " : BTSLBaseException " + be);
            throw be;
        } catch (IOException io) {
            _log.errorTrace(METHOD_NAME, io);
            _log.error("getFileLength", "The file (" + _fileName + ")could not be read properly to get the number of records");
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksMobileCom[getFileLength]", "", "", "", "The file (" + _fileName + ")could not be read properly to get the number of records");
            throw new BTSLBaseException("VoucherFileChecksMobileCom", "getFileLength", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);
        }// end of IOException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(" getFileLength", " Exception = " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksMobileCom[getFileLength]", "", "", "", e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksMobileCom", "getFileLength", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);
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
     * To rwead the Header of file i.e
     * "Num of records,PIN Length, Curreny" e.g. 5,14,JOD
     * 
     * @param p_inFile
     * @return
     * @throws Exception
     */
    private int readFileHeader(BufferedReader p_inFile) throws Exception {
        final String METHOD_NAME = "readFileHeader";
        if (_log.isDebugEnabled()) {
            _log.debug(" readFileHeader", " Entered to validate the header of _fileName=" + _fileName);
        }
        // Connection con = null;
        _errorList = new ArrayList();
        try {
            String fileData = p_inFile.readLine();
            fileData = fileData.toUpperCase();
            String arr[] = null;
            if (!BTSLUtil.isNullString(fileData)) {
                fileData = fileData.trim();
                arr = fileData.split(_headerSeparator);

                _quantity = arr[0];
                if (BTSLUtil.isNullString(_quantity)) {
                    _log.error(" readFileHeader", " Quantity is Null");
                    throw new BTSLBaseException("VoucherFileChecksMobileCom", "readFileHeader", "Quantity is Null", "uploadvoucher");
                }

                _quantity = _quantity.trim();
                if (!BTSLUtil.isNumeric(_quantity)) {
                    _log.error(" readFileHeader", " Invalid Quantity");
                    throw new BTSLBaseException("VoucherFileChecksMobileCom", "readFileHeader", "Invalid Quantity", "uploadvoucher");
                }

                String pinLength = arr[1];
                if (BTSLUtil.isNullString(pinLength)) {
                    _log.error(" readFileHeader", " Pin Length is Null");
                    throw new BTSLBaseException("VoucherFileChecksMobileCom", "readFileHeader", "PIN Length is null", "uploadvoucher");
                }

                pinLength = pinLength.trim();
                if (!BTSLUtil.isNumeric(pinLength)) {
                    _log.error(" readFileHeader", " Invalid Pin Length");
                    throw new BTSLBaseException("VoucherFileChecksMobileCom", "readFileHeader", "Invalid PIN Length", "uploadvoucher");
                }
                _pinLength = Integer.parseInt(pinLength);

                _currency = arr[2];
                if (BTSLUtil.isNullString(_currency)) {
                    _log.error(" readFileHeader", " Quantity is Null");
                    throw new BTSLBaseException("VoucherFileChecksMobileCom", "readFileHeader", PretupsErrorCodesI.ERROR_INVALID_PRODUCT_QUANTITY, "uploadvoucher");
                }
                _currency = _currency.trim();
            }
            try {
                _numberOfRecordsEntered = Integer.parseInt(_quantity);

                if (_maxNoRecordsAllowed < _numberOfRecordsEntered) {
                    _log.error("readFileHeader", "Total number of records entered (" + _numberOfRecordsEntered + ") should be less than the allowed records in file (" + _maxNoRecordsAllowed + ")");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherFileChecksMobileCom[readFileHeader]", "", "", "", "Total number of records entered (" + _numberOfRecordsEntered + ") should be less than the allowed records in voucher file (" + _maxNoRecordsAllowed + ")");
                    throw new BTSLBaseException("VoucherFileChecksMobileCom", "readFileHeader", PretupsErrorCodesI.VOUCHER_ERROR_NUMBER_REC_MORE_THAN_ALLWD, "uploadvoucher");// "The voucher file does not exists at the location specified"
                }
            } catch (NumberFormatException e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error("readFileHeader", " Quantity field in the header should be Numeric");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksMobileCom[readFileHeader]", "", "", "", " Quantity field in the header should be integer");
                throw new BTSLBaseException("VoucherFileChecksMobileCom", "readFileHeader", PretupsErrorCodesI.VOUCHER_FILE_HEADER_QUANTITY_ERROR);
            }
            _voucherUploadVO.setNoOfRecordsInFile(_numberOfRecordsEntered);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error(" readFileHeader", " Exception = " + be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksMobileCom[readFileHeader]", "", "", "", be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(" readFileHeader", " Exception = " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksMobileCom[readFileHeader]", "", "", "", e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksMobileCom", "readFileHeader", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" readFileHeader", " Exiting _numberOfRecordsEntered=" + _numberOfRecordsEntered);
            }
        }// end of Exception
        return _numberOfRecordsEntered;
    }

    public VoucherUploadVO validateVoucherFile() throws BTSLBaseException, Exception {
        final String METHOD_NAME = "validateVoucherFile";
        if (_log.isDebugEnabled()) {
            _log.debug(" validateVoucherFile", " Entered to validate the file _fileName=" + _fileName);
        }

        String dataValuePIN = null;
        String dataValueSerial = null;
        String dataValueAmount = null;
        String dataValueProfile = null;
        String dataValueVendorName = null;
        Date dataValueExpDate = null;

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
        ArrayList pinList = new ArrayList();
        boolean firstOnly = true;
        // Connection con = null;

        try {
            _voucherArr = new ArrayList();
            minSerialLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MIN_LENGTH))).intValue();
            maxSerialLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH))).intValue();
            minPinLength = _pinLength;
            maxPinLength = _pinLength;
            Date currentDate = new Date();
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
                if (fileRecord.length != 6) {
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
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksMobileCom[validateVoucherFile]", "", "", "", "The number of error (" + _runningErrorCount + ") in the file exceed the user specified value (" + _allowedNumberofErrors + ")");
                    throw new BTSLBaseException("VoucherFileChecksMobileCom", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT, "uploadvoucher");// "Total number of error in the file exceed the specified number of error"
                }

                dataValuePIN = fileRecord[0].trim();
                if (BTSLUtil.isNullString(dataValuePIN)) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " PIN Not Valid");
                    _runningErrorCount++;
                    _errorList.add(" Record Number = " + _runningFileRecordCount + " PIN Not Valid");
                    continue;
                }
                // Above we have validated the pin length in the header with
                // system preference
                // so here just validating, pin length against header pin length
                else if (!VoucherFileUploaderUtil.isValidDataLength(dataValuePIN.length(), minPinLength, maxPinLength)) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " PIN Length Not Valid");
                    _errorList.add(" Record Number = " + _runningFileRecordCount + " PIN Length Not Valid");
                    _runningErrorCount++;
                    continue;
                }
                String finalPin = dataValuePIN;

                // arraylist pinList and before adding next time searches in
                // this list first
                if (pinList.isEmpty()) {
                    pinList.add(finalPin);
                } else {
                    if (pinList.contains(finalPin)) {
                        _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " PIN Not Unique");
                        _runningErrorCount++;
                        _errorList.add(" Record Number = " + _runningFileRecordCount + " PIN Not Unique");
                        continue;
                    }
                    pinList.add(finalPin);
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

                dataValueSerial = fileRecord[1].trim();
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

                dataValueAmount = fileRecord[2].trim();
                if (BTSLUtil.isNullString(dataValueAmount)) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Amount Not Valid");
                    _runningErrorCount++;
                    _errorList.add(" Record Number = " + _runningFileRecordCount + " Amount Not Valid");
                    continue;
                } else if (!BTSLUtil.isNumeric(dataValueAmount)) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Amount Not numeric");
                    _runningErrorCount++;
                    _errorList.add(" Record Number = " + _runningFileRecordCount + " Amount Not numeric");
                    continue;
                }

                long valueAmount = Long.parseLong(dataValueAmount);

                dataValueProfile = fileRecord[3].trim();
                if (BTSLUtil.isNullString(dataValueProfile)) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " ProfileId Not Valid");
                    _runningErrorCount++;
                    _errorList.add(" Record Number = " + _runningFileRecordCount + " ProfileId Not Valid");
                    continue;
                }
                _productID = dataValueProfile;

                String date = fileRecord[4].trim();
                if (BTSLUtil.isNullString(date)) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Date Not Valid");
                    _runningErrorCount++;
                    _errorList.add(" Record Number = " + _runningFileRecordCount + " Date Not Valid");
                    continue;
                }

                try {
                    dataValueExpDate = BTSLUtil.getDateFromDateString(date.trim(), _vomsfiledateformat);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Expiry Date Not Valid");
                    _runningErrorCount++;
                    _errorList.add(" Record Number = " + _runningFileRecordCount + " Expiry Date Not Valid");
                    continue;
                }
                // check the expiry date is before current date
                if (dataValueExpDate.before(currentDate)) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Expiry Date Not Valid");
                    _runningErrorCount++;
                    _errorList.add(" Record Number = " + _runningFileRecordCount + " Expiry Date Not Valid");
                    continue;
                }

                dataValueVendorName = fileRecord[5].trim();
                if (BTSLUtil.isNullString(dataValueVendorName)) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Vendor Name Not Valid");
                    _runningErrorCount++;
                    _errorList.add(" Record Number = " + _runningFileRecordCount + " Vendor Name Not Valid");
                    continue;
                }
                _venderName = dataValueVendorName;

                if (_runningFileRecordCount == 1) {
                    _startingRecord = dataValueSerial;// set only for the first
                }
                // time
                _endingRecord = dataValueSerial;

                populateValuesInVoucherList(dataValueSerial, encryptedPin, _productID, dataValueExpDate, valueAmount);

            }// end of for loop
             // check running error count is less than allowed error count
            if (_runningErrorCount > _allowedNumberofErrors) {
                _log.error("validateVoucherFile", " Total Number of error (" + _runningErrorCount + ") in the File (" + _fileName + ") exceeds the user specified error number= " + _allowedNumberofErrors);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksMobileCom[validateVoucherFile]", "", "", "", "The number of error (" + _runningErrorCount + ") in the file exceed the user specified value (" + _allowedNumberofErrors + ")");
                throw new BTSLBaseException("VoucherFileChecksMobileCom", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT, "uploadvoucher");// "Total number of error in the file exceed the specified number of error"
            }// end of if block
             // counts the number of actual valid records in the file
            _voucherUploadVO.setProductID(_productID);
            _voucherUploadVO.setFromSerialNo(_startingRecord);
            _voucherUploadVO.setToSerialNo(_endingRecord);
            _voucherUploadVO.setActualNoOfRecords(_voucherArr.size());
            _voucherUploadVO.setVoucherArrayList(_voucherArr);
            _voucherUploadVO.setErrorArrayList(_errorList);
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("validateVoucherFile", " : BTSLBaseException " + be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksMobileCom[validateVoucherFile]", "", "", "", be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(" validateVoucherFile", " Exception = " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksMobileCom[validateVoucherFile]", "", "", "", e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksMobileCom", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" validateVoucherFile", " Exiting ");
            }
        }
        return _voucherUploadVO;

    }

    private void populateValuesInVoucherList(String p_serialNo, String p_pin, String p_productID, Date expiryDate, long p_amount) throws BTSLBaseException {
        final String METHOD_NAME = "populateValuesInVoucherList";
        if (_log.isDebugEnabled()) {
            _log.debug(" populateValuesInVoucherList", " Entered with p_serialNo=" + p_serialNo + " p_pin=" + p_pin + " p_productID=" + p_productID + " p_amount=" + p_amount);
        }
        try {
            VomsVoucherVO vomsVoucherVO = new VomsVoucherVO();
            vomsVoucherVO.setSerialNo(p_serialNo);
            vomsVoucherVO.setPinNo(p_pin);
            vomsVoucherVO.setMRP(p_amount);
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
            _log.error("VoucherFileChecksMobileCom[populateValuesInVoucherList]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksMobileCom[populateValuesInVoucherList]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksMobileCom ", " populateValuesInVoucherList ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" populateValuesInVoucherList", " Exiting ");
            }
        }

    }

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
            _log.error("VoucherFileChecksMobileCom[getValuesFromVO]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksMobileCom[getValuesFromVO]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksMobileCom ", " getValuesFromVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
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
