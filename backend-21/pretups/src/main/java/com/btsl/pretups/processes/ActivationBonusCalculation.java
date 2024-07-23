package com.btsl.pretups.processes;

/*
 * ActivationBonusCalculation.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Manisha Jain 19/02/09 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2009 Bharti Telesoft Ltd.
 */
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.ProfileSetDetailsVO;
import com.btsl.pretups.channel.profile.businesslogic.ProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.ProfileSetVersionVO;
import com.btsl.pretups.channel.profile.businesslogic.RetSubsMappingVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.ProfileBonusLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ActivationBonusVO;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.processes.businesslogic.UserTransactionVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.ibm.icu.util.Calendar;

class ActivationBonusCalculation {
    private static final Log _logger = LogFactory.getLog(ActivationBonusCalculation.class.getName());
    private static OperatorUtilI _operatorUtil = null;
    private static HashMap _setIdMap = null;
    private static HashMap _actvationMap = null;
    private static HashMap _productMap = null;
    private static PreparedStatement _loadTxnPrfStmt = null;
    private static PreparedStatement _loadVolumePrfStmt = null;
    private static PreparedStatement _loadmappingStmt = null;
    private static PreparedStatement _updateTempTableStmt = null;
    private static PreparedStatement _loadSetIdStmt = null;
    private static PreparedStatement _updateMappingStmt = null;
    private static PreparedStatement _fetchDataStmt = null;
    private static PreparedStatement _categoryProfileStmt = null;
    private static PreparedStatement _checkUserExistStmt = null;
    private static PreparedStatement _saveBonusStmt = null;
    private static PreparedStatement _saveUserTxnStmt = null;
    private static PreparedStatement _updateBonusStmt = null;
    private static long _noOfRecordProcess = 10000L;
    private static Date _currentDate = new Date();
    private static PreparedStatement _updateUsrTxnStmt = null;
    private static PreparedStatement _userInUserTxnStmt = null;

    /**
     * ensures no instantiation
     */
    private ActivationBonusCalculation(){
    	
    }
    
