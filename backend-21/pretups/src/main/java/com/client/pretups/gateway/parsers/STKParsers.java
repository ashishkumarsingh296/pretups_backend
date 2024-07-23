package com.client.pretups.gateway.parsers;

import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.services.businesslogic.SimProfileVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.stk.Exception348;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.client.pretups.stk.DES;
import com.client.pretups.stk.STKVNMCryptoUtil;

/**
 * Parser for Vietnam STK
 * @author
 * @since 27/09/2017
 *
 */
public class STKParsers extends ParserUtility {
    private static final Log LOG = LogFactory.getLog(STKParsers.class.getName());
    private static String chnlMessageSep = null;
      
    static {
        try {
            chnlMessageSep = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            if (BTSLUtil.isNullString(chnlMessageSep)) {
                chnlMessageSep = " "; 
            }
        } catch (Exception e) {
            LOG.errorTrace("static", e);
        }
    }
    public void parseRequestMessage(RequestVO requestVO) throws BTSLBaseException {
        requestVO.setDecryptedMessage(requestVO.getRequestMessage());
    }

    public void generateResponseMessage(RequestVO requestVO) {
        final String methodName = "generateResponseMessage";
        LogFactory.printLog(methodName,"Enter requestVO: " + requestVO , LOG);
        String message = null;
        if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
            message = requestVO.getSenderReturnMessage();
        } else {
            message = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
        }
        requestVO.setSenderReturnMessage(message);
        LogFactory.printLog(methodName,"Exiting message " + message,LOG);
    }

    public void parseChannelRequestMessage(RequestVO requestVO, Connection pCon) throws BTSLBaseException {
        final String methodName = "parseChannelRequestMessage";
        LogFactory.printLog(methodName,"Entered requestVO: " + requestVO , LOG);
        String message = null;
        if (!requestVO.getRequestMessage().contains(chnlMessageSep)){
            String decryptedMessageText=decrypt(requestVO);
            requestVO.setDecryptedMessage(decryptedMessageText.trim());
            int action = actionParser(requestVO);
            if (action!=-1){
                try {
                    parseRequest(action, requestVO);
                } catch (BTSLBaseException e) {
                    throw e;
                }
                requestVO.setMessageAlreadyParsed(true);
                requestVO.setPlainMessage(true);
                message = requestVO.getDecryptedMessage();
            }else{
                throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            }
        }else {
            requestVO.setDecryptedMessage(requestVO.getRequestMessage());
            int action = actionParser(requestVO);
            if (action!=-1){
                try {
                    parseRequest(action, requestVO);
                } catch (BTSLBaseException e) {
                    throw e;
                }
            }else{
                requestVO.setDecryptedMessage(requestVO.getRequestMessage());
            }
            requestVO.setMessageAlreadyParsed(true);
            requestVO.setPlainMessage(true);
            message = requestVO.getDecryptedMessage();
        }
        if (message.contains(chnlMessageSep) && "SUSRESUSR".equals(message.substring(0, message.indexOf(chnlMessageSep)))) {
            suspendResumeChannelUserParse(requestVO);
        }
        ChannelUserBL.updateUserInfo(pCon, requestVO);
        LogFactory.printLog(methodName,"Exiting requestVO ID : " + requestVO.getRequestIDStr(),LOG);
    }

    private String decrypt(RequestVO requestVO) {
        final String methodName = "decrypt";
        LogFactory.printLog(methodName,"Enter request VO : " + requestVO , LOG);
        String iccidLength=null;
        int iccidLengthInt=0;
        try{
            iccidLength= Constants.getProperty("ICCID_LENGTH_FOR_STK");
            iccidLengthInt=Integer.parseInt(iccidLength);
        }catch(Exception e){
            iccidLength="40";
            iccidLengthInt=Integer.parseInt(iccidLength);
        }
        String iccid =fromHextoString(requestVO.getRequestMessage().toUpperCase().substring(0,iccidLengthInt));
        String iccidLast16=iccid.substring(4,iccid.length());
        int lenMessage=Integer.parseInt(requestVO.getRequestMessage().toUpperCase().substring(iccidLengthInt+2,iccidLengthInt+4), 16);
        String parsedMessage=requestVO.getRequestMessage().toUpperCase().substring(iccidLengthInt+4,iccidLengthInt+4+2*lenMessage);
        String language =requestVO.getRequestMessage().toUpperCase().substring(requestVO.getRequestMessage().lastIndexOf("2F")).split("20")[1].trim();
        String counter =requestVO.getRequestMessage().toUpperCase().substring(requestVO.getRequestMessage().lastIndexOf("2F")).split("20")[2].trim();
        requestVO.setTempTransID(counter);
        String zpk="";
        try {
            String zmk=((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.STK_MASTER_KEY)).trim();
            String zpk1= DES.cipher(fromHextoString(zmk.substring(0, zmk.length()/2)),iccidLast16);
            String zpk2= DES.cipher(fromHextoString(zmk.substring(zmk.length()/2,zmk.length())),iccidLast16);
            zpk=zpk1.substring(0,16)+zpk2.substring(0,16);    
        } catch (Exception e) {
            LogFactory.printError(methodName,"Issue while creating ZPK" , LOG);
        }
        STKVNMCryptoUtil cryptoUtil=new STKVNMCryptoUtil();
        String decryptedMessageText="";
        try {
            decryptedMessageText= cryptoUtil.decrypt348Data(parsedMessage, zpk, new SimProfileVO());
        } catch (Exception348 e) {
            LogFactory.printError(methodName,"Issue in decrypt348Data" , LOG);
        } catch (GeneralSecurityException e) {
            LogFactory.printError(methodName,"Issue while decrypting" , LOG);
        }
        if ("10".equals(language)){
            decryptedMessageText=decryptedMessageText.trim()+chnlMessageSep+"0";
            requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails("0"));
        }else if("11".equals(language)){
            decryptedMessageText=decryptedMessageText.trim()+chnlMessageSep+"1";
            requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails("1"));
        }else{
            decryptedMessageText=decryptedMessageText.trim()+chnlMessageSep+"0";
            requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
        }
        LogFactory.printLog(methodName,"Enter decryptedMessageText: " + decryptedMessageText , LOG);
        return decryptedMessageText;
    }

    private static String fromHextoString(String hexString){
        final String methodName = "fromHextoString";
        LogFactory.printLog(methodName,"Enter hexString: " + hexString , LOG);
        byte[] bytes;
        String asciiString="";
        try {
            bytes = Hex.decodeHex(hexString.toCharArray());
            asciiString= new String(bytes);
        } catch (DecoderException e) {
            LOG.errorTrace(methodName, e);
            LogFactory.printLog(methodName,"Hex.decodeHex() caused an EXCEPTION" , LOG);
        }
        LogFactory.printLog(methodName,"Enter asciiString: " + asciiString , LOG);
        return asciiString;
    }
    
    
    /*
     * In case Vodafone-Egypt there is a issue that
     * if a user logined in (WEB) using language ENGLISH
     * and user language is Arabic in USER_PHONES table.
     * The error message displayed to channel user is in language Arabic
     * whereas it should be in ENGLISH language.
     * So extract sender language from request message and set it in
     * SenderLocale.
     */
    public void generateChannelResponseMessage(RequestVO requestVO) {
        final String methodName = "generateChannelResponseMessage";
        LogFactory.printLog(methodName,"Enter request VO : " + requestVO , LOG);
        String message = null;
        if (PretupsI.REQUEST_SOURCE_TYPE_WEB.equals(requestVO.getRequestGatewayType()) && requestVO.getSenderLocale() == null) {
            message = setSenderLocaleWeb(requestVO);
        }
        if (BTSLUtil.isNullString(message)) {
            if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
                message = requestVO.getSenderReturnMessage();
            } else {
                message = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
            }

        }
        requestVO.setSenderReturnMessage(message);
        LogFactory.printLog(methodName,"Exiting message: " + message,LOG);
    }

    public String setSenderLocaleWeb(RequestVO requestVO) {
        final String methodName = "setSenderLocaleWeb";
        LogFactory.printLog(methodName,"Entered requestVO: " + requestVO , LOG);
        String message = null;
        String msg = requestVO.getRequestMessage();
        String[] reqMessage = msg.split((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR));
        if (reqMessage != null && (PretupsI.SERVICE_TYPE_CHNL_RECHARGE.equals(reqMessage[0]) || 
                PretupsI.SERVICE_TYPE_CHNL_BILLPAY.equals(reqMessage[0])) && reqMessage.length == 7) {
            if (BTSLUtil.isNullString(reqMessage[4])) {
                requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
            } else {
                requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(reqMessage[4]));
            }
            message = BTSLUtil.getMessage(requestVO.getSenderLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
        }
        LogFactory.printLog(methodName,"Exiting message: " + message,LOG);
        return message;
    }

    public void parseChannelRequestMessage(Connection con, RequestVO requestVO) throws BTSLBaseException {
        final String methodName = "parseChannelRequestMessage";
        LogFactory.printLog(methodName,"Enter requestVO : " + requestVO , LOG);
        requestVO.setDecryptedMessage(requestVO.getRequestMessage());
        updateUserInfo(con, requestVO);
        LogFactory.printLog(methodName,"Exiting requestVO ID: " + requestVO.getRequestIDStr(),LOG);
    }

    /**
     * @author gaurav.pandey
     * @param requestVO
     * @return void
     * added for suspend resume channel use (Road map 5.8)
     */

    private void suspendResumeChannelUserParse(RequestVO requestVO) {
        final String methodName = "suspendResumeChannelUserParse";
        LogFactory.printLog(methodName,"Enter requestVO : " + requestVO +"requestVO.getDecryptedMessage()"+requestVO.getDecryptedMessage(), LOG);
        String reqMessage;
        try {
            reqMessage = requestVO.getDecryptedMessage();
            HashMap requestHashMap = requestVO.getRequestMap();
            if (requestHashMap == null) {
                requestHashMap = new HashMap();
            }
            String[] temp = reqMessage.trim().split(" ");
            requestHashMap.put("TYPE", temp[0]);
            requestHashMap.put("MSISDN", temp[1]);
            requestHashMap.put("ACTION", temp[2]);
            requestHashMap.put("PIN", temp[3]);
            String msisdn = temp[1];
            requestVO.setDecryptedMessage(reqMessage);
            requestVO.setRequestMap(requestHashMap);
            requestVO.setReceiverMsisdn(msisdn);
        }catch (Exception e) {
            requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            LogFactory.printError(methodName, "Exception e: " + e,LOG);
        } finally {
            LogFactory.printLog(methodName,"Exiting requestVO ID: " + requestVO.getRequestIDStr(),LOG);
        }

    }

    /**
     * Method to parse the Operator request
     * 
     * @param requestVO
     * @throws BTSLBaseException
     */
    public void parseOperatorRequestMessage(RequestVO requestVO) throws BTSLBaseException {
        final String methodName = "parseOperatorRequestMessage";
        LogFactory.printLog(methodName,"Enter requestVO: " + requestVO , LOG);
        requestVO.setDecryptedMessage(requestVO.getRequestMessage());
        if (!BTSLUtil.isNullString(requestVO.getRequestMSISDN())) {
            String filteredMsisdn = PretupsBL.getFilteredMSISDN(requestVO.getRequestMSISDN());
            requestVO.setFilteredMSISDN(filteredMsisdn);
            requestVO.setMessageSentMsisdn(filteredMsisdn);
        }
        LogFactory.printLog(methodName,"Exiting ",LOG);
    }
    /**
     * Method to find the action (Keyword) in the request
     * 
     * @param requestVO
     * @return
     * @throws BTSLBaseException
     */
    public static int actionParser(RequestVO requestVO) throws BTSLBaseException {
        final String methodName = "actionParser";
        final String requestStr = requestVO.getDecryptedMessage();
        LogFactory.printLog(methodName,"Enter requestVO " + requestVO.toString() + " requestStr: " + requestStr , LOG);
        int action = -1;
        String type = null;
        try {
            type =requestStr.substring(0, requestStr.indexOf(" "));
            if ("RELOAD".equals(type)) {
                action = ACTION_CHNL_CREDIT_TRANSFER;
            } else if ("BALANCE".equals(type)) {
                action = ACTION_CHNL_BALANCE_ENQUIRY;
            } else if ("CASHTRANSFER".equals(type)){
                action =ACTION_CHNL_TRANSFER_MESSAGE;
            } else if ("CHGMPIN".equals(type)){
                action=ACTION_CHNL_CHANGE_PIN;
            } else if ("RECHSOLD".equals(type)){
                action =ACTION_CHNL_DAILY_STATUS_REPORT;
            } else if ("TK".equals(type)){
                action =ACTION_BALANCE;
            } else if ("DOIMK".equals(type)){
                action =ACTION_CHGMPIN;
            } else if ("CT".equals(type)){
                action =ACTION_CASHTRANSFER;
            } else if ("NT".equals(type)){
                action =ACTION_RELOAD;
            }
            requestVO.setActionValue(action);
        } catch (Exception e) {
            LogFactory.printError(methodName," Exception e: " + e, LOG);
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            LogFactory.printLog(methodName,"Exit Action: " + action,LOG);
        }
        return action;    
    }
    /**
     * Method to parse request of P2P on basis of action
     * 
     * @param action
     * @param requestVO
     * @throws Exception
     */
    public void parseRequest(int action, RequestVO requestVO) throws BTSLBaseException {
        final String methodName = "parseRequest";
        LogFactory.printLog(methodName,"Enter Request ID = " + requestVO.getRequestID() + " action=" + action+ " requestVO.getDecryptedMessage()="
        + requestVO.getDecryptedMessage() , LOG);
        String[] parts =  requestVO.getDecryptedMessage().split(chnlMessageSep);
        String parsedRequestStr="";
        try {
            switch (action){
                case ACTION_CHNL_CREDIT_TRANSFER:
                    if (parts.length==5){
                        String selector=null;
                        final ServiceSelectorMappingVO serviceSelectorMappingVO = 
                        		ServiceSelectorMappingCache.getDefaultSelectorForServiceType(PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
                        if (serviceSelectorMappingVO != null) {
                            selector = serviceSelectorMappingVO.getSelectorCode();
                        }
                        parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_RECHARGE + chnlMessageSep + parts[2] + chnlMessageSep + parts[3] + 
                        		chnlMessageSep + selector +chnlMessageSep + parts[4] +  chnlMessageSep + parts[1];
                        requestVO.setDecryptedMessage(parsedRequestStr);
                    }else{
                        requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                        throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                    }
                    break;
                case ACTION_CHNL_BALANCE_ENQUIRY:
                    if (parts.length==3){
                        parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_BALANCE_ENQUIRY + chnlMessageSep + parts[1];
                        requestVO.setDecryptedMessage(parsedRequestStr);
                    }else{
                        requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                        throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                    }
                    break;
                case ACTION_CHNL_TRANSFER_MESSAGE:
                    if (parts.length==5){
                        parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_TRANSFER + chnlMessageSep + parts[2] + chnlMessageSep + parts[3] + 
                        		chnlMessageSep + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_PRODUCT)) + chnlMessageSep + parts[1];
                        requestVO.setDecryptedMessage(parsedRequestStr);
                    }else{
                        requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                        throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                    }
                    break;
                case ACTION_CHNL_CHANGE_PIN:
                    if (parts.length==4){
                        parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_CHANGEPIN + chnlMessageSep + parts[1] + chnlMessageSep + parts[2] + chnlMessageSep +parts[2];
                        requestVO.setDecryptedMessage(parsedRequestStr);
                    }else{
                        requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                        throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                    }
                    break;
                case ACTION_CHNL_DAILY_STATUS_REPORT:
                    if (parts.length==4){
                        if ( "TODAY".equals(parts[2].trim())){
                            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_DAILY_STATUS_REPORT + chnlMessageSep + parts[1];
                            requestVO.setDecryptedMessage(parsedRequestStr);
                        }else {
                            requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                            throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                        }
                    }else{
                        requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                        throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                    }            
                    break;
                case ACTION_BALANCE:
                    if (parts.length==2){
                        parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_BALANCE_ENQUIRY + chnlMessageSep + parts[1];
                        requestVO.setDecryptedMessage(parsedRequestStr);
                    }else{
                        requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                        throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                    }
                    break;
                case ACTION_CHGMPIN:
                    if (parts.length==3){
                        parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_CHANGEPIN + chnlMessageSep + parts[1] + chnlMessageSep + parts[2] + chnlMessageSep +parts[2];
                        requestVO.setDecryptedMessage(parsedRequestStr);
                    }else{
                        requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                        throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                    }
                    break;
                case ACTION_CASHTRANSFER:
                    if (parts.length==4){
                        parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_TRANSFER + chnlMessageSep + parts[2] + chnlMessageSep + parts[3] + 
                        		chnlMessageSep + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_PRODUCT)) + chnlMessageSep + parts[1];
                        requestVO.setDecryptedMessage(parsedRequestStr);
                    }else{
                        requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                        throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                    }
                    break;
                case ACTION_RELOAD:
                    if (parts.length==4){
                        parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_RECHARGE + chnlMessageSep + parts[2] + chnlMessageSep + parts[3] +  chnlMessageSep + parts[1];
                        requestVO.setDecryptedMessage(parsedRequestStr);
                    }else{
                        requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                        throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
                    }
                    break;
                default:
                    throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            }
            } catch (BTSLBaseException e) {
            LogFactory.printError(methodName," Exception e: " + e, LOG);
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            LogFactory.printLog(methodName,"Exit parsedRequestStr: " + parsedRequestStr,LOG);
        }
    }
    
    /**
     * Method to load and Validate the channel User Details (On MSISDN by
     * Default)
     * 
     * @param con
     * @param requestVO
     * @throws BTSLBaseException
     */
    public ChannelUserVO loadValidateUserDetails(Connection con, RequestVO requestVO) throws BTSLBaseException {
        final String methodName = "loadValidateUserDetails";
        LogFactory.printLog(methodName,"Enter Request ID=" + requestVO.getRequestID()  , LOG);
        ChannelUserVO channelUserVO = null;
        ChannelUserVO staffUserVO = null;
        try {
            channelUserVO = _channelUserDAO.loadChannelUserDetails(con, requestVO.getFilteredMSISDN());
            if (channelUserVO != null && PretupsI.CATEGORY_USER_TYPE.equalsIgnoreCase(channelUserVO.getUserType())) {
                channelUserVO.setGeographicalAreaList(new GeographicalDomainDAO().loadUserGeographyList(con, channelUserVO.getUserID(), channelUserVO.getNetworkID()));
                requestVO.setActiverUserId(channelUserVO.getUserID());
            }
            validateUserDetails(requestVO, channelUserVO);
            if (channelUserVO != null) {
                requestVO.setMessageSentMsisdn(channelUserVO.getUserPhoneVO().getMsisdn());

                if (!channelUserVO.getUserID().equals(requestVO.getActiverUserId())) {
                    channelUserVO.setStaffUser(true);
                    staffUserVO = _channelUserDAO.loadChannelUserDetailsByUserId(con, requestVO.getActiverUserId(), channelUserVO.getUserID());
                    validateUserDetails(requestVO, staffUserVO);
                    channelUserVO.setActiveUserID(staffUserVO.getUserID());
                    staffUserVO.setActiveUserID(staffUserVO.getUserID());
                    staffUserVO.setStaffUser(true);
                    if (staffUserVO != null && !PretupsI.NOT_AVAILABLE.equals(staffUserVO.getUserPhoneVO().getMsisdn())) {
                        requestVO.setMessageSentMsisdn(staffUserVO.getUserPhoneVO().getMsisdn());
                    } else {
                        final UserPhoneVO userPhoneVO = channelUserVO.getUserPhoneVO();
                        requestVO.setSenderLocale(new Locale(userPhoneVO.getPhoneLanguage(), userPhoneVO.getCountry()));
                    }
                    channelUserVO.setStaffUserDetails(staffUserVO);
                } else {
                    channelUserVO.setActiveUserID(channelUserVO.getUserID());
                }
            }

            requestVO.setSenderVO(channelUserVO);

            if (!requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                requestVO.setPinValidationRequired(false);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } finally {
            LogFactory.printLog(methodName,"Exit UserVO ",LOG);
        }
        if (channelUserVO != null && !channelUserVO.isStaffUser()) {
            return channelUserVO;
        } else {
            return staffUserVO;
        }
    }

    /**
     * Method to validate User Details, check various status
     * 
     * @param requestVO
     * @param channelUserVO
     * @throws BTSLBaseException
     */
    public void validateUserDetails(RequestVO requestVO, ChannelUserVO channelUserVO) throws BTSLBaseException {
        final String methodName = "loadValidateUserDetails";
        LogFactory.printLog(methodName,"Enter Request ID=" + requestVO.getRequestID()  , LOG);
        try {
            if (channelUserVO == null) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_NO_SUCH_USER);
            }
            requestVO.setLocale(new Locale((channelUserVO.getUserPhoneVO()).getPhoneLanguage(), (channelUserVO.getUserPhoneVO()).getCountry()));
            if (channelUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND)) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SENDER_SUSPEND);
            }
            boolean statusAllowed = false;
            final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkID(), channelUserVO.getCategoryCode(), channelUserVO
                .getUserType(), requestVO.getRequestGatewayType());
            if (userStatusVO == null) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
            } else {
                final String userStatusAllowed = userStatusVO.getUserSenderAllowed();
                final String[] status = userStatusAllowed.split(",");
                for (int i = 0; i < status.length; i++) {
                    if (status[i].equals(channelUserVO.getStatus())) {
                        statusAllowed = true;
                    }
                }
                if (statusAllowed) {
                    if (!channelUserVO.getCategoryVO().getAllowedGatewayTypes().contains(requestVO.getMessageGatewayVO().getGatewayType())) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_CAT_GATETYPENOTALLOWED);
                    } else if (channelUserVO.getGeographicalCodeStatus().equals(PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_SUSPEND)) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_GEODOMAIN_SUSPEND);
                    }
                } else {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SENDER_NOTALLOWED);
                }
            }
            final String lastTransferID = (channelUserVO.getUserPhoneVO()).getTempTransferID();
            if (!BTSLUtil.isNullString(lastTransferID) && !BTSLUtil.isNullString(requestVO.getTempTransID()) ) {
                
                String dbTxnId=lastTransferID;
                String inTxnId=requestVO.getTempTransID();
                if (inTxnId.equals(dbTxnId)) {
                    LogFactory.printError(methodName,"requestVO.getRequestIDStr(): "+ requestVO.getRequestIDStr()+
                        " MSISDN=" + requestVO.getFilteredMSISDN() + " Temp Trans ID in DB=" + dbTxnId + " from STK:" + inTxnId,LOG);
                    EventHandler
                        .handle(
                            EventIDI.SYSTEM_INFO,
                            EventComponentI.SYSTEM,
                            EventStatusI.RAISED,
                            EventLevelI.INFO,
                            "PretupsBL[validateTempTransactionID]",
                            requestVO.getRequestIDStr(),
                            requestVO.getFilteredMSISDN(),
                            "",
                            "Incoming transaction is less than or equal to that stored in DB for the number: " + 
                            requestVO.getFilteredMSISDN() + " in DB=" + dbTxnId + " from STK:" + inTxnId);
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_TEMPTRANSID_INVALID);
                }
                channelUserVO.getUserPhoneVO().setTempTransferID(requestVO.getTempTransID());
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            LogFactory.printError(methodName, "Exception e: " + e,LOG);
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            LogFactory.printLog(methodName,"Exiting Request ID = " + requestVO.getRequestID(),LOG);
        }
    }
}


