/**
 * CheckStatusResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.oloapi.stub;

public class CheckStatusResponse  implements java.io.Serializable {
    private java.lang.String status;

    private com.inter.oloapi.stub.Correlation correlation;

    public CheckStatusResponse() {
    }

    public CheckStatusResponse(
           java.lang.String status,
           com.inter.oloapi.stub.Correlation correlation) {
           this.status = status;
           this.correlation = correlation;
    }


    /**
     * Gets the status value for this CheckStatusResponse.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this CheckStatusResponse.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the correlation value for this CheckStatusResponse.
     * 
     * @return correlation
     */
    public com.inter.oloapi.stub.Correlation getCorrelation() {
        return correlation;
    }


    /**
     * Sets the correlation value for this CheckStatusResponse.
     * 
     * @param correlation
     */
    public void setCorrelation(com.inter.oloapi.stub.Correlation correlation) {
        this.correlation = correlation;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CheckStatusResponse)) return false;
        CheckStatusResponse other = (CheckStatusResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.correlation==null && other.getCorrelation()==null) || 
             (this.correlation!=null &&
              this.correlation.equals(other.getCorrelation())));
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
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getCorrelation() != null) {
            _hashCode += getCorrelation().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CheckStatusResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:prepay", "CheckStatusResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("correlation");
        elemField.setXmlName(new javax.xml.namespace.QName("", "correlation"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:prepay", "Correlation"));
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
