/**
 * AutogestionWsLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroca.dth.enquiry;

public class AutogestionWsLocator extends org.apache.axis.client.Service implements com.inter.claroca.dth.enquiry.AutogestionWs {

    public AutogestionWsLocator() {
    }


    public AutogestionWsLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AutogestionWsLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for AutogestionWsSoap12
    private java.lang.String AutogestionWsSoap12_address = "http://172.24.4.247:7500/AutogestionWs.asmx";

    public java.lang.String getAutogestionWsSoap12Address() {
        return AutogestionWsSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AutogestionWsSoap12WSDDServiceName = "AutogestionWsSoap12";

    public java.lang.String getAutogestionWsSoap12WSDDServiceName() {
        return AutogestionWsSoap12WSDDServiceName;
    }

    public void setAutogestionWsSoap12WSDDServiceName(java.lang.String name) {
        AutogestionWsSoap12WSDDServiceName = name;
    }

    public com.inter.claroca.dth.enquiry.AutogestionWsSoap_PortType getAutogestionWsSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AutogestionWsSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAutogestionWsSoap12(endpoint);
    }

    public com.inter.claroca.dth.enquiry.AutogestionWsSoap_PortType getAutogestionWsSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.claroca.dth.enquiry.AutogestionWsSoap12Stub _stub = new com.inter.claroca.dth.enquiry.AutogestionWsSoap12Stub(portAddress, this);
            _stub.setPortName(getAutogestionWsSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAutogestionWsSoap12EndpointAddress(java.lang.String address) {
        AutogestionWsSoap12_address = address;
    }


    // Use to get a proxy class for AutogestionWsSoap
    private java.lang.String AutogestionWsSoap_address = "http://172.24.4.247:7500/AutogestionWs.asmx";

    public java.lang.String getAutogestionWsSoapAddress() {
        return AutogestionWsSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AutogestionWsSoapWSDDServiceName = "AutogestionWsSoap";

    public java.lang.String getAutogestionWsSoapWSDDServiceName() {
        return AutogestionWsSoapWSDDServiceName;
    }

    public void setAutogestionWsSoapWSDDServiceName(java.lang.String name) {
        AutogestionWsSoapWSDDServiceName = name;
    }

    public com.inter.claroca.dth.enquiry.AutogestionWsSoap_PortType getAutogestionWsSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AutogestionWsSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAutogestionWsSoap(endpoint);
    }

    public com.inter.claroca.dth.enquiry.AutogestionWsSoap_PortType getAutogestionWsSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.claroca.dth.enquiry.AutogestionWsSoap_BindingStub _stub = new com.inter.claroca.dth.enquiry.AutogestionWsSoap_BindingStub(portAddress, this);
            _stub.setPortName(getAutogestionWsSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAutogestionWsSoapEndpointAddress(java.lang.String address) {
        AutogestionWsSoap_address = address;
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
            if (com.inter.claroca.dth.enquiry.AutogestionWsSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.claroca.dth.enquiry.AutogestionWsSoap12Stub _stub = new com.inter.claroca.dth.enquiry.AutogestionWsSoap12Stub(new java.net.URL(AutogestionWsSoap12_address), this);
                _stub.setPortName(getAutogestionWsSoap12WSDDServiceName());
                return _stub;
            }
            if (com.inter.claroca.dth.enquiry.AutogestionWsSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.claroca.dth.enquiry.AutogestionWsSoap_BindingStub _stub = new com.inter.claroca.dth.enquiry.AutogestionWsSoap_BindingStub(new java.net.URL(AutogestionWsSoap_address), this);
                _stub.setPortName(getAutogestionWsSoapWSDDServiceName());
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
        if ("AutogestionWsSoap12".equals(inputPortName)) {
            return getAutogestionWsSoap12();
        }
        else if ("AutogestionWsSoap".equals(inputPortName)) {
            return getAutogestionWsSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "AutogestionWs");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "AutogestionWsSoap12"));
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "AutogestionWsSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("AutogestionWsSoap12".equals(portName)) {
            setAutogestionWsSoap12EndpointAddress(address);
        }
        else 
if ("AutogestionWsSoap".equals(portName)) {
            setAutogestionWsSoapEndpointAddress(address);
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
