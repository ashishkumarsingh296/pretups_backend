/**
 * PaymentsPOS_bindQSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroca.dth.payment;

public class PaymentsPOS_bindQSServiceLocator extends org.apache.axis.client.Service implements com.inter.claroca.dth.payment.PaymentsPOS_bindQSService {

/**
 * OSB Service
 */

    public PaymentsPOS_bindQSServiceLocator() {
    }


    public PaymentsPOS_bindQSServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PaymentsPOS_bindQSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for PaymentsPOS_bindQSPort
    private java.lang.String PaymentsPOS_bindQSPort_address = "http://osb.busclarocenam.clarogt.americamovil.ca1:80/PaymentsPOS/ApplyPayment/PS/ApplyPayment_PS";

    public java.lang.String getPaymentsPOS_bindQSPortAddress() {
        return PaymentsPOS_bindQSPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PaymentsPOS_bindQSPortWSDDServiceName = "PaymentsPOS_bindQSPort";

    public java.lang.String getPaymentsPOS_bindQSPortWSDDServiceName() {
        return PaymentsPOS_bindQSPortWSDDServiceName;
    }

    public void setPaymentsPOS_bindQSPortWSDDServiceName(java.lang.String name) {
        PaymentsPOS_bindQSPortWSDDServiceName = name;
    }

    public com.inter.claroca.dth.payment.PaymentsPOS getPaymentsPOS_bindQSPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PaymentsPOS_bindQSPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPaymentsPOS_bindQSPort(endpoint);
    }

    public com.inter.claroca.dth.payment.PaymentsPOS getPaymentsPOS_bindQSPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.claroca.dth.payment.PaymentsPOS_bindStub _stub = new com.inter.claroca.dth.payment.PaymentsPOS_bindStub(portAddress, this);
            _stub.setPortName(getPaymentsPOS_bindQSPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPaymentsPOS_bindQSPortEndpointAddress(java.lang.String address) {
        PaymentsPOS_bindQSPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.inter.claroca.dth.payment.PaymentsPOS.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.claroca.dth.payment.PaymentsPOS_bindStub _stub = new com.inter.claroca.dth.payment.PaymentsPOS_bindStub(new java.net.URL(PaymentsPOS_bindQSPort_address), this);
                _stub.setPortName(getPaymentsPOS_bindQSPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
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
        if ("PaymentsPOS_bindQSPort".equals(inputPortName)) {
            return getPaymentsPOS_bindQSPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://esb.claro.com.gt/PaymentsPOS", "PaymentsPOS_bindQSService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://esb.claro.com.gt/PaymentsPOS", "PaymentsPOS_bindQSPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("PaymentsPOS_bindQSPort".equals(portName)) {
            setPaymentsPOS_bindQSPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
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
