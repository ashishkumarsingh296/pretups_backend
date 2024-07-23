/**
 * VoucherRechargeRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge;

public class VoucherRechargeRequest  implements java.io.Serializable {
    private java.lang.String logID;

    private java.lang.String subscriberNo;

    private java.lang.String cardPinNumber;

    private java.lang.String rechargeType;

    private java.lang.String accountType;

    private java.lang.String location;

    private java.lang.String cellID;

    private java.lang.String paymentMode;

    private java.lang.String bankCode;

    private java.lang.String depositType;

    private java.lang.Integer channelID;

    private java.lang.String accountCode;

    private java.lang.String callingNumber;

    public VoucherRechargeRequest() {
    }

    public VoucherRechargeRequest(
           java.lang.String logID,
           java.lang.String subscriberNo,
           java.lang.String cardPinNumber,
           java.lang.String rechargeType,
           java.lang.String accountType,
           java.lang.String location,
           java.lang.String cellID,
           java.lang.String paymentMode,
           java.lang.String bankCode,
           java.lang.String depositType,
           java.lang.Integer channelID,
           java.lang.String accountCode,
           java.lang.String callingNumber) {
           this.logID = logID;
           this.subscriberNo = subscriberNo;
           this.cardPinNumber = cardPinNumber;
           this.rechargeType = rechargeType;
           this.accountType = accountType;
           this.location = location;
           this.cellID = cellID;
           this.paymentMode = paymentMode;
           this.bankCode = bankCode;
           this.depositType = depositType;
           this.channelID = channelID;
           this.accountCode = accountCode;
           this.callingNumber = callingNumber;
    }


    /**
     * Gets the logID value for this VoucherRechargeRequest.
     * 
     * @return logID
     */
    public java.lang.String getLogID() {
        return logID;
    }


    /**
     * Sets the logID value for this VoucherRechargeRequest.
     * 
     * @param logID
     */
    public void setLogID(java.lang.String logID) {
        this.logID = logID;
    }


    /**
     * Gets the subscriberNo value for this VoucherRechargeRequest.
     * 
     * @return subscriberNo
     */
    public java.lang.String getSubscriberNo() {
        return subscriberNo;
    }


    /**
     * Sets the subscriberNo value for this VoucherRechargeRequest.
     * 
     * @param subscriberNo
     */
    public void setSubscriberNo(java.lang.String subscriberNo) {
        this.subscriberNo = subscriberNo;
    }


    /**
     * Gets the cardPinNumber value for this VoucherRechargeRequest.
     * 
     * @return cardPinNumber
     */
    public java.lang.String getCardPinNumber() {
        return cardPinNumber;
    }


    /**
     * Sets the cardPinNumber value for this VoucherRechargeRequest.
     * 
     * @param cardPinNumber
     */
    public void setCardPinNumber(java.lang.String cardPinNumber) {
        this.cardPinNumber = cardPinNumber;
    }


    /**
     * Gets the rechargeType value for this VoucherRechargeRequest.
     * 
     * @return rechargeType
     */
    public java.lang.String getRechargeType() {
        return rechargeType;
    }


    /**
     * Sets the rechargeType value for this VoucherRechargeRequest.
     * 
     * @param rechargeType
     */
    public void setRechargeType(java.lang.String rechargeType) {
        this.rechargeType = rechargeType;
    }


    /**
     * Gets the accountType value for this VoucherRechargeRequest.
     * 
     * @return accountType
     */
    public java.lang.String getAccountType() {
        return accountType;
    }


    /**
     * Sets the accountType value for this VoucherRechargeRequest.
     * 
     * @param accountType
     */
    public void setAccountType(java.lang.String accountType) {
        this.accountType = accountType;
    }


    /**
     * Gets the location value for this VoucherRechargeRequest.
     * 
     * @return location
     */
    public java.lang.String getLocation() {
        return location;
    }


    /**
     * Sets the location value for this VoucherRechargeRequest.
     * 
     * @param location
     */
    public void setLocation(java.lang.String location) {
        this.location = location;
    }


    /**
     * Gets the cellID value for this VoucherRechargeRequest.
     * 
     * @return cellID
     */
    public java.lang.String getCellID() {
        return cellID;
    }


    /**
     * Sets the cellID value for this VoucherRechargeRequest.
     * 
     * @param cellID
     */
    public void setCellID(java.lang.String cellID) {
        this.cellID = cellID;
    }


    /**
     * Gets the paymentMode value for this VoucherRechargeRequest.
     * 
     * @return paymentMode
     */
    public java.lang.String getPaymentMode() {
        return paymentMode;
    }


    /**
     * Sets the paymentMode value for this VoucherRechargeRequest.
     * 
     * @param paymentMode
     */
    public void setPaymentMode(java.lang.String paymentMode) {
        this.paymentMode = paymentMode;
    }


    /**
     * Gets the bankCode value for this VoucherRechargeRequest.
     * 
     * @return bankCode
     */
    public java.lang.String getBankCode() {
        return bankCode;
    }


    /**
     * Sets the bankCode value for this VoucherRechargeRequest.
     * 
     * @param bankCode
     */
    public void setBankCode(java.lang.String bankCode) {
        this.bankCode = bankCode;
    }


    /**
     * Gets the depositType value for this VoucherRechargeRequest.
     * 
     * @return depositType
     */
    public java.lang.String getDepositType() {
        return depositType;
    }


    /**
     * Sets the depositType value for this VoucherRechargeRequest.
     * 
     * @param depositType
     */
    public void setDepositType(java.lang.String depositType) {
        this.depositType = depositType;
    }


    /**
     * Gets the channelID value for this VoucherRechargeRequest.
     * 
     * @return channelID
     */
    public java.lang.Integer getChannelID() {
        return channelID;
    }


    /**
     * Sets the channelID value for this VoucherRechargeRequest.
     * 
     * @param channelID
     */
    public void setChannelID(java.lang.Integer channelID) {
        this.channelID = channelID;
    }


    /**
     * Gets the accountCode value for this VoucherRechargeRequest.
     * 
     * @return accountCode
     */
    public java.lang.String getAccountCode() {
        return accountCode;
    }


    /**
     * Sets the accountCode value for this VoucherRechargeRequest.
     * 
     * @param accountCode
     */
    public void setAccountCode(java.lang.String accountCode) {
        this.accountCode = accountCode;
    }


    /**
     * Gets the callingNumber value for this VoucherRechargeRequest.
     * 
     * @return callingNumber
     */
    public java.lang.String getCallingNumber() {
        return callingNumber;
    }


    /**
     * Sets the callingNumber value for this VoucherRechargeRequest.
     * 
     * @param callingNumber
     */
    public void setCallingNumber(java.lang.String callingNumber) {
        this.callingNumber = callingNumber;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VoucherRechargeRequest)) return false;
        VoucherRechargeRequest other = (VoucherRechargeRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.logID==null && other.getLogID()==null) || 
             (this.logID!=null &&
              this.logID.equals(other.getLogID()))) &&
            ((this.subscriberNo==null && other.getSubscriberNo()==null) || 
             (this.subscriberNo!=null &&
              this.subscriberNo.equals(other.getSubscriberNo()))) &&
            ((this.cardPinNumber==null && other.getCardPinNumber()==null) || 
             (this.cardPinNumber!=null &&
              this.cardPinNumber.equals(other.getCardPinNumber()))) &&
            ((this.rechargeType==null && other.getRechargeType()==null) || 
             (this.rechargeType!=null &&
              this.rechargeType.equals(other.getRechargeType()))) &&
            ((this.accountType==null && other.getAccountType()==null) || 
             (this.accountType!=null &&
              this.accountType.equals(other.getAccountType()))) &&
            ((this.location==null && other.getLocation()==null) || 
             (this.location!=null &&
              this.location.equals(other.getLocation()))) &&
            ((this.cellID==null && other.getCellID()==null) || 
             (this.cellID!=null &&
              this.cellID.equals(other.getCellID()))) &&
            ((this.paymentMode==null && other.getPaymentMode()==null) || 
             (this.paymentMode!=null &&
              this.paymentMode.equals(other.getPaymentMode()))) &&
            ((this.bankCode==null && other.getBankCode()==null) || 
             (this.bankCode!=null &&
              this.bankCode.equals(other.getBankCode()))) &&
            ((this.depositType==null && other.getDepositType()==null) || 
             (this.depositType!=null &&
              this.depositType.equals(other.getDepositType()))) &&
            ((this.channelID==null && other.getChannelID()==null) || 
             (this.channelID!=null &&
              this.channelID.equals(other.getChannelID()))) &&
            ((this.accountCode==null && other.getAccountCode()==null) || 
             (this.accountCode!=null &&
              this.accountCode.equals(other.getAccountCode()))) &&
            ((this.callingNumber==null && other.getCallingNumber()==null) || 
             (this.callingNumber!=null &&
              this.callingNumber.equals(other.getCallingNumber())));
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
        if (getLogID() != null) {
            _hashCode += getLogID().hashCode();
        }
        if (getSubscriberNo() != null) {
            _hashCode += getSubscriberNo().hashCode();
        }
        if (getCardPinNumber() != null) {
            _hashCode += getCardPinNumber().hashCode();
        }
        if (getRechargeType() != null) {
            _hashCode += getRechargeType().hashCode();
        }
        if (getAccountType() != null) {
            _hashCode += getAccountType().hashCode();
        }
        if (getLocation() != null) {
            _hashCode += getLocation().hashCode();
        }
        if (getCellID() != null) {
            _hashCode += getCellID().hashCode();
        }
        if (getPaymentMode() != null) {
            _hashCode += getPaymentMode().hashCode();
        }
        if (getBankCode() != null) {
            _hashCode += getBankCode().hashCode();
        }
        if (getDepositType() != null) {
            _hashCode += getDepositType().hashCode();
        }
        if (getChannelID() != null) {
            _hashCode += getChannelID().hashCode();
        }
        if (getAccountCode() != null) {
            _hashCode += getAccountCode().hashCode();
        }
        if (getCallingNumber() != null) {
            _hashCode += getCallingNumber().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VoucherRechargeRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "VoucherRechargeRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("logID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "LogID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subscriberNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "SubscriberNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardPinNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "CardPinNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rechargeType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "RechargeType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "AccountType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("location");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "Location"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cellID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "CellID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paymentMode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "PaymentMode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "BankCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("depositType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "DepositType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("channelID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "ChannelID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "AccountCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callingNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "CallingNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
