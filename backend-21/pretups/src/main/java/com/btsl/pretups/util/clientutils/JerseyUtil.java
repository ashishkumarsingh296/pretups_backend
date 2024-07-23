package com.btsl.pretups.util.clientutils;

import java.sql.Connection;
import java.util.HashMap;
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
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @(#)ClaroUtil.java
 *                    Copyright(c) 2010, Comviva Technologies Ltd.
 *                    All Rights Reserved
 * 
 *                    Jersey Util class
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Trasha Dewan August 29, 2017 Initital Creation
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 * 
 */

public class JerseyUtil extends OperatorUtil{
    
    private static final Log LOG = LogFactory.getLog(JerseyUtil.class.getName());
    
    public HashMap validatePassword(String loginID, String password){
        String methodName="validatePassword";
        LogFactory.printLog(methodName, "Entered, p_userID= "+loginID+", Password= "+password ,LOG);
        HashMap messageMap=new HashMap();
        String defaultPin = BTSLUtil.getDefaultPasswordNumeric(password);
        if(defaultPin.equals(password)) {
            return messageMap;
        }
        defaultPin = BTSLUtil.getDefaultPasswordText(password);
        if(defaultPin.equals(password)) {
            return messageMap;
        }
        if (password.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue() || password.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue()){
            String[] args={String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue()),String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue())};
            messageMap.put("operatorutil.validatepassword.error.passwordlenerr", args);
        }
        int result=BTSLUtil.isSMSPinValid(password);
        if(result==-1) {
            messageMap.put("operatorutil.validatepassword.error.passwordsamedigit",null);
        } else if(result==1) {
            messageMap.put("operatorutil.validatepassword.error.passwordconsecutive",null);
        }
        if(password.trim().length()!= password.length()){
            messageMap.put("operatorutil.validatepassword.error.nospace", null);
        }
        // For OCI Password Should contains atleast one character
        if(!BTSLUtil.containsChar(password)){
            messageMap.put("operatorutil.validatepassword.error.passwordnotcontainschar",null);
        }    

        // for special character
        String specialChar=Constants.getProperty("SPECIAL_CHARACTER_PASSWORD_VALIDATION");
        if(!BTSLUtil.isNullString(specialChar)){
            String[] specialCharArray={specialChar};
            String[] passwordCharArray=specialChar.split(",");
            boolean specialCharFlag=false;
            for(int i=0,j=passwordCharArray.length;i<j;i++){
                if(password.contains(passwordCharArray[i])){
                    specialCharFlag=true;
                    break;
                }
            }
            if(!specialCharFlag){
                messageMap.put("operatorutil.validatepassword.error.passwordspecialchar",specialCharArray);
            }         
        }
        // for number
        String[]passwordNumberStrArray={"0","1","2","3","4","5","6","7","8","9"};
        boolean numberStrFlag=false;
        for(int i=0,j=passwordNumberStrArray.length;i<j;i++){
            if(password.contains(passwordNumberStrArray[i])){
                numberStrFlag=true;
                break;
            }
        }
        if(!numberStrFlag){
            messageMap.put("operatorutil.validatepassword.error.passwordnumberchar",null);
        }
        if(loginID.equals(password)){
            messageMap.put("operatorutil.validatepassword.error.sameusernamepassword",null);
        }
        if(!BTSLUtil.containsCapChar(password)){
            messageMap.put("operatorutil.validatepassword.error.passwordnotcontaincapschar",null);
        }
        
        LogFactory.printLog(methodName, "Exiting messageMap.size()="+messageMap.size() ,LOG);
        return messageMap;
    }

    /**
     * Method that will validate the user message sent
     * @param con
     * @param c2sTransferVO
     * @param requestVO
     * @throws BTSLBaseException
     * @see com.btsl.pretups.util.OperatorUtilI#validateC2SRechargeRequest(Connection, C2STransferVO, RequestVO)
     */
    public void validateC2SRechargeRequest(Connection con,C2STransferVO c2sTransferVO,RequestVO requestVO) throws BTSLBaseException{
        final String obj = "validateC2SRechargeRequest";
        try{
            String[] requestArr=requestVO.getRequestMessageArray();
            String custMsisdn=null;
            String requestAmtStr=null;
            ChannelUserVO channelUserVO=(ChannelUserVO)c2sTransferVO.getSenderVO();
            UserPhoneVO userPhoneVO=null;
            if(!channelUserVO.isStaffUser()) {
                userPhoneVO=(UserPhoneVO)channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO=(UserPhoneVO)channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }

            int messageLen=requestArr.length;
            LogFactory.printLog(obj, "messageLen: "+messageLen ,LOG);
            for(int i=0;i<messageLen;i++){
                LogFactory.printLog(obj, "i: "+i+" value: "+requestArr[i] ,LOG);
            }
            switch(messageLen){
            case 4:{
                //Do the 000 check Default PIN 
                    if(userPhoneVO.getPinRequired().equals(PretupsI.YES)){
                        try{
                            ChannelUserBL.validatePIN(con,channelUserVO,requestArr[3]);
                        }catch(BTSLBaseException be){
                            if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  
                            		(be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                                con.commit();
                            }
                            throw be;
                       }    
                    }                    
                    ReceiverVO receiverVO=new ReceiverVO();
                    //Customer MSISDN Validation
                    custMsisdn=requestArr[1];
                    //Change for the SID logic
                    requestVO.setSid(custMsisdn);
                    receiverVO.setSid(custMsisdn);
                    PrivateRchrgVO prvo=null;
                    if((prvo=getPrivateRechargeDetails(con,custMsisdn))!=null){
                        c2sTransferVO.setSubscriberSID(custMsisdn);
                        custMsisdn=prvo.getMsisdn();                        
                    }                        
                    PretupsBL.validateMsisdn(con,receiverVO,c2sTransferVO.getRequestID(),custMsisdn);
                    //Recharge amount Validation
                    requestAmtStr=requestArr[2];
                    if(("SMS".equals(requestVO.getSourceType())||"SMSC".equals(requestVO.getSourceType())) && requestAmtStr.startsWith("0")){      
                        try{
                            double amount=Double.parseDouble(requestAmtStr);      
                            amount=amount/100;
                            requestAmtStr=Double.toString(amount);
                          }catch (NumberFormatException e) {
                            LogFactory.printLog(obj, "requestAmtStr  isn't parsable." ,LOG);
                        }
                    }
                    PretupsBL.validateAmount(c2sTransferVO,requestAmtStr);
                    c2sTransferVO.setReceiverVO(receiverVO);        
                    //Changed on 27/05/07 for Service Type selector Mapping
                    ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(c2sTransferVO.getServiceType());
                    if(serviceSelectorMappingVO!=null) {
                        requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                    }                                    
                    requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                    break;
                }

            case 5:{
                    if(userPhoneVO.getPinRequired().equals(PretupsI.YES)){
                        try{
                            ChannelUserBL.validatePIN(con,channelUserVO,requestArr[4]);
                        }catch(BTSLBaseException be){
                            if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  
                            		(be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                                con.commit();
                        }
                            throw be;
                        }    
                    }                        
                    ReceiverVO receiverVO=new ReceiverVO();
                    //Customer MSISDN Validation
                    custMsisdn=requestArr[1];
                    requestVO.setSid(custMsisdn);
                    receiverVO.setSid(custMsisdn);
                    PrivateRchrgVO prvo=null;
                    if((prvo=getPrivateRechargeDetails(con,custMsisdn))!=null){
                        c2sTransferVO.setSubscriberSID(custMsisdn);
                        custMsisdn=prvo.getMsisdn();                        
                    }
                    PretupsBL.validateMsisdn(con,receiverVO,c2sTransferVO.getRequestID(),custMsisdn);

                    //Recharge amount Validation
                    requestAmtStr=requestArr[2];
                    if(("SMS".equals(requestVO.getSourceType())||"SMSC".equals(requestVO.getSourceType())) && requestAmtStr.startsWith("0")){      
                        try{
                            double amount=Double.parseDouble(requestAmtStr);      
                            amount=amount/100;
                            requestAmtStr=Double.toString(amount);
                           }catch (NumberFormatException e) {
                            LogFactory.printLog(obj, " requestAmtStr is not parsable." ,LOG);
                           }
                    }
                    PretupsBL.validateAmount(c2sTransferVO,requestAmtStr);
                    c2sTransferVO.setReceiverVO(receiverVO);

                    if(BTSLUtil.isNullString(requestArr[3])) {
                        requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                    } else{
                        int langCode=PretupsBL.getLocaleValueFromCode(requestVO,requestArr[3]);
                        if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null) {
                            throw new BTSLBaseException(this,obj,PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                        }
                        requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                    }
                    //Changed on 27/05/07 for Service Type selector Mapping
                    ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(c2sTransferVO.getServiceType());
                    if(serviceSelectorMappingVO!=null) {
                        requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                    }                                    
                    
                    break;
            }

            case 6:{
                    if(userPhoneVO.getPinRequired().equals(PretupsI.YES)){
                        try{
                            ChannelUserBL.validatePIN(con,channelUserVO,requestArr[5]);
                        }catch(BTSLBaseException be){
                            if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  
                        	    	(be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                                con.commit();
                            }
                            throw be;
                        }                                
                    }

                    ReceiverVO receiverVO=new ReceiverVO();
                    //Customer MSISDN Validation
                    custMsisdn=requestArr[1];
                    //Change for the SID logic
                    requestVO.setSid(custMsisdn);
                    receiverVO.setSid(custMsisdn);
                    PrivateRchrgVO prvo=null;
                    if((prvo=getPrivateRechargeDetails(con,custMsisdn))!=null){
                        c2sTransferVO.setSubscriberSID(custMsisdn);
                        custMsisdn=prvo.getMsisdn();                        
                    }
                    PretupsBL.validateMsisdn(con,receiverVO,c2sTransferVO.getRequestID(),custMsisdn);

                    //Recharge amount Validation
                    requestAmtStr=requestArr[2];
                    if(("SMS".equals(requestVO.getSourceType())||"SMSC".equals(requestVO.getSourceType())) && requestAmtStr.startsWith("0")){      
                        try{
                            double amount=Double.parseDouble(requestAmtStr);      
                            amount=amount/100;
                            requestAmtStr=Double.toString(amount);
                        }catch (NumberFormatException e) {
                            LogFactory.printLog(obj, "requestAmtStr isn't parsable." ,LOG);
                        }
                    }
                    PretupsBL.validateAmount(c2sTransferVO,requestAmtStr);
                    c2sTransferVO.setReceiverVO(receiverVO);
                    if(BTSLUtil.isNullString(requestArr[3])){
                        if("en".equalsIgnoreCase(requestVO.getLocale().getLanguage())){
                        //Changed on 27/05/07 for Service Type selector Mapping
                            ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(c2sTransferVO.getServiceType());
                            if(serviceSelectorMappingVO!=null) {
                                requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                            }                                    
                       }
                    //changed for CRE_INT_CR00029 by ankit Zindal
                    //in case of binary message we will set default value after calling getselectorvaluefromcode method
                    } else {
                        requestVO.setReqSelector(requestArr[3]);
                }

                    PretupsBL.getSelectorValueFromCode(requestVO);
                    if(BTSLUtil.isNullString(requestVO.getReqSelector())){
                    //Changed on 27/05/07 for Service Type selector Mapping
                        ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(c2sTransferVO.getServiceType());
                        if(serviceSelectorMappingVO!=null) {
                            requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                        }                                    
                    }
                    if(BTSLUtil.isNullString(requestArr[4])) {
                        requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                    } else{
                        int langCode=PretupsBL.getLocaleValueFromCode(requestVO,requestArr[4]);
                        if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null) {
                            throw new BTSLBaseException(this,obj,PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                        }
                        requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                    }
                    break;
            }
            case 7:{
                    if(userPhoneVO.getPinRequired().equals(PretupsI.YES)){
                        try{
                            ChannelUserBL.validatePIN(con,channelUserVO,requestArr[6]);
                        }catch(BTSLBaseException be){
                            if(be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) ||  
                            		(be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                                con.commit();
                        }
                            throw be;
                        }                                
                    }

                    ReceiverVO receiverVO=new ReceiverVO();
                    custMsisdn=requestArr[1];
                    requestVO.setSid(custMsisdn);
                    receiverVO.setSid(custMsisdn);
                    PrivateRchrgVO prvo=null;
                    if((prvo=getPrivateRechargeDetails(con,custMsisdn))!=null){
                        c2sTransferVO.setSubscriberSID(custMsisdn);
                        custMsisdn=prvo.getMsisdn();                        
                    }    

                    PretupsBL.validateMsisdn(con,receiverVO,c2sTransferVO.getRequestID(),custMsisdn);

                    //Recharge amount Validation
                    requestAmtStr=requestArr[2];
                    if(("SMS".equals(requestVO.getSourceType())||"SMSC".equals(requestVO.getSourceType())) && requestAmtStr.startsWith("0")){      
                        try{
                            double amount=Double.parseDouble(requestAmtStr);      
                            amount=amount/100;
                            requestAmtStr=Double.toString(amount);
                        }catch (NumberFormatException e) {
                            LogFactory.printLog(obj, "requestAmtStr isn't parsable." ,LOG);
                        }
                    }
                    PretupsBL.validateAmount(c2sTransferVO,requestAmtStr);
                    c2sTransferVO.setReceiverVO(receiverVO);
                    if(BTSLUtil.isNullString(requestArr[3])){
                        if("en".equalsIgnoreCase(requestVO.getLocale().getLanguage())){
                            ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(c2sTransferVO.getServiceType());
                            if(serviceSelectorMappingVO!=null) {
                                requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                            }                                    
                       }
                    } else {
                        requestVO.setReqSelector(requestArr[3]);
                    }

                    PretupsBL.getSelectorValueFromCode(requestVO);
                    if(BTSLUtil.isNullString(requestVO.getReqSelector())){
                        ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(c2sTransferVO.getServiceType());
                        if(serviceSelectorMappingVO!=null) {
                            requestVO.setReqSelector(serviceSelectorMappingVO.getSelectorCode());
                        }                                    
                    }
                    //For handling of sender locale
                    if(BTSLUtil.isNullString(requestArr[4])) {
                        requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                    } else{
                        int langCode=PretupsBL.getLocaleValueFromCode(requestVO,requestArr[4]);
                        requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                        c2sTransferVO.setLocale(requestVO.getSenderLocale());
                        c2sTransferVO.setLanguage(c2sTransferVO.getLocale().getLanguage());
                        c2sTransferVO.setCountry(c2sTransferVO.getLocale().getCountry());
                    }
                    LogFactory.printLog(obj,"sender locale: ="+requestVO.getSenderLocale() ,LOG);
                    if(BTSLUtil.isNullString(requestArr[5])) {
                        requestVO.setReceiverLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                    } else{
                        int langCode=PretupsBL.getLocaleValueFromCode(requestVO,requestArr[5]);
                        if(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode))==null) {
                            throw new BTSLBaseException(this,obj,PretupsErrorCodesI.C2S_INVALID_PAYEE_NOT_LANG);
                        }
                        requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(String.valueOf(langCode)));
                    }
                    break;
                }
            default:
                    throw new BTSLBaseException(this,obj,PretupsErrorCodesI.C2S_INVALID_MESSAGE_FORMAT,0,new String[]{requestVO.getActualMessageFormat()},null);
            }
        }catch(BTSLBaseException be){
            throw be;
        }catch(Exception e){
            LOG.errorTrace(obj, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,
            		"PretupsBL[validateC2SRechargeRequest]","","","","Exception while validating user message" +" ,getting Exception="+e.getMessage());
            throw new BTSLBaseException(this,obj,PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        LogFactory.printLog(obj,"Exiting " ,LOG);
    }    
}
