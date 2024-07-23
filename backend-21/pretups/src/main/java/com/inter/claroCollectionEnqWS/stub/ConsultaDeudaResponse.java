/**
 * ConsultaDeudaResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroCollectionEnqWS.stub;

public class ConsultaDeudaResponse  implements java.io.Serializable {
    private com.inter.claroCollectionEnqWS.stub.AuditType audit;

    private java.lang.String xIdentificacion;

    private java.lang.String xNombreCliente;

    private java.lang.String xMasDocumentosFlag;

    private java.math.BigDecimal xPosUltDocumento;

    private java.lang.String xNroReferencia;

    private java.lang.String xNroIdentifCliente;

    private java.math.BigDecimal xNroServDevueltos;

    private java.math.BigDecimal xNroDocsDeuda;

    private java.lang.String xDatoTransaccion;

    /* Estructura XML con los servicios y documentos que adeuda. Ver
     * Tipo="DeudaServiciosType" */
    private com.inter.claroCollectionEnqWS.stub.DeudaServicioType[] xDeudaCliente;

    /* Codigo de Mensaje */
    private java.lang.String xErrStatus;

    /* Descripcion de Mensaje */
    private java.lang.String xErrMessage;

    public ConsultaDeudaResponse() {
    }

    public ConsultaDeudaResponse(
           com.inter.claroCollectionEnqWS.stub.AuditType audit,
           java.lang.String xIdentificacion,
           java.lang.String xNombreCliente,
           java.lang.String xMasDocumentosFlag,
           java.math.BigDecimal xPosUltDocumento,
           java.lang.String xNroReferencia,
           java.lang.String xNroIdentifCliente,
           java.math.BigDecimal xNroServDevueltos,
           java.math.BigDecimal xNroDocsDeuda,
           java.lang.String xDatoTransaccion,
           com.inter.claroCollectionEnqWS.stub.DeudaServicioType[] xDeudaCliente,
           java.lang.String xErrStatus,
           java.lang.String xErrMessage) {
           this.audit = audit;
           this.xIdentificacion = xIdentificacion;
           this.xNombreCliente = xNombreCliente;
           this.xMasDocumentosFlag = xMasDocumentosFlag;
           this.xPosUltDocumento = xPosUltDocumento;
           this.xNroReferencia = xNroReferencia;
           this.xNroIdentifCliente = xNroIdentifCliente;
           this.xNroServDevueltos = xNroServDevueltos;
           this.xNroDocsDeuda = xNroDocsDeuda;
           this.xDatoTransaccion = xDatoTransaccion;
           this.xDeudaCliente = xDeudaCliente;
           this.xErrStatus = xErrStatus;
           this.xErrMessage = xErrMessage;
    }


    /**
     * Gets the audit value for this ConsultaDeudaResponse.
     * 
     * @return audit
     */
    public com.inter.claroCollectionEnqWS.stub.AuditType getAudit() {
        return audit;
    }


    /**
     * Sets the audit value for this ConsultaDeudaResponse.
     * 
     * @param audit
     */
    public void setAudit(com.inter.claroCollectionEnqWS.stub.AuditType audit) {
        this.audit = audit;
    }


    /**
     * Gets the xIdentificacion value for this ConsultaDeudaResponse.
     * 
     * @return xIdentificacion
     */
    public java.lang.String getXIdentificacion() {
        return xIdentificacion;
    }


    /**
     * Sets the xIdentificacion value for this ConsultaDeudaResponse.
     * 
     * @param xIdentificacion
     */
    public void setXIdentificacion(java.lang.String xIdentificacion) {
        this.xIdentificacion = xIdentificacion;
    }


    /**
     * Gets the xNombreCliente value for this ConsultaDeudaResponse.
     * 
     * @return xNombreCliente
     */
    public java.lang.String getXNombreCliente() {
        return xNombreCliente;
    }


    /**
     * Sets the xNombreCliente value for this ConsultaDeudaResponse.
     * 
     * @param xNombreCliente
     */
    public void setXNombreCliente(java.lang.String xNombreCliente) {
        this.xNombreCliente = xNombreCliente;
    }


    /**
     * Gets the xMasDocumentosFlag value for this ConsultaDeudaResponse.
     * 
     * @return xMasDocumentosFlag
     */
    public java.lang.String getXMasDocumentosFlag() {
        return xMasDocumentosFlag;
    }


    /**
     * Sets the xMasDocumentosFlag value for this ConsultaDeudaResponse.
     * 
     * @param xMasDocumentosFlag
     */
    public void setXMasDocumentosFlag(java.lang.String xMasDocumentosFlag) {
        this.xMasDocumentosFlag = xMasDocumentosFlag;
    }


    /**
     * Gets the xPosUltDocumento value for this ConsultaDeudaResponse.
     * 
     * @return xPosUltDocumento
     */
    public java.math.BigDecimal getXPosUltDocumento() {
        return xPosUltDocumento;
    }


    /**
     * Sets the xPosUltDocumento value for this ConsultaDeudaResponse.
     * 
     * @param xPosUltDocumento
     */
    public void setXPosUltDocumento(java.math.BigDecimal xPosUltDocumento) {
        this.xPosUltDocumento = xPosUltDocumento;
    }


    /**
     * Gets the xNroReferencia value for this ConsultaDeudaResponse.
     * 
     * @return xNroReferencia
     */
    public java.lang.String getXNroReferencia() {
        return xNroReferencia;
    }


    /**
     * Sets the xNroReferencia value for this ConsultaDeudaResponse.
     * 
     * @param xNroReferencia
     */
    public void setXNroReferencia(java.lang.String xNroReferencia) {
        this.xNroReferencia = xNroReferencia;
    }


    /**
     * Gets the xNroIdentifCliente value for this ConsultaDeudaResponse.
     * 
     * @return xNroIdentifCliente
     */
    public java.lang.String getXNroIdentifCliente() {
        return xNroIdentifCliente;
    }


    /**
     * Sets the xNroIdentifCliente value for this ConsultaDeudaResponse.
     * 
     * @param xNroIdentifCliente
     */
    public void setXNroIdentifCliente(java.lang.String xNroIdentifCliente) {
        this.xNroIdentifCliente = xNroIdentifCliente;
    }


    /**
     * Gets the xNroServDevueltos value for this ConsultaDeudaResponse.
     * 
     * @return xNroServDevueltos
     */
    public java.math.BigDecimal getXNroServDevueltos() {
        return xNroServDevueltos;
    }


    /**
     * Sets the xNroServDevueltos value for this ConsultaDeudaResponse.
     * 
     * @param xNroServDevueltos
     */
    public void setXNroServDevueltos(java.math.BigDecimal xNroServDevueltos) {
        this.xNroServDevueltos = xNroServDevueltos;
    }


    /**
     * Gets the xNroDocsDeuda value for this ConsultaDeudaResponse.
     * 
     * @return xNroDocsDeuda
     */
    public java.math.BigDecimal getXNroDocsDeuda() {
        return xNroDocsDeuda;
    }


    /**
     * Sets the xNroDocsDeuda value for this ConsultaDeudaResponse.
     * 
     * @param xNroDocsDeuda
     */
    public void setXNroDocsDeuda(java.math.BigDecimal xNroDocsDeuda) {
        this.xNroDocsDeuda = xNroDocsDeuda;
    }


    /**
     * Gets the xDatoTransaccion value for this ConsultaDeudaResponse.
     * 
     * @return xDatoTransaccion
     */
    public java.lang.String getXDatoTransaccion() {
        return xDatoTransaccion;
    }


    /**
     * Sets the xDatoTransaccion value for this ConsultaDeudaResponse.
     * 
     * @param xDatoTransaccion
     */
    public void setXDatoTransaccion(java.lang.String xDatoTransaccion) {
        this.xDatoTransaccion = xDatoTransaccion;
    }


    /**
     * Gets the xDeudaCliente value for this ConsultaDeudaResponse.
     * 
     * @return xDeudaCliente   * Estructura XML con los servicios y documentos que adeuda. Ver
     * Tipo="DeudaServiciosType"
     */
    public com.inter.claroCollectionEnqWS.stub.DeudaServicioType[] getXDeudaCliente() {
        return xDeudaCliente;
    }


    /**
     * Sets the xDeudaCliente value for this ConsultaDeudaResponse.
     * 
     * @param xDeudaCliente   * Estructura XML con los servicios y documentos que adeuda. Ver
     * Tipo="DeudaServiciosType"
     */
    public void setXDeudaCliente(com.inter.claroCollectionEnqWS.stub.DeudaServicioType[] xDeudaCliente) {
        this.xDeudaCliente = xDeudaCliente;
    }


    /**
     * Gets the xErrStatus value for this ConsultaDeudaResponse.
     * 
     * @return xErrStatus   * Codigo de Mensaje
     */
    public java.lang.String getXErrStatus() {
        return xErrStatus;
    }


    /**
     * Sets the xErrStatus value for this ConsultaDeudaResponse.
     * 
     * @param xErrStatus   * Codigo de Mensaje
     */
    public void setXErrStatus(java.lang.String xErrStatus) {
        this.xErrStatus = xErrStatus;
    }


    /**
     * Gets the xErrMessage value for this ConsultaDeudaResponse.
     * 
     * @return xErrMessage   * Descripcion de Mensaje
     */
    public java.lang.String getXErrMessage() {
        return xErrMessage;
    }


    /**
     * Sets the xErrMessage value for this ConsultaDeudaResponse.
     * 
     * @param xErrMessage   * Descripcion de Mensaje
     */
    public void setXErrMessage(java.lang.String xErrMessage) {
        this.xErrMessage = xErrMessage;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConsultaDeudaResponse)) return false;
        ConsultaDeudaResponse other = (ConsultaDeudaResponse) obj;
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
            ((this.xIdentificacion==null && other.getXIdentificacion()==null) || 
             (this.xIdentificacion!=null &&
              this.xIdentificacion.equals(other.getXIdentificacion()))) &&
            ((this.xNombreCliente==null && other.getXNombreCliente()==null) || 
             (this.xNombreCliente!=null &&
              this.xNombreCliente.equals(other.getXNombreCliente()))) &&
            ((this.xMasDocumentosFlag==null && other.getXMasDocumentosFlag()==null) || 
             (this.xMasDocumentosFlag!=null &&
              this.xMasDocumentosFlag.equals(other.getXMasDocumentosFlag()))) &&
            ((this.xPosUltDocumento==null && other.getXPosUltDocumento()==null) || 
             (this.xPosUltDocumento!=null &&
              this.xPosUltDocumento.equals(other.getXPosUltDocumento()))) &&
            ((this.xNroReferencia==null && other.getXNroReferencia()==null) || 
             (this.xNroReferencia!=null &&
              this.xNroReferencia.equals(other.getXNroReferencia()))) &&
            ((this.xNroIdentifCliente==null && other.getXNroIdentifCliente()==null) || 
             (this.xNroIdentifCliente!=null &&
              this.xNroIdentifCliente.equals(other.getXNroIdentifCliente()))) &&
            ((this.xNroServDevueltos==null && other.getXNroServDevueltos()==null) || 
             (this.xNroServDevueltos!=null &&
              this.xNroServDevueltos.equals(other.getXNroServDevueltos()))) &&
            ((this.xNroDocsDeuda==null && other.getXNroDocsDeuda()==null) || 
             (this.xNroDocsDeuda!=null &&
              this.xNroDocsDeuda.equals(other.getXNroDocsDeuda()))) &&
            ((this.xDatoTransaccion==null && other.getXDatoTransaccion()==null) || 
             (this.xDatoTransaccion!=null &&
              this.xDatoTransaccion.equals(other.getXDatoTransaccion()))) &&
            ((this.xDeudaCliente==null && other.getXDeudaCliente()==null) || 
             (this.xDeudaCliente!=null &&
              java.util.Arrays.equals(this.xDeudaCliente, other.getXDeudaCliente()))) &&
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
        if (getXIdentificacion() != null) {
            _hashCode += getXIdentificacion().hashCode();
        }
        if (getXNombreCliente() != null) {
            _hashCode += getXNombreCliente().hashCode();
        }
        if (getXMasDocumentosFlag() != null) {
            _hashCode += getXMasDocumentosFlag().hashCode();
        }
        if (getXPosUltDocumento() != null) {
            _hashCode += getXPosUltDocumento().hashCode();
        }
        if (getXNroReferencia() != null) {
            _hashCode += getXNroReferencia().hashCode();
        }
        if (getXNroIdentifCliente() != null) {
            _hashCode += getXNroIdentifCliente().hashCode();
        }
        if (getXNroServDevueltos() != null) {
            _hashCode += getXNroServDevueltos().hashCode();
        }
        if (getXNroDocsDeuda() != null) {
            _hashCode += getXNroDocsDeuda().hashCode();
        }
        if (getXDatoTransaccion() != null) {
            _hashCode += getXDatoTransaccion().hashCode();
        }
        if (getXDeudaCliente() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getXDeudaCliente());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getXDeudaCliente(), i);
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
        new org.apache.axis.description.TypeDesc(ConsultaDeudaResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/consultapagos/", ">consultaDeudaResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("audit");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/consultapagos/", "audit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/consultapagos/", "AuditType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XIdentificacion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/consultapagos/", "xIdentificacion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XNombreCliente");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/consultapagos/", "xNombreCliente"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XMasDocumentosFlag");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/consultapagos/", "xMasDocumentosFlag"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XPosUltDocumento");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/consultapagos/", "xPosUltDocumento"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XNroReferencia");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/consultapagos/", "xNroReferencia"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XNroIdentifCliente");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/consultapagos/", "xNroIdentifCliente"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XNroServDevueltos");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/consultapagos/", "xNroServDevueltos"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XNroDocsDeuda");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/consultapagos/", "xNroDocsDeuda"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XDatoTransaccion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/consultapagos/", "xDatoTransaccion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XDeudaCliente");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/consultapagos/", "xDeudaCliente"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/consultapagos/", "DeudaServicioType"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/consultapagos/", "xDeudaServicio"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XErrStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/consultapagos/", "xErrStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XErrMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/consultapagos/", "xErrMessage"));
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
