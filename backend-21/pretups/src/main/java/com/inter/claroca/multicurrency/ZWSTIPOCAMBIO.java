package com.inter.claroca.multicurrency;


import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.6.1
 * 2016-11-17T13:42:42.696+05:30
 * Generated source version: 2.6.1
 * 
 */
@WebService(targetNamespace = "urn:sap-com:document:sap:soap:functions:mc-style", name = "ZWS_TIPO_CAMBIO")
@XmlSeeAlso({ObjectFactory.class})
public interface ZWSTIPOCAMBIO {

    @WebResult(name = "Ukurs", targetNamespace = "")
    @RequestWrapper(localName = "ZtipoDeCambio", targetNamespace = "urn:sap-com:document:sap:soap:functions:mc-style", className = "test.multicurrency.ZtipoDeCambio")
    @WebMethod(operationName = "ZtipoDeCambio")
    @ResponseWrapper(localName = "ZtipoDeCambioResponse", targetNamespace = "urn:sap-com:document:sap:soap:functions:mc-style", className = "test.multicurrency.ZtipoDeCambioResponse")
    public java.math.BigDecimal ztipoDeCambio(
        @WebParam(name = "Fcurr", targetNamespace = "")
        java.lang.String fcurr,
        @WebParam(name = "Gdatu", targetNamespace = "")
        java.lang.String gdatu,
        @WebParam(name = "Kurst", targetNamespace = "")
        java.lang.String kurst,
        @WebParam(name = "Tcurr", targetNamespace = "")
        java.lang.String tcurr
    );
}
