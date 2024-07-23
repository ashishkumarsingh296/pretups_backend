/**
 * CrearPagoDetServicioRespType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroCollPayWS.stub;

public class CrearPagoDetServicioRespType  implements java.io.Serializable {
    private java.math.BigDecimal xNroDocsPagado;

    private java.math.BigDecimal xMontoTotalPagado;

    private java.lang.String xCodMoneda;

    private java.lang.String xCodTipoServicio;

    private java.lang.String xOpcionRecaudacion;

    private java.lang.String xDatoServicio;

    private java.lang.String xMensaje1;

    private java.lang.String xMensaje2;

    private com.inter.claroCollPayWS.stub.CrearPagoDetDocumentoRespType[] xDetalleDocs;

    public CrearPagoDetServicioRespType() {
    }

    public CrearPagoDetServicioRespType(
           java.math.BigDecimal xNroDocsPagado,
           java.math.BigDecimal xMontoTotalPagado,
           java.lang.String xCodMoneda,
           java.lang.String xCodTipoServicio,
           java.lang.String xOpcionRecaudacion,
           java.lang.String xDatoServicio,
           java.lang.String xMensaje1,
           java.lang.String xMensaje2,
           com.inter.claroCollPayWS.stub.CrearPagoDetDocumentoRespType[] xDetalleDocs) {
           this.xNroDocsPagado = xNroDocsPagado;
           this.xMontoTotalPagado = xMontoTotalPagado;
           this.xCodMoneda = xCodMoneda;
           this.xCodTipoServicio = xCodTipoServicio;
           this.xOpcionRecaudacion = xOpcionRecaudacion;
           this.xDatoServicio = xDatoServicio;
           this.xMensaje1 = xMensaje1;
           this.xMensaje2 = xMensaje2;
           this.xDetalleDocs = xDetalleDocs;
    }


    /**
     * Gets the xNroDocsPagado value for this CrearPagoDetServicioRespType.
     * 
     * @return xNroDocsPagado
     */
    public java.math.BigDecimal getXNroDocsPagado() {
        return xNroDocsPagado;
    }


    /**
     * Sets the xNroDocsPagado value for this CrearPagoDetServicioRespType.
     * 
     * @param xNroDocsPagado
     */
    public void setXNroDocsPagado(java.math.BigDecimal xNroDocsPagado) {
        this.xNroDocsPagado = xNroDocsPagado;
    }


    /**
     * Gets the xMontoTotalPagado value for this CrearPagoDetServicioRespType.
     * 
     * @return xMontoTotalPagado
     */
    public java.math.BigDecimal getXMontoTotalPagado() {
        return xMontoTotalPagado;
    }


    /**
     * Sets the xMontoTotalPagado value for this CrearPagoDetServicioRespType.
     * 
     * @param xMontoTotalPagado
     */
    public void setXMontoTotalPagado(java.math.BigDecimal xMontoTotalPagado) {
        this.xMontoTotalPagado = xMontoTotalPagado;
    }


    /**
     * Gets the xCodMoneda value for this CrearPagoDetServicioRespType.
     * 
     * @return xCodMoneda
     */
    public java.lang.String getXCodMoneda() {
        return xCodMoneda;
    }


    /**
     * Sets the xCodMoneda value for this CrearPagoDetServicioRespType.
     * 
     * @param xCodMoneda
     */
    public void setXCodMoneda(java.lang.String xCodMoneda) {
        this.xCodMoneda = xCodMoneda;
    }


    /**
     * Gets the xCodTipoServicio value for this CrearPagoDetServicioRespType.
     * 
     * @return xCodTipoServicio
     */
    public java.lang.String getXCodTipoServicio() {
        return xCodTipoServicio;
    }


    /**
     * Sets the xCodTipoServicio value for this CrearPagoDetServicioRespType.
     * 
     * @param xCodTipoServicio
     */
    public void setXCodTipoServicio(java.lang.String xCodTipoServicio) {
        this.xCodTipoServicio = xCodTipoServicio;
    }


    /**
     * Gets the xOpcionRecaudacion value for this CrearPagoDetServicioRespType.
     * 
     * @return xOpcionRecaudacion
     */
    public java.lang.String getXOpcionRecaudacion() {
        return xOpcionRecaudacion;
    }


    /**
     * Sets the xOpcionRecaudacion value for this CrearPagoDetServicioRespType.
     * 
     * @param xOpcionRecaudacion
     */
    public void setXOpcionRecaudacion(java.lang.String xOpcionRecaudacion) {
        this.xOpcionRecaudacion = xOpcionRecaudacion;
    }


    /**
     * Gets the xDatoServicio value for this CrearPagoDetServicioRespType.
     * 
     * @return xDatoServicio
     */
    public java.lang.String getXDatoServicio() {
        return xDatoServicio;
    }


    /**
     * Sets the xDatoServicio value for this CrearPagoDetServicioRespType.
     * 
     * @param xDatoServicio
     */
    public void setXDatoServicio(java.lang.String xDatoServicio) {
        this.xDatoServicio = xDatoServicio;
    }


    /**
     * Gets the xMensaje1 value for this CrearPagoDetServicioRespType.
     * 
     * @return xMensaje1
     */
    public java.lang.String getXMensaje1() {
        return xMensaje1;
    }


    /**
     * Sets the xMensaje1 value for this CrearPagoDetServicioRespType.
     * 
     * @param xMensaje1
     */
    public void setXMensaje1(java.lang.String xMensaje1) {
        this.xMensaje1 = xMensaje1;
    }


    /**
     * Gets the xMensaje2 value for this CrearPagoDetServicioRespType.
     * 
     * @return xMensaje2
     */
    public java.lang.String getXMensaje2() {
        return xMensaje2;
    }


    /**
     * Sets the xMensaje2 value for this CrearPagoDetServicioRespType.
     * 
     * @param xMensaje2
     */
    public void setXMensaje2(java.lang.String xMensaje2) {
        this.xMensaje2 = xMensaje2;
    }


    /**
     * Gets the xDetalleDocs value for this CrearPagoDetServicioRespType.
     * 
     * @return xDetalleDocs
     */
    public com.inter.claroCollPayWS.stub.CrearPagoDetDocumentoRespType[] getXDetalleDocs() {
        return xDetalleDocs;
    }


    /**
     * Sets the xDetalleDocs value for this CrearPagoDetServicioRespType.
     * 
     * @param xDetalleDocs
     */
    public void setXDetalleDocs(com.inter.claroCollPayWS.stub.CrearPagoDetDocumentoRespType[] xDetalleDocs) {
        this.xDetalleDocs = xDetalleDocs;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CrearPagoDetServicioRespType)) return false;
        CrearPagoDetServicioRespType other = (CrearPagoDetServicioRespType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.xNroDocsPagado==null && other.getXNroDocsPagado()==null) || 
             (this.xNroDocsPagado!=null &&
              this.xNroDocsPagado.equals(other.getXNroDocsPagado()))) &&
            ((this.xMontoTotalPagado==null && other.getXMontoTotalPagado()==null) || 
             (this.xMontoTotalPagado!=null &&
              this.xMontoTotalPagado.equals(other.getXMontoTotalPagado()))) &&
            ((this.xCodMoneda==null && other.getXCodMoneda()==null) || 
             (this.xCodMoneda!=null &&
              this.xCodMoneda.equals(other.getXCodMoneda()))) &&
            ((this.xCodTipoServicio==null && other.getXCodTipoServicio()==null) || 
             (this.xCodTipoServicio!=null &&
              this.xCodTipoServicio.equals(other.getXCodTipoServicio()))) &&
            ((this.xOpcionRecaudacion==null && other.getXOpcionRecaudacion()==null) || 
             (this.xOpcionRecaudacion!=null &&
              this.xOpcionRecaudacion.equals(other.getXOpcionRecaudacion()))) &&
            ((this.xDatoServicio==null && other.getXDatoServicio()==null) || 
             (this.xDatoServicio!=null &&
              this.xDatoServicio.equals(other.getXDatoServicio()))) &&
            ((this.xMensaje1==null && other.getXMensaje1()==null) || 
             (this.xMensaje1!=null &&
              this.xMensaje1.equals(other.getXMensaje1()))) &&
            ((this.xMensaje2==null && other.getXMensaje2()==null) || 
             (this.xMensaje2!=null &&
              this.xMensaje2.equals(other.getXMensaje2()))) &&
            ((this.xDetalleDocs==null && other.getXDetalleDocs()==null) || 
             (this.xDetalleDocs!=null &&
              java.util.Arrays.equals(this.xDetalleDocs, other.getXDetalleDocs())));
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
        if (getXNroDocsPagado() != null) {
            _hashCode += getXNroDocsPagado().hashCode();
        }
        if (getXMontoTotalPagado() != null) {
            _hashCode += getXMontoTotalPagado().hashCode();
        }
        if (getXCodMoneda() != null) {
            _hashCode += getXCodMoneda().hashCode();
        }
        if (getXCodTipoServicio() != null) {
            _hashCode += getXCodTipoServicio().hashCode();
        }
        if (getXOpcionRecaudacion() != null) {
            _hashCode += getXOpcionRecaudacion().hashCode();
        }
        if (getXDatoServicio() != null) {
            _hashCode += getXDatoServicio().hashCode();
        }
        if (getXMensaje1() != null) {
            _hashCode += getXMensaje1().hashCode();
        }
        if (getXMensaje2() != null) {
            _hashCode += getXMensaje2().hashCode();
        }
        if (getXDetalleDocs() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getXDetalleDocs());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getXDetalleDocs(), i);
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
        new org.apache.axis.description.TypeDesc(CrearPagoDetServicioRespType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "CrearPagoDetServicioRespType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XNroDocsPagado");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xNroDocsPagado"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XMontoTotalPagado");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xMontoTotalPagado"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XCodMoneda");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xCodMoneda"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XCodTipoServicio");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xCodTipoServicio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XOpcionRecaudacion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xOpcionRecaudacion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XDatoServicio");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xDatoServicio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XMensaje1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xMensaje1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XMensaje2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xMensaje2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XDetalleDocs");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xDetalleDocs"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "CrearPagoDetDocumentoRespType"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xDetDocumento"));
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
