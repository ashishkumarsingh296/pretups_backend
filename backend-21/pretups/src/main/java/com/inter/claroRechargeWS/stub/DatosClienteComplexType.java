/**
 * DatosClienteComplexType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroRechargeWS.stub;

public class DatosClienteComplexType  implements java.io.Serializable {
    private java.lang.String saldoActual;

    private java.lang.String fechaExpiracion;

    private java.lang.String diasVigencia;

    public DatosClienteComplexType() {
    }

    public DatosClienteComplexType(
           java.lang.String saldoActual,
           java.lang.String fechaExpiracion,
           java.lang.String diasVigencia) {
           this.saldoActual = saldoActual;
           this.fechaExpiracion = fechaExpiracion;
           this.diasVigencia = diasVigencia;
    }


    /**
     * Gets the saldoActual value for this DatosClienteComplexType.
     * 
     * @return saldoActual
     */
    public java.lang.String getSaldoActual() {
        return saldoActual;
    }


    /**
     * Sets the saldoActual value for this DatosClienteComplexType.
     * 
     * @param saldoActual
     */
    public void setSaldoActual(java.lang.String saldoActual) {
        this.saldoActual = saldoActual;
    }


    /**
     * Gets the fechaExpiracion value for this DatosClienteComplexType.
     * 
     * @return fechaExpiracion
     */
    public java.lang.String getFechaExpiracion() {
        return fechaExpiracion;
    }


    /**
     * Sets the fechaExpiracion value for this DatosClienteComplexType.
     * 
     * @param fechaExpiracion
     */
    public void setFechaExpiracion(java.lang.String fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }


    /**
     * Gets the diasVigencia value for this DatosClienteComplexType.
     * 
     * @return diasVigencia
     */
    public java.lang.String getDiasVigencia() {
        return diasVigencia;
    }


    /**
     * Sets the diasVigencia value for this DatosClienteComplexType.
     * 
     * @param diasVigencia
     */
    public void setDiasVigencia(java.lang.String diasVigencia) {
        this.diasVigencia = diasVigencia;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DatosClienteComplexType)) return false;
        DatosClienteComplexType other = (DatosClienteComplexType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.saldoActual==null && other.getSaldoActual()==null) || 
             (this.saldoActual!=null &&
              this.saldoActual.equals(other.getSaldoActual()))) &&
            ((this.fechaExpiracion==null && other.getFechaExpiracion()==null) || 
             (this.fechaExpiracion!=null &&
              this.fechaExpiracion.equals(other.getFechaExpiracion()))) &&
            ((this.diasVigencia==null && other.getDiasVigencia()==null) || 
             (this.diasVigencia!=null &&
              this.diasVigencia.equals(other.getDiasVigencia())));
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
        if (getSaldoActual() != null) {
            _hashCode += getSaldoActual().hashCode();
        }
        if (getFechaExpiracion() != null) {
            _hashCode += getFechaExpiracion().hashCode();
        }
        if (getDiasVigencia() != null) {
            _hashCode += getDiasVigencia().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DatosClienteComplexType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "DatosClienteComplexType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("saldoActual");
        elemField.setXmlName(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "saldoActual"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fechaExpiracion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "fechaExpiracion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("diasVigencia");
        elemField.setXmlName(new javax.xml.namespace.QName("http://services.eai.claro.com.pe/ebsRecargaVirtualWS", "diasVigencia"));
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
