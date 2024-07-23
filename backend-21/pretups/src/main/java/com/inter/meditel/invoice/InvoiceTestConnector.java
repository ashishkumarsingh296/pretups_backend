package com.inter.meditel.invoice;

import java.rmi.Remote;
import java.util.HashMap;

import org.apache.axis.client.Stub;
import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.orange.www.MEBS.Interfaces.ManagePayment.PaymentManagement.v1.ManagePaymentPaymentManagement;
import com.orange.www.MEBS.Interfaces.ManagePayment.PaymentManagement.v1.ManagePaymentPaymentManagementBindingStub;
import com.orange.www.MEBS.Interfaces.ManagePayment.PaymentManagement.v1.ManagePaymentPaymentManagement_MEBSLocator;

public class InvoiceTestConnector {
    private Log _log = LogFactory.getLog(InvoiceTestConnector.class.getName());
    private Stub _stub = null;
    private ManagePaymentPaymentManagement _service = null;
    private HashMap _requestMap = null;

    public InvoiceTestConnector(HashMap _requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("InvoiceTestConnector", " Entered _requestMap" + _requestMap);
        try {

            ManagePaymentPaymentManagement_MEBSLocator locator = new ManagePaymentPaymentManagement_MEBSLocator();
            Remote remote = (Remote) locator.getPort(ManagePaymentPaymentManagement.class);
            _stub = (ManagePaymentPaymentManagementBindingStub) remote;

            String url = (String) _requestMap.get("URL");
            ;
            String userName = (String) _requestMap.get("USER_NAME");
            ;
            String password = (String) _requestMap.get("PASSWORD");
            ;
            _stub._setProperty(_stub.ENDPOINT_ADDRESS_PROPERTY, url);
            _stub.setUsername(userName);
            _stub.setPassword(password);
            _stub.setTimeout(Integer.parseInt((String) _requestMap.get("TIME_OUT")));
            _service = (ManagePaymentPaymentManagementBindingStub) _stub;
        } catch (Exception e) {
            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InvoiceTestConnector[InvoiceTestConnector]", "", "", "", "Unable to get Client Stub");
            _log.error("InvoiceTestConnector", "Unable to get Client Stub");
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("InvoiceTestConnector", " Exited _service " + _service);
        }
    }

    public Stub getStub() {
        return _stub;
    }

    public void setStub(Stub _stub) {
        _stub = _stub;
    }

    public ManagePaymentPaymentManagement getService() {
        return _service;
    }

    public void setService(ManagePaymentPaymentManagement service) {
        _service = service;
    }
}
