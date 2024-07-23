/**
 * GetKeyNamePairResponseData.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Nov 19, 2006 (02:31:34 GMT+00:00) WSDL2Java emitter.
 */

package com.inter.alepoogn.alepoognstub;

public class GetKeyNamePairResponseData implements java.io.Serializable {
    private java.lang.String code;

    private com.inter.alepoogn.alepoognstub.KeyName[] keynamepair;

    public GetKeyNamePairResponseData() {
    }

    public GetKeyNamePairResponseData(java.lang.String code, com.inter.alepoogn.alepoognstub.KeyName[] keynamepair) {
        this.code = code;
        this.keynamepair = keynamepair;
    }

    /**
     * Gets the code value for this GetKeyNamePairResponseData.
     * 
     * @return code
     */
    public java.lang.String getCode() {
        return code;
    }

    /**
     * Sets the code value for this GetKeyNamePairResponseData.
     * 
     * @param code
     */
    public void setCode(java.lang.String code) {
        this.code = code;
    }

    /**
     * Gets the keynamepair value for this GetKeyNamePairResponseData.
     * 
     * @return keynamepair
     */
    public com.inter.alepoogn.alepoognstub.KeyName[] getKeynamepair() {
        return keynamepair;
    }

    /**
     * Sets the keynamepair value for this GetKeyNamePairResponseData.
     * 
     * @param keynamepair
     */
    public void setKeynamepair(com.inter.alepoogn.alepoognstub.KeyName[] keynamepair) {
        this.keynamepair = keynamepair;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetKeyNamePairResponseData))
            return false;
        GetKeyNamePairResponseData other = (GetKeyNamePairResponseData) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && ((this.code == null && other.getCode() == null) || (this.code != null && this.code.equals(other.getCode()))) && ((this.keynamepair == null && other.getKeynamepair() == null) || (this.keynamepair != null && java.util.Arrays.equals(this.keynamepair, other.getKeynamepair())));
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
        if (getCode() != null) {
            _hashCode += getCode().hashCode();
        }
        if (getKeynamepair() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getKeynamepair()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getKeynamepair(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(GetKeyNamePairResponseData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://localhost:9000", "GetKeyNamePairResponseData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("code");
        elemField.setXmlName(new javax.xml.namespace.QName("", "code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("keynamepair");
        elemField.setXmlName(new javax.xml.namespace.QName("", "keynamepair"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://localhost:9000", "KeyName"));
        elemField.setNillable(true);
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
