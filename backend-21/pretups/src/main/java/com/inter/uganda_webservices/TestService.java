package com.inter.uganda_webservices;

import java.net.MalformedURLException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Calendar;

import javax.xml.rpc.ServiceException;
import javax.xml.soap.SOAPException;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.message.token.UsernameToken;

import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.AccountBean;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.DCPPort;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.DCPServiceLocator;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.DCPServiceSoapBindingStub;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.GetAccountBalanceRequest;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.GetAccountBalanceResponse;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.GetAccountBlockcausesReq;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.GetAccountBlockcausesResponse;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.GetAccountReq;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.GetAccountResponse;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.GetProductSubscriptionBlockCausesReq;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.GetProductSubscriptionBlockCausesResponse;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.GetProductSubscriptionReq;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.GetProductSubscriptionResponse;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.LogonRequest;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.LogonResponse;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.MDCGetSubscribedPackagesReq;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.SearchAccountsReq;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.SearchAccountsResponse;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.UpdateAccountReq;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.AccountResponse;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.MDCAddSubscribedPackageReq;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.MDCAddSubscribedPackageResponse;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.MDCGetSubscribedPackagesResponse;
import java.util.Calendar;

public class TestService {
    public static String MSISDN = "256790800147";
    static DCPServiceSoapBindingStub _service = null;

