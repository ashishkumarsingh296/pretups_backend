/**
 * QuotationParam.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.iat.client;

public class QuotationParam implements java.io.Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private java.lang.String SNwId;

    private java.util.Calendar SNwTimeStamp;

    private int SCountryCode;

    private int RCountryCode;

    private java.lang.String MSISDN1;

    private java.lang.String MSISDN2;

    private java.lang.String MSISDN3;

    private java.lang.String retailerId;

    private java.lang.String deviceId;

    private java.lang.String amount;

    private java.lang.String receivableValueSc;

    private java.lang.String receivableId;

    public QuotationParam() {
    }

    public QuotationParam(java.lang.String SNwId, java.util.Calendar SNwTimeStamp, int SCountryCode, int RCountryCode, java.lang.String MSISDN1, java.lang.String MSISDN2, java.lang.String MSISDN3, java.lang.String retailerId, java.lang.String deviceId, java.lang.String amount, java.lang.String receivableValueSc, java.lang.String receivableId) {
        this.SNwId = SNwId;
        this.SNwTimeStamp = SNwTimeStamp;
        this.SCountryCode = SCountryCode;
        this.RCountryCode = RCountryCode;
        this.MSISDN1 = MSISDN1;
        this.MSISDN2 = MSISDN2;
        this.MSISDN3 = MSISDN3;
        this.retailerId = retailerId;
        this.deviceId = deviceId;
        this.amount = amount;
        this.receivableValueSc = receivableValueSc;
        this.receivableId = receivableId;
    }

    /**
     * Gets the SNwId value for this QuotationParam.
     * 
     * @return SNwId
     */
    public java.lang.String getSNwId() {
        return SNwId;
    }

    /**
     * Sets the SNwId value for this QuotationParam.
     * 
     * @param SNwId
     */
    public void setSNwId(java.lang.String SNwId) {
        this.SNwId = SNwId;
    }

    /**
     * Gets the SNwTimeStamp value for this QuotationParam.
     * 
     * @return SNwTimeStamp
     */
    public java.util.Calendar getSNwTimeStamp() {
        return SNwTimeStamp;
    }

    /**
     * Sets the SNwTimeStamp value for this QuotationParam.
     * 
     * @param SNwTimeStamp
     */
    public void setSNwTimeStamp(java.util.Calendar SNwTimeStamp) {
        this.SNwTimeStamp = SNwTimeStamp;
    }

    /**
     * Gets the SCountryCode value for this QuotationParam.
     * 
     * @return SCountryCode
     */
    public int getSCountryCode() {
        return SCountryCode;
    }

    /**
     * Sets the SCountryCode value for this QuotationParam.
     * 
     * @param SCountryCode
     */
    public void setSCountryCode(int SCountryCode) {
        this.SCountryCode = SCountryCode;
    }

    /**
     * Gets the RCountryCode value for this QuotationParam.
     * 
     * @return RCountryCode
     */
    public int getRCountryCode() {
        return RCountryCode;
    }

    /**
     * Sets the RCountryCode value for this QuotationParam.
     * 
     * @param RCountryCode
     */
    public void setRCountryCode(int RCountryCode) {
        this.RCountryCode = RCountryCode;
    }

    /**
     * Gets the MSISDN1 value for this QuotationParam.
     * 
     * @return MSISDN1
     */
    public java.lang.String getMSISDN1() {
        return MSISDN1;
    }

    /**
     * Sets the MSISDN1 value for this QuotationParam.
     * 
     * @param MSISDN1
     */
    public void setMSISDN1(java.lang.String MSISDN1) {
        this.MSISDN1 = MSISDN1;
    }

    /**
     * Gets the MSISDN2 value for this QuotationParam.
     * 
     * @return MSISDN2
     */
    public java.lang.String getMSISDN2() {
        return MSISDN2;
    }

    /**
     * Sets the MSISDN2 value for this QuotationParam.
     * 
     * @param MSISDN2
     */
    public void setMSISDN2(java.lang.String MSISDN2) {
        this.MSISDN2 = MSISDN2;
    }

    /**
     * Gets the MSISDN3 value for this QuotationParam.
     * 
     * @return MSISDN3
     */
    public java.lang.String getMSISDN3() {
        return MSISDN3;
    }

    /**
     * Sets the MSISDN3 value for this QuotationParam.
     * 
     * @param MSISDN3
     */
    public void setMSISDN3(java.lang.String MSISDN3) {
        this.MSISDN3 = MSISDN3;
    }

    /**
     * Gets the retailerId value for this QuotationParam.
     * 
     * @return retailerId
     */
    public java.lang.String getRetailerId() {
        return retailerId;
    }

    /**
     * Sets the retailerId value for this QuotationParam.
     * 
     * @param retailerId
     */
    public void setRetailerId(java.lang.String retailerId) {
        this.retailerId = retailerId;
    }

    /**
     * Gets the deviceId value for this QuotationParam.
     * 
     * @return deviceId
     */
    public java.lang.String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the deviceId value for this QuotationParam.
     * 
     * @param deviceId
     */
    public void setDeviceId(java.lang.String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Gets the amount value for this QuotationParam.
     * 
     * @return amount
     */
    public java.lang.String getAmount() {
        return amount;
    }

    /**
     * Sets the amount value for this QuotationParam.
     * 
     * @param amount
     */
    public void setAmount(java.lang.String amount) {
        this.amount = amount;
    }

    /**
     * Gets the receivableValueSc value for this QuotationParam.
     * 
     * @return receivableValueSc
     */
    public java.lang.String getReceivableValueSc() {
        return receivableValueSc;
    }

    /**
     * Sets the receivableValueSc value for this QuotationParam.
     * 
     * @param receivableValueSc
     */
    public void setReceivableValueSc(java.lang.String receivableValueSc) {
        this.receivableValueSc = receivableValueSc;
    }

    /**
     * Gets the receivableId value for this QuotationParam.
     * 
     * @return receivableId
     */
    public java.lang.String getReceivableId() {
        return receivableId;
    }

    /**
     * Sets the receivableId value for this QuotationParam.
     * 
     * @param receivableId
     */
    public void setReceivableId(java.lang.String receivableId) {
        this.receivableId = receivableId;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof QuotationParam))
            return false;
        QuotationParam other = (QuotationParam) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && ((this.SNwId == null && other.getSNwId() == null) || (this.SNwId != null && this.SNwId.equals(other.getSNwId()))) && ((this.SNwTimeStamp == null && other.getSNwTimeStamp() == null) || (this.SNwTimeStamp != null && this.SNwTimeStamp.equals(other.getSNwTimeStamp()))) && this.SCountryCode == other.getSCountryCode() && this.RCountryCode == other.getRCountryCode() && ((this.MSISDN1 == null && other.getMSISDN1() == null) || (this.MSISDN1 != null && this.MSISDN1.equals(other.getMSISDN1()))) && ((this.MSISDN2 == null && other.getMSISDN2() == null) || (this.MSISDN2 != null && this.MSISDN2.equals(other.getMSISDN2()))) && ((this.MSISDN3 == null && other.getMSISDN3() == null) || (this.MSISDN3 != null && this.MSISDN3.equals(other.getMSISDN3()))) && ((this.retailerId == null && other.getRetailerId() == null) || (this.retailerId != null && this.retailerId.equals(other.getRetailerId()))) && ((this.deviceId == null && other.getDeviceId() == null) || (this.deviceId != null && this.deviceId.equals(other.getDeviceId()))) && ((this.amount == null && other.getAmount() == null) || (this.amount != null && this.amount.equals(other.getAmount()))) && ((this.receivableValueSc == null && other.getReceivableValueSc() == null) || (this.receivableValueSc != null && this.receivableValueSc.equals(other.getReceivableValueSc()))) && ((this.receivableId == null && other.getReceivableId() == null) || (this.receivableId != null && this.receivableId.equals(other.getReceivableId())));
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
        _hashCode += getRCountryCode();
        if (getMSISDN1() != null) {
            _hashCode += getMSISDN1().hashCode();
        }
        if (getMSISDN2() != null) {
            _hashCode += getMSISDN2().hashCode();
        }
        if (getMSISDN3() != null) {
            _hashCode += getMSISDN3().hashCode();
        }
        if (getRetailerId() != null) {
            _hashCode += getRetailerId().hashCode();
        }
        if (getDeviceId() != null) {
            _hashCode += getDeviceId().hashCode();
        }
        if (getAmount() != null) {
            _hashCode += getAmount().hashCode();
        }
        if (getReceivableValueSc() != null) {
            _hashCode += getReceivableValueSc().hashCode();
        }
        if (getReceivableId() != null) {
            _hashCode += getReceivableId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(QuotationParam.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "QuotationParam"));
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
        elemField.setFieldName("RCountryCode");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "RCountryCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MSISDN1");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "MSISDN1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MSISDN2");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "MSISDN2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MSISDN3");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "MSISDN3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("retailerId");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "RetailerId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deviceId");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "DeviceId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "Amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("receivableValueSc");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "ReceivableValueSc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("receivableId");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "ReceivableId"));
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
