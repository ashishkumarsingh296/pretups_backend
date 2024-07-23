/**
 * CrearAnulacionResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroCollPayWS.stub;

public class CrearAnulacionResponse  implements java.io.Serializable {
    private com.inter.claroCollPayWS.stub.AuditType audit;

    private java.lang.String xCodAplicacion;

    private java.lang.String xExtorno;

    private java.lang.String xCodMoneda;

    private java.lang.String xTipoIdentific;

    private java.lang.String xDatoIdentific;

    private java.lang.String xNombreCliente;

    private java.lang.String xTrace;

    private java.lang.String xRucAcreedor;

    private java.lang.String xNroIdentifCli;

    private java.lang.String xNroOperacionCobr;

    private java.lang.String xNroOperacionAcre;

    private java.lang.String xNroReferencia;

    private java.math.BigDecimal xImportePago;

    private java.lang.String xCodZonaDeudor;

    private java.math.BigDecimal xNroServAnulados;

    /* Estructura XML con los servicios y documentos que van a ser
     * cancelados. Ver ComplexType="CrearAnulDetServiciosRespType" */
    private com.inter.claroCollPayWS.stub.CrearAnulDetServicioRespType[] xDetDocumentos;

    /* Codigo de Mensaje */
    private java.lang.String xErrStatus;

    /* Descripcion de Mensaje */
    private java.lang.String xErrMessage;

    public CrearAnulacionResponse() {
    }

    public CrearAnulacionResponse(
           com.inter.claroCollPayWS.stub.AuditType audit,
           java.lang.String xCodAplicacion,
           java.lang.String xExtorno,
           java.lang.String xCodMoneda,
           java.lang.String xTipoIdentific,
           java.lang.String xDatoIdentific,
           java.lang.String xNombreCliente,
           java.lang.String xTrace,
           java.lang.String xRucAcreedor,
           java.lang.String xNroIdentifCli,
           java.lang.String xNroOperacionCobr,
           java.lang.String xNroOperacionAcre,
           java.lang.String xNroReferencia,
           java.math.BigDecimal xImportePago,
           java.lang.String xCodZonaDeudor,
           java.math.BigDecimal xNroServAnulados,
           com.inter.claroCollPayWS.stub.CrearAnulDetServicioRespType[] xDetDocumentos,
           java.lang.String xErrStatus,
           java.lang.String xErrMessage) {
           this.audit = audit;
           this.xCodAplicacion = xCodAplicacion;
           this.xExtorno = xExtorno;
           this.xCodMoneda = xCodMoneda;
           this.xTipoIdentific = xTipoIdentific;
           this.xDatoIdentific = xDatoIdentific;
           this.xNombreCliente = xNombreCliente;
           this.xTrace = xTrace;
           this.xRucAcreedor = xRucAcreedor;
           this.xNroIdentifCli = xNroIdentifCli;
           this.xNroOperacionCobr = xNroOperacionCobr;
           this.xNroOperacionAcre = xNroOperacionAcre;
           this.xNroReferencia = xNroReferencia;
           this.xImportePago = xImportePago;
           this.xCodZonaDeudor = xCodZonaDeudor;
           this.xNroServAnulados = xNroServAnulados;
           this.xDetDocumentos = xDetDocumentos;
           this.xErrStatus = xErrStatus;
           this.xErrMessage = xErrMessage;
    }


    /**
     * Gets the audit value for this CrearAnulacionResponse.
     * 
     * @return audit
     */
    public com.inter.claroCollPayWS.stub.AuditType getAudit() {
        return audit;
    }


    /**
     * Sets the audit value for this CrearAnulacionResponse.
     * 
     * @param audit
     */
    public void setAudit(com.inter.claroCollPayWS.stub.AuditType audit) {
        this.audit = audit;
    }


    /**
     * Gets the xCodAplicacion value for this CrearAnulacionResponse.
     * 
     * @return xCodAplicacion
     */
    public java.lang.String getXCodAplicacion() {
        return xCodAplicacion;
    }


    /**
     * Sets the xCodAplicacion value for this CrearAnulacionResponse.
     * 
     * @param xCodAplicacion
     */
    public void setXCodAplicacion(java.lang.String xCodAplicacion) {
        this.xCodAplicacion = xCodAplicacion;
    }


    /**
     * Gets the xExtorno value for this CrearAnulacionResponse.
     * 
     * @return xExtorno
     */
    public java.lang.String getXExtorno() {
        return xExtorno;
    }


    /**
     * Sets the xExtorno value for this CrearAnulacionResponse.
     * 
     * @param xExtorno
     */
    public void setXExtorno(java.lang.String xExtorno) {
        this.xExtorno = xExtorno;
    }


    /**
     * Gets the xCodMoneda value for this CrearAnulacionResponse.
     * 
     * @return xCodMoneda
     */
    public java.lang.String getXCodMoneda() {
        return xCodMoneda;
    }


    /**
     * Sets the xCodMoneda value for this CrearAnulacionResponse.
     * 
     * @param xCodMoneda
     */
    public void setXCodMoneda(java.lang.String xCodMoneda) {
        this.xCodMoneda = xCodMoneda;
    }


    /**
     * Gets the xTipoIdentific value for this CrearAnulacionResponse.
     * 
     * @return xTipoIdentific
     */
    public java.lang.String getXTipoIdentific() {
        return xTipoIdentific;
    }


    /**
     * Sets the xTipoIdentific value for this CrearAnulacionResponse.
     * 
     * @param xTipoIdentific
     */
    public void setXTipoIdentific(java.lang.String xTipoIdentific) {
        this.xTipoIdentific = xTipoIdentific;
    }


    /**
     * Gets the xDatoIdentific value for this CrearAnulacionResponse.
     * 
     * @return xDatoIdentific
     */
    public java.lang.String getXDatoIdentific() {
        return xDatoIdentific;
    }


    /**
     * Sets the xDatoIdentific value for this CrearAnulacionResponse.
     * 
     * @param xDatoIdentific
     */
    public void setXDatoIdentific(java.lang.String xDatoIdentific) {
        this.xDatoIdentific = xDatoIdentific;
    }


    /**
     * Gets the xNombreCliente value for this CrearAnulacionResponse.
     * 
     * @return xNombreCliente
     */
    public java.lang.String getXNombreCliente() {
        return xNombreCliente;
    }


    /**
     * Sets the xNombreCliente value for this CrearAnulacionResponse.
     * 
     * @param xNombreCliente
     */
    public void setXNombreCliente(java.lang.String xNombreCliente) {
        this.xNombreCliente = xNombreCliente;
    }


    /**
     * Gets the xTrace value for this CrearAnulacionResponse.
     * 
     * @return xTrace
     */
    public java.lang.String getXTrace() {
        return xTrace;
    }


    /**
     * Sets the xTrace value for this CrearAnulacionResponse.
     * 
     * @param xTrace
     */
    public void setXTrace(java.lang.String xTrace) {
        this.xTrace = xTrace;
    }


    /**
     * Gets the xRucAcreedor value for this CrearAnulacionResponse.
     * 
     * @return xRucAcreedor
     */
    public java.lang.String getXRucAcreedor() {
        return xRucAcreedor;
    }


    /**
     * Sets the xRucAcreedor value for this CrearAnulacionResponse.
     * 
     * @param xRucAcreedor
     */
    public void setXRucAcreedor(java.lang.String xRucAcreedor) {
        this.xRucAcreedor = xRucAcreedor;
    }


    /**
     * Gets the xNroIdentifCli value for this CrearAnulacionResponse.
     * 
     * @return xNroIdentifCli
     */
    public java.lang.String getXNroIdentifCli() {
        return xNroIdentifCli;
    }


    /**
     * Sets the xNroIdentifCli value for this CrearAnulacionResponse.
     * 
     * @param xNroIdentifCli
     */
    public void setXNroIdentifCli(java.lang.String xNroIdentifCli) {
        this.xNroIdentifCli = xNroIdentifCli;
    }


    /**
     * Gets the xNroOperacionCobr value for this CrearAnulacionResponse.
     * 
     * @return xNroOperacionCobr
     */
    public java.lang.String getXNroOperacionCobr() {
        return xNroOperacionCobr;
    }


    /**
     * Sets the xNroOperacionCobr value for this CrearAnulacionResponse.
     * 
     * @param xNroOperacionCobr
     */
    public void setXNroOperacionCobr(java.lang.String xNroOperacionCobr) {
        this.xNroOperacionCobr = xNroOperacionCobr;
    }


    /**
     * Gets the xNroOperacionAcre value for this CrearAnulacionResponse.
     * 
     * @return xNroOperacionAcre
     */
    public java.lang.String getXNroOperacionAcre() {
        return xNroOperacionAcre;
    }


    /**
     * Sets the xNroOperacionAcre value for this CrearAnulacionResponse.
     * 
     * @param xNroOperacionAcre
     */
    public void setXNroOperacionAcre(java.lang.String xNroOperacionAcre) {
        this.xNroOperacionAcre = xNroOperacionAcre;
    }


    /**
     * Gets the xNroReferencia value for this CrearAnulacionResponse.
     * 
     * @return xNroReferencia
     */
    public java.lang.String getXNroReferencia() {
        return xNroReferencia;
    }


    /**
     * Sets the xNroReferencia value for this CrearAnulacionResponse.
     * 
     * @param xNroReferencia
     */
    public void setXNroReferencia(java.lang.String xNroReferencia) {
        this.xNroReferencia = xNroReferencia;
    }


    /**
     * Gets the xImportePago value for this CrearAnulacionResponse.
     * 
     * @return xImportePago
     */
    public java.math.BigDecimal getXImportePago() {
        return xImportePago;
    }


    /**
     * Sets the xImportePago value for this CrearAnulacionResponse.
     * 
     * @param xImportePago
     */
    public void setXImportePago(java.math.BigDecimal xImportePago) {
        this.xImportePago = xImportePago;
    }


    /**
     * Gets the xCodZonaDeudor value for this CrearAnulacionResponse.
     * 
     * @return xCodZonaDeudor
     */
    public java.lang.String getXCodZonaDeudor() {
        return xCodZonaDeudor;
    }


    /**
     * Sets the xCodZonaDeudor value for this CrearAnulacionResponse.
     * 
     * @param xCodZonaDeudor
     */
    public void setXCodZonaDeudor(java.lang.String xCodZonaDeudor) {
        this.xCodZonaDeudor = xCodZonaDeudor;
    }


    /**
     * Gets the xNroServAnulados value for this CrearAnulacionResponse.
     * 
     * @return xNroServAnulados
     */
    public java.math.BigDecimal getXNroServAnulados() {
        return xNroServAnulados;
    }


    /**
     * Sets the xNroServAnulados value for this CrearAnulacionResponse.
     * 
     * @param xNroServAnulados
     */
    public void setXNroServAnulados(java.math.BigDecimal xNroServAnulados) {
        this.xNroServAnulados = xNroServAnulados;
    }


    /**
     * Gets the xDetDocumentos value for this CrearAnulacionResponse.
     * 
     * @return xDetDocumentos   * Estructura XML con los servicios y documentos que van a ser
     * cancelados. Ver ComplexType="CrearAnulDetServiciosRespType"
     */
    public com.inter.claroCollPayWS.stub.CrearAnulDetServicioRespType[] getXDetDocumentos() {
        return xDetDocumentos;
    }


    /**
     * Sets the xDetDocumentos value for this CrearAnulacionResponse.
     * 
     * @param xDetDocumentos   * Estructura XML con los servicios y documentos que van a ser
     * cancelados. Ver ComplexType="CrearAnulDetServiciosRespType"
     */
    public void setXDetDocumentos(com.inter.claroCollPayWS.stub.CrearAnulDetServicioRespType[] xDetDocumentos) {
        this.xDetDocumentos = xDetDocumentos;
    }


    /**
     * Gets the xErrStatus value for this CrearAnulacionResponse.
     * 
     * @return xErrStatus   * Codigo de Mensaje
     */
    public java.lang.String getXErrStatus() {
        return xErrStatus;
    }


    /**
     * Sets the xErrStatus value for this CrearAnulacionResponse.
     * 
     * @param xErrStatus   * Codigo de Mensaje
     */
    public void setXErrStatus(java.lang.String xErrStatus) {
        this.xErrStatus = xErrStatus;
    }


    /**
     * Gets the xErrMessage value for this CrearAnulacionResponse.
     * 
     * @return xErrMessage   * Descripcion de Mensaje
     */
    public java.lang.String getXErrMessage() {
        return xErrMessage;
    }


    /**
     * Sets the xErrMessage value for this CrearAnulacionResponse.
     * 
     * @param xErrMessage   * Descripcion de Mensaje
     */
    public void setXErrMessage(java.lang.String xErrMessage) {
        this.xErrMessage = xErrMessage;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CrearAnulacionResponse)) return false;
        CrearAnulacionResponse other = (CrearAnulacionResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.audit==null && other.getAudit()==null) || 
             (this.audit!=null &&
              this.audit.equals(other.getAudit()))) &&
            ((this.xCodAplicacion==null && other.getXCodAplicacion()==null) || 
             (this.xCodAplicacion!=null &&
              this.xCodAplicacion.equals(other.getXCodAplicacion()))) &&
            ((this.xExtorno==null && other.getXExtorno()==null) || 
             (this.xExtorno!=null &&
              this.xExtorno.equals(other.getXExtorno()))) &&
            ((this.xCodMoneda==null && other.getXCodMoneda()==null) || 
             (this.xCodMoneda!=null &&
              this.xCodMoneda.equals(other.getXCodMoneda()))) &&
            ((this.xTipoIdentific==null && other.getXTipoIdentific()==null) || 
             (this.xTipoIdentific!=null &&
              this.xTipoIdentific.equals(other.getXTipoIdentific()))) &&
            ((this.xDatoIdentific==null && other.getXDatoIdentific()==null) || 
             (this.xDatoIdentific!=null &&
              this.xDatoIdentific.equals(other.getXDatoIdentific()))) &&
            ((this.xNombreCliente==null && other.getXNombreCliente()==null) || 
             (this.xNombreCliente!=null &&
              this.xNombreCliente.equals(other.getXNombreCliente()))) &&
            ((this.xTrace==null && other.getXTrace()==null) || 
             (this.xTrace!=null &&
              this.xTrace.equals(other.getXTrace()))) &&
            ((this.xRucAcreedor==null && other.getXRucAcreedor()==null) || 
             (this.xRucAcreedor!=null &&
              this.xRucAcreedor.equals(other.getXRucAcreedor()))) &&
            ((this.xNroIdentifCli==null && other.getXNroIdentifCli()==null) || 
             (this.xNroIdentifCli!=null &&
              this.xNroIdentifCli.equals(other.getXNroIdentifCli()))) &&
            ((this.xNroOperacionCobr==null && other.getXNroOperacionCobr()==null) || 
             (this.xNroOperacionCobr!=null &&
              this.xNroOperacionCobr.equals(other.getXNroOperacionCobr()))) &&
            ((this.xNroOperacionAcre==null && other.getXNroOperacionAcre()==null) || 
             (this.xNroOperacionAcre!=null &&
              this.xNroOperacionAcre.equals(other.getXNroOperacionAcre()))) &&
            ((this.xNroReferencia==null && other.getXNroReferencia()==null) || 
             (this.xNroReferencia!=null &&
              this.xNroReferencia.equals(other.getXNroReferencia()))) &&
            ((this.xImportePago==null && other.getXImportePago()==null) || 
             (this.xImportePago!=null &&
              this.xImportePago.equals(other.getXImportePago()))) &&
            ((this.xCodZonaDeudor==null && other.getXCodZonaDeudor()==null) || 
             (this.xCodZonaDeudor!=null &&
              this.xCodZonaDeudor.equals(other.getXCodZonaDeudor()))) &&
            ((this.xNroServAnulados==null && other.getXNroServAnulados()==null) || 
             (this.xNroServAnulados!=null &&
              this.xNroServAnulados.equals(other.getXNroServAnulados()))) &&
            ((this.xDetDocumentos==null && other.getXDetDocumentos()==null) || 
             (this.xDetDocumentos!=null &&
              java.util.Arrays.equals(this.xDetDocumentos, other.getXDetDocumentos()))) &&
            ((this.xErrStatus==null && other.getXErrStatus()==null) || 
             (this.xErrStatus!=null &&
              this.xErrStatus.equals(other.getXErrStatus()))) &&
            ((this.xErrMessage==null && other.getXErrMessage()==null) || 
             (this.xErrMessage!=null &&
              this.xErrMessage.equals(other.getXErrMessage())));
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
        if (getAudit() != null) {
            _hashCode += getAudit().hashCode();
        }
        if (getXCodAplicacion() != null) {
            _hashCode += getXCodAplicacion().hashCode();
        }
        if (getXExtorno() != null) {
            _hashCode += getXExtorno().hashCode();
        }
        if (getXCodMoneda() != null) {
            _hashCode += getXCodMoneda().hashCode();
        }
        if (getXTipoIdentific() != null) {
            _hashCode += getXTipoIdentific().hashCode();
        }
        if (getXDatoIdentific() != null) {
            _hashCode += getXDatoIdentific().hashCode();
        }
        if (getXNombreCliente() != null) {
            _hashCode += getXNombreCliente().hashCode();
        }
        if (getXTrace() != null) {
            _hashCode += getXTrace().hashCode();
        }
        if (getXRucAcreedor() != null) {
            _hashCode += getXRucAcreedor().hashCode();
        }
        if (getXNroIdentifCli() != null) {
            _hashCode += getXNroIdentifCli().hashCode();
        }
        if (getXNroOperacionCobr() != null) {
            _hashCode += getXNroOperacionCobr().hashCode();
        }
        if (getXNroOperacionAcre() != null) {
            _hashCode += getXNroOperacionAcre().hashCode();
        }
        if (getXNroReferencia() != null) {
            _hashCode += getXNroReferencia().hashCode();
        }
        if (getXImportePago() != null) {
            _hashCode += getXImportePago().hashCode();
        }
        if (getXCodZonaDeudor() != null) {
            _hashCode += getXCodZonaDeudor().hashCode();
        }
        if (getXNroServAnulados() != null) {
            _hashCode += getXNroServAnulados().hashCode();
        }
        if (getXDetDocumentos() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getXDetDocumentos());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getXDetDocumentos(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getXErrStatus() != null) {
            _hashCode += getXErrStatus().hashCode();
        }
        if (getXErrMessage() != null) {
            _hashCode += getXErrMessage().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CrearAnulacionResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", ">crearAnulacionResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("audit");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "audit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "AuditType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XCodAplicacion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xCodAplicacion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XExtorno");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xExtorno"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XCodMoneda");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xCodMoneda"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XTipoIdentific");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xTipoIdentific"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XDatoIdentific");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xDatoIdentific"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XNombreCliente");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xNombreCliente"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XTrace");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xTrace"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XRucAcreedor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xRucAcreedor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XNroIdentifCli");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xNroIdentifCli"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XNroOperacionCobr");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xNroOperacionCobr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XNroOperacionAcre");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xNroOperacionAcre"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XNroReferencia");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xNroReferencia"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XImportePago");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xImportePago"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XCodZonaDeudor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xCodZonaDeudor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XNroServAnulados");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xNroServAnulados"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XDetDocumentos");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xDetDocumentos"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "CrearAnulDetServicioRespType"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xDeudaServicio"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XErrStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xErrStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XErrMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xErrMessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
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
