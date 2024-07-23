/**
 * VoucherEnquiryBySeqResultMsg.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.cardrechargemgr;

public class VoucherEnquiryBySeqResultMsg  implements java.io.Serializable {
    private com.inter.umniah.huawei.www.bme.cbsinterface.common.ResultHeader resultHeader;

    private com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherEnquiryBySeqResult voucherEnquiryBySeqResult;

    public VoucherEnquiryBySeqResultMsg() {
    }

    public VoucherEnquiryBySeqResultMsg(
           com.inter.umniah.huawei.www.bme.cbsinterface.common.ResultHeader resultHeader,
           com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherEnquiryBySeqResult voucherEnquiryBySeqResult) {
           this.resultHeader = resultHeader;
           this.voucherEnquiryBySeqResult = voucherEnquiryBySeqResult;
    }


    /**
     * Gets the resultHeader value for this VoucherEnquiryBySeqResultMsg.
     * 
     * @return resultHeader
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.common.ResultHeader getResultHeader() {
        return resultHeader;
    }


    /**
     * Sets the resultHeader value for this VoucherEnquiryBySeqResultMsg.
     * 
     * @param resultHeader
     */
    public void setResultHeader(com.inter.umniah.huawei.www.bme.cbsinterface.common.ResultHeader resultHeader) {
        this.resultHeader = resultHeader;
    }


    /**
     * Gets the voucherEnquiryBySeqResult value for this VoucherEnquiryBySeqResultMsg.
     * 
     * @return voucherEnquiryBySeqResult
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherEnquiryBySeqResult getVoucherEnquiryBySeqResult() {
        return voucherEnquiryBySeqResult;
    }


    /**
     * Sets the voucherEnquiryBySeqResult value for this VoucherEnquiryBySeqResultMsg.
     * 
     * @param voucherEnquiryBySeqResult
     */
    public void setVoucherEnquiryBySeqResult(com.inter.umniah.huawei.www.bme.cbsinterface.cardrecharge.VoucherEnquiryBySeqResult voucherEnquiryBySeqResult) {
        this.voucherEnquiryBySeqResult = voucherEnquiryBySeqResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VoucherEnquiryBySeqResultMsg)) return false;
        VoucherEnquiryBySeqResultMsg other = (VoucherEnquiryBySeqResultMsg) obj;
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
            ((this.voucherEnquiryBySeqResult==null && other.getVoucherEnquiryBySeqResult()==null) || 
             (this.voucherEnquiryBySeqResult!=null &&
              this.voucherEnquiryBySeqResult.equals(other.getVoucherEnquiryBySeqResult())));
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
        if (getVoucherEnquiryBySeqResult() != null) {
            _hashCode += getVoucherEnquiryBySeqResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VoucherEnquiryBySeqResultMsg.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrechargemgr", ">VoucherEnquiryBySeqResultMsg"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultHeader");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ResultHeader"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "ResultHeader"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("voucherEnquiryBySeqResult");
        elemField.setXmlName(new javax.xml.namespace.QName("", "VoucherEnquiryBySeqResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/cardrecharge", "VoucherEnquiryBySeqResult"));
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
