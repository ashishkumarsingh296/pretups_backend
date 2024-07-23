/**
 * EbsRecargaVirtualWSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroRechargeWS.stub;

public class EbsRecargaVirtualWSServiceLocator extends org.apache.axis.client.Service implements com.inter.claroRechargeWS.stub.EbsRecargaVirtualWSService {

    public EbsRecargaVirtualWSServiceLocator() {
    }


    public EbsRecargaVirtualWSServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EbsRecargaVirtualWSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for EbsRecargaVirtualWSPortSB11
    private java.lang.String EbsRecargaVirtualWSPortSB11_address = "http://limdeseaiv13.tim.com.pe:7909/RecargaVirtuaWS/EbsRecargaVirtualWSPortSB11";

    public java.lang.String getEbsRecargaVirtualWSPortSB11Address() {
        return EbsRecargaVirtualWSPortSB11_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String EbsRecargaVirtualWSPortSB11WSDDServiceName = "EbsRecargaVirtualWSPortSB11";

    public java.lang.String getEbsRecargaVirtualWSPortSB11WSDDServiceName() {
        return EbsRecargaVirtualWSPortSB11WSDDServiceName;
    }

    public void setEbsRecargaVirtualWSPortSB11WSDDServiceName(java.lang.String name) {
        EbsRecargaVirtualWSPortSB11WSDDServiceName = name;
    }

    public com.inter.claroRechargeWS.stub.EbsRecargaVirtualPortType getEbsRecargaVirtualWSPortSB11() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(EbsRecargaVirtualWSPortSB11_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getEbsRecargaVirtualWSPortSB11(endpoint);
    }

    public com.inter.claroRechargeWS.stub.EbsRecargaVirtualPortType getEbsRecargaVirtualWSPortSB11(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.claroRechargeWS.stub.EbsRecargaVirtualWSPortTypeSOAP11BindingStub _stub = new com.inter.claroRechargeWS.stub.EbsRecargaVirtualWSPortTypeSOAP11BindingStub(portAddress, this);
            _stub.setPortName(getEbsRecargaVirtualWSPortSB11WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setEbsRecargaVirtualWSPortSB11EndpointAddress(java.lang.String address) {
        EbsRecargaVirtualWSPortSB11_address = address;
    }


    // Use to get a proxy class for EbsRecargaVirtualWSPortSB12
    private java.lang.String EbsRecargaVirtualWSPortSB12_address = "http://limdeseaiv13.tim.com.pe:7909/RecargaVirtuaWS/EbsRecargaVirtualWSPortSB12";

    public java.lang.String getEbsRecargaVirtualWSPortSB12Address() {
        return EbsRecargaVirtualWSPortSB12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String EbsRecargaVirtualWSPortSB12WSDDServiceName = "EbsRecargaVirtualWSPortSB12";

    public java.lang.String getEbsRecargaVirtualWSPortSB12WSDDServiceName() {
        return EbsRecargaVirtualWSPortSB12WSDDServiceName;
    }

    public void setEbsRecargaVirtualWSPortSB12WSDDServiceName(java.lang.String name) {
        EbsRecargaVirtualWSPortSB12WSDDServiceName = name;
    }

    public com.inter.claroRechargeWS.stub.EbsRecargaVirtualPortType getEbsRecargaVirtualWSPortSB12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(EbsRecargaVirtualWSPortSB12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getEbsRecargaVirtualWSPortSB12(endpoint);
    }

    public com.inter.claroRechargeWS.stub.EbsRecargaVirtualPortType getEbsRecargaVirtualWSPortSB12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.claroRechargeWS.stub.EbsRecargaVirtualWSPortTypeSOAP12BindingStub _stub = new com.inter.claroRechargeWS.stub.EbsRecargaVirtualWSPortTypeSOAP12BindingStub(portAddress, this);
            _stub.setPortName(getEbsRecargaVirtualWSPortSB12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setEbsRecargaVirtualWSPortSB12EndpointAddress(java.lang.String address) {
        EbsRecargaVirtualWSPortSB12_address = address;
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
            if (com.inter.claroRechargeWS.stub.EbsRecargaVirtualPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.claroRechargeWS.stub.EbsRecargaVirtualWSPortTypeSOAP11BindingStub _stub = new com.inter.claroRechargeWS.stub.EbsRecargaVirtualWSPortTypeSOAP11BindingStub(new java.net.URL(EbsRecargaVirtualWSPortSB11_address), this);
                _stub.setPortName(getEbsRecargaVirtualWSPortSB11WSDDServiceName());
                return _stub;
            }
            if (com.inter.claroRechargeWS.stub.EbsRecargaVirtualPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.claroRechargeWS.stub.EbsRecargaVirtualWSPortTypeSOAP12BindingStub _stub = new com.inter.claroRechargeWS.stub.EbsRecargaVirtualWSPortTypeSOAP12BindingStub(new java.net.URL(EbsRecargaVirtualWSPortSB12_address), this);
                _stub.setPortName(getEbsRecargaVirtualWSPortSB12WSDDServiceName());
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
        if ("EbsRecargaVirtualWSPortSB11".equals(inputPortName)) {
            return getEbsRecargaVirtualWSPortSB11();
        }
        else if ("EbsRecargaVirtualWSPortSB12".equals(inputPortName)) {
            return getEbsRecargaVirtualWSPortSB12();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "EbsRecargaVirtualWSService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "EbsRecargaVirtualWSPortSB11"));
            ports.add(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "EbsRecargaVirtualWSPortSB12"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("EbsRecargaVirtualWSPortSB11".equals(portName)) {
            setEbsRecargaVirtualWSPortSB11EndpointAddress(address);
        }
        else 
if ("EbsRecargaVirtualWSPortSB12".equals(portName)) {
            setEbsRecargaVirtualWSPortSB12EndpointAddress(address);
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
