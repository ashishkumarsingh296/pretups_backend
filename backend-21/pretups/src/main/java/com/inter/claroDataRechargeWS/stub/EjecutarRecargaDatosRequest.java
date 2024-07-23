/**
 * EjecutarRecargaDatosRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroDataRechargeWS.stub;

public class EjecutarRecargaDatosRequest  implements java.io.Serializable {
    private java.lang.String idTransaccion;

    private java.lang.String ipAplicacion;

    private java.lang.String nombreAplicacion;

    private java.lang.String fechaTX;

    private java.lang.String horaTX;

    private java.lang.String binAdquiriente;

    private java.lang.String forwardInstitution;

    private java.lang.String producto;

    private java.lang.String telefono;

    private java.lang.String monto;

    public EjecutarRecargaDatosRequest() {
    }

    public EjecutarRecargaDatosRequest(
           java.lang.String idTransaccion,
           java.lang.String ipAplicacion,
           java.lang.String nombreAplicacion,
           java.lang.String fechaTX,
           java.lang.String horaTX,
           java.lang.String binAdquiriente,
           java.lang.String forwardInstitution,
           java.lang.String producto,
           java.lang.String telefono,
           java.lang.String monto) {
           this.idTransaccion = idTransaccion;
           this.ipAplicacion = ipAplicacion;
           this.nombreAplicacion = nombreAplicacion;
           this.fechaTX = fechaTX;
           this.horaTX = horaTX;
           this.binAdquiriente = binAdquiriente;
           this.forwardInstitution = forwardInstitution;
           this.producto = producto;
           this.telefono = telefono;
           this.monto = monto;
    }


    /**
     * Gets the idTransaccion value for this EjecutarRecargaDatosRequest.
     * 
     * @return idTransaccion
     */
    public java.lang.String getIdTransaccion() {
        return idTransaccion;
    }


    /**
     * Sets the idTransaccion value for this EjecutarRecargaDatosRequest.
     * 
     * @param idTransaccion
     */
    public void setIdTransaccion(java.lang.String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }


    /**
     * Gets the ipAplicacion value for this EjecutarRecargaDatosRequest.
     * 
     * @return ipAplicacion
     */
    public java.lang.String getIpAplicacion() {
        return ipAplicacion;
    }


    /**
     * Sets the ipAplicacion value for this EjecutarRecargaDatosRequest.
     * 
     * @param ipAplicacion
     */
    public void setIpAplicacion(java.lang.String ipAplicacion) {
        this.ipAplicacion = ipAplicacion;
    }


    /**
     * Gets the nombreAplicacion value for this EjecutarRecargaDatosRequest.
     * 
     * @return nombreAplicacion
     */
    public java.lang.String getNombreAplicacion() {
        return nombreAplicacion;
    }


    /**
     * Sets the nombreAplicacion value for this EjecutarRecargaDatosRequest.
     * 
     * @param nombreAplicacion
     */
    public void setNombreAplicacion(java.lang.String nombreAplicacion) {
        this.nombreAplicacion = nombreAplicacion;
    }


    /**
     * Gets the fechaTX value for this EjecutarRecargaDatosRequest.
     * 
     * @return fechaTX
     */
    public java.lang.String getFechaTX() {
        return fechaTX;
    }


    /**
     * Sets the fechaTX value for this EjecutarRecargaDatosRequest.
     * 
     * @param fechaTX
     */
    public void setFechaTX(java.lang.String fechaTX) {
        this.fechaTX = fechaTX;
    }


    /**
     * Gets the horaTX value for this EjecutarRecargaDatosRequest.
     * 
     * @return horaTX
     */
    public java.lang.String getHoraTX() {
        return horaTX;
    }


    /**
     * Sets the horaTX value for this EjecutarRecargaDatosRequest.
     * 
     * @param horaTX
     */
    public void setHoraTX(java.lang.String horaTX) {
        this.horaTX = horaTX;
    }


    /**
     * Gets the binAdquiriente value for this EjecutarRecargaDatosRequest.
     * 
     * @return binAdquiriente
     */
    public java.lang.String getBinAdquiriente() {
        return binAdquiriente;
    }


    /**
     * Sets the binAdquiriente value for this EjecutarRecargaDatosRequest.
     * 
     * @param binAdquiriente
     */
    public void setBinAdquiriente(java.lang.String binAdquiriente) {
        this.binAdquiriente = binAdquiriente;
    }


    /**
     * Gets the forwardInstitution value for this EjecutarRecargaDatosRequest.
     * 
     * @return forwardInstitution
     */
    public java.lang.String getForwardInstitution() {
        return forwardInstitution;
    }


    /**
     * Sets the forwardInstitution value for this EjecutarRecargaDatosRequest.
     * 
     * @param forwardInstitution
     */
    public void setForwardInstitution(java.lang.String forwardInstitution) {
        this.forwardInstitution = forwardInstitution;
    }


    /**
     * Gets the producto value for this EjecutarRecargaDatosRequest.
     * 
     * @return producto
     */
    public java.lang.String getProducto() {
        return producto;
    }


    /**
     * Sets the producto value for this EjecutarRecargaDatosRequest.
     * 
     * @param producto
     */
    public void setProducto(java.lang.String producto) {
        this.producto = producto;
    }


    /**
     * Gets the telefono value for this EjecutarRecargaDatosRequest.
     * 
     * @return telefono
     */
    public java.lang.String getTelefono() {
        return telefono;
    }


    /**
     * Sets the telefono value for this EjecutarRecargaDatosRequest.
     * 
     * @param telefono
     */
    public void setTelefono(java.lang.String telefono) {
        this.telefono = telefono;
    }


    /**
     * Gets the monto value for this EjecutarRecargaDatosRequest.
     * 
     * @return monto
     */
    public java.lang.String getMonto() {
        return monto;
    }


    /**
     * Sets the monto value for this EjecutarRecargaDatosRequest.
     * 
     * @param monto
     */
    public void setMonto(java.lang.String monto) {
        this.monto = monto;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EjecutarRecargaDatosRequest)) return false;
        EjecutarRecargaDatosRequest other = (EjecutarRecargaDatosRequest) obj;
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
            ((this.fechaTX==null && other.getFechaTX()==null) || 
             (this.fechaTX!=null &&
              this.fechaTX.equals(other.getFechaTX()))) &&
            ((this.horaTX==null && other.getHoraTX()==null) || 
             (this.horaTX!=null &&
              this.horaTX.equals(other.getHoraTX()))) &&
            ((this.binAdquiriente==null && other.getBinAdquiriente()==null) || 
             (this.binAdquiriente!=null &&
              this.binAdquiriente.equals(other.getBinAdquiriente()))) &&
            ((this.forwardInstitution==null && other.getForwardInstitution()==null) || 
             (this.forwardInstitution!=null &&
              this.forwardInstitution.equals(other.getForwardInstitution()))) &&
            ((this.producto==null && other.getProducto()==null) || 
             (this.producto!=null &&
              this.producto.equals(other.getProducto()))) &&
            ((this.telefono==null && other.getTelefono()==null) || 
             (this.telefono!=null &&
              this.telefono.equals(other.getTelefono()))) &&
            ((this.monto==null && other.getMonto()==null) || 
             (this.monto!=null &&
              this.monto.equals(other.getMonto())));
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
        if (getFechaTX() != null) {
            _hashCode += getFechaTX().hashCode();
        }
        if (getHoraTX() != null) {
            _hashCode += getHoraTX().hashCode();
        }
        if (getBinAdquiriente() != null) {
            _hashCode += getBinAdquiriente().hashCode();
        }
        if (getForwardInstitution() != null) {
            _hashCode += getForwardInstitution().hashCode();
        }
        if (getProducto() != null) {
            _hashCode += getProducto().hashCode();
        }
        if (getTelefono() != null) {
            _hashCode += getTelefono().hashCode();
        }
        if (getMonto() != null) {
            _hashCode += getMonto().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EjecutarRecargaDatosRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", ">ejecutarRecargaDatosRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idTransaccion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", "idTransaccion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ipAplicacion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", "ipAplicacion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nombreAplicacion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", "nombreAplicacion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fechaTX");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", "fechaTX"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("horaTX");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", "horaTX"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("binAdquiriente");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", "binAdquiriente"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("forwardInstitution");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", "forwardInstitution"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("producto");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", "producto"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("telefono");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", "telefono"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("monto");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/postventa/ebsRecargaPaqueteDatos", "monto"));
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
