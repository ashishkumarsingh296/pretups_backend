/**
 * AplicaRecargaResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.claroca.dth.enquiry;

public class AplicaRecargaResponse  implements java.io.Serializable {
    private com.inter.claroca.dth.enquiry.AplicaRecargaResponseAplicaRecargaResult aplicaRecargaResult;

    public AplicaRecargaResponse() {
    }

    public AplicaRecargaResponse(
           com.inter.claroca.dth.enquiry.AplicaRecargaResponseAplicaRecargaResult aplicaRecargaResult) {
           this.aplicaRecargaResult = aplicaRecargaResult;
    }


    /**
     * Gets the aplicaRecargaResult value for this AplicaRecargaResponse.
     * 
     * @return aplicaRecargaResult
     */
    public com.inter.claroca.dth.enquiry.AplicaRecargaResponseAplicaRecargaResult getAplicaRecargaResult() {
        return aplicaRecargaResult;
    }


    /**
     * Sets the aplicaRecargaResult value for this AplicaRecargaResponse.
     * 
     * @param aplicaRecargaResult
     */
    public void setAplicaRecargaResult(com.inter.claroca.dth.enquiry.AplicaRecargaResponseAplicaRecargaResult aplicaRecargaResult) {
        this.aplicaRecargaResult = aplicaRecargaResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AplicaRecargaResponse)) return false;
        AplicaRecargaResponse other = (AplicaRecargaResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.aplicaRecargaResult==null && other.getAplicaRecargaResult()==null) || 
             (this.aplicaRecargaResult!=null &&
              this.aplicaRecargaResult.equals(other.getAplicaRecargaResult())));
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
        if (getAplicaRecargaResult() != null) {
            _hashCode += getAplicaRecargaResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AplicaRecargaResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">AplicaRecargaResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("aplicaRecargaResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "AplicaRecargaResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">>AplicaRecargaResponse>AplicaRecargaResult"));
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
