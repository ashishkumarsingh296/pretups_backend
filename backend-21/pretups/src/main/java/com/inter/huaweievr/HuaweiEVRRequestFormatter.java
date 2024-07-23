package com.inter.huaweievr;

/**
 * @(#)HuaweiEVRRequestFormatter.java
 *                                    Copyright(c) 2007, Bharti Telesoft Int.
 *                                    Public Ltd.
 *                                    All Rights Reserved
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Author Date History
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Vinay Kumar Singh December 10, 2007
 *                                    Initial Creation
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    ------------
 *                                    This class is responsible to generate the
 *                                    request and parse the response for the
 *                                    HuaweiEVR interface.
 */
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;

public class HuaweiEVRRequestFormatter {
    public Log _log = LogFactory.getLog("HuaweiEVRRequestFormatter".getClass().getName());
    private static String _lineSep = System.getProperty("line.separator");

    /**
     * This method will return of MML request message.
     * This method internally calls private method to get MML request string.
     * 
     * @param int p_action
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    public String generateRequest(int p_action, HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Entered p_action=" + p_action + " map: " + p_map);
        String str = null;
        p_map.put("action", String.valueOf(p_action));
        try {
            switch (p_action) {
            case HuaweiEVRI.ACTION_LOGIN: {
                str = generateLoginRequest(p_map);
                break;
            }
            case HuaweiEVRI.ACTION_LOGOUT: {
                str = generateLogoutRequest(p_map);
                break;
            }
            case HuaweiEVRI.ACTION_ACCOUNT_INFO: {
                str = generateGetAccountInfoRequest(p_map);
                break;
            }
            case HuaweiEVRI.ACTION_RECHARGE_CREDIT: {
                str = generateRechargeCreditRequest(p_map);
                break;
            }
            case HuaweiEVRI.ACTION_HEART_BEAT: {
                str = generateHeartBeatRequest(p_map);
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
                _log.debug("generateRequest", "Exited Request String: str=" + str);
        }// end of finally
         // For the local testing request string ended with colon, remove this
         // colon while the delivery of code
        return str + ";";
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
    public HashMap parseResponse(int p_action, String p_responseStr) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Entered p_action=" + p_action + " p_responseStr=" + p_responseStr);

        HashMap map = null;
        try {
            switch (p_action) {
            case HuaweiEVRI.ACTION_LOGIN: {
                map = parseLoginResponse(p_responseStr);
                break;
            }
            case HuaweiEVRI.ACTION_LOGOUT: {
                map = parseLogoutResponse(p_responseStr);
                break;
            }
            case HuaweiEVRI.ACTION_ACCOUNT_INFO: {
                map = parseGetAccountInfoResponse(p_responseStr);
                break;
            }
            case HuaweiEVRI.ACTION_RECHARGE_CREDIT: {
                map = parseRechargeCreditResponse(p_responseStr);
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
     * This method will return MML request message for Account info (validate
     * action).
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    private String generateGetAccountInfoRequest(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetAccountInfoRequest", "Entered p_map= " + p_map);
        String requestStr = null;
        StringBuffer sbf = null;
        sbf = new StringBuffer(1028);
        sbf.append("<ACTION>");
        sbf.append(_lineSep);
        sbf.append(HuaweiEVRI.SUBINFO);
        sbf.append(_lineSep);
        sbf.append("</ACTION>");

        sbf.append(_lineSep);
        sbf.append("<MSISDN>");
        sbf.append(_lineSep);
        sbf.append((String) p_map.get("MSISDN"));
        sbf.append(_lineSep);
        sbf.append("</MSISDN>");

        sbf.append(_lineSep);
        sbf.append("<DATE>");
        sbf.append(_lineSep);
        sbf.append(InterfaceUtil.getDateTimeFormat("dd-MM-yyyy HH:mm:ss"));
        sbf.append(_lineSep);
        sbf.append("</DATE>");

        sbf.append(_lineSep);
        sbf.append("<TXNID>");
        sbf.append(_lineSep);
        sbf.append(getValidationReconTxnID(p_map));
        sbf.append(_lineSep);
        sbf.append("</TXNID>");
        sbf.append(_lineSep);
        requestStr = sbf.toString();
        return requestStr;
    }

    /**
     * This method parse the response for Acount INfo from MML String into
     * HashMap and returns it
     * 
     * @param String
     *            p_responseStr
     * @return HashMap map
     * @throws Exception
     */
    private HashMap parseGetAccountInfoResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse", "Entered p_responseStr: " + p_responseStr);
        HashMap map = null;
        try {
            map = new HashMap();
            int index = p_responseStr.indexOf("<STATUS>");
            String result = p_responseStr.substring(index + "<STATUS>".length(), p_responseStr.indexOf("</STATUS>", index));
            map.put("STATUS", result);
            if (HuaweiEVRI.RESULT_OK.equals((result))) {
                index = p_responseStr.indexOf("<SUBSCRIBERTYPE>");
                String subscriberType = p_responseStr.substring(index + "<SUBSCRIBERTYPE>".length(), p_responseStr.indexOf("</SUBSCRIBERTYPE>", index));
                map.put("SUBSCRIBERTYPE", subscriberType);

                index = p_responseStr.indexOf("<SERVICECLASS>");
                String serviceClass = p_responseStr.substring(index + "<SERVICECLASS>".length(), p_responseStr.indexOf("</SERVICECLASS>", index));
                map.put("SERVICECLASS", serviceClass);

                index = p_responseStr.indexOf("<ACCOUNTSTATUS>");
                String accountStatus = p_responseStr.substring(index + "<ACCOUNTSTATUS>".length(), p_responseStr.indexOf("</ACCOUNTSTATUS>", index));
                map.put("ACCOUNTSTATUS", accountStatus);

                index = p_responseStr.indexOf("<TXNID>");
                String txnID = p_responseStr.substring(index + "<TXNID>".length(), p_responseStr.indexOf("</TXNID>", index));
                map.put("TXNID", txnID);

                index = p_responseStr.indexOf("<DATE>");
                String date = p_responseStr.substring(index + "<DATE>".length(), p_responseStr.indexOf("</DATE>", index));
                map.put("DATE", date);
            }
        } catch (Exception e) {
            _log.error("parseGetAccountInfoResponse", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoResponse", "Exit  map:" + map);
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
    private String generateRechargeCreditRequest(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRechargeCreditRequest", "Entered p_map= " + p_map);
        String msisdn = null;
        String cardPin = null;
        String rechargeCommand = null;
        String chargeType = null;
        String sessionID = null;
        String requestStr = null;
        String requestData = null;
        String versionNumber = null;
        String term = null;
        String rechargeService = null;
        String tsrv = null;
        String dlgCon = null;
        String rsv = null;
        String dlgCtrl = null;
        String startFlag = null;
        String inHaederTxnID = null;
        String msgHeaderLanguage = null;
        try {
            msisdn = (String) p_map.get("MSISDN");
            cardPin = (String) p_map.get("PIN");
            rechargeCommand = (String) p_map.get("RECHARGE_COMMAND");
            chargeType = (String) p_map.get("CHRGTYPE");
            sessionID = (String) p_map.get("SESSIONID");
            versionNumber = (String) p_map.get("VERSION_NUMBER");
            term = (String) p_map.get("TERM");
            rechargeService = (String) p_map.get("RECHARGE_SERIVICE");
            dlgCon = (String) p_map.get("DLGCON");
            rsv = (String) p_map.get("RSV");
            dlgCtrl = (String) p_map.get("DLGCTRL");
            tsrv = (String) p_map.get("TSRV");
            startFlag = (String) p_map.get("START_FLAG");
            inHaederTxnID = (String) p_map.get("IN_HEADER_TXN_ID");
            msgHeaderLanguage = (String) p_map.get("MSG_HEAD_LANGUAGE");
            // methods to be implemented after confirmation.
            String inReconTxnID = getINReconTxnID(p_map);

            // Preparing the header information
            StringBuffer headerInfoBuffer = new StringBuffer(versionNumber);// Version-number
            headerInfoBuffer.append(term);// TERMINAL
            headerInfoBuffer.append(spacePad(rechargeService, 8, " ", 'r'));// Recharge
                                                                            // service
            // headerInfoBuffer.append(spacePad(msgHeaderLanguage,8," ",'r'));//Language
            // Selected
            headerInfoBuffer.append(spacePad(sessionID, 8, "0", 'l'));// session
                                                                      // id
            headerInfoBuffer.append(dlgCon);// session continued
            headerInfoBuffer.append(spacePad(rsv, 4, "0", 'l'));// reserve
            headerInfoBuffer.append(spacePad(inHaederTxnID, 8, "0", 'l'));// //txn
                                                                          // id
                                                                          // in
                                                                          // header
                                                                          // of
                                                                          // request
            headerInfoBuffer.append(spacePad(dlgCtrl, 6, " ", 'r'));// represents
                                                                    // txn
                                                                    // begin,continued,end
            headerInfoBuffer.append(spacePad(tsrv, 4, "0", 'l'));// txn reserve
            String headerInfoStr = headerInfoBuffer.toString();

            // prepare MML command and service parameter
            StringBuffer rechargeDataBuffer = new StringBuffer(rechargeCommand);
            rechargeDataBuffer.append("MSISDN=" + msisdn + ",");
            rechargeDataBuffer.append("CHRGTYPE=" + chargeType + ",");
            rechargeDataBuffer.append("CARDPIN=" + cardPin);

            // Recharge request string (MML Command + operative parameters)
            requestData = rechargeDataBuffer.toString();
            // make the length of data request String to integral of 4
            requestData = lenpad(requestData);
            String headerAndCommand = headerInfoStr + requestData;
            // get checksum of MML message prepared.
            String chckSum = getCheckSum(headerAndCommand);
            requestStr = startFlag.trim() + len(headerAndCommand, 4) + headerAndCommand + chckSum;
            requestStr = requestStr + "&TransId=" + inReconTxnID;
        } catch (Exception e) {
            _log.error("generateRechargeCreditRequest", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRechargeCreditRequest", "Exiting  requestStr:" + requestStr);
        }
        return requestStr;
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
    private HashMap parseRechargeCreditResponse(String p_responseStr) throws BTSLBaseException {
        // ACK: CHGTRIG CHRG ACNT: RETN=0, DESC= Manual re-charging succeeded,
        // ATTR=BALANCE& ACTIVESTOP&VALUE&VALUEADD&VALIDITY&VALIDITYADD,
        // RESULT=55|20041201|10|10|20| 20|;
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditResponse", "Entered p_responseStr: " + p_responseStr);
        HashMap map = null;
        int index = 0;
        String status = null;
        String attr = null;
        String result = null;
        String transactionId = null;
        StringTokenizer attrToken = null;
        StringTokenizer resultToken = null;
        int attrCnt = 0;
        int resultCnt = 0;
        try {
            map = new HashMap();
            index = p_responseStr.indexOf("RETN=");
            if (index < 0)
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            status = (p_responseStr.substring(index + 5, p_responseStr.indexOf(",", index))).trim();// status
                                                                                                    // of
                                                                                                    // txn
            map.put("status", status);
            if (!HuaweiEVRI.RESULT_OK.equals(status))// if status is not OK then
                                                     // return else proceed with
                                                     // other values
                return map;
            transactionId = getResponseTransactionId(p_responseStr);// get txnid
            map.put("transaction_id", transactionId);// put txnid in map
            index = p_responseStr.indexOf("ATTR=");
            attr = p_responseStr.substring(index + 5, p_responseStr.indexOf(",", index));// get
                                                                                         // parameters
                                                                                         // queried
                                                                                         // in
                                                                                         // validate
                                                                                         // seperated
                                                                                         // by
                                                                                         // '&'.
                                                                                         // (SUBCOSID&SERVICESTOP&ACTIVESTOP&BALANCE&LANGUAGETYPE&FRAUDLOCK&SUSPENDSTOP)
            index = p_responseStr.indexOf("RESULT=");
            result = p_responseStr.substring(index + 7, p_responseStr.indexOf(";", index + 7));// get
                                                                                               // values
                                                                                               // of
                                                                                               // parameters
                                                                                               // queried
                                                                                               // in
                                                                                               // validate
                                                                                               // seperated
                                                                                               // by
                                                                                               // '|'(1|1|1|1|1|1|1|1|)
            attrToken = new StringTokenizer(attr, "&");
            resultToken = new StringTokenizer(result, "|");
            attrCnt = attrToken.countTokens();
            resultCnt = resultToken.countTokens();
            if ((attrCnt != resultCnt) || attrCnt == 0) {
                _log.error("parseRechargeCreditResponse", "Response does not have equal Number of Attributes and Result or Attributes and Result have zero count.");
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }
            while (attrToken.hasMoreTokens() && resultToken.hasMoreTokens())
                map.put(attrToken.nextElement(), resultToken.nextElement());// put
                                                                            // above
                                                                            // mentioned
                                                                            // parameter
                                                                            // names
                                                                            // and
                                                                            // values
                                                                            // in
                                                                            // map.
        } catch (BTSLBaseException be) {
            _log.error("parseRechargeCreditResponse", "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("parseRechargeCreditResponse", "Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponse", "Exit  map:" + map);
        }
        return map;
    }

    /**
     * This method will return MML request message for Login action.
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    private String generateLoginRequest(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateLoginRequest", "Entered p_map: " + p_map);
        String interfaceID = null;
        String sessionID = null;
        String requestStr = null;
        String inHaederTxnID = null;
        try {
            interfaceID = (String) p_map.get("interfaceID");
            sessionID = (String) p_map.get("sessionID");
            String msgHeaderLanguage = FileCache.getValue(interfaceID, "MSG_HEAD_LANGUAGE");
            if (InterfaceUtil.isNullString(msgHeaderLanguage)) {
                _log.error("generateLoginRequest", "MSG_HEAD_LANGUAGE is not defined in the INFile");
                throw new BTSLBaseException(this, "generateLoginRequest", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            msgHeaderLanguage = msgHeaderLanguage.trim();
            p_map.put("MSG_HEAD_LANGUAGE", msgHeaderLanguage);
            String userName = FileCache.getValue(interfaceID, "USER_NAME");
            if (InterfaceUtil.isNullString(userName)) {
                _log.error("generateLoginRequest", "USER_NAME is not defined in the INFile");
                throw new BTSLBaseException(this, "generateLoginRequest", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            userName = userName.trim();
            p_map.put("userName", userName);
            String password = FileCache.getValue(interfaceID, "PASSWORD");
            if (InterfaceUtil.isNullString(password)) {
                _log.error("generateLoginRequest", "PASSWORD is not defined in the INFile");
                throw new BTSLBaseException(this, "generateLoginRequest", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            password = password.trim();
            p_map.put("password", password);
            String versionNumber = FileCache.getValue(interfaceID, "VERSION_NUMBER");
            if (InterfaceUtil.isNullString(versionNumber)) {
                _log.error("generateLoginRequest", "VERSION_NUMBER is not defined in the INFile");
                throw new BTSLBaseException(this, "generateLoginRequest", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            versionNumber = versionNumber.trim();
            p_map.put("VERSION_NUMBER", versionNumber);
            String term = FileCache.getValue(interfaceID, "TERM");
            if (InterfaceUtil.isNullString(term)) {
                _log.error("generateLoginRequest", "TERM is not defined in the INFile");
                throw new BTSLBaseException(this, "generateLoginRequest", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            term = term.trim();
            p_map.put("TERM", term);
            String loginCommand = FileCache.getValue(interfaceID, "LOGIN_COMMAND");
            if (InterfaceUtil.isNullString(loginCommand)) {
                _log.error("generateLoginRequest", "LOGIN_COMMAND is not defined in the INFile");
                throw new BTSLBaseException(this, "generateLoginRequest", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            loginCommand = loginCommand.trim();
            p_map.put("LOGIN_COMMAND", loginCommand);
            String loginService = FileCache.getValue(interfaceID, "LOGIN_SERIVICE");
            if (InterfaceUtil.isNullString(loginService)) {
                _log.error("generateLoginRequest", "LOGIN_SERIVICE is not defined in the INFile");
                throw new BTSLBaseException(this, "generateLoginRequest", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            loginService = loginService.trim();
            p_map.put("LOGIN_SERIVICE", loginService);
            String dlgLgn = FileCache.getValue(interfaceID, "DLGLGN");
            if (InterfaceUtil.isNullString(dlgLgn)) {
                _log.error("generateLoginRequest", "DLGLGN is not defined in the INFile");
                throw new BTSLBaseException(this, "generateLoginRequest", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            dlgLgn = dlgLgn.trim();
            p_map.put("DLGLGN", dlgLgn);
            String rsv = FileCache.getValue(interfaceID, "RSV");
            if (InterfaceUtil.isNullString(rsv)) {
                _log.error("generateLoginRequest", "RSV is not defined in the INFile");
                throw new BTSLBaseException(this, "generateLoginRequest", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            rsv = rsv.trim();
            p_map.put("RSV", rsv);
            String dlgCtrl = FileCache.getValue(interfaceID, "DLGCTRL");
            if (InterfaceUtil.isNullString(dlgCtrl)) {
                _log.error("generateLoginRequest", "DLGCTRL is not defined in the INFile");
                throw new BTSLBaseException(this, "generateLoginRequest", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            dlgCtrl = dlgCtrl.trim();
            p_map.put("DLGCTRL", dlgCtrl);
            String tsrv = FileCache.getValue(interfaceID, "TSRV");
            if (InterfaceUtil.isNullString(tsrv)) {
                _log.error("generateLoginRequest", "TSRV is not defined in the INFile");
                throw new BTSLBaseException(this, "generateLoginRequest", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            tsrv = tsrv.trim();
            p_map.put("TSRV", tsrv);
            String startFlag = FileCache.getValue(interfaceID, "START_FLAG");
            if (InterfaceUtil.isNullString(startFlag)) {
                _log.error("generateLoginRequest", "START_FLAG is not defined in the INFile");
                throw new BTSLBaseException(this, "generateLoginRequest", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            startFlag = startFlag.trim();
            p_map.put("START_FLAG", startFlag);
            inHaederTxnID = (String) p_map.get("IN_HEADER_TXN_ID");
            // Preparing the header information
            StringBuffer headerInfoBuffer = new StringBuffer(versionNumber);// Version-number
            headerInfoBuffer.append(term);// TERMINAL
            headerInfoBuffer.append(spacePad(loginService, 8, " ", 'r'));// Login
                                                                         // service
            // headerInfoBuffer.append(spacePad(msgHeaderLanguage,8," ",'r'));//Language
            // Selected
            headerInfoBuffer.append(spacePad(sessionID, 8, "0", 'l'));// session
                                                                      // id
            headerInfoBuffer.append(dlgLgn);// session continued
            headerInfoBuffer.append(spacePad(rsv, 4, "0", 'l'));// reserve
            headerInfoBuffer.append(spacePad(inHaederTxnID, 8, "0", 'l'));// //txn
                                                                          // id
                                                                          // in
                                                                          // header
                                                                          // of
                                                                          // request
            headerInfoBuffer.append(spacePad(dlgCtrl, 6, " ", 'r'));// represents
                                                                    // txn
                                                                    // begin,continued,end
            headerInfoBuffer.append(spacePad(tsrv, 4, "0", 'l'));// txn reserve
            // LOGIN_COMMAND=LOGIN:PSWD=%p,USER=%u (From the INFile)
            loginCommand = loginCommand.replaceAll("%p", password);
            loginCommand = loginCommand.replaceAll("%u", userName);
            if (_log.isDebugEnabled())
                _log.debug("generateLoginRequest", "login command after putting password and user name loginCommand:" + loginCommand);
            String headerInfoStr = headerInfoBuffer.toString();
            loginCommand = lenpad(loginCommand);
            String headerAndCommand = headerInfoStr + loginCommand;
            String chckSum = getCheckSum(headerAndCommand);
            // requestStr="\'"+startFlag.trim()+"\'"+len(headerAndCommand,4)+headerInfoStr+loginCommand+chckSum;
            requestStr = startFlag + len(headerAndCommand, 4) + headerInfoStr + loginCommand + chckSum;
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("generateLoginRequest", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateLoginRequest", "Exiting  requestStr:" + requestStr);
        }
        return requestStr;
    }

    /**
     * This method will return hashMap(containing login response details ) after
     * parsing response string.
     * 
     * @param HashMap
     *            p_responseStr
     * @return String
     * @throws Exception
     */
    private HashMap parseLoginResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseLoginResponse", "Entered p_responseStr: " + p_responseStr);
        HashMap map = null;
        int index = 0;
        String status = null;
        try {
            map = new HashMap();
            index = p_responseStr.indexOf("RETN=");
            if (index < 0)
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            status = p_responseStr.substring(index + 5, p_responseStr.indexOf(",", index));
            // put the status into map
            map.put("response_status", status.trim());
            if (_log.isDebugEnabled())
                _log.debug("parseLoginResponse", "status:" + status);
        } catch (Exception e) {
            _log.error("parseLoginResponse", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseLoginResponse", "Exiting map:" + map);
        }
        return map;
    }

    /**
     * This method will return MML request message for Logout action.
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    private String generateLogoutRequest(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateLogoutRequest", "Entered p_map: " + p_map);
        String interfaceID = null;
        String sessionID = null;
        String inHaederTxnID = null;
        String userName = null;
        String requestStr = null;
        String versionNumber = null;
        String term = null;
        String tsrv = null;
        String dlgCon = null;
        String rsv = null;
        String dlgCtrl = null;
        String startFlag = null;
        String logoutCommand = null;
        String logoutService = null;
        String msgHeaderLanguage = null;
        try {
            interfaceID = (String) p_map.get("interfaceID");
            sessionID = (String) p_map.get("sessionID");
            userName = (String) p_map.get("userName");
            versionNumber = (String) p_map.get("VERSION_NUMBER");
            term = (String) p_map.get("TERM");
            rsv = (String) p_map.get("RSV");
            tsrv = (String) p_map.get("TSRV");
            dlgCtrl = (String) p_map.get("DLGCTRL");
            startFlag = (String) p_map.get("START_FLAG");
            msgHeaderLanguage = (String) p_map.get("MSG_HEAD_LANGUAGE");
            logoutCommand = FileCache.getValue(interfaceID, "LOGOUT_COMMAND");
            if (InterfaceUtil.isNullString(logoutCommand)) {
                _log.error("generateLoginRequest", "LOGIN_COMMAND is not defined in the INFile");
                throw new BTSLBaseException(this, "generateLogoutRequest", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            logoutCommand = logoutCommand.trim();
            p_map.put("LOGOUT_COMMAND", logoutCommand);
            logoutService = FileCache.getValue(interfaceID, "LOGOUT_SERIVICE");
            if (InterfaceUtil.isNullString(logoutService)) {
                _log.error("generateLoginRequest", "LOGOUT_SERIVICE is not defined in the INFile");
                throw new BTSLBaseException(this, "generateLogoutRequest", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            logoutService = logoutService.trim();
            p_map.put("LOGOUT_SERIVICE", logoutService);
            dlgCon = FileCache.getValue(interfaceID, "DLGCON");
            if (InterfaceUtil.isNullString(dlgCon)) {
                _log.error("generateLoginRequest", "DLGCON is not defined in the INFile");
                throw new BTSLBaseException(this, "generateLogoutRequest", InterfaceErrorCodesI.ERROR_BAD_REQUEST);
            }
            dlgCon = dlgCon.trim();
            p_map.put("DLGCON", dlgCon);
            inHaederTxnID = (String) p_map.get("IN_HEADER_TXN_ID");
            // Preparing the header information
            StringBuffer headerInfoBuffer = new StringBuffer(versionNumber);// Version-number
            headerInfoBuffer.append(term);// TERMINAL
            headerInfoBuffer.append(spacePad(logoutService, 8, " ", 'r'));// Logout
                                                                          // service
            // headerInfoBuffer.append(spacePad(msgHeaderLanguage,8," ",'r'));//Language
            // Selected
            headerInfoBuffer.append(spacePad(sessionID, 8, "0", 'l'));// session
                                                                      // id
            headerInfoBuffer.append(dlgCon);
            headerInfoBuffer.append(spacePad(rsv, 4, "0", 'l'));
            headerInfoBuffer.append(spacePad(inHaederTxnID, 8, "0", 'l'));
            headerInfoBuffer.append(spacePad(dlgCtrl, 6, " ", 'r'));
            headerInfoBuffer.append(spacePad(tsrv, 4, "0", 'l'));

            // LOGOUT_COMMAND=LOGOUT:USER=%u (From the INFile)
            logoutCommand = logoutCommand.replaceAll("%u", userName);
            String headerInfoStr = headerInfoBuffer.toString();
            logoutCommand = lenpad(logoutCommand);
            String headerAndCommand = headerInfoStr + logoutCommand;
            String chckSum = getCheckSum(headerAndCommand);
            // requestStr="\'"+startFlag.trim()+"\'"+len(headerAndCommand,4)+headerInfoStr+loginCommand+chckSum;
            requestStr = startFlag + len(headerAndCommand, 4) + headerInfoStr + logoutCommand + chckSum;
        } catch (Exception e) {
            _log.error("generateLogoutRequest", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateLogoutRequest", "Exiting  requestStr:" + requestStr);
        }
        return requestStr;
    }

    /**
     * This method will return hashMap(containing logout response details) after
     * parsing response string.
     * 
     * @param HashMap
     *            p_responseStr
     * @return String
     * @throws Exception
     */
    private HashMap parseLogoutResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseLogoutResponse", "Entered p_responseStr: " + p_responseStr);
        HashMap map = null;
        int index = 0;
        String response_status = null;
        try {
            map = new HashMap();
            index = p_responseStr.indexOf("RETN=");
            if (index < 0)
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            response_status = (p_responseStr.substring(index + 5, p_responseStr.indexOf(",", index))).trim();
            map.put("response_status", response_status.trim());
            if (_log.isDebugEnabled())
                _log.debug("parseLogoutResponse", "response_status:" + response_status);
        } catch (Exception e) {
            _log.error("parseLogoutResponse", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseLogoutResponse", "Exiting map:" + map);
        }
        return map;
    }

    /**
     * This method will return MML request message for ImmediateDebit action.
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    private String generateHeartBeatRequest(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateHeartBeatRequest", "Entered p_map= " + p_map);
        String requestStr = null;
        String requestData = null;
        String startFlag = null;
        try {
            requestData = (String) p_map.get("HEART_BEAT_COMMAND");
            startFlag = (String) p_map.get("START_FLAG");
            requestData = lenpad(requestData);
            String chckSum = getCheckSum(requestData);
            requestStr = startFlag + len(requestData, 4) + requestData + chckSum;
        } catch (Exception e) {
            _log.error("generateHeartBeatRequest", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateHeartBeatRequest", "Exiting  requestStr:" + requestStr);
        }
        return requestStr;
    }

    /**
     * Calculates the length of the message and adds the specified character in
     * the left or
     * the right side if it smaller than required.
     * 
     * @param String
     *            p_messageStr
     * @param int p_padLength
     * @param String
     *            p_padStr
     * @param char p_direction
     * @return String
     */
    private String spacePad(String p_messageStr, int p_padLength, String p_padStr, char p_direction) throws Exception {
        StringBuffer padStrBuffer = null;
        try {
            if (p_messageStr.length() < p_padLength) {
                int paddingLength = p_padLength - p_messageStr.length();
                padStrBuffer = new StringBuffer(10);
                if (p_direction == 'r')
                    padStrBuffer.append(p_messageStr);
                for (int i = 0; i < paddingLength; i++)
                    padStrBuffer.append(p_padStr);
                if (p_direction == 'l')
                    padStrBuffer.append(p_messageStr);
                p_messageStr = padStrBuffer.toString();
            }
        } catch (Exception e) {
            _log.error("spacePad", "Exception e:" + e.getMessage());
            throw e;
        }
        return p_messageStr;
    }

    /**
     * calculates the length of the string, if it is not divisible by 4, it
     * increases the length
     * and also adds spaces in the increased fields.
     * 
     * @param String
     *            p_messageStr
     * @return String
     */
    private String lenpad(String p_messageStr) throws Exception {
        int paddingLength = 0;
        StringBuffer messageStrBuffer = null;
        try {
            int messageStrLength = p_messageStr.length();
            if (messageStrLength % 4 != 0)
                while (messageStrLength % 4 != 0)
                    messageStrLength++;
            if (p_messageStr.length() < messageStrLength) {
                messageStrBuffer = new StringBuffer(1024);
                messageStrBuffer.append(p_messageStr);
                paddingLength = messageStrLength - p_messageStr.length();
                for (int j = 0; j < paddingLength; j++)
                    messageStrBuffer.append(" ");
                p_messageStr = messageStrBuffer.toString();
            }
        } catch (Exception e) {
            _log.error("lenpad", "Exception e:" + e.getMessage());
            throw e;
        }
        return p_messageStr;
    }

    /**
     * This method calculates the length of a string.
     * Unless the length is not divisible by 4,it increases the length value and
     * returns it in the hexa-decimal format.
     * It adds zeros in-front until the length becomes 4.
     * 
     * @param String
     *            p_messageStr
     * @param int p_pad
     * @return String
     */
    private String len(String p_messageStr, int p_pad) throws Exception {
        String messageStrLength = null;
        StringBuffer msgLengthBuffer = null;
        try {
            int i = p_messageStr.length();// length of string
            if (i % 4 != 0)
                while (i % 4 != 0)
                    i++;
            messageStrLength = Integer.toHexString(i);
            if (messageStrLength.length() < p_pad) {
                msgLengthBuffer = new StringBuffer(10);
                int paddingLength = p_pad - messageStrLength.length();
                for (int j = 0; j < paddingLength; j++)
                    msgLengthBuffer.append("0");
                msgLengthBuffer.append(messageStrLength);
                messageStrLength = msgLengthBuffer.toString();
            }

        } catch (Exception e) {
            _log.error("len", "Exception e:" + e.getMessage());
            throw e;
        }
        return messageStrLength;
    }

    /**
     * This method calculates checksum of request MML message prepared.
     * 
     * @param p_headerAndDataString
     * @return String
     * @throws Exception
     */
    private String getCheckSum(String p_headerAndDataString) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getCheckSum", "Entered p_headerAndDataString:" + p_headerAndDataString);
        int[] c = null;
        int index = 0;
        StringBuffer hexBuffer = null;
        try {
            c = new int[4];
            index = p_headerAndDataString.indexOf("1.01");
            // index is set to 0,because in Hearbeat message version is not set.
            if (index < 0)
                index = 0;
            for (int k = 0; k < 4; k++)
                c[k] = 0;
            for (int j = 0, stringLength = p_headerAndDataString.length(); j < stringLength - index; j = j + 4) {
                c[0] = c[0] ^ ((int) p_headerAndDataString.charAt(index + j));
                c[1] = c[1] ^ ((int) p_headerAndDataString.charAt(index + j + 1));
                c[2] = c[2] ^ ((int) p_headerAndDataString.charAt(index + j + 2));
                c[3] = c[3] ^ ((int) p_headerAndDataString.charAt(index + j + 3));
            }
            hexBuffer = new StringBuffer(1028);
            for (int k = 0; k < 4; k = k + 1)
                hexBuffer.append(Integer.toHexString(~c[k] & (0x0ff)));
            hexBuffer.toString().toUpperCase();
        } catch (Exception e) {
            _log.error("getCheckSum", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getCheckSum", "Exited");
        }
        return hexBuffer.toString().toUpperCase();
    }

    /**
     * This method parses transaction id from response string.
     * 
     * @param p_responseStr
     * @return String
     * @throws Exception
     */
    private String getResponseTransactionId(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getResponseTransactionId", "Entered p_responseStr:" + p_responseStr);
        int index = 0;
        String transIdStr = null;
        try {
            index = p_responseStr.indexOf("DLGCON");
            transIdStr = p_responseStr.substring(index + 10, p_responseStr.indexOf("TXEND"));
        } catch (Exception e) {
            _log.error("getResponseTransactionId", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getResponseTransactionId", "Exited transIdStr:" + transIdStr);
        }
        return transIdStr.trim();
    }

    private String getValidationReconTxnID(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getValidationReconTxnID", "Enetered p_requestMap =" + p_requestMap);
        String inReconID = null;
        inReconID = ((String) p_requestMap.get("TRANSACTION_ID"));
        p_requestMap.put("IN_RECON_ID", inReconID);

        if (_log.isDebugEnabled())
            _log.debug("getValidationReconTxnID", "exited p_requestMap =" + p_requestMap);
        return inReconID;
    }

    /**
     * This method will generate Reconciliation Id for each transaction between
     * IN and INHandler module of pretups.
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String getINReconTxnID(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getINReconTxnID", "Enetered p_requestMap =" + p_requestMap);
        String inReconID = null;
        Date date = null;
        SimpleDateFormat sdf = null;
        try {
            date = new Date();
            sdf = new SimpleDateFormat("yyMMddHHmmssSSSSS");
            inReconID = sdf.format(date);
            p_requestMap.put("IN_RECON_ID", inReconID);
        }// end of try block
        catch (Exception e) {
            _log.error("getINReconTxnID", "Exception e=" + e);
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getINReconTxnID", "Exited inReconID =" + inReconID);
        }// end of finally
        return inReconID;
    }// end of getINReconTxnID*/

    /**
     * This method will return Date & Time in required Format
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String getDateTimeFormat(String format) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getDateTimeFormat", "Enetered: format= " + format);
        String dateString = null;
        Date date = null;
        SimpleDateFormat sdf = null;
        try {
            date = new Date();
            sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            dateString = sdf.format(date);
        }// end of try block
        catch (Exception e) {
            _log.error("getDateTimeFormat", "Exception e=" + e);
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getDateTimeFormat", "Exited dateString =" + dateString);
        }// end of finally
        return dateString;
    }// end of getDateTimeFormat*/
}
