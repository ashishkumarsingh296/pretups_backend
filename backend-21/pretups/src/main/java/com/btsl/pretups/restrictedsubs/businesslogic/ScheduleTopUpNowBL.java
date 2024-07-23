package com.btsl.pretups.restrictedsubs.businesslogic;



/**
 * ScheduleTopUpNowBL.java
 * Name                                 Date            History
 *------------------------------------------------------------------------
 * Hargovind Karki			              27/11/2015         Initial Creation
 *------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 * This class is responsible for the Scheduled TOP-UP Process of the restricted subscribers.
 */


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ErrorMap;
import com.btsl.common.MasterErrorList;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.loadcontroller.LoadControllerCache;
import com.btsl.loadcontroller.NetworkLoadVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.logging.ScheduledBatchLogger;
import com.btsl.pretups.channel.logging.ScheduledTopUpLogger;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LocaleMasterDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduledBatchesDAO;
import com.btsl.pretups.scheduletopup.process.BatchFileParserI;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.NumberConstants;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;


public class ScheduleTopUpNowBL
{ 
    private static final Log log = LogFactory.getLog(ScheduleTopUpNowBL.class.getName());
    private long _sleepTime; //Time interval for enquiry with C2STransfer table to get the final status.
    private int _numberOfScheduledDays;//Number of back days from which scheduled batch is picked up(till current date0
    private String _SCHDULE_TOP_UP_URL;//Recharge URL
    private int _RST_NO_SLEEP_ENQUIRY;//Number of times scheduled process will enquire with C2STransfer table to get the final status.
    private int _RST_CON_REFUSE_COUNTER;//After this allowed number if consecutive failure occurs while creating connection process will abort.
	private int _SMS_SERVER_CONNECT_TIMEOUT;//SMS server connect time out.
	private int _SMS_SERVER_READ_TIMEOUT;//SMS server read time out.
    private String _instanceID; //Store the instanceID to load the details of running instance.
	private Date _date = null;
	private ProcessStatusVO _processStatusVO = null;
	private ProcessStatusDAO _processStatusDAO = null;
	private LocaleMasterDAO _localeMasterDAO=null;
	private boolean _processStatusOK = false;
	private static String _batchType=null;
	private BatchFileParserI _batchFileParserI=null;
	private static String _batchID=null;
	String finalResponse="";
	
