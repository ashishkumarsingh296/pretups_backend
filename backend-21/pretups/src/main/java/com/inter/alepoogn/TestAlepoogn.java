package com.inter.alepoogn;

import java.io.File;
import java.io.FileInputStream;
import java.rmi.Remote;
import java.text.SimpleDateFormat;
import java.util.Properties;
import javax.xml.rpc.Stub;
import javax.xml.rpc.soap.SOAPFaultException;

import com.inter.alepoogn.alepoognstub.AlepoSoapBindingStub;
import com.inter.alepoogn.alepoognstub.AlepoSoapServiceLocator;
import com.inter.alepoogn.alepoognstub.DataItem;
import com.inter.alepoogn.alepoognstub.Radius;
import com.inter.alepoogn.alepoognstub.SoapResponseData;

public class TestAlepoogn {
    private Radius _stub = null;
    private String _action = null;
    private String _pageName = null;
    private String _propertiesFilePath = null;
    private String _msisdn = null;
    private String _amount = null;
    private String _validity = null;
    private String _grace = null;
    private String _otherInfo = null;

    private Stub _stubSuper = null;
    static AlepoSoapBindingStub _service = null;

    public TestAlepoogn() {
        super();
    }

    public static void main(String[] args) {
        TestAlepoogn testAlepo = new TestAlepoogn();
        // testAlepo._propertiesFilePath = args[0].trim();
        testAlepo._action = "2";
        // testAlepo._action = args[1];
        try {
            org.apache.log4j.PropertyConfigurator.configure("/pretupshome/tomcat5_web/webapps/pretups/WEB-INF/classes/configfiles/LogConfig.props");
            // testAlepo.loadInputs();
            // EngineConfiguration config = new
            // FileProvider("/pretupshome/tomcat5_web/webapps/pretups/WEB-INF/src/com/btsl/pretups/inter/huawei_webservices/client_deploy.wsdd");
            AlepoSoapServiceLocator test = new AlepoSoapServiceLocator();
            // Remote remote = (Remote)test.getPort(AlepoSoapBindingStub.class);
            // Stub _axisPort = (AlepoSoapBindingStub)remote;
            // _axisPort._setProperty(_axisPort.ENDPOINT_ADDRESS_PROPERTY,"http://10.173.1.193:8005");
            // _axisPort._setProperty(WSHandlerConstants.ACTION,WSHandlerConstants.USERNAME_TOKEN);
            // _axisPort._setProperty(WSHandlerConstants.USER,"comviva");
            // _axisPort._setProperty(UsernameToken.PASSWORD_TYPE,WSConstants.PASSWORD_TEXT);
            // _axisPort._setProperty(WSConstants.PASSWORD_TEXT,"Comviva123");
            // _axisPort._setProperty(WSHandlerConstants.NO_SECURITY,"NoSecurity");
            // _axisPort._createCall().setSOAPActionURI("");
            // _service = (AlepoSoapBindingStub)_axisPort;
            // DataItem[] request = null;
            // if(testAlepo._action.equals("2"))
            // request = testAlepo.generateGetAccountInfoRequest();
            // else if(testAlepo._action.equals("2"))
            // request = testAlepo.generateRechargeCreditRequest();
            // else if(testAlepo._action.equals("3"))
            // request = testAlepo.generateSubscriberCreditRequest();
            // else if(testAlepo._action.equals("4"))
            // request = testAlepo.generateSubscriberDebitRequest();
            // long startTime=System.currentTimeMillis();
            testAlepo.loadInputs();
            // SoapResponseData response =
            // testAlepo._stub.soapRequest(testAlepo._pageName, request);

            testAlepo._stub = test.getAlepo();

            testAlepo._stubSuper = (Stub) testAlepo._stub;
            testAlepo._stubSuper._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, "http://10.173.1.193:8005");
            testAlepo._stub = (Radius) testAlepo._stubSuper;

            DataItem[] req = null;
            if (testAlepo._action.equals("1"))
                req = testAlepo.generateGetAccountInfoRequest();
            else if (testAlepo._action.equals("2"))
                req = testAlepo.generateRechargeCreditRequest();
            else if (testAlepo._action.equals("3"))
                req = testAlepo.generateSubscriberCreditRequest();
            else if (testAlepo._action.equals("4"))
                req = testAlepo.generateSubscriberDebitRequest();
            else if (testAlepo._action.equals("5"))
                req = testAlepo.generatePostPaymentRequest();

            long startTime = System.currentTimeMillis();

            SoapResponseData res = testAlepo._stub.soapRequest(testAlepo._pageName, req);

            long endTime = System.currentTimeMillis();
            double INtime = (endTime - startTime) / 1000;
            System.out.println("\nTime taken at IN for processing in seconds:" + INtime);

            System.out.println(testAlepo._pageName + " Response:");
            System.out.println("\tResponse Code:" + res.getCode());

            if (res.getCode().equals("SOAP_SUCCESS")) {
                DataItem[] resp = res.getData();
                System.out.println(resp);
                DataItem dataItem = null;
                for (int i = 0, j = resp.length; i < j; i++) {
                    dataItem = resp[i];
                    String code = dataItem.getKey();
                    String value = dataItem.getValue();
                    System.out.println("\t" + code + "=" + value);
                }
            }
        } catch (SOAPFaultException se) {
            System.out.println("SOAPFaultException getFaultString=" + se.getMessage());
            se.printStackTrace();
        } catch (Exception e) {
            System.out.println("testAlepo Exception=" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadInputs() throws Exception {
        try {
            Properties properties = new Properties();
            // File file= new File(_propertiesFilePath);
            // properties.load(new FileInputStream(file));
            File file = new File("/pretupshome/tomcat5_web/webapps/pretups/WEB-INF/classes/configfiles/INFiles/Config.txt");
            properties.load(new FileInputStream(file));
            System.out.println("File path is" + file);
            _msisdn = properties.getProperty("MSISDN");
            _validity = properties.getProperty("VALIDITY");
            _amount = properties.getProperty("AMOUNT");
            _grace = properties.getProperty("GRACE");
            _otherInfo = properties.getProperty("OTHERINFO");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private DataItem[] generateGetAccountInfoRequest() throws Exception {
        DataItem[] req = null;
        _pageName = "AuthorizeUserAccount";
        try {
            DataItem msisdn = new DataItem("UserID", _msisdn);
            DataItem txnID = new DataItem("TransactionID", getRequestID());
            DataItem method = new DataItem("Method", "GET");
            DataItem template = new DataItem("Template", "AuthorizeUserAccount.hts");
            DataItem otherInfo = new DataItem("OtherInfo", _otherInfo);
            req = new DataItem[] { msisdn, txnID, method, template, otherInfo };

            System.out.println(_pageName + " Request: ");
            for (int i = 0, j = req.length; i < j; i++) {
                DataItem dataItem = req[i];
                String code = dataItem.getKey();
                String value = dataItem.getValue();
                System.out.println("\t" + code + "=" + value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return req;
    }

    private DataItem[] generateRechargeCreditRequest() throws Exception {
        DataItem[] req = null;
        _pageName = "RefillAccount";
        try {
            DataItem userID = new DataItem("UserID", _msisdn);
            DataItem update = new DataItem("UPDATE", "UPDATE");
            DataItem validityDays = new DataItem("ValidityDays", _validity);
            DataItem graceDays = new DataItem("GraceDays", _grace);
            DataItem amount = new DataItem("Amount", _amount);
            DataItem txnID = new DataItem("TransactionID", getRequestID());
            DataItem otherInfo = new DataItem("OtherInfo", _otherInfo);

            // req = new
            // DataItem[]{userID,update,validityDays,graceDays,amount,txnID,otherInfo};
            req = new DataItem[] { userID, update, validityDays, amount, txnID, otherInfo };

            System.out.println(_pageName + " Request: ");
            for (int i = 0, j = req.length; i < j; i++) {
                DataItem dataItem = req[i];
                String code = dataItem.getKey();
                String value = dataItem.getValue();
                System.out.println("\t" + code + "=" + value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return req;
    }

    private static String getRequestID() {
        String reqId = "";
        try {
            java.util.Date mydate = new java.util.Date();
            SimpleDateFormat sdfReqId = new SimpleDateFormat("yyyyMMddhhmmssSSS");
            reqId = sdfReqId.format(mydate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reqId;
    }

    private DataItem[] generateSubscriberCreditRequest() throws Exception {
        DataItem[] req = null;
        _pageName = "CreditUserAccount";
        try {
            DataItem userID = new DataItem("UserID", _msisdn);
            DataItem update = new DataItem("UPDATE", "UPDATE");
            DataItem validityDays = new DataItem("ValidityDays", _validity);
            DataItem graceDays = new DataItem("GraceDays", _grace);
            DataItem amount = new DataItem("Amount", _amount);
            DataItem txnID = new DataItem("TransactionID", getRequestID());
            DataItem otherInfo = new DataItem("OtherInfo", _otherInfo);

            req = new DataItem[] { userID, update, validityDays, graceDays, amount, txnID, otherInfo };
            // req = new
            // DataItem[]{userID,update,validityDays,amount,txnID,otherInfo};

            System.out.println(_pageName + " Request: ");
            for (int i = 0, j = req.length; i < j; i++) {
                DataItem dataItem = req[i];
                String code = dataItem.getKey();
                String value = dataItem.getValue();
                System.out.println("\t" + code + "=" + value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return req;
    }

    private DataItem[] generateSubscriberDebitRequest() throws Exception {
        DataItem[] req = null;
        _pageName = "DebitUserAccount";
        try {
            DataItem userID = new DataItem("UserID", _msisdn);
            DataItem update = new DataItem("UPDATE", "UPDATE");
            DataItem validityDays = new DataItem("ValidityDays", _validity);
            DataItem graceDays = new DataItem("GraceDays", _grace);
            DataItem amount = new DataItem("Amount", _amount);
            DataItem txnID = new DataItem("TransactionID", getRequestID());
            DataItem otherInfo = new DataItem("OtherInfo", _otherInfo);

            req = new DataItem[] { userID, update, validityDays, graceDays, amount, txnID, otherInfo };

            System.out.println(_pageName + " Request: ");
            for (int i = 0, j = req.length; i < j; i++) {
                DataItem dataItem = req[i];
                String code = dataItem.getKey();
                String value = dataItem.getValue();
                System.out.println("\t" + code + "=" + value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return req;
    }

    private DataItem[] generatePostPaymentRequest() throws Exception {
        DataItem[] req = null;
        _pageName = "PaymentEdit";
        try {
            DataItem userType = new DataItem("UserType", "1");
            DataItem userID = new DataItem("UserID", _msisdn);
            DataItem payment = new DataItem("PaymentMethod", "5");
            DataItem amount = new DataItem("Amount", _amount);
            DataItem txnID = new DataItem("Direction", "Crdeit");
            // DataItem validityDays = new DataItem("ValidityDays",_validity);
            // DataItem graceDays = new DataItem("GraceDays",_grace);
            // DataItem amount = new DataItem("Amount",_amount);
            // DataItem txnID = new DataItem("TransactionID",getRequestID());
            // DataItem otherInfo = new DataItem("OtherInfo",_otherInfo);

            req = new DataItem[] { userType, userID, payment, amount, txnID };

            System.out.println(_pageName + " Request: ");
            for (int i = 0, j = req.length; i < j; i++) {
                DataItem dataItem = req[i];
                String code = dataItem.getKey();
                String value = dataItem.getValue();
                System.out.println("\t" + code + "=" + value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return req;
    }

}
