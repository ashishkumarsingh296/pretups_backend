package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.ProcessesLog;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyPointsRedemptionVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.PromotionDetailsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.processes.businesslogic.ProgressiveMessageVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.ibm.icu.util.Calendar;

public class LMSProgressiveMessages {
    private static Log _logger = LogFactory.getLog(LMSProgressiveMessages.class.getName());
    private static ProcessBL _processBL = null;
    private static ProcessStatusVO _processStatusMISVO = null;
    
    /**
     * ensures no instantiation
     */
    private LMSProgressiveMessages(){
    	
    }
    public static void main(String[] args) {
        final String METHOD_NAME = "main";
        try {

            final File constantsFile = new File(args[0]);// File constantsFile =
            // new
            // File(args[0]);
            if (!constantsFile.exists()) {
                System.out.println(" Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(args[1]);// File logconfigFile =
            // new
            // File(args[1]);
            if (!logconfigFile.exists()) {
                System.out.println(" Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        }// end try
        catch (Exception ex) {
            System.out.println("Error in Loading Configuration files ...........................: " + ex);
            _logger.errorTrace(METHOD_NAME, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }// end catch
        Connection con = null;
        Date processedUptoMIS = null;
        try {
            con = OracleUtil.getSingleConnection();
            _processBL = new ProcessBL();
            String lmsProgressiveMisCkReq = Constants.getProperty("LMSPROGRESSIVE_MIS_CHECK_REQUIRED");
            if (BTSLUtil.isNullString(lmsProgressiveMisCkReq)) {
                lmsProgressiveMisCkReq = PretupsI.YES;
            } else if ("null".equalsIgnoreCase(lmsProgressiveMisCkReq) || !PretupsI.NO.equalsIgnoreCase(lmsProgressiveMisCkReq)) {
                lmsProgressiveMisCkReq = PretupsI.YES;
            }
            if (PretupsI.YES.equals(lmsProgressiveMisCkReq)) {
                // Process should not execute until the MIS has not executed
                // successfully for previous day
                _processStatusMISVO = _processBL.checkProcessUnderProcess(con, ProcessI.C2SMIS);
                processedUptoMIS = _processStatusMISVO.getExecutedUpto();
                if (processedUptoMIS != null) {
                    con.rollback();
                    final Calendar cal4CurrentDate = BTSLDateUtil.getInstance();
                    final Calendar cal14MisExecutedUpTo = BTSLDateUtil.getInstance();
                    cal4CurrentDate.add(Calendar.DAY_OF_MONTH, -1);
                    final Date currentDate = cal4CurrentDate.getTime(); // Current
                    // Date
                    cal14MisExecutedUpTo.setTime(processedUptoMIS);
                    final Calendar cal24CurrentDate = BTSLDateUtil.getCalendar(cal4CurrentDate);
                    final Calendar cal34MisExecutedUpTo = BTSLDateUtil.getCalendar(cal14MisExecutedUpTo);
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(METHOD_NAME, "(currentDate - 1) = " + currentDate + " processedUptoMIS = " + processedUptoMIS);
                    }
                    if (cal24CurrentDate.compareTo(cal34MisExecutedUpTo) == 0) {
                        // method call balanceAlert to acquire information that
                        // to what number alert should be send
                        getUsersforMessage(con);
                    } else {
                        if (_logger.isDebugEnabled()) {
                            _logger.debug(METHOD_NAME, "The MIS has not been executed for the previous day.");
                        }
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LMSProgressiveMessages[main]", "", "", "",
                            "The MIS has not been executed for the previous day.");
                        throw new BTSLBaseException("LMSProgressiveMessages", METHOD_NAME, PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
                    }
                } else {
                    throw new BTSLBaseException("LMSProgressiveMessages", METHOD_NAME, PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
                }
            } else {
                // method call balanceAlert to acquire information that to what
                // number alert should be send
                getUsersforMessage(con);
            }
        }// end try
        catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " " + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
        }// end catch
        finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.info(METHOD_NAME, "Exiting");
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            ConfigServlet.destroyProcessCache();
        }// end finally
    }// end main

    public static void getUsersforMessage(Connection p_con) {
        final String METHOD_NAME = "getUsersforMessage";
        int alertsSendCount = 0;
        final PreparedStatement pstmt = null;
        final ResultSet rst = null;
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelect1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;

        // ArrayList arrayList=null;
        ProgressiveMessageVO alertVO = null;
        Locale locale = null;
        if (_logger.isDebugEnabled()) {
            _logger.info(METHOD_NAME, "Entered");
        }
        try {
            final String defaultLanguage = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE));
            final String defaultCountry = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
            
            final Date date = new Date();
            PromotionDetailsVO promotionDetailsVO = null;
            ArrayList promotionDetailsList = null;
			
            LMSProgressiveMessagesQry lmsProgressiveMessagesQry = (LMSProgressiveMessagesQry) ObjectProducer.getObject(QueryConstants.LMS_PROGRESSIVE_MESSAGES_QRY, QueryConstants.QUERY_PRODUCER);
           
                final String selectQuery = lmsProgressiveMessagesQry.getUsersforMessageQry();
                pstmtSelect = p_con.prepareStatement(selectQuery);
                int i = 0;
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "select query:" + selectQuery);
                }

                final StringBuffer strBuff2 = new StringBuffer();
                strBuff2.append("SELECT message_code,message1,message2 from MESSAGES_MASTER where message_code=? ");
                final String selectQuery1 = strBuff2.toString();
                pstmtSelect1 = p_con.prepareStatement(selectQuery1);

                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue()) {
                    pstmtSelect.setString(++i, PretupsI.YES);
                    pstmtSelect.setString(++i, PretupsI.NO);
                    pstmtSelect.setString(++i, PretupsI.OPT_IN);
                    pstmtSelect.setString(++i, PretupsI.NORMAL);
                } else {
                    pstmtSelect.setString(++i, PretupsI.NO);
                    pstmtSelect.setString(++i, PretupsI.NORMAL);
                }
                pstmtSelect.setString(++i, PretupsI.NO);
                pstmtSelect.setString(++i, PretupsI.LMS_PROMOTION_TYPE_STOCK);
                pstmtSelect.setString(++i, PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT);
                pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(date));
                pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(date));
                pstmtSelect.setString(++i, PretupsI.STATUS_ACTIVE);
                pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(date));
                pstmtSelect.setString(++i, PretupsI.DAILY_FILTER);
                rs = pstmtSelect.executeQuery();
                promotionDetailsList = new ArrayList();
                while (rs.next()) {

                    promotionDetailsVO = new PromotionDetailsVO();
                    alertVO = new ProgressiveMessageVO();
                    promotionDetailsVO.setStartRangeAsString(Long.toString(rs.getLong("start_range")));
                    promotionDetailsVO.setEndRangeAsString(Long.toString(rs.getLong("end_range")));
                    promotionDetailsVO.setStartRange(Long.parseLong(rs.getString("start_range")));
                    promotionDetailsVO.setEndRange(Long.parseLong(rs.getString("end_range")));
                    promotionDetailsVO.setFromDate(rs.getDate("applicable_from"));
                    promotionDetailsVO.setToDate(rs.getDate("applicable_to"));
                    promotionDetailsVO.set_setId(rs.getString("set_id"));
                    promotionDetailsVO.setSetName(rs.getString("set_name"));
                    promotionDetailsVO.setPointsTypeCode(rs.getString("points_type"));
                    promotionDetailsVO.setPointsAsString(rs.getString("points"));
                    promotionDetailsVO.setPoints(rs.getLong("points"));
                    promotionDetailsVO.setDetailType(rs.getString("detail_type"));
                    promotionDetailsVO.setDetailSubType(rs.getString("detail_subtype"));
                    promotionDetailsVO.setSubscriberType(rs.getString("subscriber_type"));
                    promotionDetailsVO.setPeriodId(rs.getString("period_id"));
                    promotionDetailsVO.setType(rs.getString("type"));
                    promotionDetailsVO.setUserType(rs.getString("user_type"));
                    promotionDetailsVO.setServiceCode(rs.getString("service_code"));
                    promotionDetailsVO.setMinLimit(rs.getLong("min_limit"));
                    promotionDetailsVO.setMaxLimit(rs.getLong("max_limit"));
                    promotionDetailsVO.setSubscriberType(rs.getString("subscriber_type"));
                    promotionDetailsVO.setVersion(rs.getString("version"));
                    promotionDetailsVO.setReferenceBasedAllowed(rs.getString("REF_BASED_ALLOWED"));
                    promotionDetailsVO.setDetailid(rs.getString("DETAIL_ID"));
                    promotionDetailsVO.setMessageConfEnabled(rs.getString("MESSAGE_MANAGEMENT_ENABLED"));
					//Filling alert VO
                    alertVO.setMsisdn(rs.getString("msisdn"));
                    alertVO.setProfileID(rs.getString("set_id"));
                    alertVO.setUserId(rs.getString("user_id"));
                    alertVO.setVersion(rs.getString("version"));
					alertVO.setServiceCode(rs.getString("service_code"));
					alertVO.setProductCode(rs.getString("PRODUCT_CODE"));
                    try {
                        locale = new Locale(rs.getString("ph_language"), rs.getString("country"));
                    } catch (Exception e) {
                        locale = new Locale(defaultLanguage, defaultCountry);
                        _logger.errorTrace(METHOD_NAME, e);
                    }
                    alertVO.setLocale(locale);
                    promotionDetailsVO.setProgressiveMessageVO(alertVO);
					//Handling of OPTIN/OPTOUT Progressive Message
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue()) {
                        promotionDetailsVO.setOptInOutEnabled(rs.getString("OPT_IN_OUT_ENABLED"));
                        promotionDetailsVO.setOptInOutStatus(rs.getString("OPT_IN_OUT_STATUS"));
                        if (PretupsI.YES.equalsIgnoreCase(promotionDetailsVO.getOptInOutEnabled()) && PretupsI.NORMAL.equalsIgnoreCase(promotionDetailsVO.getOptInOutStatus())) {
                            if (_logger.isDebugEnabled()) {
                                _logger.info(METHOD_NAME, "No need to send progressive message for the retailer [ " + alertVO.getMsisdn() + " ]");
                            }
                            continue;
                        }
                    }
                    // addActivationDetailList.add(activationProfileDeatilsVO);

                    // brajesh
                    if (("Y").equals(promotionDetailsVO.getMessageConfEnabled())) {
                        String Message_code = PretupsI.SUCCESS_MESSAGE + "_" + promotionDetailsVO.get_setId();
                        pstmtSelect1.clearParameters();
                        pstmtSelect1.setString(1, Message_code);
                        try
                        {
                        rs1 = pstmtSelect1.executeQuery();
                        while (rs1.next()) {

                            try {
                                if ((defaultLanguage.equalsIgnoreCase(rs.getString("ph_language")) && defaultCountry.equalsIgnoreCase(rs.getString("country")))) {
                                    promotionDetailsVO.setMessageSuccess(rs1.getString("message1"));
                                } else {
                                    promotionDetailsVO.setMessageSuccess(rs1.getString("message2"));
                                }
                            } catch (Exception e) {
                                _logger.errorTrace(METHOD_NAME, e);
                            }

                        }
                        Message_code = PretupsI.FAILURE_MESSAGE + "_" + promotionDetailsVO.get_setId();
                        pstmtSelect1.clearParameters();
                        pstmtSelect1.setString(1, Message_code);
                        }
                        finally{
            				if(rs1!=null)
            					rs1.close();
                        }
                        
                        rs1=null;
                        rs1 = pstmtSelect1.executeQuery();
                        while (rs1.next()) {
                            try {
                                if ((defaultLanguage.equalsIgnoreCase(rs.getString("ph_language")) && defaultCountry.equalsIgnoreCase(rs.getString("country")))) {
                                    promotionDetailsVO.setMessageFailure(rs1.getString("message1"));
                                } else {
                                    promotionDetailsVO.setMessageFailure(rs1.getString("message2"));
                                }
                            } catch (Exception e) {
                                _logger.errorTrace(METHOD_NAME, e);
                            }

                        }
                        promotionDetailsList.add(promotionDetailsVO);
                    } else {
                        promotionDetailsList.add(promotionDetailsVO);
                    }
                }
            

            if (promotionDetailsList.size() > 0) {
                alertsSendCount = sendAlerts(p_con, promotionDetailsList);
            }
            if (alertsSendCount > 0) {
                try {
                    p_con.commit();
                } catch (SQLException e) {
                    p_con.rollback();
                    _logger.errorTrace(METHOD_NAME, e);
                }
            }
            /*
             * if(arrayList.size()>0)
             * {
             * for(int i=0; i<arrayList.size(); i++)
             * {
             * alertVO=(ProgressiveMessageVO)arrayList.get(i);
             * if(alertVO!=null)
             * {
             * // sendEmailNotification(p_con,alertVO);
             * }
             * }
             * }
             */

            if (_logger.isDebugEnabled()) {
                _logger.info(METHOD_NAME, "Total alert send:" + alertsSendCount);
            }
        }// end try
        catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Error:" + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
        }// end catch
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e2) {
                    _logger.errorTrace(METHOD_NAME, e2);
                }
            }
            try {
                if (rs1 != null) {
                	rs1.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (pstmtSelect != null) {
                try {
                    pstmtSelect.close();
                } catch (SQLException e3) {
                    _logger.errorTrace(METHOD_NAME, e3);
                }
            }
            if (pstmtSelect1 != null) {
                try {
                    pstmtSelect1.close();
                } catch (SQLException e3) {
                    _logger.errorTrace(METHOD_NAME, e3);
                }
            }

            if (_logger.isDebugEnabled()) {
                _logger.info(METHOD_NAME, " Exiting");
            }
        }// end finally
    }

    public static int sendAlerts(Connection p_con, ArrayList p_profileList) {
        final String METHOD_NAME = "sendAlerts";
        if (_logger.isDebugEnabled()) {
            _logger.info(METHOD_NAME, "Entered with user Profile list Size:" + p_profileList.size());
        }
        int recordCount = 0;
        String alertSleepTime = null;
        long lAlertSleepTime = 50;
        Locale locale = null;
        final Date currentDate = new Date();

        int o2cWflag = 0, c2cWflag = 0, c2sWflag = 0, o2cMflag = 0, c2cMflag = 0, c2sMflag = 0, o2cEopflag = 0, c2cEopflag = 0, c2sEopflag = 0;
        PromotionDetailsVO promotionDetailsVO = null;
        ProgressiveMessageVO alertVO = null;
        String msisdn = null;
        final String _lowBalRequestCode = null;
        // PreparedStatement pstmt,pstmt1=null;
        PreparedStatement pstmtUOP = null;
        PreparedStatement pstmtDailyC2STD0 = null;
        PreparedStatement pstmtDailyC2STD = null;
        PreparedStatement pstmtDailyC2STD1 = null;

        PreparedStatement pstmtDailyCTM = null;
        PreparedStatement pstmtDailyCTM1 = null;
        // ResultSet rst,rst1 = null;
        ResultSet rstUOP = null;
        ResultSet rstDailyC2STD = null;
        ResultSet rstDailyCTM = null;
        ResultSet rstDailyCTM1 = null;
        String msg = null;
        long currentTransactionAmountc2sW = 0;
        long currentTransactionAmountc2sM = 0;
        long currentTransactionAmounto2cW = 0;
        long currentTransactionAmounto2cM = 0;
        long currentTransactionAmountc2cW = 0;
        long currentTransactionAmountc2cM = 0;
        long currentTransactionAmountc2sEop = 0;
        long currentTransactionAmountc2cEop = 0;
        long currentTransactionAmounto2cEop = 0;

        LoyaltyPointsRedemptionVO redemptionVO = null;
        final ArrayList targetList = new ArrayList(250);
        String lsmProfileSetName = null;
        try {
            alertSleepTime = Constants.getProperty("ALERT_SLEEP_TIME");
            lAlertSleepTime = Integer.parseInt(alertSleepTime);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            lAlertSleepTime = 50;// in miliseconds
        }
        try {
            promotionDetailsVO = new PromotionDetailsVO();
            final StringBuffer selectQueryBuff1 = new StringBuffer(
                "select uop.TARGET ,ps.TYPE,ps.SERVICE_CODE,ps.DETAIL_ID from USER_OTH_PROFILES uop , PROFILE_DETAILS ps where ps.SET_ID=uop.SET_ID and uop.DETAIL_ID=ps.DETAIL_ID ");
			selectQueryBuff1.append(" and ps.SET_ID=? and uop.profile_type=? and USER_ID=? AND uop.VERSION=? AND uop.product_code=ps.product_code AND uop.product_code=? ");
            final String selectQuery1 = selectQueryBuff1.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "select query:" + selectQuery1);
            }
            int m = 0;
            pstmtUOP = p_con.prepareStatement(selectQuery1);
            LMSProgressiveMessagesQry lmsProgressiveMessagesQry = (LMSProgressiveMessagesQry) ObjectProducer.getObject(QueryConstants.LMS_PROGRESSIVE_MESSAGES_QRY, QueryConstants.QUERY_PRODUCER);

            final String selectQuery = lmsProgressiveMessagesQry.sendAlertsGetSumSenderTransferAmountQry();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "select query:" + selectQuery);
            }
            pstmtDailyC2STD0 = p_con.prepareStatement(selectQuery);

        
            final String selectQuery2 = lmsProgressiveMessagesQry.sendAlertsGetSumC2CTransferOutAmountQry();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "select query:" + selectQuery2);
            }
            pstmtDailyCTM = p_con.prepareStatement(selectQuery2);

            
            final String selectQuery3 = lmsProgressiveMessagesQry.sendAlertsGetSumO2CTransferInAmountQry();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "select query:" + selectQuery3);
            }
            pstmtDailyCTM1 = p_con.prepareStatement(selectQuery3);

			
            final String selectQuery4 = lmsProgressiveMessagesQry.sendAlertsGetSumSenderTransferAmountQry();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "select query:" + selectQuery4);
            }
            pstmtDailyC2STD1 = p_con.prepareStatement(selectQuery4);

            for (int j = 0; j < p_profileList.size(); j++) {
                o2cWflag = 0;
                c2cWflag = 0;
                c2sWflag = 0;
                o2cMflag = 0;
                c2cMflag = 0;
                c2sMflag = 0;
                o2cEopflag = 0;
                c2cEopflag = 0;
                c2sEopflag = 0;
                final ArrayList<Long> c2sW = new ArrayList<Long>();
                final ArrayList<Long> c2sM = new ArrayList<Long>();
                final ArrayList<Long> o2cW = new ArrayList<Long>();
                final ArrayList<Long> o2cM = new ArrayList<Long>();
                final ArrayList<Long> c2cW = new ArrayList<Long>();
                final ArrayList<Long> c2cM = new ArrayList<Long>();
                final ArrayList<Long> c2sEop = new ArrayList<Long>();
                final ArrayList<Long> c2cEop = new ArrayList<Long>();
                final ArrayList<Long> o2cEop = new ArrayList<Long>();
                promotionDetailsVO = (PromotionDetailsVO) p_profileList.get(j);
                alertVO = promotionDetailsVO.getProgressiveMessageVO();
                msisdn = alertVO.getMsisdn();
                m = 0;

                pstmtUOP.clearParameters();
                // pstmt1.setString(++m,alertVO.getUserId());
                pstmtUOP.setString(++m, alertVO.getProfileID());
                pstmtUOP.setString(++m, PretupsI.LMS);
                pstmtUOP.setString(++m, alertVO.getUserId());
    			pstmtUOP.setString(++m,alertVO.getVersion());
				pstmtUOP.setString(++m,alertVO.getProductCode());
                if (targetList.size() > 0) {
                    for (int i = 0; i < targetList.size(); i++) {
                        final LoyaltyPointsRedemptionVO loyaltyPointsRedemptionVO = (LoyaltyPointsRedemptionVO) targetList.get(i);
                        if (!loyaltyPointsRedemptionVO.getUserID().equals(alertVO.getUserId())) {
                            targetList.clear();
                            rstUOP = pstmtUOP.executeQuery();
                        }

                    }
                } else {
                    rstUOP = pstmtUOP.executeQuery();
                    if(_logger.isDebugEnabled()) {
    					_logger.debug(METHOD_NAME, "Target is being fetched for USER_ID = "+alertVO.getUserId()+", VERSION = "+alertVO.getVersion());
    				}
                }

                while (rstUOP.next()) {
                    redemptionVO = new LoyaltyPointsRedemptionVO();

                    redemptionVO.setModuleType(rstUOP.getString("TYPE"));
                    redemptionVO.setServiceCode(rstUOP.getString("SERVICE_CODE"));
                    redemptionVO.setReferenceTarget(rstUOP.getLong("TARGET"));
                    redemptionVO.setDetailId(rstUOP.getString("DETAIL_ID"));
                    redemptionVO.setUserID(alertVO.getUserId());
                    targetList.add(redemptionVO);
                    redemptionVO = null;
                }
                /*
                 * for (int k=0;k<p_profileList.size();k++){
                 * promotionDetailsVO=(PromotionDetailsVO)p_profileList.get(k);
                 */boolean isServiceCodeAll = false;
                if (!BTSLUtil.isNullString(promotionDetailsVO.getServiceCode()) && PretupsI.ALL.equalsIgnoreCase(promotionDetailsVO.getServiceCode())) {
                    // selectQueryBuff =new
                    // StringBuffer("select sum(SENDER_TRANSFER_AMOUNT) from DAILY_C2S_TRANS_DETAILS");
                    // selectQueryBuff.append(" where user_id =?  and TRANS_DATE >= ? and TRANS_DATE <=sysdate-1");
                    // selectQuery=selectQueryBuff.toString();
                    // if(_logger.isDebugEnabled())_logger.debug("loadLMSProfileAndVersion","select query:"+selectQuery
                    // );
                    // pstmtDailyC2STD1 = p_con.prepareStatement(selectQuery4);
                    isServiceCodeAll = true;
                }
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "isServiceCodeAll:" + isServiceCodeAll);
                }
                /*
                 * if(alertVO.getProfileID().equals(promotionDetailsVO.get_setId(
                 * )))
                 * {
                 */
                // //check promotion is c2s or o2c or c2s so as to get specefic
                // transaction from daily tables
                if (!BTSLUtil.isNullString(promotionDetailsVO.getSetName())) {
                    lsmProfileSetName = promotionDetailsVO.getSetName();
                }

                if (PretupsI.C2S_MODULE.equals(promotionDetailsVO.getType())) {
                    m = 0;
                    if (!isServiceCodeAll) {
                        pstmtDailyC2STD = pstmtDailyC2STD0;
                        pstmtDailyC2STD.clearParameters();
                        pstmtDailyC2STD.setString(++m, alertVO.getUserId());
                        pstmtDailyC2STD.setString(++m, promotionDetailsVO.getServiceCode());
                    } else {
                        pstmtDailyC2STD1.clearParameters();
                        pstmtDailyC2STD1.setString(++m, alertVO.getUserId());
						pstmtDailyC2STD1.setString(++m,alertVO.getServiceCode());
                        pstmtDailyC2STD = null;
                        pstmtDailyC2STD = pstmtDailyC2STD1;
                    }
                    if ("WEEKLY".equals(promotionDetailsVO.getPeriodId())) {
                        c2sWflag = 1;
                        if ("Y".equals(promotionDetailsVO.getReferenceBasedAllowed())) {
                            for (int h = 0; h < targetList.size(); h++) {
                                redemptionVO = new LoyaltyPointsRedemptionVO();
                                redemptionVO = (LoyaltyPointsRedemptionVO) targetList.get(h);
                                if (promotionDetailsVO.getDetailid().equals(redemptionVO.getDetailId())) {
                                    c2sW.add(redemptionVO.getReferenceTarget());
                                }
                            }
                        } else {// if(promotionDetailsVO.getDetailid().equals(redemptionVO.getDetailId()))
                            c2sW.add(promotionDetailsVO.getEndRange());
                            if (_logger.isDebugEnabled()) {
                                _logger.debug(METHOD_NAME, "promotionDetailsVO.getEndRange :" + promotionDetailsVO.getEndRange());
                            }
                        }
                        // pstmtDailyC2STD.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDifferenceDate(currentDate,-4))
                        // );
                        final Date date = getWeeklyTranscationFromDate(promotionDetailsVO.getFromDate(), currentDate);
                        pstmtDailyC2STD.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(date));
                        // );
                        rstDailyC2STD = pstmtDailyC2STD.executeQuery();
                        while (rstDailyC2STD.next()) {
                            currentTransactionAmountc2sW = rstDailyC2STD.getLong(1);
                            if (_logger.isDebugEnabled()) {
                                _logger.debug(METHOD_NAME, "currentTransactionAmountc2sW:" + currentTransactionAmountc2sW);
                            }
                        }
                    } else if ("MONTHLY".equals(promotionDetailsVO.getPeriodId())) {
                        c2sMflag = 1;
                        if ("Y".equals(promotionDetailsVO.getReferenceBasedAllowed())) {
                            for (int h = 0; h < targetList.size(); h++) {
                                redemptionVO = new LoyaltyPointsRedemptionVO();
                                redemptionVO = (LoyaltyPointsRedemptionVO) targetList.get(h);
                                if (promotionDetailsVO.getDetailid().equals(redemptionVO.getDetailId())) {
                                    c2sM.add(redemptionVO.getReferenceTarget());
                                }
                            }
                        } else {
                            // if(promotionDetailsVO.getDetailid().equals(redemptionVO.getDetailId()))
                            c2sM.add(promotionDetailsVO.getEndRange());
                        }
                        /*
                         * cal.setTime(promotionDetailsVO.getFromDate());
                         * cal1.setTime(currentDate);
                         * int diff=0;
                         * diff=cal.compareTo(cal1);
                         * while(diff<0)
                         * {
                         * cal2=cal;
                         * cal.add(Calendar.MONTH, 1);
                         * diff=cal.compareTo(cal1);
                         * }
                         * Date date = cal2.getTime();
                         * cal1.setTime(currentDate);
                         */
                        final Date date = getMonthlyTranscationFronDate(promotionDetailsVO.getFromDate(), currentDate);
                        if (_logger.isDebugEnabled()) {
                            _logger.debug(METHOD_NAME, "Monthly From Date:" + BTSLUtil.getSQLDateFromUtilDate(date));
                        }
                        // pstmtDailyC2STD.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDifferenceDate(currentDate,-4))
                        // );
                        pstmtDailyC2STD.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(promotionDetailsVO.getFromDate()));
                        rstDailyC2STD = pstmtDailyC2STD.executeQuery();
                        while (rstDailyC2STD.next()) {
                            currentTransactionAmountc2sM = rstDailyC2STD.getLong(1);
                            if (_logger.isDebugEnabled()) {
                                _logger.debug(METHOD_NAME, "currentTransactionAmountc2sM:" + currentTransactionAmountc2sM);
                            }
                        }
                    } else if ("EOP".equals(promotionDetailsVO.getPeriodId())) {
                        c2sEopflag = 1;
                        if ("Y".equals(promotionDetailsVO.getReferenceBasedAllowed())) {
                            for (int h = 0; h < targetList.size(); h++) {
                                redemptionVO = new LoyaltyPointsRedemptionVO();
                                redemptionVO = (LoyaltyPointsRedemptionVO) targetList.get(h);
                                if (promotionDetailsVO.getDetailid().equals(redemptionVO.getDetailId())) {
                                    c2sEop.add(redemptionVO.getReferenceTarget());
                                }
                            }
                        } else {
                            c2sEop.add(promotionDetailsVO.getEndRange());
                            if (_logger.isDebugEnabled()) {
                                _logger.debug(METHOD_NAME, "promotionDetailsVO.getEndRange :" + promotionDetailsVO.getEndRange());
                            }
                        }
                        if (_logger.isDebugEnabled()) {
                            _logger.debug(METHOD_NAME, "From Date:" + promotionDetailsVO.getFromDate());
                        }
                        if (_logger.isDebugEnabled()) {
                            _logger.debug(METHOD_NAME, "currentDate:" + currentDate);
                        }
                        final Date date = getEopTranscationFromDate(promotionDetailsVO.getFromDate(), promotionDetailsVO.getToDate(), currentDate);
                        if (date != null) {
                            pstmtDailyC2STD.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(date));
                            rstDailyC2STD = pstmtDailyC2STD.executeQuery();
                            while (rstDailyC2STD.next()) {
                                currentTransactionAmountc2sEop = rstDailyC2STD.getLong(1);
                                if (_logger.isDebugEnabled()) {
                                    _logger.debug(METHOD_NAME, "currentTransactionAmountc2sEop:" + currentTransactionAmountc2sEop);
                                }
                            }
                        }
                    }
                }

                if (PretupsI.C2C_MODULE.equals(promotionDetailsVO.getType())) {
                    m = 0;
                    pstmtDailyCTM.clearParameters();
                    pstmtDailyCTM.setString(++m, alertVO.getUserId());
                    // pstmtDailyCTM.setString(++i,promotionDetailsVO.getServiceCode());
                    if ("WEEKLY".equals(promotionDetailsVO.getPeriodId())) {
                    	try{
                        c2cWflag = 1;
                        final Date date = getWeeklyTranscationFromDate(promotionDetailsVO.getFromDate(), currentDate);
                        pstmtDailyCTM.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(date));
                        if ("Y".equals(promotionDetailsVO.getReferenceBasedAllowed())) {
                            for (int h = 0; h < targetList.size(); h++) {
                                redemptionVO = new LoyaltyPointsRedemptionVO();
                                redemptionVO = (LoyaltyPointsRedemptionVO) targetList.get(h);
                                if (promotionDetailsVO.getDetailid().equals(redemptionVO.getDetailId())) {
                                    c2cW.add(redemptionVO.getReferenceTarget());
                                }
                            }
                        } else {// if(promotionDetailsVO.getDetailid().equals(redemptionVO.getDetailId()))
                            c2cW.add(promotionDetailsVO.getEndRange());
                        }
                        rstDailyCTM = pstmtDailyCTM.executeQuery();
                    	while (rstDailyCTM.next()) {
                            currentTransactionAmountc2cW = (rstDailyCTM.getLong(1) + rstDailyCTM.getLong(2));
                        }
                    }
                    	finally{
                    		if(rstDailyCTM!=null)
                    			rstDailyCTM.close();
                    	}
                    }
                    if ("MONTHLY".equals(promotionDetailsVO.getPeriodId())) {
                        c2cMflag = 1;
                        if ("Y".equals(promotionDetailsVO.getReferenceBasedAllowed())) {
                            for (int h = 0; h < targetList.size(); h++) {
                                redemptionVO = new LoyaltyPointsRedemptionVO();
                                redemptionVO = (LoyaltyPointsRedemptionVO) targetList.get(h);
                                if (promotionDetailsVO.getDetailid().equals(redemptionVO.getDetailId())) {
                                    c2cM.add(redemptionVO.getReferenceTarget());
                                }
                            }
                        } else { // if(promotionDetailsVO.getDetailid().equals(redemptionVO.getDetailId()))
                            c2cM.add(promotionDetailsVO.getEndRange());
                        }
                        /*
                         * cal.setTime(promotionDetailsVO.getFromDate());
                         * cal1.setTime(currentDate);
                         * int diff=0;
                         * diff=cal.compareTo(cal1);
                         * while(diff<0)
                         * { cal2=cal;
                         * cal.add(Calendar.MONTH, 1);
                         * diff=cal.compareTo(cal1);
                         * }
                         * Date date = cal2.getTime();
                         */
                        final Date date = getMonthlyTranscationFronDate(promotionDetailsVO.getFromDate(), currentDate);
                        pstmtDailyCTM.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(date));
                        try{
                        rstDailyCTM=null;
                        rstDailyCTM = pstmtDailyCTM.executeQuery();
                        while (rstDailyCTM.next()) {
                            currentTransactionAmountc2cM = (rstDailyCTM.getLong(1) + rstDailyCTM.getLong(2));
                        }
                        }
                        finally{
                        	if(rstDailyCTM!=null)
                    			rstDailyCTM.close();
                        	}
                    }
                    if ("EOP".equals(promotionDetailsVO.getPeriodId())) {
                        c2cEopflag = 1;
                        final Date date = getEopTranscationFromDate(promotionDetailsVO.getFromDate(), promotionDetailsVO.getToDate(), currentDate);
                        if (date != null) {
                            pstmtDailyCTM.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(date));
                        }
                        if ("Y".equals(promotionDetailsVO.getReferenceBasedAllowed())) {
                            for (int h = 0; h < targetList.size(); h++) {
                                redemptionVO = new LoyaltyPointsRedemptionVO();
                                redemptionVO = (LoyaltyPointsRedemptionVO) targetList.get(h);
                                if (promotionDetailsVO.getDetailid().equals(redemptionVO.getDetailId())) {
                                    c2cEop.add(redemptionVO.getReferenceTarget());
                                }
                            }
                        } else {
                            c2cEop.add(promotionDetailsVO.getEndRange());
                        }
                        if (date != null) {
                            rstDailyCTM = pstmtDailyCTM.executeQuery();
                            while (rstDailyCTM.next()) {
                                currentTransactionAmountc2cEop = (rstDailyCTM.getLong(1) + rstDailyCTM.getLong(2));
                            }
                        }
                    }
                }
                if (PretupsI.O2C_MODULE.equals(promotionDetailsVO.getType())) {
                    m = 0;
                    pstmtDailyCTM1.clearParameters();
                    pstmtDailyCTM1.setString(++m, alertVO.getUserId());
                    // pstmtDailyCTM1.setString(++m,promotionDetailsVO.getServiceCode());
                    if ("WEEKLY".equals(promotionDetailsVO.getPeriodId())) {
                    	try{
                        o2cWflag = 1;
                        final Date date = getWeeklyTranscationFromDate(promotionDetailsVO.getFromDate(), currentDate);
                        pstmtDailyCTM1.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(date));
                        if ("Y".equals(promotionDetailsVO.getReferenceBasedAllowed())) {
                            for (int h = 0; h < targetList.size(); h++) {
                                redemptionVO = new LoyaltyPointsRedemptionVO();
                                redemptionVO = (LoyaltyPointsRedemptionVO) targetList.get(h);
                                if (promotionDetailsVO.getDetailid().equals(redemptionVO.getDetailId())) {
                                    o2cW.add(redemptionVO.getReferenceTarget());
                                }
                            }
                        } else {// if(promotionDetailsVO.getDetailid().equals(redemptionVO.getDetailId()))
                            o2cW.add(promotionDetailsVO.getEndRange());
                        }
                        rstDailyCTM1 = pstmtDailyCTM1.executeQuery();
                    
                    	
                        while (rstDailyCTM1.next()) {
                            currentTransactionAmounto2cW = rstDailyCTM1.getLong(1);
                        }
                    	}
                    	finally{
                    		if(rstDailyCTM1!=null)
                                rstDailyCTM1.close();
                    	}
                    }
                    if ("MONTHLY".equals(promotionDetailsVO.getPeriodId())) {
                        o2cMflag = 1;
                        if ("Y".equals(promotionDetailsVO.getReferenceBasedAllowed())) {
                            for (int h = 0; h < targetList.size(); h++) {
                                redemptionVO = new LoyaltyPointsRedemptionVO();
                                redemptionVO = (LoyaltyPointsRedemptionVO) targetList.get(h);
                                // if(promotionDetailsVO.getDetailid().equals(redemptionVO.getDetailId()))
                                o2cM.add(redemptionVO.getReferenceTarget());
                            }
                        } else {// if(promotionDetailsVO.getDetailid().equals(redemptionVO.getDetailId()))
                            o2cM.add(promotionDetailsVO.getEndRange());
                        }
                        /*
                         * cal.setTime(promotionDetailsVO.getFromDate());
                         * cal1.setTime(currentDate);
                         * int diff=0;
                         * diff=cal.compareTo(cal1);
                         * while(diff<0)
                         * { cal2=cal;
                         * cal.add(Calendar.MONTH, 1);
                         * diff=cal.compareTo(cal1);
                         * }
                         * Date date = cal2.getTime();
                         */
                        final Date date = getMonthlyTranscationFronDate(promotionDetailsVO.getFromDate(), currentDate);
                        pstmtDailyCTM1.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(date));
                        try{
                        rstDailyCTM1=null;
                        rstDailyCTM1 = pstmtDailyCTM1.executeQuery();
                        while (rstDailyCTM1.next()) {
                            currentTransactionAmounto2cM = rstDailyCTM1.getLong(1);
                        }
                        }
                        finally{
                        	if(rstDailyCTM1!=null)
                    			rstDailyCTM1.close();
                        }
                    }
                    if ("EOP".equalsIgnoreCase(promotionDetailsVO.getPeriodId())) {
                        o2cEopflag = 1;
                        final Date date = getEopTranscationFromDate(promotionDetailsVO.getFromDate(), promotionDetailsVO.getToDate(), currentDate);
                        if (date != null) {
                            pstmtDailyCTM1.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(date));
                        }
                        if ("Y".equals(promotionDetailsVO.getReferenceBasedAllowed())) {
                            for (int h = 0; h < targetList.size(); h++) {
                                redemptionVO = new LoyaltyPointsRedemptionVO();
                                redemptionVO = (LoyaltyPointsRedemptionVO) targetList.get(h);
                                if (promotionDetailsVO.getDetailid().equals(redemptionVO.getDetailId())) {
                                    o2cEop.add(redemptionVO.getReferenceTarget());
                                }
                            }
                        } else {
                            o2cEop.add(promotionDetailsVO.getEndRange());
                        }
                        if (date != null) {
                            rstDailyCTM1 = pstmtDailyCTM1.executeQuery();
                            while (rstDailyCTM1.next()) {
                                currentTransactionAmounto2cEop = rstDailyCTM1.getLong(1);
                            }
                        }
                    }

                }
                recordCount++;
                // }

                // message sending
                locale = alertVO.getLocale();
                msisdn = alertVO.getMsisdn();
                // keyArgumentVO=new KeyArgumentVO();

                // keyArgumentVO.setKey(PretupsErrorCodesI.LOYALTY_PROGRESSIVE_MESSAGE);
                // String target1;
                /*
                 * if("Y".equals(promotionDetailsVO.getReferenceBasedAllowed()))
                 * {
                 * target1=String.valueOf(alertVO.getLmsTarget());
                 * }
                 * else
                 * {
                 * target1=promotionDetailsVO.getEndRangeAsString();
                 * }
                 */
                String arr[] = null;
                if (c2sWflag == 1) {
                    long targetForMessage = 0;
                    for (int i = 0; i < c2sW.size(); i++) {
                        if (i == 0) {
                            targetForMessage = (long) c2sW.get(i);
                        } else if ((long) c2sW.get(i) > currentTransactionAmountc2sW && (long) c2sW.get(i) <= targetForMessage) {
                            targetForMessage = (long) c2sW.get(i);
                        }
                    }
                    if(_logger.isDebugEnabled()){
    					_logger.debug(METHOD_NAME, "targetForMessage = "+targetForMessage);
    				}
                    // brajesh
                    if (("Y").equals(promotionDetailsVO.getMessageConfEnabled())) {
                        if (currentTransactionAmountc2sW < targetForMessage) {
                            if (!BTSLUtil.isNullString(promotionDetailsVO.getMessageFailure())) {
                                PushMessage pushMessage = null;
                                msg = promotionDetailsVO.getMessageFailure();

                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING5, BTSLUtil.getMessage(alertVO.getLocale(), "C2S_Weekly"));
                                // msg =
                                // msg.replace(PretupsI.LMS_MSG_SUBSTRING4,
                                // String.valueOf(targetForMessage));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, PretupsBL.getDisplayAmount(targetForMessage).toString());
                                // msg =
                                // msg.replace(PretupsI.LMS_MSG_SUBSTRING6,
                                // String.valueOf(currentTransactionAmountc2sW));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING6, PretupsBL.getDisplayAmount(currentTransactionAmountc2sW).toString());
                                // brajesh
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, lsmProfileSetName);
								msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode()));
                                // _lowBalRequestCode=(String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.LOW_BAL_MSGGATEWAY,alertVO.getNetworkCode());
                                pushMessage = new PushMessage(alertVO.getMsisdn(), msg, "", "", alertVO.getLocale());
                                pushMessage.push();
                            }

                        } else {
                            if (!BTSLUtil.isNullString(promotionDetailsVO.getMessageSuccess())) {
                                PushMessage pushMessage = null;
                                msg = promotionDetailsVO.getMessageSuccess();

							msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING5, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getServiceCode()+"C2S_Weekly"));
                                // msg =
                                // msg.replace(PretupsI.LMS_MSG_SUBSTRING4,String.valueOf(targetForMessage));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, PretupsBL.getDisplayAmount(targetForMessage).toString());

                                // msg =
                                // msg.replace(PretupsI.LMS_MSG_SUBSTRING6,
                                // String.valueOf(currentTransactionAmountc2sW));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING6, PretupsBL.getDisplayAmount(currentTransactionAmountc2sW).toString());

                                // brajesh
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, lsmProfileSetName);
							msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode()));
                                // pushMessage.push(_lowBalRequestCode,null);
                                pushMessage = new PushMessage(alertVO.getMsisdn(), msg, "", "", alertVO.getLocale());
                                pushMessage.push();
                            }
                        }
                    } else {

					arr= new String[]{BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getServiceCode()+"C2S_Weekly"),PretupsBL.getDisplayAmount(currentTransactionAmountc2sW).toString(),String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)),lsmProfileSetName,BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode())};
                       if (_logger.isDebugEnabled()) {
                            _logger.debug("currentTransactionAmountc2sW",
                                " currentTransactionAmountc2sW = " + currentTransactionAmountc2sW + " currentTransactionAmountc2sW = " + PretupsBL.getDisplayAmount(
                                    currentTransactionAmountc2sW).toString());
                            // if(_logger.isDebugEnabled())_logger.debug("currentTransactionAmountc2sW"," currentTransactionAmountc2sW = "+PretupsBL.getDisplayAmount(currentTransactionAmountc2sW).toString()
                            // );
                            // keyArgumentVO.setArguments(arr);
                        }
                        String senderMessage = null;
                        if (currentTransactionAmountc2sW < targetForMessage) {
                            senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_PROGRESSIVE_MESSAGE_FAIL, arr);
                        } else {
                            // arr[0]=array ;
                            senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_PROGRESSIVE_MESSAGE, arr);
                            // _lowBalRequestCode=(String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.LOW_BAL_MSGGATEWAY,alertVO.getNetworkCode());
                        }
                        final PushMessage pushMessage = new PushMessage(msisdn, senderMessage, null, null, locale);
                        pushMessage.push();
                    }
                    final StringBuffer otherInfo = new StringBuffer();
                    otherInfo.append(("[PRODUCT(S) AND THEIR BALANCE(S) = " + arr + "] "));
                    ProcessesLog.log("Progressive-Message-LMS", msisdn, msg, otherInfo);
                    arr = null;
                }
                if (c2sMflag == 1) {
                    long targetForMessage = 0;
                    for (int i = 0; i < c2sM.size(); i++) {
                        if (i == 0) {
                            targetForMessage = (long) c2sM.get(i);
                        } else if ((long) c2sM.get(i) > currentTransactionAmountc2sM && (long) c2sM.get(i) <= targetForMessage) {
                            targetForMessage = (long) c2sM.get(i);
                        }
                    }
    				if(_logger.isDebugEnabled()){
    					_logger.debug(METHOD_NAME, "targetForMessage = "+targetForMessage);
    				}
                    // brajesh
                    if (("Y").equals(promotionDetailsVO.getMessageConfEnabled())) {
                        if (currentTransactionAmountc2sM < targetForMessage) {
                            if (!BTSLUtil.isNullString(promotionDetailsVO.getMessageFailure())) {
                                PushMessage pushMessage = null;
                                msg = promotionDetailsVO.getMessageFailure();

						msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING5, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getServiceCode()+"C2S_Monthly"));
                                // msg =
                                // msg.replace(PretupsI.LMS_MSG_SUBSTRING4,
                                // String.valueOf(targetForMessage));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)));
                                // msg =
                                // msg.replace(PretupsI.LMS_MSG_SUBSTRING6,
                                // String.valueOf(currentTransactionAmountc2sM));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING6, String.valueOf(PretupsBL.getDisplayAmount(currentTransactionAmountc2sM)));

                                // brajesh
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, lsmProfileSetName);
						msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode()));
                                pushMessage = new PushMessage(alertVO.getMsisdn(), msg, "", "", alertVO.getLocale());
                                pushMessage.push();
                            }
                        } else {
                            if (!BTSLUtil.isNullString(promotionDetailsVO.getMessageSuccess())) {
                                PushMessage pushMessage = null;
                                msg = promotionDetailsVO.getMessageSuccess();

						msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING5, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getServiceCode()+"C2S_Monthly"));
                                // msg =
                                // msg.replace(PretupsI.LMS_MSG_SUBSTRING4,
                                // String.valueOf(targetForMessage));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)));
                                // msg =
                                // msg.replace(PretupsI.LMS_MSG_SUBSTRING6,
                                // String.valueOf(currentTransactionAmountc2sM));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING6, String.valueOf(PretupsBL.getDisplayAmount(currentTransactionAmountc2sM)));

                                // brajesh
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, lsmProfileSetName);
						msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode()));
                                // pushMessage.push(_lowBalRequestCode,null);
                                pushMessage = new PushMessage(alertVO.getMsisdn(), msg, "", "", alertVO.getLocale());
                                pushMessage.push();
                            }
                        }
                    } else {
					arr= new String[]{BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getServiceCode()+"C2S_Monthly"),PretupsBL.getDisplayAmount(currentTransactionAmountc2sM).toString(),String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)),lsmProfileSetName,BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode())};
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("currentTransactionAmountc2sM", " currentTransactionAmountc2sM = " + currentTransactionAmountc2sM);
                        }
                        String senderMessage = null;
                        if (currentTransactionAmountc2sM < targetForMessage) {
                            senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_PROGRESSIVE_MESSAGE_FAIL, arr);
                        } else {
                            senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_PROGRESSIVE_MESSAGE, arr);
                            // _lowBalRequestCode=(String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.LOW_BAL_MSGGATEWAY,alertVO.getNetworkCode());
                        }
                        final PushMessage pushMessage = new PushMessage(msisdn, senderMessage, null, null, locale);
                        pushMessage.push();
                    }
                    final StringBuffer otherInfo = new StringBuffer();
                    otherInfo.append(("[PRODUCT(S) AND THEIR BALANCE(S) = " + arr + "] "));
                    ProcessesLog.log("Progressive-Message-LMS", msisdn, msg, otherInfo);
                    arr = null;
                }
                if (c2sEopflag == 1) {
                    long targetForMessage = 0;
                    for (int i = 0; i < c2sEop.size(); i++) {
                        if (i == 0) {
                            targetForMessage = (long) c2sEop.get(i);
                        } else if ((long) c2sEop.get(i) > currentTransactionAmountc2sEop && (long) c2sEop.get(i) <= targetForMessage) {
                            targetForMessage = (long) c2sEop.get(i);
                        }
                        if (_logger.isDebugEnabled()) {
                            _logger.debug(METHOD_NAME, "c2sEop.get(i):" + c2sEop.get(i));
                        }
                    }
                    if(_logger.isDebugEnabled()){
    					_logger.debug(METHOD_NAME, "targetForMessage = "+targetForMessage);
                    }
                    if (("Y").equals(promotionDetailsVO.getMessageConfEnabled())) {
                        if (currentTransactionAmountc2sEop < targetForMessage) {
                            if (!BTSLUtil.isNullString(promotionDetailsVO.getMessageFailure())) {
                                PushMessage pushMessage = null;
                                msg = promotionDetailsVO.getMessageFailure();

						msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING5, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getServiceCode()+"C2S_Eop"));
                                // msg =
                                // msg.replace(PretupsI.LMS_MSG_SUBSTRING4,
                                // String.valueOf(targetForMessage));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, PretupsBL.getDisplayAmount(targetForMessage).toString());
                                // msg =
                                // msg.replace(PretupsI.LMS_MSG_SUBSTRING6,
                                // String.valueOf(currentTransactionAmountc2sEop));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING6, PretupsBL.getDisplayAmount(currentTransactionAmountc2sEop).toString());

                                // brajesh
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, lsmProfileSetName);
						msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode()));
                                pushMessage = new PushMessage(alertVO.getMsisdn(), msg, "", "", alertVO.getLocale());
                                pushMessage.push();
                            }
                        } else {
                            if (!BTSLUtil.isNullString(promotionDetailsVO.getMessageSuccess())) {
                                PushMessage pushMessage = null;
                                msg = promotionDetailsVO.getMessageSuccess();

						msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING5, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getServiceCode()+"C2S_Eop"));
                                // msg =
                                // msg.replace(PretupsI.LMS_MSG_SUBSTRING4,
                                // String.valueOf(targetForMessage));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, PretupsBL.getDisplayAmount(targetForMessage).toString());
                                // msg =
                                // msg.replace(PretupsI.LMS_MSG_SUBSTRING6,
                                // String.valueOf(currentTransactionAmountc2sEop));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING6, PretupsBL.getDisplayAmount(currentTransactionAmountc2sEop).toString());
                                // brajesh
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, lsmProfileSetName);
						msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode()));
                                // pushMessage.push(_lowBalRequestCode,null);
                                pushMessage = new PushMessage(alertVO.getMsisdn(), msg, "", "", alertVO.getLocale());
                                pushMessage.push();
                            }
                        }
                    } else {
					arr= new String[]{BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getServiceCode()+"C2S_Eop"),PretupsBL.getDisplayAmount(currentTransactionAmountc2sEop).toString(),String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)),lsmProfileSetName,BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode())};
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("currentTransactionAmountc2sEop", " currentTransactionAmountc2sEop = " + currentTransactionAmountc2sEop);
                        }
                        String senderMessage = null;
                        if (currentTransactionAmountc2sEop < targetForMessage) {
                            senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_PROGRESSIVE_MESSAGE_FAIL, arr);
                        } else {
                            senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_PROGRESSIVE_MESSAGE, arr);
                            // _lowBalRequestCode=(String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.LOW_BAL_MSGGATEWAY,alertVO.getNetworkCode());
                        }
                        final PushMessage pushMessage = new PushMessage(msisdn, senderMessage, null, null, locale);
                        pushMessage.push();
                    }
                    final StringBuffer otherInfo = new StringBuffer();
                    otherInfo.append(("[PRODUCT(S) AND THEIR BALANCE(S) = " + arr + "] "));
                    ProcessesLog.log("Progressive-Message-LMS", msisdn, msg, otherInfo);
                    arr = null;
                }

                if (c2cWflag == 1) {
                    long targetForMessage = 0;
                    for (int i = 0; i < c2cW.size(); i++) {
                        if (i == 0) {
                            targetForMessage = (long) c2cW.get(i);
                        } else if ((long) c2cW.get(i) > currentTransactionAmountc2cW && (long) c2cW.get(i) <= targetForMessage) {
                            targetForMessage = (long) c2cW.get(i);
                        }
                    }
                    // brajesh
                    if (("Y").equals(promotionDetailsVO.getMessageConfEnabled())) {
                        if (currentTransactionAmountc2cW < targetForMessage) {
                            if (!BTSLUtil.isNullString(promotionDetailsVO.getMessageFailure())) {
                                PushMessage pushMessage = null;
                                msg = promotionDetailsVO.getMessageFailure();

                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING5, BTSLUtil.getMessage(alertVO.getLocale(), "C2C_Weekly"));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING6, String.valueOf(PretupsBL.getDisplayAmount(currentTransactionAmountc2cW)));
                                // brajesh
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, lsmProfileSetName);
						msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode()));
                                pushMessage = new PushMessage(alertVO.getMsisdn(), msg, "", "", alertVO.getLocale());
                                pushMessage.push();
                            }
                        } else {
                            if (!BTSLUtil.isNullString(promotionDetailsVO.getMessageSuccess())) {
                                PushMessage pushMessage = null;
                                msg = promotionDetailsVO.getMessageSuccess();

                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING5, BTSLUtil.getMessage(alertVO.getLocale(), "C2C_Weekly"));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING6, String.valueOf(String.valueOf(PretupsBL.getDisplayAmount(currentTransactionAmountc2cW))));
                                // brajesh
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, lsmProfileSetName);
						msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode()));
                                // pushMessage.push(_lowBalRequestCode,null);
                                pushMessage = new PushMessage(alertVO.getMsisdn(), msg, "", "", alertVO.getLocale());
                                pushMessage.push();
                            }
                        }
                    } else {

					arr= new String[]{BTSLUtil.getMessage(alertVO.getLocale(),"C2C_Weekly"),PretupsBL.getDisplayAmount(currentTransactionAmountc2cW).toString(),String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)),lsmProfileSetName,BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode())};
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("currentTransactionAmountc2cW", " currentTransactionAmountc2cW = " + currentTransactionAmountc2cW);
                        }
                        String senderMessage = null;
                        if (currentTransactionAmountc2cW < targetForMessage) {
                            senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_PROGRESSIVE_MESSAGE_FAIL, arr);
                        } else {
                            senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_PROGRESSIVE_MESSAGE, arr);
                            // _lowBalRequestCode=(String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.LOW_BAL_MSGGATEWAY,alertVO.getNetworkCode());
                        }
                        final PushMessage pushMessage = new PushMessage(msisdn, senderMessage, null, null, locale);
                        pushMessage.push();
                    }
                    final StringBuffer otherInfo = new StringBuffer();
                    otherInfo.append(("[PRODUCT(S) AND THEIR BALANCE(S) = " + arr + "] "));
                    ProcessesLog.log("Progressive-Message-LMS", msisdn, msg, otherInfo);
                    arr = null;
                    // array = null;
                }
                if (c2cMflag == 1) {

                    long targetForMessage = 0;
                    for (int i = 0; i < c2cM.size(); i++) {
                        if (i == 0) {
                            targetForMessage = (long) c2cM.get(i);
                        } else if ((long) c2cM.get(i) > currentTransactionAmountc2cM && (long) c2cM.get(i) <= targetForMessage) {
                            targetForMessage = (long) c2cM.get(i);
                        }
                    }
                    // brajesh
                    if (("Y").equals(promotionDetailsVO.getMessageConfEnabled())) {
                        if (currentTransactionAmountc2cM < targetForMessage) {
                            if (!BTSLUtil.isNullString(promotionDetailsVO.getMessageFailure())) {
                                PushMessage pushMessage = null;
                                msg = promotionDetailsVO.getMessageFailure();

                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING5, BTSLUtil.getMessage(alertVO.getLocale(), "C2C_Monthly"));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING6, String.valueOf(PretupsBL.getDisplayAmount(currentTransactionAmountc2cM)));
                                // brajesh
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, lsmProfileSetName);
						msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode()));
                                pushMessage = new PushMessage(alertVO.getMsisdn(), msg, "", "", alertVO.getLocale());
                                pushMessage.push();
                            }
                        } else {
                            if (!BTSLUtil.isNullString(promotionDetailsVO.getMessageSuccess())) {
                                PushMessage pushMessage = null;
                                msg = promotionDetailsVO.getMessageSuccess();

                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING5, BTSLUtil.getMessage(alertVO.getLocale(), "C2C_Monthly"));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING6, String.valueOf(PretupsBL.getDisplayAmount(currentTransactionAmountc2cM)));
                                // brajesh
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, lsmProfileSetName);
						msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode()));
                                // pushMessage.push(_lowBalRequestCode,null);
                                pushMessage = new PushMessage(alertVO.getMsisdn(), msg, "", "", alertVO.getLocale());
                                pushMessage.push();
                            }
                        }
                    } else {
					arr= new String[]{BTSLUtil.getMessage(alertVO.getLocale(),"C2C_Monthly"),PretupsBL.getDisplayAmount(currentTransactionAmountc2cM).toString(),String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)),lsmProfileSetName,BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode())};
					if(_logger.isDebugEnabled()){
                            _logger.debug("currentTransactionAmountc2cM", " currentTransactionAmountc2cM = " + currentTransactionAmountc2cM);
                        }
                        String senderMessage = null;
                        if (currentTransactionAmountc2cM < targetForMessage) {
                            senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_PROGRESSIVE_MESSAGE_FAIL, arr);
                        } else {
                            senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_PROGRESSIVE_MESSAGE, arr);
                            // _lowBalRequestCode=(String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.LOW_BAL_MSGGATEWAY,alertVO.getNetworkCode());
                        }
                        final PushMessage pushMessage = new PushMessage(msisdn, senderMessage, null, null, locale);
                        pushMessage.push();
                    }
                    final StringBuffer otherInfo = new StringBuffer();
                    otherInfo.append(("[PRODUCT(S) AND THEIR BALANCE(S) = " + arr + "] "));
                    ProcessesLog.log("Progressive-Message-LMS", msisdn, msg, otherInfo);
                    arr = null;
                    // array = null;
                }
                if (c2cEopflag == 1) {
                    long targetForMessage = 0;
                    for (int i = 0; i < c2cEop.size(); i++) {
                        if (i == 0) {
                            targetForMessage = (long) c2cEop.get(i);
                        } else if ((long) c2cEop.get(i) > currentTransactionAmountc2cEop && (long) c2cEop.get(i) <= targetForMessage) {
                            targetForMessage = (long) c2cEop.get(i);
                        }
                    }
                    if (("Y").equals(promotionDetailsVO.getMessageConfEnabled())) {
                        if (currentTransactionAmountc2cEop < targetForMessage) {
                            if (!BTSLUtil.isNullString(promotionDetailsVO.getMessageFailure())) {
                                PushMessage pushMessage = null;
                                msg = promotionDetailsVO.getMessageFailure();

                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING5, BTSLUtil.getMessage(alertVO.getLocale(), "C2C_Eop"));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING6, String.valueOf(PretupsBL.getDisplayAmount(currentTransactionAmountc2cEop)));
                                // brajesh
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, lsmProfileSetName);
						msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode()));
                                pushMessage = new PushMessage(alertVO.getMsisdn(), msg, "", "", alertVO.getLocale());
                                pushMessage.push();
                            }
                        } else {
                            if (!BTSLUtil.isNullString(promotionDetailsVO.getMessageSuccess())) {
                                PushMessage pushMessage = null;
                                msg = promotionDetailsVO.getMessageSuccess();

                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING5, BTSLUtil.getMessage(alertVO.getLocale(), "C2C_Eop"));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING6, String.valueOf(PretupsBL.getDisplayAmount(currentTransactionAmountc2cEop)));
                                // brajesh
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, lsmProfileSetName);
						msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode()));
                                // pushMessage.push(_lowBalRequestCode,null);
                                pushMessage = new PushMessage(alertVO.getMsisdn(), msg, "", "", alertVO.getLocale());
                                pushMessage.push();
                            }
                        }
                    } else {
					arr= new String[]{BTSLUtil.getMessage(alertVO.getLocale(),"C2C_Eop"),PretupsBL.getDisplayAmount(currentTransactionAmountc2sEop).toString(),String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)),lsmProfileSetName,BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode())};
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("currentTransactionAmountc2cEop", " currentTransactionAmountc2cEop = " + currentTransactionAmountc2cEop);
                        }
                        String senderMessage = null;
                        if (currentTransactionAmountc2cEop < targetForMessage) {
                            senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_PROGRESSIVE_MESSAGE_FAIL, arr);
                        } else {
                            senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_PROGRESSIVE_MESSAGE, arr);
                            // _lowBalRequestCode=(String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.LOW_BAL_MSGGATEWAY,alertVO.getNetworkCode());
                        }
                        final PushMessage pushMessage = new PushMessage(msisdn, senderMessage, null, null, locale);
                        pushMessage.push();
                    }
                    final StringBuffer otherInfo = new StringBuffer();
                    otherInfo.append(("[PRODUCT(S) AND THEIR BALANCE(S) = " + arr + "] "));
                    ProcessesLog.log("Progressive-Message-LMS", msisdn, msg, otherInfo);
                    arr = null;
                }
                if (o2cWflag == 1) {
                    long targetForMessage = 0;
                    for (int i = 0; i < o2cW.size(); i++) {
                        if (i == 0) {
                            targetForMessage = (long) o2cW.get(i);
                        } else if ((long) o2cW.get(i) > currentTransactionAmounto2cW && (long) o2cW.get(i) <= targetForMessage) {
                            targetForMessage = (long) o2cW.get(i);
                        }
                    }
                    // brajesh
                    if (("Y").equals(promotionDetailsVO.getMessageConfEnabled())) {
                        if (currentTransactionAmounto2cW < targetForMessage) {
                            if (!BTSLUtil.isNullString(promotionDetailsVO.getMessageFailure())) {
                                PushMessage pushMessage = null;
                                msg = promotionDetailsVO.getMessageFailure();

                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING5, BTSLUtil.getMessage(alertVO.getLocale(), "O2C_Weekly"));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING6, String.valueOf(PretupsBL.getDisplayAmount(currentTransactionAmounto2cW)));
                                // brajesh
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, lsmProfileSetName);
						msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode()));
                                pushMessage = new PushMessage(alertVO.getMsisdn(), msg, "", "", alertVO.getLocale());
                                pushMessage.push();
                            }
                        } else {
                            if (!BTSLUtil.isNullString(promotionDetailsVO.getMessageSuccess())) {
                                PushMessage pushMessage = null;
                                msg = promotionDetailsVO.getMessageSuccess();

                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING5, BTSLUtil.getMessage(alertVO.getLocale(), "O2C_Weekly"));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING6, String.valueOf(PretupsBL.getDisplayAmount(currentTransactionAmounto2cW)));
                                // brajesh
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, lsmProfileSetName);
						msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode()));
                                // pushMessage.push(_lowBalRequestCode,null);
                                pushMessage = new PushMessage(alertVO.getMsisdn(), msg, "", "", alertVO.getLocale());
                                pushMessage.push();
                            }
                        }
                    } else {

					arr= new String[]{BTSLUtil.getMessage(alertVO.getLocale(),"O2C_Weekly"),PretupsBL.getDisplayAmount(currentTransactionAmounto2cW).toString(),String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)),lsmProfileSetName,BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode())};
                       if (_logger.isDebugEnabled()) {
                            _logger.debug("currentTransactionAmounto2cW", " currentTransactionAmounto2cW = " + currentTransactionAmounto2cW);
                        }
                        String senderMessage = null;
                        if (currentTransactionAmounto2cW < targetForMessage) {
                            senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_PROGRESSIVE_MESSAGE_FAIL, arr);
                        } else {
                            senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_PROGRESSIVE_MESSAGE, arr);
                            // _lowBalRequestCode=(String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.LOW_BAL_MSGGATEWAY,alertVO.getNetworkCode());
                        }
                        final PushMessage pushMessage = new PushMessage(msisdn, senderMessage, null, null, locale);
                        pushMessage.push();
                    }
                    final StringBuffer otherInfo = new StringBuffer();
                    otherInfo.append(("[PRODUCT(S) AND THEIR BALANCE(S) = " + arr + "] "));
                    ProcessesLog.log("Progressive-Message-LMS", msisdn, msg, otherInfo);
                    arr = null;
                }
                if (o2cMflag == 1) {
                    long targetForMessage = 0;
                    for (int i = 0; i < o2cM.size(); i++) {
                        if (i == 0) {
                            targetForMessage = (long) o2cM.get(i);
                        } else if ((long) o2cM.get(i) > currentTransactionAmounto2cM && (long) o2cM.get(i) <= targetForMessage) {
                            targetForMessage = (long) o2cM.get(i);
                        }
                    }
                    // brajesh
                    if (("Y").equals(promotionDetailsVO.getMessageConfEnabled())) {
                        if (currentTransactionAmounto2cM < targetForMessage) {
                            if (!BTSLUtil.isNullString(promotionDetailsVO.getMessageFailure())) {
                                PushMessage pushMessage = null;
                                msg = promotionDetailsVO.getMessageFailure();

                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING5, BTSLUtil.getMessage(alertVO.getLocale(), "O2C_Monthly"));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING6, String.valueOf(PretupsBL.getDisplayAmount(currentTransactionAmounto2cM)));
                                // brajesh
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, lsmProfileSetName);
						msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode()));
                                pushMessage = new PushMessage(alertVO.getMsisdn(), msg, "", "", alertVO.getLocale());
                                pushMessage.push();
                            }
                        } else {
                            if (!BTSLUtil.isNullString(promotionDetailsVO.getMessageSuccess())) {
                                PushMessage pushMessage = null;
                                msg = promotionDetailsVO.getMessageSuccess();

                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING5, BTSLUtil.getMessage(alertVO.getLocale(), "O2C_Monthly"));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING6, String.valueOf(PretupsBL.getDisplayAmount(currentTransactionAmounto2cM)));
                                // brajesh
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, lsmProfileSetName);
						msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode()));
                                // pushMessage.push(_lowBalRequestCode,null);
                                pushMessage = new PushMessage(alertVO.getMsisdn(), msg, "", "", alertVO.getLocale());
                                pushMessage.push();
                            }
                        }
                    } else {
					arr= new String[]{BTSLUtil.getMessage(alertVO.getLocale(),"O2C_Monthly"),PretupsBL.getDisplayAmount(currentTransactionAmounto2cM).toString(),String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)),lsmProfileSetName,BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode())};
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("currentTransactionAmounto2cM", " currentTransactionAmounto2cM = " + currentTransactionAmounto2cM);
                        }
                        String senderMessage = null;
                        if (currentTransactionAmounto2cM < targetForMessage) {
                            senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_PROGRESSIVE_MESSAGE_FAIL, arr);
                        } else {
                            senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_PROGRESSIVE_MESSAGE, arr);
                            // _lowBalRequestCode=(String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.LOW_BAL_MSGGATEWAY,alertVO.getNetworkCode());
                        }
                        final PushMessage pushMessage = new PushMessage(msisdn, senderMessage, null, null, locale);
                        pushMessage.push();
                    }
                    final StringBuffer otherInfo = new StringBuffer();
                    otherInfo.append(("[PRODUCT(S) AND THEIR BALANCE(S) = " + arr + "] "));
                    ProcessesLog.log("Progressive-Message-LMS", msisdn, msg, otherInfo);
                    arr = null;
                }
                if (o2cEopflag == 1) {
                    long targetForMessage = 0;
                    for (int i = 0; i < o2cEop.size(); i++) {
                        if (i == 0) {
                            targetForMessage = (long) o2cEop.get(i);
                        } else if ((long) o2cEop.get(i) > currentTransactionAmounto2cEop && (long) o2cEop.get(i) <= targetForMessage) {
                            targetForMessage = (long) o2cEop.get(i);
                        }
                    }
                    if (("Y").equals(promotionDetailsVO.getMessageConfEnabled())) {
                        if (currentTransactionAmounto2cEop < targetForMessage) {
                            if (!BTSLUtil.isNullString(promotionDetailsVO.getMessageFailure())) {
                                PushMessage pushMessage = null;
                                msg = promotionDetailsVO.getMessageFailure();

                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING5, BTSLUtil.getMessage(alertVO.getLocale(), "O2C_Eop"));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING6, String.valueOf(PretupsBL.getDisplayAmount(currentTransactionAmounto2cEop)));
                                // brajesh
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, lsmProfileSetName);
						msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode()));
                                pushMessage = new PushMessage(alertVO.getMsisdn(), msg, "", "", alertVO.getLocale());
                                pushMessage.push();
                            }
                        } else {
                            if (!BTSLUtil.isNullString(promotionDetailsVO.getMessageSuccess())) {
                                PushMessage pushMessage = null;
                                msg = promotionDetailsVO.getMessageSuccess();

                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING5, BTSLUtil.getMessage(alertVO.getLocale(), "O2C_Eop"));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)));
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING6, String.valueOf(PretupsBL.getDisplayAmount(currentTransactionAmounto2cEop)));
                                // brajesh
                                msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, lsmProfileSetName);
						msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode()));
                                // pushMessage.push(_lowBalRequestCode,null);
                                pushMessage = new PushMessage(alertVO.getMsisdn(), msg, "", "", alertVO.getLocale());
                                pushMessage.push();
                            }
                        }
                    } else {
					arr= new String[]{BTSLUtil.getMessage(alertVO.getLocale(),"O2C_Eop"),PretupsBL.getDisplayAmount(currentTransactionAmounto2cM).toString(),String.valueOf(PretupsBL.getDisplayAmount(targetForMessage)),lsmProfileSetName,BTSLUtil.getMessage(alertVO.getLocale(),alertVO.getProductCode())};
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("currentTransactionAmounto2cEop", " currentTransactionAmounto2cEop = " + currentTransactionAmounto2cEop);
                        }
                        String senderMessage = null;
                        if (currentTransactionAmounto2cEop < targetForMessage) {
                            senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_PROGRESSIVE_MESSAGE_FAIL, arr);
                        } else {
                            senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_PROGRESSIVE_MESSAGE, arr);
                            // _lowBalRequestCode=(String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.LOW_BAL_MSGGATEWAY,alertVO.getNetworkCode());
                        }
                        final PushMessage pushMessage = new PushMessage(msisdn, senderMessage, null, null, locale);
                        pushMessage.push();
                    }
                    final StringBuffer otherInfo = new StringBuffer();
                    otherInfo.append(("[PRODUCT(S) AND THEIR BALANCE(S) = " + arr + "] "));
                    ProcessesLog.log("Progressive-Message-LMS", msisdn, msg, otherInfo);
                    arr = null;
                }
                try {
                    Thread.sleep(lAlertSleepTime);
                } catch (Exception exSleep) {
                    _logger.errorTrace(METHOD_NAME, exSleep);
                }
                // targetList.clear();
                redemptionVO = null;
                // Added by Diwakar for flushing the flag in case of no
                // calculation in case of daily based profile
                c2sWflag = 0;
                c2sMflag = 0;
                c2sEopflag = 0;
                c2cWflag = 0;
                c2cMflag = 0;
                c2cEopflag = 0;
                o2cWflag = 0;
                o2cMflag = 0;
                o2cEopflag = 0;

                currentTransactionAmountc2sW = 0;
                currentTransactionAmountc2sM = 0;
                currentTransactionAmountc2sEop = 0;
                currentTransactionAmountc2cW = 0;
                currentTransactionAmountc2cM = 0;
                currentTransactionAmountc2cEop = 0;
                currentTransactionAmounto2cW = 0;
                currentTransactionAmounto2cM = 0;
                currentTransactionAmounto2cEop = 0;
                // Ended Here

            }
            return recordCount;
        }// end try
        catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Error:" + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
            return recordCount;
        } finally {
            try {
                if (pstmtUOP != null) {
                    ;
                }
                pstmtUOP.close();
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtDailyC2STD != null) {
                    ;
                }
                pstmtDailyC2STD.close();
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtDailyCTM != null) {
                    ;
                }
                pstmtDailyCTM.close();
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtDailyCTM1 != null) {
                    ;
                }
                pstmtDailyCTM1.close();
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }

            try {
                if (rstUOP != null) {
                    ;
                }
                rstUOP.close();
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rstDailyC2STD != null) {
                    ;
                }
                rstDailyC2STD.close();
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rstDailyCTM != null) {
                    ;
                }
                rstDailyCTM.close();
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rstDailyCTM1 != null) {
                    ;
                }
                rstDailyCTM1.close();
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtDailyC2STD0 != null) {
                    ;
                }
                pstmtDailyC2STD0.close();
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.info(METHOD_NAME, " Exiting");
            }
        }
    }

    private static Date getMonthlyTranscationFronDate(Date fromDate, Date currentDateValue) {
        final String METHOD_NAME = "getMonthlyTranscationFronDate";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " fromDate = " + fromDate + " , currentDateValue = " + currentDateValue);
        }
        final Calendar cal = BTSLDateUtil.getInstance();
        final Calendar cal1 = BTSLDateUtil.getInstance();
        final Calendar cal2 = BTSLDateUtil.getInstance();
        Date currentDate = null;
        Date date = cal.getTime();
        try {
            currentDate = currentDateValue;
            cal.setTime(fromDate); // LMS Profile Creation Date
            date = cal.getTime();
            cal2.setTime(fromDate);
            cal1.setTime(currentDate);// Current Date
            int diff = 0;
            diff = cal1.compareTo(cal2);
            while (diff > 0) {
                cal2.add(Calendar.MONTH, 1); // Add One Month
                diff = cal1.compareTo(cal2);
                if (diff > 0) {
                    date = cal2.getTime();
                }

            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " date = " + date);
            }
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Exiting");
            }
        }
        return date;
    }

    /**
     * @author diwakar
     * @param fromDate
     * @param toDate
     * @param currentDateValue
     * @return
     */
    private static Date getEopTranscationFromDate(Date fromDate, Date toDate, Date currentDateValue) {
        final String METHOD_NAME = "getEopTranscationFromDate";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " fromDate = " + fromDate + " , currentDateValue = " + currentDateValue);
        }
        final Calendar cal = BTSLDateUtil.getInstance();
        final Calendar cal1 = BTSLDateUtil.getInstance();
        final Calendar cal2 = BTSLDateUtil.getInstance();
        Date currentDate = null;
        Date date = null;
        try {
            currentDate = currentDateValue;
            cal.setTime(fromDate); // LMS Profile Creation Date
            cal2.setTime(toDate);
            cal1.setTime(currentDate);// Current Date
            cal1.set(Calendar.DATE, -1);
            int diff = 0;
            diff = cal1.compareTo(cal2);
            if (diff <= 0) {
                date = cal.getTime();
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " date = " + date);
            }
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Exiting");
            }
        }
        return date;
    }

    private static Date getWeeklyTranscationFromDate(Date fromDate, Date currentDate) {
        final String METHOD_NAME = "getWeeklyTranscationFromDate";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " fromDate = " + fromDate + " , currentDate = " + currentDate);
        }
        Date date = null;
        int noOfReminderDays = 0;
        try {
            noOfReminderDays = ((BTSLUtil.getDifferenceInUtilDates(fromDate, currentDate)) % 7);
			// Handling for 1st day Progresive message if older user associated with LMS Profile
            if (noOfReminderDays == 0 && BTSLUtil.getDifferenceInUtilDates(fromDate, currentDate) > 0) {
                noOfReminderDays = 7;
            }
            date = BTSLUtil.getDifferenceDate(currentDate, -noOfReminderDays);
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " date = " + date);
            }
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Exiting : date = " + date + ", noOfReminderDays = " + noOfReminderDays);
            }
        }
        return date;
    }
}
