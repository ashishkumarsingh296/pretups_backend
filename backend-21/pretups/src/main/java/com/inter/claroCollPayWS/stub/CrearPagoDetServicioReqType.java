/**
 * CrearPagoDetServicioReqType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroCollPayWS.stub;

public class CrearPagoDetServicioReqType  implements java.io.Serializable {
    private java.lang.String pEstadoDeudor;

    private java.lang.String pCodTipoServicio;

    private java.math.BigDecimal pNroDocs;

    private java.math.BigDecimal pMontoPagado;

    private java.lang.String pDatoServicio;

    private com.inter.claroCollPayWS.stub.CrearPagoDetDocumentoReqType[] pDetalleDocs;

    public CrearPagoDetServicioReqType() {
    }

    public CrearPagoDetServicioReqType(
           java.lang.String pEstadoDeudor,
           java.lang.String pCodTipoServicio,
           java.math.BigDecimal pNroDocs,
           java.math.BigDecimal pMontoPagado,
           java.lang.String pDatoServicio,
           com.inter.claroCollPayWS.stub.CrearPagoDetDocumentoReqType[] pDetalleDocs) {
           this.pEstadoDeudor = pEstadoDeudor;
           this.pCodTipoServicio = pCodTipoServicio;
           this.pNroDocs = pNroDocs;
           this.pMontoPagado = pMontoPagado;
           this.pDatoServicio = pDatoServicio;
           this.pDetalleDocs = pDetalleDocs;
    }


    /**
     * Gets the pEstadoDeudor value for this CrearPagoDetServicioReqType.
     * 
     * @return pEstadoDeudor
     */
    public java.lang.String getpEstadoDeudor() {
        return pEstadoDeudor;
    }


    /**
     * Sets the pEstadoDeudor value for this CrearPagoDetServicioReqType.
     * 
     * @param pEstadoDeudor
     */
    public void setpEstadoDeudor(java.lang.String pEstadoDeudor) {
        this.pEstadoDeudor = pEstadoDeudor;
    }


    /**
     * Gets the pCodTipoServicio value for this CrearPagoDetServicioReqType.
     * 
     * @return pCodTipoServicio
     */
    public java.lang.String getpCodTipoServicio() {
        return pCodTipoServicio;
    }


    /**
     * Sets the pCodTipoServicio value for this CrearPagoDetServicioReqType.
     * 
     * @param pCodTipoServicio
     */
    public void setpCodTipoServicio(java.lang.String pCodTipoServicio) {
        this.pCodTipoServicio = pCodTipoServicio;
    }


    /**
     * Gets the pNroDocs value for this CrearPagoDetServicioReqType.
     * 
     * @return pNroDocs
     */
    public java.math.BigDecimal getpNroDocs() {
        return pNroDocs;
    }


    /**
     * Sets the pNroDocs value for this CrearPagoDetServicioReqType.
     * 
     * @param pNroDocs
     */
    public void setpNroDocs(java.math.BigDecimal pNroDocs) {
        this.pNroDocs = pNroDocs;
    }


    /**
     * Gets the pMontoPagado value for this CrearPagoDetServicioReqType.
     * 
     * @return pMontoPagado
     */
    public java.math.BigDecimal getpMontoPagado() {
        return pMontoPagado;
    }


    /**
     * Sets the pMontoPagado value for this CrearPagoDetServicioReqType.
     * 
     * @param pMontoPagado
     */
    public void setpMontoPagado(java.math.BigDecimal pMontoPagado) {
        this.pMontoPagado = pMontoPagado;
    }


    /**
     * Gets the pDatoServicio value for this CrearPagoDetServicioReqType.
     * 
     * @return pDatoServicio
     */
    public java.lang.String getpDatoServicio() {
        return pDatoServicio;
    }


    /**
     * Sets the pDatoServicio value for this CrearPagoDetServicioReqType.
     * 
     * @param pDatoServicio
     */
    public void setpDatoServicio(java.lang.String pDatoServicio) {
        this.pDatoServicio = pDatoServicio;
    }


    /**
     * Gets the pDetalleDocs value for this CrearPagoDetServicioReqType.
     * 
     * @return pDetalleDocs
     */
    public com.inter.claroCollPayWS.stub.CrearPagoDetDocumentoReqType[] getpDetalleDocs() {
        return pDetalleDocs;
    }


    /**
     * Sets the pDetalleDocs value for this CrearPagoDetServicioReqType.
     * 
     * @param pDetalleDocs
     */
    public void setpDetalleDocs(com.inter.claroCollPayWS.stub.CrearPagoDetDocumentoReqType[] pDetalleDocs) {
        this.pDetalleDocs = pDetalleDocs;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CrearPagoDetServicioReqType)) return false;
        CrearPagoDetServicioReqType other = (CrearPagoDetServicioReqType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.pEstadoDeudor==null && other.getpEstadoDeudor()==null) || 
             (this.pEstadoDeudor!=null &&
              this.pEstadoDeudor.equals(other.getpEstadoDeudor()))) &&
            ((this.pCodTipoServicio==null && other.getpCodTipoServicio()==null) || 
             (this.pCodTipoServicio!=null &&
              this.pCodTipoServicio.equals(other.getpCodTipoServicio()))) &&
            ((this.pNroDocs==null && other.getpNroDocs()==null) || 
             (this.pNroDocs!=null &&
              this.pNroDocs.equals(other.getpNroDocs()))) &&
            ((this.pMontoPagado==null && other.getpMontoPagado()==null) || 
             (this.pMontoPagado!=null &&
              this.pMontoPagado.equals(other.getpMontoPagado()))) &&
            ((this.pDatoServicio==null && other.getpDatoServicio()==null) || 
             (this.pDatoServicio!=null &&
              this.pDatoServicio.equals(other.getpDatoServicio()))) &&
            ((this.pDetalleDocs==null && other.getpDetalleDocs()==null) || 
             (this.pDetalleDocs!=null &&
              java.util.Arrays.equals(this.pDetalleDocs, other.getpDetalleDocs())));
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
        if (getpEstadoDeudor() != null) {
            _hashCode += getpEstadoDeudor().hashCode();
        }
        if (getpCodTipoServicio() != null) {
            _hashCode += getpCodTipoServicio().hashCode();
        }
        if (getpNroDocs() != null) {
            _hashCode += getpNroDocs().hashCode();
        }
        if (getpMontoPagado() != null) {
            _hashCode += getpMontoPagado().hashCode();
        }
        if (getpDatoServicio() != null) {
            _hashCode += getpDatoServicio().hashCode();
        }
        if (getpDetalleDocs() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getpDetalleDocs());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getpDetalleDocs(), i);
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
        new org.apache.axis.description.TypeDesc(CrearPagoDetServicioReqType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "CrearPagoDetServicioReqType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pEstadoDeudor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pEstadoDeudor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pCodTipoServicio");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pCodTipoServicio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pNroDocs");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pNroDocs"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pMontoPagado");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pMontoPagado"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pDatoServicio");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pDatoServicio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pDetalleDocs");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pDetalleDocs"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "CrearPagoDetDocumentoReqType"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "pDetDocumento"));
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
