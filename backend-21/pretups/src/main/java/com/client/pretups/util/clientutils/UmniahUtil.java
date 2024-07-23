/**
 * @(#)UmniahUtil.java
 *                     Copyright(c) 2007, Bharti Telesoft Ltd.
 *                     All Rights Reserved
 * 
 *                     <description>
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Ved Prakash July 17, 2007 Initital Creation
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 * 
 */

package com.client.pretups.util.clientutils;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class UmniahUtil extends OperatorUtil
{
	private Log _log = LogFactory.getLog(this.getClass().getName());
    
    /**
     * Method that will validate the user message sent
     * 
     * @param p_con
     * @param p_c2sTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     * @see com.btsl.pretups.util.UmniahUtilI#validateEVDRequestFormat(Connection,
     *      C2STransferVO, RequestVO)
     */
    public void validateEVDRequestFormat(Connection p_con,C2STransferVO p_c2sTransferVO,RequestVO p_requestVO) throws BTSLBaseException
    {
        final String METHOD_NAME = "validateEVDRequestFormat";
        try
        {
            String[] p_requestArr=p_requestVO.getRequestMessageArray();
            String custMsisdn=null;
            String requestAmtStr=null;
            ChannelUserVO channelUserVO=(ChannelUserVO)p_c2sTransferVO.getSenderVO();
            
            int messageLen=p_requestArr.length;
            if(_log.isDebugEnabled()) _log.debug("validateEVDRequestFormat","messageLen: "+messageLen);
            for(int i=0;i<messageLen;i++)
            {
                if(_log.isDebugEnabled()) _log.debug("validateEVDRequestFormat","i: "+i+" value: "+p_requestArr[i]);
            }
            switch(messageLen)
            {
                case 4:
                {
                    //Do the 000 check Default PIN 
                    if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES))
                    {
                        try
                        {
                            ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[3]);
                        }
                        catch(BTSLBaseException be)
                        {
                            if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK))))
                                p_con.commit();
                            throw be;
                        }   
                    }                   
                    ReceiverVO receiverVO=new ReceiverVO();
                    //Customer MSISDN Validation
                    custMsisdn=p_requestArr[1];
                    
                    PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
                    
                    //Recharge amount Validation
                    requestAmtStr=p_requestArr[2];
                    //if("SMS".equals(p_requestVO.getSourceType()) && requestAmtStr.startsWith("00"))
		    if(("SMS".equals(p_requestVO.getSourceType())||"SMSC".equals(p_requestVO.getSourceType())) && requestAmtStr.startsWith("0"))
                       
			    	try{
			    		double amount=Double.parseDouble(requestAmtStr);	
			    		amount=amount/100;
			    		requestAmtStr=Double.toString(amount);
			    	}catch (Exception e) {
	
			    	}
		    		
                    PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
                    p_c2sTransferVO.setReceiverVO(receiverVO);      
                    //p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
                    //Changed on 27/05/07 for Service Type selector Mapping
                    ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
                    if(serviceSelectorMappingVO!=null)
                        p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());                                 
                    p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                    break;
                }
                
                case 5:
                {
                    //Do the 000 check Default PIN 
                    if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES))
                    {
                        try
                        {
                            ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[4]);
                        }
                        catch(BTSLBaseException be)
                        {
                            if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK))))
                                p_con.commit();
                            throw be;
                        }   
                    }                       
                    ReceiverVO receiverVO=new ReceiverVO();
                    //Customer MSISDN Validation
                    custMsisdn=p_requestArr[1];
                    
                    PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
                    
                    //Recharge amount Validation
                    requestAmtStr=p_requestArr[2];
                    //if("SMS".equals(p_requestVO.getSourceType()) && requestAmtStr.startsWith("00"))
