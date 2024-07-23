/*
 * Created on June 18, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.vodaidea.vodafone.comverse;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;

/**
 * @author abhay.singh
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ComverseRequestFormatter 
{
    public static Log _log = LogFactory.getLog(ComverseRequestFormatter.class);
//    private String lineSep = null;
    private String _soapAction="";
    /** VFI: Birendra: 24April15: To Fetch the Values from IN Files against keys. */
    private String _interfaceID = "";
    /** VFI: Birendra: 24April15 */
    
    public ComverseRequestFormatter()
    {
//    	lineSep = System.getProperty("line.separator")+"";
    }
    
    public ComverseRequestFormatter(String p_interfaceId)
    {
//    	lineSep = System.getProperty("line.separator")+"";
    //	this._interfaceID = p_interfaceId; 
    }
 
    /**
     * This method is used to parse the response string based on the type of Action.
     * @param	int p_action
     * @param	HashMap p_map
     * @return	String.
     * @throws	Exception
     */
	protected String generateRequest(int p_action, HashMap p_map) throws Exception 
	{
		final String methodName = "generateRequest";
        if(_log.isDebugEnabled()){
        	_log.debug(methodName,"Entered p_action := "+p_action+", map:= "+p_map);
        }
		String str = null;
		try{
			p_map.put("action", String.valueOf(p_action));
			
		}
		catch(Exception e)
		{
		_log.errorTrace(methodName, e);
		}
		 if(_log.isDebugEnabled()){
	        	_log.debug(methodName,"Entered generateRequest p_map:= "+p_map);
	        }
	    try
		{
			switch(p_action)
			{
			
			    case ComverseI.ACTION_ACCOUNT_DETAILS: 
			    {
			    	 if(_log.isDebugEnabled()){
				        	_log.debug(methodName,"Entered in Switch Case "+ ComverseI.ACTION_ACCOUNT_DETAILS +"p_action:= "+p_action+", p_map "+p_map );
				        }
			       // _soapAction="RetrieveSubscriberLite";
				   str=generateGetAccountInfoRequest(p_map);
				   
				   break;	
			    }
				case ComverseI.ACTION_RECHARGE_CREDIT: 
				{
				   // _soapAction="NonVoucherRecharge";
				    str=generateRechargeCreditRequest(p_map);
					break;	
				}
				case ComverseI.ACTION_IMMEDIATE_DEBIT: 
				{
				  //  _soapAction="NonVoucherRecharge";
					str=generateImmediateDebitRequest(p_map);
					break;	
				}	
				case ComverseI.ACTION_IMMEDIATE_CREDIT: 
				{
				  //  _soapAction="NonVoucherRecharge";
					str=generateImmediateDebitRequest(p_map);
					break;	
				}
				
				/** VFI: Birendra: START: Commented */
				/*case ComverseI.ACTION_LANGUAGE_CODE: 
				{
				    _soapAction="RetrieveSubscriberWithIdentityNoHistory";
					str=generateLanguageCodeRequest(p_map);
					break;	
				}*/
				/** VFI: Birendra: END */
			}
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e := "+e.getMessage());
		   	_log.errorTrace(methodName, e);
		     
			throw e;
		} 
		finally
		{
			if(_log.isDebugEnabled()){
				_log.debug(methodName,"Exited Request String:= "+str);
			}
		}
		return str;
	}

	/** VFI: Birendra: START: Commented */
    /**
	 * This method is used to generate the request for getting account details along with AccountStatus.
	 * @param	HashMap	p_requestMap
	 * @return	String
	 * @throws	Exception
	 */
