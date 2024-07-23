/**
 * DistribuidorDeudaDatosType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroUserInfoWS.stub;

public class DistribuidorDeudaDatosType  implements java.io.Serializable {
    /* Nombre del distribuidor */
    private java.lang.String nombre;

    /* Monto */
    private java.lang.String monto;

    /* Numero de identificacion fiscal */
    private java.lang.String nroDoc;

    private java.lang.String mensaje;

    public DistribuidorDeudaDatosType() {
    }

    public DistribuidorDeudaDatosType(
           java.lang.String nombre,
           java.lang.String monto,
           java.lang.String nroDoc,
           java.lang.String mensaje) {
           this.nombre = nombre;
           this.monto = monto;
           this.nroDoc = nroDoc;
           this.mensaje = mensaje;
    }


    /**
     * Gets the nombre value for this DistribuidorDeudaDatosType.
     * 
     * @return nombre   * Nombre del distribuidor
     */
    public java.lang.String getNombre() {
        return nombre;
    }


    /**
     * Sets the nombre value for this DistribuidorDeudaDatosType.
     * 
     * @param nombre   * Nombre del distribuidor
     */
    public void setNombre(java.lang.String nombre) {
        this.nombre = nombre;
    }


    /**
     * Gets the monto value for this DistribuidorDeudaDatosType.
     * 
     * @return monto   * Monto
     */
    public java.lang.String getMonto() {
        return monto;
    }


    /**
     * Sets the monto value for this DistribuidorDeudaDatosType.
     * 
     * @param monto   * Monto
     */
    public void setMonto(java.lang.String monto) {
        this.monto = monto;
    }


    /**
     * Gets the nroDoc value for this DistribuidorDeudaDatosType.
     * 
     * @return nroDoc   * Numero de identificacion fiscal
     */
    public java.lang.String getNroDoc() {
        return nroDoc;
    }


    /**
     * Sets the nroDoc value for this DistribuidorDeudaDatosType.
     * 
     * @param nroDoc   * Numero de identificacion fiscal
     */
    public void setNroDoc(java.lang.String nroDoc) {
        this.nroDoc = nroDoc;
    }


    /**
     * Gets the mensaje value for this DistribuidorDeudaDatosType.
     * 
     * @return mensaje
     */
    public java.lang.String getMensaje() {
        return mensaje;
    }


    /**
     * Sets the mensaje value for this DistribuidorDeudaDatosType.
     * 
     * @param mensaje
     */
    public void setMensaje(java.lang.String mensaje) {
        this.mensaje = mensaje;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DistribuidorDeudaDatosType)) return false;
        DistribuidorDeudaDatosType other = (DistribuidorDeudaDatosType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.nombre==null && other.getNombre()==null) || 
             (this.nombre!=null &&
              this.nombre.equals(other.getNombre()))) &&
            ((this.monto==null && other.getMonto()==null) || 
             (this.monto!=null &&
              this.monto.equals(other.getMonto()))) &&
            ((this.nroDoc==null && other.getNroDoc()==null) || 
             (this.nroDoc!=null &&
              this.nroDoc.equals(other.getNroDoc()))) &&
            ((this.mensaje==null && other.getMensaje()==null) || 
             (this.mensaje!=null &&
              this.mensaje.equals(other.getMensaje())));
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
        if (getNombre() != null) {
            _hashCode += getNombre().hashCode();
        }
        if (getMonto() != null) {
            _hashCode += getMonto().hashCode();
        }
        if (getNroDoc() != null) {
            _hashCode += getNroDoc().hashCode();
        }
        if (getMensaje() != null) {
            _hashCode += getMensaje().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DistribuidorDeudaDatosType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorDeudaTot.xsd", "distribuidorDeudaDatosType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nombre");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorDeudaTot.xsd", "nombre"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("monto");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorDeudaTot.xsd", "monto"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nroDoc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorDeudaTot.xsd", "nroDoc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mensaje");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorDeudaTot.xsd", "mensaje"));
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
