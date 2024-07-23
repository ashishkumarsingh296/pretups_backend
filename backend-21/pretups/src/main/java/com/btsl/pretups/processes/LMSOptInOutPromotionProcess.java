/**
 * @(#)LMSOptInOutPromotionProcess.java
 *                                      Copyright(c) 2015, Bharti Telesoft Ltd.
 *                                      All Rights Reserved
 * 
 *                                      <Process to push messages to the users
 *                                      associated on the previous date>
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Author Date History
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Diwakar 03 July,2015 Initital Creation
 * 
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 */

package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

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
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.ibm.icu.util.Calendar;

public class LMSOptInOutPromotionProcess {

    private static final Log logger = LogFactory.getLog(LMSOptInOutPromotionProcess.class.getName());
   
    private static ProcessStatusVO processStatusVO = null;
    private static final String exception = "Exception : ";
    private static String btslException = "BTSLBaseException : ";
    private static String sqlException = "SQLException : ";
    private static String sqlQuery = "SQL Query :";
    
    public static void main(String[] args) {
        final String methodName = "main";
        try {
            if (args.length != 2) {
                LogFactory.printLog(methodName, "Usage : LMSOptInOutPromotionProcess [Constants file] [LogConfig file] [Upload File Path]", logger);
                return;
            }
            final File constantsFile = Constants.validateFilePath(args[0]);
            if (!constantsFile.exists()) {
                LogFactory.printLog(methodName, " Constants File Not Found ............", logger);
                return;
            }
            final File logconfigFile = Constants.validateFilePath(args[1]);
            if (!logconfigFile.exists()) {
                LogFactory.printLog(methodName, " Logconfig File Not Found .............", logger);
                return;
            }

            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            LookupsCache.loadLookAtStartup();
        }// end try
        catch (Exception ex) {
            logger.error(methodName, "Error in Loading Configuration files ...........................: " + ex);
			logger.errorTrace(methodName, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            final LMSOptInOutPromotionProcess lmsOptInOutPromotionProcess = new LMSOptInOutPromotionProcess();
            lmsOptInOutPromotionProcess.process();
        } catch (BTSLBaseException be) {
            logger.error(methodName, btslException + be.getMessage());
            logger.errorTrace(methodName, be);
        } catch (Exception e) {
            logger.error(methodName, exception + e.getMessage());
            logger.errorTrace(methodName, e);
        } finally {
            if (logger.isDebugEnabled()) {
                logger.info(methodName, " Exiting");
            }
            ConfigServlet.destroyProcessCache();
        }

    }

    public void process() throws BTSLBaseException {
        final String methodName = "process";
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, " Entered:");
        }
        ProcessBL processBL = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        boolean statusOk = false;
        Date processedUpto = null;
        final Date currentDate = new Date();
        LoyaltyVO loyaltyVO = null;
        ArrayList associatedUserList = null;

