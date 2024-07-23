/**
 * PrepayPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.oloapi.stub;

public interface PrepayPortType extends java.rmi.Remote {

    /**
     * Reverse Payment
     */
    public com.inter.oloapi.stub.ReversePaymentResponse reversePayment(com.inter.oloapi.stub.ReversePaymentRequest request) throws java.rmi.RemoteException;

    /**
     * Prepay Recharge
     */
    public com.inter.oloapi.stub.PrepayRechargeResponse prepayRecharge(com.inter.oloapi.stub.PrepayRechargeRequest request) throws java.rmi.RemoteException;

    /**
     * Check Status
     */
    public com.inter.oloapi.stub.CheckStatusResponse checkStatus(com.inter.oloapi.stub.CheckStatusRequest req) throws java.rmi.RemoteException;

    /**
     * Validate Customer
     */
    public com.inter.oloapi.stub.ValidateCustomerResponse validateCustomer(com.inter.oloapi.stub.ValidateCustomerRequest request) throws java.rmi.RemoteException;

    /**
     * Validate Customer PreTups
     */
    public com.inter.oloapi.stub.ValidateCustomerPreTupsResponse validateCustomerPreTups(com.inter.oloapi.stub.ValidateCustomerPreTupsRequest request) throws java.rmi.RemoteException;
}
