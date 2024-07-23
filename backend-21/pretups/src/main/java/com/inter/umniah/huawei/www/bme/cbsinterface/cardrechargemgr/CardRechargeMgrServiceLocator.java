/**
 * CardRechargeMgrServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr;

public class CardRechargeMgrServiceLocator extends org.apache.axis.client.Service implements com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.CardRechargeMgrService {

    public CardRechargeMgrServiceLocator() {
    }


    public CardRechargeMgrServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CardRechargeMgrServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for CardRechargeMgrServicePort
    private java.lang.String CardRechargeMgrServicePort_address = "http://www.huawei.com/bme/cbsinterface/cardrecharge/wsdl/CBSInterface_CardRecharge.wsdl";

    public java.lang.String getCardRechargeMgrServicePortAddress() {
        return CardRechargeMgrServicePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CardRechargeMgrServicePortWSDDServiceName = "CardRechargeMgrServicePort";

    public java.lang.String getCardRechargeMgrServicePortWSDDServiceName() {
        return CardRechargeMgrServicePortWSDDServiceName;
    }

    public void setCardRechargeMgrServicePortWSDDServiceName(java.lang.String name) {
        CardRechargeMgrServicePortWSDDServiceName = name;
    }

    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.CardRechargeMgr getCardRechargeMgrServicePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CardRechargeMgrServicePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCardRechargeMgrServicePort(endpoint);
    }

    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.CardRechargeMgr getCardRechargeMgrServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.CardRechargeMgrBindingStub _stub = new com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.CardRechargeMgrBindingStub(portAddress, this);
            _stub.setPortName(getCardRechargeMgrServicePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCardRechargeMgrServicePortEndpointAddress(java.lang.String address) {
        CardRechargeMgrServicePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.CardRechargeMgr.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.CardRechargeMgrBindingStub _stub = new com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr.CardRechargeMgrBindingStub(new java.net.URL(CardRechargeMgrServicePort_address), this);
                _stub.setPortName(getCardRechargeMgrServicePortWSDDServiceName());
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
        if ("CardRechargeMgrServicePort".equals(inputPortName)) {
            return getCardRechargeMgrServicePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", "CardRechargeMgrService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", "CardRechargeMgrServicePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("CardRechargeMgrServicePort".equals(portName)) {
            setCardRechargeMgrServicePortEndpointAddress(address);
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
