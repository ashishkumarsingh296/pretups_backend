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
 *                               Manoj kumar Nov 11,2005
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
import org.w3c.dom.*; // W3C DOM classes for traversing the document
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.util.XMLRequestResponse;
import java.io.*; // For reading the input file
import java.util.HashMap;

public class FermaRequestResponse extends XMLRequestResponse {
    public final static int ACTION_LOGIN = 0;

    public final static int ACTION_ACCOUNT_INFO = 1;

    public final static int ACTION_RECHARGE_BALANCE = 2;

    public final static int ACTION_BALANCE_ADJUST = 3;

    public final static int ACTION_LOGOUT = 4;

    public Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * this method construct the request in XML String from HashMap
     * 
     * @param action
     *            int
     * @param map
     *            java.util.HashMap
     * @return str java.lang.String
     */
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

    /**
     * this method parse the response from XML String into HashMap
     * 
     * @param action
     *            int
     * @param responseStr
     *            java.lang.String
     * @return map java.util.HashMap
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
     * this method connstruct LoginRequest in XML format from map
     * 
     * @param map
     *            java.util.HsahMap
     * @return requestStr java.lang.String
     */
    private String generateLoginRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateLoginRequest", "Entered userName: " + map.get("UserName") + " password:" + map.get("Password") + " protocol: " + map.get("ProtocolVersion") + "transactionId:" + map.get("IN_TXN_ID"));
        String requestStr = null;
        try {
            Document document = _parser.parse(new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"UTF-8\"?><envelope  action=\"login\"><LoginRequest xmlns=\"http://www.ferma.fr/ppg/logic/service/login\"><ns1:TransactionId xmlns:ns1=\"http://www.ferma.fr/ppg/logic/service\">" + (String) map.get("IN_TXN_ID") + "</ns1:TransactionId></LoginRequest></envelope>").getBytes()));
            NodeList rootNodes = document.getElementsByTagName("LoginRequest");
            Element loginRequestTag = (Element) rootNodes.item(0);
            // Element transactionIdNode =
            // document.createElement("TransactionId");
            Element userNameNode = document.createElement("UserName");
            Element passwordNode = document.createElement("Password");
            Element protocolVersionNode = document.createElement("ProtocolVersion");
            // transactionIdNode.appendChild(document.createTextNode((String)map.get("TransactionId")));
            userNameNode.appendChild(document.createTextNode((String) map.get("UserName")));
            passwordNode.appendChild(document.createTextNode((String) map.get("Password")));
            protocolVersionNode.appendChild(document.createTextNode((String) map.get("ProtocolVersion")));
            // loginRequestTag.appendChild(transactionIdNode);
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

    /**
     * this method parse LoginResponse from XML String to HashMap
     * 
     * @param responseStr
     *            java.lang.String
     * @return map java.util.HsahMap
     */
    private HashMap parseLoginResponse(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseLoginResponse", "Entered responseStr: " + responseStr);
        HashMap map = new HashMap();
        try {
            Document document = _parser.parse(new ByteArrayInputStream(responseStr.getBytes()));
            NodeList rootNodes = document.getElementsByTagName("log:LoginResponse");
            Element loginResponseTag = (Element) rootNodes.item(0);
            Element statusTag = (Element) loginResponseTag.getElementsByTagName("ser:Status").item(0);
            Text statusNode = (Text) statusTag.getFirstChild();
            String status = null;
            if (statusNode == null)
                status = "";
            else
                status = statusNode.getData().trim();
            Element transactionIdTag = (Element) loginResponseTag.getElementsByTagName("ser:TransactionId").item(0);
            Text transactionIdNode = (Text) transactionIdTag.getFirstChild();
            String transactionId = null;
            if (transactionIdNode == null)
                transactionId = "";
            else
                transactionId = transactionIdNode.getData().trim();
            Element interfaceIdTag = (Element) loginResponseTag.getElementsByTagName("ser:InterfaceId").item(0);
            Text interfaceIdNode = (Text) interfaceIdTag.getFirstChild();
            String interfaceId = null;
            if (interfaceIdNode == null)
                interfaceId = "";
            else
                interfaceId = interfaceIdNode.getData().trim();

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

    /**
     * this method connstruct GetAccountInfoRequest in XML format from Map
     * 
     * @param map
     *            java.util.HsahMap
     * @return requestStr java.lang.String
     */
    private String generateGetAccountInfoRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetAccountInfoRequest", "Entered map=" + map);
        String requestStr = null;
        try {
            Document document = _parser.parse(new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"UTF-8\"?><envelope  action=\"getAccountInfo\"><AccountInfoGettingRequest xmlns=\"http://www.ferma.fr/ppg/logic/service/accountinfo\"><ns1:TransactionId xmlns:ns1=\"http://www.ferma.fr/ppg/logic/service\">" + (String) map.get("IN_TXN_ID") + "</ns1:TransactionId></AccountInfoGettingRequest></envelope>").getBytes()));
            NodeList rootNodes = document.getElementsByTagName("AccountInfoGettingRequest");
            Element accountInfoGettingRequestTag = (Element) rootNodes.item(0);
            Element interfaceIdNode = document.createElement("InterfaceId");
            Element accessIdentifierNode = document.createElement("AccessIdentifier");
            Element accessTypeNode = document.createElement("AccessType");
            Element balanceIdNode = document.createElement("BalanceId");
            Element BalanceNode = document.createElement("Balance");
            interfaceIdNode.appendChild(document.createTextNode((String) map.get("FERMA_INTERFACE_ID")));
            accessIdentifierNode.appendChild(document.createTextNode(InterfaceUtil.getFilterMSISDN((String) map.get("INTERFACE_ID"), (String) map.get("MSISDN"))));
            accessTypeNode.appendChild(document.createTextNode((String) map.get("AccessType")));
            balanceIdNode.appendChild(document.createTextNode((String) map.get("BalanceId")));
            accountInfoGettingRequestTag.appendChild(interfaceIdNode);
            accountInfoGettingRequestTag.appendChild(accessIdentifierNode);
            accountInfoGettingRequestTag.appendChild(accessTypeNode);
            BalanceNode.appendChild(balanceIdNode);
            accountInfoGettingRequestTag.appendChild(BalanceNode);
            StringBuffer strBuff = new StringBuffer();
            requestStr = write(document, strBuff, "");
            requestStr = requestStr.replaceAll("xml version='1.0'", "xml version='1.0' encoding=\"UTF-8\"");
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
     * this method parse GetAccountInfoResponse from XML String to HashMap
     * 
     * @param responseStr
     *            java.lang.String
     * @return map java.util.HsahMap
     */
    private HashMap parseGetAccountInfoResponse(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse", "Entered responseStr: " + responseStr);
        HashMap map = new HashMap();
        try {
            Document document = _parser.parse(new ByteArrayInputStream(responseStr.getBytes()));
            NodeList rootNodes = document.getElementsByTagName("ser:PrepaidServiceResponse");
            Element accountInfoGettingResponseTag = (Element) rootNodes.item(0);
            Element statusTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("ser:Status").item(0);
            Text statusNode = (Text) statusTag.getFirstChild();
            String status = null;
            if (statusNode == null)
                status = "";
            else
                status = statusNode.getData().trim();
            map.put("Status", status);

            Element transactionIdTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("ser:TransactionId").item(0);
            Text transactionIdNode = (Text) transactionIdTag.getFirstChild();
            String transactionId = null;
            if (transactionIdNode == null)
                transactionId = "";
            else
                transactionId = transactionIdNode.getData().trim();
            map.put("TransactionId", transactionId);
            if (status.equals("0")) {
                String interfaceId = null;
                Element interfaceIdTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("ser:InterfaceId").item(0);
                Text interfaceIdNode = (Text) interfaceIdTag.getFirstChild();
                if (interfaceIdNode == null)
                    interfaceId = "";
                else
                    interfaceId = interfaceIdNode.getData().trim();
                String accessIdentifier = null;
                Element accessIdentifierTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("ser:AccessIdentifier").item(0);
                Text accessIdentifierNode = (Text) accessIdentifierTag.getFirstChild();
                if (accessIdentifierNode == null)
                    accessIdentifier = "";
                else
                    accessIdentifier = accessIdentifierNode.getData().trim();
                String accessType = null;
                Element accessTypeTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("ser:AccessType").item(0);
                Text accessTypeNode = (Text) accessTypeTag.getFirstChild();
                if (accessTypeNode == null)
                    accessType = "";
                else
                    accessType = accessTypeNode.getData().trim();
                String accountId = null;
                Element accountIdTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("ser:AccountId").item(0);
                Text accountIdNode = (Text) accountIdTag.getFirstChild();
                if (accountIdNode == null)
                    accountId = "";
                else
                    accountId = accountIdNode.getData().trim();
                String profile = null;
                Element profileTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("ser:Profil").item(0);
                Text profileNode = (Text) profileTag.getFirstChild();
                if (profileNode == null)
                    profile = "";
                else
                    profile = profileNode.getData().trim();
                String accountLanguage = null;
                Element AccountLanguageTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("ser:AccountLanguage").item(0);
                Text accountLanguageNode = (Text) AccountLanguageTag.getFirstChild();
                if (accountLanguageNode == null)
                    accountLanguage = "";
                else
                    accountLanguage = accountLanguageNode.getData().trim();
                String accountStatus = null;
                Element accountStatusTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("ser:AccountStatus").item(0);
                Text accountStatusNode = (Text) accountStatusTag.getFirstChild();
                if (accountStatusNode == null)
                    accountStatus = "";
                else
                    accountStatus = accountStatusNode.getData().trim();
                String lockStatus = null;
                Element lockStatusTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("ser:LockStatus").item(0);
                Text lockStatusNode = (Text) lockStatusTag.getFirstChild();
                if (lockStatusNode == null)
                    lockStatus = "";
                else
                    lockStatus = lockStatusNode.getData().trim();
                String tariffPlan = null;
                Element tariffPlanTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("ser:TariffPlan").item(0);
                Text tariffPlanNode = (Text) tariffPlanTag.getFirstChild();
                if (tariffPlanNode == null)
                    tariffPlan = "";
                else
                    tariffPlan = tariffPlanNode.getData().trim();
                NodeList balancesNode = accountInfoGettingResponseTag.getElementsByTagName("ser:Balances");
                Element balancesTag = (Element) balancesNode.item(0);
                NodeList balanceNode = balancesTag.getElementsByTagName("ser:Balance");
                Element balanceTag = (Element) balanceNode.item(0);
                String balanceId = null;
                Element balanceIdTag = (Element) balanceTag.getElementsByTagName("ser:BalanceId").item(0);
                Text balanceIdNode = (Text) balanceIdTag.getFirstChild();
                if (balanceIdNode == null)
                    balanceId = "";
                else
                    balanceId = balanceIdNode.getData().trim();
                String lifeCycle = null;
                Element lifeCycleTag = (Element) balanceTag.getElementsByTagName("ser:LifeCycle").item(0);
                Text lifeCycleNode = (Text) lifeCycleTag.getFirstChild();
                if (lifeCycleNode == null)
                    lifeCycle = "";
                else
                    lifeCycle = lifeCycleNode.getData().trim();
                String option = null;
                Element optionTag = (Element) balanceTag.getElementsByTagName("ser:Option").item(0);
                Text optionNode = (Text) optionTag.getFirstChild();
                if (optionNode == null)
                    option = "";
                else
                    option = optionNode.getData().trim();
                String amount = null;
                Element amountTag = (Element) balanceTag.getElementsByTagName("ser:Amount").item(0);
                Text amountNode = (Text) amountTag.getFirstChild();
                if (amountNode == null)
                    amount = "";
                else
                    amount = amountNode.getData().trim();
                String unitType = null;
                Element unitTypeTag = (Element) balanceTag.getElementsByTagName("ser:UnitType").item(0);
                Text unitTypeNode = (Text) unitTypeTag.getFirstChild();
                if (unitTypeNode == null)
                    unitType = "";
                else
                    unitType = unitTypeNode.getData().trim();
                String validityDate = null;
                Element validityDateTag = (Element) balanceTag.getElementsByTagName("ser:ValidityDate").item(0);
                Text validityDateNode = (Text) validityDateTag.getFirstChild();
                if (validityDateNode == null)
                    validityDate = "";
                else
                    validityDate = validityDateNode.getData().trim();
                String graceDate = null;
                Element graceDateTag = (Element) balanceTag.getElementsByTagName("ser:GraceDate").item(0);
                Text graceDateNode = (Text) graceDateTag.getFirstChild();
                if (graceDateNode == null)
                    graceDate = "";
                else
                    graceDate = graceDateNode.getData().trim();
                String currentState = null;
                Element currentStateTag = (Element) balanceTag.getElementsByTagName("ser:State").item(0);
                Text currentStateNode = (Text) currentStateTag.getFirstChild();
                if (currentStateNode == null)
                    currentState = "";
                else
                    currentState = currentStateNode.getData().trim();
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
     * this method connstruct RechargeBalanceRequest in XML format from map
     * 
     * @param map
     *            java.util.HsahMap
     * @return requestStr java.lang.String
     */
    private String generateRechargeBalanceRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRechargeBalanceRequest", "Entered map=" + map);
        String requestStr = null;
        try {
            Document document = _parser.parse(new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"UTF-8\"?><envelope  action=\"balanceRecharge\"><BalanceRechargingRequest xmlns=\"http://www.ferma.fr/ppg/logic/service/recharging\"><ns1:TransactionId xmlns:ns1=\"http://www.ferma.fr/ppg/logic/service\">" + (String) map.get("IN_TXN_ID") + "</ns1:TransactionId></BalanceRechargingRequest></envelope>").getBytes()));
            NodeList rootNodes = document.getElementsByTagName("BalanceRechargingRequest");
            Element balanceRechargingRequestTag = (Element) rootNodes.item(0);
            Element interfaceIdNode = document.createElement("InterfaceId");
            Element accessIdentifierNode = document.createElement("AccessIdentifier");
            Element accessTypeNode = document.createElement("AccessType");
            Element accountIdNode = document.createElement("AccountId");
            Element balanceIdNode = document.createElement("BalanceId");
            Element rechargeValueNode = document.createElement("RechargeValue");
            Element unitTypeNode = document.createElement("UnitType");
            Element BalancesNode = document.createElement("Balances");
            Element BalanceNode = document.createElement("Balance");
            interfaceIdNode.appendChild(document.createTextNode((String) map.get("FERMA_INTERFACE_ID")));
            accessIdentifierNode.appendChild(document.createTextNode(InterfaceUtil.getFilterMSISDN((String) map.get("INTERFACE_ID"), (String) map.get("MSISDN"))));
            accessTypeNode.appendChild(document.createTextNode((String) map.get("AccessType")));
            accountIdNode.appendChild(document.createTextNode((String) map.get("AccountId")));
            balanceIdNode.appendChild(document.createTextNode((String) map.get("BalanceId")));
            rechargeValueNode.appendChild(document.createTextNode((String) map.get("CARD_GROUP")));
            unitTypeNode.appendChild(document.createTextNode((String) map.get("UnitType")));
            BalanceNode.appendChild(balanceIdNode);
            BalanceNode.appendChild(rechargeValueNode);
            BalanceNode.appendChild(unitTypeNode);
            BalancesNode.appendChild(BalanceNode);
            balanceRechargingRequestTag.appendChild(interfaceIdNode);
            balanceRechargingRequestTag.appendChild(accessIdentifierNode);
            balanceRechargingRequestTag.appendChild(accessTypeNode);
            balanceRechargingRequestTag.appendChild(accountIdNode);
            balanceRechargingRequestTag.appendChild(BalancesNode);
            StringBuffer strBuff = new StringBuffer();
            requestStr = write(document, strBuff, "");
            requestStr = requestStr.replaceAll("xml version='1.0'", "xml version='1.0' encoding=\"UTF-8\"");
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
     * this method parse RechargeBalanceResponse from XML string to HashMap
     * 
     * @param responseStr
     *            java.lang.String
     * @return map java.util.HsahMap
     */
    private HashMap parseRechargeBalanceResponse(String responseStr) throws Exception {

        if (_log.isDebugEnabled())
            _log.debug("parseRechargeBalanceResponse", "Entered responseStr: " + responseStr);
        HashMap map = new HashMap();
        try {
            Document document = _parser.parse(new ByteArrayInputStream(responseStr.getBytes()));
            NodeList rootNodes = document.getElementsByTagName("ser:PrepaidServiceResponse");
            Element balanceRechargingResponseTag = (Element) rootNodes.item(0);
            Element statusTag = (Element) balanceRechargingResponseTag.getElementsByTagName("ser:Status").item(0);
            Text statusNode = (Text) statusTag.getFirstChild();
            String status = null;
            if (statusNode == null)
                status = "";
            else
                status = statusNode.getData().trim();
            map.put("Status", status);

            Element transactionIdTag = (Element) balanceRechargingResponseTag.getElementsByTagName("ser:TransactionId").item(0);
            Text transactionIdNode = (Text) transactionIdTag.getFirstChild();
            String transactionId = null;
            if (transactionIdNode == null)
                transactionId = "";
            else
                transactionId = transactionIdNode.getData().trim();
            map.put("TransactionId", transactionId);
            if (status.equals("0")) {
                String interfaceId = null;
                Element interfaceIdTag = (Element) balanceRechargingResponseTag.getElementsByTagName("ser:InterfaceId").item(0);
                Text interfaceIdNode = (Text) interfaceIdTag.getFirstChild();
                if (interfaceIdNode == null)
                    interfaceId = "";
                else
                    interfaceId = interfaceIdNode.getData().trim();
                String accessIdentifier = null;
                Element accessIdentifierTag = (Element) balanceRechargingResponseTag.getElementsByTagName("ser:AccessIdentifier").item(0);
                Text accessIdentifierNode = (Text) accessIdentifierTag.getFirstChild();
                if (accessIdentifierNode == null)
                    accessIdentifier = "";
                else
                    accessIdentifier = accessIdentifierNode.getData().trim();
                String accessType = null;
                Element accessTypeTag = (Element) balanceRechargingResponseTag.getElementsByTagName("ser:AccessType").item(0);
                Text accessTypeNode = (Text) accessTypeTag.getFirstChild();
                if (accessTypeNode == null)
                    accessType = "";
                else
                    accessType = accessTypeNode.getData().trim();
                String accountId = null;
                Element accountIdTag = (Element) balanceRechargingResponseTag.getElementsByTagName("ser:AccountId").item(0);
                Text accountIdNode = (Text) accountIdTag.getFirstChild();
                if (accountIdNode == null)
                    accountId = "";
                else
                    accountId = accountIdNode.getData().trim();
                String profile = null;
                Element profileTag = (Element) balanceRechargingResponseTag.getElementsByTagName("ser:Profil").item(0);
                Text profileNode = (Text) profileTag.getFirstChild();
                if (profileNode == null)
                    profile = "";
                else
                    profile = profileNode.getData().trim();
                String accountLanguage = null;
                Element AccountLanguageTag = (Element) balanceRechargingResponseTag.getElementsByTagName("ser:AccountLanguage").item(0);
                Text accountLanguageNode = (Text) AccountLanguageTag.getFirstChild();
                if (accountLanguageNode == null)
                    accountLanguage = "";
                else
                    accountLanguage = accountLanguageNode.getData().trim();
                String accountStatus = null;
                Element accountStatusTag = (Element) balanceRechargingResponseTag.getElementsByTagName("ser:AccountStatus").item(0);
                Text accountStatusNode = (Text) accountStatusTag.getFirstChild();
                if (accountStatusNode == null)
                    accountStatus = "";
                else
                    accountStatus = accountStatusNode.getData().trim();
                String lockStatus = null;
                Element lockStatusTag = (Element) balanceRechargingResponseTag.getElementsByTagName("ser:LockStatus").item(0);
                Text lockStatusNode = (Text) lockStatusTag.getFirstChild();
                if (lockStatusNode == null)
                    lockStatus = "";
                else
                    lockStatus = lockStatusNode.getData().trim();
                String tariffPlan = null;
                Element tariffPlanTag = (Element) balanceRechargingResponseTag.getElementsByTagName("ser:TariffPlan").item(0);
                Text tariffPlanNode = (Text) tariffPlanTag.getFirstChild();
                if (tariffPlanNode == null)
                    tariffPlan = "";
                else
                    tariffPlan = tariffPlanNode.getData().trim();
                NodeList balancesNode = balanceRechargingResponseTag.getElementsByTagName("ser:Balances");
                Element balancesTag = (Element) balancesNode.item(0);
                NodeList balanceNode = balancesTag.getElementsByTagName("ser:Balance");
                Element balanceTag = (Element) balanceNode.item(0);
                String balanceId = null;
                Element balanceIdTag = (Element) balanceTag.getElementsByTagName("ser:BalanceId").item(0);
                Text balanceIdNode = (Text) balanceIdTag.getFirstChild();
                if (balanceIdNode == null)
                    balanceId = "";
                else
                    balanceId = balanceIdNode.getData().trim();
                String lifeCycle = null;
                Element lifeCycleTag = (Element) balanceTag.getElementsByTagName("ser:LifeCycle").item(0);
                Text lifeCycleNode = (Text) lifeCycleTag.getFirstChild();
                if (lifeCycleNode == null)
                    lifeCycle = "";
                else
                    lifeCycle = lifeCycleNode.getData().trim();
                String option = null;
                Element optionTag = (Element) balanceTag.getElementsByTagName("ser:Option").item(0);
                Text optionNode = (Text) optionTag.getFirstChild();
                if (optionNode == null)
                    option = "";
                else
                    option = optionNode.getData().trim();
                String amount = null;
                Element amountTag = (Element) balanceTag.getElementsByTagName("ser:Amount").item(0);
                Text amountNode = (Text) amountTag.getFirstChild();
                if (amountNode == null)
                    amount = "";
                else
                    amount = amountNode.getData().trim();
                String unitType = null;
                Element unitTypeTag = (Element) balanceTag.getElementsByTagName("ser:UnitType").item(0);
                Text unitTypeNode = (Text) unitTypeTag.getFirstChild();
                if (unitTypeNode == null)
                    unitType = "";
                else
                    unitType = unitTypeNode.getData().trim();
                String validityDate = null;
                Element validityDateTag = (Element) balanceTag.getElementsByTagName("ser:ValidityDate").item(0);
                Text validityDateNode = (Text) validityDateTag.getFirstChild();
                if (validityDateNode == null)
                    validityDate = "";
                else
                    validityDate = validityDateNode.getData().trim();
                String graceDate = null;
                Element graceDateTag = (Element) balanceTag.getElementsByTagName("ser:GraceDate").item(0);
                Text graceDateNode = (Text) graceDateTag.getFirstChild();
                if (graceDateNode == null)
                    graceDate = "";
                else
                    graceDate = graceDateNode.getData().trim();
                String currentState = null;
                Element currentStateTag = (Element) balanceTag.getElementsByTagName("ser:State").item(0);
                Text currentStateNode = (Text) currentStateTag.getFirstChild();
                if (currentStateNode == null)
                    currentState = "";
                else
                    currentState = currentStateNode.getData().trim();
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
     * this method connstruct BalanceAdjustmentRequest in XML format from map
     * 
     * @param map
     *            java.util.HsahMap
     * @return requestStr java.lang.String
     */
    private String generateBalanceAdjustmentRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateBalanceAdjustmentRequest", "Entered map=" + map);
        String requestStr = null;
        try {
            Document document = _parser.parse(new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"UTF-8\"?><envelope  action=\"balanceAdjustment\"><BalanceAdjustmentRequest xmlns=\"http://www.ferma.fr/ppg/logic/service/adjustment\"><ns1:TransactionId xmlns:ns1=\"http://www.ferma.fr/ppg/logic/service\">" + (String) map.get("IN_TXN_ID") + "</ns1:TransactionId></BalanceAdjustmentRequest></envelope>").getBytes()));
            NodeList rootNodes = document.getElementsByTagName("BalanceAdjustmentRequest");
            Element balanceAdjustmentRequestTag = (Element) rootNodes.item(0);
            Element interfaceIdNode = document.createElement("InterfaceId");
            Element accessIdentifierNode = document.createElement("AccessIdentifier");
            Element accessTypeNode = document.createElement("AccessType");
            Element accountIdNode = document.createElement("AccountId");
            Element profileNode = document.createElement("Profil");
            Element accountStatusNode = document.createElement("AccountStatus");
            Element lockStatusNode = document.createElement("LockStatus");
            Element rechInstallmentNode = document.createElement("RechInstallment");
            Element BalancesNode = document.createElement("Balances");
            Element BalanceNode = document.createElement("Balance");
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
            interfaceIdNode.appendChild(document.createTextNode((String) map.get("FERMA_INTERFACE_ID")));
            accessIdentifierNode.appendChild(document.createTextNode(InterfaceUtil.getFilterMSISDN((String) map.get("INTERFACE_ID"), (String) map.get("MSISDN"))));
            accessTypeNode.appendChild(document.createTextNode((String) map.get("AccessType")));
            accountIdNode.appendChild(document.createTextNode((String) map.get("AccountId")));
            profileNode.appendChild(document.createTextNode((String) map.get("Profile")));
            accountStatusNode.appendChild(document.createTextNode((String) map.get("AccountStatus")));
            lockStatusNode.appendChild(document.createTextNode((String) map.get("LockStatus")));
            rechInstallmentNode.appendChild(document.createTextNode((String) map.get("RechInstallment")));
            balanceIdNode.appendChild(document.createTextNode((String) map.get("BalanceId")));
            lifeCycleNode.appendChild(document.createTextNode((String) map.get("LifeCycle")));
            optionNode.appendChild(document.createTextNode((String) map.get("Option")));
            // if (_log.isDebugEnabled())
            // _log.debug("generateBalanceAdjustmentRequest",
            // "BEFORE Amount========" + (String) map.get("transfer_amount"));

            if (map.get("transfer_amount") != null) {
                amountNode.appendChild(document.createTextNode((String) map.get("transfer_amount")));
            }
            unitTypeNode.appendChild(document.createTextNode((String) map.get("UnitType")));
            currentValidityDateNode.appendChild(document.createTextNode((String) map.get("CurrentValidityDate")));
            currentGraceDateNode.appendChild(document.createTextNode((String) map.get("CurrentGraceDate")));
            newValidityDateNode.appendChild(document.createTextNode((String) map.get("NewValidityDate")));
            newGraceDateNode.appendChild(document.createTextNode((String) map.get("NewGraceDate")));
            currentStateNode.appendChild(document.createTextNode((String) map.get("CurrentState")));
            BalanceNode.appendChild(balanceIdNode);
            BalanceNode.appendChild(lifeCycleNode);
            BalanceNode.appendChild(optionNode);
            BalanceNode.appendChild(amountNode);
            BalanceNode.appendChild(unitTypeNode);
            BalanceNode.appendChild(currentValidityDateNode);
            BalanceNode.appendChild(currentGraceDateNode);
            BalanceNode.appendChild(newValidityDateNode);
            BalanceNode.appendChild(newGraceDateNode);
            BalanceNode.appendChild(currentStateNode);
            BalancesNode.appendChild(BalanceNode);
            balanceAdjustmentRequestTag.appendChild(interfaceIdNode);
            balanceAdjustmentRequestTag.appendChild(accessIdentifierNode);
            balanceAdjustmentRequestTag.appendChild(accessTypeNode);
            balanceAdjustmentRequestTag.appendChild(accountIdNode);
            balanceAdjustmentRequestTag.appendChild(profileNode);
            balanceAdjustmentRequestTag.appendChild(accountStatusNode);
            balanceAdjustmentRequestTag.appendChild(lockStatusNode);
            balanceAdjustmentRequestTag.appendChild(rechInstallmentNode);
            balanceAdjustmentRequestTag.appendChild(BalancesNode);
            StringBuffer strBuff = new StringBuffer();
            requestStr = write(document, strBuff, "");
            requestStr = requestStr.replaceAll("xml version='1.0'", "xml version='1.0' encoding=\"UTF-8\"");
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
     * this method parse BalanceAdjustmentResponse from XML string into HashMap
     * 
     * @param responseStr
     *            java.lang.String
     * @return map java.util.HsahMap
     */
    private HashMap parseBalanceAdjustmentResponse(String responseStr) throws Exception {

        if (_log.isDebugEnabled())
            _log.debug("parseBalanceAdjustmentResponse", "Entered responseStr: " + responseStr);
        HashMap map = new HashMap();
        try {
            Document document = _parser.parse(new ByteArrayInputStream(responseStr.getBytes()));
            NodeList rootNodes = document.getElementsByTagName("ser:PrepaidServiceResponse");
            Element balanceAdjustmentResponseTag = (Element) rootNodes.item(0);
            Element statusTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("ser:Status").item(0);
            Text statusNode = (Text) statusTag.getFirstChild();
            String status = null;
            if (statusNode == null)
                status = "";
            else
                status = statusNode.getData().trim();
            map.put("Status", status);
            Element transactionIdTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("ser:TransactionId").item(0);
            Text transactionIdNode = (Text) transactionIdTag.getFirstChild();
            String transactionId = null;
            if (transactionIdNode == null)
                transactionId = "";
            else
                transactionId = transactionIdNode.getData().trim();
            map.put("TransactionId", transactionId);
            if (status.equals("0")) {
                String interfaceId = null;
                Element interfaceIdTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("ser:InterfaceId").item(0);
                Text interfaceIdNode = (Text) interfaceIdTag.getFirstChild();
                if (interfaceIdNode == null)
                    interfaceId = "";
                else
                    interfaceId = interfaceIdNode.getData().trim();
                String accessIdentifier = null;
                Element accessIdentifierTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("ser:AccessIdentifier").item(0);
                Text accessIdentifierNode = (Text) accessIdentifierTag.getFirstChild();
                if (accessIdentifierNode == null)
                    accessIdentifier = "";
                else
                    accessIdentifier = accessIdentifierNode.getData().trim();
                String accessType = null;
                Element accessTypeTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("ser:AccessType").item(0);
                Text accessTypeNode = (Text) accessTypeTag.getFirstChild();
                if (accessTypeNode == null)
                    accessType = "";
                else
                    accessType = accessTypeNode.getData().trim();
                String accountId = null;
                Element accountIdTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("ser:AccountId").item(0);
                Text accountIdNode = (Text) accountIdTag.getFirstChild();
                if (accountIdNode == null)
                    accountId = "";
                else
                    accountId = accountIdNode.getData().trim();
                String profile = null;
                Element profileTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("ser:Profil").item(0);
                Text profileNode = (Text) profileTag.getFirstChild();
                if (profileNode == null)
                    profile = "";
                else
                    profile = profileNode.getData().trim();
                String accountLanguage = null;
                Element AccountLanguageTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("ser:AccountLanguage").item(0);
                Text accountLanguageNode = (Text) AccountLanguageTag.getFirstChild();
                if (accountLanguageNode == null)
                    accountLanguage = "";
                else
                    accountLanguage = accountLanguageNode.getData().trim();
                String accountStatus = null;
                Element accountStatusTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("ser:AccountStatus").item(0);
                Text accountStatusNode = (Text) accountStatusTag.getFirstChild();
                if (accountStatusNode == null)
                    accountStatus = "";
                else
                    accountStatus = accountStatusNode.getData().trim();
                String lockStatus = null;
                Element lockStatusTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("ser:LockStatus").item(0);
                Text lockStatusNode = (Text) lockStatusTag.getFirstChild();
                if (lockStatusNode == null)
                    lockStatus = "";
                else
                    lockStatus = lockStatusNode.getData().trim();
                String tariffPlan = null;
                Element tariffPlanTag = (Element) balanceAdjustmentResponseTag.getElementsByTagName("ser:TariffPlan").item(0);
                Text tariffPlanNode = (Text) tariffPlanTag.getFirstChild();
                if (tariffPlanNode == null)
                    tariffPlan = "";
                else
                    tariffPlan = tariffPlanNode.getData().trim();
                NodeList balancesNode = balanceAdjustmentResponseTag.getElementsByTagName("ser:Balances");
                Element balancesTag = (Element) balancesNode.item(0);
                NodeList balanceNode = balancesTag.getElementsByTagName("ser:Balance");
                Element balanceTag = (Element) balanceNode.item(0);
                String balanceId = null;
                Element balanceIdTag = (Element) balanceTag.getElementsByTagName("ser:BalanceId").item(0);
                Text balanceIdNode = (Text) balanceIdTag.getFirstChild();
                if (balanceIdNode == null)
                    balanceId = "";
                else
                    balanceId = balanceIdNode.getData().trim();
                String lifeCycle = null;
                Element lifeCycleTag = (Element) balanceTag.getElementsByTagName("ser:LifeCycle").item(0);
                Text lifeCycleNode = (Text) lifeCycleTag.getFirstChild();
                if (lifeCycleNode == null)
                    lifeCycle = "";
                else
                    lifeCycle = lifeCycleNode.getData().trim();
                String option = null;
                Element optionTag = (Element) balanceTag.getElementsByTagName("ser:Option").item(0);
                Text optionNode = (Text) optionTag.getFirstChild();
                if (optionNode == null)
                    option = "";
                else
                    option = optionNode.getData().trim();
                String amount = null;
                Element amountTag = (Element) balanceTag.getElementsByTagName("ser:Amount").item(0);
                Text amountNode = (Text) amountTag.getFirstChild();
                if (amountNode == null)
                    amount = "";
                else
                    amount = amountNode.getData().trim();
                String unitType = null;
                Element unitTypeTag = (Element) balanceTag.getElementsByTagName("ser:UnitType").item(0);
                Text unitTypeNode = (Text) unitTypeTag.getFirstChild();
                if (unitTypeNode == null)
                    unitType = "";
                else
                    unitType = unitTypeNode.getData().trim();
                String validityDate = null;
                Element validityDateTag = (Element) balanceTag.getElementsByTagName("ser:ValidityDate").item(0);
                Text validityDateNode = (Text) validityDateTag.getFirstChild();
                if (validityDateNode == null)
                    validityDate = "";
                else
                    validityDate = validityDateNode.getData().trim();
                String graceDate = null;
                Element graceDateTag = (Element) balanceTag.getElementsByTagName("ser:GraceDate").item(0);
                Text graceDateNode = (Text) graceDateTag.getFirstChild();
                if (graceDateNode == null)
                    graceDate = "";
                else
                    graceDate = graceDateNode.getData().trim();
                String currentState = null;
                Element currentStateTag = (Element) balanceTag.getElementsByTagName("ser:State").item(0);
                Text currentStateNode = (Text) currentStateTag.getFirstChild();
                if (currentStateNode == null)
                    currentState = "";
                else
                    currentState = currentStateNode.getData().trim();
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
     * this method connstruct LogoutRequest in XML format
     * 
     * @param map
     *            java.util.HsahMap
     * @return requestStr java.lang.String
     */
    private String generateLogoutRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateLogoutRequest", "Entered map=" + map);
        String requestStr = null;
        try {
            Document document = _parser.parse(new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"UTF-8\"?><envelope  action=\"logout\"><LogoutRequest xmlns=\"http://www.ferma.fr/ppg/logic/service/logout\"><ns1:TransactionId xmlns:ns1=\"http://www.ferma.fr/ppg/logic/service\">" + (String) map.get("IN_TXN_ID") + "</ns1:TransactionId></LogoutRequest></envelope>").getBytes()));
            NodeList rootNodes = document.getElementsByTagName("LogoutRequest");
            Element logoutRequestTag = (Element) rootNodes.item(0);
            Element interfaceIdNode = document.createElement("InterfaceId");
            interfaceIdNode.appendChild(document.createTextNode((String) map.get("FERMA_INTERFACE_ID")));
            logoutRequestTag.appendChild(interfaceIdNode);
            StringBuffer strBuff = new StringBuffer();
            requestStr = write(document, strBuff, "");
            requestStr = requestStr.replaceAll("xml version='1.0'", "xml version='1.0' encoding=\"UTF-8\"");
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
     * this method parse LogoutResponse from XML string to HashMap
     * 
     * @param responseStr
     *            java.lang.String
     * @return map java.util.HsahMap
     */
    private HashMap parseLogoutResponse(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseLogoutResponse", "Entered responseStr: " + responseStr);
        HashMap map = new HashMap();
        try {
            Document document = _parser.parse(new ByteArrayInputStream(responseStr.getBytes()));
            NodeList rootNodes = document.getElementsByTagName("log:LogoutResponse");
            Element logoutResponseTag = (Element) rootNodes.item(0);
            String status = null;
            Element statusTag = (Element) logoutResponseTag.getElementsByTagName("ser:Status").item(0);
            Text statusNode = (Text) statusTag.getFirstChild();
            if (statusNode == null)
                status = "";
            else
                status = statusNode.getData().trim();
            String transactionId = null;
            Element transactionIdTag = (Element) logoutResponseTag.getElementsByTagName("ser:TransactionId").item(0);
            Text transactionIdNode = (Text) transactionIdTag.getFirstChild();
            if (transactionIdNode == null)
                transactionId = "";
            else
                transactionId = transactionIdNode.getData().trim();
            map.put("Status", status);
            map.put("TransactionId", transactionId);
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
}