/**
 * CheckStatusParam.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.iat.client;

public class CheckStatusParam implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private java.lang.String SNwTrxId;

    private java.lang.String iatTrxId;

    private java.lang.String SNwId;

    public CheckStatusParam() {
    }

    public CheckStatusParam(java.lang.String SNwTrxId, java.lang.String iatTrxId, java.lang.String SNwId) {
        this.SNwTrxId = SNwTrxId;
        this.iatTrxId = iatTrxId;
        this.SNwId = SNwId;
    }

    public String toString() {
        StringBuffer sbfObj = new StringBuffer();
        sbfObj.append("SNwTrxId::=" + SNwTrxId);
        sbfObj.append("iatTrxId::=" + iatTrxId);
        sbfObj.append("SNwId::=" + SNwId);

        return sbfObj.toString();
    }

    /**
     * Gets the SNwTrxId value for this CheckStatusParam.
     * 
     * @return SNwTrxId
     */
    public java.lang.String getSNwTrxId() {
        return SNwTrxId;
    }

    /**
     * Sets the SNwTrxId value for this CheckStatusParam.
     * 
     * @param SNwTrxId
     */
    public void setSNwTrxId(java.lang.String SNwTrxId) {
        this.SNwTrxId = SNwTrxId;
    }

    /**
     * Gets the iatTrxId value for this CheckStatusParam.
     * 
     * @return iatTrxId
     */
    public java.lang.String getIatTrxId() {
        return iatTrxId;
    }

    /**
     * Sets the iatTrxId value for this CheckStatusParam.
     * 
     * @param iatTrxId
     */
    public void setIatTrxId(java.lang.String iatTrxId) {
        this.iatTrxId = iatTrxId;
    }

    /**
     * Gets the SNwId value for this CheckStatusParam.
     * 
     * @return SNwId
     */
    public java.lang.String getSNwId() {
        return SNwId;
    }

    /**
     * Sets the SNwId value for this CheckStatusParam.
     * 
     * @param SNwId
     */
    public void setSNwId(java.lang.String SNwId) {
        this.SNwId = SNwId;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CheckStatusParam))
            return false;
        CheckStatusParam other = (CheckStatusParam) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && ((this.SNwTrxId == null && other.getSNwTrxId() == null) || (this.SNwTrxId != null && this.SNwTrxId.equals(other.getSNwTrxId()))) && ((this.iatTrxId == null && other.getIatTrxId() == null) || (this.iatTrxId != null && this.iatTrxId.equals(other.getIatTrxId()))) && ((this.SNwId == null && other.getSNwId() == null) || (this.SNwId != null && this.SNwId.equals(other.getSNwId())));
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
        if (getSNwTrxId() != null) {
            _hashCode += getSNwTrxId().hashCode();
        }
        if (getIatTrxId() != null) {
            _hashCode += getIatTrxId().hashCode();
        }
        if (getSNwId() != null) {
            _hashCode += getSNwId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(CheckStatusParam.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "CheckStatusParam"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SNwTrxId");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "SNwTrxId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("iatTrxId");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "IatTrxId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SNwId");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "SNwId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
