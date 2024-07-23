package com.btsl.pretups.processes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.RetSubsMappingVO;
import com.btsl.pretups.logging.AssociateMsisdnFileProcessLog;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/*
 * ActivationMappingReader.java
 * Name Date History
 * ------------------------------------------------------------------------
 * nand.sahu 24/02/2009 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright(c) 2008, Bharti Telesoft Ltd.
 * This class reads the Subscriber Retailer mapping for Activation bonus.
 */
public class ActivationMappingReader {
    private static Log _logger = LogFactory.getLog(ActivationMappingReader.class.getName());
    private int _recordsCountInFile = 0;

    /**
     * Read Retailer Subscriber Mapping details from Application server at
     * predefined path.
     * 
     * @author nand.sahu
     * @return _activatinMappingList
     */
    public void getActivationMappingList(File _fileObject, java.util.HashMap<String, RetSubsMappingVO> p_activationMappingMap, java.util.HashMap<String, RetSubsMappingVO> p_activationMappingErrorList, ArrayList<RetSubsMappingVO> p_duplicateMappingErrorList) throws BTSLBaseException {
        // Decide what kind of source available for reading mapping
        _recordsCountInFile = 0;
        final String METHOD_NAME = "getActivationMappingList";
        try {
            readMappingFromFile(_fileObject, p_activationMappingMap, p_activationMappingErrorList, p_duplicateMappingErrorList);
            final int definedActivationMappingMaxCount = Integer.parseInt(Constants.getProperty("ACTIVATION_MAPPING_FILE_RECORDS_MAX_COUNT"));
            if (_recordsCountInFile > definedActivationMappingMaxCount) {
                AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A",
                    "Number of records in one file should not greater than " + definedActivationMappingMaxCount, "Number of Total Records in File = " + _recordsCountInFile);
                throw new BTSLBaseException("ActivationMappingReader", "getActivationMappingList()",
                    "Number of records in one file should not greater than " + definedActivationMappingMaxCount);
            }

        } catch (Exception exp) {
            _logger.errorTrace(METHOD_NAME, exp);
            throw new BTSLBaseException("ActivationMappingReader", "getActivationMappingList()", "Exception at File reading FileName = " + _fileObject.getName());
        }
    }

    /**
     * Read Retailer Subscriber Mapping details from CRM.
     * 
     * @author nand.sahu
     * @param _activationMappingMap
     * @param _activationMappingErrorList
     * @param duplicateMappingErrorList
     * @throws BTSLBaseException
     */
    public void getActivationMappingList(java.util.HashMap<String, RetSubsMappingVO> p_activationMappingMap, java.util.HashMap<String, RetSubsMappingVO> p_activationMappingErrorList, ArrayList<RetSubsMappingVO> p_duplicateMappingErrorList) throws BTSLBaseException {

        final String METHOD_NAME = "getActivationMappingList";
        try {
            // readMappingFromCRM(p_activationMappingMap,
            // p_activationMappingErrorList, p_duplicateMappingErrorList);

        } catch (Exception exp) {
            _logger.errorTrace(METHOD_NAME, exp);
            throw new BTSLBaseException("ActivationMappingReader", "getActivationMappingList()", "Exception ");
        }
    }

    /**
     * @author nand.sahu
     * @param _fileObject
     * @param _activationMappingHt
     * @param ErrorList
     * @throws FileNotFoundException
     * @throws IOException
     * @throws BTSLBaseException 
     */
    private void readMappingFromFile(File _fileObject, java.util.HashMap<String, RetSubsMappingVO> p_activationMappingMap, java.util.HashMap<String, RetSubsMappingVO> p_activationMappingErrorList, ArrayList<RetSubsMappingVO> p_duplicateMappingErrorList) throws FileNotFoundException, IOException, BTSLBaseException {
        final String METHOD_NAME = "readMappingFromFile";
        if (_logger.isDebugEnabled()) {
            _logger.debug("readMappingFromFile()", "Entered ");
        }
        String thisLine = null;
        BufferedReader input = null;
        RetSubsMappingVO retSubsMappingVO = null;
        final java.util.Date currentUtilDate = new java.util.Date();
        // ActivationSubscriberRetailerMapping
        // activationSubscriberRetailerMapping= new
        // ActivationSubscriberRetailerMapping();
        int formattingErrors = 0;
        /* Start Reading .txt file * */
        try {
            final String currentDateStr = BTSLUtil.getDateStringFromDate(currentUtilDate);
            input = new BufferedReader(new FileReader(_fileObject));

            while (!(thisLine = ((input.readLine()).toUpperCase())).startsWith("EOF")) {
                _recordsCountInFile++;

                int iLoop = 0;
                final ArrayList<String> st = split(thisLine, ',');
                if (st.size() < 3 || st.size() > 4) {
                    formattingErrors = formattingErrors + 1;
                    AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "<FAILED> File format is incorrect for line = " + thisLine,
                        "File Name = " + _fileObject.getName());
                    continue;
                }
                retSubsMappingVO = new RetSubsMappingVO();
                try {
                    // Reading Retailer MSISDN
                    final String retMSISDN = BTSLUtil.NullToString(st.get(iLoop++)).trim();
                    if (BTSLUtil.isNullString(retMSISDN)) {
                        formattingErrors = formattingErrors + 1;
                        AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "<FAILED> Retailer MSISDN can not be blank",
                            "Given Record in File = " + thisLine);
                        continue;
                    } else {
                        retSubsMappingVO.setRetailerMsisdn(retMSISDN);
                    }
                    // Reading Subscriber MSISDN
                    final String subscriberMsisdn = BTSLUtil.NullToString(st.get(iLoop++)).trim();
                    if (BTSLUtil.isNullString(subscriberMsisdn)) {
                        formattingErrors = formattingErrors + 1;
                        AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "<FAILED> Subscriber MSISDN can not be blank",
                            "Given Record in File = " + thisLine);
                        continue;
                    } else {
                        retSubsMappingVO.setSubscriberMsisdn(subscriberMsisdn);
                    }
                    // Reading Subscriber Type
                    final String subscriberType = BTSLUtil.NullToString(st.get(iLoop++));
                    // _logger.debug("readMappingFromFile",
                    // "subscriberType = "+subscriberType);
                    retSubsMappingVO.setSubscriberType(subscriberType);
                    // If Subscriber already read then no need to process again
                    if (!(p_activationMappingMap.containsKey(subscriberMsisdn) || p_activationMappingErrorList.containsKey(subscriberMsisdn))) {
                        if (BTSLUtil.isNullString(subscriberType)) {
                            retSubsMappingVO.setMessage("<FAILED> Subscriber Type can not be blank");
                            p_activationMappingErrorList.put(retSubsMappingVO.getSubscriberMsisdn(), retSubsMappingVO);
                            continue;
                        } else {
                            retSubsMappingVO.setSubscriberType(subscriberType);
                        }

                        // Retailer MSISDN and Subscriber MSISDN can not be same
                        if (retMSISDN.equalsIgnoreCase(subscriberMsisdn)) {
                            retSubsMappingVO.setMessage("<FAILED> Retailer and Subscriber MSISDN can not be same");
                            p_activationMappingErrorList.put(retSubsMappingVO.getSubscriberMsisdn(), retSubsMappingVO);
                            continue;
                        }
                        // Reading Activation Date
                        String _activationdateStr = null;
                        try {
                            _activationdateStr = (BTSLUtil.NullToString((String) st.get(iLoop++))).trim();
                        } catch (Exception ee) {
                            _activationdateStr = null;
                            _logger.errorTrace(METHOD_NAME, ee);
                        }
                        java.util.Date _activationUtilDate = null;
                        if (!BTSLUtil.isNullString(_activationdateStr)) {
                            try {
                                _activationUtilDate = BTSLUtil.getDateFromDateString(_activationdateStr);
                                if (_activationUtilDate.before(currentUtilDate) && !currentDateStr.equalsIgnoreCase(_activationdateStr)) {
                                    // Setting Previous date if file date is
                                    // less than current date
                                    retSubsMappingVO.setRegisteredOn(BTSLUtil.addDaysInUtilDate(currentUtilDate, -1));// Setting
                                    // Previous
                                    // date.
                                } else {
                                    retSubsMappingVO.setMessage("<FAILED> Activation date should be Previous date");
                                    p_activationMappingErrorList.put(retSubsMappingVO.getSubscriberMsisdn(), retSubsMappingVO);
                                    continue;
                                }
                            } catch (Exception ex) {
                                _logger.error("readMappingFromFile()", "Activation date format is not correct  _activationdateStr = " + _activationdateStr);
                                retSubsMappingVO.setMessage("<FAILED> Incorrect format for date");
                                p_activationMappingErrorList.put(retSubsMappingVO.getSubscriberMsisdn(), retSubsMappingVO);
                                _logger.errorTrace(METHOD_NAME, ex);
                                continue;
                            }
                        }// End of if block
                        else {
                            // Setting Previous date if date is not provided in
                            // file.
                            retSubsMappingVO.setRegisteredOn(BTSLUtil.addDaysInUtilDate(currentUtilDate, -1));
                        }
                        // Read data successfully and adding in VO
                        p_activationMappingMap.put(subscriberMsisdn, retSubsMappingVO);
                    } else {// Both duplicate records should fail.
                        retSubsMappingVO.setMessage("<FAILED> Duplicate record");
                        p_duplicateMappingErrorList.add(retSubsMappingVO);
                        formattingErrors = formattingErrors + 1;
                    }
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                    _logger.error("readMappingFromFile()", "Records not well formated in file  thisLine = " + thisLine);
                }
            }// End while block
            java.util.ArrayList<RetSubsMappingVO> temp_duplicateMappingErrorList = new java.util.ArrayList<RetSubsMappingVO>();
            temp_duplicateMappingErrorList.addAll(p_duplicateMappingErrorList);
            // Removing duplicate records if exist in p_activationMappingMap
            int listSizes = temp_duplicateMappingErrorList.size();
            for (int i = 0; i < listSizes ; i++) {
                final RetSubsMappingVO allreadyDuplicateAddedVO = temp_duplicateMappingErrorList.get(i);
                if (allreadyDuplicateAddedVO != null && p_activationMappingMap.containsKey(BTSLUtil.NullToString(allreadyDuplicateAddedVO.getSubscriberMsisdn()))) {
                    final RetSubsMappingVO duplicateMappingVO = p_activationMappingMap.get(allreadyDuplicateAddedVO.getSubscriberMsisdn());
                    duplicateMappingVO.setMessage("<FAILED> Duplicate record");
                    p_duplicateMappingErrorList.add(duplicateMappingVO);
                    formattingErrors = formattingErrors + 1;// Error for
                    // Previous one and
                    // current one
                    p_activationMappingMap.remove(allreadyDuplicateAddedVO.getSubscriberMsisdn());// Removing
                    // records
                    // from
                    // Process
                    // Map
                }
            }
            temp_duplicateMappingErrorList = null;
            ActivationSubscriberRetailerMapping.setCountErrorAtDataAccess(formattingErrors);
            ActivationSubscriberRetailerMapping.setTotalRecords(_recordsCountInFile);
        } catch (FileNotFoundException fe) {
            _logger.error("readMappingFromFile()", ":: File not Found:::::::");
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationSubscriberRetailerMapping[main]", "", "",
                "", "Exception at reading data from FILE");
            _logger.errorTrace(METHOD_NAME, fe);
            throw new FileNotFoundException();
        } catch (IOException ioe) {
            _logger.error("readMappingFromFile()", " Exception at file reading " + ioe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationSubscriberRetailerMapping[main]", "", "",
                "", "Exception at reading data from FILE");
            _logger.errorTrace(METHOD_NAME, ioe);
            throw new IOException();
        } catch (Exception e) {
            _logger.error("readMappingFromFile()", " Exception at file reading" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationSubscriberRetailerMapping[main]", "", "",
                "", "Exception at reading data from FILE");
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME, "Exception in reading Mapping From File");
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            _logger
                .debug(
                    "readMappingFromFile()",
                    "Exited File Name =  " + _fileObject.getName() + " _recordsCountInFile = " + _recordsCountInFile + ", Errors at Reading From Files = " + formattingErrors + ", No. of Duplicate Errors in file = " + p_duplicateMappingErrorList
                        .size());
        }

    }

    /**
     * This method is similar to a StringTokenizer. In case there is no space
     * between 2 consecutive delimiters, then also a blank space is taken unlike
     * in a String Tokenizer which skips if there is no space between 2
     * delimiters.
     * 
     * @author nand.sahu
     * @param st
     * @param sep
     * @return
     */
    public ArrayList<String> split(String st, char sep) {

        final ArrayList<String> alist = new ArrayList<String>();

        final int len = st.length();
        int pos = 0;
        int fin = 0;

        // while not end of string, and you can find a match
        while (pos < len && (fin = st.indexOf(sep, pos)) != -1) {
            alist.add(st.substring(pos, fin));
            pos = fin + 1;
        }
        // Push remainder if it's not empty
        final String remainder = st.substring(pos);
        if (remainder.length() != 0) {
            alist.add(remainder);
        }

        return alist;
    } // end of split

}// End of class
