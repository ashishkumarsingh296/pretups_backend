/**
 * AlepoSoapServiceLocator.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Nov 19, 2006 (02:31:34 GMT+00:00) WSDL2Java emitter.
 */

package com.inter.alepoogn.alepoognstub;

public class AlepoSoapServiceLocator extends org.apache.axis.client.Service implements com.inter.alepoogn.alepoognstub.AlepoSoapService {

    public AlepoSoapServiceLocator() {
    }

    public AlepoSoapServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AlepoSoapServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for Alepo
    private java.lang.String Alepo_address = "http://localhost:9000";

    public java.lang.String getAlepoAddress() {
        return Alepo_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AlepoWSDDServiceName = "Alepo";

    public java.lang.String getAlepoWSDDServiceName() {
        return AlepoWSDDServiceName;
    }

    public void setAlepoWSDDServiceName(java.lang.String name) {
        AlepoWSDDServiceName = name;
    }

    public com.inter.alepoogn.alepoognstub.Radius getAlepo() throws javax.xml.rpc.ServiceException {
        java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(Alepo_address);
        } catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAlepo(endpoint);
    }

    public com.inter.alepoogn.alepoognstub.Radius getAlepo(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.alepoogn.alepoognstub.AlepoSoapBindingStub _stub = new com.inter.alepoogn.alepoognstub.AlepoSoapBindingStub(portAddress, this);
            _stub.setPortName(getAlepoWSDDServiceName());
            return _stub;
        } catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAlepoEndpointAddress(java.lang.String address) {
        Alepo_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.inter.alepoogn.alepoognstub.Radius.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.alepoogn.alepoognstub.AlepoSoapBindingStub _stub = new com.inter.alepoogn.alepoognstub.AlepoSoapBindingStub(new java.net.URL(Alepo_address), this);
                _stub.setPortName(getAlepoWSDDServiceName());
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
        if ("Alepo".equals(inputPortName)) {
            return getAlepo();
        } else {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://localhost:9000", "AlepoSoapService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://localhost:9000", "Alepo"));
        }
        return ports.iterator();
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {

        if ("Alepo".equals(portName)) {
            setAlepoEndpointAddress(address);
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
