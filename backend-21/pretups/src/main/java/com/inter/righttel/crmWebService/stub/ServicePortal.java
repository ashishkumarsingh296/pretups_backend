/**
 * ServicePortal.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.righttel.crmWebService.stub;

public interface ServicePortal extends javax.xml.rpc.Service {

/**
 * OSB Service
 */
    public java.lang.String getwebserviceAddress();

    public com.inter.righttel.crmWebService.stub.ServicePortalPortType getwebservice() throws javax.xml.rpc.ServiceException;

    public com.inter.righttel.crmWebService.stub.ServicePortalPortType getwebservice(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
