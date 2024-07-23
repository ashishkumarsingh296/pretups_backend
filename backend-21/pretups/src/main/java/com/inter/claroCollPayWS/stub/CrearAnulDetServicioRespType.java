/**
 * CrearAnulDetServicioRespType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroCollPayWS.stub;

public class CrearAnulDetServicioRespType  implements java.io.Serializable {
    private java.math.BigDecimal xNroDocsAnul;

    private java.math.BigDecimal xMontoTotalAnul;

    private java.lang.String xCodMoneda;

    private java.lang.String xCodTipoServicio;

    private java.lang.String xOpcionRecaudacion;

    private java.lang.String xMensaje1;

    private java.lang.String xMensaje2;

    private com.inter.claroCollPayWS.stub.CrearAnulDetDocumentoRespType[] xDetalleDocs;

    public CrearAnulDetServicioRespType() {
    }

    public CrearAnulDetServicioRespType(
           java.math.BigDecimal xNroDocsAnul,
           java.math.BigDecimal xMontoTotalAnul,
           java.lang.String xCodMoneda,
           java.lang.String xCodTipoServicio,
           java.lang.String xOpcionRecaudacion,
           java.lang.String xMensaje1,
           java.lang.String xMensaje2,
           com.inter.claroCollPayWS.stub.CrearAnulDetDocumentoRespType[] xDetalleDocs) {
           this.xNroDocsAnul = xNroDocsAnul;
           this.xMontoTotalAnul = xMontoTotalAnul;
           this.xCodMoneda = xCodMoneda;
           this.xCodTipoServicio = xCodTipoServicio;
           this.xOpcionRecaudacion = xOpcionRecaudacion;
           this.xMensaje1 = xMensaje1;
           this.xMensaje2 = xMensaje2;
           this.xDetalleDocs = xDetalleDocs;
    }


    /**
     * Gets the xNroDocsAnul value for this CrearAnulDetServicioRespType.
     * 
     * @return xNroDocsAnul
     */
    public java.math.BigDecimal getXNroDocsAnul() {
        return xNroDocsAnul;
    }


    /**
     * Sets the xNroDocsAnul value for this CrearAnulDetServicioRespType.
     * 
     * @param xNroDocsAnul
     */
    public void setXNroDocsAnul(java.math.BigDecimal xNroDocsAnul) {
        this.xNroDocsAnul = xNroDocsAnul;
    }


    /**
     * Gets the xMontoTotalAnul value for this CrearAnulDetServicioRespType.
     * 
     * @return xMontoTotalAnul
     */
    public java.math.BigDecimal getXMontoTotalAnul() {
        return xMontoTotalAnul;
    }


    /**
     * Sets the xMontoTotalAnul value for this CrearAnulDetServicioRespType.
     * 
     * @param xMontoTotalAnul
     */
    public void setXMontoTotalAnul(java.math.BigDecimal xMontoTotalAnul) {
        this.xMontoTotalAnul = xMontoTotalAnul;
    }


    /**
     * Gets the xCodMoneda value for this CrearAnulDetServicioRespType.
     * 
     * @return xCodMoneda
     */
    public java.lang.String getXCodMoneda() {
        return xCodMoneda;
    }


    /**
     * Sets the xCodMoneda value for this CrearAnulDetServicioRespType.
     * 
     * @param xCodMoneda
     */
    public void setXCodMoneda(java.lang.String xCodMoneda) {
        this.xCodMoneda = xCodMoneda;
    }


    /**
     * Gets the xCodTipoServicio value for this CrearAnulDetServicioRespType.
     * 
     * @return xCodTipoServicio
     */
    public java.lang.String getXCodTipoServicio() {
        return xCodTipoServicio;
    }


    /**
     * Sets the xCodTipoServicio value for this CrearAnulDetServicioRespType.
     * 
     * @param xCodTipoServicio
     */
    public void setXCodTipoServicio(java.lang.String xCodTipoServicio) {
        this.xCodTipoServicio = xCodTipoServicio;
    }


    /**
     * Gets the xOpcionRecaudacion value for this CrearAnulDetServicioRespType.
     * 
     * @return xOpcionRecaudacion
     */
    public java.lang.String getXOpcionRecaudacion() {
        return xOpcionRecaudacion;
    }


    /**
     * Sets the xOpcionRecaudacion value for this CrearAnulDetServicioRespType.
     * 
     * @param xOpcionRecaudacion
     */
    public void setXOpcionRecaudacion(java.lang.String xOpcionRecaudacion) {
        this.xOpcionRecaudacion = xOpcionRecaudacion;
    }


    /**
     * Gets the xMensaje1 value for this CrearAnulDetServicioRespType.
     * 
     * @return xMensaje1
     */
    public java.lang.String getXMensaje1() {
        return xMensaje1;
    }


    /**
     * Sets the xMensaje1 value for this CrearAnulDetServicioRespType.
     * 
     * @param xMensaje1
     */
    public void setXMensaje1(java.lang.String xMensaje1) {
        this.xMensaje1 = xMensaje1;
    }


    /**
     * Gets the xMensaje2 value for this CrearAnulDetServicioRespType.
     * 
     * @return xMensaje2
     */
    public java.lang.String getXMensaje2() {
        return xMensaje2;
    }


    /**
     * Sets the xMensaje2 value for this CrearAnulDetServicioRespType.
     * 
     * @param xMensaje2
     */
    public void setXMensaje2(java.lang.String xMensaje2) {
        this.xMensaje2 = xMensaje2;
    }


    /**
     * Gets the xDetalleDocs value for this CrearAnulDetServicioRespType.
     * 
     * @return xDetalleDocs
     */
    public com.inter.claroCollPayWS.stub.CrearAnulDetDocumentoRespType[] getXDetalleDocs() {
        return xDetalleDocs;
    }


    /**
     * Sets the xDetalleDocs value for this CrearAnulDetServicioRespType.
     * 
     * @param xDetalleDocs
     */
    public void setXDetalleDocs(com.inter.claroCollPayWS.stub.CrearAnulDetDocumentoRespType[] xDetalleDocs) {
        this.xDetalleDocs = xDetalleDocs;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CrearAnulDetServicioRespType)) return false;
        CrearAnulDetServicioRespType other = (CrearAnulDetServicioRespType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.xNroDocsAnul==null && other.getXNroDocsAnul()==null) || 
             (this.xNroDocsAnul!=null &&
              this.xNroDocsAnul.equals(other.getXNroDocsAnul()))) &&
            ((this.xMontoTotalAnul==null && other.getXMontoTotalAnul()==null) || 
             (this.xMontoTotalAnul!=null &&
              this.xMontoTotalAnul.equals(other.getXMontoTotalAnul()))) &&
            ((this.xCodMoneda==null && other.getXCodMoneda()==null) || 
             (this.xCodMoneda!=null &&
              this.xCodMoneda.equals(other.getXCodMoneda()))) &&
            ((this.xCodTipoServicio==null && other.getXCodTipoServicio()==null) || 
             (this.xCodTipoServicio!=null &&
              this.xCodTipoServicio.equals(other.getXCodTipoServicio()))) &&
            ((this.xOpcionRecaudacion==null && other.getXOpcionRecaudacion()==null) || 
             (this.xOpcionRecaudacion!=null &&
              this.xOpcionRecaudacion.equals(other.getXOpcionRecaudacion()))) &&
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
        if (getXNroDocsAnul() != null) {
            _hashCode += getXNroDocsAnul().hashCode();
        }
        if (getXMontoTotalAnul() != null) {
            _hashCode += getXMontoTotalAnul().hashCode();
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
        new org.apache.axis.description.TypeDesc(CrearAnulDetServicioRespType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "CrearAnulDetServicioRespType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XNroDocsAnul");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xNroDocsAnul"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XMontoTotalAnul");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xMontoTotalAnul"));
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
        elemField.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "CrearAnulDetDocumentoRespType"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://claro.com.pe/eai/oac/transaccionpagos/", "xDeudaDocumento"));
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
