/**
 * VoucherRechargeBySeqResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge;

public class VoucherRechargeBySeqResult  implements java.io.Serializable {
    private java.lang.Long faceValue;

    private java.lang.Long newBalance;

    private java.lang.String newActiveStop;

    private java.lang.Integer validityPeriod;

    private com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeBySeqResultRechargeBonus[] rechargeBonus;

    public VoucherRechargeBySeqResult() {
    }

    public VoucherRechargeBySeqResult(
           java.lang.Long faceValue,
           java.lang.Long newBalance,
           java.lang.String newActiveStop,
           java.lang.Integer validityPeriod,
           com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeBySeqResultRechargeBonus[] rechargeBonus) {
           this.faceValue = faceValue;
           this.newBalance = newBalance;
           this.newActiveStop = newActiveStop;
           this.validityPeriod = validityPeriod;
           this.rechargeBonus = rechargeBonus;
    }


    /**
     * Gets the faceValue value for this VoucherRechargeBySeqResult.
     * 
     * @return faceValue
     */
    public java.lang.Long getFaceValue() {
        return faceValue;
    }


    /**
     * Sets the faceValue value for this VoucherRechargeBySeqResult.
     * 
     * @param faceValue
     */
    public void setFaceValue(java.lang.Long faceValue) {
        this.faceValue = faceValue;
    }


    /**
     * Gets the newBalance value for this VoucherRechargeBySeqResult.
     * 
     * @return newBalance
     */
    public java.lang.Long getNewBalance() {
        return newBalance;
    }


    /**
     * Sets the newBalance value for this VoucherRechargeBySeqResult.
     * 
     * @param newBalance
     */
    public void setNewBalance(java.lang.Long newBalance) {
        this.newBalance = newBalance;
    }


    /**
     * Gets the newActiveStop value for this VoucherRechargeBySeqResult.
     * 
     * @return newActiveStop
     */
    public java.lang.String getNewActiveStop() {
        return newActiveStop;
    }


    /**
     * Sets the newActiveStop value for this VoucherRechargeBySeqResult.
     * 
     * @param newActiveStop
     */
    public void setNewActiveStop(java.lang.String newActiveStop) {
        this.newActiveStop = newActiveStop;
    }


    /**
     * Gets the validityPeriod value for this VoucherRechargeBySeqResult.
     * 
     * @return validityPeriod
     */
    public java.lang.Integer getValidityPeriod() {
        return validityPeriod;
    }


    /**
     * Sets the validityPeriod value for this VoucherRechargeBySeqResult.
     * 
     * @param validityPeriod
     */
    public void setValidityPeriod(java.lang.Integer validityPeriod) {
        this.validityPeriod = validityPeriod;
    }


    /**
     * Gets the rechargeBonus value for this VoucherRechargeBySeqResult.
     * 
     * @return rechargeBonus
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeBySeqResultRechargeBonus[] getRechargeBonus() {
        return rechargeBonus;
    }


    /**
     * Sets the rechargeBonus value for this VoucherRechargeBySeqResult.
     * 
     * @param rechargeBonus
     */
    public void setRechargeBonus(com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeBySeqResultRechargeBonus[] rechargeBonus) {
        this.rechargeBonus = rechargeBonus;
    }

    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeBySeqResultRechargeBonus getRechargeBonus(int i) {
        return this.rechargeBonus[i];
    }

    public void setRechargeBonus(int i, com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeBySeqResultRechargeBonus _value) {
        this.rechargeBonus[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VoucherRechargeBySeqResult)) return false;
        VoucherRechargeBySeqResult other = (VoucherRechargeBySeqResult) obj;
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
            ((this.rechargeBonus==null && other.getRechargeBonus()==null) || 
             (this.rechargeBonus!=null &&
              java.util.Arrays.equals(this.rechargeBonus, other.getRechargeBonus())));
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
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VoucherRechargeBySeqResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "VoucherRechargeBySeqResult"));
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
        elemField.setFieldName("rechargeBonus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "RechargeBonus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherRechargeBySeqResult>RechargeBonus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
