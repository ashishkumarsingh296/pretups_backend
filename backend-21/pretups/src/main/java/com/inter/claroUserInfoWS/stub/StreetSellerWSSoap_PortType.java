/**
 * StreetSellerWSSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroUserInfoWS.stub;

public interface StreetSellerWSSoap_PortType extends java.rmi.Remote {
    public com.inter.claroUserInfoWS.stub.FacturaResponseType obtenerFactura(com.inter.claroUserInfoWS.stub.FacturaRequestType facturaRequest) throws java.rmi.RemoteException;
    public com.inter.claroUserInfoWS.stub.DistribuidorDeudaResponseType obtenerDeudaDistribuidor(com.inter.claroUserInfoWS.stub.DistribuidorDeudaRequestType distribuidorDeudaRequest) throws java.rmi.RemoteException;
    public com.inter.claroUserInfoWS.stub.DistribuidorDataResponseType obtenerDatosDistribuidor(com.inter.claroUserInfoWS.stub.DistribuidorDataRequestType distribuidorDataRequest) throws java.rmi.RemoteException;
    public com.inter.claroUserInfoWS.stub.DistribuidorStatusResponseType obtenerEstadoDistribuidor(com.inter.claroUserInfoWS.stub.DistribuidorStatusRequestType distribuidorStatusRequest) throws java.rmi.RemoteException;
    public com.inter.claroUserInfoWS.stub.DistribuidorDataResponseType obtenerDistribuidor(com.inter.claroUserInfoWS.stub.DistribuidorDataRequestType distribuidorDataRequest) throws java.rmi.RemoteException;
}
