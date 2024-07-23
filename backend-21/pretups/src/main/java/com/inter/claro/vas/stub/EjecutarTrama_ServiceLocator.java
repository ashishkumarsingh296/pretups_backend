/**
 * EjecutarTrama_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claro.vas.stub;

public class EjecutarTrama_ServiceLocator extends org.apache.axis.client.Service implements com.inter.claro.vas.stub.EjecutarTrama_Service {

    public EjecutarTrama_ServiceLocator() {
    }


    public EjecutarTrama_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EjecutarTrama_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for EjecutarTramaSOAP
    private java.lang.String EjecutarTramaSOAP_address = "http://www.comcel.com.co/";

    public java.lang.String getEjecutarTramaSOAPAddress() {
        return EjecutarTramaSOAP_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String EjecutarTramaSOAPWSDDServiceName = "EjecutarTramaSOAP";

    public java.lang.String getEjecutarTramaSOAPWSDDServiceName() {
        return EjecutarTramaSOAPWSDDServiceName;
    }

    public void setEjecutarTramaSOAPWSDDServiceName(java.lang.String name) {
        EjecutarTramaSOAPWSDDServiceName = name;
    }

    public com.inter.claro.vas.stub.EjecutarTrama_PortType getEjecutarTramaSOAP() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(EjecutarTramaSOAP_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getEjecutarTramaSOAP(endpoint);
    }

    public com.inter.claro.vas.stub.EjecutarTrama_PortType getEjecutarTramaSOAP(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.claro.vas.stub.EjecutarTramaSOAPStub _stub = new com.inter.claro.vas.stub.EjecutarTramaSOAPStub(portAddress, this);
            _stub.setPortName(getEjecutarTramaSOAPWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setEjecutarTramaSOAPEndpointAddress(java.lang.String address) {
        EjecutarTramaSOAP_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.inter.claro.vas.stub.EjecutarTrama_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.claro.vas.stub.EjecutarTramaSOAPStub _stub = new com.inter.claro.vas.stub.EjecutarTramaSOAPStub(new java.net.URL(EjecutarTramaSOAP_address), this);
                _stub.setPortName(getEjecutarTramaSOAPWSDDServiceName());
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
        if ("EjecutarTramaSOAP".equals(inputPortName)) {
            return getEjecutarTramaSOAP();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://172.30.16.22:8080/ITEL/services/ITELimpl/", "EjecutarTrama");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://172.30.16.22:8080/ITEL/services/ITELimpl/", "EjecutarTramaSOAP"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("EjecutarTramaSOAP".equals(portName)) {
            setEjecutarTramaSOAPEndpointAddress(address);
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