if(("SMS".equals(p_requestVO.getSourceType())||"SMSC".equals(p_requestVO.getSourceType())) && requestAmtStr.startsWith("0"))
					try{
						double amount=Double.parseDouble(requestAmtStr);	
						amount=amount/100;
						requestAmtStr=Double.toString(amount);
					}catch (Exception e) {
				
					}
					PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
                    p_c2sTransferVO.setReceiverVO(receiverVO);
                    if(BTSLUtil.isNullString(p_requestArr[3]))
                        p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                    else
                    {
                        int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[3]);
                        if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
                            throw new BTSLBaseException(this,"validateEVDRequestFormat",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                    }
                    //p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
                    //Changed on 27/05/07 for Service Type selector Mapping
                    ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
                    if(serviceSelectorMappingVO!=null)
                        p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());                                 
                    break;
                }
                
                case 6:
                {
                    if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES))
                    {
                        try
                        {
                            ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[5]);
                        }
                        catch(BTSLBaseException be)
                        {
                            if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK))))
                                p_con.commit();
                            throw be;
                        }                               
                    }
                    
                    ReceiverVO receiverVO=new ReceiverVO();
                    //Customer MSISDN Validation
                    custMsisdn=p_requestArr[1];
                    
                    PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
                    
                    //Recharge amount Validation
                    requestAmtStr=p_requestArr[2];
//                    if("SMS".equals(p_requestVO.getSourceType()) && requestAmtStr.startsWith("00"))
if(("SMS".equals(p_requestVO.getSourceType())||"SMSC".equals(p_requestVO.getSourceType())) && requestAmtStr.startsWith("0"))
                    
						try{
							double amount=Double.parseDouble(requestAmtStr);	
							amount=amount/100;
							requestAmtStr=Double.toString(amount);
						}catch (Exception e) {
					
						}
					PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
                    p_c2sTransferVO.setReceiverVO(receiverVO);
                    if(BTSLUtil.isNullString(p_requestArr[3]))
                    {
                        if(PretupsI.LOCALE_LANGAUGE_EN.equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
                        {
                            //p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
                            //Changed on 27/05/07 for Service Type selector Mapping
                            ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
                            if(serviceSelectorMappingVO!=null)
                                p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());                                 
                        }
                    }
                    else
                        p_requestVO.setReqSelector(p_requestArr[3]);
                    
                    PretupsBL.getSelectorValueFromCode(p_requestVO);
                    if(BTSLUtil.isNullString(p_requestVO.getReqSelector()))
                    {
                        //p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
                        //Changed on 27/05/07 for Service Type selector Mapping
                        ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
                        if(serviceSelectorMappingVO!=null)
                            p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());                                 
                    }
                    if(BTSLUtil.isNullString(p_requestArr[4]))
                        p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                    else
                    {
                        int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[4]);
                        if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
                            throw new BTSLBaseException(this,"validateEVDRequestFormat",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                    }
                    break;
                }
                case 7:
                {
                    if((((ChannelUserVO)p_c2sTransferVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES))
                    {
                        try
                        {
                            ChannelUserBL.validatePIN(p_con,channelUserVO,p_requestArr[6]);
                        }
                        catch(BTSLBaseException be)
                        {
                            if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK))))
                                p_con.commit();
                            throw be;
                        }                               
                    }
                    
                    ReceiverVO receiverVO=new ReceiverVO();
                    //Customer MSISDN Validation
                    custMsisdn=p_requestArr[1];
                    
                    PretupsBL.validateMsisdn(p_con,receiverVO,p_c2sTransferVO.getRequestID(),custMsisdn);
                    
                    //Recharge amount Validation
                    requestAmtStr=p_requestArr[2];
