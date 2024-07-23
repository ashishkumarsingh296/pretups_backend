package com.btsl.pretups.scheduletopup.process;

/**
 * @# RechargeBatchFileParser
 * 
 *    Created by Created on History
 *    --------------------------------------------------------------------------
 *    ------
 *    ved.sharma 19/05/08 Initial creation
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2008 Bharti Telesoft Ltd.
 * 
 * 
 */

import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.ScheduleFileProcessLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
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
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingCache;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.txn.pretups.restrictedsubs.businesslogic.RestrictedSubscriberTxnDAO;

/**
 * this class is used to schedule file for service type RC- recharge
 **/
public class RechargeBatchFileParser implements BatchFileParserI {
    public static final Log _log = LogFactory.getLog(RechargeBatchFileParser.class.getName());

    /**
     * Method downloadFile.
     * This method is called to download file at the time of scheduling the file
     * 
     * @param p_con
     *            Connection
     * @param p_fileWriter
     *            Writer
     * @param p_scheduleInfoMap
     *            HashMap
     * @throws BTSLBaseException
     */
    @Override
    public void downloadFile(Connection p_con, Writer p_fileWriter, String p_fileType, HashMap p_scheduleInfoMap) throws BTSLBaseException {
    	 final String methodname = "downloadFile";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodname, "Entered p_fileType= " + p_fileType);
        }
       
        try {
            if (PretupsI.BATCH_TYPE_CORPORATE.equals(p_fileType)) {
                downloadCorpFile(p_con, p_fileWriter, p_scheduleInfoMap);
            } else {
                downloadNormalFile(p_fileWriter, p_scheduleInfoMap);
            }
        } catch (BTSLBaseException be) {
            _log.error(methodname, "Unable to write data " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodname, e);
            _log.error(methodname, "Unable to write data " + e.getMessage());
            throw new BTSLBaseException("RechargeBatchFileParser", "downloadFile", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
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
     * @param p_scheduleInfoMap
     *            HashMap
     * @throws BTSLBaseException
     */
    @Override
    public void downloadFileForReshedule(Connection p_con, Writer p_fileWriter, String p_fileType, HashMap p_scheduleInfoMap) throws BTSLBaseException {
    	 final String methodname = "downloadFileForReshedule";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodname, "Entered p_fileType= " + p_fileType);
        }
       
        try {
            if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(p_fileType)) {
                downloadCorporateFileForReschedule(p_con, p_fileWriter, p_scheduleInfoMap);
            } else {
                downloadNormalFileForReschedule(p_con, p_fileWriter, p_scheduleInfoMap);
            }
        } catch (BTSLBaseException be) {

            _log.error(methodname, "Unable to write data " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodname, e);
            _log.error(methodname, "Unable to write data " + e.getMessage());
            throw new BTSLBaseException("RechargeBatchFileParser", "downloadFileForReshedule", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodname, "Exiting");
            }
        }

    }

    /**
     * Method uploadFile.
     * This method is called to upload file
     * 
     * @param p_con
     *            Connection
     * @param p_fileWriter
     *            Writer
     * @param p_scheduleInfoMap
     *            HashMap
     * @param p_isErrorFound
     *            boolean
     * @throws BTSLBaseException
     */
    @Override
    public void uploadFile(Connection p_con, String p_fileType, HashMap p_scheduleInfoMap, boolean p_isErrorFound) throws BTSLBaseException {
    	 final String methodname = "uploadFile";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodname, "Entered p_fileType= " + p_fileType + " p_isErrorFound= " + p_isErrorFound);
        }
       
        try {
        	
        	String batch_type =    (String) p_scheduleInfoMap.get("BATCH_TYPE");
        	 if(BTSLUtil.isEmpty(batch_type )) {
        		 batch_type=PretupsI.BATCH_TYPE_CORPORATE;
        	 }
        	
            if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(batch_type)) {
                uploadCorporateFile(p_con, p_scheduleInfoMap, p_isErrorFound);
            } else {
                uploadNormalFile(p_con, p_scheduleInfoMap, p_isErrorFound);
            }
        } catch (BTSLBaseException be) {
            _log.error(methodname, "Unable to write data " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error(methodname, "Unable to write data " + e.getMessage());
            _log.errorTrace(methodname, e);
            throw new BTSLBaseException("RechargeBatchFileParser", methodname, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodname, "Exiting");
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
     * @param p_scheduleInfoMap
     *            HashMap
     * @throws BTSLBaseException
     */
    private void downloadCorpFile(Connection p_con, Writer p_fileWriter, HashMap p_scheduleInfoMap) throws BTSLBaseException {
    	   final String methodname = "downloadCorpFile";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodname, "Entered");
        }
     
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
            RestrictedSubscriberVO restrictedSubscriberVO;
            Iterator iterator = hashMap.keySet().iterator();
            String key;
            boolean msisdnAllowed;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                restrictedSubscriberVO = (RestrictedSubscriberVO) hashMap.get(key);
                msisdnAllowed = isAllowedMsisdn(restrictedSubscriberVO.getMsisdn(),null);
                if (msisdnAllowed) {
                    p_fileWriter.write(restrictedSubscriberVO.getMsisdn() + ",");
                    p_fileWriter.write(restrictedSubscriberVO.getSubscriberID() + ",");
                    p_fileWriter.write(BTSLUtil.NullToString(restrictedSubscriberVO.getEmployeeName()) + ",");
                    p_fileWriter.write(PretupsBL.getDisplayAmount(restrictedSubscriberVO.getMinTxnAmount()) + ",");
                    p_fileWriter.write(PretupsBL.getDisplayAmount(restrictedSubscriberVO.getMaxTxnAmount()) + ",");
                    p_fileWriter.write(PretupsBL.getDisplayAmount(restrictedSubscriberVO.getMonthlyLimit()) + ",");
                    p_fileWriter.write(PretupsBL.getDisplayAmount(restrictedSubscriberVO.getTotalTransferAmount()) + ",");
                    p_fileWriter.write(ServiceSelectorMappingCache.getDefaultSelectorForServiceType((String) p_scheduleInfoMap.get(SERVICE_TYPE)).getSelectorCode()); // for
                                                                                                                                                                      // default
                                                                                                                                                                      // value
                                                                                                                                                                      // of
                                                                                                                                                                      // sub-service
                    p_fileWriter.write("\n");
                }
            }
            p_fileWriter.write("eof,,,,,,,");

        } catch (BTSLBaseException be) {
            _log.error(methodname, "Unable to write data " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodname, e);
            _log.error(methodname, "Unable to write data " + e.getMessage());
            throw new BTSLBaseException("RechargeBatchFileParser", methodname, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodname, "Exiting");
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
    	 final String methodname = "downloadNormalFile";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodname, "Entered");
        }
       
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
            _log.error(methodname, "Unable to write data " + ex.getMessage());
            _log.errorTrace(methodname, ex);
            throw new BTSLBaseException("RechargeBatchFileParser", methodname, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodname, "Exiting");
            }

        }
    }

    /**
     * Method downloadCorporateFileForReschedule.
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
    private void downloadCorporateFileForReschedule(Connection p_con, Writer p_fileWriter, HashMap p_scheduleInfoMap) throws BTSLBaseException {
    	final String methodname = "downloadCorporateFileForReschedule";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodname, "Entered p_scheduleInfoMap.size()= " + p_scheduleInfoMap.size());
        }
        
        try {
            RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
            LinkedHashMap hashMap = restrictedSubscriberDAO.loadScheduleBatchDetailsList(p_con, (String) (p_scheduleInfoMap.get(BATCH_ID)), PretupsI.STATUS_EQUAL, PretupsI.SCHEDULE_STATUS_SCHEDULED);
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
                p_fileWriter.write((String) (columnHeaderList.get(i)) + ",");
            }
            p_fileWriter.write("\n");
            // / Iterating thru the Hashmap object and writing the actual data
            // to the PrintWriter object ///
            ScheduleBatchDetailVO scheduleDetailVO;
            Iterator iterator = hashMap.keySet().iterator();
            String key;
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
                p_fileWriter.write(PretupsBL.getDisplayAmount(scheduleDetailVO.getAmount()));
                p_fileWriter.write("\n");
            }
            p_fileWriter.write("eof,,,,,,,");

        } catch (BTSLBaseException be) {
            _log.error(methodname, "Unable to write data  " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error(methodname, "Unable to write data " + e.getMessage());
            _log.errorTrace(methodname, e);
            throw new BTSLBaseException("RechargeBatchFileParser", methodname, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodname, "Exiting");
            }
        }
    }
    
    /**
     * Method downloadCorporateFileForRescheduleRest.
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
    private void downloadCorporateFileForRescheduleRest(Connection p_con, Writer p_fileWriter, HashMap p_scheduleInfoMap) throws BTSLBaseException {
    	final String methodname = "downloadCorporateFileForRescheduleRest";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodname, "Entered p_scheduleInfoMap.size()= " + p_scheduleInfoMap.size());
        }
        
        try {
            RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
            LinkedHashMap hashMap = restrictedSubscriberDAO.loadScheduleBatchDetailsList(p_con, (String) (p_scheduleInfoMap.get(BATCH_ID)), PretupsI.STATUS_EQUAL, PretupsI.SCHEDULE_STATUS_SCHEDULED);
            p_scheduleInfoMap.put("DATA_MAP", hashMap);
            String heading = (String) ((ArrayList) p_scheduleInfoMap.get(HEADER_KEY)).get(0);
            ArrayList columnHeaderList = (ArrayList) p_scheduleInfoMap.get(COLUMN_HEADER_KEY);
            p_fileWriter.write("," + heading + ",");
            p_fileWriter.write("\n");
			
            for (int i = 0, j = columnHeaderList.size(); i < j; i++) {
                if(i  == j-1) {
                 p_fileWriter.write((String) (columnHeaderList.get(i)));
                }else {
                p_fileWriter.write((String) (columnHeaderList.get(i)) + ",");
               }
            }
            p_fileWriter.write("\n");
            // / Iterating thru the Hashmap object and writing the actual data
            // to the PrintWriter object ///
            ScheduleBatchDetailVO scheduleDetailVO;
            Iterator iterator = hashMap.keySet().iterator();
            String key;
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
                p_fileWriter.write(PretupsBL.getDisplayAmount(scheduleDetailVO.getAmount()));
                p_fileWriter.write("\n");
            }
           // p_fileWriter.write("eof,,,,,,,");

        } catch (BTSLBaseException be) {
            _log.error(methodname, "Unable to write data  " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error(methodname, "Unable to write data " + e.getMessage());
            _log.errorTrace(methodname, e);
            throw new BTSLBaseException("RechargeBatchFileParser", methodname, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodname, "Exiting");
            }
        }
    }
    

    /**
     * Method downloadNormalFileForReschedule.
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
    private void downloadNormalFileForReschedule(Connection p_con, Writer p_fileWriter, HashMap p_scheduleInfoMap) throws BTSLBaseException {
    	 final String methodname = "downloadNormalFileForReschedule";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodname, "Entered p_scheduleInfoMap.size()" + p_scheduleInfoMap.size());
        }
       
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
                p_fileWriter.write(LocaleMasterCache.getLocaleDetailsFromlocale(new Locale(scheduleBatchDetailVO.getLanguage(), scheduleBatchDetailVO.getCountry())).getLanguage_code());
                p_fileWriter.write("\n");
            }
            p_fileWriter.write("eof,,,,\n");
            p_fileWriter.write("\n");
        } catch (BTSLBaseException be) {
            _log.error(methodname, "Unable to write data  " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error(methodname, "Unable to write data " + e.getMessage());
            _log.errorTrace(methodname, e);
            throw new BTSLBaseException("RechargeBatchFileParser", methodname, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodname, "Exiting");
            }
        }
    }
    
    
    /**
     * Method downloadNormalFileForRescheduleRest.
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
    private void downloadNormalFileForRescheduleRest(Connection p_con, Writer p_fileWriter, HashMap p_scheduleInfoMap) throws BTSLBaseException {
    	 final String methodname = "downloadNormalFileForRescheduleRest";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodname, "Entered p_scheduleInfoMap.size()" + p_scheduleInfoMap.size());
        }
       
        try {
            RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
            ArrayList list = restrictedSubscriberDAO.loadBatchDetailVOList(p_con, (String) p_scheduleInfoMap.get(BATCH_ID), PretupsI.STATUS_EQUAL, PretupsI.SCHEDULE_STATUS_SCHEDULED);

            String heading = (String) ((ArrayList) p_scheduleInfoMap.get(HEADER_KEY)).get(0);
            ArrayList columnHeaderList = (ArrayList) p_scheduleInfoMap.get(COLUMN_HEADER_KEY);
            p_fileWriter.write("," + heading + ",");
            p_fileWriter.write("\n");
            for (int i = 0, j = columnHeaderList.size(); i < j; i++) {
            	if(i == j-1) {
            	p_fileWriter.write((String) columnHeaderList.get(i));
            	}else {
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
                p_fileWriter.write(LocaleMasterCache.getLocaleDetailsFromlocale(new Locale(scheduleBatchDetailVO.getLanguage(), scheduleBatchDetailVO.getCountry())).getLanguage_code());
                p_fileWriter.write("\n");
            }
            //p_fileWriter.write("eof,,,,\n");
            //p_fileWriter.write("\n");
        } catch (BTSLBaseException be) {
            _log.error(methodname, "Unable to write data  " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error(methodname, "Unable to write data " + e.getMessage());
            _log.errorTrace(methodname, e);
            throw new BTSLBaseException("RechargeBatchFileParser", methodname, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodname, "Exiting");
            }
        }
    }
    
    
    
    

    /**
     * Method uploadCorporateFile.
     * This method is called to upload the RestrictedUser's file for corporate
     * file type
     * 
     * @param p_con
     *            Connection
     * @param p_scheduleInfoMap
     *            HashMap
     * @param isErrorFound
     *            boolean
     * @throws BTSLBaseException
     */
    private void uploadCorporateFile(Connection p_con, HashMap p_scheduleInfoMap, boolean isErrorFound) throws BTSLBaseException {
    	final String methodname = "uploadCorporateFile";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodname, "Entered isErrorFound= " + isErrorFound + " p_scheduleInfoMap.size()= " + p_scheduleInfoMap.size());
        }
        
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
            HashMap scheduleInfoMap = p_scheduleInfoMap;
            int temp = 0;

            ArrayList finalList = (ArrayList) scheduleInfoMap.get("FINAL_LIST");
            int finalListSize = Integer.parseInt(scheduleInfoMap.get("FINAL_LIST_SIZE").toString());
            UserVO userVO = (UserVO) scheduleInfoMap.get("USER_VO");
            String filePath = Constants.getProperty("UploadRestrictedMSISDNFilePath");
            filePathAndFileName = filePath + (String) scheduleInfoMap.get("FILE_NAME");

            // get error keys from hashmap for different validations
            String[] errorUploadKeys = (String[]) scheduleInfoMap.get(ERROR_KEY);
            String invalidMSISDN = errorUploadKeys[0];
            String prefixNotFound = errorUploadKeys[1];
            String networkNotSupported = errorUploadKeys[2];
            String informaiotnNotFound = errorUploadKeys[3];
            String subServiceNotFound = errorUploadKeys[4];
            String reqAmtNotFound = errorUploadKeys[5];
            String subServiceInvalid = errorUploadKeys[6];
            String reqAmtInvalid = errorUploadKeys[7];
            String amtNotinRange = errorUploadKeys[8];

            String definedSubServiceValue = Constants.getProperty("DEFINED_SUB_SERVICE_VALUE");

            // If user does not click on the down load link and upload file form
            // his own then load data form the
            // database for the checking purpose
            // /
            HashMap downLoadDataMap = (HashMap) scheduleInfoMap.get(DATA_MAP);

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
            if ("Schedule".equalsIgnoreCase(requestFor)) {
                errorKey = errorUploadKeys[11];
                if (downLoadDataMap == null || downLoadDataMap.isEmpty()) {
                    downLoadDataMap = restrictedSubscriberTxnDAO.loadRestrictedSubscriberList(p_con, userID, ownerID);
                }
            } else if ("Reschedule".equalsIgnoreCase(requestFor)) {
                errorKey = errorUploadKeys[12];
                if (downLoadDataMap == null || downLoadDataMap.isEmpty() || !downloadedBatchID.equals(batchID)) {
                    downLoadDataMap = restrictedSubscriberDAO.loadScheduleBatchDetailsList(p_con, batchID, PretupsI.STATUS_EQUAL, PretupsI.SCHEDULE_STATUS_CANCELED);
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
                    _log.debug(methodname, "Processing starts for  " + dataStr);
                }
                countData++;
                if (errorVO.getErrorCode() == null) {
                    arr = dataStr.split(",");
                    // check for first record
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
                        // check file type
                        if (arrLength < 8) {
                            isErrorFound = true;
                            scheduleInfoMap.put("IS_ERROR_FOUND", new Boolean(isErrorFound));
                            scheduleInfoMap.put("IS_FILE_TYPE_DIFF", errorUploadKeys[13]);
                            return;
                        }
                    }

                    // check for valid MSISDN
                    try {
                        // if receiver msisdn is null
                        if (BTSLUtil.isNullString(arr[0])) {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Receiver msisdn not found", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(errorUploadKeys[15]);
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
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodname, "Not a valid MSISDN " + arr[0]);
                        }
                        _log.errorTrace(methodname, e);
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid MSISDN", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
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
                        if (_log.isDebugEnabled()) {
                            _log.debug("uploadCorporateFile", "Not a valid MSISDN " + arr[0]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid MSISDN", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
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
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodname, "Not Network prefix found " + arr[0]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not Network prefix found", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(prefixNotFound);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    // check network support of the MSISDN
                    networkCode = networkPrefixVO.getNetworkCode();
                    if (!networkCode.equals(userVO.getNetworkID())) {
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodname, "Not supporting Network" + arr[0]);
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
                            _log.debug("uploadCorporateFile", "Not a valid Receiver Msisdn= " + filteredMsisdn);
                        }
                        _log.errorTrace(methodname, e);
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Entered number is Not a valid GSM number ", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
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
                            _log.debug(methodname, "Not a valid sub-service = " + tmpSubService);
                        }
                        _log.errorTrace(methodname, e);
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
                            _log.debug(methodname, "Not a valid requested amount tmpAmt: " + tmpAmt);
                        }
                        _log.errorTrace(methodname, e);
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

                    restrictedSubscriberVO = (RestrictedSubscriberVO) downLoadDataMap.get(filteredMsisdn);
                    if (restrictedSubscriberVO == null) {
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodname, "No informaiton found for = " + arr[0]);
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
                            _log.debug(methodname, "Requested amount is not in between minimum amount and maximum amount= " + arr[8]);
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
                    scheduleDetailVO.setLanguage(language);
                    scheduleDetailVO.setCountry(country);

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
            scheduleInfoMap.put("PROCESSED_RECS", "0");
            scheduleInfoMap.put("IS_ERROR_FOUND", new Boolean(isErrorFound));
            if (validDataList.isEmpty()) {
                scheduleInfoMap.put("FINAL_LIST", finalList);
                scheduleBatchMasterVO.setUploadFailedCount(countData);
                if (_log.isDebugEnabled()) {
                    _log.debug("uploadCorporateFile", "No valid Data in the file, size :" + validDataList.size());
                }
                throw new BTSLBaseException(this, methodname, errorKey, "showMsg");
            }

            // /
            // Now check the mobile numbers by the subscriber's status and their
            // already schedule or underprocess
            // /
            String returnData = restrictedSubscriberDAO.isSubscriberExistByStatus(p_con, ownerID, validDataList, PretupsI.STATUS_EQUAL, PretupsI.RES_MSISDN_STATUS_ASSOCIATED, null);
            // /
            // Now form the returnData string get the msisdns which are
            // associated with the user (since it may
            // deleted or suspended during the processing)and keep then in the
            // new list for further processing.
            // /

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
                        _log.debug(methodname, "Mobile number is not associated (Active)= " + scheduleDetailVO.getMsisdn());
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
                // /
                // If all the data form the list is removed due to invalid data
                // then show the error message on the
                // common jsp and show the error string
                // /
                if (newValidDataList.isEmpty()) {
                    scheduleInfoMap.put("FINAL_LIST", finalList);
                    scheduleBatchMasterVO.setUploadFailedCount(countData);
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodname, "No valid Data in the list, size :" + newValidDataList.size());
                    }
                    throw new BTSLBaseException(this, methodname, errorKey, "showMsg");
                }
                validDataList = null;
                validDataList = newValidDataList;
            }

            // Now validate all the data for the status of the schedule.
            String statusStr = "'" + PretupsI.SCHEDULE_STATUS_CANCELED + "','" + PretupsI.SCHEDULE_STATUS_EXECUTED + "'";
            String statusMode = PretupsI.STATUS_NOTIN;
            String logMessage = "Mobile number is already scheduled or underprodess";

            returnData = scheduledBatchDetailDAO.isScheduleExistByStatus(p_con, validDataList, statusMode, statusStr, ownerID, modifiedOn);

            // /
            // Now form the returnData string get the msisdns and remove
            // them for the arrayList for further processing.
            // /
            if (!BTSLUtil.isNullString(returnData)) {
                String alreadScheduled = errorUploadKeys[10];
                arr = returnData.split(",");
                for (int i = 0, j = arr.length; i < j; i++) {
                    filteredMsisdn = arr[i];
                    for (int m = 0, n = validDataList.size(); m < n; m++) {
                        scheduleDetailVO = (ScheduleBatchDetailVO) validDataList.get(m);
                        if (scheduleDetailVO.getMsisdn().equals(filteredMsisdn)) {
                            validDataList.remove(m--);
                            n--;
                            if (_log.isDebugEnabled()) {
                                _log.debug("uploadCorporateFile", logMessage + "=" + filteredMsisdn);
                            }
                            ScheduleFileProcessLog.log("Checking Data in Database", createdBy, filteredMsisdn, batchID, logMessage, "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO = new RestrictedSubscriberVO();
                            errorVO.setLineNumber(scheduleDetailVO.getLineNumber());
                            errorVO.setErrorCode(alreadScheduled);
                            errorVO.setMsisdn(filteredMsisdn);
                            finalList.add(errorVO);
                            isErrorFound = true;
                            break;
                        }
                    }
                }
                scheduleInfoMap.put("IS_ERROR_FOUND", new Boolean(isErrorFound));
                scheduleInfoMap.put("FINAL_LIST", finalList);
                // /
                // If all the data form the list is removed due to invalid data
                // then show the error message on the
                // common jsp and show the error string
                // /
                if (validDataList.isEmpty()) {
                    scheduleBatchMasterVO.setUploadFailedCount(countData);
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodname, "No valid Data in the list, size :" + validDataList.size());
                    }
                    throw new BTSLBaseException(this, methodname, errorKey, "showMsg");
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
                    _log.error(methodname, "Scheduling file for the service recharge is unsuccessful");
                    throw new BTSLBaseException(this, methodname, errorUploadKeys[14], "scheduleTopUpAuthorise");
                }
            } else {
                p_con.rollback();
                _log.error(methodname, "Scheduling file for the service recharge is unsuccessful");
                throw new BTSLBaseException(this, methodname, errorUploadKeys[14], "scheduleTopUpAuthorise");
            }
        } catch (BTSLBaseException be) {
            _log.error(methodname, "Unable to write data  " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error(methodname, "Scheduling file for the service recharge is unsuccessful " + e.getMessage());
            _log.errorTrace(methodname, e);
            throw new BTSLBaseException("RechargeBatchFileParser", methodname, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodname, "Exiting");
            }
        }
    }

    /**
     * Method uploadNormalFile.
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
    private void uploadNormalFile(Connection p_con, HashMap p_scheduleInfoMap, boolean isErrorFound) throws BTSLBaseException {
        final String methodname = "uploadNormalFile";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodname, "Entered isErrorFound= " + isErrorFound + " p_scheduleInfoMap.size()= " + p_scheduleInfoMap.size());
        }
   
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
            String reqAmtNotFound = errorUploadKeys[4];
            String subServiceInvalid = errorUploadKeys[5];
            String reqAmtInvalid = errorUploadKeys[6];
            String languageCodeInvalid = errorUploadKeys[8];

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
                    _log.debug("uploadNormalFile", "Processing starts for  " + dataStr);
                }
                countData++;
                if (errorVO.getErrorCode() == null) {
                    arr = dataStr.split(",");
                    if (temp == 0) {
                        int arrLength = 0;
                        int ind;
                        for (int i = 0; i < 9; i++) {
                            ind = dataStr.indexOf(',');
                            if (ind != -1) {
                                dataStr = dataStr.substring(ind + 1, dataStr.length());
                                arrLength++;
                            }
                        }
                        temp = 1;
                        if (arrLength > 4) {
                            isErrorFound = true;
                            p_scheduleInfoMap.put("IS_ERROR_FOUND", new Boolean(isErrorFound));
                            p_scheduleInfoMap.put("IS_FILE_TYPE_DIFF", errorUploadKeys[9]);
                            return;
                        }
                    }
                    // check for valid MSISDN
                    try {
                        // if receiver msisdn is null
                        if (BTSLUtil.isNullString(arr[0])) {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Receiver msisdn not found", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(errorUploadKeys[12]);
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
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodname, "Not a valid MSISDN " + arr[0]);
                        }
                        _log.errorTrace(methodname, e);
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid MSISDN", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
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
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodname, "Not a valid MSISDN " + arr[0]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid MSISDN", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
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
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodname, "Not Network prefix found " + arr[0]);
                        }
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not Network prefix found", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(prefixNotFound);
                        errorVO.setMsisdn(arr[0]);
                        isErrorFound = true;
                        continue;
                    }
                    // check network support of the MSISDN
                    networkCode = networkPrefixVO.getNetworkCode();
                    if (!networkCode.equals(userVO.getNetworkID())) {
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodname, "Not supporting Network" + arr[0]);
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
                            _log.debug("uploadNormalFile", "Not a valid Receiver Msisdn= " + filteredMsisdn);
                        }
                        _log.errorTrace(methodname, e);
                        ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a GSM number entered", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                        errorVO.setErrorCode(errorUploadKeys[13]);
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
                            _log.debug("uploadNormalFile", "Not a valid sub-service = " + tmpSubService);
                        }
                        _log.errorTrace(methodname, e);
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
                            _log.debug("uploadNormalFile", "Not a valid requested amount tmpAmt: " + tmpAmt);
                        }
                        _log.errorTrace(methodname, e);
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
                     
                        tmpAmt = tmpAmt.trim();
                        Locale locale = LocaleMasterCache.getLocaleFromCodeDetails(tmpAmt);
                        if (locale == null) {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid lanuage code = " + tmpAmt, "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(languageCodeInvalid);
                            errorVO.setErrorCodeArgs(new String[] { tmpAmt });
                            errorVO.setMsisdn(arr[0]);
                            isErrorFound = true;
                            continue;
                        }
                        language = locale.getLanguage();
                        country = locale.getCountry();
                    } catch (Exception e) {
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodname, "Not a valid lanuage code: " + tmpAmt);
                        }
                        _log.errorTrace(methodname, e);
                        if (!BTSLUtil.isNullString(tmpAmt)) {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid lanuage code = " + tmpAmt, "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCodeArgs(new String[] { tmpAmt });
                            errorVO.setErrorCode(languageCodeInvalid);
                        } else {
                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Not a valid lanuage code = " + tmpAmt, "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
                            errorVO.setErrorCode(errorUploadKeys[11]);
                        }
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
           
                scheduleBatchMasterVO.setUploadFailedCount(countData);
                if (_log.isDebugEnabled()) {
                    _log.debug("uploadNormalFile", "No valid Data in the list, size :" + validDataList.size());
                }
                throw new BTSLBaseException(this, "uploadNormalFile", errorUploadKeys[10], "showMsg");
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
                        _log.debug(methodname, "commit connection");
                    }
                    p_scheduleInfoMap.put("PROCESSED_RECS", String.valueOf(validDataList.size()));
                } else {
                    p_con.rollback();
                    _log.error(methodname, "Scheduling file for the service recharge is unsuccessful");
                    throw new BTSLBaseException(this, "uploadNormalFile", errorUploadKeys[7], "scheduleTopUpAuthorise");
                }
            } else {
                p_con.rollback();
                _log.error(methodname, "Scheduling file for the service recharge is unsuccessful");
                throw new BTSLBaseException(this, "uploadNormalFile", errorUploadKeys[7], "scheduleTopUpAuthorise");
            }

        } catch (BTSLBaseException be) {
            _log.error(methodname, "Unable to write data  " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodname, e);
            _log.error(methodname, "Scheduling file for the service recharge is unsuccessful " + e.getMessage());
            throw new BTSLBaseException("RechargeBatchFileParser", "uploadNormalFile", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodname, "Exiting");
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
    @Override
    public String[] getColumnKeys(String p_fileType) {
        if (_log.isDebugEnabled()) {
            _log.debug("getColumnKeys", "Entered p_fileType= " + p_fileType);
        }
        String[] keyArr = null;
        if (PretupsI.BATCH_TYPE_CORPORATE.equals(p_fileType)) {
            String[] keyArr1 = { FILE_COLUMN_HEADER_KEYS[0], FILE_COLUMN_HEADER_KEYS[1], FILE_COLUMN_HEADER_KEYS[2], FILE_COLUMN_HEADER_KEYS[3], FILE_COLUMN_HEADER_KEYS[4], FILE_COLUMN_HEADER_KEYS[5], FILE_COLUMN_HEADER_KEYS[6], FILE_COLUMN_HEADER_KEYS[7], FILE_COLUMN_HEADER_KEYS[8] };
            keyArr = keyArr1;
        }

        else {
            String[] keyArr1 = { FILE_COLUMN_HEADER_KEYS[0], FILE_COLUMN_HEADER_KEYS[7], FILE_COLUMN_HEADER_KEYS[8], FILE_COLUMN_HEADER_KEYS[9] };
            keyArr = keyArr1;
        }
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
    @Override
    public String[] getErrorKeys(String p_fileType) {
        if (_log.isDebugEnabled()) {
            _log.debug("getErrorKeys", "Entered p_fileType= " + p_fileType);
        }
        String[] errorKeyArr = null;
        if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(p_fileType)) {
            String[] errorKeyArr1 = { UPLOAD_ERROR_KEYS[0], UPLOAD_ERROR_KEYS[1], UPLOAD_ERROR_KEYS[2], UPLOAD_ERROR_KEYS[3], UPLOAD_ERROR_KEYS[4], UPLOAD_ERROR_KEYS[5], UPLOAD_ERROR_KEYS[6], UPLOAD_ERROR_KEYS[7], UPLOAD_ERROR_KEYS[8], UPLOAD_ERROR_KEYS[9], UPLOAD_ERROR_KEYS[10], UPLOAD_ERROR_KEYS[11], UPLOAD_ERROR_KEYS[12], UPLOAD_ERROR_KEYS[13], UPLOAD_ERROR_KEYS[14], UPLOAD_ERROR_KEYS[30], UPLOAD_ERROR_KEYS[36] };
            errorKeyArr = errorKeyArr1;
        } else {
            String[] errorKeyArr1 = { UPLOAD_ERROR_KEYS[0], UPLOAD_ERROR_KEYS[1], UPLOAD_ERROR_KEYS[2], UPLOAD_ERROR_KEYS[4], UPLOAD_ERROR_KEYS[5], UPLOAD_ERROR_KEYS[6], UPLOAD_ERROR_KEYS[7], UPLOAD_ERROR_KEYS[14], UPLOAD_ERROR_KEYS[15], UPLOAD_ERROR_KEYS[16], UPLOAD_ERROR_KEYS[17], UPLOAD_ERROR_KEYS[32], UPLOAD_ERROR_KEYS[30], UPLOAD_ERROR_KEYS[36] };
            errorKeyArr = errorKeyArr1;
        }
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
    @Override
    public String getHeaderKey(String p_fileType) {
        if (_log.isDebugEnabled()) {
            _log.debug("getHeaderKey", "Entered p_fileType= " + p_fileType);
        }
        String heading;
        if (PretupsI.BATCH_TYPE_CORPORATE.equals(p_fileType)) {
            heading = FILE_HEADER_KEYS[0];
        } else {
            heading = FILE_HEADER_KEYS[1];
        }
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
    @Override
    public String getRequestMessage(ScheduleBatchDetailVO p_scheduleBatchDetailVO, ChannelUserVO p_channelUserVO, String p_serviceType) {
        if (_log.isDebugEnabled()) {
            _log.debug("getRequestMessage", "Entered ScheduleBatchMasterVO =" + p_scheduleBatchDetailVO + " p_serviceType= " + p_serviceType);
        }
        String message = null;
        String seprator = null;
        try {
            if (!BTSLUtil.isNullString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR))) {
                seprator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            } else {
                seprator = " ";
            }

            message = p_serviceType + seprator + p_scheduleBatchDetailVO.getMsisdn() + seprator + PretupsBL.getDisplayAmount(p_scheduleBatchDetailVO.getAmount()) + seprator + Integer.parseInt(p_scheduleBatchDetailVO.getSubService()) + seprator + p_scheduleBatchDetailVO.getLanguageCode() + seprator + BTSLUtil.decryptText(p_channelUserVO.getUserPhoneVO().getSmsPin());
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
    public boolean isAllowedMsisdn(String p_msisdn,String serviceType) {
        final String methodname = "isAllowedMsisdn";
        if (_log.isDebugEnabled()) {
            _log.debug(methodname, "Entered p_msisdn=" + p_msisdn);
        }
        String serviceCode =null;
        if(serviceType==null) {
        	serviceCode=PretupsI.SERVICE_TYPE_CHNL_RECHARGE;
        }else {
        	serviceCode=serviceType;
        }
        PreparedStatement pstmt=null;
		ResultSet rs = null;
        boolean isPstnMsisdn = false;
        String msisdnPrefix = null;
        NetworkPrefixVO networkPrefixVO = null;
        MSISDNPrefixInterfaceMappingVO msisdnPrefixInterfaceMappingVO = null;
        ArrayList arrList = new ArrayList();
        Connection con=null;
        MComConnectionI mcomCon = null;
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
               ServiceSelectorInterfaceMappingVO interfaceMappingVO1=null;
           	String netCode = networkPrefixVO.getNetworkCode();
					if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SELECTOR_INTERFACE_MAPPING)).booleanValue())
					{
						mcomCon = new MComConnection();
						con=mcomCon.getConnection();
					
					String defaultSelCode = null;
				
					StringBuffer selCodeQuery = new StringBuffer("SELECT SELECTOR_CODE FROM SVC_SETOR_INTFC_MAPPING WHERE SERVICE_TYPE = ? AND ACTION=? AND NETWORK_CODE=?");
					
					if (_log.isDebugEnabled()) {
                        _log.debug(methodname, "QUERY sqlSelect=" + selCodeQuery);
                    }
					
					pstmt = con.prepareStatement(selCodeQuery.toString());
					pstmt.setString(1,serviceCode);
					pstmt.setString(2,PretupsI.INTERFACE_VALIDATE_ACTION);
					pstmt.setString(3, netCode);
					rs = pstmt.executeQuery();

					while (rs.next()) {
						defaultSelCode = rs.getString("SELECTOR_CODE");
					}


					interfaceMappingVO1=ServiceSelectorInterfaceMappingCache.getObject(serviceCode+"_"+defaultSelCode+"_"+PretupsI.INTERFACE_VALIDATE_ACTION+"_"+ netCode+"_"+prefixId);
					if(interfaceMappingVO1!=null)
						{
						String _str=serviceCode+"_"+interfaceMappingVO1.getInterfaceTypeID();
						itr=arrList.iterator();
						while(itr.hasNext())
						{
							String _serviceTypeIntfId=(String)itr.next();
							if(_str.equalsIgnoreCase(_serviceTypeIntfId)) {
                                isPstnMsisdn=true;
                            }
						}
					}
					}
					else{
						 {
							msisdnPrefixInterfaceMappingVO=(MSISDNPrefixInterfaceMappingVO)MSISDNPrefixInterfaceMappingCache.getObject(prefixId,PretupsI.SERVICE_TYPE_CHNL_RECHARGE,"V");
							if(msisdnPrefixInterfaceMappingVO!=null)
							{
                        String str = serviceCode + "_" + msisdnPrefixInterfaceMappingVO.getInterfaceTypeID();
                        itr = arrList.iterator();
                        while (itr.hasNext()) {
                            String serviceTypeIntfId = (String) itr.next();
                            if (str.equalsIgnoreCase(serviceTypeIntfId)) {
                                isPstnMsisdn = true;
                            }
                        }

                    }
                }
            }
                }
            }

        } catch (BTSLBaseException be) {
            _log.error("caught exception ::", "");
            _log.errorTrace(methodname, be);
        } catch (Exception e) {
            _log.error("isAllowedMsisdn", "isPstnMsisdn " + e.getMessage());
            _log.errorTrace(methodname, e);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("RechargeBatchFileParser#isAllowedMsisdn");
				mcomCon = null;
			}
            con = null;
            try{
            	if (rs!= null){
            		rs.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing result set.", e);
            }
            try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing result set.", e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodname, "Exiting");
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
        final String methodname = "getInterfaceIdList";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodname, "Entered");
        }

        ArrayList arrlist = new ArrayList();
        try {
            String serviceWithInterafaceId = Constants.getProperty("PARSER_CONFIGURE_PARAM");
            String[] semiColonArr = serviceWithInterafaceId.trim().split(";");
            for (int i = 0; i < semiColonArr.length; i++) {
                String[] splitOnColon = semiColonArr[i].split(":");
                String serviceType = splitOnColon[0];
                String[] interfaceTypeId = splitOnColon[1].split(",");
                for (int j = 0; j < interfaceTypeId.length; j++) {
                    String s = serviceType + "_" + interfaceTypeId[j];
                    arrlist.add(s);
                }
            }
        } catch (Exception e) {
            _log.error("getInterfaceIdList", "getInterfaceIdList", e.getMessage());
            _log.errorTrace(methodname, e);
            throw new BTSLBaseException("getInterfaceIdList", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "PARSER_CONFIGURE_PARAM not defined in constants.props");
        }

        return arrlist;
    }

	@Override
	public void downloadFileForResheduleRest(Connection p_con, Writer p_fileWriter, String p_fileType,
			HashMap p_scheduleInfoMap) throws BTSLBaseException {

   	 final String methodname = "downloadFileForReshedule";
   	if (_log.isDebugEnabled()) {
           _log.debug(methodname, "Entered p_fileType= " + p_fileType);
       }
      
       try {
           if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(p_fileType)) {
               downloadCorporateFileForRescheduleRest(p_con, p_fileWriter, p_scheduleInfoMap);
           } else {
               downloadNormalFileForRescheduleRest(p_con, p_fileWriter, p_scheduleInfoMap);
           }
       } catch (BTSLBaseException be) {

           _log.error(methodname, "Unable to write data " + be.getMessage());
           throw be;
       } catch (Exception e) {
           _log.errorTrace(methodname, e);
           _log.error(methodname, "Unable to write data " + e.getMessage());
           throw new BTSLBaseException("RechargeBatchFileParser", "downloadFileForReshedule", PretupsErrorCodesI.GENERAL_PROCESSING_ERROR, "scheduleTopUpAuthorise");
       } finally {
           if (_log.isDebugEnabled()) {
               _log.debug(methodname, "Exiting");
           }
       }

   
	}
}
