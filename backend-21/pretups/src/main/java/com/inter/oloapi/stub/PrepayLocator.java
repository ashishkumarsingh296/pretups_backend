/**
 * PrepayLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.oloapi.stub;

public class PrepayLocator extends org.apache.axis.client.Service implements com.inter.oloapi.stub.Prepay {

    public PrepayLocator() {
    }


    public PrepayLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PrepayLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for prepayPort
    private java.lang.String prepayPort_address = "http://10.0.3.149/olowebserviceprepay/service.php";

    public java.lang.String getprepayPortAddress() {
        return prepayPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String prepayPortWSDDServiceName = "prepayPort";

    public java.lang.String getprepayPortWSDDServiceName() {
        return prepayPortWSDDServiceName;
    }

    public void setprepayPortWSDDServiceName(java.lang.String name) {
        prepayPortWSDDServiceName = name;
    }

    public com.inter.oloapi.stub.PrepayPortType getprepayPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(prepayPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getprepayPort(endpoint);
    }

    public com.inter.oloapi.stub.PrepayPortType getprepayPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.oloapi.stub.PrepayBindingStub _stub = new com.inter.oloapi.stub.PrepayBindingStub(portAddress, this);
            _stub.setPortName(getprepayPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setprepayPortEndpointAddress(java.lang.String address) {
        prepayPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.inter.oloapi.stub.PrepayPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.oloapi.stub.PrepayBindingStub _stub = new com.inter.oloapi.stub.PrepayBindingStub(new java.net.URL(prepayPort_address), this);
                _stub.setPortName(getprepayPortWSDDServiceName());
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
        if ("prepayPort".equals(inputPortName)) {
            return getprepayPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:prepay", "prepay");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:prepay", "prepayPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("prepayPort".equals(portName)) {
            setprepayPortEndpointAddress(address);
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
