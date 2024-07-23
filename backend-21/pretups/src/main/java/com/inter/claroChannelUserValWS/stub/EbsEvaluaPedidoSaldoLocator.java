/**
 * EbsEvaluaPedidoSaldoLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroChannelUserValWS.stub;

public class EbsEvaluaPedidoSaldoLocator extends org.apache.axis.client.Service implements com.inter.claroChannelUserValWS.stub.EbsEvaluaPedidoSaldo {

    public EbsEvaluaPedidoSaldoLocator() {
    }


    public EbsEvaluaPedidoSaldoLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EbsEvaluaPedidoSaldoLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ebsEvaluaPedidoSaldoSB11
    private java.lang.String ebsEvaluaPedidoSaldoSB11_address = "http://limdeseaiv13.tim.com.pe:7909/EvaluaPedidoSaldoEAR/ebsEvaluaPedidoSaldoSB11";

    public java.lang.String getebsEvaluaPedidoSaldoSB11Address() {
        return ebsEvaluaPedidoSaldoSB11_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ebsEvaluaPedidoSaldoSB11WSDDServiceName = "ebsEvaluaPedidoSaldoSB11";

    public java.lang.String getebsEvaluaPedidoSaldoSB11WSDDServiceName() {
        return ebsEvaluaPedidoSaldoSB11WSDDServiceName;
    }

    public void setebsEvaluaPedidoSaldoSB11WSDDServiceName(java.lang.String name) {
        ebsEvaluaPedidoSaldoSB11WSDDServiceName = name;
    }

    public com.inter.claroChannelUserValWS.stub.EbsEvaluaPedidoSaldoPortType getebsEvaluaPedidoSaldoSB11() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ebsEvaluaPedidoSaldoSB11_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getebsEvaluaPedidoSaldoSB11(endpoint);
    }

    public com.inter.claroChannelUserValWS.stub.EbsEvaluaPedidoSaldoPortType getebsEvaluaPedidoSaldoSB11(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.claroChannelUserValWS.stub.EbsEvaluaPedidoSaldoPortTypeSOAP11BindingStub _stub = new com.inter.claroChannelUserValWS.stub.EbsEvaluaPedidoSaldoPortTypeSOAP11BindingStub(portAddress, this);
            _stub.setPortName(getebsEvaluaPedidoSaldoSB11WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setebsEvaluaPedidoSaldoSB11EndpointAddress(java.lang.String address) {
        ebsEvaluaPedidoSaldoSB11_address = address;
    }


    // Use to get a proxy class for ebsEvaluaPedidoSaldoSB12
    private java.lang.String ebsEvaluaPedidoSaldoSB12_address = "http://localhost/EvaluaPedidoSaldo/ebsEvaluaPedidoSaldoSB12";

    public java.lang.String getebsEvaluaPedidoSaldoSB12Address() {
        return ebsEvaluaPedidoSaldoSB12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ebsEvaluaPedidoSaldoSB12WSDDServiceName = "ebsEvaluaPedidoSaldoSB12";

    public java.lang.String getebsEvaluaPedidoSaldoSB12WSDDServiceName() {
        return ebsEvaluaPedidoSaldoSB12WSDDServiceName;
    }

    public void setebsEvaluaPedidoSaldoSB12WSDDServiceName(java.lang.String name) {
        ebsEvaluaPedidoSaldoSB12WSDDServiceName = name;
    }

    public com.inter.claroChannelUserValWS.stub.EbsEvaluaPedidoSaldoPortType getebsEvaluaPedidoSaldoSB12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ebsEvaluaPedidoSaldoSB12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getebsEvaluaPedidoSaldoSB12(endpoint);
    }

    public com.inter.claroChannelUserValWS.stub.EbsEvaluaPedidoSaldoPortType getebsEvaluaPedidoSaldoSB12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.claroChannelUserValWS.stub.EbsEvaluaPedidoSaldoPortTypeSOAP12BindingStub _stub = new com.inter.claroChannelUserValWS.stub.EbsEvaluaPedidoSaldoPortTypeSOAP12BindingStub(portAddress, this);
            _stub.setPortName(getebsEvaluaPedidoSaldoSB12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setebsEvaluaPedidoSaldoSB12EndpointAddress(java.lang.String address) {
        ebsEvaluaPedidoSaldoSB12_address = address;
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
            if (com.inter.claroChannelUserValWS.stub.EbsEvaluaPedidoSaldoPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.claroChannelUserValWS.stub.EbsEvaluaPedidoSaldoPortTypeSOAP11BindingStub _stub = new com.inter.claroChannelUserValWS.stub.EbsEvaluaPedidoSaldoPortTypeSOAP11BindingStub(new java.net.URL(ebsEvaluaPedidoSaldoSB11_address), this);
                _stub.setPortName(getebsEvaluaPedidoSaldoSB11WSDDServiceName());
                return _stub;
            }
            if (com.inter.claroChannelUserValWS.stub.EbsEvaluaPedidoSaldoPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.claroChannelUserValWS.stub.EbsEvaluaPedidoSaldoPortTypeSOAP12BindingStub _stub = new com.inter.claroChannelUserValWS.stub.EbsEvaluaPedidoSaldoPortTypeSOAP12BindingStub(new java.net.URL(ebsEvaluaPedidoSaldoSB12_address), this);
                _stub.setPortName(getebsEvaluaPedidoSaldoSB12WSDDServiceName());
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
        if ("ebsEvaluaPedidoSaldoSB11".equals(inputPortName)) {
            return getebsEvaluaPedidoSaldoSB11();
        }
        else if ("ebsEvaluaPedidoSaldoSB12".equals(inputPortName)) {
            return getebsEvaluaPedidoSaldoSB12();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/ws/evalua/EvaluaPedidoSaldo/", "ebsEvaluaPedidoSaldo");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/ws/evalua/EvaluaPedidoSaldo/", "ebsEvaluaPedidoSaldoSB11"));
            ports.add(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/ws/evalua/EvaluaPedidoSaldo/", "ebsEvaluaPedidoSaldoSB12"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ebsEvaluaPedidoSaldoSB11".equals(portName)) {
            setebsEvaluaPedidoSaldoSB11EndpointAddress(address);
        }
        else 
if ("ebsEvaluaPedidoSaldoSB12".equals(portName)) {
            setebsEvaluaPedidoSaldoSB12EndpointAddress(address);
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
