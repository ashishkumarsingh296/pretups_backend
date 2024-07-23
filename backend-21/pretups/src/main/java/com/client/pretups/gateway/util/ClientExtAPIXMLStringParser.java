
package com.client.pretups.gateway.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.ClientExtAPIXMLStringParserI;
import com.btsl.pretups.gateway.util.ExtAPIXMLStringParser;
import com.btsl.pretups.gateway.util.XMLStringValidation;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class ClientExtAPIXMLStringParser implements ClientExtAPIXMLStringParserI{

    public static final Log _log = LogFactory.getLog(ClientExtAPIXMLStringParser.class.getName());

    private static String P2P_MESSAGE_SEP = null;
    private static String CHNL_MESSAGE_SEP = null;
    private static String MULT_CRE_TRA_DED_ACC_SEP = null;
    private final static String _blank = "";
    public final static String MNP_RES = "UPLOADMNPFILERESP";
    public final static String ADD_USER_RES = "USERADDRESP";// 21-02-2014
    public final static String MODIFY_USER_RES = "USERMODRESP";// 21-02-2014
    public final static String SUSPEND_RESUME_USER_RES = "USERSRRESP"; // 21-02-2014
    public final static String DELETE_USER_RES = "USERDELRESP";// 21-02-2014
    public final static String ADD_MODIFY_USER_ROLE_RES = "EXTCNGROLERESP";
    public final static String CHANE_PASSWORD_RES = "EXTCNGPWDRESP";
    public final static String ICCID_MSISDN_MAP_RES = "ICCIDMSISDNMAPRESP";
    private static OperatorUtilI _operatorUtil = null;
    private static final String EXCEPTION = "EXCEPTION: ";

    public ClientExtAPIXMLStringParser(){
    	
    }
    
    static {

        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        }catch (InstantiationException e) {
        	_log.errorTrace("VoucherConsController", e);
        } catch (ClassNotFoundException e) {
        	_log.errorTrace("VoucherConsController", e);
        } 
        catch (Exception e) {
            _log.errorTrace("VoucherConsController", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }

        try {
            CHNL_MESSAGE_SEP = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                CHNL_MESSAGE_SEP = " ";
            }
            P2P_MESSAGE_SEP = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR));
            if (BTSLUtil.isNullString(P2P_MESSAGE_SEP)) {
                P2P_MESSAGE_SEP = " ";
            }

            MULT_CRE_TRA_DED_ACC_SEP = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULT_CRE_TRA_DED_ACC_SEP));
            if (BTSLUtil.isNullString(MULT_CRE_TRA_DED_ACC_SEP)) {
                MULT_CRE_TRA_DED_ACC_SEP = ",";
            }
        } catch (Exception e) {
            _log.errorTrace("static", e);
        }
    }

    
     /////////////////////////////VHA START //////////////////////////////////////////////////
        /**
         * @param p_requestVO
         * @throws Exception
         * to parse voucher validate request
         */
        public void parseVoucherValidateRequest(RequestVO p_requestVO) throws BTSLBaseException {
            final String methodName = "parseVoucherValidateRequest";
            final String className = "ExtAPIXMLStringParser";
            StringBuilder loggerValue= new StringBuilder();
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Entered: p_requestVO=");
            	loggerValue.append(p_requestVO.toString());
            	_log.debug(methodName, loggerValue);
            }
            String parsedRequestStr = null;
            String extNwCode = null;
            String loginId = null;
            String extRefNumber = null;
            String extCode = null;
            String voucherType = null;
            String voucherPin = null;
            String voucherSerialNo = null;
            
            try {
                final String requestStr = p_requestVO.getRequestMessage();
                int index = requestStr.indexOf("<TYPE>");
                final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
                index = requestStr.indexOf("<DATE>");
                String requestDate = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
                
                index = requestStr.indexOf("<VTYPE>");
                if (index != -1) {
                    voucherType = requestStr.substring(index + "<VTYPE>".length(), requestStr.indexOf("</VTYPE>", index));
                }

                index = requestStr.indexOf("<PIN>");
                if (index != -1) {
                    voucherPin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
                }
                /*if(voucherPin.isEmpty()){
                	voucherPin="0";
                }*/
                index = requestStr.indexOf("<SNO>");
                if (index != -1) {
                    voucherSerialNo = requestStr.substring(index + "<SNO>".length(), requestStr.indexOf("</SNO>", index));
                }
                /*if(voucherSerialNo.isEmpty()){
                	voucherSerialNo="0";
                }*/
            

                try {
                    
                    
                	String SystemWiseGatewayCode="";
	                SystemWiseGatewayCode = Constants.getProperty("SYSTEM_TYPE_CHANNEL_GATEWAY_CODES");
	                if(!SystemWiseGatewayCode.contains(p_requestVO.getRequestGatewayCode())){
	                	index = requestStr.indexOf("<LOGINID>");
	                    loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
	                    index = requestStr.indexOf("<PASSWORD>");
	                    final String password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
	                    index = requestStr.indexOf("<EXTCODE>");
	                    extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    loggerValue.setLength(0);
        			loggerValue.append(EXCEPTION);
        			loggerValue.append(e.getMessage());
        			_log.error(methodName, loggerValue);
                }     
                 
                	index = requestStr.indexOf("<EXTREFNUM>");
                    extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
                    index = requestStr.indexOf("<EXTNWCODE>");
                    extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
                    index = requestStr.indexOf("<LANGUAGE1>");
                    final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
                 
                    
               
                
                
               
               XMLStringValidation.validateVoucherValidateRequest(p_requestVO,type,requestDate,extNwCode,voucherPin,voucherSerialNo,extRefNumber,language1);
                
               	if (voucherPin.isEmpty()) {
               		voucherPin = "0";
	   			}
	   			if (voucherSerialNo.isEmpty()) {
	   				voucherSerialNo = "0";
	   			}          

                parsedRequestStr = VOMSI.VOUCHER_VALIDATION + CHNL_MESSAGE_SEP + voucherPin + CHNL_MESSAGE_SEP + voucherSerialNo;
                p_requestVO.setDecryptedMessage(parsedRequestStr);
                p_requestVO.setExternalNetworkCode(extNwCode);
                p_requestVO.setSenderExternalCode(extCode);
                p_requestVO.setSenderLoginID(loginId);
                p_requestVO.setExternalReferenceNum(extRefNumber);
                p_requestVO.setVoucherType(voucherType);
                p_requestVO.setVoucherCode(voucherPin);
                p_requestVO.setSerialnumber(voucherSerialNo);
                p_requestVO.setSerialNo(voucherSerialNo);
                p_requestVO.setRequestMessageArray(PretupsBL.parsePlainMessage(parsedRequestStr));
            } catch (BTSLBaseException be) {
            	
                loggerValue.setLength(0);
    			loggerValue.append(EXCEPTION);
    			loggerValue.append(be.getMessage());
    			_log.error(methodName, loggerValue);
    			if(p_requestVO.getMessageCode()==null){
            		p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            		throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            	}else{
            		throw be;
            	}
                
            } catch (Exception e) {
                loggerValue.setLength(0);
    			loggerValue.append(EXCEPTION);
    			loggerValue.append(e.getMessage());
    			_log.error(methodName, loggerValue);
    			if(p_requestVO.getMessageCode()==null){
            		p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            		throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            	}else{
            		throw e;
            	}
            } finally {
            	if (_log.isDebugEnabled()) {
                 	loggerValue.setLength(0);
                 	loggerValue.append("Exiting: p_requestVO:");
                 	loggerValue.append(p_requestVO.toString());
                 	_log.debug(methodName, loggerValue);
                 }
            }
        }
        
        /**
         * this is used to construct response message for voucher validation
         * 
         * @param p_requestVO
         *            RequestVO
         * @return responseStr java.lang.String
         */
        public void generateVoucherValidateResponse(RequestVO p_requestVO) throws Exception {
            final String methodName = "generateVoucherValidateResponse";
            final String classMethodName = "ExtAPIStringParser[generateVoucherValidateResponse]";
            StringBuilder loggerValue= new StringBuilder();
            VomsVoucherVO vo=null;
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Entered: p_requestVO=");
            	loggerValue.append(p_requestVO.toString());
            	_log.debug(methodName, loggerValue);
            }
            String responseStr = null;
            StringBuilder sbf = null;
            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
            sdf.setLenient(false); // this is required else it will convert
            
            try {
                sbf = new StringBuilder(1024);
                
                if(null!=p_requestVO.getValueObject())
                {
                	vo=(VomsVoucherVO)p_requestVO.getValueObject();
                }
                 
                sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
                sbf.append("<TYPE>VOMSVALRES</TYPE>");
                Date date=Calendar.getInstance().getTime();
                sbf.append("<DATE>" + BTSLDateUtil.getLocaleDateTimeFromDate(date) + "</DATE>");
                if (p_requestVO.isSuccessTxn()) {
                    sbf.append("<TXNSTATUS>" + PretupsI.TXN_STATUS_SUCCESS + "</TXNSTATUS>");
                } else {
                    String message = p_requestVO.getMessageCode();
                    if (message.indexOf("_") != -1) {
                        message = message.substring(0, message.indexOf("_"));
                    }
                    if(p_requestVO.getVomsMessage()!=null){
                    	sbf.append("<TXNSTATUS>" + p_requestVO.getVomsMessage() + "</TXNSTATUS>");
                    }else{
                    	sbf.append("<TXNSTATUS>" + message + "</TXNSTATUS>");
                    }
                }
                
                if (vo !=null) {                
	                sbf.append("<SNO>" + p_requestVO.getSerialNo() + "</SNO>");
	               	sbf.append("<PROFILE>" + vo.getProductName() + "</PROFILE>");
	                sbf.append("<ISVALID>" + p_requestVO.getVomsValid() + "</ISVALID>");
                }
                
                if(p_requestVO.getExternalReferenceNum()!=null)
	                sbf.append("<EXTREFNUM>" + p_requestVO.getExternalReferenceNum() + "</EXTREFNUM>");
                
                if (!p_requestVO.isSuccessTxn()) {
                	if(p_requestVO.getVomsError()!=null)
                		sbf.append("<MESSAGE>" + p_requestVO.getVomsError() + "</MESSAGE>");
                	else{
                		
                		String message=ExtAPIXMLStringParser.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                		message=message.replace("mclass^2&pid^61", "");
                        message=message.replace(p_requestVO.getMessageCode(), "");
                        message=message.replaceAll(":", "");
                        
                		sbf.append("<MESSAGE>" + message + "</MESSAGE>");
                	}
                } else {
                	
                	String message=ExtAPIXMLStringParser.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
            		message=message.replace("mclass^2&pid^61", "");
                    message=message.replace(p_requestVO.getMessageCode(), "");
                    message=message.replaceAll(":", "");
                                   	
                	sbf.append("<MESSAGE>" + message + "</MESSAGE>");
                }
                
                
                //sbf.append("<ERROR>" + p_requestVO.getVomsError() + "</ERROR>");
                sbf.append("</COMMAND>");
                responseStr = sbf.toString();
                p_requestVO.setSenderReturnMessage(responseStr);
            } catch (Exception e) {
            	loggerValue.setLength(0);
    			loggerValue.append(EXCEPTION);
    			loggerValue.append(e.getMessage());
    			_log.error(methodName, loggerValue);
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateVoucherValidateResponse]",
                    PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateVoucherValidateResponse:" + e.getMessage());
            } finally {
            	if (_log.isDebugEnabled()) {
                 	loggerValue.setLength(0);
                 	loggerValue.append("Exiting: responseStr:");
                 	loggerValue.append(responseStr.toString());
                 	_log.debug(methodName, loggerValue);
                 }
            }
        }
        ///////////////////////////////////////////////////////////////////////////////////////
        /**
         * @param p_requestVO
         * @throws Exception
         * to parse voucher reserve request
         */
        public void parseVoucherReserveRequest(RequestVO p_requestVO) throws BTSLBaseException {
            final String methodName = "parseVoucherReserveRequest";
            final String className = "ExtAPIXMLStringParser";
            StringBuilder loggerValue= new StringBuilder();
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Entered: p_requestVO=");
            	loggerValue.append(p_requestVO.toString());
            	_log.debug(methodName, loggerValue);
            }
            String parsedRequestStr = null;
            String extNwCode = null;
            String loginId = null;
            String extRefNumber = null;
            String extCode = null;
            String voucherType = null;
            String voucherPin = null;
            String voucherSerialNo = null;
            
            try {
                final String requestStr = p_requestVO.getRequestMessage();
                int index = requestStr.indexOf("<TYPE>");
                final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
                index = requestStr.indexOf("<DATE>");
                String requestDate = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));
                
                index = requestStr.indexOf("<VTYPE>");
                if (index != -1) {
                    voucherType = requestStr.substring(index + "<VTYPE>".length(), requestStr.indexOf("</VTYPE>", index));
                }

                index = requestStr.indexOf("<PIN>");
                if (index != -1) {
                    voucherPin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
                }
             
                index = requestStr.indexOf("<SNO>");
                if (index != -1) {
                    voucherSerialNo = requestStr.substring(index + "<SNO>".length(), requestStr.indexOf("</SNO>", index));
                }
                index = requestStr.indexOf("<SUBID>");
                final String subsId = requestStr.substring(index + "<SUBID>".length(), requestStr.indexOf("</SUBID>", index));
                
                /*if(subsId.isEmpty()){
                	throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.VOUCHER_SUBSCRIBER_MSISDN_ISREQUIRED);
                }*/

                try {
                                        
                	String SystemWiseGatewayCode="";
	                SystemWiseGatewayCode = Constants.getProperty("SYSTEM_TYPE_CHANNEL_GATEWAY_CODES");
	                if(!SystemWiseGatewayCode.contains(p_requestVO.getRequestGatewayCode())){
	                	index = requestStr.indexOf("<LOGINID>");
	                    loginId = requestStr.substring(index + "<LOGINID>".length(), requestStr.indexOf("</LOGINID>", index));
	                    index = requestStr.indexOf("<PASSWORD>");
	                    final String password = requestStr.substring(index + "<PASSWORD>".length(), requestStr.indexOf("</PASSWORD>", index));
	                    index = requestStr.indexOf("<EXTCODE>");
	                    extCode = requestStr.substring(index + "<EXTCODE>".length(), requestStr.indexOf("</EXTCODE>", index));
                    }
                    
                    
                    
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                    loggerValue.setLength(0);
        			loggerValue.append(EXCEPTION);
        			loggerValue.append(e.getMessage());
        			_log.error(methodName, loggerValue);
                }
                
                index = requestStr.indexOf("<EXTREFNUM>");
                extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(), requestStr.indexOf("</EXTREFNUM>", index));
                index = requestStr.indexOf("<EXTNWCODE>");
                extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(), requestStr.indexOf("</EXTNWCODE>", index));
                index = requestStr.indexOf("<LANGUAGE1>");
                final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(), requestStr.indexOf("</LANGUAGE1>", index));
             
                
                XMLStringValidation.validateVoucherReserveRequest(p_requestVO,type,requestDate,extNwCode,voucherPin,voucherSerialNo,extRefNumber,language1,subsId);
                
            
                if (voucherPin.isEmpty()) {
    				voucherPin = "0";
    			}
    			if (voucherSerialNo.isEmpty()) {
    				voucherSerialNo = "0";
    			}
                parsedRequestStr = VOMSI.VOUCHER_RESERVATION + CHNL_MESSAGE_SEP + voucherPin + CHNL_MESSAGE_SEP + voucherSerialNo+ CHNL_MESSAGE_SEP + extNwCode+ CHNL_MESSAGE_SEP + subsId;
                p_requestVO.setDecryptedMessage(parsedRequestStr);
                p_requestVO.setExternalNetworkCode(extNwCode);
                p_requestVO.setSenderExternalCode(extCode);
                p_requestVO.setSenderLoginID(loginId);
                p_requestVO.setExternalReferenceNum(extRefNumber);
                p_requestVO.setVoucherType(voucherType);
                p_requestVO.setRequestMessageArray(PretupsBL.parsePlainMessage(parsedRequestStr));
                p_requestVO.setVoucherCode(voucherPin);
                p_requestVO.setSerialnumber(voucherSerialNo);
                p_requestVO.setReceiverMsisdn(subsId);
                
                
            } catch (BTSLBaseException be) {
            	
                loggerValue.setLength(0);
    			loggerValue.append(EXCEPTION);
    			loggerValue.append(be.getMessage());
    			_log.error(methodName, loggerValue);
    			if(p_requestVO.getMessageCode()==null){
            		p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            		throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            	}else{
            		throw be;
            	}
                
            } catch (Exception e) {
                loggerValue.setLength(0);
    			loggerValue.append(EXCEPTION);
    			loggerValue.append(e.getMessage());
    			_log.error(methodName, loggerValue);
    			if(p_requestVO.getMessageCode()==null){
            		p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            		throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            	}else{
            		throw e;
            	}
            } finally {
            	if (_log.isDebugEnabled()) {
                 	loggerValue.setLength(0);
                 	loggerValue.append("Exiting: p_requestVO:");
                 	loggerValue.append(p_requestVO.toString());
                 	_log.debug(methodName, loggerValue+"parsedRequestStr"+parsedRequestStr);
                 }
            }
        }
        
        // added for voucher query and rollback request
        /**
         * this is used to construct response mesage for voucher reserve
         * 
         * @param p_requestVO
         *            RequestVO
         * @return responseStr java.lang.String
         */
        public void generateVoucherReserveResponse(RequestVO p_requestVO) throws Exception {
            final String methodName = "generateVoucherReserveResponse";
            final String classMethodName = "ExtAPIStringParser[generateVoucherReserveResponse]";
            StringBuilder loggerValue= new StringBuilder();
            VomsVoucherVO vo=null;
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Entered: p_requestVO=");
            	loggerValue.append(p_requestVO.toString());
            	_log.debug(methodName, loggerValue);
            }
            String responseStr = null;
            StringBuilder sbf = null;
            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
            sdf.setLenient(false); // this is required else it will convert
            
            try {
                sbf = new StringBuilder(1024);
                
                if(null!=p_requestVO.getValueObject())
                {
                	vo=(VomsVoucherVO)p_requestVO.getValueObject();
                }
                
                sbf.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
                sbf.append("<TYPE>VOMSRSVRESP</TYPE>");
                Date date=Calendar.getInstance().getTime();
                sbf.append("<DATE>" + BTSLDateUtil.getLocaleDateTimeFromDate(date) + "</DATE>");
                if (p_requestVO.isSuccessTxn()) {
                    sbf.append("<TXNSTATUS>" + PretupsI.TXN_STATUS_SUCCESS + "</TXNSTATUS>");
                } else {
                    String message = p_requestVO.getMessageCode();
                    if (message.indexOf("_") != -1) {
                        message = message.substring(0, message.indexOf("_"));
                    }
                    if(p_requestVO.getVomsMessage()!=null){
                    	sbf.append("<TXNSTATUS>" + p_requestVO.getVomsMessage() + "</TXNSTATUS>");
                    }else{
                    	sbf.append("<TXNSTATUS>" + message + "</TXNSTATUS>");
                    }
                }
                

                
                if (!p_requestVO.isSuccessTxn()) {
                	if(p_requestVO.getVomsError()!=null)
                		sbf.append("<MESSAGE>" + p_requestVO.getVomsError() + "</MESSAGE>");
                	else{
                		
                		String message=ExtAPIXMLStringParser.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                		message=message.replace("mclass^2&pid^61", "");
                        message=message.replace(p_requestVO.getMessageCode(), "");
                        message=message.replaceAll(":", "");
                        
                		sbf.append("<MESSAGE>" + message + "</MESSAGE>");
                	}
                } else {
                	
                	String message=ExtAPIXMLStringParser.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
            		message=message.replace("mclass^2&pid^61", "");
                    message=message.replace(p_requestVO.getMessageCode(), "");
                    message=message.replaceAll(":", "");
                                   	
                	sbf.append("<MESSAGE>" + message + "</MESSAGE>");
                }
                if (vo !=null) {
	                sbf.append("<SNO>" + p_requestVO.getSerialNo() + "</SNO>");
	                sbf.append("<PROFILE>" + vo.getProductName() + "</PROFILE>");
	                sbf.append("<SUBID>" + p_requestVO.getReceiverMsisdn() + "</SUBID>");
                }
                
                if(p_requestVO.getExternalReferenceNum()!=null)
	                sbf.append("<EXTREFNUM>" + p_requestVO.getExternalReferenceNum() + "</EXTREFNUM>");
                //sbf.append("<ERROR>" + p_requestVO.getVomsError() + "</ERROR>");
                sbf.append("</COMMAND>");
                responseStr = sbf.toString();
                p_requestVO.setSenderReturnMessage(responseStr);
            } catch (Exception e) {
            	loggerValue.setLength(0);
    			loggerValue.append(EXCEPTION);
    			loggerValue.append(e.getMessage());
    			_log.error(methodName, loggerValue);
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLStringParser[generateVoucherReserveResponse]",
                    PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateVoucherReserveResponse:" + e.getMessage());
            } finally {
            	if (_log.isDebugEnabled()) {
                 	loggerValue.setLength(0);
                 	loggerValue.append("Exiting: responseStr:");
                 	loggerValue.append(responseStr.toString());
                 	_log.debug(methodName, loggerValue);
                 }
            }
        }
        
	///////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @param p_requestVO
	 * @throws Exception
	 *             to parse voucher reserve request
	 */
	public void parseVoucherDirectConsumptionRequest(RequestVO p_requestVO) throws BTSLBaseException {
		final String methodName = "parseVoucherDirectConsumptionRequest";
		final String className = "ExtAPIXMLStringParser";
		StringBuilder loggerValue = new StringBuilder();
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: p_requestVO=");
			loggerValue.append(p_requestVO.toString());
			_log.debug(methodName, loggerValue);
		}
		String parsedRequestStr = null;
		String extNwCode = null;
		String loginId = null;
		String extRefNumber = null;
		String extCode = null;
		String voucherType = null;
		String voucherPin = null;
		String voucherSerialNo = null;

		try {
			final String requestStr = p_requestVO.getRequestMessage();
			int index = requestStr.indexOf("<TYPE>");
			final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
			index = requestStr.indexOf("<DATE>");
			String requestDate = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));

			index = requestStr.indexOf("<VTYPE>");
			if (index != -1) {
				voucherType = requestStr.substring(index + "<VTYPE>".length(), requestStr.indexOf("</VTYPE>", index));
			}

			index = requestStr.indexOf("<PIN>");
			if (index != -1) {
				voucherPin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
			}
			
			index = requestStr.indexOf("<SNO>");
			if (index != -1) {
				voucherSerialNo = requestStr.substring(index + "<SNO>".length(), requestStr.indexOf("</SNO>", index));
			}
			
			index = requestStr.indexOf("<SUBID>");
			final String subsId = requestStr.substring(index + "<SUBID>".length(),
					requestStr.indexOf("</SUBID>", index));

			try {

				String SystemWiseGatewayCode="";
                SystemWiseGatewayCode = Constants.getProperty("SYSTEM_TYPE_CHANNEL_GATEWAY_CODES");
                if(!SystemWiseGatewayCode.contains(p_requestVO.getRequestGatewayCode())){
                	index = requestStr.indexOf("<LOGINID>");
					loginId = requestStr.substring(index + "<LOGINID>".length(),
							requestStr.indexOf("</LOGINID>", index));
					index = requestStr.indexOf("<PASSWORD>");
					final String password = requestStr.substring(index + "<PASSWORD>".length(),
							requestStr.indexOf("</PASSWORD>", index));
					index = requestStr.indexOf("<EXTCODE>");
					extCode = requestStr.substring(index + "<EXTCODE>".length(),
							requestStr.indexOf("</EXTCODE>", index));
				}

		
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
				loggerValue.setLength(0);
				loggerValue.append(EXCEPTION);
				loggerValue.append(e.getMessage());
				_log.error(methodName, loggerValue);
			}
			
			index = requestStr.indexOf("<EXTREFNUM>");
			extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(),
					requestStr.indexOf("</EXTREFNUM>", index));
			index = requestStr.indexOf("<EXTNWCODE>");
			extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(),
					requestStr.indexOf("</EXTNWCODE>", index));
			index = requestStr.indexOf("<LANGUAGE1>");
			final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(),
					requestStr.indexOf("</LANGUAGE1>", index));


			XMLStringValidation.validateDirectConsumptionRequest(p_requestVO,type,requestDate,extNwCode,voucherPin,voucherSerialNo,extRefNumber,language1,subsId);
            
            
            if (voucherPin.isEmpty()) {
				voucherPin = "0";
			}
			if (voucherSerialNo.isEmpty()) {
				voucherSerialNo = "0";
			}
        
			
			parsedRequestStr = VOMSI.VOUCHER_DIRECT_CONSUMPTION + CHNL_MESSAGE_SEP + voucherPin + CHNL_MESSAGE_SEP
					+ voucherSerialNo + CHNL_MESSAGE_SEP + extNwCode + CHNL_MESSAGE_SEP + subsId+ CHNL_MESSAGE_SEP + extRefNumber;
			p_requestVO.setDecryptedMessage(parsedRequestStr);
			p_requestVO.setExternalNetworkCode(extNwCode);
			p_requestVO.setSenderExternalCode(extCode);
			p_requestVO.setSenderLoginID(loginId);
			p_requestVO.setExternalReferenceNum(extRefNumber);
			p_requestVO.setVoucherType(voucherType);
		
			 p_requestVO.setVoucherCode(voucherPin);
             p_requestVO.setSerialnumber(voucherSerialNo);
             p_requestVO.setReceiverMsisdn(subsId);
            
			
			
			p_requestVO.setRequestMessageArray(PretupsBL.parsePlainMessage(parsedRequestStr));
	
		
		
		}  catch (BTSLBaseException be) {
        	
            loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(be.getMessage());
			_log.error(methodName, loggerValue);
			if(p_requestVO.getMessageCode()==null){
        		p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        		throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        	}else{
        		throw be;
        	}
            
        } catch (Exception e) {
            loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
			if(p_requestVO.getMessageCode()==null){
        		p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        		throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        	}else{
        		throw e;
        	} 
        }finally {
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting: p_requestVO:");
				loggerValue.append(p_requestVO.toString());
				_log.debug(methodName, loggerValue);
			}
		}
	}

	// added for voucher query and rollback request
	/**
	 * this is used to construct response mesage for voucher reserve
	 * 
	 * @param p_requestVO
	 *            RequestVO
	 * @return responseStr java.lang.String
	 */
	public void generateVoucherDirectConsumptionResponse(RequestVO p_requestVO) throws Exception {
		final String methodName = "generateVoucherDirectConsumptionResponse";
		final String classMethodName = "ExtAPIXMLStringParser[generateVoucherDirectConsumptionResponse]";
		StringBuilder loggerValue = new StringBuilder();
		VomsVoucherVO vo = null;
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: p_requestVO=");
			loggerValue.append(p_requestVO.toString());
			_log.debug(methodName, loggerValue);
		}
		String responseStr = null;
		StringBuilder sbf = null;
		final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
		sdf.setLenient(false); // this is required else it will convert

		try {
			sbf = new StringBuilder(1024);

			if (null != p_requestVO.getValueObject()) {
				vo = (VomsVoucherVO) p_requestVO.getValueObject();
			}
    
			sbf.append(
					"<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
			sbf.append("<TYPE>VOMSDCONSRESP</TYPE>");
			Date date = Calendar.getInstance().getTime();
			 sbf.append("<DATE>" + BTSLDateUtil.getLocaleDateTimeFromDate(date) + "</DATE>");
             if (p_requestVO.isSuccessTxn()) {
                 sbf.append("<TXNSTATUS>" + PretupsI.TXN_STATUS_SUCCESS + "</TXNSTATUS>");
             } else {
                 String message = p_requestVO.getMessageCode();
                 if (message.indexOf("_") != -1) {
                     message = message.substring(0, message.indexOf("_"));
                 }
                 if(p_requestVO.getVomsMessage()!=null){
                 	sbf.append("<TXNSTATUS>" + p_requestVO.getVomsMessage() + "</TXNSTATUS>");
                 }else{
                 	sbf.append("<TXNSTATUS>" + message + "</TXNSTATUS>");
                 }
             }
             

             
             if (!p_requestVO.isSuccessTxn()) {
             	if(p_requestVO.getVomsError()!=null)
             		sbf.append("<MESSAGE>" + p_requestVO.getVomsError() + "</MESSAGE>");
             	else{
             		
             		String message=ExtAPIXMLStringParser.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
             		message=message.replace("mclass^2&pid^61", "");
                     message=message.replace(p_requestVO.getMessageCode(), "");
                     message=message.replaceAll(":", "");
                     
             		sbf.append("<MESSAGE>" + message + "</MESSAGE>");
             	}
             } else {
             	
             	String message=ExtAPIXMLStringParser.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
         		message=message.replace("mclass^2&pid^61", "");
                 message=message.replace(p_requestVO.getMessageCode(), "");
                 message=message.replaceAll(":", "");
                                	
             	sbf.append("<MESSAGE>" + message + "</MESSAGE>");
             }
             if (vo !=null) {
				sbf.append("<SNO>" + p_requestVO.getSerialNo() + "</SNO>");
				sbf.append("<PROFILE>" + vo.getProductName() + "</PROFILE>");
				sbf.append("<EXTREFNUM>" + p_requestVO.getExternalReferenceNum() + "</EXTREFNUM>");
				sbf.append("<TXNID>" + p_requestVO.getTransactionID() + "</TXNID>");
				sbf.append("<SUBID>" + p_requestVO.getReceiverMsisdn() + "</SUBID>");
             }
			// sbf.append("<ERROR>" + p_requestVO.getVomsError() + "</ERROR>");
			sbf.append("</COMMAND>");
			responseStr = sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"XMLStringParser[generateVoucherDirectConsumptionResponse]", PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "",
					"generateVoucherDirectConsumptionResponse:" + e.getMessage());
		} finally {
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting: responseStr:");
				loggerValue.append(responseStr.toString());
				_log.debug(methodName, loggerValue);
			}
		}
	}

	/**
	 * @param p_requestVO
	 * @throws Exception
	 *             to parse voucher reserve request
	 */
	public void parseVoucherDirectRollbackRequest(RequestVO p_requestVO) throws BTSLBaseException {
		final String methodName = "parseVoucherDirectRollbackRequest";
		final String className = "ExtAPIXMLStringParser";
		StringBuilder loggerValue = new StringBuilder();
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: p_requestVO=");
			loggerValue.append(p_requestVO.toString());
			_log.debug(methodName, loggerValue);
		}
		String parsedRequestStr = null;
		String extNwCode = null;
		String loginId = null;
		String extRefNumber = null;
		String extCode = null;
		String voucherType = null;
		String voucherPin = null;
		String voucherSerialNo = null;
		String stateChangeReason = null;

		try {
			final String requestStr = p_requestVO.getRequestMessage();
			int index = requestStr.indexOf("<TYPE>");
			final String type = requestStr.substring(index + "<TYPE>".length(), requestStr.indexOf("</TYPE>", index));
			index = requestStr.indexOf("<DATE>");
			String requestDate = requestStr.substring(index + "<DATE>".length(), requestStr.indexOf("</DATE>", index));

			index = requestStr.indexOf("<VTYPE>");
			if (index != -1) {
				voucherType = requestStr.substring(index + "<VTYPE>".length(), requestStr.indexOf("</VTYPE>", index));
			}

			index = requestStr.indexOf("<PIN>");
			if (index != -1) {
				voucherPin = requestStr.substring(index + "<PIN>".length(), requestStr.indexOf("</PIN>", index));
			}
			
			index = requestStr.indexOf("<SNO>");
			if (index != -1) {
				voucherSerialNo = requestStr.substring(index + "<SNO>".length(), requestStr.indexOf("</SNO>", index));
			}
			
			index = requestStr.indexOf("<SUBID>");
			final String subsId = requestStr.substring(index + "<SUBID>".length(),
					requestStr.indexOf("</SUBID>", index));

			try {

				String SystemWiseGatewayCode="";
                SystemWiseGatewayCode = Constants.getProperty("SYSTEM_TYPE_CHANNEL_GATEWAY_CODES");
                if(!SystemWiseGatewayCode.contains(p_requestVO.getRequestGatewayCode())){
                	index = requestStr.indexOf("<LOGINID>");
					loginId = requestStr.substring(index + "<LOGINID>".length(),
							requestStr.indexOf("</LOGINID>", index));
					index = requestStr.indexOf("<PASSWORD>");
					final String password = requestStr.substring(index + "<PASSWORD>".length(),
							requestStr.indexOf("</PASSWORD>", index));
					index = requestStr.indexOf("<EXTCODE>");
					extCode = requestStr.substring(index + "<EXTCODE>".length(),
							requestStr.indexOf("</EXTCODE>", index));
				}

			
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
				loggerValue.setLength(0);
				loggerValue.append(EXCEPTION);
				loggerValue.append(e.getMessage());
				_log.error(methodName, loggerValue);
			}
			
			index = requestStr.indexOf("<EXTREFNUM>");
			extRefNumber = requestStr.substring(index + "<EXTREFNUM>".length(),
					requestStr.indexOf("</EXTREFNUM>", index));
			index = requestStr.indexOf("<EXTNWCODE>");
			extNwCode = requestStr.substring(index + "<EXTNWCODE>".length(),
					requestStr.indexOf("</EXTNWCODE>", index));
			index = requestStr.indexOf("<LANGUAGE1>");
			final String language1 = requestStr.substring(index + "<LANGUAGE1>".length(),
					requestStr.indexOf("</LANGUAGE1>", index));

			stateChangeReason = requestStr.substring(index + "<STATE_CHANGE_REASON>".length(),
					requestStr.indexOf("</STATE_CHANGE_REASON>", index));
			
			XMLStringValidation.validateDirectRollbackRequest(p_requestVO,type,requestDate,extNwCode,voucherPin,voucherSerialNo,extRefNumber,language1,subsId,stateChangeReason);
            
            
            if (voucherPin.isEmpty()) {
				voucherPin = "0";
			}
			if (voucherSerialNo.isEmpty()) {
				voucherSerialNo = "0";
			}
			
			parsedRequestStr = VOMSI.VOUCHER_DIRECT_ROLLBACK + CHNL_MESSAGE_SEP + voucherPin + CHNL_MESSAGE_SEP
					+ voucherSerialNo + CHNL_MESSAGE_SEP + extNwCode + CHNL_MESSAGE_SEP + subsId+ CHNL_MESSAGE_SEP + extRefNumber;
			p_requestVO.setDecryptedMessage(parsedRequestStr);
			p_requestVO.setExternalNetworkCode(extNwCode);
			p_requestVO.setSenderExternalCode(extCode);
			p_requestVO.setSenderLoginID(loginId);
			p_requestVO.setExternalReferenceNum(extRefNumber);
			p_requestVO.setVoucherType(voucherType);
			p_requestVO.setPin(voucherPin);
			p_requestVO.setVoucherCode(voucherPin);
            p_requestVO.setSerialnumber(voucherSerialNo);
            p_requestVO.setReceiverMsisdn(subsId);
			p_requestVO.setRequestMessageArray(PretupsBL.parsePlainMessage(parsedRequestStr));
		}catch (BTSLBaseException be) {
        	
            loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(be.getMessage());
			_log.error(methodName, loggerValue);
			if(p_requestVO.getMessageCode()==null){
        		p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        		throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        	}else{
        		throw be;
        	}
            
        } catch (Exception e) {
            loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
			if(p_requestVO.getMessageCode()==null){
        		p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        		throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        	}else{
        		throw e;
        	} 
        } finally {
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting: p_requestVO:");
				loggerValue.append(p_requestVO.toString());
				_log.debug(methodName, loggerValue);
			}
		}
	}

	// added for voucher query and rollback request
	/**
	 * this is used to construct response mesage for voucher reserve
	 * 
	 * @param p_requestVO
	 *            RequestVO
	 * @return responseStr java.lang.String
	 */
	public void generateVoucherDirectRollbackResponse(RequestVO p_requestVO) throws Exception {
		final String methodName = "VOUCHER_DIRECT_ROLLBACK";
		final String classMethodName = "ExtAPIXMLStringParser[generateVoucherDirectRollbackResponse]";
		StringBuilder loggerValue = new StringBuilder();
		VomsVoucherVO vo = null;
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: p_requestVO=");
			loggerValue.append(p_requestVO.toString());
			_log.debug(methodName, loggerValue);
		}
		String responseStr = null;
		StringBuilder sbf = null;
		final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
		sdf.setLenient(false); // this is required else it will convert

		try {
			sbf = new StringBuilder(1024);

			if (null != p_requestVO.getValueObject()) {
				vo = (VomsVoucherVO) p_requestVO.getValueObject();
			}

			sbf.append(
					"<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
			sbf.append("<TYPE>VOMSROLLBACKRES</TYPE>");
			Date date = Calendar.getInstance().getTime();
			 sbf.append("<DATE>" + BTSLDateUtil.getLocaleDateTimeFromDate(date) + "</DATE>");
             if (p_requestVO.isSuccessTxn()) {
                 sbf.append("<TXNSTATUS>" + PretupsI.TXN_STATUS_SUCCESS + "</TXNSTATUS>");
             } else {
                 String message = p_requestVO.getMessageCode();
                 if (message.indexOf("_") != -1) {
                     message = message.substring(0, message.indexOf("_"));
                 }
                 if(p_requestVO.getVomsMessage()!=null){
                 	sbf.append("<TXNSTATUS>" + p_requestVO.getVomsMessage() + "</TXNSTATUS>");
                 }else{
                 	sbf.append("<TXNSTATUS>" + message + "</TXNSTATUS>");
                 }
             }
             

             
             if (!p_requestVO.isSuccessTxn()) {
             	if(p_requestVO.getVomsError()!=null)
             		sbf.append("<MESSAGE>" + p_requestVO.getVomsError() + "</MESSAGE>");
             	else{
             		
             		String message=ExtAPIXMLStringParser.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
             		message=message.replace("mclass^2&pid^61", "");
                     message=message.replace(p_requestVO.getMessageCode(), "");
                     message=message.replaceAll(":", "");
                     
             		sbf.append("<MESSAGE>" + message + "</MESSAGE>");
             	}
             } else {
             	
             	String message=ExtAPIXMLStringParser.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
         		message=message.replace("mclass^2&pid^61", "");
                 message=message.replace(p_requestVO.getMessageCode(), "");
                 message=message.replaceAll(":", "");
                                	
             	sbf.append("<MESSAGE>" + message + "</MESSAGE>");
             }
             if (p_requestVO.isSuccessTxn()) {
				sbf.append("<SNO>" + p_requestVO.getSerialNo() + "</SNO>");
				sbf.append("<PIN>" + p_requestVO.getPin() + "</PIN>");
				//sbf.append("<PROFILE>" + vo.getProductName() + "</PROFILE>");
				sbf.append("<EXTREFNUM>" + p_requestVO.getExternalReferenceNum() + "</EXTREFNUM>");
				//sbf.append("<TXNID>" + p_requestVO.getTransactionID() + "</TXNID>");
				sbf.append("<SUBID>" + p_requestVO.getReceiverMsisdn() + "</SUBID>");
				sbf.append("<PRE_STATE>" + VOMSI.VOUCHER_UNPROCESS + "</PRE_STATE>");
				sbf.append("<CUR_STATE>" + VOMSI.VOUCHER_ENABLE + "</CUR_STATE>");
             }
				// sbf.append("<ERROR>" + p_requestVO.getVomsError() + "</ERROR>");
			sbf.append("</COMMAND>");
			responseStr = sbf.toString();
			p_requestVO.setSenderReturnMessage(responseStr);
		} catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
			p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"XMLStringParser[generateVoucherDirectRollbackResponse]", PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "",
					"generateVoucherDirectRollbackResponse:" + e.getMessage());
		} finally {
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting: responseStr:");
				loggerValue.append(responseStr.toString());
				_log.debug(methodName, loggerValue);
			}
		}
	}
        /////////////////////////////VHA END //////////////////////////////////////////////////
}
