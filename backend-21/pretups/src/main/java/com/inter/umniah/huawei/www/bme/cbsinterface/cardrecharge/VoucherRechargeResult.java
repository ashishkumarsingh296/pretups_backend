/**
 * VoucherRechargeResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge;

public class VoucherRechargeResult  implements java.io.Serializable {
    private java.lang.Long faceValue;

    private java.lang.Long newBalance;

    private java.lang.String newActiveStop;

    private java.lang.Integer validityPeriod;

    private java.lang.Long balanceAfterRecharge;

    private java.lang.String newSuspendStopDate;

    private java.lang.String newDisableStopDate;

    private com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeResultRechargeBonus[] rechargeBonus;

    private java.lang.Integer extraValidity;

    private java.lang.Long loanAmount;

    private java.lang.Long loanPoundage;

    private java.lang.Long loanRecovered;

    private java.lang.String balanceValidity;

    private java.lang.Integer balanceValidityPeriod;

    private java.lang.String balanceActiveDate;

    private java.lang.Integer userDays;

    private com.inter.umniah.huawei.www.bme.cbsinterface.subscribe.OfferOrderInfo[] offerList;

    public VoucherRechargeResult() {
    }

    public VoucherRechargeResult(
           java.lang.Long faceValue,
           java.lang.Long newBalance,
           java.lang.String newActiveStop,
           java.lang.Integer validityPeriod,
           java.lang.Long balanceAfterRecharge,
           java.lang.String newSuspendStopDate,
           java.lang.String newDisableStopDate,
           com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeResultRechargeBonus[] rechargeBonus,
           java.lang.Integer extraValidity,
           java.lang.Long loanAmount,
           java.lang.Long loanPoundage,
           java.lang.Long loanRecovered,
           java.lang.String balanceValidity,
           java.lang.Integer balanceValidityPeriod,
           java.lang.String balanceActiveDate,
           java.lang.Integer userDays,
           com.inter.umniah.huawei.www.bme.cbsinterface.subscribe.OfferOrderInfo[] offerList) {
           this.faceValue = faceValue;
           this.newBalance = newBalance;
           this.newActiveStop = newActiveStop;
           this.validityPeriod = validityPeriod;
           this.balanceAfterRecharge = balanceAfterRecharge;
           this.newSuspendStopDate = newSuspendStopDate;
           this.newDisableStopDate = newDisableStopDate;
           this.rechargeBonus = rechargeBonus;
           this.extraValidity = extraValidity;
           this.loanAmount = loanAmount;
           this.loanPoundage = loanPoundage;
           this.loanRecovered = loanRecovered;
           this.balanceValidity = balanceValidity;
           this.balanceValidityPeriod = balanceValidityPeriod;
           this.balanceActiveDate = balanceActiveDate;
           this.userDays = userDays;
           this.offerList = offerList;
    }


    /**
     * Gets the faceValue value for this VoucherRechargeResult.
     * 
     * @return faceValue
     */
    public java.lang.Long getFaceValue() {
        return faceValue;
    }


    /**
     * Sets the faceValue value for this VoucherRechargeResult.
     * 
     * @param faceValue
     */
    public void setFaceValue(java.lang.Long faceValue) {
        this.faceValue = faceValue;
    }


    /**
     * Gets the newBalance value for this VoucherRechargeResult.
     * 
     * @return newBalance
     */
    public java.lang.Long getNewBalance() {
        return newBalance;
    }


    /**
     * Sets the newBalance value for this VoucherRechargeResult.
     * 
     * @param newBalance
     */
    public void setNewBalance(java.lang.Long newBalance) {
        this.newBalance = newBalance;
    }


    /**
     * Gets the newActiveStop value for this VoucherRechargeResult.
     * 
     * @return newActiveStop
     */
    public java.lang.String getNewActiveStop() {
        return newActiveStop;
    }


    /**
     * Sets the newActiveStop value for this VoucherRechargeResult.
     * 
     * @param newActiveStop
     */
    public void setNewActiveStop(java.lang.String newActiveStop) {
        this.newActiveStop = newActiveStop;
    }


    /**
     * Gets the validityPeriod value for this VoucherRechargeResult.
     * 
     * @return validityPeriod
     */
    public java.lang.Integer getValidityPeriod() {
        return validityPeriod;
    }


    /**
     * Sets the validityPeriod value for this VoucherRechargeResult.
     * 
     * @param validityPeriod
     */
    public void setValidityPeriod(java.lang.Integer validityPeriod) {
        this.validityPeriod = validityPeriod;
    }


    /**
     * Gets the balanceAfterRecharge value for this VoucherRechargeResult.
     * 
     * @return balanceAfterRecharge
     */
    public java.lang.Long getBalanceAfterRecharge() {
        return balanceAfterRecharge;
    }


    /**
     * Sets the balanceAfterRecharge value for this VoucherRechargeResult.
     * 
     * @param balanceAfterRecharge
     */
    public void setBalanceAfterRecharge(java.lang.Long balanceAfterRecharge) {
        this.balanceAfterRecharge = balanceAfterRecharge;
    }


    /**
     * Gets the newSuspendStopDate value for this VoucherRechargeResult.
     * 
     * @return newSuspendStopDate
     */
    public java.lang.String getNewSuspendStopDate() {
        return newSuspendStopDate;
    }


    /**
     * Sets the newSuspendStopDate value for this VoucherRechargeResult.
     * 
     * @param newSuspendStopDate
     */
    public void setNewSuspendStopDate(java.lang.String newSuspendStopDate) {
        this.newSuspendStopDate = newSuspendStopDate;
    }


    /**
     * Gets the newDisableStopDate value for this VoucherRechargeResult.
     * 
     * @return newDisableStopDate
     */
    public java.lang.String getNewDisableStopDate() {
        return newDisableStopDate;
    }


    /**
     * Sets the newDisableStopDate value for this VoucherRechargeResult.
     * 
     * @param newDisableStopDate
     */
    public void setNewDisableStopDate(java.lang.String newDisableStopDate) {
        this.newDisableStopDate = newDisableStopDate;
    }


    /**
     * Gets the rechargeBonus value for this VoucherRechargeResult.
     * 
     * @return rechargeBonus
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeResultRechargeBonus[] getRechargeBonus() {
        return rechargeBonus;
    }


    /**
     * Sets the rechargeBonus value for this VoucherRechargeResult.
     * 
     * @param rechargeBonus
     */
    public void setRechargeBonus(com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeResultRechargeBonus[] rechargeBonus) {
        this.rechargeBonus = rechargeBonus;
    }

    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeResultRechargeBonus getRechargeBonus(int i) {
        return this.rechargeBonus[i];
    }

    public void setRechargeBonus(int i, com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeResultRechargeBonus _value) {
        this.rechargeBonus[i] = _value;
    }


    /**
     * Gets the extraValidity value for this VoucherRechargeResult.
     * 
     * @return extraValidity
     */
    public java.lang.Integer getExtraValidity() {
        return extraValidity;
    }


    /**
     * Sets the extraValidity value for this VoucherRechargeResult.
     * 
     * @param extraValidity
     */
    public void setExtraValidity(java.lang.Integer extraValidity) {
        this.extraValidity = extraValidity;
    }


    /**
     * Gets the loanAmount value for this VoucherRechargeResult.
     * 
     * @return loanAmount
     */
    public java.lang.Long getLoanAmount() {
        return loanAmount;
    }


    /**
     * Sets the loanAmount value for this VoucherRechargeResult.
     * 
     * @param loanAmount
     */
    public void setLoanAmount(java.lang.Long loanAmount) {
        this.loanAmount = loanAmount;
    }


    /**
     * Gets the loanPoundage value for this VoucherRechargeResult.
     * 
     * @return loanPoundage
     */
    public java.lang.Long getLoanPoundage() {
        return loanPoundage;
    }


    /**
     * Sets the loanPoundage value for this VoucherRechargeResult.
     * 
     * @param loanPoundage
     */
    public void setLoanPoundage(java.lang.Long loanPoundage) {
        this.loanPoundage = loanPoundage;
    }


    /**
     * Gets the loanRecovered value for this VoucherRechargeResult.
     * 
     * @return loanRecovered
     */
    public java.lang.Long getLoanRecovered() {
        return loanRecovered;
    }


    /**
     * Sets the loanRecovered value for this VoucherRechargeResult.
     * 
     * @param loanRecovered
     */
    public void setLoanRecovered(java.lang.Long loanRecovered) {
        this.loanRecovered = loanRecovered;
    }


    /**
     * Gets the balanceValidity value for this VoucherRechargeResult.
     * 
     * @return balanceValidity
     */
    public java.lang.String getBalanceValidity() {
        return balanceValidity;
    }


    /**
     * Sets the balanceValidity value for this VoucherRechargeResult.
     * 
     * @param balanceValidity
     */
    public void setBalanceValidity(java.lang.String balanceValidity) {
        this.balanceValidity = balanceValidity;
    }


    /**
     * Gets the balanceValidityPeriod value for this VoucherRechargeResult.
     * 
     * @return balanceValidityPeriod
     */
    public java.lang.Integer getBalanceValidityPeriod() {
        return balanceValidityPeriod;
    }


    /**
     * Sets the balanceValidityPeriod value for this VoucherRechargeResult.
     * 
     * @param balanceValidityPeriod
     */
    public void setBalanceValidityPeriod(java.lang.Integer balanceValidityPeriod) {
        this.balanceValidityPeriod = balanceValidityPeriod;
    }


    /**
     * Gets the balanceActiveDate value for this VoucherRechargeResult.
     * 
     * @return balanceActiveDate
     */
    public java.lang.String getBalanceActiveDate() {
        return balanceActiveDate;
    }


    /**
     * Sets the balanceActiveDate value for this VoucherRechargeResult.
     * 
     * @param balanceActiveDate
     */
    public void setBalanceActiveDate(java.lang.String balanceActiveDate) {
        this.balanceActiveDate = balanceActiveDate;
    }


    /**
     * Gets the userDays value for this VoucherRechargeResult.
     * 
     * @return userDays
     */
    public java.lang.Integer getUserDays() {
        return userDays;
    }


    /**
     * Sets the userDays value for this VoucherRechargeResult.
     * 
     * @param userDays
     */
    public void setUserDays(java.lang.Integer userDays) {
        this.userDays = userDays;
    }


    /**
     * Gets the offerList value for this VoucherRechargeResult.
     * 
     * @return offerList
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.subscribe.OfferOrderInfo[] getOfferList() {
        return offerList;
    }


    /**
     * Sets the offerList value for this VoucherRechargeResult.
     * 
     * @param offerList
     */
    public void setOfferList(com.inter.umniah.huawei.www.bme.cbsinterface.subscribe.OfferOrderInfo[] offerList) {
        this.offerList = offerList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VoucherRechargeResult)) return false;
        VoucherRechargeResult other = (VoucherRechargeResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.faceValue==null && other.getFaceValue()==null) || 
             (this.faceValue!=null &&
              this.faceValue.equals(other.getFaceValue()))) &&
            ((this.newBalance==null && other.getNewBalance()==null) || 
             (this.newBalance!=null &&
              this.newBalance.equals(other.getNewBalance()))) &&
            ((this.newActiveStop==null && other.getNewActiveStop()==null) || 
             (this.newActiveStop!=null &&
              this.newActiveStop.equals(other.getNewActiveStop()))) &&
            ((this.validityPeriod==null && other.getValidityPeriod()==null) || 
             (this.validityPeriod!=null &&
              this.validityPeriod.equals(other.getValidityPeriod()))) &&
            ((this.balanceAfterRecharge==null && other.getBalanceAfterRecharge()==null) || 
             (this.balanceAfterRecharge!=null &&
              this.balanceAfterRecharge.equals(other.getBalanceAfterRecharge()))) &&
            ((this.newSuspendStopDate==null && other.getNewSuspendStopDate()==null) || 
             (this.newSuspendStopDate!=null &&
              this.newSuspendStopDate.equals(other.getNewSuspendStopDate()))) &&
            ((this.newDisableStopDate==null && other.getNewDisableStopDate()==null) || 
             (this.newDisableStopDate!=null &&
              this.newDisableStopDate.equals(other.getNewDisableStopDate()))) &&
            ((this.rechargeBonus==null && other.getRechargeBonus()==null) || 
             (this.rechargeBonus!=null &&
              java.util.Arrays.equals(this.rechargeBonus, other.getRechargeBonus()))) &&
            ((this.extraValidity==null && other.getExtraValidity()==null) || 
             (this.extraValidity!=null &&
              this.extraValidity.equals(other.getExtraValidity()))) &&
            ((this.loanAmount==null && other.getLoanAmount()==null) || 
             (this.loanAmount!=null &&
              this.loanAmount.equals(other.getLoanAmount()))) &&
            ((this.loanPoundage==null && other.getLoanPoundage()==null) || 
             (this.loanPoundage!=null &&
              this.loanPoundage.equals(other.getLoanPoundage()))) &&
            ((this.loanRecovered==null && other.getLoanRecovered()==null) || 
             (this.loanRecovered!=null &&
              this.loanRecovered.equals(other.getLoanRecovered()))) &&
            ((this.balanceValidity==null && other.getBalanceValidity()==null) || 
             (this.balanceValidity!=null &&
              this.balanceValidity.equals(other.getBalanceValidity()))) &&
            ((this.balanceValidityPeriod==null && other.getBalanceValidityPeriod()==null) || 
             (this.balanceValidityPeriod!=null &&
              this.balanceValidityPeriod.equals(other.getBalanceValidityPeriod()))) &&
            ((this.balanceActiveDate==null && other.getBalanceActiveDate()==null) || 
             (this.balanceActiveDate!=null &&
              this.balanceActiveDate.equals(other.getBalanceActiveDate()))) &&
            ((this.userDays==null && other.getUserDays()==null) || 
             (this.userDays!=null &&
              this.userDays.equals(other.getUserDays()))) &&
            ((this.offerList==null && other.getOfferList()==null) || 
             (this.offerList!=null &&
              java.util.Arrays.equals(this.offerList, other.getOfferList())));
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
        if (getFaceValue() != null) {
            _hashCode += getFaceValue().hashCode();
        }
        if (getNewBalance() != null) {
            _hashCode += getNewBalance().hashCode();
        }
        if (getNewActiveStop() != null) {
            _hashCode += getNewActiveStop().hashCode();
        }
        if (getValidityPeriod() != null) {
            _hashCode += getValidityPeriod().hashCode();
        }
        if (getBalanceAfterRecharge() != null) {
            _hashCode += getBalanceAfterRecharge().hashCode();
        }
        if (getNewSuspendStopDate() != null) {
            _hashCode += getNewSuspendStopDate().hashCode();
        }
        if (getNewDisableStopDate() != null) {
            _hashCode += getNewDisableStopDate().hashCode();
        }
        if (getRechargeBonus() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRechargeBonus());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRechargeBonus(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getExtraValidity() != null) {
            _hashCode += getExtraValidity().hashCode();
        }
        if (getLoanAmount() != null) {
            _hashCode += getLoanAmount().hashCode();
        }
        if (getLoanPoundage() != null) {
            _hashCode += getLoanPoundage().hashCode();
        }
        if (getLoanRecovered() != null) {
            _hashCode += getLoanRecovered().hashCode();
        }
        if (getBalanceValidity() != null) {
            _hashCode += getBalanceValidity().hashCode();
        }
        if (getBalanceValidityPeriod() != null) {
            _hashCode += getBalanceValidityPeriod().hashCode();
        }
        if (getBalanceActiveDate() != null) {
            _hashCode += getBalanceActiveDate().hashCode();
        }
        if (getUserDays() != null) {
            _hashCode += getUserDays().hashCode();
        }
        if (getOfferList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getOfferList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getOfferList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VoucherRechargeResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "VoucherRechargeResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("faceValue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "FaceValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("newBalance");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "NewBalance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("newActiveStop");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "NewActiveStop"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("validityPeriod");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "ValidityPeriod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("balanceAfterRecharge");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "BalanceAfterRecharge"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("newSuspendStopDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "NewSuspendStopDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("newDisableStopDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "NewDisableStopDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rechargeBonus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "RechargeBonus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherRechargeResult>RechargeBonus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extraValidity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "ExtraValidity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loanAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "LoanAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loanPoundage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "LoanPoundage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loanRecovered");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "LoanRecovered"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("balanceValidity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "BalanceValidity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("balanceValidityPeriod");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "BalanceValidityPeriod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("balanceActiveDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "BalanceActiveDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userDays");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "UserDays"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("offerList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "OfferList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/subscribe", "OfferOrderInfo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "Offer"));
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