/*    private String generateGetAccountInfoRequest(HashMap p_requestMap) throws Exception
	{
	    if(_log.isDebugEnabled()) _log.debug("generateGetAccountInfoRequest","Entered p_requestMap::"+p_requestMap);
		String requestStr= null;
		StringBuffer stringBuffer = null;
		try
		{
			stringBuffer = new StringBuffer(1028);
			stringBuffer.append(EnvelopeTag());	
			String header = HeaderTag(_soapAction, p_requestMap);
			stringBuffer.append(header);
			StringBuffer body = new StringBuffer();
            body.append("<soap:Body>"+lineSep);
			body.append("<RetrieveSubscriberLite xmlns=\"http://comverse-in.com/prepaid/ccws\">"+lineSep);    
            body.append("<subscriberID>"+p_requestMap.get("MSISDN")+"</subscriberID>"+lineSep);
			body.append("</RetrieveSubscriberLite>"+lineSep);            
			body.append("</soap:Body>"+lineSep);
            stringBuffer.append(body.toString());
            stringBuffer.append("</soap:Envelope>");
	        requestStr = stringBuffer.toString().trim();
		}
		catch(Exception e)
		{
			_log.error("generateGetAccountInfoRequest","Exception e::"+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateGetAccountInfoRequest","Exiting Request String:requestStr::"+requestStr);
		}
		return requestStr;
	}*/
	/** VFI: Birendra: END */
    
    /**
     * @author birendra.mishra
     * @param p_requestMap
     * @return
     * @throws Exception
     */
    private String generateGetAccountInfoRequest(HashMap p_requestMap) throws Exception
	{
    	final String methodName = "generateGetAccountInfoRequestOne";
    	 if(_log.isDebugEnabled()){
   			_log.debug(methodName,"Entered  generateGetAccountInfoRequest p_requestMap := "+p_requestMap);
   		}
    	 String userIdName ="";
    	try{
    		if(_log.isDebugEnabled()){
       			_log.debug(methodName,"   _interfaceID := "+_interfaceID);
       		}
    		_interfaceID= (String)p_requestMap.get("INTERFACE_ID");
       	   if(_log.isDebugEnabled()){
       		_log.debug(methodName,"Entered  generateGetAccountInfoRequest NETWORK_CODE ="+ p_requestMap.get("NETWORK_CODE")+"_interfaceID"+(String)p_requestMap.get("INTERFACE_ID"));
    		_log.debug(methodName,"Entered  generateGetAccountInfoRequest"+p_requestMap.get("NETWORK_CODE")+"_SOAP_ONE_USER_ID_NAME="+  FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),p_requestMap.get("NETWORK_CODE")+"_SOAP_ONE_USER_ID_NAME"));
           	
    		_log.debug(methodName,"Entered  generateGetAccountInfoRequest p_requestMap := "+p_requestMap);
    	   	
         			_log.debug(methodName,"Entered  generateGetAccountInfoRequest userIdName := "+ FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"), p_requestMap.get("NETWORK_CODE")+"_SOAP_ONE_USER_ID_NAME").toString());
         		}
    	
         userIdName = FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"), p_requestMap.get("NETWORK_CODE")+"_SOAP_ONE_USER_ID_NAME").toString();
        
    	}
    	catch(Exception e)
    	{
    		_log.debug(methodName,"Entered  Exception in generateGetAccountInfoRequest"+ p_requestMap.get("TRANSACTION_ID"));
    		
    		e.printStackTrace();
    		throw e;
    	}
    	String securityToken = "";
        String requestStr = null;

        try {
        	
          //  String lineSep = System.getProperty("line.separator") + "\r";
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(envelopeTagOpen());
            stringBuffer.append(headerTagOne());
            StringBuffer body = new StringBuffer();
            body.append("<soapenv:Body>");
            	body.append("<com:SubscriberRetrieveLite>");
            		body.append("<com:input>");
            			body.append("<requestLanguageCode>1</requestLanguageCode>");

			            if(PretupsI.DEFAULT_YES.equalsIgnoreCase(FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"IS_SECURITY_TOKEN_ENABLE"))){
			            	body.append("<securityToken>" + securityToken + "</securityToken>");
			            }
			            else{
			            	body.append("<securityToken></securityToken>");
			            }
			            body.append("<userIdName>" +  userIdName + "</userIdName>");
			            body.append("<subscriberId>");
			            	body.append("<attribs>0</attribs>");
			            	body.append("<subscriberId changed=\"false\"  set=\"true\">");

			            	if (FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),p_requestMap.get("NETWORK_CODE")+"_ComvSoapOne_MSISDNPREEFIX91_ALLOWED").equalsIgnoreCase("YES")) {

                                if (p_requestMap.get("MSISDN").toString().length() == ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()) {
                                        body.append("<value>" + FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"COUNTRY_CODE")+p_requestMap.get("MSISDN")  + "</value>");
                                } else {
                                        body.append("<value>" + p_requestMap.get("MSISDN").toString() + "</value>");
                                }
                        } else {

                                if (p_requestMap.get("MSISDN").toString().length() == ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()) {
                                	body.append("<value>" + p_requestMap.get("MSISDN")  + "</value>");
                                        body.append("<value>" + p_requestMap.get("MSISDN").toString() + "</value>");
                                } else {
                                        body.append("<value>" + p_requestMap.get("MSISDN").toString().substring(2) + "</value>");
                                }

                        }
			            	
			            	
			            	body.append("</subscriberId>");
			            	body.append("<subscriberExternalIdType changed=\"false\" set=\"true\">");
			            		body.append("<value>1</value>");
			            	body.append("</subscriberExternalIdType>");
			            body.append("</subscriberId>");
			            body.append("<info>");
			            	body.append("<attribs>0</attribs>");
			            	body.append("<onlyCoreBalance changed=\"false\"  set=\"true\">");
			            		body.append("<value>true</value>");
			            	body.append("</onlyCoreBalance>");
			            	body.append("<subscriberData changed=\"false\"  set=\"true\">");
			            		body.append("<value>true</value>");
			            	body.append("</subscriberData>");
			            body.append("</info>");
			        body.append("</com:input>");
			    body.append("</com:SubscriberRetrieveLite>");
            body.append("</soapenv:Body>");
            body.append(envelopeTagClose());
            
            stringBuffer.append(body.toString());
            stringBuffer.trimToSize();
            requestStr = stringBuffer.toString();
            requestStr.trim();
                
        } catch (Exception e) {
        	_log.error(methodName,"Exception e := "+e.getMessage());
        	
           	_log.errorTrace(methodName, e);
            
        }
        finally
		{
			if(_log.isDebugEnabled()){
				_log.debug(methodName,"Exiting Request String:requestStr := "+requestStr);
			}
		}
        return requestStr;

}
    /** VFI: Birendra: START: Commented */
    
    /**
     * 
     * @param p_map
     * @return
     * @throws Exception
     */
    /*private String generateRechargeCreditRequest(HashMap p_requestMap) throws Exception
    {
	    if(_log.isDebugEnabled()) _log.debug("generateRechargeCreditRequest","Entered p_requestMap::"+p_requestMap);
		String requestStr= null;
		StringBuffer stringBuffer = null;
		try
		{
			stringBuffer = new StringBuffer();
			stringBuffer.append(EnvelopeTag());
			stringBuffer.append( HeaderTag(_soapAction, p_requestMap));
	        
			StringBuffer body = new StringBuffer();            
            body.append("<soap:Body>"+lineSep);
            body.append("<NonVoucherRecharge xmlns=\"http://comverse-in.com/prepaid/ccws\">"+lineSep);
            body.append("<subscriberId>"+ p_requestMap.get("MSISDN").toString() +"</subscriberId>"+lineSep);
            body.append("<rechValue>"+ p_requestMap.get("transfer_amount").toString() +"</rechValue>"+lineSep);                          
			body.append("<rechDays>"+ p_requestMap.get("VALIDITY_DAYS").toString() +"</rechDays>"+lineSep);
		    body.append("<rechComm>"+ p_requestMap.get("IN_RECON_ID").toString() +"</rechComm>"+lineSep);
            body.append("</NonVoucherRecharge>"+lineSep);
            body.append("</soap:Body>"+lineSep);
            stringBuffer.append(body.toString());
            stringBuffer.append("</soap:Envelope>");
	        requestStr = stringBuffer.toString().trim();
		}
		catch(Exception e)
		{
			_log.error("generateRechargeCreditRequest","Exception e: "+e);
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateRechargeCreditRequest","Exiting Request requestStr::"+requestStr);
		}
		return requestStr;
    }*/

    /** VFI: Birendra: END */
    
    /**
     * @author birendra.mishra
     * @param p_requestMap
     * @return
     * @throws Exception
     */
    private String generateRechargeCreditRequest(HashMap p_requestMap) throws Exception
    {
    	final String methodName = "generateRechargeCreditRequest";
        String requestStr = null;
        String securityToken = "";

        try {

        	if(_log.isDebugEnabled()){
       			_log.debug(methodName,"   _interfaceID := "+_interfaceID);
       		}
        	
        	_interfaceID= (String)p_requestMap.get("INTERFACE_ID");
        	        	
       	
               // String lineSep = System.getProperty("line.separator") + "\r";
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(envelopeTagOpen());
                stringBuffer.append(headerTagOne());
                StringBuffer body = new StringBuffer();
                body.append("<soapenv:Body>");
                body.append("<com:NonVoucherRechargeSubscriber><com:input><requestLanguageCode>1</requestLanguageCode>");
                _log.debug(methodName, "Before IS_SECURITY_TOKEN_ENABLE");
                if(PretupsI.DEFAULT_YES.equalsIgnoreCase(FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"IS_SECURITY_TOKEN_ENABLE"))){
                	body.append("<securityToken>" + securityToken + "</securityToken>");
                }
                else{
                	body.append("<securityToken></securityToken>");
                }
                body.append("<userIdName>" + FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),p_requestMap.get("NETWORK_CODE")+"_"+"SOAP_ONE_USER_ID_NAME").toString() + "</userIdName>");
                body.append("<subscriberID><attribs>0</attribs><subscriberId changed=\"false\"  set=\"true\">");
                if (FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),p_requestMap.get("NETWORK_CODE")+"_ComvSoapOne_MSISDNPREEFIX91_ALLOWED").equalsIgnoreCase("YES")) {
                	if (p_requestMap.get("MSISDN").toString().length() == ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()) {
                            body.append("<value>" + FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"COUNTRY_CODE")+p_requestMap.get("MSISDN")  + "</value>");
                    } else {
                            body.append("<value>" + p_requestMap.get("MSISDN").toString() + "</value>");
                    }
               } else {
                    if (p_requestMap.get("MSISDN").toString().length() == ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()) {
                    	body.append("<value>" + p_requestMap.get("MSISDN")  + "</value>");
                            body.append("<value>" + p_requestMap.get("MSISDN").toString() + "</value>");
                    } else {
                            body.append("<value>" + p_requestMap.get("MSISDN").toString().substring(2) + "</value>");
                    }

               }
                body.append("</subscriberId>");
                body.append("<subscriberExternalIdType changed=\"false\"");
                body.append("  set=\"true\"");
                body.append(">");
                body.append("<value>1</value>");
                body.append("</subscriberExternalIdType>");
                body.append("</subscriberID>");
                body.append("<faceValue>" + p_requestMap.get("transfer_amount").toString() + "</faceValue>");
                body.append("<rechargeOffset>" + p_requestMap.get("VALIDITY_DAYS").toString() + "</rechargeOffset>");
                body.append("<unitType>" + FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"SOAP_ONE_UNIT_TYPE_VALUE").toString() + "</unitType>");
                body.append("<currencyIsoCode>" + FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"SOAP_ONE_CURRENCY_ISOCODE_VALUE").toString() + "</currencyIsoCode>");
                body.append("<originatingApp>" + FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"SOAP_ONE_ORIGNATINGAPP_VALUE").toString() + "</originatingApp>");
                //body.append("<comments>" +p_requestMap.get("VOUCHER_TYPE").toString()+"_"+ p_requestMap.get("SERIAL_NUMBER").toString() + "_" + p_requestMap.get("MRP").toString() + "_" + p_requestMap.get("PRODUCT_NAME").toString()+ "_" + p_requestMap.get("VOUCHER_SEGMENT").toString()+ "_" + p_requestMap.get("IN_TXN_ID").toString());
                body.append("<comments>"+FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),p_requestMap.get("NETWORK_CODE")+"_"+p_requestMap.get("VOUCHER_TYPE").toString()).toString()+"_"+ p_requestMap.get("SERIAL_NUMBER").toString() + "_" + p_requestMap.get("MRP").toString() + "_" + p_requestMap.get("PRODUCT_NAME").toString()+ "-" + FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),p_requestMap.get("NETWORK_CODE")+"_"+p_requestMap.get("VOUCHER_SEGMENT").toString()).toString()+ "-"+ p_requestMap.get("IN_TXN_ID").toString());
                body.append("</comments>");

                //body.append("</comments>");
       	    
                body.append("<currencyConversionRate>" + FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"SOAP_ONE_CURRENCYCONVERSION_RATE_VALUE").toString() + "</currencyConversionRate>");
                body.append("<paymentProfileID>" + FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"SOAP_ONE_PAYMENT_PROFILE_ID_VALUE").toString() + "</paymentProfileID>");
                body.append("</com:input>");
                body.append("</com:NonVoucherRechargeSubscriber>");
                body.append("</soapenv:Body>");
                stringBuffer.append(body.toString());
                stringBuffer.append("</soapenv:Envelope>");
                stringBuffer.trimToSize();
                requestStr = stringBuffer.toString();
                requestStr.trim();

        }

        catch(Exception e)
		{
			_log.error(methodName,"Exception e := "+e);
			_log.errorTrace(methodName, e);
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()){
				_log.debug(methodName,"Exiting Request requestStr := "+requestStr);
			}
		}

        return requestStr;
}
 
    /** VFI: Birendra: START: Commented */
    /**
     * 
     * @param map
     * @return
     * @throws Exception
     */
    /*private String generateImmediateDebitRequest(HashMap p_requestMap) throws Exception
    {
		if(_log.isDebugEnabled())_log.debug("generateImmediateDebitRequest","Entered p_requestMap::"+p_requestMap);
		String requestStr= null;
		StringBuffer stringBuffer = null;
		try
		{
			stringBuffer = new StringBuffer();
			stringBuffer.append(EnvelopeTag());
			stringBuffer.append( HeaderTag(_soapAction, p_requestMap));
	        
			StringBuffer body = new StringBuffer();            
            body.append("<soap:Body>");
            body.append("<NonVoucherRecharge xmlns=\"http://comverse-in.com/prepaid/ccws\">");
            body.append("<subscriberId>"+ p_requestMap.get("MSISDN").toString() +"</subscriberId>"+lineSep);
            body.append("<rechValue>-"+ p_requestMap.get("transfer_amount").toString() +"</rechValue>"+lineSep);                          
			body.append("<rechDays>"+ p_requestMap.get("CAL_OLD_EXPIRY_DATE").toString() +"</rechDays>"+lineSep);
		    body.append("<rechComm>"+ p_requestMap.get("IN_RECON_ID").toString() +"</rechComm>"+lineSep);
            body.append("</NonVoucherRecharge>"+lineSep);
            body.append("</soap:Body>"+lineSep);
            stringBuffer.append(body.toString());
            stringBuffer.append("</soap:Envelope>");
	        requestStr = stringBuffer.toString().trim();
		}
		catch(Exception e)
		{
			_log.error("generateImmediateDebitRequest","Exception e: "+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateImmediateDebitRequest","Exiting  requestStr::"+requestStr);
		}
        return requestStr;
    }*/
    /** VFI: Birendra: END */
    
    
    /**
     * @author birendra.mishra
     * @param p_requestMap
     * @return
     * @throws Exception
     */
    private String generateImmediateDebitRequest(HashMap p_requestMap) throws Exception
    {
    	final String methodName = "generateImmediateDebitRequest";
        String requestStr = null;
        String securityToken = "";

        try {

                //String lineSep = System.getProperty("line.separator") + "\r";
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(envelopeTagOpen());
                stringBuffer.append(headerTagOne());
                StringBuffer body = new StringBuffer();
                body.append("<soapenv:Body>");
                body.append("<com:SubscriberAdjustBalanceInstance><com:input><requestLanguageCode>1</requestLanguageCode>");

                if(PretupsI.DEFAULT_YES.equalsIgnoreCase(FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"IS_SECURITY_TOKEN_ENABLE"))){
                	body.append("<securityToken>" + securityToken + "</securityToken>");
                }
                else{
                	body.append("<securityToken></securityToken>");
                }
                
                body.append("<userIdName>" + FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),p_requestMap.get("NETWORK_CODE")+"_"+"SOAP_ONE_USER_ID_NAME").toString() + "</userIdName>");
                body.append("<subscriberId><attribs>0</attribs><subscriberId changed=\"false\"  set=\"true\">");

                if (FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),p_requestMap.get("NETWORK_CODE")+"_"+"ComvSoapOne_MSISDNPREEFIX91_ALLOWED").equalsIgnoreCase("YES")) {

                    if (p_requestMap.get("MSISDN").toString().length() == ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()) {
                            body.append("<value>" + FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"COUNTRY_CODE")+p_requestMap.get("MSISDN")  + "</value>");
                    } else {
                            body.append("<value>" + p_requestMap.get("MSISDN").toString() + "</value>");
                    }
            } else {

                    if (p_requestMap.get("MSISDN").toString().length() == ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()) {
                       // body.append("<value>" + p_requestMap.get("MSISDN")  + "</value>");
                            body.append("<value>" + p_requestMap.get("MSISDN").toString() + "</value>");
                    } else {
                            body.append("<value>" + p_requestMap.get("MSISDN").toString().substring(2) + "</value>");
                    }

            }
                body.append("</subscriberId>");
                body.append("<subscriberExternalIdType changed=\"false\"");
                body.append("  set=\"true\"");
                body.append(">");
                body.append("<value>1</value>");
                body.append("</subscriberExternalIdType>");
                body.append("</subscriberId>");
				body.append("<balanceName>"+p_requestMap.get("BALANCE_NAME").toString()+"</balanceName>");
				// body.append("<balanceId>" + p_requestMap.get("TRANSACTION_ID").toString() + "</balanceId>");
               // body.append("<faceValue>"+"-" + p_requestMap.get("transfer_amount").toString() + "</faceValue>");
            //    body.append("<rechargeOffset>" + p_requestMap.get("VALIDITY_DAYS").toString() + "</rechargeOffset>" + lineSep);
               // body.append("<rechargeOffset></rechargeOffset>");
                body.append("<valueDelta>"+"-" + p_requestMap.get("transfer_amount").toString() + "</valueDelta>");
				body.append("<dateDelta></dateDelta>");
				body.append("<mtrComment>"+"WRR_"+(String)p_requestMap.get("SENDER_MSISDN")+"_"+(String)p_requestMap.get("REQ_OLD_ID")+"</mtrComment>");
                //body.append("<unitType>" + FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"SOAP_ONE_UNIT_TYPE_VALUE").toString() + "</unitType>");
               // body.append("<currencyIsoCode>" + FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"SOAP_ONE_CURRENCY_ISOCODE_VALUE").toString() + "</currencyIsoCode>");
               // body.append("<originatingApp>" + FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"SOAP_ONE_ORIGNATINGAPP_VALUE").toString() + "</originatingApp>");
                //body.append("<comments>" + p_requestMap.get("IN_TXN_ID").toString() + "_" + p_requestMap.get("TRANSACTION_ID").toString() + "_" + Long.parseLong(p_requestMap.get("REQUESTED_AMOUNT").toString()));
                //Change to send retailer msisdn in topup req. N = No; Y = Yes
       	    /*if(FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"IS_RETAILER_MSISDN_REQ_ComverseIN").equalsIgnoreCase("Y"))
       	    {
       	    	if(FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"RETAILERMSISDN_With91Prefix_ComverseIN").equalsIgnoreCase("Y"))
       			{
       				if(p_requestMap.get("MSISDN").toString().startsWith(FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"COUNTRY_CODE").toString()))
       				{
       					body.append("_"+p_requestMap.get("MSISDN").toString()+"</comments>");
       				}
       				else
       				{
       					body.append("_"+FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"COUNTRY_CODE").toString()+p_requestMap.get("MSISDN").toString()+"</comments>");
       				}
       			}
       			else if(FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"RETAILERMSISDN_With91Prefix_ComverseIN").equalsIgnoreCase("N") ) {
       				if(p_requestMap.get("MSISDN").toString().startsWith(FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"COUNTRY_CODE").toString()))
       				{
       					body.append("_"+p_requestMap.get("MSISDN").toString().substring(2)+"</comments>");
       				}

       				else
       				{
       					body.append("_"+p_requestMap.get("MSISDN").toString()+"</comments>");
       				}
       			}	
       	    	
       	    }
       	    else
       	    {
       	    body.append("</comments>");
       	    }*/
                //body.append("<currencyConversionRate>" + FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"SOAP_ONE_CURRENCYCONVERSION_RATE_VALUE").toString() + "</currencyConversionRate>");
               // body.append("<paymentProfileID>" + FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"SOAP_ONE_PAYMENT_PROFILE_ID_VALUE").toString() + "</paymentProfileID>");
			    //body.append("<currencyCode>" + FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"CURRENCY").toString() + "</currencyCode>");
                body.append("</com:input>");
                body.append("</com:SubscriberAdjustBalanceInstance>");
                body.append("</soapenv:Body>");
                stringBuffer.append(body.toString());
                stringBuffer.append("</soapenv:Envelope>");
                stringBuffer.trimToSize();
                requestStr = stringBuffer.toString();
                requestStr.trim();

        }

        catch(Exception e)
		{
			_log.error(methodName,"Exception e := "+e);
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()){
				_log.debug(methodName,"Exiting Request requestStr := "+requestStr);
			}
		}

        return requestStr;
}
	
    /** VFI: Birendra: 24April15 */
    /*private String envelopeTag()
	{
		String envelope="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+lineSep+
				"<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\""+lineSep+
				" xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/03/addressing\""+lineSep+
				" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\""+lineSep+
				" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\""+lineSep+
				" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"+lineSep;
		
		return envelope;
	}*/
    /** VFI: Birendra: 24April15 */
    
    private String envelopeTagOpen() {
       // String lineSep = System.getProperty("line.separator") + "\r";
        String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:com=\"http://www.comverse.com\">";
        return envelope;
    }
    
    private String envelopeTagClose() {
       // String lineSep = System.getProperty("line.separator") + "\r";
        String envelope = "</soapenv:Envelope>";
        return envelope;
    }
    
    /** VFI: Birendra: START: Commented */
    /*private String HeaderTag(String p_soapAction, HashMap p_requestMap) throws Exception
	{
    	if(_log.isDebugEnabled())_log.debug("HeaderTag","Entered p_requestMap::"+p_requestMap);
    	String strReq = null;
    	try{
			String userName = p_requestMap.get("COMV_INIT_ID").toString();
			String nonce = generateNonce();
			String timeStamp = getCurrentDateTimeZ();
			String initialPassword = p_requestMap.get("COMV_INIT_PASSWORD").toString();
	        String finalPassword = PasswordService.getInstance().encrypt(nonce,timeStamp,initialPassword);
			String messageid = ""+java.util.UUID.randomUUID();
			
			StringBuffer header = new StringBuffer();
			header.append("<soap:Header>"+lineSep);
	        header.append(" <wsa:Action>http://comverse-in.com/prepaid/ccws/"+p_soapAction+"</wsa:Action>"+lineSep);        
	        header.append(" <wsa:MessageID>uuid:"+messageid+"</wsa:MessageID>"+lineSep);
	        header.append(" <wsa:ReplyTo>"+lineSep);
	        header.append("     <wsa:Address>http://schemas.xmlsoap.org/ws/2004/03/addressing/role/anonymous</wsa:Address>"+lineSep);
	        header.append(" </wsa:ReplyTo>"+lineSep);
			header.append(" <wsa:To>"+ p_requestMap.get("COMV_SOAP_URL").toString() +"</wsa:To>"+lineSep);
			header.append(" <wsse:Security soap:mustUnderstand=\"1\">"+lineSep);
	        header.append("  <wsu:Timestamp wsu:Id=\"Timestamp-"+java.util.UUID.randomUUID()+"\">"+lineSep);
	        header.append("  	<wsu:Created>"+timeStamp+"</wsu:Created>"+lineSep);
	        header.append("  </wsu:Timestamp>"+lineSep);
	        header.append("  <wsse:UsernameToken"+lineSep);
	        header.append("		wsu:Id=\"SecurityToken-"+java.util.UUID.randomUUID()+"\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">"+lineSep);
	        header.append("		<wsse:Username>"+userName+"</wsse:Username>"+lineSep);
	        header.append("		<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest\">"+finalPassword+"</wsse:Password>"+lineSep);
	        header.append("		<wsse:Nonce>"+nonce+"</wsse:Nonce>"+lineSep);
	        header.append("		<wsu:Created>"+timeStamp+"</wsu:Created>"+lineSep);
	        header.append("	 </wsse:UsernameToken>"+lineSep);
	        header.append("	</wsse:Security>"+lineSep);
	        header.append("</soap:Header>"+lineSep);
	        strReq = header.toString().trim();
    	}
    	catch(Exception e)
		{
			_log.error("HeaderTag","Exception e::"+e.getMessage());
			throw e;
		}
    	if(_log.isDebugEnabled())_log.debug("HeaderTag","Exiting  requestStr::"+strReq);
        return strReq;
    }*/
    /** VFI: Birendra: STOP */
    
    
    private String headerTagOne() {

       // String lineSep = System.getProperty("line.separator") + "\r";
        StringBuffer header = new StringBuffer();
        header.append("<soapenv:Header/>");
        header.trimToSize();
        String strReq = header.toString();
        strReq.trim();
        return strReq;
}
    
    
    /*private String getCurrentDateTimeZ()
	{
	    SimpleDateFormat cformatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",Locale.ENGLISH);
		TimeZone tz1 = TimeZone.getTimeZone("UTC");
		cformatter.setTimeZone(tz1);
		Date d=new Date();
		long time=d.getTime();
		time += 2117000;
		d.setTime(time);
		String currentdateTime=cformatter.format(d);
		return currentdateTime;		
	}*/
	
	public String getBase64EncodingValue(String str){
		String encodedStr=null;
		try{
			byte [] brr=str.getBytes();
			System.out.println(new String(brr));
			Base64 bs=new Base64();
			byte [] bsrr=bs.encode(brr);
			encodedStr=new String(bsrr);
			System.out.println(encodedStr);			
			}
			catch(Exception e){e.printStackTrace();}
			return encodedStr;
	}
	
    public String generateNonce()
	{
	   return generateNonce(128);
	}
	 
	public String generateNonce(int length)
	{
	    SecureRandom random = null;
	    
	    try
	    {
	      random = SecureRandom.getInstance("SHA1PRNG");
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    
	    byte nonceValue[] = new byte[length / 8];
	    random.nextBytes(nonceValue);
	    
	    Base64 bs=new Base64();
		byte [] bsrr=bs.encode(nonceValue);
	
	    return new String(bsrr);
	  }
	
      public String generateUuid()
	  {
      	SecureRandom random = null;
	    try
	    {
	      random = SecureRandom.getInstance("SHA1PRNG");
	    }
	    catch (NoSuchAlgorithmException e)
	    {
	      e.printStackTrace();
	    }
	    
	    byte nonceValue[] = new byte[32];
	    random.nextBytes(nonceValue);

	    Base64 bs=new Base64();
		byte [] bsrr=bs.encode(nonceValue);
		
	    return new String(bsrr);
	  }
      
      /** VFI: Birendra: START: Commented & Setters,Getters */
      
      /*private String generateLanguageCodeRequest(HashMap p_requestMap) throws Exception
      {
  		String requestStr= null;
  		StringBuffer stringBuffer = null;
  		try
  		{
  			stringBuffer = new StringBuffer();
  			stringBuffer.append(EnvelopeTag());
  			stringBuffer.append( HeaderTag(_soapAction,p_requestMap));
  	        
  			StringBuffer body = new StringBuffer();            
            body.append("<soap:Body>"+lineSep);
              
  			body.append("<RetrieveSubscriberWithIdentityNoHistory xmlns=\"http://comverse-in.com/prepaid/ccws\">"+lineSep);  
  			body.append("<subscriberID>"+ p_requestMap.get("MSISDN").toString() +"</subscriberID>"+lineSep);
  			body.append("<identity/>"+lineSep);
  			body.append("<informationToRetrieve>1</informationToRetrieve>"+lineSep);
  		    body.append("</RetrieveSubscriberWithIdentityNoHistory>"+lineSep);
  		    body.append("</soap:Body>"+lineSep);
  			
            stringBuffer.append(body.toString());
            stringBuffer.append("</soap:Envelope>");
  	        requestStr = stringBuffer.toString().trim();
  		}
  		catch(Exception e)
  		{
  			throw e;
  		}
  		finally
  		{
  		}
          return requestStr;
      }*/
      
	public String getInterfaceID() {
		return _interfaceID;
	}

	public void setInterfaceID(String _interfaceid) {
		_interfaceID = _interfaceid;
	}
	/** VFI: Birendra: END */
}
