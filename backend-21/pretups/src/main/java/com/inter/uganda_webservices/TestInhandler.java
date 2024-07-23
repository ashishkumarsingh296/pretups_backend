package com.inter.uganda_webservices;

import java.io.BufferedReader;
import java.io.IOException;
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
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.DCPServiceLocator;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.DCPServiceSoapBindingStub;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.LogonResponse;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.LogonRequest;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.GetAccountReq;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.GetAccountResponse;

public class TestInhandler {

    // HuaweiOMTConnector connector=null;
    // NodeVO nodevo=null;
    static DCPServiceSoapBindingStub _service = null;

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
            System.out.println("1:Logon");
            System.out.println("2:Account Adjustment");
            option = Integer.parseInt(br.readLine());
            if (option > 2 || option < 1)
                throw new Exception("Error in input");
            org.apache.log4j.PropertyConfigurator.configure("/pretupshome/tomcat5_smsr/webapps/pretups/WEB-INF/classes/configfiles/LogConfig.props");

        } catch (Exception e) {
            System.out.println("Error while giving input");

        }
        try {
            EngineConfiguration config = new FileProvider("/pretupshome/tomcat5_smsr/webapps/pretups/WEB-INF/src/com/btsl/pretups/inter/uganda_webservices/client_deploy.wsdd");
            DCPServiceLocator locator = new DCPServiceLocator(config);

            locator.setDCPServiceEndpointAddress("http://172.19.69.231:8183/axis/services/DCPService");

            Remote remote = (Remote) locator.getPort(DCPServiceSoapBindingStub.class);
            Stub _axisPort = (DCPServiceSoapBindingStub) remote;

            _axisPort._setProperty(_axisPort.ENDPOINT_ADDRESS_PROPERTY, "http://172.19.69.231:8183/axis/services/DCPService");

            _axisPort._setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
            _axisPort._setProperty(WSHandlerConstants.USER, "dev");
            _axisPort._setProperty(UsernameToken.PASSWORD_TYPE, WSConstants.PASSWORD_TEXT);
            _axisPort._setProperty(WSConstants.PASSWORD_TEXT, "orange");
            _axisPort._setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, "PWCallback");

            // _axisPort._setProperty(WSHandlerConstants.ACTION,
            // WSHandlerConstants.NO_SECURITY);
            // _axisPort._createCall().setSOAPActionURI("http://www.volubill.com/dcp/soap");
            // _axisPort._createCall().setSOAPActionURI("http://www.volubill.com/DCPService/");

            _axisPort._createCall().setSOAPActionURI("http://www.volubill.com/DCPPort");

            System.out.println("Hello=================_axisPort OBJ============_axisPort=" + _axisPort);

            _service = (DCPServiceSoapBindingStub) _axisPort;

            System.out.println("Hello Service =" + _service);
            LogonResponse response = new LogonResponse();

            LogonRequest request = new LogonRequest();
            // GetAccountReq getAccountReq = new GetAccountReq();
            // GetAccountResponse getAccountResponse=new GetAccountResponse();
            request.setUsername("dev");
            request.setPassword("orange");
            request.setServantType("customercare");
            // getAccountReq.setAccountNo("256790008060");
            request.setServiceProviderName("Test");

            System.out.println("Hello=================request OBJ============request=" + request);

            response = _service.logon(request);
            // getAccountResponse=_service.getAccount(getAccountReq);

            System.out.println("Hello=================Response OBJ============response=" + response);

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

}
