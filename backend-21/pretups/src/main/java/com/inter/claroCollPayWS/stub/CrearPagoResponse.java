/**
 * CrearPagoResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroCollPayWS.stub;

public class CrearPagoResponse  implements java.io.Serializable {
    private com.inter.claroCollPayWS.stub.AuditType audit;

    private java.lang.String xCodAplicacion;

    private java.lang.String pExtorno;

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

    private java.lang.String xCodZonaDeudor;

    private java.lang.String xDatoTransaccion;

    /* Estructura XML con los servicios y documentos que van a ser
     * cancelados. Ver ComplexType="CrearPagoDetServiciosRespType" */
    private com.inter.claroCollPayWS.stub.CrearPagoDetServicioRespType[] xDetDocumentos;

    /* Codigo de Mensaje */
    private java.lang.String xErrStatus;

    /* Descripcion de Mensaje */
    private java.lang.String xErrMessage;

    public CrearPagoResponse() {
    }

    public CrearPagoResponse(
           com.inter.claroCollPayWS.stub.AuditType audit,
           java.lang.String xCodAplicacion,
           java.lang.String pExtorno,
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
           java.lang.String xCodZonaDeudor,
           java.lang.String xDatoTransaccion,
           com.inter.claroCollPayWS.stub.CrearPagoDetServicioRespType[] xDetDocumentos,
           java.lang.String xErrStatus,
           java.lang.String xErrMessage) {
           this.audit = audit;
           this.xCodAplicacion = xCodAplicacion;
           this.pExtorno = pExtorno;
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
           this.xCodZonaDeudor = xCodZonaDeudor;
           this.xDatoTransaccion = xDatoTransaccion;
           this.xDetDocumentos = xDetDocumentos;
           this.xErrStatus = xErrStatus;
           this.xErrMessage = xErrMessage;
    }


    /**
     * Gets the audit value for this CrearPagoResponse.
     * 
     * @return audit
     */
    public com.inter.claroCollPayWS.stub.AuditType getAudit() {
        return audit;
    }


    /**
     * Sets the audit value for this CrearPagoResponse.
     * 
     * @param audit
     */
    public void setAudit(com.inter.claroCollPayWS.stub.AuditType audit) {
        this.audit = audit;
    }


    /**
     * Gets the xCodAplicacion value for this CrearPagoResponse.
     * 
     * @return xCodAplicacion
     */
    public java.lang.String getXCodAplicacion() {
        return xCodAplicacion;
    }


    /**
     * Sets the xCodAplicacion value for this CrearPagoResponse.
     * 
     * @param xCodAplicacion
     */
    public void setXCodAplicacion(java.lang.String xCodAplicacion) {
        this.xCodAplicacion = xCodAplicacion;
    }


    /**
     * Gets the pExtorno value for this CrearPagoResponse.
     * 
     * @return pExtorno
     */
    public java.lang.String getPExtorno() {
        return pExtorno;
    }


    /**
     * Sets the pExtorno value for this CrearPagoResponse.
     * 
     * @param pExtorno
     */
    public void setPExtorno(java.lang.String pExtorno) {
        this.pExtorno = pExtorno;
    }


    /**
     * Gets the xCodMoneda value for this CrearPagoResponse.
     * 
     * @return xCodMoneda
     */
    public java.lang.String getXCodMoneda() {
        return xCodMoneda;
    }


    /**
     * Sets the xCodMoneda value for this CrearPagoResponse.
     * 
     * @param xCodMoneda
     */
    public void setXCodMoneda(java.lang.String xCodMoneda) {
        this.xCodMoneda = xCodMoneda;
    }


    /**
     * Gets the xTipoIdentific value for this CrearPagoResponse.
     * 
     * @return xTipoIdentific
     */
    public java.lang.String getXTipoIdentific() {
        return xTipoIdentific;
    }


    /**
     * Sets the xTipoIdentific value for this CrearPagoResponse.
     * 
     * @param xTipoIdentific
     */
    public void setXTipoIdentific(java.lang.String xTipoIdentific) {
        this.xTipoIdentific = xTipoIdentific;
    }


    /**
     * Gets the xDatoIdentific value for this CrearPagoResponse.
     * 
     * @return xDatoIdentific
     */
    public java.lang.String getXDatoIdentific() {
        return xDatoIdentific;
    }


    /**
     * Sets the xDatoIdentific value for this CrearPagoResponse.
     * 
     * @param xDatoIdentific
     */
    public void setXDatoIdentific(java.lang.String xDatoIdentific) {
        this.xDatoIdentific = xDatoIdentific;
    }


    /**
     * Gets the xNombreCliente value for this CrearPagoResponse.
     * 
     * @return xNombreCliente
     */
    public java.lang.String getXNombreCliente() {
        return xNombreCliente;
    }


    /**
     * Sets the xNombreCliente value for this CrearPagoResponse.
     * 
     * @param xNombreCliente
     */
    public void setXNombreCliente(java.lang.String xNombreCliente) {
        this.xNombreCliente = xNombreCliente;
    }


    /**
     * Gets the xTrace value for this CrearPagoResponse.
     * 
     * @return xTrace
     */
    public java.lang.String getXTrace() {
        return xTrace;
    }


    /**
     * Sets the xTrace value for this CrearPagoResponse.
     * 
     * @param xTrace
     */
    public void setXTrace(java.lang.String xTrace) {
        this.xTrace = xTrace;
    }


    /**
     * Gets the xRucAcreedor value for this CrearPagoResponse.
     * 
     * @return xRucAcreedor
     */
    public java.lang.String getXRucAcreedor() {
        return xRucAcreedor;
    }


    /**
     * Sets the xRucAcreedor value for this CrearPagoResponse.
     * 
     * @param xRucAcreedor
     */
    public void setXRucAcreedor(java.lang.String xRucAcreedor) {
        this.xRucAcreedor = xRucAcreedor;
    }


    /**
     * Gets the xNroIdentifCli value for this CrearPagoResponse.
     * 
     * @return xNroIdentifCli
     */
    public java.lang.String getXNroIdentifCli() {
        return xNroIdentifCli;
    }


    /**
     * Sets the xNroIdentifCli value for this CrearPagoResponse.
     * 
     * @param xNroIdentifCli
     */
    public void setXNroIdentifCli(java.lang.String xNroIdentifCli) {
        this.xNroIdentifCli = xNroIdentifCli;
    }


    /**
     * Gets the xNroOperacionCobr value for this CrearPagoResponse.
     * 
     * @return xNroOperacionCobr
     */
    public java.lang.String getXNroOperacionCobr() {
        return xNroOperacionCobr;
    }


    /**
     * Sets the xNroOperacionCobr value for this CrearPagoResponse.
     * 
     * @param xNroOperacionCobr
     */
    public void setXNroOperacionCobr(java.lang.String xNroOperacionCobr) {
        this.xNroOperacionCobr = xNroOperacionCobr;
    }


    /**
     * Gets the xNroOperacionAcre value for this CrearPagoResponse.
     * 
     * @return xNroOperacionAcre
     */
    public java.lang.String getXNroOperacionAcre() {
        return xNroOperacionAcre;
    }


    /**
     * Sets the xNroOperacionAcre value for this CrearPagoResponse.
     * 
     * @param xNroOperacionAcre
     */
    public void setXNroOperacionAcre(java.lang.String xNroOperacionAcre) {
        this.xNroOperacionAcre = xNroOperacionAcre;
    }


    /**
     * Gets the xNroReferencia value for this CrearPagoResponse.
     * 
     * @return xNroReferencia
     */
    public java.lang.String getXNroReferencia() {
        return xNroReferencia;
    }


    /**
     * Sets the xNroReferencia value for this CrearPagoResponse.
     * 
     * @param xNroReferencia
     */
    public void setXNroReferencia(java.lang.String xNroReferencia) {
        this.xNroReferencia = xNroReferencia;
    }


    /**
     * Gets the xCodZonaDeudor value for this CrearPagoResponse.
     * 
     * @return xCodZonaDeudor
     */
    public java.lang.String getXCodZonaDeudor() {
        return xCodZonaDeudor;
    }


    /**
     * Sets the xCodZonaDeudor value for this CrearPagoResponse.
     * 
     * @param xCodZonaDeudor
     */
    public void setXCodZonaDeudor(java.lang.String xCodZonaDeudor) {
        this.xCodZonaDeudor = xCodZonaDeudor;
    }


    /**
     * Gets the xDatoTransaccion value for this CrearPagoResponse.
     * 
     * @return xDatoTransaccion
     */
    public java.lang.String getXDatoTransaccion() {
        return xDatoTransaccion;
    }


    /**
     * Sets the xDatoTransaccion value for this CrearPagoResponse.
     * 
     * @param xDatoTransaccion
     */
    public void setXDatoTransaccion(java.lang.String xDatoTransaccion) {
        this.xDatoTransaccion = xDatoTransaccion;
    }


    /**
     * Gets the xDetDocumentos value for this CrearPagoResponse.
     * 
     * @return xDetDocumentos   * Estructura XML con los servicios y documentos que van a ser
     * cancelados. Ver ComplexType="CrearPagoDetServiciosRespType"
     */
    public com.inter.claroCollPayWS.stub.CrearPagoDetServicioRespType[] getXDetDocumentos() {
        return xDetDocumentos;
    }


    /**
     * Sets the xDetDocumentos value for this CrearPagoResponse.
     * 
     * @param xDetDocumentos   * Estructura XML con los servicios y documentos que van a ser
     * cancelados. Ver ComplexType="CrearPagoDetServiciosRespType"
     */
    public void setXDetDocumentos(com.inter.claroCollPayWS.stub.CrearPagoDetServicioRespType[] xDetDocumentos) {
        this.xDetDocumentos = xDetDocumentos;
    }


    /**
     * Gets the xErrStatus value for this CrearPagoResponse.
     * 
     * @return xErrStatus   * Codigo de Mensaje
     */
    public java.lang.String getXErrStatus() {
        return xErrStatus;
    }


    /**
     * Sets the xErrStatus value for this CrearPagoResponse.
     * 
     * @param xErrStatus   * Codigo de Mensaje
     */
    public void setXErrStatus(java.lang.String xErrStatus) {
        this.xErrStatus = xErrStatus;
    }


    /**
     * Gets the xErrMessage value for this CrearPagoResponse.
     * 
     * @return xErrMessage   * Descripcion de Mensaje
     */
    public java.lang.String getXErrMessage() {
        return xErrMessage;
    }


    /**
     * Sets the xErrMessage value for this CrearPagoResponse.
     * 
     * @param xErrMessage   * Descripcion de Mensaje
     */
    public void setXErrMessage(java.lang.String xErrMessage) {
        this.xErrMessage = xErrMessage;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CrearPagoResponse)) return false;
        CrearPagoResponse other = (CrearPagoResponse) obj;
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
            ((this.pExtorno==null && other.getPExtorno()==null) || 
             (this.pExtorno!=null &&
              this.pExtorno.equals(other.getPExtorno()))) &&
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
            ((this.xCodZonaDeudor==null && other.getXCodZonaDeudor()==null) || 
             (this.xCodZonaDeudor!=null &&
              this.xCodZonaDeudor.equals(other.getXCodZonaDeudor()))) &&
            ((this.xDatoTransaccion==null && other.getXDatoTransaccion()==null) || 
             (this.xDatoTransaccion!=null &&
              this.xDatoTransaccion.equals(other.getXDatoTransaccion()))) &&
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
        if (getPExtorno() != null) {
            _hashCode += getPExtorno().hashCode();
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
        if (getXCodZonaDeudor() != null) {
            _hashCode += getXCodZonaDeudor().hashCode();
        }
        if (getXDatoTransaccion() != null) {
            _hashCode += getXDatoTransaccion().hashCode();
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
        new org.apache.axis.description.TypeDesc(CrearPagoResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", ">crearPagoResponse"));
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
        elemField.setFieldName("PExtorno");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pExtorno"));
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
        elemField.setFieldName("XCodZonaDeudor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xCodZonaDeudor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XDatoTransaccion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xDatoTransaccion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XDetDocumentos");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xDetDocumentos"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "CrearPagoDetServicioRespType"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xDetServicio"));
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