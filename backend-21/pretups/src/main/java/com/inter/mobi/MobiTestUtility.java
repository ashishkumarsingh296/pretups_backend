package com.inter.mobi;

import java.util.HashMap;
import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.inter.module.HandlerCommonUtility;

/**
 * @author vinay.singh
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class MobiTestUtility {

    private String _validate = "0";
    private String _credit = "1";
    private String _creditAdjust = "2";
    private String _debitAdjust = "3";
    private String _singleStageCreditDebit = "4";
    private String _conversion = "5";
    private String _action = null;
    private static int _count250 = 0;
    private static int _count500 = 0;
    private static int _count200 = 0;
    private boolean _flag = false;
    private String _interfaceID = null;

    public MobiTestUtility() {

    }

    public MobiTestUtility(String p_interfaceID) {
        this._interfaceID = p_interfaceID;
    }

    public MobiTestUtility(String p_action, String threadCnt) {
        this._action = p_action;
        int count = Integer.parseInt(threadCnt);
        try {
            long startTime = System.currentTimeMillis();
            long totalTime = 0;

            for (int i = 0; i < count; i++) {
                new LoadTestHandler().start();
                Thread.sleep(20);
            }
            totalTime = System.currentTimeMillis() - startTime;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("CbossTestUtility Exception e:" + e.getMessage());
        }
    }// end of Constructor-LoadTest

    public void executeINMethods(String p_action, String srcMsisdn, String destMsisdn, String interfaceAmt, String currency) {
        try {
            this._action = p_action;
            HashMap _requestMap = null;
            MobiINHandler inhandler = null;
            _requestMap = new HashMap();
            if (_action.equals(_validate)) {
                _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", "NA:450");
                _requestMap.put("READ_TIMEOUT_VAL", "5000");
                _requestMap.put("INT_ST_TYPE", "A");
                _requestMap.put("INTERFACE_HANDLER", "com.btsl.pretups.inter.mobi.MobiINHandler");
                _requestMap.put("INT_MOD_CLASSNAME", "com.btsl.pretups.inter.module.InterfaceModule");
                _requestMap.put("AccessType", "1");
                _requestMap.put("BalanceId", "");
                _requestMap.put("INT_MOD_IP", "null");
                _requestMap.put("CANCEL_NA", "NA");
                _requestMap.put("IN_TXN_ID", "07071312472900277");
                _requestMap.put("NETWORK_CODE", "MO");
                _requestMap.put("SYSTEM_STATUS_MAPPING", "3100;250,30:206");
                _requestMap.put("Stage", "VAL");
                _requestMap.put("USER_TYPE", "R");
                _requestMap.put("INT_MOD_PORT", "0");
                _requestMap.put("MODULE", "C2S");
                _requestMap.put("INTERFACE_ID", (_interfaceID == null ? "INTID00001" : _interfaceID));
                _requestMap.put("INT_MOD_COMM_TYPE", "SINGLE_JVM");
                _requestMap.put("MSISDN", srcMsisdn);
                _requestMap.put("TRANSACTION_ID", "R070713.1247.10001");
                _requestMap.put("REQ_SERVICE", "RC");
                _requestMap.put("CARD_GROUP_SELECTOR", "1");
                _requestMap.put("URL_ID", "1");
                _requestMap.put("INTERFACE_ACTION", "V");
                _requestMap.put("CANCEL_TXN_ALLOWED", "N");
            } else if (_action.equals(_credit)) {
                _requestMap.put("SENDER_ID", "MOD0000092205");
                _requestMap.put("SERVICE_CLASS", "1");
                _requestMap.put("SOURCE_TYPE", "WEB");
                _requestMap.put("GRACE_DAYS", "10");
                _requestMap.put("INT_ST_TYPE", "A");
                _requestMap.put("INTERFACE_PREV_BALANCE", "5000");
                _requestMap.put("OLD_EXPIRY_DATE", "30/11/08");
                _requestMap.put("INTERFACE_HANDLER", "com.btsl.pretups.inter.mobi.MobiINHandler");
                _requestMap.put("INT_MOD_CLASSNAME", "com.btsl.pretups.inter.module.InterfaceModule");
                _requestMap.put("INT_MOD_IP", "null");
                _requestMap.put("INTERFACE_AMOUNT", interfaceAmt);
                _requestMap.put("BONUS_AMOUNT", "0");
                _requestMap.put("NETWORK_CODE", "MO");
                _requestMap.put("SENDER_EXTERNAL_CODE", "test1");
                _requestMap.put("PRODUCT_CODE", "ETOPUP");
                _requestMap.put("USER_TYPE", "R");
                _requestMap.put("INT_MOD_PORT", "0");
                _requestMap.put("MODULE", "C2S");
                _requestMap.put("INTERFACE_ID", (_interfaceID == null ? "INTID00001" : _interfaceID));
                _requestMap.put("VALIDITY_DAYS", "30");
                _requestMap.put("REQUESTED_AMOUNT", "1000");
                _requestMap.put("INT_MOD_COMM_TYPE", "SINGLE_JVM");
                _requestMap.put("MSISDN", srcMsisdn);
                _requestMap.put("TRANSACTION_ID", "R070714.1419.10001");
                _requestMap.put("BONUS_VALIDITY_DAYS", "0");
                _requestMap.put("REQ_SERVICE", "RC");
                _requestMap.put("CARD_GROUP", "10000010");
                _requestMap.put("CARD_GROUP_SELECTOR", "1");
                _requestMap.put("SENDER_MSISDN", destMsisdn);
                _requestMap.put("INTERFACE_ACTION", "C");
            } else if (_action.equals(_creditAdjust) || _action.equals(_debitAdjust)) {
                _requestMap.put("MIN_CARD_GROUP_AMT", "1000");
                _requestMap.put("SENDER_ID", "MOD0000092205");
                _requestMap.put("SERVICE_CLASS", "1");
                _requestMap.put("SOURCE_TYPE", "WEB");
                _requestMap.put("GRACE_DAYS", "10");
                _requestMap.put("INT_ST_TYPE", "A");
                _requestMap.put("INTERFACE_PREV_BALANCE", "5000");
                _requestMap.put("OLD_EXPIRY_DATE", "30/11/08");
                _requestMap.put("INTERFACE_HANDLER", "com.btsl.pretups.inter.mobi.MobiINHandler");
                _requestMap.put("INT_MOD_CLASSNAME", "com.btsl.pretups.inter.module.InterfaceModule");
                _requestMap.put("INT_MOD_IP", "null");
                _requestMap.put("INTERFACE_AMOUNT", interfaceAmt);
                _requestMap.put("BONUS_AMOUNT", "0");
                _requestMap.put("NETWORK_CODE", "MO");
                _requestMap.put("SENDER_EXTERNAL_CODE", "test1");
                _requestMap.put("PRODUCT_CODE", "ETOPUP");
                _requestMap.put("USER_TYPE", "R");
                _requestMap.put("INT_MOD_PORT", "0");
                _requestMap.put("MODULE", "C2S");
                _requestMap.put("INTERFACE_ID", (_interfaceID == null ? "INTID00001" : _interfaceID));
                _requestMap.put("VALIDITY_DAYS", "30");
                _requestMap.put("REQUESTED_AMOUNT", "1000");
                _requestMap.put("INT_MOD_COMM_TYPE", "SINGLE_JVM");
                _requestMap.put("MSISDN", srcMsisdn);
                _requestMap.put("RECEIVER_MSISDN", destMsisdn);
                _requestMap.put("TRANSACTION_ID", "R070714.1419.10001");
                _requestMap.put("BONUS_VALIDITY_DAYS", "0");
                _requestMap.put("REQ_SERVICE", "RC");
                _requestMap.put("CARD_GROUP", "10000010");
                _requestMap.put("CARD_GROUP_SELECTOR", "1");
                _requestMap.put("SENDER_MSISDN", srcMsisdn);
                _requestMap.put("INTERFACE_ACTION", "C");
                if (_action.equals(_debitAdjust))
                    _requestMap.put("INTERFACE_ACTION", "D");
                _requestMap.put("ADJUST", "Y");
            } else if (_action.equals(_singleStageCreditDebit)) {
                _requestMap.put("SENDER_INTERFACE_AMOUNT", interfaceAmt);
                _requestMap.put("RECEIVER_INTERFACE_AMOUNT", interfaceAmt);
                _requestMap.put("SERVICE_CLASS", "1");
                _requestMap.put("SOURCE_TYPE", "WEB");
                _requestMap.put("GRACE_DAYS", "10");
                _requestMap.put("INT_ST_TYPE", "A");
                _requestMap.put("INTERFACE_PREV_BALANCE", "5000");
                _requestMap.put("OLD_EXPIRY_DATE", "30/11/08");
                _requestMap.put("INTERFACE_HANDLER", "com.btsl.pretups.inter.mobi.MobiINHandler");
                _requestMap.put("INT_MOD_CLASSNAME", "com.btsl.pretups.inter.module.InterfaceModule");
                _requestMap.put("INT_MOD_IP", "null");
                _requestMap.put("INTERFACE_AMOUNT", interfaceAmt);
                _requestMap.put("BONUS_AMOUNT", "0");
                _requestMap.put("NETWORK_CODE", "MO");
                _requestMap.put("SENDER_EXTERNAL_CODE", "test1");
                _requestMap.put("PRODUCT_CODE", "ETOPUP");
                _requestMap.put("USER_TYPE", "R");
                _requestMap.put("INT_MOD_PORT", "0");
                _requestMap.put("MODULE", "C2S");
                _requestMap.put("INTERFACE_ID", (_interfaceID == null ? "INTID00001" : _interfaceID));
                _requestMap.put("VALIDITY_DAYS", "30");
                _requestMap.put("REQUESTED_AMOUNT", "1000");
                _requestMap.put("INT_MOD_COMM_TYPE", "SINGLE_JVM");
                _requestMap.put("MSISDN", srcMsisdn);
                _requestMap.put("RECEIVER_MSISDN", destMsisdn);
                _requestMap.put("TRANSACTION_ID", "R080714.1419.10001");
                _requestMap.put("BONUS_VALIDITY_DAYS", "0");
                _requestMap.put("REQ_SERVICE", "RC");
                _requestMap.put("CARD_GROUP", "10000010");
                _requestMap.put("CARD_GROUP_SELECTOR", "1");
                _requestMap.put("SENDER_MSISDN", srcMsisdn);
                _requestMap.put("INTERFACE_ACTION", "C");
                if (_action.equals(_debitAdjust))
                    _requestMap.put("INTERFACE_ACTION", "D");
                _requestMap.put("ADJUST", "Y");
                _requestMap.put("SINGLE_STEP_CREDIT_DEBIT", "Y");
            } else if (_action.equals(_conversion)) {
                // System.out.println(HandlerCommonUtility.getConversionRate(_interfaceID));
            }
            inhandler = new MobiINHandler();
            if (_credit.equals(_action))
                inhandler.credit(_requestMap);
            else if (_creditAdjust.equals(_action))
                inhandler.creditAdjust(_requestMap);
            else if (_debitAdjust.equals(_action))
                inhandler.debitAdjust(_requestMap);
            else if (_validate.equals(_action))
                inhandler.validate(_requestMap);
            else if (_singleStageCreditDebit.equals(_action))
                inhandler.creditAdjust(_requestMap);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception e:" + e.getMessage());
        }
    }

    public static void main(String[] args) {

    }

    class LoadTestHandler extends Thread {
        MobiINHandler inhandler = null;
        int _threadNo = 0;
        HashMap _requestMap = null;

        public void run() {
            try {
                inhandler = new MobiINHandler();
                _requestMap = new HashMap();
                if (_action.equals(_validate)) {
                    _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING", "NA:450");
                    _requestMap.put("READ_TIMEOUT_VAL", "5000");
                    _requestMap.put("INT_ST_TYPE", "A");
                    _requestMap.put("INTERFACE_HANDLER", "com.btsl.pretups.inter.mobi.CbossINHandler");
                    _requestMap.put("INT_MOD_CLASSNAME", "com.btsl.pretups.inter.module.InterfaceModule");
                    _requestMap.put("AccessType", "1");
                    _requestMap.put("BalanceId", "");
                    _requestMap.put("INT_MOD_IP", "null");
                    _requestMap.put("CANCEL_NA", "NA");
                    _requestMap.put("IN_TXN_ID", "07071312472900277");
                    _requestMap.put("NETWORK_CODE", "MO");
                    _requestMap.put("SYSTEM_STATUS_MAPPING", "3100;250,30:206");
                    _requestMap.put("Stage", "VAL");
                    _requestMap.put("USER_TYPE", "R");
                    _requestMap.put("INT_MOD_PORT", "0");
                    _requestMap.put("MODULE", "C2S");
                    _requestMap.put("INTERFACE_ID", (_interfaceID == null ? "INTID00001" : _interfaceID));
                    _requestMap.put("INT_MOD_COMM_TYPE", "SINGLE_JVM");
                    _requestMap.put("MSISDN", "9830012345");
                    _requestMap.put("TRANSACTION_ID", "R070713.1247.10001");
                    _requestMap.put("REQ_SERVICE", "RC");
                    _requestMap.put("CARD_GROUP_SELECTOR", "1");
                    _requestMap.put("URL_ID", "1");
                    _requestMap.put("INTERFACE_ACTION", "V");
                    _requestMap.put("CANCEL_TXN_ALLOWED", "N");
                } else if (_action.equals(_credit)) {
                    _requestMap.put("SENDER_ID", "MOD0000092205");
                    _requestMap.put("SERVICE_CLASS", "1");
                    _requestMap.put("SOURCE_TYPE", "WEB");
                    _requestMap.put("GRACE_DAYS", "10");
                    _requestMap.put("INT_ST_TYPE", "A");
                    _requestMap.put("INTERFACE_PREV_BALANCE", "5000");
                    _requestMap.put("OLD_EXPIRY_DATE", "30/11/08");
                    _requestMap.put("INTERFACE_HANDLER", "com.btsl.pretups.inter.mobi.CbossINHandler");
                    _requestMap.put("INT_MOD_CLASSNAME", "com.btsl.pretups.inter.module.InterfaceModule");
                    _requestMap.put("INT_MOD_IP", "null");
                    _requestMap.put("INTERFACE_AMOUNT", "1000");
                    _requestMap.put("BONUS_AMOUNT", "0");
                    _requestMap.put("NETWORK_CODE", "MO");
                    _requestMap.put("SENDER_EXTERNAL_CODE", "test1");
                    _requestMap.put("PRODUCT_CODE", "ETOPUP");
                    _requestMap.put("USER_TYPE", "R");
                    _requestMap.put("INT_MOD_PORT", "0");
                    _requestMap.put("MODULE", "C2S");
                    _requestMap.put("INTERFACE_ID", (_interfaceID == null ? "INTID00001" : _interfaceID));
                    _requestMap.put("VALIDITY_DAYS", "30");
                    _requestMap.put("REQUESTED_AMOUNT", "1000");
                    _requestMap.put("INT_MOD_COMM_TYPE", "SINGLE_JVM");
                    _requestMap.put("MSISDN", "9830090907");
                    _requestMap.put("TRANSACTION_ID", "R070714.1419.10001");
                    _requestMap.put("BONUS_VALIDITY_DAYS", "0");
                    _requestMap.put("REQ_SERVICE", "RC");
                    _requestMap.put("CARD_GROUP", "10000010");
                    _requestMap.put("CARD_GROUP_SELECTOR", "1");
                    _requestMap.put("SENDER_MSISDN", "9810025698");
                    _requestMap.put("INTERFACE_ACTION", "C");
                } else if (_action.equals(_creditAdjust) || _action.equals(_debitAdjust)) {
                    _requestMap.put("MIN_CARD_GROUP_AMT", "1000");
                    _requestMap.put("SENDER_ID", "MOD0000092205");
                    _requestMap.put("SERVICE_CLASS", "1");
                    _requestMap.put("SOURCE_TYPE", "WEB");
                    _requestMap.put("GRACE_DAYS", "10");
                    _requestMap.put("INT_ST_TYPE", "A");
                    _requestMap.put("INTERFACE_PREV_BALANCE", "5000");
                    _requestMap.put("OLD_EXPIRY_DATE", "30/11/08");
                    _requestMap.put("INTERFACE_HANDLER", "com.btsl.pretups.inter.mobi.CbossINHandler");
                    _requestMap.put("INT_MOD_CLASSNAME", "com.btsl.pretups.inter.module.InterfaceModule");
                    _requestMap.put("INT_MOD_IP", "null");
                    _requestMap.put("INTERFACE_AMOUNT", "1000");
                    _requestMap.put("BONUS_AMOUNT", "0");
                    _requestMap.put("NETWORK_CODE", "MO");
                    _requestMap.put("SENDER_EXTERNAL_CODE", "test1");
                    _requestMap.put("PRODUCT_CODE", "ETOPUP");
                    _requestMap.put("USER_TYPE", "R");
                    _requestMap.put("INT_MOD_PORT", "0");
                    _requestMap.put("MODULE", "C2S");
                    _requestMap.put("INTERFACE_ID", (_interfaceID == null ? "INTID00001" : _interfaceID));
                    _requestMap.put("VALIDITY_DAYS", "30");
                    _requestMap.put("REQUESTED_AMOUNT", "1000");
                    _requestMap.put("INT_MOD_COMM_TYPE", "SINGLE_JVM");
                    _requestMap.put("MSISDN", "9830090907");
                    _requestMap.put("TRANSACTION_ID", "R070714.1419.10001");
                    _requestMap.put("BONUS_VALIDITY_DAYS", "0");
                    _requestMap.put("REQ_SERVICE", "RC");
                    _requestMap.put("CARD_GROUP", "10000010");
                    _requestMap.put("CARD_GROUP_SELECTOR", "1");
                    _requestMap.put("SENDER_MSISDN", "9810025698");
                    _requestMap.put("INTERFACE_ACTION", "C");
                    _requestMap.put("ADJUST", "Y");
                }

                if (_credit.equals(_action))
                    inhandler.credit(_requestMap);
                else if (_creditAdjust.equals(_action))
                    inhandler.creditAdjust(_requestMap);
                else if (_debitAdjust.equals(_action))
                    inhandler.debitAdjust(_requestMap);
                else if (_validate.equals(_action))
                    inhandler.validate(_requestMap);
            } catch (BTSLBaseException be) {
                System.out.println("BTSLBaseException BTSLBaseException BTSLBaseException BTSLBaseException BTSLBaseException  _count250:" + _count250 + " _count500:" + _count500 + " _count200" + _count200 + "be.getMessage():;;;;;" + be.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception Exception Exception Exception Exception Exception e=" + e.getMessage());
            }

        }// end of run method
    }// end of Class-LoadTestHandler
} // end of Class-CbossTestUtility
