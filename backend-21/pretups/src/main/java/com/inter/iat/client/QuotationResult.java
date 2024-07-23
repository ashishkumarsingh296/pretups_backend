/**
 * QuotationResult.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.iat.client;

public class QuotationResult implements java.io.Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private int status;

    private java.lang.String message;

    private double receivedAmount;

    private java.lang.String receivableId;

    private java.util.Calendar quotationEndValidityDate;

    public QuotationResult() {
    }

    public QuotationResult(int status, java.lang.String message, double receivedAmount, java.lang.String receivableId, java.util.Calendar quotationEndValidityDate) {
        this.status = status;
        this.message = message;
        this.receivedAmount = receivedAmount;
        this.receivableId = receivableId;
        this.quotationEndValidityDate = quotationEndValidityDate;
    }

    /**
     * Gets the status value for this QuotationResult.
     * 
     * @return status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the status value for this QuotationResult.
     * 
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Gets the message value for this QuotationResult.
     * 
     * @return message
     */
    public java.lang.String getMessage() {
        return message;
    }

    /**
     * Sets the message value for this QuotationResult.
     * 
     * @param message
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }

    /**
     * Gets the receivedAmount value for this QuotationResult.
     * 
     * @return receivedAmount
     */
    public double getReceivedAmount() {
        return receivedAmount;
    }

    /**
     * Sets the receivedAmount value for this QuotationResult.
     * 
     * @param receivedAmount
     */
    public void setReceivedAmount(double receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    /**
     * Gets the receivableId value for this QuotationResult.
     * 
     * @return receivableId
     */
    public java.lang.String getReceivableId() {
        return receivableId;
    }

    /**
     * Sets the receivableId value for this QuotationResult.
     * 
     * @param receivableId
     */
    public void setReceivableId(java.lang.String receivableId) {
        this.receivableId = receivableId;
    }

    /**
     * Gets the quotationEndValidityDate value for this QuotationResult.
     * 
     * @return quotationEndValidityDate
     */
    public java.util.Calendar getQuotationEndValidityDate() {
        return quotationEndValidityDate;
    }

    /**
     * Sets the quotationEndValidityDate value for this QuotationResult.
     * 
     * @param quotationEndValidityDate
     */
    public void setQuotationEndValidityDate(java.util.Calendar quotationEndValidityDate) {
        this.quotationEndValidityDate = quotationEndValidityDate;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof QuotationResult))
            return false;
        QuotationResult other = (QuotationResult) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && this.status == other.getStatus() && ((this.message == null && other.getMessage() == null) || (this.message != null && this.message.equals(other.getMessage()))) && this.receivedAmount == other.getReceivedAmount() && ((this.receivableId == null && other.getReceivableId() == null) || (this.receivableId != null && this.receivableId.equals(other.getReceivableId()))) && ((this.quotationEndValidityDate == null && other.getQuotationEndValidityDate() == null) || (this.quotationEndValidityDate != null && this.quotationEndValidityDate.equals(other.getQuotationEndValidityDate())));
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
        _hashCode += new Double(getReceivedAmount()).hashCode();
        if (getReceivableId() != null) {
            _hashCode += getReceivableId().hashCode();
        }
        if (getQuotationEndValidityDate() != null) {
            _hashCode += getQuotationEndValidityDate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(QuotationResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "QuotationResult"));
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
        elemField.setFieldName("receivedAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "ReceivedAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("receivableId");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "ReceivableId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quotationEndValidityDate");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "QuotationEndValidityDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
