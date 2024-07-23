package com.client.voms.vomsprocesses.businesslogic;

/*
 * @(#)VoucherFileCheckRM.java
 * Name Date History
 * ------------------------------------------------------------------------
 *This class is used for parsing the data uploaded for voucher
 * ------------------------------------------------------------------------
 *
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsRestUtil;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.VoucherUploadErrorLog;
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
import com.univocity.parsers.common.processor.RowListProcessor;

/**
 * @author akanksha
 *Name Date History
 * ------------------------------------------------------------------------
 *This class is used for parsing the data uploaded for voucher in CSV format
 *Date: 28/03/17
 * ------------------------------------------------------------------------
 */
public class VoucherFileCheckRM implements VoucherFileChecksI {
    private String valueSeparator = null; // separater for the file
    private String headerSeparator = null; // separater for header
    private String startingRecord = null;
    private String endingRecord = null;
    private String productID = null; // product ID for the vouchers
    private String filePath = null; // path of the file
    private String fileName = null; // name of file
    private String startSN = null; // start serial number of file
    private String quantity = null; // number of records to be uploaded
    private String networkCode=null;
    private String prfileId = null;
    private String networkID=null;
    private Date expiryDate = null; // expiry date of vouchers
    private List<String[]> rows;
    private long rowSize=0;
    private ArrayList voucherArr = null;
    private ChannelUserVO channelUserVO = null;
    private VoucherUploadVO voucherUploadVO = null;

    private int maxNoRecordsAllowed = 0;
    private int numberOfRecordsEntered = 0;
    private int allowedNumberofErrors = 0;
    private int numberofvaluesinheader = 0;
    private int runningErrorCount = 0;
    private long numberOfRecordScheduled = 0;
    private ArrayList errorList = null; // used to save error information for
                                         // fututre use

    // file format
    private static final String LINE_HEADER_1 = "NETWORK_CODE";
    private static final String LINE_HEADER_2 = "QUANTITY";
    private static final String LINE_HEADER_3 = "START SN";
    private static final String LINE_HEADER_4 = "PROFILE";
    private static final String CLASS_NAME="VoucherFileCheckRM";
    private static Log log = LogFactory.getLog(VoucherFileCheckRM.class.getName());

    /**
     * The method loads the values for the various properties to be used later
     * during the parsing and validation of the file
     * 
     * @return void
     * @throws BTSLBaseException
     */
    