    /**
     * @param args
     *            String[]
     *            Take arguements Constants.props and LogConfig.props
     */
    public static void main(String[] args) {
        final String METHOD_NAME = "main";
        try {
            if (args.length != 2) {
                System.out.println("Usage : ActivationBonusCalculation [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = Constants.validateFilePath(args[0]);
            if (!constantsFile.exists()) {
                System.out.println(" Constants File Not Found .............");
                return;
            }
            final File logconfigFile = Constants.validateFilePath(args[1]);
            if (!logconfigFile.exists()) {
                System.out.println(" Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        }// end try
        catch (Exception ex) {
            _logger.error(METHOD_NAME, "Error in Loading Configuration files ...........................: " + ex);
            _logger.errorTrace(METHOD_NAME, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }

        try {
            try {
                // load util class
                final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
                _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusCalculation[main]", "", "", "",
                    "Exception while loading operator util class:" + e.getMessage());
                throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.UNABLE_TO_LOAD_UTIL_CLASS,e);
            }
            process();
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {

            _logger.error(METHOD_NAME, "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.info(METHOD_NAME, " Exiting");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * @throws Exception
     *             1.load details of txn process, process id=ACTTXN
     *             2.check if it is already executed for 1 day before current
     *             day
     *             3.if not then load details of volume process, process
     *             id=ACTVOLUME
     *             4.if executed_upto of volume process is less than txn process
     *             then execute volume process of previous days.
     *             5.else exceute txn process then execute volume process
     *             6.create temporary table c2s_transfers_temp
     *             7.insert data in temporary table, selected from c2s_transfer
     *             table
     *             8.pick first 1000 records, process each records
     *             9.take receiver msisdn from c2s_transfers_temp table and
     *             find its mapping with channel user from
     *             act_bonus_subscriber_mapping table
     *             10.if mapping exist then find profile set id associated with
     *             channel user
     *             11.first search profile set in user_oth_profile table
     *             corresponding to user_id
     *             12.if no entry found then load profile set id on the category
     *             basis, from profile mapping table
     *             13.calculate bonus
     *             14.update bonus table if entry already exist in bonus table
     *             corressponding to user_id,points_date,
     *             product_code
     *             15.if entry does not exist then insert it in bonus table
     *             16.write all information in ProfileBonusLog
     *             17.update c2s_transfers_temp table
     *             18.update details in process_status table corresponding to
     *             txn process id
     */
    private static void process() throws Exception {
        final String METHOD_NAME = "process";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered");
        }
        Date processedUpto = null;
        Date processedUptoVolume = null;
        Date currentDate = null;
        Connection con = null;
        String processId = null;
        int beforeInterval = 0;
        int beforeIntervalVolume = 0;
        ProcessStatusDAO processStatusDAO = null;
        ProcessStatusVO processStatusVolumeVO = null;
        ProcessStatusVO processStatusVO = null;
        String processVolumeID;
        ArrayList dataList = null;
        C2STransferVO c2sTransferVO = null;
        RetSubsMappingVO actBonusSubsMappingVO = null;
        ProfileSetVO profileSetVO = null;
        ActivationBonusVO bonusVO = null;
        ActivationBonusVO bonusOldVO = null;
        int updateCount = 0;
        long insertCount = 0;
        boolean volumeStatus = false;
        double points = 0;
        ProfileSetVersionVO profileSetVersionVO = null;
        String setId = null;
        Date dateCount = null;
        try {
            _logger.debug(METHOD_NAME, " Start of process Total memory: " + Runtime.getRuntime().totalMemory() / 1048576 + " Free memory: " + Runtime.getRuntime()
                .freeMemory() / 1048576);
            currentDate = BTSLUtil.getSQLDateFromUtilDate(_currentDate);
            processId = ProcessI.ACT_TXN_BONUS;
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                _logger.debug(METHOD_NAME, "Activation bonus calculation process connection is null");
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusCalculation[process]", "", "", "", "DATABASE Connection is NULL");
                return;
            }
            processStatusVO = checkProcessUnderProcess(con, processId);

            // method call to find maximum date till which process has been
            // executed
            processedUpto = processStatusVO.getExecutedUpto();

            final int diffDate = BTSLUtil.getDifferenceInUtilDates(processedUpto, currentDate);

            // load volume process details
            processVolumeID = ProcessI.ACT_VOLUME_BONUS;
            processStatusVolumeVO = checkProcessUnderProcess(con, processVolumeID);
            processedUptoVolume = processStatusVolumeVO.getExecutedUpto();
            int diffDateVolume = BTSLUtil.getDifferenceInUtilDates(processedUptoVolume, currentDate);
            // check difference of txn process and volume process with current
            // date is less than before interval of respective processes.
            // if it is less then beforeInterval then
            beforeInterval = BTSLUtil.parseLongToInt( (processStatusVO.getBeforeInterval() / (60 * 24)) );
            beforeIntervalVolume = BTSLUtil.parseLongToInt( (processStatusVolumeVO.getBeforeInterval() / (60 * 24)) );
            if (diffDate <= beforeInterval && diffDateVolume <= beforeIntervalVolume) {
                _logger.error(METHOD_NAME, " Process already executed for the days less than before interval.....");
                throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.ACT_TXN_PROCESS_ALREADY_EXECUTED);
            }
            // currentDate=new Date();
            diffDateVolume = BTSLUtil.getDifferenceInUtilDates(processedUpto, processedUptoVolume);
            makeQuery(con);
            try {
				createTempTable(con);
			} catch (Exception e1) {
				_logger.errorTrace(METHOD_NAME,e1);
				//dropTempTable(con);
			}
            // if difference between exteciutedUpto of txn process and volume
            // process is less than 0, it means
            // volume process is executedUpto of volume process is less than
            // executed upto of txn process,
            // then execute volume process first,then execute txn process
            _logger.debug(METHOD_NAME, "volume process diffDateVolume="+diffDateVolume);
            if (diffDateVolume < 0) {
                _logger.error(METHOD_NAME, " Volume process is not executed.....");
                _productMap = new HashMap();
                _setIdMap = new HashMap();
                _actvationMap = new HashMap();
                volumeStatus = processVolumeTransaction(con, processStatusVolumeVO);
                if (!volumeStatus) {
                    _logger.debug(METHOD_NAME, "volume process not executed successfully");
                    throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.VOLUME_PROCESS_NOT_EXECUTED_SUCCESSFULLY);
                }
            }
            // if difference is less than before interval then dont execute
            // process, process is already executed for the days less than
            // before interval
            if (diffDate <= beforeInterval) {
                _logger.error(METHOD_NAME, " Process already executed....." + diffDate + " diffDateVolume: " + diffDateVolume);
                return;
            }
            processStatusVO.setStartDate(_currentDate);
            processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);

            processStatusDAO = new ProcessStatusDAO();
            // execute process for all the days that are less than before
            // interval from current date,
            // means execute process upto currentDate-beforeInterval
            boolean status = false;
            for (dateCount = processedUpto; dateCount.before(BTSLUtil.addDaysInUtilDate(_currentDate, -beforeInterval)); dateCount = BTSLUtil.addDaysInUtilDate(dateCount, 1)) {
                processStatusVO.setProcessStatus(ProcessI.STATUS_UNDERPROCESS);
                updateCount = processStatusDAO.updateProcessDetail(con, processStatusVO);
                if (updateCount > 0) {
                    con.commit();
                }
                processedUpto = dateCount;
                status = false;
                try {
                	status=createTempTable(con);
    			} catch (Exception e1) {
    				_logger.errorTrace(METHOD_NAME,e1);
    				status=true;
    			}
                if (status) {
                    _logger.error(METHOD_NAME, " Insert data in temporary table");
                    insertCount = insertDataInTempTable(con, processedUpto);
                    createIndex(con);
                    if (insertCount < 0) {
                        _logger.error(METHOD_NAME, " Temp table not updated.....");
                        throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.TEMP_TABLE_NOT_UPDATED);
                    }
                }
                try {
                    con.commit();
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                }
                _productMap = new HashMap();
                _setIdMap = new HashMap();
                _actvationMap = new HashMap();
                //do {
                    // pick _noOfRecordProcess records at a time to process
                    dataList = fetchDataFromTempTable();
                    _logger.debug(METHOD_NAME, " After fetching data Total memory: " + Runtime.getRuntime().totalMemory() / 1048576 + " Free memory: " + Runtime.getRuntime()
                        .freeMemory() / 1048576);
                    if (dataList == null || dataList.size() <= 0) {
                        break;
                    }
                    for (int j = 0; j < dataList.size(); j++) {
                        c2sTransferVO = (C2STransferVO) dataList.get(j);
                        if (_productMap != null) {
                            _productMap.put(c2sTransferVO.getServiceType(), c2sTransferVO.getProductCode());
                        }
                        // check if _actvationMap is null, if it is not null
                        // then get actBonusSubsMappingVO from Map
                        // actBonusSubsMappingVO is null then load VO from DB
                        if (_actvationMap != null) {
                            actBonusSubsMappingVO = (RetSubsMappingVO) _actvationMap.get(c2sTransferVO.getReceiverMsisdn());
                        }
                        if (actBonusSubsMappingVO == null) {
                            actBonusSubsMappingVO = loadRetailerSubscriberMapping(c2sTransferVO.getReceiverMsisdn(), processedUpto);
                        }

                        if (actBonusSubsMappingVO == null) {
                            _logger.debug(METHOD_NAME, "no mapping found for subscriber " + c2sTransferVO.getReceiverMsisdn());
                            ProfileBonusLog.log("No subscriber retailer mapping found", null, "NA", c2sTransferVO.getReceiverMsisdn(), 0, PretupsBL
                                .getDisplayAmount(c2sTransferVO.getTransferValue()), 0);
                            updateCount = updateTempTable(PretupsI.NO_MAPPING_FOUND, c2sTransferVO.getTransferID());
                            continue;
                        } else if ((actBonusSubsMappingVO.getExpiryDate() != null && processedUpto.after(actBonusSubsMappingVO.getExpiryDate())) || !(PretupsI.YES
                            .equals(actBonusSubsMappingVO.getStatus()))) {
                            _actvationMap.put(c2sTransferVO.getReceiverMsisdn(), actBonusSubsMappingVO);
                            _logger.debug(METHOD_NAME, "No active mapping found " + c2sTransferVO.getReceiverMsisdn());
                            ProfileBonusLog.log("No active mapping found", null, actBonusSubsMappingVO.getUserID(), c2sTransferVO.getReceiverMsisdn(), 0, PretupsBL
                                .getDisplayAmount(c2sTransferVO.getTransferValue()), 0);
                            updateCount = updateTempTable(PretupsI.NO_MAPPING_FOUND, c2sTransferVO.getTransferID());
                            continue;
                        }
                        _actvationMap.put(c2sTransferVO.getReceiverMsisdn(), actBonusSubsMappingVO);
                        // first take setId from Map, if set id is null then
                        // load it frim user_oth_profile table
                        // if no entry exist there corresponding to user id then
                        // pick category of user and load set id
                        // from profile_mapping table
                        if (_setIdMap != null) {
                            setId = (String) _setIdMap.get(actBonusSubsMappingVO.getUserID());
                        }
                        if (BTSLUtil.isNullString(setId)) {
                            setId = loadUserOtherProfile(actBonusSubsMappingVO.getUserID());
                            if (BTSLUtil.isNullString(setId)) {
                                setId = loadCategoryLevelProfile(actBonusSubsMappingVO.getUserID());
                                if (BTSLUtil.isNullString(setId)) {
                                    _logger.debug(METHOD_NAME, "No user level and category level activation profile found " + c2sTransferVO.getReceiverMsisdn());
                                    ProfileBonusLog.log("No user level and category level activation profile found", null, actBonusSubsMappingVO.getUserID(), c2sTransferVO
                                        .getReceiverMsisdn(), 0, PretupsBL.getDisplayAmount(c2sTransferVO.getTransferValue()), 0);
                                    updateCount = updateTempTable(PretupsI.NO_MAPPING_FOUND, c2sTransferVO.getTransferID());
                                    continue;
                                }
                            }
                            _setIdMap.put(actBonusSubsMappingVO.getUserID(), setId);
                        }
                        profileSetVO = loadProfileDetails(c2sTransferVO.getTransferDateTime(), setId, c2sTransferVO.getServiceType());
                        if (profileSetVO == null) {
                            _logger.debug(METHOD_NAME, "No active version found " + actBonusSubsMappingVO.getUserID());
                            ProfileBonusLog.log("No active version found ", null, actBonusSubsMappingVO.getUserID(), c2sTransferVO.getReceiverMsisdn(), 0, PretupsBL
                                .getDisplayAmount(c2sTransferVO.getTransferValue()), 0);
                            updateCount = updateTempTable(PretupsI.NO_MAPPING_FOUND, c2sTransferVO.getTransferID());
                            continue;
                        }
                        // calculate transaction based bonus
                        bonusVO = _operatorUtil.calculateActivationTxnBonus(c2sTransferVO, profileSetVO, actBonusSubsMappingVO.getSubscriberType());
                        profileSetVersionVO = profileSetVO.getProfileLastVersion();
                        // if no one time binus activation bonus is given to the
                        // user, then add one time activation bonus to the user
                        if (PretupsI.NO.equals(actBonusSubsMappingVO.getActivationBonusGiven()) && profileSetVersionVO.getProductCode().equals(c2sTransferVO.getProductCode())) {
                            points = bonusVO.getPoints() + profileSetVersionVO.getOneTimeBonus();
                            updateActBonusMappingTable(actBonusSubsMappingVO.getUserID(), actBonusSubsMappingVO.getSubscriberMsisdn());
                            actBonusSubsMappingVO.setActivationBonusGiven(PretupsI.YES);
                            if (PretupsI.NO.equals(bonusVO.getTxnCalculationDone())) {
                                bonusVO.setTxnCalculationDone(PretupsI.YES);
                            }
                            bonusVO.setPoints(points);
                            _actvationMap.put(c2sTransferVO.getReceiverMsisdn(), actBonusSubsMappingVO);
                        }
                        if (bonusVO != null && (PretupsI.YES.equals(bonusVO.getTxnCalculationDone()))) {
                            // check entry already present in BONUS table
                            // corresponding to user_id,product_type, point date
                            // and product code
                            bonusOldVO = checkUserAlreadyExist(actBonusSubsMappingVO.getUserID(), processedUpto, c2sTransferVO.getProductCode());
                            if (bonusOldVO != null) {
                                // if it is present then update the entries
                                points = bonusVO.getPoints() + bonusOldVO.getPoints();
                                bonusOldVO.setLastAllocationType(PretupsI.PROFILE_TRANS);
                                bonusOldVO.setLastAllocationdate(processedUpto);
                                // Write Profile Bonus Log
                                ProfileBonusLog.log("Success", bonusOldVO, actBonusSubsMappingVO.getUserID(), c2sTransferVO.getReceiverMsisdn(), bonusVO.getPoints(),
                                    PretupsBL.getDisplayAmount(c2sTransferVO.getTransferValue()), bonusOldVO.getPoints());
                                bonusOldVO.setPoints(points);
                                bonusOldVO.setTransferId(c2sTransferVO.getTransferID());
                                updateCount = updateBonusOfUser(bonusOldVO);
                            } else {
                                // and if it is not present then insert new
                                // entry
                                bonusVO.setProfileType(PretupsI.PROFILE_TYPE_ACTIVATION);
                                bonusVO.setUserId(actBonusSubsMappingVO.getUserID());
                                bonusVO.setBucketCode(PretupsI.BUCKET_ONE);
                                bonusVO.setProductCode(c2sTransferVO.getProductCode());
                                bonusVO.setLastAllocationType(PretupsI.PROFILE_TRANS);
                                bonusVO.setPointsDate(processedUpto);
                                bonusVO.setLastAllocationdate(processedUpto);
                                bonusVO.setCreatedOn(currentDate);
                                bonusVO.setCreatedBy(PretupsI.SYSTEM);
                                bonusVO.setModifiedOn(currentDate);
                                bonusVO.setModifiedBy(PretupsI.SYSTEM);
                                bonusVO.setTransferId(c2sTransferVO.getTransferID());
                                // Write Profile Bonus Log
                                ProfileBonusLog.log("Success", bonusVO, actBonusSubsMappingVO.getUserID(), c2sTransferVO.getReceiverMsisdn(), bonusVO.getPoints(), PretupsBL
                                    .getDisplayAmount(c2sTransferVO.getTransferValue()), 0);
                                insertCount = saveBonus(bonusVO);
                                if (insertCount <= 0) {
                                    // _logger.debug("process","Entry not inserted in BONUS table");
                                    throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.INSERTION_ERROR_BONUS_TABLE);
                                }
                            }
                        } else {
                            ProfileBonusLog.log("No bonus given to channel user for the transfer amount ", null, actBonusSubsMappingVO.getUserID(), c2sTransferVO
                                .getReceiverMsisdn(), 0, PretupsBL.getDisplayAmount(c2sTransferVO.getTransferValue()), bonusVO.getPoints());
                        }
                        // then update temporary table corresponding to
                        // transfer_id
                        updateCount = updateTempTable(bonusVO.getTxnCalculationDone(), c2sTransferVO.getTransferID());
                        actBonusSubsMappingVO = null;
                        bonusVO = null;
                        points = 0;
                        setId = null;
                    }
                    try {
                        con.commit();
                    } catch (Exception e) {
                        _logger.errorTrace(METHOD_NAME, e);
                    }
                //} while (dataList == null || dataList.size() >= _noOfRecordProcess);
                dataList = null;
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationBonusCalculation[process]", "", "", "",
                    " Activation bonus process for transaction profile is executed successfully.");
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "transaction process executed successfully");
                }
                processStatusVO.setExecutedUpto(processedUpto);
                processStatusVO.setExecutedOn(_currentDate);
                processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                // update process details in process_status table corresponding
                // to txn process
                // mark txn process COMPLETE
                updateCount = processStatusDAO.updateProcessDetail(con, processStatusVO);
                if (updateCount > 0) {
                    con.commit();
                }
                _logger.debug(METHOD_NAME, " After completion of tnx process Total memory: " + Runtime.getRuntime().totalMemory() / 1048576 + " Free memory: " + Runtime
                    .getRuntime().freeMemory() / 1048576);
                volumeStatus = processVolumeTransaction(con, processStatusVolumeVO);
                if (!volumeStatus) {
                    // _logger.debug("process","volume process not executed successfully");
                    throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.VOLUME_PROCESS_NOT_EXECUTED_SUCCESSFULLY);
                }
            }
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception e1) {
                    _logger.errorTrace(METHOD_NAME, e1);
                }
            }
            throw be;
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception : " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception e1) {
                    _logger.errorTrace(METHOD_NAME, e1);
                }
            }
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationBonusCalculation[process]", "", "", "",
                " ActivationBonusCalculation process not executed successfully.");
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                    con = null;
                } catch (SQLException e1) {
                    _logger.errorTrace(METHOD_NAME, e1);
                }
            }
            if (_loadTxnPrfStmt != null) {
                try {
                    _loadTxnPrfStmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_loadVolumePrfStmt != null) {
                try {
                    _loadVolumePrfStmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_updateMappingStmt != null) {
                try {
                    _updateMappingStmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_loadmappingStmt != null) {
                try {
                    _loadmappingStmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_loadSetIdStmt != null) {
                try {
                    _loadSetIdStmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_updateTempTableStmt != null) {
                try {
                    _updateTempTableStmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_fetchDataStmt != null) {
                try {
                    _fetchDataStmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_categoryProfileStmt != null) {
                try {
                    _categoryProfileStmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_checkUserExistStmt != null) {
                try {
                    _checkUserExistStmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_saveBonusStmt != null) {
                try {
                    _saveBonusStmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_saveUserTxnStmt != null) {
                try {
                    _saveUserTxnStmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_updateBonusStmt != null) {
                try {
                    _updateBonusStmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_updateUsrTxnStmt != null) {
                try {
                    _updateUsrTxnStmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_userInUserTxnStmt != null) {
                try {
                    _userInUserTxnStmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... ");
            }
        }
    }

    /**
     * @param p_con
     *            Connection
     * @return boolean
     * @throws BTSLBaseException
     *             create temporary tble from C2S_TRANSFER table.
     */
    private static boolean createTempTable(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "createTempTable";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered");
        }
        PreparedStatement pstm = null;
        boolean status = false;
        try {
        	ActivationBonusCalculationQry activationBonusCalculationQry = (ActivationBonusCalculationQry)
        			ObjectProducer.getObject(QueryConstants.ACTIVATION_BONUS_CAL_QRY, QueryConstants.QUERY_PRODUCER);
            final String query =activationBonusCalculationQry.createTempTableQry();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Query: " + query);
            }
            pstm = p_con.prepareStatement(query);
            pstm.executeUpdate();
            status = true;
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "c2s_transfers_temp table already exist ");
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationBonusCalculation[createTempTable]", "", "",
                "", " Temporary table not created");
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.TEMP_TABLE_NOT_CREATED,e);
        } finally {
            if (pstm != null) {
                try {
                    pstm.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... status: " + status);
            }
        }
        return status;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_processingDate
     *            Date
     * @return long
     * @throws BTSLBaseException
     */
    private static long insertDataInTempTable(Connection p_con, Date p_processingDate) throws BTSLBaseException {
    	//local_index_implemented
        final String METHOD_NAME = "insertDataInTempTable";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered p_processingDate: " + p_processingDate);
        }
        PreparedStatement pstm = null;
        long count = 0L;
        try {
            final StringBuffer qryBuffer = new StringBuffer();
            qryBuffer.append(" INSERT INTO C2S_TRANSFERS_TEMP SELECT ");
            qryBuffer.append(" transfer_id,transfer_date, transfer_date_time, ");
            qryBuffer.append(" network_code,product_code,receiver_msisdn,transfer_value, ");
            qryBuffer.append(" service_type,reconciliation_date,'N','N' ");
            qryBuffer.append(" FROM C2S_TRANSFERS where (transfer_date=? OR reconciliation_date=?) ");
            qryBuffer.append(" AND transfer_status=? ");
            final String query = qryBuffer.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Query: " + query);
            }
            pstm = p_con.prepareStatement(query);
            pstm.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_processingDate));
            pstm.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_processingDate));
            pstm.setString(3, PretupsI.TXN_STATUS_SUCCESS);
            count = pstm.executeUpdate();
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.TEMP_TABLE_NOT_UPDATED,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.TEMP_TABLE_NOT_UPDATED,e);
        } finally {
            if (pstm != null) {
                try {
                    pstm.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... count: " + count);
            }
        }
        return count;
    }

    /**
     * @return ArrayList
     * @throws BTSLBaseException
     *             Fetch data from temporary table for transaction process
     */
    private static ArrayList fetchDataFromTempTable() throws BTSLBaseException {
        final String METHOD_NAME = "fetchDataFromTempTable";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered ");
        }
        ArrayList dataList = null;
        ResultSet rst = null;
        C2STransferVO c2sTransferVO = null;
        try {
            rst = _fetchDataStmt.executeQuery();
            dataList = new ArrayList();
            while (rst.next()) {
                c2sTransferVO = new C2STransferVO();
                c2sTransferVO.setTransferID(rst.getString("transfer_id"));
                c2sTransferVO.setTransferDate(rst.getDate("transfer_date"));
                c2sTransferVO.setTransferDateTime(rst.getTimestamp("transfer_date_time"));
                c2sTransferVO.setNetworkCode(rst.getString("network_code"));
                c2sTransferVO.setProductCode(rst.getString("product_code"));
                c2sTransferVO.setReceiverMsisdn(rst.getString("receiver_msisdn"));
                c2sTransferVO.setTransferValue(rst.getLong("transfer_value"));
                c2sTransferVO.setServiceType(rst.getString("service_type"));
                c2sTransferVO.setReconciliationDate(rst.getDate("reconciliation_date"));
                c2sTransferVO.setProcessed(rst.getString("processed"));
                c2sTransferVO.setTxnCalculationDone(rst.getString("txn_calculation_done"));
                dataList.add(c2sTransferVO);
            }
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... dataListSize: " + dataList.size());
            }
        }
        return dataList;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_transferDate
     *            Date
     * @param p_setID
     *            String
     * @return ProfileSetVO
     * @throws BTSLBaseException
     *             Load profile details for Transaction profiles only.
     */
    private static ProfileSetVO loadProfileDetails(Date p_transferDate, String p_setID, String p_serviceType) throws BTSLBaseException {
        final String METHOD_NAME = "loadProfileDetails";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered p_transferDate: " + p_transferDate + " p_setID: " + p_setID + " p_serviceType: " + p_serviceType);
        }
        ResultSet rst = null;
        ProfileSetVO profileSetVO = null;
        ProfileSetVersionVO profileSetVersionVO = null;
        ProfileSetDetailsVO profileSetDetailsVO = null;
        ArrayList profileDetailList = null;
        try {
            _loadTxnPrfStmt.clearParameters();
            int i = 1;
            _loadTxnPrfStmt.setString(i++, p_setID);
            _loadTxnPrfStmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferDate));
            _loadTxnPrfStmt.setString(i++, p_serviceType);

            rst = _loadTxnPrfStmt.executeQuery();
            while (rst.next()) {
                if (profileSetVO == null || !profileSetVO.getSetId().equals(profileSetDetailsVO.getSetId())) {
                    profileSetVO = new ProfileSetVO();
                    profileSetVO.setSetId(rst.getString("set_id"));
                    profileSetVersionVO = new ProfileSetVersionVO();
                    profileSetVersionVO.setOneTimeBonus(rst.getLong("one_time_bonus"));
                    profileSetVersionVO.setProductCode(rst.getString("product_code"));
                    profileDetailList = new ArrayList();
                    profileSetVersionVO.setProfileSetDetails(profileDetailList);
                    profileSetVO.setProfileLastVersion(profileSetVersionVO);
                }
                profileSetDetailsVO = new ProfileSetDetailsVO();
                profileSetDetailsVO.setSetId(rst.getString("detail_set_id"));
                profileSetDetailsVO.setDetailId(rst.getString("detail_id"));
                profileSetDetailsVO.setStartRange(rst.getLong("start_range"));
                profileSetDetailsVO.setEndRange(rst.getLong("end_range"));
                profileSetDetailsVO.setProfileType(rst.getString("points_type"));
                profileSetDetailsVO.setPoints(rst.getLong("points"));
                profileSetDetailsVO.setSubscriberType(rst.getString("subscriber_type"));
                profileSetDetailsVO.setProductCode(rst.getString("product_code"));
                profileDetailList.add(profileSetDetailsVO);
            }
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting.....profileSetVO: " + profileSetVO);
            }
        }
        return profileSetVO;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_processingDate
     *            Date
     * @param p_setID
     *            String
     * @param p_period
     *            String
     * @return ProfileSetVO
     * @throws BTSLBaseException
     *             Load profile details for Volume profiles only.
     */
    private static ProfileSetVO loadVolumeProfileDetails(Date p_transferDate, String p_setId, String p_serviceCode, String p_period) throws BTSLBaseException {
        final String METHOD_NAME = "loadVolumeProfileDetails";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered p_transferDate: " + p_transferDate + " p_setId: " + p_setId + " p_serviceCode: " + p_serviceCode + " p_period: " + p_period);
        }
        ResultSet rst = null;
        ProfileSetVO profileSetVO = null;
        ProfileSetVersionVO profileSetVersionVO = null;
        ProfileSetDetailsVO profileSetDetailsVO = null;
        ArrayList profileDetailList = null;
        try {

            int i = 1;
            _loadVolumePrfStmt.clearParameters();
            _loadVolumePrfStmt.setString(i++, p_setId);
            _loadVolumePrfStmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferDate));
            _loadVolumePrfStmt.setString(i++, p_period);
            _loadVolumePrfStmt.setString(i++, p_serviceCode);
            rst = _loadVolumePrfStmt.executeQuery();
            while (rst.next()) {
                if (profileSetVO == null || !profileSetVO.getSetId().equals(profileSetDetailsVO.getSetId())) {
                    profileSetVO = new ProfileSetVO();
                    profileSetVO.setSetId(rst.getString("set_id"));

                    profileSetVersionVO = new ProfileSetVersionVO();
                    profileSetVersionVO.setVersion(rst.getString("version_id"));
                    profileSetVersionVO.setApplicableFrom(rst.getDate("applicable_from"));
                    profileSetVersionVO.setOneTimeBonus(rst.getLong("one_time_bonus"));
                    profileSetVersionVO.setProductCode(rst.getString("product_code"));
                    profileDetailList = new ArrayList();
                    profileSetVersionVO.setProfileSetDetails(profileDetailList);
                    profileSetVO.setProfileLastVersion(profileSetVersionVO);
                }
                profileSetDetailsVO = new ProfileSetDetailsVO();
                profileSetDetailsVO.setSetId(rst.getString("detail_set_id"));
                profileSetDetailsVO.setDetailId(rst.getString("detail_id"));
                profileSetDetailsVO.setType(rst.getString("type"));
                profileSetDetailsVO.setDetailType(rst.getString("detail_type"));
                profileSetDetailsVO.setDetailSubType(rst.getString("detail_subtype"));
                profileSetDetailsVO.setPeriodId(rst.getString("period_id"));
                profileSetDetailsVO.setServiceCode(rst.getString("service_code"));
                profileSetDetailsVO.setStartRange(rst.getLong("start_range"));
                profileSetDetailsVO.setEndRange(rst.getLong("end_range"));
                profileSetDetailsVO.setProfileType(rst.getString("points_type"));
                profileSetDetailsVO.setPoints(rst.getLong("points"));
                profileSetDetailsVO.setSubscriberType(rst.getString("subscriber_type"));
                profileDetailList.add(profileSetDetailsVO);
            }
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting.....profileSetVO: " + profileSetVO);
            }
        }
        return profileSetVO;
    }

    /**
     * @param p_receiverMsisdn
     *            String
     * @param p_p_processingDate
     *            Date
     * @return ActBonusSubsMappingVO
     * @throws BTSLBaseException
     *             Load retailer subscriber mapping from ACT_BONUS_SUBS_MAPPING
     *             table.
     */
    private static RetSubsMappingVO loadRetailerSubscriberMapping(String p_receiverMsisdn, Date p_processingDate) throws BTSLBaseException {
        final String METHOD_NAME = "loadRetailerSubscriberMapping";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered p_receiverMsisdn:" + p_receiverMsisdn, " p_processingDate: " + p_processingDate);
        }
        RetSubsMappingVO actBonusSubsMappingVO = null;
        ResultSet rst = null;
        try {
            _loadmappingStmt.clearParameters();
            _loadmappingStmt.setString(1, p_receiverMsisdn);
            _loadmappingStmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_processingDate));
            rst = _loadmappingStmt.executeQuery();
            if (rst.next()) {
                actBonusSubsMappingVO = new RetSubsMappingVO();
                actBonusSubsMappingVO.setUserID(rst.getString("user_id"));
                actBonusSubsMappingVO.setSubscriberMsisdn(rst.getString("subscriber_msisdn"));
                actBonusSubsMappingVO.setSubscriberType(rst.getString("subscriber_type"));
                actBonusSubsMappingVO.setExpiryDate(rst.getDate("expiry_date"));
                actBonusSubsMappingVO.setActivationBonusGiven(rst.getString("activation_bonus_given"));
                actBonusSubsMappingVO.setStatus(rst.getString("status"));
            }
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting.....  ");
            }
        }
        return actBonusSubsMappingVO;
    }

    /**
     * @param p_txnCalculationDone
     *            String
     * @param p_transferID
     *            String
     * @return int
     * @throws BTSLBaseException
     *             Update Temporary table, C2S_TRANSFERS_TEMP after processing
     *             record.
     */
    private static int updateTempTable(String p_txnCalculationDone, String p_transferID) throws BTSLBaseException {
        final String METHOD_NAME = "updateTempTable";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered  p_txnCalculationDone: " + p_txnCalculationDone + " p_transferID: " + p_transferID);
        }
        int updateCount = 0;
        try {
            _updateTempTableStmt.clearParameters();
            int i = 1;
            _updateTempTableStmt.setString(i++, p_txnCalculationDone);
            _updateTempTableStmt.setString(i++, p_transferID);
            updateCount = _updateTempTableStmt.executeUpdate();
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.TEMP_TABLE_NOT_UPDATED,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.TEMP_TABLE_NOT_UPDATED,e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... updateCount: " + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * @param p_bonusVO
     *            BonusVO
     * @return int
     * @throws BTSLBaseException
     *             Insert values in BONUS table after calculation bonus.
     */
    private static int saveBonus(ActivationBonusVO p_bonusVO) throws BTSLBaseException {
        final String METHOD_NAME = "saveBonus";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered p_processingDate: p_bonusVO: " + p_bonusVO.toString());
        }
        int count = 0;
        try {
            _saveBonusStmt.clearParameters();
            _saveBonusStmt.setString(1, p_bonusVO.getProfileType());
            _saveBonusStmt.setString(2, p_bonusVO.getUserId());
            _saveBonusStmt.setDouble(3, p_bonusVO.getPoints());
            _saveBonusStmt.setString(4, p_bonusVO.getBucketCode());
            _saveBonusStmt.setString(5, p_bonusVO.getProductCode());
            _saveBonusStmt.setDate(6, BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getPointsDate()));
            _saveBonusStmt.setString(7, p_bonusVO.getLastRedemptionId());
            _saveBonusStmt.setDate(8, BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getLastRedemptionDate()));
            _saveBonusStmt.setString(9, p_bonusVO.getLastAllocationType());
            _saveBonusStmt.setDate(10, BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getLastAllocationdate()));
            _saveBonusStmt.setDate(11, BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getCreatedOn()));
            _saveBonusStmt.setString(12, p_bonusVO.getCreatedBy());
            _saveBonusStmt.setDate(13, BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getModifiedOn()));
            _saveBonusStmt.setString(14, p_bonusVO.getModifiedBy());
            _saveBonusStmt.setString(15, p_bonusVO.getTransferId());
            count = _saveBonusStmt.executeUpdate();
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.INSERTION_ERROR_BONUS_TABLE,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.INSERTION_ERROR_BONUS_TABLE,e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... count: " + count);
            }
        }
        return count;
    }

    /**
     * @param con
     *            Connection
     * @param processStatusVolumeVO
     *            ProcessStatusVO
     * @return boolean
     * @throws BTSLBaseException
     *             Process volume transactions on the basis of total transaction
     *             amount
     *             and total transaction count.
     *             1.check volume process status and marrk it U.
     *             2.Load value of constant parameter VOLUME_CALCULATION_ALLOWED
     *             3.If it's value is "N" then give bonus to those transactions
     *             that have txn_calculation_done="N" in C2S_TRANSFERS_TABLE.
     *             4.If it is "Y" then calculate bonus for all transactions in
     *             C2S_TRANSFERS_TEMP table.
     *             6.Load volume profiles from
     *             PROFILES_SET,PROFILE_SET_VERSION,PROFILE_SET_DETAIL table
     *             7.Load data from temporary table.
     *             8.Load subscriber retailer mapping from
     *             ACT_BONUS_SUBS_MAPPING table.
     *             9.Insert data in USER_TXN table, after calculating total
     *             transaction amount and counts for 1 subscriber.
     *             10.Load data from USER_TXN table.
     *             11.Call operatorUtil method to calculate bonus, from Amount
     *             and Counts
     *             12.save data in BONUS table.
     *             13.Delete temporay table.
     *             14.Update process details in PROCESS_STATUS table.
     */
    private static boolean processVolumeTransaction(Connection con, ProcessStatusVO processStatusVolumeVO) throws BTSLBaseException {
        final String METHOD_NAME = "processVolumeTransaction";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered processStatusVolumeVO: " + processStatusVolumeVO);
        }
        Date processedUpto = null;
        Date currentDate = null;
        final int beforeInterval = 0;
        String volumeCalculation = null;
        String allowedPeriodicity = null;
        ArrayList dataList = null;
        ProfileSetVO profileSetVO = null;
        ActivationBonusVO bonusVO = null;
        long count = 0;
        UserTransactionVO userTransactionVO = null;
        ProcessStatusDAO processStatusDAO = null;
        String userId = null;
        String setId = null;
        ActivationBonusVO bonusOldVO = null;
        boolean status = false;
        String productCode = null;
        String[] periodArray = null;
        C2STransferVO c2sTransferVO = null;
        try {
            _logger.debug(METHOD_NAME, " Start of volume process Total memory: " + Runtime.getRuntime().totalMemory() / 1048576 + " Free memory: " + Runtime.getRuntime()
                .freeMemory() / 1048576);
            processedUpto = processStatusVolumeVO.getExecutedUpto();
            currentDate = BTSLUtil.getSQLDateFromUtilDate(_currentDate);
            if (BTSLUtil.getDifferenceInUtilDates(processedUpto, currentDate) <= 1) {
                _logger.error(METHOD_NAME, " Process already executed.....");
                throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.VOLUME_PROCESS_ALREADY_EXECUTED);
            }
            processStatusVolumeVO.setStartDate(_currentDate);
            processStatusVolumeVO.setProcessStatus(ProcessI.STATUS_UNDERPROCESS);
            processStatusDAO = new ProcessStatusDAO();
            // update process details, set process status of volume process to
            // underprocess
            if (processStatusDAO.updateProcessDetail(con, processStatusVolumeVO) > 0) {
                processStatusVolumeVO.setStatusOkBool(true);
            }
            if (processStatusVolumeVO.isStatusOkBool()) {
                con.commit();
                processedUpto = BTSLUtil.addDaysInUtilDate(processStatusVolumeVO.getExecutedUpto(), (beforeInterval + 1));
                volumeCalculation = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOLUME_CALC_ALLOWED);
                if (BTSLUtil.isNullString(volumeCalculation)) {
                    _logger.debug(METHOD_NAME, "Parameter VOLUME_CALC_ALLOWED not found in system preference, using default value as 'N'");
                    volumeCalculation = "N";
                } else {
                    _logger.debug(METHOD_NAME, "Value of parameter VOLUME_CALC_ALLOWED is: " + volumeCalculation);
                }
                allowedPeriodicity = Constants.getProperty("ACT_BONUS_CALC_PERIODICITY");
                if (BTSLUtil.isNullString(allowedPeriodicity)) {
                    _logger.debug(METHOD_NAME, "Parameter ACT_BONUS_CALC_PERIODICITY not found in constants file");
                    throw new BTSLBaseException("loadConstantValues", PretupsErrorCodesI.ACT_BONUS_CALC_PERIODICITY_NOT_FOUND,
                        " could not found value for ACT_BONUS_CALC_PERIODICITY in constant file ");
                } else {
                    _logger.debug(METHOD_NAME, "Value of parameter ACT_BONUS_CALC_PERIODICITY is: " + allowedPeriodicity);
                }
                periodArray = allowedPeriodicity.split(",");
                // first check user_txns table is already updated fror DAILY
                // periodicity and for transaction date,
                // if it is not updated then insert values in user_txns table
                // for DAILY periodicity
                if (!checkUserTxnAlreadyUpdated(con, processedUpto, PretupsI.PERIOD_DAILY)) {
                    count = fetchAndInsertVolumeData(con, volumeCalculation);
                    if (count < 0) {
                        _logger.debug(METHOD_NAME, "Error while inserting data in user_txn table");
                        throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.INSERTION_ERROR_USER_TXN);
                    }
                }
                allowedPeriodicity = "";
                for (int i = 0; i < periodArray.length; i++) {
                    if (PretupsI.PERIOD_UNLIMITED.equalsIgnoreCase(periodArray[i]) && !checkUserTxnAlreadyUpdated(con, processedUpto, PretupsI.PERIOD_UNLIMITED)) {
                        dataList = fetchVolumeDataFromTempTable(con, processedUpto, volumeCalculation);
                        for (int j = 0; j < dataList.size(); j++) {
                            c2sTransferVO = (C2STransferVO) dataList.get(j);
                            userTransactionVO = userAlreadyExistInUserTxn(c2sTransferVO.getUserId(), c2sTransferVO.getSubscriberType(), PretupsI.PERIOD_UNLIMITED,
                                c2sTransferVO.getServiceType(), processedUpto);
                            if (userTransactionVO != null) {
                                userTransactionVO.setAmount(userTransactionVO.getAmount() + c2sTransferVO.getTransferValue());
                                userTransactionVO.setCount(userTransactionVO.getCount() + c2sTransferVO.getCounts());
                                userTransactionVO.setTxnDate(processedUpto);
                                updateUserTxnTable(userTransactionVO);
                            } else {
                                userTransactionVO = new UserTransactionVO();
                                userTransactionVO.setProfileType(PretupsI.PROFILE_TYPE_ACTIVATION);
                                userTransactionVO.setUserIdOrMsisdn(c2sTransferVO.getUserId());
                                userTransactionVO.setPeriodicity(PretupsI.PERIOD_UNLIMITED);
                                userTransactionVO.setUserCategory(c2sTransferVO.getCategoryCode());
                                userTransactionVO.setServiceType(c2sTransferVO.getServiceType());
                                userTransactionVO.setCount(c2sTransferVO.getCounts());
                                userTransactionVO.setAmount(c2sTransferVO.getTransferValue());
                                userTransactionVO.setSubscriberType(c2sTransferVO.getSubscriberType());
                                count = saveUserTransaction(processedUpto, userTransactionVO);
                                if (count <= 0) {
                                    _logger.debug(METHOD_NAME, "temporary table status not updated");
                                    throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.INSERTION_ERROR_USER_TXN);
                                }
                            }
                        }
                    } else if (PretupsI.PERIOD_WEEKLY.equalsIgnoreCase(periodArray[i]) && !checkUserTxnAlreadyUpdated(con, processedUpto, PretupsI.PERIOD_WEEKLY)) {
                        final Calendar calender = BTSLDateUtil.getInstance();
                        calender.setTime(processedUpto);
                        if (Calendar.SUNDAY == calender.getFirstDayOfWeek()) {
                            final Date fromDate = BTSLUtil.addDaysInUtilDate(processedUpto, -7);
                            count = fetchAndInsertDataWeeklyMonthly(con, PretupsI.PERIOD_WEEKLY, processedUpto, fromDate);
                            if (count <= 0) {
                                _logger.debug(METHOD_NAME, "Error while inserting data for weekly process in user_txn table");
                                throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.INSERTION_ERROR_USER_TXN);
                            }
                        } else {
                            _logger.debug(METHOD_NAME, "Today is not the first day of week");
                        }
                    } else if (PretupsI.PERIOD_MONTHLY.equalsIgnoreCase(periodArray[i]) && !checkUserTxnAlreadyUpdated(con, processedUpto, PretupsI.PERIOD_MONTHLY)) {
                        final Calendar calender = BTSLDateUtil.getInstance();
                        calender.setTime(processedUpto);
                        calender.add(Calendar.DATE, 1);
                        calender.add(Calendar.MONTH, 1);
                        final Date nextDay = calender.getTime();
                        if (BTSLUtil.getDifferenceInUtilDates(nextDay, processedUpto) == 1) {
                            final Calendar newCalender = BTSLDateUtil.getInstance();
                            newCalender.setTime(processedUpto);
                            final Date fromDate = BTSLUtil.addDaysInUtilDate(processedUpto, -(newCalender.getActualMaximum(Calendar.DAY_OF_MONTH)));
                            count = fetchAndInsertDataWeeklyMonthly(con, PretupsI.PERIOD_MONTHLY, processedUpto, fromDate);
                        } else {
                            _logger.debug(METHOD_NAME, "Today is not the first day of month");
                        }
                    }
                    if (i == periodArray.length - 1) {
                        allowedPeriodicity = allowedPeriodicity + "'" + periodArray[i] + "'";
                    } else {
                        allowedPeriodicity = allowedPeriodicity + "'" + periodArray[i] + "',";
                    }
                }
                _logger.debug(METHOD_NAME,
                    " After fetching and inserting volume data Total memory: " + Runtime.getRuntime().totalMemory() / 1048576 + " Free memory: " + Runtime.getRuntime()
                        .freeMemory() / 1048576);
                // load user_txns on the basis of periodicity, transaction date
                dataList = loadUserTxn(con, processedUpto, allowedPeriodicity);
                for (int i = 0; i < dataList.size(); i++) {
                    userTransactionVO = (UserTransactionVO) dataList.get(i);
                    userId = userTransactionVO.getUserIdOrMsisdn();
                    // load setId from Map, if setId is null then load it from
                    // user_oth_profiles
                    // table on the basis of user_id if set id is again null
                    // then load it
                    // from profile_mapping table on the basis of category of
                    // user
                    if (_setIdMap != null) {
                        setId = (String) _setIdMap.get(userId);
                    }
                    if (BTSLUtil.isNullString(setId)) {
                        setId = loadUserOtherProfile(userId);
                        if (BTSLUtil.isNullString(setId)) {
                            setId = loadCategoryLevelProfile(userId);
                            if (BTSLUtil.isNullString(setId)) {
                                _logger.debug(METHOD_NAME, "No user level and category level activation profile found for user: " + userId);
                                ProfileBonusLog.log("No user level and category level activation profile found", null, userId, "NA", 0, PretupsBL
                                    .getDisplayAmount(userTransactionVO.getAmount()), 0);
                                continue;
                            }
                        }
                        _setIdMap.put(userId, setId);
                    }
                    // load volume profile details on the basis of setId,
                    // service_type and periodicity
                    profileSetVO = loadVolumeProfileDetails(processedUpto, setId, userTransactionVO.getServiceType(), userTransactionVO.getPeriodicity());
                    if (profileSetVO == null) {
                        _logger.debug(METHOD_NAME, "No volume profile detail found for set id " + setId + " for user: " + userId);
                        ProfileBonusLog.log("No profile detail found for volume process", null, userId, "NA", 0, PretupsBL.getDisplayAmount(userTransactionVO.getAmount()), 0);
                        continue;
                    }
                    // if periodicity is unlimited then calculate bonus of user
                    if (PretupsI.PERIOD_UNLIMITED.equals(userTransactionVO.getPeriodicity())) {
                        bonusVO = _operatorUtil.calculateActivationVolumeBonusUnlimitedPeriod(userTransactionVO, profileSetVO, userTransactionVO.getSubscriberType());
                        // if periodicity is DAILY/MONTHLY/WEEKLY then calculate
                        // bonus of user
                    } else {
                        bonusVO = _operatorUtil.calculateActivationVolumeBonus(userTransactionVO, profileSetVO, userTransactionVO.getSubscriberType());
                    }

                    if (bonusVO != null) {
                        // update user_txns table if periodicity is unlimited
                        if (PretupsI.PERIOD_UNLIMITED.equals(userTransactionVO.getPeriodicity())) {
                            updateUserTxnTable(userTransactionVO);
                        }

                        // load product code from productMap, if productCode is
                        // null then load it from database
                        if (_productMap != null) {
                            productCode = (String) _productMap.get(userTransactionVO.getServiceType());
                        }
                        if (BTSLUtil.isNullString(productCode)) {
                            productCode = loadProductCode(con, userTransactionVO.getServiceType());
                            _productMap.put(userTransactionVO.getServiceType(), productCode);
                        }
                        // check entry already present in bonus table
                        // corresponding to user_id, points_date,service type
                        bonusOldVO = checkUserAlreadyExist(userId, processedUpto, productCode);
                        if (bonusOldVO != null) {
                            // update entry in bonus table
                            bonusOldVO.setLastAllocationType(PretupsI.PROFILE_VOL);
                            bonusOldVO.setLastAllocationdate(processedUpto);
                            ProfileBonusLog.log("Success", bonusOldVO, userId, "NA", bonusVO.getPoints(), PretupsBL.getDisplayAmount(userTransactionVO.getAmount()),
                                bonusOldVO.getPoints());
                            bonusOldVO.setPoints(bonusOldVO.getPoints() + bonusVO.getPoints());
                            count = updateBonusOfUser(bonusOldVO);
                        } else {
                            // if user does not exist in BONUS table then insert
                            // new entry
                            bonusVO.setProfileType(PretupsI.PROFILE_TYPE_ACTIVATION);
                            bonusVO.setUserId(userId);
                            bonusVO.setBucketCode(PretupsI.BUCKET_ONE);
                            bonusVO.setLastAllocationType(PretupsI.PROFILE_VOL);
                            bonusVO.setPointsDate(processedUpto);
                            bonusVO.setLastAllocationdate(processedUpto);
                            bonusVO.setCreatedOn(currentDate);
                            bonusVO.setCreatedBy(PretupsI.SYSTEM);
                            bonusVO.setModifiedOn(currentDate);
                            bonusVO.setModifiedBy(PretupsI.SYSTEM);
                            bonusVO.setProductCode(productCode);
                            bonusVO.setTransferId(null);
                            // Write Profile Bonus Log
                            ProfileBonusLog.log("Success", bonusVO, userId, "NA", bonusVO.getPoints(), PretupsBL.getDisplayAmount(userTransactionVO.getAmount()), 0);
                            count = saveBonus(bonusVO);
                            if (count <= 0) {
                                _logger.debug(METHOD_NAME, "Entry not inserted in BONUS table");
                                throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.INSERTION_ERROR_BONUS_TABLE);
                            }
                        }
                    }
                    userId = null;
                    setId = null;
                    bonusVO = null;
                    productCode = null;
                    userTransactionVO = null;
                }
                try {
                    con.commit();
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                }
                // Drop temporary table and index on table
                if (!dropTempTable(con)) {
                    _logger.debug(METHOD_NAME, "Error in droping table");
                    throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.ERROR_DROPING_TEMP_TABLE);
                }
                processStatusVolumeVO.setExecutedUpto(processedUpto);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                    "ActivationBonusCalculation[processVolumeTransaction]", "", "", "", " Activation bonus process for volume profile is executed successfully.");
                _logger.debug("process", " End of volume process Total memory: " + Runtime.getRuntime().totalMemory() / 1048576 + " Free memory: " + Runtime.getRuntime()
                    .freeMemory() / 1048576);
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "Voulme process executed successfully");
                }
                status = true;
            } else {
                throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
            }
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception e1) {
                    _logger.errorTrace(METHOD_NAME, e1);
                }
            }
            throw be;
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception : " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception e1) {
                    _logger.errorTrace(METHOD_NAME, e1);
                }
            }
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationBonusCalculation[processVolumeTransaction]",
                "", "", "", " Activation Bonus calculation process could not be executed successfully.");
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        } finally {
            try {
                if (processStatusVolumeVO.isStatusOkBool()) {
                    processStatusVolumeVO.setExecutedOn(_currentDate);
                    processStatusVolumeVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    count = (new ProcessStatusDAO()).updateProcessDetail(con, processStatusVolumeVO);
                    if (count > 0) {
                        con.commit();
                    }
                }
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "Exception in updating process status ");
                }
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... ");
            }
        }
        return status;
    }

    /**
     * @param p_processingDate
     *            Date
     * @param userTransactionVO
     *            UserTransactionVO
     * @return int
     * @throws BTSLBaseException
     *             Save user transactions on the basis of user_id and
     *             product_code.
     */
    private static int saveUserTransaction(Date p_processingDate, UserTransactionVO userTransactionVO) throws BTSLBaseException {
        final String METHOD_NAME = "saveUserTransaction";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered p_processingDate: " + p_processingDate + " userTransactionVO: " + userTransactionVO.toString());
        }
        int successCount = 0;
        try {
            int i = 1;
            _saveUserTxnStmt.clearParameters();
            _saveUserTxnStmt.setString(i++, userTransactionVO.getProfileType());
            _saveUserTxnStmt.setString(i++, userTransactionVO.getUserIdOrMsisdn());
            _saveUserTxnStmt.setString(i++, userTransactionVO.getPeriodicity());
            _saveUserTxnStmt.setString(i++, userTransactionVO.getUserCategory());
            _saveUserTxnStmt.setString(i++, userTransactionVO.getServiceType());
            _saveUserTxnStmt.setLong(i++, userTransactionVO.getCount());
            _saveUserTxnStmt.setLong(i++, userTransactionVO.getAmount());
            _saveUserTxnStmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_processingDate));
            _saveUserTxnStmt.setString(i++, userTransactionVO.getSubscriberType());
            successCount = _saveUserTxnStmt.executeUpdate();
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.INSERTION_ERROR_USER_TXN,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.INSERTION_ERROR_USER_TXN,e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... successCount: " + successCount);
            }
        }
        return successCount;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_processingDate
     *            Date
     * @param p_period
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     *             Select records from USER_TXN table from bonus calculation
     *             (Volume profile).
     */
    private static ArrayList loadUserTxn(Connection p_con, Date p_processingDate, String p_period) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserTxn";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered p_processingDate: " + p_processingDate + " p_period: " + p_period);
        }
        ArrayList dataList = null;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        UserTransactionVO userTransactionVO = null;
        try {
            final String p_period1 = p_period.replaceAll("'", "");
            final String p = p_period1.replaceAll("\" ", "");
            final String m_period[] = p.split(",");
            final StringBuffer qryBuffer = new StringBuffer();
            qryBuffer.append(" SELECT profile_type,user_id_or_msisdn, ");
            qryBuffer.append(" periodicity,user_category,service_type,count,amount,txn_date,subscriber_type ");
            qryBuffer.append(" FROM USER_TXNS WHERE profile_type=? AND periodicity IN(");
            for (int i = 0; i < m_period.length; i++) {
                qryBuffer.append(" ?");
                if (i != m_period.length - 1) {
                    qryBuffer.append(",");
                }
            }
            qryBuffer.append(")");
            qryBuffer.append(" AND txn_date=? ");
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Query:" + qryBuffer.toString());
            }
            pstm = p_con.prepareStatement(qryBuffer.toString());
            int i = 1;
            pstm.setString(i++, PretupsI.PROFILE_TYPE_ACTIVATION);
            for (int x = 0; x < m_period.length; x++) {
                pstm.setString(i++, m_period[x]);
            }
            pstm.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_processingDate));
            rst = pstm.executeQuery();
            dataList = new ArrayList();
            while (rst.next()) {
                userTransactionVO = new UserTransactionVO();
                userTransactionVO.setProfileType(rst.getString("profile_type"));
                userTransactionVO.setUserIdOrMsisdn(rst.getString("user_id_or_msisdn"));
                userTransactionVO.setPeriodicity(rst.getString("periodicity"));
                userTransactionVO.setUserCategory(rst.getString("user_category"));
                userTransactionVO.setServiceType(rst.getString("service_type"));
                userTransactionVO.setCount(rst.getLong("count"));
                userTransactionVO.setAmount(rst.getLong("amount"));
                userTransactionVO.setTxnDate(rst.getDate("txn_date"));
                userTransactionVO.setSubscriberType(rst.getString("subscriber_type"));
                dataList.add(userTransactionVO);
            }
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (pstm != null) {
                try {
                    pstm.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... dataListSize: " + dataList.size());
            }
        }
        return dataList;
    }

    /**
     * @param p_con
     *            Connection
     * @return int
     * @throws BTSLBaseException
     *             Drop temporary table, C2S_TRANSFERS_TEMP
     */
    private static boolean dropTempTable(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "dropTempTable";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered");
        }
        PreparedStatement pstm = null;
        boolean status = false;
        try {
          
        	ActivationBonusCalculationQry activationBonusCalculationQry = (ActivationBonusCalculationQry) ObjectProducer.getObject(QueryConstants.ACTIVATION_BONUS_CAL_QRY, QueryConstants.QUERY_PRODUCER);
        	String qryBuffer = activationBonusCalculationQry.dropTempTableQry();
        	if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Query:" + qryBuffer.toString());
            }
            pstm = p_con.prepareStatement(qryBuffer.toString());
            pstm.executeUpdate();
            status = true;
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        } finally {
            if (pstm != null) {
                try {
                    pstm.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... status: " + status);
            }
        }
        return status;
    }

    /**
     * @param p_con
     *            Connection
     * @return String
     * @throws BTSLBaseException
     */
    private static String loadProductCode(Connection p_con, String p_serviceCode) throws BTSLBaseException {
        final String METHOD_NAME = "loadProductCode";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered");
        }
        PreparedStatement pstm = null;
        ResultSet rst = null;
        String productCode = null;
        try {
            final StringBuffer qryBuffer = new StringBuffer();
            qryBuffer.append(" SELECT P.product_code FROM products P,product_service_type_mapping PSM ");
            qryBuffer.append(" WHERE service_type=? AND P.product_type=PSM.product_type ");
            qryBuffer.append("AND P.PRODUCT_CODE=PSM.PRODUCT_CODE ");
            qryBuffer.append(" AND P.status=? AND P.module_code=? ");
            if (_logger.isDebugEnabled()) {
                _logger.debug("loadActiveProductList", "Query:" + qryBuffer.toString());
            }
            pstm = p_con.prepareStatement(qryBuffer.toString());
            pstm.setString(1, p_serviceCode);
            pstm.setString(2, PretupsI.YES);
            pstm.setString(3, PretupsI.C2S_MODULE);
            rst = pstm.executeQuery();
            if (rst.next()) {
                productCode = rst.getString("product_code");
            }
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (pstm != null) {
                try {
                    pstm.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... productCode: " + productCode);
            }
        }
        return productCode;
    }

    /**
     * @param p_useId
     *            String
     * @param p_subscriberMsisdn
     *            String
     * @return int
     * @throws BTSLBaseException
     */
    private static int updateActBonusMappingTable(String p_useId, String p_subscriberMsisdn) throws BTSLBaseException {
        final String METHOD_NAME = "updateActBonusMappingTable";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered p_useId: " + p_useId + " p_subscriberMsisdn: " + p_subscriberMsisdn);
        }
        int successCount = 0;
        try {
            _updateMappingStmt.clearParameters();
            int i = 1;
            _updateMappingStmt.setString(i++, p_useId);
            _updateMappingStmt.setString(i++, p_subscriberMsisdn);
            successCount = _updateMappingStmt.executeUpdate();
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "SQLException: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... successCount: " + successCount);
            }
        }
        return successCount;
    }

    /**
     * @param p_userId
     *            String
     * @param p_processedUpto
     *            Date
     * @param p_productCode
     *            String
     * @return ActivationBonusVO
     * @throws BTSLBaseException
     */
    private static ActivationBonusVO checkUserAlreadyExist(String p_userId, Date p_processedUpto, String p_productCode) throws BTSLBaseException {
        final String METHOD_NAME = "checkUserAlreadyExist";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered p_useId: " + p_userId + " p_processedUpto: " + p_processedUpto + " p_productCode: " + p_productCode);
        }
        ActivationBonusVO bonusVO = null;
        ResultSet rst = null;
        try {
            _checkUserExistStmt.clearParameters();
            _checkUserExistStmt.setString(1, p_userId);
            _checkUserExistStmt.setString(2, p_productCode);
            _checkUserExistStmt.setDate(3, BTSLUtil.getSQLDateFromUtilDate(p_processedUpto));
            rst = _checkUserExistStmt.executeQuery();
            if (rst.next()) {
                bonusVO = new ActivationBonusVO();
                bonusVO.setProfileType(rst.getString("profile_type"));
                bonusVO.setUserId(rst.getString("user_id_or_msisdn"));
                bonusVO.setPoints(rst.getLong("points"));
                bonusVO.setBucketCode(rst.getString("bucket_code"));
                bonusVO.setProductCode(rst.getString("product_code"));
                bonusVO.setPointsDate(rst.getDate("points_date"));
                bonusVO.setLastRedemptionId(rst.getString("last_redemption_id"));
                bonusVO.setLastRedemptionDate(rst.getDate("last_redemption_on"));
                bonusVO.setLastAllocationType(rst.getString("last_allocation_type"));
                bonusVO.setLastAllocationdate(rst.getDate("last_allocated_on"));
                bonusVO.setCreatedOn(rst.getDate("created_on"));
                bonusVO.setCreatedBy(rst.getString("created_by"));
                bonusVO.setModifiedOn(rst.getDate("modified_on"));
                bonusVO.setModifiedBy(rst.getString("modified_by"));
                bonusVO.setTransferId(rst.getString("transfer_id"));
            }
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "SQLException: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting.....bonusVO: " + bonusVO);
            }
        }
        return bonusVO;
    }

    /**
     * @param p_bonusVO
     *            ActivationBonusVO
     * @return int
     * @throws BTSLBaseException
     */
    private static int updateBonusOfUser(ActivationBonusVO p_bonusVO) throws BTSLBaseException {
        final String METHOD_NAME = "updateBonusOfUser";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered p_bonusVO: " + p_bonusVO.toString());
        }
        int count = 0;
        try {
            _updateBonusStmt.clearParameters();
            int i = 1;
            _updateBonusStmt.setDouble(i++, p_bonusVO.getPoints());
            _updateBonusStmt.setString(i++, p_bonusVO.getLastAllocationType());
            _updateBonusStmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getLastAllocationdate()));
            _updateBonusStmt.setString(i++, p_bonusVO.getTransferId());
            _updateBonusStmt.setString(i++, p_bonusVO.getUserId());
            _updateBonusStmt.setString(i++, p_bonusVO.getProductCode());
            _updateBonusStmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getPointsDate()));
            count = _updateBonusStmt.executeUpdate();
        } catch (SQLException se) {
            _logger.error("checkUserAlreadyExist", "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", "checkUserAlreadyExist", PretupsErrorCodesI.BONUS_TABLE_NOT_UPDATED,se);
        } catch (Exception e) {
            _logger.error("checkUserAlreadyExist", "SQLException: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", "checkUserAlreadyExist", PretupsErrorCodesI.BONUS_TABLE_NOT_UPDATED,e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... count: " + count);
            }
        }
        return count;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_periodicity
     *            String
     * @param p_txnCalc
     *            String
     * @return int
     * @throws BTSLBaseException
     *             Fetch data from temporary table for volume process
     */
    private static long fetchAndInsertVolumeData(Connection p_con, String p_txnCalc) throws BTSLBaseException {
        final String METHOD_NAME = "fetchAndInsertVolumeData";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered p_txnCalc: " + p_txnCalc);
        }
        PreparedStatement pstm = null;
        long count = 0;
        try {
            final StringBuffer qryBuffer = new StringBuffer(" ");
            qryBuffer
                .append(" INSERT INTO USER_TXNS (PROFILE_TYPE,USER_ID_OR_MSISDN,PERIODICITY,USER_CATEGORY,SERVICE_TYPE,COUNT,AMOUNT,TXN_DATE,SUBSCRIBER_TYPE,OLD_USER_ID_OR_MSISDN,MSISDN_MODIFIED)(SELECT 'ACT',a.user_id,'DAILY',u.category_code,c.service_type, ");
            qryBuffer.append(" count(1) count, sum(c.transfer_value) AMOUNT,c.transfer_date,a.subscriber_type, u.old_msisdn,u.msisdn_modified ");
            qryBuffer.append(" FROM c2s_transfers_temp c, act_bonus_subs_mapping a, users u WHERE ");
            qryBuffer.append(" a.user_id=u.user_id AND a.subscriber_msisdn=c.receiver_msisdn AND a.status='Y' ");
            qryBuffer.append(" AND (a.expiry_date IS NULL OR  a.expiry_date>=c.transfer_date) ");
            if (PretupsI.NO.equals(p_txnCalc)) {
                qryBuffer.append(" AND c.txn_calculation_done=? ");
            } else {
                qryBuffer.append(" AND c.txn_calculation_done <> ? ");
            }
            qryBuffer.append(" GROUP BY a.user_id,u.category_code,c.service_type,c.transfer_date,a.subscriber_type, u.old_msisdn,u.msisdn_modified ) ");

            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Query: " + qryBuffer.toString());
            }
            pstm = p_con.prepareStatement(qryBuffer.toString());
            if (PretupsI.NO.equals(p_txnCalc)) {
                pstm.setString(1, PretupsI.NO);
            } else {
                pstm.setString(1, PretupsI.NO_MAPPING_FOUND);
            }
            count = pstm.executeUpdate();
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        } finally {
            if (pstm != null) {
                try {
                    pstm.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... Total rows inserted: " + count);
            }
        }
        return count;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_processingDate
     *            Date
     * @return boolean
     */
    private static boolean checkUserTxnAlreadyUpdated(Connection p_con, Date p_processingDate, String p_period) throws BTSLBaseException {
        final String METHOD_NAME = "checkUserTxnAlreadyUpdated";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered p_processingDate:" + p_processingDate);
        }
        PreparedStatement pstm = null;
        ResultSet rst = null;
        boolean isExist = false;
        try {
            final StringBuffer qryBuffer = new StringBuffer();
            qryBuffer.append(" SELECT 1 FROM USER_TXNS WHERE profile_type=? ");
            qryBuffer.append(" AND periodicity=? AND txn_date=? ");
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Query: " + qryBuffer.toString());
            }
            pstm = p_con.prepareStatement(qryBuffer.toString());
            pstm.setString(1, PretupsI.PROFILE_TYPE_ACTIVATION);
            pstm.setString(2, p_period);
            pstm.setDate(3, BTSLUtil.getSQLDateFromUtilDate(p_processingDate));
            rst = pstm.executeQuery();
            if (rst.next()) {
                isExist = true;
            }
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        } finally {
            if (pstm != null) {
                try {
                    pstm.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (rst != null) {
                try {
                    rst.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... isExist: " + isExist);
            }
        }
        return isExist;
    }

    /**
     * Load process details, if process is already marked UnderProcess then
     * check expiry time
     * of process
     * 
     * @param p_con
     *            Connection
     * @param p_processID
     *            String
     * @return ProcessStatusVO
     * @throws BTSLBaseException
     */
    private static ProcessStatusVO checkProcessUnderProcess(Connection p_con, String p_processID) throws BTSLBaseException {
        final String METHOD_NAME = "checkProcessUnderProcess";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered with p_processID=" + p_processID);
        }
        long dateDiffInMinute = 0;
        int successC = 0;
        ProcessStatusDAO processStatusDAO = null;
        ProcessStatusVO processStatusVO = null;
        final Date date = new Date();
        try {
            processStatusDAO = new ProcessStatusDAO();
            // load the Scheduler information - start date and status of
            // scheduler
            processStatusVO = processStatusDAO.loadProcessDetail(p_con, p_processID);

            // Check Process Entry,if no entry for the process throw the
            // exception and stop the process
            if (processStatusVO == null) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusCalculation[checkProcessunderProcess]", "", "", "", "No entry found in the process_status table for processId=" + p_processID);
                throw new BTSLBaseException(METHOD_NAME, PretupsErrorCodesI.PROCESS_ENTRY_NOT_FOUND);
            }
            // if the scheduler status is UnderProcess check the expiry of
            // scheduler.
            else if (ProcessI.STATUS_UNDERPROCESS.equals(processStatusVO.getProcessStatus())) {
                // set the current date while updating the start date of process
                if (processStatusVO.getStartDate() != null) {
                    dateDiffInMinute = ((date.getTime() - processStatusVO.getStartDate().getTime()) / (1000 * 60));
                } else {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "ActivationBonusCalculation[checkProcessunderProcess]", "", "", "", "Process start date is null for processId=" + p_processID);
                    throw new BTSLBaseException(METHOD_NAME, "Process Start Date is NULL");
                }
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "startDate = " + processStatusVO.getStartDate() + "dateDiffInMinute= " + dateDiffInMinute + " expiryTime = " + processStatusVO
                        .getExpiryTime());
                }
                // Checking for the exipry time of the process.
                if (dateDiffInMinute >= processStatusVO.getExpiryTime()) {
                    processStatusVO.setStartDate(date);
                    processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    successC = processStatusDAO.updateProcessDetail(p_con, processStatusVO);
                    if (successC > 0) {
                        processStatusVO.setStatusOkBool(true);
                    } else {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "ActivationBonusCalculation[checkProcessunderProcess]", "", "", "",
                            "The entry in the process_status could not be updated to 'Underprocess' after the expiry of underprocess time limit for processId=" + p_processID);
                        throw new BTSLBaseException(METHOD_NAME, PretupsErrorCodesI.PROCESS_ERROR_UPDATE_STATUS);
                    }
                } else {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "ActivationBonusCalculation[checkProcessunderProcess]", "", "", "", "Process is already running for processId=" + p_processID);
                    throw new BTSLBaseException(METHOD_NAME, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
                }
            }
        }// end of try-block
        catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException while loading process detail" + be);
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception while loading process detail " + e);
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusCalculation[checkProcessUnderProcess]",
                "processStatusVO.getProcessID()" + processStatusVO.getProcessID(), "", "", " Exception while loadng process detail " + e.getMessage());
            throw new BTSLBaseException(METHOD_NAME, e.getMessage());
        }// end of catch-Exception
        finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting processStatusVO=" + processStatusVO);
            }
        }// end of finally
        return processStatusVO;
    }// end of checkProcessUnderProcess

    /**
     * generate preparedStatement for all the queries that are used more
     * 
     * @param p_con
     *            Connection
     * @throws BTSLBaseException
     */
    private static void makeQuery(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "makeQuery";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered");
        }
        String query = null;
        try {
            // fetch data from temp table for transaction based calculation,
            // method name: fetchDataFromTempTable()
           
        	ActivationBonusCalculationQry activationBonusCalculationQry = (ActivationBonusCalculationQry) ObjectProducer.getObject(QueryConstants.ACTIVATION_BONUS_CAL_QRY, QueryConstants.QUERY_PRODUCER);

            query = activationBonusCalculationQry.selectFromC2STransferTemp();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Query: " + query);
            }
            _fetchDataStmt = p_con.prepareStatement(query);
            _fetchDataStmt.setLong(1, _noOfRecordProcess);

            // load profile details for transaction based calculation, profile
            // details are picked on the basis of set id, mathod name:
            // loadProfileDetails()
            StringBuffer qryBuffer = new StringBuffer();
            qryBuffer.append(" SELECT s.set_id, v.version version_id, d.set_id detail_set_id, ");
            qryBuffer.append(" v.applicable_from, v.one_time_bonus, d.product_code, ");
            qryBuffer.append(" d.detail_id,d.type, d.detail_type, ");
            qryBuffer.append(" d.service_code,d.start_range, d.end_range,d.points_type,d.points, ");
            qryBuffer.append(" d.subscriber_type FROM profile_set s,profile_set_version v, ");
            qryBuffer.append(" profile_details d WHERE s.set_id =? AND d.set_id=s.set_id AND ");
            qryBuffer.append(" s.set_id = v.set_id AND v.version=d.version AND ");
            qryBuffer.append(" (v.applicable_from =(SELECT max(v2.applicable_from) FROM profile_set_version ");
            qryBuffer.append(" v2 WHERE v2.applicable_from <= ? AND s.set_id = v2.set_id )) ");
            qryBuffer.append(" AND s.status <> 'N' AND v.status <> 'N'  AND d.service_code=? AND d.type='C2S' ");
            qryBuffer.append(" AND s.profile_type='ACT' AND d.detail_type='TRANS' and d.user_type='S' ");
            query = qryBuffer.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Query: " + query);
            }
            _loadTxnPrfStmt = p_con.prepareStatement(query);

            // load profile details for volume based calculation, profile
            // details are picked on the basis of set id, mathod name:
            // loadVolumeProfileDetails()
            qryBuffer = new StringBuffer();
            qryBuffer.append(" SELECT s.set_id, v.version version_id, d.set_id detail_set_id, ");
            qryBuffer.append(" v.applicable_from, v.one_time_bonus, d.product_code, ");
            qryBuffer.append(" d.detail_id,d.type, d.detail_type, d.detail_subtype,d.period_id, ");
            qryBuffer.append(" d.service_code,d.start_range, d.end_range,d.points_type,d.points, ");
            qryBuffer.append(" d.subscriber_type from profile_set s,profile_set_version v, ");
            qryBuffer.append(" profile_details d where s.set_id=? and d.set_id=s.set_id and  ");
            qryBuffer.append(" s.set_id = v.set_id AND v.version=d.version AND ");
            qryBuffer.append(" (v.applicable_from =(SELECT max(v2.applicable_from) FROM profile_set_version ");
            qryBuffer.append(" v2 WHERE v2.applicable_from <= ? and s.set_id = v2.set_id )) ");
            qryBuffer.append(" AND s.status <> 'N' AND v.status <> 'N' AND d.type='C2S' ");
            qryBuffer.append(" AND s.profile_type='ACT' AND d.detail_type='VOLUME' AND ");
            qryBuffer.append(" d.user_type='S' AND d.period_id=? AND service_code=? ORDER BY d.start_range");
            query = qryBuffer.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Query: " + query);
            }
            _loadVolumePrfStmt = p_con.prepareStatement(query);

            // update temporary table at the time of transaction based
            // calculation, updateTempTable()
            qryBuffer = new StringBuffer();
            qryBuffer.append(" UPDATE c2s_transfers_temp SET processed='Y', txn_calculation_done=? ");
            qryBuffer.append(" WHERE transfer_id=? ");
            query = qryBuffer.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Query: " + query);
            }
            _updateTempTableStmt = p_con.prepareStatement(query);

            // load retailer subscriber mapping, method name:
            // loadRetailerSubscriberMapping()
            qryBuffer = new StringBuffer();
            qryBuffer.append(" SELECT a.user_id, a.subscriber_msisdn, a.subscriber_type, a.set_id, a.version, ");
            qryBuffer.append(" a.expiry_date, a.registered_on,a.activation_bonus_given, ");
            qryBuffer.append(" a.status,a.created_on,a.created_by,a.modified_on,a.modified_by,a.network_code ");
            qryBuffer.append(" FROM act_bonus_subs_mapping a WHERE a.subscriber_msisdn=? AND a.registered_on<=? ");
            qryBuffer.append(" ORDER BY a.registered_on DESC ");
            query = qryBuffer.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "_loadmappingStmt: " + query);
            }
            _loadmappingStmt = p_con.prepareStatement(query);

            // update act_bonus_subs_mapping table if one time bonus is given to
            // channel user, updateActBonusMappingTable()
            qryBuffer = new StringBuffer();
            qryBuffer.append(" UPDATE act_bonus_subs_mapping ");
            qryBuffer.append(" SET activation_bonus_given='Y' WHERE user_id=? ");
            qryBuffer.append(" AND subscriber_msisdn=? AND status='Y' ");
            query = qryBuffer.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "_updateMappingStmt: " + query);
            }
            _updateMappingStmt = p_con.prepareStatement(query);

            // load set id from user_oth_profiles table on the basis of user_id,
            // loadUserOtherProfile()
            qryBuffer = new StringBuffer();
            qryBuffer.append(" SELECT set_id ");
            qryBuffer.append(" FROM user_oth_profiles WHERE user_id=? ");
            qryBuffer.append(" AND profile_type='ACT' ");
            query = qryBuffer.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "_loadSetIdStmt: " + query);
            }
            _loadSetIdStmt = p_con.prepareStatement(query);

            // load set id from profile_mapping table on the basis of category
            // of user, loadCategoryLevelProfile()
            qryBuffer = new StringBuffer();
            qryBuffer.append(" SELECT p.set_id, u.category_code FROM profile_mapping p, users u ");
            qryBuffer.append(" WHERE u.user_id=? AND u.category_code=p.srv_class_or_category_code ");
            qryBuffer.append(" and profile_type= 'ACT' AND p.is_default='Y' ");
            query = qryBuffer.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "_categoryProfileStmt: " + query);
            }
            _categoryProfileStmt = p_con.prepareStatement(query);

            // check user details exist in BONUS table, before adding new
            // entries, checkUserAlreadyExist()
         
            query = activationBonusCalculationQry.selectFromBonusQry();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Query: " + query);
            }
            _checkUserExistStmt = p_con.prepareStatement(query);

            // insert entries in bonus table if user does not exist, saveBonus()
            qryBuffer = new StringBuffer();
            qryBuffer.append(" INSERT INTO BONUS (profile_type,user_id_or_msisdn,points, ");
            qryBuffer.append(" bucket_code,product_code,points_date,last_redemption_id,last_redemption_on, ");
            qryBuffer.append(" last_allocation_type,last_allocated_on,created_on,created_by,modified_on, ");
            qryBuffer.append(" modified_by,transfer_id)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            query = qryBuffer.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "_saveBonusStmt:" + query);
            }
            _saveBonusStmt = p_con.prepareStatement(query);

            // insert entries in user_txn table for volume based calculation,
            // saveUserTransaction()
            qryBuffer = new StringBuffer();
            qryBuffer.append(" INSERT INTO USER_TXNS (profile_type,user_id_or_msisdn, ");
            qryBuffer.append(" periodicity,user_category,service_type,count,amount,txn_date,subscriber_type) ");
            qryBuffer.append(" VALUES (?,?,?,?,?,?,?,?,?) ");
            query = qryBuffer.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "_saveUserTxnStmt:" + qryBuffer.toString());
            }
            _saveUserTxnStmt = p_con.prepareStatement(qryBuffer.toString());

            // if user exist in bonus table then update entries corresponding to
            // user, updateBonusOfUser()
            qryBuffer = new StringBuffer();
            qryBuffer.append(" UPDATE BONUS SET points=?, last_allocation_type=?,last_allocated_on=?, ");
            qryBuffer.append(" transfer_id=? WHERE user_id_or_msisdn=? AND profile_type='ACT' AND  ");
            qryBuffer.append(" product_code=?  AND points_date=? AND bucket_code='1' ");
            query = qryBuffer.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "_updateBonusStmt: " + query);
            }
            _updateBonusStmt = p_con.prepareStatement(query);

            // update user_txn table, updateUserTxnTable()
            qryBuffer = new StringBuffer();
            qryBuffer.append(" UPDATE USER_TXNS SET count=?, amount=?,txn_date=? ");
            qryBuffer.append(" WHERE profile_type='ACT' AND user_id_or_msisdn=? AND ");
            qryBuffer.append(" periodicity=? AND service_type=? AND subscriber_type=? ");
            query = qryBuffer.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "_updateUsrTxnStmt: " + query);
            }
            _updateUsrTxnStmt = p_con.prepareStatement(query);

            // check user exist in user_txns table, userAlreadyExistInUserTxn()
            qryBuffer = new StringBuffer();
            qryBuffer.append(" SELECT profile_type,user_id_or_msisdn, subscriber_type, ");
            qryBuffer.append(" periodicity,user_category,service_type,count,amount,txn_date ");
            qryBuffer.append(" FROM USER_TXNS WHERE profile_type='ACT' AND user_id_or_msisdn=? ");
            qryBuffer.append(" AND periodicity=? AND service_type=? AND subscriber_type=? ");
            query = qryBuffer.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "_userInUserTxnStmt: " + query);
            }
            _userInUserTxnStmt = p_con.prepareStatement(query);

            query = null;
            qryBuffer = null;
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... ");
            }
        }
    }

    /**
     * create index on TRANSFER_ID field of temp table
     * 
     * @param p_con
     *            Connection
     * @throws BTSLBaseException
     */
    private static void createIndex(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "createIndex";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered");
        }
        PreparedStatement pstm = null;
        try {
            final StringBuffer qryBuffer = new StringBuffer();
            qryBuffer.append(" CREATE UNIQUE INDEX PK_C2S_TRANSFER_TEMP ON C2S_TRANSFERS_TEMP (TRANSFER_ID) ");
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Query: " + qryBuffer.toString());
            }
            pstm = p_con.prepareStatement(qryBuffer.toString());
            pstm.executeUpdate();
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        } finally {
            if (pstm != null) {
                try {
                    pstm.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... ");
            }
        }
    }

    /**
     * @param p_userId
     *            String
     * @return String
     * @throws BTSLBaseException
     */
    private static String loadUserOtherProfile(String p_userId) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserOtherProfile";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered p_userId: " + p_userId);
        }
        ResultSet rst = null;
        String setId = null;
        try {
            _loadSetIdStmt.clearParameters();
            _loadSetIdStmt.setString(1, p_userId);
            rst = _loadSetIdStmt.executeQuery();
            if (rst.next()) {
                setId = rst.getString("set_id");
            }
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "SQLException: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... setId: " + setId);
            }
        }
        return setId;
    }

    /**
     * @param p_userId
     *            String
     * @return String
     * @throws BTSLBaseException
     */
    private static String loadCategoryLevelProfile(String p_userId) throws BTSLBaseException {
        final String METHOD_NAME = "loadCategoryLevelProfile";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered p_userId: " + p_userId);
        }
        ResultSet rst = null;
        String setId = null;
        try {
            _categoryProfileStmt.clearParameters();
            _categoryProfileStmt.setString(1, p_userId);
            rst = _categoryProfileStmt.executeQuery();
            if (rst.next()) {
                setId = rst.getString("set_id");
            }
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "SQLException: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... setId: " + setId);
            }
        }
        return setId;
    }

    /**
     * @param p_userId
     *            String
     * @param p_subscriberType
     *            String
     * @param p_periodicity
     *            String
     * @param p_productCode
     *            String
     * @param p_processedUpto
     *            Date
     * @return UserTransactionVO
     * @throws BTSLBaseException
     */
    private static UserTransactionVO userAlreadyExistInUserTxn(String p_userId, String p_subscriberType, String p_periodicity, String p_serviceType, Date p_processedUpto) throws BTSLBaseException {
        final String METHOD_NAME = "userAlreadyExistInUserTxn";
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    METHOD_NAME,
                    "Entered p_userId: " + p_userId + " p_subscriberType: " + p_subscriberType + " p_periodicity: " + p_periodicity + " p_serviceType: " + p_serviceType + " p_processedUpto: " + p_processedUpto);
        }
        UserTransactionVO userTransactionVO = null;
        ResultSet rst = null;
        try {
            _userInUserTxnStmt.clearParameters();
            int i = 1;
            _userInUserTxnStmt.setString(i++, p_userId);
            _userInUserTxnStmt.setString(i++, p_periodicity);
            _userInUserTxnStmt.setString(i++, p_serviceType);
            _userInUserTxnStmt.setString(i++, p_subscriberType);
            rst = _userInUserTxnStmt.executeQuery();
            if (rst.next()) {
                userTransactionVO = new UserTransactionVO();
                userTransactionVO.setProfileType(rst.getString("profile_type"));
                userTransactionVO.setUserIdOrMsisdn(rst.getString("user_id_or_msisdn"));
                userTransactionVO.setSubscriberType(rst.getString("subscriber_type"));
                userTransactionVO.setPeriodicity(rst.getString("periodicity"));
                userTransactionVO.setUserCategory(rst.getString("user_category"));
                userTransactionVO.setServiceType(rst.getString("service_type"));
                userTransactionVO.setCount(rst.getLong("count"));
                userTransactionVO.setAmount(rst.getLong("amount"));
                userTransactionVO.setTxnDate(rst.getDate("txn_date"));
            }
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "SQLException: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... userTransactionVO: " + userTransactionVO);
            }
        }
        return userTransactionVO;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_processingDate
     *            Date
     * @param p_txnCalc
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     *             Fetch data from temporary table for volume process
     */
    private static ArrayList fetchVolumeDataFromTempTable(Connection p_con, Date p_processingDate, String p_txnCalc) throws BTSLBaseException {
        final String METHOD_NAME = "fetchVolumeDataFromTempTable";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered p_processingDate: " + p_processingDate + " p_txnCalc: " + p_txnCalc);
        }
        ArrayList dataList = null;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        C2STransferVO c2sTransferVO = null;
        try {
            final StringBuffer qryBuffer = new StringBuffer(" ");
            qryBuffer.append(" SELECT a.user_id, count(1) counts, sum(c.transfer_value) transfer_value, c.service_type, ");
            qryBuffer.append(" u.category_code,a.subscriber_type FROM c2s_transfers_temp c, act_bonus_subs_mapping a, users u WHERE ");
            qryBuffer.append(" a.user_id=u.user_id AND a.subscriber_msisdn=c.receiver_msisdn AND a.status='Y' ");
            qryBuffer.append(" AND (a.expiry_date IS NULL OR  a.expiry_date>=?) ");
            if (PretupsI.NO.equals(p_txnCalc)) {
                qryBuffer.append(" AND c.txn_calculation_done=? ");
            } else {
                qryBuffer.append(" AND c.txn_calculation_done <> ? ");
            }
            qryBuffer.append(" GROUP BY a.user_id,c.service_type,u.category_code,a.subscriber_type ");
            
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Query: " + qryBuffer.toString());
            }
            pstm = p_con.prepareStatement(qryBuffer.toString());
            pstm.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_processingDate));
            if (PretupsI.NO.equals(p_txnCalc)) {
                pstm.setString(2, PretupsI.NO);
            } else {
                pstm.setString(2, PretupsI.NO_MAPPING_FOUND);
            }
            rst = pstm.executeQuery();
            dataList = new ArrayList();
            while (rst.next()) {
                c2sTransferVO = new C2STransferVO();
                c2sTransferVO.setUserId(rst.getString("user_id"));
                c2sTransferVO.setCounts(rst.getLong("counts"));
                c2sTransferVO.setTransferValue(rst.getLong("transfer_value"));
                c2sTransferVO.setServiceType(rst.getString("service_type"));
                c2sTransferVO.setCategoryCode(rst.getString("category_code"));
                c2sTransferVO.setSubscriberType(rst.getString("subscriber_type"));
                dataList.add(c2sTransferVO);
            }
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (pstm != null) {
                try {
                    pstm.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... dataListSize: " + dataList.size());
            }
        }
        return dataList;
    }

    /**
     * @param p_userTransactionVO
     *            UserTransactionVO
     * @return int
     * @throws BTSLBaseException
     */
    private static int updateUserTxnTable(UserTransactionVO p_userTransactionVO) throws BTSLBaseException {
        final String METHOD_NAME = "updateUserTxnTable";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered p_userTransactionVO: " + p_userTransactionVO);
        }
        int count = 0;
        try {
            _updateUsrTxnStmt.clearParameters();
            int i = 1;
            _updateUsrTxnStmt.setLong(i++, p_userTransactionVO.getCount());
            _updateUsrTxnStmt.setLong(i++, p_userTransactionVO.getAmount());
            _updateUsrTxnStmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_userTransactionVO.getTxnDate()));
            _updateUsrTxnStmt.setString(i++, p_userTransactionVO.getUserIdOrMsisdn());
            _updateUsrTxnStmt.setString(i++, p_userTransactionVO.getPeriodicity());
            _updateUsrTxnStmt.setString(i++, p_userTransactionVO.getServiceType());
            _updateUsrTxnStmt.setString(i++, p_userTransactionVO.getSubscriberType());
            count = _updateUsrTxnStmt.executeUpdate();
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.USER_TXN_TABLE_NOT_UPDATED,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "SQLException: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.USER_TXN_TABLE_NOT_UPDATED,e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... count: " + count);
            }
        }
        return count;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_period
     *            String
     * @param p_toDate
     *            Date
     * @param p_fromDate
     *            Date
     * @return long
     * @throws BTSLBaseException
     */
    private static long fetchAndInsertDataWeeklyMonthly(Connection p_con, String p_period, Date p_toDate, Date p_fromDate) throws BTSLBaseException {
        final String METHOD_NAME = "fetchAndInsertDataWeeklyMonthly";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered p_period: " + p_period + " p_toDate: " + p_toDate + " p_fromDate: " + p_fromDate);
        }
        PreparedStatement pstm = null;
        long count = 0;
        try {
            final StringBuffer qryBuffer = new StringBuffer(" ");
            qryBuffer.append(" INSERT INTO user_txns SELECT ?,ux.user_id_or_msisdn,?,ux.user_category, ");
            qryBuffer.append(" ux.service_type,sum(ux.count) count, sum(ux.amount) amount,?,ux.subscriber_type ");
            qryBuffer.append(" FROM  user_txns ux WHERE ux.profile_type='ACT' AND ux.periodicity='DAILY' ");
            qryBuffer.append(" AND ux.txn_date>=? AND ux.txn_date<=? ");
            qryBuffer.append(" GROUP BY ux.user_id_or_msisdn,ux.user_category,ux.service_type,ux.subscriber_type ");

            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Query: " + qryBuffer.toString());
            }
            pstm = p_con.prepareStatement(qryBuffer.toString());
            int i = 1;
            pstm.setString(i++, PretupsI.PROFILE_TYPE_ACTIVATION);
            pstm.setString(i++, p_period);
            pstm.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            pstm.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
            pstm.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            count = pstm.executeUpdate();
        } catch (SQLException se) {
            _logger.error(METHOD_NAME, "SQLException: " + se.getMessage());
            _logger.errorTrace(METHOD_NAME, se);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,se);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusCalculation", METHOD_NAME, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        } finally {
            if (pstm != null) {
                try {
                    pstm.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... Total rows inserted: " + count);
            }
        }
        return count;
    }
}