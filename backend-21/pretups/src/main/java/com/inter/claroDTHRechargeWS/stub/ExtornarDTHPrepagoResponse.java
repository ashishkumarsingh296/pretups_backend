/**
 * ExtornarDTHPrepagoResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroDTHRechargeWS.stub;

public class ExtornarDTHPrepagoResponse  implements java.io.Serializable {
    private java.lang.String idTransaccion;

    private java.lang.String codigoRespuesta;

    private java.lang.String mensajeRespuesta;

    private java.lang.String numeroReferencia;

    public ExtornarDTHPrepagoResponse() {
    }

    public ExtornarDTHPrepagoResponse(
           java.lang.String idTransaccion,
           java.lang.String codigoRespuesta,
           java.lang.String mensajeRespuesta,
           java.lang.String numeroReferencia) {
           this.idTransaccion = idTransaccion;
           this.codigoRespuesta = codigoRespuesta;
           this.mensajeRespuesta = mensajeRespuesta;
           this.numeroReferencia = numeroReferencia;
    }


    /**
     * Gets the idTransaccion value for this ExtornarDTHPrepagoResponse.
     * 
     * @return idTransaccion
     */
    public java.lang.String getIdTransaccion() {
        return idTransaccion;
    }


    /**
     * Sets the idTransaccion value for this ExtornarDTHPrepagoResponse.
     * 
     * @param idTransaccion
     */
    public void setIdTransaccion(java.lang.String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }


    /**
     * Gets the codigoRespuesta value for this ExtornarDTHPrepagoResponse.
     * 
     * @return codigoRespuesta
     */
    public java.lang.String getCodigoRespuesta() {
        return codigoRespuesta;
    }


    /**
     * Sets the codigoRespuesta value for this ExtornarDTHPrepagoResponse.
     * 
     * @param codigoRespuesta
     */
    public void setCodigoRespuesta(java.lang.String codigoRespuesta) {
        this.codigoRespuesta = codigoRespuesta;
    }


    /**
     * Gets the mensajeRespuesta value for this ExtornarDTHPrepagoResponse.
     * 
     * @return mensajeRespuesta
     */
    public java.lang.String getMensajeRespuesta() {
        return mensajeRespuesta;
    }


    /**
     * Sets the mensajeRespuesta value for this ExtornarDTHPrepagoResponse.
     * 
     * @param mensajeRespuesta
     */
    public void setMensajeRespuesta(java.lang.String mensajeRespuesta) {
        this.mensajeRespuesta = mensajeRespuesta;
    }


    /**
     * Gets the numeroReferencia value for this ExtornarDTHPrepagoResponse.
     * 
     * @return numeroReferencia
     */
    public java.lang.String getNumeroReferencia() {
        return numeroReferencia;
    }


    /**
     * Sets the numeroReferencia value for this ExtornarDTHPrepagoResponse.
     * 
     * @param numeroReferencia
     */
    public void setNumeroReferencia(java.lang.String numeroReferencia) {
        this.numeroReferencia = numeroReferencia;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ExtornarDTHPrepagoResponse)) return false;
        ExtornarDTHPrepagoResponse other = (ExtornarDTHPrepagoResponse) obj;
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
            ((this.codigoRespuesta==null && other.getCodigoRespuesta()==null) || 
             (this.codigoRespuesta!=null &&
              this.codigoRespuesta.equals(other.getCodigoRespuesta()))) &&
            ((this.mensajeRespuesta==null && other.getMensajeRespuesta()==null) || 
             (this.mensajeRespuesta!=null &&
              this.mensajeRespuesta.equals(other.getMensajeRespuesta()))) &&
            ((this.numeroReferencia==null && other.getNumeroReferencia()==null) || 
             (this.numeroReferencia!=null &&
              this.numeroReferencia.equals(other.getNumeroReferencia())));
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
        if (getCodigoRespuesta() != null) {
            _hashCode += getCodigoRespuesta().hashCode();
        }
        if (getMensajeRespuesta() != null) {
            _hashCode += getMensajeRespuesta().hashCode();
        }
        if (getNumeroReferencia() != null) {
            _hashCode += getNumeroReferencia().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ExtornarDTHPrepagoResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/ws/ventas/transacciondthprepago", ">extornarDTHPrepagoResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idTransaccion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ws/ventas/transacciondthprepago", "idTransaccion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codigoRespuesta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ws/ventas/transacciondthprepago", "codigoRespuesta"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mensajeRespuesta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ws/ventas/transacciondthprepago", "mensajeRespuesta"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numeroReferencia");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ws/ventas/transacciondthprepago", "numeroReferencia"));
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
