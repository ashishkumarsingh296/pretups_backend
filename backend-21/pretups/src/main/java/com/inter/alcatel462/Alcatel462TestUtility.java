package com.inter.alcatel462;

/**
 * @(#)Alcatel462TestUtility.java
 *                                Copyright(c) 2011, COMVIVA TECHNOLOGIES
 *                                LIMITED. All rights reserved.
 *                                COMVIVA PROPRIETARY/CONFIDENTIAL. Use is
 *                                subject to license terms.
 *                                All Rights Reserved
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Vinay Kumar Singh January 25, 2011 Initial
 *                                Creation
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                ----
 *                                This class can be used to simulate the PreTUPS
 *                                system..
 */
import java.util.HashMap;

public class Alcatel462TestUtility {
    private String _validate = "0";
    private String _credit = "1";
    private String _creditAdjust = "2";
    private String _debitAdjust = "3";
    private String _action = null;
    private String _interfaceID = null;
    private String _cardGroupSelector = null;

    public Alcatel462TestUtility() {

    }

    public Alcatel462TestUtility(String p_action, String p_interfaceID, String p_cardGroupSelector) {
        this._action = p_action;
        this._interfaceID = p_interfaceID;
        this._cardGroupSelector = p_cardGroupSelector;
    }

    public void executeINMethods(String p_action) {
        try {
            this._action = p_action;
            HashMap<String, String> _requestMap = null;
            Alcatel462INHandler inHandler = null;
            _requestMap = new HashMap<String, String>();
            if (_action.equals(_validate)) {
                _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", "NA:450");
                _requestMap.put("INT_ST_TYPE", "A");
                _requestMap.put("INTERFACE_HANDLER", "com.btsl.pretups.inter.AlcatelOCI452.AlcatelOCI452INHandler");
                _requestMap.put("INT_MOD_CLASSNAME", "com.btsl.pretups.inter.module.InterfaceModule");
                _requestMap.put("AccessType", "1");
                _requestMap.put("BalanceId", "");
                _requestMap.put("INT_MOD_IP", "null");
                _requestMap.put("CANCEL_NA", "NA");
                _requestMap.put("IN_TXN_ID", "07071312472900277");
                _requestMap.put("NETWORK_CODE", "ML");
                _requestMap.put("Stage", "VAL");
                _requestMap.put("USER_TYPE", "R");
                _requestMap.put("INT_MOD_PORT", "0");
                _requestMap.put("MODULE", "C2S");
                _requestMap.put("INTERFACE_ID", (_interfaceID == null ? "INTID00099" : _interfaceID));
                _requestMap.put("INT_MOD_COMM_TYPE", "SINGLE_JVM");
                _requestMap.put("MSISDN", "4600003");
                _requestMap.put("TRANSACTION_ID", "R080801.1247.10001");
                _requestMap.put("REQ_SERVICE", "RC");
                _requestMap.put("URL_ID", "1");
                _requestMap.put("INTERFACE_ACTION", "V");
                _requestMap.put("CANCEL_TXN_ALLOWED", "N");
                _requestMap.put("CARD_GROUP_SELECTOR", _cardGroupSelector);
                _requestMap.put("action", "90");
            } else if (_action.equals(_credit)) {
                _requestMap.put("SENDER_ID", "MOD0000092205");
                _requestMap.put("SERVICE_CLASS", "1");
                _requestMap.put("SOURCE_TYPE", "WEB");
                _requestMap.put("GRACE_DAYS", "10");
                _requestMap.put("INT_ST_TYPE", "A");
                _requestMap.put("INTERFACE_PREV_BALANCE", "5000");
                _requestMap.put("GRACE_DAYS", "10");
                _requestMap.put("VALIDITY_DAYS", "30");
                _requestMap.put("OLD_EXPIRY_DATE", "30/11/08");
                _requestMap.put("INTERFACE_HANDLER", "com.btsl.pretups.inter.AlcatelOCI452.AlcatelOCI452INHandler");
                _requestMap.put("INT_MOD_CLASSNAME", "com.btsl.pretups.inter.module.InterfaceModule");
                _requestMap.put("INT_MOD_IP", "null");
                _requestMap.put("INTERFACE_AMOUNT", "10");
                _requestMap.put("BONUS_AMOUNT", "2");
                _requestMap.put("NETWORK_CODE", "ML");
                _requestMap.put("SENDER_EXTERNAL_CODE", "test1");
                _requestMap.put("PRODUCT_CODE", "ETOPUP");
                _requestMap.put("USER_TYPE", "R");
                _requestMap.put("INT_MOD_PORT", "0");
                _requestMap.put("MODULE", "C2S");
                _requestMap.put("INTERFACE_ID", (_interfaceID == null ? "INTID00099" : _interfaceID));
                _requestMap.put("REQUESTED_AMOUNT", "10");
                _requestMap.put("INT_MOD_COMM_TYPE", "SINGLE_JVM");
                _requestMap.put("MSISDN", "4600003");
                _requestMap.put("TRANSACTION_ID", "R080801.1419.10001");
                _requestMap.put("BONUS_VALIDITY_DAYS", "0");
                _requestMap.put("REQ_SERVICE", "RC");
                _requestMap.put("CARD_GROUP", "10000010");
                _requestMap.put("SENDER_MSISDN", "8600002");
                _requestMap.put("INTERFACE_ACTION", "C");
                _requestMap.put("BUNDLE_VALIDITIES", "20,null");
                _requestMap.put("CARD_GROUP_SELECTOR", _cardGroupSelector);
                _requestMap.put("action", "1");
                _requestMap.put("BONUS1", "5");
                _requestMap.put("BONUS2", "0");
            } else if (_action.equals(_creditAdjust)) {
                _requestMap.put("MIN_CARD_GROUP_AMT", "1000");
                _requestMap.put("SENDER_ID", "MOD0000092205");
                _requestMap.put("SERVICE_CLASS", "1");
                _requestMap.put("SOURCE_TYPE", "WEB");
                _requestMap.put("INT_ST_TYPE", "A");
                _requestMap.put("INTERFACE_PREV_BALANCE", "5000");
                _requestMap.put("INTERFACE_HANDLER", "com.btsl.pretups.inter.AlcatelOCI452.AlcatelOCI452INHandler");
                _requestMap.put("INT_MOD_CLASSNAME", "com.btsl.pretups.inter.module.InterfaceModule");
                _requestMap.put("INT_MOD_IP", "null");
                _requestMap.put("INTERFACE_AMOUNT", "10");
                _requestMap.put("NETWORK_CODE", "ML");
                _requestMap.put("SENDER_EXTERNAL_CODE", "test1");
                _requestMap.put("PRODUCT_CODE", "ETOPUP");
                _requestMap.put("USER_TYPE", "R");
                _requestMap.put("INT_MOD_PORT", "0");
                _requestMap.put("MODULE", "C2S");
                _requestMap.put("INTERFACE_ID", (_interfaceID == null ? "INTID00099" : _interfaceID));
                _requestMap.put("VALIDITY_DAYS", "30");
                _requestMap.put("REQUESTED_AMOUNT", "10");
                _requestMap.put("INT_MOD_COMM_TYPE", "SINGLE_JVM");
                _requestMap.put("MSISDN", "4600003");
                _requestMap.put("TRANSACTION_ID", "R080801.1419.10001");
                _requestMap.put("BONUS_AMOUNT", "2");
                _requestMap.put("BONUS_VALIDITY_DAYS", "0");
                _requestMap.put("REQ_SERVICE", "RC");
                _requestMap.put("CARD_GROUP", "10000010");
                _requestMap.put("CARD_GROUP_SELECTOR", _cardGroupSelector);
                _requestMap.put("SENDER_MSISDN", "8600002");
                _requestMap.put("INTERFACE_ACTION", "C");
                _requestMap.put("BUNDLE_VALIDITIES", "20,null");
                _requestMap.put("ADJUST", "Y");
                _requestMap.put("action", "1");
                _requestMap.put("BONUS1", "5");
                _requestMap.put("BONUS2", "0");
            } else if (_action.equals(_debitAdjust)) {
                _requestMap.put("MIN_CARD_GROUP_AMT", "1000");
                _requestMap.put("SENDER_ID", "MOD0000092205");
                _requestMap.put("SERVICE_CLASS", "1");
                _requestMap.put("SOURCE_TYPE", "WEB");
                _requestMap.put("INT_ST_TYPE", "A");
                _requestMap.put("INTERFACE_PREV_BALANCE", "5000");
                _requestMap.put("INTERFACE_HANDLER", "com.btsl.pretups.inter.AlcatelOCI452.AlcatelOCI452INHandler");
                _requestMap.put("INT_MOD_CLASSNAME", "com.btsl.pretups.inter.module.InterfaceModule");
                _requestMap.put("INT_MOD_IP", "null");
                _requestMap.put("INTERFACE_AMOUNT", "10");
                _requestMap.put("NETWORK_CODE", "ML");
                _requestMap.put("SENDER_EXTERNAL_CODE", "test1");
                _requestMap.put("PRODUCT_CODE", "ETOPUP");
                _requestMap.put("USER_TYPE", "S");
                _requestMap.put("INT_MOD_PORT", "0");
                _requestMap.put("MODULE", "C2S");
                _requestMap.put("INTERFACE_ID", (_interfaceID == null ? "INTID00099" : _interfaceID));
                _requestMap.put("REQUESTED_AMOUNT", "10");
                _requestMap.put("INT_MOD_COMM_TYPE", "SINGLE_JVM");
                _requestMap.put("MSISDN", "4600003");
                _requestMap.put("TRANSACTION_ID", "R080801.1419.10001");
                _requestMap.put("REQ_SERVICE", "RC");
                _requestMap.put("CARD_GROUP", "10000010");
                _requestMap.put("CARD_GROUP_SELECTOR", _cardGroupSelector);
                _requestMap.put("SENDER_MSISDN", "8600002");
                _requestMap.put("INTERFACE_ACTION", "D");
                _requestMap.put("ADJUST", "Y");
                _requestMap.put("action", "0");
                _requestMap.put("BONUS1", "5");
                _requestMap.put("BONUS2", "0");
            }
            inHandler = new Alcatel462INHandler();
            if (_credit.equals(_action))
                inHandler.credit(_requestMap);
            else if (_creditAdjust.equals(_action))
                inHandler.creditAdjust(_requestMap);
            else if (_debitAdjust.equals(_action))
                inHandler.debitAdjust(_requestMap);
            else if (_validate.equals(_action))
                inHandler.validate(_requestMap);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception e:" + e.getMessage());
        }
    }
}
