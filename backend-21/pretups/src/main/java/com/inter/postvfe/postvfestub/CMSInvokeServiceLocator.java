/**
 * CMSInvokeServiceLocator.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC1 Sep 29, 2004 (08:29:40 EDT) WSDL2Java emitter.
 */

package com.inter.postvfe.postvfestub;

public class CMSInvokeServiceLocator extends org.apache.axis.client.Service implements com.inter.postvfe.postvfestub.CMSInvokeService {

    // Use to get a proxy class for CMSInvoke
    private java.lang.String CMSInvoke_address = "http://localhost:8084/CMSWebService/CMSInvokeService";

    public java.lang.String getCMSInvokeAddress() {
        return CMSInvoke_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CMSInvokeWSDDServiceName = "CMSInvoke";

    public java.lang.String getCMSInvokeWSDDServiceName() {
        return CMSInvokeWSDDServiceName;
    }

    public void setCMSInvokeWSDDServiceName(java.lang.String name) {
        CMSInvokeWSDDServiceName = name;
    }

    public com.inter.postvfe.postvfestub.CMSInvoke getCMSInvoke() throws javax.xml.rpc.ServiceException {
        java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CMSInvoke_address);
        } catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCMSInvoke(endpoint);
    }

    public com.inter.postvfe.postvfestub.CMSInvoke getCMSInvoke(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.inter.postvfe.postvfestub.CMSInvokeSoapBindingStub _stub = new com.inter.postvfe.postvfestub.CMSInvokeSoapBindingStub(portAddress, this);
            _stub.setPortName(getCMSInvokeWSDDServiceName());
            return _stub;
        } catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCMSInvokeEndpointAddress(java.lang.String address) {
        CMSInvoke_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.inter.postvfe.postvfestub.CMSInvoke.class.isAssignableFrom(serviceEndpointInterface)) {
                com.inter.postvfe.postvfestub.CMSInvokeSoapBindingStub _stub = new com.inter.postvfe.postvfestub.CMSInvokeSoapBindingStub(new java.net.URL(CMSInvoke_address), this);
                _stub.setPortName(getCMSInvokeWSDDServiceName());
                return _stub;
            }
        } catch (java.lang.Throwable t) {
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
        if ("CMSInvoke".equals(inputPortName)) {
            return getCMSInvoke();
        } else {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://cmsadapter.asset.com/interfaces/webservice/", "CMSInvokeService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://cmsadapter.asset.com/interfaces/webservice/", "CMSInvoke"));
        }
        return ports.iterator();
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        if ("CMSInvoke".equals(portName)) {
            setCMSInvokeEndpointAddress(address);
        } else { // Unknown Port Name
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
