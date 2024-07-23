/**
 * @(#)LMSReferenceTargetCalculationNew.java
 *                                Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                All Rights Reserved
 * 
 *                                <Process to calculate target for channel users
 *                                of reference based profile >
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Vibhu Trehan 10 Jan,2014 Initital Creation
 * 
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 */

package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyPointsRedemptionVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.ibm.icu.util.Calendar;

public class LMSReferenceTargetCalculationNew {

    private static final Log _logger = LogFactory.getLog(LMSReferenceTargetCalculationNew.class.getName());
    private static ProcessBL _processBL = null;
    private static ProcessStatusVO _processStatusVO = null;
    public static OperatorUtilI operatorUtill = null;

    public static void main(String[] args) {
        final String METHOD_NAME = "main";
        try {
            if (args.length != 2) {
                System.out.println("Usage : LMSReferenceTargetCalculationNew [Constants file] [LogConfig file] [Upload File Path]");
                return;
            }
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                _logger.debug(METHOD_NAME, " Constants file not found on provided location.");
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                _logger.debug(METHOD_NAME, " Logconfig file not found on provided location.");
                return;
            }

            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            LookupsCache.loadLookAtStartup();
            final String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            try {
                operatorUtill = (OperatorUtilI) Class.forName(taxClass).newInstance();
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BuddyMgtAction", "", "", "",
                    "Exception while loading the operator util class at the addBuddyInfo:" + e.getMessage());
            }

        }// end try
        catch (Exception ex) {
            _logger.error(METHOD_NAME, "Error in Loading Configuration files ...........................: " + ex);
            _logger.errorTrace(METHOD_NAME, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            final LMSReferenceTargetCalculationNew lmsReferenceTargetCalculationNew = new LMSReferenceTargetCalculationNew();
            lmsReferenceTargetCalculationNew.process();
        } catch (BTSLBaseException be) {
            _logger.error("main", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            _logger.error("main", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.info("main", " Exiting");
            }
            ConfigServlet.destroyProcessCache();
        }

    }

    public void process() throws BTSLBaseException, SQLException {
        final String METHOD_NAME = "process";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered: ");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        boolean statusOk = false;
        Date processedUpto = null;
        Date currentDate = new Date();
        Date limitDate = new Date();

        Date dateCount = null;
        Date sumTxnDate = null;
        final int count = 0;
        ArrayList userProfileList = null;
        LoyaltyPointsRedemptionVO redemptionVO = null;
        ArrayList profileMappingList = null;
        // String [] serviceTarget=null;
        // LoyaltyPointsRedemptionVO mappingVO = new
        // LoyaltyPointsRedemptionVO();
        String reportTo = null;
        String prevDateStr = null;
        ProcessStatusVO _processStatusMISVO = null;
        Date processedUptoMIS = null;
        PreparedStatement psmtInsert = null;
        PreparedStatement psmtDelete = null;
        int beforeInterval = 0;
        try {
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            _processBL = new ProcessBL();
            String lmsRefTarCalMisCkReq = Constants.getProperty("LMSREFTGT_MIS_CHECK_REQUIRED");
            if (BTSLUtil.isNullString(lmsRefTarCalMisCkReq)) {
                lmsRefTarCalMisCkReq = PretupsI.YES;
            } else if ("null".equalsIgnoreCase(lmsRefTarCalMisCkReq) || !PretupsI.NO.equalsIgnoreCase(lmsRefTarCalMisCkReq)) {
                lmsRefTarCalMisCkReq = PretupsI.YES;
            }
            if (PretupsI.YES.equals(lmsRefTarCalMisCkReq)) {
                // Process should not execute until the MIS has not executed
                // successfully for previous day
                _processStatusMISVO = _processBL.checkProcessUnderProcess(con, ProcessI.C2SMIS);
                processedUptoMIS = _processStatusMISVO.getExecutedUpto();
                if (processedUptoMIS != null) {
                    con.rollback();
                    final Calendar cal4CurrentDate = BTSLDateUtil.getInstance();
                    final Calendar cal14MisExecutedUpTo = BTSLDateUtil.getInstance();
                    cal4CurrentDate.add(Calendar.DAY_OF_MONTH, -1);
                    final Date currentDate1 = cal4CurrentDate.getTime(); // Current
                    // Date
                    cal14MisExecutedUpTo.setTime(processedUptoMIS);
                    final Calendar cal24CurrentDate = BTSLDateUtil.getCalendar(cal4CurrentDate);
                    final Calendar cal34MisExecutedUpTo = BTSLDateUtil.getCalendar(cal14MisExecutedUpTo);
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(METHOD_NAME, "(currentDate - 1) = " + currentDate1 + " processedUptoMIS = " + processedUptoMIS);
                    }
                    if (cal24CurrentDate.compareTo(cal34MisExecutedUpTo) != 0) {
                        if (_logger.isDebugEnabled()) {
                            _logger.debug(METHOD_NAME, "The MIS has not been executed for the previous day.");
                        }
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LMSReferenceTargetCalculationNew[process]",
                            "", "", "", "The MIS has not been executed for the previous day.");
                        throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
                    }
                } else {
                    throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
                }
            } /*else {
                throw new BTSLBaseException("LMSReferenceTargetCalculationNew", METHOD_NAME, PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
            }*/
            _processStatusVO = _processBL.checkProcessUnderProcess(con, ProcessI.LMS_REF_CAL);
            statusOk = _processStatusVO.isStatusOkBool();
            beforeInterval = BTSLUtil.parseLongToInt( _processStatusVO.getBeforeInterval() / (60 * 24));
            // check process status.
            if (statusOk) {
                processedUpto = _processStatusVO.getExecutedUpto();
                if (processedUpto != null) {

                    processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                    final Calendar cal = BTSLDateUtil.getInstance();
                    currentDate = cal.getTime(); // Current Date
                    currentDate = BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval);
                    limitDate = BTSLUtil.addDaysInUtilDate(currentDate, 0);

                    try {
                        final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT);
                        sdf.setLenient(false); // this is required else it will
                        // convert
                        reportTo = sdf.format(currentDate); // Current Date
                        prevDateStr = sdf.format(processedUpto);// Last MIS Done
                        // Date +1
                    } catch (Exception e) {
                        reportTo = "";
                        prevDateStr = "";
                        _logger.errorTrace(METHOD_NAME, e);
                        throw new BTSLBaseException("Not able to convert date to String");
                    }

                    // Process will be exceuted from the start till to date -1
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("LMSReferenceTargetCalculationNew[process]",
                            "From date=" + prevDateStr + " To Date(currentDate-interval)=" + reportTo + " processedUpto.compareTo(currentDate-interval)=" + processedUpto
                                .compareTo(currentDate));
                    }

                    // If process has already run for the last day, then you
                    // can't run it again ;)
                    if (processedUpto.compareTo(currentDate) >= 0) {
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "RunLMSForTargetCredit[main]", "", "", "",
                            "LMS Target Credit Controller has already been executed for the date=" + String.valueOf(currentDate));
                        return;
                    }

                    // if(processedUpto >= currentDate)
                    // {
                    // _logger.error("RunLMSForTargetCredit"," LMS Target Credit Process has already been executed...");
                    // throw new
                    // BTSLBaseException("RunLMSControllerForTargetCredit","process",PretupsErrorCodesI.LMS_FOR_TARGET_CREDIT_PROCESS_ALREADY_EXECUTED);
                    // }
                }
            } else {
                throw new BTSLBaseException(METHOD_NAME, "process", PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
            }
            String instanceID = null;
            instanceID = Constants.getProperty("INSTANCE_ID");
            if (BTSLUtil.isNullString(instanceID)) {
                _logger.error(METHOD_NAME, " Not able to get the instance ID for the network=");
                throw new BTSLBaseException(METHOD_NAME, "initaiateC2CTransferRequest", "lms.point.redemption.form.error.unableto.initiateo2c", "");
            }

            // Added by Diwakar as cursor are opened for more users
            final StringBuffer selectInsertBuffer = new StringBuffer();
            final StringBuffer selectDeleteBuffer = new StringBuffer();
			selectInsertBuffer.append("insert into USER_OTH_PROFILES (USER_ID,PROFILE_TYPE,SET_ID,TARGET,DETAIL_ID,VERSION,PRODUCT_CODE) values (?,?,?,?,?,?,?)");
			selectDeleteBuffer.append("Delete from USER_OTH_PROFILES where user_id =? and PROFILE_TYPE=? and SET_ID=? and DETAIL_ID=? AND VERSION=? AND PRODUCT_CODE=? ");
            final String sqlInsert = selectInsertBuffer.toString();
            final String sqlDelete = selectDeleteBuffer.toString();
            psmtInsert = con.prepareStatement(sqlInsert);
            psmtDelete = con.prepareStatement(sqlDelete);
            // Ended by Diwakar as cursor are opened for more users

            // one more loop here to handled skipped date- Note that:
            // currentDate is (currentDate-1)
            for (dateCount = BTSLUtil.getSQLDateFromUtilDate(processedUpto); dateCount.before(limitDate); dateCount = BTSLUtil.addDaysInUtilDate(dateCount, 1)) {
                _logger.error(METHOD_NAME, " Process executing for dateCount = " + dateCount);
                final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
                sdf.setLenient(false); // this is required else it will convert
                reportTo = sdf.format(dateCount); // Current Date

                sumTxnDate = BTSLUtil.addDaysInUtilDate(dateCount, -1);
				if(_logger.isDebugEnabled()){
					_logger.debug(METHOD_NAME,"loading Target based Profile for the date = "+sumTxnDate);
				}
                // Loading the details here.
                userProfileList = loadTargetProfile(con, sumTxnDate);
                redemptionVO = new LoyaltyPointsRedemptionVO();
                // mappingVO = new LoyaltyPointsRedemptionVO();
                if (userProfileList.isEmpty()) {
                    redemptionVO.setErrorCode(PretupsErrorCodesI.LMS_NO_TARGET_ACTIVE_PROFILE);
                    _logger.error(METHOD_NAME, "No Active Target Profile Exists in the System.");
                    _processStatusVO.setExecutedUpto(dateCount);
                    _processStatusVO.setExecutedOn(new Date());
                    // throw new
                    // BTSLBaseException(METHOD_NAME,"process",PretupsErrorCodesI.LMS_NO_TARGET_ACTIVE_PROFILE);
                    // throw new
                    // BTSLBaseException("RunLMSForTargetCredit","process",PretupsErrorCodesI.LMS_NO_TARGET_ACTIVE_PROFILE);
                } else {

                    profileMappingList = new ArrayList();
                    profileMappingList = loadProfileMapping(con);
                    // LoyaltyPointsRedemptionVO redemptionVO1=null;
                     int userProfileSizes = userProfileList.size();
                    for (int k = 0; k < userProfileSizes ; k++) {
                        redemptionVO = (LoyaltyPointsRedemptionVO) userProfileList.get(k);
                        redemptionVO.setCurrentProcessDate(dateCount);
                        redemptionVO.setSumTxnsDate(sumTxnDate);

                        /*
                         * for (int l=0;l<profileMappingList.size();l++)
                         * {
                         * mappingVO=(LoyaltyPointsRedemptionVO)profileMappingList
                         * .get(l);
                         * if
                         * (mappingVO.getSetId().equals(redemptionVO.getSetId(
                         * )))
                         * {
                         */
                        // Getting the sum of the Txns here- On daily or weekly
                        // or monthly basis.
                        // if(redemptionVO.getPeriodId().equalsIgnoreCase("DAILY")
                        // ||
                        // redemptionVO.getPeriodId().equalsIgnoreCase("WEEKLY")
                        // ||
                        // redemptionVO.getPeriodId().equalsIgnoreCase("MONTHLY")
                        // || )
                        // {

                        redemptionVO = loadCummulativeTxnForTargetUsers(con, redemptionVO, psmtInsert, psmtDelete);

                        /*
                         * if(redemptionVO.getRefBaseAllowed().equalsIgnoreCase(
                         * PretupsI.LMS_REFERENCE_TYPE_ACTIVE))
                         * {}
                         * else
                         * if(redemptionVO.getRefBaseAllowed().equalsIgnoreCase
                         * (PretupsI.LMS_REFERENCE_TYPE_INACTIVE))
                         * {}
                         */

                        // }
                        // }
                        // }
                        // change in db executed_upto (datecount)
                        _processStatusVO.setExecutedUpto(dateCount);
                        _processStatusVO.setExecutedOn(new Date());
                        final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
                        final int maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, _processStatusVO);
                        if (maxDoneDateUpdateCount > 0) {
                            con.commit();
                        } else {
                            con.rollback();
                            redemptionVO.setErrorCode(null);
                            throw new BTSLBaseException(METHOD_NAME, "process", PretupsErrorCodesI.LMS_COULD_NOT_UPDATE_MAX_DONE_DATE);
                        }
                    }// end daily process loop here
                }

            }
            // Added by Diwakar as cursor are opened for more users
            
            // Ended by Diwakar as cursor are opened for more users

            final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        } catch (BTSLBaseException ex) {
            con.rollback();
            _logger.errorTrace(METHOD_NAME, ex);

            _logger.error(METHOD_NAME, "exit");

        }

        catch (Exception e) {
            con.rollback();
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            try {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, " loyalty points: ");
                }

                if (statusOk) {
                    if (markProcessStatusAsComplete(con, ProcessI.LMS_REF_CAL) == 1) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    } else {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    }
                }
				if (mcomCon != null) {
					mcomCon.close("LMSReferenceTargetCalculationNew#process");
					mcomCon = null;
				}
				try {
	                if (psmtDelete != null) {
	                    psmtDelete.close();
	                }
	            } catch (Exception e) {
	                _logger.errorTrace(METHOD_NAME, e);
	            }
	            try {
	                if (psmtInsert != null) {
	                    psmtInsert.close();
	                }
	            } catch (Exception e) {
	                _logger.errorTrace(METHOD_NAME, e);
	            }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
                System.out.println("Exception while closing statement in LMSPromotionProcess method ");
            } finally {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, " Count of users that are given promotions: " + count);
                }
				if (mcomCon != null) {
					mcomCon.close("LMSReferenceTargetCalculationNew#process");
					mcomCon = null;
				}
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "Exiting");
                }
            }
        }

    }

    // Method to load Profile & User Details for active volume type LMS
    // Promotions
    ArrayList loadTargetProfile(Connection p_con, Date sumTxnDate) {
        final String METHOD_NAME = "loadTargetProfile";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered");
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        LoyaltyPointsRedemptionVO redemptionVO = null;
        ArrayList profileList = null;
        LMSReferenceTargetCalculationNewQry lmsReferenceTargetCalculationNewQry = (LMSReferenceTargetCalculationNewQry) 
        		ObjectProducer.getObject(QueryConstants.LMS_REFERENCE_TARGET_CAL_NEW_QRY, QueryConstants.QUERY_PRODUCER);
        

        final String selectQuery = lmsReferenceTargetCalculationNewQry.loadTargetProfileQry();
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "SQL Query :" + selectQuery);
        }
        try {
            profileList = new ArrayList();
            int index = 1;
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(index++, PretupsI.PROFILE_VOL);
            pstmtSelect.setString(index++, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(index++, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(index++, PretupsI.LMS_PROFILE_TYPE);
            pstmtSelect.setString(index++, PretupsI.NO);
            pstmtSelect.setString(index++, PretupsI.NORMAL);
            pstmtSelect.setString(index++, PretupsI.NO);
            pstmtSelect.setString(index++, PretupsI.LMS_PROMOTION_TYPE_STOCK);
            pstmtSelect.setString(index++, PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT);
            pstmtSelect.setString(index++, PretupsI.USER_STATUS_ACTIVE);
            // pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getReferenceTargetDate()));
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                redemptionVO = new LoyaltyPointsRedemptionVO();
                redemptionVO.setSetId(rs.getString("set_id"));
            	redemptionVO.setVersion(rs.getString("VERSION"));
                redemptionVO.setPeriodId(rs.getString("period_id"));
                redemptionVO.setServiceCode(rs.getString("service_code"));
                redemptionVO.setPointsType(rs.getString("points_type"));
                redemptionVO.setRefBaseAllowed(rs.getString("ref_based_allowed"));
                redemptionVO.setUserID(rs.getString("user_id"));
                redemptionVO.setModuleType(rs.getString("type"));
                redemptionVO.setMsisdn(rs.getString("msisdn"));
                redemptionVO.setParentID(rs.getString("parent_id"));
                redemptionVO.setNetworkID(rs.getString("network_code"));
                redemptionVO.setCategoryCode(rs.getString("category_code"));
                redemptionVO.setParentMsisdn(rs.getString("parent_msisdn"));
                redemptionVO.setParentEncryptedPin(rs.getString("parent_sms_pin"));
                redemptionVO.setEndRange(rs.getString("end_range"));
                redemptionVO.setToRange(rs.getLong("end_range"));
                redemptionVO.setTotalCrLoyaltyPoint(rs.getLong("points"));

                redemptionVO.setFromDate(rs.getDate("applicable_from"));
                redemptionVO.setPromoStartDate(String.valueOf(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from"))));
                redemptionVO.setProductCode(rs.getString("product_code"));
                redemptionVO.setProductShortCode(rs.getString("product_short_code"));
                redemptionVO.setOperatorContribution(rs.getInt("opt_contribution"));
                redemptionVO.setParentContribution(rs.getInt("prt_contribution"));
                // redemptionVO.setReferenceTargetDate(rs.getDate("REFERENCE_DATE"));
                redemptionVO.setModifiedOn(rs.getDate("lms_profile_updated_on"));
                redemptionVO.setDetailId((rs.getString("DETAIL_ID")));
                redemptionVO.setReferenceFromDate(rs.getDate("REFERENCE_FROM"));
                redemptionVO.setReferenceToDate(rs.getDate("REFERENCE_TO"));
                redemptionVO.setTargetType(rs.getString("detail_subtype"));
                profileList.add(redemptionVO);
            }
        } catch (SQLException sqe) {
            _logger.error(METHOD_NAME, "SQLException : " + sqe);
            _logger.errorTrace(METHOD_NAME, sqe);
        } catch (Exception ex) {
            _logger.error(METHOD_NAME, "Exception : " + ex);
            _logger.errorTrace(METHOD_NAME, ex);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting: profileList size=" + profileList.size());
            }
        }
        return profileList;

    }

    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) {
        final String METHOD_NAME = "markProcessStatusAsComplete";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered:  p_processId:" + p_processId);
        }
        int updateCount = 0;
        final Date currentDate = new Date();
        final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        _processStatusVO.setProcessID(p_processId);
        _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        _processStatusVO.setStartDate(currentDate);
        _processStatusVO.setExecutedOn(currentDate);
        // Commented by Diwakar on 05-July-15 for setting the correct date for
        // executed_upto
        // _processStatusVO.setExecutedUpto(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exception= " + e.getMessage());
            }
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    /**
     * 
     * @param p_con
     * @param p_redemptionVO
     * @return
     * @author gaurav.pandey
     */
    public LoyaltyPointsRedemptionVO loadCummulativeTxnForTargetUsers(Connection p_con, LoyaltyPointsRedemptionVO p_redemptionVO, PreparedStatement psmtInsert, PreparedStatement psmtDelete) {
        final String METHOD_NAME = "loadCummulativeTxnForTargetUsers";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered with userId:" + p_redemptionVO.getUserID(), "CurrentProcessingDate:" + p_redemptionVO.getCurrentProcessDate());
        }
        int insertCount = 0, deleteCount = 0;

        try {
            p_redemptionVO = operatorUtill.CalculateSumOfTrasaction(p_con, p_redemptionVO);
            if (p_redemptionVO.getTargetType().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                p_redemptionVO.setSumAmount(p_redemptionVO.getSumAmount() + p_redemptionVO.getToRange());
            } else {
                p_redemptionVO.setSumAmount(p_redemptionVO.getSumAmount() + (Long.parseLong(PretupsBL.getDisplayAmount(p_redemptionVO.getToRange())) * p_redemptionVO
                    .getSumAmount()) / 100);
            }

            psmtDelete.clearParameters();
            psmtInsert.clearParameters();

            psmtDelete.setString(1, p_redemptionVO.getUserID());
            psmtDelete.setString(2, PretupsI.LMS);
            psmtDelete.setString(3, p_redemptionVO.getSetId());
            psmtDelete.setString(4, p_redemptionVO.getDetailId());
            psmtDelete.setString(5, p_redemptionVO.getVersion());
		    psmtDelete.setString(6, p_redemptionVO.getProductCode());
            psmtInsert.setString(1, p_redemptionVO.getUserID());
            psmtInsert.setString(2, PretupsI.LMS);
            psmtInsert.setString(3, p_redemptionVO.getSetId());
            psmtInsert.setLong(4, p_redemptionVO.getSumAmount());
            psmtInsert.setString(5, p_redemptionVO.getDetailId());
            psmtInsert.setString(6, p_redemptionVO.getVersion());
		    psmtInsert.setString(7, p_redemptionVO.getProductCode());
            deleteCount = psmtDelete.executeUpdate();
            insertCount = psmtInsert.executeUpdate();
            if (deleteCount > 0 || insertCount > 0) {
                p_con.commit();
            } else {
                p_con.rollback();
            }

        } catch (SQLException sqe) {
            _logger.error(METHOD_NAME, "SQLException : " + sqe);
            _logger.errorTrace(METHOD_NAME, sqe);
        } catch (Exception ex) {
            _logger.error("", "Exception : " + ex);
            _logger.errorTrace(METHOD_NAME, ex);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting with sum Amount:" + p_redemptionVO.getSumAmount());
            }
        }

        return p_redemptionVO;
    }

    ArrayList loadProfileMapping(Connection p_con) {
        final String METHOD_NAME = "loadProfileMapping";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered");
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        LoyaltyPointsRedemptionVO redemptionVO = null;
        ArrayList profileList = null;

        final StringBuffer selectQueryBuffer = new StringBuffer();
        // selectQueryBuffer.append(" select SET_ID,MODIFIED_ON from PROFILE_MAPPING where MODIFIED_ON= trunc(sysdate-1)");
        selectQueryBuffer.append(" select SET_ID,ps.MODIFIED_ON from PROFILE_SET ps where status='Y' ");
        // selectQueryBuffer.append("and MODIFIED_ON between trunc(sysdate)-1 and trunc(sysdate) ");
        final String selectQuery = selectQueryBuffer.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "SQL Query :" + selectQuery);
        }
        try {
            profileList = new ArrayList();
            pstmtSelect = p_con.prepareStatement(selectQuery);

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                redemptionVO = new LoyaltyPointsRedemptionVO();
                redemptionVO.setSetId(rs.getString("SET_ID"));

                redemptionVO.setModifiedOn(rs.getDate("MODIFIED_ON"));
                profileList.add(redemptionVO);
            }
        } catch (SQLException sqe) {
            _logger.error(METHOD_NAME, "SQLException : " + sqe);
            _logger.errorTrace(METHOD_NAME, sqe);
        } catch (Exception ex) {
            _logger.error(METHOD_NAME, "Exception : " + ex);
            _logger.errorTrace(METHOD_NAME, ex);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting: profileList size=" + profileList.size());
            }
        }
        return profileList;

    }

}
