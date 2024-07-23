/**
 * EbsRecargaPaqueteDatosServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroDataRechargeWS.stub;

public class EbsRecargaPaqueteDatosServiceLocator extends org.apache.axis.client.Service implements com.inter.claroDataRechargeWS.stub.EbsRecargaPaqueteDatosService {

    public EbsRecargaPaqueteDatosServiceLocator() {
    }


    public EbsRecargaPaqueteDatosServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EbsRecargaPaqueteDatosServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ebsRecargaPaqueteDatosSoapPort11
    private java.lang.String ebsRecargaPaqueteDatosSoapPort11_address = "http://limdeseaiocsv02.tim.com.pe:8909/RecargaPaqueteDatos/ebsRecargaPaqueteDatosSB11";

    public java.lang.String getebsRecargaPaqueteDatosSoapPort11Address() {
        return ebsRecargaPaqueteDatosSoapPort11_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ebsRecargaPaqueteDatosSoapPort11WSDDServiceName = "ebsRecargaPaqueteDatosSoapPort11";

    public java.lang.String getebsRecargaPaqueteDatosSoapPort11WSDDServiceName() {
        return ebsRecargaPaqueteDatosSoapPort11WSDDServiceName;
    }

    public void setebsRecargaPaqueteDatosSoapPort11WSDDServiceName(java.lang.String name) {
        ebsRecargaPaqueteDatosSoapPort11WSDDServiceName = name;
    }

    public com.inter.claroDataRechargeWS.stub.EbsRecargaPaqueteDatos getebsRecargaPaqueteDatosSoapPort11() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ebsRecargaPaqueteDatosSoapPort11_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getebsRecargaPaqueteDatosSoapPort11(endpoint);
    }

    public com.inter.claroDataRechargeWS.stub.EbsRecargaPaqueteDatos getebsRecargaPaqueteDatosSoapPort11(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.claroDataRechargeWS.stub.EbsRecargaPaqueteDatosSOAP11BindingStub _stub = new com.inter.claroDataRechargeWS.stub.EbsRecargaPaqueteDatosSOAP11BindingStub(portAddress, this);
            _stub.setPortName(getebsRecargaPaqueteDatosSoapPort11WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setebsRecargaPaqueteDatosSoapPort11EndpointAddress(java.lang.String address) {
        ebsRecargaPaqueteDatosSoapPort11_address = address;
    }


    // Use to get a proxy class for ebsRecargaPaqueteDatosSoapPort12
    private java.lang.String ebsRecargaPaqueteDatosSoapPort12_address = "http://limdeseaiocsv02.tim.com.pe:8909/RecargaPaqueteDatos/ebsRecargaPaqueteDatosSB12";

    public java.lang.String getebsRecargaPaqueteDatosSoapPort12Address() {
        return ebsRecargaPaqueteDatosSoapPort12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ebsRecargaPaqueteDatosSoapPort12WSDDServiceName = "ebsRecargaPaqueteDatosSoapPort12";

    public java.lang.String getebsRecargaPaqueteDatosSoapPort12WSDDServiceName() {
        return ebsRecargaPaqueteDatosSoapPort12WSDDServiceName;
    }

    public void setebsRecargaPaqueteDatosSoapPort12WSDDServiceName(java.lang.String name) {
        ebsRecargaPaqueteDatosSoapPort12WSDDServiceName = name;
    }

    public com.inter.claroDataRechargeWS.stub.EbsRecargaPaqueteDatos getebsRecargaPaqueteDatosSoapPort12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ebsRecargaPaqueteDatosSoapPort12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getebsRecargaPaqueteDatosSoapPort12(endpoint);
    }

    public com.inter.claroDataRechargeWS.stub.EbsRecargaPaqueteDatos getebsRecargaPaqueteDatosSoapPort12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.claroDataRechargeWS.stub.EbsRecargaPaqueteDatosSOAP12BindingStub _stub = new com.inter.claroDataRechargeWS.stub.EbsRecargaPaqueteDatosSOAP12BindingStub(portAddress, this);
            _stub.setPortName(getebsRecargaPaqueteDatosSoapPort12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setebsRecargaPaqueteDatosSoapPort12EndpointAddress(java.lang.String address) {
        ebsRecargaPaqueteDatosSoapPort12_address = address;
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
            if (com.inter.claroDataRechargeWS.stub.EbsRecargaPaqueteDatos.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.claroDataRechargeWS.stub.EbsRecargaPaqueteDatosSOAP11BindingStub _stub = new com.inter.claroDataRechargeWS.stub.EbsRecargaPaqueteDatosSOAP11BindingStub(new java.net.URL(ebsRecargaPaqueteDatosSoapPort11_address), this);
                _stub.setPortName(getebsRecargaPaqueteDatosSoapPort11WSDDServiceName());
                return _stub;
            }
            if (com.inter.claroDataRechargeWS.stub.EbsRecargaPaqueteDatos.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.claroDataRechargeWS.stub.EbsRecargaPaqueteDatosSOAP12BindingStub _stub = new com.inter.claroDataRechargeWS.stub.EbsRecargaPaqueteDatosSOAP12BindingStub(new java.net.URL(ebsRecargaPaqueteDatosSoapPort12_address), this);
                _stub.setPortName(getebsRecargaPaqueteDatosSoapPort12WSDDServiceName());
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
        if ("ebsRecargaPaqueteDatosSoapPort11".equals(inputPortName)) {
            return getebsRecargaPaqueteDatosSoapPort11();
        }
        else if ("ebsRecargaPaqueteDatosSoapPort12".equals(inputPortName)) {
            return getebsRecargaPaqueteDatosSoapPort12();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", "ebsRecargaPaqueteDatosService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", "ebsRecargaPaqueteDatosSoapPort11"));
            ports.add(new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", "ebsRecargaPaqueteDatosSoapPort12"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ebsRecargaPaqueteDatosSoapPort11".equals(portName)) {
            setebsRecargaPaqueteDatosSoapPort11EndpointAddress(address);
        }
        else 
if ("ebsRecargaPaqueteDatosSoapPort12".equals(portName)) {
            setebsRecargaPaqueteDatosSoapPort12EndpointAddress(address);
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
