/**
 * DistribuidorDataResponseType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroUserInfoWS.stub;

public class DistribuidorDataResponseType  implements java.io.Serializable {
    private com.inter.claroUserInfoWS.stub.DistribuidorDataDatosType datos;

    private com.inter.claroUserInfoWS.stub.DistribuidorDataInfoType retorno;

    public DistribuidorDataResponseType() {
    }

    public DistribuidorDataResponseType(
           com.inter.claroUserInfoWS.stub.DistribuidorDataDatosType datos,
           com.inter.claroUserInfoWS.stub.DistribuidorDataInfoType retorno) {
           this.datos = datos;
           this.retorno = retorno;
    }


    /**
     * Gets the datos value for this DistribuidorDataResponseType.
     * 
     * @return datos
     */
    public com.inter.claroUserInfoWS.stub.DistribuidorDataDatosType getDatos() {
        return datos;
    }


    /**
     * Sets the datos value for this DistribuidorDataResponseType.
     * 
     * @param datos
     */
    public void setDatos(com.inter.claroUserInfoWS.stub.DistribuidorDataDatosType datos) {
        this.datos = datos;
    }


    /**
     * Gets the retorno value for this DistribuidorDataResponseType.
     * 
     * @return retorno
     */
    public com.inter.claroUserInfoWS.stub.DistribuidorDataInfoType getRetorno() {
        return retorno;
    }


    /**
     * Sets the retorno value for this DistribuidorDataResponseType.
     * 
     * @param retorno
     */
    public void setRetorno(com.inter.claroUserInfoWS.stub.DistribuidorDataInfoType retorno) {
        this.retorno = retorno;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DistribuidorDataResponseType)) return false;
        DistribuidorDataResponseType other = (DistribuidorDataResponseType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.datos==null && other.getDatos()==null) || 
             (this.datos!=null &&
              this.datos.equals(other.getDatos()))) &&
            ((this.retorno==null && other.getRetorno()==null) || 
             (this.retorno!=null &&
              this.retorno.equals(other.getRetorno())));
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
        if (getDatos() != null) {
            _hashCode += getDatos().hashCode();
        }
        if (getRetorno() != null) {
            _hashCode += getRetorno().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DistribuidorDataResponseType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "distribuidorDataResponseType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("datos");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "datos"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "distribuidorDataDatosType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("retorno");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "retorno"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pe/com/claro/esb/services/sap/streetseller/schemas/distribuidor/distribuidorData.xsd", "distribuidorDataInfoType"));
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
