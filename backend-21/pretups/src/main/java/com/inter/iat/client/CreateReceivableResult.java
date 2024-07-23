/**
 * CreateReceivableResult.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.iat.client;

public class CreateReceivableResult implements java.io.Serializable {
    private int status;

    private java.lang.String message;

    private java.lang.String receivableId;

    private double receivableValueEuro;

    private java.util.Calendar receivableEndValidityDate;

    public CreateReceivableResult() {
    }

    public CreateReceivableResult(int status, java.lang.String message, java.lang.String receivableId, double receivableValueEuro, java.util.Calendar receivableEndValidityDate) {
        this.status = status;
        this.message = message;
        this.receivableId = receivableId;
        this.receivableValueEuro = receivableValueEuro;
        this.receivableEndValidityDate = receivableEndValidityDate;
    }

    /**
     * Gets the status value for this CreateReceivableResult.
     * 
     * @return status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the status value for this CreateReceivableResult.
     * 
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Gets the message value for this CreateReceivableResult.
     * 
     * @return message
     */
    public java.lang.String getMessage() {
        return message;
    }

    /**
     * Sets the message value for this CreateReceivableResult.
     * 
     * @param message
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }

    /**
     * Gets the receivableId value for this CreateReceivableResult.
     * 
     * @return receivableId
     */
    public java.lang.String getReceivableId() {
        return receivableId;
    }

    /**
     * Sets the receivableId value for this CreateReceivableResult.
     * 
     * @param receivableId
     */
    public void setReceivableId(java.lang.String receivableId) {
        this.receivableId = receivableId;
    }

    /**
     * Gets the receivableValueEuro value for this CreateReceivableResult.
     * 
     * @return receivableValueEuro
     */
    public double getReceivableValueEuro() {
        return receivableValueEuro;
    }

    /**
     * Sets the receivableValueEuro value for this CreateReceivableResult.
     * 
     * @param receivableValueEuro
     */
    public void setReceivableValueEuro(double receivableValueEuro) {
        this.receivableValueEuro = receivableValueEuro;
    }

    /**
     * Gets the receivableEndValidityDate value for this CreateReceivableResult.
     * 
     * @return receivableEndValidityDate
     */
    public java.util.Calendar getReceivableEndValidityDate() {
        return receivableEndValidityDate;
    }

    /**
     * Sets the receivableEndValidityDate value for this CreateReceivableResult.
     * 
     * @param receivableEndValidityDate
     */
    public void setReceivableEndValidityDate(java.util.Calendar receivableEndValidityDate) {
        this.receivableEndValidityDate = receivableEndValidityDate;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CreateReceivableResult))
            return false;
        CreateReceivableResult other = (CreateReceivableResult) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && this.status == other.getStatus() && ((this.message == null && other.getMessage() == null) || (this.message != null && this.message.equals(other.getMessage()))) && ((this.receivableId == null && other.getReceivableId() == null) || (this.receivableId != null && this.receivableId.equals(other.getReceivableId()))) && this.receivableValueEuro == other.getReceivableValueEuro() && ((this.receivableEndValidityDate == null && other.getReceivableEndValidityDate() == null) || (this.receivableEndValidityDate != null && this.receivableEndValidityDate.equals(other.getReceivableEndValidityDate())));
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
        if (getReceivableId() != null) {
            _hashCode += getReceivableId().hashCode();
        }
        _hashCode += new Double(getReceivableValueEuro()).hashCode();
        if (getReceivableEndValidityDate() != null) {
            _hashCode += getReceivableEndValidityDate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(CreateReceivableResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "CreateReceivableResult"));
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
        elemField.setFieldName("receivableId");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "ReceivableId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("receivableValueEuro");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "ReceivableValueEuro"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("receivableEndValidityDate");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "ReceivableEndValidityDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
