/**
 * ConsultaPagos_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroCollectionEnqWS.stub;

public class ConsultaPagos_ServiceLocator extends org.apache.axis.client.Service implements com.inter.claroCollectionEnqWS.stub.ConsultaPagos_Service {

    public ConsultaPagos_ServiceLocator() {
    }


    public ConsultaPagos_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ConsultaPagos_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ConsultaPagosSOAP
    private java.lang.String ConsultaPagosSOAP_address = "http://172.19.74.141:8901/OAC_Services/Inquiry/ConsultaPagos";

    public java.lang.String getConsultaPagosSOAPAddress() {
        return ConsultaPagosSOAP_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ConsultaPagosSOAPWSDDServiceName = "ConsultaPagosSOAP";

    public java.lang.String getConsultaPagosSOAPWSDDServiceName() {
        return ConsultaPagosSOAPWSDDServiceName;
    }

    public void setConsultaPagosSOAPWSDDServiceName(java.lang.String name) {
        ConsultaPagosSOAPWSDDServiceName = name;
    }

    public com.inter.claroCollectionEnqWS.stub.ConsultaPagos_PortType getConsultaPagosSOAP() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ConsultaPagosSOAP_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getConsultaPagosSOAP(endpoint);
    }

    public com.inter.claroCollectionEnqWS.stub.ConsultaPagos_PortType getConsultaPagosSOAP(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.claroCollectionEnqWS.stub.ConsultaPagosSOAPStub _stub = new com.inter.claroCollectionEnqWS.stub.ConsultaPagosSOAPStub(portAddress, this);
            _stub.setPortName(getConsultaPagosSOAPWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setConsultaPagosSOAPEndpointAddress(java.lang.String address) {
        ConsultaPagosSOAP_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.inter.claroCollectionEnqWS.stub.ConsultaPagos_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.claroCollectionEnqWS.stub.ConsultaPagosSOAPStub _stub = new com.inter.claroCollectionEnqWS.stub.ConsultaPagosSOAPStub(new java.net.URL(ConsultaPagosSOAP_address), this);
                _stub.setPortName(getConsultaPagosSOAPWSDDServiceName());
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
        if ("ConsultaPagosSOAP".equals(inputPortName)) {
            return getConsultaPagosSOAP();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/consultapagos/", "ConsultaPagos");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/consultapagos/", "ConsultaPagosSOAP"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ConsultaPagosSOAP".equals(portName)) {
            setConsultaPagosSOAPEndpointAddress(address);
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
