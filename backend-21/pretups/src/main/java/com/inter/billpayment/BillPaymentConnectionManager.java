package com.inter.billpayment;

import org.apache.axis.client.Stub;

import com.inter.billpayment.stub.PayBill;
import com.inter.billpayment.stub.PayBillServiceLocator;

public class BillPaymentConnectionManager {

    private PayBill _stub = null;
    private static Stub _stubSuper = null;

    /**
     * @author vipan.kumar
     * @date 26 Oct 2010
     */
    public BillPaymentConnectionManager(String p_serviceAddress, int p_timeoutStr, String p_username, String p_password) throws javax.xml.rpc.ServiceException, java.net.MalformedURLException {
        System.out.println("p_serviceAddress = " + p_serviceAddress);
        PayBillServiceLocator payBillServiceLocator = new PayBillServiceLocator();
        _stub = payBillServiceLocator.getPayBillPort(new java.net.URL(p_serviceAddress));
        _stubSuper = (Stub) _stub;
        _stubSuper.setTimeout(p_timeoutStr);

        _stubSuper._setProperty(Stub.USERNAME_PROPERTY, p_username);
        _stubSuper._setProperty(Stub.PASSWORD_PROPERTY, p_password);
        _stubSuper.setUsername(p_username);
        _stubSuper.setPassword(p_password);

    }

    protected PayBill getBillPaymentClient() {
        return (PayBill) _stubSuper;
    }

}
