package com.btsl.pretups.p2p.subscriber.requesthandler;

/**
 * @(#)SubscriberThresholdEnquiryController.java
 * Copyright(c) 2013, Mahindra Comviva Pvt.Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * Author                        Date            History
 *-------------------------------------------------------------------------------------------------
 * Vikas Jauhari              Apr 25, 2013     Initital Creation
 *-------------------------------------------------------------------------------------------------
 */

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCacheVO;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;
import com.txn.pretups.preference.businesslogic.PreferenceTxnDAO;
import com.web.pretups.master.businesslogic.ServiceClassWebDAO;

public class SubscriberThresholdEnquiryController implements ServiceKeywordControllerI
{
        private static Log _log = LogFactory.getLog(SubscriberThresholdEnquiryController.class.getName());

        private RequestVO _requestVO=null;
        private SenderVO _senderVO;
        private String _receiverMSISDN;
        private HashMap _preferenceDetailsMap = null;
        public static OperatorUtilI _operatorUtil = null;

        public void process(RequestVO p_requestVO)
        {
                if (_log.isDebugEnabled()) _log.debug("process", " Entered Request ID" + p_requestVO.getRequestID()+" Msisdn="+p_requestVO.getFilteredMSISDN());

                Connection con = null;
                boolean isRegisteredUser = false;
                boolean isServiceClassCodeExist = false;
                SubscriberDAO subscriberDAO = null;
                PreferenceTxnDAO preferenceDAO = null;
                ServiceClassWebDAO serviceClassDAO =null;
                Date currentdate = new Date();
                ArrayList serviceClassIDList = null;
                ListValueVO listValueVO=null;
                String [] listValue =null;
                try
                {
                        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
                        _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
                }
                catch(Exception e)
                {
                	 _log.errorTrace("process", e);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"SubscriberThresholdEnquiryController[process]","","","","Exception while loading the operator util class :"+e.getMessage());
                }
                try
                {
                        con = OracleUtil.getConnection();
                        _requestVO = p_requestVO;
                        _receiverMSISDN = _operatorUtil.getSystemFilteredMSISDN(_requestVO.getReceiverMsisdn());

                        //Validate External Network Code
                        this.validateExternalNetworkCode(_requestVO);

                        //Check subscriber to whom we need to enquired is registered or not.
                        subscriberDAO = new SubscriberDAO();
						_senderVO = subscriberDAO.loadSubscriberDetailsByMsisdn(con,_receiverMSISDN,PretupsI.SERVICE_TYPE_P2PRECHARGE);
                        isRegisteredUser = subscriberDAO.isMSISDNExist(con, _receiverMSISDN, _senderVO.getSubscriberType());
                        if(isRegisteredUser)
                        {
                                
                                _requestVO.setModule(PretupsI.P2P_MODULE);

                                // check service class exist in the system or not.
                                serviceClassDAO = new ServiceClassWebDAO();
                                isServiceClassCodeExist = serviceClassDAO.isServiceCodeExists(con, _requestVO.getReceiverServiceClassId());
                                if(isServiceClassCodeExist)
                                {
                                        _senderVO.setModifiedOn(currentdate);
                                        _senderVO.setModifiedBy(PretupsI.SYSTEM_USER);
                                        _senderVO.setServiceClassCode(_requestVO.getReceiverServiceClassId());
                                        //updateCount = subscriberDAO.updateSubscriberServiceClassCode(con,_senderVO);
                                }
                                else
                                {
                                        p_requestVO.setMessageCode(PretupsErrorCodesI.SERVICE_CLASS_CODE_NOT_FOUND_IN_SYSTEM);
                                        throw new BTSLBaseException(this,"process",PretupsErrorCodesI.SERVICE_CLASS_CODE_NOT_FOUND_IN_SYSTEM);
                                }
                                // Load Service Class ID details of subscriber
                                preferenceDAO = new PreferenceTxnDAO();
                                serviceClassIDList = preferenceDAO.loadServiceClassIDList(con, _senderVO.getNetworkCode(),_senderVO.getServiceClassCode());
                                if(serviceClassIDList.size()>0)
                                {
                                        String serviceClass=null;
                                        for(int i=0; i<serviceClassIDList.size();i++)
                                        {
                                                listValueVO = (ListValueVO)serviceClassIDList.get(i);
                                                if(listValueVO.getLabel().equals(_senderVO.getServiceClassCode()))
                                                {
                                                        listValue = (listValueVO.getValue()).split("_");
                                                        String serviceClassID = listValue[0];
                                                        String interfaceCategory = listValue[1];
                                                        if(interfaceCategory.equals(_senderVO.getSubscriberType()) && serviceClassID.equals(_senderVO.getServiceClassID()))
                                                        {
                                                                serviceClass=serviceClassID;
                                                                break;
                                                        }
                                                        else if (interfaceCategory.equals(_senderVO.getSubscriberType()) && BTSLUtil.isNullString(serviceClass))
                                                                serviceClass=serviceClassID;
                                                }
                                        }
                                        _senderVO.setServiceClassID(serviceClass);
                                }
                                else
                                {
                                        p_requestVO.setMessageCode(PretupsErrorCodesI.SERVICECLASS_NOT_USED_IN_SYSTEM);
                                        throw new BTSLBaseException(this,"process",PretupsErrorCodesI.SERVICECLASS_NOT_USED_IN_SYSTEM);
                                }
                                //_senderVO.getServiceClassID()
                                _preferenceDetailsMap = preferenceDAO.loadPreferenceByServiceClassId(_senderVO.getNetworkCode(), _senderVO.getServiceClassID());

                                SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
                                prepareUpdatedRequestVO(senderVO,_senderVO);
                                senderVO.setServiceClassID(_senderVO.getServiceClassID());
                                p_requestVO.setSenderVO(senderVO);
                        }
                        else
                        {
                                String arr[] = { _requestVO.getReceiverMsisdn() };
                                p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_SUBSCRIBER_NOT_RGISTERED_FOR_ENQUIRY);
                                throw new BTSLBaseException(this,"process",PretupsErrorCodesI.P2P_SUBSCRIBER_NOT_RGISTERED_FOR_ENQUIRY,0,arr,null);
                        }
                }
                catch (BTSLBaseException be)
                {
                        p_requestVO.setSuccessTxn(false);
                        _log.error("process", "BTSLBaseException while suspending services for ="+p_requestVO.getFilteredMSISDN()+" getting exception=" + be.getMessage());
                        if(BTSLUtil.isNullString(p_requestVO.getMessageCode()))
                                p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_SUBSCRIBER_THRESHOLD_ENQUIRY_ERROR);
                }
                catch (Exception e)
                {
                        p_requestVO.setSuccessTxn(false);
                        _log.error("process", "Exception while suspending services for ="+p_requestVO.getFilteredMSISDN()+" getting exception="+ e.getMessage());
                        _log.errorTrace("process", e);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"SubscriberThresholdEnquiryController[process]",p_requestVO.getFilteredMSISDN(),"","","Exception while enquiry for threshold:"+e.getMessage());
                        if(BTSLUtil.isNullString(p_requestVO.getMessageCode()))
                                p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_SUBSCRIBER_THRESHOLD_ENQUIRY_ERROR);
                }
                finally
                {
                        try     {if (con != null)con.close();} catch (Exception e){ _log.errorTrace("process", e);}
                        if (_log.isDebugEnabled()) _log.debug("process", " Exited ");
                }
        }


        private void prepareUpdatedRequestVO(SenderVO p_updatedSenderVO, SenderVO p_ExsitingDetailVO) throws BTSLBaseException
        {
                if (_log.isDebugEnabled()) _log.debug("prepareUpdatedRequestVO", " Entered");

                p_updatedSenderVO.setSubscriberType(p_ExsitingDetailVO.getSubscriberType());
                String dailyMaxTransCount = null;
                String dailyMaxTransAmt =null;
                String weeklyMaxTransCount = null;
                String weeklyMaxTransAmt =null;
                String monthlyMaxTransCount = null;
                String monthlyMaxTransAmt =null;
                PreferenceCacheVO preferenceCacheVO = null;
                boolean [] counterResetCheck = null;

                try
                {
                        counterResetCheck = this.checkResetCountersRequiredinEnquiry(p_ExsitingDetailVO.getLastSuccessTransferDate(),new Date());

                        if(counterResetCheck[0])
                        {
                                p_updatedSenderVO.setDailyTransferCount(0);
                                p_updatedSenderVO.setDailyTransferAmount(0);
                        }
                        else
                        {
                                p_updatedSenderVO.setDailyTransferCount(p_ExsitingDetailVO.getDailyTransferCount());
                                p_updatedSenderVO.setDailyTransferAmount(Long.valueOf(PretupsBL.getDisplayAmount(p_ExsitingDetailVO.getDailyTransferAmount())));
                        }

                        if(counterResetCheck[1])
                        {
                                p_updatedSenderVO.setWeeklyTransferCount(0);
                                p_updatedSenderVO.setWeeklyTransferAmount(0);
                        }
                        else
                        {
                                p_updatedSenderVO.setWeeklyTransferCount(p_ExsitingDetailVO.getWeeklyTransferCount());
                                p_updatedSenderVO.setWeeklyTransferAmount(Long.valueOf(PretupsBL.getDisplayAmount(p_ExsitingDetailVO.getWeeklyTransferAmount())));
                        }
                        if(counterResetCheck[2])
                        {
                                p_updatedSenderVO.setMonthlyTransferCount(0);
                                p_updatedSenderVO.setMonthlyTransferAmount(0);
                        }
                        else
                        {
                                p_updatedSenderVO.setMonthlyTransferCount(p_ExsitingDetailVO.getMonthlyTransferCount());
                                p_updatedSenderVO.setMonthlyTransferAmount(Long.valueOf(PretupsBL.getDisplayAmount(p_ExsitingDetailVO.getMonthlyTransferAmount())));
                        }

                        // Daily Max Transaction Count Allowed
                        preferenceCacheVO = (PreferenceCacheVO)_preferenceDetailsMap.get(PreferenceI.DAILY_MAX_TRFR_NUM_CODE+"_"+p_ExsitingDetailVO.getNetworkCode()+"_"+p_ExsitingDetailVO.getServiceClassID());
                        if(preferenceCacheVO !=null)
                                dailyMaxTransCount =  preferenceCacheVO.getValue();
                        if(!BTSLUtil.isNullString(dailyMaxTransCount))
                                p_updatedSenderVO.setDailyMaxTransCountThreshold(Long.valueOf(dailyMaxTransCount));
                        else
                        {
                                dailyMaxTransCount= String.valueOf((Integer)PreferenceCache.getSystemPreferenceValue(PreferenceI.DAILY_MAX_TRFR_NUM_CODE));
                                p_updatedSenderVO.setDailyMaxTransCountThreshold(Long.valueOf(dailyMaxTransCount));
                        }

                        //Weekly Max Transaction Count Allowed
                        preferenceCacheVO = (PreferenceCacheVO)_preferenceDetailsMap.get(PreferenceI.WEEKLY_MAX_TRFR_NUM_CODE+"_"+p_ExsitingDetailVO.getNetworkCode()+"_"+p_ExsitingDetailVO.getServiceClassID());
                        if(preferenceCacheVO !=null)
                                weeklyMaxTransCount =  preferenceCacheVO.getValue();
                        if(!BTSLUtil.isNullString(weeklyMaxTransCount))
                                p_updatedSenderVO.setWeeklyMaxTransCountThreshold(Long.valueOf(weeklyMaxTransCount));
                        else
                        {
                                weeklyMaxTransCount= String.valueOf((Integer)PreferenceCache.getSystemPreferenceValue(PreferenceI.DAILY_MAX_TRFR_NUM_CODE));
                                p_updatedSenderVO.setWeeklyMaxTransCountThreshold(Long.valueOf(weeklyMaxTransCount));
                        }

                        //Monthly Max Transaction Count Allowed
                        preferenceCacheVO = (PreferenceCacheVO)_preferenceDetailsMap.get(PreferenceI.MONTHLY_MAX_TRFR_NUM_CODE+"_"+p_ExsitingDetailVO.getNetworkCode()+"_"+p_ExsitingDetailVO.getServiceClassID());
                        if(preferenceCacheVO !=null)
                                monthlyMaxTransCount =  preferenceCacheVO.getValue();
                        if(!BTSLUtil.isNullString(monthlyMaxTransCount))
                                p_updatedSenderVO.setMonthlyMaxTransCountThreshold(Long.valueOf(monthlyMaxTransCount));
                        else
                        {
                                monthlyMaxTransCount= String.valueOf((Integer)PreferenceCache.getSystemPreferenceValue(PreferenceI.DAILY_MAX_TRFR_NUM_CODE));
                                p_updatedSenderVO.setMonthlyMaxTransCountThreshold(Long.valueOf(monthlyMaxTransCount));
                        }

                        // Daily Maximum Transaction Amount Threshold
                        preferenceCacheVO= (PreferenceCacheVO)_preferenceDetailsMap.get(PreferenceI.DAILY_MAX_TRFR_AMOUNT_CODE+"_"+p_ExsitingDetailVO.getNetworkCode()+"_"+p_ExsitingDetailVO.getServiceClassID());
                        if(preferenceCacheVO !=null)
                                dailyMaxTransAmt =  preferenceCacheVO.getValue();
                        if(!BTSLUtil.isNullString(dailyMaxTransAmt))
                                p_updatedSenderVO.setDailyMaxTransAmtThreshold(Long.valueOf(dailyMaxTransAmt));
                        else
                        {
                                dailyMaxTransAmt = String.valueOf((Long)PreferenceCache.getSystemPreferenceValue(PreferenceI.DAILY_MAX_TRFR_AMOUNT_CODE));
                                p_updatedSenderVO.setDailyMaxTransAmtThreshold(Long.valueOf(PretupsBL.getDisplayAmount(Long.valueOf(dailyMaxTransAmt))));
                        }

                        //Weekly Maximum Transaction Amount Threshold
                        preferenceCacheVO= (PreferenceCacheVO)_preferenceDetailsMap.get(PreferenceI.WEEKLY_MAX_TRFR_AMOUNT_CODE+"_"+p_ExsitingDetailVO.getNetworkCode()+"_"+p_ExsitingDetailVO.getServiceClassID());
                        if(preferenceCacheVO !=null)
                                weeklyMaxTransAmt =  preferenceCacheVO.getValue();
                        if(!BTSLUtil.isNullString(weeklyMaxTransAmt))
                                p_updatedSenderVO.setWeeklyMaxTransAmtThreshold(Long.valueOf(weeklyMaxTransAmt));
                        else
                        {
                                weeklyMaxTransAmt = String.valueOf((Long)PreferenceCache.getSystemPreferenceValue(PreferenceI.WEEKLY_MAX_TRFR_AMOUNT_CODE));
                                p_updatedSenderVO.setWeeklyMaxTransAmtThreshold(Long.valueOf(PretupsBL.getDisplayAmount(Long.valueOf(weeklyMaxTransAmt))));
                        }

                        //Monthly Maximum Transaction Amount Threshold
                        preferenceCacheVO= (PreferenceCacheVO)_preferenceDetailsMap.get(PreferenceI.MONTHLY_MAX_TRFR_AMOUNT_CODE+"_"+p_ExsitingDetailVO.getNetworkCode()+"_"+p_ExsitingDetailVO.getServiceClassID());
                        if(preferenceCacheVO !=null)
                                monthlyMaxTransAmt =  preferenceCacheVO.getValue();
                        if(!BTSLUtil.isNullString(monthlyMaxTransAmt))
                                p_updatedSenderVO.setMonthlyMaxTransAmtThreshold(Long.valueOf(monthlyMaxTransAmt));
                        else
                        {
                                monthlyMaxTransAmt = String.valueOf((Long)PreferenceCache.getSystemPreferenceValue(PreferenceI.MONTHLY_MAX_TRFR_AMOUNT_CODE));
                                p_updatedSenderVO.setMonthlyMaxTransAmtThreshold(Long.valueOf(PretupsBL.getDisplayAmount(Long.valueOf(monthlyMaxTransAmt))));
                        }

                }
                catch(ClassCastException nfe)
                {
                        _log.errorTrace("process", nfe);
                        _requestVO.setMessageCode(PretupsErrorCodesI.P2P_SUBSCRIBER_THRESHOLD_NOT_UPDATED);
                        throw new BTSLBaseException(this,"process",PretupsErrorCodesI.P2P_SUBSCRIBER_THRESHOLD_NOT_UPDATED);

                }
                finally
                {
                        if (_log.isDebugEnabled()) _log.debug("prepareUpdatedRequestVO", " Exited p_updatedSenderVO"  + p_updatedSenderVO);
                }
        }


        public void validateExternalNetworkCode(RequestVO p_requestVO) throws BTSLBaseException
        {
                if(_log.isDebugEnabled()) _log.debug("validateExternalNetworkCode","Entered External Network Code= "+p_requestVO.getExternalNetworkCode());
                boolean isvValidate = false;
                try
                {
                        NetworkVO networkVO=(NetworkVO)NetworkCache.getNetworkByExtNetworkCode(p_requestVO.getExternalNetworkCode());
                        NetworkPrefixVO phoneNetworkPrefixVO=null;
                        //Also check if MSISDN is there then get the network Details from it and match with network from external code
                        if(networkVO!=null && !BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN()))
                        {
                                phoneNetworkPrefixVO=PretupsBL.getNetworkDetails(p_requestVO.getFilteredMSISDN(),PretupsI.USER_TYPE_SENDER);
                                if(!phoneNetworkPrefixVO.getNetworkCode().equals(networkVO.getNetworkCode()))
                                {
                                        p_requestVO.setMessageCode(PretupsErrorCodesI.NETWORK_CODE_MSIDN_NETWORK_MISMATCH);
                                        throw new BTSLBaseException(this,"validateExternalNetworkCode",PretupsErrorCodesI.NETWORK_CODE_MSIDN_NETWORK_MISMATCH);
                                }
                        }
                        else if(networkVO==null)
                        {
                                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_EXT_NETWORK_CODE);
                                throw new BTSLBaseException(this,"validateExternalNetworkCode",PretupsErrorCodesI.ERROR_EXT_NETWORK_CODE);
                        }
                }
                catch(BTSLBaseException be)
                {
                        throw be;
                }
                finally
                {
                        if(_log.isDebugEnabled()) _log.debug("validateExternalNetworkCode","Exiting isvValidate="+isvValidate);
                }
        }


        public boolean[] checkResetCountersRequiredinEnquiry(Date p_lastSuccessTransferDate , java.util.Date p_newDate)
        {
                if(_log.isDebugEnabled())
                        _log.debug("checkResetCountersRequiredinEnquiry","Entered p_lastSuccessTransferDate="+p_lastSuccessTransferDate);

                boolean isDayCounterChange=false;
                boolean isWeekCounterChange=false;
                boolean isMonthCounterChange=false;
                boolean[] counterReset = new boolean[3];
                try
                {
                        if(p_lastSuccessTransferDate!=null)
                        {
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(p_newDate);
                                int presentDay = cal.get(Calendar.DAY_OF_MONTH);
                                int presentWeek = cal.get(Calendar.WEEK_OF_YEAR);
                                int presentMonth = cal.get(Calendar.MONTH);
                                int presentYear = cal.get(Calendar.YEAR);

                                cal.setTime(p_lastSuccessTransferDate);
                                int lastWeek = cal.get(Calendar.WEEK_OF_YEAR);
                                int lastDay = cal.get(Calendar.DAY_OF_MONTH);
                                int lastMonth = cal.get(Calendar.MONTH);
                                int lastYear = cal.get(Calendar.YEAR);

                                if(presentDay!=lastDay)
                                        isDayCounterChange=true;
                                if(presentWeek!=lastWeek)
                                        isWeekCounterChange=true;

                                if(presentMonth!=lastMonth)
                                {
                                        isDayCounterChange=true;
                                        isWeekCounterChange=true;
                                        isMonthCounterChange=true;
                                }
                                if(presentYear!=lastYear)
                                {
                                        isDayCounterChange=true;
                                        isWeekCounterChange=true;
                                        isMonthCounterChange=true;
                                }

                                counterReset[0] = isDayCounterChange;
                                counterReset[1] = isWeekCounterChange;
                                counterReset[2] = isMonthCounterChange;

                        }
                        else
                        {
                                counterReset[0] = counterReset[1] = counterReset[2]= true;
                        }
                }
                catch(Exception ex)
                {
                        _log.errorTrace("checkResetCountersRequiredinEnquiry", ex);
                }
                finally
                {
                        if(_log.isDebugEnabled())
                                _log.debug("checkResetCountersRequiredinEnquiry","Exited");
                }
                return counterReset;
        }
}
