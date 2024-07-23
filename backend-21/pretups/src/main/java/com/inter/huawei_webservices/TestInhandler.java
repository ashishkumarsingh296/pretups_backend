package com.inter.huawei_webservices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.rmi.Remote;
import java.util.HashMap;
import java.util.Properties;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.message.token.UsernameToken;

// import com.btsl.pretups.inter.comversetg.scheduler.NodeVO;
import com.btsl.pretups.inter.huawei_webservices.omt_huawei.*;

import java.io.*;

/**
 * @author shashank shukla
 * 
 */
public class TestInhandler {
    // HuaweiOMTConnector connector=null;
    // NodeVO nodevo=null;
    static AdjustAccountResultMsg result = null;
    static CBSInterfaceAccountMgrBindingStub _service = null;

    public static void main(String aa[]) throws IOException {
        int option = 2;
        Properties _properties = new Properties();
        HashMap _requestMap = new HashMap();
        String _msisdn = "";
        String _ReconID = "";
        String _testComment = "";
        Double _transferAmount = 0.0;
        int _validity = 0;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Test Program");
            System.out.println("1:Payment");
            System.out.println("2:Account Adjustment");
            option = Integer.parseInt(br.readLine());
            if (option > 2 || option < 1)
                throw new Exception("Error in input");
            org.apache.log4j.PropertyConfigurator.configure("/pretupshome/tomcat5_web/webapps/pretups/WEB-INF/classes/configfiles/LogConfig.props");

        } catch (Exception e) {
            System.out.println("Error while giving input");

        }
        /*
         * nodevo=new NodeVO();
         * nodevo.setNodeNumber(1);
         * nodevo.setUrl("http://172.16.1.121:5555/pretups/");
         * nodevo.setReadTimeOut(4000);
         * nodevo.setWssdFileLoc("C://");
         * nodevo.setSoapUri("http://");
         * nodevo.setPwbackCall(
         * "com.btsl.pretups.inter.huawei_webservices.HuaweiOMTConnector");
         * nodevo.setWarnTime(2000);
         * nodevo.setUserName("pretups");
         * nodevo.setPassword("pretups");
         * nodevo.setExpiryDuration(4000);
         * nodevo.setBarredCount(2);
         * connector=new HuaweiOMTConnector(nodevo,"INTID0001");
         * CBSInterfaceAccountMgr clientstub=connector.getService();
         * 
         * //------------request--------------
         * AdjustAccountRequestMsg requestobj=new AdjustAccountRequestMsg();
         * // requestobj.set
         */
        // -----------------------------------

        // result=clientstub.adjustAccount(adjustAccountRequestMsg)

