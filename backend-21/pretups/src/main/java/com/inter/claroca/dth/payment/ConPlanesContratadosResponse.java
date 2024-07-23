/**
 * ConPlanesContratadosResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroca.dth.payment;

public class ConPlanesContratadosResponse  implements java.io.Serializable {
    private com.inter.claroca.dth.payment.ConPlanesContratadosResponseConPlanesContratadosResult conPlanesContratadosResult;

    public ConPlanesContratadosResponse() {
    }

    public ConPlanesContratadosResponse(
           com.inter.claroca.dth.payment.ConPlanesContratadosResponseConPlanesContratadosResult conPlanesContratadosResult) {
           this.conPlanesContratadosResult = conPlanesContratadosResult;
    }


    /**
     * Gets the conPlanesContratadosResult value for this ConPlanesContratadosResponse.
     * 
     * @return conPlanesContratadosResult
     */
    public com.inter.claroca.dth.payment.ConPlanesContratadosResponseConPlanesContratadosResult getConPlanesContratadosResult() {
        return conPlanesContratadosResult;
    }


    /**
     * Sets the conPlanesContratadosResult value for this ConPlanesContratadosResponse.
     * 
     * @param conPlanesContratadosResult
     */
    public void setConPlanesContratadosResult(com.inter.claroca.dth.payment.ConPlanesContratadosResponseConPlanesContratadosResult conPlanesContratadosResult) {
        this.conPlanesContratadosResult = conPlanesContratadosResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConPlanesContratadosResponse)) return false;
        ConPlanesContratadosResponse other = (ConPlanesContratadosResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.conPlanesContratadosResult==null && other.getConPlanesContratadosResult()==null) || 
             (this.conPlanesContratadosResult!=null &&
              this.conPlanesContratadosResult.equals(other.getConPlanesContratadosResult())));
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
        if (getConPlanesContratadosResult() != null) {
            _hashCode += getConPlanesContratadosResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConPlanesContratadosResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">conPlanesContratadosResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conPlanesContratadosResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "conPlanesContratadosResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">>conPlanesContratadosResponse>conPlanesContratadosResult"));
        elemField.setMinOccurs(0);
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
