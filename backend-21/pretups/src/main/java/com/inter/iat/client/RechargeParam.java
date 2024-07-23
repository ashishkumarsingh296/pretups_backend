/**
 * RechargeParam.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.iat.client;

public class RechargeParam implements java.io.Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private int serviceType;

    private java.lang.String SNwTrxId;

    private java.lang.String SNwId;

    private java.lang.String SNwType;

    private int SCountryCode;

    private int RCountryCode;

    private java.lang.String sendingBearer;

    private int rechargeType;

    private java.lang.String MSISDN1;

    private java.lang.String MSISDN2;

    private java.lang.String MSISDN3;

    private java.lang.String retailerId;

    private java.lang.String deviceId;

    private java.util.Calendar SNwTimeStamp;

    private double amount;

    private java.lang.String ext1;

    private java.lang.String ext2;

    private java.lang.String ext3;

    public RechargeParam() {
    }

    public RechargeParam(int serviceType, java.lang.String SNwTrxId, java.lang.String SNwId, java.lang.String SNwType, int SCountryCode, int RCountryCode, java.lang.String sendingBearer, int rechargeType, java.lang.String MSISDN1, java.lang.String MSISDN2, java.lang.String MSISDN3, java.lang.String retailerId, java.lang.String deviceId, java.util.Calendar SNwTimeStamp, double amount, java.lang.String ext1, java.lang.String ext2, java.lang.String ext3) {
        this.serviceType = serviceType;
        this.SNwTrxId = SNwTrxId;
        this.SNwId = SNwId;
        this.SNwType = SNwType;
        this.SCountryCode = SCountryCode;
        this.RCountryCode = RCountryCode;
        this.sendingBearer = sendingBearer;
        this.rechargeType = rechargeType;
        this.MSISDN1 = MSISDN1;
        this.MSISDN2 = MSISDN2;
        this.MSISDN3 = MSISDN3;
        this.retailerId = retailerId;
        this.deviceId = deviceId;
        this.SNwTimeStamp = SNwTimeStamp;
        this.amount = amount;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
    }

    public String toString() {
        StringBuffer sbfObj = new StringBuffer();

        sbfObj.append("serviceType::=" + serviceType);
        sbfObj.append("SNwTrxId::=" + SNwTrxId);
        sbfObj.append("SNwId::=" + SNwId);
        sbfObj.append("SNwType::=" + SNwType);
        sbfObj.append("SCountryCode::=" + SCountryCode);
        sbfObj.append("RCountryCode::=" + RCountryCode);
        sbfObj.append("sendingBearer::=" + sendingBearer);
        sbfObj.append("rechargeType::=" + rechargeType);
        sbfObj.append("MSISDN1::=" + MSISDN1);
        sbfObj.append("MSISDN2::=" + MSISDN2);
        sbfObj.append("MSISDN3::=" + MSISDN3);
        sbfObj.append("retailerId::=" + retailerId);
        sbfObj.append("deviceId::=" + deviceId);
        sbfObj.append("SNwTimeStamp::=" + SNwTimeStamp);
        sbfObj.append("amount::=" + amount);
        sbfObj.append("ext1::=" + ext1);
        sbfObj.append("ext2::=" + ext2);
        sbfObj.append("ext3::=" + ext3);

        return sbfObj.toString();

    }

    /**
     * Gets the serviceType value for this RechargeParam.
     * 
     * @return serviceType
     */
    public int getServiceType() {
        return serviceType;
    }

    /**
     * Sets the serviceType value for this RechargeParam.
     * 
     * @param serviceType
     */
    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * Gets the SNwTrxId value for this RechargeParam.
     * 
     * @return SNwTrxId
     */
    public java.lang.String getSNwTrxId() {
        return SNwTrxId;
    }

    /**
     * Sets the SNwTrxId value for this RechargeParam.
     * 
     * @param SNwTrxId
     */
    public void setSNwTrxId(java.lang.String SNwTrxId) {
        this.SNwTrxId = SNwTrxId;
    }

    /**
     * Gets the SNwId value for this RechargeParam.
     * 
     * @return SNwId
     */
    public java.lang.String getSNwId() {
        return SNwId;
    }

    /**
     * Sets the SNwId value for this RechargeParam.
     * 
     * @param SNwId
     */
    public void setSNwId(java.lang.String SNwId) {
        this.SNwId = SNwId;
    }

    /**
     * Gets the SNwType value for this RechargeParam.
     * 
     * @return SNwType
     */
    public java.lang.String getSNwType() {
        return SNwType;
    }

    /**
     * Sets the SNwType value for this RechargeParam.
     * 
     * @param SNwType
     */
    public void setSNwType(java.lang.String SNwType) {
        this.SNwType = SNwType;
    }

    /**
     * Gets the SCountryCode value for this RechargeParam.
     * 
     * @return SCountryCode
     */
    public int getSCountryCode() {
        return SCountryCode;
    }

    /**
     * Sets the SCountryCode value for this RechargeParam.
     * 
     * @param SCountryCode
     */
    public void setSCountryCode(int SCountryCode) {
        this.SCountryCode = SCountryCode;
    }

    /**
     * Gets the RCountryCode value for this RechargeParam.
     * 
     * @return RCountryCode
     */
    public int getRCountryCode() {
        return RCountryCode;
    }

    /**
     * Sets the RCountryCode value for this RechargeParam.
     * 
     * @param RCountryCode
     */
    public void setRCountryCode(int RCountryCode) {
        this.RCountryCode = RCountryCode;
    }

    /**
     * Gets the sendingBearer value for this RechargeParam.
     * 
     * @return sendingBearer
     */
    public java.lang.String getSendingBearer() {
        return sendingBearer;
    }

    /**
     * Sets the sendingBearer value for this RechargeParam.
     * 
     * @param sendingBearer
     */
    public void setSendingBearer(java.lang.String sendingBearer) {
        this.sendingBearer = sendingBearer;
    }

    /**
     * Gets the rechargeType value for this RechargeParam.
     * 
     * @return rechargeType
     */
    public int getRechargeType() {
        return rechargeType;
    }

    /**
     * Sets the rechargeType value for this RechargeParam.
     * 
     * @param rechargeType
     */
    public void setRechargeType(int rechargeType) {
        this.rechargeType = rechargeType;
    }

    /**
     * Gets the MSISDN1 value for this RechargeParam.
     * 
     * @return MSISDN1
     */
    public java.lang.String getMSISDN1() {
        return MSISDN1;
    }

    /**
     * Sets the MSISDN1 value for this RechargeParam.
     * 
     * @param MSISDN1
     */
    public void setMSISDN1(java.lang.String MSISDN1) {
        this.MSISDN1 = MSISDN1;
    }

    /**
     * Gets the MSISDN2 value for this RechargeParam.
     * 
     * @return MSISDN2
     */
    public java.lang.String getMSISDN2() {
        return MSISDN2;
    }

    /**
     * Sets the MSISDN2 value for this RechargeParam.
     * 
     * @param MSISDN2
     */
    public void setMSISDN2(java.lang.String MSISDN2) {
        this.MSISDN2 = MSISDN2;
    }

    /**
     * Gets the MSISDN3 value for this RechargeParam.
     * 
     * @return MSISDN3
     */
    public java.lang.String getMSISDN3() {
        return MSISDN3;
    }

    /**
     * Sets the MSISDN3 value for this RechargeParam.
     * 
     * @param MSISDN3
     */
    public void setMSISDN3(java.lang.String MSISDN3) {
        this.MSISDN3 = MSISDN3;
    }

    /**
     * Gets the retailerId value for this RechargeParam.
     * 
     * @return retailerId
     */
    public java.lang.String getRetailerId() {
        return retailerId;
    }

    /**
     * Sets the retailerId value for this RechargeParam.
     * 
     * @param retailerId
     */
    public void setRetailerId(java.lang.String retailerId) {
        this.retailerId = retailerId;
    }

    /**
     * Gets the deviceId value for this RechargeParam.
     * 
     * @return deviceId
     */
    public java.lang.String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the deviceId value for this RechargeParam.
     * 
     * @param deviceId
     */
    public void setDeviceId(java.lang.String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Gets the SNwTimeStamp value for this RechargeParam.
     * 
     * @return SNwTimeStamp
     */
    public java.util.Calendar getSNwTimeStamp() {
        return SNwTimeStamp;
    }

    /**
     * Sets the SNwTimeStamp value for this RechargeParam.
     * 
     * @param SNwTimeStamp
     */
    public void setSNwTimeStamp(java.util.Calendar SNwTimeStamp) {
        this.SNwTimeStamp = SNwTimeStamp;
    }

    /**
     * Gets the amount value for this RechargeParam.
     * 
     * @return amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the amount value for this RechargeParam.
     * 
     * @param amount
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Gets the ext1 value for this RechargeParam.
     * 
     * @return ext1
     */
    public java.lang.String getExt1() {
        return ext1;
    }

    /**
     * Sets the ext1 value for this RechargeParam.
     * 
     * @param ext1
     */
    public void setExt1(java.lang.String ext1) {
        this.ext1 = ext1;
    }

    /**
     * Gets the ext2 value for this RechargeParam.
     * 
     * @return ext2
     */
    public java.lang.String getExt2() {
        return ext2;
    }

    /**
     * Sets the ext2 value for this RechargeParam.
     * 
     * @param ext2
     */
    public void setExt2(java.lang.String ext2) {
        this.ext2 = ext2;
    }

    /**
     * Gets the ext3 value for this RechargeParam.
     * 
     * @return ext3
     */
    public java.lang.String getExt3() {
        return ext3;
    }

    /**
     * Sets the ext3 value for this RechargeParam.
     * 
     * @param ext3
     */
    public void setExt3(java.lang.String ext3) {
        this.ext3 = ext3;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RechargeParam))
            return false;
        RechargeParam other = (RechargeParam) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && this.serviceType == other.getServiceType() && ((this.SNwTrxId == null && other.getSNwTrxId() == null) || (this.SNwTrxId != null && this.SNwTrxId.equals(other.getSNwTrxId()))) && ((this.SNwId == null && other.getSNwId() == null) || (this.SNwId != null && this.SNwId.equals(other.getSNwId()))) && ((this.SNwType == null && other.getSNwType() == null) || (this.SNwType != null && this.SNwType.equals(other.getSNwType()))) && this.SCountryCode == other.getSCountryCode() && this.RCountryCode == other.getRCountryCode() && ((this.sendingBearer == null && other.getSendingBearer() == null) || (this.sendingBearer != null && this.sendingBearer.equals(other.getSendingBearer()))) && this.rechargeType == other.getRechargeType() && ((this.MSISDN1 == null && other.getMSISDN1() == null) || (this.MSISDN1 != null && this.MSISDN1.equals(other.getMSISDN1()))) && ((this.MSISDN2 == null && other.getMSISDN2() == null) || (this.MSISDN2 != null && this.MSISDN2.equals(other.getMSISDN2()))) && ((this.MSISDN3 == null && other.getMSISDN3() == null) || (this.MSISDN3 != null && this.MSISDN3.equals(other.getMSISDN3()))) && ((this.retailerId == null && other.getRetailerId() == null) || (this.retailerId != null && this.retailerId.equals(other.getRetailerId()))) && ((this.deviceId == null && other.getDeviceId() == null) || (this.deviceId != null && this.deviceId.equals(other.getDeviceId()))) && ((this.SNwTimeStamp == null && other.getSNwTimeStamp() == null) || (this.SNwTimeStamp != null && this.SNwTimeStamp.equals(other.getSNwTimeStamp()))) && this.amount == other.getAmount() && ((this.ext1 == null && other.getExt1() == null) || (this.ext1 != null && this.ext1.equals(other.getExt1()))) && ((this.ext2 == null && other.getExt2() == null) || (this.ext2 != null && this.ext2.equals(other.getExt2()))) && ((this.ext3 == null && other.getExt3() == null) || (this.ext3 != null && this.ext3.equals(other.getExt3())));
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
        _hashCode += getServiceType();
        if (getSNwTrxId() != null) {
            _hashCode += getSNwTrxId().hashCode();
        }
        if (getSNwId() != null) {
            _hashCode += getSNwId().hashCode();
        }
        if (getSNwType() != null) {
            _hashCode += getSNwType().hashCode();
        }
        _hashCode += getSCountryCode();
        _hashCode += getRCountryCode();
        if (getSendingBearer() != null) {
            _hashCode += getSendingBearer().hashCode();
        }
        _hashCode += getRechargeType();
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
        if (getSNwTimeStamp() != null) {
            _hashCode += getSNwTimeStamp().hashCode();
        }
        _hashCode += new Double(getAmount()).hashCode();
        if (getExt1() != null) {
            _hashCode += getExt1().hashCode();
        }
        if (getExt2() != null) {
            _hashCode += getExt2().hashCode();
        }
        if (getExt3() != null) {
            _hashCode += getExt3().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(RechargeParam.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "RechargeParam"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceType");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "ServiceType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SNwTrxId");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "SNwTrxId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SNwId");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "SNwId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SNwType");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "SNwType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
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
        elemField.setFieldName("sendingBearer");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "SendingBearer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rechargeType");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "RechargeType"));
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
        elemField.setNillable(true);
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
        elemField.setFieldName("SNwTimeStamp");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "SNwTimeStamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "Amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ext1");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "Ext1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ext2");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "Ext2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ext3");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "Ext3"));
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
