/**
 * ExchangeValueType.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.iat.client;

public class ExchangeValueType implements java.io.Serializable {
    private double SAmount;

    private double RAmount;

    public ExchangeValueType() {
    }

    public ExchangeValueType(double SAmount, double RAmount) {
        this.SAmount = SAmount;
        this.RAmount = RAmount;
    }

    /**
     * Gets the SAmount value for this ExchangeValueType.
     * 
     * @return SAmount
     */
    public double getSAmount() {
        return SAmount;
    }

    /**
     * Sets the SAmount value for this ExchangeValueType.
     * 
     * @param SAmount
     */
    public void setSAmount(double SAmount) {
        this.SAmount = SAmount;
    }

    /**
     * Gets the RAmount value for this ExchangeValueType.
     * 
     * @return RAmount
     */
    public double getRAmount() {
        return RAmount;
    }

    /**
     * Sets the RAmount value for this ExchangeValueType.
     * 
     * @param RAmount
     */
    public void setRAmount(double RAmount) {
        this.RAmount = RAmount;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ExchangeValueType))
            return false;
        ExchangeValueType other = (ExchangeValueType) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && this.SAmount == other.getSAmount() && this.RAmount == other.getRAmount();
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
        _hashCode += new Double(getSAmount()).hashCode();
        _hashCode += new Double(getRAmount()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(ExchangeValueType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "ExchangeValueType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "SAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("RAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "RAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
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