	public ScheduleTopUpNowBL()
	{
	    super();
		_processStatusDAO = new ProcessStatusDAO();
		_localeMasterDAO=new LocaleMasterDAO();
	}
	/**
	 * This method is used to get the values from constants.prop
	 * @throws BTSLBaseException
	 */
	
	
	private void loadConstantValues() throws BTSLBaseException
	{
	    final String METHOD_NAME="loadConstantValues";
		try
	    {
	        if(log.isDebugEnabled()) {
				log.debug("loadConstantValues","Entered");
			}
		    //sleepTime is used to wait for enquiry with the C2STransfer after getting the 'Validation response'.
		    String str =Constants.getProperty("RST_MAX_DAYS_SCH_BATCH");//Number of back days from which scheduled batch is picked up(till current date0
		    _numberOfScheduledDays = Integer.parseInt(str);
		    String noSleeptimes = Constants.getProperty("RST_NO_SLEEP_ENQUIRY");
		    _RST_NO_SLEEP_ENQUIRY = Integer.parseInt(noSleeptimes);
		    String connectionRefuseCounter =Constants.getProperty("RST_CON_REFUSE_COUNTER");
		    _RST_CON_REFUSE_COUNTER=Integer.parseInt(connectionRefuseCounter);     
		    _sleepTime	= Long.parseLong(Constants.getProperty("RST_SLEEP_ENQUIRY_TIME")); 
		    _SCHDULE_TOP_UP_URL = Constants.getProperty("SCHDULE_TOP_UP_URL");
			_SMS_SERVER_CONNECT_TIMEOUT=Integer.parseInt(Constants.getProperty("SMS_SERVER_CONNECT_TIMEOUT"));
			_SMS_SERVER_READ_TIMEOUT=Integer.parseInt(Constants.getProperty("SMS_SERVER_READ_TIMEOUT"));
			//_instanceID = Constants.getProperty("RST_INSTANCE_ID");
			_instanceID = Constants.getProperty("DEF_INSTANCE_ID");
			
	    }
	    catch(Exception e)
	    {
	        log.errorTrace(METHOD_NAME,e);
	        log.error("loadConstantValues","Exception e="+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RestrictedSubscriberTopUp[loadConstantValues]","","","","Exception:"+e.getMessage());
	        throw new BTSLBaseException("RestrictedSubscriberTopUp","loadConstantValues",e.getMessage());
	    }
	    finally
	    {
	        if(log.isDebugEnabled()) {
				log.debug("loadConstantValues","Exiting: numberOfScheduledDays="+_numberOfScheduledDays+" sleepTime="+_sleepTime +" _RST_CON_REFUSE_COUNTER"+_RST_CON_REFUSE_COUNTER+"_SCHDULE_TOP_UP_URL"+_SCHDULE_TOP_UP_URL+" _instanceID = "+_instanceID);
			}
	    }
	    
	}
	/**
	 * This method is used to check the Process Status.
 	 * If there is no entry for corresponding processID STOP the process.
	 * If the status is 'C' set processStatusOK as True.
	 * If the status is 'U' check the exipiry if it expires,update status as 'U' and set processStatusOK as True.
	 * In case of expiry send the alarm also.
	 * @param	Connection p_con
	 * @throws	BTSLBaseException
	 */
	private void checkProcessUnderProcess(Connection p_con) throws BTSLBaseException
	{	
	    if(log.isDebugEnabled()) {
			log.debug("checkProcessUnderProcess","Entered ");
		}
	    final String METHOD_NAME="checkProcessUnderProcess";
		long dateDiffInHour=0;
		int successC = 0;
		try
		{
			//load the Scheduler information - start date and status of scheduler
			_processStatusVO = (ProcessStatusVO)_processStatusDAO.loadProcessDetail(p_con,ProcessI.REST_SCH_TOPUP_PROCESSID);
			
		    //Check Process Entry,if no entry for the process throw the exception and stop the process
			if(_processStatusVO==null) {
				throw new BTSLBaseException("RestrictedSubscriberTopUp","checkProcessUnderProcess",PretupsErrorCodesI.PROCESS_ENTRY_NOT_FOUND);
			} else if(ProcessI.STATUS_COMPLETE.equals(_processStatusVO.getProcessStatus()))
			{
		        //set the current date while updating the start date of process
			    _processStatusVO.setStartDate(_date);
			    _processStatusVO.setProcessStatus(ProcessI.STATUS_UNDERPROCESS);
			    successC=_processStatusDAO.updateProcessDetail(p_con,_processStatusVO);
			    if(successC>0) {
					_processStatusOK=true;
				} else {
					throw new BTSLBaseException("RestrictedSubscriberTopUp","checkProcessUnderProcess",PretupsErrorCodesI.PROCESS_ERROR_UPDATE_STATUS);
				}
			}
		    //if the scheduler status is UnderProcess check the expiry of scheduler.
			else if(ProcessI.STATUS_UNDERPROCESS.equals(_processStatusVO.getProcessStatus()))
			{
			    //dateDiffInHour = BTSLUtil.getDifferenceInUtilDates(_processStatusVO.getStartDate(),_date);
			    if(_processStatusVO.getStartDate()!=null) {
					dateDiffInHour = getDiffOfDateInHour(_date,_processStatusVO.getStartDate());
				} else {
					throw new BTSLBaseException("RestrictedSubscriberTopUp","checkProcessUnderProcess","Process Start Date is NULL");
				}
			    if(log.isDebugEnabled()) {
					log.debug("checkProcessUnderProcess","dateDiffInHour="+dateDiffInHour +" _processStatusVO.getExpiryTime() = "+_processStatusVO.getExpiryTime());
				}
			    if(dateDiffInHour >=_processStatusVO.getExpiryTime())
			    {
			        //set the current date while updating the start date of process
			        _processStatusVO.setStartDate(_date);
			        successC=_processStatusDAO.updateProcessDetail(p_con,_processStatusVO);
					if(successC>0) {
						_processStatusOK=true;
					} else {
						throw new BTSLBaseException("RestrictedSubscriberTopUp","checkProcessUnderProcess",PretupsErrorCodesI.PROCESS_ERROR_UPDATE_STATUS);
					}
			    } else {
					throw new BTSLBaseException("RestrictedSubscriberTopUp","checkProcessUnderProcess",PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
				}
			}
		}
		catch(BTSLBaseException be)
		{
			log.error("checkProcessUnderProcess", "BTSLBaseException : " + be);
			throw be;
		}
		catch(Exception e)
		{
			log.error("checkProcessUnderProcess", "Exception : " + e);
			log.errorTrace(METHOD_NAME,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RestrictedSubscriberTopUp[invokeProcessByStatus]","processStatusVO.getProcessID()"+_processStatusVO.getProcessID(),"","","Exception:"+e.getMessage());
		}
		finally
		{
			if(log.isDebugEnabled()) {
				log.debug("checkProcessUnderProcess", "Exiting _processStatusVO="+_processStatusVO);
			}
		}
	}
	/**
	 * Used to get the difference of date in Minutes
	 * @param p_currentDate Date
	 * @param p_startDate Date
	 * @return long
	 * @throws BTSLBaseException
	 */
	private long getDiffOfDateInHour(Date p_currentDate, Date p_startDate)
	{
		if(log.isDebugEnabled()) {
			log.debug("getDiffOfDateInHour", "Entered p_currentDate="+p_currentDate+" p_startDate: "+p_startDate);
		}
		final String METHOD_NAME="getDiffOfDateInHour";
		long diff=0;
	    try
	    {
			diff=((p_currentDate.getTime()-p_startDate.getTime())/(1000*60*60));
	    }
	    catch(Exception e)
	    {
	        log.errorTrace(METHOD_NAME,e);
			log.error("getDiffOfDateInHour", "Exception : "+e.getMessage());
		}
		if(log.isDebugEnabled()) {
			log.debug("getDiffOfDateInHour", "Exiting diff="+diff);
		}
		return diff;
	}
	/**
	 * This method is invoked by Main Method.
	 * It is used to decide whether to invoke the process method or not.
	 * This decision is made by maintaining a single entry of processID,date-time and status,in PROCESS_STATUS.
	 * For this it interaly calls the checkProcessUnderProcess method and based on the parameter processStatusOK it calls the process Method.
	 * @param errorMap TODO
	 * @throws BTSLBaseException
	 */
	public void invokeProcessByStatus(Connection p_con,String scheduleNowType , ScheduleBatchMasterVO scheduleBatchMasterVO, ErrorMap errorMap) throws BTSLBaseException
	{	
		String p_Batch_ID = scheduleBatchMasterVO.getBatchID(); 
		if(log.isDebugEnabled()) {
			log.debug("invokeProcessByStatus","Entered with  scheduleNowType : "+scheduleNowType+" , p_Batch_ID : "+p_Batch_ID);
		}
	    final String METHOD_NAME="invokeProcessByStatus";

		int successU = 0;
		try
		{
		    _date = new Date();
		    _batchType=scheduleNowType;
		    if(!BTSLUtil.isNullString(p_Batch_ID))
		    	_batchID=p_Batch_ID;
		    else
		    	_batchID = PretupsI.ALL;
			//load the constatn values from constants.prop
		    loadConstantValues();

			//checkProcessUnderProcess(p_con);
			
			//If the schedulerOK is TRUE call the process
			//if(_processStatusOK)
			//{
			    //Commiting the status of process status as 'U-Under Process' and call the process.
				p_con.commit();
			    process(p_con, scheduleBatchMasterVO, errorMap);
			//}
		}
		catch(BTSLBaseException be)
		{
			log.error("invokeProcessByStatus", "BTSLBaseException be= " + be);
		    if(p_con!=null) {
				try {p_con.rollback();} catch (Exception e1){log.errorTrace(METHOD_NAME,e1);}
			}
			throw be;
		}
		catch(Exception e)
		{
		    log.error("invokeProcessByStatus", "Exception be= " + e);
			log.errorTrace(METHOD_NAME,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RestrictedSubscriberTopUp[invokeProcessByStatus]","processStatusVO.getProcessID()"+_processStatusVO.getProcessID(),"","","Exception:"+e.getMessage());
		}
		finally
		{
		   /* try
		    {
			    //Setting the process status as 'C-Complete' if the _processStatusOK is true
			    if(_processStatusOK)
			    {
			        _date = new Date();
				    _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
				    _processStatusVO.setExecutedOn(_date);
				    _processStatusVO.setExecutedUpto(_date);
				    successU = _processStatusDAO.updateProcessDetail(p_con,_processStatusVO);
				    
				    //Commiting the process status as 'C-Complete'
				    if(successU>0) {
				    	p_con.commit();
					} else {
						throw new BTSLBaseException("RestrictedSubscriberTopUp","invokeProcessByStatus",PretupsErrorCodesI.PROCESS_ERROR_UPDATE_STATUS);
					}
			    }
		    }
		    catch(BTSLBaseException be)
		    {
				_log.error("invokeProcessByStatus", "BTSLBaseException be= " + be);
			    if(p_con!=null) {
					try {p_con.rollback();} catch (Exception e1){_log.errorTrace(METHOD_NAME,e1);}
				}
				throw be;
		    }
		    catch(Exception e)
		    {
				_log.errorTrace(METHOD_NAME,e);
				_log.error("invokeProcessByStatus", "Exception e= " + e);
			    if(p_con!=null) {
					try {p_con.rollback();} catch (Exception e1){_log.errorTrace(METHOD_NAME,e1);}
				}
		    }*/
			if(p_con!=null) {
				try {p_con.close();} catch (Exception e1){log.errorTrace(METHOD_NAME,e1);}
			}
			if(log.isDebugEnabled()) {
				log.debug("invokeProcessByStatus", "Exiting _processStatusVO="+_processStatusVO);
			}
		}
		
	}
	/**
	 * This method is used to 
	 * 1.load the batches
	 * 2.load the batch detail for the process
	 * 3.load the Restricted Subscriber list
	 * 4.update the status of batch
	 * 5.update the status of batch detail 
	 * @param errorMap TODO
	 * @param	Connection p_con 	
	 * 
	 * @throws	BTSLBaseException
	 */
	
	private void process(Connection p_con, ScheduleBatchMasterVO scheduleBatchMasterVo, ErrorMap errorMap)throws BTSLBaseException
	{
	    if(log.isDebugEnabled()) {
			log.debug("process","Entered ");
		}
	    final String METHOD_NAME="process";
	    ScheduledBatchesDAO 	scheduledBatchesDAO		=null;
	    ScheduleBatchMasterVO  	scheduledBatchMasterVO	=null;
	    ScheduleBatchDetailVO 	scheduledBatchDetailVO	=null;
	    ChannelUserVO	channelUserVO	=null;
	    ChannelUserDAO	channelUserDAO	=null;
	    int updateSuccessCount=0;
	    String status="";
	    boolean validate=false;
	    Date fromDateforScheduled;
	    ArrayList	batchList	=	null;
	    ArrayList	batchDetailList	=	null;
	    try
		{
			//fromDateforScheduled is back date with respect to current date and the number of days intered into constant.props. 
			fromDateforScheduled  = BTSLUtil.addDaysInUtilDate(_date,-_numberOfScheduledDays);
			if(log.isDebugEnabled()) {
				log.debug("processs ","fromDateForScheduled= "+fromDateforScheduled);
			}

			//load batch list whose scheduled_date lies between configured days(fromDateforScheduled)and Current date.
		    scheduledBatchesDAO	= new ScheduledBatchesDAO();
		    channelUserDAO		= new ChannelUserDAO();
		    RestrictedSubscriberDAO restrictedSubscriberDAO= new RestrictedSubscriberDAO();

		    //Construct a comma seprated string for the status to Load the batch list
		    status="'"+PretupsI.REST_SCH_BATCH_STATUS_SCHEDULED+"','"+PretupsI.REST_SCH_BATCH_STATUS_UNDER_PROCESS+"'";
		    
		    //load batch details according to batch type
		    batchList = scheduledBatchesDAO.loadBatchList(p_con,fromDateforScheduled,_date,status,_batchType , _batchID);
		    
		    //Check whether there is no batch scheduled
		    //if exists then iterate the list and pick up the first batch to process.before processing the selected batch,update the status as UNDER_PROCESS
		    if(batchList == null || batchList.size()<=0)
		    {
			    if(log.isDebugEnabled()) {
					log.debug("processs ","Batch List Not Found throwing error code : "+PretupsErrorCodesI.REST_SCH_NO_BATCH_SCHEDULED);
				}
	            EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"RestrictedSubscriberTopUp[process]","","","","No scheduled batches available for topup processing : "+PretupsErrorCodesI.REST_SCH_NO_BATCH_SCHEDULED+" for dates from "+fromDateforScheduled+" to "+_date);
		        throw new BTSLBaseException("RestrictedSubscriberTopUp","process",PretupsErrorCodesI.REST_SCH_NO_BATCH_SCHEDULED);
		    }
		     Date newdate =new Date();
		     for(int j=0,listSize=batchList.size();j<listSize;j++)
		    {
		        try
		        {
		            scheduledBatchMasterVO = (ScheduleBatchMasterVO) batchList.get(j);
		            
		            //for logging set the startDate and time for the Batch
		            scheduledBatchMasterVO.setStartDateOfBatch(BTSLUtil.getTimestampFromUtilDate(newdate));
		            scheduledBatchMasterVO.setProcessedOn(newdate);	
		            scheduledBatchMasterVO.setExecutedIterations(scheduledBatchMasterVO.getExecutedIterations() + 1);
		            //updating batch master status as 'U-Under Process and Commiting.
		            scheduledBatchMasterVO.setStatus(PretupsI.REST_SCH_BATCH_STATUS_UNDER_PROCESS);
		            updateSuccessCount = scheduledBatchesDAO.updateBatchStatus(p_con,scheduledBatchMasterVO);
		            if(updateSuccessCount>0) {
						p_con.commit();
					} else {
						throw new BTSLBaseException("RestrictedSubscriberTopUp","process",PretupsErrorCodesI.REST_SCH_ERR_BATCH_MASTER_UPDATION);
					}
					    //load channelUserDetail who has scheduled the Batch (BatchMaster)initiated_id=(User)user_id 
		            channelUserVO = channelUserDAO.loadChannelUserByUserID(p_con,scheduledBatchMasterVO.getInitiatedBy());
		            if(channelUserVO!=null)
		            {
		                //Getting sender locale
		                Locale senderLocale =null;
		                try
		                {
		                    senderLocale = new Locale(channelUserVO.getUserPhoneVO().getPhoneLanguage(),channelUserVO.getUserPhoneVO().getCountry());
		                }
		                catch(Exception e1)
						{
							senderLocale=new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
							log.errorTrace(METHOD_NAME, e1);
						}
						scheduledBatchMasterVO.setSenderLocale(senderLocale);
						scheduledBatchMasterVO.setSenderMsisdn(((UserPhoneVO)channelUserVO.getUserPhoneVO()).getMsisdn());
		            } else {
						throw new BTSLBaseException("RestrictedSubscriberTopUp","process",PretupsErrorCodesI.REST_SCH_ERROR_CHANNEL_USER_NOT_EXIST);
					}
				    //Load and iterate batch detail list and pick up one batch detail for the process of request.
		            if(PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(scheduledBatchMasterVO.getBatchType())) {
						batchDetailList	=	scheduledBatchesDAO.loadBatchDetailsList(p_con,scheduledBatchMasterVO.getBatchID());
					} else {
						batchDetailList	=	restrictedSubscriberDAO.loadBatchDetailVOList(p_con,scheduledBatchMasterVO.getBatchID(), PretupsI.STATUS_EQUAL, PretupsI.SCHEDULE_STATUS_SCHEDULED);
					}
				    //If against the selected batch,All the msisdn has status other than 'S'
				    //Log that Batch with information and iterate the Next Batch 
				    if(batchDetailList == null ||  batchDetailList.size()<=0) {
						//for logging set that no msisdns are scheduled for that batchid in info1 of scheduledBatchMasterVO
				        scheduledBatchMasterVO.setOtherInfo1(PretupsErrorCodesI.REST_SCH_NO_MSISDN_SCHEDULED);
					} else
				    {	
				        scheduledBatchMasterVO.setTotalRecords( batchDetailList.size());
				        scheduledBatchMasterVO.setList(batchDetailList);
				        //get the object of parser class
				        ServiceKeywordCacheVO serviceKeywordCacheVO =  ServiceKeywordCache.getServiceTypeObject(scheduledBatchMasterVO.getServiceType(),PretupsI.C2S_MODULE);
		                _batchFileParserI = (BatchFileParserI)Class.forName(serviceKeywordCacheVO.getFileParser()).newInstance();
		                _date = new Date();
		                for(int i=0,sizeOfDetList=batchDetailList.size();i<sizeOfDetList;i++)
				        {
				            try
				            {
				                scheduledBatchMasterVO.setProccessedRecords(scheduledBatchMasterVO.getProccessedRecords()+1);
				                scheduledBatchMasterVO.setUnproccessedRecords(scheduledBatchMasterVO.getTotalRecords()-scheduledBatchMasterVO.getProccessedRecords());
				                scheduledBatchDetailVO = (ScheduleBatchDetailVO)batchDetailList.get(i);
				                scheduledBatchDetailVO.setExecutedIterations(scheduledBatchDetailVO.getExecutedIterations() + 1);
				                //get sender language code 
				                senderLanguageCode(p_con,scheduledBatchDetailVO,channelUserVO.getUserPhoneVO().getPhoneLanguage(),channelUserVO.getUserPhoneVO().getCountry());
				                //setting the reciever Language code
								recieverLanguageCode(p_con,scheduledBatchDetailVO);
								if(log.isDebugEnabled()) {
									log.debug("process","scheduledBatchDetailVO.getLanguageCode()="+scheduledBatchDetailVO.getLanguageCode());
								}
									
								//get language code for donor
								if(PretupsI.SERVICE_TYPE_CHANNEL_GIFT_RECHARGE.equals(scheduledBatchMasterVO.getServiceType())) {
									donorLanguageCode(p_con,scheduledBatchDetailVO);
								}
								//Setting the current status as the previous status.
				                scheduledBatchDetailVO.setPrevScheduleStatus(scheduledBatchDetailVO.getScheduleStatus());
				                scheduledBatchDetailVO.setScheduleStatus(PretupsI.REST_SCH_BATCH_STATUS_UNDER_PROCESS);
								
				                //Marking status under process and comitting transaction
				                updateSuccessCount = scheduledBatchesDAO.updateBatchDetailStatus(p_con,scheduledBatchDetailVO);
				                if(updateSuccessCount>0) {
									p_con.commit();
								} else {
									throw new BTSLBaseException("RestrictedSubscriberTopUp","process",PretupsErrorCodesI.REST_SCH_ERR_BATCH_DETAIL_UPDATION);
								}
	
							    //validate restricted msisdn detail,for status,minimum and maximum transfer amount.
				                if(PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(scheduledBatchMasterVO.getBatchType())) {
									validate=validateRestrictedMsisdnDetails(scheduledBatchMasterVO,scheduledBatchDetailVO);
								} else {
									//In case of batch recharge there is validation required as validation required in corportate. 
				                    // it is because not any threshold for this batch topup. 
				                    validate=true;
								}
				                //On successful validation of batch detail information, send the request.
				                if(validate) {
									sendRequest(p_con,scheduledBatchMasterVO,scheduledBatchDetailVO,channelUserVO, i+3, errorMap);
								} else {
									scheduledBatchDetailVO.setErrorCode(PretupsErrorCodesI.REST_SCH_SUBS_VALIDATION_FAIL);
								}
				            }//end of try block
				            catch(BTSLBaseException be)
				            {
				                if(scheduledBatchMasterVO.getConnectionRefuseCounter()==_RST_CON_REFUSE_COUNTER)
				                {
				                    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RestrictedSubscriberTopUp[process]","","","","BTSLBaseException:"+be.getMessage());
				                    throw be;
				                }
				                if(p_con!=null) {
									try {p_con.rollback();} catch (Exception e1){log.errorTrace(METHOD_NAME,e1);}
								}
				                log.error("process","BTSLBaseException:e="+be);
				                log.errorTrace(METHOD_NAME,be);
				            }
				            catch(Exception e)
				            {
								//Log the information to the BatchLogger for any Processed Msisdn,if error occurs during the proccessing.
				                scheduledBatchDetailVO.setErrorCode(PretupsErrorCodesI.REST_SCH_SUBS_VALIDATION_FAIL);
								log.error("process","Exceptin:e="+e);
								log.errorTrace(METHOD_NAME,e);
								EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RestrictedSubscriberTopUp[process]","","","","Exception:"+e.getMessage());
				            }//end of catch
				            finally
				            {
			                	//In finally Update the informations of each Msisdn in detail table,these informations are 
								//ProcessedOn,TransactionID,Status from UnderProcess to Executed,modifiedon, modified by.
				                try
				                {
				                   
									scheduledBatchDetailVO.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(_date));
									scheduledBatchDetailVO.setModifiedBy(PretupsI.SYSTEM_USER);
									scheduledBatchDetailVO.setProcessedOn(BTSLUtil.getTimestampFromUtilDate(_date));
	
									//Setting the previous status as the current status before updation
									scheduledBatchDetailVO.setPrevScheduleStatus(scheduledBatchDetailVO.getScheduleStatus());
									 if(scheduledBatchMasterVO.getIterations() > 1){
								    	 scheduledBatchDetailVO.setScheduleStatus(PretupsI.REST_SCH_BATCH_STATUS_SCHEDULED);
								    }else{
								    	scheduledBatchDetailVO.setScheduleStatus(PretupsI.REST_SCH_BATCH_STATUS_EXECUTED);
								    }
									updateSuccessCount = scheduledBatchesDAO.updateBatchDetailStatus(p_con,scheduledBatchDetailVO);
									if(updateSuccessCount>0) {
										p_con.commit();
									} else {
										throwBTSLException("RestrictedSubscriberTopUp","process",PretupsErrorCodesI.REST_SCH_ERR_BATCH_DETAIL_UPDATION);
									}
				                }
				                catch(BTSLBaseException be)
				                {
				                    log.error("process","BTSLBaseException ="+be);
				                    if(p_con!=null) {
										try {p_con.rollback();} catch (Exception e1){log.errorTrace(METHOD_NAME,e1);}
									}
				                }
				                catch(Exception e)
				                {
				                    log.error("process","Exception e="+e);
				                    log.errorTrace(METHOD_NAME,e);
				                }
				                //if(_log.isDebugEnabled())_log.debug("process","Proecessing of msisdn = "+scheduledBatchDetailVO.getMsisdn() +" End");
								if (log.isDebugEnabled()) {
									log.debug("process", "scheduledBatchDetailVO:" + scheduledBatchDetailVO +"is processed");
								}
				            }//end of finally
				        }//end of for loop,iteration of batch detail
				    }//end of else ,checking batch detail list size
				    
			    }
			    catch(BTSLBaseException be)
			    {
			        if(scheduledBatchMasterVO.getConnectionRefuseCounter()==_RST_CON_REFUSE_COUNTER)
			        {
			            log.error("process","Aborting the process because of maximum connection counter reached");
			            throw be;
			        }
			        if(p_con!=null) {
						try {p_con.rollback();} catch (Exception e1){log.errorTrace(METHOD_NAME,e1);}
					}
			        log.error("process","BTSLBaseException:e="+be);
			        log.errorTrace(METHOD_NAME,be);
			    }
			    catch(Exception e)
			    {
			        log.error("process","Exceptin:e="+e);
			        log.errorTrace(METHOD_NAME,e);
			        EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RestrictedSubscriberTopUp[process]","","","","Exception:"+e.getMessage());
			    }	
			    finally
			    {
			        try
			        {
					    //_date =new Date();
					    scheduledBatchMasterVO.setModifiedOn(BTSLUtil.getTimestampFromUtilDate(_date));
					    scheduledBatchMasterVO.setModifiedBy(PretupsI.SYSTEM_USER);
					    //Check if there are errors during the process of MSISDN's corressponding to batch.
					    scheduledBatchMasterVO.setEndDateOfBatch(BTSLUtil.getTimestampFromUtilDate(_date));
					    //After Processing one Batch update the Batch Information in Master,these informations are
					    //Status from 'U'-'E',SuccessfulCount,ProcessFailCount,ModifiedOn, ModifiedBy.
					    
					    //setting the previous status as the current status before updation,This done to check the previous status while updating
					    scheduledBatchMasterVO.setPrevStatus(scheduledBatchMasterVO.getStatus());
					    if(scheduledBatchMasterVO.getIterations() > 1){
					    	 scheduledBatchMasterVO.setStatus(PretupsI.REST_SCH_BATCH_STATUS_SCHEDULED);
					    }else{
					    	 scheduledBatchMasterVO.setStatus(PretupsI.REST_SCH_BATCH_STATUS_EXECUTED);
					    }
					    
					    updateSuccessCount = scheduledBatchesDAO.updateBatchStatus(p_con,scheduledBatchMasterVO);
					    if(updateSuccessCount>0) {
					    	scheduleBatchMasterVo.setSuccessfulCount(scheduledBatchMasterVO.getSuccessfulCount());
							p_con.commit();
						} else {
							throwBTSLException("RestrictedSubscriberTopUp","process",PretupsErrorCodesI.REST_SCH_ERR_BATCH_MASTER_UPDATION);
						}
					    ScheduledBatchLogger.log(scheduledBatchMasterVO);
					    //Generate the message to send as PushMessage using BTSLMessage.
					    BTSLMessages pushMsg = getPushMessage(scheduledBatchMasterVO);
					    //After Updating the information in Master Table send a Push message to the Batch Owner(who initiated the scheduled process).
					    if(pushMsg!=null)
					    {
					        try
					        {
					            (new PushMessage(scheduledBatchMasterVO.getSenderMsisdn(),pushMsg,scheduledBatchMasterVO.getBatchID(),"",scheduledBatchMasterVO.getSenderLocale(),scheduledBatchMasterVO.getNetworkCode())).push();
					        }
					    	catch(Exception e)
					    	{
					    	    log.error("process","Exception e"+e);
					    	}
					    }
			        }
			        catch(BTSLBaseException be)
			        {
			            log.error("process","BTSLBaseException="+be);
			            if(p_con!=null) {
							try {p_con.rollback();} catch (Exception e1){log.errorTrace(METHOD_NAME,e1);}
						}
			        }
			        catch(Exception e)
			        {
			            log.error("process","Exception e="+e);
			        }
			    }//end of finally
				    
		        }//end of for loop, iteration of BatchMaster		
		}
		catch(BTSLBaseException be)
		{
			log.error("process","BTSLBaseException:e="+be);
			throw be;
		}
		catch(Exception e)
		{
		    log.error("process","Exceptin:e="+e);
		    log.errorTrace(METHOD_NAME,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RestrictedSubscriberTopUp[process]","","","","Exception:"+e.getMessage());
		}
		finally
		{
		    if (log.isDebugEnabled()) {
				log.debug("process","Exiting");
			}
		}
	}//end of process
	/**
	 * This method is used to validate the details of subscriber these details are
	 * 1.Check the Status of the mobile number ,it should be 'Y' in corresponding RESTRICTED_MSISDNS list
	 * 2.Requested amount should not be less than that of the min_txn_amount in corresponding RESTRICTED_MSISDNS list
	 * 3.Requested amount should not be greater than that of the max_txn_amount in corresponding RESTRICTED_MSISDNS llist
	 * 
	 * @param ScheduleBatchMasterVO p_scheduledBatchMasterVO
	 * @param ScheduleBatchDetailVO p_scheduledBatchDetailVO
	 * @return boolean
	 */
	
	private boolean validateRestrictedMsisdnDetails(ScheduleBatchMasterVO p_scheduledBatchMasterVO,ScheduleBatchDetailVO p_scheduledBatchDetailVO)
	{
	    if(log.isDebugEnabled()) {
			log.debug("validateRestrictedMsisdnDetails", "Entered :: p_scheduledBatchMasterVO= "+p_scheduledBatchMasterVO+" p_scheduledBatchDetailVO= "+p_scheduledBatchDetailVO);
		}
	    final String METHOD_NAME="validateRestrictedMsisdnDetails";
		long minTxnDisplayAmountOfRest=0;
		long maxTxnDisplayAmountOfRest=0;
		long requestedAmount=0;
	    try
	    {
	        minTxnDisplayAmountOfRest = p_scheduledBatchDetailVO.getMinTxnAmount();
	        maxTxnDisplayAmountOfRest = p_scheduledBatchDetailVO.getMaxTxnAmount();
	        requestedAmount			  = p_scheduledBatchDetailVO.getAmount();
	        if(log.isDebugEnabled()) {
				log.debug("validateRestrictedMsisdnDetails","minTxnDisplayAmountOfRest= "+minTxnDisplayAmountOfRest+" maxTxnDisplayAmountOfRest"+maxTxnDisplayAmountOfRest+" requestedAmount"+requestedAmount);
			}
	        //Check if the subscriber status in restricted msisdn(stored in scheduledBatchDetailVO),it should be 'Y'.
		    if(!PretupsI.REST_SCH_SUBS_MSISDN_STATUS.equals(p_scheduledBatchDetailVO.getStatus())) {
				throw new BTSLBaseException("RestrictedSubscriberTopUp","validateRestrictedMsisdnDetails",PretupsErrorCodesI.REST_SCH_USER_IS_NOT_ACTIVE);
			}

		    //Requested amount checks. 1.Minimum transfer amount 2.Maximum transfer amount 
		    if(requestedAmount <minTxnDisplayAmountOfRest) {
				throw new BTSLBaseException("RestrictedSubscriberTopUp","validateRestrictedMsisdnDetails",PretupsErrorCodesI.REST_SCH_ERROR_AMOUNT_LESS_MINTAX_AMT);
			}
		    if(requestedAmount>maxTxnDisplayAmountOfRest) {
				throw new BTSLBaseException("RestrictedSubscriberTopUp","validateRestrictedMsisdnDetails",PretupsErrorCodesI.REST_SCH_ERROR_AMOUNT_GREATER_MAXTAX_AMT);
			}
		    if(PretupsI.RES_MSISDN_BLACKLIST_STATUS.equals(p_scheduledBatchDetailVO.getC2sPayeeStatus())) {
				throw new BTSLBaseException("RestrictedSubscriberTopUp","validateRestrictedMsisdnDetails",PretupsErrorCodesI.RM_ERROR_RESTRICTED_SUBSCRIBER_BLACKLISTED);
			}
		    
	    }
	    catch(BTSLBaseException be)
	    {
    	     // This catch block is due to the failure of validation,in this case following is done
    	     // 1.Increment Fail count in ScheduledBatch VO 
    	     // 2.Set the transfer staus FAIL in scheduledBatchDetail.
    	     // 3.Return false
		    log.error("validateRestrictedMsisdnDetails","BTSLBaseException:e="+be.getMessage());
		    log.errorTrace(METHOD_NAME,be);
    	    p_scheduledBatchMasterVO.setProcessFailedCount(p_scheduledBatchMasterVO.getProcessFailedCount()+1);
    	    p_scheduledBatchDetailVO.setTransactionStatus(PretupsErrorCodesI.REST_SCH_SUBS_VALIDATION_FAIL);
    	    p_scheduledBatchDetailVO.setErrorCode(be.getMessage());
	        return false;
	    }
	    catch(Exception e)
	    {
	         // This catch block is due to fail other than validation
	         // In this case we also do the same as above ,Increment the fail count,set the transfer status and return false.
		    log.error("validateRestrictedMsisdnDetails","Exceptin:e="+e);
		    log.errorTrace(METHOD_NAME,e);
		    p_scheduledBatchDetailVO.setErrorCode(e.getMessage());
    	    p_scheduledBatchMasterVO.setProcessFailedCount(p_scheduledBatchMasterVO.getProcessFailedCount()+1);
    	    p_scheduledBatchDetailVO.setTransactionStatus(PretupsErrorCodesI.SUBSCRIBER_VALIDATION_FAILS_INSIDE_SCHEDULED_PROCESS);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RestrictedSubscriberTopUp[validateRestrictedMsisdnDetails]","","","","Exception:"+e.getMessage());
	        return false;
	    }
	    if(log.isDebugEnabled()) {
			log.debug("validateRestrictedMsisdnDetails ","Exited returned TRUE");
		}
		return true;
	}
	/**
	 * This method is used for send the request to the Receiver servlet for top-up,for this it does the following
	 * 1.Construct Request String
	 * 2.Construct URL for the Request
	 * 3.Open connection 
	 * 4.Get the response in String.
	 * 5.Response String is stored in Map base on Key & Value
	 * 6.Check the status of the response and update the details in ScheduleBatchMasterVO 
	 * 7.Based on the response increment the success and processed fail count.
	 * @param index TODO
	 * @param errorMap TODO
	 * @param Connection p_con (used to enquiry with C2STransfer)
	 * @param ScheduleBatchMasterVO p_scheduledBatchMasterVO
	 * @param ScheduleBatchDetailVO p_scheduledBatchDetailVO
	 * @param ChannelUserVO	p_channelUserVO
	 */
	private void sendRequest(Connection p_con,ScheduleBatchMasterVO p_scheduledBatchMasterVO, ScheduleBatchDetailVO p_scheduledBatchDetailVO, ChannelUserVO p_channelUserVO, int index, ErrorMap errorMap) throws BTSLBaseException
	{
		final String METHOD_NAME = "sendRequest";
		if (log.isDebugEnabled()) {
			log.debug("sendRequest Entered ","p_scheduledBatchMasterVO= "+p_scheduledBatchMasterVO+" p_scheduledBatchDetailVO= "+p_scheduledBatchDetailVO +" p_channelUserVO= "+p_channelUserVO);
		}
		HttpURLConnection httpURLCon=null;
		BufferedReader in=null;
		C2STransferVO c2sTransferVO	=null;
		InstanceLoadVO instanceLoadVO=null;
		StringBuffer loggerMessage=null;
		String txn_status=null;
		String error_code=null;
		HashMap map=new HashMap();
		String txn_id=null;
		boolean failOnValidationFail=false;
		boolean successOnTxnSuccess=false;
		boolean failOnTxnSuccess	=false;
		boolean ambiguousCase	=false;
		NetworkPrefixVO networkPrefixVO=null;
		String msisdnPrefix=null;
		String networkCode=null;
		String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(defaultLanguage, defaultCountry);
		RowErrorMsgLists rowErrorMsgLists = new RowErrorMsgLists();
    	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<>();
		try
		{
			loggerMessage=new StringBuffer();
			//int subServiceValue=SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_SCH_CODE;
			if(log.isDebugEnabled()) {
				log.debug("sendRequest","p_scheduledBatchDetailVO.getSubService()="+p_scheduledBatchDetailVO.getSubService());
			}
			int subServiceValue = Integer.parseInt(p_scheduledBatchDetailVO.getSubService());
			String seprator=null;
			if(!BTSLUtil.isNullString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR))) {
				seprator=(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
			} else {
				seprator=" ";
			}
			MessageGatewayVO messageGatewayVO=MessageGatewayCache.getObject(PretupsI.GATEWAY_TYPE_WEB);
			if (log.isDebugEnabled()) {
				log.debug("sendRequest", "messageGatewayVO: "+messageGatewayVO);
			}
			if(messageGatewayVO==null)
			{
				//throw exception with message no gateway found
				log.error("sendRequest","**************Message Gateway not found in cache**************");
				throw new BTSLBaseException(this, "sendRequest", "Message Gateway not found in cache");
			}
			if(!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getStatus())) {
				throw new BTSLBaseException(this, "sendRequest", "Message Gateway not ACTIVE");
			} else if(!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getRequestGatewayVO().getStatus())) {
				throw new BTSLBaseException(this, "sendRequest", "Request message gateway is not active");
			}
				//Get the msisdn prefix to load the network details.
			msisdnPrefix = PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(p_scheduledBatchMasterVO.getSenderMsisdn()));
			//Get the network detail based on the msidn prefix.
			networkPrefixVO=(NetworkPrefixVO)NetworkPrefixCache.getObject(msisdnPrefix);
			//Check the value of networkprefixVO
			if(networkPrefixVO==null)
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RestrictedSubscriberTopUp[sendRequest]","","","msisdnPrefix = "+msisdnPrefix,"Reciever Network is not found"+PretupsErrorCodesI.CHNL_ERROR_RECR_NOTFOUND_RECEIVERNETWORK);
				log.error("sendRequest","p_scheduledBatchMasterVO.getConnectionRefuseCounter()="+p_scheduledBatchMasterVO.getConnectionRefuseCounter());
				throw new BTSLBaseException(this,"sendRequest",PretupsErrorCodesI.CHNL_ERROR_RECR_NOTFOUND_RECEIVERNETWORK);
			}
			networkCode=networkPrefixVO.getNetworkCode();
			String smsInstanceID=null;

