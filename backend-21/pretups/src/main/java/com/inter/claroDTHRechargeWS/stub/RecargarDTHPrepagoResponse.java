/**
 * RecargarDTHPrepagoResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroDTHRechargeWS.stub;

public class RecargarDTHPrepagoResponse  implements java.io.Serializable {
    private java.lang.String idTransaccion;

    private java.lang.String codigoRespuesta;

    private java.lang.String mensajeRespuesta;

    private java.lang.String numeroReferencia;

    private java.lang.String tipoProducto;

    private double saldo;

    private java.lang.String fechaVigenciaMonedero;

    public RecargarDTHPrepagoResponse() {
    }

    public RecargarDTHPrepagoResponse(
           java.lang.String idTransaccion,
           java.lang.String codigoRespuesta,
           java.lang.String mensajeRespuesta,
           java.lang.String numeroReferencia,
           java.lang.String tipoProducto,
           double saldo,
           java.lang.String fechaVigenciaMonedero) {
           this.idTransaccion = idTransaccion;
           this.codigoRespuesta = codigoRespuesta;
           this.mensajeRespuesta = mensajeRespuesta;
           this.numeroReferencia = numeroReferencia;
           this.tipoProducto = tipoProducto;
           this.saldo = saldo;
           this.fechaVigenciaMonedero = fechaVigenciaMonedero;
    }


    /**
     * Gets the idTransaccion value for this RecargarDTHPrepagoResponse.
     * 
     * @return idTransaccion
     */
    public java.lang.String getIdTransaccion() {
        return idTransaccion;
    }


    /**
     * Sets the idTransaccion value for this RecargarDTHPrepagoResponse.
     * 
     * @param idTransaccion
     */
    public void setIdTransaccion(java.lang.String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }


    /**
     * Gets the codigoRespuesta value for this RecargarDTHPrepagoResponse.
     * 
     * @return codigoRespuesta
     */
    public java.lang.String getCodigoRespuesta() {
        return codigoRespuesta;
    }


    /**
     * Sets the codigoRespuesta value for this RecargarDTHPrepagoResponse.
     * 
     * @param codigoRespuesta
     */
    public void setCodigoRespuesta(java.lang.String codigoRespuesta) {
        this.codigoRespuesta = codigoRespuesta;
    }


    /**
     * Gets the mensajeRespuesta value for this RecargarDTHPrepagoResponse.
     * 
     * @return mensajeRespuesta
     */
    public java.lang.String getMensajeRespuesta() {
        return mensajeRespuesta;
    }


    /**
     * Sets the mensajeRespuesta value for this RecargarDTHPrepagoResponse.
     * 
     * @param mensajeRespuesta
     */
    public void setMensajeRespuesta(java.lang.String mensajeRespuesta) {
        this.mensajeRespuesta = mensajeRespuesta;
    }


    /**
     * Gets the numeroReferencia value for this RecargarDTHPrepagoResponse.
     * 
     * @return numeroReferencia
     */
    public java.lang.String getNumeroReferencia() {
        return numeroReferencia;
    }


    /**
     * Sets the numeroReferencia value for this RecargarDTHPrepagoResponse.
     * 
     * @param numeroReferencia
     */
    public void setNumeroReferencia(java.lang.String numeroReferencia) {
        this.numeroReferencia = numeroReferencia;
    }


    /**
     * Gets the tipoProducto value for this RecargarDTHPrepagoResponse.
     * 
     * @return tipoProducto
     */
    public java.lang.String getTipoProducto() {
        return tipoProducto;
    }


    /**
     * Sets the tipoProducto value for this RecargarDTHPrepagoResponse.
     * 
     * @param tipoProducto
     */
    public void setTipoProducto(java.lang.String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }


    /**
     * Gets the saldo value for this RecargarDTHPrepagoResponse.
     * 
     * @return saldo
     */
    public double getSaldo() {
        return saldo;
    }


    /**
     * Sets the saldo value for this RecargarDTHPrepagoResponse.
     * 
     * @param saldo
     */
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }


    /**
     * Gets the fechaVigenciaMonedero value for this RecargarDTHPrepagoResponse.
     * 
     * @return fechaVigenciaMonedero
     */
    public java.lang.String getFechaVigenciaMonedero() {
        return fechaVigenciaMonedero;
    }


    /**
     * Sets the fechaVigenciaMonedero value for this RecargarDTHPrepagoResponse.
     * 
     * @param fechaVigenciaMonedero
     */
    public void setFechaVigenciaMonedero(java.lang.String fechaVigenciaMonedero) {
        this.fechaVigenciaMonedero = fechaVigenciaMonedero;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RecargarDTHPrepagoResponse)) return false;
        RecargarDTHPrepagoResponse other = (RecargarDTHPrepagoResponse) obj;
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
              this.numeroReferencia.equals(other.getNumeroReferencia()))) &&
            ((this.tipoProducto==null && other.getTipoProducto()==null) || 
             (this.tipoProducto!=null &&
              this.tipoProducto.equals(other.getTipoProducto()))) &&
            this.saldo == other.getSaldo() &&
            ((this.fechaVigenciaMonedero==null && other.getFechaVigenciaMonedero()==null) || 
             (this.fechaVigenciaMonedero!=null &&
              this.fechaVigenciaMonedero.equals(other.getFechaVigenciaMonedero())));
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
        if (getTipoProducto() != null) {
            _hashCode += getTipoProducto().hashCode();
        }
        _hashCode += new Double(getSaldo()).hashCode();
        if (getFechaVigenciaMonedero() != null) {
            _hashCode += getFechaVigenciaMonedero().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RecargarDTHPrepagoResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/ws/ventas/transacciondthprepago", ">recargarDTHPrepagoResponse"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tipoProducto");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ws/ventas/transacciondthprepago", "tipoProducto"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("saldo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ws/ventas/transacciondthprepago", "saldo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fechaVigenciaMonedero");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/ws/ventas/transacciondthprepago", "fechaVigenciaMonedero"));
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
