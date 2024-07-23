/**
 * CatalogParam.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.iat.client;

public class CatalogParam implements java.io.Serializable {
    private java.lang.String SNwId;

    private java.util.Calendar SNwTimeStamp;

    private int SCountryCode;

    private double[] SAmounts;

    public CatalogParam() {
    }

    public CatalogParam(java.lang.String SNwId, java.util.Calendar SNwTimeStamp, int SCountryCode, double[] SAmounts) {
        this.SNwId = SNwId;
        this.SNwTimeStamp = SNwTimeStamp;
        this.SCountryCode = SCountryCode;
        this.SAmounts = SAmounts;
    }

    /**
     * Gets the SNwId value for this CatalogParam.
     * 
     * @return SNwId
     */
    public java.lang.String getSNwId() {
        return SNwId;
    }

    /**
     * Sets the SNwId value for this CatalogParam.
     * 
     * @param SNwId
     */
    public void setSNwId(java.lang.String SNwId) {
        this.SNwId = SNwId;
    }

    /**
     * Gets the SNwTimeStamp value for this CatalogParam.
     * 
     * @return SNwTimeStamp
     */
    public java.util.Calendar getSNwTimeStamp() {
        return SNwTimeStamp;
    }

    /**
     * Sets the SNwTimeStamp value for this CatalogParam.
     * 
     * @param SNwTimeStamp
     */
    public void setSNwTimeStamp(java.util.Calendar SNwTimeStamp) {
        this.SNwTimeStamp = SNwTimeStamp;
    }

    /**
     * Gets the SCountryCode value for this CatalogParam.
     * 
     * @return SCountryCode
     */
    public int getSCountryCode() {
        return SCountryCode;
    }

    /**
     * Sets the SCountryCode value for this CatalogParam.
     * 
     * @param SCountryCode
     */
    public void setSCountryCode(int SCountryCode) {
        this.SCountryCode = SCountryCode;
    }

    /**
     * Gets the SAmounts value for this CatalogParam.
     * 
     * @return SAmounts
     */
    public double[] getSAmounts() {
        return SAmounts;
    }

    /**
     * Sets the SAmounts value for this CatalogParam.
     * 
     * @param SAmounts
     */
    public void setSAmounts(double[] SAmounts) {
        this.SAmounts = SAmounts;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CatalogParam))
            return false;
        CatalogParam other = (CatalogParam) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && ((this.SNwId == null && other.getSNwId() == null) || (this.SNwId != null && this.SNwId.equals(other.getSNwId()))) && ((this.SNwTimeStamp == null && other.getSNwTimeStamp() == null) || (this.SNwTimeStamp != null && this.SNwTimeStamp.equals(other.getSNwTimeStamp()))) && this.SCountryCode == other.getSCountryCode() && ((this.SAmounts == null && other.getSAmounts() == null) || (this.SAmounts != null && java.util.Arrays.equals(this.SAmounts, other.getSAmounts())));
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
        if (getSNwId() != null) {
            _hashCode += getSNwId().hashCode();
        }
        if (getSNwTimeStamp() != null) {
            _hashCode += getSNwTimeStamp().hashCode();
        }
        _hashCode += getSCountryCode();
        if (getSAmounts() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getSAmounts()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSAmounts(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(CatalogParam.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "CatalogParam"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SNwId");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "SNwId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SNwTimeStamp");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "SNwTimeStamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SCountryCode");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "SCountryCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SAmounts");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "SAmounts"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "SAmount"));
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
    public static org.apache.axis.encoding.Serializer getSerializer(java.lang.String mechType, java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.BeanSerializer(_javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(java.lang.String mechType, java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType, _xmlType, typeDesc);
    }

}
