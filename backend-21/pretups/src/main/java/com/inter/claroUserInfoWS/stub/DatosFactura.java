/**
 * DatosFactura.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroUserInfoWS.stub;

public class DatosFactura  implements java.io.Serializable {
    /* Numero de cliente */
    private java.lang.String nroCliente;

    /* Nombre */
    private java.lang.String nombre;

    /* Fecha de factura */
    private java.lang.String fechaFactura;

    /* Numero de documento comercial */
    private java.lang.String nroDocComercial;

    /* Importe de impuesto */
    private java.lang.String impuesto;

    /* Valor neto */
    private java.lang.String valorNeto;

    /* Estado */
    private java.lang.String estado;

    public DatosFactura() {
    }

    public DatosFactura(
           java.lang.String nroCliente,
           java.lang.String nombre,
           java.lang.String fechaFactura,
           java.lang.String nroDocComercial,
           java.lang.String impuesto,
           java.lang.String valorNeto,
           java.lang.String estado) {
           this.nroCliente = nroCliente;
           this.nombre = nombre;
           this.fechaFactura = fechaFactura;
           this.nroDocComercial = nroDocComercial;
           this.impuesto = impuesto;
           this.valorNeto = valorNeto;
           this.estado = estado;
    }


    /**
     * Gets the nroCliente value for this DatosFactura.
     * 
     * @return nroCliente   * Numero de cliente
     */
    public java.lang.String getNroCliente() {
        return nroCliente;
    }


    /**
     * Sets the nroCliente value for this DatosFactura.
     * 
     * @param nroCliente   * Numero de cliente
     */
    public void setNroCliente(java.lang.String nroCliente) {
        this.nroCliente = nroCliente;
    }


    /**
     * Gets the nombre value for this DatosFactura.
     * 
     * @return nombre   * Nombre
     */
    public java.lang.String getNombre() {
        return nombre;
    }


    /**
     * Sets the nombre value for this DatosFactura.
     * 
     * @param nombre   * Nombre
     */
    public void setNombre(java.lang.String nombre) {
        this.nombre = nombre;
    }


    /**
     * Gets the fechaFactura value for this DatosFactura.
     * 
     * @return fechaFactura   * Fecha de factura
     */
    public java.lang.String getFechaFactura() {
        return fechaFactura;
    }


    /**
     * Sets the fechaFactura value for this DatosFactura.
     * 
     * @param fechaFactura   * Fecha de factura
     */
    public void setFechaFactura(java.lang.String fechaFactura) {
        this.fechaFactura = fechaFactura;
    }


    /**
     * Gets the nroDocComercial value for this DatosFactura.
     * 
     * @return nroDocComercial   * Numero de documento comercial
     */
    public java.lang.String getNroDocComercial() {
        return nroDocComercial;
    }


    /**
     * Sets the nroDocComercial value for this DatosFactura.
     * 
     * @param nroDocComercial   * Numero de documento comercial
     */
    public void setNroDocComercial(java.lang.String nroDocComercial) {
        this.nroDocComercial = nroDocComercial;
    }


    /**
     * Gets the impuesto value for this DatosFactura.
     * 
     * @return impuesto   * Importe de impuesto
     */
    public java.lang.String getImpuesto() {
        return impuesto;
    }


    /**
     * Sets the impuesto value for this DatosFactura.
     * 
     * @param impuesto   * Importe de impuesto
     */
    public void setImpuesto(java.lang.String impuesto) {
        this.impuesto = impuesto;
    }


    /**
     * Gets the valorNeto value for this DatosFactura.
     * 
     * @return valorNeto   * Valor neto
     */
    public java.lang.String getValorNeto() {
        return valorNeto;
    }


    /**
     * Sets the valorNeto value for this DatosFactura.
     * 
     * @param valorNeto   * Valor neto
     */
    public void setValorNeto(java.lang.String valorNeto) {
        this.valorNeto = valorNeto;
    }


    /**
     * Gets the estado value for this DatosFactura.
     * 
     * @return estado   * Estado
     */
    public java.lang.String getEstado() {
        return estado;
    }


    /**
     * Sets the estado value for this DatosFactura.
     * 
     * @param estado   * Estado
     */
    public void setEstado(java.lang.String estado) {
        this.estado = estado;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DatosFactura)) return false;
        DatosFactura other = (DatosFactura) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.nroCliente==null && other.getNroCliente()==null) || 
             (this.nroCliente!=null &&
              this.nroCliente.equals(other.getNroCliente()))) &&
            ((this.nombre==null && other.getNombre()==null) || 
             (this.nombre!=null &&
              this.nombre.equals(other.getNombre()))) &&
            ((this.fechaFactura==null && other.getFechaFactura()==null) || 
             (this.fechaFactura!=null &&
              this.fechaFactura.equals(other.getFechaFactura()))) &&
            ((this.nroDocComercial==null && other.getNroDocComercial()==null) || 
             (this.nroDocComercial!=null &&
              this.nroDocComercial.equals(other.getNroDocComercial()))) &&
            ((this.impuesto==null && other.getImpuesto()==null) || 
             (this.impuesto!=null &&
              this.impuesto.equals(other.getImpuesto()))) &&
            ((this.valorNeto==null && other.getValorNeto()==null) || 
             (this.valorNeto!=null &&
              this.valorNeto.equals(other.getValorNeto()))) &&
            ((this.estado==null && other.getEstado()==null) || 
             (this.estado!=null &&
              this.estado.equals(other.getEstado())));
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
        if (getNroCliente() != null) {
            _hashCode += getNroCliente().hashCode();
        }
        if (getNombre() != null) {
            _hashCode += getNombre().hashCode();
        }
        if (getFechaFactura() != null) {
            _hashCode += getFechaFactura().hashCode();
        }
        if (getNroDocComercial() != null) {
            _hashCode += getNroDocComercial().hashCode();
        }
        if (getImpuesto() != null) {
            _hashCode += getImpuesto().hashCode();
        }
        if (getValorNeto() != null) {
            _hashCode += getValorNeto().hashCode();
        }
        if (getEstado() != null) {
            _hashCode += getEstado().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DatosFactura.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", "datosFactura"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nroCliente");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", "nroCliente"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nombre");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", "nombre"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fechaFactura");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", "fechaFactura"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nroDocComercial");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", "nroDocComercial"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("impuesto");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", "impuesto"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("valorNeto");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", "valorNeto"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("estado");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", "estado"));
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
