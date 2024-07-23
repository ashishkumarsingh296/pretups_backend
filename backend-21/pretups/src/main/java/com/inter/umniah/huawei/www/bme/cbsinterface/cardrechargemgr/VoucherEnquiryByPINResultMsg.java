/**
 * VoucherEnquiryByPINResultMsg.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr;

public class VoucherEnquiryByPINResultMsg  implements java.io.Serializable {
    private com.inter.umniah.huawei.www.bme.cbsinterface.common.ResultHeader resultHeader;

    private com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherEnquiryByPINResult voucherEnquiryByPINResult;

    public VoucherEnquiryByPINResultMsg() {
    }

    public VoucherEnquiryByPINResultMsg(
           com.inter.umniah.huawei.www.bme.cbsinterface.common.ResultHeader resultHeader,
           com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherEnquiryByPINResult voucherEnquiryByPINResult) {
           this.resultHeader = resultHeader;
           this.voucherEnquiryByPINResult = voucherEnquiryByPINResult;
    }


    /**
     * Gets the resultHeader value for this VoucherEnquiryByPINResultMsg.
     * 
     * @return resultHeader
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.common.ResultHeader getResultHeader() {
        return resultHeader;
    }


    /**
     * Sets the resultHeader value for this VoucherEnquiryByPINResultMsg.
     * 
     * @param resultHeader
     */
    public void setResultHeader(com.inter.umniah.huawei.www.bme.cbsinterface.common.ResultHeader resultHeader) {
        this.resultHeader = resultHeader;
    }


    /**
     * Gets the voucherEnquiryByPINResult value for this VoucherEnquiryByPINResultMsg.
     * 
     * @return voucherEnquiryByPINResult
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherEnquiryByPINResult getVoucherEnquiryByPINResult() {
        return voucherEnquiryByPINResult;
    }


    /**
     * Sets the voucherEnquiryByPINResult value for this VoucherEnquiryByPINResultMsg.
     * 
     * @param voucherEnquiryByPINResult
     */
    public void setVoucherEnquiryByPINResult(com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherEnquiryByPINResult voucherEnquiryByPINResult) {
        this.voucherEnquiryByPINResult = voucherEnquiryByPINResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VoucherEnquiryByPINResultMsg)) return false;
        VoucherEnquiryByPINResultMsg other = (VoucherEnquiryByPINResultMsg) obj;
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
            ((this.voucherEnquiryByPINResult==null && other.getVoucherEnquiryByPINResult()==null) || 
             (this.voucherEnquiryByPINResult!=null &&
              this.voucherEnquiryByPINResult.equals(other.getVoucherEnquiryByPINResult())));
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
        if (getVoucherEnquiryByPINResult() != null) {
            _hashCode += getVoucherEnquiryByPINResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VoucherEnquiryByPINResultMsg.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherEnquiryByPINResultMsg"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultHeader");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ResultHeader"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "ResultHeader"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("voucherEnquiryByPINResult");
        elemField.setXmlName(new javax.xml.namespace.QName("", "VoucherEnquiryByPINResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "VoucherEnquiryByPINResult"));
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
