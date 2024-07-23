/**
 * StreetSellerWSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroUserInfoWS.stub;

public class StreetSellerWSServiceLocator extends org.apache.axis.client.Service implements com.inter.claroUserInfoWS.stub.StreetSellerWSService {

    public StreetSellerWSServiceLocator() {
    }


    public StreetSellerWSServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public StreetSellerWSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for StreetSellerWSSoap
    private java.lang.String StreetSellerWSSoap_address = "http://limdeseaiv22.tim.com.pe:8909/SapWS/StreetSellerWS";

    public java.lang.String getStreetSellerWSSoapAddress() {
        return StreetSellerWSSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String StreetSellerWSSoapWSDDServiceName = "StreetSellerWSSoap";

    public java.lang.String getStreetSellerWSSoapWSDDServiceName() {
        return StreetSellerWSSoapWSDDServiceName;
    }

    public void setStreetSellerWSSoapWSDDServiceName(java.lang.String name) {
        StreetSellerWSSoapWSDDServiceName = name;
    }

    public com.inter.claroUserInfoWS.stub.StreetSellerWSSoap_PortType getStreetSellerWSSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(StreetSellerWSSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getStreetSellerWSSoap(endpoint);
    }

    public com.inter.claroUserInfoWS.stub.StreetSellerWSSoap_PortType getStreetSellerWSSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.claroUserInfoWS.stub.StreetSellerWSServiceSoapBindingStub _stub = new com.inter.claroUserInfoWS.stub.StreetSellerWSServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getStreetSellerWSSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setStreetSellerWSSoapEndpointAddress(java.lang.String address) {
        StreetSellerWSSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.inter.claroUserInfoWS.stub.StreetSellerWSSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.claroUserInfoWS.stub.StreetSellerWSServiceSoapBindingStub _stub = new com.inter.claroUserInfoWS.stub.StreetSellerWSServiceSoapBindingStub(new java.net.URL(StreetSellerWSSoap_address), this);
                _stub.setPortName(getStreetSellerWSSoapWSDDServiceName());
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
        if ("StreetSellerWSSoap".equals(inputPortName)) {
            return getStreetSellerWSSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.openuri.org/", "StreetSellerWSService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.openuri.org/", "StreetSellerWSSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("StreetSellerWSSoap".equals(portName)) {
            setStreetSellerWSSoapEndpointAddress(address);
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
