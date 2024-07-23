/**
 * TopupServicePortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.righttel.crmSOAP.stub;

public interface TopupServicePortType extends java.rmi.Remote {
    public com.inter.righttel.crmSOAP.stub.QueryTopupResponseType queryTopup(com.inter.righttel.crmSOAP.stub.QueryTopupRequestType parameters1) throws java.rmi.RemoteException;
    public com.inter.righttel.crmSOAP.stub.TopupResponseType topup(com.inter.righttel.crmSOAP.stub.TopupRequestType parameters1) throws java.rmi.RemoteException;
    public com.inter.righttel.crmSOAP.stub.InitTopupResponseType initTopup(com.inter.righttel.crmSOAP.stub.InitTopupRequestType parameters1) throws java.rmi.RemoteException;
    public com.inter.righttel.crmSOAP.stub.CancelTopupResponseType cancelTopup(com.inter.righttel.crmSOAP.stub.CancelTopupRequestType parameters1) throws java.rmi.RemoteException;
    public com.inter.righttel.crmSOAP.stub.ValidateTopupResponseType validateTopup(com.inter.righttel.crmSOAP.stub.ValidateTopupRequestType parameters1) throws java.rmi.RemoteException;
}
