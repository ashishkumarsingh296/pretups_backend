/**
 * AlepoSoapService.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Nov 19, 2006 (02:31:34 GMT+00:00) WSDL2Java emitter.
 */

package com.inter.alepoogn.alepoognstub;

public interface AlepoSoapService extends javax.xml.rpc.Service {
    public java.lang.String getAlepoAddress();

    public com.inter.alepoogn.alepoognstub.Radius getAlepo() throws javax.xml.rpc.ServiceException;

    public com.inter.alepoogn.alepoognstub.Radius getAlepo(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
