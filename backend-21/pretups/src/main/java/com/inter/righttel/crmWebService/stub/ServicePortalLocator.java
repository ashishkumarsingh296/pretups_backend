/**
 * ServicePortalLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.righttel.crmWebService.stub;

public class ServicePortalLocator extends org.apache.axis.client.Service implements com.inter.righttel.crmWebService.stub.ServicePortal {

/**
 * OSB Service
 */

    public ServicePortalLocator() {
    }


    public ServicePortalLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ServicePortalLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for webservice
    private java.lang.String webservice_address = "http://172.20.32.7:80/IN/webservices";

    public java.lang.String getwebserviceAddress() {
        return webservice_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String webserviceWSDDServiceName = "webservice";

    public java.lang.String getwebserviceWSDDServiceName() {
        return webserviceWSDDServiceName;
    }

    public void setwebserviceWSDDServiceName(java.lang.String name) {
        webserviceWSDDServiceName = name;
    }

    public com.inter.righttel.crmWebService.stub.ServicePortalPortType getwebservice() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(webservice_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getwebservice(endpoint);
    }

    public com.inter.righttel.crmWebService.stub.ServicePortalPortType getwebservice(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.righttel.crmWebService.stub.ServicePortalBindingStub _stub = new com.inter.righttel.crmWebService.stub.ServicePortalBindingStub(portAddress, this);
            _stub.setPortName(getwebserviceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setwebserviceEndpointAddress(java.lang.String address) {
        webservice_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.inter.righttel.crmWebService.stub.ServicePortalPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.righttel.crmWebService.stub.ServicePortalBindingStub _stub = new com.inter.righttel.crmWebService.stub.ServicePortalBindingStub(new java.net.URL(webservice_address), this);
                _stub.setPortName(getwebserviceWSDDServiceName());
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
        if ("webservice".equals(inputPortName)) {
            return getwebservice();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://osb.rightel.ir", "ServicePortal");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://osb.rightel.ir", "webservice"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("webservice".equals(portName)) {
            setwebserviceEndpointAddress(address);
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
