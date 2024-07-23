package com.btsl.pretups.gateway.util;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.RestTagValueValidation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class RestAPIStringParser {
    public static final String TYPE = "type";
    public static final String DATE = "date";
    public static final String EXTNWCODE = "extnwcode";
    public static final String MSISDN = "msisdn";
    public static final String PIN = "pin";
    public static final String LOGINID = "loginid";
    public static final String PSWD = "password";
    public static final String EXTCODE = "extcode";
    public static final String EXTREFNUM = "extrefnum";
    public static final String LANGUAGE1 = "language1";
    public static final String TXNSTATUS = "TXNSTATUS";
    public static final String RECORD = "RECORD";
    public static final String PRODUCTCODE = "PRODUCTCODE";
    public static final String PRODUCTSHORTNAME = "PRODUCTSHORTNAME";
    public static final String BALANCE = "BALANCE";
    public static final String MESSAGE = "MESSAGE";
    public static final String FIXEDPARAMETERS = "fixedP";
    public static final String OPTIONALPARAMETERS = "optionalP";
    public static final String MANADATORYPARAMETERS = "mandatP";
    public static final String MESSAGEFORMAT = "messageF";
    public static final String RESPONSEFIXEDPARAMETERS = "responseFixedP";
    public static final String RESPONSEMANDPARAMETERS = "responseMandP";
    public static final String LASTTRFTYPE = "LASTTRFTYPE";
    public static final String FIXEDSYSTEMPARAMETERS = "fixedSP";
    public static final String AVLBLVOUCHERS = "Available";
    public static final String MSISDN1 = "msisdn1";
    public static final String TRANSFER = "transfers";
    public static final String USERTYPE = "userType";
    public static final Log log = LogFactory.getLog(RestAPIStringParser.class.getName());
    private static String chnlMessageSep;
    static {
        try {
            chnlMessageSep = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            if (BTSLUtil.isNullString(chnlMessageSep)) {
                chnlMessageSep = " ";
            }
        } catch (Exception e) {
            log.errorTrace("static", e);
        }
    }

    /**
 * 
 */
    private RestAPIStringParser() {

    }

    /**
     * @param requestVO
     * @throws BTSLBaseException
     */
    public static void parseJsonRequest(RequestVO requestVO) throws BTSLBaseException {
        final String methodName = "parseJsonRequest";
        LogFactory.printLog(methodName, "Entered requestVO:" + requestVO, log);
        List<String> arr = new ArrayList<>();
        try {
            final Map<String, Object> requestHashMap = new HashMap();
            JsonNode data = (JsonNode) PretupsRestUtil.convertJSONToObject(requestVO.getRequestMessage(),
                    new TypeReference<JsonNode>() {
                    });
            String jsonObj = PretupsRestUtil.getMessageString(requestVO.getServiceKeyword().toLowerCase());

            JsonNode fields = (JsonNode) PretupsRestUtil.convertJSONToObject(jsonObj, new TypeReference<JsonNode>() {
            });
            JsonNode fixedJson = null;
            if(requestVO.getActionValue()==PretupsI.SYSTEM_RECEIVER_ACTION){
                fixedJson = fields.get(FIXEDSYSTEMPARAMETERS);
                parseFixedSystemParams(requestHashMap, data, fixedJson, requestVO);
            }else if(requestVO.getActionValue()==PretupsI.P2P_RECEIVER_ACTION){
                fixedJson = fields.get(FIXEDPARAMETERS);
                parseFixedP2PParams(requestHashMap, data, fixedJson, requestVO);
            } else{
                fixedJson = fields.get(FIXEDPARAMETERS);
                parseFixedParams(requestHashMap, data, fixedJson, requestVO);
            }
            JsonNode mandFields =null;
            if(requestVO.getActionValue()==PretupsI.P2P_RECEIVER_ACTION){
            	 mandFields = fields.get(MANADATORYPARAMETERS);
                parseP2PMandParams(requestHashMap, data, mandFields, requestVO, fields, arr);
             } else{
            	  mandFields = fields.get(MANADATORYPARAMETERS);
                 parseMandParams(requestHashMap, data, mandFields, requestVO, fields, arr);
            }
            
            JsonNode optionFields = fields.get(OPTIONALPARAMETERS);
            parseOptionalParams(requestHashMap, data, optionFields, requestVO, fields, arr);
            
            String[] messageF = fields.get(MESSAGEFORMAT).textValue().split(",");
            parseMessageFormat(requestHashMap, data, mandFields,fixedJson, requestVO, messageF, arr);
            requestVO.setRequestMap((HashMap) requestHashMap);
        }
        // 03-MAR-2014
        catch (BTSLBaseException be) {
        	requestVO.setSuccessTxn(false);
            requestVO.setMessageCode(be.getMessageKey());
            requestVO.setMessageArguments(be.getArgs());
            throw be;
        }
        // Ended Here
        catch (Exception e) {
            log.errorTrace(methodName, e);
            requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException("ExtAPIXMLStringParser", methodName,
                    PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            LogFactory.printLog(methodName, "Exiting requestVO:" + requestVO, log);

        }

    }

    public static void parseFixedParams(Map<String, Object> requestHashMap, JsonNode data, JsonNode fixedJson,
            RequestVO requestVO) throws BTSLBaseException {

        requestHashMap.put(TYPE.toUpperCase(), requestVO.getServiceKeyword().toLowerCase());
        requestHashMap.put(DATE.toUpperCase(), getNodeValue(data,getNodeValue(fixedJson, DATE)));
        requestHashMap.put(EXTNWCODE.toUpperCase(), getNodeValue(data, getNodeValue(fixedJson, EXTNWCODE)));
        requestHashMap.put( EXTREFNUM.toUpperCase(), getNodeValue(data, getNodeValue(fixedJson, EXTREFNUM)));
        requestHashMap.put( MSISDN.toUpperCase(), getNodeValue(data, getNodeValue(fixedJson, MSISDN)));
        requestHashMap.put( PIN.toUpperCase(), getNodeValue(data, getNodeValue(fixedJson, PIN)));
        requestHashMap.put( LOGINID.toUpperCase(), getNodeValue(data, getNodeValue(fixedJson, LOGINID)));
        requestHashMap.put( PSWD.toUpperCase(), getNodeValue(data, getNodeValue(fixedJson, PSWD)));
        requestHashMap.put(EXTCODE.toUpperCase(), getNodeValue(data, getNodeValue(fixedJson, EXTCODE)));
        requestHashMap.put( EXTREFNUM.toUpperCase(), getNodeValue(data, getNodeValue(fixedJson, EXTREFNUM)));

       
		if (requestVO.getRequestMessage() != null) {
			try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode reqMessageJson = mapper.readTree(requestVO.getRequestMessage());

			if (requestHashMap.get(MSISDN.toUpperCase()) == null || requestHashMap.get(MSISDN.toUpperCase()).toString().trim().length() == 0) {
				requestHashMap.put(MSISDN.toUpperCase(), getNodeValueNullCheck(reqMessageJson, MSISDN));
				requestHashMap.put(PIN.toUpperCase(), getNodeValueNullCheck(reqMessageJson, PIN));

			}

			if (requestHashMap.get(LOGINID.toUpperCase()) == null || requestHashMap.get(LOGINID.toUpperCase()).toString().trim().length() == 0) {
				requestHashMap.put(LOGINID.toUpperCase(), getNodeValueNullCheck(reqMessageJson, LOGINID));
				requestHashMap.put(PSWD.toUpperCase(), getNodeValueNullCheck(reqMessageJson,  PSWD));

			}

			if (requestHashMap.get(EXTCODE.toUpperCase()) == null || requestHashMap.get(EXTCODE.toUpperCase()).toString().trim().length() == 0) {
				requestHashMap.put(EXTCODE.toUpperCase(), getNodeValueNullCheck(reqMessageJson, EXTCODE));
			}
                requestHashMap.put(USERTYPE.toUpperCase(), getNodeValueNullCheck(reqMessageJson, USERTYPE));
			}catch(Exception e) {
				log.errorTrace("Exception occured - ", e);
			}
		}

        if(requestVO.getActionValue() == PretupsI.OPERATOR_RECEIVER_ACTION){
            XMLStringValidation.validateOptReceiverRequest(requestVO, requestHashMap.get(TYPE.toUpperCase()).toString(),
                    requestHashMap.get(DATE.toUpperCase()).toString(), requestHashMap.get(EXTNWCODE.toUpperCase()).toString(),
                    requestHashMap.get(EXTREFNUM.toUpperCase()).toString(), requestHashMap.get(LOGINID.toUpperCase()).toString(), 
                    requestHashMap.get(PSWD.toUpperCase()).toString(), requestHashMap.get(MSISDN.toUpperCase()).toString(),
                    requestHashMap.get(PIN.toUpperCase()).toString(), requestHashMap.get(EXTCODE.toUpperCase()).toString());
        }else{
            XMLStringValidation.validateExtChannelUserBalanceRequest(requestVO, requestHashMap.get(TYPE.toUpperCase()).toString(),
                    requestHashMap.get(DATE.toUpperCase()).toString(), requestHashMap.get(EXTNWCODE.toUpperCase()).toString(),
                    requestHashMap.get(MSISDN.toUpperCase()).toString(), requestHashMap.get(PIN.toUpperCase()).toString(),
                    requestHashMap.get(LOGINID.toUpperCase()).toString(), requestHashMap.get(PSWD.toUpperCase()).toString(),
                    requestHashMap.get(EXTCODE.toUpperCase()).toString(), requestHashMap.get(EXTREFNUM.toUpperCase()).toString(),
                    requestHashMap.get(USERTYPE.toUpperCase()).toString());
        }
        requestVO.setSenderExternalCode(requestHashMap.get(EXTCODE.toUpperCase()).toString());
        requestVO.setSenderLoginID(requestHashMap.get(LOGINID.toUpperCase()).toString());
        requestVO.setRequestMSISDN(requestHashMap.get(MSISDN.toUpperCase()).toString());
        requestVO.setExternalNetworkCode(requestHashMap.get(EXTNWCODE.toUpperCase()).toString());
        requestVO.setExternalReferenceNum(requestHashMap.get(EXTREFNUM.toUpperCase()).toString());
    }
    
    public static void parseFixedP2PParams(Map<String, Object> requestHashMap, JsonNode data, JsonNode fixedJson,
            RequestVO requestVO) throws BTSLBaseException {
    		requestHashMap.put(TYPE.toUpperCase(), requestVO.getServiceKeyword().toLowerCase());
    		
    		if(!BTSLUtil.isNullString(getNodeValue(data, getNodeValue(fixedJson, MSISDN1)))){
    			requestHashMap.put( MSISDN1.toUpperCase(), getNodeValue(data, getNodeValue(fixedJson, MSISDN1)));	
    			requestVO.setRequestMSISDN(requestHashMap.get(MSISDN1.toUpperCase()).toString());
    		}
    		if(!BTSLUtil.isNullString(getNodeValue(data, getNodeValue(fixedJson, MSISDN)))){
    			requestHashMap.put( MSISDN.toUpperCase(), getNodeValue(data, getNodeValue(fixedJson, MSISDN)));	
    			requestVO.setRequestMSISDN(requestHashMap.get(MSISDN.toUpperCase()).toString());
    		}	
        	
      
      }
    
    public static void parseFixedSystemParams(Map<String, Object> requestHashMap, JsonNode data, JsonNode fixedJson,
            RequestVO requestVO) throws BTSLBaseException {

        requestHashMap.put(TYPE.toUpperCase(), requestVO.getServiceKeyword().toLowerCase());
        requestHashMap.put(DATE.toUpperCase(), BTSLDateUtil.getGregorianDateInString(getNodeValue(fixedJson, DATE)));
        requestHashMap.put(EXTNWCODE.toUpperCase(), getNodeValue(data, getNodeValue(fixedJson, EXTNWCODE)));
        requestHashMap.put( EXTREFNUM.toUpperCase(), getNodeValue(data, getNodeValue(fixedJson, EXTREFNUM)));
        XMLStringValidation.validateSystemReceiverRequest(requestVO, requestHashMap.get(TYPE.toUpperCase()).toString(),
          requestHashMap.get(DATE.toUpperCase()).toString(), requestHashMap.get(EXTNWCODE.toUpperCase()).toString(), 
          requestHashMap.get(EXTREFNUM.toUpperCase()).toString());
        requestVO.setExternalNetworkCode(requestHashMap.get(EXTNWCODE.toUpperCase()).toString());
        requestVO.setExternalReferenceNum(requestHashMap.get(EXTREFNUM.toUpperCase()).toString());
    }

    public static void parseMandParams(Map<String, Object> requestHashMap, JsonNode data, JsonNode mandFields,
            RequestVO requestVO, JsonNode fields, List<String> arr) throws BTSLBaseException {

        Iterator<String> key = mandFields.fieldNames();

        while (key.hasNext()) {
            String field = key.next();
            if (data.get(getNodeValue(mandFields, field)).isArray()) {
                parseMandParamsArray(requestHashMap, data, mandFields, requestVO, fields, arr,field);
            } else {
                RestTagValueValidation.validateFixedFields(field, mandFields, data, requestVO, requestHashMap, arr);
            }
        }
    }
    
    public static void parseP2PMandParams(Map<String, Object> requestHashMap, JsonNode data, JsonNode mandFields,
            RequestVO requestVO, JsonNode fields, List<String> arr) throws BTSLBaseException {

        Iterator<String> key = mandFields.fieldNames();
        while (key.hasNext()) {
            String field = key.next();
            if (data.get(getNodeValue(mandFields, field)).isArray()) {
                parseP2PMandParamsArray(requestHashMap, data, mandFields, requestVO, fields, arr,field);
            } else {
                RestTagValueValidation.validateP2PFixedFields(field, mandFields, data, requestVO, requestHashMap, arr);
            }
        }
    }
    
    public static void parseOptionalParams(Map<String, Object> requestHashMap, JsonNode data, JsonNode optionFields,
            RequestVO requestVO, JsonNode fields, List<String> arr) throws BTSLBaseException {

        Iterator<String> key = optionFields.fieldNames();
        while (key.hasNext()) {
            String field = key.next();
            requestHashMap.put(field.toUpperCase(),
                    getNodeValue(data, getNodeValue(optionFields, field)));
            if(data.get(getNodeValue(optionFields, field))!=null){
            	if (data.get(getNodeValue(optionFields, field)).isArray()) {
                    parseOptionalParamsArray(requestHashMap, data, optionFields, requestVO, fields, arr,field);
                } else {
                    RestTagValueValidation.validateOptionalFields(field, optionFields, data, requestVO, requestHashMap, arr);
                }
             }
        }
    }
    public static void parseMandParamsArray(Map<String, Object> requestHashMap, JsonNode data, JsonNode mandFields,
            RequestVO requestVO, JsonNode fields, List<String> arr,String field) throws BTSLBaseException {

        ArrayNode jsonNode = (ArrayNode) data.get(getNodeValue(mandFields, field));
        Iterator<JsonNode> keyelement = jsonNode.elements();
        JsonNode arrayJson = fields.get(getNodeValue(mandFields, field));
        while (keyelement.hasNext()) {
            JsonNode elements = keyelement.next();
            Iterator<String> arrkey = arrayJson.fieldNames();
            while (arrkey.hasNext()) {
                String fieldarr = arrkey.next();
                for(JsonNode jsonarr : jsonNode){
                if(jsonarr.get(fieldarr).isArray()){
                        parseMandParamsArray(requestHashMap, jsonarr, arrayJson, requestVO, fields, arr,fieldarr);
                        RestTagValueValidation.validateFixedFields(fieldarr, arrayJson, elements, requestVO,
                                requestHashMap, arr);  
                }else{
                RestTagValueValidation.validateFixedFields(fieldarr, arrayJson, elements, requestVO,
                        requestHashMap, arr);
                break;
                }
                
                }
            }
        }
    
        
    }

    
    public static void parseP2PMandParamsArray(Map<String, Object> requestHashMap, JsonNode data, JsonNode mandFields,
            RequestVO requestVO, JsonNode fields, List<String> arr,String field) throws BTSLBaseException {

        ArrayNode jsonNode = (ArrayNode) data.get(getNodeValue(mandFields, field));
        Iterator<JsonNode> keyelement = jsonNode.elements();
        JsonNode arrayJson = fields.get(getNodeValue(mandFields, field));
        while (keyelement.hasNext()) {
            JsonNode elements = keyelement.next();
            Iterator<String> arrkey = arrayJson.fieldNames();
            while (arrkey.hasNext()) {
                String fieldarr = arrkey.next();
                for(JsonNode jsonarr : jsonNode){
                if(jsonarr.get(fieldarr).isArray()){
                	parseP2PMandParamsArray(requestHashMap, jsonarr, arrayJson, requestVO, fields, arr,fieldarr);
                        RestTagValueValidation.validateFixedFields(fieldarr, arrayJson, elements, requestVO,
                                requestHashMap, arr);  
                }else{
                RestTagValueValidation.validateP2PFixedFields(fieldarr, arrayJson, elements, requestVO,
                        requestHashMap, arr);
                }
                }
            }
        }
    
        
    }

    
    public static void parseOptionalParamsArray(Map<String, Object> requestHashMap, JsonNode data, JsonNode mandFields,
            RequestVO requestVO, JsonNode fields, List<String> arr,String field) throws BTSLBaseException {

        ArrayNode jsonNode = (ArrayNode) data.get(getNodeValue(mandFields, field));
        Iterator<JsonNode> keyelement = jsonNode.elements();
        JsonNode arrayJson = fields.get(getNodeValue(mandFields, field));
        while (keyelement.hasNext()) {
            JsonNode elements = keyelement.next();
            Iterator<String> arrkey = arrayJson.fieldNames();
            while (arrkey.hasNext()) {
                String fieldarr = arrkey.next();
                for(JsonNode jsonarr : jsonNode){
               if(jsonarr.get(fieldarr)==null){
                if(jsonarr.get(fieldarr).isArray()){
                	parseOptionalParamsArray(requestHashMap, jsonarr, arrayJson, requestVO, fields, arr,fieldarr);
                        RestTagValueValidation.validateOptionalFields(fieldarr, arrayJson, elements, requestVO,
                                requestHashMap, arr);  
                }else{
                RestTagValueValidation.validateOptionalFields(fieldarr, arrayJson, elements, requestVO,
                        requestHashMap, arr);
                }
                	}
                }
            }
        }
    
        
    }
    public static void parseOptionalParams(Map<String, Object> requestHashMap, JsonNode data, JsonNode optionFields)
            throws BTSLBaseException {

        Iterator<String> optionalkey = optionFields.fieldNames();
        while (optionalkey.hasNext()) {
            String currentOptionalKey = optionalkey.next();
            requestHashMap.put(currentOptionalKey.toUpperCase(),
                    getNodeValue(data, getNodeValue(optionFields, currentOptionalKey)));
        }
    }

    public static void parseMessageFormat(Map<String, Object> requestHashMap, JsonNode data, JsonNode mandFields,JsonNode fixedJson,
            RequestVO requestVO, String[] messageF, List<String> arr) throws BTSLBaseException {
        StringBuilder parsedRequestStr = new StringBuilder();
        int index = 0;
        parsedRequestStr.append(requestHashMap.get(TYPE.toUpperCase()));
        
        for (int i = 0; i < messageF.length ; i++) {
            if( messageF.length==1&&BTSLUtil.isNullString(messageF[0])){
                continue;
            }
            if(!BTSLUtil.isNullString(getNodeValue(mandFields, messageF[i]))){
                messageformatmandFixed(mandFields,data,messageF[i],parsedRequestStr,arr,requestHashMap,index); 
            }else if(!BTSLUtil.isNullString(getNodeValue(fixedJson, messageF[i]))){
                messageformatmandFixed(fixedJson,data,messageF[i],parsedRequestStr,arr,requestHashMap,index); 
            }
        }
        

        requestVO.setDecryptedMessage(parsedRequestStr.toString());
    }
    
    public static void messageformatmandFixed(JsonNode mandFields,JsonNode data, String messageF,StringBuilder parsedRequestStr, List<String> arr,Map<String, Object> requestHashMap, int index ){
        
        if (data.get(getNodeValue(mandFields, messageF)).isArray()) {
            ArrayNode jsonNode = (ArrayNode) data.get(getNodeValue(mandFields, messageF));
            Iterator<JsonNode> keyelement = jsonNode.elements();
            while (keyelement.hasNext()) {
                keyelement.next();
                parsedRequestStr.append(chnlMessageSep + arr.get(index + 1) + chnlMessageSep + arr.get(index));
                index = index + 2;
            }
        } else {
            parsedRequestStr.append(chnlMessageSep + requestHashMap.get(messageF.toUpperCase()));
        }
    }

    public static void generateJsonResponse(RequestVO requestVO) {
        PretupsResponse<JsonNode> response = new PretupsResponse<>();
        final String methodName = "generateJsonResponse";
        JsonObject json = new JsonObject();
        String responseStr =null;
        boolean change = false;
        final HashMap requestHashMap = requestVO.getRequestMap();
        Object valueObj = requestVO.getValueObject();

        try {

            if (requestHashMap!=null&&requestHashMap.containsKey(LASTTRFTYPE)) {
                requestVO.setValueObject(requestHashMap.get(requestHashMap.get(LASTTRFTYPE).toString()));
                change = true;
            }
            if (requestHashMap!=null&&requestHashMap.containsKey("CHNUSERVO")) {
                ChannelUserVO   channelUserVO = (ChannelUserVO) requestVO.getRequestMap().get("CHNUSERVO");
                requestVO.setValueObject(channelUserVO);
                change = true;
            }
            String jsonObj = PretupsRestUtil.getMessageString(requestVO.getServiceKeyword().toLowerCase());

            JsonNode fields = (JsonNode) PretupsRestUtil.convertJSONToObject(jsonObj, new TypeReference<JsonNode>() {
            });
            generateFixedResponse(fields, requestVO, json);
            generateMandResponse(fields, requestVO, json);
            /*
            if(requestVO.getMessageCode()==null || requestVO.getMessageCode().equals("200"))
            {
            generateMandResponse(fields, requestVO, json);
            }*/
            responseStr  = json.toString();
            response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,
                    (JsonNode) PretupsRestUtil.convertJSONToObject(responseStr, new TypeReference<JsonNode>() {
                    }));
            if (change) {
                requestVO.setValueObject(valueObj);
            }
            requestVO.setJsonReponse(response);
            requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            log.error(methodName, "Exception e: " + e);
        } finally {
            requestVO.setJsonReponse(response);
            requestVO.setSenderReturnMessage(responseStr);
        }
    }

    public static void generateFixedResponse(JsonNode fields, RequestVO requestVO, JsonObject json) {
    	
    	final String methodName = "generateJsonResponse";
        JsonNode fixedResponsJson = fields.get(RESPONSEFIXEDPARAMETERS);
        json.addProperty(TYPE, requestVO.getServiceKeyword());
        String EXTERNAL_DATE_FORMAT = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT);
        json.addProperty(getNodeValue(fixedResponsJson, DATE), BTSLDateUtil.getSystemLocaleDate(new Date(), EXTERNAL_DATE_FORMAT));
        
        if (requestVO.isSuccessTxn()) {
        	if(!BTSLUtil.isNullString(getNodeValue(fixedResponsJson, TXNSTATUS.toLowerCase())))
        	{
        		json.addProperty(getNodeValue(fixedResponsJson, TXNSTATUS.toLowerCase()), PretupsI.TXN_STATUS_SUCCESS);	
        	}
        }	
        else
        {
        	if(!BTSLUtil.isNullString(getNodeValue(fixedResponsJson, TXNSTATUS.toLowerCase())))
        	{
        		json.addProperty(getNodeValue(fixedResponsJson, TXNSTATUS.toLowerCase()), PretupsI.TXN_STATUS_FAIL);	
        	}
        }
        
        if(PretupsI.SERVICE_TYPE_C2STRFSVCNT.equalsIgnoreCase(requestVO.getServiceKeyword()))
        {

        	
			JsonArray arr = new JsonArray();
			JsonParser parser = new JsonParser();
			JsonElement elem;
			JsonArray elemArr = new JsonArray();
			try {
				if(requestVO.getJsonReponse()!=null && requestVO.getJsonReponse().getDataObject()!=null){
				elem = parser.parse(PretupsRestUtil.convertObjectToJSONString(requestVO.getJsonReponse().getDataObject()));
				elemArr = elem.getAsJsonArray();
				}
			json.add("c2sservicedetails", elemArr);
			} catch (JsonSyntaxException e) {
				 log.errorTrace(methodName, e);
		         requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
		         log.error(methodName, "Exception e: " + e);
			} catch (JsonProcessingException e) {
				log.errorTrace(methodName, e);
		        requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
		        log.error(methodName, "Exception e: " + e);
			}
        }
        else if(PretupsI.AUTO_COMPLETE_USERS_DETAILS.equalsIgnoreCase(requestVO.getServiceKeyword()) ||
        		"USRPMTYPE".equalsIgnoreCase(requestVO.getServiceKeyword()))
        {

        	
			JsonArray arr = new JsonArray();
			JsonParser parser = new JsonParser();
			JsonElement elem;
			JsonArray elemArr = new JsonArray();
			try {
				if(requestVO.getJsonReponse()!=null && requestVO.getJsonReponse().getDataObject()!=null){
				elem = parser.parse(PretupsRestUtil.convertObjectToJSONString(requestVO.getJsonReponse().getDataObject()));
				elemArr = elem.getAsJsonArray();
				}
			json.add("userDetails", elemArr);
			} catch (JsonSyntaxException e) {
				 log.errorTrace(methodName, e);
		         requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
		         log.error(methodName, "Exception e: " + e);
			} catch (JsonProcessingException e) {
				log.errorTrace(methodName, e);
		        requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
		        log.error(methodName, "Exception e: " + e);
			}
        }
        
        if("COMINCOME".equalsIgnoreCase(requestVO.getServiceKeyword()))
        {

			JsonArray arr = new JsonArray();
			JsonParser parser = new JsonParser();
			JsonElement elem = null;
			JsonArray elemArr = new JsonArray();
			try 
			{
				if(requestVO.getJsonReponse()!=null && requestVO.getJsonReponse().getDataObject()!=null){
				elem = parser.parse(PretupsRestUtil.convertObjectToJSONString(requestVO.getJsonReponse().getDataObject()));
				json.add("comincomedetails", elem);
			}
			
			} catch (JsonSyntaxException e) {
				 log.errorTrace(methodName, e);
		         requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
		         log.error(methodName, "Exception e: " + e);
			} catch (JsonProcessingException e) {
				log.errorTrace(methodName, e);
		        requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
		        log.error(methodName, "Exception e: " + e);
			}
        }
        
        if("UPUSRHRCHY".equalsIgnoreCase(requestVO.getServiceKeyword()))
        {

        	
			JsonArray arr = new JsonArray();
			JsonParser parser = new JsonParser();
			JsonElement elem;
			try {
				elem = parser
						.parse(PretupsRestUtil.convertObjectToJSONString(requestVO.getJsonReponse().getDataObject()));
			

			JsonArray elemArr = elem.getAsJsonArray();

			json.add("upwardhierarchy", elemArr);
			} catch (JsonSyntaxException e) {
				
				log.errorTrace(methodName, e);
			} catch (JsonProcessingException e) {
				
				log.errorTrace(methodName, e);
			}
        }
        if("C2CBUYUSENQ".equalsIgnoreCase(requestVO.getServiceKeyword()))
        {

        	
			JsonParser parser = new JsonParser();
			JsonElement elem;
			try {
				elem = parser
						.parse(PretupsRestUtil.convertObjectToJSONString(requestVO.getJsonReponse().getDataObject()));
			

			JsonArray elemArr = elem.getAsJsonArray();

			json.add("c2cRecentEnq", elemArr);
			} catch (JsonSyntaxException e) {
				
				log.errorTrace(methodName, e);
			} catch (JsonProcessingException e) {
				
				log.errorTrace(methodName, e);
			}
        }
        
        if(!("c2ctrfappr".equalsIgnoreCase(requestVO.getServiceKeyword())) && !("mvd".equalsIgnoreCase(requestVO.getServiceKeyword())))
        {
        if (BTSLUtil.isNullString(requestVO.getExternalReferenceNum())) {
        	if(!BTSLUtil.isNullString(getNodeValue(fixedResponsJson, EXTREFNUM)))
        	{
            json.addProperty(getNodeValue(fixedResponsJson, EXTREFNUM), "");
        	}
        } else {
            json.addProperty(getNodeValue(fixedResponsJson, EXTREFNUM), requestVO.getExternalReferenceNum());
        }
        }
        if(null!=requestVO.getMessageCode()){
        	String message;
        	if(requestVO.isSuccessTxn() && (("rctrf").equalsIgnoreCase((json.get(TYPE).getAsString()))|| ("postpaid").equalsIgnoreCase((json.get(TYPE).getAsString()))|| ("vrctrf").equalsIgnoreCase((json.get(TYPE).getAsString()))) ){
        		message =PretupsRestUtil.getMessageString(PretupsErrorCodesI.C2S_SENDER_SUCCESS, requestVO.getMessageArguments());
        	}else if(requestVO.isSuccessTxn() && (("vomsconsreq").equalsIgnoreCase((json.get(TYPE).getAsString()))) ){
        		message =PretupsRestUtil.getMessageString(PretupsErrorCodesI.VOUCHER_CONSUMPTION_SUCCESS, requestVO.getMessageArguments());
        	}
        	else{
        		
        		message=getMessage(requestVO.getLocale(), requestVO.getMessageCode(), requestVO.getMessageArguments());
        		//message =PretupsRestUtil.getMessageString(requestVO.getMessageCode(), requestVO.getMessageArguments());
        	}
			if(message!=null){
                json.addProperty(getNodeValue(fixedResponsJson, MESSAGE.toLowerCase()), message );
            }
			
			if(requestVO.isSuccessTxn() && (("vcavlblreq").equalsIgnoreCase((json.get(TYPE).getAsString()))) ){
				ChannelUserVO channelUserVO = (ChannelUserVO)(requestVO.getSenderVO());
				ArrayList<ArrayList<String> > arr = channelUserVO.getVoucherList();
				if(arr.size() > 0)
					json.addProperty(getNodeValue(fixedResponsJson, AVLBLVOUCHERS.toLowerCase()), arr.get(0).get(arr.get(0).size() - 1));
				else
					json.addProperty(getNodeValue(fixedResponsJson, AVLBLVOUCHERS.toLowerCase()), 0);
			}
			else if(requestVO.isSuccessTxn() && (("c2cviewvc").equalsIgnoreCase((json.get(TYPE).getAsString()))) ){
				ChannelTransferVO channelTrfVO = requestVO.getChannelTransferVO();
				json.add(getNodeValue(fixedResponsJson, TRANSFER.toLowerCase()), new Gson().toJsonTree(channelTrfVO));
			}
        }
    }

    public static String getMessage(Locale locale, String key, String[] p_args) {
    	
        final String methodName = "getMessage";    	
		HttpServletRequest request = null;
		String language = null;
		String country = null;

		if (RequestContextHolder.getRequestAttributes() != null) {

			request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

			if(request != null) {
			language = request.getHeader("language");
			country = request.getHeader("country");
			}
		}

		if (language != null && country != null) {
			locale = new Locale(language, country);
		}
    	    		

        StringBuilder loggerValue= new StringBuilder();
        
        if(key == null) {
        	log.debug(methodName, "key is null");
        	key = PretupsErrorCodesI.PROPER_ERROR_NOT_SET_KEYISNULL;
        }
        
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_requestVO=");
        	loggerValue.append(key.toString());
        	log.debug(methodName, loggerValue);
        }
        String message = BTSLUtil.getMessage(locale, key, p_args);
        try {
            final LocaleMasterVO localeMasterVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
            if (message.indexOf("mclass^") == 0) {
                final int colonIndex = message.indexOf(":");
                final String messageClassPID = message.substring(0, colonIndex);
                final String[] messageClassPIDArray = messageClassPID.split("&");
                final String messageClass = messageClassPIDArray[0].split("\\^")[1];
                final String pid = messageClassPIDArray[1].split("\\^")[1];
                message = message.substring(colonIndex + 1);
                int endIndexForMessageCode;
                String messageCode = null;
                if ("ar".equals(localeMasterVO.getLanguage())) {
                    endIndexForMessageCode = message.indexOf("%00%3A");
                    if (endIndexForMessageCode != -1) {
                        messageCode = URLDecoder.decode(message.substring(0, endIndexForMessageCode), "UTF16");
                        // message=message.substring(endIndexForMessageCode+1)
                        message = message.substring(endIndexForMessageCode);
                    }
                } else {
                    endIndexForMessageCode = message.indexOf(":");
                    if (endIndexForMessageCode != -1) {
                        messageCode = message.substring(0, endIndexForMessageCode);
                        message = message.substring(endIndexForMessageCode + 1);
                    }
                }
                /*
                 * Code killed by Avinash: to remove encoding in case of XML
                 * response.
                 */
                /*
                 * if("ar".equals(locale.getLanguage()) &&
                 * !message.startsWith("%"))
                 * message=BTSLUtil.encodeSpecial(message,true,localeMasterVO.
                 * getEncoding())
                 * else if(!"ar".equals(locale.getLanguage()))
                 * message=URLEncoder.encode(message,localeMasterVO.getEncoding()
                 * )
                 */
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            log.error(methodName, "EXCEPTION" + e);
        }
        return message;
    }

    public static void generateMandResponse(JsonNode fields, RequestVO requestVO, JsonObject json) throws Exception {

        JsonNode mandResponsJson = fields.get(RESPONSEMANDPARAMETERS);
        if (log.isDebugEnabled()) {
        	log.debug("generateMandResponse", mandResponsJson);
        }
        Iterator<String> key = mandResponsJson.fieldNames();
        Object list = requestVO.getValueObject();
        JsonArray jsonarr = new JsonArray();
        String res = "res";

        while (key.hasNext()) {
            String field = key.next();
            if ((fields.get(field + res) != null) && (fields.get(field + res)).isObject()) {
                if (list instanceof List<?>) {
                    ArrayList list1 = (ArrayList<Object>) requestVO.getValueObject();
                    while (!list1.isEmpty()) {
                        RestTagValueValidation.generateMandFieldsResponse(field + res, fields, json, requestVO,
                                list1.get(0), true, jsonarr);
                        list1.remove(0);
                    }
                } else {
                    RestTagValueValidation.generateMandFieldsResponse(field + res, fields, json, requestVO, list, true,
                            jsonarr);
                }
                RestTagValueValidation.generateMandFieldsResponse(field, fields, json, requestVO, list, true, jsonarr);
            } else {
                RestTagValueValidation.generateMandFieldsResponse(field, mandResponsJson, json, requestVO, list, false,
                        jsonarr);
            }
        }
    }

    private static String getNodeValue(JsonNode node, String value) {
        if (node.get(value) != null) {
            return node.get(value).textValue();
        } else {
            return "";
        }
    }

    private static String getNodeValueNullCheck(JsonNode node, String value) {
        if (node.get(value) != null) {
        	if(node.get(value).textValue() == null) {
        		return "";
        	}else {
        		return node.get(value).textValue();	
        	}
            
        } else {
            return "";
        }
    }
    public static String getMessage(Locale locale, String key) {
        return getMessage(locale, key, null);
    }
}
