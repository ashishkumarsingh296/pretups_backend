/**
 * ConsultarDatosPINxTelefonoRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroPINRechargeWS.stub;

public class ConsultarDatosPINxTelefonoRequest  implements java.io.Serializable {
    private java.lang.String idTransaccion;

    private java.lang.String ipAplicacion;

    private java.lang.String nombreAplicacion;

    private java.lang.String MSISDN;

    public ConsultarDatosPINxTelefonoRequest() {
    }

    public ConsultarDatosPINxTelefonoRequest(
           java.lang.String idTransaccion,
           java.lang.String ipAplicacion,
           java.lang.String nombreAplicacion,
           java.lang.String MSISDN) {
           this.idTransaccion = idTransaccion;
           this.ipAplicacion = ipAplicacion;
           this.nombreAplicacion = nombreAplicacion;
           this.MSISDN = MSISDN;
    }


    /**
     * Gets the idTransaccion value for this ConsultarDatosPINxTelefonoRequest.
     * 
     * @return idTransaccion
     */
    public java.lang.String getIdTransaccion() {
        return idTransaccion;
    }


    /**
     * Sets the idTransaccion value for this ConsultarDatosPINxTelefonoRequest.
     * 
     * @param idTransaccion
     */
    public void setIdTransaccion(java.lang.String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }


    /**
     * Gets the ipAplicacion value for this ConsultarDatosPINxTelefonoRequest.
     * 
     * @return ipAplicacion
     */
    public java.lang.String getIpAplicacion() {
        return ipAplicacion;
    }


    /**
     * Sets the ipAplicacion value for this ConsultarDatosPINxTelefonoRequest.
     * 
     * @param ipAplicacion
     */
    public void setIpAplicacion(java.lang.String ipAplicacion) {
        this.ipAplicacion = ipAplicacion;
    }


    /**
     * Gets the nombreAplicacion value for this ConsultarDatosPINxTelefonoRequest.
     * 
     * @return nombreAplicacion
     */
    public java.lang.String getNombreAplicacion() {
        return nombreAplicacion;
    }


    /**
     * Sets the nombreAplicacion value for this ConsultarDatosPINxTelefonoRequest.
     * 
     * @param nombreAplicacion
     */
    public void setNombreAplicacion(java.lang.String nombreAplicacion) {
        this.nombreAplicacion = nombreAplicacion;
    }


    /**
     * Gets the MSISDN value for this ConsultarDatosPINxTelefonoRequest.
     * 
     * @return MSISDN
     */
    public java.lang.String getMSISDN() {
        return MSISDN;
    }


    /**
     * Sets the MSISDN value for this ConsultarDatosPINxTelefonoRequest.
     * 
     * @param MSISDN
     */
    public void setMSISDN(java.lang.String MSISDN) {
        this.MSISDN = MSISDN;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConsultarDatosPINxTelefonoRequest)) return false;
        ConsultarDatosPINxTelefonoRequest other = (ConsultarDatosPINxTelefonoRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.idTransaccion==null && other.getIdTransaccion()==null) || 
             (this.idTransaccion!=null &&
              this.idTransaccion.equals(other.getIdTransaccion()))) &&
            ((this.ipAplicacion==null && other.getIpAplicacion()==null) || 
             (this.ipAplicacion!=null &&
              this.ipAplicacion.equals(other.getIpAplicacion()))) &&
            ((this.nombreAplicacion==null && other.getNombreAplicacion()==null) || 
             (this.nombreAplicacion!=null &&
              this.nombreAplicacion.equals(other.getNombreAplicacion()))) &&
            ((this.MSISDN==null && other.getMSISDN()==null) || 
             (this.MSISDN!=null &&
              this.MSISDN.equals(other.getMSISDN())));
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
        if (getIdTransaccion() != null) {
            _hashCode += getIdTransaccion().hashCode();
        }
        if (getIpAplicacion() != null) {
            _hashCode += getIpAplicacion().hashCode();
        }
        if (getNombreAplicacion() != null) {
            _hashCode += getNombreAplicacion().hashCode();
        }
        if (getMSISDN() != null) {
            _hashCode += getMSISDN().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConsultarDatosPINxTelefonoRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/venta/PinVirtual", ">consultarDatosPINxTelefonoRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idTransaccion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/venta/PinVirtual", "idTransaccion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ipAplicacion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/venta/PinVirtual", "ipAplicacion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nombreAplicacion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/venta/PinVirtual", "nombreAplicacion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MSISDN");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/venta/PinVirtual", "MSISDN"));
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
