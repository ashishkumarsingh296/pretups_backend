package com.inter.voms;

import java.util.HashMap;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.Constants;
import com.btsl.voms.vomscommon.VOMSI;

public class VOMSResponseParser {

    private static Log _log = LogFactory.getLog(VOMSResponseParser.class.getName());
    public static OperatorUtilI _operatorUtil = null;

    /**
     * This method is used to parse the response.
     * 
     * @param int p_action
     * @param String
     *            p_responseStr
     * @return HashMap
     * @throws Exception
     */
    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("VoucherConsController", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherConsController[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public HashMap parseResponse(int p_action, String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Entered p_action::" + p_action + " p_responseStr:: " + p_responseStr);
        HashMap map = null;
        try {
            switch (p_action) {
            case VOMSI.ACTION_SERIALNO_PIN_DETAILS: {
                map = parseGetPinInfoResponse(p_responseStr);
                break;
            }
            case VOMSI.ACTION_VOUCHER_INFO: {
                map = parsePinInfoResponse(p_responseStr);
                break;
            }
            case VOMSI.ACTION_VOUCHER_CONSUMPTION: {
                map = parsePinConResponse(p_responseStr);
                break;
            }
            case VOMSI.ACTION_VOUCHER_DETAILS_AGAIN: {
                map = parseGetPinInfoResponse(p_responseStr);
                break;
            }
            case VOMSI.ACTION_VOUCHER_ROLLBACK: {
                map = parseVoucherRollbackResponse(p_responseStr);
                break;
            }
            case VOMSI.ACTION_VOUCHER_RET_ROLLBACK: {
                map = parseVoucherRetrivalRollbackResponse(p_responseStr);
                break;
            }
            }
            String pin = "";
            try {
                pin = map.get("PIN").toString();
                if (pin.contains(",")){ // it contains more than one PIN
                	StringBuilder decryptedPINinCSV  = new StringBuilder();
            		String[] pins = pin.split(",");
                    if (_log.isDebugEnabled())
                        _log.debug("parseResponse", "Size of PIN Array::" + pins.length);
            		for(String tempPin : pins){
            			tempPin = _operatorUtil.decryptPINPassword(tempPin);
            			decryptedPINinCSV.append(tempPin+",");
            		}
            		pin = decryptedPINinCSV.toString().substring(0,decryptedPINinCSV.toString().lastIndexOf(',')) ;
            		if (_log.isDebugEnabled())
                        _log.debug("parseResponse", "PIN AFTER PARSING::" + pin);
                }
                else{
                	pin = _operatorUtil.decryptPINPassword(pin);
                }
            } catch (Exception e) {
                pin = "";
            }
            map.put("PIN", pin);
        } catch (Exception e) {
            _log.error("parseResponse", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponse", "Exiting map::" + map);
        }
        return map;
    }

    /**
     * This method is used to parse the response of GetAccountinformation.
     * 
     * @param String
     *            p_responseStr
     * @return HashMap
     */
    private HashMap parseGetPinInfoResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetPinInfoResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        try {
            responseMap = new HashMap();
            int index = p_responseStr.indexOf("<TXNSTATUS>");
            if (index > 0) {
                String TXNSTATUS = p_responseStr.substring(index + "<TXNSTATUS>".length(), p_responseStr.indexOf("</TXNSTATUS>", index));
                responseMap.put("TXNSTATUS", TXNSTATUS);

                if (InterfaceUtil.NullToString((String) responseMap.get("TXNSTATUS")).equals("200")) {
                    responseMap.put("ErrorCode", "0");
                } else {
                    responseMap.put("ErrorCode", "Fail");
                }

            } else {
                responseMap.put("ErrorCode", "Fail");
            }
            
            
            	index = p_responseStr.indexOf("<Pin>");
                if (index > 0) {
                    String pin = p_responseStr.substring(index + "<Pin>".length(), p_responseStr.indexOf("</Pin>", index));
                    responseMap.put("PIN", pin);
                }

                index = p_responseStr.indexOf("<TALKTIME>");
                if (index > 0) {
                    String talkTime = p_responseStr.substring(index + "<TALKTIME>".length(), p_responseStr.indexOf("</TALKTIME>", index));
                    responseMap.put("TALKTIME", talkTime);
                }

                index = p_responseStr.indexOf("<Serial_No>");
                if (index > 0) {
                    String SerialID = p_responseStr.substring(index + "<Serial_No>".length(), p_responseStr.indexOf("</Serial_No>", index));
                    responseMap.put("SERIAL_NUMBER", SerialID);
                }
                index = p_responseStr.indexOf("<VALIDITY>");
                if (index > 0) {
                    String accExp = p_responseStr.substring(index + "<VALIDITY>".length(), p_responseStr.indexOf("</VALIDITY>", index));
                    responseMap.put("VALIDITY", accExp);
                }

                index = p_responseStr.indexOf("<VOUCHER_EXPIRY_DATE>");
                if (index > 0) {
                    String accExp = p_responseStr.substring(index + "<VOUCHER_EXPIRY_DATE>".length(), p_responseStr.indexOf("</VOUCHER_EXPIRY_DATE>", index));
                    responseMap.put("Expire_Date", accExp);
                }
                index = p_responseStr.indexOf("<State>");
                if (index > 0) {
                    String State = p_responseStr.substring(index + "<State>".length(), p_responseStr.indexOf("</State>", index));
                    responseMap.put("State", State);
                }

        } catch (Exception e) {
            _log.error("parseGetPinInfoResponse", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetPinInfoResponse", "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }

    /**
     * This method is used to parse the response of GetAccountinformation.
     * 
     * @param String
     *            p_responseStr
     * @return HashMap
     */
    private HashMap parsePinInfoResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parsePinInfoResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        try {
            responseMap = new HashMap();
            int index = p_responseStr.indexOf("<TXNSTATUS>");
            if (index > 0) {
                String TXNSTATUS = p_responseStr.substring(index + "<TXNSTATUS>".length(), p_responseStr.indexOf("</TXNSTATUS>", index));
                responseMap.put("TXNSTATUS", TXNSTATUS);

                if (InterfaceUtil.NullToString((String) responseMap.get("TXNSTATUS")).equals("200")) {
                    responseMap.put("ErrorCode", "0");
                } else {
                    responseMap.put("ErrorCode", "Fail");
                }

            } else {
                responseMap.put("ErrorCode", "Fail");
            }
            index = p_responseStr.indexOf("<SNO>");
            if (index > 0) {
                String SNO = p_responseStr.substring(index + "<SNO>".length(), p_responseStr.indexOf("</SNO>", index));
                responseMap.put("SNO", SNO);
            }
            index = p_responseStr.indexOf("<TOPUP>");
            if (index > 0) {
                String TOPUP = p_responseStr.substring(index + "<TOPUP>".length(), p_responseStr.indexOf("</TOPUP>", index));
                responseMap.put("TOPUP", TOPUP);
            }
            index = p_responseStr.indexOf("<VALIDITY>");
            if (index > 0) {
                String accExp = p_responseStr.substring(index + "<VALIDITY>".length(), p_responseStr.indexOf("</VALIDITY>", index));
                responseMap.put("VALIDITY", accExp);
            }else{
            	responseMap.put("VALIDITY_DAYS", "30");
            }

        } catch (Exception e) {
            _log.error("parseGetPinInfoResponse", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parsePinInfoResponse", "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }

    /**
     * This method is used to parse the response of GetAccountinformation.
     * 
     * @param String
     *            p_responseStr
     * @return HashMap
     */
    private HashMap parsePinConResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetPinInfoResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        try {
            responseMap = new HashMap();
            int index = p_responseStr.indexOf("<TXNSTATUS>");
            if (index > 0) {
                String TXNSTATUS = p_responseStr.substring(index + "<TXNSTATUS>".length(), p_responseStr.indexOf("</TXNSTATUS>", index));
                responseMap.put("TXNSTATUS", TXNSTATUS);

                if (InterfaceUtil.NullToString((String) responseMap.get("TXNSTATUS")).equals("200")) {
                    responseMap.put("ErrorCode", "0");
                } else {
                    responseMap.put("ErrorCode", "Fail");
                }

            } else {
                responseMap.put("ErrorCode", "Fail");
            }
            index = p_responseStr.indexOf("<SNO>");
            if (index > 0) {
                String SNO = p_responseStr.substring(index + "<SNO>".length(), p_responseStr.indexOf("</SNO>", index));
                responseMap.put("SNO", SNO);
            }
            index = p_responseStr.indexOf("<TOPUP>");
            if (index > 0) {
                String TOPUP = p_responseStr.substring(index + "<TOPUP>".length(), p_responseStr.indexOf("</TOPUP>", index));
                responseMap.put("TOPUP", TOPUP);
            }
            index = p_responseStr.indexOf("<COMSUMED>");
            if (index > 0) {
                String COMSUMED = p_responseStr.substring(index + "<COMSUMED>".length(), p_responseStr.indexOf("</COMSUMED>", index));
                responseMap.put("COMSUMED", COMSUMED);
            }

        } catch (Exception e) {
            _log.error("parseGetPinInfoResponse", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetPinInfoResponse", "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }

    /**
     * This method is used to parse the response of GetAccountinformation.
     * 
     * @param String
     *            p_responseStr
     * @return HashMap
     */
    private HashMap parseVoucherRetrivalRollbackResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseVoucherRollbackResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        try {
            responseMap = new HashMap();
            int index = p_responseStr.indexOf("<TXNSTATUS>");
            if (index > 0) {
                String TXNSTATUS = p_responseStr.substring(index + "<TXNSTATUS>".length(), p_responseStr.indexOf("</TXNSTATUS>", index));
                responseMap.put("TXNSTATUS", TXNSTATUS);

                if (InterfaceUtil.NullToString((String) responseMap.get("TXNSTATUS")).equals("200")) {
                    responseMap.put("ErrorCode", "0");
                } else {
                    responseMap.put("ErrorCode", "Fail");
                }

            } else {
                responseMap.put("ErrorCode", "Fail");
            }
        } catch (Exception e) {
            _log.error("parseVoucherRollbackResponse", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseVoucherRollbackResponse", "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }

    /**
     * This method is used to parse the response of GetAccountinformation.
     * 
     * @param String
     *            p_responseStr
     * @return HashMap
     */
    private HashMap parseVoucherRollbackResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseVoucherRollbackResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        try {
            responseMap = new HashMap();
            int index = p_responseStr.indexOf("<TXNSTATUS>");
            if (index > 0) {
                String TXNSTATUS = p_responseStr.substring(index + "<TXNSTATUS>".length(), p_responseStr.indexOf("</TXNSTATUS>", index));
                responseMap.put("TXNSTATUS", TXNSTATUS);

                if (InterfaceUtil.NullToString((String) responseMap.get("TXNSTATUS")).equals("200")) {
                    responseMap.put("ErrorCode", "0");
                } else {
                    responseMap.put("ErrorCode", "Fail");
                }

            } else {
                responseMap.put("ErrorCode", "Fail");
            }
            index = p_responseStr.indexOf("<SNO>");
            if (index > 0) {
                String SNO = p_responseStr.substring(index + "<SNO>".length(), p_responseStr.indexOf("</SNO>", index));
                responseMap.put("SNO", SNO);
            }
            index = p_responseStr.indexOf("<TOPUP>");
            if (index > 0) {
                String TOPUP = p_responseStr.substring(index + "<TOPUP>".length(), p_responseStr.indexOf("</TOPUP>", index));
                responseMap.put("TOPUP", TOPUP);
            }

        } catch (Exception e) {
            _log.error("parseVoucherRollbackResponse", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseVoucherRollbackResponse", "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }

}
