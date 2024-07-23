package com.inter.roam;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;

/**
 * @(#)IATRequestFormatter
 *                         Copyright(c) 2009, Bharti Telesoft Int. Public Ltd.
 *                         All Rights Reserved
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         vikasy Feb 19,2009 Initial Creation
 *                         ----------------------------------------------------
 *                         --------------------------------------------
 *                         This class is used to format the request and response
 *                         based on the action.
 *                         REQUEST: XML request is generated from the hash map
 *                         based on key value pairs.
 *                         RESPONSE: From XML response elements values are
 *                         stored in HashMap.
 */
public class RoamRequestFormatter {
    public static Log _log = LogFactory.getLog(RoamRequestFormatter.class);
    private SimpleDateFormat _sdf = null;
    private TimeZone _timeZone = null;
    private String _transDateFormat = "yyyyMMdd'T'HH:mm:ss";// Defines the Date
                                                            // and time format
                                                            // of IATIN.
    private DecimalFormat _twoDigits = null;
    private int _offset;
    private String _sign = "+";
    private int _hours;
    private int _minutes;

    public RoamRequestFormatter() throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("RoamRequestFormatter[constructor]", "Entered");
        try {
            _sdf = new SimpleDateFormat(_transDateFormat);
            _timeZone = TimeZone.getDefault();
            _sdf.setTimeZone(_timeZone);
            _twoDigits = new DecimalFormat("00");
            _offset = _sdf.getTimeZone().getOffset(new Date().getTime());
            if (_offset < 0) {
                _offset = -_offset;
                _sign = "-";
            }
            _hours = _offset / 3600000;
            _minutes = (_offset - _hours * 3600000) / 60000;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("RoamRequestFormatter[constructor]", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("RoamRequestFormatter[constructor]", "Exited");
        }// end of finally
    }// end of RoamRequestFormatter[constructor]

    /**
     * /**
     * This method is used to parse the response string based on the type of
     * Action.
     * 
     * @param int p_action
     * @param HashMap
     *            p_map
     * @return String.
     * @throws Exception
     */
    protected String generateRequest(int p_action, HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Entered p_action::" + p_action + " map::" + p_map);
        String str = null;
        p_map.put("action", String.valueOf(p_action));
        try {
            switch (p_action) {
            case RoamI.ACTION_ACCOUNT_INFO: {
                str = generateGetAccountInfoRequest(p_map);
                break;
            }
            case RoamI.ACTION_CREDIT: {
                str = generateRechargeCreditRequest(p_map);
                break;
            }
            case RoamI.ACTION_DEBIT: {
                str = generateImmediateDebitRequest(p_map);
                break;
            }

            }// end of switch block
        }// end of try block
        catch (Exception e) {
            _log.error("generateRequest", "Exception e ::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRequest", "Exited Request String: str::" + str);
        }// end of finally
        return str;
    }// end of generateRequest

