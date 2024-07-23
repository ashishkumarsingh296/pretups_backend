/**
 * CreateReceivableParam.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.iat.client;

public class CreateReceivableParam implements java.io.Serializable {
    private java.lang.String SNwId;

    private java.util.Calendar SNwTimeStamp;

    private int SCountryCode;

    private java.lang.String durationType;

    private double receivableValueSc;

    public CreateReceivableParam() {
    }

    public CreateReceivableParam(java.lang.String SNwId, java.util.Calendar SNwTimeStamp, int SCountryCode, java.lang.String durationType, double receivableValueSc) {
        this.SNwId = SNwId;
        this.SNwTimeStamp = SNwTimeStamp;
        this.SCountryCode = SCountryCode;
        this.durationType = durationType;
        this.receivableValueSc = receivableValueSc;
    }

    /**
     * Gets the SNwId value for this CreateReceivableParam.
     * 
     * @return SNwId
     */
    public java.lang.String getSNwId() {
        return SNwId;
    }

    /**
     * Sets the SNwId value for this CreateReceivableParam.
     * 
     * @param SNwId
     */
    public void setSNwId(java.lang.String SNwId) {
        this.SNwId = SNwId;
    }

    /**
     * Gets the SNwTimeStamp value for this CreateReceivableParam.
     * 
     * @return SNwTimeStamp
     */
    public java.util.Calendar getSNwTimeStamp() {
        return SNwTimeStamp;
    }

    /**
     * Sets the SNwTimeStamp value for this CreateReceivableParam.
     * 
     * @param SNwTimeStamp
     */
    public void setSNwTimeStamp(java.util.Calendar SNwTimeStamp) {
        this.SNwTimeStamp = SNwTimeStamp;
    }

    /**
     * Gets the SCountryCode value for this CreateReceivableParam.
     * 
     * @return SCountryCode
     */
    public int getSCountryCode() {
        return SCountryCode;
    }

    /**
     * Sets the SCountryCode value for this CreateReceivableParam.
     * 
     * @param SCountryCode
     */
    public void setSCountryCode(int SCountryCode) {
        this.SCountryCode = SCountryCode;
    }

    /**
     * Gets the durationType value for this CreateReceivableParam.
     * 
     * @return durationType
     */
    public java.lang.String getDurationType() {
        return durationType;
    }

    /**
     * Sets the durationType value for this CreateReceivableParam.
     * 
     * @param durationType
     */
    public void setDurationType(java.lang.String durationType) {
        this.durationType = durationType;
    }

    /**
     * Gets the receivableValueSc value for this CreateReceivableParam.
     * 
     * @return receivableValueSc
     */
    public double getReceivableValueSc() {
        return receivableValueSc;
    }

    /**
     * Sets the receivableValueSc value for this CreateReceivableParam.
     * 
     * @param receivableValueSc
     */
    public void setReceivableValueSc(double receivableValueSc) {
        this.receivableValueSc = receivableValueSc;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CreateReceivableParam))
            return false;
        CreateReceivableParam other = (CreateReceivableParam) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && ((this.SNwId == null && other.getSNwId() == null) || (this.SNwId != null && this.SNwId.equals(other.getSNwId()))) && ((this.SNwTimeStamp == null && other.getSNwTimeStamp() == null) || (this.SNwTimeStamp != null && this.SNwTimeStamp.equals(other.getSNwTimeStamp()))) && this.SCountryCode == other.getSCountryCode() && ((this.durationType == null && other.getDurationType() == null) || (this.durationType != null && this.durationType.equals(other.getDurationType()))) && this.receivableValueSc == other.getReceivableValueSc();
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
        if (getDurationType() != null) {
            _hashCode += getDurationType().hashCode();
        }
        _hashCode += new Double(getReceivableValueSc()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(CreateReceivableParam.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "CreateReceivableParam"));
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
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SCountryCode");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "SCountryCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("durationType");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "DurationType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("receivableValueSc");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "ReceivableValueSc"));
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
