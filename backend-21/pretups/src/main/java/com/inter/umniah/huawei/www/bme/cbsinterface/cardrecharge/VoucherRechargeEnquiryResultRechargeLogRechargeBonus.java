/**
 * VoucherRechargeEnquiryResultRechargeLogRechargeBonus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge;

public class VoucherRechargeEnquiryResultRechargeLogRechargeBonus  implements java.io.Serializable {
    private java.lang.String prmAcctType;

    private java.lang.Long prmAmt;

    private java.lang.Long currPrmAcctBalance;

    private java.math.BigInteger chgExpTime;

    private java.lang.String applyTime;

    private java.lang.String expiryTime;

    private java.math.BigInteger minMeasureId;

    private java.lang.String accountTypeDescription;

    public VoucherRechargeEnquiryResultRechargeLogRechargeBonus() {
    }

    public VoucherRechargeEnquiryResultRechargeLogRechargeBonus(
           java.lang.String prmAcctType,
           java.lang.Long prmAmt,
           java.lang.Long currPrmAcctBalance,
           java.math.BigInteger chgExpTime,
           java.lang.String applyTime,
           java.lang.String expiryTime,
           java.math.BigInteger minMeasureId,
           java.lang.String accountTypeDescription) {
           this.prmAcctType = prmAcctType;
           this.prmAmt = prmAmt;
           this.currPrmAcctBalance = currPrmAcctBalance;
           this.chgExpTime = chgExpTime;
           this.applyTime = applyTime;
           this.expiryTime = expiryTime;
           this.minMeasureId = minMeasureId;
           this.accountTypeDescription = accountTypeDescription;
    }


    /**
     * Gets the prmAcctType value for this VoucherRechargeEnquiryResultRechargeLogRechargeBonus.
     * 
     * @return prmAcctType
     */
    public java.lang.String getPrmAcctType() {
        return prmAcctType;
    }


    /**
     * Sets the prmAcctType value for this VoucherRechargeEnquiryResultRechargeLogRechargeBonus.
     * 
     * @param prmAcctType
     */
    public void setPrmAcctType(java.lang.String prmAcctType) {
        this.prmAcctType = prmAcctType;
    }


    /**
     * Gets the prmAmt value for this VoucherRechargeEnquiryResultRechargeLogRechargeBonus.
     * 
     * @return prmAmt
     */
    public java.lang.Long getPrmAmt() {
        return prmAmt;
    }


    /**
     * Sets the prmAmt value for this VoucherRechargeEnquiryResultRechargeLogRechargeBonus.
     * 
     * @param prmAmt
     */
    public void setPrmAmt(java.lang.Long prmAmt) {
        this.prmAmt = prmAmt;
    }


    /**
     * Gets the currPrmAcctBalance value for this VoucherRechargeEnquiryResultRechargeLogRechargeBonus.
     * 
     * @return currPrmAcctBalance
     */
    public java.lang.Long getCurrPrmAcctBalance() {
        return currPrmAcctBalance;
    }


    /**
     * Sets the currPrmAcctBalance value for this VoucherRechargeEnquiryResultRechargeLogRechargeBonus.
     * 
     * @param currPrmAcctBalance
     */
    public void setCurrPrmAcctBalance(java.lang.Long currPrmAcctBalance) {
        this.currPrmAcctBalance = currPrmAcctBalance;
    }


    /**
     * Gets the chgExpTime value for this VoucherRechargeEnquiryResultRechargeLogRechargeBonus.
     * 
     * @return chgExpTime
     */
    public java.math.BigInteger getChgExpTime() {
        return chgExpTime;
    }


    /**
     * Sets the chgExpTime value for this VoucherRechargeEnquiryResultRechargeLogRechargeBonus.
     * 
     * @param chgExpTime
     */
    public void setChgExpTime(java.math.BigInteger chgExpTime) {
        this.chgExpTime = chgExpTime;
    }


    /**
     * Gets the applyTime value for this VoucherRechargeEnquiryResultRechargeLogRechargeBonus.
     * 
     * @return applyTime
     */
    public java.lang.String getApplyTime() {
        return applyTime;
    }


    /**
     * Sets the applyTime value for this VoucherRechargeEnquiryResultRechargeLogRechargeBonus.
     * 
     * @param applyTime
     */
    public void setApplyTime(java.lang.String applyTime) {
        this.applyTime = applyTime;
    }


    /**
     * Gets the expiryTime value for this VoucherRechargeEnquiryResultRechargeLogRechargeBonus.
     * 
     * @return expiryTime
     */
    public java.lang.String getExpiryTime() {
        return expiryTime;
    }


    /**
     * Sets the expiryTime value for this VoucherRechargeEnquiryResultRechargeLogRechargeBonus.
     * 
     * @param expiryTime
     */
    public void setExpiryTime(java.lang.String expiryTime) {
        this.expiryTime = expiryTime;
    }


    /**
     * Gets the minMeasureId value for this VoucherRechargeEnquiryResultRechargeLogRechargeBonus.
     * 
     * @return minMeasureId
     */
    public java.math.BigInteger getMinMeasureId() {
        return minMeasureId;
    }


    /**
     * Sets the minMeasureId value for this VoucherRechargeEnquiryResultRechargeLogRechargeBonus.
     * 
     * @param minMeasureId
     */
    public void setMinMeasureId(java.math.BigInteger minMeasureId) {
        this.minMeasureId = minMeasureId;
    }


    /**
     * Gets the accountTypeDescription value for this VoucherRechargeEnquiryResultRechargeLogRechargeBonus.
     * 
     * @return accountTypeDescription
     */
    public java.lang.String getAccountTypeDescription() {
        return accountTypeDescription;
    }


    /**
     * Sets the accountTypeDescription value for this VoucherRechargeEnquiryResultRechargeLogRechargeBonus.
     * 
     * @param accountTypeDescription
     */
    public void setAccountTypeDescription(java.lang.String accountTypeDescription) {
        this.accountTypeDescription = accountTypeDescription;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VoucherRechargeEnquiryResultRechargeLogRechargeBonus)) return false;
        VoucherRechargeEnquiryResultRechargeLogRechargeBonus other = (VoucherRechargeEnquiryResultRechargeLogRechargeBonus) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.prmAcctType==null && other.getPrmAcctType()==null) || 
             (this.prmAcctType!=null &&
              this.prmAcctType.equals(other.getPrmAcctType()))) &&
            ((this.prmAmt==null && other.getPrmAmt()==null) || 
             (this.prmAmt!=null &&
              this.prmAmt.equals(other.getPrmAmt()))) &&
            ((this.currPrmAcctBalance==null && other.getCurrPrmAcctBalance()==null) || 
             (this.currPrmAcctBalance!=null &&
              this.currPrmAcctBalance.equals(other.getCurrPrmAcctBalance()))) &&
            ((this.chgExpTime==null && other.getChgExpTime()==null) || 
             (this.chgExpTime!=null &&
              this.chgExpTime.equals(other.getChgExpTime()))) &&
            ((this.applyTime==null && other.getApplyTime()==null) || 
             (this.applyTime!=null &&
              this.applyTime.equals(other.getApplyTime()))) &&
            ((this.expiryTime==null && other.getExpiryTime()==null) || 
             (this.expiryTime!=null &&
              this.expiryTime.equals(other.getExpiryTime()))) &&
            ((this.minMeasureId==null && other.getMinMeasureId()==null) || 
             (this.minMeasureId!=null &&
              this.minMeasureId.equals(other.getMinMeasureId()))) &&
            ((this.accountTypeDescription==null && other.getAccountTypeDescription()==null) || 
             (this.accountTypeDescription!=null &&
              this.accountTypeDescription.equals(other.getAccountTypeDescription())));
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
        if (getPrmAcctType() != null) {
            _hashCode += getPrmAcctType().hashCode();
        }
        if (getPrmAmt() != null) {
            _hashCode += getPrmAmt().hashCode();
        }
        if (getCurrPrmAcctBalance() != null) {
            _hashCode += getCurrPrmAcctBalance().hashCode();
        }
        if (getChgExpTime() != null) {
            _hashCode += getChgExpTime().hashCode();
        }
        if (getApplyTime() != null) {
            _hashCode += getApplyTime().hashCode();
        }
        if (getExpiryTime() != null) {
            _hashCode += getExpiryTime().hashCode();
        }
        if (getMinMeasureId() != null) {
            _hashCode += getMinMeasureId().hashCode();
        }
        if (getAccountTypeDescription() != null) {
            _hashCode += getAccountTypeDescription().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VoucherRechargeEnquiryResultRechargeLogRechargeBonus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">>VoucherRechargeEnquiryResult>RechargeLog>RechargeBonus"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("prmAcctType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "PrmAcctType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("prmAmt");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "PrmAmt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currPrmAcctBalance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "CurrPrmAcctBalance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chgExpTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "ChgExpTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("applyTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "ApplyTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expiryTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "ExpiryTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("minMeasureId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "MinMeasureId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountTypeDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "AccountTypeDescription"));
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