    /**
     * This method is used to parse the response.
     * 
     * @param int p_action
     * @param String
     *            p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseResponse(int p_action, String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Entered p_action::" + p_action + " p_responseStr:: " + p_responseStr);
        HashMap map = null;
        try {
            switch (p_action) {
            case RoamI.ACTION_ACCOUNT_INFO: {
                map = parseGetAccountInfoResponse(p_responseStr);
                break;
            }
            case RoamI.ACTION_CREDIT: {
                map = parseRechargeCreditResponse(p_responseStr);
                break;
            }
            case RoamI.ACTION_DEBIT: {
                map = parseImmediateDebitResponse(p_responseStr);
                break;
            }

            }// end of switch block
        }// end of try block
        catch (Exception e) {
            _log.error("parseResponse", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponse", "Exiting map::" + map);
        }// end of finally
        return map;
    }// end of parseResponse

    /**
     * This method is used to generate the request for getting account
     * information.
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String generateGetAccountInfoRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetAccountInfoRequest", "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;
        return requestStr;
    }// end of generateGetAccountInfoRequest

    /**
     * 
     * @param p_map
     * @return
     * @throws Exception
     */
    private String generateRechargeCreditRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRechargeCreditRequest", "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;
        StringBuffer stringBuffer = null;
        String inTransactionID = null;
        try {
            System.out.println();
            stringBuffer = new StringBuffer(1028);
            stringBuffer.append("<?xml version=\"1.0\"?>");
            stringBuffer.append("<COMMAND>");
            stringBuffer.append("<TYPE>EXTROAMRCREQ</TYPE>");// hard coded
            stringBuffer.append("<DATE>" + BTSLUtil.getDateTimeStringFromDate(new Date(), PretupsI.TIMESTAMP_DATESPACEHHMMSS) + "</DATE>");
            stringBuffer.append("<EXTNWCODE>" + (String) p_requestMap.get("EXTNWCODE") + "</EXTNWCODE>");// will
                                                                                                         // be
                                                                                                         // same
                                                                                                         // as
                                                                                                         // receiver
                                                                                                         // network
                                                                                                         // code
            stringBuffer.append("<MSISDN>" + BTSLUtil.NullToString((String) p_requestMap.get("DUMMYMSISDN") + "</MSISDN>"));// dummy
                                                                                                                            // msisdn
            stringBuffer.append("<PIN>" + BTSLUtil.NullToString((String) p_requestMap.get("PIN") + "</PIN>"));// dummy
                                                                                                              // PIN
            stringBuffer.append("<LOGINID>" + BTSLUtil.NullToString((String) p_requestMap.get("LOGINID")) + "</LOGINID>");// dummy
                                                                                                                          // login
                                                                                                                          // ID
            stringBuffer.append("<PASSWORD>" + BTSLUtil.NullToString((String) p_requestMap.get("PASSWORD")) + "</PASSWORD>");// dummy
                                                                                                                             // password
            stringBuffer.append("<EXTCODE>" + BTSLUtil.NullToString((String) p_requestMap.get("EXTCODE")) + "</EXTCODE>");// dummy
                                                                                                                          // password
            stringBuffer.append("<EXTREFNUM>" + BTSLUtil.NullToString((String) p_requestMap.get("TRANSACTION_ID")) + "</EXTREFNUM>");// dummy
                                                                                                                                     // password
            stringBuffer.append("<MSISDN2>" + (String) p_requestMap.get("MSISDN") + "</MSISDN2>");
            stringBuffer.append("<RECHAMT>" + (String) p_requestMap.get("transfer_amount") + "</RECHAMT>");
            stringBuffer.append("<MULTFACTOR>" + (String) p_requestMap.get("MULTFACTOR") + "</MULTFACTOR>");
            stringBuffer.append("<SELECTOR>" + (String) p_requestMap.get("CARD_GROUP_SELECTOR") + "</SELECTOR>");
            stringBuffer.append("<LANGUAGE>" + (String) p_requestMap.get("LANGUAGE") + "</LANGUAGE>");
            stringBuffer.append("<VALIDATIONDONE>" + (String) p_requestMap.get("VALIDATIONDONE") + "</VALIDATIONDONE>");

            if ("Y".equalsIgnoreCase((String) p_requestMap.get("VALIDATIONDONE"))) {
                stringBuffer.append("<INPARAMS>");
                stringBuffer.append("<ACCOUNTSTATUS>" + (String) p_requestMap.get("ACCOUNTSTATUS") + "</ACCOUNTSTATUS>");
                stringBuffer.append("<SERVICECLASS>" + (String) p_requestMap.get("SERVICECLASS") + "</SERVICECLASS>");
                stringBuffer.append("<VALIDITYDATE>" + (String) p_requestMap.get("VALIDITYDATE") + "</VALIDITYDATE>");
                stringBuffer.append("<GRACEDATE>" + (String) p_requestMap.get("GRACEDATE") + "</GRACEDATE>");
                stringBuffer.append("<BALANCE>" + (String) p_requestMap.get("BALANCE") + "</BALANCE>");
                stringBuffer.append("<LANGUAGE>" + (String) p_requestMap.get("LANGUAGE") + "</LANGUAGE>");
                stringBuffer.append("<NOTIFICATIONNUMBER>" + (String) p_requestMap.get("NOTIFICATIONNUMBER") + "</NOTIFICATIONNUMBER>");
                stringBuffer.append("</INPARAMS>");
            }

            stringBuffer.append("<OTHERINFO>");
            stringBuffer.append("<INITMSISDN>" + BTSLUtil.NullToString((String) p_requestMap.get("SENDER_MSISDN")) + "</INITMSISDN>");
            stringBuffer.append("<RECHCURR>" + BTSLUtil.NullToString((String) p_requestMap.get("RECHCURR")) + "</RECHCURR>");
            stringBuffer.append("<REQAMT>" + BTSLUtil.NullToString((String) p_requestMap.get("transfer_amount")) + "</REQAMT>");
            stringBuffer.append("<CURRCONVFACT>" + BTSLUtil.NullToString((String) p_requestMap.get("CURRCONVFACT")) + "</CURRCONVFACT>");
            stringBuffer.append("</OTHERINFO>");
            stringBuffer.append("</COMMAND>");
            requestStr = stringBuffer.toString();
        }// end of try-block
        catch (Exception e) {
            _log.error("generateRechargeCreditRequest", "Exception e: " + e);
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRechargeCreditRequest", "Exiting Request requestStr::" + requestStr);
        }// end of finally
        return requestStr;
    }// end of generateRechargeCreditRequest

    /**
     * 
     * @param map
     * @return
     * @throws Exception
     */
    private String generateImmediateDebitRequest(HashMap p_requestMap) throws Exception {
    	if(_log.isDebugEnabled())_log.debug("generateImmediateDebitRequest","Entered p_requestMap::"+p_requestMap);
		String requestStr= null;
		StringBuffer stringBuffer = null;
		try
		{
			System.out.println();
			stringBuffer = new StringBuffer(1028);
	        stringBuffer.append("<?xml version=\"1.0\"?>");
	        stringBuffer.append("<COMMAND>");
	        stringBuffer.append("<TYPE>EXTROAMRCREVREQ</TYPE>");//hard coded
	        stringBuffer.append("<EXTNWCODE>"+(String)p_requestMap.get("EXTNWCODE")+"</EXTNWCODE>");
	        stringBuffer.append("<DATE>"+BTSLUtil.getDateTimeStringFromDate(new Date(),PretupsI.TIMESTAMP_DATESPACEHHMMSS)+"</DATE>");
	        stringBuffer.append("<MSISDN>"+BTSLUtil.NullToString((String)p_requestMap.get("DUMMYMSISDN")+"</MSISDN>"));//dummy msisdn
	        stringBuffer.append("<PIN>"+BTSLUtil.NullToString((String)p_requestMap.get("PIN")+"</PIN>"));//dummy PIN
	        stringBuffer.append("<LOGINID>"+BTSLUtil.NullToString((String)p_requestMap.get("LOGINID"))+"</LOGINID>");//dummy login ID
	        stringBuffer.append("<PASSWORD>"+BTSLUtil.NullToString((String)p_requestMap.get("PASSWORD"))+"</PASSWORD>");//dummy password
	        stringBuffer.append("<EXTCODE>"+BTSLUtil.NullToString((String)p_requestMap.get("EXTCODE"))+"</EXTCODE>");
	        stringBuffer.append("<EXTREFNUM>"+BTSLUtil.NullToString((String)p_requestMap.get("TRANSACTION_ID"))+"</EXTREFNUM>");  //dummy transaction id
	        stringBuffer.append("<MSISDN2>"+BTSLUtil.NullToString((String)p_requestMap.get("MSISDN"))+"</MSISDN2>");
	        stringBuffer.append("<TXNID>"+BTSLUtil.NullToString((String)p_requestMap.get("DUMMY_TRANSACTION_ID"))+"</TXNID>");       //dummy transaction id
	        stringBuffer.append("<LANGUAGE1>"+BTSLUtil.NullToString((String)p_requestMap.get("LANGUAGE"))+"</LANGUAGE1>");
	        stringBuffer.append("<LANGUAGE2>"+BTSLUtil.NullToString((String)p_requestMap.get("LANGUAGE"))+"</LANGUAGE2>");
	        stringBuffer.append("</COMMAND>");
	        requestStr = stringBuffer.toString();
		}catch(Exception e)
		{
			_log.error("generateImmediateDebitRequest","Exception e: "+e);
			throw e;
		}//end of catch-Exception
		finally
		{
			if(_log.isDebugEnabled())_log.debug("generateImmediateDebitRequest","Exiting Request requestStr::"+requestStr);
		}//end of finally
        return requestStr;
    }// end of generateImmediateDebitRequest

    /**
     * This method is used to parse the response of GetAccountinformation.
     * 
     * @param String
     *            p_responseStr
     * @return HashMap
     */
    private HashMap parseGetAccountInfoResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        return responseMap;
    }// end of parseGetAccountInfoResponse

    /**
     * This method is used to parse the credit response.
     * 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseRechargeCreditResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        int indexStart = 0;
        int indexEnd = 0;
        int tempIndex = 0;
        String responseCode = null;
        try {
            System.out.println();
            responseMap = new HashMap();
            indexStart = p_responseStr.indexOf("<TXNSTATUS>");
            String status = p_responseStr.substring(indexStart + "<TXNSTATUS>".length(), p_responseStr.indexOf("</TXNSTATUS>", indexStart));
            responseMap.put("status", status);
            if ("200".equals(status.trim())) {
                indexStart = p_responseStr.indexOf("<DATE>");
                String date = p_responseStr.substring(indexStart + "<DATE>".length(), p_responseStr.indexOf("</DATE>", indexStart));
                responseMap.put("date", date);

                indexStart = p_responseStr.indexOf("<EXTREFNUM>");
                String extRefNumber = p_responseStr.substring(indexStart + "<EXTREFNUM>".length(), p_responseStr.indexOf("</EXTREFNUM>", indexStart));
                responseMap.put("extRefNumber", extRefNumber);

                indexStart = p_responseStr.indexOf("<TXNID>");
                String txnID = p_responseStr.substring(indexStart + "<TXNID>".length(), p_responseStr.indexOf("</TXNID>", indexStart));
                responseMap.put("txnID", txnID);

                indexStart = p_responseStr.indexOf("<TOPUPAMOUNT>");
                if (indexStart != -1) {
                    String topupAmt = p_responseStr.substring(indexStart + "<TOPUPAMOUNT>".length(), p_responseStr.indexOf("</TOPUPAMOUNT>", indexStart));
                    responseMap.put("topupAmt", topupAmt);
                }

                indexStart = p_responseStr.indexOf("<CURRENCY>");
                if (indexStart != -1) {
                    String currency = p_responseStr.substring(indexStart + "<CURRENCY>".length(), p_responseStr.indexOf("</CURRENCY>", indexStart));
                    responseMap.put("currency", currency);
                }

                indexStart = p_responseStr.indexOf("<BALANCE>");
                if (indexStart != -1) {
                    String balance = p_responseStr.substring(indexStart + "<BALANCE>".length(), p_responseStr.indexOf("</BALANCE>", indexStart));
                    responseMap.put("balance", balance);
                }

                indexStart = p_responseStr.indexOf("<VALIDITYDATE>");
                if (indexStart != -1) {
                    String valDate = p_responseStr.substring(indexStart + "<VALIDITYDATE>".length(), p_responseStr.indexOf("</VALIDITYDATE>", indexStart));
                    responseMap.put("valDate", valDate);
                }

                indexStart = p_responseStr.indexOf("<GRACEDATE>");
                if (indexStart != -1) {
                    String graceDate = p_responseStr.substring(indexStart + "<GRACEDATE>".length(), p_responseStr.indexOf("</GRACEDATE>", indexStart));
                    responseMap.put("graceDate", graceDate);
                }

                indexStart = p_responseStr.indexOf("<MESSAGE>");
                String message = p_responseStr.substring(indexStart + "<MESSAGE>".length(), p_responseStr.indexOf("</MESSAGE>", indexStart));
                responseMap.put("message", message);

            } else {
                indexStart = p_responseStr.indexOf("<MESSAGE>");
                String message = p_responseStr.substring(indexStart + "<MESSAGE>".length(), p_responseStr.indexOf("</MESSAGE>", indexStart));
                responseMap.put("message", message);
            }
            return responseMap;

        } catch (Exception e) {
            _log.error("parseGetAccountInfoResponse", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponse", "Exited responseMap::" + responseMap);
        }// end of finally

    }// end of parseRechargeCreditResponse

    /**
     * This method is used to parse the credit response.
     * 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    private HashMap parseImmediateDebitResponse(String p_responseStr) throws Exception {
    	 if(_log.isDebugEnabled()) _log.debug("parseImmediateDebitResponse","Entered p_responseStr::"+p_responseStr);
         HashMap responseMap = null;
         int indexStart=0;
         int indexEnd=0;
         int tempIndex=0;
         String responseCode = null;
         try
         {
         	System.out.println();
             responseMap = new HashMap();
 	        indexStart = p_responseStr.indexOf("<TXNSTATUS>");
 	        String status=p_responseStr.substring(indexStart+"<TXNSTATUS>".length(),p_responseStr.indexOf("</TXNSTATUS>",indexStart));
 	        responseMap.put("status",status);
 	        if("200".equals(status.trim()))
 	        {
 	 	       
 	 	       indexStart = p_responseStr.indexOf("<DATE>");
 		        String date=p_responseStr.substring(indexStart+"<DATE>".length(),p_responseStr.indexOf("</DATE>",indexStart));
 		        responseMap.put("date",date);
 		        
 		       indexStart = p_responseStr.indexOf("<EXTREFNUM>");
		        if(indexStart != -1)
		        {
		        	String extRefNum=p_responseStr.substring(indexStart+"<EXTREFNUM>".length(),p_responseStr.indexOf("</EXTREFNUM>",indexStart));
		        	responseMap.put("extRefNum",extRefNum);
		        }
		        
		        
 		       indexStart = p_responseStr.indexOf("<TXNID>");
		        if(indexStart != -1)
		        {
		        	String txnID=p_responseStr.substring(indexStart+"<TXNID>".length(),p_responseStr.indexOf("</TXNID>",indexStart));
		        	responseMap.put("txnid",txnID);
		        }
		        
		        indexStart = p_responseStr.indexOf("<MESSAGE>");
		        if(indexStart != -1)
		        {
                String message=p_responseStr.substring(indexStart+"<MESSAGE>".length(),p_responseStr.indexOf("</MESSAGE>",indexStart));
                  responseMap.put("message",message);
		        }
          
 	           
 	        }
 	        else
            {
                     indexStart = p_responseStr.indexOf("<MESSAGE>");
                   String message=p_responseStr.substring(indexStart+"<MESSAGE>".length(),p_responseStr.indexOf("</MESSAGE>",indexStart));
                     responseMap.put("message",message);
             }
 	        return responseMap;
 	      
         }
         catch(Exception e)
         {
             _log.error("parseImmediateDebitResponse","Exception e::"+e.getMessage());
             throw e;
         }//end of catch-Exception
         finally
         {
             if(_log.isDebugEnabled()) _log.debug("parseImmediateDebitResponse","Exited responseMap::"+responseMap);
         }
    }// end of parseImmediateDebitResponse

    /**
     * Method to get the Transaction date and time with specified format.
     * 
     * @return String
     * @throws Exception
     */
    private String getTransDateTime() throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getTransDateTime", "Entered");
        String transDateTime = null;
        try {
            Date now = new Date();
            transDateTime = _sdf.format(now) + _sign + _twoDigits.format(_hours) + _twoDigits.format(_minutes);
        }// end of try block
        catch (Exception e) {
            _log.error("getTransDateTime", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getTransDateTime", "Exited transDateTime: " + transDateTime);
        }// end of finally
        return transDateTime;
    }// end of getTransDateTime

    public static void main(String[] args) throws Exception {

        HashMap requestMap = new HashMap();
        // String
        // responseStr="<?xml version=\"1.0\" encoding=\"utf-8\"?><methodResponse><params><param><value><struct><member><name>accountFlags</name><value><string>10000000</string></value></member><member><name>accountValue1</name><value><string>3340</string></value></member><member><name>creditClearanceDate</name><value><dateTime.iso8601>20080503T00:00:00+0000</dateTime.iso8601></value></member><member><name>currency1</name><value><string>JOD</string></value></member><member><name>currentLanguageID</name><value><i4>2</i4></value></member><member><name>responseCode</name><value><i4>0</i4></value></member><member><name>serviceClassCurrent</name><value><i4>105</i4></value></member><member><name>serviceFeeDate</name><value><dateTime.iso8601>20080403T00:00:00+0000</dateTime.iso8601></value></member><member><name>serviceRemovalDate</name><value><dateTime.iso8601>20080503T00:00:00+0000</dateTime.iso8601></value></member><member><name>supervisionDate</name><value><dateTime.iso8601>20080203T00:00:00+0000</dateTime.iso8601></value></member></struct></value></param></params></methodResponse>";
        String responseStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?><methodResponse><params><param><value><struct><member><name>accountValueAfter1</name><value><string>3395</string></value></member><member><name>currency1</name><value><string>JOD</string></value></member><member><name>dedicatedAccountInformation</name><value><array><data><value><struct><member><name>dedicatedAccountID</name><value><i4>3</i4></value></member><member><name>rechargeAmount1DedicatedTotal</name><value><string>3</string></value></member></struct></value></data></array></value></member><member><name>rechargeAmount1MainTotal</name><value><string>20</string></value></member><member><name>responseCode</name><value><i4>0</i4></value></member><member><name>serviceClassCurrent</name><value><i4>105</i4></value></member><member><name>serviceFeeDateAfter</name><value><dateTime.iso8601>20080403T00:00:00+0000</dateTime.iso8601></value></member><member><name>supervisionDateAfter</name><value><dateTime.iso8601>20080203T00:00:00+0000</dateTime.iso8601></value></member></struct></value></param></params></methodResponse>";

        RoamRequestFormatter testCS3 = new RoamRequestFormatter();
        requestMap = testCS3.parseRechargeCreditResponse(responseStr);
        System.out.println("Response Map:->" + requestMap);
        /*
         * requestMap.put("origin_node_type","ETUPS");
         * requestMap.put("origin_host_name","DNS");
         * requestMap.put("PRETUPS_ID_AS_ORGN_TXN_ID","N");
         * requestMap.put("message_capability_flag","00000000");
         * requestMap.put("MSISDN","777898894");
         * requestMap.put("transfer_amount","20");
         * requestMap.put("transaction_currency","JOD");
         * requestMap.put("ExternalData1","ZEBRA");
         * requestMap.put("ExternalData2","R070807.1230.0032.0776222242");
         * requestMap.put("INSTANCE_ID","2");
         * requestMap.put("CARD_GROUP","Z0");
         */
        /*
         * requestMap.put("","");
         * requestMap.put("","");
         * requestMap.put("","");
         * requestMap.put("","");
         * requestMap.put("","");
         * requestMap.put("","");
         * requestMap.put("","");
         * requestMap.put("","");
         * requestMap.put("","");
         */

        // System.out.println(" RequestStr: "+testCS3.generateGetAccountInfoRequest(requestMap));
        // System.out.println(" RequestStr: "+testCS3.generateRechargeCreditRequest(requestMap));
        // Prepare the requestMap.

        /*
         * StringBuffer response= new StringBuffer(
         * "<?xml version=\"1.0\" encoding=\"utf-8\"?><methodResponse><params><param><value><struct>"
         * );
         * response.append(
         * "<member><name>accountFlags</name><value><string>10001111</string></value></member>"
         * );
         * response.append(
         * "<member><name>accountValue1</name><value><string>1 </string></value></member>"
         * );
         * response.append(
         * "<member><name>creditClearanceDate</name><value><dateTime.iso8601>20051015T00:00:00+0000</dateTime.iso8601></value></member>"
         * );
         * response.append(
         * "<member><name>currency1</name><value><string>BDT</string></value></member>"
         * );
         * response.append(
         * "<member><name>currentLanguageID</name><value><i4>1</i4></value></member>"
         * );
         * response.append(
         * "<member><name>firstIVRCallFlag</name><value><string>1</string></value></member>"
         * );
         * response.append(
         * "<member><name>responseCode</name><value><i4>10</i4></value></member>"
         * );
         * response.append(
         * "<member><name>serviceClassCurrent</name><value><i4>9999</i4></value></member>"
         * );
         * response.append(
         * "<member><name>serviceFeeDate</name><value><dateTime.iso8601>20050930T00:00:00+0000</dateTime.iso8601></value></member>"
         * );
         * response.append(
         * "<member><name>serviceRemovalDate</name><value><dateTime.iso8601>20051015T00:00:00+0000</dateTime.iso8601></value></member>"
         * );
         * response.append(
         * "<member><name>supervisionDate</name><value><dateTime.iso8601>20050930T00:00:00+0000</dateTime.iso8601></value></member>"
         * );
         * response.append("</struct></value></param></params></methodResponse>")
         * ;
         * String respStr = response.toString();
         */

    }

}
