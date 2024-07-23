package com.client.voms.vomsprocesses.businesslogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.spec.AlgorithmParameterSpec;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

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
import com.btsl.voms.voucher.businesslogic.VomsSerialUploadCheckVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class VoucherFileChecksAirtelBDFormat implements VoucherFileChecksI {

    private final static String FILE_VALUE_SEPARATOR = ",";
    private String _vomsfiledateformat = null;
    private int _numberOfRecordsEntered = 0;
    // private int _pinLength=13;

    private int _allowedNumberofErrors = 0;
    private int _numberofvaluesinheader = 0;
    private String _startingRecord = null;
    private String _endingRecord = null;
    private String _productID = null;
    private String _filePath = null;
    private String _fileName = null;
    private ChannelUserVO _channelUserVO = null;
    private Date _currentDate = null;

    private ArrayList<VomsVoucherVO> _voucherArr = null;
    private VoucherUploadVO _voucherUploadVO = null;
    private ArrayList<String> _fileDataArr = null;
    private Date _expiryDate = null;
    private String _venderName = null;
    private int _vomsFaceValue = 0;

    private VomsSerialUploadCheckVO _vomsSerialUploadCheckVO = null;
    private static Log _log = LogFactory.getLog(VoucherFileChecksAirtelBDFormat.class.getName());

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
                    _log.debug("VoucherFileChecksAirtelBDFormat", " loadConstantValues   VOUCHER_EXPIRY_DATE_FORMAT " + _vomsfiledateformat);
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" loadConstantValues ", "Invalid value for VOUCHER_EXPIRY_DATE_FORMAT in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksAirtelBDFormat[loadConstantValues]", "", "", "", "Invalid value for VOUCHER_EXPIRY_DATE_FORMAT in Constant File ");
                throw new BTSLBaseException("VoucherFileChecksAirtelBDFormat ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                _allowedNumberofErrors = Integer.parseInt(Constants.getProperty("VOMS_TOTAL_ALLOWED_ERRORS"));
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksAirtelBDFormat ", " loadConstantValues   VOMS_TOTAL_ALLOWED_ERRORS " + _allowedNumberofErrors);
                }

            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _allowedNumberofErrors = 0;
                _log.info("loadConstantValues", " Total number of error (Entry VOMS_TOTAL_ALLOWED_ERRORS) not found in Constants . Thus taking default values as 0");
            }
            try {
                _numberofvaluesinheader = Integer.parseInt(Constants.getProperty("NUMBER_OF_VALUES_IN_HEADER"));
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksAirtelBDFormat ", " loadConstantValues  NUMBER_OF_VALUES_IN_HEADER " + _numberofvaluesinheader);
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" loadConstantValues", " Invalid value for NUMBER_OF_VALUES_IN_HEADER  in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksAirtelBDFormat[loadConstantValues]", "", "", "", "Invalid value for NUMBER_OF_VALUES_IN_HEADER in Constant File while voucher upload process");
                throw new BTSLBaseException("VoucherFileChecksAirtelBDFormat ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                // _valueSeparator =
                // Constants.getProperty("VOMS_FILE_SEPARATOR");
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksAirtelBDFormat ", " loadConstantValues   VOMS_FILE_SEPARATOR " + FILE_VALUE_SEPARATOR);
                }
                if (FILE_VALUE_SEPARATOR.length() != 1) {
                    _log.error(" loadConstantValues", " Invalid value for VOMS_FILE_SEPARATOR  in Constant File ");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksAirtelBDFormat[loadConstantValues]", "", "", "", "Invalid value for VOMS_FILE_SEPARATOR in Constant File while voucher upload process");
                    throw new BTSLBaseException("VoucherFileChecksAirtelBDFormat ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);

                }

            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" loadConstantValues", " Invalid value for VOMS_FILE_SEPARATOR  in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksAirtelBDFormat[loadConstantValues]", "", "", "", "Invalid value for VOMS_FILE_SEPARATOR in Constant File while voucher upload process");
                throw new BTSLBaseException("VoucherFileChecksAirtelBDFormat ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksAirtelBDFormat[loadConstantValues]", "", "", "", "Exception while loading the constant values from the Constants.prop file " + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksAirtelBDFormat ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" loadConstantValues", "   Exiting with _allowedNumberofErrors=" + _allowedNumberofErrors + " _valueSeparator=" + FILE_VALUE_SEPARATOR);
            }
        }// end of finally
    }// end of loadConstantValues

    private String getKey(String p_fileName) throws BTSLBaseException {
        final String methodName = "getKey";
        BufferedReader br = null;
        File f = null;
        try {
            f = new File(p_fileName.substring(0, p_fileName.lastIndexOf("_") + 1) + "key" + p_fileName.substring(p_fileName.lastIndexOf(".")));
            br = new BufferedReader(new FileReader(f));
            while (br.ready()) {
                return br.readLine().toString();
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksAirtelBDFormat[loadConstantValues]", "", "", "", "Exception while loading the constant values from the Constants.prop file " + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksAirtelBDFormat ", " getKey  unable to get the key from the file=" + f.getAbsolutePath(), PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of catch-Exception
        finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    _log.errorTrace(methodName, e);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(" getKey", "   Exiting with _allowedNumberofErrors=" + _allowedNumberofErrors + " _valueSeparator=" + FILE_VALUE_SEPARATOR);
            }
        }// end of finally
        return "";
    }

    private BufferedReader decrypt(InputStream is, String p_fileName) throws BTSLBaseException {
        final String methodName = "decrypt";
        String key = getKey(p_fileName);
        Cipher dcipher;
        byte[] iv = new byte[] { (byte) 0x8E, 0x12, 0x39, (byte) 0x9C, 0x07, 0x72, 0x6F, 0x5A };
        BufferedReader br = null;
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
        try {
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            SecretKey desKey = skf.generateSecret(dks);
            dcipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            dcipher.init(Cipher.DECRYPT_MODE, desKey, paramSpec);
            br = new BufferedReader(new InputStreamReader(new CipherInputStream(is, dcipher)));
            return br;
        } catch (java.security.InvalidAlgorithmParameterException e) {
            _log.errorTrace(methodName, e);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }

        return br;
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
    public void getFileLength(VoucherUploadVO p_voucherUploadVO) throws BTSLBaseException, Exception {
        final String methodName = "getFileLength";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered with p_voucherUploadVO=" + p_voucherUploadVO);
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
                _log.debug(methodName, " Starting processing to get the number of records for source File Path = " + srcFile + " File Name" + _fileName);
            }

            // creates a new bufferedreader to read the Voucher Upload file
            inFile = decrypt((new FileInputStream((srcFile))), srcFile.getAbsolutePath());
            // inFile = new BufferedReader(new FileReader(srcFile));
            fileData = null;
            while ((fileData = inFile.readLine()) != null) {
                // this is used to check if the line is blank ie the record is
                // blank
                if (BTSLUtil.isNullString(fileData)) {
                    _log.error(methodName, " Record Number = " + lineCount + " No Data Found");
                    continue;
                }
                ++fileLineCount;
                _fileDataArr.add(fileData);
            }
        }// end of try block
        catch (IOException io) {
            _log.errorTrace(methodName, io);
            _log.error(methodName, "The file (" + _fileName + ")could not be read properly to get the number of records");
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAirtelBDFormat[getFileLength]", "", "", "", "The file (" + _fileName + ")could not be read properly to get the number of records");
            throw new BTSLBaseException("VoucherFileChecksAirtelBDFormat", methodName, PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);
        }// end of IOException
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(" getFileLength", " Exception = " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VocuherFileChecks[getFileLength]", "", "", "", e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksAirtelBDFormat", methodName, PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);
        }// end of Exception
        finally {
            try {
                inFile.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
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
        final String methodName = "validateVoucherFile";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered to validate the file _fileName=" + _fileName);
        }

        // String dataValuePINFirst =null;
        String dataValuePIN = null;
        String dataValueSerial = null;
        long previousSerialNo = 0L;
        long presentSerialNo = 0L;
        int runningErrorCount = 0;
        int _runningFileRecordCount = 0;
        String fileData = null;
        ArrayList<String> pinList = new ArrayList<String>();
        String encryptedPin = null;
        String[] fileRecord = null;
        boolean firstOnly = true;
        int minSerialLength = 0;
        int maxSerialLength = 0;
        _numberOfRecordsEntered = 0;
        try {
            _voucherArr = new ArrayList<VomsVoucherVO>();
            minSerialLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MIN_LENGTH))).intValue();
            maxSerialLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH))).intValue();

            // Delte this
            // minSerialLength=9;
            // maxSerialLength=13;
            int fileDataArrSize = _fileDataArr.size();
            for (int loop = 0; loop < fileDataArrSize; loop++) {
                fileData = null;
                fileData = (String) _fileDataArr.get(loop);
                fileRecord = fileData.split(FILE_VALUE_SEPARATOR);
                if (_log.isDebugEnabled()) {
                    _log.debug(" validateVoucherFile", " Entered ::" + "fileData ::" + fileData + " fileRecord ::" + fileRecord);
                }
                if (fileRecord.length == 0) {
                    _log.error(methodName, " Record Number = " + _runningFileRecordCount + " No Data Found");
                    runningErrorCount++;
                    continue;
                }

                // Garbage Handling
                if (fileRecord.length > 0 && fileRecord.length != 6) {
                    _log.error(methodName, " Record Number = " + _runningFileRecordCount + " No Data Found");
                    continue;
                }

                if (_log.isDebugEnabled()) {
                    _log.debug("getFileLength", " fileRecord ::" + fileRecord.length + "fileRecord[0] :: " + fileRecord[0] + "fileRecord[1] :: " + fileRecord[1]);
                }

                // checks if the number of error encontered while parsing of
                // file does not
                // exceed the user given error count limit
                if (runningErrorCount > _allowedNumberofErrors) {
                    // isValidFile = false;
                    _log.error(methodName, " Total Number of error (" + runningErrorCount + ") in the File (" + _fileName + ") exceeds the user specified error number= " + _allowedNumberofErrors);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAirtelBDFormat[validateVoucherFile]", "", "", "", "The number of error (" + runningErrorCount + ") in the file exceed the user specified value (" + _allowedNumberofErrors + ")");
                    throw new BTSLBaseException("VoucherFileChecksAirtelBDFormat", methodName, PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);// "Total number of error in the file exceed the specified number of error"
                }

                _numberOfRecordsEntered++;
                _runningFileRecordCount++;// keeps record of number of records
                                          // in file

                dataValueSerial = fileRecord[1];
                if (BTSLUtil.isNullString(dataValueSerial)) {
                    _log.error(methodName, " Record Number = " + _runningFileRecordCount + " Serial Number Not Valid");
                    runningErrorCount++;
                    continue;
                } else if (!VoucherFileUploaderUtil.isValidDataLength(dataValueSerial.length(), minSerialLength, maxSerialLength)) {
                    _log.error(methodName, " Record Number = " + _runningFileRecordCount + " Serial Number Not Valid");
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
                        _log.error(methodName, " Record Number = " + _runningFileRecordCount + " Serial Numbers are not Continous");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAirtelBDFormat[validateVoucherFile]", "", "", "", "The serial number (" + presentSerialNo + ") is not consecutive");
                        // break;
                        throw new BTSLBaseException("VoucherFileChecksAirtelBDFormat", methodName, PretupsErrorCodesI.VOUCHER_DUPLICATE_SERIAL_NO_ERROR);
                    }
                    previousSerialNo++;
                }

                // dataValueProfile=VoucherFileUploaderUtil.extractData(fileData,PRODUCT_ID_LABEL,_valueSeparator);
                dataValuePIN = fileRecord[0];
                if (BTSLUtil.isNullString(dataValuePIN)) {
                    _log.error(methodName, " Record Number = " + _runningFileRecordCount + " PIN Not Valid");
                    runningErrorCount++;
                    continue;
                }
                // Above we have validated the pin length in the header with
                // system preference
                // so here just validating, pin length against header pin length
                else if (!VoucherFileUploaderUtil.isValidDataLength(dataValuePIN.length(), ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MIN_LENGTH))).intValue(), ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MAX_LENGTH))).intValue())) {
                    _log.error(methodName, " Record Number = " + _runningFileRecordCount + " PIN Not Valid");
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
                        _log.error(methodName, " Record Number = " + _runningFileRecordCount + " PIN Not Unique");
                        runningErrorCount++;
                        continue;
                    }
                    /*
                     * if(finalPin.length()!=_pinLength)
                     * {
                     * _log.error("validateVoucherFile"," Record Number = "+
                     * _runningFileRecordCount+ " PIN Length is not correct");
                     * runningErrorCount++;
                     * continue;
                     * }
                     */
                    pinList.add(finalPin);
                }
                try {
                    encryptedPin = VomsUtil.encryptText(finalPin);
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    _log.error(methodName, " Record Number = " + _runningFileRecordCount + " Not able to encrypt PIN");
                    runningErrorCount++;
                    continue;
                }

                // if the record is valid, prepare the 2D array with the parsed
                // values
                if (_runningFileRecordCount == 1) {
                    /*
                     * if(!_startingRecord.equalsIgnoreCase(dataValueSerial))
                     * {
                     * System.out.println("Starting sequence number["+
                     * _startingRecord+
                     * "] specified in header is not matching the starting serial number ["
                     * + dataValueSerial + "] in file data..............");
                     * _log.error("validateVoucherFile",
                     * " Serial Number in Record Number = "
                     * +_runningFileRecordCount+
                     * " is not matching the Starting Serial Numbers given in header data"
                     * );
                     * EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.
                     * SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,
                     * "VoucherFileChecksAirtelBDFormat[validateVoucherFile]"
                     * ,"","","","Serial Number in Record Number = "+
                     * _runningFileRecordCount+
                     * " is not matching the Starting Serial Numbers given in header data"
                     * );
                     * //break;
                     * throw new
                     * BTSLBaseException("VoucherFileChecksAirtelBDFormat"
                     * ,"validateVoucherFile"
                     * ,PretupsErrorCodesI.VOUCHER_ERROR_HEADER_INFO);
                     * }
                     */
                    _startingRecord = dataValueSerial;// set only for the first
                                                      // time

                    int multiplyFactor = 100 / ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
                    String productIDFromQty = null;
                    _vomsFaceValue = Integer.parseInt(fileRecord[2].toString()) / multiplyFactor;
                    Connection con = null;
                    con = OracleUtil.getSingleConnection();
                    productIDFromQty = new VomsVoucherDAO().loadProductIDFromMRP(con, _vomsFaceValue);
                    if (BTSLUtil.isNullString(productIDFromQty)) {

                        _log.error("readFileHeader", "Product ID dose no exist for face value " + _vomsFaceValue);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAirtelBDFormat[readFileHeader]", "", "", "", "Product ID dose no exist for face value " + _vomsFaceValue);
                        throw new BTSLBaseException("VoucherFileChecksAirtelBDFormat", "readFileHeader", PretupsErrorCodesI.PRODUCT_NOT_EXIST);// "Total number of records in the file are different from the one specified"

                    }

                    if (!BTSLUtil.isNullString(_productID) && !_productID.equalsIgnoreCase(productIDFromQty)) {
                        _log.error("readFileHeader", "Product ID given by user from console ::" + _productID + " and in Voucher file header ::" + productIDFromQty + " are not matching ");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAirtelBDFormat[readFileHeader]", "", "", "", "Product ID given by user from console ::" + _productID + " and in Voucher file header ::" + productIDFromQty + " are not matching ");
                        throw new BTSLBaseException("VoucherFileChecksAirtelBDFormat", "readFileHeader", PretupsErrorCodesI.PRODUCT_NOT_EXIST);// "Total number of records in the file are different from the one specified"
                    }
                    // p_voucherUploadVO.setProductID(productIDFromQty);
                    _voucherUploadVO.setProductID(productIDFromQty);
                    if (con != null) {
                        con.close();
                    }

                }
                _endingRecord = dataValueSerial;
                String _tempExpiryDate;
                Date currentDate;
                _tempExpiryDate = fileRecord[4];
                if (BTSLUtil.isNullString(_tempExpiryDate)) {
                    _log.error(methodName, " Record Number = " + _runningFileRecordCount + " Expiry Date Not Valid");
                    runningErrorCount++;
                    continue;
                } else {

                    currentDate = new Date();

                    if (!BTSLUtil.isNullString(_tempExpiryDate)) {
                        try {
                            _expiryDate = BTSLUtil.getDateFromDateString(_tempExpiryDate, _vomsfiledateformat);
                            if (_expiryDate.before(currentDate)) {
                                _log.error(methodName, " Expiary date of vouchers is already expired.");
                            }
                        } catch (Exception e) {
                            _log.errorTrace(methodName, e);
                            _log.error(methodName, " Expiry-date Not Valid");
                        }
                    }
                }
                populateValuesInVoucherList(dataValueSerial, encryptedPin, _voucherUploadVO.getProductID(), _expiryDate);

            }// end of for loop
             // counts the number of actual valid records in the file
            _voucherUploadVO.setFromSerialNo(_startingRecord);
            _voucherUploadVO.setToSerialNo(_endingRecord);
            _voucherUploadVO.setActualNoOfRecords(_voucherArr.size());
            _voucherUploadVO.setVoucherArrayList(_voucherArr);
            _voucherUploadVO.setNoOfRecordsInFile(_numberOfRecordsEntered);

        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error(methodName, " : BTSLBaseException " + be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksAirtelBDFormat[validateVoucherFile]", "", "", "", be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(" validateVoucherFile", " Exception = " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksAirtelBDFormat[validateVoucherFile]", "", "", "", e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksAirtelBDFormat", methodName, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);

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
        final String methodName = " populateValuesInVoucherList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered with p_serialNo=" + p_serialNo + " p_pin=" + p_pin + " p_productID=" + p_productID);
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
            _log.errorTrace(methodName, e);
            _log.error("VoucherFileChecksAirtelBDFormat[populateValuesInVoucherList]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksAirtelBDFormat[populateValuesInVoucherList]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksAirtelBDFormat ", " populateValuesInVoucherList ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting ");
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
        final String methodName = "getValuesFromVO";
        if (_log.isDebugEnabled()) {

            _log.debug(methodName, " Entered with  p_voucherUploadVO=" + p_voucherUploadVO);
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
            _log.errorTrace(methodName, e);
            _log.error("VoucherFileChecksAirtelBDFormat[getValuesFromVO]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksAirtelBDFormat[getValuesFromVO]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksAirtelBDFormat ", " getValuesFromVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" getValuesFromVO", " Exiting");
            }
        }
    }

    public VomsSerialUploadCheckVO populateVomsSerialUploadCheckVO(Connection p_con) throws BTSLBaseException, Exception {
        int addCount = 0;
        ArrayList voucherList = new ArrayList();
        voucherList = _voucherUploadVO.getVoucherArrayList();
        int length = voucherList.size();
        VomsVoucherVO vomsvoucherVO = (VomsVoucherVO) voucherList.get(0);
        String startSerialNO = vomsvoucherVO.getSerialNo();
        vomsvoucherVO = (VomsVoucherVO) voucherList.get(length - 1);
        String endSerialNO = vomsvoucherVO.getSerialNo();
        final String methodName = "populateVomsSerialUploadCheckVO";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered ");
        }
        try {
            _vomsSerialUploadCheckVO = new VomsSerialUploadCheckVO();
            _vomsSerialUploadCheckVO.setStartSerialNo(startSerialNO);
            _vomsSerialUploadCheckVO.setEndSerialNO(endSerialNO);
            _vomsSerialUploadCheckVO.setDenomination(_vomsFaceValue);
            _vomsSerialUploadCheckVO.setExpiryDate(_expiryDate);
            _vomsSerialUploadCheckVO.setFileName(_voucherUploadVO.getFileName());
            _vomsSerialUploadCheckVO.setUploadDate(_currentDate);
            _vomsSerialUploadCheckVO.setCreatedBy(_channelUserVO.getUserID());
            _vomsSerialUploadCheckVO.setCreatedOn(_currentDate);

            VomsVoucherDAO vomsVoucherDAO = new VomsVoucherDAO();
            boolean status = vomsVoucherDAO.checkDuplicateSerialNO(p_con, _vomsSerialUploadCheckVO);
            if (status) {
                _fileName = _voucherUploadVO.getFileName();
                String moveLocation = BTSLUtil.NullToString(Constants.getProperty("VOMS_VOUCHER_FILE_MOVE_PATH"));
                VoucherFileUploaderUtil.moveFileToAnotherDirectory(_fileName, _filePath + File.separator + _fileName, moveLocation);
                _log.error(methodName, " The file details already exist in DB");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherFileChecksAirtelBDFormat[populateVomsSerialUploadCheckVO]", "", "", "", "File has been uploaded before");
                throw new BTSLBaseException("VoucherFileChecksAirtelBDFormat", methodName, PretupsErrorCodesI.VOUCHER_ERROR_FILE_ALREADY_UPLOADED);// "The voucher file does not exists at the location specified"
            } else {
                addCount = vomsVoucherDAO.insertFileValues(p_con, _vomsSerialUploadCheckVO);
            }
            if (addCount <= 0) {
                _log.error("VoucherFileChecksAirtelBDFormat[populateVomsSerialUploadCheckVO]", " Not able to update Voucher serial upload check table ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksAirtelBDFormat[populateVomsSerialUploadCheckVO]", "", "", "", " The Voucher serial check table could not be updated");
                throw new BTSLBaseException("VoucherFileChecksAirtelBDFormat ", " populateVomsSerialUploadCheckVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error("VoucherFileChecksAirtelBDFormat[populateVomsSerialUploadCheckVO]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksAirtelBDFormat[populateVomsSerialUploadCheckVO]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksAirtelBDFormat ", " populateVomsSerialUploadCheckVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" populateVomsSerialUploadCheckVO", " Exiting");
            }
        }

        return _vomsSerialUploadCheckVO;
    }

}
