package com.btsl.pretups.scheduletopup.process;

/**
 * @# RechargeBatchFileParser
 * 
 *    Created by Created on History
 *    --------------------------------------------------------------------------
 *    ------
 *    Manisha jain 03/06/08 Initial creation
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2008 Bharti Telesoft Ltd.
 * 
 * 
 */
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.ScheduleFileProcessLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberDAO;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;
import com.btsl.pretups.restrictedsubs.businesslogic.ScheduledBatchDetailDAO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.txn.pretups.restrictedsubs.businesslogic.RestrictedSubscriberTxnDAO;

public class GiftRechargeBatchFileParser implements BatchFileParserI {
    public static final Log logger = LogFactory.getLog(GiftRechargeBatchFileParser.class.getName());

    /**
     * Method downloadFile.
     * This method is called to download file at the time of scheduling the file
     * 
     * @param p_fileWriter
     *            Writer
     * @param p_fileType
     *            String
     * @param p_scheduleInfoMap
     *            HashMap
     * @throws BTSLBaseException
     */
    public void downloadFile(Connection p_con, Writer p_fileWriter, String p_fileType, HashMap p_scheduleInfoMap) throws BTSLBaseException {
        if (logger.isDebugEnabled()) {
            logger.debug("downloadFile", "Entered p_fileType=" + p_fileType + " ,p_scheduleInfoMap=" + p_scheduleInfoMap.size());
        }
        final String METHOD_NAME = "downloadFile";
        try {
            if (PretupsI.BATCH_TYPE_CORPORATE.equals(p_fileType)) {
                downloadCorpFile(p_con, p_fileWriter, p_scheduleInfoMap);
            } else {
                downloadNormalFile(p_fileWriter, p_scheduleInfoMap);
            }

        } catch (Exception ex) {
            logger.error("downloadNormalRCFile", "Unable to write data " + ex.getMessage());
            logger.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException("RechargeBatchFileParser", "downloadCorpRCFile", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("downloadFile", "Exiting");
            }

        }
    }

    /**
     * Method downloadFileForReshedule.
     * This method is called to download file at the time of re-scheduling the
     * file
     * 
     * @param p_con
     *            Connection
     * @param p_fileType
     *            String
     * @param p_fileWriter
     *            Writer
     * @param p_scheduleInfoMap
     *            HashMap
     * @throws BTSLBaseException
     */
    public void downloadFileForReshedule(Connection p_con, Writer p_fileWriter, String p_fileType, HashMap p_scheduleInfoMap) throws BTSLBaseException {
        if (logger.isDebugEnabled()) {
            logger.debug("downloadFileForReshedule", "Entered p_scheduleInfoMap.size()" + p_scheduleInfoMap.size() + " ,p_fileType=" + p_fileType);
        }
        final String METHOD_NAME = "downloadFileForReshedule";
        try {
            RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
            ArrayList list = restrictedSubscriberDAO.loadBatchDetailVOList(p_con, (String) p_scheduleInfoMap.get(BATCH_ID), PretupsI.STATUS_EQUAL, PretupsI.SCHEDULE_STATUS_SCHEDULED);

            String heading = (String) ((ArrayList) p_scheduleInfoMap.get(HEADER_KEY)).get(0);
            ArrayList columnHeaderList = (ArrayList) p_scheduleInfoMap.get(COLUMN_HEADER_KEY);
            p_fileWriter.write("," + heading + ",");
            p_fileWriter.write("\n");
            for (int i = 0, j = columnHeaderList.size(); i < j; i++) {
                p_fileWriter.write((String) columnHeaderList.get(i) + ",");
            }
            p_fileWriter.write("\n");
            ScheduleBatchDetailVO scheduleBatchDetailVO = null;
            for (int i = 0, j = list.size(); i < j; i++) {
                scheduleBatchDetailVO = (ScheduleBatchDetailVO) list.get(i);
                p_fileWriter.write(scheduleBatchDetailVO.getMsisdn() + ",");
                p_fileWriter.write(scheduleBatchDetailVO.getSubService() + ",");
                p_fileWriter.write(PretupsBL.getDisplayAmount(scheduleBatchDetailVO.getAmount()) + ",");
                p_fileWriter.write(LocaleMasterCache.getLocaleDetailsFromlocale(new Locale(scheduleBatchDetailVO.getLanguage(), scheduleBatchDetailVO.getCountry())).getLanguage_code() + ",");
                p_fileWriter.write(scheduleBatchDetailVO.getDonorMsisdn() + ",");
                p_fileWriter.write(scheduleBatchDetailVO.getDonorName() + ",");
                p_fileWriter.write(LocaleMasterCache.getLocaleDetailsFromlocale(new Locale(scheduleBatchDetailVO.getDonorLanguage(), scheduleBatchDetailVO.getDonorCountry())).getLanguage_code());
                p_fileWriter.write("\n");
            }
            p_fileWriter.write("eof,,,,\n");
            p_fileWriter.write("\n");
        } catch (BTSLBaseException be) {
            logger.error("downloadFileForReshedule", "Unable to write data  " + be.getMessage());
            throw be;
        } catch (Exception e) {
            logger.error("downloadFileForReshedule", "Unable to write data " + e.getMessage());
            logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("RechargeBatchFileParser", "downloadFileForReshedule", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("downloadFileForReshedule", "Exiting");
            }
        }
    }

