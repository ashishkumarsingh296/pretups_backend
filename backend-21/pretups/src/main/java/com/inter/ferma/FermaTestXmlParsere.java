package com.inter.ferma;

/**
 * @(#)FermaRequestResponse.java
 *                               Copyright(c) 2005, Bharti Telesoft Int. Public
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
 *                               Abhijit Chauhan Oct 06,2005 Initial Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               --
 *                               A FermaRequestResponse object is a wrapper
 *                               around a DOM tree.
 *                               The methods of the class use the DOM API to
 *                               work with the
 *                               tree in various ways.
 **/
import javax.xml.parsers.*; // JAXP classes for parsing

import org.w3c.dom.*; // W3C DOM classes for traversing the document
import org.xml.sax.*; // SAX classes used for error handling by JAXP
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.util.XMLRequestResponse;
import com.btsl.util.BTSLUtil;

import java.io.*; // For reading the input file
import java.util.HashMap;

public class FermaTestXmlParsere extends XMLRequestResponse {
    public final static int ACTION_LOGIN = 0;
    public final static int ACTION_ACCOUNT_INFO = 1;
    public final static int ACTION_RECHARGE_BALANCE = 2;
    public final static int ACTION_BALANCE_ADJUST = 3;
    public final static int ACTION_LOGOUT = 4;
    public Log _log = LogFactory.getLog(this.getClass().getName());

    public HashMap parseResponse(int action, String responseStr) throws Exception {
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
        return map;
    }

    public HashMap constructMapFromStr(int action, String responseStr) throws Exception {
        HashMap map = null;
        switch (action) {
        case ACTION_LOGIN: {
            map = parseLoginRequest(responseStr);
            break;
        }
        case ACTION_ACCOUNT_INFO: {
            map = parseGetAccountInfoRequest(responseStr);
            break;
        }
        case ACTION_RECHARGE_BALANCE: {
            map = parseRechargeBalanceRequest(responseStr);
            break;
        }
        case ACTION_BALANCE_ADJUST: {
            map = parseBalanceAdjustmentRequest(responseStr);
            break;
        }
        case ACTION_LOGOUT: {
            map = parseLogoutResponse(responseStr);
            break;
        }
        }
        return map;
    }

    public String constructStrFromMapRequest(int action, HashMap map) throws Exception {
        String str = null;
        switch (action) {
        case ACTION_LOGIN: {
            str = generateLoginReponse(map);
            break;
        }
        case ACTION_ACCOUNT_INFO: {
            str = generateGetAccountInfoResponse(map);
            break;
        }
        case ACTION_RECHARGE_BALANCE: {
            str = generateRechargeBalanceResponse(map);
            break;
        }
        case ACTION_BALANCE_ADJUST: {
            str = generateBalanceAdjustmentResponse(map);
            break;
        }
        case ACTION_LOGOUT: {
            str = generateLogoutRequest(map);
            break;
        }
        }
        return str;
    }

    public String generateRequest(int action, HashMap map) throws Exception {
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
        return str;
    }

