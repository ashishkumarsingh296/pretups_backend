/**
 * EbsTransaccionDTHPrepago_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroDTHRechargeWS.stub;

public class EbsTransaccionDTHPrepago_ServiceLocator extends org.apache.axis.client.Service implements com.inter.claroDTHRechargeWS.stub.EbsTransaccionDTHPrepago_Service {

    public EbsTransaccionDTHPrepago_ServiceLocator() {
    }


    public EbsTransaccionDTHPrepago_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EbsTransaccionDTHPrepago_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ebsTransaccionDTHPrepago
    private java.lang.String ebsTransaccionDTHPrepago_address = "http://172.19.74.68:8909/RecargaDTHWS/ebsTransaccionDTHPrepago";

    public java.lang.String getebsTransaccionDTHPrepagoAddress() {
        return ebsTransaccionDTHPrepago_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ebsTransaccionDTHPrepagoWSDDServiceName = "ebsTransaccionDTHPrepago";

    public java.lang.String getebsTransaccionDTHPrepagoWSDDServiceName() {
        return ebsTransaccionDTHPrepagoWSDDServiceName;
    }

    public void setebsTransaccionDTHPrepagoWSDDServiceName(java.lang.String name) {
        ebsTransaccionDTHPrepagoWSDDServiceName = name;
    }

    public com.inter.claroDTHRechargeWS.stub.TransaccionDTHPrepagoPortType getebsTransaccionDTHPrepago() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ebsTransaccionDTHPrepago_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getebsTransaccionDTHPrepago(endpoint);
    }

    public com.inter.claroDTHRechargeWS.stub.TransaccionDTHPrepagoPortType getebsTransaccionDTHPrepago(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.claroDTHRechargeWS.stub.TransaccionDTHPrepagoPortTypeSOAP11BindingStub _stub = new com.inter.claroDTHRechargeWS.stub.TransaccionDTHPrepagoPortTypeSOAP11BindingStub(portAddress, this);
            _stub.setPortName(getebsTransaccionDTHPrepagoWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setebsTransaccionDTHPrepagoEndpointAddress(java.lang.String address) {
        ebsTransaccionDTHPrepago_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.inter.claroDTHRechargeWS.stub.TransaccionDTHPrepagoPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.claroDTHRechargeWS.stub.TransaccionDTHPrepagoPortTypeSOAP11BindingStub _stub = new com.inter.claroDTHRechargeWS.stub.TransaccionDTHPrepagoPortTypeSOAP11BindingStub(new java.net.URL(ebsTransaccionDTHPrepago_address), this);
                _stub.setPortName(getebsTransaccionDTHPrepagoWSDDServiceName());
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
        if ("ebsTransaccionDTHPrepago".equals(inputPortName)) {
            return getebsTransaccionDTHPrepago();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://claro.com.pe/eai/ws/ventas/transacciondthprepago", "ebsTransaccionDTHPrepago");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://claro.com.pe/eai/ws/ventas/transacciondthprepago", "ebsTransaccionDTHPrepago"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ebsTransaccionDTHPrepago".equals(portName)) {
            setebsTransaccionDTHPrepagoEndpointAddress(address);
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