    /**
     * Method uploadFile.
     * This method is called to upload the file for Gift recharge service
     * 
     * @param p_con
     *            Connection
     * @param p_fileType
     *            String
     * @param p_scheduleInfoMap
     *            HashMap
     * @param isErrorFound
     *            boolean
     * @throws BTSLBaseException
     */
    public void uploadFile(Connection p_con, String p_fileType, HashMap p_scheduleInfoMap, boolean isErrorFound) throws BTSLBaseException {
        if (logger.isDebugEnabled()) {
            logger.debug("uploadFile", "Entered isErrorFound= " + isErrorFound + " p_scheduleInfoMap.size()= " + p_scheduleInfoMap.size() + " ,p_fileType=" + p_fileType);
        }
        String filePathAndFileName = null;
        final String METHOD_NAME = "uploadFile";
        try {
            String dataStr = null;
            int countData = 0;
            String language = null;
            String country = null;
            String languageGifter = null;
            String countryGifter = null;
            RestrictedSubscriberVO errorVO = null;
            String arr[] = null;
            String filteredMsisdn = null;
            String filteredMsisdnGifter = null;
            String msisdnPrefix = null;
            String msisdnPrefixGifter = null;
            NetworkPrefixVO networkPrefixVO = null;
            String networkCode = null;
            String networkCodeGifter = null;
            String tmpSubService = null;
            String subService = null;
            String tmpAmt = null;
            long reqAmt = 0;
            String requestFor = null;
            String batchID = null;
            String createdBy = null;
            String gifterName = null;
            ScheduleBatchMasterVO scheduleBatchMasterVO = null;
            ScheduleBatchDetailVO scheduleDetailVO = null;
            ArrayList validDataList = null;
            int temp = 0;

            // get error keys from the HashMap for different validations
            String[] errorUploadKeys = (String[]) p_scheduleInfoMap.get(ERROR_KEY);
            String invalidMSISDN = errorUploadKeys[0]; // msisdn of receiver
            String prefixNotFound = errorUploadKeys[1]; // network prefix for
                                                        // receiver
            String networkNotSupported = errorUploadKeys[2]; // network not
                                                             // supported for
                                                             // receiver
            String subServiceNotFound = errorUploadKeys[3]; // subservice not
                                                            // found
            String reqAmtNotFound = errorUploadKeys[4]; // request amount not
                                                        // found
            String subServiceInvalid = errorUploadKeys[5]; // invalid sub
                                                           // service
            String reqAmtInvalid = errorUploadKeys[6]; // invalid requested
                                                       // amount to recharge
            String languageCodeInvalid = errorUploadKeys[8]; // language code
                                                             // for receiver
            String invalidGifterMSISDN = errorUploadKeys[11]; // msisdn of
                                                              // gifter
            String prefixNotFoundGifter = errorUploadKeys[12]; // network prefix
                                                               // for gifter
            String networkNotSupportedGifter = errorUploadKeys[13]; // network
                                                                    // not
                                                                    // supported
                                                                    // for
                                                                    // gifter
            String languageCodeInvalidGifter = errorUploadKeys[14]; // invalid
                                                                    // language
                                                                    // code for
                                                                    // gifter
            String invalidName = errorUploadKeys[15]; // gifter name invalid
            String nameNotFound = errorUploadKeys[16]; // gifter name not found
            String languageNotFound = errorUploadKeys[20];

            String definedSubServiceValue = Constants.getProperty("DEFINED_SUB_SERVICE_VALUE");
            // get values from the hashMap
            ArrayList finalList = (ArrayList) p_scheduleInfoMap.get("FINAL_LIST");
            int finalListSize = Integer.parseInt(p_scheduleInfoMap.get("FINAL_LIST_SIZE").toString());
            UserVO userVO = (UserVO) p_scheduleInfoMap.get("USER_VO");
            String filePath = Constants.getProperty("UploadRestrictedMSISDNFilePath");
            filePathAndFileName = filePath + (String) p_scheduleInfoMap.get("FILE_NAME");
            requestFor = (String) p_scheduleInfoMap.get("REQUEST_FOR");
            batchID = (String) p_scheduleInfoMap.get(BATCH_ID);
            createdBy = (String) p_scheduleInfoMap.get("CREATED_BY");
            scheduleBatchMasterVO = (ScheduleBatchMasterVO) p_scheduleInfoMap.get("SCHEDULED_VO");
            validDataList = new ArrayList();
            int errorCount = 0;
            while (finalListSize != countData) {
                language = null;
                country = null;
                languageGifter = null;
                countryGifter = null;
                errorVO = (RestrictedSubscriberVO) finalList.get(countData);
                dataStr = errorVO.getMsisdn();

                if (logger.isDebugEnabled()) {
                    logger.debug("uploadFile", "Processing starts for  " + dataStr);
                }
                countData++;
                if (errorVO.getErrorCode() == null) {
                    arr = dataStr.split(",");
                    if (temp == 0) {
                        int arrLength = 0;
                        int ind = 0;
                        for (int i = 0; i < 9; i++) {
                            ind = dataStr.indexOf(',');
                            if (ind != -1) {
                                dataStr = dataStr.substring(ind + 1, dataStr.length());
                                arrLength++;
                            }
                        }
                        temp = 1;
                        if (arrLength > 7 || arrLength < 6) {
                            isErrorFound = true;
                            p_scheduleInfoMap.put("IS_ERROR_FOUND", new Boolean(isErrorFound));
                            p_scheduleInfoMap.put("IS_FILE_TYPE_DIFF", errorUploadKeys[9]);
                            return;
                        }
                    }

                    // check for valid MSISDN
                    try {
                        if (BTSLUtil.isNullString(arr[0])) {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Receiver msisdn not found", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(errorUploadKeys[18]);
                            errorVO.setMsisdn(arr[0]);
                            isErrorFound = true;
                            continue;
                        }
                        // Change ID=ACCOUNTID
                        // FilteredMSISDN is replaced by
                        // getFilteredIdentificationNumber
                        // This is done because this field can contains msisdn
                        // or account id
                        filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(arr[0]); // before
                                                                                            // process
                                                                                            // MSISDN
                                                                                            // filter
                                                                                            // each-one
                    } catch (Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("uploadFile", "Receiver msisdn is invalid " + arr[0]);
                        }
                        logger.errorTrace(METHOD_NAME, e);
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Receiver msisdn is invalid", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(invalidMSISDN);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    // Change ID=ACCOUNTID
                    // isValidMsisdn is replaced by isValidIdentificationNumber
                    // This is done because this field can contains msisdn or
                    // account id
                    if (!BTSLUtil.isValidIdentificationNumber(filteredMsisdn)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("uploadFile", "Receiver msisdn is invalid " + arr[0]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Receiver msisdn is invalid", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(invalidMSISDN);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    // check prefix of the MSISDN
                    msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); // get
                                                                              // the
                                                                              // prefix
                                                                              // of
                                                                              // the
                                                                              // MSISDN
                    networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                    if (networkPrefixVO == null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("uploadFile", "Receiver msisdn Network prefix not found " + arr[0]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Receiver msisdn Network prefix not found", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(prefixNotFound);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    // check network support of the MSISDN
                    networkCode = networkPrefixVO.getNetworkCode();
                    if (!networkCode.equals(userVO.getNetworkID())) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("uploadFile", "Receiver mobile number is from unsupporting Network" + arr[0]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Receiver mobile number is from unsupporting Network", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(networkNotSupported);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    try {
                        tmpSubService = null;
                        tmpSubService = arr[1];
                        subService = String.valueOf(Integer.parseInt(tmpSubService));
                        if (definedSubServiceValue.indexOf(subService) == -1) {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid sub-service value = " + subService, "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(subServiceInvalid);
                            errorVO.setErrorCodeArgs(new String[] { subService });
                            errorVO.setMsisdn(arr[0]);
                            isErrorFound = true;
                            continue;
                        }
                    } catch (Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("uploadFile", "Not a valid sub-service = " + tmpSubService);
                        }
                        logger.errorTrace(METHOD_NAME, e);
                        if (!BTSLUtil.isNullString(tmpSubService)) {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid sub-service value = " + tmpSubService, "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(subServiceInvalid);
                            errorVO.setErrorCodeArgs(new String[] { tmpSubService });
                        } else {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Sub-service value is not entered", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(subServiceNotFound);
                        }

                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    try {
                        tmpAmt = null;
                        tmpAmt = arr[2];
                        reqAmt = PretupsBL.getSystemAmount(tmpAmt);
                    } catch (Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("uploadFile", "Not a valid requested amount tmpAmt: " + tmpAmt);
                        }
                        logger.errorTrace(METHOD_NAME, e);
                        if (!BTSLUtil.isNullString(tmpAmt)) {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid requested amount = " + tmpAmt, "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(reqAmtInvalid);
                            errorVO.setErrorCodeArgs(new String[] { tmpAmt });
                        } else {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Requested amount is not entered", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(reqAmtNotFound);
                        }

                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    // validate for language code only use in batch recharge
                    try {
                        tmpAmt = null;
                        tmpAmt = arr[3];
                        if (BTSLUtil.isNullString(tmpAmt)) {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Language code not found", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(languageNotFound);
                            errorVO.setMsisdn(arr[0]);
                            isErrorFound = true;
                            continue;
                        } else {
                            tmpAmt = tmpAmt.trim();
                        }
                        Locale locale = LocaleMasterCache.getLocaleFromCodeDetails(tmpAmt);
                        if (locale == null) {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid Receiver lanuage code = " + tmpAmt, "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(languageCodeInvalid);
                            errorVO.setErrorCodeArgs(new String[] { tmpAmt });
                            errorVO.setMsisdn(arr[0]);
                            isErrorFound = true;
                            continue;
                        }
                        language = locale.getLanguage();
                        country = locale.getCountry();
                    } catch (Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("uploadFile", "Not a valid Receiver lanuage code: " + tmpAmt);
                        }
                        logger.errorTrace(METHOD_NAME, e);
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid Receiver lanuage code = " + tmpAmt, "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCodeArgs(new String[] { tmpAmt });
                        errorVO.setErrorCode(languageCodeInvalid);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    // validations for gifter
                    try {
                        if (BTSLUtil.isNullString(arr[4])) {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Gifter msisdn not found", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(errorUploadKeys[19]);
                            errorVO.setMsisdn(arr[0]);
                            isErrorFound = true;
                            continue;
                        }
                        filteredMsisdnGifter = PretupsBL.getFilteredIdentificationNumber(arr[4]); // before
                                                                                                  // process
                                                                                                  // MSISDN
                                                                                                  // filter
                                                                                                  // each-one
                    } catch (Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("uploadFile", "Receiver msisdn is invalid " + arr[0]);
                        }
                        logger.errorTrace(METHOD_NAME, e);
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Receiver msisdn is invalid", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(invalidGifterMSISDN);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    if (!BTSLUtil.isValidIdentificationNumber(filteredMsisdnGifter)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("uploadFile", "Receiver msisdn is invalid " + arr[0]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Receiver msisdn is invalid", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(invalidGifterMSISDN);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    // check prefix of the MSISDN
                    msisdnPrefixGifter = PretupsBL.getMSISDNPrefix(filteredMsisdnGifter); // get
                                                                                          // the
                                                                                          // prefix
                                                                                          // of
                                                                                          // the
                                                                                          // MSISDN
                    networkPrefixVO = null;
                    networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefixGifter);
                    if (networkPrefixVO == null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("uploadFile", "Receiver msisdn Network prefix not found " + arr[0]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Receiver msisdn Network prefix not found", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(prefixNotFoundGifter);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    // check network support of the MSISDN
                    networkCodeGifter = networkPrefixVO.getNetworkCode();
                    if (!networkCodeGifter.equals(userVO.getNetworkID())) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("uploadFile", "Receiver mobile number is from unsupporting Network" + arr[0]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Receiver mobile number is from unsupporting Network", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(networkNotSupportedGifter);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }

                    // gifter name validations: space, special character and
                    // numeric values are not allowed in gifter name
                    try {
                        gifterName = arr[5].trim();
                        if (BTSLUtil.isNullString(gifterName)) {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Name of gifter is not entered", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(nameNotFound);
                            errorVO.setMsisdn(arr[0]);
                            isErrorFound = true;
                            continue;
                        }
                        char charArr[] = gifterName.toCharArray();
                        errorCount = 0;
                        for (int i = 0; i < charArr.length; i++) {
                            if (!((charArr[i] >= 65 && charArr[i] <= 90) || (charArr[i] >= 97 && charArr[i] <= 122))) {
                                ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid Name of gifter = " + gifterName, "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                                errorVO.setErrorCode(invalidName);
                                errorVO.setErrorCodeArgs(new String[] { gifterName });
                                errorVO.setMsisdn(arr[0]);
                                isErrorFound = true;
                                errorCount++;
                                break;
                            }
                        }
                        if (errorCount > 0) {
                            continue;
                        }

                    } catch (Exception e) {

                        if (logger.isDebugEnabled()) {
                            logger.debug("uploadFile", "Not a valid Name of gifter = " + gifterName);
                        }
                        logger.errorTrace(METHOD_NAME, e);
                    }
                    // gifter language code validations
                    try {
                        tmpAmt = null;
                        tmpAmt = arr[6];
                        tmpAmt = tmpAmt.trim();
                        Locale locale = LocaleMasterCache.getLocaleFromCodeDetails(tmpAmt);
                        if (locale == null) {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid Gifter lanuage code = " + tmpAmt, "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(languageCodeInvalidGifter);
                            errorVO.setErrorCodeArgs(new String[] { tmpAmt });
                            errorVO.setMsisdn(arr[0]);
                            isErrorFound = true;
                            continue;
                        }
                        languageGifter = locale.getLanguage();
                        countryGifter = locale.getCountry();
                    } catch (Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("uploadFile", "Not a valid Receiver lanuage code: " + tmpAmt);
                        }
                        logger.errorTrace(METHOD_NAME, e);
                        if (!BTSLUtil.isNullString(tmpAmt)) {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid Gifter lanuage code = " + tmpAmt, "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCodeArgs(new String[] { tmpAmt });
                            errorVO.setErrorCode(languageCodeInvalidGifter);
                        } else {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Language code not found", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(languageNotFound);
                        }
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    // check receiver mobile number and gifter mobile number are
                    // same
                    if (filteredMsisdn.equals(filteredMsisdnGifter)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("uploadFile", "Receiver msisdn and gifter msisdn cannot be same: " + filteredMsisdn);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Receiver msisdn and gifter msisdn cannot be same: " + filteredMsisdn, "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(errorUploadKeys[17]);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }

                    scheduleDetailVO = new ScheduleBatchDetailVO();
                    scheduleDetailVO.setBatchID(scheduleBatchMasterVO.getBatchID());
                    scheduleDetailVO.setAmount(reqAmt);
                    scheduleDetailVO.setSubService(subService);
                    scheduleDetailVO.setMsisdn(filteredMsisdn);
                    scheduleDetailVO.setSubscriberID(filteredMsisdn);
                    scheduleDetailVO.setStatus(PretupsI.SCHEDULE_STATUS_SCHEDULED);
                    scheduleDetailVO.setModifiedBy(userVO.getActiveUserID());
                    scheduleDetailVO.setModifiedOn((Date) p_scheduleInfoMap.get("MODIFIED_ON"));
                    scheduleDetailVO.setCreatedBy(userVO.getActiveUserID());
                    scheduleDetailVO.setCreatedOn((Date) p_scheduleInfoMap.get("CREATED_ON"));
                    scheduleDetailVO.setLineNumber(errorVO.getLineNumber());
                    scheduleDetailVO.setLanguage(language);
                    scheduleDetailVO.setCountry(country);
                    scheduleDetailVO.setDonorMsisdn(filteredMsisdnGifter);
                    scheduleDetailVO.setDonorName(gifterName);
                    scheduleDetailVO.setDonorLanguage(languageGifter);
                    scheduleDetailVO.setDonorCountry(countryGifter);

                    // insert the valid MSISDN Data in the validMsisdnList
                    validDataList.add(scheduleDetailVO);

                } else if ("NEWLINE".equals(errorVO.getErrorCode()) || "NEWLINE".equals(errorVO.getMsisdn())) {
                    // /
                    // Remove this object form the list since not for further in
                    // use
                    // /
                    finalList.remove(--countData);
                    finalListSize--;
                }
            }
            p_scheduleInfoMap.put("IS_ERROR_FOUND", new Boolean(isErrorFound));
            p_scheduleInfoMap.put("FINAL_LIST", finalList);
            if (validDataList.isEmpty()) {
                p_scheduleInfoMap.put("FINAL_LIST", finalList);
                p_scheduleInfoMap.put("PROCESSED_RECS", "0");
                p_scheduleInfoMap.put("IS_ERROR_FOUND", new Boolean(isErrorFound));
                // theForm.setErrorLogList(finalList);
                scheduleBatchMasterVO.setUploadFailedCount(countData);
                if (logger.isDebugEnabled()) {
                    logger.debug("uploadFile", "No valid Data in the list, size :" + validDataList.size());
                }
                throw new BTSLBaseException(this, "uploadFile", errorUploadKeys[10], "showMsg");
            }

            int addCount = 0;
            addCount = new ScheduledBatchDetailDAO().addScheduleDetails(p_con, validDataList, true);
            if (addCount > 0) {
                p_con.commit();
                if (logger.isDebugEnabled()) {
                    logger.debug("uploadFile", "commit connection");
                }
            }
            if (addCount == validDataList.size()) {
                scheduleBatchMasterVO.setTotalCount(countData);
                scheduleBatchMasterVO.setUploadFailedCount(countData - addCount);
                scheduleBatchMasterVO.setCancelledCount(0);
                scheduleBatchMasterVO.setProcessFailedCount(0);
                scheduleBatchMasterVO.setSuccessfulCount(0);
                addCount = new ScheduledBatchDetailDAO().updateScheduleBatchMaster(p_con, scheduleBatchMasterVO);
                if (addCount > 0) {
                    p_con.commit();
                    if (logger.isDebugEnabled()) {
                        logger.debug("uploadFile", "commit connection");
                    }
                    p_scheduleInfoMap.put("PROCESSED_RECS", String.valueOf(validDataList.size()));
                } else {
                    p_con.rollback();
                    logger.error("uploadFile", "Scheduling file for the service recharge is unsuccessful");
                    throw new BTSLBaseException(this, "uploadFile", errorUploadKeys[7], "scheduleTopUpAuthorise");
                }
            } else {
                p_con.rollback();
                logger.error("uploadFile", "Scheduling file for the service recharge is unsuccessful");
                throw new BTSLBaseException(this, "uploadFile", errorUploadKeys[7], "scheduleTopUpAuthorise");
            }

        } catch (BTSLBaseException be) {
            logger.error("uploadFile", "Unable to write data  " + be.getMessage());
            throw be;
        } catch (Exception e) {
            logger.error("uploadFile", "Scheduling file for the service recharge is unsuccessful " + e.getMessage());
            logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("RechargeBatchFileParser", "uploadFile", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("uploadFile", "Exiting");
            }
        }
    }

    /**
     * Method getColumnKeys.
     * This method is called to get column keys used in file depends on file
     * type
     * 
     * @param p_fileType
     *            String
     * @return String[]
     */
    public String[] getColumnKeys(String p_fileType) {
        if (logger.isDebugEnabled()) {
            logger.debug("getColumnKeys", "Entered p_fileType= " + p_fileType);
        }
        String[] keyArr = { FILE_COLUMN_HEADER_KEYS[0], FILE_COLUMN_HEADER_KEYS[7], FILE_COLUMN_HEADER_KEYS[8], FILE_COLUMN_HEADER_KEYS[10], FILE_COLUMN_HEADER_KEYS[11], FILE_COLUMN_HEADER_KEYS[12], FILE_COLUMN_HEADER_KEYS[13] };
        return keyArr;
    }

    /**
     * Method getErrorKeys.
     * This method is called to get error keys used in file depends on file
     * type, used at time time of upload of file
     * 
     * @param p_fileType
     *            String
     * @return String[]
     */
    public String[] getErrorKeys(String p_fileType) {
        if (logger.isDebugEnabled()) {
            logger.debug("getErrorKeys", "Entered p_fileType= " + p_fileType);
        }
        String[] errorKeyArr = { UPLOAD_ERROR_KEYS[18], UPLOAD_ERROR_KEYS[20], UPLOAD_ERROR_KEYS[22], UPLOAD_ERROR_KEYS[4], UPLOAD_ERROR_KEYS[5], UPLOAD_ERROR_KEYS[6], UPLOAD_ERROR_KEYS[7], UPLOAD_ERROR_KEYS[14], UPLOAD_ERROR_KEYS[24], UPLOAD_ERROR_KEYS[26], UPLOAD_ERROR_KEYS[17], UPLOAD_ERROR_KEYS[19], UPLOAD_ERROR_KEYS[21], UPLOAD_ERROR_KEYS[23], UPLOAD_ERROR_KEYS[25], UPLOAD_ERROR_KEYS[27], UPLOAD_ERROR_KEYS[28], UPLOAD_ERROR_KEYS[29], UPLOAD_ERROR_KEYS[30], UPLOAD_ERROR_KEYS[31], UPLOAD_ERROR_KEYS[32] };
        return errorKeyArr;
    }

    /**
     * Method getHeaderKey.
     * This method is called to get header keys used in file depends on file
     * type
     * 
     * @param p_fileType
     *            String
     * @return String
     */
    public String getHeaderKey(String p_fileType) {
        if (logger.isDebugEnabled()) {
            logger.debug("getHeaderKey", "Entered p_fileType= " + p_fileType);
        }
        String heading = FILE_HEADER_KEYS[1];
        return heading;
    }

    /**
     * Method getCommaString.
     * This method append the commas to the string for the display purpose
     * 
     * @param p_text
     *            String
     * @return java.lang.String
     */
    private String getCommaString(String p_text) {
        if (logger.isDebugEnabled()) {
            logger.debug("getCommaString", "Entered p_text=" + p_text);
        }

        StringBuffer commaString = new StringBuffer();
        int flag = 0;
        for (int k = 0, l = p_text.length(); k < l; k++) {
            if (p_text.charAt(k) == ' ') {
                flag = 1;
                commaString.append(",");
            }
        }
        if (flag != 1) {
            commaString.append(",");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getCommaString", "Exiting:commaString=" + commaString);
        }

        return commaString.toString();
    }

    /**
     * Method getRequestMessage.
     * This method returns the message format accordind to service type
     * 
     * @param p_scheduleBatchDetailVO
     *            ScheduleBatchDetailVO
     * @param p_smsPin
     *            String
     * @return java.lang.String
     */
    public String getRequestMessage(ScheduleBatchDetailVO p_scheduleBatchDetailVO, ChannelUserVO p_channelUserVO, String p_serviceType) {
        if (logger.isDebugEnabled()) {
            logger.debug("getRequestMessage", "Entered ScheduleBatchDetailVO =" + p_scheduleBatchDetailVO + " p_channelUserVO= " + p_channelUserVO + " p_serviceType= " + p_serviceType);
        }
        String message = null;
        String seprator = null;
        try {
            if (!BTSLUtil.isNullString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR))) {
                seprator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            } else {
                seprator = " ";
            }
            message = p_serviceType + seprator + p_scheduleBatchDetailVO.getMsisdn() + seprator + PretupsBL.getDisplayAmount(p_scheduleBatchDetailVO.getAmount()) + seprator + Integer.parseInt(p_scheduleBatchDetailVO.getSubService()) + seprator + p_scheduleBatchDetailVO.getLanguageCode() + seprator + p_scheduleBatchDetailVO.getSenderLanguageCode() + seprator + p_scheduleBatchDetailVO.getDonorMsisdn() + seprator + p_scheduleBatchDetailVO.getDonorName() + seprator + p_scheduleBatchDetailVO.getDonorLanguageCode() + seprator + BTSLUtil.decryptText(p_channelUserVO.getUserPhoneVO().getSmsPin());
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("getRequestMessage", "Exitin message formate =" + message);
            }
        }
        return message;
    }

    /**
     * Method downloadCorpFile.
     * This method is called to download corporate file at the time of
     * scheduling the file
     * 
     * @param p_con
     *            Connection
     * @param p_fileWriter
     *            Writer
     * @param p_scheduleInfoMap
     *            HashMap
     * @throws BTSLBaseException
     */
    private void downloadCorpFile(Connection p_con, Writer p_fileWriter, HashMap p_scheduleInfoMap) throws BTSLBaseException {
        if (logger.isDebugEnabled()) {
            logger.debug("downloadCorpFile", "Entered");
        }
        final String METHOD_NAME = "downloadCorpFile";
        try {

            String userId = (String) p_scheduleInfoMap.get("USER_ID");
            String ownerId = (String) p_scheduleInfoMap.get("OWNER_ID");

            RestrictedSubscriberTxnDAO restrictedSubscriberTxnDAO = new RestrictedSubscriberTxnDAO();
            LinkedHashMap hashMap = restrictedSubscriberTxnDAO.loadRestrictedSubscriberList(p_con, userId, ownerId);
            p_scheduleInfoMap.put("DATA_MAP", hashMap);

            String heading = (String) ((ArrayList) p_scheduleInfoMap.get(HEADER_KEY)).get(0);
            ArrayList columnHeaderList = (ArrayList) p_scheduleInfoMap.get(COLUMN_HEADER_KEY);

            for (int j = 0; j <= 9; j++) {
                if (j == 3) {
                    p_fileWriter.write("," + heading + getCommaString(heading));
                } else {
                    p_fileWriter.write(",");
                }
            }
            p_fileWriter.write("\n");

            for (int i = 0, j = columnHeaderList.size(); i < j; i++) {

                p_fileWriter.write((String) columnHeaderList.get(i) + ",");
            }
            p_fileWriter.write("\n");

            // Iterating thru the Hashmap object and writing the actual data to
            // the PrintWriter object ///
            RestrictedSubscriberVO restrictedSubscriberVO = null;
            Iterator iterator = hashMap.keySet().iterator();
            String key = null;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                restrictedSubscriberVO = (RestrictedSubscriberVO) hashMap.get(key);
                p_fileWriter.write(restrictedSubscriberVO.getMsisdn() + ",");
                p_fileWriter.write(ServiceSelectorMappingCache.getDefaultSelectorForServiceType((String) p_scheduleInfoMap.get(SERVICE_TYPE)).getSelectorCode()); // for
                                                                                                                                                                  // default
                                                                                                                                                                  // value
                                                                                                                                                                  // of
                                                                                                                                                                  // sub-service
                p_fileWriter.write(",,,,,");
                p_fileWriter.write("\n");
            }
            p_fileWriter.write("eof,,,,,,,");

        } catch (BTSLBaseException be) {
            logger.error("downloadCorpFile", "Unable to write data  " + be.getMessage());
            throw be;
        } catch (Exception e) {
            logger.error("downloadCorpFile", "Unable to write data " + e.getMessage());
            logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("RechargeBatchFileParser", "downloadCorpRCFile", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("downloadCorpFile", "Exiting");
            }
        }
    }

    /**
     * Method downloadNormalFile.
     * This method is called to download normal file at the time of scheduling
     * the file
     * 
     * @param p_fileWriter
     *            Writer
     * @param p_scheduleInfoMap
     *            HashMap
     * @throws BTSLBaseException
     */
    private void downloadNormalFile(Writer p_fileWriter, HashMap p_scheduleInfoMap) throws BTSLBaseException {
        if (logger.isDebugEnabled()) {
            logger.debug("downloadNormalFile", "Entered");
        }
        final String METHOD_NAME = "downloadNormalFile";
        try {

            String heading = (String) ((ArrayList) p_scheduleInfoMap.get(HEADER_KEY)).get(0);
            ArrayList columnHeaderList = (ArrayList) p_scheduleInfoMap.get(COLUMN_HEADER_KEY);
            p_fileWriter.write(",,,,");
            p_fileWriter.write("\n");
            p_fileWriter.write("," + heading + ",,");
            p_fileWriter.write("\n");
            for (int i = 0, j = columnHeaderList.size(); i < j; i++) {

                p_fileWriter.write((String) columnHeaderList.get(i) + ",");
            }
            int recordCount = Integer.parseInt(Constants.getProperty("BATCH_MSISDN_LIST_SIZE"));
            for (int i = 0; i < recordCount; i++) {
                p_fileWriter.write("\n");
            }
            p_fileWriter.write("eof,,,,");
            p_fileWriter.write("\n");
        } catch (Exception ex) {
            logger.error("downloadNormalRCFile", "Unable to write data " + ex.getMessage());
            logger.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException("RechargeBatchFileParser", "downloadCorpRCFile", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("downloadNormalFile", "Exiting");
            }

        }
    }

	@Override
	public void downloadFileForResheduleRest(Connection p_con, Writer p_fileWriter, String p_fileType,
			HashMap p_scheduleInfoMap) throws BTSLBaseException {
		 if (logger.isDebugEnabled()) {
	            logger.debug("downloadFileForResheduleRest", "Entered p_scheduleInfoMap.size()" + p_scheduleInfoMap.size() + " ,p_fileType=" + p_fileType);
	        }
	        final String METHOD_NAME = "downloadFileForResheduleRest";
	        try {
	            RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
	            ArrayList list = restrictedSubscriberDAO.loadBatchDetailVOList(p_con, (String) p_scheduleInfoMap.get(BATCH_ID), PretupsI.STATUS_EQUAL, PretupsI.SCHEDULE_STATUS_SCHEDULED);

	            ArrayList columnHeaderList = (ArrayList) p_scheduleInfoMap.get(COLUMN_HEADER_KEY);
	            String heading = (String) ((ArrayList) p_scheduleInfoMap.get(HEADER_KEY)).get(0);
	            p_fileWriter.write("," + heading + ",");
	            p_fileWriter.write("\n");
	            for (int i = 0, j = columnHeaderList.size(); i < j; i++) {
	              if(i  == j-1) {
	                 p_fileWriter.write((String) (columnHeaderList.get(i)));
	              }else{
	                p_fileWriter.write((String) columnHeaderList.get(i) + ",");
	              }
	            }
	            p_fileWriter.write("\n");
	            ScheduleBatchDetailVO scheduleBatchDetailVO = null;
	            for (int i = 0, j = list.size(); i < j; i++) {
	                scheduleBatchDetailVO = (ScheduleBatchDetailVO) list.get(i);
	                p_fileWriter.write(scheduleBatchDetailVO.getMsisdn() + ",");
	                p_fileWriter.write(scheduleBatchDetailVO.getSubService() + ",");
	                p_fileWriter.write(PretupsBL.getDisplayAmount(scheduleBatchDetailVO.getAmount()) + ",");
	                p_fileWriter.write(LocaleMasterCache.getLocaleDetailsFromlocale(new Locale(scheduleBatchDetailVO.getLanguage(), scheduleBatchDetailVO.getCountry())).getLanguage_code() + ",");
	                p_fileWriter.write(scheduleBatchDetailVO.getDonorMsisdn() + ",");
	                p_fileWriter.write(scheduleBatchDetailVO.getDonorName() + ",");
	                p_fileWriter.write(LocaleMasterCache.getLocaleDetailsFromlocale(new Locale(scheduleBatchDetailVO.getDonorLanguage(), scheduleBatchDetailVO.getDonorCountry())).getLanguage_code());
	                p_fileWriter.write("\n");
	            }
	        } catch (BTSLBaseException be) {
	            logger.error("downloadFileForResheduleRest", "Unable to write data  " + be.getMessage());
	            throw be;
	        } catch (Exception e) {
	            logger.error("downloadFileForResheduleRest", "Unable to write data " + e.getMessage());
	            logger.errorTrace(METHOD_NAME, e);
	            throw new BTSLBaseException("RechargeBatchFileParser", "downloadFileForResheduleRest", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
	        } finally {
	            if (logger.isDebugEnabled()) {
	                logger.debug("downloadFileForResheduleRest", "Exiting");
	            }
	        }
		
	}

}
