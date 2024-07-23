package com.inter.bank;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtilI;

public class BankINRequestFormatter {
    public static Log _log = LogFactory.getLog("BankINRequestFormatter".getClass().getName());
    // private static long _prevReqTime=0;
    // private static int _transactionIDCounter=0;
    public static OperatorUtilI _operatorUtil = null;
    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SPrepaidController[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /*
     * public static synchronized String getINTransactionID()
     * {
     * String transferID=null;
     * try
     * {
     * long currentReqTime= System.currentTimeMillis();
     * if(currentReqTime-_prevReqTime>=(60000))
     * _transactionIDCounter=1;
     * else
     * _transactionIDCounter=_transactionIDCounter+1;
     * _prevReqTime=currentReqTime;
     * if(_transactionIDCounter==0)
     * throw new
     * BTSLBaseException("BankINRequestFormatter","getINTransactionID",
     * PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
     * //transferID=_operatorUtil.formatC2STransferID(p_transferVO,
     * _transactionIDCounter);
     * if(transferID==null)
     * throw new
     * BTSLBaseException("BankINRequestFormatter","getINTransactionID",
     * PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
     * 
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * }
     * return transferID;
     * }
     */

    /**
     * Based on the action value, a method is referenced to generate the xml
     * string for corresponding request
     * 
     * @param int p_action
     * @param HashMap
     *            p_map
     * @return String str
     */
    protected String generateRequest(int p_action, HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Entered p_action:" + p_action + " p_map: " + p_map);
        String str = null;
        try {
            p_map.put("action", String.valueOf(p_action));
            switch (p_action) {
            case BankINI.ACTION_ACCOUNT_INFO: {
                str = generateGetAccountInfoRequest(p_map);
                break;
            }
            case BankINI.ACTION_RECHARGE_CREDIT: {
                str = generateRechargeCreditRequest(p_map);
                break;
            }
            case BankINI.ACTION_IMMEDIATE_DEBIT: {
                str = generateImmediateDebitRequest(p_map);
                break;
            }
            }
        } catch (Exception e) {
            _log.error("generateRequest", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRequest", "Exited str:" + str);
        }
        return str;
    }

    /**
     * Based on the action value, a method is referenced to parse the xml string
     * for corresponding response.
     * 
     * @param int p_action
     * @param String
     *            p_responseStr
     * @return HashMap
     */

    protected HashMap parseResponse(int p_action, String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Entered p_action:" + p_action + " responseStr:  " + p_responseStr);
        HashMap map = null;
        try {
            switch (p_action) {
            case BankINI.ACTION_ACCOUNT_INFO: {
                map = parseGetAccountInfoResponse(p_responseStr);
                break;
            }
            case BankINI.ACTION_RECHARGE_CREDIT: {
                map = parseRechargeCreditResponse(p_responseStr);
                break;
            }
            case BankINI.ACTION_IMMEDIATE_DEBIT: {
                map = parseImmediateDebitResponse(p_responseStr);
                break;
            }
            }
        } catch (Exception e) {
            _log.error("parseResponse", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponse", "Exiting map: " + map);
        }
        return map;
    }

    /**
     * This Method generate account information request
     * 
     * @param map
     *            HashMap
     * @throws Exception
     * @return requestStr
     */
    private String generateGetAccountInfoRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetAccountInfoRequest", "Entered map=" + map);
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            return requestStr;
        } catch (Exception e) {
            _log.error("generateGetAccountInfoRequest", "Exception e: " + e);
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateGetAccountInfoRequest", "Exiting requestStr: " + requestStr);
        }
    }

    /**
     * This Method parse GetAccount Info Response
     * 
     * @param String
     *            p_responseStr
     * @throws Exception
     * @return HashMap
     */
    public HashMap parseGetAccountInfoResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse", "Entered p_responseStr: " + p_responseStr);
        HashMap map = null;
        try {
            return map;
        }// end of try-block
        catch (Exception e) {
            _log.error("parseGetAccountInfoResponse", "Exception e: " + e);
            throw e;
        }// end of catch -Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoResponse", "Exiting map: " + map);
        }// end of finally
    }

    /**
     * This method is responsible to generate the xml string for the credit
     * request.
     * 
     * @param HashMap
     *            map
     * @return String requestStr
     * @throws Exception
     */
    private String generateRechargeCreditRequest(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRechargeCreditRequest", "Entered p_map:" + p_map);
        String requestStr = null;
        StringBuffer sbf = null;
        try {
        } catch (Exception e) {
            _log.error("generateRechargeCreditRequest", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRechargeCreditRequest", "Exited requestStr: " + requestStr);
        }
        return requestStr;
    }

    /**
     * This method is responsible to parse the xml string for the credit
     * response.
     * 
     * @param responseStr
     *            String
     * @throws Exception
     * @return map
     */
    public HashMap parseRechargeCreditResponse(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditResponse", "Entered responseStr: " + responseStr);
        HashMap map = null;
        try {
            return map;
        } catch (Exception e) {
            _log.error("parseRechargeCreditResponse", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponse", "Exiting map: " + map);
        }
    }

    /**
     * This method is responsible to generate the xml string for the debit
     * request (Only amount is supported).
     * 
     * @param HashMap
     *            p_map
     * @throws Exception
     * @return String
     */
    private String generateImmediateDebitRequest(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateImmediateDebitRequest", "Entered p_map=" + p_map);
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);
            sbf.append("MSISDN=");
            sbf.append(p_map.get("MSISDN"));
            sbf.append("&PTRefId=");
            sbf.append(p_map.get("IN_TXN_ID"));
            sbf.append("&PTDateTime=");
            sbf.append(getCurrentDateTime());
            sbf.append("&TASOrigInstCode=");
            sbf.append(p_map.get("TAS_ORIGIN_ST_CODE"));
            sbf.append("&Amount=");
            sbf.append(p_map.get("transfer_amount"));
            sbf.append("&PIN=");
            sbf.append(p_map.get("BANK_PIN"));// check this PIN
            requestStr = sbf.toString();
        } catch (Exception e) {
            _log.error("generateImmediateDebitRequest", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateImmediateDebitRequest", "Exiting requestStr: " + requestStr);
        }
        return requestStr;
    }

    /**
     * This Method parse Immediate Debit Response
     * 
     * @param responseStr
     *            String
     * @throws Exception
     * @return map
     */
    public HashMap parseImmediateDebitResponse(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseImmediateDebitResponse", "Entered responseStr: " + responseStr);
        HashMap map = new HashMap();

        try {
            String[] resMessageArray = responseStr.split("&");
            for (int i = 0; i < resMessageArray.length; i++) {
                int index = resMessageArray[i].indexOf("STATUS");
                if (index != -1) {
                    String Status = resMessageArray[i].substring(index + "Status".length() + 1);
                    map.put("status", Status);
                }
                index = resMessageArray[i].indexOf("MSISDN");
                if (index != -1) {
                    String MSISDN = resMessageArray[i].substring(index + "MSISDN".length() + 1);
                    map.put("MSISDN", MSISDN);
                }
                index = resMessageArray[i].indexOf("PTRefId");
                if (index != -1) {
                    String PTRefId = resMessageArray[i].substring(index + "PTRefId".length() + 1);
                    map.put("PTRefId", PTRefId);
                }
                index = resMessageArray[i].indexOf("PTDateTime");
                if (index != -1) {
                    String PTDateTime = resMessageArray[i].substring(index + "PTDateTime".length() + 1);
                    map.put("PTDateTime", PTDateTime);
                }
                index = resMessageArray[i].indexOf("TASConfirmationId");
                if (index != -1) {
                    String TASConfirmationId = resMessageArray[i].substring(index + "TASConfirmationId".length() + 1);
                    map.put("TASConfirmationId", TASConfirmationId);
                }
            }// end of for loop
            return map;
        }// end of try block.
        catch (Exception e) {
            _log.error("parseImmediateDebitResponse", "Exception e: " + e.getMessage());
            throw e;
        }// end of Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseImmediateDebitResponse", "Exiting map: " + map);
        }
    }// end of parseImmediateDebitResponse

    public String getCurrentDateTime() {

        Date date = new Date();
        String format = "yyyyMMddHHmmss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.format(date);

    }// End of getCurrentDateTime

    /**
     * This Method parse Immediate Debit Response
     * 
     * @param requestStr
     *            String
     * @throws Exception
     * @return map
     */
    public HashMap parseImmediateDebitRequest(String requestStr) throws Exception {// MSISDN=919810012345&PTRefId=KA0007073178&PTDateTime=20070831235518&TASOrigInstCode=00001700053
                                                                                   // &Amount=000000005000&PIN=1234
        if (_log.isDebugEnabled())
            _log.debug("parseImmediateDebitResponse", "Entered responseStr: " + requestStr);
        HashMap map = new HashMap();
        try {
            String[] resMessageArray = requestStr.split("&");
            for (int i = 0; i < resMessageArray.length; i++) {
                int index = resMessageArray[i].indexOf("PIN");
                if (index != -1) {
                    String PIN = resMessageArray[i].substring(index + "PIN".length() + 1);
                    map.put("PIN", PIN);
                }
                index = resMessageArray[i].indexOf("Amount");
                if (index != -1) {
                    String Amount = resMessageArray[i].substring(index + "Amount".length() + 1);
                    map.put("Amount", Amount);
                }
                index = resMessageArray[i].indexOf("TASOrigInstCode");
                if (index != -1) {
                    String TASOrigInstCode = resMessageArray[i].substring(index + "TASOrigInstCode".length() + 1);
                    map.put("TASOrigInstCode", TASOrigInstCode);
                }
                index = resMessageArray[i].indexOf("PTDateTime");
                if (index != -1) {
                    String PTDateTime = resMessageArray[i].substring(index + "PTDateTime".length() + 1);
                    map.put("PTDateTime", PTDateTime);
                }
                index = resMessageArray[i].indexOf("PTRefId");
                if (index != -1) {
                    String PTRefId = resMessageArray[i].substring(index + "PTRefId".length() + 1);
                    map.put("PTRefId", PTRefId);
                }
                index = resMessageArray[i].indexOf("MSISDN");
                if (index != -1) {
                    String MSISDN = resMessageArray[i].substring(index + "MSISDN".length() + 1);
                    map.put("MSISDN", MSISDN);
                }
            }// end of for loop
            return map;
        }// end of try block.
        catch (Exception e) {
            _log.error("parseImmediateDebitResponse", "Exception e: " + e.getMessage());
            throw e;
        }// end of Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseImmediateDebitResponse", "Exiting map: " + map);
        }
    }// end of parseImmediateDebitResponse

    public static void main(String args[]) throws Exception {
        BankINRequestFormatter abc = new BankINRequestFormatter();
        System.out.println(abc.parseImmediateDebitResponse("TASConfirmationId=001376029678&MSISDN=919810012345&PTRefId=KA0007073178&PTDateTime=20070831235518&Status=200"));
    }

}
