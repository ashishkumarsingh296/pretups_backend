
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestClient;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.channel.user.businesslogic.BatchUserDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRestI;
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
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingCache;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.txn.pretups.restrictedsubs.businesslogic.RestrictedSubscriberTxnDAO;

	/**
	 * this class is used to schedule file for service type RC- recharge
	 **/
	public class DVDBulkParser  implements BatchFileParserI {
	    public static final Log _log = LogFactory.getLog(DVDBulkParser.class.getName());

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
	                uploadCorporateFile(p_con, p_scheduleInfoMap, p_isErrorFound);
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
	            p_fileWriter.write(heading);
	            p_fileWriter.write("\n");
	          for (int j = 0; j <=7; j++) {
	        	  if(j==0){
	        		  p_fileWriter.write("Service Name");
	        	  }
	        	  else if (j==1) {
	              
	                    p_fileWriter.write( ","+heading + getCommaString(heading));
	                } else {
	                    p_fileWriter.write(",");
	                }
	            }
	            /*p_fileWriter.write( heading + getCommaString(heading));
	            p_fileWriter.write("\n");*/
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
	                msisdnAllowed = isAllowedMsisdn(restrictedSubscriberVO.getMsisdn());
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
	        ArrayList<RestrictedSubscriberVO> checkForDuplicate = new ArrayList<RestrictedSubscriberVO>();
	        try {
	            String dataStr = null;
	            int countData = 0;
	            RestrictedSubscriberVO errorVO = null;
	            String arr[] = null;
	            String filteredMsisdn = null;
	            String msisdnPrefix = null;
	            NetworkPrefixVO networkPrefixVO = null;
	            String networkCode = null;
	            String requestFor = null;
	            String ownerID = null;
	            String userID = null;
	            String batchID = null;
	            String createdBy = null;
	        	String txnStatus = "";
	            String errorKey;
	            String responseString;
	        	
	            ArrayList<PretupsResponse<JsonNode>> validDataList = new ArrayList<PretupsResponse<JsonNode>>();
	            BatchUserDAO batchUserDao = new BatchUserDAO();
	            PretupsRestClient pretupsRestClient = new PretupsRestClient();
	            PretupsResponse<JsonNode> pretupsResponse;

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
	            String noDataExists = errorUploadKeys[18];
	            String invalidData = errorUploadKeys[19];
	            String invalidQuantity = errorUploadKeys[20];
	           

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


	            RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
	            RestrictedSubscriberTxnDAO restrictedSubscriberTxnDAO = new RestrictedSubscriberTxnDAO();
	        
				if ("Schedule".equals(requestFor)) {
	                errorKey = errorUploadKeys[11];
	                if (downLoadDataMap == null || downLoadDataMap.isEmpty()) {
	                    downLoadDataMap = restrictedSubscriberTxnDAO.loadRestrictedSubscriberList(p_con, userID, ownerID);
	                }
	            } else if ("Reschedule".equals(requestFor)) {
	                errorKey = errorUploadKeys[12];
	                if (downLoadDataMap == null || downLoadDataMap.isEmpty() || !downloadedBatchID.equals(batchID)) {
	                    downLoadDataMap = restrictedSubscriberDAO.loadScheduleBatchDetailsList(p_con, batchID, PretupsI.STATUS_EQUAL, PretupsI.SCHEDULE_STATUS_CANCELED);
	                }
	            }
	            // ends here
	            while (finalListSize != countData) {
	            	
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
	                        if (arrLength < 5) {
	                            isErrorFound = true;
	                            errorVO.setisErrorFound(true);
	                            scheduleInfoMap.put("IS_ERROR_FOUND", new Boolean(isErrorFound));
	                            scheduleInfoMap.put("IS_FILE_TYPE_DIFF", errorUploadKeys[13]);
	                            return;
	                        }
	                    }
			                 errorVO.setMsisdn(arr[0]);
			                 errorVO.setvoucherType(arr[1]);
			                 errorVO.setvoucherSegment(arr[2]);
							 errorVO.setAmount(Long.parseLong(arr[3]));
							 errorVO.setvoucherProfile(arr[4]);
							 errorVO.setVoucherQuantity(arr[5]);
		  
	                    // check for valid MSISDN
	                    try {
	                        // if receiver msisdn is null
	                        if (BTSLUtil.isNullString(arr[0])) {
	                            ScheduleFileProcessLog.log("Validating Data", createdBy, arr[0], batchID, "Receiver msisdn not found", "FAIL", "UPLOADED FILE =" + filePathAndFileName + ", TYPE=" + requestFor);
	                            errorVO.setErrorCode(errorUploadKeys[15]);
	                            errorVO.setMsisdn(arr[0]);
	                            errorVO.setisErrorFound(true);
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
	                        errorVO.setisErrorFound(true);
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
	                        errorVO.setisErrorFound(true);
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
	                        errorVO.setisErrorFound(true);
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
	                        errorVO.setisErrorFound(true);
	                        continue;
	                    }

	                    try {
	                    	VomsVoucherDAO vomsVoucherDAO = new VomsVoucherDAO();
	                    	ArrayList<CardGroupDetailsVO> digitalList = null;
	                    	String[] voucherType = {arr[1]};
	            			digitalList = vomsVoucherDAO.returnVoucherDetailsWithCount(p_con, userVO.getUserID(), voucherType, userVO.getNetworkID());
	            			
	            			CardGroupDetailsVO cardGroupDetailsVO = null;
	            			
	            			for(CardGroupDetailsVO cardGroupObject: digitalList)
	            			{
	            				if(cardGroupObject.getVoucherProductId().equals(arr[4]) 
	            				 && cardGroupObject.getVoucherSegment().equals(arr[2]))
	            				{
	            					cardGroupDetailsVO = cardGroupObject;
	            					break;
	            				}
	            			}
	            			
	            			if(cardGroupDetailsVO != null && cardGroupDetailsVO.getVoucherType().equalsIgnoreCase(arr[1])
	            				&& cardGroupDetailsVO.getVoucherSegment().equals(arr[2])
	            					&& cardGroupDetailsVO.getVoucherDenomination().equals(arr[3]))
	            			{
	            				
	            				if(Integer.parseInt(cardGroupDetailsVO.getAvailableVouchers()) < Integer.parseInt(arr[5]))
	            				{
	            					errorVO.setErrorCode(invalidQuantity);
		            				errorVO.setMsisdn(arr[0]);
		            				errorVO.setisErrorFound(true);
			                        continue;
	            				}
	            				
	            				Map<String, Object> data = new HashMap<>();
	            	            data.put("extnwcode",userVO.getNetworkID());
	            	            data.put("vouchertype", arr[1]);
	            	            data.put("loginid",userVO.getLoginID());
	            	            data.put("password",userVO.getPassword());
	            	            data.put("extcode","");
	            	            data.put("voucherprofile", arr[4]);
	            	            data.put("quantity", arr[5]);
	            	            data.put("msisdn","");
	            	            data.put("pin", "");
	            	            data.put("msisdn2",arr[0]);
	            	            data.put("vouchersegment",arr[2]);
	            	            data.put("amount",arr[3]);
	            	            data.put("language1","");
	            	            data.put("language2","");
	            	            data.put("selector","");
	            	            Map<String, Object> requestObject = new HashMap<>();
	            	            requestObject.put("data", data);
	            	            requestObject.put("reqGatewayLoginId", "pretups");
	            	            requestObject.put("reqGatewayPassword", "1357");
	            	            requestObject.put("reqGatewayCode", "REST");
	            	            requestObject.put("reqGatewayType", "REST");
	            	            requestObject.put("servicePort", "190");
	            	            requestObject.put("sourceType", "JSON");


	            				
	            				 responseString = pretupsRestClient.postJSONRequest(requestObject, PretupsRestI.DVD_RECEIVER);
	            				
	            				
	            				
	            	    		 pretupsResponse = (PretupsResponse<JsonNode>) PretupsRestUtil
	            	    				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<JsonNode>>() {
	            	    				});
	            	            
	            	    	
	            	            
	            	            if(pretupsResponse !=null)
	            	            {
	            	            	JsonNode dataNode = pretupsResponse.getDataObject();
	            	            	
	            	            	if(dataNode.get("txnstatus") != null)
	            	            	{
	            	            		txnStatus = dataNode.get("txnstatus").textValue();
	            	            	}
	            	            }
	            	            
	            				if(txnStatus.equals("200"))
	            				{
	            					validDataList.add(pretupsResponse);
	            					errorVO.setisErrorFound(false);
	            				}
	            				else
	            				{
	            					errorVO.setErrorCode(invalidData);
		            				errorVO.setMsisdn(arr[0]);
		            				errorVO.setisErrorFound(true);
			                        continue;
	            				}	
	            			}
	            			else
	            			{
	            				errorVO.setErrorCode(noDataExists);
	            				errorVO.setMsisdn(arr[0]);
	            				errorVO.setisErrorFound(true);
		                        continue;
	            			}
	                    
	                    } catch (Exception e) {
	                        errorVO.setMsisdn(arr[0]);
	                        errorVO.setisErrorFound(true);
	                        continue;
	                    }
	                    	
	                } else if ("NEWLINE".equals(errorVO.getErrorCode()) || "NEWLINE".equals(errorVO.getMsisdn())) {
	                    // /
	                    // Remove this object form the list since not for further in
	                    // use
	                    // /
	                    finalList.remove(--countData);
	                    finalListSize--;
	                }
	        
	            }	           
                if(validDataList.size() == Integer.valueOf((String)p_scheduleInfoMap.get("RECORDS")))
                {
                	scheduleInfoMap.put("IS_ERROR_FOUND", false);
                }
                else
                {
                	scheduleInfoMap.put("IS_ERROR_FOUND", true);	 
                }
                	
                
	            scheduleInfoMap.put("PROCESSED_RECS", String.valueOf(validDataList.size()));
                scheduleInfoMap.put("IS_FILE_TYPE_DIFF", "");
                
              /*  if(validDataList.size() > 0)
                {
                	batchUserDao.updateDailyCountForUser(p_con, userVO);
                }*/
                
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
	            String[] keyArr1 = { FILE_COLUMN_DVD_KEYS[0], FILE_COLUMN_DVD_KEYS[1], FILE_COLUMN_DVD_KEYS[2], FILE_COLUMN_DVD_KEYS[3], FILE_COLUMN_DVD_KEYS[4], FILE_COLUMN_DVD_KEYS[5] };
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
	            String[] errorKeyArr1 = { UPLOAD_ERROR_KEYS[0], UPLOAD_ERROR_KEYS[1], UPLOAD_ERROR_KEYS[2], UPLOAD_ERROR_KEYS[3], UPLOAD_ERROR_KEYS[4], UPLOAD_ERROR_KEYS[5], UPLOAD_ERROR_KEYS[6], UPLOAD_ERROR_KEYS[7], UPLOAD_ERROR_KEYS[8], UPLOAD_ERROR_KEYS[9], UPLOAD_ERROR_KEYS[10], UPLOAD_ERROR_KEYS[11], UPLOAD_ERROR_KEYS[12], UPLOAD_ERROR_KEYS[13], UPLOAD_ERROR_KEYS[14], UPLOAD_ERROR_KEYS[30], UPLOAD_ERROR_KEYS[36], UPLOAD_ERROR_KEYS[37], UPLOAD_ERROR_KEYS[38], UPLOAD_ERROR_KEYS[39], UPLOAD_ERROR_KEYS[40] };
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
	            heading = FILE_HEADER_KEYS[4];
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
	    private boolean isAllowedMsisdn(String p_msisdn) {
	        final String methodname = "isAllowedMsisdn";
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodname, "Entered p_msisdn=" + p_msisdn);
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
						pstmt.setString(1,PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
						pstmt.setString(2,PretupsI.INTERFACE_VALIDATE_ACTION);
						pstmt.setString(3, netCode);
						rs = pstmt.executeQuery();

						while (rs.next()) {
							defaultSelCode = rs.getString("SELECTOR_CODE");
						}


						interfaceMappingVO1=ServiceSelectorInterfaceMappingCache.getObject(PretupsI.SERVICE_TYPE_CHNL_RECHARGE+"_"+defaultSelCode+"_"+PretupsI.INTERFACE_VALIDATE_ACTION+"_"+ netCode+"_"+prefixId);
						if(interfaceMappingVO1!=null)
							{
							String _str=PretupsI.SERVICE_TYPE_CHNL_RECHARGE+"_"+interfaceMappingVO1.getInterfaceTypeID();
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
	                        String str = PretupsI.SERVICE_TYPE_CHNL_RECHARGE + "_" + msisdnPrefixInterfaceMappingVO.getInterfaceTypeID();
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
			// TODO Auto-generated method stub
			
		}
	}


