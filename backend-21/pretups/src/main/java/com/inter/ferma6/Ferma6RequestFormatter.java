package com.inter.ferma6;

/**
 * @(#)Ferma6RequestFormatter.java
 *                                 Copyright(c) 2005, Bharti Telesoft Int.
 *                                 Public Ltd.
 *                                 All Rights Reserved
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Manoj Kumar Jan 24,2006 Initial Creation
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 ------
 *                                 A Ferma6RequestFormatter object
 * 
 **/

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceUtil;
import java.util.HashMap;

public class Ferma6RequestFormatter {
    public final static int ACTION_LOGIN = 0;

    public final static int ACTION_ACCOUNT_INFO = 1;

    public final static int ACTION_RECHARGE_BALANCE = 2;

    public final static int ACTION_BALANCE_ADJUST = 3;

    public final static int ACTION_LOGOUT = 4;

    public final static int ACTION_ACCOUNT_INFO_2 = 5;

    public Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Get IN Reconciliation Txn ID
     * 
     * @param p_requestMap
     * @return
     */
    private String getINReconTxnID(HashMap p_requestMap) {
        // return ((String)p_requestMap.get("IN_TXN_ID"));
        String inReconID = null;
        String userType = (String) p_requestMap.get("USER_TYPE");
        if (userType != null)
            inReconID = ((String) p_requestMap.get("TRANSACTION_ID") + userType);
        else
            inReconID = ((String) p_requestMap.get("TRANSACTION_ID"));
        p_requestMap.put("IN_RECON_ID", inReconID);
        return inReconID;
    }

    /**
     * This method construct the request in XML String from HashMap
     * 
     * @param int action
     * @param HashMap
     *            map
     * @return String str
     */
    public String generateRequest(int action, HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Entered action=" + action + "map=" + map);
        String str = null;
        switch (action) {
        case ACTION_LOGIN: {
            str = generateLoginRequest(map);
            break;
        }
        case ACTION_ACCOUNT_INFO: {
            str = generateGetAccountInfoRequest(map);
            break;
        }
        case ACTION_ACCOUNT_INFO_2: {
            str = generateGetAccountInfoRequest2(map);
            break;
        }
        case ACTION_RECHARGE_BALANCE: {
            str = generateRechargeBalanceRequest(map);
            break;
        }
        case ACTION_BALANCE_ADJUST: {
            str = generateBalanceAdjustmentRequest(map);
            break;
        }
        case ACTION_LOGOUT: {
            str = generateLogoutRequest(map);
            break;
        }
        }
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Exited str=" + str);
        return str;
    }

    /**
     * This method parse the response from XML String into HashMap
     * 
     * @param action
     *            int
     * @param responseStr
     *            String
     * @return map HashMap
     */
    public HashMap parseResponse(int action, String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Entered action=" + action + "responseStr=" + responseStr);

        HashMap map = null;
        switch (action) {
        case ACTION_LOGIN: {
            map = parseLoginResponse(responseStr);
            break;
        }
        case ACTION_ACCOUNT_INFO: {
            map = parseGetAccountInfoResponse(responseStr);
            break;
        }
        case ACTION_ACCOUNT_INFO_2: {
            map = parseGetAccountInfoResponse2(responseStr);
            break;
        }
        case ACTION_RECHARGE_BALANCE: {
            map = parseRechargeBalanceResponse(responseStr);
            break;
        }
        case ACTION_BALANCE_ADJUST: {
            map = parseBalanceAdjustmentResponse(responseStr);
            break;
        }
        case ACTION_LOGOUT: {
            map = parseLogoutResponse(responseStr);
            break;
        }
        }
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Exited map=" + map);
        return map;
    }

