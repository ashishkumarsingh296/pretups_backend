/**
 * CrearPago.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroCollPayWS.stub;

public class CrearPago  implements java.io.Serializable {
    private java.lang.String txId;

    private java.lang.String pCodAplicacion;

    private java.lang.String pExtorno;

    private java.lang.String pCodBanco;

    private java.lang.String pCodReenvia;

    private java.lang.String pCodMoneda;

    private java.lang.String pTipoIdentific;

    private java.lang.String pDatoIdentific;

    private java.util.Calendar pFechaHora;

    private java.lang.String pTrace;

    private java.lang.String pNroOperacion;

    private java.lang.String pNombreComercio;

    private java.lang.String pNroComercio;

    private java.lang.String pCodAgencia;

    private java.lang.String pCodCanal;

    private java.lang.String pCodCiudad;

    private java.lang.String pNroTerminal;

    private java.lang.String pPlaza;

    private java.lang.String pMedioPago;

    private java.lang.String pNroReferencia;

    private java.lang.String pNroCheque1;

    private java.lang.String pNroCheque2;

    private java.lang.String pNroCheque3;

    private java.lang.String pPlazaBcoCheque1;

    private java.lang.String pPlazaBcoCheque2;

    private java.lang.String pPlazaBcoCheque3;

    private java.lang.String pBcoGiradCheque1;

    private java.lang.String pBcoGiradCheque2;

    private java.lang.String pBcoGiradCheque3;

    private java.math.BigDecimal pPagoEfectivo;

    private java.math.BigDecimal pPagoTotal;

    private java.lang.String pDatoTransaccion;

    /* Estructura XML con los servicios y documentos que van a ser
     * cancelados. Ver ComplexType="CrearPagoDetServiciosReqType" */
    private com.inter.claroCollPayWS.stub.CrearPagoDetServicioReqType[] pDetDocumentos;

    public CrearPago() {
    }

    public CrearPago(
           java.lang.String txId,
           java.lang.String pCodAplicacion,
           java.lang.String pExtorno,
           java.lang.String pCodBanco,
           java.lang.String pCodReenvia,
           java.lang.String pCodMoneda,
           java.lang.String pTipoIdentific,
           java.lang.String pDatoIdentific,
           java.util.Calendar pFechaHora,
           java.lang.String pTrace,
           java.lang.String pNroOperacion,
           java.lang.String pNombreComercio,
           java.lang.String pNroComercio,
           java.lang.String pCodAgencia,
           java.lang.String pCodCanal,
           java.lang.String pCodCiudad,
           java.lang.String pNroTerminal,
           java.lang.String pPlaza,
           java.lang.String pMedioPago,
           java.lang.String pNroReferencia,
           java.lang.String pNroCheque1,
           java.lang.String pNroCheque2,
           java.lang.String pNroCheque3,
           java.lang.String pPlazaBcoCheque1,
           java.lang.String pPlazaBcoCheque2,
           java.lang.String pPlazaBcoCheque3,
           java.lang.String pBcoGiradCheque1,
           java.lang.String pBcoGiradCheque2,
           java.lang.String pBcoGiradCheque3,
           java.math.BigDecimal pPagoEfectivo,
           java.math.BigDecimal pPagoTotal,
           java.lang.String pDatoTransaccion,
           com.inter.claroCollPayWS.stub.CrearPagoDetServicioReqType[] pDetDocumentos) {
           this.txId = txId;
           this.pCodAplicacion = pCodAplicacion;
           this.pExtorno = pExtorno;
           this.pCodBanco = pCodBanco;
           this.pCodReenvia = pCodReenvia;
           this.pCodMoneda = pCodMoneda;
           this.pTipoIdentific = pTipoIdentific;
           this.pDatoIdentific = pDatoIdentific;
           this.pFechaHora = pFechaHora;
           this.pTrace = pTrace;
           this.pNroOperacion = pNroOperacion;
           this.pNombreComercio = pNombreComercio;
           this.pNroComercio = pNroComercio;
           this.pCodAgencia = pCodAgencia;
           this.pCodCanal = pCodCanal;
           this.pCodCiudad = pCodCiudad;
           this.pNroTerminal = pNroTerminal;
           this.pPlaza = pPlaza;
           this.pMedioPago = pMedioPago;
           this.pNroReferencia = pNroReferencia;
           this.pNroCheque1 = pNroCheque1;
           this.pNroCheque2 = pNroCheque2;
           this.pNroCheque3 = pNroCheque3;
           this.pPlazaBcoCheque1 = pPlazaBcoCheque1;
           this.pPlazaBcoCheque2 = pPlazaBcoCheque2;
           this.pPlazaBcoCheque3 = pPlazaBcoCheque3;
           this.pBcoGiradCheque1 = pBcoGiradCheque1;
           this.pBcoGiradCheque2 = pBcoGiradCheque2;
           this.pBcoGiradCheque3 = pBcoGiradCheque3;
           this.pPagoEfectivo = pPagoEfectivo;
           this.pPagoTotal = pPagoTotal;
           this.pDatoTransaccion = pDatoTransaccion;
           this.pDetDocumentos = pDetDocumentos;
    }


    /**
     * Gets the txId value for this CrearPago.
     * 
     * @return txId
     */
    public java.lang.String getTxId() {
        return txId;
    }


    /**
     * Sets the txId value for this CrearPago.
     * 
     * @param txId
     */
    public void setTxId(java.lang.String txId) {
        this.txId = txId;
    }


    /**
     * Gets the pCodAplicacion value for this CrearPago.
     * 
     * @return pCodAplicacion
     */
    public java.lang.String getpCodAplicacion() {
        return pCodAplicacion;
    }


    /**
     * Sets the pCodAplicacion value for this CrearPago.
     * 
     * @param pCodAplicacion
     */
    public void setpCodAplicacion(java.lang.String pCodAplicacion) {
        this.pCodAplicacion = pCodAplicacion;
    }


    /**
     * Gets the pExtorno value for this CrearPago.
     * 
     * @return pExtorno
     */
    public java.lang.String getpExtorno() {
        return pExtorno;
    }


    /**
     * Sets the pExtorno value for this CrearPago.
     * 
     * @param pExtorno
     */
    public void setpExtorno(java.lang.String pExtorno) {
        this.pExtorno = pExtorno;
    }


    /**
     * Gets the pCodBanco value for this CrearPago.
     * 
     * @return pCodBanco
     */
    public java.lang.String getpCodBanco() {
        return pCodBanco;
    }


    /**
     * Sets the pCodBanco value for this CrearPago.
     * 
     * @param pCodBanco
     */
    public void setpCodBanco(java.lang.String pCodBanco) {
        this.pCodBanco = pCodBanco;
    }


    /**
     * Gets the pCodReenvia value for this CrearPago.
     * 
     * @return pCodReenvia
     */
    public java.lang.String getpCodReenvia() {
        return pCodReenvia;
    }


    /**
     * Sets the pCodReenvia value for this CrearPago.
     * 
     * @param pCodReenvia
     */
    public void setpCodReenvia(java.lang.String pCodReenvia) {
        this.pCodReenvia = pCodReenvia;
    }


    /**
     * Gets the pCodMoneda value for this CrearPago.
     * 
     * @return pCodMoneda
     */
    public java.lang.String getpCodMoneda() {
        return pCodMoneda;
    }


    /**
     * Sets the pCodMoneda value for this CrearPago.
     * 
     * @param pCodMoneda
     */
    public void setpCodMoneda(java.lang.String pCodMoneda) {
        this.pCodMoneda = pCodMoneda;
    }


    /**
     * Gets the pTipoIdentific value for this CrearPago.
     * 
     * @return pTipoIdentific
     */
    public java.lang.String getpTipoIdentific() {
        return pTipoIdentific;
    }


    /**
     * Sets the pTipoIdentific value for this CrearPago.
     * 
     * @param pTipoIdentific
     */
    public void setpTipoIdentific(java.lang.String pTipoIdentific) {
        this.pTipoIdentific = pTipoIdentific;
    }


    /**
     * Gets the pDatoIdentific value for this CrearPago.
     * 
     * @return pDatoIdentific
     */
    public java.lang.String getpDatoIdentific() {
        return pDatoIdentific;
    }


    /**
     * Sets the pDatoIdentific value for this CrearPago.
     * 
     * @param pDatoIdentific
     */
    public void setpDatoIdentific(java.lang.String pDatoIdentific) {
        this.pDatoIdentific = pDatoIdentific;
    }


    /**
     * Gets the pFechaHora value for this CrearPago.
     * 
     * @return pFechaHora
     */
    public java.util.Calendar getpFechaHora() {
        return pFechaHora;
    }


    /**
     * Sets the pFechaHora value for this CrearPago.
     * 
     * @param pFechaHora
     */
    public void setpFechaHora(java.util.Calendar pFechaHora) {
        this.pFechaHora = pFechaHora;
    }


    /**
     * Gets the pTrace value for this CrearPago.
     * 
     * @return pTrace
     */
    public java.lang.String getpTrace() {
        return pTrace;
    }


    /**
     * Sets the pTrace value for this CrearPago.
     * 
     * @param pTrace
     */
    public void setpTrace(java.lang.String pTrace) {
        this.pTrace = pTrace;
    }


    /**
     * Gets the pNroOperacion value for this CrearPago.
     * 
     * @return pNroOperacion
     */
    public java.lang.String getpNroOperacion() {
        return pNroOperacion;
    }


    /**
     * Sets the pNroOperacion value for this CrearPago.
     * 
     * @param pNroOperacion
     */
    public void setpNroOperacion(java.lang.String pNroOperacion) {
        this.pNroOperacion = pNroOperacion;
    }


    /**
     * Gets the pNombreComercio value for this CrearPago.
     * 
     * @return pNombreComercio
     */
    public java.lang.String getpNombreComercio() {
        return pNombreComercio;
    }


    /**
     * Sets the pNombreComercio value for this CrearPago.
     * 
     * @param pNombreComercio
     */
    public void setpNombreComercio(java.lang.String pNombreComercio) {
        this.pNombreComercio = pNombreComercio;
    }


    /**
     * Gets the pNroComercio value for this CrearPago.
     * 
     * @return pNroComercio
     */
    public java.lang.String getpNroComercio() {
        return pNroComercio;
    }


    /**
     * Sets the pNroComercio value for this CrearPago.
     * 
     * @param pNroComercio
     */
    public void setpNroComercio(java.lang.String pNroComercio) {
        this.pNroComercio = pNroComercio;
    }


    /**
     * Gets the pCodAgencia value for this CrearPago.
     * 
     * @return pCodAgencia
     */
    public java.lang.String getpCodAgencia() {
        return pCodAgencia;
    }


    /**
     * Sets the pCodAgencia value for this CrearPago.
     * 
     * @param pCodAgencia
     */
    public void setpCodAgencia(java.lang.String pCodAgencia) {
        this.pCodAgencia = pCodAgencia;
    }


    /**
     * Gets the pCodCanal value for this CrearPago.
     * 
     * @return pCodCanal
     */
    public java.lang.String getpCodCanal() {
        return pCodCanal;
    }


    /**
     * Sets the pCodCanal value for this CrearPago.
     * 
     * @param pCodCanal
     */
    public void setpCodCanal(java.lang.String pCodCanal) {
        this.pCodCanal = pCodCanal;
    }


    /**
     * Gets the pCodCiudad value for this CrearPago.
     * 
     * @return pCodCiudad
     */
    public java.lang.String getpCodCiudad() {
        return pCodCiudad;
    }


    /**
     * Sets the pCodCiudad value for this CrearPago.
     * 
     * @param pCodCiudad
     */
    public void setpCodCiudad(java.lang.String pCodCiudad) {
        this.pCodCiudad = pCodCiudad;
    }


    /**
     * Gets the pNroTerminal value for this CrearPago.
     * 
     * @return pNroTerminal
     */
    public java.lang.String getpNroTerminal() {
        return pNroTerminal;
    }


    /**
     * Sets the pNroTerminal value for this CrearPago.
     * 
     * @param pNroTerminal
     */
    public void setpNroTerminal(java.lang.String pNroTerminal) {
        this.pNroTerminal = pNroTerminal;
    }


    /**
     * Gets the pPlaza value for this CrearPago.
     * 
     * @return pPlaza
     */
    public java.lang.String getpPlaza() {
        return pPlaza;
    }


    /**
     * Sets the pPlaza value for this CrearPago.
     * 
     * @param pPlaza
     */
    public void setpPlaza(java.lang.String pPlaza) {
        this.pPlaza = pPlaza;
    }


    /**
     * Gets the pMedioPago value for this CrearPago.
     * 
     * @return pMedioPago
     */
    public java.lang.String getpMedioPago() {
        return pMedioPago;
    }


    /**
     * Sets the pMedioPago value for this CrearPago.
     * 
     * @param pMedioPago
     */
    public void setpMedioPago(java.lang.String pMedioPago) {
        this.pMedioPago = pMedioPago;
    }


    /**
     * Gets the pNroReferencia value for this CrearPago.
     * 
     * @return pNroReferencia
     */
    public java.lang.String getpNroReferencia() {
        return pNroReferencia;
    }


    /**
     * Sets the pNroReferencia value for this CrearPago.
     * 
     * @param pNroReferencia
     */
    public void setpNroReferencia(java.lang.String pNroReferencia) {
        this.pNroReferencia = pNroReferencia;
    }


    /**
     * Gets the pNroCheque1 value for this CrearPago.
     * 
     * @return pNroCheque1
     */
    public java.lang.String getpNroCheque1() {
        return pNroCheque1;
    }


    /**
     * Sets the pNroCheque1 value for this CrearPago.
     * 
     * @param pNroCheque1
     */
    public void setpNroCheque1(java.lang.String pNroCheque1) {
        this.pNroCheque1 = pNroCheque1;
    }


    /**
     * Gets the pNroCheque2 value for this CrearPago.
     * 
     * @return pNroCheque2
     */
    public java.lang.String getpNroCheque2() {
        return pNroCheque2;
    }


    /**
     * Sets the pNroCheque2 value for this CrearPago.
     * 
     * @param pNroCheque2
     */
    public void setpNroCheque2(java.lang.String pNroCheque2) {
        this.pNroCheque2 = pNroCheque2;
    }


    /**
     * Gets the pNroCheque3 value for this CrearPago.
     * 
     * @return pNroCheque3
     */
    public java.lang.String getpNroCheque3() {
        return pNroCheque3;
    }


    /**
     * Sets the pNroCheque3 value for this CrearPago.
     * 
     * @param pNroCheque3
     */
    public void setpNroCheque3(java.lang.String pNroCheque3) {
        this.pNroCheque3 = pNroCheque3;
    }


    /**
     * Gets the pPlazaBcoCheque1 value for this CrearPago.
     * 
     * @return pPlazaBcoCheque1
     */
    public java.lang.String getpPlazaBcoCheque1() {
        return pPlazaBcoCheque1;
    }


    /**
     * Sets the pPlazaBcoCheque1 value for this CrearPago.
     * 
     * @param pPlazaBcoCheque1
     */
    public void setpPlazaBcoCheque1(java.lang.String pPlazaBcoCheque1) {
        this.pPlazaBcoCheque1 = pPlazaBcoCheque1;
    }


    /**
     * Gets the pPlazaBcoCheque2 value for this CrearPago.
     * 
     * @return pPlazaBcoCheque2
     */
    public java.lang.String getpPlazaBcoCheque2() {
        return pPlazaBcoCheque2;
    }


    /**
     * Sets the pPlazaBcoCheque2 value for this CrearPago.
     * 
     * @param pPlazaBcoCheque2
     */
    public void setpPlazaBcoCheque2(java.lang.String pPlazaBcoCheque2) {
        this.pPlazaBcoCheque2 = pPlazaBcoCheque2;
    }


    /**
     * Gets the pPlazaBcoCheque3 value for this CrearPago.
     * 
     * @return pPlazaBcoCheque3
     */
    public java.lang.String getpPlazaBcoCheque3() {
        return pPlazaBcoCheque3;
    }


    /**
     * Sets the pPlazaBcoCheque3 value for this CrearPago.
     * 
     * @param pPlazaBcoCheque3
     */
    public void setpPlazaBcoCheque3(java.lang.String pPlazaBcoCheque3) {
        this.pPlazaBcoCheque3 = pPlazaBcoCheque3;
    }


    /**
     * Gets the pBcoGiradCheque1 value for this CrearPago.
     * 
     * @return pBcoGiradCheque1
     */
    public java.lang.String getpBcoGiradCheque1() {
        return pBcoGiradCheque1;
    }


    /**
     * Sets the pBcoGiradCheque1 value for this CrearPago.
     * 
     * @param pBcoGiradCheque1
     */
    public void setpBcoGiradCheque1(java.lang.String pBcoGiradCheque1) {
        this.pBcoGiradCheque1 = pBcoGiradCheque1;
    }


    /**
     * Gets the pBcoGiradCheque2 value for this CrearPago.
     * 
     * @return pBcoGiradCheque2
     */
    public java.lang.String getpBcoGiradCheque2() {
        return pBcoGiradCheque2;
    }


    /**
     * Sets the pBcoGiradCheque2 value for this CrearPago.
     * 
     * @param pBcoGiradCheque2
     */
    public void setpBcoGiradCheque2(java.lang.String pBcoGiradCheque2) {
        this.pBcoGiradCheque2 = pBcoGiradCheque2;
    }


    /**
     * Gets the pBcoGiradCheque3 value for this CrearPago.
     * 
     * @return pBcoGiradCheque3
     */
    public java.lang.String getpBcoGiradCheque3() {
        return pBcoGiradCheque3;
    }


    /**
     * Sets the pBcoGiradCheque3 value for this CrearPago.
     * 
     * @param pBcoGiradCheque3
     */
    public void setpBcoGiradCheque3(java.lang.String pBcoGiradCheque3) {
        this.pBcoGiradCheque3 = pBcoGiradCheque3;
    }


    /**
     * Gets the pPagoEfectivo value for this CrearPago.
     * 
     * @return pPagoEfectivo
     */
    public java.math.BigDecimal getpPagoEfectivo() {
        return pPagoEfectivo;
    }


    /**
     * Sets the pPagoEfectivo value for this CrearPago.
     * 
     * @param pPagoEfectivo
     */
    public void setpPagoEfectivo(java.math.BigDecimal pPagoEfectivo) {
        this.pPagoEfectivo = pPagoEfectivo;
    }


    /**
     * Gets the pPagoTotal value for this CrearPago.
     * 
     * @return pPagoTotal
     */
    public java.math.BigDecimal getpPagoTotal() {
        return pPagoTotal;
    }


    /**
     * Sets the pPagoTotal value for this CrearPago.
     * 
     * @param pPagoTotal
     */
    public void setpPagoTotal(java.math.BigDecimal pPagoTotal) {
        this.pPagoTotal = pPagoTotal;
    }


    /**
     * Gets the pDatoTransaccion value for this CrearPago.
     * 
     * @return pDatoTransaccion
     */
    public java.lang.String getpDatoTransaccion() {
        return pDatoTransaccion;
    }


    /**
     * Sets the pDatoTransaccion value for this CrearPago.
     * 
     * @param pDatoTransaccion
     */
    public void setpDatoTransaccion(java.lang.String pDatoTransaccion) {
        this.pDatoTransaccion = pDatoTransaccion;
    }


    /**
     * Gets the pDetDocumentos value for this CrearPago.
     * 
     * @return pDetDocumentos   * Estructura XML con los servicios y documentos que van a ser
     * cancelados. Ver ComplexType="CrearPagoDetServiciosReqType"
     */
    public com.inter.claroCollPayWS.stub.CrearPagoDetServicioReqType[] getpDetDocumentos() {
        return pDetDocumentos;
    }


    /**
     * Sets the pDetDocumentos value for this CrearPago.
     * 
     * @param pDetDocumentos   * Estructura XML con los servicios y documentos que van a ser
     * cancelados. Ver ComplexType="CrearPagoDetServiciosReqType"
     */
    public void setpDetDocumentos(com.inter.claroCollPayWS.stub.CrearPagoDetServicioReqType[] pDetDocumentos) {
        this.pDetDocumentos = pDetDocumentos;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CrearPago)) return false;
        CrearPago other = (CrearPago) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.txId==null && other.getTxId()==null) || 
             (this.txId!=null &&
              this.txId.equals(other.getTxId()))) &&
            ((this.pCodAplicacion==null && other.getpCodAplicacion()==null) || 
             (this.pCodAplicacion!=null &&
              this.pCodAplicacion.equals(other.getpCodAplicacion()))) &&
            ((this.pExtorno==null && other.getpExtorno()==null) || 
             (this.pExtorno!=null &&
              this.pExtorno.equals(other.getpExtorno()))) &&
            ((this.pCodBanco==null && other.getpCodBanco()==null) || 
             (this.pCodBanco!=null &&
              this.pCodBanco.equals(other.getpCodBanco()))) &&
            ((this.pCodReenvia==null && other.getpCodReenvia()==null) || 
             (this.pCodReenvia!=null &&
              this.pCodReenvia.equals(other.getpCodReenvia()))) &&
            ((this.pCodMoneda==null && other.getpCodMoneda()==null) || 
             (this.pCodMoneda!=null &&
              this.pCodMoneda.equals(other.getpCodMoneda()))) &&
            ((this.pTipoIdentific==null && other.getpTipoIdentific()==null) || 
             (this.pTipoIdentific!=null &&
              this.pTipoIdentific.equals(other.getpTipoIdentific()))) &&
            ((this.pDatoIdentific==null && other.getpDatoIdentific()==null) || 
             (this.pDatoIdentific!=null &&
              this.pDatoIdentific.equals(other.getpDatoIdentific()))) &&
            ((this.pFechaHora==null && other.getpFechaHora()==null) || 
             (this.pFechaHora!=null &&
              this.pFechaHora.equals(other.getpFechaHora()))) &&
            ((this.pTrace==null && other.getpTrace()==null) || 
             (this.pTrace!=null &&
              this.pTrace.equals(other.getpTrace()))) &&
            ((this.pNroOperacion==null && other.getpNroOperacion()==null) || 
             (this.pNroOperacion!=null &&
              this.pNroOperacion.equals(other.getpNroOperacion()))) &&
            ((this.pNombreComercio==null && other.getpNombreComercio()==null) || 
             (this.pNombreComercio!=null &&
              this.pNombreComercio.equals(other.getpNombreComercio()))) &&
            ((this.pNroComercio==null && other.getpNroComercio()==null) || 
             (this.pNroComercio!=null &&
              this.pNroComercio.equals(other.getpNroComercio()))) &&
            ((this.pCodAgencia==null && other.getpCodAgencia()==null) || 
             (this.pCodAgencia!=null &&
              this.pCodAgencia.equals(other.getpCodAgencia()))) &&
            ((this.pCodCanal==null && other.getpCodCanal()==null) || 
             (this.pCodCanal!=null &&
              this.pCodCanal.equals(other.getpCodCanal()))) &&
            ((this.pCodCiudad==null && other.getpCodCiudad()==null) || 
             (this.pCodCiudad!=null &&
              this.pCodCiudad.equals(other.getpCodCiudad()))) &&
            ((this.pNroTerminal==null && other.getpNroTerminal()==null) || 
             (this.pNroTerminal!=null &&
              this.pNroTerminal.equals(other.getpNroTerminal()))) &&
            ((this.pPlaza==null && other.getpPlaza()==null) || 
             (this.pPlaza!=null &&
              this.pPlaza.equals(other.getpPlaza()))) &&
            ((this.pMedioPago==null && other.getpMedioPago()==null) || 
             (this.pMedioPago!=null &&
              this.pMedioPago.equals(other.getpMedioPago()))) &&
            ((this.pNroReferencia==null && other.getpNroReferencia()==null) || 
             (this.pNroReferencia!=null &&
              this.pNroReferencia.equals(other.getpNroReferencia()))) &&
            ((this.pNroCheque1==null && other.getpNroCheque1()==null) || 
             (this.pNroCheque1!=null &&
              this.pNroCheque1.equals(other.getpNroCheque1()))) &&
            ((this.pNroCheque2==null && other.getpNroCheque2()==null) || 
             (this.pNroCheque2!=null &&
              this.pNroCheque2.equals(other.getpNroCheque2()))) &&
            ((this.pNroCheque3==null && other.getpNroCheque3()==null) || 
             (this.pNroCheque3!=null &&
              this.pNroCheque3.equals(other.getpNroCheque3()))) &&
            ((this.pPlazaBcoCheque1==null && other.getpPlazaBcoCheque1()==null) || 
             (this.pPlazaBcoCheque1!=null &&
              this.pPlazaBcoCheque1.equals(other.getpPlazaBcoCheque1()))) &&
            ((this.pPlazaBcoCheque2==null && other.getpPlazaBcoCheque2()==null) || 
             (this.pPlazaBcoCheque2!=null &&
              this.pPlazaBcoCheque2.equals(other.getpPlazaBcoCheque2()))) &&
            ((this.pPlazaBcoCheque3==null && other.getpPlazaBcoCheque3()==null) || 
             (this.pPlazaBcoCheque3!=null &&
              this.pPlazaBcoCheque3.equals(other.getpPlazaBcoCheque3()))) &&
            ((this.pBcoGiradCheque1==null && other.getpBcoGiradCheque1()==null) || 
             (this.pBcoGiradCheque1!=null &&
              this.pBcoGiradCheque1.equals(other.getpBcoGiradCheque1()))) &&
            ((this.pBcoGiradCheque2==null && other.getpBcoGiradCheque2()==null) || 
             (this.pBcoGiradCheque2!=null &&
              this.pBcoGiradCheque2.equals(other.getpBcoGiradCheque2()))) &&
            ((this.pBcoGiradCheque3==null && other.getpBcoGiradCheque3()==null) || 
             (this.pBcoGiradCheque3!=null &&
              this.pBcoGiradCheque3.equals(other.getpBcoGiradCheque3()))) &&
            ((this.pPagoEfectivo==null && other.getpPagoEfectivo()==null) || 
             (this.pPagoEfectivo!=null &&
              this.pPagoEfectivo.equals(other.getpPagoEfectivo()))) &&
            ((this.pPagoTotal==null && other.getpPagoTotal()==null) || 
             (this.pPagoTotal!=null &&
              this.pPagoTotal.equals(other.getpPagoTotal()))) &&
            ((this.pDatoTransaccion==null && other.getpDatoTransaccion()==null) || 
             (this.pDatoTransaccion!=null &&
              this.pDatoTransaccion.equals(other.getpDatoTransaccion()))) &&
            ((this.pDetDocumentos==null && other.getpDetDocumentos()==null) || 
             (this.pDetDocumentos!=null &&
              java.util.Arrays.equals(this.pDetDocumentos, other.getpDetDocumentos())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getTxId() != null) {
            _hashCode += getTxId().hashCode();
        }
        if (getpCodAplicacion() != null) {
            _hashCode += getpCodAplicacion().hashCode();
        }
        if (getpExtorno() != null) {
            _hashCode += getpExtorno().hashCode();
        }
        if (getpCodBanco() != null) {
            _hashCode += getpCodBanco().hashCode();
        }
        if (getpCodReenvia() != null) {
            _hashCode += getpCodReenvia().hashCode();
        }
        if (getpCodMoneda() != null) {
            _hashCode += getpCodMoneda().hashCode();
        }
        if (getpTipoIdentific() != null) {
            _hashCode += getpTipoIdentific().hashCode();
        }
        if (getpDatoIdentific() != null) {
            _hashCode += getpDatoIdentific().hashCode();
        }
        if (getpFechaHora() != null) {
            _hashCode += getpFechaHora().hashCode();
        }
        if (getpTrace() != null) {
            _hashCode += getpTrace().hashCode();
        }
        if (getpNroOperacion() != null) {
            _hashCode += getpNroOperacion().hashCode();
        }
        if (getpNombreComercio() != null) {
            _hashCode += getpNombreComercio().hashCode();
        }
        if (getpNroComercio() != null) {
            _hashCode += getpNroComercio().hashCode();
        }
        if (getpCodAgencia() != null) {
            _hashCode += getpCodAgencia().hashCode();
        }
        if (getpCodCanal() != null) {
            _hashCode += getpCodCanal().hashCode();
        }
        if (getpCodCiudad() != null) {
            _hashCode += getpCodCiudad().hashCode();
        }
        if (getpNroTerminal() != null) {
            _hashCode += getpNroTerminal().hashCode();
        }
        if (getpPlaza() != null) {
            _hashCode += getpPlaza().hashCode();
        }
        if (getpMedioPago() != null) {
            _hashCode += getpMedioPago().hashCode();
        }
        if (getpNroReferencia() != null) {
            _hashCode += getpNroReferencia().hashCode();
        }
        if (getpNroCheque1() != null) {
            _hashCode += getpNroCheque1().hashCode();
        }
        if (getpNroCheque2() != null) {
            _hashCode += getpNroCheque2().hashCode();
        }
        if (getpNroCheque3() != null) {
            _hashCode += getpNroCheque3().hashCode();
        }
        if (getpPlazaBcoCheque1() != null) {
            _hashCode += getpPlazaBcoCheque1().hashCode();
        }
        if (getpPlazaBcoCheque2() != null) {
            _hashCode += getpPlazaBcoCheque2().hashCode();
        }
        if (getpPlazaBcoCheque3() != null) {
            _hashCode += getpPlazaBcoCheque3().hashCode();
        }
        if (getpBcoGiradCheque1() != null) {
            _hashCode += getpBcoGiradCheque1().hashCode();
        }
        if (getpBcoGiradCheque2() != null) {
            _hashCode += getpBcoGiradCheque2().hashCode();
        }
        if (getpBcoGiradCheque3() != null) {
            _hashCode += getpBcoGiradCheque3().hashCode();
        }
        if (getpPagoEfectivo() != null) {
            _hashCode += getpPagoEfectivo().hashCode();
        }
        if (getpPagoTotal() != null) {
            _hashCode += getpPagoTotal().hashCode();
        }
        if (getpDatoTransaccion() != null) {
            _hashCode += getpDatoTransaccion().hashCode();
        }
        if (getpDetDocumentos() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getpDetDocumentos());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getpDetDocumentos(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CrearPago.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", ">crearPago"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("txId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "txId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pCodAplicacion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pCodAplicacion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pExtorno");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pExtorno"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pCodBanco");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pCodBanco"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pCodReenvia");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pCodReenvia"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pCodMoneda");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pCodMoneda"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pTipoIdentific");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pTipoIdentific"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pDatoIdentific");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pDatoIdentific"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pFechaHora");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pFechaHora"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pTrace");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pTrace"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pNroOperacion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pNroOperacion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pNombreComercio");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pNombreComercio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pNroComercio");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pNroComercio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pCodAgencia");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pCodAgencia"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pCodCanal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pCodCanal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pCodCiudad");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pCodCiudad"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pNroTerminal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pNroTerminal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pPlaza");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pPlaza"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pMedioPago");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pMedioPago"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pNroReferencia");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pNroReferencia"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pNroCheque1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pNroCheque1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pNroCheque2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pNroCheque2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pNroCheque3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pNroCheque3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pPlazaBcoCheque1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pPlazaBcoCheque1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pPlazaBcoCheque2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pPlazaBcoCheque2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pPlazaBcoCheque3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pPlazaBcoCheque3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pBcoGiradCheque1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pBcoGiradCheque1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pBcoGiradCheque2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pBcoGiradCheque2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pBcoGiradCheque3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pBcoGiradCheque3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pPagoEfectivo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pPagoEfectivo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pPagoTotal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pPagoTotal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pDatoTransaccion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pDatoTransaccion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pDetDocumentos");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pDetDocumentos"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "CrearPagoDetServicioReqType"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pDetServicio"));
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
