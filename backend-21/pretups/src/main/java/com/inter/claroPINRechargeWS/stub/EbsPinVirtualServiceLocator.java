/**
 * EbsPinVirtualServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroPINRechargeWS.stub;

public class EbsPinVirtualServiceLocator extends org.apache.axis.client.Service implements com.inter.claroPINRechargeWS.stub.EbsPinVirtualService {

    public EbsPinVirtualServiceLocator() {
    }


    public EbsPinVirtualServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EbsPinVirtualServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for EbsPinVirtualSoapPort
    private java.lang.String EbsPinVirtualSoapPort_address = "http://limdeseaiv13.tim.com.pe:7909/ConsultaPinVirtual/EbsPinVirtualSoapPort";

    public java.lang.String getEbsPinVirtualSoapPortAddress() {
        return EbsPinVirtualSoapPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String EbsPinVirtualSoapPortWSDDServiceName = "EbsPinVirtualSoapPort";

    public java.lang.String getEbsPinVirtualSoapPortWSDDServiceName() {
        return EbsPinVirtualSoapPortWSDDServiceName;
    }

    public void setEbsPinVirtualSoapPortWSDDServiceName(java.lang.String name) {
        EbsPinVirtualSoapPortWSDDServiceName = name;
    }

    public com.inter.claroPINRechargeWS.stub.EbsPinVirtual getEbsPinVirtualSoapPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(EbsPinVirtualSoapPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getEbsPinVirtualSoapPort(endpoint);
    }

    public com.inter.claroPINRechargeWS.stub.EbsPinVirtual getEbsPinVirtualSoapPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.claroPINRechargeWS.stub.EbsPinVirtualSOAPBindingStub _stub = new com.inter.claroPINRechargeWS.stub.EbsPinVirtualSOAPBindingStub(portAddress, this);
            _stub.setPortName(getEbsPinVirtualSoapPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setEbsPinVirtualSoapPortEndpointAddress(java.lang.String address) {
        EbsPinVirtualSoapPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.inter.claroPINRechargeWS.stub.EbsPinVirtual.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.claroPINRechargeWS.stub.EbsPinVirtualSOAPBindingStub _stub = new com.inter.claroPINRechargeWS.stub.EbsPinVirtualSOAPBindingStub(new java.net.URL(EbsPinVirtualSoapPort_address), this);
                _stub.setPortName(getEbsPinVirtualSoapPortWSDDServiceName());
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
        if ("EbsPinVirtualSoapPort".equals(inputPortName)) {
            return getEbsPinVirtualSoapPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://claro.com.pe/eai/venta/PinVirtual", "EbsPinVirtualService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://claro.com.pe/eai/venta/PinVirtual", "EbsPinVirtualSoapPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("EbsPinVirtualSoapPort".equals(portName)) {
            setEbsPinVirtualSoapPortEndpointAddress(address);
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
