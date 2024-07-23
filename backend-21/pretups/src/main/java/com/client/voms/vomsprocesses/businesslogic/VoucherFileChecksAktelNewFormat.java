/*
 * Created on Jun 24, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.client.voms.vomsprocesses.businesslogic;

/*
 * @(#)VoucherFileChecksAktelNewFormat.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Sanjeev Sharma 4/07/06 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2009 comviva Telesoft Ltd.
 * New Parser class for voucher file processing
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class VoucherFileChecksAktelNewFormat implements VoucherFileChecksI {
    private final static String FILE_VALUE_SEPARATOR = " ";
    private String _vomsfiledateformat = null;
    private int _numberOfRecordsEntered = 0;
    private int _pinLength = 16;
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
    private Date _expiryDate = null;
    private String _venderName = null;
    private int _vomsFaceValue = 0;
    private final static String HEADER_VALUE_SEPARATOR = ":";

    private final static int FILE_NAME_LENGTH = 47;
    private VomsSerialUploadCheckVO _vomsSerialUploadCheckVO = null;

    private static Log _log = LogFactory.getLog(VoucherFileChecksAktelNewFormat.class.getName());

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
                    _log.debug("VoucherFileChecksAktelNewFormat", " loadConstantValues   VOUCHER_EXPIRY_DATE_FORMAT " + _vomsfiledateformat);
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" loadConstantValues ", "Invalid value for VOUCHER_EXPIRY_DATE_FORMAT in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksAktelNewFormat[loadConstantValues]", "", "", "", "Invalid value for VOUCHER_EXPIRY_DATE_FORMAT in Constant File ");
                throw new BTSLBaseException("VoucherFileChecksAktelNewFormat ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                _allowedNumberofErrors = Integer.parseInt(Constants.getProperty("VOMS_TOTAL_ALLOWED_ERRORS"));
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksAktelNewFormat ", " loadConstantValues   VOMS_TOTAL_ALLOWED_ERRORS " + _allowedNumberofErrors);
                }

            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _allowedNumberofErrors = 0;
                _log.info("loadConstantValues", " Total number of error (Entry VOMS_TOTAL_ALLOWED_ERRORS) not found in Constants . Thus taking default values as 0");
            }
            try {
                _numberofvaluesinheader = Integer.parseInt(Constants.getProperty("NUMBER_OF_VALUES_IN_HEADER"));
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksAktelNewFormat ", " loadConstantValues  NUMBER_OF_VALUES_IN_HEADER " + _numberofvaluesinheader);
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" loadConstantValues", " Invalid value for NUMBER_OF_VALUES_IN_HEADER  in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksAktelNewFormat[loadConstantValues]", "", "", "", "Invalid value for NUMBER_OF_VALUES_IN_HEADER in Constant File while voucher upload process");
                throw new BTSLBaseException("VoucherFileChecksAktelNewFormat ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                // _valueSeparator =
                // Constants.getProperty("VOMS_FILE_SEPARATOR");
                if (_log.isDebugEnabled()) {
                    _log.debug("VoucherFileChecksAktelNewFormat ", " loadConstantValues   VOMS_FILE_SEPARATOR " + FILE_VALUE_SEPARATOR);
                }
                if (FILE_VALUE_SEPARATOR.length() != 1) {
                    _log.error(" loadConstantValues", " Invalid value for VOMS_FILE_SEPARATOR  in Constant File ");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksAktelNewFormat[loadConstantValues]", "", "", "", "Invalid value for VOMS_FILE_SEPARATOR in Constant File while voucher upload process");
                    throw new BTSLBaseException("VoucherFileChecksAktelNewFormat ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);

                }

            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(" loadConstantValues", " Invalid value for VOMS_FILE_SEPARATOR  in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksAktelNewFormat[loadConstantValues]", "", "", "", "Invalid value for VOMS_FILE_SEPARATOR in Constant File while voucher upload process");
                throw new BTSLBaseException("VoucherFileChecksAktelNewFormat ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksAktelNewFormat[loadConstantValues]", "", "", "", "Exception while loading the constant values from the Constants.prop file " + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksAktelNewFormat ", " loadConstantValues ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" loadConstantValues", "   Exiting with _allowedNumberofErrors=" + _allowedNumberofErrors + " _valueSeparator=" + FILE_VALUE_SEPARATOR);
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
        String[] fileRecord = null;
        int headerRecordNumber = 0;

        String[] fileNameArray = null;
        String[] fileType = null;

        HashMap h_map = new HashMap();
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

            while (!"[BEGIN]".equalsIgnoreCase(fileData = inFile.readLine())) {
                headerRecordNumber = 1;
                fileRecord = fileData.split(HEADER_VALUE_SEPARATOR);
                if (fileRecord.length == 2) {
                    h_map.put(fileRecord[0], fileRecord[1]);
                } else {
                    _log.error("validateVoucherFile", " Record in Header at line number = " + headerRecordNumber + " is not valid");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktelNewFormat[validateVoucherFile]", "", "", "", "Record in Header at line number = " + headerRecordNumber + " is not valid");
                    throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_FILE_HEADER_ERROR);
                }
            }

            readFileHeader(h_map, p_voucherUploadVO);
            fileData = null;

            /*
             * Validate file name according to new format
             * <OperatorID><0000rr><Under Score><Under Score><mkcard><Under
             * Score><TaskID><Under Score><BatchID><dot><Format name>
             * Ex:- TMIB____0000rr__mkcard_U200811120010_000000.txt
             * Length of this file name string is 48
             * Ignore first 8 characters by using substring(8);
             * 
             * fileName[0] = 0000rr Length ::6
             * fileName[1] = Length ::0
             * fileName[2] = mkcard Length ::6
             * fileName[3] = U200811120010 Length ::13
             * fileName[4] = 000000.txt Length ::10
             */

            if (FILE_NAME_LENGTH == _fileName.length()) {
                _fileName = _fileName.substring(8); // ignore OperatorID of
                                                    // length 8.
                fileNameArray = _fileName.split("_");
                if (!("0000rr".equalsIgnoreCase(fileNameArray[0]))) {
                    _log.error("validateVoucherFile", "File name [" + _fileName + "] must have [0000rr] after [OperatorID] like <OperatorID><0000rr><Under Score><Under Score><mkcard><Under Score><TaskID><Under Score><BatchID><dot><Format name>");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktelNewFormat[validateVoucherFile]", "", "", "", "File name [" + _fileName + "] must have [0000rr] after [OperatorID] like <OperatorID><0000rr><Under Score><Under Score><mkcard><Under Score><TaskID><Under Score><BatchID><dot><Format name>");
                    throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_FILE_NAME_ERROR);
                }
                if (fileNameArray[1].length() != 0) {
                    _log.error("validateVoucherFile", "File name [" + _fileName + "] must have [__] after [0000rr] like <OperatorID><0000rr><Under Score><Under Score><mkcard><Under Score><TaskID><Under Score><BatchID><dot><Format name>");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktelNewFormat[validateVoucherFile]", "", "", "", "File name [" + _fileName + "] must have [__] after [0000rr] like <OperatorID><0000rr><Under Score><Under Score><mkcard><Under Score><TaskID><Under Score><BatchID><dot><Format name>");
                    throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_FILE_NAME_ERROR);
                }
                if (!("mkcard".equalsIgnoreCase(fileNameArray[2]))) {
                    _log.error("validateVoucherFile", "File name [" + _fileName + "] must have [mkcard] in file name like <OperatorID><0000rr><Under Score><Under Score><mkcard><Under Score><TaskID><Under Score><BatchID><dot><Format name>");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktelNewFormat[validateVoucherFile]", "", "", "", "File name [" + _fileName + "] must have [mkcard] in file name like <OperatorID><0000rr><Under Score><Under Score><mkcard><Under Score><TaskID><Under Score><BatchID><dot><Format name>");
                    throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_FILE_NAME_ERROR);
                }
                if (fileNameArray[3].length() != 13 && !fileNameArray[3].startsWith("U")) {
                    _log.error("validateVoucherFile", "File name [" + _fileName + "] must be like <OperatorID><0000rr><Under Score><Under Score><mkcard><Under Score><TaskID><Under Score><BatchID><dot><Format name>");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktelNewFormat[validateVoucherFile]", "", "", "", "File name [" + _fileName + "] must be like <OperatorID><0000rr><Under Score><Under Score><mkcard><Under Score><TaskID><Under Score><BatchID><dot><Format name>");
                    throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_FILE_NAME_ERROR);
                }

                fileType = (fileNameArray[4].split("\\."));

                if (fileType.length != 2) {
                    _log.error("validateVoucherFile", "File must be a text file having .txt extension");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktelNewFormat[validateVoucherFile]", "", "", "", "File name [" + _fileName + "] must be a text file having .txt extension");
                    throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_FILE_NAME_ERROR);
                }

                if (fileNameArray[4].length() != 10) {
                    _log.error("validateVoucherFile", "File name [" + _fileName + "] must end with [<BatchID><dot><Format name>] i.e. [" + _venderName + ".txt]");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktelNewFormat[validateVoucherFile]", "", "", "", "File name [" + _fileName + "] must end with [<BatchID><dot><Format name>] i.e. [" + _venderName + ".txt]");
                    throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_FILE_NAME_ERROR);
                }

                if (_venderName.length() != 6) {
                    _log.error("validateVoucherFile", "Batch ID [" + _venderName + "] must be of length 6 in File [" + _fileName + "]");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktelNewFormat[validateVoucherFile]", "", "", "", "Batch ID [" + _venderName + "] must be of length 6 in File [" + _fileName + "]");
                    throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_FILE_NAME_ERROR);
                }

                if (!fileType[0].equalsIgnoreCase(_venderName)) {
                    _log.error("validateVoucherFile", "File name [" + _fileName + "] must end with [<BatchID><dot><Format name>] i.e. [" + _venderName + ".txt]");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktelNewFormat[validateVoucherFile]", "", "", "", "File name [" + _fileName + "] must end with [<BatchID><dot><Format name>] i.e. [" + _venderName + ".txt]");
                    throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_FILE_NAME_ERROR);
                }

                if (!("TXT".equalsIgnoreCase(fileType[1]))) {
                    _log.error("validateVoucherFile", "File must be a text file having .txt extension");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktelNewFormat[validateVoucherFile]", "", "", "", "File name [" + _fileName + "] must be a text file having .txt extension");
                    throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_FILE_NAME_ERROR);
                }
            } else {
                _log.error("validateVoucherFile", "File must be like <OperatorID><0000rr><Under Score><Under Score><mkcard><Under Score><TaskID><Under Score><BatchID><dot><Format name>");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktelNewFormat[validateVoucherFile]", "", "", "", "File name [" + _fileName + "] must be like <OperatorID><0000rr><Under Score><Under Score><mkcard><Under Score><TaskID><Under Score><BatchID><dot><Format name>");
                throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_FILE_NAME_ERROR);

            }

            while (inFile.ready() && !"[END]".equalsIgnoreCase((fileData = inFile.readLine()))) {
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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktelNewFormat[getFileLength]", "", "", "", "The number of records (" + (fileLineCount) + ") in the file doesn't match the entered value (" + _numberOfRecordsEntered + ")");
                throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "getFileLength", PretupsErrorCodesI.VOUCHER_ERROR_INVALID_RECORD_COUNT);// "Total number of records in the file are different from the one specified"
            }
        }// end of try block
        catch (IOException io) {
            _log.errorTrace(METHOD_NAME, io);
            _log.error("getFileLength", "The file (" + _fileName + ")could not be read properly to get the number of records");
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktelNewFormat[getFileLength]", "", "", "", "The file (" + _fileName + ")could not be read properly to get the number of records");
            throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "getFileLength", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);
        }// end of IOException
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(" getFileLength", " Exception = " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VocuherFileChecks[getFileLength]", "", "", "", e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "getFileLength", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);
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
     * @param p_voucherUploadVO
     *            TODO
     * @param fileData
     */
    private void readFileHeader(HashMap p_map, VoucherUploadVO p_voucherUploadVO) throws BTSLBaseException {
        final String METHOD_NAME = "readFileHeader";
        if (_log.isDebugEnabled()) {
            _log.debug(" readFileHeader", " Entered to validate the header of _fileName=" + _fileName);
        }

        Connection con = null;MComConnectionI mcomCon = null;
        String _tempExpiryDate;
        Date currentDate;
        String productIDFromQty = null;
        int multiplyFactor = 10000 / ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
        try {

            if (!(p_map.size() == _numberofvaluesinheader)) {
                _log.error("readFileHeader", " Total Number of values in the header is not equal to =" + _numberofvaluesinheader);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktelNewFormat[readFileHeader]", "", "", "", "Total Number of values in the header is not equal to =" + _numberofvaluesinheader);
                throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_FILE_HEADER_ERROR);
            }

            try {
                _numberOfRecordsEntered = Integer.parseInt(p_map.get("Quantity").toString());
                if (VoucherUploadVO._MANUALPROCESSTYPE.equalsIgnoreCase(_voucherUploadVO.getProcessType()) && _numberOfRecordsEntered != _voucherUploadVO.getNoOfRecordsInFile()) {
                    System.out.println(" Total number of records in file header (" + _numberOfRecordsEntered + ") is not equal to the file length entered by user (" + _voucherUploadVO.getNoOfRecordsInFile() + ") .............");
                    _log.error("readFileHeader", " Total number of records in file header (" + _numberOfRecordsEntered + ") is not equal to the file length entered by user (" + _voucherUploadVO.getNoOfRecordsInFile() + ")");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherFileProcessor[getDetailsFromConsole]", "", "", "", "Total number of records in file header (" + _numberOfRecordsEntered + ") is not equal to the file length entered by user (" + _voucherUploadVO.getNoOfRecordsInFile() + ")");
                    throw new BTSLBaseException("VoucherFileProcessor", "getDetailsFromConsole", PretupsErrorCodesI.VOUCHER_ERROR_INVALID_RECORD_COUNT);// "The voucher file does not exists at the location specified"
                }

                if (_maxNoRecordsAllowed < _numberOfRecordsEntered) {
                    System.out.println(" Total number of records entered (" + _numberOfRecordsEntered + ") should be less than the allowed records in file (" + _maxNoRecordsAllowed + ") .............");
                    _log.error("readFileHeader", "Total number of records entered (" + _numberOfRecordsEntered + ") should be less than the allowed records in file (" + _maxNoRecordsAllowed + ")");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherFileProcessor[getDetailsFromConsole]", "", "", "", "Total number of records entered (" + _numberOfRecordsEntered + ") should be less than the allowed records in voucher file (" + _maxNoRecordsAllowed + ")");
                    throw new BTSLBaseException("VoucherFileProcessor", "getDetailsFromConsole", PretupsErrorCodesI.VOUCHER_ERROR_NUMBER_REC_MORE_THAN_ALLWD);// "The voucher file does not exists at the location specified"
                }
                _vomsFaceValue = Integer.parseInt(p_map.get("FaceValue").toString()) / multiplyFactor;
            } catch (NumberFormatException nfe) {
                _log.errorTrace(METHOD_NAME, nfe);
                _log.error("readFileHeader", " Quantity field in the header should be Numeric");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktelNewFormat[readFileHeader]", "", "", "", " Quantity field in the header should be integer");
                throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_FILE_HEADER_QUANTITY_ERROR);
            }

            mcomCon = new MComConnection();con=mcomCon.getConnection();
            productIDFromQty = new VomsVoucherDAO().loadProductIDFromMRP(con, _vomsFaceValue);
            if (BTSLUtil.isNullString(productIDFromQty)) {
                System.out.println("Product ID dose no exist for face value " + _vomsFaceValue);
                _log.error("readFileHeader", "Product ID dose no exist for face value " + _vomsFaceValue);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktelNewFormat[readFileHeader]", "", "", "", "Product ID dose no exist for face value " + _vomsFaceValue);
                throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "readFileHeader", PretupsErrorCodesI.PRODUCT_NOT_EXIST);// "Total number of records in the file are different from the one specified"

            }

            if (!BTSLUtil.isNullString(_productID) && !_productID.equalsIgnoreCase(productIDFromQty)) {
                System.out.println("Product ID given by user from console ::" + _productID + " and in Voucher file header ::" + productIDFromQty + " are not matching) " + ".............");
                _log.error("readFileHeader", "Product ID given by user from console ::" + _productID + " and in Voucher file header ::" + productIDFromQty + " are not matching ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktelNewFormat[readFileHeader]", "", "", "", "Product ID given by user from console ::" + _productID + " and in Voucher file header ::" + productIDFromQty + " are not matching ");
                throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "readFileHeader", PretupsErrorCodesI.PRODUCT_NOT_EXIST);// "Total number of records in the file are different from the one specified"
            }
            p_voucherUploadVO.setProductID(productIDFromQty);
            _voucherUploadVO.setProductID(productIDFromQty);

            _startingRecord = p_map.get("Start_Sequence").toString();

            _tempExpiryDate = p_map.get("StopDate").toString();

            currentDate = new Date();

            if (!BTSLUtil.isNullString(_tempExpiryDate)) {
                try {
                    _expiryDate = BTSLUtil.getDateFromDateString(_tempExpiryDate, _vomsfiledateformat);
                    if (_expiryDate.before(currentDate)) {
                        _log.error("validateVoucherFile", " Expiary date of vouchers is already expired.");
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    _log.error("validateVoucherFile", " Expiry-date Not Valid");
                }
            }

            /*
             * In new request format, vender name is not given. So we are
             * considering Batch as Vendor name.
             * Just for reference.
             */
            _venderName = p_map.get("Batch").toString();

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
            throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "readFileHeader", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" readFileHeader", " Exiting _numberOfRecordsEntered=" + _numberOfRecordsEntered);
            }

            if(mcomCon != null){mcomCon.close("VoucherFileChecksAktelNewFormat#readFileHeader");mcomCon=null;}
        }// end of Exception

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
        long previousSerialNo = 0L;
        long presentSerialNo = 0L;
        int runningErrorCount = 0;
        int _runningFileRecordCount = 0;
        String fileData = null;
        ArrayList pinList = new ArrayList();
        String encryptedPin = null;
        String[] fileRecord = null;
        boolean firstOnly = true;
        int minSerialLength = 0;
        int maxSerialLength = 0;

        try {
            _voucherArr = new ArrayList();
            minSerialLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MIN_LENGTH))).intValue();
            maxSerialLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH))).intValue();
            int fileDataArrSize = _fileDataArr.size();
            for (int loop = 0; loop < fileDataArrSize; loop++) {
                fileData = null;
                fileData = (String) _fileDataArr.get(loop);
                fileRecord = fileData.split(FILE_VALUE_SEPARATOR);
                if (_log.isDebugEnabled()) {
                    _log.debug(" validateVoucherFile", " Entered ::" + "fileData ::" + fileData + " fileRecord ::" + fileRecord);
                }

                _runningFileRecordCount++;// keeps record of number of records
                                          // in file

                if (fileRecord.length == 0) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " No Data Found");
                    runningErrorCount++;
                    continue;
                }
                if (fileRecord.length != 2) {
                    _log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + "Number of values in the record are not currect");
                    runningErrorCount++;
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
                    _log.error("validateVoucherFile", " Total Number of error (" + runningErrorCount + ") in the File (" + _fileName + ") exceeds the user specified error number= " + _allowedNumberofErrors);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktelNewFormat[validateVoucherFile]", "", "", "", "The number of error (" + runningErrorCount + ") in the file exceed the user specified value (" + _allowedNumberofErrors + ")");
                    throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);// "Total number of error in the file exceed the specified number of error"
                }
                // dataValueSerial=VoucherFileUploaderUtil.extractData(fileData,SERIAL_NO_LABEL,_valueSeparator);

                dataValueSerial = fileRecord[0];
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
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktelNewFormat[validateVoucherFile]", "", "", "", "The serial number (" + presentSerialNo + ") is not consecutive");
                        // break;
                        throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_DUPLICATE_SERIAL_NO_ERROR);
                    }
                    previousSerialNo++;
                }

                // dataValueProfile=VoucherFileUploaderUtil.extractData(fileData,PRODUCT_ID_LABEL,_valueSeparator);
                dataValuePIN = fileRecord[1];
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

                // if the record is valid, prepare the 2D array with the parsed
                // values
                if (_runningFileRecordCount == 1) {
                    if (!_startingRecord.equalsIgnoreCase(dataValueSerial)) {
                        System.out.println("Starting sequence number[" + _startingRecord + "] specified in header is not matching the starting serial number [" + dataValueSerial + "] in file data..............");
                        _log.error("validateVoucherFile", " Serial Number in Record Number = " + _runningFileRecordCount + " is not matching the Starting Serial Numbers given in header data");
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileChecksAktelNewFormat[validateVoucherFile]", "", "", "", "Serial Number in Record Number = " + _runningFileRecordCount + " is not matching the Starting Serial Numbers given in header data");
                        // break;
                        throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_ERROR_HEADER_INFO);
                    }
                    _startingRecord = dataValueSerial;// set only for the first
                                                      // time
                }
                _endingRecord = dataValueSerial;

                populateValuesInVoucherList(dataValueSerial, encryptedPin, _voucherUploadVO.getProductID(), _expiryDate);

            }// end of for loop
             // counts the number of actual valid records in the file

            _voucherUploadVO.setFromSerialNo(_startingRecord);
            _voucherUploadVO.setToSerialNo(_endingRecord);
            _voucherUploadVO.setActualNoOfRecords(_voucherArr.size());
            _voucherUploadVO.setVoucherArrayList(_voucherArr);

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("validateVoucherFile", " : BTSLBaseException " + be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksAktelNewFormat[validateVoucherFile]", "", "", "", be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(" validateVoucherFile", " Exception = " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksAktelNewFormat[validateVoucherFile]", "", "", "", e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "validateVoucherFile", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);

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
            _log.error("VoucherFileChecksAktelNewFormat[populateValuesInVoucherList]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksAktelNewFormat[populateValuesInVoucherList]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksAktelNewFormat ", " populateValuesInVoucherList ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
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
            _log.error("VoucherFileChecksAktelNewFormat[getValuesFromVO]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksAktelNewFormat[getValuesFromVO]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksAktelNewFormat ", " getValuesFromVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" getValuesFromVO", " Exiting");
            }
        }
    }

    public VomsSerialUploadCheckVO populateVomsSerialUploadCheckVO(Connection p_con) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "populateVomsSerialUploadCheckVO";
        int addCount = 0;
        ArrayList voucherList = new ArrayList();
        voucherList = _voucherUploadVO.getVoucherArrayList();
        int length = voucherList.size();
        VomsVoucherVO vomsvoucherVO = (VomsVoucherVO) voucherList.get(0);
        String startSerialNO = vomsvoucherVO.getSerialNo();
        vomsvoucherVO = (VomsVoucherVO) voucherList.get(length - 1);
        String endSerialNO = vomsvoucherVO.getSerialNo();
        if (_log.isDebugEnabled()) {
            _log.debug(" populateVomsSerialUploadCheckVO", " Entered ");
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
                _log.error("populateVomsSerialUploadCheckVO", " The file details already exist in DB");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherFileChecksAktelNewFormat[populateVomsSerialUploadCheckVO]", "", "", "", "File has been uploaded before");
                throw new BTSLBaseException("VoucherFileChecksAktelNewFormat", "populateVomsSerialUploadCheckVO", PretupsErrorCodesI.VOUCHER_ERROR_FILE_ALREADY_UPLOADED);// "The voucher file does not exists at the location specified"
            } else {
                addCount = vomsVoucherDAO.insertFileValues(p_con, _vomsSerialUploadCheckVO);
            }
            if (addCount <= 0) {
                _log.error("VoucherFileChecksAktelNewFormat[populateVomsSerialUploadCheckVO]", " Not able to update Voucher serial upload check table ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileChecksAktelNewFormat[populateVomsSerialUploadCheckVO]", "", "", "", " The Voucher serial check table could not be updated");
                throw new BTSLBaseException("VoucherFileChecksAktelNewFormat ", " populateVomsSerialUploadCheckVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("VoucherFileChecksAktelNewFormat[populateVomsSerialUploadCheckVO]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChecksAktelNewFormat[populateVomsSerialUploadCheckVO]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherFileChecksAktelNewFormat ", " populateVomsSerialUploadCheckVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" populateVomsSerialUploadCheckVO", " Exiting");
            }
        }

        return _vomsSerialUploadCheckVO;
    }
}