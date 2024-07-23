/**
 * VoucherRechargeResultMsg.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr;

public class VoucherRechargeResultMsg  implements java.io.Serializable {
    private com.inter.umniah.huawei.www.bme.cbsinterface.common.ResultHeader resultHeader;

    private com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeResult voucherRechargeResult;

    public VoucherRechargeResultMsg() {
    }

    public VoucherRechargeResultMsg(
           com.inter.umniah.huawei.www.bme.cbsinterface.common.ResultHeader resultHeader,
           com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeResult voucherRechargeResult) {
           this.resultHeader = resultHeader;
           this.voucherRechargeResult = voucherRechargeResult;
    }


    /**
     * Gets the resultHeader value for this VoucherRechargeResultMsg.
     * 
     * @return resultHeader
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.common.ResultHeader getResultHeader() {
        return resultHeader;
    }


    /**
     * Sets the resultHeader value for this VoucherRechargeResultMsg.
     * 
     * @param resultHeader
     */
    public void setResultHeader(com.inter.umniah.huawei.www.bme.cbsinterface.common.ResultHeader resultHeader) {
        this.resultHeader = resultHeader;
    }


    /**
     * Gets the voucherRechargeResult value for this VoucherRechargeResultMsg.
     * 
     * @return voucherRechargeResult
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeResult getVoucherRechargeResult() {
        return voucherRechargeResult;
    }


    /**
     * Sets the voucherRechargeResult value for this VoucherRechargeResultMsg.
     * 
     * @param voucherRechargeResult
     */
    public void setVoucherRechargeResult(com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherRechargeResult voucherRechargeResult) {
        this.voucherRechargeResult = voucherRechargeResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VoucherRechargeResultMsg)) return false;
        VoucherRechargeResultMsg other = (VoucherRechargeResultMsg) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.resultHeader==null && other.getResultHeader()==null) || 
             (this.resultHeader!=null &&
              this.resultHeader.equals(other.getResultHeader()))) &&
            ((this.voucherRechargeResult==null && other.getVoucherRechargeResult()==null) || 
             (this.voucherRechargeResult!=null &&
              this.voucherRechargeResult.equals(other.getVoucherRechargeResult())));
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
        if (getResultHeader() != null) {
            _hashCode += getResultHeader().hashCode();
        }
        if (getVoucherRechargeResult() != null) {
            _hashCode += getVoucherRechargeResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VoucherRechargeResultMsg.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherRechargeResultMsg"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultHeader");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ResultHeader"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "ResultHeader"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("voucherRechargeResult");
        elemField.setXmlName(new javax.xml.namespace.QName("", "VoucherRechargeResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "VoucherRechargeResult"));
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
