package com.inter.huaweicitycell;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;

/**
 * @HuaweiRequestFormatter.java
 *                              Copyright(c) 2009, Bharti Telesoft Int. Public
 *                              Ltd.
 *                              All Rights Reserved
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Vipan Nov 16, 2010 Initial Creation
 *                              ------------------------------------------------
 *                              -----------------------------------------------
 *                              This class is responsible to generate the
 *                              request and parse the response for the HUAWEI
 *                              interface.
 */
public class HuaweiRequestFormatter {

    public Log _log = LogFactory.getLog("HuaweiRequestFormatter".getClass().getName());
    private byte requestByte[] = null;
    int j = 0;
    public static byte heartBeatRequestInByte[];

    /**
     * This method will return of plain request message.
     * This method internally calls private method to get MML request string.
     * 
     * @param int p_action
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    public byte[] generateRequest(int p_action, HashMap<String, String> p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Entered p_action=" + p_action + " map: " + p_map);

        p_map.put("action", String.valueOf(p_action));
        j = 0;
        requestByte = new byte[127];
        try {
            switch (p_action) {
            case HuaweiI.ACTION_ACCOUNT_INFO: {
                generateAccountInfoRequest(p_map);
                break;
            }
            case HuaweiI.ACTION_RECHARGE_CREDIT: {
                generateRechargeCreditRequest(p_map);
                break;
            }
            case HuaweiI.ACTION_IMMEDIATE_DEBIT: {
                generateImmediateDebitRequest(p_map);
                break;
            }
            case HuaweiI.ACTION_HEART_BEAT: {
                generateHeartBeatRequest(p_map);
                break;
            }

            }// end of switch block
        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("generateRequest", "Exception e:" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRequest", "Exited ");
        }// end of finally
         // For the local testing request string ended with colon, remove this
         // colon while the delivery of code
        return requestByte;
    }// end of generateRequest

    /**
     * This method internally calls methods (according to p_action parameter) to
     * get response HashMap and returns it.
     * 
     * @param int action
     * @param String
     *            responseStr
     * @return HashMap map
     * @throws BTSLBaseException
     *             ,Exception
     */
    public HashMap<String, String> parseResponse(int p_action, byte[] p_responseStr) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse ", "Entered p_action=" + p_action + "p_responseStr Length = " + p_responseStr.length + " p_responseStr=" + InterfaceUtil.printByteData(p_responseStr));
        HashMap<String, String> map = null;
        try {
            switch (p_action) {
            case HuaweiI.ACTION_ACCOUNT_INFO: {
                map = parseRechargeResponse(p_responseStr, HuaweiI.RESPONSE_TYPE_VALIDATE);
                break;
            }
            case HuaweiI.ACTION_RECHARGE_CREDIT: {
                map = parseRechargeResponse(p_responseStr, HuaweiI.RESPONSE_TYPE_RECHARGE);
                break;
            }
            case HuaweiI.ACTION_IMMEDIATE_DEBIT: {
                map = parseRechargeResponse(p_responseStr, HuaweiI.RESPONSE_TYPE_RECHARGE);
                break;
            }
            case HuaweiI.ACTION_HEART_BEAT: {
                map = generateHeartBeatResponse(p_responseStr);
                break;
            }
            }// end of switch block
        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("parseResponse", "Exception e:" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponse", "Exiting map: " + map);
        }// end of finally
        return map;
    }// end of parseResponse

