/**
 * Radius.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Nov 19, 2006 (02:31:34 GMT+00:00) WSDL2Java emitter.
 */

package com.inter.alepoogn.alepoognstub;

public interface Radius extends java.rmi.Remote {
    public com.inter.alepoogn.alepoognstub.SoapResponseData soapRequest(java.lang.String pageName, com.inter.alepoogn.alepoognstub.DataItem[] keyvaluepair) throws java.rmi.RemoteException;

    public com.inter.alepoogn.alepoognstub.GetKeyNamePairResponseData getKeyNamePairRequest(java.lang.String selectquery) throws java.rmi.RemoteException;
}
