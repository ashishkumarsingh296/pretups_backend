/**
 * @(#)CorporateIATRechargeBatchFileParser.java
 *                                              Copyright(c) 2011, Comviva
 *                                              Technologies Ltd.
 *                                              All Rights Reserved
 *                                              --------------------------------
 *                                              --------------------------------
 *                                              ----------------
 *                                              Created by Created on History
 *                                              --------------------------------
 *                                              --------------------------------
 *                                              ----------------
 *                                              Babu Kunwar 07-OCT-2011 Initial
 *                                              creation
 *                                              --------------------------------
 *                                              --------------------------------
 *                                              ----------------
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
import com.btsl.pretups.iat.businesslogic.IATCountryMasterVO;
import com.btsl.pretups.iat.util.IATCommonUtil;
import com.btsl.pretups.iatrestrictedsubs.businesslogic.IATRestrictedSubscriberDAO;
import com.btsl.pretups.logging.ScheduleFileProcessLog;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberDAO;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;
import com.btsl.pretups.restrictedsubs.businesslogic.ScheduledBatchDetailDAO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.txn.pretups.iatrestrictedsubs.businesslogic.IATRestrictedSubscriberTxnDAO;

public class CorporateIATRechargeBatchFileParser implements BatchFileParserI {

    /**
     * This class is used to schedule the file for service type - INTRCC
     */
    private static Log _log = LogFactory.getLog(CorporateIATRechargeBatchFileParser.class.getName());

    /**
     * Method downloadFile.
     * This method is called to download file at the time of scheduling.
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
    public void downloadFile(Connection p_con, Writer p_fileWriter, String p_fileType, HashMap p_scheduleInfoMap) throws BTSLBaseException {
        // TODO Auto-generated method stub
        if (_log.isDebugEnabled()) {
            _log.debug("downloadFile", "Entered p_fileType= " + p_fileType + ", p_scheduleMap.size()=" + p_scheduleInfoMap.size());
        }
        final String METHOD_NAME = "downloadFile";
        try {
            if (PretupsI.BATCH_TYPE_CORPORATE.equals(p_fileType)) {
                downloadFileForCorporateBatch(p_con, p_fileWriter, p_scheduleInfoMap);
            }
        } catch (BTSLBaseException be) {
            _log.error("downloadFile", "Unable to write data " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("downloadFile", "Unable to write data " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("CorporateIATRechargeBatchFileParser", "downloadFile", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
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
                downloadCorporateIATFileForReschedule(p_con, p_fileWriter, p_scheduleMap);
            }
        } catch (BTSLBaseException be) {
            _log.error("downloadFileForReshedule", "Unable to write data " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("downloadFileForReshedule", "Unable to write data " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("CorporateIATRechargeBatchFileParser", "downloadFileForReshedule", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("downloadFileForReshedule", "Exiting");
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
            String[] keyColumnArr1 = { IAT_FILE_COLUMN_KEY_HEADER[0], IAT_FILE_COLUMN_KEY_HEADER[1], IAT_FILE_COLUMN_KEY_HEADER[2], IAT_FILE_COLUMN_KEY_HEADER[3], IAT_FILE_COLUMN_KEY_HEADER[4] };
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
            String[] errorKeyArr1 = { IAT_ERROR_KEY[0], IAT_ERROR_KEY[1], IAT_ERROR_KEY[2], IAT_ERROR_KEY[3], IAT_ERROR_KEY[4], IAT_ERROR_KEY[5], IAT_ERROR_KEY[6], IAT_ERROR_KEY[7], IAT_ERROR_KEY[8], IAT_ERROR_KEY[9], IAT_ERROR_KEY[10], IAT_ERROR_KEY[11] };
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
        String heading = IAT_FILE_HEADER_KEY[0];
        return heading;
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

            message = p_serviceType + separator + p_scheduleBatchDetailVO.getMsisdn() + separator + PretupsBL.getDisplayAmount(p_scheduleBatchDetailVO.getAmount()) + separator + BTSLUtil.decryptText(p_channelUserVO.getUserPhoneVO().getSmsPin());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("getRequestMessage", "Exitin message formate =" + message);
            }
        }
        return message;
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
                uploadCorporateInternateFile(p_con, p_scheduleInfoMap, p_isErrorFound);
            }
        } catch (BTSLBaseException be) {
            _log.error("uploadFile", "Unable to write data " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("uploadFile", "Unable to write data " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("CorporateIATRechargeBatchFileParser", "uploadFile", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("uploadFile", "Exiting");
            }
        }
    }

    /**
     * Method downloadCorpInternateFile.
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
    private void downloadFileForCorporateBatch(Connection p_con, Writer p_fileWriter, HashMap p_scheduleMap) throws BTSLBaseException {
        // String
        // ntwrPrfxNotFound="MSISDN prefix not found OR Reciever country short name are not same";
        // String
        // countryCodeNotMatch="MSISDN does not start with defined country code in system";
        final String METHOD_NAME = "downloadFileForCorporateBatch";
        if (_log.isDebugEnabled()) {
            _log.debug("downloadFileForCorporateBatch", "Entered");
        }
        try {
            String userId = (String) p_scheduleMap.get("USER_ID");
            String ownerId = (String) p_scheduleMap.get("OWNER_ID");
            // String filteredMsisdn=null;
            // String msisdn=null;
            IATRestrictedSubscriberTxnDAO iatRestrictedSubscriberTxnDAO = new IATRestrictedSubscriberTxnDAO();
            HashMap hashMap = iatRestrictedSubscriberTxnDAO.loadIATRestrictedSubscriberList(p_con, userId, ownerId);
            p_scheduleMap.put("DATA_MAP", hashMap);
            String heading = (String) ((ArrayList) p_scheduleMap.get(HEADER_KEY)).get(0);
            ArrayList columnHeaderList = (ArrayList) p_scheduleMap.get(COLUMN_HEADER_KEY);
            for (int j = 0; j <= 5; j++) {
                if (j == 2) {
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
            RestrictedSubscriberVO iatRestrictedSubscriberVO = null;
            Iterator iterator = hashMap.keySet().iterator();
            String key = null;
            // IATCountryMasterVO masterCountryVO=null;
            // String []errorMsgArray=new
            // String[]{ntwrPrfxNotFound,countryCodeNotMatch};
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                iatRestrictedSubscriberVO = (RestrictedSubscriberVO) hashMap.get(key);
                /*
                 * countryCode=String.valueOf(iatRestrictedSubscriberVO.
                 * getCountryCode());
                 * msisdn=countryCode+iatRestrictedSubscriberVO.getMsisdn();
                 * ReceiverVO recieverVO=new ReceiverVO();
                 * try
                 * {
                 * masterCountryVO=IATCommonUtil.validateIATMsisdn(errorMsgArray,
                 * msisdn, recieverVO);
                 * if(masterCountryVO!=null)
                 * {
                 * msisdn=recieverVO.getMsisdn();
                 * filteredMsisdn=msisdn;
                 * }
                 * }
                 * catch(Exception e)
                 * {
                 * if(_log.isDebugEnabled())
                 * _log.debug("downloadFileForCorporateBatch","Not a valid MSISDN "
                 * +msisdn);
                 * 
                 * }
                 */
                p_fileWriter.write(iatRestrictedSubscriberVO.getCountryCode() + iatRestrictedSubscriberVO.getMsisdn() + ",");
                p_fileWriter.write(iatRestrictedSubscriberVO.getSubscriberID() + ",");
                p_fileWriter.write(BTSLUtil.NullToString(iatRestrictedSubscriberVO.getEmployeeName()) + ",");
                p_fileWriter.write(ServiceSelectorMappingCache.getDefaultSelectorForServiceType((String) p_scheduleMap.get(SERVICE_TYPE)).getSelectorCode() + ",");
                p_fileWriter.write(",");
                p_fileWriter.write("\n");
            }
            p_fileWriter.write("EOF,,,,,,,,,");

        } catch (BTSLBaseException be) {
            _log.error("downloadCorpInternateFile", "Unable to write data  " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("downloadCorpInternateFile", "Unable to write data " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("CorporateIATRechargeBatchFileParser", "downloadCorpInternateFile", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "downloadCorpInternateFile");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("downloadCorpInternateFile", "Exiting");
            }
        }
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
     * Method downloadCorporateIATFileForReschedule.
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
    private void downloadCorporateIATFileForReschedule(Connection p_con, Writer p_fileWriter, HashMap p_scheduleMap) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("downloadCorporateIATFileForReschedule", "Entered p_scheduleMap.size()= " + p_scheduleMap.size());
        }
        final String METHOD_NAME = "downloadCorporateIATFileForReschedule";
        try {
            RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
            LinkedHashMap hashMap = restrictedSubscriberDAO.loadScheduleBatchDetailsList(p_con, (String) (p_scheduleMap.get(BATCH_ID)), PretupsI.STATUS_EQUAL, PretupsI.SCHEDULE_STATUS_SCHEDULED);
            p_scheduleMap.put("DATA_MAP", hashMap);
            String heading = (String) ((ArrayList) p_scheduleMap.get(HEADER_KEY)).get(0);
            ArrayList columnHeaderList = (ArrayList) p_scheduleMap.get(COLUMN_HEADER_KEY);
            for (int j = 0; j <= 5; j++) {
                if (j == 2) {
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
                p_fileWriter.write(scheduleDetailVO.getSubService() + ",");
                p_fileWriter.write(PretupsBL.getDisplayAmount(scheduleDetailVO.getAmount()) + ",");
                p_fileWriter.write("\n");
            }
            p_fileWriter.write("EOF,,,,,,,");

        } catch (BTSLBaseException be) {
            _log.error("downloadCorporateIATFileForReschedule", "Unable to write data  " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("downloadCorporateIATFileForReschedule", "Unable to write data " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("CorporateIATRechargeBatchFileParser", "downloadCorporateIATFileForReschedule", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("downloadCorporateIATFileForReschedule", "Exiting");
            }
        }
    }

    /**
     * Method uploadCorporateInternateFile.
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
    public void uploadCorporateInternateFile(Connection p_con, HashMap p_scheduleInfoMap, boolean isErrorFound) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("uploadCorporateInternateFile", "Entered isErrorFound= " + isErrorFound + " p_scheduleInfoMap.size()= " + p_scheduleInfoMap.size() + p_scheduleInfoMap.get(SERVICE_TYPE) + " p_scheduleInfoMap.get(SERVICE_TYPE)" + p_scheduleInfoMap.get("FINAL_LIST_SIZE"));
        }
        final String METHOD_NAME = "uploadCorporateInternateFile";
        String filePathAndFileName = null;
        try {
            String dataStr = null;
            int countData = 0;
            RestrictedSubscriberVO errorVO = null;
            String arr[] = null;
            String filteredMsisdn = null;
            String msisdn = null;
            String tmpSubService = null;
            String subService = null;
            String tmpAmt = null;
            String countryCodeMsisdn = null;
            long reqAmt = 0;
            String errorKey = null;
            String requestFor = null;
            String ownerID = null;
            String userID = null;
            String batchID = null;
            String createdBy = null;
            Date modifiedOn = null;
            ScheduleBatchMasterVO scheduleBatchMasterVO = null;
            RestrictedSubscriberVO iatRestrictedSubscriberVO = null;
            ScheduleBatchDetailVO scheduleDetailVO = null;
            IATCountryMasterVO masterCountryVO = null;

            HashMap scheduleInfoMap = p_scheduleInfoMap;

            ArrayList finalList = (ArrayList) scheduleInfoMap.get("FINAL_LIST");
            int finalListSize = Integer.parseInt(scheduleInfoMap.get("FINAL_LIST_SIZE").toString());
            UserVO userVO = (UserVO) scheduleInfoMap.get("USER_VO");
            String filePath = Constants.getProperty("UploadRestrictedMSISDNFilePath");
            filePathAndFileName = filePath + (String) scheduleInfoMap.get("FILE_NAME");

            // get error keys from hashmap for different validations
            String[] errorUploadKeys = (String[]) scheduleInfoMap.get(ERROR_KEY);
            String invalidReceiverMSISDN = errorUploadKeys[0];
            String subServiceNotFound = errorUploadKeys[1];
            String reqAmtNotFound = errorUploadKeys[2];
            String subServiceInvalid = errorUploadKeys[3];
            String reqAmtInvalid = errorUploadKeys[4];
            String amtNotinRange = errorUploadKeys[5];
            String informaiotnNotFound = errorUploadKeys[9];
            String ntwrPrfxNotFound = errorUploadKeys[10];
            String countryCodeNotMatch = errorUploadKeys[11];
            String definedSubServiceValue = (ServiceSelectorMappingCache.getDefaultSelectorForServiceType((String) p_scheduleInfoMap.get(SERVICE_TYPE)).getSelectorCode());
            /*
             * If user does not click on the download link and upload file form
             * his own then load data form the
             * database for the checking purpose
             */
            HashMap dataMap = (HashMap) scheduleInfoMap.get(DATA_MAP);
            requestFor = (String) scheduleInfoMap.get("REQUEST_FOR");
            ownerID = (String) scheduleInfoMap.get(OWNER_ID);
            userID = (String) scheduleInfoMap.get(USER_ID);
            batchID = (String) scheduleInfoMap.get(BATCH_ID);
            createdBy = (String) scheduleInfoMap.get("CREATED_BY");
            String downloadedBatchID = (String) scheduleInfoMap.get("DOWNLOAD_BATCH_ID");
            scheduleBatchMasterVO = (ScheduleBatchMasterVO) scheduleInfoMap.get("SCHEDULED_VO");
            modifiedOn = (Date) scheduleInfoMap.get("MODIFIED_ON");
            String[] errorMsgArray = new String[] { ntwrPrfxNotFound, countryCodeNotMatch };
            IATRestrictedSubscriberDAO iatRestrictedSubscriberDAO = new IATRestrictedSubscriberDAO();
            IATRestrictedSubscriberTxnDAO iatRestrictedSubscriberTxnDAO = new IATRestrictedSubscriberTxnDAO();
            ScheduledBatchDetailDAO scheduledBatchDetailDAO = new ScheduledBatchDetailDAO();
            if ("Schedule".equals(requestFor)) {
                errorKey = errorUploadKeys[6];
                if (dataMap == null || dataMap.isEmpty()) {
                    dataMap = iatRestrictedSubscriberTxnDAO.loadIATRestrictedSubscriberList(p_con, userID, ownerID);
                }
            } else if ("Reschedule".equals(requestFor)) {
                errorKey = errorUploadKeys[7];
                if (dataMap == null || dataMap.isEmpty() || !downloadedBatchID.equals(batchID)) {
                    dataMap = iatRestrictedSubscriberDAO.loadScheduleBatchDetailsList(p_con, batchID, PretupsI.STATUS_EQUAL, PretupsI.SCHEDULE_STATUS_CANCELED);
                }
            }
            ArrayList<ScheduleBatchDetailVO> validDataList = new ArrayList<ScheduleBatchDetailVO>();
            ReceiverVO recieverVO = new ReceiverVO();
            while (finalListSize != countData) {
                errorVO = (RestrictedSubscriberVO) finalList.get(countData);
                dataStr = errorVO.getMsisdn();

                if (_log.isDebugEnabled()) {
                    _log.debug("uploadCorporateInternateFile", "Processing starts for  " + dataStr);
                }
                countData++;
                if (errorVO.getErrorCode() == null) {
                    arr = dataStr.split(",");
                    try {
                        if (BTSLUtil.isNullString(arr[0])) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("uploadCorporateInternateFile", "Receiver MSISDN is Null " + arr[0]);
                            }
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Receiver msisdn not found", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(invalidReceiverMSISDN);
                            errorVO.setMsisdn(arr[0]);
                            isErrorFound = true;
                            continue;
                        } else {
                            msisdn = arr[0];
                            masterCountryVO = IATCommonUtil.validateIATMsisdn(errorMsgArray, msisdn, recieverVO);
                            if (!recieverVO.isErrorMsgFound()) {
                                filteredMsisdn = recieverVO.getMsisdn();
                                // MSISDN with country Code For Scheduling Entry
                                countryCodeMsisdn = masterCountryVO.getRecCountryCode() + filteredMsisdn;
                            } else {
                                if (!BTSLUtil.isNullString(recieverVO.getNtworkErrorMsg())) {
                                    errorVO.setMsisdn(arr[0]);
                                    errorVO.setErrorCode(ntwrPrfxNotFound);
                                    isErrorFound = true;
                                    ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Country Short name does not match OR NtwrkPrefix Not Found", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                                    continue;
                                }
                                if (!BTSLUtil.isNullString(recieverVO.getCountryCodeMatchError())) {
                                    errorVO.setMsisdn(arr[0]);
                                    errorVO.setErrorCode(countryCodeNotMatch);
                                    isErrorFound = true;
                                    ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Country Code Not Found", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                                    continue;
                                }
                            }
                        }
                    } catch (Exception e) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadCorporateInternateFile", "Not a valid MSISDN " + arr[0]);
                        }
                        _log.errorTrace(METHOD_NAME, e);
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid MSISDN", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(invalidReceiverMSISDN);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    /*
                     * Validating sub-service type
                     */
                    try {
                        tmpSubService = null;
                        tmpSubService = arr[3];
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
                            _log.debug("uploadCorporateInternateFile", "Not a valid sub-service = " + tmpSubService);
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
                    /*
                     * Validating amount entered by user
                     */
                    try {
                        tmpAmt = null;
                        tmpAmt = arr[4];
                        reqAmt = PretupsBL.getSystemAmount(tmpAmt);
                    } catch (Exception e) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadCorporateInternateFile", "Not a valid requested amount,tmpAmt: " + tmpAmt);
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
                    if ("Schedule".equals(requestFor)) {
                        iatRestrictedSubscriberVO = (RestrictedSubscriberVO) dataMap.get(filteredMsisdn);
                    } else if ("Reschedule".equals(requestFor)) {
                        iatRestrictedSubscriberVO = (RestrictedSubscriberVO) dataMap.get(msisdn);
                    }
                    if (iatRestrictedSubscriberVO == null) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadCorporateInternateFile", "No informaiton found for = " + arr[0]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "No informaiton found for = " + arr[0], "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(informaiotnNotFound);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    if (reqAmt < iatRestrictedSubscriberVO.getMinTxnAmount() || reqAmt > iatRestrictedSubscriberVO.getMaxTxnAmount()) {
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadCorporateInternateFile", "Requested amount is not in between minimum amount and maximum amount= " + arr[4]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Requested amount is not in between minimum amount and maximum amount= " + arr[4], "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCodeArgs(new String[] { arr[4] });
                        errorVO.setErrorCode(amtNotinRange);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }

                    scheduleDetailVO = new ScheduleBatchDetailVO();
                    scheduleDetailVO.setBatchID(scheduleBatchMasterVO.getBatchID());
                    scheduleDetailVO.setAmount(reqAmt);
                    scheduleDetailVO.setSubService(subService);
                    scheduleDetailVO.setMsisdn(countryCodeMsisdn);
                    scheduleDetailVO.setSubscriberID(iatRestrictedSubscriberVO.getSubscriberID());
                    scheduleDetailVO.setStatus(PretupsI.SCHEDULE_STATUS_SCHEDULED);
                    scheduleDetailVO.setModifiedBy(userVO.getActiveUserID());
                    scheduleDetailVO.setModifiedOn(modifiedOn);
                    scheduleDetailVO.setCreatedBy(userVO.getActiveUserID());
                    scheduleDetailVO.setCreatedOn((Date) scheduleInfoMap.get("CREATED_ON"));
                    scheduleDetailVO.setLineNumber(errorVO.getLineNumber());
                    scheduleDetailVO.setLanguage(iatRestrictedSubscriberVO.getLanguage());
                    scheduleDetailVO.setCountry(iatRestrictedSubscriberVO.getCountry());
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
                    _log.debug("uploadCorporateInternateFile", "No valid Data in the file, size :" + validDataList.size());
                }
                throw new BTSLBaseException(this, "uploadCorporateInternateFile", errorKey, "showMsg");
            }
            /*
             * Now form the returnData string get the msisdns which are
             * associated with the user.
             * Since it may not be associtaed with any user. If not in the
             * Associated State
             * Remove the MSISDN from the list.
             */
            String returnData = iatRestrictedSubscriberDAO.isIATSubscriberExistByStatus(p_con, ownerID, validDataList, PretupsI.STATUS_EQUAL, PretupsI.RES_MSISDN_STATUS_ASSOCIATED, null);

            if (!BTSLUtil.isNullString(returnData)) {
                ArrayList<ScheduleBatchDetailVO> newValidDataList = new ArrayList<ScheduleBatchDetailVO>();
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
                String notAssociatedMSISDN = errorUploadKeys[6];

                for (int m = 0, n = validDataList.size(); m < n; m++) {
                    scheduleDetailVO = (ScheduleBatchDetailVO) validDataList.get(m);
                    if (_log.isDebugEnabled()) {
                        _log.debug("uploadCorporateInternateFile", "Mobile number is not associated (Active)= " + scheduleDetailVO.getMsisdn());
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
                        _log.debug("uploadCorporateInternateFile", "No valid Data in the list, size :" + newValidDataList.size());
                    }
                    throw new BTSLBaseException(this, "uploadCorporateInternateFile", errorKey, "showMsg");
                }
                validDataList = null;
                validDataList = newValidDataList;
            }

            // Now validate all the data for the status of the schedule.
            String statusStr = "'" + PretupsI.SCHEDULE_STATUS_CANCELED + "','" + PretupsI.SCHEDULE_STATUS_EXECUTED + "'";
            String statusMode = PretupsI.STATUS_NOTIN;
            String logMessage = "Mobile number is already scheduled or canceled";

            returnData = scheduledBatchDetailDAO.isScheduleExistByStatus(p_con, validDataList, statusMode, statusStr, ownerID, modifiedOn);
            /*
             * Now from the returnData string get the msisdns (which is already
             * scheduled or canceled status)
             * and remove them for the arrayList for further processing.
             */
            if (!BTSLUtil.isNullString(returnData)) {
                String alreadyScheduled = errorUploadKeys[7];
                arr = returnData.split(",");
                for (int i = 0, j = arr.length; i < j; i++) {
                    filteredMsisdn = arr[i];
                    for (int m = 0, n = validDataList.size(); m < n; m++) {
                        scheduleDetailVO = (ScheduleBatchDetailVO) validDataList.get(m);
                        if (scheduleDetailVO.getMsisdn().equals(filteredMsisdn)) {
                            validDataList.remove(m--);
                            n--;
                            if (_log.isDebugEnabled()) {
                                _log.debug("uploadCorporateInternateFile", logMessage + "=" + filteredMsisdn);
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
                /*
                 * If all the data form the list is removed due to invalid data
                 * then show the
                 * error message on the common jsp and show the error string
                 */
                if (validDataList.isEmpty()) {
                    scheduleBatchMasterVO.setUploadFailedCount(countData);
                    if (_log.isDebugEnabled()) {
                        _log.debug("uploadCorporateInternateFile", "No valid Data in the list, size :" + validDataList.size());
                    }
                    throw new BTSLBaseException(this, "uploadCorporateInternateFile", errorKey, "showMsg");
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
                    _log.error("uploadCorporateInternateFile", "Scheduling file for the service recharge is unsuccessful");
                    throw new BTSLBaseException(this, "uploadCorporateInternateFile", errorUploadKeys[8], "scheduleTopUpAuthorise");
                }
            } else {
                p_con.rollback();
                _log.error("uploadCorporateInternateFile", "Scheduling file for the service recharge is unsuccessful");
                throw new BTSLBaseException(this, "uploadCorporateInternateFile", errorUploadKeys[8], "scheduleTopUpAuthorise");
            }
        } catch (BTSLBaseException be) {
            _log.error("uploadCorporateInternateFile", "Unable to write data  " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("uploadCorporateInternateFile", "Scheduling file for the service recharge is unsuccessful " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("CorporateIATRechargeBatchFileParser", "uploadCorporateInternateFile", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("uploadCorporateInternateFile", "Exiting");
            }
        }
    }

	@Override
	public void downloadFileForResheduleRest(Connection p_con, Writer p_fileWriter, String p_fileType,
			HashMap p_scheduleInfoMap) throws BTSLBaseException {
		// TODO Auto-generated method stub
		
	}
}
