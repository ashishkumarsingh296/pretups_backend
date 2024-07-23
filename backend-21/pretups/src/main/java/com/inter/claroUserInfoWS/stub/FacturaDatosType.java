/**
 * FacturaDatosType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroUserInfoWS.stub;

public class FacturaDatosType  implements java.io.Serializable {
    private com.inter.claroUserInfoWS.stub.DatosFactura t_Datos;

    public FacturaDatosType() {
    }

    public FacturaDatosType(
           com.inter.claroUserInfoWS.stub.DatosFactura t_Datos) {
           this.t_Datos = t_Datos;
    }


    /**
     * Gets the t_Datos value for this FacturaDatosType.
     * 
     * @return t_Datos
     */
    public com.inter.claroUserInfoWS.stub.DatosFactura getT_Datos() {
        return t_Datos;
    }


    /**
     * Sets the t_Datos value for this FacturaDatosType.
     * 
     * @param t_Datos
     */
    public void setT_Datos(com.inter.claroUserInfoWS.stub.DatosFactura t_Datos) {
        this.t_Datos = t_Datos;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FacturaDatosType)) return false;
        FacturaDatosType other = (FacturaDatosType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.t_Datos==null && other.getT_Datos()==null) || 
             (this.t_Datos!=null &&
              this.t_Datos.equals(other.getT_Datos())));
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
        if (getT_Datos() != null) {
            _hashCode += getT_Datos().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FacturaDatosType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", "facturaDatosType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("t_Datos");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", "T_Datos"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/factura/factura.xsd", "datosFactura"));
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
