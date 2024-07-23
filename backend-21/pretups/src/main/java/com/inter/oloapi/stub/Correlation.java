/**
 * Correlation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.oloapi.stub;

public class Correlation  implements java.io.Serializable {
    private java.lang.String mobiquityTransactionId;

    private java.lang.String thirdPartyTransactionId;

    public Correlation() {
    }

    public Correlation(
           java.lang.String mobiquityTransactionId,
           java.lang.String thirdPartyTransactionId) {
           this.mobiquityTransactionId = mobiquityTransactionId;
           this.thirdPartyTransactionId = thirdPartyTransactionId;
    }


    /**
     * Gets the mobiquityTransactionId value for this Correlation.
     * 
     * @return mobiquityTransactionId
     */
    public java.lang.String getMobiquityTransactionId() {
        return mobiquityTransactionId;
    }


    /**
     * Sets the mobiquityTransactionId value for this Correlation.
     * 
     * @param mobiquityTransactionId
     */
    public void setMobiquityTransactionId(java.lang.String mobiquityTransactionId) {
        this.mobiquityTransactionId = mobiquityTransactionId;
    }


    /**
     * Gets the thirdPartyTransactionId value for this Correlation.
     * 
     * @return thirdPartyTransactionId
     */
    public java.lang.String getThirdPartyTransactionId() {
        return thirdPartyTransactionId;
    }


    /**
     * Sets the thirdPartyTransactionId value for this Correlation.
     * 
     * @param thirdPartyTransactionId
     */
    public void setThirdPartyTransactionId(java.lang.String thirdPartyTransactionId) {
        this.thirdPartyTransactionId = thirdPartyTransactionId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Correlation)) return false;
        Correlation other = (Correlation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.mobiquityTransactionId==null && other.getMobiquityTransactionId()==null) || 
             (this.mobiquityTransactionId!=null &&
              this.mobiquityTransactionId.equals(other.getMobiquityTransactionId()))) &&
            ((this.thirdPartyTransactionId==null && other.getThirdPartyTransactionId()==null) || 
             (this.thirdPartyTransactionId!=null &&
              this.thirdPartyTransactionId.equals(other.getThirdPartyTransactionId())));
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
        if (getMobiquityTransactionId() != null) {
            _hashCode += getMobiquityTransactionId().hashCode();
        }
        if (getThirdPartyTransactionId() != null) {
            _hashCode += getThirdPartyTransactionId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Correlation.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:prepay", "Correlation"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mobiquityTransactionId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mobiquityTransactionId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thirdPartyTransactionId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "thirdPartyTransactionId"));
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
