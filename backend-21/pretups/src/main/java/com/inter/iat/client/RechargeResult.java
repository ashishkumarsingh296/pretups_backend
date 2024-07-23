/**
 * RechargeResult.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.iat.client;

public class RechargeResult implements java.io.Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private java.lang.String iatTrxId;

    private java.lang.String SNwTrxId;

    private java.util.Calendar iatTimeStamp;

    private int status;

    private java.lang.String message;

    public RechargeResult() {
    }

    public RechargeResult(java.lang.String iatTrxId, java.lang.String SNwTrxId, java.util.Calendar iatTimeStamp, int status, java.lang.String message) {
        this.iatTrxId = iatTrxId;
        this.SNwTrxId = SNwTrxId;
        this.iatTimeStamp = iatTimeStamp;
        this.status = status;
        this.message = message;
    }

    public String toString() {
        StringBuffer sbfObj = new StringBuffer();

        sbfObj.append("iatTrxId::=" + iatTrxId);
        sbfObj.append("SNwTrxId::=" + SNwTrxId);
        sbfObj.append("iatTimeStamp::=" + iatTimeStamp);
        sbfObj.append("status::=" + status);
        sbfObj.append("message::=" + message);

        return sbfObj.toString();
    }

    /**
     * Gets the iatTrxId value for this RechargeResult.
     * 
     * @return iatTrxId
     */
    public java.lang.String getIatTrxId() {
        return iatTrxId;
    }

    /**
     * Sets the iatTrxId value for this RechargeResult.
     * 
     * @param iatTrxId
     */
    public void setIatTrxId(java.lang.String iatTrxId) {
        this.iatTrxId = iatTrxId;
    }

    /**
     * Gets the SNwTrxId value for this RechargeResult.
     * 
     * @return SNwTrxId
     */
    public java.lang.String getSNwTrxId() {
        return SNwTrxId;
    }

    /**
     * Sets the SNwTrxId value for this RechargeResult.
     * 
     * @param SNwTrxId
     */
    public void setSNwTrxId(java.lang.String SNwTrxId) {
        this.SNwTrxId = SNwTrxId;
    }

    /**
     * Gets the iatTimeStamp value for this RechargeResult.
     * 
     * @return iatTimeStamp
     */
    public java.util.Calendar getIatTimeStamp() {
        return iatTimeStamp;
    }

    /**
     * Sets the iatTimeStamp value for this RechargeResult.
     * 
     * @param iatTimeStamp
     */
    public void setIatTimeStamp(java.util.Calendar iatTimeStamp) {
        this.iatTimeStamp = iatTimeStamp;
    }

    /**
     * Gets the status value for this RechargeResult.
     * 
     * @return status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the status value for this RechargeResult.
     * 
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Gets the message value for this RechargeResult.
     * 
     * @return message
     */
    public java.lang.String getMessage() {
        return message;
    }

    /**
     * Sets the message value for this RechargeResult.
     * 
     * @param message
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RechargeResult))
            return false;
        RechargeResult other = (RechargeResult) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && ((this.iatTrxId == null && other.getIatTrxId() == null) || (this.iatTrxId != null && this.iatTrxId.equals(other.getIatTrxId()))) && ((this.SNwTrxId == null && other.getSNwTrxId() == null) || (this.SNwTrxId != null && this.SNwTrxId.equals(other.getSNwTrxId()))) && ((this.iatTimeStamp == null && other.getIatTimeStamp() == null) || (this.iatTimeStamp != null && this.iatTimeStamp.equals(other.getIatTimeStamp()))) && this.status == other.getStatus() && ((this.message == null && other.getMessage() == null) || (this.message != null && this.message.equals(other.getMessage())));
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
        if (getIatTrxId() != null) {
            _hashCode += getIatTrxId().hashCode();
        }
        if (getSNwTrxId() != null) {
            _hashCode += getSNwTrxId().hashCode();
        }
        if (getIatTimeStamp() != null) {
            _hashCode += getIatTimeStamp().hashCode();
        }
        _hashCode += getStatus();
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(RechargeResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "RechargeResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("iatTrxId");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "IatTrxId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SNwTrxId");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "SNwTrxId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("iatTimeStamp");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "IatTimeStamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "Status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "Message"));
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
