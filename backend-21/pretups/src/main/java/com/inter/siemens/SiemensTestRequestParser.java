package com.inter.siemens;

import java.util.HashMap;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

/**
 * @(#)SiemensRequestParser.java
 *                               Copyright(c) 2006, Bharti Telesoft Int. Public
 *                               Ltd.
 *                               All Rights Reserved
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Ashish Kumar Jun 16,2006 Initial Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               --
 *                               This class implements the functionality to
 *                               parse the request.
 *                               These requests (getAccountInfo, credit,
 *                               debitAdjust) are parsed based on
 *                               the action.
 * 
 */
public class SiemensTestRequestParser {

    public Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * This method is used to parse the string(Account Information) and store
     * the elements and value into HashMap.
     * Format of getAccountInfo Request
     * 
     * ReqCred.Role=4&ReqCred.UserId=PreTUPS service
     * charger&ReqCred.PIN=&AccessFrontendId=PreTUPSClient1
     * &ConsumerAccountId=0&ConsumerPIN=&MerchantId=PreTUPS-1&ProductId=E-TOPUP&
     * Money.Currency=FCFA&Money.Amount=10000
     * &RoutingInfo=&AccountType=1&ClusterName=C1&ConsumerId=2211000771&
     * TransactionId=C060314.0436.0025S&
     * purpose=PT01;04/05/2006 12:23:33;&RequestType=chargeAmount1
     * 
     * @param String
     *            requestStr
     * @return HashMap
     * @throws Exception
     * 
     */
    public HashMap parseGetAccountInfoRequest(String p_requestStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoRequest", "Entered requestStr: " + p_requestStr);
        HashMap map = new HashMap();
        String[] purposeElementArray = null;// String array contains the element
                                            // of purpose
        try {
            map = BTSLUtil.getStringToHash(p_requestStr, "&", "=");
            String purpose = (String) map.get("purpose");
            purposeElementArray = purpose.split(";");
            map.put("purpose_action", purposeElementArray[0]);
            map.put("trans_date_time", purposeElementArray[1]);
        }// end of try-block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("parseGetAccountInfoRequest", "Exception e =" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoRequest", "Exiting map: " + map);
        }// end of finally
        return map;
    }// end of parseGetAccountInfoRequest

    /**
     * This method is used to parse the xml string(of RechargeCreditRequest) and
     * store the xml elements and value into HashMap
     * ReqCred.Role=4&ReqCred.UserId= PreTUPS service charger
     * &ReqCred.PIN=&AccessFrontendId=PreTUPSClient1
     * &ConsumerAccountId=0&ConsumerPIN=&MerchantId=PreTUPS-1&ProductId=E-TOPUP&
     * Money.Currency=FCFA&Money.Amount=10000
     * &RoutingInfo=&AccountType=1&ClusterName=C1&ConsumerId=2211000771&
     * TransactionId=C060314.0436.0025S
     * &Purpose=PT03;03/04/2006 06:00:00;500;600;10;15;1;
     * 
     * where purpose field contains following information with specified format
     * purpose=PT03;<TransDateTime>;
     * <Amount of Money to crediting the OnPeak account>;
     * <Amount of Money to crediting the SMS&Data account>;
     * <Activity period of OnPeak account>;
     * <Activity period SMS&Data account>;
     * <ETopupProfileName>;
     * 
     * @param String
     *            requestStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseRechargeCreditRequest(String p_requestStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditRequest", "Entered p_requestStr = " + p_requestStr);
        HashMap map = new HashMap();
        String[] purposeElementArray = null;// String array contains the element
                                            // of purpose
        try {
            map = BTSLUtil.getStringToHash(p_requestStr, "&", "=");
            String purpose = (String) map.get("purpose");
            purposeElementArray = purpose.split(";");
            map.put("purpose_action", purposeElementArray[0]);
            map.put("trans_date_time", purposeElementArray[1]);
            map.put("onpeak_amount", purposeElementArray[2]);
            map.put("sms_data_amount", purposeElementArray[3]);
            map.put("onpeak_end_activity", purposeElementArray[4]);
            map.put("sms_data_end_activity", purposeElementArray[5]);
        }// end of try block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("parseRechargeCreditRequest", "Exception  e =" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditRequest", "Exiting map: " + map);
        }// end of finally
        return map;
    }// end of parseRechargeCreditRequest

    /**
     * This method is used to parse the xml string(ImmediateDebitRequest)and
     * store the xml elements and value into HashMap
     * Debit request contains following element with specified format
     * ReqCred.Role=4&ReqCred.UserId= PreTUPS service charger
     * &ReqCred.PIN=&AccessFrontendId=PreTUPSClient1
     * &ConsumerAccountId=0&ConsumerPIN=&MerchantId=PreTUPS-1&ProductId=E-TOPUP&
     * Money.Currency=FCFA&Money.Amount=10000
     * &RoutingInfo=&AccountType=1&ClusterName=C1&ConsumerId=2211000771&
     * TransactionId=C060314.0436.0025S
     * &purpose=PT02;04/05/2006;100;0;0;0;Tax Fee=0;
     * 
     * purpose field contains the following information
     * purpose=PT02;<TransDateTime>;<Amount of Money to debiting the OnPeak
     * account>;
     * <Amount of Money to debiting the SMS&Data account>;<Amount of day to
     * reduce activity period of OnPeak account>;
     * <Amount of day to reduce activity period SMS&Data account>;
     * Tax Fee=<Amount of Tax Fee to debiting the Onpeak account>;
     * 
     * @param String
     *            requestStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseImmediateDebitRequest(String p_requestStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseImmediateDebitRequest", "Entered p_requestStr = " + p_requestStr);
        HashMap map = new HashMap();
        String[] purposeElementArray = null;// String array contains the element
                                            // of purpose
        try {
            map = BTSLUtil.getStringToHash(p_requestStr, "&", "=");
            String purpose = (String) map.get("purpose");
            purposeElementArray = purpose.split(";");
            map.put("purpose_action", purposeElementArray[0]);
            map.put("trans_date_time", purposeElementArray[1]);
            map.put("onpeak_amount", purposeElementArray[2]);
            map.put("sms_data_amount", purposeElementArray[3]);
            map.put("onpeak_end_activity", purposeElementArray[4]);
            map.put("sms_data_end_activity", purposeElementArray[5]);
            map.put("tax_fee", purposeElementArray[6]);
        }// end of try-block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("parseImmediateDebitRequest", "Exception  e =" + e.getMessage());
            throw e;
        }// end of catch-block
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseImmediateDebitRequest", "Exiting map: " + map);
        }// end of finally
        return map;
    }// end of parseImmediateDebitRequest
}
