/**
 * RNwResultType.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.iat.client;

public class RNwResultType implements java.io.Serializable {
    private java.lang.String RNwId;

    private java.lang.String RCountryCode;

    private com.inter.iat.client.ExchangeValueType[] exchangeValues;

    public RNwResultType() {
    }

    public RNwResultType(java.lang.String RNwId, java.lang.String RCountryCode, com.inter.iat.client.ExchangeValueType[] exchangeValues) {
        this.RNwId = RNwId;
        this.RCountryCode = RCountryCode;
        this.exchangeValues = exchangeValues;
    }

    /**
     * Gets the RNwId value for this RNwResultType.
     * 
     * @return RNwId
     */
    public java.lang.String getRNwId() {
        return RNwId;
    }

    /**
     * Sets the RNwId value for this RNwResultType.
     * 
     * @param RNwId
     */
    public void setRNwId(java.lang.String RNwId) {
        this.RNwId = RNwId;
    }

    /**
     * Gets the RCountryCode value for this RNwResultType.
     * 
     * @return RCountryCode
     */
    public java.lang.String getRCountryCode() {
        return RCountryCode;
    }

    /**
     * Sets the RCountryCode value for this RNwResultType.
     * 
     * @param RCountryCode
     */
    public void setRCountryCode(java.lang.String RCountryCode) {
        this.RCountryCode = RCountryCode;
    }

    /**
     * Gets the exchangeValues value for this RNwResultType.
     * 
     * @return exchangeValues
     */
    public com.inter.iat.client.ExchangeValueType[] getExchangeValues() {
        return exchangeValues;
    }

    /**
     * Sets the exchangeValues value for this RNwResultType.
     * 
     * @param exchangeValues
     */
    public void setExchangeValues(com.inter.iat.client.ExchangeValueType[] exchangeValues) {
        this.exchangeValues = exchangeValues;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RNwResultType))
            return false;
        RNwResultType other = (RNwResultType) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && ((this.RNwId == null && other.getRNwId() == null) || (this.RNwId != null && this.RNwId.equals(other.getRNwId()))) && ((this.RCountryCode == null && other.getRCountryCode() == null) || (this.RCountryCode != null && this.RCountryCode.equals(other.getRCountryCode()))) && ((this.exchangeValues == null && other.getExchangeValues() == null) || (this.exchangeValues != null && java.util.Arrays.equals(this.exchangeValues, other.getExchangeValues())));
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
        if (getRNwId() != null) {
            _hashCode += getRNwId().hashCode();
        }
        if (getRCountryCode() != null) {
            _hashCode += getRCountryCode().hashCode();
        }
        if (getExchangeValues() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getExchangeValues()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getExchangeValues(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(RNwResultType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "RNwResultType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("RNwId");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "RNwId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("RCountryCode");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "RCountryCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("exchangeValues");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "ExchangeValues"));
        elemField.setXmlType(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "ExchangeValueType"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "ExchangeValue"));
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
