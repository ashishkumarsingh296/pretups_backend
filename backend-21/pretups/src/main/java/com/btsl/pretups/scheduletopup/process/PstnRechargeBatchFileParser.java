/**
 * @# RechargeBatchFileParser
 * 
 *    Created by Created on History
 *    --------------------------------------------------------------------------
 *    ------
 *    ranjana.chouhan 16/02/09 Initial creation
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2008 Bharti Telesoft Ltd.
 * 
 * 
 */
package com.btsl.pretups.scheduletopup.process;

import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.ScheduleFileProcessLog;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingCache;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingVO;
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

/**
 * this class is used to schedule the file for service type - PSTNRC
 */
public class PstnRechargeBatchFileParser implements BatchFileParserI {

    private static Log _log = LogFactory.getLog(PstnRechargeBatchFileParser.class.getName());

    /**
     * Method downloadFile.
     * This method is called to download file at the time of scheduling the file
     * 
     * @param p_con
     *            Connection
     * @param p_fileWriter
     *            Writer
     * @param p_scheduleMap
     *            HashMap
     * @param p_fileType
     *            String
     * @throws BTSLBaseException
     */
    public void downloadFile(Connection p_con, Writer p_fileWriter, String p_fileType, HashMap p_scheduleMap) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("downloadFile", "Entered p_fileType= " + p_fileType + ", p_scheduleMap.size()=" + p_scheduleMap.size());
        }
        final String METHOD_NAME = "downloadFile";
        try {
            if (PretupsI.BATCH_TYPE_CORPORATE.equals(p_fileType)) {
                downloadCorpPstnFile(p_con, p_fileWriter, p_scheduleMap);
            } else {
                downloadNormalPstnFile(p_fileWriter, p_scheduleMap);
            }
        } catch (BTSLBaseException be) {
            _log.error("downloadFile", "Unable to write data " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("downloadFile", "Unable to write data " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("PstnRechargeBatchFileParser", "downloadFile", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("downloadFile", "Exiting");
            }
        }
    }

    /**
     * Method downloadFileForReshedule.
     * This method is called to download file at the time of rescheduling the
     * file
     * 
     * @param p_con
     *            Connection
     * @param p_fileWriter
     *            Writer
     * @param p_fileType
     *            String
     * @param p_scheduleMap
     *            HashMap
     * @throws BTSLBaseException
     */
    public void downloadFileForReshedule(Connection p_con, Writer p_fileWriter, String p_fileType, HashMap p_scheduleMap) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("downloadFileForReshedule", "Entered p_fileType = " + p_fileType + ",p_scheduleMap.size()=" + p_scheduleMap.size());
        }
        final String METHOD_NAME = "downloadFileForReshedule";
        try {
            if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(p_fileType)) {
                downloadCorporatePstnFileForReschedule(p_con, p_fileWriter, p_scheduleMap);
            } else {
                downloadNormalPstnFileForReschedule(p_con, p_fileWriter, p_scheduleMap);
            }
        } catch (BTSLBaseException be) {
            _log.error("downloadFileForReshedule", "Unable to write data " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("downloadFileForReshedule", "Unable to write data " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("PstnRechargeBatchFileParser", "downloadFileForReshedule", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("downloadFileForReshedule", "Exiting");
            }
        }

    }

    /**
     * Method uploadFile.
     * This method is called to upload file
     * 
     * @param p_con
     *            Connection
     * @param p_fileType
     *            String
     * @param p_fileWriter
     *            Writer
     * @param p_scheduleInfoMap
     *            HashMap
     * @param p_isErrorFound
     *            boolean
     * @throws BTSLBaseException
     */
    public void uploadFile(Connection p_con, String p_fileType, HashMap p_scheduleInfoMap, boolean p_isErrorFound) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("uploadFile", "Entered p_fileType= " + p_fileType + " p_isErrorFound= " + p_isErrorFound + ", p_scheduleInfoMap.size()=" + p_scheduleInfoMap.size());
        }
        final String METHOD_NAME = "uploadFile";
        try {
            if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(p_fileType)) {
                uploadCorporatePstnFile(p_con, p_scheduleInfoMap, p_isErrorFound);
            } else {
                uploadNormalPstnFile(p_con, p_scheduleInfoMap, p_isErrorFound);
            }
        } catch (BTSLBaseException be) {
            _log.error("uploadFile", "Unable to write data " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("uploadFile", "Unable to write data " + e.getMessage());
            throw new BTSLBaseException("PstnRechargeBatchFileParser", "uploadFile", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("uploadFile", "Exiting");
            }
        }
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
     * @param p_scheduleMap
     *            HashMap
     * @throws BTSLBaseException
     */
    private void downloadCorpPstnFile(Connection p_con, Writer p_fileWriter, HashMap p_scheduleMap) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("downloadCorpPstnFile", "Entered");
        }
        final String METHOD_NAME = "downloadCorpPstnFile";
        try {
            String userId = (String) p_scheduleMap.get("USER_ID");
            String ownerId = (String) p_scheduleMap.get("OWNER_ID");

            RestrictedSubscriberTxnDAO restrictedSubscriberTxnDAO = new RestrictedSubscriberTxnDAO();
            LinkedHashMap hashMap = restrictedSubscriberTxnDAO.loadRestrictedSubscriberList(p_con, userId, ownerId);
            p_scheduleMap.put("DATA_MAP", hashMap);

            String heading = (String) ((ArrayList) p_scheduleMap.get(HEADER_KEY)).get(0);
            ArrayList columnHeaderList = (ArrayList) p_scheduleMap.get(COLUMN_HEADER_KEY);

            for (int j = 0; j <= 9; j++) {
                if (j == 4) {
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
            boolean isPstnNumber = false;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                restrictedSubscriberVO = (RestrictedSubscriberVO) hashMap.get(key);
                isPstnNumber = isPstnMsisdn(restrictedSubscriberVO.getMsisdn());
                if (isPstnNumber) {
                    p_fileWriter.write(restrictedSubscriberVO.getMsisdn() + ",");
                    p_fileWriter.write(restrictedSubscriberVO.getSubscriberID() + ",");
                    p_fileWriter.write(BTSLUtil.NullToString(restrictedSubscriberVO.getEmployeeName()) + ",");
                    p_fileWriter.write(PretupsBL.getDisplayAmount(restrictedSubscriberVO.getMinTxnAmount()) + ",");
                    p_fileWriter.write(PretupsBL.getDisplayAmount(restrictedSubscriberVO.getMaxTxnAmount()) + ",");
                    p_fileWriter.write(PretupsBL.getDisplayAmount(restrictedSubscriberVO.getMonthlyLimit()) + ",");
                    p_fileWriter.write(PretupsBL.getDisplayAmount(restrictedSubscriberVO.getTotalTransferAmount()) + ",");
                    p_fileWriter.write(ServiceSelectorMappingCache.getDefaultSelectorForServiceType((String) p_scheduleMap.get(SERVICE_TYPE)).getSelectorCode()); // for
                                                                                                                                                                  // default
                                                                                                                                                                  // value
                                                                                                                                                                  // of
                                                                                                                                                                  // sub-service
                    p_fileWriter.write("\n");
                }
            }
            p_fileWriter.write("EOF,,,,,,,,,");

        } catch (BTSLBaseException be) {
            _log.error("downloadCorpPstnFile", "Unable to write data  " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("downloadCorpPstnFile", "Unable to write data " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("PstnRechargeBatchFileParser", "downloadCorpPstnFile", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "downloadCorpPstnFile");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("downloadCorpPstnFile", "Exiting");
            }
        }
    }

    /**
     * Method downloadNormalPstnFile.
     * This method is called to download normal file at the time of scheduling
     * the file
     * 
     * @param p_fileWriter
     *            Writer
     * @param p_scheduleMap
     *            HashMap
     * @throws BTSLBaseException
     */
    private void downloadNormalPstnFile(Writer p_fileWriter, HashMap p_scheduleMap) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("downloadNormalPstnFile", "Entered");
        }
        final String METHOD_NAME = "downloadNormalPstnFile";
        try {
            String heading = (String) ((ArrayList) p_scheduleMap.get(HEADER_KEY)).get(0);
            ArrayList columnHeaderList = (ArrayList) p_scheduleMap.get(COLUMN_HEADER_KEY);

            p_fileWriter.write(",,,,");
            p_fileWriter.write("\n");
            for (int j = 0; j < 4; j++) {
                if (j == 1) {
                    p_fileWriter.write("," + heading + getCommaString(heading));
                } else {
                    p_fileWriter.write(",");
                }
            }
            p_fileWriter.write("\n");

            for (int i = 0, j = columnHeaderList.size(); i < j; i++) {
                p_fileWriter.write((String) columnHeaderList.get(i) + ",");
            }

            int recordCount = Integer.parseInt(Constants.getProperty("BATCH_MSISDN_LIST_SIZE"));
            for (int i = 0; i < recordCount; i++) {
                p_fileWriter.write("\n");
            }
            p_fileWriter.write("EOF,,,,,,,,,");
            p_fileWriter.write("\n");
        } catch (Exception ex) {
            _log.error("downloadNormalRCFile", "Unable to write data " + ex.getMessage());
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException("PstnRechargeBatchFileParser", "downloadNormalPstnFile", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "downloadNormalPstnFile");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("downloadNormalPstnFile", "Exiting");
            }
        }
    }

    /**
     * Method downloadCorporatePstnFileForReschedule.
     * This method is called to download corporate file at the time of
     * re-scheduling the file
     * 
     * @param p_con
     *            Connection
     * @param p_fileWriter
     *            Writer
     * @param p_scheduleInfoMap
     *            HashMap
     * @throws BTSLBaseException
     */
    private void downloadCorporatePstnFileForReschedule(Connection p_con, Writer p_fileWriter, HashMap p_scheduleMap) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("downloadCorporatePstnFileForReschedule", "Entered p_scheduleMap.size()= " + p_scheduleMap.size());
        }
        final String METHOD_NAME = "downloadCorporatePstnFileForReschedule";
        try {
            RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
            LinkedHashMap hashMap = restrictedSubscriberDAO.loadScheduleBatchDetailsList(p_con, (String) (p_scheduleMap.get(BATCH_ID)), PretupsI.STATUS_EQUAL, PretupsI.SCHEDULE_STATUS_SCHEDULED);
            p_scheduleMap.put("DATA_MAP", hashMap);
            String heading = (String) ((ArrayList) p_scheduleMap.get(HEADER_KEY)).get(0);
            ArrayList columnHeaderList = (ArrayList) p_scheduleMap.get(COLUMN_HEADER_KEY);
            for (int j = 0; j <= 9; j++) {
                if (j == 4) {
                    p_fileWriter.write("," + heading + getCommaString(heading));
                } else {
                    p_fileWriter.write(",");
                }
            }
            p_fileWriter.write("\n");
            for (int i = 0, j = columnHeaderList.size(); i < j; i++) {
                p_fileWriter.write((String) (columnHeaderList.get(i)) + ",");
            }
            p_fileWriter.write("\n");
            // / Iterating thru the Hashmap object and writing the actual data
            // to the PrintWriter object ///
            ScheduleBatchDetailVO scheduleDetailVO = null;
            Iterator iterator = hashMap.keySet().iterator();
            String key = null;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                scheduleDetailVO = (ScheduleBatchDetailVO) hashMap.get(key);
                p_fileWriter.write(scheduleDetailVO.getMsisdn() + ",");
                p_fileWriter.write(scheduleDetailVO.getSubscriberID() + ",");
                p_fileWriter.write(BTSLUtil.NullToString(scheduleDetailVO.getEmployeeName()) + ",");
                p_fileWriter.write(PretupsBL.getDisplayAmount(scheduleDetailVO.getMinTxnAmount()) + ",");
                p_fileWriter.write(PretupsBL.getDisplayAmount(scheduleDetailVO.getMaxTxnAmount()) + ",");
                p_fileWriter.write(PretupsBL.getDisplayAmount(scheduleDetailVO.getMonthlyLimit()) + ",");
                p_fileWriter.write(PretupsBL.getDisplayAmount(scheduleDetailVO.getTotalTransferAmount()) + ",");
                p_fileWriter.write(scheduleDetailVO.getSubService() + ",");
                p_fileWriter.write(PretupsBL.getDisplayAmount(scheduleDetailVO.getAmount()) + ",");
                p_fileWriter.write(scheduleDetailVO.getDonorMsisdn());
                p_fileWriter.write("\n");
            }
            p_fileWriter.write("EOF,,,,,,,");

        } catch (BTSLBaseException be) {
            _log.error("downloadCorporatePstnFileForReschedule", "Unable to write data  " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("downloadCorporatePstnFileForReschedule", "Unable to write data " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("PstnRechargeBatchFileParser", "downloadCorporatePstnFileForReschedule", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("downloadCorporatePstnFileForReschedule", "Exiting");
            }
        }
    }

    /**
     * Method downloadNormalPstnFileForReschedule.
     * This method is called to download normal file at the time of
     * re-scheduling the file
     * 
     * @param p_con
     *            Connection
     * @param p_fileWriter
     *            Writer
     * @param p_scheduleInfoMap
     *            HashMap
     * @throws BTSLBaseException
     */
    private void downloadNormalPstnFileForReschedule(Connection p_con, Writer p_fileWriter, HashMap p_scheduleInfoMap) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("downloadNormalFileForReschedule", "Entered p_scheduleInfoMap.size()=" + p_scheduleInfoMap.size());
        }
        final String METHOD_NAME = "downloadNormalPstnFileForReschedule";
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
                p_fileWriter.write(scheduleBatchDetailVO.getDonorMsisdn());
                p_fileWriter.write("\n");
            }
            p_fileWriter.write("EOF,,,,\n");
            p_fileWriter.write("\n");
        } catch (BTSLBaseException be) {
            _log.error("downloadNormalPstnFileForReschedule", "Unable to write data  " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("downloadNormalPstnFileForReschedule", "Unable to write data " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("PstnRechargeBatchFileParser", "downloadNormalPstnFileForReschedule", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("downloadNormalPstnFileForReschedule", "Exiting");
            }
        }
    }

    /**
     * Method uploadCorporatePstnFile.
     * This method is called to upload the RestrictedUser's file or normal user
     * file
     * 
     * @param p_con
     *            Connection
     * @param p_scheduleInfoMap
     *            HashMap
     * @param isErrorFound
     *            boolean
     * @throws BTSLBaseException
     */
    public void uploadCorporatePstnFile(Connection p_con, HashMap p_scheduleInfoMap, boolean isErrorFound) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("uploadCorporatePstnFile", "Entered isErrorFound= " + isErrorFound + " p_scheduleInfoMap.size()= " + p_scheduleInfoMap.size());
        }
        final String METHOD_NAME = "uploadCorporatePstnFile";
        String filePathAndFileName = null;
        try {
            String dataStr = null;
            int countData = 0;
            String language = null;
            String country = null;
            RestrictedSubscriberVO errorVO = null;
            String arr[] = null;
            String filteredMsisdn = null;
            String msisdnPrefix = null;
            NetworkPrefixVO networkPrefixVO = null;
            String networkCode = null;
            String tmpSubService = null;
            String subService = null;
            String tmpAmt = null;
            long reqAmt = 0;
            String errorKey = null;
            String requestFor = null;
            String ownerID = null;
            String userID = null;
            String batchID = null;
            String createdBy = null;
            Date modifiedOn = null;
            ScheduleBatchMasterVO scheduleBatchMasterVO = null;
            RestrictedSubscriberVO restrictedSubscriberVO = null;
            ScheduleBatchDetailVO scheduleDetailVO = null;
            String filteredNotificationMsisdn = null;

            HashMap scheduleInfoMap = p_scheduleInfoMap;
            int temp = 0;

            ArrayList finalList = (ArrayList) scheduleInfoMap.get("FINAL_LIST");
            int finalListSize = Integer.parseInt(scheduleInfoMap.get("FINAL_LIST_SIZE").toString());
            UserVO userVO = (UserVO) scheduleInfoMap.get("USER_VO");
            String filePath = Constants.getProperty("UploadRestrictedMSISDNFilePath");
            filePathAndFileName = filePath + (String) scheduleInfoMap.get("FILE_NAME");

            // get error keys from hashmap for different validations
            String[] errorUploadKeys = (String[]) scheduleInfoMap.get(ERROR_KEY);
            String invalidReceiverMSISDN = errorUploadKeys[0];
            String prefixNotFound = errorUploadKeys[1];
            String networkNotSupported = errorUploadKeys[2];
            String informaiotnNotFound = errorUploadKeys[3];
            String subServiceNotFound = errorUploadKeys[4];
            String reqAmtNotFound = errorUploadKeys[5];
            String subServiceInvalid = errorUploadKeys[6];
            String reqAmtInvalid = errorUploadKeys[7];
            String amtNotinRange = errorUploadKeys[8];
            String invalidNotificationMsisdn = errorUploadKeys[15];
            String definedSubServiceValue = Constants.getProperty("DEFINED_SUB_SERVICE_VALUE");

            // If user does not click on the down load link and upload file form
            // his own then load data form the
            // database for the checking purpose

            HashMap dataMap = (HashMap) scheduleInfoMap.get(DATA_MAP);

            requestFor = (String) scheduleInfoMap.get("REQUEST_FOR");
            ownerID = (String) scheduleInfoMap.get(OWNER_ID);
            userID = (String) scheduleInfoMap.get(USER_ID);
            batchID = (String) scheduleInfoMap.get(BATCH_ID);
            createdBy = (String) scheduleInfoMap.get("CREATED_BY");
            String downloadedBatchID = (String) scheduleInfoMap.get("DOWNLOAD_BATCH_ID");
            scheduleBatchMasterVO = (ScheduleBatchMasterVO) scheduleInfoMap.get("SCHEDULED_VO");
            modifiedOn = (Date) scheduleInfoMap.get("MODIFIED_ON");

            RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
            RestrictedSubscriberTxnDAO restrictedSubscriberTxnDAO = new RestrictedSubscriberTxnDAO();
            ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
            if ("Schedule".equals(requestFor)) {
                errorKey = errorUploadKeys[11];
                if (dataMap == null || dataMap.isEmpty()) {
                    dataMap = restrictedSubscriberTxnDAO.loadRestrictedSubscriberList(p_con, userID, ownerID);
                }
            } else if ("Reschedule".equals(requestFor)) {
                errorKey = errorUploadKeys[12];
                if (dataMap == null || dataMap.isEmpty() || !downloadedBatchID.equals(batchID)) {
                    dataMap = restrictedSubscriberDAO.loadScheduleBatchDetailsList(p_con, batchID, PretupsI.STATUS_EQUAL, PretupsI.SCHEDULE_STATUS_CANCELED);
                }
            }
            // ends here
            ArrayList validDataList = new ArrayList();
            while (finalListSize != countData) {
                language = null;
                country = null;
                errorVO = (RestrictedSubscriberVO) finalList.get(countData);
                dataStr = errorVO.getMsisdn();

                if (_log.isDebugEnabled()) {
                    _log.debug("uploadCorporatePstnFile", "Processing starts for  " + dataStr);
                }
                countData++;
                if (errorVO.getErrorCode() == null) {
                    arr = dataStr.split(",");
                    // check for first record
                    if (temp == 0) {
                        int arrLength = 0;
                        int ind = 0;
                        for (int i = 0; i < 10; i++) {
                            ind = dataStr.indexOf(',');
                            if (ind != -1) {
                                dataStr = dataStr.substring(ind + 1, dataStr.length());
                                arrLength++;
                            }
                        }
                        temp = 1;
                        // check file type
                        if (arrLength < 9) {
                            isErrorFound = true;
                            scheduleInfoMap.put("IS_ERROR_FOUND", new Boolean(isErrorFound));
                            scheduleInfoMap.put("IS_FILE_TYPE_DIFF", errorUploadKeys[13]);
                            return;
                        }
                    }

                    // validation starts for receiver MSISDN
                    try {
                        if (BTSLUtil.isNullString(arr[0])) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("uploadCorporatePstnFile", "Receiver MSISDN is Null " + arr[0]);
                            }
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Receiver msisdn not found", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(invalidReceiverMSISDN);
                            errorVO.setMsisdn(arr[0]);
                            isErrorFound = true;
                            continue;
                        }
                        filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(arr[0]); // before
                                                                                            // process
                                                                                            // MSISDN
                                                                                            // filter
                                                                                            // each-one
                    } catch (Exception e) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadCorporatePstnFile", "Not a valid MSISDN " + arr[0]);
                        }
                        _log.errorTrace(METHOD_NAME, e);
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid MSISDN", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(invalidReceiverMSISDN);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    if (!BTSLUtil.isValidIdentificationNumber(filteredMsisdn)) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadCorporatePstnFile", "Not a valid MSISDN " + arr[0]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid MSISDN", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(invalidReceiverMSISDN);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); // get
                                                                              // the
                                                                              // prefix
                                                                              // of
                                                                              // the
                                                                              // MSISDN
                    networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                    if (networkPrefixVO == null) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadCorporatePstnFile", "Not Network prefix found " + arr[0]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not Network prefix found", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(prefixNotFound);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    networkCode = networkPrefixVO.getNetworkCode();
                    if (!networkCode.equals(userVO.getNetworkID())) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadCorporatePstnFile", "Not supporting Network" + arr[0]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not Supporting Network", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(networkNotSupported);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    try {
                        if (networkPrefixVO != null) {
                            long prefixId = networkPrefixVO.getPrefixID();
                            String type = networkPrefixVO.getSeriesType();

                        }
                    } catch (Exception e) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadCorporatePstnFile", "Not a valid Receiver Msisdn= " + filteredMsisdn);
                        }
                        _log.errorTrace(METHOD_NAME, e);
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Entered number is Not a PSTN number ", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(errorUploadKeys[16]);
                        errorVO.setErrorCodeArgs(new String[] { filteredMsisdn });
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }// Receiver Msisdn validation ends here
                    try {
                        tmpSubService = null;
                        tmpSubService = arr[7];
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
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadCorporatePstnFile", "Not a valid sub-service = " + tmpSubService);
                        }
                        _log.errorTrace(METHOD_NAME, e);
                        if (!BTSLUtil.isNullString(tmpSubService)) {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid sub-service value = " + tmpSubService, "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCodeArgs(new String[] { tmpSubService });
                            errorVO.setErrorCode(subServiceInvalid);
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
                        tmpAmt = arr[8];
                        reqAmt = PretupsBL.getSystemAmount(tmpAmt);
                    } catch (Exception e) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadCorporatePstnFile", "Not a valid requested amount,tmpAmt: " + tmpAmt);
                        }
                        _log.errorTrace(METHOD_NAME, e);
                        if (!BTSLUtil.isNullString(tmpAmt)) {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid requested amount = " + tmpAmt, "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCodeArgs(new String[] { tmpAmt });
                            errorVO.setErrorCode(reqAmtInvalid);
                        } else {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Requested amount is not entered", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(reqAmtNotFound);
                        }
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    // validate the notification msisdn
                    try {
                        if (BTSLUtil.isNullString(arr[9])) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("uploadCorporatePstnFile", "MSISDN is Null " + arr[0]);
                            }
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Receiver msisdn not found", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(invalidNotificationMsisdn);
                            errorVO.setMsisdn(arr[0]);
                            isErrorFound = true;
                            continue;
                        }
                        filteredNotificationMsisdn = PretupsBL.getFilteredIdentificationNumber(arr[9]);
                    } catch (Exception e) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadCorporatePstnFile", "Not a valid notification MSISDN " + arr[0]);
                        }
                        _log.errorTrace(METHOD_NAME, e);
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid notification MSISDN", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(invalidNotificationMsisdn);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    // Check the filtered notification msisdn, it is valid or
                    // not
                    if (!BTSLUtil.isValidIdentificationNumber(filteredNotificationMsisdn)) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadCorporatePstnFile", "Not a valid MSISDN " + arr[0]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid MSISDN", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(errorUploadKeys[17]);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }

                    restrictedSubscriberVO = (RestrictedSubscriberVO) dataMap.get(filteredMsisdn);
                    if (restrictedSubscriberVO == null) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadCorporatePstnFile", "No informaiton found for = " + arr[0]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "No informaiton found for = " + arr[0], "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(informaiotnNotFound);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    language = restrictedSubscriberVO.getLanguage();
                    country = restrictedSubscriberVO.getCountry();

                    if (reqAmt < restrictedSubscriberVO.getMinTxnAmount() || reqAmt > restrictedSubscriberVO.getMaxTxnAmount()) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadCorporatePstnFile", "Requested amount is not in between minimum amount and maximum amount= " + arr[8]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Requested amount is not in between minimum amount and maximum amount= " + arr[8], "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCodeArgs(new String[] { arr[8] });
                        errorVO.setErrorCode(amtNotinRange);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }

                    scheduleDetailVO = new ScheduleBatchDetailVO();
                    scheduleDetailVO.setBatchID(scheduleBatchMasterVO.getBatchID());
                    scheduleDetailVO.setAmount(reqAmt);
                    scheduleDetailVO.setSubService(subService);
                    scheduleDetailVO.setMsisdn(filteredMsisdn);
                    scheduleDetailVO.setSubscriberID(restrictedSubscriberVO.getSubscriberID());
                    scheduleDetailVO.setStatus(PretupsI.SCHEDULE_STATUS_SCHEDULED);
                    scheduleDetailVO.setModifiedBy(userVO.getActiveUserID());
                    scheduleDetailVO.setModifiedOn(modifiedOn);
                    scheduleDetailVO.setCreatedBy(userVO.getActiveUserID());
                    scheduleDetailVO.setCreatedOn((Date) scheduleInfoMap.get("CREATED_ON"));
                    scheduleDetailVO.setLineNumber(errorVO.getLineNumber());
                    scheduleDetailVO.setDonorMsisdn(filteredNotificationMsisdn);

                    scheduleDetailVO.setLanguage(language);
                    scheduleDetailVO.setCountry(country);

                    // insert the valid MSISDN Data in the validMsisdnList
                    validDataList.add(scheduleDetailVO);

                } else if ("NEWLINE".equals(errorVO.getErrorCode()) || "NEWLINE".equals(errorVO.getMsisdn())) {
                    // Remove this object form the list since not for further in
                    // use
                    finalList.remove(--countData);
                    finalListSize--;
                }
            }
            scheduleInfoMap.put("PROCESSED_RECS", "0");
            scheduleInfoMap.put("IS_ERROR_FOUND", new Boolean(isErrorFound));
            if (validDataList == null || validDataList.isEmpty()) {
                scheduleInfoMap.put("FINAL_LIST", finalList);
                scheduleBatchMasterVO.setUploadFailedCount(countData);
                if (_log.isDebugEnabled()) {
                    _log.debug("uploadCorporatePstnFile", "No valid Data in the file, size :" + validDataList.size());
                }
                throw new BTSLBaseException(this, "uploadCorporatePstnFile", errorKey, "showMsg");
            }
            // Now form the returnData string get the msisdns which are
            // associated with the user (since it may
            String returnData = restrictedSubscriberDAO.isSubscriberExistByStatus(p_con, ownerID, validDataList, PretupsI.STATUS_EQUAL, PretupsI.RES_MSISDN_STATUS_ASSOCIATED, null);

            if (!BTSLUtil.isNullString(returnData)) {
                ArrayList newValidDataList = new ArrayList();
                arr = returnData.split(",");
                for (int i = 0, j = arr.length; i < j; i++) {
                    filteredMsisdn = arr[i];
                    for (int m = 0, n = validDataList.size(); m < n; m++) {
                        scheduleDetailVO = (ScheduleBatchDetailVO) validDataList.get(m);
                        if (scheduleDetailVO.getMsisdn().equals(filteredMsisdn)) {
                            newValidDataList.add(scheduleDetailVO);
                            validDataList.remove(m--);
                            n--;
                            break;
                        }
                    }
                }
                String notAssociatedMSISDN = errorUploadKeys[9];

                for (int m = 0, n = validDataList.size(); m < n; m++) {
                    scheduleDetailVO = (ScheduleBatchDetailVO) validDataList.get(m);
                    if (_log.isDebugEnabled()) {
                        _log.debug("uploadCorporatePstnFile", "Mobile number is not associated (Active)= " + scheduleDetailVO.getMsisdn());
                    }
                    ScheduleFileProcessLog.log("Checking Data in Database", createdBy, scheduleDetailVO.getMsisdn(), batchID, "Mobile number is not associated (Active)", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                    errorVO = new RestrictedSubscriberVO();
                    errorVO.setLineNumber(scheduleDetailVO.getLineNumber());
                    errorVO.setErrorCode(notAssociatedMSISDN);
                    errorVO.setMsisdn(scheduleDetailVO.getMsisdn());
                    finalList.add(errorVO);
                    isErrorFound = true;
                }
                scheduleInfoMap.put("IS_ERROR_FOUND", new Boolean(isErrorFound));
                // If all the data form the list is removed due to invalid data
                // then show the error message on the
                // common jsp and show the error string
                if (newValidDataList.isEmpty()) {
                    scheduleInfoMap.put("FINAL_LIST", finalList);
                    scheduleBatchMasterVO.setUploadFailedCount(countData);
                    if (_log.isDebugEnabled()) {
                        _log.debug("uploadCorporatePstnFile", "No valid Data in the list, size :" + newValidDataList.size());
                    }
                    throw new BTSLBaseException(this, "uploadCorporatePstnFile", errorKey, "showMsg");
                }
                validDataList = null;
                validDataList = newValidDataList;
            }

            // Now validate all the data for the status of the schedule.
            String statusStr = "'" + PretupsI.SCHEDULE_STATUS_CANCELED + "','" + PretupsI.SCHEDULE_STATUS_EXECUTED + "'";
            String statusMode = PretupsI.STATUS_NOTIN;
            String logMessage = "Mobile number is already scheduled or canceled";

            returnData = scheduledBatchDetailDAO.isScheduleExistByStatus(p_con, validDataList, statusMode, statusStr, ownerID, modifiedOn);
            // Now from the returnData string get the msisdns (which is already
            // scheduled or canceled status)
            // and remove them for the arrayList for further processing.

            if (!BTSLUtil.isNullString(returnData)) {
                String alreadyScheduled = errorUploadKeys[10];
                arr = returnData.split(",");
                for (int i = 0, j = arr.length; i < j; i++) {
                    filteredMsisdn = arr[i];
                    for (int m = 0, n = validDataList.size(); m < n; m++) {
                        scheduleDetailVO = (ScheduleBatchDetailVO) validDataList.get(m);
                        if (scheduleDetailVO.getMsisdn().equals(filteredMsisdn)) {
                            validDataList.remove(m--);
                            n--;
                            if (_log.isDebugEnabled()) {
                                _log.debug("uploadCorporatePstnFile", logMessage + "=" + filteredMsisdn);
                            }
                            ScheduleFileProcessLog.log("Checking Data in Database", createdBy, filteredMsisdn, batchID, logMessage, "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO = new RestrictedSubscriberVO();
                            errorVO.setLineNumber(scheduleDetailVO.getLineNumber());
                            errorVO.setErrorCode(alreadyScheduled);
                            errorVO.setMsisdn(filteredMsisdn);
                            finalList.add(errorVO);
                            isErrorFound = true;
                            break;
                        }
                    }
                }
                scheduleInfoMap.put("IS_ERROR_FOUND", new Boolean(isErrorFound));
                scheduleInfoMap.put("FINAL_LIST", finalList);

                // If all the data form the list is removed due to invalid data
                // then show the error message on the
                // common jsp and show the error string
                if (validDataList.isEmpty()) {
                    // scheduleInfoMap.put("FINAL_LIST",finalList);
                    scheduleBatchMasterVO.setUploadFailedCount(countData);
                    if (_log.isDebugEnabled()) {
                        _log.debug("uploadCorporatePstnFile", "No valid Data in the list, size :" + validDataList.size());
                    }
                    throw new BTSLBaseException(this, "uploadCorporatePstnFile", errorKey, "showMsg");
                }
            }

            int addCount = 0;
            addCount = scheduledBatchDetailDAO.addScheduleDetails(p_con, validDataList, true);
            if (addCount == validDataList.size()) {
                scheduleBatchMasterVO.setTotalCount(countData);
                scheduleBatchMasterVO.setUploadFailedCount(countData - addCount);
                scheduleBatchMasterVO.setCancelledCount(0);
                scheduleBatchMasterVO.setProcessFailedCount(0);
                scheduleBatchMasterVO.setSuccessfulCount(0);
                addCount = scheduledBatchDetailDAO.updateScheduleBatchMaster(p_con, scheduleBatchMasterVO);
                if (addCount > 0) {
                    p_con.commit();
                    scheduleInfoMap.put("PROCESSED_RECS", String.valueOf(validDataList.size()));
                } else {
                    p_con.rollback();
                    _log.error("uploadCorporatePstnFile", "Scheduling file for the service recharge is unsuccessful");
                    throw new BTSLBaseException(this, "uploadCorporatePstnFile", errorUploadKeys[14], "scheduleTopUpAuthorise");
                }
            } else {
                p_con.rollback();
                _log.error("uploadCorporatePstnFile", "Scheduling file for the service recharge is unsuccessful");
                throw new BTSLBaseException(this, "uploadCorporatePstnFile", errorUploadKeys[14], "scheduleTopUpAuthorise");
            }
        } catch (BTSLBaseException be) {
            _log.error("uploadCorporatePstnFile", "Unable to write data  " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("uploadCorporatePstnFile", "Scheduling file for the service recharge is unsuccessful " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("PstnRechargeBatchFileParser", "uploadCorporatePstnFile", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("uploadCorporatePstnFile", "Exiting");
            }
        }
    }

    /**
     * Method uploadNormalPstnFile.
     * This method is called to upload the normal user's file
     * 
     * @param p_con
     *            Connection
     * @param p_scheduleInfoMap
     *            HashMap
     * @param isErrorFound
     *            boolean
     * @throws BTSLBaseException
     */
    private void uploadNormalPstnFile(Connection p_con, HashMap p_scheduleInfoMap, boolean isErrorFound) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("uploadNormalPstnFile", "Entered isErrorFound= " + isErrorFound + " p_scheduleInfoMap.size()= " + p_scheduleInfoMap.size());
        }
        String filePathAndFileName = null;
        final String METHOD_NAME = "uploadNormalPstnFile";
        try {
            String dataStr = null;
            int countData = 0;
            String language = null;
            String country = null;
            RestrictedSubscriberVO errorVO = null;
            String arr[] = null;
            String filteredMsisdn = null;
            String filteredNotificationMsisdn = null;
            String msisdnPrefix = null;
            NetworkPrefixVO networkPrefixVO = null;

            String networkCode = null;
            String tmpSubService = null;
            String subService = null;
            String tmpAmt = null;
            long reqAmt = 0;
            String requestFor = null;
            String batchID = null;
            String createdBy = null;
            ScheduleBatchMasterVO scheduleBatchMasterVO = null;
            ScheduleBatchDetailVO scheduleDetailVO = null;
            ArrayList validDataList = null;

            // get error keys from the HashMap for different validations
            String[] errorUploadKeys = (String[]) p_scheduleInfoMap.get(ERROR_KEY);
            String invalidMSISDN = errorUploadKeys[0];
            String prefixNotFound = errorUploadKeys[1];
            String networkNotSupported = errorUploadKeys[2];
            String subServiceNotFound = errorUploadKeys[3];
            String reqAmtNotFound = errorUploadKeys[5];
            String subServiceInvalid = errorUploadKeys[4];
            String reqAmtInvalid = errorUploadKeys[6];
            String invalidDataInFile = errorUploadKeys[10];
            String invalidNotificationMsisdn = errorUploadKeys[14];

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
            int temp = 0;
            while (finalListSize != countData) {
                language = null;
                country = null;
                errorVO = (RestrictedSubscriberVO) finalList.get(countData);
                dataStr = errorVO.getMsisdn();

                if (_log.isDebugEnabled()) {
                    _log.debug("uploadNormalPstnFile", "Processing starts for  " + dataStr);
                }
                countData++;
                if (errorVO.getErrorCode() == null) {
                    arr = dataStr.split(",");
                    if (temp == 0) {
                        int arrLength = 0;
                        int ind = 0;
                        for (int i = 0; i < 4; i++) {
                            ind = dataStr.indexOf(',');
                            if (ind != -1) {
                                dataStr = dataStr.substring(ind + 1, dataStr.length());
                                arrLength++;
                            }
                        }
                        temp = 1;
                        if (arrLength < 3) {
                            isErrorFound = true;
                            p_scheduleInfoMap.put("IS_ERROR_FOUND", new Boolean(isErrorFound));
                            p_scheduleInfoMap.put("IS_FILE_TYPE_DIFF", invalidDataInFile);
                            return;
                        }
                    }

                    // check for valid MSISDN
                    try {
                        if (BTSLUtil.isNullString(arr[0])) {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Receiver msisdn not found", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(invalidMSISDN);
                            errorVO.setMsisdn(arr[0]);
                            isErrorFound = true;
                            continue;
                        }
                        filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(arr[0]);
                    } catch (Exception e) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadNormalPstnFile", "Not a valid MSISDN " + arr[0]);
                        }
                        _log.errorTrace(METHOD_NAME, e);
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid MSISDN", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(invalidMSISDN);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    if (!BTSLUtil.isValidIdentificationNumber(filteredMsisdn)) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadNormalPstnFile", "Not a valid MSISDN " + arr[0]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid MSISDN", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(invalidMSISDN);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                    networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                    if (networkPrefixVO == null) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadNormalPstnFile", "Not Network prefix found " + arr[0]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not Network prefix found", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(prefixNotFound);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    networkCode = networkPrefixVO.getNetworkCode();
                    if (!networkCode.equals(userVO.getNetworkID())) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadNormalPstnFile", "Not supporting Network" + arr[0]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not Supporting Network", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(networkNotSupported);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    try {
                        long prefixId = networkPrefixVO.getPrefixID();
                        String type = networkPrefixVO.getSeriesType();

                    } catch (Exception e) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadNormalPstnFile", "Not a valid Receiver Msisdn= " + filteredMsisdn);
                        }
                        _log.errorTrace(METHOD_NAME, e);
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a PSTN number entered", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(errorUploadKeys[16]);
                        errorVO.setErrorCodeArgs(new String[] { filteredMsisdn });
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }// Receiver Msisdn validation ends here
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
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadNormalPstnFile", "Not a valid sub-service = " + tmpSubService);
                        }
                        _log.errorTrace(METHOD_NAME, e);
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
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadNormalPstnFile", "Not a valid requested amount tmpAmt: " + tmpAmt);
                        }
                        _log.errorTrace(METHOD_NAME, e);
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

                    // validate the notification msisdn
                    try {
                        if (BTSLUtil.isNullString(arr[3])) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("uploadNormalPstnFile", "Notification MSISDN is Null " + arr[0]);
                            }
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Notification msisdn not found", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(invalidNotificationMsisdn);
                            errorVO.setMsisdn(arr[3]);
                            isErrorFound = true;
                            continue;
                        }
                        filteredNotificationMsisdn = PretupsBL.getFilteredIdentificationNumber(arr[3]);
                    } catch (Exception e) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadNormalPstnFile", "Not a valid notification MSISDN " + arr[0]);
                        }
                        _log.errorTrace(METHOD_NAME, e);
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid notification MSISDN", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(invalidNotificationMsisdn);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    // Check the filtered notification msisdn, it is valid or
                    // not
                    if (!BTSLUtil.isValidIdentificationNumber(filteredNotificationMsisdn)) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadNormalPstnFile", "Not a valid MSISDN " + arr[0]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid MSISDN", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(errorUploadKeys[16]);
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
                    scheduleDetailVO.setDonorMsisdn(filteredNotificationMsisdn);
                    scheduleDetailVO.setLanguage(language);
                    scheduleDetailVO.setCountry(country);
                    // insert the valid MSISDN Data in the validMsisdnList
                    validDataList.add(scheduleDetailVO);

                } else if ("NEWLINE".equals(errorVO.getErrorCode()) || "NEWLINE".equals(errorVO.getMsisdn())) {
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

                scheduleBatchMasterVO.setUploadFailedCount(countData);
                if (_log.isDebugEnabled()) {
                    _log.debug("uploadNormalPstnFile", "No valid Data in the list, size :" + validDataList.size());
                }
                throw new BTSLBaseException(this, "uploadNormalPstnFile", errorUploadKeys[10], "showMsg");
            }

            int addCount = 0;
            addCount = new ScheduledBatchDetailDAO().addScheduleDetails(p_con, validDataList, true);
            if (addCount == validDataList.size()) {
                scheduleBatchMasterVO.setTotalCount(countData);
                scheduleBatchMasterVO.setUploadFailedCount(countData - addCount);
                scheduleBatchMasterVO.setCancelledCount(0);
                scheduleBatchMasterVO.setProcessFailedCount(0);
                scheduleBatchMasterVO.setSuccessfulCount(0);
                addCount = new ScheduledBatchDetailDAO().updateScheduleBatchMaster(p_con, scheduleBatchMasterVO);
                if (addCount > 0) {
                    p_con.commit();
                    if (_log.isDebugEnabled()) {
                        _log.debug("uploadNormalPstnFile", "commit connection");
                    }
                    p_scheduleInfoMap.put("PROCESSED_RECS", String.valueOf(validDataList.size()));
                } else {
                    p_con.rollback();
                    _log.error("uploadNormalPstnFile", "Scheduling file for the service recharge is unsuccessful");
                    throw new BTSLBaseException(this, "uploadNormalPstnFile", errorUploadKeys[7], "scheduleTopUpAuthorise");
                }
            } else {
                p_con.rollback();
                _log.error("uploadNormalPstnFile", "Scheduling file for the service recharge is unsuccessful");
                throw new BTSLBaseException(this, "uploadNormalPstnFile", errorUploadKeys[7], "scheduleTopUpAuthorise");
            }

        } catch (BTSLBaseException be) {
            _log.error("uploadNormalPstnFile", "Unable to write data  " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("uploadNormalPstnFile", "Scheduling file for the service recharge is unsuccessful " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("PstnRechargeBatchFileParser", "uploadNormalPstnFile", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("uploadNormalPstnFile", "Exiting");
            }
        }
    }

    /**
     * Method getColumnKeys.
     * This method is called to get column keys used in file.
     * 
     * @param p_fileType
     *            String
     * @return String[]
     */
    public String[] getColumnKeys(String p_fileType) {
        if (_log.isDebugEnabled()) {
            _log.debug("getColumnKeys", "Entered p_fileType= " + p_fileType);
        }
        String[] keyColumnArr = null;
        if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(p_fileType)) {
            String[] keyColumnArr1 = { FILE_COLUMN_HEADER_KEYS[0], FILE_COLUMN_HEADER_KEYS[1], FILE_COLUMN_HEADER_KEYS[2], FILE_COLUMN_HEADER_KEYS[3], FILE_COLUMN_HEADER_KEYS[4], FILE_COLUMN_HEADER_KEYS[5], FILE_COLUMN_HEADER_KEYS[6], FILE_COLUMN_HEADER_KEYS[7], FILE_COLUMN_HEADER_KEYS[8], FILE_COLUMN_HEADER_KEYS[14] };
            keyColumnArr = keyColumnArr1;
        } else if (PretupsI.BATCH_TYPE_NORMAL.equalsIgnoreCase(p_fileType)) {
            String[] keyColumnArr1 = { FILE_COLUMN_HEADER_KEYS[0], FILE_COLUMN_HEADER_KEYS[7], FILE_COLUMN_HEADER_KEYS[8], FILE_COLUMN_HEADER_KEYS[14] };
            keyColumnArr = keyColumnArr1;
        }

        if (_log.isDebugEnabled()) {
            _log.debug("getColumnKeys", "Exited keyColumnArr.length= " + keyColumnArr.length);
        }
        return keyColumnArr;
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
        if (_log.isDebugEnabled()) {
            _log.debug("getErrorKeys", "Entered p_fileType= " + p_fileType);
        }
        String[] errorKeyArr = null;
        if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(p_fileType)) {
            String[] errorKeyArr1 = { UPLOAD_ERROR_KEYS[0], UPLOAD_ERROR_KEYS[1], UPLOAD_ERROR_KEYS[2], UPLOAD_ERROR_KEYS[3], UPLOAD_ERROR_KEYS[4], UPLOAD_ERROR_KEYS[5], UPLOAD_ERROR_KEYS[6], UPLOAD_ERROR_KEYS[7], UPLOAD_ERROR_KEYS[8], UPLOAD_ERROR_KEYS[9], UPLOAD_ERROR_KEYS[10], UPLOAD_ERROR_KEYS[11], UPLOAD_ERROR_KEYS[12], UPLOAD_ERROR_KEYS[13], UPLOAD_ERROR_KEYS[14], UPLOAD_ERROR_KEYS[33], UPLOAD_ERROR_KEYS[34], UPLOAD_ERROR_KEYS[35] };
            errorKeyArr = errorKeyArr1;
        } else {
            String[] errorKeyArr1 = { UPLOAD_ERROR_KEYS[0], UPLOAD_ERROR_KEYS[1], UPLOAD_ERROR_KEYS[2], UPLOAD_ERROR_KEYS[3], UPLOAD_ERROR_KEYS[4], UPLOAD_ERROR_KEYS[5], UPLOAD_ERROR_KEYS[6], UPLOAD_ERROR_KEYS[7], UPLOAD_ERROR_KEYS[9], UPLOAD_ERROR_KEYS[10], UPLOAD_ERROR_KEYS[11], UPLOAD_ERROR_KEYS[12], UPLOAD_ERROR_KEYS[13], UPLOAD_ERROR_KEYS[14], UPLOAD_ERROR_KEYS[33], UPLOAD_ERROR_KEYS[34], UPLOAD_ERROR_KEYS[35] };
            errorKeyArr = errorKeyArr1;
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getErrorKeys", "Exiting ");
        }
        return errorKeyArr;
    }

    /**
     * Method getHeaderKey.
     * This method is called to get header keys used in file.
     * 
     * @param p_fileType
     *            String
     * @return String
     */
    public String getHeaderKey(String p_fileType) {
        if (_log.isDebugEnabled()) {
            _log.debug("getHeaderKey", "Entered p_fileType= " + p_fileType);
        }
        String heading = FILE_HEADER_KEYS[2];

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
        if (_log.isDebugEnabled()) {
            _log.debug("getCommaString", "Entered p_text=" + p_text);
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
        if (_log.isDebugEnabled()) {
            _log.debug("getCommaString", "Exiting:commaString=" + commaString);
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
        if (_log.isDebugEnabled()) {
            _log.debug("getRequestMessage", "Entered ScheduleBatchMasterVO =" + p_scheduleBatchDetailVO + " p_serviceType= " + p_serviceType);
        }
        String message = null;
        String separator = null;
        try {
            if (!BTSLUtil.isNullString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR))) {
                separator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            } else {
                separator = " ";
            }

            message = p_serviceType + separator + p_scheduleBatchDetailVO.getMsisdn() + separator + PretupsBL.getDisplayAmount(p_scheduleBatchDetailVO.getAmount()) + separator + p_scheduleBatchDetailVO.getDonorMsisdn() + separator + BTSLUtil.decryptText(p_channelUserVO.getUserPhoneVO().getSmsPin());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("getRequestMessage", "Exitin message formate =" + message);
            }
        }
        return message;
    }

    /**
     * Method isPstnMsisdn.
     * This method is used to check that the msisdn is pstn number on not.
     * 
     * @param p_msisdn
     *            String
     * @return java.lang.String
     * @throws BTSLBaseException
     */
    private boolean isPstnMsisdn(String p_msisdn) {
        if (_log.isDebugEnabled()) {
            _log.debug("isPstnMsisdn", "Entered p_msisdn=" + p_msisdn);
        }
        final String METHOD_NAME = "isPstnMsisdn";
        boolean isPstnMsisdn = false;
        String msisdnPrefix = null;
        NetworkPrefixVO networkPrefixVO = null;
        MSISDNPrefixInterfaceMappingVO msisdnPrefixInterfaceMappingVO = null;
        ArrayList arrList = new ArrayList();
        Iterator itr = null;
        try {
            String filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(p_msisdn);
            if (BTSLUtil.isValidIdentificationNumber(filteredMsisdn)) {
                msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); // get
                                                                          // the
                                                                          // prefix
                                                                          // of
                                                                          // the
                                                                          // MSISDN
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                arrList = getInterfaceIdList();
                if (networkPrefixVO != null) {
                    long prefixId = networkPrefixVO.getPrefixID();
                    String type = networkPrefixVO.getSeriesType();
                    msisdnPrefixInterfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(prefixId, type, "V");
                    if (msisdnPrefixInterfaceMappingVO != null) {
                        String _str = PretupsI.SERVICE_TYPE_CHNL_RECHARGE_PSTN + "_" + msisdnPrefixInterfaceMappingVO.getInterfaceTypeID();
                        itr = arrList.iterator();
                        while (itr.hasNext()) {
                            String _serviceTypeIntfId = (String) itr.next();
                            if (_str.equalsIgnoreCase(_serviceTypeIntfId)) {
                                isPstnMsisdn = true;
                            }
                        }

                    }
                }
            }

        } catch (Exception e) {
            _log.error("isPstnMsisdn", "isPstnMsisdn " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            // throw new
            // BTSLBaseException("isPstnMsisdn","isPstnMsisdn",PretupsErrorCodesI.GENERAL_PROCESSING_ERROR,"scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("isPstnMsisdn", "Exiting");
            }
        }
        return isPstnMsisdn;
    }

    /**
     * MethodName getInterfaceIdList, to get the arrayList of service Type and
     * InterfaceId
     * ie PSTNRC_FIXLINE,RC_CS3 etc...
     * 
     * @return ArrayList
     * @throws BTSLBaseException
     */
    private ArrayList getInterfaceIdList() throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getInterfaceIdList", "Entered");
        }
        final String METHOD_NAME = "getInterfaceIdList";
        ArrayList arrlist = new ArrayList();
        try {
            String serviceWithInterafaceId = Constants.getProperty("PARSER_CONFIGURE_PARAM");
            String[] semiColonArr = serviceWithInterafaceId.trim().split(";");
            for (int i = 0; i < semiColonArr.length; i++) {
                String[] _splitOnColon = semiColonArr[i].split(":");
                String _serviceType = _splitOnColon[0];
                String[] _interfaceTypeId = _splitOnColon[1].split(",");
                for (int j = 0; j < _interfaceTypeId.length; j++) {
                    String s = _serviceType + "_" + _interfaceTypeId[j];
                    arrlist.add(s);
                }
            }
        } catch (Exception e) {
            _log.error("getInterfaceIdList", "getInterfaceIdList", e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("getInterfaceIdList", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "PARSER_CONFIGURE_PARAM not defined in constants.props");
        }

        return arrlist;
    }

	@Override
	public void downloadFileForResheduleRest(Connection p_con, Writer p_fileWriter, String p_fileType,
			HashMap p_scheduleInfoMap) throws BTSLBaseException {
		// TODO Auto-generated method stub
		
	}
}
