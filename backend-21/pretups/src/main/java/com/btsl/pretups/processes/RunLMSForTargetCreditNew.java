/**
 * @(#)RunLMSForTargetCreditNew.java
 * Copyright(c) 2016, Bharti Telesoft Ltd.
 * All Rights Reserved
 *
 * <Process to credit Air-Time to the users who achieved their target  >
 *-------------------------------------------------------------------------------------------------
 * Author                        Date            History
 *-------------------------------------------------------------------------------------------------
 * Diwakar				  26 Feb,2016      Initial Creation

 *-------------------------------------------------------------------------------------------------
 */

package com.btsl.pretups.processes;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.SecureRandom;
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
import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.loadcontroller.LoadControllerCache;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.RequestGatewayVO;
import com.btsl.pretups.logging.LoyaltyPointsLog;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyPointsRedemptionVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ActivationBonusVO;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.CryptoUtil;
import com.ibm.icu.util.Calendar;

public class RunLMSForTargetCreditNew {

	private static final Log _logger = LogFactory.getLog(RunLMSForTargetCreditNew.class.getName());
	private static ProcessBL _processBL=null;
	private static ProcessStatusVO _processStatusVO =null;
	private static ProcessStatusVO _processStatusMISVO =null;
	private static PreparedStatement _saveBonusStmt=null;
	private static PreparedStatement _checkUserExistStmt=null;
	private static PreparedStatement _updateBonusStmt=null;
	private static PreparedStatement _checkUserExistLastDateStmt=null;
	private static final float EPSILON=0.0000001f;

	public static void main(String[] args) {
		final String METHOD_NAME = "main";
		try	{
			if(args.length!=2) {
				System.out.println("Usage : RunLMSForTargetCreditNew [Constants file] [LogConfig file] [Upload File Path]");
				return;
			}
			File constantsFile = Constants.validateFilePath(args[0]);
			if(!constantsFile.exists())	{
				System.out.println(" Constants File Not Found .............");
				return;
			}
			File logconfigFile = Constants.validateFilePath(args[1]);
			if(!logconfigFile.exists())	{
				System.out.println(" Logconfig File Not Found .............");
				return;
			}
			ConfigServlet.loadProcessCache(constantsFile.toString(),logconfigFile.toString());
			LookupsCache.loadLookAtStartup();
		} catch(Exception ex) {
			_logger.error(METHOD_NAME, "Error in Loading Configuration files ...........................: "+ex);
			_logger.errorTrace(METHOD_NAME,ex);
			ConfigServlet.destroyProcessCache();
			return;
		}
		
		try	{
			RunLMSForTargetCreditNew runLMSForTargetCredit= new RunLMSForTargetCreditNew();
			runLMSForTargetCredit.process();
		} catch(BTSLBaseException be) {
			_logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
			_logger.errorTrace(METHOD_NAME,be);
		} catch(Exception e) {
			_logger.error(METHOD_NAME, "Exception : " + e.getMessage());
			_logger.errorTrace(METHOD_NAME,e);
		} finally {
			if(_logger.isDebugEnabled()) {
				_logger.info(METHOD_NAME," Exiting");
			}
			ConfigServlet.destroyProcessCache();
		}

	}

