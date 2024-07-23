/**
 * CheckStatusResult.java
 * 
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.iat.client;

public class CheckStatusResult implements java.io.Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private int status;

    private int level;

    private int iatReasonCode;

    private java.lang.String iatReasonMessage;

    private java.lang.String RNwReasonCode;

    private java.lang.String RNwReasonMessage;

    private double iatReceivedAmount;

    private double fees;

    private double provRatio;

    private double exchangeRate;

    private double RBonus;

    private double RPfReceivedAmount;

    private double recipientReceivedAmount;

    private java.lang.String RNwId;

    public CheckStatusResult() {
    }

    public CheckStatusResult(int status, int level, int iatReasonCode, java.lang.String iatReasonMessage, java.lang.String RNwReasonCode, java.lang.String RNwReasonMessage, double iatReceivedAmount, double fees, double provRatio, double exchangeRate, double RBonus, double RPfReceivedAmount, double recipientReceivedAmount, java.lang.String RNwId) {
        this.status = status;
        this.level = level;
        this.iatReasonCode = iatReasonCode;
        this.iatReasonMessage = iatReasonMessage;
        this.RNwReasonCode = RNwReasonCode;
        this.RNwReasonMessage = RNwReasonMessage;
        this.iatReceivedAmount = iatReceivedAmount;
        this.fees = fees;
        this.provRatio = provRatio;
        this.exchangeRate = exchangeRate;
        this.RBonus = RBonus;
        this.RPfReceivedAmount = RPfReceivedAmount;
        this.recipientReceivedAmount = recipientReceivedAmount;
        this.RNwId = RNwId;
    }

    public String toString() {
        StringBuffer sbfObj = new StringBuffer();
        sbfObj.append("status::=" + status);
        sbfObj.append("level::=" + level);
        sbfObj.append("iatReasonCode::=" + iatReasonCode);
        sbfObj.append("iatReasonMessage::=" + iatReasonMessage);
        sbfObj.append("RNwReasonCode::=" + RNwReasonCode);
        sbfObj.append("RNwReasonMessage::=" + RNwReasonMessage);
        sbfObj.append("iatReceivedAmount::=" + iatReceivedAmount);
        sbfObj.append("fees::=" + fees);
        sbfObj.append("provRatio::=" + provRatio);
        sbfObj.append("exchangeRate::=" + exchangeRate);
        sbfObj.append("RBonus::=" + RBonus);
        sbfObj.append("RPfReceivedAmount::=" + RPfReceivedAmount);
        sbfObj.append("recipientReceivedAmount::=" + recipientReceivedAmount);
        sbfObj.append("RNwId::=" + RNwId);

        return sbfObj.toString();
    }

    /**
     * Gets the status value for this CheckStatusResult.
     * 
     * @return status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the status value for this CheckStatusResult.
     * 
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Gets the level value for this CheckStatusResult.
     * 
     * @return level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the level value for this CheckStatusResult.
     * 
     * @param level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Gets the iatReasonCode value for this CheckStatusResult.
     * 
     * @return iatReasonCode
     */
    public int getIatReasonCode() {
        return iatReasonCode;
    }

    /**
     * Sets the iatReasonCode value for this CheckStatusResult.
     * 
     * @param iatReasonCode
     */
    public void setIatReasonCode(int iatReasonCode) {
        this.iatReasonCode = iatReasonCode;
    }

    /**
     * Gets the iatReasonMessage value for this CheckStatusResult.
     * 
     * @return iatReasonMessage
     */
    public java.lang.String getIatReasonMessage() {
        return iatReasonMessage;
    }

    /**
     * Sets the iatReasonMessage value for this CheckStatusResult.
     * 
     * @param iatReasonMessage
     */
    public void setIatReasonMessage(java.lang.String iatReasonMessage) {
        this.iatReasonMessage = iatReasonMessage;
    }

    /**
     * Gets the RNwReasonCode value for this CheckStatusResult.
     * 
     * @return RNwReasonCode
     */
    public java.lang.String getRNwReasonCode() {
        return RNwReasonCode;
    }

    /**
     * Sets the RNwReasonCode value for this CheckStatusResult.
     * 
     * @param RNwReasonCode
     */
    public void setRNwReasonCode(java.lang.String RNwReasonCode) {
        this.RNwReasonCode = RNwReasonCode;
    }

    /**
     * Gets the RNwReasonMessage value for this CheckStatusResult.
     * 
     * @return RNwReasonMessage
     */
    public java.lang.String getRNwReasonMessage() {
        return RNwReasonMessage;
    }

    /**
     * Sets the RNwReasonMessage value for this CheckStatusResult.
     * 
     * @param RNwReasonMessage
     */
    public void setRNwReasonMessage(java.lang.String RNwReasonMessage) {
        this.RNwReasonMessage = RNwReasonMessage;
    }

    /**
     * Gets the iatReceivedAmount value for this CheckStatusResult.
     * 
     * @return iatReceivedAmount
     */
    public double getIatReceivedAmount() {
        return iatReceivedAmount;
    }

    /**
     * Sets the iatReceivedAmount value for this CheckStatusResult.
     * 
     * @param iatReceivedAmount
     */
    public void setIatReceivedAmount(double iatReceivedAmount) {
        this.iatReceivedAmount = iatReceivedAmount;
    }

    /**
     * Gets the fees value for this CheckStatusResult.
     * 
     * @return fees
     */
    public double getFees() {
        return fees;
    }

    /**
     * Sets the fees value for this CheckStatusResult.
     * 
     * @param fees
     */
    public void setFees(double fees) {
        this.fees = fees;
    }

    /**
     * Gets the provRatio value for this CheckStatusResult.
     * 
     * @return provRatio
     */
    public double getProvRatio() {
        return provRatio;
    }

    /**
     * Sets the provRatio value for this CheckStatusResult.
     * 
     * @param provRatio
     */
    public void setProvRatio(double provRatio) {
        this.provRatio = provRatio;
    }

    /**
     * Gets the exchangeRate value for this CheckStatusResult.
     * 
     * @return exchangeRate
     */
    public double getExchangeRate() {
        return exchangeRate;
    }

    /**
     * Sets the exchangeRate value for this CheckStatusResult.
     * 
     * @param exchangeRate
     */
    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    /**
     * Gets the RBonus value for this CheckStatusResult.
     * 
     * @return RBonus
     */
    public double getRBonus() {
        return RBonus;
    }

    /**
     * Sets the RBonus value for this CheckStatusResult.
     * 
     * @param RBonus
     */
    public void setRBonus(double RBonus) {
        this.RBonus = RBonus;
    }

    /**
     * Gets the RPfReceivedAmount value for this CheckStatusResult.
     * 
     * @return RPfReceivedAmount
     */
    public double getRPfReceivedAmount() {
        return RPfReceivedAmount;
    }

    /**
     * Sets the RPfReceivedAmount value for this CheckStatusResult.
     * 
     * @param RPfReceivedAmount
     */
    public void setRPfReceivedAmount(double RPfReceivedAmount) {
        this.RPfReceivedAmount = RPfReceivedAmount;
    }

    /**
     * Gets the recipientReceivedAmount value for this CheckStatusResult.
     * 
     * @return recipientReceivedAmount
     */
    public double getRecipientReceivedAmount() {
        return recipientReceivedAmount;
    }

    /**
     * Sets the recipientReceivedAmount value for this CheckStatusResult.
     * 
     * @param recipientReceivedAmount
     */
    public void setRecipientReceivedAmount(double recipientReceivedAmount) {
        this.recipientReceivedAmount = recipientReceivedAmount;
    }

    /**
     * Gets the RNwId value for this CheckStatusResult.
     * 
     * @return RNwId
     */
    public java.lang.String getRNwId() {
        return RNwId;
    }

    /**
     * Sets the RNwId value for this CheckStatusResult.
     * 
     * @param RNwId
     */
    public void setRNwId(java.lang.String RNwId) {
        this.RNwId = RNwId;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CheckStatusResult))
            return false;
        CheckStatusResult other = (CheckStatusResult) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && this.status == other.getStatus() && this.level == other.getLevel() && this.iatReasonCode == other.getIatReasonCode() && ((this.iatReasonMessage == null && other.getIatReasonMessage() == null) || (this.iatReasonMessage != null && this.iatReasonMessage.equals(other.getIatReasonMessage()))) && ((this.RNwReasonCode == null && other.getRNwReasonCode() == null) || (this.RNwReasonCode != null && this.RNwReasonCode.equals(other.getRNwReasonCode()))) && ((this.RNwReasonMessage == null && other.getRNwReasonMessage() == null) || (this.RNwReasonMessage != null && this.RNwReasonMessage.equals(other.getRNwReasonMessage()))) && this.iatReceivedAmount == other.getIatReceivedAmount() && this.fees == other.getFees() && this.provRatio == other.getProvRatio() && this.exchangeRate == other.getExchangeRate() && this.RBonus == other.getRBonus() && this.RPfReceivedAmount == other.getRPfReceivedAmount() && this.recipientReceivedAmount == other.getRecipientReceivedAmount() && ((this.RNwId == null && other.getRNwId() == null) || (this.RNwId != null && this.RNwId.equals(other.getRNwId())));
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
        _hashCode += getLevel();
        _hashCode += getIatReasonCode();
        if (getIatReasonMessage() != null) {
            _hashCode += getIatReasonMessage().hashCode();
        }
        if (getRNwReasonCode() != null) {
            _hashCode += getRNwReasonCode().hashCode();
        }
        if (getRNwReasonMessage() != null) {
            _hashCode += getRNwReasonMessage().hashCode();
        }
        _hashCode += new Double(getIatReceivedAmount()).hashCode();
        _hashCode += new Double(getFees()).hashCode();
        _hashCode += new Double(getProvRatio()).hashCode();
        _hashCode += new Double(getExchangeRate()).hashCode();
        _hashCode += new Double(getRBonus()).hashCode();
        _hashCode += new Double(getRPfReceivedAmount()).hashCode();
        _hashCode += new Double(getRecipientReceivedAmount()).hashCode();
        if (getRNwId() != null) {
            _hashCode += getRNwId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(CheckStatusResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "CheckStatusResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "Status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("level");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "Level"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("iatReasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "IatReasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("iatReasonMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "IatReasonMessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("RNwReasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "RNwReasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("RNwReasonMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "RNwReasonMessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("iatReceivedAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "IatReceivedAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fees");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "Fees"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("provRatio");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "ProvRatio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("exchangeRate");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "ExchangeRate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("RBonus");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "RBonus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("RPfReceivedAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "RPfReceivedAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recipientReceivedAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "RecipientReceivedAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("RNwId");
        elemField.setXmlName(new javax.xml.namespace.QName("java:com.wha.iah.pretups.ws", "RNwId"));
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
