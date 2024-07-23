package com.inter.vodaidea.vodafone.comverse;

import java.util.HashMap;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class ComverseResponseParser {

	private static Log _log = LogFactory.getLog(ComverseResponseParser.class.getName());
	
	/**
     * This method is used to parse the response.
     * @param	int p_action
     * @param	String p_responseStr
     * @return	HashMap
     * @throws	Exception
     */
    public HashMap parseResponse(int p_action,String p_responseStr) throws Exception
    {
    	final String methodName = "parseResponse";
	    
    	if(_log.isDebugEnabled()){
	    	_log.debug(methodName,"Entered p_action:= "+p_action+", p_responseStr:= "+p_responseStr);
	    }
    	
		HashMap map=null;
		try
		{
			switch(p_action)
			{
				case ComverseI.ACTION_ACCOUNT_DETAILS: 
				{
					map=parseGetAccountInfoResponse(p_responseStr);
					break;	
				}
				case ComverseI.ACTION_RECHARGE_CREDIT: 
				{
					map = parseRechargeCreditResponse(p_responseStr);
					break;	
				}
				case ComverseI.ACTION_IMMEDIATE_DEBIT:
				{
					map = parseImmediateDebitResponseOne(p_responseStr);
					break;	
				}
				case ComverseI.ACTION_IMMEDIATE_CREDIT:
				{
					map = parseRechargeCreditResponse(p_responseStr);
					break;	
				}
				case ComverseI.ACTION_LANGUAGE_CODE:
				{
					map=parseLanguageCodeResponse(p_responseStr);
					break;	
				}
			}
		}
		catch(Exception e)
		{
			_log.error(methodName,"Exception e:= "+e.getMessage());
			throw e;
		}
		finally
		{
			if(_log.isDebugEnabled()){
				_log.debug(methodName,"Exiting map:= "+map);
			}
		}
		return map;	
	}
    
    
    /**
     * @author birendra.mishra
     * @param p_responseStr
     * @return
     */
    private HashMap parseImmediateDebitResponseOne(String p_responseStr) {
    	final String methodName = "parseImmediateDebitResponseOne";

    	if(_log.isDebugEnabled()){
    		_log.debug(methodName,"Entered...");
    	}
    	
        HashMap<String,String> responseMap = new HashMap<String, String>();
        String reponse = "";
        String ErrorCode = "";
        String errorCode="errorCode";
        String origFaceValue="origFaceValue";
        String INResPonse=p_responseStr.toString();

        try {
            int index6 = INResPonse.indexOf("<errorCode");
            int index7 = INResPonse.indexOf("<origFaceValue");
            int bignningerorindex = INResPonse.indexOf("<messageCode>");
            int endingerrorindex = INResPonse.indexOf("</messageCode>");
            int lengtherror = "<messageCode>".length();

            if (bignningerorindex != -1) {
				String errorCodeString = INResPonse.substring(bignningerorindex + lengtherror, endingerrorindex);
				responseMap.put("ErrorCode", errorCodeString);
				_log.debug(methodName, "Error MessageCode  from IN is :" + errorCodeString + "Error Code is: " + ErrorCode);
            }
            else
            {
            	responseMap.put("ErrorCode", "0");
            	if(index6!=-1){
            		try {
                        String str = INResPonse.substring(INResPonse.indexOf("<"+errorCode), INResPonse.indexOf("</"+errorCode+">"));
                        str = str.substring(str.indexOf("<value>")+"<value>".length(), str.indexOf("</value>"));
                        responseMap.put(errorCode, str);
                    }
                    catch(Exception e){
                    	_log.debug(methodName,"Invalid XML due to problem in tag : "+errorCode+" Exception is: "+e.getMessage());
                    	throw new Exception("Invalid XML due to problem in tag : "+errorCode);
                    }
            	}
            	else{
            		_log.debug(methodName,"Error code tag not found in TOPUP IN response ");
            		throw new Exception("Invalid XML due to problem in tag:Error code tag not found in TOPUP IN response "+errorCode);
            	}
            	if(index7!=-1){
                    try{
	                    String str = INResPonse.substring(INResPonse.indexOf("<"+origFaceValue), INResPonse.indexOf("</"+origFaceValue+">"));
	                    str = str.substring(str.indexOf("<value>")+"<value>".length(), str.indexOf("</value>"));
	                    responseMap.put(origFaceValue, str);
                    }
                    catch(Exception e){
                    	_log.debug(methodName,"Invalid XML due to problem in tag: "+origFaceValue+" Exception is: "+e.getMessage());
                        throw new Exception("Invalid XML due to problem in tag: "+origFaceValue);
                    }
            	}
            	else{
            		_log.debug(methodName,"origFaceValue  tag not found in TOPUP IN response ");
            		throw new Exception("Invalid XML due to problem in tag:origFaceValue  tag not found in TOPUP IN response "+errorCode);
            	}
            }
        } catch (Exception ex) {
                _log.error(methodName, "Exception during parsing the Response from IN Exception is :" + ex.getMessage());
        }
        return responseMap;
    }


	/**
	 * @author birendra.mishra
	 * @param p_responseStr
	 * @return
	 */
	private HashMap parseRechargeCreditResponse(String p_responseStr) {
    	final String methodName = "parseRechargeCreditResponseOne";

    	if(_log.isDebugEnabled()){
    		_log.debug(methodName,"Entered...");
    	}
    	
        HashMap<String,String> responseMap = new HashMap<String, String>();
        String reponse = "";
        String ErrorCode = "";
        String errorCode="errorCode";
        String origFaceValue="origFaceValue";
        String INResPonse=p_responseStr.toString();

        try {
            int index6 = INResPonse.indexOf("<errorCode");
            int index7 = INResPonse.indexOf("<origFaceValue");
            int bignningerorindex = INResPonse.indexOf("<messageCode>");
            int endingerrorindex = INResPonse.indexOf("</messageCode>");
            int lengtherror = "<messageCode>".length();

            if (bignningerorindex != -1) {
				String errorCodeString = INResPonse.substring(bignningerorindex + lengtherror, endingerrorindex);
				responseMap.put("ErrorCode", errorCodeString);
				_log.debug(methodName, "Error MessageCode  from IN is :" + errorCodeString + "Error Code is: " + ErrorCode);
            }
            else
            {
            	responseMap.put("ErrorCode", "0");
            	if(index6!=-1){
            		try {
                        String str = INResPonse.substring(INResPonse.indexOf("<"+errorCode), INResPonse.indexOf("</"+errorCode+">"));
                        str = str.substring(str.indexOf("<value>")+"<value>".length(), str.indexOf("</value>"));
                        responseMap.put(errorCode, str);
                    }
                    catch(Exception e){
                    	_log.debug(methodName,"Invalid XML due to problem in tag : "+errorCode+" Exception is: "+e.getMessage());
                    	throw new Exception("Invalid XML due to problem in tag : "+errorCode);
                    }
            	}
            	else{
            		_log.debug(methodName,"Error code tag not found in TOPUP IN response ");
            		throw new Exception("Invalid XML due to problem in tag:Error code tag not found in TOPUP IN response "+errorCode);
            	}
            	if(index7!=-1){
                    try{
	                    String str = INResPonse.substring(INResPonse.indexOf("<"+origFaceValue), INResPonse.indexOf("</"+origFaceValue+">"));
	                    str = str.substring(str.indexOf("<value>")+"<value>".length(), str.indexOf("</value>"));
	                    responseMap.put(origFaceValue, str);
                    }
                    catch(Exception e){
                    	_log.debug(methodName,"Invalid XML due to problem in tag: "+origFaceValue+" Exception is: "+e.getMessage());
                        throw new Exception("Invalid XML due to problem in tag: "+origFaceValue);
                    }
            	}
            	else{
            		_log.debug(methodName,"origFaceValue  tag not found in TOPUP IN response ");
            		throw new Exception("Invalid XML due to problem in tag:origFaceValue  tag not found in TOPUP IN response "+errorCode);
            	}
            }
        } catch (Exception ex) {
                _log.error(methodName, "Exception during parsing the Response from IN Exception is :" + ex.getMessage());
        }
        return responseMap;
    }


	private HashMap parseGetAccountInfoResponse(String p_responseStr) {

		final String methodName = "parseGetAccountInfoResponseOne";
    	if(_log.isDebugEnabled()){
    		_log.debug(methodName,"Entered...");
    	}

        HashMap<String,String> responseMap = new HashMap<String, String>();
        String reponse = "";
        String ErrorCode = "";
        String serviceclass="primaryOfferId";
        String subscriberstate="ratingState";
        String availableBalance="availableBalance";
        String dateEnterActive="dateEnterActive";
        String acctExpireDate="acctExpireDate";
        String INResPonse=p_responseStr.toString();
        
        try {
			int index1 = INResPonse.indexOf("<primaryOfferId");
			int index2 = INResPonse.indexOf("<ratingState");
			int index3 = INResPonse.indexOf("<availableBalance");
			int index4 = INResPonse.indexOf("<dateEnterActive");
			int index5 = INResPonse.indexOf("<acctExpireDate");

            _log.debug(" index1", index1);
            System.out.println(" index2"+index2);
            System.out.println(" index3"+index3);
            System.out.println(" index4"+index4);
            System.out.println(" index5"+index5);

            int bignningerorindex = INResPonse.indexOf("<messageCode>");
            int endingerrorindex = INResPonse.indexOf("</messageCode>");
            int lengtherror = "<messageCode>".length();

            if (bignningerorindex != -1) {
				String errorCodeString = INResPonse.substring(bignningerorindex + lengtherror, endingerrorindex);
				responseMap.put("ErrorCode", errorCodeString);
				_log.debug(methodName,"Error MessageCode from IN is :" + errorCodeString + "Error Code is: " + ErrorCode);
            }
            else
            {
            	responseMap.put("ErrorCode","0");
				if(index1!=-1){
			        try{
		                String str = INResPonse.substring(INResPonse.indexOf("<"+serviceclass), INResPonse.indexOf("</"+serviceclass+">"));
		                str = str.substring(str.indexOf("<value>")+"<value>".length(), str.indexOf("</value>"));
		                responseMap.put("COS", str);
			        }
			        catch(Exception e){
		                _log.debug(methodName,"Invalid XML due to problem in tag: "+serviceclass+" Exception is: "+e.getMessage());
		                throw new Exception("Invalid XML due to problem in tag: "+serviceclass);
			        }
				}
				else{
				        _log.debug(methodName, serviceclass+" tag Not found in IN Response");
				        throw new Exception("Invalid XML due to problem in tag::Service Class Not found in IN Response "+serviceclass);
				}
                if(index2!=-1){
                    try{
                        String str = INResPonse.substring(INResPonse.indexOf("<"+subscriberstate), INResPonse.indexOf("</"+subscriberstate+">"));
                        str = str.substring(str.indexOf("<value>")+"<value>".length(), str.indexOf("</value>"));
                        responseMap.put("State", str);
                    }
                    catch(Exception e){
                    	_log.debug(methodName,"Invalid XML due to problem in tag: "+subscriberstate+" Exception is: "+e.getMessage());
                    	throw new Exception("Invalid XML due to problem in tag: "+subscriberstate);
                    }
                }
                else{
                	_log.debug(methodName,subscriberstate+" tag Not found in IN Response");
                	throw new Exception("Invalid XML due to problem in tag::Subscriber rating state tag Not found in IN Response"+subscriberstate);
                }
				if(index3!=-1){
			        try{
		                String str = INResPonse.substring(INResPonse.indexOf("<"+availableBalance), INResPonse.indexOf("</"+availableBalance+">"));
		                str = str.substring(str.indexOf("<value>")+"<value>".length(), str.indexOf("</value>"));
		                responseMap.put("Balance", str);
			        }
			        catch(Exception e){
			        	_log.debug(methodName,"Invalid XML due to problem in tag: "+availableBalance+" Exception is: "+e.getMessage());
		                throw new Exception("Invalid XML due to problem in tag: "+availableBalance);
			        }
				}
				else{
					_log.debug(methodName,availableBalance+" availableBalance tag Not found in IN Response");
					throw new Exception("Invalid XML due to problem in tag::Subscriber availableBalance  tag Not found in IN Response"+availableBalance);
				}

                if(index4!=-1){
                    try{
                        System.out.println("Date of EnterActive "+INResPonse.indexOf("</dateEnterActive>"));
                        if(INResPonse.indexOf("</dateEnterActive>") != -1){
                            String str = INResPonse.substring(INResPonse.indexOf("<"+dateEnterActive), INResPonse.indexOf("</"+dateEnterActive+">"));
                            str = str.substring(str.indexOf("<value>")+"<value>".length(), str.indexOf("</value>"));
                            responseMap.put(dateEnterActive, str);
                        }
                    }
                    catch(Exception e){
                    	_log.debug(methodName,"Invalid XML due to problem in tag : "+dateEnterActive+" Exception is: "+e.getMessage());
                    	throw new Exception("Invalid XML due to problem in tag: "+dateEnterActive);
                    }
                }
                else{
                	_log.debug(methodName,dateEnterActive+" tag Not found in IN VAL Response");
                	throw new Exception("Invalid XML due to problem in tag::dateEnterActive  tag Not found in  IN VAL Response"+dateEnterActive);
                }
                if(index5!=-1){
                    try{
                        System.out.println("Date of acctExpireDate "+INResPonse.indexOf("</acctExpireDate>"));
                        if(INResPonse.indexOf("</acctExpireDate>") != -1){
                            String str = INResPonse.substring(INResPonse.indexOf("<"+acctExpireDate), INResPonse.indexOf("</"+acctExpireDate+">"));
                            str = str.substring(str.indexOf("<value>")+"<value>".length(), str.indexOf("</value>"));
                            responseMap.put("Expire_Date", str);
                        }
                    }
                    catch(Exception e){
                    	_log.debug(methodName,"Invalid XML due to problem in tag: "+acctExpireDate+" Exception is: "+e.getMessage());
                        throw new Exception("Invalid XML due to problem in tag: "+acctExpireDate);
                    }
                }
                else{
                	_log.debug(methodName,acctExpireDate+" tag Not found in  IN VAL Response");
                	throw new Exception("Invalid XML due to problem in tag::acctExpireDate tag Not found in  IN VAL Response"+subscriberstate);
                }
            }
        } catch (Exception ex) {
        	_log.error("parseGetAccountInfoResponse","Exception e::"+ex.getMessage());
        }
        return responseMap;
	}


	/**
     * This method is used to parse the response of GetAccountinformation.
     * @param	String p_responseStr
     * @return	HashMap
     */
    /*private HashMap parseGetAccountInfoResponse(String p_responseStr) throws Exception
    {
        if(_log.isDebugEnabled()) _log.debug("parseGetAccountInfoResponse","Entered p_responseStr::"+p_responseStr);
        HashMap responseMap = null;
        try
        {
            responseMap = new HashMap();
            //responseMap = parsevalidatexml(new BufferedReader(new StringReader(p_responseStr)));
            int index =p_responseStr.indexOf("<Balance>");
            if(index>0)
            {
		        String balance=p_responseStr.substring(index+"<Balance>".length(),p_responseStr.indexOf("</Balance>",index));
		        responseMap.put("Balance",balance);
            }
            index =p_responseStr.indexOf("<Expire_Date>");
            if(index>0)
            {
		        String accExp=p_responseStr.substring(index+"<Expire_Date>".length(),p_responseStr.indexOf("</Expire_Date>",index));
		        responseMap.put("Expire_Date",accExp);
            }
            
            index =p_responseStr.indexOf("<COS>");
            if(index>0)
            {
		        String cos=p_responseStr.substring(index+"<COS>".length(),p_responseStr.indexOf("</COS>",index));
		        responseMap.put("COS",cos);
            }
            //Lohit for sos aon
            index =p_responseStr.indexOf("<"+PretupsI.AON_TAG+">");
            if(index>0)
            {
		        String aon=p_responseStr.substring(index+("<"+PretupsI.AON_TAG+">").length(),p_responseStr.indexOf(("</"+PretupsI.AON_TAG+">"),index));
		        responseMap.put("AON",new Date(aon));
            }
            index =p_responseStr.indexOf("<State>");
            if(index>0)
            {
		        String state=p_responseStr.substring(index+"<State>".length(),p_responseStr.indexOf("</State>",index));
		        responseMap.put("State",state);
            }
            index=p_responseStr.indexOf("<ErrorCode>");
            if(index >=0)
            {
                String errorCode=p_responseStr.substring(index+"<ErrorCode>".length(),p_responseStr.indexOf("</ErrorCode>",index));
		        responseMap.put("ErrorCode",errorCode);
            }
            else
                responseMap.put("ErrorCode","0"); 
        }
        catch(Exception e)
        {
            _log.error("parseGetAccountInfoResponse","Exception e::"+e.getMessage());
            throw e;
        }
        finally
        {
            if(_log.isDebugEnabled()) _log.debug("parseGetAccountInfoResponse","Exited responseMap::"+responseMap);
        }
		return responseMap;
    }*/
    /**
     * This method is used to parse the credit response. 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    /*public HashMap parseRechargeCreditResponse(String p_responseStr) throws Exception
    {
        if(_log.isDebugEnabled()) _log.debug("parseRechargeCreditResponse","Entered p_responseStr::"+p_responseStr);
        HashMap responseMap = null;
        try
        {
        	responseMap = new HashMap();
           // responseMap = parsevalidatexml(new BufferedReader(new StringReader(p_responseStr)));
            int index =p_responseStr.indexOf("<NonVoucherRechargeResult>");
            if(index>0)
            {
		        String resp=p_responseStr.substring(index+"<NonVoucherRechargeResult>".length(),p_responseStr.indexOf("</NonVoucherRechargeResult>",index));
		        responseMap.put("NonVoucherRechargeResult",resp);
            }
            index=p_responseStr.indexOf("<ErrorCode>");
            if(index>=0)
            {
                String errorCode=p_responseStr.substring(index+"<ErrorCode>".length(),p_responseStr.indexOf("</ErrorCode>",index));
		        responseMap.put("ErrorCode",errorCode);
            }
            else
                responseMap.put("ErrorCode","0"); 
        	
         }
        catch(Exception e)
        {
            _log.error("parseGetAccountInfoResponse","Exception e::"+e.getMessage());
            throw e;
        }
        finally
        {
            if(_log.isDebugEnabled()) _log.debug("parseRechargeCreditResponse","Exited responseMap::"+responseMap);
        }
		return responseMap;
    }*/

    /**
     * This method is used to parse the credit response. 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    private HashMap parseImmediateDebitResponse(String p_responseStr) throws Exception
    {
        if(_log.isDebugEnabled()) _log.debug("parseImmediateDebitResponse","Entered p_responseStr::"+p_responseStr);
        HashMap responseMap = null;
        try
        {
        	responseMap = new HashMap();
            //responseMap = parsevalidatexml(new BufferedReader(new StringReader(p_responseStr)));
        	int index =p_responseStr.indexOf("<NonVoucherRechargeResult>");
            if(index>0)
            {
		        String resp=p_responseStr.substring(index+"<NonVoucherRechargeResult>".length(),p_responseStr.indexOf("</NonVoucherRechargeResult>",index));
		        responseMap.put("NonVoucherRechargeResult",resp);
            }
            index=p_responseStr.indexOf("<ErrorCode>");
            if(index>=0)
            {
                String errorCode=p_responseStr.substring(index+"<ErrorCode>".length(),p_responseStr.indexOf("</ErrorCode>",index));
		        responseMap.put("ErrorCode",errorCode);
            }
            else
                responseMap.put("ErrorCode","0"); 
        }
        catch(Exception e)
        {
            _log.error("parseImmediateDebitResponse","Exception e::"+e.getMessage());
            throw e;
        }
        finally
        {
            if(_log.isDebugEnabled()) _log.debug("parseImmediateDebitResponse","Exited responseMap::"+responseMap);
        }
		return responseMap;
    }
    
    
    /*public HashMap<String,String> parsevalidatexml(BufferedReader InResp)
	{
    	if(_log.isDebugEnabled()) _log.debug("parsevalidatexml","Entered...");
		HashMap<String,String> ht = new HashMap<String,String>();
		XMLParser xmlParser = new XMLParser(InResp);
	
		if(xmlParser.isValidate){ 
		    String root=xmlParser.getRootElement();
			ArrayList<Node> arr=null;
			if(root!=null){
				arr=xmlParser.getAllChildElement(root);
			}
			 
			if(arr!=null){
				for(int i=0;i<arr.size();i++){
					 Node n=(Node)arr.get(i);
					 if(n.getNodeName().equals("<soap:Body>")){					 	
					 	ht=xmlParser.forDisp(n);					 	
					 }
				 }
			 }
		}
		System.out.println("parsevalidatexml exited with response map::"+ht);
		if(_log.isDebugEnabled()) _log.debug("parsevalidatexml","exited");
		return ht;
 	}	*/
    
    public HashMap parseLanguageCodeResponse(String p_responseStr) throws Exception
    {
        if(_log.isDebugEnabled()) _log.debug("parseLanguageCodeResponse","Entered p_responseStr::"+p_responseStr);
        HashMap responseMap = null;
        //String[] respBalances= null;
        try
        {
            responseMap = new HashMap();
            int index =p_responseStr.indexOf("<NotificationLanguage>");
            if(index>0)
            {
                String notificationLang=p_responseStr.substring(index+"<NotificationLanguage>".length(),p_responseStr.indexOf("</NotificationLanguage>",index));
    	        responseMap.put("NotificationLanguage",notificationLang);
            }
            index =p_responseStr.indexOf("<LanguageName>");
            if(index>0)
            {
		        String languageName=p_responseStr.substring(index+"<LanguageName>".length(),p_responseStr.indexOf("</LanguageName>",index));
		        responseMap.put("LanguageName",languageName);
            }
            
            //respBalances= p_responseStr.split("<Balance><Balance>");
            
            //index =p_responseStr.indexOf("<BalanceName>");
	        //String balancName=p_responseStr.substring(index+"<BalanceName>".length(),p_responseStr.indexOf("</BalanceName>",index));
	        //if("CORE".equalsIgnoreCase(balancName))  
	        //{
	            /*index =p_responseStr.indexOf("<Balance><Balance>");
	            if(index>0)
	            {
			        String balance=p_responseStr.substring(index+"<Balance><Balance>".length(),p_responseStr.indexOf("</Balance>",index));
			        responseMap.put("Balance",balance);
	            }
	            index =p_responseStr.indexOf("<AccountExpiration>");
	            if(index>0)
	            {
			        String accExp=p_responseStr.substring(index+"<AccountExpiration>".length(),p_responseStr.indexOf("</AccountExpiration>",index));
			        responseMap.put("AccountExpiration",accExp);
	            }*/
	            index =p_responseStr.indexOf("<CurrentState>");
	            if(index>0)
	            {
			        String state=p_responseStr.substring(index+"<CurrentState>".length(),p_responseStr.indexOf("</CurrentState>",index));
			        responseMap.put("State",state);
	            }
	            
	            index=p_responseStr.indexOf("<ErrorCode>");
	            if(index>=0)
	            {
	                String errorCode=p_responseStr.substring(index+"<ErrorCode>".length(),p_responseStr.indexOf("</ErrorCode>",index));
			        responseMap.put("ErrorCode",errorCode);
	            }
	            else
	                responseMap.put("ErrorCode","0"); 
	            //added for prepaid reversal by Vikas Singh
	            index =p_responseStr.indexOf("<AvailableBalance>");
	            if(index>0)
	            {
			        String balance=p_responseStr.substring(index+"<AvailableBalance>".length(),p_responseStr.indexOf("</AvailableBalance>",index));
			        responseMap.put("AvailableBalance",balance);
	            }
	            //end
        }
        catch(Exception e)
        {
            _log.error("parseLanguageCodeResponse","Exception e::"+e.getMessage());
            throw e;
        }
        finally
        {
            if(_log.isDebugEnabled()) _log.debug("parseLanguageCodeResponse","Exited responseMap::"+responseMap);
        }
		return responseMap;
    }
	
}