//                    if("SMS".equals(p_requestVO.getSourceType()) && requestAmtStr.startsWith("00"))
if(("SMS".equals(p_requestVO.getSourceType())||"SMSC".equals(p_requestVO.getSourceType())) && requestAmtStr.startsWith("00"))
                        requestAmtStr=requestAmtStr.replace("00", ".");
                    PretupsBL.validateAmount(p_c2sTransferVO,requestAmtStr);
                    p_c2sTransferVO.setReceiverVO(receiverVO);
                    if(BTSLUtil.isNullString(p_requestArr[3]))
                    {
                        if(PretupsI.LOCALE_LANGAUGE_EN.equalsIgnoreCase(p_requestVO.getLocale().getLanguage()))
                        {
                            //p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
                            //Changed on 27/05/07 for Service Type selector Mapping
                            ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
                            if(serviceSelectorMappingVO!=null)
                                p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());                                 
                        }
                    }
                    else
                        p_requestVO.setReqSelector(p_requestArr[3]);
                    
                    PretupsBL.getSelectorValueFromCode(p_requestVO);
                    if(BTSLUtil.isNullString(p_requestVO.getReqSelector()))
                    {
                        //p_requestVO.setReqSelector(String.valueOf(SystemPreferences.C2S_TRANSFER_DEF_SELECTOR_CODE));
                        //Changed on 27/05/07 for Service Type selector Mapping
                        ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_c2sTransferVO.getServiceType());
                        if(serviceSelectorMappingVO!=null)
                            p_requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());                                 
                    }
                    //For handling of sender locale
                    if(BTSLUtil.isNullString(p_requestArr[4]))
                        p_requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                    else
                    {
                        int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[4]);
                        p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                    }
                    if (_log.isDebugEnabled()) 
                        _log.debug(this,"sender locale: ="+p_requestVO.getSenderLocale());
                    
                    if(BTSLUtil.isNullString(p_requestArr[5]))
                        p_requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                    else
                    {
                        int langCode=PretupsBL.getLocaleValueFromCode(p_requestVO,p_requestArr[5]);
                        if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null)
                            throw new BTSLBaseException(this,"validateEVDRequestFormat",PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                    }
                    break;
                }
                default:
                    throw new BTSLBaseException(this,"validateEVDRequestFormat",PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT,0,new String[]{p_requestVO.getActualMessageFormat()},null);
            }
        }
        catch(BTSLBaseException be)
        {
            throw be;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            _log.error("validateEVDRequestFormat","  Exception while validating user message :"+e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UmniahUtil[validateEVDRequestFormat]","","","","Exception while validating user message" +" ,getting Exception="+e.getMessage());
            throw new BTSLBaseException(this,"validateEVDRequestFormat",PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if(_log.isDebugEnabled()) _log.debug("validateEVDRequestFormat","Exiting (p_requestVO.getSourceType()="+p_requestVO.getSourceType());
    
    }
    
    /**
     * Method to validate the PIN that is sent by user and that stored in database
     * @param p_con
     * @param p_channelUserVO
     * @param p_requestPin
     * @throws BTSLBaseException
     */
    public void validatePIN(Connection p_con, ChannelUserVO p_channelUserVO, String p_requestPin) throws BTSLBaseException
    {
        if (_log.isDebugEnabled()) _log.debug("validatePIN", "Entered with p_userPhoneVO:" + p_channelUserVO.toString() + " p_requestPin=" + p_requestPin);
        int updateStatus = 0;
        boolean increaseInvalidPinCount = false;
        boolean isUserBarred=false;
        int mintInDay=24*60;
        try
        {
            UserPhoneVO userPhoneVO=new UserPhoneVO();
            userPhoneVO=p_channelUserVO.getUserPhoneVO();
            //Force the user to change PIN if he has not changed the same in the defined no of days
            if (_log.isDebugEnabled()) _log.debug("validatePIN", "Modified Time=:" + userPhoneVO.getModifiedOn() + " userPhoneVO.getPinModifiedOn()=" +userPhoneVO.getPinModifiedOn()+"userPhoneVO.getCreatedOn()"+userPhoneVO.getCreatedOn());
            
            //added for OCI changes regarding to change PIN on 1st request
            if(userPhoneVO.isForcePinCheckReqd() && (userPhoneVO.getPinModifiedOn().getTime())==(userPhoneVO.getCreatedOn().getTime()))
                throw new BTSLBaseException("UmniahUtil", "validatePIN", PretupsErrorCodesI.CHNL_FIRST_REQUEST_PIN_CHANGE);
            
            int daysAfterChngPn=((Integer)PreferenceCache.getControlPreference(PreferenceI.C2S_DAYS_AFTER_CHANGE_PIN,p_channelUserVO.getNetworkID(),p_channelUserVO.getCategoryCode())).intValue();
            if(userPhoneVO.isForcePinCheckReqd() && userPhoneVO.getPinModifiedOn()!=null && ((userPhoneVO.getModifiedOn().getTime()-userPhoneVO.getPinModifiedOn().getTime())/(24*60*60*1000)) > daysAfterChngPn)
            {
                //Force the user to change PIN if he has not changed the same in the defined no of days
                if (_log.isDebugEnabled()) _log.debug("validatePIN", "Modified Time=:" + userPhoneVO.getModifiedOn() + " userPhoneVO.getPinModifiedOn()=" +userPhoneVO.getPinModifiedOn()+" Difference="+((userPhoneVO.getModifiedOn().getTime()-userPhoneVO.getPinModifiedOn().getTime())/(24*60*60*1000)));
                EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"OperatorUtil[validatePIN]","",userPhoneVO.getMsisdn(),"","Force User to change PIN after "+daysAfterChngPn+" days as last changed on "+userPhoneVO.getPinModifiedOn());  
                String strArr[]={String.valueOf(daysAfterChngPn)};                      
                throw new BTSLBaseException("UmniahUtil", "validatePIN", PretupsErrorCodesI.CHNL_ERROR_SNDR_FORCE_CHANGEPIN,0,strArr,null);
            }
            else
            {
                String decryptedPin = BTSLUtil.decryptText(userPhoneVO.getSmsPin());
                if (_log.isDebugEnabled())
                    _log.debug("validatePIN", "Sender MSISDN:" + userPhoneVO.getMsisdn() + " decrypted PIN of database=" + decryptedPin + " p_requestPin =" + p_requestPin);
              
                //added for Moldova Change the default PIN
                if(userPhoneVO.isForcePinCheckReqd() && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN)).equals(decryptedPin))
                    throw new BTSLBaseException("UmniahUtil", "validatePIN", PretupsErrorCodesI.CHNLUSR_CHANGE_DEFAULT_PIN);
        
                if (!decryptedPin.equals(p_requestPin))
                {
                    increaseInvalidPinCount = true;
                    if(userPhoneVO.getFirstInvalidPinTime()!=null)
                    {
                        //Check if PIN counters needs to be reset after the reset duration
                        long pnBlckRstDuration=((Long)PreferenceCache.getControlPreference(PreferenceI.C2S_PIN_BLK_RST_DURATION,p_channelUserVO.getNetworkID(),p_channelUserVO.getCategoryCode())).longValue();
                        if (_log.isDebugEnabled()) _log.debug("validatePIN", "p_userPhoneVO.getModifiedOn().getTime()="+userPhoneVO.getModifiedOn().getTime()+" p_userPhoneVO.getFirstInvalidPinTime().getTime()="+userPhoneVO.getFirstInvalidPinTime().getTime()+" Diff="+((userPhoneVO.getModifiedOn().getTime()-userPhoneVO.getFirstInvalidPinTime().getTime())/(60*1000))+" Allowed="+pnBlckRstDuration);
                        Calendar cal=Calendar.getInstance();
                        cal.setTime(userPhoneVO.getModifiedOn());
                        int d1=cal.get(Calendar.DAY_OF_YEAR);
                        cal.setTime(userPhoneVO.getFirstInvalidPinTime());
                        int d2=cal.get(Calendar.DAY_OF_YEAR);
                        if (_log.isDebugEnabled()) _log.debug("validatePIN", "Day Of year of Modified On="+d1+" Day Of year of FirstInvalidPinTime="+d2);
                        if(d1!=d2 && pnBlckRstDuration<=mintInDay)
                        {
                            //reset
                            userPhoneVO.setInvalidPinCount(1);
                            userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
                        }
                        else if(d1!=d2 && pnBlckRstDuration>mintInDay && (d1-d2)>=(pnBlckRstDuration/mintInDay))
                        {
                            //Reset
                            userPhoneVO.setInvalidPinCount(1);
                            userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
                        }
                        else if(((userPhoneVO.getModifiedOn().getTime()-userPhoneVO.getFirstInvalidPinTime().getTime())/(60*1000))<pnBlckRstDuration)
                        {
                            int maxPinBlckCnt=((Integer)PreferenceCache.getControlPreference(PreferenceI.C2S_MAX_PIN_BLOCK_COUNT_CODE,p_channelUserVO.getNetworkID(),p_channelUserVO.getCategoryCode())).intValue();
                            if (userPhoneVO.getInvalidPinCount() - maxPinBlckCnt == 0)
                            {
                                //Set The flag that indicates that we need to bar the user because of PIN Change
                                userPhoneVO.setInvalidPinCount(0);
                                userPhoneVO.setFirstInvalidPinTime(null);
                                userPhoneVO.setBarUserForInvalidPin(true);
                                isUserBarred=true;
                            }
                            else
                                userPhoneVO.setInvalidPinCount(userPhoneVO.getInvalidPinCount() + 1);
                                
                            if(userPhoneVO.getInvalidPinCount()==0)
                                userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
                        }
                        else
                        {
                            userPhoneVO.setInvalidPinCount(1);
                            userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
                        }
                    }
                    else
                    {
                        userPhoneVO.setInvalidPinCount(1);
                        userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
                    }
                } 
                else
                {
                    // initilize PIN Counters if ifPinCount>0
                    if (userPhoneVO.getInvalidPinCount() > 0)
                    {
                        userPhoneVO.setInvalidPinCount(0);
                        userPhoneVO.setFirstInvalidPinTime(null);
                        updateStatus = new ChannelUserDAO().updateSmsPinCounter(p_con, userPhoneVO);
                        if (updateStatus < 0)
                        {
                            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorUtil[validatePIN]","",userPhoneVO.getMsisdn(),"","Not able to update invalid PIN count for users");    
                            throw new BTSLBaseException("UmniahUtil", "validatePIN", PretupsErrorCodesI.ERROR_EXCEPTION);
                        }
                    }
                }
                if (increaseInvalidPinCount)
                {
                    updateStatus = new ChannelUserDAO().updateSmsPinCounter(p_con, userPhoneVO);
                    if (updateStatus > 0 && !isUserBarred)
                        throw new BTSLBaseException("UmniahUtil", "validatePIN", PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN);
                    else if(updateStatus > 0 && isUserBarred)
                        throw new BTSLBaseException("UmniahUtil", "validatePIN", PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK);
                    else
                    {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorUtil[validatePIN]","",userPhoneVO.getMsisdn(),"","Not able to update invalid PIN count for users");    
                        throw new BTSLBaseException("UmniahUtil", "validatePIN", PretupsErrorCodesI.ERROR_EXCEPTION);
                    }
                }
            }
            
        } 
        catch (BTSLBaseException bex)
        {
            throw bex;
        } 
        catch (Exception e)
        {
            _log.error("validatePIN", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorUtil[validatePIN]","","","","Exception:"+e.getMessage());  
            throw new BTSLBaseException("UmniahUtil", "validatePIN", PretupsErrorCodesI.ERROR_EXCEPTION);
        }
        finally{
            if (_log.isDebugEnabled())
                _log.debug("validatePIN", "Exiting with increase invalid Pin Count flag=" + increaseInvalidPinCount);
        }

    }

public String getOperatorFilteredMSISDN(String p_msisdn)
{
	if(p_msisdn.length()==8)
		p_msisdn="7"+p_msisdn;
	return Constants.getProperty("COUNTRY_CODE")+p_msisdn; 

}
}