    /**
     * This method construct LoginRequest in XML format from map
     * 
     * @param map
     *            HsahMap
     * @return requestStr String
     */
    private String generateLoginRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateLoginRequest", "Entered userName: " + map.get("UserName") + " password:" + map.get("Password") + " protocol: " + map.get("ProtocolVersion") + "transactionId:" + map.get("IN_TXN_ID"));
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><envelope  action=\"login\"><LoginRequest xmlns=\"http://www.ferma.fr/ppg/logic/service/login\"><ns1:TransactionId xmlns:ns1=\"http://www.ferma.fr/ppg/logic/service\">" + (String) map.get("IN_TXN_ID") + "</ns1:TransactionId>");
            sbf.append("<UserName>" + map.get("UserName") + "</UserName>");
            sbf.append("<Password>" + map.get("Password") + "</Password>");
            sbf.append("<ProtocolVersion>" + map.get("ProtocolVersion") + "</ProtocolVersion>");
            sbf.append("</LoginRequest></envelope>");
            requestStr = sbf.toString();
            return requestStr;
        } catch (Exception e) {
            _log.error("generateLoginRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateLoginRequest", "Entered requestStr: " + requestStr);
        }
    }

    /**
     * This method parse LoginResponse from XML String to HashMap
     * 
     * @param responseStr
     *            String
     * @return map HsahMap
     */
    private HashMap parseLoginResponse(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseLoginResponse", "Entered responseStr: " + responseStr);
        HashMap map = null;
        try {
            map = new HashMap();
            int index = responseStr.indexOf("<ser:Status xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">");
            if (index != -1) {
                String status = responseStr.substring(index + "<ser:Status xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">".length(), responseStr.indexOf("</ser:Status>", index));
                map.put("Status", status);
            }
            index = responseStr.indexOf("<ser:TransactionId xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">");
            if (index != -1) {
                String transactionId = responseStr.substring(index + "<ser:TransactionId xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">".length(), responseStr.indexOf("</ser:TransactionId>", index));
                map.put("TransactionId", transactionId);
            }
            index = responseStr.indexOf("<ser:InterfaceId xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">");
            if (index != -1) {
                String interfaceId = responseStr.substring(index + "<ser:InterfaceId xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">".length(), responseStr.indexOf("</ser:InterfaceId>", index));
                map.put("InterfaceId", interfaceId);
            }
            return map;
        } catch (Exception e) {
            _log.error("parseLoginResponse", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseLoginResponse", "Exiting map: " + map);
        }
    }

    /**
     * This method construct GetAccountInfoRequest in XML format from Map
     * 
     * @param map
     *            HsahMap
     * @return requestStr String
     */
    private String generateGetAccountInfoRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetAccountInfoRequest", "Entered Map: " + map);
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><envelope  action=\"getAccountInfo\"><AccountInfoGettingRequest xmlns=\"http://www.ferma.fr/ppg/logic/service/accountinfo\"><ns1:TransactionId xmlns:ns1=\"http://www.ferma.fr/ppg/logic/service\">" + getINReconTxnID(map) + "</ns1:TransactionId>");
            sbf.append("<InterfaceId>" + map.get("FERMA_INTERFACE_ID") + "</InterfaceId>");
            sbf.append("<AccessIdentifier>" + InterfaceUtil.getFilterMSISDN((String) map.get("INTERFACE_ID"), (String) map.get("MSISDN")) + "</AccessIdentifier>");
            sbf.append("<AccessType>" + map.get("AccessType") + "</AccessType>");
            sbf.append("<Balance><BalanceId>" + InterfaceUtil.NullToString((String) map.get("BalanceId")) + "</BalanceId></Balance>");
            sbf.append("</AccountInfoGettingRequest></envelope>");
            requestStr = sbf.toString();
            return requestStr;
        } catch (Exception e) {
            _log.error("generateGetAccountInfoRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateGetAccountInfoRequest", "Exiting requestStr: " + requestStr);
        }
    }

    /**
     * This method parse GetAccountInfoResponse from XML String to HashMap
     * 
     * @param responseStr
     *            String
     * @return map HsahMap
     */
    private HashMap parseGetAccountInfoResponse(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse", "Entered responseStr: " + responseStr);
        HashMap map = null;
        try {
            map = new HashMap();
            int index = responseStr.indexOf("<ser:Status>");
            String status = responseStr.substring(index + "<ser:Status>".length(), responseStr.indexOf("</ser:Status>", index));
            map.put("Status", status);
            index = responseStr.indexOf("<ser:TransactionId>");
            String transactionId = responseStr.substring(index + "<ser:TransactionId>".length(), responseStr.indexOf("</ser:TransactionId>", index));
            map.put("TransactionId", transactionId);
            if (status.equals("0")) {
                index = responseStr.indexOf("<ser:InterfaceId>");
                if (index != -1) {
                    String interfaceId = responseStr.substring(index + "<ser:InterfaceId>".length(), responseStr.indexOf("</ser:InterfaceId>", index));
                    map.put("InterfaceId", interfaceId);
                }
                index = responseStr.indexOf("<ser:AccessIdentifier>");
                if (index != -1) {
                    String accessIdentifier = responseStr.substring(index + "<ser:AccessIdentifier>".length(), responseStr.indexOf("</ser:AccessIdentifier>", index));
                    map.put("AccessIdentifier", accessIdentifier);
                }
                index = responseStr.indexOf("<ser:AccessType>");
                if (index != -1) {
                    String accessType = responseStr.substring(index + "<ser:AccessType>".length(), responseStr.indexOf("</ser:AccessType>", index));
                    map.put("AccessType", accessType);
                }
                index = responseStr.indexOf("<ser:AccountId>");
                if (index != -1) {
                    String accountId = responseStr.substring(index + "<ser:AccountId>".length(), responseStr.indexOf("</ser:AccountId>", index));
                    map.put("AccountId", accountId);
                }
                index = responseStr.indexOf("<ser:Profil>");
                if (index != -1) {
                    String profile = responseStr.substring(index + "<ser:Profil>".length(), responseStr.indexOf("</ser:Profil>", index));
                    map.put("Profile", profile);
                }
                index = responseStr.indexOf("<ser:AccountLanguage>");
                if (index != -1) {
                    String accountLanguage = responseStr.substring(index + "<ser:AccountLanguage>".length(), responseStr.indexOf("</ser:AccountLanguage>", index));
                    map.put("AccountLanguage", accountLanguage);
                }
                index = responseStr.indexOf("<ser:AccountStatus>");
                if (index != -1) {
                    String accountStatus = responseStr.substring(index + "<ser:AccountStatus>".length(), responseStr.indexOf("</ser:AccountStatus>", index));
                    map.put("AccountStatus", accountStatus);
                }
                index = responseStr.indexOf("<ser:LockStatus>");
                if (index != -1) {
                    String lockStatus = responseStr.substring(index + "<ser:LockStatus>".length(), responseStr.indexOf("</ser:LockStatus>", index));
                    map.put("LockStatus", lockStatus);
                    map.put("LOCK_FLAG", lockStatus);
                }
                index = responseStr.indexOf("<ser:TariffPlan>");
                if (index != -1) {
                    String tariffPlan = responseStr.substring(index + "<ser:TariffPlan>".length(), responseStr.indexOf("</ser:TariffPlan>", index));
                    map.put("TariffPlan", tariffPlan);
                }
                index = responseStr.indexOf("<ser:Balances><ser:Balance><ser:BalanceId>");
                if (index != -1) {
                    String balanceId = responseStr.substring(index + "<ser:Balances><ser:Balance><ser:BalanceId>".length(), responseStr.indexOf("</ser:BalanceId>", index));
                    map.put("BalanceId", balanceId);
                }
                index = responseStr.indexOf("<ser:LifeCycle>");
                if (index != -1) {
                    String lifeCycle = responseStr.substring(index + "<ser:LifeCycle>".length(), responseStr.indexOf("</ser:LifeCycle>", index));
                    map.put("LifeCycle", lifeCycle);
                }
                index = responseStr.indexOf("<ser:Option>");
                if (index != -1) {
                    String option = responseStr.substring(index + "<ser:Option>".length(), responseStr.indexOf("</ser:Option>", index));
                    map.put("Option", option);
                }
                index = responseStr.indexOf("<ser:Amount>");
                if (index != -1) {
                    String amount = responseStr.substring(index + "<ser:Amount>".length(), responseStr.indexOf("</ser:Amount>", index));
                    map.put("Amount", amount);
                }
                index = responseStr.indexOf("<ser:UnitType>");
                if (index != -1) {
                    String unitType = responseStr.substring(index + "<ser:UnitType>".length(), responseStr.indexOf("</ser:UnitType>", index));
                    map.put("UnitType", unitType);
                }
                index = responseStr.indexOf("<ser:ValidityDate>");
                if (index != -1) {
                    String validityDate = responseStr.substring(index + "<ser:ValidityDate>".length(), responseStr.indexOf("</ser:ValidityDate>", index));
                    map.put("ValidityDate", validityDate);
                }
                index = responseStr.indexOf("<ser:GraceDate>");
                if (index != -1) {
                    String graceDate = responseStr.substring(index + "<ser:GraceDate>".length(), responseStr.indexOf("</ser:GraceDate>", index));
                    map.put("GraceDate", graceDate);
                }
                index = responseStr.indexOf("<ser:State>");
                if (index != -1) {
                    String currentState = responseStr.substring(index + "<ser:State>".length(), responseStr.indexOf("</ser:State>", index));
                    map.put("CurrentState", currentState);
                }

            }
            return map;
        } catch (Exception e) {
            _log.error("parseGetAccountInfoResponse", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoResponse", "Exiting map: " + map);
        }
    }

    /**
     * This method construct GetAccountInfoRequest2 in XML format from Map
     * 
     * @param map
     *            HsahMap
     * @return requestStr String
     */
    private String generateGetAccountInfoRequest2(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetAccountInfoRequest2", "Entered Map: " + map);
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><envelope  action=\"getAccountInfo2\"><GettingAccountInfoRequest2 xmlns=\"http://www.ferma.fr/ppg/logic/service/accountinfo2\"><ns1:TransactionId xmlns:ns1=\"http://www.ferma.fr/ppg/logic/service\">" + getINReconTxnID(map) + "</ns1:TransactionId>");
            sbf.append("<InterfaceId>" + map.get("FERMA_INTERFACE_ID") + "</InterfaceId>");
            sbf.append("<AccessIdentifier>" + InterfaceUtil.getFilterMSISDN((String) map.get("INTERFACE_ID"), (String) map.get("MSISDN")) + "</AccessIdentifier>");
            sbf.append("<AccessType>" + map.get("AccessType") + "</AccessType>");
            sbf.append("<Balance><BalanceId>" + InterfaceUtil.NullToString((String) map.get("BalanceId")) + "</BalanceId></Balance>");
            sbf.append("</GettingAccountInfoRequest2></envelope>");
            requestStr = sbf.toString();
            return requestStr;
        } catch (Exception e) {
            _log.error("generateGetAccountInfoRequest2", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateGetAccountInfoRequest2", "Exiting requestStr: " + requestStr);
        }
    }

    /**
     * This method parse GetAccountInfoResponse2 from XML String to HashMap
     * 
     * @param responseStr
     *            String
     * @return map HsahMap
     */
    private HashMap parseGetAccountInfoResponse2(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse2", "Entered responseStr: " + responseStr);
        HashMap map = null;
        try {
            map = new HashMap();
            int index = responseStr.indexOf("<ser:Status>");
            String status = responseStr.substring(index + "<ser:Status>".length(), responseStr.indexOf("</ser:Status>", index));
            map.put("Status", status);
            index = responseStr.indexOf("<ser:TransactionId>");
            String transactionId = responseStr.substring(index + "<ser:TransactionId>".length(), responseStr.indexOf("</ser:TransactionId>", index));
            map.put("TransactionId", transactionId);
            if (status.equals("0")) {
                index = responseStr.indexOf("<ser:InterfaceId>");
                if (index != -1) {
                    String interfaceId = responseStr.substring(index + "<ser:InterfaceId>".length(), responseStr.indexOf("</ser:InterfaceId>", index));
                    map.put("InterfaceId", interfaceId);
                }
                index = responseStr.indexOf("<ser:AccessIdentifier>");
                if (index != -1) {
                    String accessIdentifier = responseStr.substring(index + "<ser:AccessIdentifier>".length(), responseStr.indexOf("</ser:AccessIdentifier>", index));
                    map.put("AccessIdentifier", accessIdentifier);
                }
                index = responseStr.indexOf("<ser:AccessType>");
                if (index != -1) {
                    String accessType = responseStr.substring(index + "<ser:AccessType>".length(), responseStr.indexOf("</ser:AccessType>", index));
                    map.put("AccessType", accessType);
                }
                index = responseStr.indexOf("<ser:AccountId>");
                if (index != -1) {
                    String accountId = responseStr.substring(index + "<ser:AccountId>".length(), responseStr.indexOf("</ser:AccountId>", index));
                    map.put("AccountId", accountId);
                }
                index = responseStr.indexOf("<ser:Profil>");
                if (index != -1) {
                    String profile = responseStr.substring(index + "<ser:Profil>".length(), responseStr.indexOf("</ser:Profil>", index));
                    map.put("Profile", profile);
                }
                index = responseStr.indexOf("<ser:AccountLanguage>");
                if (index != -1) {
                    String accountLanguage = responseStr.substring(index + "<ser:AccountLanguage>".length(), responseStr.indexOf("</ser:AccountLanguage>", index));
                    map.put("AccountLanguage", accountLanguage);
                }
                index = responseStr.indexOf("<ser:AccountStatus>");
                if (index != -1) {
                    String accountStatus = responseStr.substring(index + "<ser:AccountStatus>".length(), responseStr.indexOf("</ser:AccountStatus>", index));
                    map.put("AccountStatus", accountStatus);
                }
                index = responseStr.indexOf("<ser:LockStatus>");
                if (index != -1) {
                    String lockStatus = responseStr.substring(index + "<ser:LockStatus>".length(), responseStr.indexOf("</ser:LockStatus>", index));
                    map.put("LockStatus", lockStatus);
                    map.put("LOCK_FLAG", lockStatus);
                }
                index = responseStr.indexOf("<ser:TariffPlan>");
                if (index != -1) {
                    String tariffPlan = responseStr.substring(index + "<ser:TariffPlan>".length(), responseStr.indexOf("</ser:TariffPlan>", index));
                    map.put("TariffPlan", tariffPlan);
                }
                index = responseStr.indexOf("<ser:Balances><ser:Balance><ser:BalanceId>");
                if (index != -1) {
                    String balanceId = responseStr.substring(index + "<ser:Balances><ser:Balance><ser:BalanceId>".length(), responseStr.indexOf("</ser:BalanceId>", index));
                    map.put("BalanceId", balanceId);
                }
                index = responseStr.indexOf("<ser:LifeCycle>");
                if (index != -1) {
                    String lifeCycle = responseStr.substring(index + "<ser:LifeCycle>".length(), responseStr.indexOf("</ser:LifeCycle>", index));
                    map.put("LifeCycle", lifeCycle);
                }
                index = responseStr.indexOf("<ser:Option>");
                if (index != -1) {
                    String option = responseStr.substring(index + "<ser:Option>".length(), responseStr.indexOf("</ser:Option>", index));
                    map.put("Option", option);
                }
                index = responseStr.indexOf("<ser:Amount>");
                if (index != -1) {
                    String amount = responseStr.substring(index + "<ser:Amount>".length(), responseStr.indexOf("</ser:Amount>", index));
                    map.put("Amount", amount);
                }
                index = responseStr.indexOf("<ser:UnitType>");
                if (index != -1) {
                    String unitType = responseStr.substring(index + "<ser:UnitType>".length(), responseStr.indexOf("</ser:UnitType>", index));
                    map.put("UnitType", unitType);
                }
                index = responseStr.indexOf("<ser:ValidityDate>");
                if (index != -1) {
                    String validityDate = responseStr.substring(index + "<ser:ValidityDate>".length(), responseStr.indexOf("</ser:ValidityDate>", index));
                    map.put("ValidityDate", validityDate);
                }
                index = responseStr.indexOf("<ser:GraceDate>");
                if (index != -1) {
                    String graceDate = responseStr.substring(index + "<ser:GraceDate>".length(), responseStr.indexOf("</ser:GraceDate>", index));
                    map.put("GraceDate", graceDate);
                }
                index = responseStr.indexOf("<ser:State>");
                if (index != -1) {
                    String currentState = responseStr.substring(index + "<ser:State>".length(), responseStr.indexOf("</ser:State>", index));
                    map.put("CurrentState", currentState);
                }

                index = responseStr.indexOf("<ser:Icc>");
                if (index != -1) {
                    String iccState = responseStr.substring(index + "<ser:Icc>".length(), responseStr.indexOf("</ser:Icc>", index));
                    map.put("Icc", iccState);
                }

                index = responseStr.indexOf("<ser:FirstCallDate>");
                if (index != -1) {
                    String firstCallDate = responseStr.substring(index + "<ser:FirstCallDate>".length(), responseStr.indexOf("</ser:FirstCallDate>", index));
                    map.put("FirstCallDate", firstCallDate);
                }
            }
            return map;
        } catch (Exception e) {
            _log.error("parseGetAccountInfoResponse2", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoResponse2", "Exiting map: " + map);
        }
    }

    /**
     * This method construct RechargeBalanceRequest in XML format from map
     * 
     * @param map
     *            HsahMap
     * @return requestStr String
     */
    private String generateRechargeBalanceRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRechargeBalanceRequest", "Entered map=" + map);
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><envelope  action=\"balanceRecharge\"><BalanceRechargingRequest xmlns=\"http://www.ferma.fr/ppg/logic/service/recharging\"><ns1:TransactionId xmlns:ns1=\"http://www.ferma.fr/ppg/logic/service\">" + getINReconTxnID(map) + "</ns1:TransactionId>");
            sbf.append("<InterfaceId>" + map.get("FERMA_INTERFACE_ID") + "</InterfaceId>");
            sbf.append("<AccessIdentifier>" + InterfaceUtil.getFilterMSISDN((String) map.get("INTERFACE_ID"), (String) map.get("MSISDN")) + "</AccessIdentifier>");
            sbf.append("<AccessType>" + map.get("AccessType") + "</AccessType>");
            sbf.append("<AccountId>" + InterfaceUtil.NullToString((String) map.get("AccountId")) + "</AccountId>");
            sbf.append("<Balances><Balance><BalanceId>" + InterfaceUtil.NullToString((String) map.get("BalanceId")) + "</BalanceId>");
            sbf.append("<RechargeValue>" + map.get("CARD_GROUP") + "</RechargeValue>");
            sbf.append("<UnitType>" + InterfaceUtil.NullToString((String) map.get("UnitType")) + "</UnitType></Balance></Balances>");
            sbf.append("</BalanceRechargingRequest></envelope>");
            requestStr = sbf.toString();
            return requestStr;
        } catch (Exception e) {
            _log.error("generateRechargeBalanceRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRechargeBalanceRequest", "Exited requestStr: " + requestStr);
        }
    }

    /**
     * This method parse RechargeBalanceResponse from XML string to HashMap
     * 
     * @param responseStr
     *            String
     * @return map HsahMap
     */
    private HashMap parseRechargeBalanceResponse(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeBalanceResponse", "Entered responseStr: " + responseStr);
        HashMap map = null;
        try {
            map = new HashMap();
            String status = null;
            int index = responseStr.indexOf("<ser:Status>");
            if (index != -1) {
                status = responseStr.substring(index + "<ser:Status>".length(), responseStr.indexOf("</ser:Status>", index));
                map.put("Status", status);
            }
            index = responseStr.indexOf("<ser:TransactionId>");
            if (index != -1) {
                String transactionId = responseStr.substring(index + "<ser:TransactionId>".length(), responseStr.indexOf("</ser:TransactionId>", index));
                map.put("TransactionId", transactionId);
            }
            if (status.equals("0") || status.equals("20")) {
                index = responseStr.indexOf("<ser:InterfaceId>");
                if (index != -1) {
                    String interfaceId = responseStr.substring(index + "<ser:InterfaceId>".length(), responseStr.indexOf("</ser:InterfaceId>", index));
                    map.put("InterfaceId", interfaceId);
                }
                index = responseStr.indexOf("<ser:AccessIdentifier>");
                if (index != -1) {
                    String accessIdentifier = responseStr.substring(index + "<ser:AccessIdentifier>".length(), responseStr.indexOf("</ser:AccessIdentifier>", index));
                    map.put("AccessIdentifier", accessIdentifier);
                }
                index = responseStr.indexOf("<ser:AccessType>");
                if (index != -1) {
                    String accessType = responseStr.substring(index + "<ser:AccessType>".length(), responseStr.indexOf("</ser:AccessType>", index));
                    map.put("AccessType", accessType);
                }
                index = responseStr.indexOf("<ser:AccountId>");
                if (index != -1) {
                    String accountId = responseStr.substring(index + "<ser:AccountId>".length(), responseStr.indexOf("</ser:AccountId>", index));
                    map.put("AccountId", accountId);
                }
                index = responseStr.indexOf("<ser:Profil>");
                if (index != -1) {
                    String profile = responseStr.substring(index + "<ser:Profil>".length(), responseStr.indexOf("</ser:Profil>", index));
                    map.put("Profile", profile);
                }
                index = responseStr.indexOf("<ser:AccountLanguage>");
                if (index != -1) {
                    String accountLanguage = responseStr.substring(index + "<ser:AccountLanguage>".length(), responseStr.indexOf("</ser:AccountLanguage>", index));
                    map.put("AccountLanguage", accountLanguage);
                }
                index = responseStr.indexOf("<ser:AccountStatus>");
                if (index != -1) {
                    String accountStatus = responseStr.substring(index + "<ser:AccountStatus>".length(), responseStr.indexOf("</ser:AccountStatus>", index));
                    map.put("AccountStatus", accountStatus);
                }
                index = responseStr.indexOf("<ser:LockStatus>");
                if (index != -1) {
                    String lockStatus = responseStr.substring(index + "<ser:LockStatus>".length(), responseStr.indexOf("</ser:LockStatus>", index));
                    map.put("LockStatus", lockStatus);
                    map.put("LOCK_FLAG", lockStatus);
                }
                index = responseStr.indexOf("<ser:TariffPlan>");
                if (index != -1) {
                    String tariffPlan = responseStr.substring(index + "<ser:TariffPlan>".length(), responseStr.indexOf("</ser:TariffPlan>", index));
                    map.put("TariffPlan", tariffPlan);
                }
                index = responseStr.indexOf("<ser:Balances><ser:Balance><ser:BalanceId>");
                if (index != -1) {
                    String balanceId = responseStr.substring(index + "<ser:Balances><ser:Balance><ser:BalanceId>".length(), responseStr.indexOf("</ser:BalanceId>", index));
                    map.put("BalanceId", balanceId);
                }
                index = responseStr.indexOf("<ser:LifeCycle>");
                if (index != -1) {
                    String lifeCycle = responseStr.substring(index + "<ser:LifeCycle>".length(), responseStr.indexOf("</ser:LifeCycle>", index));
                    map.put("LifeCycle", lifeCycle);
                }
                index = responseStr.indexOf("<ser:Option>");
                if (index != -1) {
                    String option = responseStr.substring(index + "<ser:Option>".length(), responseStr.indexOf("</ser:Option>", index));
                    map.put("Option", option);
                }
                index = responseStr.indexOf("<ser:Amount>");
                if (index != -1) {
                    String amount = responseStr.substring(index + "<ser:Amount>".length(), responseStr.indexOf("</ser:Amount>", index));
                    map.put("Amount", amount);
                }
                index = responseStr.indexOf("<ser:UnitType>");
                if (index != -1) {
                    String unitType = responseStr.substring(index + "<ser:UnitType>".length(), responseStr.indexOf("</ser:UnitType>", index));
                    map.put("UnitType", unitType);
                }
                index = responseStr.indexOf("<ser:ValidityDate>");
                if (index != -1) {
                    String validityDate = responseStr.substring(index + "<ser:ValidityDate>".length(), responseStr.indexOf("</ser:ValidityDate>", index));
                    map.put("ValidityDate", validityDate);
                }
                index = responseStr.indexOf("<ser:GraceDate>");
                if (index != -1) {
                    String graceDate = responseStr.substring(index + "<ser:GraceDate>".length(), responseStr.indexOf("</ser:GraceDate>", index));
                    map.put("GraceDate", graceDate);
                }
                index = responseStr.indexOf("<ser:State>");
                if (index != -1) {
                    String currentState = responseStr.substring(index + "<ser:State>".length(), responseStr.indexOf("</ser:State>", index));
                    map.put("CurrentState", currentState);
                }
            }
            return map;
        } catch (Exception e) {
            _log.error("parseRechargeBalanceResponse", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeBalanceResponse", "Exiting map: " + map);
        }
    }

    /**
     * This method construct BalanceAdjustmentRequest in XML format from map
     * 
     * @param map
     *            HsahMap
     * @return requestStr String
     */
    private String generateBalanceAdjustmentRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateBalanceAdjustmentRequest", "Entered map=" + map);
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><envelope  action=\"balanceAdjustment\"><BalanceAdjustmentRequest xmlns=\"http://www.ferma.fr/ppg/logic/service/adjustment\"><ns1:TransactionId xmlns:ns1=\"http://www.ferma.fr/ppg/logic/service\">" + getINReconTxnID(map) + "</ns1:TransactionId>");
            sbf.append("<InterfaceId>" + map.get("FERMA_INTERFACE_ID") + "</InterfaceId>");
            sbf.append("<AccessIdentifier>" + InterfaceUtil.getFilterMSISDN((String) map.get("INTERFACE_ID"), (String) map.get("MSISDN")) + "</AccessIdentifier>");
            sbf.append("<AccessType>" + map.get("AccessType") + "</AccessType>");
            sbf.append("<AccountId>" + InterfaceUtil.NullToString((String) map.get("AccountId")) + "</AccountId>");
            sbf.append("<Profil>" + InterfaceUtil.NullToString((String) map.get("Profile")) + "</Profil>");
            sbf.append("<AccountStatus>" + InterfaceUtil.NullToString((String) map.get("AccountStatus")) + "</AccountStatus>");
            sbf.append("<LockStatus>" + InterfaceUtil.NullToString((String) map.get("LockStatus")) + "</LockStatus>");
            sbf.append("<RechInstallment>" + InterfaceUtil.NullToString((String) map.get("RechInstallment")) + "</RechInstallment>");
            sbf.append("<Balances><Balance>");
            sbf.append("<BalanceId>" + InterfaceUtil.NullToString((String) map.get("BalanceId")) + "</BalanceId>");
            sbf.append("<LifeCycle>" + InterfaceUtil.NullToString((String) map.get("LifeCycle")) + "</LifeCycle>");
            sbf.append("<Option>" + InterfaceUtil.NullToString((String) map.get("Option")) + "</Option>");
            sbf.append("<Amount>" + InterfaceUtil.NullToString((String) map.get("transfer_amount")) + "</Amount>");
            sbf.append("<UnitType>" + InterfaceUtil.NullToString((String) map.get("UnitType")) + "</UnitType>");
            sbf.append("<CurrentValidityDate>" + InterfaceUtil.NullToString((String) map.get("CurrentValidityDate")) + "</CurrentValidityDate>");
            sbf.append("<CurrentGraceDate>" + InterfaceUtil.NullToString((String) map.get("CurrentGraceDate")) + "</CurrentGraceDate>");
            sbf.append("<NewValidityDate>" + InterfaceUtil.NullToString((String) map.get("NewValidityDate")) + "</NewValidityDate>");
            sbf.append("<NewGraceDate>" + InterfaceUtil.NullToString((String) map.get("NewGraceDate")) + "</NewGraceDate>");
            sbf.append("<CurrentState>" + InterfaceUtil.NullToString((String) map.get("CurrentState")) + "</CurrentState>");
            sbf.append("</Balance></Balances>");
            sbf.append("</BalanceAdjustmentRequest></envelope>");
            requestStr = sbf.toString();
            return requestStr;
        } catch (Exception e) {
            _log.error("generateBalanceAdjustmentRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateBalanceAdjustmentRequest", "Exited requestStr: " + requestStr);
        }
    }

    /**
     * This method parse BalanceAdjustmentResponse from XML string into HashMap
     * 
     * @param responseStr
     *            String
     * @return map HsahMap
     */
    private HashMap parseBalanceAdjustmentResponse(String responseStr) throws Exception {

        if (_log.isDebugEnabled())
            _log.debug("parseBalanceAdjustmentResponse", "Entered responseStr: " + responseStr);
        HashMap map = null;
        try {
            map = new HashMap();
            String status = null;
            int index = responseStr.indexOf("<ser:Status>");
            if (index != -1) {
                status = responseStr.substring(index + "<ser:Status>".length(), responseStr.indexOf("</ser:Status>", index));
                map.put("Status", status);
            }
            index = responseStr.indexOf("<ser:TransactionId>");
            if (index != -1) {
                String transactionId = responseStr.substring(index + "<ser:TransactionId>".length(), responseStr.indexOf("</ser:TransactionId>", index));
                map.put("TransactionId", transactionId);
            }
            if (status.equals("0")) {
                index = responseStr.indexOf("<ser:InterfaceId>");
                if (index != -1) {
                    String interfaceId = responseStr.substring(index + "<ser:InterfaceId>".length(), responseStr.indexOf("</ser:InterfaceId>", index));
                    map.put("InterfaceId", interfaceId);
                }
                index = responseStr.indexOf("<ser:AccessIdentifier>");
                if (index != -1) {
                    String accessIdentifier = responseStr.substring(index + "<ser:AccessIdentifier>".length(), responseStr.indexOf("</ser:AccessIdentifier>", index));
                    map.put("AccessIdentifier", accessIdentifier);
                }
                index = responseStr.indexOf("<ser:AccessType>");
                if (index != -1) {
                    String accessType = responseStr.substring(index + "<ser:AccessType>".length(), responseStr.indexOf("</ser:AccessType>", index));
                    map.put("AccessType", accessType);
                }
                index = responseStr.indexOf("<ser:AccountId>");
                if (index != -1) {
                    String accountId = responseStr.substring(index + "<ser:AccountId>".length(), responseStr.indexOf("</ser:AccountId>", index));
                    map.put("AccountId", accountId);
                }
                index = responseStr.indexOf("<ser:Profil>");
                if (index != -1) {
                    String profile = responseStr.substring(index + "<ser:Profil>".length(), responseStr.indexOf("</ser:Profil>", index));
                    map.put("Profile", profile);
                }
                index = responseStr.indexOf("<ser:AccountLanguage>");
                if (index != -1) {
                    String accountLanguage = responseStr.substring(index + "<ser:AccountLanguage>".length(), responseStr.indexOf("</ser:AccountLanguage>", index));
                    map.put("AccountLanguage", accountLanguage);
                }
                index = responseStr.indexOf("<ser:AccountStatus>");
                if (index != -1) {
                    String accountStatus = responseStr.substring(index + "<ser:AccountStatus>".length(), responseStr.indexOf("</ser:AccountStatus>", index));
                    map.put("AccountStatus", accountStatus);
                }
                index = responseStr.indexOf("<ser:LockStatus>");
                if (index != -1) {
                    String lockStatus = responseStr.substring(index + "<ser:LockStatus>".length(), responseStr.indexOf("</ser:LockStatus>", index));
                    map.put("LockStatus", lockStatus);
                    map.put("LOCK_FLAG", lockStatus);
                }
                index = responseStr.indexOf("<ser:TariffPlan>");
                if (index != -1) {
                    String tariffPlan = responseStr.substring(index + "<ser:TariffPlan>".length(), responseStr.indexOf("</ser:TariffPlan>", index));
                    map.put("TariffPlan", tariffPlan);
                }
                index = responseStr.indexOf("<ser:Balances><ser:Balance><ser:BalanceId>");
                if (index != -1) {
                    String balanceId = responseStr.substring(index + "<ser:Balances><ser:Balance><ser:BalanceId>".length(), responseStr.indexOf("</ser:BalanceId>", index));
                    map.put("BalanceId", balanceId);
                }
                index = responseStr.indexOf("<ser:LifeCycle>");
                if (index != -1) {
                    String lifeCycle = responseStr.substring(index + "<ser:LifeCycle>".length(), responseStr.indexOf("</ser:LifeCycle>", index));
                    map.put("LifeCycle", lifeCycle);
                }
                index = responseStr.indexOf("<ser:Option>");
                if (index != -1) {
                    String option = responseStr.substring(index + "<ser:Option>".length(), responseStr.indexOf("</ser:Option>", index));
                    map.put("Option", option);
                }
                index = responseStr.indexOf("<ser:Amount>");
                if (index != -1) {
                    String amount = responseStr.substring(index + "<ser:Amount>".length(), responseStr.indexOf("</ser:Amount>", index));
                    map.put("Amount", amount);
                }
                index = responseStr.indexOf("<ser:UnitType>");
                if (index != -1) {
                    String unitType = responseStr.substring(index + "<ser:UnitType>".length(), responseStr.indexOf("</ser:UnitType>", index));
                    map.put("UnitType", unitType);
                }
                index = responseStr.indexOf("<ser:ValidityDate>");
                if (index != -1) {
                    String validityDate = responseStr.substring(index + "<ser:ValidityDate>".length(), responseStr.indexOf("</ser:ValidityDate>", index));
                    map.put("ValidityDate", validityDate);
                }
                index = responseStr.indexOf("<ser:GraceDate>");
                if (index != -1) {
                    String graceDate = responseStr.substring(index + "<ser:GraceDate>".length(), responseStr.indexOf("</ser:GraceDate>", index));
                    map.put("GraceDate", graceDate);
                }
                index = responseStr.indexOf("<ser:State>");
                if (index != -1) {
                    String currentState = responseStr.substring(index + "<ser:State>".length(), responseStr.indexOf("</ser:State>", index));
                    map.put("CurrentState", currentState);
                }
            }
            return map;
        } catch (Exception e) {
            _log.error("parseBalanceAdjustmentResponse", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeBalanceResponse", "Exiting map: " + map);
        }
    }

    /**
     * This method construct LogoutRequest in XML format
     * 
     * @param map
     *            HsahMap
     * @return requestStr String
     */
    private String generateLogoutRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateLogoutRequest", "Entered map=" + map);
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><envelope  action=\"logout\"><LogoutRequest xmlns=\"http://www.ferma.fr/ppg/logic/service/logout\"><ns1:TransactionId xmlns:ns1=\"http://www.ferma.fr/ppg/logic/service\">" + (String) map.get("IN_TXN_ID") + "</ns1:TransactionId>");
            sbf.append("<InterfaceId>" + map.get("FERMA_INTERFACE_ID") + "</InterfaceId>");
            sbf.append("</LogoutRequest></envelope>");
            requestStr = sbf.toString();
            return requestStr;
        } catch (Exception e) {
            _log.error("generateLogoutRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateLogoutRequest", "Exited requestStr: " + requestStr);
        }
    }

    /**
     * This method parse LogoutResponse from XML string to HashMap
     * 
     * @param responseStr
     *            String
     * @return map HsahMap
     */
    private HashMap parseLogoutResponse(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseLogoutResponse", "Entered responseStr: " + responseStr);
        HashMap map = null;
        try {
            map = new HashMap();
            int index = responseStr.indexOf("<ser:Status xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">");
            if (index != -1) {
                String status = responseStr.substring(index + "<ser:Status xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">".length(), responseStr.indexOf("</ser:Status>", index));
                map.put("Status", status);
            }
            index = responseStr.indexOf("<ser:TransactionId xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">");
            if (index != -1) {
                String transactionId = responseStr.substring(index + "<ser:TransactionId xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">".length(), responseStr.indexOf("</ser:TransactionId>", index));
                map.put("TransactionId", transactionId);
            }
            return map;
        } catch (Exception e) {
            _log.error("parseLogoutResponse", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseLogoutResponse", "Exiting map: " + map);
        }
    }

    public String replaceInterfaceID(String p_requestString, String newInterfaceID) {
        if (_log.isDebugEnabled())
            _log.debug("replaceInterfaceID", "Entered p_requestString: " + p_requestString + " newInterfaceID : " + newInterfaceID);
        int index1 = p_requestString.indexOf("<InterfaceId>");
        int index2 = p_requestString.indexOf("</InterfaceId>", index1);
        String oldString = p_requestString.substring(index1, index2 + "</InterfaceId>".length());
        String newString = "<InterfaceId>" + newInterfaceID + "</InterfaceId>";
        return p_requestString.replaceAll(oldString, newString);
    }
    /**
     * public static void main(String[] str)
     * {
     * System.out.println(new Ferma6RequestFormatter().replaceInterfaceID(
     * "111111111111122222<InterfaceId>123</InterfaceId>2222222","456"));
     * }
     **/
}
