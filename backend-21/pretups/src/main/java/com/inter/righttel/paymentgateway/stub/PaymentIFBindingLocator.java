/**
 * PaymentIFBindingLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.righttel.paymentgateway.stub;

public class PaymentIFBindingLocator extends org.apache.axis.client.Service implements com.inter.righttel.paymentgateway.stub.PaymentIFBinding {

    public PaymentIFBindingLocator() {
    }


    public PaymentIFBindingLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PaymentIFBindingLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for PaymentIFBindingSoap12
    private java.lang.String PaymentIFBindingSoap12_address = "https://sep.shaparak.ir/payments/referencepayment.asmx";

    public java.lang.String getPaymentIFBindingSoap12Address() {
        return PaymentIFBindingSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PaymentIFBindingSoap12WSDDServiceName = "PaymentIFBindingSoap12";

    public java.lang.String getPaymentIFBindingSoap12WSDDServiceName() {
        return PaymentIFBindingSoap12WSDDServiceName;
    }

    public void setPaymentIFBindingSoap12WSDDServiceName(java.lang.String name) {
        PaymentIFBindingSoap12WSDDServiceName = name;
    }

    public com.inter.righttel.paymentgateway.stub.PaymentIFBindingSoap_PortType getPaymentIFBindingSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PaymentIFBindingSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPaymentIFBindingSoap12(endpoint);
    }

    public com.inter.righttel.paymentgateway.stub.PaymentIFBindingSoap_PortType getPaymentIFBindingSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.righttel.paymentgateway.stub.PaymentIFBindingSoap12Stub _stub = new com.inter.righttel.paymentgateway.stub.PaymentIFBindingSoap12Stub(portAddress, this);
            _stub.setPortName(getPaymentIFBindingSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPaymentIFBindingSoap12EndpointAddress(java.lang.String address) {
        PaymentIFBindingSoap12_address = address;
    }


    // Use to get a proxy class for PaymentIFBindingSoap
    private java.lang.String PaymentIFBindingSoap_address = "https://sep.shaparak.ir/payments/referencepayment.asmx";

    public java.lang.String getPaymentIFBindingSoapAddress() {
        return PaymentIFBindingSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PaymentIFBindingSoapWSDDServiceName = "PaymentIFBindingSoap";

    public java.lang.String getPaymentIFBindingSoapWSDDServiceName() {
        return PaymentIFBindingSoapWSDDServiceName;
    }

    public void setPaymentIFBindingSoapWSDDServiceName(java.lang.String name) {
        PaymentIFBindingSoapWSDDServiceName = name;
    }

    public com.inter.righttel.paymentgateway.stub.PaymentIFBindingSoap_PortType getPaymentIFBindingSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PaymentIFBindingSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPaymentIFBindingSoap(endpoint);
    }

    public com.inter.righttel.paymentgateway.stub.PaymentIFBindingSoap_PortType getPaymentIFBindingSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.righttel.paymentgateway.stub.PaymentIFBindingSoapStub _stub = new com.inter.righttel.paymentgateway.stub.PaymentIFBindingSoapStub(portAddress, this);
            _stub.setPortName(getPaymentIFBindingSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPaymentIFBindingSoapEndpointAddress(java.lang.String address) {
        PaymentIFBindingSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.inter.righttel.paymentgateway.stub.PaymentIFBindingSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.righttel.paymentgateway.stub.PaymentIFBindingSoap12Stub _stub = new com.inter.righttel.paymentgateway.stub.PaymentIFBindingSoap12Stub(new java.net.URL(PaymentIFBindingSoap12_address), this);
                _stub.setPortName(getPaymentIFBindingSoap12WSDDServiceName());
                return _stub;
            }
            if (com.inter.righttel.paymentgateway.stub.PaymentIFBindingSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.righttel.paymentgateway.stub.PaymentIFBindingSoapStub _stub = new com.inter.righttel.paymentgateway.stub.PaymentIFBindingSoapStub(new java.net.URL(PaymentIFBindingSoap_address), this);
                _stub.setPortName(getPaymentIFBindingSoapWSDDServiceName());
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
        if ("PaymentIFBindingSoap12".equals(inputPortName)) {
            return getPaymentIFBindingSoap12();
        }
        else if ("PaymentIFBindingSoap".equals(inputPortName)) {
            return getPaymentIFBindingSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:Foo", "PaymentIFBinding");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:Foo", "PaymentIFBindingSoap12"));
            ports.add(new javax.xml.namespace.QName("urn:Foo", "PaymentIFBindingSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("PaymentIFBindingSoap12".equals(portName)) {
            setPaymentIFBindingSoap12EndpointAddress(address);
        }
        else 
if ("PaymentIFBindingSoap".equals(portName)) {
            setPaymentIFBindingSoapEndpointAddress(address);
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
