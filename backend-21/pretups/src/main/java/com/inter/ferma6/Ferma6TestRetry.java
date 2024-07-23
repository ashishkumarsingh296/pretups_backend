/*
 * Created on Jun 11, 2007
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.ferma6;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * @author dhiraj.tiwari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class Ferma6TestRetry {
    // static String _fermaID=null;
    String _requestStr = null;
    String _responseStr = null;
    Properties props = null;
    String _responseStatus = null;
    String _interfaceId = null;
    String _transactionId = null;
    int _retryCount = 0;
    int _maxCount = 0;
    String _inTXNid = null;
    boolean _isRetryRequest = false;

    public static void main(String[] args) {
        Ferma6TestRetry testObj = new Ferma6TestRetry();
        testObj.retryTestMethod(args[0]);
    }

    private void retryTestMethod(String option) {
        System.out.println("option" + option);
        HttpURLConnection con = null;
        try {
            _inTXNid = getINTxnID();
            props = new Properties();
            props.load(new FileInputStream("/home/pretups512_dev/tomcat5/webapps/pretups/pretups_scripts/fermaretry.properties"));
            if ("0".equals(option)) {
                // requestStr = props.getProperty("LOGIN");
                _requestStr = generateLoginRequest();

            } else if ("1".equals(option)) {
                _requestStr = generateGetAccountInfoRequest();
            } else if ("2".equals(option)) {
                // requestStr = props.getProperty("BALANCE_RECHARGE");
                _requestStr = generateRechargeBalanceRequest();
            } else if ("3".equals(option)) {
                // requestStr = props.getProperty("BALANCE_RECHARGE");
                _requestStr = generateBalanceAdjustmentRequest();
            } else if ("4".equals(option)) {
                // requestStr = props.getProperty("BALANCE_RECHARGE");
                _requestStr = generateLogoutRequest();
            } else if ("5".equals(option)) {
                _requestStr = generateGetAccountInfoRequest2();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Request String :" + _requestStr);
        try {

            String fermaID = null;
            if ("2".equals(option))
                _maxCount = 1;
            else
                _maxCount = 0;
            while (_retryCount++ <= _maxCount) {
                System.out.println("Inside While _retryCount :" + _retryCount);
                String urlString = props.getProperty("URL1");
                URL url = new URL(urlString.toString());
                URLConnection uc = url.openConnection();
                con = (HttpURLConnection) uc;
                con.setUseCaches(false);
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestMethod("POST");
                PrintWriter out = new PrintWriter(con.getOutputStream());
                out.println(_requestStr);
                out.flush();
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                    String str = null;
                    StringBuffer message = new StringBuffer();
                    while ((str = br.readLine()) != null) {
                        message.append(str);
                    }
                    _responseStr = message.toString();
                    System.out.println("Response String :" + _responseStr);
                } catch (Exception e) {
                    if (_retryCount > _maxCount) {
                        System.out.println("Error While reading response from IN and max retry count also reached");
                        System.out.println("e.getMessage() :: " + e.getMessage());
                        throw new Exception("Error While reading response from IN and max retry count also reached");
                    }
                    System.out.println("Error While reading response from IN so retrying. _retryCount= " + _retryCount);
                    _isRetryRequest = true;
                    continue;
                } finally {

                }

                int index = 0;
                if ("0".equals(option))// Login
                {
                    index = _responseStr.indexOf("<ser:Status>");
                    if (index != -1) {
                        _responseStatus = _responseStr.substring(index + "<ser:Status>".length(), _responseStr.indexOf("</ser:Status>", index));

                    }
                    index = _responseStr.indexOf("<ser:InterfaceId>");
                    if (index != -1) {
                        fermaID = _responseStr.substring(index + "<ser:InterfaceId>".length(), _responseStr.indexOf("</ser:InterfaceId>", index));
                    }
                    // fermaID="5";
                    File file = new File("/home/pretups512_dev/tomcat5/webapps/pretups/pretups_scripts/fermaretry.ser");
                    FileOutputStream fos = new FileOutputStream(file);
                    ObjectOutput out1 = new ObjectOutputStream(fos);
                    out1.writeObject(fermaID);
                    out1.close();
                    // new FermaIdVO().SetFermaID(fermaID);
                    System.out.println("\nLOGIN ::::  fermaID :" + fermaID + ", Status :" + _responseStatus);
                    if (fermaID == null)
                        throw new Exception("Unable to Login to Ferma IN");
                } else if ("2".equals(option))// Balance Re-charge
                {
                    index = _responseStr.indexOf("<ser:Status>");
                    _responseStatus = _responseStr.substring(index + "<ser:Status>".length(), _responseStr.indexOf("</ser:Status>", index));
                    System.out.println("\nBALANCE_RECHARGE :::: Status :" + _responseStatus);
                    if (con.getResponseCode() == 200) {
                        if (_responseStatus.equals("30")) {
                            if (_retryCount > _maxCount) {
                                System.out.println("Response Code is 30 and max retry count also reached. _retryCount is : " + _retryCount);
                                throw new Exception("Response Code is 30 and max retry count also reached.");
                            }
                            System.out.println("Response Code is 30 so SENDING  RETRY REQUEST. _retryCount is : " + _retryCount);
                            _isRetryRequest = true;
                            continue;
                        }
                        if (_responseStatus.equals("3")) {
                            if (_retryCount > _maxCount) {
                                System.out.println("Response Code is 3 and max retry count also reached. _retryCount is : " + _retryCount);
                                throw new Exception("Response Code is 3 and max retry count also reached.");
                            }
                            System.out.println("Response Code is 3 so SENDING  RETRY REQUEST. _retryCount is : " + _retryCount);
                            _isRetryRequest = true;
                            continue;
                        } else if (_responseStatus.equals("20")) {
                            if (!_isRetryRequest) {
                                System.out.println("Response Code is 20 at first attempt so no retry . _retryCount is : " + _retryCount);
                                throw new Exception("Response Code is 20 at first attempt so no retry.");
                            }
                            System.out.println("Successful Transaction after retry. Response Code is 20.");
                        } else if (_responseStatus.equals("0")) {
                            if (!_isRetryRequest)
                                System.out.println("Successful Transaction . Response Code is 0. _isRetryRequest" + _isRetryRequest);
                            else
                                System.out.println("Successful Transaction after retry. Response Code is 0. _isRetryRequest" + _isRetryRequest);
                        } else if (_responseStr == null || _responseStr.length() == 0) {
                            if (_retryCount > _maxCount) {
                                System.out.println("Response String is null and max retry count also reached. _retryCount is : " + _retryCount);
                                throw new Exception("Response String is null and max retry count also reached.");
                            }
                            System.out.println("RESPONSE STRING IS NULL SO SENDING  RETRY REQUEST.  _retryCount is : " + _retryCount);
                            _isRetryRequest = true;
                            continue;
                        }
                    } else {
                    }
                } else if ("1".equals(option))// Account Info,
                {
                    String amount = null;
                    String unitType = null;
                    String validityDate = null;
                    String graceDate = null;

                    index = _responseStr.indexOf("<ser:Status>");
                    _responseStatus = _responseStr.substring(index + "<ser:Status>".length(), _responseStr.indexOf("</ser:Status>", index));
                    index = _responseStr.indexOf("<ser:TransactionId>");
                    String transactionId = _responseStr.substring(index + "<ser:TransactionId>".length(), _responseStr.indexOf("</ser:TransactionId>", index));
                    index = _responseStr.indexOf("<ser:Amount>");
                    if (index != -1) {
                        amount = _responseStr.substring(index + "<ser:Amount>".length(), _responseStr.indexOf("</ser:Amount>", index));

                    }
                    index = _responseStr.indexOf("<ser:UnitType>");
                    if (index != -1) {
                        unitType = _responseStr.substring(index + "<ser:UnitType>".length(), _responseStr.indexOf("</ser:UnitType>", index));

                    }
                    index = _responseStr.indexOf("<ser:ValidityDate>");
                    if (index != -1) {
                        validityDate = _responseStr.substring(index + "<ser:ValidityDate>".length(), _responseStr.indexOf("</ser:ValidityDate>", index));

                    }
                    index = _responseStr.indexOf("<ser:GraceDate>");
                    if (index != -1) {
                        graceDate = _responseStr.substring(index + "<ser:GraceDate>".length(), _responseStr.indexOf("</ser:GraceDate>", index));

                    }
                    System.out.println("_responseStatus =" + _responseStatus + " transactionId =" + transactionId + " amount=" + amount + " validityDate=" + validityDate + " graceDate=" + graceDate);
                } else if ("4".equals(option))// Logout
                {
                    index = _responseStr.indexOf("<ser:Status>");
                    String transactionId = null;
                    if (index != -1) {
                        _responseStatus = _responseStr.substring(index + "<ser:Status>".length(), _responseStr.indexOf("</ser:Status>", index));

                    }
                    index = _responseStr.indexOf("<ser:InterfaceId>");
                    if (index != -1) {
                        fermaID = _responseStr.substring(index + "<ser:InterfaceId>".length(), _responseStr.indexOf("</ser:InterfaceId>", index));
                    }
                    index = _responseStr.indexOf("<ser:TransactionId>");
                    if (index != -1) {
                        transactionId = _responseStr.substring(index + "<ser:TransactionId>".length(), _responseStr.indexOf("</ser:TransactionId>", index));
                    }
                    System.out.println("_responseStatus =" + _responseStatus + " transactionId =" + transactionId + " _responseStatus=" + _responseStatus);
                } else if ("5".equals(option))// Account Info,
                {
                    String amount = null;
                    String unitType = null;
                    String validityDate = null;
                    String graceDate = null;
                    String icc = null;
                    String firstCallDate = null;

                    index = _responseStr.indexOf("<ser:Status>");
                    _responseStatus = _responseStr.substring(index + "<ser:Status>".length(), _responseStr.indexOf("</ser:Status>", index));
                    index = _responseStr.indexOf("<ser:TransactionId>");
                    String transactionId = _responseStr.substring(index + "<ser:TransactionId>".length(), _responseStr.indexOf("</ser:TransactionId>", index));
                    index = _responseStr.indexOf("<ser:Amount>");
                    if (index != -1) {
                        amount = _responseStr.substring(index + "<ser:Amount>".length(), _responseStr.indexOf("</ser:Amount>", index));

                    }
                    index = _responseStr.indexOf("<ser:UnitType>");
                    if (index != -1) {
                        unitType = _responseStr.substring(index + "<ser:UnitType>".length(), _responseStr.indexOf("</ser:UnitType>", index));

                    }
                    index = _responseStr.indexOf("<ser:ValidityDate>");
                    if (index != -1) {
                        validityDate = _responseStr.substring(index + "<ser:ValidityDate>".length(), _responseStr.indexOf("</ser:ValidityDate>", index));

                    }
                    index = _responseStr.indexOf("<ser:GraceDate>");
                    if (index != -1) {
                        graceDate = _responseStr.substring(index + "<ser:GraceDate>".length(), _responseStr.indexOf("</ser:GraceDate>", index));

                    }
                    index = _responseStr.indexOf("<ser:Icc>");
                    if (index != -1) {
                        icc = _responseStr.substring(index + "<ser:ICC>".length(), _responseStr.indexOf("</ser:Icc>", index));

                    }
                    index = _responseStr.indexOf("<ser:FirstCallDate>");
                    if (index != -1) {
                        firstCallDate = _responseStr.substring(index + "<ser:FirstCallDate>".length(), _responseStr.indexOf("</ser:FirstCallDate>", index));

                    }
                    System.out.println("_responseStatus =" + _responseStatus + " transactionId =" + transactionId + " amount=" + amount + " validityDate=" + validityDate + " graceDate=" + graceDate + " Icc= " + icc + " FirstCallDate= " + firstCallDate);
                } else // 3 Balance Adjustment
                {
                    index = _responseStr.indexOf("<ser:Status>");
                    String transactionId = null;
                    if (index != -1) {
                        _responseStatus = _responseStr.substring(index + "<ser:Status>".length(), _responseStr.indexOf("</ser:Status>", index));

                    }
                    index = _responseStr.indexOf("<ser:InterfaceId>");
                    if (index != -1) {
                        fermaID = _responseStr.substring(index + "<ser:InterfaceId>".length(), _responseStr.indexOf("</ser:InterfaceId>", index));
                    }
                    index = _responseStr.indexOf("<ser:TransactionId>");
                    if (index != -1) {
                        transactionId = _responseStr.substring(index + "<ser:TransactionId>".length(), _responseStr.indexOf("</ser:TransactionId>", index));
                    }
                    System.out.println("_responseStatus =" + _responseStatus + " transactionId =" + transactionId + " _responseStatus=" + _responseStatus);
                }
                break;
            }

        } catch (Exception e) {
            System.out.println("e.getMessage : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    private String generateLoginRequest() throws Exception {
        System.out.println("generateLoginRequest Entered ");
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><envelope  action=\"login\"><LoginRequest xmlns=\"http://www.ferma.fr/ppg/logic/service/login\"><ser:TransactionId xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">" + _inTXNid + "</ser:TransactionId>");
            sbf.append("<InterfaceId></InterfaceId>");
            sbf.append("<UserName>" + props.getProperty("UserName") + "</UserName>");
            sbf.append("<Password>" + props.getProperty("Password") + "</Password>");
            sbf.append("<ProtocolVersion>" + props.getProperty("ProtocolVersion") + "</ProtocolVersion>");
            sbf.append("</LoginRequest></envelope>");
            requestStr = sbf.toString();
            return requestStr;
        } catch (Exception e) {
            System.out.println("generateLoginRequest Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            // System.out.println("generateLoginRequest Entered requestStr: " +
            // requestStr);
        }
    }

    private String generateGetAccountInfoRequest() {
        System.out.println("generateGetAccountInfoRequest Entered");
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><envelope  action=\"getAccountInfo\"><AccountInfoGettingRequest xmlns=\"http://www.ferma.fr/ppg/logic/service/accountinfo\"><ser:TransactionId xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">" + _inTXNid + "</ser:TransactionId>");
            sbf.append("<ser:InterfaceId>" + getFermaID() + "</ser:InterfaceId>");
            sbf.append("<AccessIdentifier>" + (String) props.get("MSISDN") + "</AccessIdentifier>");
            sbf.append("<AccessType>" + (String) props.get("AccessType") + "</AccessType>");
            sbf.append("<Balance><BalanceId>" + (String) props.get("BalanceId") + "</BalanceId></Balance>");
            sbf.append("</AccountInfoGettingRequest></envelope>");
            requestStr = sbf.toString();
        } catch (Exception e) {
            System.out.println("generateGetAccountInfoRequest Exception e: " + e);
            e.printStackTrace();
        }
        return requestStr;
    }

    private String generateRechargeBalanceRequest() throws Exception {
        System.out.println("generateRechargeBalanceRequest Entered");
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><envelope  action=\"balanceRecharge\"><BalanceRechargingRequest xmlns=\"http://www.ferma.fr/ppg/logic/service/recharging\"><ser:TransactionId xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">");
            sbf.append(_inTXNid + "</ser:TransactionId>");
            sbf.append("<ser:InterfaceId>" + getFermaID() + "</ser:InterfaceId>");
            sbf.append("<AccessIdentifier>" + (String) props.get("MSISDN") + "</AccessIdentifier>");
            sbf.append("<AccessType>" + (String) props.get("AccessType") + "</AccessType>");
            sbf.append("<AccountId>" + (String) props.get("AccountId") + "</AccountId>");
            sbf.append("<Balances><Balance><BalanceId>" + (String) props.get("BalanceId") + "</BalanceId>");
            sbf.append("<RechargeValue>" + (String) props.get("RechargeValue") + "</RechargeValue>");
            sbf.append("<UnitType>" + (String) props.get("UnitType") + "</UnitType></Balance></Balances>");
            sbf.append("</BalanceRechargingRequest></envelope>");
            requestStr = sbf.toString();
            return requestStr;
        } catch (Exception e) {
            System.out.println("generateRechargeBalanceRequest Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            // System.out.println("generateRechargeBalanceRequest Exited requestStr: "
            // + requestStr);
        }
    }

    private String generateBalanceAdjustmentRequest() throws Exception {
        System.out.println("generateBalanceAdjustmentRequest Entered ");
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><envelope  action=\"balanceAdjustment\"><BalanceAdjustmentRequest xmlns=\"http://www.ferma.fr/ppg/logic/service/adjustment\"><ser:TransactionId xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">" + _inTXNid + "</ser:TransactionId>");
            sbf.append("<ser:InterfaceId>" + getFermaID() + "</ser:InterfaceId>");
            sbf.append("<AccessIdentifier>" + (String) props.get("MSISDN") + "</AccessIdentifier>");
            sbf.append("<AccessType>" + (String) props.get("AccessType") + "</AccessType>");
            sbf.append("<AccountId>" + (String) props.get("AccountId") + "</AccountId>");
            sbf.append("<Profil>" + (String) props.get("Profile") + "</Profil>");
            sbf.append("<AccountStatus>" + (String) props.get("AccountStatus") + "</AccountStatus>");
            sbf.append("<LockStatus>" + (String) props.get("LockStatus") + "</LockStatus>");
            sbf.append("<RechInstallment>" + (String) props.get("RechInstallment") + "</RechInstallment>");
            sbf.append("<Balances><Balance>");
            sbf.append("<BalanceId>" + (String) props.get("BalanceId") + "</BalanceId>");
            sbf.append("<LifeCycle>" + (String) props.get("LifeCycle") + "</LifeCycle>");
            sbf.append("<Option>" + (String) props.get("Option") + "</Option>");
            sbf.append("<Amount>" + (String) props.get("transfer_amount") + "</Amount>");
            sbf.append("<UnitType>" + (String) props.get("UnitType") + "</UnitType>");
            sbf.append("<CurrentValidityDate>" + (String) props.get("CurrentValidityDate") + "</CurrentValidityDate>");
            sbf.append("<CurrentGraceDate>" + (String) props.get("CurrentGraceDate") + "</CurrentGraceDate>");
            sbf.append("<NewValidityDate>" + (String) props.get("NewValidityDate") + "</NewValidityDate>");
            sbf.append("<NewGraceDate>" + (String) props.get("NewGraceDate") + "</NewGraceDate>");
            sbf.append("<CurrentState>" + (String) props.get("CurrentState") + "</CurrentState>");
            sbf.append("</Balance></Balances>");
            sbf.append("</BalanceAdjustmentRequest></envelope>");
            requestStr = sbf.toString();
            return requestStr;
        } catch (Exception e) {
            System.out.println("generateBalanceAdjustmentRequest Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            System.out.println("generateBalanceAdjustmentRequest Exited requestStr: " + requestStr);
        }
    }

    private String generateLogoutRequest() {
        System.out.println("generateLogoutRequest Entered ");
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><envelope  action=\"logout\"><LogoutRequest xmlns=\"http://www.ferma.fr/ppg/logic/service/logout\"><ser:TransactionId xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">" + _inTXNid + "</ser:TransactionId>");
            sbf.append("<InterfaceId>" + getFermaID() + "</InterfaceId>");
            sbf.append("</LogoutRequest></envelope>");
            requestStr = sbf.toString();

        } catch (Exception e) {
            System.out.println("generateLogoutRequest Exception e: " + e);
            e.printStackTrace();

        }
        return requestStr;
    }

    private String getINTxnID() {
        String txnId = null;
        java.util.Date mydate = new java.util.Date();
        // Change on 17/05/06 for making the TXN ID as unique in Interface
        // Transaction Table (CR00021)
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSSSS");
        String dateString = sdf.format(mydate);
        System.out.println("getINTxnID Exited  id: " + dateString);
        return dateString;
    }

    private String getFermaID() {
        Object ob = null;
        File file = new File("/home/pretups512_dev/tomcat5/webapps/pretups/pretups_scripts/fermaretry.ser");
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInput in = new ObjectInputStream(fis);
            ob = in.readObject();
            in.close();

        } catch (Exception e) {

        }
        return (String) ob;
    }

    // Added for account info2
    private String generateGetAccountInfoRequest2() {
        System.out.println("generateGetAccountInfoRequest2 Entered");
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><envelope  action=\"getAccountInfo2\"><GettingAccountInfoRequest2 xmlns=\"http://www.ferma.fr/ppg/logic/service/accountinfo2\"><ser:TransactionId xmlns:ser=\"http://www.ferma.fr/ppg/logic/service\">" + _inTXNid + "</ser:TransactionId>");
            sbf.append("<ser:InterfaceId>" + getFermaID() + "</ser:InterfaceId>");
            sbf.append("<AccessIdentifier>" + (String) props.get("MSISDN") + "</AccessIdentifier>");
            sbf.append("<AccessType>" + (String) props.get("AccessType") + "</AccessType>");
            sbf.append("<Balance><BalanceId>" + (String) props.get("BalanceId") + "</BalanceId></Balance>");
            sbf.append("</GettingAccountInfoRequest2></envelope>");
            requestStr = sbf.toString();

        } catch (Exception e) {
            System.out.println("generateGetAccountInfoRequest2 Exception e: " + e);
            e.printStackTrace();
        }
        return requestStr;
    }
}