        ProcessStatusVO processStatusMISVO = null;
        Date processedUptoMIS = null;
        try {
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue()) {
                mcomCon = new MComConnection();
                con=mcomCon.getConnection();
                processBL = new ProcessBL();
                String optInOutMisCkReq = Constants.getProperty("LMSOPTINOUT_MIS_CHECK_REQUIRED");
                if (BTSLUtil.isNullString(optInOutMisCkReq) || "null".equalsIgnoreCase(optInOutMisCkReq) || !PretupsI.NO.equalsIgnoreCase(optInOutMisCkReq)) {
                    optInOutMisCkReq = PretupsI.YES;
                }
                
                if (PretupsI.YES.equals(optInOutMisCkReq)) {
                    // Process should not execute until the MIS has not executed
                    // successfully for previous day
                    processStatusMISVO = processBL.checkProcessUnderProcess(con, ProcessI.C2SMIS);
                    processedUptoMIS = processStatusMISVO.getExecutedUpto();
                    if (processedUptoMIS != null) {
                        con.rollback();
                        final Calendar cal4CurrentDate = Calendar.getInstance();
                        final Calendar cal14MisExecutedUpTo = Calendar.getInstance();
                        cal4CurrentDate.add(Calendar.DAY_OF_MONTH, -1);
                        final Date currentDate1 = cal4CurrentDate.getTime(); // Current
                        // Date
                        cal14MisExecutedUpTo.setTime(processedUptoMIS);
                        final Calendar cal24CurrentDate = BTSLDateUtil.getCalendar(cal4CurrentDate);
                        final Calendar cal34MisExecutedUpTo = BTSLDateUtil.getCalendar(cal14MisExecutedUpTo);
                        if (logger.isDebugEnabled()) {
                            logger.debug(methodName, "(currentDate - 1) = " + currentDate1 + " processedUptoMIS = " + processedUptoMIS);
                        }
                        if (cal24CurrentDate.compareTo(cal34MisExecutedUpTo) != 0) {
                            if (logger.isDebugEnabled()) {
                                logger.debug(methodName, "The MIS has not been executed for the previous day.");
                            }
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LMSPromotionProcess[process]", "", "",
                                "", "The MIS has not been executed for the previous day.");
                            throw new BTSLBaseException(methodName, methodName, PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
                        }
                    } else {
                        throw new BTSLBaseException(methodName, methodName, PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
                    }
                }
                loyaltyVO = new LoyaltyVO();
                PushMessage pushMessage = null;
                String msg = null;
                processStatusVO = processBL.checkProcessUnderProcess(con, ProcessI.LMS_OPTINOUT_PROMO_MSG);
                statusOk = processStatusVO.isStatusOkBool();
                // check process status.
                if (statusOk) {
                    processedUpto = processStatusVO.getExecutedUpto();
                    if (processedUpto != null) {
                        processedUpto = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(processedUpto));
                        final int diffDate = BTSLUtil.getDifferenceInUtilDates(processedUpto, currentDate);
                        if (diffDate <= 1) {
                            logger.error(methodName, " LMS OPT IN/OUT Promotion Process has already been executed...");
                            throw new BTSLBaseException(methodName, methodName, PretupsErrorCodesI.LMS_PROMOTION_PROCESS_ALREADY_EXECUTED);
                        }
                    }
                } else {
                    throw new BTSLBaseException(methodName, "process", PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
                }

                // Get the associated users.
                associatedUserList = loadAssociatedUsersAndProfiles(con);

                if (associatedUserList.isEmpty()) {
                    logger.error(methodName, " No users for association...");
                }
                // else block for message push
                else {
                	int associatedUserListSizes=associatedUserList.size();
                    for (int k = 0; k < associatedUserListSizes; k++) {
                        try {
                            loyaltyVO = (LoyaltyVO) associatedUserList.get(k);
                            if (("VOLUME").equals(loyaltyVO.getDetailType())) {
                                if (PretupsI.YES.equalsIgnoreCase(Constants.getProperty("MESAGE_REQURIED").trim())) {
                                    if (!BTSLUtil.isNullString(loyaltyVO.getMessage())) {
                                        if (logger.isDebugEnabled()) {
                                            logger.debug(methodName,
                                                "SetName = " + loyaltyVO.getSetName() + " , " + loyaltyVO.getFromDate().toString() + " , EndRange = " + PretupsBL
                                                    .getDisplayAmount(Long.valueOf(loyaltyVO.getEndRange())));
                                        }
                                        if (updateNotificationTime(con, loyaltyVO.getUserid(), loyaltyVO.getSetId()) > 0) {
                                            msg = loyaltyVO.getMessage();
                                            msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, loyaltyVO.getSetName());
                                            msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING2, BTSLUtil.getDateStringFromDate(loyaltyVO.getFromDate()));
                                            msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING3, BTSLUtil.getDateStringFromDate(loyaltyVO.getToDate()));
                                            // brajesh
											msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING5, BTSLUtil.getMessage(loyaltyVO.getLocale(),loyaltyVO.getPeriodId()));
											if(PretupsI.YES.equalsIgnoreCase(Constants.getProperty("PRODUCTCODE_IN_MESAGE_REQURIED").trim())){
												msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, PretupsBL.getDisplayAmount(Long.valueOf(loyaltyVO.getEndRange()))+":"+BTSLUtil.getMessage(loyaltyVO.getLocale(),loyaltyVO.getProductCode()));
											} else {
                                            msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, PretupsBL.getDisplayAmount(Long.valueOf(loyaltyVO.getEndRange())));
											}
											msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(loyaltyVO.getLocale(),loyaltyVO.getProductCode()));
											msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING8, BTSLUtil.getMessage(loyaltyVO.getLocale(),loyaltyVO.getServiceType()));
                                            pushMessage = new PushMessage(loyaltyVO.getMsisdn(), msg, "", "", loyaltyVO.getLocale());
                                            pushMessage.push();
                                        }
                                    }
                                } else {
                                    if (updateNotificationTime(con, loyaltyVO.getUserid(), loyaltyVO.getSetId()) > 0) {
                                        String messageRED = null;
                                        final String[] arr = { String.valueOf(loyaltyVO.getSetName()), BTSLUtil.getDateStringFromDate(loyaltyVO.getFromDate()), BTSLUtil
                                            .getDateStringFromDate(loyaltyVO.getToDate()), BTSLUtil.getMessage(loyaltyVO.getLocale(), loyaltyVO.getPeriodId(), null), PretupsBL
                                            .getDisplayAmount(Long.valueOf(loyaltyVO.getEndRange())),loyaltyVO.getProductCode(),loyaltyVO.getServiceType() };
                                        messageRED = BTSLUtil.getMessage(loyaltyVO.getLocale(), PretupsErrorCodesI.OPTINOUT_PROMOTION_MESSAGE_NOTIFICATION, arr);
                                        pushMessage = new PushMessage(loyaltyVO.getMsisdn(), messageRED, null, null, loyaltyVO.getLocale());
                                        pushMessage.push();
                                    }
                                }
                            } else {
                                // brajesh
                                if (PretupsI.YES.equalsIgnoreCase(Constants.getProperty("MESAGE_REQURIED").trim())) {
                                    if (updateNotificationTime(con, loyaltyVO.getUserid(), loyaltyVO.getSetId()) > 0) {
                                        msg = loyaltyVO.getMessage();
                                        msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, loyaltyVO.getSetName());
                                        msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING2, BTSLUtil.getDateStringFromDate(loyaltyVO.getFromDate()));
                                        msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING3, BTSLUtil.getDateStringFromDate(loyaltyVO.getToDate()));
										msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(loyaltyVO.getLocale(),loyaltyVO.getProductCode(),null));
										msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING8, BTSLUtil.getMessage(loyaltyVO.getLocale(),loyaltyVO.getServiceType(),null));
                                        pushMessage = new PushMessage(loyaltyVO.getMsisdn(), msg, "", "", loyaltyVO.getLocale());
                                        pushMessage.push();
                                    }
                                } else {
                                    if (updateNotificationTime(con, loyaltyVO.getUserid(), loyaltyVO.getSetId()) > 0) {
                                        String messageRED = null;
                                        final String[] arr = { String.valueOf(loyaltyVO.getSetName()), BTSLUtil.getDateStringFromDate(loyaltyVO.getFromDate()), BTSLUtil
                                            .getDateStringFromDate(loyaltyVO.getToDate()),String.valueOf(loyaltyVO.getProductCode()),loyaltyVO.getServiceType() };

                                        messageRED = BTSLUtil.getMessage(loyaltyVO.getLocale(), PretupsErrorCodesI.OPTINOUT_PROMOTION_MESSAGE_NOTIFICATION_TXN, arr);
                                        pushMessage = new PushMessage(loyaltyVO.getMsisdn(), messageRED, null, null, loyaltyVO.getLocale());
                                        pushMessage.push();
                                    }
                                }

                            }
                        } catch (Exception ex) {
                            logger.errorTrace(methodName, ex);
                        }
                    }
                }

                final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
                final int maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, processStatusVO);
                if (maxDoneDateUpdateCount > 0) {
                    con.commit();
                } else {
                    con.rollback();
                    throw new BTSLBaseException(methodName, methodName, PretupsErrorCodesI.LMS_COULD_NOT_UPDATE_MAX_DONE_DATE);
                }
            } else {
                logger.info(methodName,
                    "Opt IN/OUT Promotion Msg Process will execute only if the preference<" + ProcessI.LMS_OPTINOUT_PROMO_MSG + "> is enabled into the system.");
            }
        } catch (Exception ex) {
            logger.errorTrace(methodName, ex);
        } finally {
            try {
                if (statusOk) {
                    if (markProcessStatusAsComplete(con, ProcessI.LMS_OPTINOUT_PROMO_MSG) == 1) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            logger.errorTrace(methodName, e);
                        }
                    } else {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            logger.errorTrace(methodName, e);
                        }
                    }
                }
				if (mcomCon != null) {
					mcomCon.close("LMSOptInOutPromotionProcess#BTSLBaseException");
					mcomCon = null;
				}
            } catch (Exception ex) {
                logger.errorTrace(methodName, ex);
                logger.info(methodName, "Exception while closing statement in LMSPromotionProcess method ");
            }
        }

    }

    @SuppressWarnings("unchecked")
	ArrayList loadAssociatedUsersAndProfiles(Connection con) {
        final String methodName = "loadAssociatedUsersAndProfiles";
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, " Entered");
        }
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelect1 = null;
        PreparedStatement pstmtSelect2 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        LoyaltyVO loyaltyVO = null;
        Locale locale = null;
        ArrayList userList = null;
        final String defaultLanguage = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE));
        final String defaultCountry = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));

       
        LMSOptInOutPromotionProcessQry lmsOptInOutPromotionProcessQry = (LMSOptInOutPromotionProcessQry)ObjectProducer.getObject(QueryConstants.LMS_OPT_IN_OUT_PROMOTION_PROCESS_QRY, QueryConstants.QUERY_PRODUCER);
        final String selectQuery = lmsOptInOutPromotionProcessQry.loadAssociatedUsersAndProfilesQry();
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, sqlQuery + selectQuery);
        }

        final StringBuilder strBuff3 = new StringBuilder();
		strBuff3.append("SELECT uop.target, uop.detail_id FROM user_oth_profiles uop WHERE uop.set_id =?  AND uop.detail_id=?  AND uop.USER_ID=? AND uop.VERSION=? AND uop.PRODUCT_CODE=? ");
        final String selectQuery1 = strBuff3.toString();
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, sqlQuery + selectQuery1);
        }

        final StringBuilder strBuff2 = new StringBuilder();
        strBuff2.append("SELECT message_code,message1,message2 from MESSAGES_MASTER where message_code=? ");
        final String selectQuery2 = strBuff2.toString();
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, sqlQuery + selectQuery2);
        }

        try {
            userList = new ArrayList();
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect1 =  con.prepareStatement(selectQuery1);
            pstmtSelect2 = con.prepareStatement(selectQuery2);
            int index = 1;
            pstmtSelect.setString(index++, PretupsI.YES);
            pstmtSelect.setString(index++, PretupsI.NORMAL);
            pstmtSelect.setString(index++, PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT);
            pstmtSelect.setString(index++, PretupsI.LMS_PROMOTION_TYPE_STOCK);
            pstmtSelect.setString(index++, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(index++, PretupsI.USER_STATUS_ACTIVE);
            pstmtSelect.setString(index++, PretupsI.NO);
            pstmtSelect.setString(index++, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(index++, PretupsI.STATUS_ACTIVE);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                loyaltyVO = new LoyaltyVO();
                loyaltyVO.setSetId(rs.getString("set_id"));
                loyaltyVO.setUserid(rs.getString("user_id"));
                loyaltyVO.setSetName(rs.getString("set_name"));
                loyaltyVO.setMsisdn(rs.getString("msisdn"));
                loyaltyVO.setDetailId(rs.getString("detail_id"));
                loyaltyVO.setPeriodId(rs.getString("period_id"));
                loyaltyVO.setVersion(rs.getString("VERSION"));
				loyaltyVO.setProductCode(rs.getString("PRODUCT_CODE"));
				loyaltyVO.setServiceType(rs.getString("service_code"));
                if (PretupsI.NO.equals(rs.getString("REF_BASED_ALLOWED"))) {
                    loyaltyVO.setEndRange(rs.getString("end_range"));
                } else {
                    pstmtSelect1.clearParameters();
                    index = 1;
                    pstmtSelect1.setString(index++, loyaltyVO.getSetId());
                    pstmtSelect1.setString(index++, loyaltyVO.getDetailId());
                    pstmtSelect1.setString(index++, loyaltyVO.getUserid());
                	pstmtSelect1.setString(index++,loyaltyVO.getVersion());
					pstmtSelect1.setString(index++,loyaltyVO.getProductCode());
                    rs1 = pstmtSelect1.executeQuery();
                    while (rs1.next()) {
                        try {
                            loyaltyVO.setEndRange(rs1.getString("target"));
                        } catch (Exception e) {
                            logger.errorTrace(methodName, e);
                        }
                    }
                    rs1 = null;
                }
                loyaltyVO.setFromDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                loyaltyVO.setToDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_to")));
                locale = new Locale(rs.getString("ph_language"), rs.getString("country"));
                loyaltyVO.setMessageConfEnabled(rs.getString("MESSAGE_MANAGEMENT_ENABLED"));
                loyaltyVO.setDetailType(rs.getString("detail_type"));
                boolean isDuplicateUserNotFound = true;

                if (PretupsI.YES.equals(loyaltyVO.getMessageConfEnabled())) {
                    String messageCode = null;
                    if (("VOLUME").equals(loyaltyVO.getDetailType())) {
                        messageCode = PretupsI.OPTINOUT_WEL_MESSAGE + "_" + loyaltyVO.getSetId();
                    } else if (("TRANS").equals(loyaltyVO.getDetailType())) {
                        messageCode = PretupsI.OPTINOUT_TRA_WEL_MSG + "_" + loyaltyVO.getSetId();
                    }

                    pstmtSelect2.clearParameters();
                    index = 1;
                    pstmtSelect2.setString(index++, messageCode);
                    rs2 = pstmtSelect2.executeQuery();
                    while (rs2.next()) {
                        try {
                            if (("VOLUME").equals(loyaltyVO.getDetailType())) {
                                if (!isDuplicateEntryOfTarProfile(userList, loyaltyVO)) {
                                    if (!(defaultLanguage.equalsIgnoreCase(rs.getString("ph_language")) && defaultCountry.equalsIgnoreCase(rs.getString("country")))) {
                                        loyaltyVO.setMessage(rs2.getString("message2"));
                                    } else {
                                        loyaltyVO.setMessage(rs2.getString("message1"));
                                    }
                                    isDuplicateUserNotFound = false;
                                }
                            } else if (("TRANS").equals(loyaltyVO.getDetailType())) {
                                if (!isDuplicateEntryOfTxnProfile(userList, loyaltyVO)) {
                                    if (!(defaultLanguage.equalsIgnoreCase(rs.getString("ph_language")) && defaultCountry.equalsIgnoreCase(rs.getString("country")))) {
                                        loyaltyVO.setMessage(rs2.getString("message2"));
                                    } else {
                                        loyaltyVO.setMessage(rs2.getString("message1"));
                                    }
                                    isDuplicateUserNotFound = false;
                                }
                            }
                        } catch (Exception e) {
                            logger.error(methodName, "Exception:be=" + e);
                        }
                    }
                    loyaltyVO.setLocale(locale);
                } else {
                    try {
                        if (("VOLUME").equals(loyaltyVO.getDetailType())) {
                            if (!isDuplicateEntryOfTarProfile(userList, loyaltyVO)) {
                                locale = new Locale(rs.getString("ph_language"), rs.getString("country"));
                                if (!(defaultLanguage.equalsIgnoreCase(rs.getString("ph_language")) && defaultCountry.equalsIgnoreCase(rs.getString("country")))) {
                                    loyaltyVO.setMessage(BTSLUtil.getMessage(locale, PretupsI.OPTINOUT_WEL_MESSAGE_LANG2, null));
                                } else {
                                    loyaltyVO.setMessage(BTSLUtil.getMessage(locale, PretupsI.OPTINOUT_WEL_MESSAGE_LANG1, null));
                                }
                                isDuplicateUserNotFound = false;
                            }
                        } else {
                            if (!isDuplicateEntryOfTxnProfile(userList, loyaltyVO)) {
                                locale = new Locale(rs.getString("ph_language"), rs.getString("country"));
                                if (!(defaultLanguage.equalsIgnoreCase(rs.getString("ph_language")) && defaultCountry.equalsIgnoreCase(rs.getString("country")))) {
                                    loyaltyVO.setMessage(BTSLUtil.getMessage(locale, PretupsI.OPTINOUT_TRA_WEL_MSG_LANG2, null));
                                } else {
                                    loyaltyVO.setMessage(BTSLUtil.getMessage(locale, PretupsI.OPTINOUT_TRA_WEL_MSG_LANG1, null));
                                }
                                isDuplicateUserNotFound = false;
                            }
                        }
                    } catch (Exception e) {
                        logger.errorTrace(methodName, e);
                    }
                    loyaltyVO.setLocale(locale);
                }
                if (!isDuplicateUserNotFound) {
                    userList.add(loyaltyVO);
                }
                isDuplicateUserNotFound = true;
            }
        } catch (SQLException sqe) {
            logger.error(methodName, sqlException + sqe);
            logger.errorTrace(methodName, sqe);
        } catch (Exception ex) {
            logger.error(methodName, exception + ex);
            logger.errorTrace(methodName, ex);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
            try {
                if (rs2 != null) {
                    rs2.close();
                }
            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect1 != null) {
                    pstmtSelect1.close();
                }
            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect2 != null) {
                    pstmtSelect2.close();
                }
            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "Exiting: userServicesList size=" + userList.size());
            }
        }
        return userList;
    }

    private static int markProcessStatusAsComplete(Connection con, String processId) {
        final String methodName = "markProcessStatusAsComplete";
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, " Entered:  p_processId:" + processId);
        }
        int updateCount = 0;
        Date currentDate = new Date();
        final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        processStatusVO.setProcessID(processId);
        processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        processStatusVO.setStartDate(currentDate);
        processStatusVO.setExecutedOn(currentDate);
        currentDate = BTSLUtil.addDaysInUtilDate(currentDate, -1);
        processStatusVO.setExecutedUpto(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(con, processStatusVO);
        } catch (Exception e) {
            logger.errorTrace(methodName, e);
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "Exception= " + e.getMessage());
            }
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    private int updateNotificationTime(Connection con, String userId, String lmsProfile) {
        final String methodName = "updateNotificationTime";
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, " Entered:  user_id = " + userId + " , lms_profile = " + lmsProfile);
        }
        PreparedStatement pstmtSelect = null;
        final StringBuilder selectQueryBuffer = new StringBuilder();
        selectQueryBuffer.append(" UPDATE  channel_users SET OPT_IN_OUT_NOTIFY_DATE=? ");
        selectQueryBuffer.append(" WHERE user_id=? ");
        selectQueryBuffer.append(" AND lms_profile=? ");
        final String selectQuery = selectQueryBuffer.toString();
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, sqlQuery + selectQuery);
        }
        int updateCount = 0;
        final Date date = new Date();
        try {
            pstmtSelect =  con.prepareStatement(selectQuery);
            int index = 1;
            pstmtSelect.setTimestamp(index++, BTSLUtil.getSQLDateTimeFromUtilDate(date));
            pstmtSelect.setString(index++, userId);
            pstmtSelect.setString(index++, lmsProfile);
            updateCount = pstmtSelect.executeUpdate();
            if (updateCount == 1) {
                con.commit();
                if (logger.isDebugEnabled()) {
                    logger.debug(methodName, "The notification has been updated for channel user =" + userId);
                }
            } else {
                con.rollback();
            }
        } catch (SQLException sqe) {
            logger.error(methodName, sqlException + sqe);
            logger.errorTrace(methodName, sqe);
        } catch (Exception ex) {
            logger.error(methodName, exception + ex);
            logger.errorTrace(methodName, ex);

        } finally {
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "Exiting: updateCount =" + updateCount);
            }
        }
        return updateCount;
    }

    public boolean isDuplicateEntryOfTxnProfile(ArrayList userList, LoyaltyVO loyaltyVO) {
        final String methodName = "isDuplicateEntryOfTxnProfile";
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, " Entered:  user_id = " + loyaltyVO.getUserid() + " ,Notification MSISDN " + loyaltyVO.getMsisdn());
        }
        boolean isFound = false;
        try {
            if (!userList.isEmpty()) {
                final Iterator iter = userList.iterator();
                while (iter.hasNext()) {
                    final LoyaltyVO loyaltyVOTemp = (LoyaltyVO) iter.next();
                    if (loyaltyVOTemp.getUserid().equalsIgnoreCase(loyaltyVO.getUserid()) && loyaltyVOTemp.getMsisdn().equalsIgnoreCase(loyaltyVO.getMsisdn()) && loyaltyVOTemp
                        .getVersion().equalsIgnoreCase(loyaltyVO.getVersion()) && loyaltyVOTemp.getProductCode().equalsIgnoreCase(loyaltyVO.getProductCode()) && loyaltyVOTemp.getServiceType().equalsIgnoreCase(loyaltyVO.getServiceType())) {
                        isFound = true;
                        break;
                    }
                }
            }
        } catch (RuntimeException e) {
            logger.errorTrace(methodName, e);
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, " isFound = " + isFound);
            }
        }
        return isFound;
    }

    public boolean isDuplicateEntryOfTarProfile(ArrayList userList, LoyaltyVO loyaltyVO) {
        final String methodName = "isDuplicateEntryOfTarProfile";
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, " Entered:  user_id = " + loyaltyVO.getUserid() + " ,Notification MSISDN " + loyaltyVO.getMsisdn());
        }
        boolean isFound = false;
        try {
            if (!userList.isEmpty()) {
                final Iterator iter = userList.iterator();
                while (iter.hasNext()) {
                    final LoyaltyVO loyaltyVOTemp = (LoyaltyVO) iter.next();
                    if (loyaltyVOTemp.getUserid().equalsIgnoreCase(loyaltyVO.getUserid()) && loyaltyVOTemp.getMsisdn().equalsIgnoreCase(loyaltyVO.getMsisdn()) && loyaltyVOTemp
                        .getVersion().equalsIgnoreCase(loyaltyVO.getVersion()) && loyaltyVOTemp.getPeriodId().equalsIgnoreCase(loyaltyVO.getPeriodId()) && loyaltyVOTemp.getProductCode().equalsIgnoreCase(loyaltyVO.getProductCode()) && loyaltyVOTemp.getServiceType().equalsIgnoreCase(loyaltyVO.getServiceType())) {
                        isFound = true;
                        break;
                    }
                }
            }
        } catch (RuntimeException e) {
            logger.errorTrace(methodName, e);
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, " isFound = " + isFound);
            }
        }
        return isFound;
    }
}