        try {
            /*
             * File file = new File(
             * "C:\\workspace\\pretups55\\src\\com\\btsl\\pretups\\inter\\huawei_webservices\\Config.txt"
             * );
             * _properties.load(new FileInputStream(file));
             * String reqStr=_properties.getProperty("REQUEST");
             * // _msisdn=_properties.getProperty("MSISDN");
             * _ReconID=_properties.getProperty("RECONID");
             * _testComment=_properties.getProperty("TESTCOMMENT");
             * // _transferAmount=Double.parseDouble(_properties.getProperty(
             * "TRANSFER_AMOUNT"));
             * //
             * _validity=Integer.parseInt(_properties.getProperty("VALIDITY_DAYS"
             * ));
             * _requestMap.put("WSDD_LOCATION",
             * _properties.getProperty("WSDD_LOCATION"));
             * _requestMap.put("END_URL", _properties.getProperty("END_URL"));
             * _requestMap.put("READ_TIME_OUT",
             * _properties.getProperty("READ_TIME_OUT"));
             * _requestMap.put("USER_NAME",
             * _properties.getProperty("USER_NAME"));
             * _requestMap.put("SOAP_ACTION_URI",
             * _properties.getProperty("SOAP_ACTION_URI"));
             * 
             * //-------------------------------connector-----------------------
             * EngineConfiguration config = new
             * FileProvider((String)_requestMap.get("WSDD_LOCATION"));
             * System.out.println(" ========request map"+_requestMap);
             * CBSInterfaceAccountMgrServiceLocator locator = new
             * CBSInterfaceAccountMgrServiceLocator();
             * Remote remote = (Remote)
             * locator.getPort(CBSInterfaceAccountMgrBindingStub.class);
             * Stub _axisPort=null;
             * //CBSInterfaceAccountMgrBindingStub _service=null;
             * _axisPort = (CBSInterfaceAccountMgrBindingStub)remote;
             * _axisPort._setProperty(_axisPort.ENDPOINT_ADDRESS_PROPERTY,(String
             * )_requestMap.get("END_URL"));
             * _axisPort.setTimeout(Integer.parseInt((String)_requestMap.get(
             * "READ_TIME_OUT")));
             * _axisPort._setProperty(WSHandlerConstants.ACTION,WSHandlerConstants
             * .USERNAME_TOKEN);
             * _axisPort._setProperty(UsernameToken.PASSWORD_TYPE,WSConstants.
             * PASSWORD_TEXT);
             * _axisPort._setProperty(WSHandlerConstants.USER,
             * (String)_requestMap.get("USER_NAME"));
             * _axisPort._setProperty(WSHandlerConstants.PW_CALLBACK_CLASS,
             * "com.btsl.pretups.inter.huawei_webservices.PWCallback");
             * _axisPort._createCall().setSOAPActionURI((String)_requestMap.get(
             * "SOAP_ACTION_URI"));
             * _service = (CBSInterfaceAccountMgrBindingStub)_axisPort;
             * // _service._initOperationDesc1();
             * //-----------------------------------
             */

            EngineConfiguration config = new FileProvider("/pretupshome/tomcat5_web/webapps/pretups/WEB-INF/src/com/btsl/pretups/inter/huawei_webservices/client_deploy.wsdd");

            CBSInterfaceAccountMgrServiceLocator locator = new CBSInterfaceAccountMgrServiceLocator(config);
            Remote remote = (Remote) locator.getPort(CBSInterfaceAccountMgrBindingStub.class);
            Stub _axisPort = (CBSInterfaceAccountMgrBindingStub) remote;
            _axisPort._setProperty(_axisPort.ENDPOINT_ADDRESS_PROPERTY, "http://172.16.6.11:7782//services/CBSInterfaceAccountMgrService");
            _axisPort._setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
            _axisPort._setProperty(WSHandlerConstants.USER, "comviva");
            _axisPort._setProperty(UsernameToken.PASSWORD_TYPE, WSConstants.PASSWORD_TEXT);
            _axisPort._setProperty(WSConstants.PASSWORD_TEXT, "Comviva123");
            // _axisPort._setProperty(WSHandlerConstants.NO_SECURITY,"NoSecurity");
            _axisPort._createCall().setSOAPActionURI("http://www.huawei.com/bme/cbsinterface/cbs/accountmgr");
            _service = (CBSInterfaceAccountMgrBindingStub) _axisPort;
            PaymentResultMsg response = new PaymentResultMsg();
            RequestHeader requestHeader = new RequestHeader();
            PaymentRequest request = new PaymentRequest();

            // -----request object---------------
            // -- request header ----
            requestHeader.setCommandId("Payment");
            requestHeader.setVersion("1.2");
            requestHeader.setTransactionId("trans001");
            requestHeader.setSequenceId("2232");
            // change the constructor to public from protected
            requestHeader.setRequestType(new RequestHeaderRequestType("Event"));
            requestHeader.setSerialNo("R110608.1270.11006");

            // ------------- request
            request.setSubscriberNo("7984420");
            request.setPaymentAmt(1000);
            request.setPaymentMode("1400");
            // request.setAccountType("2000");
            // request.setAdditionalInfo("abc");
            // request.setLocation("2");
            // ---------------------
            PaymentRequestMsg paymentRequestMsg = new PaymentRequestMsg(requestHeader, request);

            response = _service.payment(paymentRequestMsg);

            System.out.println("=================Response OBJ============response" + response);

            // CBSInterfaceAccountMgrBindingStub stub=new
            // CBSInterfaceAccountMgrBindingStub("http://172.16.1.121:5555/pretups/",_service);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            _properties = null;
            _service = null;
            _requestMap = null;
            _msisdn = null;
            _ReconID = null;
            _testComment = null;
            _transferAmount = null;
            if (br != null)
                br.close();
        }

    }

    public static void paymmentRequest() throws Exception {

        PaymentResultMsg response = new PaymentResultMsg();

        // -----request object---------------
        // -- request header ----
        RequestHeader requestHeader = new RequestHeader();
        requestHeader.setCommandId("Payment");
        requestHeader.setVersion("1");
        requestHeader.setTransactionId("1");
        requestHeader.setSequenceId("1");
        // change the constructor to public from protected
        requestHeader.setRequestType(new RequestHeaderRequestType("Event"));
        requestHeader.setSerialNo("88880296911");
        // ----------------------
        // ------------- request
        PaymentRequest request = new PaymentRequest();
        request.setLogID("88880296911");
        request.setSubscriberNo("88880296911");
        request.setPaymentAmt(1000);
        request.setPaymentMode("1001");
        // ---------------------
        PaymentRequestMsg paymentRequestMsg = new PaymentRequestMsg(requestHeader, request);// check
        System.out.println("===============Request OBJ=========paymentRequestMsg" + paymentRequestMsg.toString());
        // ----------------------------------
        response = _service.payment(paymentRequestMsg);

        System.out.println("=================Response OBJ============response" + response);
        // _service.payment(paymentRequestMsg)

    }

    public static void accountAdjustmentRequest() throws Exception {
        AdjustAccountResultMsg adjustment = new AdjustAccountResultMsg();
        // -----request object---------------
        // -- request header ----
        RequestHeader requestHeader = new RequestHeader();
        requestHeader.setCommandId("AdjustAccount");
        requestHeader.setVersion("1");
        requestHeader.setTransactionId("1");
        requestHeader.setSequenceId("1");
        // change the constructor to public from protected
        requestHeader.setRequestType(new RequestHeaderRequestType("Event"));
        requestHeader.setSerialNo("48001511111");
        // ----------------------
        // ------------- request
        AdjustAccountRequest request = new AdjustAccountRequest();
        // request.setLogID("88880296911");
        request.setSubscriberNo("88880296911");
        request.setOperateType(2);
        request.setAdditionalInfo("abcabc");

        // ---------------------
        AdjustAccountRequestMsg adjustAccountRequestMsg = new AdjustAccountRequestMsg(requestHeader, request);// check
        System.out.println("===============Request OBJ=========paymentRequestMsg" + adjustAccountRequestMsg.toString());
        // ----------------------------------
        adjustment = _service.adjustAccount(adjustAccountRequestMsg);
        System.out.println("=================Response OBJ============response" + adjustment);
        // _service.payment(paymentRequestMsg)

    }

}