    /**
     * This method will return MML request message for Credit action.
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    private void generateRechargeCreditRequest(HashMap<String, String> p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRechargeCreditRequest", "Entered p_map= " + p_map);
        String requestStr = "";
        try {
            getHeaderInfoStr(p_map);
            getRequestDataInfoStr(p_map);
        } catch (Exception e) {
            _log.error("generateRechargeCreditRequest", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRechargeCreditRequest", "Exiting  requestStr:" + requestStr);
        }

    }

    private HashMap<String, String> generateHeartBeatResponse(byte[] p_responseByte) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug(" generateHeartBeatResponse ", "Entered heartBeatResponseByte : " + InterfaceUtil.printByteData(p_responseByte));
        HashMap<String, String> map = null;
        try {
            if (p_responseByte.length == 5) {
                map = new HashMap();
                map.put("status", HuaweiI.HEARTBEAT_SUCCEEDED);
            } else {
                map = new HashMap();
                map.put("status", HuaweiI.HEARTBEAT_FAILED);
            }
        } catch (Exception e) {
            _log.error("generateHeartBeatResponse", "Exception e:" + e.getMessage());
            map.put("status", HuaweiI.HEARTBEAT_FAILED);
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateHeartBeatResponse", " Exit  map : " + map);
        }
        return map;
    }

    /**
     * This method will return MML request message for Credit action.
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    private void generateAccountInfoRequest(HashMap<String, String> p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateAccountInfoRequest", "Entered p_map= " + p_map);

        try {
            getHeaderInfoStr(p_map);
            getRequestDataInfoStr(p_map);
        } catch (Exception e) {
            _log.error("generateAccountInfoRequest", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateAccountInfoRequest", "Exiting ");
        }

    }

    /**
     * This method will return MML request message for Credit action.
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    private void getHeaderInfoStr(HashMap<String, String> p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getHeaderInfoStr", "Entered  ");
        String _interfaceID = null;
        try {
            if (InterfaceUtil.isNullString((String) p_map.get("INTERFACE_ID"))) {
                _log.error("getHeaderInfoStr", "Can't get value of INTERFACE_ID from map");
                throw new BTSLBaseException(this, "getHeaderInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _interfaceID = (String) p_map.get("INTERFACE_ID");

            if (!InterfaceUtil.isNullString((String) p_map.get("INTERFACE_ACTION")) && "V".equals((String) p_map.get("INTERFACE_ACTION"))) {
                if (InterfaceUtil.isNullString(FileCache.getValue(_interfaceID, "ACCOUNT_INFO_REQ_TYPE"))) {
                    _log.error("getHeaderInfoStr", "Value of ACCOUNT_INFO_REQ_TYPE is not defined in the INFile");
                    throw new BTSLBaseException(this, "getHeaderInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
            } else {
                if (InterfaceUtil.isNullString(FileCache.getValue(_interfaceID, "RECHARGE_REQ_TYPE"))) {
                    _log.error("getHeaderInfoStr", "Value of ACCOUNT_INFO_REQ_TYPE is not defined in the INFile");
                    throw new BTSLBaseException(this, "getHeaderInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                }
            }

            if (InterfaceUtil.isNullString(FileCache.getValue(_interfaceID, "MESSAGELENGTH"))) {
                _log.error("getHeaderInfoStr", "Value of MESSAGELENGTH is not defined in the INFile");
                throw new BTSLBaseException(this, "getHeaderInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            if (InterfaceUtil.isNullString(FileCache.getValue(_interfaceID, "MESSAGETYPE"))) {
                _log.error("getHeaderInfoStr", "Value of MESSAGETYPE is not defined in the INFile");
                throw new BTSLBaseException(this, "getHeaderInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            if (InterfaceUtil.isNullString(FileCache.getValue(_interfaceID, "MESSAGEID"))) {
                _log.error("getHeaderInfoStr", "Value of MESSAGEID is not defined in the INFile");
                throw new BTSLBaseException(this, "getHeaderInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }

            if (InterfaceUtil.isNullString(FileCache.getValue(_interfaceID, "SRCFE"))) {
                _log.error("getHeaderInfoStr", "Value of SRCFE is not defined in the INFile");
                throw new BTSLBaseException(this, "getHeaderInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }

            if (InterfaceUtil.isNullString(FileCache.getValue(_interfaceID, "DSTFE"))) {
                _log.error("getHeaderInfoStr", "Value of SRCFE is not defined in the INFile");
                throw new BTSLBaseException(this, "getHeaderInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }

            if (InterfaceUtil.isNullString((String) p_map.get("IN_HEADER_TXN_ID"))) {
                _log.error("getHeaderInfoStr", "Value of IN_HEADER_TXN_ID is null or empty in request map");
                throw new BTSLBaseException(this, "getHeaderInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }

            if (InterfaceUtil.isNullString(FileCache.getValue(_interfaceID, "DSTFSM"))) {
                _log.error("getHeaderInfoStr", "Value of DSTFSM is not defined in the INFile");
                throw new BTSLBaseException(this, "getHeaderInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            if (InterfaceUtil.isNullString(FileCache.getValue(_interfaceID, "NODENO"))) {
                _log.error("getHeaderInfoStr", "Value of NODENO is not defined in the INFile");
                throw new BTSLBaseException(this, "getHeaderInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            if (InterfaceUtil.isNullString(FileCache.getValue(_interfaceID, "SERVICEKEY"))) {
                _log.error("getHeaderInfoStr", "Value of SERVICEKEY is not defined in the INFile");
                throw new BTSLBaseException(this, "getHeaderInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }

            intToByteArray(Integer.parseInt(FileCache.getValue(_interfaceID, "MESSAGELENGTH")));
            intToByteArray(Integer.parseInt(FileCache.getValue(_interfaceID, "MESSAGETYPE")));
            intToByteArray(Integer.parseInt(FileCache.getValue(_interfaceID, "MESSAGEID")));
            intToByteArray(Integer.parseInt(FileCache.getValue(_interfaceID, "SRCFE")));
            intToByteArray(Integer.parseInt(FileCache.getValue(_interfaceID, "DSTFE")));
            intToByteArray(Integer.parseInt((String) p_map.get("IN_HEADER_TXN_ID")));
            intToByteArray(Integer.parseInt(FileCache.getValue(_interfaceID, "DSTFSM")));
            intToByteArray(Integer.parseInt(FileCache.getValue(_interfaceID, "NODENO")));
            intToByteArray(Integer.parseInt(FileCache.getValue(_interfaceID, "SERVICEKEY")));
            if (!InterfaceUtil.isNullString((String) p_map.get("INTERFACE_ACTION")) && "V".equals((String) p_map.get("INTERFACE_ACTION")))
                intToByteArray(Integer.parseInt(FileCache.getValue(_interfaceID, "ACCOUNT_INFO_REQ_TYPE")));
            else
                intToByteArray(Integer.parseInt(FileCache.getValue(_interfaceID, "RECHARGE_REQ_TYPE")));
        } catch (Exception e) {
            _log.error("getHeaderInfoStr", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getHeaderInfoStr", "Exiting ");
        }

    }

    /**
     * This method will return MML request message for Credit action.
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    private void getRequestDataInfoStr(HashMap<String, String> p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getRequestDataInfoStr", "Entered ");
        int msisdnLength = 0, msisdnPinLength = 0, acountNumberLength = 0, requestIdLength = 0;
        String _interfaceID = null;
        try {

            if (InterfaceUtil.isNullString((String) p_map.get("INTERFACE_ID"))) {
                _log.error("getRequestDataInfoStr", "Can't get value of INTERFACE_ID from map");
                throw new BTSLBaseException(this, "getRequestDataInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            _interfaceID = (String) p_map.get("INTERFACE_ID");

            if (InterfaceUtil.isNullString(FileCache.getValue(_interfaceID, "MSISDN_MAX_LENGTH"))) {
                _log.error("getRequestDataInfoStr", "Value of MSISDNMAXLENGTH is not defined in the INFile");
                throw new BTSLBaseException(this, "getRequestDataInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            msisdnLength = Integer.parseInt(FileCache.getValue(_interfaceID, "MSISDN_MAX_LENGTH"));
            String MSISDN = (String) p_map.get("MSISDN");
            if (InterfaceUtil.isNullString(MSISDN)) {
                _log.error("getRequestDataInfoStr", "Can't get value of MSISDN from map");
                throw new BTSLBaseException(this, "getRequestDataInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            convertStringToByteWithPadding(String.valueOf(MSISDN.subSequence(1, MSISDN.length())), msisdnLength);
            // convertStringToByteWithPadding("1199400036",msisdnLength);

            if (InterfaceUtil.isNullString(FileCache.getValue(_interfaceID, "MSISDN_PIN_MAX_LENGTH"))) {
                _log.error("getRequestDataInfoStr", "Value of MSISDN_PIN_MAX_LENGTH is not defined in the INFile");
                throw new BTSLBaseException(this, "getRequestDataInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            msisdnPinLength = Integer.parseInt(FileCache.getValue(_interfaceID, "MSISDN_PIN_MAX_LENGTH"));

            if (InterfaceUtil.isNullString(FileCache.getValue(_interfaceID, "MSISDN_PIN"))) {
                _log.error("getRequestDataInfoStr", "Value of MSISDN_PIN is not defined in the INFile");
                throw new BTSLBaseException(this, "getRequestDataInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            convertStringToByteWithPadding(FileCache.getValue(_interfaceID, "MSISDN_PIN"), msisdnPinLength);

            if (InterfaceUtil.isNullString(FileCache.getValue(_interfaceID, "REQUESTID_MAX_LENGTH"))) {
                _log.error("getRequestDataInfoStr", "Value of REQUESTID_MAX_LENGTH is not defined in the INFile");
                throw new BTSLBaseException(this, "getRequestDataInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            requestIdLength = Integer.parseInt(FileCache.getValue(_interfaceID, "REQUESTID_MAX_LENGTH"));

            // convertStringToByteWithPadding(InterfaceUtil.getINTxnID(),requestIdLength);
            convertStringToByteWithPadding((String) p_map.get("IN_TXN_ID"), requestIdLength);

            if (InterfaceUtil.isNullString((String) p_map.get("INTERFACE_ACTION"))) {
                _log.error("getRequestDataInfoStr", "Can't get value of INTERFACE_ACTION from map");
                throw new BTSLBaseException(this, "getRequestDataInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            } else if (PretupsI.INTERFACE_VALIDATE_ACTION.equals((String) p_map.get("INTERFACE_ACTION"))) {
                intToByteArray(0);
                intToByteArray(0);
            } else {
                if (InterfaceUtil.isNullString((String) p_map.get("INTERFACE_AMOUNT"))) {
                    _log.error("getRequestDataInfoStr", "Can't get value of REQUESTED_AMOUNT from map");
                    throw new BTSLBaseException(this, "getRequestDataInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
                } else if (PretupsI.INTERFACE_DEBIT_ACTION.equals((String) p_map.get("INTERFACE_ACTION"))) {
                    int amount = Integer.parseInt((String) p_map.get("INTERFACE_AMOUNT")) * (-1);
                    intToByteArray(amount);
                }

                else
                    intToByteArray(Integer.parseInt((String) p_map.get("INTERFACE_AMOUNT")));

                if (InterfaceUtil.isNullString((String) p_map.get("VALIDITY_DAYS")))
                    intToByteArray(1);
                else
                    intToByteArray(Integer.parseInt((String) p_map.get("VALIDITY_DAYS")));

            }

            if (InterfaceUtil.isNullString(FileCache.getValue(_interfaceID, "ACCOUNT_NUMBER_MAX_LENGTH"))) {
                _log.error("getRequestDataInfoStr", "Value of ACCOUNT_NUMBER_MAX_LENGTH is not defined in the INFile");
                throw new BTSLBaseException(this, "getRequestDataInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            acountNumberLength = Integer.parseInt(FileCache.getValue(_interfaceID, "ACCOUNT_NUMBER_MAX_LENGTH"));
            if (InterfaceUtil.isNullString(FileCache.getValue(_interfaceID, "ACCOUNT_NUMBER"))) {
                _log.error("getRequestDataInfoStr", "Value of ACCOUNT_NUMBER is not defined in the INFile");
                throw new BTSLBaseException(this, "getRequestDataInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            if (PretupsI.SERVICE_TYPE_P2PCREDITRECHARGE.equals(p_map.get("REQ_SERVICE"))) {
                convertStringToByteWithPadding(FileCache.getValue(_interfaceID, "ACCOUNT_NUMBER_P2P"), acountNumberLength);
            } else {
                convertStringToByteWithPadding(FileCache.getValue(_interfaceID, "ACCOUNT_NUMBER"), acountNumberLength);
            }
        } catch (Exception e) {
            _log.error("getRequestDataInfoStr", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getRequestDataInfoStr", "Exit ");
        }
    }

    /**
     * This method will return MML request message for Credit action.
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    private void generateImmediateDebitRequest(HashMap<String, String> p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateImmediateDebitRequest", "Entered p_map= " + p_map);
        String requestStr = "";
        try {

            getHeaderInfoStr(p_map);
            getRequestDataInfoStr(p_map);
        } catch (Exception e) {
            _log.error("generateImmediateDebitRequest", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateImmediateDebitRequest", "Exiting  requestStr:" + requestStr);
        }
    }

    /**
     * This method will return MML request message for ImmediateDebit action.
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    private void generateHeartBeatRequest(HashMap<String, String> p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateHeartBeatRequest", "Entered p_map= " + p_map);
        String requestData = null;
        try {
            requestData = FileCache.getValue((String) p_map.get("interfaceID"), "HEART_BEAT_COMMAND");
            byte heartBeatdataByte[] = new byte[requestData.length()];
            for (int i = 0; i < heartBeatdataByte.length; i++) {
                heartBeatdataByte[i] = (byte) Integer.parseInt(requestData.charAt(i) + "");
            }
            requestByte = heartBeatdataByte;

        } catch (Exception e) {
            _log.error("generateHeartBeatRequest", "Exception e:" + e.getMessage());
            throw e;
        } finally {

            if (_log.isDebugEnabled())
                _log.debug("generateHeartBeatRequest", "Exiting  heartBeatRequestBye : " + InterfaceUtil.printByteData(requestByte));
        }
    }

    /**
     * This method parse the response for Credit from MML String into HashMap
     * and returns it
     * 
     * @param String
     *            p_responseStr
     * @return HashMap map
     * @throws Exception
     */
    private HashMap<String, String> parseRechargeResponse(byte[] p_responseByte, int p_action) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeResponse", "Entered  p_action  = " + p_action);
        HashMap<String, String> map = null;
        try {
            if (p_responseByte.length < 116) {
                map = new HashMap();
                map.put("status", HuaweiI.RECHARGE_FAILED);
            } else {
                map = new HashMap();

                if ((int) p_responseByte[39] == p_action) {
                    String returnCode = null, IN_TransactionId = null, validity = null, accountNumber = null, accountBalance = null, headerTeansactionId = null;
                    headerTeansactionId = convertByteToInt(p_responseByte, 20, 23) + "";
                    returnCode = convertByteToString(p_responseByte, 40, 43);
                    IN_TransactionId = convertByteToString(p_responseByte, 44, 67);
                    accountBalance = convertByteToInt(p_responseByte, 68, 71) + "";
                    validity = convertByteToString(p_responseByte, 72, 79);
                    accountNumber = convertByteToString(p_responseByte, 80, 11);
                    if (_log.isDebugEnabled())
                        _log.debug("parseRechargeResponse", "Entered  headerTeansactionId  = " + headerTeansactionId + " :: returnCode = " + returnCode + " :: IN_TransactionId = " + IN_TransactionId + " :: accountBalance = " + accountBalance + " :: validity = " + validity + " :: accountNumber = " + accountNumber);
                    if (InterfaceUtil.isNullString(headerTeansactionId)) {
                        _log.error("parseRechargeResponse", "Can't get value of headerTeansactionId from response string");
                        throw new BTSLBaseException(this, "getRequestDataInfoStr", InterfaceErrorCodesI.INVALID_RESPONSE);
                    }
                    if (InterfaceUtil.isNullString(returnCode.toString())) {
                        _log.error("parseRechargeResponse", "Can't get value of returnCode from response string");
                        throw new BTSLBaseException(this, "getRequestDataInfoStr", InterfaceErrorCodesI.INVALID_RESPONSE);
                    }
                    if (HuaweiI.RECHARGE_SUCCEEDED.equals(returnCode.toString())) {
                        if (InterfaceUtil.isNullString(IN_TransactionId.toString())) {
                            _log.error("parseRechargeResponse", "Can't get value of transactionId from response string");
                            throw new BTSLBaseException(this, "getRequestDataInfoStr", InterfaceErrorCodesI.INVALID_RESPONSE);
                        }
                        if (InterfaceUtil.isNullString(accountBalance)) {
                            _log.error("parseRechargeResponse", "Can't get value of accountBalance from response string");
                            throw new BTSLBaseException(this, "getRequestDataInfoStr", InterfaceErrorCodesI.INVALID_RESPONSE);
                        }

                        else if (p_action == HuaweiI.RESPONSE_TYPE_VALIDATE) {
                            if (!InterfaceUtil.isNumeric(accountBalance)) {
                                _log.error("parseRechargeResponse", "Balance from IN is less than zero or its not numeric");
                                throw new BTSLBaseException(this, "getRequestDataInfoStr", InterfaceErrorCodesI.RC_INSIFFICENT_ACC_BALANCE);
                            }
                        }
                        if (InterfaceUtil.isNullString(validity.toString())) {
                            _log.error("parseRechargeResponse", "Can't get value of validity from response string");
                            throw new BTSLBaseException(this, "getRequestDataInfoStr", InterfaceErrorCodesI.INVALID_RESPONSE);
                        }

                        if (p_action == HuaweiI.RESPONSE_TYPE_VALIDATE)
                            map.put("resType", HuaweiI.ACCOUNT_INFO_RES_TYPE);
                        else
                            map.put("resType", HuaweiI.RECHARGE_RES_TYPE);
                        map.put("status", returnCode);
                        map.put("transaction_id", IN_TransactionId);
                        map.put("accountBalance", accountBalance.trim());
                        map.put("AFTERVALIDITY", validity);
                        map.put("accountNumber", accountNumber);
                        map.put("IN_HEADER_TXN_ID", headerTeansactionId.trim());

                    } else {
                        map.put("status", returnCode);
                    }
                }
            }
        } catch (Exception e) {
            _log.error("parseRechargeResponse", "Exception e:" + e.getMessage());
            map = new HashMap();
            map.put("status", HuaweiI.RECHARGE_FAILED);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeResponse", "Exit");
        }
        return map;
    }

    public static String convertIntToHexWithPadding(int p_requestByte, int p_orgHeaderLength) {
        String requestByteStr = "";
        requestByteStr = Integer.toHexString(p_requestByte);
        int paddingLength = p_orgHeaderLength - requestByteStr.length();

        if (p_orgHeaderLength > requestByteStr.length()) {
            for (int i = 0; i < paddingLength; i++) {
                requestByteStr = "0" + requestByteStr;
            }
        }
        return requestByteStr;
    }

    private void intToByteArray(int p_value) {
        if (_log.isDebugEnabled())
            _log.debug("intToByteArray ", " Entered  p_value = " + p_value);
        byte b[] = new byte[4];
        for (int i = 0; i < 4; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) (p_value >>> offset & 0xff);

        }
        if (_log.isDebugEnabled())
            _log.debug("intToByteArray ", " Entered  p_value = " + InterfaceUtil.printByteData(b));
        for (int k = 0; k < b.length;) {
            requestByte[j] = b[k];
            k++;
            j++;
        }
    }

    private void convertStringToByteWithPadding(String originalString, int reqLength) {
        if (_log.isDebugEnabled())
            _log.debug("convertStringToByteWithPadding", "Entered  originalString = " + originalString + " :: reqLength = " + reqLength);
        int orglength = 0;
        orglength = originalString.length();
        if (reqLength > orglength) {
            for (int i = 0; i < reqLength - orglength; i++) {
                originalString = originalString + "\0";
            }
        }
        for (int i = 0; i < originalString.length(); i++) {
            requestByte[j] = (byte) originalString.charAt(i);
            j++;
        }

    }

    private String convertByteToString(byte[] p_responseByte, int p_startIndex, int p_endIndex) {
        if (_log.isDebugEnabled())
            _log.debug("convertByteToString ", "Entered  p_startIndex = " + p_startIndex + " :: p_endIndex = " + p_endIndex);
        StringBuffer responseString = new StringBuffer();
        for (int a = p_startIndex; a <= p_endIndex; a++) {
            responseString.append((char) p_responseByte[a]);
        }
        return responseString.toString();

    }

    private int convertByteToInt(byte[] p_responseByte, int p_startIndex, int p_endIndex) {
        if (_log.isDebugEnabled())
            _log.debug("convertByteToInt ", "Entered  p_startIndex = " + p_startIndex + " :: p_endIndex = " + p_endIndex);
        int k = 0;
        byte[] intData = new byte[4];
        for (int c = p_startIndex; c <= p_endIndex; c++) {

            intData[k] = p_responseByte[c];
            k++;
        }
        return byteToInt(intData);
    }

    public static int byteToInt(byte[] data) {
        if (data == null || data.length != 4)
            return 0x0;
        return (int) ((0xff & data[0]) << 24 | (0xff & data[1]) << 16 | (0xff & data[2]) << 8 | (0xff & data[3]) << 0);
    }

    protected void heartBeatRequest(String p_interfaceId) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("heartBeatRequest : ", "Entered = " + p_interfaceId);
        String heartBeatRequest = null;
        try {
            if (InterfaceUtil.isNullString(FileCache.getValue(p_interfaceId, "HEART_BEAT_COMMAND"))) {
                _log.error("getRequestDataInfoStr", "Value of HEART_BEAT_COMMAND is not defined in the INFile");
                throw new BTSLBaseException(this, "getRequestDataInfoStr", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            heartBeatRequest = FileCache.getValue(p_interfaceId, "HEART_BEAT_COMMAND");
            byte heartBeatRequestByte[] = new byte[heartBeatRequest.length()];
            for (int i = 0; i < heartBeatRequest.length(); i++) {
                heartBeatRequestByte[i] = (byte) Integer.parseInt(heartBeatRequest.charAt(i) + "");
            }
            heartBeatRequestInByte = heartBeatRequestByte;
        } catch (BTSLBaseException e) {

            _log.error("heartBeatRequest", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("heartBeatRequest : ", "Exit :: HeartBeat request = " + InterfaceUtil.printByteData(heartBeatRequestInByte));
        }
    }
}
