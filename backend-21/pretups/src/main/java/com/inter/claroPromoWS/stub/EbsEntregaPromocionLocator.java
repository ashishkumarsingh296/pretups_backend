/**
 * EbsEntregaPromocionLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroPromoWS.stub;

public class EbsEntregaPromocionLocator extends org.apache.axis.client.Service implements com.inter.claroPromoWS.stub.EbsEntregaPromocion {

    public EbsEntregaPromocionLocator() {
    }


    public EbsEntregaPromocionLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EbsEntregaPromocionLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ebsEntregaPromocionSB11
    private java.lang.String ebsEntregaPromocionSB11_address = "http://limdeseaiv13.tim.com.pe:7909/EbsEntregaPromociones/ebsEntregaPromocionSB11";

    public java.lang.String getebsEntregaPromocionSB11Address() {
        return ebsEntregaPromocionSB11_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ebsEntregaPromocionSB11WSDDServiceName = "ebsEntregaPromocionSB11";

    public java.lang.String getebsEntregaPromocionSB11WSDDServiceName() {
        return ebsEntregaPromocionSB11WSDDServiceName;
    }

    public void setebsEntregaPromocionSB11WSDDServiceName(java.lang.String name) {
        ebsEntregaPromocionSB11WSDDServiceName = name;
    }

    public com.inter.claroPromoWS.stub.EbsEntregaPromocionPortType getebsEntregaPromocionSB11() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ebsEntregaPromocionSB11_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getebsEntregaPromocionSB11(endpoint);
    }

    public com.inter.claroPromoWS.stub.EbsEntregaPromocionPortType getebsEntregaPromocionSB11(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.claroPromoWS.stub.EbsEntregaPromocionSOAP11BindingStub _stub = new com.inter.claroPromoWS.stub.EbsEntregaPromocionSOAP11BindingStub(portAddress, this);
            _stub.setPortName(getebsEntregaPromocionSB11WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setebsEntregaPromocionSB11EndpointAddress(java.lang.String address) {
        ebsEntregaPromocionSB11_address = address;
    }


    // Use to get a proxy class for ebsEntregaPromocionSB12
    private java.lang.String ebsEntregaPromocionSB12_address = "http://localhost:7001/EntregaPromocion/ebsEntregaPromocionSB12";

    public java.lang.String getebsEntregaPromocionSB12Address() {
        return ebsEntregaPromocionSB12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ebsEntregaPromocionSB12WSDDServiceName = "ebsEntregaPromocionSB12";

    public java.lang.String getebsEntregaPromocionSB12WSDDServiceName() {
        return ebsEntregaPromocionSB12WSDDServiceName;
    }

    public void setebsEntregaPromocionSB12WSDDServiceName(java.lang.String name) {
        ebsEntregaPromocionSB12WSDDServiceName = name;
    }

    public com.inter.claroPromoWS.stub.EbsEntregaPromocionPortType getebsEntregaPromocionSB12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ebsEntregaPromocionSB12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getebsEntregaPromocionSB12(endpoint);
    }

    public com.inter.claroPromoWS.stub.EbsEntregaPromocionPortType getebsEntregaPromocionSB12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.claroPromoWS.stub.EbsEntregaPromocionSOAP12BindingStub _stub = new com.inter.claroPromoWS.stub.EbsEntregaPromocionSOAP12BindingStub(portAddress, this);
            _stub.setPortName(getebsEntregaPromocionSB12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setebsEntregaPromocionSB12EndpointAddress(java.lang.String address) {
        ebsEntregaPromocionSB12_address = address;
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
            if (com.inter.claroPromoWS.stub.EbsEntregaPromocionPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.claroPromoWS.stub.EbsEntregaPromocionSOAP11BindingStub _stub = new com.inter.claroPromoWS.stub.EbsEntregaPromocionSOAP11BindingStub(new java.net.URL(ebsEntregaPromocionSB11_address), this);
                _stub.setPortName(getebsEntregaPromocionSB11WSDDServiceName());
                return _stub;
            }
            if (com.inter.claroPromoWS.stub.EbsEntregaPromocionPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.claroPromoWS.stub.EbsEntregaPromocionSOAP12BindingStub _stub = new com.inter.claroPromoWS.stub.EbsEntregaPromocionSOAP12BindingStub(new java.net.URL(ebsEntregaPromocionSB12_address), this);
                _stub.setPortName(getebsEntregaPromocionSB12WSDDServiceName());
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
        if ("ebsEntregaPromocionSB11".equals(inputPortName)) {
            return getebsEntregaPromocionSB11();
        }
        else if ("ebsEntregaPromocionSB12".equals(inputPortName)) {
            return getebsEntregaPromocionSB12();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/promocion/EntregaPromocion/ws", "ebsEntregaPromocion");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/promocion/EntregaPromocion/ws", "ebsEntregaPromocionSB11"));
            ports.add(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/promocion/EntregaPromocion/ws", "ebsEntregaPromocionSB12"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ebsEntregaPromocionSB11".equals(portName)) {
            setebsEntregaPromocionSB11EndpointAddress(address);
        }
        else 
if ("ebsEntregaPromocionSB12".equals(portName)) {
            setebsEntregaPromocionSB12EndpointAddress(address);
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
