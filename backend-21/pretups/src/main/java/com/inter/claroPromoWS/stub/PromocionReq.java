/**
 * PromocionReq.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroPromoWS.stub;

public class PromocionReq  implements java.io.Serializable {
    private java.lang.String cantidad;

    private java.lang.String tipoPromocion;

    public PromocionReq() {
    }

    public PromocionReq(
           java.lang.String cantidad,
           java.lang.String tipoPromocion) {
           this.cantidad = cantidad;
           this.tipoPromocion = tipoPromocion;
    }


    /**
     * Gets the cantidad value for this PromocionReq.
     * 
     * @return cantidad
     */
    public java.lang.String getCantidad() {
        return cantidad;
    }


    /**
     * Sets the cantidad value for this PromocionReq.
     * 
     * @param cantidad
     */
    public void setCantidad(java.lang.String cantidad) {
        this.cantidad = cantidad;
    }


    /**
     * Gets the tipoPromocion value for this PromocionReq.
     * 
     * @return tipoPromocion
     */
    public java.lang.String getTipoPromocion() {
        return tipoPromocion;
    }


    /**
     * Sets the tipoPromocion value for this PromocionReq.
     * 
     * @param tipoPromocion
     */
    public void setTipoPromocion(java.lang.String tipoPromocion) {
        this.tipoPromocion = tipoPromocion;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PromocionReq)) return false;
        PromocionReq other = (PromocionReq) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.cantidad==null && other.getCantidad()==null) || 
             (this.cantidad!=null &&
              this.cantidad.equals(other.getCantidad()))) &&
            ((this.tipoPromocion==null && other.getTipoPromocion()==null) || 
             (this.tipoPromocion!=null &&
              this.tipoPromocion.equals(other.getTipoPromocion())));
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
        if (getCantidad() != null) {
            _hashCode += getCantidad().hashCode();
        }
        if (getTipoPromocion() != null) {
            _hashCode += getTipoPromocion().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PromocionReq.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/promocion/EntregaPromocion/ws", "promocionReq"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cantidad");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/promocion/EntregaPromocion/ws", "cantidad"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tipoPromocion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ebs/promocion/EntregaPromocion/ws", "tipoPromocion"));
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