    private HashMap parseLoginResponse(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseLoginResponse", "Entered responseStr: " + responseStr);
        HashMap map = new HashMap();
        try {
            Document document = _parser.parse(new ByteArrayInputStream(responseStr.getBytes()));
            NodeList rootNodes = document.getElementsByTagName("LoginResponse");
            Element loginResponseTag = (Element) rootNodes.item(0);

            Element statusTag = (Element) loginResponseTag.getElementsByTagName("Status").item(0);
            String status = ((Text) statusTag.getFirstChild()).getData().trim();
            System.out.println("Status: " + status);
            Element transactionIdTag = (Element) loginResponseTag.getElementsByTagName("TransactionId").item(0);
            String transactionId = ((Text) transactionIdTag.getFirstChild()).getData().trim();
            System.out.println("TransactionId: " + transactionId);
            Element interfaceIdTag = (Element) loginResponseTag.getElementsByTagName("InterfaceId").item(0);
            String interfaceId = ((Text) interfaceIdTag.getFirstChild()).getData().trim();
            System.out.println("InterfaceId: " + interfaceId);
            map.put("Status", status);
            map.put("TransactionId", transactionId);
            map.put("InterfaceId", interfaceId);
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

    private HashMap parseGetAccountInfoRequest(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseLoginRequest", "Entered responseStr: " + responseStr);
        HashMap map = new HashMap();
        try {
            Document document = _parser.parse(new ByteArrayInputStream(responseStr.getBytes()));
            NodeList rootNodes = document.getElementsByTagName("AccountInfoGettingRequest");
            Element accountInfoGettingRequestTag = (Element) rootNodes.item(0);
            Element transactionIdTag = (Element) accountInfoGettingRequestTag.getElementsByTagName("ns1:TransactionId").item(0);
            String transactionId = ((Text) transactionIdTag.getFirstChild()).getData().trim();
            System.out.println("TransactionId: " + transactionId);

            String interfaceId = null;
            Element interfaceIdTag = (Element) accountInfoGettingRequestTag.getElementsByTagName("InterfaceId").item(0);
            Text interfaceIdNode = (Text) interfaceIdTag.getFirstChild();
            if (interfaceIdNode == null)
                interfaceId = "";
            else
                interfaceId = interfaceIdNode.getData().trim();
            String accessIdentifier = null;
            Element accessIdentifierTag = (Element) accountInfoGettingRequestTag.getElementsByTagName("AccessIdentifier").item(0);
            Text accessIdentifierNode = (Text) accessIdentifierTag.getFirstChild();
            if (accessIdentifierNode == null)
                accessIdentifier = "";
            else
                accessIdentifier = accessIdentifierNode.getData().trim();
            String accessType = null;
            Element accessTypeTag = (Element) accountInfoGettingRequestTag.getElementsByTagName("AccessType").item(0);
            Text accessTypeNode = (Text) accessTypeTag.getFirstChild();
            if (accessTypeNode == null)
                accessType = "";
            else
                accessType = accessTypeNode.getData().trim();

            map.put("TransactionId", transactionId);
            map.put("InterfaceId", interfaceId);
            map.put("AccessIdentifier", accessIdentifier);
            map.put("AccessType", accessType);
            return map;
        } catch (Exception e) {
            _log.error("parseLoginRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseLoginRequest", "Exiting map: " + map);
        }
    }

    private HashMap parseRechargeBalanceRequest(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeBalanceRequest", "Entered responseStr: " + responseStr);
        HashMap map = new HashMap();
        try {
            Document document = _parser.parse(new ByteArrayInputStream(responseStr.getBytes()));
            NodeList rootNodes = document.getElementsByTagName("BalanceRechargingRequest");
            Element balanceRechargingRequestTag = (Element) rootNodes.item(0);
            Element transactionIdTag = (Element) balanceRechargingRequestTag.getElementsByTagName("ns1:TransactionId").item(0);
            String transactionId = ((Text) transactionIdTag.getFirstChild()).getData().trim();
            System.out.println("TransactionId: " + transactionId);
            String interfaceId = null;

            Element interfaceIdTag = (Element) balanceRechargingRequestTag.getElementsByTagName("InterfaceId").item(0);
            Text interfaceIdNode = (Text) interfaceIdTag.getFirstChild();
            if (interfaceIdNode == null)
                interfaceId = "";
            else
                interfaceId = interfaceIdNode.getData().trim();
            String accessIdentifier = null;
            Element accessIdentifierTag = (Element) balanceRechargingRequestTag.getElementsByTagName("AccessIdentifier").item(0);
            Text accessIdentifierNode = (Text) accessIdentifierTag.getFirstChild();
            if (accessIdentifierNode == null)
                accessIdentifier = "";
            else
                accessIdentifier = accessIdentifierNode.getData().trim();
            String accessType = null;
            Element accessTypeTag = (Element) balanceRechargingRequestTag.getElementsByTagName("AccessType").item(0);
            Text accessTypeNode = (Text) accessTypeTag.getFirstChild();
            if (accessTypeNode == null)
                accessType = "";
            else
                accessType = accessTypeNode.getData().trim();

            /*
             * Element interfaceIdTag = (Element)balanceRechargingRequestTag
             * .getElementsByTagName("InterfaceId").item(0);
             * String
             * interfaceId=((Text)interfaceIdTag.getFirstChild()).getData(
             * ).trim();
             * System.out.println("InterfaceId: "+interfaceId);
             * Element accessIdentifierTag =
             * (Element)balanceRechargingRequestTag
             * .getElementsByTagName("AccessIdentifier").item(0);
             * String
             * accessIdentifier=((Text)accessIdentifierTag.getFirstChild()
             * ).getData().trim();
             * System.out.println("AccessIdentifier: "+accessIdentifier);
             * Element accessTypeTag =
             * (Element)balanceRechargingRequestTag.getElementsByTagName
             * ("AccessType").item(0);
             * String
             * accessType=((Text)accessTypeTag.getFirstChild()).getData().
             * trim();
             * System.out.println("AccessType: "+accessType);
             */

            /*
             * Element accountIdTag =
             * (Element)balanceRechargingRequestTag.getElementsByTagName
             * ("AccountId").item(0);
             * String
             * accountId=((Text)accountIdTag.getFirstChild()).getData().trim();
             * System.out.println("AccountId: "+accountId);
             */
            /*
             * String accountId=null;
             * Element accountIdTag =
             * (Element)balanceRechargingRequestTag.getElementsByTagName
             * ("AccountId").item(0);
             * Text accountIdNode=(Text)accountIdTag.getFirstChild();
             * if(accountIdNode==null)
             * accountId="";
             * else
             * accountId=accountIdNode.getData().trim();
             * 
             * String balanceId=null;
             * Element accountIdTag =
             * (Element)balanceRechargingRequestTag.getElementsByTagName
             * ("AccountId").item(0);
             * Text accountIdNode=(Text)accountIdTag.getFirstChild();
             * if(accountIdNode==null)
             * accountId="";
             * else
             * accountId=accountIdNode.getData().trim();
             * 
             * Element balanceIdTag =
             * (Element)balanceRechargingRequestTag.getElementsByTagName
             * ("BalanceId").item(0);
             * String
             * balanceId=((Text)balanceIdTag.getFirstChild()).getData().trim();
             * System.out.println("BalanceId: "+balanceId);
             * 
             * Element rechargeValueTag =
             * (Element)balanceRechargingRequestTag.getElementsByTagName
             * ("RechargeValue").item(0);
             * String
             * rechargeValue=((Text)rechargeValueTag.getFirstChild()).getData
             * ().trim();
             * System.out.println("RechargeValue: "+rechargeValue);
             * 
             * Element unitTypeTag =
             * (Element)balanceRechargingRequestTag.getElementsByTagName
             * ("UnitType").item(0);
             * String
             * unitType=((Text)unitTypeTag.getFirstChild()).getData().trim();
             * System.out.println("UnitType: "+unitType);
             */

            map.put("TransactionId", transactionId);
            map.put("InterfaceId", interfaceId);
            map.put("AccessIdentifier", accessIdentifier);
            map.put("AccessType", accessType);
            /*
             * map.put("AccountId",accountId);
             * map.put("BalanceId",balanceId);
             * map.put("RechargeValue",rechargeValue);
             * map.put("UnitType",unitType);
             */
            return map;
        } catch (Exception e) {
            _log.error("parseRechargeBalanceRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseLoginRequest", "Exiting map: " + map);
        }
    }

    private HashMap parseBalanceAdjustmentRequest(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseBalanceAdjustmentRequest", "Entered responseStr: " + responseStr);
        HashMap map = new HashMap();
        try {
            Document document = _parser.parse(new ByteArrayInputStream(responseStr.getBytes()));
            NodeList rootNodes = document.getElementsByTagName("BalanceAdjustmentRequest");
            Element balanceAdjustmentRequestTag = (Element) rootNodes.item(0);
            Element transactionIdTag = (Element) balanceAdjustmentRequestTag.getElementsByTagName("ns1:TransactionId").item(0);
            String transactionId = ((Text) transactionIdTag.getFirstChild()).getData().trim();
            System.out.println("TransactionId: " + transactionId);

            String interfaceId = null;
            Element interfaceIdTag = (Element) balanceAdjustmentRequestTag.getElementsByTagName("InterfaceId").item(0);
            Text interfaceIdNode = (Text) interfaceIdTag.getFirstChild();
            if (interfaceIdNode == null)
                interfaceId = "";
            else
                interfaceId = interfaceIdNode.getData().trim();
            String accessIdentifier = null;
            Element accessIdentifierTag = (Element) balanceAdjustmentRequestTag.getElementsByTagName("AccessIdentifier").item(0);
            Text accessIdentifierNode = (Text) accessIdentifierTag.getFirstChild();
            if (accessIdentifierNode == null)
                accessIdentifier = "";
            else
                accessIdentifier = accessIdentifierNode.getData().trim();
            String accessType = null;
            Element accessTypeTag = (Element) balanceAdjustmentRequestTag.getElementsByTagName("AccessType").item(0);
            Text accessTypeNode = (Text) accessTypeTag.getFirstChild();
            if (accessTypeNode == null)
                accessType = "";
            else
                accessType = accessTypeNode.getData().trim();

            /*
             * Element interfaceIdTag = (Element)balanceAdjustmentRequestTag
             * .getElementsByTagName("InterfaceId").item(0);
             * String
             * interfaceId=((Text)interfaceIdTag.getFirstChild()).getData(
             * ).trim();
             * System.out.println("InterfaceId: "+interfaceId);
             * Element accessIdentifierTag =
             * (Element)balanceAdjustmentRequestTag
             * .getElementsByTagName("AccessIdentifier").item(0);
             * String
             * accessIdentifier=((Text)accessIdentifierTag.getFirstChild()
             * ).getData().trim();
             * System.out.println("AccessIdentifier: "+accessIdentifier);
             * Element accessTypeTag = (Element)balanceAdjustmentRequestTag
             * .getElementsByTagName("AccessType").item(0);
             * String
             * accessType=((Text)accessTypeTag.getFirstChild()).getData().
             * trim();
             * System.out.println("AccessType: "+accessType);*
             * 
             * /*Element accountIdTag = (Element)balanceAdjustmentRequestTag
             * .getElementsByTagName("AccountId").item(0);
             * String
             * accountId=((Text)accountIdTag.getFirstChild()).getData().trim();
             * System.out.println("AccountId: "+accountId);
             */

            /*
             * Element profileTag = (Element)balanceAdjustmentRequestTag
             * .getElementsByTagName("Profile").item(0);
             * String
             * profile=((Text)profileTag.getFirstChild()).getData().trim();
             * System.out.println("Profile: "+profile);
             */

            /*
             * Element accountStatusTag = (Element)balanceAdjustmentRequestTag
             * .getElementsByTagName("AccountStatus").item(0);
             * String
             * accountStatus=((Text)accountStatusTag.getFirstChild()).getData
             * ().trim();
             * System.out.println("AccountStatus: "+accountStatus);
             * 
             * Element lockStatusTag = (Element)balanceAdjustmentRequestTag
             * .getElementsByTagName("LockStatus").item(0);
             * String
             * lockStatus=((Text)lockStatusTag.getFirstChild()).getData().
             * trim();
             * System.out.println("LockStatus: "+lockStatus);
             */

            /*
             * Element rechInstallmentTag = (Element)balanceAdjustmentRequestTag
             * .getElementsByTagName("RechInstallment").item(0);
             * String
             * rechInstallment=((Text)rechInstallmentTag.getFirstChild()).
             * getData().trim();
             * System.out.println("RechInstallment: "+rechInstallment);
             * 
             * Element balanceIdTag = (Element)balanceAdjustmentRequestTag
             * .getElementsByTagName("BalanceId").item(0);
             * String
             * balanceId=((Text)balanceIdTag.getFirstChild()).getData().trim();
             * System.out.println("BalanceId: "+balanceId);
             * 
             * Element lifeCycleTag = (Element)balanceAdjustmentRequestTag
             * .getElementsByTagName("LifeCycle").item(0);
             * String
             * lifeCycle=((Text)lifeCycleTag.getFirstChild()).getData().trim();
             * System.out.println("LifeCycle: "+lifeCycle);
             * 
             * Element optionTag = (Element)balanceAdjustmentRequestTag
             * .getElementsByTagName("Option").item(0);
             * String option=((Text)optionTag.getFirstChild()).getData().trim();
             * System.out.println("Option: "+option);
             */
            NodeList balancesNode = balanceAdjustmentRequestTag.getElementsByTagName("Balances");
            Element balancesTag = (Element) balancesNode.item(0);
            NodeList balanceNode = balancesTag.getElementsByTagName("Balance");
            Element balanceTag = (Element) balanceNode.item(0);
            String balanceId = null;
            Element balanceIdTag = (Element) balanceTag.getElementsByTagName("BalanceId").item(0);
            Text balanceIdNode = (Text) balanceIdTag.getFirstChild();
            if (balanceIdNode == null)
                balanceId = "";
            else
                balanceId = balanceIdNode.getData().trim();
            String lifeCycle = null;
            Element lifeCycleTag = (Element) balanceTag.getElementsByTagName("LifeCycle").item(0);
            Text lifeCycleNode = (Text) lifeCycleTag.getFirstChild();
            if (lifeCycleNode == null)
                lifeCycle = "";
            else
                lifeCycle = lifeCycleNode.getData().trim();
            String option = null;
            Element optionTag = (Element) balanceTag.getElementsByTagName("Option").item(0);
            Text optionNode = (Text) optionTag.getFirstChild();
            if (optionNode == null)
                option = "";
            else
                option = optionNode.getData().trim();
            String amount = null;
            Element amountTag = (Element) balanceTag.getElementsByTagName("Amount").item(0);
            Text amountNode = (Text) amountTag.getFirstChild();
            if (amountNode == null)
                amount = "";
            else
                amount = amountNode.getData().trim();
            /*
             * Element unitTypeTag = (Element)balanceAdjustmentRequestTag
             * .getElementsByTagName("UnitType").item(0);
             * String
             * unitType=((Text)unitTypeTag.getFirstChild()).getData().trim();
             * System.out.println("UnitType: "+unitType);
             */

            /*
             * Element currentValidityDateTag=
             * (Element)balanceAdjustmentRequestTag
             * .getElementsByTagName("CurrentValidityDate").item(0);
             * String
             * currentValidityDate=((Text)currentValidityDateTag.getFirstChild
             * ()).getData().trim();
             * System.out.println("CurrentValidityDate: "+currentValidityDate);
             * 
             * Element currentGraceDateTag= (Element)balanceAdjustmentRequestTag
             * .getElementsByTagName("CurrentGraceDate").item(0);
             * String
             * currentGraceDate=((Text)currentGraceDateTag.getFirstChild()
             * ).getData().trim();
             * System.out.println("CurrentGraceDate: "+currentGraceDate);
             * 
             * Element newValidityDateTag= (Element)balanceAdjustmentRequestTag
             * .getElementsByTagName("NewValidityDate").item(0);
             * String
             * newValidityDate=((Text)newValidityDateTag.getFirstChild()).
             * getData().trim();
             * System.out.println("NewValidityDate: "+newValidityDate);
             * 
             * Element newGraceDateTag= (Element)balanceAdjustmentRequestTag
             * .getElementsByTagName("NewGraceDate").item(0);
             * String
             * newGraceDate=((Text)newGraceDateTag.getFirstChild()).getData
             * ().trim();
             * System.out.println("NewGraceDate: "+newGraceDate);
             * 
             * Element currentStateTag= (Element)balanceAdjustmentRequestTag
             * .getElementsByTagName("CurrentState").item(0);
             * String
             * currentState=((Text)currentStateTag.getFirstChild()).getData
             * ().trim();
             * System.out.println("CurrentState: "+currentState);
             */

            map.put("TransactionId", transactionId);
            map.put("InterfaceId", interfaceId);
            map.put("AccessIdentifier", accessIdentifier);
            map.put("AccessType", accessType);
            // map.put("AccountId",accountId);
            // map.put("Profile",profile);
            // map.put("AccountStatus",accountStatus);
            // map.put("LockStatus",lockStatus);
            // map.put("RechInstallment",rechInstallment);
            map.put("BalanceId", balanceId);
            map.put("LifeCycle", lifeCycle);
            map.put("Option", option);
            map.put("Amount", amount);
            /*
             * map.put("UnitType",unitType);
             * map.put("CurrentValidityDate",currentValidityDate);
             * map.put("CurrentGraceDate",currentGraceDate);
             * map.put("NewValidityDate",newValidityDate);
             * map.put("NewGraceDate",newGraceDate);
             * map.put("CurrentState",currentState);
             */
            return map;
        } catch (Exception e) {
            _log.error("parseBalanceAdjustmentRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseLoginRequest", "Exiting map: " + map);
        }
    }

    private HashMap parseLoginRequest(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseLoginRequest", "Entered responseStr: " + responseStr);
        HashMap map = new HashMap();
        try {
            Document document = _parser.parse(new ByteArrayInputStream(responseStr.getBytes()));
            NodeList rootNodes = document.getElementsByTagName("LoginRequest");
            Element loginRequestTag = (Element) rootNodes.item(0);
            Element transactionIdTag = (Element) loginRequestTag.getElementsByTagName("ns1:TransactionId").item(0);
            Text transactionIdNode = (Text) transactionIdTag.getFirstChild();
            String transactionId = null;
            if (transactionIdNode == null)
                transactionId = "";
            else
                transactionId = transactionIdNode.getData().trim();
            Element userNameTag = (Element) loginRequestTag.getElementsByTagName("UserName").item(0);
            String userName = ((Text) userNameTag.getFirstChild()).getData().trim();
            System.out.println("UserName: " + userName);
            Element passwordTag = (Element) loginRequestTag.getElementsByTagName("Password").item(0);
            String password = ((Text) passwordTag.getFirstChild()).getData().trim();
            System.out.println("Password: " + password);
            Element protocolVersionTag = (Element) loginRequestTag.getElementsByTagName("ProtocolVersion").item(0);
            String protocolVersion = ((Text) protocolVersionTag.getFirstChild()).getData().trim();
            System.out.println("ProtocolVersion: " + protocolVersion);
            map.put("TransactionId", transactionId);
            map.put("UserName", userName);
            map.put("Password", password);
            map.put("ProtocolVersion", protocolVersion);
            return map;
        } catch (Exception e) {
            _log.error("parseLoginRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseLoginRequest", "Exiting map: " + map);
        }
    }

    private HashMap parseLogoutResponse(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseLogoutResponse", "Entered responseStr: " + responseStr);
        HashMap map = new HashMap();
        try {
            Document document = _parser.parse(new ByteArrayInputStream(responseStr.getBytes()));
            NodeList rootNodes = document.getElementsByTagName("LogoutRequest");
            Element logoutResponseTag = (Element) rootNodes.item(0);
            /*
             * Element statusTag =
             * (Element)logoutResponseTag.getElementsByTagName
             * ("Status").item(0);
             * String status
             * =((Text)statusTag.getFirstChild()).getData().trim();
             * System.out.println("Status: "+status);
             */

            Element transactionIdTag = (Element) logoutResponseTag.getElementsByTagName("ns1:TransactionId").item(0);
            Text transactionIdNode = (Text) transactionIdTag.getFirstChild();
            String transactionId = null;
            if (transactionIdNode == null)
                transactionId = "";
            else
                transactionId = transactionIdNode.getData().trim();
            map.put("TransactionId", transactionId);
            String interfaceId = null;
            Element interfaceIdTag = (Element) logoutResponseTag.getElementsByTagName("InterfaceId").item(0);
            Text interfaceIdNode = (Text) interfaceIdTag.getFirstChild();
            if (interfaceIdNode == null)
                interfaceId = "";
            else
                interfaceId = interfaceIdNode.getData().trim();
            map.put("InterfaceId", interfaceId);
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

    private String generateLoginRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateLoginRequest", "Entered userName: " + map.get("UserName") + " password:" + map.get("Password") + " protocol: " + map.get("ProtocolVersion"));
        String requestStr = null;
        try {
            Document document = _parser.parse(new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><LoginRequest></LoginRequest>".getBytes()));
            NodeList rootNodes = document.getElementsByTagName("LoginRequest");
            Element loginRequestTag = (Element) rootNodes.item(0);
            Element transactionIdNode = document.createElement("TransactionId");
            Element userNameNode = document.createElement("UserName");
            Element passwordNode = document.createElement("Password");
            Element protocolVersionNode = document.createElement("ProtocolVersion");
            transactionIdNode.appendChild(document.createTextNode((String) map.get("TransactionId")));
            userNameNode.appendChild(document.createTextNode((String) map.get("UserName")));
            passwordNode.appendChild(document.createTextNode((String) map.get("Password")));
            protocolVersionNode.appendChild(document.createTextNode((String) map.get("ProtocolVersion")));
            loginRequestTag.appendChild(transactionIdNode);
            loginRequestTag.appendChild(userNameNode);
            loginRequestTag.appendChild(passwordNode);
            loginRequestTag.appendChild(protocolVersionNode);
            StringBuffer strBuff = new StringBuffer();
            requestStr = write(document, strBuff, "");
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

    private String generateLoginReponse(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateLoginReponse", "Entered status: " + map.get("Status") + " transactionId:" + map.get("TransactionId") + " interfaceId: " + map.get("InterfaceId"));
        String requestStr = null;
        try {
            Document document = _parser.parse(new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"UTF-8\"?><log:LoginResponse xmlns:log=\"http://www.ferma.fr/ppg/logic/service/login\"><ser:Status xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">" + map.get("Status") + "</ser:Status><ser:TransactionId xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">" + map.get("TransactionId") + "</ser:TransactionId><ser:InterfaceId xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">" + map.get("InterfaceId") + "</ser:InterfaceId></log:LoginResponse>").getBytes()));
            /*
             * NodeList rootNodes =
             * document.getElementsByTagName("log:LoginResponse");
             * Element loginResponseTag = (Element)rootNodes.item(0);
             * Element statusNode = document.createElement("ser:Status");
             * Element transactionIdNode =
             * document.createElement("ser:TransactionId");
             * Element interfaceIdNode =
             * document.createElement("ser:InterfaceId");
             * 
             * statusNode.appendChild(document.createTextNode((String)map.get(
             * "Status")));
             * transactionIdNode.appendChild(document.createTextNode((String)map.
             * get("TransactionId")));
             * interfaceIdNode.appendChild(document.createTextNode((String)map.get
             * ("InterfaceId")));
             * loginResponseTag.appendChild(statusNode);
             * loginResponseTag .appendChild(transactionIdNode);
             * loginResponseTag .appendChild(interfaceIdNode);
             */
            StringBuffer strBuff = new StringBuffer();
            requestStr = write(document, strBuff, "");
            return requestStr;
        } catch (Exception e) {
            _log.error("parseLoginResponse", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseLoginResponse", "Entered requestStr: " + requestStr);
        }
    }

    private String generateGetAccountInfoResponse(HashMap map) throws Exception {

        String requestStr = null;
        try {
            Document document = _parser.parse(new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ser:PrepaidServiceResponse xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\"></ser:PrepaidServiceResponse>".getBytes()));
            NodeList rootNodes = document.getElementsByTagName("ser:PrepaidServiceResponse");

            Element accountInfoGettingResponseTag = (Element) rootNodes.item(0);
            Element statusNode = document.createElement("ser:Status");
            Element transactionIdNode = document.createElement("ser:TransactionId");
            Element interfaceIdNode = document.createElement("ser:InterfaceId");
            Element accessIdentifierNode = document.createElement("ser:AccessIdentifier");
            Element accessTypeNode = document.createElement("ser:AccessType");
            Element accountIdNode = document.createElement("ser:AccountId");
            Element profileNode = document.createElement("ser:Profil");
            Element accountLanguageNode = document.createElement("ser:AccountLanguage");
            Element accountStatusNode = document.createElement("ser:AccountStatus");
            Element lockStatusNode = document.createElement("ser:LockStatus");
            Element tariffPlanNode = document.createElement("ser:TariffPlan");

            Element BalancesNode = document.createElement("ser:Balances");
            Element BalanceNode = document.createElement("ser:Balance");

            Element balanceIdNode = document.createElement("ser:BalanceId");
            Element lifeCycleNode = document.createElement("ser:LifeCycle");
            Element optionNode = document.createElement("ser:Option");
            Element amountNode = document.createElement("ser:Amount");
            Element unitTypeNode = document.createElement("ser:UnitType");
            Element validityDateNode = document.createElement("ser:ValidityDate");
            Element graceDateNode = document.createElement("ser:GraceDate");
            Element currentStateNode = document.createElement("ser:State");

            statusNode.appendChild(document.createTextNode((String) map.get("Status")));
            transactionIdNode.appendChild(document.createTextNode((String) map.get("TransactionId")));
            interfaceIdNode.appendChild(document.createTextNode((String) map.get("InterfaceId")));
            accessIdentifierNode.appendChild(document.createTextNode((String) map.get("AccessIdentifier")));
            accessTypeNode.appendChild(document.createTextNode((String) map.get("AccessType")));
            accountIdNode.appendChild(document.createTextNode((String) map.get("AccountId")));
            profileNode.appendChild(document.createTextNode((String) map.get("Profile")));
            accountLanguageNode.appendChild(document.createTextNode((String) map.get("AccountLanguage")));
            accountStatusNode.appendChild(document.createTextNode((String) map.get("AccountStatus")));
            lockStatusNode.appendChild(document.createTextNode((String) map.get("LockStatus")));
            tariffPlanNode.appendChild(document.createTextNode((String) map.get("TariffPlan")));

            balanceIdNode.appendChild(document.createTextNode((String) map.get("BalanceId")));
            lifeCycleNode.appendChild(document.createTextNode((String) map.get("LifeCycle")));
            optionNode.appendChild(document.createTextNode((String) map.get("Option")));
            amountNode.appendChild(document.createTextNode((String) map.get("Amount")));
            unitTypeNode.appendChild(document.createTextNode((String) map.get("UnitType")));
            validityDateNode.appendChild(document.createTextNode((String) map.get("ValidityDate")));
            graceDateNode.appendChild(document.createTextNode((String) map.get("GraceDate")));
            currentStateNode.appendChild(document.createTextNode((String) map.get("CurrentState")));

            accountInfoGettingResponseTag.appendChild(statusNode);
            accountInfoGettingResponseTag.appendChild(transactionIdNode);
            accountInfoGettingResponseTag.appendChild(interfaceIdNode);
            accountInfoGettingResponseTag.appendChild(accessIdentifierNode);
            accountInfoGettingResponseTag.appendChild(accessTypeNode);
            accountInfoGettingResponseTag.appendChild(accountIdNode);
            accountInfoGettingResponseTag.appendChild(profileNode);
            accountInfoGettingResponseTag.appendChild(accountLanguageNode);
            accountInfoGettingResponseTag.appendChild(accountStatusNode);
            accountInfoGettingResponseTag.appendChild(lockStatusNode);
            accountInfoGettingResponseTag.appendChild(tariffPlanNode);

            BalanceNode.appendChild(balanceIdNode);
            BalanceNode.appendChild(lifeCycleNode);
            BalanceNode.appendChild(optionNode);
            BalanceNode.appendChild(amountNode);
            BalanceNode.appendChild(unitTypeNode);
            BalanceNode.appendChild(validityDateNode);
            BalanceNode.appendChild(graceDateNode);
            BalanceNode.appendChild(currentStateNode);
            BalancesNode.appendChild(BalanceNode);
            accountInfoGettingResponseTag.appendChild(BalancesNode);
            StringBuffer strBuff = new StringBuffer();
            requestStr = write(document, strBuff, "");
            return requestStr;
        } catch (Exception e) {
            _log.error("parseLoginResponse", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseLoginResponse", "Entered requestStr: " + requestStr);
        }
    }

    private String generateRechargeBalanceResponse(HashMap map) throws Exception {
        String requestStr = null;
        try {
            Document document = _parser.parse(new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ser:PrepaidServiceResponse xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\"></ser:PrepaidServiceResponse>".getBytes()));
            NodeList rootNodes = document.getElementsByTagName("ser:PrepaidServiceResponse");
            Element balanceRechargingResponseTag = (Element) rootNodes.item(0);
            Element statusNode = document.createElement("ser:Status");
            Element transactionIdNode = document.createElement("ser:TransactionId");
            Element interfaceIdNode = document.createElement("ser:InterfaceId");
            Element accessIdentifierNode = document.createElement("ser:AccessIdentifier");
            Element accessTypeNode = document.createElement("ser:AccessType");
            Element accountIdNode = document.createElement("ser:AccountId");
            Element profileNode = document.createElement("ser:Profil");
            Element accountLanguageNode = document.createElement("ser:AccountLanguage");
            Element accountStatusNode = document.createElement("ser:AccountStatus");
            Element lockStatusNode = document.createElement("ser:LockStatus");
            Element tariffPlanNode = document.createElement("ser:TariffPlan");
            Element BalancesNode = document.createElement("ser:Balances");
            Element BalanceNode = document.createElement("ser:Balance");

            Element balanceIdNode = document.createElement("ser:BalanceId");
            Element lifeCycleNode = document.createElement("ser:LifeCycle");
            Element optionNode = document.createElement("ser:Option");
            Element amountNode = document.createElement("ser:Amount");
            Element unitTypeNode = document.createElement("ser:UnitType");
            Element validityDateNode = document.createElement("ser:ValidityDate");
            Element graceDateNode = document.createElement("ser:GraceDate");
            Element currentStateNode = document.createElement("ser:State");

            statusNode.appendChild(document.createTextNode((String) map.get("Status")));
            transactionIdNode.appendChild(document.createTextNode((String) map.get("TransactionId")));
            interfaceIdNode.appendChild(document.createTextNode((String) map.get("InterfaceId")));
            accessIdentifierNode.appendChild(document.createTextNode((String) map.get("AccessIdentifier")));
            accessTypeNode.appendChild(document.createTextNode((String) map.get("AccessType")));
            accountIdNode.appendChild(document.createTextNode((String) map.get("AccountId")));
            profileNode.appendChild(document.createTextNode((String) map.get("Profile")));
            accountLanguageNode.appendChild(document.createTextNode((String) map.get("AccountLanguage")));
            accountStatusNode.appendChild(document.createTextNode((String) map.get("AccountStatus")));
            lockStatusNode.appendChild(document.createTextNode((String) map.get("LockStatus")));
            tariffPlanNode.appendChild(document.createTextNode((String) map.get("TariffPlan")));

            balanceIdNode.appendChild(document.createTextNode((String) map.get("BalanceId")));
            lifeCycleNode.appendChild(document.createTextNode((String) map.get("LifeCycle")));
            optionNode.appendChild(document.createTextNode((String) map.get("Option")));
            amountNode.appendChild(document.createTextNode((String) map.get("Amount")));
            unitTypeNode.appendChild(document.createTextNode((String) map.get("UnitType")));
            validityDateNode.appendChild(document.createTextNode((String) map.get("ValidityDate")));
            graceDateNode.appendChild(document.createTextNode((String) map.get("GraceDate")));
            currentStateNode.appendChild(document.createTextNode((String) map.get("CurrentState")));

            balanceRechargingResponseTag.appendChild(statusNode);
            balanceRechargingResponseTag.appendChild(transactionIdNode);
            balanceRechargingResponseTag.appendChild(interfaceIdNode);
            balanceRechargingResponseTag.appendChild(accessIdentifierNode);
            balanceRechargingResponseTag.appendChild(accessTypeNode);
            balanceRechargingResponseTag.appendChild(accountIdNode);
            balanceRechargingResponseTag.appendChild(profileNode);
            balanceRechargingResponseTag.appendChild(accountLanguageNode);
            balanceRechargingResponseTag.appendChild(accountStatusNode);
            balanceRechargingResponseTag.appendChild(lockStatusNode);
            balanceRechargingResponseTag.appendChild(tariffPlanNode);

            BalanceNode.appendChild(balanceIdNode);
            BalanceNode.appendChild(lifeCycleNode);
            BalanceNode.appendChild(optionNode);
            BalanceNode.appendChild(amountNode);
            BalanceNode.appendChild(unitTypeNode);
            BalanceNode.appendChild(validityDateNode);
            BalanceNode.appendChild(graceDateNode);
            BalanceNode.appendChild(currentStateNode);
            BalancesNode.appendChild(BalanceNode);
            balanceRechargingResponseTag.appendChild(BalancesNode);
            StringBuffer strBuff = new StringBuffer();
            requestStr = write(document, strBuff, "");
            return requestStr;
        } catch (Exception e) {
            _log.error("generateRechargeBalanceResponse", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRechargeBalanceResponse", "Entered requestStr: " + requestStr);
        }
    }

    private String generateBalanceAdjustmentResponse(HashMap map) throws Exception {
        String requestStr = null;
        try {
            Document document = _parser.parse(new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ser:PrepaidServiceResponse xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\"></ser:PrepaidServiceResponse>".getBytes()));
            NodeList rootNodes = document.getElementsByTagName("ser:PrepaidServiceResponse");
            Element balanceAdjustmentResponseTag = (Element) rootNodes.item(0);
            Element statusNode = document.createElement("Status");
            Element transactionIdNode = document.createElement("TransactionId");
            Element interfaceIdNode = document.createElement("InterfaceId");
            Element accessIdentifierNode = document.createElement("AccessIdentifier");
            Element accessTypeNode = document.createElement("AccessType");
            Element accountIdNode = document.createElement("AccountId");
            Element profileNode = document.createElement("Profile");
            Element accountLanguageNode = document.createElement("AccountLanguage");
            Element accountStatusNode = document.createElement("AccountStatus");
            Element lockStatusNode = document.createElement("LockStatus");
            Element tariffPlanNode = document.createElement("TariffPlan");

            Element BalancesNode = document.createElement("ser:Balances");
            Element BalanceNode = document.createElement("ser:Balance");

            Element balanceIdNode = document.createElement("ser:BalanceId");
            Element lifeCycleNode = document.createElement("ser:LifeCycle");
            Element optionNode = document.createElement("ser:Option");
            Element amountNode = document.createElement("ser:Amount");
            Element unitTypeNode = document.createElement("ser:UnitType");
            Element validityDateNode = document.createElement("ser:ValidityDate");
            Element graceDateNode = document.createElement("ser:GraceDate");
            Element currentStateNode = document.createElement("ser:State");

            statusNode.appendChild(document.createTextNode((String) map.get("Status")));
            transactionIdNode.appendChild(document.createTextNode((String) map.get("TransactionId")));
            interfaceIdNode.appendChild(document.createTextNode((String) map.get("InterfaceId")));
            accessIdentifierNode.appendChild(document.createTextNode((String) map.get("AccessIdentifier")));
            accessTypeNode.appendChild(document.createTextNode((String) map.get("AccessType")));
            accountIdNode.appendChild(document.createTextNode((String) map.get("AccountId")));
            profileNode.appendChild(document.createTextNode((String) map.get("Profile")));
            accountLanguageNode.appendChild(document.createTextNode((String) map.get("AccountLanguage")));
            accountStatusNode.appendChild(document.createTextNode((String) map.get("AccountStatus")));
            lockStatusNode.appendChild(document.createTextNode((String) map.get("LockStatus")));
            tariffPlanNode.appendChild(document.createTextNode((String) map.get("TariffPlan")));

            balanceIdNode.appendChild(document.createTextNode((String) map.get("BalanceId")));
            lifeCycleNode.appendChild(document.createTextNode((String) map.get("LifeCycle")));
            optionNode.appendChild(document.createTextNode((String) map.get("Option")));
            amountNode.appendChild(document.createTextNode((String) map.get("Amount")));
            unitTypeNode.appendChild(document.createTextNode((String) map.get("UnitType")));
            validityDateNode.appendChild(document.createTextNode((String) map.get("ValidityDate")));
            graceDateNode.appendChild(document.createTextNode((String) map.get("GraceDate")));
            currentStateNode.appendChild(document.createTextNode((String) map.get("CurrentState")));

            balanceAdjustmentResponseTag.appendChild(statusNode);
            balanceAdjustmentResponseTag.appendChild(transactionIdNode);
            balanceAdjustmentResponseTag.appendChild(interfaceIdNode);
            balanceAdjustmentResponseTag.appendChild(accessIdentifierNode);
            balanceAdjustmentResponseTag.appendChild(accessTypeNode);
            balanceAdjustmentResponseTag.appendChild(accountIdNode);
            balanceAdjustmentResponseTag.appendChild(profileNode);
            balanceAdjustmentResponseTag.appendChild(accountLanguageNode);
            balanceAdjustmentResponseTag.appendChild(accountStatusNode);
            balanceAdjustmentResponseTag.appendChild(lockStatusNode);
            balanceAdjustmentResponseTag.appendChild(tariffPlanNode);

            BalanceNode.appendChild(balanceIdNode);
            BalanceNode.appendChild(lifeCycleNode);
            BalanceNode.appendChild(optionNode);
            BalanceNode.appendChild(amountNode);
            BalanceNode.appendChild(unitTypeNode);
            BalanceNode.appendChild(validityDateNode);
            BalanceNode.appendChild(graceDateNode);
            BalanceNode.appendChild(currentStateNode);
            BalancesNode.appendChild(BalanceNode);
            balanceAdjustmentResponseTag.appendChild(BalancesNode);
            StringBuffer strBuff = new StringBuffer();
            requestStr = write(document, strBuff, "");
            return requestStr;
        } catch (Exception e) {
            _log.error("generateBalanceAdjustmentResponse", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateBalanceAdjustmentResponse", "Entered requestStr: " + requestStr);
        }
    }

    private String generateLogoutRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateLogoutRequest", "Entered transactionId: " + map.get("TransactionId") + " interfaceId:" + map.get("InterfaceId"));
        String requestStr = null;
        try {
            Document document = _parser.parse(new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"UTF-8\"?><log:LogoutResponse xmlns:log=\"http://www.ferma.fr/ppg/logic/service/logout\"><ser:Status xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">" + map.get("Status") + "</ser:Status><ser:TransactionId xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">" + map.get("TransactionId") + "</ser:TransactionId><ser:InterfaceId xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\"/></log:LogoutResponse>").getBytes()));
            /*
             * NodeList rootNodes =
             * document.getElementsByTagName("LogoutRequest");
             * Element logoutRequestTag = (Element)rootNodes.item(0);
             * Element transactionIdNode =
             * document.createElement("TransactionId");
             * Element interfaceIdNode = document.createElement("InterfaceId");
             * transactionIdNode.appendChild(document.createTextNode((String)map.
             * get("TransactionId")));
             * interfaceIdNode.appendChild(document.createTextNode((String)map.get
             * ("InterfaceId")));
             * logoutRequestTag.appendChild(transactionIdNode);
             * logoutRequestTag.appendChild(interfaceIdNode);
             */
            StringBuffer strBuff = new StringBuffer();
            requestStr = write(document, strBuff, "");
            return requestStr;
        } catch (Exception e) {
            _log.error("generateLogoutRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateLogoutRequest", "Entered requestStr: " + requestStr);
        }
    }

    private HashMap parseGetAccountInfoResponse(String responseStr) throws Exception {

        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse", "Entered responseStr: " + responseStr);
        HashMap map = new HashMap();
        try {
            Document document = _parser.parse(new ByteArrayInputStream(responseStr.getBytes()));
            NodeList rootNodes = document.getElementsByTagName("AccountInfoGettingResponse");
            Element accountInfoGettingResponseTag = (Element) rootNodes.item(0);
            Element statusTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("Status").item(0);
            String status = ((Text) statusTag.getFirstChild()).getData().trim();
            System.out.println("Status: " + status);
            Element transactionIdTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("TransactionId").item(0);
            String transactionId = ((Text) transactionIdTag.getFirstChild()).getData().trim();
            System.out.println("TransactionId: " + transactionId);
            Element interfaceIdTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("InterfaceId").item(0);
            String interfaceId = ((Text) interfaceIdTag.getFirstChild()).getData().trim();
            System.out.println("InterfaceId: " + interfaceId);
            Element accessIdentifierTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("AccessIdentifier").item(0);
            String accessIdentifier = ((Text) accessIdentifierTag.getFirstChild()).getData().trim();
            System.out.println("AccessIdentifier: " + accessIdentifier);
            Element accessTypeTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("AccessType").item(0);
            String accessType = ((Text) accessTypeTag.getFirstChild()).getData().trim();
            System.out.println("AccessType: " + accessType);
            Element accountIdTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("AccountId").item(0);
            String accountId = ((Text) accountIdTag.getFirstChild()).getData().trim();
            System.out.println("AccountId: " + accountId);
            Element profileTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("Profile").item(0);
            String profile = ((Text) profileTag.getFirstChild()).getData().trim();
            System.out.println("Profile: " + profile);
            Element accountLanguageTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("AccountLanguage").item(0);
            String accountLanguage = ((Text) accountLanguageTag.getFirstChild()).getData().trim();
            System.out.println("AccountLanguage: " + accountLanguage);
            Element accountStatusTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("AccountStatus").item(0);
            String accountStatus = ((Text) accountStatusTag.getFirstChild()).getData().trim();
            System.out.println("AccountStatus: " + accountStatus);
            Element lockStatusTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("LockStatus").item(0);
            String lockStatus = ((Text) lockStatusTag.getFirstChild()).getData().trim();
            System.out.println("LockStatus: " + lockStatus);
            Element tariffPlanTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("TariffPlan").item(0);
            String tariffPlan = ((Text) tariffPlanTag.getFirstChild()).getData().trim();
            System.out.println("TariffPlan: " + tariffPlan);
            Element balanceIdTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("BalanceId").item(0);
            String balanceId = ((Text) balanceIdTag.getFirstChild()).getData().trim();
            System.out.println("BalanceId: " + balanceId);
            Element lifeCycleTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("LifeCycle").item(0);
            String lifeCycle = ((Text) lifeCycleTag.getFirstChild()).getData().trim();
            System.out.println("LifeCycle: " + lifeCycle);
            Element optionTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("Option").item(0);
            String option = ((Text) optionTag.getFirstChild()).getData().trim();
            System.out.println("Option: " + option);
            Element amountTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("Amount").item(0);
            String amount = ((Text) amountTag.getFirstChild()).getData().trim();
            System.out.println("Amount: " + amount);
            Element unitTypeTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("UnitType").item(0);
            String unitType = ((Text) unitTypeTag.getFirstChild()).getData().trim();
            System.out.println("UnitType: " + unitType);
            Element validityDateTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("ValidityDate").item(0);
            String validityDate = ((Text) validityDateTag.getFirstChild()).getData().trim();
            System.out.println("ValidityDate: " + validityDate);
            Element graceDateTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("GraceDate").item(0);
            String graceDate = ((Text) graceDateTag.getFirstChild()).getData().trim();
            System.out.println("GraceDate: " + graceDate);
            Element currentStateTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("CurrentState").item(0);
            String currentState = ((Text) currentStateTag.getFirstChild()).getData().trim();
            System.out.println("CurrentState: " + currentState);
            map.put("Status", status);
            map.put("TransactionId", transactionId);
            map.put("InterfaceId", interfaceId);
            map.put("AccessIdentifier", accessIdentifier);
            map.put("AccessType", accessType);
            map.put("AccountId", accountId);
            map.put("Profile", profile);
            map.put("AccountLanguage", accountLanguage);
            map.put("AccountStatus", accountStatus);
            map.put("LockStatus", lockStatus);
            map.put("TariffPlan", tariffPlan);
            map.put("BalanceId", balanceId);
            map.put("LifeCycle", lifeCycle);
            map.put("Option", option);
            map.put("Amount", amount);
            map.put("UnitType", unitType);
            map.put("ValidityDate", validityDate);
            map.put("GraceDate", graceDate);
            map.put("CurrentState", currentState);
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

    private String generateGetAccountInfoRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetAccountInfoRequest", "Entered transactionId: " + map.get("TransactionId") + " interfaceId:" + map.get("InterfaceId") + " accessIdentifier: " + map.get("AccessIdentifier") + " accessType: " + map.get("AccessType"));
        String requestStr = null;
        try {
            Document document = _parser.parse(new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><AccountInfoGettingRequest></AccountInfoGettingRequest>".getBytes()));
            NodeList rootNodes = document.getElementsByTagName("AccountInfoGettingRequest");
            Element accountInfoGettingRequestTag = (Element) rootNodes.item(0);
            Element transactionIdNode = document.createElement("TransactionId");
            Element interfaceIdNode = document.createElement("InterfaceId");
            Element accessIdentifierNode = document.createElement("AccessIdentifier");
            Element accessTypeNode = document.createElement("AccessType");
            transactionIdNode.appendChild(document.createTextNode((String) map.get("TransactionId")));
            interfaceIdNode.appendChild(document.createTextNode((String) map.get("InterfaceId")));
            accessIdentifierNode.appendChild(document.createTextNode((String) map.get("AccessIdentifier")));
            accessTypeNode.appendChild(document.createTextNode((String) map.get("AccessType")));
            accountInfoGettingRequestTag.appendChild(transactionIdNode);
            accountInfoGettingRequestTag.appendChild(interfaceIdNode);
            accountInfoGettingRequestTag.appendChild(accessIdentifierNode);
            accountInfoGettingRequestTag.appendChild(accessTypeNode);
            StringBuffer strBuff = new StringBuffer();
            requestStr = write(document, strBuff, "");
            return requestStr;
        } catch (Exception e) {
            _log.error("generateGetAccountInfoRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateGetAccountInfoRequest", "Entered requestStr: " + requestStr);
        }

    }

    private HashMap parseRechargeBalanceResponse(String responseStr) throws Exception {

        if (_log.isDebugEnabled())
            _log.debug("parseRechargeBalanceResponse", "Entered responseStr: " + responseStr);
        HashMap map = new HashMap();
        try {
            Document document = _parser.parse(new ByteArrayInputStream(responseStr.getBytes()));
            NodeList rootNodes = document.getElementsByTagName("BalanceRechargingResponse");
            Element balanceRechargingResponseTag = (Element) rootNodes.item(0);
            Element statusTag = (Element) balanceRechargingResponseTag.getElementsByTagName("Status").item(0);
            String status = ((Text) statusTag.getFirstChild()).getData().trim();
            System.out.println("Status: " + status);
            Element transactionIdTag = (Element) balanceRechargingResponseTag.getElementsByTagName("TransactionId").item(0);
            String transactionId = ((Text) transactionIdTag.getFirstChild()).getData().trim();
            System.out.println("TransactionId: " + transactionId);
            Element interfaceIdTag = (Element) balanceRechargingResponseTag.getElementsByTagName("InterfaceId").item(0);
            String interfaceId = ((Text) interfaceIdTag.getFirstChild()).getData().trim();
            System.out.println("InterfaceId: " + interfaceId);
            Element accessIdentifierTag = (Element) balanceRechargingResponseTag.getElementsByTagName("AccessIdentifier").item(0);
            String accessIdentifier = ((Text) accessIdentifierTag.getFirstChild()).getData().trim();
            System.out.println("AccessIdentifier: " + accessIdentifier);
            Element accessTypeTag = (Element) balanceRechargingResponseTag.getElementsByTagName("AccessType").item(0);
            String accessType = ((Text) accessTypeTag.getFirstChild()).getData().trim();
            System.out.println("AccessType: " + accessType);
            Element accountIdTag = (Element) balanceRechargingResponseTag.getElementsByTagName("AccountId").item(0);
            String accountId = ((Text) accountIdTag.getFirstChild()).getData().trim();
            System.out.println("AccountId: " + accountId);
            Element profileTag = (Element) balanceRechargingResponseTag.getElementsByTagName("Profile").item(0);
            String profile = ((Text) profileTag.getFirstChild()).getData().trim();
            System.out.println("Profile: " + profile);
            Element accountLanguageTag = (Element) balanceRechargingResponseTag.getElementsByTagName("AccountLanguage").item(0);
            String accountLanguage = ((Text) accountLanguageTag.getFirstChild()).getData().trim();
            System.out.println("AccountLanguage: " + accountLanguage);
            Element accountStatusTag = (Element) balanceRechargingResponseTag.getElementsByTagName("AccountStatus").item(0);
            String accountStatus = ((Text) accountStatusTag.getFirstChild()).getData().trim();
            System.out.println("AccountStatus: " + accountStatus);
            Element lockStatusTag = (Element) balanceRechargingResponseTag.getElementsByTagName("LockStatus").item(0);
            String lockStatus = ((Text) lockStatusTag.getFirstChild()).getData().trim();
            System.out.println("LockStatus: " + lockStatus);
            Element tariffPlanTag = (Element) balanceRechargingResponseTag.getElementsByTagName("TariffPlan").item(0);
            String tariffPlan = ((Text) tariffPlanTag.getFirstChild()).getData().trim();
            System.out.println("TariffPlan: " + tariffPlan);
            Element balanceIdTag = (Element) balanceRechargingResponseTag.getElementsByTagName("BalanceId").item(0);
            String balanceId = ((Text) balanceIdTag.getFirstChild()).getData().trim();
            System.out.println("BalanceId: " + balanceId);
            Element lifeCycleTag = (Element) balanceRechargingResponseTag.getElementsByTagName("LifeCycle").item(0);
            String lifeCycle = ((Text) lifeCycleTag.getFirstChild()).getData().trim();
            System.out.println("LifeCycle: " + lifeCycle);
            Element optionTag = (Element) balanceRechargingResponseTag.getElementsByTagName("Option").item(0);
            String option = ((Text) optionTag.getFirstChild()).getData().trim();
            System.out.println("Option: " + option);
            Element amountTag = (Element) balanceRechargingResponseTag.getElementsByTagName("Amount").item(0);
            String amount = ((Text) amountTag.getFirstChild()).getData().trim();
            System.out.println("Amount: " + amount);
            Element unitTypeTag = (Element) balanceRechargingResponseTag.getElementsByTagName("UnitType").item(0);
            String unitType = ((Text) unitTypeTag.getFirstChild()).getData().trim();
            System.out.println("UnitType: " + unitType);
            Element validityDateTag = (Element) balanceRechargingResponseTag.getElementsByTagName("ValidityDate").item(0);
            String validityDate = ((Text) validityDateTag.getFirstChild()).getData().trim();
            System.out.println("ValidityDate: " + validityDate);
            Element graceDateTag = (Element) balanceRechargingResponseTag.getElementsByTagName("GraceDate").item(0);
            String graceDate = ((Text) graceDateTag.getFirstChild()).getData().trim();
            System.out.println("GraceDate: " + graceDate);
            Element currentStateTag = (Element) balanceRechargingResponseTag.getElementsByTagName("CurrentState").item(0);
            String currentState = ((Text) currentStateTag.getFirstChild()).getData().trim();
            System.out.println("CurrentState: " + currentState);
            map.put("Status", status);
            map.put("TransactionId", transactionId);
            map.put("InterfaceId", interfaceId);
            map.put("AccessIdentifier", accessIdentifier);
            map.put("AccessType", accessType);
            map.put("AccountId", accountId);
            map.put("Profile", profile);
            map.put("AccountLanguage", accountLanguage);
            map.put("AccountStatus", accountStatus);
            map.put("LockStatus", lockStatus);
            map.put("TariffPlan", tariffPlan);
            map.put("BalanceId", balanceId);
            map.put("LifeCycle", lifeCycle);
            map.put("Option", option);
            map.put("Amount", amount);
            map.put("UnitType", unitType);
            map.put("ValidityDate", validityDate);
            map.put("GraceDate", graceDate);
            map.put("CurrentState", currentState);
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

    private String generateRechargeBalanceRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRechargeBalanceRequest", "Entered transactionId: " + map.get("TransactionId") + " interfaceId:" + map.get("InterfaceId") + " accessIdentifier: " + map.get("AccessIdentifier") + " accessType: " + map.get("AccessType") + " accountID: " + map.get("AccountId") + " balanceId: " + map.get("BalanceId") + " rechargeValue: " + map.get("RechargeValue") + " unitType: " + map.get("UnitType"));
        String requestStr = null;
        try {
            Document document = _parser.parse(new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><BalanceRechargingRequest></BalanceRechargingRequest>".getBytes()));
            NodeList rootNodes = document.getElementsByTagName("BalanceRechargingRequest");
            Element balanceRechargingRequestTag = (Element) rootNodes.item(0);
            Element transactionIdNode = document.createElement("TransactionId");
            Element interfaceIdNode = document.createElement("InterfaceId");
            Element accessIdentifierNode = document.createElement("AccessIdentifier");
            Element accessTypeNode = document.createElement("AccessType");
            Element accountIdNode = document.createElement("AccountId");
            Element balanceIdNode = document.createElement("BalanceId");
            Element rechargeValueNode = document.createElement("RechargeValue");
            Element unitTypeNode = document.createElement("UnitType");
            transactionIdNode.appendChild(document.createTextNode((String) map.get("TransactionId")));
            interfaceIdNode.appendChild(document.createTextNode((String) map.get("InterfaceId")));
            accessIdentifierNode.appendChild(document.createTextNode((String) map.get("AccessIdentifier")));
            accessTypeNode.appendChild(document.createTextNode((String) map.get("AccessType")));
            accountIdNode.appendChild(document.createTextNode((String) map.get("AccountId")));
            balanceIdNode.appendChild(document.createTextNode((String) map.get("BalanceId")));
            rechargeValueNode.appendChild(document.createTextNode((String) map.get("RechargeValue")));
            unitTypeNode.appendChild(document.createTextNode((String) map.get("UnitType")));
            balanceRechargingRequestTag.appendChild(transactionIdNode);
            balanceRechargingRequestTag.appendChild(interfaceIdNode);
            balanceRechargingRequestTag.appendChild(accessIdentifierNode);
            balanceRechargingRequestTag.appendChild(accessTypeNode);
            balanceRechargingRequestTag.appendChild(accountIdNode);
            balanceRechargingRequestTag.appendChild(balanceIdNode);
            balanceRechargingRequestTag.appendChild(rechargeValueNode);
            balanceRechargingRequestTag.appendChild(unitTypeNode);
            StringBuffer strBuff = new StringBuffer();
            requestStr = write(document, strBuff, "");
            return requestStr;
        } catch (Exception e) {
            _log.error("generateRechargeBalanceRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRechargeBalanceRequest", "Entered requestStr: " + requestStr);
        }
    }

    private HashMap parseBalanceAdjustmentResponse(String responseStr) throws Exception {

        if (_log.isDebugEnabled())
            _log.debug("parseBalanceAdjustmentResponse", "Entered responseStr: " + responseStr);
        HashMap map = new HashMap();
        try {
            Document document = _parser.parse(new ByteArrayInputStream(responseStr.getBytes()));

            NodeList rootNodes = document.getElementsByTagName("BalanceAdjustmentResponse");
            Element balanceAdjustmentResponseTag = (Element) rootNodes.item(0);
            Element statusTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("Status").item(0);
            String status = ((Text) statusTag.getFirstChild()).getData().trim();
            System.out.println("Status: " + status);
            Element transactionIdTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("TransactionId").item(0);
            String transactionId = ((Text) transactionIdTag.getFirstChild()).getData().trim();
            System.out.println("TransactionId: " + transactionId);
            Element interfaceIdTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("InterfaceId").item(0);
            String interfaceId = ((Text) interfaceIdTag.getFirstChild()).getData().trim();
            System.out.println("InterfaceId: " + interfaceId);
            Element accessIdentifierTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("AccessIdentifier").item(0);
            String accessIdentifier = ((Text) accessIdentifierTag.getFirstChild()).getData().trim();
            System.out.println("AccessIdentifier: " + accessIdentifier);
            Element accessTypeTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("AccessType").item(0);
            String accessType = ((Text) accessTypeTag.getFirstChild()).getData().trim();
            System.out.println("AccessType: " + accessType);
            Element accountIdTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("AccountId").item(0);
            String accountId = ((Text) accountIdTag.getFirstChild()).getData().trim();
            System.out.println("AccountId: " + accountId);
            Element profileTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("Profile").item(0);
            String profile = ((Text) profileTag.getFirstChild()).getData().trim();
            System.out.println("Profile: " + profile);

            Element accountLanguageTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("AccountLanguage").item(0);
            String accountLanguage = ((Text) accountLanguageTag.getFirstChild()).getData().trim();
            System.out.println("AccountLanguage: " + accountLanguage);

            Element accountStatusTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("AccountStatus").item(0);
            String accountStatus = ((Text) accountStatusTag.getFirstChild()).getData().trim();
            System.out.println("AccountStatus: " + accountStatus);
            Element lockStatusTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("LockStatus").item(0);
            String lockStatus = ((Text) lockStatusTag.getFirstChild()).getData().trim();
            System.out.println("LockStatus: " + lockStatus);
            Element tariffPlanTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("TariffPlan").item(0);
            String tariffPlan = ((Text) tariffPlanTag.getFirstChild()).getData().trim();
            System.out.println("TariffPlan: " + tariffPlan);
            Element balanceIdTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("BalanceId").item(0);
            String balanceId = ((Text) balanceIdTag.getFirstChild()).getData().trim();
            System.out.println("BalanceId: " + balanceId);
            Element lifeCycleTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("LifeCycle").item(0);
            String lifeCycle = ((Text) lifeCycleTag.getFirstChild()).getData().trim();
            System.out.println("LifeCycle: " + lifeCycle);
            Element optionTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("Option").item(0);
            String option = ((Text) optionTag.getFirstChild()).getData().trim();
            System.out.println("Option: " + option);
            Element amountTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("Amount").item(0);
            String amount = ((Text) amountTag.getFirstChild()).getData().trim();
            System.out.println("Amount: " + amount);
            Element unitTypeTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("UnitType").item(0);
            String unitType = ((Text) unitTypeTag.getFirstChild()).getData().trim();
            System.out.println("UnitType: " + unitType);
            Element validityDateTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("ValidityDate").item(0);
            String validityDate = ((Text) validityDateTag.getFirstChild()).getData().trim();
            System.out.println("ValidityDate: " + validityDate);
            Element graceDateTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("GraceDate").item(0);
            String graceDate = ((Text) graceDateTag.getFirstChild()).getData().trim();
            System.out.println("GraceDate: " + graceDate);
            Element currentStateTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("CurrentState").item(0);
            String currentState = ((Text) currentStateTag.getFirstChild()).getData().trim();
            System.out.println("CurrentState: " + currentState);
            map.put("Status", status);
            map.put("TransactionId", transactionId);
            map.put("InterfaceId", interfaceId);
            map.put("AccessIdentifier", accessIdentifier);
            map.put("AccessType", accessType);
            map.put("AccountId", accountId);
            map.put("Profile", profile);
            map.put("AccountStatus", accountStatus);
            map.put("LockStatus", lockStatus);
            map.put("TariffPlan", tariffPlan);
            map.put("BalanceId", balanceId);
            map.put("LifeCycle", lifeCycle);
            map.put("Option", option);
            map.put("Amount", amount);
            map.put("UnitType", unitType);
            map.put("ValidityDate", validityDate);
            map.put("GraceDate", graceDate);
            map.put("CurrentState", currentState);
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

    private String generateBalanceAdjustmentRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateBalanceAdjustmentRequest", "Entered transactionId: " + map.get("TransactionId") + " interfaceId:" + map.get("InterfaceId") + " accessIdentifier: " + map.get("AccessIdentifier") + " accessType: " + map.get("AccessType") + " accountID: " + map.get("AccountId") + " profil: " + map.get("Profil") + " accountStatus: " + map.get("AccountStatus") + " lockStatus: " + map.get("LockStatus") + " rechInstallment: " + map.get("RechInstallment") + " balanceId: " + map.get("BalanceId") + " lifeCycle: " + map.get("LifeCycle") + " option: " + map.get("Option") + " amount: " + map.get("Amount") + " unitType: " + map.get("UnitType") + " currentValidityDate: " + map.get("CurrentValidityDate") + " currentGraceDate: " + map.get("CurrentGraceDate") + " newValidityDate: " + map.get("NewValidityDate") + " newGraceDate: " + map.get("NewGraceDate") + " currentState: " + map.get("CurrentState"));
        String requestStr = null;
        try {
            Document document = _parser.parse(new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><BalanceAdjustmentRequest></BalanceAdjustmentRequest>".getBytes()));
            NodeList rootNodes = document.getElementsByTagName("BalanceAdjustmentRequest");
            Element balanceAdjustmentRequestTag = (Element) rootNodes.item(0);
            Element transactionIdNode = document.createElement("TransactionId");
            Element interfaceIdNode = document.createElement("InterfaceId");
            Element accessIdentifierNode = document.createElement("AccessIdentifier");
            Element accessTypeNode = document.createElement("AccessType");
            Element accountIdNode = document.createElement("AccountId");
            Element profileNode = document.createElement("Profile");
            Element accountStatusNode = document.createElement("AccountStatus");
            Element lockStatusNode = document.createElement("LockStatus");
            Element rechInstallmentNode = document.createElement("RechInstallment");
            Element balanceIdNode = document.createElement("BalanceId");
            Element lifeCycleNode = document.createElement("LifeCycle");
            Element optionNode = document.createElement("Option");
            Element amountNode = document.createElement("Amount");
            Element unitTypeNode = document.createElement("UnitType");
            Element currentValidityDateNode = document.createElement("CurrentValidityDate");
            Element currentGraceDateNode = document.createElement("CurrentGraceDate");
            Element newValidityDateNode = document.createElement("NewValidityDate");
            Element newGraceDateNode = document.createElement("NewGraceDate");
            Element currentStateNode = document.createElement("CurrentState");
            transactionIdNode.appendChild(document.createTextNode((String) map.get("TransactionId")));
            interfaceIdNode.appendChild(document.createTextNode((String) map.get("InterfaceId")));
            accessIdentifierNode.appendChild(document.createTextNode((String) map.get("AccessIdentifier")));
            accessTypeNode.appendChild(document.createTextNode((String) map.get("AccessType")));
            accountIdNode.appendChild(document.createTextNode((String) map.get("AccountId")));
            profileNode.appendChild(document.createTextNode((String) map.get("Profile")));
            accountStatusNode.appendChild(document.createTextNode((String) map.get("AccountStatus")));
            lockStatusNode.appendChild(document.createTextNode((String) map.get("LockStatus")));
            rechInstallmentNode.appendChild(document.createTextNode((String) map.get("RechInstallment")));
            balanceIdNode.appendChild(document.createTextNode((String) map.get("BalanceId")));
            lifeCycleNode.appendChild(document.createTextNode((String) map.get("LifeCycle")));
            optionNode.appendChild(document.createTextNode((String) map.get("Option")));
            amountNode.appendChild(document.createTextNode((String) map.get("Amount")));
            unitTypeNode.appendChild(document.createTextNode((String) map.get("UnitType")));
            currentValidityDateNode.appendChild(document.createTextNode((String) map.get("CurrentValidityDate")));
            currentGraceDateNode.appendChild(document.createTextNode((String) map.get("CurrentGraceDate")));
            newValidityDateNode.appendChild(document.createTextNode((String) map.get("NewValidityDate")));
            newGraceDateNode.appendChild(document.createTextNode((String) map.get("NewGraceDate")));
            currentStateNode.appendChild(document.createTextNode((String) map.get("CurrentState")));
            balanceAdjustmentRequestTag.appendChild(transactionIdNode);
            balanceAdjustmentRequestTag.appendChild(interfaceIdNode);
            balanceAdjustmentRequestTag.appendChild(accessIdentifierNode);
            balanceAdjustmentRequestTag.appendChild(accessTypeNode);
            balanceAdjustmentRequestTag.appendChild(accountIdNode);
            balanceAdjustmentRequestTag.appendChild(profileNode);
            balanceAdjustmentRequestTag.appendChild(accountStatusNode);
            balanceAdjustmentRequestTag.appendChild(lockStatusNode);
            balanceAdjustmentRequestTag.appendChild(rechInstallmentNode);
            balanceAdjustmentRequestTag.appendChild(balanceIdNode);
            balanceAdjustmentRequestTag.appendChild(lifeCycleNode);
            balanceAdjustmentRequestTag.appendChild(optionNode);
            balanceAdjustmentRequestTag.appendChild(amountNode);
            balanceAdjustmentRequestTag.appendChild(unitTypeNode);
            balanceAdjustmentRequestTag.appendChild(currentValidityDateNode);
            balanceAdjustmentRequestTag.appendChild(currentGraceDateNode);
            balanceAdjustmentRequestTag.appendChild(newValidityDateNode);
            balanceAdjustmentRequestTag.appendChild(newGraceDateNode);
            balanceAdjustmentRequestTag.appendChild(currentStateNode);
            StringBuffer strBuff = new StringBuffer();
            requestStr = write(document, strBuff, "");
            return requestStr;
        } catch (Exception e) {
            _log.error("generateBalanceAdjustmentRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateBalanceAdjustmentRequest", "Entered requestStr: " + requestStr);
        }
    }

    public int requestParser(String requestStr) throws Exception {

        if (_log.isDebugEnabled())
            _log.debug("requestParser", "Entered responseStr: " + requestStr);
        int action = -1;
        try {
            if (requestStr.indexOf("LoginRequest") != -1) {
                action = 0;
            } else if (requestStr.indexOf("AccountInfoGettingRequest") != -1) {
                action = 1;
            } else if (requestStr.indexOf("BalanceRechargingRequest") != -1) {
                action = 2;
            } else if (requestStr.indexOf("BalanceAdjustmentRequest") != -1) {
                action = 3;
            } else if (requestStr.indexOf("LogoutRequest") != -1) {
                action = 4;
            }

        } catch (Exception e) {
            _log.error("requestParser", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("requestParser", "Entered requestStr: " + requestStr);
        }
        return action;
    }
}