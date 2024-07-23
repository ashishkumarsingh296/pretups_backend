/**
 * EjecutarRecargaDatosResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroDataRechargeWS.stub;

public class EjecutarRecargaDatosResponse  implements java.io.Serializable {
    private java.lang.String idTransaccion;

    private java.lang.String codigoRespuesta;

    private java.lang.String mensajeRespuesta;

    private java.lang.String monto;

    private java.lang.String codTipoLinea;

    private java.lang.String nombreProducto;

    public EjecutarRecargaDatosResponse() {
    }

    public EjecutarRecargaDatosResponse(
           java.lang.String idTransaccion,
           java.lang.String codigoRespuesta,
           java.lang.String mensajeRespuesta,
           java.lang.String monto,
           java.lang.String codTipoLinea,
           java.lang.String nombreProducto) {
           this.idTransaccion = idTransaccion;
           this.codigoRespuesta = codigoRespuesta;
           this.mensajeRespuesta = mensajeRespuesta;
           this.monto = monto;
           this.codTipoLinea = codTipoLinea;
           this.nombreProducto = nombreProducto;
    }


    /**
     * Gets the idTransaccion value for this EjecutarRecargaDatosResponse.
     * 
     * @return idTransaccion
     */
    public java.lang.String getIdTransaccion() {
        return idTransaccion;
    }


    /**
     * Sets the idTransaccion value for this EjecutarRecargaDatosResponse.
     * 
     * @param idTransaccion
     */
    public void setIdTransaccion(java.lang.String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }


    /**
     * Gets the codigoRespuesta value for this EjecutarRecargaDatosResponse.
     * 
     * @return codigoRespuesta
     */
    public java.lang.String getCodigoRespuesta() {
        return codigoRespuesta;
    }


    /**
     * Sets the codigoRespuesta value for this EjecutarRecargaDatosResponse.
     * 
     * @param codigoRespuesta
     */
    public void setCodigoRespuesta(java.lang.String codigoRespuesta) {
        this.codigoRespuesta = codigoRespuesta;
    }


    /**
     * Gets the mensajeRespuesta value for this EjecutarRecargaDatosResponse.
     * 
     * @return mensajeRespuesta
     */
    public java.lang.String getMensajeRespuesta() {
        return mensajeRespuesta;
    }


    /**
     * Sets the mensajeRespuesta value for this EjecutarRecargaDatosResponse.
     * 
     * @param mensajeRespuesta
     */
    public void setMensajeRespuesta(java.lang.String mensajeRespuesta) {
        this.mensajeRespuesta = mensajeRespuesta;
    }


    /**
     * Gets the monto value for this EjecutarRecargaDatosResponse.
     * 
     * @return monto
     */
    public java.lang.String getMonto() {
        return monto;
    }


    /**
     * Sets the monto value for this EjecutarRecargaDatosResponse.
     * 
     * @param monto
     */
    public void setMonto(java.lang.String monto) {
        this.monto = monto;
    }


    /**
     * Gets the codTipoLinea value for this EjecutarRecargaDatosResponse.
     * 
     * @return codTipoLinea
     */
    public java.lang.String getCodTipoLinea() {
        return codTipoLinea;
    }


    /**
     * Sets the codTipoLinea value for this EjecutarRecargaDatosResponse.
     * 
     * @param codTipoLinea
     */
    public void setCodTipoLinea(java.lang.String codTipoLinea) {
        this.codTipoLinea = codTipoLinea;
    }


    /**
     * Gets the nombreProducto value for this EjecutarRecargaDatosResponse.
     * 
     * @return nombreProducto
     */
    public java.lang.String getNombreProducto() {
        return nombreProducto;
    }


    /**
     * Sets the nombreProducto value for this EjecutarRecargaDatosResponse.
     * 
     * @param nombreProducto
     */
    public void setNombreProducto(java.lang.String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EjecutarRecargaDatosResponse)) return false;
        EjecutarRecargaDatosResponse other = (EjecutarRecargaDatosResponse) obj;
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
            ((this.monto==null && other.getMonto()==null) || 
             (this.monto!=null &&
              this.monto.equals(other.getMonto()))) &&
            ((this.codTipoLinea==null && other.getCodTipoLinea()==null) || 
             (this.codTipoLinea!=null &&
              this.codTipoLinea.equals(other.getCodTipoLinea()))) &&
            ((this.nombreProducto==null && other.getNombreProducto()==null) || 
             (this.nombreProducto!=null &&
              this.nombreProducto.equals(other.getNombreProducto())));
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
        if (getMonto() != null) {
            _hashCode += getMonto().hashCode();
        }
        if (getCodTipoLinea() != null) {
            _hashCode += getCodTipoLinea().hashCode();
        }
        if (getNombreProducto() != null) {
            _hashCode += getNombreProducto().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EjecutarRecargaDatosResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", ">ejecutarRecargaDatosResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idTransaccion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", "idTransaccion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codigoRespuesta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", "codigoRespuesta"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mensajeRespuesta");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", "mensajeRespuesta"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("monto");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", "monto"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codTipoLinea");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", "codTipoLinea"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nombreProducto");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", "nombreProducto"));
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
