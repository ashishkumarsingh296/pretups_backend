/**
 * TopupServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.righttel.crmSOAP.stub;

public class TopupServiceLocator extends org.apache.axis.client.Service implements com.inter.righttel.crmSOAP.stub.TopupService {

    public TopupServiceLocator() {
    }


    public TopupServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public TopupServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for TopupServiceHttpsSoap12Endpoint
    private java.lang.String TopupServiceHttpsSoap12Endpoint_address = "https://10.21.7.153:8243/services/TopupService.TopupServiceHttpsSoap12Endpoint";

    public java.lang.String getTopupServiceHttpsSoap12EndpointAddress() {
        return TopupServiceHttpsSoap12Endpoint_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String TopupServiceHttpsSoap12EndpointWSDDServiceName = "TopupServiceHttpsSoap12Endpoint";

    public java.lang.String getTopupServiceHttpsSoap12EndpointWSDDServiceName() {
        return TopupServiceHttpsSoap12EndpointWSDDServiceName;
    }

    public void setTopupServiceHttpsSoap12EndpointWSDDServiceName(java.lang.String name) {
        TopupServiceHttpsSoap12EndpointWSDDServiceName = name;
    }

    public com.inter.righttel.crmSOAP.stub.TopupServicePortType getTopupServiceHttpsSoap12Endpoint() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(TopupServiceHttpsSoap12Endpoint_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getTopupServiceHttpsSoap12Endpoint(endpoint);
    }

    public com.inter.righttel.crmSOAP.stub.TopupServicePortType getTopupServiceHttpsSoap12Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.righttel.crmSOAP.stub.TopupServiceSoap12BindingStub _stub = new com.inter.righttel.crmSOAP.stub.TopupServiceSoap12BindingStub(portAddress, this);
            _stub.setPortName(getTopupServiceHttpsSoap12EndpointWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setTopupServiceHttpsSoap12EndpointEndpointAddress(java.lang.String address) {
        TopupServiceHttpsSoap12Endpoint_address = address;
    }


    // Use to get a proxy class for TopupServiceHttpsSoap11Endpoint
    private java.lang.String TopupServiceHttpsSoap11Endpoint_address = "https://10.21.7.153:8243/services/TopupService.TopupServiceHttpsSoap11Endpoint";

    public java.lang.String getTopupServiceHttpsSoap11EndpointAddress() {
        return TopupServiceHttpsSoap11Endpoint_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String TopupServiceHttpsSoap11EndpointWSDDServiceName = "TopupServiceHttpsSoap11Endpoint";

    public java.lang.String getTopupServiceHttpsSoap11EndpointWSDDServiceName() {
        return TopupServiceHttpsSoap11EndpointWSDDServiceName;
    }

    public void setTopupServiceHttpsSoap11EndpointWSDDServiceName(java.lang.String name) {
        TopupServiceHttpsSoap11EndpointWSDDServiceName = name;
    }

    public com.inter.righttel.crmSOAP.stub.TopupServicePortType getTopupServiceHttpsSoap11Endpoint() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(TopupServiceHttpsSoap11Endpoint_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getTopupServiceHttpsSoap11Endpoint(endpoint);
    }

    public com.inter.righttel.crmSOAP.stub.TopupServicePortType getTopupServiceHttpsSoap11Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.righttel.crmSOAP.stub.TopupServiceSoap11BindingStub _stub = new com.inter.righttel.crmSOAP.stub.TopupServiceSoap11BindingStub(portAddress, this);
            _stub.setPortName(getTopupServiceHttpsSoap11EndpointWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setTopupServiceHttpsSoap11EndpointEndpointAddress(java.lang.String address) {
        TopupServiceHttpsSoap11Endpoint_address = address;
    }


    // Use to get a proxy class for TopupServiceHttpSoap12Endpoint
    private java.lang.String TopupServiceHttpSoap12Endpoint_address = "http://10.21.7.153:8080/services/TopupService.TopupServiceHttpSoap12Endpoint";

    public java.lang.String getTopupServiceHttpSoap12EndpointAddress() {
        return TopupServiceHttpSoap12Endpoint_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String TopupServiceHttpSoap12EndpointWSDDServiceName = "TopupServiceHttpSoap12Endpoint";

    public java.lang.String getTopupServiceHttpSoap12EndpointWSDDServiceName() {
        return TopupServiceHttpSoap12EndpointWSDDServiceName;
    }

    public void setTopupServiceHttpSoap12EndpointWSDDServiceName(java.lang.String name) {
        TopupServiceHttpSoap12EndpointWSDDServiceName = name;
    }

    public com.inter.righttel.crmSOAP.stub.TopupServicePortType getTopupServiceHttpSoap12Endpoint() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(TopupServiceHttpSoap12Endpoint_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getTopupServiceHttpSoap12Endpoint(endpoint);
    }

    public com.inter.righttel.crmSOAP.stub.TopupServicePortType getTopupServiceHttpSoap12Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.righttel.crmSOAP.stub.TopupServiceSoap12BindingStub _stub = new com.inter.righttel.crmSOAP.stub.TopupServiceSoap12BindingStub(portAddress, this);
            _stub.setPortName(getTopupServiceHttpSoap12EndpointWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setTopupServiceHttpSoap12EndpointEndpointAddress(java.lang.String address) {
        TopupServiceHttpSoap12Endpoint_address = address;
    }


    // Use to get a proxy class for TopupServiceHttpSoap11Endpoint
    private java.lang.String TopupServiceHttpSoap11Endpoint_address = "http://10.21.7.153:8080/services/TopupService.TopupServiceHttpSoap11Endpoint";

    public java.lang.String getTopupServiceHttpSoap11EndpointAddress() {
        return TopupServiceHttpSoap11Endpoint_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String TopupServiceHttpSoap11EndpointWSDDServiceName = "TopupServiceHttpSoap11Endpoint";

    public java.lang.String getTopupServiceHttpSoap11EndpointWSDDServiceName() {
        return TopupServiceHttpSoap11EndpointWSDDServiceName;
    }

    public void setTopupServiceHttpSoap11EndpointWSDDServiceName(java.lang.String name) {
        TopupServiceHttpSoap11EndpointWSDDServiceName = name;
    }

    public com.inter.righttel.crmSOAP.stub.TopupServicePortType getTopupServiceHttpSoap11Endpoint() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(TopupServiceHttpSoap11Endpoint_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getTopupServiceHttpSoap11Endpoint(endpoint);
    }

    public com.inter.righttel.crmSOAP.stub.TopupServicePortType getTopupServiceHttpSoap11Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.righttel.crmSOAP.stub.TopupServiceSoap11BindingStub _stub = new com.inter.righttel.crmSOAP.stub.TopupServiceSoap11BindingStub(portAddress, this);
            _stub.setPortName(getTopupServiceHttpSoap11EndpointWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setTopupServiceHttpSoap11EndpointEndpointAddress(java.lang.String address) {
        TopupServiceHttpSoap11Endpoint_address = address;
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
            if (com.inter.righttel.crmSOAP.stub.TopupServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.righttel.crmSOAP.stub.TopupServiceSoap12BindingStub _stub = new com.inter.righttel.crmSOAP.stub.TopupServiceSoap12BindingStub(new java.net.URL(TopupServiceHttpsSoap12Endpoint_address), this);
                _stub.setPortName(getTopupServiceHttpsSoap12EndpointWSDDServiceName());
                return _stub;
            }
            if (com.inter.righttel.crmSOAP.stub.TopupServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.righttel.crmSOAP.stub.TopupServiceSoap11BindingStub _stub = new com.inter.righttel.crmSOAP.stub.TopupServiceSoap11BindingStub(new java.net.URL(TopupServiceHttpsSoap11Endpoint_address), this);
                _stub.setPortName(getTopupServiceHttpsSoap11EndpointWSDDServiceName());
                return _stub;
            }
            if (com.inter.righttel.crmSOAP.stub.TopupServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.righttel.crmSOAP.stub.TopupServiceSoap12BindingStub _stub = new com.inter.righttel.crmSOAP.stub.TopupServiceSoap12BindingStub(new java.net.URL(TopupServiceHttpSoap12Endpoint_address), this);
                _stub.setPortName(getTopupServiceHttpSoap12EndpointWSDDServiceName());
                return _stub;
            }
            if (com.inter.righttel.crmSOAP.stub.TopupServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.righttel.crmSOAP.stub.TopupServiceSoap11BindingStub _stub = new com.inter.righttel.crmSOAP.stub.TopupServiceSoap11BindingStub(new java.net.URL(TopupServiceHttpSoap11Endpoint_address), this);
                _stub.setPortName(getTopupServiceHttpSoap11EndpointWSDDServiceName());
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
        if ("TopupServiceHttpsSoap12Endpoint".equals(inputPortName)) {
            return getTopupServiceHttpsSoap12Endpoint();
        }
        else if ("TopupServiceHttpsSoap11Endpoint".equals(inputPortName)) {
            return getTopupServiceHttpsSoap11Endpoint();
        }
        else if ("TopupServiceHttpSoap12Endpoint".equals(inputPortName)) {
            return getTopupServiceHttpSoap12Endpoint();
        }
        else if ("TopupServiceHttpSoap11Endpoint".equals(inputPortName)) {
            return getTopupServiceHttpSoap11Endpoint();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "TopupService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "TopupServiceHttpsSoap12Endpoint"));
            ports.add(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "TopupServiceHttpsSoap11Endpoint"));
            ports.add(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "TopupServiceHttpSoap12Endpoint"));
            ports.add(new javax.xml.namespace.QName("http://www.inew-cs.com/mvno/integration/TopupService/", "TopupServiceHttpSoap11Endpoint"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("TopupServiceHttpsSoap12Endpoint".equals(portName)) {
            setTopupServiceHttpsSoap12EndpointEndpointAddress(address);
        }
        else 
if ("TopupServiceHttpsSoap11Endpoint".equals(portName)) {
            setTopupServiceHttpsSoap11EndpointEndpointAddress(address);
        }
        else 
if ("TopupServiceHttpSoap12Endpoint".equals(portName)) {
            setTopupServiceHttpSoap12EndpointEndpointAddress(address);
        }
        else 
if ("TopupServiceHttpSoap11Endpoint".equals(portName)) {
            setTopupServiceHttpSoap11EndpointEndpointAddress(address);
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
