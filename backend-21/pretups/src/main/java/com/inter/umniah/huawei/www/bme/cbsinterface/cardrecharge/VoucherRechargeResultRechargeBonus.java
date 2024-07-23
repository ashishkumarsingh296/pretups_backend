/**
 * VoucherRechargeResultRechargeBonus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge;

public class VoucherRechargeResultRechargeBonus  implements java.io.Serializable {
    private java.lang.String prmAcctType;

    private java.lang.Long prmAmt;

    private java.lang.Long currAcctBal;

    private java.lang.String currExpTime;

    private java.math.BigInteger chgExpTime;

    private java.lang.String accountTypeDescription;

    private java.lang.String applyTime;

    private java.lang.Long balanceId;

    private java.math.BigInteger minMeasureId;

    public VoucherRechargeResultRechargeBonus() {
    }

    public VoucherRechargeResultRechargeBonus(
           java.lang.String prmAcctType,
           java.lang.Long prmAmt,
           java.lang.Long currAcctBal,
           java.lang.String currExpTime,
           java.math.BigInteger chgExpTime,
           java.lang.String accountTypeDescription,
           java.lang.String applyTime,
           java.lang.Long balanceId,
           java.math.BigInteger minMeasureId) {
           this.prmAcctType = prmAcctType;
           this.prmAmt = prmAmt;
           this.currAcctBal = currAcctBal;
           this.currExpTime = currExpTime;
           this.chgExpTime = chgExpTime;
           this.accountTypeDescription = accountTypeDescription;
           this.applyTime = applyTime;
           this.balanceId = balanceId;
           this.minMeasureId = minMeasureId;
    }


    /**
     * Gets the prmAcctType value for this VoucherRechargeResultRechargeBonus.
     * 
     * @return prmAcctType
     */
    public java.lang.String getPrmAcctType() {
        return prmAcctType;
    }


    /**
     * Sets the prmAcctType value for this VoucherRechargeResultRechargeBonus.
     * 
     * @param prmAcctType
     */
    public void setPrmAcctType(java.lang.String prmAcctType) {
        this.prmAcctType = prmAcctType;
    }


    /**
     * Gets the prmAmt value for this VoucherRechargeResultRechargeBonus.
     * 
     * @return prmAmt
     */
    public java.lang.Long getPrmAmt() {
        return prmAmt;
    }


    /**
     * Sets the prmAmt value for this VoucherRechargeResultRechargeBonus.
     * 
     * @param prmAmt
     */
    public void setPrmAmt(java.lang.Long prmAmt) {
        this.prmAmt = prmAmt;
    }


    /**
     * Gets the currAcctBal value for this VoucherRechargeResultRechargeBonus.
     * 
     * @return currAcctBal
     */
    public java.lang.Long getCurrAcctBal() {
        return currAcctBal;
    }


    /**
     * Sets the currAcctBal value for this VoucherRechargeResultRechargeBonus.
     * 
     * @param currAcctBal
     */
    public void setCurrAcctBal(java.lang.Long currAcctBal) {
        this.currAcctBal = currAcctBal;
    }


    /**
     * Gets the currExpTime value for this VoucherRechargeResultRechargeBonus.
     * 
     * @return currExpTime
     */
    public java.lang.String getCurrExpTime() {
        return currExpTime;
    }


    /**
     * Sets the currExpTime value for this VoucherRechargeResultRechargeBonus.
     * 
     * @param currExpTime
     */
    public void setCurrExpTime(java.lang.String currExpTime) {
        this.currExpTime = currExpTime;
    }


    /**
     * Gets the chgExpTime value for this VoucherRechargeResultRechargeBonus.
     * 
     * @return chgExpTime
     */
    public java.math.BigInteger getChgExpTime() {
        return chgExpTime;
    }


    /**
     * Sets the chgExpTime value for this VoucherRechargeResultRechargeBonus.
     * 
     * @param chgExpTime
     */
    public void setChgExpTime(java.math.BigInteger chgExpTime) {
        this.chgExpTime = chgExpTime;
    }


    /**
     * Gets the accountTypeDescription value for this VoucherRechargeResultRechargeBonus.
     * 
     * @return accountTypeDescription
     */
    public java.lang.String getAccountTypeDescription() {
        return accountTypeDescription;
    }


    /**
     * Sets the accountTypeDescription value for this VoucherRechargeResultRechargeBonus.
     * 
     * @param accountTypeDescription
     */
    public void setAccountTypeDescription(java.lang.String accountTypeDescription) {
        this.accountTypeDescription = accountTypeDescription;
    }


    /**
     * Gets the applyTime value for this VoucherRechargeResultRechargeBonus.
     * 
     * @return applyTime
     */
    public java.lang.String getApplyTime() {
        return applyTime;
    }


    /**
     * Sets the applyTime value for this VoucherRechargeResultRechargeBonus.
     * 
     * @param applyTime
     */
    public void setApplyTime(java.lang.String applyTime) {
        this.applyTime = applyTime;
    }


    /**
     * Gets the balanceId value for this VoucherRechargeResultRechargeBonus.
     * 
     * @return balanceId
     */
    public java.lang.Long getBalanceId() {
        return balanceId;
    }


    /**
     * Sets the balanceId value for this VoucherRechargeResultRechargeBonus.
     * 
     * @param balanceId
     */
    public void setBalanceId(java.lang.Long balanceId) {
        this.balanceId = balanceId;
    }


    /**
     * Gets the minMeasureId value for this VoucherRechargeResultRechargeBonus.
     * 
     * @return minMeasureId
     */
    public java.math.BigInteger getMinMeasureId() {
        return minMeasureId;
    }


    /**
     * Sets the minMeasureId value for this VoucherRechargeResultRechargeBonus.
     * 
     * @param minMeasureId
     */
    public void setMinMeasureId(java.math.BigInteger minMeasureId) {
        this.minMeasureId = minMeasureId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VoucherRechargeResultRechargeBonus)) return false;
        VoucherRechargeResultRechargeBonus other = (VoucherRechargeResultRechargeBonus) obj;
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
            ((this.currAcctBal==null && other.getCurrAcctBal()==null) || 
             (this.currAcctBal!=null &&
              this.currAcctBal.equals(other.getCurrAcctBal()))) &&
            ((this.currExpTime==null && other.getCurrExpTime()==null) || 
             (this.currExpTime!=null &&
              this.currExpTime.equals(other.getCurrExpTime()))) &&
            ((this.chgExpTime==null && other.getChgExpTime()==null) || 
             (this.chgExpTime!=null &&
              this.chgExpTime.equals(other.getChgExpTime()))) &&
            ((this.accountTypeDescription==null && other.getAccountTypeDescription()==null) || 
             (this.accountTypeDescription!=null &&
              this.accountTypeDescription.equals(other.getAccountTypeDescription()))) &&
            ((this.applyTime==null && other.getApplyTime()==null) || 
             (this.applyTime!=null &&
              this.applyTime.equals(other.getApplyTime()))) &&
            ((this.balanceId==null && other.getBalanceId()==null) || 
             (this.balanceId!=null &&
              this.balanceId.equals(other.getBalanceId()))) &&
            ((this.minMeasureId==null && other.getMinMeasureId()==null) || 
             (this.minMeasureId!=null &&
              this.minMeasureId.equals(other.getMinMeasureId())));
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
        if (getCurrAcctBal() != null) {
            _hashCode += getCurrAcctBal().hashCode();
        }
        if (getCurrExpTime() != null) {
            _hashCode += getCurrExpTime().hashCode();
        }
        if (getChgExpTime() != null) {
            _hashCode += getChgExpTime().hashCode();
        }
        if (getAccountTypeDescription() != null) {
            _hashCode += getAccountTypeDescription().hashCode();
        }
        if (getApplyTime() != null) {
            _hashCode += getApplyTime().hashCode();
        }
        if (getBalanceId() != null) {
            _hashCode += getBalanceId().hashCode();
        }
        if (getMinMeasureId() != null) {
            _hashCode += getMinMeasureId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VoucherRechargeResultRechargeBonus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherRechargeResult>RechargeBonus"));
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
        elemField.setFieldName("currAcctBal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "CurrAcctBal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currExpTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "CurrExpTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chgExpTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "ChgExpTime"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("applyTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "ApplyTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("balanceId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "BalanceId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("minMeasureId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "MinMeasureId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
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
