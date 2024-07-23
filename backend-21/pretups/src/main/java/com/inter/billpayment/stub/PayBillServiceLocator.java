/**
 * PayBillServiceLocator.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.billpayment.stub;

public class PayBillServiceLocator extends org.apache.axis.client.Service implements com.inter.billpayment.stub.PayBillService {

    public PayBillServiceLocator() {
    }

    public PayBillServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PayBillServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for PayBillPort
    private java.lang.String PayBillPort_address = "http://172.0.0.1:8080/PBTLBillPayService/PayBillService";

    public java.lang.String getPayBillPortAddress() {
        return PayBillPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PayBillPortWSDDServiceName = "PayBillPort";

    public java.lang.String getPayBillPortWSDDServiceName() {
        return PayBillPortWSDDServiceName;
    }

    public void setPayBillPortWSDDServiceName(java.lang.String name) {
        PayBillPortWSDDServiceName = name;
    }

    public com.inter.billpayment.stub.PayBill getPayBillPort() throws javax.xml.rpc.ServiceException {
        java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PayBillPort_address);
        } catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPayBillPort(endpoint);
    }

    public com.inter.billpayment.stub.PayBill getPayBillPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.billpayment.stub.PayBillPortBindingStub _stub = new com.inter.billpayment.stub.PayBillPortBindingStub(portAddress, this);
            _stub.setPortName(getPayBillPortWSDDServiceName());
            return _stub;
        } catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPayBillPortEndpointAddress(java.lang.String address) {
        PayBillPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.inter.billpayment.stub.PayBill.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.billpayment.stub.PayBillPortBindingStub _stub = new com.inter.billpayment.stub.PayBillPortBindingStub(new java.net.URL(PayBillPort_address), this);
                _stub.setPortName(getPayBillPortWSDDServiceName());
                return _stub;
            }
        } catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("PayBillPort".equals(inputPortName)) {
            return getPayBillPort();
        } else {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://pay.bill.payments.pbtl.java.ushacomm.com/", "PayBillService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://pay.bill.payments.pbtl.java.ushacomm.com/", "PayBillPort"));
        }
        return ports.iterator();
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {

        if ("PayBillPort".equals(portName)) {
            setPayBillPortEndpointAddress(address);
        } else { // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