			//Changed to handle multiple SMS servers for C2S and P2P on 20/07/06
			if(LoadControllerCache.getNetworkLoadHash()!=null &&  LoadControllerCache.getNetworkLoadHash().containsKey(LoadControllerCache.getInstanceID()+"_"+networkCode)) {
				smsInstanceID=((NetworkLoadVO)(LoadControllerCache.getNetworkLoadHash().get(LoadControllerCache.getInstanceID()+"_"+networkCode))).getC2sInstanceID();
			} else
			{
				log.error("sendRequest"," Not able to get the instance ID for the network="+networkCode+" where the request for recharge needs to be send");
				throw new BTSLBaseException("RestrictedSubscriberTopUp","sendRequest",PretupsErrorCodesI.REST_SCH_INSTANCE_NULL);
			}
			if(log.isDebugEnabled()) {
				log.debug("sendRequest","msisdnPrefix = "+msisdnPrefix+" networkCode = "+networkCode+" smsInstanceID="+smsInstanceID);
			}

			//Get the instance load whose source type is SMS.
			instanceLoadVO=LoadControllerCache.getInstanceLoadForNetworkHash(smsInstanceID+"_"+networkCode+"_"+PretupsI.REQUEST_SOURCE_TYPE_SMS);
			//If the instanceVO is null handle the event and stop the processing of recharge request.
			if(instanceLoadVO==null)
			{

				//For instance type Dummy(used for Apache)
				instanceLoadVO=LoadControllerCache.getInstanceLoadForNetworkHash(smsInstanceID+"_"+networkCode+"_"+PretupsI.REQUEST_SOURCE_TYPE_DUMMY);
				if(instanceLoadVO==null)
				{
 					EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RestrictedSubscriberTopUp[sendRequest]","networkCode = "+networkCode,"","Source Type = "+PretupsI.REQUEST_SOURCE_TYPE_DUMMY,"Instance is not loaded"+PretupsErrorCodesI.REST_SCH_INSTANCE_NULL);
					log.error("sendRequest","p_scheduledBatchMasterVO.getConnectionRefuseCounter()="+p_scheduledBatchMasterVO.getConnectionRefuseCounter());

				    instanceLoadVO=LoadControllerCache.getInstanceLoadForNetworkHash(smsInstanceID+"_"+networkCode+"_"+PretupsI.REQUEST_SOURCE_TYPE_WEB);
					if(instanceLoadVO==null)
					{
						EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RestrictedSubscriberTopUp[sendRequest]","networkCode = "+networkCode,"","Source Type = "+PretupsI.REQUEST_SOURCE_TYPE_SMS,"Instance is not loaded"+PretupsErrorCodesI.REST_SCH_INSTANCE_NULL);
						log.error("sendRequest","p_scheduledBatchMasterVO.getConnectionRefuseCounter()="+p_scheduledBatchMasterVO.getConnectionRefuseCounter());
						throw new BTSLBaseException("RestrictedSubscriberTopUp","sendRequest",PretupsErrorCodesI.REST_SCH_INSTANCE_NULL);
					}
					/*					
 					EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RestrictedSubscriberTopUp[sendRequest]","networkCode = "+networkCode,"","Source Type = "+PretupsI.REQUEST_SOURCE_TYPE_DUMMY,"Instance is not loaded"+PretupsErrorCodesI.REST_SCH_INSTANCE_NULL);
					_log.error("sendRequest","p_scheduledBatchMasterVO.getConnectionRefuseCounter()="+p_scheduledBatchMasterVO.getConnectionRefuseCounter());
					throw new BTSLBaseException("RestrictedSubscriberTopUp","sendRequest",PretupsErrorCodesI.REST_SCH_INSTANCE_NULL);
					 */				
				}
			}
			//added by akanksha for tigo guatemala CR
			String msgGWPass=null;
			// If Encrypted Password check box is not checked. i.e. send password in request as plain. 
			if(messageGatewayVO.getReqpasswordtype().equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
				msgGWPass =BTSLUtil.decryptText(messageGatewayVO.getRequestGatewayVO().getPassword());
			} else {
				msgGWPass=messageGatewayVO.getRequestGatewayVO().getPassword();
			}
		
