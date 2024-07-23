/**
 * RecargaPinVirtualRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroPINRechargeWS.stub;

public class RecargaPinVirtualRequest  implements java.io.Serializable {
    private java.lang.String idTransaccion;

    private java.lang.String ipAplicacion;

    private java.lang.String nombreAplicacion;

    private java.lang.String msisdn;

    private java.lang.String monto;

    private java.lang.String tipo;

    private java.lang.String fecha;

    private java.lang.String hora;

    private java.lang.String binadquiriente;

    private java.lang.String forwardinst;

    public RecargaPinVirtualRequest() {
    }

    public RecargaPinVirtualRequest(
           java.lang.String idTransaccion,
           java.lang.String ipAplicacion,
           java.lang.String nombreAplicacion,
           java.lang.String msisdn,
           java.lang.String monto,
           java.lang.String tipo,
           java.lang.String fecha,
           java.lang.String hora,
           java.lang.String binadquiriente,
           java.lang.String forwardinst) {
           this.idTransaccion = idTransaccion;
           this.ipAplicacion = ipAplicacion;
           this.nombreAplicacion = nombreAplicacion;
           this.msisdn = msisdn;
           this.monto = monto;
           this.tipo = tipo;
           this.fecha = fecha;
           this.hora = hora;
           this.binadquiriente = binadquiriente;
           this.forwardinst = forwardinst;
    }


    /**
     * Gets the idTransaccion value for this RecargaPinVirtualRequest.
     * 
     * @return idTransaccion
     */
    public java.lang.String getIdTransaccion() {
        return idTransaccion;
    }


    /**
     * Sets the idTransaccion value for this RecargaPinVirtualRequest.
     * 
     * @param idTransaccion
     */
    public void setIdTransaccion(java.lang.String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }


    /**
     * Gets the ipAplicacion value for this RecargaPinVirtualRequest.
     * 
     * @return ipAplicacion
     */
    public java.lang.String getIpAplicacion() {
        return ipAplicacion;
    }


    /**
     * Sets the ipAplicacion value for this RecargaPinVirtualRequest.
     * 
     * @param ipAplicacion
     */
    public void setIpAplicacion(java.lang.String ipAplicacion) {
        this.ipAplicacion = ipAplicacion;
    }


    /**
     * Gets the nombreAplicacion value for this RecargaPinVirtualRequest.
     * 
     * @return nombreAplicacion
     */
    public java.lang.String getNombreAplicacion() {
        return nombreAplicacion;
    }


    /**
     * Sets the nombreAplicacion value for this RecargaPinVirtualRequest.
     * 
     * @param nombreAplicacion
     */
    public void setNombreAplicacion(java.lang.String nombreAplicacion) {
        this.nombreAplicacion = nombreAplicacion;
    }


    /**
     * Gets the msisdn value for this RecargaPinVirtualRequest.
     * 
     * @return msisdn
     */
    public java.lang.String getMsisdn() {
        return msisdn;
    }


    /**
     * Sets the msisdn value for this RecargaPinVirtualRequest.
     * 
     * @param msisdn
     */
    public void setMsisdn(java.lang.String msisdn) {
        this.msisdn = msisdn;
    }


    /**
     * Gets the monto value for this RecargaPinVirtualRequest.
     * 
     * @return monto
     */
    public java.lang.String getMonto() {
        return monto;
    }


    /**
     * Sets the monto value for this RecargaPinVirtualRequest.
     * 
     * @param monto
     */
    public void setMonto(java.lang.String monto) {
        this.monto = monto;
    }


    /**
     * Gets the tipo value for this RecargaPinVirtualRequest.
     * 
     * @return tipo
     */
    public java.lang.String getTipo() {
        return tipo;
    }


    /**
     * Sets the tipo value for this RecargaPinVirtualRequest.
     * 
     * @param tipo
     */
    public void setTipo(java.lang.String tipo) {
        this.tipo = tipo;
    }


    /**
     * Gets the fecha value for this RecargaPinVirtualRequest.
     * 
     * @return fecha
     */
    public java.lang.String getFecha() {
        return fecha;
    }


    /**
     * Sets the fecha value for this RecargaPinVirtualRequest.
     * 
     * @param fecha
     */
    public void setFecha(java.lang.String fecha) {
        this.fecha = fecha;
    }


    /**
     * Gets the hora value for this RecargaPinVirtualRequest.
     * 
     * @return hora
     */
    public java.lang.String getHora() {
        return hora;
    }


    /**
     * Sets the hora value for this RecargaPinVirtualRequest.
     * 
     * @param hora
     */
    public void setHora(java.lang.String hora) {
        this.hora = hora;
    }


    /**
     * Gets the binadquiriente value for this RecargaPinVirtualRequest.
     * 
     * @return binadquiriente
     */
    public java.lang.String getBinadquiriente() {
        return binadquiriente;
    }


    /**
     * Sets the binadquiriente value for this RecargaPinVirtualRequest.
     * 
     * @param binadquiriente
     */
    public void setBinadquiriente(java.lang.String binadquiriente) {
        this.binadquiriente = binadquiriente;
    }


    /**
     * Gets the forwardinst value for this RecargaPinVirtualRequest.
     * 
     * @return forwardinst
     */
    public java.lang.String getForwardinst() {
        return forwardinst;
    }


    /**
     * Sets the forwardinst value for this RecargaPinVirtualRequest.
     * 
     * @param forwardinst
     */
    public void setForwardinst(java.lang.String forwardinst) {
        this.forwardinst = forwardinst;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RecargaPinVirtualRequest)) return false;
        RecargaPinVirtualRequest other = (RecargaPinVirtualRequest) obj;
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
            ((this.msisdn==null && other.getMsisdn()==null) || 
             (this.msisdn!=null &&
              this.msisdn.equals(other.getMsisdn()))) &&
            ((this.monto==null && other.getMonto()==null) || 
             (this.monto!=null &&
              this.monto.equals(other.getMonto()))) &&
            ((this.tipo==null && other.getTipo()==null) || 
             (this.tipo!=null &&
              this.tipo.equals(other.getTipo()))) &&
            ((this.fecha==null && other.getFecha()==null) || 
             (this.fecha!=null &&
              this.fecha.equals(other.getFecha()))) &&
            ((this.hora==null && other.getHora()==null) || 
             (this.hora!=null &&
              this.hora.equals(other.getHora()))) &&
            ((this.binadquiriente==null && other.getBinadquiriente()==null) || 
             (this.binadquiriente!=null &&
              this.binadquiriente.equals(other.getBinadquiriente()))) &&
            ((this.forwardinst==null && other.getForwardinst()==null) || 
             (this.forwardinst!=null &&
              this.forwardinst.equals(other.getForwardinst())));
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
        if (getMsisdn() != null) {
            _hashCode += getMsisdn().hashCode();
        }
        if (getMonto() != null) {
            _hashCode += getMonto().hashCode();
        }
        if (getTipo() != null) {
            _hashCode += getTipo().hashCode();
        }
        if (getFecha() != null) {
            _hashCode += getFecha().hashCode();
        }
        if (getHora() != null) {
            _hashCode += getHora().hashCode();
        }
        if (getBinadquiriente() != null) {
            _hashCode += getBinadquiriente().hashCode();
        }
        if (getForwardinst() != null) {
            _hashCode += getForwardinst().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RecargaPinVirtualRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://claro.com.pe/eai/venta/PinVirtual", ">recargaPinVirtualRequest"));
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
        elemField.setFieldName("msisdn");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/venta/PinVirtual", "msisdn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("monto");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/venta/PinVirtual", "monto"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tipo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/venta/PinVirtual", "tipo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fecha");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/venta/PinVirtual", "fecha"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hora");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/venta/PinVirtual", "hora"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("binadquiriente");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/venta/PinVirtual", "binadquiriente"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("forwardinst");
        elemField.setXmlName(new javax.xml.namespace.QName("http://claro.com.pe/eai/venta/PinVirtual", "forwardinst"));
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
