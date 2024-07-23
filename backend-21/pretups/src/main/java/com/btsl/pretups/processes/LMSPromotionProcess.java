/**
 * @(#)LMSPromotionProcess.java
 *                              Copyright(c) 2005, Bharti Telesoft Ltd.
 *                              All Rights Reserved
 * 
 *                              <Process to push messages to the users
 *                              associated on the previous date>
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Vibhu Trehan 10 Jan,2014 Initital Creation
 * 
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 */

package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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

public class LMSPromotionProcess {

    private static Log logger = LogFactory.getLog(LMSPromotionProcess.class.getName());
    private static ProcessBL processBL = null;
    private static ProcessStatusVO processStatusVO = null;

    public static void main(String[] args) {
        final String METHOD_NAME = "main";
        try {
            if (args.length != 2) {
                LogFactory.printLog(METHOD_NAME, "Usage : LMSPromotionProcess [Constants file] [LogConfig file] [Upload File Path]", logger);
                return;
            }
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                LogFactory.printLog(METHOD_NAME, " Constants File Not Found .............", logger);

                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                LogFactory.printLog(METHOD_NAME, " Logconfig File Not Found .............", logger);
                return;
            }

            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            LookupsCache.loadLookAtStartup();
        }// end try
        catch (Exception ex) {
            LogFactory.printLog(METHOD_NAME, "Error in Loading Configuration files ...........................: "+ex, logger);
            logger.errorTrace(METHOD_NAME, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            final LMSPromotionProcess lmsPromotionProcess = new LMSPromotionProcess();
            lmsPromotionProcess.process();
        } catch (BTSLBaseException be) {
            logger.error("main", "BTSLBaseException : " + be.getMessage());
            logger.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            logger.error("main", "Exception : " + e.getMessage());
            logger.errorTrace(METHOD_NAME, e);
        } finally {
            if (logger.isDebugEnabled()) {
                logger.info("main", " Exiting");
            }
            ConfigServlet.destroyProcessCache();
        }

    }

