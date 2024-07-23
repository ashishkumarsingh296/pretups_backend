/**
 * PayBill.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.billpayment.stub;

public interface PayBill extends java.rmi.Remote {
    public java.lang.String payBill(java.lang.String infoXML) throws java.rmi.RemoteException;

    public java.lang.String rollBackPayment(java.lang.String infoXML) throws java.rmi.RemoteException;

    public java.lang.String retryPayment(java.lang.String infoXML) throws java.rmi.RemoteException;

    public java.lang.String depositBill(java.lang.String infoXML) throws java.rmi.RemoteException;

    public java.lang.String debitAdjustment(java.lang.String infoXML) throws java.rmi.RemoteException;

    public java.lang.String creditAdjustment(java.lang.String infoXML) throws java.rmi.RemoteException;

    public java.lang.String subInfo(java.lang.String infoXML) throws java.rmi.RemoteException;
}