    @Override
    public void loadConstantValues() throws BTSLBaseException {
        final String methodName = "loadConstantValues";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Entered the loadContantsValues.....");
        }
        try {

            try {
                allowedNumberofErrors = Integer.parseInt(Constants.getProperty("VOMS_TOTAL_ALLOWED_ERRORS"));
                if (log.isDebugEnabled()) {
                    log.debug(CLASS_NAME+" "+methodName, "  VOMS_TOTAL_ALLOWED_ERRORS " + allowedNumberofErrors);
                }

            } catch (Exception e) {
                log.errorTrace(methodName, e);
                allowedNumberofErrors = 0;
                log.info(methodName, " Total number of error (Entry VOMS_TOTAL_ALLOWED_ERRORS) not found in Constants . Thus taking default values as 0");
            }
            try {
                numberofvaluesinheader = Integer.parseInt(Constants.getProperty("NUMBER_OF_VALUES_IN_HEADER"));
                if (log.isDebugEnabled()) {
                    log.debug(CLASS_NAME+" "+methodName, "  NUMBER_OF_VALUES_IN_HEADER " + numberofvaluesinheader);
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                log.error(methodName, " Invalid value for NUMBER_OF_VALUES_IN_HEADER  in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, CLASS_NAME+"["+methodName+"]", "", "", "", "Invalid value for NUMBER_OF_VALUES_IN_HEADER in Constant File while voucher upload process");
                throw new BTSLBaseException(CLASS_NAME+" "+methodName," ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                valueSeparator = Constants.getProperty("VOMS_FILE_SEPARATOR");
                if (log.isDebugEnabled()) {
                    log.debug(CLASS_NAME+" "+methodName,"   VOMS_FILE_SEPARATOR " + valueSeparator);
                }
                if (BTSLUtil.isNullString(valueSeparator)) {
                    log.error(methodName, " Invalid value for VOMS_FILE_SEPARATOR  in Constant File ");
                    valueSeparator = " ";
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                log.error(methodName, " Invalid value for VOMS_FILE_SEPARATOR  in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, CLASS_NAME+"["+methodName+"]", "", "", "", "Invalid value for VOMS_FILE_SEPARATOR in Constant File while voucher upload process");
                throw new BTSLBaseException(CLASS_NAME+" "+methodName," ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                headerSeparator = Constants.getProperty("VOMS_FILE_HEADER_SEPARATOR");
                if (log.isDebugEnabled()) {
                    log.debug(CLASS_NAME+" "+methodName,"   VOMS_FILE_HEADER_SEPARATOR " + headerSeparator);
                }
                if (BTSLUtil.isNullString(headerSeparator)) {
                    log.error(methodName, " Invalid value for VOMS_FILE_HEADER_SEPARATOR  in Constant File ");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, CLASS_NAME+"["+methodName+"]", "", "", "", "Invalid value for VOMS_FILE_HEADER_SEPARATOR in Constant File while voucher upload process");
                    throw new BTSLBaseException(CLASS_NAME+" "+methodName," ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                log.error(methodName, " Invalid value for VOMS_FILE_HEADER_SEPARATOR  in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, CLASS_NAME+"["+methodName+"]", "", "", "", "Invalid value for VOMS_FILE_HEADER_SEPARATOR in Constant File while voucher upload process");
                throw new BTSLBaseException(CLASS_NAME+" "+methodName," ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                numberOfRecordScheduled = Long.parseLong(Constants.getProperty("VOMS_MAX_FILE_LENGTH"));
                if (log.isDebugEnabled()) {
                    log.debug(CLASS_NAME+" "+methodName,"    NUMBER_OF_RECORDS_SCHEDULE " + numberOfRecordScheduled);
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                log.error(methodName, "Invalid value for REC_ALLOWED_FOR_SCRIPT in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, CLASS_NAME+"["+methodName+"]", "", "", "", "Invalid value for REC_ALLOWED_FOR_SCRIPT in Constant File ");
                throw new BTSLBaseException(CLASS_NAME+" "+methodName," ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
        }// end of try block
        catch (BTSLBaseException be) {
            log.errorTrace(methodName, be);
            log.error(methodName, " BTSLBaseException be = " + be);
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            log.errorTrace(methodName, e);
            log.error(methodName, " Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME+"["+methodName+"]", "", "", "", "Exception while loading the constant values from the Constants.prop file " + e.getMessage());
            throw new BTSLBaseException(CLASS_NAME+" "+methodName," ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of catch-Exception
        finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "   Exiting with _allowedNumberofErrors=" + allowedNumberofErrors + " _valueSeparator=" + valueSeparator);
            }
        }// end of finally
    }// end of loadConstantValues

    /**
     * This method counts the number of records in the file and simultaneously
     * stores the record in the
     * arraylist for future processing of the records.
     * Akanksha
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
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Entered with p_voucherUploadVO=" + p_voucherUploadVO);
        }
        int lineCount = 0;
        String fileData = null;
        BufferedReader inFile;
        try {
            getValuesFromVO(p_voucherUploadVO);
            String srcFile;
            // creates the actual path of the file
            if (BTSLUtil.isNullString(filePath)) {
                srcFile = fileName;
            } else {
                srcFile = filePath + File.separator+ fileName;
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Starting processing to get the number of records for source File Path = " + srcFile + " File Name" + fileName);
            }
            
            Integer intObj = new Integer(numberofvaluesinheader);
            RowListProcessor processor1 = PretupsRestUtil.readCsvFileWithLimitedRowNum(srcFile,intObj);
        	List<String[]> headersRows = processor1.getRows();
            p_voucherUploadVO.setNoOfRecordsInFile(readFileHeader(headersRows));
            readRowsFromCsv(methodName, srcFile, intObj);
        	for (String[] row : rows) {
        		 fileData = row[0];// reads line by line
                 // this is used to check if the line is blank ie the record is
                 // blank
                 if(PretupsRestUtil.checkIfEmpty(row)){
                	 log.error("methodName", " Record Number = " + rowSize+1 + " No Data Found");
                     continue;
                	}
                	rowSize++;
                 
                 if (rowSize==1) {
                     String firstSerialNo = fileData;
                     // check starting serial number is equal to first serial
                     // number
                     if (!(firstSerialNo).equals(startSN)) {
                         log.error(methodName, " Start Serial Number  (" + startSN + ") in the File (" + fileName + ") is not equal to first serial Number = " + firstSerialNo);
                         throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.START_SERIALNO_NOT_EQUAL_TO_FIRST_SERIALNO, "uploadvoucher");
                     }
                 }
        		
        	}
        	  // end of for loop
            // this is used to check if the actual number of records in the file
               // and the records
               // entered by the user are same or not
        		if(rowSize==0){
        		EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileCheckRM[checkForInvalidOrNoRecords]", "", "", "", "Uploaded file does not contain any record for serial number or pin");
    			throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.NO_RECORDS_FOUND_IN_VOUCHER_UPLOAD, "uploadvoucher");
        		}
               if (numberOfRecordsEntered != rowSize) {
                   log.error(methodName, " Total Number of Records (" + (rowSize) + ") in the file (" + fileName + ") doesn't match the entered value = " + numberOfRecordsEntered + ". Control Returning ");
                   throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.NO_OF_RECORDS_NOT_EQUALTO_QUANTITY, "uploadvoucher");
               }
               // this is used to check the number of records are greater than
               // 20000
               // if number of records in the file are greater than 20000 than
               // schedule the file for later process
               if (rowSize > numberOfRecordScheduled) {
                   log.error(methodName, " Number of records in the file  (" + rowSize + ") in The File (" + fileName + ") are more than scheduled = (" + numberOfRecordScheduled + ")");
                   throw new BTSLBaseException(CLASS_NAME,methodName, PretupsErrorCodesI.VOUCHER_FILE_MORE_RECORDS);
               }

        }// end of try block
        catch (BTSLBaseException be) {
            log.errorTrace(methodName, be);
            log.error(methodName, " : BTSLBaseException " + be);
            throw be;
        } catch (IOException io) {
            log.errorTrace(methodName, io);
            log.error(methodName, "The file (" + fileName + ")could not be read properly to get the number of records");
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileCheckRM[getFileLength]", "", "", "", "The file (" + fileName + ")could not be read properly to get the number of records");
            throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);
        }// end of IOException
        catch (Exception e) {
            log.errorTrace(methodName, e);
            log.error(methodName, " Exception := " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VocuherFileChecks[getFileLength]", "", "", "", e.getMessage());
            throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);
        }// end of Exception
        finally {
           log.error(methodName, " Exiting getFileLength()");
        }// end of finally
    }// end of getFileLength

	private void readRowsFromCsv(final String methodName, String srcFile,
			Integer intObj) throws BTSLBaseException {
		RowListProcessor processor;
		try{
		processor = PretupsRestUtil.readCsvFile(srcFile,intObj);
		}
		catch(BTSLBaseException ex){
			log.errorTrace(methodName, ex);
			LogFactory.printLog(methodName, ex.getMessage(), log);
			EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileCheckRM[checkForInvalidOrNoRecords]", "", "", "", "Uploaded file does not contain any record for serial number or pin");
			throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.NO_RECORDS_FOUND_IN_VOUCHER_UPLOAD, "uploadvoucher");
		}
		catch(Exception ex){
			log.errorTrace(methodName, ex);
			LogFactory.printLog(methodName, ex.getMessage(), log);
			EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileCheckRM[checkForInvalidOrNoRecords]", "", "", "", "Uploaded file does not contain any record for serial number or pin");
			throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.NO_RECORDS_FOUND_IN_VOUCHER_UPLOAD, "uploadvoucher");
		}
		rows = processor.getRows();
	}

    /**
     * This method is used to parse the header
     * and it also validate the header values
     * Akanksha
     * @param p_inFile
     * @throws Exception
     */
    private int readFileHeader(List<String[]> rows) throws BTSLBaseException, IOException {
        final String methodName = "readFileHeader";
        if (log.isDebugEnabled()) {
            log.debug(" readFileHeader", " Entered to validate the header of _fileName=" + fileName);
        }
        Connection con;
        errorList = new ArrayList();
        try {
        	
        	//Akanksha
        	
        	String fileData;
            String batchValue;
            Date currentDate = new Date();
            int validHeaderCount = 0;
            int counter = 0;

            
        	for (String[] row : rows) {
					fileData = row[0];
                fileData = fileData.toUpperCase();
                 batchValue = null;
                counter++;
                
                // extract the Network Code
                if (counter == 1) {
                    if (fileData.indexOf(LINE_HEADER_1) == -1)// means not found
                    {
                        errorList.add("Information for " + LINE_HEADER_1 + " Not found");
                        runningErrorCount++;
                        VoucherUploadErrorLog.logGenMsg(fileName, "Header name " + LINE_HEADER_1 + " Not found");
                    } else {
                    	if ("Y".equalsIgnoreCase(voucherUploadVO.getRunningFromCron())) {
                    		networkID=row[1];
                    		if (BTSLUtil.isNullString(networkID)) {
                                errorList.add("Information for " + LINE_HEADER_1 + " Not found");
                                VoucherUploadErrorLog.logGenMsg(fileName, "Information for " + LINE_HEADER_1 + " Not found");
                                runningErrorCount++;
                            } 
                   	 }
                    		validHeaderCount++;
                            continue;
                    }
                }
                
                // extract the Quantity
                if (counter == 2) {
                    if (fileData.indexOf(LINE_HEADER_2) == -1)// means not found
                    {
                        errorList.add("Information for " + LINE_HEADER_2 + " Not found");
                        VoucherUploadErrorLog.logGenMsg(fileName, "Header name " + LINE_HEADER_2 + " Not found");
                        runningErrorCount++;
                    } else {
                        batchValue =row[1] ;
                        if (BTSLUtil.isNullString(batchValue)) {
                            errorList.add("Information for " + LINE_HEADER_2 + " Not found");
                            VoucherUploadErrorLog.logGenMsg(fileName, "Information for " + LINE_HEADER_2 + " Not found");
                            runningErrorCount++;
                        } else {
                            quantity = batchValue.trim();
                            if (!BTSLUtil.isNumeric(quantity)) {
                                log.error(" readFileHeader", " Invalid Quantity");
                                throw new BTSLBaseException(CLASS_NAME, "readFileHeader", PretupsErrorCodesI.ERROR_INVALID_PRODUCT_QUANTITY, "uploadvoucher");
                            }
                            validHeaderCount++;
                            continue;
                        }
                    }
                }
                
                // extract the Start Serial number
                if (counter == 3) {
                    if (fileData.indexOf(LINE_HEADER_3) == -1)// means not found
                    {
                        errorList.add("Information for " + LINE_HEADER_3 + " Not found");
                        VoucherUploadErrorLog.logGenMsg(fileName, "Header Name " + LINE_HEADER_3 + " Not found");
                        runningErrorCount++;
                    } else {
                        batchValue =row[1];
                        if (BTSLUtil.isNullString(batchValue)) {
                            errorList.add("Information for " + LINE_HEADER_3 + " Not found");
                            VoucherUploadErrorLog.logGenMsg(fileName, "Information for " + LINE_HEADER_3 + " Not found");
                            runningErrorCount++;
                        } else {
                            startSN = batchValue.trim();
                            validHeaderCount++;
                            continue;
                        }
                    }
                }
                
                	 if (counter == 4) {

                         if (fileData.indexOf(LINE_HEADER_4) == -1)// means not
                                                                   // found
                         {
                             errorList.add("Information for " + LINE_HEADER_4 + " Not found");
                             VoucherUploadErrorLog.logGenMsg(fileName, "Header Name " + LINE_HEADER_4 + " Not found");
                             runningErrorCount++;
                         } 
                         else {
                        	 if ("Y".equalsIgnoreCase(voucherUploadVO.getRunningFromCron())) {
                        		 prfileId=row[1]; 
                        		 if (BTSLUtil.isNullString(prfileId)) {
                                     errorList.add("Information for " + LINE_HEADER_4 + " Not found");
                                     VoucherUploadErrorLog.logGenMsg(fileName, "Information for " + LINE_HEADER_4 + " Not found");
                                     runningErrorCount++;
                                 } 
                        	 }
                        	 validHeaderCount++;
                        	 continue;
                         }
                	 
                	 } 
                 }
            if (validHeaderCount != numberofvaluesinheader) {
                log.error("readFileHeader", " Total Number of values in the header is not equal to =" + numberofvaluesinheader);
                throw new BTSLBaseException(CLASS_NAME, "readFileHeader", PretupsErrorCodesI.HEADER_FIELDS_NOT_EQUALTO_DEFINED_VALUE, "uploadvoucher");
            }
            try {
                numberOfRecordsEntered = Integer.parseInt(quantity);
                if (VoucherUploadVO._MANUALPROCESSTYPE.equalsIgnoreCase(voucherUploadVO.getProcessType()) && numberOfRecordsEntered != voucherUploadVO.getNoOfRecordsInFile()) {
                    log.error("readFileHeader", " Total number of records in file header (" + numberOfRecordsEntered + ") is not equal to the file length entered by user (" + voucherUploadVO.getNoOfRecordsInFile() + ")");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherFileProcessor[readFileHeader]", "", "", "", "Total number of records in file header (" + numberOfRecordsEntered + ") is not equal to the file length entered by user (" + voucherUploadVO.getNoOfRecordsInFile() + ")");
                    throw new BTSLBaseException(CLASS_NAME, "readFileHeader", PretupsErrorCodesI.VOUCHER_ERROR_INVALID_RECORD_COUNT);// "The voucher file does not exists at the location specified"
                }
                // check number of records entered are less than maximum number
                // of records allowed
                if (maxNoRecordsAllowed < numberOfRecordsEntered) {
                    log.error("readFileHeader", "Total number of records entered (" + numberOfRecordsEntered + ") should be less than the allowed records in file (" + maxNoRecordsAllowed + ")");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherFileProcessor[readFileHeader]", "", "", "", "Total number of records entered (" + numberOfRecordsEntered + ") should be less than the allowed records in voucher file (" + maxNoRecordsAllowed + ")");
                    throw new BTSLBaseException(CLASS_NAME, "readFileHeader", PretupsErrorCodesI.VOUCHER_ERROR_NUMBER_REC_MORE_THAN_ALLWD, "uploadvoucher");// "The voucher file does not exists at the location specified"
                }
            }// end of try block
            catch (NumberFormatException nfe) {
                log.errorTrace(methodName, nfe);
                log.error("readFileHeader", " Quantity field in the header should be Numeric");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileCheckRM[readFileHeader]", "", "", "", " Quantity field in the header should be integer");
                throw new BTSLBaseException(CLASS_NAME, "readFileHeader", PretupsErrorCodesI.VOUCHER_FILE_HEADER_QUANTITY_ERROR);
            }
            voucherUploadVO.setNoOfRecordsInFile(numberOfRecordsEntered);
            voucherUploadVO.setPrfileId(prfileId);
            voucherUploadVO.setNetwrkID(networkID);

        } catch (BTSLBaseException be) {
            log.errorTrace(methodName, be);
            log.error(" readFileHeader", " Exception = " + be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChechsUmniah[readFileHeader]", "", "", "", be.getMessage());
            throw be;
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            log.error(" readFileHeader", " Exception = " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileChechsUmniah[readFileHeader]", "", "", "", e.getMessage());
            throw new BTSLBaseException(CLASS_NAME, "readFileHeader", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(" readFileHeader", " Exiting _numberOfRecordsEntered=" + numberOfRecordsEntered);
            }
        }// end of Exception
        return numberOfRecordsEntered;
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
        final String methodName = "validateVoucherFile";
        if (log.isDebugEnabled()) {
            log.debug(" validateVoucherFile", " Entered to validate the file _fileName=" + fileName);
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
            voucherArr = new ArrayList();
            minSerialLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MIN_LENGTH))).intValue();
            maxSerialLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH))).intValue();
            minPinLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MIN_LENGTH))).intValue();
            maxPinLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MAX_LENGTH))).intValue();
            for (String[] row : rows) {
            	_runningFileRecordCount++;// keeps record of number of records
                // in file
            	if (row.length == 1) {
                    log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " No Data Found");
                    runningErrorCount++;
                    errorList.add(" Record Number = " + _runningFileRecordCount + " No Data Found");
                    continue;
                }
                if (row.length < 2) {
                    log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + "Number of values in the record are not correct");
                    runningErrorCount++;
                    errorList.add("Record Number = " + _runningFileRecordCount + "Number of values in the record are not correct");
                    VoucherUploadErrorLog.logGenMsg(fileName, "Number of values in the record are not correct");
                    continue;
                }
                
             // checks if the number of error encontered while parsing of
                // file does not
                // exceed the user given error count limit
                if (runningErrorCount > allowedNumberofErrors) {
                    log.error("validateVoucherFile", " Total Number of error (" + runningErrorCount + ") in the File (" + fileName + ") exceeds the user specified error number= " + allowedNumberofErrors);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileCheckRM[validateVoucherFile]", "", "", "", "The number of error (" + runningErrorCount + ") in the file exceed the user specified value (" + allowedNumberofErrors + ")");
                    throw new BTSLBaseException(CLASS_NAME, "validateVoucherFile", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT, "uploadvoucher");// "Total number of error in the file exceed the specified number of error"
                }
                
                
                dataValueSerial = row[0];
                    if (BTSLUtil.isNullString(dataValueSerial)) {
                        log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Serial Number Not Valid");
                        runningErrorCount++;
                        errorList.add(" Record Number = " + _runningFileRecordCount + " Serial Number Not Valid");
                        VoucherUploadErrorLog.logGenMsg(fileName, " Line Number = " + (_runningFileRecordCount+numberofvaluesinheader) + " Serial Number is empty");
                        continue;
                    } else if (!VoucherFileUploaderUtil.isValidDataLength(dataValueSerial.length(), minSerialLength, maxSerialLength)) {
                        log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Serial Number Length Not Valid");
                        runningErrorCount++;
                        errorList.add(" Record Number = " + _runningFileRecordCount + " Serial Number Length Not Valid");
                        VoucherUploadErrorLog.logGenMsg(fileName, " Line Number = " + (_runningFileRecordCount+numberofvaluesinheader) + " Serial Number Length Not Valid");
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
                        log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Serial Numbers are not Continous");
                        errorList.add(" Record Number = " + _runningFileRecordCount + " Serial Numbers are not Continous");
                        VoucherUploadErrorLog.logGenMsg(fileName, " Line Number = " + (_runningFileRecordCount+numberofvaluesinheader) + " Serial Numbers are not Continous");
                        continue;
                    }
                    previousSerialNo++;
                }

                
                