    public static void main(String[] args) throws RemoteException, SOAPException, MalformedURLException, ServiceException {
        try {
            org.apache.log4j.PropertyConfigurator.configure("/pretupshome/tomcat5_web/webapps/pretups/WEB-INF/classes/configfiles/LogConfig.props");
            EngineConfiguration config = new FileProvider("/pretupshome/tomcat5_smsr/webapps/pretups/WEB-INF/src/com/btsl/pretups/inter/uganda_webservices/client_deploy.wsdd");

            DCPServiceLocator locator = new DCPServiceLocator(config);
            // locator.setDCPServiceEndpointAddress("http://172.19.69.209:8183/axis/services/DCPService");
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

            _service = (DCPServiceSoapBindingStub) _axisPort;

            String sessionId = logon(_service);
            Thread.sleep(5000);
            try {
                System.out.println("First Validate Session_ID=" + sessionId);
                getAccount(sessionId, _service);
                System.out.println("First Validate Completed=" + sessionId);
            } catch (Exception e) {
                System.out.println("Exception for validate.");
                e.printStackTrace();
            }

            try {
                System.out.println("Credit Session_ID=" + sessionId);
                mdcAddSubscribedPackage(sessionId, _service);
                System.out.println("Credit Completed=" + sessionId);
            } catch (Exception e) {
                System.out.println("Exception for Credit.");
                e.printStackTrace();
            }

            /*
             * try
             * {
             * System.out.println("Second Validate Session_ID="+sessionId);
             * mdcGetSubscribedPackage(sessionId,_service);
             * System.out.println("Second Validate Completed="+sessionId);
             * }
             * catch(Exception e)
             * {
             * System.out.println("Exception for validate.");
             * e.printStackTrace();
             * }
             */
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void searchAccount(String p_sessionID, DCPPort service) throws SOAPException, RemoteException, MalformedURLException, ServiceException {

        SearchAccountsReq searchAccountsReq = new SearchAccountsReq();
        searchAccountsReq.setAccountNo(MSISDN);
        SearchAccountsResponse searchAccountsResponse = service.searchAccounts(searchAccountsReq);

        if (searchAccountsResponse.getError() != null) {
            if (searchAccountsResponse.getError(0).getMessage().equals("Session does not exist")) {
                setVolubillAuthentication(p_sessionID, service);
                searchAccountsResponse = service.searchAccounts(searchAccountsReq);
            }
        }
    }

    public static void getAccount(String p_sessionID, DCPPort service) throws SOAPException, RemoteException, MalformedURLException, ServiceException {
        GetAccountReq getAccountReq = new GetAccountReq();
        getAccountReq.setAccountNo(MSISDN);
        getAccountReq.setServiceProviderName("Test");
        GetAccountResponse getAccountResponse = service.getAccount(getAccountReq);
        System.out.println("getAccount Response OBJ=" + getAccountResponse);

        if (getAccountResponse.getError() != null) {
            if (getAccountResponse.getError(0).getMessage().equals("Session does not exist")) {
                setVolubillAuthentication(p_sessionID, service);
                getAccountResponse = service.getAccount(getAccountReq);
            }
        }

        System.out.println(getAccountResponse.toString());
    }

    public static void updateAccount(String p_sessionID, DCPPort service) throws SOAPException, RemoteException, MalformedURLException, ServiceException {

        UpdateAccountReq updateAccountReq = new UpdateAccountReq();
        updateAccountReq.setAccountNo(MSISDN);
        updateAccountReq.setServiceProviderName("Test");
        AccountBean account = new AccountBean();
        account.setRefillAmount(Double.parseDouble("100"));
        updateAccountReq.setAccount(account);
        AccountResponse accountResponse = service.updateAccount(updateAccountReq);
        System.out.println("updateAccount Response OBJ=" + accountResponse);

        if (accountResponse.getError() != null) {
            if (accountResponse.getError(0).getMessage().equals("Session does not exist")) {
                setVolubillAuthentication(p_sessionID, service);
                accountResponse = service.updateAccount(updateAccountReq);
            }
        }
    }

    public static void getAccountBalance(String p_sessionID, DCPPort service) throws SOAPException, RemoteException, MalformedURLException, ServiceException {

        GetAccountBalanceRequest getAccountReq = new GetAccountBalanceRequest();
        getAccountReq.setAccountNo(MSISDN);
        getAccountReq.setServiceProviderName("Test");
        System.out.println("Hello=================request OBJ============request=" + getAccountReq);
        GetAccountBalanceResponse getAccountResponse = service.getAccountBalance(getAccountReq);

        if (getAccountResponse.getError() != null) {
            if (getAccountResponse.getError(0).getMessage().equals("Session does not exist")) {
                setVolubillAuthentication(p_sessionID, service);
                getAccountResponse = service.getAccountBalance(getAccountReq);
            }
        }

        // System.out.println(PojoDescriber.objToString(getAccountResponse));
    }

    public static void getAccountBlockcauses(String p_sessionID, DCPPort service) throws SOAPException, RemoteException, MalformedURLException, ServiceException {

        GetAccountBlockcausesReq getAccountReq = new GetAccountBlockcausesReq();
        getAccountReq.setAccountNo(MSISDN);
        getAccountReq.setServiceProviderName("Test");
        GetAccountBlockcausesResponse getAccountResponse = service.getAccountBlockcauses(getAccountReq);

        if (getAccountResponse.getError() != null) {
            if (getAccountResponse.getError(0).getMessage().equals("Session does not exist")) {
                setVolubillAuthentication(p_sessionID, service);
                getAccountResponse = service.getAccountBlockcauses(getAccountReq);
            }
        }

        // System.out.println(PojoDescriber.objToString(getAccountResponse));
    }

    public static void getProductSubscription(String p_sessionID, DCPPort service) throws SOAPException, RemoteException, MalformedURLException, ServiceException {

        GetProductSubscriptionReq getProductSubscriptionReq = new GetProductSubscriptionReq();
        getProductSubscriptionReq.setAccountNo(MSISDN);
        getProductSubscriptionReq.setServiceProviderName("Test");
        GetProductSubscriptionResponse getAccountResponse = service.getProductSubscription(getProductSubscriptionReq);

        // System.out.println(PojoDescriber.objToString(getAccountResponse));
    }

    public static void getProductSubscriptionBlockCauses(String p_sessionID, DCPPort service) throws SOAPException, RemoteException, MalformedURLException, ServiceException {

        GetProductSubscriptionBlockCausesReq getProductSubscriptionReq = new GetProductSubscriptionBlockCausesReq();
        getProductSubscriptionReq.setAccountNo(MSISDN);
        getProductSubscriptionReq.setServiceProviderName("Test");
        GetProductSubscriptionBlockCausesResponse getAccountResponse = service.getProductSubscriptionBlockCauses(getProductSubscriptionReq);

        // System.out.println(PojoDescriber.objToString(getAccountResponse));
    }

    public static String logon(DCPPort service) throws RemoteException {
        LogonRequest logonReq = new LogonRequest();
        logonReq.setUsername("dev");
        logonReq.setPassword("orange");
        System.out.println("Hello=================request OBJ============request=" + logonReq);
        LogonResponse logonResponse = service.logon(logonReq);
        System.out.println("Hello=================Response OBJ============response=" + logonResponse);
        if (logonResponse.getError() != null) {
            String errorMessage = logonResponse.getError(0).getMessage();
            throw new RuntimeException("Error accessing volubill service : " + errorMessage);
        }

        SOAPHeaderElement sessionHeader = ((Stub) service).getResponseHeader("http://xml.apache.org/axis/session", "sessionID");
        String sessionId = sessionHeader.getFirstChild().getNodeValue();

        return sessionId;
    }

    public static void mdcAddSubscribedPackage(String p_sessionID, DCPPort service) throws SOAPException, RemoteException, MalformedURLException, ServiceException {
        MDCAddSubscribedPackageReq mdcAddSubscribedPackageReq = new MDCAddSubscribedPackageReq();
        mdcAddSubscribedPackageReq.setServiceProviderName("Test");
        mdcAddSubscribedPackageReq.setMsisdn(MSISDN);
        mdcAddSubscribedPackageReq.setPackageName("IEW Bronze 6 month");
        Calendar purchaseDate = Calendar.getInstance();
        mdcAddSubscribedPackageReq.setPurchaseDate(purchaseDate);
        System.out.println("Hello=================request OBJ============request=" + mdcAddSubscribedPackageReq);
        MDCAddSubscribedPackageResponse mdcResponse = service.mdcAddSubscribedPackage(mdcAddSubscribedPackageReq);
        System.out.println("Hello=================Response OBJ============response=" + mdcResponse);
        if (mdcResponse.getError() != null) {
            if (mdcResponse.getError(0).getMessage().equals("Session does not exist")) {
                setVolubillAuthentication(p_sessionID, service);
                mdcResponse = service.mdcAddSubscribedPackage(mdcAddSubscribedPackageReq);
            }
        }
    }

    public static void mdcGetSubscribedPackage(String p_sessionID, DCPPort service) throws SOAPException, RemoteException, MalformedURLException, ServiceException {
        MDCGetSubscribedPackagesReq mdcGetSubscribedPackageReq = new MDCGetSubscribedPackagesReq();
        mdcGetSubscribedPackageReq.setServiceProviderName("Test");
        mdcGetSubscribedPackageReq.setMsisdn(MSISDN);
        System.out.println("Hello=================request OBJ============request=" + mdcGetSubscribedPackageReq);
        MDCGetSubscribedPackagesResponse mdcGetResponse = service.mdcGetSubscribedPackages(mdcGetSubscribedPackageReq);
        System.out.println("Hello=================Response OBJ============response=" + mdcGetResponse);
        if (mdcGetResponse.getError() != null) {
            if (mdcGetResponse.getError(0).getMessage().equals("Session does not exist")) {
                setVolubillAuthentication(p_sessionID, service);
                mdcGetResponse = service.mdcGetSubscribedPackages(mdcGetSubscribedPackageReq);
            }
        }
    }

    private static void setVolubillAuthentication(String p_sessionID, DCPPort service) throws RemoteException, SOAPException {
        SOAPHeaderElement sessionHeader = new SOAPHeaderElement("http://xml.apache.org/axis/session", "sessionID");
        sessionHeader.setMustUnderstand(false);
        sessionHeader.addTextNode(p_sessionID);

        ((Stub) service).setHeader(sessionHeader);
    }
}