	public void process() throws BTSLBaseException, SQLException {
		final String METHOD_NAME = "RunLMSForTargetCreditNew";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME," Entered: ");
		}
		Connection con=null;
		MComConnectionI mcomCon = null;
		boolean statusOk=false;
		Date processedUpto=null;
		Date processedUptoMIS=null;
		Date currentDate = new Date();
		Date dateCount = null;
		//Date sumTxnDate = null;
		int count=0;
		ArrayList userProfileDetailListRef =null;
		ArrayList userProfileDetailListNonRef =null;
		LoyaltyPointsRedemptionVO redemptionVO=null;
		String reportTo = null;
		String prevDateStr = null;
		int beforeInterval=0;
		try	{
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			_processBL = new ProcessBL();
			//Process should not execute until the MIS has not executed successfully for previous day
			_processStatusMISVO=_processBL.checkProcessUnderProcess(con,ProcessI.C2SMIS);
			processedUptoMIS=_processStatusMISVO.getExecutedUpto();				
			if(processedUptoMIS!=null) {
				con.rollback();
				//Process should not execute until the MIS has not executed successfully for previous day
				Calendar cal4CurrentDate = BTSLDateUtil.getInstance();
				Calendar cal14MisExecutedUpTo = BTSLDateUtil.getInstance();
				cal4CurrentDate.add(Calendar.DAY_OF_MONTH, -1);
				Date currentDate1=cal4CurrentDate.getTime(); //Current Date
				cal14MisExecutedUpTo.setTime(processedUptoMIS);
				Calendar cal24CurrentDate =BTSLDateUtil.getCalendar(cal4CurrentDate);
				Calendar cal34MisExecutedUpTo = BTSLDateUtil.getCalendar(cal14MisExecutedUpTo);
			    if(_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME,"(currentDate - 1) = "+currentDate1 +" processedUptoMIS = "+processedUptoMIS);
				}
				if(cal34MisExecutedUpTo.compareTo(cal24CurrentDate) < 0) {
					if(_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME, "The MIS has not been executed for the previous day.");
					}
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"RunLMSForTargetCreditNew[process]","","","","The MIS has not been executed for the previous day.");
					throw new BTSLBaseException(METHOD_NAME,METHOD_NAME,PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
				}				
				_processStatusVO=_processBL.checkProcessUnderProcess(con,ProcessI.LMS_TARGET_CREDIT);
				statusOk=_processStatusVO.isStatusOkBool();
				beforeInterval=BTSLUtil.parseLongToInt(_processStatusVO.getBeforeInterval()/(60*24));
				//check process status.	
				if(statusOk) {
					processedUpto=_processStatusVO.getExecutedUpto();
					if(processedUpto!=null)	{
						processedUptoMIS=BTSLUtil.addDaysInUtilDate(processedUptoMIS,1);//for comparison.
						//Calendar cal = BTSLDateUtil.getInstance();
						currentDate=new Date(); //Current Date
						try	{
							SimpleDateFormat sdf = new SimpleDateFormat (PretupsI.DATE_FORMAT_DDMMYYYY);
							sdf.setLenient(false); // this is required else it will convert
							prevDateStr=sdf.format(processedUpto);//Last PROCESS Done Date +1
							currentDate = sdf.parse(sdf.format(currentDate));

						} catch(Exception e) {
							prevDateStr="";
							 _logger.errorTrace(METHOD_NAME,e);
							throw new BTSLBaseException("Not able to convert date to String");
						}

						//Process will be exceuted from the start till to date -1
						if(_logger.isDebugEnabled()) {
							_logger.debug("RunLMSForTargetCreditNew[process]","From date="+prevDateStr+" To Date(currentDate-interval)="+reportTo+" processedUpto.compareTo(currentDate-interval)="+processedUpto.compareTo(currentDate));
						}

						//If process has already run for the last day, then you can't run it again ;)
						Date checkProcess=processedUpto;
						if(BTSLUtil.addDaysInUtilDate(checkProcess,1).compareTo(currentDate)>=0) {
							EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"RunLMSForTargetCreditNew[main]","","","","LMS Target Credit Controller has already been executed for the date="+String.valueOf(processedUpto));
							return;
						}
					}
				} else	{	
					throw new BTSLBaseException(METHOD_NAME,METHOD_NAME,PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
				}

			} else	{
				throw new BTSLBaseException(METHOD_NAME,METHOD_NAME,PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
			}
			
			if(!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_VOL_CREDIT_LOYAL_PTS))).booleanValue())	{
				String instanceID=null;
				instanceID=Constants.getProperty("INSTANCE_ID");
				if(BTSLUtil.isNullString(instanceID))	{
					_logger.error(METHOD_NAME," Not able to get the instance ID for the network=");
					throw new BTSLBaseException(METHOD_NAME,"initaiateC2CTransferRequest","lms.point.redemption.form.error.unableto.initiateo2c","");
				}
			}
			makeQuery(con);
			//Loading the details here.
			//one more loop here to handle skipped date(s)- Note that: currentDate is (currentDate-1) 
			if(_logger.isDebugEnabled()) {
				_logger.error(METHOD_NAME," BTSLUtil.getSQLDateFromUtilDate(processedUpto)="+BTSLUtil.getSQLDateFromUtilDate(processedUpto)+" ,currentDate="+currentDate+" , compare= "+BTSLUtil.getSQLDateFromUtilDate(processedUpto).before(currentDate));				
			}
			for(dateCount=BTSLUtil.addDaysInUtilDate(BTSLUtil.getSQLDateFromUtilDate(processedUpto),1);dateCount.before(currentDate);dateCount=BTSLUtil.addDaysInUtilDate(dateCount,1)) {
				if(_logger.isDebugEnabled()) {
					_logger.error(METHOD_NAME," dateCount="+dateCount+" ,currentDate="+dateCount.before(currentDate)+", dateCount.before(processedUptoMIS)="+dateCount.before(processedUptoMIS));				
				}
				SimpleDateFormat sdf = new SimpleDateFormat (PretupsI.TIMESTAMP_DATESPACEHHMMSS);
				sdf.setLenient(false); // this is required else it will convert 
				reportTo=sdf.format(dateCount); //Current Date
				//we check whether MIS has been executed or not. If not, don't proceed further.
				if(dateCount.before(processedUptoMIS)) {					
					userProfileDetailListRef = loadRefTargetProfile(con, dateCount);
					userProfileDetailListNonRef = loadNonRefTargetProfiles(con, dateCount);
					redemptionVO = new LoyaltyPointsRedemptionVO();
					try {
						//Bonus credit for Reference based profile
						bonusCredit4ReferenceBasedProfile(con,userProfileDetailListRef,dateCount,count);
						//Bonus credit for Non-Reference based profile
						bonusCredit4NonReferenceBasedProfile(con,userProfileDetailListNonRef,dateCount,count);
					} catch (RuntimeException e) {
						_logger.errorTrace(METHOD_NAME, e);
					}
				} else {
					dateCount=BTSLUtil.addDaysInUtilDate(dateCount,-1);
					if(_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME, "Process has been executed upto = "+dateCount);
					}
					throw new BTSLBaseException(METHOD_NAME,METHOD_NAME,PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
				}
			}
			//change in db executed_upto (datecount)
			dateCount=BTSLUtil.addDaysInUtilDate(dateCount,-1);
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Process has been executed upto = "+dateCount);
			}
			_processStatusVO.setExecutedUpto(dateCount);
			_processStatusVO.setExecutedOn(new Date());
			ProcessStatusDAO processStatusDAO=new ProcessStatusDAO();
			int maxDoneDateUpdateCount=processStatusDAO.updateProcessDetail(con,_processStatusVO);
			if(maxDoneDateUpdateCount>0) {
				con.commit();
			} else	{
				con.rollback();
				redemptionVO.setErrorCode(null);
				throw new BTSLBaseException(METHOD_NAME,METHOD_NAME,PretupsErrorCodesI.LMS_COULD_NOT_UPDATE_MAX_DONE_DATE);
			}
		} catch(BTSLBaseException ex) {
			//end daily process loop here
			con.rollback();
			_logger.errorTrace(METHOD_NAME,ex);
			if(dateCount!=null)	{
				_processStatusVO.setExecutedUpto(dateCount);
				_processStatusVO.setExecutedOn(new Date());
				ProcessStatusDAO processStatusDAO=new ProcessStatusDAO();
				int maxDoneDateUpdateCount=processStatusDAO.updateProcessDetail(con,_processStatusVO);
				if(maxDoneDateUpdateCount>0) {
					con.commit();
				} else	{
					con.rollback();
				}
			}
			_logger.error(METHOD_NAME,"exit");
		} catch(Exception e) {
			con.rollback();
			_logger.errorTrace(METHOD_NAME,e);
		} finally {
			try	{
				if(_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME," loyalty points: ");
				}
				if(statusOk) {
					if(markProcessStatusAsComplete(con,ProcessI.LMS_TARGET_CREDIT)==1) {
						try {
							con.commit();
						} catch(Exception e) {
							_logger.errorTrace(METHOD_NAME,e);
						}
					} else {
						try {
							con.rollback();
						} catch(Exception e) {
							_logger.errorTrace(METHOD_NAME,e);
						}
					}
				}
				if (mcomCon != null) {
					mcomCon.close("RunLMSForTargetCreditNew#process");
					mcomCon = null;
				}
			} catch(Exception ex) {
				 _logger.error(METHOD_NAME,"Exception while closing statement in LMSPromotionProcess method ");
				 _logger.errorTrace(METHOD_NAME,ex);
			} finally {
				if(_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME," Count of given promotions: "+count);
				}
				if(_checkUserExistStmt!=null) {
					try {
						_checkUserExistStmt.close();
					} catch (Exception ex) {
						_logger.errorTrace(METHOD_NAME,ex);
					}
				}
				if(_saveBonusStmt!=null) {
					try {
						_saveBonusStmt.close();
					} catch (Exception ex) {
						_logger.errorTrace(METHOD_NAME,ex);
					}
				}
				if(_updateBonusStmt!=null) {
					try {
						_updateBonusStmt.close();
					} catch (Exception ex) {
						_logger.errorTrace(METHOD_NAME,ex);
					}
				}
				if (mcomCon != null) {
					mcomCon.close("RunLMSForTargetCreditNew#process");
					mcomCon = null;
				}
				if(_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME , "Exiting");
				}
			}
		}

	}

	private void bonusCredit4ReferenceBasedProfile(	Connection con, ArrayList userProfileDetailListRef, Date dateCount, int numberOfUsersBonusCreditedCountt) throws BTSLBaseException, SQLException {
		String METHOD_NAME="bonusCredit4ReferenceBasedProfile";
		LoyaltyPointsRedemptionVO redemptionVO = null;
		Date createDate = null; 
		LoyaltyVO loyaltyVO = null;
		String txnId = null;
		String [] focResponse=null;
		String [] c2cResponse=null;
		String c2cTxnId =null;
		String focTxnId=null;
		boolean c2cFlag=false;
		if(userProfileDetailListRef.size()!=0) {				
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Number of users for reference based profile = "+userProfileDetailListRef.size());
			}
			int profileDetailListSizes=userProfileDetailListRef.size();
			for (int k=0;k<profileDetailListSizes;k++)	{						
				redemptionVO=(LoyaltyPointsRedemptionVO)userProfileDetailListRef.get(k);
				if(_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "Target Credit for redemptionVO = "+redemptionVO.getUserID()+" , K = "+k);
				}							
				redemptionVO.setCurrentProcessDate(dateCount);
				redemptionVO.setSumTxnsDate(dateCount);
				//Handling of OPT IN/OPT OUT as design changed 
				if(PretupsI.YES.equalsIgnoreCase(redemptionVO.getOptInOutEnabled()) && PretupsI.NORMAL.equalsIgnoreCase(redemptionVO.getOptInOutStatus())) {
					if(_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME," Escaping the lms bonus credit for the  MSISDN [ "+ redemptionVO.getMsisdn()+" ] with associated profile [ "+redemptionVO.getSetId()+" ] because of no acknowledgement received for optin/optout.");
					}
					continue;
				}
				// For LMS Txn ID- different for different days
				if(k==0)  {
					createDate = new Date();
					loyaltyVO = new LoyaltyVO();
					loyaltyVO.setNetworkCode(redemptionVO.getNetworkID());
					loyaltyVO.setCreatedOn(createDate);
					PretupsBL.generateLMSTransferID(loyaltyVO);
					txnId=loyaltyVO.getLmstxnid();

				}
				redemptionVO.setLmsTxnId(txnId);
				//Getting the sum of the Txns here- On daily or weekly or monthly basis.
				redemptionVO = loadCummulativeTxnForUsers(con, redemptionVO);
				if(redemptionVO.getBonusCreditDateReached()) {
					if(!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_PCT_POINTS_CALCULATION))).booleanValue()) {
						if(redemptionVO.getPointsType().equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
							double points=Double.parseDouble(PretupsBL.getDisplayAmount(redemptionVO.getSumAmount()*redemptionVO.getTotalCrLoyaltyPoint()))/100;
							redemptionVO.setTotalCrLoyaltyPoint(BTSLUtil.parseDoubleToLong( PretupsBL.Round(points, 0)));					
						}							
	                }	
					// *** LOGIC FOR POINTS or airtime DISTRIBUTION STARTS *** (In case of Ref)
					if(_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME, "USER_ID = "+redemptionVO.getUserID()+" , redemptionVO.getPeriodId() = "+redemptionVO.getPeriodId()+" , redemptionVO.getServiceCode()="+redemptionVO.getServiceCode()+", redemptionVO.getSumAmount()="+redemptionVO.getSumAmount()+" , redemptionVO.getTarget()="+redemptionVO.getTarget()	);
					}
					if( redemptionVO.getSumAmount() >= redemptionVO.getTarget() ) {
						//Distribution of Loyalty Points		
						c2cFlag=false;
						if(!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_VOL_CREDIT_LOYAL_PTS))).booleanValue()) {
							calO2CAndC2CContribution(redemptionVO);
						}
						try	{
							if(_logger.isDebugEnabled()) {
								_logger.debug(METHOD_NAME, "redemptionVO.getUserID() = "+redemptionVO.getUserID()+" , redemptionVO.getPointsType()="+redemptionVO.getPointsType());
							}
							if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_PCT_POINTS_CALCULATION))).booleanValue()) {
								if(redemptionVO.getPointsType().equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
									double points=Double.parseDouble(PretupsBL.getDisplayAmount((redemptionVO.getSumAmount()-redemptionVO.getTarget())*redemptionVO.getTotalCrLoyaltyPoint()))/100;
									if(Math.abs(points-0)<EPSILON) {
										if(_logger.isDebugEnabled()) {
											_logger.debug(METHOD_NAME, "USER_ID= "+redemptionVO.getUserID()+" , POINTS_TYPE = PCT , points = "+points);
										}
										continue;
									} else {
										redemptionVO.setTotalCrLoyaltyPoint(BTSLUtil.parseDoubleToLong( PretupsBL.Round(points, 0)));
									}
								}
							}
							if(!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_VOL_CREDIT_LOYAL_PTS))).booleanValue())	{
								focTxnId =  this.initaiateFocRedemptionRequest(redemptionVO);
								focResponse =focTxnId.split("@");
								redemptionVO.setReferenceNo(focResponse[0]);
								redemptionVO.setTxnStatus(focResponse[1]);
								try {	
									SettleLoyaltyPoints(con, redemptionVO, c2cFlag);
									redemptionVO.setSumAmount(0L);
								} catch(Exception ex) {
									_logger.errorTrace(METHOD_NAME,ex);
								}
							} else {
								try	{
									if(redemptionVO.getTotalCrLoyaltyPoint()>0) {
										DistributeLoyaltyPoints(con, redemptionVO, c2cFlag,dateCount);
									}
									redemptionVO.setSumAmount(0L);	
									numberOfUsersBonusCreditedCountt++;
								} catch(Exception ex) {
									_logger.errorTrace(METHOD_NAME,ex);
								}
							}
	
							if(redemptionVO.getParentContribution()>0 && !redemptionVO.getParentID().equalsIgnoreCase(PretupsI.ROOT_PARENT_ID))	{
								try	{
									//if pref=false
									if(!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_VOL_CREDIT_LOYAL_PTS))).booleanValue()) {
										c2cTxnId =  this.initaiateC2CTransferRequest(redemptionVO);
									}
									c2cFlag= true;
								} catch(BTSLBaseException be) {
									con.rollback();
									_logger.errorTrace(METHOD_NAME,be);
	
								}
							}
	
							if(c2cFlag)	{
								//if pref=false
								if(!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_VOL_CREDIT_LOYAL_PTS))).booleanValue())	{
									c2cResponse =c2cTxnId.split("@");
									redemptionVO.setReferenceNo(c2cResponse[0]);
									redemptionVO.setTxnStatus(c2cResponse[1]);
									try	{	
										SettleLoyaltyPoints(con, redemptionVO, c2cFlag);
										redemptionVO.setSumAmount(0L);
									} catch(Exception ex) {
										_logger.errorTrace(METHOD_NAME,ex);
									}
								}
							}
							if(_logger.isDebugEnabled()) {
								_logger.debug(METHOD_NAME, "LMS Bonus has been credited to user = "+redemptionVO.getUserID()+" for service = "+redemptionVO.getServiceCode()+" and period type = "+redemptionVO.getPeriodId()+" based on it's heigher ranges "+redemptionVO.getTarget());
							}
							
							if(k+1 <userProfileDetailListRef.size()) {
								LoyaltyPointsRedemptionVO redemptionVO3 =null;
								redemptionVO3=(LoyaltyPointsRedemptionVO)userProfileDetailListRef.get(k+1);
								while(redemptionVO.getUserID().equals(redemptionVO3.getUserID()) && redemptionVO.getPeriodId().equalsIgnoreCase(redemptionVO3.getPeriodId())
										&& redemptionVO.getServiceCode().equalsIgnoreCase(redemptionVO3.getServiceCode()) ) {
									if(_logger.isDebugEnabled()) {
										_logger.debug(METHOD_NAME, "There is no need to credit bonus for user = "+redemptionVO.getUserID()+" for service = "+redemptionVO.getServiceCode()+" and period type = "+redemptionVO.getPeriodId()+" corresponding to lower ranges = "+redemptionVO3.getTarget()+" as bonus has been credited based on it's heigher ranges "+redemptionVO.getTarget());
									}
								
									k =k+1; // INCREMENTING THE LOOP
									if(k+1 < userProfileDetailListRef.size()) {
										redemptionVO3=(LoyaltyPointsRedemptionVO)userProfileDetailListRef.get(k+1);
									} else{
										break;
									}
								}
							}											
							
						} catch(BTSLBaseException be) {
							con.rollback();
							_logger.errorTrace(METHOD_NAME,be);
	
						}
					} else {
						if(_logger.isDebugEnabled()) {
							_logger.debug(METHOD_NAME, "LMS Bonus will not be credited to user = "+redemptionVO.getUserID()+" for service = "+redemptionVO.getServiceCode()+" and period type = "+redemptionVO.getPeriodId()+" corresponding to his target = "+redemptionVO.getTarget()+" as per his transcation amount = "+redemptionVO.getSumAmount()+" is not reached.");
						}
						
					}
				} else {
					if(_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME, "LMS Bonus will not be credited to user = "+redemptionVO.getUserID()+" for service = "+redemptionVO.getServiceCode()+" and period type = "+redemptionVO.getPeriodId()+" his bonus credit date is not reached.");
					}
				}
			}
		}
		
	}
	
	private void bonusCredit4NonReferenceBasedProfile(Connection con, ArrayList userProfileDetailListNonRef, Date dateCount, int numberOfUsersBonusCreditedCountt) throws BTSLBaseException, SQLException {
		String METHOD_NAME = "bonusCredit4NonReferenceBasedProfile";
		LoyaltyPointsRedemptionVO redemptionVO = null;
		Date createDate = null; 
		LoyaltyVO loyaltyVO = null;
		String txnId = null;
		String [] focResponse=null;
		String [] c2cResponse=null;
		String c2cTxnId =null;
		String focTxnId=null;
		boolean c2cFlag=false;
		//FOR NON-REFERENCED
		if(userProfileDetailListNonRef.size()!=0)	{				

			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Number of users for non-reference based profile = "+userProfileDetailListNonRef.size());
			}
			int profileDetailListNonRefSizes = userProfileDetailListNonRef.size();
			for (int k=0;k< profileDetailListNonRefSizes;k++)	{						
				redemptionVO=(LoyaltyPointsRedemptionVO)userProfileDetailListNonRef.get(k);
				if(_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "Target Credit for redemptionVO = "+redemptionVO.getUserID()+" , K = "+k);
				}
				redemptionVO.setCurrentProcessDate(dateCount);
				redemptionVO.setSumTxnsDate(dateCount);
				//Handling of OPT IN/OPT OUT as design changed
				if(PretupsI.YES.equalsIgnoreCase(redemptionVO.getOptInOutEnabled()) && PretupsI.NORMAL.equalsIgnoreCase(redemptionVO.getOptInOutStatus())) {
					if(_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME," Escaping the lms bonus credit for the  MSISDN [ "+ redemptionVO.getMsisdn()+" ] with associated profile [ "+redemptionVO.getSetId()+" ] because of no acknowledgement received for optin/optout.");
					}
					continue;
				}
				// For LMS Txn ID- different for different days
				if(k==0) {
					if(BTSLUtil.isNullString(txnId)) {
						createDate = new Date();
						loyaltyVO = new LoyaltyVO();
						loyaltyVO.setNetworkCode(redemptionVO.getNetworkID());
						loyaltyVO.setCreatedOn(createDate);
						PretupsBL.generateLMSTransferID(loyaltyVO);
						txnId=loyaltyVO.getLmstxnid();
					}

				}
				redemptionVO.setLmsTxnId(txnId);
				redemptionVO = loadCummulativeTxnForUsers(con, redemptionVO);
				if(redemptionVO.getBonusCreditDateReached()) {
					if(!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_PCT_POINTS_CALCULATION))).booleanValue()) {
						if(redemptionVO.getPointsType().equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
							double points=Double.parseDouble(PretupsBL.getDisplayAmount(redemptionVO.getSumAmount()*redemptionVO.getTotalCrLoyaltyPoint()))/100;
							redemptionVO.setTotalCrLoyaltyPoint(BTSLUtil.parseDoubleToLong( PretupsBL.Round(points, 0)));					
						}
					}
					// *** LOGIC FOR POINTS or airtime DISTRIBUTION STARTS *** (In case of Non-Ref)
					if(_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME, "USER_ID = "+redemptionVO.getUserID()+" , redemptionVO.getPeriodId() = "+redemptionVO.getPeriodId()+" , redemptionVO.getServiceCode()="+redemptionVO.getServiceCode()+" , redemptionVO.getSumAmount()="+redemptionVO.getSumAmount()+" , redemptionVO.getToRange()="+redemptionVO.getToRange());
					}
					if( redemptionVO.getSumAmount() >= redemptionVO.getToRange() ) {
						//	Distribution of Loyalty Points		
						c2cFlag=false;
						if(!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_VOL_CREDIT_LOYAL_PTS))).booleanValue()) {
							calO2CAndC2CContribution(redemptionVO);
						}
						try	{
							if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_PCT_POINTS_CALCULATION))).booleanValue()) {
								if(redemptionVO.getPointsType().equals(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
									double points=Double.parseDouble(PretupsBL.getDisplayAmount((redemptionVO.getSumAmount()-redemptionVO.getToRange())*redemptionVO.getTotalCrLoyaltyPoint()))/100;
									if(Math.abs(points-0)<EPSILON) {
										if(_logger.isDebugEnabled()) {
											_logger.debug(METHOD_NAME, "USER_ID= "+redemptionVO.getUserID()+" , POINTS_TYPE = PCT , points = "+points);
										}
										continue;
									} else {								
										redemptionVO.setTotalCrLoyaltyPoint(BTSLUtil.parseDoubleToLong( PretupsBL.Round(points, 0)));
									}
								}
							}
							if(!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_VOL_CREDIT_LOYAL_PTS))).booleanValue())	{
								focTxnId =  this.initaiateFocRedemptionRequest(redemptionVO);
								focResponse =focTxnId.split("@");
								redemptionVO.setReferenceNo(focResponse[0]);
								redemptionVO.setTxnStatus(focResponse[1]);
								try {	
									SettleLoyaltyPoints(con, redemptionVO, c2cFlag);
									redemptionVO.setSumAmount(0L);
								} catch(Exception ex) {
									_logger.errorTrace(METHOD_NAME,ex);
								}							
							} else {
								try {
									if(redemptionVO.getTotalCrLoyaltyPoint()>0) {
										DistributeLoyaltyPoints(con, redemptionVO, c2cFlag,dateCount);
									}
									redemptionVO.setSumAmount(0L);	
									numberOfUsersBonusCreditedCountt++;
								} catch(Exception ex) {
									_logger.errorTrace(METHOD_NAME,ex);
								}
							}
		
							if(redemptionVO.getParentContribution()>0 && !redemptionVO.getParentID().equalsIgnoreCase(PretupsI.ROOT_PARENT_ID))	{
								try {
									//if pref=false
									if(!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_VOL_CREDIT_LOYAL_PTS))).booleanValue()) {
										c2cTxnId =  this.initaiateC2CTransferRequest(redemptionVO);
									}
									c2cFlag= true;
								} catch(BTSLBaseException be) {
									con.rollback();
									_logger.errorTrace(METHOD_NAME,be);														
								}
							}
		
							if(c2cFlag) {
								//if pref=false
								if(!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_VOL_CREDIT_LOYAL_PTS))).booleanValue()) {
									c2cResponse =c2cTxnId.split("@");
									redemptionVO.setReferenceNo(c2cResponse[0]);
									redemptionVO.setTxnStatus(c2cResponse[1]);
									try {	
										SettleLoyaltyPoints(con, redemptionVO, c2cFlag);
										redemptionVO.setSumAmount(0L);
									} catch(Exception ex) {
										_logger.errorTrace(METHOD_NAME,ex);
									}
								}
							}
							if(_logger.isDebugEnabled()) {
								_logger.debug(METHOD_NAME, "LMS Bonus has been credited to user = "+redemptionVO.getUserID()+" for service = "+redemptionVO.getServiceCode()+" and period type = "+redemptionVO.getPeriodId()+" based on it's heigher ranges "+redemptionVO.getToRange());
							}
							if(k+1 <userProfileDetailListNonRef.size()) {
								LoyaltyPointsRedemptionVO redemptionVO3 =null;
								redemptionVO3=(LoyaltyPointsRedemptionVO)userProfileDetailListNonRef.get(k+1);
								while(redemptionVO.getUserID().equals(redemptionVO3.getUserID()) && redemptionVO.getPeriodId().equalsIgnoreCase(redemptionVO3.getPeriodId())
										&& redemptionVO.getServiceCode().equalsIgnoreCase(redemptionVO3.getServiceCode()) ) {
									if(_logger.isDebugEnabled()) {
										_logger.debug(METHOD_NAME, "There is no need to credit bonus for user = "+redemptionVO.getUserID()+" for service = "+redemptionVO.getServiceCode()+" and period type = "+redemptionVO.getPeriodId()+" corresponding to lower ranges = "+redemptionVO3.getToRange()+" as bonus has been credited based on it's heigher ranges "+redemptionVO.getToRange());
									}
									k =k+1; // INCREMENTING THE LOOP
									if(k+1 < userProfileDetailListNonRef.size()) {
										redemptionVO3=(LoyaltyPointsRedemptionVO)userProfileDetailListNonRef.get(k+1);
									}else{
										break;
									}
								}
							}
						} catch(BTSLBaseException be) {
							con.rollback();
							_logger.errorTrace(METHOD_NAME,be);
						}
					} else {
						if(_logger.isDebugEnabled()) {
							_logger.debug(METHOD_NAME, "LMS Bonus will not be credited to user = "+redemptionVO.getUserID()+" for service = "+redemptionVO.getServiceCode()+" and period type = "+redemptionVO.getPeriodId()+" corresponding to his target = "+redemptionVO.getToRange()+" as per his transcation amount = "+redemptionVO.getSumAmount()+" is not reached.");
						}						
					}
					// *** LOGIC FOR POINTS or airtime DISTRIBUTION ENDS *** (In case of Non-Ref)
				} else {
					if(_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME, "LMS Bonus will not be credited to user = "+redemptionVO.getUserID()+" for service = "+redemptionVO.getServiceCode()+" and period type = "+redemptionVO.getPeriodId()+" his bonus credit date is not reached.");
					}
				}
			}
		}
		
	}

	// Method to load Profile & User Details for active volume type LMS Promotions
	ArrayList loadRefTargetProfile(Connection p_con, Date p_dateCount) {
		final String METHOD_NAME = "loadRefTargetProfile";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME," Entered");
		}
		PreparedStatement pstmtSelect=null;
		ResultSet rs = null;
		LoyaltyPointsRedemptionVO redemptionVO=null;
		ArrayList profileList=null;

		RunLMSForTargetCreditNewQry selectQueryBuffer = (RunLMSForTargetCreditNewQry)
				ObjectProducer.getObject(QueryConstants.RUN_LMS_TARGET_CREDIT_NEW_QRY, QueryConstants.QUERY_PRODUCER);
		String selectQuery=selectQueryBuffer.loadRefTargetProfileQry();
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME ,"SQL Query :"+selectQuery);
		}
		try {
			profileList=new ArrayList();
			int index=1;
			pstmtSelect =p_con.prepareStatement(selectQuery);
			pstmtSelect.setString(index++,PretupsI.PROFILE_VOL);
			pstmtSelect.setString(index++,PretupsI.STATUS_ACTIVE);
			pstmtSelect.setString(index++,PretupsI.LMS_PROFILE_TYPE);
			if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue()) {
				pstmtSelect.setString(index++,PretupsI.YES);
				pstmtSelect.setString(index++,PretupsI.NO);
				pstmtSelect.setString(index++,PretupsI.OPT_IN);
				pstmtSelect.setString(index++,PretupsI.NORMAL);	
			} else {
				pstmtSelect.setString(index++,PretupsI.NO);
				pstmtSelect.setString(index++,PretupsI.NORMAL);
			}
			pstmtSelect.setString(index++,PretupsI.NO);
			pstmtSelect.setString(index++,PretupsI.LMS_PROMOTION_TYPE_STOCK);
			pstmtSelect.setString(index++,PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT);
			pstmtSelect.setDate(index++,BTSLUtil.getSQLDateFromUtilDate(p_dateCount));
			pstmtSelect.setDate(index++,BTSLUtil.getSQLDateFromUtilDate(p_dateCount));
			pstmtSelect.setDate(index++,BTSLUtil.getSQLDateFromUtilDate(p_dateCount));
			rs = pstmtSelect.executeQuery();
			while (rs.next()) {
				redemptionVO= new LoyaltyPointsRedemptionVO();
				redemptionVO.setSetId(rs.getString("set_id"));
				redemptionVO.setPeriodId(rs.getString("period_id"));
				redemptionVO.setServiceCode(rs.getString("service_code"));
				redemptionVO.setPointsType(rs.getString("points_type"));
				redemptionVO.setRefBaseAllowed(rs.getString("ref_based_allowed"));
				redemptionVO.setUserID(rs.getString("user_id"));
				redemptionVO.setModuleType(rs.getString("type"));
				redemptionVO.setMsisdn(rs.getString("msisdn"));
				redemptionVO.setTotalCrLoyaltyPoint(rs.getLong("points"));
				redemptionVO.setLmsTarget(rs.getString("target"));
				redemptionVO.setTarget(rs.getLong("target"));
				redemptionVO.setFromDate(rs.getDate("applicable_from"));
				redemptionVO.setPromoStartDate(String.valueOf(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from"))));
				redemptionVO.setParentID(rs.getString("parent_id"));
				redemptionVO.setNetworkID(rs.getString("network_code"));
				redemptionVO.setCategoryCode(rs.getString("category_code"));
				redemptionVO.setParentMsisdn(rs.getString("parent_msisdn"));
				redemptionVO.setParentEncryptedPin(rs.getString("parent_sms_pin"));
				redemptionVO.setProductCode(rs.getString("product_code"));
				redemptionVO.setProductShortCode(rs.getString("product_short_code"));
				redemptionVO.setOperatorContribution(rs.getInt("opt_contribution"));
				redemptionVO.setParentContribution(rs.getInt("prt_contribution"));
				redemptionVO.setApplicableToDate(rs.getDate("applicable_to"));
				redemptionVO.setVersion(rs.getString("version"));
				//Handling of OPT IN/OPT OUT as design changed 
				redemptionVO.setOptInOutEnabled(rs.getString("OPT_IN_OUT_ENABLED"));
				redemptionVO.setOptInOutStatus(rs.getString("OPT_IN_OUT_STATUS"));
				profileList.add(redemptionVO);
			}
		} catch (SQLException sqe) {
			_logger.error(METHOD_NAME, "SQLException : " + sqe);
			_logger.errorTrace(METHOD_NAME,sqe);
		} catch (Exception ex) {
			_logger.error("", "Exception : " + ex);
			_logger.errorTrace(METHOD_NAME,ex);

		} finally	{
			try {
				if(rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				_logger.errorTrace(METHOD_NAME,e);
			}
			try {
				if(pstmtSelect != null) {
					pstmtSelect.close();
				}
			} catch (Exception e) {
				_logger.errorTrace(METHOD_NAME,e);
			}
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Exiting: profileList size=" + profileList.size());
			}
		}
		return profileList;
	}
	private RunLMSForTargetCreditNewQry selectQueryBuffer = (RunLMSForTargetCreditNewQry)
			ObjectProducer.getObject(QueryConstants.RUN_LMS_TARGET_CREDIT_NEW_QRY, QueryConstants.QUERY_PRODUCER);
	// Method to load Non Reference based Profile & User Details for active volume type LMS Promotions
	ArrayList loadNonRefTargetProfiles(Connection p_con, Date p_dateCount) {
		final String METHOD_NAME = "loadNonRefTargetProfiles";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME," Entered p_dateCount = "+p_dateCount);
		}
		PreparedStatement pstmtSelect=null;
		ResultSet rs = null;
		LoyaltyPointsRedemptionVO redemptionVO=null;
		ArrayList profileList=null;
		
		String selectQuery=selectQueryBuffer.loadNonRefTargetProfilesQry();
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME ,"SQL Query :"+selectQuery);
		}
		try {
			int index=1;
			profileList=new ArrayList();
			pstmtSelect =p_con.prepareStatement(selectQuery);
			pstmtSelect.setString(index++,PretupsI.PROFILE_VOL);
			pstmtSelect.setString(index++,PretupsI.STATUS_ACTIVE);
			pstmtSelect.setString(index++,PretupsI.LMS_PROFILE_TYPE);
			if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue()) {
				pstmtSelect.setString(index++,PretupsI.YES);
				pstmtSelect.setString(index++,PretupsI.NO);
				pstmtSelect.setString(index++,PretupsI.OPT_IN);
				pstmtSelect.setString(index++,PretupsI.NORMAL);	
			} else {
				pstmtSelect.setString(index++,PretupsI.NO);
				pstmtSelect.setString(index++,PretupsI.NORMAL);
			}
			pstmtSelect.setString(index++,PretupsI.NO);
			pstmtSelect.setString(index++,PretupsI.LMS_PROMOTION_TYPE_STOCK);
			pstmtSelect.setString(index++,PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT);
			pstmtSelect.setDate(index++,BTSLUtil.getSQLDateFromUtilDate(p_dateCount));
			pstmtSelect.setDate(index++,BTSLUtil.getSQLDateFromUtilDate(p_dateCount));
			pstmtSelect.setDate(index++,BTSLUtil.getSQLDateFromUtilDate(p_dateCount));
			rs = pstmtSelect.executeQuery();
			while (rs.next()) {
				redemptionVO= new LoyaltyPointsRedemptionVO();
				redemptionVO.setSetId(rs.getString("set_id"));
				redemptionVO.setPeriodId(rs.getString("period_id"));
				redemptionVO.setPointsType(rs.getString("points_type"));
				redemptionVO.setRefBaseAllowed(rs.getString("ref_based_allowed"));
				redemptionVO.setUserID(rs.getString("user_id"));
				redemptionVO.setModuleType(rs.getString("type"));
				redemptionVO.setMsisdn(rs.getString("msisdn"));
				redemptionVO.setParentID(rs.getString("parent_id"));
				redemptionVO.setEndRange(rs.getString("end_range"));
				redemptionVO.setToRange(rs.getLong("end_range"));
				redemptionVO.setTotalCrLoyaltyPoint(rs.getLong("points"));
				redemptionVO.setFromDate(rs.getDate("applicable_from"));
				redemptionVO.setPromoStartDate(String.valueOf(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from"))));
				redemptionVO.setOperatorContribution(rs.getInt("opt_contribution"));
				redemptionVO.setParentContribution(rs.getInt("prt_contribution"));
				redemptionVO.setServiceCode(rs.getString("service_code"));
				redemptionVO.setProductCode(rs.getString("product_code"));
				redemptionVO.setProductShortCode(rs.getString("product_short_code"));
				redemptionVO.setNetworkID(rs.getString("network_code"));
				redemptionVO.setCategoryCode(rs.getString("category_code"));
				redemptionVO.setParentMsisdn(rs.getString("parent_msisdn"));
				redemptionVO.setParentEncryptedPin(rs.getString("parent_sms_pin"));
				redemptionVO.setApplicableToDate(rs.getDate("applicable_to"));
				redemptionVO.setVersion(rs.getString("version"));
				//Handling of OPT IN/OPT 
				redemptionVO.setOptInOutEnabled(rs.getString("OPT_IN_OUT_ENABLED"));
				redemptionVO.setOptInOutStatus(rs.getString("OPT_IN_OUT_STATUS"));
				profileList.add(redemptionVO);
			}
		} catch (SQLException sqe) {
			_logger.error(METHOD_NAME, "SQLException : " + sqe);
			_logger.errorTrace(METHOD_NAME,sqe);
		} catch (Exception ex) {
			_logger.error("", "Exception : " + ex);
			_logger.errorTrace(METHOD_NAME,ex);

		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				_logger.errorTrace(METHOD_NAME,e);
			}
			try {
				if(pstmtSelect != null) {
					pstmtSelect.close();
				}
			} catch (Exception e) {
				_logger.errorTrace(METHOD_NAME,e);
			}
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Exiting: profileList size=" + profileList.size());
			}
		}
		return profileList;

	}

	public LoyaltyPointsRedemptionVO loadCummulativeTxnForUsers(Connection p_con, LoyaltyPointsRedemptionVO p_redemptionVO)	{
		final String METHOD_NAME = "loadCummulativeTxnForUsers";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME," Entered with userId:"+p_redemptionVO.getUserID(),"CurrentProcessingDate:"+p_redemptionVO.getCurrentProcessDate() );
		}
		PreparedStatement pstmtSelect=null;
		ResultSet rs = null;
		Date currentProcessDate = p_redemptionVO.getCurrentProcessDate();
		Calendar startMonth = BTSLDateUtil.getInstance();
		Calendar currentMonth = BTSLDateUtil.getInstance();
		Calendar cal = BTSLDateUtil.getInstance();
		Date processedDate= new Date();
		SimpleDateFormat sdf = new SimpleDateFormat (PretupsI.DATE_FORMAT);
		String date1=sdf.format(currentProcessDate); 
		try {
		 processedDate=BTSLUtil.getDateFromDateString(date1);
		} catch (Exception e) {
			_logger.errorTrace(METHOD_NAME,e);
		}
		int dayDiff = 0;
		//this will determine whether we will run the query or not.
		//Date currentDate = new Date();
		StringBuffer selectQueryBuffer= new StringBuffer();
		if("C2S".equalsIgnoreCase(p_redemptionVO.getModuleType()) && "ALL".equalsIgnoreCase(p_redemptionVO.getServiceCode())) {
			selectQueryBuffer.append(" SELECT SUM (dctd.sender_transfer_amount) FROM daily_c2s_trans_details dctd ");
			selectQueryBuffer.append(" WHERE ");
			if("DAILY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
				selectQueryBuffer.append(" dctd.user_id=? AND dctd.trans_date=?  ");
			} else if("WEEKLY".equalsIgnoreCase(p_redemptionVO.getPeriodId()) || "MONTHLY".equalsIgnoreCase(p_redemptionVO.getPeriodId()) || "EOP".equalsIgnoreCase(p_redemptionVO.getPeriodId()))	{
				selectQueryBuffer.append(" dctd.user_id =? and dctd.trans_date >= ? and dctd.trans_date <=? ");
			}
			String selectQuery=selectQueryBuffer.toString();
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME ,"userId:"+p_redemptionVO.getUserID()+" , SQL Query :"+selectQuery);
			}
			try {
				if("DAILY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					pstmtSelect =p_con.prepareStatement(selectQuery);
					pstmtSelect.setString(1,p_redemptionVO.getUserID());
					pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
					
					rs = pstmtSelect.executeQuery();
				} else if("WEEKLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					
					Date date = getWeeklyBonusTargetCreditFromDate(p_redemptionVO.getFromDate(), BTSLUtil.addDaysInUtilDate(currentProcessDate,1));
					int noOfDaysInWeek = 7;
					dayDiff = BTSLUtil.getDifferenceInUtilDates(date, BTSLUtil.addDaysInUtilDate(currentProcessDate,1))%noOfDaysInWeek;
					if(dayDiff == 0 ) {												
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						
						pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(date) );
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
						
						rs = pstmtSelect.executeQuery();
					}
				} else if("MONTHLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					Date date = getMonthlyBonusTargetCreditFromDate(p_redemptionVO.getFromDate(), BTSLUtil.addDaysInUtilDate(currentProcessDate,1));
					int noOfDaysInCreatedMonth = getNoOfDaysInCreatedLMSProfileOnMonth(p_redemptionVO.getFromDate());
					dayDiff = BTSLUtil.getDifferenceInUtilDates(date, BTSLUtil.addDaysInUtilDate(currentProcessDate,1))%noOfDaysInCreatedMonth; 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						
						pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getFromDate()));
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
						rs = pstmtSelect.executeQuery();
					}
				} else if("EOP".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					
					dayDiff = BTSLUtil.getDifferenceInUtilDates(processedDate,p_redemptionVO.getApplicableToDate()); 
					if(dayDiff==0) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						
						pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getFromDate()) );
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getApplicableToDate()));
						
						rs = pstmtSelect.executeQuery();
					}
				}
				if(rs!=null) {
					while (rs.next()) {
						if(_logger.isDebugEnabled()) {
							_logger.debug(METHOD_NAME, "Resultset is found for UserID = "+p_redemptionVO.getUserID());
						}						
						p_redemptionVO.setSumAmount(rs.getLong(1));
						p_redemptionVO.setBonusCreditDateReached(true);
					}
				} else {
					p_redemptionVO.setBonusCreditDateReached(false);
					if(_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME, "Resultset is not found for UserID = "+p_redemptionVO.getUserID());
					}
				}
			} catch (SQLException sqe) {
				_logger.error(METHOD_NAME, "SQLException : " + sqe);
				_logger.errorTrace(METHOD_NAME,sqe);
			}  catch (Exception ex) {
				_logger.error("", "Exception : " + ex);
				_logger.errorTrace(METHOD_NAME,ex);

			} finally {
				try {
					if(rs != null) {
						rs.close();
					}
				} catch (Exception e) {
					_logger.errorTrace(METHOD_NAME,e);
				}
				try {
					if(pstmtSelect != null) {
						pstmtSelect.close();
					}
				} catch (Exception e) {
					_logger.errorTrace(METHOD_NAME,e);
				}
				if(_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "Exiting with sum Amount:"+p_redemptionVO.getSumAmount()+" , UserID = "+p_redemptionVO.getUserID());
				}
			}

		}
		//For All C2S Services
		else if("C2S".equalsIgnoreCase(p_redemptionVO.getModuleType()))
		{
			selectQueryBuffer.append(" SELECT SUM (dctd.sender_transfer_amount) FROM daily_c2s_trans_details dctd ");
			selectQueryBuffer.append(" WHERE ");
			if("DAILY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
				selectQueryBuffer.append(" dctd.user_id=? AND dctd.trans_date=? AND dctd.service_type=? ");
			} else if("WEEKLY".equalsIgnoreCase(p_redemptionVO.getPeriodId()) || "MONTHLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())|| "EOP".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
				selectQueryBuffer.append(" dctd.user_id =? and dctd.service_type=? and dctd.trans_date >= ? and dctd.trans_date <=? ");
			}
			String selectQuery=selectQueryBuffer.toString();
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME ,"userId:"+p_redemptionVO.getUserID()+" , SQL Query :"+selectQuery);
			}
			try {
				if("DAILY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					pstmtSelect =p_con.prepareStatement(selectQuery);
					pstmtSelect.setString(1,p_redemptionVO.getUserID());
					pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
					pstmtSelect.setString(3,p_redemptionVO.getServiceCode());
					rs = pstmtSelect.executeQuery();
				} else if("WEEKLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					
					Date date = getWeeklyBonusTargetCreditFromDate(p_redemptionVO.getFromDate(), BTSLUtil.addDaysInUtilDate(currentProcessDate,1));
					int noOfDaysInWeek = 7;
					dayDiff = BTSLUtil.getDifferenceInUtilDates(date, BTSLUtil.addDaysInUtilDate(currentProcessDate,1))%noOfDaysInWeek; 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						pstmtSelect.setString(2,p_redemptionVO.getServiceCode());
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(date) );
						pstmtSelect.setDate(4,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
						
						rs = pstmtSelect.executeQuery();
					}
				} else if("MONTHLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					Date date = getMonthlyBonusTargetCreditFromDate(p_redemptionVO.getFromDate(), BTSLUtil.addDaysInUtilDate(currentProcessDate,1));
					int noOfDaysInCreatedMonth = getNoOfDaysInCreatedLMSProfileOnMonth(p_redemptionVO.getFromDate());
					dayDiff = BTSLUtil.getDifferenceInUtilDates(date, BTSLUtil.addDaysInUtilDate(currentProcessDate,1))%noOfDaysInCreatedMonth; 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						pstmtSelect.setString(2,p_redemptionVO.getServiceCode());
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(date));
						pstmtSelect.setDate(4,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
						rs = pstmtSelect.executeQuery();
					}
				} else if("EOP".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					
					dayDiff = BTSLUtil.getDifferenceInUtilDates(processedDate,p_redemptionVO.getApplicableToDate()); 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						pstmtSelect.setString(2,p_redemptionVO.getServiceCode());
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getFromDate()) );
						pstmtSelect.setDate(4,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getApplicableToDate()));
						
						rs = pstmtSelect.executeQuery();
					}
				}
				
				if(rs!=null) {
					if(_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME, "Resultset is found for UserID = "+p_redemptionVO.getUserID());
					}
					while (rs.next()) {
						p_redemptionVO.setSumAmount(rs.getLong(1));
						p_redemptionVO.setBonusCreditDateReached(true);
					}
				} else {
					p_redemptionVO.setBonusCreditDateReached(false);
					if(_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME, "Resultset is not found for UserID = "+p_redemptionVO.getUserID());
					}
				}
			} catch (SQLException sqe) {
				_logger.error(METHOD_NAME, "SQLException : " + sqe);
				_logger.errorTrace(METHOD_NAME,sqe);
			} catch (Exception ex) {
				_logger.error("", "Exception : " + ex);
				_logger.errorTrace(METHOD_NAME,ex);

			} finally {
				try {
					if(rs != null) {
						rs.close();
					}
				} catch (Exception e) {
					_logger.errorTrace(METHOD_NAME,e);
				}
				try { 
					if(pstmtSelect != null) {
						pstmtSelect.close();
					}
				} catch (Exception e) {
					_logger.errorTrace(METHOD_NAME,e);
				}
				if(_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "Exiting with sum Amount:"+p_redemptionVO.getSumAmount()+" , UserID = "+p_redemptionVO.getUserID());
				}
			}

		} else if("O2C".equalsIgnoreCase(p_redemptionVO.getModuleType())) {
			selectQueryBuffer.append(" SELECT SUM(dctm.O2C_TRANSFER_IN_AMOUNT) FROM daily_chnl_trans_main dctm ");
			selectQueryBuffer.append(" WHERE ");
			if("DAILY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
				selectQueryBuffer.append(" dctm.user_id=? AND dctm.trans_date=? ");
			} else if("WEEKLY".equalsIgnoreCase(p_redemptionVO.getPeriodId()) || "MONTHLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())|| "EOP".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
				selectQueryBuffer.append(" dctm.user_id=? AND dctm.trans_date >= ? and dctm.trans_date <=? ");
			}
			String selectQuery=selectQueryBuffer.toString();
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME ,"userId:"+p_redemptionVO.getUserID()+" , SQL Query :"+selectQuery);
			}
			try {
				if("DAILY".equalsIgnoreCase(p_redemptionVO.getPeriodId()))
				{
					pstmtSelect =p_con.prepareStatement(selectQuery);
					pstmtSelect.setString(1,p_redemptionVO.getUserID());
					pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
					rs = pstmtSelect.executeQuery();
				} else if("WEEKLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					Date date = getWeeklyBonusTargetCreditFromDate(p_redemptionVO.getFromDate(), BTSLUtil.addDaysInUtilDate(currentProcessDate,1));
					int noOfDaysInWeek = 7;
					dayDiff = BTSLUtil.getDifferenceInUtilDates(date, BTSLUtil.addDaysInUtilDate(currentProcessDate,1))%noOfDaysInWeek; 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(date) );
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
						rs = pstmtSelect.executeQuery();
					}
				} else if("MONTHLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					Date date = getMonthlyBonusTargetCreditFromDate(p_redemptionVO.getFromDate(), BTSLUtil.addDaysInUtilDate(currentProcessDate,1));
					int noOfDaysInCreatedMonth = getNoOfDaysInCreatedLMSProfileOnMonth(p_redemptionVO.getFromDate());
					dayDiff = BTSLUtil.getDifferenceInUtilDates(date, BTSLUtil.addDaysInUtilDate(currentProcessDate,1))%noOfDaysInCreatedMonth; 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(date));
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
						rs = pstmtSelect.executeQuery();
					}
				} else if("EOP".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
										
					dayDiff = BTSLUtil.getDifferenceInUtilDates(processedDate,p_redemptionVO.getApplicableToDate()); 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());						
						pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getFromDate()) );
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getApplicableToDate()));
						
						rs = pstmtSelect.executeQuery();
					}
				}

				if(rs!=null) {
					while (rs.next()) {
						if(_logger.isDebugEnabled()) {
							_logger.debug(METHOD_NAME, "Resultset is found for UserID = "+p_redemptionVO.getUserID());
						}
						//p_redemptionVO= new LoyaltyPointsRedemptionVO();
						p_redemptionVO.setSumAmount(rs.getLong(1));
						p_redemptionVO.setBonusCreditDateReached(true);
					}
				}  else {
					p_redemptionVO.setBonusCreditDateReached(false);
					if(_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME, "Resultset is not found for UserID = "+p_redemptionVO.getUserID());
					}
				}
			} catch (SQLException sqe) {
				_logger.error(METHOD_NAME, "SQLException : " + sqe);
				_logger.errorTrace(METHOD_NAME,sqe);
			} catch (Exception ex) {
				_logger.error("", "Exception : " + ex);
				_logger.errorTrace(METHOD_NAME,ex);				
			} finally {
				try {
					if(rs != null) {
						rs.close();
					}
				} catch (Exception e) {
					_logger.errorTrace(METHOD_NAME,e);
				}
				try {
					if(pstmtSelect != null) {
						pstmtSelect.close();
					}
				} catch (Exception e) {
					_logger.errorTrace(METHOD_NAME,e);
				}
				if(_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "Exiting with sum Amount:"+p_redemptionVO.getSumAmount()+" , UserID = "+p_redemptionVO.getUserID());
				}
			}
		} else if("C2C".equalsIgnoreCase(p_redemptionVO.getModuleType())) {
			selectQueryBuffer.append(" SELECT SUM(C2C_TRANSFER_OUT_AMOUNT),SUM(C2C_TRANSFER_IN_AMOUNT) ");
			selectQueryBuffer.append(" FROM daily_chnl_trans_main dctm ");
			selectQueryBuffer.append(" WHERE ");
			if("DAILY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
				selectQueryBuffer.append(" dctm.user_id=? AND dctm.trans_date=? ");
			} else if("WEEKLY".equalsIgnoreCase(p_redemptionVO.getPeriodId()) || "MONTHLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())|| "EOP".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
				selectQueryBuffer.append(" dctm.user_id=? AND dctm.trans_date >= ? and dctm.trans_date <=? ");
			}
			String selectQuery=selectQueryBuffer.toString();
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME ,"userId:"+p_redemptionVO.getUserID()+" , SQL Query :"+selectQuery);
			}
			try {
				if("DAILY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					pstmtSelect =p_con.prepareStatement(selectQuery);
					pstmtSelect.setString(1,p_redemptionVO.getUserID());
					pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
					rs = pstmtSelect.executeQuery();
				} else if("WEEKLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					Date date = getWeeklyBonusTargetCreditFromDate(p_redemptionVO.getFromDate(), BTSLUtil.addDaysInUtilDate(currentProcessDate,1));
					int noOfDaysInWeek = 7;
					dayDiff = BTSLUtil.getDifferenceInUtilDates(date, BTSLUtil.addDaysInUtilDate(currentProcessDate,1))%noOfDaysInWeek; 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(date) );
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
										
						rs = pstmtSelect.executeQuery();
					}
				} else if("MONTHLY".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					Date date = getMonthlyBonusTargetCreditFromDate(p_redemptionVO.getFromDate(), BTSLUtil.addDaysInUtilDate(currentProcessDate,1));
					int noOfDaysInCreatedMonth = getNoOfDaysInCreatedLMSProfileOnMonth(p_redemptionVO.getFromDate());
					dayDiff = BTSLUtil.getDifferenceInUtilDates(p_redemptionVO.getFromDate(), BTSLUtil.addDaysInUtilDate(currentProcessDate,1))%noOfDaysInCreatedMonth; 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(date));
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getSumTxnsDate()));
						rs = pstmtSelect.executeQuery();
					}
				}  else if("EOP".equalsIgnoreCase(p_redemptionVO.getPeriodId())) {
					
					dayDiff = BTSLUtil.getDifferenceInUtilDates(processedDate,p_redemptionVO.getApplicableToDate()); 
					if(dayDiff == 0 ) {
						pstmtSelect =p_con.prepareStatement(selectQuery);
						pstmtSelect.setString(1,p_redemptionVO.getUserID());
						dayDiff = BTSLUtil.getDifferenceInUtilDates(currentProcessDate,p_redemptionVO.getApplicableToDate()); 
						pstmtSelect.setDate(2,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getFromDate()) );
						pstmtSelect.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_redemptionVO.getApplicableToDate()));
						
						rs = pstmtSelect.executeQuery();
					}
				}
				if(rs!=null) {
					while (rs.next()) {
						if(_logger.isDebugEnabled()) {
							_logger.debug(METHOD_NAME, "Resultset is found for UserID = "+p_redemptionVO.getUserID());
						}
						//p_redemptionVO= new LoyaltyPointsRedemptionVO();
						p_redemptionVO.setSumAmount(rs.getLong(1) + rs.getLong(2));
						p_redemptionVO.setBonusCreditDateReached(true);
					}
				} else {
					if(_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME, "Resultset is not found for UserID = "+p_redemptionVO.getUserID());
					}
					p_redemptionVO.setBonusCreditDateReached(false);
				}
			} catch (SQLException sqe) {
				_logger.error(METHOD_NAME, "SQLException : " + sqe);
				_logger.errorTrace(METHOD_NAME,sqe);
			} catch (Exception ex) {
				_logger.error("", "Exception : " + ex);
				_logger.errorTrace(METHOD_NAME,ex);				
			} finally {
				try {
					if(rs != null) {
						rs.close();
					}
				} catch (Exception e) {
					_logger.errorTrace(METHOD_NAME,e);
				}
				try {
					if(pstmtSelect != null) {
						pstmtSelect.close();
						}
				} catch (Exception e) {
					_logger.errorTrace(METHOD_NAME,e);
				}
				if(_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "Exiting with sum Amount:"+p_redemptionVO.getSumAmount()+" , UserID = "+p_redemptionVO.getUserID());
				}
			}

		}

		return p_redemptionVO;

	}

	public void SettleLoyaltyPoints(Connection con, LoyaltyPointsRedemptionVO p_redemptionVO, boolean p_c2cFlag)throws BTSLBaseException {
		final String METHOD_NAME = "SettleLoyaltyPoints";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME,"Entered with userId: "+p_redemptionVO.getUserID());		
		}
		try {

			long totalCreditPoints=0;
			int count=0;

			//count=creditLoyaltyPoint(con,p_redemptionVO, p_c2cFlag);

			totalCreditPoints=totalCreditPoints+p_redemptionVO.getTotalCrLoyaltyPoint();
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_STOCK_REQUIRED))).booleanValue()) {
				count=debitNetworkLoyaltyStock(con,p_redemptionVO);
			} else {
				count=1;
			}
            
			if(count==0) {
				throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
			} else {
				con.commit();
			}

		} catch(BTSLBaseException bex) {
			try {
				con.rollback();
			} catch(Exception e) {
				_logger.errorTrace(METHOD_NAME,e);
			}
			_logger.error("", "Exception : " + bex);
			_logger.errorTrace(METHOD_NAME,bex);
		} catch(Exception ex) {
			_logger.error("", "Exception : " + ex);
			_logger.errorTrace(METHOD_NAME,ex);
		}
	}

	public void DistributeLoyaltyPoints(Connection con, LoyaltyPointsRedemptionVO p_redemptionVO, boolean p_c2cFlag,Date p_processingDate)throws BTSLBaseException {
		final String METHOD_NAME = "DistributeLoyaltyPoints";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME,"Entered with userId: "+p_redemptionVO.getUserID());
		}		
		try {

			long totalCreditPoints=0;
			int count1=0;
			//int count2=0;
			int count3=0;
			//p_redemptionVO.setSumAmount(totalCreditPoints); //flushing it for next iteration
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_STOCK_REQUIRED))).booleanValue()) {
				count1=debitNetworkLoyaltyStock(con,p_redemptionVO);
			} else {
				count1=1;
			}
			//commenting because we don't need parent involvement for loyalty points, but for airtime.(new plan ;))
			if(count1>0) {
				count3=creditLoyaltyPoint(con,p_redemptionVO,p_processingDate);
				totalCreditPoints=totalCreditPoints+p_redemptionVO.getTotalCrLoyaltyPoint();
				if(count3>0) {
					con.commit();
					LoyaltyPointsLog.log("TAR",p_redemptionVO.getUserID(),p_redemptionVO.getSetId(),new Date(),PretupsI.CREDIT,"",p_redemptionVO.getModuleType(),p_redemptionVO.getServiceCode(),p_redemptionVO.getTotalCrLoyaltyPoint(),p_redemptionVO.getTarget(),p_redemptionVO.getSumAmount(),"200","");
				} else {
					throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
				}
			} else {
				throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
			}				
		} catch(BTSLBaseException bex) {
			p_redemptionVO.setErrorCode(PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
			try {
				con.rollback();
			} catch(Exception e) {
				_logger.errorTrace(METHOD_NAME,e);
			}
			LoyaltyPointsLog.log("TAR",p_redemptionVO.getUserID(),p_redemptionVO.getSetId(),new Date(),PretupsI.CREDIT,"",p_redemptionVO.getModuleType(),p_redemptionVO.getServiceCode(),p_redemptionVO.getTotalCrLoyaltyPoint(),p_redemptionVO.getTarget(),p_redemptionVO.getSumAmount(),"206",p_redemptionVO.getErrorCode());
			_logger.error("", "Exception : " + bex);
			_logger.errorTrace(METHOD_NAME,bex);
		} catch(Exception ex) {
			p_redemptionVO.setErrorCode(PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
			_logger.error("", "Exception : " + ex);
			LoyaltyPointsLog.log("TAR",p_redemptionVO.getUserID(),p_redemptionVO.getSetId(),new Date(),PretupsI.CREDIT,"",p_redemptionVO.getModuleType(),p_redemptionVO.getServiceCode(),p_redemptionVO.getTotalCrLoyaltyPoint(),p_redemptionVO.getTarget(),p_redemptionVO.getSumAmount(),"206",p_redemptionVO.getErrorCode());
			_logger.errorTrace(METHOD_NAME,ex);
		}
	}

	public int creditLoyaltyPoint(Connection con,LoyaltyPointsRedemptionVO p_redemptionVO,Date p_processingDate) throws BTSLBaseException {		
		final String METHOD_NAME = "creditLoyaltyPoint";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME," UserId :"+p_redemptionVO.getUserID()+" , p_processingDate="+p_processingDate);
		}
		double p_loyaltypoint=0;
		double c_loyaltypoint=0;	
		ActivationBonusVO bonusOldVO=null;
		//Date realCurrentDate = new Date();
		Date realCurrentDate = p_processingDate;
		int insertCount =0;
		try {		
			//check entry already present in BONUS table corresponding to user_id,product_type, point date and product code
			bonusOldVO=checkUserAlreadyExist(p_redemptionVO.getUserID(),realCurrentDate,p_redemptionVO.getProductCode());
			if(bonusOldVO!=null) {
				//if it is present then update the entries 
				p_loyaltypoint = p_redemptionVO.getTotalCrLoyaltyPoint();				
				bonusOldVO.setLastAllocationType("VOLUME");
				bonusOldVO.setPointsDate(p_redemptionVO.getCurrentProcessDate());

				bonusOldVO.setUserId(p_redemptionVO.getUserID());
				bonusOldVO.setLastAllocationdate(realCurrentDate);
				bonusOldVO.setProductCode(p_redemptionVO.getProductCode());
				//Write Profile Bonus Log
				//ProfileBonusLog.log("Success",bonusOldVO,p_redemptionVO.getUserID(),c2sTransferVO.getReceiverMsisdn(),bonusVO.getPoints(),PretupsBL.getDisplayAmount(c2sTransferVO.getTransferValue()),bonusOldVO.getPoints());
				bonusOldVO.setPoints(p_loyaltypoint);
				bonusOldVO.setTransferId(p_redemptionVO.getLmsTxnId());
				insertCount=updateBonusOfUser(bonusOldVO);
				////
			} else {
				long accumPoints=0L;
				bonusOldVO = null;
				bonusOldVO=checkUserExistLastDateDetail(p_redemptionVO.getUserID(),p_redemptionVO.getProductCode());
				if(bonusOldVO == null) {
					bonusOldVO= new ActivationBonusVO();
					bonusOldVO.setAccumulatedPoints(p_redemptionVO.getTotalCrLoyaltyPoint());
				} else {
					bonusOldVO.setAccumulatedPoints(p_redemptionVO.getTotalCrLoyaltyPoint()+bonusOldVO.getAccumulatedPoints());
				}
				//and if it is not present then insert new entry				
				bonusOldVO.setProfileType("LMS");
				//Brajesh
				//Done to set different bucket codes for different types of allocation types
				LookupsVO lookupsVO = new LookupsVO();
				lookupsVO = (LookupsVO)LookupsCache.getObject(PretupsI.BUCKET_CODE, PretupsI.BUCKET_CODE_VOL);
				//
				bonusOldVO.setBucketCode(lookupsVO.getLookupName());
				//
				//bonusOldVO.setBucketCode(PretupsI.BUCKET_ONE);
				bonusOldVO.setProductCode(p_redemptionVO.getProductCode());
				bonusOldVO.setLastAllocationType(PretupsI.PROFILE_TRANS);
				bonusOldVO.setCreatedOn(realCurrentDate);
				bonusOldVO.setCreatedBy(PretupsI.SYSTEM);
				bonusOldVO.setModifiedOn(realCurrentDate);
				bonusOldVO.setModifiedBy(PretupsI.SYSTEM);
				bonusOldVO.setLastAllocationType("VOLUME");
				bonusOldVO.setPointsDate(p_redemptionVO.getCurrentProcessDate());
				bonusOldVO.setUserId(p_redemptionVO.getUserID());
				bonusOldVO.setLastAllocationdate(realCurrentDate);
				bonusOldVO.setPoints(p_redemptionVO.getTotalCrLoyaltyPoint());
				//Brajesh
				bonusOldVO.setSetID(p_redemptionVO.getSetId());
				bonusOldVO.setVersion(p_redemptionVO.getVersion());
				//Write Profile Bonus Log
				//ProfileBonusLog.log("Success",bonusVO,actBonusSubsMappingVO.getUserID(),c2sTransferVO.getReceiverMsisdn(),bonusVO.getPoints(),PretupsBL.getDisplayAmount(c2sTransferVO.getTransferValue()),0);
				insertCount=saveBonus(bonusOldVO);
				if(insertCount<=0) {
					//_logger.debug(METHOD_NAME,"Entry not inserted in BONUS table");
					throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.INSERTION_ERROR_BONUS_TABLE); 
				}
			}
		} catch(Exception ex) {
			_logger.errorTrace(METHOD_NAME,ex);
			throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
		} finally {			
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Exiting with Count :" +insertCount );
			}
		}
		return insertCount;
	}
	
	public int debitNetworkLoyaltyStock(Connection con, LoyaltyPointsRedemptionVO p_redemptionVO) throws BTSLBaseException {
		final String METHOD_NAME = "debitNetworkLoyaltyStock";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME,"");
		}
		PreparedStatement pstmt = null;	
		PreparedStatement pstmt1 = null;	
		PreparedStatement psmtInsert = null;

		int count=0;
		long p_loyaltypoint=0;
		long c_loyaltypoint=0;	
		long updatedloyaltypoint=0;
		ResultSet rs = null;
		int insertCount=0;
		try {						        
			StringBuffer SelectstrBuff = new StringBuffer("SELECT LOYALTY_STOCK,PREVIOUS_LOYALTY_STOCK FROM LOYALTY_STOCK WHERE NETWORK_CODE=? AND NETWORK_CODE_FOR= ? AND PRODUCT_CODE= ? FOR UPDATE ");
			StringBuffer UpdatestrBuff = new StringBuffer("UPDATE LOYALTY_STOCK SET LOYALTY_STOCK= ?,PREVIOUS_LOYALTY_STOCK= ? WHERE NETWORK_CODE=? AND NETWORK_CODE_FOR= ? AND PRODUCT_CODE= ?");
			StringBuffer InserstrBuff= new StringBuffer("INSERT INTO LOYALTY_STOCK_TRANSACTION (TXN_NO,NETWORK_CODE,LOYALTY_STOCK,PREVIOUS_LOYALTY_STOCK,LAST_TXN_TYPE,LOYALTY_POINT_SPEND,TXN_STATUS,REQUESTED_POINTS,CREATED_ON,CREATED_BY) VALUES (?,?,?,?,?,?,?,?,?,?)");
			String sqlSelect= SelectstrBuff.toString();
			String sqlUpdate= UpdatestrBuff.toString();
			String sqlInsert= InserstrBuff.toString();
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME,"debitNetworkLoyaltyStock Method.....sqlSelect ",""+sqlSelect);
			}
			pstmt = con.prepareStatement(sqlSelect);
			pstmt.setString(1,p_redemptionVO.getNetworkID() );	
			pstmt.setString(2,p_redemptionVO.getNetworkID() );	
			pstmt.setString(3,p_redemptionVO.getProductCode());	
			rs=pstmt.executeQuery();
			while(rs.next()) {		        	
				p_loyaltypoint=rs.getLong("PREVIOUS_LOYALTY_STOCK");
				c_loyaltypoint=rs.getLong("LOYALTY_STOCK");
			}
			if(c_loyaltypoint>=p_redemptionVO.getTotalCrLoyaltyPoint()) {
				updatedloyaltypoint=c_loyaltypoint-p_redemptionVO.getTotalCrLoyaltyPoint();
			} else {
				p_redemptionVO.setErrorCode(PretupsErrorCodesI.LOYALTY_NETWORK_STOCK_NOT_OK);
				throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.LOYALTY_NETWORK_STOCK_NOT_OK);
			}
			p_loyaltypoint=c_loyaltypoint;
			pstmt.clearParameters();
			if(_logger.isDebugEnabled()) {
				_logger.debug("debitNetworkLoyaltyStock Method.....sqlUpdate ",""+sqlUpdate+"previous loyaltypoint "+p_loyaltypoint+"CurrentLoyaltypoint "+updatedloyaltypoint);
			}
			pstmt1 = con.prepareStatement(sqlUpdate);
			pstmt1.setLong(1,updatedloyaltypoint );
			pstmt1.setLong(2, p_loyaltypoint);
			pstmt1.setString(3, p_redemptionVO.getNetworkID());
			pstmt1.setString(4, p_redemptionVO.getNetworkID());
			pstmt1.setString(5, p_redemptionVO.getProductCode());
			count=pstmt1.executeUpdate();
			if(count<1)	{
				p_redemptionVO.setErrorCode(PretupsErrorCodesI.LOYALTY_NETWORK_STOCK_NOT_OK);
				throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.LOYALTY_NETWORK_STOCK_NOT_OK);
			} else {
				count=0;				
				psmtInsert = con.prepareStatement(sqlInsert);
				psmtInsert.setString(1, p_redemptionVO.getLmsTxnId());
				psmtInsert.setString(2, p_redemptionVO.getNetworkID());
				psmtInsert.setLong(3, p_loyaltypoint-p_redemptionVO.getTotalCrLoyaltyPoint());
				psmtInsert.setLong(4, p_loyaltypoint);
				psmtInsert.setString(5,p_redemptionVO.getServiceCode());					
				psmtInsert.setLong(6, p_redemptionVO.getTotalCrLoyaltyPoint());				
				psmtInsert.setString(7, PretupsI.SUCCESS);
				psmtInsert.setLong(8, p_redemptionVO.getTotalCrLoyaltyPoint());
				psmtInsert.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(new Date()));			
				psmtInsert.setString(10,PretupsI.SYSTEM);				
				insertCount = psmtInsert.executeUpdate();	
				if(insertCount==0) {
					throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.LOYALTY_PROCESSING_FAILED);
				} else {
					count=1;
				}
			}
		} catch(Exception ex) {
			_logger.errorTrace(METHOD_NAME,ex);
			p_redemptionVO.setErrorCode(PretupsErrorCodesI.LOYALTY_NETWORK_STOCK_NOT_OK);
			throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.LOYALTY_NETWORK_STOCK_NOT_OK);
		} finally {			
				
			try {
				if(rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				_logger.errorTrace(METHOD_NAME,e);
			}
			try {
				if(pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				_logger.errorTrace(METHOD_NAME,e);
			}
			try {
				if(pstmt1 != null) {
					pstmt1.close();
				}
			} catch (Exception e) {
				_logger.errorTrace(METHOD_NAME,e);
			}
			try {
				if(psmtInsert != null) {
					psmtInsert.close();
				}
			} catch (Exception e) {
				_logger.errorTrace(METHOD_NAME,e);
			}
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Exiting with Count :" +count );
			}
		}
		return count;
	}

	// METHOD TO CUSTOMIZE O2C REQUEST
	public String initaiateFocRedemptionRequest(LoyaltyPointsRedemptionVO p_redemptionVO) throws BTSLBaseException	{
		final String METHOD_NAME = "initaiateFocRedemptionRequest";
		if(_logger.isDebugEnabled())  {
			_logger.debug(METHOD_NAME, "Entered");
		}
		String o2cFocTxnId = null;
		HttpURLConnection con=null;
		BufferedReader in=null;
		InstanceLoadVO instanceLoadVO=null;
		String urlToSend=null;
		//String msisdn=null;
		//String msisdnPrefix=null;
		String httpURLPrefix="http://";
		String msgGWPass=null;
		String requestXML =null;
		String responseStr=null;
		String finalResponse="";
		String response =null;
		try {
			//msisdn= p_redemptionVO.getMsisdn();
			//msisdnPrefix = PretupsBL.getMSISDNPrefix(msisdn);
			requestXML = this.generateRequestFocXML(p_redemptionVO);
			MessageGatewayVO messageGatewayVO=MessageGatewayCache.getObject(PretupsI.REQUEST_SOURCE_TYPE_EXTGW);
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "messageGatewayVO: "+messageGatewayVO);
			}

			if(messageGatewayVO==null) {
				p_redemptionVO.setErrorCode(PretupsI.TXN_STATUS_FAIL);
				throw new BTSLBaseException("RunLMSForTargetCreditNew",METHOD_NAME,PretupsErrorCodesI.ERROR_NOTFOUND_MESSAGEGATEWAY);
			}
			RequestGatewayVO requestGatewayVO=messageGatewayVO.getRequestGatewayVO(); 
			if(requestGatewayVO==null) {
				p_redemptionVO.setErrorCode(PretupsI.TXN_STATUS_FAIL);
				throw new BTSLBaseException("RunLMSForTargetCreditNew",METHOD_NAME,PretupsErrorCodesI.ERROR_NOTFOUND_REQMESSAGEGATEWAY);
			}

			if(!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getStatus())) {
				p_redemptionVO.setErrorCode(PretupsI.TXN_STATUS_FAIL);
				throw new BTSLBaseException(this, METHOD_NAME, "c2stranfer.c2srecharge.error.messagegatewaynotactive","c2sRecharge");
			} else if(!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getRequestGatewayVO().getStatus())) {
				p_redemptionVO.setErrorCode(PretupsI.TXN_STATUS_FAIL);
				throw new BTSLBaseException(this, METHOD_NAME, "c2stranfer.c2srecharge.error.reqmessagegatewaynotactive","c2sRecharge");
			}
			//If Encrypted Password check box is checked. i.e. send password in request as encrypted.
			if(messageGatewayVO.getReqpasswordtype().equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
				msgGWPass =BTSLUtil.decryptText(messageGatewayVO.getRequestGatewayVO().getPassword());
			} else {
				msgGWPass=messageGatewayVO.getRequestGatewayVO().getPassword();
			}

			String networkCode = p_redemptionVO.getNetworkID();
			String instanceID=null;
			instanceID=Constants.getProperty("INSTANCE_ID");
			if(BTSLUtil.isNullString(instanceID)) {
				_logger.error("RunLMSForTargetCreditNew"," Not able to get the instance ID for the network="+networkCode+" where the request for o2c needs to be send");
				throw new BTSLBaseException("RunLMSForTargetCreditNew",METHOD_NAME,"lms.point.redemption.form.error.unableto.initiateo2c","");
			}
			instanceLoadVO=LoadControllerCache.getInstanceLoadForNetworkHash(instanceID+"_"+networkCode+"_"+PretupsI.REQUEST_SOURCE_TYPE_SMS);

			if(instanceLoadVO==null) {
				instanceLoadVO=LoadControllerCache.getInstanceLoadForNetworkHash(instanceID+"_"+networkCode+"_"+PretupsI.REQUEST_SOURCE_TYPE_WEB);
			}
			if(instanceLoadVO==null) {
				instanceLoadVO=LoadControllerCache.getInstanceLoadForNetworkHash(instanceID+"_"+networkCode+"_"+PretupsI.REQUEST_SOURCE_TYPE_DUMMY);
			}

			StringBuffer sbf1 = new StringBuffer();
			sbf1.append("REQUEST_GATEWAY_CODE="+messageGatewayVO.getGatewayCode());
			sbf1.append("&REQUEST_GATEWAY_TYPE="+messageGatewayVO.getGatewayType());
			sbf1.append("&LOGIN="+messageGatewayVO.getRequestGatewayVO().getLoginID());
			sbf1.append("&PASSWORD="+msgGWPass );
			sbf1.append("&SERVICE_PORT="+messageGatewayVO.getRequestGatewayVO().getServicePort());
			sbf1.append("&SOURCE_TYPE="+PretupsI.REQUEST_SOURCE_TYPE_EXTGW);
			String authorization = sbf1.toString();	
			if(instanceLoadVO==null) {
				_logger.error("RunLMSForTargetCreditNew"," Not able to get the instance detaile for the network="+networkCode+" where the request for o2c needs to be send");
				throw new BTSLBaseException("RunLMSForTargetCreditNew",METHOD_NAME,PretupsErrorCodesI.INSTANCE_CODE_NOT_FOUND);
			} else {
				urlToSend=httpURLPrefix+instanceLoadVO.getHostAddress()+":"+instanceLoadVO.getHostPort()+Constants.getProperty("CHANNEL_WEB_RECHARGE_SERVLET")+"?";	
			}

			try {
				URL url = new URL(urlToSend);
				URLConnection uc = url.openConnection();
				con = (HttpURLConnection) uc;
				con.addRequestProperty("Content-Type", "text/xml");
				con.addRequestProperty("Authorization", authorization);
				con.setUseCaches(false);
				con.setDoInput(true);
				con.setDoOutput(true);
				con.setRequestMethod("POST");
				try(BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF8"));)
				{
				// Send data
				wr.write(requestXML);
				wr.flush();
				// Get response
				in= new BufferedReader(new InputStreamReader(con.getInputStream()));
				while ((responseStr = in.readLine()) !=null) {
					finalResponse=finalResponse+responseStr ;
				}
				wr.close();
				}
			} catch (Exception e) {
				_logger.errorTrace(METHOD_NAME,e);
			} finally {
				try{
            		if(in != null){
            			in.close();
            		}
            	}catch(Exception e){
            		_logger.errorTrace(METHOD_NAME, e);
            	}
				if(con != null) {con.disconnect();}
			}

			if(!BTSLUtil.isNullString(finalResponse)) {
				int index=finalResponse.indexOf("<TXNID>");
				o2cFocTxnId=finalResponse.substring(index+"<TXNID>".length(),finalResponse.indexOf("</TXNID>",index));
				index=finalResponse.indexOf("<TXNSTATUS>");
				String focO2cTxnStatus=finalResponse.substring(index+"<TXNSTATUS>".length(),finalResponse.indexOf("</TXNSTATUS>",index));

				response=o2cFocTxnId +"@"+focO2cTxnStatus;
				if(_logger.isDebugEnabled()) {
					_logger.debug("RunLMSForTargetCreditNew", "initaiateFocRedemptionRequest: o2cFocTxnId="+o2cFocTxnId+" focO2cTxnStatus= "+focO2cTxnStatus);
				}
			} else {
				o2cFocTxnId=null;
				response=null;
			}
		} catch(BTSLBaseException bse) {
			_logger.errorTrace(METHOD_NAME,bse);
			throw new BTSLBaseException("RunLMSForTargetCreditNew",METHOD_NAME,"catch","");
		} catch(Exception e) {
			_logger.errorTrace(METHOD_NAME,e);
		} finally {
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME , "Exiting response=" +response);
			}
		}
		return response;
	}


	public String generateRequestFocXML(LoyaltyPointsRedemptionVO p_redempVO) {
		final String METHOD_NAME = "generateRequestFocXML";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME, "Entered");
		}
		String requesStr=null;
		StringBuffer sbf=null;
		try {
			SecureRandom exttxnno = new SecureRandom();
			SecureRandom refno = new SecureRandom();
			SecureRandom pinstno = new SecureRandom();

			sbf=new StringBuffer();
			sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command1.0//EN\" \"xml/command.dtd\"><COMMAND>");	
			sbf.append("<TYPE>O2CINTREQ</TYPE>"); 
			sbf.append("<EXTNWCODE>"+p_redempVO.getNetworkID()+"</EXTNWCODE>");
			sbf.append("<MSISDN>"+p_redempVO.getMsisdn()+"</MSISDN>");
			sbf.append("<PIN>"+((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN))+"</PIN>");
			if(!BTSLUtil.isNullString(p_redempVO.getExternalCode())) {
				sbf.append("<EXTCODE>"+p_redempVO.getExternalCode()+"</EXTCODE>");
			} else {
				sbf.append("<EXTCODE></EXTCODE>");
			}

			sbf.append("<EXTTXNNUMBER>"+ exttxnno.nextInt(1000000)+"</EXTTXNNUMBER>");
			sbf.append("<EXTTXNDATE>"+BTSLUtil.getDateStringFromDate(p_redempVO.getCurrentProcessDate()) +"</EXTTXNDATE>");
			sbf.append("<PRODUCTS>");
			sbf.append("<PRODUCTCODE>"+p_redempVO.getProductShortCode()+"</PRODUCTCODE>");
			sbf.append("<QTY>"+p_redempVO.getO2cContribution()+"</QTY>");
			sbf.append("</PRODUCTS>");
			sbf.append("<TRFCATEGORY>FOC</TRFCATEGORY>");
			sbf.append("<REFNUMBER>"+refno.nextInt(1000000)+"</REFNUMBER>");
			sbf.append("<PAYMENTDETAILS>");
			sbf.append("<PAYMENTTYPE>CHQ</PAYMENTTYPE>");
			sbf.append("<PAYMENTINSTNUMBER>"+ pinstno.nextInt(1000)+"</PAYMENTINSTNUMBER>");
			sbf.append("<PAYMENTDATE>"+BTSLUtil.getDateStringFromDate(p_redempVO.getCurrentProcessDate()) +"</PAYMENTDATE>");
			sbf.append("</PAYMENTDETAILS>");
			sbf.append("<REMARKS>LMSFOCO2C</REMARKS>");
			sbf.append("</COMMAND>");
			requesStr = sbf.toString();
		} catch(Exception ex) {
			_logger.errorTrace(METHOD_NAME,ex);
		} finally {
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME , "Exiting requesStr=" +requesStr);
			}
		}
		return requesStr;
	}

	private static int markProcessStatusAsComplete(Connection p_con,String p_processId) {
		final String METHOD_NAME = "markProcessStatusAsComplete";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME," Entered:  p_processId:"+p_processId);
		}
		int updateCount=0;
		Date currentDate=new Date();
		ProcessStatusDAO processStatusDAO=new ProcessStatusDAO();
		_processStatusVO.setProcessID(p_processId);
		_processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
		_processStatusVO.setStartDate(currentDate);
		try {
			updateCount =processStatusDAO.updateProcessDetail(p_con,_processStatusVO);
		} catch(Exception e) {
			_logger.errorTrace(METHOD_NAME,e);
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME,"Exception= " + e.getMessage());
			}
		} finally {
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME,"Exiting: updateCount=" + updateCount);
			}
		} 
		// end of finally
		return updateCount;
	}

	public void calO2CAndC2CContribution(LoyaltyPointsRedemptionVO p_redemptionVO) {
		final String METHOD_NAME = "calO2CAndC2CContribution";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME,"Entered: target Loyalty Point=" + p_redemptionVO.getLmsTarget());
		}
		int optContPercentage=0;
		int prtContPercentage=0;
		long  loyaltyPoint=0;
		double multFactor=0;
		long o2cRequstedAmt = 0;
		long c2cRequstedAmt =0;
		//28/01/14
		//String targetBreak = null;
		//String serviceBreak = null;
		//String target = null;
		try {
			loyaltyPoint = p_redemptionVO.getTotalCrLoyaltyPoint();
			multFactor = Double.parseDouble(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_MULT_FACTOR)));;
			optContPercentage=p_redemptionVO.getOperatorContribution();
			prtContPercentage=p_redemptionVO.getParentContribution();
			//System.out.println("x");
			o2cRequstedAmt = BTSLUtil.parseDoubleToLong(loyaltyPoint * multFactor);
			if((optContPercentage)<100 && !p_redemptionVO.getParentID().equalsIgnoreCase(PretupsI.ROOT_PARENT_ID))	{
				o2cRequstedAmt = BTSLUtil.parseDoubleToLong((loyaltyPoint * optContPercentage * multFactor)/100) ;
				c2cRequstedAmt =  BTSLUtil.parseDoubleToLong((loyaltyPoint * prtContPercentage * multFactor)/100) ;
				p_redemptionVO.setC2cContribution(c2cRequstedAmt);
			}
			p_redemptionVO.setO2cContribution(o2cRequstedAmt);

		} catch(Exception ex) {
			_logger.errorTrace(METHOD_NAME,ex);
		} finally {
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME,"Exiting: p_redemptionVO o2c requsted value=" + p_redemptionVO.getO2cContribution()+" c2c requsted value= "+p_redemptionVO.getC2cContribution());
			}
		}
	}

	public String initaiateC2CTransferRequest(LoyaltyPointsRedemptionVO p_redemptionVO) throws BTSLBaseException {
		final String METHOD_NAME = "initaiateC2CTransferRequest";
		if(_logger.isDebugEnabled())  {
			_logger.debug(METHOD_NAME, "Entered");
		}
		String o2cFocTxnId = null;
		HttpURLConnection con=null;
		BufferedReader in=null;
		InstanceLoadVO instanceLoadVO=null;
		String urlToSend=null;
		String httpURLPrefix="http://";
		String msgGWPass=null;
		String requestXML =null;
		String responseStr=null;
		String finalResponse="";
		String response =null;
		try {
			//msisdn= p_redemptionVO.getMsisdn();
			//msisdnPrefix = PretupsBL.getMSISDNPrefix(msisdn);
			requestXML = this.generateRequestC2CTRFXML(p_redemptionVO);
			MessageGatewayVO messageGatewayVO=MessageGatewayCache.getObject(PretupsI.REQUEST_SOURCE_TYPE_EXTGW);
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "messageGatewayVO: "+messageGatewayVO);
			}

			if(messageGatewayVO==null) {
				throw new BTSLBaseException("RunLMSForTargetCreditNew",METHOD_NAME,PretupsErrorCodesI.ERROR_NOTFOUND_MESSAGEGATEWAY);
			}
			RequestGatewayVO requestGatewayVO=messageGatewayVO.getRequestGatewayVO(); 
			if(requestGatewayVO==null) {
				throw new BTSLBaseException("RunLMSForTargetCreditNew",METHOD_NAME,PretupsErrorCodesI.ERROR_NOTFOUND_REQMESSAGEGATEWAY);
			}

			if(!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getStatus())) {
				throw new BTSLBaseException(this, METHOD_NAME, "c2stranfer.c2srecharge.error.messagegatewaynotactive","c2sRecharge");
			} else if(!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getRequestGatewayVO().getStatus())) {
				throw new BTSLBaseException(this, METHOD_NAME, "c2stranfer.c2srecharge.error.reqmessagegatewaynotactive","c2sRecharge");
			}

			//If Encrypted Password check box is checked. i.e. send password in request as encrypted.
			if(messageGatewayVO.getReqpasswordtype().equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
				msgGWPass =BTSLUtil.decryptText(messageGatewayVO.getRequestGatewayVO().getPassword());
			} else {
				msgGWPass=messageGatewayVO.getRequestGatewayVO().getPassword();
			}

			String networkCode=p_redemptionVO.getNetworkID();
			String instanceID=null;
			//Changed to handle multiple SMS servers for C2S and P2P on 20/07/06
			//if(LoadControllerCache.getNetworkLoadHash()!=null &&  LoadControllerCache.getNetworkLoadHash().containsKey(LoadControllerCache.getInstanceID()+"_"+networkCode))
			//	smsInstanceID=((NetworkLoadVO)(LoadControllerCache.getNetworkLoadHash().get(LoadControllerCache.getInstanceID()+"_"+networkCode))).getC2sInstanceID();
			instanceID=Constants.getProperty("INSTANCE_ID");
			if(BTSLUtil.isNullString(instanceID)) {
				_logger.error("RunLMSForTargetCreditNew"," Not able to get the instance ID for the network="+networkCode+" where the request for o2c needs to be send");
				throw new BTSLBaseException("RunLMSForTargetCreditNew",METHOD_NAME,"lms.point.redemption.form.error.unableto.initiateo2c","");
			}
			instanceLoadVO=LoadControllerCache.getInstanceLoadForNetworkHash(instanceID+"_"+networkCode+"_"+PretupsI.REQUEST_SOURCE_TYPE_SMS);

			StringBuffer sbf1 = new StringBuffer();

			sbf1.append("REQUEST_GATEWAY_CODE="+messageGatewayVO.getGatewayCode());
			sbf1.append("&REQUEST_GATEWAY_TYPE="+messageGatewayVO.getGatewayType());
			sbf1.append("&LOGIN="+messageGatewayVO.getRequestGatewayVO().getLoginID());
			sbf1.append("&PASSWORD="+msgGWPass );
			sbf1.append("&SERVICE_PORT="+messageGatewayVO.getRequestGatewayVO().getServicePort());
			sbf1.append("&SOURCE_TYPE="+PretupsI.REQUEST_SOURCE_TYPE_EXTGW);
			String authorization = sbf1.toString();	
			if(instanceLoadVO==null)
			{
				_logger.error("RunLMSForTargetCreditNew"," Not able to get the instance detaile for the network="+networkCode+" where the request for o2c needs to be send");
				throw new BTSLBaseException("RunLMSForTargetCreditNew",METHOD_NAME,PretupsErrorCodesI.INSTANCE_CODE_NOT_FOUND);
			} else {
				urlToSend=httpURLPrefix+instanceLoadVO.getHostAddress()+":"+instanceLoadVO.getHostPort()+Constants.getProperty("CHANNEL_WEB_RECHARGE_SERVLET")+"?";	
			}
			String encodeUrl = URLEncoder.encode(urlToSend);
			URL url = new URL(urlToSend);
			URLConnection uc = url.openConnection();
			con = (HttpURLConnection) uc;
			con.addRequestProperty("Content-Type", "text/xml");
			con.addRequestProperty("Authorization", authorization);
			con.setUseCaches(false);
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			try(BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF8"));) {
				
				
				// Send data
				wr.write(requestXML);
				wr.flush();
				// Get response
				in= new BufferedReader(new InputStreamReader(con.getInputStream()));
				while ((responseStr = in.readLine()) !=null)
				{finalResponse=finalResponse+responseStr ;}
				wr.close();
				in.close();
			} catch (Exception e) {
				_logger.errorTrace(METHOD_NAME,e);
			} finally {
				if(con != null) {
					con.disconnect();
				}
			}

			if(!BTSLUtil.isNullString(finalResponse)) {
				int index=finalResponse.indexOf("<TXNID>");
				o2cFocTxnId=finalResponse.substring(index+"<TXNID>".length(),finalResponse.indexOf("</TXNID>",index));
				index=finalResponse.indexOf("<TXNSTATUS>");
				String focO2cTxnStatus=finalResponse.substring(index+"<TXNSTATUS>".length(),finalResponse.indexOf("</TXNSTATUS>",index));

				response=o2cFocTxnId +"@"+focO2cTxnStatus;
				if(_logger.isDebugEnabled()) {
					_logger.debug("RunLMSForTargetCreditNew", "initaiateC2CTransferRequest: C2CTxnId="+o2cFocTxnId+" C2CTxnStatus= "+focO2cTxnStatus);
				}
			} else {
				o2cFocTxnId=null;
				response=null;
			}
		} catch(BTSLBaseException bse) {
			_logger.errorTrace(METHOD_NAME,bse);
			p_redemptionVO.setErrorCode(PretupsErrorCodesI.INSTANCE_CODE_NOT_FOUND);
			throw new BTSLBaseException("RunLMSForTargetCreditNew",METHOD_NAME,PretupsErrorCodesI.INSTANCE_CODE_NOT_FOUND);
		} catch(Exception e) {
			_logger.errorTrace(METHOD_NAME,e);
		} finally {
			if(in != null)
    		{
    			try{
            		if(in != null){
            			in.close();	
            		}
            	}catch(Exception e){
            		_logger.errorTrace("RunLMSForTargetCreditNew:initaiateC2CTransferRequest()", e);
            	}
    		}
			if(_logger.isDebugEnabled()) {
				_logger.debug("initaiateFocRedemptionRequest" , "Exiting response=" +response);
			}
		}
		return response;
	}

	public String generateRequestC2CTRFXML(LoyaltyPointsRedemptionVO p_redempVO) {
		final String METHOD_NAME = "generateRequestC2CTRFXML";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME, "Entered");
		}
		String requesStr=null;
		StringBuffer sbf=null;
		try {
			SecureRandom exttxnno = new SecureRandom();


			sbf=new StringBuffer();
			sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command1.0//EN\" \"xml/command.dtd\"><COMMAND>");	
			sbf.append("<TYPE>EXC2CTRFREQ</TYPE>"); 
			sbf.append("<DATE>"+BTSLUtil.getDateStringFromDate(new Date())+"</DATE>");
			sbf.append("<EXTNWCODE>"+p_redempVO.getNetworkID()+"</EXTNWCODE>");
			sbf.append("<MSISDN1>"+p_redempVO.getParentMsisdn()+"</MSISDN1>");
			sbf.append("<PIN>"+ new CryptoUtil().decrypt(p_redempVO.getParentEncryptedPin(),Constants.KEY)+"</PIN>");
			sbf.append("<LOGINID></LOGINID>");
			sbf.append("<PASSWORD></PASSWORD>");
			if(!BTSLUtil.isNullString(p_redempVO.getExternalCode())) {
				sbf.append("<EXTCODE>"+p_redempVO.getExternalCode()+"</EXTCODE>");
			} else {
				sbf.append("<EXTCODE></EXTCODE>");
			}
			sbf.append("<EXTREFNUM>"+ exttxnno.nextInt(10000)+"</EXTREFNUM>");
			sbf.append("<MSISDN2>"+p_redempVO.getMsisdn()+"</MSISDN2>");
			sbf.append("<LOGINID2></LOGINID2>");
			sbf.append("<EXTCODE2></EXTCODE2>");
			sbf.append("<PRODUCTS>");
			sbf.append("<PRODUCTCODE>"+p_redempVO.getProductShortCode()+"</PRODUCTCODE>");
			sbf.append("<QTY>"+p_redempVO.getC2cContribution()+"</QTY>");
			sbf.append("</PRODUCTS>");
			sbf.append("<LANGUAGE1></LANGUAGE1>");
			sbf.append("</COMMAND>");
			requesStr = sbf.toString();
		} catch(Exception ex) {
			_logger.errorTrace(METHOD_NAME,ex);
		} finally {
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME , "Exiting requesStr=" +requesStr);
			}
		}
		return requesStr;
	}

	private static void makeQuery(Connection p_con) throws BTSLBaseException {
		final String METHOD_NAME = "makeQuery";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME, "Entered");
		}
		try {
			String query=null;
			StringBuffer qryBuffer= new StringBuffer();
			
			
			//check user details exist in BONUS table, before adding new entries, checkUserAlreadyExist()
			qryBuffer= new StringBuffer();
			RunLMSForTargetCreditNewQry selectQueryBuffer = (RunLMSForTargetCreditNewQry)
					ObjectProducer.getObject(QueryConstants.RUN_LMS_TARGET_CREDIT_NEW_QRY, QueryConstants.QUERY_PRODUCER);
			
			query=selectQueryBuffer.checkUserDetailsExistInBonusTable();
		
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME,"Query: "+query);
			}
			_checkUserExistStmt=p_con.prepareStatement(query);			
			//if user details does not exist in bonus table, we check if it has any previous record so that we can add points in accumulated
			qryBuffer= new StringBuffer();
			qryBuffer.append(" select accumulated_points from bonus ");
			qryBuffer.append(" where USER_ID_OR_MSISDN=? AND profile_type='LMS' ");
			qryBuffer.append(" AND product_code=?  and points_date = ");
			qryBuffer.append(" (select max (points_date) from bonus where USER_ID_OR_MSISDN=? AND profile_type=? ");
			qryBuffer.append(" AND product_code=? ) ");
	        query=qryBuffer.toString();
	        if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME,"Query: "+query);
			}
	        _checkUserExistLastDateStmt=p_con.prepareStatement(query);
	        //insert entries in bonus table if user does not exist, saveBonus()
			qryBuffer= new StringBuffer();
			qryBuffer.append(" INSERT INTO BONUS (profile_type,user_id_or_msisdn,points, ");
			qryBuffer.append(" bucket_code,product_code,points_date,last_redemption_id,last_redemption_on, ");
			qryBuffer.append(" last_allocation_type,last_allocated_on,created_on,created_by,modified_on, ");
			qryBuffer.append(" modified_by,transfer_id, accumulated_points,profile_id,version)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
			query=qryBuffer.toString();
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME,"Query:"+query);
			}
			_saveBonusStmt=p_con.prepareStatement(query);

			//if user exist in bonus table then update entries corresponding to user, updateBonusOfUser()
			qryBuffer= new StringBuffer();
			qryBuffer.append(" UPDATE BONUS SET ACCUMULATED_POINTS=ACCUMULATED_POINTS+?, points=?, last_allocation_type=?,last_allocated_on=?, ");
			qryBuffer.append(" transfer_id=? ,LAST_REDEMPTION_ID= ?, LAST_REDEMPTION_ON=? WHERE user_id_or_msisdn=? AND profile_type='LMS' AND  ");
			qryBuffer.append(" product_code=?  AND points_date=? AND bucket_code=? ");
			query=qryBuffer.toString();
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME,"Query: "+query);
			}
			_updateBonusStmt=p_con.prepareStatement(query);

			query=null;
			qryBuffer=null;
		} catch(SQLException se) {
			_logger.error(METHOD_NAME,"SQLException: "+se.getMessage());
			_logger.errorTrace(METHOD_NAME,se);
			throw new BTSLBaseException("ActivationBonusCalculation",METHOD_NAME,PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
		} catch(Exception e) {
			_logger.error(METHOD_NAME,"Exception: "+e.getMessage());
			_logger.errorTrace(METHOD_NAME,e);
			throw new BTSLBaseException("ActivationBonusCalculation",METHOD_NAME,PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
		} finally {
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Exiting..... ");
			}
		}
	}
	
	private static int saveBonus(ActivationBonusVO p_bonusVO) throws BTSLBaseException {
		final String METHOD_NAME = "saveBonus";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME, "Entered p_processingDate: p_bonusVO: "+p_bonusVO.toString());
		}
		int count=0;
		try {
			_saveBonusStmt.clearParameters();
			_saveBonusStmt.setString(1,p_bonusVO.getProfileType());
			_saveBonusStmt.setString(2,p_bonusVO.getUserId());
			_saveBonusStmt.setDouble(3,p_bonusVO.getPoints());
			_saveBonusStmt.setString(4,p_bonusVO.getBucketCode());
			_saveBonusStmt.setString(5,p_bonusVO.getProductCode());
			_saveBonusStmt.setDate(6,BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getPointsDate()));
			_saveBonusStmt.setString(7,p_bonusVO.getLastRedemptionId());
			_saveBonusStmt.setDate(8,BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getLastRedemptionDate()));
			_saveBonusStmt.setString(9,p_bonusVO.getLastAllocationType());
			_saveBonusStmt.setDate(10,BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getLastAllocationdate()));
			_saveBonusStmt.setDate(11,BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getCreatedOn()));
			_saveBonusStmt.setString(12,p_bonusVO.getCreatedBy());
			_saveBonusStmt.setDate(13,BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getModifiedOn()));
			_saveBonusStmt.setString(14,p_bonusVO.getModifiedBy());
			_saveBonusStmt.setString(15,p_bonusVO.getTransferId());
			_saveBonusStmt.setLong(16,p_bonusVO.getAccumulatedPoints());
			//Brajesh
			_saveBonusStmt.setString(17,p_bonusVO.getSetId());
			_saveBonusStmt.setString(18,p_bonusVO.getVersion());
			
			//
			count=_saveBonusStmt.executeUpdate();
		} catch(SQLException se) {
			_logger.error(METHOD_NAME,"SQLException: "+se.getMessage());
			_logger.errorTrace(METHOD_NAME,se);
			throw new BTSLBaseException("ActivationBonusCalculation",METHOD_NAME,PretupsErrorCodesI.INSERTION_ERROR_BONUS_TABLE);
		} catch(Exception e) {
			_logger.error(METHOD_NAME,"Exception: "+e.getMessage());
			_logger.errorTrace(METHOD_NAME,e);
			throw new BTSLBaseException("ActivationBonusCalculation",METHOD_NAME,PretupsErrorCodesI.INSERTION_ERROR_BONUS_TABLE);
		} finally {
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Exiting..... count: "+count);
			}
		}
		return count;
	} 
	
	private static ActivationBonusVO checkUserAlreadyExist(String p_userId, Date p_currentDate, String p_productCode) throws BTSLBaseException	{
		final String METHOD_NAME = "checkUserAlreadyExist";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME, "Entered p_useId: "+p_userId+" p_processedUpto: "+p_currentDate+" p_productCode: "+p_productCode);
		}
		ActivationBonusVO bonusVO=null;
		ResultSet rst=null;
		try {
			//Brajesh
			LookupsVO lookupsVO = new LookupsVO();
			lookupsVO = (LookupsVO)LookupsCache.getObject(PretupsI.BUCKET_CODE, PretupsI.BUCKET_CODE_VOL);
			
			//
			_checkUserExistStmt.clearParameters();
			_checkUserExistStmt.setString(1,p_userId);
			_checkUserExistStmt.setString(2,p_productCode);
			_checkUserExistStmt.setDate(3,BTSLUtil.getSQLDateFromUtilDate(p_currentDate));
		    _checkUserExistStmt.setString(4,lookupsVO.getLookupName());
			rst=_checkUserExistStmt.executeQuery();
			if(rst.next())	{
				bonusVO= new ActivationBonusVO();
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
		} catch(SQLException se) {
			_logger.error(METHOD_NAME,"SQLException: "+se.getMessage());
			_logger.errorTrace(METHOD_NAME,se);
			throw new BTSLBaseException("ActivationBonusCalculation",METHOD_NAME,PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
		} catch(Exception e) {
			_logger.error(METHOD_NAME,"SQLException: "+e.getMessage());
			_logger.errorTrace(METHOD_NAME,e);
			throw new BTSLBaseException("RunLMSForTargetCreditNew",METHOD_NAME,PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
		} finally {
			if(rst!=null) {
				try {
					rst.close();
				} catch (Exception ex) {
					_logger.errorTrace(METHOD_NAME,ex);
				}
			}
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Exiting.....bonusVO: "+bonusVO);
			}
		}
		return bonusVO;
	}
	
	private static ActivationBonusVO checkUserExistLastDateDetail(String p_userId, String p_productCode) throws BTSLBaseException {
		final String METHOD_NAME = "checkUserExistLastDateDetail";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME, "Entered p_userId: "+p_userId+" p_productCode: "+p_productCode);
		}
		ActivationBonusVO bonusVO=null;
		ResultSet rst=null;
		try {
			_checkUserExistLastDateStmt.clearParameters();
			_checkUserExistLastDateStmt.setString(1,p_userId);
			_checkUserExistLastDateStmt.setString(2,p_productCode);
			_checkUserExistLastDateStmt.setString(3,p_userId);
			_checkUserExistLastDateStmt.setString(4,PretupsI.LMS_PROFILE_TYPE);
			_checkUserExistLastDateStmt.setString(5,p_productCode);
			rst=_checkUserExistLastDateStmt.executeQuery();
			if(rst.next()) {
				bonusVO= new ActivationBonusVO();
				bonusVO.setAccumulatedPoints(rst.getLong("accumulated_points"));
			}

		} catch(SQLException se) {
			_logger.error(METHOD_NAME,"SQLException: "+se.getMessage());
			_logger.errorTrace(METHOD_NAME,se);
			throw new BTSLBaseException(METHOD_NAME,PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
		} catch(Exception e) {
			_logger.error(METHOD_NAME,"SQLException: "+e.getMessage());
			_logger.errorTrace(METHOD_NAME,e);
			throw new BTSLBaseException("RunLMSForTargetCreditNew",METHOD_NAME,PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
		} finally {
			if(rst!=null) {
				try {
					rst.close();
				} catch (Exception ex) {
					_logger.errorTrace(METHOD_NAME,ex);
				}
			}
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Exiting.....bonusVO: "+bonusVO);
			}
		}
		return bonusVO;
	}
	
	private static int updateBonusOfUser(ActivationBonusVO p_bonusVO) throws BTSLBaseException  {
		final String METHOD_NAME = "updateBonusOfUser";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME, "Entered p_bonusVO: "+p_bonusVO.toString());
		}
		int count=0;
		try {
			//Brajesh
			LookupsVO lookupsVO = new LookupsVO();
			lookupsVO = (LookupsVO)LookupsCache.getObject(PretupsI.BUCKET_CODE, PretupsI.BUCKET_CODE_VOL);
			//
			_updateBonusStmt.clearParameters();
			int i=1;
			_updateBonusStmt.setDouble(i++,p_bonusVO.getPoints());
			_updateBonusStmt.setDouble(i++,p_bonusVO.getPoints());
			_updateBonusStmt.setString(i++,p_bonusVO.getLastAllocationType());
			_updateBonusStmt.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getLastAllocationdate()));
			_updateBonusStmt.setString(i++,p_bonusVO.getTransferId());
			_updateBonusStmt.setString(i++,"");
			_updateBonusStmt.setString(i++,"");
			_updateBonusStmt.setString(i++,p_bonusVO.getUserId());
			_updateBonusStmt.setString(i++,p_bonusVO.getProductCode());
			_updateBonusStmt.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getPointsDate()));
			//Brajesh
			_updateBonusStmt.setString(i++,p_bonusVO.getBucketCode());
			count=_updateBonusStmt.executeUpdate();
		} catch(SQLException se) {
			_logger.error(METHOD_NAME,"SQLException: "+se.getMessage());
			_logger.errorTrace(METHOD_NAME,se);
			throw new BTSLBaseException("RunLMSForTargetCreditNew",METHOD_NAME,PretupsErrorCodesI.BONUS_TABLE_NOT_UPDATED);
		} catch(Exception e) {
			_logger.error("checkUserAlreadyExist","SQLException: "+e.getMessage());
			_logger.errorTrace(METHOD_NAME,e);
			throw new BTSLBaseException("RunLMSForTargetCreditNew",METHOD_NAME,PretupsErrorCodesI.BONUS_TABLE_NOT_UPDATED);
		} finally {
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Exiting..... count: "+count);
			}
		}
		return count;
	}

	private static Date getWeeklyBonusTargetCreditFromDate(Date fromDate,Date currentDate) {
		String METHOD_NAME = "getWeeklyBonusTargetCreditFromDate";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME," fromDate = "+fromDate+" , currentDate = "+currentDate);
		}
		Date date  = null;		
		int noOfReminderDays = 0;
		try {
			noOfReminderDays = ((BTSLUtil.getDifferenceInUtilDates(fromDate, currentDate))%7);
			if(noOfReminderDays == 0 && BTSLUtil.getDifferenceInUtilDates(fromDate, currentDate)>0) {
				noOfReminderDays = 7;
			}
			date = BTSLUtil.getDifferenceDate(currentDate,-noOfReminderDays) ;
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME," date = "+date);
			}
		} catch (Exception e) {
			_logger.errorTrace(METHOD_NAME, e);
		} finally {
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME," Exiting : date = "+date+", noOfReminderDays = "+noOfReminderDays);
			}
		}
		return date;
	}
	
	private static Date getMonthlyBonusTargetCreditFromDate(Date fromDate,Date currentDateValue) {
		String METHOD_NAME = "getMonthlyTranscationFronDate";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME," fromDate = "+fromDate+" , currentDateValue = "+currentDateValue);
		}
		Calendar cal= BTSLDateUtil.getInstance();
		Calendar cal1= BTSLDateUtil.getInstance();
		Calendar cal2= BTSLDateUtil.getInstance();
		Date currentDate= null;
		Date date  = cal.getTime();		
		try {
			currentDate = currentDateValue;
			cal.setTime(fromDate); //LMS Profile Creation Date
			date  = cal.getTime();
			cal2.setTime(fromDate);
			cal1.setTime(currentDate);//Current Date
			int diff=0;
			diff=cal1.compareTo(cal2);
			while(diff > 0) {
				cal2.add(Calendar.MONTH, 1); //Add One Month
				diff=cal1.compareTo(cal2);
				if(diff > 0) {
					date  = cal2.getTime();
				}

			}
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME," date = "+date);
			}
		} catch (Exception e) {
			_logger.errorTrace(METHOD_NAME, e);
		} finally {
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME," Exiting");
			}
		}
		return date;
	}
	
	private static int getNoOfDaysInCreatedLMSProfileOnMonth(Date fromDate) {
		String METHOD_NAME = "getNoOfDaysInCreatedLMSProfileOnMonth";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME," fromDate = "+fromDate);
		}
		Calendar cal= BTSLDateUtil.getInstance();
		int days =0;	
		try {
			cal.setTime(fromDate); //LMS Profile Creation Date
			days = cal.getActualMaximum(Calendar.DAY_OF_MONTH); 
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME," days = "+days);
			}
		} catch (Exception e) {
			_logger.errorTrace(METHOD_NAME, e);
		} finally {
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME," Exiting");
			}
		}
		return days;
	}

}
