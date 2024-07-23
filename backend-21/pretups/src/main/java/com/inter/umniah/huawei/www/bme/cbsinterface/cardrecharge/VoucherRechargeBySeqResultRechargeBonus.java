/**
 * VoucherRechargeBySeqResultRechargeBonus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge;

public class VoucherRechargeBySeqResultRechargeBonus  implements java.io.Serializable {
    private java.lang.String prmAcctType;

    private java.lang.Long prmAmt;

    public VoucherRechargeBySeqResultRechargeBonus() {
    }

    public VoucherRechargeBySeqResultRechargeBonus(
           java.lang.String prmAcctType,
           java.lang.Long prmAmt) {
           this.prmAcctType = prmAcctType;
           this.prmAmt = prmAmt;
    }


    /**
     * Gets the prmAcctType value for this VoucherRechargeBySeqResultRechargeBonus.
     * 
     * @return prmAcctType
     */
    public java.lang.String getPrmAcctType() {
        return prmAcctType;
    }


    /**
     * Sets the prmAcctType value for this VoucherRechargeBySeqResultRechargeBonus.
     * 
     * @param prmAcctType
     */
    public void setPrmAcctType(java.lang.String prmAcctType) {
        this.prmAcctType = prmAcctType;
    }


    /**
     * Gets the prmAmt value for this VoucherRechargeBySeqResultRechargeBonus.
     * 
     * @return prmAmt
     */
    public java.lang.Long getPrmAmt() {
        return prmAmt;
    }


    /**
     * Sets the prmAmt value for this VoucherRechargeBySeqResultRechargeBonus.
     * 
     * @param prmAmt
     */
    public void setPrmAmt(java.lang.Long prmAmt) {
        this.prmAmt = prmAmt;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VoucherRechargeBySeqResultRechargeBonus)) return false;
        VoucherRechargeBySeqResultRechargeBonus other = (VoucherRechargeBySeqResultRechargeBonus) obj;
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
              this.prmAmt.equals(other.getPrmAmt())));
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
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VoucherRechargeBySeqResultRechargeBonus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", ">VoucherRechargeBySeqResult>RechargeBonus"));
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
