/**
 * IATHUB_pretups.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.iat.client;

public interface IATHUB_pretups extends java.rmi.Remote {
    public com.inter.iat.client.CreateReceivableResult createReceivableRequest(com.inter.iat.client.CreateReceivableParam createReceivableParam);

    public com.inter.iat.client.BurnResult burnRequest(com.inter.iat.client.BurnParam burnParam);

    public com.inter.iat.client.CheckStatusResult checkStatusRequest(com.inter.iat.client.CheckStatusParam checkStatusParam, com.inter.iat.client.AuthHeader authHeader);

    public com.inter.iat.client.RechargeResult rechargeRequest(com.inter.iat.client.RechargeParam rechargeParam, com.inter.iat.client.AuthHeader authHeader);

    public com.inter.iat.client.QuotationResult quotationRequest(com.inter.iat.client.QuotationParam quotationParam);

    public com.inter.iat.client.CatalogResult catalogRequest(com.inter.iat.client.CatalogParam catalogParam);
}
