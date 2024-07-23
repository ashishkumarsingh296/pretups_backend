/**
 * CatalogResult.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.iat.client;

public class CatalogResult implements java.io.Serializable {
    private int status;

    private java.lang.String message;

    private java.lang.String errRNwId;

    private com.inter.iat.client.RNwResultType[] RNwResults;

    public CatalogResult() {
    }

    public CatalogResult(int status, java.lang.String message, java.lang.String errRNwId, com.inter.iat.client.RNwResultType[] RNwResults) {
        this.status = status;
        this.message = message;
        this.errRNwId = errRNwId;
        this.RNwResults = RNwResults;
    }

    /**
     * Gets the status value for this CatalogResult.
     * 
     * @return status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the status value for this CatalogResult.
     * 
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Gets the message value for this CatalogResult.
     * 
     * @return message
     */
    public java.lang.String getMessage() {
        return message;
    }

    /**
     * Sets the message value for this CatalogResult.
     * 
     * @param message
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }

    /**
     * Gets the errRNwId value for this CatalogResult.
     * 
     * @return errRNwId
     */
    public java.lang.String getErrRNwId() {
        return errRNwId;
    }

    /**
     * Sets the errRNwId value for this CatalogResult.
     * 
     * @param errRNwId
     */
    public void setErrRNwId(java.lang.String errRNwId) {
        this.errRNwId = errRNwId;
    }

    /**
     * Gets the RNwResults value for this CatalogResult.
     * 
     * @return RNwResults
     */
    public com.inter.iat.client.RNwResultType[] getRNwResults() {
        return RNwResults;
    }

    /**
     * Sets the RNwResults value for this CatalogResult.
     * 
     * @param RNwResults
     */
    public void setRNwResults(com.inter.iat.client.RNwResultType[] RNwResults) {
        this.RNwResults = RNwResults;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CatalogResult))
            return false;
        CatalogResult other = (CatalogResult) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && this.status == other.getStatus() && ((this.message == null && other.getMessage() == null) || (this.message != null && this.message.equals(other.getMessage()))) && ((this.errRNwId == null && other.getErrRNwId() == null) || (this.errRNwId != null && this.errRNwId.equals(other.getErrRNwId()))) && ((this.RNwResults == null && other.getRNwResults() == null) || (this.RNwResults != null && java.util.Arrays.equals(this.RNwResults, other.getRNwResults())));
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
        _hashCode += getStatus();
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        if (getErrRNwId() != null) {
            _hashCode += getErrRNwId().hashCode();
        }
        if (getRNwResults() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getRNwResults()); i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRNwResults(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(CatalogResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "CatalogResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "Status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "Message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errRNwId");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "ErrRNwId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("RNwResults");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "RNwResults"));
        elemField.setXmlType(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "RNwResultType"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "RNwResult"));
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