                dataValuePIN = row[1];
              
                    if (BTSLUtil.isNullString(dataValuePIN)) {
                        log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " PIN Not Valid");
                        runningErrorCount++;
                        errorList.add(" Record Number = " + _runningFileRecordCount + " PIN Not Valid");
                        VoucherUploadErrorLog.logGenMsg(fileName, " Line Number = " + (_runningFileRecordCount+numberofvaluesinheader) + "  PIN is empty");
                        continue;
                    }
                    // Above we have validated the pin length in the header with
                    // system preference
                    // so here just validating, pin length against header pin
                    // length
                    else if (!VoucherFileUploaderUtil.isValidDataLength(dataValuePIN.length(), minPinLength, maxPinLength)) {
                        log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " PIN Length Not Valid");
                        errorList.add(" Record Number = " + _runningFileRecordCount + " PIN Length Not Valid");
                        VoucherUploadErrorLog.logGenMsg(fileName, " Line Number = " + (_runningFileRecordCount+numberofvaluesinheader) + "  PIN length not valid");
                        runningErrorCount++;
                        continue;
                    }
                
                String finalPin = dataValuePIN;

                // this condition checks for the uniqueness of the pin in the
                // file. Adds the pin in the
                // arraylist pinList and before adding next time searches in
                // this list first
      
                    if (pinList.isEmpty()) {
                        pinList.put(finalPin, finalPin);
                        beforeSize = pinList.size();
                    } else {
                        pinList.put(finalPin, finalPin);
                        if (pinList.size() == beforeSize) {
                            log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " PIN Not Unique");
                            runningErrorCount++;
                            VoucherUploadErrorLog.logGenMsg(fileName, " Line Number = " + (_runningFileRecordCount+numberofvaluesinheader) + "  PIN Not Unique");
                            errorList.add(" Record Number = " + _runningFileRecordCount + " PIN Not Unique");
                            continue;
                        }
                        beforeSize = pinList.size();
                    }
              
                try {
                    encryptedPin = VomsUtil.encryptText(finalPin);
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                    log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " Not able to encrypt PIN");
                    runningErrorCount++;
                    errorList.add(" Record Number = " + _runningFileRecordCount + " Not able to encrypt PIN");
                    VoucherUploadErrorLog.logGenMsg(fileName, " Line Number = " + (_runningFileRecordCount+numberofvaluesinheader) + "  Not able to encrypt pin");
                    continue;
                }

                if (_runningFileRecordCount == 1) {
                    startingRecord = dataValueSerial;// set only for the first
                }
                // time
                endingRecord = dataValueSerial;
                populateValuesInVoucherList(dataValueSerial, encryptedPin, productID, expiryDate);
                
            }
          
             // check running error count is less than allowed error count
            if (runningErrorCount > allowedNumberofErrors) {
                log.error("validateVoucherFile", " Total Number of error (" + runningErrorCount + ") in the File (" + fileName + ") exceeds the user specified error number= " + allowedNumberofErrors);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileCheckRM[validateVoucherFile]", "", "", "", "The number of error (" + runningErrorCount + ") in the file exceed the user specified value (" + allowedNumberofErrors + ")");
                throw new BTSLBaseException(CLASS_NAME, "validateVoucherFile", PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT, "uploadvoucher");// "Total number of error in the file exceed the specified number of error"
            }// end of if block
             // counts the number of actual valid records in the file
            voucherUploadVO.setFromSerialNo(startingRecord);
            voucherUploadVO.setToSerialNo(endingRecord);
            voucherUploadVO.setActualNoOfRecords(voucherArr.size());
            voucherUploadVO.setVoucherArrayList(voucherArr);
            voucherUploadVO.setErrorArrayList(errorList);
        } catch (BTSLBaseException be) {
            log.errorTrace(methodName, be);
            log.error("validateVoucherFile", " : BTSLBaseException " + be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileCheckRM[validateVoucherFile]", "", "", "", be.getMessage());
            throw be;
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            log.error(" validateVoucherFile", " Exception = " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileCheckRM[validateVoucherFile]", "", "", "", e.getMessage());
            throw new BTSLBaseException(CLASS_NAME, "validateVoucherFile", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(" validateVoucherFile", " Exiting ");
            }
        }
        return voucherUploadVO;
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
        if (log.isDebugEnabled()) {
            log.debug(" populateValuesInVoucherList", " Entered with p_serialNo=" + p_serialNo + " p_pin=" + p_pin + " p_productID=" + p_productID);
        }
        try {
            VomsVoucherVO vomsVoucherVO = new VomsVoucherVO();
            vomsVoucherVO.setSerialNo(p_serialNo);
            vomsVoucherVO.setPinNo(p_pin);
            voucherArr.add(vomsVoucherVO);
        } catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("VoucherFileCheckRM[populateValuesInVoucherList]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileCheckRM[populateValuesInVoucherList]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException(CLASS_NAME, " populateValuesInVoucherList ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(" populateValuesInVoucherList", " Exiting ");
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
        final String methodName = "getValuesFromVO";
        if (log.isDebugEnabled()) {
            log.debug(" getValuesFromVO", " Entered with  p_voucherUploadVO=" + p_voucherUploadVO);
        }
        try {
            fileName = p_voucherUploadVO.getFileName();
            filePath = p_voucherUploadVO.getFilePath();
            numberOfRecordsEntered = p_voucherUploadVO.getNoOfRecordsInFile();
            maxNoRecordsAllowed = p_voucherUploadVO.getMaxNoOfRecordsAllowed();
            channelUserVO = p_voucherUploadVO.getChannelUserVO();
            voucherUploadVO = p_voucherUploadVO;
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            log.error(CLASS_NAME+"["+methodName+"]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileCheckRM[getValuesFromVO]", "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException(CLASS_NAME, " getValuesFromVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(" getValuesFromVO", " Exiting");
            }
        }
    }// end of getValuesFromVO

    @Override
    public VomsSerialUploadCheckVO populateVomsSerialUploadCheckVO(Connection con) throws BTSLBaseException, Exception {
        return null;
    }
    
    
    /**
     * @param rows
     * @param emptyRows
     * @throws BTSLBaseException
     */
    public void checkForInvalidOrNoRecords(List<String[]> rows,
			int emptyRows) throws BTSLBaseException {
		String methodName = "checkForInvalidOrNoRecords";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try{
			if (rows.size() == emptyRows) {
				 log.error(methodName, "Uploaded file does not contain any record ");
	              EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileCheckRM[checkForInvalidOrNoRecords]", "", "", "", "Uploaded file does not contain any record");
	              throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.NO_RECORDS_FOUND_IN_VOUCHER_UPLOAD, "uploadvoucher");
			}
		}finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}
    
    /**
     * @param rows
     * @return
     */
    public int getEmptryRow(List<String[]> rows) {
		int emptyRows = 0;
		for (String[] strings : rows) {
			if (strings.length == 0) {
				emptyRows++;
			}
		}
		return emptyRows;
	}
}