    public void process() throws BTSLBaseException {
        final String methodName = "process";
        if (logger.isDebugEnabled()) {
            logger.debug("LMSPromotionProcess", " Entered:");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        boolean statusOk = false;
        Date processedUpto = null;
        final Date currentDate = new Date();
        LoyaltyVO loyaltyVO = null;
        ArrayList associatedUserList = null;

        ProcessStatusVO _processStatusMISVO = null;
        Date processedUptoMIS = null;
        try {
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
            processBL = new ProcessBL();
            String lmsPromoMisCkReq = Constants.getProperty("LMSPROMO_MIS_CHECK_REQUIRED");
            if (BTSLUtil.isNullString(lmsPromoMisCkReq)) {
                lmsPromoMisCkReq = PretupsI.YES;
            } else if ("null".equalsIgnoreCase(lmsPromoMisCkReq) || !PretupsI.NO.equalsIgnoreCase(lmsPromoMisCkReq)) {
                lmsPromoMisCkReq = PretupsI.YES;
            }
            if (PretupsI.YES.equals(lmsPromoMisCkReq)) {
                // Process should not execute until the MIS has not executed
                // successfully for previous day
                _processStatusMISVO = processBL.checkProcessUnderProcess(con, ProcessI.C2SMIS);
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
                    if (logger.isDebugEnabled()) {
                        logger.debug(methodName, "(currentDate - 1) = " + currentDate1 + " processedUptoMIS = " + processedUptoMIS);
                    }
                    if (cal24CurrentDate.compareTo(cal34MisExecutedUpTo) != 0) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(methodName, "The MIS has not been executed for the previous day.");
                        }
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LMSPromotionProcess[process]", "", "", "",
                            "The MIS has not been executed for the previous day.");
                        throw new BTSLBaseException(methodName, methodName, PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
                    }
                } else {
                    throw new BTSLBaseException(methodName, methodName, PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
                }
            } 
            loyaltyVO = new LoyaltyVO();
            PushMessage pushMessage = null;
            String msg = null;
            processStatusVO = processBL.checkProcessUnderProcess(con, ProcessI.LMS_PROMO_MSG);
            statusOk = processStatusVO.isStatusOkBool();
            // check process status.
            if (statusOk) {
                processedUpto = processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    processedUpto = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(processedUpto));
                    final int diffDate = BTSLUtil.getDifferenceInUtilDates(processedUpto, currentDate);
                    if (diffDate <= 1) {
                        logger.error("LMSPromotionProcess", " LMS Promotion Process has already been executed...");
                        throw new BTSLBaseException("LMSPromotionProcess", "process", PretupsErrorCodesI.LMS_PROMOTION_PROCESS_ALREADY_EXECUTED);
                    }
                }
            } else {
                throw new BTSLBaseException("LMSPromotionProcess", "process", PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
            }

            // Get the associated users.
            associatedUserList = loadAssociatedUsersAndProfiles(con);

            if (associatedUserList.isEmpty()) {
                logger.error("LMSPromotionProcess", " No users for association...");
            }
            // else block for message push
            else {
            	int userListSizes = associatedUserList.size();
                for (int k = 0; k < userListSizes ; k++) {
                    try {
                        loyaltyVO = (LoyaltyVO) associatedUserList.get(k);
                        // Handling of OPT IN/OPT OUT Message
                        if (PretupsI.YES.equalsIgnoreCase(loyaltyVO.getOptInOutEnabled()) && PretupsI.NORMAL.equalsIgnoreCase(loyaltyVO.getOptInOutStatus())) {
                            if (logger.isDebugEnabled()) {
                                logger
                                    .debug(
                                        "LMSPromotionProcess",
                                        " Escaping the welcome message for the  MSISDN [ " + loyaltyVO.getMsisdn() + " ] with associated profile [ " + loyaltyVO.getSetName() + " ] because of no acknowledgement received for optin/optout.");
                            }
                            continue;
                        }
                        if (PretupsI.PROFILE_VOL.equals(loyaltyVO.getDetailType())) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("LMSPromotionProcess", " loyaltyVO.getEndRange()= " + loyaltyVO.getEndRange() + ", loyaltyVO.getSetName()=" + loyaltyVO
                                    .getSetName());
                            }
                            if (PretupsI.YES.equalsIgnoreCase(Constants.getProperty("MESAGE_REQURIED").trim())) {
                            	if(!BTSLUtil.isNullString(loyaltyVO.getMessage())){
									msg=loyaltyVO.getMessage();	
									msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, loyaltyVO.getSetName());
									msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING2, BTSLUtil.getDateStringFromDate(loyaltyVO.getFromDate()));
									msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING3, BTSLUtil.getDateStringFromDate(loyaltyVO.getToDate()));
									//brajesh
									msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING5, BTSLUtil.getMessage(loyaltyVO.getLocale(),loyaltyVO.getPeriodId()));
									if(loyaltyVO.getEndRange().contains(",")){
										String targets="";
										String [] messasgeArr=loyaltyVO.getEndRange().split(",");
										Long[] messasgeArrLong = new Long[messasgeArr.length];
										int messasgeLength=messasgeArr.length;
										for(int i=0;i<messasgeLength;i++){
											try {
												messasgeArrLong[i]= Long.parseLong(messasgeArr[i]);
											} catch (Exception e) {
												messasgeArrLong[i]= BTSLUtil.parseStringToLong(messasgeArr[i]);												
											}
										}
										Arrays.sort(messasgeArrLong);
										 int messasgeLongs=messasgeArrLong.length;
											for(int i=0;i<messasgeLongs;i++){
												messasgeArr[i]= PretupsBL.getDisplayAmount(messasgeArrLong[i]);
												targets=targets+messasgeArr[i]+";";
											}
									targets=targets.substring(0, targets.lastIndexOf(";"));	
									msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, targets);	
									msg=msg+". Benefits will vary depending on target achieved.";
									}else{
									msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING4, PretupsBL.getDisplayAmount(Long.parseLong(loyaltyVO.getEndRange())));
									}
									msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(loyaltyVO.getLocale(),loyaltyVO.getProductCode()));
									msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING8, BTSLUtil.getMessage(loyaltyVO.getLocale(),loyaltyVO.getServiceType()));
									pushMessage=new PushMessage(loyaltyVO.getMsisdn(),msg,"","",loyaltyVO.getLocale());
									pushMessage.push();
								}
							}else{
								String messageRED=null;               	///
								String arr[]= {String.valueOf(loyaltyVO.getSetName()),BTSLUtil.getDateStringFromDate(loyaltyVO.getFromDate()),BTSLUtil.getDateStringFromDate(loyaltyVO.getToDate()),BTSLUtil.getMessage(loyaltyVO.getLocale(),loyaltyVO.getPeriodId()),PretupsBL.getDisplayAmount(Long.valueOf(loyaltyVO.getEndRange())),loyaltyVO.getProductCode(),loyaltyVO.getServiceType()};
								messageRED=BTSLUtil.getMessage(loyaltyVO.getLocale(),PretupsErrorCodesI.PROMOTION_MESSAGE_NOTIFICATION,arr);
								pushMessage=new PushMessage(loyaltyVO.getMsisdn(),messageRED,null,null,loyaltyVO.getLocale());
								pushMessage.push();
							}
						}else{
							//brajesh
							if(PretupsI.YES.equalsIgnoreCase(Constants.getProperty("MESAGE_REQURIED").trim()))
							{	
								if(!BTSLUtil.isNullString(loyaltyVO.getMessage())){
									msg=loyaltyVO.getMessage();				
									msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING1, loyaltyVO.getSetName());
									msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING2, BTSLUtil.getDateStringFromDate(loyaltyVO.getFromDate()));
									msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING3, BTSLUtil.getDateStringFromDate(loyaltyVO.getToDate()));
									msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING7, BTSLUtil.getMessage(loyaltyVO.getLocale(),loyaltyVO.getProductCode()));
									msg = msg.replace(PretupsI.LMS_MSG_SUBSTRING8, BTSLUtil.getMessage(loyaltyVO.getLocale(),loyaltyVO.getServiceType()));
									pushMessage=new PushMessage(loyaltyVO.getMsisdn(),msg,"","",loyaltyVO.getLocale());
									pushMessage.push();
								}

							}
							else
							{
								String messageRED=null;               	
								String arr[]= {String.valueOf(loyaltyVO.getSetName()),BTSLUtil.getDateStringFromDate(loyaltyVO.getFromDate()),BTSLUtil.getDateStringFromDate(loyaltyVO.getToDate()),String.valueOf(loyaltyVO.getProductCode()),loyaltyVO.getServiceType()};

								messageRED=BTSLUtil.getMessage(loyaltyVO.getLocale(),PretupsErrorCodesI.PROMOTION_MESSAGE_NOTIFICATION_TXN,arr);
								pushMessage=new PushMessage(loyaltyVO.getMsisdn(),messageRED,null,null,loyaltyVO.getLocale());
								pushMessage.push();
							}

						}
					}catch (Exception ex) {
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
                throw new BTSLBaseException("RunLMSForTargetCredit", "process", PretupsErrorCodesI.LMS_COULD_NOT_UPDATE_MAX_DONE_DATE);
            }

        } catch (Exception ex) {
            logger.errorTrace(methodName, ex);
        } finally {
            try {
                if (statusOk) {
                    if (markProcessStatusAsComplete(con, ProcessI.LMS_PROMO_MSG) == 1) {
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
					mcomCon.close("LMSPromotionProcess#process");
					mcomCon = null;
				}
            } catch (Exception ex) {
                logger.errorTrace(methodName, ex);
                System.out.println("Exception while closing statement in LMSPromotionProcess method ");
            }
        }

    }

    ArrayList loadAssociatedUsersAndProfiles(Connection p_con) {
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
        final Connection con = null;
        LoyaltyVO loyaltyVO = null;
        Locale locale = null;
        ArrayList userList = null;
        final String defaultLanguage = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE));
        final String defaultCountry = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));

        LMSPromotionProcessQry lmsPromotionProcessQry = (LMSPromotionProcessQry)ObjectProducer.getObject(QueryConstants.LMS_PROMOTIONAL_PROCESS_QRY, QueryConstants.QUERY_PRODUCER);
		String selectQuery=lmsPromotionProcessQry.loadAssociatedUsersAndProfilesQry();
		if(logger.isDebugEnabled()){
			logger.debug(methodName ,"SQL Query :"+selectQuery);
		}
		

		StringBuffer strBuff3 = new StringBuffer();
		strBuff3.append("SELECT uop.target, uop.detail_id FROM user_oth_profiles uop WHERE uop.set_id =?  AND uop.detail_id=? AND uop.USER_ID=? AND uop.VERSION=? AND uop.PRODUCT_CODE=? ");
		String selectQuery1 = strBuff3.toString();
		if(logger.isDebugEnabled()){
			logger.debug(methodName ,"SQL Query :"+selectQuery1);
		}

		StringBuffer strBuff2 = new StringBuffer();
		strBuff2.append("SELECT message_code,message1,message2 from MESSAGES_MASTER where message_code=? ");
		String selectQuery2 = strBuff2.toString();
		if(logger.isDebugEnabled()){
			logger.debug(methodName ,"SQL Query :"+selectQuery2);
		}


		try
		{
			userList=new ArrayList();
			pstmtSelect =p_con.prepareStatement(selectQuery);
			pstmtSelect1 =p_con.prepareStatement(selectQuery1);
			pstmtSelect2 = p_con.prepareStatement(selectQuery2);
			int index=1;
			pstmtSelect.setString(index,PretupsI.NORMAL);
			index++;
			pstmtSelect.setString(index,PretupsI.OPT_IN);
			index++;
			pstmtSelect.setString(index,PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT);
			index++;
			pstmtSelect.setString(index,PretupsI.LMS_PROMOTION_TYPE_STOCK);
			index++;
			pstmtSelect.setString(index,PretupsI.STATUS_ACTIVE);
			index++;
			pstmtSelect.setString(index,PretupsI.USER_STATUS_ACTIVE);
			index++;
			pstmtSelect.setString(index,PretupsI.NO);
			index++;
			pstmtSelect.setString(index,PretupsI.STATUS_ACTIVE);
			index++;
			pstmtSelect.setString(index,PretupsI.STATUS_ACTIVE);
			rs = pstmtSelect.executeQuery();
			while (rs.next())
			{
				loyaltyVO= new LoyaltyVO();
				loyaltyVO.setSetId(rs.getString("set_id"));
				loyaltyVO.setUserid(rs.getString("user_id"));
				loyaltyVO.setSetName(rs.getString("set_name"));
				loyaltyVO.setMsisdn(rs.getString("msisdn"));
				loyaltyVO.setDetailId(rs.getString("detail_id"));
				loyaltyVO.setPeriodId(rs.getString("period_id"));
				loyaltyVO.setVersion(rs.getString("VERSION"));
				loyaltyVO.setProductCode(rs.getString("PRODUCT_CODE"));
				loyaltyVO.setServiceType(rs.getString("service_code"));
				//Handling of OPT IN/OPT OUT Message
				loyaltyVO.setOptInOutEnabled(rs.getString("OPT_IN_OUT_ENABLED"));
				loyaltyVO.setOptInOutStatus(rs.getString("OPT_IN_OUT_STATUS"));
				
				if(("N").equals(rs.getString("REF_BASED_ALLOWED"))){
					loyaltyVO.setEndRange(rs.getString("end_range"));
				}
				else
				{
					pstmtSelect1.clearParameters();
					pstmtSelect1.setString(1,loyaltyVO.getSetId());
					pstmtSelect1.setString(2,loyaltyVO.getDetailId());
					pstmtSelect1.setString(3,loyaltyVO.getUserid());
					pstmtSelect1.setString(4,loyaltyVO.getVersion());
					pstmtSelect1.setString(5,loyaltyVO.getProductCode());
					rs1 = pstmtSelect1.executeQuery();
					while (rs1.next()){
						try
						{
							loyaltyVO.setEndRange(rs1.getString("target"));
						}catch(Exception e){logger.error(methodName,"Exception:be="+e);}
					}
					rs1=null;
				}
				loyaltyVO.setFromDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
				loyaltyVO.setToDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_to")));
				locale=new Locale(rs.getString("ph_language"),rs.getString("country"));
				loyaltyVO.setMessageConfEnabled(rs.getString("MESSAGE_MANAGEMENT_ENABLED"));
				loyaltyVO.setDetailType(rs.getString("detail_type"));
				if(logger.isDebugEnabled()){
					logger.debug(methodName ,"loyaltyVO.getMessageConfEnabled():"+loyaltyVO.getMessageConfEnabled()+" , loyaltyVO.getUserid()="+loyaltyVO.getUserid());
				}
				boolean isDuplicateUserNotFound = true;
				if(("Y").equals(loyaltyVO.getMessageConfEnabled())){
					String Message_code=null;
					if(("VOLUME").equals(loyaltyVO.getDetailType())){
						Message_code= PretupsI.WEL_MESSAGE+"_"+loyaltyVO.getSetId();
					} else if(("TRANS").equals(loyaltyVO.getDetailType())){
						Message_code= PretupsI.TRA_WEL_MESSAGE+"_"+loyaltyVO.getSetId();
					}
					pstmtSelect2.clearParameters();
					pstmtSelect2.setString(1,Message_code);
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
							}
							else if (("TRANS").equals(loyaltyVO.getDetailType())) {
								if (!isDuplicateEntryOfTxnProfile(userList, loyaltyVO)) {
									if (!(defaultLanguage.equalsIgnoreCase(rs.getString("ph_language")) && defaultCountry.equalsIgnoreCase(rs.getString("country")))) {
										loyaltyVO.setMessage(rs2.getString("message2"));
									} else {
										loyaltyVO.setMessage(rs2.getString("message1"));
									}
									isDuplicateUserNotFound = false;
								}
							}
						}
						catch (Exception e) {
							logger.error(methodName, "Exception:be=" + e);
						}
					}
					if (isDuplicateUserNotFound) {
						if (("VOLUME").equals(loyaltyVO.getDetailType())) {
							if (!isDuplicateEntryOfTarProfile(userList, loyaltyVO)) {
								locale = new Locale(rs.getString("ph_language"), rs.getString("country"));
								if (!(defaultLanguage.equalsIgnoreCase(rs.getString("ph_language")) && defaultCountry.equalsIgnoreCase(rs.getString("country")))) {
									loyaltyVO.setMessage(BTSLUtil.getMessage(locale, PretupsI.WEL_MESSAGE_LANG2));
								}
								else {
									loyaltyVO.setMessage(BTSLUtil.getMessage(locale, PretupsI.WEL_MESSAGE_LANG1));
								}
								isDuplicateUserNotFound = false;
							}
						}
						else {
							if(!isDuplicateEntryOfTxnProfile(userList,loyaltyVO))
							{
								locale=new Locale(rs.getString("ph_language"),rs.getString("country"));
								if(!(defaultLanguage.equalsIgnoreCase(rs.getString("ph_language")) && defaultCountry.equalsIgnoreCase(rs.getString("country")) )){
									loyaltyVO.setMessage(BTSLUtil.getMessage(locale, PretupsI.TRA_WEL_MSG_LANG2));
								}
								else{
									loyaltyVO.setMessage(BTSLUtil.getMessage(locale, PretupsI.TRA_WEL_MSG_LANG1));
								}
								isDuplicateUserNotFound = false;								
							}
						}
					}
					loyaltyVO.setLocale(locale);
				}
				else {
					try {
						if (("VOLUME").equals(loyaltyVO.getDetailType())) {
							if (!isDuplicateEntryOfTarProfile(userList, loyaltyVO)) {
								locale=new Locale(rs.getString("ph_language"),rs.getString("country"));
								if(!(defaultLanguage.equalsIgnoreCase(rs.getString("ph_language")) && defaultCountry.equalsIgnoreCase(rs.getString("country")) )){
									loyaltyVO.setMessage(BTSLUtil.getMessage(locale, PretupsI.WEL_MESSAGE_LANG2));
								}
								else{
									loyaltyVO.setMessage(BTSLUtil.getMessage(locale, PretupsI.WEL_MESSAGE_LANG1));
								}
								isDuplicateUserNotFound = false;								
							}
						}
						else {
							if (!isDuplicateEntryOfTxnProfile(userList, loyaltyVO)) {
								locale = new Locale(rs.getString("ph_language"), rs.getString("country"));
								if (!(defaultLanguage.equalsIgnoreCase(rs.getString("ph_language")) && defaultCountry.equalsIgnoreCase(rs.getString("country")))) {
									loyaltyVO.setMessage(BTSLUtil.getMessage(locale, PretupsI.TRA_WEL_MSG_LANG2));
								}
								else {
									loyaltyVO.setMessage(BTSLUtil.getMessage(locale, PretupsI.TRA_WEL_MSG_LANG1));
								}
								isDuplicateUserNotFound = false;
							}
						}
					}
					catch(Exception e){
						logger.errorTrace(methodName,e);
					}
					loyaltyVO.setLocale(locale);
				}
				if (!isDuplicateUserNotFound) {
					userList.add(loyaltyVO);
					if (logger.isDebugEnabled()) {
						logger.debug(methodName, "Add :: loyaltyVO.getMessageConfEnabled():" + loyaltyVO.getMessageConfEnabled() + " , loyaltyVO.getUserid()=" + loyaltyVO.getUserid());
					}
				}
				isDuplicateUserNotFound = true;
			}
		}
		catch (SQLException sqe)
		{
			logger.error(methodName, "SQLException : " + sqe);
			logger.errorTrace(methodName,sqe);
		} 
		catch (Exception ex)
		{
			logger.error("", "Exception : " + ex);
			logger.errorTrace(methodName,ex);

		}
		finally
		{
			try {
				if (rs != null) {
					rs.close();
				}
			}
			catch (Exception e) {
				logger.errorTrace(methodName, e);
			}
			try {
				if (rs1 != null) {
					rs1.close();
				}
			}
			catch (Exception e) {
				logger.errorTrace(methodName, e);
			}
			try {
				if (rs2 != null) {
					rs2.close();
				}
			}
			catch (Exception e) {
				logger.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelect != null) {
					pstmtSelect.close();
				}
			}
			catch (Exception e) {
				logger.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelect1 != null) {
					pstmtSelect1.close();
				}
			}
			catch (Exception e) {
				logger.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelect2 != null) {
					pstmtSelect2.close();
				}
			}
			catch (Exception e) {
				logger.errorTrace(methodName, e);
			}
			if (logger.isDebugEnabled()) {
				logger.debug(methodName, "Exiting: userServicesList size=" + userList.size());
			}
		}
		return userList;
	}

    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) {
        final String methodName = "markProcessStatusAsComplete";
        if (logger.isDebugEnabled()) {
            logger.debug("markProcessStatusAsComplete", " Entered:  p_processId:" + p_processId);
        }
        int updateCount = 0;
        Date currentDate = new Date();
        final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        processStatusVO.setProcessID(p_processId);
        processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        processStatusVO.setStartDate(currentDate);
        processStatusVO.setExecutedOn(currentDate);
        // change in db executed_upto (datecount)
        currentDate = BTSLUtil.addDaysInUtilDate(currentDate, -1);
        processStatusVO.setExecutedUpto(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, processStatusVO);
        } catch (Exception e) {
            logger.errorTrace(methodName, e);
            if (logger.isDebugEnabled()) {
                logger.debug("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            }
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
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
                    if (loyaltyVOTemp.getUserid().equalsIgnoreCase(loyaltyVO.getUserid()) && loyaltyVOTemp.getMsisdn().equalsIgnoreCase(loyaltyVO.getMsisdn()) && loyaltyVOTemp.getVersion().equalsIgnoreCase(loyaltyVO.getVersion()) && loyaltyVOTemp.getPeriodId().equalsIgnoreCase(loyaltyVO.getPeriodId())  && loyaltyVOTemp.getProductCode().equalsIgnoreCase(loyaltyVO.getProductCode()) && loyaltyVOTemp.getServiceType().equalsIgnoreCase(loyaltyVO.getServiceType())) {
                    	isFound = true;
                    	if (!(isFound && loyaltyVOTemp.getEndRange().equalsIgnoreCase(loyaltyVO.getEndRange())))	{
                    		loyaltyVOTemp.setEndRange(loyaltyVOTemp.getEndRange()+","+loyaltyVO.getEndRange());
                    		isFound = true;
                    	}
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