			//create message for the request
			String messageFormat= _batchFileParserI.getRequestMessage(p_scheduledBatchDetailVO, p_channelUserVO,p_scheduledBatchMasterVO.getServiceType());
			//Constructing the recharge url
			String urlToSend=null;
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.HTTPS_ENABLE)).booleanValue()) {
				urlToSend = "https://";
            }
			else {
				urlToSend="http://";
			}
			
			
			urlToSend=urlToSend+instanceLoadVO.getHostAddress()+":"+instanceLoadVO.getHostPort()+_SCHDULE_TOP_UP_URL+"?MSISDN=";
			urlToSend =urlToSend+p_scheduledBatchMasterVO.getSenderMsisdn()+"&MESSAGE="+URLEncoder.encode(messageFormat);
			urlToSend =urlToSend +"&REQUEST_GATEWAY_CODE="+messageGatewayVO.getGatewayCode()+"&REQUEST_GATEWAY_TYPE="+messageGatewayVO.getGatewayType();
			urlToSend =urlToSend +"&SERVICE_PORT="+messageGatewayVO.getRequestGatewayVO().getServicePort()+"&LOGIN="+messageGatewayVO.getRequestGatewayVO().getLoginID() ;
			urlToSend =urlToSend +"&PASSWORD="+URLEncoder.encode(msgGWPass)+"&SOURCE_TYPE="+p_scheduledBatchMasterVO.getBatchID()+"&ACTIVE_USER_ID="+p_scheduledBatchMasterVO.getActiveUserId();
			if(log.isDebugEnabled()) {
				log.debug("sendRequest","--------------------------------------------");
			}
			if(log.isDebugEnabled()) {
				log.debug("sendRequest"," urlToSend = "+urlToSend);
			}
			if(log.isDebugEnabled()) {
				log.debug("sendRequest","--------------------------------------------");
				//-----------
			}
 			
			
			/*			
			 *  String urlToSend = _SCHDULE_TOP_UP_URL;
 				String tempMsg =p_scheduledBatchMasterVO.getServiceType()+seprator+p_scheduledBatchDetailVO.getMsisdn()+seprator+PretupsBL.getDisplayAmount(p_scheduledBatchDetailVO.getAmount())+seprator+subServiceValue+seprator+p_scheduledBatchDetailVO.getLanguageCode()+seprator+BTSLUtil.decryptText(p_channelUserVO.getUserPhoneVO().getSmsPin());
 				tempMsg = URLEncoder.encode(tempMsg);
 				urlToSend = urlToSend.replaceAll("sendv",p_channelUserVO.getUserPhoneVO().getMsisdn());
 				urlToSend = urlToSend.replaceAll("msg",tempMsg);
 				urlToSend = urlToSend.replaceAll("sourcetypevalue",p_scheduledBatchMasterVO.getBatchID());
			 */			
			loggerMessage.append("[SUBSERVICE : "+subServiceValue+"] "+" [ SEPRATOR"+seprator+"] ");
			loggerMessage.append("[URL : "+urlToSend+"] ");
			URL url=null;
			url=new URL(urlToSend);
		    if (log.isDebugEnabled()) {
				log.debug("sendRequest","URL: ="+url);
			}
			try
			{
				httpURLCon=(HttpURLConnection )url.openConnection();
				httpURLCon.setDoInput( true);
				httpURLCon.setDoOutput(true);
				httpURLCon.setRequestMethod("GET");
				httpURLCon.setConnectTimeout(_SMS_SERVER_CONNECT_TIMEOUT);
				httpURLCon.setReadTimeout(_SMS_SERVER_READ_TIMEOUT);
				in= new BufferedReader(new InputStreamReader(httpURLCon.getInputStream()));
			}
			catch(Exception e)
			{
			    //Increment the connection refuse counter if error occured while openning the connection.
			    p_scheduledBatchMasterVO.setConnectionRefuseCounter(p_scheduledBatchMasterVO.getConnectionRefuseCounter()+1);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RestrictedSubscriberTopUp[sendRequest]","","","","Exception:"+e.getMessage());
				log.error("sendRequest","p_scheduledBatchMasterVO.getConnectionRefuseCounter()="+p_scheduledBatchMasterVO.getConnectionRefuseCounter());
				log.errorTrace(METHOD_NAME, e);
				p_scheduledBatchDetailVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
				throw new BTSLBaseException("RestrictedSubscriberTopUp","sendRequest",PretupsErrorCodesI.REST_SCH_ERROR_CONNECTION);
			}
			//Reinitialize the connection refuse counter when the connection is open successfully
			p_scheduledBatchMasterVO.initializeConRefCounter();

			String responseStr=null;
			finalResponse = "";
		    while((responseStr = in.readLine())!=null)
			{
				finalResponse=finalResponse+responseStr ;
			}
			loggerMessage.append("[RESULT : COMPLETE] ");
			loggerMessage.append("[RESPONSE : "+finalResponse+"]  ");
			if(!BTSLUtil.isNullString(finalResponse))
			{
				map=BTSLUtil.getStringToHash(finalResponse,"&","=");
				finalResponse=URLDecoder.decode((String)map.get("MESSAGE"),"UTF16");
				
				txn_id=(String)map.get("TXN_ID");
				// Since HashMap returns the "null" as string,if there is not any key.
				// for this,check both for "null" and null then replace it with empty string. 
				txn_id = txn_id==null ||txn_id.equalsIgnoreCase("null")?"":map.get("TXN_ID").toString();
				txn_status=(String)map.get("TXN_STATUS");
				if (log.isDebugEnabled()) {
					log.debug("sendRequest","map="+map+" txn_id="+txn_id);
				}
				
				if(BTSLUtil.isNullString(txn_id))
				{
			        txn_status =PretupsErrorCodesI.TXN_STATUS_FAIL;
			        error_code =PretupsErrorCodesI.TXN_STATUS_FAIL;
				    failOnValidationFail=true;
				    loggerMessage.append("[REASON OF FAIL : Receiver validations fail before creation transactions id ]  ");
				}
				else if(PretupsI.TXN_STATUS_SUCCESS.equals(txn_status))
				{
				    //1.If the transaction status is success(Validation is successfull) make sleep for configurable time.
				    //2.Then Inquire to the C2STransfer for Transfer Status corresponding to the txn_id.
				    //3.If the Entry is null corresponding to txn_id 
				    //4.then again sleep for the configurable time and then Enquire for the same in C2STransfer Table.
				    //5.Based on the enquiry UPDATE THE DETAIL TABLE iformation.
				    //6.Increment the SUCCESSFUL and FAIL COUNTER in ScheduleBatchVO based on the transfer_status as Success or Fail
				    
				    //make enquiry with C2STransfer and sleep number of configured times,till the transferVO returns null.  
				    for(int k=0;k<_RST_NO_SLEEP_ENQUIRY;k++)
				    {
					    Thread.sleep(_sleepTime);
					    c2sTransferVO = getTransferVO(p_con,txn_id);
					    if((c2sTransferVO!=null)&&(PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(c2sTransferVO.getTransferStatus())
					            || PretupsErrorCodesI.TXN_STATUS_FAIL.equals(c2sTransferVO.getTransferStatus())
					            ||PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS.equals(c2sTransferVO.getTransferStatus())))
					    {
					        break;
					    }
					}
				    if(c2sTransferVO!=null)
				    {
				        txn_status =c2sTransferVO.getTransferStatus();
				        error_code =c2sTransferVO.getErrorCode();
					    if(PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS.equals(txn_status)||PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(txn_status)) {
							successOnTxnSuccess=true;
						} else {
							failOnTxnSuccess=true;
							
						}
				    } else {
						throw new BTSLBaseException("RestrictedSubscriberTopUp","sendRequest",PretupsErrorCodesI.REST_SCH_ERR_NO_RESPONSE_C2STRANSFER);
					}
				}
				else
				{
				     // This is the case when TXN_STATUS_SUCCESS is failed means Validation is failed.Increment the Fail count of shceduled batchVO for corressponding batch.
				     // In this case we also make enquiry with c2sTransfer and get the transfer status and error code.
				    failOnValidationFail=true;
				    c2sTransferVO = getTransferVO(p_con,txn_id);
			        txn_status =c2sTransferVO.getTransferStatus();
			        error_code =c2sTransferVO.getErrorCode();
				}
			}//end of if block-checking for null response
 else {
				//Ambiguous Case (When response is NULL string)-Increment the fail count. 
			    ambiguousCase=true;
			    c2sTransferVO = getTransferVO(p_con,txn_id);
		        txn_status =c2sTransferVO.getTransferStatus();
		        error_code =c2sTransferVO.getErrorCode();
			}
			
		}//end of try block
		catch(BTSLBaseException be)
		{
		    //Log the error message if request fails due to connection refused or network problem.
			 txn_status =PretupsErrorCodesI.TXN_STATUS_FAIL;
		     error_code =PretupsErrorCodesI.TXN_STATUS_FAIL;
			loggerMessage.append("[RESULT : EXCEPTION] ");
			loggerMessage.append("[EXCEPTION : "+be.getMessage()+"] ");
			loggerMessage.append("[RESPONSE : FAIL] ");
		    log.error("sendRequest","BTSLBaseException:e="+be);
			if(p_scheduledBatchMasterVO.getConnectionRefuseCounter()==_RST_CON_REFUSE_COUNTER)
			{
			    //ABORT THE PROCESS AND LOG THE ERROR CODE
			    log.error("sendRequest","_RST_CON_REFUSE_COUNTER ="+_RST_CON_REFUSE_COUNTER);
			    log.error("sendRequest",PretupsErrorCodesI.REST_SCH_ERROR_MAX_LIMIT_CONN_REFUSE_REACH);
			    throw new BTSLBaseException("RestrictedSubscriberTopUp","sendRequest",PretupsErrorCodesI.REST_SCH_ERROR_MAX_LIMIT_CONN_REFUSE_REACH);
			}
			
		}//end of catch-BTSLBaseException
		catch(Exception e)
		{     
			  txn_status =PretupsErrorCodesI.TXN_STATUS_FAIL;
	          error_code =PretupsErrorCodesI.TXN_STATUS_FAIL;
		    //Log the error message due to any failures 
		    log.error("sendRequest","Exceptin:e="+e);
		    log.errorTrace(METHOD_NAME,e);
			loggerMessage.append("[RESULT : EXCEPTION] ");
			loggerMessage.append("[EXCEPTION : "+e.getMessage()+"] ");
			loggerMessage.append("[RESPONSE : FAIL] ");
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RestrictedSubscriberTopUp[sendRequest]","","","","Exception:"+e.getMessage());
		}//end of catch-Exception
		finally
		{
		    //First close the connection and inputstream object
		    try{if(in!=null) {
				in.close();
			}}catch(Exception e){
				log.errorTrace(METHOD_NAME,e);
			}
			try{if(httpURLCon !=null) {
				httpURLCon.disconnect();
			}}catch(Exception e){
				log.errorTrace(METHOD_NAME,e);
			}
		    //Check whether to increment fail count or success count and set the transfer status based on the flag.These flag are set after getting the response.
		    if(successOnTxnSuccess)
		    {
		        if (log.isDebugEnabled()) {
					log.debug("sendRequest ","successOnTxnSuccess= "+successOnTxnSuccess);
				}
		        // Increment the success count and set the transfer status when the validation  and the transfer is successfull. 
		        p_scheduledBatchMasterVO.setSuccessfulCount(p_scheduledBatchMasterVO.getSuccessfulCount()+1);
		        p_scheduledBatchDetailVO.setTransactionStatus(txn_status);
		        p_scheduledBatchDetailVO.setTransactionID(txn_id);
		        p_scheduledBatchDetailVO.setTransferErrorCode(error_code);
		    }
		    else if(failOnTxnSuccess||failOnValidationFail)
		    {
		        if (log.isDebugEnabled()) {
					log.debug("sendRequest"," failOnTxnSuccess="+failOnTxnSuccess +" failOnValidationFail="+failOnValidationFail);
				}
		        // Increment the fail count and set the transfer status in the case 1.when Validaion is successful but the transfer is not successful  2.or validation is failed.
		        p_scheduledBatchDetailVO.setErrorCode(error_code);
		        p_scheduledBatchMasterVO.setProcessFailedCount(p_scheduledBatchMasterVO.getProcessFailedCount()+1);
		        p_scheduledBatchDetailVO.setTransactionStatus(txn_status);
		        p_scheduledBatchDetailVO.setTransactionID(txn_id);
		        p_scheduledBatchDetailVO.setTransferErrorCode(error_code);
		        if(errorMap != null)
		        {
		        	MasterErrorList masterErrorList = new MasterErrorList();
					String msg = finalResponse.substring(NumberConstants.N22.getIntValue());//RestAPIStringParser.getMessage(locale, p_scheduledBatchDetailVO.getErrorCode(), null);
					masterErrorList.setErrorCode(p_scheduledBatchDetailVO.getErrorCode());
					masterErrorList.setErrorMsg(msg);
					masterErrorLists.add(masterErrorList);
					rowErrorMsgLists.setMasterErrorList(masterErrorLists);
					rowErrorMsgLists.setRowValue(p_scheduledBatchDetailVO.getMsisdn());
					rowErrorMsgLists.setRowName("Line" + index);
					if(errorMap.getRowErrorMsgLists() == null)
						errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists> ());
					(errorMap.getRowErrorMsgLists()).add(rowErrorMsgLists);
		        }
		    }
		    else if(ambiguousCase)
		    {
		        if (log.isDebugEnabled()) {
					log.debug("sendRequest","ambiguousCase= "+ambiguousCase);
				}
		        p_scheduledBatchDetailVO.setErrorCode(PretupsErrorCodesI.REST_SCH_ERR_NO_RESPONSE_C2STRANSFER);
		        p_scheduledBatchMasterVO.setProcessFailedCount(p_scheduledBatchMasterVO.getProcessFailedCount()+1);
		        p_scheduledBatchDetailVO.setTransactionStatus(txn_status);
		        p_scheduledBatchDetailVO.setTransactionID(txn_id);
		    }
		    else
		    {
		        if (log.isDebugEnabled()) {
					log.debug("sendRequest","else case");
				}
		        p_scheduledBatchDetailVO.setErrorCode(PretupsErrorCodesI.REST_SCH_ERR_NO_RESPONSE_C2STRANSFER);
		        p_scheduledBatchMasterVO.setProcessFailedCount(p_scheduledBatchMasterVO.getProcessFailedCount()+1);
		        p_scheduledBatchDetailVO.setErrorCode(error_code);
		        p_scheduledBatchDetailVO.setTransactionStatus(txn_status);
		    }

	        p_scheduledBatchDetailVO.setStatus(PretupsI.REST_SCH_BATCH_STATUS_EXECUTED);
			loggerMessage.append("[ EXITING SEND REQUEST ]");	
			ScheduledTopUpLogger.log(loggerMessage.toString());
			if(log.isDebugEnabled()) {
				log.debug("sendRequest ","Exited ::p_scheduledBatchDetailVO "+p_scheduledBatchDetailVO);
			}
		}//end of finally		
	}//end of sendRequest
	/**
	 * This method is used to Get the TransferVO from C2STransfer based on the transfer ID.
	 * @param String p_transactioID
	 * @return C2STransferVO
	 */
	public C2STransferVO getTransferVO(Connection p_conn,String p_transactioID)
	{	
	    if (log.isDebugEnabled()) {
			log.debug("getTransferVO Entered","p_transactioID"+p_transactioID);
		}
	    final String METHOD_NAME="getTransferVO";
		C2STransferVO transferVO=null;
		try
		{
			C2STransferDAO c2STransferDAO =new C2STransferDAO();
			transferVO=c2STransferDAO.loadC2STransferDetailsForSchProcess(p_conn,p_transactioID);
		}
		catch(Exception e)
		{
			log.error("getTransferVO","Exceptin:e="+e);
			log.errorTrace(METHOD_NAME,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RestrictedSubscriberTopUp[getTransferVO]","","","","Exception:"+e.getMessage());
		}
		finally
		{
			if(log.isDebugEnabled()) {
				log.debug("getTransferVO ","Exited transferVO "+transferVO);
			}
		}
	    return transferVO;
	}
	
	/**
	 * This method is used to creat BTSLMessage for each BatchID,Message contains the information like,
	 * BatchID,SuccessCount,FailCount 
	 * @param ScheduleBatchMasterVO p_scheduleBatchMasterVO
	 * @throws BTSLBaseException
	 */
	private BTSLMessages getPushMessage(ScheduleBatchMasterVO p_scheduleBatchMasterVO)
	{
	    if(log.isDebugEnabled()) {
			log.debug("getPushMessage","Entered p_scheduleBatchMasterVO="+p_scheduleBatchMasterVO);
		}
	    String[] messageArray = null;
	    BTSLMessages pushMessage = null;
	    try
	    {
		    messageArray = new String[6];    
		    messageArray[0]=(p_scheduleBatchMasterVO.getBatchID());
		    messageArray[1]=(String.valueOf(p_scheduleBatchMasterVO.getSuccessfulCount()));
		    messageArray[2]=(String.valueOf(p_scheduleBatchMasterVO.getProcessFailedCount()));
		    messageArray[3]=(String.valueOf(p_scheduleBatchMasterVO.getTotalCount()));
		    messageArray[4]=(String.valueOf(p_scheduleBatchMasterVO.getProccessedRecords()));
		    messageArray[5]=(String.valueOf(p_scheduleBatchMasterVO.getUnproccessedRecords()));
		    //Done by ankit Z on date 4/8/06 for problem of getting message null
		    pushMessage= new BTSLMessages(PretupsErrorCodesI.INITIATOR_MSG_SCHEDULE_TOPUP_FINAL,messageArray);
	    }
	    catch(Exception e)
	    {
	        log.error("getPushMessage","Exception e"+e);
	    }
	    if(log.isDebugEnabled()) {
			log.debug("getPushMessage","Exited pushMessage="+pushMessage);
		}
	    return pushMessage;
	}
	
	/**
	 * This method is used to get the reciever language code.
	 * @param ScheduleBatchDetailVO p_scheduledBatchDetailVO
	 */
	private  void recieverLanguageCode(Connection p_con,ScheduleBatchDetailVO p_scheduledBatchDetailVO) throws BTSLBaseException
	{
	    if(log.isDebugEnabled()) {
			log.debug("recieverLanguageCode","Entered p_scheduledBatchDetailVO= "+p_scheduledBatchDetailVO);
		}
	    String receiverLanguageCode;
	    try
	    {
			receiverLanguageCode=_localeMasterDAO.loadLocaleMasterCode(p_con,p_scheduledBatchDetailVO.getLanguage(),p_scheduledBatchDetailVO.getCountry());
			p_scheduledBatchDetailVO.setLanguageCode(receiverLanguageCode);
	    }
	    catch(Exception e)
	    {
	        log.error("recieverLanguageCode","Exception e"+e);
	        throw new BTSLBaseException("RestrictedSubscriberTopUp","recieverLanguageCode","Receiver Language Code Not Found");
	    }
	    if(log.isDebugEnabled()) {
			log.debug("recieverLanguageCode","Exited receiverLanguageCode="+receiverLanguageCode);
		}
	}

	
	/**
	 * This method is used to get the donor language code.
	 * @param ScheduleBatchDetailVO p_scheduledBatchDetailVO
	 */
	private  void donorLanguageCode(Connection p_con,ScheduleBatchDetailVO p_scheduledBatchDetailVO) throws BTSLBaseException
	{
	    if(log.isDebugEnabled()) {
			log.debug("donorLanguageCode","Entered p_scheduledBatchDetailVO= "+p_scheduledBatchDetailVO);
		}
	    String donorLanguageCode;
	    try
	    {
	        donorLanguageCode=_localeMasterDAO.loadLocaleMasterCode(p_con,p_scheduledBatchDetailVO.getDonorLanguage(),p_scheduledBatchDetailVO.getDonorCountry());
			p_scheduledBatchDetailVO.setDonorLanguageCode(donorLanguageCode);
	    }
	    catch(Exception e)
	    {
	        log.error("donorLanguageCode","Exception e"+e);
	        throw new BTSLBaseException("RestrictedSubscriberTopUp","donorLanguageCode","Donor Language Code Not Found");
	    }
	    if(log.isDebugEnabled()) {
			log.debug("donorLanguageCode","Exited donorLanguageCode="+donorLanguageCode);
		}
	}
	
	/**
	 * This method is used to get the sender language code.
	 * @param ScheduleBatchDetailVO p_scheduledBatchDetailVO
	 */
	
	private  void senderLanguageCode(Connection p_con,ScheduleBatchDetailVO p_scheduledBatchDetailVO,String p_language, String p_country ) throws BTSLBaseException
	{
	    if(log.isDebugEnabled()) {
			log.debug("senderLanguageCode","Entered p_scheduledBatchDetailVO= "+p_scheduledBatchDetailVO+ " p_language= "+p_language+ " p_country= "+p_country);
		}
	    String senderLanguageCode;
	    try
	    {
	        senderLanguageCode=_localeMasterDAO.loadLocaleMasterCode(p_con,p_language,p_country);
			p_scheduledBatchDetailVO.setSenderLanguageCode(senderLanguageCode);
	    }
	    catch(Exception e)
	    {
	        log.error("senderLanguageCode","Exception e"+e);
	        throw new BTSLBaseException("RestrictedSubscriberTopUp","senderLanguageCode","Sender Language Code Not Found");
	    }
	    if(log.isDebugEnabled()) {
			log.debug("senderLanguageCode","Exited senderLanguageCode="+senderLanguageCode);
		}
	}
	
	private void throwBTSLException(String className, String method, String message) throws BTSLBaseException{
		throw new BTSLBaseException(className,method,message);
	}
}